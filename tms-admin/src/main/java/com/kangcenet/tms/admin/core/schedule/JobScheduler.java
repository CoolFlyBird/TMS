package com.kangcenet.tms.admin.core.schedule;

import com.kangcenet.tms.admin.core.jobbean.ExecJobBean;
import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.thread.JobFailMonitorHelper;
import com.kangcenet.tms.admin.core.thread.JobTriggerPoolHelper;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.admin.dao.JobLogDao;
import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.impl.ExecutorBizImpl;
import com.kangcenet.tms.core.thread.TriggerCallbackThread;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.HashSet;
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
    public static JobLogDao jobLogDao;
    public static JobInfoDao jobInfoDao;
//    public static XxlJobRegistryDao xxlJobRegistryDao;
//    public static XxlJobGroupDao xxlJobGroupDao;
//    public static AdminBiz adminBiz;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobScheduler.jobLogDao = applicationContext.getBean(JobLogDao.class);
        JobScheduler.jobInfoDao = applicationContext.getBean(JobInfoDao.class);
//        XxlJobDynamicScheduler.xxlJobRegistryDao = applicationContext.getBean(XxlJobRegistryDao.class);
//        XxlJobDynamicScheduler.xxlJobGroupDao = applicationContext.getBean(XxlJobGroupDao.class);
//        XxlJobDynamicScheduler.adminBiz = applicationContext.getBean(AdminBiz.class);
    }

    public void init() throws Exception {
//        // admin registry monitor run
//        JobRegistryMonitorHelper.getInstance().start();
//        // admin monitor run
        TriggerCallbackThread.getInstance().start();
        JobFailMonitorHelper.getInstance().start();
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
        JobFailMonitorHelper.getInstance().toStop();
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
     * unscheduleJob
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean removeJob(String jobName, String jobGroup) throws SchedulerException {
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        boolean result = false;
        if (checkExists(jobName, jobGroup)) {
            result = scheduler.unscheduleJob(triggerKey);
        }
        return true;
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


    /**
     * rescheduleJob
     *
     * @param jobGroup
     * @param jobName
     * @param cronExpression
     * @return
     * @throws SchedulerException
     */
    public static boolean rescheduleJob(String jobGroup, String jobName, String cronExpression) throws SchedulerException {
        // TriggerKey valid if_exists
        if (!checkExists(jobName, jobGroup)) {
//            logger.info(">>>>>>>>>>> rescheduleJob fail, job not exists, JobGroup:{}, JobName:{}", jobGroup, jobName);
            return false;
        }
        // TriggerKey : name + group
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (oldTrigger != null) {
            // avoid repeat
            String oldCron = oldTrigger.getCronExpression();
            if (oldCron.equals(cronExpression)) {
                return true;
            }
            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            oldTrigger = oldTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
            // rescheduleJob
            scheduler.rescheduleJob(triggerKey, oldTrigger);
        } else {
            // CronTrigger : TriggerKey + cronExpression
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            // JobDetail-JobDataMap fresh
            JobKey jobKey = new JobKey(jobName, jobGroup);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            /*JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.clear();
            jobDataMap.putAll(JacksonUtil.readValue(jobInfo.getJobData(), Map.class));*/
            // Trigger fresh
            HashSet<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(cronTrigger);
            scheduler.scheduleJob(jobDetail, triggerSet, true);
        }
//        logger.info(">>>>>>>>>>> resumeJob success, JobGroup:{}, JobName:{}", jobGroup, jobName);
        return true;
    }

    /**
     * fill job info
     *
     * @param jobInfo
     */
    public static void fillJobInfo(JobInfo jobInfo) {
        // TriggerKey : name + group
        String group = String.valueOf(jobInfo.getJobGroup());
        String name = String.valueOf(jobInfo.getId());
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            // parse params
            if (trigger != null && trigger instanceof CronTriggerImpl) {
                String cronExpression = ((CronTriggerImpl) trigger).getCronExpression();
                jobInfo.setJobCron(cronExpression);
            }
            //JobKey jobKey = new JobKey(jobInfo.getJobName(), String.valueOf(jobInfo.getJobGroup()));
            //JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            //String jobClass = jobDetail.getJobClass().getName();
//            if (triggerState!=null) {
//                jobInfo.setJobStatus(triggerState.name());
//            }
        } catch (SchedulerException e) {
//            logger.error(e.getMessage(), e);
        }
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
