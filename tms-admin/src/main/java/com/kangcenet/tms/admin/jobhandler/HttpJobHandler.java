package com.kangcenet.tms.admin.jobhandler;

import com.kangcenet.tms.admin.core.util.HttpClientUtil;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.handler.annotation.JobHandler;
import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@JobHandler(value = "httpJobHandler")
@Component
public class HttpJobHandler extends IJobHandler<TriggerParam> {
    private static Log log = LogFactory.getLog(ShellJobHandler.class);

    @Override
    public Return<String> execute(TriggerParam param) {
        return HttpClientUtil.execute(param.getAddress(), param.getCommand());
    }
}
