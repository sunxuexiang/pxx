package com.wanmi.sbc.order.provider.impl.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.DisableCustomerDetailGetByAccountRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.response.customer.DisableCustomerDetailGetByAccountResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseByIdResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyByCustomerIdResponse;
import com.wanmi.sbc.order.api.response.inventorydetailsamount.InventoryDetailSamountResponse;
import com.wanmi.sbc.order.api.response.trade.*;
import com.wanmi.sbc.order.bean.constant.ConstantContent;
import com.wanmi.sbc.order.bean.dto.TmsOrderBillNoDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.ordertrack.root.OrderTrack;
import com.wanmi.sbc.order.ordertrack.service.OrderTrackService;
import com.wanmi.sbc.order.payorder.model.root.PayOrder;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.OrderSalesRanking;
import com.wanmi.sbc.order.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.entity.value.Consignee;
import com.wanmi.sbc.order.trade.model.entity.value.Logistics;
import com.wanmi.sbc.order.trade.model.entity.value.Supplier;
import com.wanmi.sbc.order.trade.model.root.*;
import com.wanmi.sbc.order.trade.reponse.TradeFreightResponse;
import com.wanmi.sbc.order.trade.reponse.TradeLogisticsReponse;
import com.wanmi.sbc.order.trade.reponse.TradeRemedyDetails;
import com.wanmi.sbc.order.trade.repository.OrderCountRepository;
import com.wanmi.sbc.order.trade.request.TradeDeliverRequest;
import com.wanmi.sbc.order.trade.request.*;
import com.wanmi.sbc.order.trade.service.PileStockRecordAttachmentService;
import com.wanmi.sbc.order.trade.service.ProviderTradeService;
import com.wanmi.sbc.order.trade.service.TradeLogisticsService;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.setting.api.provider.DeliveryQueryProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackMapResp;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 10:04
 */
@Slf4j
@RestController
public class TradeQueryController implements TradeQueryProvider {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private PileStockRecordAttachmentService pileStockRecordAttachmentService;

    @Autowired
    private TradeLogisticsService tradeLogisticsService;

    @Autowired
    private OrderTrackService orderTrackService;

    @Autowired
    private DeliveryQueryProvider deliveryQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;
    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;

    @Autowired
    private OrderCommonService orderCommonService;
    @Autowired
    private OrderCountRepository orderCountRepository;
    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;


    /**
     * @param tradePageRequest 分页参数 {@link TradePageRequest}
     * @return
     */
//    @Override
//    public BaseResponse<TradePageResponse> page(@RequestBody @Valid TradePageRequest tradePageRequest) {
//        Page<Trade> page = tradeService.page(tradePageRequest.getQueryBuilder(), tradePageRequest.getRequest());
//        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
//        return BaseResponse.success(TradePageResponse.builder().tradePage(tradeVOS).build());
//    }

    /**
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @Override
    public BaseResponse<TradePageCriteriaResponse> pageCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest) {
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradePageCriteriaRequest.getTradePageDTO(), TradeQueryRequest.class);
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<Trade> page = tradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
        //获取所有订单号
        formatTradeVO(tradeVOS.getContent());
        return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
    }

    /**
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @Override
    public BaseResponse<TradePageCriteriaResponse> supplierPageCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest) {
        //根据providerTradeId模糊查询ProviderTrade,获取tradeId
        if (StringUtils.isNotBlank(tradePageCriteriaRequest.getTradePageDTO().getProviderTradeId())
                || StringUtils.isNotBlank(tradePageCriteriaRequest.getTradePageDTO().getProviderName())) {
            List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder()
                    .providerName(tradePageCriteriaRequest.getTradePageDTO().getProviderName())
                    .id(tradePageCriteriaRequest.getTradePageDTO().getProviderTradeId()).build());
            if (CollectionUtils.isNotEmpty(providerTrades)) {
                List<String> tradeIds = providerTrades.stream().map(ProviderTrade::getParentId).collect(Collectors.toList());
                String[] ids = tradeIds.toArray(new String[tradeIds.size()]);
                tradePageCriteriaRequest.getTradePageDTO().setIds(ids);
            } else {
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(new MicroServicePage<TradeVO>()).build());
            }
        }
        tradePageCriteriaRequest.getTradePageDTO().setProviderTradeId(null);
        tradePageCriteriaRequest.getTradePageDTO().setProviderName(null);
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradePageCriteriaRequest.getTradePageDTO(), TradeQueryRequest.class);
        tradeQueryRequest.setDeletedFlag(0);
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<Trade> page = tradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
        List<TradeVO> tradeVOList = tradeVOS.getContent();
        if (CollectionUtils.isNotEmpty(tradeVOS.getContent())) {
            List<String> providerTradeIds = tradeVOS.getContent().stream().map(TradeVO::getId).collect(Collectors.toList());
            //查询所有的子订单(providerTrade表)
            List<ProviderTrade> providerTradeList = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentIds(providerTradeIds).build());
            //通过提货订单查询囤货订单
            List<StockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentService.getStockAssociatedOrderTid(providerTradeIds);
            log.info("TradeQueryController.supplierPageCriteria stockRecordAttachmentList:{}", JSONObject.toJSONString(stockRecordAttachmentList));
            List<String> orderList = pileStockRecordAttachmentService.getCachePushKingdeeOrder(providerTradeIds);
            Map<String, List<StockRecordAttachment>> stockRecordMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(stockRecordAttachmentList)) {
                stockRecordMap = stockRecordAttachmentList.stream().collect(Collectors.groupingBy(StockRecordAttachment::getTid));
            }
            log.info("TradeQueryController.supplierPageCriteria stockRecordMap:{}",JSONObject.toJSONString(stockRecordMap));
            if (CollectionUtils.isNotEmpty(providerTradeList)) {
                List<TradeVO> result = new ArrayList<>();
                //查询主订单编号列表
                tradeVOList.forEach(vo -> {
                    List<TradeVO> items = new ArrayList<>();
                    for (ProviderTrade item : providerTradeList) {
                        if (vo.getId().equals(item.getParentId())) {
                            TradeVO tradeVO = KsBeanUtil.convert(item, TradeVO.class);
                            items.add(tradeVO);
                        }
                    }
                    encapsulation(items);
                    vo.setTradeVOList(items);
                });

                for (TradeVO tradeVO : tradeVOList) {
                    Boolean isContainsTrade = false;
                    if (CollectionUtils.isNotEmpty(tradeVO.getTradeVOList())) {
                        List<Long> storeList = tradeVO.getTradeVOList().stream().map(TradeVO::getStoreId).collect(Collectors.toList());
                        isContainsTrade = storeList.contains(null);
                    }
                    tradeVO.setIsContainsTrade(isContainsTrade);
                    if (stockRecordMap != null && stockRecordMap.containsKey(tradeVO.getId())){
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getOrderCode).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("TradeQueryController.supplierPageCriteria tradeVO:{}",tradeVO.getStockOrder());
                    }

                    if (CollectionUtils.isNotEmpty(orderList)){
                        orderList.stream().forEach(cachePushKingdeeOrder -> {
                            if (tradeVO.getId().equals(cachePushKingdeeOrder)){
                                tradeVO.setIntercept(true);
                                tradeVO.setNewVilageFlag(true);
                            }
                        });
                    }

                }
                log.info("TradeQueryController.supplierPageCriteria tradeVOList:{}",JSONObject.toJSONString(tradeVOList));
                encapsulation(tradeVOList);
                tradeVOS.setContent(tradeVOList);
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
            } else {
                for (TradeVO tradeVO : tradeVOList) {
                    if (stockRecordMap != null && stockRecordMap.containsKey(tradeVO.getId())){
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getOrderCode).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("TradeQueryController.supplierPageCriteria tradeVO2:{}",tradeVO.getStockOrder());
                    }
                    if (CollectionUtils.isNotEmpty(orderList)){
                        orderList.stream().forEach(cachePushKingdeeOrder -> {
                            if (tradeVO.getId().equals(cachePushKingdeeOrder)){
                                tradeVO.setIntercept(true);
                                tradeVO.setNewVilageFlag(true);
                            }
                        });
                    }
                }
                encapsulation(tradeVOList);
                tradeVOS.setContent(tradeVOList);
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
            }
        }
        encapsulation(tradeVOList);
        tradeVOS.setContent(tradeVOList);
        return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
    }

    /**
     * @param tradePageCriteriaRequest Boss端带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @Override
    public BaseResponse<TradePageCriteriaResponse> pageBossCriteria(@RequestBody @Valid TradePageCriteriaRequest tradePageCriteriaRequest) {
        log.info("===================================================开始查询订单列表===============================================================");
        //查看支付订单号是否存在，如果存在，由支付订单查出对应的O订单，赋值O订单，如果O订单和支付订单一起，则以支付订单为主，查出O订单，匹配是否一样，不一样就返回无数据
        String payOrderNo = "";
        String queryOrderNo = tradePageCriteriaRequest.getTradePageDTO().getPayOrderNo();
        if(StringUtils.isNotEmpty(queryOrderNo) && queryOrderNo.length() > 4){
            PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
            String orderNo = queryOrderNo;
            /**
             * zhouzhenguo 20230720
             *  修改支持好友代付支付订单号查找。当好友代付支付订单搜索时，订单号没有“31YT”前缀，所以不需要截掉前四位
             */
            if (queryOrderNo.startsWith("31YT") || queryOrderNo.length() > 20) {
                orderNo = queryOrderNo.substring (4,queryOrderNo.length());
            }
            recordRequest.setPayOrderNo(orderNo);
            PayTradeRecordResponse record = payQueryProvider.findByPayOrderNo(recordRequest).getContext();
            if(Objects.isNull(record)){
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(new MicroServicePage<TradeVO>()).build());
            }
            Trade trade = tradeService.detail(record.getBusinessId());
            if(trade == null){
                List<Trade> trades = tradeService.detailsByParentId(record.getBusinessId());
                if(CollectionUtils.isEmpty(trades)){
                    return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(new MicroServicePage<TradeVO>()).build());
                }
                List<String>  ids = new ArrayList<>();
                for (Trade t: trades) {
                    ids.add(t.getId());
                }
                tradePageCriteriaRequest.getTradePageDTO().setIds(ids.toArray());
            }else{
                if(StringUtils.isEmpty(tradePageCriteriaRequest.getTradePageDTO().getId())){
                    tradePageCriteriaRequest.getTradePageDTO().setId(trade.getId());
                }else{
                    if(!trade.getId().equals(tradePageCriteriaRequest.getTradePageDTO().getId())){
                        return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(new MicroServicePage<TradeVO>()).build());
                    }
                }
            }
            payOrderNo = record.getPayOrderNo();
            tradePageCriteriaRequest.getTradePageDTO().setPayOrderNo(null);
        }

        //根据providerTradeId模糊查询ProviderTrade,获取tradeId
        if (StringUtils.isNotBlank(tradePageCriteriaRequest.getTradePageDTO().getProviderTradeId())
                || StringUtils.isNotBlank(tradePageCriteriaRequest.getTradePageDTO().getProviderName())
                || StringUtils.isNotEmpty(tradePageCriteriaRequest.getTradePageDTO().getProviderCode())) {
            List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder()
                    .providerName(tradePageCriteriaRequest.getTradePageDTO().getProviderName())
                    .providerCode(tradePageCriteriaRequest.getTradePageDTO().getProviderCode())
                    .id(tradePageCriteriaRequest.getTradePageDTO().getProviderTradeId()).build());
            if (CollectionUtils.isNotEmpty(providerTrades)) {
                List<String> tradeIds = providerTrades.stream().map(ProviderTrade::getParentId).collect(Collectors.toList());
                String[] ids = tradeIds.toArray(new String[tradeIds.size()]);
                tradePageCriteriaRequest.getTradePageDTO().setIds(ids);
            } else {
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(new MicroServicePage<TradeVO>()).build());
            }
        }
        tradePageCriteriaRequest.getTradePageDTO().setProviderTradeId(null);
        tradePageCriteriaRequest.getTradePageDTO().setProviderName(null);
        tradePageCriteriaRequest.getTradePageDTO().setProviderCode(null);
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradePageCriteriaRequest.getTradePageDTO(), TradeQueryRequest.class);
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<Trade> page = tradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
        List<TradeVO> tradeVOList = tradeVOS.getContent();
        if (CollectionUtils.isNotEmpty(tradeVOS.getContent())) {
            List<String> providerTradeIds = tradeVOS.getContent().stream().map(TradeVO::getId).collect(Collectors.toList());
            //查询所有的子订单(providerTrade表)
            List<ProviderTrade> providerTradeList = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentIds(providerTradeIds).build());
            //通过提货订单查询囤货订单
            List<StockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentService.getStockAssociatedOrderTid(providerTradeIds);
            log.info("TradeQueryController.pageBossCriteria stockRecordAttachmentList:{}", JSONObject.toJSONString(stockRecordAttachmentList));
            Map<String, List<StockRecordAttachment>> stockRecordMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(stockRecordAttachmentList)) {
                stockRecordMap = stockRecordAttachmentList.stream().collect(Collectors.groupingBy(StockRecordAttachment::getTid));
            }
            log.info("TradeQueryController.pageBossCriteria stockRecordMap:{}",JSONObject.toJSONString(stockRecordMap));
            if (CollectionUtils.isNotEmpty(providerTradeList)) {
                List<TradeVO> result = new ArrayList<>();
                //查询主订单编号列表
                tradeVOList.forEach(vo -> {
                    List<TradeVO> items = new ArrayList<>();
                    for (ProviderTrade item : providerTradeList) {
                        if (vo.getId().equals(item.getParentId())) {
                            TradeVO tradeVO = KsBeanUtil.convert(item, TradeVO.class);
                            items.add(tradeVO);
                        }
                    }
                    vo.setTradeVOList(items);
                });
                for (TradeVO tradeVO : tradeVOList) {
                    Boolean isContainsTrade = false;
                    if (CollectionUtils.isNotEmpty(tradeVO.getTradeVOList())) {
                        List<Long> storeList = tradeVO.getTradeVOList().stream().map(TradeVO::getStoreId).collect(Collectors.toList());
                        isContainsTrade = storeList.contains(null);
                    }
                    tradeVO.setIsContainsTrade(isContainsTrade);
                    if (stockRecordMap != null && stockRecordMap.containsKey(tradeVO.getId())){
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getOrderCode).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("TradeQueryController.pageBossCriteria tradeVO:{}",tradeVO.getStockOrder());
                    }
                }
                formatTradeVO(tradeVOList);
                encapsulation(tradeVOList);
                setPayOrderNo(payOrderNo,tradeVOList);
                tradeVOS.setContent(tradeVOList);
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
            } else {
                for (TradeVO tradeVO : tradeVOList) {
                    if (stockRecordMap != null && stockRecordMap.containsKey(tradeVO.getId())){
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getOrderCode).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("TradeQueryController.pageBossCriteria tradeVO2:{}",tradeVO.getStockOrder());
                    }
                }
                formatTradeVO(tradeVOList);
                encapsulation(tradeVOList);
                setPayOrderNo(payOrderNo,tradeVOList);
                tradeVOS.setContent(tradeVOList);
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
            }
        }
        encapsulation(tradeVOList);
        setPayOrderNo(payOrderNo,tradeVOList);

        formatTradeVO(tradeVOList);

        tradeVOS.setContent(tradeVOList);
        return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
    }

    private void setPayOrderNo(String payOrderNo,List<TradeVO> tradeVOS){
        if(CollectionUtils.isEmpty(tradeVOS)){
            return;
        }

        /**
         * zhouzhenguo 20230720
         *  修改支持好友代付支付订单号查找。当好友代付支付订单搜索时，前端会传递参数 payOrderNo 查询，
         *  这样导致将好友代付支付订单号增加“31YT”前缀，所以注释调
         */
//        if(payOrderNo != null && !payOrderNo.equals("")){
//            for (TradeVO tradeVO :tradeVOS) {
//                tradeVO.setPayOrderNo("31YT" + payOrderNo);
//            }
//        }else{
            for (TradeVO tradeVO :tradeVOS) {

                PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
                recordRequest.setBusinessId(tradeVO.getParentId());
                PayTradeRecordResponse record = payQueryProvider.findByBusinessId(recordRequest).getContext();
                if(Objects.isNull(record) || !record.getStatus().equals(TradeStatus.SUCCEED)){
                    recordRequest.setBusinessId(tradeVO.getId());
                    record = payQueryProvider.findByBusinessId(recordRequest).getContext();
                }
                if(Objects.nonNull(record)){
                    if(StringUtils.isNotEmpty(record.getPayOrderNo())){
                        tradeVO.setPayOrderNo(getPayOrderNo(record));
                    }
                }
            }
//        }
    }

    private static String getPayOrderNo(PayTradeRecordResponse record) {
        if (record.getChannelItemId() == 16L || record.getChannelItemId() == 32L) {
            return record.getPayOrderNo();
        }
        return "31YT" + record.getPayOrderNo();
    }

    private void encapsulation(List<TradeVO> tradeVOS){
        if(true||CollectionUtils.isEmpty(tradeVOS)){
            return;
        }
        //购买用户的业务员id集合
        log.info("=================================================开始查询业务员：{}===============================");
        Map<String, String> empNameByEmpIdMap = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList()
                .stream().collect(Collectors.toMap(EmployeeListVO::getEmployeeId, EmployeeListVO::getEmployeeName));
        log.info("=================================================查询业务员结束开始分配数据===============================");
        if (MapUtils.isNotEmpty(empNameByEmpIdMap)) {
            tradeVOS.stream().forEach(tradeVO -> {
                tradeVO.setEmployeeName(empNameByEmpIdMap.get(tradeVO.getBuyer().getEmployeeId()));
                tradeVO.setManagerName("system");
                if (!TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(tradeVO.getActivityType())) {
                    tradeVO.setManagerName(empNameByEmpIdMap.get(tradeVO.getBuyer().getManagerId()));
                }
            });
        }
        log.info("=================================================业务员数据分配结束===============================");
    }

    private void formatTradeVO(List<TradeVO> tradeVOS){
        if(CollectionUtils.isEmpty(tradeVOS)){
            return;
        }
        tradeService.setTradeVoInvestmentManager(tradeVOS);
        //获取所有订单号
        List<String> logisticsIds = new ArrayList<>(tradeVOS.size());
        List<String> kuaidi100LogisticsIds = new ArrayList<>(tradeVOS.size());
        for(TradeVO t:tradeVOS){
            if(Objects.nonNull(t.getSupplier())&&!t.getSupplier().getIsSelf()){
                setTradeVOLogisticsByWith100(t);
            }else{
                logisticsIds.add(t.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(logisticsIds)){
            //查询订单物流信息
            List<OrderLogistics> logistics = tradeLogisticsService.findByLogisticIds(logisticsIds);
            for (TradeVO vo : tradeVOS){
                if(CollectionUtils.isNotEmpty(logistics)){
                    OrderLogistics orderLogistics = logistics.stream().filter(l -> vo.getId().equals(l.getLogisticId())).findFirst().orElse(null);
                    if(Objects.nonNull(orderLogistics)){
                        vo.setLogistics(JSONObject.toJSONString(orderLogistics));
                    }
                }
            }
        }
    }

    /**
     * 获取采购件数
     *
     * @param
     * @return: com.wanmi.sbc.common.base.BaseResponse<java.lang.Long>
     */
    @Override
    public BaseResponse<Long> queryPurchaseCount(@RequestBody @Valid PurchaseQueryCountRequest purchaseQueryCountRequest) {
        Long goodsTotalNum = 0L;
        List<FlowState> notFlowStates = new ArrayList<>();
        notFlowStates.add(FlowState.VOID);
        notFlowStates.add(FlowState.REFUND);
        List<Trade> providerTrades = tradeService.queryAll(TradeQueryRequest.builder()
                .orderType(OrderType.ALL_ORDER)
                .notFlowStates(notFlowStates)
                .employeeId(purchaseQueryCountRequest.getTradePageDTO().getEmployeeId())
                .build());
        //统计商品总件数
        if (CollectionUtils.isEmpty(providerTrades)) {
            return BaseResponse.success(goodsTotalNum);
        }
        for (Trade trade : providerTrades) {
            goodsTotalNum += trade.getTradeItems().stream().map(TradeItem::getNum).reduce(Long::sum).orElse(0L);
            goodsTotalNum += trade.getGifts().stream().map(TradeItem::getNum).reduce(Long::sum).orElse(0L);
        }
        return BaseResponse.success(goodsTotalNum);
    }

    /**
     * @param tradeCountCriteriaRequest 带参分页参数 {@link TradeCountCriteriaRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeCountCriteriaResponse> countCriteria(@RequestBody @Valid TradeCountCriteriaRequest tradeCountCriteriaRequest) {
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradeCountCriteriaRequest.getTradePageDTO(), TradeQueryRequest.class);
        long count = tradeService.countNum(tradeQueryRequest.getWhereCriteria(), tradeQueryRequest);
        return BaseResponse.success(TradeCountCriteriaResponse.builder().count(count).build());
    }

    /**
     * 调用校验与封装单个订单信息
     *
     * @param tradeWrapperBackendCommitRequest 包装信息参数 {@link TradeWrapperBackendCommitRequest}
     * @return 订单信息 {@link TradeWrapperBackendCommitResponse}
     */
    @Override
    public BaseResponse<TradeWrapperBackendCommitResponse> wrapperBackendCommit(@RequestBody @Valid TradeWrapperBackendCommitRequest tradeWrapperBackendCommitRequest) {
        Trade trade = tradeService.wrapperBackendCommitTrade(tradeWrapperBackendCommitRequest.getOperator(),
                KsBeanUtil.convert(tradeWrapperBackendCommitRequest.getCompanyInfo(), CompanyInfoVO.class),
                KsBeanUtil.convert(tradeWrapperBackendCommitRequest.getStoreInfo(), StoreInfoResponse.class),
                KsBeanUtil.convert(tradeWrapperBackendCommitRequest.getTradeCreate(), TradeCreateRequest.class));
        return BaseResponse.success(TradeWrapperBackendCommitResponse.builder()
                .tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
    }

    /**
     * 查询店铺订单应付的运费
     *
     * @param tradeParamsRequest 包装信息参数 {@link TradeParamsRequest}
     * @return 店铺运费信息 {@link TradeGetFreightResponse}
     */
    @Override
    public BaseResponse<TradeGetFreightResponse> getFreight(@RequestBody @Valid TradeParamsRequest tradeParamsRequest) {
        TradeFreightResponse freight = tradeService.getFreight(KsBeanUtil.convert(tradeParamsRequest,
                TradeParams.class));
        return BaseResponse.success(KsBeanUtil.convert(freight, TradeGetFreightResponse.class));
    }

    @Override
    public BaseResponse<List<TradeGetFreightResponse>> getFreightForDeliveryToStore(TradeParamsRequestForApp tradeParamsRequestForApp) {
        List<TradeFreightResponse> freight = tradeService.getFreightForDeliveryToStore(KsBeanUtil.convert(tradeParamsRequestForApp.getTradeParams(),
                TradeParams.class));
        return BaseResponse.success(KsBeanUtil.convert(freight, TradeGetFreightResponse.class));
    }
    @Override
    public BaseResponse<TradeGetFreightResponse> getTradeFreightAndBluk(@RequestBody @Valid TradeParamsRequest tradeParamsRequest) {
        TradeParams tradeParams = KsBeanUtil.convert(tradeParamsRequest, TradeParams.class);

        Consignee consignee = tradeParams.getConsignee();
        Supplier supplier = tradeParams.getSupplier();
        DeliverWay deliverWay = tradeParams.getDeliverWay();
        BigDecimal totalPrice = tradeParams.getTotalPrice();
        List<TradeItem> oldTradeItems = tradeParams.getOldTradeItems();
        List<TradeItem> oldBulkTradeItems = tradeParams.getOldBulkTradeItems();
        List<TradeItem> oldGifts = tradeParams.getOldGifts();
        TradeFreightResponse freight = tradeService.getTradeFreightAndBluk(consignee, supplier, deliverWay, totalPrice, oldTradeItems, oldBulkTradeItems, oldGifts);
        return BaseResponse.success(KsBeanUtil.convert(freight, TradeGetFreightResponse.class));
    }

    /**
     * 查询平台订单应付的运费
     *
     * @param tradeParams 包装信息参数 {@link TradeParamsRequest}
     * @return 店铺运费信息 {@link TradeGetFreightResponse}
     */
    @Override
    public BaseResponse<List<TradeGetFreightResponse>> getBossFreight(@Valid List<TradeParamsRequest> tradeParams) {
        List<TradeFreightResponse> bossFreight = tradeService.getBossFreight(KsBeanUtil.convert(tradeParams,
                TradeParams.class));
        return BaseResponse.success(KsBeanUtil.convert(bossFreight, TradeGetFreightResponse.class));
    }

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link GoodsInfoViewListResponse}
     */
    @Override
    public BaseResponse<TradeGetGoodsResponse> getGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest) {
        return BaseResponse.success(tradeService.getGoodsResponseMatchFlag(tradeGetGoodsRequest.getSkuIds(), tradeGetGoodsRequest.getWareId()
                , tradeGetGoodsRequest.getWareHouseCode(), tradeGetGoodsRequest.getMatchWareHouseFlag()));
    }

    /**
     * 获取拆箱订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link GoodsInfoViewListResponse}
     */
    @Override
    public BaseResponse<TradeGetGoodsResponse> getDevanningGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest) {
        return BaseResponse.success(tradeService.getDevanningGoodsResponseMatchFlag(tradeGetGoodsRequest.getDevanningIds(), tradeGetGoodsRequest.getWareId()
                , tradeGetGoodsRequest.getWareHouseCode(), tradeGetGoodsRequest.getMatchWareHouseFlag()));
    }

    @Override
    public BaseResponse<TradeGetGoodsResponse> getRetailGoods(TradeGetGoodsRequest tradeGetGoodsRequest) {
        return BaseResponse.success(tradeService.getRetailGoodsResponseMatchFlag(tradeGetGoodsRequest.getSkuIds(), tradeGetGoodsRequest.getWareId()
                , tradeGetGoodsRequest.getWareHouseCode(), tradeGetGoodsRequest.getMatchWareHouseFlag()));
    }

    @Override
    public BaseResponse<TradeGetGoodsResponse> getBulkGoods(TradeGetGoodsRequest tradeGetGoodsRequest) {
        return BaseResponse.success(tradeService.getBulkGoodsResponseMatchFlag(tradeGetGoodsRequest.getSkuIds(), tradeGetGoodsRequest.getWareId()
                , tradeGetGoodsRequest.getWareHouseCode(), tradeGetGoodsRequest.getMatchWareHouseFlag()));
    }

    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tradeDeliveryCheckRequest 订单号 物流信息 {@link TradeDeliveryCheckRequest}
     * @return 处理结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse deliveryCheck(@RequestBody @Valid TradeDeliveryCheckRequest tradeDeliveryCheckRequest) {
        tradeService.deliveryCheck(tradeDeliveryCheckRequest.getTid(),
                KsBeanUtil.convert(tradeDeliveryCheckRequest.getTradeDeliver(), TradeDeliverRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 通过id获取交易单信息,并将buyer.account加密
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @Override
    public BaseResponse<TradeGetByIdResponse> getById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest) {
        Trade trade = tradeService.detail(tradeGetByIdRequest.getTid());
        if (Objects.isNull(trade)) {
            return BaseResponse.success(TradeGetByIdResponse.builder().tradeVO(null).build());
        }
        List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentId(trade.getId()).build());
        log.info("打印原始数据中的数量jeffrey: {}", JSON.toJSONString(trade));
        //求出商品总件数
        Long allNum = 0L;
        for (TradeItem tradeItem : trade.getTradeItems()) {
            Long num = tradeItem.getNum() == null ? 0L : tradeItem.getNum();
            allNum += num;
        }
        if (trade.getGoodsTotalNum() == null) {
            trade.setGoodsTotalNum(allNum);
        }
        log.info("商品总件数是: {}", allNum);
        List<TradeVO> providerTradeVOS = KsBeanUtil.convert(providerTrades, TradeVO.class);
        
//        if(CollectionUtils.isNotEmpty(providerTradeVOS)){
//            //业务员id集合
//            List<String> employeeIds = providerTradeVOS.stream().map(tradeVO -> {
//                return tradeVO.getBuyer().getEmployeeId();
//            }).collect(Collectors.toList());
//        }
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }
        String employeeId = trade.getBuyer().getEmployeeId();
        String employeeName  = "";
        if(StringUtils.isNotEmpty(employeeId)){
             employeeName = employeeQueryProvider.getById(EmployeeByIdRequest.builder().employeeId(employeeId).build()).getContext().getEmployeeName();
        }

        formatTradeVO(trade);
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        baseResponse.getContext().getTradeVO().setTradeVOList(providerTradeVOS);
        baseResponse.getContext().getTradeVO().setEmployeeName(employeeName);
        return baseResponse;
    }

    private void formatTradeVO(Trade trade){
        if(Objects.isNull(trade)){
            return;
        }
        //获取所有订单号
        if(StringUtils.isNotEmpty(trade.getId())) {
            //查询订单物流信息
            if (!orderCommonService.selfOrder(trade)) {
                setTradeVOLogisticsByWith100(trade);
            } else {
                OrderLogistics logistics = tradeLogisticsService.findByLogisticId(trade.getId());
                if (Objects.nonNull(logistics)) {
                    trade.setLogistics(JSONObject.toJSONString(logistics));
                }
            }
        }
    }

    private void formatTradeVO(TradeVO tradeVo){
        if(Objects.isNull(tradeVo)){
            return;
        }
        //获取所有订单号
        if(StringUtils.isNotEmpty(tradeVo.getId())){
            //查询订单物流信息
            if(Objects.nonNull(tradeVo.getSupplier())&&!tradeVo.getSupplier().getIsSelf()){
                setTradeVOLogisticsByWith100(tradeVo);
            }else{
                //查询订单物流信息
                OrderLogistics logistics = tradeLogisticsService.findByLogisticId(tradeVo.getId());
                if(Objects.nonNull(logistics)){
                    tradeVo.setLogistics(JSONObject.toJSONString(logistics));
                }
            }
        }
    }

    private void setTradeVOLogisticsByWith100(TradeVO tradeVo) {
        if (CollectionUtils.isEmpty(tradeVo.getTradeDelivers())) {
            log.info("发货记录为空{}", tradeVo.getId());
            return;
        }
        for (TradeDeliverVO tradeDeliver : tradeVo.getTradeDelivers()) {
            LogisticsVO tradeDeliverLogistics = tradeDeliver.getLogistics();
            if (null == tradeDeliverLogistics
                    || StringUtils.isBlank(tradeDeliverLogistics.getLogisticNo())
                    || StringUtils.isBlank(tradeDeliverLogistics.getLogisticStandardCode())) {
                log.info("发货记录物流信息不全{}", tradeVo.getId());
                return;
            }
            if (Objects.nonNull(tradeVo.getTradeState().getDeliverStatus()) && (tradeVo.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || tradeVo.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
                QueryTrackParam trackParam  = new QueryTrackParam();
                trackParam.setCom(tradeDeliverLogistics.getLogisticStandardCode());
                trackParam.setNum(tradeDeliverLogistics.getLogisticNo());
                trackParam.setTo(tradeVo.getConsignee().getAddress());
                QueryTrackMapResp queryTrackMapResp = deliveryQueryProvider.queryExpressMapObj(trackParam).getContext();
                String mapData = JSONObject.toJSONString(queryTrackMapResp);
                tradeVo.setLogistics100(mapData);
                if(ConstantContent.TRAIL_CHECKED.equals(queryTrackMapResp.getIscheck())){
                    OrderTrack orderTrack = getOrderTrack(tradeVo.getId(), trackParam, mapData);
                    orderTrackService.add(orderTrack);
                }
            }
        }
    }

    private void setTradeVOLogisticsByWith100(Trade trade) {
        if (CollectionUtils.isEmpty(trade.getTradeDelivers())) {
            log.info("发货记录为空{}", trade.getId());
            return;
        }
        for (TradeDeliver tradeDeliver : trade.getTradeDelivers()) {
            Logistics tradeDeliverLogistics = tradeDeliver.getLogistics();
            if (null == tradeDeliverLogistics
                    || StringUtils.isBlank(tradeDeliverLogistics.getLogisticNo())
                    || StringUtils.isBlank(tradeDeliverLogistics.getLogisticStandardCode())) {
                log.info("发货记录物流信息不全{}", trade.getId());
                return;
            }
            if (Objects.nonNull(trade.getTradeState().getDeliverStatus()) && (trade.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || trade.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
                QueryTrackParam trackParam  = new QueryTrackParam();
                trackParam.setCom(tradeDeliverLogistics.getLogisticStandardCode());
                trackParam.setNum(tradeDeliverLogistics.getLogisticNo());
                trackParam.setTo(trade.getConsignee().getAddress());
                QueryTrackMapResp queryTrackMapResp = deliveryQueryProvider.queryExpressMapObj(trackParam).getContext();
                String mapData = JSONObject.toJSONString(queryTrackMapResp);
                trade.setLogistics100(mapData);
                if(ConstantContent.TRAIL_CHECKED.equals(queryTrackMapResp.getIscheck())){
                    OrderTrack orderTrack = getOrderTrack(trade.getId(), trackParam, mapData);
                    orderTrackService.add(orderTrack);
                }
            }
        }
    }

    private static OrderTrack getOrderTrack(String tradeId, QueryTrackParam trackParam, String mapData) {
        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setCom(trackParam.getCom());
        orderTrack.setNum(trackParam.getNum());
        orderTrack.setOrderCode(tradeId);
        orderTrack.setOrderType(1);
        orderTrack.setData(mapData);
        return orderTrack;
    }

    @Override
    public BaseResponse<TradeGetByIdResponse> getByIdManager(@RequestBody @Valid TradeGetByIdManagerRequest request) {
        TradeVO trade = tradeService.detailVO(request.getTid());
        List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentId(trade.getId()).build());
        log.info("打印原始数据中的数量jeffrey: {}", JSON.toJSONString(trade));
        //求出商品总件数
        Long allNum = 0L;
        for (TradeItemVO tradeItem : trade.getTradeItems()) {
            Long num = tradeItem.getNum() == null ? 0L : tradeItem.getNum();
            allNum += num;
        }
        if (trade.getGoodsTotalNum() == null) {
            trade.setGoodsTotalNum(allNum);
        }
        log.info("商品总件数是: {}", allNum);
        List<TradeVO> providerTradeVOS = KsBeanUtil.convert(providerTrades, TradeVO.class);
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        //赋值是否标星
        if(StringUtils.isNotEmpty(trade.getBuyer().getAccount())){
            DisableCustomerDetailGetByAccountRequest disableCustomerDetailGetByAccountRequest = new DisableCustomerDetailGetByAccountRequest();
            disableCustomerDetailGetByAccountRequest.setCustomerAccount(trade.getBuyer().getAccount());
            BaseResponse<DisableCustomerDetailGetByAccountResponse> disableCustomerDetailByAccount = customerQueryProvider.getCustomerDetailByAccount(disableCustomerDetailGetByAccountRequest);
            if(Objects.nonNull(disableCustomerDetailByAccount.getContext())){
                baseResponse.getContext().getTradeVO().getBuyer().setBeaconStar(disableCustomerDetailByAccount.getContext().getBeaconStar());
            }
        }
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }
        String employeeId = trade.getBuyer().getEmployeeId();
        String employeeName  = "";
        if(StringUtils.isNotEmpty(employeeId)){
            employeeName = employeeQueryProvider.getById(EmployeeByIdRequest.builder().employeeId(employeeId).build()).getContext().getEmployeeName();
        }

        WareHouseByIdResponse context = wareHouseQueryProvider.getByWareId(WareHouseByIdRequest.builder().wareId(Objects.nonNull(trade.getWareId())?trade.getWareId():1L).build()).getContext();
        trade.setWareHouseNmae(context.getWareHouseVO().getWareName());
        Map<String, String> collect = trade.getTradeItems().stream().filter(o -> Objects.nonNull(o.getGoodsSubtitle())).collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getGoodsSubtitle, (a, b) -> a));

        trade.getTradeDelivers().forEach(v->{
            v.getShippingItems().forEach(b->{
                b.setSpecDetails(collect.get(b.getSkuId()));
            });
        });
        baseResponse.getContext().getTradeVO().setTradeVOList(providerTradeVOS);
        baseResponse.getContext().getTradeVO().setEmployeeName(employeeName);
        TradeVO tradeVO = baseResponse.getContext().getTradeVO();
        formatTradeVO(tradeVO);
        baseResponse.getContext().setTradeVO(tradeVO);
        log.info("TradeQueryController getByIdManager tradeVO:{}", JSON.toJSONString(tradeVO));
        return baseResponse;
    }

    /**
     * 通过parentId获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByParentRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @Override
    public BaseResponse<TradeGetByIdResponse> getByParent(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest) {
        Trade trade = tradeService.detailByParentId(tradeGetByParentRequest.getParentId());
        List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentId(trade.getId()).build());
        List<TradeVO> providerTradeVOS = KsBeanUtil.convert(providerTrades, TradeVO.class);
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        baseResponse.getContext().getTradeVO().setTradeVOList(providerTradeVOS);
        return baseResponse;
    }

    /**
     * 通过id获取交易单信息
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @Override
    public BaseResponse<TradeGetByIdResponse> getOrderById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest) {
        Trade trade = tradeService.detail(tradeGetByIdRequest.getTid());
        if(Objects.isNull(trade)){
            return BaseResponse.success(TradeGetByIdResponse.builder().tradeVO(null).build());
        }
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        return baseResponse;
    }

    @Override
    public BaseResponse<TradeListByParentIdResponse> getListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest) {
        List<Trade> tradeList = tradeService.detailsByParentId(tradeListByParentIdRequest.getParentTid());
        log.info("=========tradeList==========",JSONObject.toJSONString(tradeList));
        if (tradeList.isEmpty()) {
            return BaseResponse.success(TradeListByParentIdResponse.builder().tradeVOList(Collections.emptyList()).build());
        }
        //父订单号对应的子订单的买家信息应该是相同的
        Trade trade = tradeList.get(0);
        //买家银行账号加密
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }
        final Buyer buyer = trade.getBuyer();
        //统一设置账号加密后的买家信息
        List<TradeVO> tradeVOList = tradeList.stream().map(i -> {
            i.setBuyer(buyer);
            return KsBeanUtil.convert(i, TradeVO.class);
        }).collect(Collectors.toList());
        log.info("=========tradeVOList==========",JSONObject.toJSONString(tradeVOList));
        return BaseResponse.success(TradeListByParentIdResponse.builder().
                tradeVOList(tradeVOList).build());
    }

    @Override
    public BaseResponse<TradeListByParentIdResponse> getOrderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest) {
        List<Trade> tradeList = tradeService.detailsByParentId(tradeListByParentIdRequest.getParentTid());
        if (tradeList.isEmpty()) {
            return BaseResponse.success(TradeListByParentIdResponse.builder().tradeVOList(Collections.emptyList()).build());
        }
        //统一设置账号加密后的买家信息
        List<TradeVO> tradeVOList = tradeList.stream().map(i -> {
            return KsBeanUtil.convert(i, TradeVO.class);
        }).collect(Collectors.toList());
        return BaseResponse.success(TradeListByParentIdResponse.builder().
                tradeVOList(tradeVOList).build());
    }


    /**
     * 验证订单是否存在售后申请
     *
     * @param tradeVerifyAfterProcessingRequest 交易单id {@link TradeVerifyAfterProcessingRequest}
     * @return 验证结果 {@link TradeVerifyAfterProcessingResponse}
     */
    @Override
    public BaseResponse<TradeVerifyAfterProcessingResponse> verifyAfterProcessing(@RequestBody @Valid TradeVerifyAfterProcessingRequest tradeVerifyAfterProcessingRequest) {
        return BaseResponse.success(TradeVerifyAfterProcessingResponse.builder().
                verifyResult(tradeService.verifyAfterProcessing(tradeVerifyAfterProcessingRequest.getTid())).build());
    }

    /**
     * 条件查询所有订单
     *
     * @param tradeListAllRequest 查询条件 {@link TradeListAllRequest}
     * @return 验证结果 {@link TradeListAllResponse}
     */
    @Override
    public BaseResponse<TradeListAllResponse> listAll(@RequestBody @Valid TradeListAllRequest tradeListAllRequest) {
        List<Trade> trades = tradeService.queryAll(KsBeanUtil.convert(tradeListAllRequest.getTradeQueryDTO(),
                TradeQueryRequest.class));
        return BaseResponse.success(TradeListAllResponse.builder().tradeVOList(KsBeanUtil.convert(trades,
                TradeVO.class)).build());
    }

    /**
     * 获取支付单
     *
     * @param tradeGetPayOrderByIdRequest 支付单号 {@link TradeGetPayOrderByIdRequest}
     * @return 支付单 {@link TradeGetPayOrderByIdResponse}
     */
    @Override
    public BaseResponse<TradeGetPayOrderByIdResponse> getPayOrderById(@RequestBody @Valid TradeGetPayOrderByIdRequest tradeGetPayOrderByIdRequest) {
        PayOrder payOrder = tradeService.findPayOrder(tradeGetPayOrderByIdRequest.getPayOrderId());
        return BaseResponse.success(TradeGetPayOrderByIdResponse.builder()
                .payOrder(KsBeanUtil.convert(payOrder, PayOrderVO.class)).build());
    }

    /**
     * 查询订单信息作为结算原始数据
     *
     * @param tradePageForSettlementRequest 查询分页参数 {@link TradePageForSettlementRequest}
     * @return 支付单集合 {@link TradePageForSettlementResponse}
     */
    @Override
    public BaseResponse<TradePageForSettlementResponse> pageForSettlement(@RequestBody @Valid TradePageForSettlementRequest tradePageForSettlementRequest) {
        List<Trade> tradeList = tradeService.findTradeListForSettlement(tradePageForSettlementRequest.getStoreId(),
                tradePageForSettlementRequest.getStartTime()
                , tradePageForSettlementRequest.getEndTime(), tradePageForSettlementRequest.getPageRequest());
        return BaseResponse.success(TradePageForSettlementResponse.builder().tradeVOList(KsBeanUtil.convert(tradeList
                , TradeVO.class)).build());
    }

    /**
     * 根据快照封装订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @Override
    public BaseResponse<TradeQueryPurchaseInfoResponse> queryPurchaseInfo(TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest) {
        TradeConfirmItem tradeConfirmItem = tradeService.getPurchaseInfo(
                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemGroupDTO(), TradeItemGroup.class),
                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemList(), TradeItem.class));
        return BaseResponse.success(TradeQueryPurchaseInfoResponse.builder()
                .tradeConfirmItemVO(KsBeanUtil.convert(tradeConfirmItem, TradeConfirmItemVO.class)).build());
    }

    /**
     * 根据快照封装订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @Override
    public BaseResponse<TradeQueryPurchaseInfoResponse> queryTakePurchaseInfo(@RequestBody @Valid TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest) {
        TradeConfirmItem tradeConfirmItem = tradeService.getTakePurchaseInfo(
                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemGroupDTO(), TradeItemGroup.class));
//                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemList(), TradeItem.class));
        return BaseResponse.success(TradeQueryPurchaseInfoResponse.builder()
                .tradeConfirmItemVO(KsBeanUtil.convert(tradeConfirmItem, TradeConfirmItemVO.class)).build());
    }

    /**
     * 根据订单状态统计订单
     *
     * @param tradeCountByFlowStateRequest 店铺id {@link TradeCountByFlowStateRequest}
     * @return 支付单集合 {@link TradeCountByFlowStateResponse}
     */
//    @Override
//    public BaseResponse<TradeCountByFlowStateResponse> countByFlowState(@RequestBody @Valid TradeCountByFlowStateRequest tradeCountByFlowStateRequest) {
//        TradeTodoReponse tradeTodoReponse =
//                tradeService.countTradebyFlowState(tradeCountByFlowStateRequest.getCompanyInfoId(),
//                        tradeCountByFlowStateRequest.getStoreId());
//        return BaseResponse.success(KsBeanUtil.convert(tradeTodoReponse, TradeCountByFlowStateResponse.class));
//    }
//
//    /**
//     * 根据支付状态统计订单
//     *
//     * @param tradeCountByPayStateRequest 店铺id {@link TradeCountByPayStateRequest}
//     * @return 支付单集合 {@link TradeCountByPayStateResponse}
//     */
//    @Override
//    public BaseResponse<TradeCountByPayStateResponse> countByPayState(@RequestBody @Valid TradeCountByPayStateRequest tradeCountByPayStateRequest) {
//        TradeTodoReponse tradeTodoReponse =
//                tradeService.countTradeByPayState(tradeCountByPayStateRequest.getCompanyInfoId(),
//                        tradeCountByPayStateRequest.getStoreId());
//        return BaseResponse.success(KsBeanUtil.convert(tradeTodoReponse, TradeCountByPayStateResponse.class));
//    }

    /**
     * 用于编辑订单前的展示信息，包含了原订单信息和最新关联的订单商品价格（计算了会员价和级别价后的商品单价）
     *
     * @param tradeGetRemedyByTidRequest 交易单id {@link TradeGetRemedyByTidRequest}
     * @return 废弃单 {@link TradeGetRemedyByTidResponse}
     */
    @Override
    public BaseResponse<TradeGetRemedyByTidResponse> getRemedyByTid(@RequestBody @Valid TradeGetRemedyByTidRequest tradeGetRemedyByTidRequest) {
        TradeRemedyDetails tradeRemedyDetails = tradeService.getTradeRemedyDetails(tradeGetRemedyByTidRequest.getTid());
        return BaseResponse.success(TradeGetRemedyByTidResponse.builder()
                .tradeRemedyDetailsVO(KsBeanUtil.convert(tradeRemedyDetails, TradeRemedyDetailsVO.class)).build());
    }

    /**
     * 查询客户首笔完成的交易号
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<TradeQueryFirstCompleteResponse> queryFirstCompleteTrade(@RequestBody @Valid TradeQueryFirstCompleteRequest request) {
        String tradeId = tradeService.queryFirstCompleteTrade(request.getCustomerId());
        return BaseResponse.success(TradeQueryFirstCompleteResponse.builder().tradeId(tradeId).build());
    }

    /**
     * 订单选择银联企业支付通知财务
     *
     * @param tradeSendEmailToFinanceRequest 邮箱信息 {@link TradeSendEmailToFinanceRequest}
     * @return 发送结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse sendEmailToFinance(@RequestBody @Valid TradeSendEmailToFinanceRequest tradeSendEmailToFinanceRequest) {
        tradeService.sendEmailToFinance(tradeSendEmailToFinanceRequest.getCustomerId(),
                tradeSendEmailToFinanceRequest.getOrderId(), tradeSendEmailToFinanceRequest.getUrl());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 发送上个月的订单信息给运营管理员 XLSX
     *
     * @return
     */
    @Override
    public BaseResponse sendEmailTranslate() {
        tradeService.sendEmailTranslate();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return 验证结果 {@link TradeListExportResponse}
     */
    @Override
    public BaseResponse<TradeListExportResponse> listTradeExport(@RequestBody @Valid TradeListExportRequest tradeListExportRequest) {
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(tradeListExportRequest.getTradeQueryDTO(),
                TradeQueryRequest.class);
        List<Trade> tradeList = tradeService.listTradeExport(tradeQueryRequest);

        List<TradeVO> tradeVOS = KsBeanUtil.convert(tradeList,
                TradeVO.class);

        tradeVOS.forEach(tradeVO -> {
            /*PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
            recordRequest.setBusinessId(tradeVO.getParentId());
            PayTradeRecordResponse record = payQueryProvider.findByBusinessId(recordRequest).getContext();
            if(Objects.isNull(record) || !record.getStatus().equals(TradeStatus.SUCCEED)){
                recordRequest.setBusinessId(tradeVO.getId());
                record = payQueryProvider.findByBusinessId(recordRequest).getContext();
            }
            if(Objects.nonNull(record)){
                if(StringUtils.isNotEmpty(record.getPayOrderNo())){
                    tradeVO.setPayOrderNo(getPayOrderNo(record));
                }
            }*/
            String payOrderNo = tradeVO.getPayOrderNo();
            if (StringUtils.isNotBlank(payOrderNo) && (Objects.equals(PayWay.CUPSALI,tradeVO.getPayWay()) || Objects.equals(PayWay.CUPSWECHAT,tradeVO.getPayWay()))) {
                tradeVO.setPayOrderNo("31YT" + payOrderNo);
            }

            if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(tradeVO.getActivityType())){
                InventoryDetailSamountRequest inventoryDetailSamountRequest = InventoryDetailSamountRequest.builder().takeId(tradeVO.getId()).build();
                List<InventoryDetailSamountVO> inventoryDetailSamountVOS = Optional.ofNullable(inventoryDetailSamountProvider.getInventoryByTakeId(inventoryDetailSamountRequest))
                        .map(BaseResponse::getContext)
                        .map(InventoryDetailSamountResponse::getInventoryDetailSamountVOS)
                        .orElse(Lists.newArrayList());
                if(CollectionUtils.isNotEmpty(inventoryDetailSamountVOS)){
                    Map<Integer, BigDecimal> collect1 = inventoryDetailSamountVOS.stream()
                            .collect(Collectors.groupingBy(
                                    InventoryDetailSamountVO::getMoneyType,
                                    Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)
                            ));
                    BigDecimal rmb = Optional.ofNullable(collect1.get(0)).orElse(BigDecimal.ZERO);     // 金钱类型 0是余额 1真实的钱
                    BigDecimal jintie = Optional.ofNullable(collect1.get(1)).orElse(BigDecimal.ZERO);  // 金钱类型 0是余额 1真实的钱
                    BigDecimal paidPrice = rmb.add(jintie);
                    tradeVO.getTradePrice().setPaidPrice(paidPrice);
                }
            }
            //补SKU成本价
            formatTradeItemInfo(tradeVO.getTradeItems());
            if(CollectionUtils.isNotEmpty(tradeVO.getGifts())){
                formatTradeItemInfo(tradeVO.getGifts());
            }
        });
        tradeService.setTradeVoInvestmentManager(tradeVOS);
        return BaseResponse.success(TradeListExportResponse.builder().tradeVOList(tradeVOS).build());
    }

    private void formatTradeItemInfo(List<TradeItemVO> tradeItemVOS) {
        List<String> tradeSkuIds = tradeItemVOS.stream().filter(sku->sku.getCost()==null).map(TradeItemVO::getSkuId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(tradeSkuIds)) {
            DevanningGoodsInfoListResponse goodsInfoListResponse = devanningGoodsInfoQueryProvider.listByCondition(DevanningGoodsInfoListByConditionRequest.builder().goodsInfoIds(tradeSkuIds).build()).getContext();
            if (goodsInfoListResponse != null && org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoListResponse.getDevanningGoodsInfoVOS())) {
                tradeItemVOS.forEach(tradeItem -> {
                    DevanningGoodsInfoVO devanningGoodsInfoVO = goodsInfoListResponse.getDevanningGoodsInfoVOS().stream().filter(d -> d.getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
                    if (devanningGoodsInfoVO != null && devanningGoodsInfoVO.getCostPrice() != null) {
                        tradeItem.setCost(devanningGoodsInfoVO.getCostPrice());
                    }
                });
            }
        }
    }

    @Override
    public BaseResponse<TradeListExportResponse> listTradeExportMonth(TradeListExportRequest tradeListExportRequest) {
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(tradeListExportRequest.getTradeQueryDTO(),
                TradeQueryRequest.class);
        List<Trade> tradeList = tradeService.listTradeExportMonth(tradeQueryRequest);
        return BaseResponse.success(TradeListExportResponse.builder().tradeVOList(KsBeanUtil.convert(tradeList,
                TradeVO.class)).build());
    }

    /**
     * 根据支付单查询
     *
     * @param tradeByPayOrderIdRequest 父交易单id {@link TradeByPayOrderIdRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeByPayOrderIdResponse> getOrderByPayOrderId(@RequestBody @Valid TradeByPayOrderIdRequest tradeByPayOrderIdRequest) {
        Trade trade = tradeService.queryAll(TradeQueryRequest.builder().payOrderId(tradeByPayOrderIdRequest.getPayOrderId()).build()).get(0);
        List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentId(trade.getId()).build());
        List<TradeVO> providerTradeVOS = KsBeanUtil.convert(providerTrades, TradeVO.class);
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }
        BaseResponse<TradeByPayOrderIdResponse> baseResponse = BaseResponse.success(TradeByPayOrderIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        baseResponse.getContext().getTradeVO().setTradeVOList(providerTradeVOS);
        return baseResponse;
    }

    @Override
    public BaseResponse<TradeListAllResponse> queryAllConfirmFailed() {
        return BaseResponse.success(TradeListAllResponse.builder().tradeVOList(tradeService.queryConfrimFailedTrades()).build());
    }


    @Override
    public BaseResponse<TradeListAllResponse> getListByLogisticsCompanyId(@RequestBody @Valid TradeGetByLogisticsCompanyIdRequest tradeListByParentIdRequest) {
        return tradeService.findListByByLogisticsCompanyId(tradeListByParentIdRequest.getId());
    }


    @Override
    public BaseResponse<HistoryLogisticsCompanyByCustomerIdResponse> getByCustomerId(@Valid TradeByCustomerIdRequest request) {
        return tradeService.getByCustomerId(request);
    }

    @Override
    public BaseResponse<Long> getByCustomerIdAndMarketId(TradeByCustomerIdRequest request) {
        HistoryLogisticsCompanyVO historyLogisticsCompanyVO = tradeService.getByCustomerIdAndMarketId(request);
        if (Objects.nonNull(historyLogisticsCompanyVO) && null != historyLogisticsCompanyVO.getCompanyId()) {
            return BaseResponse.success(historyLogisticsCompanyVO.getCompanyId());
        }
        return BaseResponse.success(-1L);
    }

    @Override
    public BaseResponse<HistoryLogisticsCompanyVO> getHistoryVoByCustomerIdAndMarketId(TradeByCustomerIdRequest request) {
        HistoryLogisticsCompanyVO historyLogisticsCompanyVO = tradeService.getByCustomerIdAndMarketId(request);
        if (Objects.nonNull(historyLogisticsCompanyVO) && null != historyLogisticsCompanyVO.getCompanyId()) {
            return BaseResponse.success(historyLogisticsCompanyVO);
        }
        return BaseResponse.success(null);
    }

    @Override
    public BaseResponse<List<OrderSalesRanking>> salesRanking() {
        return BaseResponse.success(tradeService.querySalesRanking());
    }

    @Override
    public BaseResponse<Map<String,String>> getOrderTimeByCustomerIds(TradeByCustomerIdRequest request) {
        Map<String,String> localDateTimeMap = new HashMap<>();
        List<String> customerIds = request.getCustomerIds();
        customerIds.forEach(customerId -> {
            List<Trade> trades = tradeService.getCustomerLastPayOrderTime(customerId);
            Optional<Trade> trade =  trades.stream().findFirst();
            if (trade!=null && trade.isPresent()){
                if (Objects.nonNull(trade.get().getTradeState().getCreateTime())){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String dateTime = formatter.format(trade.get().getTradeState().getCreateTime());
                    localDateTimeMap.put(customerId,dateTime);
                }
            }
        });

        return BaseResponse.success(localDateTimeMap);
    }

    @Override
    public BaseResponse<TradeLogisticsReponse> getOrderLogisticsId(TradeLogisticsRequest request) {
        OrderLogistics orderLogistics = tradeLogisticsService.findByLogisticId(request.getLogisticsId());
        TradeLogisticsReponse reponse = new TradeLogisticsReponse();
        if(orderLogistics != null){
            reponse.setLogisticsData(orderLogistics.getLogisticsData());
            reponse.setLogisticId(orderLogistics.getLogisticId());
            reponse.setCreateTime(orderLogistics.getCreateTime());
            reponse.setEcId(orderLogistics.getEcId());
            reponse.setUpdateTime(orderLogistics.getUpdateTime());
            reponse.setLogisticCompanyId(orderLogistics.getLogisticCompanyId());
        }
        return BaseResponse.success(reponse);
    }

    @Override
    public BaseResponse<List<TradeVO>>  getOrderByIds(List<String> ids) {
        return BaseResponse.success( KsBeanUtil.convert(tradeService.getListByIds(ids),TradeVO.class));
    }

    @Override
    public BaseResponse<List<TradeVO>>  getOrderByIdsSimplify(List<String> ids) {
        return BaseResponse.success( KsBeanUtil.convert(tradeService.getListByIdsSimplify(ids),TradeVO.class));
    }

    @Override
    public BaseResponse<TradeCheckResponse> checkTrade(TradeCheckRequest request) {
        return BaseResponse.success(tradeService.checkTrade(request));
    }

    @Override
    public BaseResponse<List<OrderRecommendCount>> recommendTypeByCustomerIdAndCompanyInfo(TradeListAllRequest tradeListAllRequest) {
        return BaseResponse.success(tradeService.recommendTypeByCustomerIdAndCompanyInfo(tradeListAllRequest));
    }
    @Override
    public BaseResponse<List<OrderRecommendCount>> recommendTypeGetCustomerId() {
        return BaseResponse.success(tradeService.recommendTypeGetCustomerId());
    }
    @Override
    public BaseResponse<List<Object>> orderSort() {

        List<Object> orderCounts = orderCountRepository.orderSort();

        return BaseResponse.success(orderCounts);

    }
    @Override
    public BaseResponse<Integer> queryKingdeePushPayStatus(String tid) {
        Integer pushPayStatu = tradeService.queryKingdeePushPayStatus(tid);
        return BaseResponse.success(pushPayStatu);
    }

    @Override
    public BaseResponse<List<OrderRecommendSkuCount>> recommendTypeByCustomerId(TradeListAllRequest tradeListAllRequest) {
        return BaseResponse.success(tradeService.recommendTypeByCustomerId(tradeListAllRequest));
    }
    @Override
    public BaseResponse<List<OrderRecommendSkuCount>> sortByCustomerId(TradeListAllRequest tradeListAllRequest) {
        return BaseResponse.success(tradeService.sortByCustomerId(tradeListAllRequest));
    }
    @Override
    public BaseResponse<List<OrderRecommendCount>> recommendByUserIdAndSku() {
        return BaseResponse.success(tradeService.recommendByUserIdAndSku());
    }
    @Override
    public BaseResponse<List<Object>> listAllbyCostmerId(@RequestBody @Valid TradeListAllRequest tradeListAllRequest ) {
        List<Object> orderCounts = orderCountRepository.queryAllByBuyerId(tradeListAllRequest.getTradeQueryDTO().getBuyerId());
        return BaseResponse.success(orderCounts);
    }
    @Override
    public BaseResponse<List<OrderRecommendCount>> sortByUserIdAndSku() {
        List<OrderRecommendCount> orderRecommendCounts = tradeService.sortByUserIdAndSku();
        return BaseResponse.success(orderRecommendCounts);
    }

    @Override
    public BaseResponse<TradeVO> findTradeVOById(String id) {
        TradeVO trade = tradeService.detailBaseVO(id);
        return BaseResponse.success(trade);
    }

    @Override
    public BaseResponse<BigDecimal> findVillageAddDeliveryByTradeId(String tradeId) {
        BigDecimal price = tradeService.findVillageAddDeliveryByTradeId(tradeId);
        return BaseResponse.success(price);
    }

    @Override
    public BaseResponse<TmsOrderBillNoDTO> queryPrintOrderBillInfo(String tradeId) {
        TradeVO trade = tradeService.detailBaseVO(tradeId);
        TmsOrderBillNoDTO tmsOrderBillNoDTO = new TmsOrderBillNoDTO();
        List<TradeDeliverVO> tradeDelivers = trade.getTradeDelivers();
        if(CollectionUtils.isEmpty(tradeDelivers)){
            return BaseResponse.success(tmsOrderBillNoDTO);
        }
        TradeDeliverVO tradeDeliverVO=tradeDelivers.get(0);
        tmsOrderBillNoDTO.setTmsOrderId(tradeDeliverVO.getDeliverId());
        tmsOrderBillNoDTO.setTradeOrderId(tradeId);
        tmsOrderBillNoDTO.setStoreId(trade.getSupplier().getStoreId());
        tmsOrderBillNoDTO.setStoreName(trade.getSupplier().getStoreName());
        tmsOrderBillNoDTO.setReceiverName(tradeDeliverVO.getConsignee().getName());
        tmsOrderBillNoDTO.setReceiverPhone(tradeDeliverVO.getConsignee().getPhone());
        tmsOrderBillNoDTO.setReceiverAddress(tradeDeliverVO.getConsignee().getDetailAddress());
        if(DeliverWay.isTmsDelivery(trade.getDeliverWay())) {
            tmsOrderBillNoDTO.setAreaName(tradeDeliverVO.getLogistics().getShipmentSiteName());
        }else{
            tmsOrderBillNoDTO.setAreaName(tradeDeliverVO.getLogistics().getLogisticCompanyName());
        }

        List<TmsOrderBillNoDTO.BillNoItemDTO> billNoList = new ArrayList<>();
        Long goodsTotalNum = 0L;
        String idxTmplate = "%s/%s";
        String childBillNoTmplate = "%s-%s";
        if(CollectionUtils.isNotEmpty(tradeDeliverVO.getShippingItems())){
            goodsTotalNum += tradeDeliverVO.getShippingItems().stream().map(ShippingItemVO::getItemNum).reduce(Long::sum).orElse(0L);
        }
        if(CollectionUtils.isNotEmpty(tradeDeliverVO.getGiftItemList())){
            goodsTotalNum += tradeDeliverVO.getGiftItemList().stream().map(ShippingItemVO::getItemNum).reduce(Long::sum).orElse(0L);
        }
        int billIndex=0;
        if(CollectionUtils.isNotEmpty(tradeDeliverVO.getShippingItems())){
            for (int i=0; i <tradeDeliverVO.getShippingItems().size() ; i++) {
                billIndex++;
                TmsOrderBillNoDTO.BillNoItemDTO billNoItemDTO = new TmsOrderBillNoDTO.BillNoItemDTO();
                billNoItemDTO.setIdx(String.format(idxTmplate,billIndex,goodsTotalNum));
                billNoItemDTO.setWaybillType(1);
                billNoItemDTO.setOriginBillNo(tradeDeliverVO.getDeliverId());
                billNoItemDTO.setChildBillNo(String.format(childBillNoTmplate,tradeDeliverVO.getDeliverId(),StringUtils.leftPad( String.valueOf(billIndex),3,"0")));
                billNoList.add(billNoItemDTO);
            }
        }
        if(CollectionUtils.isNotEmpty(tradeDeliverVO.getGiftItemList())){
            for (int i=0; i <tradeDeliverVO.getGiftItemList().size() ; i++) {
                billIndex++;
                TmsOrderBillNoDTO.BillNoItemDTO billNoItemDTO = new TmsOrderBillNoDTO.BillNoItemDTO();
                billNoItemDTO.setIdx(String.format(idxTmplate,billIndex,goodsTotalNum));
                billNoItemDTO.setWaybillType(1);
                billNoItemDTO.setOriginBillNo(tradeDeliverVO.getDeliverId());
                billNoItemDTO.setChildBillNo(String.format(childBillNoTmplate,tradeDeliverVO.getDeliverId(),StringUtils.leftPad( String.valueOf(billIndex),3,"0")));
                billNoList.add(billNoItemDTO);
            }
        }
        tmsOrderBillNoDTO.setBillNoList(billNoList);
        return BaseResponse.success(tmsOrderBillNoDTO);
    }
}
