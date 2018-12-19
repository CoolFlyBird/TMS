package com.kangcenet.tms.admin.core.model;

public class JobInfo {
    private String id;                // 主键ID	    (JobKey.name)
    private String jobGroup;        // 执行器主键ID	(JobKey.group)
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

    private String author;        // 负责人
    private String alarmEmail = "1239985370@qq.com";    // 报警邮件

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
    }
}
