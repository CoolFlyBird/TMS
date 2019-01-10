package com.kangcenet.tms.admin.core.thread;

import com.kangcenet.tms.admin.core.model.JobGroup;
import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.JobLog;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.admin.core.util.MailUtil;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.handler.IJobHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
public class JobFailMonitorHelper {
    private static Logger logger = LoggerFactory.getLogger(JobFailMonitorHelper.class);

    private static JobFailMonitorHelper instance = new JobFailMonitorHelper();

    public static JobFailMonitorHelper getInstance() {
        return instance;
    }

    // ---------------------- monitor ----------------------

    private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(0xfff8);

    private Thread monitorThread;
    private volatile boolean toStop = false;

    public void start() {
        monitorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // monitor
                while (!toStop) {
                    try {
                        List<Integer> jobLogIdList = new ArrayList<Integer>();
                        int drainToNum = JobFailMonitorHelper.instance.queue.drainTo(jobLogIdList);

                        if (CollectionUtils.isNotEmpty(jobLogIdList)) {
                            for (Integer jobLogId : jobLogIdList) {
                                if (jobLogId == null || jobLogId == 0) {
                                    continue;
                                }
                                JobLog log = JobScheduler.jobLogDao.load(null,jobLogId);
                                if (log == null) {
                                    continue;
                                }
                                if (IJobHandler.SUCCESS.getCode() == log.getTriggerCode() && log.getHandleCode() == 0) {
                                    // job running
                                    JobFailMonitorHelper.monitor(jobLogId);
                                    logger.debug(">>>>>>>>>>> job monitor, job running, JobLogId:{}", jobLogId);
                                } else if (IJobHandler.SUCCESS.getCode() == log.getHandleCode()) {
                                    // job success, pass
                                    logger.info(">>>>>>>>>>> job monitor, job success, JobLogId:{}", jobLogId);
                                } else /*if (IJobHandler.FAIL.getCode() == log.getTriggerCode()
										|| IJobHandler.FAIL.getCode() == log.getHandleCode()
//										|| IJobHandler.FAIL_RETRY.getCode() == log.getHandleCode() )*/
                                    if (IJobHandler.FAIL.getCode() == log.getTriggerCode()
                                            || IJobHandler.FAIL.getCode() == log.getHandleCode()) {
                                        // job fail,
                                        JobInfo info = JobScheduler.jobInfoDao.loadById(null,log.getJobId());
                                        JobScheduler.jobLogDao.updateTriggerInfo(log);

                                        // 1、fail retry
//									if (log.getExecutorFailRetryCount() > 0) {
//										JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount()-1), log.getExecutorShardingParam(), null);
//										String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_type_retry") +"<<<<<<<<<<< </span><br>";
//										log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
//										JobDynamicScheduler.jobLogDao.updateTriggerInfo(log);
//									}
                                        // 2、fail alarm
                                        failAlarm(info, log);
                                        logger.info(">>>>>>>>>>> job monitor, job fail, JobLogId:{}", jobLogId);
                                    }/* else {
									JobFailMonitorHelper.monitor(jobLogId);
									logger.info(">>>>>>>>>>> job monitor, job status unknown, JobLogId:{}", jobLogId);
								}*/
                            }
                        }
                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception e) {
                        logger.error("job monitor error:{}", e);
                    }
                }

                // monitor all clear
                List<Integer> jobLogIdList = new ArrayList<Integer>();
                int drainToNum = getInstance().queue.drainTo(jobLogIdList);
                if (jobLogIdList != null && jobLogIdList.size() > 0) {
                    for (Integer jobLogId : jobLogIdList) {
                        JobLog log = JobScheduler.jobLogDao.load(null,jobLogId);
                        if (Return.FAIL_CODE == log.getTriggerCode() || Return.FAIL_CODE == log.getHandleCode()) {
                            // job fail,
                            JobInfo info = JobScheduler.jobInfoDao.loadById(null,log.getJobId());
                            try {
                                failAlarm(info, log);
                            } catch (EmailException e) {
                                e.printStackTrace();
                            }
                            logger.info(">>>>>>>>>>> job monitor last, job fail, JobLogId:{}", jobLogId);
                        }
                    }
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    // producer
    public static void monitor(int jobLogId) {
        getInstance().queue.offer(jobLogId);
    }


    // ---------------------- alarm ----------------------

    // email alarm template
    private static final String mailBodyTemplate = "<h5>" + "jobconf_monitor_detail" + "：</span>" +
            "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
            "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
            "      <tr>\n" +
            "         <td width=\"20%\" >" + "jobinfo_field_jobgroup" + "</td>\n" +
            "         <td width=\"10%\" >" + "jobinfo_field_id" + "</td>\n" +
            "         <td width=\"20%\" >" + "jobinfo_field_jobdesc" + "</td>\n" +
            "         <td width=\"10%\" >" + "jobconf_monitor_alarm_title" + "</td>\n" +
            "         <td width=\"40%\" >" + "jobconf_monitor_alarm_content" + "</td>\n" +
            "      </tr>\n" +
            "   </thead>\n" +
            "   <tbody>\n" +
            "      <tr>\n" +
            "         <td>{0}</td>\n" +
            "         <td>{1}</td>\n" +
            "         <td>{2}</td>\n" +
            "         <td>" + "jobconf_monitor_alarm_type" + "</td>\n" +
            "         <td>{3}</td>\n" +
            "      </tr>\n" +
            "   </tbody>\n" +
            "</table>";

    /**
     * fail alarm
     *
     * @param jobLog
     */
    private void failAlarm(JobInfo info, JobLog jobLog) throws EmailException {

        // send monitor email
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != Return.SUCCESS_CODE) {
                alarmContent += "<br>TriggerMsg=" + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != Return.SUCCESS_CODE) {
                alarmContent += "<br>HandleCode=" + jobLog.getHandleMsg();
            }

            Set<String> emailSet = new HashSet<String>(Arrays.asList(info.getAlarmEmail().split(",")));
            for (String email : emailSet) {
//				JobGroup group = JobScheduler.jobGroupDao.load(info.getJobGroup());
                JobGroup group = null;
                String title = "jobconf_monitor";
                String content = MessageFormat.format(mailBodyTemplate,
                        group != null ? group.getTitle() : "null",
                        info.getId(),
                        info.getJobDesc(),
                        alarmContent);
                MailUtil.sendMail(email, title, content);
            }
        }
    }

}
