package com.wanmi.sbc.customer.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 个人中心页商品收藏、店铺收藏、优惠券数量、钱包余额
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerFollowsCountResponse {

    @ApiModelProperty(value = "商品收藏数")
    private Long goodsFollow;

    @ApiModelProperty(value = "店铺收藏数")
    private Long storeFollow;

    @ApiModelProperty(value = "可用优惠券数")
    private Long unUseCount;

    @ApiModelProperty(value = "钱包余额")
    private BigDecimal balance;

}
