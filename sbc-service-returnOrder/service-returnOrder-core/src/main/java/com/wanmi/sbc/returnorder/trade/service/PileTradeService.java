package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wanmi.sbc.account.api.provider.finance.record.AccountRecordProvider;
import com.wanmi.sbc.account.api.provider.wallet.*;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordAddRequest;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordDeleteByOrderCodeAndTypeRequest;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.api.response.wallet.WalletRecordResponse;
import com.wanmi.sbc.account.bean.enums.*;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.account.bean.vo.WalletRecordVO;
import com.wanmi.sbc.common.base.*;
import com.wanmi.sbc.common.config.wms.WmsApiProperties;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.enums.node.DistributionType;
import com.wanmi.sbc.common.enums.node.OrderProcessType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.email.CustomerEmailQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.invitationstatistics.InvitationStatisticsProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerListForOrderCommitRequest;
import com.wanmi.sbc.customer.api.request.email.NoDeleteCustomerEmailListByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationTradeStatisticsRequest;
import com.wanmi.sbc.customer.api.request.invoice.CustomerInvoiceByIdAndDelFlagRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelByCustomerIdAndStoreIdRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelMapByCustomerIdAndStoreIdsRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.api.response.invoice.CustomerInvoiceByIdAndDelFlagResponse;
import com.wanmi.sbc.customer.api.response.store.ListNoDeleteStoreByIdsResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CompanyType;
import com.wanmi.sbc.customer.bean.enums.*;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateStoreQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsSaveProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.flashsalegoods.FlashSaleGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateStoreListByStoreIdAndDeleteFlagRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsLackStockRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsMinusStockRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.distribution.DistributionCacheQueryProvider;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.grouponrecord.GrouponRecordProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingCouponPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingTradePluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeBatchModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeReturnByIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.SendCouponRechargeRequest;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityByIdRequest;
import com.wanmi.sbc.marketing.api.request.grouponrecord.GrouponRecordIncrBuyNumRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingMapGetByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingCouponWrapperRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingTradeBatchWrapperRequest;
import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import com.wanmi.sbc.marketing.api.response.distribution.MultistageSettingGetResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingCouponWrapperResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeBatchModifyDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.mongo.MongoTccHelper;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.pay.api.provider.AliPayProvider;
import com.wanmi.sbc.pay.api.provider.PayProvider;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.provider.WxPayProvider;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.vo.PayChannelItemVO;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayConstants;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import com.wanmi.sbc.returnorder.api.provider.purchase.PurchaseQueryProvider;
import com.wanmi.sbc.returnorder.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.returnorder.api.request.paycallbackresult.PayCallBackResultQueryRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseBatchSaveRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.PurchaseOrderMarketingRequest;
import com.wanmi.sbc.returnorder.api.request.trade.*;
import com.wanmi.sbc.returnorder.api.response.historylogisticscompany.HistoryLogisticsCompanyByCustomerIdResponse;
import com.wanmi.sbc.returnorder.api.response.purchase.PurchaseOrderMarketingResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeGetGoodsResponse;
import com.wanmi.sbc.returnorder.api.response.trade.TradeListAllResponse;
import com.wanmi.sbc.returnorder.ares.service.OrderAresService;
import com.wanmi.sbc.returnorder.bean.dto.*;
import com.wanmi.sbc.returnorder.bean.enums.*;
import com.wanmi.sbc.returnorder.bean.vo.HistoryLogisticsCompanyVO;
import com.wanmi.sbc.returnorder.bean.vo.OfflineSettlementVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeGoodsListVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.common.OperationLogMq;
import com.wanmi.sbc.returnorder.common.OrderCommonService;
import com.wanmi.sbc.returnorder.constant.OrderErrorCode;
import com.wanmi.sbc.returnorder.customer.service.CustomerCommonService;
import com.wanmi.sbc.returnorder.historylogisticscompany.service.HistoryLogisticsCompanyService;
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
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseService;
import com.wanmi.sbc.returnorder.pilepurchase.request.PilePurchaseRequest;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseAction;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.purchase.PurchaseService;
import com.wanmi.sbc.returnorder.purchase.request.PurchaseRequest;
import com.wanmi.sbc.returnorder.receivables.model.root.Receivable;
import com.wanmi.sbc.returnorder.receivables.repository.ReceivableRepository;
import com.wanmi.sbc.returnorder.receivables.request.ReceivableAddRequest;
import com.wanmi.sbc.returnorder.receivables.service.ReceivableService;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnPileOrderRepository;
import com.wanmi.sbc.returnorder.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.returnorder.trade.fsm.params.StateRequest;
import com.wanmi.sbc.returnorder.trade.model.entity.*;
import com.wanmi.sbc.returnorder.trade.model.entity.value.*;
import com.wanmi.sbc.returnorder.trade.model.root.*;
import com.wanmi.sbc.returnorder.trade.newpilefsm.NewPileTradeFSMService;
import com.wanmi.sbc.returnorder.trade.pilefsm.PileTradeFSMService;
import com.wanmi.sbc.returnorder.trade.reponse.TradeFreightResponse;
import com.wanmi.sbc.returnorder.trade.repository.GrouponInstanceRepository;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordRepository;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeGroupRepository;
import com.wanmi.sbc.returnorder.trade.repository.PileTradeRepository;
import com.wanmi.sbc.returnorder.trade.request.TradeDeliverRequest;
import com.wanmi.sbc.returnorder.trade.request.*;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.returnorder.util.EmailOrderUtil;
import com.wanmi.sbc.returnorder.util.KingdeeLoginUtils;
import com.wanmi.sbc.returnorder.util.TradeExportUtil;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.EmailConfigProvider;
import com.wanmi.sbc.setting.api.provider.SystemPointsConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyListRequest;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import com.wanmi.sbc.setting.api.response.SystemPointsConfigQueryResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyListResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.enums.EmailStatus;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import com.wanmi.sbc.wms.api.provider.erp.PushOrderKingdeeProvider;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSOrderProvider;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushRequest;
import com.wanmi.sbc.wms.api.request.erp.PileTradePushTableBodyRequest;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSOrderCancelRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSPushOrderDetailsRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSPushOrderRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.wanmi.sbc.wms.api.response.wms.ResponseWMSReturnResponse;
import com.wanmi.sbc.wms.bean.vo.ERPWMSConstants;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import com.wanmi.sbc.wms.bean.vo.WmsErpIdConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.Document;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 订单service
 * Created by jinwei on 27/3/2017.
 */
@Service
@Slf4j
public class PileTradeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${send.order.last.month}")
    private String sendOrderLastMonth;

    @Autowired
    private OrderAresService orderAresService;

    @Autowired
    private PileTradeFSMService pileTradeFSMService;


    @Autowired
    private NewPileTradeFSMService newPileTradeFSMService;
    @Autowired
    private PileTradeRepository pileTradeRepository;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private OrderInvoiceService orderInvoiceService;

    @Autowired
    private ReturnPileOrderRepository returnPileOrderRepository;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private PayOrderRepository payOrderRepository;

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private PileTradeItemService pileTradeItemService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private AccountRecordProvider accountRecordProvider;

    @Autowired
    private ReceivableService receivableService;

    @Autowired
    private MarketingTradePluginProvider marketingTradePluginProvider;

    @Autowired
    private MarketingCouponPluginProvider marketingCouponPluginProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private FreightTemplateStoreQueryProvider freightTemplateStoreQueryProvider;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private PileTradeGroupRepository pileTradeGroupRepository;

    @Autowired
    private PileTradeGroupService pileTradeGroupService;

    @Autowired
    private EmailConfigProvider emailConfigProvider;

    @Autowired
    private CustomerEmailQueryProvider customerEmailQueryProvider;

    @Autowired
    private SystemPointsConfigQueryProvider systemPointsConfigQueryProvider;

    @Autowired
    private TradeEmailService tradeEmailService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private DistributionCustomerQueryProvider distributionCustomerQueryProvider;

    @Autowired
    private DistributionCacheQueryProvider distributionCacheQueryProvider;

    @Autowired
    private GrouponActivityQueryProvider grouponActivityQueryProvider;
    @Autowired
    private ProviderTradeService providerTradeService;

    @Autowired
    private GrouponInstanceRepository grouponInstanceRepository;
    /**
     * 注入消费记录生产者service
     */
    @Autowired
    public OrderProducerService orderProducerService;

    @Autowired
    public PointsGoodsSaveProvider pointsGoodsSaveProvider;

    @Autowired
    private GrouponRecordProvider grouponRecordProvider;

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Resource
    private MongoTccHelper mongoTccHelper;

    @Autowired
    private TradeCacheService tradeCacheService;

    @Autowired
    private RequestWMSOrderProvider requestWMSOrderProvider;

    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private PickUpRecordService pickUpRecordService;


    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Autowired
    private HistoryLogisticsCompanyService historyLogisticsCompanyService;

    @Autowired
    private OrderCommonService orderCommonService;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private PayCallBackResultService payCallBackResultService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WxPayProvider wxPayProvider;

    @Autowired
    private AliPayProvider aliPayProvider;

    @Autowired
    private PayProvider payProvider;

    @Autowired
    private InvitationStatisticsProvider invitationStatisticsProvider;

    @Autowired
    private PurchaseQueryProvider purchaseQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Resource
    private EmailOrderUtil emailOrderUtil;

    @Autowired
    private TradeExportUtil tradeExportUtil;

    @Autowired
    private WalletRecordProvider walletRecordProvider;


    @Autowired
    private VirtualGoodsQueryProvider virtualGoodsQueryProvider;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private TicketsFormProvider ticketsFormProvider;

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private PilePurchaseService pilePurchaseService;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;

    @Autowired
    private MarketingScopeProvider marketingScopeProvider;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private PushOrderKingdeeProvider pushOrderKingdeeProvider;

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


    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private WmsApiProperties wmsApiProperties;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param pileTrade
     */
    @MongoRollback(persistence = PileTrade.class, operation = Operation.ADD)
    public void addTrade(PileTrade pileTrade) {
        pileTradeRepository.save(pileTrade);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param pileTrade
     */
    @MongoRollback(persistence = PileTrade.class, operation = Operation.UPDATE)
    public void updateTrade(PileTrade pileTrade) {
        pileTradeRepository.save(pileTrade);
    }

    /**
     * 删除文档
     *
     * @param tid
     */
    @MongoRollback(persistence = PileTrade.class, idExpress = "tid", operation = Operation.UPDATE)
    public void deleteTrade(String tid) {
        pileTradeRepository.deleteById(tid);
    }


    /**
     * 订单分页
     *
     * @param whereCriteria 条件
     * @param request       参数
     * @return
     */
    public Page<PileTrade> page(Criteria whereCriteria, TradeQueryRequest request) {
        long totalSize = this.countNum(whereCriteria, request);
        if (totalSize < 1) {
            return new PageImpl<>(new ArrayList<>(), request.getPageRequest(), totalSize);
        }
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        return new PageImpl<>(mongoTemplate.find(query.with(request.getPageRequest()), PileTrade.class), request
                .getPageable(), totalSize);
    }

    /**
     * 统计数量
     *
     * @param whereCriteria
     * @param request
     * @return
     */
    public long countNum(Criteria whereCriteria, TradeQueryRequest request) {
        request.putSort(request.getSortColumn(), request.getSortRole());
        Query query = new Query(whereCriteria);
        long totalSize = mongoTemplate.count(query, PileTrade.class);
        return totalSize;
    }


    /**
     * 根据流程状态时间查询订单
     *
     * @param endDate   endDate
     * @param flowState flowState
     * @return List<Trade>
     */
    public List<PileTrade> queryTradeByDate(LocalDateTime endDate, FlowState flowState, int PageNum, int pageSize) {
        Criteria criteria = new Criteria();

        criteria.andOperator(Criteria.where("tradeState.flowState").is(flowState.toValue())
                , Criteria.where("tradeState.deliverTime").lt(endDate)
                , Criteria.where("orderType").is(OrderType.NORMAL_ORDER.getOrderTypeId())
        );

        return mongoTemplate.find(
                new Query(criteria).skip(PageNum * pageSize).limit(pageSize)
                , PileTrade.class);
    }

    /**
     * 查询客户首笔完成的交易号
     *
     * @param customreId
     * @return
     */
    public String queryFirstCompleteTrade(String customreId) {
        Criteria criteria = new Criteria();

        criteria.andOperator(Criteria.where("tradeState.flowState").is(FlowState.COMPLETED.toValue()),
                Criteria.where("buyer.id").is(customreId));
        Query query = new Query(criteria);

        query.with(Sort.by(new Sort.Order(Sort.Direction.ASC, "tradeState.endTime"))).limit(1);

        List<PileTrade> tradeList = mongoTemplate.find((query), PileTrade.class);
        if (CollectionUtils.isNotEmpty(tradeList)) {
            return tradeList.get(0).getId();
        }

        return StringUtils.EMPTY;
    }

    /**
     * 根据流程状态时间查询总条数
     *
     * @param endDate
     * @param flowState
     * @return
     */
    public long countTradeByDate(LocalDateTime endDate, FlowState flowState) {
        Criteria criteria = new Criteria();

        criteria.andOperator(Criteria.where("tradeState.flowState").is(flowState.toValue())
                , Criteria.where("tradeState.deliverTime").lt(endDate)
                , Criteria.where("orderType").is(OrderType.NORMAL_ORDER.getOrderTypeId())
        );
        return mongoTemplate.count(new Query(criteria), PileTrade.class);
    }


    /**
     * C端下单
     */
    @Transactional
    @LcnTransaction
    public List<TradeCommitResult> commit(PileTradeCommitRequest pileTradeCommitRequest) {

        // 验证用户
        CustomerVO customer = verifyService.verifyCustomer(pileTradeCommitRequest.getOperator().getUserId());
        pileTradeCommitRequest.setCustomer(customer);

        Operator operator = pileTradeCommitRequest.getOperator();
        //查询mongo
        List<TradeItemGroup> tradeItemGroups = pileTradeItemService.find(customer.getCustomerId());

        // 如果为PC商城下单，将分销商品变为普通商品
        if (ChannelType.PC_MALL.equals(pileTradeCommitRequest.getDistributeChannel().getChannelType())) {
            tradeItemGroups.forEach(tradeItemGroup ->
                    tradeItemGroup.getTradeItems().forEach(tradeItem -> {
                        tradeItem.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    })
            );
        }

        // 拼团订单--验证
        TradeGrouponCommitForm grouponForm = tradeItemGroups.get(NumberUtils.INTEGER_ZERO).getGrouponForm();

        TradeCommitRequest tradeCommitRequest = KsBeanUtil.convert(pileTradeCommitRequest, TradeCommitRequest.class);

        // 1.验证失效的营销信息(目前包括失效的赠品、满系活动、优惠券)
        verifyService.verifyInvalidMarketings(tradeCommitRequest, tradeItemGroups, customer);

        // 2.按店铺包装多个订单信息、订单组信息
        List<PileTrade> trades = this.wrapperTradeList(tradeCommitRequest, tradeItemGroups);
        PileTradeGroup tradeGroup = this.wrapperTradeGroup(trades, tradeCommitRequest, grouponForm);

        //满订单优惠
        this.orderMarketing(trades);

        // 3.批量提交订单
        List<TradeCommitResult> successResults;
        if (tradeGroup != null) {
            successResults = this.createBatchWithGroup(trades, tradeGroup, operator);
        } else {
            successResults = this.createBatch(trades, operator);
        }

        try {
            // 4.订单提交成功，删除关联的采购单商品
            trades.forEach(
                    trade -> {
                        List<String> tradeSkuIds =
                                trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
                        deletePurchaseOrder(customer.getCustomerId(), tradeSkuIds,
                                pileTradeCommitRequest.getDistributeChannel());
                        //减去营销商品限购数量
                        calMarketGoodsNum(trade.getTradeItems(),false);
                        //新增囤货商品 改到支付成功回调接口中新增囤货商品 jiangxin 20211002
//                        List<GoodsInfoDTO> goodsInfoDTOS = Lists.newArrayList();
//                        trade.getTradeItems().forEach(ti->{
//                            GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
//                            goodsInfoDTO.setGoodsInfoId(ti.getSkuId());
//                            goodsInfoDTO.setBuyCount(ti.getNum());
//                            goodsInfoDTOS.add(goodsInfoDTO);
//                        });
//                        PurchaseSaveDTO build = PurchaseBatchSaveRequest.builder().customerId(operator.getUserId())
//                                .goodsInfos(goodsInfoDTOS)
//                                .wareId(pileTradeCommitRequest.getWareId())
//                                .inviteeId(pileTradeCommitRequest.getDistributeChannel().getInviteeId())
//                                .build();
//
//                        pilePurchaseService.batchSave(KsBeanUtil.convert(build,PilePurchaseRequest.class));
                    }
            );
            // 5.订单提交成功，删除订单商品快照
            pileTradeItemService.remove(customer.getCustomerId());

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

    private void orderMarketing(List<PileTrade> trades) {
        trades.forEach(trade -> {
            BigDecimal tradePrice = trade.getTradePrice().getTotalPrice();//满订单优惠前价格
            //8.2.1 计算满订单优惠
            PurchaseOrderMarketingResponse context = purchaseQueryProvider.getOrderMarketings(PurchaseOrderMarketingRequest.builder().goodsTotalNum(trade.getTradeItems().stream().map(TradeItem::getNum).reduce((sum, item) -> {
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
                pileTradeItemService.calcSplitPrice(trade.getTradeItems(), newTradePrice, tradePrice);
            }
        });
    }

    /**
     * 功能描述: 代客下单运费计算
     */
    public List<Trade> tradeManagerCommit(List<Trade> trades) {
        List<Trade> bossFreight = new ArrayList<>(20);
        List<Trade> storeFreight = new ArrayList<>(20);
        //平台
        boolean platformFlag = false;
        //统仓统配
        boolean unifiedFlag = false;
        //三方卖家
        boolean supplierFlag = false;

        for (Trade inner : trades) {
            if (!inner.getDeliverWay().equals(DeliverWay.EXPRESS)) {
                inner.getTradePrice().setDeliveryPrice(BigDecimal.ZERO);
            } else {
                if (inner.getSupplier().getCompanyType().equals(com.wanmi.sbc.common.enums.CompanyType.PLATFORM)) {
                    bossFreight.add(inner);
                    platformFlag = true;
                } else if (inner.getSupplier().getCompanyType().equals(com.wanmi.sbc.common.enums.CompanyType.UNIFIED)) {
                    bossFreight.add(inner);
                    unifiedFlag = true;
                } else {
                    supplierFlag = true;
                    storeFreight.add(inner);
                }
//                //平台
//                platformFlag = true;
//                //统仓统配
//                unifiedFlag = true;
//                //三方卖家
//                supplierFlag = true;
//                storeFreight.add(inner);
            }
        }
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

                    // 8.5.订单总价、原始金额追加运费
                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
                }
//                BigDecimal totalPrice = storeFreight.stream().map(
//                        trade -> trade.getTradePrice().getGoodsPrice()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
//                Trade trade = storeFreight.stream().findFirst().get();
//                /**合并所有订单商品*/
//                List<List<TradeItem>> totalTradeItemList = storeFreight.stream().map(s -> s.getTradeItems()).collect(Collectors.toList());
//                List<TradeItem> totalTradeItem = Lists.newArrayList();
//                totalTradeItemList.forEach(c->{
//                    totalTradeItem.addAll(c);
//                });
//                /**合并所有订单赠品*/
//                List<List<TradeItem>> totalGiftsList = storeFreight.stream().map(s -> s.getGifts()).collect(Collectors.toList());
//                List<TradeItem> totalGifts = Lists.newArrayList();
//                totalGiftsList.forEach(c->{
//                    totalGifts.addAll(c);
//                });
//                //商家固定为超级大白鲸
//                Supplier supplier = new Supplier();
//                StoreVO store = storeQueryProvider.getValidStoreById(new ValidStoreByIdRequest(123457927L)).getContext().getStoreVO();
//                supplier.setStoreId(store.getStoreId());
//                supplier.setFreightTemplateType(store.getFreightTemplateType());
//
//                //1返回当前订单 全部商品  运费总金额
//                BigDecimal decimal = this.calcTradeFreight(trade.getConsignee(), supplier,
//                        trade.getDeliverWay(),
//                        totalPrice, totalTradeItem, totalGifts);
//                storeFreight.stream().forEach(trade1 -> {
//                    BigDecimal goodsPrice = trade1.getTradePrice().getGoodsPrice();
//
//                    TradePrice tradePrice = trade1.getTradePrice();
//                    BigDecimal deliveryPrice = BigDecimal.ZERO;
//                    //商品订单总金额
//                    if (totalPrice.compareTo(BigDecimal.ZERO) == Constants.no) {
//                        // decimal 订单运费不为0
//                        if (decimal.compareTo(BigDecimal.ZERO) == Constants.yes) {
//                            deliveryPrice = decimal.divide(new BigDecimal(storeFreight.size()), 2, BigDecimal.ROUND_HALF_UP);
//                        }
//                    } else {
//                        deliveryPrice = goodsPrice.compareTo(BigDecimal.ZERO) ==
//                                Constants.no ? BigDecimal.ZERO : decimal.compareTo(BigDecimal.ZERO) == Constants.no ? BigDecimal.ZERO :
//                                goodsPrice.divide(totalPrice.divide(decimal, 4, BigDecimal.ROUND_HALF_UP),
//                                        4, BigDecimal.ROUND_HALF_UP).setScale(1, BigDecimal.ROUND_HALF_UP);
//                    }
//                    //判断是否为秒杀抢购订单
//                    if (Objects.nonNull(trade1.getIsFlashSaleGoods()) && trade1.getIsFlashSaleGoods()) {
//                        //秒杀商品是否包邮
//                        //获取秒杀抢购活动详情
//                        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
//                                .id(trade1.getTradeItems().get(0).getFlashSaleGoodsId())
//                                .build())
//                                .getContext().getFlashSaleGoodsVO();
//                        if (flashSaleGoodsVO.getPostage().equals(1)) {
//                            deliveryPrice = new BigDecimal(0);
//                        }
//                    }
//
//                    tradePrice.setDeliveryPrice(deliveryPrice);
//                    // 2.计算订单总价(追加运费)
//                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
//                    // 3订单总价、原始金额追加运费
//                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
//                });
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

                    // 8.5.订单总价、原始金额追加运费
                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
                }
            }
        }
        return trades;
    }

    /**
     * 运费模板重新计算，以平台运费模板为标准
     *
     * @param trades
     */
    public void computers(List<Trade> trades, TradeGrouponCommitForm grouponForm) {
        //分类汇总=》三方卖家走店铺模板，其他走平台模板
        List<Trade> bossFreight = new ArrayList<>(10);
        List<Trade> storeFreight = new ArrayList<>(10);
        //平台
        boolean platformFlag = false;
        //统仓统配
        boolean unifiedFlag = false;
        //三方卖家
        boolean supplierFlag = false;

        for (Trade inner : trades) {
            if (!inner.getDeliverWay().equals(DeliverWay.EXPRESS)
                    || (Objects.nonNull(grouponForm) && grouponForm.isFreeDelivery())) {
                //非快递或拼团活动包邮
                inner.getTradePrice().setDeliveryPrice(BigDecimal.ZERO);
            } else {
                if (inner.getSupplier().getCompanyType().equals(com.wanmi.sbc.common.enums.CompanyType.PLATFORM)) {
                    bossFreight.add(inner);
                    platformFlag = true;
                } else if (inner.getSupplier().getCompanyType().equals(com.wanmi.sbc.common.enums.CompanyType.UNIFIED)) {
                    bossFreight.add(inner);
                    unifiedFlag = true;
                } else {
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

//                BigDecimal totalPrice = storeFreight.stream().map(
//                        trade -> trade.getTradePrice().getGoodsPrice()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
//                Trade trade = storeFreight.stream().findFirst().get();
//                /**合并所有订单商品*/
//                List<List<TradeItem>> totalTradeItemList = storeFreight.stream().map(s -> s.getTradeItems()).collect(Collectors.toList());
//                List<TradeItem> totalTradeItem = Lists.newArrayList();
//                totalTradeItemList.forEach(c->{
//                    totalTradeItem.addAll(c);
//                });
//                /**合并所有订单赠品*/
//                List<List<TradeItem>> totalGiftsList = storeFreight.stream().map(s -> s.getGifts()).collect(Collectors.toList());
//                List<TradeItem> totalGifts = Lists.newArrayList();
//                totalGiftsList.forEach(c->{
//                    totalGifts.addAll(c);
//                });
//                //1返回当前订单 全部商品  运费总金额
//                //商家固定为超级大白鲸
//                //商家固定为超级大白鲸
//                Supplier supplier = new Supplier();
//                StoreVO store = storeQueryProvider.getValidStoreById(new ValidStoreByIdRequest(123457927L)).getContext().getStoreVO();
//                supplier.setStoreId(store.getStoreId());
//                supplier.setFreightTemplateType(store.getFreightTemplateType());
//
//                BigDecimal decimal = this.calcTradeFreight(trade.getConsignee(), supplier,
//                        trade.getDeliverWay(),
//                        totalPrice, totalTradeItem, totalGifts);
//                storeFreight.stream().forEach(trade1 -> {
//                    BigDecimal goodsPrice = trade1.getTradePrice().getGoodsPrice();
//
//                    TradePrice tradePrice = trade1.getTradePrice();
//                    BigDecimal deliveryPrice = BigDecimal.ZERO;
//                    //商品订单总金额
//                    if (totalPrice.compareTo(BigDecimal.ZERO) == Constants.no) {
//                        // decimal 订单运费不为0
//                        if (decimal.compareTo(BigDecimal.ZERO) == Constants.yes) {
//                            deliveryPrice = decimal.divide(new BigDecimal(storeFreight.size()), 2, BigDecimal.ROUND_HALF_UP);
//                        }
//                    } else {
//                        deliveryPrice = goodsPrice.compareTo(BigDecimal.ZERO) ==
//                                Constants.no ? BigDecimal.ZERO : decimal.compareTo(BigDecimal.ZERO) == Constants.no ? BigDecimal.ZERO :
//                                goodsPrice.divide(totalPrice.divide(decimal, 4, BigDecimal.ROUND_HALF_UP),
//                                        4, BigDecimal.ROUND_HALF_UP).setScale(1, BigDecimal.ROUND_HALF_UP);
//                    }
//                    //判断是否为秒杀抢购订单
//                    if (Objects.nonNull(trade1.getIsFlashSaleGoods()) && trade1.getIsFlashSaleGoods()) {
//                        //秒杀商品是否包邮
//                        //获取秒杀抢购活动详情
//                        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
//                                .id(trade1.getTradeItems().get(0).getFlashSaleGoodsId())
//                                .build())
//                                .getContext().getFlashSaleGoodsVO();
//                        if (flashSaleGoodsVO.getPostage().equals(1)) {
//                            deliveryPrice = new BigDecimal(0);
//                        }
//                    }
//
//                    tradePrice.setDeliveryPrice(deliveryPrice);
//                    // 2.计算订单总价(追加运费)
//                    tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
//                    // 3订单总价、原始金额追加运费
//                    tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
//                });
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
     * 拼团订单--处理
     */
    private void dealGroupon(PileTrade trade, TradeParams tradeParams) {
        TradeGrouponCommitForm grouponForm = tradeParams.getGrouponForm();
        // 1.将价格设回拼团价
        trade.getTradeItems().forEach(item -> {
            item.setSplitPrice(grouponForm.getGrouponPrice().multiply(new BigDecimal(item.getNum())));
            item.setPrice(grouponForm.getGrouponPrice());
            item.setLevelPrice(grouponForm.getGrouponPrice());
        });

        TradeItem tradeItem = trade.getTradeItems().get(NumberUtils.INTEGER_ZERO);
        GrouponActivityVO grouponActivity = grouponActivityQueryProvider.getById(
                new GrouponActivityByIdRequest(grouponForm.getGrouponActivityId())).getContext().getGrouponActivity();
        grouponForm.setFreeDelivery(grouponActivity.isFreeDelivery());

        // 2.设置订单拼团信息
        TradeGroupon tradeGroupon = TradeGroupon.builder()
                .grouponNo(grouponForm.getGrouponNo())
                .grouponActivityId(grouponActivity.getGrouponActivityId())
                .goodInfoId(tradeItem.getSkuId())
                .goodId(tradeItem.getSpuId())
                .returnNum(NumberUtils.INTEGER_ZERO)
                .returnPrice(BigDecimal.ZERO)
                .grouponOrderStatus(GrouponOrderStatus.WAIT)
                .leader(grouponForm.getOpenGroupon())
                .payState(PayState.NOT_PAID).build();
        trade.setGrouponFlag(Boolean.TRUE);
        trade.setTradeGroupon(tradeGroupon);
        trade.setOrderTimeOut(LocalDateTime.now().plusMinutes(5L));

        // 3.如果是开团，设置团实例
        if (grouponForm.getOpenGroupon()) {
            // 设置团实例
            String grouponNo = generatorService.generateGrouponNo();
            GrouponInstance grouponInstance = GrouponInstance.builder()
                    .id(grouponNo)
                    .grouponNo(grouponNo)
                    .grouponActivityId(grouponActivity.getGrouponActivityId())
                    .grouponNum(grouponActivity.getGrouponNum())
                    .joinNum(NumberUtils.INTEGER_ZERO)
                    .customerId(tradeParams.getCustomer().getCustomerId())
                    .grouponStatus(GrouponOrderStatus.UNPAY)
                    .build();
            // 修改拼团信息中的团号
            tradeGroupon.setGrouponNo(grouponInstance.getGrouponNo());
            grouponInstanceRepository.save(grouponInstance);
        }

        // 4.如果活动为包邮，设置运费为0
        TradePrice tradePrice = trade.getTradePrice();
        if (grouponActivity.isFreeDelivery()) {
            tradePrice.setDeliveryPrice(BigDecimal.ZERO);
        }

        // 5.增加拼团活动单品的购买量
        GrouponRecordIncrBuyNumRequest request = GrouponRecordIncrBuyNumRequest.builder()
                .buyNum(tradeItem.getNum().intValue())
                .customerId(tradeParams.getCustomer().getCustomerId())
                .goodsId(tradeItem.getSpuId())
                .goodsInfoId(tradeItem.getSkuId())
                .grouponActivityId(grouponActivity.getGrouponActivityId())
                .limitSellingNum(grouponForm.getLimitSellingNum()).build();
        grouponRecordProvider.incrBuyNumByGrouponActivityIdAndCustomerIdAndGoodsInfoId(request);

    }


    /**
     * 从购物车中删除商品信息
     */
    private void deletePurchaseOrder(String customerId, List<String> skuIds, DistributeChannel distributeChannel) {
        PurchaseRequest request = PurchaseRequest.builder()
                .customerId(customerId)
                .goodsInfoIds(skuIds).inviteeId(getPurchaseInviteeId(distributeChannel))
                .build();
        purchaseService.delete(request);
    }

    /**
     * 减去营销商品限购数量信息
     * @param tradeItems
     * @param isAddFlag
     */
    public void calMarketGoodsNum(List<TradeItem> tradeItems,Boolean isAddFlag){
        //遍历订单商品信息
        tradeItems.forEach(item -> {
            if(CollectionUtils.isNotEmpty(item.getMarketingIds())){
                log.info("create trade ... reduce or add marketing goods nums:::item.getMarketingIds()={}",item.getMarketingIds());
                //遍历商品使用到的营销信息
                item.getMarketingIds().forEach(marketId -> {
                    MarketingScopeByMarketingIdRequest request = new MarketingScopeByMarketingIdRequest();
                    request.setMarketingId(marketId);
                    request.setSkuId(item.getSkuId());
                    //通过营销ID和商品ID查询 营销关联商品scope信息
                    MarketingScopeByMarketingIdResponse response = marketingScopeQueryProvider.listByMarketingIdAndSkuId(request).getContext();
                    //如果营销关联商品信息不为空
                    if(CollectionUtils.isNotEmpty(response.getMarketingScopeVOList())){
                        //遍历营销关联商品信息
                        response.getMarketingScopeVOList().forEach(marketingScopeVO -> {
                            //如果营销关联商品信息 设置了限购数量
                            if(Objects.nonNull(marketingScopeVO.getPurchaseNum()) && marketingScopeVO.getPurchaseNum() >= 0){
                                log.info("计算营销商品限制数量:::isAddFlag={},marketingScopeVO = {}",isAddFlag,marketingScopeVO);
                                //取消订单
                                if(isAddFlag){
                                    //营销商品表
                                    marketingScopeVO.setPurchaseNum(marketingScopeVO.getPurchaseNum() + item.getNum());
                                }else{//创建订单
                                    //限购数量大于购买数量
                                    if(marketingScopeVO.getPurchaseNum() > item.getNum()){
                                        //赋值限购数量=限购数量-购买数量
                                        marketingScopeVO.setPurchaseNum(marketingScopeVO.getPurchaseNum() - item.getNum());
                                    }else{
                                        //限购数量小于或等于购买数量，允许用户购买并设置限购数量为0
                                        marketingScopeVO.setPurchaseNum(0L);
                                    }
                                }
                                marketingScopeProvider.saveMarketingScope(marketingScopeVO);
                                //查询商品信息
                                GoodsInfoVO goodsInfoVO = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(marketingScopeVO.getScopeId()).build()).getContext();
                                if(Objects.nonNull(goodsInfoVO.getPurchaseNum()) && goodsInfoVO.getPurchaseNum() >= 0){
                                    //设置商品表限购数量
                                    goodsInfoVO.setPurchaseNum(marketingScopeVO.getPurchaseNum());
                                    //同步商品表营销限购数量
                                    GoodsInfoModifyRequest goodsInfoModifyRequest = new GoodsInfoModifyRequest();
                                    goodsInfoModifyRequest.setGoodsInfo(KsBeanUtil.convert(goodsInfoVO,GoodsInfoDTO.class));
                                    goodsInfoProvider.modify(goodsInfoModifyRequest);
                                }
                            }
                        });
                    }
                });
            }
        });
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
     * 将用户下单信息 根据不同店铺 包装成 多个订单 [前端客户下单]
     * 1.校验营销活动
     * 2.校验商品是否可以下单
     * 3.填充订单商品,订单赠品,订单营销信息...
     *
     * @return
     */
    public List<PileTrade> wrapperTradeList(TradeCommitRequest tradeCommitRequest, List<TradeItemGroup> tradeItemGroups) {
        CustomerVO customer = tradeCommitRequest.getCustomer();
        List<PileTrade> trades = new ArrayList<>();

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
        tradeCommitRequest.getStoreCommitInfoList().forEach(
                i -> {
                    TradeItemGroup group = tradeItemGroupsMap.get(i.getStoreId());
                    // 2.1.组装发票信息(缺少联系人,联系方式), 统一入参, 方便调用公共方法
                    Invoice invoice = Invoice.builder()
                            .generalInvoice(KsBeanUtil.convert(i.getGeneralInvoice(), GeneralInvoice.class))
                            .specialInvoice(KsBeanUtil.convert(i.getSpecialInvoice(), SpecialInvoice.class))
                            .address(i.getInvoiceAddressDetail())
                            .addressId(i.getInvoiceAddressId())
                            .projectId(i.getInvoiceProjectId())
                            .projectName(i.getInvoiceProjectName())
                            .projectUpdateTime(i.getInvoiceProjectUpdateTime())
                            .type(i.getInvoiceType())
                            .sperator(i.isSpecialInvoiceAddress())
                            .updateTime(i.getInvoiceAddressUpdateTime())
                            .taxNo(setInvoiceTaxNo(i.getInvoiceType(), i.getGeneralInvoice(), i.getSpecialInvoice()))
                            .build();
                    if (storeMap.get(group.getSupplier().getStoreId()) == null) {
                        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                    }
                    group.getSupplier().setFreightTemplateType(
                            storeMap.get(group.getSupplier().getStoreId()).getFreightTemplateType());
                    boolean flashSale = false;
                    //验证是否是秒杀商品
                    if (CollectionUtils.isNotEmpty(group.getTradeItems())
                            && group.getTradeItems().size() == 1
                            && Objects.nonNull(group.getTradeItems().get(0).getIsFlashSaleGoods())
                            && group.getTradeItems().get(0).getIsFlashSaleGoods()) {
                        flashSale = true;
                    }

                    // 2.2.【公共方法】下单信息验证, 将信息包装成订单
                    trades.add(this.validateAndWrapperTrade(new PileTrade(),
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
                                    .consignee(null) //客户下单,不可填写临时收货地址
                                    .invoice(invoice)
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
                                    .isFlashSaleGoods(flashSale)
                                    .wareHouseCode(i.getWareHouseCode())
                                    .wareId(i.getWareId())
                                    .tradeWareHouse(wareHouseTurnToTrade(i.getWareHouseVO()))
                                    .bookingDate(i.getBookingDate())
                                    .logisticsInfo(toLogisticsInfo(i.getLogisticsInfo()))
                                    .group(group)
                                    .build()));
                }
        );
        return trades;
    }


    public Trade wrapperPointsTrade(PointsTradeCommitRequest commitRequest) {
        Trade trade = new Trade();
        // 设置订单基本信息(购买人,商家,收货地址,配送方式,支付方式,备注,订单商品,订单总价...)
        Optional<CommonLevelVO> commonLevelVO;
        boolean flag = true;
        commonLevelVO =
                Optional.of(fromCustomerLevel(customerLevelQueryProvider.getDefaultCustomerLevel().getContext()));
        trade.setBuyer(Buyer.fromCustomer(commitRequest.getCustomer(), commonLevelVO, flag));
        trade.setSupplier(KsBeanUtil.convert(commitRequest.getPointsTradeItemGroup().getSupplier(), Supplier.class));
        TradeItem tradeItem = KsBeanUtil.convert(commitRequest.getPointsTradeItemGroup().getTradeItem(),
                TradeItem.class);
        tradeItem.setOid(generatorService.generateOid());
        if (StringUtils.isBlank(tradeItem.getAdminId())) {
            tradeItem.setAdminId(String.format("%d", trade.getSupplier().getSupplierId()));
        }

        trade.setId(generatorService.generateTid());
        trade.setPlatform(Platform.CUSTOMER);
        trade.setOrderSource(OrderSource.WECHAT);
        trade.setOrderType(OrderType.POINTS_ORDER);
        trade.setPointsOrderType(PointsOrderType.POINTS_GOODS);
        trade.setConsignee(wrapperConsignee(commitRequest.getConsigneeId(), commitRequest.getConsigneeAddress(),
                commitRequest.getConsigneeUpdateTime(), null));
        trade.setDeliverWay(DeliverWay.EXPRESS);
        trade.setPayInfo(PayInfo.builder()
                .payTypeId(String.format("%d", PayType.ONLINE.toValue()))
                .payTypeName(PayType.ONLINE.name())
                .desc(PayType.ONLINE.getDesc())
                .build());
        trade.setBuyerRemark(commitRequest.getBuyerRemark());
        trade.setRequestIp(commitRequest.getOperator().getIp());
        trade.setTradeItems(Collections.singletonList(tradeItem));
        trade.setTradePrice(TradePrice.builder().points(commitRequest.getPointsTradeItemGroup().getTradeItem()
                .getPoints() * commitRequest.getPointsTradeItemGroup().getTradeItem().getNum()).build());

        return trade;
    }

    /**
     * 设置纳税人识别号
     *
     * @param invoiceType    发票类型 0：普通发票 1：增值税发票 -1：无
     * @param generalInvoice 普票信息
     * @param specialInvoice 增票信息
     * @return
     */
    private String setInvoiceTaxNo(
            Integer invoiceType, GeneralInvoiceDTO generalInvoice, SpecialInvoiceDTO specialInvoice) {
        String taxNo = "";
        //不需要发票
        if (!InvoiceType.NORMAL.equals(invoiceType) && !InvoiceType.SPECIAL.equals(invoiceType)) {
            return taxNo;
        }
        //增票
        if (InvoiceType.SPECIAL.equals(invoiceType)) {
            taxNo = Objects.nonNull(specialInvoice) ? specialInvoice.getIdentification() : "";
        } else {
            taxNo = Objects.nonNull(generalInvoice) ? generalInvoice.getIdentification() : "";
        }
        return taxNo;
    }

    public void dealPoints(List<Trade> trades, TradeCommitRequest tradeCommitRequest) {
        if (tradeCommitRequest.getPoints() == null || tradeCommitRequest.getPoints() <= 0) {
            return;
        }
        // 如果使用积分 校验可使用积分
        verifyService.verifyPoints(trades, tradeCommitRequest);

        List<TradeItem> items =
                trades.stream().flatMap(trade -> trade.getTradeItems().stream().filter(param -> (Objects.nonNull(param.getGoodsInfoType())
                        && param.getGoodsInfoType() == 0))).collect(Collectors.toList());

        // 设置关联商品的积分均摊
        // 查询积分设置
        SystemPointsConfigQueryResponse pointsConfig =
                systemPointsConfigQueryProvider.querySystemPointsConfig().getContext();
        BigDecimal pointWorth = BigDecimal.valueOf(pointsConfig.getPointsWorth());
        BigDecimal pointsTotalPrice = BigDecimal.valueOf(tradeCommitRequest.getPoints()).divide(pointWorth, 2,
                BigDecimal.ROUND_HALF_UP);
        pileTradeItemService.calcPoints(items, pointsTotalPrice, tradeCommitRequest.getPoints(), pointWorth);

        // 设置关联商品的均摊价
        BigDecimal total = pileTradeItemService.calcSkusTotalPrice(items);
        pileTradeItemService.calcSplitPrice(items, total.subtract(pointsTotalPrice), total);

        Map<Long, List<TradeItem>> itemsMap = items.stream().collect(Collectors.groupingBy(TradeItem::getStoreId));
        itemsMap.keySet().forEach(storeId -> {
            // 找到店铺对应订单的价格信息
            Trade trade = trades.stream()
                    .filter(t -> t.getSupplier().getStoreId().equals(storeId)).findFirst().orElse(null);
            TradePrice tradePrice = trade.getTradePrice();

            // 计算积分抵扣额(pointsPrice、points)，并追加至订单优惠金额、积分抵扣金额
            BigDecimal pointsPrice = pileTradeItemService.calcSkusTotalPointsPrice(itemsMap.get(storeId));
            Long points = pileTradeItemService.calcSkusTotalPoints(itemsMap.get(storeId));
            tradePrice.setPointsPrice(pointsPrice);
            tradePrice.setPoints(points);
            tradePrice.setPointWorth(pointsConfig.getPointsWorth());
            // 重设订单总价
            tradePrice.setTotalPrice(tradePrice.getTotalPrice().subtract(pointsPrice));
        });
    }

    /**
     * 构建订单组对象，同时修改订单列表中相应的价格信息
     *
     * @param trades             订单列表
     * @param tradeCommitRequest
     * @return
     */
    public PileTradeGroup wrapperTradeGroup(
            List<PileTrade> trades, TradeCommitRequest tradeCommitRequest, TradeGrouponCommitForm grouponForm) {
        if (tradeCommitRequest.getCommonCodeId() == null) {
            return null;
        }
        CustomerVO customer = tradeCommitRequest.getCustomer();
        PileTradeGroup tradeGroup = new PileTradeGroup();

        // 1.请求营销插件，验证并包装优惠券信息
        List<TradeItem> items = trades.stream().flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors
                .toList());
        TradeCouponVO tradeCoupon = this.buildTradeCouponInfo(
                items,
                tradeCommitRequest.getCommonCodeId(),
                tradeCommitRequest.isForceCommit(),
                StringUtils.isNotBlank(customer.getParentCustomerId()) ? customer.getParentCustomerId() : customer.getCustomerId());
        if (tradeCoupon == null) {
            return null;
        }
        log.info("================ items:{}",items);
        log.info("================ goodsInfoIds:{}",tradeCoupon.getGoodsInfoIds());
        // 2.找出需要均摊的商品，以及总价
        List<TradeItem> matchItems = items.stream()
                .filter(t -> tradeCoupon.getGoodsInfoIds().contains(t.getSkuId())).collect(Collectors.toList());
        BigDecimal total = pileTradeItemService.calcSkusTotalPrice(matchItems);
        log.info("================= matchItems:{}",matchItems);
        // 3.判断是否达到优惠券使用门槛
        BigDecimal fullBuyPrice = tradeCoupon.getFullBuyPrice();
        if (fullBuyPrice != null && fullBuyPrice.compareTo(total) == 1) {
            log.error("fullBuyPrice:{},total:{}===================",fullBuyPrice,total);
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
        pileTradeItemService.calcSplitPrice(matchItems, total.subtract(tradeCoupon.getDiscountsAmount()), total);

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
            PileTrade trade = trades.stream()
                    .filter(t -> t.getSupplier().getStoreId().equals(storeId)).findFirst().orElse(null);
            TradePrice tradePrice = trade.getTradePrice();

            // 8.2.计算平台优惠券优惠额(couponPrice)，并追加至订单优惠金额、优惠券优惠金额
            BigDecimal marketTotalPrice = tradePrice.getGoodsPrice().subtract(tradePrice.getDiscountsPrice());
            BigDecimal couponTotalPrice = pileTradeItemService.calcSkusTotalPrice(itemsMap.get(storeId));
            BigDecimal couponPrice = marketTotalPrice.subtract(couponTotalPrice);
            tradePrice.setDiscountsPrice(tradePrice.getDiscountsPrice().add(couponPrice));
            tradePrice.setCouponPrice(tradePrice.getCouponPrice().add(couponPrice));

            // 8.3.重设订单总价、原始金额
            tradePrice.setTotalPrice(couponTotalPrice);
            tradePrice.setOriginPrice(tradePrice.getGoodsPrice());

            trade.getTradeItems().forEach(tradeItem -> {
                TradeItem matchItem = matchItems.stream().filter(
                        item -> item.getSkuId().equals(tradeItem.getSkuId())).findFirst().orElse(null);
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
     * 调用校验与封装单个订单信息 - [后端代客下单]
     * 业务员app/商家-共用
     *
     * @return
     */
    public PileTrade wrapperBackendCommitTrade(Operator operator, CompanyInfoVO companyInfo, StoreInfoResponse
            storeInfoResponse, TradeCreateRequest tradeCreateRequest) {
        //1.获取代客下单操作人信息
        Seller seller = Seller.fromOperator(operator);

        //2.获取商家信息
        Supplier supplier = Supplier.builder()
                .isSelf(storeInfoResponse.getCompanyType() == com.wanmi.sbc.common.enums.CompanyType.PLATFORM)
                .supplierCode(companyInfo.getCompanyCode())
                .supplierId(companyInfo.getCompanyInfoId())
                .employeeId(operator.getUserId())
                .supplierName(companyInfo.getSupplierName())
                .employeeName(operator.getName())
                .freightTemplateType(storeInfoResponse.getFreightTemplateType())
                .storeName(storeInfoResponse.getStoreName())
                .storeId(storeInfoResponse.getStoreId())
                .companyType(companyInfo.getCompanyType())
                .build();

        //3.获取并验证客户信息
//        Customer customer = verifyService.verifyCustomer(tradeCreateRequest.getCustom());
        CustomerGetByIdResponse customer = verifyService.verifyCustomer(tradeCreateRequest.getCustom());
        if (storeInfoResponse.getCompanyType().equals(CompanyType.SUPPLIER)) {
            verifyService.verifyCustomerWithSupplier(customer.getCustomerId(), companyInfo.getCompanyInfoId());
        }

        CustomerLevelByCustomerIdAndStoreIdRequest request = CustomerLevelByCustomerIdAndStoreIdRequest.builder()
                .customerId(customer.getCustomerId())
                .storeId(storeInfoResponse.getStoreId()).build();
        CommonLevelVO storeLevel = customerLevelQueryProvider.getCustomerLevelByCustomerIdAndStoreId(request)
                .getContext();

        //4.【公共方法】下单信息验证, 将信息包装成订单
        return this.validateAndWrapperTrade(new PileTrade(),
                TradeParams.builder()
                        .backendFlag(true) //表示后端操作
                        .commitFlag(true) //表示下单
                        .marketingList(tradeCreateRequest.getTradeMarketingList())
                        .tradePrice(tradeCreateRequest.getTradePrice())
                        .tradeItems(tradeCreateRequest.getTradeItems())
                        .oldGifts(Collections.emptyList())//下单,非修改订单
                        .oldTradeItems(Collections.emptyList())//下单,非修改订单
                        .storeLevel(storeLevel)
                        .customer(customer)
                        .supplier(supplier)
                        .seller(seller)
                        .consigneeId(tradeCreateRequest.getConsigneeId())
                        .detailAddress(tradeCreateRequest.getConsigneeAddress())
                        .consigneeUpdateTime(tradeCreateRequest.getConsigneeUpdateTime())
                        .consignee(tradeCreateRequest.getConsignee())
                        .invoice(tradeCreateRequest.getInvoice())
                        .invoiceConsignee(tradeCreateRequest.getInvoiceConsignee())
                        .deliverWay(tradeCreateRequest.getDeliverWay())
                        .payType(tradeCreateRequest.getPayType())
                        .buyerRemark(tradeCreateRequest.getBuyerRemark())
                        .sellerRemark(tradeCreateRequest.getSellerRemark())
                        .encloses(tradeCreateRequest.getEncloses())
                        .ip(operator.getIp())
                        .wareHouseCode(tradeCreateRequest.getWareHouseCode())
                        .platform(operator.getPlatform())
                        .forceCommit(tradeCreateRequest.isForceCommit())
                        .distributeChannel(new DistributeChannel())
                        .wareId(tradeCreateRequest.getWareId())
                        .tradeWareHouse(wareHouseTurnToTrade(tradeCreateRequest.getWareHouseVO()))
                        /**设置优惠券*/
                        .couponCodeId(StringUtils.isNotBlank(tradeCreateRequest.getCouponCodeId()) ? tradeCreateRequest.getCouponCodeId() : null)
                        .build());
    }

    /**
     * 调用校验与封装单个订单信息 - [后端修改订单]
     * 业务员app/商家-共用
     *
     * @return
     */
    public PileTrade wrapperBackendRemedyTrade(PileTrade trade, Operator operator, TradeRemedyRequest tradeRemedyRequest) {
        tradeRemedyRequest.getInvoice().setOrderInvoiceId(
                Objects.nonNull(trade.getInvoice()) ?
                        trade.getInvoice().getOrderInvoiceId() : null);
        //【公共方法】修改订单信息验证, 将修改的信息包装成新订单
        return this.validateAndWrapperTrade(trade,
                TradeParams.builder()
                        .backendFlag(true) //表示后端操作
                        .commitFlag(false) //表示修改订单
                        .marketingList(tradeRemedyRequest.getTradeMarketingList())
                        .tradePrice(tradeRemedyRequest.getTradePrice())
                        .tradeItems(tradeRemedyRequest.getTradeItems())
                        .oldGifts(trade.getGifts()) //修改订单,设置旧赠品
                        .oldTradeItems(trade.getTradeItems()) //修改订单,设置旧商品
                        .storeLevel(null) //修改订单,客户,商家,代理人都无法修改,所以设置为null
                        .customer(null)
                        .supplier(null)
                        .seller(null)
                        .consigneeId(tradeRemedyRequest.getConsigneeId())
                        .detailAddress(tradeRemedyRequest.getConsigneeAddress())
                        .consigneeUpdateTime(tradeRemedyRequest.getConsigneeUpdateTime())
                        .consignee(tradeRemedyRequest.getConsignee())
                        .invoice(tradeRemedyRequest.getInvoice())
                        .invoiceConsignee(tradeRemedyRequest.getInvoiceConsignee())
                        .deliverWay(tradeRemedyRequest.getDeliverWay())
                        .payType(tradeRemedyRequest.getPayType())
                        .buyerRemark(tradeRemedyRequest.getBuyerRemark())
                        .sellerRemark(tradeRemedyRequest.getSellerRemark())
                        .encloses(tradeRemedyRequest.getEncloses())
                        .ip(operator.getIp())
                        .forceCommit(tradeRemedyRequest.isForceCommit())
                        .build());
    }

    /**
     * 验证下单信息并封装订单信息
     * 【公共方法】-客户下单(PC/H5/APP...), 商家代客下单/修改订单(supplier/employeeApp/supplierAPP...)
     * 1.验证tradeParams中的用户下单信息
     * 2.封装trade,方便后面持久化
     *
     * @param tradeParams 用户下单信息
     * @return 待入库的订单对象
     */
    public PileTrade validateAndWrapperTrade(PileTrade trade, TradeParams tradeParams) {
        //判断是否为秒杀抢购商品订单
        if (Objects.nonNull(tradeParams.getIsFlashSaleGoods()) && tradeParams.getIsFlashSaleGoods()) {
            trade.setIsFlashSaleGoods(tradeParams.getIsFlashSaleGoods());
        }

        // 2.1.设置订单基本信息(购买人,商家,代客下单操作人,收货地址,发票信息,配送方式,支付方式,备注,附件,操作人ip,订单商品,订单总价...)
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
            trade.setId(generatorService.generateTid());
            trade.setPlatform(tradeParams.getPlatform());
            trade.setOrderSource(tradeParams.getOrderSource());
            trade.setOrderType(OrderType.NORMAL_ORDER);
            trade.setShareUserId(tradeParams.getShareUserId());
            trade.setTradeWareHouse(tradeParams.getTradeWareHouse());
        }
        //发票信息(必须在收货地址下面-因为使用临时发票收货地,却未填写的时候,将使用订单商品收货地址作为发票收货地)
//        trade.setInvoice(wrapperTradeInvoice(tradeParams.getInvoice(), tradeParams.getInvoiceConsignee(),
//                trade.getConsignee()));
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


        // 2.2.订单中商品信息填充(同时设置商品的客户级别价格/客户指定价salePrice)
        TradeGoodsListVO skuList = this.getGoodsInfoResponse(trade);

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
        boolean throwFlag = verifyService.verifyPileGoodsTo(trade.getTradeItems(), tradeParams.getOldTradeItems(), skuList, trade.getSupplier()
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
                    i.setSplitPrice(i.getVipPrice().multiply(new BigDecimal(i.getNum())));
                    i.setPrice(i.getVipPrice());
                    i.setLevelPrice(i.getVipPrice());
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
        tradeParams.getMarketingList().forEach(i -> {
            List<TradeItem> items = trade.getTradeItems().stream().filter(s -> i.getSkuIds().contains(s.getSkuId()))
                    .collect(Collectors.toList());
            items.forEach(s -> s.getMarketingIds().add(i.getMarketingId()));
        });

        // 拼团订单--处理
        if (Objects.nonNull(tradeParams.getGrouponForm())) {
            dealGroupon(trade, tradeParams);
        }
        //营销信息
        this.wrapperMarketingForCommit(trade, tradeParams, tradeParams.getCustomer());

        // 2.6.赠品信息校验与填充
        List<String> giftIds = tradeParams.getMarketingList().stream().filter(parm -> CollectionUtils.isNotEmpty(parm.getGiftSkuIds())).flatMap(
                r -> r.getGiftSkuIds().stream()).distinct().collect(Collectors.toList());
        TradeGetGoodsResponse giftResp = this.getGoodsResponse(giftIds, trade.getWareId(), trade.getWareHouseCode());
        List<TradeItem> gifts = giftIds.stream().map(g -> TradeItem.builder().price(BigDecimal.ZERO).skuId(g)
                .build()).collect(Collectors.toList());
        verifyService.mergeGoodsInfo(gifts, giftResp);
        trade.setGifts(gifts);
        giftSet(trade);

        //2.8.计算满系营销、优惠券均摊价，并设置结算信息
        calcMarketingPrice(trade);

        // 2.9.计算并设置订单总价(已减去营销优惠总金额)
        trade.setTradePrice(calc(trade));
        if (throwFlag) {
            PileTradeItemSnapshot byCustomerId = pileTradeItemService.findByCustomerId(trade.getBuyer().getId());
            byCustomerId.getItemGroups().forEach(param -> {
                if (param.getSupplier().getStoreId().equals(trade.getSupplier()
                        .getStoreId())) {
                    param.setTradeMarketings(trade.getTradeMarketings());
                }
            });
            pileTradeItemService.updateTradeItemSnapshotNoRollback(byCustomerId);
            throw new SbcRuntimeException("K-050137");
        }

        // 2.10.计算运费
        TradePrice tradePrice = trade.getTradePrice();
        BigDecimal deliveryPrice = tradePrice.getDeliveryPrice();
        if (tradePrice.getDeliveryPrice() == null) {

            // 弃用商家模板 计算运费方式改变 采用平台模板 使用所有商品总金额计算运费
           /* deliveryPrice = this.calcTradeFreight(trade.getConsignee(), trade.getSupplier(), trade.getDeliverWay(),
                    tradePrice.getTotalPrice(), trade.getTradeItems(), trade.getGifts());*/
            deliveryPrice = BigDecimal.ZERO;
            tradePrice.setDeliveryPrice(deliveryPrice);
        }

        //判断是否为秒杀抢购订单
        if (Objects.nonNull(trade.getIsFlashSaleGoods()) && trade.getIsFlashSaleGoods()) {
            //秒杀商品是否包邮
            //获取秒杀抢购活动详情
            FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
                            .id(trade.getTradeItems().get(0).getFlashSaleGoodsId())
                            .build())
                    .getContext().getFlashSaleGoodsVO();
            if (flashSaleGoodsVO.getPostage().equals(1)) {
                deliveryPrice = new BigDecimal(0);
            }
        }

        // 2.11.计算订单总价(追加运费)
        tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
        if (tradePrice.isSpecial()) {
            // 2.12.【商品价格计算第③步】: 商品的 特价订单 均摊价 -> splitPrice
            pileTradeItemService.clacSplitPrice(trade.getTradeItems(), tradePrice.getPrivilegePrice());
            tradePrice.setTotalPrice(tradePrice.getPrivilegePrice().add(deliveryPrice));//应付金额 = 特价+运费
        } else {
            tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
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

        /**配送方式：第三方物流，收货地址为外省且满30件商品时，配送费可优惠2元/每箱;
         * 优惠方式为商品单价-2，统计优惠金额
         * */
//        long buySum = tradeParams.getTradeItems().stream().mapToLong(t -> t.getNum()).sum();
//
//        boolean freightCouponFlag = DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && trade.getConsignee().getProvinceId() != 430000 && 30 <= buySum;
//
//        log.info("trade.getDeliverWay {}", trade.getDeliverWay());
//
//        log.info("freightCouponFlag {}", freightCouponFlag);
//        BigDecimal freightCouponPrice = BigDecimal.valueOf(buySum).multiply(BigDecimal.valueOf(2));
//        if (freightCouponFlag) {
//            BigDecimal subtract = tradePrice.getTotalPrice().subtract(freightCouponPrice);
//            //支付金额小于等于0
//            if (subtract.compareTo(BigDecimal.ZERO) == -1 || subtract.compareTo(BigDecimal.ZERO) == 0) {
//                tradePrice.setDeliveryCouponPrice(tradePrice.getTotalPrice());
//                tradePrice.setTotalPrice(BigDecimal.ZERO);
//                //均摊
//                int size = trade.getTradeItems().size();
//                //订单总价
//                BigDecimal totalSplitPrice = trade.getTradeItems().stream().map(tradeItem -> tradeItem.getSplitPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
//                //运费优惠金额
//                BigDecimal totalDeliveryCoupon = BigDecimal.ZERO;
//                for (int i = 0; i < size; i++) {
//                    TradeItem deliverTradeItem = trade.getTradeItems().get(i);
//                    if (i == size - 1) {
//                        deliverTradeItem.setSplitPrice(deliverTradeItem.getSplitPrice().subtract(tradePrice.getDeliveryCouponPrice().subtract(totalDeliveryCoupon)));
//                    } else {
//                        //计算优惠均摊 = 商品均摊价/商品总价 * 优惠总金额
//                        BigDecimal multiply = tradePrice.getDeliveryCouponPrice().multiply(
//                                deliverTradeItem.getSplitPrice().divide(totalSplitPrice, 2, BigDecimal.ROUND_HALF_UP)
//                        ).setScale(2);
//                        totalDeliveryCoupon.add(multiply);
//                        deliverTradeItem.setSplitPrice(deliverTradeItem.getSplitPrice().subtract(multiply));
//                    }
//                }
//            } else {
//                tradePrice.setDeliveryCouponPrice(freightCouponPrice);
//                //订单实付金额 = 应付金额 - 运费优惠金额
//                tradePrice.setTotalPrice(tradePrice.getTotalPrice().subtract(freightCouponPrice));
//                //订单商品价格减2
//                trade.getTradeItems().forEach(ti -> {
//                    ti.setPrice(ti.getPrice().subtract(BigDecimal.valueOf(2)));
//                    //均摊价每箱减2
//                    ti.setSplitPrice(ti.getSplitPrice().subtract(BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(ti.getNum()))));
//                });
//            }
//        }

        Optional<Long> _giftNum = trade.getGifts().stream()
                .map(TradeItem::getNum).reduce((sum, item) -> {
                    sum += item;
                    return sum;
                });
        if (_giftNum.isPresent()) {
            goodsTotalNum += _giftNum.get();
        }

//        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
//                .builder().deliveryAddressId(tradeParams.getConsigneeId()).build()).getContext();
//
//        //校验商品所在店铺配置免费配送范围
//        FreightTemplateDeliveryAreaByStoreIdResponse freightTemplateDeliveryAreaByStoreIdResponse = freightTemplateDeliveryAreaQueryProvider
//                .query(FreightTemplateDeliveryAreaListRequest
//                        .builder().storeId(trade.getSupplier().getStoreId()).build()).getContext();
//        //常规
//        FreightTemplateDeliveryAreaVO freightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getFreightTemplateDeliveryAreaVO();
//
//        //乡镇满十件
//        FreightTemplateDeliveryAreaVO areaTenFreightTemplateDeliveryAreaVO = freightTemplateDeliveryAreaByStoreIdResponse.getAreaTenFreightTemplateDeliveryAreaVO();
//
//        if (
//            //常规
//                (Objects.nonNull(freightTemplateDeliveryAreaVO) && Objects.nonNull(freightTemplateDeliveryAreaVO.getDestinationArea())
//                        && (ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getProvinceId().toString())
//                        || ArrayUtils.contains(freightTemplateDeliveryAreaVO.getDestinationArea(), response.getCityId().toString())))
//        ) {
//            if(
//                //乡镇满十件(免费店配)
//                    (Objects.nonNull(areaTenFreightTemplateDeliveryAreaVO) && Objects.nonNull(areaTenFreightTemplateDeliveryAreaVO.getDestinationAreaName())
//                            && (checkDeliveryDestination(response.getDeliveryAddress(), Stream.of(areaTenFreightTemplateDeliveryAreaVO.getDestinationAreaName()).collect(Collectors.toList()))
//                            || checkDeliveryDestination(response.getDetailDeliveryAddress(), Stream.of(areaTenFreightTemplateDeliveryAreaVO.getDestinationAreaName()).collect(Collectors.toList()))))
//            ){
//                if(goodsTotalNum < 10){
//                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "乡镇免费店配数量不足！");
//                }
//            }
//        }

        trade.setGoodsTotalNum(goodsTotalNum);
        return trade;
    }

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
     * 校验库存—— 通过wms校验库存
     */
    private void verifyGoodsByWMS(Trade trade, TradeParams tradeParams) {
        //1.合并赠品
        List<TradeItem> tradeItemList = trade.getTradeItems();
        List<TradeItem> tradeItems = KsBeanUtil.convert(tradeItemList, TradeItem.class);
        tradeItems.addAll(trade.getGifts());
        //2.根据 erpNo 分组(普通商品)
        Map<String, List<TradeItem>> tradeItemMap = tradeItems.stream().filter(t -> Objects.isNull(t.getGoodsInfoType()) || 1 != t.getGoodsInfoType())
                .collect(Collectors.groupingBy(TradeItem::getErpSkuNo));
        //特价商品
        Map<String, List<TradeItem>> specialTradeItemMap = tradeItems.stream().filter(t -> Objects.nonNull(t.getGoodsInfoType()) && 1 == t.getGoodsInfoType())
                .collect(Collectors.groupingBy(TradeItem::getErpSkuNo));
        //普通商品
        if (MapUtils.isNotEmpty(tradeItemMap)) {
            Map<String, Long> erpNoMap = new HashMap<>();
            for (String erp : tradeItemMap.keySet()) {
                Long num = tradeItemMap.get(erp).stream().mapToLong(TradeItem::getNum).sum();
                BigDecimal addStep = tradeItemMap.get(erp).get(0).getAddStep().setScale(0, BigDecimal.ROUND_HALF_UP);
                ;
                erpNoMap.put(erp, num * addStep.longValue());
            }
            //3.组装wms的请求数据
            List<String> erpGoodsInfoNos = new ArrayList<>(erpNoMap.keySet());
            if (wmsAPIFlag && wmsApiProperties.getInventoryFlag()) {
                String loatt04 = ERPWMSConstants.MAIN_WH;
                if ("WH01".equals(tradeParams.getWareHouseCode())) {
                    loatt04 = ERPWMSConstants.MAIN_WH;
                }
                if ("WH02".equals(tradeParams.getWareHouseCode())) {
                    loatt04 = ERPWMSConstants.SUB_WH;
                }
                /* try {*/
                InventoryQueryResponse response = requestWMSInventoryProvider.batchQueryInventory(BatchInventoryQueryRequest.builder()
                        .skuIds(erpGoodsInfoNos)
                        .CustomerID(StringUtils.isNotEmpty(tradeParams.getSupplier().getErpId()) ?
                                tradeParams.getSupplier().getErpId() : "XYY")
                        .WarehouseID(tradeParams.getWareHouseCode())
                        .Lotatt04(loatt04)
                        .build()).getContext();
                if (Objects.nonNull(response)
                        && CollectionUtils.isNotEmpty(response.getInventoryQueryReturnVO())
                        && response.getInventoryQueryReturnVO().size() == erpGoodsInfoNos.size()) {
                    List<InventoryQueryReturnVO> inventoryQueryReturnVOS = response.getInventoryQueryReturnVO();
                    inventoryQueryReturnVOS.stream().forEach(i -> {
                        Long buyNum = erpNoMap.get(i.getSku());
                        if (Objects.nonNull(i.getStockNum()) && i.getStockNum().compareTo(new BigDecimal(buyNum)) == -1) {
                            //发送wms的通知
                            orderProducerService.sendMessageToDelLackStock(GoodsLackStockRequest.builder()
                                    .erpGoodsNo(i.getSku()).lackNum(buyNum - i.getStockNum().setScale(0,
                                            BigDecimal.ROUND_DOWN).longValue()).build());
                            throw new SbcRuntimeException("K-050116");
                        }
                    });
                } else {
                    // 商品不存在
                    throw new SbcRuntimeException("K-050117");
                }
                /*}catch (Exception e){
                    if(e instanceof SbcRuntimeException){
                        SbcRuntimeException exception = (SbcRuntimeException)e;
                        if("K-050510".equals(exception.getErrorCode())){
                            throw new SbcRuntimeException("K-050510");
                        }
                    }
                }*/
            }
        }
        // 特价
        if (MapUtils.isNotEmpty(specialTradeItemMap)) {
            Map<String, Long> erpNoMap = new HashMap<>();
            for (String erp : specialTradeItemMap.keySet()) {
                Long num = specialTradeItemMap.get(erp).stream().mapToLong(TradeItem::getNum).sum();
                BigDecimal addStep = specialTradeItemMap.get(erp).get(0).getAddStep().setScale(0,
                        BigDecimal.ROUND_HALF_UP);
                erpNoMap.put(erp, num * addStep.longValue());
            }
            //3.组装wms的请求数据
            List<String> erpGoodsInfoNos = new ArrayList<>(erpNoMap.keySet());
            if (wmsAPIFlag && wmsApiProperties.getInventoryFlag()) {
                String loatt04 = ERPWMSConstants.MAIN_WH;
                if ("WH01".equals(tradeParams.getWareHouseCode())) {
                    loatt04 = ERPWMSConstants.MAIN_MARKETING_WH;
                }
                if ("WH02".equals(tradeParams.getWareHouseCode())) {
                    loatt04 = ERPWMSConstants.SUB_MARKETING_WH;
                }
                try {
                    InventoryQueryResponse response = requestWMSInventoryProvider.batchQueryInventory(BatchInventoryQueryRequest.builder()
                            .skuIds(erpGoodsInfoNos)
                            .CustomerID(StringUtils.isNotEmpty(tradeParams.getSupplier().getErpId()) ?
                                    tradeParams.getSupplier().getErpId() : "XYY")
                            .WarehouseID(tradeParams.getWareHouseCode())
                            .Lotatt04(loatt04)
                            .build()).getContext();
                    if (Objects.nonNull(response)
                            && CollectionUtils.isNotEmpty(response.getInventoryQueryReturnVO())
                            && response.getInventoryQueryReturnVO().size() == erpGoodsInfoNos.size()) {
                        List<InventoryQueryReturnVO> inventoryQueryReturnVOS = response.getInventoryQueryReturnVO();
                        inventoryQueryReturnVOS.stream().forEach(i -> {
                            Long buyNum = erpNoMap.get(i.getSku());
                            if (Objects.nonNull(i.getStockNum()) && i.getStockNum().compareTo(new BigDecimal(buyNum)) == -1) {
                                // 特价商品不发送到货通知
                                throw new SbcRuntimeException("K-050509");
                            }
                        });
                    } else {
                        // 商品不存在
                        throw new SbcRuntimeException("K-050509");
                    }
                } catch (Exception e) {
                    if (e instanceof SbcRuntimeException) {
                        SbcRuntimeException exception = (SbcRuntimeException) e;
                        if ("K-050510".equals(exception.getErrorCode())) {
                            throw new SbcRuntimeException("K-050510");
                        }
                    }
                }
            }
        }
    }


    /**
     * 处理分销订单
     *
     * @param trade
     * @param tradeParams
     */
    private void dealDistribution(Trade trade, TradeParams tradeParams) {
        if ((Objects.isNull(trade.getIsFlashSaleGoods()) || (Objects.nonNull(trade.getIsFlashSaleGoods()) && !trade.getIsFlashSaleGoods())) && DefaultFlag.YES.equals(tradeParams.getOpenFlag())) {
            if (DefaultFlag.YES.equals(tradeParams.getStoreBagsFlag())) {
                // 开店礼包商品，使用市场价，且不计算营销
                trade.getTradeItems().forEach(item -> {
                    item.setSplitPrice(item.getOriginalPrice().multiply(new BigDecimal(item.getNum())));
                    item.setPrice(item.getOriginalPrice());
                    item.setLevelPrice(item.getOriginalPrice());
                });
                trade.setStoreBagsFlag(DefaultFlag.YES);
                tradeParams.setMarketingList(new ArrayList<>());
                trade.setTradeMarketings(new ArrayList<>());
                trade.setTradeCoupon(null);
                trade.setStoreBagsInviteeId(tradeParams.getDistributeChannel().getInviteeId());
            } else {
                // 非开店礼包，且为分销商品
                // 1.将分销商品设回市场价
                DistributeChannel channel = tradeParams.getDistributeChannel();
                trade.getTradeItems().forEach(item -> {
                    if (DistributionGoodsAudit.CHECKED == item.getDistributionGoodsAudit()
                            && DefaultFlag.YES.equals(tradeParams.getStoreOpenFlag())
                            && ChannelType.PC_MALL != channel.getChannelType()) {
                        item.setSplitPrice(item.getOriginalPrice().multiply(new BigDecimal(item.getNum())));
                        item.setPrice(item.getOriginalPrice());
                        item.setLevelPrice(item.getOriginalPrice());
                        // 初步计算分销佣金
                        item.setDistributionCommission(item.getSplitPrice().multiply(item.getCommissionRate()));
                    } else {
                        item.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    }
                });

                List<TradeItem> distributionTradeItems = trade.getTradeItems().stream()
                        .filter(item -> DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit())).collect(Collectors.toList());

                // 2.设置分销相关字段
                if (distributionTradeItems.size() != 0) {

                    MultistageSettingGetResponse multistageSetting =
                            distributionCacheQueryProvider.getMultistageSetting().getContext();

                    // 2.1.查询佣金受益人列表
                    DistributionCustomerListForOrderCommitRequest request =
                            new DistributionCustomerListForOrderCommitRequest();
                    request.setBuyerId(trade.getBuyer().getId());
                    request.setCommissionPriorityType(
                            CommissionPriorityType.fromValue(multistageSetting.getCommissionPriorityType().toValue())
                    );
                    request.setCommissionUnhookType(
                            CommissionUnhookType.fromValue(multistageSetting.getCommissionUnhookType().toValue())
                    );
                    request.setDistributorLevels(multistageSetting.getDistributorLevels());
                    request.setInviteeId(channel.getInviteeId());
                    request.setIsDistributor(tradeParams.getIsDistributor());
                    List<DistributionCustomerSimVO> inviteeCustomers = distributionCustomerQueryProvider
                            .listDistributorsForOrderCommit(request).getContext().getDistributorList();

                    List<TradeDistributeItem> distributeItems = new ArrayList<>();

                    // 商品分销佣金map(记录每个分销商品基础分销佣金)
                    Map<String, BigDecimal> skuBaseCommissionMap = new HashMap<>();
                    distributionTradeItems.forEach(item ->
                            skuBaseCommissionMap.put(item.getSkuId(), item.getDistributionCommission())
                    );

                    // 2.2.根据受益人列表设置分销相关字段
                    BigDecimal totalCommission = BigDecimal.ZERO;
                    if (CollectionUtils.isNotEmpty(inviteeCustomers)) {

                        for (int idx = 0; idx < inviteeCustomers.size(); idx++) {

                            DistributionCustomerSimVO customer = inviteeCustomers.get(idx);

                            DistributorLevelVO level = multistageSetting.getDistributorLevels().stream()
                                    .filter(l -> l.getDistributorLevelId().equals(customer.getDistributorLevelId())).findFirst().get();

                            if (idx == 0) {
                                // 2.2.1.设置返利人信息
                                distributionTradeItems.forEach(item -> {
                                    // 设置trade.tradeItems
                                    item.setDistributionCommission(
                                            DistributionCommissionUtils.calDistributionCommission(
                                                    item.getDistributionCommission(), level.getCommissionRate())
                                    );
                                    item.setCommissionRate(item.getCommissionRate().multiply(level.getCommissionRate()));

                                    // 设置trade.distributeItems
                                    TradeDistributeItem distributeItem = new TradeDistributeItem();
                                    distributeItem.setGoodsInfoId(item.getSkuId());
                                    distributeItem.setNum(item.getNum());
                                    distributeItem.setActualPaidPrice(item.getSplitPrice());
                                    distributeItem.setCommissionRate(item.getCommissionRate());
                                    distributeItem.setCommission(item.getDistributionCommission());
                                    distributeItems.add(distributeItem);
                                });

                                // 设置trade.[inviteeId,distributorId,distributorName,commission]
                                trade.setInviteeId(customer.getCustomerId());
                                trade.setDistributorId(customer.getDistributionId());
                                trade.setDistributorName(customer.getCustomerName());
                                trade.setCommission(
                                        distributeItems.stream().map(TradeDistributeItem::getCommission)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                );
                                // 累加返利人佣金至总佣金
                                totalCommission = totalCommission.add(trade.getCommission());

                            } else {
                                // 2.2.2.设置提成人信息
                                BigDecimal percentageTotal = BigDecimal.ZERO;
                                for (int i = 0; i < distributeItems.size(); i++) {
                                    // 设置trade.distributeItems.commissions
                                    TradeDistributeItem item = distributeItems.get(i);
                                    TradeDistributeItemCommission itemCommission = new TradeDistributeItemCommission();
                                    itemCommission.setCustomerId(customer.getCustomerId());
                                    itemCommission.setDistributorId(customer.getDistributionId());
                                    itemCommission.setCommission(
                                            skuBaseCommissionMap.get(item.getGoodsInfoId()).multiply(
                                                    level.getPercentageRate()).setScale(2, BigDecimal.ROUND_DOWN));
                                    item.getCommissions().add(itemCommission);
                                    percentageTotal = percentageTotal.add(itemCommission.getCommission());
                                }

                                // 设置trade.commissions
                                TradeCommission tradeCommission = new TradeCommission();
                                tradeCommission.setCustomerId(customer.getCustomerId());
                                tradeCommission.setCommission(percentageTotal);
                                tradeCommission.setDistributorId(customer.getDistributionId());
                                tradeCommission.setCustomerName(customer.getCustomerName());
                                trade.getCommissions().add(tradeCommission);

                                // 累加提成人佣金至总佣金
                                totalCommission = totalCommission.add(tradeCommission.getCommission());
                            }

                        }

                        // 求和分销商品总佣金 trade.distributeItems.totalCommission
                        distributeItems.forEach(item -> {
                            // 追加返利人佣金
                            item.setTotalCommission(item.getCommission());
                            // 追加提成人佣金
                            item.getCommissions().forEach(i ->
                                    item.setTotalCommission(item.getTotalCommission().add(i.getCommission()))
                            );
                        });

                        // 设置总佣金、分销商品
                        trade.setTotalCommission(totalCommission);
                        trade.setDistributeItems(distributeItems);
                    }
                }
            }
        }
        //设置渠道信息、小店名称、小B-会员ID
        trade.setChannelType(tradeParams.getDistributeChannel().getChannelType());
        trade.setShopName(tradeParams.getShopName());
        trade.setDistributionShareCustomerId(tradeParams.getDistributeChannel().getInviteeId());
    }


    private CommonLevelVO fromCustomerLevel(CustomerLevelVO customerLevelVO) {
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
     * 查询店铺订单应付的运费(需要参数具体如下)
     * consignee 收货地址 - 省id,市id
     * supplier 店铺信息 - 店铺id-使用运费模板类型
     * deliverWay 配送方式
     * totalPrice 订单总价(扣除营销优惠后)
     * oldTradeItems 订单商品List - 均摊价(计算营销后),件数   ,体积,重量,使用的运费模板id
     * oldGifts 订单赠品List - 价格为0,件数   ,体积,重量,使用的运费模板id
     *
     * @param tradeParams
     * @return
     */
    public TradeFreightResponse getFreight(TradeParams tradeParams) {
        BigDecimal deliveryPrice = this.calcTradeFreight(tradeParams.getConsignee(), tradeParams.getSupplier(),
                tradeParams.getDeliverWay(),
                tradeParams.getTradePrice().getTotalPrice(), tradeParams.getOldTradeItems(), tradeParams.getOldGifts());

        TradeFreightResponse freightResponse = new TradeFreightResponse();
        freightResponse.setStoreId(tradeParams.getSupplier().getStoreId());
        freightResponse.setDeliveryPrice(deliveryPrice);
        return freightResponse;
    }

    /**
     * 查询平台运费模板 xyy
     * 查询店铺订单应付的运费(需要参数具体如下)
     * consignee 收货地址 - 省id,市id
     * supplier 店铺信息  只有平台模板   店铺id 默认就为-1
     * totalPrice 订单总价(扣除营销优惠后)
     * oldTradeItems 订单商品List - 均摊价(计算营销后),件数   ,体积,重量,使用的运费模板id
     *
     * @param tradeParamsList
     * @return
     */
    public List<TradeFreightResponse> getBossFreight(List<TradeParams> tradeParamsList) {
        TradeParams tradeParams = tradeParamsList.stream().findFirst().get();
        tradeParams.getSupplier().setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        // 商品总金额
        BigDecimal totalPrice = tradeParamsList.stream().map(param ->
                param.getTradePrice().getTotalPrice()).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        //  订单总运费
        BigDecimal deliveryPrice = this.calcBossTradeFreight(tradeParams.getConsignee(), tradeParams.getSupplier(),
                totalPrice);
        List<TradeFreightResponse> freightResponseList = tradeParamsList.stream().map(param -> {
            TradeFreightResponse freightResponse = new TradeFreightResponse();
            freightResponse.setStoreId(param.getSupplier().getStoreId());
            // 订单金额如为零
            if (totalPrice.compareTo(BigDecimal.ZERO) == Constants.no) {
                // deliveryPrice 订单运费不为0
                if (deliveryPrice.compareTo(BigDecimal.ZERO) == Constants.yes) {
                    freightResponse.setDeliveryPrice(deliveryPrice.divide(new BigDecimal(tradeParamsList.size()), 2, BigDecimal.ROUND_HALF_UP));
                }
            } else {
                freightResponse.setDeliveryPrice(param.getTradePrice().getTotalPrice().compareTo(BigDecimal.ZERO) == Constants.no ?
                        BigDecimal.ZERO : deliveryPrice.compareTo(BigDecimal.ZERO) == Constants.no ? BigDecimal.ZERO :
                        param.getTradePrice().getTotalPrice().divide(
                                        totalPrice.divide(deliveryPrice, 4, BigDecimal.ROUND_HALF_UP), 4, BigDecimal.ROUND_HALF_UP).
                                setScale(1, BigDecimal.ROUND_HALF_UP)
                );
            }
            return freightResponse;
        }).collect(Collectors.toList());
        return freightResponseList;
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
    public BigDecimal calcBossTradeFreight(Consignee consignee, Supplier supplier, BigDecimal
            totalPrice) {
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
    public BigDecimal calcTradeFreight(Consignee consignee, Supplier supplier, DeliverWay deliverWay, BigDecimal
            totalPrice, List<TradeItem> goodsList, List<TradeItem> giftList) {
        BigDecimal freight = BigDecimal.ZERO;
        if (DefaultFlag.NO.equals(supplier.getFreightTemplateType())) {
            //1. 店铺运费模板计算
            FreightTemplateStoreVO templateStore;
            List<FreightTemplateStoreVO> storeTemplateList =
                    freightTemplateStoreQueryProvider.listByStoreIdAndDeleteFlag(
                            FreightTemplateStoreListByStoreIdAndDeleteFlagRequest.builder()
                                    .storeId(supplier.getStoreId()).deleteFlag(DeleteFlag.NO).build()
                    ).getContext().getFreightTemplateStoreVOList();
//            List<FreightTemplateStoreVO> storeTemplateList = freightTemplateStoreRepository.findByAll(supplier
//                    .getStoreId(), DeleteFlag.NO);
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
            // 2.4.计算剩余的每个模板的运费
            final Long tempId = maxTemplate.getFreightTempId();
            freight = templateList.stream().map(temp -> getSingleTemplateFreight(temp, tempId, templateGoodsMap))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        }
        return freight;
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
    private boolean getFreeFreightFlag(FreightTemplateGoodsVO temp, Map<Long, TradeItem> templateGoodsMap, DeliverWay
            deliverWay, Long provId, Long cityId) {
        if (DefaultFlag.YES.equals(temp.getSpecifyTermFlag())) {
            ValuationType valuationType = temp.getValuationType();
            List<FreightTemplateGoodsFreeVO> freeTemplateList = temp.getFreightTemplateGoodsFrees();
            Optional<FreightTemplateGoodsFreeVO> freeOptional = freeTemplateList.stream().filter(free -> matchArea(
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
     * 计算某个单品模板的运费
     *
     * @param temp             单品运费模板
     * @param freightTempId    需要计算首件运费的配送地模板id
     * @param templateGoodsMap 按模板id分组的商品汇总信息
     * @return 模板的总运费
     */
    private BigDecimal getSingleTemplateFreight(FreightTemplateGoodsVO temp, Long freightTempId, Map<Long, TradeItem>
            templateGoodsMap) {
        //是否需要计算首件运费标识
        boolean startFlag = temp.getFreightTempId().equals(freightTempId);
        TradeItem traItem = templateGoodsMap.get(temp.getFreightTempId());

        switch (temp.getValuationType()) {
            case NUMBER: //按件数
                return startFlag ? getStartAndPlusFreight(BigDecimal.valueOf(traItem.getNum()), temp.getExpTemplate())
                        : getPlusFreight(BigDecimal.valueOf(traItem.getNum()), temp.getExpTemplate());
            case WEIGHT: //按重量
                return startFlag ? getStartAndPlusFreight(traItem.getGoodsWeight(), temp.getExpTemplate())
                        : getPlusFreight(traItem.getGoodsWeight(), temp.getExpTemplate());
            case VOLUME: //按体积
                return startFlag ? getStartAndPlusFreight(traItem.getGoodsCubage(), temp.getExpTemplate())
                        : getPlusFreight(traItem.getGoodsCubage(), temp.getExpTemplate());
            case WEIGHTBYNUM://按重量/件
                return startFlag ? getWeightByNumStartAndPlusFreight(traItem.getGoodsWeight(),
                        BigDecimal.valueOf(traItem.getNum()), temp.getExpTemplate())
                        : getWeightByNumPlusFreight(traItem.getGoodsWeight(), BigDecimal.valueOf(traItem.getNum()), temp.getExpTemplate());
            default:
                return BigDecimal.ZERO;
        }
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
            return expTemplate.getFreightStartPrice().add(itemCount.divide(num, 10, BigDecimal.ROUND_UP).subtract(expTemplate.getFreightStartNum())
                            .divide(expTemplate.getFreightPlusNum(), 0, BigDecimal.ROUND_UP)
                            .multiply(expTemplate.getFreightPlusPrice()))
                    .multiply(num);
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
                matchArea(exp.getDestinationArea(), provId, cityId)).findFirst();
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
     * 匹配配送地区
     *
     * @param areaStr 存储的逗号相隔的areaId(provId,cityId都有可能)
     * @param provId  收货省份id
     * @param cityId  收货城市id
     * @return 是否匹配上
     */
    private boolean matchArea(String areaStr, Long provId, Long cityId) {
        String[] arr = areaStr.split(",");
        return Arrays.stream(arr).anyMatch(area -> area.equals(String.valueOf(provId)))
                || Arrays.stream(arr).anyMatch(area -> area.equals(String.valueOf(cityId)));
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
                    templateGoodsMap.put(goods.getFreightTempId(), TradeItem.builder()
                            .freightTempId(goods.getFreightTempId())
                            .num(goods.getNum())
                            .goodsWeight(goods.getGoodsWeight().multiply(BigDecimal.valueOf(goods.getNum())))
                            .goodsCubage(goods.getGoodsCubage().multiply(BigDecimal.valueOf(goods.getNum())))
                            .splitPrice(goods.getSplitPrice() == null ? BigDecimal.ZERO : goods.getSplitPrice())
                            .build());
                } else {
                    item.setNum(item.getNum() + goods.getNum());
                    item.setGoodsWeight(item.getGoodsWeight().add(goods.getGoodsWeight().multiply(BigDecimal.valueOf
                            (goods.getNum()))));
                    item.setGoodsCubage(item.getGoodsCubage().add(goods.getGoodsCubage().multiply(BigDecimal.valueOf
                            (goods.getNum()))));
                    item.setSplitPrice(item.getSplitPrice().add(goods.getSplitPrice() == null ? BigDecimal.ZERO :
                            goods.getSplitPrice()));
                }
            });
        }
    }

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     */
    public TradeGetGoodsResponse getGoodsResponse(List<String> skuIds, Long wareId, String wareHouseCode) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return new TradeGetGoodsResponse();
        }
        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(skuIds)
                .isHavSpecText(Constants.yes)
                .wareId(wareId)
                .wareHouseCode(wareHouseCode)
                .build();
        GoodsInfoViewByIdsResponse response = goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
        TradeGetGoodsResponse goodsResponse = new TradeGetGoodsResponse();
        goodsResponse.setGoodses(response.getGoodses());
        goodsResponse.setGoodsInfos(response.getGoodsInfos());
        return goodsResponse;
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

    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param tid                 订单id
     * @param tradeDeliverRequest 发货请求参数结构
     */
    public void deliveryCheck(String tid, TradeDeliverRequest tradeDeliverRequest) {
        PileTrade trade = detail(tid);
        Map<String, TradeItem> skusMap = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                Function.identity()));
        Map<String, TradeItem> giftsMap = trade.getGifts().stream().collect(Collectors.toMap(TradeItem::getSkuId,
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
     * 根据用户提交的收货地址信息封装对象
     *
     * @param consigneeId         选择的收货地址id
     * @param detailAddress       详细地址(包括省市区)
     * @param consigneeUpdateTime 地址更新时间 - 可能已经用不到了
     * @param consigneeTmp        用户提交的临时收货地址
     * @return 封装后的收货地址对象
     */
    private Consignee wrapperConsignee(String consigneeId, String detailAddress, String consigneeUpdateTime,
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
                    .build();
        }
    }

    /**
     * 根据用户提交的发票信息封装对象
     * 主要是为了补充 联系人 与 联系地址
     *
     * @param invoice             发票信息(至少缺联系人与联系地址)
     * @param invoiceConsigneeTmp 订单发票临时收货地址
     * @param consignee           订单商品收货地址
     * @return 完整的发票信息
     */
    private Invoice wrapperTradeInvoice(Invoice invoice, Consignee invoiceConsigneeTmp, Consignee consignee) {
        if (invoice.getType() != -1) {
            // 1.若用户选择了某个发票收货地址,查询该地址的联系人与联系方式
            if (StringUtils.isNotBlank(invoice.getAddressId())) {
                BaseResponse<CustomerDeliveryAddressByIdResponse> customerDeliveryAddressByIdResponseBaseResponse =
                        tradeCacheService.getCustomerDeliveryAddressById(invoice.getAddressId());
                CustomerDeliveryAddressByIdResponse customerDeliveryAddressByIdResponse =
                        customerDeliveryAddressByIdResponseBaseResponse.getContext();
                invoice.setPhone(customerDeliveryAddressByIdResponse.getConsigneeNumber());
                invoice.setContacts(customerDeliveryAddressByIdResponse.getConsigneeName());
                invoice.setProvinceId(customerDeliveryAddressByIdResponse.getProvinceId());
                invoice.setCityId(customerDeliveryAddressByIdResponse.getCityId());
                invoice.setAreaId(customerDeliveryAddressByIdResponse.getAreaId());
            }
            // 2.若用户没有选择发货地址，使用临时地址(代客下单特殊-可以传发票临时收货地址)
            else {
                // 2.1.临时地址为null，就用收货地址
                if (Objects.isNull(invoiceConsigneeTmp) || Objects.isNull(invoiceConsigneeTmp.getProvinceId())) {
                    invoice.setPhone(consignee.getPhone());
                    invoice.setContacts(consignee.getName());
                    invoice.setProvinceId(consignee.getProvinceId());
                    invoice.setCityId(consignee.getCityId());
                    invoice.setAreaId(consignee.getAreaId());
                    invoice.setAddress(consignee.getAddress());//依赖了前面步骤中封装的收货地址信息
                }
                // 2.2.使用填写的临时地址
                else {
                    invoice.setPhone(invoiceConsigneeTmp.getPhone());
                    invoice.setContacts(invoiceConsigneeTmp.getName());
                    invoice.setProvinceId(invoiceConsigneeTmp.getProvinceId());
                    invoice.setCityId(invoiceConsigneeTmp.getCityId());
                    invoice.setAreaId(invoiceConsigneeTmp.getAreaId());
                    invoice.setAddress(invoiceConsigneeTmp.getAddress());
                }
            }

            // 3.校验与填充增票信息
            if (invoice.getType() == 1) {
                SpecialInvoice spInvoice = invoice.getSpecialInvoice();
                CustomerInvoiceByIdAndDelFlagRequest customerInvoiceByCustomerIdRequest =
                        new CustomerInvoiceByIdAndDelFlagRequest();
                customerInvoiceByCustomerIdRequest.setCustomerInvoiceId(spInvoice.getId());
                BaseResponse<CustomerInvoiceByIdAndDelFlagResponse> customerInvoiceByIdAndDelFlagResponseBaseResponse = tradeCacheService.getCustomerInvoiceByIdAndDelFlag(spInvoice.getId());
                CustomerInvoiceByIdAndDelFlagResponse customerInvoiceByIdAndDelFlagResponse =
                        customerInvoiceByIdAndDelFlagResponseBaseResponse.getContext();
                if (Objects.nonNull(customerInvoiceByIdAndDelFlagResponse)) {
                    if (customerInvoiceByIdAndDelFlagResponse.getCheckState() != CheckState.CHECKED) {
                        throw new SbcRuntimeException("K-010013");
                    }
                    spInvoice.setAccount(customerInvoiceByIdAndDelFlagResponse.getBankNo());
                    spInvoice.setIdentification(customerInvoiceByIdAndDelFlagResponse.getTaxpayerNumber());
                    spInvoice.setAddress(customerInvoiceByIdAndDelFlagResponse.getCompanyAddress());
                    spInvoice.setBank(customerInvoiceByIdAndDelFlagResponse.getBankName());
                    spInvoice.setCompanyName(customerInvoiceByIdAndDelFlagResponse.getCompanyName());
                    spInvoice.setPhoneNo(customerInvoiceByIdAndDelFlagResponse.getCompanyPhone());
                }
            }
        }
        return invoice;
    }

    /**
     * 创建订单-入库
     *
     * @param trade
     * @param operator
     */
    @Transactional
    public PileTrade create(PileTrade trade, Operator operator) {
        // 1.下单校验店铺有效性, 校验店铺支持的发票项
        verifyService.verifyStore(Collections.singletonList(trade.getSupplier().getStoreId()));
        //囤货不校验发票
//        if (operator.getPlatform() != Platform.SUPPLIER) {
//            verifyService.verifyInvoice(trade.getInvoice(), trade.getSupplier());
//        }

        // 3.初始化订单提交状态
        FlowState flowState = FlowState.INIT;
        AuditState auditState;
        //是否开启订单审核（同时判断是否为秒杀抢购商品订单）
        Boolean orderAuditSwitch = tradeCacheService.isSupplierOrderAudit();
        logger.info("PileTradeService.create orderAuditSwitch:{}",orderAuditSwitch);

        //如果是秒杀抢购商品不需要审核
        if (Objects.nonNull(trade.getIsFlashSaleGoods()) && trade.getIsFlashSaleGoods()) {
            flowState = FlowState.AUDIT;
            auditState = AuditState.CHECKED;
            orderAuditSwitch = Boolean.FALSE;
        }
        // 如果是拼团订单商品不需要审
        else if (Objects.nonNull(trade.getGrouponFlag()) && trade.getGrouponFlag()) {
            auditState = AuditState.CHECKED;
            orderAuditSwitch = Boolean.FALSE;
        } else {
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
        }
        trade.setTradeState(TradeState
                .builder()
                .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                .flowState(flowState)
                .payState(PayState.NOT_PAID)
                .createTime(LocalDateTime.now())
                .build());

        // 4.若订单审核关闭了,直接创建订单开票跟支付单
        logger.info("PileTradeService.create createPayOrder trade.flowState:{}",trade.getTradeState().getFlowState());
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
                    // 线下支付默认一个小时后取消
                    if (PayType.OFFLINE.name().equals(trade.getPayInfo().getPayTypeName())) {
                        min = 60;
                    }
                    trade.setOrderTimeOut(LocalDateTime.now().plusMinutes(min));
                    orderProducerService.cancelPileOrder(trade.getId(), min * 60 * 1000L);
                    log.info("订单号：{}，MQ取消订单生产者运行成功",trade.getId());
                }
            }
        }

        trade.appendTradeEventLog(new TradeEventLog(operator, "创建囤货订单", "创建囤货订单", LocalDateTime.now()));
        trade.setWMSPushFlag(true);
        String verifyCode = "";
        //先货后款
        boolean goodsFirst = Objects.equals(auditState, AuditState.CHECKED) && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        logger.info("PileTradeService.create trade.flowState:{}",trade.getTradeState().getFlowState());
        // 5.订单入库
        addTrade(trade);
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
            orderProducerService.insertCompany(company);
        }

        if (Platform.SUPPLIER.equals(operator.getPlatform())) {
            this.operationLogMq.convertAndSend(operator, "代客下单", "订单号" + trade.getId());
        }
        //推送短信
        if (goodsFirst) {
            //自提订单
            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
               /* smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{trade.getConsignee().getPhone()},
                        trade.getId(), verifyCode);*/
                sendPickUpMessage(trade);
            }
        }
        return trade;
    }

    /**
     * 创建积分订单-入库
     *
     * @param trade
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public PileTrade createPoints(PileTrade trade, Operator operator) {
        // 初始化订单提交状态
        trade.setTradeState(TradeState
                .builder()
                .deliverStatus(DeliverStatus.NOT_YET_SHIPPED)
                .flowState(FlowState.AUDIT)
                .createTime(LocalDateTime.now())
                .build());

        // 创建订单开票跟支付单
        createPayOrder(trade, operator, false);

        // 创建对账单
        saveAccountRecord(trade);
        trade.getTradeState().setAuditState(AuditState.CHECKED);
        trade.getTradeState().setPayState(PayState.PAID);
        trade.appendTradeEventLog(new TradeEventLog(operator, "创建订单", "创建订单", LocalDateTime.now()));
        // 订单入库
        addTrade(KsBeanUtil.convert(trade, PileTrade.class));

        return trade;
    }

    /**
     * 更新订单
     *
     * @param tradeUpdateRequest
     */
    @LcnTransaction
    public void updateTradeInfo(TradeUpdateRequest tradeUpdateRequest) {
        updateTrade(KsBeanUtil.convert(tradeUpdateRequest.getTrade(), PileTrade.class));
    }

    /**
     * 生成对账单
     *
     * @param trade
     */
    private void saveAccountRecord(PileTrade trade) {
        // 根据订单id查询交易流水号
        BaseResponse<PayTradeRecordResponse> payTradeRecord = payQueryProvider.getTradeRecordByOrderOrParentCode(new
                TradeRecordByOrderOrParentCodeRequest(trade.getId(), trade.getParentId()));
        String tradeNo = Objects.isNull(payTradeRecord.getContext()) ? null : payTradeRecord.getContext().getTradeNo();
        // 添加对账记录
        AccountRecordAddRequest record = AccountRecordAddRequest.builder()
                .customerId(trade.getBuyer().getId())
                .customerName(trade.getBuyer().getName())
                .orderCode(trade.getId())
                .tradeNo(tradeNo)
                .orderTime(trade.getTradeState().getCreateTime())
                .payWay(PayWay.POINT)
                .storeId(trade.getSupplier().getStoreId())
                .supplierId(trade.getSupplier().getSupplierId())
                .tradeTime(LocalDateTime.now())
                .type((byte) 0)
                .build();
        // 计算积分结算价
        BigDecimal settlementPrice = trade.getTradeItems().stream()
                .map(tradeItem -> tradeItem.getSettlementPrice().multiply(BigDecimal.valueOf(tradeItem.getNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        record.setAmount(settlementPrice);
        record.setPoints(trade.getTradePrice().getPoints());
        accountRecordProvider.add(record);
    }

    /**
     * 批量创建订单
     *
     * @param trades   各店铺订单
     * @param operator 操作人
     * @return 订单提交结果集
     */
    @Transactional
    @LcnTransaction
    public List<TradeCommitResult> createBatch(List<PileTrade> trades, Operator operator) {
        List<TradeCommitResult> resultList = new ArrayList<>();
        final String parentId = generatorService.generatePoId();
        trades.forEach(
                trade -> {
                    //创建订单
                    try {
                        trade.setParentId(parentId);
                        trade.setOrderType(OrderType.NORMAL_ORDER);
                        PileTrade pileTrade = create(trade, operator);
                        Trade result = KsBeanUtil.convert(pileTrade, Trade.class);
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

        // 批量修改优惠券状态
        List<CouponCodeBatchModifyDTO> dtoList = new ArrayList<>();
        trades.forEach(trade -> {
            if (trade.getTradeCoupon() != null) {
                TradeCouponVO tradeCoupon = trade.getTradeCoupon();
                dtoList.add(CouponCodeBatchModifyDTO.builder()
                        .couponCodeId(tradeCoupon.getCouponCodeId())
                        .orderCode(trade.getId())
                        .useStatus(DefaultFlag.YES).build());
            }
        });
        if (dtoList.size() > 0) {
            couponCodeProvider.batchModify(CouponCodeBatchModifyRequest.builder().modifyDTOList(dtoList).build());
        }

        trades.forEach(trade -> {
            if (trade.getTradePrice().getPoints() != null && trade.getTradePrice().getPoints() > 0) {
                // 增加客户积分明细 扣除积分
                customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                        .customerId(trade.getBuyer().getId())
                        .type(OperateType.DEDUCT)
                        .serviceType(PointsServiceType.ORDER_DEDUCTION)
                        .points(trade.getTradePrice().getPoints())
                        .content(JSONObject.toJSONString(Collections.singletonMap("orderNo", trade.getId())))
                        .build());
            }
        });

        trades.stream().forEach(trade -> {
            MessageMQRequest messageMQRequest = new MessageMQRequest();
            if (!AuditState.REJECTED.equals(trade.getTradeState().getAuditState())) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
                map.put("id", trade.getId());
                map.put("orderType",1);
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

//        //向金蝶push订单数据
//        trades.stream().forEach(trade -> {
//            pushSalesOrderkingdee(trade);
//        });

        //ares埋点-订单-用户创建订单(同时发送多笔订单)
        orderAresService.dispatchFunction("addOrderList", trades);
        return resultList;
    }

//    public static void main(String[] args) {
//        TradeService tradeService = new TradeService();
//        Seller supplier = new Seller();
//        supplier.setAdminId("102");
//        Buyer buyer = new Buyer();
//        buyer.setCustomerErpId("CUST0005");
//        Supplier supplier1 = new Supplier();
//        supplier1.setErpId("BM000063");
//        TradePrice tradePrice = new TradePrice();
//        tradePrice.setDeliveryPrice(new BigDecimal(20));
//        TradeItem tradeItem = new TradeItem();
//        tradeItem.setSkuId("001.001.001.001");
//        tradeItem.setNum(10L);
//        tradeItem.setPrice(new BigDecimal(4.0));
//        List<TradeItem> list = new ArrayList<>();
//        list.add(tradeItem);
//        Trade trade = new Trade();
//        trade.setSeller(supplier);
//        trade.setBuyer(buyer);
//        trade.setSupplier(supplier1);
//        trade.setTradePrice(tradePrice);
//        trade.setTradeItems(list);
//        tradeService.pushSalesOrderkingdee(trade);
//        //支付
////        Buyer buyer01 = new Buyer();
////        buyer01.setCustomerErpId("000");
////        Supplier supplier = new Supplier();
////        supplier.setSupplierId(102L);
////
////        Trade trade01 = new Trade();
////        trade01.setBuyer(buyer01);
////        trade01.setSupplier(supplier);
////        trade01.setPayWay(PayWay.ALIPAY);
////        PayOrder payOrder = new PayOrder();
////        payOrder.setPayOrderPrice(new BigDecimal(20));
////        tradeService.pushPayOrderKingdee(trade01,payOrder);
//    }
//    /**
//     * 向金蝶推送订单数据
//     * @param trade
//     */
//    public void pushSalesOrderkingdee(Trade trade){
//        logger.info("TradeService.pustSalesOrderkingdee req id:{}",trade.getId());
//        KingdeeSalesOrder order = new KingdeeSalesOrder();
//        try {
//            Map FCustId = new HashMap();
//            FCustId.put("FNumber",trade.getBuyer().getCustomerErpId());
//            order.setFCustId(FCustId);
//            order.setFDate(DateUtil.nowDate());
//            Map fSaleOrgId = new HashMap();
//            fSaleOrgId.put("FNumber",trade.getSeller().getAdminId());
//            order.setFSaleOrgId(fSaleOrgId);
//            Map FSaleDeptId = new HashMap();
//            FSaleDeptId.put("FNumber",trade.getSupplier().getErpId());
//            order.setFSaleDeptId(FSaleDeptId);
//            order.setFFreight(trade.getTradePrice().getDeliveryPrice());
//            Map FSalerId = new HashMap();
//            FSalerId.put("FNumber","10000018_GW000033_1");
//            order.setFSalerId(FSalerId);
//            List<FSaleOrderEntry> fSaleOrderEntry = new ArrayList<>();
//            //购买商品
//            if (trade.getTradeItems().size() > 0) {
//                for (TradeItem item : trade.getTradeItems()) {
//                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
//                    Map FMaterialId = new HashMap();
//                    FMaterialId.put("FNumber",item.getSkuId());
//                    orderEntry.setFMaterialId(FMaterialId);
//                    orderEntry.setFQty(item.getNum());
//                    orderEntry.setFPrice(item.getPrice());
//                    orderEntry.setFTaxPrice(item.getPrice());
//                    orderEntry.setFIsFree("1");
//                    orderEntry.setFEntryTaxRate(new BigDecimal(0));//税率
//                    orderEntry.setFAmount(item.getSettlementPrice());//金额
//                    orderEntry.setFAllAmount(item.getSettlementPrice());//价税合计
//                    fSaleOrderEntry.add(orderEntry);
//                }
//            }
//            //赠送商品
//            if (trade.getGifts().size() > 0) {
//                for (TradeItem item : trade.getGifts()) {
//                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
//                    Map FMaterialId = new HashMap();
//                    FMaterialId.put("FNumber",item.getSkuId());
//                    orderEntry.setFMaterialId(FMaterialId);
//                    orderEntry.setFQty(item.getNum());
//                    orderEntry.setFPrice(item.getPrice());
//                    orderEntry.setFTaxPrice(item.getPrice());
//                    orderEntry.setFIsFree("0");
//                    fSaleOrderEntry.add(orderEntry);
//                }
//            }
//            order.setFSaleOrderEntry(fSaleOrderEntry);
//            //登录财务系统
//            Map<String,Object> requestLogMap = new HashMap<>();
//            requestLogMap.put("user",kingdeeUser);
//            requestLogMap.put("pwd",kingdeePwd);
//            HttpCommonResult result = HttpCommonUtil.post(loginUrl,requestLogMap);
//            if (result != null && !result.getResultCode().equals("200")){
//                logger.info("TradeService.pustSalesOrderkingdee login error:{}",result.getResultCode());
//                return;
//            }
//
//            logger.info("TradeService.pustSalesOrderkingdee Log result Code:{}", result.getResultCode());
//            kingdeeRes kingdeeRes = JSONObject.parseObject(result.getResultData(),kingdeeRes.class);
//            //提交财务单
//            Map<String,Object> requestMap = new HashMap<>();
//            requestMap.put("Model",order);
//            HttpCommonResult result1 = HttpCommonUtil.postHeader(orderUrl,requestMap,kingdeeRes.getData());
//            logger.info("TradeService.pustSalesOrderkingdee result1:{}", result1.getResultData());
//        }catch (Exception e){
//            logger.error("TradeService.pustSalesOrderkingdee error:{}",e);
//        }
//    }

    /**
     * 提交积分订单
     *
     * @param trade    积分订单
     * @param operator 操作人
     * @return 订单提交结果
     */
    @Transactional
    @LcnTransaction
    public PointsTradeCommitResult createPointsTrade(PileTrade trade, Operator operator) {
        PointsTradeCommitResult commitResult = null;

        //创建订单
        try {
            PileTrade points = createPoints(trade, operator);
            Trade result = KsBeanUtil.convert(points,Trade.class);
            commitResult = new PointsTradeCommitResult(result.getId(), result.getTradePrice().getPoints());
        } catch (Exception e) {
            log.error("commit points trade error,trade={}", trade, e);
            if (e instanceof SbcRuntimeException) {
                throw e;
            } else {
                throw new SbcRuntimeException("K-020010");
            }
        }

        // 增加客户积分明细 扣除积分
        customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                .customerId(trade.getBuyer().getId())
                .type(OperateType.DEDUCT)
                .serviceType(PointsServiceType.POINTS_EXCHANGE)
                .points(trade.getTradePrice().getPoints())
                .content(JSONObject.toJSONString(Collections.singletonMap("orderNo", trade.getId())))
                .build());

        // 扣除积分商品库存
        pointsGoodsSaveProvider.minusStock(PointsGoodsMinusStockRequest.builder().stock(trade.getTradeItems().get(0)
                .getNum()).pointsGoodsId(trade.getTradeItems().get(0).getPointsGoodsId()).build());

        //ares埋点-订单-用户创建订单(同时发送多笔订单)
        orderAresService.dispatchFunction("addOrderList", trade);
        return commitResult;
    }

    /**
     * 创建订单和订单组
     */
    @Transactional
    @LcnTransaction
    public List<TradeCommitResult> createBatchWithGroup(List<PileTrade> trades, PileTradeGroup tradeGroup, Operator operator) {
        // 1.保存订单及订单组信息
        if (StringUtils.isEmpty(tradeGroup.getId())) {
            tradeGroup.setId(UUIDUtil.getUUID());
        }

        pileTradeGroupService.addTradeGroup(tradeGroup);
        trades.forEach(trade -> trade.setGroupId(tradeGroup.getId()));
        List<TradeCommitResult> resultList = this.createBatch(trades, operator);

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
     * 订单改价
     *
     * @param request  包含修改后的订单总价和运费价格
     * @param tid      订单编号
     * @param operator 操作人信息
     */
    @Transactional
    @LcnTransaction
    public void changePrice(TradePriceChangeRequest request, String tid, Operator operator) {
        //1.获取旧订单信息,并校验当前状态是否可修改
        PileTrade trade = this.detail(tid);
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            throw new SbcRuntimeException("K-050212");
        }
        BaseResponse<PayTradeRecordCountResponse> response = payQueryProvider.getTradeRecordCountByOrderOrParentCode(new
                TradeRecordCountByOrderOrParentCodeRequest(trade.getId(), trade.getParentId()));
        long tradeRecordCount = response.getContext().getCount();
        if (tradeRecordCount > 0) {
            throw new SbcRuntimeException("K-050211");
        }

        //2.校验客户有效性
        verifyService.verifyCustomer(trade.getBuyer().getId());
        //3.改价
        TradePrice tradePrice = trade.getTradePrice();
        BigDecimal oldDeliveryPrice = tradePrice.getDeliveryPrice() == null ? BigDecimal.ZERO : tradePrice
                .getDeliveryPrice();
        //3.1 重置特价和运费
        tradePrice.setDeliveryPrice(request.getFreight());
        tradePrice.setPrivilegePrice(request.getTotalPrice());
        tradePrice.setSpecial(true);
        tradePrice.setEnableDeliveryPrice(request.getFreight() != null);
        BigDecimal freight = request.getFreight() == null ? BigDecimal.ZERO : request.getFreight();
        //3.2 重置原价 原始商品总额+新运费
        tradePrice.setOriginPrice(tradePrice.getOriginPrice().subtract(oldDeliveryPrice).add(freight));
        //3.3 重置均摊价和应付金额
        pileTradeItemService.clacSplitPrice(trade.getTradeItems(), tradePrice.getPrivilegePrice());
        // 3.4. 已计算好均摊价后，如果有分销商品，重新赋值分销商品的实付金额
        if (CollectionUtils.isNotEmpty(trade.getDistributeItems())) {
            // 如果有分销商品
            this.reCalcDistributionItem(trade);
        }
        tradePrice.setTotalPrice(tradePrice.getPrivilegePrice().add(freight));//应付金额 = 特价+运费

        //4.在s2b模式下 如果存在支付单，删除，创建一笔新的支付单
        if (osUtil.isS2b() && trade.getTradeState().getAuditState().equals(AuditState.CHECKED)) {
            if (Objects.nonNull(trade.getPayOrderId())) {
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
                            OrderType.NORMAL_ORDER));

            trade.getTradeState().setPayState(PayState.NOT_PAID);
            optional.ifPresent(payOrder -> trade.setPayOrderId(payOrder.getPayOrderId()));
        }

        //7.状态变更,订单修改入库
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(trade.getId())
                .operator(operator)
                .data(trade)
                .event(TradeEvent.REMEDY)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);
    }

    /**
     * 订单改价后，重新计算分销商品的佣金
     *
     * @param trade
     */
    public void reCalcDistributionItem(PileTrade trade) {
        // 如果有分销商品
        IteratorUtils.zip(trade.getDistributeItems(), trade.getTradeItems(),
                (collect1, levels1) -> collect1.getGoodsInfoId().equals(levels1.getSkuId()),
                (collect2, levels2) -> {
                    collect2.setActualPaidPrice(levels2.getSplitPrice());
                    collect2.setCommission(levels2.getSplitPrice().multiply(levels2.getCommissionRate()));
                    levels2.setDistributionCommission(collect2.getCommission());
                }
        );
        // 重新计算订单总佣金
        BigDecimal totalCommission = trade.getDistributeItems().stream().map(item -> item.getCommission())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        trade.setCommission(totalCommission);
    }

    /**
     * 修改订单
     *
     * @param request
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void remedy(TradeRemedyRequest request, Operator operator, StoreInfoResponse storeInfoResponse) {
        //1.获取旧订单信息,并校验当前状态是否可修改
        PileTrade trade = this.detail(request.getTradeId());
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            throw new SbcRuntimeException("K-050212");
        }

        BaseResponse<PayTradeRecordCountResponse> response = payQueryProvider.getTradeRecordCountByOrderOrParentCode(new
                TradeRecordCountByOrderOrParentCodeRequest(trade.getId(), trade.getParentId()));
        long tradeRecordCount = response.getContext().getCount();
        if (tradeRecordCount > 0) {
            throw new SbcRuntimeException("K-050211");
        }

        //2.校验客户有效性
        verifyService.verifyCustomer(trade.getBuyer().getId());

        //批量新增旧订单商品，赠品库存
        List<GoodsInfoPlusStockDTO> plusStockList = trade.getTradeItems().stream().map(i -> {
            GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
            dto.setStock(BigDecimal.valueOf(i.getNum()));
            dto.setGoodsInfoId(i.getSkuId());
            return dto;
        }).collect(Collectors.toList());

        trade.getGifts().forEach(i -> {
            GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
            dto.setStock(BigDecimal.valueOf(i.getNum()));
            dto.setGoodsInfoId(i.getSkuId());
            plusStockList.add(dto);
        });

        if (CollectionUtils.isNotEmpty(plusStockList)) {
            GoodsInfoBatchPlusStockRequest plusStockRequest = GoodsInfoBatchPlusStockRequest.builder()
                    .stockList(plusStockList).build();
            goodsInfoProvider.batchPlusStock(plusStockRequest);
        }


        //4.校验与包装待修改的订单信息
        trade.getSupplier().setFreightTemplateType(storeInfoResponse.getFreightTemplateType());
        trade = this.wrapperBackendRemedyTrade(trade, operator, request);

        //5.减商品,赠品库存
        verifyService.subSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),null);
        verifyService.subSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),null);

        //6.在s2b模式下 如果存在支付单，删除，创建一笔新的支付单
        if (osUtil.isS2b() && trade.getTradeState().getAuditState().equals(AuditState.CHECKED)) {
            if (Objects.nonNull(trade.getPayOrderId())) {
                createPayOrder(trade);
            }
        }

        //7.状态变更,订单修改入库
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(trade.getId())
                .operator(operator)
                .data(trade)
                .event(TradeEvent.REMEDY)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);
    }


    /**
     * 修改订单-部分修改
     *
     * @param request           修改订单请求对象
     * @param operator          操作人
     * @param storeInfoResponse 店铺信息
     */
    @Transactional
    @LcnTransaction
    public void remedyPart(TradeRemedyRequest request, Operator operator, StoreInfoResponse storeInfoResponse) {
        //1.获取旧订单信息,并校验当前状态是否可修改
        PileTrade trade = this.detail(request.getTradeId());
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            throw new SbcRuntimeException("K-050212");
        }

        BaseResponse<PayTradeRecordCountResponse> response = payQueryProvider.getTradeRecordCountByOrderOrParentCode(new
                TradeRecordCountByOrderOrParentCodeRequest(trade.getId(), trade.getParentId()));
        long tradeRecordCount = response.getContext().getCount();
        if (tradeRecordCount > 0) {
            throw new SbcRuntimeException("K-050211");
        }

        // 2.校验客户有效性
//        verifyService.verifyCustomer(trade.getBuyer().getId());

        // 3.将新数据设置到旧订单trade对象中（包括收货信息、发票信息、特价、运费信息）
        request.getInvoice().setOrderInvoiceId(
                Objects.nonNull(trade.getInvoice()) ?
                        trade.getInvoice().getOrderInvoiceId() : null);
        trade.setConsignee(wrapperConsignee(request.getConsigneeId(), request.getConsigneeAddress(),
                request.getConsigneeUpdateTime(), request.getConsignee()));
        //发票信息(必须在收货地址下面-因为使用临时发票收货地,却未填写的时候,将使用订单商品收货地址作为发票收货地)
        trade.setInvoice(wrapperTradeInvoice(request.getInvoice(), request.getInvoiceConsignee(),
                trade.getConsignee()));
        trade.setDeliverWay(request.getDeliverWay());
        if (request.getPayType() != null) {
            trade.setPayInfo(PayInfo.builder()
                    .payTypeId(String.format("%d", request.getPayType().toValue()))
                    .payTypeName(request.getPayType().name())
                    .desc(request.getPayType().getDesc())
                    .build());
        }
        trade.setBuyerRemark(request.getBuyerRemark());
        trade.setSellerRemark(request.getSellerRemark());
        trade.setEncloses(request.getEncloses());
        trade.setRequestIp(operator.getIp());
        TradePrice tradePrice = trade.getTradePrice();
        TradePrice newTradePrice = request.getTradePrice();
        tradePrice.setPrivilegePrice(newTradePrice.getPrivilegePrice());
        tradePrice.setEnableDeliveryPrice(newTradePrice.isEnableDeliveryPrice());
        tradePrice.setTotalPrice(tradePrice.getTotalPrice().subtract(tradePrice.getDeliveryPrice()));
        tradePrice.setDeliveryPrice(newTradePrice.getDeliveryPrice());

        // 4.如果取消特价的情况，则要重新计算totalPrice和tradeItem的splitPrice
        if (newTradePrice.isSpecial() == false && tradePrice.isSpecial() == true) {
            trade.getTradeItems().forEach(tradeItem -> {
                BigDecimal splitPrice = tradeItem.getLevelPrice().multiply(
                        new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP);
                List<TradeItem.MarketingSettlement> marketings = tradeItem.getMarketingSettlements();
                List<TradeItem.CouponSettlement> coupons = tradeItem.getCouponSettlements();
                if (!CollectionUtils.isEmpty(coupons)) {
                    splitPrice = coupons.get(coupons.size() - 1).getSplitPrice();
                } else if (!CollectionUtils.isEmpty(marketings)) {
                    splitPrice = marketings.get(marketings.size() - 1).getSplitPrice();
                }
                tradeItem.setSplitPrice(splitPrice);
            });
            BigDecimal totalPrice = trade.getTradeItems().stream()
                    .map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tradePrice.setTotalPrice(totalPrice);
        }
        tradePrice.setSpecial(newTradePrice.isSpecial());

        // 5.计算运费
        trade.getSupplier().setFreightTemplateType(storeInfoResponse.getFreightTemplateType());

        BigDecimal deliveryPrice = tradePrice.getDeliveryPrice();
        if (tradePrice.getDeliveryPrice() == null) {
            deliveryPrice = this.calcTradeFreight(trade.getConsignee(), trade.getSupplier(), trade.getDeliverWay(),
                    tradePrice.getTotalPrice(), trade.getTradeItems(), trade.getGifts());
            tradePrice.setDeliveryPrice(deliveryPrice);
        }

        // 6.计算订单总价
        tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
        if (tradePrice.isSpecial()) {
            trade.getTradeItems().forEach(tradeItem ->
                    tradeItem.setSplitPrice(tradeItem.getLevelPrice().multiply(
                            new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP))
            );
            // 6.1 计算特价均摊价
            pileTradeItemService.clacSplitPrice(trade.getTradeItems(), tradePrice.getPrivilegePrice());

            // 6.2 已计算好均摊价后，如果有分销商品，重新赋值分销商品的实付金额
            if (CollectionUtils.isNotEmpty(trade.getDistributeItems())) {
                // 如果有分销商品
                this.reCalcDistributionItem(trade);
            }
            tradePrice.setTotalPrice(tradePrice.getPrivilegePrice().add(deliveryPrice));//应付金额 = 特价+运费
        } else {
            tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
        }

        // 7.在s2b模式下 如果存在支付单，删除，创建一笔新的支付单
        if (osUtil.isS2b() && trade.getTradeState().getAuditState().equals(AuditState.CHECKED)) {
            if (Objects.nonNull(trade.getPayOrderId())) {
                createPayOrder(trade);
            }
        }

        //8.状态变更,订单修改入库
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(trade.getId())
                .operator(operator)
                .data(trade)
                .event(TradeEvent.REMEDY)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);
    }


    /**
     * 取消订单
     *
     * @param tid
     * @param operator
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void cancel(String tid, Operator operator) {
        PileTrade trade = detail(tid);
        Platform platform = operator.getPlatform();
        if (!Platform.SUPPLIER.equals(platform) && !trade.getBuyer().getId().equals(operator.getUserId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{tid});
        }
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            throw new SbcRuntimeException("K-050202");
        }
        if (trade.getTradeState().getDeliverStatus() != DeliverStatus.NOT_YET_SHIPPED) {
            throw new SbcRuntimeException("K-050203");
        }
        if (trade.getTradeState().getAuditState() == AuditState.CHECKED) {
            //删除支付单
            payOrderService.deleteByPayOrderId(trade.getPayOrderId());
        }
        //是否是秒杀抢购商品订单
//        if (Objects.nonNull(trade.getIsFlashSaleGoods()) && trade.getIsFlashSaleGoods()) {
//            flashSaleGoodsOrderAddStock(trade);
//        } else {
//            //释放库存
//            verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
//            verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
//        }
        //状态变更
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(trade.getId())
                .operator(operator)
                .event(TradeEvent.VOID)
                .data("用户取消订单")
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);
        // 退优惠券
        returnCoupon(tid);
        // 取消供应商订单
        providerTradeService.providerCancel(tid, operator, false);

        //减去营销商品限购数量
        calMarketGoodsNum(trade.getTradeItems(),true);
    }

    /**
     * 订单审核
     *
     * @param tid
     * @param auditState 审核 | 驳回
     * @param reason     驳回原因，用于审核驳回
     * @param operator   操作人
     */
    @Transactional
    @LcnTransaction
    public void audit(String tid, AuditState auditState, String reason, Operator operator,Boolean financialFlag) {
        if (operator.getPlatform() != Platform.BOSS && operator.getPlatform() != Platform.SUPPLIER &&
                operator.getPlatform() != Platform.PLATFORM) {
            throw new SbcRuntimeException("K-000014");
        }
        //订单驳回释放库存
        PileTrade trade = detail(tid);
        if(!financialFlag){
            if (trade.getTradeState().getAuditState() != AuditState.NON_CHECKED) {
                throw new SbcRuntimeException("K-050316");
            }
        }

        if (AuditState.CHECKED.equals(auditState)
                && DeliverWay.LOGISTICS.equals(trade.getDeliverWay())
                && (Objects.isNull(trade.getLogisticsCompanyInfo())
                || StringUtils.isBlank(trade.getLogisticsCompanyInfo().getId()))) {
            throw new SbcRuntimeException("K-180002");
        }
        if (auditState == AuditState.REJECTED) {
            trade.getTradeState().setObsoleteReason(reason);
            verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),null);
            verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),null);

            //删除支付单
            if (financialFlag){
                payOrderService.deleteByPayOrderId(trade.getPayOrderId());
            }

        } else {
            createPayOrder(trade);
        }
        //如果当前为自提订单且审核通过需要扭转订单状态
        boolean pickUpAndChecked = auditState.equals(AuditState.CHECKED) && trade.getDeliverWay().equals(DeliverWay.PICK_SELF);
        //是否是先货后款
        boolean goodsFirst = Objects.equals(auditState, AuditState.CHECKED)
                && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        PickUpRecord pickUpRecord = new PickUpRecord();
        //满足两个条件生成自提码存入数据库
        if (pickUpAndChecked) {
            if (goodsFirst) {
                pickUpRecord = sendPickUpCode(trade);
                trade.getTradeWareHouse().setPickUpCode(pickUpRecord.getPickUpCode());
                pickUpRecordService.add(pickUpRecord);
            }
        }
        //物流状态新增最后付款时间
        if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay())) {
            Boolean needTimeOut = Objects.equals(auditState, AuditState.CHECKED) &&
                    trade.getPaymentOrder() == PaymentOrder.PAY_FIRST;

            if (needTimeOut) {
                // 先货后款情况下，查询订单是否开启订单失效时间设置
                ConfigVO timeoutCancelConfig = tradeCacheService.getTradeConfigByType(ConfigType.ORDER_SETTING_TIMEOUT_CANCEL);
                Integer timeoutSwitch = timeoutCancelConfig.getStatus();
                if (timeoutSwitch == 1) {
                    // 查询设置中订单超时时间
                    Integer min =
                            Integer.valueOf(JSON.parseObject(timeoutCancelConfig.getContext()).get("hour").toString());
                    // 发送非拼团单取消订单延迟队列;
                    if (Objects.nonNull(trade.getGrouponFlag()) && !trade.getGrouponFlag()) {
                        // 线下支付默认一个小时后取消
                        if (PayType.OFFLINE.name().equals(trade.getPayInfo().getPayTypeName())) {
                            min = 60;
                        }
                        trade.setOrderTimeOut(LocalDateTime.now().plusMinutes(min));
                        orderProducerService.cancelOrder(trade.getId(), min * 60 * 1000L);
                    }
                }
            }
        }
        updateTrade(trade);

        if (pickUpAndChecked) {
            //订单状态扭转审核=》待自提
            StateRequest stateRequest = StateRequest
                    .builder()
                    .tid(tid)
                    .operator(operator)
                    .event(TradeEvent.PICK_UP)
                    .data(auditState)
                    .build();
            pileTradeFSMService.changePileTradeState(stateRequest);
        } else {
            //订单状态扭转
            StateRequest stateRequest = StateRequest
                    .builder()
                    .tid(tid)
                    .operator(operator)
                    .event(TradeEvent.AUDIT)
                    .data(auditState)
                    .build();
            pileTradeFSMService.changePileTradeState(stateRequest);
        }

        if (auditState == AuditState.REJECTED) {
            // 退优惠券
            returnCoupon(tid);
        }
        //订单审核通过/未通过发送通知消息
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        List<String> params = new ArrayList<>();
        params.add(trade.getTradeItems().get(0).getSkuName());
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        if (AuditState.CHECKED.equals(auditState)) {
            messageMQRequest.setNodeCode(OrderProcessType.ORDER_CHECK_PASS.getType());
            map.put("node", OrderProcessType.ORDER_CHECK_PASS.toValue());
        } else {
            messageMQRequest.setNodeCode(OrderProcessType.ORDER_CHECK_NOT_PASS.getType());
            map.put("node", OrderProcessType.ORDER_CHECK_NOT_PASS.toValue());
            params.add(reason);
        }
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setParams(params);
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setMobile(trade.getBuyer().getAccount());
        orderProducerService.sendMessage(messageMQRequest);


        //自提码推送
        if (goodsFirst && pickUpAndChecked) {
           /* smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{trade.getConsignee().getPhone()},
                    trade.getId(), pickUpRecord.getPickUpCode());*/
            sendPickUpMessage(trade);
        }
        // 6.推送订单
        if (wmsAPIFlag && AuditState.CHECKED.equals(auditState)) {
            //拼团在订单那提交的时候不推送，在成团时推送
            if (Objects.isNull(trade.getGrouponFlag()) || !trade.getGrouponFlag()) {
                pushWMSOrder(trade, false);
            }
        }

        // 同步审核供应商订单
        // providerTradeService.providerAudit(tid, reason, auditState);

    }

    /**
     * 组装自提码参数
     */
    private PickUpRecord sendPickUpCode(PileTrade trade) {
        String verifyCode = RandomStringUtils.randomNumeric(6);
        return PickUpRecord.builder().storeId(trade.getSupplier().getStoreId())
                .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build();
    }

    /**
     * auditAction 创建支付单
     *
     * @param trade trade
     */
    private void createPayOrder(PileTrade trade) {
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
     * 订单回审
     *
     * @param tid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void retrial(String tid, Operator operator) {
        Boolean orderAuditSwitch = auditQueryProvider.isSupplierOrderAudit().getContext().isAudit();

        if (!orderAuditSwitch) {
            throw new SbcRuntimeException("K-050133");
        }
        //作废支付单
        PileTrade trade = detail(tid);
        if (trade.getTradeState().getPayState() != PayState.NOT_PAID) {
            throw new SbcRuntimeException("K-050127");
        }
        BaseResponse<PayTradeRecordCountResponse> response = payQueryProvider.getTradeRecordCountByOrderOrParentCode(new
                TradeRecordCountByOrderOrParentCodeRequest(trade.getId(), trade.getParentId()));
        long tradeRecordCount = response.getContext().getCount();
        if (tradeRecordCount > 0) {
            throw new SbcRuntimeException("K-050128");
        }
        payOrderService.deleteByPayOrderId(trade.getPayOrderId());
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(tid)
                .operator(operator)
                .event(TradeEvent.RE_AUDIT)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);
    }

    /**
     * 批量审核
     *
     * @param ids      ids
     * @param audit    审核状态
     * @param reason   驳回原因，用于审核驳回
     * @param operator 审核人信息
     */
    @Transactional
    @LcnTransaction
    public void batchAudit(String[] ids, AuditState audit, String reason, Operator operator) {
        if (ArrayUtils.isNotEmpty(ids)) {
            Stream.of(ids).forEach(id -> audit(id, audit, reason, operator,false));
        }
    }

    @SuppressWarnings("unused")
    public void confirmRemedySellerRemark(String tid, String sellerRemark, Operator operator) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelRemedySellerRemark(String tid, String sellerRemark, Operator operator) {
        mongoTccHelper.cancel();
    }

    /**
     * 修改卖家备注
     *
     * @param tid
     * @param sellerRemark
     */
    @TccTransaction
    public void remedySellerRemark(String tid, String sellerRemark, Operator operator) {
        //1、查找订单信息
        PileTrade trade = detail(tid);
        trade.setSellerRemark(sellerRemark);
        trade.appendTradeEventLog(new TradeEventLog(operator, "修改备注", "修改卖家备注", LocalDateTime.now()));
        //保存
        updateTrade(trade);
        this.operationLogMq.convertAndSend(operator, "修改备注", "修改卖家备注");
    }


    /**
     * 查询订单
     *
     * @param tid
     */
    public PileTrade detail(String tid) {
        return pileTradeRepository.findById(tid).orElse(null);
    }

    /**
     * 查 parentId 对于订单集合
     *
     * @param parentId
     */
    public PileTrade detailByParentId(String parentId) {
        List<PileTrade> tradeList = pileTradeRepository.findListByParentId(parentId);
        PileTrade trade = tradeList.stream().findFirst().get();
        tradeList.stream().filter(t -> !t.getId().equals(trade.getId())).forEach(
                d -> {
                    trade.getTradeItems().addAll(d.getTradeItems());
                    trade.getTradePrice().setTotalPrice(trade.getTradePrice().getTotalPrice().add(d.getTradePrice().
                            getTotalPrice()));
                }
        );
        return trade;
    }

    /**
     * 查询订单集合
     *
     * @param tids
     */
    public List<PileTrade> details(List<String> tids) {
        return org.apache.commons.collections4.IteratorUtils.toList(pileTradeRepository.findAllById(tids).iterator());
    }

    /**
     * 根据父订单号查询订单
     *
     * @param parentTid
     */
    public List<PileTrade> detailsByParentId(String parentTid) {
        return pileTradeRepository.findListByParentId(parentTid);
    }

    /**
     * 发货
     *
     * @param tid
     * @param tradeDeliver
     * @param operator
     */
    public String deliver(String tid, TradeDeliver tradeDeliver, Operator operator) {
        PileTrade trade = detail(tid);
        //是否开启订单审核
        if (auditQueryProvider.isSupplierOrderAudit().getContext().isAudit() && trade.getTradeState().getAuditState()
                != AuditState.CHECKED) {
            //只有已审核订单才能发货
            throw new SbcRuntimeException("K-050317");
        }
        // 先款后货并且未支付的情况下禁止发货
        if (trade.getPaymentOrder() == PaymentOrder.PAY_FIRST && trade.getTradeState().getPayState() == PayState.NOT_PAID && trade.getPayInfo().getPayTypeId().equals(0)) {
            throw new SbcRuntimeException("K-050318");
        }
        if (verifyAfterProcessing(tid)) {
            throw new SbcRuntimeException("K-050114", new Object[]{tid});
        }
        checkLogisticsNo(tradeDeliver.getLogistics().getLogisticNo(), tradeDeliver.getLogistics()
                .getLogisticStandardCode());
        // 生成ID
        tradeDeliver.setDeliverId(generatorService.generate("TD"));
        tradeDeliver.setStatus(DeliverStatus.NOT_YET_SHIPPED);
        tradeDeliver.setTradeId(tid);
        tradeDeliver.setProviderName(trade.getSupplier().getSupplierName());
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(tid)
                .operator(operator)
                .data(tradeDeliver)
                .event(TradeEvent.DELIVER)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);

        //发货完成发送通知消息
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", tid);
        map.put("node", OrderProcessType.ORDER_SEND_GOODS.toValue());
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setNodeCode(OrderProcessType.ORDER_SEND_GOODS.getType());
        String skuName = StringUtils.EMPTY;
        String pic = StringUtils.EMPTY;
        if (CollectionUtils.isNotEmpty(tradeDeliver.getShippingItems())) {
            skuName = tradeDeliver.getShippingItems().get(0).getItemName();
            pic = tradeDeliver.getShippingItems().get(0).getPic();
        }
        messageMQRequest.setParams(Lists.newArrayList(skuName));
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setPic(pic);
        messageMQRequest.setMobile(trade.getBuyer().getAccount());
        orderProducerService.sendMessage(messageMQRequest);

        return tradeDeliver.getDeliverId();
    }


    /**
     * 验证订单是否存在售后申请
     *
     * @param tid
     * @return true|false:存在售后，阻塞订单进程|不存在售后，订单进程正常
     */
    public boolean verifyAfterProcessing(String tid) {
        List<ReturnPileOrder> returnOrders = returnPileOrderRepository.findByTid(tid);
        if (!CollectionUtils.isEmpty(returnOrders)) {
            // 查询是否存在正在进行中的退单(不是作废,不是拒绝退款,不是已结束)
            Optional<ReturnPileOrder> optional = returnOrders.stream().filter(item -> item.getReturnFlowState() !=
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
     * 确认收货
     *
     * @param tid
     * @param operator
     */
    @LcnTransaction
    public void confirmReceive(String tid, Operator operator) {
        PileTrade trade = detail(tid);
        TradeEvent event;
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            event = TradeEvent.COMPLETE;
        } else {
            event = TradeEvent.CONFIRM;
        }
        StateRequest stateRequest = StateRequest
                .builder()
                .data(trade)
                .tid(tid)
                .operator(operator)
                .event(event)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);

        // 发送订单完成MQ消息
        if (trade.getTradeState().getPayState() == PayState.PAID) {
            orderProducerService.sendMQForOrderComplete(tid);

            Map<String, Object> map = new HashMap<>();
            map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
            map.put("node", OrderProcessType.ORDER_COMPILE.toValue());
            MessageMQRequest messageMQRequest = new MessageMQRequest();
            messageMQRequest.setNodeCode(OrderProcessType.ORDER_COMPILE.getType());
            messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
            messageMQRequest.setParams(Lists.newArrayList(trade.getTradeItems().get(0).getSkuName()));
            messageMQRequest.setRouteParam(map);
            messageMQRequest.setCustomerId(trade.getBuyer().getId());
            messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
            messageMQRequest.setMobile(trade.getBuyer().getAccount());
            orderProducerService.sendMessage(messageMQRequest);
        }
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
        pileTradeFSMService.changePileTradeState(stateRequest);
    }

    /**
     * 作废订单
     *
     * @param tid
     * @param operator
     */
    public void voidTrade(String tid, Operator operator) {
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(tid)
                .operator(operator)
                .event(TradeEvent.VOID)
                .data("已全部退货或退款")
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);

        PileTrade trade = detail(tid);
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
     * 退单作废后的订单状态扭转
     *
     * @param tid
     * @param operator
     */
    public void reverse(String tid, Operator operator, ReturnType returnType) {
        PileTrade trade = detail(tid);
        if (trade.getTradeState().getFlowState() != FlowState.VOID) {
            return;
        }
        TradeEvent event;
        Object data;
        if (returnType == ReturnType.RETURN) {
            event = TradeEvent.REVERSE_RETURN;
            data = trade;
        } else {
            event = TradeEvent.REVERSE_REFUND;
            data = AuditState.CHECKED;
        }
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(tid)
                .operator(operator)
                .event(event)
                .data(data)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);
    }


    /**
     * 查询全部订单
     *
     * @param request
     * @return
     */
    public List<PileTrade> queryAll(TradeQueryRequest request) {
        return mongoTemplate.find(new Query(request.getWhereCriteria()), PileTrade.class);
    }


    /**
     * 发货记录作废
     *
     * @param tid
     * @param deliverId
     * @param operator
     */
    public void deliverRecordObsolete(String tid, String deliverId, Operator operator) {
        StateRequest stateRequest = StateRequest.builder()
                .tid(tid)
                .operator(operator)
                .data(deliverId)
                .event(TradeEvent.OBSOLETE_DELIVER)
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);

    }

    @SuppressWarnings("unused")
    public void confirmSaveInvoice(String tid, Invoice invoice) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelSaveInvoice(String tid, Invoice invoice) {
        mongoTccHelper.cancel();
    }

    /**
     * 保存发票信息
     *
     * @param tid
     * @param invoice
     */
    @LcnTransaction
    public void saveInvoice(String tid, Invoice invoice) {
        PileTrade trade = detail(tid);
        trade.setInvoice(invoice);
        updateTrade(trade);
    }


    /**
     * 支付作废
     *
     * @param tid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void payRecordObsolete(String tid, Operator operator) {
        PileTrade trade = detail(tid);
        //删除对账记录
        accountRecordProvider.deleteByOrderCodeAndType(
                AccountRecordDeleteByOrderCodeAndTypeRequest.builder().orderCode(trade.getId())
                        .accountRecordType(AccountRecordType.INCOME).build()
        );
        if (trade.getTradeState().getPayState() == PayState.NOT_PAID) {
            throw new SbcRuntimeException("K-050125", new Object[]{"作废支付"});
        }
        trade.getTradePrice().setTotalPayCash(null);
        if (trade.getTradeState().getFlowState() == FlowState.COMPLETED) {
            //已完成订单，扭转流程状态与支付状态
            StateRequest stateRequest = StateRequest.builder()
                    .tid(tid)
                    .operator(operator)
                    .event(TradeEvent.OBSOLETE_PAY)
                    .build();
            pileTradeFSMService.changePileTradeState(stateRequest);
        } else {
            //进行中订单，只扭转付款状态
            trade.getTradeState().setPayState(PayState.NOT_PAID);
            trade.getTradeState().setPayTime(null);
            //添加操作日志
            String detail = String.format("订单[%s]支付记录已作废，当前支付状态[%s],操作人：%s", trade.getId(),
                    trade.getTradeState().getPayState().getDescription(), operator.getName());
            trade.appendTradeEventLog(TradeEventLog
                    .builder()
                    .operator(operator)
                    .eventType(TradeEvent.OBSOLETE_PAY.getDescription())
                    .eventDetail(detail)
                    .eventTime(LocalDateTime.now())
                    .build());
            updateTrade(trade);
            this.operationLogMq.convertAndSend(operator, TradeEvent.OBSOLETE_PAY.getDescription(), detail);
        }
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
    public void payCallBackOnline(PileTrade trade, PayOrder payOrderOld, Operator operator) {
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
                ReceivableAddRequest param = new ReceivableAddRequest(trade.getPayOrderId(), Arrays.asList(trade.getPayOrderId()),LocalDateTime.now()
                        .toString()
                        , trade.getSellerRemark(), 0L, chanelItemResponse.getName(), chanelItemResponse.getId(), null);

                Optional<PayOrder> payOrder = addReceivable(param, operator.getPlatform());

                log.info("=========payOrder.isPresent==========: {}",payOrder.isPresent());

                log.info("=========chanelItemResponse==========: {}",JSONObject.toJSONString(chanelItemResponse));

                if(payOrder.isPresent()){
                    //订单状态变更
                    payCallBack(trade.getId(), payOrder.get().getPayOrderPrice(), operator, PayWay.valueOf
                            (chanelItemResponse
                                    .getChannel().toUpperCase()));

                    //线上回调成功新增囤货信息
                    List<GoodsInfoDTO> goodsInfoDTOS = Lists.newArrayList();
                    trade.getTradeItems().forEach(ti->{
                        GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
                        goodsInfoDTO.setGoodsInfoId(ti.getSkuId());
                        goodsInfoDTO.setBuyCount(ti.getNum());
                        goodsInfoDTO.setGoodsSplitPrice(ti.getSplitPrice().divide(new BigDecimal(ti.getNum()),2,BigDecimal.ROUND_HALF_UP));
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
                    pilePurchaseService.batchSave(KsBeanUtil.convert(build,PilePurchaseRequest.class));
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


//    /**
//     * 向金蝶push支付单
//     * @param trade
//     * @param payOrderOld
//     */
//    private void pushPayOrderKingdee(Trade trade, PayOrder payOrderOld){
//        logger.info("TradeService.pushPayOrderKingdee req PayOrderId:{}",trade.getId());
//        try{
//            KingdeePayOrder payOrder = new KingdeePayOrder();
//            payOrder.setFDate(DateUtil.nowDate());
//            if (StringUtils.isEmpty(trade.getBuyer().getCustomerErpId())){
//                logger.info("TradeService.pushPayOrderKingdee Lack FCustId");
//                return;
//            }
//            if (trade.getSupplier().getSupplierId() == null){
//                logger.info("TradeService.pushPayOrderKingdee Lack FPAYORGID");
//                return;
//            }
//            Map FCustId = new HashMap();
//            FCustId.put("FNumber",trade.getBuyer().getCustomerErpId());
//            payOrder.setFCustId(FCustId);
//            Map FPAYORGID = new HashMap();
//            FPAYORGID.put("FNumber",trade.getSupplier().getSupplierId());
//            payOrder.setFPAYORGID(FPAYORGID);
//            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
//            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
//            if (trade.getPayWay() == null){
//                logger.info("TradeService.pushPayOrderKingdee Lack FSETTLETYPEID");
//                return;
//            }
//            if (payOrderOld.getPayOrderPrice() == null){
//                logger.info("TradeService.pushPayOrderKingdee Lack FRECTOTALAMOUNTFOR");
//                return;
//            }
//            Map FSETTLETYPEID = new HashMap();
//            FSETTLETYPEID.put("FNumber",trade.getPayWay().toValue());
//            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);
//            payOrderSettlement.setFRECTOTALAMOUNTFOR(payOrderOld.getPayOrderPrice().toString());
//            if (StringUtils.isNotEmpty(trade.getBuyer().getAccount())){
//                Map FACCOUNTID = new HashMap();
//                FACCOUNTID.put("FNumber",trade.getBuyer().getAccount());
//                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
//            }
//            freceivebillentry.add(payOrderSettlement);
//            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
//
//            //登录财务系统
//            Map<String,Object> requestLogMap = new HashMap<>();
//            requestLogMap.put("user",kingdeeUser);
//            requestLogMap.put("pwd",kingdeePwd);
//            HttpCommonResult result = HttpCommonUtil.post(loginUrl, requestLogMap);
//            if (result != null && !result.getResultCode().equals("200")){
//                logger.info("TradeService.pushPayOrderKingdee login error:{}",result.getResultCode());
//                return;
//            }
//
//            logger.info("TradeService.pushPayOrderKingdee Log result Code:{}", result.getResultCode());
//            kingdeeRes kingdeeRes = JSONObject.parseObject(result.getResultData(), kingdeeRes.class);
//            //提交支付财务单
//            Map<String,Object> requestMap = new HashMap<>();
//            requestMap.put("Model",payOrder);
//            HttpCommonResult result1 = HttpCommonUtil.postHeader(payUrl,requestMap,kingdeeRes.getData());
//            logger.info("TradeService.pushPayOrderKingdee result1:{}", result1.getResultData());
//        }catch (Exception e){
//            logger.error("TradeService.pushPayOrderKingdee error:{}",e);
//        }
//    }

    @LcnTransaction
    @Transactional
    public void payCallBackOnlineBatch(List<PayCallBackOnlineBatchPile> request, Operator operator) {
        request.forEach(i -> payCallBackOnline(i.getTrade(), i.getPayOrderOld(), operator));
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


    /**
     * 功能描述: <br>  手动推送WMS第三方接口
     * 〈〉
     *
     * @Param: []
     * @Return: void
     * @Author:
     * @Date: 2020/5/18 15:58
     */
    @LcnTransaction
    public void pushWMSOrder(String tid) {
        PileTrade trade = detail(tid);
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException();
        }

        if (wmsAPIFlag && AuditState.CHECKED.equals(trade.getTradeState().getAuditState())) {
            //拼团在订单那提交的时候不推送，在成团时推送
            if (Objects.isNull(trade.getGrouponFlag()) || !trade.getGrouponFlag()) {
                try {
                    pushWMSOrder(trade, false);
                } catch (Exception e) {
                    log.info("=====订单推送报错日志：" + e + ";  订单编号:" + trade.getId());
                }
            }
        }
        updateTrade(trade);
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
            PileTrade trade = detail(tid);
            TradePrice tradePrice = trade.getTradePrice();
            if (payOrderPrice.compareTo(tradePrice.getTotalPrice()) != 0) {
                throw new SbcRuntimeException("K-050101", new Object[]{tid, tradePrice.getTotalPrice(), payOrderPrice});
            }

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
                if (Objects.nonNull(trade.getGrouponFlag()) && trade.getGrouponFlag()) {
//                trade = grouponOrderService.handleGrouponOrderPaySuccess(trade);

                }
                trade.getTradeState().setPayState(PayState.PAID);
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
                if (StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId())) {
                    BigDecimal totalPrice = trade.getTradePrice().getTotalPrice();
                    Long goodsTotalNum = trade.getGoodsTotalNum();
                    invitationStatisticsProvider.tradeStatistics(InvitationTradeStatisticsRequest.builder().employeeId(employeeId).orderId(tid).goodsCount(goodsTotalNum).tradePrice(totalPrice).build());
                }
                PayOrderResponse payOrder = payOrderService.findPilePayOrderById(trade.getPayOrderId());
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
                log.info("=====================交易流水信息{}",payTradeRecord);
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
                Optional<OrderInvoice> invoiceByOrderNo = orderInvoiceService.findByOrderNo(trade.getId());
                if(invoiceByOrderNo.isPresent()){
                    orderInvoiceService.deleteOrderInvoiceByOrderNo(trade.getId());
                }
                //订单开票
                createOrderInvoice(trade, operator);

                boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED) && PaymentOrder.NO_LIMIT.equals(trade.getPaymentOrder());
                //是否是自提订单如果不是发送自提码
                if (!goodsFirst) {
                    if (null != trade.getDeliverWay() && (DeliverWay.PICK_SELF).equals(trade.getDeliverWay())) {
                        PickUpRecord pickUpRecord = sendPickUpCode(trade);
                        trade.getTradeWareHouse().setPickUpCode(pickUpRecord.getPickUpCode());
                        pickUpRecordService.add(pickUpRecord);
                    }
                }
                //加入囤货流水
                List<PileStockRecord> pileStockRecordList = new ArrayList<>();
                trade.getTradeItems().stream().forEach(tradeItem -> {
                    BigDecimal realPay = tradeItem.getSplitPrice().divide(new BigDecimal(tradeItem.getNum()),2,BigDecimal.ROUND_HALF_UP);
                    PileStockRecord pileStockRecord = PileStockRecord.builder()
                                                                     .goodsId(tradeItem.getSpuId())
                                                                     .goodsInfoId(tradeItem.getSkuId())
                                                                     .customerId(trade.getBuyer().getId())
                                                                     .stockRecordNum(tradeItem.getNum())
                                                                     .stockRecordRemainingNum(0L)
                                                                     .stockRecordPrice(realPay)
                                                                     .orderCode(trade.getId())
                                                                     .isUse(0L)
                                                                     .createTime(LocalDateTime.now())
                                                                     .updateTime(LocalDateTime.now())
                                                                     .build();
                    pileStockRecordList.add(pileStockRecord);
                });
                pileStockRecordRepository.saveAll(pileStockRecordList);

                //囤货推erp
                orderProducerService.pushStockkingdee(trade.getId(),6 * 10L);
            }

            log.info("trade================== {}", trade);
            if (FlowState.CONFIRMED .equals( trade.getTradeState().getFlowState()) && PayState.PAID .equals(trade.getTradeState().getPayState())) {
                // 订单支付后，发送MQ消息
                //this.sendMQForOrderPayed(trade);
                //已支付并已收货，结束订单流程
                StateRequest stateRequest = StateRequest.builder()
                        .tid(tid)
                        .operator(operator)
                        .event(TradeEvent.COMPLETE)
                        .data(trade)
                        .build();
                pileTradeFSMService.changePileTradeState(stateRequest);
                // 订单完成后，发送MQ消息
                this.sendMQForOrderPayedAndComplete(trade);
                //如果是自订单需要扭转为待自提
            } else if ( FlowState.AUDIT.equals(trade.getTradeState().getFlowState())
                    &&  PayState.PAID.equals(trade.getTradeState().getPayState())
                    && DeliverWay.PICK_SELF.equals(trade.getDeliverWay())) {
                // 订单支付后，发送MQ消息
                this.sendMQForOrderPayed(trade);
                trade.getTradeState().setFlowState(FlowState.TOPICKUP);

                //添加操作日志
                String detail = String.format("订单[%s]已付款,待自提,操作人:%s", trade.getId(), eventStr, operator.getName());
                trade.appendTradeEventLog(TradeEventLog
                        .builder()
                        .operator(operator)
                        .eventType("订单待自提")
                        .eventTime(LocalDateTime.now())
                        .eventDetail(detail)
                        .build());
                updateTrade(trade);
                this.operationLogMq.convertAndSend(operator, TradeEvent.PAY.getDescription(), detail);
            } else if (!FlowState.CONFIRMED.equals(trade.getTradeState().getFlowState()) &&
                    PayState.PAID.equals(trade.getTradeState().getPayState())) {
                // 订单支付后，发送MQ消息
                this.sendMQForOrderPayed(trade);


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
            //拼团成功
            if (Objects.nonNull(trade.getGrouponFlag()) && trade.getGrouponFlag() &&
                    Objects.nonNull(trade.getTradeGroupon()) && GrouponOrderStatus.COMPLETE .equals( trade.getTradeGroupon().getGrouponOrderStatus())) {
                StateRequest stateRequest = StateRequest
                        .builder()
                        .tid(trade.getId())
                        .operator(operator)
                        .event(TradeEvent.JOIN_GROUPON)
                        .build();
                pileTradeFSMService.changePileTradeState(stateRequest);
            }

            boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED) && PaymentOrder.NO_LIMIT == trade.getPaymentOrder();
            if (!goodsFirst && PayState.PAID.equals(trade.getTradeState().getPayState())) {
                //短信推送
                if (null != trade.getDeliverWay() && DeliverWay.PICK_SELF.equals(trade.getDeliverWay())) {
                    sendPickUpMessage(trade);
                }
            }

    }

    private void updateProviderTrade(Trade trade) {
        String parentId = trade.getId();
        List<ProviderTrade> tradeList =
                providerTradeService.findListByParentId(parentId);
        if (CollectionUtils.isNotEmpty(tradeList)) {
            tradeList.forEach(childTradeVO -> {
                childTradeVO.getTradeState().setPayState(PayState.PAID);
                TradeUpdateRequest tradeUpdateRequest = new TradeUpdateRequest(KsBeanUtil.convert(childTradeVO, TradeUpdateDTO.class));
                providerTradeService.updateProviderTrade(tradeUpdateRequest);
            });
        }
    }


    /**
     * 发送订单支付、订单完成MQ消息
     *
     * @param trade
     */
    private void sendMQForOrderPayedAndComplete(PileTrade trade) {
        TradeVO tradeVO = KsBeanUtil.convert(trade, TradeVO.class);
        orderProducerService.sendMQForOrderPayedAndComplete(tradeVO);
    }

    /**
     * 订单支付后，发送MQ消息
     *
     * @param trade
     */
    private void sendMQForOrderPayed(PileTrade trade) {
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
        map.put("orderType",TradeActivityTypeEnum.STOCKUP.toActivityType());
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
     * 0 元订单默认支付
     *
     * @param trade
     * @param payWay
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public boolean tradeDefaultPay(PileTrade trade, PayWay payWay) {
        String tid = trade.getId();
        if (Objects.isNull(trade.getTradePrice().getTotalPrice()) || trade.getTradePrice().getTotalPrice().compareTo
                (BigDecimal.ZERO) != 0) {
            throw new SbcRuntimeException("K-050407");
        }
        //0元自提订单推送短信信息
        PickUpRecord pickUpRecord = new PickUpRecord();
        boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED) && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        //是否是自提订单如果不是发送自提码
        if (!goodsFirst) {
            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                pickUpRecord = sendPickUpCode(trade);
                trade.getTradeWareHouse().setPickUpCode(pickUpRecord.getPickUpCode());
                pickUpRecordService.add(pickUpRecord);
                //自提码刷新进订单
                this.updateTrade(trade);
            }
        }

        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(tid).orElse(null);
        ReceivableAddRequest receivableAddRequest = null;
        if (Objects.nonNull(payOrder) && payOrder.getPayType() == PayType.OFFLINE) {
            receivableAddRequest = ReceivableAddRequest.builder().accountId(Constants.DEFAULT_RECEIVABLE_ACCOUNT)
                    .createTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1))
                    .payOrderId(trade.getPayOrderId()).build();
        } else {
            receivableAddRequest = ReceivableAddRequest.builder().payChannelId(Constants
                            .DEFAULT_RECEIVABLE_ACCOUNT).payChannel("默认支付").createTime(DateUtil.format(LocalDateTime.now(),
                            DateUtil.FMT_TIME_1))
                    .payOrderId(trade.getPayOrderId()).build();
        }
        this.addReceivable(receivableAddRequest, Platform.PLATFORM).ifPresent(pay ->
                this.payCallBack(tid, BigDecimal.ZERO,
                        Operator.builder().adminId("0").name("system").account("system").platform
                                (Platform.PLATFORM).build(), payWay)
        );
        //短信推送
        if (!goodsFirst) {
            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
           /* smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{trade.getConsignee().getPhone()},
                    trade.getId(), pickUpRecord.getPickUpCode());*/
                sendPickUpMessage(trade);
            }
        }

        return true;
    }

    /**
     * 0元订单批量支付
     *
     * @param trades
     * @param payWay
     * @return true|false
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void tradeDefaultPayBatch(List<PileTrade> trades, PayWay payWay) {
        trades.forEach(i -> this.tradeDefaultPay(i, payWay));
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

        PileTrade trade = detail(payOrder.getOrderCode());
        //判断是否是先货后款
        boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED)
                && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
        String verifyCode = "";
        if (!goodsFirst && PayOrderStatus.PAYED.equals(status)) {
            //生成自提码并推送
            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                trade.getTradeState().setFlowState(FlowState.TOPICKUP);
                verifyCode = RandomStringUtils.randomNumeric(6);
                trade.getTradeWareHouse().setPickUpCode(verifyCode);
                pickUpRecordService.add(PickUpRecord.builder().storeId(trade.getSupplier().getStoreId())
                        .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                        .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build());
                //刷新订单
                updateTrade(trade);
            }
        }
        Optional.of(payOrder).ifPresent(p ->
                this.payCallBack(p.getOrderCode(), p.getPayOrderPrice(), operator, PayWay.CASH));
        //推送短信
        if (!goodsFirst && PayOrderStatus.PAYED.equals(status)) {
            //自提订单
            if (null != trade.getDeliverWay() && trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
               /* smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{trade.getConsignee().getPhone()},
                        trade.getId(), verifyCode);*/
                sendPickUpMessage(trade);
            }
        }
        if (PayOrderStatus.PAYED.equals(status)) {//如果是已支付,需要增加埋点(线上付款成功 或者 Boss端添加线下付款单)
            //ares埋点-订单-用户线上支付订单 或者 Boss端添加线下付款单
            orderAresService.dispatchFunction("payOrder", payOrder, LocalDateTime.now());
        }
    }


    /**
     * 确认支付单
     * //todo PayService doPay
     *
     * @param payOrderIds payOrderIds
     */
    @LcnTransaction
    @Transactional
    public void confirmPayOrder(List<String> payOrderIds, Operator operator) {
        List<PayOrder> offlinePayOrders = null;
        if (CollectionUtils.isEmpty(payOrderIds)) {
            throw new SbcRuntimeException("K-020002");
        }
        // 页面不区分线上付款还是线下付款，都会传过来，这里先过滤一遍，得到线下付款的
        List<PayOrder> payOrders = payOrderRepository.findByPayOrderIds(payOrderIds);

        if (!CollectionUtils.isEmpty(payOrders)) {
            offlinePayOrders = payOrders.stream().filter(payOrder -> payOrder.getPayType() == PayType.OFFLINE)
                    .collect(Collectors.toList());
            List<String> offlineIds = offlinePayOrders.stream().map(PayOrder::getPayOrderId).collect(Collectors
                    .toList());
            if (!CollectionUtils.isEmpty(offlineIds)) {
                payOrderRepository.updatePayOrderStatus(offlineIds, PayOrderStatus.PAYED);
            }
        }
        Optional.ofNullable(offlinePayOrders).ifPresent(payOrderVOS -> payOrderVOS.forEach(e -> {
            //
            /*  Trade trade = detail(e.getOrderCode());*/
           /* //判断是否是先货后款
            boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED)
                    && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
            String verifyCode="";
            if (!goodsFirst) {
                //生成自提码并推送
                if (null!=trade.getDeliverWay()&&trade.getDeliverWay().equals(DeliverWay.PICK_SELF)){
                    trade.getTradeState().setFlowState(FlowState.TOPICKUP);
                    verifyCode = RandomStringUtils.randomNumeric(6);
                    trade.getTradeWareHouse().setPickUpCode(verifyCode);
                    pickUpRecordService.add(PickUpRecord.builder().storeId(trade.getSupplier().getStoreId())
                            .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                            .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build());
                    //刷新订单
                    tradeService.updateTrade(trade);
                }
            }*/
            this.payCallBack(e.getOrderCode(), e.getPayOrderPrice(), operator, PayWay.CASH);
            PileTrade trade = detail(e.getOrderCode());
            //新增囤货信息<线下支付成功后回调>
            List<GoodsInfoDTO> goodsInfoDTOS = Lists.newArrayList();
            trade.getTradeItems().forEach(ti->{
                GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
                goodsInfoDTO.setGoodsInfoId(ti.getSkuId());
                goodsInfoDTO.setBuyCount(ti.getNum());
                goodsInfoDTO.setGoodsSplitPrice(ti.getSplitPrice().divide(new BigDecimal(ti.getNum()),20,BigDecimal.ROUND_HALF_UP));
                goodsInfoDTOS.add(goodsInfoDTO);
            });
            PurchaseSaveDTO build = PurchaseBatchSaveRequest.builder().customerId(trade.getBuyer().getId())
                    .goodsInfos(goodsInfoDTOS)
                    .wareId(trade.getWareId())
                    .inviteeId(trade.getInviteeId())
                    .pid(trade.getParentId())
                    .orderCode(trade.getId())
                    .orderTotalPrice(trade.getTradePrice().getTotalPrice())
                    .build();
            pilePurchaseService.batchSave(KsBeanUtil.convert(build,PilePurchaseRequest.class));
          /*  //推送短信
            if (!goodsFirst) {
                //自提订单
                if (null!=trade.getDeliverWay()&&trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                    *//*smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{trade.getConsignee().getPhone()},
                            trade.getId(), verifyCode);*//*
                    sendPickUpMessage(trade);
                }
            }*/
        }));
        //ares埋点-订单-用户线下支付订单,商家确认
        orderAresService.dispatchFunction("offlinePayOrder", payOrders, LocalDateTime.now());
    }

    /**
     * 线下确认单
     * @param payOrderIds
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void confirmPayOrderOffline(List<OfflineSettlementVO> payOrderIds, Operator operator) {
        List<PayOrder> offlinePayOrders = null;
        if (CollectionUtils.isEmpty(payOrderIds)) {
            throw new SbcRuntimeException("K-020002");
        }
        logger.info("PileTradeService.confirmPayOrderOffline payOrderId：{} realPay：{}",payOrderIds.get(0).getPayOrderId(),payOrderIds.get(0).getRealPay());
        // 页面不区分线上付款还是线下付款，都会传过来，这里先过滤一遍，得到线下付款的
        List<String> offlinePayOrderId = new ArrayList<>();
        offlinePayOrderId.add(payOrderIds.get(0).getPayOrderId());
        List<PayOrder> payOrders = payOrderRepository.findByPayOrderIds(offlinePayOrderId);

        if (!CollectionUtils.isEmpty(payOrders)) {
            offlinePayOrders = payOrders.stream().filter(payOrder -> payOrder.getPayType() == PayType.OFFLINE)
                                        .collect(Collectors.toList());
            offlinePayOrders.stream().forEach(orders->{
                payOrderIds.stream().forEach(payOrder->{
                    if (Objects.nonNull(orders) && Objects.nonNull(payOrder) && orders.getPayOrderId().equals(payOrder.getPayOrderId())){
                        payOrderRepository.updatePayOrderStatusAmountRealPay(orders.getPayOrderId(),PayOrderStatus.PAYED,payOrder.getRealPay());
                    }
                });
            });
        }
        Optional.ofNullable(offlinePayOrders).ifPresent(payOrderVOS -> payOrderVOS.forEach(e -> {
            //
            /*  Trade trade = detail(e.getOrderCode());*/
           /* //判断是否是先货后款
            boolean goodsFirst = Objects.equals(trade.getTradeState().getAuditState(), AuditState.CHECKED)
                    && trade.getPaymentOrder() == PaymentOrder.NO_LIMIT;
            String verifyCode="";
            if (!goodsFirst) {
                //生成自提码并推送
                if (null!=trade.getDeliverWay()&&trade.getDeliverWay().equals(DeliverWay.PICK_SELF)){
                    trade.getTradeState().setFlowState(FlowState.TOPICKUP);
                    verifyCode = RandomStringUtils.randomNumeric(6);
                    trade.getTradeWareHouse().setPickUpCode(verifyCode);
                    pickUpRecordService.add(PickUpRecord.builder().storeId(trade.getSupplier().getStoreId())
                            .tradeId(trade.getId()).pickUpCode(verifyCode).pickUpFlag(DefaultFlag.NO)
                            .delFlag(DeleteFlag.NO).contactPhone(trade.getConsignee().getPhone()).createTime(LocalDateTime.now()).build());
                    //刷新订单
                    tradeService.updateTrade(trade);
                }
            }*/
            this.payCallBack(e.getOrderCode(), e.getPayOrderPrice(), operator, PayWay.CASH);
            PileTrade trade = detail(e.getOrderCode());
            //新增囤货信息<线下支付成功后回调>
            List<GoodsInfoDTO> goodsInfoDTOS = Lists.newArrayList();
            trade.getTradeItems().forEach(ti->{
                GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
                goodsInfoDTO.setGoodsInfoId(ti.getSkuId());
                goodsInfoDTO.setBuyCount(ti.getNum());
                goodsInfoDTO.setGoodsSplitPrice(ti.getSplitPrice().divide(new BigDecimal(ti.getNum()),20,BigDecimal.ROUND_HALF_UP));
                goodsInfoDTOS.add(goodsInfoDTO);
            });
            PurchaseSaveDTO build = PurchaseBatchSaveRequest.builder().customerId(trade.getBuyer().getId())
                                                            .goodsInfos(goodsInfoDTOS)
                                                            .wareId(trade.getWareId())
                                                            .inviteeId(trade.getInviteeId())
                                                            .pid(trade.getParentId())
                                                            .orderCode(trade.getId())
                                                            .orderTotalPrice(trade.getTradePrice().getTotalPrice())
                                                            .build();
            pilePurchaseService.batchSave(KsBeanUtil.convert(build,PilePurchaseRequest.class));
          /*  //推送短信
            if (!goodsFirst) {
                //自提订单
                if (null!=trade.getDeliverWay()&&trade.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                    *//*smsSendUtil.send(SmsTemplate.CUSTOMER_PASSWORD, new String[]{trade.getConsignee().getPhone()},
                            trade.getId(), verifyCode);*//*
                    sendPickUpMessage(trade);
                }
            }*/
        }));
        //ares埋点-订单-用户线下支付订单,商家确认
        orderAresService.dispatchFunction("offlinePayOrder", payOrders, LocalDateTime.now());
    }


    /**
     * 更新订单的结算状态
     *
     * @param storeId
     * @param startTime
     * @param endTime
     */
    public void updateSettlementStatus(Long storeId, Date startTime, Date endTime) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("supplier.storeId").is(storeId)
                , new Criteria().orOperator(
                        Criteria.where("tradeState.flowState").is(FlowState.COMPLETED),
                        Criteria.where("refundFlag").is(true))
                , Criteria.where("tradeState.deliverStatus").in(Arrays.asList(DeliverStatus.SHIPPED,
                        DeliverStatus.PART_SHIPPED))
                , Criteria.where("tradeState.endTime").lt(endTime).gte(startTime)
        );

        mongoTemplate.updateMulti(new Query(criteria), new Update().set("hasBeanSettled", true), PileTrade.class);
    }

    /**
     * 查询订单信息作为结算原始数据
     *
     * @param storeId
     * @param startTime
     * @param endTime
     * @param pageRequest
     * @return
     */
    public List<PileTrade> findTradeListForSettlement(Long storeId, Date startTime, Date endTime, Pageable pageRequest) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("supplier.storeId").is(storeId)
                , new Criteria().orOperator(
                        Criteria.where("tradeState.flowState").is(FlowState.COMPLETED),
                        Criteria.where("tradeState.flowState").is(FlowState.VOID),
                        Criteria.where("refundFlag").is(true))
                , Criteria.where("tradeState.deliverStatus").in(Arrays.asList(DeliverStatus.SHIPPED,
                        DeliverStatus.PART_SHIPPED))
                , Criteria.where("returnOrderNum").is(0)
                , Criteria.where("tradeState.finalTime").lt(endTime).gte(startTime)
        );

        return mongoTemplate.find(
                new Query(criteria).skip(pageRequest.getPageNumber() * pageRequest.getPageSize()).limit(pageRequest
                        .getPageSize())
                , PileTrade.class);
    }

    /**
     * 根据快照封装订单确认页信息
     *
     * @param g
     * @return
     */
    public TradeConfirmItem getPurchaseInfo(TradeItemGroup g, List<TradeItem> gifts) {
        TradeConfirmItem item = new TradeConfirmItem();
        TradePrice price = new TradePrice();
        item.setTradeItems(g.getTradeItems());
        item.setSupplier(g.getSupplier());
        //计算商品总价
        handlePrice(g.getTradeItems(), price);
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
            pileTradeItemService.clacSplitPrice(items, i.getRealPayAmount());
        });

        //应付金额 = 商品总金额 - 优惠总金额

        if (!price.isSpecial()) {
            BigDecimal discountsPrice = tempList.stream().map(TradeMarketingVO::getDiscountsAmount).reduce(BigDecimal
                    .ZERO, BigDecimal::add);
            price.setTotalPrice(price.getTotalPrice().subtract(discountsPrice));
        }
        item.setTradePrice(price);
        //赠品信息
        item.setGifts(wrapperGifts(g.getTradeMarketingList(), tradeMarketings, gifts));
        item.setGifts(giftNumCheck(item.getGifts(), g.getWareId()));
        return item;
    }

    /**
     * 包装营销信息(供确认订单使用)
     */
    public List<TradeMarketingVO> wrapperMarketingForConfirm(List<TradeItem> skus, List<TradeMarketingDTO>
            tradeMarketingRequests) {

        // 1.构建营销插件请求对象
        List<TradeMarketingWrapperDTO> requests = new ArrayList<>();
        List<TradeMarketingVO> tradeMarketings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tradeMarketingRequests)) {
            tradeMarketingRequests.forEach(tradeMarketing -> {
                List<TradeItemInfoDTO> tradeItems = skus.stream()
                        .filter(s -> tradeMarketing.getSkuIds().contains(s.getSkuId()))
                        .map(t -> TradeItemInfoDTO.builder()
                                .num(t.getNum())
                                .price(t.getPrice())
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
        // 2.调用营销插件，并设置满系营销信息
        if (CollectionUtils.isNotEmpty(requests)) {
            List<TradeMarketingWrapperVO> voList =
                    marketingTradePluginProvider.batchWrapper(MarketingTradeBatchWrapperRequest.builder()
                            .wraperDTOList(requests).build()).getContext().getWraperVOList();
            if (CollectionUtils.isNotEmpty(voList)) {
                voList.forEach(tradeMarketingWrapperVO -> {
                    tradeMarketings.add(tradeMarketingWrapperVO.getTradeMarketing());
                });
            }
        }

        return tradeMarketings;
    }


    /**
     * 包装营销信息(供提交订单使用)
     */
    public void wrapperMarketingForCommit(PileTrade trade, TradeParams tradeParams, CustomerVO customerVO) {

        // 1.构建订单满系营销对象
        trade.setTradeMarketings(this.wrapperMarketingForConfirm(trade.getTradeItems(),
                tradeParams.getMarketingList()));

        // 2.构建订单优惠券对象
        if (StringUtils.isNotEmpty(tradeParams.getCouponCodeId())) {
            trade.setTradeCoupon(this.buildTradeCouponInfo(
                    trade.getTradeItems(), tradeParams.getCouponCodeId(), tradeParams.isForceCommit(),
                    StringUtils.isNotBlank(customerVO.getParentCustomerId()) ? customerVO.getParentCustomerId() : customerVO.getCustomerId()));
        }

    }


    /**
     * 调用营销插件，构造订单优惠券对象
     *
     * @return
     */
    private TradeCouponVO buildTradeCouponInfo(List<TradeItem> tradeItems, String couponCodeId,
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
                        .num(t.getNum())
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
     * 获取赠品信息
     * 主要是设置各赠品应赠送的数量
     *
     * @param marketingRequests
     * @param tradeMarketings
     * @param gifts             @return
     */
    private List<TradeItem> wrapperGifts(List<TradeMarketingDTO> marketingRequests,
                                         List<TradeMarketingVO> tradeMarketings, List<TradeItem> gifts) {
        if (CollectionUtils.isEmpty(gifts)) {
            return Collections.emptyList();
        }
        List<TradeItem> resultList = new ArrayList<>();
        Map<Long, TradeMarketingVO> tradeMarketingMap = tradeMarketings.stream().filter(m -> m.getMarketingType() ==
                        MarketingType.GIFT)
                .collect(Collectors.toMap(TradeMarketingVO::getMarketingId, Function.identity()));
        for (TradeMarketingDTO i : marketingRequests) {
            TradeMarketingVO marketing = tradeMarketingMap.get(i.getMarketingId());
            if (marketing == null) {
                //若传入的营销并非满赠,则跳过循环
                continue;
            }
            MarketingFullGiftLevelVO level = marketing.getGiftLevel();
            // 已经在营销插件中取出
//            FullGiftDetailListByMarketingIdAndLevelIdRequest request =
//                    FullGiftDetailListByMarketingIdAndLevelIdRequest.builder().build();
//            request.setMarketingId(i.getMarketingId());
//            request.setGiftLevelId(level.getGiftLevelId());
//            FullGiftDetailListByMarketingIdAndLevelIdResponse fullGiftDetailListByMarketingIdAndLevelIdResponse =
//                    fullGiftQueryProvider.listDetailByMarketingIdAndLevelId(request).getContext();
//
//            Map<String, MarketingFullGiftDetailVO> detailMap = fullGiftDetailListByMarketingIdAndLevelIdResponse
//                    .getFullGiftDetailVOList().stream().filter(d -> i.getGiftSkuIds().contains(d.getProductId()))
//                    .collect(Collectors.toMap(MarketingFullGiftDetailVO::getProductId, Function.identity()));

//            level.setFullGiftDetailList(new ArrayList<>(detailMap.values()));
            Map<String, MarketingFullGiftDetailVO> detailMap = level.getFullGiftDetailList().stream().filter(d -> i.getGiftSkuIds().contains(d.getProductId()))
                    .collect(Collectors.toMap(MarketingFullGiftDetailVO::getProductId, Function.identity()));
            List<String> giftIds = new ArrayList<>(detailMap.keySet());
            //校验是否满足满赠条件
            boolean flag = i.getGiftSkuIds().stream().anyMatch(g -> !giftIds.contains(g));
            if (flag) {
                throw new SbcRuntimeException("K-050312");
            }

            List<TradeItem> giftItems = gifts.stream().filter(g -> i.getGiftSkuIds().contains(g.getSkuId()))
                    .collect(Collectors.toList());
            List<TradeItem> tpList = giftItems.stream().map(g -> {
                TradeItem item = new TradeItem();
                BeanUtils.copyProperties(g, item);
                item.setNum(detailMap.get(g.getSkuId()).getProductNum());
                return item;
            }).collect(Collectors.toList());
            resultList.addAll(tpList);
        }
        return resultList;

    }

    /**
     * 平台,商家带客下单，审核关闭都要创建支付单
     *
     * @param trade
     * @param operator
     * @param orderAuditSwitch
     */
    private void createPayOrder(PileTrade trade, Operator operator, Boolean orderAuditSwitch) {
        if (operator.getPlatform() == Platform.BOSS || operator.getPlatform() == Platform.SUPPLIER ||
                !orderAuditSwitch) {
            createPayOrder(trade);
        }
    }

    private void createOrderInvoice(PileTrade trade, Operator operator) {
        OrderInvoiceSaveRequest request = buildOrderInvoiceSaveRequest(trade);
        if (request == null) {
            return;
        }
        Optional<OrderInvoice> optional = orderInvoiceService.generateOrderInvoice(request, operator.getUserId(),
                InvoiceState.WAIT);
        optional.ifPresent(invoice -> trade.getInvoice().setOrderInvoiceId(invoice.getOrderInvoiceId()));
    }

    private OrderInvoiceSaveRequest buildOrderInvoiceSaveRequest(PileTrade trade) {
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
     * 计算订单价格
     * 订单价格 = 商品总价 - 营销优惠总金额
     *
     * @param trade
     */
    private TradePrice calc(PileTrade trade) {
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
        tradePrice.setDiscountsPrice(discountPrice);
        tradePrice.setTotalPrice(tradePrice.getTotalPrice().subtract(discountPrice));
        return tradePrice;
    }


    /**
     * 1.根据 订单项List 获取商品信息List
     * 2.设置该商品信息List中会用到的区间价信息
     * 3.修改商品信息List中的会员价(salePrice)
     *
     * @param trade 订单
     * @return 商品信息List
     */
    public TradeGoodsListVO getGoodsInfoResponse(PileTrade trade) {
        //1. 获取sku
        Buyer b = trade.getBuyer();
        GoodsInfoViewByIdsRequest goodsInfoRequest = new GoodsInfoViewByIdsRequest();
        goodsInfoRequest.setGoodsInfoIds(IteratorUtils.collectKey(trade.getTradeItems(), TradeItem::getSkuId));
        goodsInfoRequest.setIsHavSpecText(Constants.yes);
        goodsInfoRequest.setWareId(trade.getWareId());
        goodsInfoRequest.setWareHouseCode(trade.getWareHouseCode());
        GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIdsLimitWareId(goodsInfoRequest).getContext();
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
        return response;
    }


    private void calcGoodsPrice(List<TradeItem> tradeItems, TradeGoodsListVO goodsInfoResponse) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap =
                goodsInfos.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,
                        Function.identity()));
        tradeItems
                .forEach(tradeItem -> {
                    GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
                    GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
                    //4. 填充价格
                    List<GoodsIntervalPriceVO> goodsIntervalPrices = goodsInfoResponse.getGoodsIntervalPrices();
                    // 订货区间设价
                    if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
                        Long buyNum = tradeItem.getNum();
                        Optional<GoodsIntervalPriceVO> first = goodsIntervalPrices.stream()
                                .filter(item -> item.getGoodsInfoId().equals(tradeItem.getSkuId()))
                                .filter(intervalPrice -> buyNum >= intervalPrice.getCount()).max(Comparator
                                        .comparingLong(GoodsIntervalPriceVO::getCount));
                        if (first.isPresent()) {
                            GoodsIntervalPriceVO goodsIntervalPrice = first.get();
                            tradeItem.setLevelPrice(goodsIntervalPrice.getPrice());
                            tradeItem.setPrice(goodsIntervalPrice.getPrice());
                            return;
                        }
                    }
                    tradeItem.setPrice(goodsInfo.getSalePrice());
                    tradeItem.setLevelPrice(goodsInfo.getSalePrice());
                });
    }

    private void checkLogisticsNo(String logisticsNo, String logisticStandardCode) {
        if (pileTradeRepository
                .findTopByTradeDelivers_Logistics_LogisticNoAndTradeDelivers_Logistics_logisticStandardCode(logisticsNo,
                        logisticStandardCode)
                .isPresent()) {
            throw new SbcRuntimeException("K-050124");
        }

    }

    /**
     * 计算商品总价
     *
     * @param tradeItems 多个订单项(商品)
     * @param tradePrice 订单价格对象(其中包括商品商品总金额,原始金额,应付金额)
     */
    private void handlePrice(List<TradeItem> tradeItems, TradePrice tradePrice) {
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

    private List<TradeItem> giftNumCheck(List<TradeItem> gifts, Long wareId) {
        if (CollectionUtils.isEmpty(gifts)) {
            return Collections.emptyList();
        }
        List<TradeItem> distinctGifts = new ArrayList<>();
        //相同赠品累加
        Map<String, List<TradeItem>> giftMap = gifts.stream().collect(Collectors.groupingBy(TradeItem::getSkuId));
        giftMap.forEach((key, item) -> {
            Long num = item.stream().map(TradeItem::getNum).reduce(0L, (a, b) -> a + b);
            TradeItem tradeItem = item.get(0);
            tradeItem.setNum(num);
            distinctGifts.add(tradeItem);
        });

        List<String> giftIds = new ArrayList<>(giftMap.keySet());

        Map<String, GoodsInfoVO> skusMap = goodsInfoQueryProvider.listByIds(
                        GoodsInfoListByIdsRequest.builder().goodsInfoIds(giftIds).wareId(wareId).build()
                ).getContext().getGoodsInfos().stream()
                .collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
        distinctGifts.forEach(i -> {
            //赠品根据库存剩余，赠完为止
            GoodsInfoVO goodsInfo = skusMap.get(i.getSkuId());
            if (BigDecimal.valueOf(i.getNum()).compareTo(goodsInfo.getStock()) > 0) {
                i.setNum(goodsInfo.getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
            }
        });
        return distinctGifts;
    }

    /**
     * 根据营销活动,检查并设置各赠品数量
     *
     * @param trade
     */
    private void giftSet(PileTrade trade) {
        //赠品设置
        List<TradeMarketingDTO> marketingRequests = new ArrayList<>();
        trade.getTradeMarketings().forEach(i -> {
            if (i.getMarketingType() == MarketingType.GIFT) {
                TradeMarketingDTO req = TradeMarketingDTO.builder()
                        .giftSkuIds(i.getGiftIds())
                        .marketingId(i.getMarketingId())
                        .marketingLevelId(i.getGiftLevel().getGiftLevelId())
                        .skuIds(i.getSkuIds())
                        .build();
                marketingRequests.add(req);
            }
        });
        trade.setGifts(wrapperGifts(marketingRequests, trade.getTradeMarketings(), trade.getGifts()));
        trade.setGifts(giftNumCheck(trade.getGifts(), trade.getWareId()));
    }

    /**
     * 营销价格计算-结算信息设置
     * 【商品价格计算第②步】: 商品的 满折/满减营销活动 均摊价 -> splitPrice
     *
     * @param trade
     */
    private void calcMarketingPrice(PileTrade trade) {
        // 1.设置满系营销商品优惠后的均摊价、结算信息
        trade.getTradeMarketings().stream().filter(i -> i.getMarketingType() != MarketingType.GIFT).forEach(i -> {
            List<TradeItem> items = trade.getTradeItems().stream().filter(t -> i.getSkuIds().contains(t.getSkuId()))
                    .collect(Collectors.toList());
            pileTradeItemService.clacSplitPrice(items, i.getRealPayAmount());
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
            BigDecimal total = pileTradeItemService.calcSkusTotalPrice(items);

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
            pileTradeItemService.calcSplitPrice(items, total.subtract(trade.getTradeCoupon().getDiscountsAmount()), total);
            items.forEach(item -> {
                TradeItem.CouponSettlement settlement =
                        item.getCouponSettlements().get(item.getCouponSettlements().size() - 1);
                settlement.setReducePrice(settlement.getSplitPrice().subtract(item.getSplitPrice()));
                settlement.setSplitPrice(item.getSplitPrice());
            });
        }


    }

    /**
     * 更新订单的业务员
     *
     * @param employeeId 业务员
     * @param customerId 客户
     */
    public void updateEmployeeId(String employeeId, String customerId) {
        mongoTemplate.updateMulti(new Query(Criteria.where("buyer.id").is(customerId)), new Update().set("buyer" +
                ".employeeId", employeeId), PileTrade.class);
    }

    /**
     * 更新是否返利标志
     *
     * @param tradeId
     * @param commissionFlag
     */
    public void updateCommissionFlag(String tradeId, Boolean commissionFlag) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(tradeId)),
                new Update().set("commissionFlag", commissionFlag), PileTrade.class);
    }

    /**
     * 更新入账时间
     */
    public void updateFinalTime(String tradeId, LocalDateTime finalTime) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(tradeId)),
                new Update().set("tradeState.finalTime", finalTime), PileTrade.class);
    }

    /**
     * 更新正在进行的退单数量、入账时间
     *
     * @param tradeId 订单id
     * @param addFlag 退单数加减状态
     */
    public void updateReturnOrderNum(String tradeId, boolean addFlag) {
        PileTrade trade = pileTradeRepository.findById(tradeId).orElse(null);
        if (Objects.isNull(trade)) {
            log.error("订单ID:{},查询不到订单信息", tradeId);
            return;
        }
        // 1.根据addFlag加减正在进行的退单
        Integer num = trade.getReturnOrderNum();

        // 2.如果当前退单完成时间比入账时间晚时,或者订单未完成直接进行退款操作，则将当前退单完成时间设置为入账时间
        LocalDateTime finalTime = trade.getTradeState().getFinalTime();
        LocalDateTime nowTime = LocalDateTime.now();
        if (Objects.isNull(finalTime) || (!addFlag && nowTime.isAfter(finalTime))) {
            finalTime = nowTime;
        }
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(tradeId)), new Update()
                .set("returnOrderNum", addFlag ? ++num : --num)
                .set("tradeState.finalTime", finalTime), Trade.class);
    }


    /**
     * 完善没有业务员的订单
     */
    public void fillEmployeeId() {
        List<PileTrade> trades = mongoTemplate.find(new Query(Criteria.where("buyer.employeeId").is(null)), PileTrade.class);
        if (CollectionUtils.isEmpty(trades)) {
            return;
        }
        List<String> buyerIds = trades.stream()
                .filter(t -> Objects.nonNull(t.getBuyer()) && StringUtils.isNotBlank(t.getBuyer().getId()))
                .map(PileTrade::getBuyer)
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
     * 退优惠券
     *
     * @param tradeId 订单id
     */
    public void returnCoupon(String tradeId) {
        // 获取当前的======订单
        PileTrade trade = this.detail(tradeId);
        // 获取订单中购买的商品数量
        Map<String, TradeItem> boughtSkuNum = trade.getTradeItems().stream()
                .collect(Collectors.toMap(TradeItem::getSkuId, Function.identity()));
        // 累加所有已退商品的数量
        Map<String, BigDecimal> returnSkuNum = new HashMap<>();
        // 商家驳回订单
        if (trade.getTradeState().getAuditState() == AuditState.REJECTED) {
            setReturnNum(returnSkuNum, boughtSkuNum);
        } else if (trade.getTradeState().getFlowState() == FlowState.VOID
                && trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED
                && trade.getTradeState().getPayState() == PayState.NOT_PAID) {
            // 用户取消订单
            setReturnNum(returnSkuNum, boughtSkuNum);
        }
        // 获取所有已退的===退单
        List<ReturnPileOrder> returnOrders = returnPileOrderRepository.findByTid(trade.getId()).stream()
                .filter(item -> item.getReturnFlowState() == ReturnFlowState.COMPLETED)
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
        PileTradeGroup tradeGroup = StringUtils.isNotEmpty(trade.getGroupId()) ?
                pileTradeGroupRepository.findById(trade.getGroupId()).orElse(null) : null;
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
                            pileTradeGroupRepository.save(tradeGroup);
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
                            pileTradeGroupRepository.save(tradeGroup);
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
     * 设置退货数量
     *
     * @param returnSkuNum
     * @param boughtSkuNum
     */
    private void setReturnNum(Map<String, BigDecimal> returnSkuNum, Map<String, TradeItem> boughtSkuNum) {
        boughtSkuNum.forEach((key, value) -> {
            BigDecimal returnNum = returnSkuNum.get(key);
            if (Objects.isNull(returnNum)) {
                returnSkuNum.put(key, BigDecimal.valueOf(value.getNum()));
            } else {
                returnSkuNum.put(key, returnNum.add(BigDecimal.valueOf(value.getNum())));
            }
        });
    }


    /**
     * 根据查询条件获取订单列表--不分页
     *
     * @param whereCriteria
     * @return
     */
    public List<PileTrade> getTradeList(Criteria whereCriteria) {
        Query query = new Query(whereCriteria);
        List<PileTrade> tradeList = mongoTemplate.find(query, PileTrade.class);
        return tradeList;
    }

    /**
     * 订单超时未支付，系统自动取消订单
     *
     * @param tid
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void autoCancelOrder(String tid, Operator operator) {
        PileTrade trade = detail(tid);
        //异常状态订单无需抛异常，不作处理即可
        if (trade.getTradeState().getPayState() == PayState.PAID || trade.getTradeState().getPayState() == PayState.UNCONFIRMED) {
//            throw new SbcRuntimeException("K-050202");
            return;
        }
        if (trade.getTradeState().getDeliverStatus() != DeliverStatus.NOT_YET_SHIPPED) {
//            throw new SbcRuntimeException("K-050203");
            return;
        }

        if (trade.getTradeState().getFlowState() == FlowState.VOID) {
//            throw new SbcRuntimeException("K-050317");
            return;
        }

        if (trade.getTradeState().getAuditState() == AuditState.CHECKED) {
            //删除支付单
            payOrderService.deleteByPayOrderId(trade.getPayOrderId());
        }

        //状态变更
        StateRequest stateRequest = StateRequest
                .builder()
                .tid(trade.getId())
                .operator(operator)
                .event(TradeEvent.VOID)
                .data("订单超时未支付，系统自动取消")
                .build();
        pileTradeFSMService.changePileTradeState(stateRequest);

        // 退优惠券
        returnCoupon(tid);

        //删除囤货商品表数据
//        List<GoodsInfoDTO> goodsInfoDTOS = Lists.newArrayList();
//        trade.getTradeItems().forEach(ti->{
//            GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
//            goodsInfoDTO.setGoodsInfoId(ti.getSkuId());
//            goodsInfoDTO.setBuyCount(ti.getNum());
//
//        });

//        PilePurchaseRequest pilePurchaseRequest = new PilePurchaseRequest();
//        pilePurchaseRequest.setCustomerId(operator.getUserId());
//        pilePurchaseRequest.setGoodsInfos(goodsInfoDTOS);
//        pilePurchaseRequest.setInviteeId(trade.getInviteeId());
//        pilePurchaseRequest.setGoodsInfoIds(trade.getTradeItems().stream().map(t->t.getSkuId()).collect(Collectors.toList()));
//        pilePurchaseService.updateNumByIds(pilePurchaseRequest);

        // 取消供应商订单
        providerTradeService.providerCancel(tid, operator, true);

        //减去营销商品限购数量
        calMarketGoodsNum(trade.getTradeItems(),true);
    }

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 秒杀商品订单还库存
     * @Date 13:47 2019/7/2
     * @Param [trade]
     **/
    @Transactional
    public void flashSaleGoodsOrderAddStock(PileTrade trade) {
        //获取秒杀抢购活动详情
        FlashSaleGoodsVO flashSaleGoodsVO = flashSaleGoodsQueryProvider.getById(FlashSaleGoodsByIdRequest.builder()
                        .id(trade.getTradeItems().get(0).getFlashSaleGoodsId())
                        .build())
                .getContext().getFlashSaleGoodsVO();
        //判断秒杀活动是否还在进行中，如果在进行中，将库存加到秒杀活动商品的库存，否则加到原商品库存
        if (LocalDateTime.now().isAfter(flashSaleGoodsVO.getActivityFullTime()) &&
                LocalDateTime.now().isBefore(flashSaleGoodsVO.getActivityFullTime().plusHours(2))) {
            trade.getBuyer().getId();
            verifyService.addFlashSaleGoodsStock(trade.getTradeItems(), trade.getBuyer().getId());
        } else {
            //释放库存
            verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),null);
            verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),null);
        }
    }

    /**
     * 订单选择银联企业支付通知财务
     *
     * @param customerId
     * @param orderId
     * @param url
     */
    public void sendEmailToFinance(String customerId, String orderId, String url) {
        // 客户id、订单id、PC端服务器路径url不能为空
        if (StringUtils.isBlank(customerId) || StringUtils.isBlank(orderId) || StringUtils.isBlank(url)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        } else {
            BaseResponse<EmailConfigQueryResponse> config = emailConfigProvider.queryEmailConfig();
            // 邮箱停用状态下直接返回
            if (config.getContext().getStatus() == EmailStatus.DISABLE) {
                return;
            }
            // 查询客户收信邮箱
//            List<CustomerEmail> customerEmails = customerEmailRepository
//                    .findCustomerEmailsByCustomerIdAndDelFlagOrderByCreateTime(customerId, DeleteFlag.NO);
            List<CustomerEmailVO> customerEmails = customerEmailQueryProvider
                    .list(new NoDeleteCustomerEmailListByCustomerIdRequest(customerId)).getContext()
                    .getCustomerEmails();
            if (customerEmails.isEmpty()) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            PileTrade tradedetail = this.detail(orderId);
            tradeEmailService.sendMail(config, customerEmails, KsBeanUtil.convert(tradedetail,Trade.class), url);
        }
    }

    /**
     * 将上月订单发送到制定邮件
     */
    public void sendEmailTranslate() {
        BaseResponse<EmailConfigQueryResponse> config = emailConfigProvider.queryEmailConfig();
        // 邮箱停用状态下直接返回
        if (config.getContext().getStatus() == EmailStatus.DISABLE) {
            return;
        }
        //获取上个月第一天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startMonthDay = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        //获取上个月的最后一天
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 0);
        String endMonthDay = new SimpleDateFormat("yyyy-MM-dd").format(calendar2.getTime());
        String format = new SimpleDateFormat("yyyy-MM").format(calendar2.getTime());
        TradeListExportRequest request = new TradeListExportRequest();
        TradeQueryDTO dto = new TradeQueryDTO();
        dto.setBeginTime(startMonthDay);
        dto.setEndTime(endMonthDay);
        request.setTradeQueryDTO(dto);
        log.info("================================开始查" + format + "订单信息=======================================", LocalDateTime.now());
        List<TradeVO> list = pileTradeQueryProvider.listTradeExportMonth(request).getContext().getTradeVOList();
        log.info("================================查询" + format + "订单信息结束=======================================", LocalDateTime.now());
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            log.info("================================开始转换" + format + "订单信息=======================================");
            byteArrayOutputStream = tradeExportUtil.exportToByteArrayOutputStream(list);
            log.info("================================转换" + format + "订单信息完成=======================================");
        } catch (Exception e) {
            log.error("sendEmailTranslate,", e);
        }

        GenerateExcelSendEmailVo vo = new GenerateExcelSendEmailVo<>();
        vo.setOut(byteArrayOutputStream);
        // 3.设置email的title
        vo.setEmailTitle("订单信息");
        //4.设置email的内容
        vo.setEmailContent("订单详情");
        // 5.设置收件人
        List<String> acceptAddressList = new ArrayList<>();
        if(StringUtils.isNotEmpty(sendOrderLastMonth)){
            String[] split = sendOrderLastMonth.split(",");
            for (String s : split) {
                if(StringUtils.isNotEmpty(s)){
                    acceptAddressList.add(s);
                }
            }
        }
        vo.setAcceptAddressList(acceptAddressList);
        Long nanoTime = System.nanoTime();
        if(CollectionUtils.isNotEmpty(acceptAddressList)){
            log.info("####开始发送邮件#### : {} -- {}", acceptAddressList, nanoTime);
            emailOrderUtil.sendOrderEmail(vo, config);
        }else {
            log.info("#####收件人邮箱为空!请前往配置文件设置邮箱！######");
        }
    }

    /**
     * 查询导出数据
     *
     * @param tradeQueryRequest
     */
    public List<PileTrade> listTradeExportMonth(TradeQueryRequest tradeQueryRequest) {
//        long count = this.countNum(tradeQueryRequest.getWhereCriteria(), tradeQueryRequest);
//        if (count > 1000) {
//            count = 1000;
//        }
//        tradeQueryRequest.putSort(tradeQueryRequest.getSortColumn(), tradeQueryRequest.getSortRole());
//        tradeQueryRequest.setPageNum(0);
//        tradeQueryRequest.setPageSize((int) count);

        //设置返回字段
        Map fieldsObject = new HashMap();
        fieldsObject.put("_id", true);
        fieldsObject.put("tradeState.createTime", true);
        fieldsObject.put("buyer.name", true);
        fieldsObject.put("buyer.account", true);
        fieldsObject.put("buyer.levelName", true);
        fieldsObject.put("consignee.name", true);
        fieldsObject.put("consignee.phone", true);
        fieldsObject.put("consignee.detailAddress", true);
        fieldsObject.put("payInfo.desc", true);
        fieldsObject.put("deliverWay", true);
        fieldsObject.put("tradePrice.deliveryPrice", true);
        fieldsObject.put("tradePrice.goodsPrice", true);
        fieldsObject.put("tradePrice.special", true);
        fieldsObject.put("tradePrice.privilegePrice", true);
        fieldsObject.put("tradePrice.totalPrice", true);
        fieldsObject.put("tradeItems.oid", true);
        fieldsObject.put("tradeItems.skuId", true);
        fieldsObject.put("tradeItems.skuNo", true);
        fieldsObject.put("tradeItems.num", true);
        fieldsObject.put("buyerRemark", true);
        fieldsObject.put("sellerRemark", true);
        fieldsObject.put("tradeState.flowState", true);
        fieldsObject.put("tradeState.payState", true);
        fieldsObject.put("tradeState.deliverStatus", true);
        fieldsObject.put("invoice.type", true);
        fieldsObject.put("invoice.projectName", true);
        fieldsObject.put("invoice.generalInvoice.title", true);
        fieldsObject.put("invoice.specialInvoice.companyName", true);
        fieldsObject.put("supplier.supplierName", true);
        //增加返回赠品信息
        fieldsObject.put("gifts.oid", true);
        fieldsObject.put("gifts.skuId", true);
        fieldsObject.put("gifts.skuNo", true);
        fieldsObject.put("gifts.num", true);
        //优惠券信息
        //券码id
        fieldsObject.put("tradeCoupon.couponCodeId", true);
        fieldsObject.put("tradeCoupon.couponCode", true);
        fieldsObject.put("tradeCoupon.discountsAmount", true);
        fieldsObject.put("buyer", true);
        String beginTime = tradeQueryRequest.getBeginTime();
        String endTime = tradeQueryRequest.getEndTime();
        Criteria criteria = new Criteria();
        LocalDateTime localDateTime = DateUtil.parseDay(endTime).plusDays(1);
        Criteria gte = Criteria.where("tradeState.createTime").gte(DateUtil.parseDay(beginTime));
        Criteria lt = Criteria.where("tradeState.createTime").lt(localDateTime);
        criteria.andOperator(gte, lt);
        Query query = new Query(criteria);
        System.err.println("mongo：  " + LocalDateTime.now());
        List<PileTrade> tradeList = mongoTemplate.find(query, PileTrade.class);

        System.err.println("mongo：  " + LocalDateTime.now());
        return tradeList;
    }


    public List<PileTrade> listTradeExport(TradeQueryRequest tradeQueryRequest) {
        long count = this.countNum(tradeQueryRequest.getWhereCriteria(), tradeQueryRequest);
        if (count > 3000) {
            count = 3000;
        }
        tradeQueryRequest.putSort(tradeQueryRequest.getSortColumn(), tradeQueryRequest.getSortRole());
        tradeQueryRequest.setPageNum(0);
        tradeQueryRequest.setPageSize((int) count);

        //设置返回字段
        Map fieldsObject = new HashMap();
        fieldsObject.put("_id", true);
        fieldsObject.put("parentId",true);
        fieldsObject.put("groupId",true);
        fieldsObject.put("tradeState.createTime", true);
        fieldsObject.put("buyer.name", true);
        fieldsObject.put("buyer.account", true);
        fieldsObject.put("buyer.levelName", true);
        fieldsObject.put("consignee.name", true);
        fieldsObject.put("consignee.phone", true);
        fieldsObject.put("consignee.detailAddress", true);
        fieldsObject.put("payInfo.desc", true);
        fieldsObject.put("deliverWay", true);
        fieldsObject.put("tradePrice.deliveryPrice", true);
        fieldsObject.put("tradePrice.goodsPrice", true);
        fieldsObject.put("tradePrice.special", true);
        fieldsObject.put("tradePrice.privilegePrice", true);
        fieldsObject.put("tradePrice.totalPrice", true);
        fieldsObject.put("tradeItems.oid", true);
        fieldsObject.put("tradeItems.skuId", true);
        fieldsObject.put("tradeItems.skuNo", true);
        fieldsObject.put("tradeItems.num", true);
        fieldsObject.put("buyerRemark", true);
        fieldsObject.put("sellerRemark", true);
        fieldsObject.put("tradeState.flowState", true);
        fieldsObject.put("tradeState.payState", true);
        fieldsObject.put("tradeState.deliverStatus", true);
        fieldsObject.put("invoice.type", true);
        fieldsObject.put("invoice.projectName", true);
        fieldsObject.put("invoice.generalInvoice.title", true);
        fieldsObject.put("invoice.specialInvoice.companyName", true);
        fieldsObject.put("supplier.supplierName", true);
        //增加返回赠品信息
        fieldsObject.put("gifts.oid", true);
        fieldsObject.put("gifts.skuId", true);
        fieldsObject.put("gifts.skuNo", true);
        fieldsObject.put("gifts.num", true);
        //优惠券信息
        //券码id
        fieldsObject.put("tradeCoupon.couponCodeId", true);
        fieldsObject.put("tradeCoupon.couponCode", true);
        fieldsObject.put("tradeCoupon.discountsAmount", true);
        fieldsObject.put("buyer", true);
        Query query = new BasicQuery(new Document(), new Document(fieldsObject));
        query.addCriteria(tradeQueryRequest.getWhereCriteria());
        System.err.println("mongo：  " + LocalDateTime.now());
        List<PileTrade> tradeList = mongoTemplate.find(query.with(tradeQueryRequest.getPageRequest()), PileTrade.class);

        System.err.println("mongo：  " + LocalDateTime.now());
        return tradeList;
    }

    /**
     * 提交积分订单
     *
     * @param trade 积分订单
     * @return 订单提交结果
     */
    @Transactional
    public PointsTradeCommitResult createPointsCouponTrade(PileTrade trade) {
        PointsTradeCommitResult commitResult = null;

        //创建订单
        try {
            // 订单状态默认为已完成
            trade.setTradeState(TradeState
                    .builder()
                    .deliverStatus(DeliverStatus.SHIPPED)
                    .payState(PayState.PAID)
                    .flowState(FlowState.COMPLETED)
                    .createTime(LocalDateTime.now())
                    .build());
            // 订单入库
            addTrade(trade);

            commitResult = new PointsTradeCommitResult(trade.getId(), trade.getTradePrice().getPoints());
        } catch (Exception e) {
            log.error("commit points coupon trade error,trade={}", trade, e);
            if (e instanceof SbcRuntimeException) {
                throw e;
            } else {
                throw new SbcRuntimeException("K-020010");
            }
        }
        return commitResult;
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
     * 判断商品是否企业购商品
     *
     * @param enterpriseAuditState
     * @return
     */
    private boolean isEnjoyIepGoodsInfo(EnterpriseAuditState enterpriseAuditState) {
        return !Objects.isNull(enterpriseAuditState)
                && enterpriseAuditState == EnterpriseAuditState.CHECKED;
    }

    /**
     * 功能描述: WareHouseVO转TradeWareHouse
     * 〈〉
     */
    private TradeWareHouse wareHouseTurnToTrade(WareHouseVO wareHouseVO) {
        if (null != wareHouseVO) {
            return KsBeanUtil.convert(wareHouseVO, TradeWareHouse.class);
        }
        return null;
    }

    private LogisticsInfo toLogisticsInfo(LogisticsInfoDTO logisticsInfoDTO) {
        if (null != logisticsInfoDTO) {
            return KsBeanUtil.convert(logisticsInfoDTO, LogisticsInfo.class);
        }
        return null;
    }

    /**
     * 根据供货商拆单并入库
     *
     * @param trade
     */
    private void splitProvideTrade(Trade trade) {
        List<TradeItem> tradeItemList = trade.getTradeItems();

        // 订单商品id集合
        List<String> goodsInfoIdList = tradeItemList.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        BaseResponse<GoodsInfoListByIdsResponse> listByIdsResponse =
                goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(goodsInfoIdList).build());
        List<GoodsInfoVO> goodsInfoVOList = listByIdsResponse.getContext().getGoodsInfos();
        tradeItemList.forEach(tradeItem -> goodsInfoVOList.forEach(goodsInfoVO -> {
            if (tradeItem.getSkuId().equals(goodsInfoVO.getGoodsInfoId())) {
                // tradeItem设置供应商id
                tradeItem.setProviderId(goodsInfoVO.getProviderId());
                // 供货价
                tradeItem.setSupplyPrice(goodsInfoVO.getSupplyPrice());
                BigDecimal supplyPrice = Objects.nonNull(goodsInfoVO.getSupplyPrice()) ?
                        goodsInfoVO.getSupplyPrice() : BigDecimal.ZERO;
                // 供货价总额
                tradeItem.setTotalSupplyPrice(supplyPrice.multiply(new BigDecimal(tradeItem.getNum())));
            }
        }));

        // 查询订单商品所属供应商id集合
        List<Long> providerIds = goodsInfoVOList.stream().filter(
                        goodsInfoVO -> Objects.nonNull(goodsInfoVO.getProviderId()))
                .map(GoodsInfoVO::getProviderId).distinct().collect(Collectors.toList());

        // 判断是否有供应商id，有则需要根据供应商拆单
        if (CollectionUtils.isNotEmpty(providerIds)) {
            // 1. 商户自己的商品信息，单独作为一个拆单项保存
            List<TradeItem> storeItemList =
                    tradeItemList.stream().filter(tradeItem -> Objects.isNull(tradeItem.getProviderId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeItemList)) {
                ProviderTrade storeTrade = KsBeanUtil.convert(trade, ProviderTrade.class);
                // 用经营商户订单id作为供应商订单的父id
                storeTrade.setParentId(trade.getId());
                storeTrade.setId(generatorService.generateProviderTid());
                storeTrade.setTradeItems(storeItemList);

                // 拆单后，重新计算价格信息
                TradePrice tradePrice = storeTrade.getTradePrice();
                // 商品总价
                BigDecimal goodsPrice = BigDecimal.ZERO;
                // 订单总价:实付金额
                BigDecimal orderPrice = BigDecimal.ZERO;
                // 订单供货价总额
                BigDecimal orderSupplyPrice = BigDecimal.ZERO;
                for (TradeItem providerTradeItem : storeItemList) {
                    // 商品总价
                    goodsPrice = goodsPrice.add(providerTradeItem.getPrice().multiply(new BigDecimal(providerTradeItem.getNum())));
                    // 商品分摊价格
                    BigDecimal splitPrice = Objects.isNull(providerTradeItem.getSplitPrice()) ? BigDecimal.ZERO :
                            providerTradeItem.getSplitPrice();
                    // 订单总价:用分摊金额乘以数量，计算订单实际价格
                    orderPrice = orderPrice.add(splitPrice.multiply(new BigDecimal(providerTradeItem.getNum())));
                    // 订单供货价总额
                    orderSupplyPrice = orderSupplyPrice.add(providerTradeItem.getTotalSupplyPrice());
                }
                // 商品总价
                tradePrice.setGoodsPrice(goodsPrice);
                tradePrice.setOriginPrice(goodsPrice);
                // 订单总价
                tradePrice.setTotalPrice(orderPrice);
                tradePrice.setTotalPayCash(orderPrice);
                // 订单供货价总额
                tradePrice.setOrderSupplyPrice(orderSupplyPrice);

                storeTrade.setTradePrice(tradePrice);

                providerTradeService.addProviderTrade(storeTrade);
            }

            // 查询供货商店铺信息
            BaseResponse<ListNoDeleteStoreByIdsResponse> storesResposne =
                    storeQueryProvider.listNoDeleteStoreByIds(ListNoDeleteStoreByIdsRequest.builder().storeIds(providerIds).build());
            List<StoreVO> storeVOList = storesResposne.getContext().getStoreVOList();

            // 2. 根据供货商id拆单
            providerIds.forEach(providerId -> {
                ProviderTrade providerTrade = KsBeanUtil.convert(trade, ProviderTrade.class);

                // 用经营商户订单id作为供应商订单的父id
                providerTrade.setParentId(trade.getId());
                providerTrade.setId(generatorService.generateProviderTid());
                // 筛选当前供应商的订单商品信息
                List<TradeItem> providerTradeItems =
                        tradeItemList.stream().filter(tradeItem -> providerId.equals(tradeItem.getProviderId())).collect(Collectors.toList());

                providerTrade.setTradeItems(providerTradeItems);
                // 原订单所属商家名称
                providerTrade.setSupplierName(trade.getSupplier().getSupplierName());
                // 原订单所属商家编号
                providerTrade.setSupplierCode(trade.getSupplier().getSupplierCode());
                // 原订单所属商户id
                providerTrade.setStoreId(trade.getSupplier().getStoreId());
                Supplier supplier = providerTrade.getSupplier();

                // 供应商信息
                StoreVO provider =
                        storeVOList.stream().filter(store -> store.getStoreId().equals(providerId)).findFirst().get();
                // 保存供应商店铺信息
                supplier.setStoreId(provider.getStoreId());
                supplier.setSupplierName(provider.getSupplierName());
                supplier.setSupplierId(provider.getCompanyInfo().getCompanyInfoId());
                supplier.setSupplierCode(provider.getCompanyInfo().getCompanyCode());
                // 使用的运费模板类别(0:店铺运费,1:单品运费)
                supplier.setFreightTemplateType(provider.getFreightTemplateType());
                // providerTrade中supplier对象更新为供应商信息
                providerTrade.setSupplier(supplier);

                // 拆单后，重新计算价格信息
                TradePrice tradePrice = providerTrade.getTradePrice();
                // 商品总价
                BigDecimal goodsPrice = BigDecimal.ZERO;
                // 订单总价:实付金额
                BigDecimal orderPrice = BigDecimal.ZERO;
                // 订单供货价总额
                BigDecimal orderSupplyPrice = BigDecimal.ZERO;
                for (TradeItem providerTradeItem : providerTradeItems) {
                    // 商品总价
                    goodsPrice = goodsPrice.add(providerTradeItem.getPrice().multiply(new BigDecimal(providerTradeItem.getNum())));
                    // 商品分摊价格
                    BigDecimal splitPrice = Objects.isNull(providerTradeItem.getSplitPrice()) ? BigDecimal.ZERO :
                            providerTradeItem.getSplitPrice();
                    // 订单总价:用分摊金额乘以数量，计算订单实际价格
                    orderPrice = orderPrice.add(splitPrice.multiply(new BigDecimal(providerTradeItem.getNum())));
                    // 订单供货价总额
                    orderSupplyPrice = orderSupplyPrice.add(providerTradeItem.getTotalSupplyPrice());
                    // 供应商名称
                    providerTradeItem.setProviderName(provider.getSupplierName());
                    // 供应商编号
                    providerTradeItem.setProviderCode(provider.getCompanyInfo().getCompanyCode());
                }

                // 商品总价
                tradePrice.setGoodsPrice(goodsPrice);
                tradePrice.setOriginPrice(goodsPrice);
                // 订单总价
                tradePrice.setTotalPrice(orderPrice);
                tradePrice.setTotalPayCash(orderPrice);
                // 订单供货价总额
                tradePrice.setOrderSupplyPrice(orderSupplyPrice);

                providerTrade.setTradePrice(tradePrice);
                providerTradeService.addProviderTrade(providerTrade);
            });
        }
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
    private Boolean pushCancelOrder(Trade trade) {
        if(!orderCommonService.wmsCanTrade(trade)){
            return true;
        }
        BaseResponse<ResponseWMSReturnResponse> result = null;
        try {
            result = requestWMSOrderProvider.cancelOrder(WMSOrderCancelRequest.builder()
                    .docNo(trade.getId())
                    .customerId("XYY")
                    .warehouseId(trade.getWareHouseCode())
                    .orderType("XSCK")
                    .erpCancelReason("不想要了！！")
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
     * 功能描述: <br>调用第三方WMS接口
     * 〈〉
     *
     * @Param: [returnOrder, warehouseId]
     * @Return: java.lang.Boolean
     * @Author:
     * @Date: 2020/5/19 15:12
     */
    public Boolean pushConfirmOrder(Trade trade) {
        BaseResponse<ResponseWMSReturnResponse> result = null;
        try {
            result = requestWMSOrderProvider.confirmSalesOrder(WMSOrderCancelRequest.builder()
                    .customerId("XYY")
                    .docNo(trade.getId())
                    .orderType("XSCK")
                    .warehouseId(trade.getWareHouseCode())
                    .erpCancelReason("不想要了！！")
                    .build());
        } catch (Exception e) {
            return false;
        }
        return !Objects.isNull(result) && !Objects.isNull(result.getContext()) &&
                !Objects.isNull(result.getContext().getResponseWMSReturnVO()) &&
                AbstractXYYConstant.RESPONSE_SUCCESS.equals(result.getContext().getResponseWMSReturnVO().getReturnCode()) &&
                Objects.nonNull(result.getContext().getResponseWMSReturnVO().getReturnFlag()) &&
                result.getContext().getResponseWMSReturnVO().getReturnFlag() > 0;
    }

    /**
     * 功能描述: <br> 订单推送WMS
     * 〈〉
     *
     * @Param: [trade, warehouseId]
     * @Return: java.lang.Boolean
     * @Date: 2020/5/18 11:24
     */
    public void pushWMSOrder(PileTrade trade, Boolean hasGroupedFlag) {
        if(!orderCommonService.wmsCanPileTrade(trade)){
            return;
        }
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
        wmsPushOrderRequest.setUserDefine3(noLimitFlag ? "0" : "1");
        ResponseWMSReturnResponse response = requestWMSOrderProvider.putSalesOrder(wmsPushOrderRequest).getContext();
        if (Objects.isNull(response)
                || Objects.isNull(response.getResponseWMSReturnVO())
                || Objects.isNull(response.getResponseWMSReturnVO().getReturnFlag())
                || response.getResponseWMSReturnVO().getReturnFlag() < 1) {
            throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_TIME_OUT);
        }
    }


    /**
     * 根据推送状态获取订单
     *
     * @return List<Trade>
     */
    public List<TradeVO> queryConfrimFailedTrades() {
        List<PileTrade> list = pileTradeRepository.findListByWMSPushFlag(false);
        return KsBeanUtil.convert(list, TradeVO.class);
    }

    public void sendPickUpMessage(PileTrade trade) {
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

    public void sendPickUpSuccessMessage(PileTrade trade) {
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("type", NodeType.ORDER_PROGRESS_RATE.toValue());
        map.put("id", trade.getId());
        messageMQRequest.setNodeCode(OrderProcessType.CUSTOMER_PICK_UP_RECEIVE.getType());
        map.put("node", OrderProcessType.CUSTOMER_PICK_UP_RECEIVE.toValue());
        messageMQRequest.setNodeType(NodeType.ORDER_PROGRESS_RATE.toValue());
        messageMQRequest.setParams(Collections.singletonList(trade.getId()));
        messageMQRequest.setPic(trade.getTradeItems().get(0).getPic());
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(trade.getBuyer().getId());
        messageMQRequest.setMobile(trade.getConsignee().getPhone());
        orderProducerService.sendMessage(messageMQRequest);
    }


    @Transactional
    public void updateTradeLogisticsCompany(String tid, String areaInfo, Long companyId, Operator operator) {
        LogisticsCompanyByIdResponse context = null;
        if (Objects.nonNull(companyId)) {
            context = logisticsCompanyQueryProvider.getById(LogisticsCompanyByIdRequest.builder().id(companyId).build()).getContext();
            if (Objects.isNull(context) || Objects.isNull(context.getLogisticsCompanyVO())) {
                throw new SbcRuntimeException("K-170001");
            }
        }

        PileTrade trade = detail(tid);
        if (Objects.isNull(trade)) {
            throw new SbcRuntimeException("K-050100", new Object[]{tid});
        }
        if (Objects.isNull(trade.getTradeState())
                || !AuditState.NON_CHECKED.equals(trade.getTradeState().getAuditState())) {
            throw new SbcRuntimeException("K-180001");
        }
        if (Objects.isNull(companyId)) {
            if (StringUtils.isNotBlank(areaInfo)) {
                LogisticsInfo tradeCompany = new LogisticsInfo();
                tradeCompany.setReceivingPoint(areaInfo);
                trade.setLogisticsCompanyInfo(tradeCompany);
            }
        } else {
            LogisticsCompanyVO logisticsCompanyVO = context.getLogisticsCompanyVO();
            if (Objects.nonNull(trade.getLogisticsCompanyInfo())) {
                LogisticsInfo tradeCompany = trade.getLogisticsCompanyInfo();
                tradeCompany.setId(logisticsCompanyVO.getId().toString());
                tradeCompany.setCompanyNumber(logisticsCompanyVO.getCompanyNumber());
                tradeCompany.setLogisticsAddress(logisticsCompanyVO.getLogisticsAddress());
                tradeCompany.setLogisticsCompanyName(logisticsCompanyVO.getLogisticsName());
                tradeCompany.setLogisticsCompanyPhone(logisticsCompanyVO.getLogisticsPhone());
                if (StringUtils.isNotBlank(areaInfo)) {
                    tradeCompany.setReceivingPoint(areaInfo);
                }
            } else {
                LogisticsInfo tradeCompany = new LogisticsInfo();
                tradeCompany.setId(logisticsCompanyVO.getId().toString());
                tradeCompany.setCompanyNumber(logisticsCompanyVO.getCompanyNumber());
                tradeCompany.setLogisticsAddress(logisticsCompanyVO.getLogisticsAddress());
                tradeCompany.setLogisticsCompanyName(logisticsCompanyVO.getLogisticsName());
                tradeCompany.setLogisticsCompanyPhone(logisticsCompanyVO.getLogisticsPhone());
                if (StringUtils.isNotBlank(areaInfo)) {
                    tradeCompany.setReceivingPoint(areaInfo);
                }
                trade.setLogisticsCompanyInfo(tradeCompany);
            }
        }

        trade.appendTradeEventLog(new TradeEventLog(operator, "修改物流公司", "修改物流公司信息", LocalDateTime.now()));
        updateTrade(trade);
        this.operationLogMq.convertAndSend(operator, "修改物流公司", "修改物流公司信息");
    }

    /**
     * 根据物流公司id查询订单存在哪些符合条件的物流公司
     *
     * @param id
     * @return
     */
    public BaseResponse<TradeListAllResponse> findListByByLogisticsCompanyId(Long id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("logisticsCompanyInfo.id").is(String.valueOf(id)));
        query.addCriteria(Criteria.where("tradeState.flowState").nin(FlowState.VOID.getStateId(),
                FlowState.COMPLETED.getStateId()));

        List<Trade> trades = mongoTemplate.find(query, Trade.class);

        return BaseResponse.success(TradeListAllResponse.builder().tradeVOList(KsBeanUtil.convertList(trades,
                TradeVO.class)).build());
    }

    /**
     * 根据会员id查询返回最新下单对应的一个公司
     *
     * @param id
     * @return
     */
    public BaseResponse<HistoryLogisticsCompanyByCustomerIdResponse> getByCustomerId(String id) {
        return BaseResponse.success(HistoryLogisticsCompanyByCustomerIdResponse.builder().
                historyLogisticsCompanyVO(KsBeanUtil.convert(historyLogisticsCompanyService.findByCustomerId(id), HistoryLogisticsCompanyVO.class)).build());
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
    private void checkLogsitcCompanyInfo(List<StoreCommitInfoDTO> storeCommitInfoDTOList) {
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

    /**
     * 检验wms 取消是否符合要求
     */

    public BaseResponse checkWms(Trade trade) {
        //推送给wms取消退单
        if (wmsAPIFlag) {
            //拼团订单取消拼团时不需要推送
            if (!(Objects.nonNull(trade.getGrouponFlag()) && trade.getGrouponFlag())) {
                if (!pushCancelOrder(trade)) {
                    throw new SbcRuntimeException(OrderErrorCode.CANCEL_ORDER_FAILD_IN_WMS, "订单取消失败，仓库已拣货！");
                }
            }
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 支付回调处理，将原有逻辑迁移到order处理
     * @Date 14:56 2020/7/2
     * @Param [tradePayOnlineCallBackRequest]
     **/
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
                PileTrade trade = new PileTrade();
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
                        List<PileTrade> trades = new ArrayList<>();
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
                                    trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
                            if (cancel || (paid && !recordResponse.getTradeNo().equals(wxPayResultResponse.getTransaction_id()))) {
                                //同一批订单重复支付或过期作废，直接退款
                                wxRefundHandle(wxPayResultResponse, businessId, -1L);
                            } else if (payCallBackResult.getResultStatus() != PayCallBackResultStatus.SUCCESS) {
                                wxPayCallbackHandle(payGatewayConfig, wxPayResultResponse, businessId, trades, true);
                            }
                        } else {
                            //单笔支付
                            PileTrade trade = new PileTrade();
                            if (isTailPayOrder(businessId)) {
                                trade =
                                        queryAll(TradeQueryRequest.builder().tailOrderNo(businessId).build()).get(0);
                            } else {
                                trade = detail(businessId);
                            }
                            if (trade.getTradeState().getFlowState() == FlowState.VOID || (trade.getTradeState()
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
     * 钱包充值WX回调
     *
     * @param tradePayOnlineCallBackRequest
     * @throws Exception
     */
    @LcnTransaction
    @Transactional
    public void wxPayRechargeOnlineCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) throws Exception {
        String businessId = "";
        try {
            PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                    GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, Constants.BOSS_DEFAULT_STORE_ID)).getContext();
            String apiKey = payGatewayConfig.getApiKey();
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayResultResponse.class);
            WxPayResultResponse wxPayResultResponse =
                    (WxPayResultResponse) xStream.fromXML(tradePayOnlineCallBackRequest.getWxPayCallBackResultStr());
            log.info("-------------微信充值支付回调,wxPayRechargeOnlineCallBack：{}------------", wxPayResultResponse);
            //判断当前回调是否是合并支付
            businessId = wxPayResultResponse.getOut_trade_no();
            String lockName;
            //非组合支付，则查出该单笔订单。
            BaseResponse<WalletRecordResponse> response = walletRecordProvider.queryWalletRecord(new QueryWalletRecordRequest().builder().recordNo(businessId).build());
            //这笔订单信息
            List<WalletRecordVO> walletRecordVOs = response.getContext().getWalletRecordVOs();
            Integer tradeState = walletRecordVOs.get(0).getTradeState().toValue();
            lockName = walletRecordVOs.get(0).getRecordNo();
            //redis锁，防止同一订单重复回调
            RLock rLock = redissonClient.getFairLock(lockName);
            rLock.lock();
            //执行回调
            try {
                //已支付
                if (tradeState.equals(1)) {
                    log.info("=======================订单号:{} 已是支付状态,无需进行其他操作=======================", lockName);
                    return;
                }
                //支付回调事件成功
                if (wxPayResultResponse.getReturn_code().equals(WXPayConstants.SUCCESS) &&
                        wxPayResultResponse.getResult_code().equals(WXPayConstants.SUCCESS)) {
                    log.info("微信支付充值异步通知回调状态---成功");
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
                        wxPayRechargeCallbackHandle(payGatewayConfig, wxPayResultResponse, businessId, walletRecordVOs.get(0), false);
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
     * 是否是尾款订单号
     *
     * @param businessId
     * @return
     */
    private boolean isTailPayOrder(String businessId) {
        return businessId.startsWith(GeneratorService._PREFIX_TRADE_TAIL_ID);
    }

    /**
     * @return boolean
     * @Author lvzhenwei
     * @Description 判断是否为主订单
     * @Date 15:36 2020/7/2
     * @Param [businessId]
     **/
    private boolean isMergePayOrder(String businessId) {
        log.info("============================= isMergePayOrder.businessId:{}============================",businessId);
        return businessId.startsWith(GeneratorService._PREFIX_PARENT_TRADE_ID);
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
                                     String businessId, List<PileTrade> trades, boolean isMergePay) {
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

        /**
         * 修改订单金额（新增余额支付金额）
         */
        doWallet(trades, payTradeRecordRequest);

        log.info("payTrades============= {}", trades);
        payCallbackOnline(trades, operator, isMergePay);
    }

    /**
     *  修改订单金额（新增余额支付金额）
     */
    private void doWallet(List<PileTrade> trades, PayTradeRecordRequest payTradeRecordRequest){
        BigDecimal pTradeTotalPrice = trades.stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                .stream().map(p -> p.getTotalPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        if(pTradeTotalPrice.compareTo(payTradeRecordRequest.getPracticalPrice()) == 1){
            //订单在线累计金额
            AtomicReference<BigDecimal> divideTradeTotalPrice = new AtomicReference<>(BigDecimal.ZERO);
            //余额支付总金额
            BigDecimal balanceTradeTotalPrice = pTradeTotalPrice.subtract(payTradeRecordRequest.getPracticalPrice());
            //余额支付累计金额
            AtomicReference<BigDecimal> cumulativeTradeTotalPrice = new AtomicReference<>(BigDecimal.ZERO);
            PileTrade pileTrade = trades.stream().reduce((f, s) -> s).orElse(null);

            BalanceByCustomerIdResponse context = customerWalletQueryProvider.getBalanceByCustomerId(
                    WalletByCustomerIdQueryRequest.builder().customerId(trades.get(0).getBuyer().getId()).build())
                    .getContext();

            trades.forEach(t->{
                if(t.equals(pileTrade)){
                    t.getTradePrice().setOnlinePrice(payTradeRecordRequest.getPracticalPrice().subtract(divideTradeTotalPrice.get()));
                    t.getTradePrice().setBalancePrice(balanceTradeTotalPrice.subtract(cumulativeTradeTotalPrice.get()));

                    if(Objects.nonNull(context) && Objects.nonNull(context.getCustomerWalletVO())){
                        doCreateWalletRecord(t, context.getCustomerWalletVO().getBalance().subtract(balanceTradeTotalPrice));
                    }

                }else{
                    BigDecimal divide = t.getTradePrice().getTotalPrice().divide(pTradeTotalPrice, 2, BigDecimal.ROUND_DOWN);
                    //订单在线支付分摊金额
                    BigDecimal divideTradePirce = payTradeRecordRequest.getPracticalPrice().multiply(divide).setScale(2, BigDecimal.ROUND_DOWN);
                    divideTradeTotalPrice.get().add(divideTradePirce);
                    t.getTradePrice().setOnlinePrice(divideTradePirce);
                    //余额支付金额
                    BigDecimal balance = t.getTradePrice().getTotalPrice().subtract(divideTradePirce);
                    cumulativeTradeTotalPrice.get().add(balance);
                    t.getTradePrice().setBalancePrice(balance);

                    if(Objects.nonNull(context) && Objects.nonNull(context.getCustomerWalletVO())){
                        doCreateWalletRecord(t, context.getCustomerWalletVO().getBalance().subtract(cumulativeTradeTotalPrice.get()));
                    }
                }
            });

            //新增余额支付记录，修改账户余额
            if(Objects.nonNull(context) && Objects.nonNull(context.getCustomerWalletVO())){
                CustomerWalletVO customerWalletVO = context.getCustomerWalletVO();
                customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(balanceTradeTotalPrice));
                customerWalletProvider.updateCustomerWalletByWalletId(CustomerWalletModifyRequest.builder().customerWalletVO(customerWalletVO).build());
            }
        }

    }

    private void doCreateWalletRecord(PileTrade t, BigDecimal currentBalance){
        walletRecordProvider.addWalletRecord(AddWalletRecordRecordRequest.builder()
                .relationOrderId(t.getId())
                //暂时没有
                .blockGiveBalance(BigDecimal.ZERO)
                .budgetType(BudgetType.EXPENDITURE)
                .chargePrice(BigDecimal.ZERO)
                .currentBalance(currentBalance)
                .customerAccount(t.getBuyer().getAccount())
                .customerId(t.getBuyer().getId())
                .dealPrice(t.getTradePrice().getBalancePrice())
                .tradeType(WalletRecordTradeType.BALANCE_PAY)
                .tradeState(TradeStateEnum.PAID)
                .tradeRemark(t.getBuyerRemark())
                .remark("余额支付：" + t.getTradePrice().getBalancePrice() + "元！")
                .build());
    }

    private void wxPayRechargeCallbackHandle(PayGatewayConfigResponse payGatewayConfig, WxPayResultResponse wxPayResultResponse,
                                             String businessId, WalletRecordVO vo, boolean isMergePay) {
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
        payRechargeCallbackOnline(vo, wxPayResultResponse);
    }

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 支付回调处理，将原有逻辑迁移到order处理
     * @Date 14:56 2020/7/2
     * @Param [tradePayOnlineCallBackRequest]
     **/
    @Transactional
    @LcnTransaction
    public void aliPayOnlineCallBack(TradePayOnlineCallBackRequest tradePayOnlineCallBackRequest) throws IOException {
        log.info("===============支付宝回调开始==============");
        GatewayConfigByGatewayRequest gatewayConfigByGatewayRequest = new GatewayConfigByGatewayRequest();
        gatewayConfigByGatewayRequest.setGatewayEnum(PayGatewayEnum.ALIPAY);
        gatewayConfigByGatewayRequest.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        //查询支付宝配置信息
        PayGatewayConfigResponse payGatewayConfigResponse =
                payQueryProvider.getGatewayConfigByGateway(gatewayConfigByGatewayRequest).getContext();
        //支付宝公钥
        String aliPayPublicKey = payGatewayConfigResponse.getPublicKey();
        boolean signVerified = false;
        Map<String, String> params =
                JSONObject.parseObject(tradePayOnlineCallBackRequest.getAliPayCallBackResultStr(), Map.class);
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, aliPayPublicKey, "UTF-8", "RSA2"); //调用SDK验证签名
        } catch (AlipayApiException e) {
            log.error("支付宝回调签名校验异常：", e);
        }
        //商户订单号
        String out_trade_no = params.get("out_trade_no");
        if (signVerified) {
            try {
                //支付宝交易号
                String trade_no = params.get("trade_no");
                //交易状态
                String trade_status = params.get("trade_status");
                //订单金额
                String total_amount = params.get("total_amount");
                //支付终端类型
                String type = params.get("passback_params");

                boolean isMergePay = isMergePayOrder(out_trade_no);
                log.info("-------------支付回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}------------",
                        out_trade_no, trade_no, trade_status, total_amount, isMergePay);
                String lockName;
                //非组合支付，则查出该单笔订单。
                //非组合支付，则查出该单笔订单。
                if (!isMergePay) {
                    PileTrade trade = new PileTrade();
                    if (isTailPayOrder(out_trade_no)) {
                        trade = queryAll(TradeQueryRequest.builder().tailOrderNo(out_trade_no).build()).get(0);
                    } else {
                        trade = detail(out_trade_no);
                    }
                    // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                    lockName = trade.getParentId();
                } else {
                    lockName = out_trade_no;
                }
                Operator operator =
                        Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                                .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
                //redis锁，防止同一订单重复回调
                RLock rLock = redissonClient.getFairLock(lockName);
                rLock.lock();
                //执行
                try {
                    List<PileTrade> trades = new ArrayList<>();
                    //查询交易记录
                    TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                            new TradeRecordByOrderCodeRequest(out_trade_no);
                    PayTradeRecordResponse recordResponse =
                            payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                    PayCallBackResult payCallBackResult =
                            payCallBackResultService.list(PayCallBackResultQueryRequest.builder().businessId(out_trade_no).build()).get(0);
                    if (isMergePay) {
                        /*
                         * 合并支付
                         * 查询订单是否已支付或过期作废
                         */
                        trades = detailsByParentId(out_trade_no);
                        //订单合并支付场景状态采样
                        boolean paid =
                                trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);
                        boolean cancel =
                                trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
                        //订单的支付渠道。17、18、19是我们自己对
                        //   接的支付宝渠道， 表：pay_channel_item
                        if (cancel || (paid && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                != 18L && recordResponse.getChannelItemId() != 19L)) {
                            //重复支付，直接退款
                            alipayRefundHandle(out_trade_no, total_amount);
                        } else if (payCallBackResult.getResultStatus() != PayCallBackResultStatus.SUCCESS) {
                            alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                    operator, trades, true, recordResponse);
                        }
                    } else {
                        //单笔支付
                        //单笔支付
                        PileTrade trade = new PileTrade();
                        if (isTailPayOrder(out_trade_no)) {
                            trade = queryAll(TradeQueryRequest.builder().tailOrderNo(out_trade_no).build()).get(0);
                        } else {
                            trade = detail(out_trade_no);
                        }
                        if (trade.getTradeState().getFlowState() == FlowState.VOID || (trade.getTradeState()
                                .getPayState() == PayState.PAID && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                                != 18L && recordResponse.getChannelItemId() != 19L)) {
                            //同一批订单重复支付或过期作废，直接退款
                            alipayRefundHandle(out_trade_no, total_amount);
                        } else if (payCallBackResult.getResultStatus() != PayCallBackResultStatus.SUCCESS) {
                            trades.add(trade);
                            alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                    operator, trades, false, recordResponse);
                        }
                    }
                    payCallBackResultService.updateStatus(out_trade_no, PayCallBackResultStatus.SUCCESS);
                } finally {
                    //解锁
                    rLock.unlock();
                }
            } catch (Exception e) {
                log.error("支付宝回调异常：", e);
                payCallBackResultService.updateStatus(out_trade_no, PayCallBackResultStatus.FAILED);
            }
        }
    }

    /**
     * 支付宝退款处理
     *
     * @param out_trade_no
     * @param total_amount
     */
    private void alipayRefundHandle(String out_trade_no, String total_amount) {
        //调用退款接口。直接退款。不走退款流程，没有交易对账，只记了操作日志
        AliPayRefundResponse aliPayRefundResponse =
                aliPayProvider.aliPayRefund(AliPayRefundRequest.builder().businessId(out_trade_no)
                        .amount(new BigDecimal(total_amount)).description("重复支付退款").build()).getContext();
        log.info("支付宝重复支付、超时订单退款,单号：{}", out_trade_no);
    }

    private void alipayCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                      String type, Operator operator, List<PileTrade> trades, boolean isMergePay,
                                      PayTradeRecordResponse recordResponse) {
        if (recordResponse.getApplyPrice().compareTo(new BigDecimal(total_amount)) == 0 && trade_status.equals(
                "TRADE_SUCCESS")) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);

            /**
             * 修改订单金额（新增余额支付金额）
             */
            doWallet(trades, payTradeRecordRequest);

            payCallbackOnline(trades, operator, isMergePay);
            log.info("支付回调成功,单号：{}", out_trade_no);
        }
    }


    /**
     * 线上订单支付回调
     * 订单 支付单 操作信息
     *
     * @return 操作结果
     */
    private void payCallbackOnline(List<PileTrade> trades, Operator operator, boolean isMergePay) {
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
                backOnlineBatch.setTrade(KsBeanUtil.convert(trade,PileTrade.class));
                backOnlineBatch.setPayOrderOld(payOrder);
                return backOnlineBatch;
            } else {
                //支付单信息
                PayOrder payOrder = findPayOrder(trade.getPayOrderId());

                if ((PayWay.BALANCE).equals(trade.getPayWay())) {
                    payOrder.setPayType(PayType.BALANCER);
                }
                PayCallBackOnlineBatchPile backOnlineBatch = new PayCallBackOnlineBatchPile();
                backOnlineBatch.setTrade(KsBeanUtil.convert(trade,PileTrade.class));
                backOnlineBatch.setPayOrderOld(payOrder);
                return backOnlineBatch;
            }
        }).collect(Collectors.toList());
//        log.info("payCallBackOnlineBatchList------- {}", payCallBackOnlineBatchList);
        payCallBackOnlineBatch(payCallBackOnlineBatchList, operator);
        // 订单支付回调同步供应商订单状态
        //this.providerTradePayCallBack(trades);
    }

    /**
     * 线上订单充值支付回调
     * 订单 支付单 操作信息
     *
     * @return 操作结果
     */
    public void payRechargeCallbackOnline(WalletRecordVO walletRecordVO, WxPayResultResponse wxPayResultResponse) {
        //交易金额
        BigDecimal totalFee = new BigDecimal(wxPayResultResponse.getTotal_fee()).divide(new BigDecimal(100));
        //用户
        String customerAccount = walletRecordVO.getCustomerAccount();
        //交易id
        String recordNo = walletRecordVO.getRecordNo();
        //虚拟商品id
        Integer virtualGoodsId = walletRecordVO.getVirtualGoodsId();
        WalletByCustomerAccountQueryRequest request = WalletByCustomerAccountQueryRequest.builder().customerAccount(customerAccount).build();
        //添加对应的充值余额到充值金额中,添加对应的充值金额到余额中,获取用户钱包表
        CustomerWalletVO walletVO = customerWalletQueryProvider.getCustomerWalletByCustomerAccount(request).getContext().getCustomerWalletVO();
        //赠送优惠券
        this.giveCoupon(virtualGoodsId.longValue(),walletVO.getCustomerId());
        TicketsFormModifyRequest ticketsForm = new TicketsFormModifyRequest();
        ticketsForm.setWalletId(walletVO.getWalletId());
        ticketsForm.setVirtualGoodsId(virtualGoodsId);
        if (totalFee.compareTo(walletRecordVO.getDealPrice()) != 0) {
            ticketsForm.setApplyPrice(totalFee);
        } else {
            ticketsForm.setApplyPrice(walletRecordVO.getDealPrice());
        }
        ticketsForm.setApplyTime(LocalDateTime.now());
        ticketsForm.setRechargeStatus(2);
        ticketsForm.setRemark(new StringBuilder().append("用户：").append(customerAccount).append("充值").append(walletRecordVO.getDealPrice())
                .append("元,充值时间").append(LocalDateTime.now()).toString());
        ticketsForm.setRecordNo(recordNo);
        //新增交易记录
        ticketsFormProvider.saveTicketsForm(ticketsForm);
        QueryWalletRecordRequest build = new QueryWalletRecordRequest().builder().customerAccount(customerAccount).build();
        //判断是不是第一次充值,是的话赠送对应金额
        List<WalletRecordVO> walletRecordVOs = walletRecordProvider.queryWalletRecordByCustomerAccount(build).getContext().getWalletRecordVOs();
        //赠送的金额
        BigDecimal giveMoney = null;
        //充值的金额
        BigDecimal rechargeMoney = walletRecordVO.getDealPrice();
        //最终余额
        BigDecimal finalBalance;
        //最终充值金额
        BigDecimal finalRechargeMoney;
        //最终赠送金额
        BigDecimal finalGiveMoney;
        //根据虚拟商品id查询出虚拟商品对应的赠送金额进行赠送,给赠送金额添加余额,给赠送的金额添加金额
        List<Long> ids = new ArrayList<>();
        ids.add(Long.valueOf(virtualGoodsId));
        List<VirtualGoodsVO> goodsList = virtualGoodsQueryProvider.getVirtualGoodsList(VirtualGoodsRequest.builder().goodsIdList(ids).build()).getContext().getVirtualGoodsList();
        VirtualGoodsVO virtualGoods = null;
        if (CollectionUtils.isNotEmpty(goodsList)) {
            virtualGoods = goodsList.get(0);
        }
        BigDecimal givePrice = virtualGoods.getGivePrice();
        //判断是否首充赠送
        if (virtualGoods.getFirstSendFlag().equals(0)) {
            //是首充才送
            //首次充值,赠送礼包
            if (CollectionUtils.isEmpty(walletRecordVOs)) {
                //判断是否首充赠送
                if (Objects.nonNull(givePrice)) {
                    giveMoney = givePrice;
                }
            }
        } else {
            //判断是否首充赠送
            if (Objects.nonNull(givePrice)) {
                giveMoney = givePrice;
            }
        }
        //钱包不为空进行添加余额,添加充值金额,添加赠送金额
        if (Objects.nonNull(walletVO)) {
            //余额
            BigDecimal balanceOld = walletVO.getBalance();
            //余额添加充值金额
            BigDecimal balance = balanceOld.add(rechargeMoney);
            //赠送金额不为空,余额添加赠送金额
            if (Objects.nonNull(giveMoney)) {
                finalBalance = balance.add(giveMoney);
            } else {
                //充值金额为空,余额等于充值金额加当前余额
                finalBalance = balance;
            }
            //最终余额
            walletVO.setBalance(finalBalance);
            //充值金额
            BigDecimal rechargeBalance = walletVO.getRechargeBalance();
            if (Objects.nonNull(rechargeBalance)) {
                finalRechargeMoney = rechargeMoney.add(rechargeBalance);
            } else {
                finalRechargeMoney = rechargeMoney;
            }
            //最终充值金额
            walletVO.setRechargeBalance(finalRechargeMoney);
            //赠送金额
            BigDecimal giveBalance = walletVO.getGiveBalance();
            //本次赠送金额不为空
            if (Objects.nonNull(giveMoney)) {
                //以往赠送金额不为空
                if (Objects.nonNull(giveBalance)) {
                    finalGiveMoney = giveBalance.add(giveMoney);
                } else {
                    finalGiveMoney = giveMoney;
                }
                //赠送金额
                walletVO.setGiveBalance(finalGiveMoney);
            }
            CustomerWalletModifyRequest modifyRequest = new CustomerWalletModifyRequest().builder().customerWalletVO(walletVO).build();
            customerWalletProvider.updateCustomerWalletByWalletId(modifyRequest);
        }
    }

    /**
     * 赠送优惠券
     * @param virtualGoodsId
     */
    public List<GetCouponGroupResponse> giveCoupon(Long virtualGoodsId,String customerId) {
        VirtualGoodsVO virtualGoods = virtualGoodsQueryProvider.getVirtualGoods(VirtualGoodsRequest.builder().goodsId(virtualGoodsId).build()).getContext().getVirtualGoods();
        String activityId = virtualGoods.getActivityId();
        //当活动id不等于空的时候，赠送优惠券
        if(StringUtils.isNotEmpty(activityId)){
            List<GetCouponGroupResponse> couponList = couponActivityProvider.giveRechargeCoupon(SendCouponRechargeRequest.builder().activityId(activityId).customerId(customerId).build()).getContext().getCouponList();
            return couponList;
        }
        return null;
    }

    public static void main(String[] args) {
        BigDecimal decimal = new BigDecimal(12);
        BigDecimal d;
        d = decimal;
        System.out.println(d);
    }

    /**
     * 查询订单状态是已下单的近30天订单数据
     *
     * @return List<Trade>
     */
    public List<OrderSalesRanking> querySalesRanking() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime justTime = now.minus(30, ChronoUnit.DAYS);
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("tradeState.createTime").gte(justTime)
                , Criteria.where("tradeState.payState").is("PAID")
        );
        Query query = new Query(criteria);
        List<PileTrade> providerTrades = mongoTemplate.find(query, PileTrade.class);
        List<OrderSalesRanking> orderSalesRankings = Lists.newArrayList();
        for (PileTrade providerTrade : providerTrades) {
            List<TradeItem> tradeItems = providerTrade.getTradeItems();
            for (TradeItem tradeItem : tradeItems) {
                String skuId = tradeItem.getSkuId();
                String spuId = tradeItem.getSpuId();
                Long cateId = tradeItem.getCateId();
                String cateName = tradeItem.getCateName();
                OrderSalesRanking build = OrderSalesRanking.builder().skuId(skuId).spuId(spuId).cateId(cateId).cateName(cateName).build();
                orderSalesRankings.add(build);
            }
        }
        return orderSalesRankings;
    }

    /**
     * 获取客户最近支付时间
     *
     * @return
     */
    public List<PileTrade> getCustomerLastPayOrderTime(String customerId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("tradeState.payState").is("PAID"),
                Criteria.where("buyer._id").is(customerId));

        Query query = new Query(criteria);
        query.limit(1);
        query.with(Sort.by(Sort.Order.desc("tradeState.createTime")));
        List<PileTrade> tradeList = mongoTemplate.find(query, PileTrade.class);
        return tradeList;
    }

    /**
     * 给订单加上组合支付标识
     * @param request
     */
    public void addMergePay(TradeAddMergePayRequest request){
        if (Objects.nonNull(request)){
            logger.info("TradeService.addMergePay tId:{} isMergePay:{}",request.getTid(),request.isMergePay());
            PileTrade trade = detail(request.getTid());
            if (Objects.nonNull(trade)) {
                logger.info("TradeService.addMergePay addMergePay");
                trade.getPayInfo().setMergePay(request.isMergePay());
                updateTrade(trade);
            }
        }
    }

    @Autowired
    private PilePurchaseActionRepository pilePurchaseActionRepository;

    /**
     * 同步囤货订单总金额及优惠后商品单价
     */
    public void syncPileOrderPrice(){
        LocalDateTime startTime = LocalDateTime.now();

        List<PilePurchaseAction> pilePurchaseActionList = pilePurchaseActionRepository.getPilePurchaseActions();
        pilePurchaseActionList.forEach(pilePurchaseAction -> {
            if(Objects.nonNull(pilePurchaseAction.getOrderCode())){
                PileTrade trade = detail(pilePurchaseAction.getOrderCode());
                if(Objects.nonNull(trade)){
                    trade.getTradeItems().forEach(tradeItem -> {
                        if (tradeItem.getSkuId().equals(pilePurchaseAction.getGoodsInfoId())){
                            pilePurchaseAction.setPid(trade.getParentId());
                            pilePurchaseActionRepository.saveAndFlush(pilePurchaseAction);
                        }
                    });
                }
            }
        });

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("同步囤货订单总金额及优惠后商品单价完成，耗时：{}毫秒",duration.toMillis());
    }

    /**
     * 余额支付
     */
    @Transactional
    @LcnTransaction
    public void walletPay(WalletPayRequest request) {

        CustomerWalletVO customerWalletVO = customerWalletQueryProvider.getBalanceByCustomerId(
                WalletByCustomerIdQueryRequest
                        .builder()
                        .customerId(request.getCustomerId())
                        .build()).getContext().getCustomerWalletVO();

        PileTrade detail = detail(request.getTid());

        if (Objects.nonNull(request.getParentId())) {
            detail = detailByParentId(request.getParentId());
        }

        CustomerGetByIdRequest customerGetByIdRequest = new CustomerGetByIdRequest();
        customerGetByIdRequest.setCustomerId(request.getCustomerId());
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(customerGetByIdRequest).getContext();

        /**账户余额不足*/
        if (customerWalletVO == null || customerWalletVO.getBalance().compareTo(detail.getTradePrice().getTotalPrice()) == -1) {
            throw new SbcRuntimeException("K-190001");
        }
        /**订单和用户对应不上*/
        if (!request.getCustomerId().equals(detail.getBuyer().getId())) {
            throw new SbcRuntimeException("K-050100", new Object[]{detail.getId()});
        }

        log.info("oldCustomerWalletVO {}", customerWalletVO);

        /**订单相关逻辑*/
        String businessId = Objects.isNull(request.getParentId()) ? request.getTid() : request.getParentId();
        boolean isMergePay = isMergePayOrder(businessId);

        PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                GatewayConfigByGatewayRequest(PayGatewayEnum.BALANCE, Constants.BOSS_DEFAULT_STORE_ID)).getContext();

        //分布式锁，防止同一订单重复回调
        RLock rLock = redissonClient.getFairLock(businessId);
        rLock.lock();

        List<PileTrade> trades = null;
        try {
            trades = new ArrayList<>();
            //查询支付记录
            WalletRecordResponse context = walletRecordProvider.getWalletRecordByRelationOrderId(QueryWalletRecordByRelationOrderIdRequest
                    .builder().relationOrderId(detail.getId()).build())
                    .getContext();
            if (isMergePay) {
                /*
                 * 合并支付
                 * 查询订单是否已支付或过期作废
                 */
                trades = detailsByParentId(businessId);
                //订单合并支付场景状态采样
                //余额扣除成功
                if (context == null || Objects.isNull(context.getWalletRecordVO())) {
                    /**扣除账户余额*/
                    TradePrice tradePrice = detail.getTradePrice();
                    if (customerWalletVO.getRechargeBalance().compareTo(tradePrice.getTotalPrice()) == Constants.yes) {

                        customerWalletVO.setRechargeBalance(customerWalletVO.getRechargeBalance().subtract(tradePrice.getTotalPrice()));
                        customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getTotalPrice()));
                    } else {
                        BigDecimal subtract = tradePrice.getTotalPrice().subtract(customerWalletVO.getRechargeBalance());
                        //充值余额 = 下单金额
                        if (subtract.compareTo(BigDecimal.ZERO) == 0) {
                            customerWalletVO.setRechargeBalance(customerWalletVO.getRechargeBalance().subtract(tradePrice.getTotalPrice()));
                            customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getTotalPrice()));
                        } else {
                            customerWalletVO.setRechargeBalance(BigDecimal.ZERO);
                            customerWalletVO.setGiveBalance(customerWalletVO.getGiveBalance().subtract(subtract));
                            customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getTotalPrice()));
                        }
                    }

                    log.info("newCustomerWalletVO {}", customerWalletVO);

                    customerWalletProvider.updateCustomerWalletByWalletId(CustomerWalletModifyRequest.builder().customerWalletVO(customerWalletVO).build());
                    /**新增交易记录*/
                    //交易流水号
                    String recordNo = generatorService.generate("W");
                    WalletRecordVO walletRecordVO = new WalletRecordVO();
                    walletRecordVO.setRecordNo(recordNo);
                    walletRecordVO.setTradeRemark("余额支付");
                    walletRecordVO.setCustomerAccount(customer.getCustomerAccount());
                    walletRecordVO.setRelationOrderId(detail.getId());
                    //枚举
                    walletRecordVO.setTradeType(WalletRecordTradeType.BALANCE_PAY);
                    walletRecordVO.setBudgetType(BudgetType.EXPENDITURE);
                    walletRecordVO.setDealPrice(tradePrice.getTotalPrice());
                    walletRecordVO.setChargePrice(BigDecimal.ZERO);
                    walletRecordVO.setDealTime(LocalDateTime.now());
                    walletRecordVO.setCurrentBalance(customerWalletVO.getBalance());
                    walletRecordVO.setTradeState(TradeStateEnum.PAID);

                    walletRecordProvider.addWalletRecord(KsBeanUtil.convert(walletRecordVO, AddWalletRecordRecordRequest.class));
                    balancePayHandle(payGatewayConfig, recordNo, businessId, trades, true, request.getChannelType());
                }
            } else {
                //单笔支付
                PileTrade trade = new PileTrade();
                if (isTailPayOrder(businessId)) {
                    trade =
                            queryAll(TradeQueryRequest.builder().tailOrderNo(businessId).build()).get(0);
                } else {
                    trade = detail(businessId);
                }
                if (context == null || Objects.isNull(context.getWalletRecordVO())) {
                    /**扣除账户余额*/
                    TradePrice tradePrice = detail.getTradePrice();
                    if (customerWalletVO.getRechargeBalance().compareTo(tradePrice.getTotalPrice()) == Constants.yes) {

                        customerWalletVO.setRechargeBalance(customerWalletVO.getRechargeBalance().subtract(tradePrice.getTotalPrice()));
                        customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getTotalPrice()));
                    } else {
                        BigDecimal subtract = tradePrice.getTotalPrice().subtract(customerWalletVO.getRechargeBalance());
                        //充值余额 = 下单金额
                        if (subtract.compareTo(BigDecimal.ZERO) == 0) {
                            customerWalletVO.setRechargeBalance(customerWalletVO.getRechargeBalance().subtract(tradePrice.getTotalPrice()));
                            customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getTotalPrice()));
                        } else {
                            customerWalletVO.setRechargeBalance(BigDecimal.ZERO);
                            customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(tradePrice.getTotalPrice()));
                        }
                    }

                    log.info("newCustomerWalletVO {}", customerWalletVO);

                    customerWalletProvider.updateCustomerWalletByWalletId(CustomerWalletModifyRequest.builder().customerWalletVO(customerWalletVO).build());
                    /**新增交易记录*/
                    //交易流水号
                    String recordNo = generatorService.generate("W");
                    WalletRecordVO walletRecordVO = new WalletRecordVO();
                    walletRecordVO.setRecordNo(recordNo);
                    walletRecordVO.setTradeRemark("余额支付");
                    walletRecordVO.setCustomerAccount(customer.getCustomerAccount());
                    walletRecordVO.setRelationOrderId(detail.getId());
                    //枚举
                    walletRecordVO.setTradeType(WalletRecordTradeType.BALANCE_PAY);
                    walletRecordVO.setBudgetType(BudgetType.EXPENDITURE);
                    walletRecordVO.setDealPrice(tradePrice.getTotalPrice());
                    walletRecordVO.setChargePrice(BigDecimal.ZERO);
                    walletRecordVO.setDealTime(LocalDateTime.now());
                    walletRecordVO.setCurrentBalance(customerWalletVO.getBalance());
                    walletRecordVO.setTradeState(TradeStateEnum.PAID);
                    walletRecordProvider.addWalletRecord(KsBeanUtil.convert(walletRecordVO, AddWalletRecordRecordRequest.class));

                    trades.add(trade);
                    balancePayHandle(payGatewayConfig, recordNo, businessId, trades, false, request.getChannelType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //解锁
            rLock.unlock();
        }
    }


    private void balancePayHandle(PayGatewayConfigResponse payGatewayConfig, String recordNo, String businessId, List<PileTrade> trades, boolean isMergePay, String channelType) {
        //异步回调添加交易数据
        PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
        //流水号
        payTradeRecordRequest.setTradeNo(recordNo);
        //商户订单号或父单号
        payTradeRecordRequest.setBusinessId(businessId);
        payTradeRecordRequest.setResult_code("SUCCESS");
        //订单金额
        BigDecimal totalPrice = trades.stream().map(t -> t.getTradePrice()).collect(Collectors.toList())
                .stream().map(tp -> tp.getTotalPrice()).collect(Collectors.toList())
                .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("totalPrice============= {}", totalPrice);
        payTradeRecordRequest.setPracticalPrice(totalPrice);
        payTradeRecordRequest.setApplyPrice(totalPrice);
        ChannelItemByGatewayRequest channelItemByGatewayRequest = new ChannelItemByGatewayRequest();
        channelItemByGatewayRequest.setGatewayName(payGatewayConfig.getPayGateway().getName());
        PayChannelItemListResponse payChannelItemListResponse =
                payQueryProvider.listChannelItemByGatewayName(channelItemByGatewayRequest).getContext();
        List<PayChannelItemVO> payChannelItemVOList =
                payChannelItemListResponse.getPayChannelItemVOList();
        ChannelItemSaveRequest channelItemSaveRequest = new ChannelItemSaveRequest();
        String code = "balance_pc";
        if (("APP").equals(channelType)) {
            code = "balance_app";
        } else if (("H5").equals(channelType)) {
            code = "balance_h5";
        }
        channelItemSaveRequest.setCode(code);
        payChannelItemVOList.forEach(payChannelItemVO -> {
            if (channelItemSaveRequest.getCode().equals(payChannelItemVO.getCode())) {
                //更新支付项
                payTradeRecordRequest.setChannelItemId(payChannelItemVO.getId());
            }
        });
        //微信支付异步回调添加交易数据
//        payProvider.wxPayCallBack(payTradeRecordRequest);
        // 新增交易记录
        List<PayTradeRecordRequest> payTradeRecordRequests = Lists.newArrayList(payTradeRecordRequest);
        payProvider.batchSavePayTradeRecord(payTradeRecordRequests);
        //订单 支付单 操作信息
        Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("-1").name(PayGatewayEnum.BALANCE.name())
                .account(PayGatewayEnum.BALANCE.name()).platform(Platform.THIRD).build();
        payCallbackOnline(trades, operator, isMergePay);
    }

    //推erp囤货订单入口
    public void pushKingdeeStockOrders(PileTrade trade,Boolean threadPool,PayWay payWay){
        log.info("PileTradeService.pushKingdeeStockOrders req id:{} payWay:{}",trade.getId(),payWay);
        PileTradePushRequest tradePushRequest = new PileTradePushRequest();
        tradePushRequest.setThreadPool(threadPool);
        //获取金蝶登录token
        Map<String,Object> requestLogMap = new HashMap<>();
        requestLogMap.put("user",kingdeeUser);
        requestLogMap.put("pwd",kingdeePwd);
        String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap,loginUrl);
        if (StringUtils.isNotEmpty(loginToken)){
            //添加表头部分
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.isNull(employeeResponse) || StringUtils.isEmpty(employeeResponse.getErpEmployeeId())){
                log.info("PileTradeService.pushKingdeeStockOrders Lack employeeResponse");
                return;
            }
            tradePushRequest.setLoginToken(loginToken);
            tradePushRequest.setFBillNo(trade.getId());
            tradePushRequest.setFDate(DateUtil.nowDate());
            Map fSaleOrgId = new HashMap();
            fSaleOrgId.put("FNumber",kingdeeOrganization);
            tradePushRequest.setFSaleOrgId(fSaleOrgId);
            Map fCustId = new HashMap();
            fCustId.put("FNumber",trade.getBuyer().getAccount());
            tradePushRequest.setFCustId(fCustId);
            Map fSalerId = new HashMap();
            fSalerId.put("FNumber",employeeResponse.getErpEmployeeId());
            tradePushRequest.setFSalerId(fSalerId);
            if (payWay != null && (payWay.equals(PayWay.ALIPAY) || payWay.equals(PayWay.WECHAT) || payWay.equals(PayWay.UNIONPAY))){
                Map fBankAccount = new HashMap();
                fBankAccount.put("FNumber",kingdeeUnionpay);
                tradePushRequest.setFBankAccount(fBankAccount);
            }
            Map fSetType = new HashMap();
            if (payWay.equals(PayWay.CASH)){
                //先下支付
                fSetType.put("FNumber","offlinepay");
                tradePushRequest.setFSetType(fSetType);
            }else {
                fSetType.put("FNumber",payWay.toValue());
                tradePushRequest.setFSetType(fSetType);
            }
            //收款方式
            tradePushRequest.setFCollectType(trade.getPayInfo().getDesc());
            List<PileTradePushTableBodyRequest> pushTableBodyRequestList = new ArrayList<>();
            //添加表体部分
            if (trade.getTradeItems().size() > 0) {
                trade.getTradeItems().stream().forEach(tradeItem -> {
                    PileTradePushTableBodyRequest tableBodyRequest = new PileTradePushTableBodyRequest();
                    BigDecimal price = tradeItem.getSplitPrice().divide(BigDecimal.valueOf(tradeItem.getNum()), 6, BigDecimal.ROUND_DOWN);
                    Map fMaterialId = new HashMap();
                    fMaterialId.put("FNumber", tradeItem.getErpSkuNo());
                    tableBodyRequest.setFMaterialId(fMaterialId);
                    tableBodyRequest.setFQty(tradeItem.getNum());
                    tableBodyRequest.setFPrice(price);
                    tableBodyRequest.setFTaxPrice(price);
                    tableBodyRequest.setFIsFree(false);
                    tableBodyRequest.setFDSBMText(tradeItem.getSkuNo());
                    pushTableBodyRequestList.add(tableBodyRequest);
                });
            }
            if (trade.getGifts().size() > 0) {
                trade.getGifts().stream().forEach(tradeItem -> {
                    PileTradePushTableBodyRequest tableBodyRequest = new PileTradePushTableBodyRequest();
                    BigDecimal price = tradeItem.getSplitPrice().divide(BigDecimal.valueOf(tradeItem.getNum()), 6, BigDecimal.ROUND_DOWN);
                    Map fMaterialId = new HashMap();
                    fMaterialId.put("FNumber", tradeItem.getErpSkuNo());
                    tableBodyRequest.setFMaterialId(fMaterialId);
                    tableBodyRequest.setFQty(tradeItem.getNum());
                    tableBodyRequest.setFPrice(price);
                    tableBodyRequest.setFTaxPrice(price);
                    tableBodyRequest.setFIsFree(true);
                    tableBodyRequest.setFDSBMText(tradeItem.getSkuNo());
                    pushTableBodyRequestList.add(tableBodyRequest);
                });
            }
            tradePushRequest.setPushTableBodyRequestList(pushTableBodyRequestList);
            pushOrderKingdeeProvider.pushSalesKingdee(tradePushRequest);
            log.info("PileTradeService.pushKingdeeStockOrders end id:{}",trade.getId());
        }
    }

    /**
     * 新囤货订单审核: 驳回
     * @param tid
     * @param auditState
     * @param reason
     * @param operator
     * @param financialFlag
     */
    public void newAudit(String tid, AuditState auditState, String reason, Operator operator, Boolean financialFlag) {
        newPileTradeService.reject(tid, auditState, reason, operator, financialFlag);
    }
}
