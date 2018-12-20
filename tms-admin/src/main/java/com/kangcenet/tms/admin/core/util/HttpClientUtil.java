package com.kangcenet.tms.admin.core.util;

import com.kangcenet.tms.core.biz.model.Return;
import okhttp3.*;

import java.io.IOException;

public class HttpClientUtil {
    private static OkHttpClient client = new OkHttpClient.Builder().build();

    public static Return execute(String address, String command) {
        Request request = new Request.Builder().url(address + command).build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return new Return(Return.FAIL.getCode(), e.getMessage());
        }
        if (response.code() == 200) {
            return new Return(Return.SUCCESS.getCode(), "success");
        } else {
            String result = response.message();
            return new Return(Return.FAIL.getCode(), result);
        }

    }
}
