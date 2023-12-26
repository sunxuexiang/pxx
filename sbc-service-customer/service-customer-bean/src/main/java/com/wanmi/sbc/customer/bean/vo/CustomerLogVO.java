package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description  用户日志表
 * @author  shiy
 * @date    2023/4/7 10:56
 * @params
 * @return
*/
@Data
public class CustomerLogVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */

    private Long id;

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

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 版本更新时间
     */
    @ApiModelProperty(value = "版本更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime versionUpdateTime;
}