package com.kangcenet.tms.core.trigger;

import com.kangcenet.tms.core.biz.ExecutorBiz;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;

import javax.annotation.Resource;

public class JobTrigger {
    @Resource
    private ExecutorBiz executorBiz;

    public Return<String> runExecutor(TriggerParam triggerParam, String address) {
        Return<String> runResult = null;
        try {
//            ExecutorBiz executorBiz = JobDynamicScheduler.getExecutorBiz(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
//            logger.error(">>>>>>>>>>> xxl-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new Return<String>(Return.FAIL_CODE, "" + e);
        }
        return runResult;
    }
}
