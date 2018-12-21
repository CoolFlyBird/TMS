package com.kangcenet.tms.core.biz;

import com.kangcenet.tms.core.biz.model.HandleCallbackParam;
import com.kangcenet.tms.core.biz.model.Return;

import java.util.List;

public interface AdminBiz {
    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public Return<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------
//    /**
//     * registry
//     *
//     * @param registryParam
//     * @return
//     */
//    public ReturnT<String> registry(RegistryParam registryParam);
//
//    /**
//     * registry remove
//     *
//     * @param registryParam
//     * @return
//     */
//    public ReturnT<String> registryRemove(RegistryParam registryParam);
}
