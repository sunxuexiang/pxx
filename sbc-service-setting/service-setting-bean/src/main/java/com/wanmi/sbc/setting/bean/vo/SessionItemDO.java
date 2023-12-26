package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
/**
 * <p>在线客服之拉取会话记录</p>
 * @Author shiGuangYi
 * @createDate 2023-06-19 20:31
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class SessionItemDO implements Serializable {
    @ApiModelProperty(value = "会话类型：1 表示 C2C 会话；2 表示 G2C 会话")
    private int type;
    @ApiModelProperty(value = "会话才会返回，返回会话方的 UserID")
    private String toAccount;
    @ApiModelProperty(value = "会话时间")
    private long msgTime;
    @ApiModelProperty(value = "置顶标记：0 标识普通会话；1 标识置顶会话")
    private int topFlag;
}
