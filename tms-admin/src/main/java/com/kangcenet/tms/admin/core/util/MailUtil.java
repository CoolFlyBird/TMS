package com.kangcenet.tms.admin.core.util;

import com.kangcenet.tms.admin.core.conf.JobAdminConfig;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtil {
    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    /**
     *
     * @param toAddress		收件人邮箱
     * @param mailSubject	邮件主题
     * @param mailBody		邮件正文
     * @return
     */
    public static boolean sendMail(String toAddress, String mailSubject, String mailBody) throws EmailException {

        try {
            // Create the email message
            HtmlEmail email = new HtmlEmail();
            //email.setDebug(true);		// 将会打印一些log
            //email.setTLS(true);		// 是否TLS校验，，某些邮箱需要TLS安全校验，同理有SSL校验
            //email.setSSL(true);

            email.setHostName(JobAdminConfig.getAdminConfig().getMailHost());

            if (JobAdminConfig.getAdminConfig().isMailSSL()) {
                email.setSslSmtpPort(JobAdminConfig.getAdminConfig().getMailPort());
                email.setSSLOnConnect(true);
            } else {
                email.setSmtpPort(Integer.valueOf(JobAdminConfig.getAdminConfig().getMailPort()));
            }

            email.setAuthenticator(new DefaultAuthenticator(JobAdminConfig.getAdminConfig().getMailUsername(), JobAdminConfig.getAdminConfig().getMailPassword()));
            email.setCharset("UTF-8");

            email.setFrom(JobAdminConfig.getAdminConfig().getMailUsername(), JobAdminConfig.getAdminConfig().getMailSendNick());
            email.addTo(toAddress);
            email.setSubject(mailSubject);
            email.setMsg(mailBody);

            //email.attach(attachment);	// add the attachment

            email.send();				// send the email
            return true;
        } catch (EmailException e) {
            logger.error(e.getMessage(), e);

        }
        return false;
    }
}
