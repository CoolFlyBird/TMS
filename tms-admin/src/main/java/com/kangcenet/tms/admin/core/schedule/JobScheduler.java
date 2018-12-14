package com.kangcenet.tms.admin.core.schedule;

import com.kangcenet.tms.admin.core.jobbean.ExecJobBean;
import com.kangcenet.tms.admin.core.thread.JobTriggerPoolHelper;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.impl.ExecutorBizImpl;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

public class JobScheduler implements ApplicationContextAware {
    // scheduler
    public static Scheduler scheduler;

    public void setScheduler(Scheduler scheduler) {
        JobScheduler.scheduler = scheduler;
    }

    // accessToken
    private static String accessToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    //    // dao
//    public static XxlJobLogDao xxlJobLogDao;
    public static JobInfoDao jobInfoDao;
//    public static XxlJobRegistryDao xxlJobRegistryDao;
//    public static XxlJobGroupDao xxlJobGroupDao;
//    public static AdminBiz adminBiz;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        XxlJobDynamicScheduler.xxlJobLogDao = applicationContext.getBean(XxlJobLogDao.class);
        JobScheduler.jobInfoDao = applicationContext.getBean(JobInfoDao.class);
//        XxlJobDynamicScheduler.xxlJobRegistryDao = applicationContext.getBean(XxlJobRegistryDao.class);
//        XxlJobDynamicScheduler.xxlJobGroupDao = applicationContext.getBean(XxlJobGroupDao.class);
//        XxlJobDynamicScheduler.adminBiz = applicationContext.getBean(AdminBiz.class);
    }

    public void init() throws Exception {
//        // admin registry monitor run
//        JobRegistryMonitorHelper.getInstance().start();
//        // admin monitor run
//        JobFailMonitorHelper.getInstance().start();
//        // admin-server(spring-mvc)
//        NetComServerFactory.putService(AdminBiz.class, XxlJobDynamicScheduler.adminBiz);
//        NetComServerFactory.setAccessToken(accessToken);
        // valid
        Assert.notNull(scheduler, "quartz scheduler is null");
    }


    public void destroy() {
        // admin trigger pool stop
        JobTriggerPoolHelper.toStop();
//        // admin registry stop
//        JobRegistryMonitorHelper.getInstance().toStop();
//        // admin monitor stop
//        JobFailMonitorHelper.getInstance().toStop();
    }


    /**
     * check if exists
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return scheduler.checkExists(triggerKey);
    }


    /**
     * pause
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean pauseJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            scheduler.pauseTrigger(triggerKey);
            result = true;
//            logger.info(">>>>>>>>>>> pauseJob success, triggerKey:{}", triggerKey);
        } else {
//            logger.info(">>>>>>>>>>> pauseJob fail, triggerKey:{}", triggerKey);
        }
        return result;
    }

    /**
     * resume
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean resumeJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            scheduler.resumeTrigger(triggerKey);
            result = true;
//            logger.info(">>>>>>>>>>> resumeJob success, triggerKey:{}", triggerKey);
        } else {
//            logger.info(">>>>>>>>>>> resumeJob fail, triggerKey:{}", triggerKey);
        }
        return result;
    }

    /**
     * addJob
     *
     * @param jobName
     * @param jobGroup
     * @param cronExpression
     * @return
     * @throws SchedulerException
     */
    public static boolean addJob(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);
        // TriggerKey valid if_exists
        if (checkExists(jobName, jobGroup)) {
//            logger.info(">>>>>>>>> addJob fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
            return false;
        }
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
        Class<? extends Job> jobClass = ExecJobBean.class;
        // 使用quartz框架，定时触发
        // JobTriggerPoolHelper.trigger(jobId)
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
        System.err.println("addJob success-->jobDetail:" + jobDetail + " cronTrigger:" + cronTrigger);
        return true;
    }


    // ---------------------- executor-client ----------------------
    private static ConcurrentHashMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();

    public static ExecutorBiz getExecutorBiz(String address) {
//        // valid
//        if (address==null || address.trim().length()==0) {
//            return null;
//        }
//        // load-cache
//        address = address.trim();
//        ExecutorBiz executorBiz = executorBizRepository.get(address);
//        if (executorBiz != null) {
//            return executorBiz;
//        }
//        // set-cache
////        executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, address, accessToken).getObject();
//        executorBiz = new ExecutorBizImpl();
//        executorBizRepository.put(address, executorBiz);
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz == null) {
            executorBiz = new ExecutorBizImpl();
            executorBizRepository.put(address, executorBiz);
        }
        return executorBiz;
    }

}
