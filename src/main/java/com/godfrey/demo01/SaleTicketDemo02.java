package com.godfrey.demo01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description : 模拟卖票
 *
 * @author godfrey
 * @since 2020-05-14
 */
public class SaleTicketDemo02 {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> { for (int i = 0; i < 40; i++) ticket.sale(); }, "A").start();
        new Thread(() -> { for (int i = 0; i < 40; i++) ticket.sale(); }, "B").start();
        new Thread(() -> { for (int i = 0; i < 40; i++) ticket.sale(); }, "C").start();
    }
}

//Lock
class Ticket2 {
    private int number = 30;

    Lock lock = new ReentrantLock();

    public synchronized void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出了第" + (50 - (number--)) + "票，剩余：" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}