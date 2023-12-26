package com.wanmi.sbc.goods.api.response.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by yang on 2021/1/22.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoStockByIdsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * goodsInfoStockMap
     */
    @ApiModelProperty(value = "goodsInfoStockMap")
    private Map<String, BigDecimal> goodsInfoStockMap;
}
