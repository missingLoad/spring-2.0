package com.zrobot.formwork.annotation;

import java.lang.annotation.*;

/**
 * @author weizhaoli
 * @title: RequestParam
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/5 14:36
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";
}
