package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.account.api.request.AccountBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryWalletRecordRequest extends AccountBaseRequest {
    /**
     * 交易单号
     */
    @ApiModelProperty(value = "交易单号")
    private String recordNo;

    @ApiModelProperty(value = "用户账户")
    private String customerAccount;
}
