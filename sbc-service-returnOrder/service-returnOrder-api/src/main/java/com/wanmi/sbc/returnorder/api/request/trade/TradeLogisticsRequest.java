package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: yinxianzhi
 * @Date: Created In 下午3:28 2019/5/20
 */
@ApiModel
@Data
public class TradeLogisticsRequest extends BaseRequest {

    /**
     * 积分商品id
     */
    @ApiModelProperty("订单ID")
    @NotNull
    private String logisticsId;
}
