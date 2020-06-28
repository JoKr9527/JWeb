package com.duofei.distributed.lock.redis;

import com.duofei.distributed.lock.redis.DistributeLock;
import com.duofei.distributed.lock.redis.JedisUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis 分布式锁测试
 *
 * @author duofei
 * @date 2020/5/25
 */
public class JedisUnBlockLockTest {

    @Test
    public void process1() {
        executeTask();
    }

    @Test
    public void process2() {
        executeTask();
    }

    private void executeTask() {
        System.out.println("开始...");
        ExecutorService executorService = new ThreadPoolExecutor(6, 8, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(1000);
                executorService.execute(new DistributedBlockTask());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            executorService.awaitTermination(50, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Jedis jedis = JedisUtils.getJedis();
        System.out.println(jedis.get("userCountValue"));
        jedis.close();
    }

    static class DistributedBlockTask implements Runnable {
        @Override
        public void run() {
            Lock userCountLock = DistributeLock.User_Count.newLock();
            if (userCountLock.tryLock()) {
                try (Jedis jedis = JedisUtils.getJedis()) {
                    String key = "userCountValue";
                    String value = jedis.get(key);
                    int i = (value != null ? Integer.parseInt(value) : 0);
                    System.out.println(Thread.currentThread().getName() + " " + i);
                    i++;
                    jedis.set(key, String.valueOf(i));
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    userCountLock.unlock();
                }
            }
        }
    }
}
