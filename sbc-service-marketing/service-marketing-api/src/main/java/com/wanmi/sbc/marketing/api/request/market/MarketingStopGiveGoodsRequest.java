package com.wanmi.sbc.marketing.api.request.market;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingStopGiveGoodsRequest implements Serializable {
    /**
     * 促销Id
     */
    @ApiModelProperty(value = "促销Id")
    private Long marketingId;

    @ApiModelProperty(value = "活动赠品id")
    private String giftDetailId;

    @ApiModelProperty(value = "商品id")
    private String productId;
}
