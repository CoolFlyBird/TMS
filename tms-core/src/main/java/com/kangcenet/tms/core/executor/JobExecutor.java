package com.kangcenet.tms.core.executor;

import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.handler.annotation.JobHandler;
import com.kangcenet.tms.core.thread.JobThread;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobExecutor implements ApplicationContextAware {
    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        start();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void start(){
        System.err.println("start()");
        initJobHandlerRepository(applicationContext);
    }

    public void destroy() {
        // destory JobThreadRepository
        if (JobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : JobThreadRepository.entrySet()) {
                removeJobThread(item.getKey(), "web container destroy and kill the job.");
            }
            JobThreadRepository.clear();
        }

//        // destory executor-server
//        stopExecutorServer();
//        // destory JobLogFileCleanThread
//        JobLogFileCleanThread.getInstance().toStop();
    }

    // ---------------------- job handler repository ----------------------
    private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();

    public static IJobHandler registerJobHandler(String name, IJobHandler jobHandler) {
//        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    private static void initJobHandlerRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }
        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);
        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler) {
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    System.err.println("register:" + name);
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) == null) {
                        registerJobHandler(name, handler);
//                        throw new RuntimeException("job handler naming conflicts.");
                    }
                }
            }
        }
    }


    // ---------------------- job thread repository ----------------------
    private static ConcurrentHashMap<Integer, JobThread> JobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();

    public static JobThread registerJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
//        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});
        JobThread oldJobThread = JobThreadRepository.put(jobId, newJobThread);    // putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
        return newJobThread;
    }

    public static void removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = JobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }

    public static JobThread loadJobThread(int jobId) {
        JobThread jobThread = JobThreadRepository.get(jobId);
        return jobThread;
    }

}
