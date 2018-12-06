package com.kangcenet.tms.admin.core.model;

public class JobInfo {
    private int id;				// 主键ID	    (JobKey.name)
    private int jobGroup;		// 执行器主键ID	(JobKey.group)
    //时间参数
    private String jobCron = "";
    //任务描述
    private String jobDesc = "";
    //脚本执行器，目前置入的有 apiHandler和shellHandler
    private String executorHandler = "";
    //执行机器的地址
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

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getJobCron() {
        return jobCron;
    }

    public void setJobCron(String jobCron) {
        this.jobCron = jobCron;
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

    public int getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(int jobGroup) {
        this.jobGroup = jobGroup;
    }
}
