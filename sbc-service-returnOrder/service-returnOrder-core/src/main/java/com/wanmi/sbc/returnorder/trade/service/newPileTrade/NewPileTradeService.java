package com.wanmi.sbc.returnorder.trade.service.newPileTrade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wanmi.sbc.account.api.provider.finance.record.AccountRecordProvider;
import com.wanmi.sbc.account.api.provider.offline.OfflineQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordAddRequest;
import com.wanmi.sbc.account.api.request.offline.OfflineAccountGetByIdRequest;
import com.wanmi.sbc.account.api.request.wallet.AddWalletRecordRecordRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletRequest;
import com.wanmi.sbc.account.api.response.offline.OfflineAccountGetByIdResponse;
import com.wanmi.sbc.account.bean.enums.*;
import com.wanmi.sbc.common.base.*;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.enums.node.DistributionType;
import com.wanmi.sbc.common.enums.node.OrderProcessType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.invitationstatistics.InvitationStatisticsProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationTradeStatisticsRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelMapByCustomerIdAndStoreIdsRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateStoreQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsSaveProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailAddRequest;
import com.wanmi.sbc.goods.api.request.flashsalegoods.FlashSaleGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateStoreListByStoreIdAndDeleteFlagRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingCouponPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeBatchModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeReturnByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingMapGetByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.TerminationMarketingScopeRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityStockRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingCouponWrapperRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingTradeBatchWrapperRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingEffectiveRespose;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingCouponWrapperResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeBatchModifyDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.pay.api.provider.*;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.vo.PayChannelItemVO;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayConstants;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import com.wanmi.sbc.returnorder.api.constant.OrderCancelType;
import com.wanmi.sbc.returnorder.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.returnorder.api.provider.shopcart.ShopCartNewPileProvider;
import com.wanmi.sbc.returnorder.api.provider.shopcart.ShopCartNewPileQueryProvider;
import com.wanmi.sbc.returnorder.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.returnorder.api.request.InventoryDetailSamount.SubInventoryDetailSamountRequest;
import com.wanmi.sbc.returnorder.api.request.growthvalue.OrderGrowthValueTempQueryRequest;
import com.wanmi.sbc.returnorder.api.request.paycallbackresult.PayCallBackResultQueryRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseBatchSaveRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseDeleteRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseOrderMarketingRequest;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.purchase.PurchaseOrderMarketingResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeGetGoodsResponse;
import com.wanmi.sbc.returnorder.ares.service.OrderAresService;
import com.wanmi.sbc.returnorder.bean.dto.*;
import com.wanmi.sbc.returnorder.bean.enums.*;
import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeGoodsListVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.common.OperationLogMq;
import com.wanmi.sbc.returnorder.common.OrderCommonService;
import com.wanmi.sbc.returnorder.constant.OrderErrorCode;
import com.wanmi.sbc.returnorder.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.returnorder.growthvalue.model.root.OrderGrowthValueTemp;
import com.wanmi.sbc.returnorder.growthvalue.repository.OrderGrowthValueTempRepository;
import com.wanmi.sbc.returnorder.growthvalue.service.OrderGrowthValueTempService;
import com.wanmi.sbc.returnorder.historytownshiporder.model.root.HistoryTownShipOrder;
import com.wanmi.sbc.returnorder.historytownshiporder.repository.HistoryTownShipOrderRepository;
import com.wanmi.sbc.returnorder.historytownshiporder.response.TrueStock;
import com.wanmi.sbc.returnorder.historytownshiporder.service.HistoryTownShipOrderService;
import com.wanmi.sbc.returnorder.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.returnorder.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.orderinvoice.model.root.OrderInvoice;
import com.wanmi.sbc.returnorder.orderinvoice.request.OrderInvoiceSaveRequest;
import com.wanmi.sbc.returnorder.orderinvoice.service.OrderInvoiceService;
import com.wanmi.sbc.returnorder.paycallbackresult.model.root.PayCallBackResult;
import com.wanmi.sbc.returnorder.paycallbackresult.service.PayCallBackResultService;
import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.payorder.repository.PayOrderRepository;
import com.wanmi.sbc.returnorder.payorder.request.PayOrderGenerateRequest;
import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
import com.wanmi.sbc.returnorder.payorder.service.PayOrderService;
import com.wanmi.sbc.returnorder.pickuprecord.model.root.PickUpRecord;
import com.wanmi.sbc.returnorder.pickuprecord.service.PickUpRecordService;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchase;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseRepository;
import com.wanmi.sbc.returnorder.receivables.model.root.Receivable;
import com.wanmi.sbc.returnorder.receivables.repository.ReceivableRepository;
import com.wanmi.sbc.returnorder.receivables.request.ReceivableAddRequest;
import com.wanmi.sbc.returnorder.receivables.service.ReceivableService;
import com.wanmi.sbc.returnorder.redis.RedisCache;
import com.wanmi.sbc.returnorder.redis.RedisKeyConstants;
import com.wanmi.sbc.returnorder.refund.service.RefundFactory;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.repository.NewPileReturnOrderRepository;
import com.wanmi.sbc.returnorder.returnorder.request.NewPileReturnQueryRequest;
import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.returnorder.shopcart.ShopCartService;
import com.wanmi.sbc.returnorder.shopcart.request.ShopCartRequest;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.*;
import com.wanmi.sbc.returnorder.trade.model.entity.value.*;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.*;
import com.wanmi.sbc.returnorder.trade.model.root.*;
import com.wanmi.sbc.returnorder.trade.newpilefsm.NewPileTradeFSMService;
import com.wanmi.sbc.returnorder.trade.repository.TradeCachePushKingdeeOrderRepository;
import com.wanmi.sbc.returnorder.trade.repository.TradeGroupRepository;
import com.wanmi.sbc.returnorder.trade.repository.TradePushKingdeePayRepository;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.GoodsPickStockRepository;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.NewPileOldDataRepository;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.NewPileSendDataRepository;
import com.wanmi.sbc.returnorder.trade.repository.newPileTrade.NewPileTradeRepository;
import com.wanmi.sbc.returnorder.trade.request.NewPileTradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.request.TradeParams;
import com.wanmi.sbc.returnorder.trade.request.TradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.service.TradeCacheService;
import com.wanmi.sbc.returnorder.trade.service.TradeItemService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.trade.service.VerifyService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.update.NewPilePickTradeItemSnapshotService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.update.NewPileTradeGroupService;
import com.wanmi.sbc.returnorder.util.KingdeeLoginUtils;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.provider.region.RegionQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyListRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyListResponse;
import com.wanmi.sbc.setting.api.response.region.RegionQueryResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.enums.LogisticsType;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import com.wanmi.sbc.setting.bean.vo.RegionCopyVO;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletGiveRequest;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSOrderProvider;
import com.wanmi.sbc.wms.api.request.wms.WMSPushOrderDetailsRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSPushOrderRequest;
import com.wanmi.sbc.wms.api.response.wms.ResponseWMSReturnResponse;
import com.wanmi.sbc.wms.bean.vo.ERPWMSConstants;
import com.wanmi.sbc.wms.bean.vo.WmsErpIdConstants;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewPileTradeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${send.order.last.month}")
    private String sendOrderLastMonth;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;

    @Autowired
    private CustomerAreaLimitDetailProvider customerAreaLimitDetailProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private HistoryTownShipOrderService historyTownShipOrderService;

    @Autowired
    private RequestWMSOrderProvider requestWMSOrderProvider;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private ReceivableService receivableService;

    @Autowired
    private OrderInvoiceService orderInvoiceService;

    @Autowired
    private PayOrderRepository payOrderRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AccountRecordProvider accountRecordProvider;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private PayProvider payProvider;
    /**
     * 注入消费记录生产者service
     */
    @Autowired
    public OrderProducerService orderProducerService;

    @Autowired
    public PointsGoodsSaveProvider pointsGoodsSaveProvider;

    @Autowired
    private PickUpRecordService pickUpRecordService;

    @Autowired
    private HistoryTownShipOrderRepository historyTownShipOrderRepository;

    @Autowired
    private NewPileSendDataRepository newPileSendDataRepository;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private InvitationStatisticsProvider invitationStatisticsProvider;

    @Autowired
    private TradeCachePushKingdeeOrderRepository tradeCachePushKingdeeOrderRepository;

    @Autowired
    CupsPayProvider cupsPayProvider;

    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    @Autowired
    private NewPileTradeFSMService pileTradeFSMService;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;

    @Autowired
    private MarketingScopeProvider marketingScopeProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private OfflineQueryProvider offlineQueryProvider;

    @Autowired
    private PayCallBackResultService payCallBackResultService;

    @Autowired
    private OrderAresService orderAresService;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.pay.url}")
    private String payUrl;

    @Value("${kingdee.orderInvalid.url}")
    private String orderInvalidUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.alipay}")
    private String kingdeeAlipay;

    @Value("${kingdee.wechat}")
    private String kingdeeWechat;

    @Value("${kingdee.unionpay}")
    private String kingdeeUnionpay;

    @Value("${kingdee.ccb}")
    private String kingdeeCcbpay;

    @Value("${kingdee.bocom}")
    private String kingdeeBocomPay;

    /**
     * 是否开启新金蝶
     */
    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;

    @Autowired
    private NewPileTradeRepository newPileTradeRepository;

    @Autowired
    private NewPilePickTradeItemSnapshotService newPilePickTradeItemSnapshotService;

    @Autowired
    private NewPileTradeItemService newPileTradeItemService;

    @Autowired
    private NewPileTradeFSMService newPileTradeFSMService;

    @Autowired
    private GoodsPickStockRepository goodsPickStockRepository;

    @Autowired
    private NewPileReturnOrderRepository newPileReturnOrderRepository;

    @Autowired
    private NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    private TradeGroupRepository tradeGroupRepository;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private NewPileTradeGroupService newPileTradeGroupService;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;

    @Autowired
    private InventoryDetailSamountService inventoryDetailSamountService;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Autowired
    private TradeItemService tradeItemService;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private MarketingCouponPluginProvider marketingCouponPluginProvider;

    @Autowired
    private TradePushKingdeePayRepository tradePushKingdeePayRepository;

    @Autowired
    private ShopCartService shopCartService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ShopCartNewPileQueryProvider shopCartNewPileQueryProvider;

    @Autowired
    private ShopCartNewPileProvider shopCartNewPileProvider;

    @Autowired
    private PilePurchaseRepository pilePurchaseRepository;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private NewPilePickTradeItemService newPilePickTradeItemService;

    @Autowired
    private TradeCacheService tradeCacheService;

    @Autowired
    private FreightTemplateStoreQueryProvider freightTemplateStoreQueryProvider;

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    @Lazy
    private TradeService tradeService;

    @Autowired
    private RegionQueryProvider regionQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private OrderGrowthValueTempService orderGrowthValueTempService;

    @Autowired
    private OrderGrowthValueTempRepository orderGrowthValueTempRepository;

    @Autowired
    private WxPayProvider wxPayProvider;


    @Autowired
    private NewPileOldDataRepository newPileOldDataRepository;
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private OrderCommonService orderCommonService;

    @Autowired
    private com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider walletCustomerWalletProvider;

    @Autowired
    private WalletMerchantProvider walletMerchantProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;



    /**
     * C端下单（囤货整箱）
     */
    @Transactional
    public List<NewPileTradeCommitResult> newPileCommitAll(TradeCommitRequest tradeCommitRequest) {
        // 验证用户
        CustomerVO customer = verifyService.verifyCustomer(tradeCommitRequest.getOperator().getUserId());
        tradeCommitRequest.setCustomer(customer);

        CusWalletVO cusWalletVO = tradeService.checkoutWallet(tradeCommitRequest, customer);

        //验证并填充物流公司信息
        checkLogsitcCompanyInfo(tradeCommitRequest.getStoreCommitInfoList());

        Operator operator = tradeCommitRequest.getOperator();
        //查询mongo
        List<TradeItemGroup> tradeItemGroups = newPileTradeItemService.findAll(customer.getCustomerId());
        // 如果为PC商城下单，将分销商品变为普通商品
        if (ChannelType.PC_MALL.equals(tradeCommitRequest.getDistributeChannel().getChannelType())) {
            tradeItemGroups.forEach(tradeItemGroup ->
                    tradeItemGroup.getTradeItems().forEach(tradeItem -> {
                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    })
            );
        }
        List<String> skuslist = new LinkedList<>();
        List<TradeItem> tradeItemList = new LinkedList<>();
        //校验库存有效性
        tradeItemGroups.forEach(v -> {
            skuslist.addAll(v.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList()));
            tradeItemList.addAll(v.getTradeItems());
        });

        Map<String, Long> collectNum = tradeItemList.stream().collect(
                Collectors.groupingBy(
                        TradeItem::getSkuId, Collectors.summingLong(TradeItem::getNum))
        );

        BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();
        if (CollectionUtils.isEmpty(startPileActivity.getContext())) {
            throw new SbcRuntimeException("K-050137", "无正在进行中的囤货活动");
        }
        //todo 多商家囤货获取参与囤货活动商品虚拟库存
        List<PileActivityGoodsVO> pileActivitycontext = pileActivityProvider.getStartPileActivityPileActivityGoods(
                PileActivityPileActivityGoodsRequest.builder().goodsInfoIds(skuslist).build()).getContext();
        Map<String, String> pileActivityGoodsMap = pileActivitycontext.stream()
                .collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, PileActivityGoodsVO::getActivityId, (k1, k2) -> k2));


        if (CollectionUtils.isNotEmpty(pileActivitycontext)) {
            pileActivitycontext.forEach(var -> {
                if (var.getVirtualStock() < collectNum.get(var.getGoodsInfoId())) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在商品囤货库存不足" + JSONObject.toJSONString(var.getGoodsInfoId()));
                }
            });
        }

        log.info("==========tradeItemGroups:{},skuslist:{}", JSONObject.toJSONString(tradeItemGroups), JSONObject.toJSONString(skuslist));

        // 1.验证失效的营销信息(目前包括失效的赠品、满系活动、优惠券)
        verifyService.verifyInvalidMarketingsNewPile(tradeCommitRequest, tradeItemGroups, customer);

        // 2.按店铺包装多个订单信息、订单组信息
        List<NewPileTrade> trades = newPileTradeList(tradeCommitRequest, tradeItemGroups);

        NewPileTradeGroup tradeGroup = wrapperTradeGroupNewPile(trades, tradeCommitRequest);

        //满订单优惠
        orderMarketingNewPile(trades);

        //使用余额, 重新计算应付金额
        useWallet(trades, tradeCommitRequest, customer, cusWalletVO);
        // 3.批量提交订单
        List<NewPileTradeCommitResult> successResults;
        if (tradeGroup != null) {
            successResults = createBatchWithGroupNewPile(trades, tradeGroup, operator, pileActivityGoodsMap);
        } else {
            successResults = createBatchNewPile(trades, operator, pileActivityGoodsMap);
        }

        try {
            // 4.订单提交成功，删除关联的采购单商品
            trades.forEach(
                    trade -> {
                        List<String> tradeSkuIds =
                                trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
                        //囤货
                        List<Long> tradeDevannings =
                                trade.getTradeItems().stream().map(TradeItem::getDevanningId).collect(Collectors.toList());
                        deleteShopCartOrderdevanningNewPile(customer.getCustomerId(), tradeSkuIds, tradeDevannings,
                                tradeCommitRequest.getDistributeChannel(), tradeCommitRequest.getWareId());
                    }
            );

            if (kingdeeOpenState) {
                trades.stream().forEach(trade -> {
//            trades.stream().forEach(trade -> {
//                if (!orderkingdeeCheck(trade)) {
//                    throw new SbcRuntimeException("K-020010");
//                }
                    log.info("NewPileTradeService.createBatch tradeId:{}", trade.getId());
                    orderProducerService.newPilekingdeePushShopCartOrder(trade.getId(), 10 * 1000L);
                });
            }

            // 5.订单提交成功，删除订单商品快照
            newPileTradeItemService.remove(customer.getCustomerId());
        } catch (Exception e) {
            log.error("Delete the trade sku list snapshot or the purchase order exception," +
                            "trades={}," +
                            "customer={}",
                    JSONObject.toJSONString(trades),
                    customer,
                    e
            );
        }
        return successResults;
    }

    public void useWallet(List<NewPileTrade> trades, TradeCommitRequest tradeCommitRequest, CustomerVO customer, CusWalletVO cusWalletVO) {
        BigDecimal walletBalance = Optional.ofNullable(tradeCommitRequest).map(TradeCommitRequest::getWalletBalance).orElse(BigDecimal.ZERO);
        //校验
        if (walletBalance == null || !(walletBalance.compareTo(BigDecimal.ZERO) > 0)) {
            return;
        }
//        if (walletBalance.compareTo(BigDecimal.ZERO) > 0) {
//            throw new SbcRuntimeException("K-000099", "鲸币暂停使用，请关闭使用鲸币选项，请联系客服！！！");
//        }
        //可用余额
        BigDecimal balance = cusWalletVO.getBalance();

        //运费问题: 如果有运费, 则商品应付金额可以用余额全部抵扣, 如果没有运费, 则商品应付金额应该保留1元, 其他用余额抵扣均摊
        //订单是否存在运费
        boolean booleanBeliveryPrice = trades.stream().anyMatch(trade -> trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) == 1);

        //1 需要余额扣减的订单商品
        List<TradeItem> collect = trades.stream().flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors.toList());
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));

        //最低商品支付金额
        Long totalNum = collect.stream().map(TradeItem::getNum).reduce(0L, Long::sum);
        //BigDecimal reduce = collect.stream().map(TradeItem::getBNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bottomPrice = new BigDecimal("0.02").multiply(new BigDecimal(totalNum));

        //2 订单商品总价
        BigDecimal total = tradeItemService.calcSkusTotalPrice(collect);

        //2.1 新 订单商品总价 (没有加运费)
       /* BigDecimal newTotal = booleanBeliveryPrice
                ? total.compareTo(balance) > 0 ? total.subtract(balance) : BigDecimal.ZERO
                : total.subtract(BigDecimal.ONE).compareTo(balance) > 0 ? total.subtract(balance) : BigDecimal.ONE;*/
        BigDecimal newTotal = total.subtract(bottomPrice).compareTo(balance) > 0 ? total.subtract(balance) : bottomPrice;

        //此次扣减的余额
        BigDecimal deductionTotal = total.subtract(newTotal);

        //3 设置关联商品的结算信息
        for (TradeItem tradeItem : collect) {
            tradeItem.getWalletSettlements().add(
                    TradeItem.WalletSettlement.builder().reduceWalletPrice(balance.subtract(newTotal)).splitPrice(tradeItem.getSplitPrice()).build()
            );
        }

        //4 设置关联商品的均摊价格 (已修改均摊价格)
        tradeItemService.calcSplitPrice(collect, newTotal, total);

        //5 刷新关联商品的结算信息
        for (TradeItem tradeItem : collect) {
            TradeItem.WalletSettlement walletSettlement = tradeItem.getWalletSettlements().get(tradeItem.getWalletSettlements().size() - 1);
            walletSettlement.setReduceWalletPrice(walletSettlement.getSplitPrice().subtract(tradeItem.getSplitPrice()));
            walletSettlement.setSplitPrice(tradeItem.getSplitPrice());
        }

        //6 按照店铺分组被均摊的商品, 刷新相应的订单的价格信息
        Map<Long, List<TradeItem>> itemsMap = collect.stream().collect(Collectors.groupingBy(TradeItem::getStoreId));

        for (Long storeIdKey : itemsMap.keySet()) {
            //找到店铺对应订单的价格信息
            NewPileTrade trade = trades.stream().filter(t -> t.getSupplier().getStoreId().equals(storeIdKey)).findFirst().orElse(null);
            List<TradeItem> tradeItems = itemsMap.get(storeIdKey);
            //订单金额, (余额暂未修改)
            TradePrice tradePrice = trade.getTradePrice();

            //设置余额扣减金额
            BigDecimal sumReduceWalletPrice = tradeItems
                    .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                    .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tradePrice.setBalancePrice(sumReduceWalletPrice);

            //商品集合的均摊总价
            BigDecimal couponTotalPrice = tradeItemService.calcSkusTotalPrice(itemsMap.get(storeIdKey));
            //重设订单总价(并追加配送费用)
            //tradePrice.setTotalPrice(couponTotalPrice);
            //tradePrice.setOriginPrice(tradePrice.getGoodsPrice());
            tradePrice.setTotalPrice(couponTotalPrice.add(tradePrice.getDeliveryPrice()));

            trade.getTradeItems().forEach(tradeItem -> {
                TradeItem matchItem = collect.stream().filter(item -> item.getSkuId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
                if (Objects.nonNull(tradeItem.getDevanningId())) {
                    matchItem = collect.stream().filter(item -> item.getDevanningId().equals(tradeItem.getDevanningId())).findFirst().orElse(null);
                }
                if (matchItem != null) {
                    tradeItem.setSplitPrice(matchItem.getSplitPrice());
                }
            });
        }
        log.info("================ trades ================:{}", JSONObject.toJSONString(trades));
    }


    /**
     * 取消订单
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public BaseResponse cancel(TradeCancelRequest request) {
        if (StringUtils.isBlank(request.getCancelType())) {
            request.setCancelType(OrderCancelType.manualCancel);
        }
        NewPileTrade trade = checkTradeForCancel(request.getTid());
        doCancel(request, trade);
        return BaseResponse.SUCCESSFUL();
    }

    private void doCancel(TradeCancelRequest request, NewPileTrade trade) {
        if (trade.getTradeState().getAuditState() == AuditState.CHECKED) {
            //删除支付单
            payOrderService.deleteByPayOrderId(trade.getPayOrderId());
        }

        //囤货单取消推送金蝶
        if (kingdeeOpenState) {//判断是否开启新金蝶
            //todo 囤货单取消推送金蝶
        }

        //恢复囤货虚拟库存
        returnVirtualStock(trade);
        //释放营销限购库存
        calMarketGoodsNum(trade.getTradeItems(), true);

        //状态变更： 默认手动取消
        TradeEvent tradeEvent = TradeEvent.NEW_PILE_CANCEL;
        String desc = "用户取消订单";
        if (Objects.equals(request.getCancelType(), OrderCancelType.autoCancel)) {
            tradeEvent = TradeEvent.VOID;
            desc = "订单超时未支付，系统自动取消";
        } else if (Objects.equals(request.getCancelType(), OrderCancelType.rejectCancel)) {
            tradeEvent = TradeEvent.VOID;
            desc = "线下付款驳回：" + request.getReason();
        }

        StateRequest stateRequest = StateRequest
                .builder()
                .tid(trade.getId())
                .operator(request.getOperator())
                .event(tradeEvent)
                .data(desc)
                .build();
        changeState(trade, stateRequest, request.getOperator());

        //囤货 用户取消订单，订单超时未支付，线下付款驳回退优惠券
        returnCoupon(request.getTid(), null);
        //DONE: 退款开关，囤货退款-取消订单
        // 退余额
        RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_CANCEL)
                .refund(trade, null);

    }

    private void returnVirtualStock(NewPileTrade trade) {
        if (StringUtils.isNoneEmpty(trade.getPileActiviteId())) {
            List<PileActivityStockRequest> pileActivityStock = new ArrayList<>();
            trade.getTradeItems().forEach(item -> {
                pileActivityStock.add(PileActivityStockRequest.builder()
                        .activityId(trade.getPileActiviteId())
                        .addOrSub(false)
                        .goodsInfoId(item.getSkuId())
                        .num(item.getNum().intValue())
                        .build());
            });
            pileActivityProvider.updateVirtualStock(pileActivityStock);
        }
    }


    /**
     * 线下付款审核：驳回
     * @param tid
     * @param auditState
     * @param reason
     * @param operator
     * @param financialFlag
     */
    public void reject(String tid, AuditState auditState, String reason, Operator operator, Boolean financialFlag) {
        NewPileTrade trade = checkTradeForCancel(tid);

        if (operator.getPlatform() != Platform.BOSS && operator.getPlatform() != Platform.SUPPLIER &&
                operator.getPlatform() != Platform.PLATFORM) {
            //K-000014=此功能您没有权限访问，请联系管理员修改权限
            throw new SbcRuntimeException("K-000014");
        }
        if (auditState != AuditState.REJECTED) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "调用驳回接口参数错误");
        }

        if(!financialFlag){
            if (trade.getTradeState().getAuditState() != AuditState.NON_CHECKED) {
                //K-050316=当前订单审核状态已发生变化，请刷新页面查看更新
                throw new SbcRuntimeException("K-050316");
            }
        }

        //删除支付单
        if (financialFlag) {
            payOrderService.deleteByPayOrderId(trade.getPayOrderId());
        }

        TradeCancelRequest request = new TradeCancelRequest();
        request.setTid(tid);
        request.setOperator(operator);
        request.setCancelType(OrderCancelType.rejectCancel);
        request.setReason(reason);

        doCancel(request,trade);
    }

    private void changeState(NewPileTrade trade, StateRequest request, Operator operator) {
        NewPileTradeState tradeState = trade.getTradeState();
        logger.info("evaluateInternal------------->" + JSONObject.toJSONString(tradeState));
        tradeState.setEndTime(LocalDateTime.now());
        tradeState.setFlowState(NewPileFlowState.VOID);
        tradeState.setObsoleteReason(Objects.toString(request.getData(), ""));

        String detail = String.format("[%s]作废了订单%s", operator.getName(), trade.getId());
        trade.appendTradeEventLog(TradeEventLog
                .builder()
                .operator(operator)
                .eventTime(LocalDateTime.now())
                .eventType(NewPileFlowState.VOID.getDescription())
                .eventDetail(detail)
                .build());

        updateTrade(trade);
        operationLogMq.convertAndSend(operator, NewPileFlowState.VOID.getDescription(), detail);

        // 删除订单成长值临时表中的数据
        List<OrderGrowthValueTemp> result = orderGrowthValueTempService.list(
                OrderGrowthValueTempQueryRequest.builder().orderNo(trade.getId()).build());
        if (CollectionUtils.isNotEmpty(result)) {
            orderGrowthValueTempRepository.deleteAll(result);
        }

    }

    /**
     * C端下单（提货整箱）
     */
    @Transactional
    @LcnTransaction
    public List<TradeCommitResult> newPilePickCommitAll(TradeCommitRequest tradeCommitRequest) {

        // 验证用户
        CustomerVO customer = verifyService.verifyCustomer(tradeCommitRequest.getOperator().getUserId());
        tradeCommitRequest.setCustomer(customer);

        //验证并填充物流公司信息
        checkLogsitcCompanyInfo(tradeCommitRequest.getStoreCommitInfoList());

        Operator operator = tradeCommitRequest.getOperator();

        //查询mongo
        List<TradeItemGroup> tradeItemGroups = newPilePickTradeItemSnapshotService.findAll(customer.getCustomerId());

        // 1.校验库存有效性(去除乡镇件库存)-------------start
        //全部skuId
        Set<String> skuslist = new HashSet<>();
        //以skuid维度，对应提货商品数量
        Map<String, Long> pickSkuNumMap = new HashMap<>();

        List<NewPileTrade> allDetailByCustomerId = getAllDetailByCustomerId(customer.getCustomerId());

        List<String> newPileTradeNolist = allDetailByCustomerId.stream().map(NewPileTrade::getId).collect(Collectors.toList());
        //校验提货数量是否可提及形成对应数据结构
        tradeItemGroups.forEach(v -> {
            v.getTradeItems().forEach(tradeItem -> {
                skuslist.add(tradeItem.getSkuId());
                if (pickSkuNumMap.containsKey(tradeItem.getSkuId())) {
                    pickSkuNumMap.put(tradeItem.getSkuId(), pickSkuNumMap.get(tradeItem.getSkuId()) + tradeItem.getNum());
                } else {
                    pickSkuNumMap.put(tradeItem.getSkuId(), tradeItem.getNum());
                }
            });
        });

        log.info("==========tradeItemGroups:{},skuslist:{}", JSONObject.toJSONString(tradeItemGroups), JSONObject.toJSONString(skuslist));

        //获得对应囤货订单并转为map
        Map<String, NewPileTrade> pileTradeMap = allDetailByCustomerId.stream().collect(Collectors.toMap(NewPileTrade::getId, g -> g, (a, b) -> a));

        //获取对应囤货单的囤货数量
        List<GoodsPickStock> getPileStock = goodsPickStockRepository.getPileStock(new ArrayList<>(newPileTradeNolist), new ArrayList<>(skuslist));
        if (CollectionUtils.isEmpty(getPileStock)) {
            throw new SbcRuntimeException("k-030301", "无可提商品");
        }
        //囤货商品库存
        Map<String, Long> skuStockMap = getPileStock.stream().filter(i -> i.getStock() != null).
                collect(Collectors.groupingBy(GoodsPickStock::getGoodsInfoId, Collectors.summingLong(GoodsPickStock::getStock)));
        //校验实际库存
        Map<String, BigDecimal> collect = historyTownShipOrderService.getskusstock(new ArrayList<>(skuslist)).stream().collect(Collectors.toMap(TrueStock::getSkuid, TrueStock::getStock, (a, b) -> a));

        for (Map.Entry<String, Long> entry : pickSkuNumMap.entrySet()) {
            if (BigDecimal.valueOf(entry.getValue()).compareTo(collect.getOrDefault(entry.getKey(), BigDecimal.ZERO)) > 0
                    || skuStockMap.get(entry.getKey()) - entry.getValue() < 0) {
                throw new SbcRuntimeException("k-030301", "系统库存校验失败请重新到提货页面选择数量");
            }
        }
        //校验库存有效性(去除乡镇件库存)--------------end

        // 2.按店铺包装多个订单信息、订单组信息
        List<Trade> trades = newPileTradeListPick(tradeCommitRequest, tradeItemGroups);

        computersNewPile(trades);

        log.info("newPilePickCommitAll-------------->"+JSONObject.toJSONString(trades));
        // 3.批量提交订单
        List<TradeCommitResult> batchNewPilePick = createBatchNewPilePick(trades, operator, getPileStock, pileTradeMap);
        return batchNewPilePick;
    }


    /**
     * 删除文档
     *
     * @param tid
     */
    @MongoRollback(persistence = NewPileTrade.class, idExpress = "tid", operation = Operation.UPDATE)
    public void deleteTrade(String tid) {
        newPileTradeRepository.deleteById(tid);
    }


    /**
     * 查询订单
     *
     * @param tid
     */
    public NewPileTrade detail(String tid) {
        return newPileTradeRepository.findById(tid).orElse(null);
    }

    /**
     * 退货 | 退款
     *
     * @param tid
     * @param operator
     */
    public void returnOrder(String tid, Operator operator) {
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(tid)
                .operator(operator)
                .event(TradeEvent.REFUND)
                .build();
        newPileTradeFSMService.changeState(stateRequest);
    }

    /**
     * 根据快照封装订单确认页信息
     *
     * @param g
     * @return
     */
    public TradeConfirmItem getPurchaseInfo(TradeItemGroup g) {
        TradeConfirmItem item = new TradeConfirmItem();
        TradePrice price = new TradePrice();
        item.setTradeItems(g.getTradeItems());
        item.setSupplier(g.getSupplier());
        log.info("==============1" + g.getTradeItems());
        //计算商品总价
        handlePrice(g.getTradeItems(), price);
        log.info("==============2" + g.getTradeItems());
        //验证并计算各营销活动的优惠金额,实付金额,赠品List
        List<TradeMarketingVO> tradeMarketings = wrapperMarketingForConfirm(g.getTradeItems(),
                g.getTradeMarketingList());
        List<Discounts> discountsList = new ArrayList<>();
        //每个订单的多个优惠信息(满折优惠了xx,满减优惠了yy)
        item.setDiscountsPrice(discountsList);
        List<TradeMarketingVO> tempList =
                tradeMarketings.stream().filter(i -> i.getMarketingType() != MarketingType.GIFT)
                        .collect(Collectors.toList());
        tempList.forEach(i -> {
            Discounts discounts = Discounts.builder()
                    .amount(i.getDiscountsAmount())
                    .subType(i.getSubType())
                    .type(i.getMarketingType())
                    .build();
            discountsList.add(discounts);
            //设置营销商品优惠后的均摊价 (用于计算运费)
            List<TradeItem> items = item.getTradeItems().stream().filter(t -> i.getSkuIds().contains(t.getSkuId()))
                    .collect(Collectors.toList());
            newPileTradeItemService.clacSplitPrice(items, i.getRealPayAmount());
        });

        //应付金额 = 商品总金额 - 优惠总金额
        if (!price.isSpecial()) {
            BigDecimal discountsPrice = tempList.stream().map(TradeMarketingVO::getDiscountsAmount).reduce(BigDecimal
                    .ZERO, BigDecimal::add);
            price.setTotalPrice(price.getTotalPrice().subtract(discountsPrice));
        }
        //订单应付金额、原始金额追加包装费
        if (price.getPackingPrice().compareTo(BigDecimal.ZERO) > 0) {
            price.setTotalPrice(price.getTotalPrice().add(price.getPackingPrice()));
            price.setOriginPrice(price.getTotalPrice().add(price.getPackingPrice()));
        }
        item.setTradePrice(price);
        return item;
    }

    /**
     * 订单分页
     *
     * @param whereCriteria 条件
     * @param request       参数
     * @return
     */
    public Page<NewPileTrade> page(Criteria whereCriteria, NewPileTradeQueryRequest request) {
        long totalSize = this.countNum(whereCriteria, request);
        log.info("whereCriteria:{},NewPileTradeQueryRequest:{},NewPileTradecountNum:{}", JSONObject.toJSONString(whereCriteria),
                JSONObject.toJSONString(request), totalSize);
        if (totalSize < 1) {
            return new PageImpl<>(new ArrayList<>(), request.getPageRequest(), totalSize);
        }
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        return new PageImpl<>(mongoTemplate.find(query.with(request.getPageRequest()), NewPileTrade.class), request
                .getPageable(), totalSize);
    }

    /**
     * 统计数量
     *
     * @param whereCriteria
     * @param request
     * @return
     */
    public long countNum(Criteria whereCriteria, NewPileTradeQueryRequest request) {
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        long totalSize = mongoTemplate.count(query, NewPileTrade.class);
        return totalSize;
    }

    /**
     * 查询囤货订单
     *
     * @param whereCriteria
     * @param request
     * @return
     */
    public List<NewPileTrade> newPileTradeList(Criteria whereCriteria, TradeQueryRequest request) {
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        return mongoTemplate.find(query.with(request.getPageRequest()), NewPileTrade.class);
    }

    /**
     * 用户查询囤货商品及数量
     *
     * @param request
     * @return
     */
    public List<GoodsPickStock> getNewPileGoodsNumByCustomer(NewPileGoodsNumByCustomerRequest request) {
        NewPileTradeQueryRequest tradeQueryRequest = new NewPileTradeQueryRequest();
        tradeQueryRequest.setPageNum(0);
        tradeQueryRequest.setPageSize(1000);
        tradeQueryRequest.setBuyerId(request.getCustomer().getCustomerId());
        tradeQueryRequest.setFlowStates(Arrays.asList(NewPileFlowState.PICK_PART, NewPileFlowState.PILE));
        Criteria criteria = tradeQueryRequest.getWhereCriteria();

        Page<NewPileTrade> page = page(criteria, tradeQueryRequest);
        List<GoodsPickStock> byNewPileTradeNos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            List<String> newPileOrderNo = page.getContent().stream().map(o -> o.getId()).collect(Collectors.toList());
            NewPileReturnQueryRequest returnQueryRequest = new NewPileReturnQueryRequest();
            returnQueryRequest.setCustomerIds(ArrayUtils.toArray(request.getCustomer().getCustomerId()));
            Page<NewPileReturnOrder> returnOrdersPage = newPileReturnOrderService.page(returnQueryRequest);
            List<NewPileReturnOrder> content = returnOrdersPage.getContent();

            List<String> strno = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(content)) {
                List<String> returnPileNo = content.stream().map(o -> o.getTid()).collect(Collectors.toList());
                newPileOrderNo.forEach(var -> {
                    if (!returnPileNo.contains(var)) {
                        strno.add(var);
                    }
                });
            } else {
                strno.addAll(newPileOrderNo);
            }
            log.info("byNewPileTradeNos-------->" + JSONObject.toJSONString(newPileOrderNo));
            log.info("byNewPileTradeNos-------->" + JSONObject.toJSONString(strno));
            log.info("byNewPileTradeNos-------->" + JSONObject.toJSONString(byNewPileTradeNos));
            if (CollectionUtils.isNotEmpty(strno)) {

                if (Objects.isNull(request.getWareId())) {
                    byNewPileTradeNos = goodsPickStockRepository.findByNewPileTradeNos(strno);
                } else {
                    byNewPileTradeNos = goodsPickStockRepository.findByNewPileTradeNosAndWareId(strno, request.getWareId());
                }
            }
        }
        return byNewPileTradeNos;
    }


    /**
     * 用户查询囤货订单仓库id
     *
     * @param request
     * @return
     */
    public BaseResponse<List<Long>> getNewPileTradeWareHoseCustomer(NewPileGoodsNumByCustomerRequest request) {
        NewPileTradeQueryRequest tradeQueryRequest = new NewPileTradeQueryRequest();
        tradeQueryRequest.setPageNum(0);
        tradeQueryRequest.setPageSize(1000);
        tradeQueryRequest.setBuyerId(request.getCustomer().getCustomerId());
        tradeQueryRequest.setFlowStates(Arrays.asList(NewPileFlowState.PICK_PART, NewPileFlowState.PILE));
        Criteria criteria = tradeQueryRequest.getWhereCriteria();
        Page<NewPileTrade> page = page(criteria, tradeQueryRequest);
        log.info("getNewPileTradeWareHoseCustomer------>"+JSONObject.toJSONString(page.getContent()));
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            List<Long> wareIds = page.getContent().stream().map(o -> o.getWareId()).collect(Collectors.toList());
            log.info("wareIds------>"+JSONObject.toJSONString(wareIds));
            return BaseResponse.success(wareIds);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新增线下收款单(包含线上线下的收款单)(包含支付回调)
     *
     * @param receivableAddRequest receivableAddRequest
     * @param platform             platform
     * @return 收款单
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    @LcnTransaction
    public void addReceivable(ReceivableAddRequest receivableAddRequest, Platform platform, Operator operator) {
        PayOrder payOrder = payOrderRepository.findById(receivableAddRequest.getPayOrderId()).orElse(null);
        if (Objects.isNull(payOrder) || DeleteFlag.YES.equals(payOrder.getDelFlag())) {
            throw new SbcRuntimeException("K-070001");
        }
        if (!CollectionUtils.isEmpty(receivableRepository.findByDelFlagAndPayOrderId(DeleteFlag.NO, payOrder
                .getPayOrderId()))) {
            throw new SbcRuntimeException("K-070005");
        }

        /**1.创建收款单*/
        Receivable receivable = new Receivable();
        BeanUtils.copyProperties(receivableAddRequest, receivable);
        receivable.setOfflineAccountId(receivableAddRequest.getAccountId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (receivableAddRequest.getCreateTime().length() == 10) {
            receivable.setCreateTime(LocalDateTime.of(LocalDate.parse(receivableAddRequest.getCreateTime(),
                    formatter), LocalTime.MIN));
        } else {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            receivable.setCreateTime(LocalDateTime.parse(receivableAddRequest.getCreateTime(), df));
        }
        receivable.setDelFlag(DeleteFlag.NO);
        receivable.setReceivableNo(generatorService.generateSid());
        receivable.setPayChannel(receivableAddRequest.getPayChannel());
        receivable.setPayChannelId(receivableAddRequest.getPayChannelId());

        //这里往缓存里面写
        payOrder.setReceivable(receivableRepository.saveAndFlush(receivable));
        /**2.更改支付单状态*/
        PayOrderStatus status;
        if (osUtil.isS2b()) {
            status = platform == Platform.PLATFORM ? PayOrderStatus.PAYED : PayOrderStatus.TOCONFIRM;
        } else {
            status = platform == Platform.BOSS ? PayOrderStatus.PAYED : PayOrderStatus.TOCONFIRM;
        }
        if (PayType.ONLINE.equals(payOrder.getPayType())) {
            status = PayOrderStatus.PAYED;
        }
        payOrder.setPayOrderStatus(status);
        payOrderService.updatePayOrder(receivableAddRequest.getPayOrderId(), status);

        NewPileTrade trade = detail(payOrder.getOrderCode());
        //判断是否是先货后款
        boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED)
                && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        String verifyCode = "";
        if (!goodsFirst && PayOrderStatus.PAYED.equals(status)) {
//            //生成自提码并推送
//            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
//                trade.getTradeState().setFlowState(FlowState.TOPICKUP);
//                verifyCode = RandomStringUtils.randomNumeric(6);
//                trade.getTradeWareHouse().setPickUpCode(verifyCode);
//                pickUpRecordService.add(PickUpRecord.builder().storeId(trade.getSupplier().getStoreId())
//                        .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
//                        .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build());
//                //刷新订单
//                updateTrade(trade);
//            }
        }
        Optional.of(payOrder).ifPresent(p ->
                this.payCallBack(p.getOrderCode(), p.getPayOrderPrice(), operator, PayWay.CASH));
        //推送短信
        if (!goodsFirst && PayOrderStatus.PAYED.equals(status)) {
            //自提订单
            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                sendPickUpMessageNewPile(trade);
            }
        }
    }


    /**
     * 订单支付回调
     *
     * @param tid
     * @param payOrderPrice
     * @param operator
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void payCallBack(String tid, BigDecimal payOrderPrice, Operator operator, PayWay payWay) {
        NewPileTrade trade = detail(tid);
        TradePrice tradePrice = trade.getTradePrice();
        //todo 多商家支付完成后不检验应付金额和实付金额
//        if (payOrderPrice.compareTo(tradePrice.getTotalPrice()) != 0) {
//            throw new SbcRuntimeException("K-050101", new Object[]{tid, tradePrice.getTotalPrice(), payOrderPrice});
//        }

        trade.getTradePrice().setTotalPayCash(payOrderPrice);
        String eventStr = trade.getTradeState().getPayState() == PayState.UNCONFIRMED ? "确认支付" : "支付";
        if (osUtil.isS2b()) {
            trade.getTradeState().setPayState(operator.getPlatform() == Platform.PLATFORM ? PayState.PAID : PayState
                    .UNCONFIRMED);
        } else {
            trade.getTradeState().setPayState(operator.getPlatform() == Platform.BOSS ? PayState.PAID : PayState
                    .UNCONFIRMED);
        }

        if (PayType.fromValue(Integer.parseInt(trade.getPayInfo().getPayTypeId())) == PayType.ONLINE) {
            trade.getTradeState().setPayState(PayState.PAID);
            trade.getTradeState().setFlowState(NewPileFlowState.PILE);
            trade.setPayWay(payWay);
            operator.setPlatform(Platform.CUSTOMER);
            operator.setName(trade.getBuyer().getName());
            operator.setAccount(trade.getBuyer().getAccount());
            operator.setUserId(trade.getBuyer().getId());

        }

        log.info("=============邀新统计开始，参数：" + trade.getTradeState().getPayState().getDescription());
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            //记录邀新用户下单统计
            String employeeId = trade.getBuyer().getEmployeeId();
            log.info("=============邀新统计，参数：" + trade.getBuyer().getEmployeeId());
            //TODO:提货支付回调不统计
            if (StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId()) && TradeActivityTypeEnum.NEWPILETRADE.toActivityType().equals(trade.getActivityType())) {
                BigDecimal totalPrice = trade.getTradePrice().getTotalPrice();
                Long goodsTotalNum = trade.getGoodsTotalNum();
                invitationStatisticsProvider.tradeStatistics(InvitationTradeStatisticsRequest.builder().employeeId(employeeId).orderId(tid).goodsCount(goodsTotalNum).tradePrice(totalPrice).build());
            }
            PayOrderResponse payOrder = payOrderService.findPayOrderById(trade.getPayOrderId());
            //线下付款订单付款时间取实际扭转为已付款的时间
            trade.getTradeState().setPayTime(LocalDateTime.now());
            // 查询交易流水号
            BaseResponse<PayTradeRecordResponse> payTradeRecord;
            if (trade.getPayInfo().isMergePay()) {
                payTradeRecord =
                        payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(trade.getParentId()));
            } else {
                payTradeRecord =
                        payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(trade.getId()));
            }
            String tradeNo = Objects.isNull(payTradeRecord.getContext()) ? null :
                    payTradeRecord.getContext().getTradeNo();
            //已支付，添加对账记录
            AccountRecordAddRequest record = AccountRecordAddRequest.builder()
                    .amount(payOrderPrice)
                    .customerId(trade.getBuyer().getId())
                    .customerName(trade.getBuyer().getName())
                    .orderCode(trade.getId())
                    .tradeNo(tradeNo)
                    .orderTime(trade.getTradeState().getCreateTime())
                    .payWay(payWay)
                    .storeId(trade.getSupplier().getStoreId())
                    .supplierId(trade.getSupplier().getSupplierId())
                    .tradeTime(payOrder.getReceiveTime())
                    .type((byte) 0)
                    .build();
            accountRecordProvider.add(record);

            //已支付或者，添加订单开票
            //删除存在的开票信息
            orderInvoiceService.deleteOrderInvoiceByOrderNo(trade.getId());
            //订单开票
            this.createOrderInvoiceNewPile(trade, operator);
            //推金蝶和wms都放在一个延时队列中3分钟后推送
            //todo 推第三方
            if (wmsAPIFlag) {
                if (Objects.isNull(trade.getGrouponFlag()) || !trade.getGrouponFlag()) {
                    if (PaymentOrder.PAY_FIRST.equals(trade.getPaymentOrder())) {
                        logger.info("NewPileTradeService.payCallBackOnline mq push pay order id:{} kingdeeOpenState", trade.getId(), kingdeeOpenState);
                        if (kingdeeOpenState) {
                            //满时件不推金蝶,囤货收款单不推送wms
                            //推新金蝶
                            TradePushPayOrderGroupon pushPayOrderGroupon = new TradePushPayOrderGroupon();
                            pushPayOrderGroupon.setPayCode(payOrder.getPayOrderId());
                            pushPayOrderGroupon.setOrderCode(trade.getId());
                            orderProducerService.delayPushingPaymentOrders(pushPayOrderGroupon);
                        }
                    }
                }
            }
            if (wmsAPIFlag && Objects.equals(PayWay.CCB, payWay)) {
                String tradeId = trade.getId();
                log.info("非自营商家，佣金部分推送ERP收款单：{}", tradeId);
                orderProducerService.pushCommisionToKingdee(tradeId, 2 * 60 * 1000L);
            }
            //插入支付单
            this.savePayOrder(KsBeanUtil.convert(trade, Trade.class), payOrder, payWay);
        }

        if (trade.getTradeState().getFlowState() != NewPileFlowState.PILE && trade.getTradeState().getPayState() ==
                PayState.PAID) {
            // 订单支付后，发送MQ消息
            this.sendMQForOrderPayed(KsBeanUtil.convert(trade, Trade.class));

            //添加操作日志
            String detail = String.format("订单[%s]已%s,操作人：%s", trade.getId(), eventStr, operator.getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventType(TradeEvent.PAY.getDescription())
                    .eventTime(LocalDateTime.now())
                    .eventDetail(detail)
                    .build());
            updateTrade(trade);
            this.operationLogMq.convertAndSend(operator, TradeEvent.PAY.getDescription(), detail);
        } else {
            //添加操作日志
            String detail = String.format("订单[%s]已%s,操作人：%s", trade.getId(), eventStr, operator.getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventType(TradeEvent.PAY.getDescription())
                    .eventTime(LocalDateTime.now())
                    .eventDetail(detail)
                    .build());
            updateTrade(trade);
            this.operationLogMq.convertAndSend(operator, TradeEvent.PAY.getDescription(), detail);
        }

        if (trade.getTradeState().getPayState().equals(PayState.PAID)) {
            //购买指定商品赠券
            this.buyGoodsSendCoupon(trade.getId());
            log.info("==================热销排行MQ===================={}", trade.getId());
            //热销排行 埋点
            this.hotSaleArea(trade.getId());
        }
    }

    public void createOrderInvoiceNewPile(NewPileTrade trade, Operator operator) {
        OrderInvoiceSaveRequest request = buildOrderInvoiceSaveRequest(trade);
        if (request == null) {
            return;
        }
        Optional<OrderInvoice> optional = orderInvoiceService.generateOrderInvoice(request, operator.getUserId(),
                InvoiceState.WAIT);
        optional.ifPresent(invoice -> trade.getInvoice().setOrderInvoiceId(invoice.getOrderInvoiceId()));
    }


    /**
     * 发送订单支付、订单完成MQ消息
     *
     * @param trade
     */
    private void sendMQForOrderPayedAndComplete(NewPileTrade trade) {
        TradeVO tradeVO = KsBeanUtil.convert(trade, TradeVO.class);
        orderProducerService.sendMQForOrderPayedAndComplete(tradeVO);
    }

    /**
     * 根据父订单号查询订单
     *
     * @param parentTid
     */
    public List<NewPileTrade> detailsByParentId(String parentTid) {
        return newPileTradeRepository.findListByParentId(parentTid);
    }

    /**
     * 根据囤货订单id查询提货详情
     *
     * @param tid
     */
    public void pickTradeDetailsByTid(String tid) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("tradeItems.newPileOrderNo").is(tid)
        );
        List<Trade> tradeList = mongoTemplate.find(
                new Query(criteria)
                , Trade.class);
        tradeList.forEach(trade -> {
            PickDetailDTO pickDetailDTO = new PickDetailDTO();
            pickDetailDTO.setId(trade.getId());
            BigDecimal payPrice = BigDecimal.valueOf(0.00);
            AtomicReference<Integer> num = new AtomicReference<>(0);
            /*(trade.getPickGoodsList().stream().filter(pickGoodsDTO -> tid.equals(pickGoodsDTO.getNewPileOrderNo())).collect(Collectors.toList())).forEach(pickGoodsDTO -> {
                pickGoodsDTO.getPickGoodsItem().forEach(pickGoodsItemDTO -> {
                    num.updateAndGet(v -> v + pickGoodsItemDTO.getNum());
                });
            });*/
            pickDetailDTO.setGoodsNum(num.get());
        });
    }

    /**
     * 订单超时未支付，系统自动取消订单
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void autoCancelOrder(TradeCancelRequest request) {
        request.setCancelType(OrderCancelType.autoCancel);
        cancel(request);
    }

    private NewPileTrade checkTradeForCancel(String tid) {
        NewPileTrade trade = detail(tid);
        if (Objects.isNull(trade)) {
            log.warn("囤货订单不存在:{}", tid);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货订单不存在！");
        }
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            throw new SbcRuntimeException("K-050202");
        }
        if (trade.getTradeState().getDeliverStatus() != DeliverStatus.NOT_YET_SHIPPED) {
            throw new SbcRuntimeException("K-050203");
        }
        if (trade.getTradeState().getFlowState() == NewPileFlowState.VOID) {
            throw new SbcRuntimeException("K-050203");
        }
        return trade;
    }

    /**
     * 退优惠券
     *
     * @param tradeId  囤货单号
     * @param returnId 退单号
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void returnCoupon(String tradeId, String returnId) {
        NewPileTrade trade = this.detail(tradeId);

        if (trade.getTradePrice().getCouponPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (trade.getTradeState().getFlowState() == NewPileFlowState.VOID) {
            doReturnCoupon(trade);
            return;
        }

        List<InventoryDetailSamount> amountList = inventoryDetailSamountService.getInventoryByPileId(tradeId);
        //所有分摊金额全部已退 或
        // 当前退单标记为已完成 且 未退部分由当前退单占用
        List<InventoryDetailSamount> notReturnedList = amountList.stream()
                .filter(item -> !Objects.equals(item.getReturnFlag(), 1))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notReturnedList) || allTakenByCurrentReturnId(notReturnedList, returnId)) {
            doReturnCoupon(trade);
        }
    }

    private void doReturnCoupon(NewPileTrade trade) {
        log.info("处理退优惠券：囤货单id={}", trade.getId());
        //处理需要退优惠券的囤货单
        String customerId = trade.getBuyer().getId();
        trade.getTradeItems().stream()
                .flatMap(tradeItemVO -> tradeItemVO.getCouponSettlements().stream())
                .forEach(coupon -> {
                    CouponCodeReturnByIdRequest request = new CouponCodeReturnByIdRequest();
                    request.setCouponCodeId(coupon.getCouponCodeId());
                    request.setCustomerId(customerId);
                    couponCodeProvider.returnById(request);
                });
        log.info("处理退优惠券结束：囤货单id={}", trade.getId());
    }

    private boolean allTakenByCurrentReturnId(List<InventoryDetailSamount> notReturnedList, String returnId) {
        Assert.notEmpty(notReturnedList, "未退的分摊金额列表不能为空");
        Assert.hasText(returnId, "退单号不能为空");

        for (InventoryDetailSamount item : notReturnedList) {
            if (!Objects.equals(item.getReturnId(), returnId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 退优惠券
     *
     * @param tradeId 订单id
     */
    public void returnCouponBackup(String tradeId) {
        // 获取当前的======订单
        NewPileTrade trade = this.detail(tradeId);


        trade.getTradeItems().forEach(v -> {
            if (Objects.nonNull(v.getDevanningId())) {
                v.setBNum(v.getDivisorFlag().multiply(BigDecimal.valueOf(v.getNum())));
            } else {
                v.setBNum(BigDecimal.valueOf(v.getNum()));
            }
        });
        Map<String, BigDecimal> collect = trade.getTradeItems().stream().collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getBNum, BigDecimal::add)));
        trade.getTradeItems().forEach(v -> {
            v.setBNum(collect.get(v.getSkuId()));
        });

        // 获取订单中购买的商品数量
        Map<String, TradeItem> boughtSkuNum = trade.getTradeItems().stream()
                .collect(Collectors.toMap(TradeItem::getSkuId, Function.identity(), (last, next) -> next));

        // 累加所有已退商品的数量
        Map<String, BigDecimal> returnSkuNum = new HashMap<>();
        // 商家驳回订单
        if (trade.getTradeState().getAuditState() == AuditState.REJECTED) {
            setReturnNum(returnSkuNum, boughtSkuNum);
        } else if (trade.getTradeState().getFlowState() == NewPileFlowState.VOID
                && trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED
                && trade.getTradeState().getPayState() == PayState.NOT_PAID) {
            // 用户取消订单
            setReturnNum(returnSkuNum, boughtSkuNum);
        }
        // 获取所有已退的===退单
        List<NewPileReturnOrder> returnOrders = newPileReturnOrderRepository.findByTid(trade.getId()).stream()
                .filter(item -> item.getReturnFlowState() == NewPileReturnFlowState.COMPLETED)
                .collect(Collectors.toList());
        // 获取已退商品数量集合
        returnOrders.forEach(r -> {
            r.getReturnItems().forEach(returnItem -> {
                BigDecimal returnNum = returnSkuNum.get(returnItem.getSkuId());
                if (Objects.isNull(returnNum)) {
                    returnSkuNum.put(returnItem.getSkuId(), returnItem.getNum());
                } else {
                    returnSkuNum.put(returnItem.getSkuId(), returnNum.add(returnItem.getNum()));
                }
            });
        });
        // 获取订单组信息
        TradeGroup tradeGroup = StringUtils.isNotEmpty(trade.getGroupId()) ?
                tradeGroupRepository.findById(trade.getGroupId()).orElse(null) : null;
        List<String> storeIds = new ArrayList<>();
        // 循环进行记录或者退券
        returnSkuNum.forEach((key, value) -> {
            // 退款中的该商品使用了优惠券 并且 退款商品的数量和订单中商品购买数量一致.
            if (Objects.nonNull(boughtSkuNum.get(key)) &&
                    !CollectionUtils.isEmpty(boughtSkuNum.get(key).getCouponSettlements()) &&
                    value.longValue() == boughtSkuNum.get(key).getNum()) {
                // 订单组中订单使用了平台优惠券(全场赠券)
                if (boughtSkuNum.get(key).getCouponSettlements().stream()
                        .filter(f -> f.getCouponType() == CouponType.GENERAL_VOUCHERS).findFirst().isPresent() &&
                        Objects.nonNull(tradeGroup) && Objects.nonNull(tradeGroup.getCommonCoupon())) {
                    // 退货的商品使用了全场赠券
                    if (tradeGroup.getCommonCoupon().getGoodsInfoIds().contains(key)) {
                        // 此时认为该商品已经完全退货, 需要在"订单组"中做记录.
                        List<String> ids = tradeGroup.getCommonSkuIds();
                        if (!ids.contains(key)) {
                            ids.add(key);
                            tradeGroup.setCommonSkuIds(ids);
                            tradeGroupRepository.save(tradeGroup);
                        }
                        List<String> skuIds = tradeGroup.getCommonCoupon().getGoodsInfoIds();
                        // 如果全场赠券中商品集合为空, 则不执行后续操作
                        if (CollectionUtils.isEmpty(skuIds)) {
                            return;
                        }
                        // 如果已退商品集合和参加全场赠券的商品集合完全一致
                        if (CollectionUtils.isEqualCollection(skuIds, ids)) {
                            // 设置平台券完全已退
                            tradeGroup.setCommonCouponIsReturn(Boolean.TRUE);
                            tradeGroupRepository.save(tradeGroup);
                            // 退券(全场赠券)
                            couponCodeProvider.returnById(CouponCodeReturnByIdRequest.builder()
                                    .couponCodeId(tradeGroup.getCommonCoupon().getCouponCodeId())
                                    .customerId(trade.getBuyer().getId())
                                    .build());
                        }
                    }
                }
                // 该订单存在使用店铺优惠券
                if (boughtSkuNum.get(key).getCouponSettlements().stream()
                        .filter(f -> f.getCouponType() == CouponType.STORE_VOUCHERS).findFirst().isPresent() &&
                        Objects.nonNull(trade.getTradeCoupon())) {
                    storeIds.add(key);
                    if (CollectionUtils.isEqualCollection(
                            trade.getTradeCoupon().getGoodsInfoIds(), storeIds)) {
                        // 退券(店铺券)
                        couponCodeProvider.returnById(CouponCodeReturnByIdRequest.builder()
                                .couponCodeId(trade.getTradeCoupon().getCouponCodeId())
                                .customerId(trade.getBuyer().getId())
                                .build());
                    }
                }
            }
        });
    }

    /**
     * 退款囤货订单
     *
     * @param tid
     * @param operator
     */
    public void fireRefundTradeEvent(String tid, Operator operator) {
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(tid)
                .operator(operator)
                .event(TradeEvent.REFUND)
                .data("已全部退货或退款")
                .build();
        pileTradeFSMService.changeState(stateRequest);

        NewPileTrade trade = detail(tid);
        // 判断是否是退款订单，并且有分销员id和分销商品
        if (Objects.nonNull(trade.getRefundFlag()) && trade.getRefundFlag()
                && trade.getTradeState().getPayState() == PayState.PAID
                && StringUtils.isNotBlank(trade.getDistributorId())
                && CollectionUtils.isNotEmpty(trade.getDistributeItems())) {
            // trade对象转tradeVO对象
            TradeVO tradeVO = KsBeanUtil.convert(trade, TradeVO.class);
            // 订单作废后，发送MQ消息
            orderProducerService.sendMQForOrderRefundVoid(tradeVO);
        }
    }


    /**
     * 减去营销商品限购数量信息
     *
     * @param tradeItems
     * @param isAddFlag
     */
    public void calMarketGoodsNum(List<TradeItem> tradeItems, Boolean isAddFlag) {
        //遍历订单商品信息
        /*tradeItems.forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getMarketingIds())) {
                log.info("create trade ... reduce or add marketing goods nums:::item.getMarketingIds()={}", item.getMarketingIds());
                //遍历商品使用到的营销信息
                item.getMarketingIds().forEach(marketId -> {
                    MarketingScopeByMarketingIdRequest request = new MarketingScopeByMarketingIdRequest();
                    request.setMarketingId(marketId);
                    request.setSkuId(item.getSkuId());
                    //通过营销ID和商品ID查询 营销关联商品scope信息
                    MarketingScopeByMarketingIdResponse response = marketingScopeQueryProvider.listByMarketingIdAndSkuId(request).getContext();
                    //如果营销关联商品信息不为空
                    if (CollectionUtils.isNotEmpty(response.getMarketingScopeVOList())) {
                        //遍历营销关联商品信息
                        response.getMarketingScopeVOList().forEach(marketingScopeVO -> {
                            //如果营销关联商品信息 设置了限购数量
                            if (Objects.nonNull(marketingScopeVO.getPurchaseNum()) && marketingScopeVO.getPurchaseNum() >= 0) {
                                log.info("计算营销商品限制数量:::isAddFlag={},marketingScopeVO = {}", isAddFlag, marketingScopeVO);
                                //取消订单
                                if (isAddFlag) {
                                    //营销商品表
                                    marketingScopeVO.setPurchaseNum(marketingScopeVO.getPurchaseNum() + item.getNum());
                                } else {//创建订单
                                    //限购数量大于购买数量
                                    if (marketingScopeVO.getPurchaseNum() > item.getNum()) {
                                        //赋值限购数量=限购数量-购买数量
                                        marketingScopeVO.setPurchaseNum(marketingScopeVO.getPurchaseNum() - item.getNum());
                                    } else {
                                        //限购数量小于或等于购买数量，允许用户购买并设置限购数量为0
                                        marketingScopeVO.setPurchaseNum(0L);
                                    }
                                }
                                marketingScopeProvider.saveMarketingScope(marketingScopeVO);
                                //查询商品信息
                                GoodsInfoVO goodsInfoVO = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(marketingScopeVO.getScopeId()).build()).getContext();
                                if (Objects.nonNull(goodsInfoVO.getPurchaseNum()) && goodsInfoVO.getPurchaseNum() >= 0) {
                                    //设置商品表限购数量
                                    goodsInfoVO.setPurchaseNum(marketingScopeVO.getPurchaseNum());
                                    //同步商品表营销限购数量
                                    GoodsInfoModifyRequest goodsInfoModifyRequest = new GoodsInfoModifyRequest();
                                    goodsInfoModifyRequest.setGoodsInfo(KsBeanUtil.convert(goodsInfoVO, GoodsInfoDTO.class));
                                    goodsInfoProvider.modify(goodsInfoModifyRequest);
                                }
                            }
                        });
                    }
                });
            }
        });*/
    }


    /**
     * 功能描述: 验证物流公司信息
     * 〈〉
     *
     * @Param: [storeCommitInfoDTOList]
     * @Return: void
     * @Author: yxb
     * @Date: 2020/11/9 14:36
     */
    public void checkLogsitcCompanyInfo(List<StoreCommitInfoDTO> storeCommitInfoDTOList) {
        Set<Long> ids = new HashSet<>();
        for (StoreCommitInfoDTO storeCommitInfoDTO : storeCommitInfoDTOList) {
            LogisticsInfoDTO logisticsInfo = storeCommitInfoDTO.getLogisticsInfo();
            if (DeliverWay.LOGISTICS.equals(storeCommitInfoDTO.getDeliverWay())) {
                if (Objects.nonNull(logisticsInfo) && StringUtils.isNotBlank(logisticsInfo.getId())) {
                    ids.add(Long.valueOf(logisticsInfo.getId()));
                }
            }
        }
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        LogisticsCompanyListResponse context = logisticsCompanyQueryProvider
                .list(LogisticsCompanyListRequest.builder().idList(new ArrayList<>(ids)).build()).getContext();
        if (Objects.isNull(context)) {
            throw new SbcRuntimeException("K-170001");
        }
        for (StoreCommitInfoDTO storeCommitInfoDTO : storeCommitInfoDTOList) {
            LogisticsInfoDTO logisticsInfo = storeCommitInfoDTO.getLogisticsInfo();
            if (DeliverWay.LOGISTICS.equals(storeCommitInfoDTO.getDeliverWay())) {
                Optional<LogisticsCompanyVO> first = context.getLogisticsCompanyVOList().stream().
                        filter(info -> String.valueOf(info.getId()).equals(logisticsInfo.getId())).findFirst();
                first.ifPresent(companyInfo -> {
                    logisticsInfo.setCompanyNumber(companyInfo.getCompanyNumber());
                    logisticsInfo.setLogisticsAddress(companyInfo.getLogisticsAddress());
                    logisticsInfo.setLogisticsCompanyName(companyInfo.getLogisticsName());
                    logisticsInfo.setLogisticsCompanyPhone(companyInfo.getLogisticsPhone());
                });
            }
        }
    }


    /**
     * 将用户下单信息 根据不同店铺 包装成 多个订单 [前端客户下单] 拆箱
     * 1.校验营销活动
     * 2.校验商品是否可以下单
     * 3.填充订单商品,订单赠品,订单营销信息...
     *
     * @return
     */
    public List<NewPileTrade> newPileTradeList(TradeCommitRequest tradeCommitRequest, List<TradeItemGroup> tradeItemGroups) {
        CustomerVO customer = tradeCommitRequest.getCustomer();
        List<NewPileTrade> trades = new ArrayList<>();

        // 1.查询快照中的购物清单
        // list转map,方便获取 店铺id为key
        Map<Long, TradeItemGroup> tradeItemGroupsMap = tradeItemGroups.stream().collect(
                Collectors.toMap(g -> g.getSupplier().getStoreId(), Function.identity()));

        List<StoreVO> storeVOList = tradeCacheService.queryStoreList(new ArrayList<>(tradeItemGroupsMap.keySet()));

        Map<Long, StoreVO> storeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeVOList)) {
            storeMap.putAll(storeVOList.stream().collect(Collectors.toMap(StoreVO::getStoreId, s -> s)));
        }

        CustomerLevelMapByCustomerIdAndStoreIdsRequest request = new CustomerLevelMapByCustomerIdAndStoreIdsRequest();
        request.setCustomerId(customer.getCustomerId());
        request.setStoreIds(new ArrayList<>(tradeItemGroupsMap.keySet()));
        Map<Long, CommonLevelVO> storeLevelMap = customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds
                (request)
                .getContext().getCommonLevelVOMap();

        // 2.遍历各个店铺下单信息
        NewPileTradeItemSnapshot tradeItemSnapshot = newPileTradeItemService.findTradeItemSnapshot(customer.getCustomerId());
        tradeCommitRequest.getStoreCommitInfoList().forEach(
                i -> {
                    TradeItemGroup group = tradeItemGroupsMap.get(i.getStoreId());
                    if (storeMap.get(group.getSupplier().getStoreId()) == null) {
                        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                    }
                    group.getSupplier().setFreightTemplateType(
                            storeMap.get(group.getSupplier().getStoreId()).getFreightTemplateType());

                    // 2.2.【公共方法】下单信息验证, 将信息包装成订单
                    trades.add(this.validateAndWrapperNewPileTrade(new NewPileTrade(),
                            TradeParams.builder()
                                    .backendFlag(false) //表示前端操作
                                    .commitFlag(true) //表示下单
                                    .marketingList(group.getTradeMarketingList())
                                    .couponCodeId(i.getCouponCodeId())
                                    .tradePrice(new TradePrice())
                                    .tradeItems(group.getTradeItems())
                                    .oldGifts(Collections.emptyList())//下单,非修改订单
                                    .oldTradeItems(Collections.emptyList())//下单,非修改订单
                                    .storeLevel(storeLevelMap.get(group.getSupplier().getStoreId()))
                                    .customer(customer)
                                    .supplier(group.getSupplier())
                                    .seller(null) //客户下单
                                    .consigneeId(tradeCommitRequest.getConsigneeId())
                                    .detailAddress(tradeCommitRequest.getConsigneeAddress())
                                    .consigneeUpdateTime(tradeCommitRequest.getConsigneeUpdateTime())
                                    .consignee(null) //客户下单,不可填写临时收货地址
                                    .invoice(null)
                                    .invoiceConsignee(null) //客户下单,不可填写发票临时收货地址
                                    .deliverWay(i.getDeliverWay())
                                    .payType(i.getPayType())
                                    .buyerRemark(i.getBuyerRemark())
                                    .sellerRemark(null) //客户下单,无卖家备注
                                    .encloses(i.getEncloses())
                                    .ip(tradeCommitRequest.getOperator().getIp())
                                    .platform(Platform.CUSTOMER)
                                    .forceCommit(tradeCommitRequest.isForceCommit())
                                    .orderSource(tradeCommitRequest.getOrderSource())
                                    .distributeChannel(tradeCommitRequest.getDistributeChannel())
                                    .storeBagsFlag(group.getStoreBagsFlag())
                                    .shopName(tradeCommitRequest.getShopName())
                                    .isDistributor(tradeCommitRequest.getIsDistributor())
                                    .storeOpenFlag(i.getStoreOpenFlag())
                                    .openFlag(tradeCommitRequest.getOpenFlag())
                                    .grouponForm(group.getGrouponForm())
                                    .shareUserId(customer.getCustomerId().equals(tradeCommitRequest.getShareUserId())
                                            ? null : tradeCommitRequest.getShareUserId())
                                    .isFlashSaleGoods(false)
                                    .wareHouseCode(i.getWareHouseCode())
                                    .wareId(i.getWareId())
                                    .wareName(tradeCommitRequest.getWareName())
                                    .tradeWareHouse(wareHouseTurnToTrade(i.getWareHouseVO()))
                                    .bookingDate(i.getBookingDate())
                                    .logisticsInfo(toLogisticsInfo(i.getLogisticsInfo()))
                                    .group(group)
                                    .suitBuyCount(tradeItemSnapshot.getSuitBuyCount())
                                    .marketingId(tradeItemSnapshot.getMarketingId())
                                    .sourceChannel(tradeCommitRequest.getSourceChannel())
                                    .saleType(group.getSaleType())
                                    .build()));
                }
        );
        return trades;
    }


    /**
     * 将用户下单信息 根据不同店铺 包装成 多个订单 [前端客户下单] 拆箱
     * 1.校验营销活动
     * 2.校验商品是否可以下单
     * 3.填充订单商品,订单赠品,订单营销信息...
     *
     * @return
     */
    public List<Trade> newPileTradeListPick(TradeCommitRequest tradeCommitRequest, List<TradeItemGroup> tradeItemGroups) {
        CustomerVO customer = tradeCommitRequest.getCustomer();
        List<Trade> trades = new ArrayList<>();
        // 1.查询快照中的购物清单
        // list转map,方便获取 店铺id为key
        Map<Long, TradeItemGroup> tradeItemGroupsMap = tradeItemGroups.stream().collect(
                Collectors.toMap(g -> g.getSupplier().getStoreId(), Function.identity()));

        List<StoreVO> storeVOList = tradeCacheService.queryStoreList(new ArrayList<>(tradeItemGroupsMap.keySet()));

        Map<Long, StoreVO> storeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeVOList)) {
            storeMap.putAll(storeVOList.stream().collect(Collectors.toMap(StoreVO::getStoreId, s -> s)));
        }

        CustomerLevelMapByCustomerIdAndStoreIdsRequest request = new CustomerLevelMapByCustomerIdAndStoreIdsRequest();
        request.setCustomerId(customer.getCustomerId());
        request.setStoreIds(new ArrayList<>(tradeItemGroupsMap.keySet()));
        Map<Long, CommonLevelVO> storeLevelMap = customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds
                (request)
                .getContext().getCommonLevelVOMap();

        // 2.遍历各个店铺下单信息

        NewPilePickTradeItemSnapshot tradeItemSnapshot = newPilePickTradeItemService.findTradeItemSnapshot(customer.getCustomerId());
        tradeCommitRequest.getStoreCommitInfoList().forEach(
                i -> {
                    TradeItemGroup group = tradeItemGroupsMap.get(i.getStoreId());
                    if (storeMap.get(group.getSupplier().getStoreId()) == null) {
                        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                    }
                    group.getSupplier().setFreightTemplateType(
                            storeMap.get(group.getSupplier().getStoreId()).getFreightTemplateType());

                    // 2.2.【公共方法】下单信息验证, 将信息包装成订单
                    trades.add(this.validateAndWrapperNewPileTradePick(new Trade(),
                            TradeParams.builder()
                                    .backendFlag(false) //表示前端操作
                                    .commitFlag(true) //表示下单
                                    .marketingList(group.getTradeMarketingList())
                                    .couponCodeId(i.getCouponCodeId())
                                    .tradePrice(new TradePrice())
                                    .tradeItems(group.getTradeItems())
                                    .oldGifts(Collections.emptyList())//下单,非修改订单
                                    .oldTradeItems(Collections.emptyList())//下单,非修改订单
                                    .storeLevel(storeLevelMap.get(group.getSupplier().getStoreId()))
                                    .customer(customer)
                                    .supplier(group.getSupplier())
                                    .seller(null) //客户下单
                                    .consigneeId(tradeCommitRequest.getConsigneeId())
                                    .detailAddress(tradeCommitRequest.getConsigneeAddress())
                                    .consigneeUpdateTime(tradeCommitRequest.getConsigneeUpdateTime())
                                    .consignee(null) //客户下单,不可填写临时收货地址
                                    .invoice(null)
                                    .invoiceConsignee(null) //客户下单,不可填写发票临时收货地址
                                    .deliverWay(i.getDeliverWay())
                                    .payType(i.getPayType())
                                    .buyerRemark(i.getBuyerRemark())
                                    .sellerRemark(null) //客户下单,无卖家备注
                                    .encloses(i.getEncloses())
                                    .ip(tradeCommitRequest.getOperator().getIp())
                                    .platform(Platform.CUSTOMER)
                                    .forceCommit(tradeCommitRequest.isForceCommit())
                                    .orderSource(tradeCommitRequest.getOrderSource())
                                    .distributeChannel(tradeCommitRequest.getDistributeChannel())
                                    .storeBagsFlag(group.getStoreBagsFlag())
                                    .shopName(tradeCommitRequest.getShopName())
                                    .isDistributor(tradeCommitRequest.getIsDistributor())
                                    .storeOpenFlag(i.getStoreOpenFlag())
                                    .openFlag(tradeCommitRequest.getOpenFlag())
                                    .grouponForm(group.getGrouponForm())
                                    .shareUserId(customer.getCustomerId().equals(tradeCommitRequest.getShareUserId())
                                            ? null : tradeCommitRequest.getShareUserId())
                                    .isFlashSaleGoods(false)
                                    .wareHouseCode(i.getWareHouseCode())
                                    .wareId(i.getWareId())
                                    .wareName(tradeCommitRequest.getWareName())
                                    .tradeWareHouse(wareHouseTurnToTrade(i.getWareHouseVO()))
                                    .bookingDate(i.getBookingDate())
                                    .logisticsInfo(toLogisticsInfo(i.getLogisticsInfo()))
                                    .group(group)
                                    .suitBuyCount(tradeItemSnapshot.getSuitBuyCount())
                                    .marketingId(null)
                                    .sourceChannel(tradeCommitRequest.getSourceChannel())
                                    .saleType(group.getSaleType())
                                    .build()));
                }
        );
        return trades;
    }


    /**
     * 功能描述: WareHouseVO转TradeWareHouse
     * 〈〉
     */
    public TradeWareHouse wareHouseTurnToTrade(WareHouseVO wareHouseVO) {
        if (null != wareHouseVO) {
            return KsBeanUtil.convert(wareHouseVO, TradeWareHouse.class);
        }
        return null;
    }

    public LogisticsInfo toLogisticsInfo(LogisticsInfoDTO logisticsInfoDTO) {
        if (null != logisticsInfoDTO) {
            return KsBeanUtil.convert(logisticsInfoDTO, LogisticsInfo.class);
        }
        return null;
    }


    /**
     * 验证下单信息并封装订单信息 拆箱
     * 1.验证tradeParams中的用户下单信息
     * 2.封装trade,方便后面持久化
     *
     * @param tradeParams 用户下单信息
     * @return 待入库的订单对象
     */
    public NewPileTrade validateAndWrapperNewPileTrade(NewPileTrade trade, TradeParams tradeParams) {

        // 1.设置订单基本信息(购买人,商家,代客下单操作人,收货地址,发票信息,配送方式,支付方式,备注,附件,操作人ip,订单商品,订单总价...)
        if (tradeParams.isCommitFlag()) {
            // 购买人,商家,代客下单操作人,订单项Oid,订单id,订单来源方等只有在下单的时候才设置(因为在修改订单时无法修改这些信息)
            Optional<CommonLevelVO> commonLevelVO;
            boolean flag = true;
            if (tradeParams.getStoreLevel() == null) {
                flag = false;
                commonLevelVO =
                        Optional.of(fromCustomerLevel(customerLevelQueryProvider.getDefaultCustomerLevel().getContext()));
            } else {
                commonLevelVO = Optional.of(tradeParams.getStoreLevel());
            }
            trade.setBuyer(Buyer.fromCustomer(tradeParams.getCustomer(), commonLevelVO, flag));
            trade.setSupplier(tradeParams.getSupplier());
            trade.setSeller(tradeParams.getSeller());
            tradeParams.getTradeItems().forEach(t -> {
                t.setOid(generatorService.generateOid());
                if (StringUtils.isBlank(t.getAdminId())) {
                    t.setAdminId(String.format("%d", tradeParams.getSupplier().getSupplierId()));
                }
            });
            trade.setId(generatorService.generateNewPileTid());

            trade.setPlatform(tradeParams.getPlatform());
            trade.setOrderSource(tradeParams.getOrderSource());
            trade.setOrderType(OrderType.NORMAL_ORDER);
            trade.setShareUserId(tradeParams.getShareUserId());
            trade.setTradeWareHouse(tradeParams.getTradeWareHouse());
        }
        trade.setConsignee(wrapperConsignee(tradeParams.getConsigneeId(), tradeParams.getDetailAddress(),
                tradeParams.getConsigneeUpdateTime(), tradeParams.getConsignee()));

        trade.setDeliverWay(tradeParams.getDeliverWay());
        if (tradeParams.getPayType() != null) {
            trade.setPayInfo(PayInfo.builder()
                    .payTypeId(String.format("%d", tradeParams.getPayType().toValue()))
                    .payTypeName(tradeParams.getPayType().name())
                    .desc(tradeParams.getPayType().getDesc())
                    .build());
        }
        trade.setBuyerRemark(tradeParams.getBuyerRemark());
        trade.setSellerRemark(tradeParams.getSellerRemark());
        trade.setEncloses(tradeParams.getEncloses());
        trade.setRequestIp(tradeParams.getIp());
        trade.setTradeItems(tradeParams.getTradeItems());
        trade.setTradePrice(tradeParams.getTradePrice());
        trade.setWareHouseCode(tradeParams.getWareHouseCode());
        trade.setWareId(tradeParams.getWareId());
        trade.setBookingDate(tradeParams.getBookingDate());
        trade.setActivityType(TradeActivityTypeEnum.NEWPILETRADE.toActivityType());
        if (Objects.nonNull(tradeParams.getSuitBuyCount()) && tradeParams.getSuitBuyCount() > 0) {
            trade.setSuitBuyCount(tradeParams.getSuitBuyCount());
        }
        if (Objects.nonNull(tradeParams.getMarketingId())) {
            trade.setMarketingId(tradeParams.getMarketingId());
        }
        trade.setSaleType(tradeParams.getSaleType());

        // 2.2.订单中商品信息填充(同时设置商品的客户级别价格/客户指定价salePrice)
        log.info("TradeService.validateAndWrapperTrade GoodsInfo front trade:{}", JSONObject.toJSONString(trade));
        Trade convertTrade = KsBeanUtil.convert(trade, Trade.class);
        TradeGoodsListVO skuList = getDevanningGoodsInfoResponse(convertTrade);
        // 2.3.若是后端下单/修改,校验商家跟商品的关系(因为前端下单信息都是从库里读取的,无需验证)
        if (tradeParams.isBackendFlag()) {
            boolean existInvalidGoods = skuList.getGoodsInfos().parallelStream().anyMatch(goodsInfo -> !trade
                    .getSupplier().getSupplierId()
                    .equals(goodsInfo.getCompanyInfoId()));
            if (existInvalidGoods) {
                throw new SbcRuntimeException("K-030006");
            }
        }

        // 分销商品、开店礼包商品、拼团商品、企业购商品不验证起限定量
        boolean isIepCustomer = trade.getBuyer().isIepCustomer();
        skuList.getGoodsInfos().forEach(item -> {
            if (DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())
                    || DefaultFlag.YES.equals(trade.getStoreBagsFlag())
                    || Objects.nonNull(tradeParams.getGrouponForm())
                    || isIepCustomer) {
                item.setCount(null);
                item.setMaxCount(null);
            }
        });
        // 2.4.校验sku 和 【商品价格计算第①步】: 商品的 客户级别价格 (完成客户级别价格/客户指定价/订货区间价计算) -> levelPrice
        boolean throwFlag = verifyService.verifyDevanningGoodsToNewPile(trade.getTradeItems(), tradeParams.getOldTradeItems(), skuList, trade.getSupplier()
                .getStoreId(), true);
        List<String> marketingSkuIds = new ArrayList();
        tradeParams.getMarketingList().stream().forEach(tradeMarketingDTO -> {
            if (CollectionUtils.isNotEmpty(tradeMarketingDTO.getSkuIds())) {
                marketingSkuIds.addAll(tradeMarketingDTO.getSkuIds());
            }
        });

        // 特价商品和大客户商品回设 ——— 特价 > 企业购 > 大客户价
        boolean isVipCustomer = trade.getBuyer().isVipCustomer();
        if (Objects.isNull(trade.getIsFlashSaleGoods()) || !trade.getIsFlashSaleGoods()) {
            trade.getTradeItems().forEach(i -> {
                if (Objects.nonNull(i.getGoodsInfoType()) && i.getGoodsInfoType() == 1 && Objects.nonNull(i.getSpecialPrice())) {
                    i.setSplitPrice(i.getSpecialPrice().multiply(new BigDecimal(i.getNum())));
                    i.setPrice(i.getSpecialPrice());
                    i.setLevelPrice(i.getSpecialPrice());
                } else if (isIepCustomer && Objects.nonNull(i.getVipPrice()) && i.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //企业购会员价格不参与任何营销活动
                    if (!marketingSkuIds.contains(i.getSkuId())) {
                        i.setSplitPrice(i.getVipPrice().multiply(new BigDecimal(i.getNum())));
                        i.setPrice(i.getVipPrice());
                        i.setLevelPrice(i.getVipPrice());
                    }
                } else if (isVipCustomer && Objects.nonNull(i.getVipPrice()) && i.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //vip价格不参与任何营销活动
                    if (!marketingSkuIds.contains(i.getSkuId())) {
                        i.setSplitPrice(i.getVipPrice().multiply(new BigDecimal(i.getNum())));
                        i.setPrice(i.getVipPrice());
                        i.setLevelPrice(i.getVipPrice());
                    }
                }
            });
        }

        // 2.5.商品营销信息冗余,验证,计算,设置各营销优惠,实付金额
        if (tradeParams.getMarketingList().size() > 0) {
            logger.info("TradeService.validateAndWrapperTrade MarketingList:{}", JSONObject.toJSONString(tradeParams.getMarketingList()));
            tradeParams.getMarketingList().forEach(i -> {
                List<TradeItem> items = trade.getTradeItems().stream().filter(s -> i.getSkuIds().contains(s.getSkuId()))
                        .collect(Collectors.toList());
                items.forEach(s -> s.getMarketingIds().add(i.getMarketingId()));
            });
        }
        //营销信息
        this.wrapperMarketingForCommitNewPile(trade, tradeParams, tradeParams.getCustomer());


        //2.8.计算满系营销、优惠券均摊价，并设置结算信息
        calcMarketingPriceNewPile(trade);

        // 2.9.计算并设置订单总价(已减去营销优惠总金额)
        trade.setTradePrice(calcNewPile(trade));
        if (throwFlag) {
            NewPileTradeItemSnapshot byCustomerId = newPileTradeItemService.findByCustomerId(trade.getBuyer().getId());
            byCustomerId.getItemGroups().forEach(param -> {
                if (param.getSupplier().getStoreId().equals(trade.getSupplier()
                        .getStoreId())) {
                    param.setTradeMarketings(trade.getTradeMarketings());
                }
            });
            newPileTradeItemService.updateTradeItemSnapshotNoRollback(byCustomerId);
            throw new SbcRuntimeException("K-050137");
        }

        TradePrice tradePrice = trade.getTradePrice();
        // 2.11.计算订单总价
        if (tradePrice.isSpecial()) {
            // 2.12.【商品价格计算第③步】: 商品的 特价订单 均摊价 -> splitPrice
            tradeItemService.clacSplitPrice(trade.getTradeItems(), tradePrice.getPrivilegePrice());
            tradePrice.setTotalPrice(tradePrice.getPrivilegePrice());//应付金额 = 特价+运费（囤货阶段不算运费）
        } else {
            tradePrice.setTotalPrice(tradePrice.getTotalPrice());//应付金额 = 应付+运费（囤货阶段不算运费））
        }
        //统计商品总件数
        Long goodsTotalNum = 0L;
        Optional<Long> _goodsTotalNum = trade.getTradeItems().stream()
                .map(TradeItem::getNum).reduce((sum, item) -> {
                    sum += item;
                    return sum;
                });
        if (_goodsTotalNum.isPresent()) {
            goodsTotalNum += _goodsTotalNum.get();
        }
        log.info("trade.getDeliverWay {}", trade.getDeliverWay());

        Optional<Long> _giftNum = trade.getGifts().stream()
                .map(TradeItem::getNum).reduce((sum, item) -> {
                    sum += item;
                    return sum;
                });
        if (_giftNum.isPresent()) {
            goodsTotalNum += _giftNum.get();
        }
        trade.setGoodsTotalNum(goodsTotalNum);
        trade.setSourceChannel(tradeParams.getSourceChannel());
        return trade;
    }


    /**
     * 验证下单信息并封装订单信息 拆箱
     * 【公共方法】-客户下单(PC/H5/APP...), 商家代客下单/修改订单(supplier/employeeApp/supplierAPP...)
     * 1.验证tradeParams中的用户下单信息
     * 2.封装trade,方便后面持久化
     *
     * @param tradeParams 用户下单信息
     * @return 待入库的订单对象
     */
    public Trade validateAndWrapperNewPileTradePick(Trade trade, TradeParams tradeParams) {

        // 1.设置订单基本信息(购买人,商家,代客下单操作人,收货地址,发票信息,配送方式,支付方式,备注,附件,操作人ip,订单商品,订单总价...)
        if (tradeParams.isCommitFlag()) {
            // 购买人,商家,代客下单操作人,订单项Oid,订单id,订单来源方等只有在下单的时候才设置(因为在修改订单时无法修改这些信息)
            Optional<CommonLevelVO> commonLevelVO;
            boolean flag = true;
            if (tradeParams.getStoreLevel() == null) {
                flag = false;
                commonLevelVO =
                        Optional.of(fromCustomerLevel(customerLevelQueryProvider.getDefaultCustomerLevel().getContext()));
            } else {
                commonLevelVO = Optional.of(tradeParams.getStoreLevel());
            }
            trade.setBuyer(Buyer.fromCustomer(tradeParams.getCustomer(), commonLevelVO, flag));
            trade.setSupplier(tradeParams.getSupplier());
            trade.setSeller(tradeParams.getSeller());
            tradeParams.getTradeItems().forEach(t -> {
                if (StringUtils.isBlank(t.getAdminId())) {
                    t.setAdminId(String.format("%d", tradeParams.getSupplier().getSupplierId()));
                }
            });

            trade.setId(generatorService.generateNewPilePickTid());
            trade.setPlatform(tradeParams.getPlatform());
            trade.setOrderSource(tradeParams.getOrderSource());
            trade.setOrderType(OrderType.NORMAL_ORDER);
            trade.setShareUserId(tradeParams.getShareUserId());
            trade.setTradeWareHouse(tradeParams.getTradeWareHouse());
        }
        trade.setConsignee(wrapperConsignee(tradeParams.getConsigneeId(), tradeParams.getDetailAddress(),
                tradeParams.getConsigneeUpdateTime(), tradeParams.getConsignee()));
        trade.setDeliverWay(tradeParams.getDeliverWay());
        if (tradeParams.getPayType() != null) {
            trade.setPayInfo(PayInfo.builder()
                    .payTypeId(String.format("%d", tradeParams.getPayType().toValue()))
                    .payTypeName(tradeParams.getPayType().name())
                    .desc(tradeParams.getPayType().getDesc())
                    .build());
        }
        trade.setBuyerRemark(tradeParams.getBuyerRemark());
        trade.setSellerRemark(tradeParams.getSellerRemark());
        trade.setEncloses(tradeParams.getEncloses());
        trade.setRequestIp(tradeParams.getIp());
        trade.setTradeItems(tradeParams.getTradeItems());
        trade.setTradePrice(tradeParams.getTradePrice());
        trade.setWareHouseCode(tradeParams.getWareHouseCode());
        trade.setWareId(tradeParams.getWareId());
        trade.setActivityType(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType());

        trade.setBookingDate(tradeParams.getBookingDate());
        if (Objects.nonNull(tradeParams.getSuitBuyCount()) && tradeParams.getSuitBuyCount() > 0) {
            trade.setSuitBuyCount(tradeParams.getSuitBuyCount());
        }
        if (Objects.nonNull(tradeParams.getMarketingId())) {
            trade.setMarketingId(tradeParams.getMarketingId());
        }
        trade.setSaleType(tradeParams.getSaleType());
        trade.setTradePrice(TradePrice.builder().totalPrice(BigDecimal.ZERO).originPrice(BigDecimal.ZERO).build());
        //塞入物流公司信息
        if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay())) {
            if (Objects.nonNull(tradeParams.getLogisticsInfo())
                    && (StringUtils.isNotBlank(tradeParams.getLogisticsInfo().getId())
                    || (Objects.nonNull(tradeParams.getLogisticsInfo().getInsertFlag())
                    && tradeParams.getLogisticsInfo().getInsertFlag() == 1))) {
                trade.setLogisticsCompanyInfo(tradeParams.getLogisticsInfo());
            } else if (Objects.nonNull(tradeParams.getLogisticsInfo()) && StringUtils.isNotEmpty(tradeParams.getLogisticsInfo().getReceivingPoint())) {
                trade.setLogisticsCompanyInfo(tradeParams.getLogisticsInfo());
            }
        }

        // 2.订单中商品信息填充(同时设置商品的客户级别价格/客户指定价salePrice)
        log.info("TradeService.validateAndWrapperTrade GoodsInfo front trade:{}", JSONObject.toJSONString(trade));

        //统计商品总件数
        AtomicReference<Long> buySum = new AtomicReference<>(0l);
        trade.getTradeItems().forEach(var -> {
            buySum.updateAndGet(v -> v + var.getNum());
        });
        //填充商品
        TradeGoodsListVO skuList = getDevanningGoodsInfoResponse(trade.getBuyer(), trade.getTradeItems(), trade.getWareId(), trade.getWareHouseCode());
        boolean throwFlag = verifyService.verifyDevanningGoodsTo(trade.getTradeItems(), tradeParams.getOldTradeItems(), skuList, trade.getSupplier()
                .getStoreId(), true);
        if (throwFlag) {
            NewPileTradeItemSnapshot byCustomerId = newPileTradeItemService.findByCustomerId(trade.getBuyer().getId());
            byCustomerId.getItemGroups().forEach(param -> {
                if (param.getSupplier().getStoreId().equals(trade.getSupplier()
                        .getStoreId())) {
                    param.setTradeMarketings(trade.getTradeMarketings());
                }
            });
            newPileTradeItemService.updateTradeItemSnapshotNoRollback(byCustomerId);
            throw new SbcRuntimeException("K-050137");
        }
        /**配送方式：第三方物流，收货地址为外省且满30件商品时，配送费可优惠2元/每箱;
         * 优惠方式为商品单价-2，统计优惠金额
         * */
        log.info("trade.getDeliverWay {}", trade.getDeliverWay());
        boolean freightCouponFlag = DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && trade.getConsignee().getProvinceId() != 430000 && 30 <= buySum.get();

        log.info("trade.getDeliverWay {}", trade.getDeliverWay());

        log.info("freightCouponFlag {}", freightCouponFlag);
        //todo 此处反余额或优惠券
        if (freightCouponFlag) {
            BigDecimal freightCouponPrice = BigDecimal.valueOf(buySum.get()).multiply(BigDecimal.valueOf(2));
        }
        // 2.9.计算并设置订单总价(已减去营销优惠总金额)
        trade.setTradePrice(calc(trade));
        trade.setGoodsTotalNum(buySum.get());
        trade.setSourceChannel(tradeParams.getSourceChannel());
        return trade;
    }


    /**
     * 计算订单价格
     * 订单价格 = 商品总价 - 营销优惠总金额
     *
     * @param trade
     */
    private TradePrice calc(Trade trade) {
        TradePrice tradePriceTemp = trade.getTradePrice();
        if (tradePriceTemp == null) {
            tradePriceTemp = new TradePrice();
            trade.setTradePrice(tradePriceTemp);
        }
        final TradePrice tradePrice = tradePriceTemp;

        // 1.计算商品总价
        handlePricePick(trade.getTradeItems(), tradePrice);
//        List<TradeMarketingVO> list = trade.getTradeMarketings().stream().filter(i -> i.getMarketingType()
//                != MarketingType.GIFT).collect(Collectors.toList());

//        // 2.计算所有营销活动的总优惠金额(非满赠)
//        BigDecimal discountPrice = list.stream().filter(i -> i.getMarketingType() != MarketingType.GIFT).map
//                (TradeMarketingVO
//                        ::getDiscountsAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//        if (trade.getTradeCoupon() != null) {
//            discountPrice = discountPrice.add(trade.getTradeCoupon().getDiscountsAmount());
//        }
//
//        // 3.计算各类营销活动的优惠金额(比如:满折优惠xxx,满减优惠yyy)
//        List<DiscountsPriceDetail> discountsPriceDetails = new ArrayList<>();
//        list.stream().collect(Collectors.groupingBy(TradeMarketingVO::getMarketingType)).forEach((key, value) -> {
//            DiscountsPriceDetail detail = DiscountsPriceDetail.builder()
//                    .marketingType(key)
//                    .discounts(value.stream().map(TradeMarketingVO::getDiscountsAmount).reduce(BigDecimal.ZERO,
//                            BigDecimal::add))
//                    .build();
//            discountsPriceDetails.add(detail);
//        });
//        tradePrice.setDiscountsPriceDetails(discountsPriceDetails);
//
//        // 4.设置优惠券优惠金额
//        if (trade.getTradeCoupon() != null) {
//            BigDecimal couponPrice = trade.getTradeCoupon().getDiscountsAmount();
//            tradePrice.setCouponPrice(couponPrice);
////            discountPrice.subtract(couponPrice);
//        }
//
//        // 5.设置优惠总金额、应付金额 = 商品总金额 - 总优惠金额
//        tradePrice.setDiscountsPrice(discountPrice);
//        tradePrice.setTotalPrice(tradePrice.getTotalPrice().subtract(discountPrice));
        return tradePrice;
    }

    /**
     * 批发拆箱
     * 1.根据 订单项List 获取商品信息List
     * 2.设置该商品信息List中会用到的区间价信息
     * 3.修改商品信息List中的会员价(salePrice)
     *
     * @return 商品信息List
     */
    public TradeGoodsListVO getDevanningGoodsInfoResponse(Buyer buyer, List<TradeItem> tradeItems, Long wareId, String wareHouseCode) {
        //todo  当前理解在计算费用的时候是拿goodsinfo表的salePrice  这个字段是在goodsInfoQueryProvider.listViewByIdsByMatchFlag这个接口获取goodsinfo表的market_price
        //todo  goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
        //todo  拆箱之后需要去devanninggoodsinfo表的MarketPrice 为了不变动之前逻辑在此处做循环遍历
        //1. 获取sku
        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
//        goodsInfoRequest.setGoodsInfoIds(IteratorUtils.collectKey(trade.getTradeItems(), TradeItem::getSkuId));
        goodsInfoRequest.setDevanningIds(IteratorUtils.collectKey(tradeItems, TradeItem::getDevanningId));
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        goodsInfoRequest.setWareId(wareId);
        goodsInfoRequest.setWareHouseCode(wareHouseCode);
//        GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIdsByMatchFlag(goodsInfoRequest).getContext();

        DevanningGoodsInfoListResponse responseDevanning = devanningGoodsInfoQueryProvider.listViewByIdsByMatchFlag(goodsInfoRequest).getContext();

        GoodsInfoViewByIdsResponse idsResponse = new GoodsInfoViewByIdsResponse();
        idsResponse.setGoodses(responseDevanning.getGoodses());
        idsResponse.setGoodsInfos(KsBeanUtil.convert(responseDevanning.getDevanningGoodsInfoVOS(), GoodsInfoVO.class));

        TradeGoodsListVO response = new TradeGoodsListVO();
        response.setGoodsInfos(idsResponse.getGoodsInfos());
        response.setGoodses(idsResponse.getGoodses());
        CustomerVO customerVO = customerQueryProvider.getCustomerById(new
                CustomerGetByIdRequest(buyer.getId())).getContext();

        List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class);
        GoodsIntervalPriceByCustomerIdResponse intervalPriceResponse =
                goodsIntervalPriceProvider.putByCustomerId(
                        GoodsIntervalPriceByCustomerIdRequest.builder().goodsInfoDTOList(goodsInfoDTOList)
                                .customerId(customerVO.getCustomerId()).build()).getContext();
        //计算区间价
        response.setGoodsIntervalPrices(intervalPriceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(intervalPriceResponse.getGoodsInfoVOList());

        //目前只计算商品的客户级别价格/客户指定价
        MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
        filterRequest.setGoodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class));
        filterRequest.setCustomerDTO(KsBeanUtil.convert(customerVO, CustomerDTO.class));
        response.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext().getGoodsInfoVOList());
        logger.info("TradeService.getGoodsInfoResponse response:{}", response);
        return response;
    }


    public CommonLevelVO fromCustomerLevel(CustomerLevelVO customerLevelVO) {
        if (customerLevelVO == null) {
            return null;
        }
        CommonLevelVO result = new CommonLevelVO();
        result.setLevelId(customerLevelVO.getCustomerLevelId());
        result.setLevelName(customerLevelVO.getCustomerLevelName());
        result.setLevelDiscount(customerLevelVO.getCustomerLevelDiscount());

        return result;
    }

    /**
     * 根据用户提交的收货地址信息封装对象
     *
     * @param consigneeId         选择的收货地址id
     * @param detailAddress       详细地址(包括省市区)
     * @param consigneeUpdateTime 地址更新时间 - 可能已经用不到了
     * @param consigneeTmp        用户提交的临时收货地址
     * @return 封装后的收货地址对象
     */
    public Consignee wrapperConsignee(String consigneeId, String detailAddress, String consigneeUpdateTime,
                                      Consignee consigneeTmp) {
        if (StringUtils.isNotBlank(consigneeId)) {
            // 根据id查询收货人信息
            BaseResponse<CustomerDeliveryAddressByIdResponse> customerDeliveryAddressByIdResponseBaseResponse =
                    tradeCacheService.getCustomerDeliveryAddressById(consigneeId);
            CustomerDeliveryAddressByIdResponse customerDeliveryAddressByIdResponse =
                    customerDeliveryAddressByIdResponseBaseResponse.getContext();
            if (customerDeliveryAddressByIdResponse == null || customerDeliveryAddressByIdResponse.getDelFlag() == DeleteFlag.YES) {
                throw new SbcRuntimeException("K-050313");
            }
            return Consignee
                    .builder()
                    .id(consigneeId)
                    .detailAddress(detailAddress)
                    .phone(customerDeliveryAddressByIdResponse.getConsigneeNumber())
                    .provinceId(customerDeliveryAddressByIdResponse.getProvinceId())
                    .cityId(customerDeliveryAddressByIdResponse.getCityId())
                    .areaId(customerDeliveryAddressByIdResponse.getAreaId())
                    .address(customerDeliveryAddressByIdResponse.getDeliveryAddress())
                    .name(customerDeliveryAddressByIdResponse.getConsigneeName())
                    .twonId(customerDeliveryAddressByIdResponse.getTwonId())
                    .twonName(customerDeliveryAddressByIdResponse.getTwonName())
                    .build();
        } else {
            //若id为空,则赋值页面传入的临时地址(代客下单特殊-可以传临时地址)
            return Consignee
                    .builder()
                    .detailAddress(detailAddress)
                    .phone(consigneeTmp.getPhone())
                    .provinceId(consigneeTmp.getProvinceId())
                    .cityId(consigneeTmp.getCityId())
                    .areaId(consigneeTmp.getAreaId())
                    .address(consigneeTmp.getAddress())
                    .name(consigneeTmp.getName())
                    .twonId(consigneeTmp.getTwonId())
                    .twonName(consigneeTmp.getTwonName())
                    .build();
        }
    }


    /**
     * 批发拆箱
     * 1.根据 订单项List 获取商品信息List
     * 2.设置该商品信息List中会用到的区间价信息
     * 3.修改商品信息List中的会员价(salePrice)
     *
     * @param trade 订单
     * @return 商品信息List
     */
    public TradeGoodsListVO getDevanningGoodsInfoResponse(Trade trade) {
        //todo  当前理解在计算费用的时候是拿goodsinfo表的salePrice  这个字段是在goodsInfoQueryProvider.listViewByIdsByMatchFlag这个接口获取goodsinfo表的market_price
        //todo  goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
        //todo  拆箱之后需要去devanninggoodsinfo表的MarketPrice 为了不变动之前逻辑在此处做循环遍历
        //1. 获取sku
        Buyer b = trade.getBuyer();
        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
//        goodsInfoRequest.setGoodsInfoIds(IteratorUtils.collectKey(trade.getTradeItems(), TradeItem::getSkuId));
        goodsInfoRequest.setDevanningIds(IteratorUtils.collectKey(trade.getTradeItems(), TradeItem::getDevanningId));
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        goodsInfoRequest.setWareId(trade.getWareId());
        goodsInfoRequest.setWareHouseCode(trade.getWareHouseCode());
//        GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIdsByMatchFlag(goodsInfoRequest).getContext();

        DevanningGoodsInfoListResponse responseDevanning = devanningGoodsInfoQueryProvider.listViewByIdsByMatchFlag(goodsInfoRequest).getContext();

        GoodsInfoViewByIdsResponse idsResponse = new GoodsInfoViewByIdsResponse();
        idsResponse.setGoodses(responseDevanning.getGoodses());
        idsResponse.setGoodsInfos(KsBeanUtil.convert(responseDevanning.getDevanningGoodsInfoVOS(), GoodsInfoVO.class));
//        List<String> goodsinfoids = idsResponse.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
//        List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoProvider.getmaxdata(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(goodsinfoids).build()).getContext().getDevanningGoodsInfoVOS();
//        for (GoodsInfoVO goodsInfoVO:idsResponse.getGoodsInfos()){
//            for (DevanningGoodsInfoVO devanningGoodsInfoVO:devanningGoodsInfoVOS){
//                if (devanningGoodsInfoVO.getGoodsInfoId().equalsIgnoreCase(goodsInfoVO.getGoodsInfoId())){
//                    goodsInfoVO.setMarketPrice(devanningGoodsInfoVO.getMarketPrice());
//                    goodsInfoVO.setSalePrice(devanningGoodsInfoVO.getMarketPrice());
//                }
//            }
//        }
        TradeGoodsListVO response = new TradeGoodsListVO();
        response.setGoodsInfos(idsResponse.getGoodsInfos());
        response.setGoodses(idsResponse.getGoodses());
        CustomerVO customerVO = customerQueryProvider.getCustomerById(new
                CustomerGetByIdRequest(b.getId())).getContext();

        List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class);
        GoodsIntervalPriceByCustomerIdResponse intervalPriceResponse =
                goodsIntervalPriceProvider.putByCustomerId(
                        GoodsIntervalPriceByCustomerIdRequest.builder().goodsInfoDTOList(goodsInfoDTOList)
                                .customerId(customerVO.getCustomerId()).build()).getContext();
        //计算区间价
        response.setGoodsIntervalPrices(intervalPriceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(intervalPriceResponse.getGoodsInfoVOList());

        //目前只计算商品的客户级别价格/客户指定价
        MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
        filterRequest.setGoodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class));
        filterRequest.setCustomerDTO(KsBeanUtil.convert(customerVO, CustomerDTO.class));
        response.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext().getGoodsInfoVOList());
        log.info("TradeService.getGoodsInfoResponse response:{}", response);
        return response;
    }


    /**
     * 包装营销信息(供提交订单使用) 拆箱
     */
    public void wrapperMarketingForCommitNewPile(NewPileTrade trade, TradeParams tradeParams, CustomerVO customerVO) {
        // 1.构建订单满系营销对象
        trade.setTradeMarketings(this.wrapperMarketingForConfirmDevanning(trade.getTradeItems(),
                tradeParams.getMarketingList()));

        // 2.构建订单优惠券对象
        if (StringUtils.isNotEmpty(tradeParams.getCouponCodeId())) {
            trade.setTradeCoupon(this.buildTradeCouponInfo(
                    trade.getTradeItems(), tradeParams.getCouponCodeId(), tradeParams.isForceCommit(),
                    StringUtils.isNotBlank(customerVO.getParentCustomerId()) ? customerVO.getParentCustomerId() : customerVO.getCustomerId()));
        }
    }


    /**
     * 包装营销信息(供确认订单使用) 拆箱
     */
    public List<TradeMarketingVO> wrapperMarketingForConfirmDevanning(List<TradeItem> skus, List<TradeMarketingDTO>
            tradeMarketingRequests) {

        //1.需求变动 skus有相同skuid 合并数量并且合并总金额
        for (TradeItem tradeItem : skus) {
            tradeItem.setAllMarketPrice(BigDecimal.valueOf(tradeItem.getNum()).multiply(tradeItem.getPrice()));
//            tradeItem.setVipPrice(BigDecimal.valueOf(tradeItem.getNum()).multiply(tradeItem.getVipPrice()));
            if (Objects.nonNull(tradeItem.getDivisorFlag()) && tradeItem.getDivisorFlag().compareTo(BigDecimal.ZERO) > 0) {
                tradeItem.setBNum(tradeItem.getDivisorFlag().multiply(BigDecimal.valueOf(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_DOWN));
            } else {
                tradeItem.setBNum(BigDecimal.valueOf(tradeItem.getNum()));
            }
        }
        Map<String, BigDecimal> numCollect = skus.stream().filter(x -> {
            return Objects.nonNull(x.getDivisorFlag()) && x.getDivisorFlag().compareTo(BigDecimal.ONE) >= 0;
        }).collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getBNum, BigDecimal::add)));

        Map<String, BigDecimal> marketCollect = skus.stream().filter(x -> {
            return Objects.nonNull(x.getDivisorFlag()) && x.getDivisorFlag().compareTo(BigDecimal.ONE) >= 0;
        }).collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getAllMarketPrice, BigDecimal::add)));
//        Map<String, BigDecimal> vipCollect = skus.stream().collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getVipPrice, BigDecimal::add)));
        skus = skus.stream().filter(x -> {
            // 移除拆箱的规格
            return Objects.nonNull(x.getDivisorFlag()) && x.getDivisorFlag().compareTo(BigDecimal.ONE) >= 0;
        }).filter(distinctByKey((p) -> (p.getSkuId()))).collect(Collectors.toList());
        // 1.构建营销插件请求对象
        List<TradeMarketingWrapperDTO> requests = new ArrayList<>();
        List<TradeMarketingVO> tradeMarketings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tradeMarketingRequests)) {
            List<TradeItem> finalSkus = skus;
            if (CollectionUtils.isNotEmpty(finalSkus)) {
                tradeMarketingRequests.forEach(tradeMarketing -> {
                    List<TradeItemInfoDTO> tradeItems = finalSkus.stream()
                            .filter(s -> tradeMarketing.getSkuIds().contains(s.getSkuId()))
                            .map(t -> TradeItemInfoDTO.builder()
//                                .num(Objects.isNull(t.getDivisorFlag()) ? t.getNum() : (t.getDivisorFlag().multiply(BigDecimal.valueOf(t.getNum()))).setScale(0,BigDecimal.ROUND_DOWN).longValue())
                                    .num(numCollect.get(t.getSkuId()).setScale(0, BigDecimal.ROUND_DOWN).longValue())
                                    .price(t.getPrice())
                                    .allMarketPrice(marketCollect.get(t.getSkuId()))
//                                .allVipPrice(vipCollect.get(t.getSkuId()))
                                    .skuId(t.getSkuId())
                                    .storeId(t.getStoreId())
                                    .distributionGoodsAudit(t.getDistributionGoodsAudit())
                                    .build())
                            .collect(Collectors.toList());
                    requests.add(TradeMarketingWrapperDTO.builder()
                            .tradeMarketingDTO(tradeMarketing)
                            .tradeItems(tradeItems).build());
                });
            }
        }

        // 2.调用营销插件，并设置满系营销信息
        if (CollectionUtils.isNotEmpty(requests)) {
            MarketingTradeBatchWrapperRequest marketingTradeBatchWrapperRequest = MarketingTradeBatchWrapperRequest.builder()
                    .wraperDTOList(requests)
                    .build();
            List<TradeMarketingWrapperVO> voList = marketingQueryProvider.batchWrapper(marketingTradeBatchWrapperRequest).getContext().getWraperVOList();
            if (CollectionUtils.isNotEmpty(voList)) {
                voList.forEach(tradeMarketingWrapperVO -> {
                    tradeMarketings.add(tradeMarketingWrapperVO.getTradeMarketing());
                });
            }
        }

        return tradeMarketings;
    }


    /**
     * 营销价格计算-结算信息设置
     * 【商品价格计算第②步】: 商品的 满折/满减营销活动 均摊价 -> splitPrice
     *
     * @param trade
     */
    public void calcMarketingPriceNewPile(NewPileTrade trade) {
        // 1.设置满系营销商品优惠后的均摊价、结算信息
        trade.getTradeMarketings().stream().filter(i -> i.getMarketingType() != MarketingType.GIFT).forEach(i -> {
            List<TradeItem> items = trade.getTradeItems().stream().filter(t -> i.getSkuIds().contains(t.getSkuId()))
                    .collect(Collectors.toList());
            tradeItemService.clacSplitPrice(items, i.getRealPayAmount());
            items.forEach(t -> {
                List<TradeItem.MarketingSettlement> settlements = new ArrayList<>();
//                settlements.add(t.new MarketingSettlement(i.getMarketingType(),
//                        t.getSplitPrice())
//                );
                settlements.add(TradeItem.MarketingSettlement.builder().marketingType(i.getMarketingType())
                        .splitPrice(t.getSplitPrice()).build());
                t.setMarketingSettlements(settlements);
            });
        });

        // 2.设置店铺优惠券后的均摊价、结算信息
        TradeCouponVO tradeCoupon = trade.getTradeCoupon();
        if (tradeCoupon != null) {
            // 2.1.查找出优惠券关联的商品，及总价
            List<TradeItem> items = trade.getTradeItems().stream()
                    .filter(t -> trade.getTradeCoupon().getGoodsInfoIds().contains(t.getSkuId()))
                    .collect(Collectors.toList());
            BigDecimal total = tradeItemService.calcSkusTotalPrice(items);

            // 2.2.判断是否达到优惠券使用门槛
            BigDecimal fullBuyPrice = tradeCoupon.getFullBuyPrice();
            if (fullBuyPrice != null && fullBuyPrice.compareTo(total) == 1) {
                throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID);
            }

            // 2.3.如果商品总价小于优惠券优惠金额，设置优惠金额为商品总价
            if (total.compareTo(tradeCoupon.getDiscountsAmount()) == -1) {
                tradeCoupon.setDiscountsAmount(total);
            }

            // 2.4.计算均摊价、结算信息
            items.forEach(item ->
                    item.getCouponSettlements().add(TradeItem.CouponSettlement.builder()
                            .couponType(tradeCoupon.getCouponType())
                            .couponCodeId(tradeCoupon.getCouponCodeId())
                            .couponCode(tradeCoupon.getCouponCode())
                            .splitPrice(item.getSplitPrice()).build())
            );
            tradeItemService.calcSplitPrice(items, total.subtract(trade.getTradeCoupon().getDiscountsAmount()), total);
            items.forEach(item -> {
                TradeItem.CouponSettlement settlement =
                        item.getCouponSettlements().get(item.getCouponSettlements().size() - 1);
                settlement.setReducePrice(settlement.getSplitPrice().subtract(item.getSplitPrice()));
                settlement.setSplitPrice(item.getSplitPrice());
            });
        }
    }


    /**
     * 计算订单价格
     * 订单价格 = 商品总价 - 营销优惠总金额
     *
     * @param trade
     */
    public TradePrice calcNewPile(NewPileTrade trade) {
        TradePrice tradePriceTemp = trade.getTradePrice();
        if (tradePriceTemp == null) {
            tradePriceTemp = new TradePrice();
            trade.setTradePrice(tradePriceTemp);
        }
        final TradePrice tradePrice = tradePriceTemp;

        // 1.计算商品总价
        handlePrice(trade.getTradeItems(), tradePrice);
        List<TradeMarketingVO> list = trade.getTradeMarketings().stream().filter(i -> i.getMarketingType()
                != MarketingType.GIFT).collect(Collectors.toList());

        // 2.计算所有营销活动的总优惠金额(非满赠)
        BigDecimal discountPrice = list.stream().filter(i -> i.getMarketingType() != MarketingType.GIFT).map
                (TradeMarketingVO
                        ::getDiscountsAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (trade.getTradeCoupon() != null) {
            discountPrice = discountPrice.add(trade.getTradeCoupon().getDiscountsAmount());
        }

        // 3.计算各类营销活动的优惠金额(比如:满折优惠xxx,满减优惠yyy)
        List<DiscountsPriceDetail> discountsPriceDetails = new ArrayList<>();
        list.stream().collect(Collectors.groupingBy(TradeMarketingVO::getMarketingType)).forEach((key, value) -> {
            DiscountsPriceDetail detail = DiscountsPriceDetail.builder()
                    .marketingType(key)
                    .discounts(value.stream().map(TradeMarketingVO::getDiscountsAmount).reduce(BigDecimal.ZERO,
                            BigDecimal::add))
                    .build();
            discountsPriceDetails.add(detail);
        });
        tradePrice.setDiscountsPriceDetails(discountsPriceDetails);

        // 4.设置优惠券优惠金额
        if (trade.getTradeCoupon() != null) {
            BigDecimal couponPrice = trade.getTradeCoupon().getDiscountsAmount();
            tradePrice.setCouponPrice(couponPrice);
//            discountPrice.subtract(couponPrice);
        }

        // 5.设置优惠总金额、应付金额 = 商品总金额 - 总优惠金额
        tradePrice.setDeliveryPrice(BigDecimal.ZERO);
        tradePrice.setDiscountsPrice(discountPrice);
        tradePrice.setTotalPrice(tradePrice.getTotalPrice().subtract(discountPrice));
        return tradePrice;
    }


    /**
     * 调用营销插件，构造订单优惠券对象
     *
     * @return
     */
    public TradeCouponVO buildTradeCouponInfo(List<TradeItem> tradeItems, String couponCodeId,
                                              boolean forceCommit, String customerId) {
        // 1.查询tradeItems的storeCateIds
        List<String> goodsIds = tradeItems.stream()
                .map(TradeItem::getSpuId).distinct().collect(Collectors.toList());
        List<StoreCateGoodsRelaVO> relas =
                storeCateQueryProvider.listByGoods(new StoreCateListByGoodsRequest(goodsIds)).getContext().getStoreCateGoodsRelaVOList();
        Map<String, List<StoreCateGoodsRelaVO>> relasMap = relas.stream()
                .collect(Collectors.groupingBy(rela -> rela.getGoodsId()));

        // 2.构建营销插件请求对象
        List<TradeItemInfoDTO> tradeItemInfos = tradeItems.stream()
                .map(t -> TradeItemInfoDTO.builder()
                        .num(Objects.isNull(t.getDevanningId()) ? t.getNum()
                                : (t.getDivisorFlag().multiply(BigDecimal.valueOf(t.getNum()))
                                .setScale(0, BigDecimal.ROUND_DOWN).longValue()))
                        .price(t.getPrice())
                        .skuId(t.getSkuId())
                        .cateId(t.getCateId())
                        .storeId(t.getStoreId())
                        .brandId(t.getBrand())
                        .storeCateIds(relasMap.get(t.getSpuId()).stream()
                                .map(rela -> rela.getStoreCateId()).collect(Collectors.toList()))
                        .distributionGoodsAudit(t.getDistributionGoodsAudit())
                        .build())
                .collect(Collectors.toList());
        MarketingCouponWrapperRequest request = new MarketingCouponWrapperRequest();
        request.setCustomerId(customerId);
        request.setCouponCodeId(couponCodeId);
        request.setForceCommit(forceCommit);
        request.setTradeItems(tradeItemInfos);

        // 3.调用营销插件，查询订单优惠券对象
        MarketingCouponWrapperResponse response = marketingCouponPluginProvider.wrapper(request).getContext();
        if (response != null) {
            return response.getTradeCoupon();
        }
        return null;
    }


    /**
     * 计算商品总价
     *
     * @param tradeItems 多个订单项(商品)
     * @param tradePrice 订单价格对象(其中包括商品商品总金额,原始金额,应付金额)
     */
    public void handlePrice(List<TradeItem> tradeItems, TradePrice tradePrice) {
        tradePrice.setGoodsPrice(BigDecimal.ZERO);
        tradePrice.setOriginPrice(BigDecimal.ZERO);
        tradePrice.setTotalPrice(BigDecimal.ZERO);
        tradeItems.forEach(t -> {
            BigDecimal buyItemPrice = t.getPrice().multiply(BigDecimal.valueOf(t.getNum()));
            // 订单商品总价
            tradePrice.setGoodsPrice(tradePrice.getGoodsPrice().add(buyItemPrice));
            // 订单应付总金额
            tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(buyItemPrice));
            // 订单原始总金额
            tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(buyItemPrice));
        });
    }

    /**
     * 计算商品总价
     *
     * @param tradeItems 多个订单项(商品)
     * @param tradePrice 订单价格对象(其中包括商品商品总金额,原始金额,应付金额)
     */
    public void handlePricePick(List<TradeItem> tradeItems, TradePrice tradePrice) {
        tradePrice.setGoodsPrice(BigDecimal.ZERO);
        tradePrice.setOriginPrice(BigDecimal.ZERO);
        tradePrice.setTotalPrice(BigDecimal.ZERO);
        tradeItems.forEach(t -> {
            BigDecimal buyItemPrice = t.getPrice().multiply(BigDecimal.valueOf(t.getNum()));
            // 订单商品总价
            tradePrice.setGoodsPrice(tradePrice.getGoodsPrice().add(buyItemPrice));
        });
    }


    /**
     * 构建订单组对象，同时修改订单列表中相应的价格信息
     *
     * @param trades             订单列表
     * @param tradeCommitRequest
     * @return
     */
    public NewPileTradeGroup wrapperTradeGroupNewPile(List<NewPileTrade> trades, TradeCommitRequest tradeCommitRequest) {
        if (tradeCommitRequest.getCommonCodeId() == null) {
            return null;
        }
        CustomerVO customer = tradeCommitRequest.getCustomer();
        NewPileTradeGroup tradeGroup = new NewPileTradeGroup();

        // 1.请求营销插件，验证并包装优惠券信息(这里只需要批发订单 add by jiangxin 20220326)
        List<TradeItem> items = trades.stream().filter(item -> item.getSaleType().equals(SaleType.WHOLESALE)).flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors
                .toList());
        TradeCouponVO tradeCoupon = this.buildTradeCouponInfo(
                items,
                tradeCommitRequest.getCommonCodeId(),
                tradeCommitRequest.isForceCommit(),
                StringUtils.isNotBlank(customer.getParentCustomerId()) ? customer.getParentCustomerId() : customer.getCustomerId());
        if (tradeCoupon == null) {
            return null;
        }
        log.info("================ items:{}", items);
        log.info("================ goodsInfoIds:{}", tradeCoupon.getGoodsInfoIds());
        // 2.找出需要均摊的商品，以及总价
        List<TradeItem> matchItems = items.stream()
                .filter(t -> tradeCoupon.getGoodsInfoIds().contains(t.getSkuId())).collect(Collectors.toList());
        BigDecimal total = newPileTradeItemService.calcSkusTotalPrice(matchItems);
        log.info("================= matchItems:{}", matchItems);
        // 3.判断是否达到优惠券使用门槛
        BigDecimal fullBuyPrice = tradeCoupon.getFullBuyPrice();
        if (fullBuyPrice != null && fullBuyPrice.compareTo(total) == 1) {
            log.error("fullBuyPrice:{},total:{}===================", fullBuyPrice, total);
            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID);
        }

        // 4.如果商品总价小于优惠券优惠金额，设置优惠金额为商品总价
        if (total.compareTo(tradeCoupon.getDiscountsAmount()) == -1) {
            tradeCoupon.setDiscountsAmount(total);
        }

        // 5.设置关联商品的结算信息
        matchItems.forEach(item ->
                item.getCouponSettlements().add(TradeItem.CouponSettlement.builder()
                        .couponType(tradeCoupon.getCouponType())
                        .couponCodeId(tradeCoupon.getCouponCodeId())
                        .couponCode(tradeCoupon.getCouponCode())
                        .splitPrice(item.getSplitPrice()).build())
        );

        // 6.设置关联商品的均摊价
        newPileTradeItemService.calcSplitPrice(matchItems, total.subtract(tradeCoupon.getDiscountsAmount()), total);

        // 7.刷新关联商品的结算信息
        matchItems.forEach(item -> {
            TradeItem.CouponSettlement settlement =
                    item.getCouponSettlements().get(item.getCouponSettlements().size() - 1);
            settlement.setReducePrice(settlement.getSplitPrice().subtract(item.getSplitPrice()));
            settlement.setSplitPrice(item.getSplitPrice());
        });

        // 8.按店铺分组被均摊的商品，刷新相应订单的价格信息
        Map<Long, List<TradeItem>> itemsMap = items.stream().collect(Collectors.groupingBy(TradeItem::getStoreId));
        itemsMap.keySet().forEach(storeId -> {
            // 8.1.找到店铺对应订单的价格信息
            NewPileTrade trade = trades.stream()
                    .filter(t -> t.getSupplier().getStoreId().equals(storeId)).findFirst().orElse(null);
            TradePrice tradePrice = trade.getTradePrice();

            // 8.2.计算平台优惠券优惠额(couponPrice)，并追加至订单优惠金额、优惠券优惠金额
            BigDecimal marketTotalPrice = tradePrice.getGoodsPrice().subtract(tradePrice.getDiscountsPrice());
            BigDecimal couponTotalPrice = newPileTradeItemService.calcSkusTotalPrice(itemsMap.get(storeId));
            BigDecimal couponPrice = marketTotalPrice.subtract(couponTotalPrice);
            tradePrice.setDiscountsPrice(tradePrice.getDiscountsPrice().add(couponPrice));
            tradePrice.setCouponPrice(tradePrice.getCouponPrice().add(couponPrice));

            // 8.3.重设订单总价、原始金额
            tradePrice.setTotalPrice(couponTotalPrice);
            tradePrice.setOriginPrice(tradePrice.getGoodsPrice());

            trade.getTradeItems().forEach(tradeItem -> {
                TradeItem matchItem = matchItems.stream().filter(
                        item -> item.getSkuId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
                if (Objects.nonNull(tradeItem.getDevanningId())) {
                    matchItem = matchItems.stream().filter(
                            item -> item.getDevanningId().equals(tradeItem.getDevanningId())).findFirst().orElse(null);
                }
                if (matchItem != null) {
                    tradeItem.setSplitPrice(matchItem.getSplitPrice());
                }
            });
        });

        // 9.设置订单组平台优惠券
        tradeGroup.setCommonCoupon(tradeCoupon);
        return tradeGroup;
    }


    /**
     * 运费模板重新计算，以平台运费模板为标准
     *
     * @param trades
     */
    public void computersNewPile(List<Trade> trades) {
        //分类汇总=》三方卖家走店铺模板，其他走平台模板
        List<Trade> bossFreight = new ArrayList<>(10);
        List<Trade> storeFreight = new ArrayList<>(10);
        //零售订单走特殊运费
//        List<Trade> retailFreight = new ArrayList<>(10);
        //平台
        boolean platformFlag = false;
        //统仓统配
        boolean unifiedFlag = false;
        //三方卖家
        boolean supplierFlag = false;

        for (Trade inner : trades) {
            if (!inner.getDeliverWay().equals(DeliverWay.EXPRESS)) {
                //非快递或拼团活动包邮
                inner.getTradePrice().setDeliveryPrice(BigDecimal.ZERO);
            } else {
                if (inner.getSupplier().getCompanyType().equals(CompanyType.PLATFORM)) {
                    bossFreight.add(inner);
                    platformFlag = true;
                } else if (inner.getSupplier().getCompanyType().equals(CompanyType.UNIFIED)) {
                    bossFreight.add(inner);
                    unifiedFlag = true;
                } /*else if (inner.getSupplier().getCompanyType().equals(com.wanmi.sbc.common.enums.CompanyType.RETAIL)) {
                    retailFreight.add(inner);
                }*/ else {
                    storeFreight.add(inner);
                    supplierFlag = true;
                }
            }
        }
        //存在三种店铺时，走以下计算方式否则还是走店铺运费计算
        if (platformFlag && unifiedFlag && supplierFlag) {
            //平台模板
            if (CollectionUtils.isNotEmpty(bossFreight)) {
                BigDecimal totalPrice = bossFreight.stream().map(
                        trade -> trade.getTradePrice().getGoodsPrice()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                Trade trade = bossFreight.stream().findFirst().get();
                trade.getSupplier().setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
                //1返回当前订单 全部商品  运费总金额
                BigDecimal decimal = this.calcBossTradeFreight(trade.getConsignee(), trade.getSupplier(), totalPrice);

                bossFreight.stream().forEach(trade1 -> {
                    BigDecimal goodsPrice = trade1.getTradePrice().getGoodsPrice();

                    TradePrice tradePrice = trade1.getTradePrice();
                    BigDecimal deliveryPrice = BigDecimal.ZERO;
                    //商品订单总金额
                    if (totalPrice.compareTo(BigDecimal.ZERO) == Constants.no) {
                        // decimal 订单运费不为0
                        if (decimal.compareTo(BigDecimal.ZERO) == Constants.yes) {
                            deliveryPrice = decimal.divide(new BigDecimal(bossFreight.size()), 2, BigDecimal.ROUND_HALF_UP);
                        }
                    } else {
                        deliveryPrice = goodsPrice.compareTo(BigDecimal.ZERO) ==
                                Constants.no ? BigDecimal.ZERO : decimal.compareTo(BigDecimal.ZERO) == Constants.no ? BigDecimal.ZERO :
                                goodsPrice.divide(totalPrice.divide(decimal, 4, BigDecimal.ROUND_HALF_UP),
                                        4, BigDecimal.ROUND_HALF_UP).setScale(1, BigDecimal.ROUND_HALF_UP);
                    }
                    //判断是否为秒杀抢购订单
                    if (Objects.nonNull(trade1.getIsFlashSaleGoods()) && trade1.getIsFlashSaleGoods()) {
                        //秒杀商品是否包邮
                        //获取秒杀抢购活动详情
                        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
                                .id(trade1.getTradeItems().get(0).getFlashSaleGoodsId())
                                .build())
                                .getContext().getFlashSaleGoodsVO();
                        if (flashSaleGoodsVO.getPostage().equals(1)) {
                            deliveryPrice = new BigDecimal(0);
                        }
                    }

                    tradePrice.setDeliveryPrice(deliveryPrice);
                    // 2.计算订单总价(追加运费)
                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                    // 3订单总价、原始金额追加运费
                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
                });
            }
            //商家模板
            if (CollectionUtils.isNotEmpty(storeFreight)) {
                for (Trade inner : storeFreight) {
                    TradePrice tradePrice = inner.getTradePrice();
                    // 8.4.计算运费
                    BigDecimal deliveryPrice = this.calcTradeFreight(inner.getConsignee(), inner.getSupplier(),
                            inner.getDeliverWay(),
                            tradePrice.getTotalPrice(), inner.getTradeItems(), inner.getGifts());
                    tradePrice.setDeliveryPrice(deliveryPrice);

                    //判断是否为秒杀抢购订单
                    if (Objects.nonNull(inner.getIsFlashSaleGoods()) && inner.getIsFlashSaleGoods()) {
                        //秒杀商品是否包邮
                        //获取秒杀抢购活动详情
                        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
                                .id(inner.getTradeItems().get(0).getFlashSaleGoodsId())
                                .build())
                                .getContext().getFlashSaleGoodsVO();
                        if (flashSaleGoodsVO.getPostage().equals(1)) {
                            deliveryPrice = BigDecimal.ZERO;
                            tradePrice.setDeliveryPrice(BigDecimal.ZERO);
                        }
                    }
                    // 8.5.订单总价、原始金额追加运费
                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
                }
            }
        } else {
            //商家模板
            if (CollectionUtils.isNotEmpty(storeFreight) || CollectionUtils.isNotEmpty(bossFreight)) {
                for (Trade inner : storeFreight) {
                    TradePrice tradePrice = inner.getTradePrice();
                    // 8.4.计算运费
                    BigDecimal deliveryPrice = this.calcTradeFreight(inner.getConsignee(), inner.getSupplier(),
                            inner.getDeliverWay(),
                            tradePrice.getTotalPrice(), inner.getTradeItems(), inner.getGifts());
                    tradePrice.setDeliveryPrice(deliveryPrice);
                    //判断是否为秒杀抢购订单
                    if (Objects.nonNull(inner.getIsFlashSaleGoods()) && inner.getIsFlashSaleGoods()) {
                        //秒杀商品是否包邮
                        //获取秒杀抢购活动详情
                        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
                                .id(inner.getTradeItems().get(0).getFlashSaleGoodsId())
                                .build())
                                .getContext().getFlashSaleGoodsVO();
                        if (flashSaleGoodsVO.getPostage().equals(1)) {
                            deliveryPrice = BigDecimal.ZERO;
                            tradePrice.setDeliveryPrice(BigDecimal.ZERO);
                        }
                    }

                    // 8.5.订单总价、原始金额追加运费
                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
                }
                for (Trade inner : bossFreight) {
                    TradePrice tradePrice = inner.getTradePrice();
                    // 8.4.计算运费
                    BigDecimal deliveryPrice = this.calcTradeFreight(inner.getConsignee(), inner.getSupplier(),
                            inner.getDeliverWay(),
                            tradePrice.getTotalPrice(), inner.getTradeItems(), inner.getGifts());
                    tradePrice.setDeliveryPrice(deliveryPrice);
                    //判断是否为秒杀抢购订单
                    if (Objects.nonNull(inner.getIsFlashSaleGoods()) && inner.getIsFlashSaleGoods()) {
                        //秒杀商品是否包邮
                        //获取秒杀抢购活动详情
                        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
                                .id(inner.getTradeItems().get(0).getFlashSaleGoodsId())
                                .build())
                                .getContext().getFlashSaleGoodsVO();
                        if (flashSaleGoodsVO.getPostage().equals(1)) {
                            deliveryPrice = BigDecimal.ZERO;
                            tradePrice.setDeliveryPrice(BigDecimal.ZERO);
                        }
                    }
                    // 8.5.订单总价、原始金额追加运费
                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
                }
            }
        }
    }


    /**
     * 设置订单运费,并追加到订单原价/应付金额中
     * 若商家没有单独填写订单运费,则根据订单商品,赠品按照运费模板进行计算
     *
     * @param consignee  收货地址 - 省id,市id
     * @param supplier   店铺信息 - 店铺id-使用运费模板类型
     * @param totalPrice 订单总价(扣除营销优惠后)
     * @return freight 订单应付运费
     */
    public BigDecimal calcBossTradeFreight(Consignee consignee, Supplier supplier, BigDecimal totalPrice) {
        BigDecimal freight = BigDecimal.ZERO;
        if (DefaultFlag.NO.equals(supplier.getFreightTemplateType())) {
            //1. 店铺运费模板计算
            FreightTemplateStoreVO templateStore;
            List<FreightTemplateStoreVO> storeTemplateList =
                    freightTemplateStoreQueryProvider.listByStoreIdAndDeleteFlag(
                            FreightTemplateStoreListByStoreIdAndDeleteFlagRequest.builder()
                                    .storeId(supplier.getStoreId()).deleteFlag(DeleteFlag.NO).build()
                    ).getContext().getFreightTemplateStoreVOList();
            Optional<FreightTemplateStoreVO> tempOptional = storeTemplateList.stream().filter(temp -> matchArea(
                    temp.getDestinationArea(), consignee.getProvinceId(), consignee.getCityId())).findFirst();
            if (tempOptional.isPresent()) {
                templateStore = tempOptional.get();
            } else {
                templateStore = storeTemplateList.stream().filter(temp ->
                        DefaultFlag.YES.equals(temp.getDefaultFlag())).findFirst().get();
            }
            if (DefaultFlag.NO.equals(templateStore.getFreightType())) {
                //1.2. 满金额包邮情况
                if (totalPrice.compareTo(templateStore.getSatisfyPrice()) < 0) {
                    freight = templateStore.getSatisfyFreight();
                }
            } else {
                //1.3. 固定运费情况
                freight = templateStore.getFixedFreight();
            }
        }
        return freight;
    }

    /**
     * 匹配配送地区
     *
     * @param areaStr 存储的逗号相隔的areaId(provId,cityId都有可能)
     * @param provId  收货省份id
     * @param cityId  收货城市id
     * @return 是否匹配上
     */
    public boolean matchArea(String areaStr, Long provId, Long cityId) {
        String[] arr = areaStr.split(",");
        return Arrays.stream(arr).anyMatch(area -> area.equals(String.valueOf(provId)))
                || Arrays.stream(arr).anyMatch(area -> area.equals(String.valueOf(cityId)));
    }

    /**
     * 设置订单运费,并追加到订单原价/应付金额中
     * 若商家没有单独填写订单运费,则根据订单商品,赠品按照运费模板进行计算
     *
     * @param consignee  收货地址 - 省id,市id
     * @param supplier   店铺信息 - 店铺id-使用运费模板类型
     * @param deliverWay 配送方式
     * @param totalPrice 订单总价(扣除营销优惠后)
     * @param goodsList  订单商品List - 均摊价(计算营销后),件数   ,体积,重量,使用的运费模板id
     * @param giftList   订单赠品List - 价格为0,件数   ,体积,重量,使用的运费模板id
     * @return freight 订单应付运费
     */
    public BigDecimal calcTradeFreight(Consignee consignee, Supplier supplier, DeliverWay deliverWay, BigDecimal totalPrice, List<TradeItem> goodsList, List<TradeItem> giftList) {
        BigDecimal freight = BigDecimal.ZERO;
        if (DefaultFlag.NO.equals(supplier.getFreightTemplateType())) {
            //1. 店铺运费模板计算
            FreightTemplateStoreVO templateStore;
            List<FreightTemplateStoreVO> storeTemplateList =
                    freightTemplateStoreQueryProvider.listByStoreIdAndDeleteFlag(
                            FreightTemplateStoreListByStoreIdAndDeleteFlagRequest.builder()
                                    .storeId(supplier.getStoreId()).deleteFlag(DeleteFlag.NO).build()
                    ).getContext().getFreightTemplateStoreVOList();
            //1.1. 配送地匹配运费模板(若匹配不上则使用默认运费模板)
            Optional<FreightTemplateStoreVO> tempOptional = storeTemplateList.stream().filter(temp -> matchArea(
                    temp.getDestinationArea(), consignee.getProvinceId(), consignee.getCityId())).findFirst();
            if (tempOptional.isPresent()) {
                templateStore = tempOptional.get();
            } else {
                templateStore = storeTemplateList.stream().filter(temp ->
                        DefaultFlag.YES.equals(temp.getDefaultFlag())).findFirst().get();
            }

            if (DefaultFlag.NO.equals(templateStore.getFreightType())) {
                //1.2. 满金额包邮情况
                if (totalPrice.compareTo(templateStore.getSatisfyPrice()) < 0) {
                    freight = templateStore.getSatisfyFreight();
                }
            } else {
                //1.3. 固定运费情况
                freight = templateStore.getFixedFreight();
            }
        } else if (DefaultFlag.YES.equals(supplier.getFreightTemplateType())) {
            // 2.单品运费模板计算
            // 2.1.根据templateId分组聚合总件数,重量,体积,价格, 并查询各运费模板信息
            Map<Long, TradeItem> templateGoodsMap = new LinkedHashMap<>();
            log.info("========================算取运费传来的数据" + goodsList);
            log.info("========================算取运费传来的数据赠品" + giftList);
            this.setGoodsSumMap(templateGoodsMap, goodsList);
            this.setGoodsSumMap(templateGoodsMap, giftList);
            List<Long> tempIdList = new ArrayList<>(templateGoodsMap.keySet());
//            List<FreightTemplateGoods> templateList = freightTemplateGoodsService.queryAllByIds(tempIdList);
            List<FreightTemplateGoodsVO> templateList = tradeCacheService.queryFreightTemplateGoodsListByIds(tempIdList);


            // 2.2.剔除满足指定条件包邮的运费模板(即剔除运费为0的)
            templateList = templateList.stream().filter(temp ->
                    getFreeFreightFlag(temp, templateGoodsMap, deliverWay, consignee.getProvinceId(), consignee
                            .getCityId()))
                    .collect(Collectors.toList());

            // 2.3.遍历单品运费模板List,设置匹配上收货地的配送地信息,同时计算出最大首运费的模板
            FreightTemplateGoodsExpressVO maxTemplate = new FreightTemplateGoodsExpressVO();
            for (int i = 0; i < templateList.size(); i++) {
                FreightTemplateGoodsVO temp = templateList.get(i);
                FreightTemplateGoodsExpressVO freExp = getMatchFreightTemplate(temp.getFreightTemplateGoodsExpresses(),
                        consignee.getProvinceId(), consignee.getCityId());
                temp.setExpTemplate(freExp);
                if (i == 0) {
                    maxTemplate = freExp;
                } else {
                    maxTemplate = maxTemplate.getFreightStartPrice().compareTo(freExp.getFreightStartPrice()) < 0 ?
                            freExp : maxTemplate;
                }
            }
            log.info("========================templateList" + templateList);
            log.info("========================templateGoodsMap" + templateGoodsMap);
            // 2.4.计算剩余的每个模板的运费
            final Long tempId = maxTemplate.getFreightTempId();
            freight = templateList.stream().map(temp -> getSingleTemplateFreight(temp, tempId, templateGoodsMap))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("最终算出金额" + freight);

        }
        return freight;
    }


    /**
     * 创建订单和订单组(新版本囤货)
     */
    @Transactional
    @LcnTransaction
    public List<NewPileTradeCommitResult> createBatchWithGroupNewPile(List<NewPileTrade> trades, NewPileTradeGroup tradeGroup, Operator operator, Map<String, String> pileActivityGoodsMap) {
        // 1.保存订单及订单组信息
        if (StringUtils.isEmpty(tradeGroup.getId())) {
            tradeGroup.setId(UUIDUtil.getUUID());
        }

        newPileTradeGroupService.addTradeGroup(tradeGroup);
        trades.forEach(trade -> trade.setGroupId(tradeGroup.getId()));
        List<NewPileTradeCommitResult> resultList = this.createBatchNewPile(trades, operator, pileActivityGoodsMap);
        // 2.修改优惠券状态
        List<CouponCodeBatchModifyDTO> dtoList = new ArrayList<>();
        TradeCouponVO tradeCoupon = tradeGroup.getCommonCoupon();
        dtoList.add(CouponCodeBatchModifyDTO.builder()
                .couponCodeId(tradeCoupon.getCouponCodeId())
                .orderCode(null)
                .useStatus(DefaultFlag.YES).build());
        couponCodeProvider.batchModify(CouponCodeBatchModifyRequest.builder().modifyDTOList(dtoList).build());
        return resultList;
    }


    /**
     * 余额扣减
     *
     * @param trades
     */
    private void deductionWalletBalance(List<Trade> trades) {
        if (true) return;
        boolean b = trades.stream().anyMatch(trade -> trade.getTradePrice().getBalancePrice() != null && trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) > 0);
        if (!b) {
            return;
        }
        Trade tradeTem = trades.stream().filter(trade -> trade.getBuyer() != null && trade.getBuyer().getId() != null).findFirst().orElseThrow(() -> new SbcRuntimeException("没有找到购买人信息, 请重新检查"));
        //用户余额信息
        com.wanmi.sbc.account.bean.vo.CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(tradeTem.getId()).build())
                .getContext().getCustomerWalletVO();

        // 订单总金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Trade trade : trades) {
            List<TradeItem> tradeItems = trade.getTradeItems();
            TradePrice tradePrice = trade.getTradePrice();
            Buyer buyer = trade.getBuyer();

            if (!(tradePrice.getBalancePrice() != null && tradePrice.getBalancePrice().compareTo(BigDecimal.ZERO) > 0)) {
                return;
            }

            totalPrice = totalPrice.add(tradePrice.getBalancePrice());

            AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
            request.setTradeRemark(WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId());
            //客户账号
            String customerAccount = buyer.getAccount();
            request.setCustomerAccount(customerAccount);
            request.setRelationOrderId(trade.getId());
            request.setTradeType(WalletRecordTradeType.BALANCE_PAY);
            request.setBudgetType(BudgetType.EXPENDITURE);
            request.setDealPrice(trade.getTradePrice().getTotalPrice());
            request.setRemark(WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId());
//                request.setDealTime(LocalDateTime.now());
            request.setCurrentBalance(customerWalletVO.getBalance());
            request.setTradeState(TradeStateEnum.PAID);
            request.setPayType(1);
            request.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getBalancePrice()));
            walletRecordProvider.addWalletRecord(request);
        }
        //扣除余额
        if (totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            Buyer buyer = tradeTem.getBuyer();

            WalletRequest walletRequest = WalletRequest.builder()
                    .customerId(buyer.getId())
                    .expenseAmount(totalPrice)
                    .customerAccount(buyer.getAccount()).build();
            customerWalletProvider.balancePay(walletRequest);
        }
    }


    /**
     * 批量创建订单(新版本囤货)
     *
     * @param trades   各店铺订单
     * @param operator 操作人
     * @return 订单提交结果集
     */
    @Transactional
    @LcnTransaction
    public List<NewPileTradeCommitResult> createBatchNewPile(List<NewPileTrade> trades, Operator operator, Map<String, String> pileActivityGoodsMap) {
        List<NewPileTradeCommitResult> resultList = new ArrayList<>();
        // final String parentId = generatorService.generatePoId();
        final String parentId = generatorService.generateNpPoId();

        //零售订单
        NewPileTrade retailTrade = trades.stream().filter(t -> SaleType.RETAIL.equals(t.getSaleType())).findFirst().orElse(null);
        if (Objects.nonNull(retailTrade)) {
            throw new RuntimeException("囤货不支持零售商品！");
        }

        //平台自营订单
        NewPileTrade platFormTrade = trades.stream().filter(t -> CompanyType.PLATFORM.equals(t.getSupplier().getCompanyType())).findFirst().orElse(null);
        /*
        //平台自营订单商品总数
        AtomicReference<Long> platFormGoodsTotalNum = new AtomicReference<>(0L);
        if (Objects.nonNull(platFormTrade)) {
            platFormGoodsTotalNum.set(platFormTrade.getGoodsTotalNum());
        }

        //订单总商品数
        AtomicReference<Long> tradeGoodsTotalNum = new AtomicReference<>(0L);
        tradeGoodsTotalNum.set(trades.stream().mapToLong(NewPileTrade::getGoodsTotalNum).sum());

        //第三方商家订单集合
        List<NewPileTrade> supplierTrades = trades.stream().filter(t -> com.wanmi.sbc.common.enums.CompanyType.SUPPLIER.equals(t.getSupplier().getCompanyType())).collect(Collectors.toList());


        //合并第三方商家的订单
        NewPileTrade mergeSupplierOrder = null;
        //第三方商家订单商品
        List<TradeItem> supplierItems = Lists.newArrayList();
        //第三方商家商品总件数
        AtomicReference<Long> supplierGoodsTotalNum = new AtomicReference<>(0L);
        if (CollectionUtils.isNotEmpty(supplierTrades)) {
            supplierTrades.forEach(s -> {
                //订单数据补全
                s.setParentId(parentId);
                s.setOrderType(OrderType.NORMAL_ORDER);
                supplierItems.addAll(s.getTradeItems());
                supplierGoodsTotalNum.updateAndGet(v -> v + s.getGoodsTotalNum());
            });

            mergeSupplierOrder = supplierTrades.stream().findFirst().orElse(null);
            if (Objects.isNull(mergeSupplierOrder)) {
                mergeSupplierOrder = supplierTrades.get(0);
            }
            mergeSupplierOrder.setParentId(parentId);
            mergeSupplierOrder.setOrderType(OrderType.NORMAL_ORDER);
            mergeSupplierOrder.setSaleType(SaleType.WHOLESALE);

            //第三方商家发货单
            List<TradeDeliver> supplierTradeDeliverList = Lists.newArrayList();
            //第三方商家订单营销信息
            List<TradeMarketingVO> supplierTradeMarketingVOList = Lists.newArrayList();
            //合并第三方商家订单信息
            supplierTrades.forEach(s -> {
                //商品信息合并
                if (CollectionUtils.isNotEmpty(s.getTradeDelivers())) {
                    supplierTradeDeliverList.addAll(s.getTradeDelivers());
                }
                if (CollectionUtils.isNotEmpty(s.getTradeMarketings())) {
                    supplierTradeMarketingVOList.addAll(s.getTradeMarketings());
                }
            });

            mergeSupplierOrder.setTradeItems(supplierItems);
            mergeSupplierOrder.setTradeDelivers(supplierTradeDeliverList);
            mergeSupplierOrder.setTradeMarketings(supplierTradeMarketingVOList);
            mergeSupplierOrder.setGoodsTotalNum(supplierGoodsTotalNum.get());

            *//********************第三方商家订单金额赋值开始********************//*
            List<TradePrice> supplierTradePriceList = supplierTrades.stream().map(t -> t.getTradePrice()).collect(Collectors.toList());
            mergeSupplierOrder.setTradePrice(TradePrice.builder()
                    *//**订单应付金额 需要加上鲸币金额 *//*
                    .totalPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getTotalPrice())).map(p -> p.getTotalPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**原始金额, 不作为付费金额*//*
                    .originPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getOriginPrice())).map(p -> p.getOriginPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**优惠金额*//*
                    .discountsPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDiscountsPrice())).map(p -> p.getDiscountsPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**配送费用*//*
                    .deliveryPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDeliveryPrice())).map(p -> p.getDeliveryPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**订单实际支付金额*//*
                    .totalPayCash(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getTotalPayCash())).map(p -> p.getTotalPayCash()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**商品总金额*//*
                    .goodsPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getGoodsPrice())).map(p -> p.getGoodsPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**优惠券优惠金额*//*
                    .couponPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getCouponPrice())).map(p -> p.getCouponPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**运费优惠金额*//*
                    .deliveryCouponPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDeliveryCouponPrice())).map(p -> p.getDeliveryCouponPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**包装费*//*
                    .packingPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getPackingPrice())).map(p -> p.getPackingPrice()).reduce(BigDecimal.ZERO, BigDecimal::add)).build());
            *//********************第三方商家订单金额赋值结束********************//*

        }

        //清理订单数据，合并自营、零售及第三方商家订单数据
        trades.clear();
        //批发 自营
        if (Objects.nonNull(platFormTrade)) {
            trades.add(platFormTrade);
        }
        //批发 第三方商家
        if (Objects.nonNull(mergeSupplierOrder)) {
            trades.add(mergeSupplierOrder);
        }
        */

        trades.forEach(
                trade -> {
                    //创建订单
                    try {
                        trade.setParentId(parentId);
                        trade.setOrderType(OrderType.NORMAL_ORDER);

                        //实际创建trade
                        NewPileTrade result = createNewPileTrade(trade, operator, pileActivityGoodsMap);

                        boolean hasImg = CollectionUtils.isNotEmpty(result.getTradeItems());
                        BigDecimal totalPayPrice = result.getTradeItems().stream()
                                .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                                .map(TradeItem::getSplitPrice)
                                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                        resultList.add(new NewPileTradeCommitResult(result.getId(),
                                result.getParentId(), result.getTradeState(),
                                result.getPaymentOrder(), totalPayPrice,
                                result.getOrderTimeOut(),
                                result.getSupplier().getStoreName(),
                                result.getSupplier().getIsSelf(),
                                hasImg ? result.getTradeItems().get(0).getPic() : null));
                    } catch (Exception e) {
                        log.error("commit trade error,trade={}", trade, e);
                        if (e instanceof SbcRuntimeException) {
                            throw e;
                        } else {
                            throw new SbcRuntimeException("K-020010");
                        }
                    }
                }
        );
        //获取订单组第一个订单
        NewPileTrade firstTrade = trades.stream().findFirst().get();
        // 批量修改优惠券状态
        List<CouponCodeBatchModifyDTO> dtoList = new ArrayList<>();
        trades.forEach(trade -> {
            //订单数据补全
            if (Objects.isNull(trade.getTradeState())) {
                trade.setTradeState(firstTrade.getTradeState());
            }
            if (Objects.nonNull(platFormTrade)) {
                if (Objects.isNull(trade.getTradeState())) {
                    trade.setTradeState(platFormTrade.getTradeState());
                }
                if (Objects.isNull(trade.getParentId())) {
                    trade.setParentId(platFormTrade.getParentId());
                }
            }

            if (trade.getTradeCoupon() != null) {
                TradeCouponVO tradeCoupon = trade.getTradeCoupon();
                dtoList.add(CouponCodeBatchModifyDTO.builder()
                        .couponCodeId(tradeCoupon.getCouponCodeId())
                        .orderCode(trade.getId())
                        .useStatus(DefaultFlag.YES).build());
            }

            MessageMQRequest messageMQRequest = new MessageMQRequest();
            if (!AuditState.REJECTED.equals(trade.getTradeState().getAuditState())) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
                map.put("id", trade.getId());
                if (AuditState.CHECKED.equals(trade.getTradeState().getAuditState())) {
                    messageMQRequest.setNodeCode(OrderProcessType.ORDER_COMMIT_SUCCESS.getType());
                    map.put("node", OrderProcessType.ORDER_COMMIT_SUCCESS.toValue());
                } else {
                    messageMQRequest.setNodeCode(OrderProcessType.ORDER_COMMIT_SUCCESS_CHECK.getType());
                    map.put("node", OrderProcessType.ORDER_COMMIT_SUCCESS_CHECK.toValue());
                }
                messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
                messageMQRequest.setParams(Lists.newArrayList(trade.getTradeItems().get(0).getSkuName()));
                messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
                messageMQRequest.setRouteParam(map);
                messageMQRequest.setCustomerId(trade.getBuyer().getId());
                messageMQRequest.setMobile(trade.getBuyer().getAccount());
                orderProducerService.sendMessage(messageMQRequest);
            }
        });
        modifyWalletBalance(trades, false);

        if (dtoList.size() > 0) {
            couponCodeProvider.batchModify(CouponCodeBatchModifyRequest.builder().modifyDTOList(dtoList).build());
        }

        log.info("========================================= trades =========================================, {}", JSONObject.toJSONString(trades));
        return resultList;
    }

    /**
     * 批量创建订单(新版本囤货--提货)
     *
     * @param trades   各店铺订单
     * @param operator 操作人
     * @return 订单提交结果集
     */
    @Transactional
    @LcnTransaction
    public List<TradeCommitResult> createBatchNewPilePick(List<Trade> trades, Operator operator, List<GoodsPickStock> getPileStock, Map<String, NewPileTrade> pileTradeMap) {
        List<TradeCommitResult> resultList = new ArrayList<>();
        final String parentId = generatorService.generateOPKPoId();

        //零售订单
        Trade retailTrade = trades.stream().filter(t -> SaleType.RETAIL.equals(t.getSaleType())).findFirst().orElse(null);
        if (Objects.nonNull(retailTrade)) {
            throw new RuntimeException("囤货不支持零售商品！");
        }

        //平台自营订单
        Trade platFormTrade = trades.stream().filter(t -> CompanyType.PLATFORM.equals(t.getSupplier().getCompanyType())).findFirst().orElse(null);
        //平台自营订单商品总数
        /*
        AtomicReference<Long> platFormGoodsTotalNum = new AtomicReference<>(0L);
        if (Objects.nonNull(platFormTrade)) {
            platFormGoodsTotalNum.set(platFormTrade.getGoodsTotalNum());
        }
        //订单总商品数
        AtomicReference<Long> tradeGoodsTotalNum = new AtomicReference<>(0L);
        tradeGoodsTotalNum.set(trades.stream().mapToLong(Trade::getGoodsTotalNum).sum());

        //第三方商家订单集合
        List<Trade> supplierTrades = trades.stream().filter(t -> com.wanmi.sbc.common.enums.CompanyType.SUPPLIER.equals(t.getSupplier().getCompanyType())).collect(Collectors.toList());
        //合并第三方商家的订单
        Trade mergeSupplierOrder = null;
        //第三方商家订单商品
        List<TradeItem> supplierItems = Lists.newArrayList();
        //第三方商家商品总件数
        AtomicReference<Long> supplierGoodsTotalNum = new AtomicReference<>(0L);
        if (CollectionUtils.isNotEmpty(supplierTrades)) {
            supplierTrades.forEach(s -> {
                //订单数据补全
                s.setParentId(parentId);
                s.setOrderType(OrderType.NORMAL_ORDER);
                supplierItems.addAll(s.getTradeItems());
                supplierGoodsTotalNum.updateAndGet(v -> v + s.getGoodsTotalNum());
            });
            mergeSupplierOrder = supplierTrades.stream().findFirst().orElse(null);
            if (Objects.isNull(mergeSupplierOrder)) {
                mergeSupplierOrder = supplierTrades.get(0);
            }
            mergeSupplierOrder.setParentId(parentId);
            mergeSupplierOrder.setOrderType(OrderType.NORMAL_ORDER);
            mergeSupplierOrder.setSaleType(SaleType.WHOLESALE);

            //第三方商家发货单
            List<TradeDeliver> supplierTradeDeliverList = Lists.newArrayList();
            //合并第三方商家订单信息
            supplierTrades.forEach(s -> {
                //商品信息合并
                if (CollectionUtils.isNotEmpty(s.getTradeDelivers())) {
                    supplierTradeDeliverList.addAll(s.getTradeDelivers());
                }
            });

            mergeSupplierOrder.setTradeItems(supplierItems);
            mergeSupplierOrder.setTradeDelivers(supplierTradeDeliverList);
            mergeSupplierOrder.setGoodsTotalNum(supplierGoodsTotalNum.get());

            *//********************第三方商家订单金额赋值开始********************//*
            List<TradePrice> supplierTradePriceList = supplierTrades.stream().map(t -> t.getTradePrice()).collect(Collectors.toList());
            mergeSupplierOrder.setTradePrice(TradePrice.builder()
                    *//**订单应付金额*//*
                    .totalPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDeliveryPrice())).map(p -> p.getDeliveryPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**原始金额, 不作为付费金额*//*
                    .originPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getOriginPrice())).map(p -> p.getOriginPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**优惠金额*//*
                    .discountsPrice(BigDecimal.ZERO)
                    *//**配送费用*//*
                    .deliveryPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDeliveryPrice())).map(p -> p.getDeliveryPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**订单实际支付金额*//*
                    .totalPayCash(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDeliveryPrice())).map(p -> p.getDeliveryPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**商品总金额*//*
                    .goodsPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getGoodsPrice())).map(p -> p.getGoodsPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**优惠券优惠金额*//*
                    .couponPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getCouponPrice())).map(p -> p.getCouponPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**运费优惠金额*//*
                    .deliveryCouponPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getDeliveryCouponPrice())).map(p -> p.getDeliveryCouponPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    *//**包装费*//*
                    .packingPrice(supplierTradePriceList.stream().filter(p -> Objects.nonNull(p.getPackingPrice())).map(p -> p.getPackingPrice()).reduce(BigDecimal.ZERO, BigDecimal::add))
                    .balancePrice(BigDecimal.ZERO)
                    .build());
            *//********************第三方商家订单金额赋值结束********************//*
        }

        //清理订单数据，合并自营、零售及第三方商家订单数据
        trades.clear();
        //批发 自营
        if (Objects.nonNull(platFormTrade)) {
            trades.add(platFormTrade);
        }
        //批发 第三方商家
        if (Objects.nonNull(mergeSupplierOrder)) {
            trades.add(mergeSupplierOrder);
        }
        log.info("createBatchNewPilePick-----7--->"+JSONObject.toJSONString(supplierTrades));
        log.info("createBatchNewPilePick-----8--->"+JSONObject.toJSONString(trades));
        */
        trades.forEach(
                trade -> {
                    //创建订单
                    try {
                        trade.setParentId(parentId);
                        trade.setOrderType(OrderType.NORMAL_ORDER);
                        //实际创建trade
                        Trade result = createNewPileTradePick(trade, operator, getPileStock, pileTradeMap);

                        boolean hasImg = CollectionUtils.isNotEmpty(result.getTradeItems());
                        resultList.add(new TradeCommitResult(result.getId(),
                                result.getParentId(), result.getTradeState(),
                                result.getPaymentOrder(), result.getTradePrice().getTotalPrice(),
                                result.getOrderTimeOut(),
                                result.getSupplier().getStoreName(),
                                result.getSupplier().getIsSelf(),
                                hasImg ? result.getTradeItems().get(0).getPic() : null));
                    } catch (Exception e) {
                        log.error("commit trade error,trade={}", trade, e);
                        if (e instanceof SbcRuntimeException) {
                            throw e;
                        } else {
                            throw new SbcRuntimeException("K-020010");
                        }
                    }
                }
        );
        //获取订单组第一个订单
        Trade firstTrade = trades.stream().findFirst().get();
        trades.forEach(trade -> {
            //订单数据补全
            if (Objects.isNull(trade.getTradeState())) {
                trade.setTradeState(firstTrade.getTradeState());
            }
            if (Objects.nonNull(platFormTrade)) {
                if (Objects.isNull(trade.getTradeState())) {
                    trade.setTradeState(platFormTrade.getTradeState());
                }
                if (Objects.isNull(trade.getParentId())) {
                    trade.setParentId(platFormTrade.getParentId());
                }
            }
            MessageMQRequest messageMQRequest = new MessageMQRequest();
            if (!AuditState.REJECTED.equals(trade.getTradeState().getAuditState())) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
                map.put("id", trade.getId());
                if (AuditState.CHECKED.equals(trade.getTradeState().getAuditState())) {
                    messageMQRequest.setNodeCode(OrderProcessType.ORDER_COMMIT_SUCCESS.getType());
                    map.put("node", OrderProcessType.ORDER_COMMIT_SUCCESS.toValue());
                } else {
                    messageMQRequest.setNodeCode(OrderProcessType.ORDER_COMMIT_SUCCESS_CHECK.getType());
                    map.put("node", OrderProcessType.ORDER_COMMIT_SUCCESS_CHECK.toValue());
                }
                messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
                messageMQRequest.setParams(Lists.newArrayList(trade.getTradeItems().get(0).getSkuName()));
                messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
                messageMQRequest.setRouteParam(map);
                messageMQRequest.setCustomerId(trade.getBuyer().getId());
                messageMQRequest.setMobile(trade.getBuyer().getAccount());
                orderProducerService.sendMessage(messageMQRequest);
            }
        });

        tradeService.modifyWalletBalance(trades, false);

        log.info("========================================= trades =========================================, {}", JSONObject.toJSONString(trades));

        if (kingdeeOpenState) {

            trades.stream().forEach(trade -> {
                logger.info("TradeService.createBatch tradeId:{}", trade.getId());
                if (Objects.nonNull(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
                    if (StringUtils.isEmpty(trade.getLogistics()) || trade.getLogistics().equals("01")) {
                        log.info("TradeService.createBatch logistics:{}", trade.getId());
                        orderProducerService.kingdeePushOrder(trade.getId(), 10 * 1000L);
                    }
                } else {
                    log.info("TradeService.createBatch shoppingCart tid:{}", trade.getId());
                    orderProducerService.shoppingCartKingdeePushOrder(trade.getId(), 10 * 1000L);
                }
            });
        }
        return resultList;
    }


    /**
     * 创建订单-入库
     *
     * @param trade
     * @param operator
     */
    @Transactional
    public NewPileTrade createNewPileTrade(NewPileTrade trade, Operator operator, Map<String, String> pileActivityGoodsMap) {
        if (!orderkingdeeCheckNewPileTrade(trade)) {
            throw new SbcRuntimeException("K-020010");
        }

        //保存囤货库存  同时扣减虚拟库存
        List<GoodsPickStock> goodsPickStockList = new ArrayList<>();
        List<PileActivityStockRequest> pileActivityStock = new ArrayList<>();
        trade.getTradeItems().forEach(item -> {
            LocalDateTime now = LocalDateTime.now();
            goodsPickStockList.add(GoodsPickStock.builder()
                    .createTime(now)
                    .updateTime(now)
                    .goodsId(item.getSpuId())
                    .goodsInfoId(item.getSkuId())
                    .newPileTradeNo(trade.getId())
                    .stock(item.getNum())
                    .state(1)
                    .goodsInfoNo(item.getSkuNo())
                    .wareId(item.getWareId())
                    .build());
            pileActivityStock.add(PileActivityStockRequest.builder()
                    .activityId(pileActivityGoodsMap.get(item.getSkuId()))
                    .addOrSub(true)
                    .goodsInfoId(item.getSkuId())
                    .num(item.getNum().intValue())
                    .build());
        });
        goodsPickStockRepository.saveAll(goodsPickStockList);
        //扣减虚拟库存
        pileActivityProvider.updateVirtualStock(pileActivityStock);
        //保存商品价格(分摊)
        inventoryDetailSamountProvider.saveGoodsShareMoney(
                InventoryDetailSamountRequest.builder().tradeItemVOS(KsBeanUtil.convert(trade.getTradeItems(), TradeItemVO.class))
                        .oid(trade.getId()).build());

        // 3.初始化订单提交状态
        NewPileFlowState flowState;
        AuditState auditState;
        //是否开启订单审核（同时判断是否为秒杀抢购商品订单）
        Boolean orderAuditSwitch = false;
        flowState = NewPileFlowState.INIT;

        //线下支付跳过init
//        if(PayType.OFFLINE.name().equals(trade.getPayInfo().getPayTypeName())){
//            flowState = NewPileFlowState.AUDIT;
//        }

        auditState = AuditState.CHECKED;
        trade.setTradeState(NewPileTradeState
                .builder()
                .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                .flowState(flowState)
                .payState(PayState.NOT_PAID)
                .createTime(LocalDateTime.now())
                .build());

        trade.getTradeState().setAuditState(auditState);
        trade.setIsAuditOpen(orderAuditSwitch);
        trade.setPileActiviteId(pileActivityGoodsMap.get(trade.getTradeItems().get(0).getSkuId()));

        // 4.若订单审核关闭了,直接创建订单开票跟支付单
        createPayOrderNewPileTrade(trade, operator, orderAuditSwitch);

        // 查询订单支付顺序设置
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_PAYMENT_ORDER);
        Integer paymentOrder = tradeCacheService.getTradeConfigByType(ConfigType.ORDER_SETTING_PAYMENT_ORDER).getStatus();
        trade.setPaymentOrder(PaymentOrder.values()[paymentOrder]);
        // 先款后货且已审核订单（审核开关关闭）
        Boolean needTimeOut = Objects.equals(auditState, AuditState.CHECKED) &&
                trade.getPaymentOrder() == PaymentOrder.PAY_FIRST;

        if (needTimeOut) {
            // 先货后款情况下，查询订单是否开启订单失效时间设置
            request.setConfigType(ConfigType.ORDER_SETTING_TIMEOUT_CANCEL);
            ConfigVO timeoutCancelConfig = tradeCacheService.getTradeConfigByType(ConfigType.ORDER_SETTING_TIMEOUT_CANCEL);
            Integer timeoutSwitch = timeoutCancelConfig.getStatus();
            if (timeoutSwitch == 1) {
                // 查询设置中订单超时时间
                Integer min = Integer.valueOf(JSON.parseObject(timeoutCancelConfig.getContext()).get("hour").toString());
                // 发送非拼团单取消订单延迟队列;
                if (Objects.nonNull(trade.getGrouponFlag()) && !trade.getGrouponFlag()) {
                    // 线下支付默认一个小时后取消
                    if (PayType.OFFLINE.name().equals(trade.getPayInfo().getPayTypeName())) {
                        min = 60;
                    }
                    trade.setOrderTimeOut(LocalDateTime.now().plusMinutes(min));
                    log.info("orderProducerService.cancelOrderPileNew(trade.getId(), 2 * 60 * 1000L);" + trade.getId());
                    orderProducerService.cancelOrderPileNew(trade.getId(), min * 60 * 1000L);
                }
            }
        }

        trade.appendTradeEventLog(new TradeEventLog(operator, "创建囤货订单", "创建囤货订单", LocalDateTime.now()));
        trade.setWMSPushFlag(true);

        logger.info("TradeService.create push wms Trade:{}", JSONObject.toJSONString(trade));
        // 6.推送订单(销售订单推wms)
        if (kingdeeOpenState) {

        }
        //如果订单为普通订单，线下支付需要审核
        if (TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType()) && trade.getPayInfo().getPayTypeId().equals("1")) {
            auditState = AuditState.NON_CHECKED;
        }

        if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            trade.setVillageFlag(true);
        } else {
            trade.setVillageFlag(false);
        }
        if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            trade.setNewVilageFlag(true);
        } else {
            trade.setNewVilageFlag(false);
        }
        log.info("===========data查看" + trade.getDeliverWay());
        log.info("===========data查看" + trade.getNewVilageFlag());
        // 5.订单入库
        addTrade(trade);

        // 记录限购数据
        this.recordPurchuseLimit(trade);


        if (Platform.SUPPLIER.equals(operator.getPlatform())) {
            this.operationLogMq.convertAndSend(operator, "代客下单", "订单号" + trade.getId());
        }
        return trade;
    }

    /**
     * 记录限购记录
     * @param trade
     */
    private void recordPurchuseLimit(NewPileTrade trade) {
        //用户区域限购表
        List<CustomerAreaLimitDetailVO> list = new ArrayList<>();
        //购买商品
        List<TradeItem> tradeItems = trade.getTradeItems();
        List<Long> devaningIds = tradeItems.stream().map(TradeItem::getDevanningId).collect(Collectors.toList());

        tradeItems.forEach(v -> {
            v.setBNum(v.getDivisorFlag().multiply(BigDecimal.valueOf(v.getNum())));
        });
        //得到goodsinfo:num的数据 因为有拆箱的存在
        Map<String, BigDecimal> skunummap = tradeItems.stream()
                .collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getBNum, BigDecimal::add));
        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
        goodsInfoRequest.setDevanningIds(devaningIds);
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        goodsInfoRequest.setWareId(trade.getWareId());
        DevanningGoodsInfoListResponse context = devanningGoodsInfoQueryProvider.listViewByIdsByMatchFlag(goodsInfoRequest).getContext();
        context.getDevanningGoodsInfoVOS().forEach(pa -> {
            if (StringUtils.isNotBlank(pa.getSingleOrderAssignArea()) && Objects.nonNull(trade.getConsignee().getProvinceId())
                    && Objects.nonNull(trade.getConsignee().getCityId())) {
                List<Long> singleOrderAssignAreaList = Arrays.stream(pa.getSingleOrderAssignArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(singleOrderAssignAreaList)
                        && (singleOrderAssignAreaList.contains(trade.getConsignee().getProvinceId()) || singleOrderAssignAreaList.contains(trade.getConsignee().getCityId()))) {
                    CustomerAreaLimitDetailVO parem = new CustomerAreaLimitDetailVO();
                    List<String> collect1 = list.stream().map(CustomerAreaLimitDetailVO::getGoodsInfoId).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(list) || !collect1.contains(pa.getGoodsInfoId())) {
                        parem.setGoodsInfoId(pa.getGoodsInfoId()).setCreateTime(LocalDateTime.now()).setTradeId(trade.getId())
                                .setCustomerId(trade.getBuyer().getId()).setRegionId(trade.getConsignee().getCityId().intValue())
                                .setNum(skunummap.get(pa.getGoodsInfoId()));
                    }
                    list.add(parem);
                }
            }
        });
        if (CollectionUtils.isNotEmpty(list)) {
            customerAreaLimitDetailProvider.addByOrder(CustomerAreaLimitDetailAddRequest.builder().list(list).build());
        }

        //营销限购表增加记录
        List<TradeMarketingVO> tradeMarketings = trade.getTradeMarketings();
        if (CollectionUtils.isNotEmpty(tradeMarketings)) {
            List<MarketingPurchaseLimitVO> requstparam = new LinkedList<>();
            //获取营销对应的商品哪些有限购
            MarketingEffectiveRespose context1 = marketingScopeQueryProvider.getMarketingScopeLimitPurchase
                    (MarketingEffectiveRequest.builder().tradeMarketingList(KsBeanUtil.convert(tradeMarketings, TradeMarketingDTO.class)).build()).getContext();
            if (CollectionUtils.isNotEmpty(context1.getTradeMarketingList())) {
                context1.getTradeMarketingList().forEach(v -> {
                    v.getSkuIds().forEach(vb -> {
                        MarketingPurchaseLimitVO marketingPurchaseLimitVO = new MarketingPurchaseLimitVO();
                        marketingPurchaseLimitVO.setMarketingId(v.getMarketingId());
                        marketingPurchaseLimitVO.setCreateTime(LocalDateTime.now());
                        marketingPurchaseLimitVO.setGoodsInfoId(vb);
                        marketingPurchaseLimitVO.setTradeId(trade.getId());
                        marketingPurchaseLimitVO.setCustomerId(trade.getBuyer().getId());
                        marketingPurchaseLimitVO.setNum(skunummap.get(vb));
                        requstparam.add(marketingPurchaseLimitVO);
                    });

                });
            }
            if (CollectionUtils.isNotEmpty(requstparam)) {
                marketingPurchaseLimitProvider.add(requstparam);
                this.terminatedMarketingWhenReachPurchuseLimit(requstparam);
            }
        }
    }

    /**
     * 宕商品购买数量达到活动总限购量时，终止商品参与该活动
     * @param requstparam
     */
    private void terminatedMarketingWhenReachPurchuseLimit(List<MarketingPurchaseLimitVO> requstparam) {
        // TODO log
        log.info("PileTradeService_marketingPurchaseLimitProvider:{}", JSON.toJSONString(requstparam));
        // 判断是否超出了营销的总限购数量，超出总限量，终止该商品参与营销活动
        for(MarketingPurchaseLimitVO marketingPurchaseLimitVO : requstparam){
            // TODO log
            log.info("PileTradeService_marketingPurchaseLimitProvider_marketingPurchaseLimitVO:{}",JSON.toJSONString(marketingPurchaseLimitVO));
            MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest = new MarketingScopeByMarketingIdRequest();
            marketingScopeByMarketingIdRequest.setMarketingId(marketingPurchaseLimitVO.getMarketingId());
            marketingScopeByMarketingIdRequest.setSkuId(marketingPurchaseLimitVO.getGoodsInfoId());
            BaseResponse<MarketingScopeByMarketingIdResponse> marketingScopeByMarketingIdResponseBaseResponse = marketingScopeQueryProvider.listByMarketingIdAndSkuIdAndCache(marketingScopeByMarketingIdRequest);

            List<MarketingScopeVO> marketingScopeVOList = Optional.ofNullable(marketingScopeByMarketingIdResponseBaseResponse).map(BaseResponse::getContext)
                    .map(MarketingScopeByMarketingIdResponse::getMarketingScopeVOList)
                    .orElse(Lists.newArrayList());
            if(CollectionUtils.isEmpty(marketingScopeVOList)){
                continue;
            }
            MarketingScopeVO marketingScopeVO = marketingScopeVOList.stream().findFirst().get();

            Long purchaseNum = marketingScopeVO.getPurchaseNum(); // 营销总限购量
            if(Objects.isNull(purchaseNum)){
                // 未设置营销活动总限购量，跳过
                continue;
            }
            // TODO log
            log.info("PileTradeService_marketingPurchaseLimitProvider_1:{},{}",marketingScopeVO.getMarketingId(), marketingScopeVO.getPurchaseNum());
            Map<String,Object> req = new LinkedHashMap<>();
            req.put("marketingId",marketingPurchaseLimitVO.getMarketingId());
            req.put("goodsInfoId", marketingPurchaseLimitVO.getGoodsInfoId());
            List<MarketingPurchaseLimitVO> purchaseLimits = marketingPurchaseLimitProvider.queryListByParmNoUser(req).getContext();
            log.info("PileTradeService_marketingPurchaseLimitProvider_2:{}", JSON.toJSONString(purchaseLimits));
            AtomicReference<BigDecimal> marketingNum = new AtomicReference<>(BigDecimal.ZERO); // 已占用的限购物数量（总限购）

            if(CollectionUtils.isNotEmpty(purchaseLimits)){
                List<String> tradeIds = purchaseLimits.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
                //获取生效订单
                List<Trade> trades = tradeService.getListByIdsSimplify(tradeIds);
                List<String> validTradeIds = trades.stream().map(Trade::getId).collect(Collectors.toList());
                log.info("PileTradeService_marketingPurchaseLimitProvider_3:{}", JSON.toJSONString(validTradeIds));
                purchaseLimits.forEach(q->{
                    if (validTradeIds.contains(q.getTradeId())){
                        marketingNum.set(marketingNum.get().add(q.getNum()));
                    }
                });
            }
            log.info("PileTradeService_marketingPurchaseLimitProvider_3:{},{}",purchaseNum, marketingNum.get().longValue());
            if(purchaseNum <= marketingNum.get().longValue()){
                // 终止该商品参与该营销活动
                TerminationMarketingScopeRequest terminationMarketingScopeRequest = TerminationMarketingScopeRequest.builder()
                        .marketingId(marketingPurchaseLimitVO.getMarketingId())
                        .scopeId(marketingPurchaseLimitVO.getGoodsInfoId())
                        .build();
                marketingScopeProvider.terminationByMarketingIdAndScopeId(terminationMarketingScopeRequest);
            }
        }
    }


    /**
     * 处理囤货已支付
     * @param trade
     */
    private void disposePaidPrice(Trade trade){
        List<InventoryDetailSamountVO> inventoryDetailSamount = new ArrayList<>();
        trade.getTradeItems().forEach(var->{
            inventoryDetailSamount.addAll(var.getInventoryDetailSamount());
        });
        if(CollectionUtils.isEmpty(inventoryDetailSamount)){
            return;
        }
        Map<String, BigDecimal> collect = inventoryDetailSamount.stream()
                .collect(Collectors.groupingBy(InventoryDetailSamountVO::getGoodsInfoId, Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
        trade.getTradeItems().forEach(var->{
            var.setPaidGoodsPrice(collect.get(var.getSkuId()));
        });

        BigDecimal reduce = inventoryDetailSamount.stream()
                .filter(p -> Objects.nonNull(p.getAmortizedExpenses()))
                .map(p -> p.getAmortizedExpenses()).reduce(BigDecimal.ZERO, BigDecimal::add);

        /**已支付金额**/
        trade.getTradePrice().setPaidPrice(reduce);
    }

    /**
     * 创建订单-入库
     *
     * @param trade
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public Trade createNewPileTradePick(Trade trade, Operator operator, List<GoodsPickStock> getPileStock, Map<String, NewPileTrade> pileTradeMap) {
        //处理囤货已支付金额
        disposePaidPrice(trade);

        log.info("createBatchNewPilePick-----9--->"+JSONObject.toJSONString(trade));
        if (!orderkingdeeCheck(trade)) {
            throw new SbcRuntimeException("K-020010");
        }
        //扣减实际库存
        this.stockupdate(trade, false);

        //囤货单更新提货数量----------start
        updateNewPileTradeItem(trade, getPileStock, pileTradeMap);
        //囤货单增加提货数量----------end

        // 3.初始化订单提交状态
        FlowState flowState;
        AuditState auditState;
        //是否开启订单审核（同时判断是否为秒杀抢购商品订单）
        Boolean orderAuditSwitch = tradeCacheService.isSupplierOrderAudit();

        //如果是秒杀抢购商品不需要审核
        if (Objects.nonNull(trade.getIsFlashSaleGoods()) && trade.getIsFlashSaleGoods()) {
            flowState = FlowState.AUDIT;
            auditState = AuditState.CHECKED;
            orderAuditSwitch = Boolean.FALSE;
        }
        if (!orderAuditSwitch) {
            flowState = FlowState.AUDIT;
            auditState = AuditState.CHECKED;
        } else {
            //商家 boss 初始化状态是不需要审核的
            if (operator.getPlatform() == Platform.BOSS || operator.getPlatform() == Platform.SUPPLIER) {
                flowState = FlowState.AUDIT;
                auditState = AuditState.CHECKED;
            } else {
                flowState = FlowState.INIT;
                auditState = AuditState.NON_CHECKED;
            }
        }

        //如果是物流类型且没有物流信息直接进入待审核状态
        if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay())) {
            //由超级大白鲸代选需要审核 其他不需要
            if (Objects.isNull(trade.getLogisticsCompanyInfo()) || StringUtils.isEmpty(trade.getLogisticsCompanyInfo().getLogisticsCompanyName())) {
                flowState = FlowState.INIT;
                auditState = AuditState.NON_CHECKED;
                trade.setLogistics("02");
                logger.error("==订单=={}==物流公司信息未填写 ", trade.getId());
            } else {
                trade.setLogistics("01");
            }
        }

        trade.setTradeState(TradeState
                .builder()
                .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                .flowState(flowState)
                .payState(PayState.NOT_PAID)
                .createTime(LocalDateTime.now())
                .build());
        // 4.若订单审核关闭了,直接创建订单开票跟支付单
        createPayOrder(trade, operator, orderAuditSwitch);

        trade.getTradeState().setAuditState(auditState);
        trade.setIsAuditOpen(orderAuditSwitch);


        // 查询订单支付顺序设置
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_PAYMENT_ORDER);
        Integer paymentOrder = tradeCacheService.getTradeConfigByType(ConfigType.ORDER_SETTING_PAYMENT_ORDER).getStatus();
        trade.setPaymentOrder(PaymentOrder.values()[paymentOrder]);
        // 先款后货且已审核订单（审核开关关闭）
        Boolean needTimeOut = Objects.equals(auditState, AuditState.CHECKED) &&
                trade.getPaymentOrder() == PaymentOrder.PAY_FIRST;

        if (needTimeOut) {
            // 先货后款情况下，查询订单是否开启订单失效时间设置
            request.setConfigType(ConfigType.ORDER_SETTING_TIMEOUT_CANCEL);
            ConfigVO timeoutCancelConfig = tradeCacheService.getTradeConfigByType(ConfigType.ORDER_SETTING_TIMEOUT_CANCEL);
            Integer timeoutSwitch = timeoutCancelConfig.getStatus();
            if (timeoutSwitch == 1) {
                // 查询设置中订单超时时间
                Integer min =
                        Integer.valueOf(JSON.parseObject(timeoutCancelConfig.getContext()).get("hour").toString());
                // 发送非拼团单取消订单延迟队列;
                if (Objects.nonNull(trade.getGrouponFlag()) && !trade.getGrouponFlag()) {
                    // 线下支付默认一个小时后取消 -> 囤货提货订单 一个小时后取消
                    if (isPayByOfflineOrIsOPK(trade)) {
                        min = 60;
                    }
                    trade.setOrderTimeOut(LocalDateTime.now().plusMinutes(min));
                    orderProducerService.cancelOrder(trade.getId(), min * 60 * 1000L);
                }
            }
        }
        trade.appendTradeEventLog(new TradeEventLog(operator, "创建新提货订单", "创建新提货订单", LocalDateTime.now()));
        trade.setWMSPushFlag(true);

        // 6.推送订单
        // 6.推送订单(销售订单推wms)
        if (kingdeeOpenState) {
            log.info("TradeService.create Open the push wms kingdeeOpenState:{}", kingdeeOpenState);

//            if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            if (false) {
                //加入缓存表中
                logger.info("TradeService.create cache orderId:{}", trade.getId());
                TradeCachePushKingdeeOrder cachePushKingdeeOrder = TradeCachePushKingdeeOrder.builder()
                        .orderCode(trade.getId())
                        .parentId(trade.getParentId())
                        .orderStatus(0)
                        .pushStatus(0)
                        .erroNum(0)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                tradeCachePushKingdeeOrderRepository.save(cachePushKingdeeOrder);
                //加入历史乡镇件表
                String tid = trade.getId();
                Long wareId = trade.getWareId();
                List<HistoryTownShipOrder> list = trade.getTradeItems().stream().map(v -> HistoryTownShipOrder.builder()
                        .cateId(v.getCateId())
                        .brand(v.getBrand())
                        .cateName(v.getCateName())
                        .erpNo(v.getErpSkuNo())
                        .num(BigDecimal.valueOf(v.getNum()))
                        .devanningId(v.getDevanningId())
                        .divisorFlag(v.getDivisorFlag())
                        .skuId(v.getSkuId())
                        .skuName(v.getSkuName())
                        .spuId(v.getSpuId())
                        .spuName(v.getSpuName())
                        .storeId(String.valueOf(v.getStoreId()))
                        .supplierCode(v.getSupplierCode())
                        .skuNo(v.getSkuNo())
                        .erpNo(v.getErpSkuNo())
                        .tid(tid)
                        .wareId(wareId).build()).collect(Collectors.toList());

                List<HistoryTownShipOrder> list2 = trade.getGifts().stream().map(v -> HistoryTownShipOrder.builder()
                        .cateId(v.getCateId())
                        .brand(v.getBrand())
                        .cateName(v.getCateName())
                        .erpNo(v.getErpSkuNo())
                        .num(BigDecimal.valueOf(v.getNum()))
                        .devanningId(v.getDevanningId())
                        .divisorFlag(v.getDivisorFlag())
                        .skuId(v.getSkuId())
                        .skuName(v.getSkuName())
                        .spuId(v.getSpuId())
                        .spuName(v.getSpuName())
                        .storeId(String.valueOf(v.getStoreId()))
                        .supplierCode(v.getSupplierCode())
                        .skuNo(v.getSkuNo())
                        .erpNo(v.getErpSkuNo())
                        .giftFlag(1)
                        .tid(tid)
                        .wareId(wareId).build()).collect(Collectors.toList());
                list.addAll(list2);
                log.info("listAll" + list);
                historyTownShipOrderRepository.saveAll(list);
            }
            if (wmsAPIFlag && AuditState.CHECKED.equals(auditState)) {
                //拼团在订单那提交的时候不推送，在成团时推送
                if (Objects.isNull(trade.getGrouponFlag()) || !trade.getGrouponFlag()) {
                    try {
                        Trade pushWmsTrade = KsBeanUtil.convert(trade, Trade.class);
                        log.info("TradeService.create shoppingCart:{}", trade.getId());
                        pushWMSOrder(pushWmsTrade, false, false);
                    } catch (Exception e) {
                        log.info("=====订单推送报错日志：" + e + ";  订单编号:" + trade.getId());
                        e.printStackTrace();
                    }
                }
            }
        }

        //如果订单为普通订单，线下支付需要审核
        if (TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType()) && trade.getPayInfo().getPayTypeId().equals("1")) {
            auditState = AuditState.NON_CHECKED;
        }

        if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            trade.setVillageFlag(true);
        } else {
            trade.setVillageFlag(false);
        }
        if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            trade.setNewVilageFlag(true);
        } else {
            trade.setNewVilageFlag(false);
        }
        log.info("===========data查看" + trade.getDeliverWay());
        log.info("===========data查看" + trade.getNewVilageFlag());
        // 5.订单入库

        if (trade.getTradePrice().getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            //TODO 测试打印日志上线删除
            log.info("0元提货单直接推送wms" + trade);
            //为0 走回调逻辑
            orderProducerService.pushOrderPayWMS(trade.getId());
            trade.setWMSPushFlag(true);
        }

        tradeService.addTrade(trade);


        //记录物流信息
        if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo()) && StringUtils.isNotEmpty(trade.getLogisticsCompanyInfo().getId())) {
            boolean insertFlag = (Objects.nonNull(trade.getLogisticsCompanyInfo())
                    && (Objects.nonNull(trade.getLogisticsCompanyInfo().getInsertFlag())
                    && trade.getLogisticsCompanyInfo().getInsertFlag() == 1));
            HistoryLogisticCompanyRequest company = new HistoryLogisticCompanyRequest();
            company.setCreateTime(LocalDateTime.now());
            company.setLogisticsName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
            company.setLogisticsPhone(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
            company.setOrderId(trade.getId());
            company.setReceivingSite(StringUtils.isNotBlank(trade.getLogisticsCompanyInfo().getReceivingPoint())
                    ? trade.getLogisticsCompanyInfo().getReceivingPoint() : null);
            company.setCustomerId(trade.getBuyer().getId());
            if (insertFlag) {
                company.setSelFlag(1);
            } else {
                company.setSelFlag(0);
                company.setCompanyId(Long.valueOf(trade.getLogisticsCompanyInfo().getId()));
                company.setLogisticsAddress(trade.getLogisticsCompanyInfo().getLogisticsAddress());
            }
            company.setLogisticsType(DeliverWay.LOGISTICS.equals(trade.getDeliverWay())? LogisticsType.THIRD_PARTY_LOGISTICS.toValue():LogisticsType.SPECIFY_LOGISTICS.toValue());
            company.setMarketId(trade.getSupplier().getMarketId());
            orderProducerService.insertCompany(company);
        }

        if (Platform.SUPPLIER.equals(operator.getPlatform())) {
            this.operationLogMq.convertAndSend(operator, "代客下单", "订单号" + trade.getId());
        }
        return trade;
    }

    public List<NewPileTrade> getAllDetailByCustomerId(String customerId) {
        NewPileTradeQueryRequest tradeQueryRequest = new NewPileTradeQueryRequest();
        tradeQueryRequest.setPageNum(0);
        tradeQueryRequest.setPageSize(1000);
        tradeQueryRequest.setBuyerId(customerId);
        tradeQueryRequest.setFlowStates(Arrays.asList(NewPileFlowState.PICK_PART, NewPileFlowState.PILE));
        Criteria criteria = tradeQueryRequest.getWhereCriteria();
        Page<NewPileTrade> page = page(criteria, tradeQueryRequest);
        return page.getContent();
    }

    public List<NewPileTrade> getAllDetailByCustomerIdAndWareId(String customerId,Long wareId) {
        NewPileTradeQueryRequest tradeQueryRequest = new NewPileTradeQueryRequest();
        tradeQueryRequest.setPageNum(0);
        tradeQueryRequest.setPageSize(1000);
        tradeQueryRequest.setBuyerId(customerId);
        tradeQueryRequest.setWareId(wareId);
        tradeQueryRequest.setFlowStates(Arrays.asList(NewPileFlowState.PICK_PART, NewPileFlowState.PILE));
        Criteria criteria = tradeQueryRequest.getWhereCriteria();
        Page<NewPileTrade> page = page(criteria, tradeQueryRequest);
        return page.getContent();
    }

    /**
     * 扣减囤货单可提货商品数量
     *
     * @param trade
     */
    @Transactional
    public synchronized void updateNewPileTradeItem(Trade trade, List<GoodsPickStock> getPileStock, Map<String, NewPileTrade> pileTradeMap) {

        List<InventoryDetailSamountVO> inventoryDetailSamountTrades = new ArrayList<>();
        trade.getTradeItems().forEach(item -> {

            inventoryDetailSamountTrades.addAll(item.getInventoryDetailSamount());
            List<PickGoodsDTO> pickGoodsList = item.getPickGoodsList();
            if(CollectionUtils.isEmpty(pickGoodsList)){
                throw new RuntimeException("错误订单！");
            }
            pickGoodsList.forEach(var ->{
                //扣减囤货单库存
                goodsPickStockRepository.subStockByGoodsInfoIdNewPileTradeNo(var.getNum(), var.getGoodsInfoId(), var.getNewPileOrderNo());
            });

            //根据囤货单号分组
            Map<String, List<GoodsPickStock>> goodsPickStock = getPileStock.stream().collect(
                    Collectors.groupingBy(GoodsPickStock::getNewPileTradeNo));
            //当前提货单提货商品根据囤货单号分组
            Map<String, List<PickGoodsDTO>> pickGoodsMap = pickGoodsList.stream().collect(
                    Collectors.groupingBy(PickGoodsDTO::getNewPileOrderNo));
            //校验是否已经提完  变更囤货单状态
            for (Map.Entry<String, List<GoodsPickStock>> pickStockList:goodsPickStock.entrySet()){
                List<Boolean> falg = new ArrayList<>();
                log.info("pickStockList:goodsPickStock.entrySet()---->"+JSONObject.toJSONString(pickStockList.getValue()));
                pickStockList.getValue().forEach(pickStock->{
                    List<PickGoodsDTO> pickGoodsDTOS = pickGoodsMap.get(pickStockList.getKey());
                    log.info("pickGoodsDTOS---->"+JSONObject.toJSONString(pickGoodsDTOS));
                    if(CollectionUtils.isNotEmpty(pickGoodsDTOS)){
                        pickGoodsDTOS.forEach(var->{
                            if(pickStock.getGoodsInfoId().equals(var.getGoodsInfoId())){
                                if(pickStock.getStock() - var.getNum() <= 0){
                                    falg.add(true);
                                }else{
                                    falg.add(false);
                                }
                            }
                        });
                    }
                });
                log.info("pickStockList.getValue()1---->"+JSONObject.toJSONString(pickStockList.getValue().size()));
                log.info("pickStockList.getValue()2---->"+JSONObject.toJSONString(falg.size()));
                log.info("pickStockList.getValue()3---->"+JSONObject.toJSONString(falg));
                //如果囤货单不包含false 则意味着该囤货单全部提完
                if(CollectionUtils.isNotEmpty(falg)){
                    NewPileTrade detail = detail(pickStockList.getKey());
                    //囤货商品库存已经提完
                    List<GoodsPickStock> byNewPileTradeNos = goodsPickStockRepository.findByNewPileTradeNos(Arrays.asList(detail.getId()));
                    if(!falg.contains(false) && CollectionUtils.isEmpty(byNewPileTradeNos)){
                        detail.getTradeState().setFlowState(NewPileFlowState.COMPLETED);
                        updateTrade(detail);
                    }else{
                        detail.getTradeState().setFlowState(NewPileFlowState.PICK_PART);
                        updateTrade(detail);
                    }
                }
            }
        });

        //使用囤货单金额
        inventoryDetailSamountProvider.subInventoryDetailSamountFlag(SubInventoryDetailSamountRequest.builder()
                .inventoryDetailSamountVOList(inventoryDetailSamountTrades)
                .takeId(trade.getId())
                .build());
    }

   /* *//**
     * 扣减囤货单可提货商品数量
     *
     * @param trade
     *//*
    @Transactional
    public synchronized void updateNewPileTradeItem(Trade trade, List<GoodsPickStock> getPileStock, Map<String, NewPileTrade> pileTradeMap) {

        trade.getTradeItems().forEach(item -> {
            List<PickGoodsDTO> pickGoodsList = new ArrayList<>();

            Long temp = 0l;
            for (GoodsPickStock pickStock : getPileStock) {
                if (item.getSkuId().equals(pickStock.getGoodsInfoId())) {
                    PickGoodsDTO pickGoodsDTO = new PickGoodsDTO();
                    pickGoodsDTO.setNewPileOrderNo(pickStock.getNewPileTradeNo());
                    pickGoodsDTO.setGoodsInfoId(pickStock.getGoodsInfoId());

                    temp += pickStock.getStock();
                    if (item.getNum() >= temp) {
                        pickGoodsDTO.setNum(pickStock.getStock());
                    } else {
                        Long stock = pickStock.getStock() - (temp - item.getNum());
                        pickGoodsDTO.setNum(stock);
                    }
                    pickGoodsList.add(pickGoodsDTO);
                    //使用囤货单金额
                    inventoryDetailSamountProvider.updateInventoryDetailSamountFlag(InventoryDetailSamountRequest.builder()
                            .oid(pickGoodsDTO.getNewPileOrderNo())
                            .num(pickGoodsDTO.getNum().intValue())
                            .goodsInfoId(pickGoodsDTO.getGoodsInfoId())
                            .takeId(trade.getId())
                            .build());
                    //扣减囤货单库存
                    goodsPickStockRepository.subStockByGoodsInfoIdNewPileTradeNo(pickGoodsDTO.getNum(), pickStock.getGoodsInfoId(), pickStock.getNewPileTradeNo());
                    if (item.getNum() < temp) {
                        break;
                    }
                }
            }
            log.info("updateNewPileTradeItem----------->setPickGoodsList" + JSONObject.toJSONString(pickGoodsList));
            item.setPickGoodsList(pickGoodsList);

            //根据囤货单号分组
            Map<String, List<GoodsPickStock>> goodsPickStock = getPileStock.stream().collect(
                    Collectors.groupingBy(GoodsPickStock::getNewPileTradeNo));
            //当前提货单提货商品根据囤货单号分组
            Map<String, List<PickGoodsDTO>> pickGoodsMap = pickGoodsList.stream().collect(
                    Collectors.groupingBy(PickGoodsDTO::getNewPileOrderNo));
            //校验是否已经提完  变更囤货单状态
            for (Map.Entry<String, List<GoodsPickStock>> pickStockList:goodsPickStock.entrySet()){
                List<Boolean> falg = new ArrayList<>();
                log.info("pickStockList:goodsPickStock.entrySet()---->"+JSONObject.toJSONString(pickStockList.getValue()));
                pickStockList.getValue().forEach(pickStock->{
                    List<PickGoodsDTO> pickGoodsDTOS = pickGoodsMap.get(pickStockList.getKey());
                    log.info("pickGoodsDTOS---->"+JSONObject.toJSONString(pickGoodsDTOS));
                    if(CollectionUtils.isNotEmpty(pickGoodsDTOS)){
                        pickGoodsDTOS.forEach(var->{
                            if(pickStock.getGoodsInfoId().equals(var.getGoodsInfoId())){
                                if(pickStock.getStock() - var.getNum() <= 0){
                                    falg.add(true);
                                }else{
                                    falg.add(false);
                                }
                            }
                        });
                    }
                });
                log.info("pickStockList.getValue()1---->"+JSONObject.toJSONString(pickStockList.getValue().size()));
                log.info("pickStockList.getValue()2---->"+JSONObject.toJSONString(falg.size()));
                log.info("pickStockList.getValue()3---->"+JSONObject.toJSONString(falg));
                //如果囤货单不包含false 则意味着该囤货单全部提完
                if(CollectionUtils.isNotEmpty(falg)){
                    NewPileTrade detail = detail(pickStockList.getKey());
                    //囤货商品库存已经提完
                    List<GoodsPickStock> byNewPileTradeNos = goodsPickStockRepository.findByNewPileTradeNos(Arrays.asList(detail.getId()));
                    if(!falg.contains(false) && CollectionUtils.isEmpty(byNewPileTradeNos)){
                        detail.getTradeState().setFlowState(NewPileFlowState.COMPLETED);
                        updateTrade(detail);
                    }else{
                        detail.getTradeState().setFlowState(NewPileFlowState.PICK_PART);
                        updateTrade(detail);
                    }
                }
            }
        });
    }
*/
    /**
     * 功能描述: <br> 订单推送WMS
     * 〈〉
     *
     * @Param: [trade, warehouseId]
     * @Return: java.lang.Boolean
     * @Date: 2020/5/18 11:24
     */
    public void pushWMSOrder(Trade trade, Boolean hasGroupedFlag, Boolean villageFlag) {
        if(!orderCommonService.wmsCanTrade(trade)){
            return;
        }
        logger.info("TradeService.pushWMSOrder req tradeId:{} ", trade.getId());
        //查询省市区

        List<Long> city = new ArrayList<>();
        city.add(trade.getConsignee().getProvinceId());
        city.add(trade.getConsignee().getCityId());
        city.add(trade.getConsignee().getAreaId());
        city.add(trade.getConsignee().getTwonId());
        logger.info("TradeService.pushWMSOrder query:{}", JSONObject.toJSONString(city));
        List<RegionCopyVO> responseResults = regionQueryProvider.queryRegionCopyNumber(RegionQueryResponse.builder().number(city).build()).getContext();
        logger.info("TradeService.pushWMSOrder responseResults:{}", JSONObject.toJSONString(responseResults));
        WMSPushOrderRequest wmsPushOrderRequest = new WMSPushOrderRequest();
        String jobNo = WmsErpIdConstants.ERP_CUSTOMER_ID;
        if (StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId())) {
            EmployeeByIdResponse response = employeeQueryProvider
                    .getById(EmployeeByIdRequest.builder().employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(response) && Objects.nonNull(response.getJobNo())) {
                jobNo = response.getJobNo();
            }
        }
        List<WMSPushOrderDetailsRequest> orderDetails = new ArrayList<>(100);
        AtomicInteger lineNo = new AtomicInteger(1);
        //组装订单详情参数
        List<TradeItem> allItems = new ArrayList<>();
        allItems.addAll(trade.getTradeItems());
//        allItems.addAll(trade.getGifts());
        AtomicReference<Long> wholesaleItemNum = new AtomicReference<>(0L);
        allItems.forEach(param -> {
            //子单的erp 仓库编码
            if (!SaleType.RETAIL.equals(trade.getSaleType())) {
                String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                param.setErpSkuNo(param.getErpSkuNo().replace(prefix, ""));
            }
            String loatt04 = ERPWMSConstants.MAIN_WH;
            if ("WH01".equals(trade.getWareHouseCode())) {
                if (Objects.nonNull(param.getGoodsInfoType()) && 1 == param.getGoodsInfoType()) {
                    loatt04 = ERPWMSConstants.MAIN_MARKETING_WH;
                } else {
                    loatt04 = ERPWMSConstants.MAIN_WH;
                }
            }
            if ("WH02".equals(trade.getWareHouseCode())) {
                if (Objects.nonNull(param.getGoodsInfoType()) && 1 == param.getGoodsInfoType()) {
                    loatt04 = ERPWMSConstants.SUB_MARKETING_WH;
                } else {
                    loatt04 = ERPWMSConstants.SUB_WH;
                }
            }
            BigDecimal addStep = param.getAddStep().setScale(2,
                    BigDecimal.ROUND_HALF_UP);
//            BigDecimal itemPrice = param.getPrice();
            /* BigDecimal spliteItemPrice = itemPrice.divide(addStep,2,BigDecimal.ROUND_HALF_UP);*/

            //拆箱规格合并
            BigDecimal num = BigDecimal.valueOf(param.getNum());
            wholesaleItemNum.set(wholesaleItemNum.get() + param.getNum());

            orderDetails.add(WMSPushOrderDetailsRequest.builder()
                    .sku(param.getErpSkuNo())
                    .qtyOrdered(true ? num.multiply(addStep).setScale(2,
                            BigDecimal.ROUND_HALF_UP) : num)
                    .qtyOrdered_each(true ? num.multiply(addStep).setScale(2,
                            BigDecimal.ROUND_HALF_UP) : num)


                    //todo 注意有基础单位的换算 零售保持原来的逻辑 批发不需要乘以步长
//                    .qtyOrdered(trade.getSaleType().equals(SaleType.RETAIL)?BigDecimal.valueOf(param.getNum()).multiply(addStep).setScale(2,
//                            BigDecimal.ROUND_HALF_UP):BigDecimal.valueOf(param.getNum()))
//                    .qtyOrdered_each(trade.getSaleType().equals(SaleType.RETAIL)?BigDecimal.valueOf(param.getNum()).multiply(addStep).setScale(2,
//                            BigDecimal.ROUND_HALF_UP):BigDecimal.valueOf(param.getNum()))
                    .lineNo(lineNo.get())
                    .customerId("XYY")
                    //todo 注意这边的价格后期可能要调整
                    .price(param.getSplitPrice())
                    .userDefine1(param.getGoodsInfoType())
                    .userDefine5(param.getSkuNo())
                    .lotatt04(loatt04)
                    .dedi07(param.getPileOrderCode())
                    .dedi04("N")
                    .dedi08(Objects.nonNull(param.getDevanningId()) ? param.getDevanningId().toString() : null)
                    .build());
            lineNo.incrementAndGet();
        });
        //赠品组装
        AtomicReference<Long> reduce= new AtomicReference<>(0L);
        if (Objects.nonNull(trade.getGifts()) && CollectionUtils.isNotEmpty(trade.getGifts())) {
            trade.getGifts().forEach(param -> {
                if (!SaleType.RETAIL.equals(trade.getSaleType())) {
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    param.setErpSkuNo(param.getErpSkuNo().replace(prefix, ""));
                }
                reduce.set(reduce.get() + param.getNum());
                //子单的erp 仓库编码
                String loatt04 = ERPWMSConstants.MAIN_WH;
                if ("WH01".equals(trade.getWareHouseCode())) {
                    if (Objects.nonNull(param.getGoodsInfoType()) && 1 == param.getGoodsInfoType()) {
                        loatt04 = ERPWMSConstants.MAIN_MARKETING_WH;
                    } else {
                        loatt04 = ERPWMSConstants.MAIN_WH;
                    }
                }
                if ("WH02".equals(trade.getWareHouseCode())) {
                    if (Objects.nonNull(param.getGoodsInfoType()) && 1 == param.getGoodsInfoType()) {
                        loatt04 = ERPWMSConstants.SUB_MARKETING_WH;
                    } else {
                        loatt04 = ERPWMSConstants.SUB_WH;
                    }
                }
                BigDecimal addStep = param.getAddStep().setScale(2,
                        BigDecimal.ROUND_HALF_UP);
//            BigDecimal itemPrice = param.getPrice();
                /* BigDecimal spliteItemPrice = itemPrice.divide(addStep,2,BigDecimal.ROUND_HALF_UP);*/
                orderDetails.add(WMSPushOrderDetailsRequest.builder()
                        .sku(param.getErpSkuNo())
                        //todo 注意有基础单位的换算
                        .qtyOrdered(BigDecimal.valueOf(param.getNum()).multiply(addStep).setScale(2,
                                BigDecimal.ROUND_HALF_UP))
                        .qtyOrdered_each(BigDecimal.valueOf(param.getNum()).multiply(addStep).setScale(2,
                                BigDecimal.ROUND_HALF_UP))
                        .lineNo(lineNo.get())
                        .customerId("XYY")
                        //todo 注意这边的价格后期可能要调整
                        .price(param.getSplitPrice())
                        .userDefine1(param.getGoodsInfoType())
                        .userDefine5(param.getSkuNo())
                        .lotatt04(loatt04)
                        .dedi07(param.getPileOrderCode())
                        .dedi08(Objects.nonNull(param.getDevanningId()) ? param.getDevanningId().toString() : null)
                        .dedi04("Y")
                        .build());
                lineNo.incrementAndGet();
            });
        }


        Supplier supplier = trade.getSupplier();
        //组装订单参数
        wmsPushOrderRequest.setWarehouseId(trade.getWareHouseCode());
        //商家在erp开户ID
        wmsPushOrderRequest.setCustomerId(supplier.getErpId() != null ? supplier.getErpId() : "XYY");
        //固定值
        wmsPushOrderRequest.setOrderType(orderCommonService.getWmsDocType(trade));
        //来源
        wmsPushOrderRequest.setSoReferenceA("001");
        //订单号
        wmsPushOrderRequest.setDocNo(trade.getId());
        //固定值
        wmsPushOrderRequest.setAddWho("DS");
        //固定值
        wmsPushOrderRequest.setSoReferenceD(orderCommonService.getWmsSoReference5(trade));
        //订单释放状态 --- 如果先款后货的传N 如果是不限的传Y
        wmsPushOrderRequest.setPriority(PaymentOrder.PAY_FIRST.equals(trade.getPaymentOrder()) ? "N" : "Y");
        //如果是拼团成功的订单也直接存Y
        if (hasGroupedFlag) {
            wmsPushOrderRequest.setPriority("Y");
        }
        //省市
        if (CollectionUtils.isNotEmpty(responseResults)) {
            StringBuffer consigneeProvince = new StringBuffer();
            StringBuffer consigneeArea = new StringBuffer();
            for (RegionCopyVO regionVO : responseResults) {
                if ((regionVO.getCode().equals(trade.getConsignee().getProvinceId())) || (regionVO.getCode().equals(trade.getConsignee().getCityId()))) {
                    //省
                    consigneeProvince.append(regionVO.getName());
                }
                if (regionVO.getCode().equals(trade.getConsignee().getAreaId())) {
                    consigneeArea.append(regionVO.getName());
                }
            }
            wmsPushOrderRequest.setConsigneeProvince(consigneeProvince.toString());
            wmsPushOrderRequest.setConsigneeCity(consigneeArea.toString());
            log.info("ConsigneeCity - consigneeArea : " + consigneeArea.toString() + " twonid:" + trade.getConsignee().getTwonId());
        }
        //订单创建时间
        wmsPushOrderRequest.setOrderTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1));
        //收货人的erpId
//        wmsPushOrderRequest.setConsigneeId(trade.getBuyer().getCustomerErpId());
        wmsPushOrderRequest.setConsigneeId(trade.getBuyer().getAccount());
        //收货人名称
        wmsPushOrderRequest.setConsigneeName(trade.getConsignee().getName());
        //收货人联系人名称
        wmsPushOrderRequest.setConsigneeTel2(trade.getConsignee().getName());
        //收货人电话
        wmsPushOrderRequest.setConsigneeTel1(trade.getConsignee().getPhone());
        //收货人的地址
        wmsPushOrderRequest.setConsigneeAddress1(trade.getConsignee().getDetailAddress());
        //订单总价-配送费-包装费
        /*String priceDetail = trade.getTradePrice().getTotalPrice().toString();
        if (Objects.nonNull(trade.getTradePrice().getDeliveryPrice()) && trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) >= 0) {
            priceDetail = priceDetail.concat("-").concat(trade.getTradePrice().getDeliveryPrice().toString());
        } else {
            priceDetail = priceDetail.concat("-").concat("0");
        }
        if (Objects.nonNull(trade.getTradePrice().getPackingPrice()) && trade.getTradePrice().getPackingPrice().compareTo(BigDecimal.ZERO) >= 0) {
            priceDetail = priceDetail.concat("-").concat(trade.getTradePrice().getPackingPrice().toString());
        } else {
            priceDetail = priceDetail.concat("-").concat("0");
        }*/
        wmsPushOrderRequest.setConsigneeAddress2(String.valueOf(wholesaleItemNum.get() +reduce.get()));
        //线上线下
        wmsPushOrderRequest.setUserDefine1(jobNo);
        //付款标识
        wmsPushOrderRequest.setUserDefine2(PayState.PAID.equals(trade.getTradeState().getPayState()) ? "2" : "0");
        wmsPushOrderRequest.setUserDefine3(supplier.getStoreName());
        //预约发货时间
        wmsPushOrderRequest.setExpectedShipmentTime1(Objects.isNull(trade.getBookingDate()) ? null : trade.getBookingDate().toString() + " 00:00:00");
        //业务员
        wmsPushOrderRequest.setUserDefine6(jobNo);
        //备注
        wmsPushOrderRequest.setNotes(trade.getBuyerRemark());
        //运送方式
        wmsPushOrderRequest.setCarrierMail(switchWMSPushOrderType(trade.getDeliverWay()));
        //是否是乡村件
        if (villageFlag && trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            wmsPushOrderRequest.setConsigneeAddress3("1");
        } else {
            wmsPushOrderRequest.setConsigneeAddress3("0");
        }


        //明细
        wmsPushOrderRequest.setDetails(orderDetails);
        if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo())) {
            if (Objects.nonNull(trade.getLogisticsCompanyInfo().getCompanyNumber())) {
                wmsPushOrderRequest.setCarrierId(trade.getLogisticsCompanyInfo().getCompanyNumber());
            } else {
                String uuid = UUIDUtil.getUUID().substring(29);
                wmsPushOrderRequest.setCarrierId("customer-" + uuid);
            }
            wmsPushOrderRequest.setCarrierName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
            wmsPushOrderRequest.setCarrierFax(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
            wmsPushOrderRequest.setUserDefine5(trade.getLogisticsCompanyInfo().getLogisticsAddress());
            wmsPushOrderRequest.setUserDefine4(trade.getLogisticsCompanyInfo().getReceivingPoint());//增加收货站点
        }
        boolean noLimitFlag = AuditState.CHECKED.equals(trade.getTradeState().getAuditState()) && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        //wmsPushOrderRequest.setUserDefine3(noLimitFlag ? "0" : "1");
        wmsPushOrderRequest.setUserDefine3(supplier.getStoreName());
        ResponseWMSReturnResponse response = requestWMSOrderProvider.putSalesOrder(wmsPushOrderRequest).getContext();
        if (Objects.isNull(response)
                || Objects.isNull(response.getResponseWMSReturnVO())
                || Objects.isNull(response.getResponseWMSReturnVO().getReturnFlag())
                || response.getResponseWMSReturnVO().getReturnFlag() < 1) {
            throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_TIME_OUT);
        }
        //扣除乡镇件库存、减去真实库存
        if (trade.getVillageFlag()) {
            this.updateStockAndTownStock(trade);
        }

    }

    public void updateStockAndTownStock(Trade trade) {
        log.info("com.wanmi.sbc.returnorder.trade.service.TradeService.updateStockAndTownStock乡镇件减库存tid=" + trade.getId());
//        verifyService.subSkuVillagesStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
//        verifyService.subSkuVillagesStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
        verifyService.subSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
        verifyService.subSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
        historyTownShipOrderService.reduceTownStock(trade.getId());
    }

    private String switchWMSPushOrderType(DeliverWay deliverWay) {
        if (DeliverWay.PICK_SELF.equals(deliverWay)) {
            return "ZTCK";
        } else if (DeliverWay.EXPRESS.equals(deliverWay)) {
            return "KDCK";
        } else if (DeliverWay.DELIVERY_HOME.equals(deliverWay)) {
            return "PSCK";
        } else {
            return "WLCK";
        }
    }

    public void sendPickUpMessage(Trade trade) {
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        messageMQRequest.setNodeCode(OrderProcessType.CUSTOMER_PICK_UP_PAY_SUCCESS.getType());
        map.put("node", OrderProcessType.CUSTOMER_PICK_UP_PAY_SUCCESS.toValue());
        List<String> param = new ArrayList<>(2);
        param.add(trade.getId());
        param.add(trade.getTradeWareHouse().getPickUpCode());
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setParams(param);
        messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setMobile(trade.getConsignee().getPhone());
        orderProducerService.sendMessage(messageMQRequest);
    }

    public void sendPickUpMessageNewPile(NewPileTrade trade) {
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        messageMQRequest.setNodeCode(OrderProcessType.CUSTOMER_PICK_UP_PAY_SUCCESS.getType());
        map.put("node", OrderProcessType.CUSTOMER_PICK_UP_PAY_SUCCESS.toValue());
        List<String> param = new ArrayList<>(2);
        param.add(trade.getId());
        param.add(trade.getTradeWareHouse().getPickUpCode());
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setParams(param);
        messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setMobile(trade.getConsignee().getPhone());
        orderProducerService.sendMessage(messageMQRequest);
    }

    /**
     * 平台,商家带客下单，审核关闭都要创建支付单
     *
     * @param trade
     * @param operator
     * @param orderAuditSwitch
     */
    public void createPayOrder(Trade trade, Operator operator, Boolean orderAuditSwitch) {
        log.info("operator.getPlatform======: {}", JSONObject.toJSONString(operator.getPlatform()));

        log.info("orderAuditSwitch======: {}", orderAuditSwitch);

        if (operator.getPlatform() == Platform.BOSS || operator.getPlatform() == Platform.SUPPLIER ||
                !orderAuditSwitch) {
            if (trade.getPayOrderId() != null) {
                payOrderService.deleteByPayOrderId(trade.getPayOrderId());
                receivableService.deleteReceivables(Collections.singletonList(trade.getPayOrderId()));
            }
            //创建支付单
            Optional<PayOrder> optional = payOrderService.generatePayOrderByOrderCode(
                    new PayOrderGenerateRequest(trade.getId(),
                            trade.getBuyer().getId(),
                            trade.getTradePrice().getTotalPrice(),
                            trade.getTradePrice().getPoints(),
                            PayType.valueOf(trade.getPayInfo().getPayTypeName()),
                            trade.getSupplier().getSupplierId(),
                            trade.getTradeState().getCreateTime(),
                            trade.getOrderType()));

            trade.getTradeState().setPayState(PayState.NOT_PAID);
            optional.ifPresent(payOrder -> trade.setPayOrderId(payOrder.getPayOrderId()));
        }
    }


    public void createPayOrderNewPileTrade(NewPileTrade newPileTrade, Operator operator, Boolean orderAuditSwitch) {
        log.info("operator.getPlatform======: {}", JSONObject.toJSONString(operator.getPlatform()));

        log.info("orderAuditSwitch======: {}", orderAuditSwitch);

        if (operator.getPlatform() == Platform.BOSS || operator.getPlatform() == Platform.SUPPLIER ||
                !orderAuditSwitch) {

            if (newPileTrade.getPayOrderId() != null) {
                payOrderService.deleteByPayOrderId(newPileTrade.getPayOrderId());
                receivableService.deleteReceivables(Collections.singletonList(newPileTrade.getPayOrderId()));
            }
            //创建支付单
            Optional<PayOrder> optional = payOrderService.generatePayOrderByOrderCode(
                    new PayOrderGenerateRequest(newPileTrade.getId(),
                            newPileTrade.getBuyer().getId(),
                            newPileTrade.getTradePrice().getTotalPrice(),
                            newPileTrade.getTradePrice().getPoints(),
                            PayType.valueOf(newPileTrade.getPayInfo().getPayTypeName()),
                            newPileTrade.getSupplier().getSupplierId(),
                            newPileTrade.getTradeState().getCreateTime(),
                            newPileTrade.getOrderType()));

            newPileTrade.getTradeState().setPayState(PayState.NOT_PAID);
            optional.ifPresent(payOrder -> newPileTrade.setPayOrderId(payOrder.getPayOrderId()));
        }
    }


    public synchronized void stockupdate(Trade trade, Boolean flag) {
        List<TradeItem> tradeItems = trade.getTradeItems();
//        this.checkGoodsStockForValid(tradeItems);
        //如果是乡镇件并且是不是实时推那么不扣库存
//        if (flag && !isActualpush()){
//            trade.setVillageFlag(true);
//            // 乡镇件 库存表+库存
//            verifyService.addSkuVillagesStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
//            verifyService.addSkuVillagesStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
//            //减营销活动限购数量
//            this.calMarketGoodsNum(trade.getTradeItems(),false);
//        }else {
//            //判断是否为秒杀抢购订单--进行扣减秒杀商品库存和增加销量操作
//            if (Objects.nonNull(trade.getIsFlashSaleGoods()) && trade.getIsFlashSaleGoods()) {
//                //扣减秒杀商品库存和增加销量
//                verifyService.batchFlashSaleGoodsStockAndSalesVolume(trade.getTradeItems());
//            } else {
//                // 2.减商品,赠品库存
//                verifyService.subSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),trade.getSaleType());
//                verifyService.subSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),trade.getSaleType());
//                //提交订单成功减囤货数量
//                if(trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())){
//                    this.TakeGoods(trade);
//                }
//                //减营销活动限购数量
//                this.calMarketGoodsNum(trade.getTradeItems(),false);
//            }
//        }


        if (!flag) {
            //判断是否为秒杀抢购订单--进行扣减秒杀商品库存和增加销量操作
            if (Objects.nonNull(trade.getIsFlashSaleGoods()) && trade.getIsFlashSaleGoods()) {
                //扣减秒杀商品库存和增加销量
                verifyService.batchFlashSaleGoodsStockAndSalesVolume(trade.getTradeItems());
            } else {
                // 2.减商品,赠品库存
                verifyService.subSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                verifyService.subSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                //提交订单成功减囤货数量
                if (trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
                    this.TakeGoods(trade);
                }
                //减营销活动限购数量
                this.calMarketGoodsNum(trade.getTradeItems(), false);
            }
        } else {
            trade.setVillageFlag(true);
            // 乡镇件 库存表+库存
            verifyService.addSkuVillagesStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
            verifyService.addSkuVillagesStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
            //减营销活动限购数量
            this.calMarketGoodsNum(trade.getTradeItems(), false);
        }
    }

    private void TakeGoods(Trade trade) {
        String customerId = trade.getBuyer().getId();
        List<TradeItem> tradeItems = trade.getTradeItems();
        tradeItems.stream().forEach(tradeItem -> {
            String spuId = tradeItem.getSpuId();
            String skuId = tradeItem.getSkuId();
            //购买数量
            Long num = tradeItem.getNum();
            //通过spuId+skuId+用户id查询囤货表的数据看看是不是有多个，有多个就根据时间减他妈的
            List<PilePurchase> pilePurchases = pilePurchaseRepository.queryPilePurchase(customerId, spuId, skuId);
            if (CollectionUtils.isNotEmpty(pilePurchases)) {
                long sum = pilePurchases.stream().mapToLong(PilePurchase::getGoodsNum).sum();
                if (num > sum) {
                    throw new SbcRuntimeException("K-030301");
                }
                //第一笔
                PilePurchase pilePurchase = pilePurchases.stream().findFirst().get();
                if (num > pilePurchase.getGoodsNum()) {
                    throw new SbcRuntimeException("K-030301");
                }
                pilePurchase.setGoodsNum(pilePurchase.getGoodsNum() - num);
                pilePurchaseRepository.save(pilePurchase);
            }
        });
    }


    /**
     * 从(囤货)购物车中删除商品信息
     */
    private void deleteShopCartOrderdevanningNewPile(String customerId, List<String> skuIds, List<Long> devannings, DistributeChannel distributeChannel, Long wareId) {

        PurchaseDeleteRequest request = new PurchaseDeleteRequest();
        request.setCustomerId(customerId);
        request.setGoodsInfoIds(skuIds);
        request.setDevanningIds(devannings);
        shopCartNewPileProvider.devanningDeleteCache(request);

        //删除购物车数据
        String numkey = RedisKeyConstants.STORE_SHOPPING_CART_HASH.concat(customerId).concat(wareId.toString());
        String key = RedisKeyConstants.STORE_SHOPPING_CART_EXTRA_HASH.concat(customerId).concat(wareId.toString());
        devannings.forEach(v -> {
            if (redisCache.HashHasKey(key, v.toString())) {
                redisCache.hashDel(key, v.toString());
                redisCache.hashDel(numkey, RedisKeyConstants.is_check.concat(v.toString()));
                redisCache.hashDel(numkey, RedisKeyConstants.good_num.concat(v.toString()));
            }
        });
        //删除购物车快照
        String kkey = RedisKeyConstants.FIRST_SNAPSHOT.concat(request.getCustomerId());
        redisCache.delete(kkey);

    }


    /**
     * 从(正常)购物车中删除商品信息
     */
    private void deleteShopCartOrderdevanning(String customerId, List<String> skuIds, List<Long> devannings, DistributeChannel distributeChannel, Long wareId) {
        ShopCartRequest request = ShopCartRequest.builder()
                .customerId(customerId).devanningIds(devannings)
                .goodsInfoIds(skuIds).inviteeId(getPurchaseInviteeId(distributeChannel))
                .build();
        shopCartService.delete(request);

        //删除购物车数据
        String numkey = RedisKeyConstants.SHOPPING_CART_HASH.concat(request.getCustomerId()).concat(wareId.toString());
        String key = RedisKeyConstants.SHOPPING_CART_EXTRA_HASH.concat(customerId).concat(wareId.toString());
        devannings.forEach(v -> {
            if (redisCache.HashHasKey(key, v.toString())) {
                redisCache.hashDel(key, v.toString());
                redisCache.hashDel(numkey, RedisKeyConstants.is_check.concat(v.toString()));
                redisCache.hashDel(numkey, RedisKeyConstants.good_num.concat(v.toString()));
            }
        });
        //删除购物车快照
        String kkey = RedisKeyConstants.FIRST_SNAPSHOT.concat(request.getCustomerId());
        redisCache.delete(kkey);

    }


    /**
     * 获取购物车归属
     * 当且仅当为店铺精选时，需要根据InviteeId区分购物车
     */
    public String getPurchaseInviteeId(DistributeChannel distributeChannel) {

        if (null != distributeChannel && Objects.equals(distributeChannel.getChannelType(), ChannelType.SHOP)) {
            return distributeChannel.getInviteeId();
        }
        return Constants.PURCHASE_DEFAULT;
    }

    /**
     * 计算某个单品模板的运费
     *
     * @param temp             单品运费模板
     * @param freightTempId    需要计算首件运费的配送地模板id
     * @param templateGoodsMap 按模板id分组的商品汇总信息
     * @return 模板的总运费
     */
    public BigDecimal getSingleTemplateFreight(FreightTemplateGoodsVO temp, Long freightTempId, Map<Long, TradeItem> templateGoodsMap) {
        //是否需要计算首件运费标识
        boolean startFlag = temp.getFreightTempId().equals(freightTempId);
        log.info("计算费用falg" + startFlag);
        TradeItem traItem = templateGoodsMap.get(temp.getFreightTempId());
        BigDecimal num = BigDecimal.valueOf(traItem.getNum());//商品数量
        BigDecimal goodsWeight = traItem.getGoodsWeight();//商品重量
        BigDecimal goodsCubage = traItem.getGoodsCubage();//商品体积
        //判断是白鲸散批还是拆箱批发
//        List<String> goodslist = new LinkedList<>();
//        goodslist.add(traItem.getSpuId());
//        List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = devanningGoodsInfoProvider.getmaxdata(DevanningGoodsInfoPageRequest.builder().goodsIds(goodslist).build()).getContext().getDevanningGoodsInfoVOS();
//        if (!CollectionUtils.isEmpty(devanningGoodsInfoVOS)){
//            //拆箱批发
//            Optional<DevanningGoodsInfoVO> deoptional = devanningGoodsInfoVOS.stream().filter(devanningGoodsInfoVO -> {
//                if (1 == devanningGoodsInfoVO.getAddedFlag() && DeleteFlag.NO.equals(devanningGoodsInfoVO.getDelFlag())) {
//                    return true;
//                }
//                return false;
//            }).findFirst();
//            if (deoptional.isPresent()){
//                throw new SbcRuntimeException("K-050510","商品已经删除或者已经下架请联系客户处理");
//            }
//            DevanningGoodsInfoVO devanningGoodsInfoVO=deoptional.get();
//            num=num.divide(devanningGoodsInfoVO.getAddStep(),0,BigDecimal.ROUND_DOWN);
//            num=num.compareTo(BigDecimal.ZERO)==0?BigDecimal.ONE:num;
//            goodsWeight=goodsWeight.divide(devanningGoodsInfoVO.getAddStep(),3,BigDecimal.ROUND_DOWN);
//            goodsCubage=goodsCubage.divide(devanningGoodsInfoVO.getAddStep(),6,BigDecimal.ROUND_DOWN);
//        }
        FreightTemplateGoodsExpressVO expTemplate = temp.getExpTemplate();
        switch (temp.getValuationType()) {
            case NUMBER: //按件数
                return startFlag ? getStartAndPlusFreight(num, expTemplate)
                        : getPlusFreight(num, expTemplate);
            case WEIGHT: //按重量
                return startFlag ? getStartAndPlusFreight(goodsWeight, expTemplate)
                        : getPlusFreight(goodsWeight, expTemplate);
            case VOLUME: //按体积
                return startFlag ? getStartAndPlusFreight(goodsCubage, expTemplate)
                        : getPlusFreight(goodsCubage, expTemplate);
            case WEIGHTBYNUM://按重量/件
                return startFlag ? getWeightByNumStartAndPlusFreight(goodsWeight,
                        num, expTemplate)
                        : getWeightByNumPlusFreight(goodsWeight, num, expTemplate);
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 计算 首件 + 续件 总费用
     *
     * @param itemCount
     * @param expTemplate
     * @return
     */
    private BigDecimal getStartAndPlusFreight(BigDecimal itemCount, FreightTemplateGoodsExpressVO expTemplate) {
        if (itemCount.compareTo(expTemplate.getFreightStartNum()) <= 0) {
            return expTemplate.getFreightStartPrice();//首件数以内,则只算首运费
        } else {
            //总费用 = 首件费用 + 续件总费用
            return expTemplate.getFreightStartPrice().add(
                    getPlusFreight(itemCount.subtract(expTemplate.getFreightStartNum()), expTemplate)
            );
        }
    }

    /**
     * 获取匹配的单品运费模板-用于计算运费
     *
     * @param temps  多个收货的运费模板
     * @param provId 省份id
     * @param cityId 地市id
     * @return 匹配上的运费模板
     */
    private FreightTemplateGoodsExpressVO getMatchFreightTemplate(List<FreightTemplateGoodsExpressVO> temps,
                                                                  Long provId,
                                                                  Long cityId) {
        Optional<FreightTemplateGoodsExpressVO> expOpt = temps.stream().filter(exp ->
                this.matchArea(exp.getDestinationArea(), provId, cityId)).findFirst();
        FreightTemplateGoodsExpressVO expTemp;
        if (expOpt.isPresent()) {
            expTemp = expOpt.get();
        } else {
            expTemp = temps.stream().filter(exp ->
                    DefaultFlag.YES.equals(exp.getDefaultFlag())).findFirst().get();
        }
        return expTemp;
    }

    /**
     * 校验推送参数
     *
     * @param trade
     * @return
     */
    private Boolean orderkingdeeCheckNewPileTrade(NewPileTrade trade) {
        if (StringUtils.isEmpty(trade.getBuyer().getAccount())) {
            logger.info("TradeService.pushSalesOrderkingdee Lack FCustId");
            return false;
        }
        if (trade.getTradePrice().getDeliveryPrice() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack FFreight");
            return false;
        }
        if (StringUtils.isEmpty(trade.getBuyer().getEmployeeId())) {
            logger.info("TradeService.pushSalesOrderkingdee Lack FSalerId");
            return false;
        }
        if (trade.getConsignee().getProvinceId() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack ProvinceId");
            return false;
        }
        if (trade.getConsignee().getCityId() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack CityId");
            return false;
        }
        if (trade.getConsignee().getAreaId() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack AreaId");
            return false;
        }
        if (StringUtils.isEmpty(trade.getConsignee().getDetailAddress())) {
            logger.info("TradeService.pushSalesOrderkingdee Lack DetailAddress");
            return false;
        }
        if (trade.getDeliverWay() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack DeliverWay");
            return false;
        }
        return true;
    }


    /**
     * 校验推送参数
     *
     * @param trade
     * @return
     */
    private Boolean orderkingdeeCheck(Trade trade) {
        if (StringUtils.isEmpty(trade.getBuyer().getAccount())) {
            logger.info("TradeService.pushSalesOrderkingdee Lack FCustId");
            return false;
        }
        if (trade.getTradePrice().getDeliveryPrice() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack FFreight");
            return false;
        }
        if (StringUtils.isEmpty(trade.getBuyer().getEmployeeId())) {
            logger.info("TradeService.pushSalesOrderkingdee Lack FSalerId");
            return false;
        }
        if (trade.getConsignee().getProvinceId() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack ProvinceId");
            return false;
        }
        if (trade.getConsignee().getCityId() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack CityId");
            return false;
        }
        if (trade.getConsignee().getAreaId() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack AreaId");
            return false;
        }
        if (StringUtils.isEmpty(trade.getConsignee().getDetailAddress())) {
            logger.info("TradeService.pushSalesOrderkingdee Lack DetailAddress");
            return false;
        }
        if (trade.getDeliverWay() == null) {
            logger.info("TradeService.pushSalesOrderkingdee Lack DeliverWay");
            return false;
        }
        //暂时去掉销售类型订单校验，不兼容囤货
//        if (Objects.isNull(trade.getSaleType())) {
//            logger.info("TradeService.pushSalesOrderkingdee Lack SaleType");
//            return false;
//        }
        return true;
    }

    /**
     * 计算续件总费用
     *
     * @param itemCount   商品数量
     * @param expTemplate 匹配的运费模板
     * @return 续件总费用
     */
    private BigDecimal getPlusFreight(BigDecimal itemCount, FreightTemplateGoodsExpressVO expTemplate) {
        //商品数量/续件数量 * 续件金额
        return itemCount.divide(expTemplate.getFreightPlusNum(), 0, BigDecimal.ROUND_UP)
                .multiply(expTemplate.getFreightPlusPrice());
    }

    /**
     * 功能描述:
     *
     * @param itemCount   总重量
     * @param num         购买件数
     * @param expTemplate
     * @return: java.math.BigDecimal 运费金额
     */
    private BigDecimal getWeightByNumStartAndPlusFreight(BigDecimal itemCount, BigDecimal num,
                                                         FreightTemplateGoodsExpressVO expTemplate) {
        if (itemCount.divide(num, 3, BigDecimal.ROUND_UP).compareTo(expTemplate.getFreightStartNum()) <= 0) {//单件的重量小于首重
            //首重*数量*首重价格
            return expTemplate.getFreightStartPrice().multiply(num);
        } else {
            //（首重价格+((总重/件数)-首重)/续重*续重价格）*件数
            log.info("==========================z" + itemCount);
            log.info("==========================z" + num);
            BigDecimal multiply = expTemplate.getFreightStartPrice().add(itemCount.divide(num, 10, BigDecimal.ROUND_UP).subtract(expTemplate.getFreightStartNum())
                    .divide(expTemplate.getFreightPlusNum(), 0, BigDecimal.ROUND_UP)
                    .multiply(expTemplate.getFreightPlusPrice()))
                    .multiply(num);
            return multiply;
        }
    }

    /**
     * 功能描述:
     * 计算：总重/件数/续重*续重价格*件数
     *
     * @param itemCount   总重
     * @param num         购买件数
     * @param expTemplate
     * @return: java.math.BigDecimal
     */
    private BigDecimal getWeightByNumPlusFreight(BigDecimal itemCount, BigDecimal num,
                                                 FreightTemplateGoodsExpressVO expTemplate) {
        return itemCount.divide(num, 10, BigDecimal.ROUND_UP)
                .divide(expTemplate.getFreightPlusNum(), 0, BigDecimal.ROUND_UP)
                .multiply(expTemplate.getFreightPlusPrice())
                .multiply(num);
    }

    /**
     * 是否包邮
     *
     * @param temp             单品运费模板
     * @param templateGoodsMap 按模板id分组的商品汇总信息
     * @param deliverWay       运送方式
     * @param provId           省份id
     * @param cityId           城市id
     * @return
     */
    public boolean getFreeFreightFlag(FreightTemplateGoodsVO temp, Map<Long, TradeItem> templateGoodsMap, DeliverWay deliverWay, Long provId, Long cityId) {
        if (DefaultFlag.YES.equals(temp.getSpecifyTermFlag())) {
            ValuationType valuationType = temp.getValuationType();
            List<FreightTemplateGoodsFreeVO> freeTemplateList = temp.getFreightTemplateGoodsFrees();
            Optional<FreightTemplateGoodsFreeVO> freeOptional = freeTemplateList.stream().filter(free -> this.matchArea(
                    free.getDestinationArea(), provId, cityId)).findFirst();

            //2.3.1. 找到收货地匹配的 并且 运送方式一致的指定包邮条件
            if (freeOptional.isPresent() && deliverWay.equals(freeOptional.get().getDeliverWay())) {
                FreightTemplateGoodsFreeVO freeObj = freeOptional.get();
                ConditionType conditionType = freeObj.getConditionType();

                //2.3.2. 根据计价方式,计算包邮条件是否满足
                switch (valuationType) {
                    case NUMBER: //按件数
                        switch (conditionType) {
                            case VALUATION:
                                if (BigDecimal.valueOf(templateGoodsMap.get(temp.getFreightTempId()).getNum())
                                        .compareTo(freeObj.getConditionOne()) >= 0) {//件数高于-包邮
                                    return false;
                                }
                                break;
                            case MONEY:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getSplitPrice()
                                        .compareTo(freeObj.getConditionTwo()) >= 0) {//金额高于-包邮
                                    return false;
                                }
                                break;
                            case VALUATIONANDMONEY:
                                if (BigDecimal.valueOf(templateGoodsMap.get(temp.getFreightTempId()).getNum())
                                        .compareTo(freeObj.getConditionOne()) >= 0 &&
                                        templateGoodsMap.get(temp.getFreightTempId()).getSplitPrice()
                                                .compareTo(freeObj.getConditionTwo()) >= 0) {//件数高于,金额高于-包邮
                                    return false;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case WEIGHT: //按重量
                        switch (conditionType) {
                            case VALUATION:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getGoodsWeight()
                                        .compareTo(freeObj.getConditionOne()) <= 0) {//重量低于-包邮
                                    return false;
                                }
                                break;
                            case MONEY:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getSplitPrice()
                                        .compareTo(freeObj.getConditionTwo()) >= 0) {//金额高于-包邮
                                    return false;
                                }
                                break;
                            case VALUATIONANDMONEY:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getGoodsWeight()
                                        .compareTo(freeObj.getConditionOne()) <= 0 &&
                                        templateGoodsMap.get(temp.getFreightTempId()).getSplitPrice()
                                                .compareTo(freeObj.getConditionTwo()) >= 0) {//重量低于,金额高于-包邮
                                    return false;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case VOLUME: //按体积
                        switch (conditionType) {
                            case VALUATION:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getGoodsCubage()
                                        .compareTo(freeObj.getConditionOne()) <= 0) {//体积低于-包邮
                                    return false;
                                }
                                break;
                            case MONEY:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getSplitPrice()
                                        .compareTo(freeObj.getConditionTwo()) >= 0) {//金额高于-包邮
                                    return false;
                                }
                                break;
                            case VALUATIONANDMONEY:
                                if (templateGoodsMap.get(temp.getFreightTempId()).getGoodsCubage()
                                        .compareTo(freeObj.getConditionOne()) <= 0 &&
                                        templateGoodsMap.get(temp.getFreightTempId()).getSplitPrice()
                                                .compareTo(freeObj.getConditionTwo()) >= 0) {//体积低于,金额高于-包邮
                                    return false;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }


    /**
     * 按模板id分组的商品汇总信息(模板Id,件数,重量,体积,小计均摊价)
     *
     * @param templateGoodsMap
     * @param items
     */
    public static void setGoodsSumMap(Map<Long, TradeItem> templateGoodsMap, List<TradeItem> items) {
        if (items != null) {
            items.stream().forEach(goods -> {
                TradeItem item = templateGoodsMap.get(goods.getFreightTempId());
                if (item == null) {
                    if (Objects.nonNull(goods.getDevanningId())) {
                        BigDecimal num = BigDecimal.valueOf(goods.getNum());//原数量
                        BigDecimal buyNum = BigDecimal.valueOf(goods.getNum()).multiply(goods.getDivisorFlag());
                        buyNum = buyNum.compareTo(BigDecimal.ONE) == -1 ? BigDecimal.ONE : buyNum;
                        templateGoodsMap.put(goods.getFreightTempId(), TradeItem.builder()
                                .freightTempId(goods.getFreightTempId())
                                .num(buyNum.setScale(0, BigDecimal.ROUND_UP).longValue())
                                .goodsWeight(goods.getGoodsWeight().multiply(buyNum.setScale(0, BigDecimal.ROUND_UP)))
                                .goodsCubage(goods.getGoodsCubage().multiply(buyNum))
                                .splitPrice(goods.getSplitPrice() == null ? BigDecimal.ZERO : goods.getSplitPrice())
                                .build());
                    } else {
                        templateGoodsMap.put(goods.getFreightTempId(), TradeItem.builder()
                                .freightTempId(goods.getFreightTempId())
                                .num(goods.getNum())
                                .goodsWeight(goods.getGoodsWeight().multiply(BigDecimal.valueOf(goods.getNum())))
                                .goodsCubage(goods.getGoodsCubage().multiply(BigDecimal.valueOf(goods.getNum())))
                                .splitPrice(goods.getSplitPrice() == null ? BigDecimal.ZERO : goods.getSplitPrice())
                                .build());
                    }
                } else {
                    if (Objects.nonNull(goods.getDevanningId())) {
                        BigDecimal buyNum = BigDecimal.valueOf(goods.getNum()).multiply(goods.getDivisorFlag());
                        buyNum = buyNum.compareTo(BigDecimal.ONE) == -1 ? BigDecimal.ONE : buyNum;
                        item.setNum(item.getNum() + buyNum.setScale(0, BigDecimal.ROUND_UP).longValue());
                        item.setGoodsWeight(item.getGoodsWeight().add(goods.getGoodsWeight().multiply(buyNum)));
                        item.setGoodsCubage(item.getGoodsCubage().add(goods.getGoodsCubage().multiply(buyNum)));
                        item.setSplitPrice(item.getSplitPrice().add(goods.getSplitPrice() == null ? BigDecimal.ZERO :
                                goods.getSplitPrice()));
                    } else {
                        item.setNum(item.getNum() + goods.getNum());
                        item.setGoodsWeight(item.getGoodsWeight().add(goods.getGoodsWeight().multiply(BigDecimal.valueOf
                                (goods.getNum()))));
                        item.setGoodsCubage(item.getGoodsCubage().add(goods.getGoodsCubage().multiply(BigDecimal.valueOf
                                (goods.getNum()))));
                        item.setSplitPrice(item.getSplitPrice().add(goods.getSplitPrice() == null ? BigDecimal.ZERO :
                                goods.getSplitPrice()));
                    }
                }
            });
        }
    }

    /**
     * 包装营销信息(供确认订单使用)
     */
    public List<TradeMarketingVO> wrapperMarketingForConfirm(List<TradeItem> skus, List<TradeMarketingDTO>
            tradeMarketingRequests) {
        log.info("==============3" + skus);
        // 1.新促销规定拆箱的商品不参加营销活动,不需要合并
        for (TradeItem tradeItem : skus) {
            tradeItem.setPrice(BigDecimal.valueOf(tradeItem.getNum()).multiply(tradeItem.getPrice()));
            if (Objects.nonNull(tradeItem.getDivisorFlag()) && tradeItem.getDivisorFlag().compareTo(BigDecimal.ZERO) > 0) {
                tradeItem.setBNum(tradeItem.getDivisorFlag().multiply(BigDecimal.valueOf(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_DOWN));
            } else {
                tradeItem.setBNum(BigDecimal.valueOf(tradeItem.getNum()));
            }
        }
        log.info("new sku:{}", skus);

        Map<String, BigDecimal> numCollect = skus.stream().filter(x -> {
            return Objects.nonNull(x.getDivisorFlag()) && x.getDivisorFlag().compareTo(BigDecimal.ONE) >= 0;
        }).collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getBNum, BigDecimal::add)));
        log.info("TradeService wrapperMarketingForConfirm numCollect :{}", numCollect);

        Map<String, BigDecimal> marketCollect = skus.stream().filter(x -> {
            return Objects.nonNull(x.getDivisorFlag()) && x.getDivisorFlag().compareTo(BigDecimal.ONE) >= 0;
        }).collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getPrice, BigDecimal::add)));
        log.info("TradeService wrapperMarketingForConfirm marketCollect :{}", marketCollect);

//        Map<String, BigDecimal> vipCollect = skus.stream().collect(Collectors.groupingBy(TradeItem::getSkuId, Collectors.reducing(BigDecimal.ZERO, TradeItem::getVipPrice, BigDecimal::add)));
        skus = skus.stream().filter(x -> {
            // 移除拆箱的规格
            return Objects.nonNull(x.getDivisorFlag()) && x.getDivisorFlag().compareTo(BigDecimal.ONE) >= 0;
        }).filter(distinctByKey((p) -> (p.getSkuId()))).collect(Collectors.toList());
        // 1.构建营销插件请求对象
        List<TradeMarketingWrapperDTO> requests = new ArrayList<>();
        List<TradeMarketingVO> tradeMarketings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tradeMarketingRequests)) {

            List<TradeItem> finalSkus = skus;
            if (CollectionUtils.isNotEmpty(finalSkus)) {
                for (TradeMarketingDTO tradeMarketing : tradeMarketingRequests) {
                    List<TradeItemInfoDTO> tradeItems = finalSkus.stream()
                            .filter(s -> tradeMarketing.getSkuIds().contains(s.getSkuId()))
                            .map(t -> TradeItemInfoDTO.builder()
//                                .num(Objects.isNull(t.getDivisorFlag()) ? t.getNum() : (t.getDivisorFlag().multiply(BigDecimal.valueOf(t.getNum()))).setScale(0,BigDecimal.ROUND_DOWN).longValue())
                                    .num(numCollect.get(t.getSkuId()).setScale(0, BigDecimal.ROUND_DOWN).longValue())
                                    .price(t.getPrice())
                                    .allMarketPrice(marketCollect.get(t.getSkuId()))
//                                .allVipPrice(vipCollect.get(t.getSkuId()))
                                    .skuId(t.getSkuId())
                                    .storeId(t.getStoreId())
                                    .distributionGoodsAudit(t.getDistributionGoodsAudit())
                                    .build())
                            .collect(Collectors.toList());
                    requests.add(TradeMarketingWrapperDTO.builder()
                            .tradeMarketingDTO(tradeMarketing)
                            .tradeItems(tradeItems).build());
                }
            }
        }
        log.info("调用插件前传入的数据" + requests);

        // 2.调用营销插件，并设置满系营销信息
        if (CollectionUtils.isNotEmpty(requests)) {
            MarketingTradeBatchWrapperRequest marketingTradeBatchWrapperRequest = MarketingTradeBatchWrapperRequest.builder()
                    .wraperDTOList(requests)
                    .build();
            List<TradeMarketingWrapperVO> voList = marketingQueryProvider.batchWrapper(marketingTradeBatchWrapperRequest).getContext().getWraperVOList();
            if (CollectionUtils.isNotEmpty(voList)) {
                voList.forEach(tradeMarketingWrapperVO -> {
                    tradeMarketings.add(tradeMarketingWrapperVO.getTradeMarketing());
                });
            }
        }
        log.info("调用营销插件后算出来的数据" + tradeMarketings);
        return tradeMarketings;
    }

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     */
    public TradeGetGoodsResponse getGoodsResponseMatchFlag(List<String> skuIds, Long wareId, String wareHouseCode, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new TradeGetGoodsResponse();
        }
        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(skuIds)
                .isHavSpecText(Constants.yes)
                .wareId(wareId)
                .wareHouseCode(wareHouseCode)
                .matchWareHouseFlag(matchWareHouseFlag)
                .build();
        GoodsInfoViewByIdsResponse response = goodsInfoQueryProvider.listViewByIdsByMatchFlag(goodsInfoRequest).getContext();
        TradeGetGoodsResponse goodsResponse = new TradeGetGoodsResponse();
        goodsResponse.setGoodses(response.getGoodses());
        goodsResponse.setGoodsInfos(response.getGoodsInfos());
        return goodsResponse;
    }

    public OrderInvoiceSaveRequest buildOrderInvoiceSaveRequest(NewPileTrade trade) {
        Invoice invoice;
        if ((invoice = trade.getInvoice()) == null || trade.getInvoice().getType() == -1) {
            return null;
        }
        boolean isGeneral = invoice.getType() == 0;
        OrderInvoiceSaveRequest request = new OrderInvoiceSaveRequest();
        request.setCustomerId(trade.getBuyer().getId());
        if (Objects.nonNull(invoice.getAddress())) {
            request.setInvoiceAddress(trade.getInvoice().getContacts() + " " + trade.getInvoice().getPhone() + " " +
                    invoice.getAddress());
        } else {
            request.setInvoiceAddress(trade.getBuyer().getName() + " " + trade.getBuyer().getPhone() + " " + trade
                    .getConsignee().getDetailAddress());
        }
        request.setInvoiceTitle(isGeneral ? invoice.getGeneralInvoice().getFlag() == 0 ? null : invoice
                .getGeneralInvoice().getTitle()
                : invoice.getSpecialInvoice().getCompanyName());

        request.setInvoiceType(InvoiceType.NORMAL.fromValue(invoice.getType()));
        request.setOrderNo(trade.getId());
        request.setProjectId(invoice.getProjectId());
        request.setOrderInvoiceId(invoice.getOrderInvoiceId());
        request.setCompanyInfoId(trade.getSupplier().getSupplierId());
        request.setStoreId(trade.getSupplier().getStoreId());
        return request;
    }

    /**
     * 组装自提码参数
     */
    public PickUpRecord sendPickUpCode(Trade trade) {
        String verifyCode = RandomStringUtils.randomNumeric(6);
        return PickUpRecord.builder().storeId(trade.getSupplier().getStoreId())
                .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build();
    }


    /**
     * 在加入延时队列前先插入一条支付数据
     *
     * @param trade
     * @param payOrders
     * @param payWay
     */
    public void savePayOrder(Trade trade, PayOrderResponse payOrders, PayWay payWay) {
        logger.info("TradeService.savePayOrder req PayOrderId:{} payType:{} PayOrderRealPayPrice:{}", trade.getParentId(), payOrders.getPayType(), payOrders.getPayOrderRealPayPrice());
        //查询支付记录中是否有支付单
        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(payOrders.getPayOrderId());
        if (number == 0) {
            TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
            tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
            tradePushKingdeePayOrder.setOrderCode(trade.getId());
            tradePushKingdeePayOrder.setPayType(payWay.toValue());
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
            tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
            tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
            if (payOrders.getPayType().equals(PayType.OFFLINE)) {
                tradePushKingdeePayOrder.setPracticalPrice(payOrders.getTotalPrice());
            } else {
                tradePushKingdeePayOrder.setPracticalPrice(payOrders.getTotalPrice());
            }
            tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
        }
    }


    /**
     * 订单支付后，发送MQ消息
     *
     * @param trade
     */
    public void sendMQForOrderPayed(Trade trade) {
        // trade对象转tradeVO对象
        TradeVO tradeVO = KsBeanUtil.convert(trade, TradeVO.class);
        orderProducerService.sendMQForOrderPayed(tradeVO);

        String customerId = trade.getBuyer().getId();
        String pic = trade.getTradeItems().get(0).getPic();
        String account = trade.getBuyer().getAccount();

        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        map.put("node", OrderProcessType.ORDER_PAY_SUCCESS.toValue());
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setNodeCode(OrderProcessType.ORDER_PAY_SUCCESS.getType());
        messageMQRequest.setParams(Lists.newArrayList(trade.getTradeItems().get(0).getSkuName()));
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(customerId);
        messageMQRequest.setPic(pic);
        messageMQRequest.setMobile(account);
        orderProducerService.sendMessage(messageMQRequest);

        //推广订单节点触发
        if (trade.getDistributorId() != null) {
            map.put("type", NodeType.DISTRIBUTION.toValue());
            map.put("node", DistributionType.PROMOTE_ORDER_PAY_SUCCESS.toValue());
            List<String> params = Lists.newArrayList(trade.getDistributorName(),
                    trade.getTradeItems().get(0).getSkuName(),
                    trade.getCommission().toString());
            this.sendMessage(NodeType.DISTRIBUTION, DistributionType.PROMOTE_ORDER_PAY_SUCCESS, params,
                    map, trade.getInviteeId(), pic, account);
        }
    }


    /**
     * 发送消息
     *
     * @param nodeType
     * @param nodeCode
     * @param params
     * @param routeParam
     * @param customerId
     */
    private void sendMessage(NodeType nodeType, DistributionType nodeCode, List<String> params,
                             Map<String, Object> routeParam, String customerId, String pic, String mobile) {
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeCode(nodeCode.getType());
        messageMQRequest.setNodeType(nodeType.toValue());
        messageMQRequest.setParams(params);
        messageMQRequest.setRouteParam(routeParam);
        messageMQRequest.setCustomerId(customerId);
        messageMQRequest.setPic(pic);
        messageMQRequest.setMobile(mobile);
        orderProducerService.sendMessage(messageMQRequest);
    }

    /**
     * 发送购买指定商品赠券MQ
     *
     * @param tid
     */
    public void buyGoodsSendCoupon(String tid) {
        log.info("===================>购买指定商品赠券MQ发送开始，tid：：：{}", tid);
        orderProducerService.buyGoodsSendCoupon(tid);
        log.info("===================>购买指定商品赠券MQ发送完成，tid：：：{}", tid);
    }

    /**
     * 热销排行榜 MQ
     *
     * @param tid
     */
    public void hotSaleArea(String tid) {
        log.info("===================>热销排行榜MQ发送开始，tid：：：{}", tid);
        orderProducerService.hotSaleArea(tid);
        log.info("===================>热销排行榜MQ发送完成，tid：：：{}", tid);
    }

    /**
     * 设置退货数量
     *
     * @param returnSkuNum
     * @param boughtSkuNum
     */
    public void setReturnNum(Map<String, BigDecimal> returnSkuNum, Map<String, TradeItem> boughtSkuNum) {
        boughtSkuNum.forEach((key, value) -> {
            BigDecimal returnNum = returnSkuNum.get(key);
            if (Objects.isNull(returnNum)) {
                returnSkuNum.put(key, value.getBNum());
            } else {
                returnSkuNum.put(key, value.getBNum());
            }
        });
    }

    private void orderMarketingNewPile(List<NewPileTrade> trades) {
        trades.forEach(trade -> {
            if (SaleType.WHOLESALE.equals(trade.getSaleType())) {
                BigDecimal tradePrice = trade.getTradePrice().getTotalPrice();//满订单优惠前价格
                //8.2.1 计算满订单优惠
                PurchaseOrderMarketingResponse context;
                /**正常购物车*/
                context = shopCartNewPileQueryProvider.getOrderMarketings(PurchaseOrderMarketingRequest.builder()
                        .goodsTotalNum(trade.getTradeItems().stream().map(TradeItem::getNum).reduce((sum, item) -> {
                            sum = sum + item;
                            return sum;
                        }).orElse(0l)).totalPrice(trade.getTradePrice().getTotalPrice()).build()).getContext();

                if (Objects.nonNull(context)) {
                    BigDecimal newTradePrice = context.getTotalPrice();
                    trade.getTradePrice().setTotalPrice(newTradePrice);
                    List<String> goodsInfoIds = new ArrayList<>();
                    goodsInfoIds.add(Constant.FULL_GIT_ORDER_GOODS);
                    Map<String, List<MarketingVO>> listMap = marketingQueryProvider.getOrderMarketingMap(MarketingMapGetByGoodsIdRequest.builder()
                            .goodsInfoIdList(goodsInfoIds)
                            .deleteFlag(DeleteFlag.NO)
                            .cascadeLevel(true)
                            .marketingStatus(MarketingStatus.STARTED)
                            .build()).getContext().getListMap();
                    if (Objects.nonNull(listMap)) {
                        List<MarketingVO> marketingViewVOS = listMap.get(Constant.FULL_GIT_ORDER_GOODS);
                        if (!CollectionUtils.isEmpty(marketingViewVOS)) {
                            MarketingVO orderByMarketingVO = marketingViewVOS.stream().findFirst().orElse(null);
                            trade.getTradeMarketings().add(TradeMarketingVO.builder()
                                    .marketingId(orderByMarketingVO.getMarketingId())
                                    .marketingName(orderByMarketingVO.getMarketingName())
                                    .marketingType(orderByMarketingVO.getMarketingType())
                                    .subType(orderByMarketingVO.getSubType())
                                    .discountsAmount(context.getDiscountsTotalOrderPrice())
                                    //.realPayAmount()
                                    .isOverlap(orderByMarketingVO.getIsOverlap())
                                    .build());
                        }
                    }
                    //计算平摊价格
                    tradeItemService.calcSplitPrice(trade.getTradeItems(), newTradePrice, tradePrice);
                }
            }
        });
    }

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param trade
     */
    @MongoRollback(persistence = NewPileTrade.class, operation = Operation.ADD)
    public void addTrade(NewPileTrade trade) {
        logger.info("newPileTradeService.addTrade trade:{}", trade);
        newPileTradeRepository.save(trade);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param trade
     */
    @MongoRollback(persistence = NewPileTrade.class, operation = Operation.UPDATE)
    public void updateTrade(NewPileTrade trade) {
        newPileTradeRepository.save(trade);
    }


    /**
     * 余额修改
     *
     * @param trades
     * @param increaseDeductionFlag true增加 false扣减
     */
    private void modifyWalletBalance(List<NewPileTrade> trades, Boolean increaseDeductionFlag) {
        boolean b = trades.stream().anyMatch(trade -> trade.getTradePrice().getBalancePrice() != null && trade.getTradePrice().getBalancePrice().compareTo(BigDecimal.ZERO) > 0);
        if (!b) {
            return;
        }
        NewPileTrade tradeTem = trades.stream().filter(trade -> trade.getBuyer() != null && trade.getBuyer().getId() != null).findFirst().orElseThrow(() -> new SbcRuntimeException("没有找到购买人信息, 请重新检查"));
        //用户余额信息
        com.wanmi.sbc.account.bean.vo.CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(tradeTem.getBuyer().getId()).build())
                .getContext().getCustomerWalletVO();

        log.info("=====订单信息=====>> trades:{}", JSONObject.toJSONString(trades));
        log.info("=====余额信息=====>> customerWalletVO:{}", JSONObject.toJSONString(customerWalletVO));

        // 订单总金额
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (NewPileTrade trade : trades) {
            List<TradeItem> tradeItems = trade.getTradeItems();
            TradePrice tradePrice = trade.getTradePrice();
            Buyer buyer = trade.getBuyer();

            if (!(tradePrice.getBalancePrice() != null && tradePrice.getBalancePrice().compareTo(BigDecimal.ZERO) > 0)) {
                return;
            }

            totalPrice = totalPrice.add(tradePrice.getBalancePrice());
            /*
            AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();

            //交易备注
            String tradeRemark = !increaseDeductionFlag ? WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId() : WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc() + "-" + trade.getId();
            //备注
            String remark = !increaseDeductionFlag ? WalletDetailsType.DEDUCTION_ORDER_DEDUCTION.getDesc() + "-" + trade.getId() : WalletDetailsType.INCREASE_ORDER_CANCEL.getDesc() + "-" + trade.getId();
            //交易类型【1充值，2提现，3余额支付】
            WalletRecordTradeType tradeType = !increaseDeductionFlag ? WalletRecordTradeType.BALANCE_PAY : WalletRecordTradeType.RECHARGE;
            //收支类型 [1 收入, 2支出]
            BudgetType budgetType = !increaseDeductionFlag ? BudgetType.EXPENDITURE : BudgetType.INCOME;
            //订单类型
            String activityType = trade.getActivityType();

            request.setTradeRemark(tradeRemark);
            request.setCustomerAccount(buyer.getAccount());
            request.setRelationOrderId(trade.getId());
            request.setTradeType(tradeType);
            request.setBudgetType(budgetType);
            request.setDealPrice(trade.getTradePrice().getBalancePrice());
            request.setRemark(remark);
//                request.setDealTime(LocalDateTime.now());
            request.setCurrentBalance(customerWalletVO.getBalance());
            request.setTradeState(TradeStateEnum.PAID);
            request.setPayType(1);
            request.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getBalancePrice()));
            request.setActivityType(activityType);

            BaseResponse<AddWalletRecordResponse> result = walletRecordProvider.addWalletRecord(request);
            if (!result.getCode().equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }*/

            //入驻商家新鲸币抵扣/充值业务    @jkp
            if (tradePrice.getBalancePrice().compareTo(BigDecimal.ZERO) > 0) {
                String balanceResultCode = "";
                if (!increaseDeductionFlag) {
                    //扣除余额
                    CustomerWalletOrderByRequest walletRequest = CustomerWalletOrderByRequest.builder()
                            .customerId(buyer.getId())
                            .customerAccount(buyer.getAccount())
                            .balance(tradePrice.getBalancePrice())
                            .storeId(tradeItems.get(0).getStoreId().toString())
                            .relationOrderId(trade.getId())
                            .remark("鲸币抵扣")
                            .tradeRemark("鲸币抵扣-"+trade.getId())
                            .dealTime(LocalDateTime.now())
                            .walletRecordTradeType(com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType.DEDUCTION)
                            .build();
                    balanceResultCode = walletCustomerWalletProvider.orderByGiveStore(walletRequest).getCode();
                }
                if (increaseDeductionFlag) {
                    //增加余额
                    CustomerWalletGiveRequest giveRequest = CustomerWalletGiveRequest.builder()
                            .customerId(buyer.getId())
                            .customerAccount(buyer.getAccount())
                            .balance(tradePrice.getBalancePrice())
                            .storeId(tradeItems.get(0).getStoreId().toString())
                            .relationOrderId(trade.getId())
                            .remark("商品返鲸币退还")
                            .tradeRemark("商品返鲸币退还-"+trade.getId())
                            .dealTime(LocalDateTime.now())
                            .opertionType(0)
                            .walletRecordTradeType(com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType.ORDER_CASH_BACK)
                            .build();

                    balanceResultCode = walletMerchantProvider.merchantGiveUser(giveRequest).getCode();
                }
                if (!balanceResultCode.equals(ResultCode.SUCCESSFUL)) {
                    throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
                }
            }
        }

        /*if (totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            Buyer buyer = tradeTem.getBuyer();

            WalletRequest walletRequest = WalletRequest.builder()
                    .customerId(buyer.getId())
                    .expenseAmount(totalPrice)
                    .customerAccount(buyer.getAccount()).build();

            String balanceResultCode = "";
            if (!increaseDeductionFlag) {
                //扣除余额
                balanceResultCode = customerWalletProvider.balancePay(walletRequest).getCode();
            }
            if (increaseDeductionFlag) {
                //增加余额
                balanceResultCode = customerWalletProvider.addAmount(walletRequest).getCode();
            }
            if (!balanceResultCode.equals(ResultCode.SUCCESSFUL)) {
                throw new SbcRuntimeException("余额执行操作失败, 请重新操作! ");
            }
        }*/
    }

    /**
     * 查询全部订单
     *
     * @param request
     * @return
     */
    public List<NewPileTrade> queryAll(TradeQueryRequest request) {
        return mongoTemplate.find(new Query(request.getWhereCriteria()), NewPileTrade.class);
    }

    /**
     * 更新订单
     *
     * @param tradeUpdateRequest
     */
    @LcnTransaction
    public void updateTradeInfo(NewPileTradeUpdateRequest tradeUpdateRequest) {
        updateTrade(KsBeanUtil.convert(tradeUpdateRequest.getTrade(), NewPileTrade.class));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * auditAction 创建支付单
     *
     * @param trade trade
     */
    private void createPayOrder(NewPileTrade trade) {
        if (trade.getPayOrderId() != null) {
            payOrderService.deleteByPayOrderId(trade.getPayOrderId());
            receivableService.deleteReceivables(Collections.singletonList(trade.getPayOrderId()));
        }
        //创建支付单
        Optional<PayOrder> optional = payOrderService.generatePayOrderByOrderCode(
                new PayOrderGenerateRequest(trade.getId(),
                        trade.getBuyer().getId(),
                        trade.getTradePrice().getTotalPrice(),
                        trade.getTradePrice().getPoints(),
                        PayType.valueOf(trade.getPayInfo().getPayTypeName()),
                        trade.getSupplier().getSupplierId(),
                        trade.getTradeState().getCreateTime(),
                        trade.getOrderType()));

        trade.getTradeState().setPayState(PayState.NOT_PAID);
        optional.ifPresent(payOrder -> trade.setPayOrderId(payOrder.getPayOrderId()));
    }

    /**
     * 功能描述: <br> 订单推送WMS
     * 〈〉
     *
     * @Param: [trade, warehouseId]
     * @Return: java.lang.Boolean
     * @Date: 2020/5/18 11:24
     */
    public void pushWMSOrder(NewPileTrade trade, Boolean hasGroupedFlag) {
        WMSPushOrderRequest wmsPushOrderRequest = new WMSPushOrderRequest();
        String jobNo = WmsErpIdConstants.ERP_CUSTOMER_ID;
        if (StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId())) {
            EmployeeByIdResponse response = employeeQueryProvider
                    .getById(EmployeeByIdRequest.builder().employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(response) && Objects.nonNull(response.getJobNo())) {
                jobNo = response.getJobNo();
            }
        }
        List<WMSPushOrderDetailsRequest> orderDetails = new ArrayList<>(100);
        AtomicInteger lineNo = new AtomicInteger(1);
        //组装订单详情参数
        List<TradeItem> allItems = new ArrayList<>();
        allItems.addAll(trade.getTradeItems());
        allItems.addAll(trade.getGifts());
        allItems.forEach(param -> {
            //子单的erp 仓库编码
            String loatt04 = ERPWMSConstants.MAIN_WH;
            if ("WH01".equals(trade.getWareHouseCode())) {
                if (Objects.nonNull(param.getGoodsInfoType()) && 1 == param.getGoodsInfoType()) {
                    loatt04 = ERPWMSConstants.MAIN_MARKETING_WH;
                } else {
                    loatt04 = ERPWMSConstants.MAIN_WH;
                }
            }
            if ("WH02".equals(trade.getWareHouseCode())) {
                if (Objects.nonNull(param.getGoodsInfoType()) && 1 == param.getGoodsInfoType()) {
                    loatt04 = ERPWMSConstants.SUB_MARKETING_WH;
                } else {
                    loatt04 = ERPWMSConstants.SUB_WH;
                }
            }
            BigDecimal addStep = param.getAddStep().setScale(2,
                    BigDecimal.ROUND_HALF_UP);
//            BigDecimal itemPrice = param.getPrice();
            /* BigDecimal spliteItemPrice = itemPrice.divide(addStep,2,BigDecimal.ROUND_HALF_UP);*/
            orderDetails.add(WMSPushOrderDetailsRequest.builder()
                    .sku(param.getErpSkuNo())
                    //todo 注意有基础单位的换算
                    .qtyOrdered(BigDecimal.valueOf(param.getNum()).multiply(addStep).setScale(2,
                            BigDecimal.ROUND_HALF_UP))
                    .qtyOrdered_each(BigDecimal.valueOf(param.getNum()).multiply(addStep).setScale(2,
                            BigDecimal.ROUND_HALF_UP))
                    .lineNo(lineNo.get())
                    .customerId("XYY")
                    //todo 注意这边的价格后期可能要调整
                    .price(param.getSplitPrice())
                    .userDefine1(param.getGoodsInfoType())
                    .userDefine5(param.getSkuNo())
                    .lotatt04(loatt04)
                    .build());
            lineNo.incrementAndGet();
        });
        Supplier supplier = trade.getSupplier();
        //组装订单参数
        wmsPushOrderRequest.setWarehouseId(trade.getWareHouseCode());
        //商家在erp开户ID
        wmsPushOrderRequest.setCustomerId(supplier.getErpId() != null ? supplier.getErpId() : "XYY");
        //固定值
        wmsPushOrderRequest.setOrderType(orderCommonService.getWmsDocType(trade));
        //来源
        wmsPushOrderRequest.setSoReferenceA("001");
        //订单号
        wmsPushOrderRequest.setDocNo(trade.getId());
        //固定值
        wmsPushOrderRequest.setAddWho("DS");
        //固定值
        wmsPushOrderRequest.setSoReferenceD(orderCommonService.getWmsSoReference5(trade));
        //订单释放状态 --- 如果先款后货的传N 如果是不限的传Y
        wmsPushOrderRequest.setPriority(PaymentOrder.PAY_FIRST.equals(trade.getPaymentOrder()) ? "N" : "Y");
        //如果是拼团成功的订单也直接存Y
        if (hasGroupedFlag) {
            wmsPushOrderRequest.setPriority("Y");
        }
        //订单创建时间
        wmsPushOrderRequest.setOrderTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1));
        //收货人的erpId
        wmsPushOrderRequest.setConsigneeId(trade.getBuyer().getCustomerErpId());
        // wmsPushOrderRequest.setConsigneeId(trade.getBuyer().getAccount());
        //收货人名称
        wmsPushOrderRequest.setConsigneeName(trade.getConsignee().getName());
        //收货人联系人名称
        wmsPushOrderRequest.setConsigneeTel2(trade.getConsignee().getName());
        //收货人电话
        wmsPushOrderRequest.setConsigneeTel1(trade.getConsignee().getPhone());
        //收货人的地址
        wmsPushOrderRequest.setConsigneeAddress1(trade.getConsignee().getDetailAddress());
        //线上线下
        wmsPushOrderRequest.setUserDefine1(jobNo);
        //付款标识
        wmsPushOrderRequest.setUserDefine2(PayState.PAID.equals(trade.getTradeState().getPayState()) ? "2" : "0");
        wmsPushOrderRequest.setUserDefine3(supplier.getStoreName());
        //预约发货时间
        wmsPushOrderRequest.setExpectedShipmentTime1(Objects.isNull(trade.getBookingDate()) ? null : trade.getBookingDate().toString() + " 00:00:00");
        //业务员
        wmsPushOrderRequest.setUserDefine6(jobNo);
        //备注
        wmsPushOrderRequest.setNotes(trade.getBuyerRemark());
        //运送方式
        wmsPushOrderRequest.setCarrierMail(switchWMSPushOrderType(trade.getDeliverWay()));
        //明细
        wmsPushOrderRequest.setDetails(orderDetails);
        if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo())) {
            if (Objects.nonNull(trade.getLogisticsCompanyInfo().getCompanyNumber())) {
                wmsPushOrderRequest.setCarrierId(trade.getLogisticsCompanyInfo().getCompanyNumber());
            } else {
                String uuid = UUIDUtil.getUUID().substring(29);
                wmsPushOrderRequest.setCarrierId("customer-" + uuid);
            }
            wmsPushOrderRequest.setCarrierName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
            wmsPushOrderRequest.setCarrierFax(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
            wmsPushOrderRequest.setUserDefine5(trade.getLogisticsCompanyInfo().getLogisticsAddress());
            wmsPushOrderRequest.setUserDefine4(trade.getLogisticsCompanyInfo().getReceivingPoint());//增加收货站点
        }
        boolean noLimitFlag = AuditState.CHECKED.equals(trade.getTradeState().getAuditState()) && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        //wmsPushOrderRequest.setUserDefine3(noLimitFlag ? "0" : "1");
        wmsPushOrderRequest.setUserDefine3(supplier.getStoreName());
        ResponseWMSReturnResponse response = requestWMSOrderProvider.putSalesOrder(wmsPushOrderRequest).getContext();
        if (Objects.isNull(response)
                || Objects.isNull(response.getResponseWMSReturnVO())
                || Objects.isNull(response.getResponseWMSReturnVO().getReturnFlag())
                || response.getResponseWMSReturnVO().getReturnFlag() < 1) {
            throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_TIME_OUT);
        }
    }

    public void sendPickUpMessage(NewPileTrade trade) {
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        messageMQRequest.setNodeCode(OrderProcessType.CUSTOMER_PICK_UP_PAY_SUCCESS.getType());
        map.put("node", OrderProcessType.CUSTOMER_PICK_UP_PAY_SUCCESS.toValue());
        List<String> param = new ArrayList<>(2);
        param.add(trade.getId());
        param.add(trade.getTradeWareHouse().getPickUpCode());
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setParams(param);
        messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setMobile(trade.getConsignee().getPhone());
        orderProducerService.sendMessage(messageMQRequest);
    }

    public List<NewPileTrade> listByPileNos(List<String> pileNos) {
        return newPileTradeRepository.findListByIdIn(pileNos);
    }


    /**
     * 向金蝶push支付单
     *
     * @param trade
     * @param payOrders
     */
    @Transactional
    public Boolean pushPayOrderKingdee(NewPileTrade trade, PayOrderResponse payOrders, PayWay payWay) {
        //查询是否为线下订单，是重新赋实付金额
        PayOrder pay = payOrderRepository.findById(trade.getPayOrderId())
                .orElseThrow(() -> new SbcRuntimeException("K-070001"));
        if (Objects.nonNull(pay) && pay.getPayType() == PayType.OFFLINE
                && Objects.nonNull(pay.getPayOrderRealPayPrice()) && pay.getPayOrderRealPayPrice().compareTo(BigDecimal.ZERO) == 1) {
            payOrders.setTotalPrice(pay.getPayOrderRealPayPrice());
        }
        log.info("NewPileTradeService.pushPayOrderKingdee ActivityType:{} TotalPrice:{} tid:{}", trade.getActivityType(), payOrders.getTotalPrice(), trade.getId());
        if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
            if (StringUtils.isNotEmpty(trade.getLogistics()) && trade.getLogistics().equals("02")) {
                orderProducerService.kingdeePushOrder(trade.getId(), 10 * 1000L);
                return false;
            }
        }
        BigDecimal price = BigDecimal.ZERO;
//        BigDecimal deliveryPrice = BigDecimal.ZERO;
        if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType()) && payOrders.getTotalPrice().compareTo(BigDecimal.ZERO) == 1) {
//            BigDecimal totalPrice = trade.getTradeItems().stream().map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//            deliveryPrice = totalPrice.add(payOrders.getTotalPrice());
            price = payOrders.getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
        } else if (payOrders.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
            //运费为0 且不为物流
            logger.info("NewPileTradeService.pushPayOrderKingdee not push kingdee id:{}", trade.getId());
            return false;
        } else {
            price = payOrders.getTotalPrice().setScale(6, BigDecimal.ROUND_DOWN);
        }

        // 建行支付要佣金和商家金额 要分开推送erp 不分开推了 230807
        /*BigDecimal commission = BigDecimal.ZERO;
        if (Objects.equals(payWay, PayWay.CCB)) {
            String tradeId = trade.getId();
            String payOrderNo = trade.getPayOrderNo();
            CcbPayOrderRecordResponse record = ccbPayProvider.queryCcbPayOrderRecord(tradeId, payOrderNo).getContext();
            if (Objects.nonNull(record) && Objects.nonNull(record.getCommission()) && record.getCommission().compareTo(BigDecimal.ZERO) > 0) {
                price = record.getTxnAmt().setScale(6, BigDecimal.ROUND_DOWN);
                payOrders.setTotalPrice(price);
                commission = record.getCommission();
            }
        }
        if (commission.compareTo(BigDecimal.ZERO) > 0) {
            PayOrderResponse payOrdersCommission = new PayOrderResponse();
            payOrdersCommission.setPayOrderId(payOrders.getPayOrderId() + "YJ");
            payOrdersCommission.setTotalPrice(commission);
            this.pushPayOrderCommissionKingdee(trade, payOrdersCommission, payWay);
        }*/

        logger.info("NewPileTradeService.pushPayOrderKingdee req PayOrderId:{} totalPrice:{}", payOrders.getPayOrderId(),
                price);
        Boolean resultState = false;
        //查询支付记录中是否有支付单
        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(payOrders.getPayOrderId());
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            if (!checkPayOrderKingdee(trade, payOrders, payWay, tradePushKingdeePayOrder)) {
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return resultState;
            }
            KingdeePayOrder payOrder = new KingdeePayOrder();
            String fdate =  null != trade.getTradeState().getPayTime() ? DateUtil.getDate(trade.getTradeState().getPayTime()) : DateUtil.nowDate();
            // 业务时间使用惠市宝清算时间
            if (StringUtils.isNotBlank(trade.getPayOrderNo())) {
                CcbPayRecordResponse ccbPayRecordResponse = ccbPayProvider.queryCcbPayRecordByPayOrderNo(trade.getPayOrderNo()).getContext();
                if (Objects.nonNull(ccbPayRecordResponse) && Objects.nonNull(ccbPayRecordResponse.getClrgDt())) {
                    fdate =  DateUtil.getDate(DateUtil.parseDate(ccbPayRecordResponse.getClrgDt(), DateUtil.FMT_TIME_5));
                }
            }
            payOrder.setFDate(fdate);
            // payOrder.setFDate(null != trade.getTradeState().getCreateTime() ? DateUtil.getDate(trade.getTradeState().getCreateTime()) : DateUtil.nowDate());
            payOrder.setOrderNumber(trade.getId());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", trade.getBuyer().getAccount());
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);//收款组织
            //收款方式
            if (payWay.equals(PayWay.CASH)) {
                payOrder.setFColType("线下");
            } else {
                payOrder.setFColType("线上");
            }

            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
            Map FSETTLETYPEID = new HashMap();
            List<Receivable> receivableList = receivableRepository.findByDelFlagAndPayOrderId(DeleteFlag.NO, trade.getPayOrderId());
            if (payWay.equals(PayWay.CASH)) {
                //线下支付
                FSETTLETYPEID.put("FNumber", "offlinepay");
                if (CollectionUtils.isNotEmpty(receivableList)) {
                    Receivable receivable = receivableList.stream().findFirst().orElse(null);
                    OfflineAccountGetByIdRequest offlineAccountGetByIdRequest = new OfflineAccountGetByIdRequest();
                    offlineAccountGetByIdRequest.setOfflineAccountId(receivable.getOfflineAccountId());
                    BaseResponse<OfflineAccountGetByIdResponse> offlineAccountResponse = offlineQueryProvider.getById(offlineAccountGetByIdRequest);
                    if (Objects.nonNull(offlineAccountResponse) && Objects.nonNull(offlineAccountResponse.getContext())) {
                        OfflineAccountGetByIdResponse offlineAccount = offlineAccountResponse.getContext();
                        if (StringUtils.isNotBlank(offlineAccount.getBankCode())) {
                            FSETTLETYPEID.put("FNumber", offlineAccount.getBankCode());
                        }
                    }
                }
            } else {
                FSETTLETYPEID.put("FNumber", payWay.toValue());
            }
            /**
             *
             * 结算方式需要修改
             * */
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式
            payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());

            //销售订单号
            payOrderSettlement.setF_ora_YDDH(trade.getId());

            log.info("囤货收款单推金蝶----->" + JSONObject.toJSONString(trade.getPayWay()));
            //使用银行卡,先下支付不用传银行卡，是先下支付trade.getPayWay()为空
            Map FACCOUNTID = new HashMap();
            if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", kingdeeUnionpay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.CCB)) {
                FACCOUNTID.put("FNumber", kingdeeBocomPay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            }
            //预收款   SFKYT01_SYS //正常收款
            Map FPURPOSEID = new HashMap();
            FPURPOSEID.put("FNumber", "SFKYT02_SYS");
            payOrderSettlement.setFPURPOSEID(FPURPOSEID);
            //线下支付将收款银行信息推到金蝶
            if (Objects.nonNull(pay) && pay.getPayType().equals(PayType.OFFLINE)) {
                if (CollectionUtils.isNotEmpty(receivableList)) {
                    Receivable receivable = receivableList.stream().findFirst().orElse(null);
                    OfflineAccountGetByIdRequest offlineAccountGetByIdRequest = new OfflineAccountGetByIdRequest();
                    offlineAccountGetByIdRequest.setOfflineAccountId(receivable.getOfflineAccountId());
                    BaseResponse<OfflineAccountGetByIdResponse> offlineAccountResponse = offlineQueryProvider.getById(offlineAccountGetByIdRequest);
                    if (Objects.nonNull(offlineAccountResponse) && Objects.nonNull(offlineAccountResponse.getContext())) {
                        OfflineAccountGetByIdResponse offlineAccount = offlineAccountResponse.getContext();
                        if (StringUtils.isNotBlank(offlineAccount.getBankCode())) {
                            FACCOUNTID.put("FNumber", offlineAccount.getBankNo());
                        }
                    }
                }
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            }
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                log.info("收款单推金蝶---实际推送内容-->" + JSONObject.toJSONString(payOrder));
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(payUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                logger.info("TradeService.pushPayOrderKingdee result1:{} code:{}", httpCommonResult.getResultData(), kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    resultState = true;
                } else {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeePayOrder.setInstructions(httpCommonResult.getResultData());
            } else {
                logger.error("TradeService.pushPayOrderKingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeePayOrder.setInstructions(res);
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("TradeService.pushPayOrderKingdee error:{}", e);
            String res = "金蝶推送失败";
            tradePushKingdeePayOrder.setInstructions(res);
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            if (number == 0) {
                tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
                tradePushKingdeePayOrder.setOrderCode(trade.getId());
                tradePushKingdeePayOrder.setPayType(payWay.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                if (payOrders.getPayType().equals(PayType.OFFLINE)) {
                    tradePushKingdeePayOrder.setPracticalPrice(payOrders.getPayOrderRealPayPrice());
                } else {
                    tradePushKingdeePayOrder.setPracticalPrice(payOrders.getTotalPrice());
                }
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
            return resultState;
        }
    }

    public void pushPayOrderCommissionKingdee(NewPileTrade trade, PayOrderResponse payOrders, PayWay payWay, String fdate, String companyCodeNew, String suffix) {
        log.info("收款单推佣金送金蝶 单号:{},佣金:{}", trade.getId(), payOrders.getTotalPrice());

        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(payOrders.getPayOrderId());
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        String tid = trade.getId() + suffix;
        try {
            if (!checkPayOrderKingdee(trade, payOrders, payWay, tradePushKingdeePayOrder)) {
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
            }
            KingdeePayOrder payOrder = new KingdeePayOrder();
            payOrder.setFDate(StringUtils.isNotBlank(fdate) ? fdate : DateUtil.nowDate());
            payOrder.setOrderNumber(tid);
            Map FCustId = new HashMap();

            log.info("收款单推佣金送金蝶 单号:{},商家编码:{}", tid, companyCodeNew);

            FCustId.put("FNumber", companyCodeNew);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);// 收款组织
            // 收款方式
            payOrder.setFColType("线上");

            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
            Map FSETTLETYPEID = new HashMap();
            FSETTLETYPEID.put("FNumber", payWay.toValue());

            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);// 结算方式
            payOrderSettlement.setFRECTOTALAMOUNTFOR(payOrders.getTotalPrice().toString());

            // 销售订单号
            payOrderSettlement.setF_ora_YDDH(tid);

            Map FACCOUNTID = new HashMap();
            if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", kingdeeUnionpay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.CCB)) {
                FACCOUNTID.put("FNumber", kingdeeCcbpay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            }

            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);

            // 登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                // 提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                log.info("收款佣金单推金蝶---实际推送内容-->" + JSONObject.toJSONString(payOrder));
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(payUrl, requestMap, loginToken);

                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushPayOrderCommissionKingdee result1:{} code:{}", httpCommonResult.getResultData(), kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeePayOrder.setInstructions(httpCommonResult.getResultData());
            } else {
                log.error("TradeService.pushPayOrderCommissionKingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeePayOrder.setInstructions(res);
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            log.error("TradeService.pushPayOrderCommissionKingdee error:", e);
            String res = "金蝶推送失败";
            tradePushKingdeePayOrder.setInstructions(res);
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            if (number == 0) {
                tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
                tradePushKingdeePayOrder.setOrderCode(tid);
                tradePushKingdeePayOrder.setPayType(payWay.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPracticalPrice(payOrders.getTotalPrice());
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
        }
    }


    public void pushPayOrderFreightKingdee(NewPileTrade trade, PayOrderResponse payOrders,PayWay payWay, String fdate) {

        log.info("收款单推运费送金蝶 单号:{},运费:{}", trade.getId(), payOrders.getTotalPrice());

        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(payOrders.getPayOrderId());
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        String tid = trade.getId() + "YF";
        try {
            if (!checkPayOrderKingdee(trade, payOrders, payWay, tradePushKingdeePayOrder)) {
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
            }
            KingdeePayOrder payOrder = new KingdeePayOrder();
            payOrder.setFDate(StringUtils.isNotBlank(fdate) ? fdate : DateUtil.nowDate());
            payOrder.setOrderNumber(tid);
            Map FCustId = new HashMap();

            Long supplierId = trade.getSupplier().getSupplierId();
            String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(supplierId).build()).getContext().getCompanyCodeNew();
            log.info("收款单推运费送金蝶 单号:{},商家编码:{}", tid, companyCodeNew);

            FCustId.put("FNumber", companyCodeNew);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);//收款组织
            //收款方式
            payOrder.setFColType("线上");

            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
            Map FSETTLETYPEID = new HashMap();
            FSETTLETYPEID.put("FNumber", payWay.toValue());

            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式
            payOrderSettlement.setFRECTOTALAMOUNTFOR(payOrders.getTotalPrice().toString());

            //销售订单号
            payOrderSettlement.setF_ora_YDDH(tid);

            Map FACCOUNTID = new HashMap();
            if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", kingdeeUnionpay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (trade.getPayWay() != null && trade.getPayWay().equals(PayWay.CCB)) {
                FACCOUNTID.put("FNumber", kingdeeCcbpay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            }

            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                log.info("收款佣金单推金蝶---实际推送内容-->" + JSONObject.toJSONString(payOrder));
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(payUrl, requestMap, loginToken);

                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushPayOrderCommissionKingdee result1:{} code:{}", httpCommonResult.getResultData(), kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeePayOrder.setInstructions(httpCommonResult.getResultData());
            } else {
                log.error("TradeService.pushPayOrderCommissionKingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeePayOrder.setInstructions(res);
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            log.error("TradeService.pushPayOrderCommissionKingdee error:", e);
            String res = "金蝶推送失败";
            tradePushKingdeePayOrder.setInstructions(res);
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            if (number == 0) {
                tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
                tradePushKingdeePayOrder.setOrderCode(tid);
                tradePushKingdeePayOrder.setPayType(payWay.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPracticalPrice(payOrders.getTotalPrice());
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(payOrders.getPayOrderId());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
        }
    }

    /**
     * 校验支付参数
     *
     * @param trade
     * @param payOrder
     * @param payWay
     * @param tradePushKingdeePayOrder
     * @return
     */
    private Boolean checkPayOrderKingdee(NewPileTrade trade, PayOrderResponse payOrder, PayWay payWay, TradePushKingdeePayOrder tradePushKingdeePayOrder) {
        if (trade.getId() == null) {
            logger.info("NewPileTradeService.pushPayOrderKingdee Lack tId");
            tradePushKingdeePayOrder.setInstructions("Lack tId");
            return false;
        }
        if (StringUtils.isEmpty(trade.getBuyer().getAccount())) {
            logger.info("NewPileTradeService.pushPayOrderKingdee Lack FCustId");
            tradePushKingdeePayOrder.setInstructions("Lack FCustId");
            return false;
        }
        if (payWay == null) {
            logger.info("NewPileTradeService.pushPayOrderKingdee Lack payWay");
            tradePushKingdeePayOrder.setInstructions("Lack payWay");
            return false;
        }
//        if (trade.getPayWay() == null){
//            logger.info("TradeService.pushPayOrderKingdee Lack FSETTLETYPEID");
//            tradePushKingdeePayOrder.setInstructions("Lack FSETTLETYPEID");
//            return false;
//        }
        if (payOrder.getTotalPrice() == null) {
            logger.info("NewPileTradeService.pushPayOrderKingdee Lack FRECTOTALAMOUNTFOR");
            tradePushKingdeePayOrder.setInstructions("Lack FRECTOTALAMOUNTFOR");
            return false;
        }
        return true;
    }

    /**
     * 查 parentId 对于订单集合
     *
     * @param parentId
     */
    public NewPileTrade detailByParentId(String parentId) {
        List<NewPileTrade> tradeList = newPileTradeRepository.findListByParentId(parentId);
        NewPileTrade trade = tradeList.stream().findFirst().get();
        tradeList.stream().filter(t -> !t.getId().equals(trade.getId())).forEach(
                d -> {
                    trade.getTradeItems().addAll(d.getTradeItems());
                    trade.getTradePrice().setTotalPrice(trade.getTradePrice().getTotalPrice().add(d.getTradePrice().
                            getTotalPrice()));
                }
        );
        return trade;
    }


    @Transactional
    @LcnTransaction
    public void wxPayOnlineCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) throws Exception {
        String businessId = "";
        try {
            PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                    GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, Constants.BOSS_DEFAULT_STORE_ID)).getContext();
            String apiKey = payGatewayConfig.getApiKey();
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            WxPayResultResponse wxPayResultResponse =
                    (WxPayResultResponse) xStream.fromXML(tradePayOnlineCallBackRequest.getWxPayCallBackResultStr());
            log.info("-------------微信支付回调,wxPayResultResponse：{}------------", wxPayResultResponse);
            //判断当前回调是否是合并支付
            businessId = wxPayResultResponse.getOut_trade_no();
            boolean isMergePay = isMergePayOrder(businessId);
            String lockName;
            //非组合支付，则查出该单笔订单。
            if (!isMergePay) {
                NewPileTrade trade = new NewPileTrade();
                if (isTailPayOrder(businessId)) {
                    trade = queryAll(TradeQueryRequest.builder().tailOrderNo(businessId).build()).get(0);
                } else {
                    trade = detail(businessId);
                }
                // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                lockName = trade.getParentId();
            } else {
                lockName = businessId;
            }
            //redis锁，防止同一订单重复回调
            RLock rLock = redissonClient.getFairLock(lockName);
            rLock.lock();
            //执行回调
            try {
                //支付回调事件成功
                if (wxPayResultResponse.getReturn_code().equals(WXPayConstants.SUCCESS) &&
                        wxPayResultResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
                    log.info("微信支付异步通知回调状态---成功");
                    //微信回调参数数据map
                    Map<String, String> params =
                            WXPayUtil.xmlToMap(tradePayOnlineCallBackRequest.getWxPayCallBackResultXmlStr());
                    String trade_type = wxPayResultResponse.getTrade_type();
                    //app支付回调对应的api key为开放平台对应的api key
                    if (trade_type.equals("APP")) {
                        apiKey = payGatewayConfig.getOpenPlatformApiKey();
                    }
                    //微信签名校验
                    if (WXPayUtil.isSignatureValid(params, apiKey)) {
                        //签名正确，进行逻辑处理--对订单支付单以及操作信息进行处理并添加交易数据
                        List<NewPileTrade> trades = new ArrayList<>();
                        //查询交易记录
                        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                                new TradeRecordByOrderCodeRequest(businessId);
                        PayTradeRecordResponse recordResponse =
                                payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                        PayCallBackResult payCallBackResult =
                                payCallBackResultService.list(PayCallBackResultQueryRequest.builder().businessId(businessId).build()).get(0);
                        if (isMergePay) {
                            /*
                             * 合并支付
                             * 查询订单是否已支付或过期作废
                             */
                            trades = detailsByParentId(businessId);
                            //订单合并支付场景状态采样
                            boolean paid =
                                    trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);
                            boolean cancel =
                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == NewPileFlowState.VOID);
                            if (cancel || (paid && !recordResponse.getTradeNo().equals(wxPayResultResponse.getTransaction_id()))) {
                                //同一批订单重复支付或过期作废，直接退款
                                wxRefundHandle(wxPayResultResponse, businessId, -1L);
                            } else if (payCallBackResult.getResultStatus() != PayCallBackResultStatus.SUCCESS) {
                                wxPayCallbackHandle(payGatewayConfig, wxPayResultResponse, businessId, trades, true);
                            }
                        } else {
                            //单笔支付
                            NewPileTrade trade = new NewPileTrade();
                            if (isTailPayOrder(businessId)) {
                                trade =
                                        queryAll(TradeQueryRequest.builder().tailOrderNo(businessId).build()).get(0);
                            } else {
                                trade = detail(businessId);
                            }
                            if (trade.getTradeState().getFlowState() == NewPileFlowState.VOID || (trade.getTradeState()
                                    .getPayState() == PayState.PAID
                                    && !recordResponse.getTradeNo().equals(wxPayResultResponse.getTransaction_id()))) {
                                //同一批订单重复支付或过期作废，直接退款
                                wxRefundHandle(wxPayResultResponse, businessId, -1L);
                            } else if (payCallBackResult.getResultStatus() != PayCallBackResultStatus.SUCCESS) {
                                trades.add(trade);
                                wxPayCallbackHandle(payGatewayConfig, wxPayResultResponse, businessId, trades, false);
                            }
                        }

                        //支付回调处理成功
                        payCallBackResultService.updateStatus(businessId, PayCallBackResultStatus.SUCCESS);
                    } else {
                        log.info("微信支付异步回调验证签名结果[失败].");
                        //支付处理结果回写回执支付结果表
                        payCallBackResultService.updateStatus(businessId, PayCallBackResultStatus.FAILED);
                    }
                } else {
                    log.info("微信支付异步通知回调状态---失败");
                    //支付处理结果回写回执支付结果表
                    payCallBackResultService.updateStatus(businessId, PayCallBackResultStatus.FAILED);
                }
                log.info("微信支付异步通知回调end---------");
            } catch (Exception e) {
                log.error(e.getMessage());
                //支付处理结果回写回执支付结果表
                payCallBackResultService.updateStatus(businessId, PayCallBackResultStatus.FAILED);
            } finally {
                //解锁
                rLock.unlock();
            }
        } catch (Exception ex) {
            if (StringUtils.isNotBlank(businessId)) {
                payCallBackResultService.updateStatus(businessId, PayCallBackResultStatus.FAILED);
            }
            log.error(ex.getMessage());
        }
    }

    /**
     * @return boolean
     * @Author lvzhenwei
     * @Description 判断是否为主订单
     * @Date 15:36 2020/7/2
     * @Param [businessId]
     **/
    private boolean isMergePayOrder(String businessId) {
        log.info("============================= isMergePayOrder.businessId:{}============================", businessId);
        return businessId.startsWith(GeneratorService._PREFIX_PARENT_TRADE_ID);
    }

    /**
     * 是否是尾款订单号
     *
     * @param businessId
     * @return
     */
    private boolean isTailPayOrder(String businessId) {
        return businessId.startsWith(GeneratorService._PREFIX_TRADE_TAIL_ID);
    }

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 微信支付退款处理
     * @Date 15:29 2020/7/2
     * @Param [wxPayResultResponse, businessId, storeId]
     **/
    private void wxRefundHandle(WxPayResultResponse wxPayResultResponse, String businessId, Long storeId) {
        WxPayRefundInfoRequest refundInfoRequest = new WxPayRefundInfoRequest();

        refundInfoRequest.setStoreId(storeId);
        refundInfoRequest.setOut_refund_no(businessId);
        refundInfoRequest.setOut_trade_no(businessId);
        refundInfoRequest.setTotal_fee(wxPayResultResponse.getTotal_fee());
        refundInfoRequest.setRefund_fee(wxPayResultResponse.getTotal_fee());
        String tradeType = wxPayResultResponse.getTrade_type();
        if (!tradeType.equals("APP")) {
            tradeType = "PC/H5/JSAPI";
        }
        refundInfoRequest.setPay_type(tradeType);
        //重复支付进行退款处理标志
        refundInfoRequest.setRefund_type("REPEATPAY");
        BaseResponse<WxPayRefundResponse> wxPayRefund =
                wxPayProvider.wxPilePayRefund(refundInfoRequest);
        WxPayRefundResponse wxPayRefundResponse = wxPayRefund.getContext();
    }

    private void wxPayCallbackHandle(PayGatewayConfigResponse payGatewayConfig, WxPayResultResponse wxPayResultResponse,
                                     String businessId, List<NewPileTrade> trades, boolean isMergePay) {
        //异步回调添加交易数据
        PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
        //微信支付订单号--及流水号
        payTradeRecordRequest.setTradeNo(wxPayResultResponse.getTransaction_id());
        //商户订单号或父单号
        payTradeRecordRequest.setBusinessId(businessId);
        payTradeRecordRequest.setResult_code(wxPayResultResponse.getResult_code());
        payTradeRecordRequest.setPracticalPrice(new BigDecimal(wxPayResultResponse.getTotal_fee()).
                divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN));
        ChannelItemByGatewayRequest channelItemByGatewayRequest = new ChannelItemByGatewayRequest();
        channelItemByGatewayRequest.setGatewayName(payGatewayConfig.getPayGateway().getName());
        PayChannelItemListResponse payChannelItemListResponse =
                payQueryProvider.listChannelItemByGatewayName(channelItemByGatewayRequest).getContext();
        List<PayChannelItemVO> payChannelItemVOList =
                payChannelItemListResponse.getPayChannelItemVOList();
        String tradeType = wxPayResultResponse.getTrade_type();
        ChannelItemSaveRequest channelItemSaveRequest = new ChannelItemSaveRequest();
        String code = "wx_qr_code";
        if (tradeType.equals("APP")) {
            code = "wx_app";
        } else if (tradeType.equals("JSAPI")) {
            code = "js_api";
        } else if (tradeType.equals("MWEB")) {
            code = "wx_mweb";
        }
        channelItemSaveRequest.setCode(code);
        payChannelItemVOList.forEach(payChannelItemVO -> {
            if (channelItemSaveRequest.getCode().equals(payChannelItemVO.getCode())) {
                //更新支付项
                payTradeRecordRequest.setChannelItemId(payChannelItemVO.getId());
            }
        });
        //微信支付异步回调添加交易数据
        payProvider.wxPayCallBack(payTradeRecordRequest);
        //订单 支付单 操作信息
        Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.WECHAT.name())
                .account(PayGatewayEnum.WECHAT.name()).platform(Platform.THIRD).build();


        log.info("payTrades============= {}", trades);
        payCallbackOnline(trades, operator, isMergePay);
    }

    /**
     * 线上订单支付回调
     * 订单 支付单 操作信息
     *
     * @return 操作结果
     */
    private void payCallbackOnline(List<NewPileTrade> trades, Operator operator, boolean isMergePay) {
        List<PayCallBackOnlineBatchPile> payCallBackOnlineBatchList = trades.stream().map(trade -> {
            //每笔订单做是否合并支付标识
            trade.getPayInfo().setMergePay(isMergePay);
            updateTrade(trade);

            if (Objects.nonNull(trade.getIsBookingSaleGoods()) && trade.getIsBookingSaleGoods() && trade.getBookingType() == BookingType.EARNEST_MONEY &&
                    StringUtils.isNotEmpty(trade.getTailOrderNo()) && StringUtils.isNotEmpty(trade.getTailPayOrderId())) {
                //支付单信息
                PayOrder payOrder = findPayOrder(trade.getTailPayOrderId());

                if ((PayWay.BALANCE).equals(trade.getPayWay())) {
                    payOrder.setPayType(PayType.BALANCER);
                }

                PayCallBackOnlineBatchPile backOnlineBatch = new PayCallBackOnlineBatchPile();
                backOnlineBatch.setTrade(KsBeanUtil.convert(trade, PileTrade.class));
                backOnlineBatch.setPayOrderOld(payOrder);
                return backOnlineBatch;
            } else {
                //支付单信息
                PayOrder payOrder = findPayOrder(trade.getPayOrderId());

                if ((PayWay.BALANCE).equals(trade.getPayWay())) {
                    payOrder.setPayType(PayType.BALANCER);
                }
                PayCallBackOnlineBatchPile backOnlineBatch = new PayCallBackOnlineBatchPile();
                backOnlineBatch.setTrade(KsBeanUtil.convert(trade, PileTrade.class));
                backOnlineBatch.setPayOrderOld(payOrder);
                return backOnlineBatch;
            }
        }).collect(Collectors.toList());
//        log.info("payCallBackOnlineBatchList------- {}", payCallBackOnlineBatchList);
//        payCallBackOnlineBatch(payCallBackOnlineBatchList, operator);
    }

    /**
     * 获取支付单
     *
     * @param payOrderId
     * @return
     */
    public PayOrder findPayOrder(String payOrderId) {
        return payOrderRepository.findById(payOrderId).orElse(null);
    }

    @LcnTransaction
    @Transactional
    public void payCallBackOnlineBatch(List<PayCallBackOnlineBatchNewPile> request, Operator operator) {
        request.forEach(i -> payCallBackOnline(i.getTrade(), i.getPayOrderOld(), operator));
    }

    /**
     * 线上订单支付回调
     *
     * @param trade
     * @param payOrderOld
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void payCallBackOnline(NewPileTrade trade, PayOrder payOrderOld, Operator operator) {
        try {
            if (payOrderOld.getReceivable() == null) {

                BaseResponse<PayTradeRecordResponse> response;
                if (trade.getPayInfo().isMergePay()) {
                    response = payQueryProvider.getTradeRecordByOrderCode(new
                            TradeRecordByOrderCodeRequest(trade.getParentId()));
                } else {
                    response = payQueryProvider.getTradeRecordByOrderCode(new
                            TradeRecordByOrderCodeRequest(trade.getId()));
                }
                PayChannelItemResponse chanelItemResponse = payQueryProvider.getChannelItemById(new
                        ChannelItemByIdRequest(response.getContext().getChannelItemId())).getContext();
                ReceivableAddRequest param = new ReceivableAddRequest(trade.getPayOrderId(), Arrays.asList(trade.getPayOrderId()), LocalDateTime.now()
                        .toString()
                        , trade.getSellerRemark(), 0L, chanelItemResponse.getName(), chanelItemResponse.getId(), null);

                Optional<PayOrder> payOrder = addReceivable(param, operator.getPlatform());

                log.info("=========payOrder.isPresent==========: {}", payOrder.isPresent());

                log.info("=========chanelItemResponse==========: {}", JSONObject.toJSONString(chanelItemResponse));

                if (payOrder.isPresent()) {
                    //订单状态变更
                    payCallBack(trade.getId(), payOrder.get().getPayOrderPrice(), operator, PayWay.valueOf
                            (chanelItemResponse
                                    .getChannel().toUpperCase()));

                    //线上回调成功新增囤货信息
                    List<GoodsInfoDTO> goodsInfoDTOS = Lists.newArrayList();
                    trade.getTradeItems().forEach(ti -> {
                        GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
                        goodsInfoDTO.setGoodsInfoId(ti.getSkuId());
                        goodsInfoDTO.setBuyCount(ti.getNum());
                        goodsInfoDTO.setGoodsSplitPrice(ti.getSplitPrice().divide(new BigDecimal(ti.getNum()), 2, BigDecimal.ROUND_HALF_UP));
                        goodsInfoDTOS.add(goodsInfoDTO);
                    });
                    PurchaseSaveDTO build = PurchaseBatchSaveRequest.builder().customerId(operator.getUserId())
                            .goodsInfos(goodsInfoDTOS)
                            .wareId(trade.getWareId())
                            .inviteeId(trade.getInviteeId())
                            .pid(trade.getParentId())
                            .orderCode(trade.getId())
                            .orderTotalPrice(trade.getTradePrice().getTotalPrice())
                            .build();
//                    pilePurchaseService.batchSave(KsBeanUtil.convert(build, PilePurchaseRequest.class));
                }

//                addReceivable(param, operator.getPlatform()).ifPresent(payOrder ->
//                        //订单状态变更
//                        payCallBack(trade.getId(), payOrder.getPayOrderPrice(), operator, PayWay.valueOf
//                                (chanelItemResponse
//                                        .getChannel().toUpperCase()))
//                );
                logger.info("TradeService.payCallBackOnline PayOrderStatus:{}", payOrderOld.getPayOrderStatus());
//                //已支付
//                if (PayOrderStatus.PAYED.equals(payOrderOld.getPayOrderStatus())){
//                    pushPayOrderKingdee(trade,payOrderOld);
//                }
            }
        } catch (SbcRuntimeException e) {
            logger.error("The {} order status modifies the exception.error={}", trade.getId(), e);
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
    }

    /**
     * 新增线下收款单(包含线上线下的收款单)
     *
     * @param receivableAddRequest receivableAddRequest
     * @param platform             platform
     * @return 收款单
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public Optional<PayOrder> addReceivable(ReceivableAddRequest receivableAddRequest, Platform platform) {
        PayOrder payOrder = payOrderRepository.findById(receivableAddRequest.getPayOrderId()).orElse(null);
        if (Objects.isNull(payOrder) || DeleteFlag.YES.equals(payOrder.getDelFlag())) {
            throw new SbcRuntimeException("K-070001");
        }
        if (!CollectionUtils.isEmpty(receivableRepository.findByDelFlagAndPayOrderId(DeleteFlag.NO, payOrder
                .getPayOrderId()))) {
            throw new SbcRuntimeException("K-070005");
        }

        /**1.创建收款单*/
        Receivable receivable = new Receivable();
        BeanUtils.copyProperties(receivableAddRequest, receivable);
        receivable.setOfflineAccountId(receivableAddRequest.getAccountId());
        String createTime = receivableAddRequest.getCreateTime();
        // 2020-06-02T11:41:31.123
        if (createTime.contains("T")) {
            receivable.setCreateTime(LocalDateTime.parse(createTime));
        } else {
            if (createTime.length() == 10) {
                receivable.setCreateTime(LocalDateTime.of(LocalDate.parse(createTime,
                        DateTimeFormatter.ofPattern(DateUtil.FMT_DATE_1)), LocalTime.MIN));
            } else if (createTime.length() == DateUtil.FMT_TIME_1.length()) {
                receivable.setCreateTime(LocalDateTime.parse(createTime,
                        DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_1)));
            } else {
                receivable.setCreateTime(LocalDateTime.parse(createTime));
            }
        }

        receivable.setDelFlag(DeleteFlag.NO);
        receivable.setReceivableNo(generatorService.generateSid());
        receivable.setPayChannel(receivableAddRequest.getPayChannel());
        receivable.setPayChannelId(receivableAddRequest.getPayChannelId());

        //这里往缓存里面写
        payOrder.setReceivable(receivableRepository.saveAndFlush(receivable));
        /**2.更改支付单状态*/
        PayOrderStatus status;
        if (osUtil.isS2b()) {
            status = platform == Platform.PLATFORM ? PayOrderStatus.PAYED : PayOrderStatus.TOCONFIRM;
        } else {
            status = platform == Platform.BOSS ? PayOrderStatus.PAYED : PayOrderStatus.TOCONFIRM;
        }
        if (PayType.ONLINE.equals(payOrder.getPayType())) {
            status = PayOrderStatus.PAYED;
        }
        payOrder.setPayOrderStatus(status);
        payOrderService.updatePayOrder(receivableAddRequest.getPayOrderId(), status);

        if (PayOrderStatus.PAYED.equals(status)) {//如果是已支付,需要增加埋点(线上付款成功 或者 Boss端添加线下付款单)
            //ares埋点-订单-用户线上支付订单 或者 Boss端添加线下付款单
            orderAresService.dispatchFunction("payOrder", payOrder, LocalDateTime.now());
        }
        return Optional.of(payOrder);
    }

    /**
     *  通过parentId查询所有子订单
     * @param parentTid
     * @return
     */
    public List<NewPileTrade> detailsByParentIds(List<String> parentTid) {
        return mongoTemplate.find(Query.query(Criteria.where("parentId").in(parentTid)), NewPileTrade.class);
    }

    public List<NewPileTrade> listTradeExport(NewPileTradeQueryRequest tradeQueryRequest) {
        long count = this.countNum(tradeQueryRequest.getWhereCriteria(), tradeQueryRequest);
        if (count > 3000) {
            count = 3000;
        }
        tradeQueryRequest.putSort(tradeQueryRequest.getSortColumn(), tradeQueryRequest.getSortRole());
        tradeQueryRequest.setPageNum(0);
        tradeQueryRequest.setPageSize((int) count);
        Query query = new Query();
        query.addCriteria(tradeQueryRequest.getWhereCriteria());
        System.err.println("mongo：  " + LocalDateTime.now());
        List<NewPileTrade> tradeList = mongoTemplate.find(query.with(tradeQueryRequest.getPageRequest()), NewPileTrade.class);
        System.err.println("mongo：  " + LocalDateTime.now());
        return tradeList;
    }

    /**
     * 通过囤货订单ID查询
     * @param newPileTradeId 囤货订单id
     * @return
     */
    public NewPileTrade getNewPileTradeById(String newPileTradeId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(newPileTradeId)), NewPileTrade.class);
    }

    public void addTradeEventLog(NewPileTrade newPileTrade, String title, String desc, Operator operator) {
        newPileTrade.appendTradeEventLog(new TradeEventLog(operator, title, desc, LocalDateTime.now()));
        this.updateTrade(newPileTrade);
        operationLogMq.convertAndSend(operator, title, desc);
    }


    public List<NewPileOldData> newPileOldData() {
        List<Object> resultList = newPileOldDataRepository.queryAllNewPileOldData();
        return convertFromNativeSQLResult(resultList);
    }

    private List<NewPileOldData> convertFromNativeSQLResult(List<Object> resultList) {
        List<NewPileOldData> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(resultList)) {
            return voList;
        }
        for (Object obj : resultList) {
            Object[] result = (Object[]) obj;
            NewPileOldData vo = new NewPileOldData();
            vo.setCustomerId(StringUtil.cast(result, 0, String.class));
            vo.setGoodsInfoId(StringUtil.cast(result, 1, String.class));
            vo.setAccount(StringUtil.cast(result, 2, String.class));
            vo.setGoodsName(StringUtil.cast(result, 3, String.class));
            vo.setErpNo(StringUtil.cast(result, 4, String.class));
            vo.setPrice(StringUtil.cast(result, 5, BigDecimal.class));
            vo.setGoodsNum(Integer.valueOf(String.valueOf(result[6])));
            vo.setProvince(StringUtil.cast(result, 7, String.class));
            vo.setCity(StringUtil.cast(result, 8, String.class));
            vo.setArea(StringUtil.cast(result, 9, String.class));
            vo.setAddress(StringUtil.cast(result, 10, String.class));
            vo.setCreateTime(StringUtil.cast(result, 11, LocalDateTime.class));
            voList.add(vo);
        }
        return voList;
    }

    public List<NewPileOldData> saveNewPileOldData(List<NewPileOldData> datas) {
        return newPileOldDataRepository.saveAll(datas);
    }


    public NewPileSendData saveNewPileOldData(NewPileSendData data) {
        return newPileSendDataRepository.save(data);
    }


    /**
     * 谨慎调用！！！！！！！！！！！！！！！！！！！为补偿订单做的操作
     * @param tid
     */
    public void compensateOldPileTadeStauesAndPayInfo(String tid){
        Optional<NewPileTrade> byId = newPileTradeRepository.findById(tid);
        if (byId.isPresent()){
            NewPileTrade newPileTrade = byId.get();
            newPileTrade.getTradeState().setFlowState(NewPileFlowState.PILE);
            newPileTrade.getTradeState().setPayState(PayState.PAID);
            newPileTrade.getPayInfo().setPayTypeName("OFFLINE");
            newPileTrade.getPayInfo().setDesc("线下支付");
            newPileTradeRepository.save(newPileTrade);
        }else {
            log.info("====================订单id为："+tid+"在newPileTrade无数据");
        }
        Optional<PayOrder> payOrderByOrderCode = payOrderService.findPayOrderByOrderCode(tid);
        if (payOrderByOrderCode.isPresent()){
            PayOrder payOrder = payOrderByOrderCode.get();
            payOrder.setPayOrderStatus(PayOrderStatus.PAYED);
            payOrder.setPayType(PayType.OFFLINE);
            payOrderRepository.save(payOrder);
        }else{
            log.info("====================订单id为："+tid+"在PayOrder无数据");
        }


    }

    /**
     * 查询订单集合
     *
     * @param tids
     */
    public List<NewPileTrade> details(List<String> tids) {
        return org.apache.commons.collections4.IteratorUtils.toList(newPileTradeRepository.findAllById(tids).iterator());
    }

    public boolean isPayByOfflineOrIsOPK(Trade trade) {
        org.springframework.util.Assert.notNull(trade, "trade must not be null");
        org.springframework.util.Assert.notNull(trade.getPayInfo(), "trade.getPayInfo() must not be null");
        //线下支付 或 囤货提货时，不走锁定库存逻辑，直接更新可用库存
        return PayType.OFFLINE.toValue() == Integer.parseInt(trade.getPayInfo().getPayTypeId()) ||
                Objects.equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType(), trade.getActivityType());
    }
}
