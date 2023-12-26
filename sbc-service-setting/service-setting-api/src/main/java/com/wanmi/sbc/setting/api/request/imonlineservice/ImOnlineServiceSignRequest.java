package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-06 14:04
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceSignRequest implements Serializable {
    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 来源店铺id
     */
    @ApiModelProperty(value = "来源店铺id")
    private Long sourceStoreId;

    /**
     * 来源店铺id
     */
    @ApiModelProperty(value = "来源公司id")
    private Long sourceCompanyId;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userLogo;
    /**
     * 客服昵称
     */
    @ApiModelProperty(value = "客服昵称")
    private String customerServiceName;

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号")
    private String customerServiceAccount;

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号Id")
    private String customerId;

    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司Id")
    private Long companyId;

    /**
     * app登录用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "App版本号")
    private String appVersion;

    @ApiModelProperty(value = "App所用手机品牌系统")
    private String appSysModel;

    @ApiModelProperty(value = "消费者IP地址")
    private String ipAddr;

    @ApiModelProperty(value = "腾讯IM登录账号")
    private String customerImAccount;

    @ApiModelProperty(value = "用户端APP手机类型：0、安卓；1、IOS")
    private Integer appPlatform;

    @ApiModelProperty(value = "腾讯IM群组ID")
    private String imGroupId;

    private String phoneNo;

    @ApiModelProperty(value = "查询状态：0、可以接受聊天的登录在线客服；1、在线的客服")
    private Integer status = 0;
}
