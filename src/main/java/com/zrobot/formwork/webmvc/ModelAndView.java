package com.zrobot.formwork.webmvc;

import com.sun.istack.internal.Nullable;

import java.util.Map;

/**
 * @author weizhaoli
 * @title: ModelAndView
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/1 17:41
 */
public class ModelAndView {

    private String viewName;

    private Map<String, ?> model;



    public ModelAndView(String viewName, @Nullable Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
