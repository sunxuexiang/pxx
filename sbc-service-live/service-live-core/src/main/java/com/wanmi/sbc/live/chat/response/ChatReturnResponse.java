package com.wanmi.sbc.live.chat.response;

import lombok.Data;

/**
 * 聊天接口返回结果实体
 */
@Data
public class ChatReturnResponse {
    private String ActionStatus;
    private String ErrorInfo;
    private Integer ErrorCode;
    private String GroupId;
    private Long OnlineMemberNum;
}
