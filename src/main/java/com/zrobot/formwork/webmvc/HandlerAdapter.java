package com.zrobot.formwork.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author weizhaoli
 * @title: HandlerAdapter
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/1 17:45
 */
public class HandlerAdapter {

    private Map<String, Integer> paramMapping;

    public HandlerAdapter(Map<String, Integer> paramMapping){
        this.paramMapping = paramMapping;
    }

    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping hander) {

        //拿到方法的形参列表
        Class<?>[] parameterTypes = hander.getMethod().getParameterTypes();

        //拿到自定义参数的位置
        //用户通过URL传递的参数列表
        Map<String, String[]> parameterMap = req.getParameterMap();

        //构造实参列表
        Object[] args = new Object[parameterTypes.length];
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", ",");

            if (!this.paramMapping.containsKey(param.getKey())){continue;}

            //参数位置
            Integer index = this.paramMapping.get(param.getKey());
            args[index] = caseStringValue(value, parameterTypes[index]);
        }

        if (this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            Integer reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            args[reqIndex] = req;
        }

        if (this.paramMapping.containsKey(HttpServletResponse.class.getName())){
            Integer respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            args[respIndex] = req;
        }

        //拿到controller
        try {
            Object invoke = hander.getMethod().invoke(hander.getController(), args);

            if (hander.getMethod().getReturnType() == ModelAndView.class){
                return (ModelAndView)invoke;
            }else {
                return null;
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //执行方法

        return null;
    }

    private Object caseStringValue(String value, Class clazz){
        if (clazz == String.class){
            return value;
        }else if (clazz == Integer.class){
            return Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }
    }
}
