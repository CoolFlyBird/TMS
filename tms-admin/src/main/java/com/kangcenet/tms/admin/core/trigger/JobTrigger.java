package com.kangcenet.tms.admin.core.trigger;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.JobLog;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.admin.core.thread.JobFailMonitorHelper;
import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;

import java.util.Date;

public class JobTrigger {
    public static void trigger(String jobId) {
        JobInfo jobInfo = JobScheduler.jobInfoDao.loadById(jobId);
        if (jobInfo == null) {
            return;
        }
//        XxlJobGroup group = XxlJobDynamicScheduler.xxlJobGroupDao.load(jobInfo.getJobGroup());
        processTrigger(jobInfo);
    }

    private static void processTrigger(JobInfo jobInfo) {
        // 1、save log-id
        JobLog jobLog = new JobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        JobScheduler.jobLogDao.save(jobLog);

//         2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setAddress(jobInfo.getAddress());
        triggerParam.setCommand(jobInfo.getCommand());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        // 3、init address
//        String address = null;
        // 4、trigger remote executor
//        ReturnT<String> triggerResult = null;
        // 5、collection trigger info
//        StringBuffer triggerMsgSb = new StringBuffer();
        // 6、save log trigger-info

        Return<String> triggerResult = runExecutor(triggerParam);

        jobLog.setExecutorAddress(jobInfo.getAddress());
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getCommand());
        jobLog.setTriggerCode(triggerResult.getCode());
//        jobLog.setTriggerMsg(triggerMsgSb.toString());
        JobScheduler.jobLogDao.updateTriggerInfo(jobLog);

        // 7、monitor trigger
        JobFailMonitorHelper.monitor(jobLog.getId());
    }

    private static Return<String> runExecutor(TriggerParam jobInfo) {
//        ExecutorBizImpl
//        ExecutorBiz executorBiz = JobScheduler.getExecutorBiz("执行器的地址，之后可以拓展为分布式，目前执行器和 admin 放在一起");
        ExecutorBiz executorBiz = JobScheduler.getExecutorBiz("address");
        return executorBiz.run(jobInfo);
    }
}
