package com.demo.spring.simulation.v5.annotation;

import java.lang.annotation.*;

/**
 * 自定义Autowired注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
