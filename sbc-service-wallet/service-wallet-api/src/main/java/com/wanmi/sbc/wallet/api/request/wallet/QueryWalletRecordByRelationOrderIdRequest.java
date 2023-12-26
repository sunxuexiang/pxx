package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryWalletRecordByRelationOrderIdRequest extends BalanceBaseRequest {
    private static final long serialVersionUID = 7552669074470629283L;

    /**
     * 交易订单号
     */
    @ApiModelProperty(value = "交易订单号")
    private String relationOrderId;

    /**
     * 交易订单号
     */
    @ApiModelProperty(value = "交易规则")
    private String tradeRemark;
}
