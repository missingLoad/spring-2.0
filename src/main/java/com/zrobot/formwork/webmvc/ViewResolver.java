package com.zrobot.formwork.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weizhaoli
 * @title: ViewResolver
 * @projectName spring-2.0
 * @description: TODO
 * @date 2019/8/1 17:46
 */
public class ViewResolver {

    private String viewName;

    private File template;


    public ViewResolver(String viewName, File template){
        this.viewName = viewName;
        this.template = template;
    }

    public String viewResolver(ModelAndView modelAndView)throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.template, "r");

        String line = null;
        while (null !=(line = ra.readLine())){
            Matcher matcher = matcher(line);
            while (matcher.find()){
                for (int i =0; i < matcher.groupCount(); i ++){
                    String paramName = matcher.group(i);
                    Object paramValue = modelAndView.getModel().get(paramName);
                    if (null == paramValue){continue;}
                    line = line.replaceAll("￥\\{" + paramName + "\\}", paramValue.toString());
                }
            }

            sb.append(line);
        }

        return sb.toString();
    }


    private Matcher matcher(String str){
        Pattern pattern = Pattern.compile("￥\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }
}
