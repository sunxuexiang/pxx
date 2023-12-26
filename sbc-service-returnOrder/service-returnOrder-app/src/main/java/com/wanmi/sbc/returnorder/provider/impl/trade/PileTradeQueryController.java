package com.wanmi.sbc.returnorder.provider.impl.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.request.replay.ReplayTradeRequest;
import com.wanmi.ares.view.replay.ReplayTradeStatisticView;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.returnorder.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.returnorder.api.request.purchase.PilePurchaseRequest;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyByCustomerIdResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.CustomerPilePurchaseListResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.PilePurchaseGoodsNumsResponse;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import com.wanmi.sbc.returnorder.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.returnorder.bean.enums.FlowState;
import com.wanmi.sbc.returnorder.bean.vo.*;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseService;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.entity.OrderSalesRanking;
import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecord;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Buyer;
import com.wanmi.sbc.returnorder.trade.model.root.*;
import com.wanmi.sbc.returnorder.trade.reponse.TradeFreightResponse;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordRepository;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordTradeItemRepository;
import com.wanmi.sbc.returnorder.trade.request.TradeDeliverRequest;
import com.wanmi.sbc.returnorder.trade.request.*;
import com.wanmi.sbc.returnorder.trade.service.PileStockRecordAttachmentService;
import com.wanmi.sbc.returnorder.trade.service.PileTradeService;
import com.wanmi.sbc.returnorder.trade.service.ProviderTradeService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 10:04
 */
@Slf4j
@Validated
@RestController
public class PileTradeQueryController implements PileTradeQueryProvider {

    @Autowired
    private PileTradeService pileTradeService;
    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private PilePurchaseService pilePurchaseService;

    @Autowired
    private PileStockRecordAttachmentService pileStockRecordAttachmentService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private PileStockRecordTradeItemRepository pileStockRecordTradeItemRepository;

    @Autowired

    private PilePurchaseActionRepository pilePurchaseActionRepository;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;
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
        Page<PileTrade> page = pileTradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
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
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<PileTrade> page = pileTradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
        List<TradeVO> tradeVOList = tradeVOS.getContent();
        if (CollectionUtils.isNotEmpty(tradeVOS.getContent())) {
            List<String> providerTradeIds = tradeVOS.getContent().stream().map(TradeVO::getId).collect(Collectors.toList());
            //查询所有的子订单(providerTrade表)
            List<ProviderTrade> providerTradeList = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentIds(providerTradeIds).build());
            //通过囤货订单查询提货订单
            List<StockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentService.getStockAssociatedOrderCode(providerTradeIds);
            log.info("PileTradeQueryController.supplierPageCriteria stockRecordAttachmentList:{}", JSONObject.toJSONString(stockRecordAttachmentList));
            Map<String, List<StockRecordAttachment>> stockRecordMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(stockRecordAttachmentList)) {
                stockRecordMap = stockRecordAttachmentList.stream().collect(Collectors.groupingBy(StockRecordAttachment::getOrderCode));
            }
            log.info("PileTradeQueryController.supplierPageCriteria stockRecordMap:{}",JSONObject.toJSONString(stockRecordMap));
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
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getTid).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("PileTradeQueryController.supplierPageCriteria tradeVO:{}",tradeVO.getStockOrder());
                    }
                }
                log.info("PileTradeQueryController.supplierPageCriteria tradeVOList:{}",JSONObject.toJSONString(tradeVOList));
                encapsulation(tradeVOList);
                tradeVOS.setContent(tradeVOList);
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
            } else {
                for (TradeVO tradeVO : tradeVOList) {
                    if (stockRecordMap != null && stockRecordMap.containsKey(tradeVO.getId())){
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getTid).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("PileTradeQueryController.supplierPageCriteria tradeVO2:{}",tradeVO.getStockOrder());
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
        Page<PileTrade> page = pileTradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
        List<TradeVO> tradeVOList = tradeVOS.getContent();
        if (CollectionUtils.isNotEmpty(tradeVOS.getContent())) {
            List<String> providerTradeIds = tradeVOS.getContent().stream().map(TradeVO::getId).collect(Collectors.toList());
            //查询所有的子订单(providerTrade表)
            List<ProviderTrade> providerTradeList = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentIds(providerTradeIds).build());
            //通过囤货订单查询提货订单
            List<StockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentService.getStockAssociatedOrderCode(providerTradeIds);
            log.info("PileTradeQueryController.pageBossCriteria stockRecordAttachmentList:{}", JSONObject.toJSONString(stockRecordAttachmentList));
            Map<String, List<StockRecordAttachment>> stockRecordMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(stockRecordAttachmentList)) {
                stockRecordMap = stockRecordAttachmentList.stream().collect(Collectors.groupingBy(StockRecordAttachment::getOrderCode));
            }
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
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getTid).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("PileTradeQueryController.pageBossCriteria tradeVO:{}",tradeVO.getStockOrder());
                    }
                }
                encapsulation(tradeVOList);
                tradeVOS.setContent(tradeVOList);
                return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
            } else {
                for (TradeVO tradeVO : tradeVOList) {
                    if (stockRecordMap != null && stockRecordMap.containsKey(tradeVO.getId())){
                        List<String> stockOrderList = stockRecordMap.get(tradeVO.getId()).stream().map(StockRecordAttachment::getTid).collect(Collectors.toList());
                        tradeVO.setStockOrder(stockOrderList);
                        log.info("PileTradeQueryController.pageBossCriteria tradeVO2:{}",tradeVO.getStockOrder());
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

    private void encapsulation(List<TradeVO> tradeVOS){
        if(CollectionUtils.isEmpty(tradeVOS)){
            return;
        }
        //购买用户的业务员id集合
        log.info("=================================================开始查询业务员：{}===============================");
        List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();
        log.info("=================================================查询业务员结束开始分配数据===============================");
        if(CollectionUtils.isNotEmpty(employeeList)){
            tradeVOS.stream().forEach(tradeVO -> {
                String employeeId = tradeVO.getBuyer().getEmployeeId();
                //循环业务员
                for (EmployeeListVO employeeListVO: employeeList) {
                    //判断业务员id是否等于购买用户的业务员id
                    if(StringUtils.isNotEmpty(employeeId)){
                        if(employeeId.equals(employeeListVO.getEmployeeId())){
                            tradeVO.setEmployeeName(employeeListVO.getEmployeeName());
                            break;
                        }
                    }
                }
//                List<TradeVO> tradeVOList = tradeVO.getTradeVOList();
//                //有子订单
//                if(CollectionUtils.isNotEmpty(tradeVOList)){
//                    tradeVOList.stream().map(tr)
//                }
            });
        }else {
            log.info("=================================================业务员数据为空！:{}===============================");
        }
        log.info("=================================================业务员数据分配结束===============================");
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
        List<PileTrade> providerTrades = pileTradeService.queryAll(TradeQueryRequest.builder()
                .orderType(OrderType.ALL_ORDER)
                .notFlowStates(notFlowStates)
                .employeeId(purchaseQueryCountRequest.getTradePageDTO().getEmployeeId())
                .build());
        //统计商品总件数
        if (CollectionUtils.isEmpty(providerTrades)) {
            return BaseResponse.success(goodsTotalNum);
        }
        for (PileTrade trade : providerTrades) {
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
        long count = pileTradeService.countNum(tradeQueryRequest.getWhereCriteria(), tradeQueryRequest);
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
        PileTrade trade = pileTradeService.wrapperBackendCommitTrade(tradeWrapperBackendCommitRequest.getOperator(),
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
        TradeFreightResponse freight = pileTradeService.getFreight(KsBeanUtil.convert(tradeParamsRequest,
                TradeParams.class));
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
        List<TradeFreightResponse> bossFreight = pileTradeService.getBossFreight(KsBeanUtil.convert(tradeParams,
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
        return BaseResponse.success(pileTradeService.getGoodsResponseMatchFlag(tradeGetGoodsRequest.getSkuIds(), tradeGetGoodsRequest.getWareId()
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
        pileTradeService.deliveryCheck(tradeDeliveryCheckRequest.getTid(),
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
        PileTrade trade = pileTradeService.detail(tradeGetByIdRequest.getTid());
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
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        baseResponse.getContext().getTradeVO().setTradeVOList(providerTradeVOS);
        baseResponse.getContext().getTradeVO().setEmployeeName(employeeName);
        return baseResponse;
    }

    @Override
    public BaseResponse<TradeGetByIdResponse> getByIdManager(@RequestBody @Valid TradeGetByIdManagerRequest request) {
        PileTrade trade = pileTradeService.detail(request.getTid());
        List<ProviderTrade> providerTrades = providerTradeService.queryAll(ProviderTradeQueryRequest.builder().parentId(trade.getId()).build());
//        if(request.getRequestFrom() != null){
//            if(request.getRequestFrom() == AccountType.s2bSupplier){
//                Long supplierStoreId = request.getStoreId();
//                log.info("supplierStoreId：---------------"+supplierStoreId);
//                //过滤当前商家的交易条目
//                trade.setTradeItems(trade.getTradeItems().stream().filter(f->f.getStoreId().equals(supplierStoreId)).collect(Collectors.toList()));
//            }
//        }
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
        //详情添加囤货已使用数
        List<String> skuList = trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        List<PileStockRecord> pileStockRecordList = pileStockRecordAttachmentService.getPileStockRecordByOrderCodeAll(trade.getId(), skuList);
        if (pileStockRecordList.size() > 0) {
            trade.getTradeItems().stream().forEach(tradeItem -> {
                pileStockRecordList.stream().forEach(pileStockRecord -> {
                    if (tradeItem.getSkuId().equals(pileStockRecord.getGoodsInfoId())) {
                        tradeItem.setUseNum(pileStockRecord.getStockRecordRemainingNum());
                    }
                });
            });
            log.info("PileTradeQueryController.getByIdManager pileStockRecordList:{}",pileStockRecordList);
        }
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
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        baseResponse.getContext().getTradeVO().setTradeVOList(providerTradeVOS);
        baseResponse.getContext().getTradeVO().setEmployeeName(employeeName);
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
        PileTrade trade = pileTradeService.detailByParentId(tradeGetByParentRequest.getParentId());
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
        PileTrade trade = pileTradeService.detail(tradeGetByIdRequest.getTid());
        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, TradeVO.class)).build());
        return baseResponse;
    }

    @Override
    public BaseResponse<TradeListByParentIdResponse> getListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest) {
        List<PileTrade> tradeList = pileTradeService.detailsByParentId(tradeListByParentIdRequest.getParentTid());
        if (tradeList.isEmpty()) {
            return BaseResponse.success(TradeListByParentIdResponse.builder().tradeVOList(Collections.emptyList()).build());
        }
        //父订单号对应的子订单的买家信息应该是相同的
        PileTrade trade = tradeList.get(0);
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
        return BaseResponse.success(TradeListByParentIdResponse.builder().
                tradeVOList(tradeVOList).build());
    }

    @Override
    public BaseResponse<TradeListByParentIdResponse> getOrderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest) {
        List<PileTrade> tradeList = pileTradeService.detailsByParentId(tradeListByParentIdRequest.getParentTid());
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
                verifyResult(pileTradeService.verifyAfterProcessing(tradeVerifyAfterProcessingRequest.getTid())).build());
    }

    /**
     * 条件查询所有订单
     *
     * @param tradeListAllRequest 查询条件 {@link TradeListAllRequest}
     * @return 验证结果 {@link TradeListAllResponse}
     */
    @Override
    public BaseResponse<TradeListAllResponse> listAll(@RequestBody @Valid TradeListAllRequest tradeListAllRequest) {
        List<PileTrade> trades = pileTradeService.queryAll(KsBeanUtil.convert(tradeListAllRequest.getTradeQueryDTO(),
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
        PayOrder payOrder = pileTradeService.findPayOrder(tradeGetPayOrderByIdRequest.getPayOrderId());
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
        List<PileTrade> tradeList = pileTradeService.findTradeListForSettlement(tradePageForSettlementRequest.getStoreId(),
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
    public BaseResponse<TradeQueryPurchaseInfoResponse> queryPurchaseInfo(@RequestBody @Valid TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest) {
        TradeConfirmItem tradeConfirmItem = pileTradeService.getPurchaseInfo(
                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemGroupDTO(), TradeItemGroup.class),
                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemList(), TradeItem.class));
        return BaseResponse.success(TradeQueryPurchaseInfoResponse.builder()
                .tradeConfirmItemVO(KsBeanUtil.convert(tradeConfirmItem, TradeConfirmItemVO.class)).build());
    }

    /**
     * 查询客户首笔完成的交易号
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<TradeQueryFirstCompleteResponse> queryFirstCompleteTrade(@RequestBody @Valid TradeQueryFirstCompleteRequest request) {
        String tradeId = pileTradeService.queryFirstCompleteTrade(request.getCustomerId());
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
        pileTradeService.sendEmailToFinance(tradeSendEmailToFinanceRequest.getCustomerId(),
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
        pileTradeService.sendEmailTranslate();
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
        List<PileTrade> tradeList = pileTradeService.listTradeExport(tradeQueryRequest);
        return BaseResponse.success(TradeListExportResponse.builder().tradeVOList(KsBeanUtil.convert(tradeList,
                TradeVO.class)).build());
    }

    @Override
    public BaseResponse<TradeListExportResponse> listTradeExportMonth(TradeListExportRequest tradeListExportRequest) {
        TradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(tradeListExportRequest.getTradeQueryDTO(),
                TradeQueryRequest.class);
        List<PileTrade> tradeList = pileTradeService.listTradeExportMonth(tradeQueryRequest);
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
        PileTrade trade = pileTradeService.queryAll(TradeQueryRequest.builder().payOrderId(tradeByPayOrderIdRequest.getPayOrderId()).build()).get(0);
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
        return BaseResponse.success(TradeListAllResponse.builder().tradeVOList(pileTradeService.queryConfrimFailedTrades()).build());
    }


    @Override
    public BaseResponse<TradeListAllResponse> getListByLogisticsCompanyId(@RequestBody @Valid TradeGetByLogisticsCompanyIdRequest tradeListByParentIdRequest) {
        return pileTradeService.findListByByLogisticsCompanyId(tradeListByParentIdRequest.getId());
    }


    @Override
    public BaseResponse<HistoryLogisticsCompanyByCustomerIdResponse> getByCustomerId(@Valid TradeByCustomerIdRequest request) {
        return pileTradeService.getByCustomerId(request.getCustomerId());
    }

    @Override
    public BaseResponse<List<OrderSalesRanking>> salesRanking() {
        return BaseResponse.success(pileTradeService.querySalesRanking());
    }

    @Override
    public BaseResponse<Map<String,String>> getOrderTimeByCustomerIds(TradeByCustomerIdRequest request) {
        Map<String,String> localDateTimeMap = new HashMap<>();
        List<String> customerIds = request.getCustomerIds();
        customerIds.forEach(customerId -> {
            List<PileTrade> trades = pileTradeService.getCustomerLastPayOrderTime(customerId);
            Optional<PileTrade> trade =  trades.stream().findFirst();
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
    public BaseResponse<Long> getGoodsPileNum(String skuId) {
        return BaseResponse.success(pilePurchaseService.getGoodsNumBySkuId(skuId));
    }

    @Override
    public BaseResponse<PilePurchaseGoodsNumsResponse> getGoodsPileNumBySkuIds(PilePurchaseRequest request) {
        return BaseResponse.success(PilePurchaseGoodsNumsResponse.builder().goodsNumsMap(pilePurchaseService.getPileGoodsNumsBuySkuIds(request.getGoodsInfoIds())).build());
    }

    @Override
    public BaseResponse<CustomerPilePurchaseListResponse> getPilePurchaseByCustomerId(PilePurchaseRequest request) {
        com.wanmi.sbc.returnorder.pilepurchase.request.PilePurchaseRequest purchaseRequest = KsBeanUtil.convert(request, com.wanmi.sbc.returnorder.pilepurchase.request.PilePurchaseRequest.class);
        Page<PurchaseVO> purchasePage = pilePurchaseService.pageList(purchaseRequest).getPurchasePage();
        CustomerPilePurchaseListResponse customerPilePurchaseListResponse = new CustomerPilePurchaseListResponse();
        if(Objects.isNull(purchasePage) || purchasePage.isEmpty()){
            return BaseResponse.success(customerPilePurchaseListResponse);
        }
        List<PurchaseVO> purchaseVOList = purchasePage.stream().collect(Collectors.toList()).stream().filter(p -> p.getGoodsNum() > 0).collect(Collectors.toList());

        GoodsInfoViewByIdsResponse context = goodsInfoQueryProvider.listViewByIdsByMatchFlag(GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(purchaseVOList.stream().map(vp -> vp.getGoodsInfoId()).collect(Collectors.toList())).build()).getContext();

        //查询商品副标题
        GoodsByConditionRequest goodsByConditionRequest = GoodsByConditionRequest.builder()
                .goodsIds(context.getGoodsInfos().stream().map(g -> g.getGoodsId()).collect(Collectors.toList())).build();
        goodsByConditionRequest.setPageSize(context.getGoodsInfos().size());
        GoodsByConditionResponse goodsList = goodsQueryProvider.listByCondition(goodsByConditionRequest).getContext();
        Map<String, String> goodsSubtitleMap = goodsList.getGoodsVOList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitle));

        //囤货数量
        Map<String, Long> pilePurchaseNumMap = purchaseVOList.stream().collect(Collectors.toList()).stream().collect(Collectors.toMap(PurchaseVO::getGoodsInfoId, PurchaseVO::getGoodsNum));

        List<GoodsInfoVO> goodsInfoVOS = context.getGoodsInfos().stream().filter(g -> g.getStock().compareTo(BigDecimal.ZERO) > 0 && g.getAddedFlag() == 1 && g.getDelFlag().equals(DeleteFlag.NO)).collect(Collectors.toList());

        goodsInfoVOS.forEach(g->{
            g.setGoodsSubtitle(goodsSubtitleMap.getOrDefault(g.getGoodsId(),""));
            g.setPilePurchaseNum(pilePurchaseNumMap.getOrDefault(g.getGoodsInfoId(),0L));
            GoodsVO goodsVO = goodsList.getGoodsVOList().stream().filter(t -> g.getGoodsId().equals(t.getGoodsId())).findFirst().orElse(null);
            if(Objects.isNull(g.getGoodsInfoImg()) && Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getGoodsImg())){
                g.setGoodsInfoImg(goodsVO.getGoodsImg());
            }
            g.setGoods(goodsVO);
        });

        customerPilePurchaseListResponse.setGoodsInfos(new MicroServicePage<GoodsInfoVO>(goodsInfoVOS,
                request.getPageRequest(),
                purchaseVOList.size()));

        return BaseResponse.success(customerPilePurchaseListResponse);
    }

    /**
     * 查询当日订单数据
     */
    @Override
    public BaseResponse<TradeSaleStatisticResponse> getPileTradesByDate(Integer dateType) {

        List<TradeVO> tradeVOList = new ArrayList<>();

        TradeListAllRequest tradeListAllRequest = TradeListAllRequest.builder()
                .tradeQueryDTO(TradeQueryDTO.builder()
                        .beginTime(DateUtil.getStartToday())
                        .endTime(DateUtil.getEndToday())
                        .orderType(OrderType.NORMAL_ORDER)
                        .build())
                .build();

        TradeQueryRequest tradeQueryRequest = TradeQueryRequest.builder()
                .beginTime(DateUtil.getStartToday())
                .endTime(DateUtil.getEndToday())
                .orderType(OrderType.NORMAL_ORDER)
                .build();

        //参数1查询当月订单数据
        if(Objects.nonNull(dateType) && NumberUtils.INTEGER_ONE.equals(dateType)){
            tradeQueryRequest.setBeginTime(DateUtil.getStartMonth());
            tradeQueryRequest.setEndTime(DateUtil.getEndMonth());

            tradeListAllRequest.getTradeQueryDTO().setBeginTime(DateUtil.getStartMonth());
            tradeListAllRequest.getTradeQueryDTO().setEndTime(DateUtil.getEndMonth());
        }

        TradeListAllResponse tradeListAllResponse = listAll(tradeListAllRequest).getContext();

        if(Objects.nonNull(tradeListAllResponse) && CollectionUtils.isNotEmpty(tradeListAllResponse.getTradeVOList())){
            tradeVOList.addAll(tradeListAllResponse.getTradeVOList());
        }

        List<Trade> tradeList = tradeService.queryAll(tradeQueryRequest);

        List<Trade> deliveryTradeFilterList = Lists.newArrayList();
        //过滤订单金额为0,单运费订单
        if(Objects.nonNull(tradeList) && CollectionUtils.isNotEmpty(tradeList)){

            List<Trade> tradeFilterList = tradeList.stream().filter(t -> BigDecimal.ZERO.compareTo(t.getTradePrice().getTotalPrice()) == -1
                    && t.getTradePrice().getTotalPrice().compareTo(t.getTradePrice().getDeliveryPrice()) == 1
            ).collect(Collectors.toList());

            //计算运费
            deliveryTradeFilterList = tradeList.stream().filter(t -> BigDecimal.ZERO.compareTo(t.getTradePrice().getTotalPrice()) == -1
                    && t.getTradePrice().getTotalPrice().compareTo(t.getTradePrice().getDeliveryPrice()) == 0
            ).collect(Collectors.toList());

            if(Objects.nonNull(tradeFilterList)){
                tradeVOList.addAll(KsBeanUtil.convert(tradeFilterList,TradeVO.class));
            }
        }

        if(CollectionUtils.isEmpty(tradeVOList)){
            return BaseResponse.SUCCESSFUL();
        }

        List<TradeVO> tradeVOS = tradeVOList.stream().filter(t -> !t.getTradeState().getFlowState().equals(FlowState.VOID)).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(tradeVOS)){
            return BaseResponse.SUCCESSFUL();
        }

        long orderItemsNum = tradeVOS.stream().mapToLong(
                t -> t.getTradeItems().stream().mapToLong(
                        i -> i.getNum()).sum()
        ).sum();

        List<TradePriceVO> tradePriceVOList = tradeVOS.stream().map(t -> t.getTradePrice()).collect(Collectors.toList());
        tradePriceVOList.addAll(
                KsBeanUtil.convert(
                        deliveryTradeFilterList.stream().map(t -> t.getTradePrice()).collect(Collectors.toList()),TradePriceVO.class)
        );

        List<BuyerVO> buyerList = tradeVOS.stream().map(t -> t.getBuyer()).collect(Collectors.toList());

        Map<String, List<BuyerVO>> customerMap = buyerList.stream().collect(Collectors.groupingBy(b -> b.getId(), Collectors.toList()));

        return BaseResponse.success(TradeSaleStatisticResponse.builder()
                .orderNum(tradeVOS.size())
                .salePrice(tradePriceVOList.stream().map(tp->tp.getTotalPrice()).reduce(BigDecimal.ZERO,BigDecimal::add))
                .buyNum(customerMap.size())
                .orderItemNum(orderItemsNum)
                .build());
    }

    @Override
    public BaseResponse<TradeSaleStatisticResponse> getPileAndTradesPriceByDate() {

        String begin = DateUtil.getDayAdd(1);
        String end = DateUtil.getEndToday();

        //囤货金额、箱数
        List<Object[]> pileList = pilePurchaseActionRepository.statisticRecordItemPriceBySevenDate(begin, end);

        //提货金额、箱数
        List<Object[]> takeList = pileStockRecordTradeItemRepository.statisticRecordItemPriceBySevenDate(begin, end);

        //查询正常下单
        List<ReplayTradeStatisticView> replayTradeStatisticViewList = replayTradeQueryProvider.tradeTwoDaySaleStatistic().getReplayTradeStatisticViewList();

        TradeSaleStatisticResponse saleStatisticResponse = TradeSaleStatisticResponse.builder().build();

        if(CollectionUtils.isNotEmpty(replayTradeStatisticViewList)){
            //今日
            String today = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_DATE_1);
            ReplayTradeStatisticView todayTradeStatisticView = replayTradeStatisticViewList.stream().filter(r -> r.getDayTime().equals(today)).findFirst().orElse(null);
            saleStatisticResponse.setTodayTradePrice(BigDecimal.ZERO);
            if(Objects.nonNull(todayTradeStatisticView) && Objects.nonNull(todayTradeStatisticView.getTotalPrice())){
                saleStatisticResponse.setTodayTradePrice(todayTradeStatisticView.getTotalPrice());
            }
            //昨日
            ReplayTradeStatisticView yesterdayTradeStatisticView = replayTradeStatisticViewList.stream().filter(r -> r.getDayTime().equals(DateUtil.yesterdayDate())).findFirst().orElse(null);
            saleStatisticResponse.setYesterdayTradePrice(BigDecimal.ZERO);
            if(Objects.nonNull(yesterdayTradeStatisticView) && Objects.nonNull(yesterdayTradeStatisticView.getTotalPrice())){
                saleStatisticResponse.setYesterdayTradePrice(yesterdayTradeStatisticView.getTotalPrice());
            }
        }

        if(Objects.nonNull(pileList) && CollectionUtils.isNotEmpty(pileList)){
            pileList.forEach(obj->{
                String createTime = (String) obj[2];
                String price = obj[0].toString();

                log.info("createTime ======== {}", createTime);
                log.info("equals======: {}",DateUtil.nowDate().equals(createTime));
                //当日
                if(DateUtil.nowDate().equals(createTime)){
                    if(Objects.nonNull(price)){
                        saleStatisticResponse.setTodayPilePrice(Objects.isNull(price) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(price)));
                    }
//                    ReplayTradeStatisticView todayTradeStatisticView = replayTradeStatisticViewList.stream().filter(r -> r.getDayTime().equals(createTime)).findFirst().orElse(null);
//                    saleStatisticResponse.setTodayTradePrice(BigDecimal.ZERO);
//                    if(Objects.nonNull(todayTradeStatisticView) && Objects.nonNull(todayTradeStatisticView.getTotalPrice())){
//                        saleStatisticResponse.setTodayTradePrice(todayTradeStatisticView.getTotalPrice());
//                    }
                }else{
                    saleStatisticResponse.setYesterdayPilePrice(Objects.isNull(price) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(price)));
//                    ReplayTradeStatisticView yesterdayTradeStatisticView = replayTradeStatisticViewList.stream().filter(r -> r.getDayTime().equals(createTime)).findFirst().orElse(null);
//                    saleStatisticResponse.setYesterdayTradePrice(BigDecimal.ZERO);
//                    if(Objects.nonNull(yesterdayTradeStatisticView) && Objects.nonNull(yesterdayTradeStatisticView.getTotalPrice())){
//                        saleStatisticResponse.setYesterdayTradePrice(yesterdayTradeStatisticView.getTotalPrice());
//                    }
                }
            });
        }

        if(Objects.nonNull(takeList) && CollectionUtils.isNotEmpty(takeList)){
            takeList.forEach(obj->{
                String createTime = (String) obj[1];
                String price = obj[0].toString();

                log.info(" recordItemPriceByDate,createTime ======== {}", createTime);
                log.info("recordItemPriceByDate,equals======: {}",DateUtil.nowDate().equals(createTime));
                //当日
                if(DateUtil.nowDate().equals(createTime)){
                    if(Objects.nonNull(price)){
                        saleStatisticResponse.setTodayTakePrice(Objects.isNull(price) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(price)));
                    }
                }else{
                    saleStatisticResponse.setYesterdayTakePrice(Objects.isNull(price) ? BigDecimal.ZERO : BigDecimal.valueOf(Double.parseDouble(price)));
                }
            });
        }

        return BaseResponse.success(saleStatisticResponse);
    }

    private List<String> getDays(Integer days) throws ParseException {
        List<String> dayList = new ArrayList<>();
        if(days <= 0){
            return dayList;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.FMT_DATE_1);
        for(int i=0;i<days;i++){
            Date date = simpleDateFormat.parse(DateUtil.getDayAdd(i));
            dayList.add(simpleDateFormat.format(date));
        }
        return dayList;
    }
    /**
     * 查询近七日订单销售统计
     *
     */
    @Override
    public BaseResponse<TradeSaleStatisticResponse> recentSevenDaySaleStatistic() throws ParseException {
        Integer days = 7;

        String begin = DateUtil.getDayAdd(days);
        String end = DateUtil.getEndToday();
        List<String> getDays = getDays(days);

        //囤货金额、箱数
        List<Object[]> list = pilePurchaseActionRepository.statisticRecordItemPriceBySevenDate(begin, end);

        //提货金额、箱数
        List<Object[]> objects = pileStockRecordTradeItemRepository.statisticRecordItemPriceBySevenDate(begin, end);

        //正常购物车金额、箱数
        ReplayTradeRequest replayTradeRequest = new ReplayTradeRequest();
        replayTradeRequest.setStartTime(begin);
        replayTradeRequest.setEndTime(end);
        List<ReplayTradeStatisticView> replayTradeStatisticViewList = replayTradeQueryProvider.tradeSevenDaySaleStatistic(replayTradeRequest).getReplayTradeStatisticViewList();

        List<RecentSevenDaySale> recentSevenDaySaleTotalPriceList = new ArrayList<>();

        getDays.forEach(varDay->{
            RecentSevenDaySale recentSevenDaySale = new RecentSevenDaySale();
            //默认赋值
            recentSevenDaySale.setPileTotalNum(BigDecimal.ZERO);

            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(var->{
                    if(varDay.equals(var[2].toString())){
                        recentSevenDaySale.setPileTotalPrice((BigDecimal) var[0]);
                        recentSevenDaySale.setPileTotalNum((BigDecimal) var[1]);
                    }
                });
            }

            ReplayTradeStatisticView replayTradeStatisticView = replayTradeStatisticViewList.stream().filter(r -> varDay.equals(r.getDayTime())).findFirst().orElse(null);
            //正常下单箱数统计
            if(Objects.nonNull(replayTradeStatisticView)){
                recentSevenDaySale.setPileTotalNum(recentSevenDaySale.getPileTotalNum().add(BigDecimal.valueOf(replayTradeStatisticView.getTotalNum())));
                recentSevenDaySale.setTradePrice(replayTradeStatisticView.getTotalPrice());
            }

            recentSevenDaySale.setDateTime(varDay);
            recentSevenDaySaleTotalPriceList.add(recentSevenDaySale);
        });

        if(CollectionUtils.isNotEmpty(objects)){
            recentSevenDaySaleTotalPriceList.forEach(var->{
                objects.forEach(price->{
                    if(var.getDateTime().equals(price[1].toString())){
                        var.setTotalPrice((BigDecimal) price[0]);
                    }
                });
            });
        }
        TradeSaleStatisticResponse saleStatisticResponse = TradeSaleStatisticResponse.builder().build();
        saleStatisticResponse.setRecentSevenDaySaleTotalPriceList(recentSevenDaySaleTotalPriceList);
        return BaseResponse.success(saleStatisticResponse);

    }

}
