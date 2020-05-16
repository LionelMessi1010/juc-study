package com.godfrey.lock;

import java.util.concurrent.TimeUnit;

/**
 * description : 测试自定义CAS实现的自旋锁
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class TestSpinLock {
    public static void main(String[] args) throws InterruptedException {
        // 底层使用的自旋锁CAS
        SpinlockDemo lock = new SpinlockDemo();

        new Thread(() -> {
            lock.myLock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.myUnLock();
            }
        }, "T1").start();

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            lock.myLock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.myUnLock();
            }
        }, "T2").start();
    }
}
