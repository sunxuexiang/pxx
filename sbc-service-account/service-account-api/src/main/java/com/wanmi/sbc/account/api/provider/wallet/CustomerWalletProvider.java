package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.api.response.wallet.CustomerWalletTradePriceResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 用户钱包查询接口
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "CustomerWalletProvider")
public interface CustomerWalletProvider {

    /**
     * 修改钱包信息
     *
     */
    @PostMapping("/account/${application.account.version}/wallet/updateCustomerWalletByWalletId")
    BaseResponse updateCustomerWalletByWalletId(@RequestBody @Valid CustomerWalletModifyRequest request);

    /**
     * 用户发起提现
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/initiateWithdrawal")
    BaseResponse initiateWithdrawal(@RequestBody @Valid InitiateWithdrawalRequest request);

    /**
     * 修复数据用
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/InitiateWithdrawalWithoutCheck")
    BaseResponse initiateWithdrawalWhthoutCheck(@RequestBody InitiateWithdrawalWithoutCheckRequest request);

    /**
     * 用户发起取消提现
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/cancelWithdrawal")
    BaseResponse cancelWithdrawal(@RequestBody @Valid CancelWithdrawalRequest request);

    /**
     * 新增用户钱包
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/addUserWallet")
    BaseResponse<BalanceByCustomerIdResponse> addUserWallet(@RequestBody WalletByWalletIdAddRequest request);

    /**
     * 用户支付, 使用余额
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/useCustomerWallet")
    BaseResponse<CustomerWalletTradePriceResponse> useCustomerWallet(@Valid @RequestBody CustomerWalletTradePriceRequest customerWalletTradePriceRequest);

    /**
     * 初始化会员资金统计缓存
     *
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/balancePay")
    BaseResponse balancePay(@RequestBody @Valid WalletRequest request);

    @PostMapping("/account/${application.account.version}/wallet/addAmount")
    BaseResponse addAmount(@RequestBody @Valid WalletRequest request);

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse
     * @Author lvzhenwei
     * @Description 增加账户余额
     * @Date 14:58 2019/7/16
     * @Param [request]
     **/
    @PostMapping("/account/${application.account.version}/wallet/add-amount")
    BaseResponse addAmount(@RequestBody @Valid CustomerAddAmountRequest request);

    @PostMapping("/account/${application.account.version}/wallet/modifyWalletBalanceForRefundV2")
    BaseResponse modifyWalletBalanceForRefundV2(@RequestBody @Valid ModifyWalletBalanceForRefundRequest modifyRequest);

    @PostMapping("/account/${application.account.version}/wallet/aotuWalletWithdrawalBalance")
    BaseResponse aotuWalletWithdrawalBalance();

    @PostMapping("/account/${application.account.version}/wallet/modifyWalletBalanceForCoin")
    BaseResponse modifyWalletBalanceForCoin(@RequestBody @Valid ModifyWalletBalanceForCoinActivityRequest request);
}
