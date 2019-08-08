package com.zrobot.formwork.annotation;

import java.lang.annotation.*;

/**
 * @author weizhaoli
 * @title: Controller
 * @projectName selfspring
 * @description: TODO
 * @date 2019/7/29 15:08
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    String value() default "";
}
