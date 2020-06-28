package com.duofei.distributed.lock.redis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理列表
 * @author duofei
 * @date 2020/5/29
 */
public class QueueManager {

    private static Map<String, Set<String>> queues = new ConcurrentHashMap<>();

    public static long offer(String queueName, String value){
        addEleToMap(queueName, value);
        return JedisUtils.call(jedis -> jedis.rpush(queueName, value));
    }

    public static String poll(String key){
        return JedisUtils.call(jedis -> {
            String value = jedis.lpop(key);
            if(value != null){
                removeEleFromMap(key, value);
            }
            return value;
        });
    }

    public static String peek(String key){
        return JedisUtils.lIndex(key, 0L);
    }

    public static Long remove(String queueName, String ele){
        return JedisUtils.call(jedis -> jedis.lrem(queueName, 1L, ele));
    }

    public static Set<String> listQueueName(){
        return queues.keySet();
    }

    private static void addEleToMap(String key, String value){
        queues.compute(key, (k, oldValue) -> {
            if(oldValue != null){
                oldValue.add(value);
                return oldValue;
            }
            Set<String> result = new HashSet<>();
            result.add(value);
            return result;
        });
    }

    private static  void removeEleFromMap(String key, String value){
        queues.computeIfPresent(key, (k, oldValue) -> {
            oldValue.remove(value);
            return oldValue;
        });
    }
}
