package com.wanmi.sbc.setting.api.response.imonlineservice;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>IM客服常用语</p>
 * @author zzg
 * @date 2023-08-24 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceChatResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分组ID")
    private Long chatId;

    /**
     * 消费者id
     */
    @ApiModelProperty(value = "消费者id")
    private String customerId;

    /**
     * 客服服务账号
     */
    @ApiModelProperty(value = "客服服务账号")
    private String serverAccount;
    /**
     * 客服服务账号
     */
    @ApiModelProperty(value = "客服昵称")
    private String serverAccountName;

    /**
     * 消费者IM账号
     */
    @ApiModelProperty(value = "消费者IM账号")
    private String customerImAccount;

    /**
     * App版本号
     */
    @ApiModelProperty(value = "App版本号")
    private String appVersion;

    /**
     * App所用手机品牌系统
     */
    @ApiModelProperty(value = "App所用手机品牌系统")
    private String appSysModel;

    /**
     * 消费者IP地址
     */
    @ApiModelProperty(value = "消费者IP地址")
    private String ipAddr;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long companyInfoId;

    /**
     * 聊天状态：0、聊天中；1、用户超时；2、客服超时；3、会话结束
     */
    @ApiModelProperty(value = "聊天状态：0、聊天中；1、用户超时；2、客服超时；3、会话结束")
    private Integer chatState;

    /**
     * 用户端APP手机类型：0、安卓；1、IOS
     */
    @ApiModelProperty(value = "用户端APP手机类型：0、安卓；1、IOS")
    private Integer appPlatform;

    /**
     * 最后聊天时间
     */
    @ApiModelProperty(value = "最后聊天时间")
    private Long msgTime;

    /**
     * 腾讯IM群组ID
     */
    @ApiModelProperty(value = "腾讯IM群组ID")
    private String imGroupId;

    /**
     * 开始聊天时间
     */
    private Long startTime;

    /**
     * 结束聊天时间
     */
    private Long endTime;

    /**
     * 聊天时长（秒）
     */
    private Long chatDuration;

    /**
     * 客服在线状态：0、在线；1、离线
     */
    @ApiModelProperty(value = "客服在线状态：0、在线；1、离线")
    private Integer serviceState;

    /**
     * 消息发送状态：0、用户发送等待客服回复；1、客服已回复等待用户
     */
    @ApiModelProperty(value = "消息发送状态：0、用户发送等待客服回复；1、客服已回复等待用户")
    private Integer sendState;

    /**
     * 客服超时信息发送状态：0、未超时；1、已发送超时一消息；2、已发送超时2消息
     */
    @ApiModelProperty(value = "客服超时信息发送状态：0、未超时；1、已发送超时一消息；2、已发送超时2消息")

    private Integer timeoutState;
    /**
     * 用户超时信息发送状态：0、未超时；1、已发送超时消息；
     */
    @ApiModelProperty(value = "用户超时信息发送状态：0、未超时；1、已发送超时消息；")
    private Integer userTimeoutState;

    @ApiModelProperty(value = "会话关闭时间")
    private String closeTime;

    @ApiModelProperty(value = "访问渠道：0、超级大白鲸APP")
    private Integer sourceChannel=0;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime = null;

    private Long createTimeSecond;


    /**
     * 来源店铺id
     */
    private Long sourceStoreId;

    /**
     * 来源公司ID
     */
    private Long sourceCompanyId;

    /**
     * 最后发送的一条非系统消息
     */
    private String lastMessage;

    /**
     * 星标状态：0、没有；1、已星标
     */
    private Integer markState = 0;
}
