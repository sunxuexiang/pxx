package com.wanmi.sbc.goods.api.request.goods;

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
public class GoodsLackStockRequest implements Serializable {

    @ApiModelProperty(value = "erpno")
    private String erpGoodsNo;

    @ApiModelProperty(value = "取货的数量")
    private Long lackNum;
}
