package com.duofei.redis;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author duofei
 * @date 2020/5/26
 */
public class TaskManager {

    private static ExecutorService executorService = new ThreadPoolExecutor(4, 10, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(500));

    public static void handle(Runnable task){
        executorService.submit(task);
    }
}
