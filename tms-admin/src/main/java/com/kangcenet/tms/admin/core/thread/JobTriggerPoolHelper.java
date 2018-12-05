package com.kangcenet.tms.admin.core.thread;

import com.kangcenet.tms.admin.core.trigger.JobTrigger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobTriggerPoolHelper {
    //    // ---------------------- trigger pool ----------------------
    private ThreadPoolExecutor triggerPool = new ThreadPoolExecutor(
            32,
            256,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));

    //
    public void addTrigger(final int jobId, final String executorParam) {
        triggerPool.execute(new Runnable() {
            public void run() {
                JobTrigger.trigger(jobId, executorParam);
            }
        });
    }

    public void stop() {
        //triggerPool.shutdown();
        triggerPool.shutdownNow();
//        logger.info(">>>>>>>>> xxl-job trigger thread pool shutdown success.");
    }

    //
//    // ---------------------- helper ----------------------
//
    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    /**
     * @param jobId
     * @param executorParam null: use job param
     *                      not null: cover job param
     */
    public static void trigger(int jobId, String executorParam) {
        helper.addTrigger(jobId, executorParam);
    }

    public static void toStop() {
        helper.stop();
    }

}
