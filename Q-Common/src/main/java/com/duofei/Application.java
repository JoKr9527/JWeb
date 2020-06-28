package com.duofei;

import com.duofei.distributed.lock.redis.JedisUtils;
import com.duofei.distributed.lock.redis.redisson.RedissonClientFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

import java.util.concurrent.locks.Lock;

/**
 * @author duofei
 * @date 2020/5/25
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
