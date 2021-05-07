package com.demo.spring.simulation.v5.aop;

import com.demo.spring.simulation.v5.aop.aspect.MyAdvice;
import com.demo.spring.simulation.v5.aop.support.MyAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 基于JDK动态代理的AOP实现
 */
public class MyJdkDynamicAopProxy implements InvocationHandler {

    // AOP核心支撑类，保存了被代理类Class和实例化对象、AOP定义等。
    private MyAdvisedSupport advisedSupport;

    public MyJdkDynamicAopProxy(MyAdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    /**
     * 通过动态代理，生成目标类的代理类。
     *
     * @return
     */
    public Object getProxy() {
        return Proxy
            .newProxyInstance(this.getClass().getClassLoader(), this.advisedSupport.getTargetClass().getInterfaces(),
                this);
    }

    /**
     * 执行具体代理方法。
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 1、获取方法对应的AOP定义。
        Map<String, MyAdvice> advices = this.advisedSupport
            .getInterceptorsAndDynamicInterceptionAdvice(method, this.advisedSupport.getTargetClass());

        // 2、执行AOP的before方法
        invokeAdvice(advices.get("before"));

        // 3、执行被代理类切点方法
        Object returnValue = null;
        try {
            returnValue = method.invoke(this.advisedSupport.getTarget(), args);
        } catch (Exception ex) {
            // 4、发生异常时，执行AOP的Exception方法
            invokeAdvice(advices.get("afterThrow"));
            throw ex;
        }

        // 5、调用AOP的after方法
        invokeAdvice(advices.get("after"));

        return returnValue;
    }

    /**
     * 通过反射完成方法调用。
     *
     * @param advice
     */
    private void invokeAdvice(MyAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
