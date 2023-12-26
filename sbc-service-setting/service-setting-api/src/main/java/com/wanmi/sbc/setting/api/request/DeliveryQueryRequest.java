package com.wanmi.sbc.setting.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询快递详情入参
 * Created by CHENLI on 2017/5/25.
 */
@ApiModel
@Data
public class DeliveryQueryRequest extends SettingBaseRequest {
    private static final long serialVersionUID = 6609359011848476436L;
    /**
     * 快递公司代码
     */
    @ApiModelProperty(value = "快递公司代码")
    private String companyCode;

    /**
     * 快递单号
     */
    @ApiModelProperty(value = "快递单号")
    private String deliveryNo;

    /**
     * 收、寄件人的电话号码
     */
    @ApiModelProperty(value = "收、寄件人的电话号码")
    private String phone;

    /**
     * 出发地城市，省-市-区
     */
    private String from;
    /**
     * 目的地城市，省-市-区
     */
    private String to;
}
