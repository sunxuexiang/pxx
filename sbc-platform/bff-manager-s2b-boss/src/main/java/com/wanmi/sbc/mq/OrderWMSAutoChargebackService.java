package com.wanmi.sbc.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.TakeBackOrderCoinRequest;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamountTrade.InventoryDetailSamountTradeProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamountTrade.InventoryDetailSamountTradeRequest;
import com.wanmi.sbc.order.api.request.returnorder.*;
import com.wanmi.sbc.order.api.request.trade.CoinActivityPushKingdeeRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeOutRequest;
import com.wanmi.sbc.order.api.response.inventorydetailsamounttrade.InventoryDetailSamountTradeResponse;
import com.wanmi.sbc.order.bean.dto.CompanyDTO;
import com.wanmi.sbc.order.bean.dto.InventoryDetailSamountTradeDTO;
import com.wanmi.sbc.order.bean.dto.ReturnItemDTO;
import com.wanmi.sbc.order.bean.dto.ReturnOrderDTO;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>描述<p>
 * wms货物不足退款
 *
 * @author zhaowei
 * @date 2021/4/16
 */
@Service
@Slf4j
@EnableBinding(BossSink.class)
public class OrderWMSAutoChargebackService {

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private InventoryDetailSamountTradeProvider inventoryDetailSamountTradeProvider;

    /**
     * 操作人默认为系统管理员
     */
    private static final String DEFAULT_EMPLOYEE = "system";

    //强制退款状态
    private static final Integer FORCE_REFUND = 1;

    //默认推困
    private static final Long RETURN_POINTS = 0l;

    /**
     * 退货
     */
    @Value("${cjxxyls.return.url}")
    private String returnUrl;

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;
    
    @Autowired
    private CustomerWalletProvider customerWalletProvider;



    /**
     * wms实际发货数量与少发数量 生成退单
     *
     * @param
     * @return: void
     */
    @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_MESSAGE_WMS_AUTO_CHARGEBACK)
    public void wmsAutoChargeback(String json) {
        log.info("=============== wms实际发货数量与少发数量，生成退单MQ处理start ===============");
        //默认操作人
        EmployeeVO employee = employeeQueryProvider.getByAccountName(
                EmployeeByAccountNameRequest.builder()
                        .accountName(DEFAULT_EMPLOYEE)
                        .accountType(AccountType.s2bBoss).build()).getContext().getEmployee();
        Operator operator = Operator.builder()
                .account(employee.getAccountName())
                .platform(Platform.PLATFORM)
                .adminId(employee.getEmployeeId())
                .name(employee.getEmployeeName())
                .userId(employee.getEmployeeId())
                .build();
        //生成退单id
        TradeOutRequest request = JSON.parseObject(json, TradeOutRequest.class);

        //自动生成退单
        ReturnOrderDTO returnOrder = request.getReturnOrderDTO();
        //释放囤货库存
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        returnOrder.setActivityType(trade.getActivityType());
        if (TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
            returnOrder.setDescription("仓库库存不足，自动退单，商品退回客户囤货明细");
        } else {
            returnOrder.setDescription("仓库库存不足，自动退单");
            returnOrder.setSourceChannel(trade.getSourceChannel());
        }
        returnOrder.setSourceChannel(trade.getSourceChannel());
        if (Objects.nonNull(trade.getSourceChannel())) {
            if (trade.getSourceChannel().contains("chain")) {
                log.info("连锁的退款订单转发:" + trade.getId());
                //如果是连锁的订单转发
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("result", json);
                try {
                    HttpCommonUtil.postHeader(returnUrl, requestMap, null);
                } catch (Exception e) {
                    log.error("连锁的退款订单转发错误:", e);
                } finally {
                    return;
                }
            }
        }

        String rid = this.generateChargeback(returnOrder, operator);
        log.info("=============== wms自动生成退单id:" + rid + " ===============");
        ReturnOrderVO returnOrderVO = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid)
                .build()).getContext();

        // 参加鲸币活动 鲸币退回
        List<String> skuIds = returnOrderVO.getReturnItems().stream().map(ReturnItemVO::getSkuId).collect(Collectors.toList());
        List<CoinActivityRecordDetailDto> coinDetails = coinActivityProvider.queryCoinActivityRecordByOrderIdAndSkuIds(trade.getId(), skuIds).getContext();
        if (CollectionUtils.isNotEmpty(coinDetails)) {
            Map<String, ReturnItemVO> returnItemMap = returnOrderVO.getReturnItems().stream().collect(Collectors.toMap(ReturnItemVO::getSkuId, Function.identity(), (o1, o2) -> o1));
            Map<String, TradeItemVO> tradeItemMap = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItemVO::getSkuId, Function.identity(), (o1, o2) -> o1));

            coinDetails = coinDetails.stream().filter(o -> {
                if (Objects.equals(DefaultFlag.YES, o.getIsOverlap())) {
                    return true;
                } else {

                    BigDecimal cancelNumber = BigDecimal.valueOf(returnItemMap.get(o.getGoodsInfoId()).getNum());
                    Long tradeNum = tradeItemMap.get(o.getGoodsInfoId()).getNum();
                    if (cancelNumber.compareTo(BigDecimal.valueOf(tradeNum)) >= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }).collect(Collectors.toList());
            BigDecimal returnCoin = coinDetails.stream().map(o -> {
                if (Objects.equals(DefaultFlag.YES, o.getIsOverlap())) {
                    return o.getSingleCoinNum().multiply(BigDecimal.valueOf(returnItemMap.get(o.getGoodsInfoId()).getNum()));
                } else {
                    return o.getCoinNum();
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                        .getContext().getCusWalletVO();
                if (returnCoin.compareTo(cusWalletVO.getBalance()) > 0) {
                	// 此处由于没有扣除用户鲸币，存在一定风险
                    log.info("=============== 自动退单：{} 鲸币退回余额不足，应该退鲸币：{}，用户余额鲸币：{}", rid, returnCoin, cusWalletVO.getBalance());
                }else {
                    // 修改钱包
//                    String buyerId = trade.getBuyer().getId();
//                    ModifyWalletBalanceForCoinActivityRequest modifyBalanceRequest = new ModifyWalletBalanceForCoinActivityRequest();
//                    modifyBalanceRequest.setAmount(returnCoin);
//                    modifyBalanceRequest.setRelationId(rid);
//                    modifyBalanceRequest.setBuyerId(buyerId);
//                    modifyBalanceRequest.setCustomerAccount(trade.getBuyer().getAccount());
//                    modifyBalanceRequest.setWalletDetailsType(WalletDetailsType.CANCEL_GOODS_RECHARGE);
//                    modifyBalanceRequest.setBudgetType(BudgetType.EXPENDITURE);
//                    customerWalletProvider.modifyWalletBalanceForCoin(modifyBalanceRequest);
                	
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

//                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//                    String sendNo = "TZS" + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
                    String sendNo = orderByGiveStore.getContext().getSendNo();

                    //  保存退回记录
                    String customerAccount = trade.getBuyer().getAccount();
                    LocalDateTime returnTime = returnOrder.getCreateTime();
                    BigDecimal returnPrice = returnOrder.getReturnPrice().getTotalPrice();
                    LocalDateTime now = LocalDateTime.now();
                    Map<String, List<CoinActivityRecordDetailDto>> detailMap = coinDetails.stream().collect(Collectors.groupingBy(CoinActivityRecordDetailDto::getActivityId));

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
                            detailDto.setGoodsNum(returnItemMap.get(detailDto.getGoodsInfoId()).getNum());
                            if (Objects.equals(DefaultFlag.YES, detailDto.getIsOverlap())) {
                                detailDto.setCoinNum(detailDto.getSingleCoinNum().multiply(BigDecimal.valueOf(returnItemMap.get(detailDto.getGoodsInfoId()).getNum())));
                            }

                            detailDtoList.add(detailDto);
                        }

                        recordDto.setCoinNum(returnCoin);
                        recordDto.setDetailList(detailDtoList);
                        saveRequest.add(recordDto);

                    });
                    coinActivityProvider.saveCoinRecord(saveRequest);

                    // 推送金蝶
//                    CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//                            .builder()
//                            .tid(rid)
//                            .buyerAccount(trade.getBuyer().getAccount())
//                            .applyPrice(returnCoin)
//                            .saleType(trade.getSaleType())
//                            .sendNo(sendNo)
//                            .build();

//                    tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
                }

            }
        }
        
        // 订单返鲸币活动鲸币收回
        coinActivityProvider.takeBackOrderCoin(TakeBackOrderCoinRequest.builder().rid(rid).needThrowException(false).build());
        
        
        if (Objects.nonNull(returnOrderVO.getPayType()) && PayType.OFFLINE.equals(returnOrderVO.getPayType())) {
            log.info("======================退单支付状态为线下退款 ===================================");
            return;
        }

        //商家退款
        log.info("====================== 商家退款 ===================================");
        //this.businessRefund(returnOrderVO, operator, returnOrder.getReturnPrice().getActualReturnPrice());
        this.businessRefund(returnOrderVO, operator, returnOrderVO.getReturnPrice().getApplyPrice());

        //boss端退款
        log.info("======================== 平台退款 ===================================");
        this.bossRefund(rid, operator);

        if (TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
            this.freePileGoodsNum(rid);
            log.info("======================== wms自动退单释放囤货库存 ===================================");
        }
    }

    //自动生成退单
    private String generateChargeback(ReturnOrderDTO returnOrder, Operator operator) {
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(returnOrder.getTid()).build()).getContext().getTradeVO();
        returnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName()).companyType(trade.getSupplier().getCompanyType()).build());
        returnOrder.setWmsStats(true);
        assignReturnItemsInventoryDetailSamountTrades(returnOrder);
        return returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                .operator(operator).forceRefund(FORCE_REFUND).build()).getContext().getReturnOrderId();
    }

    /**
     * 赋值
     * -- returnOrder->returnItems->inventoryDetailSamountTrades
     *
     * @param returnOrder
     */
    private void assignReturnItemsInventoryDetailSamountTrades(ReturnOrderDTO returnOrder) {
        if (!TradeActivityTypeEnum.TRADE.toActivityType().equals(returnOrder.getActivityType())) {
            return;
        }
        //获取订单商品价格信息与退货信息
        BaseResponse<InventoryDetailSamountTradeResponse> inventoryByOId = inventoryDetailSamountTradeProvider.getInventoryAdaptive(InventoryDetailSamountTradeRequest.builder().oid(returnOrder.getTid()).build());
        if (inventoryByOId.getContext() == null || CollectionUtils.isEmpty(inventoryByOId.getContext().getInventoryDetailSamountTradeVOS())) {
            throw new SbcRuntimeException("获取可退商品错误!");
        }
        List<ReturnItemDTO> returnItems = returnOrder.getReturnItems();
        if (CollectionUtils.isEmpty(returnItems)) {
            return;
        }
        for (ReturnItemDTO returnItem : returnItems) {
            //退货数量
            BigDecimal num = returnItem.getNum();
            if (num.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS = inventoryByOId.getContext().getInventoryDetailSamountTradeVOS();
            List<InventoryDetailSamountTradeVO> collect = inventoryDetailSamountTradeVOS
                    .stream().filter(inventoryDetailSamountTradeVO -> StringUtils.equals(inventoryDetailSamountTradeVO.getGoodsInfoId(), returnItem.getSkuId())).collect(Collectors.toList());
            assignReturnInventoryDetailSamountTradeVOs(returnItem, collect, 1, num);
            assignReturnInventoryDetailSamountTradeVOs(returnItem, collect, 0, num);
        }
    }

    private void assignReturnInventoryDetailSamountTradeVOs(ReturnItemDTO returnItem, List<InventoryDetailSamountTradeVO> collect, int moneyType, BigDecimal num) {
        //未退款
        int returnFlag = 0;
        List<InventoryDetailSamountTradeVO> collect2 = collect
                .stream()
                .filter(inventoryDetailSamountTradeVO -> Objects.equals(inventoryDetailSamountTradeVO.getMoneyType(), moneyType))
                .filter(inventoryDetailSamountTradeVO -> Objects.equals(inventoryDetailSamountTradeVO.getReturnFlag(), returnFlag))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect2) || collect2.size() < num.intValue()) {
            return;
        }

        List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS = collect2.subList(0, num.intValue());
        for (InventoryDetailSamountTradeVO inventoryDetailSamountTradeVO : inventoryDetailSamountTradeVOS) {
            inventoryDetailSamountTradeVO.setReturnFlag(2);
        }

        if (CollectionUtils.isEmpty(returnItem.getInventoryDetailSamountTrades())) {
            returnItem.setInventoryDetailSamountTrades(new ArrayList<>());
        }

        List<InventoryDetailSamountTradeDTO> convert = KsBeanUtil.convert(inventoryDetailSamountTradeVOS, InventoryDetailSamountTradeDTO.class);
        List<InventoryDetailSamountTradeDTO> inventoryDetailSamountTradesTem = new ArrayList<>();
        // log.info("convert:{}  inventoryDetailSamountTrades:{} ", JSONObject.toJSONString(convert), JSONObject.toJSONString(returnItem.getInventoryDetailSamountTrades()));

        inventoryDetailSamountTradesTem.addAll(returnItem.getInventoryDetailSamountTrades());
        inventoryDetailSamountTradesTem.addAll(convert);
        returnItem.setInventoryDetailSamountTrades(inventoryDetailSamountTradesTem);

        // log.info("inventoryDetailSamountTrades:{} ", JSONObject.toJSONString(returnItem.getInventoryDetailSamountTrades()));
    }

    //商家退款
    private void businessRefund(ReturnOrderVO returnOrderVO, Operator operator, BigDecimal actualReturnPrice) {
        returnOrderProvider.onlineModifyPrice(ReturnOrderOnlineModifyPriceRequest.builder()
                .returnOrder(KsBeanUtil.convert(returnOrderVO, ReturnOrderDTO.class))
                .actualReturnPrice(actualReturnPrice)
                .actualReturnPoints(RETURN_POINTS)
                .operator(operator).build());
    }

    //平台端退款
    private void bossRefund(String rid, Operator operator) {
        BaseResponse<Object> res = returnOrderProvider.refundOnlineByTid(ReturnOrderOnlineRefundByTidRequest.builder().returnOrderCode(rid)
                .operator(operator).wmsStats(true).build());
        Object data = res.getContext();
        if (Objects.isNull(data)) {
            //无返回信息，追踪退单退款状态
            ReturnFlowState state = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext().getReturnFlowState();
            if (state.equals(ReturnFlowState.REFUND_FAILED)) {
                log.info("====================== boss端退款失败 ===================================");
            }
        }
    }

    //释放囤货数量
    private void freePileGoodsNum(String rid) {
        returnOrderProvider.freePileGoodsNum(CanReturnItemNumByIdRequest.builder().rid(rid).build());
    }

}
