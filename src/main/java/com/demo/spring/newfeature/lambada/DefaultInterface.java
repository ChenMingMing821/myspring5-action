package com.demo.spring.newfeature.lambada;

/**
 * Java8接口默认方法
 */
public interface DefaultInterface {
    // 普通抽象方法，默认是public abstract修饰的，没有方法体
    int size();

    /*
     * 默认方法，有方法体
     * 任何一个实现了接口的类都会向动继承isEmpty的实现
     */
    default boolean isEmpty() {
        return this.size() == 0;
    }
}
