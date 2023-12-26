package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>腾讯会话参数</p>
 * @Author shiGuangYi
 * @createDate 2023-06-19 20:36
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class RecentUserInfoVO implements Serializable {

    @ApiModelProperty(value = "会话参数")
    private List<UserProfileItemDO> userProfileItem;
    @ApiModelProperty(value = "会话参数")
    private String actionStatus;
    @ApiModelProperty(value = "会话参数")
    private int errorCode;
    @ApiModelProperty(value = "会话参数")
    private String errorInfo;
    @ApiModelProperty(value = "会话参数")
    private String errorDisplay;

}
