package com.webser.annotation;

import java.lang.annotation.*;

//定义表
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
