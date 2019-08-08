package com.zrobot.formwork.context;

import com.zrobot.formwork.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author weizhaoli
 * @title: DefaultListableBeanFactory
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/7 14:29
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {

    //保存bean的配置信息
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    protected void onRefresh() {
    }

    @Override
    protected void refreshBeanFactory() {

    }
}
