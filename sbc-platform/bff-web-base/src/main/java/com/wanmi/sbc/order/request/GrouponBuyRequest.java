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
public class GrouponBuyRequest {

    /**
     * 商品skuId
     */
    @ApiModelProperty("商品skuId")
    @NotNull
    private String goodsInfoId;

    /**
     * 购买数量
     */
    @ApiModelProperty("购买数量")
    @NotNull
    private Long buyCount;

    /**
     * 是否开团购买(true:开团 false:参团 null:非拼团购买)
     */
    @ApiModelProperty("是否开团购买")
    @NotNull
    private Boolean openGroupon;

    /**
     * 团号
     */
    @ApiModelProperty("团号")
    private String grouponNo;

    @ApiModelProperty("商品erpNo")
    private String erpGoodsInfoNo;

}
