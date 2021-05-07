package com.demo.spring.simulation.v5.beans;

/**
 * Spring IoC容器对Bean生成的代理类
 */
public class MyBeanWrapper {
    // Bean的实例化对象
    private Object wrappedInstance;

    // Bean的Class信息
    private Class<?> wrapperClass;

    public MyBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
        this.wrapperClass = wrappedInstance.getClass();
    }

    public Object getWrapperInstance() {
        return this.wrappedInstance;
    }

    // 返回代理Class
    public Class<?> getWrapperClass() {
        return this.wrapperClass;
    }
}
