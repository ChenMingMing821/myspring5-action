package com.demo.spring.simulation.v5.annotation;

import java.lang.annotation.*;

/**
 * 自定义Service注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyService {
    String value() default "";
}
