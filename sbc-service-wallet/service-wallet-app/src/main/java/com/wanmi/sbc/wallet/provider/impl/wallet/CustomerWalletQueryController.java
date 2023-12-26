package com.wanmi.sbc.wallet.provider.impl.wallet;


import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletPgListResponse;
import com.wanmi.sbc.wallet.bean.vo.WalletListVO;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.*;
import com.wanmi.sbc.wallet.bean.vo.CustomerWalletStoreIdVO;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWalletQuery;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletPageQueryRequest;
import com.wanmi.sbc.wallet.wallet.service.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@RestController
@Validated
@Slf4j
public class CustomerWalletQueryController implements CustomerWalletQueryProvider {

    @Autowired
    private CustomerWalletService customerWalletService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CustomerWalletTradeService customerWalletTradeService;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CustomerWalletMerchantService customerWalletMerchantService;
    @Autowired
    private WalletRecordService walletRecordService;
    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;
    @Autowired
    private CustomerWalletEveryDayDataService customerWalletEveryDayDataService;

    @Override
    public BaseResponse<BalanceByCustomerIdResponse> getBalanceByCustomerId(WalletByCustomerIdQueryRequest request) {
        // 客户信息
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerByIdNoThird(new CustomerGetByIdRequest
                (request.getCustomerId())).getContext();
        if (customer == null || (customer.getCustomerDetail().getCustomerStatus() == CustomerStatus.DISABLE)) {
            throw new SbcRuntimeException("K-010014");
        }

        CustomerWallet walletByCustomerId = customerWalletService.getWalletByCustomerId(request.getCustomerId());

        if (Objects.isNull(walletByCustomerId) || walletByCustomerId.getCustomerId() == null) {
            //初始化用户钱包
            BaseResponse<BalanceByCustomerIdResponse> balanceByCustomerIdResponseBaseResponse = customerWalletProvider.addUserWallet(
                    WalletByWalletIdAddRequest.builder().customerId(customer.getCustomerId()).customerAccount(customer.getCustomerAccount()).build());
            if (balanceByCustomerIdResponseBaseResponse == null || balanceByCustomerIdResponseBaseResponse.getCode().equals("K-000001")) {
                throw new SbcRuntimeException("初始化用户失败");
            }
            walletByCustomerId = KsBeanUtil.convert(balanceByCustomerIdResponseBaseResponse.getContext().getCusWalletVO(), CustomerWallet.class);
        }

        BalanceByCustomerIdResponse balanceByCustomerIdResponse = new BalanceByCustomerIdResponse();
        balanceByCustomerIdResponse.setCusWalletVO(KsBeanUtil.convert(walletByCustomerId, CusWalletVO.class));
        customerWalletService.assign(balanceByCustomerIdResponse.getCusWalletVO());

        return BaseResponse.success(balanceByCustomerIdResponse);
    }

    @Override
    public BaseResponse<CustomerWalletResponse> getCustomerWalletByCustomerAccount(WalletByCustomerAccountQueryRequest request) {
        CustomerWalletResponse response = new CustomerWalletResponse();
        CustomerWallet customerAccount = customerWalletService.getCustomerWalletByCustomerAccount(request.getCustomerAccount());
        CusWalletVO convert = KsBeanUtil.convert(customerAccount, CusWalletVO.class);
        response.setCusWalletVO(convert);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CustomerWalletResponse> getCustomerWalletByWalletId(WalletByWalletIdQueryRequest request) {
        CusWalletVO vo = KsBeanUtil.convert(customerWalletService.getCustomerWalletByWalletId(request.getWalletId()), CusWalletVO.class);
        CustomerWalletResponse response = new CustomerWalletResponse();
        response.setCusWalletVO(vo);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<BalanceCountResponse> getCustomerBalanceSum() {
        BalanceCountResponse response = new BalanceCountResponse(customerWalletService.getCustomerBalanceSum());
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<WalletAccountBalanceQueryResponse> getWalletAccountBalancePage(WalletAccountBalanceQueryRequest pageRequest) {
        CustomerWalletRequest request = new CustomerWalletRequest();
        KsBeanUtil.copyProperties(pageRequest, request);
        Page<CustomerWallet> customerWalletPage = customerWalletService.getWalletAccountBalancePage(request);
        Page<CusWalletVO> map = customerWalletPage.map(customerWallet -> {
            CusWalletVO vo = new CusWalletVO();
            KsBeanUtil.copyProperties(customerWallet, vo);
            return vo;
        });
        MicroServicePage<CusWalletVO> newPage = new MicroServicePage<>(map.getContent(), pageRequest.getPageable(), map.getTotalElements());
        WalletAccountBalanceQueryResponse response = new WalletAccountBalanceQueryResponse(newPage);
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse<CustomerWalletPgResponse> queryPageCustomerWallet(CustomerWalletRequest request) {
        CustomerWalletPageQueryRequest customerWalletPageQueryRequest = new CustomerWalletPageQueryRequest();
        KsBeanUtil.copyProperties(request, customerWalletPageQueryRequest);
        Page<CustomerWalletQuery> customerWalletQueries = customerWalletService.queryPageCustomerWallet(customerWalletPageQueryRequest);
        Page<CusWalletVO> map = customerWalletQueries.map(customerWalletQuery -> {
            CusWalletVO vo = new CusWalletVO();
            KsBeanUtil.copyProperties(customerWalletQuery, vo);
            return vo;
        });

        MicroServicePage<CusWalletVO> newPage = new MicroServicePage<>(map.getContent(), request.getPageable(), customerWalletQueries.getTotalElements());
        customerWalletService.assign(newPage);
        CustomerWalletPgResponse response = new CustomerWalletPgResponse(newPage);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CustomerWalletStorePgResponse> queryPageCustomerWalletSupplier( CustomerWalletSupplierRequest request) {
        return storeQueryProvider.walletPage(request);
    }

    /**
     * 查询当个充值详情接口
     * */
    @Override
    public BaseResponse<BalanceByCustomerDetailResponse> getBalanceByStoreId(WalletByCustomerIdQueryRequest request) {
        CustomerWalletStoreIdVO balanceByStoreId = walletRecordService.getBalanceByStoreId(request.getOrderNo());
        StoreByIdRequest storeByIdRequest = new StoreByIdRequest();
        storeByIdRequest.setStoreId(Long.valueOf(balanceByStoreId.getStoreId()));
        StoreByIdResponse context = storeQueryProvider.getById(storeByIdRequest).getContext();
        CompanyInfoByIdRequest companyInfoByIdRequest = new CompanyInfoByIdRequest();
        companyInfoByIdRequest.setCompanyInfoId(context.getStoreVO().getCompanyInfo().getCompanyInfoId());
        CompanyInfoByIdResponse context1 = companyInfoQueryProvider.getCompanyInfoById(companyInfoByIdRequest).getContext();
        balanceByStoreId.setSupplierAccount(context.getStoreVO().getContactMobile());
        balanceByStoreId.setSupplierName(context.getStoreVO().getSupplierName());
        balanceByStoreId.setStoreName(context.getStoreVO().getStoreName());
        balanceByStoreId.setSupplierCode(context1.getCompanyCodeNew());
        return BaseResponse.success(new BalanceByCustomerDetailResponse(balanceByStoreId));
    }

    /**
     * 查询所有钱包账户余额列表信息
     *
     * @param customerWalletRequest
     * @return
     */
    @Override
    public BaseResponse<List<CusWalletVO>> queryAllWalletAccountBalance(CustomerWalletRequest customerWalletRequest) {
        List<CusWalletVO> customerWalletList = customerWalletService.queryAllWalletAccountBalance(customerWalletRequest);
        customerWalletService.assignAll(customerWalletList);
        return BaseResponse.success(customerWalletList);
    }

    @Override
    public BaseResponse<CusWalletVO> checkoutWallet(CustomerWalletCheckoutRequest build) {
        CusWalletVO cusWalletVO = customerWalletTradeService.checkoutWallet(build);
        return BaseResponse.success(cusWalletVO);
    }

    @Override
    public BaseResponse<WalletCountResponse> queryWalletMoney(WalletByWalletIdAddRequest request){
        WalletCountResponse walletCountResponse = customerWalletMerchantService.queryWalletMoney(request);
        return BaseResponse.success(walletCountResponse);
    }

    @Override
    public BaseResponse<WalletCountResponse> queryWalletCountMoney() {
        return BaseResponse.success(customerWalletMerchantService.queryWalletCountMoney());
    }

    @Override
    public BaseResponse<CustomerWalletPgListResponse> userAndStoredList(WalletUserPageQueryRequest request) {
        Page<WalletListResponse> walletListResponses = customerWalletMerchantService.userList(request);
        Page<WalletListVO> map = walletListResponses.map(walletListResponse -> {
            WalletListVO walletListVO = new WalletListVO();
            KsBeanUtil.copyProperties(walletListResponse, walletListVO);
            return walletListVO;
        });
        MicroServicePage<WalletListVO> newPage = new MicroServicePage<>(map.getContent(),request.getPageable(),walletListResponses.getTotalElements());
        CustomerWalletPgListResponse response = new CustomerWalletPgListResponse(newPage);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<WalletInfoResponse> userByWalletId(WalletInfoRequest request) {

        return BaseResponse.success(customerWalletMerchantService.userByWalletId(request.getWalletId(),request.getStoreFlag()));
    }

    @Override
    public BaseResponse<byte[]> downLoadFile(DownLoadFileRequest request) throws IOException {

        return BaseResponse.success(customerWalletEveryDayDataService.getFile(request));
    }

    @Override
    public BaseResponse findWalletByBalance(CustomerWalletSchedQueryRequest customerWalletSchedQueryRequest) {
        try {
            log.info("生成鲸币余额明细报表开始==================={}",customerWalletSchedQueryRequest.getBalance());
            customerWalletEveryDayDataService.generateStoreCateExcel(customerWalletSchedQueryRequest.getBalance());
            log.info("生成鲸币余额明细报表结束");
        } catch (Exception e) {
            log.error("生成鲸币明细余额报表报错========={}",e);
        }

        return BaseResponse.SUCCESSFUL();
    }
}
