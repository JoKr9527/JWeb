package com.duofei.redis.task;

import com.duofei.ThreadControl;
import com.duofei.redis.JedisUtils;
import com.duofei.redis.QueueManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

/**
 * 可能发生的死锁检测
 * @author duofei
 * @date 2020/5/25
 */
public class DeadLockCheckTask implements Runnable{

    private Long intervalTime;

    public DeadLockCheckTask(Long intervalTime){
        this.intervalTime = intervalTime;
    }

    @Override
    public void run() {
        while(true) {
            Set<String> queueName = QueueManager.listQueueName();
            queueName.forEach(this::analysisWaitQueueAndTryUnpark);
            try {
                Thread.sleep(intervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void analysisWaitQueueAndTryUnpark(String queueName){
        List<String> sourceIds = JedisUtils.call(jedis -> {
            List<String> result = jedis.lrange(queueName, 0, -1);
            if(result.size() > 0){
                return result;
            }
            return null;
        });
        if(sourceIds != null){
            unparkAlwaysBlockedThread(sourceIds);
        }
    }

    private void unparkAlwaysBlockedThread(List<String> sourceIds){
        Map<String, Thread> uniqueSourceIdToBlockThread = ThreadControl.getUniqueSourceIdToBlockThread();
        // 唤醒等待队列的第一个线程，让它尝试去获取锁
        if(uniqueSourceIdToBlockThread.containsKey(sourceIds.get(0))){
            unpark(uniqueSourceIdToBlockThread.get(sourceIds.get(0)));
        }
        // 唤醒可能因为意外未被加入排队队列的线程
        uniqueSourceIdToBlockThread.forEach((id, thread) -> {
            if(!sourceIds.contains(id)){
                unpark(uniqueSourceIdToBlockThread.get(id));
            }
        });
    }

    private void unpark(Thread thread){
        Object blocker = LockSupport.getBlocker(thread);
        if(blocker != null && blocker instanceof String){
            // 可以在这里做打印日志操作
            String id = (String) blocker;
            ThreadControl.unpark(id);
        }
    }
}
