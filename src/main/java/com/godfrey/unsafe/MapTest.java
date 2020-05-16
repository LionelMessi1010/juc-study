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
