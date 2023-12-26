package com.wanmi.sbc.customer.api.request.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description  用户日志
 * @author  shiy
 * @date    2023/4/7 11:07
 * @params  
 * @return  
*/
@ApiModel
@Data
public class CustomerLogAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 会员标识UUID
     */
    @ApiModelProperty(value = "会员标识UUID")
    private String customerId;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userNo;

    /**
     * 1APP登录
     */
    @ApiModelProperty(value = "1APP登录")
    private Integer logType;

    /**
     * 登录IP
     */
    @ApiModelProperty(value = "登录IP")
    private String userIp;

    /**
     * 1android,2ios
     */
    @ApiModelProperty(value = "1android,2ios")
    private Integer appType;

    /**
     * app版本
     */
    @ApiModelProperty(value = "app版本")
    private String appVersion;

    /**
     * 设备信息
     */
    @ApiModelProperty(value = "设备信息")
    private String devInfo;

    /**
     * mac地址
     */
    @ApiModelProperty(value = "mac地址")
    private String macAddr;
}