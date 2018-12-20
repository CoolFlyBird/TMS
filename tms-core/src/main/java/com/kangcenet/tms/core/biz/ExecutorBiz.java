package com.kangcenet.tms.core.biz;

import com.kangcenet.tms.core.biz.model.LogResult;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;

public interface ExecutorBiz {

    /**
     * run
     *
     * @param triggerParam
     * @return
     */
    Return<String> run(TriggerParam triggerParam);


    /**
     * log
     *
     * @param logDateTim
     * @param logId
     * @param fromLineNum
     * @return
     */
    Return<LogResult> log(long logDateTim, int logId, int fromLineNum);
}
