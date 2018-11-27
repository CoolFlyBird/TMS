package com.kangcenet.tms.core.thread;

import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.handler.IJobHandler;

import java.util.concurrent.LinkedBlockingQueue;

public class JobThread extends Thread {
    private int jobId;
    private IJobHandler handler;
    private LinkedBlockingQueue<TriggerParam> triggerQueue;

    private volatile boolean toStop = false;
    private String stopReason;

    private boolean running = false;

    public JobThread(int jobId, IJobHandler handler) {
        this.jobId = jobId;
        this.handler = handler;

        this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
    }

    public IJobHandler getHandler() {
        return handler;
    }

    /**
     * new trigger to queue
     *
     * @param triggerParam
     * @return
     */
    public Return<String> pushTriggerQueue(TriggerParam triggerParam) {
        // avoid repeat
//        if (triggerLogIdSet.contains(triggerParam.getLogId())) {
//            logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
//            return new Return<String>(Return.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
//        }
//        triggerLogIdSet.add(triggerParam.getLogId());
        triggerQueue.add(triggerParam);
        return Return.SUCCESS;
    }

    /**
     * kill job thread
     *
     * @param stopReason
     */
    public void toStop(String stopReason) {
        /**
         * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
         * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
         * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
         */
        this.toStop = true;
        this.stopReason = stopReason;
    }

    @Override
    public void run() {
        try {
            handler.init();
        } catch (Throwable e) {

        }

        while (!toStop){
            running = false;
        }
    }
}
