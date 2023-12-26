package com.wanmi.sbc.goods.api.response.goods;

import lombok.Data;

import java.util.List;

/**
 * 商品SKU编辑视图响应
 * Created by daiyitian on 2017/3/24.
 */
@Data
public class GoodsInfoUpdateResponse {

    private List<String> skuIds;

}
