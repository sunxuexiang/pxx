package com.wanmi.sbc.account.wallet.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.wallet.VirtualGoodsQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.account.api.response.wallet.VirtualGoodsResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletRecordResponse;
import com.wanmi.sbc.account.api.response.wallet.WithdrawalDetailsResponse;
import com.wanmi.sbc.account.bean.enums.TradeStateEnum;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.account.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.account.bean.vo.BalanceInfoVO;
import com.wanmi.sbc.account.bean.vo.BindBankCardVo;
import com.wanmi.sbc.account.bean.vo.ExtractInfoVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.account.wallet.model.root.CustomerBindBankCard;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.account.wallet.model.root.WalletRecord;
import com.wanmi.sbc.account.wallet.repository.CustomerBindBankCardRepository;
import com.wanmi.sbc.account.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.account.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.account.wallet.request.WalletRecordQueryRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jeffrey
 * @create 2021-08-23 17:02
 */

@Service
public class WalletRecordService {

    @Autowired
    private WalletRecordRepository walletRecordRepository;

    @Autowired
    private CustomerWalletService customerWalletService;
    @Autowired
    CustomerWalletRepository customerWalletRepository;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    CustomerBindBankCardRepository customerBindBankCardRepository;

    @Autowired
    VirtualGoodsQueryProvider virtualGoodsQueryProvider;


    public Page<BalanceInfoVO> queryPageWalletRecord(WalletRecordRequest request) {
        request.setTradeState(TradeStateEnum.PAID);
        request.setRechargeStatus(2);
        request.setExtractStatus(3);
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), Sort.Direction.DESC, "dealTime");
        Page<Object> objects = walletRecordRepository.getRemainingMoneyInfo(request.getCustomerAccount(), request.getTradeState().toValue(), request.getRechargeStatus(), request.getExtractStatus(), pageable);
        List<BalanceInfoVO> balanceInfoVOS = Lists.newArrayList();
        for (Object object : objects) {
            BalanceInfoVO balanceInfoVO = new BalanceInfoVO();
            BalanceInfoVO balanceInfoVO1 = balanceInfoVO.convertFromNativeSQLResult(object);
            balanceInfoVOS.add(balanceInfoVO1);
        }
        //转数字
        List<BalanceInfoVO> resultList = balanceInfoVOS.stream().map(x -> {
            Integer tradeType = x.getTradeType();
            //交易类型【1充值，2提现，3余额支付】
            String type = null;
            if (tradeType == 1) {
                type = "充值";
            } else if (tradeType == 2) {
                type = "提现";
            } else if (tradeType == 3) {
                type = "余额支付";
            }
            x.setType(type);
            return x;
        }).collect(Collectors.toList());
        return new PageImpl<>(resultList, request.getPageable(), objects.getTotalElements());
    }

    public Page<ExtractInfoVO> queryWalletRecordByTradeType(WalletRecordRequest request) {
        //交易类型【1充值，2提现，3余额支付】
        request.setTradeType(WalletRecordTradeType.WITHDRAWAL);
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), Sort.Direction.DESC, "dealTime");
        Page<Object> objects = walletRecordRepository.queryExtractInfo(request.getTradeType().toValue(), request.getCustomerAccount(), pageable);

        List<ExtractInfoVO> extractInfos = Lists.newArrayList();
        for (Object o : objects.getContent()) {
            ExtractInfoVO extractInfo = new ExtractInfoVO();
            ExtractInfoVO extractInfo1 = extractInfo.convertFromNativeSQLResult(o);
            extractInfos.add(extractInfo1);
        }
        //状态数字转汉字
        //提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】
        List<ExtractInfoVO> resultList = extractInfos.stream().map(x -> {
            Integer extractStatus = x.getExtractStatus();
            String status = null;
            if (extractStatus == 1) {
                status = "待审核";
            } else if (extractStatus == 2) {
                status = "已审核";
            } else if (extractStatus == 3) {
                status = "已打款";
            } else if (extractStatus == 4) {
                status = "已拒绝";
            }
            x.setStatus(status);
            return x;
        }).collect(Collectors.toList());
        return new PageImpl<>(resultList, request.getPageable(), objects.getTotalElements());
    }

    public WalletRecordResponse findWalletRecordByRecordNo(WalletRecordRequest request) {
        WalletRecord walletRecord = walletRecordRepository.findByRecordNo(request.getRecordNo());
        WalletRecordVO walletRecordVO = KsBeanUtil.convert(walletRecord, WalletRecordVO.class);
        return WalletRecordResponse.builder().walletRecordVO(walletRecordVO).build();
    }

    @Transactional
    @LcnTransaction
    public AddWalletRecordResponse addWalletRecord(AddWalletRecordRecordRequest request) {
        if (Objects.isNull(request.getCurrentBalance())) {
            WalletByCustomerIdQueryRequest walletQuery = new WalletByCustomerIdQueryRequest();
            walletQuery.setCustomerId(request.getCustomerId());
            CustomerWallet customerWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByCustomerId(request.getCustomerId())).orElse(null);
            //当前余额
            BigDecimal balance = customerWallet.getBalance();
            request.setCurrentBalance(balance);
        }
        WalletRecord record = KsBeanUtil.convert(request, WalletRecord.class);
        String recordNo = generatorService.generate("W");
        record.setRecordNo(recordNo);
        record.setDealTime(LocalDateTime.now());
        record = walletRecordRepository.saveAndFlush(record);
        AddWalletRecordResponse addWalletRecordResponse = new AddWalletRecordResponse();
        WalletRecordVO vo = new WalletRecordVO();
        BeanUtils.copyProperties(record, vo);
        addWalletRecordResponse.setWalletRecord(vo);
        return addWalletRecordResponse;
    }

    @Transactional
    public BaseResponse addWalletRecordBatch(AddWalletRecordRecordBatchRequest request) {

        if(Objects.isNull(request) || CollectionUtils.isEmpty(request.getWalletRecordVOList())){
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.success(walletRecordRepository.saveAll(KsBeanUtil.convertList(request.getWalletRecordVOList(),WalletRecord.class)));
    }

    public WithdrawalDetailsResponse withdrawalDetails(WithdrawalDetailsRequest request) {
        //根据用户id查询钱包信息
        CustomerWallet customerWallet = customerWalletRepository.getCustomerWalletByCustomerId(request.getCustomerId());
        //根据钱包id查询银行卡信息
        List<CustomerBindBankCard> customerBindBankCards = customerBindBankCardRepository.findAllByWalletIdAndDelFlag(customerWallet.getWalletId(), DefaultFlag.NO);
        List<BindBankCardVo> bindBankCardVos = KsBeanUtil.convert(customerBindBankCards, BindBankCardVo.class);
        return WithdrawalDetailsResponse.builder().withdrawalAmount(customerWallet.getBalance()).banks(bindBankCardVos).build();
    }

    public VirtualGoodsResponse virtualGoods(VirtualGoodsRequest request) {
        return virtualGoodsQueryProvider.getPageVirtualGoodsList(request).getContext();
    }

    public WalletRecordResponse getWalletRecordByRelationOrderId(QueryWalletRecordByRelationOrderIdRequest request) {
        List<WalletRecord> walletRecordByRelationOrderId = walletRecordRepository.getWalletRecordByRelationOrderId(request.getRelationOrderId());

        if (CollectionUtils.isEmpty(walletRecordByRelationOrderId)) {
            return WalletRecordResponse.builder().build();
        }
        WalletRecord walletRecord = walletRecordByRelationOrderId.get(0);
        WalletRecordVO walletRecordVO = KsBeanUtil.convert(walletRecord, WalletRecordVO.class);
        return WalletRecordResponse.builder().walletRecordVO(walletRecordVO).build();
    }

    public Page<WalletRecordVO> getQueryWalletRecordList(WalletRecordRequest request) {
        WalletRecordQueryRequest newRequest = KsBeanUtil.convert(request, WalletRecordQueryRequest.class);
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), Sort.Direction.DESC, "dealTime");
        Page<WalletRecord> newPage = walletRecordRepository.findAll(WalletRecordQueryBuilder.build(newRequest), pageable);
        //数字转中文
        List<WalletRecord> oldList = newPage.getContent();
        List<WalletRecordVO> walletRecordVOS = KsBeanUtil.convertList(oldList, WalletRecordVO.class);
        for (WalletRecordVO walletRecordVO : walletRecordVOS) {
            //收支类型【0收入，1支出】
            Integer budgetType = walletRecordVO.getBudgetType().toValue();
            walletRecordVO.setBudgetTypeString(budgetType == 0 ? "收入" : "支出");
            //交易类型【1充值，2提现，3余额支付】
            Integer tradeType = walletRecordVO.getTradeType().toValue();
            walletRecordVO.setTradeTypeString(tradeType == 1 ? "充值" : (tradeType == 2 ? "提现" : "余额支付"));
        }
        return new PageImpl<>(walletRecordVOS, request.getPageable(), newPage.getTotalElements());
    }

    public Page<WalletRecordVO> queryPgWalletRecord(WalletRecordRequest request) {
        WalletRecordQueryRequest newRequest = KsBeanUtil.convert(request, WalletRecordQueryRequest.class);
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), Sort.Direction.DESC, "dealTime");
        Page<WalletRecord> newPage = walletRecordRepository.findAll(WalletRecordQueryBuilder.build(newRequest), pageable);
        //数字转中文
        List<WalletRecord> oldList = newPage.getContent();
        List<WalletRecordVO> walletRecordVOS = KsBeanUtil.convertList(oldList, WalletRecordVO.class);
        transition(walletRecordVOS);
        return new PageImpl<>(walletRecordVOS, request.getPageable(), newPage.getTotalElements());
    }

    public List<WalletRecord> findByCustomerAccount(WalletRecordRequest request) {
        List<WalletRecord> byCustomerAccount = walletRecordRepository.findByCustomerAccount(request.getCustomerAccount());
        List<WalletRecordVO> walletRecordVOS = KsBeanUtil.convertList(byCustomerAccount, WalletRecordVO.class);
        transition(walletRecordVOS);
        return byCustomerAccount;
    }

    public void transition(List<WalletRecordVO> walletRecordVOS){
        for (WalletRecordVO walletRecordVO : walletRecordVOS) {
            //收支类型【0收入，1支出】
            Integer budgetType = walletRecordVO.getBudgetType().toValue();
            walletRecordVO.setBudgetTypeString(budgetType == 0 ? "获得" : "扣除");
            String remark = walletRecordVO.getRemark();
            if (remark.contains("-")){
                //余额明细
                walletRecordVO.setRemark(remark.split("-")[0]);
            }
        }
    }

    /**
     * 根据客户账号获取分页余额明细
     * @param request
     * @return
     * @throws SbcRuntimeException
     */
    public WalletRecordResponse listByCustomerAccount(WalletRecordQueryRequest request) throws SbcRuntimeException {
        if (Objects.nonNull(request.getDealTime())) {
            request.setStartTime(DateUtil.getMonthBegin(request.getDealTime().toString()));
            request.setEndTime(DateUtil.getMonthEnd(request.getDealTime().toString()));
        }
        Page<WalletRecord> walletRecordPage = walletRecordRepository.findAll(WalletRecordQueryBuilder.build(request),request.getPageRequest());
        if (CollectionUtils.isEmpty(walletRecordPage.getContent())){
            return WalletRecordResponse.builder()
                    .pageList(new MicroServicePage<>(Collections.emptyList(),request.getPageRequest(),walletRecordPage.getTotalElements())).build();
        }
        Page<WalletRecordVO> walletRecordVOPage = KsBeanUtil.convertPage(walletRecordPage,WalletRecordVO.class);
        String sFlag = "-";
        for (WalletRecordVO walletRecordVO : walletRecordVOPage.getContent()) {
            String remark = walletRecordVO.getRemark();
            if (remark.contains(sFlag)){
                //余额明细
                String[] remarkArrays = remark.split(sFlag);
                walletRecordVO.setRemark(remarkArrays[0]);
                if (WalletDetailsType.DEDUCTION_RECHARGE.getDesc().equals(walletRecordVO.getRemark()) || WalletDetailsType.INCREASE_RECHARGE.getDesc().equals(walletRecordVO.getRemark())) {
                    if (remarkArrays.length > 1) {
                        String claimsRemark = remarkArrays[remarkArrays.length - 1];
                        if (!claimsRemark.equals(walletRecordVO.getRelationOrderId())) {
                            walletRecordVO.setClaimsRemark(claimsRemark);
                        }
                    }
                }
            }
        }
        return WalletRecordResponse.builder()
                .pageList(new MicroServicePage<>(walletRecordVOPage.getContent(),request.getPageRequest(),walletRecordVOPage.getTotalElements()))
                .build();
    }

    public WalletRecordResponse getWalletRecordByRelationOrderIdAndTradeRemark(QueryWalletRecordByRelationOrderIdRequest request) {
        List<WalletRecord> walletRecordByRelationOrderId = walletRecordRepository.getWalletRecordByRelationOrderIdAndTradeRemark(request.getRelationOrderId(),request.getTradeRemark());

        if (CollectionUtils.isEmpty(walletRecordByRelationOrderId)) {
            return WalletRecordResponse.builder().build();
        }
        WalletRecord walletRecord = walletRecordByRelationOrderId.get(0);
        WalletRecordVO walletRecordVO = KsBeanUtil.convert(walletRecord, WalletRecordVO.class);
        return WalletRecordResponse.builder().walletRecordVO(walletRecordVO).build();
    }
}
