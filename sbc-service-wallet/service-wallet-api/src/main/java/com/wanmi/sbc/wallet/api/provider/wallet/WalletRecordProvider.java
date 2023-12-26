package com.wanmi.sbc.wallet.api.provider.wallet;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletUserPgListResponse;
import com.wanmi.sbc.wallet.api.response.wallet.WalletRecordResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 钱包记录表
 * @author jeffrey
 * @create 2021-08-21 8:49
 */

@FeignClient(value = "${application.wallet.name}", url="${feign.url.wallet:#{null}}", contextId = "WalletWalletRecordProvider")
public interface WalletRecordProvider {
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletRecordByCustomerwallet")
    BaseResponse<WalletRecordResponse> queryWalletRecordByCustomerAccount(@RequestBody @Valid QueryWalletRecordRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/addCustomerWallet")
    BaseResponse<AddWalletRecordResponse> addWalletRecord(@RequestBody @Valid AddWalletRecordRecordRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/addCustomerWalletBatch")
    BaseResponse addWalletRecordBatch(@RequestBody @Valid AddWalletRecordRecordBatchRequest request);

//    @PostMapping("/wallet/${application.wallet.version}/wallet/withdrawalDetails")
//    BaseResponse withdrawalDetails(@RequestBody @Valid WithdrawalDetailsRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletRecord")
    BaseResponse<WalletRecordResponse> queryWalletRecord(@RequestBody @Valid QueryWalletRecordRequest request);

    /**
     * 余额明细：分页查询所有记录,并按交易时间倒序排
     * @param request
     * @return
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryPageWalletRecord")
    BaseResponse<WalletRecordResponse> queryPageWalletRecord(@RequestBody WalletRecordRequest request);

    /**
     * 查询提现的交易记录
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletRecordByTradeType")
    BaseResponse<WalletRecordResponse> queryPageWalletRecordByTradeType(@RequestBody WalletRecordRequest request);

    /**
     * 余额明细,查看接口,传id查看
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletRecordByRecordNo")
    BaseResponse<WalletRecordResponse> getWalletRecordByRecordNo(@RequestBody WalletRecordRequest request);

    /**
     * 商品列表
     * @return
     */
//    @PostMapping("/wallet/${application.wallet.version}/wallet/virtualGoods")
//    BaseResponse<VirtualGoodsResponse> virtualGoods(@RequestBody @Valid VirtualGoodsRequest virtualGoodsRequest);

    /**
     * 查询交易记录，根据订单号
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/query-wallet-record-by-relation-order-id")
    BaseResponse<WalletRecordResponse> getWalletRecordByRelationOrderId(@RequestBody QueryWalletRecordByRelationOrderIdRequest request);

    /**
     * BOSS后台查看余额明细
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletRecord/backstage")
    BaseResponse<WalletRecordResponse> getQueryWalletRecord(@RequestBody WalletRecordRequest request);

    /**
     * BOSS后台查看余额明细
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/queryWalletRecord/queryPgWalletRecord")
    BaseResponse<WalletRecordResponse> queryPgWalletRecord(@RequestBody WalletRecordRequest request);

    /**
     * 查询交易记录，根据订单号，是否有过返回鲸币记录
     */
    @PostMapping("/wallet/${application.wallet.version}/wallet/query-wallet-record-by-relation-order-id-and-trade-remark")
    BaseResponse<WalletRecordResponse> getWalletRecordByRelationOrderIdAndTradeRemark(@RequestBody QueryWalletRecordByRelationOrderIdRequest request);

    @PostMapping("/wallet/${application.wallet.version}/wallet/queryRecordList")
    BaseResponse<CustomerWalletUserPgListResponse> queryRecordList(@RequestBody WalletRecordQueryRequest request);

    /**
     * 查询退单是否有退鲸币记录
     * @param rid
     * @return
     */
    @GetMapping("/wallet/${application.wallet.version}/wallet/queryReturnOrderExistWalletRecord")
    BaseResponse<Boolean> queryReturnOrderExistWalletRecord(@RequestParam("rid") String rid);

    @GetMapping("/wallet/${application.wallet.version}/wallet/refreshCustomerWalletRecordAll")
    BaseResponse refreshCustomerWalletRecordAll();
}

