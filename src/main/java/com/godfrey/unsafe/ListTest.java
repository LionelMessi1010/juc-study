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
