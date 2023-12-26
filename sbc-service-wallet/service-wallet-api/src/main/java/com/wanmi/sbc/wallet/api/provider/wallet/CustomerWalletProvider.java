package com.wanmi.sbc.wallet.api.provider.wallet;

import com.wanmi.sbc.wallet.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.wallet.api.response.wallet.WalletStatusResponse;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.walletorder.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.walletorder.bean.vo.TradeVO;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletTradePriceResponse;
import com.wanmi.sbc.common.base.BaseResponse;
//import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
//import com.wanmi.sbc.order.trade.model.root.Trade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户钱包查询接口
 */
@FeignClient(value = "${application.wallet.name}",url="${feign.url.wallet:#{null}}", contextId = "WalletCustomerWalletProvider")
public interface CustomerWalletProvider {

    /**
     * 修改钱包信息
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/updateCustomerWalletByWalletId")
    BaseResponse updateCustomerWalletByWalletId(@RequestBody @Valid CustomerWalletModifyRequest request);

    /**
     * 用户发起提现
     * @param request
     * @return
     */
//    @PostMapping("/wallet/${application.wallet.version}/wallet/initiateWithdrawal")
//    BaseResponse initiateWithdrawal(@RequestBody @Valid InitiateWithdrawalRequest request);

    /**
     * 修复数据用
     * @param request
     * @return
     */
//    @PostMapping("/wallet/${application.wallet.version}/wallet/InitiateWithdrawalWithoutCheck")
//    BaseResponse initiateWithdrawalWhthoutCheck(@RequestBody InitiateWithdrawalWithoutCheckRequest request);

    /**
     * 用户发起取消提现
     * @param request
     * @return
     */
//    @PostMapping("/wallet/${application.wallet.version}/wallet/cancelWithdrawal")
//    BaseResponse cancelWithdrawal(@RequestBody @Valid CancelWithdrawalRequest request);

    /**
     * 新增用户钱包
     *
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/addUserWallet")
    BaseResponse<BalanceByCustomerIdResponse> addUserWallet(@RequestBody WalletByWalletIdAddRequest request);

    /**
     * 用户支付, 使用余额
     *
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/useCustomerWallet")
    BaseResponse<CustomerWalletTradePriceResponse> useCustomerWallet(@Valid @RequestBody CustomerWalletTradePriceRequest customerWalletTradePriceRequest);

    /**
     * 初始化会员资金统计缓存
     *
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/balancePay")
    BaseResponse balancePay(@RequestBody @Valid WalletRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/addAmount")
    BaseResponse addAmount(@RequestBody @Valid WalletRequest request);

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse
     * @Author lvzhenwei
     * @Description 增加账户余额
     * @Date 14:58 2019/7/16
     * @Param [request]
     **/
    @PostMapping("/wallet/${application.wallet.version}/wallet/add-amount")
    BaseResponse addAmount(@RequestBody @Valid CustomerAddAmountRequest request);

    //------------钱包和订单退单相关接口 start--------------//
    @PostMapping("/wallet/${application.wallet.version}/wallet/useWallet")
    BaseResponse<List<TradeVO>> useWallet(@RequestBody @Valid CustomerWalletUseRequest customer);

    @PostMapping("/wallet/${application.wallet.version}/wallet/useWalletForNewPileTrade")
    BaseResponse<List<NewPileTradeVO>> useWalletForNewPileTrade(@RequestBody @Valid CustomerWalletUseRequest customer);

    @PostMapping("/wallet/${application.wallet.version}/wallet/modifyWalletBalance")
    BaseResponse<List<TradeVO>> modifyWalletBalance(@RequestBody @Valid CustomerWalletModifyWalletRequest customerWalletModifyWalletRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/modifyWalletBalanceForNewPileTrade")
    BaseResponse<List<NewPileTradeVO>> modifyWalletBalanceForNewPileTrade(@RequestBody @Valid CustomerWalletModifyWalletRequest customerWalletModifyWalletRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/modifyWalletBalanceForRefund")
    void modifyWalletBalanceForRefund(@RequestBody @Valid CustomerWalletRefundRequest customerWalletRefundRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/awardWalletBalance")
    BaseResponse<AddWalletRecordResponse> awardWalletBalance(@RequestBody @Valid CustomerWalletAwardRequest customerWalletAwardRequest);

    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletStatus")
    BaseResponse<WalletStatusResponse> queryWalletStatus(@RequestBody @Valid WalletByWalletIdAddRequest walletByWalletIdAddRequest);
    //------------钱包和订单退单相关接口 end--------------//

    @PostMapping("/wallet/${application.wallet.version}/wallet/queryCustomerWallet")
    BaseResponse<CusWalletVO> queryCustomerWallet(@RequestBody @Valid WalletInfoRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/orderByGiveStore")
    BaseResponse<WalletRecordVO> orderByGiveStore(@RequestBody @Valid CustomerWalletOrderByRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/updateWalletDelById")
    BaseResponse updateWalletDelById(@RequestBody @Valid WalletRequest request);


}
