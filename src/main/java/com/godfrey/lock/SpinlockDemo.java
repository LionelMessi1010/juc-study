package com.godfrey.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * description : 自旋锁
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class SpinlockDemo {

    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    // 加锁
    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "==> mylock");

        // 自旋锁
        while (!atomicReference.compareAndSet(null, thread)) {
        }
    }

    // 解锁
    public void myUnLock() {
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "==> myUnlock");
        atomicReference.compareAndSet(thread, null);
    }
}
