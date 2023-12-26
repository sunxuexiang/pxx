package com.wanmi.sbc.imMessage.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImMessageResponse {

    private String fromAccount;

    /**
     * 单聊消息：接收方方账号
     */
    private String toAccount;

    /**
     * 群聊消息：群ID
     */
    private String groupId;

    /**
     * 消息发送时间
     */
    private Long msgTimestamp;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 文件URL地址
     */
    private String fileUrl;

    /**
     * 发送类型：0、系统；1、客服；2、客户
     */
    private Integer sendType;

    private String serverNick;

}
