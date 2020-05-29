package com.duofei.redis;

import com.duofei.ThreadControl;
import com.duofei.redis.task.DeadLockCheckTask;
import com.duofei.redis.task.TopicSubscribeTask;
import redis.clients.jedis.params.SetParams;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author duofei
 * @date 2020/5/20
 */
public class JedisLock implements Lock, QueueKeyConstruct {

    private String uniqueResourceIdentifier = UUID.randomUUID().toString().replaceAll("-", "");
    private static long DEFAULT_TIME = 10000;
    private static String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
    private static String RENEWAL_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('pexpire',KEYS[1],ARGV[2]) else return 0 end";
    public static String DEFAULT_TOPIC = "DistributeLock";
    public static Map<String, Boolean> subscribedTopics = new ConcurrentHashMap<>();

    private long expireTime;
    private String lockedKey;
    private String topic = DEFAULT_TOPIC;
    private Function<String, String> constructListKey;

    static {
        subscribedTopics.put(DEFAULT_TOPIC, Boolean.TRUE);
        TaskManager.handle(new TopicSubscribeTask(DEFAULT_TOPIC));
        TaskManager.handle(new DeadLockCheckTask(DEFAULT_TIME * 2));
    }

    public JedisLock(String lockedKey) {
        this(DEFAULT_TIME, lockedKey, DEFAULT_TOPIC);
    }

    public JedisLock(long expireTime, String lockedKey, String topic) {
        this.expireTime = expireTime;
        this.lockedKey = lockedKey;
        this.topic = topic;
    }

    public void setConstructListKey(Function<String, String> constructListKey) {
        this.constructListKey = constructListKey;
    }

    @Override
    public Function<String, String> getConstructQueueKey() {
        return constructListKey;
    }

    @Override
    public void lock() {
        if (!tryAcquire() && acquireList()) {
            selfInterrupt();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (!tryAcquire()) {
            doAcquireInterruptibly();
        }
    }

    @Override
    public boolean tryLock() {
        return tryAcquire();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryAcquireNanos(time, unit);
    }

    @Override
    public void unlock() {
        release();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getLockedKey() {
        return lockedKey;
    }

    public String getTopic() {
        return topic;
    }

    private boolean needSubscribe() {
        Boolean oldValue = subscribedTopics.putIfAbsent(topic, Boolean.TRUE);
        return oldValue == null;
    }

    private boolean tryAcquire() {
        return JedisUtils.call(jedis -> {
            String uniqueResourceId = jedis.get(lockedKey);
            if (uniqueResourceId == null) {
                String result = jedis.set(lockedKey, uniqueResourceIdentifier, SetParams.setParams().nx().px(expireTime));
                if (RedisConsts.OK.isEqual(result)) {
                    return true;
                }
            }
            if (uniqueResourceIdentifier.equals(uniqueResourceId)) {
                // 以脚本保证续约的原子性，否则可能为其它持有锁的线程续约
                Object result = jedis.eval(RENEWAL_SCRIPT, Collections.singletonList(lockedKey),
                        Stream.of(uniqueResourceIdentifier, String.valueOf(expireTime)).collect(Collectors.toList()));
                if (RedisConsts.SUCCESS_REPLY.isEqual(result)) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean tryAcquireNanos(long time, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return tryAcquire() || acquireListNanos(time, unit);
    }

    private boolean tryRelease() {
        return JedisUtils.call(jedis -> {
            String uniqueResourceId = jedis.get(lockedKey);
            if (!uniqueResourceIdentifier.equals(uniqueResourceId)) {
                throw new IllegalMonitorStateException();
            }
            Object result = jedis.eval(UNLOCK_SCRIPT, Collections.singletonList(lockedKey), Collections.singletonList(uniqueResourceIdentifier));
            if (RedisConsts.SUCCESS_REPLY.isEqual(result)) {
                return true;
            }
            return false;
        });
    }

    private boolean acquireList() {
        if(!topic.equals(DEFAULT_TOPIC) && needSubscribe()){
            TaskManager.handle(new TopicSubscribeTask(topic));
        }
        // 入等待队列
        String queueName = constructQueueKey(lockedKey);
        QueueManager.offer(queueName, uniqueResourceIdentifier);
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (; ; ) {
                String resourceId = QueueManager.peek(queueName);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    failed = false;
                    ThreadControl.abandon(uniqueResourceIdentifier);
                    QueueManager.poll(queueName);
                    return interrupted;
                }
                if (parkAndCheckInterrupt()) {
                    interrupted = true;
                }
            }
        } finally {
            if (failed) {
                cancelAcquire();
            }
        }
    }

    private boolean acquireListNanos(long time, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        long nanosTimeout = unit.toNanos(time);
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = nanosTimeout + System.nanoTime();
        if(!topic.equals(DEFAULT_TOPIC) && needSubscribe()){
            TaskManager.handle(new TopicSubscribeTask(topic));
        }
        String queueName = constructQueueKey(lockedKey);
        QueueManager.offer(queueName, uniqueResourceIdentifier);
        boolean failed = true;
        try {
            for (; ; ) {
                String resourceId = QueueManager.peek(queueName);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    failed = false;
                    ThreadControl.abandon(uniqueResourceIdentifier);
                    QueueManager.poll(queueName);
                    return true;
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L) {
                    return false;
                }
                final long spinForTimeoutThreshold = 1000L;
                if (nanosTimeout > spinForTimeoutThreshold) {
                    ThreadControl.parkNanos(uniqueResourceIdentifier, nanosTimeout);
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }
        } finally {
            if (failed) {
                cancelAcquire();
            }
        }
    }

    private void doAcquireInterruptibly() throws InterruptedException {
        if(!topic.equals(DEFAULT_TOPIC) && needSubscribe()){
            TaskManager.handle(new TopicSubscribeTask(topic));
        }
        String queueName = constructQueueKey(lockedKey);
        QueueManager.offer(queueName, uniqueResourceIdentifier);
        boolean failed = true;
        try {
            for (; ; ) {
                String resourceId = QueueManager.peek(queueName);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    failed = false;
                    ThreadControl.abandon(uniqueResourceIdentifier);
                    QueueManager.poll(queueName);
                    return;
                }
                if (parkAndCheckInterrupt()) {
                    throw new InterruptedException();
                }
            }
        } finally {
            if (failed) {
                cancelAcquire();
            }
        }
    }

    private boolean release() {
        if (tryRelease()) {
            JedisUtils.publish(topic, QueueManager.peek(constructQueueKey(lockedKey)));
            return true;
        }
        return false;
    }

    private void cancelAcquire() {
        ThreadControl.abandon(uniqueResourceIdentifier);
        String queueName = constructQueueKey(lockedKey);
        QueueManager.remove(queueName, uniqueResourceIdentifier);
        // 通知让其他锁请求有机会执行
        JedisUtils.publish(topic, QueueManager.poll(queueName));
    }

    void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    private boolean parkAndCheckInterrupt() {
        return ThreadControl.parkAndCheckInterrupt(uniqueResourceIdentifier);
    }

}
