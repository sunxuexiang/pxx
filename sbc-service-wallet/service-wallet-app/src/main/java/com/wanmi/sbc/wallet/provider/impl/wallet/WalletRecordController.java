package com.wanmi.sbc.wallet.provider.impl.wallet;

import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletUserPgListResponse;
import com.wanmi.sbc.wallet.api.response.wallet.WalletRecordResponse;
import com.wanmi.sbc.wallet.bean.vo.ExtractInfoVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.wallet.api.request.wallet.WalletRecordQueryRequest;
import com.wanmi.sbc.wallet.wallet.service.CustomerWalletService;
import com.wanmi.sbc.wallet.wallet.service.WalletRecordMerchantService;
import com.wanmi.sbc.wallet.wallet.service.WalletRecordService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱包记录表
 * @author jeffrey
 * @create 2021-08-21 8:45
 */


@RestController
@Validated
public class WalletRecordController implements WalletRecordProvider {

    @Autowired
    private WalletRecordRepository walletRecordRepository;

    @Autowired
    private WalletRecordService walletRecordService;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private CustomerWalletService customerWalletService;

    @Autowired
    private WalletRecordMerchantService walletRecordMerchantService;

    @Override
    public BaseResponse<WalletRecordResponse> queryWalletRecordByCustomerAccount(QueryWalletRecordRequest request) {
        List<WalletRecordVO> collect = KsBeanUtil.convert(walletRecordRepository.getWalletRecordByCustomerAccount(request.getCustomerAccount()), WalletRecordVO.class);
        return BaseResponse.success(WalletRecordResponse.builder().walletRecordVOs(collect).build());
    }

    @Override
    public BaseResponse<AddWalletRecordResponse> addWalletRecord(AddWalletRecordRecordRequest request) {
        return BaseResponse.success(walletRecordService.addWalletRecord(request));
    }

    @Override
    public BaseResponse addWalletRecordBatch(AddWalletRecordRecordBatchRequest request) {
        return BaseResponse.success(walletRecordService.addWalletRecordBatch(request));
    }

/*
    */
/**
     * 移动端提现详情页面
     * @return
     *//*

    @Override
    public BaseResponse<WithdrawalDetailsResponse> withdrawalDetails(WithdrawalDetailsRequest request) {
        return BaseResponse.success(walletRecordService.withdrawalDetails(request));
    }
*/

    @Override
    public BaseResponse<WalletRecordResponse> queryWalletRecord(QueryWalletRecordRequest request) {
        List<WalletRecordVO> vos = new ArrayList<>();
        vos.add(KsBeanUtil.convert(walletRecordRepository.getWalletRecordByRecordNo(request.getRecordNo()),WalletRecordVO.class));
        return BaseResponse.success(WalletRecordResponse.builder().walletRecordVOs(vos).build());
    }

    /**
     * 余额明细,页查询所有记录,并按交易时间倒序排,分页
     * @param request
     * @return
     */
    @Override
    public BaseResponse<WalletRecordResponse> queryPageWalletRecord(WalletRecordRequest request) {
        WalletRecordQueryRequest walletRecordQueryRequest = KsBeanUtil.convert(request,WalletRecordQueryRequest.class);
        WalletRecordResponse response = walletRecordService.listByCustomerAccount(walletRecordQueryRequest);
        return BaseResponse.success(response);
    }

    /**
     * 查询所有提现的交易记录
     *
     * @return
     */
    @Override
    public BaseResponse<WalletRecordResponse> queryPageWalletRecordByTradeType(WalletRecordRequest request) {
        //先根据用户ID判断是否在钱包记录表中并查出其帐户
        CustomerWallet customerWallet = customerWalletService.findByCustomerId(request.getCustomerAccountId());
        if (customerWallet == null) {
            return BaseResponse.FAILED();
        }
        request.setCustomerAccount(customerWallet.getCustomerAccount());
        Page<ExtractInfoVO> extractInfoResponses = walletRecordService.queryWalletRecordByTradeType(request);
        List<ExtractInfoVO> extractInfoList = extractInfoResponses.getContent();
        MicroServicePage<ExtractInfoVO> extractInfoResponses1 = new MicroServicePage<>(extractInfoList, request.getPageable(), extractInfoResponses.getTotalElements());
        return BaseResponse.success(WalletRecordResponse.builder().extractInfoPageList(extractInfoResponses1).build());
    }

    /**
     * 余额明细,查看接口,传id查看
     */
    @Override
    public BaseResponse<WalletRecordResponse> getWalletRecordByRecordNo(WalletRecordRequest request) {
        WalletRecordResponse response = walletRecordService.findWalletRecordByRecordNo(request);
        return BaseResponse.success(response);
    }

/*    *//**
     * 查询商品列表
     * @return
     *//*
    @Override
    public BaseResponse<VirtualGoodsResponse> virtualGoods(VirtualGoodsRequest virtualGoodsRequest) {
        return BaseResponse.success(walletRecordService.virtualGoods(virtualGoodsRequest));
    }*/

    @Override
    public BaseResponse<WalletRecordResponse> getWalletRecordByRelationOrderId(QueryWalletRecordByRelationOrderIdRequest request) {
        WalletRecordResponse response = walletRecordService.getWalletRecordByRelationOrderId(request);
        return BaseResponse.success(response);
    }

    /**
     * 运营后台查看余额明细
     */
    @Override
    public BaseResponse<WalletRecordResponse> getQueryWalletRecord(WalletRecordRequest request) {
        Page<WalletRecordVO> queryWalletRecordList = walletRecordService.getQueryWalletRecordList(request);
        List<WalletRecordVO> walletRecordVOS = queryWalletRecordList.getContent();
        MicroServicePage<WalletRecordVO> extractInfoResponses1 = new MicroServicePage<>(walletRecordVOS, request.getPageable(), queryWalletRecordList.getTotalElements());
        return BaseResponse.success(WalletRecordResponse.builder().pageList(extractInfoResponses1).build());
    }

    /**
     * 运营后台查看余额明细
     */
    @Override
    public BaseResponse<WalletRecordResponse> queryPgWalletRecord(WalletRecordRequest request) {
        Page<WalletRecordVO> queryWalletRecordList = walletRecordService.queryPgWalletRecord(request);
        List<WalletRecordVO> walletRecordVOS = queryWalletRecordList.getContent();
        MicroServicePage<WalletRecordVO> extractInfoResponses1 = new MicroServicePage<>(walletRecordVOS, request.getPageable(), queryWalletRecordList.getTotalElements());
        return BaseResponse.success(WalletRecordResponse.builder().pageList(extractInfoResponses1).build());
    }

    @Override
    public BaseResponse<WalletRecordResponse> getWalletRecordByRelationOrderIdAndTradeRemark(QueryWalletRecordByRelationOrderIdRequest request) {
        WalletRecordResponse response = walletRecordService.getWalletRecordByRelationOrderIdAndTradeRemark(request);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CustomerWalletUserPgListResponse> queryRecordList(WalletRecordQueryRequest request) {

        return walletRecordMerchantService.queryRecordList(request);
    }

    @Override
    public BaseResponse<Boolean> queryReturnOrderExistWalletRecord(String rid) {
        return BaseResponse.success(walletRecordService.queryReturnOrderExistWalletRecord(rid));
    }

    @Override
    public BaseResponse refreshCustomerWalletRecordAll() {
        walletRecordMerchantService.refreshCustomerWalletRecordAll();
        return BaseResponse.SUCCESSFUL();
    }
}
