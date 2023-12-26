package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 获取客户id查询已确认订单商品快照请求结构
 * @Author: daiyitian
 * @Description:
 * @Date: 2018-11-16 16:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class TradeItemStockOutRequest implements Serializable {

    private static final long serialVersionUID = -1076979847505660373L;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;


    /**
     * 客户id
     */
    @ApiModelProperty(value = "wareId")
    @NotNull
    private Long wareId;

    /**
     * 城市code
     */
    @ApiModelProperty(value = "城市code")
    private String cityCode;

    @ApiModelProperty(value = "店铺id")
    private Long storeId;

}
