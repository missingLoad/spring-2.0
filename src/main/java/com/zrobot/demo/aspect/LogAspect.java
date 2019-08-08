package com.zrobot.demo.aspect;

/**
 * @author weizhaoli
 * @title: LogAspect
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/7 15:23
 */
public class LogAspect {

    public void before(){
        System.out.println("============================");
    }

    public void after(){
        System.out.println("---------------------------");
    }
}
