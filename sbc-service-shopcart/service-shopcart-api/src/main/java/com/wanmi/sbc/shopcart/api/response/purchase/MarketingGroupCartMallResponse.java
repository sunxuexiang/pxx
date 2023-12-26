package com.wanmi.sbc.shopcart.api.response.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 促销分组响应结果
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingGroupCartMallResponse implements Serializable {

    private static final long serialVersionUID = -2023683352977805713L;

    @ApiModelProperty(value = "购物车商品列表")
    List<MarketingGroupCardResponse> marketingGroupCardResponseList;

}
