package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.service.JobService;
import com.kangcenet.tms.core.biz.model.Return;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/job")
public class JobInfoController {
    @Resource
    private JobService jobService;

    @ResponseBody
    @RequestMapping("/test")
    public Return<String> test(@RequestParam Map<String, String> params) {
        return new Return("test");
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String jobGroup, String jobDesc, String executorHandler, String filterTime) {
        return jobService.pageList(start, length, jobGroup, jobDesc, executorHandler, filterTime);
    }


    @ResponseBody
    @RequestMapping("/add")
    public Return<String> addJob(@RequestParam Map<String, String> params) {
        JobInfo jobInfo = null;
        Return<String> result = null;
        try {
            jobInfo = parseJobInfo(params);
            result = jobService.add(jobInfo);
        } catch (Exception e) {
            result = new Return<String>(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/update")
    @ResponseBody
    public Return<String> update(@RequestParam Map<String, String> params) {
        JobInfo jobInfo = null;
        try {
            jobInfo = parseJobInfo(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobService.update(jobInfo);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public Return<String> remove(@RequestParam Map<String, String> params) {
        String id = params.get("id");
        return jobService.remove(id);
    }

    @RequestMapping("/pause")
    @ResponseBody
    public Return<String> pause(@RequestParam Map<String, String> params) {
        String id = params.get("id");
        return jobService.pause(id);
    }

    @RequestMapping("/resume")
    @ResponseBody
    public Return<String> resume(@RequestParam Map<String, String> params) {
        String id = params.get("id");
        return jobService.resume(id);
    }

    @RequestMapping("/getJobsByGroup")
    @ResponseBody
    public Return<List<JobInfo>> getJobsByGroup(@RequestParam String jobGroup) {
        return jobService.getJobsByGroup(jobGroup);
    }

    @RequestMapping("/dashboard")
    @ResponseBody
    public Return<String> dashboardInfo() {
        Map<String, Object> dashboardMap = jobService.dashboardInfo();
        return new Return(dashboardMap);
    }

    @RequestMapping("/chartInfo")
    @ResponseBody
    public Return<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        Return<Map<String, Object>> chartInfo = jobService.chartInfo(startDate, endDate);
        return chartInfo;
    }


    private JobInfo parseJobInfo(Map<String, String> params) throws Exception {
        JobInfo jobInfo = new JobInfo();
        //任务描述
        String desc = params.get("desc");
        //脚本执行器，目前置入的有 apiHandler和shellHandler
        String handler = params.get("handler");
        //时间参数
        String cron = params.get("cron");
        //执行机器的地址
        String address = params.get("address");
        //执行接口
        String command = params.get("command");//api/脚本命令

        String id = params.get("id");
        String jobGroup = params.get("jobGroup");

        jobInfo.setId(id);
        jobInfo.setJobGroup(jobGroup);
        jobInfo.setJobDesc(desc);
        jobInfo.setExecutorHandler(handler);
        jobInfo.setJobCron(cron);
        jobInfo.setAddress(address);
        jobInfo.setCommand(command);
        return jobInfo;
    }
}
