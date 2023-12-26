package com.wanmi.sbc.setting.imonlineservice.root;

import lombok.Data;

import javax.persistence.*;

/**
 * <p>在线客服快捷回复常用语分组</p>
 * @author zhouzhenguo
 * @date 2023-09-02 16:10:28
 */
@Data
@Entity
@Table(name = "customer_service_today_message")
public class CustomerServiceTodayMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    private Long msgId;

    /**
     * 公司ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 发送账号
     */
    @Column(name = "from_account")
    private String fromAccount;

    /**
     * 接受账号
     */
    @Column(name = "to_account")
    private String toAccount;

    /**
     * IM群组ID
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 消息内容
     */
    @Column(name = "message")
    private String message;

    /**
     * 消息类型
     */
    @Column(name = "message_type")
    private String messageType;

    /**
     * 消息的发送时间戳，单位为秒
     */
    @Column(name = "msg_time")
    private Long msgTime;

    /**
     * 是否仅发送给在线用户标识。1代表仅发送给在线用户，否则为0
     */
    @Column(name = "online_only_flag")
    private String onlineOnlyFlag;

    /**
     * 该条消息的发送结果，0表示发送成功，非0表示发送失败
     */
    @Column(name = "send_msg_result")
    private String sendMsgResult;

    /**
     * 文件地址
     */
    @Column(name = "file_url")
    private String fileUrl;

    /**
     * 发送类型：0、系统；1、客服；2、客户
     */
    @Column(name = "send_type")
    private Integer sendType = 0;

    /**
     * 引用消息内容JSON格式
     */
    @Column(name = "quote_message")
    private String quoteMessage;
}
