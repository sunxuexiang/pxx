package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>sku分仓库存表VO</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
public class GoodsWareStockSimpDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    private Long wareId;

    /**
     * 货品库存
     */
    @ApiModelProperty(value = "货品库存")
    private Long stock;

    /**
     * 商品id+库存id
     */
    @ApiModelProperty("商品id+库存id")
    private String goodsInfoWareId;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;


}