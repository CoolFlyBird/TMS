package com.kangcenet.tms.core.thread;

import com.kangcenet.tms.core.biz.AdminBiz;
import com.kangcenet.tms.core.biz.model.HandleCallbackParam;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.log.JobFileAppender;
import com.kangcenet.tms.core.log.JobLogger;
import com.kangcenet.tms.core.util.FileUtil;
import com.kangcenet.tms.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThread {
    @Resource
    public AdminBiz adminBiz;

    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static TriggerCallbackThread instance = new TriggerCallbackThread();

    public static TriggerCallbackThread getInstance() {
        return instance;
    }

    /**
     * job results callback queue
     */
    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<HandleCallbackParam>();

    public static void pushCallBack(HandleCallbackParam callback) {
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> job, push callback request, logId:{}", callback.getLogId());
    }

    /**
     * callback thread
     */
    private Thread triggerCallbackThread;
    private Thread triggerRetryCallbackThread;
    private volatile boolean toStop = false;

    public void start() {
        logger.error("TriggerCallbackThread");
        // valid
//        if (JobExecutor.getAdminBizList() == null) {
//            logger.warn(">>>>>>>>>>> xxl-job, executor callback config fail, adminAddresses is null.");
//            return;
//        }

        // callback
        triggerCallbackThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // normal callback
                while (!toStop) {
                    try {
                        HandleCallbackParam callback = getInstance().callBackQueue.take();
                        logger.info("callback take");
                        if (callback != null) {
                            logger.info("callback {}", callback.getExecuteResult());
                            // callback list param
                            List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                            int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
                            callbackParamList.add(callback);

                            // callback, will retry if error
                            if (callbackParamList != null && callbackParamList.size() > 0) {
                                doCallback(callbackParamList);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                // last callback
                try {
                    List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
                    int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
                    if (callbackParamList != null && callbackParamList.size() > 0) {
                        doCallback(callbackParamList);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                logger.info(">>>>>>>>>>> job, executor callback thread destory.");

            }
        });
        triggerCallbackThread.setDaemon(true);
        triggerCallbackThread.start();


        // retry
        triggerRetryCallbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        retryFailCallbackFile();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
//                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException e) {
                        logger.warn(">>>>>>>>>>> xxl-job, executor retry callback thread interrupted, error msg:{}", e.getMessage());
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, executor retry callback thread destory.");
            }
        });
        triggerRetryCallbackThread.setDaemon(true);
        triggerRetryCallbackThread.start();

    }

    public void toStop() {
        toStop = true;
        // stop callback, interrupt and wait
        triggerCallbackThread.interrupt();
        try {
            triggerCallbackThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        // stop retry, interrupt and wait
        triggerRetryCallbackThread.interrupt();
        try {
            triggerRetryCallbackThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * do callback, will retry if error
     *
     * @param callbackParamList
     */
    private void doCallback(List<HandleCallbackParam> callbackParamList) {
        boolean callbackRet = false;
        // callback, will retry if error
        try {
            logger.info("adminBiz {}", adminBiz);
            Return<String> callbackResult = adminBiz.callback(callbackParamList);
            logger.info("adminBiz {}", adminBiz);
            if (callbackResult != null && Return.SUCCESS_CODE == callbackResult.getCode()) {
                callbackLog(callbackParamList, "<br>----------- job callback finish.");
                callbackRet = true;
            } else {
                callbackLog(callbackParamList, "<br>----------- job callback fail, callbackResult:" + callbackResult);
            }
        } catch (Exception e) {
            callbackLog(callbackParamList, "<br>----------- job callback error, errorMsg:" + e.getMessage());
        }
        if (!callbackRet) {
            appendFailCallbackFile(callbackParamList);
        }
    }

    /**
     * callback log
     */
    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        for (HandleCallbackParam callbackParam : callbackParamList) {
            String logFileName = JobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
            JobFileAppender.contextHolder.set(logFileName);
            JobLogger.log(logContent);
        }
    }


    // ---------------------- fail-callback file ----------------------

    private static String failCallbackFileName = JobFileAppender.getLogPath().concat(File.separator).concat("job-callback").concat(".log");

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList) {
        // append file
        String content = JacksonUtil.writeValueAsString(callbackParamList);
        FileUtil.appendFileLine(failCallbackFileName, content);
    }

    private void retryFailCallbackFile() {

        // load and clear file
        List<String> fileLines = FileUtil.loadFileLines(failCallbackFileName);
        FileUtil.deleteFile(failCallbackFileName);

        // parse
        List<HandleCallbackParam> failCallbackParamList = new ArrayList<HandleCallbackParam>();
        if (fileLines != null && fileLines.size() > 0) {
            for (String line : fileLines) {
                List<HandleCallbackParam> failCallbackParamListTmp = JacksonUtil.readValue(line, List.class, HandleCallbackParam.class);
                if (failCallbackParamListTmp != null && failCallbackParamListTmp.size() > 0) {
                    failCallbackParamList.addAll(failCallbackParamListTmp);
                }
            }
        }

        // retry callback, 100 lines per page
        if (failCallbackParamList != null && failCallbackParamList.size() > 0) {
            int pagesize = 100;
            List<HandleCallbackParam> pageData = new ArrayList<HandleCallbackParam>();
            for (int i = 0; i < failCallbackParamList.size(); i++) {
                pageData.add(failCallbackParamList.get(i));
                if (i > 0 && i % pagesize == 0) {
                    doCallback(pageData);
                    pageData.clear();
                }
            }
            if (pageData.size() > 0) {
                doCallback(pageData);
            }
        }
    }

}
