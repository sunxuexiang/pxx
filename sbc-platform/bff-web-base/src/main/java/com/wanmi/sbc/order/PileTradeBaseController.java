package com.wanmi.sbc.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.request.PaymentRecordRequest;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.LonLatUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstobeevaluate.GoodsTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.storetobeevaluate.StoreTobeEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoVerifyRequest;
import com.wanmi.sbc.goods.api.request.flashsalegoods.IsSecKillRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateQueryRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAndStockListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.storetobeevaluate.StoreTobeEvaluateQueryRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeListForUseByCustomerIdRequest;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityFreeDeliveryByIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.enums.GiftType;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.RecruitApplyType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.order.api.provider.groupon.GrouponProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.*;
import com.wanmi.sbc.order.api.request.groupon.GrouponOrderValidRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderListRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PilePurchaseRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseGetGoodsMarketingRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseQueryRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnCountByConditionRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderListResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.order.api.response.purchase.CustomerPilePurchaseListResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PilePurchaseResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseGetGoodsMarketingResponse;
import com.wanmi.sbc.order.api.response.trade.*;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.order.request.*;
import com.wanmi.sbc.order.response.DeliveryHomeFlagResponse;
import com.wanmi.sbc.order.response.OrderTodoResp;
import com.wanmi.sbc.order.response.TradeConfirmResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.DeliveryQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.DeliveryQueryRequest;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseQueryResponse;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.InventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//import com.wanmi.sbc.order.api.response.purchase.CustomerPilePurchaseListResponse;

/**
 * <p>订单公共Controller</p>
 * Created by of628-wenzhi on 2017-07-10-下午4:12.
 */
@Api(tags = "PileTradeBaseController", description = "囤货订单公共服务API")
@RestController
@RequestMapping("/pileTrade")
@Slf4j
@Validated
public class PileTradeBaseController {

    @Autowired
    private PileTradeProvider pileTradeProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private PurchaseQueryProvider purchaseQueryProvider;

    @Autowired
    private PileTradeItemProvider pileTradeItemProvider;

    @Autowired
    private PileTradeItemQueryProvider tradeItemQueryProvider;

    @Resource
    private VerifyQueryProvider verifyQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Resource
    private DeliveryQueryProvider deliveryQueryProvider;

    @Resource
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Resource
    private AuditQueryProvider auditQueryProvider;

    @Resource
    private MarketingLevelPluginProvider marketingLevelPluginProvider;

    @Resource
    private StoreQueryProvider storeQueryProvider;

    @Resource
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Resource
    private DistributorGoodsInfoQueryProvider distributorGoodsInfoQueryProvider;

    @Resource
    private DistributionCacheService distributionCacheService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GrouponProvider grouponProvider;

    @Autowired
    private GrouponActivityQueryProvider grouponActivityQueryProvider;

    @Autowired
    private GrouponInstanceQueryProvider grouponInstanceQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private ProviderTradeProvider providerTradeProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private ReturnPileOrderQueryProvider returnPileOrderQueryProvider;

    @Autowired
    private GoodsTobeEvaluateQueryProvider goodsTobeEvaluateQueryProvider;

    @Autowired
    private StoreTobeEvaluateQueryProvider storeTobeEvaluateQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;


    /**
     * 查询订单详情
     */
    @ApiOperation(value = "查询订单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> details(@PathVariable String tid) {
        TradeGetByIdResponse tradeGetByIdResponse =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext();
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        TradeVO detail = tradeGetByIdResponse.getTradeVO();

        //查询父订单总价
        TradeListByParentIdResponse parentTrades = pileTradeQueryProvider.getOrderListByParentId(TradeListByParentIdRequest.builder().parentTid(detail.getParentId()).build()).getContext();

        if(CollectionUtils.isNotEmpty(parentTrades.getTradeVOList())){
            BigDecimal parentTotalPrice = parentTrades.getTradeVOList().stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                    .stream().map(tp -> tp.getTotalPrice()).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            detail.getTradePrice().setParentTotalPrice(parentTotalPrice);
        }


        checkUnauthorized(tid, tradeGetByIdResponse.getTradeVO());
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        TradeStateVO tradeState = detail.getTradeState();
        boolean canReturnFlag =
                tradeState.getFlowState() == FlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                        && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState.getFlowState
                        () != FlowState.VOID);
        canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(detail.getTradeState().getDeliverStatus())) {
            canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                    .trade(KsBeanUtil.convert(tradeGetByIdResponse.getTradeVO(), TradeDTO.class)).build()).getContext().getCanReturnFlag();
        }
        detail.setCanReturnFlag(canReturnFlag);

        //计算订单商品分摊金额
//        BigDecimal goodsRealPrice = detail.getTradePrice().getTotalPrice().subtract(detail.getTradePrice().getDeliveryPrice());
//        BigDecimal goodsPrice = detail.getTradePrice().getGoodsPrice();
//
//        BigDecimal countSharePrice = BigDecimal.ZERO;
        List<TradeItemVO> tradeItems = detail.getTradeItems();
        for (int i = 0; i < tradeItems.size(); i++) {
            TradeItemVO item = tradeItems.get(i);
//            if (i == tradeItems.size() - 1) {
//                item.setSharePrice(goodsRealPrice.subtract(countSharePrice));
//                continue;
//            }
//            //商品金额 / 商品总金额
//            BigDecimal scale = scale(item.getPrice().multiply(new BigDecimal(item.getNum())), goodsPrice);
//            BigDecimal sharePrice = scale.multiply(goodsRealPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
            item.setSharePrice(item.getSplitPrice());
            //  countSharePrice = countSharePrice.add(item.getSharePrice());

        }
        return BaseResponse.success(detail);
    }

    private BigDecimal scale(BigDecimal b1, BigDecimal b2) {
        //倍率计算
        BigDecimal again = new BigDecimal(10);
        b1 = b1.multiply(again);
        b2 = b2.multiply(again);
        //向原理0的方向舍入
        return b1.divide(b2, 10, BigDecimal.ROUND_UP);
    }


    /**
     * 查询拆单订单 合并之后返回
     */
    @ApiOperation(value = "根据parentId查询订单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "parentId", value = "拆单后订单父ID", required = true)
    @RequestMapping(value = "get/{parentId}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> detailByParentId(@PathVariable String parentId) {
        TradeGetByIdResponse tradeGetByIdResponse =
                pileTradeQueryProvider.getByParent(TradeGetByParentRequest.builder().parentId(parentId).build()).getContext();
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        TradeVO detail = tradeGetByIdResponse.getTradeVO();
        checkUnauthorized(parentId, tradeGetByIdResponse.getTradeVO());
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        TradeStateVO tradeState = detail.getTradeState();
        boolean canReturnFlag =
                tradeState.getFlowState() == FlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                        && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState.getFlowState
                        () != FlowState.VOID);
        canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        detail.setCanReturnFlag(canReturnFlag);
        return BaseResponse.success(detail);
    }


    /**
     * 查询订单 代付人 使用
     */
    @ApiOperation(value = "根据parentId或tid查询订单详情")
    @RequestMapping(value = "getParentIdOrTid", method = RequestMethod.POST)
    public BaseResponse<TradeVO> detailByParentIdOrTid(@RequestBody @Valid TradeParentTidRequest tradeParentTidRequest) {
        TradeGetByIdResponse tradeGetByIdResponse = null;
        if (StringUtils.isNotBlank(tradeParentTidRequest.getParentId())) {
            tradeGetByIdResponse = pileTradeQueryProvider.getByParent(TradeGetByParentRequest.builder().
                    parentId(tradeParentTidRequest.getParentId()).build()).getContext();
        } else if (StringUtils.isNotBlank(tradeParentTidRequest.getTId())) {
            tradeGetByIdResponse =
                    pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().
                            tid(tradeParentTidRequest.getTId()).build()).getContext();
        }
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        TradeVO detail = tradeGetByIdResponse.getTradeVO();

//        checkUnauthorized(parentId, tradeGetByIdResponse.getTradeVO());  不做校验

        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        TradeStateVO tradeState = detail.getTradeState();
        boolean canReturnFlag =
                tradeState.getFlowState() == FlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                        && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState.getFlowState
                        () != FlowState.VOID);
        canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        detail.setCanReturnFlag(canReturnFlag);
        return BaseResponse.success(detail);
    }



    /**
     * 从采购单中确认订单商品
     */
    @ApiOperation(value = "从采购单中确认订单商品")
    @RequestMapping(value = "/confirm", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse confirm(@RequestBody @Valid TradeItemConfirmRequest confirmRequest) {
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
        List<TradeItemDTO> tradeItems = confirmRequest.getTradeItems().stream().map(
                o -> TradeItemDTO.builder().skuId(o.getSkuId()).erpSkuNo(o.getErpSkuNo()).num(o.getNum()).build()
        ).collect(Collectors.toList());
        //验证采购单
        List<String> skuIds =
                confirmRequest.getTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
        PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
        purchaseQueryRequest.setCustomerId(customerId);
        purchaseQueryRequest.setGoodsInfoIds(skuIds);
        purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        PurchaseQueryResponse purchaseQueryResponse = purchaseQueryProvider.query(purchaseQueryRequest).getContext();
        List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

        if (CollectionUtils.isEmpty(exsistSku)) {
            throw new SbcRuntimeException("K-050205");
        }
        List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
        if (skuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
            throw new SbcRuntimeException("K-050205");
        }
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        GoodsInfoResponse response = getGoodsResponseNew(skuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());

        //商品验证
        verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true,customerId));
        verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
        //营销活动校验
        List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
                (), Collections.emptyList(), tradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();

        //限定区域指定商品限购校验
        Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        GoodsInfoListByIdsRequest request = new GoodsInfoListByIdsRequest();
        request.setGoodsInfoIds(skuIds);
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(request).getContext().getGoodsInfos();
        log.info("==============>getProvinceId:{},getCityId:{}",deliveryAddress.getProvinceId(),deliveryAddress.getCityId());
        // log.info("==============>goodsInfos:{}",goodsInfos);
        goodsInfos.forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
                List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                // log.info("==============>singleAreaList:{}",singleAreaList);
                if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())){
                    if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
                        log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                        if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()){
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
                        }
                    }
                }
            }
        });

        return pileTradeItemProvider.snapshot(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems
                        (tradeItems)
                .tradeMarketingList(tradeMarketingList)
                .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }

    private void validShopGoods(List<GoodsInfoVO> goodsInfoVOS) {
        goodsInfoVOS.stream().forEach(goodsInfo -> {
            if (goodsInfo.getGoodsStatus() == GoodsStatus.INVALID) {
                throw new SbcRuntimeException("K-050117");
            }
        });
    }


    /**
     * 立即购买
     */
    @ApiOperation(value = "立即购买")
    @RequestMapping(value = "/immediate-buy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse immediateBuy(@RequestBody @Valid ImmediateBuyNewRequest confirmRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        List<TradeItemDTO> tradeItems = KsBeanUtil.convertList(confirmRequest.getTradeItemRequests(), TradeItemDTO.class);

        // 1.获取商品信息
        List<String> skuIds = tradeItems.stream().map(TradeItemDTO::getSkuId).collect(Collectors.toList());
        GoodsInfoResponse response = getGoodsResponse(skuIds, customer);
        GoodsInfoVO goodsInfo = response.getGoodsInfos().get(0);
        // 2.填充商品区间价
        tradeItems = KsBeanUtil.convertList(
                verifyQueryProvider.verifyGoods(
                        new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                                KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                goodsInfo.getStoreId(), true, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);


        List<TradeMarketingDTO> tradeMarketingList = new ArrayList<>();
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        DefaultFlag storeOpenFlag = distributionCacheService.queryStoreOpenFlag(String.valueOf(goodsInfo.getStoreId()));

        if (DefaultFlag.YES.equals(confirmRequest.getStoreBagsFlag())) {
            // 3.礼包商品，重置商品价格
            tradeItems.get(0).setPrice(goodsInfo.getMarketPrice());
        } else {
            if (goodsInfo.getDistributionGoodsAudit() != DistributionGoodsAudit.CHECKED
                    || DefaultFlag.NO.equals(openFlag)
                    || DefaultFlag.NO.equals(storeOpenFlag)) {
                // 4.1.非礼包、非分销商品，设置默认营销
                goodsInfo.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                PurchaseGetGoodsMarketingRequest purchaseGetGoodsMarketingRequest = new
                        PurchaseGetGoodsMarketingRequest();
                purchaseGetGoodsMarketingRequest.setGoodsInfos(response.getGoodsInfos());
                purchaseGetGoodsMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
                purchaseGetGoodsMarketingRequest.setWareId(confirmRequest.getWareId());
                PurchaseGetGoodsMarketingResponse marketingResponse = purchaseQueryProvider.getGoodsMarketing
                        (purchaseGetGoodsMarketingRequest).getContext();
                List<MarketingViewVO> marketings = marketingResponse.getMap().get(goodsInfo.getGoodsInfoId());
                if (tradeItems.get(0).getGoodsInfoType() == 1) {
                    tradeItems.get(0).setPrice(goodsInfo.getSpecialPrice());
                }
                TradeMarketingDTO tradeMarketing = chooseDefaultMarketing(tradeItems.get(0), marketings, confirmRequest.getMatchWareHouseFlag());
                if (tradeMarketing != null) {
                    tradeMarketingList.add(tradeMarketing);
                }
            } else {
                // 4.2.非礼包、分销商品，重置商品价格
                tradeItems.get(0).setPrice(goodsInfo.getMarketPrice());
            }
        }
        if (tradeItems.get(0).getGoodsInfoType() == 1) {
            tradeItems.get(0).setPrice(goodsInfo.getSpecialPrice());
        }
        return pileTradeItemProvider.snapshot(
                TradeItemSnapshotRequest.builder()
                        .customerId(customer.getCustomerId())
                        .tradeItems(tradeItems)
                        .tradeMarketingList(tradeMarketingList)
                        .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }

    /**
     * 选择商品默认的营销，以及它的level
     */
    private TradeMarketingDTO chooseDefaultMarketing(TradeItemDTO tradeItem, List<MarketingViewVO> marketings, boolean matchWareHouseFlag) {

        BigDecimal total = tradeItem.getPrice().multiply(new BigDecimal(tradeItem.getNum()));
        Long num = tradeItem.getNum();

        TradeMarketingDTO tradeMarketing = new TradeMarketingDTO();
        tradeMarketing.setSkuIds(Arrays.asList(tradeItem.getSkuId()));

        if (CollectionUtils.isNotEmpty(marketings)) {
            for (int i = 0; i < marketings.size(); i++) {
                MarketingViewVO marketing = marketings.get(i);

                // 满金额减
                if (marketing.getSubType() == MarketingSubType.REDUCTION_FULL_AMOUNT) {
                    List<MarketingFullReductionLevelVO> levels = marketing.getFullReductionLevelList();
                    if (CollectionUtils.isNotEmpty(levels)) {
                        levels.sort(Comparator.comparing(MarketingFullReductionLevelVO::getFullAmount).reversed());
                        for (int j = 0; j < levels.size(); j++) {
                            if (levels.get(j).getFullAmount().compareTo(total) != 1) {
                                tradeMarketing.setMarketingLevelId(levels.get(j).getReductionLevelId());
                                tradeMarketing.setMarketingId(levels.get(j).getMarketingId());
                                return tradeMarketing;
                            }
                        }
                    }
                }

                // 满数量减
                if (marketing.getSubType() == MarketingSubType.REDUCTION_FULL_COUNT) {
                    List<MarketingFullReductionLevelVO> levels = marketing.getFullReductionLevelList();
                    if (CollectionUtils.isNotEmpty(levels)) {
                        levels.sort(Comparator.comparing(MarketingFullReductionLevelVO::getFullCount).reversed());
                        for (int j = 0; j < levels.size(); j++) {
                            if (levels.get(j).getFullCount().compareTo(num) != 1) {
                                tradeMarketing.setMarketingLevelId(levels.get(j).getReductionLevelId());
                                tradeMarketing.setMarketingId(levels.get(j).getMarketingId());
                                return tradeMarketing;
                            }
                        }
                    }
                }

                // 满金额折
                if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_AMOUNT) {
                    List<MarketingFullDiscountLevelVO> levels = marketing.getFullDiscountLevelList();
                    if (CollectionUtils.isNotEmpty(levels)) {
                        levels.sort(Comparator.comparing(MarketingFullDiscountLevelVO::getFullAmount).reversed());
                        for (int j = 0; j < levels.size(); j++) {
                            if (levels.get(j).getFullAmount().compareTo(total) != 1) {
                                tradeMarketing.setMarketingLevelId(levels.get(j).getDiscountLevelId());
                                tradeMarketing.setMarketingId(levels.get(j).getMarketingId());
                                return tradeMarketing;
                            }
                        }
                    }
                }
                // 满数量折
                if (marketing.getSubType() == MarketingSubType.DISCOUNT_FULL_COUNT) {
                    List<MarketingFullDiscountLevelVO> levels = marketing.getFullDiscountLevelList();
                    if (CollectionUtils.isNotEmpty(levels)) {
                        levels.sort(Comparator.comparing(MarketingFullDiscountLevelVO::getFullCount).reversed());
                        for (int j = 0; j < levels.size(); j++) {
                            if (levels.get(j).getFullCount().compareTo(num) != 1) {
                                tradeMarketing.setMarketingLevelId(levels.get(j).getDiscountLevelId());
                                tradeMarketing.setMarketingId(levels.get(j).getMarketingId());
                                return tradeMarketing;
                            }
                        }
                    }
                }


                // 满金额赠
                if (marketing.getSubType() == MarketingSubType.GIFT_FULL_AMOUNT) {
                    List<MarketingFullGiftLevelVO> levels = marketing.getFullGiftLevelList();
                    if (CollectionUtils.isNotEmpty(levels)) {
                        levels.sort(Comparator.comparing(MarketingFullGiftLevelVO::getFullAmount).reversed());
                        for (MarketingFullGiftLevelVO level : levels) {
                            if (level.getFullAmount().compareTo(total) != 1) {
                                tradeMarketing.setMarketingLevelId(level.getGiftLevelId());
                                tradeMarketing.setMarketingId(level.getMarketingId());
                                List<String> giftIds =
                                        level.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId).collect(Collectors.toList());
                                List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listGoodsInfoAndStcokByIds(GoodsInfoAndStockListByIdsRequest
                                        .builder().goodsInfoIds(giftIds).matchWareHouseFlag(matchWareHouseFlag).build()).getContext().getGoodsInfos();
                                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                                    Iterator<String> iterator = giftIds.iterator();
                                    while (iterator.hasNext()) {
                                        String giftSku = iterator.next();
                                        Optional<GoodsInfoVO> goodsInfoVO = goodsInfos.stream().filter(param -> param.getGoodsInfoId().equals(giftSku)).findFirst();
                                        if (goodsInfoVO.isPresent()) {
                                            if (goodsInfoVO.get().getStock().compareTo(BigDecimal.ZERO) <= 0 || !CheckStatus.CHECKED
                                                    .equals(goodsInfoVO.get().getAuditStatus()) || goodsInfoVO.get().getAddedFlag() != 1) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                }
                                if (CollectionUtils.isNotEmpty(giftIds)) {
                                    if (GiftType.ONE.equals(level.getGiftType())) {
                                        giftIds = Collections.singletonList(giftIds.get(0));
                                    }
                                    tradeMarketing.setGiftSkuIds(giftIds);
                                } else {
                                    tradeMarketing.setGiftSkuIds(new ArrayList<>());
                                }
                                return tradeMarketing;
                            }
                        }
                    }
                }
                // 满数量赠
                if (marketing.getSubType() == MarketingSubType.GIFT_FULL_COUNT) {
                    List<MarketingFullGiftLevelVO> levels = marketing.getFullGiftLevelList();
                    if (CollectionUtils.isNotEmpty(levels)) {
                        levels.sort(Comparator.comparing(MarketingFullGiftLevelVO::getFullCount).reversed());
                        for (MarketingFullGiftLevelVO level : levels) {
                            if (level.getFullCount().compareTo(num) != 1) {
                                tradeMarketing.setMarketingLevelId(level.getGiftLevelId());
                                tradeMarketing.setMarketingId(level.getMarketingId());
                                List<String> giftIds =
                                        level.getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId).collect(Collectors.toList());
                                List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listGoodsInfoAndStcokByIds(GoodsInfoAndStockListByIdsRequest
                                        .builder().goodsInfoIds(giftIds).matchWareHouseFlag(matchWareHouseFlag).build()).getContext().getGoodsInfos();
                                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                                    Iterator<String> iterator = giftIds.iterator();
                                    while (iterator.hasNext()) {
                                        String giftSku = iterator.next();
                                        Optional<GoodsInfoVO> goodsInfoVO = goodsInfos.stream().filter(param -> param.getGoodsInfoId().equals(giftSku)).findFirst();
                                        if (goodsInfoVO.isPresent()) {
                                            if (goodsInfoVO.get().getStock().compareTo(BigDecimal.ZERO) <= 0 || !CheckStatus.CHECKED
                                                    .equals(goodsInfoVO.get().getAuditStatus()) || goodsInfoVO.get().getAddedFlag() != 1) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                }
                                if (CollectionUtils.isNotEmpty(giftIds)) {
                                    if (GiftType.ONE.equals(level.getGiftType())) {
                                        giftIds = Collections.singletonList(giftIds.get(0));
                                    }
                                    tradeMarketing.setGiftSkuIds(giftIds);
                                } else {
                                    tradeMarketing.setGiftSkuIds(new ArrayList<>());
                                }
                                return tradeMarketing;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 提交订单，用于生成订单操作
     */
    @ApiOperation(value = "提交订单，用于生成订单操作")
    @RequestMapping(value = "/commit", method = RequestMethod.POST)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<List<TradeCommitResultVO>> commit(@RequestBody @Valid PileTradeCommitRequest pileTradeCommitRequest) {
        RLock rLock = redissonClient.getFairLock(commonUtil.getOperatorId());
        rLock.lock();
        List<TradeCommitResultVO> successResults;
        try {
            Operator operator = commonUtil.getOperator();

            pileTradeCommitRequest.setOperator(operator);
            List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest
                    .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();
            DefaultFlag storeBagsFlag = tradeItemGroups.get(0).getStoreBagsFlag();
            if (DefaultFlag.NO.equals(storeBagsFlag) && !distributionService.checkInviteeIdEnable()) {
                // 非开店礼包情况下，判断小店状态不可用
                throw new SbcRuntimeException("K-080301");
            }

            // 邀请人不是分销员时，清空inviteeId
            DistributeChannel distributeChannel = commonUtil.getDistributeChannel();
            if (StringUtils.isNotEmpty(distributeChannel.getInviteeId()) &&
                    !distributionService.isDistributor(distributeChannel.getInviteeId())) {
                distributeChannel.setInviteeId(null);
            }
            // 设置下单用户，是否分销员
            if (distributionService.isDistributor(operator.getUserId())) {
                pileTradeCommitRequest.setIsDistributor(DefaultFlag.YES);
            }
            pileTradeCommitRequest.setDistributeChannel(distributeChannel);
            pileTradeCommitRequest.setShopName(distributionCacheService.getShopName());

            // 设置分销设置开关
            pileTradeCommitRequest.setOpenFlag(distributionCacheService.queryOpenFlag());
            pileTradeCommitRequest.getStoreCommitInfoList().forEach(item ->
                    item.setStoreOpenFlag(distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString()))
            );
            // 验证小店商品
            validShopGoods(tradeItemGroups, pileTradeCommitRequest.getDistributeChannel());

            successResults = pileTradeProvider.commit(pileTradeCommitRequest).getContext().getTradeCommitResults();
            //如果是秒杀商品订单更新会员已抢购该商品数量
            if (Objects.nonNull(pileTradeCommitRequest.getIsFlashSaleGoods()) && pileTradeCommitRequest.getIsFlashSaleGoods()) {
                TradeVO trade =
                        pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(successResults.get(0).getTid()).build()).getContext().getTradeVO();
                String havePanicBuyingKey =
                        RedisKeyConstant.FLASH_SALE_GOODS_HAVE_PANIC_BUYING + operator.getUserId() + trade.getTradeItems().get(0).getFlashSaleGoodsId();
                redisService.setString(havePanicBuyingKey,
                        Integer.valueOf(StringUtils.isNotBlank(redisService.getString(havePanicBuyingKey)) ?
                                redisService.getString(havePanicBuyingKey) : "0") + trade.getTradeItems().get(0).getNum() + "");
                //删除抢购资格
                redisService.delete(RedisKeyConstant.FLASH_SALE_GOODS_QUALIFICATIONS + commonUtil.getOperatorId() + trade.getTradeItems().get(0).getSpuId());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
        return BaseResponse.success(successResults);
    }



    @ApiOperation(value = "展示订单基本信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/show/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeCommitResultVO> commitResp(@PathVariable String tid) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        checkUnauthorized(tid, trade);
        boolean hasImg = CollectionUtils.isNotEmpty(trade.getTradeItems());
        return BaseResponse.success(new TradeCommitResultVO(tid, trade.getParentId(), trade.getTradeState(),
                trade.getPaymentOrder(),
                trade.getTradePrice().getTotalPrice(),
                trade.getOrderTimeOut(),
                trade.getSupplier().getStoreName(),
                trade.getSupplier().getIsSelf(),
                hasImg ? trade.getTradeItems().get(0).getPic() : null));
    }

    @ApiOperation(value = "验证配送到家标志位")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkDeliveryHomeFlag", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeFlagResponse> checkDeliveryHomeFlag(@RequestBody @Valid DeliveryHomeFlagRequest request) {
        DeliveryHomeFlagResponse deliveryHomeFlagResponse = new DeliveryHomeFlagResponse();
        deliveryHomeFlagResponse.setFlag(checkDistance(request.getWareId(), request.getCustomerDeliveryAddressId()));
        return BaseResponse.success(deliveryHomeFlagResponse);
    }

    @ApiOperation(value = "验证配送到家范围")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkHomeFlag", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeFlagResponse> checkHomeFlag(@RequestBody @Valid DeliveryHomeFlagRequest request) {
        DeliveryHomeFlagResponse deliveryHomeFlagResponse = new DeliveryHomeFlagResponse();
        deliveryHomeFlagResponse.setFlag(checkDeliveryArea(request.getWareId(), request.getCustomerDeliveryAddressId(),commonUtil.getOperator().getUserId()));
        return BaseResponse.success(deliveryHomeFlagResponse);
    }


    /**
     * 用于确认订单后，创建订单前的获取订单商品信息
     */
    @ApiOperation(value = "用于确认订单后，创建订单前的获取订单商品信息")
    @RequestMapping(value = "/purchase", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<TradeConfirmResponse> getPurchaseItems(@RequestParam Long wareId, Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag) {

        TradeConfirmResponse confirmResponse = new TradeConfirmResponse();
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        /**
         * 设置itemGroupVo的分仓信息
         */
        tradeItemGroups.stream().forEach(g -> g.setWareId(wareId));

        List<TradeConfirmItemVO> items = new ArrayList<>();
        List<String> skuIds = tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getSkuId).collect(Collectors.toList());
        GoodsInfoResponse skuResp = getGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
        List<String> giftIds =
                tradeItemGroups.stream().flatMap(i -> i.getTradeMarketingList().stream().filter(param -> CollectionUtils.isNotEmpty(param.getGiftSkuIds()))).flatMap(r -> r.getGiftSkuIds()
                        .stream()).distinct().collect(Collectors.toList());
        Map<Long, StoreVO> storeMap = storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                (tradeItemGroups.stream().map(g -> g
                                .getSupplier().getStoreId())
                        .collect(Collectors.toList()))).getContext().getStoreVOList().stream().collect(Collectors
                .toMap(StoreVO::getStoreId,
                        s -> s));
        TradeGetGoodsResponse giftResp;
        giftResp = pileTradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
        final TradeGetGoodsResponse giftTemp = giftResp;

        // 如果为PC商城下单，将分销商品变为普通商品
        if (ChannelType.PC_MALL.equals(commonUtil.getDistributeChannel().getChannelType())) {
            tradeItemGroups.forEach(tradeItemGroup ->
                    tradeItemGroup.getTradeItems().forEach(tradeItem -> {
                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    })
            );
            skuResp.getGoodsInfos().forEach(item -> item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS));
        }
        //企业会员判断
        boolean isIepCustomerFlag = isIepCustomer();
        tradeItemGroups.forEach(
                g -> {
                    //参与营销活动商品id集合
                    List<String> tradeMarketingSkuIds = new ArrayList<>();
                    g.getTradeMarketingList().forEach(tradeItemMarketingVO -> {
                        tradeMarketingSkuIds.addAll(tradeItemMarketingVO.getSkuIds());
                    });
                    g.getSupplier().setFreightTemplateType(storeMap.get(g.getSupplier().getStoreId())
                            .getFreightTemplateType());
                    List<TradeItemVO> tradeItems = g.getTradeItems();
                    List<TradeItemDTO> tradeItemDTOList = KsBeanUtil.convert(tradeItems, TradeItemDTO.class);
                    // 分销商品、开店礼包商品、拼团商品、企业购商品，不验证起限定量
                    skuResp.getGoodsInfos().forEach(item -> {
                        //企业购商品
                        if (DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())
                                || DefaultFlag.YES.equals(g.getStoreBagsFlag())
                                || Objects.nonNull(g.getGrouponForm()) || isIepCustomerFlag) {
                            item.setCount(null);
                            item.setMaxCount(null);
                        }
                    });
                    //商品验证并填充
                    List<TradeItemVO> tradeItemVOList =
                            verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItemDTOList, Collections
                                    .emptyList(),
                                    KsBeanUtil.convert(skuResp, TradeGoodsInfoPageDTO.class),
                                    g.getSupplier().getStoreId(), true, checkStockFlag,customerId)).getContext().getTradeItems();
                    //大客户价商品回设
                    if (isIepCustomerFlag || commonUtil.isVipCustomerByVipFlag()) {
                        tradeItemVOList.forEach(tradeItemVO -> {
                            //vip价格不参与营销活动
                            if (!tradeMarketingSkuIds.contains(tradeItemVO.getSkuId())) {
                                BigDecimal vipPrice = Objects.nonNull(tradeItemVO.getVipPrice())
                                        && tradeItemVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ?
                                        tradeItemVO.getVipPrice() : tradeItemVO.getOriginalPrice();
                                tradeItemVO.setPrice(vipPrice);
                                tradeItemVO.setSplitPrice(vipPrice.multiply(new BigDecimal(tradeItemVO.getNum())));
                                tradeItemVO.setLevelPrice(vipPrice);
                            }
                        });
                    }
                    //特价商品的价格回设
                    tradeItemVOList.forEach(tradeItemVO -> {
                        if (Objects.nonNull(tradeItemVO.getGoodsInfoType())
                                && tradeItemVO.getGoodsInfoType() == 1
                                && tradeItemVO.getSpecialPrice() != null) {
                            tradeItemVO.setPrice(tradeItemVO.getSpecialPrice());
                            tradeItemVO.setSplitPrice(tradeItemVO.getSpecialPrice().multiply(new BigDecimal(tradeItemVO.getNum())));
                            tradeItemVO.setLevelPrice(tradeItemVO.getSpecialPrice());
                        }
                    });

                    //抢购商品价格回设
                    if (StringUtils.isNotBlank(g.getSnapshotType()) && g.getSnapshotType().equals(Constants.FLASH_SALE_GOODS_ORDER_TYPE)) {
                        tradeItemVOList.forEach(tradeItemVO -> {
                            g.getTradeItems().forEach(tradeItem -> {
                                if (tradeItem.getSkuId().equals(tradeItemVO.getSkuId())) {
                                    tradeItemVO.setPrice(tradeItem.getPrice());
                                }
                            });
                        });
                    }
                    g.setTradeItems(tradeItemVOList);
                    // 分销商品、开店礼包商品，重新设回市场价
                    if (DefaultFlag.YES.equals(distributionCacheService.queryOpenFlag())
                            && !ChannelType.PC_MALL.equals(commonUtil.getDistributeChannel().getChannelType())) {
                        g.getTradeItems().forEach(item -> {
                            DefaultFlag storeOpenFlag = distributionCacheService.queryStoreOpenFlag(item.getStoreId()
                                    .toString());
                            if ((Objects.isNull(item.getIsFlashSaleGoods()) ||
                                    (Objects.nonNull(item.getIsFlashSaleGoods()) && !item.getIsFlashSaleGoods())) &&
                                    DefaultFlag.YES.equals(storeOpenFlag) && (
                                    DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())
                                            || DefaultFlag.YES.equals(g.getStoreBagsFlag()))) {
                                item.setSplitPrice(item.getOriginalPrice().multiply(new BigDecimal(item.getNum())));
                                item.setPrice(item.getOriginalPrice());
                                item.setLevelPrice(item.getOriginalPrice());
                            } else {
                                item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                            }
                        });
                    }

                    //赠品信息填充
                    List<String> giftItemIds =
                            g.getTradeMarketingList().parallelStream().filter(i -> (i.getGiftSkuIds() != null) && !i.getGiftSkuIds().isEmpty())
                                    .flatMap(r -> r.getGiftSkuIds().stream()).distinct().collect(Collectors.toList());
                    List<TradeItemDTO> gifts =
                            giftItemIds.stream().map(i -> TradeItemDTO.builder().price(BigDecimal.ZERO)
                                    .skuId(i)
                                    .build()).collect(Collectors.toList());
                    List<TradeItemVO> giftVoList = verifyQueryProvider.mergeGoodsInfo(new MergeGoodsInfoRequest(gifts
                                    , KsBeanUtil.convert(giftTemp, TradeGoodsListDTO.class)))
                            .getContext().getTradeItems();
                    gifts = KsBeanUtil.convert(giftVoList, TradeItemDTO.class);
                    confirmResponse.setStoreBagsFlag(g.getStoreBagsFlag());
                    items.add(pileTradeQueryProvider.queryPurchaseInfo(TradeQueryPurchaseInfoRequest.builder()
                            .tradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class))
                            .tradeItemList(gifts).build()).getContext().getTradeConfirmItemVO());
                }
        );

        //商品sku+订单列表=商品数量
        items.stream().forEach(tradeConfirmItemVO -> {
            //所有商品
            Long goodsNum = tradeConfirmItemVO.getTradeItems().stream().mapToLong((s) -> s.getNum()).sum();
            //所有赠品
            if (CollectionUtils.isNotEmpty(tradeConfirmItemVO.getGifts())) {
                goodsNum += tradeConfirmItemVO.getGifts().stream().mapToLong((s) -> s.getNum()).sum();
            }
            tradeConfirmItemVO.setGoodsNum(goodsNum);
        });
        confirmResponse.setTradeConfirmItems(items);

        // 根据确认订单计算出的信息，查询出使用优惠券页需要的优惠券列表数据
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        List<TradeItemInfoDTO> tradeDtos = items.stream().flatMap(confirmItem ->
                confirmItem.getTradeItems().stream().map(tradeItem -> {
                    TradeItemInfoDTO dto = new TradeItemInfoDTO();
                    dto.setBrandId(tradeItem.getBrand());
                    dto.setCateId(tradeItem.getCateId());
                    dto.setSpuId(tradeItem.getSpuId());
                    dto.setSkuId(tradeItem.getSkuId());
                    dto.setStoreId(confirmItem.getSupplier().getStoreId());
                    dto.setPrice(tradeItem.getSplitPrice());
                    dto.setGoodsInfoType(tradeItem.getGoodsInfoType());
                    dto.setDistributionGoodsAudit(tradeItem.getDistributionGoodsAudit());
                    if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(
                            distributionCacheService.queryStoreOpenFlag(String.valueOf(tradeItem.getStoreId())))) {
                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    }
                    return dto;
                })
        ).collect(Collectors.toList());

        CustomerVO customerVO = commonUtil.getCustomer();
        CouponCodeListForUseByCustomerIdRequest requ = CouponCodeListForUseByCustomerIdRequest.builder()
                .customerId(Objects.nonNull(customerVO) && StringUtils.isNotBlank(customerVO.getParentCustomerId())
                        ? customerVO.getParentCustomerId() : customerId)
                .tradeItems(tradeDtos).build();
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            requ.setStoreId(domainStoreRelaVO.getStoreId());
        }
        confirmResponse.setCouponCodes(couponCodeQueryProvider.listForUseByCustomerId(requ).getContext()
                .getCouponCodeList());
        return BaseResponse.success(confirmResponse);
    }


    private void validGrouponOrder(
            TradeConfirmResponse response, List<TradeItemGroupVO> tradeItemGroups, String customerId) {
        TradeItemGroupVO tradeItemGroup = tradeItemGroups.get(NumberUtils.INTEGER_ZERO);
        TradeItemVO item = tradeItemGroup.getTradeItems().get(NumberUtils.INTEGER_ZERO);
        TradeConfirmItemVO confirmItem = response.getTradeConfirmItems().get(NumberUtils.INTEGER_ZERO);
        TradeItemVO resItem = confirmItem.getTradeItems().get(NumberUtils.INTEGER_ZERO);
        if (Objects.nonNull(tradeItemGroup.getGrouponForm())) {

            TradeGrouponCommitFormVO grouponForm = tradeItemGroup.getGrouponForm();

            if (!DistributionGoodsAudit.COMMON_GOODS.equals(item.getDistributionGoodsAudit())) {
                log.error("拼团单，不能下分销商品");
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

            // 1.校验拼团商品
            GrouponGoodsInfoVO grouponGoodsInfo = grouponProvider.validGrouponOrderBeforeCommit(
                    GrouponOrderValidRequest.builder()
                            .buyCount(item.getNum().intValue()).customerId(customerId).goodsId(item.getSpuId())
                            .goodsInfoId(item.getSkuId())
                            .grouponNo(grouponForm.getGrouponNo())
                            .openGroupon(grouponForm.getOpenGroupon())
                            .build()).getContext().getGrouponGoodsInfo();

            if (Objects.isNull(grouponGoodsInfo)) {
                log.error("拼团单下的不是拼团商品");
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

            // 2.设置拼团活动信息
            boolean freeDelivery = grouponActivityQueryProvider.getFreeDeliveryById(
                    new GrouponActivityFreeDeliveryByIdRequest(grouponGoodsInfo.getGrouponActivityId())).getContext().isFreeDelivery();
            response.setOpenGroupon(grouponForm.getOpenGroupon());
            response.setGrouponFreeDelivery(freeDelivery);

            // 3.设成拼团价
            BigDecimal grouponPrice = grouponGoodsInfo.getGrouponPrice();
            BigDecimal splitPrice = grouponPrice.multiply(new BigDecimal(item.getNum()));
            resItem.setSplitPrice(splitPrice);
            resItem.setPrice(grouponPrice);
            resItem.setLevelPrice(grouponPrice);
            confirmItem.getTradePrice().setGoodsPrice(splitPrice);
            confirmItem.getTradePrice().setTotalPrice(splitPrice);
        }
    }

    /**
     * 根据参数查询某订单的运费
     *
     * @param tradeParams
     * @return
     */
    @ApiOperation(value = "根据参数查询某订单的运费")
    @RequestMapping(value = "/getFreight", method = RequestMethod.POST)
    public BaseResponse<List<TradeGetFreightResponse>> getFreight(@RequestBody List<TradeParamsRequest> tradeParams) {
        return BaseResponse.success(tradeParams.stream().map(params -> pileTradeQueryProvider.getFreight(params)
                .getContext()).collect(Collectors.toList()));
    }

    /**
     * 根据参数查询某订单的运费
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "根据参数查询某订单的运费(喜丫丫)APP")
    @RequestMapping(value = "/getFinalFreightForApp", method = RequestMethod.POST)
    public BaseResponse<List<TradeGetFreightResponse>> getFinalFreightFoApp(@RequestBody TradeParamsRequestForApp request) {
        return this.getFinalFreight(request.getTradeParams());
    }


    /**
     * 根据参数查询某订单的运费
     *
     * @param tradeParams
     * @return
     */
    @ApiOperation(value = "根据参数查询某订单的运费(喜丫丫)")
    @RequestMapping(value = "/getFinalFreight", method = RequestMethod.POST)
    public BaseResponse<List<TradeGetFreightResponse>> getFinalFreight(@RequestBody List<TradeParamsRequest> tradeParams) {
        List<Long> storeIds = tradeParams.stream().map(TradeParamsRequest::getSupplier).map(SupplierDTO::getStoreId).distinct().collect(Collectors.toList());
        Map<Long, StoreVO> storeMap = storeQueryProvider.listNoDeleteStoreByIds(ListNoDeleteStoreByIdsRequest.builder()
                .storeIds(storeIds).build()).getContext().getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId, g -> g));
        List<TradeGetFreightResponse> result = new ArrayList<>(tradeParams.size());
        //组装店铺类型
        for (TradeParamsRequest inner : tradeParams) {
            StoreVO storeVO = storeMap.get(inner.getSupplier().getStoreId());
            if (Objects.nonNull(storeVO)) {
                inner.getSupplier().setCompanyType(storeVO.getCompanyType());
            }
        }

        //排除所有非快递方式的订单，直接塞0
        Iterator<TradeParamsRequest> iterator = tradeParams.iterator();
        while (iterator.hasNext()) {
            TradeParamsRequest next = iterator.next();
            if (!next.getDeliverWay().equals(DeliverWay.EXPRESS)) {
                TradeGetFreightResponse inner = new TradeGetFreightResponse();
                inner.setStoreId(next.getSupplier().getStoreId());
                inner.setDeliveryPrice(BigDecimal.ZERO);
                result.add(inner);
                iterator.remove();
            } else {

                // 根据商品skuid验证商品是否是秒杀商品
                if (Objects.nonNull(next.getIsSeckill()) && next.getIsSeckill()) {
                    List<TradeItemDTO> oldTradeItems = next.getOldTradeItems();
                    if (Objects.nonNull(oldTradeItems)) {
                        List<String> skuIds = oldTradeItems.stream().map(TradeItemDTO::getSkuId).distinct().collect(Collectors.toList());
                        IsSecKillRequest isFlashSaleRequest = new IsSecKillRequest();
                        isFlashSaleRequest.setSkuIds(skuIds);
                        List<FlashSaleGoodsVO> flashSaleGoodsList = flashSaleGoodsQueryProvider.isSecKill(isFlashSaleRequest).getContext();
                        if (CollectionUtils.isNotEmpty(flashSaleGoodsList)) {
                            FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsList.get(0);
                            if (flashSaleGoodsVO.getPostage().equals(1)) {
                                TradeGetFreightResponse inner = new TradeGetFreightResponse();
                                inner.setStoreId(next.getSupplier().getStoreId());
                                inner.setDeliveryPrice(BigDecimal.ZERO);
                                result.add(inner);
                                iterator.remove();
                            }
                        }
                    }
                }

            }
        }

        //平台
        boolean platformFlag = false;
        //统仓统配
        boolean unifiedFlag = false;
        //三方卖家
        boolean supplierFlag = false;
        for (TradeParamsRequest inner : tradeParams) {
            if (inner.getSupplier().getCompanyType().equals(CompanyType.PLATFORM)) {
                platformFlag = true;
            } else if (inner.getSupplier().getCompanyType().equals(CompanyType.UNIFIED)) {
                unifiedFlag = true;
            } else {
                supplierFlag = true;
            }
        }
        //存在三种店铺时，走以下计算方式否则还是走店铺运费计算
        if (platformFlag && unifiedFlag && supplierFlag) {
            //分类汇总=》三方卖家走店铺模板，其他走平台模板
            List<TradeParamsRequest> bossFreight = new ArrayList<>(10);
            List<TradeParamsRequest> storeFreight = new ArrayList<>(10);
            for (TradeParamsRequest inner : tradeParams) {
                if (inner.getSupplier().getCompanyType().equals(CompanyType.PLATFORM)
                        || inner.getSupplier().getCompanyType().equals(CompanyType.UNIFIED)) {
                    bossFreight.add(inner);
                } else {
                    storeFreight.add(inner);
                }
            }
            if (CollectionUtils.isNotEmpty(bossFreight)) {
                result.addAll(pileTradeQueryProvider.getBossFreight(bossFreight).getContext());
            }
            if (CollectionUtils.isNotEmpty(storeFreight)) {
                result.addAll(storeFreight.stream().map(params -> pileTradeQueryProvider.getFreight(params)
                        .getContext()).collect(Collectors.toList()));
            }
        } else {
            result.addAll(tradeParams.stream().map(params -> pileTradeQueryProvider.getFreight(params)
                    .getContext()).collect(Collectors.toList()));
        }
        return BaseResponse.success(result);
    }

    /**
     * 根据参数查询某订单的运费
     *
     * @param tradeParams
     * @return
     */
    @ApiOperation(value = "根据参数查询平台的运费")
    @RequestMapping(value = "/getBossFreight", method = RequestMethod.POST)
    public BaseResponse<List<TradeGetFreightResponse>> getBossFreight(@RequestBody List<TradeParamsRequest> tradeParams) {
        return BaseResponse.success(pileTradeQueryProvider.getBossFreight(tradeParams).getContext());
    }

    /**
     * 我的拼购分页查询订单
     */
    @ApiOperation(value = "我的拼购分页查询订单")
    @RequestMapping(value = "/page/groupons", method = RequestMethod.POST)
    public BaseResponse<Page<GrouponTradeVO>> grouponPage(@RequestBody TradePageQueryRequest paramRequest) {
        TradeQueryDTO tradeQueryRequest = TradeQueryDTO.builder()
                .buyerId(commonUtil.getOperatorId())
                .grouponFlag(Boolean.TRUE)
                .isBoss(false)
                .build();
        tradeQueryRequest.setPageNum(paramRequest.getPageNum());
        tradeQueryRequest.setPageSize(paramRequest.getPageSize());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            tradeQueryRequest.setStoreId(domainInfo.getStoreId());
        }
        Page<TradeVO> tradePage = pileTradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        List<GrouponTradeVO> tradeReponses = new ArrayList<>();
        tradePage.getContent().forEach(info -> {
            GrouponTradeVO tradeReponse = KsBeanUtil.convert(info, GrouponTradeVO.class);
            //待成团-获取团实例
            if (GrouponOrderStatus.WAIT.equals(tradeReponse.getTradeGroupon().getGrouponOrderStatus())
                    && PayState.PAID.equals(tradeReponse.getTradeState().getPayState())
            ) {
                GrouponInstanceByGrouponNoRequest request = GrouponInstanceByGrouponNoRequest.builder().grouponNo(info
                        .getTradeGroupon().getGrouponNo()).build();
                tradeReponse.setGrouponInstance(grouponInstanceQueryProvider.detailByGrouponNo(request).getContext
                        ().getGrouponInstance());
            }
            tradeReponses.add(tradeReponse);
        });

        return BaseResponse.success(new PageImpl<>(tradeReponses, tradeQueryRequest.getPageable(),
                tradePage.getTotalElements()));
    }

    /**
     * 分页查询订单
     */
    @ApiOperation(value = "分页查询订单")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<Page<TradeVO>> page(@RequestBody TradePageQueryRequest paramRequest) {
        List<String> customerIdList = new ArrayList<>(20);
        customerIdList.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerIdList.addAll(child);
        }

        TradeQueryDTO tradeQueryRequest = TradeQueryDTO.builder()
                .tradeState(TradeStateDTO.builder()
                        .flowState(paramRequest.getFlowState())
                        .payState(paramRequest.getPayState())
                        .deliverStatus(paramRequest.getDeliverStatus())
                        .build())
                //.buyerId(commonUtil.getOperatorId())
                .customerIds(customerIdList.toArray())
                .inviteeId(paramRequest.getInviteeId())
                .channelType(paramRequest.getChannelType())
                .beginTime(paramRequest.getCreatedFrom())
                .endTime(paramRequest.getCreatedTo())
                .keyworks(paramRequest.getKeywords())
                .isBoss(false)
                .build();
        tradeQueryRequest.setPageNum(paramRequest.getPageNum());
        tradeQueryRequest.setPageSize(paramRequest.getPageSize());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            tradeQueryRequest.setStoreId(domainInfo.getStoreId());
        }
        //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
        tradeQueryRequest.makeAllAuditFlowStatus();

        Page<TradeVO> tradePage = pileTradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        List<TradeVO> tradeReponses = new ArrayList<>();
        tradePage.getContent().forEach(info -> {
            TradeVO tradeReponse = new TradeVO();
            BeanUtils.copyProperties(info, tradeReponse);
            TradeStateVO tradeState = tradeReponse.getTradeState();
            boolean canReturnFlag =
                    tradeState.getFlowState() == FlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                            && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState
                            .getFlowState() != FlowState.VOID);
            canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
            //开店礼包不支持退货退款
            canReturnFlag = canReturnFlag && DefaultFlag.NO == tradeReponse.getStoreBagsFlag();
            //重新计算可退数量（排除特价商品）
            if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(info.getTradeState().getDeliverStatus())) {
                canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                        .trade(KsBeanUtil.convert(info, TradeDTO.class)).build()).getContext().getCanReturnFlag();
            }
            tradeReponse.setCanReturnFlag(canReturnFlag);
            FindProviderTradeResponse findProviderTradeResponse = providerTradeProvider.findByParentIdList(FindProviderTradeRequest.builder().parentId(Arrays.asList(info.getId())).build()).getContext();
            tradeReponse.setTradeVOList(findProviderTradeResponse.getTradeVOList());
            tradeReponses.add(tradeReponse);
        });
        //增加商品数量值
        for (TradeVO tradeVO : tradeReponses) {
            Long sum = tradeVO.getTradeItems().stream().mapToLong(TradeItemVO::getNum).sum();
            sum = sum == null ? 0L : sum;
            tradeVO.setGoodsTotalNum(sum);
        }

        return BaseResponse.success(new PageImpl<>(tradeReponses, tradeQueryRequest.getPageable(),
                                                       tradePage.getTotalElements()));
    }

    /**
     * 分页查询订单（新）
     */
    @ApiOperation(value = "(新)分页查询订单")
    @RequestMapping(value = "/page2", method = RequestMethod.POST)
    public BaseResponse<Page<TradeNewVO>> pageNew(@RequestBody TradePageQueryRequest paramRequest) {
        List<String> customerIdList = new ArrayList<>(20);
        customerIdList.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerIdList.addAll(child);
        }

        TradeQueryDTO tradeQueryRequest = TradeQueryDTO.builder()
                .tradeState(TradeStateDTO.builder()
                        .flowState(paramRequest.getFlowState())
                        .payState(paramRequest.getPayState())
                        .deliverStatus(paramRequest.getDeliverStatus())
                        .build())
                //.buyerId(commonUtil.getOperatorId())
                .customerIds(customerIdList.toArray())
                .inviteeId(paramRequest.getInviteeId())
                .channelType(paramRequest.getChannelType())
                .beginTime(paramRequest.getCreatedFrom())
                .endTime(paramRequest.getCreatedTo())
                .keyworks(paramRequest.getKeywords())
                .isBoss(false)
                .build();
        tradeQueryRequest.setPageNum(paramRequest.getPageNum());
        tradeQueryRequest.setPageSize(paramRequest.getPageSize());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            tradeQueryRequest.setStoreId(domainInfo.getStoreId());
        }
        //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
        tradeQueryRequest.makeAllAuditFlowStatus();

        Page<TradeVO> tradePage = pileTradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        List<TradeNewVO> tradeReponses = new ArrayList<>();
        tradePage.getContent().forEach(info -> {
            TradeNewVO tradeReponse = new TradeNewVO();
            BeanUtils.copyProperties(info, tradeReponse);

            /**
             * 获取订单商品图片
             */
            if (CollectionUtils.isNotEmpty(info.getTradeItems())) {
                tradeReponse.setTradeItemPic(info.getTradeItems().stream().map(TradeItemVO::getPic).collect(Collectors.toList()));
            }

            TradeStateVO tradeState = tradeReponse.getTradeState();
            boolean canReturnFlag =
                    tradeState.getFlowState() == FlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                            && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState
                            .getFlowState() != FlowState.VOID);
            canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
            //开店礼包不支持退货退款
            canReturnFlag = canReturnFlag && DefaultFlag.NO == tradeReponse.getStoreBagsFlag();
            //重新计算可退数量（排除特价商品）
            if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(info.getTradeState().getDeliverStatus())) {
                canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                        .trade(KsBeanUtil.convert(info, TradeDTO.class)).build()).getContext().getCanReturnFlag();
            }
            tradeReponse.setCanReturnFlag(canReturnFlag);
            FindProviderTradeResponse findProviderTradeResponse = providerTradeProvider.findByParentIdList(FindProviderTradeRequest.builder().parentId(Arrays.asList(info.getId())).build()).getContext();


            if (CollectionUtils.isNotEmpty(findProviderTradeResponse.getTradeVOList())) {
                List<TradeNewVO> tradeNewVOS = KsBeanUtil.copyListProperties(findProviderTradeResponse.getTradeVOList(), TradeNewVO.class);
                tradeReponse.setTradeNewVOList(tradeNewVOS);
            }

            tradeReponses.add(tradeReponse);
        });
        return BaseResponse.success(new PageImpl<>(tradeReponses, tradeQueryRequest.getPageable(),
                tradePage.getTotalElements()));
    }


    /**
     * 分页查询客户订单
     */
    @ApiOperation(value = "分页查询客户订单")
    @RequestMapping(value = "/customer/page", method = RequestMethod.POST)
    public BaseResponse<Page<TradeVO>> customerOrderPage(@RequestBody TradePageQueryRequest paramRequest) {
        TradeQueryDTO tradeQueryRequest = TradeQueryDTO.builder()
                .tradeState(TradeStateDTO.builder()
                        .flowState(paramRequest.getFlowState())
                        .payState(paramRequest.getPayState())
                        .deliverStatus(paramRequest.getDeliverStatus())
                        .build())
                .inviteeId(commonUtil.getOperatorId())
                .channelType(paramRequest.getChannelType())
                .beginTime(paramRequest.getCreatedFrom())
                .endTime(paramRequest.getCreatedTo())
                .keyworks(paramRequest.getKeywords())
                .customerOrderListAllType(paramRequest.isCustomerOrderListAllType())
                .isBoss(false)
                .build();
        tradeQueryRequest.setPageSize(paramRequest.getPageSize());
        tradeQueryRequest.setPageNum(paramRequest.getPageNum());

        //设定状态条件逻辑,需筛选出已支付下已审核与部分发货订单
        tradeQueryRequest.setPayedAndAudit();

        Page<TradeVO> tradePage = pileTradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        List<TradeVO> tradeReponses = new ArrayList<>();
        tradePage.getContent().forEach(info -> {
            TradeVO tradeReponse = new TradeVO();
            BeanUtils.copyProperties(info, tradeReponse);
            TradeStateVO tradeState = tradeReponse.getTradeState();
            boolean canReturnFlag =
                    (tradeState.getPayState() == PayState.PAID || tradeState.getFlowState() == FlowState.COMPLETED
                            && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState
                            .getFlowState() != FlowState.VOID);
            canReturnFlag = isCanReturnTime(flag, days, tradeState, canReturnFlag);
            //开店礼包不支持退货退款
            canReturnFlag = canReturnFlag && DefaultFlag.NO == tradeReponse.getStoreBagsFlag();
            tradeReponse.setCanReturnFlag(canReturnFlag);
            //待发货状态下排除未支付、待确认订单
            if (!((tradeState.getPayState() == PayState.NOT_PAID
                    || tradeState.getPayState() == PayState.UNCONFIRMED) && (tradeState.getDeliverStatus() ==
                    DeliverStatus.NOT_YET_SHIPPED || DeliverStatus.SHIPPED == tradeState.getDeliverStatus()))) {
                tradeReponses.add(tradeReponse);
            }
        });
        return BaseResponse.success(new PageImpl<>(tradeReponses, tradeQueryRequest.getPageable(),
                tradePage.getTotalElements()));
    }


    /**
     * 查询订单商品清单
     */
    @ApiOperation(value = "查询订单商品清单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/goods/{tid}", method = RequestMethod.GET)
    public BaseResponse<List<TradeItemVO>> tradeItems(@PathVariable String tid) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        checkUnauthorized(tid, trade);
        return BaseResponse.success(trade.getTradeItems());
    }

    /**
     * 查询订单发货清单
     */
    @ApiOperation(value = "查询订单发货清单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/deliverRecord/{tid}/{type}", method = RequestMethod.GET)
    public BaseResponse<TradeDeliverRecordResponse> tradeDeliverRecord(@PathVariable String tid, @PathVariable String
            type) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        //订单列表做验证,客户订单列表无需验证
        if ("0".equals(type)) {
            checkUnauthorized(tid, trade);
        }

        TradeDeliverRecordResponse tradeDeliverRecord = TradeDeliverRecordResponse.builder()
                .status(trade.getTradeState().getFlowState().getStateId())
                .tradeDeliver(trade.getTradeDelivers())
                .logisticsCompanyInfo(trade.getLogisticsCompanyInfo())
                .build();
        return BaseResponse.success(tradeDeliverRecord);
    }

    /**
     * 获取订单发票信息
     */
    @ApiOperation(value = "获取订单发票信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "type", value = "主客订单TYPE", required =
                    true)
    })

    @RequestMapping(value = "/invoice/{tid}/{type}", method = RequestMethod.GET)
    public BaseResponse<InvoiceVO> invoice(@PathVariable String tid, @PathVariable String type) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if ("0".equals(type)) {
            checkUnauthorized(tid, trade);
        }
        InvoiceVO invoice = trade.getInvoice();
        //若无发票收货地址，则默认为订单收货地址
        if (invoice.getAddress() == null) {
            InvoiceVO.builder()
                    .address(trade.getConsignee().getDetailAddress())
                    .contacts(trade.getConsignee().getName())
                    .phone(trade.getConsignee().getPhone())
                    .build();
        }
        return BaseResponse.success(invoice);
    }

    /**
     * 查询订单附件信息，只做展示使用
     */
    @ApiOperation(value = "查询订单附件信息，只做展示使用")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/encloses/{tid}", method = RequestMethod.GET)
    public BaseResponse<String> encloses(@PathVariable String tid) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        checkUnauthorized(tid, trade);
        return BaseResponse.success(trade.getEncloses());
    }

    /**
     * 查询订单付款记录
     */
    @ApiOperation(value = "查询订单付款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/payOrder/{tid}", method = RequestMethod.GET)
    public BaseResponse<FindPayOrderResponse> payOrder(@PathVariable String tid) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        //这里注释掉是为了客户订单
        //checkUnauthorized(tid, trade);

        FindPayOrderResponse payOrderResponse = null;
        try {

            BaseResponse<FindPayOrderResponse> response =
                    payOrderQueryProvider.findPilePayOrder(FindPayOrderRequest.builder().value(trade.getId()).build());

            payOrderResponse = response.getContext();
        } catch (SbcRuntimeException e) {
            if ("K-070001".equals(e.getErrorCode())) {
                payOrderResponse = new FindPayOrderResponse();
                payOrderResponse.setPayType(PayType.fromValue(Integer.valueOf(trade.getPayInfo().getPayTypeId())));
                payOrderResponse.setTotalPrice(trade.getTradePrice().getTotalPrice());
            }
        }
        if (Objects.nonNull(trade.getTradeGroupon())) {
            payOrderResponse.setGrouponNo(trade.getTradeGroupon().getGrouponNo());
        }
        payOrderResponse.setPayOrderPrice(trade.getTradePrice().getTotalPrice());
        payOrderResponse.setIsSelf(trade.getSupplier().getIsSelf());
        payOrderResponse.setStoreName(trade.getSupplier().getStoreName());
        return BaseResponse.success(payOrderResponse);
    }

    /**
     * 根据父订单查询子订单付款记录
     */
    @ApiOperation(value = "查询订单付款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "parentTid", value = "父订单ID", required = true)
    @RequestMapping(value = "/payOrders/{parentTid}", method = RequestMethod.GET)
    public BaseResponse<FindPayOrderListResponse> payOrders(@PathVariable String parentTid) {
        return payOrderQueryProvider.findPayOrderList(new FindPayOrderListRequest(parentTid));
    }

    /**
     * 根据订单号与物流单号查询发货信息
     */
    @ApiOperation(value = "根据订单号与物流单号查询发货信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "tid", value = "订单ID", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "deliverId", value = "发货单号", required = true)
    })
    @RequestMapping(value = "/shipments/{tid}/{deliverId}/{type}", method = RequestMethod.GET)
    public BaseResponse<TradeDeliverVO> shippItemsByLogisticsNo(@PathVariable String tid, @PathVariable String
            deliverId, @PathVariable String type) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if ("0".equals(type)) {
            checkUnauthorized(tid, trade);
        }
        TradeDeliverVO deliver = trade.getTradeDelivers().stream().filter(
                d -> deliverId.equals(d.getDeliverId())
        ).findFirst().orElseGet(null);
        List<ShippingItemVO> shippingItemVOS = new ArrayList<>(10);
        trade.getTradeItems().forEach(tradeItemVO -> {
            Optional<ShippingItemVO> first = deliver.getShippingItems().stream().filter(d -> d.getSkuId().equals(tradeItemVO.getSkuId()))
                    .peek(x -> {
                        x.setPrice(tradeItemVO.getPrice());
                    })
                    .findFirst();
            first.ifPresent(shippingItemVOS::add);
        });
        if (CollectionUtils.isNotEmpty(shippingItemVOS)) {
            deliver.setShippingItems(shippingItemVOS);
        }
        deliver.setLogisticsCompanyInfo(trade.getLogisticsCompanyInfo());
        return BaseResponse.success(deliver);
    }

    /**
     * 根据快递公司及快递单号查询物流详情
     */
    @ApiOperation(value = "根据快递公司及快递单号查询物流详情")
    @RequestMapping(value = "/deliveryInfos", method = RequestMethod.POST)
    public ResponseEntity<List<Map<Object, Object>>> logistics(@RequestBody DeliveryQueryRequest queryRequest) {
        List<Map<Object, Object>> result = new ArrayList<>();
//        List<Map<Object, Object>> result = new ArrayList<>();
//
//        CompositeResponse<ConfigRopResponse> response = sdkClient.buildClientRequest().post(ConfigRopResponse.class,
//                "logistics.config", "1.0.0");
//        //如果快递设置为启用
//        if (Objects.nonNull(response.getSuccessResponse()) && DefaultFlag.YES.toValue() == response
//                .getSuccessResponse().getStatus()) {
//            result = deliveryQueryProvider.queryExpressInfoUrl(queryRequest).getContext().getOrderList();
//        }
        //获取快递100配置信息
        ConfigQueryRequest request = new ConfigQueryRequest();
        request.setConfigType(ConfigType.KUAIDI100.toValue());
        LogisticsRopResponse response = systemConfigQueryProvider.findKuaiDiConfig(request).getContext();
        //已启用
        if (response.getStatus() == DefaultFlag.YES.toValue()) {
            result = deliveryQueryProvider.queryExpressInfoUrl(queryRequest).getContext().getOrderList();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增线下付款单
     */
    @ApiOperation(value = "新增线下付款单")
    @RequestMapping(value = "/pay/offline", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse createPayOrder(@RequestBody @Valid PaymentRecordRequest paymentRecordRequest) {
        Operator operator = commonUtil.getOperator();
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(paymentRecordRequest.getTid()).build())
                        .getContext().getTradeVO();

//        if (!trade.getBuyer().getId().equals(commonUtil.getOperatorId())) {
//            return BaseResponse.error("非法越权操作");
//        }

        if (trade.getTradeState().getFlowState() == FlowState.INIT || trade.getTradeState().getFlowState() ==
                FlowState.VOID) {
            throw new SbcRuntimeException("K-050206");
        }
        ReceivableAddDTO receivableAddDTO = ReceivableAddDTO.builder()
                .accountId(paymentRecordRequest.getAccountId())
                .payOrderId(trade.getPayOrderId())
                .createTime(paymentRecordRequest.getCreateTime())
                .comment(paymentRecordRequest.getRemark())
                .encloses(paymentRecordRequest.getEncloses())
                .build();

        TradeAddReceivableRequest tradeAddReceivableRequest =
                TradeAddReceivableRequest.builder()
                        .receivableAddDTO(receivableAddDTO)
                        .platform(operator.getPlatform())
                        .operator(operator)
                        .build();
        return pileTradeProvider.addReceivable(tradeAddReceivableRequest);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/cancel/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse cancel(@PathVariable(value = "tid") String tid) {
        Operator operator = commonUtil.getOperator();
        TradeCancelRequest tradeCancelRequest = TradeCancelRequest.builder()
                .tid(tid).operator(operator).build();

        pileTradeProvider.cancel(tradeCancelRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 确认收货
     *
     * @param tid 订单号
     */
    @ApiOperation(value = "确认收货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/receive/{tid}", method = RequestMethod.GET)
    public BaseResponse confirm(@PathVariable String tid) {
        Operator operator = commonUtil.getOperator();
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        checkUnauthorized(tid, trade);

        TradeConfirmReceiveRequest tradeConfirmReceiveRequest = TradeConfirmReceiveRequest.builder()
                .operator(operator).tid(tid).build();

        pileTradeProvider.confirmReceive(tradeConfirmReceiveRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 0元订单默认支付
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "0元订单默认支付")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/default/pay/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<Boolean> defaultPay(@PathVariable String tid) {

        TradeDefaultPayRequest tradeDefaultPayRequest = TradeDefaultPayRequest
                .builder()
                .payWay(PayWay.UNIONPAY)
                .tid(tid)
                .build();

        return BaseResponse.success(pileTradeProvider.defaultPay(tradeDefaultPayRequest).getContext().getPayResult());
    }

    //todo 2021-01-29 目前只考虑到单个店铺情况，如有需要后续需要变动
    @ApiOperation(value = "展示订单缺货商品基本信息")
    @RequestMapping(value = "/listStockOutGroupByStoreId/{wareId}/{storeId}", method = RequestMethod.GET)
    public BaseResponse<TradeItemStockOutResponse> listStockOutGroupByStoreId(@PathVariable Long wareId, @PathVariable Long storeId) {
        return tradeItemQueryProvider.listStockOutGroupByStoreId(TradeItemStockOutRequest
                .builder().wareId(wareId).customerId(commonUtil.getOperatorId()).storeId(storeId).build());
    }

    //todo 2021-01-29 目前只考虑到单个店铺情况，如有需要后续需要变动
    @ApiOperation(value = "去除少货商品")
    @RequestMapping(value = "/updateUnStock", method = RequestMethod.PUT)
    @LcnTransaction
    public BaseResponse deleteZeroStock(@RequestBody @Valid DeleteZeroStockRequest request) {
        return pileTradeItemProvider.updateUnStock(TradeItemStockOutRequest
                .builder().wareId(request.getRealWareId()).customerId(commonUtil.getOperatorId())
                .cityCode(request.getCityCode()).storeId(request.getStoreId()).build());
    }

    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getGoodsResponse(List<String> skuIds, CustomerVO customer) {
        TradeGetGoodsResponse response =
                pileTradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().skuIds(skuIds).build()).getContext();

        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
                (response.getGoodsInfos(), customer.getCustomerId());
        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        //获取客户的等级
        if (StringUtils.isNotBlank(customer.getCustomerId())) {
            //计算会员价
            response.setGoodsInfos(
                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                                    .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
                                    .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
                            .getContext().getGoodsInfoVOList());
        }
        return GoodsInfoResponse.builder().goodsInfos(response.getGoodsInfos())
                .goodses(response.getGoodses())
                .goodsIntervalPrices(response.getGoodsIntervalPrices())
                .build();
    }

    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getGoodsResponseNew(List<String> skuIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        TradeGetGoodsResponse response =
                pileTradeQueryProvider.getGoods(TradeGetGoodsRequest.builder()
                        .skuIds(skuIds)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();
        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
                (response.getGoodsInfos(), customer.getCustomerId());
        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        //获取客户的等级
        if (StringUtils.isNotBlank(customer.getCustomerId())) {
            //计算会员价
            response.setGoodsInfos(
                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
                                    .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
                                    .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
                            .getContext().getGoodsInfoVOList());
        }
        return GoodsInfoResponse.builder().goodsInfos(response.getGoodsInfos())
                .goodses(response.getGoodses())
                .goodsIntervalPrices(response.getGoodsIntervalPrices())
                .build();
    }

    private void checkUnauthorized(@PathVariable String tid, TradeVO detail) {
        List<String> customerId = new ArrayList<>(20);
        customerId.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerId.addAll(child);
        }
        if (!customerId.contains(detail.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{tid});
        }
    }


    /**
     * 1.订单未完成 （订单已支付扒拉了巴拉  显示退货退款按钮-与后台开关设置无关）
     * 2.订单已完成，在截止时间内，且退货开关开启时，前台显示 申请入口（完成时记录订单可退申请的截止时间，如果完成时开关关闭 时间记录完成当时的时间）
     *
     * @param flag
     * @param days
     * @param tradeState
     * @param canReturnFlag
     * @return
     */
    private boolean isCanReturnTime(boolean flag, int days, TradeStateVO tradeState, boolean canReturnFlag) {
        if (canReturnFlag && tradeState.getFlowState() == FlowState.COMPLETED) {
            if (flag) {
                if (Objects.nonNull(tradeState.getFinalTime())) {
                    //是否可退根据订单完成时配置为准
                    flag = tradeState.getFinalTime().isAfter(LocalDateTime.now());
                } else if (Objects.nonNull(tradeState.getEndTime())) {
                    //容错-历史数据
                    //判断是否在可退时间范围内
                    LocalDateTime endTime = tradeState.getEndTime();
                    return endTime.plusDays(days).isAfter(LocalDateTime.now());
                }
            } else {
                return false;
            }
            return flag;
        }
        return canReturnFlag;
    }

    private void validHomeDelivery(List<StoreCommitInfoDTO> storeCommitInfo, String addressId, Long wareId, String customerId) {

        for (StoreCommitInfoDTO inner : storeCommitInfo) {
            if (inner.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
                if (DefaultFlag.NO.equals(checkDeliveryArea(wareId, addressId,customerId))) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "超出配送范围");
                }
            }
        }

    }

    /**
     * 功能描述: 验证自提门店信息,如果存在塞入自提信息
     */
    private void validPickUpPoint(List<StoreCommitInfoDTO> storeCommitInfo, TradeCommitRequest tradeCommitRequest) {
        for (StoreCommitInfoDTO inner : storeCommitInfo) {
            if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                WareHouseVO wareHouseVO = wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                        .wareId(inner.getWareId()).storeId(inner.getStoreId()).build()).getContext().getWareHouseVO();
                if (wareHouseVO.getPickUpFlag().toValue() == PickUpFlag.NO.toValue()) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "仓库不合法");
                }
                inner.setWareHouseVO(wareHouseVO);
                tradeCommitRequest.setWareId(wareHouseVO.getWareId());
            }
        }
    }

    @GetMapping("/test/{sku}/{wareId}")
    public Object getPick(@PathVariable String sku, @PathVariable String wareId) {
        List<InventoryQueryReturnVO> inventoryQueryReturnVO = queryStorkByWMS(InventoryQueryRequest.builder()
                .CustomerID(AbstractXYYConstant.CUSTOMER_ID)
                .WarehouseID(wareId)
                .SKU(sku).build()).getContext().getInventoryQueryReturnVO();
        log.info(inventoryQueryReturnVO.toString());
        return inventoryQueryReturnVO;
    }

    private BaseResponse<InventoryQueryResponse> queryStorkByWMS(InventoryQueryRequest param) {
        param.setWareHouseCode(Collections.singletonList("002"));
        BaseResponse<InventoryQueryResponse> response = requestWMSInventoryProvider.queryInventoryBySku(param);
        if (Objects.isNull(response) || Objects.isNull(response.getContext())) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        return response;
    }

    /**
     * 验证小店商品，开店礼包
     */
    private void validShopGoods(List<TradeItemGroupVO> tradeItemGroups, DistributeChannel channel) {

        DefaultFlag storeBagsFlag = tradeItemGroups.get(0).getStoreBagsFlag();
        if (DefaultFlag.NO.equals(storeBagsFlag)) {
            if (channel.getChannelType() == ChannelType.SHOP) {
                // 1.验证商品是否是小店商品
                List<String> skuIds = tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream())
                        .map(item -> item.getSkuId()).collect(Collectors.toList());
                DistributorGoodsInfoVerifyRequest verifyRequest = new DistributorGoodsInfoVerifyRequest();
                verifyRequest.setDistributorId(channel.getInviteeId());
                verifyRequest.setGoodsInfoIds(skuIds);
                List<String> invalidIds = distributorGoodsInfoQueryProvider
                        .verifyDistributorGoodsInfo(verifyRequest).getContext().getInvalidIds();
                if (CollectionUtils.isNotEmpty(invalidIds)) {
                    throw new SbcRuntimeException("K-080302");
                }

                // 2.验证商品对应商家的分销开关有没有关闭
                tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream().map(item -> {
                    item.setStoreId(i.getSupplier().getStoreId());
                    return item;
                })).forEach(item -> {
                    if (DefaultFlag.NO.equals(
                            distributionCacheService.queryStoreOpenFlag(String.valueOf(item.getStoreId())))) {
                        throw new SbcRuntimeException("K-080302");
                    }
                });
            }
        } else {
            // 开店礼包商品校验
            RecruitApplyType applyType = distributionCacheService.queryDistributionSetting().getApplyType();
            if (RecruitApplyType.REGISTER.equals(applyType)) {
                throw new SbcRuntimeException("K-080302");
            }
            TradeItemVO tradeItem = tradeItemGroups.get(0).getTradeItems().get(0);
            List<String> goodsInfoIds = distributionCacheService.queryStoreBags()
                    .stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            if (!goodsInfoIds.contains(tradeItem.getSkuId())) {
                throw new SbcRuntimeException("K-080302");
            }
        }
    }

    /**
     * 判断当前用户是否企业会员
     *
     * @return
     */
    private boolean isIepCustomer() {
        EnterpriseCheckState customerState = commonUtil.getCustomer().getEnterpriseStatusXyy();
        return !Objects.isNull(customerState)
                && customerState == EnterpriseCheckState.CHECKED;
    }

    /**
     * 判断商品是否企业购商品
     *
     * @param enterpriseAuditState
     * @return
     */
    private boolean isEnjoyIepGoodsInfo(EnterpriseAuditState enterpriseAuditState) {
        return !Objects.isNull(enterpriseAuditState)
                && enterpriseAuditState == EnterpriseAuditState.CHECKED;
    }

    /*
     * 功能描述: 计算经纬度是否符合配送范围
     * 〈〉
     * @Param: [wareId, customerDeleiverAddressId]
     * @Return: com.wanmi.sbc.common.enums.DefaultFlag
     * @Author: yxb
     * @Date: 2020/8/3 16:18
     */
    private DefaultFlag checkDistance(Long wareId, String customerDeleiverAddressId) {
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
        if (Objects.isNull(response) || response.getDelFlag().equals(DeleteFlag.YES)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        if ((Objects.isNull(wareHouseVO) || (DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        double distance = LonLatUtil.getDistance(response.getLng(), response.getLat(), wareHouseVO.getLng(), wareHouseVO.getLat());
        if (distance <= (wareHouseVO.getDistance() * 1000)) {
            return DefaultFlag.YES;
        }
        return DefaultFlag.NO;
    }

    /**
     * 功能描述:
     * 检验是否符合免费店配条件
     *
     * @param wareId
     * @param customerDeleiverAddressId
     * @return: com.wanmi.sbc.common.enums.DefaultFlag
     */
    private DefaultFlag checkDeliveryArea(Long wareId, String customerDeleiverAddressId, String customerId) {
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
        if (Objects.isNull(response) || response.getDelFlag().equals(DeleteFlag.YES)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        if ((Objects.isNull(wareHouseVO) || (DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        //校验商品所在店铺配置免费配送范围
        List<FreightTemplateDeliveryAreaByStoreIdResponse> context = freightTemplateDeliveryAreaQueryProvider
                .query(FreightTemplateDeliveryAreaListRequest
                        .builder().storeId(wareHouseVO.getStoreId()).build()).getContext();
        //常规
        FreightTemplateDeliveryAreaByStoreIdResponse freightTemplateDeliveryAreaByStoreIdResponse = context.stream().filter(v -> {
            if (v.getAreaTenFreightTemplateDeliveryAreaVO().getWareId().equals(wareId) ) {
                return true;
            }
            return false;
        }).findAny().get();


        FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getFreightTemplateDeliveryAreaVO();

        if (
            //常规
            (Objects.nonNull(freightTemplateDeliveryAreaVO) && Objects.nonNull(freightTemplateDeliveryAreaVO.getDestinationArea())
            && (ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getProvinceId().toString())
            || ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getCityId().toString())))
            ) {
            return DefaultFlag.YES;
        }
        return DefaultFlag.NO;
    }

    /**
     * 功能描述:
     * 检验是否符合乡镇十件免费店配条件
     *
     * @param wareId
     * @param customerDeleiverAddressId
     * @return: com.wanmi.sbc.common.enums.DefaultFlag
     */
//    private DefaultFlag checkTenDeliveryArea(Long wareId, String customerDeleiverAddressId, String customerId) {
//        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
//                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
//        if (Objects.isNull(response) || response.getDelFlag().equals(DeleteFlag.YES)) {
//            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
//        }
//        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
//        if ((Objects.isNull(wareHouseVO) || (DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))) {
//            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
//        }
//        //校验商品所在店铺配置免费配送范围
//        FreightTemplateDeliveryAreaByStoreIdResponse freightTemplateDeliveryAreaByStoreIdResponse = freightTemplateDeliveryAreaQueryProvider
//                .query(FreightTemplateDeliveryAreaListRequest
//                        .builder().storeId(wareHouseVO.getStoreId()).build()).getContext();
//        //常规
//        FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getFreightTemplateDeliveryAreaVO();
//
//        //乡镇满十件
//        FreightTemplateDeliveryAreaVO areaTenFreightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getAreaTenFreightTemplateDeliveryAreaVO();
//
//        List<TradeItemGroupVO> tradeItemGroupList = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest.builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
//        Long tradeItemsNum = 0L;
//        if(CollectionUtils.isNotEmpty(tradeItemGroupList)){
//            TradeItemGroupVO tradeItemGroupVO = tradeItemGroupList.stream().findFirst().get();
//            //赠品数量统计
//            List<List<TradeItemMarketingVO>> collect = tradeItemGroupList.stream().map(tg -> tg.getTradeMarketingList()).collect(Collectors.toList());
//            List<TradeItemMarketingVO> tradeItemMarketingVOS = Lists.newArrayList();
//            if(CollectionUtils.isNotEmpty(collect)){
//                collect.forEach(m->{
//                    if(CollectionUtils.isNotEmpty(m)){
//                        tradeItemMarketingVOS.addAll(m);
//                    }
//                });
//                log.info("tradeItemMarketingVOS=========== {}",tradeItemMarketingVOS.size());
//                if(CollectionUtils.isNotEmpty(tradeItemMarketingVOS)){
//                    TradeItemMarketingResponse context = tradeQueryProvider.listGiftsByTradeItemMarketing(TradeItemMarketingRequest.builder()
//                            .tradeMarketingVOList(KsBeanUtil.convertList(tradeItemMarketingVOS, TradeMarketingVO.class))
//                            .wareId(wareId)
//                            .wareCode(tradeItemGroupVO.getWareCode())
//                            .build()).getContext();
//                    if(CollectionUtils.isNotEmpty(context.getGifts())){
//                        tradeItemsNum += context.getGifts().stream().mapToLong(v->v.getNum()).sum();
//                    }
//                }
//            }
//
//            //购买商品数量统计
//            List<List<TradeItemVO>> tradeItemVOS = tradeItemGroupList.stream().map(tg -> tg.getTradeItems()).collect(Collectors.toList());
//            if(CollectionUtils.isNotEmpty(tradeItemVOS)){
//                for (List<TradeItemVO> vos: tradeItemVOS) {
//                    tradeItemsNum += vos.stream().mapToLong(v -> v.getNum()).sum();
//                }
//            }
//        }
//        log.info("tradeItemsNum=========== {}",tradeItemsNum);
//        if (
//            //常规
//                (Objects.nonNull(freightTemplateDeliveryAreaVO) && Objects.nonNull(freightTemplateDeliveryAreaVO.getDestinationArea())
//                        && (ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getProvinceId().toString())
//                        || ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getCityId().toString())))
//        ) {
//            log.info("freightTemplateDeliveryAreaVO=========== {}",tradeItemsNum);
//            if(
//                //乡镇满十件(免费店配)
//                    (Objects.nonNull(areaTenFreightTemplateDeliveryAreaVO) && Objects.nonNull(areaTenFreightTemplateDeliveryAreaVO.getDestinationAreaName())
//                            && (checkDeliveryDestination(response.getDeliveryAddress(), Stream.of(areaTenFreightTemplateDeliveryAreaVO.getDestinationAreaName()).collect(Collectors.toList()))
//                            || checkDeliveryDestination(response.getDetailDeliveryAddress(), Stream.of(areaTenFreightTemplateDeliveryAreaVO.getDestinationAreaName()).collect(Collectors.toList()))))
//            ){
//                log.info("areaTenFreightTemplateDeliveryAreaVO=========== {}",tradeItemsNum);
//                if(tradeItemsNum >= 10){
//                    return DefaultFlag.YES;
//                }else{
//                    //收货地址为配置乡镇的，购买不到十件不免费店配
//                    return DefaultFlag.NO;
//                }
//            }
//            return DefaultFlag.YES;
//        }
//        return DefaultFlag.NO;
//    }

    /**
     * 收货地址匹配乡镇免费店配地址
     */
    private Boolean checkDeliveryDestination(String deliveryAddress, List<String> destinationAreaNameList){

        if(CollectionUtils.isEmpty(destinationAreaNameList) || Objects.isNull(deliveryAddress)){
            return false;
        }

        AtomicReference<Boolean> checkDelivery = new AtomicReference<>(false);

        destinationAreaNameList.forEach(da->{
            if(deliveryAddress.contains(da)){
                checkDelivery.set(true);
            }
        });

        return checkDelivery.get();
    }

    /**
     * 匹配分仓
     *
     * @param cityCode
     * @return
     */
    private WareHouseVO matchWareStore(Long cityCode) {
        //1. 从redis里获取主仓
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES, WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if (StringUtils.isNotEmpty(wareHousesStr)) {
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            if (CollectionUtils.isNotEmpty(wareHouseVOS)) {
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.NO.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList());
            } else {
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
            }
        } else {
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
        }
        //设置selectedAreas
        wareHouseMainList.stream().forEach(w -> {
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if (wareHouseMainList.stream().anyMatch(w -> w.getSelectedAreas().contains(cityCode))) {
            Optional<WareHouseVO> matchedWareHouse = wareHouseMainList.stream().filter(w -> w.getSelectedAreas()
                    .contains(cityCode)).findFirst();
            if (matchedWareHouse.isPresent()) {
                return matchedWareHouse.get();
            } else {
                throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NO_SERVICE, "您所在的区域没有可配的仓库，请重新修改收货地址");
            }
        } else {
            throw new SbcRuntimeException(WareHouseErrorCode.WARE_HOUSE_NO_SERVICE, "您所在的区域没有可配的仓库，请重新修改收货地址");
        }
    }

    /**
     * @Description: 订单各状态（待支付、待发货...）下的统计
     * @Date: 2020/7/16 11:23
     */
    @ApiOperation(value = "订单todo")
    @GetMapping(value = "/todo")
    public BaseResponse<OrderTodoResp> TardeTodo() {
        OrderTodoResp resp = new OrderTodoResp();
        TradeQueryDTO queryRequest = new TradeQueryDTO();
        queryRequest.setBuyerId(commonUtil.getOperatorId());
        TradeStateDTO tradeState = new TradeStateDTO();

        //设置待发货状态
        // 都未发货
        tradeState.setFlowState(FlowState.AUDIT);
        queryRequest.setTradeState(tradeState);
        tradeState.setPayState(PayState.PAID);
        tradeState.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
        Long noDeliveredCount = pileTradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount();
        // 部分发货
        tradeState.setFlowState(FlowState.DELIVERED_PART);
        queryRequest.setTradeState(tradeState);
        Long deliveredPartCount = pileTradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount();
        resp.setWaitDeliver(noDeliveredCount + deliveredPartCount);

        tradeState.setDeliverStatus(null);

        //设置待付款订单
        tradeState.setFlowState(FlowState.AUDIT);
        tradeState.setPayState(PayState.NOT_PAID);
        queryRequest.setTradeState(tradeState);
        resp.setWaitPay(pileTradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount());

        //设置待收货订单
        tradeState.setPayState(null);
        tradeState.setFlowState(FlowState.DELIVERED);
        queryRequest.setTradeState(tradeState);
        resp.setWaitReceiving(pileTradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount());


        ReturnCountByConditionRequest returnQueryRequest = new ReturnCountByConditionRequest();
        returnQueryRequest.setBuyerId(commonUtil.getOperatorId());
        // 待审核
        returnQueryRequest.setReturnFlowState(ReturnFlowState.INIT);
        Long waitAudit = returnOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();
        // 待收货
        returnQueryRequest.setReturnFlowState(ReturnFlowState.DELIVERED);
        Long waitReceiving = returnOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();
        // 待退款
        returnQueryRequest.setReturnFlowState(ReturnFlowState.RECEIVED);
        Long waitRefund = returnOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();

        resp.setRefund(waitAudit + waitReceiving + waitRefund);

        // 待审核
        returnQueryRequest.setReturnFlowState(ReturnFlowState.INIT);
        Long pileWaitAudit = returnPileOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();
        // 待收货
        returnQueryRequest.setReturnFlowState(ReturnFlowState.DELIVERED);
        Long pileWaitReceiving = returnPileOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();
        // 待退款
        returnQueryRequest.setReturnFlowState(ReturnFlowState.RECEIVED);
        Long pileWaitRefund = returnPileOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();

        resp.setPileRefund(pileWaitAudit + pileWaitReceiving + pileWaitRefund);

        // 商品待评价
        Long goodsEvaluate =
                goodsTobeEvaluateQueryProvider.getGoodsTobeEvaluateNum(GoodsTobeEvaluateQueryRequest.builder()
                        .customerId(commonUtil.getOperatorId()).build()).getContext();
        // 店铺服务待评价
        Long storeEvaluate =
                storeTobeEvaluateQueryProvider.getStoreTobeEvaluateNum(StoreTobeEvaluateQueryRequest.builder()
                        .customerId(commonUtil.getOperatorId()).build()).getContext();

        //客户囤货总数量
        PurchaseQueryRequest request = new PurchaseQueryRequest();
        request.setCustomerId(commonUtil.getOperatorId());
        Long pileCountGoodsNum = purchaseQueryProvider.getPileCountNumByCustomerId(request).getContext();

        resp.setWaitEvaluate(goodsEvaluate + storeEvaluate);
        resp.setPileCountGoodsNum(pileCountGoodsNum);
        return BaseResponse.success(resp);
    }


    /**
     * 好友代付页使用
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "展示订单基本信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/friend/show/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeCommitResultVO> commitFriendResp(@PathVariable String tid) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        boolean hasImg = CollectionUtils.isNotEmpty(trade.getTradeItems());
        return BaseResponse.success(new TradeCommitResultVO(tid, trade.getParentId(), trade.getTradeState(),
                trade.getPaymentOrder(),
                trade.getTradePrice().getTotalPrice(),
                trade.getOrderTimeOut(),
                trade.getSupplier().getStoreName(),
                trade.getSupplier().getIsSelf(),
                hasImg ? trade.getTradeItems().get(0).getPic() : null));
    }

    /**
     * 查询订单付款记录
     */
    @ApiOperation(value = "查询订单付款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/friend/payOrder/{tid}", method = RequestMethod.GET)
    public BaseResponse<FindPayOrderResponse> friendPayOrder(@PathVariable String tid) {
        TradeVO trade =
                pileTradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        FindPayOrderResponse payOrderResponse = null;
        try {

            BaseResponse<FindPayOrderResponse> response =
                    payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(trade.getId()).build());

            payOrderResponse = response.getContext();
        } catch (SbcRuntimeException e) {
            if ("K-070001".equals(e.getErrorCode())) {
                payOrderResponse = new FindPayOrderResponse();
                payOrderResponse.setPayType(PayType.fromValue(Integer.valueOf(trade.getPayInfo().getPayTypeId())));
                payOrderResponse.setTotalPrice(trade.getTradePrice().getTotalPrice());
            }
        }
        if (Objects.nonNull(trade.getTradeGroupon())) {
            payOrderResponse.setGrouponNo(trade.getTradeGroupon().getGrouponNo());
        }
        payOrderResponse.setPayOrderPrice(trade.getTradePrice().getTotalPrice());
        payOrderResponse.setIsSelf(trade.getSupplier().getIsSelf());
        payOrderResponse.setStoreName(trade.getSupplier().getStoreName());
        return BaseResponse.success(payOrderResponse);
    }

    /**
     * 校验商品是否可以立即购买
     */
    @ApiOperation(value = "校验商品是否可以立即购买")
    @RequestMapping(value = "/checkGoods", method = RequestMethod.PUT)
    public BaseResponse checkGoods(@RequestBody @Valid TradeItemConfirmRequest confirmRequest) {
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
        List<TradeItemDTO> tradeItems = confirmRequest.getTradeItems().stream().map(
                o -> TradeItemDTO.builder().skuId(o.getSkuId()).num(o.getNum()).build()
        ).collect(Collectors.toList());
        List<String> skuIds =
                confirmRequest.getTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        GoodsInfoResponse response = getGoodsResponseNew(skuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
        //商品验证
        verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItems, Lists.newArrayList(),
                KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true,customerId));
        verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                (GoodsInfoVO::getStoreId).collect(Collectors.toList())));

        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "我的囤货列表")
    @RequestMapping(value = "/myPileList",method = RequestMethod.POST)
    public BaseResponse<PilePurchaseResponse> myPileList(@RequestBody @Valid PilePurchaseRequest request){
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setCustomerId(customerId);
        PilePurchaseResponse response = purchaseQueryProvider.queryMyPilePurchaseList(request).getContext();
        //填充酒水保质期
        if(Objects.nonNull(response.getGoodsInfos()) && CollectionUtils.isNotEmpty(response.getGoodsInfos().getContent())){
            response.getGoodsInfos().getContent().forEach(goodsInfoVO -> {
                if(Objects.nonNull(goodsInfoVO.getShelflife()) && goodsInfoVO.getShelflife() == 9999){
                    goodsInfoVO.setShelflife(0L);
                }
            });
        }

        //return BaseResponse.success(purchaseQueryProvider.queryMyPilePurchaseList(request).getContext());
        return BaseResponse.success(response);
    }

    //清洗数据接口
    @ApiOperation(value = "清洗囤货历史数据")
    @RequestMapping(value = "/writingHistoricalData/{customerId}",method = RequestMethod.GET)
    public BaseResponse writingHistoricalData(@PathVariable String customerId){
        if (StringUtils.isNotEmpty(customerId) && customerId.equals("qwasd90plmjuio568")){
            return pileTradeProvider.writingHistoricalData();
        }else {
            return BaseResponse.FAILED();
        }
    }

    /**
     * 查询用户当前可提货商品
     */
    @ApiOperation(value = "查询用户当前可提货商品")
    @RequestMapping(value = "/getPilePurchaseByCustomerId", method = RequestMethod.POST)
    public BaseResponse<CustomerPilePurchaseListResponse> getPilePurchaseByCustomerId(@RequestBody com.wanmi.sbc.order.api.request.purchase.PilePurchaseRequest request){
        String operatorId = commonUtil.getOperatorId();
        if(Objects.isNull(operatorId)){
            return BaseResponse.success(new CustomerPilePurchaseListResponse());
        }
        request.setCustomerId(operatorId);
        return pileTradeQueryProvider.getPilePurchaseByCustomerId(request);
    }
}
