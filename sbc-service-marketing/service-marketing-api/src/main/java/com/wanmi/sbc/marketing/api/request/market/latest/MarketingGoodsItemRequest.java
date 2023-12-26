package com.wanmi.sbc.marketing.api.request.market.latest;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 营销活动中选择的商品范围
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingGoodsItemRequest implements Serializable {

    private static final long serialVersionUID = -8189987703740512851L;

    /**
     * 商品编号
     */
    @ApiModelProperty(value = "商品编号")
    private String skuId;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @ApiModelProperty(value = "是否为必选商品")
    private BoolFlag whetherChoice;

    /**
     * 总限购量
     */
    @ApiModelProperty(value = "总限购量")
    private Long purchaseNum;

    /**
     * 单用户限购量
     */
    @ApiModelProperty(value = "单用户限购数量")
    private Long perUserPurchaseNum;
}
