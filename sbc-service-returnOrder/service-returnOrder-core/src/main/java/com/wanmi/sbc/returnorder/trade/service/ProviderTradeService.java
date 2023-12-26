package com.wanmi.sbc.returnorder.trade.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.GenerateExcelSendEmailVo;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.api.request.trade.TradeUpdateRequest;
import com.wanmi.sbc.returnorder.bean.enums.*;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.common.OperationLogMq;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.entity.value.ShippingItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradeEventLog;
import com.wanmi.sbc.returnorder.trade.model.root.ProviderTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.repository.ProviderTradeRepository;
import com.wanmi.sbc.returnorder.trade.request.ProviderTradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.request.TradeDeliverRequest;
import com.wanmi.sbc.returnorder.util.EmailOrderUtil;
import com.wanmi.sbc.returnorder.util.TradeExportUtil;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.EmailConfigProvider;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import com.wanmi.sbc.setting.bean.enums.EmailStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 供应商订单处理服务层
 * @Autho qiaokang
 * @Date：2020-02-11 22:56
 */
@Service
@Slf4j
public class ProviderTradeService {

    @Autowired
    private ProviderTradeRepository providerTradeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeEmailService tradeEmailService;

    @Autowired
    private EmailConfigProvider emailConfigProvider;

    @Autowired
    private TradeExportUtil tradeExportUtil;

    @Autowired
    private EmailOrderUtil emailOrderUtil;


    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param trade
     */
    @MongoRollback(persistence = ProviderTrade.class, operation = Operation.ADD)
    public void addProviderTrade(ProviderTrade trade) {
        providerTradeRepository.save(trade);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param trade
     */
    @MongoRollback(persistence = ProviderTrade.class, operation = Operation.UPDATE)
    public void updateProviderTrade(ProviderTrade trade) {
        providerTradeRepository.save(trade);
    }

    /**
     * 删除文档
     *
     * @param tid
     */
    @MongoRollback(persistence = ProviderTrade.class, idExpress = "tid", operation = Operation.UPDATE)
    public void deleteProviderTrade(String tid) {
        providerTradeRepository.deleteById(tid);
    }

    /**
     * 根据父订单号查询供货商订单
     *
     * @param parentTid
     */
    public List<ProviderTrade> findListByParentId(String parentTid) {
        return providerTradeRepository.findListByParentId(parentTid);
    }

    /**
     * 根据父订单列表查询供货商订单
     */
    public List<ProviderTrade> findListByParentIdList(List<String> parentTidList){
        return providerTradeRepository.findByParentIdIn(parentTidList);
    }

    /**
     *
     */
    public ProviderTrade findbyId(String id){
        return providerTradeRepository.findFirstById(id);
    }

//    /**
//     * 统计数量
//     *
//     * @param whereCriteria
//     * @param request
//     * @return
//     */
//    public long countNum(Criteria whereCriteria, TradeQueryRequest request) {
//        request.putSort(request.getSortColumn(), request.getSortRole());
//        Query query = new Query(whereCriteria);
//        long totalSize = mongoTemplate.count(query, ProviderTrade.class);
//        return totalSize;
//    }
    /**
     * 查询订单
     *
     * @param tid
     */
    public ProviderTrade providerDetail(String tid) {
        return providerTradeRepository.findById(tid).orElse(null);
    }

    /**
     * 更新订单
     *
     * @param tradeUpdateRequest
     */
    @LcnTransaction
    public void updateProviderTrade(TradeUpdateRequest tradeUpdateRequest) {
        this.updateProviderTrade(KsBeanUtil.convert(tradeUpdateRequest.getTrade(), ProviderTrade.class));
    }

    /**
     * 订单分页
     *
     * @param whereCriteria 条件
     * @param request       参数
     * @return
     */
    public Page<ProviderTrade> providerPage(Criteria whereCriteria, ProviderTradeQueryRequest request) {
        long totalSize = this.countNum(whereCriteria, request);
        if (totalSize < 1) {
            return new PageImpl<>(new ArrayList<>(), request.getPageRequest(), totalSize);
        }
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        return new PageImpl<>(mongoTemplate.find(query.with(request.getPageRequest()), ProviderTrade.class), request
                .getPageable(), totalSize);
    }

    /**
     * 统计数量
     *
     * @param whereCriteria
     * @param request
     * @return
     */
    public long countNum(Criteria whereCriteria, ProviderTradeQueryRequest request) {
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        long totalSize = mongoTemplate.count(query, ProviderTrade.class);
        return totalSize;
    }

    /**
     * 取消供应商订单
     *
     * @param parentId 父订单id
     * @param operator 操作人
     * @param isAuto   是否定时取消
     */
    @Transactional
    @LcnTransaction
    public void providerCancel(String parentId, Operator operator, boolean isAuto) {
        List<ProviderTrade> providerTradeList = this.findListByParentId(parentId);

        if (CollectionUtils.isNotEmpty(providerTradeList)) {
            String msg = "用户取消订单";
            if (isAuto) {
                msg = "订单超时未支付，系统自动取消";
            }
            final String data = msg;

            providerTradeList.forEach(providerTrade -> {
                // 更新供应商订单状态为已作废
                providerTrade.getTradeState().setFlowState(FlowState.VOID);
                providerTrade.getTradeState().setEndTime(LocalDateTime.now());
                providerTrade.appendTradeEventLog(new TradeEventLog(operator, "取消订单", data, LocalDateTime.now()));
                this.updateProviderTrade(providerTrade);
            });
        }
    }

    /**
     * 审核供应商订单
     *
     * @param parentId
     * @param reason
     * @param auditState
     */
    public void providerAudit(String parentId, String reason, AuditState auditState) {
        List<ProviderTrade> providerTradeList = this.findListByParentId(parentId);

        if (CollectionUtils.isNotEmpty(providerTradeList)) {
            providerTradeList.forEach(providerTrade -> {
                // 更新供应商订单状态为已作废
                providerTrade.getTradeState().setAuditState(auditState);
                if (AuditState.REJECTED == auditState) {
                    providerTrade.getTradeState().setObsoleteReason(reason);
                } else {
                    providerTrade.getTradeState().setFlowState(FlowState.AUDIT);
                }
                this.updateProviderTrade(providerTrade);
            });
        }

    }

    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tid                 订单id
     * @param tradeDeliverRequest 发货请求参数结构
     */
    public void deliveryCheck(String tid, TradeDeliverRequest tradeDeliverRequest) {
        ProviderTrade providerTrade = providerDetail(tid);
        Map<String, TradeItem> skusMap =
                providerTrade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                Function.identity()));
        Map<String, TradeItem> giftsMap =
                providerTrade.getGifts().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                Function.identity()));
        tradeDeliverRequest.getShippingItemList().forEach(i -> {
            TradeItem tradeItem = skusMap.get(i.getSkuId());
            if (tradeItem.getDeliveredNum() + i.getItemNum() > tradeItem.getNum()) {
                throw new SbcRuntimeException("K-050315");
            }
        });
        tradeDeliverRequest.getGiftItemList().forEach(i -> {
            TradeItem tradeItem = giftsMap.get(i.getSkuId());
            if (tradeItem.getDeliveredNum() + i.getItemNum() > tradeItem.getNum()) {
                throw new SbcRuntimeException("K-050315");
            }
        });
    }

    /**
     * 发货
     *
     * @param tid
     * @param tradeDeliver
     * @param operator
     * @return
     */
    public String deliver(String tid, TradeDeliver tradeDeliver, Operator operator) {
        ProviderTrade providerTrade = providerDetail(tid);
        //是否开启订单审核
        if (auditQueryProvider.isSupplierOrderAudit().getContext().isAudit() && providerTrade.getTradeState().getAuditState()
                != AuditState.CHECKED) {
            //只有已审核订单才能发货
            throw new SbcRuntimeException("K-050317");
        }
        // 先款后货并且未支付的情况下禁止发货
        if (providerTrade.getPaymentOrder() == PaymentOrder.PAY_FIRST && providerTrade.getTradeState().getPayState() == PayState.NOT_PAID && providerTrade.getPayInfo().getPayTypeId().equals(0)) {
            throw new SbcRuntimeException("K-050318");
        }
        if (verifyAfterProcessing(providerTrade.getParentId())) {
            throw new SbcRuntimeException("K-050114", new Object[]{providerTrade.getParentId()});
        }

        checkLogisticsNo(tradeDeliver.getLogistics().getLogisticNo(), tradeDeliver.getLogistics()
                .getLogisticStandardCode());

        // 生成ID
        tradeDeliver.setDeliverId(generatorService.generate("TD"));
        tradeDeliver.setStatus(DeliverStatus.NOT_YET_SHIPPED);
        tradeDeliver.setProviderName(providerTrade.getSupplier().getSupplierName());
        tradeDeliver.setTradeId(tid);

        List<TradeDeliver> tradeDelivers = providerTrade.getTradeDelivers();
        tradeDelivers.add(tradeDeliver);
        providerTrade.setTradeDelivers(tradeDelivers);

        providerTrade.getTradeItems().forEach(tradeItem -> {
            // 当前商品本次发货信息
            List<ShippingItem> shippingItems = tradeDeliver.getShippingItems().stream()
                    .filter(shippingItem -> tradeItem.getSkuId().equals(shippingItem.getSkuId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(shippingItems)) {
                // 当前商品发货数量加上本次发货数量
                tradeItem.setDeliveredNum(shippingItems.get(0).getItemNum() + tradeItem.getDeliveredNum());
                // 判断当前商品是否已全部发货
                if (tradeItem.getNum().equals(tradeItem.getDeliveredNum())) {
                    tradeItem.setDeliverStatus(DeliverStatus.SHIPPED);
                } else {
                    tradeItem.setDeliverStatus(DeliverStatus.PART_SHIPPED);
                }
            }
        });

        // 判断本次发货后，是否还有部分发货或未发货的商品，来设置订单发货状态
        Long partShippedNum = providerTrade.getTradeItems().stream()
                .filter(tradeItem -> (tradeItem.getDeliverStatus().equals(DeliverStatus.PART_SHIPPED) ||
                        tradeItem.getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED))).count();
        if (partShippedNum.intValue() != 0) {
            providerTrade.getTradeState().setFlowState(FlowState.DELIVERED_PART);
            providerTrade.getTradeState().setDeliverStatus(DeliverStatus.PART_SHIPPED);
        } else {
            providerTrade.getTradeState().setFlowState(FlowState.DELIVERED);
            providerTrade.getTradeState().setDeliverStatus(DeliverStatus.SHIPPED);
        }

        //添加操作日志
        String detail = String.format("订单[%s]已%s,操作人：%s", providerTrade.getId(), "发货", operator.getName());
        providerTrade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventType(TradeEvent.DELIVER.getDescription())
                .eventTime(LocalDateTime.now())
                .eventDetail(detail)
                .build());

        // 更新发货信息
        this.updateProviderTrade(providerTrade);

        return tradeDeliver.getDeliverId();
    }

    /**
     * 验证订单是否存在售后申请
     *
     * @param tid
     * @return true|false:存在售后，阻塞订单进程|不存在售后，订单进程正常
     */
    public boolean verifyAfterProcessing(String tid) {
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(tid);
        if (!CollectionUtils.isEmpty(returnOrders)) {
            // 查询是否存在正在进行中的退单(不是作废,不是拒绝退款,不是已结束)
            Optional<ReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnFlowState() !=
                    ReturnFlowState.VOID
                    && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                    && item.getReturnFlowState() != ReturnFlowState.COMPLETED).findFirst();
            if (optional.isPresent()) {
                return true;
            }

        }
        return false;
    }

    /**
     * 物流单号重复校验
     *
     * @param logisticsNo
     * @param logisticStandardCode
     */
    private void checkLogisticsNo(String logisticsNo, String logisticStandardCode) {
        if (StringUtils.isNotBlank(logisticsNo) && StringUtils.isNotBlank(logisticStandardCode)) {
            if (providerTradeRepository
                    .findTopByTradeDelivers_Logistics_LogisticNoAndTradeDelivers_Logistics_logisticStandardCode(logisticsNo,
                            logisticStandardCode)
                    .isPresent()) {
                throw new SbcRuntimeException("K-050124");
            }
        }
    }

    /**
     * 查询导出数据
     *
     * @param queryRequest
     */
    public List<ProviderTrade> listProviderTradeExport(ProviderTradeQueryRequest queryRequest) {
        long count = this.countNum(queryRequest.getWhereCriteria(), queryRequest);
        if (count > 1000) {
            count = 1000;
        }
        queryRequest.putSort(queryRequest.getSortColumn(), queryRequest.getSortRole());
        queryRequest.setPageNum(0);
        queryRequest.setPageSize((int) count);

        //设置返回字段
        Map fieldsObject = new HashMap();
        fieldsObject.put("_id", true);
        fieldsObject.put("parentId", true);
        fieldsObject.put("tradeState.createTime", true);
        fieldsObject.put("supplierName", true);
        fieldsObject.put("supplierCode", true);
        fieldsObject.put("consignee.name", true);
        fieldsObject.put("consignee.phone", true);
        fieldsObject.put("consignee.detailAddress", true);
        fieldsObject.put("deliverWay", true);
        fieldsObject.put("tradePrice.goodsPrice", true);


//        fieldsObject.put("tradePrice.special", true);
//        fieldsObject.put("tradePrice.privilegePrice", true);
        fieldsObject.put("tradePrice.totalPrice", true);
//        fieldsObject.put("tradeItems.oid", true);
        fieldsObject.put("tradeItems.skuId", true);
        fieldsObject.put("tradeItems.skuNo", true);
        fieldsObject.put("tradeItems.specDetails", true);
        fieldsObject.put("tradeItems.skuName", true);
        fieldsObject.put("tradeItems.num", true);
        fieldsObject.put("tradeItems.supplyPrice", true);

        fieldsObject.put("buyerRemark", true);
        fieldsObject.put("sellerRemark", true);
        fieldsObject.put("tradeState.flowState", true);
        fieldsObject.put("tradeState.payState", true);
        fieldsObject.put("tradeState.deliverStatus", true);
//        fieldsObject.put("invoice.type", true);
//        fieldsObject.put("invoice.projectName", true);
//        fieldsObject.put("invoice.generalInvoice.title", true);
//        fieldsObject.put("invoice.specialInvoice.companyName", true);
//        fieldsObject.put("supplier.supplierName", true);
        Query query = new BasicQuery(new Document(), new Document(fieldsObject));
        query.addCriteria(queryRequest.getWhereCriteria());
        System.err.println("mongo：  " + LocalDateTime.now());
        List<ProviderTrade> tradeList = mongoTemplate.find(query.with(queryRequest.getPageRequest()), ProviderTrade.class);

        System.err.println("mongo：  " + LocalDateTime.now());
        return tradeList;
    }

    /**
     * 查询订单集合
     *
     * @param tids
     */
    public List<ProviderTrade> details(List<String> tids) {
        return org.apache.commons.collections4.IteratorUtils.toList(providerTradeRepository.findAllById(tids).iterator());
    }

    /**
     * 查询全部订单
     *
     * @param request
     * @return
     */
    public List<ProviderTrade> queryAll(ProviderTradeQueryRequest request) {
        return mongoTemplate.find(new Query(request.getWhereCriteria()), ProviderTrade.class);
    }


    /**
     * 修改备注
     *
     * @param tid
     * @param buyerRemark
     */
    @TccTransaction
    public void remedyBuyerRemark(String tid, String buyerRemark, Operator operator) {
        //1、查找订单信息
        ProviderTrade providerTrade = providerDetail(tid);
        providerTrade.setBuyerRemark(buyerRemark);
        Trade trade = tradeService.detail(providerTrade.getParentId());
        providerTrade.appendTradeEventLog(new TradeEventLog(operator, "修改备注", "修改供应商订单备注", LocalDateTime.now()));
        trade.appendTradeEventLog(new TradeEventLog(operator, "修改备注", "修改供应商订单备注", LocalDateTime.now()));
        //保存
        providerTradeRepository.save(providerTrade);
        tradeService.updateTrade(trade);
        this.operationLogMq.convertAndSend(operator, "修改供应商订单备注", "修改供应商订单备注");
    }

    public String providerByidAndPid(String tid, String providerId) {
        String providerTradeId = StringUtils.EMPTY;
        List<ProviderTrade> providerTrades = this.findListByParentId(tid);
        for(ProviderTrade providerTrade:providerTrades){
            if(Long.parseLong(providerId) == providerTrade.getSupplier().getStoreId()){
                providerTradeId = providerTrade.getId();
            }
        }
        return providerTradeId;
    }


    /**
     * 发货记录作废
     *
     * @param tid
     * @param deliverId
     * @param operator
     */
    @Transactional
    public void deliverRecordObsolete(String tid, String deliverId, Operator operator) {

        ProviderTrade providerTrade = providerTradeRepository.findFirstById(tid);
        List<TradeDeliver> tradeDelivers = providerTrade.getTradeDelivers();
        //查询发货记录
        Optional<TradeDeliver> tradeDeliverOptional = tradeDelivers
                .stream()
                .filter(tradeDeliver -> StringUtils.equals(deliverId, tradeDeliver.getDeliverId()))
                .findFirst();

        if (tradeDeliverOptional.isPresent()) {
            StringBuilder stringBuilder = new StringBuilder(200);

            TradeDeliver tradeDeliver = tradeDeliverOptional.get();

            //处理商品
            handleShippingItems(providerTrade, tradeDeliver.getShippingItems(), stringBuilder, false);

            //订单状态更新
            TradeState tradeState = providerTrade.getTradeState();
            if (isAllNotShipped(providerTrade)) {
                tradeState.setFlowState(FlowState.AUDIT);
                tradeState.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
            } else {
                tradeState.setFlowState(FlowState.DELIVERED_PART);
                tradeState.setDeliverStatus(DeliverStatus.PART_SHIPPED);
            }

            //添加操作日志
            stringBuilder.trimToSize();
            providerTrade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventType(TradeEvent.OBSOLETE_DELIVER.getDescription())
                    .eventDetail(stringBuilder.toString())
                    .eventTime(LocalDateTime.now())
                    .build());

            //删除发货单
            tradeDelivers.remove(tradeDeliver);

            //保存
            providerTradeRepository.save(providerTrade);
            operationLogMq.convertAndSend(operator, TradeEvent.OBSOLETE_DELIVER.getDescription(), stringBuilder.toString());

        }
    }

    @Transactional
    public void handleShippingItems(ProviderTrade trade, List<ShippingItem> shippingItems, StringBuilder stringBuilder, boolean isGift) {
        ConcurrentHashMap<String, TradeItem> skuItemMap ;
        if(isGift){
            skuItemMap = trade.giftSkuItemMap();
        }else{
            skuItemMap = trade.skuItemMap();
        }

        //订单商品更新
        shippingItems.forEach(shippingItem -> {
            TradeItem tradeItem = skuItemMap.get(shippingItem.getSkuId());

            Long shippedNum = tradeItem.getDeliveredNum();
            shippedNum -= shippingItem.getItemNum();
            tradeItem.setDeliveredNum(shippedNum);

            if (shippedNum.equals(0L)) {
                tradeItem.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
            } else if (shippedNum < tradeItem.getNum()) {
                tradeItem.setDeliverStatus(DeliverStatus.PART_SHIPPED);
            }

            stringBuilder.append(String.format("订单[%s],商品[%s], 作废发货[%s], 目前状态:[%s]\r\n",
                    trade.getId(),
                    (isGift ? "【赠品】" : "" ) + tradeItem.getSkuName(),
                    shippingItem.getItemNum().toString(),
                    tradeItem.getDeliverStatus().getDescription())
            );

        });
    }

    /**
     * 是否全部未发货
     *
     * @param trade
     * @return
     */
    @Transactional
    public boolean isAllNotShipped(ProviderTrade trade) {
        List<TradeItem> allItems = new ArrayList<>();
        allItems.addAll(trade.getTradeItems());
        allItems.addAll(trade.getGifts());
        List<TradeItem> collect = allItems.stream()
                .filter(tradeItem -> !tradeItem.getDeliveredNum().equals(0L))
                .collect(Collectors.toList());
        return collect.isEmpty();
    }


    /**
     * 查询导出订单数据(需求是:导出T-1天的新用户首单数据)
     * 1. 查询T-2天所有的订单数据
     * 2. 最终要排队掉这些已下单成功的老用户
     */
    public List<Trade> queryOldUser() {
        //yyyy-MM-dd HH:mm:ss
        String beginTime="2018-01-01 00:00:00";
        LocalDateTime beginDate = DateUtil.parse(beginTime, DateUtil.FMT_TIME_1);
        String yesterdayDateString = DateUtil.yesterdayDate();
        LocalDateTime endDate = DateUtil.parseDay(yesterdayDateString);

        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("tradeState.createTime").gte(beginDate)
                ,Criteria.where("tradeState.createTime").lt(endDate)
                , Criteria.where("tradeState.payState").is("PAID")
        );
        Query query = new Query(criteria);
        List<Trade> tradeList = mongoTemplate.find(query, Trade.class);
        return tradeList;
    }

    public List<Trade> queryNewUser() {
        String yesterdayDateString = DateUtil.yesterdayDate();
        LocalDateTime beginDate = DateUtil.parseDay(yesterdayDateString);
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("tradeState.createTime").gte(beginDate)
                , Criteria.where("tradeState.payState").is("PAID")
        );
        Query query = new Query(criteria);
        //再按时间倒序排
        query.with(Sort.by(Sort.Order.desc("tradeState.createTime")));
        List<Trade> tradeList = mongoTemplate.find(query, Trade.class);
        return tradeList;
    }

    /**
     * 发送新用户订单
     * @param tradeVOList
     */
    public void sendNewUserOrder(List<TradeVO> tradeVOList, List<String> emails) {
        BaseResponse<EmailConfigQueryResponse> config = emailConfigProvider.queryEmailConfig();
        // 邮箱停用状态下直接返回
        if (config.getContext().getStatus() == EmailStatus.DISABLE) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream =  tradeExportUtil.exportToByteArrayOutputStream2(tradeVOList);
        } catch (Exception e) {
            log.error("sendEmailTranslate,", e);
        }

        GenerateExcelSendEmailVo vo = new GenerateExcelSendEmailVo<>();
        vo.setOut(byteArrayOutputStream);
        // 3.设置email的title
        vo.setEmailTitle("新用户首订单,详情请查看附件!");
        //4.设置email的内容
        vo.setEmailContent("新用户首订单,详情请查看附件!");
        // 5.设置收件人
        vo.setAcceptAddressList(emails);
        Long nanoTime = System.nanoTime();
        log.info("####新用户首单开始发送邮件#### : {} -- {}", emails, nanoTime);
        emailOrderUtil.sendOrderEmail2(vo,config);
    }
}
