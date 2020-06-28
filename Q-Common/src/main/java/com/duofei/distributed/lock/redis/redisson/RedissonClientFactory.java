package com.duofei.distributed.lock.redis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

/**
 * redissonClient factory
 * @author duofei
 * @date 2020/6/28
 */
public class RedissonClientFactory {

    public static RedissonClient getRedissonClient(){
        Config config = new Config();
        config.setTransportMode(TransportMode.EPOLL);
        config.useClusterServers().addNodeAddress("127.0.0.1:6379");
        return Redisson.create();
    }
}
