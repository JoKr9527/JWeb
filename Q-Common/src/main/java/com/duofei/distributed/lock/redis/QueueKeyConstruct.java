package com.duofei.distributed.lock.redis;

import java.util.function.Function;

/**
 * 构造 redis 的 list key
 * @author duofei
 * @date 2020/5/27
 */
public interface QueueKeyConstruct {

    static String DEFAULT_QUEUE_SUFFIX = "_LIST";

    default String constructQueueKey(String lockedKey){
        if(getConstructQueueKey() != null){
            return getConstructQueueKey().apply(lockedKey);
        }
        return lockedKey + DEFAULT_QUEUE_SUFFIX;
    }

    default Function<String, String> getConstructQueueKey() {
        return null;
    }
}
