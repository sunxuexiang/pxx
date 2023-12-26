package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>腾讯返回的信息</p>
 * @Author shiGuangYi
 * @createDate 2023-06-20 11:28
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class UnreadMsgNumVO implements Serializable {
    @ApiModelProperty(value = "错误编码")
    private String actionStatus;
    @ApiModelProperty(value = "错误信息")
    private String errorInfo;
    @ApiModelProperty(value = "对应聊天后每个人的未读")
    private int errorCode;
    @ApiModelProperty(value = "对应聊天后每个人的未读")
    private List<C2CUnreadMsgNumListDO> c2CUnreadMsgNumList;
    @ApiModelProperty(value = " 未读总数，单独查询才有")
    private Integer  allC2CUnreadMsgNum;
}
