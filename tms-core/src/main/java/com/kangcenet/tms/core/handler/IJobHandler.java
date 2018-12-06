package com.kangcenet.tms.core.handler;

import com.kangcenet.tms.core.biz.model.Return;

public abstract class IJobHandler<T> {
    /** success */
    public static final Return<String> SUCCESS = new Return<String>(1000, null);
    /** fail */
    public static final Return<String> FAIL = new Return<String>(1001, null);
    /** fail timeout */
    public static final Return<String> FAIL_TIMEOUT = new Return<String>(1002, null);

    /**
     * execute handler, invoked when executor receives a scheduling request
     * @param param
     * @return
     * @throws Exception
     */
    public abstract Return<String> execute(T param) throws Exception;

    /**
     * init handler, invoked when JobThread init
     */
    public void init() {
        // TODO
    }


    /**
     * destroy handler, invoked when JobThread destroy
     */
    public void destroy() {
        // TODO
    }

}
