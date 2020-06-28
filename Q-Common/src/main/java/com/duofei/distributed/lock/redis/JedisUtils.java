package com.duofei.distributed.lock.redis;

import com.duofei.distributed.lock.ThreadControl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Jedis 工具类
 * @author duofei
 * @date 2020/5/20
 */
public class JedisUtils {

    private static String HOST = "localhost";
    private static int PORT = 6379;
    private static JedisPool jedisPool = new JedisPool(HOST, PORT);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                if(jedisPool != null){
                    jedisPool.destroy();
                }
            }
        });
    }

    /**
     * 注意：调用该方法以后，一定记得调用 Jedis 的 close 方法；该方法与 close 方法之前也不有阻塞的可能性存在
     */
    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static long rPush(String key, String value){
        return call(jedis -> jedis.rpush(key, value));
    }

    public static String lIndex(String key, long index){
        return call(jedis -> jedis.lindex(key, index));
    }

    public static String lPop(String key){
        return call(jedis -> jedis.lpop(key));
    }

    public static void publish(String channel, String message){
        run(jedis -> {
            if(message != null && message.length() != 0 ){
                jedis.publish(channel, message);
            }
        });
    }

    public static void subscribe(String channel){
        run(jedis -> {
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if(message != null && message.length() != 0){
                        ThreadControl.unpark(message);
                    }
                }
            }, channel);
        });
    }

    public static void run(Consumer<Jedis> action){
        try(Jedis jedis = getJedis()){
            action.accept(jedis);
        }
    }

    public static <T> T call(Function<Jedis, T> actionFunction){
        try(Jedis jedis = getJedis()){
            return actionFunction.apply(jedis);
        }
    }
}
