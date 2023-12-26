package com.wanmi.sbc.common.util.tencent;


/**
 * 腾讯IM消息类型
 */
public enum TencentImMessageType {

    JingBiRecharge(13, "鲸币充值支付成功"),
    UpdateChatList(14, "更新聊天列表"),
    ImChatQueue(15, "腾讯IM客服聊天排队更新"),
    PushAdPayRes(16, "推送广告在线支付结果"),
    LiveNotify(18, "直播状态更新通知")
    ;

    private Integer msgType;
    private String msgDesc;

    private TencentImMessageType(Integer type, String desc) {
        this.msgType = type;
        this.msgDesc = desc;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public String getMsgDesc() {
        return msgDesc;
    }
}
