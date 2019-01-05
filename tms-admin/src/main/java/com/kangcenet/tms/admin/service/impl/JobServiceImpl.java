package com.kangcenet.tms.admin.service.impl;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.admin.dao.JobLogDao;
import com.kangcenet.tms.admin.service.JobService;
import com.kangcenet.tms.core.biz.model.Return;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class JobServiceImpl implements JobService {
    private static Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    //    @Resource
//    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private JobInfoDao jobInfoDao;
    @Resource
    public JobLogDao jobLogDao;

    @Override
    public Map<String, Object> pageList(int start, int length, String jobGroup, String jobDesc, String executorHandler) {
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

    @Override
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
            return Return.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            try {
                jobInfoDao.delete(jobInfo.getJobGroup(), jobInfo.getId());
                JobScheduler.removeJob(qzName, qzGroup);
            } catch (SchedulerException e1) {
                logger.error(e.getMessage(), e1);
            }
            return new Return<String>(Return.FAIL_CODE, "任务失败:" + e.getMessage());
        }
    }

    @Override
    public Return<String> update(JobInfo jobInfo) {
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new Return<String>(Return.FAIL_CODE, "Cron格式非法");
        }
        if (StringUtils.isEmpty(jobInfo.getJobDesc())) {
            return new Return<String>(Return.FAIL_CODE, "任务描述不能空");
        }

        JobInfo existsJobInfo = jobInfoDao.loadById(jobInfo.getJobGroup(), jobInfo.getId());
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
            logger.error(e.getMessage(), e);
        }
        return Return.FAIL;
    }

    @Override
    public Return<String> remove(String jobGroup, String id) {
        JobInfo xxlJobInfo = jobInfoDao.loadById(jobGroup, id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());
        try {
            JobScheduler.removeJob(name, group);
            jobInfoDao.delete(jobGroup, id);
            jobLogDao.delete(jobGroup, id);
            return Return.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
        return Return.FAIL;
    }

    @Override
    public Return<String> pause(String jobGroup, String id) {
        JobInfo jobInfo = jobInfoDao.loadById(jobGroup, id);
        String name = jobInfo.getId();
        String group = jobInfo.getJobGroup();
        try {
            boolean ret = JobScheduler.pauseJob(name, group);    // jobStatus do not store
            return ret ? Return.SUCCESS : Return.FAIL;
        } catch (SchedulerException e) {
            return Return.FAIL;
        }
    }

    @Override
    public Return<String> resume(String jobGroup, String id) {
        JobInfo jobInfo = jobInfoDao.loadById(jobGroup, id);
        String group = jobInfo.getJobGroup();
        String name = jobInfo.getId();
        try {
            boolean ret = JobScheduler.resumeJob(name, group);
            return ret ? Return.SUCCESS : Return.FAIL;
        } catch (SchedulerException e) {
            return Return.FAIL;
        }
    }


    @Override
    public Map<String, Object> dashboardInfo() {
        int jobInfoCount = jobInfoDao.findAllCount();
        int jobLogCount = jobLogDao.triggerCountByHandleCode(-1);
        int jobLogSuccessCount = jobLogDao.triggerCountByHandleCode(Return.SUCCESS_CODE);

        // executor count
//        Set<String> executerAddressSet = new HashSet<String>();
//        List<JobGroup> groupList = jobGroupDao.findAll();
//        if (CollectionUtils.isNotEmpty(groupList)) {
//            for (JobGroup group: groupList) {
//                if (CollectionUtils.isNotEmpty(group.getRegistryList())) {
//                    executerAddressSet.addAll(group.getRegistryList());
//                }
//            }
//        }

//        int executorCount = executerAddressSet.size();
        Map<String, Object> dashboardMap = new HashMap<String, Object>();
        dashboardMap.put("jobInfoCount", jobInfoCount);
        dashboardMap.put("jobLogCount", jobLogCount);
        dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
//        dashboardMap.put("executorCount", executorCount);
        return dashboardMap;
    }

    @Override
    public Return<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
		/*// get cache
		String cacheKey = TRIGGER_CHART_DATA_CACHE + "_" + startDate.getTime() + "_" + endDate.getTime();
		Map<String, Object> chartInfo = (Map<String, Object>) LocalCacheUtil.get(cacheKey);
		if (chartInfo != null) {
			return new ReturnT<Map<String, Object>>(chartInfo);
		}*/

        // process
        List<String> triggerDayList = new ArrayList<String>();
        List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
        List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
        List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
        int triggerCountRunningTotal = 0;
        int triggerCountSucTotal = 0;
        int triggerCountFailTotal = 0;

        List<Map<String, Object>> triggerCountMapAll = jobLogDao.triggerCountByDay(startDate, endDate);
        if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
            for (Map<String, Object> item : triggerCountMapAll) {
                String day = String.valueOf(item.get("triggerDay"));
                int triggerDayCount = Integer.valueOf(String.valueOf(item.get("triggerDayCount")));
                int triggerDayCountRunning = Integer.valueOf(String.valueOf(item.get("triggerDayCountRunning")));
                int triggerDayCountSuc = Integer.valueOf(String.valueOf(item.get("triggerDayCountSuc")));
                int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                triggerDayList.add(day);
                triggerDayCountRunningList.add(triggerDayCountRunning);
                triggerDayCountSucList.add(triggerDayCountSuc);
                triggerDayCountFailList.add(triggerDayCountFail);

                triggerCountRunningTotal += triggerDayCountRunning;
                triggerCountSucTotal += triggerDayCountSuc;
                triggerCountFailTotal += triggerDayCountFail;
            }
        } else {
            for (int i = 4; i > -1; i--) {
                triggerDayList.add(FastDateFormat.getInstance("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -i)));
                triggerDayCountSucList.add(0);
                triggerDayCountFailList.add(0);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("triggerDayList", triggerDayList);
        result.put("triggerDayCountRunningList", triggerDayCountRunningList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);

        result.put("triggerCountRunningTotal", triggerCountRunningTotal);
        result.put("triggerCountSucTotal", triggerCountSucTotal);
        result.put("triggerCountFailTotal", triggerCountFailTotal);

		/*// set cache
		LocalCacheUtil.set(cacheKey, result, 60*1000);     // cache 60s*/
        return new Return<Map<String, Object>>(result);
    }
}
