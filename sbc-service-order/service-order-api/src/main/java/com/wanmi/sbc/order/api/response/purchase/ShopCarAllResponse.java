package com.wanmi.sbc.order.api.response.purchase;

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
@AllArgsConstructor
@NoArgsConstructor
public class ShopCarAllResponse implements Serializable {

    @ApiModelProperty(value = "购物车最终返回结果")
    private List<MarketingGroupCardResponse> result;
}
