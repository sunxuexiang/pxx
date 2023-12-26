package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: GoodsWareStockRequest
 * @Description: TODO
 * @Date: 2020/6/2 18:18
 * @Version: 1.0
 */
@ApiModel
@Data
public class PointsGoodsWareStockRequest implements Serializable {

    @ApiModelProperty(value = "积分商品id")
    private String pointsGoodsId;

    @ApiModelProperty(value = "仓id")
    private Long wareId;
}
