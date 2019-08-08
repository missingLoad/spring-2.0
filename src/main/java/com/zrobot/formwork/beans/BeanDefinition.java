package com.zrobot.formwork.beans;

import com.sun.istack.internal.Nullable;

/**
 * @author weizhaoli
 * @title: BeanDefinition
 * @projectName spring-2.0
 * @description: 存储配置文件中的信息 保存在内存中的配置
 * @date 2019/7/30 10:37
 */
public class BeanDefinition {

    //类名
    private String beanClassName;

    private boolean lazyInit = false;

    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
