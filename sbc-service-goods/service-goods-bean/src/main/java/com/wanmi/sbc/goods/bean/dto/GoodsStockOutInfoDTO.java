package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * GoodsInfoDTO
 * 商品信息传输对象
 * @author lipeng
 * @dateTime 2018/11/6 下午2:29
 */
@ApiModel
@Data
public class GoodsStockOutInfoDTO implements Serializable {


    private static final long serialVersionUID = 643254300970702296L;
    /**
     * 商品SKU编号
     */
    @ApiModelProperty(value = "商品SKU编号")
    private String goodsInfoId;

    /**
     * 缺货数量
     */
    @ApiModelProperty(value = "缺货数量")
    private Long stockOutNum;

}