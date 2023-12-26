package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.*;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.account.wallet.model.root.CustomerWalletQuery;
import com.wanmi.sbc.account.wallet.request.CustomerWalletPageQueryRequest;
import com.wanmi.sbc.account.wallet.service.CustomerWalletService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;


@RestController
@Validated
public class CustomerWalletQueryController implements CustomerWalletQueryProvider {

    @Autowired
    private CustomerWalletService customerWalletService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;


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
            walletByCustomerId = KsBeanUtil.convert(balanceByCustomerIdResponseBaseResponse.getContext().getCustomerWalletVO(), CustomerWallet.class);
        }

        BalanceByCustomerIdResponse balanceByCustomerIdResponse = new BalanceByCustomerIdResponse();
        balanceByCustomerIdResponse.setCustomerWalletVO(KsBeanUtil.convert(walletByCustomerId, CustomerWalletVO.class));
        customerWalletService.assign(balanceByCustomerIdResponse.getCustomerWalletVO());

        return BaseResponse.success(balanceByCustomerIdResponse);
    }

    @Override
    public BaseResponse<CustomerWalletResponse> getCustomerWalletByCustomerAccount(WalletByCustomerAccountQueryRequest request) {
        CustomerWalletResponse response = new CustomerWalletResponse();
        CustomerWallet customerAccount = customerWalletService.getCustomerWalletByCustomerAccount(request.getCustomerAccount());
        CustomerWalletVO convert = KsBeanUtil.convert(customerAccount, CustomerWalletVO.class);
        response.setCustomerWalletVO(convert);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CustomerWalletResponse> getCustomerWalletByWalletId(WalletByWalletIdQueryRequest request) {
        CustomerWalletVO vo = KsBeanUtil.convert(customerWalletService.getCustomerWalletByWalletId(request.getWalletId()), CustomerWalletVO.class);
        CustomerWalletResponse response = new CustomerWalletResponse();
        response.setCustomerWalletVO(vo);
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
        Page<CustomerWalletVO> map = customerWalletPage.map(customerWallet -> {
            CustomerWalletVO vo = new CustomerWalletVO();
            KsBeanUtil.copyProperties(customerWallet, vo);
            return vo;
        });
        MicroServicePage<CustomerWalletVO> newPage = new MicroServicePage<>(map.getContent(), pageRequest.getPageable(), map.getTotalElements());
        WalletAccountBalanceQueryResponse response = new WalletAccountBalanceQueryResponse(newPage);
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse<CustomerWalletPgResponse> queryPageCustomerWallet(CustomerWalletRequest request) {
        CustomerWalletPageQueryRequest customerWalletPageQueryRequest = new CustomerWalletPageQueryRequest();
        KsBeanUtil.copyProperties(request, customerWalletPageQueryRequest);
        Page<CustomerWalletQuery> customerWalletQueries = customerWalletService.queryPageCustomerWallet(customerWalletPageQueryRequest);
        Page<CustomerWalletVO> map = customerWalletQueries.map(customerWalletQuery -> {
            CustomerWalletVO vo = new CustomerWalletVO();
            KsBeanUtil.copyProperties(customerWalletQuery, vo);
            return vo;
        });

        MicroServicePage<CustomerWalletVO> newPage = new MicroServicePage<>(map.getContent(), request.getPageable(), customerWalletQueries.getTotalElements());
        customerWalletService.assign(newPage);
        CustomerWalletPgResponse response = new CustomerWalletPgResponse(newPage);
        return BaseResponse.success(response);
    }

    /**
     * 查询所有钱包账户余额列表信息
     * @param customerWalletRequest
     * @return
     */
    @Override
    public BaseResponse<List<CustomerWalletVO>> queryAllWalletAccountBalance(CustomerWalletRequest customerWalletRequest) {
        List<CustomerWalletVO> customerWalletList = customerWalletService.queryAllWalletAccountBalance(customerWalletRequest);
        customerWalletService.assignAll(customerWalletList);
        return BaseResponse.success(customerWalletList);
    }

    @Override
    public BaseResponse<List<CustomerWalletVO>> queryAutoWalletWithdrawaAccountBalance() {
        List<CustomerWalletVO> customerWalletList = customerWalletService.queryAutoWalletWithdrawaAccountBalance();
        customerWalletService.assignAll(customerWalletList);
        return BaseResponse.success(customerWalletList);
    }

}
