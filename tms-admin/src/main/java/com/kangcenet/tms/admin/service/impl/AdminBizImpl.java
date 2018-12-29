package com.kangcenet.tms.admin.service.impl;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.admin.core.model.JobLog;
import com.kangcenet.tms.admin.dao.JobInfoDao;
import com.kangcenet.tms.admin.dao.JobLogDao;
import com.kangcenet.tms.core.biz.AdminBiz;
import com.kangcenet.tms.core.biz.model.HandleCallbackParam;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.handler.IJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Resource
    public JobLogDao xxlJobLogDao;
    @Resource
    private JobInfoDao xxlJobInfoDao;


    @Override
    public Return<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam : callbackParamList) {
            Return<String> callbackResult = callback(handleCallbackParam);
            logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode() == IJobHandler.SUCCESS.getCode() ? "success" : "fail"), handleCallbackParam, callbackResult);
        }

        return Return.SUCCESS;
    }

    private Return<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
        logger.info(">>>>>>>>> HandleCallbackParam {}-{}", handleCallbackParam.getLogId(), log);
        if (log == null) {
            return new Return<String>(Return.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new Return<String>(Return.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
            JobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
//            if (xxlJobInfo != null && StringUtils.isNotBlank(xxlJobInfo.getChildJobId())) {
//                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_child_run") + "<<<<<<<<<<< </span><br>";
//
//                String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
//                for (int i = 0; i < childJobIds.length; i++) {
//                    int childJobId = (StringUtils.isNotBlank(childJobIds[i]) && StringUtils.isNumeric(childJobIds[i])) ? Integer.valueOf(childJobIds[i]) : -1;
//                    if (childJobId > 0) {
//
//                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, 0, null, null);
//                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;
//
//                        // add msg
//                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
//                                (i + 1),
//                                childJobIds.length,
//                                childJobIds[i],
//                                (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? I18nUtil.getString("system_success") : I18nUtil.getString("system_fail")),
//                                triggerChildResult.getMsg());
//                    } else {
//                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
//                                (i + 1),
//                                childJobIds.length,
//                                childJobIds[i]);
//                    }
//                }
//            }
        }
        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        logger.info("handle message {} - {}", handleCallbackParam.getExecuteResult().getCode(), handleCallbackParam.getExecuteResult().getMsg());
        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        xxlJobLogDao.updateHandleInfo(log);
        return Return.SUCCESS;
    }

//    @Override
//    public Return<String> registry(RegistryParam registryParam) {
//        int ret = xxlJobRegistryDao.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
//        if (ret < 1) {
//            xxlJobRegistryDao.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
//        }
//        return ReturnT.SUCCESS;
//    }
//
//    @Override
//    public ReturnT<String> registryRemove(RegistryParam registryParam) {
//        xxlJobRegistryDao.registryDelete(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
//        return ReturnT.SUCCESS;
//    }

}
