package com.zrobot.formwork.aop;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author weizhaoli
 * @title: ProxyUtils
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/7 18:09
 */
public class ProxyUtils {

    /**
     * 从代理对象拿到被代理对象
     * @param proxy
     * @return
     */
    public static Object getTargetObject(Object proxy)throws Exception{
        //先判断对象是否是代理对象
        if (!isAopInstance(proxy)){
            return proxy;
        }

        return getProxyTarget(proxy);
    }

    private static boolean isAopInstance(Object proxy){
        return (proxy instanceof CglibProxy);
    }

    private static Object getProxyTarget(Object proxy)throws Exception{
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object aopProxy =  h.get(proxy);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return target.get(aopProxy);
    }
}
