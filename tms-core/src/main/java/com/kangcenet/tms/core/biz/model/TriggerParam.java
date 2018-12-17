package com.kangcenet.tms.core.biz.model;

import com.jcraft.jsch.Session;

import java.io.Serializable;

public class TriggerParam implements Serializable {
    private String id;
    private String jobId;

    // execute info
    private String executorHandler;

    //执行接口的地址
    private String address = "";
    //执行接口：api 或者 脚本命令
    private String command = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
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

}
