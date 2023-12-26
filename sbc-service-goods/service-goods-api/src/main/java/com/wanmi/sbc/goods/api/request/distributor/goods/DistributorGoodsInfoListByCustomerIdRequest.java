package com.wanmi.sbc.goods.api.request.distributor.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分销员商品-根据分销员的会员ID查询分销员商品对象
 * @author: Geek Wang
 * @createDate: 2019/2/28 14:22
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributorGoodsInfoListByCustomerIdRequest implements Serializable {

    /**
     * 分销员对应的会员ID
     */
    @ApiModelProperty(value = "分销员对应的会员ID")
    private String customerId;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;
}
