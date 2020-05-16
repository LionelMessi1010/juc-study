package com.godfrey.add;

import java.util.concurrent.CountDownLatch;

/**
 * description : 减法计数器
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        //总数是6
        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\tGo Out");
                countDownLatch.countDown();//-1
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();//等待计数器归零，然后再向下执行
        System.out.println("Close Door");
    }
}
