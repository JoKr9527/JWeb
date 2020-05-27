package com.duofei.redis;

import java.util.concurrent.*;

/**
 * 为任务执行分配线程
 * @author duofei
 * @date 2020/5/26
 */
public class TaskManager {

    private static ExecutorService executorService = new ThreadPoolExecutor(4, 5, 2000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                if(executorService != null && !executorService.isShutdown()){
                    executorService.shutdown();
                }
            }
        });
    }

    public static void handle(Runnable task){
        executorService.submit(task);
    }
}
