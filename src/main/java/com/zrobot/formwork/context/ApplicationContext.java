package com.zrobot.formwork.context;

import com.zrobot.formwork.annotation.Autowired;
import com.zrobot.formwork.annotation.Controller;
import com.zrobot.formwork.annotation.Service;
import com.zrobot.formwork.aop.AopConfig;
import com.zrobot.formwork.beans.BeanDefinition;
import com.zrobot.formwork.beans.BeanPostProcessor;
import com.zrobot.formwork.beans.BeanWarpper;
import com.zrobot.formwork.core.BeanFactory;
import com.zrobot.formwork.support.BeanDefinitionReader;
import org.omg.CORBA.INTF_REPOS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weizhaoli
 * @title: ApplicationContext
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/7/30 10:16
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {


    private String[] configLocations;

    private BeanDefinitionReader beanDefinitionReader;


    //保存注册式单例
    private Map<String, Object> beanCacheMap = new HashMap<>();

    //存储所有的被代理过的对象
    private Map<String, BeanWarpper> beanWarpperMap = new ConcurrentHashMap<>();

    /**
     *
     * @param locations 配置文件地址
     */
    public ApplicationContext(String... locations){
        this.configLocations = locations;
        this.refresh();
    }


    public void refresh(){

        //定位
        this.beanDefinitionReader = new BeanDefinitionReader(this.configLocations);
        //加载
        List<String> beanDefinitions = beanDefinitionReader.loadBeanDefinitions();
        doRegister(beanDefinitions);
        //注册
        doAutowired();
        //依赖注入 lazy-init = false
    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()){
            String key = entry.getKey();
            if (!entry.getValue().isLazyInit()){
                Object bean = getBean(key);
                System.out.println(bean.getClass());
            }
        }
    }

    public void populateBean(String beanName, Object instance){
        Class<?> clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if (!field.isAnnotationPresent(Autowired.class)){
                continue;
            }

            Autowired autowired = field.getAnnotation(Autowired.class);

            String autowiredBeanName = autowired.value().trim();

            if ("".equals(autowiredBeanName)){
                autowiredBeanName = lowerFirstCase(field.getType().getSimpleName());
            }

            field.setAccessible(true);

            try {
                field.set(instance, this.beanWarpperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private void doRegister(List<String> beanDefinitions) {

        if (beanDefinitions == null || beanDefinitions.isEmpty()){
            return;
        }

        try {
            for (String className : beanDefinitions){
                Class<?> clazz = Class.forName(className);

                if (clazz.isInterface()){
                    continue;
                }

                BeanDefinition beanDefinition = beanDefinitionReader.registerBean(className);

                if (beanDefinition != null){
                    beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }

                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class i : interfaces){
                    this.beanDefinitionMap.put(i.getName(), beanDefinition);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String name) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(name);

        try {
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object o = instanceBean(beanDefinition);

            if (o == null){
                return null;
            }
            beanPostProcessor.postProcessBeforeInitialization(o, name);

            BeanWarpper beanWarpper = new BeanWarpper(o);
            beanWarpper.setAopConfig(instanceAopConfig(beanDefinition));
            beanWarpper.setBeanPostProcessor(beanPostProcessor);
            this.beanWarpperMap.put(name, beanWarpper);

            beanPostProcessor.postProcessAfterInitialization(o, name);

            populateBean(name, o);
            return this.beanWarpperMap.get(name).getWrappedInstance();

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据bean的配置信息创建实例
     * @param beanDefinition
     * @return
     */
    private Object instanceBean(BeanDefinition beanDefinition){
        String beanClassName = beanDefinition.getBeanClassName();

        Object instance = null;

        try {

            if (beanCacheMap.containsKey(beanClassName)){
                instance =  this.beanCacheMap.get(beanClassName);
                return instance;
            }

            Class<?> clazz = Class.forName(beanClassName);
            instance = clazz.newInstance();

            this.beanCacheMap.put(beanClassName, instance);

        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;

        return new String(chars);
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
       return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig(){
        return  this.beanDefinitionReader.getConfig();
    }


    /**
     * 初始化拦截器
     * @param beanDefinition
     * @return
     */
    private AopConfig instanceAopConfig(BeanDefinition beanDefinition)throws Exception{
        AopConfig aopConfig = new AopConfig();

        String expression = this.beanDefinitionReader.getConfig().getProperty("pointCut");
        String[] aspectBefores = this.beanDefinitionReader.getConfig().getProperty("aspectBefore").split("\\s");
        String[] aspectAfters = this.beanDefinitionReader.getConfig().getProperty("aspectAfter").split("\\s");

        //被代理对象
        String className = beanDefinition.getBeanClassName();
        Class<?> targetClazz = Class.forName(className);

        //匹配规则正则表达式
        Pattern pattern = Pattern.compile(expression);

        //切面对象
        Class<?> aspectClazz = Class.forName(aspectBefores[0]);

        //对命中匹配规则的方法添加到代理配置中
        for (Method m : targetClazz.getMethods()){
            Matcher matcher = pattern.matcher(m.toString());

            if (matcher.matches()){
                aopConfig.put(m, aspectClazz.newInstance(), new Method[]{aspectClazz.getMethod(aspectBefores[1]), aspectClazz.getMethod(aspectAfters[1])});
            }

        }


        return aopConfig;
    }

}
