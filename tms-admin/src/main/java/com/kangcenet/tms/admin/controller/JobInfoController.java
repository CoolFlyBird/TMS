package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.User;
import com.kangcenet.tms.admin.dao.UserDao;
import com.kangcenet.tms.admin.service.JobService;
import com.kangcenet.tms.core.biz.model.Return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/job")
public class JobInfoController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private JobService jobService;

    @ResponseBody
    @RequestMapping("/test")
    public Return<String> test(@RequestParam Map<String, String> params) {
        return new Return("test");
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Return pageList(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(required = false, defaultValue = "0") int start,
            @RequestParam(required = false, defaultValue = "10") int length,
            String jobDesc, String executorHandler) {
        User user = userDao.loadUserInfo(auth);
        if (user == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (user.getRole() == null) {
            return new Return(Return.FAIL_CODE, "该账号未绑定项目！");
        }
        Map<String, Object> map = jobService.pageList(start, length, user.getRole(), jobDesc, executorHandler);
        return new Return(map);
    }


    @ResponseBody
    @RequestMapping("/add")
    public Return<String> addJob(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam Map<String, String> params) {
        User user = userDao.loadUserInfo(auth);
        if (user == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (user.getRole() == null) {
            return new Return(Return.FAIL_CODE, "该账号未绑定项目！");
        }
        JobInfo jobInfo = null;
        Return<String> result = null;
        try {
            jobInfo = parseJobInfo(params);
            jobInfo.setJobGroup(user.getRole());
            result = jobService.add(jobInfo);
        } catch (Exception e) {
            result = new Return(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/update")
    @ResponseBody
    public Return<String> update(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam Map<String, String> params) {
        User user = userDao.loadUserInfo(auth);
        if (user == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (user.getRole() == null) {
            return new Return(Return.FAIL_CODE, "该账号未绑定项目！");
        }
        JobInfo jobInfo = null;
        try {
            jobInfo = parseJobInfo(params);
            jobInfo.setJobGroup(user.getRole());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobService.update(jobInfo);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public Return<String> remove(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam Map<String, String> params) {
        User user = userDao.loadUserInfo(auth);
        if (user == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (user.getRole() == null) {
            return new Return(Return.FAIL_CODE, "该账号未绑定项目！");
        }
        String id = params.get("id");
        return jobService.remove(user.getRole(), id);
    }

    @RequestMapping("/pause")
    @ResponseBody
    public Return<String> pause(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam Map<String, String> params) {
        User user = userDao.loadUserInfo(auth);
        if (user == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (user.getRole() == null) {
            return new Return(Return.FAIL_CODE, "该账号未绑定项目！");
        }
        String id = params.get("id");
        return jobService.pause(user.getRole(), id);
    }

    @RequestMapping("/resume")
    @ResponseBody
    public Return<String> resume(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam Map<String, String> params) {
        User user = userDao.loadUserInfo(auth);
        if (user == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (user.getRole() == null) {
            return new Return(Return.FAIL_CODE, "该账号未绑定项目！");
        }
        String id = params.get("id");
        return jobService.resume(user.getRole(), id);
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
        String sendMail = params.get("sendMail");

        jobInfo.setId(id);
        jobInfo.setJobDesc(desc);
        jobInfo.setExecutorHandler(handler);
        jobInfo.setJobCron(cron);
        jobInfo.setAddress(address);
        jobInfo.setCommand(command);
        jobInfo.setSendMail(sendMail);
        return jobInfo;
    }
}
