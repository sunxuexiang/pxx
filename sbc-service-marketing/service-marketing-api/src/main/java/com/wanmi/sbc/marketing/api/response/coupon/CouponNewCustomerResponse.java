package com.wanmi.sbc.marketing.api.response.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 新用户优惠券弹窗响应类
 * @author: XinJiang
 * @time: 2021/11/6 14:28
 */
@Data
@ApiModel
public class CouponNewCustomerResponse implements Serializable {

    private static final long serialVersionUID = 403674654487033774L;

    /**
     * 是否弹窗
     */
    @ApiModelProperty(value = "是否弹窗，默认否")
    private Boolean popupFlag = false;

    /**
     * 弹窗图片地址
     */
    @ApiModelProperty(value = "弹窗图片地址")
    private String imageUrl;
}
