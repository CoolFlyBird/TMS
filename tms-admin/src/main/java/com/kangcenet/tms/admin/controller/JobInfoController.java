package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.jobbean.ExecJobBean;
import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.admin.service.JobService;
import com.kangcenet.tms.core.biz.model.Return;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/job")
public class JobInfoController {

    @Resource
    private JobService xxlJobService;

    @ResponseBody
    @RequestMapping("/test")
    public Return<String> test(@RequestParam Map<String, String> params) {
        return new Return("test");
    }

    @ResponseBody
    @RequestMapping("/add")
    public Return<String> addJob(@RequestParam Map<String, String> params) {
        JobInfo jobInfo = null;
        Return<String> result = null;
        try {
            jobInfo = parseJobInfo(params);
            jobInfo.setId(1005);
            jobInfo.setJobGroup(2005);
            result = xxlJobService.add(jobInfo);
        } catch (Exception e) {
            result = new Return<String>(e.getMessage());
            e.printStackTrace();
        }

        Scheduler scheduler = JobScheduler.scheduler;
        JobDetail job = JobBuilder.newJob(ExecJobBean.class).withIdentity("myJob3").build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger3", "group3").startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(40).withRepeatCount(10)).build();
        try {
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JobInfo parseJobInfo(Map<String, String> params) throws Exception {
        JobInfo jobInfo = new JobInfo();
        //任务描述
        String desc = params.get("desc");
        //脚本执行器，目前置入的有 apiHandler和shellHandler
        String handler = params.get("handler");
        //时间参数
        String cron = params.get("cron");
        System.err.println("cron:" + cron);
        //执行机器的地址
        String address = params.get("address");
        //执行接口
        String command = params.get("command");//api/脚本命令

        //脚本参数
        String user = params.get("user");//linux 账号
        String pwd = params.get("password");//linux 密码
        String p = params.get("port");//linux shell 端口
        int port = 0;
        if (!StringUtils.isEmpty(p)) {
            port = Integer.parseInt(p);
        }
        String privateKey = params.get("privateKey");//linux 秘钥登录
        String passphrase = params.get("passphrase");//linux 秘钥短语
        jobInfo.setJobDesc(desc);
        jobInfo.setExecutorHandler(handler);
        jobInfo.setJobCron(cron);
        jobInfo.setAddress(address);
        jobInfo.setCommand(command);
        jobInfo.setUser(user);
        jobInfo.setPwd(pwd);
        jobInfo.setPort(port);
        jobInfo.setPrivateKey(privateKey);
        jobInfo.setPassphrase(passphrase);
        return jobInfo;
    }
}
