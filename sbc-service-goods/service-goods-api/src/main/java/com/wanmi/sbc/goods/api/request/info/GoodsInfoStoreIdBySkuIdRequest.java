package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品SKU列表条件查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoStoreIdBySkuIdRequest implements Serializable {

    private static final long serialVersionUID = 2381622561026000775L;


    /**
     * skuId
     */
    private String skuId;
}
