package com.wanmi.sbc.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseVO;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.request.PaymentRecordRequest;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.LonLatUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.coupon.service.CouponService;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoVerifyRequest;
import com.wanmi.sbc.goods.api.request.flashsalegoods.IsSecKillRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAndStockListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoStockByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoStockRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.info.GetGoodsInfoStockByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoMarketingVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.FlashSaleGoodsVO;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoSimpleVo;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GrouponGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.distributionrecord.DistributionRecordQueryProvider;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.BuyGoodsOrFullOrderSendCouponRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeListForUseByCustomerIdRequest;
import com.wanmi.sbc.marketing.api.request.distributionrecord.DistributionRecordListRequest;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityFreeDeliveryByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCardGroupRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.coupon.BuyGoodsOrFullOrderSendCouponResponse;
import com.wanmi.sbc.marketing.api.response.distributionrecord.DistributionRecordListResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingByGoodsInfoIdAndIdResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingGetByIdForCustomerResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingGroupCardResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingQueryByIdsResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.enums.CommissionReceived;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.GiftType;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.enums.RecruitApplyType;
import com.wanmi.sbc.marketing.bean.vo.CoinGoodsVo;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.DistributionRecordVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingForEndVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftDetailVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingPurchaseLimitVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.order.api.provider.groupon.GrouponProvider;
import com.wanmi.sbc.order.api.provider.historyTownShipOrder.HistoryTownShipOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnPileOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.suit.SuitOrderTempProvider;
import com.wanmi.sbc.order.api.provider.trade.GrouponInstanceQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.ProviderTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeItemProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeItemQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.VerifyQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeItemQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.groupon.GrouponOrderValidRequest;
import com.wanmi.sbc.order.api.request.historytownshiporder.HistoryTownShipOrderStockRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderGroupRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderListRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnCountByConditionRequest;
import com.wanmi.sbc.order.api.request.suit.SuitOrderTempQueryRequest;
import com.wanmi.sbc.order.api.request.trade.FindProviderTradeRequest;
import com.wanmi.sbc.order.api.request.trade.GrouponInstanceByGrouponNoRequest;
import com.wanmi.sbc.order.api.request.trade.MergeGoodsInfoRequest;
import com.wanmi.sbc.order.api.request.trade.TradeAddReceivableRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCanReturnNumRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCancelRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCheckRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCommitRequest;
import com.wanmi.sbc.order.api.request.trade.TradeConfirmReceiveRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCountCriteriaRequest;
import com.wanmi.sbc.order.api.request.trade.TradeDefaultPayRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByParentRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetGoodsRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemByCustomerIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemSnapshotRequest;
import com.wanmi.sbc.order.api.request.trade.TradeItemStockOutRequest;
import com.wanmi.sbc.order.api.request.trade.TradeListByParentIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradePageCriteriaRequest;
import com.wanmi.sbc.order.api.request.trade.TradePageQueryRequest;
import com.wanmi.sbc.order.api.request.trade.TradeParamsRequest;
import com.wanmi.sbc.order.api.request.trade.TradeParamsRequestForApp;
import com.wanmi.sbc.order.api.request.trade.TradePushKingdeeOrderRequest;
import com.wanmi.sbc.order.api.request.trade.TradeQueryPurchaseInfoRequest;
import com.wanmi.sbc.order.api.request.trade.VerifyGoodsRequest;
import com.wanmi.sbc.order.api.request.trade.VerifyStoreRequest;
import com.wanmi.sbc.order.api.request.trade.VerifyTradeMarketingRequest;
import com.wanmi.sbc.order.api.response.discount.DiscountPriceDetailResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderListResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponseWrapper;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseGetGoodsMarketingResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseQueryResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.order.api.response.trade.FindProviderTradeResponse;
import com.wanmi.sbc.order.api.response.trade.TradeCheckResponse;
import com.wanmi.sbc.order.api.response.trade.TradeDeliverRecordResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetByIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetFreightResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetGoodsResponse;
import com.wanmi.sbc.order.api.response.trade.TradeItemByCustomerIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeItemStockOutResponse;
import com.wanmi.sbc.order.api.response.trade.TradeListByParentIdResponse;
import com.wanmi.sbc.order.api.response.trade.VerifyGoodsResponse;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.IsAccountStatus;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.request.CheckTradeRequest;
import com.wanmi.sbc.order.request.DeleteZeroStockRequest;
import com.wanmi.sbc.order.request.DeliveryHomeFlagRequest;
import com.wanmi.sbc.order.request.GrouponBuyRequest;
import com.wanmi.sbc.order.request.ImmediateBuyNewRequest;
import com.wanmi.sbc.order.request.ImmediateBuyRequest;
import com.wanmi.sbc.order.request.StoreBagsBuyRequest;
import com.wanmi.sbc.order.request.SuitBuyRequest;
import com.wanmi.sbc.order.request.TradeDetailsRequest;
import com.wanmi.sbc.order.request.TradeItemConfirmRequest;
import com.wanmi.sbc.order.request.TradeItemConfirmWareHouseRequest;
import com.wanmi.sbc.order.request.TradeItemRequest;
import com.wanmi.sbc.order.request.TradeParentTidRequest;
import com.wanmi.sbc.order.response.CheckTradeResponse;
import com.wanmi.sbc.order.response.DeliveryHomeFlagResponse;
import com.wanmi.sbc.order.response.OrderTodoResp;
import com.wanmi.sbc.order.response.TakeTradeConfirmResponse;
import com.wanmi.sbc.order.response.TradeConfirmResponse;
import com.wanmi.sbc.order.service.DeliveryhomeCfgHubei;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.DeliveryQueryProvider;
import com.wanmi.sbc.setting.api.provider.gatherboxset.GatherBoxSetProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.DeliveryQueryRequest;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetInfoResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackListParam;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackMapListResp;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackMapResp;
import com.wanmi.sbc.setting.api.response.logisticstrail.QueryTrackParam;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
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

/**
 * <p>订单公共Controller</p>
 * Created by of628-wenzhi on 2017-07-10-下午4:12.
 */
@Api(tags = "TradeBaseController", description = "订单公共服务API")
@RestController
@RequestMapping("/trade")
@Slf4j
@Validated
public class TradeBaseController {

    public static final String MARKET_NAME_SUFFIX = "批发市场";
    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;

    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private PurchaseQueryProvider purchaseQueryProvider;

    @Autowired
    private PurchaseProvider purchaseProvider;


    @Autowired
    private ShopCartQueryProvider shopCartQueryProvider;

    @Autowired
    private RetailShopCartQueryProvider retailShopCartQueryProvider;

    @Autowired
    private TradeItemProvider tradeItemProvider;

    @Autowired
    private NewPileTradeItemQueryProvider newPileTradeItemQueryProvider;

    @Autowired
    private TradeItemQueryProvider tradeItemQueryProvider;

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
    private DistributionRecordQueryProvider distributionRecordQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private HistoryTownShipOrderQueryProvider historyTownShipOrderQueryProvider;

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
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private SuitOrderTempProvider suitOrderTempProvider;

    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private ShopCartProvider shopCartProvider;

    @Autowired
    private BulkShopCartQueryProvider bulkShopCartQueryProvider;

    @Autowired
    private OrderUtil orderUtil;

    @Autowired
    private GatherBoxSetProvider gatherBoxSetProvider;
    @Autowired
    private CoinActivityProvider coinActivityProvider;
    @Autowired
    private DeliveryhomeCfgHubei deliveryhomeCfgHubei;

    @Autowired
    private CouponService couponService;
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    /**
     * 查询订单详情
     */
    @ApiOperation(value = "查询订单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> details(@PathVariable String tid) {
        TradeGetByIdResponse tradeGetByIdResponse =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext();
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        TradeVO detail = tradeGetByIdResponse.getTradeVO();

        //查询父订单总价
        TradeListByParentIdResponse parentTrades = tradeQueryProvider.getOrderListByParentId(TradeListByParentIdRequest.builder().parentTid(detail.getParentId()).build()).getContext();

       /* if (CollectionUtils.isNotEmpty(parentTrades.getTradeVOList())) {
            BigDecimal parentTotalPrice = parentTrades.getTradeVOList().stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                    .stream().map(tp -> tp.getTotalPrice()).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            detail.getTradePrice().setParentTotalPrice(parentTotalPrice);
        }*/
        if (CollectionUtils.isNotEmpty(parentTrades.getTradeVOList())) {
            List<TradeItemVO> tradeItems = new ArrayList<>();
            parentTrades.getTradeVOList().forEach(var->{
                tradeItems.addAll(var.getTradeItems());
            });
            BigDecimal parentTotalPrice = tradeItems.stream()
                    .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                    .map(TradeItemVO::getSplitPrice)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

            parentTotalPrice = parentTotalPrice.add(parentTrades.getTradeVOList().stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                    .stream().map(tp -> tp.getDeliveryPrice()).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add));

            parentTotalPrice = parentTotalPrice.add(parentTrades.getTradeVOList().stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                    .stream().map(tp -> tp.getPackingPrice()).collect(Collectors.toList())
                    .stream().reduce(BigDecimal.ZERO, BigDecimal::add));

            detail.getTradePrice().setParentTotalPrice(parentTotalPrice);
        }


        orderUtil.checkUnauthorized(tid, tradeGetByIdResponse.getTradeVO());
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
        canReturnFlag = orderUtil.isCanReturnTime(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        if (canReturnFlag && !DeliverStatus.NOT_YET_SHIPPED.equals(detail.getTradeState().getDeliverStatus())) {
            canReturnFlag = returnOrderQueryProvider.canReturnFlag(TradeCanReturnNumRequest.builder()
                    .trade(KsBeanUtil.convert(tradeGetByIdResponse.getTradeVO(), TradeDTO.class)).build()).getContext().getCanReturnFlag();
        }
        detail.setCanReturnFlag(canReturnFlag);
        if(DeliverWay.isDeliveryToStore(detail.getDeliverWay()) && detail.getConsignee().getVillageFlag() && PayState.isPaid(detail.getTradeState().getPayState())){
           BigDecimal villageAddDelivery = tradeQueryProvider.findVillageAddDeliveryByTradeId(detail.getId()).getContext();
           if(detail.getTradePrice().getDeliveryPrice()!=null) {
               villageAddDelivery = villageAddDelivery.add(detail.getTradePrice().getDeliveryPrice());
           }
            detail.getTradePrice().setDeliveryPrice(villageAddDelivery);
        }

        //计算订单商品分摊金额
//        BigDecimal goodsRealPrice = detail.getTradePrice().getTotalPrice().subtract(detail.getTradePrice().getDeliveryPrice());
//        BigDecimal goodsPrice = detail.getTradePrice().getGoodsPrice();
//
//        BigDecimal countSharePrice = BigDecimal.ZERO;
        List<TradeItemVO> tradeItems = detail.getTradeItems();
        List<String> skus = new LinkedList<>();
        //            if (i == tradeItems.size() - 1) {
        //                item.setSharePrice(goodsRealPrice.subtract(countSharePrice));
        //                continue;
        //            }
        //            //商品金额 / 商品总金额
        //            BigDecimal scale = scale(item.getPrice().multiply(new BigDecimal(item.getNum())), goodsPrice);
        //            BigDecimal sharePrice = scale.multiply(goodsRealPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
        //  countSharePrice = countSharePrice.add(item.getSharePrice());
        tradeItems.forEach(item -> {
            skus.clear();
            skus.add(item.getSkuId());
//            Optional<DevanningGoodsInfoVO> first = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(skus).build()).getContext().getDevanningGoodsInfoVOS()
//                    .stream().findFirst();
//            first.ifPresent(devanningGoodsInfoVO -> {
//                item.setDevanningId(devanningGoodsInfoVO.getDevanningId());
//                item.setAddStep(devanningGoodsInfoVO.getAddStep());
//                item.setDivisorFlag(devanningGoodsInfoVO.getDivisorFlag());
//            });
            item.setSharePrice(item.getSplitPrice());
        });
        return BaseResponse.success(detail);
    }

    /**
     * 查询订单详情
     */
    @ApiOperation(value = "聚合查询查询订单详情")
    @RequestMapping(value = "/tids", method = RequestMethod.POST)
    public BaseResponse<List<TradePriceWrapperVO>> detailsList(@RequestBody TradeDetailsRequest tradeDetailsRequest) {

        List<String> tids = tradeDetailsRequest.getTids();
        List<TradePriceWrapperVO> result = Lists.newArrayList();

        for (String tid : tids){
            TradeGetByIdResponse tradeGetByIdResponse = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext();
            if (tradeGetByIdResponse == null || tradeGetByIdResponse.getTradeVO() == null) {
                continue;
            }
            commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
            TradeVO detail = tradeGetByIdResponse.getTradeVO();

            TradePriceWrapperVO build = TradePriceWrapperVO.builder()
                    .id(detail.getId())
                    .tradePriceVO(detail.getTradePrice())
                    .build();
            result.add(build);
        }
        return BaseResponse.success(result);
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
                tradeQueryProvider.getByParent(TradeGetByParentRequest.builder().parentId(parentId).build()).getContext();
        if (tradeGetByIdResponse == null
                || tradeGetByIdResponse.getTradeVO() == null) {
            return BaseResponse.success(null);
        }
        commonUtil.checkIfStore(tradeGetByIdResponse.getTradeVO().getSupplier().getStoreId());
        TradeVO detail = tradeGetByIdResponse.getTradeVO();
        orderUtil.checkUnauthorized(parentId, tradeGetByIdResponse.getTradeVO());
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
        canReturnFlag = orderUtil.isCanReturnTime(flag, days, tradeState, canReturnFlag);
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
            tradeGetByIdResponse = tradeQueryProvider.getByParent(TradeGetByParentRequest.builder().
                    parentId(tradeParentTidRequest.getParentId()).build()).getContext();
        } else if (StringUtils.isNotBlank(tradeParentTidRequest.getTId())) {
            tradeGetByIdResponse =
                    tradeQueryProvider.getById(TradeGetByIdRequest.builder().
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
        canReturnFlag = orderUtil.isCanReturnTime(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        detail.setCanReturnFlag(canReturnFlag);
        return BaseResponse.success(detail);
    }

    /**
     * B店主客户订单详情
     */
    @ApiOperation(value = "B店主客户订单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/distribute/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> distributeDetails(@PathVariable String tid) {
        TradeGetByIdResponse tradeGetByIdResponse =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext();
        TradeVO detail = tradeGetByIdResponse.getTradeVO();
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
        canReturnFlag = orderUtil.isCanReturnTime(flag, days, tradeState, canReturnFlag);
        //开店礼包不支持退货退款
        canReturnFlag = canReturnFlag && DefaultFlag.NO == detail.getStoreBagsFlag();
        detail.setCanReturnFlag(canReturnFlag);
        detail.getConsignee().setPhone(detail.getConsignee().getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})",
                "$1****$2"));
        detail.getConsignee().setDetailAddress(detail.getConsignee().getDetailAddress().replace(detail.getConsignee()
                .getAddress(), "********"));

        //查询商品的入账状态
        DistributionRecordListRequest distributionRecordListRequest = DistributionRecordListRequest
                .builder()
                .tradeId(detail.getId())
                .build();
        BaseResponse<DistributionRecordListResponse> response = distributionRecordQueryProvider.list
                (distributionRecordListRequest);
        if (response != null && response.getContext() != null && CollectionUtils.isNotEmpty(response.getContext()
                .getDistributionRecordVOList())) {
            List<DistributionRecordVO> distributionRecordVOList = response.getContext().getDistributionRecordVOList();
            detail.getTradeItems().stream().forEach(tradeItemVO -> {
                distributionRecordVOList.stream()
                        .filter(distributionRecordVO -> distributionRecordVO.getGoodsInfoId().equals(tradeItemVO
                                .getSkuId()))
                        .forEach(distributionRecordVO -> {
                            if (distributionRecordVO.getDeleteFlag().equals(DeleteFlag.YES)) {
                                tradeItemVO.setIsAccountStatus(IsAccountStatus.FAIL);
                            } else if (distributionRecordVO.getCommissionState().equals(CommissionReceived.RECEIVED)) {
                                tradeItemVO.setIsAccountStatus(IsAccountStatus.YES);
                            } else {
                                tradeItemVO.setIsAccountStatus(IsAccountStatus.NO);
                            }
                        });
            });
        }


        //处理所有的分销奖励
//        detail.getTradeItems().forEach(tradeItemVO -> {
//            tradeItemVO.setDistributionCommission(tradeItemVO.getDistributionCommission().multiply(new BigDecimal
// (tradeItemVO.getNum())));
//        });
        return BaseResponse.success(detail);
    }

    /**
     * 从采购单中确认订单商品
     */
    @ApiOperation(value = "从采购单中确认订单商品")
    @RequestMapping(value = "/takeGoodConfirm", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse takeGoodConfirm(@RequestBody @Valid TradeItemConfirmRequest confirmRequest) {
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
//        purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        PurchaseQueryResponse purchaseQueryResponse = purchaseQueryProvider.pileQuery(purchaseQueryRequest).getContext();
        List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();
        if (CollectionUtils.isEmpty(exsistSku)) {
            throw new SbcRuntimeException("K-050205");
        }
        List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
        log.info("TradeBaseController.takeGoodConfirm existIds:{}", JSONObject.toJSONString(existIds));
        if (skuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
            throw new SbcRuntimeException("K-050205");
        }
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        GoodsInfoResponse response = orderUtil.getGoodsResponseNew(skuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
        //商品提货验证
        verifyQueryProvider.verifyTakeGoods(new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true, customerId));
        verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
        //营销活动校验
//        List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
//                (), Collections.emptyList(), tradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
        //商品指定区域销售
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        response.getGoodsInfos().forEach(goodsInfoVO -> {
            if (Objects.nonNull(deliveryAddress) && Objects.nonNull(goodsInfoVO.getAllowedPurchaseArea()) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
                List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, goodsInfoVO.getGoodsInfoName() + ",不在销售区请修改当前收货地址或联系客服！");
                }
            }
        });

        return tradeItemProvider.takeSnapshot(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems
                (tradeItems)
//                .tradeMarketingList(tradeMarketingList)
                .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }

    /**
     * 校验是否满足赠券条件
     *
     * @param confirmRequest
     * @return
     */
    @ApiOperation(value = "校验是否满足赠券条件")
    @RequestMapping(value = "/confirm/check/send/coupon", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<BuyGoodsOrFullOrderSendCouponResponse> checkSendCoupon(@RequestBody @Valid TradeItemConfirmRequest confirmRequest, HttpServletRequest httpServletRequest) {
        Long wareId = commonUtil.getWareId(httpServletRequest);
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000001");
        }
        BuyGoodsOrFullOrderSendCouponRequest request = new BuyGoodsOrFullOrderSendCouponRequest();
        request.setCustomerId(customerId);
        request.setType(CouponActivityType.BUY_ASSIGN_GOODS_COUPON);
        request.setWareId(wareId);
        request.setTradeItemInfoDTOS(KsBeanUtil.convert(confirmRequest.getTradeItems(), TradeItemInfoDTO.class));
        BuyGoodsOrFullOrderSendCouponResponse response = couponActivityQueryProvider.checkSendCoupon(request).getContext();
        List<CoinGoodsVo> coinGoodsVos = coinActivityProvider.checkSendCoin(request).getContext();
        if (CollectionUtils.isNotEmpty(coinGoodsVos)) {
            if (Objects.isNull(response)) {
                response = new BuyGoodsOrFullOrderSendCouponResponse();
            }
            List<String> ids = coinGoodsVos.stream().map(CoinGoodsVo::getGoodsInfoId).collect(Collectors.toList());
            // 图片 商品名称 单位
            Map<String, GoodsInfoSimpleVo> infoVOMap = goodsInfoQueryProvider.findGoodsInfoSimpleVoBySkuIds(ids).getContext().stream().collect(Collectors.toMap(GoodsInfoSimpleVo::getGoodsInfoId, Function.identity(), (o1, o2) -> o1));
            for (CoinGoodsVo coinGoodsVo : coinGoodsVos) {
                if (Objects.nonNull(infoVOMap.get(coinGoodsVo.getGoodsInfoId()))) {
                    GoodsInfoSimpleVo goodsInfoVO = infoVOMap.get(coinGoodsVo.getGoodsInfoId());
                    coinGoodsVo.setGoodsName(goodsInfoVO.getGoodsInfoName());
                    coinGoodsVo.setUnit(goodsInfoVO.getGoodsSubtitle());
                    coinGoodsVo.setPic(goodsInfoVO.getGoodsImage());
                }
            }
            response.setIsMeetSendCoin(true);
            response.setCoinGoodsVos(coinGoodsVos);
        }
        return BaseResponse.success(response);
    }

    /**
     * 从采购单中确认订单商品
     */
    @ApiOperation(value = "从采购单中确认订单商品")
    @RequestMapping(value = "/confirm", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse confirm(@RequestBody @Valid TradeItemConfirmRequest confirmRequest) throws Exception {
        //加锁，防止同一用户在两个手机上同时下单，导致数据错误， 不用用户id去加错
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000001");
        }
        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            log.info("TradeBaseController.confirm lock:{}", customerId);
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
            PurchaseQueryResponse purchaseQueryResponse = shopCartQueryProvider.query(purchaseQueryRequest).getContext();
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
            GoodsInfoResponse response = orderUtil.getGoodsResponseNew(skuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
//        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos();
            //根据开关重新设置分销商品标识
//        distributionService.checkDistributionSwitch(goodsInfoVOList);
            //社交分销业务
//        Purchase4DistributionRequest purchase4DistributionRequest = new Purchase4DistributionRequest();
//        purchase4DistributionRequest.setGoodsInfos(response.getGoodsInfos());
//        purchase4DistributionRequest.setGoodsIntervalPrices(response.getGoodsIntervalPrices());
//        purchase4DistributionRequest.setCustomer(customer);
//        purchase4DistributionRequest.setDistributeChannel(commonUtil.getDistributeChannel());
//        Purchase4DistributionResponse purchase4DistributionResponse = purchaseQueryProvider.distribution
//                (purchase4DistributionRequest).getContext();
//        response.setGoodsInfos(purchase4DistributionResponse.getGoodsInfos());
//        response.setGoodsIntervalPrices(purchase4DistributionResponse.getGoodsIntervalPrices());
//        //验证分销商品状态
//        validShopGoods(purchase4DistributionResponse.getGoodsInfos());

            //商品验证
            verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                    KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true, customerId));
            verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                    (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
            //营销活动校验
            List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
                    (), Collections.emptyList(), tradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();

            //限定区域指定商品限购校验
            CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
            Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
            response.getGoodsInfos().forEach(goodsInfoVO -> {
                if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())) {
                    List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())) {
                        if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))) {
                            log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}", tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                            if (tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()) {
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品：" + goodsInfoVO.getGoodsInfoName() + "，单笔订单超过限购数量：" + goodsInfoVO.getSingleOrderPurchaseNum() + "件/箱，请修改此商品购买数量！");
                            }
                        }
                    }
                }
            });

            return tradeItemProvider.snapshot(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems
                    (tradeItems)
                    .tradeMarketingList(tradeMarketingList)
                    .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
        } catch (SbcRuntimeException e) {
//            log.error("TradeBaseController.confirm error:{}",e.getMessage());
            throw new SbcRuntimeException(e.getErrorCode(), e.getResult());
        } finally {
            log.info("TradeBaseController.confirm unlock:{}", customerId);
            rLock.unlock();
        }
    }

    /**
     * 从采购单中确认订单商品（零售+批发）
     */
    @ApiOperation(value = "从采购单中确认订单商品")
    @RequestMapping(value = "/confirm-all", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse confirmAll(@RequestBody @Valid TradeItemConfirmRequest confirmRequest) throws Exception {
        //加锁，防止同一用户在两个手机上同时下单，导致数据错误， 不用用户id去加错
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)) {
            throw new SbcRuntimeException("K-000001");
        }
        if (CollectionUtils.isEmpty(confirmRequest.getTradeItems()) && CollectionUtils.isEmpty(confirmRequest.getRetailTradeItems())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单商品信息为空！");
        }
        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            log.info("TradeBaseController.confirm lock:{}", customerId);
            List<TradeItemDTO> tradeItems = Collections.emptyList();
            List<TradeItemDTO> retailTradeItems = Collections.emptyList();
            List<GoodsInfoDTO> skuList = Collections.emptyList();
            List<GoodsInfoDTO> retailSkuList = Collections.emptyList();
            WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
            //验证用户
            CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customerId)).getContext();
            if (CollectionUtils.isNotEmpty(confirmRequest.getTradeItems())) {
                tradeItems = confirmRequest.getTradeItems().stream().map(
                        o -> TradeItemDTO.builder().skuId(o.getSkuId()).erpSkuNo(o.getErpSkuNo()).num(o.getNum()).build()
                ).collect(Collectors.toList());
                //验证采购单
                List<String> skuIds =
                        confirmRequest.getTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setGoodsInfoIds(skuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = shopCartQueryProvider.query(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
                if (skuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }
                GoodsInfoResponse response = orderUtil.getGoodsResponseNew(skuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
                verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true, customerId));
                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                //营销活动校验
                List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
                        (), Collections.emptyList(), tradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
                confirmRequest.setTradeMarketingList(tradeMarketingList);
                skuList = KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class);
                //限定区域指定商品限购校验
                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())) {
                        List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())) {
                            if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))) {
                                log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}", tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                                if (tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()) {
                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品：" + goodsInfoVO.getGoodsInfoName() + "，单笔订单超过限购数量：" + goodsInfoVO.getSingleOrderPurchaseNum() + "件/箱，请修改此商品购买数量！");
                                }
                            }
                        }
                    }
                });
            }

            if (CollectionUtils.isNotEmpty(confirmRequest.getRetailTradeItems())) {
                retailTradeItems = confirmRequest.getRetailTradeItems().stream().map(
                        o -> TradeItemDTO.builder().skuId(o.getSkuId()).erpSkuNo(o.getErpSkuNo()).num(o.getNum()).build()
                ).collect(Collectors.toList());
                //验证采购单
                List<String> retailSkuIds =
                        confirmRequest.getRetailTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setGoodsInfoIds(retailSkuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = retailShopCartQueryProvider.query(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
                if (retailSkuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }
                GoodsInfoResponse response = orderUtil.getGoodsRetailResponseNew(retailSkuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
                verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(retailTradeItems, Collections.emptyList(),
                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true, customerId));
                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                //营销活动校验
                List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
                        (), Collections.emptyList(), retailTradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
                confirmRequest.setRetailTradeMarketingList(tradeMarketingList);
                retailSkuList = KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class);
                //限定区域指定商品限购校验
                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                Map<String, Long> tradeItemMap = retailTradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())) {
                        List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())) {
                            if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))) {
                                log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}", tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                                if (tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()) {
                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品：" + goodsInfoVO.getGoodsInfoName() + "，单笔订单超过限购数量：" + goodsInfoVO.getSingleOrderPurchaseNum() + "件/箱，请修改此商品购买数量！");
                                }
                            }
                        }
                    }
                });
            }


//        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos();
            //根据开关重新设置分销商品标识
//        distributionService.checkDistributionSwitch(goodsInfoVOList);
            //社交分销业务
//        Purchase4DistributionRequest purchase4DistributionRequest = new Purchase4DistributionRequest();
//        purchase4DistributionRequest.setGoodsInfos(response.getGoodsInfos());
//        purchase4DistributionRequest.setGoodsIntervalPrices(response.getGoodsIntervalPrices());
//        purchase4DistributionRequest.setCustomer(customer);
//        purchase4DistributionRequest.setDistributeChannel(commonUtil.getDistributeChannel());
//        Purchase4DistributionResponse purchase4DistributionResponse = purchaseQueryProvider.distribution
//                (purchase4DistributionRequest).getContext();
//        response.setGoodsInfos(purchase4DistributionResponse.getGoodsInfos());
//        response.setGoodsIntervalPrices(purchase4DistributionResponse.getGoodsIntervalPrices());
//        //验证分销商品状态
//        validShopGoods(purchase4DistributionResponse.getGoodsInfos());


            return tradeItemProvider.snapshotRetail(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems(tradeItems)
                    .tradeMarketingList(confirmRequest.getTradeMarketingList())
                    .skuList(skuList).retailTradeItems(retailTradeItems)
                    .retailtTradeMarketingList(confirmRequest.getRetailTradeMarketingList())
                    .retailSkuList(retailSkuList).build());
        } catch (SbcRuntimeException e) {
//            log.error("TradeBaseController.confirm error:{}",e.getMessage());
            throw new SbcRuntimeException(e.getErrorCode(), e.getResult());
        } finally {
            log.info("TradeBaseController.confirm unlock:{}", customerId);
            rLock.unlock();
        }
    }


    /**
     * 从采购单中确认订单商品（零售+批发(拆箱)）
     */
    @ApiOperation(value = "从采购单中确认订单商品")
    @RequestMapping(value = "/devanning-confirm-all", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse devanningConfirmAll(@RequestBody @Valid TradeItemConfirmRequest confirmRequest, HttpServletRequest request) throws Exception  {
        StopWatch stopWatch = new StopWatch("生成快照接口时间优化");
        stopWatch.start("check操作");
        //加锁，防止同一用户在两个手机上同时下单，导致数据错误， 不用用户id去加错
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)){
            throw new SbcRuntimeException("K-000001");
        }
        if (CollectionUtils.isEmpty(confirmRequest.getTradeItems()) && CollectionUtils.isEmpty(confirmRequest.getRetailTradeItems()) &&
        CollectionUtils.isEmpty(confirmRequest.getBulkTradeItems())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单商品信息为空！");
        }
        confirmRequest.getTradeItems().forEach(tradeItemRequest -> {

            if (Objects.isNull(tradeItemRequest.getDevanningskuId())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"" +
                        "");
            }
        });
        stopWatch.stop();
        /**
         * 这个代码是前段上线有问题 后端强行修正
         */
        stopWatch.start("修正前端逻辑");
        if (CollectionUtils.isNotEmpty(confirmRequest.getTradeItems())){
            Map<Long, TradeItemRequest> collect2 = confirmRequest.getTradeItems().stream()
                    .collect(Collectors.toMap(TradeItemRequest::getDevanningskuId, Function.identity(), (a, b) -> a));
            confirmRequest.setTradeItems(new ArrayList<>(collect2.values()));
        }
        if (CollectionUtils.isNotEmpty(confirmRequest.getRetailTradeItems())){
            Map<String, TradeItemRequest> collect = confirmRequest.getRetailTradeItems().stream()
                    .collect(Collectors.toMap(TradeItemRequest::getSkuId, Function.identity(), (a, b) -> a));
            confirmRequest.setRetailTradeItems(new ArrayList<>(collect.values()));
        }
        stopWatch.stop();


        String key = String.format("devanning_confirm:%s",customerId);
        RLock rLock = redissonClient.getFairLock(key);
        rLock.lock();
        try {
            Long wareId = commonUtil.getWareId(request);
            confirmRequest.setWareId(wareId);
            log.info("TradeBaseController.confirm lock:{}",customerId);
            List<TradeItemDTO> tradeItems = Collections.emptyList();
            List<TradeItemDTO> retailTradeItems = Collections.emptyList();
            List<TradeItemDTO> bulkTradeItems = Collections.emptyList();

            List<GoodsInfoDTO> skuList = Collections.emptyList();
            List<GoodsInfoDTO> retailSkuList = Collections.emptyList();
            List<GoodsInfoDTO> bulkSkuList = Collections.emptyList();
            WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
            //验证用户
            CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customerId)).getContext();
//            log.info("===============//批发:{}",JSONObject.toJSONString(confirmRequest.getTradeItems()));
            if (CollectionUtils.isNotEmpty(confirmRequest.getTradeItems())) {
                stopWatch.start("批发商品处理");
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

                Map<Long, DevanningGoodsInfoVO> devanningMaps = devanningGoodsInfoQueryProvider.getInfoByIds(DevanningGoodsInfoByIdRequest
                        .builder().devanningIds(tradeItems.stream().map(TradeItemDTO::getDevanningId).collect(Collectors.toList()))
                        .build()).getContext()
                        .getDevanningGoodsInfoVOS().stream()
                        .collect(Collectors.toMap(DevanningGoodsInfoVO::getDevanningId, Function.identity()));

                stopWatch.stop();
                stopWatch.start("赋值操作");
                tradeItems.forEach(v->{
                    DevanningGoodsInfoVO devanningInfoVO=  devanningMaps.get(v.getDevanningId());
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
                stopWatch.stop();
//                log.info("buyNumMaps========  {}", JSONObject.toJSONString(buyNumMaps));
                stopWatch.start("验证采购单");
                //验证采购单
                List<Long> skuIds =
                        confirmRequest.getTradeItems().stream().map(TradeItemRequest::getDevanningskuId).collect(Collectors.toList());
                PurchaseQueryCacheRequest purchaseQueryRequest = new PurchaseQueryCacheRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setDevaningId(skuIds);
                purchaseQueryRequest.setWareId(wareId);
                PurchaseQueryResponse purchaseQueryResponse = shopCartQueryProvider.queryShopCarExit(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<Long> existIds = exsistSku.stream().map(PurchaseVO::getDevanningId).collect(Collectors.toList());
                if (skuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }

//                log.info("===============confirmRequest:{}",JSONObject.toJSONString(confirmRequest));
                stopWatch.stop();
                stopWatch.start("获取区间价会员价");
                List<Long> devanning = confirmRequest.getTradeItems().stream().map(TradeItemRequest::getDevanningskuId).collect(Collectors.toList());
                GoodsInfoResponse response = orderUtil.getDevanningGoodsResponseNew(devanning, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
//                verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
//                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true,customerId));
//                log.info("===============confirmresponse:{}",JSONObject.toJSONString(response));

                buyNumMaps.forEach((k,v)->{
                    GoodsInfoVO goodsInfoVO = response.getGoodsInfos().stream().filter(f -> k.equals(f.getGoodsInfoId())).findFirst().orElse(null);
//                    log.info("goodsInfoVOresponse========= {}", JSONObject.toJSONString(response));
                    if(v.compareTo(goodsInfoVO.getStock()) == 1){
                        throw new SbcRuntimeException("K-050137");
                    }
                });
                stopWatch.stop();
                // 2.填充商品区间价
                stopWatch.start("校验购买商品,计算商品价格");
                tradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);
                stopWatch.stop();
                stopWatch.start("验证店铺");
                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                stopWatch.stop();
                //营销活动是否生效校验
                stopWatch.start("校验营销活动是否生效生产参数");
                MarketingEffectiveRequest redis = MarketingEffectiveRequest.builder()
                        .tradeMarketingList(confirmRequest.getTradeMarketingList())
                        .wareId(wareId).type("redis")
                        .tradeItems(KsBeanUtil.convert(tradeItems, TradeItemInfoDTO.class)).build();
                stopWatch.stop();
                stopWatch.start("校验营销活动是否生效请求数据");
                MarketingByGoodsInfoIdAndIdResponse context = marketingQueryProvider.getEffectiveMarketingByIdsAndGoods(redis).getContext();
                List<TradeMarketingDTO> tradeMarketingList = context.getTradeMarketingList();

                // TODO 过滤已达到营销限购数量的活动，优化好再上
                // tradeMarketingList = this.filterOverPurchuseLimit(tradeMarketingList, buyNumMaps,customerId);

                confirmRequest.setTradeMarketingList(tradeMarketingList);
                stopWatch.stop();
                stopWatch.start("异常操作");
                if (CollectionUtils.isNotEmpty(context.getRemovemarketinglist()) || CollectionUtils.isNotEmpty(context.getRemovelist())){
                    shopCartProvider.delFirstSnapShopAndMarkeing(customerId);
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
                stopWatch.stop();
//                List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
//                        (), Collections.emptyList(), tradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
                stopWatch.start("限定区域指定商品限购校验");
                skuList = KsBeanUtil.convertList(response.getGoodsInfos(),GoodsInfoDTO.class);
                //限定区域指定商品限购校验
                List<TradeMarketingDTO> tradeMarketingList1 = confirmRequest.getTradeMarketingList();
                //需要数据类型map  sku,marketingid
                Map<Long, List<TradeMarketingDTO>> collect = tradeMarketingList1.stream().collect(Collectors.groupingBy(TradeMarketingDTO::getMarketingId));
                Map<Long, List<List<String>>> collect1 = collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().stream().map(TradeMarketingDTO::getSkuIds).collect(Collectors.toList())));

                for(Map.Entry<Long, List<List<String>>> a:collect1.entrySet()){
                    List<List<String>> value = a.getValue();
                }
                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                List<DevanningGoodsInfoMarketingVO> requestparam= new LinkedList<>();
                for (TradeItemDTO tradeItemDTO:tradeItems){
                    DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO = new DevanningGoodsInfoMarketingVO();
                    devanningGoodsInfoMarketingVO.setDevanningId(tradeItemDTO.getDevanningId());
                    devanningGoodsInfoMarketingVO.setBuyCount(tradeItemDTO.getNum());
                    devanningGoodsInfoMarketingVO.setDivisorFlag(tradeItemDTO.getDivisorFlag());
                    devanningGoodsInfoMarketingVO.setGoodsInfoId(tradeItemDTO.getSkuId());
                    devanningGoodsInfoMarketingVO.setSaleType(0);
                    requestparam.add(devanningGoodsInfoMarketingVO);
                }
//                log.info("checkStockPurchase====:{}",JSON.toJSONString(requestparam));
                //区域限购营销限购校验
                StockAndPureChainNodeRsponse context1 = purchaseProvider.checkStockPurchase(StockAndPureChainNodeRequeest.builder()
                        .wareId(wareId)
                        .provinceId(deliveryAddress.getProvinceId())
                        .cityId(deliveryAddress.getCityId())
                        .checkPure(requestparam).build()).getContext();
                if (Objects.nonNull(context1) && CollectionUtils.isNotEmpty(context1.getCheckPure())){
                    for (DevanningGoodsInfoPureVO i:context1.getCheckPure() ){
                        if (Objects.isNull(i)){
                            continue;
                        }
                        if (Objects.nonNull(i.getType())){
//                            限购类型0库存，1区域限购数量，2营销总限购，3营销个人限购，-1区域限购不能购买

//                            k-250001=库存不足请重新选购
//                            k-250002=已达最大限购量，不能买更多
//                            k-250003=营销总限购不足
//                            k-250004=营销个人限购不足
//                            k-250005=区域限购不能购买

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
                stopWatch.stop();
//                Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));

//                log.info("===============tradeMarketingList:{}",JSONObject.toJSONString(tradeMarketingList));

                stopWatch.start("无效代码可以删除");
                List<TradeItemDTO> tradeItemscopy =new ArrayList<>();
                KsBeanUtil.copyList(tradeItems,tradeItemscopy);
                tradeItemscopy.forEach(t->{
                    BigDecimal bigDecimal = t.getDivisorFlag().multiply(BigDecimal.valueOf(t.getNum())).setScale(2, BigDecimal.ROUND_UP);
                    t.setBNum(bigDecimal);
//                    t.setNum(bigDecimal.longValue());
                });
                stopWatch.stop();

//                Map<String, BigDecimal> tradeItemMap = tradeItemscopy.stream().collect(Collectors.groupingBy(TradeItemDTO::getSkuId, Collectors.reducing(BigDecimal.ZERO,TradeItemDTO::getBNum,BigDecimal::add)));
//                response.getGoodsInfos().forEach(goodsInfoVO -> {
//                    if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
//                        List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
//                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
//                        if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())){
//                            if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
//                                log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
//                                if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()).compareTo(BigDecimal.valueOf(goodsInfoVO.getSingleOrderPurchaseNum())) > 0){
//                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
//                                }
//                            }
//                        }
//                    }
//                });
            }
            log.info("===============//白鲸散批:{}",JSONObject.toJSONString(confirmRequest.getRetailTradeItems()));
            if (CollectionUtils.isNotEmpty(confirmRequest.getRetailTradeItems())) {
                //白鲸散批
                retailTradeItems = confirmRequest.getRetailTradeItems().stream().map(
                        o -> TradeItemDTO.builder().skuId(o.getSkuId()).erpSkuNo(o.getErpSkuNo()).num(o.getNum()).build()
                ).collect(Collectors.toList());
                //验证采购单
                List<String> retailSkuIds =
                        confirmRequest.getRetailTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setGoodsInfoIds(retailSkuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = retailShopCartQueryProvider.query(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
                if (retailSkuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }
                GoodsInfoResponse response = orderUtil.getGoodsRetailResponseNew(retailSkuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
                verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(retailTradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                       null, false, true,customerId));


                retailTradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(retailTradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);


                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                //营销活动校验
                //营销活动是否生效校验
                MarketingByGoodsInfoIdAndIdResponse context = marketingQueryProvider.getEffectiveMarketingByIdsAndGoods(MarketingEffectiveRequest.builder()
                        .tradeMarketingList(confirmRequest.getTradeMarketingList())
                        .wareId(wareId).type("redis")
                        .tradeItems(KsBeanUtil.convert(tradeItems, TradeItemInfoDTO.class)).build()).getContext();
                List<TradeMarketingDTO> tradeMarketingList = context.getTradeMarketingList();
                confirmRequest.setTradeMarketingList(tradeMarketingList);
                if (CollectionUtils.isNotEmpty(context.getRemovemarketinglist()) || CollectionUtils.isNotEmpty(context.getRemovelist())){
                    shopCartProvider.delFirstSnapShopAndMarkeing(customerId);
                    String erroInfo = "";
                    String endmesg = "请重新结算";
                    if (CollectionUtils.isNotEmpty(context.getRemovemarketinglist())){
                        erroInfo=erroInfo+"您购买的部分商品所参与的活动已结束";
                    }
                    if (CollectionUtils.isNotEmpty(context.getRemovelist())){
                        erroInfo=erroInfo+"或赠品已经失效";
                    }
                    erroInfo=erroInfo+endmesg;
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,erroInfo);
                }

//                List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
//                        (), Collections.emptyList(), retailTradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
                retailSkuList = KsBeanUtil.convertList(response.getGoodsInfos(),GoodsInfoDTO.class);
                //限定区域指定商品限购校验



                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                List<DevanningGoodsInfoMarketingVO> requestparam= new LinkedList<>();
                for (TradeItemDTO tradeItemDTO:retailTradeItems){
                    DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO = new DevanningGoodsInfoMarketingVO();
                    devanningGoodsInfoMarketingVO
                            .setBuyCount(tradeItemDTO.getNum()).setDivisorFlag(Objects.nonNull(tradeItemDTO.getDivisorFlag())?tradeItemDTO.getDivisorFlag():BigDecimal.ONE)
                            .setGoodsInfoId(tradeItemDTO.getSkuId()).setSaleType(1);
                    requestparam.add(devanningGoodsInfoMarketingVO);
                }
                //区域限购营销限购校验
                StockAndPureChainNodeRsponse context1 = purchaseProvider.checkStockPurchase(StockAndPureChainNodeRequeest.builder()
                        .wareId(wareId)
                        .provinceId(deliveryAddress.getProvinceId())
                        .cityId(deliveryAddress.getCityId())
                        .checkPure(requestparam).build()).getContext();
                if (Objects.nonNull(context1) && CollectionUtils.isNotEmpty(context1.getCheckPure())){
                    for (DevanningGoodsInfoPureVO i:context1.getCheckPure() ){
                        if (Objects.isNull(i)){
                            continue;
                        }
                        if (Objects.nonNull(i.getType())){
//                            限购类型0库存，1区域限购数量，2营销总限购，3营销个人限购，-1区域限购不能购买
                            if (i.getType()==0){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "库存不足请重新选购");
                            }
                            else if (i.getType()==1){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "区域限购不足请重新选购");
                            }
                            else if (i.getType()==2){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "营销总限购不足请重新选购");
                            }
                            else if (i.getType()==3){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "营销个人限购不足请重新选购");
                            }
                            else if (i.getType()==-1){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "区域限购不能购买请重新选购");
                            }
                        }

                    }
                }


//                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
//                Map<String, Long> tradeItemMap = retailTradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
//                response.getGoodsInfos().forEach(goodsInfoVO -> {
//                    if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
//                        List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
//                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
//                        if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())){
//                            if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
//                                log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
//                                if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()){
//                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
//                                }
//                            }
//                        }
//                    }
//                });
            }
            //拆箱散批
            if (CollectionUtils.isNotEmpty(confirmRequest.getBulkTradeItems())){
                Long bulkWareId = commonUtil.getBulkWareId(HttpUtil.getRequest());
                //批发
                bulkTradeItems = confirmRequest.getBulkTradeItems().stream().map(
                        o -> TradeItemDTO.builder()
                                .skuId(o.getSkuId())
                                .erpSkuNo(o.getErpSkuNo())
                                .num(o.getNum())
//                                .devanningId(o.getDevanningskuId())
//                                .parentGoodsInfoId(o.getParentGoodsInfoId())
                                .wareId(bulkWareId)
                                .build()
                ).collect(Collectors.toList());



                //验证采购单
                List<String> bulkSkuIds =
                        confirmRequest.getBulkTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                PurchaseQueryCacheRequest purchaseQueryRequest = new PurchaseQueryCacheRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setGoodsInfos(bulkSkuIds);
                purchaseQueryRequest.setWareId(bulkWareId);
                PurchaseQueryResponse purchaseQueryResponse = bulkShopCartQueryProvider.queryShopCarExit(purchaseQueryRequest).getContext();

                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
                if (bulkSkuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }

                //获取商品详情
                List<String> skuids = confirmRequest.getBulkTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                WareHouseVO wareHouseVOForBulk = commonUtil.getWareHouseByWareId(bulkWareId);
                GoodsInfoResponse response = orderUtil.getBulkGoodsResponseNew(skuids, bulkWareId, wareHouseVOForBulk.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());

                //商品验证
                bulkTradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(bulkTradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);
                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));


                bulkSkuList = KsBeanUtil.convertList(response.getGoodsInfos(),GoodsInfoDTO.class);

                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                List<DevanningGoodsInfoMarketingVO> requestparam= new LinkedList<>();
                for (TradeItemDTO tradeItemDTO:bulkTradeItems){
                    DevanningGoodsInfoMarketingVO devanningGoodsInfoMarketingVO = new DevanningGoodsInfoMarketingVO();
                    devanningGoodsInfoMarketingVO.setDevanningId(tradeItemDTO.getDevanningId())
                            .setBuyCount(tradeItemDTO.getNum()).setDivisorFlag(BigDecimal.ONE)
                            .setGoodsInfoId(tradeItemDTO.getSkuId()).setSaleType(2); // 2代表拆箱散批
                    requestparam.add(devanningGoodsInfoMarketingVO);
                }
                //区域限购营销限购校验
                StockAndPureChainNodeRsponse context1 = purchaseProvider.checkStockPurchase(StockAndPureChainNodeRequeest.builder()
                        .wareId(bulkWareId)
                        .subType(4)
                        .provinceId(deliveryAddress.getProvinceId())
                        .cityId(deliveryAddress.getCityId())
                        .checkPure(requestparam).build()).getContext();
                if (Objects.nonNull(context1) && CollectionUtils.isNotEmpty(context1.getCheckPure())){
                    for (DevanningGoodsInfoPureVO i:context1.getCheckPure() ){
                        if (Objects.isNull(i)){
                            continue;
                        }
                        if (Objects.nonNull(i.getType())){
//                            限购类型0库存，1区域限购数量，2营销总限购，3营销个人限购，-1区域限购不能购买
                            if (i.getType()==0){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "库存不足请重新选购");
                            }
                            else if (i.getType()==1){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "区域限购不足请重新选购");
                            }
                            else if (i.getType()==-1){
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "区域限购不能购买请重新选购");
                            }else {
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "校验失败");
                            }
                        }

                    }
                }
            }
            stopWatch.start("生成快照方法");
            log.info("===============snapshotRetail:{}",JSONObject.toJSONString(confirmRequest.getRetailTradeItems()));
            BaseResponse baseResponse = tradeItemProvider.snapshotRetail(TradeItemSnapshotRequest.builder()
                    .customerId(customerId)
                    .tradeItems(tradeItems)
                    .tradeMarketingList(confirmRequest.getTradeMarketingList())
                    .skuList(skuList)
                    .retailTradeItems(retailTradeItems)
                    .retailtTradeMarketingList(confirmRequest.getRetailTradeMarketingList())
                    .retailSkuList(retailSkuList)
                    .bulkTradeItems(bulkTradeItems)
                    .bulkSkuList(bulkSkuList)
                    .build());
            stopWatch.stop();
            log.info("生成快照方法日志分析"+stopWatch.prettyPrint());
            return baseResponse;
        }catch (SbcRuntimeException e){
            e.printStackTrace();
            if(e.getErrorCode().equals("K-050521")){
                return BaseResponse.info(e.getErrorCode(), e.getResult(), e.getData());
            }
//            log.error("TradeBaseController.confirm error:{}",e.getMessage());
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        }finally {
            log.info("TradeBaseController.confirm unlock:{}",customerId);
            rLock.unlock();
        }
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void validShopGoods(List<GoodsInfoVO> goodsInfoVOS) {
        goodsInfoVOS.stream().forEach(goodsInfo -> {
            if (goodsInfo.getGoodsStatus() == GoodsStatus.INVALID) {
                throw new SbcRuntimeException("K-050117");
            }
        });
    }


    /**
     * 开店礼包购买
     */
    @ApiOperation(value = "开店礼包购买")
    @RequestMapping(value = "/store-bags-buy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse storeBagsBuy(@RequestBody @Valid StoreBagsBuyRequest request) {
        CustomerVO customer = commonUtil.getCustomer();

        List<TradeItemDTO> tradeItems = new ArrayList<>();
        tradeItems.add(TradeItemDTO.builder()
                .skuId(request.getGoodsInfoId())
                .num(1L).build());

        // 1.获取商品信息
        List<String> skuIds = Arrays.asList(request.getGoodsInfoId());
        GoodsInfoResponse response = getGoodsResponse(skuIds, customer);
        GoodsInfoVO goodsInfo = response.getGoodsInfos().get(0);
        //普通商品
        goodsInfo.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
        //开店礼包不限制起订量、限定量
        goodsInfo.setCount(null);
        goodsInfo.setMaxCount(null);
        //商品验证
        BaseResponse<VerifyGoodsResponse> verifyGoods = verifyQueryProvider.verifyGoods(
                new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                        goodsInfo.getStoreId(), true, true,customer.getCustomerId()));

        // 2.填充商品信息
        tradeItems = KsBeanUtil.convertList(verifyGoods.getContext().getTradeItems(), TradeItemDTO.class);
        tradeItems.get(0).setPrice(goodsInfo.getMarketPrice());

        // 3.设置订单快照
        return tradeItemProvider.snapshot(
                TradeItemSnapshotRequest.builder()
                        .customerId(customer.getCustomerId())
                        .storeBagsFlag(DefaultFlag.YES)
                        .tradeItems(tradeItems)
                        .tradeMarketingList(new ArrayList<>())
                        .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }


    /**
     * 拼团购买
     */
    @ApiOperation(value = "拼团购买")
    @RequestMapping(value = "/groupon-buy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse grouponBuy(@RequestBody @Valid GrouponBuyRequest request) {

        List<TradeItemDTO> tradeItems = new ArrayList<>();
        tradeItems.add(TradeItemDTO.builder()
                .skuId(request.getGoodsInfoId())
                .erpSkuNo(request.getErpGoodsInfoNo())
                .num(request.getBuyCount()).build());

        CustomerVO customer = commonUtil.getCustomer();
        List<String> skuIds = Arrays.asList(request.getGoodsInfoId());
        GoodsInfoResponse response = getGoodsResponse(skuIds, customer);

        // 填充、生成快照
        return tradeItemProvider.snapshot(
                TradeItemSnapshotRequest.builder()
                        .customerId(customer.getCustomerId())
                        .openGroupon(request.getOpenGroupon())
                        .grouponNo(request.getGrouponNo())
                        .tradeItems(tradeItems)
                        .tradeMarketingList(new ArrayList<>())
                        .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());

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
//                PurchaseGetGoodsMarketingResponse marketingResponse = purchaseQueryProvider.getGoodsMarketing
//                        (purchaseGetGoodsMarketingRequest).getContext();
                PurchaseGetGoodsMarketingResponse marketingResponse = shopCartQueryProvider.getGoodsMarketing
                        (purchaseGetGoodsMarketingRequest).getContext();
                List<MarketingViewVO> marketings = marketingResponse.getMap().get(goodsInfo.getGoodsInfoId());
                if (tradeItems.get(0).getGoodsInfoType() == 1) {
                    tradeItems.get(0).setPrice(goodsInfo.getSpecialPrice());
                }
                TradeMarketingDTO tradeMarketing = chooseDefaultMarketing(tradeItems.get(0), marketings, confirmRequest.getMatchWareHouseFlag(),confirmRequest.getChoseGiftIds());
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

        //限定区域指定商品限购校验
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
        response.getGoodsInfos().forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
                List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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

        return tradeItemProvider.snapshot(
                TradeItemSnapshotRequest.builder()
                        .customerId(customer.getCustomerId())
                        .tradeItems(tradeItems)
                        .tradeMarketingList(tradeMarketingList)
                        .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }

    /**
     * 拆箱立即购买
     */
    @ApiOperation(value = "拆箱立即购买")
    @RequestMapping(value = "/devanning-buy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse devanningBuy(@RequestBody @Valid ImmediateBuyNewRequest confirmRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        List<TradeItemDTO> tradeItems = KsBeanUtil.convertList(confirmRequest.getTradeItemRequests(), TradeItemDTO.class);

        // 1.获取商品信息
        List<Long> devanningIds = tradeItems.stream().map(TradeItemDTO::getDevanningId).collect(Collectors.toList());
        GoodsInfoResponse response = getDevanningGoodsResponse(devanningIds, customer, wareId);
        GoodsInfoVO goodsInfo = response.getGoodsInfos().get(0);

        //设置拆箱属性
        tradeItems.forEach(t->{
            DevanningGoodsInfoByIdResponse devanningGoodsInfoById = devanningGoodsInfoQueryProvider.getInfoById(
                    DevanningGoodsInfoByIdRequest
                            .builder()
                            .devanningId(t.getDevanningId())
                            .build()).getContext();
            t.setDivisorFlag(devanningGoodsInfoById.getDevanningGoodsInfoVO().getDivisorFlag());
            t.setGoodsSubtitle(devanningGoodsInfoById.getDevanningGoodsInfoVO().getGoodsInfoSubtitle());
        });
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
//                PurchaseGetGoodsMarketingResponse marketingResponse = purchaseQueryProvider.getGoodsMarketing
//                        (purchaseGetGoodsMarketingRequest).getContext();
//                PurchaseGetGoodsMarketingResponse marketingResponse = shopCartQueryProvider.getGoodsMarketing
//                        (purchaseGetGoodsMarketingRequest).getContext();
//                List<MarketingViewVO> marketings = marketingResponse.getMap().get(goodsInfo.getGoodsInfoId());
                if (tradeItems.get(0).getGoodsInfoType() == 1) {
                    tradeItems.get(0).setPrice(goodsInfo.getSpecialPrice());
                }
                //获取最优营销
                List<DevanningGoodsInfoVO> JoinMarketingGoods = new LinkedList<>();//需要去参加营销的商品

                DevanningGoodsInfoVO convert = KsBeanUtil.convert(goodsInfo, DevanningGoodsInfoVO.class);

                convert.setBuyCount(tradeItems.get(0).getNum());
                convert.setGoodsInfoId(tradeItems.get(0).getSkuId());
                convert.setDevanningId(tradeItems.get(0).getDevanningId());
                convert.setDivisorFlag(tradeItems.get(0).getDivisorFlag());
                convert.setMarketPrice(tradeItems.get(0).getPrice());
                JoinMarketingGoods.add(convert);

                MarketingGroupCardResponse marketingGroupCardResponse = marketingQueryProvider.singleMarketingGroupList(MarketingCardGroupRequest.builder()
                        .devanningGoodsInfoVOList(JoinMarketingGoods).customerId(customer.getCustomerId())
                        .customerVO(customer)
                        .build()).getContext();
                log.info("TradeBaseController devanningBuy marketingGroupCardResponse:{}",JSON.toJSONString(marketingGroupCardResponse));
                if (CollectionUtils.isNotEmpty(marketingGroupCardResponse.getMarketingGroupCards())){
                    List<TradeItemDTO> finalTradeItems = tradeItems;

                    TradeItemDTO tradeItemDTO = finalTradeItems.get(0);
                    String skuId = tradeItemDTO.getSkuId();

                    marketingGroupCardResponse.getMarketingGroupCards().forEach(v->{
                        BigDecimal bNum = BigDecimal.ZERO;
                        List<DevanningGoodsInfoVO> devanningGoodsInfoVOList = v.getDevanningGoodsInfoVOList();
                        Optional<DevanningGoodsInfoVO> first = devanningGoodsInfoVOList.stream().findFirst();
                        if(first.isPresent()){
                            bNum = BigDecimal.valueOf(Optional.ofNullable(first.get().getBuyCount()).orElse(0L));
                        }
                        if (v.getReachLevel()){
                            // 查出限购直接屏蔽掉
                            if (Objects.nonNull(v.getCurrentFullGiftLevel())){
                                MarketingFullGiftLevelVO currentFullGiftLevel = v.getCurrentFullGiftLevel();
                                Long purchaseNumOfMarketing = this.getPurchaseNumOfMarketing(currentFullGiftLevel.getMarketingId(), skuId, customer.getCustomerId());
                                boolean nullflag = Objects.isNull(purchaseNumOfMarketing);
                                Long aLong = Optional.ofNullable(purchaseNumOfMarketing).orElse(0L);
                                if(nullflag || bNum.longValue() <= aLong){// 未超过最大限购量才使用当前营销活动
                                    tradeMarketingList.add(TradeMarketingDTO.builder()
                                            .marketingId(currentFullGiftLevel.getMarketingId())
                                            .marketingLevelId(currentFullGiftLevel.getGiftLevelId())
                                            .skuIds(Arrays.asList(finalTradeItems.get(0).getSkuId()))
                                            .giftSkuIds(confirmRequest.getChoseGiftIds())
                                            .build());
                                }
                            }
                            else if (Objects.nonNull(v.getCurrentFullDiscountLevel())){
                                MarketingFullDiscountLevelVO currentFullDiscountLevel = v.getCurrentFullDiscountLevel();
                                Long purchaseNumOfMarketing = this.getPurchaseNumOfMarketing(currentFullDiscountLevel.getMarketingId(), skuId, customer.getCustomerId());
                                boolean nullflag = Objects.isNull(purchaseNumOfMarketing);
                                Long aLong = Optional.ofNullable(purchaseNumOfMarketing).orElse(0L);
                                if(nullflag || bNum.longValue() <= aLong  ){// 未超过最大限购量才使用当前营销活动
                                    tradeMarketingList.add(TradeMarketingDTO.builder()
                                            .marketingId(currentFullDiscountLevel.getMarketingId())
                                            .marketingLevelId(currentFullDiscountLevel.getDiscountLevelId())
                                            .skuIds(Arrays.asList(finalTradeItems.get(0).getSkuId()))
                                            .build());
                                }
                            }
                            else if (Objects.nonNull(v.getCurrentFullReductionLevel())){
                                MarketingFullReductionLevelVO currentFullReductionLevel = v.getCurrentFullReductionLevel();
                                Long purchaseNumOfMarketing = this.getPurchaseNumOfMarketing(currentFullReductionLevel.getMarketingId(), skuId, customer.getCustomerId());
                                log.info("限购数量==="+purchaseNumOfMarketing);
                                boolean nullflag = Objects.isNull(purchaseNumOfMarketing);
                                log.info("是否为null"+nullflag);
                                log.info("购买数量"+bNum);
                                Long aLong = Optional.ofNullable(purchaseNumOfMarketing).orElse(0L);
                                if(nullflag || bNum.longValue() <= aLong ) { // 未超过最大限购量才使用当前营销活动
                                    log.info("是否相加");
                                    tradeMarketingList.add(TradeMarketingDTO.builder()
                                            .marketingId(currentFullReductionLevel.getMarketingId())
                                            .marketingLevelId(currentFullReductionLevel.getReductionLevelId())
                                            .skuIds(Arrays.asList(finalTradeItems.get(0).getSkuId()))
                                            .build());
                                }
                            }
                        }
                    });

                }


//                TradeMarketingDTO tradeMarketing = chooseDefaultMarketing(tradeItems.get(0), marketings, confirmRequest.getMatchWareHouseFlag(),confirmRequest.getChoseGiftIds());
//                if (tradeMarketing != null) {
//                    tradeMarketingList.add(tradeMarketing);
//                }
            } else {
                // 4.2.非礼包、分销商品，重置商品价格
                tradeItems.get(0).setPrice(goodsInfo.getMarketPrice());
            }
        }
        if (tradeItems.get(0).getGoodsInfoType() == 1) {
            tradeItems.get(0).setPrice(goodsInfo.getSpecialPrice());
        }

        return tradeItemProvider.snapshot(
                TradeItemSnapshotRequest.builder()
                        .customerId(customer.getCustomerId())
                        .tradeItems(tradeItems)
                        .tradeMarketingList(tradeMarketingList)
                        .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }


    /**
     * 确认订单页面查询优惠明细
     * @return
     */
    @ApiOperation(value = "确认订单优惠明细")
    @GetMapping(value = "/discountPriceDetailForCommit")
    public BaseResponse<List<DiscountPriceDetailResponse>> findDiscountPriceDetailForCommit(@RequestParam(name = "purchaseType",defaultValue = "1") Integer purchaseType){
        LocalDateTime now = LocalDateTime.now();
        List<DiscountPriceDetailResponse> responseList = new ArrayList<>();

        CustomerVO customer = commonUtil.getCustomer();
        BaseResponse<TradeItemByCustomerIdResponse> response = null;
        // 查询当前用户的订单快照
        TradeItemByCustomerIdRequest customerIdRequest = TradeItemByCustomerIdRequest.builder().customerId(customer.getCustomerId()).build();
        if(purchaseType == 1){ // 正常购物车
            response = tradeItemQueryProvider.itemListByCustomerId(customerIdRequest);
        }else { // 囤货购物车
            response = newPileTradeItemQueryProvider.listByCustomerId(customerIdRequest);
        }
        if(Objects.isNull(response) || Objects.isNull(response.getContext()) || CollectionUtils.isEmpty(response.getContext().getTradeItemGroupList())){
            log.info("DiscountPriceDetailForPurchaseNow ==> CustomerId->{} 未查询到快照",customer.getCustomerId());
            return BaseResponse.success(responseList);
        }
        List<TradeItemGroupVO> tradeItemGroupVOList = response.getContext().getTradeItemGroupList();
        // 当前购买商品信息
        List<TradeItemVO> tradeItemVOS = tradeItemGroupVOList.stream().flatMap(item -> item.getTradeItems().stream()).collect(Collectors.toList());

        // 获取当前参与的活动
        List<TradeItemMarketingVO> itemMarketingVOS = tradeItemGroupVOList.stream().filter(v->{
            if (CollectionUtils.isNotEmpty(v.getTradeMarketingList())){
                return true;
            }
            return false;
        }).flatMap(tradeItemGroup -> tradeItemGroup.getTradeMarketingList().stream()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(itemMarketingVOS) || tradeItemGroupVOList.stream().allMatch(item -> CollectionUtils.isEmpty(item.getTradeMarketingList()))){
            log.info("DiscountPriceDetailForPurchaseNow ==> CustomerId->{} 未查询到参与的活动",customer.getCustomerId());
            return BaseResponse.success(responseList);
        }
        Map<Long, List<TradeItemMarketingVO>> marketingMap = itemMarketingVOS.stream().collect(Collectors.groupingBy(TradeItemMarketingVO::getMarketingId));
        // 根据活动ID查询所有活动名称
        MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
        marketingQueryByIdsRequest.setMarketingIds(new ArrayList<>(marketingMap.keySet()));
        BaseResponse<MarketingQueryByIdsResponse> marketingQueryByIdsResponse = marketingQueryProvider.queryByIds(marketingQueryByIdsRequest);
        List<MarketingVO> marketingVOList = marketingQueryByIdsResponse.getContext().getMarketingVOList();

        //去除满赠的互动
        List<Long> nonGiftMarketIds = marketingVOList.stream().filter(item -> item.getMarketingType() == MarketingType.DISCOUNT || item.getMarketingType() == MarketingType.REDUCTION)
                .map(item -> item.getMarketingId()).collect(Collectors.toList());

        // 获取参与活动的所有商品
        List<String> skuIds = itemMarketingVOS.stream().flatMap(joinMarketGoodsSku -> joinMarketGoodsSku.getSkuIds().stream()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(skuIds)){
            log.info("DiscountPriceDetailForPurchaseNow ==> CustomerId->{} 未查询到参与活动的商品",customer.getCustomerId());
            return BaseResponse.success(responseList);
        }
        BaseResponse<GoodsInfoListByIdsResponse> goodsInfoListResponse = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build());
        List<GoodsInfoVO> goodsInfoList = goodsInfoListResponse.getContext().getGoodsInfos();
        // 封装返回结果集
        marketingMap.forEach((k,v) -> {
            if(nonGiftMarketIds.contains(k)){
                // 查询当前活动明细
                marketingVOList.stream().filter(mk -> mk.getMarketingId().equals(k)).findFirst().ifPresent(marketingVO -> {
                    // 参与当前活动的SKU和商品信息
                    List<String> currentMarketGoodSkus = v.stream().flatMap(joinMarketGoodsSku -> joinMarketGoodsSku.getSkuIds().stream()).collect(Collectors.toList());
                    List<GoodsInfoVO> infoVOList = goodsInfoList.stream().filter(item -> currentMarketGoodSkus.contains(item.getGoodsInfoId())).collect(Collectors.toList());
                    List<DiscountPriceDetailResponse> tempResponse = new ArrayList<>();
                    for (GoodsInfoVO goodsInfoVO : infoVOList) {
                        Optional<TradeItemVO> itemVOOptional = tradeItemVOS.stream().filter(item -> item.getSkuId().equals(goodsInfoVO.getGoodsInfoId())).findFirst();
                        if (itemVOOptional.isPresent()) {
                            DiscountPriceDetailResponse discountPriceDetailResponse = new DiscountPriceDetailResponse();
                            TradeItemVO tradeItemVO = itemVOOptional.get();

                            discountPriceDetailResponse.setGoodsInfoName(goodsInfoVO.getGoodsInfoName());
                            discountPriceDetailResponse.setGoodsSubtitle(tradeItemVO.getGoodsSubtitle());
                            discountPriceDetailResponse.setBuyNum(tradeItemVO.getNum());
                            discountPriceDetailResponse.setGoodsImageUrl(goodsInfoVO.getGoods().getGoodsImg());
                            discountPriceDetailResponse.setTotalPrice(goodsInfoVO.getGoods().getMarketPrice());
                            tempResponse.add(discountPriceDetailResponse);
                        }
                    }
                    // 计算商品折扣金额
                    // 活动未开始 | 活动已结束 | 活动已暂停
                    if(now.isBefore(marketingVO.getBeginTime()) || now.isAfter(marketingVO.getEndTime()) || marketingVO.getIsPause() == BoolFlag.YES){
                        log.info("DiscountPriceDetailForPurchaseNow->calcDiscountPrice->marketingId:{}",marketingVO.getMarketingId());
                        return;
                    }
                    MarketingType marketingType = marketingVO.getMarketingType();
                    MarketingSubType subType = marketingVO.getSubType();
                    // 设置活动名称
                    spliceActivityName(marketingVO,tempResponse);
                    if(marketingType == MarketingType.DISCOUNT) {
                        MarketingFullDiscountLevelVO fullDiscountLevel = getFullDiscountLevel(marketingVO.getMarketingFullDiscountLevels(), tempResponse, subType);
                        calcFullDiscountPrice(fullDiscountLevel,tempResponse,subType,marketingVO.getIsOverlap());
                    }else if(marketingType == MarketingType.REDUCTION){
                        MarketingFullReductionLevelVO fullReductionLevel = getFullReductionLevel(marketingVO.getMarketingFullReductionLevels(), tempResponse, subType);
                        calcFullReductionPrice(fullReductionLevel,tempResponse,subType,marketingVO.getIsOverlap());
                    }
                    responseList.addAll(tempResponse);
                });
            }
        });

        return BaseResponse.success(responseList);
    }

    // 获取满折优惠级别
    private MarketingFullDiscountLevelVO getFullDiscountLevel(List<MarketingFullDiscountLevelVO> fullDiscountLevelVOList, List<DiscountPriceDetailResponse> tempResponseList, MarketingSubType subType){
        // 购买总数量
        Long totalBuyNum = tempResponseList.stream().map(DiscountPriceDetailResponse::getBuyNum).reduce(0l, (a, b) -> a + b);
        // 购买总金额
        BigDecimal totalBuyPrice = tempResponseList.stream().map(item -> item.getTotalPrice().multiply(BigDecimal.valueOf(item.getBuyNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        MarketingFullDiscountLevelVO fullDiscountLevelVO = null;
        // 计算倍数
        if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){ // 满数量折扣
            for (MarketingFullDiscountLevelVO discountLevelVO : fullDiscountLevelVOList) {
                if(totalBuyNum >= discountLevelVO.getFullCount()){
                    fullDiscountLevelVO = discountLevelVO;
                }
            }
        } else if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT) { // 满金额减
            for (MarketingFullDiscountLevelVO discountLevelVO : fullDiscountLevelVOList) {
                if(totalBuyPrice.compareTo(discountLevelVO.getFullAmount()) >= 0){
                    fullDiscountLevelVO = discountLevelVO;
                }
            }
        }
        return fullDiscountLevelVO;
    }

    // 获取满折优惠级别
    private MarketingFullReductionLevelVO getFullReductionLevel(List<MarketingFullReductionLevelVO> fullReductionLevelVOList, List<DiscountPriceDetailResponse> tempResponseList, MarketingSubType subType){
        // 购买总数量
        Long totalBuyNum = tempResponseList.stream().map(DiscountPriceDetailResponse::getBuyNum).reduce(0l, (a, b) -> a + b);
        // 购买总金额
        BigDecimal totalBuyPrice = tempResponseList.stream().map(item -> item.getTotalPrice().multiply(BigDecimal.valueOf(item.getBuyNum()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        MarketingFullReductionLevelVO tmp = null;
        if(subType == MarketingSubType.REDUCTION_FULL_COUNT){ // 满数量减
            for (MarketingFullReductionLevelVO reductionLevelVO : fullReductionLevelVOList) {
                if(totalBuyNum >= reductionLevelVO.getFullCount()){
                    tmp = reductionLevelVO;
                }
            }
        } else if (subType == MarketingSubType.REDUCTION_FULL_AMOUNT) { // 满金额减
            for (MarketingFullReductionLevelVO reductionLevelVO : fullReductionLevelVOList) {
                if(totalBuyPrice.compareTo(reductionLevelVO.getFullAmount()) >= 0){
                    tmp = reductionLevelVO;
                }
            }
        }
        return  tmp;
    }


    // 计算满折金额明细
    private void calcFullDiscountPrice(MarketingFullDiscountLevelVO fullDiscountLevelVO, List<DiscountPriceDetailResponse> tempResponseList, MarketingSubType subType, BoolFlag isOverlap) {
        // 购买总数量
        Long totalBuyNum = tempResponseList.stream().map(DiscountPriceDetailResponse::getBuyNum).reduce(0l, (a, b) -> a + b);
        // 购买总金额
        BigDecimal totalBuyPrice = tempResponseList.stream().map(item -> item.getTotalPrice().multiply(BigDecimal.valueOf(item.getBuyNum()).setScale(2,BigDecimal.ROUND_FLOOR))).reduce(BigDecimal.ZERO, BigDecimal::add);
        int size = tempResponseList.size();
        // 计算倍数
        long multiple = 0l;
        if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){ // 满数量折扣
            multiple = totalBuyNum / fullDiscountLevelVO.getFullCount();
        } else if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT) { // 满金额减
            multiple = totalBuyPrice.divide(fullDiscountLevelVO.getFullAmount(),2, BigDecimal.ROUND_FLOOR).longValue();
        }

        if(isOverlap == BoolFlag.YES){
            //优惠总折扣
            BigDecimal totalDiscount = BigDecimal.ZERO;
            if(multiple > 0){
                totalDiscount = BigDecimal.valueOf(Math.pow(fullDiscountLevelVO.getDiscount().doubleValue(),multiple)).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            }
            // 计算总折扣金额
            BigDecimal totalDiscountPrice = totalBuyPrice.subtract(totalBuyPrice.multiply(totalDiscount)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal splitPriceTotal = BigDecimal.ZERO;
            for (int i = 0; i < size; i++) {
                DiscountPriceDetailResponse detailResponse = tempResponseList.get(i);
                if(i == size-1){
                    detailResponse.setDiscountPrice(totalDiscountPrice.subtract(splitPriceTotal));
                }else{
                    // 单个商品的优惠金额所占比例
                    BigDecimal percent = BigDecimal.ZERO;
                    if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){ // 满数量折扣
                        percent = BigDecimal.valueOf(detailResponse.getBuyNum()).divide(BigDecimal.valueOf(totalBuyNum),2, BigDecimal.ROUND_FLOOR);
                    } else if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT) { // 满金额减
                        percent = detailResponse.getTotalPrice().multiply(BigDecimal.valueOf(detailResponse.getBuyNum())).divide(totalBuyPrice,2, BigDecimal.ROUND_FLOOR);
                    }
                    BigDecimal currentProDiscountPrice = totalDiscountPrice.multiply(percent).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                    detailResponse.setDiscountPrice(currentProDiscountPrice);
                    splitPriceTotal = splitPriceTotal.add(currentProDiscountPrice);
                }
            }
        }else {
            if(multiple > 0){
                // 计算总折扣金额
                BigDecimal totalDiscountPrice = totalBuyPrice.subtract(totalBuyPrice.multiply(fullDiscountLevelVO.getDiscount())).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                BigDecimal splitPriceTotal = BigDecimal.ZERO;
                for (int i = 0; i < size; i++) {
                    DiscountPriceDetailResponse detailResponse = tempResponseList.get(i);
                    if(i == size-1){
                        detailResponse.setDiscountPrice(totalDiscountPrice.subtract(splitPriceTotal));
                    }else{
                        // 单个商品的优惠金额所占比例
                        BigDecimal percent = BigDecimal.ZERO;
                        if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){ // 满数量折扣
                            percent = BigDecimal.valueOf(detailResponse.getBuyNum()).divide(BigDecimal.valueOf(totalBuyNum),2, BigDecimal.ROUND_FLOOR);
                        } else if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT) { // 满金额减
                            percent = detailResponse.getTotalPrice().multiply(BigDecimal.valueOf(detailResponse.getBuyNum())).divide(totalBuyPrice,2, BigDecimal.ROUND_FLOOR);
                        }
                        BigDecimal currentProDiscountPrice = totalDiscountPrice.multiply(percent).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                        detailResponse.setDiscountPrice(currentProDiscountPrice);
                        splitPriceTotal = splitPriceTotal.add(currentProDiscountPrice);
                    }
                }
            }else{ // 不满足条件
                tempResponseList.forEach(detailResponse -> {
                    detailResponse.setDiscountPrice(BigDecimal.ZERO);
                });
            }
        }
    }

    // 计算满减金额
    private void calcFullReductionPrice(MarketingFullReductionLevelVO fullReductionLevelVO, List<DiscountPriceDetailResponse> tempResponseList, MarketingSubType subType,BoolFlag isOverlap) {

        BigDecimal reduction = fullReductionLevelVO.getReduction(); // 当前活动总共优惠金额
        BigDecimal fullAmountPrice = BigDecimal.ZERO;
        Long totalBuyNum = tempResponseList.stream().map(DiscountPriceDetailResponse::getBuyNum).reduce(0l, (a, b) -> a + b);
        // 购买总金额
        BigDecimal totalBuyPrice = tempResponseList.stream().map(item -> item.getTotalPrice().multiply(BigDecimal.valueOf(item.getBuyNum()).setScale(2,BigDecimal.ROUND_FLOOR))).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (subType == MarketingSubType.REDUCTION_FULL_AMOUNT){
            int discountPercent = totalBuyPrice.intValue() / fullReductionLevelVO.getFullAmount().intValue();
            fullAmountPrice = reduction.multiply(new BigDecimal(discountPercent));
            log.info("calcFullReductionPrice --> totalBuyPrice:{},fullAmountPrice:{},discountPercent:{},reduction：{}",totalBuyPrice,fullAmountPrice,discountPercent,reduction);
        }else if(subType == MarketingSubType.REDUCTION_FULL_COUNT){
            long discountPercent = totalBuyNum / fullReductionLevelVO.getFullCount();
            fullAmountPrice = reduction.multiply(new BigDecimal(discountPercent));
        }
        int size = tempResponseList.size();
        BigDecimal splitPriceTotal = BigDecimal.ZERO;
        if(isOverlap == BoolFlag.YES){
            for (int i = 0; i < size; i++) {
                DiscountPriceDetailResponse detailResponse = tempResponseList.get(i);
                if (i == size - 1) { // 最后一位
                    detailResponse.setDiscountPrice(fullAmountPrice.subtract(splitPriceTotal));
                }else{
                    if(subType == MarketingSubType.REDUCTION_FULL_COUNT){ // 满数量减
                        long floor = detailResponse.getBuyNum() / fullReductionLevelVO.getFullCount();
                        BigDecimal decimal = fullReductionLevelVO.getReduction().multiply(BigDecimal.valueOf(floor)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                        detailResponse.setDiscountPrice(decimal);
                        splitPriceTotal = splitPriceTotal.add(decimal);
                    } else if (subType == MarketingSubType.REDUCTION_FULL_AMOUNT) { // 满金额减
                        BigDecimal totalPrice = detailResponse.getTotalPrice().multiply(BigDecimal.valueOf(detailResponse.getBuyNum()));
                        BigDecimal divide = totalPrice.divide(totalBuyPrice, 2, BigDecimal.ROUND_FLOOR);
                        BigDecimal discountPrice = divide.multiply(fullAmountPrice).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                        detailResponse.setDiscountPrice(discountPrice);
                        splitPriceTotal = splitPriceTotal.add(discountPrice);
                    }
                }
            }
        }else{
            for (int i = 0; i < size; i++) {
                DiscountPriceDetailResponse detailResponse = tempResponseList.get(i);
                if(i == size-1){ // 最后一位
                    detailResponse.setDiscountPrice(reduction.subtract(splitPriceTotal));
                }else{
                    BigDecimal percent = BigDecimal.ZERO;// 计算当前商品的优惠金额占有比例
                    if(subType == MarketingSubType.REDUCTION_FULL_COUNT){ // 满数量减
                        percent = BigDecimal.valueOf(detailResponse.getBuyNum()).divide(BigDecimal.valueOf(totalBuyNum),2, BigDecimal.ROUND_FLOOR);
                        BigDecimal currentProDiscountPrice = reduction.multiply(percent).setScale(2,BigDecimal.ROUND_HALF_DOWN);
                        detailResponse.setDiscountPrice(currentProDiscountPrice);
                        splitPriceTotal = splitPriceTotal.add(currentProDiscountPrice);
                    } else if (subType == MarketingSubType.REDUCTION_FULL_AMOUNT) { // 满金额减
                        percent = detailResponse.getTotalPrice().multiply(BigDecimal.valueOf(detailResponse.getBuyNum())).divide(totalBuyPrice,2, BigDecimal.ROUND_FLOOR);
                        BigDecimal currentProDiscountPrice = fullAmountPrice.multiply(percent).setScale(2,BigDecimal.ROUND_HALF_DOWN);
                        detailResponse.setDiscountPrice(currentProDiscountPrice);
                        splitPriceTotal = splitPriceTotal.add(currentProDiscountPrice);
                    }
                }
            }
        }
    }


    /**
     * 购物车优惠明细
     * @param marketingType 优惠类型
     * @return
     */
    @PostMapping("/discountPriceDetail/{marketingType}")
    public BaseResponse findDiscountPriceDetailByMarketingType(@RequestBody @Valid TradeItemConfirmRequest confirmRequest,@PathVariable("marketingType") MarketingType marketingType){
        log.info("findDiscountPriceDetailByMarketingType入参：{},{}",JSON.toJSONString(confirmRequest),marketingType);
        LocalDateTime now = LocalDateTime.now();
        List<DiscountPriceDetailResponse> responseList = new ArrayList<>();
        CustomerVO customer = commonUtil.getCustomer();
        // 当前购买商品信息
        List<TradeItemRequest> tradeItemRequestList = confirmRequest.getTradeItems();
        // 获取当前参与的活动
        List<TradeMarketingDTO> itemMarketingVOS = confirmRequest.getTradeMarketingList();

        //根据活动ID分组
        Map<Long, List<TradeMarketingDTO>> listMap = itemMarketingVOS.stream().filter(item -> Objects.nonNull(item.getMarketingId())).collect(Collectors.groupingBy(TradeMarketingDTO::getMarketingId));
        List<TradeMarketingDTO> comMarketingVOS = new ArrayList<>();
        // 整合同一个活动下的所有skuIds
        listMap.forEach((k,v) -> {
            List<String> skuIds = v.stream().flatMap(item -> item.getSkuIds().stream()).collect(Collectors.toList());
            TradeMarketingDTO tempMarket = v.get(0);
            tempMarket.setSkuIds(skuIds);
            comMarketingVOS.add(tempMarket);
        });

        // 根据活动ID查询所有活动名称
        MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
        marketingQueryByIdsRequest.setMarketingIds(comMarketingVOS.stream().map(TradeMarketingDTO::getMarketingId).collect(Collectors.toList()));
        BaseResponse<MarketingQueryByIdsResponse> marketingQueryByIdsResponse = marketingQueryProvider.queryByIds(marketingQueryByIdsRequest);
        List<MarketingVO> marketingVOList = marketingQueryByIdsResponse.getContext().getMarketingVOList();
        if(CollectionUtils.isEmpty(marketingVOList)){
            return BaseResponse.success(responseList);
        }
        List<MarketingVO> matchMarketingVOList = marketingVOList.stream().filter(marketingVO -> marketingType == marketingVO.getMarketingType()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(matchMarketingVOList)){
            log.info("findDiscountPriceDetailByMarketingType->未查询到{}类型活动",marketingType);
            return BaseResponse.success(responseList);
        }
        // 过滤出符合当前活动类型
        List<Long> matchMarketingIds = matchMarketingVOList.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList());
        Map<Long, List<TradeMarketingDTO>> matchMarketingMap = comMarketingVOS.stream().filter(market -> matchMarketingIds.contains(market.getMarketingId())).collect(Collectors.groupingBy(TradeMarketingDTO::getMarketingId));

        // 获取参与活动的所有商品
        List<String> skuIds = comMarketingVOS.stream().flatMap(joinMarketGoodsSku -> joinMarketGoodsSku.getSkuIds().stream()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(skuIds)){
            log.info("findDiscountPriceDetailByMarketingType ==> CustomerId->{} 未查询到参与活动的商品",customer.getCustomerId());
            return BaseResponse.success(responseList);
        }
        BaseResponse<GoodsInfoListByIdsResponse> goodsInfoListResponse = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build());
        List<GoodsInfoVO> goodsInfoList = goodsInfoListResponse.getContext().getGoodsInfos();

        // 封装返回结果集
        matchMarketingMap.forEach((k,v) -> {
            // 查询当前活动明细
            MarketingVO marketingVO = marketingVOList.stream().filter(mk ->  k.equals(mk.getMarketingId())).findFirst().get();
            // 参与当前活动的SKU和商品信息
            List<String> currentMarketGoodSkus = v.stream().flatMap(joinMarketGoodsSku -> joinMarketGoodsSku.getSkuIds().stream()).collect(Collectors.toList());
            List<GoodsInfoVO> infoVOList = goodsInfoList.stream().filter(item -> currentMarketGoodSkus.contains(item.getGoodsInfoId())).collect(Collectors.toList());
            List<DiscountPriceDetailResponse> tempResponse = new ArrayList<>();
            for (GoodsInfoVO goodsInfoVO : infoVOList) {
                Optional<TradeItemRequest> itemVOOptional = tradeItemRequestList.stream().filter(item -> item.getSkuId().equals(goodsInfoVO.getGoodsInfoId())).findFirst();
                if (itemVOOptional.isPresent()) {
                    DiscountPriceDetailResponse discountPriceDetailResponse = new DiscountPriceDetailResponse();
                    TradeItemRequest tradeItemVO = itemVOOptional.get();

                    discountPriceDetailResponse.setGoodsInfoName(goodsInfoVO.getGoodsInfoName());
                    discountPriceDetailResponse.setGoodsSubtitle(goodsInfoVO.getGoods().getGoodsSubtitle());
                    discountPriceDetailResponse.setBuyNum(tradeItemVO.getNum());
                    discountPriceDetailResponse.setGoodsImageUrl(goodsInfoVO.getGoods().getGoodsImg());
                    discountPriceDetailResponse.setTotalPrice(goodsInfoVO.getGoods().getMarketPrice());
                    tempResponse.add(discountPriceDetailResponse);
                }
            }
            // 计算商品折扣金额
            // 活动未开始 | 活动已结束 | 活动已暂停
            if(now.isBefore(marketingVO.getBeginTime()) || now.isAfter(marketingVO.getEndTime()) || marketingVO.getIsPause() == BoolFlag.YES){
                log.info("DiscountPriceDetailForPurchaseNow->calcDiscountPrice->marketingId:{}",marketingVO.getMarketingId());
                return;
            }
            MarketingSubType subType = marketingVO.getSubType();
            spliceActivityName(marketingVO,tempResponse);
            if(marketingType == MarketingType.DISCOUNT) {
                MarketingFullDiscountLevelVO fullDiscountLevel = getFullDiscountLevel(marketingVO.getMarketingFullDiscountLevels(), tempResponse, subType);
                calcFullDiscountPrice(fullDiscountLevel,tempResponse,subType,marketingVO.getIsOverlap());
            }else if(marketingType == MarketingType.REDUCTION){
                MarketingFullReductionLevelVO fullReductionLevel = getFullReductionLevel(marketingVO.getMarketingFullReductionLevels(), tempResponse, subType);
                calcFullReductionPrice(fullReductionLevel,tempResponse,subType,marketingVO.getIsOverlap());
            }
            responseList.addAll(tempResponse);
        });
        return BaseResponse.success(responseList);
    }

    private void spliceActivityName(MarketingVO marketingVO,List<DiscountPriceDetailResponse> discountPriceDetailResponseList){
        MarketingType marketingType = marketingVO.getMarketingType();
        MarketingSubType subType = marketingVO.getSubType();
        StringBuilder activityName = new StringBuilder("");
        if(marketingType == MarketingType.DISCOUNT ){ // 满折
            if(CollectionUtils.isNotEmpty(marketingVO.getMarketingFullDiscountLevels())){
                MarketingFullDiscountLevelVO fullDiscountLevelVO = getFullDiscountLevel(marketingVO.getMarketingFullDiscountLevels(),discountPriceDetailResponseList,subType);
                activityName.append("满");
                if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){ // 满数量折扣
                    activityName.append(fullDiscountLevelVO.getFullCount());
                    activityName.append("件打");
                }else if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT){ // 满金额折扣
                    activityName.append(fullDiscountLevelVO.getFullAmount());
                    activityName.append("元打");
                }
                activityName.append(fullDiscountLevelVO.getDiscount().multiply(BigDecimal.valueOf(10)).setScale(1,BigDecimal.ROUND_DOWN));
                activityName.append("折");
            }
        }else if(marketingType == MarketingType.REDUCTION){// 满减
            if(CollectionUtils.isNotEmpty(marketingVO.getMarketingFullReductionLevels())){
                MarketingFullReductionLevelVO marketingFullReductionLevelVO = getFullReductionLevel(marketingVO.getMarketingFullReductionLevels(),discountPriceDetailResponseList,subType);
                activityName.append("满");
                if(subType == MarketingSubType.REDUCTION_FULL_AMOUNT){// 满金额减
                    activityName.append(marketingFullReductionLevelVO.getFullAmount());
                    activityName.append("元减");
                }else if(subType == MarketingSubType.REDUCTION_FULL_COUNT){// 满数量减
                    activityName.append(marketingFullReductionLevelVO.getFullCount());
                    activityName.append("件减");
                }
                activityName.append(marketingFullReductionLevelVO.getReduction().setScale(2,BigDecimal.ROUND_DOWN));
                activityName.append("元");
            }
        }
        discountPriceDetailResponseList.forEach(item -> item.setActivityName(activityName.toString()));
    }

    /**
     * 套装购买
     */
    @ApiOperation(value = "套装购买")
    @RequestMapping(value = "/suit-buy", method = RequestMethod.POST)
    @LcnTransaction
    public BaseResponse suitBuy(@RequestBody @Valid SuitBuyRequest confirmRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        //查询套装
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(confirmRequest.getMarketingId());
        MarketingGetByIdForCustomerResponse context = marketingQueryProvider.getByIdForCustomer(marketingGetByIdRequest).getContext();
        if(Objects.isNull(context) || Objects.isNull(context.getMarketingForEndVO())){
            throw new SbcRuntimeException("K-080201");
        }
        MarketingForEndVO marketingForEndVO = context.getMarketingForEndVO();
        if(CollectionUtils.isEmpty(marketingForEndVO.getMarketingSuitDetialVOList())){
            throw new SbcRuntimeException("K-080201");
        }

        Integer buyCount = suitOrderTempProvider.getSuitBuyCountByCustomerAndMarketingId(SuitOrderTempQueryRequest
                .builder()
                .customerId(customer.getCustomerId())
                .marketingId(confirmRequest.getMarketingId())
                .build()).getContext();

        if(Objects.isNull(confirmRequest.getBuyCount())){
            confirmRequest.setBuyCount(1L);
        }

        if(context.getMarketingForEndVO().getSuitLimitNum() < (buyCount + confirmRequest.getBuyCount())){
            throw new SbcRuntimeException("K-050119");
        }

        //封装订单商品
        List<ImmediateBuyRequest> buyRequests = Lists.newArrayList();
        List<TradeMarketingDTO> tradeMarketingList = Lists.newArrayList();

        List<GoodsInfoStockRequest> goodsInfoStockRequestList = Lists.newArrayList();

        marketingForEndVO.getMarketingSuitDetialVOList().forEach(s->{
            ImmediateBuyRequest immediateBuyRequest = new ImmediateBuyRequest();
            immediateBuyRequest.setSkuId(s.getGoodsInfoId());
            immediateBuyRequest.setNum(context.getMarketingForEndVO().getSuitBuyNum().longValue() * confirmRequest.getBuyCount());
            immediateBuyRequest.setWareId(confirmRequest.getWareId());
            GoodsInfoStockRequest goodsInfoStockRequest = new GoodsInfoStockRequest();
            goodsInfoStockRequest.setSkuId(s.getGoodsInfoId());
            goodsInfoStockRequest.setByCount(context.getMarketingForEndVO().getSuitBuyNum().longValue() * confirmRequest.getBuyCount());
            buyRequests.add(immediateBuyRequest);
            goodsInfoStockRequestList.add(goodsInfoStockRequest);
        });

        List<TradeItemDTO> tradeItems = KsBeanUtil.convertList(buyRequests, TradeItemDTO.class);

        // 1.获取商品信息
        List<String> skuIds = tradeItems.stream().map(TradeItemDTO::getSkuId).collect(Collectors.toList());
        GoodsInfoResponse response = getGoodsResponse(skuIds, customer);

        GoodsInfoVO goodsInfo = response.getGoodsInfos().stream().findFirst().orElse(null);

        // 2.填充商品区间价
        tradeItems = KsBeanUtil.convertList(
                verifyQueryProvider.verifyGoods(
                        new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                                KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                goodsInfo.getStoreId(), true, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);

        //赠品id
        PurchaseGetGoodsMarketingRequest purchaseGetGoodsMarketingRequest = new
                PurchaseGetGoodsMarketingRequest();
        purchaseGetGoodsMarketingRequest.setGoodsInfos(response.getGoodsInfos());
        purchaseGetGoodsMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
        purchaseGetGoodsMarketingRequest.setWareId(confirmRequest.getWareId());
        PurchaseGetGoodsMarketingResponse marketingResponse = shopCartQueryProvider.getGoodsMarketing
                (purchaseGetGoodsMarketingRequest).getContext();

        //限定区域指定商品限购校验
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
        List<TradeItemDTO> finalTradeItems = tradeItems;
        response.getGoodsInfos().forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
                List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())){
                    if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
                        log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                        if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()){
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
                        }
                    }
                }
            }

            List<MarketingViewVO> marketings = marketingResponse.getMap().get(goodsInfoVO.getGoodsInfoId());
            TradeItemDTO tradeItemDTO = finalTradeItems.stream().filter(t -> t.getSkuId().equals(goodsInfoVO.getGoodsInfoId())).findFirst().orElse(null);
            if(CollectionUtils.isNotEmpty(marketings) && Objects.nonNull(tradeItemDTO)){
                /**是否匹配到仓，暂时写死*/
                TradeMarketingDTO tradeMarketing = chooseDefaultMarketing(tradeItemDTO, marketings, true,null);
                if (tradeMarketing != null) {
                    tradeMarketingList.add(tradeMarketing);
                }
            }
        });

        if(CollectionUtils.isNotEmpty(tradeMarketingList)){
            tradeMarketingList.forEach(t->{
                if(CollectionUtils.isNotEmpty(t.getGiftSkuIds())){
                    t.getGiftSkuIds().forEach(g->{
                        if(CollectionUtils.isNotEmpty(goodsInfoStockRequestList)){
                            GoodsInfoStockRequest goodsInfoStockRequest = goodsInfoStockRequestList.stream().filter(gi -> gi.getSkuId().equals(g)).findFirst().orElse(null);
                            if(Objects.nonNull(goodsInfoStockRequest)){
                                goodsInfoStockRequest.setByCount(goodsInfoStockRequest.getByCount() + 1);
                            }else{
                                GoodsInfoStockRequest StockRequest = new GoodsInfoStockRequest();
                                StockRequest.setSkuId(g);
                                //赠品默认为1；暂时写死
                                StockRequest.setByCount(1L);
                                goodsInfoStockRequestList.add(StockRequest);
                            }
                        }else{
                            GoodsInfoStockRequest goodsInfoStockRequest = new GoodsInfoStockRequest();
                            goodsInfoStockRequest.setSkuId(g);
                            //赠品默认为1；暂时写死
                            goodsInfoStockRequest.setByCount(1L);
                            goodsInfoStockRequestList.add(goodsInfoStockRequest);
                        }
                    });
                }
            });
        }

        //查询库存
        BaseResponse<GetGoodsInfoStockByIdResponse> goodsInfoStock = goodsInfoQueryProvider.findGoodsInfoStock(GoodsInfoStockByIdsRequest.builder().goodsInfo(goodsInfoStockRequestList).build());

        if(Objects.nonNull(goodsInfoStock.getContext()) && CollectionUtils.isNotEmpty(goodsInfoStock.getContext().getGoodsInfos())){
            throw new SbcRuntimeException("k-030301");
        }

        return tradeItemProvider.snapshot(
                TradeItemSnapshotRequest.builder()
                        .customerId(customer.getCustomerId())
                        .tradeItems(tradeItems)
                        .suitBuyCount(Objects.isNull(confirmRequest.getBuyCount()) ? 1L : confirmRequest.getBuyCount())
                        .marketingId(confirmRequest.getMarketingId())
                        .tradeMarketingList(tradeMarketingList)
                        .skuList(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class)).build());
    }

    /**
     * 选择商品默认的营销，以及它的level
     */
    private TradeMarketingDTO chooseDefaultMarketing(TradeItemDTO tradeItem, List<MarketingViewVO> marketings, boolean matchWareHouseFlag,List<String> choseGiftIds) {

        BigDecimal total = tradeItem.getPrice().multiply(new BigDecimal(tradeItem.getNum()));
        Long num = tradeItem.getNum();
        //判断是否拆箱
        if(Objects.nonNull(tradeItem.getDevanningId())){
            //查询拆箱规格
            BigDecimal multiply = tradeItem.getDivisorFlag().multiply(BigDecimal.valueOf(tradeItem.getNum()));
            num = multiply.setScale(0, BigDecimal.ROUND_DOWN).longValue();
//            DevanningGoodsInfoByIdResponse context = devanningGoodsInfoQueryProvider.getInfoById(DevanningGoodsInfoByIdRequest.builder().devanningId(tradeItem.getDevanningId()).build()).getContext();
//            if(Objects.isNull(context) || Objects.isNull(context.getDevanningGoodsInfoVO())){
//                DevanningGoodsInfoVO devanningGoodsInfoVO = context.getDevanningGoodsInfoVO();
//                BigDecimal multiply = devanningGoodsInfoVO.getDivisorFlag().multiply(BigDecimal.valueOf(tradeItem.getNum()));
//                //小于1则取0，大于1则下取
//                num = multiply.setScale(0, BigDecimal.ROUND_DOWN).longValue();
//            }
        }

        TradeMarketingDTO tradeMarketing = new TradeMarketingDTO();
        tradeMarketing.setSkuIds(Arrays.asList(tradeItem.getSkuId()));

        if (CollectionUtils.isNotEmpty(marketings)) {
            for (int i = 0; i < marketings.size(); i++) {
                MarketingViewVO marketing = marketings.get(i);
                if (CollectionUtils.isEmpty(choseGiftIds)) {
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
                                            if (CollectionUtils.isNotEmpty(choseGiftIds)) {
                                                giftIds = choseGiftIds;
                                            } else {
                                                giftIds = Collections.singletonList(giftIds.get(0));
                                            }
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
                                            if (CollectionUtils.isNotEmpty(choseGiftIds)) {
                                                giftIds = choseGiftIds;
                                            } else {
                                                giftIds = Collections.singletonList(giftIds.get(0));
                                            }
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
                } else {
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
                                            if (CollectionUtils.isNotEmpty(choseGiftIds)) {
                                                giftIds = choseGiftIds;
                                            } else {
                                                giftIds = Collections.singletonList(giftIds.get(0));
                                            }
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
                                            if (CollectionUtils.isNotEmpty(choseGiftIds)) {
                                                giftIds = choseGiftIds;
                                            } else {
                                                giftIds = Collections.singletonList(giftIds.get(0));
                                            }
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
    public BaseResponse<List<TradeCommitResultVO>> commit(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        if (true){
            throw new SbcRuntimeException("当前版本不可下单，请更新最新版");
        }
        //设置订单来源渠道
        tradeCommitRequest.setSourceChannel(commonUtil.getChannel(HttpUtil.getRequest()));
        RLock rLock = redissonClient.getFairLock(commonUtil.getOperatorId());
        rLock.lock();
        List<TradeCommitResultVO> successResults;
        try {

//            //根据收货地址，查询当前的用户是否配置了第四级街道地址
//            CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
//                    .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
//            if(response.getTwonId() == null || response.getTwonId().equals("") || Objects.isNull(response.getTwonId())
//                    || response.getTwonName() == null || response.getTwonName().equals("") || Objects.isNull(response.getTwonName())){
//                throw new SbcRuntimeException("K-888888","地址功能优化，需要重新编辑全部地址，否则无法提交订单");
//            }

            Operator operator = commonUtil.getOperator();
            boolean checkMatchFlag = true;
            //如果都是自提商品无需验证仓库匹配范围
            List<DeliverWay> collect = tradeCommitRequest.getStoreCommitInfoList().stream().map(StoreCommitInfoDTO::getDeliverWay).distinct().collect(Collectors.toList());
            if (collect.size() == 1 && collect.contains(DeliverWay.PICK_SELF)) {
                checkMatchFlag = false;
            }
            if (checkMatchFlag) {
                WareHouseVO wareHouseVO = this.matchWareStore(tradeCommitRequest.getCityCode());
                tradeCommitRequest.setWareId(wareHouseVO.getWareId());
                tradeCommitRequest.setWareHouseCode(wareHouseVO.getWareCode());
            }

            tradeCommitRequest.setOperator(operator);
            List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest
                    .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();

            //限定区域指定商品限购校验
//            checkGoodsNumsForCommitOrder(tradeCommitRequest,tradeItemGroups);
//todo 【分销逻辑sql暂时去掉，后续有需要再加】
//            DefaultFlag storeBagsFlag = tradeItemGroups.get(0).getStoreBagsFlag();
//            if (DefaultFlag.NO.equals(storeBagsFlag) && !distributionService.checkInviteeIdEnable()) {
//                // 非开店礼包情况下，判断小店状态不可用
//                throw new SbcRuntimeException("K-080301");
//            }

            // 邀请人不是分销员时，清空inviteeId
            DistributeChannel distributeChannel = commonUtil.getDistributeChannel();
//            if (StringUtils.isNotEmpty(distributeChannel.getInviteeId()) &&
//                    !distributionService.isDistributor(distributeChannel.getInviteeId())) {
//                distributeChannel.setInviteeId(null);
//            }
//            // 设置下单用户，是否分销员
//            if (distributionService.isDistributor(operator.getUserId())) {
//                tradeCommitRequest.setIsDistributor(DefaultFlag.YES);
//            }
            tradeCommitRequest.setDistributeChannel(distributeChannel);
//            tradeCommitRequest.setShopName(distributionCacheService.getShopName());
//
//            // 设置分销设置开关
//            tradeCommitRequest.setOpenFlag(distributionCacheService.queryOpenFlag());
//            tradeCommitRequest.getStoreCommitInfoList().forEach(item ->
//                    item.setStoreOpenFlag(distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString()))
//            );
//            // 验证小店商品
//            validShopGoods(tradeItemGroups, tradeCommitRequest.getDistributeChannel());

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

//                if (DeliverWay.LOGISTICS.equals(t.getDeliverWay())){
//                    if (Objects.isNull(t.getLogisticsInfo())) {
//                        //物流信息未填写
//                        throw new SbcRuntimeException("K-020010");
//                    }
//                }
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
            validPickUpPoint(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest);
            //验证配送到家距离
            validHomeDelivery(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest.getConsigneeId(), tradeCommitRequest.getWareId(),operator.getUserId());

            successResults = tradeProvider.commit(tradeCommitRequest).getContext().getTradeCommitResults();
            //如果是秒杀商品订单更新会员已抢购该商品数量
            if (Objects.nonNull(tradeCommitRequest.getIsFlashSaleGoods()) && tradeCommitRequest.getIsFlashSaleGoods()) {
                TradeVO trade =
                        tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(successResults.get(0).getTid()).build()).getContext().getTradeVO();
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





    /**
     * 提交订单，用于生成订单操作
     */
    @ApiOperation(value = "提交订单，用于生成订单操作")
    @RequestMapping(value = "/commit-all", method = RequestMethod.POST)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<List<TradeCommitResultVO>> commitAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        if (true){
            throw new SbcRuntimeException("当前版本不可下单，请更新最新版");
        }
        //设置订单来源渠道
        tradeCommitRequest.setSourceChannel(commonUtil.getChannel(HttpUtil.getRequest()));
        RLock rLock = redissonClient.getFairLock(commonUtil.getOperatorId());
        rLock.lock();
        List<TradeCommitResultVO> successResults;
        try {

            //根据收货地址，查询当前的用户是否配置了第四级街道地址
//            CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
//                    .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
//            if(response.getTwonId() == null || response.getTwonId().equals("") || Objects.isNull(response.getTwonId())
//                    || response.getTwonName() == null || response.getTwonName().equals("") || Objects.isNull(response.getTwonName())){
//                throw new SbcRuntimeException("K-888888","地址功能优化，需要重新编辑全部地址，否则无法提交订单");
//            }

            Operator operator = commonUtil.getOperator();
            boolean checkMatchFlag = true;
            //如果都是自提商品无需验证仓库匹配范围
            List<DeliverWay> collect = tradeCommitRequest.getStoreCommitInfoList().stream().map(StoreCommitInfoDTO::getDeliverWay).distinct().collect(Collectors.toList());
            if (collect.size() == 1 && collect.contains(DeliverWay.PICK_SELF)) {
                checkMatchFlag = false;
            }
            if (checkMatchFlag) {
                WareHouseVO wareHouseVO = this.matchWareStore(tradeCommitRequest.getCityCode());
                tradeCommitRequest.setWareId(wareHouseVO.getWareId());
                tradeCommitRequest.setWareHouseCode(wareHouseVO.getWareCode());
            }

            tradeCommitRequest.setOperator(operator);
            List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                    .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();

            //限定区域指定商品限购校验
//            checkGoodsNumsForCommitOrder(tradeCommitRequest,tradeItemGroups);

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
            validShopGoods(tradeItemGroups, tradeCommitRequest.getDistributeChannel());

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

//                if (DeliverWay.LOGISTICS.equals(t.getDeliverWay())){
//                    if (Objects.isNull(t.getLogisticsInfo())) {
//                        //物流信息未填写
//                        throw new SbcRuntimeException("K-020010");
//                    }
//                }
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
            validPickUpPoint(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest);
            //验证配送到家距离
            validHomeDelivery(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest.getConsigneeId(), tradeCommitRequest.getWareId(),operator.getUserId());

            successResults = tradeProvider.commitAll(tradeCommitRequest).getContext().getTradeCommitResults();
            //如果是秒杀商品订单更新会员已抢购该商品数量
            if (Objects.nonNull(tradeCommitRequest.getIsFlashSaleGoods()) && tradeCommitRequest.getIsFlashSaleGoods()) {
                TradeVO trade =
                        tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(successResults.get(0).getTid()).build()).getContext().getTradeVO();
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

    /**
     * 提交订单，用于生成订单操作
     */
    @ApiOperation(value = "提交订单，用于生成订单操作")
    @RequestMapping(value = "/commitDevanning-all", method = RequestMethod.POST)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<List<TradeCommitResultVO>> commitDevanningAll(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        StopWatch stopWatch = new StopWatch();
        //设置订单来源渠道
        tradeCommitRequest.setSourceChannel(commonUtil.getChannel(HttpUtil.getRequest()));
        tradeCommitRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        tradeCommitRequest.setBulkWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));
        log.info("commitDevanningAll bulkWareId:{}",commonUtil.getBulkWareId(HttpUtil.getRequest()));
        Operator operator = commonUtil.getOperator();
        RLock rLock = redissonClient.getFairLock(Constants.OVER_BOOKING+operator.getUserId());
        rLock.lock();
        List<TradeCommitResultVO> successResults;
        try {
            stopWatch.start("commitDevanningAll统计4级地址验证耗时");
            //根据收货地址，查询当前的用户是否配置了第四级街道地址
            CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                    .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
            if(response.getTwonId() == null || response.getTwonId().equals("") || Objects.isNull(response.getTwonId())
                    || response.getTwonName() == null || response.getTwonName().equals("") || Objects.isNull(response.getTwonName())){
                throw new SbcRuntimeException("K-888888","地址功能优化，需要重新编辑全部地址，否则无法提交订单");
            }
            stopWatch.stop();
            boolean checkMatchFlag = true;
            //如果都是自提商品无需验证仓库匹配范围
            List<DeliverWay> collect = tradeCommitRequest.getStoreCommitInfoList().stream().map(StoreCommitInfoDTO::getDeliverWay).distinct().collect(Collectors.toList());
            if (collect.size() == 1 && collect.contains(DeliverWay.PICK_SELF)) {
                checkMatchFlag = false;
            }
            if (checkMatchFlag) {
                WareHouseVO wareHouseVO = this.matchWareStore(tradeCommitRequest.getCityCode());
                tradeCommitRequest.setWareId(wareHouseVO.getWareId());
                tradeCommitRequest.setWareHouseCode(wareHouseVO.getWareCode());
                tradeCommitRequest.setWareName(wareHouseVO.getWareName());
            }else{
                WareHouseVO wareHouse = commonUtil.getWareHouse(HttpUtil.getRequest());
                tradeCommitRequest.setWareId(wareHouse.getWareId());
                tradeCommitRequest.setWareHouseCode(wareHouse.getWareCode());
                tradeCommitRequest.setWareName(wareHouse.getWareName());
            }

            tradeCommitRequest.setOperator(operator);
            stopWatch.start("commitDevanningAll获取订单快照耗时");
            List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                    .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();
            //区域限购验证
            stopWatch.stop();
            stopWatch.start("commitDevanningAll获取客户地址耗时");
            CustomerDeliveryAddressResponse deliveryAddress=commonUtil.getDeliveryAddress();
            stopWatch.stop();
            tradeCommitRequest.setProvinceId(deliveryAddress.getProvinceId());
            tradeCommitRequest.setCityId(deliveryAddress.getCityId());
//            checkGoodsForCommitOrder(tradeItemGroups,deliveryAddress);
            //限定区域指定商品限购校验
//            checkGoodsNumsForCommitOrder(tradeCommitRequest,tradeItemGroups);

            //校验是否还有库存 移动到order
//            checkGoodsStockForValid(tradeItemGroups);

            DefaultFlag storeBagsFlag = tradeItemGroups.get(0).getStoreBagsFlag();
            if (DefaultFlag.NO.equals(storeBagsFlag) && !distributionService.checkInviteeIdEnable()) {
                // 非开店礼包情况下，判断小店状态不可用
                throw new SbcRuntimeException("K-080301");
            }

            // 邀请人不是分销员时，清空inviteeId
            stopWatch.start("commitDevanningAll判断邀请人员和分销员");
            DistributeChannel distributeChannel = commonUtil.getDistributeChannel();
            if (StringUtils.isNotEmpty(distributeChannel.getInviteeId()) &&
                    !distributionService.isDistributor(distributeChannel.getInviteeId())) {
                distributeChannel.setInviteeId(null);
            }
            // 设置下单用户，是否分销员
            if (distributionService.isDistributor(operator.getUserId())) {
                tradeCommitRequest.setIsDistributor(DefaultFlag.YES);
            }
            stopWatch.stop();
            tradeCommitRequest.setDistributeChannel(distributeChannel);
            tradeCommitRequest.setShopName(distributionCacheService.getShopName());

            // 设置分销设置开关
            tradeCommitRequest.setOpenFlag(distributionCacheService.queryOpenFlag());
            tradeCommitRequest.getStoreCommitInfoList().forEach(item ->
                    item.setStoreOpenFlag(distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString()))
            );
            stopWatch.start("commitDevanningAll验证小店商品");
            // 验证小店商品
            validShopGoods(tradeItemGroups, tradeCommitRequest.getDistributeChannel());
            stopWatch.stop();
            stopWatch.start("commitDevanningAll循环店铺判断物流消耗时间");
            tradeCommitRequest.getStoreCommitInfoList().stream().forEach(t -> {
                DeliverWay.checkDeliveryWay(t.getDeliverWay());
                if (DeliverWay.LOGISTICS.equals(t.getDeliverWay())|| DeliverWay.SPECIFY_LOGISTICS.equals(t.getDeliverWay())) {
                    LogisticsInfoDTO logisticsInfo = t.getLogisticsInfo();
                    if (Objects.nonNull(logisticsInfo) && StringUtils.isNotEmpty(logisticsInfo.getId())) {
                        String id = logisticsInfo.getId();
                        LogisticsCompanyByIdResponse context =
                                logisticsCompanyQueryProvider.getById(LogisticsCompanyByIdRequest.builder().id(Long.valueOf(id)).build()).getContext();
                        if (Objects.isNull(context) || Objects.isNull(context.getLogisticsCompanyVO())) {
                            throw new SbcRuntimeException("K-170003");
                        }
                    }else{
                        throw new SbcRuntimeException("K-170003");
                    }
                    if(StringUtils.isBlank(logisticsInfo.getReceivingPoint())){
                        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(t.getStoreId()).build()).getContext();
                        if(storeInfoResponse!=null){
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"店铺["+storeInfoResponse.getStoreName()+"]物流收货站点不能为空");
                        }
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"配送方式为托运部或指定专线时，收货站点不能为空");
                    }
                }
                if (DeliverWay.TO_DOOR_PICK.equals(t.getDeliverWay())) {
                    if (Objects.isNull(t.getNetWorkVO())||Objects.isNull(t.getNetWorkVO().getNetworkId())) {
                            throw new SbcRuntimeException("K-170004","配送方式为上门自提时自提点信息不能为空");
                    }
                }

//                if (DeliverWay.LOGISTICS.equals(t.getDeliverWay())){
//                    if (Objects.isNull(t.getLogisticsInfo())) {
//                        //物流信息未填写
//                        throw new SbcRuntimeException("K-020010");
//                    }
//                }
                //自提的地址，设置wareCode
                if (Objects.nonNull(t.getWareId())) {
                    WareHouseVO wareHouseVO1 = commonUtil.getWareHouseByWareId(t.getWareId());
                    t.setWareHouseCode(wareHouseVO1.getWareCode());
                } else {
                    t.setWareId(tradeCommitRequest.getWareId());
                    t.setWareHouseCode(tradeCommitRequest.getWareHouseCode());
                }
            });
            stopWatch.stop();
            stopWatch.start("commitDevanningAll验证自提商品验证自提点合法性，如果合法塞入自提点信息消耗时间");
            //自提商品验证自提点合法性，如果合法塞入自提点信息
            validPickUpPoint(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest);
            //验证配送到家距离
            validHomeDelivery(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest.getConsigneeId(), tradeCommitRequest.getWareId(),operator.getUserId());
            stopWatch.stop();
            stopWatch.start("commitDevanningAll下单接口总耗时");
            successResults = tradeProvider.submitMallTrades(tradeCommitRequest).getContext().getTradeCommitResults();
            stopWatch.stop();

            //如果是秒杀商品订单更新会员已抢购该商品数量
            if (Objects.nonNull(tradeCommitRequest.getIsFlashSaleGoods()) && tradeCommitRequest.getIsFlashSaleGoods()) {
                stopWatch.start("commitDevanningAll如果是秒杀商品订单更新会员已抢购该商品数量");
                TradeVO trade =
                        tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(successResults.get(0).getTid()).build()).getContext().getTradeVO();
                String havePanicBuyingKey =
                        RedisKeyConstant.FLASH_SALE_GOODS_HAVE_PANIC_BUYING + operator.getUserId() + trade.getTradeItems().get(0).getFlashSaleGoodsId();
                redisService.setString(havePanicBuyingKey,
                        Integer.valueOf(StringUtils.isNotBlank(redisService.getString(havePanicBuyingKey)) ?
                                redisService.getString(havePanicBuyingKey) : "0") + trade.getTradeItems().get(0).getNum() + "");
                //删除抢购资格
                redisService.delete(RedisKeyConstant.FLASH_SALE_GOODS_QUALIFICATIONS + commonUtil.getOperatorId() + trade.getTradeItems().get(0).getSpuId());
                stopWatch.stop();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            rLock.unlock();
        }
        log.info("commitDevanningAll总时长"+stopWatch.prettyPrint());
        return BaseResponse.success(successResults);
    }


    /**
     * 提交订单校验配送到店订单
     */
    @ApiOperation(value = "提交订单校验配送到店订单")
    @RequestMapping(value = "/checkSubmitTradeDeliveryToStore", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse checkSubmitTradeDeliveryToStore(@RequestBody  MarketTradeInfoDTO marketTradeInfoDTO) {
        if(marketTradeInfoDTO==null||marketTradeInfoDTO.getAddressId()==null||CollectionUtils.isEmpty(marketTradeInfoDTO.getMarketlist())){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"参数错误");
        }
        return tradeProvider.checkSubmitTradeDeliveryToStore(marketTradeInfoDTO);
    }


    private void checkGoodsStockForValid(List<TradeItemGroupVO> tradeItemGroups) {
        List<TradeItemVO> tradeItems = new ArrayList<>();
        tradeItemGroups.forEach(t -> tradeItems.addAll(t.getTradeItems()));
        List<String> skuIds = tradeItems.stream().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        Map<String, BigDecimal> collect = historyTownShipOrderQueryProvider.getById(HistoryTownShipOrderStockRequest.builder().skuids(skuIds)
                .wareId(1L)//无限可加可不加
                .build()).getContext().getTrueStockVO()
                .stream().collect(Collectors.toMap(TrueStockVO::getSkuid, g -> g.getStock(), (a, b) -> a));


//        Map<String, BigDecimal> goodsInfoMap = goodsInfoQueryProvider.listGoodsInfoAndStcokByIds(
//                GoodsInfoAndStockListByIdsRequest.builder().goodsInfoIds(skuIds).matchWareHouseFlag(true).build()
//        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g.getStock()));
        tradeItems.forEach(v->{
            BigDecimal stock = collect.getOrDefault(v.getSkuId(), BigDecimal.ZERO);
            if (BigDecimal.valueOf(v.getNum()).compareTo(stock)>0){
                throw new SbcRuntimeException("k-030301","系统库存校验失败请重新到购物车页面选择数量");
            }
        });
    }


    /**
     * 提货
     */
    @ApiOperation(value = "提货，用于生成订单操作")
    @RequestMapping(value = "/takeGoodsCommit", method = RequestMethod.POST)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<List<TradeCommitResultVO>> takeGoods(@RequestBody @Valid TradeCommitRequest tradeCommitRequest) {
        RLock rLock = redissonClient.getFairLock(commonUtil.getOperatorId());
        rLock.lock();
        List<TradeCommitResultVO> successResults;
        try {

            Operator operator = commonUtil.getOperator();
            boolean checkMatchFlag = true;
            //如果都是自提商品无需验证仓库匹配范围
            List<DeliverWay> collect = tradeCommitRequest.getStoreCommitInfoList().stream().map(StoreCommitInfoDTO::getDeliverWay).distinct().collect(Collectors.toList());
            if (collect.size() == 1 && collect.contains(DeliverWay.PICK_SELF)) {
                checkMatchFlag = false;
            }
            if (checkMatchFlag) {
                WareHouseVO wareHouseVO = this.matchWareStore(tradeCommitRequest.getCityCode());
                tradeCommitRequest.setWareId(wareHouseVO.getWareId());
                tradeCommitRequest.setWareHouseCode(wareHouseVO.getWareCode());
            }


            tradeCommitRequest.setOperator(operator);
//            List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest
//                    .builder().customerId(commonUtil.getOperatorId()).build()).getContext().getTradeItemGroupList();
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

//                if (DeliverWay.LOGISTICS.equals(t.getDeliverWay())){
//                    if (Objects.isNull(t.getLogisticsInfo())) {
//                        //物流信息未填写
//                        throw new SbcRuntimeException("K-020010");
//                    }
//                }
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
            validPickUpPoint(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest);
            //验证配送到家距离
            validHomeDelivery(tradeCommitRequest.getStoreCommitInfoList(), tradeCommitRequest.getConsigneeId(), tradeCommitRequest.getWareId(),operator.getUserId());

            successResults = tradeProvider.takeGoods(tradeCommitRequest).getContext().getTradeCommitResults();
            //如果是秒杀商品订单更新会员已抢购该商品数量
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        checkUnauthorized(tid, trade);
        boolean hasImg = CollectionUtils.isNotEmpty(trade.getTradeItems());
        BigDecimal bigDecimalStream = BigDecimal.ZERO;
        if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
            bigDecimalStream = bigDecimalStream.add(trade.getTradePrice().getDeliveryPrice());
        }else{
            bigDecimalStream = trade.getTradeItems().stream()
                    .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                    .map(TradeItemVO::getSplitPrice)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            if(Objects.nonNull(trade.getTradePrice().getDeliveryPrice())){
                bigDecimalStream = bigDecimalStream.add(trade.getTradePrice().getDeliveryPrice());
            }
            if(Objects.nonNull(trade.getTradePrice().getPackingPrice())){
                bigDecimalStream = bigDecimalStream.add(trade.getTradePrice().getPackingPrice());
            }
        }

        return BaseResponse.success(new TradeCommitResultVO(tid, trade.getParentId(), trade.getTradeState(),
                trade.getPaymentOrder(),
                bigDecimalStream,
                trade.getOrderTimeOut(),
                trade.getSupplier().getStoreName(),
                trade.getSupplier().getIsSelf(),
                hasImg ? trade.getTradeItems().get(0).getPic() : null));
    }

    @ApiOperation(value = "检查订单商品信息（上下架，库存待），营销活动")
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public BaseResponse<CheckTradeResponse> checkTrade(@RequestBody @Valid CheckTradeRequest req) {
        if (StringUtils.isNotEmpty(req.getTid()) && req.getTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {
            return BaseResponse.success(new CheckTradeResponse());
        }
        //商家入驻囤货订单不进检查流程
        if (StringUtils.isNotEmpty(req.getParentTid()) && req.getParentTid().startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {
            return BaseResponse.success(new CheckTradeResponse());
        }
        //检查订单条目：1 促销活动 2 买商品赠券 3 使用的优惠券已过期
        TradeCheckResponse tradeCheckResponse = tradeQueryProvider.checkTrade(TradeCheckRequest.builder()
                        .parentTid(req.getParentTid())
                        .tid(req.getTid())
                        .build())
                .getContext();
        return BaseResponse.success(KsBeanUtil.convert(tradeCheckResponse, CheckTradeResponse.class));
    }

    @ApiOperation(value = "验证配送到家标志位")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkDeliveryHomeFlag", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeFlagResponse> checkDeliveryHomeFlag(@RequestBody @Valid DeliveryHomeFlagRequest req, HttpServletRequest request) {
        DeliveryHomeFlagResponse deliveryHomeFlagResponse = new DeliveryHomeFlagResponse();
        req.setWareId(commonUtil.getWareId(request));
        deliveryHomeFlagResponse.setFlag(checkDistance(req.getWareId(), req.getCustomerDeliveryAddressId()));
        return BaseResponse.success(deliveryHomeFlagResponse);
    }

    @ApiOperation(value = "验证配送到家范围")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkHomeFlag", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeFlagResponse> checkHomeFlag(@RequestBody @Valid DeliveryHomeFlagRequest req, HttpServletRequest request) {
        req.setWareId(commonUtil.getWareId(request));
        DeliveryHomeFlagResponse response = checkDeliveryArea(req.getWareId(), req.getCustomerDeliveryAddressId(), commonUtil.getOperator().getUserId(),commonUtil.getStoreIdWithDefault());
        return BaseResponse.success(response);
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
        giftResp = tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
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
                    TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest =  new TradeQueryPurchaseInfoRequest();
                    tradeQueryPurchaseInfoRequest.setTradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class));
                    tradeQueryPurchaseInfoRequest.setTradeItemList(giftItemList);
                    items.add(tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO());
                }
        );

        // 验证小店商品
//        this.validShopGoods(tradeItemGroups, commonUtil.getDistributeChannel());
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

        // 设置小店名称、返利总价
//        confirmResponse.setShopName(distributionCacheService.getShopName());
//        BigDecimal totalCommission = items.stream().flatMap(i -> i.getTradeItems().stream())
//                .filter(i -> DistributionGoodsAudit.CHECKED.equals(i.getDistributionGoodsAudit()))
//                .filter(i -> i.getDistributionCommission() != null)
//                .map(i -> i.getDistributionCommission().multiply(new BigDecimal(i.getNum())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        confirmResponse.setTotalCommission(totalCommission);

        // 设置邀请人名字
//        if (StringUtils.isNotEmpty(commonUtil.getDistributeChannel().getInviteeId())) {
//            DistributionCustomerByCustomerIdRequest request = new DistributionCustomerByCustomerIdRequest();
//            request.setCustomerId(commonUtil.getDistributeChannel().getInviteeId());
//            DistributionCustomerVO distributionCustomer = distributionCustomerQueryProvider.getByCustomerId(request)
//                    .getContext().getDistributionCustomerVO();
//            if (distributionCustomer != null) {
//                confirmResponse.setInviteeName(distributionCustomer.getCustomerName());
//            }
//        }

        // 校验拼团信息
        validGrouponOrder(confirmResponse, tradeItemGroups, customerId);

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

    /**
     * 用于确认拆箱订单后，创建订单前的获取订单商品信息
     */
    @ApiOperation(value = "用于确认拆箱订单后，创建订单前的获取订单商品信息")
    @RequestMapping(value = "/devanning/purchase", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<TradeConfirmResponse> getDevanningPurchaseItems(/*@RequestParam Long wareId,*/ Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag) {
        Long wareIdNew = commonUtil.getWareId(HttpUtil.getRequest());
        TradeConfirmResponse confirmResponse = new TradeConfirmResponse();
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareIdNew);
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        log.info("TradeBaseController getDevanningPurchaseItems tradeItemGroups:{}", JSON.toJSONString(tradeItemGroups));
        /**
         * 设置itemGroupVo的分仓信息
         */
        tradeItemGroups.stream().forEach(g -> g.setWareId(wareIdNew));

        List<TradeConfirmItemVO> items = new ArrayList<>();
        List<Long> skuIds = tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getDevanningId).collect(Collectors.toList());
        GoodsInfoResponse skuResp = getDevanningGoodsResponseNew(skuIds, wareIdNew, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
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
        giftResp = tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareIdNew).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
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
                    TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest =  new TradeQueryPurchaseInfoRequest();
                    tradeQueryPurchaseInfoRequest.setTradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class));
                    tradeQueryPurchaseInfoRequest.setTradeItemList(giftItemList);
                    items.add(tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO());
                }
        );

        // 验证小店商品
//        this.validShopGoods(tradeItemGroups, commonUtil.getDistributeChannel());
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

        // 设置小店名称、返利总价
//        confirmResponse.setShopName(distributionCacheService.getShopName());
//        BigDecimal totalCommission = items.stream().flatMap(i -> i.getTradeItems().stream())
//                .filter(i -> DistributionGoodsAudit.CHECKED.equals(i.getDistributionGoodsAudit()))
//                .filter(i -> i.getDistributionCommission() != null)
//                .map(i -> i.getDistributionCommission().multiply(new BigDecimal(i.getNum())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        confirmResponse.setTotalCommission(totalCommission);

        // 设置邀请人名字
//        if (StringUtils.isNotEmpty(commonUtil.getDistributeChannel().getInviteeId())) {
//            DistributionCustomerByCustomerIdRequest request = new DistributionCustomerByCustomerIdRequest();
//            request.setCustomerId(commonUtil.getDistributeChannel().getInviteeId());
//            DistributionCustomerVO distributionCustomer = distributionCustomerQueryProvider.getByCustomerId(request)
//                    .getContext().getDistributionCustomerVO();
//            if (distributionCustomer != null) {
//                confirmResponse.setInviteeName(distributionCustomer.getCustomerName());
//            }
//        }

        // 校验拼团信息
        validGrouponOrder(confirmResponse, tradeItemGroups, customerId);

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
        List<Long> wareIds = new LinkedList<>();
        wareIds.add(wareIdNew);
        wareIds.add(-1L);
        requ.setWareIds(wareIds);
//        confirmResponse.setCouponCodes(couponCodeQueryProvider.listForUseByCustomerId(requ).getContext()
//                .getCouponCodeList());
        // 将优惠券处理成商家入驻版本格式
        List<CouponCodeVO> couponCodeList = couponCodeQueryProvider.listForUseByCustomerId(requ).getContext().getCouponCodeList();
        couponService.setCouponCodes(confirmResponse, couponCodeList);
        return BaseResponse.success(confirmResponse);
    }

    /**
     * 用于确认订单后，创建订单前的获取订单商品信息（零售）
     */
    @ApiOperation(value = "用于确认订单后，创建订单前的获取订单商品信息")
    @RequestMapping(value = "/retail/purchase", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<TradeConfirmResponse> getRetailPurchaseItems(@RequestParam Long wareId, Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag) {

        TradeConfirmResponse confirmResponse = new TradeConfirmResponse();
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
                .builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        /**
         * 设置itemGroupVo的分仓信息
         */
        tradeItemGroups.stream().forEach(g -> g.setWareId(wareId));

        List<TradeConfirmItemVO> items = new ArrayList<>();
        //批发
        List<String> skuIds = tradeItemGroups.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getSkuId).collect(Collectors.toList());
        //零售
        List<String> retailSkuIds = tradeItemGroups.stream().filter(item -> SaleType.RETAIL.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getSkuId).collect(Collectors.toList());
        //批发商品
        GoodsInfoResponse skuResp = getGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
        //零售商品
        GoodsInfoResponse retailSkuResp = getGoodsRetailResponseNew(retailSkuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
        //批发赠品
        List<String> giftIds =
                tradeItemGroups.stream().filter(item -> SaleType.WHOLESALE.equals(item.getSaleType()))
                        .flatMap(i -> i.getTradeMarketingList().stream().filter(param -> CollectionUtils.isNotEmpty(param.getGiftSkuIds())))
                        .flatMap(r -> r.getGiftSkuIds()
                                .stream()).distinct().collect(Collectors.toList());
        //所有店铺信息
        Map<Long, StoreVO> storeMap = storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                (tradeItemGroups.stream().map(g -> g
                        .getSupplier().getStoreId())
                        .collect(Collectors.toList()))).getContext().getStoreVOList().stream().collect(Collectors
                .toMap(StoreVO::getStoreId,
                        s -> s));
        TradeGetGoodsResponse giftResp;
        giftResp = tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
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
        boolean isIepCustomerFlag = isIepCustomer();
        tradeItemGroups.forEach(
                g -> {
                    if (SaleType.WHOLESALE.equals(g.getSaleType())) { //批发
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
                        items.add(tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO());
                    } else { //零售
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
                        TradeConfirmItemVO tradeConfirmItemVO = tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO();
                        tradeConfirmItemVO.setSaleType(SaleType.RETAIL);
                        items.add(tradeConfirmItemVO);
                    }
                }
        );

        // 验证小店商品
//        this.validShopGoods(tradeItemGroups, commonUtil.getDistributeChannel());
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

        // 设置小店名称、返利总价
//        confirmResponse.setShopName(distributionCacheService.getShopName());
//        BigDecimal totalCommission = items.stream().flatMap(i -> i.getTradeItems().stream())
//                .filter(i -> DistributionGoodsAudit.CHECKED.equals(i.getDistributionGoodsAudit()))
//                .filter(i -> i.getDistributionCommission() != null)
//                .map(i -> i.getDistributionCommission().multiply(new BigDecimal(i.getNum())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        confirmResponse.setTotalCommission(totalCommission);

        // 设置邀请人名字
//        if (StringUtils.isNotEmpty(commonUtil.getDistributeChannel().getInviteeId())) {
//            DistributionCustomerByCustomerIdRequest request = new DistributionCustomerByCustomerIdRequest();
//            request.setCustomerId(commonUtil.getDistributeChannel().getInviteeId());
//            DistributionCustomerVO distributionCustomer = distributionCustomerQueryProvider.getByCustomerId(request)
//                    .getContext().getDistributionCustomerVO();
//            if (distributionCustomer != null) {
//                confirmResponse.setInviteeName(distributionCustomer.getCustomerName());
//            }
//        }

        // 校验拼团信息
        validGrouponOrder(confirmResponse, tradeItemGroups, customerId);

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
            confirmResponse.setCouponCodes(couponCodeQueryProvider.listForUseByCustomerId(requ).getContext()
                    .getCouponCodeList());
        }

        return BaseResponse.success(confirmResponse);
    }



    /**
     * 用于确认订单后，创建订单前的获取订单商品信息（零售）
     */
    @ApiOperation(value = "用于确认订单后，创建订单前的获取订单商品信息")
    @RequestMapping(value = "/devanning/retail/purchase", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<TradeConfirmResponse> getDevanningRetailPurchaseItems(/**@RequestParam Long wareId,*/ Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag) {

        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        log.info("=============wareId:{}",wareId);
        TradeConfirmResponse confirmResponse = new TradeConfirmResponse();
        String customerId = commonUtil.getOperatorId();
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        //验证用户
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        List<TradeItemGroupVO> tradeItemGroups = tradeItemQueryProvider.listAllByCustomerId(TradeItemByCustomerIdRequest
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

        //散批
        List<String> bulkSkuIds = tradeItemGroups.stream().filter(item -> SaleType.BULK.equals(item.getSaleType()))
                .flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getSkuId).collect(Collectors.toList());

        //批发商品
//        GoodsInfoResponse skuResp = getGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
        GoodsInfoResponse skuResp = getDevanningGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);

        //零售商品
        GoodsInfoResponse retailSkuResp = getGoodsRetailResponseNew(retailSkuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);

        //散批商品
        GoodsInfoResponse bulkSkuResp = getGoodsBulkResponseNew(bulkSkuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);

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
        giftResp = tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
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
            if (CollectionUtils.isNotEmpty(bulkSkuResp.getGoodsInfos())) {
                bulkSkuResp.getGoodsInfos().forEach(item -> item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS));
            }

        }
        //企业会员判断
        boolean isIepCustomerFlag = isIepCustomer();
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
                        TradeConfirmItemVO tradeConfirmItemVO = tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO();
                        tradeConfirmItemVO.setSaleType(g.getSaleType());
                        items.add(tradeConfirmItemVO);
                    }
                    else if (SaleType.BULK.equals(g.getSaleType())){
                        //散批
                        g.getSupplier().setFreightTemplateType(storeMap.get(g.getSupplier().getStoreId())
                                .getFreightTemplateType());
                        List<TradeItemVO> tradeItems = g.getTradeItems();
                        List<TradeItemDTO> tradeItemDTOList = KsBeanUtil.convert(tradeItems, TradeItemDTO.class);
                        bulkSkuResp.getGoodsInfos().forEach(item -> {
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
                                        KsBeanUtil.convert(bulkSkuResp, TradeGoodsInfoPageDTO.class),
                                        g.getSupplier().getStoreId(), true, checkStockFlag, customerId)).getContext().getTradeItems();

                        //大客户价商品回设 新增大客户标识字段判断
                        if (isIepCustomerFlag || commonUtil.isVipCustomerByVipFlag()) {
                            tradeItemVOList.forEach(tradeItemVO -> {
                                //vip价格不参与营销活动
                                BigDecimal vipPrice = Objects.nonNull(tradeItemVO.getVipPrice())
                                        && tradeItemVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ?
                                        tradeItemVO.getVipPrice() : tradeItemVO.getOriginalPrice();
                                tradeItemVO.setPrice(vipPrice);
                                tradeItemVO.setSplitPrice(vipPrice.multiply(new BigDecimal(tradeItemVO.getNum())));
                                tradeItemVO.setLevelPrice(vipPrice);
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
                            g.getTradeItems().forEach(item1 -> {
                                DefaultFlag storeOpenFlag = distributionCacheService.queryStoreOpenFlag(item1.getStoreId()
                                        .toString());
                                if ((Objects.isNull(item1.getIsFlashSaleGoods()) ||
                                        (Objects.nonNull(item1.getIsFlashSaleGoods()) && !item1.getIsFlashSaleGoods())) &&
                                        DefaultFlag.YES.equals(storeOpenFlag) && (
                                        DistributionGoodsAudit.CHECKED.equals(item1.getDistributionGoodsAudit())
                                                || DefaultFlag.YES.equals(g.getStoreBagsFlag()))) {
                                    item1.setSplitPrice(item1.getOriginalPrice().multiply(new BigDecimal(item1.getNum())));
                                    item1.setPrice(item1.getOriginalPrice());
                                    item1.setLevelPrice(item1.getOriginalPrice());
                                } else {
                                    item1.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                                }
                            });
                        }

                        confirmResponse.setStoreBagsFlag(g.getStoreBagsFlag());
                        TradeQueryPurchaseInfoRequest tradeQueryPurchaseInfoRequest = new TradeQueryPurchaseInfoRequest();
                        tradeQueryPurchaseInfoRequest.setTradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class));
                        //赠品列表给空，不然后续参数copy会报空指针
                        tradeQueryPurchaseInfoRequest.setTradeItemList(Collections.emptyList());
                        TradeConfirmItemVO tradeConfirmItemVO = tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO();
                        tradeConfirmItemVO.setSaleType(SaleType.BULK);
                        items.add(tradeConfirmItemVO);
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
                        TradeConfirmItemVO tradeConfirmItemVO = tradeQueryProvider.queryPurchaseInfo(tradeQueryPurchaseInfoRequest).getContext().getTradeConfirmItemVO();
                        tradeConfirmItemVO.setSaleType(SaleType.RETAIL);
                        items.add(tradeConfirmItemVO);
                    }
                }
        );

        // 验证小店商品
//        this.validShopGoods(tradeItemGroups, commonUtil.getDistributeChannel());
        //商品sku+订单列表=商品数量
        items.stream().forEach(tradeConfirmItemVO -> {
            if(tradeConfirmItemVO.getSupplier()!=null){
                CompanyMallBulkMarketVO companyMallBulkMarketVO = companyIntoPlatformQueryProvider.getMarketByStoreId(tradeConfirmItemVO.getSupplier().getStoreId()).getContext();
                if(companyMallBulkMarketVO!=null){
                    tradeConfirmItemVO.setMallMarketId(companyMallBulkMarketVO.getMarketId());
                    tradeConfirmItemVO.setMallMarketName(companyMallBulkMarketVO.getMarketName().replace(MARKET_NAME_SUFFIX,Constants.EMPTY_STR));
                    tradeConfirmItemVO.getSupplier().setMarketId(companyMallBulkMarketVO.getMarketId());
                    tradeConfirmItemVO.getSupplier().setMarketName(companyMallBulkMarketVO.getMarketName());
                }
            }
            //所有商品
            Long goodsNum = tradeConfirmItemVO.getTradeItems().stream().mapToLong((s) -> s.getNum()).sum();
            //所有赠品
            if (CollectionUtils.isNotEmpty(tradeConfirmItemVO.getGifts())) {
                goodsNum += tradeConfirmItemVO.getGifts().stream().mapToLong((s) -> s.getNum()).sum();
            }
            tradeConfirmItemVO.setGoodsNum(goodsNum);
        });
        confirmResponse.setTradeConfirmItems(items);

        // 设置小店名称、返利总价
//        confirmResponse.setShopName(distributionCacheService.getShopName());
//        BigDecimal totalCommission = items.stream().flatMap(i -> i.getTradeItems().stream())
//                .filter(i -> DistributionGoodsAudit.CHECKED.equals(i.getDistributionGoodsAudit()))
//                .filter(i -> i.getDistributionCommission() != null)
//                .map(i -> i.getDistributionCommission().multiply(new BigDecimal(i.getNum())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        confirmResponse.setTotalCommission(totalCommission);

        // 设置邀请人名字
//        if (StringUtils.isNotEmpty(commonUtil.getDistributeChannel().getInviteeId())) {
//            DistributionCustomerByCustomerIdRequest request = new DistributionCustomerByCustomerIdRequest();
//            request.setCustomerId(commonUtil.getDistributeChannel().getInviteeId());
//            DistributionCustomerVO distributionCustomer = distributionCustomerQueryProvider.getByCustomerId(request)
//                    .getContext().getDistributionCustomerVO();
//            if (distributionCustomer != null) {
//                confirmResponse.setInviteeName(distributionCustomer.getCustomerName());
//            }
//        }

        // 校验拼团信息
        validGrouponOrder(confirmResponse, tradeItemGroups, customerId);

        //只有批发订单时才去查询需要使用的优惠券列表
        /**
         * 注释的原因：优惠券支持散批和批发
         */
        //boolean calCouponFlag = tradeItemGroups.stream().anyMatch(item -> item.getSaleType().equals(SaleType.WHOLESALE));
        //if (calCouponFlag) {
        // 根据确认订单计算出的信息，查询出使用优惠券页需要的优惠券列表数据(批发订单)
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
                    dto.setSaleType(confirmItem.getSaleType());
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
//      confirmResponse.setCouponCodes(couponCodeQueryProvider.listForUseByCustomerId(requ).getContext()
//      .getCouponCodeList());

        // 将优惠券处理成商家入驻版本格式
        List<CouponCodeVO> couponCodeList = couponCodeQueryProvider.listForUseByCustomerId(requ).getContext().getCouponCodeList();
        couponService.setCouponCodes(confirmResponse, couponCodeList);


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
        });
        List<TradeConfirmItemVO> tradeConfirmItems = confirmResponse.getTradeConfirmItems();
        if(CollectionUtils.isNotEmpty(tradeConfirmItems)){
            Collections.sort(tradeConfirmItems, new Comparator<TradeConfirmItemVO>() {
                @Override
                public int compare(TradeConfirmItemVO o1, TradeConfirmItemVO o2) {
                    if(o1.getMallMarketId()!=null && o2.getMallMarketId()!=null){
                        return o1.getMallMarketId().compareTo(o2.getMallMarketId());
                    }
                    return 0;
                }
            });
        }
        return BaseResponse.success(confirmResponse);
    }




    /**
     * 用于确认提货订单后，创建订单前的获取订单商品信息
     */
    @ApiOperation(value = "用于确认提货订单后，创建订单前的获取订单商品信息")
    @RequestMapping(value = "/take/purchase", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<TakeTradeConfirmResponse> getTakePurchaseItems(@RequestParam Long wareId, Boolean matchWareHouseFlag, @RequestParam(required = false) Boolean checkStockFlag) {

        TakeTradeConfirmResponse confirmResponse = new TakeTradeConfirmResponse();
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
        List<Long> skuIds = tradeItemGroups.stream().flatMap(i -> i.getTradeItems().stream())
                .map(TradeItemVO::getDevanningId).collect(Collectors.toList());
        GoodsInfoResponse skuResp = getDevanningGoodsResponseNew(skuIds, wareId, wareHouseVO.getWareCode(), customer, matchWareHouseFlag);
//        List<String> giftIds =
//                tradeItemGroups.stream().flatMap(i -> i.getTradeMarketingList().stream().filter(param -> CollectionUtils.isNotEmpty(param.getGiftSkuIds()))).flatMap(r -> r.getGiftSkuIds()
//                        .stream()).distinct().collect(Collectors.toList());
        Map<Long, StoreVO> storeMap = storeQueryProvider.listNoDeleteStoreByIds(new ListNoDeleteStoreByIdsRequest
                (tradeItemGroups.stream().map(g -> g
                        .getSupplier().getStoreId())
                        .collect(Collectors.toList()))).getContext().getStoreVOList().stream().collect(Collectors
                .toMap(StoreVO::getStoreId,
                        s -> s));
//        TradeGetGoodsResponse giftResp;
//        giftResp = tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).skuIds(giftIds).build()).getContext();
//        final TradeGetGoodsResponse giftTemp = giftResp;

        // 如果为PC商城下单，将分销商品变为普通商品
//        if (ChannelType.PC_MALL.equals(commonUtil.getDistributeChannel().getChannelType())) {
//            tradeItemGroups.forEach(tradeItemGroup ->
//                    tradeItemGroup.getTradeItems().forEach(tradeItem -> {
//                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
//                    })
//            );
//            skuResp.getGoodsInfos().forEach(item -> item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS));
//        }
        //企业会员判断
//        boolean isIepCustomerFlag = isIepCustomer();
        tradeItemGroups.forEach(
                g -> {
                    g.getSupplier().setFreightTemplateType(storeMap.get(g.getSupplier().getStoreId())
                            .getFreightTemplateType());
                    List<TradeItemVO> tradeItems = g.getTradeItems();
                    List<TradeItemDTO> tradeItemDTOList = KsBeanUtil.convert(tradeItems, TradeItemDTO.class);
                    // 分销商品、开店礼包商品、拼团商品、企业购商品，不验证起限定量
//                    skuResp.getGoodsInfos().forEach(item -> {
//                        //企业购商品
//                        if (DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())
//                                || DefaultFlag.YES.equals(g.getStoreBagsFlag())
//                                || Objects.nonNull(g.getGrouponForm()) || isIepCustomerFlag) {
//                            item.setCount(null);
//                            item.setMaxCount(null);
//                        }
//                    });
                    //商品验证并填充
                    List<TradeItemVO> tradeItemVOList =
                            verifyQueryProvider.verifyTakeGoods(new VerifyGoodsRequest(tradeItemDTOList, Collections
                                    .emptyList(),
                                    KsBeanUtil.convert(skuResp, TradeGoodsInfoPageDTO.class),
                                    g.getSupplier().getStoreId(), true, checkStockFlag,customerId)).getContext().getTradeItems();
                    //大客户价商品回设
//                    if (isIepCustomerFlag) {
//                        tradeItemVOList.forEach(tradeItemVO -> {
//                            BigDecimal vipPrice = Objects.nonNull(tradeItemVO.getVipPrice())
//                                    && tradeItemVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ?
//                                    tradeItemVO.getVipPrice() : tradeItemVO.getOriginalPrice();
//                            tradeItemVO.setPrice(vipPrice);
//                            tradeItemVO.setSplitPrice(vipPrice.multiply(new BigDecimal(tradeItemVO.getNum())));
//                            tradeItemVO.setLevelPrice(vipPrice);
//                        });
//                    }
                    //特价商品的价格回设
//                    tradeItemVOList.forEach(tradeItemVO -> {
//                        if (Objects.nonNull(tradeItemVO.getGoodsInfoType())
//                                && tradeItemVO.getGoodsInfoType() == 1
//                                && tradeItemVO.getSpecialPrice() != null) {
//                            tradeItemVO.setPrice(tradeItemVO.getSpecialPrice());
//                            tradeItemVO.setSplitPrice(tradeItemVO.getSpecialPrice().multiply(new BigDecimal(tradeItemVO.getNum())));
//                            tradeItemVO.setLevelPrice(tradeItemVO.getSpecialPrice());
//                        }
//                    });

                    //抢购商品价格回设
//                    if (StringUtils.isNotBlank(g.getSnapshotType()) && g.getSnapshotType().equals(Constants.FLASH_SALE_GOODS_ORDER_TYPE)) {
//                        tradeItemVOList.forEach(tradeItemVO -> {
//                            g.getTradeItems().forEach(tradeItem -> {
//                                if (tradeItem.getSkuId().equals(tradeItemVO.getSkuId())) {
//                                    tradeItemVO.setPrice(tradeItem.getPrice());
//                                }
//                            });
//                        });
//                    }
                    g.setTradeItems(tradeItemVOList);
                    // 分销商品、开店礼包商品，重新设回市场价
//                    if (DefaultFlag.YES.equals(distributionCacheService.queryOpenFlag())
//                            && !ChannelType.PC_MALL.equals(commonUtil.getDistributeChannel().getChannelType())) {
//                        g.getTradeItems().forEach(item -> {
//                            DefaultFlag storeOpenFlag = distributionCacheService.queryStoreOpenFlag(item.getStoreId()
//                                    .toString());
//                            if ((Objects.isNull(item.getIsFlashSaleGoods()) ||
//                                    (Objects.nonNull(item.getIsFlashSaleGoods()) && !item.getIsFlashSaleGoods())) &&
//                                    DefaultFlag.YES.equals(storeOpenFlag) && (
//                                    DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())
//                                            || DefaultFlag.YES.equals(g.getStoreBagsFlag()))) {
//                                item.setSplitPrice(item.getOriginalPrice().multiply(new BigDecimal(item.getNum())));
//                                item.setPrice(item.getOriginalPrice());
//                                item.setLevelPrice(item.getOriginalPrice());
//                            } else {
//                                item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
//                            }
//                        });
//                    }

                    //赠品信息填充
//                    List<String> giftItemIds =
//                            g.getTradeMarketingList().parallelStream().filter(i -> (i.getGiftSkuIds() != null) && !i.getGiftSkuIds().isEmpty())
//                                    .flatMap(r -> r.getGiftSkuIds().stream()).distinct().collect(Collectors.toList());
//                    List<TradeItemDTO> gifts =
//                            giftItemIds.stream().map(i -> TradeItemDTO.builder().price(BigDecimal.ZERO)
//                                    .skuId(i)
//                                    .build()).collect(Collectors.toList());
//                    List<TradeItemVO> giftVoList = verifyQueryProvider.mergeGoodsInfo(new MergeGoodsInfoRequest(gifts
//                                    , KsBeanUtil.convert(giftTemp, TradeGoodsListDTO.class)))
//                            .getContext().getTradeItems();
//                    gifts = KsBeanUtil.convert(giftVoList, TradeItemDTO.class);
//                    confirmResponse.setStoreBagsFlag(g.getStoreBagsFlag());
//                    items.add(tradeQueryProvider.queryTakePurchaseInfo(TradeQueryPurchaseInfoRequest.builder()
//                            .tradeItemGroupDTO(KsBeanUtil.convert(g, TradeItemGroupDTO.class))
//                            /*.tradeItemList(gifts)*/.build()).getContext().getTradeConfirmItemVO());
                }
        );

        //赋值提货商品信息
        items.addAll(KsBeanUtil.convertList(tradeItemGroups,TradeConfirmItemVO.class));

        // 验证小店商品
//        this.validShopGoods(tradeItemGroups, commonUtil.getDistributeChannel());
        //商品sku+订单列表=商品数量
        items.stream().forEach(tradeConfirmItemVO -> {
            //所有商品
            Long goodsNum = tradeConfirmItemVO.getTradeItems().stream().mapToLong((s) -> s.getNum()).sum();
            //所有赠品
//            if (CollectionUtils.isNotEmpty(tradeConfirmItemVO.getGifts())) {
//                goodsNum += tradeConfirmItemVO.getGifts().stream().mapToLong((s) -> s.getNum()).sum();
//            }
            tradeConfirmItemVO.setGoodsNum(goodsNum);
        });
        confirmResponse.setTradeConfirmItems(items);

        // 根据确认订单计算出的信息，查询出使用优惠券页需要的优惠券列表数据
//        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
//        List<TradeItemInfoDTO> tradeDtos = items.stream().flatMap(confirmItem ->
//                confirmItem.getTradeItems().stream().map(tradeItem -> {
//                    TradeItemInfoDTO dto = new TradeItemInfoDTO();
//                    dto.setBrandId(tradeItem.getBrand());
//                    dto.setCateId(tradeItem.getCateId());
//                    dto.setSpuId(tradeItem.getSpuId());
//                    dto.setSkuId(tradeItem.getSkuId());
//                    dto.setStoreId(confirmItem.getSupplier().getStoreId());
//                    dto.setPrice(tradeItem.getSplitPrice());
//                    dto.setGoodsInfoType(tradeItem.getGoodsInfoType());
//                    dto.setDistributionGoodsAudit(tradeItem.getDistributionGoodsAudit());
//                    if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(
//                            distributionCacheService.queryStoreOpenFlag(String.valueOf(tradeItem.getStoreId())))) {
//                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
//                    }
//                    return dto;
//                })
//        ).collect(Collectors.toList());

//        CustomerVO customerVO = commonUtil.getCustomer();
//        CouponCodeListForUseByCustomerIdRequest requ = CouponCodeListForUseByCustomerIdRequest.builder()
//                .customerId(Objects.nonNull(customerVO) && StringUtils.isNotBlank(customerVO.getParentCustomerId())
//                        ? customerVO.getParentCustomerId() : customerId)
//                .tradeItems(tradeDtos).build();
//        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
//        if (Objects.nonNull(domainStoreRelaVO)) {
//            requ.setStoreId(domainStoreRelaVO.getStoreId());
//        }
//        confirmResponse.setCouponCodes(couponCodeQueryProvider.listForUseByCustomerId(requ).getContext()
//                .getCouponCodeList());
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
        return BaseResponse.success(tradeParams.stream().map(params -> tradeQueryProvider.getFreight(params)
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

        List<TradeParamsRequest> expressRequestList = new ArrayList<>();
        List<TradeParamsRequest> deliveryToStoreRequestList = new ArrayList<>();
        //排除所有非快递方式的订单，直接塞0
        Iterator<TradeParamsRequest> iterator = tradeParams.iterator();
        while (iterator.hasNext()) {
            TradeParamsRequest next = iterator.next();
            boolean isNeedPaidFee = DeliverWay.logisticFeeBeginPaid(next.getDeliverWay());
            if (!isNeedPaidFee) {
                TradeGetFreightResponse inner = new TradeGetFreightResponse();
                inner.setStoreId(next.getSupplier().getStoreId());
                inner.setStoreName(next.getSupplier().getStoreName());
                inner.setDeliveryPrice(BigDecimal.ZERO);
                result.add(inner);
                iterator.remove();
            } else {
                // 根据商品skuid验证商品是否是秒杀商品
                if (Objects.nonNull(next.getIsSeckill()) && next.getIsSeckill()) {
                    seckillFreight(result, iterator, next);
                }
                if(isNeedPaidFee) {
                    if(DeliverWay.isTmsDelivery(next.getDeliverWay())) {
                        if (Objects.isNull(next.getSupplier().getMarketId())) {
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家[" + next.getSupplier().getStoreName() + "]缺少批发市场信息");
                        }
                        deliveryToStoreRequestList.add(next);
                    }
                }else {
                    expressRequestList.add(next);
                }
            }
        }

        //平台
        boolean platformFlag = false;
        //统仓统配
        boolean unifiedFlag = false;
        //三方卖家
        boolean supplierFlag = false;
        //散批商家
        boolean bulkFlag = false;

        for (TradeParamsRequest inner : tradeParams) {
            if (inner.getSupplier().getCompanyType().equals(CompanyType.PLATFORM)) {
                platformFlag = true;
            // 新散批
            } else if(inner.getSupplier().getCompanyType().equals(CompanyType.BULK)){
                bulkFlag = true;
            }else if (inner.getSupplier().getCompanyType().equals(CompanyType.UNIFIED)) {
                unifiedFlag = true;
            } else {
                supplierFlag = true;
            }
        }
        log.info("getFinalFreight bulkFlag===:{}",bulkFlag);
        log.info("getFinalFreight platformFlag===:{}",platformFlag);
        log.info("getFinalFreight tradeParams===:{}",JSON.toJSONString(tradeParams));
        //存在三种店铺时，走以下计算方式否则还是走店铺运费计算
        if (false&&platformFlag && unifiedFlag && supplierFlag) {
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
                result.addAll(tradeQueryProvider.getBossFreight(bossFreight).getContext());
            }
            if (CollectionUtils.isNotEmpty(storeFreight)) {
                result.addAll(storeFreight.stream().map(params -> tradeQueryProvider.getFreight(params)
                        .getContext()).collect(Collectors.toList()));
            }
        // 有散批商品时，进行特殊处理
        } else if(bulkFlag){
            bulkFlagFreight(tradeParams, result);
        } else {
            if(expressRequestList.size()>0){
                result.addAll(expressRequestList.stream().map(params -> tradeQueryProvider.getFreight(params)
                        .getContext()).collect(Collectors.toList()));
            }
            if(deliveryToStoreRequestList.size()>0){
                String buyerId = commonUtil.getOperator().getUserId();
                deliveryToStoreRequestList.forEach(p->{
                    p.setBuyerId(buyerId);
                });
                result.addAll(tradeQueryProvider.getFreightForDeliveryToStore(TradeParamsRequestForApp.builder().tradeParams(deliveryToStoreRequestList).build()).getContext());
            }

        }
        return BaseResponse.success(result);
    }

    private void seckillFreight(List<TradeGetFreightResponse> result, Iterator<TradeParamsRequest> iterator, TradeParamsRequest next) {
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
                    inner.setStoreName(next.getSupplier().getStoreName());
                    inner.setDeliveryPrice(BigDecimal.ZERO);
                    result.add(inner);
                    iterator.remove();
                }
            }
        }
    }

    private void bulkFlagFreight(List<TradeParamsRequest> tradeParams, List<TradeGetFreightResponse> result) {
        // 零售
        List<TradeParamsRequest> retail = new ArrayList<>(10);
        // 散批
        List<TradeParamsRequest> bulk = new ArrayList<>(10);
        // 批发
        List<TradeParamsRequest> boss = new ArrayList<>(10);
        // 分组
        for (TradeParamsRequest inner : tradeParams) {
            if (inner.getSupplier().getCompanyType().equals(CompanyType.BULK)) {
                bulk.add(inner);
            } else if (inner.getSupplier().getCompanyType().equals(CompanyType.RETAIL)) {
                retail.add(inner);
            } else{
                boss.add(inner);
            }
        }
        // 散批
        if(CollectionUtils.isNotEmpty(bulk)){
            // 合并：批发+散批
            if(CollectionUtils.isNotEmpty(boss)){
                TradeParamsRequest tradeParamsRequest = boss.get(0);
                TradeParamsRequest bulkTradeParams = bulk.get(0);
                tradeParamsRequest.setOldBulkTradeItems(bulkTradeParams.getOldTradeItems());
                tradeParamsRequest.setTotalPrice(tradeParamsRequest.getTradePrice().getTotalPrice().add(bulkTradeParams.getTradePrice().getTotalPrice()));
                log.info("散批和批发获取运费计算结果===:{}",JSON.toJSONString(tradeParamsRequest));
                result.add(tradeQueryProvider.getTradeFreightAndBluk(tradeParamsRequest).getContext());
            // 只有散批
            } else {
                TradeParamsRequest bulkTradeParams = bulk.get(0);
                List<TradeItemDTO> tradeItems = bulkTradeParams.getOldTradeItems();
                BigDecimal totalPrice = bulkTradeParams.getTradePrice().getTotalPrice();

                bulkTradeParams.setOldBulkTradeItems(tradeItems);
                bulkTradeParams.setOldTradeItems(null);
                bulkTradeParams.setTotalPrice(totalPrice);

                log.info("只有散批获取运费计算结果===:{}",JSON.toJSONString(bulkTradeParams));
                result.add(tradeQueryProvider.getTradeFreightAndBluk(bulkTradeParams).getContext());
            }
        }
        // 零售单独处理
        if(CollectionUtils.isNotEmpty(retail)){
            result.addAll(retail.stream().map(params -> tradeQueryProvider.getFreight(params)
                    .getContext()).collect(Collectors.toList()));
        }
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
        return BaseResponse.success(tradeQueryProvider.getBossFreight(tradeParams).getContext());
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
        Page<TradeVO> tradePage = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
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
                .deletedFlag(0)
                .isBoss(false)
                .build();
        tradeQueryRequest.setPageNum(paramRequest.getPageNum());
        tradeQueryRequest.setPageSize(paramRequest.getPageSize());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            tradeQueryRequest.setStoreId(domainInfo.getStoreId());
        }

        //大白鲸粉页面的订单列表查询
        if(Objects.nonNull(paramRequest.getMarkType()) && paramRequest.getMarkType().intValue() == 2){
            tradeQueryRequest.setFlowStates(paramRequest.getFlowStates());
            tradeQueryRequest.makeAllAuditFlowStatusList();
        }else{
            //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
            tradeQueryRequest.makeAllAuditFlowStatus();
        }
        Page<TradeVO> tradePage = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();

        List<TradeVO> content = tradePage.getContent();




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


            List<String> sku =new LinkedList<>();
            tradeReponse.getTradeItems().forEach(t->{
                if (Objects.isNull(t.getDevanningId())){
                    //拆箱id为空需要查询最小步长的拆箱id赋值
                    sku.clear();
                    sku.add(t.getSkuId());
                    Optional<DevanningGoodsInfoVO> first = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(sku).build()).getContext().getDevanningGoodsInfoVOS()
                            .stream().findFirst();
                    if (first.isPresent()){
                        t.setDevanningId(first.get().getDevanningId());
                        t.setDivisorFlag(first.get().getDivisorFlag());
                        t.setAddStep(first.get().getAddStep());
                    }
                }
            });
        });
        //增加商品数量值
        for (TradeVO tradeVO : tradeReponses) {
            Long sum = tradeVO.getTradeItems().stream().mapToLong(TradeItemVO::getNum).sum();
            sum = sum == null ? 0L : sum;
            tradeVO.setGoodsTotalNum(sum);
            BigDecimal reduce = tradeVO.getTradeItems().stream().map(TradeItemVO::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tradeVO.getTradePrice().setTotalPrice(reduce.add(tradeVO.getTradePrice().getPackingPrice()).add(tradeVO.getTradePrice().getDeliveryPrice()));
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

        Page<TradeVO> tradePage = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
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

        Page<TradeVO> tradePage = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        //订单列表做验证,客户订单列表无需验证
        if ("0".equals(type)) {
            checkUnauthorized(tid, trade);
        }
        List<TradeDeliverVO> newTradeDeliver = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(trade.getTradeDelivers())){
            newTradeDeliver.add(trade.getTradeDelivers().get(0));
        }
        TradeDeliverRecordResponse tradeDeliverRecord = TradeDeliverRecordResponse.builder()
                .status(trade.getTradeState().getFlowState().getStateId())
                .tradeDeliver(newTradeDeliver)
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
     * 查询订单付款记录（聚合接口）
     */
    @ApiOperation(value = "查询订单付款记录聚合接口")
    @RequestMapping(value = "/payOrderGroup", method = RequestMethod.POST)
    public BaseResponse<FindPayOrderResponseWrapper> payOrderGroup(@RequestBody FindPayOrderGroupRequest request) {

        List<String> tids = request.getTids();

        List<FindPayOrderResponse> responses = Lists.newArrayList();
        for (String tid : tids) {
           TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
           FindPayOrderResponse payOrderResponse = null;
           try {
               payOrderResponse = payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(trade.getId()).build()).getContext();
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
           responses.add(payOrderResponse);
        }

        FindPayOrderResponseWrapper build = FindPayOrderResponseWrapper.builder().responses(responses).build();
        return BaseResponse.success(build);
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if ("0".equals(type)) {
            checkUnauthorized(tid, trade);
        }
        TradeDeliverVO deliver = trade.getTradeDelivers().stream().filter(
                d -> deliverId.equals(d.getDeliverId())
        ).findFirst().orElseGet(null);
        List<ShippingItemVO> shippingItemVOS = new ArrayList<>(10);
        boolean isDevanningId = CollectionUtils.isNotEmpty(deliver.getShippingItems())?null!=deliver.getShippingItems().get(0).getDevanningId():false;
        trade.getTradeItems().forEach(tradeItemVO -> {
            if(isDevanningId && Objects.nonNull(tradeItemVO.getDevanningId())){
                Optional<ShippingItemVO> first = deliver.getShippingItems().stream().filter(d -> d.getDevanningId().equals(tradeItemVO.getDevanningId()))
                        .peek(x -> {
                            x.setPrice(tradeItemVO.getPrice());
                            x.setSplitPrice(tradeItemVO.getSplitPrice());
                        })
                        .findFirst();
                first.ifPresent(shippingItemVOS::add);
            }else{

                Optional<ShippingItemVO> first = deliver.getShippingItems().stream().filter(d -> d.getSkuId().equals(tradeItemVO.getSkuId()))
                        .peek(x -> {
                            x.setPrice(tradeItemVO.getPrice());
                            x.setSplitPrice(tradeItemVO.getSplitPrice());
                        })
                        .findFirst();
                first.ifPresent(shippingItemVOS::add);
            }
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(paymentRecordRequest.getTid()).build())
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
        return tradeProvider.addReceivable(tradeAddReceivableRequest);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/cancel/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse cancel(@PathVariable(value = "tid") String tid) {
        if(StringUtils.isBlank(tid)){
            return BaseResponse.error("参数错误");
        }
        Operator operator = commonUtil.getOperator();
        log.info("用户开始{}操作取消订单{}",operator.getAccount(),tid);
        String cancelRedisKey = "cancel_trade:"+tid;
        RLock rLock = redissonClient.getFairLock(cancelRedisKey);
        if(rLock.isLocked()){
            return BaseResponse.error("订单取消中，请稍后操作");
        }
        rLock.lock();
        try {
            TradeCancelRequest tradeCancelRequest = TradeCancelRequest.builder()
                    .tid(tid).operator(operator).build();
            tradeProvider.cancel(tradeCancelRequest);
        } catch (Exception e) {
            throw e;
        } finally {
            if(rLock.isLocked()){
                rLock.unlock();
            }
        }
        log.info("用户结束{}操作取消订单{}",operator.getAccount(),tid);
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
    @MultiSubmit
    public BaseResponse confirm(@PathVariable String tid) {
        Operator operator = commonUtil.getOperator();
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (trade.getMergFlag() && !tid.substring(0,3).contains("L")){
            List<String> tids = trade.getTids();
            tids = tids.stream().filter(v -> {
                if (v.substring(0, 3).contains("L")) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            log.info("需要收货的订单号"+tids);
            tids.forEach(v->{
                if (v.equalsIgnoreCase(tid)){
                    checkUnauthorized(v, trade);
                    TradeConfirmReceiveRequest tradeConfirmReceiveRequest = TradeConfirmReceiveRequest.builder()
                            .operator(operator).tid(v).build();

                    tradeProvider.confirmReceive(tradeConfirmReceiveRequest);
                }else {
                    TradeVO trade1 =
                            tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(v).build()).getContext().getTradeVO();
                    checkUnauthorized(v, trade1);
                    TradeConfirmReceiveRequest tradeConfirmReceiveRequest = TradeConfirmReceiveRequest.builder()
                            .operator(operator).tid(v).build();

                    tradeProvider.confirmReceive(tradeConfirmReceiveRequest);

                }
            });

            return BaseResponse.SUCCESSFUL();

        }else {
            checkUnauthorized(tid, trade);

            TradeConfirmReceiveRequest tradeConfirmReceiveRequest = TradeConfirmReceiveRequest.builder()
                    .operator(operator).tid(tid).build();

            tradeProvider.confirmReceive(tradeConfirmReceiveRequest);
            return BaseResponse.SUCCESSFUL();
        }


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

        return BaseResponse.success(tradeProvider.defaultPay(tradeDefaultPayRequest).getContext().getPayResult());
    }

    /**
     * 提货0元订单默认支付
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "0元订单默认支付")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/pile/default/pay/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<Boolean> pileDefaultPay(@PathVariable String tid) {

        TradeDefaultPayRequest tradeDefaultPayRequest = TradeDefaultPayRequest
                .builder()
                .payWay(PayWay.UNIONPAY)
                .tid(tid)
                .build();

        return BaseResponse.success(tradeProvider.pileDefaultPay(tradeDefaultPayRequest).getContext().getPayResult());
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
        return tradeItemProvider.updateUnStock(TradeItemStockOutRequest
                .builder().wareId(request.getRealWareId()).customerId(commonUtil.getOperatorId())
                .cityCode(request.getCityCode()).storeId(request.getStoreId()).build());
    }

    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getGoodsResponse(List<String> skuIds, CustomerVO customer) {
        TradeGetGoodsResponse response =
                tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder().skuIds(skuIds).build()).getContext();

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
     * 获取拆箱订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getDevanningGoodsResponse(List<Long> devanningIds, CustomerVO customer, Long wareId) {
        TradeGetGoodsResponse response =
                tradeQueryProvider.getDevanningGoods(TradeGetGoodsRequest.builder().devanningIds(devanningIds).wareId(wareId).build()).getContext();

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
     * 获取提货订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getTakeGoodsResponseNew(List<String> skuIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        TradeGetGoodsResponse response =
                tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder()
                        .skuIds(skuIds)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();

//        //计算区间价
//        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
//                (response.getGoodsInfos(), customer.getCustomerId());
//        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
//        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
//        //获取客户的等级
//        if (StringUtils.isNotBlank(customer.getCustomerId())) {
//            //计算会员价
//            response.setGoodsInfos(
//                    marketingLevelPluginProvider.goodsListFilter(MarketingLevelGoodsListFilterRequest.builder()
//                                    .goodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class))
//                                    .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class)).build())
//                            .getContext().getGoodsInfoVOList());
//        }
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
        if (CollectionUtils.isEmpty(skuIds)) {
            return new GoodsInfoResponse();
        }
        TradeGetGoodsResponse response =
                tradeQueryProvider.getGoods(TradeGetGoodsRequest.builder()
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

    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getDevanningGoodsResponseNew(List<Long> devanningIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(devanningIds)) {
            return new GoodsInfoResponse();
        }

        log.info("============devanningIds:{}, wareId:{}, wareHouseCode:{}, matchWareHouseFlag:{}",JSONObject.toJSONString(devanningIds), wareId, wareHouseCode, matchWareHouseFlag);

        TradeGetGoodsResponse response =
                tradeQueryProvider.getDevanningGoods(TradeGetGoodsRequest.builder()
                        .devanningIds(devanningIds)
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

    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getGoodsRetailResponseNew(List<String> skuIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new GoodsInfoResponse();
        }
        TradeGetGoodsResponse response =
                tradeQueryProvider.getRetailGoods(TradeGetGoodsRequest.builder()
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







    /**
     * 获取订单商品详情,包含区间价，会员级别价
     */
    private GoodsInfoResponse getGoodsBulkResponseNew(List<String> skuIds, Long wareId, String wareHouseCode
            , CustomerVO customer, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new GoodsInfoResponse();
        }
        TradeGetGoodsResponse response =
                tradeQueryProvider.getBulkGoods(TradeGetGoodsRequest.builder()
                        .skuIds(skuIds)
                        .wareHouseCode(wareHouseCode)
                        .wareId(wareId)
                        .matchWareHouseFlag(matchWareHouseFlag)
                        .build()).getContext();

        //计算区间价
//        GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceService.getGoodsIntervalPriceVOList
//                (response.getGoodsInfos(), customer.getCustomerId());
//        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
//        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
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

    /**
     * 指定区域销售单笔订单限购校验
     * @param tradeCommitRequest
     * @param tradeItemGroups
     */
    private void checkGoodsNumsForCommitOrder(TradeCommitRequest tradeCommitRequest,List<TradeItemGroupVO> tradeItemGroups) {
        List<TradeItemVO> tradeItems = new ArrayList<>();
        tradeItemGroups.forEach(t -> tradeItems.addAll(t.getTradeItems()));
        Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemVO::getSkuId, TradeItemVO::getNum,(a,b)->a));
        List<String> skuIds =
                tradeItems.stream().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        GoodsInfoListByIdsRequest request = new GoodsInfoListByIdsRequest();
        request.setGoodsInfoIds(skuIds);
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(request).getContext().getGoodsInfos();
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(tradeCommitRequest.getConsigneeId()).build()).getContext();
        goodsInfos.forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
                List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                log.info("==============>singleAreaList:{}",singleAreaList);
                if (singleAreaList.contains(response.getProvinceId()) || singleAreaList.contains(response.getCityId())){
                    if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
                        log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                        if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()) > goodsInfoVO.getSingleOrderPurchaseNum()){
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
                        }
                    }
                }
            }
        });
    }

    /**
     * 区域限购验证
     * @param tradeItemGroups
     * @param deliveryAddress
     */
    private void checkGoodsForCommitOrder(List<TradeItemGroupVO> tradeItemGroups,CustomerDeliveryAddressResponse deliveryAddress) {
        List<TradeItemVO> tradeItems = new ArrayList<>();
        tradeItemGroups.forEach(t -> tradeItems.addAll(t.getTradeItems()));
        List<String> skuIds =
                tradeItems.stream().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        GoodsInfoListByIdsRequest request = new GoodsInfoListByIdsRequest();
        request.setGoodsInfoIds(skuIds);
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(request).getContext().getGoodsInfos();
        goodsInfos.forEach(goodsInfoVO -> {
            if (Objects.nonNull(deliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
                List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，在限购区域限购请重新选择商品购买！");
                }
            }
        });
    }

    private void validHomeDelivery(List<StoreCommitInfoDTO> storeCommitInfo, String addressId, Long wareId, String customerId) {

        for (StoreCommitInfoDTO inner : storeCommitInfo) {
            if (inner.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
                if (DefaultFlag.NO.equals(checkDeliveryArea(wareId, addressId,customerId,inner.getStoreId()).getFlag())) {
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
    private DeliveryHomeFlagResponse checkDeliveryArea(Long wareId, String customerDeleiverAddressId, String customerId,Long storeId) {
        DeliveryHomeFlagResponse resultResponse = new DeliveryHomeFlagResponse();
        resultResponse.setAdress("");
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
        if (Objects.isNull(response) || response.getDelFlag().equals(DeleteFlag.YES)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        if ((Objects.isNull(wareHouseVO) || (DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        DeliveryHomeFlagResponse hubeiResultResponse =deliveryhomeCfgHubei.getHubeiDeliveryHomeFlagResponse(customerDeleiverAddressId, resultResponse, response);
        if (hubeiResultResponse != null) return hubeiResultResponse;
        //校验商品所在店铺配置免费配送范围
        List<FreightTemplateDeliveryAreaVO> deliveryAreaVOList = freightTemplateDeliveryAreaQueryProvider
                .queryDeliveryHomeConfifg(FreightTemplateDeliveryAreaListRequest.builder().storeId(storeId).wareId(wareId).build()).getContext();

        FreightTemplateDeliveryAreaByStoreIdResponse freightTemplateDeliveryAreaByStoreIdResponse = new FreightTemplateDeliveryAreaByStoreIdResponse();
        if(CollectionUtils.isNotEmpty(deliveryAreaVOList)){
            for(FreightTemplateDeliveryAreaVO deliveryAreaVO:deliveryAreaVOList){
                if(deliveryAreaVO.getDestinationType()== freightTemplateDeliveryType.CONVENTION){
                    freightTemplateDeliveryAreaByStoreIdResponse.setFreightTemplateDeliveryAreaVO(deliveryAreaVO);
                    continue;
                }
                if(deliveryAreaVO.getDestinationType()== freightTemplateDeliveryType.AREATENDELIVER){
                    freightTemplateDeliveryAreaByStoreIdResponse.setAreaTenFreightTemplateDeliveryAreaVO(deliveryAreaVO);
                    continue;
                }
            }
        }
        if (Objects.isNull(freightTemplateDeliveryAreaByStoreIdResponse)){
            resultResponse.setFlag(DefaultFlag.NO);
            return resultResponse;
        }
        //常规
        FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getFreightTemplateDeliveryAreaVO();
        if (Objects.isNull(freightTemplateDeliveryAreaVO)){
            resultResponse.setFlag(DefaultFlag.NO);
            return resultResponse;
        }
        List<TradeItemGroupVO> tradeItemGroupList = tradeItemQueryProvider.itemListByCustomerId(TradeItemByCustomerIdRequest.builder().customerId(customerId).build()).getContext().getTradeItemGroupList();
        Long tradeItemsNum = 0L;
        //购买商品数量统计
        log.info("数据集合"+tradeItemGroupList);
        AtomicReference<Long> ntradeItemsNum = new AtomicReference<>(0L);
        if(CollectionUtils.isNotEmpty(tradeItemGroupList)){
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
            tradeItemGroupList.forEach(v->{
                if (v.getSaleType().equals(SaleType.WHOLESALE)){
                    ntradeItemsNum.updateAndGet(v1 -> v1 + v.getTradeItems().stream().mapToLong(pa -> pa.getNum()).sum());
                }else if (v.getSaleType().equals(SaleType.BULK)){
                    GatherBoxSetInfoResponse context1 = gatherBoxSetProvider.getGatherBoxSetInfo().getContext();
                    if (Objects.isNull(context1)){
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "未查询到散批合并整个拆箱规格");
                    }
                    if (Objects.isNull(context1.getSkuNum()) || context1.getSkuNum()==0){
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, " 散批合并整个拆箱规格异常或者为null");
                    }
                    ntradeItemsNum.updateAndGet(v1 -> v1 + (v.getTradeItems().stream().mapToLong(pa -> pa.getNum()).sum())/context1.getSkuNum());

                }
            });
            List<List<TradeItemVO>> tradeItemVOS = tradeItemGroupList.stream().map(tg -> tg.getTradeItems()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(tradeItemVOS)){
                for (List<TradeItemVO> vos: tradeItemVOS) {
                    tradeItemsNum += vos.stream().mapToLong(v -> v.getNum()).sum();
                }
            }
        }
        log.info("ntradeItemsNum=========== {}",ntradeItemsNum);
        log.info("tradeItemsNum=========== {}",tradeItemsNum);
        tradeItemsNum=ntradeItemsNum.get();
        if ((Objects.nonNull(freightTemplateDeliveryAreaVO) && Objects.nonNull(freightTemplateDeliveryAreaVO.getDestinationArea())
                        && (ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getProvinceId().toString())
                        || ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getCityId().toString())))
        ) {
            if(tradeItemsNum.longValue() >= freightTemplateDeliveryAreaVO.getFreightFreeNumber() ){
                resultResponse.setFlag(DefaultFlag.YES);
                return resultResponse;
            }else {
                log.info("DefaultFlag.YES=========== {}", tradeItemsNum);
                resultResponse.setFlag(DefaultFlag.NO);
                return resultResponse;
            }
        }
        log.info("return DefaultFlag.NO=========== {}",tradeItemsNum);
        resultResponse.setFlag(DefaultFlag.NO);
        return resultResponse;
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
        Long noDeliveredCount = tradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount();
        // 部分发货
        tradeState.setFlowState(FlowState.DELIVERED_PART);
        queryRequest.setTradeState(tradeState);
        Long deliveredPartCount = tradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount();
        resp.setWaitDeliver(noDeliveredCount + deliveredPartCount);

        tradeState.setDeliverStatus(null);

        //设置待付款订单
        tradeState.setFlowState(FlowState.AUDIT);
        tradeState.setPayState(PayState.NOT_PAID);
        queryRequest.setTradeState(tradeState);
        resp.setWaitPay(tradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount());

        //设置待收货订单
        tradeState.setPayState(null);
        tradeState.setFlowState(FlowState.DELIVERED);
        queryRequest.setTradeState(tradeState);
        resp.setWaitReceiving(tradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder()
                .whereCriteria(queryRequest.getWhereCriteria()).tradePageDTO(queryRequest).build()).getContext().getCount());


        ReturnCountByConditionRequest returnQueryRequest = new ReturnCountByConditionRequest();
        returnQueryRequest.setBuyerId(commonUtil.getOperatorId());
        List<String> returnSateIdList = ReturnFlowState.getReturnSateIdListByFlag(1);
        returnQueryRequest.setReturnFlowStateIdList(returnSateIdList);
        Long waitRefund = returnOrderQueryProvider.countByCondition(returnQueryRequest).getContext().getCount();
        resp.setRefund(waitRefund);

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
//        Long goodsEvaluate =
//                goodsTobeEvaluateQueryProvider.getGoodsTobeEvaluateNum(GoodsTobeEvaluateQueryRequest.builder()
//                        .customerId(commonUtil.getOperatorId()).build()).getContext();
        // 店铺服务待评价
//        Long storeEvaluate =
//                storeTobeEvaluateQueryProvider.getStoreTobeEvaluateNum(StoreTobeEvaluateQueryRequest.builder()
//                        .customerId(commonUtil.getOperatorId()).build()).getContext();

        //客户囤货总数量
        PurchaseQueryRequest request = new PurchaseQueryRequest();
        request.setCustomerId(commonUtil.getOperatorId());
        Long pileCountGoodsNum = purchaseQueryProvider.getPileCountNumByCustomerId(request).getContext();

//        resp.setWaitEvaluate(goodsEvaluate + storeEvaluate);
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
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
        confirmRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
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


    /**
     * 手动调用补偿金蝶
     * @param request
     */
    @RequestMapping(value = "/compensationPush",method = RequestMethod.POST)
    public void kingdeePushCompensationOrder(@RequestBody TradePushKingdeeOrderRequest request){
        log.info("kingdeePushCompensationOrder req orderType:{} DateTime:{}",request.getOrderType(),request.getDateTime());
        tradeProvider.pushKingdeeCompensationOrder(request);
    }


    /**
     * 切换地址更换仓库时，如果在购物车或者快照页面则更新购物车和快照
     */
    @ApiOperation(value = "切换仓库更新快照和购物车")
    @RequestMapping(value = "/edit-warehouse-all", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit

    public BaseResponse editWarehouseAll(@RequestBody @Valid TradeItemConfirmWareHouseRequest confirmRequest) {

        log.info("===============confirmRequest:{}",JSONObject.toJSONString(confirmRequest));

        if(confirmRequest.getOldWareId() == null || confirmRequest.getOldWareId().longValue() <= 0){
            throw new SbcRuntimeException("K-000001");
        }

        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        if(wareId == null || wareId.longValue() <= 0){
            throw new SbcRuntimeException("K-000001");
        }

        if(confirmRequest.getOldWareId().longValue() == wareId.longValue()){
            return BaseResponse.SUCCESSFUL();
        }

        //加锁，防止同一用户在两个手机上同时下单，导致数据错误， 不用用户id去加错
        String customerId = commonUtil.getOperatorId();
        if (StringUtils.isEmpty(customerId)){
            throw new SbcRuntimeException("K-000001");
        }


        PurchaseSaveRequest request = new PurchaseSaveRequest();
        request.setWareId(wareId);
        request.setOldWareId(confirmRequest.getOldWareId());
        request.setBulkWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));
        request.setOldBulkwareId(confirmRequest.getOldBulkWareId());
        request.setOperationShopCarType(confirmRequest.getOperationShopCarType());

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }

        request.setCustomerId(commonUtil.getOperatorId());

        if (StringUtils.isBlank(request.getCustomerId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());

        // 切换购物车快照
        shopCartProvider.copySaveNew(request);

        if(confirmRequest.getOperationType().intValue() == 1){
            return BaseResponse.SUCCESSFUL();
        }

        //主仓
        if(wareId.longValue() == 1){
            if (CollectionUtils.isEmpty(confirmRequest.getTradeItems()) && CollectionUtils.isEmpty(confirmRequest.getRetailTradeItems())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单商品信息为空！");
            }
            confirmRequest.getTradeItems().forEach(tradeItemRequest -> {

                if (Objects.isNull(tradeItemRequest.getDevanningskuId())){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"" +
                            "");
                }
            });
        }else{
            if (CollectionUtils.isEmpty(confirmRequest.getTradeItems()) ) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单商品信息为空！");
            }
            confirmRequest.getTradeItems().forEach(tradeItemRequest -> {

                if (Objects.isNull(tradeItemRequest.getDevanningskuId())){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"" +
                            "");
                }
            });
            confirmRequest.getRetailTradeItems().clear();
        }

        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            //查询对应items的goos_info
            List<String> parentGoodsInfoIds = confirmRequest.getTradeItems().stream().map(TradeItemRequest::getParentGoodsInfoId).collect(Collectors.toList());
            DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest = DevanningGoodsInfoPageRequest.builder().parentGoodsInfoIds(parentGoodsInfoIds).wareId(wareId).build();
            log.info("===============goodsInfoListByConditionRequest:{}",JSONObject.toJSONString(devanningGoodsInfoPageRequest));
            List<DevanningGoodsInfoVO> newGoodsId = devanningGoodsInfoProvider.getQueryList(devanningGoodsInfoPageRequest).getContext().getDevanningGoodsInfoVOS();
            log.info("===============newGoodsId:{}",JSONObject.toJSONString(newGoodsId));
            if(newGoodsId.isEmpty() || newGoodsId == null){
                throw new SbcRuntimeException("K-050205");
            }

            if(confirmRequest.getTradeItems().size() != newGoodsId.size()){
                throw new SbcRuntimeException("K-050205");
            }

            List<TradeItemRequest> itemRequests = new ArrayList<>();
            for (TradeItemRequest tradeItem: confirmRequest.getTradeItems()) {
                for (DevanningGoodsInfoVO o : newGoodsId) {
                    if(o.getParentGoodsInfoId().equals(tradeItem.getParentGoodsInfoId())){
                        tradeItem.setSkuId(o.getGoodsInfoId());
                        tradeItem.setErpSkuNo(o.getErpGoodsInfoNo());
                        tradeItem.setDevanningskuId(o.getDevanningId());
                        itemRequests.add(tradeItem);
                        break;
                    }
                }
            }

            confirmRequest.setTradeItems(itemRequests);

            log.info("TradeBaseController.confirm lock:{}",customerId);
            List<TradeItemDTO> tradeItems = Collections.emptyList();
            List<TradeItemDTO> retailTradeItems = Collections.emptyList();
            List<GoodsInfoDTO> skuList = Collections.emptyList();
            List<GoodsInfoDTO> retailSkuList = Collections.emptyList();
            WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(confirmRequest.getWareId());
            //验证用户
            CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customerId)).getContext();
            if (CollectionUtils.isNotEmpty(newGoodsId)) {
                //批发
                //todo 逻辑修改前台传入参数会出现重复的skuid 需要合并成最小规格的数量
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
                tradeItems.forEach(v->{
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
                List<String> skuIds =
                        confirmRequest.getTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setGoodsInfoIds(skuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = shopCartQueryProvider.query(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
                if (skuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<Long> devanning = confirmRequest.getTradeItems().stream().map(TradeItemRequest::getDevanningskuId).collect(Collectors.toList());
                GoodsInfoResponse response = getDevanningGoodsResponseNew(devanning, wareId, wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
//                verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
//                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true,customerId));
                buyNumMaps.forEach((k,v)->{
                    GoodsInfoVO goodsInfoVO = response.getGoodsInfos().stream().filter(f -> k.equals(f.getGoodsInfoId())).findFirst().orElse(null);
                    log.info("goodsInfoVOresponse========= {}", JSONObject.toJSONString(response));
                    if(v.compareTo(goodsInfoVO.getStock()) == 1){
                        throw new SbcRuntimeException("K-050137");
                    }
                });
                // 2.填充商品区间价
                tradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(tradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);


                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                //营销活动校验
                List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
                        (), Collections.emptyList(), tradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
                confirmRequest.setTradeMarketingList(tradeMarketingList);
                skuList = KsBeanUtil.convertList(response.getGoodsInfos(),GoodsInfoDTO.class);
                //限定区域指定商品限购校验
                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
//                Map<String, Long> tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
                List<TradeItemDTO> tradeItemscopy =new ArrayList<>();


                KsBeanUtil.copyList(tradeItems,tradeItemscopy);
                tradeItemscopy.forEach(t->{
                    BigDecimal bigDecimal = t.getDivisorFlag().multiply(BigDecimal.valueOf(t.getNum())).setScale(2, BigDecimal.ROUND_UP);
                    t.setBNum(bigDecimal);
//                    t.setNum(bigDecimal.longValue());
                });

                Map<String, BigDecimal> tradeItemMap = tradeItemscopy.stream().collect(Collectors.groupingBy(TradeItemDTO::getSkuId, Collectors.reducing(BigDecimal.ZERO,TradeItemDTO::getBNum,BigDecimal::add)));


                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
                        List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        if (singleAreaList.contains(deliveryAddress.getProvinceId()) || singleAreaList.contains(deliveryAddress.getCityId())){
                            if (Objects.nonNull(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()))){
                                log.info("============>tradeItemMap.get(goodsInfoVO.getGoodsInfoId())：{}",tradeItemMap.get(goodsInfoVO.getGoodsInfoId()));
                                if(tradeItemMap.get(goodsInfoVO.getGoodsInfoId()).compareTo(BigDecimal.valueOf(goodsInfoVO.getSingleOrderPurchaseNum())) > 0){
                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+goodsInfoVO.getGoodsInfoName()+"，单笔订单超过限购数量："+goodsInfoVO.getSingleOrderPurchaseNum()+"件/箱，请修改此商品购买数量！");
                                }
                            }
                        }
                    }
                });
            }

            if (CollectionUtils.isNotEmpty(confirmRequest.getRetailTradeItems())) {
                //白鲸散批
                retailTradeItems = confirmRequest.getRetailTradeItems().stream().map(
                        o -> TradeItemDTO.builder().skuId(o.getSkuId()).erpSkuNo(o.getErpSkuNo()).num(o.getNum()).build()
                ).collect(Collectors.toList());
                //验证采购单
                List<String> retailSkuIds =
                        confirmRequest.getRetailTradeItems().stream().map(TradeItemRequest::getSkuId).collect(Collectors.toList());
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customerId);
                purchaseQueryRequest.setGoodsInfoIds(retailSkuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = retailShopCartQueryProvider.query(purchaseQueryRequest).getContext();
                List<PurchaseVO> exsistSku = purchaseQueryResponse.getPurchaseList();

                if (CollectionUtils.isEmpty(exsistSku)) {
                    throw new SbcRuntimeException("K-050205");
                }
                List<String> existIds = exsistSku.stream().map(PurchaseVO::getGoodsInfoId).collect(Collectors.toList());
                if (retailSkuIds.stream().anyMatch(skuId -> !existIds.contains(skuId))) {
                    throw new SbcRuntimeException("K-050205");
                }
                GoodsInfoResponse response = getGoodsRetailResponseNew(retailSkuIds, confirmRequest.getWareId(), wareHouseVO.getWareCode(), customer, confirmRequest.getMatchWareHouseFlag());
                //商品验证
                verifyQueryProvider.verifyGoods(new VerifyGoodsRequest(retailTradeItems, Collections.emptyList(),
                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class), null, false, true,customerId));


                retailTradeItems = KsBeanUtil.convertList(
                        verifyQueryProvider.verifyGoods(
                                new VerifyGoodsRequest(retailTradeItems, Collections.emptyList(),
                                        KsBeanUtil.convert(response, TradeGoodsInfoPageDTO.class),
                                        null, false, true,customer.getCustomerId())).getContext().getTradeItems(), TradeItemDTO.class);


                verifyQueryProvider.verifyStore(new VerifyStoreRequest(response.getGoodsInfos().stream().map
                        (GoodsInfoVO::getStoreId).collect(Collectors.toList())));
                //营销活动校验
                List<TradeMarketingDTO> tradeMarketingList = verifyQueryProvider.verifyTradeMarketing(new VerifyTradeMarketingRequest(confirmRequest.getTradeMarketingList
                        (), Collections.emptyList(), retailTradeItems, customerId, confirmRequest.isForceConfirm(), confirmRequest.getWareId())).getContext().getTradeMarketingList();
                confirmRequest.setRetailTradeMarketingList(tradeMarketingList);
                retailSkuList = KsBeanUtil.convertList(response.getGoodsInfos(),GoodsInfoDTO.class);
                //限定区域指定商品限购校验
                CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
                Map<String, Long> tradeItemMap = retailTradeItems.stream().collect(Collectors.toMap(TradeItemDTO::getSkuId, TradeItemDTO::getNum));
                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (Objects.nonNull(goodsInfoVO.getSingleOrderPurchaseNum()) && Objects.nonNull(goodsInfoVO.getSingleOrderAssignArea())){
                        List<Long> singleAreaList = Arrays.asList(goodsInfoVO.getSingleOrderAssignArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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
            }

            return tradeItemProvider.snapshotRetail(TradeItemSnapshotRequest.builder().customerId(customerId).tradeItems(tradeItems)
                    .tradeMarketingList(confirmRequest.getTradeMarketingList())
                    .skuList(skuList).retailTradeItems(retailTradeItems)
                    .retailtTradeMarketingList(confirmRequest.getRetailTradeMarketingList())
                    .retailSkuList(retailSkuList).build());
        }catch (SbcRuntimeException e){
//            log.error("TradeBaseController.confirm error:{}",e.getMessage());
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        }finally {
            log.info("TradeBaseController.confirm unlock:{}",customerId);
            rLock.unlock();
        }
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
                Long min = Long.min(purchaseNum, perUserPurchaseNum);
                return Long.max(min, 0L);
            }
            if(Objects.nonNull(purchaseNum)){
                return Long.max(purchaseNum, 0L);
            }
            return Long.max(perUserPurchaseNum, 0L);
        }

        // 3. 有购买记录-两个限购均有
        if(CollectionUtils.isNotEmpty(purchaseLimits) && CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
            Long l1 =  Long.max(purchaseNum - marketingNum.get().longValue(), 0L);
            if(Objects.isNull(perUserPurchaseNum)){
                return l1;
            }
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
     * 查询物流地图
     * @param trackParam
     * @return
     */
    @ApiOperation(value = "查询带地图的物流信息", notes = "物流信息")
    @RequestMapping(value = "/express/queryTrack", method = RequestMethod.POST)
    public ResponseEntity<QueryTrackMapResp> queryTrackMap(@RequestBody QueryTrackParam trackParam){
        return ResponseEntity.ok(deliveryQueryProvider.queryExpressMapObj(trackParam).getContext());
    }

    /**
     * 批量查询物流地图
     * @param trackListParam
     * @return
     */
    @ApiOperation(value = "批量查询物流地图轨迹信息", notes = "物流信息")
    @RequestMapping(value = "/express/batchQueryTrack", method = RequestMethod.POST)
    public ResponseEntity<QueryTrackMapListResp> queryTrackMap(@RequestBody QueryTrackListParam trackListParam){
        return ResponseEntity.ok(deliveryQueryProvider.batchQueryExpressMapObj(trackListParam).getContext());
    }

    @GetMapping("/del/{tradeId}")
    public BaseResponse deleteByTradeId(@PathVariable String tradeId) {
        tradeProvider.deleteByTradeId(tradeId);
        return BaseResponse.SUCCESSFUL();
    }

    @PostMapping("/getFreightTempDesc")
    public BaseResponse<FreightTempDescVO> getTmsFreightTempDesc(@RequestBody FreightTempDescQueryDTO queryDTO) {
        if(queryDTO==null || StringUtils.isBlank( queryDTO.getDeliveryAddressId())||queryDTO.getDeliveryWay()==null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
       String result = tradeProvider.getTmsFreightTempDesc(queryDTO).getContext();
        FreightTempDescVO freightTempDescVO = new FreightTempDescVO();
        freightTempDescVO.setFreightTempDesc(result);
        return BaseResponse.success(freightTempDescVO);
    }

//
//    private List<TradeMarketingDTO> filterOverPurchuseLimitV2(List<TradeMarketingDTO> tradeMarketingList, HashMap<String, BigDecimal> buyNumMaps,String customerId) {
//        if(CollectionUtils.isEmpty(tradeMarketingList)){
//            return tradeMarketingList;
//        }
//
//        Map<Long, Set<String>> marketingIdAndSkuIdsMappings = Maps.newHashMap();
//        Map<Long, Set<String>> validMarketingIdAndSkuIdsMappings = Maps.newHashMap();
//        for (TradeMarketingDTO tradeMarketingDTO : tradeMarketingList){
//            Long marketingId = tradeMarketingDTO.getMarketingId();
//            Set<String> skuIds = Optional.ofNullable(marketingIdAndSkuIdsMappings.get(marketingId)).orElse(Sets.newHashSet());
//            skuIds.addAll(Optional.ofNullable(tradeMarketingDTO.getSkuIds()).orElse(Lists.newArrayList()));
//        }
//        if(MapUtils.isEmpty(marketingIdAndSkuIdsMappings)){
//            return tradeMarketingList;
//        }
//        for (Map.Entry<Long, Set<String>> marketingIdAndSkuIds : marketingIdAndSkuIdsMappings.entrySet()) {
//            Long marketingId = marketingIdAndSkuIds.getKey();
//            Set<String> skuIds = marketingIdAndSkuIds.getValue();
//            if(CollectionUtils.isEmpty(skuIds)){
//                continue;
//            }
//            for (String skuId : skuIds) {
//                BigDecimal buyNum = buyNumMaps.get(skuId);
//                if(Objects.isNull(buyNum)){
//                    continue;
//                }
//                // 处理营销活动
//                validMarketingIdAndSkuIdsMappings
//            }
//        }
//
//        // 过滤营销活动
//
//    }
}

