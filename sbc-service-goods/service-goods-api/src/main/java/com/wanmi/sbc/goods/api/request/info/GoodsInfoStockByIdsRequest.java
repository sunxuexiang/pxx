package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品库存查询
 * Created by yang on 2021/1/22.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoStockByIdsRequest implements Serializable {


    private static final long serialVersionUID = 374390865967923525L;
    /**
     * 商品信息
     */
    @ApiModelProperty(value = "商品信息")
    private List<GoodsInfoStockRequest> goodsInfo;

    /**
     * 零售商品信息
     */
    @ApiModelProperty(value = "零售商品信息")
    private List<GoodsInfoStockRequest> retailGoodsInfo;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

}
