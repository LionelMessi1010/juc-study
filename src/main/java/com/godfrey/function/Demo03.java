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
