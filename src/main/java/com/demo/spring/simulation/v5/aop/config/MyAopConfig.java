package com.demo.spring.simulation.v5.aop.config;

import lombok.Data;

/**
 * 封装AOP的配置信息，包括切点、切面、切入环绕方法。
 */
@Data
public class MyAopConfig {
    // 切点
    private String pointCut;
    // 切面
    private String aspectClass;
    // before回调方法
    private String aspectBefore;
    // after回调方法
    private String aspectAfter;
    // 异常回调方法
    private String aspectAfterThrow;
    // 异常类型捕获
    private String aspectAfterThrowingName;
}
