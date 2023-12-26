package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 统计订单spu 的数据
 * @author sgy
 * @create 2021-08-07 16:43
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRecommendSkuCount implements Serializable {

    private static final long serialVersionUID = 01506206041445342716153L;

    @ApiModelProperty(value = "skuId")
    private List<String> skuId;
    @ApiModelProperty(value = "公司id")
    private String supplierId;
    @ApiModelProperty(value = "数量")
    private int count;
    @ApiModelProperty(value = "单个分割")
    private String skuIdOne;

    @ApiModelProperty(value = "时间")
    private String createTime;


}
