package com.demo.spring.simulation.v5.beans.config;

import lombok.Data;

/**
 * Spring Bean定义信息
 */
@Data
public class MyBeanDefinition {
    // Bean全路径类名
    private String beanClassName;

    // Bean在IoC容器中名称
    private String factoryBeanName;
}
