package com.kangcenet.tms.core.biz.model;

import com.jcraft.jsch.Session;

import java.io.Serializable;

public class TriggerParam implements Serializable {
    private int id;
    private int jobId;

    private Session session;
    // execute info
    private String executorHandler;

    //执行接口的地址
    private String address = "";
    //执行接口：api 或者 脚本命令
    private String command = "";

    //脚本参数
    //linux 账号
    private String user = "";
    //linux 密码
    private String pwd = "";
    //linux shell 端口
    private int port = 0;
    //linux 秘钥登录
    private String privateKey = "";
    //linux 秘钥短语
    private String passphrase = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
