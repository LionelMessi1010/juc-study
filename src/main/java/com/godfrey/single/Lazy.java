package com.godfrey.single;

/**
 * description : 懒汉式单例  DCL，双重检验锁
 * 25行：lazy = new Lazy(); 做内存做以下操作：
 * 1. 分配内存空间
 * 2、执行构造方法，初始化对象
 * 3、把这个对象指向这个空间
 * 可能指令重排，不是一个原子性操作，所以19行变量要加volatile限制
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Lazy {
    private Lazy() {

    }

    private volatile static Lazy lazy = null;

    public static Lazy getInstance() {
        if (lazy == null) {
            synchronized (Lazy.class) {
                if (lazy == null) {
                    lazy = new Lazy();
                }
            }
        }
        return lazy;
    }
}
