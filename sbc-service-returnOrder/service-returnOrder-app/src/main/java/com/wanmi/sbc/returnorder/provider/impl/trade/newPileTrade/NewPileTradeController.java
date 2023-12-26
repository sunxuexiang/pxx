package com.wanmi.sbc.returnorder.provider.impl.trade.newPileTrade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressListRequest;
import com.wanmi.sbc.customer.api.request.customer.DisableCustomerDetailGetByAccountRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressListResponse;
import com.wanmi.sbc.customer.api.response.customer.DisableCustomerDetailGetByAccountResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseByIdResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.returnorder.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.request.trade.newpile.GoodsPickStockIdsRequest;
import com.wanmi.sbc.returnorder.api.request.trade.newpile.NewPileTradeListByParentIdRequest;
import com.wanmi.sbc.returnorder.api.request.trade.newpile.NewPileTradeListExportRequest;
import com.wanmi.sbc.returnorder.api.request.trade.newpile.NewPileTradePageCriteriaRequest;
import com.wanmi.sbc.returnorder.api.response.trade.*;
import com.wanmi.sbc.returnorder.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeCommitResultVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeConfirmItemVO;
import com.wanmi.sbc.returnorder.receivables.request.ReceivableAddRequest;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.entity.NewPileTradeCommitResult;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeCommitResult;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.GoodsPickStock;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileOldData;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileSendData;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.OrderLogistics;
import com.wanmi.sbc.returnorder.trade.model.root.StockRecordAttachment;
import com.wanmi.sbc.returnorder.trade.model.root.TradeConfirmItem;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.GoodsPickStockRepository;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.NewPileOldDataRepository;
import com.wanmi.sbc.returnorder.trade.request.NewPileTradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.service.PileStockRecordAttachmentService;
import com.wanmi.sbc.returnorder.trade.service.TradeLogisticsService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeDataHandleService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 10:04
 */
@Validated
@RestController
@Slf4j
public class NewPileTradeController implements NewPileTradeProvider {

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private PileStockRecordAttachmentService pileStockRecordAttachmentService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private TradeLogisticsService tradeLogisticsService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private GoodsPickStockRepository goodsPickStockRepository;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private NewPileOldDataRepository newPileOldDataRepository;

    @Autowired
    private NewPileTradeDataHandleService newPileTradeDataHandleService;


    /**
     * 通过parentId获取交易单信息并将buyer.account加密
     *
     * @param tradeGetByParentRequest 交易单id {@link TradeGetByIdRequest}
     * @return 交易单信息 {@link TradeGetByIdResponse}
     */
    @Override
    public BaseResponse<NewPileTradeGetByIdResponse> getByParent(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest) {
        NewPileTrade trade = newPileTradeService.detailByParentId(tradeGetByParentRequest.getParentId());
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }
        BaseResponse<NewPileTradeGetByIdResponse> baseResponse = BaseResponse.success(NewPileTradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, NewPileTradeVO.class)).build());
        return baseResponse;
    }

    @Override
    public BaseResponse<TradeCommitResponse> newPileCommitAll(@Valid TradeCommitRequest tradeCommitRequest) {

        RLock rLock = redissonClient.getFairLock(Constants.NEW_PILE_OVER_BOOKING);
        rLock.lock();
        try {
            List<NewPileTradeCommitResult> results = newPileTradeService.newPileCommitAll(tradeCommitRequest);
            return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public BaseResponse cancel(@Valid TradeCancelRequest tradeCancelRequest) {
        return newPileTradeService.cancel(tradeCancelRequest);
    }


    @Override
    public BaseResponse<TradeCommitResponse> newPilePickCommitAll(@Valid TradeCommitRequest tradeCommitRequest) {
        RLock rLock = redissonClient.getFairLock(Constants.NEW_PILE_PICK_OVER_BOOKING);
        rLock.lock();
        try {
            List<TradeCommitResult> results = newPileTradeService.newPilePickCommitAll(tradeCommitRequest);
            return BaseResponse.success(new TradeCommitResponse(KsBeanUtil.convert(results, TradeCommitResultVO.class)));
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public BaseResponse<NewPileTradeGetByIdResponse> getById(@Valid TradeGetByIdRequest tradeGetByIdRequest) {
        NewPileTrade newPileTrade = newPileTradeService.detail(tradeGetByIdRequest.getTid());
        if (Objects.isNull(newPileTrade)) {
            return BaseResponse.success(NewPileTradeGetByIdResponse.builder().tradeVO(null).build());
        }
        log.info("打印原始数据中的数量jeffrey: {}", JSON.toJSONString(newPileTrade));
        //求出商品总件数
        Long allNum = 0L;
        for (TradeItem tradeItem : newPileTrade.getTradeItems()) {
            Long num = tradeItem.getNum() == null ? 0L : tradeItem.getNum();
            allNum += num;
        }
        if (newPileTrade.getGoodsTotalNum() == null) {
            newPileTrade.setGoodsTotalNum(allNum);
        }
        log.info("商品总件数是: {}", allNum);
        if (Objects.nonNull(newPileTrade) && Objects.nonNull(newPileTrade.getBuyer()) && StringUtils.isNotEmpty(newPileTrade.getBuyer()
                .getAccount())) {
            newPileTrade.getBuyer().setAccount(ReturnOrderService.getDexAccount(newPileTrade.getBuyer().getAccount()));
        }
        String employeeId = newPileTrade.getBuyer().getEmployeeId();
        String employeeName  = "";
        if(StringUtils.isNotEmpty(employeeId)){
            employeeName = employeeQueryProvider.getById(EmployeeByIdRequest.builder().employeeId(employeeId).build()).getContext().getEmployeeName();
        }
        BaseResponse<NewPileTradeGetByIdResponse> baseResponse = BaseResponse.success(NewPileTradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(newPileTrade, NewPileTradeVO.class)).build());
        baseResponse.getContext().getTradeVO().setEmployeeName(employeeName);
        return baseResponse;
    }

    /**
     * 根据快照封装订单确认页信息
     *
     * @param tradeQueryPurchaseInfoRequest 交易单快照信息 {@link TradeQueryPurchaseInfoRequest}
     * @return 交易单确认项 {@link TradeQueryPurchaseInfoResponse}
     */
    @Override
    public BaseResponse<TradeQueryPurchaseInfoResponse> queryPurchaseInfo(TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest) {
        TradeConfirmItem tradeConfirmItem = newPileTradeService.getPurchaseInfo(
                KsBeanUtil.convert(tradeQueryPurchaseInfoRequest.getTradeItemGroupDTO(), TradeItemGroup.class));
        return BaseResponse.success(TradeQueryPurchaseInfoResponse.builder()
                .tradeConfirmItemVO(KsBeanUtil.convert(tradeConfirmItem, TradeConfirmItemVO.class)).build());
    }

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     *
     * @param tradeGetGoodsRequest 商品skuid列表 {@link TradeGetGoodsRequest}
     * @return 商品信息列表 {@link GoodsInfoViewListResponse}
     */
    @Override
    public BaseResponse<TradeGetGoodsResponse> getGoods(@RequestBody @Valid TradeGetGoodsRequest tradeGetGoodsRequest) {
        return BaseResponse.success(newPileTradeService.getGoodsResponseMatchFlag(tradeGetGoodsRequest.getSkuIds(), tradeGetGoodsRequest.getWareId()
                , tradeGetGoodsRequest.getWareHouseCode(), tradeGetGoodsRequest.getMatchWareHouseFlag()));
    }

    /**
     * @param tradePageCriteriaRequest 带参分页参数 {@link NewPileTradePageCriteriaRequest}
     * @return
     */
    @Override
    public BaseResponse<NewPileTradePageCriteriaResponse> supplierPageCriteria(@RequestBody @Valid NewPileTradePageCriteriaRequest tradePageCriteriaRequest) {
        tradePageCriteriaRequest.getTradePageDTO().setProviderTradeId(null);
        tradePageCriteriaRequest.getTradePageDTO().setProviderName(null);
        NewPileTradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradePageCriteriaRequest.getTradePageDTO(), NewPileTradeQueryRequest.class);
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<NewPileTrade> page = newPileTradeService.page(criteria, tradeQueryRequest);
        MicroServicePage<NewPileTradeVO> tradeVOS = KsBeanUtil.convertPage(page, NewPileTradeVO.class);
        List<NewPileTradeVO> tradeVOList = tradeVOS.getContent();

        if (CollectionUtils.isNotEmpty(tradeVOS.getContent())) {
            //填充仓库名
            BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());
            Map<Long, String> wareMap = list.getContext().getWareHouseVOList().stream().collect(Collectors.toMap(WareHouseVO::getWareId, WareHouseVO::getWareName));
            tradeVOList.forEach(trade->{
                if(Objects.nonNull(trade.getWareId())){
                    trade.setWareName(wareMap.getOrDefault(trade.getWareId(),""));
                }
            });

            List<String> providerTradeIds = tradeVOS.getContent().stream().map(NewPileTradeVO::getId).collect(Collectors.toList());
            List<String> orderList = pileStockRecordAttachmentService.getCachePushKingdeeOrder(providerTradeIds);

            //通过提货订单查询囤货订单
            List<StockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentService.getStockAssociatedOrderTid(providerTradeIds);
            Map<String, List<StockRecordAttachment>> stockRecordMap = stockRecordAttachmentList.stream().collect(Collectors.groupingBy(StockRecordAttachment::getTid));

            for (NewPileTradeVO tradeVO : tradeVOList) {
                Boolean isContainsTrade = false;
                if (CollectionUtils.isNotEmpty(tradeVO.getTradeVOList())) {
                    List<Long> storeList = tradeVO.getTradeVOList().stream().map(NewPileTradeVO::getStoreId).collect(Collectors.toList());
                    isContainsTrade = storeList.contains(null);
                }
                tradeVO.setIsContainsTrade(isContainsTrade);
                // 设置囤货单ID列表
                List<StockRecordAttachment> attachments = stockRecordMap.get(tradeVO.getId());
                if(CollectionUtils.isNotEmpty(attachments)){
                    tradeVO.setStockOrder(Arrays.asList(attachments.stream().map(StockRecordAttachment::getOrderCode).findFirst().get()));
                }

                if (CollectionUtils.isNotEmpty(orderList)) {
                    orderList.stream().forEach(cachePushKingdeeOrder -> {
                        if (tradeVO.getId().equals(cachePushKingdeeOrder)) {
                            tradeVO.setIntercept(true);
                            tradeVO.setNewVilageFlag(true);
                        }
                    });
                }
            }
            log.info("TradeQueryController.supplierPageCriteria tradeVOList:{}", JSONObject.toJSONString(tradeVOList));
            encapsulation(tradeVOList);
            tradeVOS.setContent(tradeVOList);
            return BaseResponse.success(NewPileTradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
        }
        encapsulation(tradeVOList);
        tradeVOS.setContent(tradeVOList);
        return BaseResponse.success(NewPileTradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
    }

    @Override
    public BaseResponse<NewPileTradeGetByIdResponse> getByIdManager(@RequestBody @Valid TradeGetByIdManagerRequest request) {
        NewPileTrade trade = newPileTradeService.detail(request.getTid());
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
        NewPileTradeVO providerTradeVOS = KsBeanUtil.convert(trade, NewPileTradeVO.class);


        BaseResponse<NewPileTradeGetByIdResponse> baseResponse = BaseResponse.success(NewPileTradeGetByIdResponse.builder().
                tradeVO(KsBeanUtil.convert(trade, NewPileTradeVO.class)).build());
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
        Map<String, String> collect = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getGoodsSubtitle, (a, b) -> a));

        trade.getTradeDelivers().forEach(v->{
            v.getShippingItems().forEach(b->{
                b.setSpecDetails(collect.get(b.getSkuId()));
            });
        });
        baseResponse.getContext().getTradeVO().setTradeVOList(Arrays.asList(providerTradeVOS));
        baseResponse.getContext().getTradeVO().setEmployeeName(employeeName);

        //填充已提数量
        Map<String, Long> stockMap = goodsPickStockRepository.findByNewPileTradeNos(Arrays.asList(request.getTid()))
                .stream().filter(v -> v.getStock() > 0)
                .collect(Collectors.toMap(GoodsPickStock::getGoodsInfoId, GoodsPickStock::getStock, (a, b) -> a));

        providerTradeVOS.getTradeItems().forEach(item -> {
            item.setUseNum(item.getNum() - stockMap.getOrDefault(item.getSkuId(), 0L));
        });
        return baseResponse;
    }

    private void encapsulation(List<NewPileTradeVO> tradeVOS){
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
            });
        }else {
            log.info("=================================================业务员数据为空！:{}===============================");
        }
        log.info("=================================================业务员数据分配结束===============================");
    }

    /**
     * 新增线下收款单(包含线上线下的收款单)
     *
     * @param tradeAddReceivableRequest 收款单平台信息{@link TradeAddReceivableRequest}
     * @return 支付结果 {@link TradeDefaultPayResponse}
     */
    @Override
    public BaseResponse addReceivable(@RequestBody @Valid TradeAddReceivableRequest tradeAddReceivableRequest) {
        newPileTradeService.addReceivable(KsBeanUtil.convert(tradeAddReceivableRequest.getReceivableAddDTO(),
                ReceivableAddRequest.class), tradeAddReceivableRequest.getPlatform(),
                tradeAddReceivableRequest.getOperator());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param tradePageCriteriaRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @Override
    public BaseResponse<NewPileTradePageCriteriaResponse> pageCriteria(@RequestBody @Valid NewPileTradePageCriteriaRequest tradePageCriteriaRequest) {
        NewPileTradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradePageCriteriaRequest.getTradePageDTO(), NewPileTradeQueryRequest.class);
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<NewPileTrade> page = newPileTradeService.page(criteria, tradeQueryRequest);

        MicroServicePage<NewPileTradeVO> tradeVOS = KsBeanUtil.convertPage(page, NewPileTradeVO.class);
        //获取所有订单号
        List<String> logisticsIds = tradeVOS.stream().map(t -> t.getId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(logisticsIds)){
            //查询订单物流信息
            List<OrderLogistics> logistics = tradeLogisticsService.findByLogisticIds(logisticsIds);
            for (NewPileTradeVO vo : tradeVOS){
                if(CollectionUtils.isNotEmpty(logistics)){
                    OrderLogistics orderLogistics = logistics.stream().filter(l -> vo.getId().equals(l.getLogisticId())).findFirst().orElse(null);
                    if(Objects.nonNull(orderLogistics)){
                        vo.setLogistics(JSONObject.toJSONString(orderLogistics));
                    }
                }
            }
        }

        return BaseResponse.success(NewPileTradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
    }

    @Override
    public BaseResponse<NewPileTradeListByParentIdResponse> getOrderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest) {
        List<NewPileTrade> tradeList = newPileTradeService.detailsByParentId(tradeListByParentIdRequest.getParentTid());
        if (tradeList.isEmpty()) {
            return BaseResponse.success(NewPileTradeListByParentIdResponse.builder().tradeVOList(Collections.emptyList()).build());
        }
        //统一设置账号加密后的买家信息
        List<NewPileTradeVO> tradeVOList = tradeList.stream().map(i -> {
            return KsBeanUtil.convert(i, NewPileTradeVO.class);
        }).collect(Collectors.toList());
        return BaseResponse.success(NewPileTradeListByParentIdResponse.builder().
                tradeVOList(tradeVOList).build());
    }

    @Override
    public BaseResponse<List<GoodsPickStockResponse>> getNewPileGoodsNumByCustomer(@Valid NewPileGoodsNumByCustomerRequest request) {
        List<GoodsPickStock> allDetailByCustomer = newPileTradeService.getNewPileGoodsNumByCustomer(request);
        return BaseResponse.success(KsBeanUtil.convertList(allDetailByCustomer, GoodsPickStockResponse.class));
    }

    @Override
    public BaseResponse<List<Long>> getNewPileTradeWareHoseCustomer(@Valid NewPileGoodsNumByCustomerRequest request) {
        return newPileTradeService.getNewPileTradeWareHoseCustomer(request);
    }

    @Override
    public BaseResponse update(@Valid NewPileTradeUpdateRequest tradeUpdateRequest) {
        newPileTradeService.updateTradeInfo(tradeUpdateRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 已在其他方法实现： newAudit(TradeAuditRequest tradeAuditRequest)
     * @param tradeAuditRequest 订单审核相关必要信息 {@link TradeAuditRequest}
     * @return
     */
    @Override
    public BaseResponse audit(TradeAuditRequest tradeAuditRequest) {
        return null;
    }

    /**
     * 已在其他方法实现
     * @param tradeAuditBatchRequest 订单审核相关必要信息 {@link TradeAuditBatchRequest}
     * @return
     */
    @Override
    public BaseResponse auditBatch(TradeAuditBatchRequest tradeAuditBatchRequest) {
        return null;
    }

    @Override
    public BaseResponse<List<NewPileTradeVO>> listByPileNos(List<String> pileNos) {
        List<NewPileTrade> newPileTrades = newPileTradeService.listByPileNos(pileNos);
        if (CollectionUtils.isEmpty(newPileTrades)) {
            return BaseResponse.success(Collections.emptyList());
        }

        log.info("listByPileNos查找到的囤货订单数: {}", newPileTrades.size());

        List<NewPileTradeVO> list = new ArrayList<>();
        newPileTrades.forEach(newPileTrade -> {
            //求出商品总件数
            Long allNum = 0L;
            for (TradeItem tradeItem : newPileTrade.getTradeItems()) {
                Long num = tradeItem.getNum() == null ? 0L : tradeItem.getNum();
                allNum += num;
            }
            if (newPileTrade.getGoodsTotalNum() == null) {
                newPileTrade.setGoodsTotalNum(allNum);
            }
            log.info("商品总件数是: {}", allNum);
            if (Objects.nonNull(newPileTrade) && Objects.nonNull(newPileTrade.getBuyer()) && StringUtils.isNotEmpty(newPileTrade.getBuyer()
                    .getAccount())) {
                newPileTrade.getBuyer().setAccount(ReturnOrderService.getDexAccount(newPileTrade.getBuyer().getAccount()));
            }
//            String employeeId = newPileTrade.getBuyer().getEmployeeId();
//            String employeeName  = "";
//            if(StringUtils.isNotEmpty(employeeId)){
//                employeeName = employeeQueryProvider.getById(EmployeeByIdRequest.builder().employeeId(employeeId).build()).getContext().getEmployeeName();
//            }
            list.add(KsBeanUtil.convert(newPileTrade, NewPileTradeVO.class));
        });
        return BaseResponse.success(list);
    }

    @Override
    public BaseResponse<NewPileTradeListExportResponse> listTradeExport(NewPileTradeListExportRequest newPileTradeListExportRequest) {

        NewPileTradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(newPileTradeListExportRequest.getNewPileTradeQueryDTO(),
                NewPileTradeQueryRequest.class);
        List<NewPileTrade> tradeList = newPileTradeService.listTradeExport(tradeQueryRequest);
        tradeList.forEach(trade -> {
            PayTradeRecordRequest recordRequest  = new PayTradeRecordRequest();
            recordRequest.setBusinessId(trade.getParentId());
            PayTradeRecordResponse record = payQueryProvider.findByBusinessId(recordRequest).getContext();
            if(Objects.isNull(record) || !record.getStatus().equals(TradeStatus.SUCCEED)){
                recordRequest.setBusinessId(trade.getId());
                record = payQueryProvider.findByBusinessId(recordRequest).getContext();
            }
            if(Objects.nonNull(record)){
                if(StringUtils.isNotEmpty(record.getPayOrderNo())){
                    trade.setPayOrderNo("31YT" + record.getPayOrderNo());
                }
            }
        });
        return BaseResponse.success(NewPileTradeListExportResponse.builder().newPileTradeVOList(KsBeanUtil.convert(tradeList,
                NewPileTradeVO.class)).build());
    }

    @Override
    public BaseResponse<NewPileTradeListByParentIdResponse> getOrderListByParentIds(NewPileTradeListByParentIdRequest request) {
        List<NewPileTrade> tradeList = newPileTradeService.detailsByParentIds(request.getParentTid());
        if (tradeList.isEmpty()) {
            return BaseResponse.success(NewPileTradeListByParentIdResponse.builder().tradeVOList(Collections.emptyList()).build());
        }
        //统一设置账号加密后的买家信息
        List<NewPileTradeVO> tradeVOList = tradeList.stream().map(i -> KsBeanUtil.convert(i, NewPileTradeVO.class)).collect(Collectors.toList());
        return BaseResponse.success(NewPileTradeListByParentIdResponse.builder().
                tradeVOList(tradeVOList).build());
    }

    @Override
    public BaseResponse<List<GoodsPickStockResponse>> findByNewPileTradeNos(GoodsPickStockIdsRequest request) {
        List<GoodsPickStock> byNewPileTradeNos = goodsPickStockRepository.findByNewPileTradeNos(request.getNewPileTradeIds());
        List<GoodsPickStockResponse> convert = KsBeanUtil.convert(byNewPileTradeNos, GoodsPickStockResponse.class);
        return BaseResponse.success(convert);
    }

    @Override
    public BaseResponse newPileOldDataHandler() {
        //1，查出去年的囤货数据
        List<NewPileOldData> newPileOldDataList =  newPileTradeService.newPileOldData();
        if(newPileOldDataList.isEmpty()){
            log.info("当前没有要同步的囤货数据....");
            return BaseResponse.SUCCESSFUL();
        }
        Map<String,  List<NewPileOldData>> map = new HashMap<>();
        for (NewPileOldData data : newPileOldDataList){
            if(Objects.isNull(data.getErpNo()) || Objects.isNull(data.getPrice()) || Objects.isNull(data.getGoodsNum())
                    || Objects.isNull(data.getGoodsInfoId()) || Objects.isNull(data.getGoodsName()) || Objects.isNull(data.getAccount())){
                data.setState(1);
            }else{
                data.setState(0);
                data.setWareId(1);
                List<NewPileOldData> copy =  map.get(data.getAccount());
                if(copy == null){
                    copy = new ArrayList<>();
                }
                if(!copy.isEmpty()){
                    boolean bool = false;
                    for(NewPileOldData data1 : copy){
                        if(data.getGoodsInfoId().equals(data1.getGoodsInfoId())){
                            data1.setGoodsNum(data1.getGoodsNum() + data.getGoodsNum());
                            data1.setPrice(data1.getPrice().add(data.getPrice()));
                            bool = true;
                        }
                    }
                    if(!bool){
                        copy.add(data);
                    }
                }else{
                    copy.add(data);
                }
                map.put(data.getAccount(),copy);
            }
        }

        //2，存入数据，异常订单做其他标记，不处理
       newPileTradeService.saveNewPileOldData(newPileOldDataList);
        //3，生成囤货数据（记住回调时候产生的数据）
        log.info("old pile data :{}",JSONObject.toJSONString(newPileOldDataList));
        for (Map.Entry<String, List<NewPileOldData>> entry : map.entrySet()) {
            String account = entry.getKey();
            List<NewPileOldData> list = entry.getValue();
            //拼快照请求数据
            Map<String, Object> kuaizhao = new HashMap<>();
            kuaizhao.put("forceConfirm", false);
            kuaizhao.put("retailTradeMarketingList", new ArrayList<>());
            kuaizhao.put("matchWareHouseFlag",true);
            kuaizhao.put("tradeMarketingList", new ArrayList<>());
            kuaizhao.put("retailTradeItems",new ArrayList<>());
            List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoQueryProvider.listByCondition(DevanningGoodsInfoListByConditionRequest.builder()
                            .goodsInfoIds(list.stream().map(NewPileOldData ::getGoodsInfoId).collect(Collectors.toList())).wareId(1L)
                    .build()).getContext().getDevanningGoodsInfoVOS();
            Map<String, DevanningGoodsInfoVO> devMap = devanningGoodsInfoVOS.stream().collect(Collectors.toMap(DevanningGoodsInfoVO::getGoodsInfoId, Function.identity(), (a, b) -> a));

            List<Map<String,Object>> kzList = new ArrayList<>();



            for (NewPileOldData data : list){
                DevanningGoodsInfoVO dev = devMap.get(data.getGoodsInfoId());
                if(dev != null){
                    Map trdeItem = new HashMap();
                    trdeItem.put("devanningId", dev.getDevanningId());
                    trdeItem.put("goodsName",dev.getGoodsInfoName());
                    trdeItem.put("devanningskuId",dev.getDevanningId());
                    trdeItem.put("skuId",dev.getGoodsInfoId());
                    trdeItem.put("divisorFlag","1.00");
                    trdeItem.put("erpSkuNo",data.getErpNo());
                    trdeItem.put("num",data.getGoodsNum());
                    trdeItem.put("parentGoodsInfoId",dev.getParentGoodsInfoId());
                    kzList.add(trdeItem);
                }else {
                    newPileOldDataRepository.updateStatus(account,data.getGoodsInfoId());
                    continue;
                }

            }
            kuaizhao.put("tradeItems",kzList);

            if (!list.stream().findAny().isPresent()){
                newPileOldDataRepository.updateByAccountStatus(account );
                continue;
                //异常数据记录
            }
                NewPileOldData newPileOldData = list.stream().findAny().get();
                //生成订单
                Map<String ,Object> orderMap = new HashMap<>();
                CustomerDeliveryAddressListRequest request = new CustomerDeliveryAddressListRequest();
                request.setCustomerId(newPileOldData.getCustomerId());
                BaseResponse<CustomerDeliveryAddressListResponse> customerDeliveryAddressListResponseBaseResponse =
                        customerDeliveryAddressQueryProvider.listByCustomerId(request);

                Optional<CustomerDeliveryAddressVO> any = customerDeliveryAddressListResponseBaseResponse.getContext().getCustomerDeliveryAddressVOList().stream().findAny();
                if (!any.isPresent()){
                    continue;
                    //异常数据记录
                }
                orderMap.put("consigneeId",any.get().getDeliveryAddressId());
                orderMap.put("wareId",1);
                orderMap.put("cityCode",430100);
                orderMap.put("walletBalance",0);
                orderMap.put("consigneeUpdateTime", DateUtil.format(new Date(),DateUtil.FMT_TIME_4));
                orderMap.put("orderSource","APP");
                orderMap.put("matchWareHouseFlag",true);
                orderMap.put("forceCommit",false);
                orderMap.put("consigneeAddress","湖南省长沙市雨花区高桥街道-高桥大市场酒水食品城农副区喜吖吖大楼(喜吖吖大楼)");

                Map<String ,Object> orderMap1 = new HashMap<>();
                orderMap1.put("storeId","123457929");
                orderMap1.put("buyerRemark","");
                orderMap1.put("invoiceType","-1");
                orderMap1.put("payType","0");
                orderMap1.put("ngDate","");
                orderMap1.put("ngDate","wareId");
                orderMap1.put("deliverWay",2);






                List<Map<String,Object>> storeCommitInfoLists = new LinkedList<>();
                storeCommitInfoLists.add(orderMap1);
                orderMap.put("storeCommitInfoList",storeCommitInfoLists);


                //生成购物车
                Map<String,Object> shopCarMap = new HashMap<>();
                shopCarMap.put("forceUpdate",false);

                List<Map<String,Object>> goodsInfos = new LinkedList<>();

                for (NewPileOldData data : list){
                    DevanningGoodsInfoVO dev = devMap.get(data.getGoodsInfoId());
                    if(dev != null){
                        Map trdeItem = new HashMap();
                        trdeItem.put("buyCount",data.getGoodsNum());
                        trdeItem.put("devanningId", dev.getDevanningId());
                        trdeItem.put("goodsInfoId",dev.getGoodsInfoId());
                        trdeItem.put("price",dev.getMarketPrice());
                        goodsInfos.add(trdeItem);
                    }
                }
                shopCarMap.put("goodsInfos",goodsInfos);
                shopCarMap.put("matchWareHouseFlag",true);
                shopCarMap.put("wareId",1);
                NewPileSendData newPileSendData = new NewPileSendData();
                newPileSendData.setAccount(account);
                newPileSendData.setConfirmAllPileNew(JSONObject.toJSONString(kuaizhao));
                newPileSendData.setPileNewCommitAll(JSONObject.toJSONString(orderMap));
                newPileSendData.setNewbatchAdd(JSONObject.toJSONString(shopCarMap));
                newPileTradeService.saveNewPileOldData(newPileSendData);


        }

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateOldDataMongoAndPayOrder(String tid) {

            newPileTradeService.compensateOldPileTadeStauesAndPayInfo(tid);

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse returnCoupon(String tid, String rid) {
        newPileTradeService.returnCoupon(tid, rid);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<String> handleNanChangPileData(Map<String, String> map) {
        Assert.notNull(map,"参数不能为null");
        Assert.notNull(map.get("customerAccount"),"customerAccount：客户账号不能为空");
        newPileTradeDataHandleService.handleNanChangPileData(map.get("customerAccount"));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<String> handleNanChangTakeData(Map<String, List<String>> map) {
        Assert.notNull(map,"参数不能为null");
        Assert.notNull(map.get("takeIds"),"takeIds：提货单集合不能为空");
        newPileTradeDataHandleService.handleNanChangTakeData(map.get("takeIds"));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 通过parentId获取交易单列表信息
     *
     */
    @Override
    public BaseResponse<List<NewPileTradeVO>> getListByParentId(@RequestBody @Valid TradeGetByParentRequest tradeGetByParentRequest) {
        List<NewPileTrade> tradeList = newPileTradeService.detailsByParentId(tradeGetByParentRequest.getParentId());
        List<NewPileTradeVO> tradeVOList = tradeList.stream().map(item->{
            return KsBeanUtil.convert(item, NewPileTradeVO.class);
        }).collect(Collectors.toList());

        return BaseResponse.success(tradeVOList);
    }

}
