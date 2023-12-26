package com.wanmi.sbc.shopcart.api.response.purchase;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 购物车返回视图
 */

@Data
@ApiModel
public class ShopCarViewResponse implements Serializable {

    private static final long serialVersionUID = -1568352064104571291L;

    List<MarketingAndGoodsResponse> marketingAndGoodsResponses;

}
