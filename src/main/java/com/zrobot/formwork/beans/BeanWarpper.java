package com.zrobot.formwork.beans;

import com.zrobot.formwork.aop.AopConfig;
import com.zrobot.formwork.aop.AopProxy;
import com.zrobot.formwork.core.FactoryBean;

/**
 * @author weizhaoli
 * @title: BeanWarpper
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/7/30 10:37
 */
public class BeanWarpper extends FactoryBean {

    private AopProxy aopProxy = new AopProxy();

    private BeanPostProcessor beanPostProcessor;

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    private Object warpperInstance;

    private Object rootObject;

    public BeanWarpper(Object o){
        warpperInstance = aopProxy.getProxy(o);
        rootObject = o;
    }

    public Object getWrappedInstance(){
        return this.warpperInstance;
    }

    public Class<?> getWrappedClass(){
        return this.warpperInstance.getClass();
    }


    public void setAopConfig(AopConfig config) {
        this.aopProxy.setConfig(config);
    }

    public Object getRootObject() {
        return rootObject;
    }

    public void setRootObject(Object rootObject) {
        this.rootObject = rootObject;
    }
}
