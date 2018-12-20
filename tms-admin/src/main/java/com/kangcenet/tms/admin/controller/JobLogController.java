package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.admin.core.model.JobLog;
import com.kangcenet.tms.admin.core.schedule.JobScheduler;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.admin.dao.JobLogDao;
import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.model.LogResult;
import com.kangcenet.tms.core.biz.model.Return;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

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

    @RequestMapping("/logDetailCat")
    @ResponseBody
    public Return<LogResult> logDetailCat(String executorAddress, long triggerTime, int logId, int fromLineNum) {
        try {
            ExecutorBiz executorBiz = JobScheduler.getExecutorBiz(executorAddress);
            Return<LogResult> logResult = executorBiz.log(triggerTime, logId, fromLineNum);
            // is end
            if (logResult.getData() != null && logResult.getData().getFromLineNum() > logResult.getData().getToLineNum()) {
                JobLog jobLog = jobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getData().setEnd(true);
                }
            }
            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new Return<LogResult>(Return.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/clearLog")
    @ResponseBody
    public Return<String> clearLog(String jobGroup, String jobId, int type) {
        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtils.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new Return<String>(Return.FAIL_CODE, "type无效输入");
        }
        jobLogDao.clearLog(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
        return Return.SUCCESS;
    }
}
