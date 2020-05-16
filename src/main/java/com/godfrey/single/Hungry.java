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
