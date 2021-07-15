package com.webser.annotation;

import java.lang.annotation.*;

//定义主键是否自动递增--有该注解时为自动递增
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedValue {
}
