package com.wanmi.sbc.goods.api.request.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsMarketingDeleteByMaketingIdAndGoodsInfoIdsRequest implements Serializable {

    /**
     * 用户编号
     */
    @ApiModelProperty(value = "营销ID")
    @NotNull
    private Long marketingId;

    /**
     * sku编号
     */
    @ApiModelProperty(value = "sku编号")
    @NotNull
    private String goodsInfoId;
}
