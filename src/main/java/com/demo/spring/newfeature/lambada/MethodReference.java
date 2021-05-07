package com.demo.spring.newfeature.lambada;

import java.util.function.Supplier;

/**
 * 方法引用
 */
public class MethodReference {
    public static void main(String[] args) {
        // Supplier是java.util.function下自带的函数式接口
        // 使用Lambada定义实现
        Supplier<Double> supplier1 = () -> Math.random();
        // 使用方法引用实现
        Supplier<Double> supplier2 = Math::random;
    }
}
