package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 智齿在线客服配置请求
 */
@Data
@ApiModel
public class SobotServiceRequest implements Serializable {

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    /**
     * 智齿开关状态：0、关闭；1、开启
     */
    @ApiModelProperty(value = "智齿开关状态：0、关闭；1、开启")
    private Integer switchStatus = 0;
}
