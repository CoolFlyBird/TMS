package com.kangcenet.tms.core.biz.impl;

import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.executor.JobExecutor;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.thread.JobThread;

public class ExecutorBizImpl implements ExecutorBiz {
    public Return<String> run(TriggerParam triggerParam) {
        // load old：jobHandler + jobThread
        JobThread jobThread = JobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;
        // new jobhandler
        IJobHandler newJobHandler = JobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
        // valid old jobThread
        if (jobThread != null && jobHandler != newJobHandler) {
            // change handler, need kill old thread
            removeOldReason = "change job handler ,and terminate the old job thread.";
            jobThread = null;
            jobHandler = null;
        }
        // valid handler
        if (jobHandler == null) {
            jobHandler = newJobHandler;
            if (jobHandler == null) {
                return new Return<String>(Return.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
            }
        }
        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = JobExecutor.registerJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }
        // push data to queue
        Return<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }
}
