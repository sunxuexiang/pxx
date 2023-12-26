package com.wanmi.sbc.marketing.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 套装购买参与营销商品详情信息
 * @author: XinJiang
 * @time: 2022/2/4 16:04
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingSuitDetialDTO {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long suitId;

    /**
     * 套装id
     */
    @ApiModelProperty(value = "套装id")
    private Long marketingId;

    /**
     * 促销商品id
     */
    @ApiModelProperty(value = "促销商品id")
    private String goodsInfoId;

    /**
     * 套装内商品的营销活动id
     */
    @ApiModelProperty(value = "套装内商品的营销活动id")
    private Long goodsMarketingId;

    /**
     * 套装内商品买赠（赠品id）
     */
    @ApiModelProperty(value = "套装内商品买赠（赠品id）")
    private String giftId;
}
