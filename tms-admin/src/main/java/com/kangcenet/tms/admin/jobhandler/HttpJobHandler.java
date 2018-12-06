package com.kangcenet.tms.admin.jobhandler;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.handler.annotation.JobHandler;
import okhttp3.*;
import org.springframework.stereotype.Component;

@JobHandler(value = "httpJobHandler")
@Component
public class HttpJobHandler extends IJobHandler<JobInfo> {
    @Override
    public Return<String> execute(JobInfo param) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(param.getAddress() + param.getCommand()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String result = response.body().string();
        System.err.println("response:" + result);
        return new Return<String>(result);
    }
}
