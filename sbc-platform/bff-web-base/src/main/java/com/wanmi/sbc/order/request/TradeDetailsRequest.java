package com.wanmi.sbc.order.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * <p>订单商品快照验证请求结构</p>
 * Created by of628-wenzhi on 2017-07-13-上午9:15.
 */
@ApiModel
@Data
public class TradeDetailsRequest implements Serializable {

    private static final long serialVersionUID = -3106790833666168436L;
    
    /**
     * 订单ID，必传
     */
    @ApiModelProperty(value = "订单ID")
    @NotEmpty
    @Valid
    private List<String> tids;
}
