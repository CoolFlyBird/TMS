package com.kangcenet.tms.admin.service;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.core.biz.model.Return;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface JobService {

    /**
     * page list
     *
     * @param start
     * @param length
     * @param jobGroup
     * @param jobDesc
     * @param executorHandler
     * @param filterTime
     * @return
     */
    Map<String, Object> pageList(int start, int length, String jobGroup, String jobDesc, String executorHandler, String filterTime);

    /**
     * add job
     *
     * @param jobInfo
     * @return
     */
    Return<String> add(JobInfo jobInfo);

    /**
     * update job
     *
     * @param jobInfo
     * @return
     */
    Return<String> update(JobInfo jobInfo);

    /**
     * remove job
     *
     * @param id
     * @return
     */
    Return<String> remove(String id);

    /**
     * pause job
     *
     * @param id
     * @return
     */
    Return<String> pause(String id);

    /**
     * resume job
     *
     * @param id
     * @return
     */
    Return<String> resume(String id);

    /**
     * getJobsByGroup
     *
     * @param jobGroup
     * @return
     */
    Return<List<JobInfo>> getJobsByGroup(String jobGroup);
    /**
     * dashboard info
     *
     * @return
     */
    public Map<String, Object> dashboardInfo();

    /**
     * chart info
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public Return<Map<String, Object>> chartInfo(Date startDate, Date endDate);
}
