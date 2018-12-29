package com.kangcenet.tms.core.thread;

import com.kangcenet.tms.core.biz.model.HandleCallbackParam;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.executor.JobExecutor;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.log.JobFileAppender;
import com.kangcenet.tms.core.log.JobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JobThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(JobThread.class);
    private String jobId;
    private IJobHandler handler;
    private volatile LinkedBlockingQueue<TriggerParam> triggerQueue;
    private Set<Integer> triggerLogIdSet;        // avoid repeat trigger for the same TRIGGER_LOG_ID

    private volatile boolean toStop = false;
    private String stopReason;


    private boolean running = false;    // if running job
    private int idleTimes = 0;            // idel times

    public JobThread(String jobId, IJobHandler handler) {
        this.jobId = jobId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
        this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Integer>());
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
        if (triggerLogIdSet.contains(triggerParam.getLogId())) {
            logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
            return new Return<String>(Return.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
        }
        triggerLogIdSet.add(triggerParam.getLogId());
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
            idleTimes++;

            TriggerParam triggerParam = null;
            Return executeResult = null;

            try {
                // to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
                try {
                    triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException:" + e.getMessage() + "->" + e);
                }
                if (triggerParam != null) {
                    running = true;
                    idleTimes = 0;
                    triggerLogIdSet.remove(triggerParam.getLogId());

                    // log filename, like "logPath/yyyy-MM-dd/9999.log"
                    String logFileName = JobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
                    JobFileAppender.contextHolder.set(logFileName);
//                    ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));

// execute
                    JobLogger.log("<br>----------- job execute start -----------<br>----------- Param:" + triggerParam.getExecutorParams());

                    // execute
//                    if (triggerParam.getExecutorTimeout() > 0)
                    System.err.println("executeResult triggerParam:" + triggerParam.getJobId() + "->" + triggerParam + ":" + triggerParam.getCommand());
                    executeResult = handler.execute(triggerParam);
                    if (executeResult == null) {
                        executeResult = IJobHandler.FAIL;
                    }
                    logger.info("executeResult:{}", executeResult.toString());
                    JobLogger.log("<br>----------- job execute end(finish) -----------<br>----------- ReturnT:" + executeResult);
                } else {
                    if (idleTimes > 30) {
                        JobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
                    }
                }
            } catch (Throwable e) {
                if (toStop) {
                    JobLogger.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
                }
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String errorMsg = stringWriter.toString();
                executeResult = new Return<String>(Return.FAIL_CODE, errorMsg);
                JobLogger.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- xxl-job job execute end(error) -----------");
            } finally {
//                System.err.println("finally:" + executeResult);
                if (triggerParam != null) {
                    if (!toStop) {
                        // common
                        logger.error("id:{}", triggerParam.getLogId());
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
                    } else {
                        // is killed
                        Return<String> stopResult = new Return<String>(Return.FAIL_CODE, stopReason + " [job running，killed]");
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
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

        // callback trigger request in queue
        while (triggerQueue != null && triggerQueue.size() > 0) {
            TriggerParam triggerParam = triggerQueue.poll();
            if (triggerParam != null) {
                // is killed
                Return<String> stopResult = new Return<String>(Return.FAIL_CODE, stopReason + " [job not executed, in the job queue, killed.]");
                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
            }
        }

        // destroy
        try {
            handler.destroy();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
//            logger.error(e.getMessage(), e);
        }
        logger.info(">>>>>>>>>>> job JobThread stoped, hashCode:{}", Thread.currentThread());
    }
}
