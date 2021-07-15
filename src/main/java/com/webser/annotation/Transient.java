package com.webser.annotation;

import java.lang.annotation.*;

//定义需要忽略的字段--实体类有,数据库没有的字段
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
}
