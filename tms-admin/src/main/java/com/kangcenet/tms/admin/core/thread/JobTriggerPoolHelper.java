package com.kangcenet.tms.admin.core.thread;

import com.kangcenet.tms.admin.core.trigger.JobTrigger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobTriggerPoolHelper {
    private ThreadPoolExecutor triggerPool = new ThreadPoolExecutor(
            32,
            256,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));

    public void addTrigger(final int jobId) {
        triggerPool.execute(new Runnable() {
            public void run() {
                JobTrigger.trigger(jobId);
            }
        });
    }

    public void stop() {
        triggerPool.shutdownNow();
    }

    private static JobTriggerPoolHelper helper = new JobTriggerPoolHelper();

    /**
     * @param jobId
     */
    public static void trigger(int jobId) {
        helper.addTrigger(jobId);
    }

    public static void toStop() {
        helper.stop();
    }
}
