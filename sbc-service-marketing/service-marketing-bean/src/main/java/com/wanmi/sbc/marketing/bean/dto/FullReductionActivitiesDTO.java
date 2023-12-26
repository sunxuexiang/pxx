package com.wanmi.sbc.marketing.bean.dto;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 满减活动商品
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class FullReductionActivitiesDTO implements Serializable {
    private static final long serialVersionUID = -4003413222837336874L;

    /**
     * 商品编号
     */
    private String skuIds;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @ApiModelProperty(value = "是否为必选商品")
    private BoolFlag whetherChoice;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;

    /**
     * 单用户限购量
     */
    @ApiModelProperty(value = "单用户限购数量")
    private Long perUserPurchaseNum;
}
