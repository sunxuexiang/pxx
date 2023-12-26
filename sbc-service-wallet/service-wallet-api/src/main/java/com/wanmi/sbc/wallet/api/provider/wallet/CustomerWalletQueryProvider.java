package com.wanmi.sbc.wallet.api.provider.wallet;

import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.*;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 用户钱包查询接口
 */
@FeignClient(value = "${application.wallet.name}",url="${feign.url.wallet:#{null}}", contextId = "WalletCustomerWalletQueryProvider")
public interface CustomerWalletQueryProvider {

    /**
     * 查询用户钱包余额
     *
     * @param request {@link WalletByCustomerIdQueryRequest}
     * @return 线下账户信息 {@link BalanceByCustomerIdResponse}
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/getBalanceByCustomerId")
    BaseResponse<BalanceByCustomerIdResponse> getBalanceByCustomerId(@RequestBody @Valid WalletByCustomerIdQueryRequest request);

    /**
     * 根据客户账户进行钱包查询
     *
     * @param request
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/getCustomerWalletByCustomerAccount")
    BaseResponse<CustomerWalletResponse> getCustomerWalletByCustomerAccount(@RequestBody @Valid WalletByCustomerAccountQueryRequest request);

    /**
     * 获取钱包账户余额信息-带分页
     *
     * @param request
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/getWalletwalletBalancePage")
    BaseResponse<WalletAccountBalanceQueryResponse> getWalletAccountBalancePage(@RequestBody @Valid WalletAccountBalanceQueryRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/getCustomerWalletByWalletId")
    BaseResponse<CustomerWalletResponse> getCustomerWalletByWalletId(@RequestBody @Valid WalletByWalletIdQueryRequest request);

    /**
     * 账户累计余额（未删除用户）
     *
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/getCustomerBalanceAcount")
    BaseResponse<BalanceCountResponse> getCustomerBalanceSum();

    /**
     * 分页查询余额列表
     *
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryPageCustomerWallet")
    BaseResponse<CustomerWalletPgResponse> queryPageCustomerWallet(@RequestBody CustomerWalletRequest request);


    @PostMapping("/wallet/${application.wallet.version}/wallet/queryPageCustomerWalletSupplier")
    BaseResponse<CustomerWalletStorePgResponse> queryPageCustomerWalletSupplier(@RequestBody CustomerWalletSupplierRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/getBalanceByStoreId")
    BaseResponse<BalanceByCustomerDetailResponse> getBalanceByStoreId(@RequestBody @Valid WalletByCustomerIdQueryRequest request);


    /**
     * 查询所有钱包账户余额列表信息
     *
     * @param customerWalletRequest
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/queryAllWalletwalletBalance")
    BaseResponse<List<CusWalletVO>> queryAllWalletAccountBalance(@RequestBody CustomerWalletRequest customerWalletRequest);

    /**
     * 校验
     *
     * @param build
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/checkoutWallet")
    BaseResponse<CusWalletVO> checkoutWallet(@RequestBody @Valid CustomerWalletCheckoutRequest build);

    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/queryWalletMoney")
    BaseResponse<WalletCountResponse> queryWalletMoney(@RequestBody @Valid WalletByWalletIdAddRequest request);

    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/queryWalletCountMoney")
    BaseResponse<WalletCountResponse> queryWalletCountMoney();

    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/userAndStoredList")
    BaseResponse<CustomerWalletPgListResponse> userAndStoredList(@RequestBody @Valid WalletUserPageQueryRequest request);

    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/userByWalletId")
    BaseResponse<WalletInfoResponse> userByWalletId(@RequestBody @Valid WalletInfoRequest request);

    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/findWalletByBalance")
    BaseResponse findWalletByBalance(@RequestBody @Valid CustomerWalletSchedQueryRequest customerWalletSchedQueryRequest);

    @PostMapping("/wallet/${application.wallet.version}/boss/wallet/downLoadFile")
    BaseResponse<byte[]> downLoadFile(@RequestBody @Valid DownLoadFileRequest request) throws IOException;
}
