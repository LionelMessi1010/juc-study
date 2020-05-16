# 多线程进阶---JUC并发编程

==**完整代码传送门，见文章末尾**==

## 1.Lock锁（重点）

> 传统 Synchronizd

```java
package com.godfrey.demo01;

/**
 * description : 模拟卖票
 *
 * @author godfrey
 * @since 2020-05-14
 */
public class SaleTicketDemo01 {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i = 0; i < 60; i++) {
                ticket.sale();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 60; i++) {
                ticket.sale();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 60; i++) {
                ticket.sale();
            }
        }, "C").start();
    }
}

//资源类OOP
class Ticket {
    private int number = 50;

    public synchronized void sale() {
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() + "卖出了第" + (50 - (number--)) + "票，剩余：" + number);
        }
    }
}
```





> Synchronized(本质：队列+锁)和Lock区别

1. Synchronized 是内置关键字，Lock 是一个Java类
2. Synchronized 无法判断锁的状态，Lock 可以判断是否获取到了锁
3. Synchronized 会自动释放锁，Lock 必须手动释放！如果不释放锁，**死锁**

4. Synchronized 线程1（获得锁，阻塞）、线程2（等待，傻傻的等）；Lock 锁就不一定会等待下去（tryLock）
5. Synchronized 可重入锁，不可中断，非公平；Lock 可重入锁 ，可以判断锁，非公平（可以自己设置）；
6. Synchronized 适合锁少量的代码同步问题，Lock 适合锁大量的同步代码！



> 锁是什么，如何判断锁的是谁

## 2.线程之间通信问题：生成者消费者问题



> Synchronized版生产者消费者问题

```java
package proc;

/**
 * description : 生产者消费者问题
 *
 * @author godfrey
 * @since 2020-05-14
 */
public class A {
    public static void main(String[] args) {
        Data data = new Data();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();
    }
}

// 判断等待，业务，通知
class Data {
    private int number = 0;

    //+1
    public synchronized void increment() throws InterruptedException {
        if (number != 0) {
            //等待
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + "==>" + number);
        //通知其他线程，我+1完毕了
        this.notifyAll();
    }

    //-1
    public synchronized void decrement() throws InterruptedException {
        if (number == 0) {
            //等待
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + "==>" + number);
        //通知其他线程，我-1完毕了
        this.notifyAll();
    }
}
```



> Lock接口

![](http://imgcloud.duiyi.xyz//data20200515214119.png)

![](http://imgcloud.duiyi.xyz//data20200515214156.png)

![](http://imgcloud.duiyi.xyz//data20200515214340.png)

公平锁：十分公平：可以先来后到
**非公平锁：十分不公平：可以插队 （默认）**

```java
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
```





> 问题存在，ABCD四个线程！怎么解决？

![](http://imgcloud.duiyi.xyz//data20200514172432.png)

if ==>while

```java
package com.godfrey.proc;

/**
 * description : Synchronized版生成者消费者问题
 *
 * @author godfrey
 * @since 2020-05-14
 */
public class A {
    public static void main(String[] args) {
        Data data = new Data();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();
    }
}

// 判断等待，业务，通知
class Data {
    private int number = 0;

    //+1
    public synchronized void increment() throws InterruptedException {
        while (number != 0) {
            //等待
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + "==>" + number);
        //通知其他线程，我+1完毕了
        this.notifyAll();
    }

    //-1
    public synchronized void decrement() throws InterruptedException {
        while (number == 0) {
            //等待
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + "==>" + number);
        //通知其他线程，我-1完毕了
        this.notifyAll();
    }
}
```



> JUC版的生产者和消费者问题

通过Lock

<div align=left><img src="http://imgcloud.duiyi.xyz//data20200514173508.png"/></div>



```java
package com.godfrey.proc;

/**
 * description : Lock版生产者消费者问题
 *
 * @author godfrey
 * @since 2020-05-14
 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class B {
    public static void main(String[] args) {
        Data2 data = new Data2();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();
    }
}

// 判断等待，业务，通知
class Data2 {
    private int number = 0;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    //+1
    public void increment() throws InterruptedException {
        lock.lock();
        try {
            while (number != 0) {
                //等待
                condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName() + "==>" + number);
            //通知其他线程，我+1完毕了
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    //-1
    public void decrement() throws InterruptedException {
        lock.lock();
        try {
            while (number == 0) {
                //等待
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + "==>" + number);
            //通知其他线程，我-1完毕了
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```



> Condition的优势：精准通知和唤醒线程

![](http://imgcloud.duiyi.xyz//data20200515073048.png).

```java
package com.godfrey.proc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description : 按顺序执行 A->B->C
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class C {
    public static void main(String[] args) {
        Data3 data = new Data3();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.printA();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.printB();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.printC();
            }
        }, "C").start();
    }
}

//资源类
class Data3 {
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();
    private int number = 1; //1A 2B 3C

    public void printA() {
        lock.lock();
        try {
            //业务，判断->执行->通知
            while (number != 1) {
                //等待
                condition1.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>AAAAA");
            //通知指定的人，B
            number = 2;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            //业务，判断->执行->通知
            while (number != 2) {
                //等待
                condition2.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>BBBBB");
            //通知指定的人，C
            number = 3;
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            //业务，判断->执行->通知
            while (number != 3) {
                //等待
                condition3.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>CCCCC");
            //通知指定的人，C
            number = 1;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```



## 3.八锁现象

```java
package com.godfrey.lock8;

import java.util.concurrent.TimeUnit;

/**
 * description : 8锁：关于锁的8个问题
 * 1.标准情况下 ，两个线程先打印发短信还是打电话? 1/发短信 2/打电话
 * 2.sendSms延时4秒 ，两个线程先打印发短信还是打电话? 1/发短信 2/打电话
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class Test1 {
    public static void main(String[] args) {
        Phone phone = new Phone();

        new Thread(() -> {
            phone.sendSms();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            phone.call();
        }, "B").start();
    }
}

class Phone {
    //synchronized 锁的对象是方法的调用者！
    public synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    public synchronized void call() {
        System.out.println("打电话");
    }
}
```



```java
package com.godfrey.lock8;

import java.util.concurrent.TimeUnit;

/**
 * description : 8锁：关于锁的8个问题
 * 3.增加了一个普通方法后!先执行发短信还是Hello? 普通方法
 * 4.创建两个对象，!先执行发短信还是打电话? 打电话
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class Test2 {
    public static void main(String[] args) {
        //两个对象，两个调用者，两把锁
        Phone2 phone1 = new Phone2();
        Phone2 phone2 = new Phone2();

        new Thread(() -> {
            phone1.sendSms();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            //phone1.hello();
            phone2.call();
        }, "B").start();
    }
}

class Phone2 {
    //synchronized 锁的对象是方法的调用者！
    public synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    public synchronized void call() {
        System.out.println("打电话");
    }

    //这里没有锁！不是同步方法，不受锁的影响
    public void hello() {
        System.out.println("Hello");
    }
}
```



```java
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
```



```java
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
```

> 小结：看锁的是Class还是对象，看是否同一个调用者



## 4.集合类不安全



> List不安全

```java
package com.godfrey.unsafe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * description : java.util.ConcurrentModificationException 并发修改异常
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class ListTest {
    public static void main(String[] args) {
        //并发下 ArrayList不安全的
        /**
         * 解决方案：
         * 1.List<String> list = new Vector<>();
         * 2.List<String> list = Collections.synchronizedList(new ArrayList<>());
         * 3.List<String> list = new CopyOnWriteArrayList<>();
         */

        //CopyOnWrite 写入时复制COW 计算机程序设计 领域的一种优化策略
        //多个线程调用的时候，list, 读取的时候，固定的，写入(覆盖)
        //在写入的时候避免覆盖，造成数据问题！
        //读写分离
        //CopyOnWriteArrayList 比 Vector 牛逼在哪里？CopyOnWriteArrayList用Lock，Vector用Synchronized

        List<String> list = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(list);
            }, String.valueOf(i)).start();
        }
    }
}

```



> Set不安全

```java
package com.godfrey.unsafe;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * description : java.util.ConcurrentModificationException 并发修改异常
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class SetTest {
    public static void main(String[] args) {
        //HashSet<String> set = new HashSet<>();
        //并发下 HashSet不安全的
        /**
         * 解决方案：
         * 1. Set<String> set = Collections.synchronizedSet(new HashSet<>());
         * 2. Set<String> set = new CopyOnWriteArraySet<>();
         */

        Set<String> set = new CopyOnWriteArraySet<>();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(set);
            }, String.valueOf(i)).start();
        }
    }
}

```



问：HashSet的底层是什么？

答：HashMap

```java
public HashSet() {
    map = new HashMap<>();
}

//add set的本质是map key
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}

private static final Object PRESENT = new Object();
```



> Map不安全

```java
package com.godfrey.unsafe;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description : java.util.ConcurrentModificationException 并发修改异常
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class MapTest {
    public static void main(String[] args) {
        // Map<String, String> map= new HashMap<>();
        // 等价于 Map<String, String> map = new HashMap<>(16,0.75f);//加载因子，初始化容量


        //并发下 HashMap不安全的
        /**
         * 解决方案：
         * 1.Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
         * 2.Map<String, String> map = new ConcurrentHashMap<>();
         */
        Map<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }
}
```



## 5.Callable

![](http://imgcloud.duiyi.xyz//data20200515152410.png)

1. 有返回值
2. 可以抛出异常
3. 方法不同，run()/call()

> 代码测试

![](http://imgcloud.duiyi.xyz//data20200515153629.png).



![](http://imgcloud.duiyi.xyz//data20200515153755.png)



![](http://imgcloud.duiyi.xyz//data20200515154226.png)



```java
package com.godfrey.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * description : Callable测试
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //new Thread(new Runnable).start();
        //new Thread(new FutureTask<V>()).start();
        //new Thread(new FutureTask<V>(Callable)).start();

        MyThread thread = new MyThread();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(thread);//适配类

        new Thread(futureTask, "A").start();
        new Thread(futureTask, "B").start();//结果会被缓存，提高效率，最后打印只有一份
        
        Integer integer = futureTask.get();//获取Callable的返回结果（get方法可能会产生阻塞【大数据等待返回结果慢】！把它放到最会，或者用异步通信）
        System.out.println(integer);
    }
}

class MyThread implements Callable<Integer> {
    @Override
    public Integer call() {
        System.out.println("call()");
        return 1024;
    }
}
```

细节：

1. 有缓存
2. get()，结果可能会等待，会阻塞



## 6.常用辅助类（必会）

### 6.1 CountDownLatch

![](http://imgcloud.duiyi.xyz//data20200515160841.png)



```java
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
```

**原理：**

`countDownLatch.countDown()` //数量-1

`countDownLatch.await()` //等待计数器归零，然后再向下执行

每次有线程调用countDown()数量-1 , 假设计数器变为0 , countDownLatch.await()就会被唤醒,继续执行!



### 6.2 CyclicBarrier

![](http://imgcloud.duiyi.xyz//data20200515162109.png)

加法计数器

```java
package com.godfrey.add;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * description : 加法计数器
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        /**
         * 集齐七颗龙珠召唤神龙
         * 集齐龙珠的线程
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            System.out.println("召唤神龙成功");
        });

        for (int i = 0; i < 7; i++) {
            final int temp = i;//lambda操作不到i
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "收集" + temp + "个龙珠");

                try {
                    cyclicBarrier.await();//等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
```









### 6.3 Semaphore

Semaphore:信号量

![](http://imgcloud.duiyi.xyz//data20200515164354.png)



抢车位！

```java
package com.godfrey.add;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * description : 信号量
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        //线程数量：停车位! 限流！
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                //acquire() 得到
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "抢到车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //release() 释放
                    semaphore.release();
                }

            }).start();
        }
    }
}
```

**原理：**

`semaphore.acquire()` 获得，假设如果已经满了, 等待,等待被释放为止!

`semaphore.release()` 释放，会将当前的信号量释放+ 1 ,然后喚醒等待的线程!

作用:

1. 多个共享资源互斥的使用!
2. 并发限流,控制最大的线程数!



## 7.读写锁

ReadWriteLock

![](http://imgcloud.duiyi.xyz//data20200515170353.png)



```java
package com.godfrey.rw;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * description : 读写锁
 * 独占锁（写锁） 一次只能被一个线程占有
 * 共享锁（读锁） 多个线程可以同时占有
 * ReadWriteLock
 * 读-读 可以共存！
 * 读-写 不能共存！
 * 写-写 不能共存！
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCacheLock myCache = new MyCacheLock();

        //写入
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.put(temp + "", temp + "");
            }, String.valueOf(i)).start();
        }

        //读取
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.get(temp + "");
            }, String.valueOf(i)).start();
        }
    }
}

//加锁的
class MyCacheLock {
    private volatile Map<String, Object> map = new HashMap<>();
    //读写锁，更加细粒度的控制
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // 存，写入的时候，只希望同时只有一个线程写
    public void put(String key, Object value) {
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入OK");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    // 取，读，所有人都可以
    public void get(String key) {
        readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "读入" + key);
            Object o = map.get(key);
            System.out.println(Thread.currentThread().getName() + "读入OK");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}

/**
 * 自定义缓存
 */
class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();

    //存，写
    public void put(String key, Object value) {
        System.out.println(Thread.currentThread().getName() + "写入" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入OK");
    }

    //取，读
    public void get(String key) {
        System.out.println(Thread.currentThread().getName() + "读入" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "读入OK");
    }
}
```



## 8.阻塞队列

![](http://imgcloud.duiyi.xyz//data20200515185342.png)

阻塞队列：

![](http://imgcloud.duiyi.xyz//data20200515191942.png)



![](http://imgcloud.duiyi.xyz//data20200515192054.png)



![](http://imgcloud.duiyi.xyz//data20200515202018.png)



什么情况下我们会使用 阻塞队列：多线程并发处理，线程池！
**学会使用队列**
添加、移除
**四组API**

| 方式           | 抛出异常 | 有返回值，不抛出异常 | 阻塞 等待 | 超时  |
| -------------- | -------- | -------------------- | --------- | ----- |
| 添加           | add      | offer                | put       | offer |
| 移除           | remove   | poll                 | take      | poll  |
| 判断队列的首部 | element  | peek                 | -         | -     |

```java
/**
 * 抛出异常
 */
public static void test1() {
    ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
    System.out.println(blockingQueue.add("a"));
    System.out.println(blockingQueue.add("b"));
    System.out.println(blockingQueue.add("c"));

    //ava.lang.IllegalStateException: Queue full  抛出异常！队列已满
    //System.out.println(blockingQueue.add("d"));

    System.out.println(blockingQueue.element());//查看队首元素是谁
    System.out.println("===================");

    System.out.println(blockingQueue.remove());
    System.out.println(blockingQueue.remove());
    System.out.println(blockingQueue.remove());
    //java.lang.IllegalStateException: Queue full 抛出异常！队列为空
    //System.out.println(blockingQueue.remove());
}
```

```java
/**
 * 有返回值，没有异常
 */
public static void test2() {
    ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
    System.out.println(blockingQueue.offer("a"));
    System.out.println(blockingQueue.offer("b"));
    System.out.println(blockingQueue.offer("c"));

    //System.out.println(blockingQueue.offer("d"));// false 不抛出异常！

    System.out.println(blockingQueue.peek());//查看队首元素是谁
    System.out.println("===================");

    System.out.println(blockingQueue.poll());
    System.out.println(blockingQueue.poll());
    System.out.println(blockingQueue.poll());
    //System.out.println(blockingQueue.remove());// null 不抛出异常！
}
```

```java
/**
 * 等待，阻塞（一直阻塞）
 */
public static void test3() throws InterruptedException {
    // 队列的大小
    ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);

    // 一直阻塞
    blockingQueue.put("a");
    blockingQueue.put("b");
    blockingQueue.put("c");
    // blockingQueue.put("d"); // 队列没有位置了，一直阻塞
    System.out.println(blockingQueue.take());
    System.out.println(blockingQueue.take());
    System.out.println(blockingQueue.take());
    System.out.println(blockingQueue.take()); // 没有这个元素，一直阻塞
}
```

```java
/**
 * 等待，阻塞（等待超市）
 */
public static void test4() throws InterruptedException {
    ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
    blockingQueue.offer("a");
    blockingQueue.offer("b");
    blockingQueue.offer("c");

    // blockingQueue.offer("d",2,TimeUnit.SECONDS); // 等待超过2秒就退出
    System.out.println("===============");
    System.out.println(blockingQueue.poll());
    System.out.println(blockingQueue.poll());
    System.out.println(blockingQueue.poll());
    blockingQueue.poll(2, TimeUnit.SECONDS); // 等待超过2秒就退出
}
```



> SynchronousQueue 同步队列

没有容量，
进去一个元素，必须等待取出来之后，才能再往里面放一个元素！
put、take

```java
package com.godfrey.bq;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
/**
 * description :
 *
 * @author godfrey
 * @since 2020-05-15
 */

/**
 * 同步队列
 * 和其他的BlockingQueue 不一样， SynchronousQueue 不存储元素
 * put了一个元素，必须从里面先take取出来，否则不能在put进去值！
 */
public class SynchronousQueueDemo {
    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>(); // 同步队列

        new Thread(() -> {
            try {
                blockingQueue.put("1");
                System.out.println(Thread.currentThread().getName() + " put 1");
                blockingQueue.put("2");
                System.out.println(Thread.currentThread().getName() + " put 2");
                blockingQueue.put("3");
                System.out.println(Thread.currentThread().getName() + " put 3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "T1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=>" + blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=>" + blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=>" + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "T2").start();
    }
}
```



## 9.线程池（重点）

线程池：三大方法、7大参数、4种拒绝策略

> 池话技术

程序的运行，本质：占用系统的资源！ 优化资源的使用！=>池化技术
线程池、连接池、内存池、对象池///..... 创建、销毁。十分浪费资源
池化技术：事先准备好一些资源，有人要用，就来我这里拿，用完之后还给我



**线程池的好处:**

1. 降低资源的消耗
2. 提高响应的速度
3. 方便管理

==**线程复用、可以控制最大并发数、管理线程**==



> 三大方法

![](http://imgcloud.duiyi.xyz//data20200515215358.png)

```java
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
```



> 七大参数

源码分析：

```java
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1,
                                0L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<Runnable>()));
}

public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}

public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}



//本质ThreadPoolExecutor()

public ThreadPoolExecutor(int corePoolSize, // 核心线程池大小
                          int maximumPoolSize, // 最大核心线程池大小
                          long keepAliveTime, // 超时了没有人调用就会释放
                          TimeUnit unit, // 超时单位
                          BlockingQueue<Runnable> workQueue, // 阻塞队列
                          ThreadFactory threadFactory, // 线程工厂：创建线程的，一般不用动
                          RejectedExecutionHandler handler // 拒绝策略) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```

![](http://imgcloud.duiyi.xyz//data20200515224657.png)

![](http://imgcloud.duiyi.xyz//data20200515224719.png)



> 手写一个线程池

```java
package com.godfrey.pool;

import java.util.concurrent.*;

/**
 * description : 七大参数与四种拒绝策略
 * 四种拒绝策略：
 * AbortPolicy(默认)：队列满了，还有任务进来，不处理这个任务的，直接抛出 RejectedExecution异常！
 * CallerRunsPolicy：哪来的回哪里！
 * DiscardOldestPolicy：队列满了，抛弃队列中等待最久的任务,然后把当前任务加入队列中尝试再次提交
 * DiscardPolicy()：队列满了，直接丢弃任务,不予任何处理也不抛出异常.如果允许任务丢失,这是最好的拒绝策略！
 *
 * @author godfrey
 * @since 2020-05-15
 */
public class Demo02 {
    public static void main(String[] args) {
        ExecutorService threadPool = new ThreadPoolExecutor(
                //模拟银行业务办理
                2,   //常驻核心线程数     办理业务窗口初始数量
                5, //线程池能够容纳同时执行的最大线程数,此值大于等于1，  办理业务窗口最大数量
                3, //多余的空闲线程存活时间,当空间时间达到keepAliveTime值时,多余的线程会被销毁直到只剩下corePoolSize个线程为止    释放后窗口数量会变为常驻核心数
                TimeUnit.SECONDS, //超时单位
                new LinkedBlockingDeque<>(3), //任务队列,被提交但尚未被执行的任务.  候客区座位数量
                Executors.defaultThreadFactory(), //线程工厂：创建线程的，一般不用动
                new ThreadPoolExecutor.DiscardOldestPolicy()); //拒绝策略,表示当线程队列满了并且工作线程大于等于线程池的最大显示 数(maxnumPoolSize)时如何来拒绝

        try {
            for (int i = 0; i < 10; i++) {
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
```



> 四种拒绝策略

1. AbortPolicy(默认)：队列满了，还有任务进来，不处理这个任务的，直接抛出 RejectedExecution异常
2. CallerRunsPolicy：哪来的回哪里！
3. DiscardOldestPolicy：队列满了，抛弃队列中等待最久的任务,然后把当前任务加入队列中尝试再次提交
4. DiscardPolicy()：队列满了，直接丢弃任务,不予任何处理也不抛出异常.如果允许任务丢失,这是最好的拒绝策略！



> 小结和拓展

池的最大的大小如何去设置！

获取CPU核数`System.out.println(Runtime.getRuntime().availableProcessors())`

了解：用来（调优）

- CPU密集型：CPU核数+1
- IO密集型：
  -  CPU核数*2
  - CPU核数/1.0-阻塞系数      阻塞系数在0.8~0.9之间



## 10.四大函数式接口（必需掌握）

新时代的程序员：lambda表达式、链式编程、函数式接口、Stream流式计算

> 函数式接口：只有一个方法的接口

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

![](http://imgcloud.duiyi.xyz//data20200515235259.png).



代码测试

> 函数式接口

![](http://imgcloud.duiyi.xyz//data20200516000920.png)。

```java
package com.godfrey.function;

import java.util.function.Function;

/**
 * description : Function 函数式接口,有一个输入参数，有一个输出
 * 只要是 函数型接口 可以 用 lambda表达式简化
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo01 {
    public static void main(String[] args) {
        //Function function = new Function<String, String>(){
        //    @Override
        //    public String apply(String o) {
        //        return o;
        //    }
        //};
        
        Function function = str->{return str;};

        System.out.println(function.apply("123"));
    }
}
```



> 断定型接口：有一个输入参数，返回值只能是 布尔值！


![](http://imgcloud.duiyi.xyz//data20200516003834.png).

```java
package com.godfrey.function;

import java.util.function.Predicate;

/**
 * description : 断定型接口,有一个输入参数，返回值只能是布尔值!
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo02 {
    public static void main(String[] args) {
        //判断字符串是否为空
        //Predicate<String> predicate = new Predicate<String>() {
        //    @Override
        //    public boolean test(String str) {
        //        return str.isEmpty();
        //    }
        //};

        Predicate<String> predicate = str -> { return str.isEmpty(); };
        System.out.println(predicate.test(""));
    }
}
```



> Consumer 消费型接口

![](http://imgcloud.duiyi.xyz//data20200516004312.png).

```java
package com.godfrey.function;

import java.util.function.Consumer;

/**
 * description : Consumer 消费型接口,只有输入，没有返回值
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo03 {
    public static void main(String[] args) {
        //Consumer<String> consumer = new Consumer<String>() {
        //    @Override
        //    public void accept(String str) {
        //        System.out.println(str);
        //    }
        //};
        Consumer<String> consumer = str -> System.out.println(str);
        consumer.accept("godfrey");
    }
}
```



> Supplier 供给型接口

![](http://imgcloud.duiyi.xyz//data20200516004933.png).

```java
package com.godfrey.function;

import java.util.function.Supplier;

/**
 * description : Supplier 供给型接口,没有参数，只有返回值
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo04 {
    public static void main(String[] args) {
        //Supplier supplier = new Supplier<Integer>() {
        //    @Override
        //    public Integer get() {
        //        System.out.println("get()");
        //        return 1024;
        //    }
        //};
        Supplier supplier = () -> { return 1024;};
        System.out.println(supplier.get());
    }
}
```



## 11.Stream流式计算

> 什么是Stream流式计算

大数据：存储 + 计算
集合、MySQL 本质就是存储东西的；
计算都应该交给流来操作！

![](http://imgcloud.duiyi.xyz//data20200516095630.png).

```java
package com.godfrey.stream;

import java.util.Arrays;
import java.util.List;

/**
 * description ：一分钟内完成此题，只能用一行代码实现！
 * 现在有5个用户！筛选：
 * 1、ID 必须是偶数
 * 2、年龄必须大于23岁
 * 3、用户名转为大写字母
 * 4、用户名字母倒着排序
 * 5、只输出一个用户！
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Test {
    public static void main(String[] args) {
        User u1 = new User(1, "a", 21);
        User u2 = new User(2, "b", 22);
        User u3 = new User(3, "c", 23);
        User u4 = new User(4, "d", 24);
        User u5 = new User(6, "e", 25);

        //集合就算存储
        List<User> list = Arrays.asList(u1, u2, u3, u4, u5);

        //计算交给Stream流
        list.stream()
                .filter(u -> { return u.getId() % 2 == 0; })
                .filter(u->{return u.getAge()>23;})
                .map(u->{return u.getName().toUpperCase();})
                .sorted((uu1,uu2)->{return uu2.compareTo(uu1);})
                .limit(1)
                .forEach(System.out::println);
    }
}
```



## 12.ForkJoin

> 什么是 ForkJoin

ForkJoin 在 JDK 1.7 ， 并行执行任务！提高效率。大数据量！
大数据：Map Reduce （把大任务拆分为小任务）

![](http://imgcloud.duiyi.xyz//data20200516100134.png)



> ForkJoin 特点：工作窃取

这个里面维护的都是双端队列

![](http://imgcloud.duiyi.xyz//data20200516100358.png).



> Forkjoin

![](http://imgcloud.duiyi.xyz//data20200516104234.png).

![](http://imgcloud.duiyi.xyz//data20200516104401.png)



```java
package com.godfrey.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * 求和计算的任务！
 * 如何使用 forkjoin
 * 1、forkjoinPool 通过它来执行
 * 2、计算任务 forkjoinPool.execute(ForkJoinTask task)
 * 3、 计算类要继承 ForkJoinTask
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class ForkJoinDemo extends RecursiveTask<Long> {
    private Long start;
    private Long end;

    //临界值
    private Long temp = 10000L;

    public ForkJoinDemo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }


    //计算方法
    @Override
    protected Long compute() {
        if ((end - start) > temp) {
            Long sum = 0L;
            for (Long i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        } else { // forkjoin 递归
            Long middle = (start + end) / 2;//中间值
            ForkJoinDemo task1 = new ForkJoinDemo(start, middle);
            task1.fork(); // 拆分任务，把任务压入线程队列
            ForkJoinDemo task2 = new ForkJoinDemo(middle + 1, end);
            task2.fork(); // 拆分任务，把任务压入线程队列
            return task1.join() + task2.join();
        }
    }
}
```

测试：

```java
package com.godfrey.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * description : 效率测试
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //test1();  //sum=499999999500000000 时间：7192
        //test2();  //sum=499999999500000000 时间：6949
        test3();    //sum=500000000500000000 时间：526
    }

    // 普通程序员
    public static void test1() {
        Long sum = 0L;
        Long start = System.currentTimeMillis();
        for (Long i = 0L; i < 10_0000_0000L; i++) {
            sum += i;
        }
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + " 时间：" + (end - start));
    }

    //ForkJoin
    public static void test2() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Long> task = new ForkJoinDemo(0L, 10_0000_0000L);
        ForkJoinTask<Long> submit = forkJoinPool.submit(task);
        Long sum = submit.get();

        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + " 时间：" + (end - start));
    }

    //Stream并行流
    public static void test3() {
        Long start = System.currentTimeMillis();

        Long sum = LongStream.rangeClosed(0L, 10_0000_0000L).parallel().reduce(0, Long::sum);
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + " 时间：" + (end - start));
    }
}
```



## 13.异步回调

> Future 设计的初衷： 对将来的某个事件的结果进行建模

![](http://imgcloud.duiyi.xyz//data20200516111155.png)

```java
package com.godfrey.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * description : 异步回调
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //test1();
        test02();
    }

    // 没有返回值的 runAsync 异步回调
    public static void test1() throws InterruptedException, ExecutionException {

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "runAsync=>Void");
        });
        System.out.println("1111");
        completableFuture.get(); // 获取阻塞执行结果
    }

    // 有返回值的 supplyAsync 异步回调
    // ajax，成功和失败的回调
    // 返回的是错误信息；
    public static void test02() throws InterruptedException, ExecutionException {

        CompletableFuture<Integer> completableFuture =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName() + "supplyAsync=>Integer");
                    int i = 10 / 0;
                    return 1024;
                });
        System.out.println(completableFuture.whenComplete((t, u) -> {
            System.out.println("t=>" + t); // 正常的返回结果
            System.out.println("u=>" + u); // 错误信息：java.util.concurrent.CompletionException:java.lang.ArithmeticException: / by zero

        }).exceptionally((e) -> {
            System.out.println(e.getMessage());
            return 233; // 可以获取到错误的返回结果
        }).get());
        /**
         * succee Code 200
         * error Code 404 500
         */}
}
```



## 14.JMM

> 请你谈谈你对 Volatile 的理解

Volatile 是 Java 虚拟机提供轻量级的同步机制

1. 保证可见性
2. ==不保证原子性==
3. 禁止指令重排



> 什么是JMM

JMM ： Java内存模型，不存在的东西，概念！约定！

**关于JMM的一些同步的约定：**

1. 线程解锁前，必须把共享变量==立刻==刷回主存
2. 线程加锁前，必须读取主存中的最新值到工作内存中！
3. 加锁和解锁是同一把锁



线程 **工作内存 、主内存**

**8种操作：**

![](http://imgcloud.duiyi.xyz//data20200516114621.png)

![](http://imgcloud.duiyi.xyz//data20200516114655.png)



**内存交互操作有8种，虚拟机实现必须保证每一个操作都是原子的，不可再分的（对于double和long类**
**型的变量来说，load、store、read和write操作在某些平台上允许例外）**

- lock （锁定）：作用于主内存的变量，把一个变量标识为线程独占状态
- unlock （解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定
- read （读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便随后的load动作使用
- load （载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中
- use （使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机遇到一个需要使用到变量的值，就会使用到这个指令
- assign （赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变量副本中
- store （存储）：作用于主内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，以便后续的write使用
- write （写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中

**JMM对这八种指令的使用，制定了如下规则：**

- 不允许read和load、store和write操作之一单独出现。即使用了read必须load，使用了store必须write
- 不允许线程丢弃他最近的assign操作，即工作变量的数据改变了之后，必须告知主存
- 不允许一个线程将没有assign的数据从工作内存同步回主内存
- 一个新的变量必须在主内存中诞生，不允许工作内存直接使用一个未被初始化的变量。就是怼变量
  实施use、store操作之前，必须经过assign和load操作
- 一个变量同一时间只有一个线程能对其进行lock。多次lock后，必须执行相同次数的unlock才能解锁
- 如果对一个变量进行lock操作，会清空所有工作内存中此变量的值，在执行引擎使用这个变量前，必须重新load或assign操作初始化变量的值
- 如果一个变量没有被lock，就不能对其进行unlock操作。也不能unlock一个被其他线程锁住的变量
- 对一个变量进行unlock操作之前，必须把此变量同步回主内存



## 15.Volatile

> 1.保证可见性

```java
package com.godfrey.tvolatile;

import java.util.concurrent.TimeUnit;

/**
 * description : volatile 保证可见性
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class JMMDemo {
    // 不加 volatile 程序就会死循环！
    // 加 volatile 可以保证可见性
    private volatile static int num = 0;

    public static void main(String[] args) { // main
        new Thread(() -> { // 线程 1 对主内存的变化不知道的
            while (num == 0) {
            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        num = 1;
        System.out.println(num);
    }
}
```



> 2.不保证原子性

原子性 : 不可分割
线程A在执行任务的时候，不能被打扰的，也不能被分割。要么同时成功，要么同时失败

```java
package com.godfrey.tvolatile;

/**
 * description : volatile 不保证原子性
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class JMMDemo {
    private volatile static int num = 0;

    public static void add() {
        num++;
    }

    public static void main(String[] args) {
        //理论上num结果应该为 2 万
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        while (Thread.activeCount() > 2) { // main gc
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + " " + num);
    }
}
```

**如果不加 lock 和 synchronized ，怎么样保证原子性**

![](http://imgcloud.duiyi.xyz//data20200516174823.png)

使用原子类，解决原子性问题

![](http://imgcloud.duiyi.xyz//data20200516175025.png).

```java
package com.godfrey.tvolatile;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * description : volatile 不保证原子性
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class VDemo02 {
    // volatile 不保证原子性
    // 原子类的 Integer
    private volatile static AtomicInteger num = new AtomicInteger();

    public static void add() {
        // num++; // 不是一个原子性操作
        num.getAndIncrement(); // AtomicInteger + 1 方法， CAS
    }

    public static void main(String[] args) {
        //理论上num结果应该为 2 万
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        while (Thread.activeCount() > 2) { // main gc
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + " " + num);
    }
}
```

这些类的底层都直接和操作系统挂钩！在内存中修改值！Unsafe类是一个很特殊的存在！



> 指令重排

什么是 指令重排：你写的程序，**计算机并不是按照你写的那样去执行的**
源代码-->编译器优化的重排--> 指令并行也可能会重排--> 内存系统也会重排---> 执行

**==处理器在进行指令重排的时候，考虑：数据之间的依赖性！==**

```java
int x = 1; // 1
int y = 2; // 2
x = x + 5; // 3
y = x * x; // 4
我们所期望的：1234 但是可能执行的时候回变成 2134 1324
可不可能是 4123！
```

可能造成影响的结果： a b x y 这四个值默认都是 0；

| 线程A | 线程B |
| ----- | ----- |
| x=a   | y=b   |
| b=1   | a=2   |

正常的结果： x = 0；y = 0；但是可能由于指令重排

| 线程A | 线程B |
| ----- | ----- |
| b=1   | a=2   |
| x=a   | y=b   |

指令重排导致的诡异结果： x = 2；y = 1；



> 禁止指令重排

**volatile可以禁止指令重排：**

内存屏障。CPU指令。作用：

1. 保证特定的操作的执行顺序！
2. 可以保证某些变量的内存可见性 （利用这些特性volatile实现了可见性）

![](http://imgcloud.duiyi.xyz//data20200516181146.png).

**Volatile 是可以保持 可见性。不能保证原子性，由于内存屏障，可以保证避免指令重排的现象产生！**



## 16.彻底玩转单例模式

饿汉式 DCL懒汉式，深究！

> 饿汉式

```java
package com.godfrey.single;

/**
 * description : 饿汉式单例
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Hungry {
    private Hungry() {
        
    }

    public final static Hungry HUNGRY = new Hungry();

    public static Hungry getInstance() {
        return HUNGRY;
    }
}
```



> 静态内部类

```java
package com.godfrey.single;

/**
 * description : 静态内部类单例
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Holder {
    private Holder() {

    }

    private static class InnerClass {
        private final static Holder HOLDER = new Holder();
    }

    public static Holder getInstance() {
        return InnerClass.HOLDER;
    }
}
```

> 单例不安全，反射





> 枚举

```java
package com.godfrey.single;

/**
 * description : 枚举单例
 *
 * @author godfrey
 * @since 2020-05-16
 */
public enum EnumSingle {
    INSTANCE;
}
```

通过class文件反编译得到Java文件：

![](http://imgcloud.duiyi.xyz//data20200516202300.png)

```java
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EnumSingle.java

package com.godfrey.single;

import java.io.PrintStream;

public final class EnumSingle extends Enum
{

    public static EnumSingle[] values()
    {
        return (EnumSingle[])$VALUES.clone();
    }

    public static EnumSingle valueOf(String name)
    {
        return (EnumSingle)Enum.valueOf(com/godfrey/single/EnumSingle, name);
    }

    private EnumSingle(String s, int i)
    {
        super(s, i);
    }

    public static void main(String args[])
    {
        System.out.println(INSTANCE);
    }

    public static final EnumSingle INSTANCE;
    private static final EnumSingle $VALUES[];

    static 
    {
        INSTANCE = new EnumSingle("INSTANCE", 0);
        $VALUES = (new EnumSingle[] {
            INSTANCE
        });
    }
}
```



可以发现枚举的单例构造器是==有参==的



## 17.深入理解CAS

> 什么是 CAS

```java
package com.godfrey.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * description : CAS compareAndSet : 比较并交换
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class CASDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(2020);

        // 期望、更新
        // public final boolean compareAndSet(int expectedValue, int newValue)
        // 如果我期望的值达到了，那么就更新，否则，就不更新, CAS 是CPU的并发原语！

        System.out.println(atomicInteger.compareAndSet(2020, 2021));
        System.out.println(atomicInteger.get());
        atomicInteger.getAndIncrement();
        System.out.println(atomicInteger.compareAndSet(2020, 2021));
        System.out.println(atomicInteger.get());
    }
}
```



> Unsafe 类

![](http://imgcloud.duiyi.xyz//data20200516204302.png)

![](http://imgcloud.duiyi.xyz//data20200516204348.png)

![](http://imgcloud.duiyi.xyz//data20200516204619.png)



CAS ： 比较当前工作内存中的值和主内存中的值，如果这个值是期望的，那么则执行操作！如果不是就一直循环！
**缺点：**

1. 循环会耗时
2. 一次性只能保证一个共享变量的原子性
3. ABA问题



> CAS ： ABA 问题（狸猫换太子）

![](http://imgcloud.duiyi.xyz//data20200516204901.png)

```java
package com.godfrey.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * description : CAS问题：ABA（狸猫换太子）
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class ABADemo {
    // CAS compareAndSet : 比较并交换！
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(2020);

        // 期望、更新
        // public final boolean compareAndSet(int expect, int update)
        // 如果我期望的值达到了，那么就更新，否则，就不更新, CAS 是CPU的并发原语！
        // ============== 捣乱的线程 ==================
        System.out.println(atomicInteger.compareAndSet(2020, 2021));
        System.out.println(atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(2021, 2020));
        System.out.println(atomicInteger.get());
        // ============== 期望的线程 ==================
        System.out.println(atomicInteger.compareAndSet(2020, 6666));
        System.out.println(atomicInteger.get());
    }
}
```



## 18.原子引用

> 解决ABA 问题，引入原子引用！ 对应的思想：乐观锁

带版本号 的原子操作！

```java
package com.godfrey.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * description : 原子引用解决ABA问题
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class AtomicStampedReferenceDemo {
    //AtomicStampedReference 注意，如果泛型是一个包装类，注意对象的引用问题
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(1, 1);

    public static void main(String[] args) {
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp(); // 获得版本号
            System.out.println("a1=>" + stamp);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedReference.compareAndSet(1, 2, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println("a2=>" + atomicStampedReference.getStamp());

            System.out.println(atomicStampedReference.compareAndSet(2, 1, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1));
            System.out.println("a3=>" + atomicStampedReference.getStamp());
        }, "a").start();

        // 乐观锁的原理相同！
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp(); // 获得版本号
            System.out.println("b1=>" + stamp);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicStampedReference.compareAndSet(1, 6, stamp, stamp + 1));
            System.out.println("b2=>" + atomicStampedReference.getStamp());
        }, "b").start();
    }
}
```

**注意：**
**Integer 使用了对象缓存机制，默认范围是 -128 ~ 127 ，推荐使用静态工厂方法 valueOf 获取对象实例，而不是 new，因为 valueOf 使用缓存，而 new 一定会创建新的对象分配新的内存空间；**

![](http://imgcloud.duiyi.xyz//data20200516211722.png)



## 19.各种锁的理解

### 1.公平锁、非公平锁

公平锁： 非常公平， 不能够插队，必须先来后到！
非公平锁：非常不公平，可以插队 （默认都是非公平）

```java
public ReentrantLock() {
	sync = new NonfairSync();
}
public ReentrantLock(boolean fair) {
	sync = fair ? new FairSync() : new NonfairSync();
}
```



### 2.可重入锁

可重入锁（递归锁）

![](http://imgcloud.duiyi.xyz//data20200516212130.png)



> Synchronized

```java
package com.godfrey.lock;

/**
 * description : Synchronized
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo01 {
    public static void main(String[] args) {
        Phone phone = new Phone();

        new Thread(() -> {
            phone.sms();
        }, "A").start();

        new Thread(() -> {
            phone.sms();
        }, "B").start();
    }
}

class Phone {
    public synchronized void sms() {
        System.out.println(Thread.currentThread().getName() + "sms");
        call(); // 这里也有锁
    }

    public synchronized void call() {
        System.out.println(Thread.currentThread().getName() + "call");
    }
}
```



> Lock 版

```java
package com.godfrey.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * description :
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo02 {
    public static void main(String[] args) {
        Phone2 phone = new Phone2();
        new Thread(() -> {
            phone.sms();
        }, "A").start();

        new Thread(() -> {
            phone.sms();
        }, "B").start();
    }
}

class Phone2 {
    Lock lock = new ReentrantLock();

    public void sms() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "sms");
            call(); // 这里也有锁
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void call() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "call");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```



### 3.自旋锁

spinlock

![](http://imgcloud.duiyi.xyz//data20200516222106.png)

我们来自定义一个锁测试

```java
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
```

> 测试

```java
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
```

![](http://imgcloud.duiyi.xyz//data20200516222514.png).



### 4.死锁

> 死锁是什么

![](http://imgcloud.duiyi.xyz//data20200516222557.png)

死锁测试，怎么排除死锁：

```java
package com.godfrey.lock;

import java.util.concurrent.TimeUnit;

/**
 * description : 死锁Demo
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class DeadLockDemo {
    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";
        new Thread(new MyThread(lockA, lockB), "T1").start();
        new Thread(new MyThread(lockB, lockA), "T2").start();
    }
}

class MyThread implements Runnable {
    private String lockA;
    private String lockB;

    public MyThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() +
                    "lock:" + lockA + "=>get" + lockB);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() +
                        "lock:" + lockB + "=>get" + lockA);
            }
        }
    }
}
```



> 解决问题

1. 使用 `jps -l` 定位进程号

![](http://imgcloud.duiyi.xyz//data20200516223220.png)



2. 使用` jstack 进程号 `找到死锁问题

![](http://imgcloud.duiyi.xyz//data20200516223744.png)



==**代码传送门：**==

- [gitee](https://gitee.com/ilovemo/juc-study/tree/master#%E5%A4%9A%E7%BA%BF%E7%A8%8B%E8%BF%9B%E9%98%B6---juc%E5%B9%B6%E5%8F%91%E7%BC%96%E7%A8%8B)
- [github](https://github.com/LionelMessi1010/juc-study)