package com.wanmi.sbc.goods.api.response.merchantconfg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>商品推荐商品列表结果</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantConfigGoodsResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品推荐商品列表结果
     */
    @ApiModelProperty(value = "商品推荐商品列表结果")
    private List<String> goodsInfoIds;
    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
    /**
     * 商户ID
     */
    @ApiModelProperty(value = "商户ID")
    private Long 	companyInfoId;
}
