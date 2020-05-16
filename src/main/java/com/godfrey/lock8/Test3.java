package com.godfrey.lock8;

import java.util.concurrent.TimeUnit;

/**
 * description : 8锁：关于锁的8个问题
 * 5.增加两个静态的同步方法，只要一个对象，先打样发短信还是打电话？ 发短信
 * 6.两个对象，增加两个静态的同步方法，只要一个对象，先打样发短信还是打电话？ 发短信
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class Test3 {
    public static void main(String[] args) {
        //两个对象，两个调用者，两把锁
        //static 静态方法
        //类一加载就有了！锁的是class
        //Phone3 phone = new Phone3();
        Phone3 phone1 = new Phone3();
        Phone3 phone2 = new Phone3();

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

class Phone3 {
    //synchronized 锁的对象是方法的调用者！
    public static synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    public static synchronized void call() {
        System.out.println("打电话");
    }
}