package com.wanmi.sbc.order.trade.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.GenerateExcelSendEmailVo;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerEmailVO;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class TradeEmailService {

    /**
     * 发送邮件
     */
//    public void sendMail(BaseResponse<EmailConfigQueryResponse> config, List<CustomerEmail> toEmailAddress,
//                         Trade tradeDetail, String url) {
    public void sendMail(BaseResponse<EmailConfigQueryResponse> config, List<CustomerEmailVO> toEmailAddress,
                         Trade tradeDetail, String url) {
        Session session = initProperties(config.getContext());
        try {
            MimeMessage msg = new MimeMessage(session);
            // 发信人邮箱及昵称
            msg.setFrom(new InternetAddress(config.getContext().getFromEmailAddress(), config.getContext().getFromPerson()));
            List<InternetAddress> address = new ArrayList<>();
            toEmailAddress.stream()
                    .forEach(customerEmail -> {
                        try {
                            address.add(new InternetAddress(customerEmail.getEmailAddress()));
                        } catch (AddressException e) {
                            e.printStackTrace();
                        }
                    });
            // 收信人邮箱地址
            msg.setRecipients(Message.RecipientType.TO, address.toArray(new InternetAddress[address.size()]));
            // 邮件标题
            msg.setSubject("订单支付通知");
            // 设置HTML内容
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            html.setContent(buildContent(tradeDetail, url), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            msg.setContent(mainPart);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            log.error("发送邮件通知财务错误:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化邮件配置
     *
     * @param config
     * @return
     */
    public Session initProperties(EmailConfigQueryResponse config) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");// 连接协议
        props.put("mail.smtp.host", config.getEmailSmtpHost());// SMTP主机名
        props.put("mail.smtp.port", config.getEmailSmtpPort());// SMTP端口号
        props.put("mail.smtp.auth", "true");// 授权
        props.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接
        props.put("mail.smtp.socketFactory.fallback", "true");
//        props.put("mail.debug", "true");// 设置是否在控制台显示debug信息
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getFromEmailAddress(), config.getAuthCode());
            }
        });
    }

    /**
     * 定义邮件内容模板
     *
     * @return
     */
    public String buildContent(Trade trade, String url) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        url = url + "/order-list";
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='width: 800px; margin: auto;'>")
                .append("<div style='padding-bottom: 50px;'>")
                .append("  <p>财务组：</p>")
                .append("  <p style='margin-left: 35px;'>于" + dtf.format(trade.getTradeState().getCreateTime()))
                .append("，生成1笔订单，请点击以下链接完成支付:</p>")
                .append("  <p style='margin-left: 35px;'><a href='" + url + "'>" + url + "</a></p>")
                .append("  <p>订单详细内容：</p>")
                .append("  <table style='border-collapse: collapse;font-size: 13px;'>")
                .append("    <tbody>")
                .append("      <tr>")
                .append("        <td style='text-align: center; padding: 10px; border: 1px solid #ddd;'>订单号："
                        + trade.getId() + "</td>")
                .append("        <td style='text-align: center; padding: 10px; border: 1px solid #ddd;'>下单时间："
                        + dtf.format(trade.getTradeState().getCreateTime()) + "</td>")
                .append("        <td style='text-align: center; padding: 10px; border: 1px solid #ddd;'>订单金额："
                        + trade.getTradePrice().getTotalPrice() + "</td>")
                .append("      </tr>")
                .append("      <tr>")
                .append("        <th style='text-align: center; padding: 10px; border: 1px solid #ddd;'>商品名称</th>")
                .append("        <th style='text-align: center; padding: 10px; border: 1px solid #ddd;'>数量</th>")
                .append("        <th style='text-align: center; padding: 10px; border: 1px solid #ddd;'></th>")
                .append("      </tr>");
        trade.getTradeItems().stream().forEach((item -> {
            content.append("<tr>")
                    .append("  <td style='text-align: center; padding: 10px; border: 1px solid #ddd;'>"
                            + item.getSkuName() + "</td>")
                    .append("  <td style='text-align: center; padding: 10px; border: 1px solid #ddd;'>"
                            + item.getNum() + "</td>")
                    .append("  <td style='text-align: center; padding: 10px; border: 1px solid #ddd;'></td>")
                    .append("</tr>");
        }));
        content.append("     </tbody>")
                .append("  </table>")
                .append("</div></body></html>");
        return content.toString();
    }



    /**
     * @Author      jiangzhou
     * @Date        2021/8/4  5:30 下午午
     * @Describetion 发送邮件的入口方法
     */
    public void sendOrderMail(BaseResponse<EmailConfigQueryResponse> config,List<String> initProperties, String text, String affixName, ByteArrayInputStream inputstream) {
        Session session = initProperties(config.getContext());
        try {
            MimeMessage msg = new MimeMessage(session);
            // 发信人邮箱及昵称
            msg.setFrom(new InternetAddress(config.getContext().getFromEmailAddress(), config.getContext().getFromPerson()));
            List<InternetAddress> address = new ArrayList<>();
            initProperties.stream()
                    .forEach(customerEmail -> {
                        try {
                            address.add(new InternetAddress(customerEmail));
                        } catch (AddressException e) {
                            e.printStackTrace();
                        }
                    });
            // 收信人邮箱地址
            msg.setRecipients(Message.RecipientType.TO, address.toArray(new InternetAddress[address.size()]));
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.DAY_OF_MONTH,0);
            String yyyyMM = new SimpleDateFormat("yyyy-MM").format(instance.getTime());
            String fileName = yyyyMM+"月订单信息.xls";
            // 邮件标题
            msg.setSubject(fileName);
            MimeBodyPart contentPart = (MimeBodyPart) createContent(text, inputstream, affixName);//参数为正文内容和附件流
            // 设置HTML内容
            Multipart mainPart = new MimeMultipart();
            mainPart.addBodyPart(contentPart);
            msg.setContent(mainPart);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            log.error("发送邮件通知运营人员错误:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendOrderMail2(BaseResponse<EmailConfigQueryResponse> config,List<String> initProperties, String text, String affixName, ByteArrayInputStream inputstream) {
        Session session = initProperties(config.getContext());
        try {
            MimeMessage msg = new MimeMessage(session);
            // 发信人邮箱及昵称
            msg.setFrom(new InternetAddress(config.getContext().getFromEmailAddress(), config.getContext().getFromPerson()));
            List<InternetAddress> address = new ArrayList<>();
            initProperties.stream()
                    .forEach(customerEmail -> {
                        try {
                            address.add(new InternetAddress(customerEmail));
                        } catch (AddressException e) {
                            log.error("发信人邮箱添加错误",e);
                        }
                    });
            // 收信人邮箱地址
            msg.setRecipients(Message.RecipientType.TO, address.toArray(new InternetAddress[address.size()]));
            String yesterdayDate = DateUtil.yesterdayDate();
            String titleName = yesterdayDate+" 的新用户首次订单";
            // 邮件标题
            msg.setSubject(titleName);
            MimeBodyPart contentPart = (MimeBodyPart) createContent(text, inputstream, affixName);//参数为正文内容和附件流
            // 设置HTML内容
            Multipart mainPart = new MimeMultipart();
            mainPart.addBodyPart(contentPart);
            msg.setContent(mainPart);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            log.error("发送邮件通知运营人员错误:" + e.getMessage());
        }
    }

    /**
     * @Author      jiangzhou
     * @Date        2021/8/4  5:40 下午午
     * @Describetion 发送邮件的入口方法
     */
    static Part createContent(String content, ByteArrayInputStream inputstream, String affixName) {
        MimeBodyPart contentPart = null;
        try {
            contentPart = new MimeBodyPart();
            MimeMultipart contentMultipart = new MimeMultipart("related");
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html;charset=gbk");
            contentMultipart.addBodyPart(htmlPart);
            //附件部分
            MimeBodyPart excelBodyPart = new MimeBodyPart();
            DataSource dataSource = new ByteArrayDataSource(inputstream, "application/excel");
            DataHandler dataHandler = new DataHandler(dataSource);
            excelBodyPart.setDataHandler(dataHandler);
            excelBodyPart.setFileName(MimeUtility.encodeText(affixName));
            contentMultipart.addBodyPart(excelBodyPart);
            contentPart.setContent(contentMultipart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentPart;
    }

}
