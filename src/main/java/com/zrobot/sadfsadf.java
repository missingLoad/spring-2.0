//package com.zrobot;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.Arrays;
//
///**
// * @author weizhaoli
// * @title: sadfsadf
// * @projectName spring-2.0
// * @description: TODO
// * @date 2019/7/30 10:12
// */
//public class sadfsadf {
//
//    static interface Subject{
//        void sayHi();
//        void sayHello();
//    }
//
//    static class SubjectImpl{
//
//
//        public void sayHi() {
//            System.out.println("hi");
//        }
//
//
//        public void sayHello() {
//            System.out.println("hello");
//        }
//    }
//
//    static class ProxyInvocationHandler implements InvocationHandler {
//        private SubjectImpl target;
//        public ProxyInvocationHandler(SubjectImpl target) {
//            this.target=target;
//        }
//
//        @Override
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            System.out.print("say:");
//            return method.invoke(target, args);
//        }
//
//    }
//
//    public static void main(String[] args) {
//        SubjectImpl subject=new SubjectImpl();
//        Object o = Proxy.newProxyInstance(subject.getClass().getClassLoader(), subject.getClass().getInterfaces(), new ProxyInvocationHandler(subject));
//        SubjectImpl cast = SubjectImpl.class.cast(o);
//        cast.sayHi();
////        subjectProxy.sayHi();
////        subjectProxy.sayHello();
//
//    }
//}
