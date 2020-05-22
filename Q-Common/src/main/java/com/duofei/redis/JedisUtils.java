package com.duofei.redis;

import com.duofei.ThreadControl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * Jedis 工具类
 * @author duofei
 * @date 2020/5/20
 */
public class JedisUtils {

    private static String HOST = "192.168.3.18";
    private static int PORT = 6379;
    private static JedisPool jedisPool = new JedisPool(HOST, PORT);

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static long rPush(String key, String value){
        return getJedis().rpush(key, value);
    }

    public static String lIndex(String key, long index){
        return getJedis().lindex(key, index);
    }

    public static String lPop(String key){
        return getJedis().lpop(key);
    }

    public static void publish(String channel, String message){
        getJedis().publish(channel, message);
    }

    public static void subscribe(String channel){
        getJedis().subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if(message != null && message.length() != 0){
                    ThreadControl.unpark(message);
                }
            }
        }, channel);
    }
}
