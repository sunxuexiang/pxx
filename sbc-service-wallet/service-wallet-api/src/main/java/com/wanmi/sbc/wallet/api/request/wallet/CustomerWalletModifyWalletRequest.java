package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.walletorder.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.walletorder.bean.vo.TradeVO;
//import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
//import com.wanmi.sbc.order.trade.model.root.Trade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class CustomerWalletModifyWalletRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    @ApiModelProperty("true增加余额 false扣减余额")
    private Boolean increaseDeductionFlag;

//    @ApiModelProperty("订单信息")
//    private List<Trade> tradeList;
//
//    @ApiModelProperty("订单信息")
//    private List<NewPileTrade> newPileTradeList;

    @ApiModelProperty("订单信息")
    private List<TradeVO> tradeList;

    @ApiModelProperty("订单信息")
    private List<NewPileTradeVO> newPileTradeList;
}
