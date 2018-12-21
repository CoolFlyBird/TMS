package com.kangcenet.tms.admin.core.util;

import com.kangcenet.tms.admin.service.impl.JobServiceImpl;
import com.kangcenet.tms.core.biz.model.Return;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static OkHttpClient client = new OkHttpClient.Builder().build();

    public static Return execute(String address, String command) {
        Response response = null;
        try {
            Request request = new Request.Builder().url(address + command).build();
            Call call = client.newCall(request);
            response = call.execute();
        } catch (Exception e) {
            logger.info("message {}", e);
            return new Return(Return.FAIL.getCode(), e.toString());
        }
        if (response.code() == 200) {
            return new Return(Return.SUCCESS.getCode(), "success");
        } else {
            String result = response.message();
            return new Return(Return.FAIL.getCode(), result);
        }

    }
}
