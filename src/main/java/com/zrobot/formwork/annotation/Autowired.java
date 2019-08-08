package com.zrobot.formwork.annotation;

import java.lang.annotation.*;

/**
 * @author weizhaoli
 * @title: Autowired
 * @projectName selfspring
 * @description: TODO
 * @date 2019/7/29 15:11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    String value() default "";
}
