package com.demo.spring.simulation.v5.annotation;

import java.lang.annotation.*;

/**
 * 自定义RequestParam注解
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
