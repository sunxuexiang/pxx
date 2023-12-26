package com.wanmi.sbc.returnorder.payorder.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.offline.OfflineQueryProvider;
import com.wanmi.sbc.account.api.request.offline.OfflineAccountGetByIdRequest;
import com.wanmi.sbc.account.api.request.offline.OfflineAccountListWithoutDeleteFlagByBankNoRequest;
import com.wanmi.sbc.account.api.response.offline.OfflineAccountGetByIdResponse;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyListRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailGetCustomerIdResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.returnorder.api.request.trade.TradePayRecordObsoleteRequest;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.returnorder.customer.service.CustomerCommonService;
import com.wanmi.sbc.returnorder.manualrefund.service.ManualRefundService;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.payorder.repository.PayOrderRepository;
import com.wanmi.sbc.returnorder.payorder.request.PayOrderGenerateRequest;
import com.wanmi.sbc.returnorder.payorder.request.PayOrderRequest;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderPageResponse;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
import com.wanmi.sbc.returnorder.receivables.model.root.Receivable;
import com.wanmi.sbc.returnorder.receivables.repository.ReceivableRepository;
import com.wanmi.sbc.returnorder.receivables.service.ReceivableService;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.request.TradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.service.PileTradeService;
import com.wanmi.sbc.returnorder.trade.service.TradeCacheService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.returnorder.util.XssUtils;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.response.CcbPayRecordResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付单服务
 * Created by zhangjin on 2017/4/20.
 */
@Slf4j
@Service
public class PayOrderService {

    @Autowired
    private PayOrderRepository payOrderRepository;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private ReceivableService receivableService;

    @Autowired
    private OfflineQueryProvider offlineQueryProvider;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PileTradeService pileTradeService;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private TradeCacheService tradeCacheService;

    @Autowired
    private ManualRefundService manualRefundService;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private NewPileTradeService newPileTradeService;
    /**
     * 根据订单号生成支付单
     *
     * @param payOrderGenerateRequest payOrderGenerateRequest
     * @return Optional<payorder>
     */
    @Transactional
    public Optional<PayOrder> generatePayOrderByOrderCode(PayOrderGenerateRequest payOrderGenerateRequest) {
        PayOrder payOrder = new PayOrder();
        BaseResponse<CustomerDetailGetCustomerIdResponse> response = tradeCacheService.getCustomerDetailByCustomerId(payOrderGenerateRequest.getCustomerId());
        CustomerDetailVO customerDetail = response.getContext();
        payOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        payOrder.setOrderCode(payOrderGenerateRequest.getOrderCode());
        payOrder.setUpdateTime(LocalDateTime.now());
        payOrder.setCreateTime(payOrderGenerateRequest.getOrderTime());
        payOrder.setDelFlag(DeleteFlag.NO);
        payOrder.setCompanyInfoId(payOrderGenerateRequest.getCompanyInfoId());
        payOrder.setPayOrderNo(generatorService.generateOid());
        if (payOrderGenerateRequest.getPayOrderPrice() == null) {
            payOrder.setPayOrderStatus(PayOrderStatus.PAYED);
        } else {
            payOrder.setPayOrderStatus(PayOrderStatus.NOTPAY);
        }
        payOrder.setPayOrderPrice(payOrderGenerateRequest.getPayOrderPrice());
        payOrder.setPayOrderPoints(payOrderGenerateRequest.getPayOrderPoints());
        payOrder.setPayType(payOrderGenerateRequest.getPayType());
        if(PayType.OFFLINE.equals(payOrderGenerateRequest.getPayType())){
            payOrder.setPayOrderRealPayPrice(payOrderGenerateRequest.getPayOrderPrice());
        }

        if (OrderType.POINTS_ORDER.equals(payOrderGenerateRequest.getOrderType())) {
            payOrderRepository.saveAndFlush(payOrder);
            // 积分订单生成收款单
            Receivable receivable = new Receivable();
            receivable.setPayOrderId(payOrder.getPayOrderId());
            receivable.setReceivableNo(generatorService.generateSid());
            receivable.setPayChannel("积分支付");
            receivable.setPayChannelId((Constants.DEFAULT_RECEIVABLE_ACCOUNT));
            receivable.setCreateTime(payOrderGenerateRequest.getOrderTime());
            receivable.setDelFlag(DeleteFlag.NO);
            receivableRepository.save(receivable);
            return Optional.ofNullable(payOrder);
        }
        PayOrder savedPayOrder =payOrderRepository.saveAndFlush(payOrder);
        log.info("====PayOrder：{}",JSONObject.toJSONString(savedPayOrder));
        return Optional.ofNullable(savedPayOrder);
    }

    /**
     * 作废支付单
     *
     * @param payOrders payOrderIds
     */
    @Transactional
    @LcnTransaction
    public void destoryPayOrder(List<PayOrder> payOrders, Operator operator) {
        //返回修改对应订单状态
        //只有支付完的订单那才改状态
        List<String> orderIds = payOrders.stream().filter(payOrder -> PayOrderStatus.PAYED.equals(payOrder
                .getPayOrderStatus()) || PayOrderStatus.TOCONFIRM.equals(payOrder
                .getPayOrderStatus())).map(PayOrder::getOrderCode).collect(Collectors.toList());

        List<String> payIds = payOrders.stream().map(PayOrder::getPayOrderId).collect(Collectors.toList());

        receivableService.deleteReceivables(payIds);
        if (!CollectionUtils.isEmpty(payIds)) {
            payOrderRepository.updatePayOrderStatus(payIds, PayOrderStatus.NOTPAY);
        }

        orderIds.forEach(orderId -> {
            TradePayRecordObsoleteRequest tradePayRecordObsoleteRequest =
                    TradePayRecordObsoleteRequest.builder().tid(orderId).operator(operator).build();
            tradeService.payRecordObsolete(tradePayRecordObsoleteRequest.getTid(),
                    tradePayRecordObsoleteRequest.getOperator());

        });
    }


    /**
     * pay模块无法引入tradeService，此处将OrderList传到controller，判断trade是否过了账期
     *
     * @param payOrderIds
     * @return
     */
    public List<PayOrder> findPayOrderByPayOrderIds(List<String> payOrderIds) {
        if (CollectionUtils.isEmpty(payOrderIds)) {
            throw new SbcRuntimeException("K-020002");
        }
        return payOrderRepository.findAllById(payOrderIds);
    }


    /**
     * 修改收款单状态
     *
     * @param payOrderId payOrderId
     * @param payOrderId payOrderStatus
     */
    @Transactional
    public void updatePayOrder(String payOrderId, PayOrderStatus payOrderStatus) {
        if (Objects.isNull(payOrderId)) {
            throw new SbcRuntimeException("K-020002");
        }
        payOrderRepository.updatePayOrderStatus(Lists.newArrayList(payOrderId), payOrderStatus);
    }

    /**
     * 根据订单编号查询支付单，支付单状态..
     *
     * @param orderCode orderCode
     * @return 支付单
     */
    public Optional<PayOrder> findPayOrderByOrderCode(String orderCode) {
        return payOrderRepository.findByOrderCodeAndDelFlag(orderCode, DeleteFlag.NO);
    }

    /**
     * 根据订单编号查询支付单，支付单状态..
     *
     * @param orderCodes orderCode
     * @return 支付单
     */
    public List<PayOrder> findByOrderNosAndDelFlag(List<String> orderCodes) {
        return payOrderRepository.findByOrderNosAndDelFlag(orderCodes, DeleteFlag.NO);
    }


    public PayOrderResponse findPayOrder(String orderNo) {
        PayOrder payOrder = payOrderRepository.findByOrderCodeAndDelFlag(orderNo, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException("K-070001"));

        payOrder.setCustomerDetail(customerCommonService.getAnyCustomerDetailById(payOrder.getCustomerDetailId()));
        payOrder.setCompanyInfo(customerCommonService.getCompanyInfoById(payOrder.getCompanyInfoId()));
        return getPayOrderResponse(payOrder);
    }

    public PayOrderResponse findPayOrderByPayOrderNo(String payOrderNo) {
        PayOrder payOrder = payOrderRepository.findByPayOrderNo(payOrderNo);

        payOrder.setCustomerDetail(customerCommonService.getAnyCustomerDetailById(payOrder.getCustomerDetailId()));
        payOrder.setCompanyInfo(customerCommonService.getCompanyInfoById(payOrder.getCompanyInfoId()));
        return getPayOrderResponse(payOrder);
    }

    /**
     * 查找囤货支付单
     * @param orderNo
     * @return
     */
    public PayOrderResponse findPilePayOrder(String orderNo) {
        PayOrder payOrder = payOrderRepository.findByOrderCodeAndDelFlag(orderNo, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException("K-070001"));

        payOrder.setCustomerDetail(customerCommonService.getAnyCustomerDetailById(payOrder.getCustomerDetailId()));
        payOrder.setCompanyInfo(customerCommonService.getCompanyInfoById(payOrder.getCompanyInfoId()));
        return getPilePayOrderResponse(payOrder);
    }

    public PayOrderResponse findPayOrderById(String payOrderId) {
        PayOrder payOrder = payOrderRepository.findById(payOrderId)
                .orElseThrow(() -> new SbcRuntimeException("K-070001"));

        payOrder.setCustomerDetail(customerCommonService.getAnyCustomerDetailById(payOrder.getCustomerDetailId()));
        payOrder.setCompanyInfo(customerCommonService.getCompanyInfoById(payOrder.getCompanyInfoId()));
        return getPayOrderResponse(payOrder);
    }

    public PayOrderResponse findPilePayOrderById(String payOrderId) {
        PayOrder payOrder = payOrderRepository.findById(payOrderId)
                .orElseThrow(() -> new SbcRuntimeException("K-070001"));

        payOrder.setCustomerDetail(customerCommonService.getAnyCustomerDetailById(payOrder.getCustomerDetailId()));
        payOrder.setCompanyInfo(customerCommonService.getCompanyInfoById(payOrder.getCompanyInfoId()));
        return getPilePayOrderResponse(payOrder);
    }


    /**
     * 根据查询条件做收款单分页查询
     *
     * @param payOrderRequest payOrderRequest
     * @return Page<payorder>
     */
    public PayOrderPageResponse findPayOrders(PayOrderRequest payOrderRequest) {
        PayOrderPageResponse payOrderPageResponse = new PayOrderPageResponse();
        payOrderPageResponse.setPayOrderResponses(Collections.emptyList());
        payOrderPageResponse.setTotal(0L);
        payOrderPageResponse.setCurrentPage(payOrderRequest.getPageNum());
        payOrderPageResponse.setPageSize(payOrderRequest.getPageSize());

        //模糊匹配会员/商户名称，不符合条件直接返回
        if (!this.likeCustomerAndSupplierNameAndBankNo(payOrderRequest)) {
            return payOrderPageResponse;
        }

        Page<PayOrder> payOrders = payOrderRepository.findAll(findByRequest(payOrderRequest),
                payOrderRequest.getPageable());
        log.info("====订单详情：{}",JSONObject.toJSONString(payOrders));
        payOrderPageResponse.setPayOrderResponses(this.getPayOrderResponses(payOrders.getContent()));
        //找到对于订单是否是绑定订单数据
        List<PayOrderResponse> payOrderResponses = payOrderPageResponse.getPayOrderResponses();
        List<String> tids = payOrderResponses.stream().map(PayOrderResponse::getOrderCode).collect(Collectors.toList());
        Map<String, Trade> collect = tradeService.getListByIds(tids).stream().collect(Collectors.toMap(Trade::getId, Function.identity(), (a, b) -> a));
        for (PayOrderResponse p : payOrderResponses){
            Trade trade = collect.get(p.getOrderCode());
            if (Objects.nonNull(trade)){
                p.setMergFlag(trade.getMergFlag());
                p.setTids(trade.getTids());
            }
        }
        payOrderPageResponse.setTotal(payOrders.getTotalElements());
        return payOrderPageResponse;
    }

    /**
     * 根据查询条件做收款单查询
     *
     * @param payOrderRequest payOrderRequest
     * @return PayOrderPageResponse
     */
    public PayOrderPageResponse findPayOrdersWithNoPage(PayOrderRequest payOrderRequest) {
        //模糊匹配会员/商户名称，不符合条件直接返回
        if (!this.likeCustomerAndSupplierNameAndBankNo(payOrderRequest)) {
            return PayOrderPageResponse.builder().payOrderResponses(Collections.EMPTY_LIST).build();
        }

        List<PayOrder> payOrders = payOrderRepository.findAll(findByRequest(payOrderRequest));
        if (Objects.isNull(payOrders) || CollectionUtils.isEmpty(payOrders)) {
            return PayOrderPageResponse.builder().payOrderResponses(Collections.EMPTY_LIST).build();
        }
        return PayOrderPageResponse.builder().payOrderResponses(this.getPayOrderResponses(payOrders)).build();
    }

    /**
     * 获取多个支付单返回数据
     *
     * @param payOrders payOrder
     * @return PayOrderResponse
     */
    private List<PayOrderResponse> getPayOrderResponses(List<PayOrder> payOrders) {
        List<Long> companyIds = payOrders.stream()
                .map(PayOrder::getCompanyInfoId).collect(Collectors.toList());

        Map<Long, CompanyInfoVO> companyInfoVOMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(companyIds)) {
            companyInfoVOMap.putAll(customerCommonService.listCompanyInfoByCondition(
                    CompanyListRequest.builder().companyInfoIds(companyIds).build()
            ).stream().collect(Collectors.toMap(CompanyInfoVO::getCompanyInfoId, c -> c)));
        }

        Map<String, Trade> tradeMap = new HashMap<>();
        Map<String, PileTrade> pileTradeMap = new HashMap<>();
        List<String> orderCodes = payOrders.stream().map(PayOrder::getOrderCode).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(orderCodes)) {
            //销售支付单
            List<Trade> trades = tradeService
                    .queryAll(TradeQueryRequest.builder().ids(orderCodes.toArray(new String[orderCodes.size()])).orderType(OrderType.ALL_ORDER).build())
                    .stream()
                    .collect(Collectors.toList());
            tradeMap.putAll(trades.stream().collect(Collectors.toMap(Trade::getId, t -> t)));
            //囤货单
            List<PileTrade> pileTrades = pileTradeService
                    .queryAll(TradeQueryRequest.builder().ids(orderCodes.toArray(new String[orderCodes.size()])).orderType(OrderType.ALL_ORDER).build())
                    .stream()
                    .collect(Collectors.toList());
            pileTradeMap.putAll(pileTrades.stream().collect(Collectors.toMap(PileTrade::getId, t -> t)));
        }

        log.info("PayOrderService.getPayOrderResponses tradeMap:{} payOrders:{}", JSONObject.toJSONString(tradeMap));
        log.info("PayOrderService.getPayOrderResponses pileTradeMap:{}",JSONObject.toJSONString(pileTradeMap));
        log.info("PayOrderService.getPayOrderResponses payOrders:{}", JSONObject.toJSONString(payOrders));

        List<PayOrderResponse> returnList = new ArrayList<>();
        payOrders.forEach(payOrder ->{
            payOrder.setCompanyInfo(companyInfoVOMap.get(payOrder.getCompanyInfoId()));
            if (tradeMap != null && tradeMap.containsKey(payOrder.getOrderCode())) {
                Buyer buyer = tradeMap.get(payOrder.getOrderCode()).getBuyer();
                CustomerDetailVO customerDetailVO = new CustomerDetailVO();
                customerDetailVO.setCustomerId(buyer.getId());
                customerDetailVO.setCustomerName(buyer.getName());
                payOrder.setCustomerDetail(customerDetailVO);
            }else {
                PileTrade pileTrade = pileTradeMap.get(payOrder.getOrderCode());
                if(Objects.nonNull(pileTrade) && Objects.nonNull(pileTrade.getBuyer())){
                    Buyer buyer = pileTradeMap.get(payOrder.getOrderCode()).getBuyer();
                    CustomerDetailVO customerDetailVO = new CustomerDetailVO();
                    customerDetailVO.setCustomerId(buyer.getId());
                    customerDetailVO.setCustomerName(buyer.getName());
                    payOrder.setCustomerDetail(customerDetailVO);
                }
            }
            returnList.add(this.getPayOrderResponse(payOrder));
        });
        for (PayOrderResponse response : returnList) {
            String orderCode = response.getOrderCode();
            if (tradeMap.containsKey(orderCode)) {
                Trade trade = tradeMap.get(orderCode);
                if (Objects.equals(trade.getPayWay(), PayWay.CCB)
                        && Objects.nonNull(trade.getPayOrderNo())
                        && Objects.equals(trade.getTradeState().getPayState(), PayState.PAID)) {
                    String payOrderNo = trade.getPayOrderNo();
                    CcbPayRecordResponse recordResponse = ccbPayProvider.queryCcbPayRecordByPayOrderNo(payOrderNo).getContext();
                    if (Objects.nonNull(recordResponse) && Objects.nonNull(recordResponse.getCcbPayImg())) {
                        response.setCcbPayImg(recordResponse.getCcbPayImg());
                    }
                }
            }
        }
        return returnList;
        /*return payOrders.stream().map(payOrder -> {
            payOrder.setCompanyInfo(companyInfoVOMap.get(payOrder.getCompanyInfoId()));
            if (tradeMap != null && tradeMap.containsKey(payOrder.getOrderCode())) {
                Buyer buyer = tradeMap.get(payOrder.getOrderCode()).getBuyer();
                CustomerDetailVO customerDetailVO = new CustomerDetailVO();
                customerDetailVO.setCustomerId(buyer.getId());
                customerDetailVO.setCustomerName(buyer.getName());
                payOrder.setCustomerDetail(customerDetailVO);
                return this.getPayOrderResponse(payOrder);
            }else {
                Buyer buyer = pileTradeMap.get(payOrder.getOrderCode()).getBuyer();
                CustomerDetailVO customerDetailVO = new CustomerDetailVO();
                customerDetailVO.setCustomerId(buyer.getId());
                customerDetailVO.setCustomerName(buyer.getName());
                payOrder.setCustomerDetail(customerDetailVO);
                return this.getPayOrderResponse(payOrder);
            }
        }).collect(Collectors.toList());*/
    }

    /**
     * 囤货获取支付单返回数据
     *
     * @param payOrder payOrder
     * @return PayOrderResponse
     */
    private PayOrderResponse getPilePayOrderResponse(PayOrder payOrder) {
        PayOrderResponse payOrderResponse = new PayOrderResponse();
        BeanUtils.copyProperties(payOrder, payOrderResponse);
        payOrderResponse.setTotalPrice(payOrder.getPayOrderPrice());
        payOrderResponse.setPayOrderPoints(payOrder.getPayOrderPoints());
        if (Objects.nonNull(payOrder.getCustomerDetail())) {
            payOrderResponse.setCustomerId(payOrder.getCustomerDetail().getCustomerId());
            payOrderResponse.setCustomerName(payOrder.getCustomerDetail().getCustomerName());

        }
        if (Objects.nonNull(payOrder.getCompanyInfo())) {
            payOrderResponse.setCompanyInfoId(payOrder.getCompanyInfo().getCompanyInfoId());
            payOrderResponse.setSupplierName(payOrder.getCompanyInfo().getSupplierName());
        }

        if (Objects.nonNull(payOrder.getReceivable())) {
            payOrderResponse.setReceivableNo(payOrder.getReceivable().getReceivableNo());
            payOrderResponse.setComment(payOrder.getReceivable().getComment());
            //收款时间
            payOrderResponse.setReceiveTime(payOrder.getReceivable().getCreateTime());
            payOrderResponse.setReceivableAccount(parseAccount(payOrder));
            //支付渠道
            payOrderResponse.setPayChannel(payOrder.getReceivable().getPayChannel());
            payOrderResponse.setPayChannelId(payOrder.getReceivable().getPayChannelId());
            payOrderResponse.setEncloses(payOrder.getReceivable().getEncloses());
            //online todo
        }
        PileTrade detail = pileTradeService.detail(payOrder.getOrderCode());
        payOrderResponse.setIsSelf(detail.getSupplier().getIsSelf());
        payOrderResponse.setTradeState(detail.getTradeState());
        payOrderResponse.setStoreName(detail.getSupplier().getStoreName());
        return payOrderResponse;
    }

    /**
     * 获取支付单返回数据
     *
     * @param payOrder payOrder
     * @return PayOrderResponse
     */
    private PayOrderResponse getPayOrderResponse(PayOrder payOrder) {
        log.info("PayOrderService.getPayOrderResponse order_code:{} payOrderRealPayPrice:{}",payOrder.getOrderCode(),payOrder.getPayOrderRealPayPrice());
        PayOrderResponse payOrderResponse = new PayOrderResponse();
        BeanUtils.copyProperties(payOrder, payOrderResponse);
        payOrderResponse.setTotalPrice(payOrder.getPayOrderPrice());
        payOrderResponse.setPayOrderPoints(payOrder.getPayOrderPoints());
        payOrderResponse.setPayOrderRealPayPrice(payOrder.getPayOrderRealPayPrice());
        if (Objects.nonNull(payOrder.getCustomerDetail())) {
            payOrderResponse.setCustomerId(payOrder.getCustomerDetail().getCustomerId());
            payOrderResponse.setCustomerName(payOrder.getCustomerDetail().getCustomerName());

        }
        if (Objects.nonNull(payOrder.getCompanyInfo())) {
            payOrderResponse.setCompanyInfoId(payOrder.getCompanyInfo().getCompanyInfoId());
            payOrderResponse.setSupplierName(payOrder.getCompanyInfo().getSupplierName());
        }

        if (Objects.nonNull(payOrder.getReceivable())) {
            payOrderResponse.setReceivableNo(payOrder.getReceivable().getReceivableNo());
            payOrderResponse.setComment(payOrder.getReceivable().getComment());
            //收款时间
            payOrderResponse.setReceiveTime(payOrder.getReceivable().getCreateTime());
            payOrderResponse.setReceivableAccount(parseAccount(payOrder));
            //支付渠道
            payOrderResponse.setPayChannel(payOrder.getReceivable().getPayChannel());
            payOrderResponse.setPayChannelId(payOrder.getReceivable().getPayChannelId());
            payOrderResponse.setEncloses(payOrder.getReceivable().getEncloses());
            //online todo
        }
        Trade detail = tradeService.detail(payOrder.getOrderCode());
        PileTrade pileTrade = pileTradeService.detail(payOrder.getOrderCode());
        NewPileTrade newPileTrade = newPileTradeService.detail(payOrder.getOrderCode());
        if (Objects.nonNull(detail)) {
            payOrderResponse.setMergFlag(detail.getMergFlag());
            payOrderResponse.setTids(detail.getTids());
            payOrderResponse.setIsSelf(detail.getSupplier().getIsSelf());
            payOrderResponse.setTradeState(detail.getTradeState());
            payOrderResponse.setStoreName(detail.getSupplier().getStoreName());
            if (StringUtils.isEmpty(detail.getActivityType())) {
                payOrderResponse.setActivityType(TradeActivityTypeEnum.TRADE.toActivityType());
            }else {
                payOrderResponse.setActivityType(detail.getActivityType());
            }
        }else if (Objects.nonNull(pileTrade)){
            payOrderResponse.setIsSelf(pileTrade.getSupplier().getIsSelf());
            payOrderResponse.setTradeState(pileTrade.getTradeState());
            payOrderResponse.setStoreName(pileTrade.getSupplier().getStoreName());
            payOrderResponse.setActivityType(TradeActivityTypeEnum.PICKGOODS.toActivityType());
        }
        // 设置新囤货订单
        if(Objects.nonNull(newPileTrade)){
            payOrderResponse.setActivityType(TradeActivityTypeEnum.NEWPILETRADE.toActivityType());
        }
        PayOrderResponse payOrderResponseCopy=manualRefundService.judgeDisplayRefund(payOrderResponse);
        payOrderResponse.setRefundPrice(payOrderResponseCopy.getRefundPrice());
        payOrderResponse.setIsDisplay(payOrderResponseCopy.getIsDisplay());
        return payOrderResponse;
    }


    private String parseAccount(PayOrder payOrder) {
        StringBuilder accountName = new StringBuilder();
        if (PayType.OFFLINE.equals(payOrder.getPayType()) && Objects.nonNull(payOrder.getReceivable()
                .getOfflineAccountId())) {
            OfflineAccountGetByIdResponse response = offlineQueryProvider.getById(new OfflineAccountGetByIdRequest
                    (payOrder
                            .getReceivable()
                            .getOfflineAccountId())).getContext();
            if (response.getAccountId() != null) {
                accountName.append(response.getBankName()).append(" ").append(ReturnOrderService.getDexAccount
                        (response.getBankNo()));
            }
        }
        return accountName.toString();
    }

    /**
     * 通过订单编号列表查询支付单
     *
     * @param orderNos
     * @return
     */
    public List<PayOrder> findByOrderNos(List<String> orderNos, PayOrderStatus payOrderStatus) {
        return payOrderRepository.findByOrderNos(orderNos, payOrderStatus);
    }

    /**
     * 删除收款单
     *
     * @param payOrderId
     * @return rows
     */
    @Transactional
    public int deleteByPayOrderId(String payOrderId) {
        return payOrderRepository.deletePayOrderById(payOrderId);
    }


    /**
     * 合计收款金额
     *
     * @param payOrderRequest request
     * @return sum
     */
    public BigDecimal sumPayOrderPrice(PayOrderRequest payOrderRequest) {
        //模糊匹配会员/商户名称，不符合条件直接返回0
        if (!likeCustomerAndSupplierNameAndBankNo(payOrderRequest)) {
            return BigDecimal.ZERO;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);

        Root<PayOrder> root = query.from(PayOrder.class);
        query.select(builder.sum(root.get("payOrderPrice")));
        query.where(buildWhere(payOrderRequest, root, query, builder));

        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 替代关联查询-模糊商家名称、模糊会员名称、银行账号，以并且关系的判断
     *
     * @param payOrderRequest
     * @return true:有符合条件的数据,false:没有符合条件的数据
     */
    private boolean likeCustomerAndSupplierNameAndBankNo(final PayOrderRequest payOrderRequest) {
        boolean supplierLike = true;
        //商家名称
        if ((StringUtils.isNotBlank(payOrderRequest.getSupplierName()) && StringUtils.isNotBlank(payOrderRequest
                .getSupplierName().trim())) ) {
            CompanyListRequest request = CompanyListRequest.builder()
                    .supplierName(payOrderRequest.getSupplierName())
                    .build();
            payOrderRequest.setCompanyInfoIds(customerCommonService.listCompanyInfoIdsByCondition(request));

            if (CollectionUtils.isEmpty(payOrderRequest.getCompanyInfoIds())) {
                supplierLike = false;
            }
        }
        //店铺名称
        if ((StringUtils.isNotBlank(payOrderRequest.getStoreName()) && StringUtils.isNotBlank(payOrderRequest
                .getStoreName().trim())) ) {
            CompanyListRequest request = CompanyListRequest.builder()
                    .storeName(payOrderRequest.getStoreName())
                    .build();
            payOrderRequest.setCompanyInfoIds(customerCommonService.listCompanyInfoIdsByCondition(request));
            if (CollectionUtils.isEmpty(payOrderRequest.getCompanyInfoIds())) {
                supplierLike = false;
            }
        }
        //模糊会员名称
        boolean customerLike = true;
        if (StringUtils.isNotBlank(payOrderRequest.getCustomerName()) && StringUtils.isNotBlank(payOrderRequest
                .getCustomerName().trim())) {
            List<Trade> trades = tradeService.queryAll(TradeQueryRequest.builder().buyerName(payOrderRequest.getCustomerName()).build());
            if (CollectionUtils.isEmpty(trades)){
                customerLike = false;
            }else {
                List<String> customerIds = trades.stream().map(Trade::getBuyer).map(Buyer::getId).collect(Collectors.toList());
                CustomerDetailListByConditionRequest request = CustomerDetailListByConditionRequest.builder().customerIds(customerIds).build();
                payOrderRequest.setCustomerDetailIds(customerCommonService.listCustomerDetailIdsByCondition(request));

                if (CollectionUtils.isEmpty(payOrderRequest.getCustomerDetailIds())) {
                    customerLike = false;
                }
            }
        }
        // 模糊银行账号
        boolean OfflineAccountLike = true;

        if (StringUtils.isNotBlank(payOrderRequest.getAccount())) {
            List<Long> offlineAccountIds = customerCommonService.listOfflineAccountIdsByBankNo(new
                    OfflineAccountListWithoutDeleteFlagByBankNoRequest(payOrderRequest.getAccount
                    ()));

            payOrderRequest.setAccountIds(offlineAccountIds);

            if (CollectionUtils.isEmpty(offlineAccountIds)) {
                OfflineAccountLike = false;
            }
        }

        return supplierLike && customerLike && OfflineAccountLike;
    }


    /**
     * 构建动态查
     *
     * @param payOrderRequest
     * @return
     */
    private Specification<PayOrder> findByRequest(final PayOrderRequest payOrderRequest) {
        return (Root<PayOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> buildWhere(payOrderRequest, root,
                query, cb);
    }

    /**
     * 构建列表查询的where条件
     *
     * @param payOrderRequest request
     * @param root            root
     * @param query           query
     * @param cb              bc
     * @return predicates
     */
    private Predicate buildWhere(PayOrderRequest payOrderRequest, Root<PayOrder> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<PayOrder, Receivable> payOrderReceivableJoin = root.join("receivable", JoinType.LEFT);
        payOrderReceivableJoin.on(cb.equal(payOrderReceivableJoin.get("delFlag"), DeleteFlag.NO));

        if (!StringUtils.isEmpty(payOrderRequest.getOrderNo()) && !StringUtils.isEmpty(payOrderRequest.getOrderNo()
                .trim())) {
            predicates.add(cb.equal(root.get("orderCode"), payOrderRequest.getOrderNo()));
        }

        if (!StringUtils.isEmpty(payOrderRequest.getOrderCode()) && !StringUtils.isEmpty(payOrderRequest.getOrderCode
                ().trim())) {
            predicates.add(cb.like(root.get("orderCode"), buildLike(payOrderRequest.getOrderCode())));
        }

        //收款单编号
        if (!StringUtils.isEmpty(payOrderRequest.getPayBillNo()) && !StringUtils.isEmpty(payOrderRequest.getPayBillNo
                ().trim())) {
            predicates.add(cb.like(payOrderReceivableJoin.get("receivableNo"), buildLike(payOrderRequest.getPayBillNo
                    ())));
        }
        //支付单状态
        if (Objects.nonNull(payOrderRequest.getPayOrderStatus())) {
            predicates.add(cb.equal(root.get("payOrderStatus"), payOrderRequest.getPayOrderStatus()));
        }

        if (Objects.nonNull(payOrderRequest.getCompanyInfoId())) {
            predicates.add(cb.equal(root.get("companyInfoId"), payOrderRequest.getCompanyInfoId()));
        }

        if (CollectionUtils.isNotEmpty(payOrderRequest.getCompanyInfoIds())) {
            predicates.add(root.get("companyInfoId").in(payOrderRequest.getCompanyInfoIds()));
        }

        if (CollectionUtils.isNotEmpty(payOrderRequest.getCustomerDetailIds())) {
            predicates.add(root.get("customerDetailId").in(payOrderRequest.getCustomerDetailIds()));
        }

        if (Objects.nonNull(payOrderRequest.getPayChannelId())) {
            predicates.add(cb.equal(payOrderReceivableJoin.get("payChannelId"), payOrderRequest.getPayChannelId()));
        }

        if (Objects.nonNull(payOrderRequest.getPayType())) {
            predicates.add(cb.equal(root.get("payType"), payOrderRequest.getPayType()));
        }

//        //账号名称离线查询
//        if (!StringUtils.isEmpty(payOrderRequest.getAccount())) {
//            predicates.add(cb.like(receivableOfflineAccountJoin.get("bankNo"), buildLike(payOrderRequest.getAccount()
//            )));
//        }
        if (!StringUtils.isEmpty(payOrderRequest.getAccount())) {
            predicates.add(payOrderReceivableJoin.get("offlineAccountId").in(payOrderRequest.getAccountIds()));
        }

        //收款开始时间
        if (!StringUtils.isEmpty(payOrderRequest.getStartTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            predicates.add(cb.greaterThanOrEqualTo(payOrderReceivableJoin.get("createTime"), LocalDateTime.of(LocalDate
                    .parse(payOrderRequest.getStartTime(), formatter), LocalTime.MIN)));
        }

        //收款
        if (!StringUtils.isEmpty(payOrderRequest.getEndTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            predicates.add(cb.lessThanOrEqualTo(payOrderReceivableJoin.get("createTime"),
                    LocalDateTime.of(LocalDate
                            .parse(payOrderRequest.getEndTime(), formatter), LocalTime.MIN)));
        }

        //线下账户查询
        if (!StringUtils.isEmpty(payOrderRequest.getAccountId())) {
            predicates.add(cb.equal(payOrderReceivableJoin.get("offlineAccountId"), payOrderRequest.getAccountId()));
        }

        if (!CollectionUtils.isEmpty(payOrderRequest.getPayOrderIds())) {
            predicates.add(root.get("payOrderId").in(payOrderRequest.getPayOrderIds()));
        }

        //删除条件
        predicates.add(cb.equal(root.get("delFlag"), DeleteFlag.NO));
        if (payOrderRequest.getSortByReceiveTime()) {
            query.orderBy(cb.desc(payOrderReceivableJoin.get("createTime")));
        } else {
            query.orderBy(cb.desc(root.get("createTime")));
        }

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

    private static String buildLike(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
    }


    @Transactional
    public int updateByPayOrderNo(String orderCodeNo, String payOrderId) {
        return payOrderRepository.updateByPayOrderNo(orderCodeNo,payOrderId);
    }
}
