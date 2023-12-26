package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>sku分仓库存表VO</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@ApiModel
@Data
public class GoodsWareStockDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * sku ID
     */
    @ApiModelProperty(value = "sku ID")
    private String goodsInfoId;

    /**
     * sku编码
     */
    @ApiModelProperty(value = "sku编码")
    private String goodsInfoNo;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID ")
    private Long wareId;

    /**
     * 货品库存
     */
    @ApiModelProperty(value = "货品库存")
    private BigDecimal stock;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * spu ID
     */
    @ApiModelProperty(value = "spu ID")
    private String goodsId;
}