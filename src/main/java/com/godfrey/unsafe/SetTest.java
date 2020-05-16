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
