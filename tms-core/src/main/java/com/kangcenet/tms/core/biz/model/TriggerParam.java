package com.kangcenet.tms.core.biz.model;

import java.io.Serializable;

public class TriggerParam implements Serializable {
    private int jobId;
    private String executorHandler;
    private String executorParams;

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

    public String getExecutorParams() {
        return executorParams;
    }

    public void setExecutorParams(String executorParams) {
        this.executorParams = executorParams;
    }
}
