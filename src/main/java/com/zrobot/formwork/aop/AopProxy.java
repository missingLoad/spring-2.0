package com.zrobot.formwork.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author weizhaoli
 * @title: AopProxy
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/7 14:38
 */
public class AopProxy implements InvocationHandler {


    private Object target;

    private AopConfig config;

    public Object getProxy(Object instance){

        this.target = instance;

        Class<?> clazz = instance.getClass();

        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //前置增强方法
        if (config.contains(method)){
            AopConfig.Aspect aspect = config.get(method);
            aspect.getPoints()[0].invoke(aspect.getAspect(), args);
        }

        Object invoke = method.invoke(proxy, args);

        //后置增强方法
        if (config.contains(method)){
            AopConfig.Aspect aspect = config.get(method);
            aspect.getPoints()[1].invoke(aspect.getAspect(), args);
        }
        return invoke;
    }

    public AopConfig getConfig() {
        return config;
    }

    public void setConfig(AopConfig config) {
        this.config = config;
    }
}
