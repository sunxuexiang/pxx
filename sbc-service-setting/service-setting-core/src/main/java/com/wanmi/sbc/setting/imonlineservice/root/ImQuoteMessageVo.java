package com.wanmi.sbc.setting.imonlineservice.root;


import lombok.Data;

/**
 * 聊天引用消息
 */
@Data
public class ImQuoteMessageVo {

    private String msgId;

    private String msgContent;

    private String sender;

    private Long msgTimestamp;
}
