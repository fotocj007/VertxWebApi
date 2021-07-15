package com.webser.annotation;

import java.lang.annotation.*;

//定义主键
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
