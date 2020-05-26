package com.duofei.redis;

import com.duofei.ThreadControl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * @author duofei
 * @date 2020/5/20
 */
public class JedisLock implements Lock {

    private String uniqueResourceIdentifier = UUID.randomUUID().toString().replaceAll("-", "");
    private static long DEFAULT_TIME = 30000;
    private static String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
    private static String DEFAULT_LIST_SUFFIX = "_LIST";
    private static Set lockedKeys = new HashSet();

    private long expireTime;
    private String lockedKey;
    private Function<String, String> constructListKey;

    public JedisLock(String lockedKey) {
        this(DEFAULT_TIME, lockedKey);
    }

    public JedisLock(long expireTime, String lockedKey) {
        this.expireTime = expireTime;
        this.lockedKey = lockedKey;
        if(!lockedKeys.contains(lockedKey)){
            lockedKeys.add(lockedKey);
            TaskManager.handle(new ListSubscribeTask(lockedKey));
        }
    }

    public void setConstructListKey(Function<String, String> constructListKey) {
        this.constructListKey = constructListKey;
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

    private boolean tryAcquire() {
        return JedisUtils.call(jedis -> {
            String uniqueResourceId = jedis.get(lockedKey);
            if (uniqueResourceId == null) {
                String result = jedis.set(lockedKey, uniqueResourceIdentifier, SetParams.setParams().nx().px(expireTime));
                if (RedisConsts.OK.isEqual(result)) {
                    return true;
                }
            }
            if(uniqueResourceIdentifier.equals(uniqueResourceId)){
                Long result = jedis.pexpire(lockedKey, expireTime);
                if(RedisConsts.SUCCESS_REPLY.isEqual(result)){
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
        //TODO 是否需要抛出
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
        // 入等待队列
        String listKey = constructListKey(lockedKey);
        JedisUtils.rPush(listKey, uniqueResourceIdentifier);
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (; ; ) {
                String resourceId = JedisUtils.lIndex(listKey, 0L);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    JedisUtils.lPop(listKey);
                    failed = false;
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
        String listKey = constructListKey(lockedKey);
        JedisUtils.rPush(listKey, uniqueResourceIdentifier);
        boolean failed = true;
        try {
            for (; ; ) {
                String resourceId = JedisUtils.lIndex(listKey, 0L);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    JedisUtils.lPop(listKey);
                    failed = false;
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
        // 入等待队列
        String listKey = constructListKey(lockedKey);
        JedisUtils.rPush(listKey, uniqueResourceIdentifier);
        boolean failed = false;
        try {
            for (; ; ) {
                String resourceId = JedisUtils.lIndex(listKey, 0L);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    JedisUtils.lPop(listKey);
                    failed = false;
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
            JedisUtils.publish(lockedKey, JedisUtils.lIndex(constructListKey(lockedKey), 0));
            return true;
        }
        return false;
    }

    private void cancelAcquire() {
        // 通知让其他锁请求有机会执行
        JedisUtils.publish(lockedKey, JedisUtils.lIndex(constructListKey(lockedKey), 0));
    }

    void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    private boolean parkAndCheckInterrupt() {
        return ThreadControl.parkAndCheckInterrupt(uniqueResourceIdentifier);
    }

    private String constructListKey(String lockedKey){
        if(constructListKey != null){
            return constructListKey.apply(lockedKey);
        }
        return lockedKey + DEFAULT_LIST_SUFFIX;
    }

}
