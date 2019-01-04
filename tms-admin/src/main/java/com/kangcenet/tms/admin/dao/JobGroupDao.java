package com.kangcenet.tms.admin.dao;

import com.kangcenet.tms.admin.core.model.JobGroup;

import java.util.List;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface JobGroupDao {
    public List<JobGroup> pageList();

    public JobGroup select(String appName);

    public int create(JobGroup jobGroup);

    public int delete(String appName);

}
