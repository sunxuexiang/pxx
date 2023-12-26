package com.wanmi.sbc.returnorder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeDeleteRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponsByCodeIdsRequest;
import com.wanmi.sbc.marketing.api.request.coupon.TakeBackOrderCoinRequest;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderNotAuditProducerRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.CoinActivityPushKingdeeRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCoinReturnVerifyRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnCountByConditionResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderListByTidResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderTransferByUserIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeCoinReturnVerifyResponse;
import com.wanmi.sbc.order.bean.dto.CompanyDTO;
import com.wanmi.sbc.order.bean.dto.ReturnItemDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by sunkun on 2017/7/10.
 */
@Slf4j
@RestController
@RequestMapping("/return")
@Api(tags = "ReturnOrderController", description = "mobile退单Api")
public class ReturnOrderController {

    @Value("${return-order.auto-refund.enabled:true}")
    boolean returnOrderAutoRefundEnabled;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private TradeProvider tradeProvider;

    /**
     * 创建退单
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse<String> create(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        StopWatch stopWatch = new StopWatch("创建退单-APP");
        stopWatch.start("创建退单");
        verifyIsReturnable(returnOrder.getTid());
        //验证用户
        String userId = commonUtil.getOperatorId();
//        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
//                (userId)).getContext();
//        if (!verifyTradeByCustomerId(returnOrder.getTid(), userId)) {
//            throw new SbcRuntimeException("K-050204");
//        }
        ReturnOrderVO oldReturnOrderTemp = returnOrderQueryProvider.getTransferByUserId(
                ReturnOrderTransferByUserIdRequest.builder().userId(userId).build()).getContext();
        if (oldReturnOrderTemp == null) {
            throw new SbcRuntimeException("K-000001");
        }
        ReturnOrderDTO oldReturnOrder = KsBeanUtil.convert(oldReturnOrderTemp, ReturnOrderDTO.class);


        oldReturnOrder.setReturnReason(returnOrder.getReturnReason());
        oldReturnOrder.setDescription(returnOrder.getDescription());
        oldReturnOrder.setImages(returnOrder.getImages());
        oldReturnOrder.setReturnLogistics(returnOrder.getReturnLogistics());
        oldReturnOrder.setReturnWay(returnOrder.getReturnWay());
        oldReturnOrder.setReturnPrice(returnOrder.getReturnPrice());
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        oldReturnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName()).companyType(trade.getSupplier().getCompanyType()).build());
        //
        oldReturnOrder.setChannelType(trade.getChannelType());
        oldReturnOrder.setDistributorId(trade.getDistributorId());
        oldReturnOrder.setInviteeId(trade.getInviteeId());
        oldReturnOrder.setShopName(trade.getShopName());
        oldReturnOrder.setDistributorName(trade.getDistributorName());
        oldReturnOrder.setDistributeItems(trade.getDistributeItems());
        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            oldReturnOrder.setSendCouponCodeIds(trade.getSendCouponCodeIds());
        }

        if (Objects.nonNull(trade.getReturnCoin())) {
            Map<String, BigDecimal> returnCoinMap = trade.getTradeItems().stream().filter(o -> Objects.nonNull(o.getReturnCoin())).collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getReturnCoin, (o1, o2) -> o1));
            BigDecimal returnCoin = returnOrder.getReturnItems().stream().map(o -> {
                if (Objects.nonNull(returnCoinMap.get(o.getSkuId()))) {
                    return returnCoinMap.get(o.getSkuId());
                } else {
                    return BigDecimal.ZERO;
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                        .getContext().getCustomerWalletVO();
                if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                    throw new SbcRuntimeException("K-050601");
                }
            }
        }

        //退单：填充订单类型
        oldReturnOrder.setActivityType(trade.getActivityType());
        String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(oldReturnOrder)
                .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();
        stopWatch.stop();

        stopWatch.start("退单快照删除");
        returnOrderProvider.deleteTransfer(ReturnOrderTransferDeleteRequest.builder().userId(userId).build());
        stopWatch.stop();

        stopWatch.start("删除用户优惠券");
        if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            couponCodeProvider.delByCustomerIdAndCouponIds(CouponCodeDeleteRequest.builder()
                    .customerId(trade.getBuyer().getId()).couponCodeIds(trade.getSendCouponCodeIds()).build());
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return BaseResponse.success(rid);
    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退款单")
    @RequestMapping(value = "/addRefund", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse<String> createRefund(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        verifyIsReturnable(returnOrder.getTid());
        //验证用户
      /*  CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (commonUtil.getOperatorId())).getContext();*/
//        if (!verifyTradeByCustomerId(returnOrder.getTid(), commonUtil.getOperatorId())) {
//            throw new SbcRuntimeException("K-050204");
//        }
        String tid = returnOrder.getTid();

        String key = "addRefundLock"+commonUtil.getCustomer().getCustomerId()+tid;
//        ReturnCountByConditionResponse context = returnOrderQueryProvider.countByCondition(ReturnCountByConditionRequest.builder().tid(tid).build()).getContext();
//        if (context.getCount().compareTo(0l)>0){
//            return BaseResponse.FAILED();
//        }
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();

        if (Objects.nonNull(trade.getReturnCoin())) {
            Map<String, BigDecimal> returnCoinMap = trade.getTradeItems().stream().filter(o -> Objects.nonNull(o.getReturnCoin())).collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getReturnCoin, (o1, o2) -> o1));
            BigDecimal returnCoin = returnOrder.getReturnItems().stream().map(o -> {
                if (Objects.nonNull(returnCoinMap.get(o.getSkuId()))) {
                    return returnCoinMap.get(o.getSkuId());
                } else {
                    return BigDecimal.ZERO;
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                        .getContext().getCustomerWalletVO();
                if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                    throw new SbcRuntimeException("K-050601");
                }
            }

        }

        RLock rLock = redissonClient.getLock(key);
        Boolean lockStatus =rLock.tryLock();
        if (lockStatus){
            try {


                // 如果已经退单成功，直接返回正确的结果
                BaseResponse<ReturnOrderListByTidResponse> returnOrderListByTidResponseBaseResponse = returnOrderQueryProvider.listByTid(ReturnOrderListByTidRequest.builder().tid(tid).build());
                List<ReturnOrderVO> returnOrderVOS = Optional.ofNullable(returnOrderListByTidResponseBaseResponse)
                        .map(BaseResponse::getContext)
                        .map(ReturnOrderListByTidResponse::getReturnOrderList).orElse(Lists.newArrayList());
                // log.info("退单列表：{}",JSON.toJSONString(returnOrderVOS));
                if(CollectionUtils.isNotEmpty(returnOrderVOS)){
                    // log.info("退单成功直接返回:{}",JSON.toJSONString(returnOrderVOS));
                    Optional<ReturnOrderVO> first = returnOrderVOS.stream().filter(x -> Arrays.asList(
                            ReturnFlowState.INIT,
                            ReturnFlowState.AUDIT,
                            ReturnFlowState.DELIVERED,
                            ReturnFlowState.RECEIVED,
                            ReturnFlowState.REFUNDED,
                            ReturnFlowState.COMPLETED
                        ).contains(x.getReturnFlowState())).findFirst();
                    if(first.isPresent()){
                        log.info("退单ID:{}",first.get().getId());
                        return BaseResponse.success(first.get().getId());
                    }
                }
                ReturnCountByConditionResponse context1 = returnOrderQueryProvider.countByCondition(ReturnCountByConditionRequest.builder().tid(tid).build()).getContext();
                if (context1.getCount().compareTo(0l)>0){
                    return BaseResponse.FAILED();
                }

                returnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                        .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                        .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName())
                        .companyType(trade.getSupplier().getCompanyType())
                        .build());
                returnOrder.setChannelType(trade.getChannelType());
                returnOrder.setDistributorId(trade.getDistributorId());
                returnOrder.setInviteeId(trade.getInviteeId());
                returnOrder.setShopName(trade.getShopName());
                returnOrder.setDistributorName(trade.getDistributorName());
                returnOrder.setDistributeItems(trade.getDistributeItems());
                returnOrder.setSaleType(trade.getSaleType());

                // log.info("订单类型activityType:{},订单信息：{}",trade.getActivityType(),trade);
                if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
                    returnOrder.setSendCouponCodeIds(trade.getSendCouponCodeIds());
                }

                returnOrder.setActivityType(trade.getActivityType());
                String rid = returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                        .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();

                if (CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
                    couponCodeProvider.delByCustomerIdAndCouponIds(CouponCodeDeleteRequest.builder()
                            .customerId(trade.getBuyer().getId()).couponCodeIds(trade.getSendCouponCodeIds()).build());
                }
                //订单未发货
                if(DeliverStatus.NOT_YET_SHIPPED.equals(trade.getTradeState().getDeliverStatus())){
                    //该订单为线上付款 并且为支付宝或微信付款或者招商银行或者余额
                    //且退单自动退款开关为打开状态
                    if(returnOrderAutoRefundEnabled && Objects.nonNull(trade.getPayInfo()) && PayType.ONLINE.toValue() == Integer.parseInt(trade.getPayInfo().getPayTypeId())
                            && (PayWay.ALIPAY.equals(trade.getPayWay())
                            || PayWay.WECHAT.equals(trade.getPayWay())
                            || PayWay.CMB.equals(trade.getPayWay())
                            || PayWay.BALANCE.equals(trade.getPayWay())
                            || PayWay.CUPSALI.equals(trade.getPayWay())
                            || PayWay.CUPSWECHAT.equals(trade.getPayWay())
                            || PayWay.CCB.equals(trade.getPayWay())
                            || (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())&&
                            PayWay.UNIONPAY.equals(trade.getPayWay()))
                    )
                    ){
                        // 鲸币退回
                        if (Objects.nonNull(trade.getReturnCoin())) {
                            Map<String, BigDecimal> returnCoinMap = trade.getTradeItems().stream().filter(o -> Objects.nonNull(o.getReturnCoin())).collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getReturnCoin, (o1, o2) -> o1));
                            BigDecimal returnCoin = returnOrder.getReturnItems().stream().map(o -> {
                                if (Objects.nonNull(returnCoinMap.get(o.getSkuId()))) {
                                    return returnCoinMap.get(o.getSkuId());
                                } else {
                                    return BigDecimal.ZERO;
                                }
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);

                            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                                // 修改钱包
//                                String buyerId = trade.getBuyer().getId();
//                                ModifyWalletBalanceForCoinActivityRequest modifyBalanceRequest = new ModifyWalletBalanceForCoinActivityRequest();
//                                modifyBalanceRequest.setAmount(returnCoin);
//                                modifyBalanceRequest.setRelationId(rid);
//                                modifyBalanceRequest.setBuyerId(buyerId);
//                                modifyBalanceRequest.setCustomerAccount(trade.getBuyer().getAccount());
//                                modifyBalanceRequest.setWalletDetailsType(WalletDetailsType.CANCEL_GOODS_RECHARGE);
//                                modifyBalanceRequest.setBudgetType(BudgetType.EXPENDITURE);
//                                customerWalletProvider.modifyWalletBalanceForCoin(modifyBalanceRequest);

                            	// 取消返鲸币商家鲸币余额增加，用户鲸币余额减少
                                Long storeId = trade.getSupplier().getStoreId();
                				String tradeRemark = WalletDetailsType.CANCEL_GOODS_RECHARGE.getDesc() + "-" + rid;
                				String customerId = trade.getBuyer().getId();
                				CustomerWalletOrderByRequest orderByRequest = CustomerWalletOrderByRequest.builder()
                						.customerId(customerId).storeId(storeId.toString()).balance(returnCoin)
                						.relationOrderId(rid).tradeRemark(tradeRemark).remark(tradeRemark)
                						.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
                						.build();
                				BaseResponse<WalletRecordVO> orderByGiveStore = customerWalletProvider.orderByGiveStore(orderByRequest);
                            	
//                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//                                String sendNo = "TZS" + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
                		        String sendNo = orderByGiveStore.getContext().getSendNo();

                                //  保存退回记录
                                String customerAccount = trade.getBuyer().getAccount();
                                LocalDateTime returnTime = returnOrder.getCreateTime();
                                BigDecimal returnPrice = returnOrder.getReturnPrice().getTotalPrice();
                                LocalDateTime now = LocalDateTime.now();
                                List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItemDTO::getSkuId).collect(Collectors.toList());
                                Map<String, List<CoinActivityRecordDetailDto>> detailMap = coinActivityProvider.queryCoinActivityRecordByOrderIdAndSkuIds(trade.getId(), skuIds).getContext()
                                        .stream().collect(Collectors.groupingBy(CoinActivityRecordDetailDto::getActivityId));

                                List<CoinActivityRecordDto> saveRequest = new ArrayList<>();
                                detailMap.forEach((k, v) -> {
                                    CoinActivityRecordDto recordDto = new CoinActivityRecordDto();
                                    recordDto.setSendNo(sendNo);
                                    recordDto.setActivityId(k);
                                    recordDto.setCustomerAccount(customerAccount);
                                    recordDto.setOrderNo(rid);
                                    recordDto.setOrderTime(returnTime);
                                    recordDto.setOrderPrice(returnPrice);
                                    recordDto.setRecordType(2);
                                    recordDto.setRecordTime(now);

                                    List<CoinActivityRecordDetailDto> detailDtoList = new ArrayList<>();
                                    for (CoinActivityRecordDetailDto detailDto : v) {
                                        detailDto.setDetailId(null);
                                        detailDto.setRecordId(null);
                                        detailDto.setOrderNo(rid);
                                        detailDto.setRecordType(2);
                                        detailDto.setRecordTime(now);

                                        detailDtoList.add(detailDto);
                                    }
                                    BigDecimal totalCoinNum = detailDtoList.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);

                                    recordDto.setCoinNum(totalCoinNum);
                                    recordDto.setDetailList(detailDtoList);
                                    saveRequest.add(recordDto);

                                });
                                coinActivityProvider.saveCoinRecord(saveRequest);

                                // 推送金蝶
//                                CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//                                        .builder()
//                                        .tid(rid)
//                                        .buyerAccount(trade.getBuyer().getAccount())
//                                        .applyPrice(returnCoin)
//                                        .saleType(trade.getSaleType())
//                                        .sendNo(sendNo)
//                                        .build();
//
//                                tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
                            }

                        }
                        
                        // 订单返鲸币活动鲸币收回
                        coinActivityProvider.takeBackOrderCoin(TakeBackOrderCoinRequest.builder().rid(rid).needThrowException(true).build());

                        log.info("==========线上自动退款:{}",trade.getPayWay());
                        RefundOrderNotAuditProducerRequest refundOrderNotAuditProducerRequest = new RefundOrderNotAuditProducerRequest();
                        refundOrderNotAuditProducerRequest.setOperator(commonUtil.getOperator());
                        refundOrderNotAuditProducerRequest.setRId(rid);
                        refundOrderNotAuditProducerRequest.setTId(returnOrder.getTid());
                        refundOrderNotAuditProducerRequest.setNewPickOrderFlag(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType()));
                        //发送mq消息，退款无需审核
                        returnOrderProvider.refundOrderNotAuditProducer(refundOrderNotAuditProducerRequest);

                    } else {
                        log.info("线下付款，需审核：订单号={}，trade.getPayInfo()={}，自动退款开关={}", trade.getId(), trade.getPayInfo(), returnOrderAutoRefundEnabled);
                    }
                }
                return BaseResponse.success(rid);

            }   finally {
                rLock.unlock();
            }
        }
        return  BaseResponse.FAILED();

    }

    /**
     * 创建退单快照
     *
     * @param returnOrder
     * @return
     */
    @ApiOperation(value = "创建退单快照")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse transfer(@RequestBody @Valid ReturnOrderDTO returnOrder) {
        BaseResponse<FindPayOrderResponse> response =
                payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(returnOrder.getTid()).build());

        FindPayOrderResponse payOrderResponse = response.getContext();
        if (Objects.isNull(payOrderResponse) || Objects.isNull(payOrderResponse.getPayOrderStatus()) || payOrderResponse.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050103");
        }
        verifyIsReturnable(returnOrder.getTid());
        //验证用户
//        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
//                (commonUtil.getOperatorId())).getContext();
//        if (!verifyTradeByCustomerId(returnOrder.getTid(), commonUtil.getOperatorId())) {
//            throw new SbcRuntimeException("K-050204");
//        }
        ReturnOrderTransferAddRequest build = ReturnOrderTransferAddRequest.builder().returnOrder(returnOrder)
                .operator(commonUtil.getOperator()).build();

        // log.info("build {}", JSONObject.toJSONString(build));
        returnOrderProvider.addTransfer(build);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 校验是否可退
     *
     * @param tid
     */
    private void verifyIsReturnable(String tid) {
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        commonUtil.checkIfStore(trade.getSupplier().getStoreId());

        if(CollectionUtils.isNotEmpty(trade.getSendCouponCodeIds())) {
            List<CouponCodeDTO> couponCodeList = couponCodeQueryProvider.findCouponsByCouponCodeIds(CouponsByCodeIdsRequest
                    .builder().couponCodeIds(trade.getSendCouponCodeIds()).build()).getContext().getCouponCodeList();
            // log.info("=================》退单校验是否赠券，couponCodeList：：：{}",couponCodeList);
            boolean useStatus = couponCodeList.stream().anyMatch(coupon -> coupon.getUseStatus().equals(DefaultFlag.YES));
            log.info("=================》使用状态，useStatus：：：{}",useStatus);
            if (useStatus) {
                throw new SbcRuntimeException("K-050320");
            }
        }

        // if (CollectionUtils.isNotEmpty(trade.getSendCoinRecordIds())) {
        /*if (Objects.nonNull(trade.getReturnCoin())) {
            CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                    .getContext().getCustomerWalletVO();
            if (trade.getReturnCoin().compareTo(customerWalletVO.getBalance()) > 0) {
                throw new SbcRuntimeException("K-050601");
            }
        }*/

        if (Objects.nonNull(trade.getTradeState().getDeliverStatus()) && (trade.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || trade.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
            TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
            request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
            TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
            if (config.getStatus() == 0) {
                throw new SbcRuntimeException("K-050208");
            }
            JSONObject content = JSON.parseObject(config.getContext());
            Integer day = content.getObject("day", Integer.class);

            if (Objects.isNull(trade.getTradeState().getEndTime())) {
                throw new SbcRuntimeException("K-050002");
            }
            if (trade.getTradeState().getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < LocalDateTime.now().minusDays(day).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                throw new SbcRuntimeException("K-050208");
            }
        }
    }

    /**
     * 查询退单快照
     *
     * @return
     */
    @ApiOperation(value = "查询退单快照")
    @RequestMapping(value = "/findTransfer", method = RequestMethod.GET)
    public BaseResponse<ReturnOrderTransferByUserIdResponse> transfer() {
        ReturnOrderTransferByUserIdResponse response = returnOrderQueryProvider.getTransferByUserId(
                ReturnOrderTransferByUserIdRequest.builder()
                        .userId(commonUtil.getOperatorId()).build()).getContext();
        if (Objects.nonNull(response)
                && Objects.nonNull(response.getCompany())) {
            commonUtil.checkIfStore(response.getCompany().getStoreId());
        }
        return BaseResponse.success(response);
    }

    private boolean verifyTradeByCustomerId(String tid, String customerId) {
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        return trade.getBuyer().getId().equals(customerId);
    }


    /**
     * 是否可创建退单
     *
     * @return
     */
    @ApiOperation(value = "是否可创建退单")
    @RequestMapping(value = "/returnable/{tid}", method = RequestMethod.GET)
    public BaseResponse isReturnable(@PathVariable String tid) {
        verifyIsReturnable(tid);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 是否可创建退单
     *
     * @return
     */
    @ApiOperation(value = "金币余额是否足够退款")
    @PostMapping(value = "/returnable/coin/verify")
    public BaseResponse<TradeCoinReturnVerifyResponse> coinVerify(@RequestBody TradeCoinReturnVerifyRequest request) {
        Assert.hasText(request.getTid(), "订单ID不能为空");

        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(request.getTid()).build()).getContext().getTradeVO();

        Assert.notNull(trade, "订单不存在");

        TradeCoinReturnVerifyResponse response = new TradeCoinReturnVerifyResponse();
        response.setCanReturnFlag(true);
        if (CollectionUtils.isNotEmpty(request.getGoodsInfoIds())) {
            List<CoinActivityRecordDetailDto> coinDetails = coinActivityProvider.queryCoinActivityRecordByOrderIdAndSkuIds(trade.getId(), request.getGoodsInfoIds()).getContext();
            if (CollectionUtils.isNotEmpty(coinDetails)) {
                BigDecimal returnCoin = coinDetails.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);

                if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                    CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                            .getContext().getCustomerWalletVO();
                    if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                        response.setCanReturnFlag(false);
                        response.setMsg("您选择的订单参加了返鲸币活动，您的鲸币余额已不足退货退款抵扣，请联系客服处理！");
                    }

                }
            }

        } else {
            if (Objects.nonNull(trade.getReturnCoin())) {
                CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                        .getContext().getCustomerWalletVO();

                BigDecimal returnCoin = trade.getReturnCoin();
                if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                    if (returnCoin.compareTo(customerWalletVO.getBalance()) > 0) {
                        response.setCanReturnFlag(false);
                        response.setMsg("您选择的订单参加了返鲸币活动，您的鲸币余额已不足退货退款抵扣，请联系客服处理！");
                    }
                }
            }
        }
        return BaseResponse.success(response);
    }
}
