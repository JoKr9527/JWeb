package com.duofei.redis;

import com.duofei.ThreadControl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author duofei
 * @date 2020/5/20
 */
public class JedisLock implements Lock {

    private static String uniqueResourceIdentifier = "randomString";
    private static int DEFAULT_TIME = 3000;
    private static String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    private int expireTime;
    private String lockedKey;

    public JedisLock(String lockedKey) {
        expireTime = DEFAULT_TIME;
        this.lockedKey = lockedKey;
    }

    public JedisLock(int expireTime, String lockedKey) {
        this.expireTime = expireTime;
        this.lockedKey = lockedKey;
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
        String uniqueResourceId = JedisUtils.getJedis().get(lockedKey);
        if (uniqueResourceId == null) {
            Jedis jedis = JedisUtils.getJedis();
            String result = jedis.set(lockedKey, uniqueResourceIdentifier, SetParams.setParams().nx().px(expireTime));
            if (RedisConsts.OK.isEqual(result)) {
                return true;
            }
        }
        return uniqueResourceIdentifier.equals(uniqueResourceId);
    }

    private boolean tryAcquireNanos(long time, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return tryAcquire() || acquireListNanos(time, unit);
    }

    private boolean tryRelease() {
        //TODO 是否需要抛出
        String uniqueResourceId = JedisUtils.getJedis().get(lockedKey);
        if (!uniqueResourceIdentifier.equals(uniqueResourceId)) {
            throw new IllegalMonitorStateException();
        }
        Jedis jedis = JedisUtils.getJedis();
        Object result = jedis.eval(UNLOCK_SCRIPT, Collections.singletonList(lockedKey), Collections.singletonList(uniqueResourceIdentifier));
        if (RedisConsts.ONEL.isEqual(result)) {
            return true;
        }
        return false;
    }

    private boolean acquireList() {
        // 入等待队列
        JedisUtils.rPush(lockedKey, uniqueResourceIdentifier);
        boolean failed = false;
        try {
            for (; ; ) {
                String resourceId = JedisUtils.lIndex(lockedKey, 0L);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    JedisUtils.lPop(lockedKey);
                    failed = false;
                    return true;
                }
                if (parkAndCheckInterrupt()) {
                    failed = true;
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
        JedisUtils.rPush(lockedKey, uniqueResourceIdentifier);
        boolean failed = true;
        try {
            for (; ; ) {
                String resourceId = JedisUtils.lIndex(lockedKey, 0L);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    JedisUtils.lPop(lockedKey);
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
        JedisUtils.rPush(lockedKey, uniqueResourceIdentifier);
        boolean failed = false;
        try {
            for (; ; ) {
                String resourceId = JedisUtils.lIndex(lockedKey, 0L);
                if (uniqueResourceIdentifier.equals(resourceId) && tryAcquire()) {
                    JedisUtils.lPop(lockedKey);
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
            //TODO 通知 如果这里失败掉这么办
            JedisUtils.publish(lockedKey, JedisUtils.lIndex(lockedKey, 0));
            return true;
        }
        return false;
    }

    private void cancelAcquire() {
        // 通知让其他锁请求有机会执行
        JedisUtils.publish(lockedKey, JedisUtils.lIndex(lockedKey, 0));
    }

    void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    private boolean parkAndCheckInterrupt() {
        return ThreadControl.parkAndCheckInterrupt(uniqueResourceIdentifier);
    }
}
