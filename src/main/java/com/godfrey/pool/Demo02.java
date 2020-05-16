package com.godfrey.pool;

import java.util.concurrent.*;

/**
 * description : 七大参数与四种拒绝策略
 * 四种拒绝策略：
 * AbortPolicy(默认)：队列满了，还有任务进来，不处理这个任务的，直接抛出 RejectedExecution异常！
 * CallerRunsPolicy：哪来的去哪里！
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
