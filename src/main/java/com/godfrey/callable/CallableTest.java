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
