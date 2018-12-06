package com.kangcenet.tms.admin.service;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.core.biz.model.Return;

import java.util.Map;

public interface JobService {

    /**
     * page list
     * @param start
     * @param length
     * @param jobGroup
     * @param jobDesc
     * @param executorHandler
     * @param filterTime
     * @return
     */
    Map<String, Object> pageList(int start, int length, int jobGroup, String jobDesc, String executorHandler, String filterTime);

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
    Return<String> remove(int id);

//    /**
//     * pause job
//     * @param id
//     * @return
//     */
//    Return<String> pause(int id);
//    /**
//     * resume job
//     * @param id
//     * @return
//     */
//    Return<String> resume(int id);
}
