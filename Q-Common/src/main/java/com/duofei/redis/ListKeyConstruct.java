package com.duofei.redis;

import java.util.function.Function;

/**
 * 构造 redis 的 list key
 * @author duofei
 * @date 2020/5/27
 */
public interface ListKeyConstruct {

    static String DEFAULT_LIST_SUFFIX = "_LIST";

    default String constructListKey(String lockedKey){
        if(getConstructListKey() != null){
            return getConstructListKey().apply(lockedKey);
        }
        return lockedKey + DEFAULT_LIST_SUFFIX;
    }

    default Function<String, String> getConstructListKey() {
        return null;
    }
}
