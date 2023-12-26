package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.WalletStatusResponse;
import com.wanmi.sbc.wallet.bean.enums.JingBiState;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWalletQuery;
import com.wanmi.sbc.wallet.wallet.model.root.WalletRecord;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletQueryRepository;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByCustomerIdsRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional(readOnly = true, timeout = 10)
@Slf4j
public class CustomerWalletService {


    @Autowired
    private CustomerWalletRepository customerWalletRepository;

    @Autowired
    private CustomerWalletQueryRepository customerWalletQueryRepository;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;


    @Autowired
    private WalletRecordService walletRecordService;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;


    /**
     * 查询账户余额
     */
    public CustomerWallet getWalletByCustomerId(String customerId) {

        return customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByCustomerId(customerId)).orElse(null);
    }

    public CustomerWallet getCustomerWalletByCustomerAccount(String customerAccount) {
        return customerWalletRepository.getCustomerWalletByCustomerAccount(customerAccount);
    }

    /**
     * 查询钱包可以用余额 page分页
     *
     * @param request
     * @return
     */
    public Page<CustomerWallet> getWalletAccountBalancePage(CustomerWalletRequest request) {
        return customerWalletRepository.findAll(CustomerWalletPageBuilder.build(request), request.getPageRequest());
    }

    public CustomerWallet getCustomerWalletByWalletId(Long walletId) {
        return customerWalletRepository.getCustomerWalletByWalletId(walletId);
    }

    /**
     * 获取钱包账户累计余额
     *
     * @return
     */
    public BigDecimal getCustomerBalanceSum() {
        return customerWalletRepository.getCustomerBalanceSum();
    }


    /**
     * 查询用户是否拥有钱包账户
     */
    public CustomerWallet findByCustomerId(String customerId) {
        return customerWalletRepository.findByCustomerId(customerId);
    }

    /**
     * 查询钱包 page分页
     *
     * @return
     */
    public Page<CustomerWalletQuery> queryPageCustomerWallet(CustomerWalletPageQueryRequest customerWalletPageQueryRequest) {
        Page<Object> all = customerWalletQueryRepository.listPageCustomerWallet(
                customerWalletPageQueryRequest.getCustomerName()
                , customerWalletPageQueryRequest.getCustomerAccount()
                , customerWalletPageQueryRequest.getRecentlyTicketsTimeStart()
                , customerWalletPageQueryRequest.getRecentlyTicketsTimeEnd()
                , customerWalletPageQueryRequest.getUseBalance()
                , customerWalletPageQueryRequest.getCustomerFundsIdList()
                , customerWalletPageQueryRequest.getPageRequest());
        List<CustomerWalletQuery> customerWalletQueries = resultToCustomerWalletQueryList(all.getContent());
        Page<CustomerWalletQuery> page = new MicroServicePage<>(customerWalletQueries, customerWalletPageQueryRequest.getPageRequest(), all.getTotalElements());
        return page;
    }

    /**
     * 分页数据解析
     *
     * @param resultsObj
     * @return
     */
    private List<CustomerWalletQuery> resultToCustomerWalletQueryList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            Object[] results = StringUtil.cast(item, Object[].class);

            CustomerWalletQuery customerWalletQuery = new CustomerWalletQuery();
            //顺序: SELECT a.customer_id, a.customer_name, a.customer_account, a.balance, a.apply_time
            customerWalletQuery.setCustomerId(StringUtil.cast(results, 0, String.class));
            customerWalletQuery.setCustomerName(StringUtil.cast(results, 1, String.class));
            customerWalletQuery.setCustomerAccount(StringUtil.cast(results, 2, String.class));
            customerWalletQuery.setBalance(getBigDecimal(results[3]));
            if (results.length >= 4) {
                customerWalletQuery.setApplyTime(getLocalDateTime(results[4]));
            }
            return customerWalletQuery;
        }).collect(Collectors.toList());
    }

    /**
     * Object解析成LocalDateTime
     * - (Timestamp-LocalDateTime)
     *
     * @param value
     * @return
     */
    public static LocalDateTime getLocalDateTime(Object value) {
        Timestamp timestamp = null;
        if (value != null) {
            if (value instanceof Timestamp) {
                timestamp = (Timestamp) value;
            } else {
                throw new SbcRuntimeException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }

        LocalDateTime localDateTime = null;
        localDateTime = timestamp == null ? null : LocalDateTime.ofInstant(new Date(timestamp.getTime()).toInstant(), ZoneId.systemDefault());

        return localDateTime;
    }

    /**
     * Object解析成BigDecimal
     *
     * @param value
     * @return
     */
    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new SbcRuntimeException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }


    public void assign(MicroServicePage<CusWalletVO> newPage) {
        List<CusWalletVO> content = newPage.getContent();
        if (CollectionUtils.isEmpty(content)) {
            return;
        }
        List<String> collect = content.stream().map(CusWalletVO::getCustomerId).collect(Collectors.toList());
        List<CustomerDetailVO> customerDetailVOList = customerDetailQueryProvider.listCustomerDetailByCustomerIds(CustomerDetailListByCustomerIdsRequest.builder().customerIds(collect).build())
                .getContext().getCustomerDetailVOList();
        if (CollectionUtils.isEmpty(customerDetailVOList)) {
            return;
        }
        for (CusWalletVO cusWalletVO : content) {
            CustomerDetailVO customerDetailVOTem = customerDetailVOList.stream().filter(customerDetailVO -> StringUtils.equalsIgnoreCase(cusWalletVO.getCustomerId(), customerDetailVO.getCustomerId())).findFirst().orElse(new CustomerDetailVO());
            cusWalletVO.setContactName(customerDetailVOTem.getContactName());
            cusWalletVO.setContactPhone(customerDetailVOTem.getContactPhone());
        }
    }

    public void assignAll(List<CusWalletVO> content) {
        List<String> collect = content.stream().map(CusWalletVO::getCustomerId).collect(Collectors.toList());
        List<CustomerDetailVO> customerDetailVOList = customerDetailQueryProvider.listCustomerDetailByCustomerIds(CustomerDetailListByCustomerIdsRequest.builder().customerIds(collect).build())
                .getContext().getCustomerDetailVOList();
        if (CollectionUtils.isEmpty(customerDetailVOList)) {
            return;
        }
        for (CusWalletVO cusWalletVO : content) {
            CustomerDetailVO customerDetailVOTem = customerDetailVOList.stream().filter(customerDetailVO -> StringUtils.equalsIgnoreCase(cusWalletVO.getCustomerId(), customerDetailVO.getCustomerId())).findFirst().orElse(new CustomerDetailVO());
            cusWalletVO.setContactName(customerDetailVOTem.getContactName());
            cusWalletVO.setContactPhone(customerDetailVOTem.getContactPhone());
        }
    }

    public void assign(CusWalletVO cusWalletVO) {
        List<WalletRecord> byCustomerAccount = walletRecordService.findByCustomerAccount(WalletRecordRequest.builder().customerAccount(cusWalletVO.getCustomerAccount()).build());
        if (CollectionUtils.isEmpty(byCustomerAccount)) {
            return;
        }
        //提现列表
        List<WalletRecord> withdrawalCollect = byCustomerAccount
                .stream().filter(walletRecord -> walletRecord.getRemark().contains("-") && walletRecord.getRemark().split("-")[0].equals(WalletDetailsType.DEDUCTION_WITHDRAW_SUCCEED.getDesc()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(withdrawalCollect)) {
            //交易总额
            BigDecimal totalDealPrice = withdrawalCollect.stream().map(WalletRecord::getDealPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            cusWalletVO.setExtractBalance(totalDealPrice);
        }
        //余额列表
        List<WalletRecord> balancePayCollect = byCustomerAccount
                .stream().filter(walletRecord -> walletRecord.getRemark().contains("-") && walletRecord.getRemark().split("-")[0].equals(WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc()))
                .collect(Collectors.toList());
        List<WalletRecord> balancePayCollectSubtract = byCustomerAccount
                .stream().filter(walletRecord -> walletRecord.getRemark().contains("-") && (
                        walletRecord.getRemark().split("-")[0].equals(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc())
                                || walletRecord.getRemark().split("-")[0].equals(WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc())))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(balancePayCollect)) {
            //交易总额
            BigDecimal totalDealPrice = balancePayCollect.stream().map(WalletRecord::getDealPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalDealPriceSubtract = CollectionUtils.isEmpty(balancePayCollectSubtract) ? BigDecimal.ZERO
                    : balancePayCollectSubtract.stream().map(WalletRecord::getDealPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            cusWalletVO.setDeductionBalance(totalDealPrice.subtract(totalDealPriceSubtract));
        }
    }


    public List<CusWalletVO> queryAllWalletAccountBalance(CustomerWalletRequest customerWalletRequest) {
        // 限制导出3000条
        PageRequest pageRequest = PageRequest.of(0, 1000);
        Page<Object> pages = customerWalletQueryRepository.listPageCustomerWallet(
                customerWalletRequest.getCustomerName()
                , customerWalletRequest.getCustomerAccount()
                , customerWalletRequest.getRecentlyTicketsTimeStart()
                , customerWalletRequest.getRecentlyTicketsTimeEnd()
                , customerWalletRequest.getUseBalance()
                , customerWalletRequest.getCustomerFundsIdList()
                , pageRequest);

        List<CustomerWalletQuery> customerWalletQueries = resultToCustomerWalletQueryList(pages.getContent());
        return KsBeanUtil.convert(customerWalletQueries, CusWalletVO.class);
    }

    public WalletStatusResponse  queryWalletStatus(WalletByWalletIdAddRequest request) {
        WalletStatusResponse walletStatusResponse = new WalletStatusResponse();
        CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(request.getStoreId())).orElse(null);
        if (null == customerWallet) {
            walletStatusResponse.setJingBiState(JingBiState.NO);
            return walletStatusResponse;
        }
        if (customerWallet.getIsEnable() == 0 || customerWallet.getIsEnable() == null) {
            walletStatusResponse.setJingBiState(JingBiState.CLOSE);
        } else {
            walletStatusResponse.setJingBiState(JingBiState.OPEN);
        }
        return walletStatusResponse;
    }

    public BaseResponse updateWalletDelById(WalletRequest request) {
        customerWalletRepository.updateByCustomerId(request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

}
