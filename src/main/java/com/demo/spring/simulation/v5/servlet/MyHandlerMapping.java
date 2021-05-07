package com.demo.spring.simulation.v5.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * HandlerMapping映射注册、根据url获取对应的处理器、拦截器注册。
 */
@Data
public class MyHandlerMapping {

    // 请求URL的正则匹配
    private Pattern pattern;

    // URL对应的Method
    private Method method;

    // Method对应的实例对象
    private Object controller;

    public MyHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }
}
