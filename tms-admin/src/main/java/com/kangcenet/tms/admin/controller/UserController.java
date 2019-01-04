package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.model.JobGroup;
import com.kangcenet.tms.admin.core.model.User;
import com.kangcenet.tms.admin.core.util.MD5Util;
import com.kangcenet.tms.admin.core.util.TokenUtil;
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

    @ResponseBody
    @RequestMapping("/login")
    public Return login(@RequestParam Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new Return(Return.FAIL.getCode(), "user or password is null");
        }
        String md5Password = MD5Util.MD5Encode(password, "utf-8");
        HashMap map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", md5Password);
        User user = userDao.select(map);
        if (user == null) {
            return new Return(Return.FAIL_CODE, "用户名或密码不正确");
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
        logger.error("a:{}", MD5Util.MD5Encode(params.get("password"), "utf-8"));
        User admin = userDao.loadUserInfo(auth);
        if (admin == null || !admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能创建账号！");
        }
        String userName = params.get("user");
        String password = params.get("password");
        String email = params.get("email");
        String role = params.get("role");
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return new Return(Return.FAIL_CODE, "user or password is null");
        }
        if (StringUtils.isEmpty(role)) {
            return new Return(Return.FAIL_CODE, "role is null");
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
        if (admin == null || !admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能删除账号！");
        }
        String userName = params.get("user");
        if (StringUtils.isEmpty(userName)) {
            return new Return(Return.FAIL_CODE, "user or password is null");
        }
        int a = userDao.delete(userName);
        logger.error("user.create{}", a);
        if (a == 1) {
            return new Return(Return.SUCCESS_CODE, "success");
        }
        return new Return(Return.FAIL_CODE, "fail");
    }

    @ResponseBody
    @RequestMapping("/users")
    public Return pageList(@RequestHeader(value = "Authorization", required = false) String auth) {
        User admin = userDao.loadUserInfo(auth);
        logger.error("loadUserInfo {}", admin);
        if (admin == null || !admin.checkAdmin()) {
            return new Return(Return.FAIL_CODE, "管理员才能查阅账号！");
        }
        List<User> users = userDao.pageList();
        return new Return(users);
    }
}
