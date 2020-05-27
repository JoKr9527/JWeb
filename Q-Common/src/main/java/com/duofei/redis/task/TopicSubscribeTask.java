package com.duofei.redis.task;

import com.duofei.redis.JedisUtils;

/**
 * redis 队列订阅
 * 注意：订阅是阻塞的，并不会释放线程，所以应保证订阅的 topics 小于 任务管理器的 corePoolSize
 * @author duofei
 * @date 2020/5/26
 */
public class TopicSubscribeTask implements Runnable {

    private String channel;

    public TopicSubscribeTask(String channel){
        this.channel = channel;
    }

    @Override
    public void run() {
        JedisUtils.subscribe(channel);
    }
}
