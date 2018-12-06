package com.kangcenet.tms.admin.core.trigger;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.model.TriggerParam;

public class JobTrigger {
    public static void trigger(int jobId) {
        System.err.println("trigger" + jobId);
        JobInfo jobInfo = JobScheduler.jobInfoDao.loadById(jobId);
        if (jobInfo == null) {
            System.err.println("jobInfo:null");
            return;
        }
        System.err.println("jobInfo:" + jobInfo);

//        XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(jobInfo.getJobGroup());
        processTrigger(jobInfo);
    }

    private static void processTrigger(JobInfo jobInfo) {
        // 1、save log-id
//        XxlJobLog jobLog = new XxlJobLog();
//         2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setAddress(jobInfo.getAddress());
        triggerParam.setCommand(jobInfo.getCommand());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setUser(jobInfo.getUser());
        triggerParam.setPwd(jobInfo.getPwd());
        triggerParam.setPort(jobInfo.getPort());
        triggerParam.setPrivateKey(jobInfo.getPrivateKey());
        triggerParam.setPassphrase(jobInfo.getPassphrase());
        // 3、init address
//        String address = null;
        // 4、trigger remote executor
//        ReturnT<String> triggerResult = null;
        // 5、collection trigger info
//        StringBuffer triggerMsgSb = new StringBuffer();
        // 6、save log trigger-info
//        jobLog.setExecutorAddress(address);
        // 7、monitor trigger
//        JobFailMonitorHelper.monitor(jobLog.getId());
        runExecutor(triggerParam);
    }

    private static void runExecutor(TriggerParam jobInfo) {
//        ExecutorBizImpl
        ExecutorBiz executorBiz = JobScheduler.getExecutorBiz("执行器的地址，之后可以拓展为分布式，目前执行器和 admin 放在一起");
        executorBiz.run(jobInfo);
    }
}
