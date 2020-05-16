package com.godfrey.lock8;

import java.util.concurrent.TimeUnit;

/**
 * description : 8锁：关于锁的8个问题
 * 7.一个静态同步方法一个普通方法，先打样发短信还是打电话？ 打电话
 * 8.一个静态同步方法一个普通方法，两个对象，先打样发短信还是打电话？ 打电话
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class Test4 {
    public static void main(String[] args) {
        //Phone4 phone = new Phone4();
        Phone4 phone1 = new Phone4();
        Phone4 phone2 = new Phone4();

        new Thread(() -> {
            //phone.sendSms();
            phone1.sendSms();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            phone2.call();
        }, "B").start();
    }
}

class Phone4 {
    //静态同步方法，锁的对象是Class模板！
    public static synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    //普通同步方法，锁的是调用者
    public synchronized void call() {
        System.out.println("打电话");
    }
}