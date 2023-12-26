package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 营销活动具体数据
 * @author: XinJiang
 * @time: 2022/3/4 15:02
 */
@Data
@ApiModel
public class MarketingReductionLevelLabelVO implements Serializable {

    private static final long serialVersionUID = 9153081317414125675L;

    /**
     * 促销描述
     */
    @ApiModelProperty(value = "促销描述")
    private String marketingDesc;

    /**
     * 满足数量
     */
    @ApiModelProperty(value = "满足数量")
    private Integer number;

    /**
     * 金额
     */
    @ApiModelProperty(value = "满足数量")
    private BigDecimal amount;

    /**
     * 满折
     */
    @ApiModelProperty(value = "满折")
    private BigDecimal fullFold;

    /**
     *  满金额|数量后减多少元
     */
    @ApiModelProperty(value = "满金额|数量后减多少元")
    private BigDecimal reduction;

    /**
     * 满金额|数量后折扣
     */
    @ApiModelProperty(value = "满金额|数量后折扣")
    private BigDecimal discount;

    /**
     * 商品限购数量
     */
    @ApiModelProperty(value = "商品限购数量")
    private Integer goodsPurchasingNumber;
}
