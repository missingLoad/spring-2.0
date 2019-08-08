package com.zrobot.formwork.annotation;

import java.lang.annotation.*;

/**
 * @author weizhaoli
 * @title: Service
 * @projectName selfspring
 * @description: TODO
 * @date 2019/7/29 15:12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String value() default "";

}
