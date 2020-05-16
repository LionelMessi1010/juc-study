package com.godfrey.function;

import java.util.function.Predicate;

/**
 * description : 断定型接口,有一个输入参数，返回值只能是布尔值!
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo02 {
    public static void main(String[] args) {
        //判断字符串是否为空
        //Predicate<String> predicate = new Predicate<String>() {
        //    @Override
        //    public boolean test(String str) {
        //        return str.isEmpty();
        //    }
        //};

        Predicate<String> predicate = str -> { return str.isEmpty();};
        System.out.println(predicate.test(""));
    }
}
