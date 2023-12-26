package com.wanmi.sbc.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: gaomuwei
 * @Date: Created In 上午10:49 2019/5/24
 * @Description:
 */
@ApiModel
@Data
public class DeliveryHomeFlagRequest {

    /**
     * 商品skuId
     */
    @ApiModelProperty("收货地址id")
    @NotNull
    private String customerDeliveryAddressId;

    /**
     * 商品skuId
     */
    @ApiModelProperty("仓库id")
    private Long wareId;




}
