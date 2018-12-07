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
//        String id = params.get("id");
//        String groupId = params.get("groupId");
//        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("*/5 * * * * ?").withMisfireHandlingInstructionDoNothing();
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(id).withSchedule(cronScheduleBuilder).build();
//
//        JobKey jobKey = new JobKey(id, groupId);
//        Class<? extends Job> jobClass_ = ExecJobBean.class;
//        // 使用quartz框架，定时触发
//        // JobTriggerPoolHelper.trigger(jobId)
//
//        JobDetail jobDetail = JobBuilder.newJob(jobClass_).withIdentity(jobKey).build();
//        JobScheduler.scheduler.scheduleJob(jobDetail, cronTrigger);
//        System.err.println("addJob success-->jobDetail:" + jobDetail + " cronTrigger:" + cronTrigger);
        return "index";
    }
}
