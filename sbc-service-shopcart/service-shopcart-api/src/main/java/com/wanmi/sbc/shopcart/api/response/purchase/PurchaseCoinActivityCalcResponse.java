package com.wanmi.sbc.shopcart.api.response.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/29 16:09
 */
@Data
@ApiModel
public class PurchaseCoinActivityCalcResponse {

    /**
     * 采购单参与营销活动的商品总数
     */
    @ApiModelProperty(value = "采购单参与营销活动的商品总数")
    private Long totalCount = 0L;

    /**
     * 采购单参与营销活动的商品总金额
     */
    @ApiModelProperty(value = "采购单参与营销活动的商品总金额")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 采购单参与营销活动的商品种类
     */
    @ApiModelProperty(value = "采购单参与营销活动的商品种类")
    private Integer totalSpecies = 0;


    @ApiModelProperty(value = "返鲸币总数")
    private BigDecimal totalCoinNum = BigDecimal.ZERO;

}
