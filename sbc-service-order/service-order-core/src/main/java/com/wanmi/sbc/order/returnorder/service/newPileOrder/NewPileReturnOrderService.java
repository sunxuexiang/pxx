package com.wanmi.sbc.order.returnorder.service.newPileOrder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.api.provider.offline.OfflineQueryProvider;
import com.wanmi.sbc.account.api.request.offline.OfflineAccountGetByIdRequest;
import com.wanmi.sbc.account.api.response.offline.OfflineAccountGetByIdResponse;
import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.enums.node.ReturnOrderProcessType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountOptionalRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountOptionalResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.distribution.ReturnOrderSendMQRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.RefreshReturnedOrderRequest;
import com.wanmi.sbc.order.api.response.inventorydetailsamount.InventoryDetailSamountResponse;
import com.wanmi.sbc.order.ares.service.OrderAresService;
import com.wanmi.sbc.order.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.order.bean.vo.RefundOrderResponse;
import com.wanmi.sbc.order.bean.vo.TradeDistributeItemVO;
import com.wanmi.sbc.order.common.OperationLogMq;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.constant.OrderErrorCode;
import com.wanmi.sbc.order.customer.service.CustomerCommonService;
import com.wanmi.sbc.order.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.order.historytownshiporder.model.root.HistoryTownShipOrder;
import com.wanmi.sbc.order.historytownshiporder.repository.HistoryTownShipOrderRepository;
import com.wanmi.sbc.order.historytownshiporder.service.HistoryTownShipOrderService;
import com.wanmi.sbc.order.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.order.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.order.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
import com.wanmi.sbc.order.mq.OrderProducerService;
import com.wanmi.sbc.order.payorder.model.root.PayOrder;
import com.wanmi.sbc.order.payorder.response.PayOrderResponse;
import com.wanmi.sbc.order.payorder.service.PayOrderService;
import com.wanmi.sbc.order.pilepurchase.PilePurchase;
import com.wanmi.sbc.order.pilepurchase.PilePurchaseRepository;
import com.wanmi.sbc.order.refund.model.root.RefundBill;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.refund.repository.RefundOrderRepository;
import com.wanmi.sbc.order.refund.service.RefundBillService;
import com.wanmi.sbc.order.refund.service.RefundFactory;
import com.wanmi.sbc.order.refund.service.RefundOrderService;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.order.returnorder.model.entity.AuditOrderEntry;
import com.wanmi.sbc.order.returnorder.model.entity.KingdeeAuditOrder;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.model.root.TradePushKingdeeReturnGoods;
import com.wanmi.sbc.order.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPoints;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.returnorder.mq.NewPileReturnOrderProducerService;
import com.wanmi.sbc.order.returnorder.newpilefsm.NewPileReturnFSMService;
import com.wanmi.sbc.order.returnorder.newpilefsm.event.NewPileReturnEvent;
import com.wanmi.sbc.order.returnorder.newpilefsm.params.NewPileReturnStateRequest;
import com.wanmi.sbc.order.returnorder.repository.NewPileReturnOrderRepository;
import com.wanmi.sbc.order.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.order.returnorder.repository.TradePushKingdeeReturnGoodsRepository;
import com.wanmi.sbc.order.returnorder.request.NewPileReturnQueryRequest;
import com.wanmi.sbc.order.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.order.returnorder.service.PushKingdeeService;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.PileStockRecord;
import com.wanmi.sbc.order.trade.model.entity.PileStockRecordAttachment;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.entity.value.KingDeeResult;
import com.wanmi.sbc.order.trade.model.newPileTrade.GoodsPickStock;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.model.root.TradeCachePushKingdeeOrder;
import com.wanmi.sbc.order.trade.repository.PileStockRecordAttachmentRepostory;
import com.wanmi.sbc.order.trade.repository.PileStockRecordRepository;
import com.wanmi.sbc.order.trade.repository.TradeCachePushKingdeeOrderRepository;
import com.wanmi.sbc.order.trade.repository.newPileTrade.GoodsPickStockRepository;
import com.wanmi.sbc.order.trade.request.TradeQueryRequest;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.order.trade.service.VerifyService;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.order.util.KingdeeLoginUtils;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSOrderProvider;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackDetailsRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSOrderCancelRequest;
import com.wanmi.sbc.wms.api.response.wms.ResponseWMSReturnResponse;
import com.wanmi.sbc.wms.bean.vo.ERPWMSConstants;
import com.wanmi.sbc.wms.bean.vo.WmsErpIdConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class NewPileReturnOrderService {

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private NewPileReturnOrderRepository newPileReturnOrderRepository;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private PileStockRecordAttachmentRepostory pileStockRecordAttachmentRepostory;

    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    /**
     * 注入退单状态变更生产者service
     */
    @Autowired
    private NewPileReturnOrderProducerService newPileReturnOrderProducerService;

    @Autowired
    private TradePushKingdeeReturnGoodsRepository tradePushKingdeeReturnGoodsRepository;

    @Value("${wms.api.flag}")
    private Boolean wmsPushFlag;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private TradeCachePushKingdeeOrderRepository tradeCachePushKingdeeOrderRepository;

    @Autowired
    private HistoryTownShipOrderService historyTownShipOrderService;
    @Autowired
    private HistoryTownShipOrderRepository historyTownShipOrderRepository;

    @Autowired
    private NewPileReturnFSMService newPileReturnFSMService;

    @Autowired
    private OrderAresService orderAresService;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private RefundBillService refundBillService;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private RefundOrderRepository refundOrderRepository;

    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;

    @Autowired
    private InventoryDetailSamountService inventoryDetailSamountService;

    @Autowired
    private OrderCommonService orderCommonService;

    @Autowired
    RedissonClient redissonClient;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    /**
     * 退货
     */
    @Value("${kingdee.Return.url}")
    private String returnUrl;

    /**
     * 退款
     */
    @Value("${kingdee.Refurn.url}")
    private String refurnUrl;

    @Value("${kingdee.orderInvalid.url}")
    private String orderInvalidUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.wechat}")
    private String kingdeeWechat;

    @Value("${kingdee.alipay}")
    private String kingdeeAlipay;

    /**
     * 是否开启新金蝶
     */
    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private OfflineQueryProvider offlineQueryProvider;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private GoodsPickStockRepository goodsPickStockRepository;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private RequestWMSOrderProvider requestWMSOrderProvider;

    @Autowired
    private NewPileReturnOrderQuery newPileReturnOrderQuery;

    @Autowired
    PushKingdeeService pushKingdeeService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    /**
     * 新囤货退单创建
     *
     * @param returnOrder
     * @param operator
     */
    @Transactional
    public String createNewPileReturnOrder(NewPileReturnOrder returnOrder, Operator operator) {
        log.info("NewPileReturnOrderService.create req tid:{}", returnOrder.getTid());

        log.info("NewPileReturnOrderService.create req1 "+ JSONObject.toJSONString(returnOrder.getReturnItems()));
        log.info("NewPileReturnOrderService.create req2 "+ JSONObject.toJSONString(returnOrder.getReturnPrice()));
        if (returnOrder.getDescription().length() > 100) {
            throw new SbcRuntimeException("K-000009");
        }
        //校验该订单关联的退款单状态
        List<NewPileReturnOrder> returnOrders = newPileReturnOrderRepository.findByTid(returnOrder.getTid());
        //该订单正在售后处理中
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<NewPileReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                            .REFUND).filter(item -> !(item.getReturnFlowState() == NewPileReturnFlowState.VOID))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        //获取该囤货单是否有未完成的提货单
        TradeQueryRequest request = TradeQueryRequest.builder()
                .notFlowStates(Arrays.asList(FlowState.VOID,FlowState.COMPLETED))
                .newPileTradeNo(returnOrder.getTid())
                .build();
        List<Trade> tradeList = tradeService.getTradeList(request.getWhereCriteria());
        if (CollectionUtils.isNotEmpty(tradeList)) {
            List<String> collect = tradeList.stream().map(Trade::getId).collect(Collectors.toList());
            throw new SbcRuntimeException("K-999999","关联提货单号为："+JSONObject.toJSONString(collect));
        }

        //查询该订单所有囤货退单
        List<NewPileReturnOrder> returnOrderList = newPileReturnOrderRepository.findByTidAndApplyChannl(returnOrder.getTid(), DefaultFlag.NO);
        if (CollectionUtils.isNotEmpty(returnOrderList)) {
            throw new SbcRuntimeException("K-050120", "该囤货单已进行过囤货退单");
        }

        //查询订单信息
        NewPileTrade trade = queryCanReturnItemNumByTidNewPile(returnOrder.getTid());
        //退款金额不可大于可退金额 ? TODO
        if (operator.getPlatform() == Platform.BOSS && PayType.fromValue(Integer.parseInt(trade.getPayInfo().getPayTypeId())) == PayType.ONLINE) {
            throw new SbcRuntimeException("K-050126");
        }

        returnOrder.setBuyer(trade.getBuyer());
        returnOrder.setWareId(trade.getWareId());
        returnOrder.setWareName(trade.getWareName());
        returnOrder.setConsignee(trade.getConsignee());
        returnOrder.setCreateTime(LocalDateTime.now());
        returnOrder.setPayType(PayType.valueOf(trade.getPayInfo().getPayTypeName()));
        returnOrder.setPlatform(operator.getPlatform());

        String rid = generatorService.generate("RP");
        returnOrder.setId(rid);
        returnOrder.setReturnFlowState(NewPileReturnFlowState.AUDIT);
        returnOrder.setApplyChannl(DefaultFlag.NO);
        returnOrder.setReturnType(ReturnType.REFUND);

        //查询该笔囤货单未提的分摊数据
        List<InventoryDetailSamountVO> detailSamountVOList = inventoryDetailSamountProvider.getNoTiinventoryDetailSamount
                (InventoryDetailSamountRequest.builder()
                        .goodsInfoIds(trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList()))
                        .oid(trade.getId()).build()).getContext().getInventoryDetailSamountVOS();

        //填充退单价格
        fillReturnPrice(returnOrder, operator, detailSamountVOList);

        //填充退单交易条目
        fillReturnItems(returnOrder, trade, detailSamountVOList);

        //记录日志
        returnOrder.appendReturnEventLog(
                new ReturnEventLog(operator, "创建退单", "创建退单", "", LocalDateTime.now())
        );

        //线上订单,扭转状态到已完成; 线下订单需要客服,财务两步退款,保持待提货状态
        if (!PayType.OFFLINE.name().equals(trade.getPayInfo().getPayTypeName())) {
            newPileTradeService.returnOrder(returnOrder.getTid(), operator);
        }

        //仅退款直接跳过审核生成退款单
        refundOrderService.generateRefundOrderByEnityNewPile(returnOrder);

        //保存退单
        this.addReturnOrder(returnOrder);

        log.info("更新囤货单可提数量-----------》"+returnOrder.getTid());
        goodsPickStockRepository.updateByNewPileTradeNo(returnOrder.getTid());

        //更新分摊金额退单信息：未提的都更新，退单号为囤货退单号，退款类型为 囤货退款
        // （已提未完成：不可进行囤货退款，已提已完成：走提货退款）
        inventoryDetailSamountService.updateNewPileReturnAmount(returnOrder);

        this.operationLogMq.convertAndSend(operator, "创建囤货退单", "创建囤货退单");
        //售后单提交成功发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_COMMIT_SUCCESS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount()
        );
        return rid;
    }

    private ReturnPrice fillReturnPrice(NewPileReturnOrder returnOrder, Operator operator, List<InventoryDetailSamountVO> detailSamountVOList) {
        //退单总金额
        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        //是否boss创建并且申请特价状态
        boolean isSpecial = returnPrice.getApplyStatus() && operator.getPlatform() == Platform.BOSS;
        log.info("NewPileReturnOrderService.create req3 "+ isSpecial);
//        //计算可退金额
        BigDecimal totalPrice;
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal balancePrice = BigDecimal.ZERO;
        if (isSpecial) {
            totalPrice = returnPrice.getApplyPrice();
        } else {
            //金额汇总
            Map<Integer, BigDecimal> amountByMoneyTypeMap = detailSamountVOList.stream().collect(
                    Collectors.groupingBy(InventoryDetailSamountVO::getMoneyType,
                            Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
            if(Objects.nonNull(amountByMoneyTypeMap.get(0))){
                balancePrice = amountByMoneyTypeMap.get(0);
            }
            if(Objects.nonNull(amountByMoneyTypeMap.get(1))){
                price = amountByMoneyTypeMap.get(1);
            }

            totalPrice = detailSamountVOList.stream().collect(
                    Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add));
        }

        log.info("ReturnOrderService.create tid:{} price:{}", returnOrder.getTid(), totalPrice);

        returnPrice.setTotalPrice(totalPrice);
        returnPrice.setApplyPrice(totalPrice);
        //未改价前，应退与实退相等
        //1. 应退现金 和 实退现金
        returnPrice.setShouldReturnCash(price);
        returnPrice.setActualReturnCash(price);
        //2. 应退鲸贴 和 实退鲸贴
        returnPrice.setBalanceReturnPrice(balancePrice);
        returnPrice.setActualBalanceReturnPrice(balancePrice);
        return returnPrice;
    }

    private void addBalancePrice(NewPileReturnOrder returnOrder, NewPileTrade trade) {
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        //设置鲸贴信息
        for (ReturnItem returnItem : returnItems) {
            List<TradeItem> tradeTradeItemCollect = trade.getTradeItems().stream().filter(tradeItem -> StringUtils.equals(tradeItem.getSkuId(), returnItem.getSkuId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tradeTradeItemCollect)) {
                continue;
            }
            List<TradeItem.WalletSettlement> tradeItemWalletSettlementCollect = tradeTradeItemCollect.stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tradeItemWalletSettlementCollect)) {
                continue;
            }
            returnItem.setBalancePrice(tradeItemWalletSettlementCollect.stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        returnOrder.getReturnPrice().setBalanceReturnPrice(returnItems.stream().filter(i -> Objects.nonNull(i.getBalancePrice())).map(ReturnItem::getBalancePrice).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private void addBalancePrice(ReturnOrder returnOrder, Trade trade) {
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        if (trade.getTradePrice().getBalancePrice() == null || trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        //设置鲸贴信息
        for (ReturnItem returnItem : returnItems) {
            List<TradeItem> tradeTradeItemCollect = trade.getTradeItems().stream().filter(tradeItem -> StringUtils.equals(tradeItem.getSkuId(), returnItem.getSkuId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tradeTradeItemCollect)) {
                continue;
            }
            List<TradeItem.WalletSettlement> tradeItemWalletSettlementCollect = tradeTradeItemCollect.stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tradeItemWalletSettlementCollect)) {
                continue;
            }
            TradeItem tradeItem = tradeTradeItemCollect.stream().findFirst().orElse(null);
            TradeItem.WalletSettlement walletSettlement = tradeItemWalletSettlementCollect.stream().findFirst().orElse(null);

            BigDecimal divide = returnItem.getNum().divide(new BigDecimal(tradeItem.getNum()), 10, BigDecimal.ROUND_DOWN);

            returnItem.setBalancePrice(walletSettlement.getReduceWalletPrice() == null ? BigDecimal.ZERO : walletSettlement.getReduceWalletPrice().multiply(divide).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        returnOrder.getReturnPrice().setBalanceReturnPrice(returnItems.stream().filter(i -> Objects.nonNull(i.getBalancePrice())).map(ReturnItem::getBalancePrice).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * 新提货退单创建
     *
     * @param returnOrder
     * @param operator
     */
//    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public String createNewPickReturnOrder(ReturnOrder returnOrder, Operator operator, Integer forceRefund){
        log.info("createNewPickReturnOrder------------------->>退款金额：{},提货单号：{}",
                JSONObject.toJSONString(returnOrder.getReturnPrice()), returnOrder.getTid());

        //申请分布式锁，以免重复处理 TODO: DEFINE CONST IN COMMON JAR
        //查询订单信息
        Trade trade = tradeService.detail(returnOrder.getTid());

        RLock rLock = redissonClient.getFairLock("refund_new_pile_pick_return_order_" + trade.getBuyer().getId());
        rLock.lock();
        try {
            checkReturnOrder(returnOrder);

            //获取金额分布,过滤出未被其他退单占用的金额
            List<InventoryDetailSamountVO> unassignedAmountList = getUnassignedAmountListByTakeId(returnOrder.getTid());

            //wms缺多少？ -> 分配退货数量： 从 returnItems 里面取
            //分配分摊金额
            List<InventoryDetailSamountVO> assignedAmountList = assignReturnAmount(returnOrder, unassignedAmountList, null);
            log.info("囤货提货退款金额 assignedAmountList：{}", JSONObject.toJSONString(assignedAmountList));


            //是否仅退款
            boolean isRefund = trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || trade
                    .getTradeState().getDeliverStatus()
                    == DeliverStatus.VOID;
            if (checkIsWmsReturn(returnOrder)) {
                isRefund = true;
            }

            //已发货，设置为退货，商家端可进行驳回
            returnOrder.setReturnType(isRefund ? ReturnType.REFUND : ReturnType.RETURN);

            //保存申请退单信息
            //保存囤货退款单信息
            //saveNewPileReturnOrder(trade, returnOrder, operator, isRefund, rid, returnPickGoodsMap,inventoryByTakeId);
            //返回提货申请单id
            return saveReturnOrder(trade, returnOrder, operator, isRefund, forceRefund, assignedAmountList);
        } finally {
            rLock.unlock();
        }
    }

    private boolean checkIsWmsReturn(ReturnOrder returnOrder) {
        return Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats();
    }

    private void checkReturnOrder(ReturnOrder returnOrder) {
        if (checkIsWmsReturn(returnOrder)) {
            //如果为缺货退单
            log.info("wms缺货退款。。。");
            //检查该订单是否已有缺货退款单，有，不可再进行缺货退款
            List<ReturnOrder> wmsReturnOrders = returnOrderRepository.findByTidAndWmsStats(returnOrder.getTid(), true);
            if (CollectionUtils.isNotEmpty(wmsReturnOrders)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单已有缺货退款单，不可重复缺货退款！ 退单id=" + wmsReturnOrders.get(0).getId());
            }
        }

        if (returnOrder.getDescription().length() > 100) {
            throw new SbcRuntimeException("K-000009");
        }
        //提货订单应退金额 前端计算出来的，如果为提货订单且为代客退单的时候要重新赋值 应退金额
//        BigDecimal stockPrice = returnOrder.getReturnPrice().getTotalPrice().add(returnOrder.getReturnPrice().getBalanceReturnPrice());

        //查询该订单所有退单
        List<ReturnOrder> returnOrderList = returnOrderRepository.findByTid(returnOrder.getTid());
        //筛选出已完成的退单列表
        if (CollectionUtils.isNotEmpty(returnOrderList)) {
            //过滤处理中的退单
            List<ReturnOrder> returnOrders = returnOrderList.stream().filter(item -> item.getReturnFlowState() !=
                            ReturnFlowState.COMPLETED
                            && item.getReturnFlowState() != ReturnFlowState.VOID
                            && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                            && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                            && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(returnOrders)) {
                throw new SbcRuntimeException("K-050120");
            }
        }
    }

    public List<InventoryDetailSamountVO> getUnassignedAmountListByTakeId(String takeId) {
        return inventoryDetailSamountProvider
                .getInventoryByTakeId(InventoryDetailSamountRequest.builder().takeId(takeId).build())
                .getContext()
                .getInventoryDetailSamountVOS()
                .stream().filter(item -> StringUtils.isBlank(item.getReturnId()))
                .collect(Collectors.toList());
    }

    private void fillReturnPrice(List<InventoryDetailSamountVO> assignedAmountList, ReturnOrder returnOrder, Trade trade) {
        BigDecimal sumPayPrice = BigDecimal.ZERO;
        BigDecimal sumBalancePrice = BigDecimal.ZERO;
        BigDecimal deliveryPrice = BigDecimal.ZERO;

        if (CollectionUtils.isNotEmpty(assignedAmountList)) {
            Map<Integer, BigDecimal> amountByMoneyTypeMap = assignedAmountList.stream()
                    .collect(Collectors.groupingBy(InventoryDetailSamountVO::getMoneyType,
                            Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
            if (Objects.nonNull(amountByMoneyTypeMap.get(0))) {
                sumBalancePrice = amountByMoneyTypeMap.get(0);
            }
            if (Objects.nonNull(amountByMoneyTypeMap.get(1))) {
                sumPayPrice = amountByMoneyTypeMap.get(1);
            }
        }

        if (DeliverStatus.NOT_YET_SHIPPED.equals(trade.getTradeState().getDeliverStatus())
                && returnNumEqPickNum(returnOrder, trade)) {
            deliveryPrice = trade.getTradePrice().getDeliveryPrice();
        }

        // 应退金额，如果对退单做了改价，使用applyPrice，否则，使用总额totalPrice
        BigDecimal totalPrice = sumPayPrice.add(sumBalancePrice).add(deliveryPrice);

        returnOrder.getReturnPrice().setTotalPrice(totalPrice);
        returnOrder.getReturnPrice().setApplyPrice(totalPrice);
        returnOrder.getReturnPrice().setActualReturnCash(sumPayPrice);
        returnOrder.getReturnPrice().setActualBalanceReturnPrice(sumBalancePrice);
        returnOrder.getReturnPrice().setShouldReturnCash(sumPayPrice);
        returnOrder.getReturnPrice().setBalanceReturnPrice(sumBalancePrice);
        returnOrder.getReturnPrice().setDeliveryPrice(deliveryPrice);
    }

    private boolean returnNumEqPickNum(ReturnOrder returnOrder, Trade trade) {
        BigDecimal returnNum = returnOrder.getReturnItems().stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        Long pickNum = trade.getTradeItems().stream().flatMap(tradeItem -> tradeItem.getPickGoodsList().stream())
                .map(PickGoodsDTO::getNum)
                .reduce(0L, Long::sum);
        return returnNum.longValue() == pickNum;
    }

    public Map<String, List<InventoryDetailSamountTrade>> mergeAmountList(List<InventoryDetailSamountVO> unassignedAmountList) {
        Map<String, List<InventoryDetailSamountVO>> amountListBySkuAndMoneyTypeMap = getAmountListBySkuAndMoneyTypeMap(unassignedAmountList,null);

        List<String> skuList = unassignedAmountList.stream().map(InventoryDetailSamountVO::getGoodsInfoId).distinct().collect(Collectors.toList());
        Map<String, List<InventoryDetailSamountTrade>> amountListBySkuIdMap = new HashMap<>();
        skuList.forEach(skuId -> {
            List<InventoryDetailSamountVO> amountList0 = amountListBySkuAndMoneyTypeMap.get(getGroupKey(skuId, 0));
            List<InventoryDetailSamountVO> amountList1 = amountListBySkuAndMoneyTypeMap.get(getGroupKey(skuId, 1));
            List<InventoryDetailSamountTrade> list = IntStream.range(0, amountList0.size())
                    .mapToObj(i -> {
                                InventoryDetailSamountTrade vo = new InventoryDetailSamountTrade();
                                BeanUtils.copyProperties(amountList0.get(i), vo);
                                vo.setAmortizedExpenses(amountList0.get(i).getAmortizedExpenses()
                                        .add(amountList1.get(i).getAmortizedExpenses()));
                                vo.setMoneyType(1);
                                return vo;
                            }
                    ).collect(Collectors.toList());
            amountListBySkuIdMap.put(skuId, list);
        });

        return amountListBySkuIdMap;
    }


    private List<InventoryDetailSamountVO> assignReturnAmount(ReturnOrder returnOrder,
                                                              List<InventoryDetailSamountVO> unassignedAmountList,
                                                              Comparator<? super InventoryDetailSamountVO> comparator) {

        log.info("assignReturnAmount-----------888888888888--------->" + JSONObject.toJSONString(unassignedAmountList));
        Map<String, List<InventoryDetailSamountVO>> amountListBySkuAndMoneyTypeMap = getAmountListBySkuAndMoneyTypeMap(unassignedAmountList, comparator);

        List<InventoryDetailSamountVO> assignedAmountVOList = new ArrayList<>();
        List<Integer> moneyTypes = Arrays.asList(0,1);
        returnOrder.getReturnItems().forEach(returnItem -> {
            //哪一个商品，退了多少
            String skuId = returnItem.getSkuId();
            BigDecimal returnNum = returnItem.getNum();
            moneyTypes.forEach(moneyType -> {
                String groupKey = getGroupKey(skuId, moneyType);
                List<InventoryDetailSamountVO> amountList = amountListBySkuAndMoneyTypeMap.get(groupKey);
                if (Objects.isNull(amountList) || returnNum.intValue() > amountList.size()) {
                    log.warn("提货退款,申请数量超限！skuId={},returnNum={},tradeId={}，可退数量={}", skuId, returnNum, returnOrder.getTid(), Objects.isNull(amountList) ? "null" : amountList.size());
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "申请数量超限！");
                }
                assignedAmountVOList.addAll(amountList.subList(0, returnNum.intValue()));
            });
        });

        log.info("assignReturnAmount-------------------->" + JSONObject.toJSONString(assignedAmountVOList));
        return assignedAmountVOList;
    }

    /**
     * sku_支付方式：金额列表
     * @param unassignedAmountList
     * @return
     */
    private Map<String, List<InventoryDetailSamountVO>> getAmountListBySkuAndMoneyTypeMap(List<InventoryDetailSamountVO> unassignedAmountList,
                                                                                          Comparator<? super InventoryDetailSamountVO> comparator) {
        //囤货单只用现金或者余额支付的，没有另一种支付方式的分摊记录，补充另一种分摊记录，金额为0，方便进行数量分配
        List<InventoryDetailSamountVO> addedAmountList = addAnotherMoneyTypeAmountRecord(unassignedAmountList);

        //未退部分，按囤货单时间从前到后，交易商品项金额从大到小，匹配退单数量，进行分摊表分配
        if (Objects.isNull(comparator)) {
            comparator = Comparator.comparing(InventoryDetailSamountVO::getNewPileTradeId)
                    .thenComparing(InventoryDetailSamountVO::getGoodsInfoId)
                    .thenComparing(InventoryDetailSamountVO::getAmortizedExpenses, Comparator.reverseOrder());
        }

        List<InventoryDetailSamountVO> sortedAmountList = addedAmountList.stream().sorted(comparator).collect(Collectors.toList());

        //sku_支付方式：金额列表
        Map<String, List<InventoryDetailSamountVO>> amountListBySkuAndMoneyTypeMap = sortedAmountList.stream()
                .collect(Collectors.groupingBy(item -> getGroupKey(item.getGoodsInfoId(), item.getMoneyType())));
        return amountListBySkuAndMoneyTypeMap;
    }

    private List<InventoryDetailSamountVO> addAnotherMoneyTypeAmountRecord(List<InventoryDetailSamountVO> detailSamountVOList) {
        if (CollectionUtils.isEmpty(detailSamountVOList)) {
            return detailSamountVOList;
        }

        Map<String, List<InventoryDetailSamountVO>> amountListByPileNoMap = detailSamountVOList.stream().collect(Collectors.groupingBy(InventoryDetailSamountVO::getNewPileTradeId));
        Map<Integer, Integer> moneyTypeMap = new HashMap<>();
        //只使用现金时，补充余额支付；只使用余额时，补充现金支付；
        moneyTypeMap.put(1, 0);
        moneyTypeMap.put(0, 1);

        for (Map.Entry<String, List<InventoryDetailSamountVO>> entry : amountListByPileNoMap.entrySet()) {
            String pileNo = entry.getKey();
            List<InventoryDetailSamountVO> amountList = entry.getValue();
            List<Integer> moneyTypes = amountList.stream().map(InventoryDetailSamountVO::getMoneyType).distinct().collect(Collectors.toList());
            if (moneyTypes.size() == 2) {
                continue;
            }
            //补充另一种支付方式的分摊记录，方便后续取值
            int needAddMoneyType = moneyTypeMap.get(moneyTypes.get(0));
            List<InventoryDetailSamountVO> addedAmountList = new ArrayList<>();
            for (int i = 0; i < amountList.size(); i++) {
                InventoryDetailSamountVO vo = new InventoryDetailSamountVO();
                vo.setNewPileTradeId(pileNo);
                vo.setGoodsInfoId(amountList.get(i).getGoodsInfoId());
                vo.setAmortizedExpenses(BigDecimal.ZERO);
                vo.setMoneyType(needAddMoneyType);
                addedAmountList.add(vo);
            }
            amountList.addAll(addedAmountList);
        }

        return amountListByPileNoMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String getGroupKey(String skuId, Integer moneyType) {
        return skuId + "_" + moneyType;
    }

    /* *//**
     * 保存囤货退款申请单
     *
     * @param trade
     * @param returnOrder
     * @param operator
     * @param isRefund
     * @return
     *//*
    private void saveNewPileReturnOrder(Trade trade, ReturnOrder returnOrder, Operator operator,
                                        Boolean isRefund, String prid,
                                        Map<String, List<ReturnPickGoodsDTO>> returnPickGoodsResult,
                                        List<InventoryDetailSamountVO> collectResult) {

        List<ReturnPickGoodsDTO> temp = new ArrayList<>();

        //结构转换
        for (String key : returnPickGoodsResult.keySet()) {
            temp.addAll(returnPickGoodsResult.get(key));
        }
        Map<String, List<ReturnPickGoodsDTO>> map = temp.stream().collect(Collectors.groupingBy(ReturnPickGoodsDTO::getNewPileOrderNo));

        for (String key : map.keySet()) {
            List<ReturnPickGoodsDTO> returnPickGoodsDTOList = map.get(key);
            //分组计算单个sku数量
            Map<String, Long> collectNum = returnPickGoodsDTOList.stream().collect(
                    Collectors.groupingBy(
                            ReturnPickGoodsDTO::getGoodsInfoId,
                            Collectors.summingLong(ReturnPickGoodsDTO::getNum)
                    )
            );
            //分组计算单个sku退款金额
            Map<Integer, List<InventoryDetailSamountVO>> collect = collectResult.stream().collect(
                    Collectors.groupingBy(InventoryDetailSamountVO::getMoneyType));

            //实际付款金额
            Map<String, BigDecimal> spriceMap = new HashMap<>();
            if(Objects.nonNull(collect.get(1))){
                spriceMap = collect.get(1).stream().collect(
                        Collectors.groupingBy(InventoryDetailSamountVO::getGoodsInfoId,
                                Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
            }
            Map<String, BigDecimal> balanceMap = new HashMap<>();
            //津贴金额
            if(Objects.nonNull(collect.get(0))){
                balanceMap = collect.get(0).stream().collect(
                        Collectors.groupingBy(InventoryDetailSamountVO::getGoodsInfoId,
                                Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
            }

            //计算单个囤货单退款总金额
            BigDecimal newPileTradeReturnPice = collectResult.stream()
                    .collect(Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add));

            List<ReturnItem> returnItems = new ArrayList<>();
            Map<String, BigDecimal> finalSpriceMap = spriceMap;
            Map<String, BigDecimal> finalBalanceMap = balanceMap;
            trade.getTradeItems().forEach(item -> {
                if (CollectionUtils.isNotEmpty(returnPickGoodsResult.get(item.getSkuId()))) {
                    returnItems.add(
                            ReturnItem.builder()
                                    .num(BigDecimal.valueOf(collectNum.get(item.getSkuId())))
                                    .skuId(item.getSkuId())
                                    .skuNo(item.getSkuNo())
                                    .devanningId(item.getDevanningId())
                                    .pic(item.getPic())
                                    .skuName(item.getSkuName())
                                    .unit(item.getUnit())
                                    .price(item.getPrice())
                                    .erpSkuNo(item.getErpSkuNo())
                                    .splitPrice(Objects.isNull(finalSpriceMap.get(item.getSkuId())) ? BigDecimal.ZERO : finalSpriceMap.get(item.getSkuId()))
                                    .balancePrice(Objects.isNull(finalBalanceMap.get(item.getSkuId())) ? BigDecimal.ZERO : finalBalanceMap.get(item.getSkuId()))
                                    .specDetails(item.getSpecDetails())
                                    .goodsSubtitle(item.getGoodsSubtitle())
                                    .build());
                }
            });

            //设置退货商品
            returnOrder.setReturnItems(returnItems);
            //设置囤货单退款金额
            ReturnPrice returnPrice = returnOrder.getReturnPrice();
            returnPrice.setApplyPrice(newPileTradeReturnPice);
            returnPrice.setTotalPrice(newPileTradeReturnPice);

            returnOrder.setBuyer(trade.getBuyer());
            returnOrder.setWareId(trade.getWareId());
            returnOrder.setWareName(trade.getWareName());
            returnOrder.setConsignee(trade.getConsignee());
            returnOrder.setCreateTime(LocalDateTime.now());
            returnOrder.setPayType(PayType.valueOf(trade.getPayInfo().getPayTypeName()));
            returnOrder.setPlatform(operator.getPlatform());

            String rid = generatorService.generate("RPK");
            returnOrder.setId(rid);

            //记录日志
            returnOrder.appendReturnEventLog(
                    new ReturnEventLog(operator, "创建新提货退单", "创建新提货退单", "", LocalDateTime.now())
            );

            returnOrder.setReturnFlowState(ReturnFlowState.INIT);

            ReturnPickTradeNewPileTrade returnPickTradeNewPileTrade = new ReturnPickTradeNewPileTrade();
            returnPickTradeNewPileTrade.setCreateTime(LocalDateTime.now());
            returnPickTradeNewPileTrade.setNewPileTradeNo(key);
            returnPickTradeNewPileTrade.setTradeNo(trade.getId());
            returnPickTradeNewPileTrade.setRetrunOrderNo(prid);
            returnPickTradeNewPileTrade.setNewPileReturnOrderNo(rid);
            returnPickTradeNewPileTradeRepository.save(returnPickTradeNewPileTrade);
            //刷新进批次号
            returnOrder.getReturnItems().forEach(info -> {
                Optional<TradeItem> tradeItemOptional = trade.getTradeItems().stream().filter(tradeItem -> info
                        .getSkuId().equals(tradeItem.getSkuId())).findFirst();
                if (tradeItemOptional.isPresent()) {
                    TradeItem tradeItem = tradeItemOptional.get();
                    info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                }
            });

            //仅退款直接跳过审核生成退款单
            //仅退款直接跳过审核生成退款单，wms缺货，缺货商品生成退款单，退货单状态改为已审核
            if (isRefund) {
                returnOrder.setReturnFlowState(ReturnFlowState.AUDIT);
                refundOrderService.generateRefundOrderByEnity(returnOrder, returnOrder.getBuyer().getId(), newPileTradeReturnPice,
                        returnOrder.getPayType());
            }

            //保存退单
            NewPileReturnOrder newPileReturnOrder = KsBeanUtil.convert(returnOrder, NewPileReturnOrder.class);
            newPileReturnOrder.setApplyChannl(DefaultFlag.YES);
            addReturnOrder(newPileReturnOrder);
            this.operationLogMq.convertAndSend(operator, "创建囤货(提货)退单", "创建囤货(提货)退单");


            if (operator.getPlatform() == Platform.BOSS) {
                auditNewPile(rid, operator);
            }

            // 创建退单时，发送MQ消息
            ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                    .addFlag(Boolean.TRUE)
                    .customerId(trade.getBuyer().getId())
                    .orderId(trade.getId())
                    .returnId(rid)
                    .build();
            newPileReturnOrderProducerService.returnOrderFlow(sendMQRequest);

            //售后单提交成功发送MQ消息
            List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
            String pic = returnOrder.getReturnItems().get(0).getPic();
            sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                    ReturnOrderProcessType.AFTER_SALE_ORDER_COMMIT_SUCCESS,
                    params,
                    returnOrder.getId(),
                    returnOrder.getBuyer().getId(),
                    pic,
                    returnOrder.getBuyer().getAccount()
            );
        }
    }*/

    /**
     * 保存提货退款申请单
     *
     * @param trade
     * @param returnOrder
     * @param operator
     * @param isRefund
     * @param assignedAmountList
     * @return
     */
    private String saveReturnOrder(Trade trade, ReturnOrder returnOrder, Operator operator, Boolean isRefund, Integer forceRefund, List<InventoryDetailSamountVO> assignedAmountList) {
        StopWatch stopWatch = new StopWatch("保存退单处理时间");
        stopWatch.start("设置退单信息");
        //设置退货商品
        returnOrder.setBuyer(trade.getBuyer());
        returnOrder.setWareId(trade.getWareId());
        returnOrder.setWareName(trade.getWareName());
        returnOrder.setConsignee(trade.getConsignee());
        returnOrder.setCreateTime(LocalDateTime.now());
        returnOrder.setPayType(PayType.valueOf(trade.getPayInfo().getPayTypeName()));
        returnOrder.setPlatform(operator.getPlatform());

        String rid = generatorService.generate("RPK");
        returnOrder.setId(rid);

        //填充退单日志
        returnOrder.appendReturnEventLog(
                new ReturnEventLog(operator, "创建新提货退单", "创建新提货退单", "", LocalDateTime.now())
        );

        //写入退单总金额
        fillReturnPrice(assignedAmountList, returnOrder, trade);

        //写入退货商品来源囤货单信息
        fillReturnGoodsList(assignedAmountList,returnOrder);

        //刷新进批次号
        returnOrder.getReturnItems().forEach(info -> {
            Optional<TradeItem> tradeItemOptional = trade.getTradeItems().stream().filter(tradeItem -> info
                    .getSkuId().equals(tradeItem.getSkuId())).findFirst();
            if (tradeItemOptional.isPresent()) {
                TradeItem tradeItem = tradeItemOptional.get();
                info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
            }
        });

        //写入退单状态
        returnOrder.setReturnFlowState(ReturnFlowState.INIT);
        if (isRefund) {
            //仅退款直接跳过审核生成退款单，wms缺货，缺货商品生成退款单，退货单状态改为已审核
            returnOrder.setReturnFlowState(ReturnFlowState.AUDIT);
        }
        boolean wmsRefundFlag = checkIsWmsReturn(returnOrder);
        if (wmsRefundFlag) {
            //如果为缺货退单，无需审核，设置退单状态为已完成
            returnOrder.setReturnFlowState(ReturnFlowState.COMPLETED);
        }
        stopWatch.stop();

        stopWatch.start("wms三方推送");
        //wms三方推送
        if (wmsPushFlag) {
            if (isRefund) {
                //是否在满十件记录
                TradeCachePushKingdeeOrder tradeCachePushKingdeeOrder = tradeCachePushKingdeeOrderRepository.findByReturnOrderCachePushKingdeeOrder(returnOrder.getTid());
                if (Objects.nonNull(tradeCachePushKingdeeOrder)) {
                    tradeCachePushKingdeeOrderRepository.updateCachePushKingdeeOrderStatus(LocalDateTime.now(), tradeCachePushKingdeeOrder.getPushKingdeeId());
                    //取消乡镇件订单

                    List<HistoryTownShipOrder> tsOrderBytid = historyTownShipOrderRepository.getTsOrderBytid(returnOrder.getTid());
                    if (CollectionUtils.isNotEmpty(tsOrderBytid)) {
                        //已经推送wms了需要返回库存
                        verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                        verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                    } else {
                        //没推送或者已经取消的订单
                        historyTownShipOrderService.reduceTownStock(returnOrder.getTid());
                    }
                    historyTownShipOrderService.CancelTownStock(returnOrder.getTid());
                    //修改状态
                    orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
                } else {
                    //已经推送
                    List<HistoryTownShipOrder> tsOrderBytid = historyTownShipOrderRepository.getTsOrderBytid(returnOrder.getTid());
                    if (CollectionUtils.isNotEmpty(tsOrderBytid)) {
                        //已经推送wms了需要返回库存
                        verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                        verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                    } else {
                        //没推送或者已经取消的订单
                        historyTownShipOrderService.reduceTownStock(returnOrder.getTid());
                    }
                    historyTownShipOrderService.CancelTownStock(returnOrder.getTid());
                    //推送取消订单
                    if (pushCancelOrder(returnOrder, trade.getWareHouseCode(),trade)) {
                        orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
                        returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                    } else {
                        //不为强制退款时，需要提示回滚
                        if (forceRefund == null || forceRefund != 1) {
                            throw new SbcRuntimeException(OrderErrorCode.ORDER_HAS_BEEN_PICK_IN_WMS, "申请退款失败，商品已拣货");
                        }
                    }
                }
            }
        }
        stopWatch.stop();


        stopWatch.start("生成退款单");
        //仅退款直接跳过审核生成退款单
        //仅退款直接跳过审核生成退款单，wms缺货，缺货商品生成退款单，退货单状态改为已审核
        if (isRefund) {
            refundOrderService.generateRefundOrderByEnity(
                    returnOrder,
                    returnOrder.getBuyer().getId(),
                    returnOrder.getReturnPrice().getTotalPrice(),
                    returnOrder.getPayType()
            );

            //扣除乡镇件库存
            if (trade.getVillageFlag()) {
                verifyService.subSkuVillagesStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
                verifyService.subSkuVillagesStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
            }
        }
        stopWatch.stop();

        stopWatch.start("更新分摊金额退单号");
        //占用分摊金额：记录提货退单号，退单类型为提货退， wms缺货退时，无需审核，标记为已完成退款
        int returnFlag = wmsRefundFlag ? 1 : 0;
        log.info("新提货退单处理：更新分摊金额退单号，退单={},assignedAmountList size={},已退标记={}", returnOrder.getId(), assignedAmountList.size(), returnFlag);
        inventoryDetailSamountService.updateNewPickReturnAmount(returnOrder.getId(), assignedAmountList, returnFlag);
        stopWatch.stop();


        //保存退单
        stopWatch.start("保存退单");
        log.info("保存退单，退单={}", returnOrder.getId());
        returnOrderService.addReturnOrder(returnOrder);
        this.operationLogMq.convertAndSend(operator, "创建提货退单", "创建提货退单");
        stopWatch.stop();

        stopWatch.start("退款：wms缺货退时自动退款");
        //退款：wms缺货退时自动退款
        if (wmsRefundFlag) {
            log.info("退款：wms缺货退时自动退款，退单={}", returnOrder.getId());
            RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.NEW_PICK_RETURN).refund(trade, returnOrder);
        }
        stopWatch.stop();

        stopWatch.start("售后单提交成功发送MQ消息");
        //售后单提交成功发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_COMMIT_SUCCESS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount()
        );
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return rid;
    }

    private void fillReturnGoodsList(List<InventoryDetailSamountVO> assignedAmountList, ReturnOrder returnOrder) {
        //按商品分组，找出分配的金额
        Map<String, List<InventoryDetailSamountVO>> amountListBySkuMap = assignedAmountList.stream().collect(
                Collectors.groupingBy(InventoryDetailSamountVO::getGoodsInfoId)
        );

        returnOrder.getReturnItems().forEach(returnItem -> {
            List<InventoryDetailSamountVO> amountList = amountListBySkuMap.get(returnItem.getSkuId());
            //按囤货单分组的金额
            Map<String, List<InventoryDetailSamountVO>> amountListByPileNoMap = amountList.stream().collect(
                    Collectors.groupingBy(InventoryDetailSamountVO::getNewPileTradeId)
            );

            List<PickGoodsDTO> returnGoodsList = amountListByPileNoMap.entrySet().stream().map(entry -> {
                List<InventoryDetailSamountVO> tmpAmountList = entry.getValue();
                BigDecimal splitPrice = tmpAmountList.stream()
                        .map(InventoryDetailSamountVO::getAmortizedExpenses)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal cashPrice = tmpAmountList.stream()
                        .filter(item -> item.getMoneyType() == 1)
                        .map(InventoryDetailSamountVO::getAmortizedExpenses)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal balancePrice = tmpAmountList.stream()
                        .filter(item -> item.getMoneyType() == 0)
                        .map(InventoryDetailSamountVO::getAmortizedExpenses)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                //该囤货单使用的支付方式 数量
                int moneyTypeCount = (int) tmpAmountList.stream().map(InventoryDetailSamountVO::getMoneyType)
                        .distinct().count();

                PickGoodsDTO pickGoodsDTO = new PickGoodsDTO();
                pickGoodsDTO.setNewPileOrderNo(entry.getKey());
                pickGoodsDTO.setGoodsInfoId(tmpAmountList.get(0).getGoodsInfoId());
                //同一个商品按支付方式分摊金额，计算数量时： 除 使用的支付方式数量
                pickGoodsDTO.setNum((long) (tmpAmountList.size() / moneyTypeCount));
                pickGoodsDTO.setSplitPrice(splitPrice);
                pickGoodsDTO.setReturnCashPrice(cashPrice);
                pickGoodsDTO.setReturnBalancePrice(balancePrice);
                //未改价前：实退金额 = 应退金额
                pickGoodsDTO.setActualSplitPrice(splitPrice);
                pickGoodsDTO.setActualReturnCashPrice(cashPrice);
                pickGoodsDTO.setActualReturnBalancePrice(balancePrice);
                return pickGoodsDTO;
            }).collect(Collectors.toList());

            returnItem.setReturnGoodsList(returnGoodsList);
        });
    }

    /**
     * 计算申请商品所分布的囤货单
     *
     * @param returnItems
     * @param allDetail
     * @return
     *//*
    private synchronized Map<String, List<ReturnPickGoodsDTO>> computeReturnPickGoods(List<ReturnItem> returnItems, List<NewPileTrade> allDetail) {
        //申请退款商品转为map
        Map<String, ReturnItem> tradeItemMap = returnItems.stream().collect(Collectors.toMap(ReturnItem::getSkuId, g -> g, (a, b) -> a));
        //返回结构
        Map<String, List<ReturnPickGoodsDTO>> result = new HashMap<>();
        //遍历提货单的对应囤货单
        allDetail.forEach(newPileTrade -> {
            AtomicReference<Long> temp = new AtomicReference<>(0l);
            //便利并组合申请退货商品的分布
            for (int i = 0; i < newPileTrade.getTradeItems().size(); i++) {
                //退货商品与囤货单关系
                List<ReturnPickGoodsDTO> returnPickGoodsDTOList = new ArrayList<>();

                TradeItem item = newPileTrade.getTradeItems().get(i);
                ReturnItem returnItem = tradeItemMap.get(item.getSkuId());

                if(Objects.nonNull(returnItem)){
                    if (temp.get() <= returnItem.getNum().longValue()) {

                        temp.updateAndGet(v -> v + returnItem.getNum().longValue());
                        returnPickGoodsDTOList.add(ReturnPickGoodsDTO.builder()
                                .goodsInfoId(item.getSkuId())
                                .num(item.getNum())
                                .newPileOrderNo(newPileTrade.getId())
                                .build());
                    } else {
                        Long returnNum = temp.get() - returnItem.getNum().longValue();
                        returnPickGoodsDTOList.add(ReturnPickGoodsDTO.builder()
                                .goodsInfoId(item.getSkuId())
                                .num(returnNum)
                                .newPileOrderNo(newPileTrade.getId())
                                .build());
                        result.put(returnItem.getSkuId(), returnPickGoodsDTOList);
                        break;
                    }
                }
            }
        });
        return result;
    }

*/

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param returnOrder
     */
    @MongoRollback(persistence = NewPileReturnOrder.class, operation = Operation.ADD)
    public NewPileReturnOrder addReturnOrder(NewPileReturnOrder returnOrder) {
        return newPileReturnOrderRepository.save(returnOrder);
    }

    /**
     * 分页查询退单列表
     *
     * @param request
     * @return
     */
    public Page<NewPileReturnOrder> page(NewPileReturnQueryRequest request) {
        Query query = new Query(request.build());
        long total = this.countNum(request);
        if (total < 1) {
            return new PageImpl<>(new ArrayList<>(), request.getPageRequest(), total);
        }
        request.putSort(request.getSortColumn(), request.getSortRole());
        List<NewPileReturnOrder> returnPileOrderList = mongoTemplate.find(query.with(request.getPageRequest()), NewPileReturnOrder.class);
        fillActualReturnPrice(returnPileOrderList);

        // 填充退款单状态
        if (CollectionUtils.isNotEmpty(returnPileOrderList)) {
            List<String> ridList = returnPileOrderList.stream().map(NewPileReturnOrder::getId).collect(Collectors.toList());
            RefundOrderRequest refundOrderRequest = new RefundOrderRequest();
            refundOrderRequest.setReturnOrderCodes(ridList);
            List<RefundOrder> refundOrders = refundOrderService.findAll(refundOrderRequest);
            returnPileOrderList.forEach(returnPileOrder -> {
                Optional<RefundOrder> refundOrderOptional = refundOrders.stream().filter(refundOrder -> refundOrder
                        .getReturnOrderCode().equals(returnPileOrder.getId())).findFirst();

                refundOrderOptional.ifPresent(refundOrder -> returnPileOrder.setRefundStatus(refundOrder.getRefundStatus
                        ()));
            });
        }
        return new PageImpl<>(returnPileOrderList, request.getPageable(), total);
    }

    /**
     * 填充实际退款金额，捕获异常，避免对主列表产生影响
     *
     * @param iterable
     */
    private void fillActualReturnPrice(List<NewPileReturnOrder> iterable) {
        try {
            List<String> returnOrderCodes = new ArrayList<>();
            // 如果有已退款的，查询退款流水的金额
            iterable.forEach(returnOrder -> {
                if (returnOrder.getReturnFlowState() == NewPileReturnFlowState.COMPLETED) {
                    returnOrderCodes.add(returnOrder.getId());
                }
            });

            if (returnOrderCodes.size() > 0) {
                RefundOrderRequest request = new RefundOrderRequest();
                request.setReturnOrderCodes(returnOrderCodes);
                // 查询退款单信息
                List<RefundOrder> refundOrderList = refundOrderService.findAll(request);

                if (!CollectionUtils.isEmpty(refundOrderList)) {

                    // 实退金额赋值
                    iterable.forEach(returnOrder ->
                            refundOrderList.stream()
                                    .filter(o -> Objects.equals(returnOrder.getId(), o.getReturnOrderCode()))
                                    .findFirst()
                                    .ifPresent(o -> {
                                        if (Objects.nonNull(o.getRefundBill())) {
                                            returnOrder.getReturnPrice().setActualReturnPrice(o.getRefundBill().getActualReturnPrice());
                                        }
                                    }));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据动态条件统计
     *
     * @param request
     * @return
     */
    public long countNum(NewPileReturnQueryRequest request) {
        Query query = new Query(request.build());
        return mongoTemplate.count(query, NewPileReturnOrder.class);
    }

    /**
     * 根据 RefundOrder 生成 RefundOrderResponse 对象
     *
     * @param returnOrderCode refundOrder
     * @return new RefundOrderResponse()
     */
    public RefundOrderResponse generatePileRefundOrderResponse(String returnOrderCode) {

        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrderCode);

        RefundOrderResponse refundOrderResponse = new RefundOrderResponse();
        BeanUtils.copyProperties(refundOrder, refundOrderResponse);

        if (StringUtils.isNotBlank(refundOrder.getReturnOrderCode())) {
            Buyer buyer = findReturnPileOrderById(refundOrder.getReturnOrderCode()).getBuyer();
            CustomerDetailVO customerDetailVO = new CustomerDetailVO();
            customerDetailVO.setCustomerId(buyer.getId());
            customerDetailVO.setCustomerName(buyer.getName());
            if (Objects.nonNull(customerDetailVO)) {
                refundOrderResponse.setCustomerName(customerDetailVO.getCustomerName());
                refundOrderResponse.setCustomerId(customerDetailVO.getCustomerId());
            }
        }
        CompanyInfoVO companyInfo = null;
        if (Objects.nonNull(refundOrder.getSupplierId())) {
            companyInfo = customerCommonService.getCompanyInfoById(refundOrder.getSupplierId());
        }
        if (Objects.nonNull(companyInfo)) {
            refundOrderResponse.setSupplierName(companyInfo.getSupplierName());
            if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                StoreVO store = companyInfo.getStoreVOList().get(0);
                refundOrderResponse.setStoreId(store.getStoreId());
            }
        }

        if (Objects.nonNull(refundOrder.getRefundBill()) && DeleteFlag.NO.equals(refundOrder.getRefundBill().getDelFlag())) {
            //从退单冗余信息中获取用户账户信息(防止用户修改后,查询的不是当时退单的账户信息)
            NewPileReturnOrder returnOrder = newPileReturnOrderRepository.findById(refundOrder.getReturnOrderCode()).orElse(null);
            if (returnOrder != null && returnOrder.getCustomerAccount() != null) {
                log.info("客户账户信息customerAccount: {}", returnOrder.getCustomerAccount());
                refundOrderResponse.setCustomerAccountName(returnOrder.getCustomerAccount().getCustomerBankName() + "" +
                        " " + (
                        StringUtils.isNotBlank(returnOrder.getCustomerAccount().getCustomerAccountNo()) ?
                                ReturnOrderService.getDexAccount(returnOrder.getCustomerAccount().getCustomerAccountNo()) : ""
                ));
            }

            refundOrderResponse.setActualReturnPrice(refundOrder.getRefundBill().getActualReturnPrice());
            refundOrderResponse.setActualReturnPoints(refundOrder.getRefundBill().getActualReturnPoints());
            refundOrderResponse.setReturnAccount(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setOfflineAccountId(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setComment(refundOrder.getRefundBill().getRefundComment());
            refundOrderResponse.setRefundBillCode(refundOrder.getRefundBill().getRefundBillCode());
            refundOrderResponse.setReturnAccountName(parseAccount(refundOrder));
            // 退款时间以boss端审核时间为准
            if (Objects.equals(RefundStatus.FINISH, refundOrder.getRefundStatus())) {
                refundOrderResponse.setRefundBillTime(refundOrder.getRefundBill().getCreateTime());
            }
            refundOrderResponse.setPayChannel(refundOrder.getRefundBill().getPayChannel());
            refundOrderResponse.setPayChannelId(refundOrder.getRefundBill().getPayChannelId());
        }

        return refundOrderResponse;
    }

    public NewPileReturnOrder findReturnPileOrderById(String rid) {
        NewPileReturnOrder newPileReturnOrder = newPileReturnOrderRepository.findById(rid).orElse(null);
        if (newPileReturnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }
        return newPileReturnOrder;
    }

    /**
     * 解析收款账号
     *
     * @param refundOrder refundOrder
     * @return string
     */
    private String parseAccount(RefundOrder refundOrder) {
        StringBuilder accountName = new StringBuilder();
        if (PayType.OFFLINE.equals(refundOrder.getPayType()) && Objects.nonNull(refundOrder.getRefundBill().getOfflineAccountId())) {
            OfflineAccountGetByIdResponse offlineAccount = offlineQueryProvider.getById(new OfflineAccountGetByIdRequest
                    (refundOrder
                            .getRefundBill()
                            .getOfflineAccountId())).getContext();

            if (offlineAccount.getAccountId() != null) {
                log.info("解析收款账号offlineAccount: {}", offlineAccount);
                Integer length = offlineAccount.getAccountName().length();
                accountName.append(offlineAccount.getBankName())
                        .append(" ").append(StringUtils.isNotEmpty(offlineAccount.getBankNo()) ?
                        RefundOrderService.getDexAccount(offlineAccount.getBankNo()) : "");
            }
        }
        return accountName.toString();
    }

    /**
     * 审核退单
     *
     * @param rid
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void audit(String rid, Operator operator) {
        //查询订单详情
        NewPileReturnOrder newPileReturnOrder = newPileReturnOrderRepository.findById(rid).orElse(null);
        //查询囤货订单详情
        NewPileTrade trade = queryCanReturnItemNumByTidNewPile(newPileReturnOrder.getTid());
        // 查询订单相关的所有退单
        List<NewPileReturnOrder> returnAllOrders = newPileReturnOrderRepository.findByTid(newPileReturnOrder.getTid());
        // 筛选出已完成的退单
        List<NewPileReturnOrder> returnPileOrders = returnAllOrders.stream().filter(allOrder -> allOrder.getReturnFlowState() ==
                NewPileReturnFlowState.COMPLETED)
                .collect(Collectors.toList());

        //计算所有已完成的退单总价格
        BigDecimal allOldPrice = new BigDecimal(0);
        for (NewPileReturnOrder order : returnPileOrders) {
            BigDecimal p = order.getReturnPrice().getApplyStatus() ? order.getReturnPrice().getApplyPrice() : order
                    .getReturnPrice().getTotalPrice();
            allOldPrice = allOldPrice.add(p);
        }
        ReturnPrice returnPrice = newPileReturnOrder.getReturnPrice();
        BigDecimal price = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(newPileReturnOrder.getTid()).get();
        // 退单金额校验 退款金额不可大于可退金额
        if (payOrder.getPayType() == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                == -1) {
            throw new SbcRuntimeException("K-050126");
        }
        //总退款金额小于囤货订单金额
        if (allOldPrice.compareTo(trade.getTradePrice().getGoodsPrice()) == -1) {
            throw new SbcRuntimeException("K-050126");
        }
        refundOrderService.generateRefundPileOrderByReturnOrderCode(rid, newPileReturnOrder.getBuyer().getId(), price,
                newPileReturnOrder.getPayType());
        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.AUDIT)
                .build();

        //售后审核通过发送MQ消息
        List<String> params = Lists.newArrayList(newPileReturnOrder.getReturnItems().get(0).getSkuName());
        String pic = newPileReturnOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_CHECK_PASS,
                params,
                newPileReturnOrder.getId(),
                newPileReturnOrder.getBuyer().getId(),
                pic,
                newPileReturnOrder.getBuyer().getAccount());

        log.info("ReturnPileOrderService.audit number:{}", returnPileOrders.size());
    }


    public List<ReturnPileOrder> findByCondition(ReturnQueryRequest request) {
        return mongoTemplate.find(new Query(request.build()), ReturnPileOrder.class);
    }

    /**
     * s2b线下退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void s2bBossRefundOffline(String rid, RefundBill refundBill, Operator operator, String tid) {
        log.info("NewPileReturnOrderService.s2bBoosRefundOffline rid:{}", rid);
        //修改退款单状态
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        Optional<RefundBill> result = refundBillService.save(refundBill);
        log.info("更新退款流水:{}", JSONObject.toJSONString(result.get()));
        refundOrder.setRefundBill(result.get());

        // 线下退款完成，发送MQ消息
        refundOffline(rid, operator, refundBill.getActualReturnPrice(), refundOrder);

//        refundOrder.setRefundStatus(RefundStatus.FINISH);
//        refundOrderRepository.saveAndFlush(refundOrder);
//        newPileTradeService.returnCoupon(tid);
//
//        //释放营销库存
//        NewPileTrade trade = newPileTradeService.detail(tid);
//        newPileTradeService.calMarketGoodsNum(trade.getTradeItems(), true);
//
//        NewPileReturnOrder newPileReturnOrder = this.findById(rid);
//        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
//                .addFlag(Boolean.FALSE)
//                .customerId(newPileReturnOrder.getBuyer().getId())
//                .orderId(newPileReturnOrder.getTid())
//                .returnId(rid)
//                .build();
//        newPileReturnOrderProducerService.returnPileOrderFlow(sendMQRequest);

        //囤货退款记录表
//        addStockWater(newPileReturnOrder);
//        log.info("ReturnPileOrderService.s2bBoosRefundOffline push kingdee:{}", newPileReturnOrder.getId());
        //推erp退货单
//        orderProducerService.pushStockReturnGoodsKingdee(newPileReturnOrder.getId(), 6 * 10L);


    }


    /**
     * 记入囤货流水表中
     *
     * @param returnPileOrder
     */
    private void addStockWater(NewPileReturnOrder returnPileOrder) {
        if (Objects.isNull(returnPileOrder)) {
            return;
        }
        log.info("ReturnPileOrderService.addStockWater Tid:{} id:{}", returnPileOrder.getTid(), returnPileOrder.getId());
        //囤货退款成功，添加流水表
        List<String> goodsInfos = returnPileOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        //通过囤货订单查找记录表
        List<PileStockRecord> pileStockRecordList = pileStockRecordRepository.getPileStockRecordByOrderCode(returnPileOrder.getTid(), goodsInfos);
        Long stockNum = pileStockRecordList.stream().filter(pileStockRecord -> pileStockRecord.getStockRecordNum().equals(pileStockRecord.getStockRecordRemainingNum())).count();
        log.info("ReturnPileOrderService.addStockWater pileStockRecordList.size:{} stockNum.intValue:{}", pileStockRecordList.size(),
                stockNum.intValue());
        if (pileStockRecordList != null && stockNum != null && pileStockRecordList.size() > 0 && pileStockRecordList.size() != stockNum.intValue()) {
            returnPileOrder.getReturnItems().stream().forEach(returnItem -> {
                pileStockRecordList.stream().forEach(pileStockRecord -> {
                    if (returnItem.getSkuId().equals(pileStockRecord.getGoodsInfoId())) {
                        //计算数量
                        Long num = returnItem.getNum().longValue();
                        pileStockRecord.setStockRecordRemainingNum(pileStockRecord.getStockRecordRemainingNum() + num);
                        pileStockRecord.setUpdateTime(LocalDateTime.now());
                        if (pileStockRecord.getStockRecordRemainingNum() == pileStockRecord.getStockRecordNum()) {
                            pileStockRecord.setIsUse(1L);
                        }
                    }
                });
            });
            pileStockRecordRepository.saveAll(pileStockRecordList);
        } else {
            //订单对应不上情况
            //累加数量(订单对应不上)
            Map<String, Long> cumulativeNumber = new HashMap<>();
            //记录已使用的记录表
            List<PileStockRecord> takePileStockRecord = new ArrayList<>();
            log.info("ReturnPileOrderService.addStockWater goodsInfos:{} buyerId:{}", JSONObject.toJSONString(goodsInfos)
                    , returnPileOrder.getBuyer().getId());
            List<PileStockRecord> pileStockRecords = pileStockRecordRepository.findSingleBackPileStockRecord(returnPileOrder.getBuyer().getId(), goodsInfos);
            if (pileStockRecords.size() > 0) {
                returnPileOrder.getReturnItems().stream().forEach(returnItem -> {
                    pileStockRecords.stream().forEach(pileStockRecord -> {
                        if (returnPileOrder.getBuyer().getId().equals(pileStockRecord.getCustomerId())
                                && returnItem.getSkuId().equals(pileStockRecord.getGoodsInfoId())) {
                            Long num = pileStockRecord.getStockRecordNum() - pileStockRecord.getStockRecordRemainingNum();
                            //某个商品已使用数量
                            Long cumulative = 0L;
                            if (cumulativeNumber.get(returnItem.getSkuId()) != null) {
                                cumulative = cumulativeNumber.get(returnItem.getSkuId());
                            }
                            Long returnNum = 0L;
                            if (returnItem.getNum() != null) {
                                returnNum = returnItem.getNum().longValue();
                            }
                            if (num > 0 && cumulative < returnNum) {
                                PileStockRecord stockRecord = KsBeanUtil.convert(pileStockRecord, PileStockRecord.class);
                                stockRecord.setUpdateTime(LocalDateTime.now());
                                //退单商品数大于等于已使用和一行记录表数据，插入累计
                                if (returnNum - (cumulative + num) >= 0) {
                                    cumulativeNumber.put(returnItem.getSkuId(), cumulative + num);
                                    stockRecord.setStockRecordRemainingNum(pileStockRecord.getStockRecordNum());
                                    stockRecord.setIsUse(1L);
                                    takePileStockRecord.add(stockRecord);
                                } else {
                                    //取记录表一行数据的部分数据
                                    Long part = returnNum - cumulative;
                                    cumulativeNumber.put(returnItem.getSkuId(), cumulative + part);
                                    stockRecord.setStockRecordRemainingNum(pileStockRecord.getStockRecordRemainingNum() + part);
                                    takePileStockRecord.add(stockRecord);
                                }
                            }
                        }
                    });
                });
                pileStockRecordRepository.saveAll(takePileStockRecord);
            }
        }
    }

    public NewPileReturnOrder findById(String rid) {
        NewPileReturnOrder newPileReturnOrder = newPileReturnOrderRepository.findById(rid).orElse(null);
        if (newPileReturnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }
        return newPileReturnOrder;
    }

    public NewPileReturnOrder dealEmptyFindById(String rid) {
        return newPileReturnOrderRepository.findById(rid).orElse(null);
    }

    /**
     * 根据订单id查询所有退单
     *
     * @param tid
     * @return
     */
    public List<NewPileReturnOrder> findReturnByTid(String tid) {
        List<NewPileReturnOrder> returnOrders = newPileReturnOrderRepository.findByTid(tid);
        return returnOrders == null ? Collections.emptyList() : returnOrders;
    }

    /**
     * 退款
     *
     * @param rid
     * @param operator
     * @param refundOrder
     */
    @Transactional
    @LcnTransaction
    public void refundOffline(String rid, Operator operator, BigDecimal price, RefundOrder refundOrder) {
        NewPileReturnOrder returnPileOrder = findById(rid);
        NewPileTrade pileTrade = newPileTradeService.detail(returnPileOrder.getTid());

        if (!NewPileReturnFlowState.AUDIT.equals(returnPileOrder.getReturnFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单状态已变化，请核实！");
        }

        if (Objects.isNull(pileTrade)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单不存在！");
        }

        if (NewPileFlowState.VOID.equals(pileTrade.getTradeState().getFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单已作废，不可退款，请核实！");
        }

        //DONE: 退款开关，囤货退款-线下
        //余额对接, 退款到余额
        RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_RETURN)
                .refund(pileTrade, returnPileOrder);

        //退单状态
        NewPileReturnStateRequest request = NewPileReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(NewPileReturnEvent.REFUND)
                .data(price)
                .build();

        newPileReturnFSMService.changePileState(request);
        pileTrade.setRefundFlag(true);
        newPileTradeService.updateTrade(pileTrade);

        //囤货线下退款退优惠券
        newPileTradeService.returnCoupon(returnPileOrder.getTid(), returnPileOrder.getId());

        if (returnPileOrder.getReturnType() == ReturnType.REFUND && returnPileOrder.getPlatform().equals(Platform.CUSTOMER)
                && (Objects.isNull(returnPileOrder.getWmsStats()) || !returnPileOrder.getWmsStats())) {

            //仅退款退单在退款完成后释放商品库存   ==》创建退单时就已经释放库存了
//            freePileGoodsNum(returnPileOrder.getTid());
            //发布订单退款事件
            newPileTradeService.fireRefundTradeEvent(returnPileOrder.getTid(), operator);
            pileTrade.getTradeState().setEndTime(LocalDateTime.now());
        }

        if (returnPileOrder.getPayType() == PayType.OFFLINE) {
            String businessId = pileTrade.getPayInfo().isMergePay() ? pileTrade.getParentId() : pileTrade.getId();
            newPileReturnOrderQuery.saveReconciliation(returnPileOrder, "", businessId, "");

            //囤货退货单推金碟
            if (kingdeeOpenState) {//判断是否开启新金蝶
                log.info("囤货退货单推送---------------》"+JSONObject.toJSONString(returnPileOrder));
                PayWay payWay = PayWay.CASH;
                returnOrderService.newPilePushRefundOrderKingdee(returnPileOrder, refundOrder, payWay);
            }
        }
    }

    /**
     * 查询订单详情,如已发货则带出可退商品数
     *
     * @param tid
     * @return
     */
    public NewPileTrade queryWmsCanReturnItemNumByTid(String tid) {
        NewPileTrade pileTrade = newPileTradeService.detail(tid);
        //校验支付状态
        PayOrderResponse payOrder = payOrderService.findPayOrder(pileTrade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
//        if (pileTrade.getTradeState().getFlowState() == NewFlowState.GROUPON) {
//            throw new SbcRuntimeException("K-050141");
//        }

        List<NewPileReturnOrder> returnsNotVoid = findReturnsNotVoid(tid);
        // 可退金额
        BigDecimal totalPrice = pileTrade.getTradeItems().stream()
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal retiredPrice = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
                .map(o -> o.getReturnPrice().getActualReturnPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
        pileTrade.setCanReturnPrice(canReturnPrice);
        return pileTrade;
    }

    /**
     * 查询退单列表，不包含已作废状态以及拒绝收货的退货单与拒绝退款的退款单
     *
     * @return
     */
    public List<NewPileReturnOrder> findReturnsNotVoid(String tid) {
        List<NewPileReturnOrder> returnOrders = newPileReturnOrderRepository.findByTid(tid);
        return filterFinishedReturnOrder(returnOrders);
    }

    /**
     * 过滤出已经收到退货的退单
     * (
     * 作废的不算
     * 拒绝收货不算
     * 仅退款的拒绝退款不算
     * )
     */
    public List<NewPileReturnOrder> filterFinishedReturnOrder(List<NewPileReturnOrder> returnPileOrders) {
        return returnPileOrders.stream().filter(t -> !t.getReturnFlowState().equals(ReturnFlowState.VOID)
                && !t.getReturnFlowState().equals(ReturnFlowState.REJECT_RECEIVE) &&
                !(t.getReturnType() == ReturnType.REFUND))
                .collect(Collectors.toList());
    }

    /**
     * 查询订单详情,如已发货则带出可退商品数
     * 囤货代客退单，未发货也可以退商品
     *
     * @param tid
     * @return
     */
    public NewPileTrade queryCanReturnItemNumByTid(String tid) {
        NewPileTrade pileTrade = newPileTradeService.detail(tid);
        //校验支付状态
        PayOrderResponse payOrder = payOrderService.findPayOrder(pileTrade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
//        if (pileTrade.getTradeState().getFlowState() == FlowState.GROUPON) {
//            throw new SbcRuntimeException("K-050141");
//        }

        /*if(trade.getDeliverWay() == DeliverWay.PICK_SELF){
            throw new SbcRuntimeException("K-050142");
        }*/
        //排除特价商品
        // trade.getTradeItems().removeIf(next -> Objects.nonNull(next.getGoodsInfoType()) && next.getGoodsInfoType() == 1);
        DeliverStatus deliverStatus = pileTrade.getTradeState().getDeliverStatus();
        if (/*deliverStatus != DeliverStatus.NOT_YET_SHIPPED &&*/ deliverStatus != DeliverStatus.VOID) {
//            //计算商品可退数
//            Map<String, BigDecimal> map = findLeftItems(pileTrade);
            //查询囤货记录表
            List<PileStockRecord> stockRecordList = pileStockRecordRepository.getStockRecordOrderCode(tid);
            Map<String, BigDecimal> stockRecordMap = stockRecordCanBackNumber(stockRecordList);
//            pileTrade.getTradeItems().forEach(
//                    item -> item.setCanReturnNum(map.get(item.getSkuId()))
//            );
            pileTrade.getTradeItems().forEach(
                    item -> item.setCanReturnNum(stockRecordMap.get(item.getSkuId()))
            );

//            //计算赠品可退数
//            if (CollectionUtils.isNotEmpty(pileTrade.getGifts())) {
//                Map<String, BigDecimal> giftMap = findLeftGiftItems(pileTrade);
//                pileTrade.getGifts().forEach(
//                        item -> item.setCanReturnNum(giftMap.get(item.getSkuId()))
//                );
//            }
        }

        List<NewPileReturnOrder> returnsNotVoid = findReturnsNotVoid(tid);
        // 已退积分
        Long retiredPoints = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPoints()) && Objects.nonNull(o.getReturnPoints().getActualPoints()))
                .map(o -> o.getReturnPoints().getActualPoints())
                .reduce((long) 0, Long::sum);
        // 可退积分
        Long points = pileTrade.getTradePrice().getPoints() == null ? 0 : pileTrade.getTradePrice().getPoints();
        pileTrade.setCanReturnPoints(points - retiredPoints);
        // 可退金额
        BigDecimal totalPrice = pileTrade.getTradeItems().stream()
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal retiredPrice = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
                .map(o -> o.getReturnPrice().getActualReturnPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
        pileTrade.setCanReturnPrice(canReturnPrice);
        return pileTrade;
    }

    /**
     * 计算囤货订单
     *
     * @param stockRecordList
     * @return
     */
    private Map<String, BigDecimal> stockRecordCanBackNumber(List<PileStockRecord> stockRecordList) {
        return stockRecordList.stream().collect(Collectors.toMap(PileStockRecord::getGoodsInfoId, pileStockRecord -> {
            Long stockRecordNum = pileStockRecord.getStockRecordNum();
            Long remainingNum = pileStockRecord.getStockRecordRemainingNum();
            if (stockRecordNum == remainingNum) {
                return BigDecimal.ZERO;
            } else {
                return BigDecimal.valueOf(stockRecordNum - remainingNum);
            }
        }));
    }

    /**
     * 创建退款单
     *
     * @param returnPileOrder
     */
    private void createWMSRefund(NewPileReturnOrder returnPileOrder, Operator operator, NewPileTrade pileTrade) {

        //校验该订单关联的退款单状态
        List<NewPileReturnOrder> returnPileOrders = newPileReturnOrderRepository.findByTid(pileTrade.getId());
        if (!CollectionUtils.isEmpty(returnPileOrders)) {
            Optional<NewPileReturnOrder> optional = returnPileOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == NewPileReturnFlowState.VOID))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        returnPileOrder.setReturnType(ReturnType.REFUND);
        //设置退货商品
        returnPileOrder.getReturnItems().forEach(returnItem -> {
            for (TradeItem item : pileTrade.getTradeItems()) {
                if (item.getSkuNo().equals(returnItem.getSkuNo())) {
                    returnItem.setSkuId(item.getSkuId());
                    returnItem.setSkuNo(item.getSkuNo());
                    returnItem.setPic(item.getPic());
                    returnItem.setSkuName(item.getSkuName());
                    returnItem.setUnit(item.getUnit());
                    returnItem.setErpSkuNo(item.getErpSkuNo());
                    returnItem.setSpecDetails(item.getSpecDetails());
                    returnItem.setGoodsSubtitle(item.getGoodsSubtitle());
                }
            }
        });
    }

    /**
     * 创建退款单
     *
     * @param returnPileOrder
     */
    private void createRefund(NewPileReturnOrder returnPileOrder, Operator operator, NewPileTrade pileTrade) {
        //校验该订单关联的退款单状态
        List<NewPileReturnOrder> returnPileOrders = newPileReturnOrderRepository.findByTid(pileTrade.getId());
        if (!CollectionUtils.isEmpty(returnPileOrders)) {
            Optional<NewPileReturnOrder> optional = returnPileOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == NewPileReturnFlowState.VOID))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }
        // 新增订单日志
        newPileTradeService.returnOrder(returnPileOrder.getTid(), operator);
        returnPileOrder.setReturnType(ReturnType.REFUND);

        //设置退货商品
        returnPileOrder.setReturnItems(pileTrade.getTradeItems().stream().map(item -> ReturnItem.builder()
                .num(BigDecimal.valueOf(item.getNum()))
                .skuId(item.getSkuId())
                .skuNo(item.getSkuNo())
                .pic(item.getPic())
                .skuName(item.getSkuName())
                .unit(item.getUnit())
                .price(item.getPrice())
                .erpSkuNo(item.getErpSkuNo())
                .splitPrice(item.getSplitPrice())
                .specDetails(item.getSpecDetails())
                .goodsSubtitle(item.getGoodsSubtitle())
                .build()).collect(Collectors.toList()));

    }

    @Autowired
    private PilePurchaseRepository pilePurchaseRepository;

    /**
     * 囤货订单中可退单的数量
     *
     * @param pileTrade
     */
    private Map<String, BigDecimal> findLeftItems(NewPileTrade pileTrade) {
//        if (!pileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !pileTrade.getTradeState()
//                .getFlowState().equals(FlowState.COMPLETED)) {
//            throw new SbcRuntimeException("K-050002");
//        }

//        Map<String, BigDecimal> map = pileTrade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
//                g->BigDecimal.valueOf(g.getDeliveredNum())));

        //订单商品id集合
        List<String> skuIds = pileTrade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        //客户订单囤货商品详情
        List<PilePurchase> pilePurchaseList = pilePurchaseRepository.queryPurchaseByGoodsInfoIdsAndCustomerId(skuIds, pileTrade.getBuyer().getId());
        //客户订单囤货商品数量map k->goodsInfoId,v->goodsNum
        Map<String, Long> goodsNumMap = pilePurchaseList.stream().collect(Collectors.toMap(PilePurchase::getGoodsInfoId, p -> p.getGoodsNum()));

        Map<String, BigDecimal> map = pileTrade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                g -> BigDecimal.valueOf(g.getNum())));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(pileTrade.getId()).stream().filter(returnPileOrder -> Objects.isNull(returnPileOrder.getWmsStats()) || !returnPileOrder.getWmsStats())

                .map(NewPileReturnOrder::getReturnItems)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
        Map<String, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getSkuId);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String key = entry.getKey();
                            BigDecimal total = map.get(key);
                            Double returned = 0d;
                            if (groupMap.get(key) != null) {
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }

                            //计算出可退数量
                            BigDecimal canReturnNum = total.subtract(BigDecimal.valueOf(returned));
                            //如果囤货商品数量不为空
                            if (Objects.nonNull(goodsNumMap.get(key))) {
                                //如果商品可退数量大于囤货数量
                                if (1 == canReturnNum.compareTo(BigDecimal.valueOf(goodsNumMap.get(key)))) {
                                    //可退数量返回囤货数量
                                    canReturnNum = BigDecimal.valueOf(goodsNumMap.get(key));
                                }
                            } else {
                                canReturnNum = BigDecimal.ZERO;
                            }
                            return canReturnNum;
                        }
                ));
    }

    private void verifyNum(NewPileTrade pileTrade, List<ReturnItem> returnItems) {
        Map<String, BigDecimal> map = this.findLeftItems(pileTrade);
        returnItems.stream().forEach(
                t -> {
                    if (map.get(t.getSkuId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                        throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                    }
                    if (t.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new SbcRuntimeException("K-050102");
                    }

                }
        );
    }


    /**
     * 创建退货单
     *
     * @param returnPileOrder
     */
    private void createReturn(NewPileReturnOrder returnPileOrder, Operator operator, NewPileTrade pileTrade) {

        // 新增订单日志
        newPileTradeService.returnOrder(returnPileOrder.getTid(), operator);

        this.verifyNum(pileTrade, returnPileOrder.getReturnItems());

//        returnPileOrder.setReturnType(ReturnType.RETURN);
        //囤货订单没有退货，只有退款
        returnPileOrder.setReturnType(ReturnType.REFUND);

        //填充退货商品信息
        Map<String, BigDecimal> map = findLeftItems(pileTrade);
        returnPileOrder.getReturnItems().forEach(item ->
                {
                    item.setSkuName(pileTrade.skuItemMapOrigin().get(item.getSkuId()).getSkuName());
                    item.setPic(pileTrade.skuItemMapOrigin().get(item.getSkuId()).getPic());
                    item.setSkuNo(pileTrade.skuItemMapOrigin().get(item.getSkuId()).getSkuNo());
                    item.setSpecDetails(pileTrade.skuItemMapOrigin().get(item.getSkuId()).getSpecDetails());
                    item.setUnit(pileTrade.skuItemMapOrigin().get(item.getSkuId()).getUnit());
                    item.setGoodsSubtitle(pileTrade.skuItemMapOrigin().get(item.getSkuId()).getGoodsSubtitle());
                    item.setCanReturnNum(map.get(item.getSkuId()));
                }
        );

    }


    /**
     * 退单创建
     *
     * @param returnPileOrder
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public String create(NewPileReturnOrder returnPileOrder, Operator operator, Integer forceRefund) {
        if (returnPileOrder.getDescription().length() > 100) {
            throw new SbcRuntimeException("K-000009");
        }
        //查询订单信息
        NewPileTrade pileTrade;
        if (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats()) { //wms自动退单
            pileTrade = this.queryWmsCanReturnItemNumByTid(returnPileOrder.getTid());
        } else {
            pileTrade = this.queryCanReturnItemNumByTid(returnPileOrder.getTid());
        }
        //查询该订单所有退单
        List<NewPileReturnOrder> returnPileOrderList = newPileReturnOrderRepository.findByTid(pileTrade.getId());

        //筛选出已完成的退单列表
        List<NewPileReturnOrder> completedReturnOrders = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(returnPileOrderList)) {
            //过滤处理中的退单
            List<NewPileReturnOrder> returnPileOrders = returnPileOrderList.stream().filter(item -> item.getReturnFlowState() !=
                    NewPileReturnFlowState.COMPLETED
                    && item.getReturnFlowState() != NewPileReturnFlowState.VOID
            )
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(returnPileOrders)) {
                throw new SbcRuntimeException("K-050120");
            }

            completedReturnOrders = returnPileOrderList.stream().filter(allOrder -> allOrder
                    .getReturnFlowState() == NewPileReturnFlowState.COMPLETED)
                    .collect(Collectors.toList());
        }


        //是否仅退款 =====>囤货订单直接退款，无退货操作
//        boolean isRefund = pileTrade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || pileTrade
//                .getTradeState().getDeliverStatus()
//                == DeliverStatus.VOID;
        boolean isRefund = true;
        //如果是代客退单
        if (Objects.nonNull(forceRefund) && 1 == forceRefund) {
            isRefund = false;
        }

        //退单总金额
        ReturnPrice returnPrice = returnPileOrder.getReturnPrice();

        //计算该订单下所有已完成退单的总金额
        BigDecimal allOldPrice = new BigDecimal(0);
        for (NewPileReturnOrder order : completedReturnOrders) {
            BigDecimal p = order.getReturnPrice().getApplyStatus() ? order.getReturnPrice().getApplyPrice() : order
                    .getReturnPrice().getTotalPrice();
            allOldPrice = allOldPrice.add(p);
        }
        //是否boss创建并且申请特价状态
        boolean isSpecial = returnPrice.getApplyStatus() && operator.getPlatform() == Platform.BOSS;
        if (!isSpecial) {
            //退单商品价格校验
//            verifyPrice(trade.skuItemMap(), returnOrder.getReturnItems());
        }

        List<ReturnItem> returnItems = returnPileOrder.getReturnItems();
        List<String> skuIds = returnItems.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
        List<TradeDistributeItemVO> tradeDistributeItemVos = returnPileOrder.getDistributeItems();
        BigDecimal price = null;
        Long points = 0L;
        if (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats()) {
            price = returnPileOrder.getReturnPrice().getActualReturnPrice();
        } else if (isRefund) {
            price = isSpecial ? returnPrice.getApplyPrice() :
                    pileTrade.getTradeItems().stream().map(t -> t.getSplitPrice())
                            .reduce(BigDecimal::add).get().add(Objects.nonNull(pileTrade.getTradePrice().getDeliveryPrice
                            ()) ? pileTrade.getTradePrice().getDeliveryPrice() : BigDecimal.ZERO);
//            points = pileTrade.getCanReturnPoints();
            returnPileOrder.setDistributeItems(tradeDistributeItemVos);
        } else {
            //------------------start-------------------
            returnPileOrder.getReturnItems().forEach(info -> {
                Optional<TradeItem> tradeItemOptional = pileTrade.getTradeItems().stream().filter(tradeItem -> info
                        .getSkuId().equals(tradeItem.getSkuId())).findFirst();
                if (!tradeItemOptional.isPresent()) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                TradeItem tradeItem = tradeItemOptional.get();
                info.setOrderSplitPrice(tradeItem.getSplitPrice());
                if (tradeItem.getNum() == 0) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                BigDecimal unitPrice = tradeItem.getSplitPrice().divide(new BigDecimal(tradeItem.getNum()), 2,
                        BigDecimal.ROUND_DOWN);
                if (tradeItem.getCanReturnNum().intValue() - info.getNum().intValue() > 0) {
                    // 该商品未退完
                    if (unitPrice.compareTo(new BigDecimal("0.01")) == -1) {
                        info.setSplitPrice(BigDecimal.ZERO);
                    } else {
                        info.setSplitPrice(unitPrice.multiply(info.getNum()));
                    }
                } else {
                    //该商品已退完
                    if (unitPrice.compareTo(new BigDecimal("0.01")) == -1) {
                        info.setSplitPrice(tradeItem.getSplitPrice());
                    } else {
                        info.setSplitPrice(tradeItem.getSplitPrice().subtract(unitPrice.multiply(BigDecimal.valueOf(tradeItem.getNum()).subtract(info.getNum()))));
                    }
                }
                //info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
            });
            //-------------------------end--------------------------
            price = isSpecial ? returnPrice.getApplyPrice() :
                    returnPileOrder.getReturnItems().stream().map(t -> t.getSplitPrice())
                            .reduce(BigDecimal::add).get();
            // 计算积分
//            if (Objects.nonNull(pileTrade.getTradePrice().getPoints())) {
//                points = getPoints(returnPileOrder, pileTrade);
//            }

            //分销商品数据接口赋值开始
//            tradeDistributeItemVos = tradeDistributeItemVos.stream().filter(item -> skuIds
//                    .contains(item.getGoodsInfoId())).collect(Collectors.toList());
//            if (Objects.nonNull(tradeDistributeItemVos) && tradeDistributeItemVos.size() > 0) {
//                List<TradeDistributeItemVO> distributeItems = tradeDistributeItemVos.stream().map
//                        (tradeDistributeItemVo -> {
//                            Optional<TradeItem> tradeItemOptional =
//                                    pileTrade.getTradeItems().stream().filter(tradeItem -> tradeDistributeItemVo
//                                            .getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst();
//                            TradeItem tradeItem = tradeItemOptional.get();
//                            Optional<ReturnItem> returnItemOpt = returnItems.stream().filter(item -> item.getSkuId()
//                                    .equals(tradeDistributeItemVo.getGoodsInfoId())).findFirst();
//                            BigDecimal unitPrice = tradeItem.getSplitPrice().divide(new BigDecimal
//                                            (tradeDistributeItemVo.getNum())
//                                    , 2,
//                                    BigDecimal.ROUND_DOWN);
//                            if (returnItemOpt.isPresent()) {
//                                ReturnItem returnItem = returnItemOpt.get();
//                                if (tradeItem.getCanReturnNum().intValue() - returnItem.getNum().intValue() > 0) {
//                                    //部分退款
//                                    if (unitPrice.compareTo(new BigDecimal("0.01")) == -1) {
//                                        tradeDistributeItemVo.setActualPaidPrice(BigDecimal.ZERO);
//                                    } else {
//                                        tradeDistributeItemVo.setActualPaidPrice(unitPrice.multiply(returnItem.getNum()));
//                                    }
//                                } else {
//                                    //该商品已退完
//                                    if (unitPrice.compareTo(new BigDecimal("0.01")) == -1) {
//                                        tradeDistributeItemVo.setActualPaidPrice(BigDecimal.ZERO);
//                                    } else {
//                                        tradeDistributeItemVo.setActualPaidPrice(tradeItem.getSplitPrice().subtract(unitPrice.multiply(new BigDecimal
//                                                (tradeItem.getNum()).subtract(returnItem.getNum()))));
//                                    }
//                                }
//                                tradeDistributeItemVo.setNum(returnItem.getNum().longValue());
//                            }
//                            return tradeDistributeItemVo;
//
//                        }).collect(Collectors.toList());
//                returnPileOrder.setDistributeItems(distributeItems);
//            }
            //分销商品的数据处理结束
        }
        returnPileOrder.getReturnPrice().setTotalPrice(price);
        returnPileOrder.getReturnPrice().setApplyPrice(price);
        if (returnPileOrder.getReturnPoints() == null) {
            returnPileOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());
        }

        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnPileOrder.getTid()).get();
        if (operator.getPlatform() == Platform.BOSS && PayType.fromValue(Integer.parseInt(pileTrade.getPayInfo()
                .getPayTypeId())) == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                == -1) {
            throw new SbcRuntimeException("K-050126");
        }

        if (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats()) {
            createWMSRefund(returnPileOrder, operator, pileTrade);
        } else if (isRefund) {
            createRefund(returnPileOrder, operator, pileTrade);
        } else {
            createReturn(returnPileOrder, operator, pileTrade);
        }


        returnPileOrder.setBuyer(pileTrade.getBuyer());
        returnPileOrder.setWareId(pileTrade.getWareId());
        returnPileOrder.setConsignee(pileTrade.getConsignee());
        returnPileOrder.setCreateTime(LocalDateTime.now());
        returnPileOrder.setPayType(PayType.valueOf(pileTrade.getPayInfo().getPayTypeName()));
        returnPileOrder.setPlatform(operator.getPlatform());
        returnPileOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());

        String rid = generatorService.generate("R");
        returnPileOrder.setId(rid);
        //boolean flag = false;

        //记录日志
        returnPileOrder.appendReturnEventLog(
                new ReturnEventLog(operator, "创建退单", "创建退单", "", LocalDateTime.now())
        );

        returnPileOrder.setReturnFlowState(NewPileReturnFlowState.AUDIT);

        // 计算并设置需要退的赠品
//        getAndSetReturnGifts(returnPileOrder, pileTrade, returnPileOrderList);

        //wms三方推送
//        if (wmsPushFlag){
//            if(isRefund){
//                //推送取消订单
//                if(pushCancelOrder(returnPileOrder, pileTrade.getWareHouseCode())){
//                    returnPileOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
//                }else{
//                    if(forceRefund!=null&&forceRefund==1){
//
//                    }else{
//                        throw new SbcRuntimeException(OrderErrorCode.ORDER_HAS_BEEN_PICK_IN_WMS,"申请退款失败，商品已拣货");
//                    }
//                }
//            }
//        }
        //刷新进批次号
        returnPileOrder.getReturnItems().forEach(info -> {
            Optional<TradeItem> tradeItemOptional = pileTrade.getTradeItems().stream().filter(tradeItem -> info
                    .getSkuId().equals(tradeItem.getSkuId())).findFirst();
            if (tradeItemOptional.isPresent()) {
                TradeItem tradeItem = tradeItemOptional.get();
                info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
            }
        });

        BigDecimal canReturnPrice = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();

        //自动退款
        Optional<RefundOrder> refundOrderOptional = null;

        //仅退款直接跳过审核生成退款单
        if (isRefund || (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats())) {
            returnPileOrder.setReturnFlowState(NewPileReturnFlowState.AUDIT);
            refundOrderOptional = refundOrderService.generateRefundPileOrderByEnity(returnPileOrder, returnPileOrder.getBuyer().getId(), canReturnPrice,
                    returnPileOrder.getPayType());

            //=====================================
        }
        //保存退单
        this.addReturnOrder(returnPileOrder);

        /**减少商品囤货数量开始*/
        String customerId = pileTrade.getBuyer().getId();
        if (isRefund) {
            //查询囤货记录表
            List<PileStockRecord> stockRecordList = pileStockRecordRepository.getStockRecordOrderCode(returnPileOrder.getTid());
            Map<String, BigDecimal> stockRecordMap = stockRecordCanBackNumber(stockRecordList);
            //减少囤货数量//多个商品
            List<TradeItem> tradeItems = pileTrade.getTradeItems();
            tradeItems.stream().forEach(tradeItem -> {
                String spuId = tradeItem.getSpuId();
                String skuId = tradeItem.getSkuId();
                //囤货记录表商品是否有为0的，有就不让申请囤货退款
                if (stockRecordMap.containsKey(skuId) && stockRecordMap.get(skuId).compareTo(BigDecimal.ZERO) == 0) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "我的囤货商品数量不足订单商品数量，无法退单！");
                }
                //购买数量
                Long num = tradeItem.getNum();
                //通过spuId+skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
                List<PilePurchase> pilePurchases = pilePurchaseRepository.queryPilePurchase(customerId, spuId, skuId);
                if (CollectionUtils.isNotEmpty(pilePurchases)) {
                    for (PilePurchase pilePurchase : pilePurchases) {
                        //每一笔的购买数量从
                        Long goodsNum = pilePurchase.getGoodsNum();
                        if (goodsNum >= num) {
                            //减去提货数量并且跳出循环
                            pilePurchase.setGoodsNum(goodsNum - num);
                            break;
                        } else {
                            //当购买数量小于提货数量,清空购买数量，减少提货数量
//                        pilePurchase.setGoodsNum(0L);
//                        num = num - pilePurchase.getGoodsNum();
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品：" + tradeItem.getSkuName() + "囤货数量不足订单购买数量，不允许退单！");
                        }
                    }
                    //减完以后
                    pilePurchaseRepository.saveAll(pilePurchases);
                }
            });
        } else {//如果是代客退单
            //减少囤货数量//多个商品
            returnItems.stream().forEach(returnItem -> {
                String skuId = returnItem.getSkuId();
                //退单数量
                Long returnNum = returnItem.getNum().longValue();
                //通过skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
                List<PilePurchase> pilePurchases = pilePurchaseRepository.getPilePurchase(customerId, skuId);
                if (CollectionUtils.isNotEmpty(pilePurchases)) {
                    for (PilePurchase pilePurchase : pilePurchases) {
                        //每一笔的购买数量从
                        Long goodsNum = pilePurchase.getGoodsNum();
                        if (goodsNum >= returnNum) {
                            //减去提货数量并且跳出循环
                            pilePurchase.setGoodsNum(goodsNum - returnNum);
                            break;
                        } else {
                            //当购买数量小于提货数量,清空购买数量，减少提货数量
//                        pilePurchase.setGoodsNum(0L);
//                        num = num - pilePurchase.getGoodsNum();
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品：" + returnItem.getSkuName() + "囤货数量{" + goodsNum + "}不足退单数量{" + returnNum + "}，不允许退单！");
                        }
                    }
                    //减完以后
                    pilePurchaseRepository.saveAll(pilePurchases);
                }
            });
        }
        /**减少商品囤货数量结束*/

        this.operationLogMq.convertAndSend(operator, "创建囤货退单", "创建囤货退单");


        if (operator.getPlatform() == Platform.BOSS) {
            audit(rid, operator);
        }

        // 创建退单时，发送MQ消息
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.TRUE)
                .customerId(pileTrade.getBuyer().getId())
                .orderId(pileTrade.getId())
                .returnId(rid)
                .build();
        newPileReturnOrderProducerService.returnPileOrderFlow(sendMQRequest);

        //售后单提交成功发送MQ消息
        List<String> params = Lists.newArrayList(returnPileOrder.getReturnItems().get(0).getSkuName());
        String pic = returnPileOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_COMMIT_SUCCESS,
                params,
                returnPileOrder.getId(),
                returnPileOrder.getBuyer().getId(),
                pic,
                returnPileOrder.getBuyer().getAccount()
        );

        //TODO:自动退款
        /** RefundOrder refundOrder = null;
         if(Objects.nonNull(refundOrderOptional) && refundOrderOptional.isPresent()){
         refundOrder = refundOrderOptional.get();
         // 填充退款流水
         RefundBill refundBill;
         if ((refundBill = refundOrder.getRefundBill()) == null) {
         refundBill = new RefundBill();
         refundBill.setActualReturnPrice(Objects.isNull(canReturnPrice) ? refundOrder.getReturnPrice() :
         canReturnPrice);
         refundBill.setActualReturnPoints(returnPileOrder.getReturnPoints().getApplyPoints());
         refundBill.setCreateTime(LocalDateTime.now());
         refundBill.setRefundId(refundOrder.getRefundId());
         refundBill.setRefundComment("用户申请"+operator.getAccount()+"退款");
         refundBillService.save(refundBill);
         } else {
         refundBill.setActualReturnPrice(Objects.isNull(canReturnPrice) ? refundOrder.getReturnPrice() :
         canReturnPrice);
         refundBill.setActualReturnPoints(returnPileOrder.getReturnPoints().getApplyPoints());
         }
         refundBillService.saveAndFlush(refundBill);
         //设置退款单状态为待平台退款
         refundOrder.setRefundStatus(RefundStatus.APPLY);
         if (returnPileOrder.getReturnPrice().getTotalPrice().compareTo(canReturnPrice) == 1) {
         returnPileOrder.getReturnPrice().setApplyStatus(true);
         returnPileOrder.getReturnPrice().setApplyPrice(canReturnPrice);
         }
         returnPileOrder.getReturnPoints().setActualPoints(returnPileOrder.getReturnPoints().getApplyPoints());
         refundOrderRepository.saveAndFlush(refundOrder);
         String detail = String.format("退单[%s]已添加线上退款单，操作人:%s", returnPileOrder.getId(), operator.getName());
         returnPileOrder.appendReturnEventLog(
         ReturnEventLog.builder()
         .operator(operator)
         .eventType(ReturnEvent.REFUND.getDesc())
         .eventTime(LocalDateTime.now())
         .eventDetail(detail)
         .build()
         );
         returnPileOrderService.updateReturnOrder(returnPileOrder);
         }
         //================================================

         //退款
         //退款退单状态需要是已审核
         if (returnPileOrder != null && returnPileOrder.getReturnType() == ReturnType.REFUND) {
         if (returnPileOrder.getReturnFlowState() != ReturnFlowState.AUDIT &&
         returnPileOrder.getReturnFlowState() != ReturnFlowState.REFUND_FAILED) {
         throw new SbcRuntimeException("K-050004");
         }
         }
         //退货退单状态需要是已收到退货/退款失败
         if (returnPileOrder != null && returnPileOrder.getReturnType() == ReturnType.RETURN) {
         if (returnPileOrder.getReturnFlowState() != ReturnFlowState.RECEIVED &&
         returnPileOrder.getReturnFlowState() != ReturnFlowState.REFUND_FAILED) {
         throw new SbcRuntimeException("K-050004");
         }
         }
         // 查询退款单
         if (Objects.nonNull(pileTrade) && Objects.nonNull(pileTrade.getBuyer()) && StringUtils.isNotEmpty(pileTrade.getBuyer()
         .getAccount())) {
         pileTrade.getBuyer().setAccount(ReturnOrderService.getDexAccount(pileTrade.getBuyer().getAccount()));
         }

         if (Objects.isNull(operator) || StringUtils.isBlank(operator.getUserId())) {
         operator = Operator.builder().ip("127.0.0.1").adminId("1").name("system").platform(Platform
         .BOSS).build();
         }

         //添加wms货物不足退款标识
         Boolean wmsStats = null;
         if(Objects.nonNull(wmsStats)){
         returnPileOrder.setWmsStats(wmsStats);
         }

         refundOrderService.autoRefundPile(Collections.singletonList(pileTrade),Collections.singletonList(returnPileOrder),Collections.singletonList(refundOrder),operator);
         //+++++++++++++++++++++++
         */
        return rid;
    }


    /**
     * 过滤出已经收到退货的退单
     * (
     * 作废的不算
     * 拒绝收货不算
     * 仅退款的拒绝退款不算
     * )
     */
    public List<NewPileReturnOrder> filterFinishedReturnOrderNewPile(List<NewPileReturnOrder> returnOrders) {
        return returnOrders.stream().filter(t -> !t.getReturnFlowState().equals(NewPileReturnFlowState.VOID)
                && !(t.getReturnType() == ReturnType.REFUND))
                .collect(Collectors.toList());
    }

    /**
     * 查询订单详情
     *
     * @param tid
     * @return
     */
    public NewPileTrade queryCanReturnItemNumByTidNewPile(String tid) {
        NewPileTrade trade = newPileTradeService.detail(tid);
        //校验支付状态trade
        PayOrderResponse payOrder = payOrderService.findPayOrder(trade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
        //计算可退商品数量------------start
        List<GoodsPickStock> goodsPickStockByNewPileTradeNo = goodsPickStockRepository.getGoodsPickStockByNewPileTradeNo(trade.getId());
        Map<String, Long> goodsPickStockMap = goodsPickStockByNewPileTradeNo.stream().filter(i -> i.getStock() != null).
                collect(Collectors.groupingBy(GoodsPickStock::getGoodsInfoId, Collectors.summingLong(GoodsPickStock::getStock)));

        log.info("goodsPickStockByNewPileTradeNo-------->"+JSONObject.toJSONString(goodsPickStockByNewPileTradeNo));
        trade.getTradeItems().forEach(
                item -> item.setCanReturnNum(BigDecimal.valueOf(goodsPickStockMap.get(item.getSkuId())))
        );

        log.info("trade.getTradeItems()-------->"+JSONObject.toJSONString(trade.getTradeItems()));
        //计算可退商品数量-----------end

        //过滤可退商品为0的商品
        List<TradeItem> tradeItemList = trade.getTradeItems().stream().filter(t -> BigDecimal.ZERO.compareTo(t.getCanReturnNum()) == NumberUtils.INTEGER_MINUS_ONE).collect(Collectors.toList());

        trade.setTradeItems(tradeItemList);

        List<NewPileReturnOrder> returnsNotVoid = findReturnsNotVoid(tid);

        // 可退金额
        BigDecimal totalPrice = trade.getTradeItems().stream()
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal retiredPrice = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
                .map(o -> o.getReturnPrice().getActualReturnPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
        trade.setCanReturnPrice(canReturnPrice);
        return trade;
    }

    /**
     * 订单中可退货的数量
     *
     * @param trade
     */
    public Map<String, BigDecimal> findLeftItems(Trade trade) {
        if (!trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !trade.getTradeState()
                .getFlowState().equals(FlowState.COMPLETED)) {
            throw new SbcRuntimeException("K-050002");
        }

        Map<String, BigDecimal> map = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                g -> BigDecimal.valueOf(g.getDeliveredNum()), (o, n) -> o.add(n)));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream()
                .filter(returnOrder -> Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())
                .map(NewPileReturnOrder::getReturnItems)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
        Map<String, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getSkuId);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String key = entry.getKey();
                            BigDecimal total = map.get(key);
                            Double returned = 0d;
                            if (groupMap.get(key) != null) {
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }
                            return total.subtract(BigDecimal.valueOf(returned));
                        },
                        (o, n) -> o.add(n)
                ));
    }

    /**
     * 订单中可退货的数量
     *
     * @param trade
     */
    public Map<String, BigDecimal> findLeftItemsNewPile(NewPileTrade trade) {
        if (!trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !trade.getTradeState()
                .getFlowState().equals(FlowState.COMPLETED)) {
            throw new SbcRuntimeException("K-050002");
        }

        Map<String, BigDecimal> map = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                g -> BigDecimal.valueOf(g.getDeliveredNum()), (o, n) -> o.add(n)));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream().filter(returnOrder -> Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())

                .map(NewPileReturnOrder::getReturnItems)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
        Map<String, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getSkuId);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String key = entry.getKey();
                            BigDecimal total = map.get(key);
                            Double returned = 0d;
                            if (groupMap.get(key) != null) {
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }
                            return total.subtract(BigDecimal.valueOf(returned));
                        },
                        (o, n) -> o.add(n)
                ));
    }

    /**
     * 拆箱订单中可退货的数量
     *
     * @param trade
     */
    public Map<Long, BigDecimal> findLeftDevanningItems(Trade trade) {
        if (!trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !trade.getTradeState()
                .getFlowState().equals(FlowState.COMPLETED)) {
            throw new SbcRuntimeException("K-050002");
        }

        Map<Long, BigDecimal> map = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getDevanningId,
                g -> BigDecimal.valueOf(g.getDeliveredNum())));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream().filter(returnOrder -> Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())
                .map(NewPileReturnOrder::getReturnItems)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
        Map<Long, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getDevanningId);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Long key = entry.getKey();
                            BigDecimal total = map.get(key);
                            Double returned = 0d;
                            if (groupMap.get(key) != null) {
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }
                            return total.subtract(BigDecimal.valueOf(returned));
                        },
                        (o, n) -> o.add(n)
                ));
    }


    /**
     * 拆箱订单中可退货的数量
     *
     * @param trade
     */
    public Map<Long, BigDecimal> findLeftDevanningItemsNewPile(NewPileTrade trade) {
        if (!trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !trade.getTradeState()
                .getFlowState().equals(FlowState.COMPLETED)) {
            throw new SbcRuntimeException("K-050002");
        }

        Map<Long, BigDecimal> map = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getDevanningId,
                g -> BigDecimal.valueOf(g.getDeliveredNum())));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream().filter(returnOrder -> Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())
                .map(NewPileReturnOrder::getReturnItems)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
        Map<Long, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getDevanningId);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Long key = entry.getKey();
                            BigDecimal total = map.get(key);
                            Double returned = 0d;
                            if (groupMap.get(key) != null) {
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }
                            return total.subtract(BigDecimal.valueOf(returned));
                        },
                        (o, n) -> o.add(n)
                ));
    }

    /**
     * 订单中可退赠品的数量
     *
     * @param trade
     */
    public Map<String, BigDecimal> findLeftGiftItems(Trade trade) {
        if (CollectionUtils.isNotEmpty(trade.getGifts())) {
            Map<String, BigDecimal> map = trade.getGifts().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                    g -> BigDecimal.valueOf(g.getDeliveredNum())));
            List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream()
                    .map(NewPileReturnOrder::getReturnGifts)
                    .reduce(new ArrayList<>(), (a, b) -> {
                        a.addAll(b);
                        return a;
                    });
            Map<String, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getSkuId);
            return map.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                String key = entry.getKey();
                                BigDecimal total = map.get(key);
                                Double returned = 0.00;
                                if (groupMap.get(key) != null) {
                                    returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                                }
                                return total.subtract(BigDecimal.valueOf(returned));
                            }
                    ));
        }
        return new HashMap<>();
    }


    public void verifyNum(Trade trade, List<ReturnItem> returnItems) {
        Map<String, BigDecimal> map = this.findLeftItems(trade);
        Map<Long, BigDecimal> leftDevanningItemsMap = Maps.newHashMap();
        //拆箱处理
        ReturnItem returnItem = returnItems.stream().findFirst().orElse(null);
        if (Objects.nonNull(returnItem.getDevanningId())) {
            leftDevanningItemsMap = this.findLeftDevanningItems(trade);
        }
        Map<Long, BigDecimal> finalLeftDevanningItemsMap = leftDevanningItemsMap;
        returnItems.stream().forEach(
                t -> {
                    if (Objects.nonNull(t.getDevanningId())) {
                        if (finalLeftDevanningItemsMap.get(t.getDevanningId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                        }
                    } else {
                        if (map.get(t.getSkuId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                        }
                    }
                    if (t.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new SbcRuntimeException("K-050102");
                    }

                }
        );
    }

    public void verifyNumNewPile(NewPileTrade trade, List<ReturnItem> returnItems) {
        Map<String, BigDecimal> map = this.findLeftItemsNewPile(trade);
        Map<Long, BigDecimal> leftDevanningItemsMap = Maps.newHashMap();
        //拆箱处理
        ReturnItem returnItem = returnItems.stream().findFirst().orElse(null);
        if (Objects.nonNull(returnItem.getDevanningId())) {
            leftDevanningItemsMap = this.findLeftDevanningItemsNewPile(trade);
        }
        Map<Long, BigDecimal> finalLeftDevanningItemsMap = leftDevanningItemsMap;
        returnItems.stream().forEach(
                t -> {
                    if (Objects.nonNull(t.getDevanningId())) {
                        if (finalLeftDevanningItemsMap.get(t.getDevanningId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                        }
                    } else {
                        if (map.get(t.getSkuId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                        }
                    }
                    if (t.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new SbcRuntimeException("K-050102");
                    }

                }
        );
    }

    // 计算积分
    public Long getPointsNewPile(NewPileReturnOrder returnOrder, NewPileTrade trade) {
        // 各商品均摊积分
        Map<String, Double> splitPointMap = new HashMap<>();
        // 各商品购买数量
        Map<String, Long> totalNumMap = new HashMap<>();
        // 各商品消耗积分
        Map<String, Long> pointsMap = new HashMap<>();
        List<TradeItem> tradeItems = trade.getTradeItems();
        for (TradeItem tradeItem : tradeItems) {
            Double splitPoint = new BigDecimal(tradeItem.getPoints())
                    .divide(new BigDecimal(tradeItem.getNum()), 2, BigDecimal.ROUND_DOWN)
                    .doubleValue();
            String skuId = tradeItem.getSkuId();
            splitPointMap.put(skuId, splitPoint);
            totalNumMap.put(skuId, tradeItem.getNum());
            pointsMap.put(skuId, tradeItem.getPoints());
        }

        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        // 可退积分计算
        return returnItems.stream()
                .map(returnItem -> {
                    String skuId = returnItem.getSkuId();
                    Long shouldPoints;
                    if (returnItem.getNum().compareTo(returnItem.getCanReturnNum()) < 0) {
                        // 小于可退数量,直接均摊积分乘以数量
                        BigDecimal totalPoints = returnItem.getNum().multiply(BigDecimal.valueOf(splitPointMap.get(skuId)));
                        shouldPoints = totalPoints.longValue();
                    } else {
                        //大于等于可退数量 , 所用积分 - 已退积分(均摊积分*(购买数量-可退数量))
                        Double retiredPoints =
                                splitPointMap.get(skuId) * (totalNumMap.get(skuId) - returnItem.getCanReturnNum().longValue());
                        shouldPoints = pointsMap.get(skuId) - retiredPoints.longValue();
                    }
                    //设置单品应退积分
                    returnItem.setSplitPoint(shouldPoints);
                    return shouldPoints;
                })
                .reduce(0L, Long::sum);
    }

    // 计算积分
    public Long getPoints(ReturnOrder returnOrder, Trade trade) {
        // 各商品均摊积分
        Map<String, Double> splitPointMap = new HashMap<>();
        // 各商品购买数量
        Map<String, Long> totalNumMap = new HashMap<>();
        // 各商品消耗积分
        Map<String, Long> pointsMap = new HashMap<>();
        List<TradeItem> tradeItems = trade.getTradeItems();
        for (TradeItem tradeItem : tradeItems) {
            Double splitPoint = new BigDecimal(tradeItem.getPoints())
                    .divide(new BigDecimal(tradeItem.getNum()), 2, BigDecimal.ROUND_DOWN)
                    .doubleValue();
            String skuId = tradeItem.getSkuId();
            splitPointMap.put(skuId, splitPoint);
            totalNumMap.put(skuId, tradeItem.getNum());
            pointsMap.put(skuId, tradeItem.getPoints());
        }

        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        // 可退积分计算
        return returnItems.stream()
                .map(returnItem -> {
                    String skuId = returnItem.getSkuId();
                    Long shouldPoints;
                    if (returnItem.getNum().compareTo(returnItem.getCanReturnNum()) < 0) {
                        // 小于可退数量,直接均摊积分乘以数量
                        BigDecimal totalPoints = returnItem.getNum().multiply(BigDecimal.valueOf(splitPointMap.get(skuId)));
                        shouldPoints = totalPoints.longValue();
                    } else {
                        //大于等于可退数量 , 所用积分 - 已退积分(均摊积分*(购买数量-可退数量))
                        Double retiredPoints =
                                splitPointMap.get(skuId) * (totalNumMap.get(skuId) - returnItem.getCanReturnNum().longValue());
                        shouldPoints = pointsMap.get(skuId) - retiredPoints.longValue();
                    }
                    //设置单品应退积分
                    returnItem.setSplitPoint(shouldPoints);
                    return shouldPoints;
                })
                .reduce(0L, Long::sum);
    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     */
    public void createWMSRefund(ReturnOrder returnOrder, Operator operator, Trade trade) {

        //校验该订单关联的退款单状态
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(trade.getId());
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<ReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                            ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        // 新增订单日志
        //tradeService.returnOrder(returnOrder.getTid(), operator);
        returnOrder.setReturnType(ReturnType.REFUND);
        //设置退货商品
        returnOrder.getReturnItems().forEach(returnItem -> {

            //拆箱
            if (Objects.nonNull(returnItem.getDevanningId())) {
                for (TradeItem item : trade.getTradeItems()) {
                    if (item.getDevanningId().equals(returnItem.getDevanningId())) {
                        returnItem.setSkuId(item.getSkuId());
                        returnItem.setSkuNo(item.getSkuNo());
                        returnItem.setPic(item.getPic());
                        returnItem.setSkuName(item.getSkuName());
                        returnItem.setUnit(item.getUnit());
                        returnItem.setErpSkuNo(item.getErpSkuNo());
                        returnItem.setSpecDetails(item.getSpecDetails());
                        returnItem.setGoodsSubtitle(item.getGoodsSubtitle());
                    }
                }
            } else {
                //正常
                for (TradeItem item : trade.getTradeItems()) {
                    if (item.getSkuNo().equals(returnItem.getSkuNo())) {
                        returnItem.setSkuId(item.getSkuId());
                        returnItem.setSkuNo(item.getSkuNo());
                        returnItem.setPic(item.getPic());
                        returnItem.setSkuName(item.getSkuName());
                        returnItem.setUnit(item.getUnit());
                        returnItem.setErpSkuNo(item.getErpSkuNo());
                        returnItem.setSpecDetails(item.getSpecDetails());
                        returnItem.setGoodsSubtitle(item.getGoodsSubtitle());
                    }
                }
            }
        });
    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     */
    public void createWMSRefundNewPile(NewPileReturnOrder returnOrder, Operator operator, NewPileTrade trade) {

        //校验该订单关联的退款单状态
        List<NewPileReturnOrder> returnOrders = newPileReturnOrderRepository.findByTid(trade.getId());
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<NewPileReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == NewPileReturnFlowState.VOID))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        // 新增订单日志
        //tradeService.returnOrder(returnOrder.getTid(), operator);
        returnOrder.setReturnType(ReturnType.REFUND);
        //设置退货商品
        returnOrder.getReturnItems().forEach(returnItem -> {

            //拆箱
            if (Objects.nonNull(returnItem.getDevanningId())) {
                for (TradeItem item : trade.getTradeItems()) {
                    if (item.getDevanningId().equals(returnItem.getDevanningId())) {
                        returnItem.setSkuId(item.getSkuId());
                        returnItem.setSkuNo(item.getSkuNo());
                        returnItem.setPic(item.getPic());
                        returnItem.setSkuName(item.getSkuName());
                        returnItem.setUnit(item.getUnit());
                        returnItem.setErpSkuNo(item.getErpSkuNo());
                        returnItem.setSpecDetails(item.getSpecDetails());
                        returnItem.setGoodsSubtitle(item.getGoodsSubtitle());
                    }
                }
            } else {
                //正常
                for (TradeItem item : trade.getTradeItems()) {
                    if (item.getSkuNo().equals(returnItem.getSkuNo())) {
                        returnItem.setSkuId(item.getSkuId());
                        returnItem.setSkuNo(item.getSkuNo());
                        returnItem.setPic(item.getPic());
                        returnItem.setSkuName(item.getSkuName());
                        returnItem.setUnit(item.getUnit());
                        returnItem.setErpSkuNo(item.getErpSkuNo());
                        returnItem.setSpecDetails(item.getSpecDetails());
                        returnItem.setGoodsSubtitle(item.getGoodsSubtitle());
                    }
                }
            }
        });
    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     */
    public void createRefund(ReturnOrder returnOrder, Operator operator, Trade trade) {
        //校验该订单关联的退款单状态
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(trade.getId());
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<ReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                            ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }
        // 新增订单日志
        tradeService.returnOrder(returnOrder.getTid(), operator);
        returnOrder.setReturnType(ReturnType.REFUND);

        //设置退货商品
        returnOrder.setReturnItems(trade.getTradeItems().stream().map(item -> ReturnItem.builder()
                .num(BigDecimal.valueOf(item.getNum()))
                .skuId(item.getSkuId())
                .skuNo(item.getSkuNo())
                .devanningId(item.getDevanningId())
                .pic(item.getPic())
                .skuName(item.getSkuName())
                .unit(item.getUnit())
                .price(item.getPrice())
                .erpSkuNo(item.getErpSkuNo())
                .splitPrice(item.getSplitPrice())
                .specDetails(item.getSpecDetails())
                .goodsSubtitle(item.getGoodsSubtitle())
                .build()).collect(Collectors.toList()));

    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     * @param trade
     * @param detailSamountVOList
     */
    public void fillReturnItems(NewPileReturnOrder returnOrder, NewPileTrade trade, List<InventoryDetailSamountVO> detailSamountVOList) {
        //设置退货商品
        Map<String, BigDecimal> amountBySkuIdMap = detailSamountVOList.stream().collect(
                Collectors.groupingBy(InventoryDetailSamountVO::getGoodsInfoId,
                        Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));

        returnOrder.setReturnItems(trade.getTradeItems().stream().map(item -> ReturnItem.builder()
                .num(item.getCanReturnNum())
                .skuId(item.getSkuId())
                .skuNo(item.getSkuNo())
                .devanningId(item.getDevanningId())
                .pic(item.getPic())
                .skuName(item.getSkuName())
                .unit(item.getUnit())
                //成交价记录的是商品原价，修改
                .price(item.getSplitPrice().divide(BigDecimal.valueOf(item.getNum()), 2, RoundingMode.HALF_UP))
                .erpSkuNo(item.getErpSkuNo())
                .splitPrice(amountBySkuIdMap.get(item.getSkuId()))
                .specDetails(item.getSpecDetails())
                .goodsSubtitle(item.getGoodsSubtitle())
                .build()).collect(Collectors.toList()));

    }


    /**
     * 功能描述: <br>调用第三方WMS接口
     * 〈〉
     *
     * @Param: [returnOrder, warehouseId]
     * @Return: java.lang.Boolean
     * @Author:
     * @Date: 2020/5/19 15:12
     */
    public Boolean pushCancelOrder(ReturnOrder returnOrder, String warehouseId,Trade trade) {
        if(!orderCommonService.wmsCanTrade(trade)){
            return true;
        }
        BaseResponse<ResponseWMSReturnResponse> result = null;
        try {
            result = requestWMSOrderProvider.cancelOrder(WMSOrderCancelRequest.builder()
                    .docNo(returnOrder.getTid())
                    .customerId("XYY")
                    .orderType("XSCK")
                    .warehouseId(warehouseId)
                    .consigneeId(returnOrder.getBuyer().getCustomerErpId())
                    .erpCancelReason(returnOrder.getReturnReason().getDesc())
                    .build());
        } catch (Exception e) {
            if (e instanceof SbcRuntimeException) {
                SbcRuntimeException exception = (SbcRuntimeException) e;
                if ("K-050510".equals(exception.getErrorCode())) {
                    throw new SbcRuntimeException("K-050510");
                }
            }
            return false;
        }
        return !Objects.isNull(result) && !Objects.isNull(result.getContext()) &&
                !Objects.isNull(result.getContext().getResponseWMSReturnVO()) &&
                AbstractXYYConstant.RESPONSE_SUCCESS.equals(result.getContext().getResponseWMSReturnVO().getReturnCode());
    }


    /**
     * 退单通知节点发送MQ消息
     *
     * @param nodeType
     * @param processType
     * @param params
     * @param rid
     * @param customerId
     */
    public void sendNoticeMessage(NodeType nodeType, ReturnOrderProcessType processType, List<String> params, String rid, String customerId, String pic, String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", nodeType.toValue());
        map.put("node", processType.toValue());
        map.put("id", rid);
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeCode(processType.getType());
        messageMQRequest.setNodeType(nodeType.toValue());
        messageMQRequest.setParams(Lists.newArrayList(params));
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(customerId);
        messageMQRequest.setPic(pic);
        messageMQRequest.setMobile(mobile);
        newPileReturnOrderProducerService.sendMessage(messageMQRequest);
    }


    /**
     * 审核退单
     *
     * @param rid
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void auditNewPile(String rid, Operator operator) {
        //查询退单详情
        NewPileReturnOrder returnOrder = findById(rid);
        // 查询订单相关的所有退单
        List<NewPileReturnOrder> returnAllOrders = newPileReturnOrderRepository.findByTid(returnOrder.getTid());
        //查询相关订单信息
        NewPileTrade trade = newPileTradeService.detail(returnOrder.getTid());
        // 筛选出已完成的退单
        List<NewPileReturnOrder> returnOrders = returnAllOrders.stream().filter(allOrder -> allOrder.getReturnFlowState() ==
                NewPileReturnFlowState.COMPLETED)
                .collect(Collectors.toList());
        //计算所有已完成的退单总价格
        BigDecimal allOldPrice = new BigDecimal(0);
        for (NewPileReturnOrder order : returnOrders) {
            BigDecimal p = order.getReturnPrice().getApplyStatus() ? order.getReturnPrice().getApplyPrice() : order
                    .getReturnPrice().getTotalPrice();
            allOldPrice = allOldPrice.add(p);
        }
        ReturnPrice returnPrice = returnOrder.getReturnPrice();

        BigDecimal price = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
        //查询订单支付流水表
        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnOrder.getTid()).get();

        //提货代客退单审核不需要校验
        if (Objects.isNull(trade.getActivityType()) || !TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
            // 退单金额校验 退款金额不可大于可退金额
            if (payOrder.getPayType() == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                    == -1) {
                throw new SbcRuntimeException("K-050126");
            }
        }

        if (returnOrder.getReturnType() == ReturnType.REFUND) {
            refundOrderService.generateRefundOrderByReturnOrderCode(rid, returnOrder.getBuyer().getId(), price,
                    returnOrder.getPayType());
            //
        }
        //修改退单状态
        NewPileReturnStateRequest request = NewPileReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(NewPileReturnEvent.REFUND)
                .build();
        newPileReturnFSMService.changeState(request);
        //自动发货
        autoDeliver(rid, operator);

        //wms三方推送
        if (!kingdeeOpenState) {//判断是否开启新金蝶
            log.info("ReturnOrderService.audit  kingdeeOpenState:{}", kingdeeOpenState);
            if (wmsPushFlag) {
                Trade trades = tradeService.detail(returnOrder.getTid());
                //只有传false的时候不推送，空和true都需要推送
                if (Objects.isNull(returnOrder.getPushNeeded()) || returnOrder.getPushNeeded()) {
                    //推送退单
                    if (pushBackOrder(returnOrder, trades.getWareHouseCode(), trades)) {
                        returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                    } else {
                        throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO, "申请退货失败，WMS接口异常");
                    }
                }

            }
        }

        log.info("ReturnOrderService.audit returnOrder.getId:{}", returnOrder.getId());
        if (kingdeeOpenState) {//判断是否开启新金蝶
            if (pushAuditKingdee(returnOrder)) {
                returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
            }
//            else {
//                throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO, "申请退货失败");
//            }
        }

        //售后审核通过发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_CHECK_PASS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount());

    }

    public Boolean pushBackOrder(NewPileReturnOrder param, String warehouseId, Trade trade) {
        if(!orderCommonService.erpCanTrade(trade)){
            return true;
        }
        List<TradeItem> tradeItems = trade.getTradeItems();
        tradeItems.addAll(trade.getGifts());

        List<WMSChargeBackDetailsRequest> details = new ArrayList<>(20);
        AtomicInteger lineNo = new AtomicInteger(1);
        List<ReturnItem> returnItemList = new ArrayList<>();
        returnItemList.addAll(param.getReturnItems());
        returnItemList.addAll(param.getReturnGifts());
        returnItemList.forEach(returnItem -> {
            TradeItem tradeItemT = tradeItems.stream().filter(t -> t.getSkuId().equals(returnItem.getSkuId())).findFirst().orElse(null);
            //拆箱规格步长
            if (Objects.nonNull(returnItem.getDevanningId())) {
                tradeItemT = tradeItems.stream().filter(t -> t.getDevanningId().equals(returnItem.getDevanningId())).findFirst().orElse(null);
            }
            BigDecimal addStep =
                    tradeItemT != null && tradeItemT.getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP) != null ?
                            tradeItemT.getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP) :
                            returnItem.getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal returnItemPrice = returnItem.getPrice().divide(addStep, 2, BigDecimal.ROUND_HALF_UP);
            details.add(WMSChargeBackDetailsRequest.builder()
                    .sku(returnItem.getErpSkuNo())
                    .expectedQty(returnItem.getNum().multiply(addStep).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .totalPrice(returnItemPrice)
                    .dedi06(Objects.nonNull(returnItem.getDevanningId()) ? returnItem.getDevanningId().toString() : null)
                    .referenceNo(param.getId())
                    .lineNo(lineNo.get())
                    .build());
            lineNo.incrementAndGet();
        });
        BaseResponse<ResponseWMSReturnResponse> result = null;

        String jobNo = WmsErpIdConstants.ERP_CUSTOMER_ID;
        if (StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId())) {
            EmployeeByIdResponse response = employeeQueryProvider
                    .getById(EmployeeByIdRequest.builder().employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(response) && Objects.nonNull(response.getJobNo())) {
                jobNo = response.getJobNo();
            }
        }

        param.getBuyer().getEmployeeId();

        result = requestWMSOrderProvider.putASN(WMSChargeBackRequest.builder()
                .warehouseId(warehouseId)
                .customerId(AbstractXYYConstant.CUSTOMER_ID)
                .supplierId(trade.getBuyer().getCustomerErpId())
                .supplierName(param.getBuyer().getName())
                .soReferenceD(orderCommonService.getWmsSoReference4(trade))
                .asnType("XSTHRK")
                .userDefine1(jobNo)//业务员
                .userDefine2(jobNo)//制单人
                .userDefine3(trade.getSupplier().getStoreName())
                .asnReferenceC(orderCommonService.getWmsSoReference5(trade))
                .docNo(param.getId())
                .asnReferenceA(param.getTid())
                .asnCreationTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1))
                .details(details)
                .build());

        return !Objects.isNull(result) && !Objects.isNull(result.getContext()) &&
                !Objects.isNull(result.getContext().getResponseWMSReturnVO()) &&
                AbstractXYYConstant.RESPONSE_SUCCESS.equals(result.getContext().getResponseWMSReturnVO().getReturnCode()) &&
                result.getContext().getResponseWMSReturnVO().getReturnFlag() > 0;
    }

    public void autoDeliver(String rid, Operator operator) {
        ReturnStateRequest request;
        NewPileReturnOrder returnOrder = findById(rid);

        //非快递退回的退货单，审核通过后变更为已发货状态
//        if (returnOrder.getReturnType() == ReturnType.RETURN && returnOrder.getReturnWay() == ReturnWay.OTHER) {
//            request = ReturnStateRequest
//                    .builder()
//                    .rid(rid)
//                    .operator(operator)
//                    .returnEvent(ReturnEvent.DELIVER)
//                    .build();
//            newPileReturnFSMService.changeState(request);
//        }
    }

    /**
     * push退货单给金蝶
     */
    public Boolean pushAuditKingdee(NewPileReturnOrder returnOrder) {
        log.info("ReturnOrderService.pushAuditKingdee req Ptid:{} returnOrderId:{}", returnOrder.getTid(), returnOrder.getId());
        Integer number = tradePushKingdeeReturnGoodsRepository.selcetPushKingdeeReturnGoodsNumber(returnOrder.getId());
        TradePushKingdeeReturnGoods pushKingdeeReturnGoods = new TradePushKingdeeReturnGoods();
        pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Boolean result = false;
        try {
            if (!checkAuditKingdee(returnOrder, pushKingdeeReturnGoods)) {
                pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return result;
            }
            Trade trade = tradeService.detail(returnOrder.getTid());
            log.info("ReturnOrderService.pushAuditKingdee trade.getActivityType:{}", trade.getActivityType());
            List<PileStockRecordAttachment> recordAttachments = new ArrayList<>();
            if (Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
                recordAttachments.addAll(getReturnGoodsLinkedData(returnOrder));
            }
            if (trade != null) {
                addReturnOrderErp(returnOrder, trade);
            }
            KingdeeAuditOrder order = new KingdeeAuditOrder();
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(employeeResponse) || StringUtils.isNotEmpty(employeeResponse.getErpEmployeeId())) {
                Map fSalerId = new HashMap();
                fSalerId.put("FNumber", employeeResponse.getErpEmployeeId());
                order.setFSalerId(fSalerId);
            }
            order.setOrderNumber(returnOrder.getId());
            order.setFSaleNum(returnOrder.getTid());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
            order.setFRetcustId(FCustId);//客户
            order.setFDate(DateUtil.nowDate());
            Map FSaleOrgId = new HashMap();
            FSaleOrgId.put("FNumber", kingdeeOrganization);
            order.setFSaleOrgId(FSaleOrgId);//组织
            //仓库id
            Map FStockId = new HashMap();
            if ("WH01".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
            } else {
                FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
            }
            order.setFStockId(FStockId);
            //退货备注
            if (Objects.nonNull(returnOrder.getReturnReason())) {
                order.setFNote(returnOrder.getReturnReason().getDesc());
            }
            List<AuditOrderEntry> fSaleOrderEntry = new ArrayList<>();
            //购买商品
            if (returnOrder.getReturnItems().size() > 0) {
                Map<String, BigDecimal> returnGoodsMap = new HashMap();

                List<ReturnItem> returnItems = returnOrder.getReturnItems();//购买商品
                //赠送商品
                List<ReturnItem> returnGifts = returnOrder.getReturnGifts();
                if (CollectionUtils.isNotEmpty(returnGifts) && returnGifts.size() > 0) {
                    returnGifts.forEach(returnItem -> {
                        returnItem.setFIsFree(true);
                        returnItems.add(returnItem);
                    });
                }
//                Iterator it = returnItems.iterator();
//                while (it.hasNext()) {
//                    log.info("ReturnOrderService.pushAuditKingdee 合并增送商品的集合"+it.next().toString());
//                }

                for (ReturnItem item : returnItems) {
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    if (Objects.isNull(prefix)) {
                        //零售仓库，默认001
                        prefix = "001-";
                    }
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix, ""));
                    if (!checkAuditKingdeeGoods(item, pushKingdeeReturnGoods)) {
                        pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return result;
                    }
                    if (CollectionUtils.isNotEmpty(recordAttachments)) {
                        AuditOrderEntry orderEntry = new AuditOrderEntry();
                        for (PileStockRecordAttachment stockRecordAttachment : recordAttachments) {
                            BigDecimal num = returnGoodsMap.get(item.getSkuId());
                            if (num == null) {
                                num = BigDecimal.ZERO;
                            }
                            if (item.getSkuId().equals(stockRecordAttachment.getSkuId()) && item.getNum().compareTo(num) == 1) {
                                AuditOrderEntry associatedOrderEntry = new AuditOrderEntry();
                                //还需要推多少商品
                                BigDecimal useNum = item.getNum().subtract(num);
                                //关联数据量是否大于还需要推多少商品
                                if ((BigDecimal.valueOf(stockRecordAttachment.getNum()).subtract(useNum)).compareTo(BigDecimal.ZERO) == 1) {
                                    associatedOrderEntry.setFRealQty(useNum);
                                    returnGoodsMap.put(item.getSkuId(), useNum.add(num));
                                } else {
                                    associatedOrderEntry.setFRealQty(BigDecimal.valueOf(stockRecordAttachment.getNum()));
                                    returnGoodsMap.put(item.getSkuId(), BigDecimal.valueOf(stockRecordAttachment.getNum()).add(num));
                                }
                                Map FMaterialId = new HashMap();
                                FMaterialId.put("FNumber", item.getErpSkuNo());
                                associatedOrderEntry.setFMaterialId(FMaterialId);
                                Map FSStockId = new HashMap();
                                if (Objects.nonNull(item.getGoodsInfoType()) && 1 == item.getGoodsInfoType()) {
                                    FSStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                                } else {
//                                    FSStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                                    if ("WH01".equals(trade.getWareHouseCode())) {
                                        FSStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                                    } else {
                                        FSStockId.put("FNumber", ERPWMSConstants.SUB_WH);
                                    }
                                }
                                associatedOrderEntry.setFStockId(FSStockId);//仓库
                                if (Objects.nonNull(item.getGoodsBatchNo())) {
                                    Map FLot = new HashMap();
                                    FLot.put("FNumber", item.getGoodsBatchNo());
                                    associatedOrderEntry.setFLot(FLot);
                                    associatedOrderEntry.setFProduceDate(item.getGoodsBatchNo());
                                }
                                associatedOrderEntry.setFTaxPrice(stockRecordAttachment.getPrice());
                                associatedOrderEntry.setF_According(stockRecordAttachment.getOrderCode());
                                //是否为赠品
                                associatedOrderEntry.setFIsFree(item.getFIsFree());
                                log.info("ReturnOrderService.pushAuditKingdee returnGoodsMap:{}", returnGoodsMap.get(item.getSkuId()), returnOrder.getId());
                                fSaleOrderEntry.add(associatedOrderEntry);
                                log.info("ReturnOrderService.pushAuditKingdee returnGoodsEntity:{}" + associatedOrderEntry.toString());
                            }
                        }
                    } else {
                        AuditOrderEntry orderEntry = new AuditOrderEntry();
                        //没有关联数据
                        Map FMaterialId = new HashMap();
                        FMaterialId.put("FNumber", item.getErpSkuNo());
                        orderEntry.setFMaterialId(FMaterialId);
                        orderEntry.setFRealQty(item.getNum());
                        Map FSStockId = new HashMap();
                        if (Objects.nonNull(item.getGoodsInfoType()) && 1 == item.getGoodsInfoType()) {
                            FSStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                        } else {
//                            FSStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                            if ("WH01".equals(trade.getWareHouseCode())) {
                                FSStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                            } else {
                                FSStockId.put("FNumber", ERPWMSConstants.SUB_WH);
                            }
                        }
                        orderEntry.setFStockId(FSStockId);//仓库
                        if (Objects.nonNull(item.getGoodsBatchNo())) {
                            Map FLot = new HashMap();
                            FLot.put("FNumber", item.getGoodsBatchNo());
                            orderEntry.setFLot(FLot);
                            orderEntry.setFProduceDate(item.getGoodsBatchNo());
                        }
                        //设置拆箱主键
                        if (Objects.nonNull(item.getDevanningId())) {
                            orderEntry.setFSkucxId(item.getDevanningId().toString());
                        }
                        //是否为赠品
                        orderEntry.setFIsFree(item.getFIsFree());
                        orderEntry.setFTaxPrice(item.getPrice());
                        fSaleOrderEntry.add(orderEntry);
                    }
                }
            }


            order.setFEntity(fSaleOrderEntry);
//                Iterator it = fSaleOrderEntry.iterator();
//                while (it.hasNext()) {
//                    log.info("金蝶推送实体"+it.next().toString());
//                }


            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", order);
//                log.info("进入推送阶段============");
                HttpCommonResult result1 = HttpCommonUtil.postHeader(returnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                log.info("ReturnOrderService.pushAuditKingdee result1:{}", result1.getResultData());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    result = true;
                } else {
                    pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                    result = false;
                }
                pushKingdeeReturnGoods.setInstructions(result1.getResultData());
            } else {
                log.error("ReturnOrderService.pushAuditKingdee push kingdee error");
                String res = "金蝶登录失败";
                pushKingdeeReturnGoods.setInstructions(res);
                pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                result = false;
            }
        } catch (Exception e) {
            log.error("ReturnOrderService.pushAuditKingdee error:{}", e);
            String res = "金蝶推送失败";
            pushKingdeeReturnGoods.setInstructions(res);
            pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return result;
        } finally {
            if (number == 0) {
                pushKingdeeReturnGoods.setReturnGoodsCode(returnOrder.getId());
                pushKingdeeReturnGoods.setOrderCode(returnOrder.getTid());
                pushKingdeeReturnGoods.setCreateTime(LocalDateTime.now());
                pushKingdeeReturnGoods.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeReturnGoodsRepository.saveAndFlush(pushKingdeeReturnGoods);
            } else {
                pushKingdeeReturnGoods.setUpdateTime(LocalDateTime.now());
                pushKingdeeReturnGoods.setReturnGoodsCode(returnOrder.getId());
                tradePushKingdeeReturnGoodsRepository.updatePushKingdeeReturnGoodsState(pushKingdeeReturnGoods);
            }
            return result;
        }
    }

    /**
     * 查询退货需要的关联数据
     *
     * @param returnOrder
     * @return
     */
    public List<PileStockRecordAttachment> getReturnGoodsLinkedData(NewPileReturnOrder returnOrder) {
        //查询关联表中的数据
        List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<String> gitftskuIds = returnOrder.getReturnGifts().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        Iterator it = gitftskuIds.iterator();
        while (it.hasNext()) {
            skuIds.add((String) it.next());
        }

        List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordTidAndSkuid(returnOrder.getTid(), skuIds);
        return stockRecordAttachmentList;
    }

    /**
     * 校验推送表体参数
     *
     * @param item
     * @param pushKingdeeReturnGoods
     * @return
     */
    public Boolean checkAuditKingdeeGoods(ReturnItem item, TradePushKingdeeReturnGoods pushKingdeeReturnGoods) {
        if (StringUtils.isEmpty(item.getErpSkuNo())) {
            log.info("ReturnOrderService.pushAuditKingdee Lack ErpSkuNo ");
            pushKingdeeReturnGoods.setInstructions("Lack ErpSkuNo");
            return false;
        }
        if (item.getNum() == null) {
            log.info("ReturnOrderService.pushAuditKingdee Lack Num ");
            pushKingdeeReturnGoods.setInstructions("Lack Num");
            return false;
        }
        if (item.getGoodsInfoType() == null) {
            log.info("ReturnOrderService.pushAuditKingdee Lack GoodsInfoType");
            pushKingdeeReturnGoods.setInstructions("Lack GoodsInfoType");
            return false;
        }
//        if (StringUtils.isEmpty(item.getGoodsBatchNo())){
//            logger.info("ReturnOrderService.pushAuditKingdee Lack GoodsBatchNo");
//            pushKingdeeReturnGoods.setInstructions("Lack GoodsBatchNo");
//            return false;
//        }
        return true;
    }

    /**
     * 校验推送头部参数
     *
     * @param returnOrder
     * @return
     */
    public Boolean checkAuditKingdee(NewPileReturnOrder returnOrder, TradePushKingdeeReturnGoods pushKingdeeReturnGoods) {
        if (StringUtils.isEmpty(returnOrder.getTid())) {
            log.info("ReturnOrderService.pushAuditKingdee Lack Tid");
            pushKingdeeReturnGoods.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getBuyer().getAccount())) {
            log.info("ReturnOrderService.pushAuditKingdee Lack FCustId");
            pushKingdeeReturnGoods.setInstructions("Lack FCustId");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getId())) {
            log.info("ReturnOrderService.pushAuditKingdee Lack id");
            pushKingdeeReturnGoods.setInstructions("Lack id");
            return false;
        }
        return true;
    }

    /**
     * 给退货单添加仓库信息
     *
     * @param returnOrder
     * @param trade
     */
    public void addReturnOrderErp(NewPileReturnOrder returnOrder, Trade trade) {
        if (returnOrder.getReturnItems().size() > 0 && trade.getTradeItems().size() > 0) {
            for (ReturnItem returnItem : returnOrder.getReturnItems()) {

                //补全信息
                if (Objects.nonNull(returnItem.getDevanningId())) {
                    TradeItem tradeItem = trade.getTradeItems().stream().filter(t -> t.getDevanningId().equals(returnItem.getDevanningId())).findFirst().orElse(null);
                    returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
                    returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                    returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
                    //查询拆箱商品
                    DevanningGoodsInfoByIdResponse context = devanningGoodsInfoQueryProvider.getInfoById(DevanningGoodsInfoByIdRequest
                            .builder()
                            .devanningId(tradeItem.getDevanningId())
                            .build())
                            .getContext();
                    if (Objects.nonNull(context) && Objects.nonNull(context.getDevanningGoodsInfoVO())) {

                    }
                    returnItem.setNum(returnItem.getNum().multiply(context.getDevanningGoodsInfoVO().getDivisorFlag()));
                } else {
                    TradeItem tradeItem = trade.getTradeItems().stream().filter(t -> t.getSkuId().equals(returnItem.getSkuId())).findFirst().orElse(null);
                    returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
                    returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                    returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
                }

//                for (TradeItem tradeItem : trade.getTradeItems()) {
//                    if (returnItem.getSkuId().equals(tradeItem.getSkuId())) {
//                        returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
//                        returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
//                        returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
//                    }
//                }
            }
        }


        if (returnOrder.getReturnGifts().size() > 0 && trade.getGifts().size() > 0) {
            for (ReturnItem returnItem : returnOrder.getReturnGifts()) {
                for (TradeItem tradeItem : trade.getGifts()) {
                    if (returnItem.getSkuId().equals(tradeItem.getSkuId())) {
                        returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
                        returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                        returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
                    }
                }
            }
        }
    }

    /**
     * 不满足满赠条件时,需要退的所有赠品
     *
     * @param giftMarketing  某个满赠营销活动
     * @param allReturnGifts 不满足满赠条件,满赠营销活动中所有需要退的赠品信息  (可能需要退)
     * @param giftItemMap    赠品具体信息Map(获取除了skuId以外的详细信息)
     */
    public void setReturnGiftsMap(TradeMarketingVO giftMarketing, Map<String, ReturnItem> allReturnGifts, Map<String, TradeItem> giftItemMap) {
        // 不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中
        giftMarketing.getGiftLevel().getFullGiftDetailList().stream().forEach(gift -> {
            ReturnItem currGiftItem = allReturnGifts.get(gift.getProductId());
            TradeItem giftDetail = giftItemMap.get(gift.getProductId());
            if (currGiftItem == null) {
                if (Objects.nonNull(giftDetail)) {
                    allReturnGifts.put(gift.getProductId(), ReturnItem.builder().skuId(giftDetail.getSkuId()).num(BigDecimal.valueOf(gift
                            .getProductNum()))
                            .skuName(giftDetail.getSkuName())
                            .skuNo(giftDetail.getSkuNo())
                            .pic(giftDetail.getPic())
                            .price(giftDetail.getPrice())
                            .erpSkuNo(giftDetail.getErpSkuNo())
                            .specDetails(giftDetail.getSpecDetails()).unit(giftDetail.getUnit()).build());
                }
            } else {
                currGiftItem.setNum(currGiftItem.getNum().add(BigDecimal.valueOf(gift.getProductNum())));
            }
        });
    }

    /**
     * 查询拆箱订单详情,如已发货则带出可退商品数
     *
     * @param tid
     * @return
     */
    public NewPileTrade queryCanReturnDevanningItemNumByTid(String tid) {
        NewPileTrade trade = newPileTradeService.detail(tid);
        //校验支付状态trade
        PayOrderResponse payOrder = payOrderService.findPayOrder(trade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }

        List<GoodsPickStock> goodsPickStockByNewPileTradeNo = goodsPickStockRepository.getGoodsPickStockByNewPileTradeNo(tid);
        if (CollectionUtils.isEmpty(goodsPickStockByNewPileTradeNo)) {
            throw new SbcRuntimeException("K-050120", "该囤货单无商品可退");
        }
        Map<String, Long> stockMap = goodsPickStockByNewPileTradeNo.stream().filter(
                v -> {
                    if (v.getStock() > 0) {
                        return true;
                    }
                    return false;
                }
        ).collect(Collectors.toMap(GoodsPickStock::getGoodsInfoId, GoodsPickStock::getStock, (a, b) -> a));

        trade.getTradeItems().forEach(item -> {
            if (Objects.nonNull(stockMap.get(item.getSkuId()))) {
                item.setCanReturnNum(BigDecimal.valueOf(stockMap.get(item.getSkuId())));
            } else {
                item.setCanReturnNum(BigDecimal.ZERO);
            }
        });

        //过滤可退商品为0的商品
        List<TradeItem> tradeItemList = trade.getTradeItems().stream().filter(t -> BigDecimal.ZERO.compareTo(t.getCanReturnNum()) == NumberUtils.INTEGER_MINUS_ONE).collect(Collectors.toList());

        trade.setTradeItems(tradeItemList);

//        List<NewPileReturnOrder> returnsNotVoid = this.findReturnsNotVoid(tid);
//
//        // 可退金额
//        BigDecimal totalPrice = trade.getTradeItems().stream()
//                .map(TradeItem::getSplitPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal retiredPrice = returnsNotVoid.stream()
//                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
//                .map(o -> o.getReturnPrice().getActualReturnPrice())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
//        trade.setCanReturnPrice(canReturnPrice);


        InventoryDetailSamountResponse context = inventoryDetailSamountProvider.getNoTiinventoryDetailSamount
                (InventoryDetailSamountRequest.builder()
                        .goodsInfoIds(trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList()))
                        .oid(trade.getId()).build()).getContext();
        Map<Integer, List<InventoryDetailSamountVO>> collect = context.getInventoryDetailSamountVOS().stream().collect(Collectors.groupingBy(InventoryDetailSamountVO::getMoneyType));

        for (Map.Entry<Integer, List<InventoryDetailSamountVO>> a : collect.entrySet()) {
            BigDecimal reduce = a.getValue().stream()
                    .map(InventoryDetailSamountVO::getAmortizedExpenses)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (a.getKey() == 1) {
                trade.setCanReturnPrice(reduce);
            } else {
                trade.setCanReturnBalance(reduce);
            }

        }
        return trade;
    }

    /**
     * 创建退货单
     *
     * @param returnOrder
     */
    public void createReturn(ReturnOrder returnOrder, Operator operator, Trade trade) {

        // 新增订单日志
        tradeService.returnOrder(returnOrder.getTid(), operator);

        this.verifyNum(trade, returnOrder.getReturnItems());

        returnOrder.setReturnType(ReturnType.RETURN);

        //填充退货商品信息
        Map<String, BigDecimal> map = this.findLeftItems(trade);
        Map<Long, BigDecimal> devanningMap = Maps.newHashMap();
        //拆箱处理
        TradeItem devanTradeItem = trade.getTradeItems().stream().findAny().orElse(null);
        if (Objects.nonNull(devanTradeItem.getDevanningId())) {
            devanningMap = this.findLeftDevanningItems(trade);
        }
//        Map<Long, BigDecimal>devanningMap = findLeftDevanningItems(trade);
        Map<Long, BigDecimal> finalDevanningMap = devanningMap;
        returnOrder.getReturnItems().forEach(item ->
                {
                    List<TradeItem> tradeItems = trade.skuItemMap().get(item.getSkuId());
                    if (Objects.nonNull(item.getDevanningId())) {
                        tradeItems = trade.skuDevanningItemMap().get(item.getDevanningId());
                    }
                    item.setSkuName(tradeItems.stream().findAny().get().getSkuName());
                    item.setPic(tradeItems.stream().findAny().get().getPic());
                    item.setSkuNo(tradeItems.stream().findAny().get().getSkuNo());
                    item.setSpecDetails(tradeItems.stream().findAny().get().getSpecDetails());
                    item.setUnit(tradeItems.stream().findAny().get().getUnit());

                    for (TradeItem tradeItem : tradeItems) {
                        if (Objects.nonNull(item.getDevanningId()) && Objects.nonNull(tradeItem.getDevanningId())
                                && item.getDevanningId() == tradeItem.getDevanningId()) {
                            item.setGoodsSubtitle(tradeItem.getGoodsSubtitle());
                        } else {
                            item.setGoodsSubtitle(tradeItem.getGoodsSubtitle());
                        }
                    }
                    item.setCanReturnNum(map.get(item.getSkuId()));
                    if (Objects.nonNull(item.getDevanningId())) {
                        item.setCanReturnNum(finalDevanningMap.get(item.getDevanningId()));
                    }
                }
        );
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param returnOrder
     */
    @MongoRollback(persistence = NewPileReturnOrder.class, operation = Operation.UPDATE)
    public void updateReturnOrder(NewPileReturnOrder returnOrder) {
        newPileReturnOrderRepository.save(returnOrder);
    }

    @Autowired
    private CustomerAccountQueryProvider customerAccountQueryProvider;

    @Transactional
    @LcnTransaction
    public void offlineRefundForSupplier(String rid, CustomerAccountVO customerAccount, RefundBill refundBill, Operator operator) {
        log.info("NewPileReturnOrderService.offlineRefundForSupplier rid:{}", rid);
        NewPileReturnOrder returnPileOrder = newPileReturnOrderQuery.findByIdNewPile(rid);
        NewPileTrade pileTrade = newPileTradeService.detail(returnPileOrder.getTid());

        if (!NewPileReturnFlowState.AUDIT.equals(returnPileOrder.getReturnFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单状态已变化，请核实！");
        }

        if (Objects.isNull(pileTrade)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单不存在！");
        }

        if (NewPileFlowState.VOID.equals(pileTrade.getTradeState().getFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单已作废，不可退款，请核实！");
        }

        // 积分信息
        if (Objects.isNull(returnPileOrder.getReturnPoints())) {
            returnPileOrder.setReturnPoints(new ReturnPoints());
        }
        returnPileOrder.getReturnPoints().setActualPoints(refundBill.getActualReturnPoints());

        // 退货金额
        if (refundBill.getActualReturnPrice().compareTo(returnPileOrder.getReturnPrice().getApplyPrice()) == -1) {
            returnPileOrder.getReturnPrice().setApplyStatus(true);
            returnPileOrder.getReturnPrice().setApplyPrice(refundBill.getActualReturnPrice());
        }
        // 如果customerAccount非空，临时账号，当快照冗余在退单
        if (Objects.nonNull(customerAccount)) {
            // 客户编号
            customerAccount.setCustomerId(returnPileOrder.getBuyer().getId());
            returnPileOrder.setCustomerAccount(customerAccount);
        } else {
            //客户账号冗余至退单
            CustomerAccountOptionalRequest customerAccountOptionalRequest = new CustomerAccountOptionalRequest();
            customerAccountOptionalRequest.setCustomerAccountId(refundBill.getCustomerAccountId());
            BaseResponse<CustomerAccountOptionalResponse> customerAccountOptionalResponseBaseResponse =
                    customerAccountQueryProvider.getByCustomerAccountIdAndDelFlag(customerAccountOptionalRequest);
            CustomerAccountOptionalResponse customerAccountOptionalResponse =
                    customerAccountOptionalResponseBaseResponse.getContext();
            if (Objects.nonNull(customerAccountOptionalResponse)) {
                customerAccount = new CustomerAccountVO();
                KsBeanUtil.copyPropertiesThird(customerAccountOptionalResponse, customerAccount);
                returnPileOrder.setCustomerAccount(customerAccount);
            } else {
                throw new SbcRuntimeException("K-070009");
            }
        }

        // 根据退款单号查询财务退款单据的编号
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        if (refundOrder.getRefundStatus().equals(RefundStatus.APPLY) || returnPileOrder.getReturnFlowState().equals
                (ReturnFlowState.REJECT_REFUND)) {
            throw new SbcRuntimeException("K-050002", new Object[]{"退款"});
        }
        if (!Objects.isNull(refundOrder.getRefundBill())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        refundBill.setRefundId(refundOrder.getRefundId());

        // 生成退款记录
        refundBillService.save(refundBill);
        returnPileOrder.appendReturnEventLog(
                ReturnEventLog.builder()
                        .operator(operator)
                        .eventType(ReturnEvent.REFUND.getDesc())
                        .eventTime(LocalDateTime.now())
                        .eventDetail(String.format("退单[%s]已添加线下退款单，操作人:%s", returnPileOrder.getId(), operator.getName()))
                        .build()
        );
        refundOrder.setRefundStatus(RefundStatus.APPLY);
        refundOrderRepository.save(refundOrder);
        //修改囤货退单 标记为财务退款阶段
        returnPileOrder.setFinancialRefundFlag(true);
        this.updateReturnOrder(returnPileOrder);
    }


    /**
     * 根据退单编号查询退款单
     *
     * @param returnOrderCode 退单编号
     * @return 退款单信息
     */
    public RefundOrderResponse findByReturnOrderNoNewPile(String returnOrderCode) {
        return generateRefundOrderResponse(refundOrderService.findRefundOrderByReturnOrderNo(returnOrderCode));
    }

    /**
     * 根据 RefundOrder 生成 RefundOrderResponse 对象
     *
     * @param refundOrder refundOrder
     * @return new RefundOrderResponse()
     */
    private RefundOrderResponse generateRefundOrderResponse(RefundOrder refundOrder) {
        RefundOrderResponse refundOrderResponse = new RefundOrderResponse();
        BeanUtils.copyProperties(refundOrder, refundOrderResponse);

        NewPileReturnOrder returnOrder = findById(refundOrder.getReturnOrderCode());
        NewPileTrade trade = newPileTradeService.detail(returnOrder.getTid());

        refundOrderResponse.setActivityType(trade.getActivityType());
        //填充退单来源
        refundOrderResponse.setPlatform(returnOrder.getPlatform());
        Buyer buyer = returnOrder.getBuyer();
        CustomerDetailVO customerDetailVO = new CustomerDetailVO();
        customerDetailVO.setCustomerId(buyer.getId());
        customerDetailVO.setCustomerName(buyer.getName());
        if (Objects.nonNull(customerDetailVO)) {
            refundOrderResponse.setCustomerName(customerDetailVO.getCustomerName());
            refundOrderResponse.setCustomerId(customerDetailVO.getCustomerId());
        }

        CompanyInfoVO companyInfo = null;
        if (Objects.nonNull(refundOrder.getSupplierId())) {
            companyInfo = customerCommonService.getCompanyInfoById(refundOrder.getSupplierId());
        }
        if (Objects.nonNull(companyInfo)) {
            refundOrderResponse.setSupplierName(companyInfo.getSupplierName());
            if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                StoreVO store = companyInfo.getStoreVOList().get(0);
                refundOrderResponse.setStoreId(store.getStoreId());
            }
        }

        if (Objects.nonNull(refundOrder.getRefundBill()) && DeleteFlag.NO.equals(refundOrder.getRefundBill().getDelFlag())) {
            //从退单冗余信息中获取用户账户信息(防止用户修改后,查询的不是当时退单的账户信息)
            ReturnOrder returnOrderfalg = returnOrderRepository.findById(refundOrder.getReturnOrderCode()).orElse(null);

            if (returnOrderfalg != null && returnOrder.getCustomerAccount() != null) {
                log.info("客户账户信息customerAccount: {}", returnOrder.getCustomerAccount());
                refundOrderResponse.setCustomerAccountName(returnOrder.getCustomerAccount().getCustomerBankName() + "" +
                        " " + (
                        StringUtils.isNotBlank(returnOrder.getCustomerAccount().getCustomerAccountNo()) ?
                                ReturnOrderService.getDexAccount(returnOrder.getCustomerAccount().getCustomerAccountNo()) : ""
                ));
            }

            refundOrderResponse.setActualReturnPrice(refundOrder.getRefundBill().getActualReturnPrice());
            refundOrderResponse.setActualReturnPoints(refundOrder.getRefundBill().getActualReturnPoints());
            refundOrderResponse.setReturnAccount(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setOfflineAccountId(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setComment(refundOrder.getRefundBill().getRefundComment());
            refundOrderResponse.setRefundBillCode(refundOrder.getRefundBill().getRefundBillCode());
            refundOrderResponse.setReturnAccountName(parseAccount(refundOrder));
            // 退款时间以boss端审核时间为准
            if (Objects.equals(RefundStatus.FINISH, refundOrder.getRefundStatus())) {
                refundOrderResponse.setRefundBillTime(refundOrder.getRefundBill().getCreateTime());
            }
            refundOrderResponse.setPayChannel(refundOrder.getRefundBill().getPayChannel());
            refundOrderResponse.setPayChannelId(refundOrder.getRefundBill().getPayChannelId());
        }
        return refundOrderResponse;
    }

    public void doRefreshReturnOrder(RefreshReturnedOrderRequest request) {
        //提货退款单：未作废？（占用分摊金额），已完成（标记为退款完成）
        processNewPickReturnTrade(request);
        //囤货退款单： 未作废？（占用分摊金额），已完成（标记为退款完成）
        processNewPileReturnTrade(request);
    }

    private void processNewPickReturnTrade(RefreshReturnedOrderRequest request) {
        //影响表：returnOrder,inventoryDetailSamount

        //查找所有OPK提货订单对应的提货退单 且未作废的
        List<ReturnOrder> all;
        if (CollectionUtils.isNotEmpty(request.getNewPickReturnIds())) {
            all = returnOrderRepository.findByIdInAndReturnFlowStateNot(request.getNewPickReturnIds(), ReturnFlowState.VOID);
        } else {
            all = returnOrderRepository.findByTidStartingWithAndReturnFlowStateNot("OPK", ReturnFlowState.VOID);
        }
        log.info("所有待处理的新提货退单数：{}", all.size());

        if(CollectionUtils.isEmpty(all)){
            return;
        }

        Map<String,String> errMap = new LinkedHashMap<>();
        Map<String,String> successMap = new LinkedHashMap<>();
        for (ReturnOrder returnOrder : all) {
            //更新分摊金额
            List<InventoryDetailSamount> oldAssignedAmountList = inventoryDetailSamountService.getInventoryByReturnId(returnOrder.getId());
            if (CollectionUtils.isNotEmpty(oldAssignedAmountList)) {
                log.info("已分配过分摊金额，不再处理：退单号：{}", returnOrder.getId());
                continue;
            }

            //获取金额分布,过滤出未被其他退单占用的金额
            List<InventoryDetailSamountVO> unassignedAmountList = getUnassignedAmountListByTakeId(returnOrder.getTid());
            //分配分摊金额
            List<InventoryDetailSamountVO> assignedAmountList;
            try {
                assignedAmountList = assignReturnAmountForRefreshData(returnOrder, unassignedAmountList);
            } catch (Exception ex) {
                errMap.put(returnOrder.getId(), ex.getMessage());
                continue;
            }
            log.info("囤货提货退款金额 assignedAmountList size：{}",assignedAmountList.size());

            if (CollectionUtils.isEmpty(assignedAmountList)) {
                errMap.put(returnOrder.getId(), "assignedAmountList为空");
                continue;
            }

            ReturnPrice returnPrice = returnOrder.getReturnPrice();
            //改价金额维持原来数据不变，先备份
            BigDecimal actualReturnPrice = returnPrice.getActualReturnPrice();
            BigDecimal applyPrice = returnPrice.getApplyPrice();
            Boolean applyStatus = returnPrice.getApplyStatus();
            BigDecimal actualBalanceReturnPrice = returnPrice.getActualBalanceReturnPrice();

            //写入退单总金额
            Trade trade = tradeService.detail(returnOrder.getTid());
            fillReturnPrice(assignedAmountList, returnOrder, trade);

            //改价金额恢复
            returnPrice.setActualReturnPrice(actualReturnPrice);
            returnPrice.setApplyPrice(applyPrice);
            returnPrice.setApplyStatus(applyStatus);
            returnPrice.setActualBalanceReturnPrice(actualBalanceReturnPrice);

            //写入退货商品来源囤货单信息
            fillReturnGoodsList(assignedAmountList,returnOrder);

            returnOrderService.updateReturnOrder(returnOrder);

            //更新分摊金额：记录提货退单号，退单类型为提货退，已退标记为 未退完
            int returnFlag = 0;
            if (ReturnFlowState.COMPLETED.equals(returnOrder.getReturnFlowState())) {
                returnFlag = 1;
            }
            inventoryDetailSamountService.updateNewPickReturnAmount(returnOrder.getId(), assignedAmountList, returnFlag);
            successMap.put(returnOrder.getId(), returnOrder.getTid());
        }

        log.info("更新异常的退单号：{}", JSON.toJSONString(errMap));
        log.info("更新正常的退单号：{}", JSON.toJSONString(successMap));
    }

    private List<InventoryDetailSamountVO> assignReturnAmountForRefreshData(ReturnOrder returnOrder, List<InventoryDetailSamountVO> unassignedAmountList) {
        List<Comparator<? super InventoryDetailSamountVO>> comparators = new ArrayList<>();
        comparators.add(null);
        comparators.add(Comparator.comparing(InventoryDetailSamountVO::getGoodsInfoId)
                .thenComparing(InventoryDetailSamountVO::getAmortizedExpenses, Comparator.reverseOrder()));
        comparators.add(Comparator.comparing(InventoryDetailSamountVO::getGoodsInfoId)
                .thenComparing(InventoryDetailSamountVO::getAmortizedExpenses));

        BigDecimal returnPriceAssigned;
        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        log.info("assignReturnAmountForRefreshData returnPrice {}", returnPrice);

        BigDecimal expectPrice = returnPrice.getTotalPrice();
        List<InventoryDetailSamountVO> assignedAmountList;
        for (int i = 0; i < comparators.size(); i++) {
            assignedAmountList = assignReturnAmount(returnOrder, unassignedAmountList, comparators.get(i));
            returnPriceAssigned = assignedAmountList.stream().map(InventoryDetailSamountVO::getAmortizedExpenses)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (returnPriceAssigned.compareTo(expectPrice) != 0) {
                log.info("assignReturnAmountForRefreshData 分配金额不匹配：期望 {}，实际 {}", expectPrice, returnPriceAssigned);
                continue;
            }
            return assignedAmountList;
        }
        return Collections.emptyList();
    }


    private void processNewPileReturnTrade(RefreshReturnedOrderRequest request) {
        //影响表：newPileReturnOrder,inventoryDetailSamount

        List<NewPileReturnOrder> all;
        if (CollectionUtils.isNotEmpty(request.getNewPileReturnIds())) {
            all = newPileReturnOrderRepository.findByIdInAndReturnFlowStateNot(request.getNewPileReturnIds(), NewPileReturnFlowState.VOID);
        } else {
            all = newPileReturnOrderRepository.findAllByReturnFlowStateNot(NewPileReturnFlowState.VOID);
        }
        log.info("所有待处理的囤货退单数：{}", all.size());

        if(CollectionUtils.isEmpty(all)){
            return;
        }

        Map<String,String> successMap = new LinkedHashMap<>();
        Map<String,String> errorMap = new LinkedHashMap<>();

        for (NewPileReturnOrder returnOrder : all) {
            String returnOrderId = returnOrder.getId();
            //未作废,（占用分摊金额）;
            //查找未提货的囤货分摊金额
            List<InventoryDetailSamount> assignedList = inventoryDetailSamountService.getInventoryByOId(returnOrder.getTid())
                    .stream().filter(item -> Objects.equals(item.getReturnId(), returnOrderId))
                    .collect(Collectors.toList());
            //当前囤货退单已分配分摊金额，跳过
            if (CollectionUtils.isNotEmpty(assignedList)) {
                continue;
            }

            //查询订单信息
            NewPileTrade newPileTrade = newPileTradeService.getNewPileTradeById(returnOrder.getTid());
            if (Objects.isNull(newPileTrade)) {
                log.info("跳过，newPileTrade为空，退单号为：{}", returnOrderId);
                continue;
            }

            if (CollectionUtils.isEmpty(newPileTrade.getTradeItems())) {
                log.info("跳过，newPileTrade.getTradeItems()为空，囤货单号为：{}", newPileTrade.getId());
                continue;
            }

            //查询该笔囤货单未提的分摊数据
            List<InventoryDetailSamountVO> notTakeAmountList = inventoryDetailSamountProvider.getNoTiinventoryDetailSamount
                    (InventoryDetailSamountRequest.builder()
                            .goodsInfoIds(newPileTrade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList()))
                            .oid(newPileTrade.getId()).build()).getContext().getInventoryDetailSamountVOS();
            if (CollectionUtils.isEmpty(notTakeAmountList)) {
                log.info("跳过，无未提的分摊金额，囤货单号为：{}", newPileTrade.getId());
                continue;
            }

            ReturnPrice returnPrice = returnOrder.getReturnPrice();
            BigDecimal expectPrice = returnPrice.getTotalPrice();
            BigDecimal returnPriceAssigned = notTakeAmountList.stream().map(InventoryDetailSamountVO::getAmortizedExpenses)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (returnPriceAssigned.compareTo(expectPrice) != 0) {
                log.info("囤货退单分配金额不匹配：期望 {}，实际 {}", expectPrice, returnPriceAssigned);
                errorMap.put(returnOrderId, String.format("囤货退单分配金额不匹配：期望 %s，实际 %s", expectPrice, returnPriceAssigned));
                continue;
            }

            //改价金额维持原来数据不变，先备份
            BigDecimal actualReturnPrice = returnPrice.getActualReturnPrice();
            BigDecimal applyPrice = returnPrice.getApplyPrice();
            Boolean applyStatus = returnPrice.getApplyStatus();
            BigDecimal actualBalanceReturnPrice = returnPrice.getActualBalanceReturnPrice();

            //填充退单价格
            fillReturnPrice(returnOrder, returnOrder.getReturnEventLogs().get(0).getOperator(), notTakeAmountList);
            //改价金额恢复
            returnPrice.setActualReturnPrice(actualReturnPrice);
            returnPrice.setApplyPrice(applyPrice);
            returnPrice.setApplyStatus(applyStatus);
            returnPrice.setActualBalanceReturnPrice(actualBalanceReturnPrice);

            //填充退单交易条目
            fillReturnItems(returnOrder, newPileTrade, notTakeAmountList);

            newPileReturnOrderRepository.save(returnOrder);
            log.info("更新囤货退单：{}", returnOrderId);
            inventoryDetailSamountService.updateNewPileReturnAmount(returnOrder);
            log.info("更新分摊金额：{}", returnOrderId);

            // 已完成（标记为退款完成）
            if (NewPileReturnFlowState.COMPLETED.equals(returnOrder.getReturnFlowState())) {
                inventoryDetailSamountService.returnAmountByRid(returnOrderId);
                log.info("更新分摊金额为已完成退款：{}", returnOrderId);
            }
            successMap.put(returnOrderId,returnOrder.getTid());
        }
        log.info("更新成功的退单号：{}", JSON.toJSONString(successMap));
        log.info("更新失败的退单号：{}", JSON.toJSONString(errorMap));
    }

    public void refillReturnBalancePriceAndCashPrice(NewPileReturnOrder returnOrder, BigDecimal modifyPrice) {
        if (modifyPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SbcRuntimeException("改价金额不能小于0");
        }
        if (modifyPrice.compareTo(returnOrder.getReturnPrice().getTotalPrice()) > 0) {
            throw new SbcRuntimeException("改价金额不能大于总可退金额");
        }

        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        //修改价格大于 付现金：现金金额不变，退余额 = 修改价格 - 退现金; 退运费=0
        if (modifyPrice.compareTo(returnPrice.getActualReturnCash()) > 0) {
            returnPrice.setActualBalanceReturnPrice(modifyPrice.subtract(returnPrice.getActualReturnCash()));
        } else {
            //修改价格小于等于 付现金：现金金额=修改价格，退余额 =0
            returnPrice.setActualReturnCash(modifyPrice);
            returnPrice.setActualBalanceReturnPrice(BigDecimal.ZERO);
            returnPrice.setDeliveryPrice(BigDecimal.ZERO);
        }
    }

    @Transactional
    @LcnTransaction
    public void refundOnlineByTid(String rid, Operator operator) {
        NewPileReturnOrder returnPileOrder = findById(rid);
        NewPileTrade pileTrade = newPileTradeService.detail(returnPileOrder.getTid());

        if (!NewPileReturnFlowState.AUDIT.equals(returnPileOrder.getReturnFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货退单状态已变化，请核实！");
        }

        if (Objects.isNull(pileTrade)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单不存在！");
        }

        if (NewPileFlowState.VOID.equals(pileTrade.getTradeState().getFlowState())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单已作废，不可退款，请核实！");
        }

        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);

        //退单状态
        NewPileReturnStateRequest request = NewPileReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(NewPileReturnEvent.REFUND)
                .data(refundOrder.getRefundBill().getActualReturnPrice())
                .build();

        newPileReturnFSMService.changePileState(request);
        pileTrade.setRefundFlag(true);
        newPileTradeService.updateTrade(pileTrade);

        //DONE: 退款开关，囤货退款-线上
        //余额对接, 退款到余额
        RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_RETURN)
                .refund(pileTrade, returnPileOrder);

        //囤货线上退款退优惠券
        newPileTradeService.returnCoupon(returnPileOrder.getTid(), returnPileOrder.getId());

        if (returnPileOrder.getReturnType() == ReturnType.REFUND && returnPileOrder.getPlatform().equals(Platform.CUSTOMER)
                && (Objects.isNull(returnPileOrder.getWmsStats()) || !returnPileOrder.getWmsStats())) {

            //仅退款退单在退款完成后释放商品库存   ==》创建退单时就已经释放库存了
//            freePileGoodsNum(returnPileOrder.getTid());
            //发布订单退款事件
            newPileTradeService.fireRefundTradeEvent(returnPileOrder.getTid(), operator);
            pileTrade.getTradeState().setEndTime(LocalDateTime.now());
        }

        if (returnPileOrder.getPayType() == PayType.OFFLINE) {
            String businessId = pileTrade.getPayInfo().isMergePay() ? pileTrade.getParentId() : pileTrade.getId();
            newPileReturnOrderQuery.saveReconciliation(returnPileOrder, "", businessId, "");

            //囤货退货单推金碟
            if (kingdeeOpenState) {//判断是否开启新金蝶
                log.info("囤货退货单推送---------------》" + JSONObject.toJSONString(returnPileOrder));
                PayWay payWay = PayWay.CASH;
                returnOrderService.newPilePushRefundOrderKingdee(returnPileOrder, refundOrder, payWay);
            }
        }
    }

    public void pushKingdeeForClaims(NewPileTrade newPileTrade, NewPileReturnOrder newPileReturnOrder) {
        if (kingdeeOpenState) {
            pushKingdeeService.pushRefundOrderKingdeeForClaims(newPileTrade, newPileReturnOrder);
        }
    }
}
