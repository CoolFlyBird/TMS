package com.kangcenet.tms.admin.service.impl;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.admin.service.JobService;
import com.kangcenet.tms.core.biz.model.Return;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class JobServiceImpl implements JobService {
    //    @Resource
//    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private JobInfoDao jobInfoDao;
//    @Resource
//    public XxlJobLogDao xxlJobLogDao;

    public Map<String, Object> pageList(int start, int length, int jobGroup, String jobDesc, String executorHandler, String filterTime) {
        return null;
    }

    public Return<String> add(JobInfo jobInfo) {
        // valid
//        JobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
//        if (group == null) {
//            return new Return<String>(Return.FAIL_CODE, "任务组");
//        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new Return<String>(Return.FAIL_CODE, "定时参数有误");
        }
        if (StringUtils.isEmpty(jobInfo.getJobDesc())) {
            return new Return<String>(Return.FAIL_CODE, "任务描述不能空");
        }
        // add in db
        jobInfoDao.save(jobInfo);

        // add in quartz
        String qzName = String.valueOf(jobInfo.getId());
        String qzGroup = String.valueOf(jobInfo.getJobGroup());
        try {
            JobScheduler.addJob(qzName, qzGroup, jobInfo.getJobCron());
            //XxlJobDynamicScheduler.pauseJob(qz_name, qz_group);
            return Return.SUCCESS;
        } catch (SchedulerException e) {
//            logger.error(e.getMessage(), e);
//            try {
//                xxlJobInfoDao.delete(jobInfo.getId());
//                JobScheduler.removeJob(qz_name, qz_group);
//            } catch (SchedulerException e1) {
//                logger.error(e.getMessage(), e1);
//            }
            return new Return<String>(Return.FAIL_CODE, "任务失败:" + e.getMessage());
        }
    }

    public Return<String> update(JobInfo jobInfo) {
        return null;
    }

    public Return<String> remove(int id) {
        JobInfo xxlJobInfo = jobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            JobScheduler.removeJob(name, group);
            jobInfoDao.delete(id);
            xxlJobLogDao.delete(id);
            return Return.SUCCESS;
        } catch (SchedulerException e) {
//            logger.error(e.getMessage(), e);
        }
        return Return.FAIL;
    }

    public Return<String> pause(int id) {
        return null;
    }

    public Return<String> resume(int id) {
        JobInfo xxlJobInfo = jobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());
        try {
            boolean ret = JobScheduler.resumeJob(name, group);
            return ret ? Return.SUCCESS : Return.FAIL;
        } catch (SchedulerException e) {
            return Return.FAIL;
        }
    }
}
