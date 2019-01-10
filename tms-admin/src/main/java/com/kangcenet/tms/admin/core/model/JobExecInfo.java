package com.kangcenet.tms.admin.core.model;

import java.util.Date;

public class JobExecInfo {
    private int total;
    private int success;
    private int fail;
    private int unHandle;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public int getUnHandle() {
        return unHandle;
    }

    public void setUnHandle(int unHandle) {
        this.unHandle = unHandle;
    }
}
