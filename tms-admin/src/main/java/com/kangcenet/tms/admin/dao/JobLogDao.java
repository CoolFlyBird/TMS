package com.kangcenet.tms.admin.dao;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.JobLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface JobLogDao {

    public List<JobLog> pageList(@Param("offset") int offset,
                                 @Param("pagesize") int pagesize,
                                 @Param("jobGroup") String jobGroup,
                                 @Param("jobId") String jobId);


    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") String jobGroup,
                             @Param("jobId") String jobId,
                             @Param("triggerTimeStart") Date triggerTimeStart,
                             @Param("triggerTimeEnd") Date triggerTimeEnd,
                             @Param("logStatus") int logStatus);

    public int save(JobInfo info);

    public JobInfo loadById(@Param("id") String id);

    public int update(JobInfo item);

    public int delete(@Param("id") String id);

    public int findAllCount();

//    public List<JobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

}
