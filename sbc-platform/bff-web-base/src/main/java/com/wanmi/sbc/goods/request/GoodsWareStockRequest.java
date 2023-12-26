package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: GoodsWareStockRequest
 * @Description: TODO
 * @Date: 2020/6/2 18:18
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsWareStockRequest implements Serializable {

    @ApiModelProperty(value = "skuId")
    private String skuId;

    @ApiModelProperty(value = "仓id")
    private Long wareId;

    @ApiModelProperty(value = "是否匹配到仓")
    private Boolean matchWareHouseFlag;

    @ApiModelProperty(value = "parentSkuId")
    private String parentSkuId;
}
