package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.JobLog;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.admin.dao.JobLogDao;
import com.kangcenet.tms.admin.service.JobService;
import com.kangcenet.tms.core.biz.model.Return;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/joblog")
public class JobLogController {

    @Resource
    public JobInfoDao jobInfoDao;
    @Resource
    public JobLogDao jobLogDao;

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        @RequestParam(required = false, defaultValue = "1") int logStatus,
                                        String jobGroup, String jobId, String filterTime) {
        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (StringUtils.isNotBlank(filterTime)) {
            String[] temp = filterTime.split(" - ");
            if (temp != null && temp.length == 2) {
                try {
                    triggerTimeStart = DateUtils.parseDate(temp[0], new String[]{"yyyy-MM-dd HH:mm:ss"});
                    triggerTimeEnd = DateUtils.parseDate(temp[1], new String[]{"yyyy-MM-dd HH:mm:ss"});
                } catch (ParseException e) {
                }
            }
        }

        // page query
        List<JobLog> list = jobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int list_count = jobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);        // 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping("/logDetailPage")
    @ResponseBody
    public Return logDetailPage(int id) {
        // base check
        JobLog jobLog = jobLogDao.load(id);
        if (jobLog == null) {
            return new Return(Return.FAIL_CODE, "id无效");
        }
        HashMap map = new HashMap<String, Object>();
        map.put("triggerCode", jobLog.getTriggerCode());
        map.put("handleCode", jobLog.getHandleCode());
        map.put("executorAddress", jobLog.getTriggerCode());
        map.put("triggerTime", jobLog.getTriggerTime().getTime());
        map.put("logId", jobLog.getId());
        return new Return(map);
    }

}
