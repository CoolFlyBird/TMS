package com.kangcenet.tms.core.thread;

import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.trigger.JobTrigger;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobTriggerPoolHelper {
    // ---------------------- trigger pool ----------------------
    private ThreadPoolExecutor triggerPool = new ThreadPoolExecutor(
            32,
            256,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));

    public void addTrigger(final int jobId, final int failRetryCount, final String executorShardingParam, final String executorParam) {
        triggerPool.execute(new Runnable() {
            @Override
            public void run() {

                // 1、save log-id
//                XxlJobLog jobLog = new XxlJobLog();
//                jobLog.setJobGroup(jobInfo.getJobGroup());
//                jobLog.setJobId(jobInfo.getId());
//                jobLog.setTriggerTime(new Date());
//                XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
//                logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());

                // 2、init trigger-param
                TriggerParam triggerParam = new TriggerParam();
                triggerParam.setJobId(jobInfo.getId());
                triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
                triggerParam.setExecutorParams(jobInfo.getExecutorParam());

                JobTrigger.trigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam);
            }
        });
    }

    public void stop() {
        //triggerPool.shutdown();
        triggerPool.shutdownNow();
        logger.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }

    // ---------------------- helper ----------------------

    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    /**
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param
     *                              <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param
     *                              not null: cover job param
     */
    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam) {
        helper.addTrigger(jobId, triggerType, failRetryCount, executorShardingParam, executorParam);
    }

    public static void toStop() {
        helper.stop();
    }

}
