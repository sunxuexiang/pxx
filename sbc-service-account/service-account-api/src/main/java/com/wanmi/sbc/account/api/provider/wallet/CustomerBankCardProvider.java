package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.BankCardPageRequest;
import com.wanmi.sbc.account.api.request.wallet.BankCardSendMobilCodeRequest;
import com.wanmi.sbc.account.api.request.wallet.BankCardValidateSendMobileCodeRequest;
import com.wanmi.sbc.account.api.request.wallet.CustomerBankCardRequest;
import com.wanmi.sbc.account.api.response.wallet.*;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description: 用户银行卡操作接口
 * @author: jiangxin
 * @create: 2021-08-20 11:45
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}",contextId = "CustomerBankCardProvider")
public interface CustomerBankCardProvider {

    /**
     * 绑定银行卡
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/bindBankCard")
    BaseResponse<BankCardResponse> bindBankCard(@RequestBody @Valid CustomerBankCardRequest request);

    /**
     * 修改银行卡信息
     * @param request
     * @return
     */
    @PutMapping("/account/${application.account.version}/wallet/updateBankCard")
    BaseResponse<BankCardResponse> updateBankCard(@RequestBody @Valid CustomerBankCardRequest request);

    /**
     * 删除或解绑银行卡
     * @param request
     * @return
     */
    @DeleteMapping("/account/${application.account.version}/wallet/delOrUnboundBankCard")
    BaseResponse<BankCardResponse> delOrUnboundBankCard(@RequestBody @Valid CustomerBankCardRequest request);

    /**
     * 查询银行卡列表
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/getBankCardsByWalletId")
    BaseResponse<BankCardInfoListResponse> getBankCardsByWalletId(@RequestBody @Valid CustomerBankCardRequest request);

    /**
     * 银行卡详细信息
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/getBankCardByCode")
    BaseResponse<BankCardResponse> getBankCardByCode(@RequestBody @Valid CustomerBankCardRequest request);

    /**
     * 分页查询银行卡信息
     * @param pageRequest
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/getBankCardsPage")
    BaseResponse<BankCardPageResponse> getBankCardsPage(@RequestBody @Valid BankCardPageRequest pageRequest);

    /**
     * 是否可以发送短信验证码
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/validateSendMobileCode")
    BaseResponse<BankCardValidateSendMobileCodeResponse> validateSendMobileCode(@RequestBody @Valid BankCardValidateSendMobileCodeRequest request);

    /**
     * 发送短信验证码
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/sendMobileCode")
    BaseResponse<BankCardSendMobilCodeResponse> sendMobileCode(@RequestBody @Valid BankCardSendMobilCodeRequest request);

    /**
     * 是否绑定银行卡
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/isBindingBankCard")
    BaseResponse<Integer> isBindingBankCard(@RequestBody @Valid CustomerBankCardRequest request);

}
