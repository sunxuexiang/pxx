package com.wanmi.sbc.message.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.message.bean.enums.MessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>App站内信消息未读通知、优惠活动消息数量</p>
 * @author zhouzhenguo
 * @date 2023-07-03 10:53:00
 */
@ApiModel
@Data
public class AppMessageUnreadVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String appMessageId;

    /**
     * 消息一级类型：0优惠促销、1服务通知
     */
    @ApiModelProperty(value = "消息一级类型：0优惠促销、1服务通知")
    private MessageType messageType;

    /**
     * 封面图
     */
    @ApiModelProperty(value = "封面图")
    private String imgUrl;

    /**
     * 消息标题
     */
    @ApiModelProperty(value = "消息标题")
    private String title;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String content;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间")
    private String sendTime;

    /**
     * 消息未读数量
     */
    @ApiModelProperty(value = "消息未读数量")
    private Integer unreadNum = 0;

    private Long sendTimeSecond = 0l;

    private String sendTimeStr = "";

}
