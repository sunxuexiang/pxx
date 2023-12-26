package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class CustomerServiceChatRequest implements Serializable {

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
}
