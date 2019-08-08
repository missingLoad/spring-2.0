package com.zrobot.formwork.support;

import com.zrobot.formwork.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author weizhaoli
 * @title: BeanDefinitionReader
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/7/30 10:35
 */
public class BeanDefinitionReader {

    //配置文件
    private Properties config = new Properties();

    private List<String> reginsterBeanClasses = new ArrayList<>();

    private String SCAN_PACKAGE = "scanPackage";


    public BeanDefinitionReader(String... locations){

        //将配置文件加载进内存
        if (locations != null && locations.length > 0){
            for (String location : locations){
                location = location.replace("classpath:", "");
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);

                try {
                    config.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (inputStream != null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //扫描所有的类 并获取全限定类名
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    public List<String> loadBeanDefinitions(){

        loadBeanDefinitions(config.getProperty(SCAN_PACKAGE));

        return this.reginsterBeanClasses;
    }

    private void loadBeanDefinitions(String packageName){
        URL url = this.getClass().getClassLoader().getResource("/" + config.getProperty(SCAN_PACKAGE).replaceAll("\\.", "/"));

        File fileDir = new File(url.getFile());

        for (File file : fileDir.listFiles() ){
            if (file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            }else {
                this.reginsterBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    public BeanDefinition registerBean(String className){
        if (!reginsterBeanClasses.contains(className)) {
            return null;
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(className);
        beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
        return beanDefinition;
    }

    public Properties getConfig(){
        return this.config;
    }


    /**
     * 扫描所有的class 并且将类名保存在内存中
     * @param packageName
     */
    private void doScanner(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));

        File fileDir = new File(url.getFile());

        for (File file : fileDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                reginsterBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    /**
     * 小写首字母
     * @param str
     * @return
     */
    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;

        return new String(chars);
    }
}
