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
public class ActivityGiveGoodsRequest implements Serializable {
    /**
     *  赠品Id
     */
    @ApiModelProperty(value = "赠品Id")
    private String productId;

    /**
     *  赠品数量
     */
    @ApiModelProperty(value = "赠品数量")
    private Long productNum;

    /**
     *  营销id
     */
    @ApiModelProperty(value = "营销id")
    private Long marketingId;

    /**
     *  与库存数量二者取小值）
     *  限赠数量（只存总数，redis存剩余数量）
     */
    @ApiModelProperty(value = "赠品限量")
    private Long boundsNum;
}
