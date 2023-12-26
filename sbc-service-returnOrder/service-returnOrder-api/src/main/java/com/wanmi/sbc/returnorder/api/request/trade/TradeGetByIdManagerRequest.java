package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.enums.AccountType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeGetByIdManagerRequest {
    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private String tid;

    /**
     * 店铺id
     */
    private Long storeId;

    private AccountType requestFrom;

}
