package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.walletorder.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.walletorder.bean.vo.TradeVO;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
//import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
//import com.wanmi.sbc.order.trade.model.root.Trade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-24 14:06
 */
@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletUseRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    /**
     * 使用余额
     */
    @ApiModelProperty(value = "使用余额")
    private BigDecimal walletBalance;

    @ApiModelProperty("客户信息")
    private CustomerVO customer;

    @ApiModelProperty("钱包信息")
    private CusWalletVO cusWalletVO;

//    @ApiModelProperty("订单信息")
//    private List<Trade> tradeList;

//    @ApiModelProperty("订单信息")
//    private List<NewPileTrade> newPileTradeList;

    @ApiModelProperty("订单信息")
    private List<TradeVO> tradeList;

    @ApiModelProperty("订单信息")
    private List<NewPileTradeVO> newPileTradeList;
}
