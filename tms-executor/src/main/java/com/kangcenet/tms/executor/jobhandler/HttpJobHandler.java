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
    @Override
    public Return<String> execute(String param) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(param).build();
        client.newCall(request);
        return null;
    }
}
