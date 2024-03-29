package com.zrobot.formwork.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weizhaoli
 * @title: AopConfig
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/7 15:28
 */
public class AopConfig {

    private Map<Method, Aspect> points = new HashMap<>();

    public void put(Method target, Object aspect, Method[] points){
        this.points.put(target, new Aspect(aspect, points));
    }

    public Aspect get(Method method){
        return this.points.get(method);
    }

    public boolean contains(Method method){
        return this.points.containsKey(method);
    }


    public class Aspect{
        private Object aspect;
        private Method[] points;

        public Aspect(Object aspect, Method[] points){
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public void setAspect(Object aspect) {
            this.aspect = aspect;
        }

        public Method[] getPoints() {
            return points;
        }

        public void setPoints(Method[] points) {
            this.points = points;
        }
    }

}
