package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.annotation.CanEmpty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品SKU实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingDiscountDetailsVO implements Serializable {

    private static final long serialVersionUID = 3973312626887597962L;


    /**
     * 满减金额
     */
    @ApiModelProperty(value = "满减金额")
    @CanEmpty
    private BigDecimal fullMinusAmount;

    /**
     * 立减金额
     */
    @ApiModelProperty(value = "立减金额")
    @CanEmpty
    private BigDecimal NowReductionAmount;


    /**
     * 满折金额
     */
    @ApiModelProperty(value = "满折金额")
    @CanEmpty
    private BigDecimal discountAmount;


    /**
     * 立折金额
     */
    @ApiModelProperty(value = "立折金额")
    @CanEmpty
    private BigDecimal NowdiscountAmount;

}