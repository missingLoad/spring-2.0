package com.zrobot.formwork.beans;

import com.sun.istack.internal.Nullable;

/**
 * @author weizhaoli
 * @title: BeanPostProcessor
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/7/31 10:29
 */
public class BeanPostProcessor {

    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
