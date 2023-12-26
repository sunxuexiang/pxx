package com.wanmi.sbc.order.api.response.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 优惠金额明细响应类
 * @author lm
 * @date 2022/12/09 10:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "优惠金额明细响应类")
public class DiscountPriceDetailResponse {

    @ApiModelProperty("商品名称")
    private String goodsInfoName;

    @ApiModelProperty("商品规格")
    private String goodsSubtitle;

    @ApiModelProperty("购买数量")
    private Long buyNum;

    @ApiModelProperty("商品金额")
    private BigDecimal totalPrice;

    @ApiModelProperty("商品图片")
    private String goodsImageUrl;

    @ApiModelProperty("活动名称")
    private String activityName;

    @ApiModelProperty("折扣金额")
    private BigDecimal discountPrice;

}
