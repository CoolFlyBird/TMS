package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.conf.JobAdminConfig;
import com.kangcenet.tms.admin.core.model.JobGroup;
import com.kangcenet.tms.admin.core.model.User;
import com.kangcenet.tms.admin.core.util.MD5Util;
import com.kangcenet.tms.admin.core.util.TokenUtil;
import com.kangcenet.tms.admin.dao.JobGroupDao;
import com.kangcenet.tms.admin.dao.UserDao;
import com.kangcenet.tms.core.biz.model.Return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private long TIME_EXPIRATION = 10 * 24 * 60 * 60 * 1000;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JobGroupDao jobGroupDao;


    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Return login(@RequestParam Map<String, String> params) {
        logger.error("params:{}", params);
        String username = params.get("username");
        String password = params.get("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new Return(Return.FAIL.getCode(), "username or password is null");
        }
        String md5Password = MD5Util.MD5Encode(password, "utf-8");
        HashMap map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", md5Password);
        User user = userDao.select(map);
        logger.error("username:{},md5Password:{}", username, md5Password);
        if (user == null) {
            logger.error("username:{},password:{}|{},{}", JobAdminConfig.USER, JobAdminConfig.PASSWORD, username, password);
            if (username.equals(JobAdminConfig.USER)
                    && password.equals(JobAdminConfig.PASSWORD)) {
            } else {
                return new Return(Return.FAIL_CODE, "用户名或密码不正确");
            }
        }
        HashMap mapToken = new HashMap<String, String>();
        String token = TokenUtil.genToken(20);
        mapToken.put("username", username);
        mapToken.put("token", token);
        mapToken.put("expiration", "" + (System.currentTimeMillis() + TIME_EXPIRATION));
        int b = userDao.setToken(mapToken);
        if (b > 0) {
            return new Return(token);
        }
        return new Return(Return.FAIL_CODE, "登录失败");
    }

    @ResponseBody
    @RequestMapping("/create")
    public Return create(@RequestHeader(value = "Authorization", required = false) String auth, @RequestParam Map<String, String> params) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (!admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能创建账号！");
        }
        String userName = params.get("user");
        String password = params.get("password");
        String email = params.get("email");
        String role = params.get("appName");
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return new Return(Return.FAIL_CODE, "user or password is null");
        }
        if (StringUtils.isEmpty(role)) {
            return new Return(Return.FAIL_CODE, "appName is null");
        }
        JobGroup jobGroup = jobGroupDao.select(role);
        if (jobGroup == null) {
            return new Return(Return.FAIL_CODE, "该项目不存在");
        }
        String md5Password = MD5Util.MD5Encode(password, "utf-8");
        User user = new User();
        user.setUsername(userName);
        user.setPassword(md5Password);
        user.setEmail(email);
        user.setRole(role);
        int a = userDao.create(user);
        if (a == 1) {
            return new Return(Return.SUCCESS_CODE, "success");
        }
        return new Return(Return.FAIL_CODE, "fail");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Return delete(@RequestHeader(value = "Authorization", required = false) String auth, @RequestParam Map<String, String> params) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (!admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能删除账号！");
        }
        String userName = params.get("user");
        if (StringUtils.isEmpty(userName)) {
            return new Return(Return.FAIL_CODE, "user or password is null");
        }
        int a = userDao.delete(userName);
        if (a == 1) {
            return new Return(Return.SUCCESS_CODE, "success");
        }
        return new Return(Return.FAIL_CODE, "fail");
    }

    @ResponseBody
    @RequestMapping("/users")
    public Return pageList(@RequestHeader(value = "Authorization", required = false) String auth) {
        User admin = userDao.loadUserInfo(auth);
        if (admin == null) {
            return new Return(Return.UN_LOGIN, "请先登录！");
        } else if (!admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能查阅账号！");
        }
        List<User> users = userDao.pageList();
        User user = null;
        for (User u : users) {
            if (u.checkAdmin()) {
                user = u;
            }
        }
        if (user != null) {
            users.remove(user);
        }
        return new Return(users);
    }
}
