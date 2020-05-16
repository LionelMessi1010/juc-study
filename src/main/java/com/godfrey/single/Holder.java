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
