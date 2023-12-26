package com.wanmi.sbc.common.util;

import com.wanmi.sbc.common.base.EmailConfigVO;
import com.wanmi.sbc.common.base.GenerateExcelSendEmailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @description: 邮件工具类
 * @author: XinJiang
 * @time: 2021/12/20 9:22
 */
@Component
@Slf4j
public class SendEmailUtil {

    /**
     * 发送邮件 带Excel附件
     * @param config
     * @param generateExcelSendEmailVo
     */
    public void sendEmail(EmailConfigVO config, GenerateExcelSendEmailVo generateExcelSendEmailVo){
        //初始化邮件配置
        Session session = initProperties(config);
        try {
            MimeMessage msg = new MimeMessage(session);
            //发信人邮箱及昵称
            msg.setFrom(new InternetAddress(config.getFromEmailAddress(), config.getFromPerson()));
            //设置收件人
            setEmailRecipients(msg,generateExcelSendEmailVo);
            //设置邮件标题
            msg.setSubject(generateExcelSendEmailVo.getEmailTitle(),"UTF-8");
            //设置邮件内容带excel附件
            MimeBodyPart contentPart = (MimeBodyPart) setEmailContent(generateExcelSendEmailVo);
            Multipart mainPart = new MimeMultipart();
            mainPart.addBodyPart(contentPart);
            msg.setContent(mainPart);
            //设置发送日期
            msg.setSentDate(new Date());
            //发送邮件
            Transport.send(msg);
        }catch (Exception e){
            log.error("邮件发送失败！" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 设置邮件内容带excel附件
     * @param generateExcelSendEmailVo
     * @return
     */
    public Part setEmailContent(GenerateExcelSendEmailVo generateExcelSendEmailVo){
        MimeBodyPart contentPart = null;
        try {
            contentPart = new MimeBodyPart();
            MimeMultipart contentMultipart = new MimeMultipart("related");
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(generateExcelSendEmailVo.getEmailContent(), "text/html;charset=GBK");
            contentMultipart.addBodyPart(htmlPart);
            if (Objects.nonNull(generateExcelSendEmailVo.getOut())) {
                //附件部分
                MimeBodyPart excelBodyPart = new MimeBodyPart();
                //附件内容
                DataSource dataSource = new ByteArrayDataSource(new ByteArrayInputStream(generateExcelSendEmailVo.getOut().toByteArray()), "application/excel");
                DataHandler dataHandler = new DataHandler(dataSource);
                excelBodyPart.setDataHandler(dataHandler);
                //附件名称
                excelBodyPart.setFileName(MimeUtility.encodeText(generateExcelSendEmailVo.getFileName()));
                contentMultipart.addBodyPart(excelBodyPart);
                contentPart.setContent(contentMultipart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentPart;
    }

    /**
     * 设置收件人
     * @param msg
     * @param generateExcelSendEmailVo
     */
    public void setEmailRecipients(Message msg, GenerateExcelSendEmailVo generateExcelSendEmailVo) throws MessagingException {
        //收件人设置
        List<InternetAddress> address = new ArrayList<>();
        generateExcelSendEmailVo.getAcceptAddressList().forEach(emailAccount -> {
            try {
                address.add(new InternetAddress(emailAccount.toString()));
            } catch (AddressException e) {
                e.printStackTrace();
            }
        });
        //设置收件人邮箱地址
        msg.setRecipients(Message.RecipientType.TO, address.toArray(new InternetAddress[address.size()]));
        //抄送
        if (Objects.nonNull(generateExcelSendEmailVo.getCcAddressList())) {
            List<InternetAddress> ccAddress = new ArrayList<>();
            generateExcelSendEmailVo.getCcAddressList().forEach(emailAccount -> {
                try {
                    ccAddress.add(new InternetAddress(emailAccount.toString()));
                } catch (AddressException e) {
                    e.printStackTrace();
                }
            });
            msg.setRecipients(Message.RecipientType.CC, ccAddress.toArray(new InternetAddress[ccAddress.size()]));
        }
        //密送
        if (Objects.nonNull(generateExcelSendEmailVo.getBccAddressList())) {
            List<InternetAddress> bccAddress = new ArrayList<>();
            generateExcelSendEmailVo.getBccAddressList().forEach(emailAccount -> {
                try {
                    bccAddress.add(new InternetAddress(emailAccount.toString()));
                } catch (AddressException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 初始化邮件配置
     * @param config
     * @return
     */
    public Session initProperties(EmailConfigVO config) {
        Properties props = new Properties();
        //连接协议
        props.put("mail.transport.protocol", "smtp");
        //SMTP主机名
        props.put("mail.smtp.host", config.getEmailSmtpHost());
        //SMTP端口号
        props.put("mail.smtp.port", config.getEmailSmtpPort());
        //授权
        props.put("mail.smtp.auth", "true");
        //设置是否使用ssl安全连接
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.fallback", "true");
        //设置是否在控制台显示debug信息
        props.put("mail.debug", "true");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getFromEmailAddress() ,config.getAuthCode());
            }
        });
    }

    /**
     * 重写用户密码
     */
//    public static class SimpleAuthenticator extends Authenticator {
//        private String username;
//        private String password;
//
//        public SimpleAuthenticator(String username, String password) {
//            super();
//            this.username = username;
//            this.password = password;
//        }
//
//        @Override
//        protected PasswordAuthentication getPasswordAuthentication() {
//            return new PasswordAuthentication(username, password);
//        }
//    }
}
