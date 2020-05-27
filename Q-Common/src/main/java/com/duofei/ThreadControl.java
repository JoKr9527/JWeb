package com.duofei;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * 负责管理阻塞的线程
 * @author duofei
 * @date 2020/5/21
 */
public class ThreadControl {

    private static Map<String, Thread> uniqueSourceIdToBlockThread = new ConcurrentHashMap<>();

    public static void park(String uniqueSourceId){
        uniqueSourceIdToBlockThread.putIfAbsent(uniqueSourceId, Thread.currentThread());
        LockSupport.park(uniqueSourceId);
    }

    public static boolean parkAndCheckInterrupt(String uniqueSourceId){
        uniqueSourceIdToBlockThread.putIfAbsent(uniqueSourceId, Thread.currentThread());
        LockSupport.park(uniqueSourceId);
        return Thread.interrupted();
    }

    public static void parkNanos(String uniqueSourceId, Long nanos){
        uniqueSourceIdToBlockThread.putIfAbsent(uniqueSourceId, Thread.currentThread());
        LockSupport.parkNanos(uniqueSourceId, nanos);
    }

    public static void unpark(String uniqueSourceId){
        Thread thread = uniqueSourceIdToBlockThread.get(uniqueSourceId);
        if(thread != null){
            LockSupport.unpark(thread);
            uniqueSourceIdToBlockThread.remove(uniqueSourceId);
        }
    }

    public static void abandon(String uniqueSourceId){
        uniqueSourceIdToBlockThread.remove(uniqueSourceId);
    }

    public static Map<String, Thread> getUniqueSourceIdToBlockThread(){
        return uniqueSourceIdToBlockThread;
    }
}
