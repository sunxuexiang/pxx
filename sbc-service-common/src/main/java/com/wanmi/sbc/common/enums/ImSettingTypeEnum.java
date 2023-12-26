package com.wanmi.sbc.common.enums;

import com.wanmi.sbc.common.annotation.ApiEnum;


/**
 * @Description: 客服服务设置类型
 * @author: zhouzhenguo
 * @create: 2023-9-4 19:36
 * 设置类型：1、人工欢迎语；2、客户超时语一；3、结束会话说辞；4、接收离线消息账号；5、客户超时语二；6、客服不在线说辞
 */
@ApiEnum
public enum ImSettingTypeEnum {

    Welecome(1, "人工客服欢迎语"),
    TimeoutOne(2, "客服超时说辞"),
    Close(3, "客户超时下线提示"),
    OfflineAccount(4, "接收离线消息账号"),
    TimeoutTwo(5, "客户超时说辞"),
    Offline(6, "客服不在线说辞"),

    LIMIT(7, "客服接待人数上限设置"),

    HandClose(8, "客服手动结束会话设置"),

    Queue(9, "客户排队说辞设置"),

    SourceStore(10, "来源店铺")

    ;

    ImSettingTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;

    private String desc;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
