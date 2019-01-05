package com.kangcenet.tms.admin.dao;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.JobLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface JobLogDao {

    public List<JobLog> pageList(@Param("offset") int offset,
                                 @Param("pagesize") int pagesize,
                                 @Param("jobGroup") String jobGroup,
                                 @Param("jobId") String jobId,
                                 @Param("triggerTimeStart") Date triggerTimeStart,
                                 @Param("triggerTimeEnd") Date triggerTimeEnd,
                                 @Param("logStatus") int logStatus);


    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") String jobGroup,
                             @Param("jobId") String jobId,
                             @Param("triggerTimeStart") Date triggerTimeStart,
                             @Param("triggerTimeEnd") Date triggerTimeEnd,
                             @Param("logStatus") int logStatus);

    public JobLog load(@Param("jobGroup") String jobGroup, @Param("id") int id);

    public int save(JobLog jobLog);

    public int updateTriggerInfo(JobLog jobLog);

    public int updateHandleInfo(JobLog jobLog);

    public int delete(@Param("jobGroup") String jobGroup, @Param("jobId") String jobId);

    public int triggerCountByHandleCode(@Param("handleCode") int handleCode);

    public List<Map<String, Object>> triggerCountByDay(@Param("from") Date from,
                                                       @Param("to") Date to);

    public int clearLog(@Param("jobGroup") String jobGroup,
                        @Param("jobId") String jobId,
                        @Param("clearBeforeTime") Date clearBeforeTime,
                        @Param("clearBeforeNum") int clearBeforeNum);

}
