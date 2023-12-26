package com.wanmi.sbc.returnorder.returnorder.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.finance.record.AccountRecordProvider;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordAddRequest;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordDeleteByReturnOrderCodeAndTypeRequest;
import com.wanmi.sbc.account.bean.enums.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.enums.node.ReturnOrderProcessType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountProvider;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountAddRequest;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountOptionalRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountAddResponse;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountOptionalResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerAccountAddOrModifyDTO;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.goods.api.provider.groupongoodsinfo.GrouponGoodsInfoSaveProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.groupongoodsinfo.GrouponGoodsInfoReturnModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchPlusStockRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivitySaveProvider;
import com.wanmi.sbc.marketing.api.provider.grouponrecord.GrouponRecordProvider;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityModifyStatisticsNumByIdRequest;
import com.wanmi.sbc.marketing.api.request.grouponrecord.GrouponRecordDecrBuyNumRequest;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.mongo.MongoTccHelper;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.ChannelItemByIdRequest;
import com.wanmi.sbc.pay.api.request.RefundResultByOrdercodeRequest;
import com.wanmi.sbc.pay.api.request.TradeRecordByOrderCodeRequest;
import com.wanmi.sbc.pay.api.response.PayChannelItemResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.returnorder.api.constant.RefundReasonConstants;
import com.wanmi.sbc.returnorder.api.request.distribution.ReturnOrderSendMQRequest;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderRefundRequest;
import com.wanmi.sbc.returnorder.api.request.refund.RefundOrderRequest;
import com.wanmi.sbc.returnorder.ares.service.OrderAresService;
import com.wanmi.sbc.returnorder.bean.enums.*;
import com.wanmi.sbc.returnorder.bean.vo.TradeDistributeItemVO;
import com.wanmi.sbc.returnorder.common.OperationLogMq;
import com.wanmi.sbc.returnorder.common.OrderCommonService;
import com.wanmi.sbc.returnorder.customer.service.CustomerCommonService;
import com.wanmi.sbc.returnorder.groupon.service.GrouponOrderService;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
import com.wanmi.sbc.returnorder.payorder.service.PayOrderService;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchase;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseRepository;
import com.wanmi.sbc.returnorder.refund.model.root.RefundBill;
import com.wanmi.sbc.returnorder.refund.model.root.RefundOrder;
import com.wanmi.sbc.returnorder.refund.repository.RefundOrderRepository;
import com.wanmi.sbc.returnorder.refund.service.RefundBillService;
import com.wanmi.sbc.returnorder.refund.service.RefundOrderService;
import com.wanmi.sbc.returnorder.returnorder.fsm.ReturnFSMService;
import com.wanmi.sbc.returnorder.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.returnorder.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.returnorder.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrderTransfer;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnEventLog;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnLogistics;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPoints;
import com.wanmi.sbc.returnorder.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.returnorder.returnorder.mq.ReturnOrderProducerService;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnPileOrderRepository;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnPileOrderTransferRepository;
import com.wanmi.sbc.returnorder.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecord;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.returnorder.trade.model.root.GrouponInstance;
import com.wanmi.sbc.returnorder.trade.model.root.PileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.TradeGroupon;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordRepository;
import com.wanmi.sbc.returnorder.trade.service.PileTradeService;
import com.wanmi.sbc.returnorder.util.KingdeeLoginUtils;
import com.wanmi.sbc.wms.api.provider.erp.PushOrderKingdeeProvider;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSOrderProvider;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushReturnGoodsRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushReturnGoodsTableBodyRequest;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 囤货退单接口service
 * @author: jiangxin
 * @create: 2021-09-28 14:30
 */
@Slf4j
@Service
public class ReturnPileOrderService {

    @Autowired
    private OrderAresService orderAresService;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private ReturnPileOrderService returnPileOrderService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private GrouponOrderService grouponOrderService;

    @Autowired
    private RefundBillService refundBillService;

    @Autowired
    private CustomerAccountQueryProvider customerAccountQueryProvider;

    @Autowired
    private CustomerAccountProvider customerAccountProvider;

    @Autowired
    private AccountRecordProvider accountRecordProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GrouponRecordProvider recordProvider;

    @Autowired
    private GrouponActivitySaveProvider activityProvider;

    @Autowired
    private GrouponGoodsInfoSaveProvider grouponGoodsInfoProvider;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private PileTradeService pileTradeService;

    @Autowired
    private ReturnFSMService returnFSMService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private PilePurchaseRepository pilePurchaseRepository;

    @Autowired
    private OrderCommonService orderCommonService;

    /**
     * 注入退单状态变更生产者service
     */
    @Autowired
    private ReturnOrderProducerService returnOrderProducerService;

    @Autowired
    private ReturnPileOrderRepository returnPileOrderRepository;

    @Autowired
    private RefundOrderRepository refundOrderRepository;

    @Autowired
    private ReturnPileOrderTransferRepository returnPileOrderTransferRepository;

    @Autowired
    private ReturnPileOrderTransferService returnPileOrderTransferService;

    @Resource
    private MongoTccHelper mongoTccHelper;

    @Autowired
    private RequestWMSOrderProvider requestWMSOrderProvider;

    @Value("${wms.api.flag}")
    private Boolean wmsPushFlag;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private PushOrderKingdeeProvider pushOrderKingdeeProvider;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.unionpay}")
    private String kingdeeUnionpay;


    @SuppressWarnings("unused")
    public void confirmDelTransfer(String userId) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelDelTransfer(String userId) {
        mongoTccHelper.cancel();
    }

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param returnPileOrder
     */
    @MongoRollback(persistence = ReturnPileOrder.class, operation = Operation.ADD)
    public ReturnPileOrder addReturnOrder(ReturnPileOrder returnPileOrder) {
        return returnPileOrderRepository.save(returnPileOrder);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param returnPileOrder
     */
    @MongoRollback(persistence = ReturnPileOrder.class, operation = Operation.UPDATE)
    public void  updateReturnOrder(ReturnPileOrder returnPileOrder) {
        returnPileOrderRepository.save(returnPileOrder);
    }

    /**
     * 删除文档
     *
     * @param rid
     */
    @MongoRollback(persistence = ReturnPileOrder.class, idExpress = "rid", operation = Operation.UPDATE)
    public void deleteReturnOrder(String rid) {
        returnPileOrderRepository.deleteById(rid);
    }

    /**
     * 创建退单快照
     *
     * @param returnPileOrder
     * @param operator
     */
    @LcnTransaction
    public void transfer(ReturnPileOrder returnPileOrder, Operator operator) {
        PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
        //查询该订单所有退单
        List<ReturnPileOrder> returnOrderList = returnPileOrderRepository.findByTid(pileTrade.getId());
        //过滤处理中的退单
        List<ReturnPileOrder> returnPileOrders = returnOrderList.stream().filter(item -> item.getReturnFlowState() !=
                        ReturnFlowState.COMPLETED
                        && item.getReturnFlowState() != ReturnFlowState.VOID
                        && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                        && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                        && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(returnPileOrders)) {
            throw new SbcRuntimeException("K-050120");
        }

        this.verifyNum(pileTrade, returnPileOrder.getReturnItems());
        returnPileOrder.setReturnType(ReturnType.RETURN);

        Buyer buyer = new Buyer();
        buyer.setId(operator.getUserId());

        returnPileOrder.setBuyer(buyer);


        // 计算并设置需要退的赠品
        getAndSetReturnGifts(returnPileOrder, pileTrade, returnOrderList);

        ReturnPileOrderTransfer returnPileOrderTransfer = new ReturnPileOrderTransfer();
        KsBeanUtil.copyProperties(returnPileOrder, returnPileOrderTransfer);
        delTransfer(operator.getUserId());
        returnPileOrderTransfer.setId(UUIDUtil.getUUID());
        returnPileOrderTransferService.addReturnPileOrderTransfer(returnPileOrderTransfer);
    }

    /**
     * 获取并设置本次退单需要退的赠品信息
     *
     * @param returnPileOrder     本次退单
     * @param pileTrade           对应的订单信息
     * @param returnPileOrderList 订单对应的所有退单
     * @author marsjiang
     */
    private void getAndSetReturnGifts(ReturnPileOrder returnPileOrder, PileTrade pileTrade, List<ReturnPileOrder> returnPileOrderList) {
        List<TradeMarketingVO> tradeMarketings = pileTrade.getTradeMarketings();
        if (CollectionUtils.isNotEmpty(tradeMarketings)) {
            // 1.找到原订单的所有满赠的营销活动marketingList
            List<TradeMarketingVO> giftMarketings = tradeMarketings.stream().filter(tradeMarketing -> MarketingType
                    .GIFT.equals(tradeMarketing.getMarketingType())).collect(Collectors.toList());
            if (giftMarketings.size() > 0) {
                //原订单所有商品的Map,方便根据skuId快速找到对应的商品信息
                Map<String, TradeItem> tradeItemMap = pileTrade.getTradeItems().stream().collect(Collectors.toMap
                        (TradeItem::getSkuId, Function.identity()));
                //原订单所有赠品的Map,方便根据skuId快速找到对应的赠品信息
                Map<String, TradeItem> giftItemMap = pileTrade.getGifts().stream().collect(Collectors.toMap
                        (TradeItem::getSkuId, Function.identity()));
                //该订单之前已完成的退单list
                List<ReturnPileOrder> comReturnOrders = filterFinishedReturnOrder(returnPileOrderList);
                // (分批退单的场景)
                Map<String, ReturnItem> comReturnSkus = new HashMap<>();//已经退的商品汇总(根据skuId汇总所有商品的数量)
                Map<String, ReturnItem> currReturnSkus = returnPileOrder.getReturnItems().stream().collect(Collectors
                        .toMap(ReturnItem::getSkuId, Function.identity()));//本次退的商品汇总
                Map<String, ReturnItem> allReturnGifts = new HashMap<>();//可能需要退的赠品汇总
                Map<String, ReturnItem> comReturnGifts = new HashMap<>();//已经退的赠品汇总
                comReturnOrders.stream().forEach(reOrder -> {
                    reOrder.getReturnItems().stream().forEach(returnItem -> {
                        ReturnItem currItem = comReturnSkus.get(returnItem.getSkuId());
                        if (currItem == null) {
                            comReturnSkus.put(returnItem.getSkuId(), returnItem);
                        } else {
                            currItem.setNum(currItem.getNum().add(returnItem.getNum()));
                        }
                    });

                    if (CollectionUtils.isNotEmpty(reOrder.getReturnGifts())) {
                        reOrder.getReturnGifts().stream().forEach(retrunGift -> {
                            ReturnItem currGiftItem = comReturnGifts.get(retrunGift.getSkuId());
                            if (currGiftItem == null) {
                                comReturnGifts.put(retrunGift.getSkuId(), retrunGift);
                            } else {
                                currGiftItem.setNum(currGiftItem.getNum().add(retrunGift.getNum()));
                            }
                        });
                    }
                });

                // 2.遍历满赠营销活动list,验证每个活动对应的剩余商品(购买数量或金额-已退的总数或总金额)是否还满足满赠等级的条件
                //   PS: 已退的总数或总金额分为两部分: a.该订单关联的所有已完成的退单的商品 b.本次用户准备退货的商品
                giftMarketings.forEach(giftMarketing -> {
                    if (MarketingSubType.GIFT_FULL_AMOUNT.equals(giftMarketing.getSubType())) {
                        BigDecimal leftSkuAmount = giftMarketing.getSkuIds().stream().map(skuId -> {
                            TradeItem skuItem = tradeItemMap.get(skuId);
                            BigDecimal comReSkuCount = comReturnSkus.get(skuId) == null ? BigDecimal.ZERO : comReturnSkus.get(skuId)
                                    .getNum();
                            BigDecimal currReSkuCount = currReturnSkus.get(skuId) == null ? BigDecimal.ZERO : currReturnSkus.get(skuId)
                                    .getNum();
                            return skuItem.getLevelPrice().multiply(BigDecimal.valueOf(skuItem.getDeliveredNum()).subtract(
                                    comReSkuCount).subtract(currReSkuCount));//某商品的发货商品价格 - 已退商品价格 - 当前准备退的商品价格
                        }).reduce(BigDecimal::add).get();//剩余商品价格汇总

                        // 3.若不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中(若满足满赠条件,则无需退赠品)
                        if (leftSkuAmount.compareTo(giftMarketing.getGiftLevel().getFullAmount()) < 0) {
                            setReturnGiftsMap(giftMarketing, allReturnGifts, giftItemMap);
                        }
                    } else if (MarketingSubType.GIFT_FULL_COUNT.equals(giftMarketing.getSubType())) {
                        long leftSkuCount = giftMarketing.getSkuIds().stream().mapToLong(skuId -> {
                            TradeItem skuItem = tradeItemMap.get(skuId);
                            long comReSkuCount = comReturnSkus.get(skuId) == null ? 0L : comReturnSkus.get(skuId)
                                    .getNum().longValue();
                            long currReSkuCount = currReturnSkus.get(skuId) == null ? 0L : currReturnSkus.get(skuId)
                                    .getNum().longValue();
                            return skuItem.getDeliveredNum().longValue() - comReSkuCount - currReSkuCount;//某商品的发货商品数
                            // - 已退商品数 - 当前准备退的商品数
                        }).sum();//剩余商品数量汇总

                        // 3.若不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中(若满足满赠条件,则无需退赠品)
                        if (leftSkuCount < giftMarketing.getGiftLevel().getFullCount()) {
                            setReturnGiftsMap(giftMarketing, allReturnGifts, giftItemMap);
                        }
                    } else if (MarketingSubType.GIFT_FULL_ORDER.equals(giftMarketing.getSubType())) {
                        long leftSkuCount = giftMarketing.getSkuIds().stream().mapToLong(skuId -> {
                            if("all".equals(skuId)){
                                return 0;
                            }
                            TradeItem skuItem = tradeItemMap.get(skuId);
                            long comReSkuCount = comReturnSkus.get(skuId) == null ? 0L : comReturnSkus.get(skuId)
                                    .getNum().longValue();
                            long currReSkuCount = currReturnSkus.get(skuId) == null ? 0L : currReturnSkus.get(skuId)
                                    .getNum().longValue();
                            return skuItem.getDeliveredNum().longValue() - comReSkuCount - currReSkuCount;//某商品的发货商品数
                            // - 已退商品数 - 当前准备退的商品数
                        }).sum();//剩余商品数量汇总

                        // 3.若不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中(若满足满赠条件,则无需退赠品)
                        if (leftSkuCount < giftMarketing.getGiftLevel().getFullCount()) {
                            setReturnGiftsMap(giftMarketing, allReturnGifts, giftItemMap);
                        }
                    }

                });

                // 4.设置具体的退单赠品信息
                returnPileOrder.setReturnGifts(getReturnGiftList(pileTrade, allReturnGifts, comReturnGifts));
            }
        }
    }

    /**
     * 不满足满赠条件时,需要退的所有赠品
     *
     * @param giftMarketing  某个满赠营销活动
     * @param allReturnGifts 不满足满赠条件,满赠营销活动中所有需要退的赠品信息
     * @param giftItemMap    赠品具体信息Map(获取除了skuId以外的详细信息)
     */
    private void setReturnGiftsMap(TradeMarketingVO giftMarketing, Map<String, ReturnItem> allReturnGifts, Map<String,
            TradeItem> giftItemMap) {
        // 不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中
        giftMarketing.getGiftLevel().getFullGiftDetailList().stream().forEach(gift -> {
            ReturnItem currGiftItem = allReturnGifts.get(gift.getProductId());
            TradeItem giftDetail = giftItemMap.get(gift.getProductId());
            if (currGiftItem == null) {
                if(Objects.nonNull(giftDetail)){
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
     * 获取具体的退单赠品信息
     *
     * @param pileTrade      订单
     * @param allReturnGifts 不满足满赠条件,满赠营销活动中所有需要退的赠品信息
     * @param comReturnGifts 所有已完成退单中的退掉的赠品信息
     * @return
     */
    private List<ReturnItem> getReturnGiftList(PileTrade pileTrade, Map<String, ReturnItem> allReturnGifts, Map<String,
            ReturnItem> comReturnGifts) {
        // 本次退单的退货赠品总数: 每个商品所有退货赠品数量 - 之前所有退单中已经退掉的赠品总数
        //   PS: 为了保证退单中赠品顺序与订单中的赠品顺序一致,遍历订单赠品,依次计算得出本次退单需要退的赠品list
        List<ReturnItem> returnGiftList = pileTrade.getGifts().stream().map(tradeItem -> {
            ReturnItem readyGiftItem = allReturnGifts.get(tradeItem.getSkuId());//准备退的
            ReturnItem comGiftItem = comReturnGifts.get(tradeItem.getSkuId());//之前已完成退单已经退掉的
            if (readyGiftItem != null) {
                BigDecimal totalNum = readyGiftItem.getNum().compareTo(BigDecimal.valueOf(tradeItem.getDeliveredNum())) < 0 ? readyGiftItem.getNum
                        () : BigDecimal.valueOf(tradeItem.getDeliveredNum());//退货总数 与 发货总数对比,取最小的值
                //仅退款数量设置为赠品总数
                if (pileTrade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || pileTrade
                        .getTradeState().getDeliverStatus()
                        == DeliverStatus.VOID) {
                    totalNum = readyGiftItem.getNum();
                }
                if (comGiftItem != null) {
                    BigDecimal currNum = totalNum.subtract(comGiftItem.getNum());
                    if (currNum.compareTo(BigDecimal.ZERO) > 0) {
                        readyGiftItem.setNum(currNum);
                    } else {
                        return null;
                    }
                } else {
                    readyGiftItem.setNum(totalNum);
                }
                return readyGiftItem;
            }
            return null;
        }).filter(reGift -> reGift != null).collect(Collectors.toList());
        return returnGiftList;
    }

    private void verifyNum(PileTrade pileTrade, List<ReturnItem> returnItems) {
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
     * 囤货订单中可退单的数量
     * @param pileTrade
     */
    private Map<String, BigDecimal> findLeftItems(PileTrade pileTrade) {
//        if (!pileTrade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !pileTrade.getTradeState()
//                .getFlowState().equals(FlowState.COMPLETED)) {
//            throw new SbcRuntimeException("K-050002");
//        }

//        Map<String, BigDecimal> map = pileTrade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
//                g->BigDecimal.valueOf(g.getDeliveredNum())));

        //订单商品id集合
        List<String> skuIds = pileTrade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        //客户订单囤货商品详情
        List<PilePurchase> pilePurchaseList = pilePurchaseRepository.queryPurchaseByGoodsInfoIdsAndCustomerId(skuIds,pileTrade.getBuyer().getId());
        //客户订单囤货商品数量map k->goodsInfoId,v->goodsNum
        Map<String,Long> goodsNumMap = pilePurchaseList.stream().collect(Collectors.toMap(PilePurchase::getGoodsInfoId,p->p.getGoodsNum()));

        Map<String, BigDecimal> map = pileTrade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                g->BigDecimal.valueOf(g.getNum())));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(pileTrade.getId()).stream().filter(returnPileOrder -> Objects.isNull(returnPileOrder.getWmsStats()) || !returnPileOrder.getWmsStats())

                .map(ReturnPileOrder::getReturnItems)
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
                                returned = groupMap.get(key).stream().mapToDouble(g->g.getNum().doubleValue()).sum();
                            }

                            //计算出可退数量
                            BigDecimal canReturnNum = total.subtract(BigDecimal.valueOf(returned));
                            //如果囤货商品数量不为空
                            if(Objects.nonNull(goodsNumMap.get(key))){
                                //如果商品可退数量大于囤货数量
                                if (1 == canReturnNum.compareTo(BigDecimal.valueOf(goodsNumMap.get(key)))){
                                    //可退数量返回囤货数量
                                    canReturnNum = BigDecimal.valueOf(goodsNumMap.get(key));
                                }
                            }else {
                                canReturnNum = BigDecimal.ZERO;
                            }
                            return canReturnNum;
                        }
                ));
    }

    /**
     * 计算囤货订单
     * @param stockRecordList
     * @return
     */
    private Map<String,BigDecimal> stockRecordCanBackNumber(List<PileStockRecord> stockRecordList){
        return stockRecordList.stream().collect(Collectors.toMap(PileStockRecord::getGoodsInfoId,pileStockRecord -> {
            Long stockRecordNum = pileStockRecord.getStockRecordNum();
            Long remainingNum = pileStockRecord.getStockRecordRemainingNum();
            if (stockRecordNum == remainingNum){
                return BigDecimal.ZERO;
            }else {
                return BigDecimal.valueOf(stockRecordNum-remainingNum);
            }
        }));
    }

    /**
     * 查询退单列表，不包含已作废状态以及拒绝收货的退货单与拒绝退款的退款单
     * @return
     */
    public List<ReturnPileOrder> findReturnsNotVoid(String tid) {
        List<ReturnPileOrder> returnOrders = returnPileOrderRepository.findByTid(tid);
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
    public List<ReturnPileOrder> filterFinishedReturnOrder(List<ReturnPileOrder> returnPileOrders) {
        return returnPileOrders.stream().filter(t -> !t.getReturnFlowState().equals(ReturnFlowState.VOID)
                        && !t.getReturnFlowState().equals(ReturnFlowState.REJECT_RECEIVE) &&
                        !(t.getReturnType() == ReturnType.REFUND && t.getReturnFlowState() == ReturnFlowState.REJECT_REFUND))
                .collect(Collectors.toList());
    }

    /**
     * 删除订单快照
     *
     * @param userId
     */
    @TccTransaction
    public void delTransfer(String userId) {
        ReturnPileOrderTransfer returnPileOrderTransfer = returnPileOrderTransferRepository.findReturnOrderTransferByBuyerId(userId);
        if (Objects.nonNull(returnPileOrderTransfer)) {
            returnPileOrderTransferService.deleteReturnPileOrderTransfer(returnPileOrderTransfer.getId());
        }
    }

    /**
     * 退单创建
     *
     * @param returnPileOrder
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public String create(ReturnPileOrder returnPileOrder, Operator operator, Integer forceRefund) {
        if (returnPileOrder.getDescription().length() > 100) {
            throw new SbcRuntimeException("K-000009");
        }
        //查询订单信息
        PileTrade pileTrade;
        if (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats()) { //wms自动退单
            pileTrade = this.queryWmsCanReturnItemNumByTid(returnPileOrder.getTid());
        }else{
            pileTrade = this.queryCanReturnItemNumByTid(returnPileOrder.getTid());
        }
        //查询该订单所有退单
        List<ReturnPileOrder> returnPileOrderList = returnPileOrderRepository.findByTid(pileTrade.getId());
        //筛选出已完成的退单列表
        List<ReturnPileOrder> completedReturnOrders = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(returnPileOrderList)){
            //过滤处理中的退单
            List<ReturnPileOrder> returnPileOrders = returnPileOrderList.stream().filter(item -> item.getReturnFlowState() !=
                            ReturnFlowState.COMPLETED
                            && item.getReturnFlowState() != ReturnFlowState.VOID
                            && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                            && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                            && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(returnPileOrders)) {
                throw new SbcRuntimeException("K-050120");
            }

            completedReturnOrders = returnPileOrderList.stream().filter(allOrder -> allOrder
                            .getReturnFlowState() == ReturnFlowState.COMPLETED)
                    .collect(Collectors.toList());
        }


        //是否仅退款 =====>囤货订单直接退款，无退货操作
//        boolean isRefund = pileTrade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || pileTrade
//                .getTradeState().getDeliverStatus()
//                == DeliverStatus.VOID;
        boolean isRefund = true;
        //如果是代客退单
        if (Objects.nonNull(forceRefund) && 1 == forceRefund){
            isRefund = false;
        }

        //退单总金额
        ReturnPrice returnPrice = returnPileOrder.getReturnPrice();

        //计算该订单下所有已完成退单的总金额
        BigDecimal allOldPrice = new BigDecimal(0);
        for (ReturnPileOrder order : completedReturnOrders) {
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
        if(returnPileOrder.getReturnPoints() == null ){
            returnPileOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());
        }

        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnPileOrder.getTid()).get();
        if (operator.getPlatform() == Platform.BOSS && PayType.fromValue(Integer.parseInt(pileTrade.getPayInfo()
                .getPayTypeId())) == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                == -1) {
            throw new SbcRuntimeException("K-050126");
        }

        if (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats() ) {
            createWMSRefund(returnPileOrder, operator, pileTrade);
        }else if (isRefund) {
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

        returnPileOrder.setReturnFlowState(ReturnFlowState.INIT);

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
        returnPileOrder.getReturnItems().forEach(info->{
            Optional<TradeItem> tradeItemOptional = pileTrade.getTradeItems().stream().filter(tradeItem -> info
                    .getSkuId().equals(tradeItem.getSkuId())).findFirst();
            if(tradeItemOptional.isPresent()){
                TradeItem tradeItem = tradeItemOptional.get();
                info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
            }
        });

        BigDecimal canReturnPrice = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();

        //自动退款
        Optional<RefundOrder> refundOrderOptional = null;

        //仅退款直接跳过审核生成退款单
        if (isRefund || (Objects.nonNull(returnPileOrder.getWmsStats()) && returnPileOrder.getWmsStats())) {
            returnPileOrder.setReturnFlowState(ReturnFlowState.AUDIT);
            refundOrderOptional = refundOrderService.generateRefundPileOrderByEnity(returnPileOrder, returnPileOrder.getBuyer().getId(), canReturnPrice,
                    returnPileOrder.getPayType());

            //=====================================
        }
        //保存退单
        returnPileOrderService.addReturnOrder(returnPileOrder);

        /**减少商品囤货数量开始*/
        String customerId = pileTrade.getBuyer().getId();
        if (isRefund){
            //查询囤货记录表
            List<PileStockRecord> stockRecordList = pileStockRecordRepository.getStockRecordOrderCode(returnPileOrder.getTid());
            Map<String,BigDecimal> stockRecordMap = stockRecordCanBackNumber(stockRecordList);
            //减少囤货数量//多个商品
            List<TradeItem> tradeItems = pileTrade.getTradeItems();
            tradeItems.stream().forEach(tradeItem -> {
                String spuId = tradeItem.getSpuId();
                String skuId = tradeItem.getSkuId();
                //囤货记录表商品是否有为0的，有就不让申请囤货退款
                if (stockRecordMap.containsKey(skuId) && stockRecordMap.get(skuId).compareTo(BigDecimal.ZERO)==0){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"我的囤货商品数量不足订单商品数量，无法退单！");
                }
                //购买数量
                Long num = tradeItem.getNum();
                //通过spuId+skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
                List<PilePurchase> pilePurchases = pilePurchaseRepository.queryPilePurchase(customerId, spuId, skuId);
                if(CollectionUtils.isNotEmpty(pilePurchases)){
                    for (PilePurchase pilePurchase : pilePurchases) {
                        //每一笔的购买数量从
                        Long goodsNum = pilePurchase.getGoodsNum();
                        if(goodsNum >= num){
                            //减去提货数量并且跳出循环
                            pilePurchase.setGoodsNum(goodsNum - num);
                            break;
                        }else {
                            //当购买数量小于提货数量,清空购买数量，减少提货数量
//                        pilePurchase.setGoodsNum(0L);
//                        num = num - pilePurchase.getGoodsNum();
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+tradeItem.getSkuName()+"囤货数量不足订单购买数量，不允许退单！");
                        }
                    }
                    //减完以后
                    pilePurchaseRepository.saveAll(pilePurchases);
                }
            });
        }else {//如果是代客退单
            //减少囤货数量//多个商品
            returnItems.stream().forEach(returnItem -> {
                String skuId = returnItem.getSkuId();
                //退单数量
                Long returnNum = returnItem.getNum().longValue();
                //通过skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
                List<PilePurchase> pilePurchases = pilePurchaseRepository.getPilePurchase(customerId, skuId);
                if(CollectionUtils.isNotEmpty(pilePurchases)){
                    for (PilePurchase pilePurchase : pilePurchases) {
                        //每一笔的购买数量从
                        Long goodsNum = pilePurchase.getGoodsNum();
                        if(goodsNum >= returnNum){
                            //减去提货数量并且跳出循环
                            pilePurchase.setGoodsNum(goodsNum - returnNum);
                            break;
                        }else {
                            //当购买数量小于提货数量,清空购买数量，减少提货数量
//                        pilePurchase.setGoodsNum(0L);
//                        num = num - pilePurchase.getGoodsNum();
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+returnItem.getSkuName()+"囤货数量{"+goodsNum+"}不足退单数量{"+returnNum+"}，不允许退单！");
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
        returnOrderProducerService.returnPileOrderFlow(sendMQRequest);

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
     * 查询订单详情,如已发货则带出可退商品数
     * 囤货代客退单，未发货也可以退商品
     * @param tid
     * @return
     */
    public PileTrade queryCanReturnItemNumByTid(String tid) {
        PileTrade pileTrade = pileTradeService.detail(tid);
        //校验支付状态
        PayOrderResponse payOrder = payOrderService.findPayOrder(pileTrade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
        if (pileTrade.getTradeState().getFlowState() == FlowState.GROUPON) {
            throw new SbcRuntimeException("K-050141");
        }
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
            Map<String,BigDecimal> stockRecordMap = stockRecordCanBackNumber(stockRecordList);
//            pileTrade.getTradeItems().forEach(
//                    item -> item.setCanReturnNum(map.get(item.getSkuId()))
//            );
            pileTrade.getTradeItems().forEach(
                    item -> item.setCanReturnNum(stockRecordMap.get(item.getSkuId()))
            );

            //计算赠品可退数
            if (CollectionUtils.isNotEmpty(pileTrade.getGifts())) {
                Map<String, BigDecimal> giftMap = findLeftGiftItems(pileTrade);
                pileTrade.getGifts().forEach(
                        item -> item.setCanReturnNum(giftMap.get(item.getSkuId()))
                );
            }
        }

        List<ReturnPileOrder> returnsNotVoid = findReturnsNotVoid(tid);
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
     * 查询订单详情,如已发货则带出可退商品数
     * 囤货代客退单，未发货也可以退商品
     * @param tid
     * @return
     */
    public PileTrade queryFilterCanReturnItemsByTid(String tid) {

        PileTrade pileTrade = queryCanReturnItemNumByTid(tid);

        List<TradeItem> filterItemList = pileTrade.getTradeItems().stream().filter(t -> BigDecimal.ZERO.compareTo(t.getCanReturnNum()) == -1).collect(Collectors.toList());

        pileTrade.setTradeItems(filterItemList);
        return pileTrade;
    }

    /**
     * 查询订单详情,如已发货则带出可退商品数
     *
     * @param tid
     * @return
     */
    public PileTrade queryWmsCanReturnItemNumByTid(String tid) {
        PileTrade pileTrade = pileTradeService.detail(tid);
        //校验支付状态
        PayOrderResponse payOrder = payOrderService.findPayOrder(pileTrade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
        if (pileTrade.getTradeState().getFlowState() == FlowState.GROUPON) {
            throw new SbcRuntimeException("K-050141");
        }


        List<ReturnPileOrder> returnsNotVoid = findReturnsNotVoid(tid);
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
     * 订单中可退赠品的数量
     *
     * @param pileTrade
     */
    private Map<String, BigDecimal> findLeftGiftItems(PileTrade pileTrade) {
        if (CollectionUtils.isNotEmpty(pileTrade.getGifts())) {
            Map<String, BigDecimal> map = pileTrade.getGifts().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                    g->BigDecimal.valueOf(g.getDeliveredNum())));
            List<ReturnItem> allReturnItems = this.findReturnsNotVoid(pileTrade.getId()).stream()
                    .map(ReturnPileOrder::getReturnGifts)
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
                                    returned = groupMap.get(key).stream().mapToDouble(g->g.getNum().doubleValue()).sum();
                                }
                                return total.subtract(BigDecimal.valueOf(returned));
                            }
                    ));
        }
        return new HashMap<>();
    }

    /**
     * 计算积分
     * @param returnPileOrder
     * @param pileTrade
     * @return
     */
    private Long getPoints(ReturnPileOrder returnPileOrder, PileTrade pileTrade) {
        // 各商品均摊积分
        Map<String, Double> splitPointMap = new HashMap<>();
        // 各商品购买数量
        Map<String, Long> totalNumMap = new HashMap<>();
        // 各商品消耗积分
        Map<String, Long> pointsMap = new HashMap<>();
        List<TradeItem> tradeItems = pileTrade.getTradeItems();
        for (TradeItem tradeItem : tradeItems) {
            Double splitPoint = new BigDecimal(tradeItem.getPoints())
                    .divide(new BigDecimal(tradeItem.getNum()), 2, BigDecimal.ROUND_DOWN)
                    .doubleValue();
            String skuId = tradeItem.getSkuId();
            splitPointMap.put(skuId, splitPoint);
            totalNumMap.put(skuId, tradeItem.getNum());
            pointsMap.put(skuId, tradeItem.getPoints());
        }

        List<ReturnItem> returnItems = returnPileOrder.getReturnItems();
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
     * @param returnPileOrder
     */
    private void createWMSRefund(ReturnPileOrder returnPileOrder, Operator operator, PileTrade pileTrade) {

        //校验该订单关联的退款单状态
        List<ReturnPileOrder> returnPileOrders = returnPileOrderRepository.findByTid(pileTrade.getId());
        if (!CollectionUtils.isEmpty(returnPileOrders)) {
            Optional<ReturnPileOrder> optional = returnPileOrders.stream().filter(item -> item.getReturnType() == ReturnType
                            .REFUND).filter(item ->
                            !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                                    ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        returnPileOrder.setReturnType(ReturnType.REFUND);
        //设置退货商品
        returnPileOrder.getReturnItems().forEach(returnItem -> {
            for(TradeItem item: pileTrade.getTradeItems()){
                if(item.getSkuNo().equals(returnItem.getSkuNo())){
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
    private void createRefund(ReturnPileOrder returnPileOrder, Operator operator, PileTrade pileTrade) {
        //校验该订单关联的退款单状态
        List<ReturnPileOrder> returnPileOrders = returnPileOrderRepository.findByTid(pileTrade.getId());
        if (!CollectionUtils.isEmpty(returnPileOrders)) {
            Optional<ReturnPileOrder> optional = returnPileOrders.stream().filter(item -> item.getReturnType() == ReturnType
                            .REFUND).filter(item ->
                            !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                                    ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }
        // 新增订单日志
        pileTradeService.returnOrder(returnPileOrder.getTid(), operator);
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

    /**
     * 创建退货单
     *
     * @param returnPileOrder
     */
    private void createReturn(ReturnPileOrder returnPileOrder, Operator operator, PileTrade pileTrade) {

        // 新增订单日志
        pileTradeService.returnOrder(returnPileOrder.getTid(), operator);

        this.verifyNum(pileTrade, returnPileOrder.getReturnItems());

//        returnPileOrder.setReturnType(ReturnType.RETURN);
        //囤货订单没有退货，只有退款
        returnPileOrder.setReturnType(ReturnType.REFUND);

        //填充退货商品信息
        Map<String, BigDecimal> map = findLeftItems(pileTrade);
        returnPileOrder.getReturnItems().forEach(item ->
                {
                    item.setSkuName(pileTrade.skuItemMap().get(item.getSkuId()).getSkuName());
                    item.setPic(pileTrade.skuItemMap().get(item.getSkuId()).getPic());
                    item.setSkuNo(pileTrade.skuItemMap().get(item.getSkuId()).getSkuNo());
                    item.setSpecDetails(pileTrade.skuItemMap().get(item.getSkuId()).getSpecDetails());
                    item.setUnit(pileTrade.skuItemMap().get(item.getSkuId()).getUnit());
                    item.setGoodsSubtitle(pileTrade.skuItemMap().get(item.getSkuId()).getGoodsSubtitle());
                    item.setCanReturnNum(map.get(item.getSkuId()));
                }
        );

    }

    /**
     * 功能描述: <br>调用第三方WMS接口
     * 〈〉
     * @Param: [returnOrder, warehouseId]
     * @Return: java.lang.Boolean
     * @Author:
     * @Date: 2020/5/19 15:12
     */
    private Boolean pushCancelOrder(ReturnPileOrder returnPileOrder,String warehouseId){
        BaseResponse<ResponseWMSReturnResponse> result=null;
        try {
            result = requestWMSOrderProvider.cancelOrder(WMSOrderCancelRequest.builder()
                    .docNo(returnPileOrder.getTid())
                    .customerId("XYY")
                    .orderType("XSCK")
                    .warehouseId(warehouseId)
                    .consigneeId(returnPileOrder.getBuyer().getCustomerErpId())
                    .erpCancelReason(returnPileOrder.getReturnReason().getDesc())
                    .build());
        }catch (Exception e){
            if(e instanceof SbcRuntimeException){
                SbcRuntimeException exception = (SbcRuntimeException)e;
                if("K-050510".equals(exception.getErrorCode())){
                    throw new SbcRuntimeException("K-050510");
                }
            }
            return false;
        }
        return !Objects.isNull(result) && !Objects.isNull(result.getContext()) &&
                !Objects.isNull(result.getContext().getResponseWMSReturnVO())&&
                AbstractXYYConstant.RESPONSE_SUCCESS.equals(result.getContext().getResponseWMSReturnVO().getReturnCode());
    }

    public ReturnPileOrder findById(String rid) {
        ReturnPileOrder returnPileOrder = returnPileOrderRepository.findById(rid).orElse(null);
        if (returnPileOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }
        return returnPileOrder;
    }

    /**
     * 查询退单，可能为空
     * @param rid
     * @return
     */
    public ReturnPileOrder dealEmptyFindById(String rid) {
        ReturnPileOrder returnPileOrder = returnPileOrderRepository.findById(rid).orElse(null);
        return returnPileOrder;
    }

    /**
     * 查找所有退货方式
     *
     * @return
     */
    public List<ReturnWay> findReturnWay() {
        return Arrays.asList(ReturnWay.values());
    }

    /**
     * 所有退货原因
     *
     * @return
     */
    public List<ReturnReason> findReturnReason() {
        return Arrays.asList(ReturnReason.values());
    }

    /**
     * 批量查询退货单
     * @param returnOrders
     * @return
     */
    public List<ReturnPileOrder> findAllById(List<String> returnOrders){
        return org.apache.commons.collections4.IteratorUtils.toList(returnPileOrderRepository.findAllById(returnOrders).iterator());
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
        //查询退单详情
        ReturnPileOrder returnPileOrder = findById(rid);
        // 查询订单相关的所有退单
        List<ReturnPileOrder> returnAllOrders = returnPileOrderRepository.findByTid(returnPileOrder.getTid());
        // 筛选出已完成的退单
        List<ReturnPileOrder> returnPileOrders = returnAllOrders.stream().filter(allOrder -> allOrder.getReturnFlowState() ==
                        ReturnFlowState.COMPLETED)
                .collect(Collectors.toList());
        //计算所有已完成的退单总价格
        BigDecimal allOldPrice = new BigDecimal(0);
        for (ReturnPileOrder order : returnPileOrders) {
            BigDecimal p = order.getReturnPrice().getApplyStatus() ? order.getReturnPrice().getApplyPrice() : order
                    .getReturnPrice().getTotalPrice();
            allOldPrice = allOldPrice.add(p);
        }
        ReturnPrice returnPrice = returnPileOrder.getReturnPrice();

        BigDecimal price = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnPileOrder.getTid()).get();
        // 退单金额校验 退款金额不可大于可退金额
        if (payOrder.getPayType() == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                == -1) {
            throw new SbcRuntimeException("K-050126");
        }
//        if (returnPileOrder.getReturnType() == ReturnType.REFUND) {
        //囤货订单 直接创建退款单，不管是客户退单退款，还是代客退款退单
        refundOrderService.generateRefundPileOrderByReturnOrderCode(rid, returnPileOrder.getBuyer().getId(), price,
                returnPileOrder.getPayType());
//        }
        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.AUDIT)
                .build();
        returnFSMService.changePileState(request);
        //自动发货
        autoDeliver(rid, operator);

        //wms三方推送
//        if (wmsPushFlag) {
//            PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
//            //只有传false的时候不推送，空和true都需要推送
//            if (Objects.isNull(returnPileOrder.getPushNeeded()) || returnPileOrder.getPushNeeded()) {
//                //推送退单
//                if (pushBackOrder(returnPileOrder, pileTrade.getWareHouseCode(), pileTrade)) {
//                    returnPileOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
//                } else {
//                    throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO, "申请退货失败，WMS接口异常");
//                }
//            }
//
//        }

        //售后审核通过发送MQ消息
        List<String> params = Lists.newArrayList(returnPileOrder.getReturnItems().get(0).getSkuName());
        String pic = returnPileOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_CHECK_PASS,
                params,
                returnPileOrder.getId(),
                returnPileOrder.getBuyer().getId(),
                pic,
                returnPileOrder.getBuyer().getAccount());

        log.info("ReturnPileOrderService.audit number:{}",returnPileOrders.size());

    }

    public boolean isReturnFull(ReturnPileOrder returnPileOrder) {
        List<ReturnPileOrder> returnPileOrders = returnPileOrderRepository.findByTid(returnPileOrder.getTid());
        returnPileOrders = returnPileOrders.stream().filter(item -> item.getReturnType() == ReturnType.RETURN).collect
                (Collectors.toList());

        List<ReturnItem> returnItems = returnPileOrders.stream().filter(item -> item.getReturnFlowState() ==
                        ReturnFlowState.COMPLETED
                        || item.getReturnFlowState() == ReturnFlowState.RECEIVED
                        || item.getReturnFlowState() == ReturnFlowState.REJECT_REFUND).flatMap(item -> item.getReturnItems()
                        .stream())
                .collect(Collectors.toList());
        Map<String, Long> tradeNumMap = pileTradeService.detail(returnPileOrder.getTid()).getTradeItems().stream().collect(

                Collectors.toMap(TradeItem::getSkuId, TradeItem::getDeliveredNum));

        Map<String, BigDecimal> returnNumMap = IteratorUtils.groupBy(returnItems, ReturnItem::getSkuId).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add)
                ));
        Optional optional = tradeNumMap.entrySet().stream().filter(entry -> {
            BigDecimal num = returnNumMap.get(entry.getKey());
            return num == null || num.compareTo(BigDecimal.valueOf(entry.getValue())) != 0;
        }).findFirst();
        return !optional.isPresent();
    }

    private void autoDeliver(String rid, Operator operator) {
        ReturnStateRequest request;
        ReturnPileOrder returnPileOrder = findById(rid);

        //非快递退回的退货单，审核通过后变更为已发货状态
        if (returnPileOrder.getReturnType() == ReturnType.RETURN && returnPileOrder.getReturnWay() == ReturnWay.OTHER) {
            request = ReturnStateRequest
                    .builder()
                    .rid(rid)
                    .operator(operator)
                    .returnEvent(ReturnEvent.DELIVER)
                    .build();
            returnFSMService.changePileState(request);
        }

    }

    private Boolean pushBackOrder(ReturnPileOrder param, String warehouseId, PileTrade trade) {
        if(!orderCommonService.wmsCanPileTrade(trade)){
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
            TradeItem tradeItemT = tradeItems.stream().filter(t->t.getSkuId().equals(returnItem.getSkuId())).findFirst().orElse(null);
            BigDecimal addStep =
                    tradeItemT != null && tradeItemT.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP) != null ?
                            tradeItemT.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP) :
                            returnItem.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal returnItemPrice = returnItem.getPrice().divide(addStep,2,BigDecimal.ROUND_HALF_UP);
            details.add(WMSChargeBackDetailsRequest.builder()
                    .sku(returnItem.getErpSkuNo())
                    .expectedQty(returnItem.getNum().multiply(addStep).setScale(2,BigDecimal.ROUND_HALF_UP))
                    .totalPrice(returnItemPrice)
                    .referenceNo(param.getId())
                    .lineNo(lineNo.get())
                    .build());
            lineNo.incrementAndGet();
        });
        BaseResponse<ResponseWMSReturnResponse> result = null;

        String jobNo = WmsErpIdConstants.ERP_CUSTOMER_ID;
        if(StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId())){
            EmployeeByIdResponse response = employeeQueryProvider
                    .getById(EmployeeByIdRequest.builder().employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if(Objects.nonNull(response) && Objects.nonNull(response.getJobNo())){
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
                .asnReferenceC(orderCommonService.getWmsSoReference5(trade))
                .docNo(param.getId())
                .asnReferenceA(param.getTid())
                .asnCreationTime(DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_1))
                .details(details)
                .build());

        return !Objects.isNull(result) && !Objects.isNull(result.getContext()) &&
                !Objects.isNull(result.getContext().getResponseWMSReturnVO()) &&
                AbstractXYYConstant.RESPONSE_SUCCESS.equals(result.getContext().getResponseWMSReturnVO().getReturnCode()) &&
                result.getContext().getResponseWMSReturnVO().getReturnFlag() > 0;
    }

    /**
     * 退单通知节点发送MQ消息
     * @param nodeType
     * @param processType
     * @param params
     * @param rid
     * @param customerId
     */
    private void sendNoticeMessage(NodeType nodeType, ReturnOrderProcessType processType, List<String> params, String rid, String customerId, String pic, String mobile){
        Map<String, Object> map = new HashMap<>();
        map.put("type", nodeType.toValue());
        map.put("orderType",1);
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
        returnOrderProducerService.sendMessage(messageMQRequest);
    }

    /**
     * 退货发出
     *
     * @param rid
     * @param logistics
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void deliver(String rid, ReturnLogistics logistics, Operator operator) {
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.DELIVER)
                .data(logistics)
                .build();
        returnFSMService.changePileState(request);
    }

    /**
     * 收货
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void receive(String rid, Operator operator) {

        // 查询退单信息
        ReturnPileOrder returnPileOrder = findById(rid);

        // 生成财务退款单
        ReturnPrice returnPrice = returnPileOrder.getReturnPrice();
        if (Objects.isNull(returnPrice.getApplyPrice())) {
            return;
        }
        BigDecimal price = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
        refundOrderService.generateRefundOrderByReturnOrderCode(rid, returnPileOrder.getBuyer().getId(), price,
                returnPileOrder.getPayType());
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.RECEIVE)
                .build();
        returnFSMService.changePileState(request);

        //判断是否全量退货完成
        if (isReturnFull(returnPileOrder)) {
            //作废订单
            pileTradeService.voidTrade(returnPileOrder.getTid(), operator);
            PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
            pileTrade.setRefundFlag(true);
            pileTradeService.updateTrade(pileTrade);
        }
    }

    /**
     * 拒绝收货
     *
     * @param rid
     * @param reason
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void rejectReceive(String rid, String reason, Operator operator) {
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.REJECT_RECEIVE)
                .data(reason)
                .build();
        returnFSMService.changePileState(request);

        // 拒绝退单时，发送MQ消息
        ReturnPileOrder returnPileOrder = this.findById(rid);
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnPileOrder.getBuyer().getId())
                .orderId(returnPileOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnPileOrderFlow(sendMQRequest);

        //退货物品拒收通知发送MQ消息
        List<String> params = Lists.newArrayList(returnPileOrder.getReturnItems().get(0).getSkuName(), reason);
        String pic = returnPileOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.RETURN_ORDER_GOODS_REJECT,
                params,
                returnPileOrder.getId(),
                returnPileOrder.getBuyer().getId(),
                pic,
                returnPileOrder.getBuyer().getAccount());

    }

    /**
     * 商家退款申请(修改退单价格新增流水)
     *
     * @param returnPileOrder
     * @param refundComment
     * @param actualReturnPoints
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void onlineEditPrice(ReturnPileOrder returnPileOrder, String refundComment, BigDecimal actualReturnPrice,
                                Long actualReturnPoints, Operator operator) {
        // 查询退款单
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnPileOrder.getId());
        // 退款单状态不等于待退款 -- 参数错误
        if (refundOrder.getRefundStatus() != RefundStatus.TODO) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        // 填充退款流水
        RefundBill refundBill;
        if ((refundBill = refundOrder.getRefundBill()) == null) {
            refundBill = new RefundBill();
            refundBill.setActualReturnPrice(Objects.isNull(actualReturnPrice) ? refundOrder.getReturnPrice() :
                    actualReturnPrice);
            refundBill.setActualReturnPoints(actualReturnPoints);
            refundBill.setCreateTime(LocalDateTime.now());
            refundBill.setRefundId(refundOrder.getRefundId());
            refundBill.setRefundComment(refundComment);
            refundBillService.save(refundBill);
        } else {
            refundBill.setActualReturnPrice(Objects.isNull(actualReturnPrice) ? refundOrder.getReturnPrice() :
                    actualReturnPrice);
            refundBill.setActualReturnPoints(actualReturnPoints);
        }
        refundBillService.saveAndFlush(refundBill);
        //设置退款单状态为待平台退款
        refundOrder.setRefundStatus(RefundStatus.APPLY);
        if (returnPileOrder.getReturnPrice().getTotalPrice().compareTo(actualReturnPrice) == 1) {
            returnPileOrder.getReturnPrice().setApplyStatus(true);
            returnPileOrder.getReturnPrice().setApplyPrice(actualReturnPrice);
        }
        returnPileOrder.getReturnPoints().setActualPoints(actualReturnPoints);
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
        this.operationLogMq.convertAndSend(operator, ReturnEvent.REFUND.getDesc(), detail);

        //退款审核通过发送MQ消息
        List<String> params = Lists.newArrayList(returnPileOrder.getReturnItems().get(0).getSkuName());
        String pic = returnPileOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.REFUND_CHECK_PASS,
                params,
                returnPileOrder.getId(),
                returnPileOrder.getBuyer().getId(),
                pic,
                returnPileOrder.getBuyer().getAccount());
    }

    /**
     * 退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void refund(String rid, Operator operator, BigDecimal price) {
        ReturnPileOrder returnPileOrder = findById(rid);
        PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
        ReturnPrice returnPrice = returnPileOrder.getReturnPrice();
        returnPrice.setActualReturnPrice(price);
        returnPileOrder.setReturnPrice(returnPrice);
        if (pileTrade.getGrouponFlag()) {
            //拼团订单退款后的处理
            modifyGrouponInfo(returnPileOrder, pileTrade);
        }
        //退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.REFUND)
                .data(price)
                .build();
        returnFSMService.changePileState(request);
        pileTrade.setRefundFlag(true);
        pileTradeService.updateTrade(pileTrade);

        String businessId = pileTrade.getPayInfo().isMergePay() ? pileTrade.getParentId() : pileTrade.getId();

        if (returnPileOrder.getReturnType() == ReturnType.REFUND && returnPileOrder.getPlatform().equals(Platform.CUSTOMER)
        && (Objects.isNull(returnPileOrder.getWmsStats()) || !returnPileOrder.getWmsStats()) ) {

            //仅退款退单在退款完成后释放商品库存   ==》创建退单时就已经释放库存了
//            freePileGoodsNum(returnPileOrder.getTid());
            //作废订单
            pileTradeService.voidTrade(returnPileOrder.getTid(), operator);
            pileTrade.getTradeState().setEndTime(LocalDateTime.now());
        }
        if (returnPileOrder.getPayType() == PayType.OFFLINE) {
            saveReconciliation(returnPileOrder, "", businessId, "");
        }
        //ares埋点-订单-用户退单(线上/线下)
        orderAresService.dispatchFunction("returnPileOrder", returnPileOrder);
    }

    /**
     * 保存退款对账明细
     *
     * @param returnPileOrder
     * @param payWayStr
     */
    @Transactional
    public void saveReconciliation(ReturnPileOrder returnPileOrder, String payWayStr, String businessId, String tradeNo) {
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnPileOrder.getId());
        if (Objects.isNull(refundOrder)) {
            return;
        }
        AccountRecordAddRequest reconciliation = new AccountRecordAddRequest();
        reconciliation.setAmount(returnPileOrder.getReturnPrice().getApplyStatus() ?
                returnPileOrder.getReturnPrice().getApplyPrice() : returnPileOrder.getReturnPrice().getTotalPrice());
        reconciliation.setCustomerId(returnPileOrder.getBuyer().getId());
        reconciliation.setCustomerName(returnPileOrder.getBuyer().getName());
        reconciliation.setOrderCode(returnPileOrder.getTid());
        reconciliation.setOrderTime(returnPileOrder.getCreateTime());
        reconciliation.setTradeTime(Objects.isNull(refundOrder.getRefundBill()) ? LocalDateTime.now() :
                refundOrder.getRefundBill().getCreateTime());

        if(StringUtils.isNotBlank(businessId)) {
            // 根据订单id查询流水号并存进对账明细
            TradeRecordByOrderCodeRequest request = new TradeRecordByOrderCodeRequest();
            request.setOrderId(businessId);
            BaseResponse<PayTradeRecordResponse> record = payQueryProvider.getTradeRecordByOrderCode(request);
            if (Objects.isNull(record)) {
                record = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(businessId));
            }
            if (Objects.nonNull(record) && Objects.nonNull(record.getContext()) && StringUtils.isNotEmpty(record
                    .getContext().getTradeNo())) {
                tradeNo = record.getContext().getTradeNo();
            }
        }
        reconciliation.setTradeNo(tradeNo);
        // 退款金额等于0 退款渠道标记为银联渠道
        if (refundOrder.getReturnPrice().compareTo(BigDecimal.ZERO) == 0) {
            payWayStr = "unionpay";
        }
        PayWay payWay;
        payWayStr = StringUtils.isBlank(payWayStr) ? payWayStr : payWayStr.toUpperCase();
        switch (payWayStr) {
            case "ALIPAY":
                payWay = PayWay.ALIPAY;
                break;
            case "WECHAT":
                payWay = PayWay.WECHAT;
                break;
            case "UNIONPAY":
                payWay = PayWay.UNIONPAY;
                break;
            case "UNIONPAY_B2B":
                payWay = PayWay.UNIONPAY_B2B;
                break;
            case "BALANCE":
                payWay = PayWay.BALANCE;
                break;
            default:
                payWay = PayWay.CASH;
        }
        reconciliation.setPayWay(payWay);
        reconciliation.setReturnOrderCode(returnPileOrder.getId());
        reconciliation.setStoreId(returnPileOrder.getCompany().getStoreId());
        reconciliation.setSupplierId(returnPileOrder.getCompany().getCompanyInfoId());
        reconciliation.setType((byte) 1);
        accountRecordProvider.add(reconciliation);
    }

    /**
     * 释放囤货订单的商品数量
     * @param tid
     */
    private void freePileGoodsNum(String tid){
        PileTrade trade = pileTradeService.detail(tid);
        String customerId = trade.getBuyer().getId();
        List<TradeItem> tradeItems = trade.getTradeItems();
        tradeItems.stream().forEach(tradeItem -> {
            String spuId = tradeItem.getSpuId();
            String skuId = tradeItem.getSkuId();
            //购买数量
            Long num = tradeItem.getNum();
            //通过spuId+skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
            List<PilePurchase> pilePurchases = pilePurchaseRepository.queryPilePurchase(customerId, spuId, skuId);
            if(CollectionUtils.isNotEmpty(pilePurchases)){
                PilePurchase purchase = pilePurchases.stream().findFirst().get();
                purchase.setGoodsNum(purchase.getGoodsNum()+num);
                //加完以后
                pilePurchaseRepository.save(purchase);
            }
        });
    }

    /**
     * 释放囤货订单商品数量，非客户退单
     * @param returnItems
     * @param customerId
     */
    public void freePileGoodsNumByNotCustomer(List<ReturnItem> returnItems,String customerId){
        returnItems.stream().forEach(returnItem -> {
            //购买数量
            Long num = returnItem.getNum().longValue();
            //通过spuId+skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
            List<PilePurchase> pilePurchases = pilePurchaseRepository.getPilePurchase(customerId,returnItem.getSkuId());
            if(CollectionUtils.isNotEmpty(pilePurchases)){
                PilePurchase purchase = pilePurchases.stream().findFirst().get();
                purchase.setGoodsNum(purchase.getGoodsNum()+num);
                //加完以后
                pilePurchaseRepository.save(purchase);
            }
        });
    }

    /**
     * 释放订单商品库存
     *
     * @param returnPileOrder
     */
    private void freeStock(ReturnPileOrder returnPileOrder, PileTrade pileTrade) {

        //批量库存释放
        List<GoodsInfoPlusStockDTO> stockList = returnPileOrder.getReturnItems().stream().map(returnItem -> {
            GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
            dto.setStock(returnItem.getNum());
            dto.setGoodsInfoId(returnItem.getSkuId());
            dto.setWareId(returnPileOrder.getWareId());
            return dto;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pileTrade.getGifts())) {
            pileTrade.getGifts().forEach(gift -> {
                GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
                dto.setStock(BigDecimal.valueOf(gift.getNum()));
                dto.setGoodsInfoId(gift.getSkuId());
                dto.setWareId(pileTrade.getWareId());
                stockList.add(dto);
            });
        }

        if (CollectionUtils.isNotEmpty(stockList)) {
            goodsInfoProvider.batchPlusStock(GoodsInfoBatchPlusStockRequest.builder().stockList(stockList).wareId(returnPileOrder.getWareId()).build());
        }
    }

    /**
     * 拼团退单相应处理
     *
     * @param returnPileOrder
     * @param pileTrade
     */
    private void modifyGrouponInfo(final ReturnPileOrder returnPileOrder, final PileTrade pileTrade) {
        List<String> tradeItemIds =
                pileTrade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        TradeGroupon tradeGroupon = pileTrade.getTradeGroupon();
        //拼团订(退)单只可能有一个sku
        ReturnItem returnItem = returnPileOrder.getReturnItems().get(0);
        //step1. 修改订单拼团信息
        tradeGroupon.setReturnPrice(returnPileOrder.getReturnPrice().getActualReturnPrice());
        tradeGroupon.setReturnNum(returnItem.getNum().intValue());
        //step2. 修改拼团商品计数
        GrouponGoodsInfoReturnModifyRequest returnModifyRequest;
        if (RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND.equals(returnPileOrder.getDescription())) {
            returnModifyRequest = GrouponGoodsInfoReturnModifyRequest.builder()
                    .grouponActivityId(tradeGroupon.getGrouponActivityId())
                    .amount(returnPileOrder.getReturnPrice().getActualReturnPrice())
                    .goodsInfoId(returnItem.getSkuId())
                    .num(NumberUtils.INTEGER_ZERO)
                    .build();
        } else {
            returnModifyRequest = GrouponGoodsInfoReturnModifyRequest.builder()
                    .grouponActivityId(tradeGroupon.getGrouponActivityId())
                    .amount(returnPileOrder.getReturnPrice().getActualReturnPrice())
                    .goodsInfoId(returnItem.getSkuId())
                    .num(returnItem.getNum().intValue())
                    .build();
        }
        grouponGoodsInfoProvider.modifyReturnInfo(returnModifyRequest);
        //step3.修改拼团活动计数
        if (RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND.equals(returnPileOrder.getDescription()) ||
                RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND_USER.equals(returnPileOrder.getDescription())) {
            GrouponOrderStatus grouponOrderStatus = GrouponOrderStatus.FAIL;
            //自动成团设置未选中，活动到期参团失败，减待成团数，加失败数
            activityProvider.modifyStatisticsNumById(GrouponActivityModifyStatisticsNumByIdRequest.builder()
                    .grouponActivityId(tradeGroupon.getGrouponActivityId())
                    .grouponNum(1)
                    .grouponOrderStatus(grouponOrderStatus)
                    .build()
            );
            tradeGroupon.setGrouponOrderStatus(grouponOrderStatus);
        }
        //用户支付成功-立即退款 & 拼团失败：团状态更新为拼团失败
        if (RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND.equals(returnPileOrder.getDescription()) ||
                RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND_USER.equals(returnPileOrder.getDescription())) {
            GrouponInstance grouponInstance = grouponOrderService.getGrouponInstanceByActivityIdAndGroupon(tradeGroupon.getGrouponNo());
            if (tradeGroupon.getLeader()){
                grouponInstance.setGrouponStatus(GrouponOrderStatus.FAIL);
                grouponInstance.setFailTime(LocalDateTime.now());
                grouponOrderService.updateGrouponInstance(grouponInstance);
            }
        }
        //step4.减去拼团商品已购买数 todo :拼团的未做小数处理
        recordProvider.decrBuyNumByGrouponActivityIdAndCustomerIdAndGoodsInfoId(GrouponRecordDecrBuyNumRequest.builder()
                .customerId(returnPileOrder.getBuyer().getId())
                .grouponActivityId(tradeGroupon.getGrouponActivityId())
                .goodsInfoId(returnItem.getSkuId())
                .buyNum(returnItem.getNum().intValue())
                .build()
        );
    }

    /**
     * 在线退款
     *
     * @param returnPileOrder
     * @param refundOrder
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void refundOnline(ReturnPileOrder returnPileOrder, RefundOrder refundOrder, Operator operator) {
        try {
            RefundBill refundBill;
            PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                    TradeRecordByOrderCodeRequest(returnPileOrder.getId())).getContext();
            if (Objects.isNull(payTradeRecordResponse)) {
                PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
                String tid = pileTrade.getPayInfo().isMergePay() ? pileTrade.getParentId() : pileTrade.getId();
                payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(
                        new TradeRecordByOrderCodeRequest(tid)).getContext();
            }
            PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                    ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                    payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                    payTradeRecordResponse.getChannelItemId())).getContext();
            // 退款流水保存
            if ((refundBill = refundOrder.getRefundBill()) == null) {
                refundBill = new RefundBill();
                refundBill.setPayChannel(channelItemResponse.getName());
                refundBill.setPayChannelId(channelItemResponse.getId());
                refundBill.setActualReturnPrice(refundOrder.getReturnPrice());
                refundBill.setCreateTime(LocalDateTime.now());
                refundBill.setRefundId(refundOrder.getRefundId());
                refundBillService.save(refundBill);
            } else {
                refundBill.setPayChannel(channelItemResponse.getName());
                refundBill.setPayChannelId(channelItemResponse.getId());
                refundBill.setCreateTime(LocalDateTime.now());
                refundBillService.saveAndFlush(refundBill);
            }
            if (returnPileOrder.getReturnFlowState() != ReturnFlowState.COMPLETED) {
                refund(returnPileOrder.getId(), operator, returnPileOrder.getReturnPrice().getApplyStatus() ?
                        returnPileOrder.getReturnPrice().getApplyPrice() : refundBill.getActualReturnPrice());
                refundOrder.setRefundStatus(RefundStatus.FINISH);
                PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());

                // 更改订单状态
                refundOrderRepository.saveAndFlush(refundOrder);
                //保存退款对账明细
                String tradeNo = Objects.nonNull(payTradeRecordResponse)? payTradeRecordResponse.getTradeNo(): "";
                saveReconciliation(returnPileOrder, channelItemResponse.getChannel(), "", tradeNo);
                pileTradeService.returnCoupon(returnPileOrder.getTid());

                if (returnPileOrder.getPlatform().equals(Platform.CUSTOMER)){
                    //释放营销信息
                    pileTradeService.calMarketGoodsNum(pileTrade.getTradeItems(),true);
                }

                // 退单完成时，发送MQ消息
                ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                        .addFlag(Boolean.FALSE)
                        .customerId(returnPileOrder.getBuyer().getId())
                        .orderId(returnPileOrder.getTid())
                        .returnId(returnPileOrder.getId())
                        .build();
                returnOrderProducerService.returnPileOrderFlow(sendMQRequest);
                //囤货退款成功，添加流水表
                addStockWater(returnPileOrder);
                log.info("ReturnPileOrderService.refundOnline push kingdee:{}",returnPileOrder.getId());
                //推erp退货单
                orderProducerService.pushStockReturnGoodsKingdee(returnPileOrder.getId(),6 * 10L);
            }

        } catch (SbcRuntimeException e) {
            log.error("{}退单状态修改异常,error={}", returnPileOrder.getId(), e);
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
    }

    /**
     * 记入囤货流水表中
     * @param returnPileOrder
     */
    private void addStockWater(ReturnPileOrder returnPileOrder){
        if (Objects.isNull(returnPileOrder)){
            return;
        }
        log.info("ReturnPileOrderService.addStockWater Tid:{} id:{}",returnPileOrder.getTid(),returnPileOrder.getId());
        //囤货退款成功，添加流水表
        List<String> goodsInfos = returnPileOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        //通过囤货订单查找记录表
        List<PileStockRecord> pileStockRecordList = pileStockRecordRepository.getPileStockRecordByOrderCode(returnPileOrder.getTid(),goodsInfos);
        Long stockNum = pileStockRecordList.stream().filter(pileStockRecord -> pileStockRecord.getStockRecordNum().equals(pileStockRecord.getStockRecordRemainingNum())).count();
        log.info("ReturnPileOrderService.addStockWater pileStockRecordList.size:{} stockNum.intValue:{}",pileStockRecordList.size(),
                                                                                                            stockNum.intValue());
        if (pileStockRecordList != null && stockNum != null && pileStockRecordList.size() > 0 && pileStockRecordList.size() != stockNum.intValue()){
            returnPileOrder.getReturnItems().stream().forEach(returnItem -> {
                pileStockRecordList.stream().forEach(pileStockRecord -> {
                    if (returnItem.getSkuId().equals(pileStockRecord.getGoodsInfoId())){
                        //计算数量
                        Long num = returnItem.getNum().longValue();
                        pileStockRecord.setStockRecordRemainingNum(pileStockRecord.getStockRecordRemainingNum()+num);
                        pileStockRecord.setUpdateTime(LocalDateTime.now());
                        if (pileStockRecord.getStockRecordRemainingNum() == pileStockRecord.getStockRecordNum()){
                            pileStockRecord.setIsUse(1L);
                        }
                    }
                });
            });
            pileStockRecordRepository.saveAll(pileStockRecordList);
        }else {
            //订单对应不上情况
            //累加数量(订单对应不上)
            Map<String,Long> cumulativeNumber = new HashMap<>();
            //记录已使用的记录表
            List<PileStockRecord> takePileStockRecord = new ArrayList<>();
            log.info("ReturnPileOrderService.addStockWater goodsInfos:{} buyerId:{}",JSONObject.toJSONString(goodsInfos)
                                                                                        ,returnPileOrder.getBuyer().getId());
            List<PileStockRecord> pileStockRecords = pileStockRecordRepository.findSingleBackPileStockRecord(returnPileOrder.getBuyer().getId(),goodsInfos);
            if (pileStockRecords.size() > 0){
                returnPileOrder.getReturnItems().stream().forEach(returnItem -> {
                    pileStockRecords.stream().forEach(pileStockRecord -> {
                        if (returnPileOrder.getBuyer().getId().equals(pileStockRecord.getCustomerId())
                                && returnItem.getSkuId().equals(pileStockRecord.getGoodsInfoId())){
                            Long num = pileStockRecord.getStockRecordNum() - pileStockRecord.getStockRecordRemainingNum();
                            //某个商品已使用数量
                            Long cumulative = 0L;
                            if (cumulativeNumber.get(returnItem.getSkuId()) != null){
                                cumulative = cumulativeNumber.get(returnItem.getSkuId());
                            }
                            Long returnNum = 0L;
                            if (returnItem.getNum() != null){
                                returnNum = returnItem.getNum().longValue();
                            }
                            if (num > 0 && cumulative < returnNum){
                                PileStockRecord stockRecord = KsBeanUtil.convert(pileStockRecord,PileStockRecord.class);
                                stockRecord.setUpdateTime(LocalDateTime.now());
                                //退单商品数大于等于已使用和一行记录表数据，插入累计
                                if (returnNum-(cumulative+num) >= 0) {
                                    cumulativeNumber.put(returnItem.getSkuId(), cumulative + num);
                                    stockRecord.setStockRecordRemainingNum(pileStockRecord.getStockRecordNum());
                                    stockRecord.setIsUse(1L);
                                    takePileStockRecord.add(stockRecord);
                                }else {
                                    //取记录表一行数据的部分数据
                                    Long part = returnNum - cumulative;
                                    cumulativeNumber.put(returnItem.getSkuId(), cumulative + part);
                                    stockRecord.setStockRecordRemainingNum(pileStockRecord.getStockRecordRemainingNum()+part);
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

    /**
     * b2b线下退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    public void refundOffline(String rid, CustomerAccountAddOrModifyDTO customerAccount, RefundBill refundBill,
                              Operator operator) {
        // 查询退单信息
        ReturnPileOrder returnPileOrder = findById(rid);

        // 如果offlineAccount非空，新增后使用
        if (Objects.nonNull(customerAccount)) {

            CustomerAccountByCustomerIdRequest customerAccountByCustomerIdRequest =
                    CustomerAccountByCustomerIdRequest.builder().customerId(customerAccount.getCustomerId()).build();
            BaseResponse<CustomerAccountByCustomerIdResponse> integerBaseResponse =
                    customerAccountQueryProvider.countByCustomerId(customerAccountByCustomerIdRequest);
            //查询会员有几条银行账户信息
            Integer count = integerBaseResponse.getContext().getResult();
            if (null != count && count >= 5) {
                //会员最多有5条银行账户信息
                throw new SbcRuntimeException("K-010005");
            }

            CustomerAccountAddRequest customerAccountAddRequest = new CustomerAccountAddRequest();
            BeanUtils.copyProperties(customerAccount, customerAccountAddRequest);

            // 客户编号
            customerAccountAddRequest.setCustomerId(returnPileOrder.getBuyer().getId());
            customerAccountAddRequest.setEmployeeId(operator.getUserId());
            BaseResponse<CustomerAccountAddResponse> customerAccountAddResponseBaseResponse =
                    customerAccountProvider.add(customerAccountAddRequest);
            CustomerAccountAddResponse customerAccountAddResponse = customerAccountAddResponseBaseResponse.getContext();

            // 设置财务退款单号
            refundBill.setCustomerAccountId(customerAccountAddResponse.getCustomerAccountId());
        }

        // 根据退款单号查询财务退款单据的编号
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        refundBill.setRefundId(refundOrder.getRefundId());

        // 生成退款记录
        refundBillService.save(refundBill);

        // 退单状态修改
        refund(rid, operator, refundBill.getActualReturnPrice());

    }

    /**
     * 商家线下退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void supplierRefundOffline(String rid, CustomerAccountVO customerAccount, RefundBill refundBill, Operator
            operator) {
        // 查询退单信息
        ReturnPileOrder returnPileOrder = findById(rid);

        // 积分信息
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
        returnPileOrderService.updateReturnOrder(returnPileOrder);
    }

    /**
     * s2b线下退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void s2bBoosRefundOffline(String rid, RefundBill refundBill, Operator operator, String tid) {
        log.info("ReturnPileOrderService.s2bBoosRefundOffline rid:{}",rid);
        //修改退款单状态
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        Optional<RefundBill> result = refundBillService.save(refundBill);
        refundOrder.setRefundBill(result.get());

        // 线下退款完成，发送MQ消息
        ReturnPileOrder returnPileOrder = this.findById(rid);
        refund(rid, operator, refundBill.getActualReturnPrice());
        refundOrder.setRefundStatus(RefundStatus.FINISH);
        refundOrderRepository.saveAndFlush(refundOrder);
        pileTradeService.returnCoupon(tid);

        //释放营销库存
        PileTrade trade = pileTradeService.detail(tid);
        pileTradeService.calMarketGoodsNum(trade.getTradeItems(),true);

        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnPileOrder.getBuyer().getId())
                .orderId(returnPileOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnPileOrderFlow(sendMQRequest);
        //囤货退款记录表
        addStockWater(returnPileOrder);
        log.info("ReturnPileOrderService.s2bBoosRefundOffline push kingdee:{}",returnPileOrder.getId());
        //推erp退货单
        orderProducerService.pushStockReturnGoodsKingdee(returnPileOrder.getId(),6 * 10L);
    }

    /**
     * 拒绝退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void refundReject(String rid, String reason, Operator operator) {
        ReturnPileOrder returnPileOrder = findById(rid);
        TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                (returnPileOrder.getTid(), returnPileOrder.getId())).getContext().getTradeStatus();
        if (tradeStatus != null) {
            if (tradeStatus == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100104");
            } else if (tradeStatus == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            }
        }
        //修改财务退款单状态
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        if (refundOrder.getRefundStatus().equals(RefundStatus.APPLY)) {
            throw new SbcRuntimeException("K-050002");
        }
        refundOrderService.refuse(refundOrder.getRefundId(), reason);

        //释放囤货订单非客户退单的商品数量
        if (ReturnType.RETURN.equals(returnPileOrder.getReturnType())){
            freePileGoodsNumByNotCustomer(returnPileOrder.getReturnItems(),returnPileOrder.getBuyer().getId());
        }

        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.REJECT_REFUND)
                .data(reason)
                .build();
        returnFSMService.changePileState(request);

        // 拒绝退款时，发送MQ消息
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnPileOrder.getBuyer().getId())
                .orderId(returnPileOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnPileOrderFlow(sendMQRequest);

        //退款审核未通过发送MQ消息
        List<String> params = Lists.newArrayList(returnPileOrder.getReturnItems().get(0).getSkuName(),reason);
        String pic = returnPileOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.REFUND_CHECK_NOT_PASS,
                params,
                returnPileOrder.getId(),
                returnPileOrder.getBuyer().getId(),
                pic,
                returnPileOrder.getBuyer().getAccount());
    }

    /**
     * 拒绝退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    public void refundRejectAndRefuse(String rid, String reason, Operator operator) {
        refundOrderService.refuse(rid, reason);
        refundOrderService.findById(rid).ifPresent(refundOrderResponse -> {
            ReturnPileOrder returnPileOrder = findById(refundOrderResponse.getReturnOrderCode());
            TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                    (returnPileOrder.getTid(), returnPileOrder.getId())).getContext().getTradeStatus();
            if (tradeStatus != null) {
                if (tradeStatus == TradeStatus.SUCCEED) {
                    throw new SbcRuntimeException("K-100104");
                } else if (tradeStatus == TradeStatus.PROCESSING) {
                    throw new SbcRuntimeException("K-100105");
                }
            }
            //修改财务退款单状态
            RefundOrder refundOrder =
                    refundOrderService.findRefundOrderByReturnOrderNo(refundOrderResponse.getReturnOrderCode());
            if (refundOrder.getRefundStatus().equals(RefundStatus.APPLY)) {
                throw new SbcRuntimeException("K-050002");
            }
            //修改退单状态
            ReturnStateRequest request = ReturnStateRequest
                    .builder()
                    .rid(refundOrderResponse.getReturnOrderCode())
                    .operator(operator)
                    .returnEvent(ReturnEvent.REJECT_REFUND)
                    .data(refundOrderResponse.getRefuseReason())
                    .build();
            returnFSMService.changePileState(request);
        });
    }

    /**
     * 驳回退单
     *
     * @param rid
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void cancel(String rid, Operator operator, String remark) {
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.VOID)
                .data(remark)
                .build();
        returnFSMService.changePileState(request);

        // 取消退单时，发送MQ消息
        ReturnPileOrder returnPileOrder = this.findById(rid);
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnPileOrder.getBuyer().getId())
                .orderId(returnPileOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnPileOrderFlow(sendMQRequest);

        //售后审核未通过发送MQ消息
        List<String> params = Lists.newArrayList(returnPileOrder.getReturnItems().get(0).getSkuName(), remark);
        String pic = returnPileOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_CHECK_NOT_PASS,
                params,
                returnPileOrder.getId(),
                returnPileOrder.getBuyer().getId(),
                pic,
                returnPileOrder.getBuyer().getAccount());

        log.info("==============》用户取消囤货退款订单释放囤货数量start ：{}《==============",returnPileOrder.getTid());
        if(ReturnType.REFUND.equals(returnPileOrder.getReturnType())){
            freePileGoodsNum(returnPileOrder.getTid());
        }else {
            freePileGoodsNumByNotCustomer(returnPileOrder.getReturnItems(),returnPileOrder.getBuyer().getId());
        }
        log.info("==============》用户取消囤货退款订单释放囤货数量end : {}《==============",returnPileOrder.getTid());

    }

    /**
     * 退款单作废状态扭转
     */
    @Transactional
    public void reverse(String rid, Operator operator) {
        //删除对账记录
        AccountRecordDeleteByReturnOrderCodeAndTypeRequest deleteRequest = new
                AccountRecordDeleteByReturnOrderCodeAndTypeRequest();
        deleteRequest.setReturnOrderCode(rid);
        deleteRequest.setAccountRecordType(AccountRecordType.REFUND);
        accountRecordProvider.deleteByReturnOrderCodeAndType(deleteRequest);
        ReturnPileOrder returnPileOrder = returnPileOrderRepository.findById(rid).orElse(new ReturnPileOrder());
        ReturnEvent event = returnPileOrder.getReturnType() == ReturnType.RETURN ? ReturnEvent.REVERSE_RETURN :
                ReturnEvent.REVERSE_REFUND;
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(event)
                .build();
        returnFSMService.changePileState(request);
    }

    /**
     * 修改退单
     *
     * @param newReturnPileOrder
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void remedy(ReturnPileOrder newReturnPileOrder, Operator operator) {
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(newReturnPileOrder.getId())
                .operator(operator)
                .returnEvent(ReturnEvent.REMEDY)
                .data(newReturnPileOrder)
                .build();
        returnFSMService.changePileState(request);
    }

    /**
     * 更新退单的业务员
     *
     * @param employeeId 业务员
     * @param customerId 客户
     */
    public void updateEmployeeId(String employeeId, String customerId) {
        mongoTemplate.updateMulti(new Query(Criteria.where("buyer.id").is(customerId)), new Update().set("buyer" +
                ".employeeId", employeeId), ReturnPileOrder.class);
    }

    /**
     * 完善没有业务员的退单
     */
    public void fillEmployeeId() {
        List<ReturnPileOrder> trades = mongoTemplate.find(new Query(Criteria.where("buyer.employeeId").is(null)),
                ReturnPileOrder.class);
        if (CollectionUtils.isEmpty(trades)) {
            return;
        }
        List<String> buyerIds = trades.stream()
                .filter(t -> Objects.nonNull(t.getBuyer()) && StringUtils.isNotBlank(t.getBuyer().getId()))
                .map(ReturnPileOrder::getBuyer)
                .map(Buyer::getId)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(buyerIds)) {
            return;
        }

        Map<String, String> customerId = customerCommonService.listCustomerDetailByCondition(
                        CustomerDetailListByConditionRequest.builder().customerIds(buyerIds).build())
                .stream()
                .filter(customerDetail -> StringUtils.isNotBlank(customerDetail.getEmployeeId()))
                .collect(Collectors.toMap(CustomerDetailVO::getCustomerId, CustomerDetailVO::getEmployeeId));

        customerId.forEach((key, value) -> this.updateEmployeeId(value, key));
    }

    /**
     * 关闭退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void closeRefund(String rid, Operator operator) {
        ReturnPileOrder returnPileOrder = findById(rid);
        TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                (returnPileOrder.getTid(), returnPileOrder.getId())).getContext().getTradeStatus();
        if (tradeStatus != null) {
            if (tradeStatus == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100104");
            } else if (tradeStatus == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            }
        }

        //修改财务退款单状态
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        refundOrder.setRefundStatus(RefundStatus.FINISH);
        refundOrderRepository.saveAndFlush(refundOrder);

        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.CLOSE_REFUND)
                .build();
        returnFSMService.changePileState(request);

        PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
        pileTrade.setRefundFlag(true);
        pileTradeService.updateTrade(pileTrade);
        // 作废订单也需要释放库存
        if (ReturnType.REFUND.equals(returnPileOrder.getReturnType())){
            freePileGoodsNum(returnPileOrder.getTid());
        }else {
            freePileGoodsNumByNotCustomer(returnPileOrder.getReturnItems(),returnPileOrder.getBuyer().getId());
        }
        //作废订单
        pileTradeService.voidTrade(returnPileOrder.getTid(), operator);
        pileTrade.getTradeState().setEndTime(LocalDateTime.now());
    }

    /**
     * 根据退单ID在线退款
     * @param returnOrderCode
     * @param operator
     * @return
     */
    public List<Object> refundOnlineByTid(String returnOrderCode,Operator operator,Boolean wmsStats){

        //查询退单
        ReturnPileOrder returnPileOrder = returnPileOrderService.findById(returnOrderCode);

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
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrderCode);
        PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
        if (Objects.nonNull(pileTrade) && Objects.nonNull(pileTrade.getBuyer()) && StringUtils.isNotEmpty(pileTrade.getBuyer()
                .getAccount())) {
            pileTrade.getBuyer().setAccount(ReturnOrderService.getDexAccount(pileTrade.getBuyer().getAccount()));
        }

        if (Objects.isNull(operator) || StringUtils.isBlank(operator.getUserId())) {
            operator = Operator.builder().ip("127.0.0.1").adminId("1").name("system").platform(Platform
                    .BOSS).build();
        }

        //添加wms货物不足退款标识
        if(Objects.nonNull(wmsStats)){
            returnPileOrder.setWmsStats(wmsStats);
        }

        return refundOrderService.autoRefundPile(Collections.singletonList(pileTrade),Collections.singletonList(returnPileOrder),Collections.singletonList(refundOrder),operator);
    }

    /**
     * 退款失败
     *
     * @param refundOrderRefundRequest
     */
    @Transactional
    @LcnTransaction
    public void refundFailed(RefundOrderRefundRequest refundOrderRefundRequest) {
        ReturnPileOrder returnPileOrder = returnPileOrderRepository.findById(refundOrderRefundRequest.getRid()).orElse(null);
        if (Objects.isNull(returnPileOrder)){
            log.error("退单ID:{},查询不到退单信息",refundOrderRefundRequest.getRid());
            return;
        }
        ReturnFlowState flowState = returnPileOrder.getReturnFlowState();
        // 如果已是退款状态的订单，直接return，不做状态扭转处理
        if (flowState == ReturnFlowState.REFUND_FAILED) {
            return;
        }
        returnPileOrder.setRefundFailedReason(refundOrderRefundRequest.getFailedReason());
        returnPileOrderService.updateReturnOrder(returnPileOrder);
        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(refundOrderRefundRequest.getRid())
                .operator(refundOrderRefundRequest.getOperator())
                .returnEvent(ReturnEvent.REFUND_FAILED)
                .build();
        returnFSMService.changePileState(request);
    }

    /**
     * 查询退单快照
     *
     * @param userId
     * @return
     */
    public ReturnPileOrder findTransfer(String userId) {
        ReturnPileOrder returnPileOrder = null;
        ReturnPileOrderTransfer returnPileOrderTransfer = returnPileOrderTransferRepository.findReturnOrderTransferByBuyerId
                (userId);
        if (returnPileOrderTransfer != null) {
            returnPileOrder = new ReturnPileOrder();
            KsBeanUtil.copyProperties(returnPileOrderTransfer, returnPileOrder);
        }
        return returnPileOrder;
    }

    /**
     * 根据动态条件统计
     *
     * @param request
     * @return
     */
    public long countNum(ReturnQueryRequest request) {
        Query query = new Query(request.build());
        return mongoTemplate.count(query, ReturnPileOrder.class);
    }

    /**
     * 分页查询退单列表
     *
     * @param request
     * @return
     */
    public Page<ReturnPileOrder> page(ReturnQueryRequest request) {
        Query query = new Query(request.build());
        long total = this.countNum(request);
        if (total < 1) {
            return new PageImpl<>(new ArrayList<>(), request.getPageRequest(), total);
        }
        request.putSort(request.getSortColumn(), request.getSortRole());
        List<ReturnPileOrder> returnPileOrderList = mongoTemplate.find(query.with(request.getPageRequest()), ReturnPileOrder.class);
        fillActualReturnPrice(returnPileOrderList);

        // 填充退款单状态
        if (CollectionUtils.isNotEmpty(returnPileOrderList)) {
            List<String> ridList = returnPileOrderList.stream().map(ReturnPileOrder::getId).collect(Collectors.toList());
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
    private void fillActualReturnPrice(Iterable<ReturnPileOrder> iterable) {
        try {
            List<String> returnOrderCodes = new ArrayList<>();
            // 如果有已退款的，查询退款流水的金额
            iterable.forEach(returnPileOrder -> {
                if (returnPileOrder.getReturnFlowState() == ReturnFlowState.COMPLETED) {
                    returnOrderCodes.add(returnPileOrder.getId());
                }
            });

            if (returnOrderCodes.size() > 0) {
                RefundOrderRequest request = new RefundOrderRequest();
                request.setReturnOrderCodes(returnOrderCodes);
                // 查询退款单信息
                List<RefundOrder> refundOrderList = refundOrderService.findAll(request);

                if (!CollectionUtils.isEmpty(refundOrderList)) {

                    // 实退金额赋值
                    iterable.forEach(returnPileOrder ->
                            refundOrderList.stream()
                                    .filter(o -> Objects.equals(returnPileOrder.getId(), o.getReturnOrderCode()))
                                    .findFirst()
                                    .ifPresent(o -> {
                                        if (Objects.nonNull(o.getRefundBill())) {
                                            returnPileOrder.getReturnPrice().setActualReturnPrice(o.getRefundBill()
                                                    .getActualReturnPrice());
                                        }
                                    }));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<ReturnPileOrder> findByCondition(ReturnQueryRequest request) {
        return mongoTemplate.find(new Query(request.build()), ReturnPileOrder.class);
    }

    /**
     * 查询可退金额
     *
     * @param rid
     * @return
     */
    public BigDecimal queryRefundPrice(String rid) {
        ReturnPileOrder returnPileOrder = findById(rid);
        return returnPileOrder.getReturnPrice().getTotalPrice();
    }

    /**
     * 功能描述: <br>排除特价商品重新计算可退货数量
     * 〈〉
     * @Param: [trade]
     * @Return: java.lang.Boolean
     * @Date: 2020/9/25 10:46
     */
    public Boolean removeSpecial(PileTrade pileTrade){
        //排除特价商品

        // trade.getTradeItems().removeIf(next -> Objects.nonNull(next.getGoodsInfoType()) && next.getGoodsInfoType() == 1);
        //计算商品可退数
        Map<String, BigDecimal> map = findLeftItems(pileTrade);
        BigDecimal total=BigDecimal.ZERO;
        Optional<BigDecimal> goods = map.values().stream().reduce(BigDecimal::add);
        if (goods.isPresent()){
            total=total.add(goods.get());
        }
        //计算赠品可退数
        if (CollectionUtils.isNotEmpty(pileTrade.getGifts())) {
            Map<String, BigDecimal> giftItems = findLeftGiftItems(pileTrade);
            Optional<BigDecimal> gift = giftItems.values().stream().reduce(BigDecimal::add);
            if (gift.isPresent()){
                total=total.add(gift.get());
            }
        }
        if (total.compareTo(BigDecimal.ZERO)==0){
            return false;
        }
        return true;
    }

    /**
     * 查询退单详情,如已发货则带出可退商品数
     *
     * @param rid
     * @return
     */
    public ReturnPileOrder queryCanReturnItemNumById(String rid) {
        ReturnPileOrder returnPileOrder = findById(rid);
        PileTrade pileTrade = pileTradeService.detail(returnPileOrder.getTid());
        if (pileTrade.getTradeState().getDeliverStatus() != DeliverStatus.NOT_YET_SHIPPED && pileTrade.getTradeState()
                .getDeliverStatus() != DeliverStatus.VOID) {
            //计算商品可退数
            Map<String, BigDecimal> map = findLeftItems(pileTrade);
            returnPileOrder.getReturnItems().forEach(item -> item.setCanReturnNum(map.get(item.getSkuId())));
        }
        return returnPileOrder;
    }

    /**
     * 根据订单id查询所有退单
     *
     * @param tid
     * @return
     */
    public List<ReturnPileOrder> findReturnByTid(String tid) {
        List<ReturnPileOrder> returnPileOrders = returnPileOrderRepository.findByTid(tid);
        return returnPileOrders == null ? Collections.emptyList() : returnPileOrders;
    }

    /**
     * 分页
     *
     * @param endDate
     * @return
     */
    public int countReturnOrderByEndDate(LocalDateTime endDate, ReturnFlowState returnFlowState) {
        Query query = getReturnOrderQuery(endDate, returnFlowState);
        return mongoTemplate.find(query, ReturnPileOrder.class).size();
    }

    /**
     * 构建查询条件
     *
     * @param endDate endDate
     * @return Query
     */
    private Query getReturnOrderQuery(LocalDateTime endDate, ReturnFlowState returnFlowState) {
        Criteria criteria = new Criteria();
        if (ReturnFlowState.DELIVERED.equals(returnFlowState)) {
            Criteria expressCriteria = Criteria.where("returnFlowState").is("DELIVERED")
                    .and("returnType").is("RETURN").and("returnWay").is("EXPRESS").and("returnLogistics.createTime")
                    .lte(endDate);
            Criteria otherCriteria = Criteria.where("returnFlowState").is("DELIVERED")
                    .and("returnWay").is("OTHER").and("returnType").is("RETURN").and("auditTime").lte(endDate);
            criteria.orOperator(expressCriteria, otherCriteria);
        } else {
            criteria = Criteria.where("returnFlowState").is("INIT").and("createTime").lte(endDate);
        }

        return new Query(criteria);
    }

    /**
     * 分页查询退单
     *
     * @param endDate
     * @param start
     * @param end
     * @return
     */
    public List<ReturnPileOrder> queryReturnOrderByEndDate(LocalDateTime endDate, int start, int end, ReturnFlowState
            returnFlowState) {
        Query query = getReturnOrderQuery(endDate, returnFlowState);
        return mongoTemplate.find(query, ReturnPileOrder.class).subList(start, end);
    }

    //推erp退货单
    public void pushKingdeeReturnGoods(ReturnPileOrder returnPileOrder,Boolean threadPool,PayWay payWay){
        log.info("ReturnPileOrderService.pushKingdeeReturnGoods rid:{}",returnPileOrder.getId());
        PileTradePushReturnGoodsRequest pushReturnGoodsRequest = new PileTradePushReturnGoodsRequest();
        pushReturnGoodsRequest.setThreadPool(threadPool);
        //获取金蝶登录token
        Map<String,Object> requestLogMap = new HashMap<>();
        requestLogMap.put("user",kingdeeUser);
        requestLogMap.put("pwd",kingdeePwd);
        String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap,loginUrl);
        if (StringUtils.isNotEmpty(loginToken)){
            //添加表头部分
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(returnPileOrder.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.isNull(employeeResponse) || StringUtils.isEmpty(employeeResponse.getErpEmployeeId())){
                log.info("ReturnPileOrderService.pushKingdeeReturnGoods Lack employeeResponse");
                return;
            }
            pushReturnGoodsRequest.setLoginToken(loginToken);
            pushReturnGoodsRequest.setFBillNo(returnPileOrder.getId());
            pushReturnGoodsRequest.setFDate(DateUtil.nowDate());
            Map fSaleOrgId = new HashMap();
            fSaleOrgId.put("FNumber",kingdeeOrganization);
            pushReturnGoodsRequest.setFSaleOrgId(fSaleOrgId);
            Map fCustId = new HashMap();
            fCustId.put("FNumber",returnPileOrder.getBuyer().getAccount());
            pushReturnGoodsRequest.setFCustId(fCustId);
            Map fSalerId = new HashMap();
            fSalerId.put("FNumber",employeeResponse.getErpEmployeeId());
            pushReturnGoodsRequest.setFSalerId(fSalerId);
            if (Objects.nonNull(returnPileOrder.getReturnReason())) {
               pushReturnGoodsRequest.setFNote(returnPileOrder.getReturnReason().getDesc());
            }
            if (payWay != null && (payWay.equals(PayWay.ALIPAY) || payWay.equals(PayWay.WECHAT) || payWay.equals(PayWay.UNIONPAY))){
                Map fBankAccount = new HashMap();
                fBankAccount.put("FNumber",kingdeeUnionpay);
                pushReturnGoodsRequest.setFBankAccount(fBankAccount);
            }

            Map fSetType = new HashMap();
            if (payWay.equals(PayWay.CASH)){
                //先下支付
                fSetType.put("FNumber","offlinepay");
                pushReturnGoodsRequest.setFSetType(fSetType);
            }else {
                fSetType.put("FNumber",payWay.toValue());
                pushReturnGoodsRequest.setFSetType(fSetType);
            }
            //收款方式
            if (returnPileOrder.getPayType().equals(PayType.OFFLINE)){
                pushReturnGoodsRequest.setFCollectType("线下");
            }else {
                pushReturnGoodsRequest.setFCollectType("线上");
            }
            pushReturnGoodsRequest.setFSaleNum(returnPileOrder.getTid());
            List<PileTradePushReturnGoodsTableBodyRequest> pushReturnGoodsTableBodyRequestList = new ArrayList<>();
            if (returnPileOrder.getReturnItems().size() > 0){
                returnPileOrder.getReturnItems().stream().forEach(returnItem -> {
                    PileTradePushReturnGoodsTableBodyRequest tableBodyRequest = new PileTradePushReturnGoodsTableBodyRequest();
                    Map FMaterialId = new HashMap();
                    FMaterialId.put("FNumber",returnItem.getErpSkuNo());
                    tableBodyRequest.setFMaterialId(FMaterialId);
                    tableBodyRequest.setFQty(returnItem.getNum().longValue());
                    Map fStockId = new HashMap();
                    if (Objects.nonNull(returnItem.getGoodsInfoType()) && 1 == returnItem.getGoodsInfoType()){
                        fStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                    }else {
                        fStockId.put("FNumber",ERPWMSConstants.MAIN_WH);
                    }
                    tableBodyRequest.setFStockId(fStockId);//仓库
                    if (Objects.nonNull(returnItem.getGoodsBatchNo())) {
                        Map FLot = new HashMap();
                        FLot.put("FNumber", returnItem.getGoodsBatchNo());
                        tableBodyRequest.setFLot(FLot);
                        tableBodyRequest.setFExpiryDate(returnItem.getGoodsBatchNo());
                    }
                    BigDecimal price = returnItem.getSplitPrice().divide(returnItem.getNum(), 6, BigDecimal.ROUND_DOWN);
                    tableBodyRequest.setFTaxPrice(price);
                    pushReturnGoodsTableBodyRequestList.add(tableBodyRequest);
                });
            }
            pushReturnGoodsRequest.setPushReturnGoodsTableBodyRequestList(pushReturnGoodsTableBodyRequestList);
            pushOrderKingdeeProvider.pushReturnGoodsKingdee(pushReturnGoodsRequest);
        }
    }
}
