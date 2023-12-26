package com.wanmi.sbc.returnorder.returnorder.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.enums.node.OrderProcessType;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByCustomerIdsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailListByCustomerIdsResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailBaseVO;
import com.wanmi.sbc.returnorder.api.constant.RefundReasonConstants;
import com.wanmi.sbc.returnorder.api.request.distribution.ReturnOrderSendMQRequest;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.bean.enums.ReturnType;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderAddressVO;
import com.wanmi.sbc.returnorder.groupon.service.GrouponOrderService;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.refund.model.root.RefundOrder;
import com.wanmi.sbc.returnorder.refund.service.RefundOrderService;
import com.wanmi.sbc.returnorder.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPoints;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.returnorder.returnorder.mq.ReturnOrderProducerService;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Company;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 拼团订单-自动退款业务处理
 *
 * @author: Geek Wang
 * @createDate: 2019/5/21 18:17
 * @version: 1.0
 */
@Slf4j
@Service
public class GrouponReturnOrderService {

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private GrouponOrderService grouponOrderService;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;



    @Autowired
    private ReturnOrderProducerService returnOrderProducerService;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;



    /**
     * 用户支付回调，主动发起自动退款
     * 根据订单信息处理自动退款业务
     *
     * @param trade
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void handleGrouponOrderRefund(Trade trade) {
        log.info("===========订单信息：{}，单个订单自动退款开始========", trade);
        handleGrouponOrderRefund(Collections.singletonList(trade), Boolean.FALSE);
        log.info("===========订单信息：{}，单个订单自动退款结束========", trade);
    }

    /**
     * 机器人触发自动退款
     * 根据团编号处理自动退款业务
     *
     * @param grouponNo
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void handleGrouponOrderRefund(String grouponNo) {
        log.info("=========团编号：{}，批量自动退款开始=======", grouponNo);
        List<Trade> tradeList = grouponOrderService.getTradeByGrouponNo(grouponNo);
        log.info("===========订单集合详细信息：{}，批量自动退款========", tradeList);
        if (CollectionUtils.isNotEmpty(tradeList)) {
            handleGrouponOrderRefund(tradeList, Boolean.TRUE);
        }
        log.info("=========团编号：{}，批量自动退款结束=======", grouponNo);
    }

    /**
     * 根据订单集合处理自动退款业务
     *
     * @param tradeList
     */
    public void handleGrouponOrderRefund(List<Trade> tradeList, Boolean isAuto) {
        Operator operator =
                Operator.builder().adminId("1").name("system").account("system").ip("127.0.0.1").platform(Platform
                        .PLATFORM).build();
        //1、生成退单数据
        List<ReturnOrder> returnOrders = getReturnOrderByTradeList(tradeList, operator, isAuto);
        log.info("===========生成退单数据,详细信息：{}========", returnOrders);
        List<ReturnOrder> returnOrderList = new ArrayList<>(returnOrders.size());
        returnOrders.forEach(returnOrder -> returnOrderList.add(returnOrderService.addReturnOrder(returnOrder)));

        //2、根据退单数据，获取出会员详情基础信息集合
        List<CustomerDetailBaseVO> customerDetailBaseVOList = listCustomerDetailBaseByCustomerIds(returnOrderList);
        log.info("===========会员详情基础信息集合,详细信息：{}========", customerDetailBaseVOList);

        //3、生成退款单数据集合
        List<RefundOrder> refundOrderList =
                getRefundOrderByReturnOrderListAndCustomerDetailBaseVOList(returnOrderList, customerDetailBaseVOList);

        refundOrderList = refundOrderService.batchAdd(refundOrderList);
        log.info("===========生成退款单数据集合,详细信息：{}========", refundOrderList);

        //4、自动退款
		refundOrderService.autoRefund(tradeList, returnOrderList, refundOrderList, operator);

        //5、处理其他无关业务
        handleOther(tradeList, returnOrderList);
    }

    /**
     * 根据订单数据生成对应退单数据
     *
     * @param tradeList
     * @return
     */
    protected List<ReturnOrder> getReturnOrderByTradeList(List<Trade> tradeList, Operator operator, Boolean isAuto) {

        List<ReturnOrder> returnOrderList = new ArrayList<>(tradeList.size());

        Map<Long,StoreInfoResponse> longStoreInfoResponseMap = new HashMap<>(tradeList.size());
        for (Trade trade : tradeList) {
            ReturnOrder returnOrder = new ReturnOrder();
            String rid = generatorService.generate("R");
            //退单号
            returnOrder.setId(rid);
            //订单编号
            returnOrder.setTid(trade.getId());
            //购买人信息
            returnOrder.setBuyer(trade.getBuyer());
            StoreInfoResponse storeInfoResponse = longStoreInfoResponseMap.get(trade.getSupplier().getStoreId());
            if(storeInfoResponse==null){
                storeInfoResponse = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(trade.getSupplier().getStoreId()).build()).getContext();
                longStoreInfoResponseMap.put(trade.getSupplier().getStoreId(),storeInfoResponse);
            }
            //商家信息
            returnOrder.setCompany(Company.builder().companyInfoId(trade.getSupplier().getSupplierId())
                    .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                    .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName())
                    .companyType(storeInfoResponse.getCompanyType())
                    .build());
            //描述信息
            returnOrder.setDescription(isAuto ? RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND :
                    RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND_USER);
            //退货商品
            returnOrder.setReturnItems(trade.getTradeItems().stream().map(item -> ReturnItem.builder()
                    .num(BigDecimal.valueOf(item.getNum()))
                    .skuId(item.getSkuId())
                    .skuNo(item.getSkuNo())
                    .pic(item.getPic())
                    .skuName(item.getSkuName())
                    .unit(item.getUnit())
                    .erpSkuNo(item.getErpSkuNo())
                    .price(item.getPrice())
                    .splitPrice(item.getSplitPrice())
                    .specDetails(item.getSpecDetails())
                    .build()).collect(Collectors.toList()));
            TradePrice tradePrice = trade.getTradePrice();
            ReturnPrice returnPrice = new ReturnPrice();

            returnPrice.setApplyPrice(tradePrice.getTotalPrice());
            //实退金额
            returnPrice.setActualReturnPrice(returnPrice.getApplyPrice());
            //商品总金额
            returnPrice.setTotalPrice(tradePrice.getTotalPrice());
            //申请金额可用
            returnPrice.setApplyStatus(Boolean.TRUE);
            //退货总金额
            returnOrder.setReturnPrice(returnPrice);
            //收货人信息
            returnOrder.setConsignee(trade.getConsignee());
            //退货单状态
            returnOrder.setReturnFlowState(ReturnFlowState.AUDIT);
            //仓库信息
            returnOrder.setWareId(trade.getWareId());
            //设置退货收货地址
            setReturnOrderAddress(returnOrder, trade);

            //记录日志
            returnOrder.appendReturnEventLog(
                    new ReturnEventLog(operator, "拼团订单自动退单", "拼团订单自动退单", "拼团订单自动退款", LocalDateTime.now())
            );

            // 支付方式
            returnOrder.setPayType(PayType.valueOf(trade.getPayInfo().getPayTypeName()));
            //退单类型
            returnOrder.setReturnType(ReturnType.REFUND);
            //退单来源
            returnOrder.setPlatform(operator.getPlatform());
            //退积分信息
            returnOrder.setReturnPoints(ReturnPoints.builder()
                    .applyPoints(Objects.isNull(tradePrice.getPoints()) ? NumberUtils.LONG_ZERO :
                            tradePrice.getPoints())
                    .actualPoints(Objects.isNull(tradePrice.getPoints()) ? NumberUtils.LONG_ZERO :
                            tradePrice.getPoints()).build());
            //创建时间
            returnOrder.setCreateTime(LocalDateTime.now());

            returnOrderList.add(returnOrder);
        }
        return returnOrderList;
    }

    private void setReturnOrderAddress(ReturnOrder returnOrder, Trade trade) {
        CompanyMallReturnGoodsAddressVO returnGoodsAddressVO = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(trade.getSupplier().getStoreId()).build()).getContext().getReturnGoodsAddress();
        if(!Objects.isNull(returnGoodsAddressVO)) {
            ReturnOrderAddressVO returnOrderAddressVO = KsBeanUtil.copyPropertiesThird(returnGoodsAddressVO,ReturnOrderAddressVO.class);
            returnOrder.setReturnOrderAddressVO(returnOrderAddressVO);
        }
    }

    /**
     * 订单日志、发送MQ消息
     *
     * @param tradeList
     * @param returnOrderList
     */
    protected void handleOther(List<Trade> tradeList, List<ReturnOrder> returnOrderList) {
        Map<String, String> map = returnOrderList.stream().collect(Collectors.toMap(ReturnOrder::getTid,
                ReturnOrder::getId));
        for (Trade trade : tradeList) {
            ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                    .addFlag(Boolean.TRUE)
                    .customerId(trade.getBuyer().getId())
                    .orderId(trade.getId())
                    .returnId(map.get(trade.getId()))
                    .build();
            returnOrderProducerService.returnOrderFlow(sendMQRequest);

            Map<String, Object> routeParam = new HashMap<>();
            routeParam.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
            routeParam.put("node",OrderProcessType.JOIN_GROUP_FAIL.toValue());
            routeParam.put("id", trade.getTradeGroupon().getGrouponNo());

            MessageMQRequest messageMQRequest = new MessageMQRequest();
            messageMQRequest.setNodeCode(OrderProcessType.JOIN_GROUP_FAIL.getType());
            messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
            messageMQRequest.setParams(Lists.newArrayList(trade.getTradeItems().get(0).getSkuName()));
            messageMQRequest.setRouteParam(routeParam);
            messageMQRequest.setCustomerId(trade.getBuyer().getId());
            messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
            messageMQRequest.setMobile(trade.getBuyer().getAccount());
            orderProducerService.sendMessage(messageMQRequest);
        }

    }

    /**
     * 根据退单数据、会员详情信息集合，生成对应的退款单数据集合
     *
     * @param returnOrderList
     * @param customerDetailBaseVOList
     * @return
     */
    protected List<RefundOrder> getRefundOrderByReturnOrderListAndCustomerDetailBaseVOList(List<ReturnOrder> returnOrderList, List<CustomerDetailBaseVO> customerDetailBaseVOList) {
        List<RefundOrder> refundOrderList = new ArrayList<>(returnOrderList.size());
        Map<String, String> map =
                customerDetailBaseVOList.stream().collect(Collectors.toMap(CustomerDetailBaseVO::getCustomerId,
                        CustomerDetailBaseVO::getCustomerDetailId));
        for (ReturnOrder returnOrder : returnOrderList) {
            RefundOrder refundOrder = new RefundOrder();
            refundOrder.setReturnOrderCode(returnOrder.getId());
            refundOrder.setCustomerDetailId(map.get(returnOrder.getBuyer().getId()));
            refundOrder.setCreateTime(LocalDateTime.now());
            refundOrder.setRefundCode(generatorService.generateRid());
            refundOrder.setRefundStatus(RefundStatus.TODO);
            refundOrder.setReturnPrice(returnOrder.getReturnPrice().getApplyPrice());
            refundOrder.setReturnPoints(Objects.nonNull(returnOrder.getReturnPoints()) ?
                    returnOrder.getReturnPoints().getApplyPoints() : null);
            refundOrder.setDelFlag(DeleteFlag.NO);
            refundOrder.setPayType(returnOrder.getPayType());
            refundOrder.setSupplierId(returnOrder.getCompany().getCompanyInfoId());
            refundOrderList.add(refundOrder);
        }
        return refundOrderList;
    }

    /**
     * 根据会员ID集合查询会员详情基础信息集合
     *
     * @param returnOrderList
     * @return
     */
    protected List<CustomerDetailBaseVO> listCustomerDetailBaseByCustomerIds(List<ReturnOrder> returnOrderList) {
        List<String> customerIds =
                returnOrderList.stream().map(returnOrder -> returnOrder.getBuyer().getId()).collect(Collectors.toList());
        BaseResponse<CustomerDetailListByCustomerIdsResponse> baseResponse =
                customerDetailQueryProvider.listCustomerDetailBaseByCustomerIds(new CustomerDetailListByCustomerIdsRequest(customerIds));
        return baseResponse.getContext().getList();
    }
}
