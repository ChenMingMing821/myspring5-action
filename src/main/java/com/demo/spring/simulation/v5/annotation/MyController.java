package com.demo.spring.simulation.v5.annotation;

import java.lang.annotation.*;

/**
 * 自定义Controller注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}
