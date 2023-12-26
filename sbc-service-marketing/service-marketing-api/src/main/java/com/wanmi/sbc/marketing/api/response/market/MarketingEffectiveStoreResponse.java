package com.wanmi.sbc.marketing.api.response.market;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品对应营销列表
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingEffectiveStoreResponse implements Serializable {
    private static final long serialVersionUID = 7293672850773285107L;


    @ApiModelProperty(value = "商家店铺Ids")
    private List<Long> storeIds;
}
