package com.wanmi.sbc.marketing.api.request.market.latest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingGiveGoodItemRequest implements Serializable {

    private static final long serialVersionUID = 6144389593672269419L;

    /**
     * 商品编号
     */
    @ApiModelProperty(value = "商品编号")
    @NotBlank
    private String skuId;
    /**
     *  赠品数量
     */
    @ApiModelProperty(value = "总限赠数量")
    private Long productNum;

    /**
     *  单人赠送数量
     */
    @ApiModelProperty(value = "单人限赠数量")
    private Long boundsNum;
}
