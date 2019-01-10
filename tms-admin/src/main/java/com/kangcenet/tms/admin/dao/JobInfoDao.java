package com.kangcenet.tms.admin.dao;

import com.kangcenet.tms.admin.core.model.JobInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface JobInfoDao {

    public List<JobInfo> pageList(@Param("page") int page,
                                  @Param("limit") int limit,
                                  @Param("jobGroup") String jobGroup,
                                  @Param("jobDesc") String jobDesc,
                                  @Param("executorHandler") String executorHandler);

    public int pageListCount(@Param("page") int page,
                             @Param("limit") int limit,
                             @Param("jobGroup") String jobGroup,
                             @Param("jobDesc") String jobDesc,
                             @Param("executorHandler") String executorHandler);

    public int save(JobInfo info);

    public int update(JobInfo item);

    public int delete(@Param("jobGroup") String jobGroup, @Param("id") String id);

    public JobInfo loadById(@Param("jobGroup") String jobGroup, @Param("id") String id);

    public int findAllCount();

}
