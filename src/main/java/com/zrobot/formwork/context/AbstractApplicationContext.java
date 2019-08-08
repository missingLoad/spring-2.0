package com.zrobot.formwork.context;

/**
 * @author weizhaoli
 * @title: AbstractApplicationContext
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/7 14:24
 */
public abstract class AbstractApplicationContext {

    /**
     * 留给字类重写
     */
    protected void onRefresh() {
        // For subclasses: do nothing by default.
    }

    protected abstract void refreshBeanFactory();
}
