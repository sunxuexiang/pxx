package com.wanmi.sbc.pay;

import com.wanmi.sbc.order.api.provider.trade.PileTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.trade.TradePayOnlineCallBackRequest;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.TradePayWalletOnlineCallBackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @ClassName PayCallBackService
 * @Description TODO
 * @Author lvzhenwei
 * @Date 2020/7/9 19:46
 **/
@Service
public class PayCallBackTaskService {

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private PileTradeProvider pileTradeProvider;
    @Autowired
    private WalletMerchantProvider walletMerchantProvider;

    @Async("payCallBack")
    public void payCallBack(TradePayOnlineCallBackRequest request){
        tradeProvider.payOnlineCallBack(request);
    }

    @Async("payRechargeCallBack")
    public void payRechargeCallBack(TradePayOnlineCallBackRequest request){
        tradeProvider.payOnlineRechargeCallBack(request);
    }


    @Async("payTakeGoodCallBack")
    public void payTakeGoodCallBack(TradePayOnlineCallBackRequest request){
        tradeProvider.payOnlineTakeGoodCallBack(request);
    }

    @Async("payPileOrderCallBack")
    public void payPileOrderCallBack(TradePayOnlineCallBackRequest request){
        pileTradeProvider.payPileOrderCallBack(request);
    }
    @Async("payMerchantCallBack")
    public void payMerchantCallBack(TradePayWalletOnlineCallBackRequest request){
        walletMerchantProvider.payMerchantCallBack(request);
    }
}
