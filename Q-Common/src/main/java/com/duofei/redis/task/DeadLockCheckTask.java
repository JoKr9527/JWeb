package com.duofei.redis.task;

import com.duofei.ThreadControl;
import com.duofei.redis.DistributeLock;
import com.duofei.redis.JedisLock;
import com.duofei.redis.JedisUtils;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * 可能发生的死锁检测
 * @author duofei
 * @date 2020/5/25
 */
public class DeadLockCheckTask implements Runnable{

    private Long intervalTime;

    public DeadLockCheckTask(Long intervalTime){
        this.intervalTime = intervalTime;
    }

    @Override
    public void run() {
        while(true) {
            Jedis jedis = null;
            try {
                DistributeLock[] locks = DistributeLock.values();
                for (int i = 0; i < locks.length; i++) {
                    Lock lock = locks[i].newLock();
                    if(lock instanceof JedisLock){
                        JedisLock jedisLock = (JedisLock) lock;
                        String listKey = jedisLock.constructListKey(jedisLock.getLockedKey());
                        jedis = JedisUtils.getJedis();
                        List<String> sourceIds = jedis.lrange(listKey, 0, -1);
                        if(jedis.get(jedisLock.getLockedKey()) == null && sourceIds.size() > 0){
                            // 唤醒等待队列的第一个线程，让它尝试去获取锁
                            Map<String, Thread> uniqueSourceIdToBlockThread = ThreadControl.getUniqueSourceIdToBlockThread();
                            if(uniqueSourceIdToBlockThread.containsKey(sourceIds.get(0))){
                                Object blocker = LockSupport.getBlocker(uniqueSourceIdToBlockThread.get(sourceIds.get(0)));
                                if(blocker instanceof String){
                                    // 可以在这里做打印日志操作
                                    String id = (String) blocker;
                                    ThreadControl.unpark(id);
                                }
                            }
                        }
                    }
                }
            }finally {
                if(jedis != null){
                    jedis.close();
                }
            }
            try {
                Thread.sleep(intervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
