package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品信息增量库存传输对象
 * @author lipeng
 * @dateTime 2018/11/6 下午2:29
 */
@ApiModel
@Data
public class GoodsInfoUnlockStockDTO implements Serializable {

    private static final long serialVersionUID = 5343712189385908315L;

    /**
     * 库存数
     */
    @ApiModelProperty(value = "库存数")
    private BigDecimal stock;

    /**
     * 商品skuId
     */
    @ApiModelProperty(value = "商品skuId")
    private String goodsInfoId;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}
