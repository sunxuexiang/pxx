package com.wanmi.sbc.wallet.api.request.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductWhaleCoinsAddRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    @ApiModelProperty(value = "商家ID，集合(有多商家得情况)")
    private Map<String,BigDecimal> storeBalance;
    @ApiModelProperty(value = "用户Id")
    private String customerId;

}
