package com.wanmi.sbc.marketing.api.request.market;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddActivityGiveGoodsRequest implements Serializable {
    /**
     *  满赠多级促销Id
     */
    @ApiModelProperty(value = "满赠多级促销Id")
    private Long giftLevelId;

    /**
     *  营销id
     */
    @ApiModelProperty(value = "营销id")
    private Long marketingId;

    /**
     * 营销id
     */
    @ApiModelProperty(value = "商品信息")
    private List<ActivityGiveGoodsRequest> addActivitGoodsRequest;
}
