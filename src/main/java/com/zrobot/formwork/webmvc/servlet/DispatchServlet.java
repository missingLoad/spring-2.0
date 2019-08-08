package com.zrobot.formwork.webmvc.servlet;

import com.zrobot.formwork.annotation.Controller;
import com.zrobot.formwork.annotation.RequestMapping;
import com.zrobot.formwork.annotation.RequestParam;
import com.zrobot.formwork.aop.ProxyUtils;
import com.zrobot.formwork.context.ApplicationContext;
import com.zrobot.formwork.webmvc.HandlerAdapter;
import com.zrobot.formwork.webmvc.HandlerMapping;
import com.zrobot.formwork.webmvc.ModelAndView;
import com.zrobot.formwork.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weizhaoli
 * @title: DispatchServlet
 * @projectName selfspring
 * @description: TODO
 * @date 2019/7/29 11:33
 */
public class DispatchServlet extends HttpServlet {

    private String CONTEXTCONFIG_LOCATION = "contextConfigLocation";

    private String TEMPLATE_ROOT = "templateRoot";

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String url = req.getRequestURI();

        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replace("/+", "/");

        try {
            doDispatch(req, resp);
        }catch (Exception e){
            resp.getWriter().write("500 Exception ,Deatils :\r\n" + Arrays.toString(e.getStackTrace()).replace("\\[\\]", "").replaceAll("\\s", "\r\n"));
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        HandlerMapping hander = getHandler(req);
        if (hander == null){
            resp.getWriter().write("404 Not Found \r\n");
        }

        HandlerAdapter ha = getHandlerAdapter(hander);

        ModelAndView mv = ha.handle(req, resp, hander);

        processDispatchResult(resp, mv);

    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView mv) throws Exception {

        if (null == mv){return;}

        if (this.viewResolvers.isEmpty()){return;}

        for (ViewResolver view: this.viewResolvers){
            if (!mv.getViewName().equals(view.getViewName())){
                continue;
            }
            String out = view.viewResolver(mv);

            if (out != null){
                resp.getWriter().write(out);
            }
        }
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping hander) {

        if (this.handlerAdapters.isEmpty()){return  null;}
        return this.handlerAdapters.get(hander);
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()){
            return null;
        }
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        requestURI = requestURI.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handlerMapping : this.handlerMappings){
            Matcher matcher = handlerMapping.getPattern().matcher(requestURI);
            if (!matcher.matches()){continue;}
            return handlerMapping;
        }

        return null;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {

        ApplicationContext applicationContext = new ApplicationContext(config.getInitParameter(CONTEXTCONFIG_LOCATION));
        System.out.println(applicationContext);
        initStrategies(applicationContext);
    }

    /**
     * 初始化springmvc组件
     * @param context
     */
    protected void initStrategies(ApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
        initFlashMapManager(context);
    }



    /**
     * 实现动态模板的解析
     * @param context
     */
    private void initViewResolvers(ApplicationContext context) {
        String templateRoot = context.getConfig().getProperty(TEMPLATE_ROOT);
        String templateRootPath = this.getClass().getClassLoader().getResource("/" + templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File template : templateRootDir.listFiles()){
            this.viewResolvers.add(new ViewResolver(template.getName(), template));
        }
    }

    /**
     * 动态匹配Method参数 类型转换 和动态赋值
     * @param context
     */
    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : this.handlerMappings){
            Map<String, Integer> paramMapping = new ConcurrentHashMap<>();

            //处理命名参数
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0; i < pa.length; i ++){
                for (Annotation a : pa[i]){
                    if (a instanceof RequestParam){
                        String paramName = ((RequestParam) a).value();
                        if (!"".equals(paramName)){
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }

            //处理非命名参数
            Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i = 0; i < pa.length; i ++){
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
                    paramMapping.put(parameterType.getName(), i);
                }
            }

            this.handlerAdapters.put(handlerMapping, new HandlerAdapter(paramMapping));
        }
    }

    /**
     * 保存控制器和RequestMapping 以及 Method和RequestMapping的对应关系
     * @param context
     */
    private void initHandlerMappings(ApplicationContext context) {

        //获取所有的注册的对象名
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames){
            Object proxy = context.getBean(beanName);

            Class<?> proxyClazz = proxy.getClass();

            Object controller = null;
            try {
                controller = ProxyUtils.getTargetObject(proxy);


                Class<?> clazz = controller.getClass();
                //如果不是控制器 没必要映射请求路径
                if (!clazz.isAnnotationPresent(Controller.class)) {
                    continue;
                }

                String baseUrl = "";

                //控制器上面有映射地址
                if (clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = annotation.value();
                }

                //获取控制器里面所有的方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods){
                    if (!method.isAnnotationPresent(RequestMapping.class)){continue;}

                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    String regex = baseUrl + annotation.value().replace("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    Method proxyMethod = proxyClazz.getMethod(method.getName(), method.getParameterTypes());
                    this.handlerMappings.add(new HandlerMapping(pattern, proxy, proxyMethod));
                    System.out.println("Mapping: " + regex + " , " + method);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void initRequestToViewNameTranslator(ApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {

    }

    private void initFlashMapManager(ApplicationContext context) {

    }

    private void initThemeResolver(ApplicationContext context) {

    }

    private void initLocaleResolver(ApplicationContext context) {
    }

    private void initMultipartResolver(ApplicationContext context) {
    }
}
