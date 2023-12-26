package com.wanmi.sbc.setting.imonlineservice.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>IM客服常用语</p>
 * @author zzg
 * @date 2023-08-24 16:10:28
 */
@Data
@Entity
@Table(name = "customer_service_chat")
public class CustomerServiceChat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * 消费者id
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 客服服务账号
     */
    @Column(name = "server_account")
    private String serverAccount;

    /**
     * 消费者IM账号
     */
    @Column(name = "customer_im_account")
    private String customerImAccount;

    /**
     * App版本号
     */
    @Column(name = "app_version")
    private String appVersion;

    /**
     * App所用手机品牌系统
     */
    @Column(name = "app_sys_model")
    private String appSysModel;

    /**
     * 消费者IP地址
     */
    @Column(name = "ip_addr")
    private String ipAddr;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 公司ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 聊天状态：0、聊天中；1、用户超时；2、客服超时；3、会话结束；4、排队中
     */
    @Column(name = "chat_state")
    private Integer chatState;

    /**
     * 用户端APP手机类型：0、安卓；1、IOS
     */
    @Column(name = "app_platform")
    private Integer appPlatform;

    /**
     * 最后聊天时间
     */
    @Column(name = "msg_time")
    private Long msgTime;

    /**
     * 开始聊天时间
     */
    @Column(name = "start_time")
    private Long startTime;

    /**
     * 聊天时长（秒）
     */
    @Column(name = "chat_duration")
    private Long chatDuration;


    /**
     * 结束聊天时间
     */
    @Column(name = "end_time")
    private Long endTime;

    /**
     * 腾讯IM群组ID
     */
    @Column(name = "im_group_id")
    private String imGroupId;

    /**
     * 客服在线状态：0、在线；1、离线
     */
    @Column(name = "service_state")
    private Integer serviceState;

    /**
     * 发送留言状态：0、未发送；1、已发送
     */
    @Column(name = "send_leave")
    private Integer sendLeave;

    /**
     * 消息发送状态：0、用户发送等待客服回复；1、客服已回复等待用户
     */
    @Column(name = "send_state")
    private Integer sendState;

    /**
     * 客服超时信息发送状态：0、未超时；1、已发送超时一消息；2、已发送超时2消息
     */
    @Column(name = "timeout_state")
    private Integer timeoutState;
    /**
     * 用户超时信息发送状态：0、未超时；1、已发送超时消息；
     */
    @Column(name = "user_timeout_state")
    private Integer userTimeoutState;

    /**
     * 店铺名称
     */
    @Column(name = "store_name")
    private String storeName;

    /**
     * 来源店铺id
     */
    @Column(name = "source_store_id")
    private Long sourceStoreId;

    /**
     * 来源公司ID
     */
    @Column(name = "source_company_id")
    private Long sourceCompanyId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 最后发送的一条非系统消息
     */
    @Column(name = "last_message")
    private String lastMessage;

    /**
     * 转接来源客服服务账号
     */
    @Column(name = "switch_source_account")
    private String switchSourceAccount;
}
