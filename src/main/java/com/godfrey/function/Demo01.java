package com.godfrey.function;

import java.util.function.Function;

/**
 * description : Function 函数式接口,有一个输入参数，有一个输出
 * 只要是 函数型接口 可以 用 lambda表达式简化
 *
 * @author godfrey
 * @since 2020-05-16
 */
public class Demo01 {
    public static void main(String[] args) {
        //Function function = new Function<String, String>(){
        //    @Override
        //    public String apply(String o) {
        //        return o;
        //    }
        //};

        Function function = str->{return str;};

        System.out.println(function.apply("123"));
    }
}
