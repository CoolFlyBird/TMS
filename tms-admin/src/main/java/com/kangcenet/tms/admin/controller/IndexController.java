package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.jobbean.ExecJobBean;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import org.quartz.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

    @ResponseBody
    @RequestMapping("/")
    public String index(@RequestParam Map<String, String> params) throws SchedulerException {
        System.err.println("index()");
        return "index";
    }
}
