package com.kangcenet.tms.core.biz.model;

import java.io.Serializable;

public class Return<T> implements Serializable {
    public static final int SUCCESS_CODE = 200;
    public static final int UN_LOGIN = 210;
    public static final int FAIL_CODE = 500;
    public static final Return<String> SUCCESS = new Return<String>(SUCCESS_CODE, null);
    public static final Return<String> FAIL = new Return<String>(FAIL_CODE, null);

    private int code;
    private String msg = "执行成功";
    private T data;


    public Return() {
    }

    public Return(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Return(T data) {
        this.code = SUCCESS_CODE;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Return [code=" + code + ", msg=" + msg + ", data=" + data + "]";
    }

}
