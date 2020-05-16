package com.godfrey.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description : Executors 工具类、3大方法
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class Demo01 {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();// 单个线程
        //ExecutorService threadPool = Executors.newFixedThreadPool(5);// 创建一个固定的线程池的大小
        //ExecutorService threadPool = Executors.newCachedThreadPool();// 可伸缩的，遇强则强，遇弱则弱

        try {
            for (int i = 0; i < 100; i++) {
                threadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "\tOK");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完，程序结束，关闭线程池
            threadPool.shutdown();
        }
    }
}
