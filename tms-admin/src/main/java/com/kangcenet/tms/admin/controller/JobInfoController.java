package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.util.ShellExecUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/job")
public class JobInfoController {

    @ResponseBody
    @RequestMapping("/test")
    public Return<String> test(@RequestParam Map<String, String> params) {
        return new Return("test");
    }

    @ResponseBody
    @RequestMapping("/trigger")
    public Return<String> addJob(@RequestParam Map<String, String> params) {
        String address = params.get("address");
        Return<String> result = null;
        String type = params.get("type");
        if ("api".equals(type)) {

        } else if ("command".equals(type)) {
            String user = params.get("user");
            String pwd = params.get("password");
            int port = 0;
            try {
                port = Integer.parseInt(params.get("port"));
            } catch (Exception e) {
            }
            String privateKey = params.get("privateKey");
            String passphrase = params.get("passphrase");
            String command = params.get("command");
            try {
                String execCmdResult = ShellExecUtil.sshExecCmd(address, user, pwd, port, privateKey, passphrase, command);
                result = new Return(execCmdResult);
            } catch (Exception e) {
                result = new Return(e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }
}
