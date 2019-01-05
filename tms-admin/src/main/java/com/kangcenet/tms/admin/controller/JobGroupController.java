package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.conf.JobAdminConfig;
import com.kangcenet.tms.admin.core.model.JobGroup;
import com.kangcenet.tms.admin.core.model.User;
import com.kangcenet.tms.admin.dao.JobGroupDao;
import com.kangcenet.tms.admin.dao.UserDao;
import com.kangcenet.tms.core.biz.model.Return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {
    private static Logger logger = LoggerFactory.getLogger(JobGroupController.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private JobGroupDao jobGroupDao;

    @ResponseBody
    @RequestMapping("/pageList")
    public Return pageList(@RequestHeader(value = "Authorization", required = false) String auth) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null || !admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能查看项目！");
        }
        List<JobGroup> jobGroupList = jobGroupDao.pageList();
        return new Return(jobGroupList);
    }

    @ResponseBody
    @RequestMapping("/select")
    public Return select(@RequestHeader(value = "Authorization", required = false) String auth, @RequestParam Map<String, String> params) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null || !admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能查看项目！");
        }
        String appName = params.get("appName");
        if (StringUtils.isEmpty(appName)) {
            return new Return(Return.FAIL_CODE, "appName is null");
        }
        JobGroup jobGroup = jobGroupDao.select(appName);
        return new Return(jobGroup);
    }

    @ResponseBody
    @RequestMapping("/create")
    public Return create(@RequestHeader(value = "Authorization", required = false) String auth, @RequestParam Map<String, String> params) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null || !admin.checkAdmin()) {
            logger.error("admin:{},{}", JobAdminConfig.USER, admin.getRole());
            return new Return(Return.FAIL_CODE, "管理员才能创建项目！");
        }
        String appName = params.get("appName");
        String title = params.get("title");
        if (StringUtils.isEmpty(appName) || StringUtils.isEmpty(title)) {
            return new Return(Return.FAIL_CODE, "appName or title is null");
        }
        JobGroup jobGroup = new JobGroup();
        jobGroup.setTitle(title);
        jobGroup.setAppName(appName);
        int a = jobGroupDao.create(jobGroup);
        if (a == 1) {
            return new Return(Return.SUCCESS_CODE, "success");
        }
        return new Return(Return.FAIL_CODE, "fail");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Return delete(@RequestHeader(value = "Authorization", required = false) String auth, @RequestParam Map<String, String> params) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null || !admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能删除项目！");
        }
        String appName = params.get("appName");
        if (StringUtils.isEmpty(appName)) {
            return new Return(Return.FAIL_CODE, "user or password is null");
        }
        int a = jobGroupDao.delete(appName);
        if (a == 1) {
            return new Return(Return.SUCCESS_CODE, "success");
        }
        return new Return(Return.FAIL_CODE, "fail");
    }
}
