package com.demo.spring.simulation.v5.annotation;

import java.lang.annotation.*;

/**
 * 自定义RequestMapping注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
