package com.wanmi.sbc.wallet.api.provider.wallet;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.BalanceByCustomerDetailResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletStorePgResponse;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 用户钱包查询接口
 */
@FeignClient(value = "${application.wallet.name}", url="${feign.url.wallet:#{null}}",contextId = "WalletMerchantProvider")
public interface WalletMerchantProvider {

    @PostMapping("/wallet/${application.wallet.version}/wallet/generateTicketAndRecord")
    BaseResponse<TicketsFormQueryVO> generateTicketAndRecord(@RequestBody CustomerWalletAddRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/payMerchantCallBack")
    void payMerchantCallBack(@RequestBody TradePayWalletOnlineCallBackRequest tradePayOnlineCallBackRequest);


    @PostMapping("/wallet/${application.wallet.version}/wallet/merchantGiveUser")
     BaseResponse<WalletRecordVO> merchantGiveUser(@RequestBody @Valid CustomerWalletGiveRequest request) throws SbcRuntimeException;

    @PostMapping("/wallet/${application.wallet.version}/wallet/withdrawal")
    BaseResponse withdrawal(@RequestBody @Valid InitiateWithdrawalRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/platoToStore")
    BaseResponse platoToStore(@RequestBody @Valid StoreAddRequest request) throws Exception;

    @PostMapping("/wallet/${application.wallet.version}/wallet/platRecharge")
    BaseResponse platRecharge(@RequestBody @Valid CustomerWalletOrderByRequest request) throws Exception;



}
