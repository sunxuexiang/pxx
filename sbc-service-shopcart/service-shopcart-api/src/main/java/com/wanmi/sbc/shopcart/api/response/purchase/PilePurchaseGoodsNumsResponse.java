package com.wanmi.sbc.shopcart.api.response.purchase;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: 商品囤货数量响应类
 * @author: XinJiang
 * @time: 2021/11/12 11:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class PilePurchaseGoodsNumsResponse implements Serializable {

    private static final long serialVersionUID = -3837436963533864475L;

    /**
     * 商品囤货数量map，key->skuId，value->goodsNum
     */
    private Map<String,Long> goodsNumsMap;
}
