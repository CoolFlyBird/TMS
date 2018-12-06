package com.kangcenet.tms.executor.jobhandler;

import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.handler.annotation.JobHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

@JobHandler(value = "httpJobHandler")
@Component
public class HttpJobHandler extends IJobHandler {

    public Return<String> execute(Object param) throws Exception {
        return null;
    }
}
