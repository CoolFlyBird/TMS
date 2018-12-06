package com.kangcenet.tms.core.thread;

import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.handler.IJobHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

        while (!toStop) {
            running = false;
            TriggerParam triggerParam = null;
            Return<String> executeResult = null;

            try {
                // to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
                triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (triggerParam != null) {
                    running = true;
                    // log filename, like "logPath/yyyy-MM-dd/9999.log"
//                    String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
//                    XxlJobFileAppender.contextHolder.set(logFileName);
//                    ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));

                    // execute
//                    if (triggerParam.getExecutorTimeout() > 0)
                    executeResult = handler.execute(triggerParam.getCommand());
                    if (executeResult == null) {
                        executeResult = IJobHandler.FAIL;
                    }
                }
            } catch (Throwable e) {

            } finally {
                System.err.println("executeResult:" + executeResult);
                if (triggerParam != null) {
                    if (!toStop) {
                        // common
//                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
                    } else {
                        // is killed
//                        Return<String> stopResult = new Return<String>(Return.FAIL_CODE, stopReason + " [job running，killed]");
//                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
                    }

                }
            }
        }

        // callback trigger request in queue
        while (triggerQueue != null && triggerQueue.size() > 0) {
            TriggerParam triggerParam = triggerQueue.poll();
            if (triggerParam != null) {
                // is killed
                Return<String> stopResult = new Return<String>(Return.FAIL_CODE, stopReason + " [job not executed, in the job queue, killed.]");
                System.err.println("stopResult:" + stopResult);
//                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
            }
        }

        // destroy
        try {
            handler.destroy();
        } catch (Throwable e) {
//            logger.error(e.getMessage(), e);
        }
    }
}
