package com.kangcenet.tms.admin.jobhandler;

import com.kangcenet.tms.admin.core.model.JobInfo;
import com.kangcenet.tms.core.biz.model.Return;
import com.kangcenet.tms.core.handler.IJobHandler;
import com.kangcenet.tms.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

import static com.kangcenet.tms.core.util.ShellExecUtil.sshExecCmd;

@JobHandler(value = "shellJobHandler")
@Component
public class ShellJobHandler extends IJobHandler<JobInfo> {
    public Return<String> execute(JobInfo jobInfo) throws Exception {
        String execCmdResult = sshExecCmd(jobInfo.getAddress(), jobInfo.getUser(), jobInfo.getPwd(), jobInfo.getPort(),
                jobInfo.getPrivateKey(), jobInfo.getPassphrase(), jobInfo.getCommand());
        System.out.println(execCmdResult);
        return new Return<String>(execCmdResult);
    }
}
