package com.kangcenet.tms.admin.core.jobbean;

import com.kangcenet.tms.admin.core.thread.JobTriggerPoolHelper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ExecJobBean extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobKey jobKey = jobExecutionContext.getTrigger().getJobKey();
        Integer jobId = Integer.valueOf(jobKey.getName());
        JobTriggerPoolHelper.trigger(jobId);
    }
}
