package com.kangcenet.tms.admin.jobhandler;

import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.biz.model.TriggerParam;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.handler.annotation.JobHandler;
import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@JobHandler(value = "shellJobHandler")
@Component
public class ShellJobHandler extends IJobHandler<TriggerParam> {
    private static Log log = LogFactory.getLog(ShellJobHandler.class);

    @Override
    public Return<String> execute(TriggerParam param) throws Exception {
//        log.info("execute:" + param.getAddress() + " " + param.getUser() + " " + param.getPwd() + " " + param.getCommand());
//        String execCmdResult = sshExecCmd(param.getSession(), param.getAddress(), param.getUser(), param.getPwd(), param.getPort(), param.getPrivateKey(), param.getPassphrase(), param.getCommand());
//        return new Return<String>(execCmdResult);
        OkHttpClient client = new OkHttpClient.Builder().build();
        RequestBody body = new FormBody.Builder()
                .add("键", "值")
                .build();
        Request request = new Request.Builder()
                .url(param.getAddress() + "/" + param.getCommand())
                .post(body)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String result = response.body().string();
        log.info("ShellJobHandler :" + response.code() + "|" + response.message() + "|" + result);
        return new Return<String>(result);
    }
}
