package com.zrobot.formwork.annotation;

import java.lang.annotation.*;

/**
 * @author weizhaoli
 * @title: RequestMapping
 * @projectName selfspring
 * @description: TODO
 * @date 2019/7/29 15:09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";
}
