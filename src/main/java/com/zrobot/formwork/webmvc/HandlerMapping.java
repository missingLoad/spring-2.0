package com.zrobot.formwork.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author weizhaoli
 * @title: HandlerMapping
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/1 17:44
 */
public class HandlerMapping {

    private Object controller;

    private Method method;

    private Pattern pattern;

    public HandlerMapping(Pattern pattern, Object controller, Method method){
        this.controller = controller;
        this.pattern = pattern;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
