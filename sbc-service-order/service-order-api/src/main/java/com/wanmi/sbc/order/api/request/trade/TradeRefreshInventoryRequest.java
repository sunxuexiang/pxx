package com.wanmi.sbc.order.api.request.trade;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeRefreshInventoryRequest implements Serializable {

    /**
     * 需要刷新库存skuId
     */
    @ApiModelProperty(value = "刷新库存SKUID")
    private List<String> skuIds;

}
