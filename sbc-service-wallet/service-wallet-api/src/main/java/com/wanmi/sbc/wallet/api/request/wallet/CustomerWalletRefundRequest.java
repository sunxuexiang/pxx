package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.common.base.BaseQueryRequest;
//import com.wanmi.sbc.order.refund.model.root.RefundOrder;
//import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
//import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
//import com.wanmi.sbc.order.trade.model.root.Trade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletRefundRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;


//    @ApiModelProperty(value = "订单")
//    private Trade trade;
//
//    @ApiModelProperty(value = "订单")
//    private NewPileTrade newPileTrade;
//
//    @ApiModelProperty(value = "退货单")
//    private ReturnOrder returnOrder;
//
//    @ApiModelProperty(value = "退款单")
//    private RefundOrder refundOrder;

    @ApiModelProperty(value = "订单id")
    private String id;

    @ApiModelProperty(value = "退单id")
    private String rid;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "买家id")
    private String buyerId;

    @ApiModelProperty(value = "会员账号")
    private String customerAccount;

    @ApiModelProperty(value = "交易备注")
    private String tradeRemark;

    @ApiModelProperty(value = "备注")
    private String remark;
}
