package com.kangcenet.tms.admin.controller;

import com.kangcenet.tms.core.biz.model.Return;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/job")
public class JobInfoController {

    @RequestMapping("/trigger")
    public Return<String> addJob(@RequestParam Map<String, String> params) {
        int id = Integer.parseInt(params.get("params"));
        String executorParam = params.get("params");
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }
        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
    }
}
