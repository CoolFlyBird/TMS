package com.kangcenet.tms.core.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellExecUtil {
    private static Log log = LogFactory.getLog(ShellExecUtil.class);

    /**
     * 创建会话连接
     *
     * @param ip         主机IP
     * @param user       主机登陆用户名
     * @param pwd        主机登陆密码
     * @param port       主机ssh2登陆端口，如果取默认值，传-1
     * @param privateKey
     * @param passphrase
     */
    private static Session sshConnect(String ip, String user, String pwd, int port,
                                     String privateKey, String passphrase) throws Exception {


        //验证主机ip
        if (null == ip || "".equals(ip)) {
            log.error("主机IP为空");
            throw new RuntimeException("主机IP为空");
        }

        //验证主机登陆用户名
        if (null == user || "".equals(user)) {
            log.error("主机登陆用户名为空");
            throw new RuntimeException("主机登陆用户名为空");
        }

        //验证主机登陆密码
        if (null == pwd || "".equals(pwd)) {
            log.error("主机登陆密码为空");
            throw new RuntimeException("主机登陆密码为空");
        }


        JSch jsch = new JSch();
        Session session = null;

        //设置密钥和密码
        if (privateKey != null && !"".equals(privateKey)) {
            if (passphrase != null && "".equals(passphrase)) {
                //设置带口令的密钥
                jsch.addIdentity(privateKey, passphrase);
            } else {
                //设置不带口令的密钥
                jsch.addIdentity(privateKey);
            }
        }

        if (port <= 0) {
            //连接服务器，采用默认端口
            session = jsch.getSession(user, ip);
        } else {
            //采用指定的端口连接服务器
            session = jsch.getSession(user, ip, port);
        }

        //如果服务器连接不上，则抛出异常
        if (null == session) {
            log.error("ssh session is null");
            throw new Exception("ssh session is null");
        }

        //设置登陆主机的密码
        session.setPassword(pwd);//设置密码
        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        //设置登陆超时时间
        session.connect(10000);
        session.sendKeepAliveMsg();
        session.setServerAliveCountMax(10000);


        //返回会话
        return session;

    }


    /**
     * 关闭会话连接
     *
     * @param session
     */
    private static void sshDisconnect(Session session) throws Exception {

        if (null == session) {
            log.error("ssh session is null,关闭session异常");
            throw new Exception("ssh session is null,关闭session异常");
        }
        session.disconnect();

    }


    /**
     * 执行ssh远程命令
     *
     * @param ip
     * @param user
     * @param pwd
     * @param port
     * @param privateKey
     * @param passphrase
     * @param command    执行命令
     */
    public static String sshExecCmd(String ip, String user, String pwd, int port, String privateKey, String passphrase, String command) throws Exception {

        //获取ssh连接会话
        Session session = sshConnect(ip, user, pwd, port,
                privateKey, passphrase);

        if (null == session) {
            log.error("创建ssh连接失败");
            throw new RuntimeException("创建ssh连接失败");
        }
        System.out.println(command);

        ChannelExec openChannel = null;
        openChannel = (ChannelExec) session.openChannel("exec");
//        openChannel.setExtOutputStream(System.err);
        openChannel.setCommand(command);
        openChannel.connect(10000);

        InputStream in = openChannel.getInputStream();
        InputStream extIn = openChannel.getExtInputStream();

        String exit = "";
        String result = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String buf = null;
        while ((buf = reader.readLine()) != null) {
            result += buf.toString() + "\n";//new String(buf.getBytes("gbk"),"UTF-8") + "\n";
        }

        BufferedReader extReader = new BufferedReader(new InputStreamReader(extIn));
        String extBuf = null;
        while ((extBuf = extReader.readLine()) != null) {
            exit += extBuf.toString() + "\n";//new String(buf.getBytes("gbk"),"UTF-8") + "\n";
        }

        exit = exit.trim().replaceAll("\\s{1,}", " ");
        result = result.trim().replaceAll("\\s{1,}", " ");
        log.info("exit:" + exit);
        log.info("result:" + result);

        //断开连接
        openChannel.disconnect();
        sshDisconnect(session);

        if (StringUtils.isEmpty(result)) {
            result = exit;
        }
        return result;
    }


    public static void main(String[] args) {
        try {
            //空格->%20
            //&->%26
            String execCmdResult = sshExecCmd("192.168.15.61", "bebepay", "bebepay", 4022, null, null, "/home/bebepay/bebepayplatform/send_dxw_lol.sh && /home/bebepay/bebepayplatform/rupdate_dxw_lol.sh");
            System.out.println(execCmdResult);
//            String execCmdResult = sshExecCmd("192.168.15.10", "root", "111111", 22, null, null, "/home/bebepay/bebepayplatform/publish_dev_lol.sh", true);
//            System.out.println(execCmdResult);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
