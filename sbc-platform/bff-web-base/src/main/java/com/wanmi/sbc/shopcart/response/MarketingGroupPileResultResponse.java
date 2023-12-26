package com.wanmi.sbc.shopcart.response;

import com.wanmi.sbc.shopcart.api.response.purchase.MarketingGroupCardResponse;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 购物车囤货商品列表返回结果
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingGroupPileResultResponse {

    private List<MarketingGroupCardResponse> result;
}
