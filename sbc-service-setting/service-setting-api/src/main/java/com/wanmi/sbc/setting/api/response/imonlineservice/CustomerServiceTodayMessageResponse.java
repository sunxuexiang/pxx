package com.wanmi.sbc.setting.api.response.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>在线客服快捷回复常用语分组</p>
 * @author zhouzhenguo
 * @date 2023-09-02 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceTodayMessageResponse implements Serializable {

    private Long msgId;

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 发送账号
     */
    @ApiModelProperty(value = "发送账号")
    private String fromAccount;

    /**
     * 接受账号
     */
    @ApiModelProperty(value = "接受账号")
    private String toAccount;

    /**
     * IM群组ID
     */
    @ApiModelProperty(value = "IM群组ID")
    private String groupId;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private String messageType;

    /**
     * 消息的发送时间戳，单位为秒
     */
    @ApiModelProperty(value = "消息的发送时间戳，单位为秒")
    private Long msgTime;

    /**
     * 是否仅发送给在线用户标识。1代表仅发送给在线用户，否则为0
     */
    @ApiModelProperty(value = "是否仅发送给在线用户标识。1代表仅发送给在线用户，否则为0")
    private String onlineOnlyFlag;

    /**
     * 该条消息的发送结果，0表示发送成功，非0表示发送失败
     */
    @ApiModelProperty(value = "该条消息的发送结果，0表示发送成功，非0表示发送失败")
    private String sendMsgResult;

    @ApiModelProperty(value = "文件地址")
    private String fileUrl;

    /**
     * 发送类型：0、系统；1、客服；2、客户
     */
    private Integer sendType = 0;

    /**
     * 引用消息内容JSON格式
     */
    private String quoteMessage;
}
