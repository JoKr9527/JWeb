package com.duofei.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * redis 分布式锁测试
 * @author duofei
 * @date 2020/5/25
 */
public class JedisLockTest {

    @Test
    public void process1(){
        executeTask();
    }

    @Test
    public void process2(){
        executeTask();
    }

    private void executeTask(){
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 8; i++) {
            executorService.execute(new DistributedBlockTask());
        }
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Jedis jedis = JedisUtils.getJedis();
        System.out.println(jedis.get("userCountValue"));
        jedis.close();
    }

    static String lockedKey = "userCount";

    static class DistributedBlockTask implements Runnable{
        @Override
        public void run() {
            JedisLock fileRead = new JedisLock(lockedKey);
            fileRead.lock();
            Jedis jedis = JedisUtils.getJedis();
            try {
                String key = "userCountValue";
                String value = jedis.get(key);
                int i = (value != null ? Integer.parseInt(value) : 0);
                System.out.println(Thread.currentThread().getName() + " " + i);
                i++;
                Thread.sleep(1000);
                jedis.set(key, String.valueOf(i));
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                jedis.close();
                fileRead.unlock();
            }
        }
    }
}
