package com.kangcenet.tms.admin.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ResponseBody
    @RequestMapping("/")
    public String index() {

// init job handler action
        String index = "";
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RequestMapping.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String[] value = serviceBean.getClass().getAnnotation(RequestMapping.class).value();
                for (String v : value) {
                    index = index + v + "|";
                }
                index = index + "-";
                Method[] methods = serviceBean.getClass().getDeclaredMethods();
                if (methods != null) {
                    for (Method method : methods) {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        if (annotation == null)
                            continue;
                        for (Method meth : methods) {
                            Class<?>[] ps = meth.getParameterTypes();
                            String pName = "";
                            for (Class<?> p : ps) {
                                pName = pName + p + "_";
                            }
                            index = index + meth.getName() + ":" + pName + "-";

                        }
                    }
                }
//                if (serviceBean instanceof IJobHandler){
//                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
//                    IJobHandler handler = (IJobHandler) serviceBean;
//                    if (loadJobHandler(name) != null) {
//                        throw new RuntimeException("xxl-job jobhandler naming conflicts.");
//                    }
//                    registJobHandler(name, handler);
//                }
            }
        }
        return index;
    }
}
