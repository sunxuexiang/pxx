package com.wanmi.sbc.shopcart.api.request.purchase;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ImportGoodsInfos implements Serializable {

    /**
     * 购买量
     */
    private Long buyCount = 0L;

    /**
     * 商品编号
     */
    private String goodsInfoId;

    /**
     * 商品库存
     */
    private Long stock;
}
