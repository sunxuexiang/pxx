package com.wanmi.sbc.account.api.provider.wallet;

import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.account.api.response.wallet.VirtualGoodsResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletRecordResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 钱包记录表
 * @author jeffrey
 * @create 2021-08-21 8:49
 */

@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "WalletRecordProvider")
public interface WalletRecordProvider {
    @PostMapping("/account/${application.account.version}/wallet/queryWalletRecordByCustomerAccount")
    BaseResponse<WalletRecordResponse> queryWalletRecordByCustomerAccount(@RequestBody @Valid QueryWalletRecordRequest request);

    @PostMapping("/account/${application.account.version}/wallet/addCustomerWallet")
    BaseResponse<AddWalletRecordResponse> addWalletRecord(@RequestBody @Valid AddWalletRecordRecordRequest request);

    @PostMapping("/account/${application.account.version}/wallet/addCustomerWalletBatch")
    BaseResponse addWalletRecordBatch(@RequestBody @Valid AddWalletRecordRecordBatchRequest request);

    @PostMapping("/account/${application.account.version}/wallet/withdrawalDetails")
    BaseResponse withdrawalDetails(@RequestBody @Valid WithdrawalDetailsRequest request);

    @PostMapping("/account/${application.account.version}/wallet/queryWalletRecord")
    BaseResponse<WalletRecordResponse> queryWalletRecord(@RequestBody @Valid QueryWalletRecordRequest request);

    /**
     * 余额明细：分页查询所有记录,并按交易时间倒序排
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/queryPageWalletRecord")
    BaseResponse<WalletRecordResponse> queryPageWalletRecord(@RequestBody WalletRecordRequest request);

    /**
     * 查询提现的交易记录
     */
    @PostMapping("/account/${application.account.version}/wallet/queryWalletRecordByTradeType")
    BaseResponse<WalletRecordResponse> queryPageWalletRecordByTradeType(@RequestBody WalletRecordRequest request);

    /**
     * 余额明细,查看接口,传id查看
     */
    @PostMapping("/account/${application.account.version}/wallet/queryWalletRecordByRecordNo")
    BaseResponse<WalletRecordResponse> getWalletRecordByRecordNo(@RequestBody WalletRecordRequest request);

    /**
     * 商品列表
     * @return
     */
    @PostMapping("/account/${application.account.version}/wallet/virtualGoods")
    BaseResponse<VirtualGoodsResponse> virtualGoods(@RequestBody @Valid VirtualGoodsRequest virtualGoodsRequest);

    /**
     * 查询交易记录，根据订单号
     */
    @PostMapping("/account/${application.account.version}/wallet/query-wallet-record-by-relation-order-id")
    BaseResponse<WalletRecordResponse> getWalletRecordByRelationOrderId(@RequestBody QueryWalletRecordByRelationOrderIdRequest request);

    /**
     * BOSS后台查看余额明细
     */
    @PostMapping("/account/${application.account.version}/wallet/queryWalletRecord/backstage")
    BaseResponse<WalletRecordResponse> getQueryWalletRecord(@RequestBody WalletRecordRequest request);

    /**
     * BOSS后台查看余额明细
     */
    @PostMapping("/account/${application.account.version}/wallet/queryWalletRecord/queryPgWalletRecord")
    BaseResponse<WalletRecordResponse> queryPgWalletRecord(@RequestBody WalletRecordRequest request);

    /**
     * 查询交易记录，根据订单号，是否有过返回鲸币记录
     */
    @PostMapping("/account/${application.account.version}/wallet/query-wallet-record-by-relation-order-id-and-trade-remark")
    BaseResponse<WalletRecordResponse> getWalletRecordByRelationOrderIdAndTradeRemark(@RequestBody QueryWalletRecordByRelationOrderIdRequest request);
}
