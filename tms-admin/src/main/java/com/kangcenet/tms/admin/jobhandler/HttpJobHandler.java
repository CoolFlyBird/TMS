package com.kangcenet.tms.admin.jobhandler;

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
    public Return<String> execute(TriggerParam param) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(param.getAddress() + param.getCommand()).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String result = response.body().string();
        log.info("response:" + result);
        return new Return<String>(result);
    }
}
