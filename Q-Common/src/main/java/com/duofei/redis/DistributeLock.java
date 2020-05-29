package com.duofei.redis;

import java.util.concurrent.locks.Lock;

/**
 * 分布式锁
 * 推荐这样使用：系统中可有的分布式锁都应在这里列出，这有助于辨别系统已使用的分布式锁
 * @author duofei
 * @date 2020/5/27
 */
public enum DistributeLock {

    /**
     * demo
     */
    User_Count{
        @Override
        public Lock newLock() {
            return new JedisLock("userCount");
        }
    };

    public abstract Lock newLock();
}
