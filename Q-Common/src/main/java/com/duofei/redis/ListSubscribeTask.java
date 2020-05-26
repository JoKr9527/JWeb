package com.duofei.redis;

/**
 * redis 队列订阅
 * @author duofei
 * @date 2020/5/26
 */
public class ListSubscribeTask implements Runnable {

    private String channel;

    public ListSubscribeTask(String channel){
        this.channel = channel;
    }

    @Override
    public void run() {
        JedisUtils.subscribe(channel);
    }
}
