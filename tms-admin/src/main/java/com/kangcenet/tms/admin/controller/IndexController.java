package com.kangcenet.tms.admin.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class IndexController implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ResponseBody
    @RequestMapping("/")
    public String index() {
// init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RequestMapping.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
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
        return "index";
    }
}
