package com.wanmi.sbc.live.chat.request;

import lombok.Data;

/**
 * 创建群组请求实体
 */
@Data
public class GroupCreatRequest {
    private String Owner_Account;//群主的 UserId（选填）
    private String Type;//群组类型：Private/Public/ChatRoom/AVChatRoom/Community
    private String Name;// 群名称（必填）
    private String Introduction;//群简介（选填）
    private String Notification;// 群公告（选填）
    private String FaceUrl;// 群头像 URL（选填）
    private Long MaxMemberCount;//最大群成员数量（选填
}
