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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobServiceImpl implements JobService {
    //    @Resource
//    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private JobInfoDao jobInfoDao;
//    @Resource
//    public XxlJobLogDao xxlJobLogDao;

    public Map<String, Object> pageList(int start, int length, String jobGroup, String jobDesc, String executorHandler, String filterTime) {
        // page list
        List<JobInfo> list = jobInfoDao.pageList(start, length, jobGroup, jobDesc, executorHandler);
        int list_count = jobInfoDao.pageListCount(start, length, jobGroup, jobDesc, executorHandler);

        // fill job info
        if (list != null && list.size() > 0) {
            for (JobInfo jobInfo : list) {
                JobScheduler.fillJobInfo(jobInfo);
            }
        }
        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("data", list);                    // 分页列表
        maps.put("recordsTotal", list_count);// 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        return maps;
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
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new Return<String>(Return.FAIL_CODE, "Cron格式非法");
        }
        if (StringUtils.isEmpty(jobInfo.getJobDesc())) {
            return new Return<String>(Return.FAIL_CODE, "任务描述不能空");
        }

        JobInfo existsJobInfo = jobInfoDao.loadById(jobInfo.getId());
        if (existsJobInfo == null) {
            return new Return<String>(Return.FAIL_CODE, "任务ID不正确");
        }
        existsJobInfo.setId(jobInfo.getId());
        existsJobInfo.setJobGroup(jobInfo.getJobGroup());
        existsJobInfo.setJobCron(jobInfo.getJobCron());
        existsJobInfo.setJobDesc(jobInfo.getJobDesc());
        existsJobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
        existsJobInfo.setAddress(jobInfo.getAddress());
        existsJobInfo.setCommand(jobInfo.getCommand());
        jobInfoDao.update(existsJobInfo);
        // fresh quartz
        String qz_name = existsJobInfo.getId();
        String qz_group = existsJobInfo.getJobGroup();
        try {
            boolean ret = JobScheduler.rescheduleJob(qz_group, qz_name, existsJobInfo.getJobCron());
            return ret ? Return.SUCCESS : Return.FAIL;
        } catch (SchedulerException e) {
//            logger.error(e.getMessage(), e);
        }
        return Return.FAIL;
    }

    public Return<String> remove(String id) {
        JobInfo xxlJobInfo = jobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            JobScheduler.removeJob(name, group);
            jobInfoDao.delete(id);
//            xxlJobLogDao.delete(id);
            return Return.SUCCESS;
        } catch (SchedulerException e) {
//            logger.error(e.getMessage(), e);
        }
        return Return.FAIL;
    }

    public Return<String> pause(String id) {
        JobInfo jobInfo = jobInfoDao.loadById(id);
        String name = jobInfo.getId();
        String group = jobInfo.getJobGroup();
        try {
            boolean ret = JobScheduler.pauseJob(name, group);    // jobStatus do not store
            return ret ? Return.SUCCESS : Return.FAIL;
        } catch (SchedulerException e) {
            return Return.FAIL;
        }
    }

    public Return<String> resume(String id) {
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
