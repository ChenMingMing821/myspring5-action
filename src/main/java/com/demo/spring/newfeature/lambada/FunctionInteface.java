package com.demo.spring.newfeature.lambada;

import java.util.List;

/**
 * 函数式接口
 */
public class FunctionInteface {

    /* 以下是一个函数式接口： */
    @FunctionalInterface
    public interface IFuntionSum<T extends Number> {
        // 抽象方法
        T sum(List<T> numbers);
    }

    /* 以下也是一个函数式接口： */
    @FunctionalInterface
    public interface IFunctionMulti<T extends Number> {
        // 抽象方法
        void multi(List<T> numbers);

        // Object中的方法
        boolean equals(Object obj);
    }

    /* 但如果改为以下形式，则不是函数式接口： */
    // IFunctionMulti 接口继承了 IFuntionSum 接口，此时 IFunctionMulti 包含了2个抽象方法
    // @FunctionalInterface //该注解直接报错了
    public interface IFunctionDiv<T extends Number> extends IFuntionSum<T> {
        void div(List<T> numbers);

        @Override
        boolean equals(Object obj);
    }
}
