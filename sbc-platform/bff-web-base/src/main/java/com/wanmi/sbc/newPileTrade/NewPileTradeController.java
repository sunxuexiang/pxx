package com.wanmi.sbc.newPileTrade;
import com.google.common.collect.Lists;
import com.wanmi.ms.domain.PageRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.common.base.MicroServicePage;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.request.PaymentRecordRequest;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.coupon.service.CouponService;
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
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeListForUseByCustomerIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingByGoodsInfoIdAndIdResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingPurchaseLimitVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.order.OrderUtil;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.*;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPilePickTradeItemQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeItemQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseQueryCacheRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradePageCriteriaRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradePageQueryRequest;
import com.wanmi.sbc.order.api.response.inventorydetailsamount.InventoryDetailSamountResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.shopcart.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.shopcart.api.response.purchase.PilePurchaseResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PilePurchaseStoreResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseQueryResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.order.api.response.trade.*;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.order.request.DeliveryHomeFlagRequest;
import com.wanmi.sbc.order.request.TradeItemConfirmRequest;
import com.wanmi.sbc.order.request.TradeItemRequest;
import com.wanmi.sbc.order.request.TradeParentTidRequest;
import com.wanmi.sbc.order.response.DeliveryHomeFlagResponse;
import com.wanmi.sbc.order.response.TradeConfirmResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartNewPileProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartNewPileQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "NewPileTradeController", description = "囤货订单api")
@RestController
@RequestMapping("/newPileTrade")
@Slf4j
@Validated
public class NewPileTradeController {


    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private NewPileTradeItemQueryProvider newPileTradeItemQueryProvider;

    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;

    @Autowired
    private NewPilePickTradeItemQueryProvider newPilePickTradeItemQueryProvider;

    @Resource
    private VerifyQueryProvider verifyQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Resource
    private AuditQueryProvider auditQueryProvider;

    @Resource
    private StoreQueryProvider storeQueryProvider;

    @Resource
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Resource
    private DistributionCacheService distributionCacheService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedissonClient redissonClient;


    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;


    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private OrderUtil orderUtil;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private PurchaseProvider purchaseProvider;

    @Autowired
    private ShopCartNewPileQueryProvider shopCartNewPileQueryProvider;

    @Autowired
    private ShopCartNewPileProvider shopCartNewPileProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private  TradeQueryProvider TradeQueryProvider;

    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;

    @Autowired
    private CouponService couponService;

    /**
     * 提交订单，用于生成订单操作
     */
    @ApiOperation(value = "提交订单，用于生成囤货订单操作")
    @RequestMapping(value = "/newPileCommitAll", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<List<TradeCommitResultVO>> newPileCommitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
//        if (null != tradeCommitRequest.getWalletBalance() && tradeCommitRequest.getWalletBalance().compareTo(BigDecimal.ZERO) > 0) {
//            throw new SbcRuntimeException("K-000099", "鲸币暂停使用，请关闭使用鲸币选项，请联系客服！！！");
//        }
        //设置订单来源渠道
        tradeCommitRequest.setSourceChannel(commonUtil.getChannel(HttpUtil.getRequest()));
        tradeCommitRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        List<TradeCommitResultVO> successResults;
        try {

            beforeSubmit(tradeCommitRequest);
            successResults = newPileTradeProvider.newPileCommitAll(tradeCommitRequest).getContext().getTradeCommitResults();
        } catch (Exception e) {
            throw e;
        }
        return BaseResponse.success(successResults);
    }


    /**
     * 提交订单，用于生成订单操作
     */
    @ApiOperation(value = "提交订单，用于生成提货订单操作")
    @RequestMapping(value = "/newPilePickCommitAll", method = RequestMethod.POST)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<List<TradeCommitResultVO>> newPilePickCommitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
//        if (null != tradeCommitRequest.getWalletBalance() && tradeCommitRequest.getWalletBalance().compareTo(BigDecimal.ZERO) > 0) {
//            throw new SbcRuntimeException("K-000099", "鲸币暂停使用，请关闭使用鲸币选项，请联系客服！！！");
//        }
        //设置订单来源渠道
        tradeCommitRequest.setSourceChannel(commonUtil.getChannel(HttpUtil.getRequest()));
        tradeCommitRequest.setWareId(tradeCommitRequest.getWareId());
        List<TradeCommitResultVO> successResults;
        try {
            tradeCommitRequest.getStoreCommitInfoList().stream().forEach(t -> {
                if (DeliverWay.OTHER.equals(t.getDeliverWay())) {
                    throw new SbcRuntimeException("K-000099", "请选择配送方式");
                }
            });
            beforeSubmitPick(tradeCommitRequest);
            successResults = newPileTradeProvider.newPilePickCommitAll(tradeCommitRequest).getContext().getTradeCommitResults();
        } catch (Exception e) {
            throw e;
        }
        return BaseResponse.success(successResults);
    }


    /**
     * 查询订单 代付人 使用
     */
    @ApiOperation(value = "根据parentId或tid查询订单详情")
    @RequestMapping(value = "/getParentIdOrTid", method = RequestMethod.POST)
    public BaseResponse<NewPileTradeVO> detailByParentIdOrTid(@RequestBody @Valid TradeParentTidRequest tradeParentTidRequest) {
        NewPileTradeGetByIdResponse tradeGetByIdResponse = null;
        if (StringUtils.isNotBlank(tradeParentTidRequest.getParentId())) {
            tradeGetByIdResponse = newPileTradeProvider.getByParent(TradeGetByParentRequest.builder().
                    parentId(tradeParentTidRequest.getParentId()).build()).getContext();
        } else if (StringUtils.isNotBlank(tradeParentTidRequest.getTId())) {
            tradeGetByIdResponse =
                    newPileTradeProvider.getById(TradeGetByIdRequest.builder().
                            tid(tradeParentTidRequest.getTId()).build()).getContext();
        }
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        NewPileTradeVO detail = tradeGetByIdResponse.getTradeVO();

        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        NewPileTradeStateVO tradeState = detail.getTradeState();
        boolean canReturnFlag =
                tradeState.getFlowState() == NewPileFlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                        && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState.getFlowState
                        () != NewPileFlowState.VOID);
        canReturnFlag = orderUtil.isCanReturnTimeNewPile(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        detail.setCanReturnFlag(canReturnFlag);
        return BaseResponse.success(detail);
    }

    @ApiOperation(value = "验证配送到家范围")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkHomeFlagNewPile", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeFlagResponse> checkHomeFlagNewPile(@RequestBody @Valid DeliveryHomeFlagRequest req) {
        DeliveryHomeFlagResponse deliveryHomeFlagResponse = new DeliveryHomeFlagResponse();
        req.setWareId(req.getWareId());
        deliveryHomeFlagResponse.setFlag(checkDeliveryAreaNewPile(req.getWareId(), req.getCustomerDeliveryAddressId(),commonUtil.getOperator().getUserId()));
        return BaseResponse.success(deliveryHomeFlagResponse);
    }
    /**
     * 功能描述:
     * 检验是否符合免费店配条件
     *
     * @param wareId
     * @param customerDeleiverAddressId
     * @return: com.wanmi.sbc.common.enums.DefaultFlag
     */
    private DefaultFlag checkDeliveryAreaNewPile(Long wareId, String customerDeleiverAddressId, String customerId) {
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
        log.info("运费模板接口返回结果"+context);
        FreightTemplateDeliveryAreaByStoreIdResponse freightTemplateDeliveryAreaByStoreIdResponse = new FreightTemplateDeliveryAreaByStoreIdResponse();
        if (CollectionUtils.isNotEmpty(context)){
            freightTemplateDeliveryAreaByStoreIdResponse= context.stream().filter(v -> {
                if (v.getAreaTenFreightTemplateDeliveryAreaVO().getWareId().equals(wareId)) {
                    return true;
                }
                return false;
            }).findAny().orElse(null);
        }
        log.info("运费模板接口返回结果"+freightTemplateDeliveryAreaByStoreIdResponse);
        if (Objects.isNull(freightTemplateDeliveryAreaByStoreIdResponse)){
            return DefaultFlag.NO;
        }
        //常规
        FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getFreightTemplateDeliveryAreaVO();
        List<TradeItemGroupVO> tradeItemGroupList = newPilePickTradeItemQueryProvider.itemListByCustomerId(TradeItemByCustomerIdRequest.builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        log.info("777777777888888777777---------->"+JSONObject.toJSONString(tradeItemGroupList));
        Long tradeItemsNum = 0L;
        if(CollectionUtils.isNotEmpty(tradeItemGroupList)){
            TradeItemGroupVO tradeItemGroupVO = tradeItemGroupList.stream().findFirst().get();
            //赠品数量统计
            List<List<TradeItemMarketingVO>> collect = tradeItemGroupList.stream().map(tg -> tg.getTradeMarketingList()).collect(Collectors.toList());
            List<TradeItemMarketingVO> tradeItemMarketingVOS = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(collect)){
                collect.forEach(m->{
                    if(CollectionUtils.isNotEmpty(m)){
                        tradeItemMarketingVOS.addAll(m);
                    }
                });
                log.info("tradeItemMarketingVOS=========== {}",tradeItemMarketingVOS.size());
            }

            //购买商品数量统计
            List<List<TradeItemVO>> tradeItemVOS = tradeItemGroupList.stream().map(tg -> tg.getTradeItems()).collect(Collectors.toList());
            log.info("777777777777777---------->"+JSONObject.toJSONString(tradeItemVOS));
            if(CollectionUtils.isNotEmpty(tradeItemVOS)){
                for (List<TradeItemVO> vos: tradeItemVOS) {
                    tradeItemsNum += vos.stream().mapToLong(v -> v.getNum()).sum();
                }
            }
        }
        log.info("tradeItemsNum=========== {}",tradeItemsNum);


        if (
            //常规
                (Objects.nonNull(freightTemplateDeliveryAreaVO) && Objects.nonNull(freightTemplateDeliveryAreaVO.getDestinationArea())
                        && (ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getProvinceId().toString())
                        || ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getCityId().toString())))
        ) {

            if(tradeItemsNum.longValue() < 5 && tradeItemsNum.longValue() > 0 ){
                log.info("DefaultFlag.NO=========== {}",tradeItemsNum);
                //小于5件看是否是同省配送，同省配送则不显示第三方物流
                //根据仓库ID获取省市区
                Long provinceId = wareHouseVO.getProvinceId();
                //获取收货地址的省份ID
                Long provinceIdAddress = response.getProvinceId();
                log.info("provinceIdAddress.provinceId=========== {},{}",provinceIdAddress,provinceId);
                if(provinceId.longValue() == provinceIdAddress.longValue()){
                    return DefaultFlag.YES;
                }
                return DefaultFlag.NO;
            }
            log.info("DefaultFlag.YES=========== {}",tradeItemsNum);
            return DefaultFlag.YES;
        }
        log.info("return DefaultFlag.NO=========== {}",tradeItemsNum);
        return DefaultFlag.NO;
    }

    /***
     * 提交前的校验
     * @param tradeCommitRequest
     */
    private void beforeSubmit(TradeCommitRequest tradeCommitRequest) {
        //根据收货地址，查询当前的用户是否配置了第四级街道地址
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
        if (response.getTwonId() == null || response.getTwonId().equals("") || Objects.isNull(response.getTwonId())
                || response.getTwonName() == null || response.getTwonName().equals("") || Objects.isNull(response.getTwonName())) {
            throw new SbcRuntimeException("K-888888", "地址功能优化，需要重新编辑全部地址，否则无法提交订单");
        }

        Operator operator = commonUtil.getOperator();
        CustomerVO customer = commonUtil.getCustomer();
        boolean checkMatchFlag = true;
        //如果都是自提商品无需验证仓库匹配范围
        List<DeliverWay> collect = tradeCommitRequest.getStoreCommitInfoList().stream().map(StoreCommitInfoDTO::getDeliverWay).distinct().collect(Collectors.toList());
        if (collect.size() == 1 && collect.contains(DeliverWay.PICK_SELF)) {
            checkMatchFlag = false;
        }
        if (checkMatchFlag) {
            WareHouseVO wareHouseVO = orderUtil.matchWareStore(tradeCommitRequest.getCityCode());
            tradeCommitRequest.setWareId(wareHouseVO.getWareId());
            tradeCommitRequest.setWareHouseCode(wareHouseVO.getWareCode());
            tradeCommitRequest.setWareName(wareHouseVO.getWareName());
        } else {
            WareHouseVO wareHouse = commonUtil.getWareHouse(HttpUtil.getRequest());
            tradeCommitRequest.setWareId(wareHouse.getWareId());
            tradeCommitRequest.setWareHouseCode(wareHouse.getWareCode());
            tradeCommitRequest.setWareName(wareHouse.getWareName());
        }

        tradeCommitRequest.setOperator(operator);
        List<TradeItemGroupVO> tradeItemGroups = newPileTradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();
        //区域限购验证
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        orderUtil.checkGoodsForCommitOrder(tradeItemGroups, deliveryAddress);
        //限定区域指定商品限购校验
        orderUtil.checkGoodsNumsForCommitOrder(tradeCommitRequest, tradeItemGroups);

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
            tradeCommitRequest.setIsDistributor(DefaultFlag.YES);
        }
        tradeCommitRequest.setDistributeChannel(distributeChannel);
        tradeCommitRequest.setShopName(distributionCacheService.getShopName());

        // 设置分销设置开关
        tradeCommitRequest.setOpenFlag(distributionCacheService.queryOpenFlag());
        tradeCommitRequest.getStoreCommitInfoList().forEach(item ->
                item.setStoreOpenFlag(distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString()))
        );
        // 验证小店商品
        orderUtil.validShopGoods(tradeItemGroups, tradeCommitRequest.getDistributeChannel());

        tradeCommitRequest.getStoreCommitInfoList().stream().forEach(t -> {
            if (DeliverWay.LOGISTICS.equals(t.getDeliverWay())) {
                LogisticsInfoDTO logisticsInfo = t.getLogisticsInfo();
                if (Objects.nonNull(logisticsInfo) && StringUtils.isNotEmpty(logisticsInfo.getId())) {
                    String id = logisticsInfo.getId();
                    LogisticsCompanyByIdResponse context =
                            logisticsCompanyQueryProvider.getById(LogisticsCompanyByIdRequest.builder().id(Long.valueOf(id)).build()).getContext();
                    if (Objects.isNull(context) || Objects.isNull(context.getLogisticsCompanyVO())) {
                        throw new SbcRuntimeException("K-170003");
                    }
                }
            }

            //自提的地址，设置wareCode
            if (Objects.nonNull(t.getWareId())) {
                WareHouseVO wareHouseVO1 = commonUtil.getWareHouseByWareId(t.getWareId());
                t.setWareHouseCode(wareHouseVO1.getWareCode());
            } else {
                t.setWareId(tradeCommitRequest.getWareId());
                t.setWareHouseCode(tradeCommitRequest.getWareHouseCode());
            }
        });
        //自提商品验证自提点合法性，如果合法塞入自提点信息
        orderUtil.validPickUpPoint(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest);
        //囤货订单不需要验证配置距离
        //验证配送到家距离
//        orderUtil.validHomeDelivery(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest.getConsigneeId(), tradeCommitRequest.getWareId(), customer.getCustomerId());
    }




    /***
     * 提交前的校验
     * @param tradeCommitRequest
     */
    private void beforeSubmitPick(TradeCommitRequest tradeCommitRequest) {
        //根据收货地址，查询当前的用户是否配置了第四级街道地址
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
        if (response.getTwonId() == null || response.getTwonId().equals("") || Objects.isNull(response.getTwonId())
                || response.getTwonName() == null || response.getTwonName().equals("") || Objects.isNull(response.getTwonName())) {
            throw new SbcRuntimeException("K-888888", "地址功能优化，需要重新编辑全部地址，否则无法提交订单");
        }

        Operator operator = commonUtil.getOperator();
        CustomerVO customer = commonUtil.getCustomer();

        WareHouseVO wareHouseVO = orderUtil.matchWareStore(tradeCommitRequest.getCityCode());
        tradeCommitRequest.setWareId(wareHouseVO.getWareId());
        tradeCommitRequest.setWareHouseCode(wareHouseVO.getWareCode());
        tradeCommitRequest.setWareName(wareHouseVO.getWareName());

        WareHouseVO wareHouseVO1 = orderUtil.matchWareStore(response.getCityId());
        if(wareHouseVO.getWareId() != wareHouseVO1.getWareId()){
            throw new SbcRuntimeException("K-888888", "收获地址非仓库服务范围！");
        }

        tradeCommitRequest.setOperator(operator);
        List<TradeItemGroupVO> tradeItemGroups = newPilePickTradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();

        DefaultFlag storeBagsFlag = tradeItemGroups.get(0).getStoreBagsFlag();
        if (DefaultFlag.NO.equals(storeBagsFlag) && !distributionService.checkInviteeIdEnable()) {
            // 非开店礼包情况下，判断小店状态不可用
            throw new SbcRuntimeException("K-080301");
        }

        tradeCommitRequest.setShopName(distributionCacheService.getShopName());

        // 设置分销设置开关
        tradeCommitRequest.setOpenFlag(distributionCacheService.queryOpenFlag());
        tradeCommitRequest.getStoreCommitInfoList().forEach(item ->{
            item.setStoreOpenFlag(distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString()));
            item.setWareId(tradeCommitRequest.getWareId());
            item.setWareHouseCode(wareHouseVO1.getWareCode());
            item.setWareHouseVO(wareHouseVO1);
        });

        //验证配送到家距离
        orderUtil.validHomeDelivery(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest.getConsigneeId(), tradeCommitRequest.getWareId(), customer.getCustomerId());
    }


    /**
     * 提货单快照
     */
    @ApiOperation(value = "提货单快照(快照)")
    @RequestMapping(value = "/confirmAllPileNewPick", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse confirmAllPileNewPick(@RequestBody @Valid TradeItemConfirmRequest confirmRequest) {
        //加锁，防止同一用户在两个手机上同时下单，导致数据错误， 不用用户id去加错
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000001");
        }
        if (CollectionUtils.isEmpty(confirmRequest.getTradeItems())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单商品信息为空！");
        }
        confirmRequest.getTradeItems().forEach(tradeItemRequest -> {

            if (Objects.isNull(tradeItemRequest.getDevanningskuId())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "" +
                        "");
            }
        });

        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            Long wareId = confirmRequest.getWareId();
            confirmRequest.setWareId(wareId);
            log.info("TradeBaseController.confirm lock:{}", customerId);
            List<TradeItemDTO> tradeItems = Collections.emptyList();
            List<GoodsInfoDTO> skuList = Collections.emptyList();
            WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
            //验证用户
            CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customerId)).getContext();

            if (CollectionUtils.isNotEmpty(confirmRequest.getTradeItems())) {
                tradeItems = confirmRequest.getTradeItems().stream().map(
                        o -> TradeItemDTO.builder()
                                .skuId(o.getSkuId())
                                .erpSkuNo(o.getErpSkuNo())
                                .num(o.getNum())
                                .devanningId(o.getDevanningskuId())
                                .parentGoodsInfoId(o.getParentGoodsInfoId())
                                .wareId(wareId)
                                .build()
                ).collect(Collectors.toList());
                //统计商品数量
                HashMap<String, BigDecimal> buyNumMaps = Maps.newHashMap();
                tradeItems.forEach(v -> {
                    DevanningGoodsInfoVO devanningInfoVO = devanningGoodsInfoQueryProvider.getInfoById(
                            DevanningGoodsInfoByIdRequest.builder()
                                    .devanningId(v.getDevanningId())
                                    .build()).getContext().getDevanningGoodsInfoVO();
                    v.setDivisorFlag(devanningInfoVO.getDivisorFlag());
                    //设置商品副标题
                    v.setGoodsSubtitle(devanningInfoVO.getGoodsInfoSubtitle());
                    BigDecimal multiply = BigDecimal.valueOf(v.getNum()).multiply(devanningInfoVO.getDivisorFlag());
                    if (Objects.isNull(buyNumMaps.get(v.getSkuId()))) {
                        buyNumMaps.put(v.getSkuId(), multiply);
                    } else {
                        buyNumMaps.put(v.getSkuId(), buyNumMaps.get(v.getSkuId()).add(multiply));
                    }
                });

                log.info("buyNumMaps========  {}", JSONObject.toJSONString(buyNumMaps));
                log.info("===============confirmRequest:{}", JSONObject.toJSONString(confirmRequest));

                List<Long> devanning = confirmRequest.getTradeItems().stream().map(TradeItemRequest::getDevanningskuId).collect(Collectors.toList());
                GoodsInfoResponse response = orderUtil.getDevanningGoodsResponseNew(devanning, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
                log.info("===============confirmresponse:{}", JSONObject.toJSONString(response));

                //验证库存（真实库存）
//                buyNumMaps.forEach((k, v) -> {
//                    GoodsInfoVO goodsInfoVO = response.getGoodsInfos().stream().filter(f -> k.equals(f.getGoodsInfoId())).findFirst().orElse(null);
//                    log.info("goodsInfoVOresponse========= {}", JSONObject.toJSONString(response));
//                    if (v.compareTo(goodsInfoVO.getStock()) == 1) {
//                        throw new SbcRuntimeException("K-050137");
//                    }
//                });
                // 2.填充商品区间价
                tradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, true, customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);


                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                List<String> ids = tradeItems.stream().map(TradeItemDTO::getSkuId).collect(Collectors.toList());
                //赋值使用的囤货商品及付款金额
                setPickGoodsList(tradeItems);
                skuList = KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class);
            }
            return newPilePickTradeItemQueryProvider.snapshotRetail(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems(tradeItems)
                    .skuList(skuList)
                    .retailtTradeMarketingList(confirmRequest.getRetailTradeMarketingList())
                    .build());
        } catch (SbcRuntimeException e) {
            throw new SbcRuntimeException(e.getErrorCode(), e.getResult());
        } finally {
            log.info("TradeBaseController.confirm unlock:{}", customerId);
            rLock.unlock();
        }
    }

    private void setPickGoodsList(List<TradeItemDTO> items){
        NewPileGoodsNumByCustomerRequest request = new NewPileGoodsNumByCustomerRequest();
        request.setCustomer(commonUtil.getCustomer());
        BaseResponse<List<GoodsPickStockResponse>> newPileGoodsNumByCustomer = newPileTradeProvider.getNewPileGoodsNumByCustomer(request);
        List<GoodsPickStockResponse> getPileStock = newPileGoodsNumByCustomer.getContext();
        if (CollectionUtils.isEmpty(getPileStock)) {
            throw new SbcRuntimeException("k-030301", "无可提商品");
        }
        items.forEach(item -> {
            List<PickGoodsDTO> pickGoodsList = new ArrayList<>();
            List<InventoryDetailSamountVO> inventoryDetailSamountTrades = new ArrayList<>();
            Long temp = 0l;
            for (GoodsPickStockResponse pickStock : getPileStock) {
                if (item.getSkuId().equals(pickStock.getGoodsInfoId())) {
                    PickGoodsDTO pickGoodsDTO = new PickGoodsDTO();
                    pickGoodsDTO.setNewPileOrderNo(pickStock.getNewPileTradeNo());
                    pickGoodsDTO.setGoodsInfoId(pickStock.getGoodsInfoId());

                    temp += pickStock.getStock();
                    if (item.getNum() >= temp) {
                        pickGoodsDTO.setNum(pickStock.getStock());
                        pickGoodsList.add(pickGoodsDTO);
                    } else {
                        Long stock = pickStock.getStock() - (temp - item.getNum());
                        if(stock > 0){
                            pickGoodsDTO.setNum(stock);
                            pickGoodsList.add(pickGoodsDTO);
                        }else{
                            log.info("囤货提货数据========：{},{}",JSONObject.toJSONString(pickGoodsDTO),temp);
                        }
                    }
                    if(pickGoodsDTO.getNum() != null){
                        //使用囤货单金额
                        BaseResponse<InventoryDetailSamountResponse> inventoryDetailSamountFlag = inventoryDetailSamountProvider.getInventoryDetailSamountFlag(InventoryDetailSamountRequest.builder()
                                .oid(pickGoodsDTO.getNewPileOrderNo())
                                .num(pickGoodsDTO.getNum().intValue())
                                .goodsInfoId(pickGoodsDTO.getGoodsInfoId())
                                .build());
                        inventoryDetailSamountTrades.addAll(inventoryDetailSamountFlag.getContext().getInventoryDetailSamountVOS());
                    }
                    if (item.getNum() < temp) {
                        break;
                    }
                }
            }
            log.info("updateNewPileTradeItem----------->setPickGoodsList" + JSONObject.toJSONString(pickGoodsList));
            item.setPickGoodsList(pickGoodsList);
            item.setInventoryDetailSamount(inventoryDetailSamountTrades);
        });
    }


    @ApiOperation(value = "用于确认订单后，创建订单前的获取订单商品信息（查询快照）")
    @RequestMapping(value = "/getPurchaseItemsNewPilePick", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<TradeConfirmResponse> getPurchaseItemsNewPilePick(Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag,@RequestParam Long wareId) {

        log.info("=============wareId:{}", wareId);
        TradeConfirmResponse confirmResponse = new TradeConfirmResponse();
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();

        List<TradeItemGroupVO> tradeItemGroups = newPilePickTradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        /**
         * 设置itemGroupVo的分仓信息
         */
        Long finalWareId = wareId;
        tradeItemGroups.stream().forEach(g -> g.setWareId(finalWareId));

        List<TradeConfirmItemVO> items = new ArrayList<>();
        //批发
        List<Long> skuIds = tradeItemGroups.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getDevanningId).collect(Collectors.toList());

        //批发商品
        GoodsInfoResponse skuResp = orderUtil.getDevanningGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);


        //所有店铺信息
        Map<Long, StoreVO> storeMap = storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                (tradeItemGroups.stream().map(g -> g
                        .getSupplier().getStoreId())
                        .collect(Collectors.toList()))).getContext().getStoreVOList().stream().collect(Collectors
                .toMap(StoreVO::getStoreId,
                        s -> s, (a, b) -> a));

        // 如果为PC商城下单，将分销商品变为普通商品
        if (ChannelType.PC_MALL.equals(commonUtil.getDistributeChannel().getChannelType())) {
            tradeItemGroups.forEach(tradeItemGroup ->
                    tradeItemGroup.getTradeItems().forEach(tradeItem -> {
                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    })
            );
            if (CollectionUtils.isNotEmpty(skuResp.getGoodsInfos())) {
                skuResp.getGoodsInfos().forEach(item -> item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS));
            }
        }
        //企业会员判断
        boolean isIepCustomerFlag = orderUtil.isIepCustomer();
        tradeItemGroups.forEach(
                g -> {
                    if (SaleType.WHOLESALE.equals(g.getSaleType())) {
                        //批发
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
                                verifyQueryProvider.verifyGoodsDevanning(new VerifyGoodsRequest(tradeItemDTOList, Collections
                                        .emptyList(),
                                        KsBeanUtil.convert(skuResp, TradeGoodsInfoPageDTO.class),
                                        g.getSupplier().getStoreId(), true, checkStockFlag, customerId)).getContext().getTradeItems();

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
                        confirmResponse.setStoreBagsFlag(g.getStoreBagsFlag());
                        TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest = new TradeQueryPurchaseInfoRequest();
                        tradeQueryPurchaseInfoRequest.setTradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class));
                        items.add(newPileTradeProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO());
                    }
                }
        );

        List<InventoryDetailSamountVO> inventoryDetailSamount = new ArrayList<>();
        //商品sku+订单列表=商品数量
        items.stream().forEach(tradeConfirmItemVO -> {
            //所有商品
            Long goodsNum = tradeConfirmItemVO.getTradeItems().stream().mapToLong((s) -> s.getNum()).sum();
            tradeConfirmItemVO.setGoodsNum(goodsNum);
            tradeConfirmItemVO.getTradeItems().forEach(var->{
                inventoryDetailSamount.addAll(var.getInventoryDetailSamount());
            });
        });

        confirmResponse.setTradeConfirmItems(items);
        if(CollectionUtils.isNotEmpty(inventoryDetailSamount)){
            BigDecimal reduce = inventoryDetailSamount.stream()
                    .filter(p -> Objects.nonNull(p.getAmortizedExpenses()))
                    .map(p -> p.getAmortizedExpenses()).reduce(BigDecimal.ZERO, BigDecimal::add);
            confirmResponse.setPaidPrice(reduce);
        }

        return BaseResponse.success(confirmResponse);
    }


    /**
     * 从采购单中确认订单商品
     */
    @ApiOperation(value = "从采购单中确认订单商品(快照)")
    @RequestMapping(value = "/confirmAllPileNew", method = RequestMethod.POST)
    //@LcnTransaction
    @MultiSubmit
    public BaseResponse confirmAllPileNew(@RequestBody @Valid TradeItemConfirmRequest confirmRequest){
        //加锁，防止同一用户在两个手机上同时下单，导致数据错误， 不用用户id去加错
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)){
            throw new SbcRuntimeException("K-000001");
        }
        if (CollectionUtils.isEmpty(confirmRequest.getTradeItems()) && CollectionUtils.isEmpty(confirmRequest.getRetailTradeItems())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单商品信息为空！");
        }
        confirmRequest.getTradeItems().forEach(tradeItemRequest -> {

            if (Objects.isNull(tradeItemRequest.getDevanningskuId())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"" +
                        "");
            }
        });
        if (CollectionUtils.isNotEmpty(confirmRequest.getTradeItems())){
            Map<Long, TradeItemRequest> collect2 = confirmRequest.getTradeItems().stream()
                    .collect(Collectors.toMap(TradeItemRequest::getDevanningskuId, Function.identity(), (a, b) -> a));
            confirmRequest.setTradeItems(new ArrayList<>(collect2.values()));
        }

        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
            confirmRequest.setWareId(wareId);
            log.info("TradeBaseController.confirm lock:{}",customerId);
            List<TradeItemDTO> tradeItems = Collections.emptyList();
            List<TradeItemDTO> retailTradeItems = Collections.emptyList();
            List<GoodsInfoDTO> skuList = Collections.emptyList();
            List<GoodsInfoDTO> retailSkuList = Collections.emptyList();
            WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
            //验证用户
            CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customerId)).getContext();
            log.info("===============//批发:{}",JSONObject.toJSONString(confirmRequest.getTradeItems()));
            if (CollectionUtils.isNotEmpty(confirmRequest.getTradeItems())) {
                //批发
                tradeItems = confirmRequest.getTradeItems().stream().map(
                        o -> TradeItemDTO.builder()
                                .skuId(o.getSkuId())
                                .erpSkuNo(o.getErpSkuNo())
                                .num(o.getNum())
                                .devanningId(o.getDevanningskuId())
                                .parentGoodsInfoId(o.getParentGoodsInfoId())
                                .wareId(wareId)
                                .build()
                ).collect(Collectors.toList());
                //统计商品数量
                HashMap<String, BigDecimal> buyNumMaps = Maps.newHashMap();
                List<String> goodsInfoIds = new ArrayList<>();
                tradeItems.forEach(v -> {

                    goodsInfoIds.add(v.getSkuId());
                    DevanningGoodsInfoVO devanningInfoVO = devanningGoodsInfoQueryProvider.getInfoById(
                            DevanningGoodsInfoByIdRequest.builder()
                                    .devanningId(v.getDevanningId())
                                    .build()).getContext().getDevanningGoodsInfoVO();
                    v.setDivisorFlag(devanningInfoVO.getDivisorFlag());
                    //设置商品副标题
                    v.setGoodsSubtitle(devanningInfoVO.getGoodsInfoSubtitle());
                    BigDecimal multiply = BigDecimal.valueOf(v.getNum()).multiply(devanningInfoVO.getDivisorFlag());
                    if(Objects.isNull(buyNumMaps.get(v.getSkuId()))){
                        buyNumMaps.put(v.getSkuId(),multiply);
                    }else{
                        buyNumMaps.put(v.getSkuId(),buyNumMaps.get(v.getSkuId()).add(multiply));
                    }
                });

                log.info("buyNumMaps========  {}", JSONObject.toJSONString(buyNumMaps));
                //验证采购单
                List<Long> devanningSkuIds =
                        confirmRequest.getTradeItems().stream().map(TradeItemRequest::getDevanningskuId).collect(Collectors.toList());
                PurchaseQueryCacheRequest purchaseQueryRequest = new PurchaseQueryCacheRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setDevaningId(devanningSkuIds);
                purchaseQueryRequest.setWareId(wareId);
                PurchaseQueryResponse purchaseQueryResponse = shopCartNewPileQueryProvider.queryShopCarExit(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<Long> existIds = exsistSku.stream().map(PurchaseVO::getDevanningId).collect(Collectors.toList());
                if (devanningSkuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }

                log.info("===============confirmRequest:{}",JSONObject.toJSONString(confirmRequest));

                List<Long> devanning = confirmRequest.getTradeItems().stream().map(TradeItemRequest::getDevanningskuId).collect(Collectors.toList());
                GoodsInfoResponse response = orderUtil.getDevanningGoodsResponseNew(devanning, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());

                //验证库存（marketing 虚拟库存）
                BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();
                if(CollectionUtils.isEmpty(startPileActivity.getContext())){
                    throw new SbcRuntimeException("K-050137", "无正在进行中的囤货活动");
                }
                //多商家囤货获取参与囤货活动商品虚拟库存
                List<PileActivityGoodsVO> pileActivitycontext = pileActivityProvider.getStartPileActivityPileActivityGoods(
                        PileActivityPileActivityGoodsRequest.builder().goodsInfoIds(goodsInfoIds).build()).getContext();
                if (CollectionUtils.isNotEmpty(pileActivitycontext)) {
                    pileActivitycontext.forEach(var -> {
                        if (buyNumMaps.get(var.getGoodsInfoId()).compareTo(BigDecimal.valueOf(var.getVirtualStock())) == 1) {
                            throw new SbcRuntimeException("K-050137", "存在商品囤货库存不足" + JSONObject.toJSONString(var.getGoodsInfoId()));
                        }
                    });
                }
                log.info("===============confirmresponse:{}",JSONObject.toJSONString(response));

                // 2.填充商品区间价
                tradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, false,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);


                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                //营销活动是否生效校验
                MarketingByGoodsInfoIdAndIdResponse context = marketingQueryProvider.getEffectiveMarketingByIdsAndGoods(MarketingEffectiveRequest.builder()
                        .tradeMarketingList(confirmRequest.getTradeMarketingList())
                        .wareId(wareId).type("redis")
                        .tradeItems(KsBeanUtil.convert(tradeItems, TradeItemInfoDTO.class)).build()).getContext();
                List<TradeMarketingDTO> tradeMarketingList = context.getTradeMarketingList();

                // 过滤已达到营销限购数量的活动
                tradeMarketingList = this.filterOverPurchuseLimit(tradeMarketingList, buyNumMaps,customerId);

                // 囤货活动不参加赠品营销活动，如果活动包含赠品，把活动移除掉
                if(CollectionUtils.isNotEmpty(tradeMarketingList)){
                    tradeMarketingList = tradeMarketingList.stream().filter(x->{
                        List<String> skuIds = Optional.ofNullable(x).map(TradeMarketingDTO::getGiftSkuIds).orElse(Lists.newArrayList());
                        List<String> giftErpSkuNos = Optional.ofNullable(x).map(TradeMarketingDTO::getGiftErpSkuNos).orElse(Lists.newArrayList());
                        return CollectionUtils.isEmpty(skuIds) || CollectionUtils.isEmpty(giftErpSkuNos);
                    }).collect(Collectors.toList());
                }
                confirmRequest.setTradeMarketingList(tradeMarketingList);

                if (CollectionUtils.isNotEmpty(context.getRemovemarketinglist()) || CollectionUtils.isNotEmpty(context.getRemovelist())){
                    shopCartNewPileProvider.delFirstSnapShopAndMarkeing(customerId);
                    String erroInfo = "";
                    String endmesg = "请重新结算";
                    if (CollectionUtils.isNotEmpty(context.getRemovemarketinglist())){
                        erroInfo=erroInfo+"您购买的部分商品所参与的活动已结束";
                    }
                    if (CollectionUtils.isNotEmpty(context.getRemovelist())){
                        erroInfo=erroInfo+"赠品已经失效";
                    }
                    erroInfo=erroInfo+endmesg;
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,erroInfo);
                }

                skuList = KsBeanUtil.convertList(response.getGoodsInfos(),GoodsInfoDTO.class);

                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                List<DevanningGoodsInfoMarketingVO> requestparam= new LinkedList<>();
                for (TradeItemDTO tradeItemDTO:tradeItems){
                    DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO = new DevanningGoodsInfoMarketingVO();
                    devanningGoodsInfoMarketingVO.setDevanningId(tradeItemDTO.getDevanningId())
                            .setBuyCount(tradeItemDTO.getNum()).setDivisorFlag(tradeItemDTO.getDivisorFlag())
                            .setGoodsInfoId(tradeItemDTO.getSkuId()).setSaleType(0);
                    requestparam.add(devanningGoodsInfoMarketingVO);
                }
                //区域限购营销限购校验
                StockAndPureChainNodeRsponse context1 = purchaseProvider.checkStockPurchase(StockAndPureChainNodeRequeest.builder()
                        .wareId(wareId)
                        .provinceId(deliveryAddress.getProvinceId())
                        .cityId(deliveryAddress.getCityId())
                                .subType(1)
                        .checkPure(requestparam).build()).getContext();
                if (Objects.nonNull(context1) && CollectionUtils.isNotEmpty(context1.getCheckPure())){
                    for (DevanningGoodsInfoPureVO i:context1.getCheckPure() ){
                        if (Objects.isNull(i)){
                            continue;
                        }
                        if (Objects.nonNull(i.getType())){
                            if (i.getType()==0){
                                throw new SbcRuntimeException("k-250001");
                            }
                            else if (i.getType()==1){
                                throw new SbcRuntimeException("k-250002");
                            }
                            else if (i.getType()==2){
                                throw new SbcRuntimeException("k-250003");
                            }
                            else if (i.getType()==3){
                                throw new SbcRuntimeException("k-250004");
                            }
                            else if (i.getType()==-1){
                                throw new SbcRuntimeException("k-250005");
                            }
                        }

                    }
                }

                List<TradeItemDTO> tradeItemscopy =new ArrayList<>();
                log.info("===============tradeMarketingList:{}",JSONObject.toJSONString(tradeMarketingList));
                KsBeanUtil.copyList(tradeItems,tradeItemscopy);
                tradeItemscopy.forEach(t->{
                    BigDecimal bigDecimal = t.getDivisorFlag().multiply(BigDecimal.valueOf(t.getNum())).setScale(2, BigDecimal.ROUND_UP);
                    t.setBNum(bigDecimal);
                });
            }
            return newPileTradeItemQueryProvider.snapshotRetail(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems(tradeItems)
                    .tradeMarketingList(confirmRequest.getTradeMarketingList())
                    .skuList(skuList).retailTradeItems(retailTradeItems)
                    .retailtTradeMarketingList(confirmRequest.getRetailTradeMarketingList())
                    .retailSkuList(retailSkuList).build());
        }catch (SbcRuntimeException e){
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        }finally {
            log.info("TradeBaseController.confirm unlock:{}",customerId);
            rLock.unlock();
        }
    }

    /**
     * 过滤已超过限购数量的营销活动
     * @param tradeMarketingList
     * @param buyNumMaps
     * @param customerId
     * @return
     */
    private List<TradeMarketingDTO> filterOverPurchuseLimit(List<TradeMarketingDTO> tradeMarketingList, HashMap<String, BigDecimal> buyNumMaps,String customerId) {
        // TODO 上线前记得使用filterOverPurchuseLimitV2优化查询次数-for czg
        if(CollectionUtils.isEmpty(tradeMarketingList)){
            return tradeMarketingList;
        }

        return tradeMarketingList.stream().filter(tradeMarketingDTO->{
            List<String> skuIds = tradeMarketingDTO.getSkuIds();
            if(CollectionUtils.isEmpty(skuIds)){
                return false;
            }
            String skuId = skuIds.get(0);
            Long purchaseNumOfMarketing = this.getPurchaseNumOfMarketing(tradeMarketingDTO.getMarketingId(), skuId, customerId);
            if(Objects.isNull(purchaseNumOfMarketing)){
                return true;
            }
            BigDecimal bNum = Optional.ofNullable(buyNumMaps.get(skuId)).orElse(BigDecimal.ZERO);
            if(bNum.longValue() <= purchaseNumOfMarketing){ // 未超过最大限购量当前营销活动有效
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 获取限购提示
     * （1）数字：表示限购数量
     * （2）null：没有限购
     * @param marketingId
     * @param goodsInfoId
     * @param customerId
     * @return
     */
    private Long getPurchaseNumOfMarketing(Long marketingId, String goodsInfoId,String customerId){
        //查询营销总限购和单商品限购
        MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest = new MarketingScopeByMarketingIdRequest();
        marketingScopeByMarketingIdRequest.setMarketingId(marketingId);
        marketingScopeByMarketingIdRequest.setSkuId(goodsInfoId);
        BaseResponse<MarketingScopeByMarketingIdResponse> marketingScopeByMarketingIdResponseBaseResponse = marketingScopeQueryProvider.listByMarketingIdAndSkuIdAndCache(marketingScopeByMarketingIdRequest);

        List<MarketingScopeVO> marketingScopeVOList = Optional.ofNullable(marketingScopeByMarketingIdResponseBaseResponse).map(BaseResponse::getContext)
                .map(MarketingScopeByMarketingIdResponse::getMarketingScopeVOList)
                .orElse(Lists.newArrayList());
        if(CollectionUtils.isEmpty(marketingScopeVOList)){
            return null;
        }
        MarketingScopeVO marketingScopeVO = marketingScopeVOList.stream().findFirst().get();
        Long purchaseNum = marketingScopeVO.getPurchaseNum(); // 营销总限购量
        Long perUserPurchaseNum = marketingScopeVO.getPerUserPurchaseNum(); // 营销单用户限购量

        // 1. 后台未设置限购数量
        if(Objects.isNull(purchaseNum) && Objects.isNull(perUserPurchaseNum)){
            return null;
        }

        AtomicReference<BigDecimal> marketingNum = new AtomicReference<>(BigDecimal.ZERO); // 已占用的限购物数量（总限购）
        AtomicReference<BigDecimal> marketinguNumOfPerUser = new AtomicReference<>(BigDecimal.ZERO);// 已占用的单用户限购（单用户）

        //通过用户id查询当前商品的营销购买数量
        Map<String,Object> req = new LinkedHashMap<>();
        req.put("customerId", customerId);
        req.put("marketingId",marketingId);
        req.put("goodsInfoId", goodsInfoId);
        List<MarketingPurchaseLimitVO> purchaseLimits = marketingPurchaseLimitProvider.queryListByParmNoUser(req).getContext();
        if (CollectionUtils.isNotEmpty(purchaseLimits)){
            List<String> collect = purchaseLimits.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
            //获取生效订单
            List<TradeVO> context2 = tradeQueryProvider.getOrderByIdsSimplify(collect).getContext();
            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
            purchaseLimits = purchaseLimits.stream().filter(q->{
                if (collect1.contains(q.getTradeId())){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(purchaseLimits)){
                BigDecimal reduce = purchaseLimits.stream().map(MarketingPurchaseLimitVO::getNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                marketingNum.set(marketingNum.get().add(reduce));
            }

        }
        List<MarketingPurchaseLimitVO> purchaseLimitForPerUsers = marketingPurchaseLimitProvider.queryListByParm(req).getContext();
        if (CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
            List<String> collect = purchaseLimitForPerUsers.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
            //获取生效订单
            List<TradeVO> context2 = tradeQueryProvider.getOrderByIdsSimplify(collect).getContext();
            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
            purchaseLimitForPerUsers = purchaseLimitForPerUsers.stream().filter(q->{
                if (collect1.contains(q.getTradeId())){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
                BigDecimal reduce = purchaseLimitForPerUsers.stream().map(MarketingPurchaseLimitVO::getNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                marketinguNumOfPerUser.set(marketinguNumOfPerUser.get().add(reduce));
            }
        }

        // 2. 该商品的没有购买记录,直接返回设置值
        if(CollectionUtils.isEmpty(purchaseLimits) && CollectionUtils.isEmpty(purchaseLimitForPerUsers)){
            if(Objects.nonNull(purchaseNum) && Objects.nonNull(perUserPurchaseNum)) {
                return Long.min(purchaseNum, perUserPurchaseNum);
            }
            if(Objects.nonNull(purchaseNum)){
                return Long.max(purchaseNum, 0L);
            }
            return Long.max(perUserPurchaseNum, 0L);
        }

        // 3. 有购买记录-两个限购均有
        if(CollectionUtils.isNotEmpty(purchaseLimits) && CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
            Long l1 =  Long.max(purchaseNum - marketingNum.get().longValue(), 0L);
            Long l2 =  Long.max( perUserPurchaseNum - marketinguNumOfPerUser.get().longValue(), 0L);
            return Long.min(l1, l2);
        }

        // 4. 有购买记录-只有营销总限购
        if(CollectionUtils.isNotEmpty(purchaseLimits)){
            return Long.max(purchaseNum - marketingNum.get().longValue(), 0L);
        }

        // 5. 有购买记录-只有单用户限购
        return  Long.max( perUserPurchaseNum - marketinguNumOfPerUser.get().longValue(), 0L);
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

        newPileTradeProvider.cancel(tradeCancelRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "用于确认订单后，创建订单前的获取订单商品信息（查询快照）")
    @RequestMapping(value = "/getPurchaseItemsNewPile", method = RequestMethod.GET)
    //@LcnTransaction
    public BaseResponse<TradeConfirmResponse> getPurchaseItemsNewPile(Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag) {

        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        log.info("=============wareId:{}",wareId);
        TradeConfirmResponse confirmResponse = new TradeConfirmResponse();
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        List<TradeItemGroupVO> tradeItemGroups = newPileTradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        /**
         * 设置itemGroupVo的分仓信息
         */
        Long finalWareId = wareId;
        tradeItemGroups.stream().forEach(g -> g.setWareId(finalWareId));

        List<TradeConfirmItemVO> items = new ArrayList<>();
        //批发
        List<Long> skuIds = tradeItemGroups.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getDevanningId).collect(Collectors.toList());
        //零售
        List<String> retailSkuIds = tradeItemGroups.stream().filter(item -> SaleType.RETAIL.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getSkuId).collect(Collectors.toList());
        GoodsInfoResponse skuResp = orderUtil.getDevanningGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);

        //零售商品
        GoodsInfoResponse retailSkuResp = orderUtil.getGoodsRetailResponseNew(retailSkuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
        //批发赠品
        List<String> giftIds =
                tradeItemGroups.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType()))
                        .flatMap(i -> i.getTradeMarketingList().stream().filter(param -> CollectionUtils.isNotEmpty(param.getGiftSkuIds())))
                        .flatMap(r -> r.getGiftSkuIds().stream())
                        .distinct().collect(Collectors.toList());




        //所有店铺信息
        Map<Long, StoreVO> storeMap = storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                (tradeItemGroups.stream().map(g -> g
                        .getSupplier().getStoreId())
                        .collect(Collectors.toList()))).getContext().getStoreVOList().stream().collect(Collectors
                .toMap(StoreVO::getStoreId,
                        s -> s,(a,b)->a));
        TradeGetGoodsResponse giftResp;
        giftResp = newPileTradeProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
        final TradeGetGoodsResponse giftTemp = giftResp;

        // 如果为PC商城下单，将分销商品变为普通商品
        if (ChannelType.PC_MALL.equals(commonUtil.getDistributeChannel().getChannelType())) {
            tradeItemGroups.forEach(tradeItemGroup ->
                    tradeItemGroup.getTradeItems().forEach(tradeItem -> {
                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    })
            );
            if (CollectionUtils.isNotEmpty(skuResp.getGoodsInfos())) {
                skuResp.getGoodsInfos().forEach(item -> item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS));
            }
            if (CollectionUtils.isNotEmpty(retailSkuResp.getGoodsInfos())) {
                retailSkuResp.getGoodsInfos().forEach(item -> item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS));
            }
        }
        //企业会员判断
        boolean isIepCustomerFlag = orderUtil.isIepCustomer();
        tradeItemGroups.forEach(
                g -> {
                    if (SaleType.WHOLESALE.equals(g.getSaleType())) {
                        //批发
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
                                verifyQueryProvider.verifyGoodsDevanning(new VerifyGoodsRequest(tradeItemDTOList, Collections
                                        .emptyList(),
                                        KsBeanUtil.convert(skuResp, TradeGoodsInfoPageDTO.class),
                                        g.getSupplier().getStoreId(), true, checkStockFlag, customerId)).getContext().getTradeItems();



                        //大客户价商品回设 新增大客户标识字段判断 add by jiangxin 20211203
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
                        List<TradeItemDTO> giftItemList = KsBeanUtil.convert(giftVoList, TradeItemDTO.class);
                        confirmResponse.setStoreBagsFlag(g.getStoreBagsFlag());
                        TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest = new TradeQueryPurchaseInfoRequest();
                        tradeQueryPurchaseInfoRequest.setTradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class));
                        tradeQueryPurchaseInfoRequest.setTradeItemList(giftItemList);
                        items.add(newPileTradeProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO());
                    }
                    else {
                        //零售
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
                        retailSkuResp.getGoodsInfos().forEach(item -> {
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
                                        KsBeanUtil.convert(retailSkuResp, TradeGoodsInfoPageDTO.class),
                                        g.getSupplier().getStoreId(), true, checkStockFlag, customerId)).getContext().getTradeItems();
                        //大客户价商品回设 新增大客户标识字段判断 add by jiangxin 20211203
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

                        confirmResponse.setStoreBagsFlag(g.getStoreBagsFlag());
                        TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest = new TradeQueryPurchaseInfoRequest();
                        tradeQueryPurchaseInfoRequest.setTradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class));
                        //赠品列表给空，不然后续参数copy会报空指针
                        tradeQueryPurchaseInfoRequest.setTradeItemList(Collections.emptyList());
                        TradeConfirmItemVO tradeConfirmItemVO = newPileTradeProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO();
                        tradeConfirmItemVO.setSaleType(SaleType.RETAIL);
                        items.add(tradeConfirmItemVO);
                    }
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

        // 校验拼团信息
        orderUtil.validGrouponOrder(confirmResponse, tradeItemGroups, customerId);

        //只有批发订单时才去查询需要使用的优惠券列表
        boolean calCouponFlag = tradeItemGroups.stream().anyMatch(item -> item.getSaleType().equals(SaleType.WHOLESALE));
        if (calCouponFlag) {
            // 根据确认订单计算出的信息，查询出使用优惠券页需要的优惠券列表数据(批发订单)
            DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
            List<TradeItemInfoDTO> tradeDtos = items.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType())).flatMap(confirmItem ->
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
            List<Long> wareIds = new ArrayList(2);
            wareIds.add(-1L);
            wareIds.add(finalWareId);
            requ.setWareIds(wareIds);
//            confirmResponse.setCouponCodes(couponCodeQueryProvider.listForUseByCustomerId(requ).getContext()
//                    .getCouponCodeList());

            // 将优惠券处理成商家入驻版本格式
            List<CouponCodeVO> couponCodeList = couponCodeQueryProvider.listForUseByCustomerId(requ).getContext().getCouponCodeList();
            couponService.setCouponCodes(confirmResponse, couponCodeList);
        }


        List<TradeItemMarketingVO> collect = tradeItemGroups.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeMarketingList().stream().filter(param -> CollectionUtils.isNotEmpty(param.getGiftSkuIds())))
                .collect(Collectors.toList());
        Map<String,String> map =new HashMap<>();
        if (CollectionUtils.isNotEmpty(collect)){
            collect.forEach(v->{
                String marketingId = v.getMarketingId().toString();
                String marketingLevelId = v.getMarketingLevelId().toString();
                List<String> giftSkuIds = v.getGiftSkuIds();
                giftSkuIds.forEach(q->{
                    map.put(q,marketingId+marketingLevelId+q);
                });
            });

        }
        confirmResponse.getTradeConfirmItems().forEach(v->{
            if(CollectionUtils.isNotEmpty(v.getGifts())){
                v.getGifts().forEach(q->{
                    String s = map.get(q.getSkuId());
                    if (StringUtils.isNotBlank(s)){
                        String o = redisService.getString(s);
                        if (Objects.nonNull(o)){
                            Long num = Long.parseLong(o);
                            if (num.compareTo(0l)<=0){
                                q.setNum(0l);
                            } else if (num.compareTo(q.getNum())<0){
                                q.setNum(num);
                            }
                        }
                    }
                });
            }
        });

        return BaseResponse.success(confirmResponse);
    }


    /**
     * 新增线下付款单
     */
    @ApiOperation(value = "新增线下付款单")
    @RequestMapping(value = "/pay/offline", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse createPayOrder(@RequestBody @Valid PaymentRecordRequest paymentRecordRequest) {
        Operator operator = commonUtil.getOperator();
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(paymentRecordRequest.getTid()).build())
                        .getContext().getTradeVO();

        if (trade.getTradeState().getFlowState() != NewPileFlowState.INIT
                && !PayType.OFFLINE.name().equals(trade.getPayInfo().getPayTypeName())) {
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
        return newPileTradeProvider.addReceivable(tradeAddReceivableRequest);
    }

    /**
     * 查询订单付款记录
     */
    @ApiOperation(value = "查询订单付款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/payOrder/{tid}", method = RequestMethod.GET)
    public BaseResponse<FindPayOrderResponse> payOrder(@PathVariable String tid) {
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        //这里注释掉是为了客户订单
        //checkUnauthorized(tid, trade);

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
     * 分页查询订单
     */
    @ApiOperation(value = "分页查询订单")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<Page<NewPileTradeVO>> page(@RequestBody NewPileTradePageQueryRequest paramRequest) {
        List<String> customerIdList = new ArrayList<>(20);
        customerIdList.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerIdList.addAll(child);
        }

        log.info("newPiles:{}",JSONObject.toJSONString(paramRequest));

        NewPileTradeQueryDTO tradeQueryRequest = NewPileTradeQueryDTO.builder()
                .tradeState(NewPileTradeStateDTO.builder()
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

        //大白鲸粉页面的订单列表查询
//        if (Objects.nonNull(paramRequest.getMarkType()) && paramRequest.getMarkType().intValue() == 2) {
////            tradeQueryRequest.setFlowStates(paramRequest.getFlowStates());
//            tradeQueryRequest.makeAllAuditFlowStatusList();
//        }
        tradeQueryRequest.makeAllAuditFlowStatusList(paramRequest.getMarkType());
        Page<NewPileTradeVO> tradePage = newPileTradeProvider.pageCriteria(NewPileTradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();

        log.info("newPileTradeProvider:{}",JSONObject.toJSONString(tradePage));

        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        List<NewPileTradeVO> tradeReponses = new ArrayList<>();
        tradePage.getContent().forEach(info -> {
            NewPileTradeVO tradeReponse = new NewPileTradeVO();
            BeanUtils.copyProperties(info, tradeReponse);
            NewPileTradeStateVO tradeState = tradeReponse.getTradeState();
            boolean canReturnFlag =
                    tradeState.getFlowState() == NewPileFlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                            && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState
                            .getFlowState() != NewPileFlowState.VOID);
            canReturnFlag = orderUtil.isCanReturnTimeNewPile(flag, days, tradeState, canReturnFlag);
            //开店礼包不支持退货退款
            canReturnFlag = canReturnFlag && DefaultFlag.NO == tradeReponse.getStoreBagsFlag();
            //重新计算可退数量（排除特价商品）
            if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(info.getTradeState().getDeliverStatus())) {
                canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                        .trade(KsBeanUtil.convert(info, TradeDTO.class)).build()).getContext().getCanReturnFlag();
            }
            tradeReponse.setCanReturnFlag(canReturnFlag);

            List<String> sku = new LinkedList<>();
            tradeReponse.getTradeItems().forEach(t -> {
                if (Objects.isNull(t.getDevanningId())) {
                    //拆箱id为空需要查询最小步长的拆箱id赋值
                    sku.clear();
                    sku.add(t.getSkuId());
                    Optional<DevanningGoodsInfoVO> first = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(sku).build()).getContext().getDevanningGoodsInfoVOS()
                            .stream().findFirst();
                    if (first.isPresent()) {
                        t.setDevanningId(first.get().getDevanningId());
                        t.setDivisorFlag(first.get().getDivisorFlag());
                        t.setAddStep(first.get().getAddStep());
                    }
                }
            });
            tradeReponses.add(tradeReponse);
        });
        //增加商品数量值
        for (NewPileTradeVO tradeVO : tradeReponses) {
            Long sum = tradeVO.getTradeItems().stream().mapToLong(TradeItemVO::getNum).sum();
            sum = sum == null ? 0L : sum;
            tradeVO.setGoodsTotalNum(sum);
        }
        log.info("tradeReponses:{}",JSONObject.toJSONString(tradeReponses));
        return BaseResponse.success(new PageImpl<>(tradeReponses, tradeQueryRequest.getPageable(),
                tradePage.getTotalElements()));
    }


    /**
     * 查询订单详情
     */
    @ApiOperation(value = "查询订单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/detail/{tid}", method = RequestMethod.GET)
    public BaseResponse<NewPileTradeVO> details(@PathVariable String tid) {
        NewPileTradeGetByIdResponse tradeGetByIdResponse =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext();
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        NewPileTradeVO detail = tradeGetByIdResponse.getTradeVO();

        //查询父订单总价
        NewPileTradeListByParentIdResponse parentTrades = newPileTradeProvider.getOrderListByParentId(TradeListByParentIdRequest.builder().parentTid(detail.getParentId()).build()).getContext();

        if (CollectionUtils.isNotEmpty(parentTrades.getTradeVOList())) {
            BigDecimal parentTotalPrice = parentTrades.getTradeVOList().stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                    .stream().map(tp -> tp.getTotalPrice()).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            detail.getTradePrice().setParentTotalPrice(parentTotalPrice);
        }
        //囤货单无拆单情况

        orderUtil.checkUnauthorizedNewPile(tid, tradeGetByIdResponse.getTradeVO());
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        final boolean flag = config.getStatus() == 1;
        int days = JSONObject.parseObject(config.getContext()).getInteger("day");
        NewPileTradeStateVO tradeState = detail.getTradeState();
        boolean canReturnFlag =
                tradeState.getFlowState() == NewPileFlowState.COMPLETED || (tradeState.getPayState() == PayState.PAID
                        && tradeState.getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED && tradeState.getFlowState
                        () != NewPileFlowState.VOID);
        canReturnFlag = orderUtil.isCanReturnTimeNewPile(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(detail.getTradeState().getDeliverStatus())) {
            canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                    .trade(KsBeanUtil.convert(tradeGetByIdResponse.getTradeVO(), TradeDTO.class)).build()).getContext().getCanReturnFlag();
        }
        detail.setCanReturnFlag(canReturnFlag);
        List<TradeItemVO> tradeItems = detail.getTradeItems();
        List<String> skus = new LinkedList<>();
        tradeItems.forEach(item -> {
            skus.clear();
            skus.add(item.getSkuId());
            item.setSharePrice(item.getSplitPrice());
        });
        return BaseResponse.success(detail);
    }

    /**
     * 我的囤货
     */
    @ApiOperation(value = "我的囤货列表")
    @RequestMapping(value = "/myNewPileTrade/{wareId}", method = RequestMethod.GET)
    public BaseResponse<PilePurchaseResponse> getNewPileGoodsNumByCustomer(@PathVariable Long wareId) {
        NewPileGoodsNumByCustomerRequest request = new NewPileGoodsNumByCustomerRequest();
        request.setCustomer(commonUtil.getCustomer());
        request.setWareId(wareId);

        BaseResponse<List<GoodsPickStockResponse>> listBaseResponse = newPileTradeProvider.getNewPileGoodsNumByCustomer(request);

        List<GoodsPickStockResponse> context = listBaseResponse.getContext();

        //以goodsInfoId分组计算尚未提货的商品数量
        Map<String, Long> collectNum = context.stream().collect(
                Collectors.groupingBy(GoodsPickStockResponse::getGoodsInfoId,
                        Collectors.summingLong(GoodsPickStockResponse::getStock))
        );

        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
        goodsInfoRequest.setGoodsInfoIds(new ArrayList<>(collectNum.keySet()));

        GoodsInfoViewByIdsResponse goodsInfosResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

        PilePurchaseResponse purchaseResponse = new PilePurchaseResponse();
        purchaseResponse.setGoodsInfos(new MicroServicePage<GoodsInfoVO>(goodsInfosResponse.getGoodsInfos(),
                new PageRequest(),
                10000));
        //此处devanningId 无用途 为了ios端报错填充
        if (CollectionUtils.isNotEmpty(goodsInfosResponse.getGoodsInfos())) {
            goodsInfosResponse.getGoodsInfos().forEach(var -> {
                var.setBuyCount(collectNum.get(var.getGoodsInfoId()));
                if(Objects.isNull(var.getDevanningId())){
                    var.setDevanningId(1l);
                }
            });
        }
        return BaseResponse.success(purchaseResponse);
    }

    /**
     * 我的囤货
     */
    @ApiOperation(value = "我的囤货数量")
    @RequestMapping(value = "/myNewPileGoodsCount", method = RequestMethod.GET)
    public BaseResponse getNewPileGoodsCountByCustomer() {
        NewPileGoodsNumByCustomerRequest request = new NewPileGoodsNumByCustomerRequest();
        request.setCustomer(commonUtil.getCustomer());

        BaseResponse<List<GoodsPickStockResponse>> listBaseResponse = newPileTradeProvider.getNewPileGoodsNumByCustomer(request);

        List<GoodsPickStockResponse> context = listBaseResponse.getContext();
        if(CollectionUtils.isNotEmpty(context)){
            return BaseResponse.success(context.stream().collect(Collectors.summingLong(GoodsPickStockResponse::getStock)));
        }
        return BaseResponse.success(0);
    }

    /**
     * 我的囤货
     */
    @ApiOperation(value = "我的囤货数量")
    @RequestMapping(value = "/getNewPileGoodsCountByPhone/{tid}", method = RequestMethod.GET)
    public BaseResponse getNewPileGoodsCountByPhone(@PathVariable String customerId) {


        CustomerGetByIdRequest request1 = new CustomerGetByIdRequest();
        request1.setCustomerId(customerId);
        BaseResponse<CustomerGetByIdResponse> customerById = customerQueryProvider.getCustomerById(request1);
        NewPileGoodsNumByCustomerRequest request = new NewPileGoodsNumByCustomerRequest();
        request.setCustomer(customerById.getContext());
        BaseResponse<List<GoodsPickStockResponse>> listBaseResponse = newPileTradeProvider.getNewPileGoodsNumByCustomer(request);

        List<GoodsPickStockResponse> context = listBaseResponse.getContext();
        if(CollectionUtils.isNotEmpty(context)){
            return BaseResponse.success(context.stream().collect(Collectors.summingLong(GoodsPickStockResponse::getStock)));
        }
        return BaseResponse.success(0);
    }

    @ApiOperation(value = "展示订单基本信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/show/{tid}", method = RequestMethod.GET)
    public BaseResponse<NewPileTradeCommitResultVO> commitResp(@PathVariable String tid) {
        NewPileTradeVO trade =
                newPileTradeProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();

        List<String> customerId = new ArrayList<>(20);
        customerId.add(commonUtil.getOperatorId());
        List<ParentCustomerRelaVO> parentCustomerRelaVOList = parentCustomerRelaQueryProvider.findAllByParentId(ParentCustomerRelaListRequest
                .builder().parentId(commonUtil.getOperatorId()).build()).getContext().getParentCustomerRelaVOList();
        if (CollectionUtils.isNotEmpty(parentCustomerRelaVOList)) {
            List<String> child = parentCustomerRelaVOList.stream().map(ParentCustomerRelaVO::getCustomerId).distinct().collect(Collectors.toList());
            customerId.addAll(child);
        }
        if (!customerId.contains(trade.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{tid});
        }

        boolean hasImg = CollectionUtils.isNotEmpty(trade.getTradeItems());
        BigDecimal bigDecimalStream = trade.getTradeItems().stream()
                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(TradeItemVO::getSplitPrice)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        return BaseResponse.success(new NewPileTradeCommitResultVO(tid, trade.getParentId(), trade.getTradeState(),
                trade.getPaymentOrder(),
                bigDecimalStream,
                trade.getOrderTimeOut(),
                trade.getSupplier().getStoreName(),
                trade.getSupplier().getIsSelf(),
                hasImg ? trade.getTradeItems().get(0).getPic() : null));
    }
    /**
     * 我的囤货
     */
    @ApiOperation(value = "我的囤货列表涉及仓库")
    @RequestMapping(value = "/myNewPileTradeWareHose", method = RequestMethod.POST)
    public BaseResponse myNewPileTradeWareHose() {
        NewPileGoodsNumByCustomerRequest request = new NewPileGoodsNumByCustomerRequest();
        request.setCustomer(commonUtil.getCustomer());
        BaseResponse<List<Long>> newPileTradeWareHoseCustomer = newPileTradeProvider.getNewPileTradeWareHoseCustomer(request);
        if (CollectionUtils.isNotEmpty(newPileTradeWareHoseCustomer.getContext())) {
            return wareHouseQueryProvider.list(WareHouseListRequest.builder().wareIdList(newPileTradeWareHoseCustomer.getContext()).build());
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 我的囤货
     */
    @ApiOperation(value = "查询商品是否参与囤货")
    @RequestMapping(value = "/isPileGoodsInfo/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse myNewPileGoods(@PathVariable String goodsInfoId) {
        List<String> context = pileActivityProvider.getStartPileActivityGoodsInfoIds().getContext();
        return BaseResponse.success(context.contains(goodsInfoId));
    }


    /**
     * 我的囤货-多商家分组商品列表
     */
    @ApiOperation(value = "我的囤货列表")
    @RequestMapping(value = "/myNewPileTradeStore/{wareId}", method = RequestMethod.GET)
    public BaseResponse<PilePurchaseStoreResponse> getNewPileGoodsStoreNumByCustomer(@PathVariable Long wareId) {
        NewPileGoodsNumByCustomerRequest request = new NewPileGoodsNumByCustomerRequest();
        request.setCustomer(commonUtil.getCustomer());
        request.setWareId(wareId);

        BaseResponse<List<GoodsPickStockResponse>> listBaseResponse = newPileTradeProvider.getNewPileGoodsNumByCustomer(request);

        List<GoodsPickStockResponse> context = listBaseResponse.getContext();

        //以goodsInfoId分组计算尚未提货的商品数量
        Map<String, Long> collectNum = context.stream().collect(
                Collectors.groupingBy(GoodsPickStockResponse::getGoodsInfoId,
                        Collectors.summingLong(GoodsPickStockResponse::getStock))
        );

        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
        goodsInfoRequest.setGoodsInfoIds(new ArrayList<>(collectNum.keySet()));

        GoodsInfoViewByIdsResponse goodsInfosResponse = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();

        //多商家囤货商品进行分组展示
        List<GoodsStoreGroupVO> groupList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(goodsInfosResponse.getGoodsInfos())){
            //计算囤货商品数量
            goodsInfosResponse.getGoodsInfos().forEach(var -> {
                var.setBuyCount(collectNum.get(var.getGoodsInfoId()));
                if(Objects.isNull(var.getDevanningId())){
                    var.setDevanningId(1l);
                }
            });

            Map<Long, List<GoodsInfoVO>> listMap = goodsInfosResponse.getGoodsInfos().stream().collect(Collectors.groupingBy(GoodsInfoVO::getStoreId));
            for (Map.Entry<Long, List<GoodsInfoVO>> entry : listMap.entrySet()){
                Long storeId = entry.getKey();
                //根据店铺Id查询店铺信息
                StoreByIdRequest storeByIdRequest = StoreByIdRequest.builder().storeId(storeId).build();
                BaseResponse<StoreByIdResponse> storeResponse = storeQueryProvider.getById(storeByIdRequest);
                StoreVO storeVO = storeResponse.getContext().getStoreVO();

                GoodsStoreGroupVO storeGroupResponse = GoodsStoreGroupVO.builder()
                        .storeId(storeId)
                        .storeName(storeVO.getStoreName())
                        .companyType(storeVO.getCompanyType().toValue())
                        .goodsInfos(entry.getValue()).build();
                groupList.add(storeGroupResponse);
            }
        }

        return BaseResponse.success(PilePurchaseStoreResponse.builder().storeGoodsList(groupList).build());
    }
}
