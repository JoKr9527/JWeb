package com.duofei.distributed.lock.redisson.lock;

import com.duofei.Application;
import com.duofei.distributed.lock.ThreadControl;
import com.duofei.distributed.lock.redis.DistributeLock;
import com.duofei.distributed.lock.redis.JedisUtils;
import com.duofei.distributed.lock.redis.redisson.RedissonClientFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RedissonBlockInterruptilyLockTest {

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
            // 提交一个可中断阻塞线程的任务
            executorService.execute(() -> {
                int i = 0;
                while(true){
                    Map<String, Thread> uniqueSourceIdToBlockThread = ThreadControl.getUniqueSourceIdToBlockThread();
                    Set<String> values = uniqueSourceIdToBlockThread.keySet();
                    Iterator<String> iterator = values.iterator();
                    while (iterator.hasNext()){
                        String id = iterator.next();
                        if(i % 2 == 0){
                            uniqueSourceIdToBlockThread.get(id).interrupt();
                            System.out.println("中断线程：" + uniqueSourceIdToBlockThread.get(id).getName() + "; id: " + id);
                        }
                        i++;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            });
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
            Lock userCountLock = RedissonClientFactory.getRedissonClient().getLock("userCount");
            try {
                userCountLock.lockInterruptibly();
                try (Jedis jedis = JedisUtils.getJedis()){
                    String key = "userCountValue";
                    String value = jedis.get(key);
                    int i = (value != null ? Integer.parseInt(value) : 0);
                    System.out.println(Thread.currentThread().getName() + " " + i);
                    i++;
                    jedis.set(key, String.valueOf(i));
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    userCountLock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
