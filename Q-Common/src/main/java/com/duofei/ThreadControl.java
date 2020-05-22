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

    private static long TIMEOUT = 5000;

    private static Map<String, Thread> uniqueSourceIdToBlockThread = new ConcurrentHashMap();

    public static void park(String uniqueSourceId){
        Thread previousThread = uniqueSourceIdToBlockThread.putIfAbsent(uniqueSourceId, Thread.currentThread());
        if(previousThread != null){
            throw new IllegalStateException();
        }
        LockSupport.park();
    }

    public static boolean parkAndCheckInterrupt(String uniqueSourceId){
        Thread previousThread = uniqueSourceIdToBlockThread.putIfAbsent(uniqueSourceId, Thread.currentThread());
        if(previousThread != null){
            throw new IllegalStateException();
        }
        LockSupport.park();
        return Thread.interrupted();
    }

    public static void parkNanos(String uniqueSourceId, Long nanos){
        Thread previousThread = uniqueSourceIdToBlockThread.putIfAbsent(uniqueSourceId, Thread.currentThread());
        if(previousThread != null){
            throw new IllegalStateException();
        }
        LockSupport.park(nanos);
    }

    public static void unpark(String uniqueSourceId){
        Thread thread = uniqueSourceIdToBlockThread.get(uniqueSourceId);
        if(thread != null){
            LockSupport.unpark(thread);
            uniqueSourceIdToBlockThread.remove(uniqueSourceId);
        }
    }
}
