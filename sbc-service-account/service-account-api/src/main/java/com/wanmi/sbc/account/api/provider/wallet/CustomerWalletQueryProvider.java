package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.offline.OfflineAccountAddRequest;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.offline.OfflineAccountAddResponse;
import com.wanmi.sbc.account.api.response.wallet.*;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.base.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户钱包查询接口
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "CustomerWalletQueryProvider")
public interface CustomerWalletQueryProvider {

    /**
     * 查询用户钱包余额
     *
     * @param request {@link WalletByCustomerIdQueryRequest}
     * @return 线下账户信息 {@link BalanceByCustomerIdResponse}
     */
    @PostMapping("/account/${application.account.version}/wallet/getBalanceByCustomerId")
    BaseResponse<BalanceByCustomerIdResponse> getBalanceByCustomerId(@RequestBody @Valid WalletByCustomerIdQueryRequest request);

    /**
     * 根据客户账户进行钱包查询
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/getCustomerWalletByCustomerAccount")
    BaseResponse<CustomerWalletResponse> getCustomerWalletByCustomerAccount(@RequestBody @Valid WalletByCustomerAccountQueryRequest request);

    /**
     * 获取钱包账户余额信息-带分页
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/getWalletAccountBalancePage")
    BaseResponse<WalletAccountBalanceQueryResponse> getWalletAccountBalancePage(@RequestBody @Valid WalletAccountBalanceQueryRequest request);

    @PostMapping("/account/${application.account.version}/wallet/getCustomerWalletByWalletId")
    BaseResponse<CustomerWalletResponse> getCustomerWalletByWalletId(@RequestBody @Valid WalletByWalletIdQueryRequest request);

    /**
     * 账户累计余额（未删除用户）
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/getCustomerBalanceAcount")
    BaseResponse<BalanceCountResponse> getCustomerBalanceSum();

    /**
     * 分页查询余额列表
     * @return
     */
    @PostMapping("/account/${application.account.version}/boss/wallet/queryPageCustomerWallet")
    BaseResponse<CustomerWalletPgResponse> queryPageCustomerWallet(@RequestBody CustomerWalletRequest request);

    /**
     * 查询所有钱包账户余额列表信息
     * @param customerWalletRequest
     * @return
     */
    @PostMapping("/account/${application.account.version}/boss/wallet/queryAllWalletAccountBalance")
    BaseResponse<List<CustomerWalletVO>> queryAllWalletAccountBalance(@RequestBody CustomerWalletRequest customerWalletRequest);

    /**
     * 查询符合提现的钱包账户余额列表信息
     * @return
     */
    @PostMapping("/account/${application.account.version}/boss/wallet/queryAutoWalletWithdrawaAccountBalance")
    BaseResponse<List<CustomerWalletVO>> queryAutoWalletWithdrawaAccountBalance();
}
