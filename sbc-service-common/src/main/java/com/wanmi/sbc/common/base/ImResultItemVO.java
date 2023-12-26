package com.wanmi.sbc.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-18 9:38
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImResultItemVO implements Serializable {

    @ApiModelProperty(value = "im需要查询的账号")
    private String UserID;
    @ApiModelProperty(value = "返回编码")
    private int ResultCode;
    @ApiModelProperty(value = "返回信息")
    private String ResultInfo;
    @ApiModelProperty(value = "状态信息")
    private String AccountStatus;
}
