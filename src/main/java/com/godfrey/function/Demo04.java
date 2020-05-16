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
