package com.kangcenet.tms.core.biz;

import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;

public interface ExecutorBiz {

    /**
     * run
     * @param triggerParam
     * @return
     */
    Return<String> run(TriggerParam triggerParam);
}
