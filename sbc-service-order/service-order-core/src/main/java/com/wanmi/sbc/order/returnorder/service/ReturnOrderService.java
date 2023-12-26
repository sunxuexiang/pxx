package com.wanmi.sbc.order.returnorder.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.api.provider.finance.record.AccountRecordProvider;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordAddRequest;
import com.wanmi.sbc.account.api.request.finance.record.AccountRecordDeleteByReturnOrderCodeAndTypeRequest;
import com.wanmi.sbc.account.bean.enums.*;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.enums.node.ReturnOrderProcessType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountProvider;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountAddRequest;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountOptionalRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountAddResponse;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountOptionalResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerAccountAddOrModifyDTO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.customer.bean.vo.CustomerAccountVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.groupongoodsinfo.GrouponGoodsInfoSaveProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.groupongoodsinfo.GrouponGoodsInfoReturnModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchPlusStockRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPresellRecordDTO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivitySaveProvider;
import com.wanmi.sbc.marketing.api.provider.grouponrecord.GrouponRecordProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeDeleteRequest;
import com.wanmi.sbc.marketing.api.request.coupon.TakeBackOrderCoinRequest;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityModifyStatisticsNumByIdRequest;
import com.wanmi.sbc.marketing.api.request.grouponrecord.GrouponRecordDecrBuyNumRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityStockRequest;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.enums.GrouponOrderStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.mongo.MongoTccHelper;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.order.api.constant.RefundReasonConstants;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamountTrade.InventoryDetailSamountTradeProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamountTrade.InventoryDetailSamountTradeRequest;
import com.wanmi.sbc.order.api.request.distribution.ReturnOrderSendMQRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderRefundRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderRequest;
import com.wanmi.sbc.order.api.request.trade.AddWalletRecordRequest;
import com.wanmi.sbc.order.api.response.inventorydetailsamounttrade.InventoryDetailSamountTradeResponse;
import com.wanmi.sbc.order.ares.service.OrderAresService;
import com.wanmi.sbc.order.bean.dto.PickGoodsDTO;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.order.common.OperationLogMq;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.constant.OrderErrorCode;
import com.wanmi.sbc.order.customer.service.CustomerCommonService;
import com.wanmi.sbc.order.enums.PushKingdeeOrderStatusEnum;
import com.wanmi.sbc.order.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.order.groupon.service.GrouponOrderService;
import com.wanmi.sbc.order.historytownshiporder.model.root.HistoryTownShipOrder;
import com.wanmi.sbc.order.historytownshiporder.repository.HistoryTownShipOrderRepository;
import com.wanmi.sbc.order.historytownshiporder.service.HistoryTownShipOrderService;
import com.wanmi.sbc.order.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.order.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.order.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
import com.wanmi.sbc.order.inventorydetailsamounttrade.service.InventoryDetailSamountTradeService;
import com.wanmi.sbc.order.mq.OrderProducerService;
import com.wanmi.sbc.order.payorder.model.root.PayOrder;
import com.wanmi.sbc.order.payorder.response.PayOrderResponse;
import com.wanmi.sbc.order.payorder.service.PayOrderService;
import com.wanmi.sbc.order.pilepurchase.PilePurchase;
import com.wanmi.sbc.order.pilepurchase.PilePurchaseRepository;
import com.wanmi.sbc.order.refund.model.root.RefundBill;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.refund.repository.RefundOrderRepository;
import com.wanmi.sbc.order.refund.service.RefundBillService;
import com.wanmi.sbc.order.refund.service.RefundFactory;
import com.wanmi.sbc.order.refund.service.RefundOrderService;
import com.wanmi.sbc.order.returnorder.fsm.ReturnFSMService;
import com.wanmi.sbc.order.returnorder.fsm.event.ReturnEvent;
import com.wanmi.sbc.order.returnorder.fsm.params.ReturnStateRequest;
import com.wanmi.sbc.order.returnorder.model.entity.AuditOrderEntry;
import com.wanmi.sbc.order.returnorder.model.entity.KingdeeAuditOrder;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.root.*;
import com.wanmi.sbc.order.returnorder.model.value.*;
import com.wanmi.sbc.order.returnorder.mq.ReturnOrderProducerService;
import com.wanmi.sbc.order.returnorder.repository.*;
import com.wanmi.sbc.order.returnorder.request.ReturnQueryRequest;
import com.wanmi.sbc.order.returnorder.service.newPileOrder.NewPileReturnOrderQuery;
import com.wanmi.sbc.order.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.PileStockRecord;
import com.wanmi.sbc.order.trade.model.entity.PileStockRecordAttachment;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.*;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.*;
import com.wanmi.sbc.order.trade.repository.*;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.order.trade.service.VerifyService;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.order.util.KingdeeLoginUtils;
import com.wanmi.sbc.order.village.OrderVillageAddDeliveryService;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.ChannelItemByIdRequest;
import com.wanmi.sbc.pay.api.request.RefundResultByOrdercodeRequest;
import com.wanmi.sbc.pay.api.request.TradeRecordByOrderCodeRequest;
import com.wanmi.sbc.pay.api.response.PayChannelItemResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSOrderProvider;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackDetailsRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSOrderCancelRequest;
import com.wanmi.sbc.wms.api.response.wms.ResponseWMSReturnResponse;
import com.wanmi.sbc.wms.bean.vo.ERPWMSConstants;
import com.wanmi.sbc.wms.bean.vo.WmsErpIdConstants;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jinwei on 20/4/2017.
 */
@Slf4j
@Service
public class ReturnOrderService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private OrderAresService orderAresService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ReturnPileOrderRepository returnPileOrderRepository;

    @Autowired
    private ReturnOrderTransferRepository returnOrderTransferRepository;

    @Autowired
    private ReturnOrderTransferService returnOrderTransferService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private ReturnFSMService returnFSMService;

    @Autowired
    private RefundOrderService refundOrderService;

    @Autowired
    private RefundBillService refundBillService;

    @Autowired
    private CustomerAccountQueryProvider customerAccountQueryProvider;

    @Autowired
    private CustomerAccountProvider customerAccountProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private AccountRecordProvider accountRecordProvider;

    @Autowired
    private RefundOrderRepository refundOrderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private OperationLogMq operationLogMq;

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    /**
     * 注入退单状态变更生产者service
     */
    @Autowired
    private ReturnOrderProducerService returnOrderProducerService;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private GrouponGoodsInfoSaveProvider grouponGoodsInfoProvider;

    @Autowired
    private GrouponActivitySaveProvider activityProvider;

    @Autowired
    private GrouponRecordProvider recordProvider;

    @Autowired
    private GrouponOrderService grouponOrderService;

    @Autowired
    private RequestWMSOrderProvider requestWMSOrderProvider;

    @Value("${wms.api.flag}")
    private Boolean wmsPushFlag;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private PilePurchaseRepository pilePurchaseRepository;

    @Autowired
    private PileStockRecordRepository pileStockRecordRepository;

    @Autowired
    private PileStockRecordAttachmentRepostory pileStockRecordAttachmentRepostory;
    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    @Autowired
    private TradePushKingdeeRefundRepository tradePushKingdeeRefundRepository;

    @Autowired
    private TradePushKingdeeReturnGoodsRepository tradePushKingdeeReturnGoodsRepository;

    @Autowired
    private TradePushKingdeeOrderRepository tradePushKingdeeOrderRepository;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private TradeCachePushKingdeeOrderRepository tradeCachePushKingdeeOrderRepository;

    @Autowired
    private HistoryTownShipOrderService historyTownShipOrderService;

    @Autowired
    private HistoryTownShipOrderRepository historyTownShipOrderRepository;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private TradePushKingdeePayRepository tradePushKingdeePayRepository;

    @Autowired
    NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    private OrderCommonService orderCommonService;

    @Autowired
    private OrderVillageAddDeliveryService orderVillageAddDeliveryService;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    /**
     * 退货
     */
    @Value("${kingdee.Return.url}")
    private String returnUrl;

    /**
     * 退款
     */
    @Value("${kingdee.Refurn.url}")
    private String refurnUrl;

    @Value("${kingdee.orderInvalid.url}")
    private String orderInvalidUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.wechat}")
    private String kingdeeWechat;

    @Value("${kingdee.alipay}")
    private String kingdeeAlipay;

    @Value("${kingdee.pay.url}")
    private String payUrl;

    @Value("${kingdee.ccb}")
    private String kingdeeCcbpay;

    @Value("${kingdee.bocom}")
    private String kingdeeBocomPay;

    /**
     * 囤货退货
     */
    @Value("${kingdee.stockUpRet.url}")
    private String stockUpRetUrl;

    /**
     * 是否开启新金蝶
     */
    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;

    @Autowired
    private NewPileReturnOrderQuery newPileReturnOrderQuery;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private InventoryDetailSamountTradeProvider inventoryDetailSamountTradeProvider;

    @Autowired
    private InventoryDetailSamountService inventoryDetailSamountService;

    @Autowired
    private InventoryDetailSamountTradeService inventoryDetailSamountTradeService;

    @Autowired
    private CcbPayProvider ccbPayProvider;


    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param returnOrder
     */
    @MongoRollback(persistence = ReturnOrder.class, operation = Operation.ADD)
    public ReturnOrder addReturnOrder(ReturnOrder returnOrder) {
        return returnOrderRepository.save(returnOrder);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param returnOrder
     */
    @MongoRollback(persistence = ReturnOrder.class, operation = Operation.UPDATE)
    @LcnTransaction
    public void updateReturnOrder(ReturnOrder returnOrder) {
        returnOrderRepository.save(returnOrder);
    }

    /**
     * 删除文档
     *
     * @param rid
     */
    @MongoRollback(persistence = ReturnOrder.class, idExpress = "rid", operation = Operation.UPDATE)
    public void deleteReturnOrder(String rid) {
        returnOrderRepository.deleteById(rid);
    }


    /**
     * 商家代客退单
     *
     * @param returnOrder
     * @param operator
     * @return
     */
    public String s2bCreate(ReturnOrder returnOrder, Operator operator) {
        Trade trade = tradeService.detail(returnOrder.getTid());
//        Customer customer = verifyService.verifyCustomer(trade.getBuyer().getId());
        CustomerGetByIdResponse customer = verifyService.verifyCustomer(trade.getBuyer().getId());
        verifyService.verifyCustomerWithSupplier(customer.getCustomerId(), returnOrder.getCompany().getCompanyInfoId());
        return create(returnOrder, operator, null);
    }

//    private void splitReturnTrade(ReturnOrder returnOrder, Trade trade ,List<ReturnOrder> returnOrders) {
//        //订单详情集合
//        List<TradeItem> tradeItemList = trade.getTradeItems();
//        // 主订单商品id集合
//        List<String> goodsInfoIdList = tradeItemList.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
//
//        BaseResponse<GoodsInfoListByIdsResponse> listByIdsResponse =
//                goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(goodsInfoIdList).build());
//        List<GoodsInfoVO> goodsInfoVOList = listByIdsResponse.getContext().getGoodsInfos();
//        tradeItemList.forEach(tradeItem -> goodsInfoVOList.forEach(goodsInfoVO -> {
//            if (tradeItem.getSkuId().equals(goodsInfoVO.getGoodsInfoId())) {
//                // tradeItem设置供应商id
//                tradeItem.setProviderId(goodsInfoVO.getProviderId());
//                // 供货价
//                tradeItem.setSupplyPrice(goodsInfoVO.getSupplyPrice());
//            }
//        }));
//
//        // 退单商品id集合
//        List<ReturnItem> returnItemList = returnOrder.getReturnItems();
//        List<String> returnGoodsInfoList = returnOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
//        BaseResponse<GoodsInfoListByIdsResponse> returnListByIdsResponse =
//                goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(returnGoodsInfoList).build());
//        List<GoodsInfoVO> returnGoodsInfoVOList = returnListByIdsResponse.getContext().getGoodsInfos();
//        returnItemList.forEach(returnItem -> returnGoodsInfoVOList.forEach(goodsInfoVO->{
//            if(returnItem.getSkuId().equals(goodsInfoVO.getGoodsInfoId())){
//                returnItem.setProviderId(goodsInfoVO.getProviderId());
//            }
//        }));
//
//        List<Long> providerIds = goodsInfoVOList.stream().filter(
//                goodsInfoVO -> Objects.nonNull(goodsInfoVO.getProviderId()))
//                .map(GoodsInfoVO::getProviderId).distinct().collect(Collectors.toList());
//
//        //1.商家商品退单
//        List<ReturnItem> returnStoreItemList =
//                returnItemList.stream().filter(returnItem -> Objects.isNull(returnItem.getProviderId())).collect(Collectors.toList());
//
//        if(CollectionUtils.isNotEmpty(returnStoreItemList)){
//            if(CollectionUtils.isEmpty(providerIds)){
//                returnOrders.add(returnOrder);
//            }else {
//                //1.商家商品退单
//                List<TradeItem> storeItemList =
//                        tradeItemList.stream().filter(tradeItem -> Objects.isNull(tradeItem.getProviderId())).collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(storeItemList)) {
//                    String providerTradeId =
//                            providerTradeProvider.providerByidAndPid(TradeGetByIdAndPidRequest.builder().tid(trade.getId()).providerId(String.valueOf(trade.getSupplier().getStoreId())).build()).getContext().getProviderTradeId();
//                    returnOrder.setPtid(providerTradeId);
//                    buildReturnOrder(returnOrder, trade, StoreType.SUPPLIER, null,storeItemList);
//                    returnOrders.add(returnOrder);
//                }
//            }
//
//        }
//
//    }

    private void buildReturnOrder(ReturnOrder returnOrder, Trade trade, StoreType storeType, Long providerId, List<TradeItem> storeItemList) {

        if (StoreType.PROVIDER.equals(storeType)) {
            StoreVO storeVO =
                    storeQueryProvider.getById(StoreByIdRequest.builder().storeId(providerId).build()).getContext().getStoreVO();
            if (storeVO != null) {
                String companyCode = storeVO.getCompanyInfo().getCompanyCode();
                Long companyInfoId = storeVO.getCompanyInfo().getCompanyInfoId();
                String supplierName = storeVO.getCompanyInfo().getSupplierName();

                returnOrder.setProviderId(String.valueOf(providerId));
                returnOrder.setProviderCode(companyCode);
                returnOrder.setProviderName(supplierName);
                returnOrder.setProviderCompanyInfoId(companyInfoId);
                // 筛选当前供应商的订单商品信息
                List<TradeItem> providerTradeItems =
                        trade.getTradeItems().stream().filter(tradeItem -> providerId.equals(tradeItem.getProviderId())).collect(Collectors.toList());

                List<ReturnItem> returnItemDTOList = new ArrayList<>();
                //组装退单详情
                BigDecimal providerTotalPrice = BigDecimal.ZERO;
                BigDecimal price = BigDecimal.ZERO;
                Long points = 0L;
                for (TradeItem tradeItemVO : providerTradeItems) {
                    for (ReturnItem returnItemDTO : returnOrder.getReturnItems()) {
                        if (tradeItemVO.getSkuId().equals(returnItemDTO.getSkuId())) {
                            returnItemDTO.setSupplyPrice(tradeItemVO.getSupplyPrice());
                            BigDecimal supplyPrice = Objects.nonNull(tradeItemVO.getSupplyPrice()) ?
                                    tradeItemVO.getSupplyPrice() : BigDecimal.ZERO;
                            returnItemDTO.setProviderPrice(supplyPrice.multiply(returnItemDTO.getNum()));
                            providerTotalPrice = providerTotalPrice.add(returnItemDTO.getProviderPrice());
                            price = price.add(tradeItemVO.getSplitPrice());
                            if (returnItemDTO.getSplitPoint() != null) {
                                points += returnItemDTO.getSplitPoint();
                            }
                            returnItemDTOList.add(returnItemDTO);

                        }
                    }
                }
                returnOrder.setReturnItems(returnItemDTOList);
                returnOrder.getReturnPrice().setProviderTotalPrice(providerTotalPrice);
                returnOrder.getReturnPrice().setApplyPrice(price);
                returnOrder.getReturnPrice().setTotalPrice(price);
                returnOrder.getReturnPoints().setApplyPoints(points);
            }
        } else {
            if (storeItemList != null) {
                List<ReturnItem> returnItemDTOList = new ArrayList<>();
                BigDecimal price = BigDecimal.ZERO;
                Long points = 0L;
                //组装退单详情
                for (TradeItem tradeItemVO : storeItemList) {
                    for (ReturnItem returnItemDTO : returnOrder.getReturnItems()) {
                        if (tradeItemVO.getSkuId().equals(returnItemDTO.getSkuId())) {
                            price = price.add(tradeItemVO.getSplitPrice());
                            if (returnItemDTO.getSplitPoint() != null) {
                                points += returnItemDTO.getSplitPoint();
                            }
                            returnItemDTOList.add(returnItemDTO);
                        }
                    }
                }
                returnOrder.setReturnItems(returnItemDTOList);
                returnOrder.getReturnPrice().setApplyPrice(price);
                returnOrder.getReturnPrice().setTotalPrice(price);
                returnOrder.getReturnPoints().setApplyPoints(points);
            }

        }
    }

    /**
     * 退单创建
     *
     * @param returnOrder
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public String createPile(ReturnOrder returnOrder, Operator operator, Integer forceRefund) {
        log.info("创建提货退单==============》activityType:{},订单号：{}", returnOrder.getActivityType(), returnOrder.getTid());
        if (returnOrder.getDescription().length() > 100) {
            throw new SbcRuntimeException("K-000009");
        }
        //查询订单信息
        Trade trade;
        if (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) { //wms自动退单
            trade = queryWmsCanReturnItemNumByTid(returnOrder.getTid()); //设置了可退金额
        } else {
            trade = queryCanReturnItemNumByTid(returnOrder.getTid()); //设置了可退金额可退积分可退数量
        }
        log.info("提货订单信息trade：{}", trade);
        //查询该订单所有退单
        List<ReturnOrder> returnOrderList = returnOrderRepository.findByTid(trade.getId());
        //筛选出已完成的退单列表
        List<ReturnOrder> completedReturnOrders = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(returnOrderList)) {
            //过滤处理中的退单
            List<ReturnOrder> returnOrders = returnOrderList.stream().filter(item -> item.getReturnFlowState() !=
                    ReturnFlowState.COMPLETED
                    && item.getReturnFlowState() != ReturnFlowState.VOID
                    && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                    && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                    && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(returnOrders)) {
                throw new SbcRuntimeException("K-050120");
            }

            completedReturnOrders = returnOrderList.stream().filter(allOrder -> allOrder
                    .getReturnFlowState() == ReturnFlowState.COMPLETED)
                    .collect(Collectors.toList());
        }

        //是否仅退款
        boolean isRefund = trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || trade
                .getTradeState().getDeliverStatus()
                == DeliverStatus.VOID;
        //退单总金额
        ReturnPrice returnPrice = returnOrder.getReturnPrice();

        //计算该订单下所有已完成退单的总金额
        BigDecimal allOldPrice = new BigDecimal(0);
        for (ReturnOrder order : completedReturnOrders) {
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

        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        List<String> skuIds = returnItems.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
        List<TradeDistributeItemVO> tradeDistributeItemVos = returnOrder.getDistributeItems();
        BigDecimal price = BigDecimal.ZERO;
        Long points = 0L;
        //仅退款，没有发货退运费，其他不退
        if (isRefund) {
            price = (Objects.nonNull(trade.getTradePrice().getDeliveryPrice
                    ()) ? trade.getTradePrice().getDeliveryPrice() : BigDecimal.ZERO);
        }

        returnOrder.getReturnPrice().setTotalPrice(price);
        returnOrder.getReturnPrice().setApplyPrice(price);
        if (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) {
            returnOrder.getReturnPrice().setActualReturnPrice(price);
        }
        if (returnOrder.getReturnPoints() == null) {
            returnOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());
        }

        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnOrder.getTid()).get();
        if (operator.getPlatform() == Platform.BOSS && PayType.fromValue(Integer.parseInt(trade.getPayInfo()
                .getPayTypeId())) == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                == -1) {
            throw new SbcRuntimeException("K-050126");
        }

        if (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) {
            createWMSRefund(returnOrder, operator, trade);
        } else if (isRefund) {
            createRefund(returnOrder, operator, trade);
        } else {
            createReturn(returnOrder, operator, trade);
        }

        if (Objects.nonNull(trade.getSaleType())) {
            returnOrder.setSaleType(trade.getSaleType());
        } else {
            returnOrder.setSaleType(SaleType.WHOLESALE);
        }
        returnOrder.setBuyer(trade.getBuyer());
        returnOrder.setWareId(trade.getWareId());
        returnOrder.setConsignee(trade.getConsignee());
        returnOrder.setCreateTime(LocalDateTime.now());
        returnOrder.setPayType(PayType.valueOf(trade.getPayInfo().getPayTypeName()));
        returnOrder.setPlatform(operator.getPlatform());
        returnOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());

        String rid = generatorService.generate("R");
        returnOrder.setId(rid);
        //boolean flag = false;
        //设置退货收货地址
        setReturnOrderAddress(returnOrder, trade);

        //记录日志
        returnOrder.appendReturnEventLog(
                new ReturnEventLog(operator, "创建提货退单", "创建提货退单", "", LocalDateTime.now())
        );

        returnOrder.setReturnFlowState(ReturnFlowState.INIT);


        // 计算并设置需要退的赠品
        getAndSetReturnGifts(returnOrder, trade, returnOrderList);

        if (wmsPushFlag) {
            if (isRefund) {
                if (kingdeeOpenState) {
                    //是否在满十件记录
                    TradeCachePushKingdeeOrder tradeCachePushKingdeeOrder = tradeCachePushKingdeeOrderRepository.findByReturnOrderCachePushKingdeeOrder(returnOrder.getTid());
                    if (Objects.nonNull(tradeCachePushKingdeeOrder)) {
                        tradeCachePushKingdeeOrderRepository.updateCachePushKingdeeOrderStatus(LocalDateTime.now(), tradeCachePushKingdeeOrder.getPushKingdeeId());
                        orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
                    } else {
                        //推送取消订单
                        if (pushCancelOrder(returnOrder, trade.getWareHouseCode(),trade)) {
                            returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                            orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
//                            if (!cancelOrder(returnOrder, true)) {
//                                throw new SbcRuntimeException(OrderErrorCode.ORDER_HAS_BEEN_PICK_IN_WMS, "申请退款失败，请稍后再试");
//                            }
                        } else {
                            //不为强制退款时，需要提示回滚
                            if (forceRefund == null || forceRefund != 1) {
                                throw new SbcRuntimeException(OrderErrorCode.ORDER_HAS_BEEN_PICK_IN_WMS, "申请退款失败，商品已拣货");
                            }
                        }
                    }
                }
            }/*else{
                //只有传false的时候不推送，空和true都需要推送
                if(Objects.isNull(returnOrder.getPushNeeded()) || returnOrder.getPushNeeded()) {
                    //推送退单
                    if (pushBackOrder(returnOrder,trade.getWareHouseCode(),trade)){
                        returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                    }else {
                        throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO,"申请退货失败，WMS接口异常");
                    }
                }
            }*/
        }
        //刷新进批次号
        returnOrder.getReturnItems().forEach(info -> {
            Optional<TradeItem> tradeItemOptional = trade.getTradeItems().stream().filter(tradeItem -> info
                    .getSkuId().equals(tradeItem.getSkuId())).findFirst();
            if (tradeItemOptional.isPresent()) {
                TradeItem tradeItem = tradeItemOptional.get();
                info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
            }
        });

        log.info("returnOrderReturnPrice============ {}", returnOrder.getReturnPrice());

        BigDecimal canReturnPrice = returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice().getApplyPrice() : returnOrder.getReturnPrice().getTotalPrice();
        log.info("canReturnPrice============ {}", canReturnPrice);
        //仅退款直接跳过审核生成退款单
        if (isRefund || (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats())) {
            returnOrder.setReturnFlowState(ReturnFlowState.AUDIT);
            refundOrderService.generateRefundOrderByEnity(returnOrder, returnOrder.getBuyer().getId(), canReturnPrice,
                    returnOrder.getPayType());
        }
        //保存退单
        returnOrderService.addReturnOrder(returnOrder);
        this.operationLogMq.convertAndSend(operator, "创建退单", "创建提货退单");

        /*  autoDeliver(rid, operator);*/
//        insertES(returnOrder);

        if (operator.getPlatform() == Platform.BOSS) {
            audit(rid, operator);
        }

        // 创建退单时，发送MQ消息
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.TRUE)
                .customerId(trade.getBuyer().getId())
                .orderId(trade.getId())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnOrderFlow(sendMQRequest);

        //售后单提交成功发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_COMMIT_SUCCESS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount()
        );

        return rid;
    }

    /**
     * 退单创建
     *
     * @param returnOrder
     * @param operator
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public String create(ReturnOrder returnOrder, Operator operator, Integer forceRefund) {
        log.info("ReturnOrderService.create req tid:{}", returnOrder.getTid());
        if (returnOrder.getDescription().length() > 100) {
            throw new SbcRuntimeException("K-000009");
        }
        //提货订单应退金额 前端计算出来的，如果为提货订单且为代客退单的时候要重新赋值 应退金额
        BigDecimal stockPrice = returnOrder.getReturnPrice().getTotalPrice();
        //查询订单信息
        Trade trade;
        if (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) { //wms自动退单
            trade = queryWmsCanReturnItemNumByTid(returnOrder.getTid());
        } else {
            //拆箱订单
            ReturnItem returnItem = returnOrder.getReturnItems().stream().findFirst().orElse(null);
            if (Objects.isNull(returnItem.getDevanningId())) {
                trade = queryCanReturnItemNumByTid(returnOrder.getTid());
            } else {
                trade = queryCanReturnDevanningItemNumByTid(returnOrder.getTid());
            }
        }
        //查询该订单所有退单
        List<ReturnOrder> returnOrderList = returnOrderRepository.findByTid(trade.getId());
        //筛选出已完成的退单列表
        List<ReturnOrder> completedReturnOrders = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(returnOrderList)) {
            //过滤处理中的退单
            List<ReturnOrder> returnOrders = returnOrderList.stream().filter(item -> item.getReturnFlowState() !=
                    ReturnFlowState.COMPLETED
                    && item.getReturnFlowState() != ReturnFlowState.VOID
                    && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                    && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                    && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(returnOrders)) {
                throw new SbcRuntimeException("K-050120");
            }

            completedReturnOrders = returnOrderList.stream().filter(allOrder -> allOrder
                    .getReturnFlowState() == ReturnFlowState.COMPLETED)
                    .collect(Collectors.toList());
        }


        //是否仅退款
        boolean isRefund = trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || trade
                .getTradeState().getDeliverStatus()
                == DeliverStatus.VOID;
        //退单总金额
        ReturnPrice returnPrice = returnOrder.getReturnPrice();

        //计算该订单下所有已完成退单的总金额
        BigDecimal allOldPrice = new BigDecimal(0);
        for (ReturnOrder order : completedReturnOrders) {
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

        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        List<String> skuIds = returnItems.stream().map(item -> item.getSkuId()).collect(Collectors.toList());

        //添加订单商品价格明细
        assignReturnItemsInventoryDetailSamountTrades(returnOrder);

        List<TradeDistributeItemVO> tradeDistributeItemVos = returnOrder.getDistributeItems();
        BigDecimal price = null;
        Long points = 0L;
        //为wms缺货退款
        if (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) {
            price = returnOrder.getReturnPrice().getActualReturnPrice();
        } else if (isRefund) {//未发货
            /*price = isSpecial ? returnPrice.getApplyPrice() :
                    trade.getTradeItems().stream().map(t -> t.getSplitPrice())
                            .reduce(BigDecimal::add).get().add(Objects.nonNull(trade.getTradePrice().getDeliveryPrice
                            ()) ? trade.getTradePrice().getDeliveryPrice() : BigDecimal.ZERO).add(Objects.nonNull(trade.getTradePrice().getBalancePrice()
                    ) ? trade.getTradePrice().getBalancePrice() : BigDecimal.ZERO);*/

            price = isSpecial ? returnPrice.getApplyPrice() :
                    trade.getTradeItems().stream().map(t -> t.getSplitPrice())
                            .reduce(BigDecimal::add).get().add(Objects.nonNull(trade.getTradePrice().getDeliveryPrice
                            ()) ? trade.getTradePrice().getDeliveryPrice() : BigDecimal.ZERO);


            //零售未发货还需要退？包装费
            if (SaleType.RETAIL.equals(trade.getSaleType())) {
                price = price.add(trade.getTradePrice().getPackingPrice());
            }

            points = trade.getCanReturnPoints();
            returnOrder.setDistributeItems(tradeDistributeItemVos);
        } else {
            //------------------start-------------------
            returnOrder.getReturnItems().forEach(info -> {
                Optional<TradeItem> tradeItemOptional = trade.getTradeItems().stream().filter(tradeItem -> info
                        .getSkuId().equals(tradeItem.getSkuId())).findFirst();
                if (Objects.nonNull(info.getDevanningId())) {
                    tradeItemOptional = trade.getTradeItems().stream().filter(tradeItem -> info
                            .getDevanningId().equals(tradeItem.getDevanningId())).findFirst();
                }
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
                    returnOrder.getReturnItems().stream().map(t -> t.getSplitPrice())
                            .reduce(BigDecimal::add).get();


            // 计算积分
            if (Objects.nonNull(trade.getTradePrice().getPoints())) {
                points = getPoints(returnOrder, trade);
            }

            //分销商品数据接口赋值开始
            tradeDistributeItemVos = tradeDistributeItemVos.stream().filter(item -> skuIds
                    .contains(item.getGoodsInfoId())).collect(Collectors.toList());
            if (Objects.nonNull(tradeDistributeItemVos) && tradeDistributeItemVos.size() > 0) {
                List<TradeDistributeItemVO> distributeItems = tradeDistributeItemVos.stream().map
                        (tradeDistributeItemVo -> {
                            Optional<TradeItem> tradeItemOptional =
                                    trade.getTradeItems().stream().filter(tradeItem -> tradeDistributeItemVo
                                            .getGoodsInfoId().equals(tradeItem.getSkuId())).findFirst();
                            TradeItem tradeItem = tradeItemOptional.get();
                            Optional<ReturnItem> returnItemOpt = returnItems.stream().filter(item -> item.getSkuId()
                                    .equals(tradeDistributeItemVo.getGoodsInfoId())).findFirst();
                            BigDecimal unitPrice = tradeItem.getSplitPrice().divide(new BigDecimal
                                            (tradeDistributeItemVo.getNum())
                                    , 2,
                                    BigDecimal.ROUND_DOWN);
                            if (returnItemOpt.isPresent()) {
                                ReturnItem returnItem = returnItemOpt.get();
                                if (tradeItem.getCanReturnNum().intValue() - returnItem.getNum().intValue() > 0) {
                                    //部分退款
                                    if (unitPrice.compareTo(new BigDecimal("0.01")) == -1) {
                                        tradeDistributeItemVo.setActualPaidPrice(BigDecimal.ZERO);
                                    } else {
                                        tradeDistributeItemVo.setActualPaidPrice(unitPrice.multiply(returnItem.getNum()));
                                    }
                                } else {
                                    //该商品已退完
                                    if (unitPrice.compareTo(new BigDecimal("0.01")) == -1) {
                                        tradeDistributeItemVo.setActualPaidPrice(BigDecimal.ZERO);
                                    } else {
                                        tradeDistributeItemVo.setActualPaidPrice(tradeItem.getSplitPrice().subtract(unitPrice.multiply(new BigDecimal
                                                (tradeItem.getNum()).subtract(returnItem.getNum()))));
                                    }
                                }
                                tradeDistributeItemVo.setNum(returnItem.getNum().longValue());
                            }
                            return tradeDistributeItemVo;

                        }).collect(Collectors.toList());
                returnOrder.setDistributeItems(distributeItems);
            }
            //分销商品的数据处理结束
        }

        if (returnOrder.getReturnPrice().getBalanceReturnPrice() != null && returnOrder.getReturnPrice().getBalanceReturnPrice().compareTo(BigDecimal.ZERO) > 0) {
            price = price.add(returnOrder.getReturnPrice().getBalanceReturnPrice());
            returnOrder.getReturnPrice().setTotalPrice(price);
            returnOrder.getReturnPrice().setApplyPrice(price);
        } else {
            returnOrder.getReturnPrice().setTotalPrice(price);
            returnOrder.getReturnPrice().setApplyPrice(price);
        }

        //如果订单为提货订单，且为代客退单,且为操作方为商家端；重新赋值应退金额为前端计算出来的金额
        if (TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType()) && forceRefund == 1) {
            log.info("ReturnOrderService.create tid:{} stockPrice:{}", returnOrder.getTid(), stockPrice);
            returnOrder.getReturnPrice().setTotalPrice(stockPrice);
            returnOrder.getReturnPrice().setApplyPrice(stockPrice);
        }

        if (returnOrder.getReturnPoints() == null) {
            returnOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());
        }

        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnOrder.getTid()).get();
        if (operator.getPlatform() == Platform.BOSS && PayType.fromValue(Integer.parseInt(trade.getPayInfo()
                .getPayTypeId())) == PayType.ONLINE && payOrder.getPayOrderPrice().compareTo(price.add(allOldPrice))
                == -1) {
            throw new SbcRuntimeException("K-050126");
        }

        if (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) {
            createWMSRefund(returnOrder, operator, trade);
        } else if (isRefund) {
            createRefund(returnOrder, operator, trade);
        } else {
            createReturn(returnOrder, operator, trade);
        }


        returnOrder.setBuyer(trade.getBuyer());
        returnOrder.setWareId(trade.getWareId());
        returnOrder.setWareName(trade.getWareName());
        returnOrder.setConsignee(trade.getConsignee());
        returnOrder.setCreateTime(LocalDateTime.now());
        returnOrder.setPayType(PayType.valueOf(trade.getPayInfo().getPayTypeName()));
        returnOrder.setPlatform(operator.getPlatform());
        returnOrder.setReturnPoints(ReturnPoints.builder().applyPoints(points).build());
        returnOrder.setPresellFlag(trade.getPresellFlag());//预售标识   @jkp
        returnOrder.setPresellDeliverDate(trade.getPresellDeliverDate());

        String rid = generatorService.generate("R");
        //设置退货收货地址
        setReturnOrderAddress(returnOrder, trade);
        returnOrder.setId(rid);
        //boolean flag = false;
        //退货退款信息同步到订单商品价格列表 (2已受理退货退款)
        updateInventoryDetailSamountTrade(returnOrder, 2, rid);

        //记录日志
        returnOrder.appendReturnEventLog(
                new ReturnEventLog(operator, "创建退单", "创建退单", "", LocalDateTime.now())
        );

        returnOrder.setReturnFlowState(ReturnFlowState.INIT);


        // 计算并设置需要退的赠品
        getAndSetReturnGifts(returnOrder, trade, returnOrderList);

        //wms三方推送
        if (wmsPushFlag) {
            if (isRefund) {
                //是否在满十件记录
                TradeCachePushKingdeeOrder tradeCachePushKingdeeOrder = tradeCachePushKingdeeOrderRepository.findByReturnOrderCachePushKingdeeOrder(returnOrder.getTid());
                if (Objects.nonNull(tradeCachePushKingdeeOrder)) {
                    tradeCachePushKingdeeOrderRepository.updateCachePushKingdeeOrderStatus(LocalDateTime.now(), tradeCachePushKingdeeOrder.getPushKingdeeId());
                    //取消乡镇件订单

                    List<HistoryTownShipOrder> tsOrderBytid = historyTownShipOrderRepository.getTsOrderBytid(returnOrder.getTid());
                    if (CollectionUtils.isNotEmpty(tsOrderBytid)) {
                        //已经推送wms了需要返回库存
                        verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                        verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
//                        verifyService.subSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),trade.getSaleType());
//                        verifyService.subSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),trade.getSaleType());
                    } else {
                        //没推送或者已经取消的订单
                        historyTownShipOrderService.reduceTownStock(returnOrder.getTid());
                    }
                    historyTownShipOrderService.CancelTownStock(returnOrder.getTid());
                    //修改状态
//                    historyTownShipOrderService.reduceTownStock(returnOrder.getTid());
                    orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
                } else {
                    //已经推送
                    List<HistoryTownShipOrder> tsOrderBytid = historyTownShipOrderRepository.getTsOrderBytid(returnOrder.getTid());
                    if (CollectionUtils.isNotEmpty(tsOrderBytid)) {
                        //已经推送wms了需要返回库存
                        verifyService.addSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
                        verifyService.addSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(), trade.getSaleType(),trade);
//                        verifyService.subSkuListStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),trade.getSaleType());
//                        verifyService.subSkuListStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId(),trade.getSaleType());
                    } else {
                        //没推送或者已经取消的订单
                        historyTownShipOrderService.reduceTownStock(returnOrder.getTid());
                    }
                    historyTownShipOrderService.CancelTownStock(returnOrder.getTid());

                    //推送取消订单
                    if (pushCancelOrder(returnOrder, trade.getWareHouseCode(),trade)) {
                        orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
                        returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                    } else {
                        //不为强制退款时，需要提示回滚
                        if (forceRefund == null || forceRefund != 1) {
                            throw new SbcRuntimeException(OrderErrorCode.ORDER_HAS_BEEN_PICK_IN_WMS, "申请退款失败，商品已拣货");
                        }
                    }
                }
            }/*else{
                //只有传false的时候不推送，空和true都需要推送
                if(Objects.isNull(returnOrder.getPushNeeded()) || returnOrder.getPushNeeded()) {
                    //推送退单
                    if (pushBackOrder(returnOrder,trade.getWareHouseCode(),trade)){
                        returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                    }else {
                        throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO,"申请退货失败，WMS接口异常");
                    }
                }
            }*/
        }
        //刷新进批次号
        returnOrder.getReturnItems().forEach(info -> {
            Optional<TradeItem> tradeItemOptional = trade.getTradeItems().stream().filter(tradeItem -> info
                    .getSkuId().equals(tradeItem.getSkuId())).findFirst();
            if (tradeItemOptional.isPresent()) {
                TradeItem tradeItem = tradeItemOptional.get();
                info.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
            }
        });

        BigDecimal canReturnPrice = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();

        //仅退款直接跳过审核生成退款单
        //仅退款直接跳过审核生成退款单，wms缺货，缺货商品生成退款单，退货单状态改为已审核
        if (isRefund || (Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats())) {
            returnOrder.setReturnFlowState(ReturnFlowState.AUDIT);
            refundOrderService.generateRefundOrderByEnity(returnOrder, returnOrder.getBuyer().getId(), canReturnPrice,
                    returnOrder.getPayType());
        }

        log.info("===balance is canReturnPrice: {}，returnOrder：{}", canReturnPrice, JSONObject.toJSONString(returnOrder));

        //添加退货单余额信息
        fillReturnBalancePriceAndCashPrice(returnOrder, trade,isRefund);
        //保存退单
        returnOrderService.addReturnOrder(returnOrder);

        this.operationLogMq.convertAndSend(operator, "创建退单", "创建退单");

        if (isRefund) {
            //扣除乡镇件库存
            if (trade.getVillageFlag()) {
                verifyService.subSkuVillagesStock(trade.getTradeItems(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
                verifyService.subSkuVillagesStock(trade.getGifts(), trade.getDeliverWay().equals(DeliverWay.PICK_SELF) ? trade.getTradeWareHouse().getWareId() : trade.getWareId());
            }
            //付款、乡镇件、配送到店、非湖南
            if(PayState.isPaid(trade.getTradeState().getPayState()) && tradeService.isTradeToStoreVillageFlag(trade) &&  DeliverWay.isDeliveryToStore(trade.getDeliverWay()) && !verifyService.isHunan(trade.getConsignee().getProvinceId())) {
                log.info("orderVillageAddDeliveryService.updateByReturn{}",trade.getId());
                orderVillageAddDeliveryService.updateByReturn(OrderVillageAddDeliveryQueryVO.builder().tradeId(returnOrder.getTid()).returnOrderId(returnOrder.getId()).delFlag(0).build());
            }
        }

        /*  autoDeliver(rid, operator);*/
//        insertES(returnOrder);

        if (operator.getPlatform() == Platform.BOSS) {
            audit(rid, operator);
        }

        // 创建退单时，发送MQ消息
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.TRUE)
                .customerId(trade.getBuyer().getId())
                .orderId(trade.getId())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnOrderFlow(sendMQRequest);

        //售后单提交成功发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_COMMIT_SUCCESS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount()
        );

        return rid;
    }

    /***
     * @desc  设置退货地址
     * @author shiy  2023/7/11 10:17
    */
    public void setReturnOrderAddress(ReturnOrder returnOrder, Trade trade) {
        CompanyMallReturnGoodsAddressVO returnGoodsAddressVO = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(trade.getSupplier().getStoreId()).build()).getContext().getReturnGoodsAddress();
        if(!Objects.isNull(returnGoodsAddressVO)) {
            ReturnOrderAddressVO returnOrderAddressVO = KsBeanUtil.copyPropertiesThird(returnGoodsAddressVO,ReturnOrderAddressVO.class);
            returnOrder.setReturnOrderAddressVO(returnOrderAddressVO);
        }
    }

    /**
     * 同步到价格商品列表
     *
     * @param returnOrder
     * @param rid
     */
    public void updateInventoryDetailSamountTrade(ReturnOrder returnOrder, int returnFlag, String rid) {
        List<InventoryDetailSamountTrade> collect = returnOrder.getReturnItems().stream().flatMap(returnItem -> returnItem.getInventoryDetailSamountTrades().stream()).collect(Collectors.toList());
        List<InventoryDetailSamountTrade> collect1 = collect.stream().filter(inventoryDetailSamountTrade -> Objects.equals(inventoryDetailSamountTrade.getReturnFlag(), 2)).collect(Collectors.toList());

        for (InventoryDetailSamountTrade inventoryDetailSamountTrade : collect1) {
            inventoryDetailSamountTrade.setReturnFlag(returnFlag);
            if (StringUtils.isNotEmpty(rid)) {
                inventoryDetailSamountTrade.setReturnId(rid);
            }
        }

        inventoryDetailSamountTradeService.updateInventoryDetailSamountReturnFlag(KsBeanUtil.convert(collect1, InventoryDetailSamountTradeVO.class));
    }

    /**
     * 赋值
     * -- returnOrder->returnItems->inventoryDetailSamountTrades
     *
     * @param returnOrder
     */
    private void assignReturnItemsInventoryDetailSamountTrades(ReturnOrder returnOrder) {
        if (!TradeActivityTypeEnum.TRADE.toActivityType().equals(returnOrder.getActivityType())) {
            return;
        }
        //获取订单商品价格信息与退货信息
        BaseResponse<InventoryDetailSamountTradeResponse> inventoryByOId = inventoryDetailSamountTradeProvider.getInventoryAdaptive(InventoryDetailSamountTradeRequest.builder().oid(returnOrder.getTid()).build());
        if (inventoryByOId.getContext() == null || CollectionUtils.isEmpty(inventoryByOId.getContext().getInventoryDetailSamountTradeVOS())) {
            throw new SbcRuntimeException("获取可退商品错误!");
        }
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        if (CollectionUtils.isEmpty(returnItems)) {
            return;
        }
        for (ReturnItem returnItem : returnItems) {
            //退货数量
            BigDecimal num = returnItem.getNum();
            if (num.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            //舍弃前端传值, 后端计算
            returnItem.setInventoryDetailSamountTrades(new ArrayList<>());

            List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS = inventoryByOId.getContext().getInventoryDetailSamountTradeVOS();
            List<InventoryDetailSamountTradeVO> collect = inventoryDetailSamountTradeVOS
                    .stream().filter(inventoryDetailSamountTradeVO -> StringUtils.equals(inventoryDetailSamountTradeVO.getGoodsInfoId(), returnItem.getSkuId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                return;
            }
            assignReturnInventoryDetailSamountTradeVOs(returnItem, collect, 1, num);
            assignReturnInventoryDetailSamountTradeVOs(returnItem, collect, 0, num);
        }
    }

    private void assignReturnInventoryDetailSamountTradeVOs(ReturnItem returnItem, List<InventoryDetailSamountTradeVO> collect, int moneyType, BigDecimal num) {
        //未退款
        int returnFlag = 0;
        List<InventoryDetailSamountTradeVO> collect2 = collect
                .stream()
                .filter(inventoryDetailSamountTradeVO -> Objects.equals(inventoryDetailSamountTradeVO.getMoneyType(), moneyType))
                .filter(inventoryDetailSamountTradeVO -> Objects.equals(inventoryDetailSamountTradeVO.getReturnFlag(), returnFlag))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect2) || collect2.size() < num.intValue()) {
            return;
        }

        List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS = collect2.subList(0, num.intValue());
        for (InventoryDetailSamountTradeVO inventoryDetailSamountTradeVO : inventoryDetailSamountTradeVOS) {
            inventoryDetailSamountTradeVO.setReturnFlag(2);
        }

        if (CollectionUtils.isEmpty(returnItem.getInventoryDetailSamountTrades())) {
            returnItem.setInventoryDetailSamountTrades(new ArrayList<>());
        }

        List<InventoryDetailSamountTrade> convert = KsBeanUtil.convert(inventoryDetailSamountTradeVOS, InventoryDetailSamountTrade.class);
        List<InventoryDetailSamountTrade> inventoryDetailSamountTradesTem = new ArrayList<>();
        log.info("convert:{}  inventoryDetailSamountTrades:{} ", JSONObject.toJSONString(convert), JSONObject.toJSONString(returnItem.getInventoryDetailSamountTrades()));

        inventoryDetailSamountTradesTem.addAll(returnItem.getInventoryDetailSamountTrades());
        inventoryDetailSamountTradesTem.addAll(convert);
        returnItem.setInventoryDetailSamountTrades(inventoryDetailSamountTradesTem);

        log.info("inventoryDetailSamountTrades:{} ", JSONObject.toJSONString(returnItem.getInventoryDetailSamountTrades()));
    }

    /**
     * 添加退货单余额信息
     *
     * @param returnOrder
     * @param trade
     */
    private void fillReturnBalancePriceAndCashPrice(ReturnOrder returnOrder, Trade trade,boolean isRefund) {
        Assert.notNull(returnOrder.getReturnItems(), "returnItems 不能为null");

        BigDecimal balanceReturnPrice = returnOrder.getReturnItems().stream()
                .flatMap(returnItem -> returnItem.getInventoryDetailSamountTrades().stream())
                .filter(inventoryDetailSamountTrade -> Objects.equals(inventoryDetailSamountTrade.getMoneyType(), 0))
                .map(InventoryDetailSamountTrade::getAmortizedExpenses)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashReturnPrice = returnOrder.getReturnItems().stream()
                .flatMap(returnItem -> returnItem.getInventoryDetailSamountTrades().stream())
                .filter(inventoryDetailSamountTrade -> Objects.equals(inventoryDetailSamountTrade.getMoneyType(), 1))
                .map(InventoryDetailSamountTrade::getAmortizedExpenses)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryPrice = BigDecimal.ZERO;
        BigDecimal packingPrice = BigDecimal.ZERO;
        BigDecimal villageAddliveryPrice =BigDecimal.ZERO;
        if (isRefund && !verifyService.isHunan(trade.getConsignee().getProvinceId())) {
            BigDecimal canReturnAddPrice = orderVillageAddDeliveryService.queryCanReturnAddPriceByCreateReturn(trade);
            if (canReturnAddPrice.compareTo(BigDecimal.ZERO) > 0) {
                if (Objects.nonNull(trade.getTradePrice().getVillageAddliveryPrice()) && trade.getTradePrice().getVillageAddliveryPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //本订单不减
                    //villageAddliveryPrice = trade.getTradePrice().getVillageAddliveryPrice();
                } else {
                    villageAddliveryPrice = canReturnAddPrice.negate();
                }
            }else{
                if (Objects.nonNull(trade.getTradePrice().getVillageAddliveryPrice()) && trade.getTradePrice().getVillageAddliveryPrice().compareTo(BigDecimal.ZERO) > 0) {
                    villageAddliveryPrice = trade.getTradePrice().getVillageAddliveryPrice();
                } else {
                }
            }
        }
        //未发货，需退运费
        if (isRefund) {
            deliveryPrice = trade.getTradePrice().getDeliveryPrice().subtract(villageAddliveryPrice);
        }
        //零售订单，微信支付和鲸币支付、线下支付，未发货退款，少退纸箱费
        if (isRefund && SaleType.RETAIL.equals(trade.getSaleType())) {
            packingPrice = trade.getTradePrice().getPackingPrice();
        }

        if (Objects.nonNull(returnOrder.getReturnPrice())) {
            //设置应退现金，应退余额
            returnOrder.getReturnPrice().setShouldReturnCash(cashReturnPrice);
            returnOrder.getReturnPrice().setActualReturnCash(cashReturnPrice);
            returnOrder.getReturnPrice().setBalanceReturnPrice(balanceReturnPrice);
            returnOrder.getReturnPrice().setActualBalanceReturnPrice(balanceReturnPrice);
            if(null!=returnOrder.getReturnPrice().getDeliveryPrice() && returnOrder.getReturnPrice().getDeliveryPrice().compareTo(BigDecimal.ZERO)>0){

            }else {
                returnOrder.getReturnPrice().setDeliveryPrice(deliveryPrice);
            }
            returnOrder.getReturnPrice().setPackingPrice(packingPrice);

            BigDecimal totalPrice = cashReturnPrice.add(balanceReturnPrice).add(deliveryPrice).add(packingPrice);
            returnOrder.getReturnPrice().setTotalPrice(totalPrice);
            returnOrder.getReturnPrice().setApplyPrice(totalPrice);
        }
    }

    private Boolean pushBackOrder(ReturnOrder param, String warehouseId, Trade trade) {
        if(!orderCommonService.wmsCanTrade(trade)){
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
            TradeItem tradeItemT = tradeItems.stream().filter(t -> t.getSkuId().equals(returnItem.getSkuId())).findFirst().orElse(null);
            //拆箱规格步长
            if (Objects.nonNull(returnItem.getDevanningId())) {
                tradeItemT = tradeItems.stream().filter(t -> t.getDevanningId().equals(returnItem.getDevanningId())).findFirst().orElse(null);
            }
            BigDecimal addStep =
                    tradeItemT != null && tradeItemT.getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP) != null ?
                            tradeItemT.getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP) :
                            returnItem.getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal returnItemPrice = returnItem.getPrice().divide(addStep, 2, BigDecimal.ROUND_HALF_UP);
            details.add(WMSChargeBackDetailsRequest.builder()
                    .sku(returnItem.getErpSkuNo())
                    .expectedQty(returnItem.getNum().multiply(addStep).setScale(2, BigDecimal.ROUND_HALF_UP))
                    .totalPrice(returnItemPrice)
                    .dedi06(Objects.nonNull(returnItem.getDevanningId()) ? returnItem.getDevanningId().toString() : null)
                    .referenceNo(param.getId())
                    .lineNo(lineNo.get())
                    .build());
            lineNo.incrementAndGet();
        });
        BaseResponse<ResponseWMSReturnResponse> result = null;

        String jobNo = WmsErpIdConstants.ERP_CUSTOMER_ID;
        if (StringUtils.isNotEmpty(trade.getBuyer().getEmployeeId())) {
            EmployeeByIdResponse response = employeeQueryProvider
                    .getById(EmployeeByIdRequest.builder().employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(response) && Objects.nonNull(response.getJobNo())) {
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
                .userDefine3(trade.getSupplier().getSupplierName())
                .asnReferenceC(orderCommonService.getWmsSoReference5(trade))
                .docNo(param.getId())
                .asnReferenceA(param.getTid())
                .asnCreationTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_1))
                .details(details)
                .build());

        return !Objects.isNull(result) && !Objects.isNull(result.getContext()) &&
                !Objects.isNull(result.getContext().getResponseWMSReturnVO()) &&
                AbstractXYYConstant.RESPONSE_SUCCESS.equals(result.getContext().getResponseWMSReturnVO().getReturnCode()) &&
                result.getContext().getResponseWMSReturnVO().getReturnFlag() > 0;
    }

    // 计算积分
    private Long getPoints(ReturnOrder returnOrder, Trade trade) {
        // 各商品均摊积分
        Map<String, Double> splitPointMap = new HashMap<>();
        // 各商品购买数量
        Map<String, Long> totalNumMap = new HashMap<>();
        // 各商品消耗积分
        Map<String, Long> pointsMap = new HashMap<>();
        List<TradeItem> tradeItems = trade.getTradeItems();
        for (TradeItem tradeItem : tradeItems) {
            Double splitPoint = new BigDecimal(tradeItem.getPoints())
                    .divide(new BigDecimal(tradeItem.getNum()), 2, BigDecimal.ROUND_DOWN)
                    .doubleValue();
            String skuId = tradeItem.getSkuId();
            splitPointMap.put(skuId, splitPoint);
            totalNumMap.put(skuId, tradeItem.getNum());
            pointsMap.put(skuId, tradeItem.getPoints());
        }

        List<ReturnItem> returnItems = returnOrder.getReturnItems();
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
     * 创建退单快照
     *
     * @param returnOrder
     * @param operator
     */
    @LcnTransaction
    public void transfer(ReturnOrder returnOrder, Operator operator) {
        Trade trade = tradeService.detail(returnOrder.getTid());
        //查询该订单所有退单
        List<ReturnOrder> returnOrderList = returnOrderRepository.findByTid(trade.getId());
        //过滤处理中的退单
        List<ReturnOrder> returnOrders = returnOrderList.stream().filter(item -> item.getReturnFlowState() !=
                ReturnFlowState.COMPLETED
                && item.getReturnFlowState() != ReturnFlowState.VOID
                && item.getReturnFlowState() != ReturnFlowState.REJECT_REFUND
                && item.getReturnFlowState() != ReturnFlowState.REJECT_RECEIVE
                && item.getReturnFlowState() != ReturnFlowState.REFUNDED)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(returnOrders)) {
            throw new SbcRuntimeException("K-050120");
        }

        verifyNum(trade, returnOrder.getReturnItems());
        returnOrder.setReturnType(ReturnType.RETURN);

        Buyer buyer = new Buyer();
        buyer.setId(operator.getUserId());

        returnOrder.setBuyer(buyer);

        //计算总金额
//        BigDecimal totalPrice = returnOrder.getReturnItems().stream().map(t -> new BigDecimal(t.getNum()).multiply
// (t.getPrice()))
//                .reduce(BigDecimal::add).get();
//        returnOrder.getReturnPrice().setTotalPrice(totalPrice);

        // 计算并设置需要退的赠品
        getAndSetReturnGifts(returnOrder, trade, returnOrderList);

        ReturnOrderTransfer returnOrderTransfer = new ReturnOrderTransfer();
        KsBeanUtil.copyProperties(returnOrder, returnOrderTransfer);
        delTransfer(operator.getUserId());
        returnOrderTransfer.setId(UUIDUtil.getUUID());
        returnOrderTransferService.addReturnOrderTransfer(returnOrderTransfer);
    }

    /**
     * 获取并设置本次退单需要退的赠品信息
     *
     * @param returnOrder     本次退单
     * @param trade           对应的订单信息
     * @param returnOrderList 订单对应的所有退单
     * @author bail
     */
    private void getAndSetReturnGifts(ReturnOrder returnOrder, Trade trade, List<ReturnOrder> returnOrderList) {
        List<TradeMarketingVO> tradeMarketings = trade.getTradeMarketings();
        if (CollectionUtils.isNotEmpty(tradeMarketings)) {
            // 1.找到原订单的所有满赠的营销活动marketingList
            List<TradeMarketingVO> giftMarketings = tradeMarketings.stream().filter(tradeMarketing -> MarketingType
                    .GIFT.equals(tradeMarketing.getMarketingType())).collect(Collectors.toList());
            if (giftMarketings.size() > 0) {
                //TODO:二期修改bug
                Map<String, TradeItem> tradeItemMap = trade.getTradeItems().stream().collect(Collectors.toMap
                        (TradeItem::getSkuId, Function.identity(), (oldVal, newVal) -> newVal));//原订单所有商品的Map,方便根据skuId快速找到对应的商品信息
                log.info("订单商品map" + tradeItemMap.toString());
                Map<String, TradeItem> giftItemMap = trade.getGifts().stream().collect(Collectors.toMap
                        (TradeItem::getSkuId, Function.identity()));//原订单所有赠品的Map,方便根据skuId快速找到对应的赠品信息
                List<ReturnOrder> comReturnOrders = filterFinishedReturnOrder(returnOrderList);//该订单之前已完成的退单list
                // (分批退单的场景)
                Map<String, ReturnItem> comReturnSkus = new HashMap<>();//已经退的商品汇总(根据skuId汇总所有商品的数量)
                Map<String, ReturnItem> currReturnSkus = returnOrder.getReturnItems().stream().collect(Collectors
                        .toMap(ReturnItem::getSkuId, Function.identity(), (oldVal, newVal) -> newVal));//本次退的商品汇总
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
                            if (Objects.nonNull(skuItem)) {
                                BigDecimal comReSkuCount = comReturnSkus.get(skuId) == null ? BigDecimal.ZERO : comReturnSkus.get(skuId)
                                        .getNum();
                                BigDecimal currReSkuCount = currReturnSkus.get(skuId) == null ? BigDecimal.ZERO : currReturnSkus.get(skuId)
                                        .getNum();
                                return (Objects.nonNull(skuItem) ? skuItem.getLevelPrice() : new BigDecimal(0)).multiply(BigDecimal.valueOf(skuItem.getDeliveredNum()).subtract(
                                        comReSkuCount).subtract(currReSkuCount));//某商品的发货商品价格 - 已退商品价格 - 当前准备退的商品价格
                            }
                            return new BigDecimal(10);

                        }).reduce(BigDecimal::add).get();//剩余商品价格汇总

                        // 3.若不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中(若满足满赠条件,则无需退赠品)
                        if (leftSkuAmount.compareTo(giftMarketing.getGiftLevel().getFullAmount()) < 0) {
                            setReturnGiftsMap(giftMarketing, allReturnGifts, giftItemMap);
                        }
                    } else if (MarketingSubType.GIFT_FULL_COUNT.equals(giftMarketing.getSubType())) {
                        long leftSkuCount = giftMarketing.getSkuIds().stream().mapToLong(skuId -> {
                            TradeItem skuItem = tradeItemMap.get(skuId);
                            if (Objects.nonNull(skuItem)) {

                                long comReSkuCount = comReturnSkus.get(skuId) == null ? 0L : comReturnSkus.get(skuId)
                                        .getNum().longValue();
                                long currReSkuCount = currReturnSkus.get(skuId) == null ? 0L : currReturnSkus.get(skuId)
                                        .getNum().longValue();
                                return (Objects.nonNull(skuItem) ? skuItem.getDeliveredNum() : 0L) - comReSkuCount - currReSkuCount;//某商品的发货商品数


                            }
                            //沒有数据说明营销活动的商品已经劝全退了放回大值使赠品不退款
                            return 10000L;
                        }).sum();//剩余商品数量汇总
                        // 3.若不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中(若满足满赠条件,则无需退赠品)
                        if (leftSkuCount < giftMarketing.getGiftLevel().getFullCount()) {
                            setReturnGiftsMap(giftMarketing, allReturnGifts, giftItemMap);
                        }
                    }
                    //满订单赠送还未开发完成故注释
//                    else if (MarketingSubType.GIFT_FULL_ORDER.equals(giftMarketing.getSubType())) {
//                        long leftSkuCount = giftMarketing.getSkuIds().stream().mapToLong(skuId -> {
//                            if("all".equals(skuId)){
//                                return 0;
//                            }
//                            TradeItem skuItem = tradeItemMap.get(skuId);
//                            long comReSkuCount = comReturnSkus.get(skuId) == null ? 0L : comReturnSkus.get(skuId)
//                                    .getNum().longValue();
//                            long currReSkuCount = currReturnSkus.get(skuId) == null ? 0L : currReturnSkus.get(skuId)
//                                    .getNum().longValue();
//                            return skuItem.getDeliveredNum() - comReSkuCount - currReSkuCount;//某商品的发货商品数
//                            // - 已退商品数 - 当前准备退的商品数
//                        }).sum();//剩余商品数量汇总
//
//                        // 3.若不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中(若满足满赠条件,则无需退赠品)
//                        if (leftSkuCount < giftMarketing.getGiftLevel().getFullCount()) {
//                            setReturnGiftsMap(giftMarketing, allReturnGifts, giftItemMap);
//                        }
//                    }

                });

                // 4.设置具体的退单赠品信息
                returnOrder.setReturnGifts(getReturnGiftList(trade, allReturnGifts, comReturnGifts));
            }
        }
    }

    /**
     * 不满足满赠条件时,需要退的所有赠品
     *
     * @param giftMarketing  某个满赠营销活动
     * @param allReturnGifts 不满足满赠条件,满赠营销活动中所有需要退的赠品信息  (可能需要退)
     * @param giftItemMap    赠品具体信息Map(获取除了skuId以外的详细信息)
     */
    private void setReturnGiftsMap(TradeMarketingVO giftMarketing, Map<String, ReturnItem> allReturnGifts, Map<String,
            TradeItem> giftItemMap) {
        // 不满足满赠条件,则退该活动的所有赠品,汇总到所有的退货赠品数量中
        giftMarketing.getGiftLevel().getFullGiftDetailList().stream().forEach(gift -> {
            ReturnItem currGiftItem = allReturnGifts.get(gift.getProductId());
            TradeItem giftDetail = giftItemMap.get(gift.getProductId());
            if (currGiftItem == null) {
                if (Objects.nonNull(giftDetail)) {
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
     * @param trade          订单
     * @param allReturnGifts 不满足满赠条件,满赠营销活动中所有需要退的赠品信息  （可能需要退的赠品）
     * @param comReturnGifts 所有已完成退单中的退掉的赠品信息
     * @return
     */
    private List<ReturnItem> getReturnGiftList(Trade trade, Map<String, ReturnItem> allReturnGifts, Map<String,
            ReturnItem> comReturnGifts) {
        // 本次退单的退货赠品总数: 每个商品所有退货赠品数量 - 之前所有退单中已经退掉的赠品总数
        //   PS: 为了保证退单中赠品顺序与订单中的赠品顺序一致,遍历订单赠品,依次计算得出本次退单需要退的赠品list
        List<ReturnItem> returnGiftList = trade.getGifts().stream().map(tradeItem -> {
            ReturnItem readyGiftItem = allReturnGifts.get(tradeItem.getSkuId());//准备退的
            ReturnItem comGiftItem = comReturnGifts.get(tradeItem.getSkuId());//之前已完成退单已经退掉的
            if (readyGiftItem != null) {
                BigDecimal totalNum = readyGiftItem.getNum().compareTo(BigDecimal.valueOf(tradeItem.getDeliveredNum())) < 0 ? readyGiftItem.getNum
                        () : BigDecimal.valueOf(tradeItem.getDeliveredNum());//退货总数 与 发货总数对比,取最小的值
                //仅退款数量设置为赠品总数
                if (trade.getTradeState().getDeliverStatus() == DeliverStatus.NOT_YET_SHIPPED || trade
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

    @Resource
    private MongoTccHelper mongoTccHelper;

    @SuppressWarnings("unused")
    public void confirmDelTransfer(String userId) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelDelTransfer(String userId) {
        mongoTccHelper.cancel();
    }

    /**
     * 删除订单快照
     *
     * @param userId
     */
    @LcnTransaction
    @TccTransaction
    public void delTransfer(String userId) {
        ReturnOrderTransfer returnOrderTransferByBuyerId = returnOrderTransferRepository.findReturnOrderTransferByBuyerId(userId);
        if (Objects.nonNull(returnOrderTransferByBuyerId)) {
            returnOrderTransferService.deleteReturnOrderTransfer(returnOrderTransferByBuyerId.getId());
        }
    }

    /**
     * 查询退单快照
     *
     * @param userId
     * @return
     */
    public ReturnOrder findTransfer(String userId) {
        ReturnOrder returnOrder = null;
        ReturnOrderTransfer returnOrderTransfer = returnOrderTransferRepository.findReturnOrderTransferByBuyerId
                (userId);
        if (returnOrderTransfer != null) {
            returnOrder = new ReturnOrder();
            KsBeanUtil.copyProperties(returnOrderTransfer, returnOrder);
        }
        return returnOrder;
    }

    /*@EsCacheAnnotation(name = "returnOrderESCacheService")
    public void insertES(ReturnOrder returnOrder) {
        returnOrderESRepository.delete(returnOrder.getId());
        returnOrderESRepository.index(returnOrder);
    }

    @EsCacheAnnotation(name = "returnOrderESCacheService")
    public void deleteES(ReturnOrder returnOrder) {
        returnOrderESRepository.delete(returnOrder.getId());
    }*/

    /**
     * 创建退货单
     *
     * @param returnOrder
     */
    private void createReturn(ReturnOrder returnOrder, Operator operator, Trade trade) {

        // 新增订单日志
        tradeService.returnOrder(returnOrder.getTid(), operator);

        this.verifyNum(trade, returnOrder.getReturnItems());

        returnOrder.setReturnType(ReturnType.RETURN);

        //填充退货商品信息
        Map<String, BigDecimal> map = findLeftItems(trade);
        Map<Long, BigDecimal> devanningMap = Maps.newHashMap();
        //拆箱处理
        TradeItem devanTradeItem = trade.getTradeItems().stream().findAny().orElse(null);
        if (Objects.nonNull(devanTradeItem.getDevanningId())) {
            devanningMap = this.findLeftDevanningItems(trade);
        }
//        Map<Long, BigDecimal>devanningMap = findLeftDevanningItems(trade);
        Map<Long, BigDecimal> finalDevanningMap = devanningMap;
        returnOrder.getReturnItems().forEach(item ->
                {
                    List<TradeItem> tradeItems = trade.skuItemMap().get(item.getSkuId());
                    if (Objects.nonNull(item.getDevanningId())) {
                        tradeItems = trade.skuDevanningItemMap().get(item.getDevanningId());
                    }
                    item.setSkuName(tradeItems.stream().findAny().get().getSkuName());
                    item.setPic(tradeItems.stream().findAny().get().getPic());
                    item.setSkuNo(tradeItems.stream().findAny().get().getSkuNo());
                    item.setSpecDetails(tradeItems.stream().findAny().get().getSpecDetails());
                    item.setUnit(tradeItems.stream().findAny().get().getUnit());

                    for (TradeItem tradeItem : tradeItems) {
                        if (Objects.nonNull(item.getDevanningId()) && Objects.nonNull(tradeItem.getDevanningId())
                                && item.getDevanningId() == tradeItem.getDevanningId()) {
                            item.setGoodsSubtitle(tradeItem.getGoodsSubtitle());
                        } else {
                            item.setGoodsSubtitle(tradeItem.getGoodsSubtitle());
                        }
                    }
                    item.setCanReturnNum(map.get(item.getSkuId()));
                    if (Objects.nonNull(item.getDevanningId())) {
                        item.setCanReturnNum(finalDevanningMap.get(item.getDevanningId()));
                    }
                }
        );

    }

    /**
     * 创建退款单
     *
     * @param returnOrder
     */
    private void createWMSRefund(ReturnOrder returnOrder, Operator operator, Trade trade) {

        //校验该订单关联的退款单状态
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(trade.getId());
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<ReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                            ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        // 新增订单日志
        //tradeService.returnOrder(returnOrder.getTid(), operator);
        returnOrder.setReturnType(ReturnType.REFUND);
        //设置退货商品
        returnOrder.getReturnItems().forEach(returnItem -> {

            //拆箱
            if (Objects.nonNull(returnItem.getDevanningId())) {
                for (TradeItem item : trade.getTradeItems()) {
                    if (item.getDevanningId().equals(returnItem.getDevanningId())) {
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
            } else {
                //正常
                for (TradeItem item : trade.getTradeItems()) {
                    if (item.getSkuNo().equals(returnItem.getSkuNo())) {
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
            }
        });
    }

    /**
     * 创建拆箱退款单
     *
     * @param returnOrder
     */
    private void devanningCreateWMSRefund(ReturnOrder returnOrder, Operator operator, Trade trade) {

        //校验该订单关联的退款单状态
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(trade.getId());
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<ReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                            ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }

        // 新增订单日志
        //tradeService.returnOrder(returnOrder.getTid(), operator);
        returnOrder.setReturnType(ReturnType.REFUND);
        //设置退货商品
        returnOrder.getReturnItems().forEach(returnItem -> {
            for (TradeItem item : trade.getTradeItems()) {
                if (item.getDevanningId().equals(returnItem.getDevanningId())) {
                    returnItem.setSkuId(item.getSkuId());
                    returnItem.setDevanningId(item.getDevanningId());
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
     * @param returnOrder
     */
    private void createRefund(ReturnOrder returnOrder, Operator operator, Trade trade) {
        //校验该订单关联的退款单状态
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(trade.getId());
        if (!CollectionUtils.isEmpty(returnOrders)) {
            Optional<ReturnOrder> optional = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType
                    .REFUND).filter(item ->
                    !(item.getReturnFlowState() == ReturnFlowState.VOID || item.getReturnFlowState() ==
                            ReturnFlowState.REJECT_REFUND))
                    .findFirst();
            if (optional.isPresent()) {
                throw new SbcRuntimeException("K-050115");
            }
        }
        // 新增订单日志
        tradeService.returnOrder(returnOrder.getTid(), operator);
        returnOrder.setReturnType(ReturnType.REFUND);

        //设置退货商品
        returnOrder.setReturnItems(trade.getTradeItems().stream().map(item -> ReturnItem.builder()
                .num(BigDecimal.valueOf(item.getNum()))
                .skuId(item.getSkuId())
                .skuNo(item.getSkuNo())
                .devanningId(item.getDevanningId())
                .pic(item.getPic())
                .skuName(item.getSkuName())
                .unit(item.getUnit())
                .price(item.getPrice())
                .erpSkuNo(item.getErpSkuNo())
                .splitPrice(item.getSplitPrice())
                .specDetails(item.getSpecDetails())
                .goodsSubtitle(item.getGoodsSubtitle())
                .inventoryDetailSamountTrades(returnOrder.getReturnItems().stream().filter(returnItem -> StringUtils.equals(returnItem.getSkuId(), item.getSkuId()))
                        .findFirst().orElse(new ReturnItem()).getInventoryDetailSamountTrades())
                .build()).collect(Collectors.toList())
        );

    }

    /**
     * 根据动态条件统计
     *
     * @param request
     * @return
     */
    public long countNum(ReturnQueryRequest request) {
        Query query = new Query(request.build());
        return mongoTemplate.count(query, ReturnOrder.class);
    }

    /**
     * 分页查询退单列表
     *
     * @param request
     * @return
     */
    public Page<ReturnOrder> page(ReturnQueryRequest request) {
        Query query = new Query(request.build());
        long total = this.countNum(request);
        if (total < 1) {
            return new PageImpl<>(new ArrayList<>(), request.getPageRequest(), total);
        }
        request.putSort(request.getSortColumn(), request.getSortRole());
        List<ReturnOrder> returnOrderList = mongoTemplate.find(query.with(request.getPageRequest()), ReturnOrder.class);
        fillActualReturnPrice(returnOrderList);

        // 填充退款单状态
        if (CollectionUtils.isNotEmpty(returnOrderList)) {
            List<String> ridList = returnOrderList.stream().map(ReturnOrder::getId).collect(Collectors.toList());
            RefundOrderRequest refundOrderRequest = new RefundOrderRequest();
            refundOrderRequest.setReturnOrderCodes(ridList);
            List<RefundOrder> refundOrders = refundOrderService.findAll(refundOrderRequest);
            returnOrderList.forEach(returnOrder -> {
                Optional<RefundOrder> refundOrderOptional = refundOrders.stream().filter(refundOrder -> refundOrder
                        .getReturnOrderCode().equals(returnOrder.getId())).findFirst();

                refundOrderOptional.ifPresent(refundOrder -> returnOrder.setRefundStatus(refundOrder.getRefundStatus
                        ()));
                //填充订单类型
                //TODO 测试打印日志上线删除
                log.info("====================错误id" + returnOrder.getId());
                log.info("====================错误id" + returnOrder.getTid());
                Trade trade = tradeService.detail(returnOrder.getTid());
                if(Objects.nonNull(trade)){
                    returnOrder.setMergFlag(trade.getMergFlag());
                    returnOrder.setTids(trade.getTids());
                    if (StringUtils.isNotBlank(trade.getActivityType())) {
                        returnOrder.setActivityType(trade.getActivityType());
                    }
                    if (StringUtils.isNotEmpty(trade.getActivityType())
                            && trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())
                            && Objects.nonNull(returnOrder.getWmsStats()) && returnOrder.getWmsStats()) {
                        returnOrder.getReturnPrice().setActualReturnPrice(BigDecimal.ZERO);
                    }
                }else{
                    //TODO 测试打印日志上线删除
                    log.info("====================else错误id" + returnOrder.getId());
                    log.info("====================else错误id" + returnOrder.getTid());
                }
            });
        }
        return new PageImpl<>(returnOrderList, request.getPageable(), total);
    }

    public List<ReturnOrder> findByCondition(ReturnQueryRequest request) {
        return mongoTemplate.find(new Query(request.build()), ReturnOrder.class);
    }

    /**
     * 填充实际退款金额，捕获异常，避免对主列表产生影响
     *
     * @param iterable
     */
    private void fillActualReturnPrice(Iterable<ReturnOrder> iterable) {
        try {
            List<String> returnOrderCodes = new ArrayList<>();
            // 如果有已退款的，查询退款流水的金额
            iterable.forEach(returnOrder -> {
                if (returnOrder.getReturnFlowState() == ReturnFlowState.COMPLETED) {
                    returnOrderCodes.add(returnOrder.getId());
                }
            });

            if (returnOrderCodes.size() > 0) {
                RefundOrderRequest request = new RefundOrderRequest();
                request.setReturnOrderCodes(returnOrderCodes);
                // 查询退款单信息
                List<RefundOrder> refundOrderList = refundOrderService.findAll(request);

                if (!CollectionUtils.isEmpty(refundOrderList)) {

                    // 实退金额赋值
                    iterable.forEach(returnOrder ->
                            refundOrderList.stream()
                                    .filter(o -> Objects.equals(returnOrder.getId(), o.getReturnOrderCode()))
                                    .findFirst()
                                    .ifPresent(o -> {
                                        if (Objects.nonNull(o.getRefundBill())) {
                                            returnOrder.getReturnPrice().setActualReturnPrice(o.getRefundBill()
                                                    .getActualReturnPrice());
                                        }
                                    }));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ReturnPileOrder findReturnPileOrderById(String rid) {
        ReturnPileOrder returnOrder = returnPileOrderRepository.findById(rid).orElse(null);
        if (returnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }
        return returnOrder;
    }

    public ReturnOrder findById(String rid) {
        ReturnOrder returnOrder = returnOrderRepository.findById(rid).orElse(null);
        if (returnOrder == null) {
            throw new SbcRuntimeException("K-050003");
        }
        return returnOrder;
    }

    public ReturnOrder findByIdNoException(String rid) {
        return returnOrderRepository.findById(rid).orElse(null);
    }

    /**
     * 批量查询退货单
     *
     * @param returnOrders
     * @return
     */
    public List<ReturnOrder> findAllById(List<String> returnOrders) {
        return org.apache.commons.collections4.IteratorUtils.toList(returnOrderRepository.findAllById(returnOrders).iterator());
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

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CoinActivityProvider coinActivityProvider;


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
        ReturnOrder returnOrder = findById(rid);
        // 查询订单相关的所有退单
        List<ReturnOrder> returnAllOrders = returnOrderRepository.findByTid(returnOrder.getTid());
        //查询相关订单信息
        Trade trade = tradeService.detail(returnOrder.getTid());
        List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<CoinActivityRecordDetailDto> coinDetails = coinActivityProvider.queryCoinActivityRecordByOrderIdAndSkuIds(trade.getId(), skuIds).getContext();
        if (CollectionUtils.isNotEmpty(coinDetails)) {
            Map<String, ReturnItem> returnItemMap = returnOrder.getReturnItems().stream().collect(Collectors.toMap(ReturnItem::getSkuId, Function.identity(), (o1, o2) -> o1));
            Map<String, TradeItem> tradeItemMap = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId, Function.identity(), (o1, o2) -> o1));
            // BigDecimal returnCoin = BigDecimal.ZERO;
            // BigDecimal returnCoin = coinDetails.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<ReturnItem> returnItemList = new ArrayList<>();
            returnAllOrders.stream().filter(allOrder -> allOrder.getReturnFlowState() ==  ReturnFlowState.COMPLETED).forEach(o -> {
                returnItemList.addAll(o.getReturnItems());
            });

            Map<String, BigDecimal> returnItemCountMap = returnItemList.stream().collect(Collectors.groupingBy(ReturnItem::getSkuId, Collectors.mapping(ReturnItem::getNum, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
            coinDetails = coinDetails.stream().filter(o -> {
                if (Objects.equals(DefaultFlag.YES, o.getIsOverlap())) {
                    return true;
                } else {
                    // 不叠加的 查询已退的数量
                    BigDecimal cancelNum = BigDecimal.ZERO;
                    if (Objects.nonNull(returnItemCountMap.get(o.getGoodsInfoId()))) {
                        cancelNum = returnItemCountMap.get(o.getGoodsInfoId());
                    }

                    BigDecimal cancelNumber = returnItemMap.get(o.getGoodsInfoId()).getNum().add(cancelNum);
                    Long tradeNum = tradeItemMap.get(o.getGoodsInfoId()).getNum();
                    if (cancelNumber.compareTo(BigDecimal.valueOf(tradeNum)) >= 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }).collect(Collectors.toList());
            BigDecimal returnCoin = coinDetails.stream().map(o -> {
                if (Objects.equals(DefaultFlag.YES, o.getIsOverlap())) {
                    return o.getSingleCoinNum().multiply(returnItemMap.get(o.getGoodsInfoId()).getNum());
                } else {
                    return o.getCoinNum();
                }
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (returnCoin.compareTo(BigDecimal.ZERO) > 0) {
                CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                        .getContext().getCusWalletVO();
                if (returnCoin.compareTo(cusWalletVO.getBalance()) > 0) {
                    throw new SbcRuntimeException("K-050601");
                }
                // 修改钱包
//                String buyerId = trade.getBuyer().getId();
//                ModifyWalletBalanceForCoinActivityRequest modifyBalanceRequest = new ModifyWalletBalanceForCoinActivityRequest();
//                modifyBalanceRequest.setAmount(returnCoin);
//                modifyBalanceRequest.setRelationId(rid);
//                modifyBalanceRequest.setBuyerId(buyerId);
//                modifyBalanceRequest.setCustomerAccount(trade.getBuyer().getAccount());
//                modifyBalanceRequest.setWalletDetailsType(WalletDetailsType.CANCEL_GOODS_RECHARGE);
//                modifyBalanceRequest.setBudgetType(BudgetType.EXPENDITURE);
//                customerWalletProvider.modifyWalletBalanceForCoin(modifyBalanceRequest);

            	// 取消返鲸币商家鲸币余额增加，用户鲸币余额减少
                Long storeId = trade.getSupplier().getStoreId();
				String tradeRemark = WalletDetailsType.CANCEL_GOODS_RECHARGE.getDesc() + "-" + rid;
				String customerId = trade.getBuyer().getId();
				CustomerWalletOrderByRequest orderByRequest = CustomerWalletOrderByRequest.builder()
						.customerId(customerId).storeId(storeId.toString()).balance(returnCoin)
						.relationOrderId(rid).tradeRemark(tradeRemark).remark(tradeRemark)
						.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
						.build();
				BaseResponse<WalletRecordVO> orderByGiveStore = customerWalletProvider.orderByGiveStore(orderByRequest);

//                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//                String sendNo = "TZS" + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
				String sendNo = orderByGiveStore.getContext().getSendNo();

                //  保存退回记录
                String customerAccount = trade.getBuyer().getAccount();
                LocalDateTime returnTime = returnOrder.getCreateTime();
                BigDecimal returnPrice = returnOrder.getReturnPrice().getTotalPrice();
                LocalDateTime now = LocalDateTime.now();
                Map<String, List<CoinActivityRecordDetailDto>> detailMap = coinDetails.stream().collect(Collectors.groupingBy(CoinActivityRecordDetailDto::getActivityId));

                List<CoinActivityRecordDto> saveRequest = new ArrayList<>();
                detailMap.forEach((k, v) -> {
                    CoinActivityRecordDto recordDto = new CoinActivityRecordDto();
                    recordDto.setSendNo(sendNo);
                    recordDto.setActivityId(k);
                    recordDto.setCustomerAccount(customerAccount);
                    recordDto.setOrderNo(rid);
                    recordDto.setOrderTime(returnTime);
                    recordDto.setOrderPrice(returnPrice);
                    recordDto.setRecordType(2);
                    recordDto.setRecordTime(now);

                    List<CoinActivityRecordDetailDto> detailDtoList = new ArrayList<>();
                    for (CoinActivityRecordDetailDto detailDto : v) {
                        detailDto.setDetailId(null);
                        detailDto.setRecordId(null);
                        detailDto.setOrderNo(rid);
                        detailDto.setRecordType(2);
                        detailDto.setRecordTime(now);
                        detailDto.setGoodsNum(returnItemMap.get(detailDto.getGoodsInfoId()).getNum().longValue());
                        if (Objects.equals(DefaultFlag.YES, detailDto.getIsOverlap())) {
                            detailDto.setCoinNum(detailDto.getSingleCoinNum().multiply(returnItemMap.get(detailDto.getGoodsInfoId()).getNum()));
                        }

                        detailDtoList.add(detailDto);
                    }

                    recordDto.setCoinNum(returnCoin);
                    recordDto.setDetailList(detailDtoList);
                    saveRequest.add(recordDto);

                });
                coinActivityProvider.saveCoinRecord(saveRequest);

                // 推送金蝶
//                CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//                        .builder()
//                        .tid(rid)
//                        .buyerAccount(trade.getBuyer().getAccount())
//                        .applyPrice(returnCoin)
//                        .saleType(trade.getSaleType())
//                        .sendNo(sendNo)
//                        .build();
//
//                pushKingdeeService.pushRefundOrderKingdeeForCoin(kingdeeRequest);
            }
        }
        
        // 订单返鲸币活动鲸币收回
        coinActivityProvider.takeBackOrderCoin(TakeBackOrderCoinRequest.builder().rid(rid).needThrowException(true).build());

        BigDecimal balancePrice = trade.getTradePrice().getBalancePrice() == null ? BigDecimal.ZERO : trade.getTradePrice().getBalancePrice();
        // 筛选出已完成的退单
        List<ReturnOrder> returnOrders = returnAllOrders.stream().filter(allOrder -> allOrder.getReturnFlowState() ==
                ReturnFlowState.COMPLETED)
                .collect(Collectors.toList());
        //计算所有已完成的退单总价格
        BigDecimal allOldPrice = new BigDecimal(0);
        for (ReturnOrder order : returnOrders) {
            BigDecimal p = order.getReturnPrice().getApplyStatus() ? order.getReturnPrice().getApplyPrice() : order
                    .getReturnPrice().getTotalPrice();
            allOldPrice = allOldPrice.add(p);
        }
        ReturnPrice returnPrice = returnOrder.getReturnPrice();

        BigDecimal price = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
        //查询订单支付流水表
        PayOrder payOrder = payOrderService.findPayOrderByOrderCode(returnOrder.getTid()).get();

        log.info("payOrderPrice: {}  balancePrice: {}  price: {}  allOldPrice: {}  ", payOrder.getPayOrderPrice(), balancePrice, price, allOldPrice);
        if (!trade.getActivityType().equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType())) {
            //提货代客退单审核不需要校验
            if (Objects.isNull(trade.getActivityType()) || !TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
                // 退单金额校验 退款金额不可大于可退金额
                if (payOrder.getPayType() == PayType.ONLINE && payOrder.getPayOrderPrice().add(balancePrice).compareTo(price.add(allOldPrice)) == -1) {
                    throw new SbcRuntimeException("K-050126");
                }
            }
        }

        if (returnOrder.getReturnType() == ReturnType.REFUND) {
            refundOrderService.generateRefundOrderByReturnOrderCode(rid, returnOrder.getBuyer().getId(), price,
                    returnOrder.getPayType());
        }
        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.AUDIT)
                .build();
        returnFSMService.changeState(request);
        //自动发货
        autoDeliver(rid, operator);

        //wms三方推送
        if (!kingdeeOpenState) {//判断是否开启新金蝶
            logger.info("ReturnOrderService.audit  kingdeeOpenState:{}", kingdeeOpenState);
            if (wmsPushFlag) {
                Trade trades = tradeService.detail(returnOrder.getTid());
                //只有传false的时候不推送，空和true都需要推送
                if (Objects.isNull(returnOrder.getPushNeeded()) || returnOrder.getPushNeeded()) {
                    //推送退单
                    if (pushBackOrder(returnOrder, trades.getWareHouseCode(), trades)) {
                        returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
                    } else {
                        throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO, "申请退货失败，WMS接口异常");
                    }
                }

            }
        }

        logger.info("ReturnOrderService.audit returnOrder.getId:{}", returnOrder.getId());
        if (kingdeeOpenState) {//判断是否开启新金蝶
            if (pushAuditKingdee(returnOrder)) {
                returnOrder.setWmsPushState(WMSPushState.BACK_ORDER_SUCCESS);
            }
//            else {
//                throw new SbcRuntimeException(OrderErrorCode.ORDER_PUSH_ERRO, "申请退货失败");
//            }
        }

        //售后审核通过发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        this.sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_CHECK_PASS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount());

    }


    /**
     * push退货单给金蝶
     */
    public Boolean pushAuditKingdee(ReturnOrder returnOrder) {
        logger.info("ReturnOrderService.pushAuditKingdee req Ptid:{} returnOrderId:{}", returnOrder.getTid(), returnOrder.getId());
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            logger.info("ReturnOrderService.pushAuditKingdee exit Ptid:{} returnOrderId:{}", returnOrder.getTid(), returnOrder.getId());
            return true;//非自营商家不考虑
        }
        Integer number = tradePushKingdeeReturnGoodsRepository.selcetPushKingdeeReturnGoodsNumber(returnOrder.getId());
        TradePushKingdeeReturnGoods pushKingdeeReturnGoods = new TradePushKingdeeReturnGoods();
        pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Boolean result = false;
        try {
            if (!checkAuditKingdee(returnOrder, pushKingdeeReturnGoods)) {
                pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return result;
            }
            Trade trade = tradeService.detail(returnOrder.getTid());
            LocalDateTime tradeCreateTime = trade.getTradeState().getCreateTime();
            LocalDateTime multiStartTime= tradeService.getMultiSpeciStartTime();
            boolean isMultiBeforeTradeCreate = tradeCreateTime.compareTo(multiStartTime)<1;
            logger.info("ReturnOrderService.pushAuditKingdee trade.getActivityType:{}", trade.getActivityType());
            List<PileStockRecordAttachment> recordAttachments = new ArrayList<>();
            if (Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
                recordAttachments.addAll(getReturnGoodsLinkedData(returnOrder));
            }
            if (trade != null) {
                addReturnOrderErp(returnOrder, trade,isMultiBeforeTradeCreate);
            }
            KingdeeAuditOrder order = new KingdeeAuditOrder();
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(employeeResponse) || StringUtils.isNotEmpty(employeeResponse.getErpEmployeeId())) {
                Map fSalerId = new HashMap();
                fSalerId.put("FNumber", employeeResponse.getErpEmployeeId());
                order.setFSalerId(fSalerId);
            }
            order.setOrderNumber(returnOrder.getId());
            order.setFSaleNum(returnOrder.getTid());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
            order.setFRetcustId(FCustId);//客户
            order.setFDate(DateUtil.nowDate());
            Map FSaleOrgId = new HashMap();
            FSaleOrgId.put("FNumber", kingdeeOrganization);
            order.setFSaleOrgId(FSaleOrgId);//组织
            //仓库id
            Map FStockId = new HashMap();
//            if("WH01".equals(trade.getWareHouseCode())){
//                FStockId.put("FNumber",ERPWMSConstants.MAIN_WH);
//            }else{
//                FStockId.put("FNumber",ERPWMSConstants.SUB_WH);
//            }

            if ("WH01".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
            } else if ("WH02".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
            } else if ("WH03".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.STORE_WH);
            } else {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "推送金蝶退货找不到对应仓库ID.");
            }
            order.setFStockId(FStockId);
            //退货备注
            if (Objects.nonNull(returnOrder.getReturnReason())) {
                order.setFNote(returnOrder.getReturnReason().getDesc());
            }
            List<AuditOrderEntry> fSaleOrderEntry = new ArrayList<>();
            //购买商品
            if (returnOrder.getReturnItems().size() > 0) {
                Map<String, BigDecimal> returnGoodsMap = new HashMap();

                List<ReturnItem> returnItems = returnOrder.getReturnItems();//购买商品
                //赠送商品
                List<ReturnItem> returnGifts = returnOrder.getReturnGifts();
                if (CollectionUtils.isNotEmpty(returnGifts) && returnGifts.size() > 0) {
                    returnGifts.forEach(returnItem -> {
                        returnItem.setFIsFree(true);
                        returnItems.add(returnItem);
                    });
                }
//                Iterator it = returnItems.iterator();
//                while (it.hasNext()) {
//                    log.info("ReturnOrderService.pushAuditKingdee 合并增送商品的集合"+it.next().toString());
//                }
                Boolean sPflag = trade.getSaleType().equals(SaleType.BULK);
                for (ReturnItem item : returnItems) {
                    log.info("退款数据==============="+JSONObject.toJSONString(item));
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    if (Objects.isNull(prefix)) {
                        //零售仓库，默认001
                        prefix = "001-";
                    }
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix, ""));
                    if (!checkAuditKingdeeGoods(item, pushKingdeeReturnGoods)) {
                        pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return result;
                    }
                    if (CollectionUtils.isNotEmpty(recordAttachments)) {
                        AuditOrderEntry orderEntry = new AuditOrderEntry();
                        for (PileStockRecordAttachment stockRecordAttachment : recordAttachments) {
                            BigDecimal num = returnGoodsMap.get(item.getSkuId());
                            if (num == null) {
                                num = BigDecimal.ZERO;
                            }
                            if (item.getSkuId().equals(stockRecordAttachment.getSkuId()) && item.getNum().compareTo(num) == 1) {
                                AuditOrderEntry associatedOrderEntry = new AuditOrderEntry();
                                //还需要推多少商品
                                BigDecimal useNum = item.getNum().subtract(num);
                                //关联数据量是否大于还需要推多少商品
                                if ((BigDecimal.valueOf(stockRecordAttachment.getNum()).subtract(useNum)).compareTo(BigDecimal.ZERO) == 1) {
                                    associatedOrderEntry.setFRealQty(useNum);
                                    returnGoodsMap.put(item.getSkuId(), useNum.add(num));
                                } else {
                                    associatedOrderEntry.setFRealQty(BigDecimal.valueOf(stockRecordAttachment.getNum()));
                                    returnGoodsMap.put(item.getSkuId(), BigDecimal.valueOf(stockRecordAttachment.getNum()).add(num));
                                }
                                Map FMaterialId = new HashMap();
                                FMaterialId.put("FNumber", item.getErpSkuNo());
                                associatedOrderEntry.setFMaterialId(FMaterialId);
                                Map FSStockId = new HashMap();
                                if (Objects.nonNull(item.getGoodsInfoType()) && 1 == item.getGoodsInfoType()) {
                                    FSStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                                } else {
//                                    FSStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
//                                    if("WH01".equals(trade.getWareHouseCode())){
//                                        FSStockId.put("FNumber",ERPWMSConstants.MAIN_WH);
//                                    }else{
//                                        FSStockId.put("FNumber",ERPWMSConstants.SUB_WH);
//                                    }
                                    if ("WH01".equals(trade.getWareHouseCode())) {
                                        FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                                    } else if ("WH02".equals(trade.getWareHouseCode())) {
                                        FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
                                    } else if ("WH03".equals(trade.getWareHouseCode())) {
                                        FStockId.put("FNumber", ERPWMSConstants.STORE_WH);
                                    } else {
                                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "推送金蝶退货找不到对应仓库ID.");
                                    }
                                }
                                associatedOrderEntry.setFStockId(FSStockId);//仓库
                                if (Objects.nonNull(item.getGoodsBatchNo())) {
                                    Map FLot = new HashMap();
                                    FLot.put("FNumber", item.getGoodsBatchNo());
                                    associatedOrderEntry.setFLot(FLot);
                                    associatedOrderEntry.setFProduceDate(item.getGoodsBatchNo());
                                }
                                associatedOrderEntry.setFTaxPrice(stockRecordAttachment.getPrice());
                                associatedOrderEntry.setF_According(stockRecordAttachment.getOrderCode());
                                //是否为赠品
                                if (stockRecordAttachment.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                                    associatedOrderEntry.setFIsFree(true);
                                } else {
                                    associatedOrderEntry.setFIsFree(item.getFIsFree());
                                }

                                log.info("ReturnOrderService.pushAuditKingdee returnGoodsMap:{}", returnGoodsMap.get(item.getSkuId()), returnOrder.getId());
                                fSaleOrderEntry.add(associatedOrderEntry);
                                log.info("ReturnOrderService.pushAuditKingdee returnGoodsEntity:{}" + associatedOrderEntry.toString());
                            }
                        }
                    } else {
                        AuditOrderEntry orderEntry = new AuditOrderEntry();
                        //没有关联数据
                        Map FMaterialId = new HashMap();
                        FMaterialId.put("FNumber", item.getErpSkuNo());
                        orderEntry.setFMaterialId(FMaterialId);
                        orderEntry.setFRealQty(item.getNum());
                        if (isMultiBeforeTradeCreate && sPflag){
                            orderEntry.setFRealQty(item.getNum().multiply(item.getAddStep()));
                        }

                        Map FSStockId = new HashMap();
                        if (Objects.nonNull(item.getGoodsInfoType()) && 1 == item.getGoodsInfoType()) {
                            FSStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                        } else {
//                            FSStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
//                            if("WH01".equals(trade.getWareHouseCode())){
//                                FSStockId.put("FNumber",ERPWMSConstants.MAIN_WH);
//                            }else{
//                                FSStockId.put("FNumber",ERPWMSConstants.SUB_WH);
//                            }
                            if ("WH01".equals(trade.getWareHouseCode())) {
                                FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                            } else if ("WH02".equals(trade.getWareHouseCode())) {
                                FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
                            } else if ("WH03".equals(trade.getWareHouseCode())) {
                                FStockId.put("FNumber", ERPWMSConstants.STORE_WH);
                            } else {
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "推送金蝶退货找不到对应仓库ID.");
                            }
                        }
                        orderEntry.setFStockId(FSStockId);//仓库
                        if (Objects.nonNull(item.getGoodsBatchNo())) {
                            Map FLot = new HashMap();
                            FLot.put("FNumber", item.getGoodsBatchNo());
                            orderEntry.setFLot(FLot);
                            orderEntry.setFProduceDate(item.getGoodsBatchNo());
                        }
                        //设置拆箱主键
                        if (Objects.nonNull(item.getDevanningId())) {
                            orderEntry.setFSkucxId(item.getDevanningId().toString());
                        }
                        //拆箱价格*2
                        if(orderEntry.getFRealQty().doubleValue() % 1D != 0){
                            orderEntry.setFTaxPrice(item.getPrice().multiply(new BigDecimal(2D)));
                        }else{
                            orderEntry.setFTaxPrice(item.getPrice());
                        }

                        //如果是散批单 含税单价需要用总价除以数量
                        if (sPflag){
                            BigDecimal price = item.getSplitPrice();
                            price = price.divide(orderEntry.getFRealQty(),6,BigDecimal.ROUND_DOWN);
                            orderEntry.setFTaxPrice(price);
                        }


                        //是否为赠品
                        if (!item.getFIsFree()){
                            if (Objects.nonNull(item.getSplitPrice()) && item.getSplitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                                if (Objects.nonNull(item.getBalancePrice()) && item.getBalancePrice().compareTo(BigDecimal.ZERO) <= 0) {
                                    orderEntry.setFIsFree(true);
                                } else {
                                    orderEntry.setFIsFree(item.getFIsFree());
                                }
                            } else {
                                orderEntry.setFIsFree(item.getFIsFree());
                            }
                        }else {
                            orderEntry.setFIsFree(true);
                        }

                        fSaleOrderEntry.add(orderEntry);
                    }
                }
            }



            order.setFEntity(fSaleOrderEntry);
//                Iterator it = fSaleOrderEntry.iterator();
//                while (it.hasNext()) {
//                    log.info("金蝶推送实体"+it.next().toString());
//                }


            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", order);
//                log.info("进入推送阶段============");
                HttpCommonResult result1 = HttpCommonUtil.postHeader(returnUrl, requestMap, loginToken);
                logger.info("ReturnOrderService.pushAuditKingdee result1:{}", result1.getResultData());
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    result = true;
                } else {
                    pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                    result = false;
                }
                pushKingdeeReturnGoods.setInstructions(result1.getResultData());
            } else {
                logger.error("ReturnOrderService.pushAuditKingdee push kingdee error");
                String res = "金蝶登录失败";
                pushKingdeeReturnGoods.setInstructions(res);
                pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                result = false;
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.pushAuditKingdee error:{}", e);
            String res = "金蝶推送失败";
            pushKingdeeReturnGoods.setInstructions(res);
            pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return result;
        } finally {
            if (number == 0) {
                pushKingdeeReturnGoods.setReturnGoodsCode(returnOrder.getId());
                pushKingdeeReturnGoods.setOrderCode(returnOrder.getTid());
                pushKingdeeReturnGoods.setCreateTime(LocalDateTime.now());
                pushKingdeeReturnGoods.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeReturnGoodsRepository.saveAndFlush(pushKingdeeReturnGoods);
            } else {
                pushKingdeeReturnGoods.setUpdateTime(LocalDateTime.now());
                pushKingdeeReturnGoods.setReturnGoodsCode(returnOrder.getId());
                tradePushKingdeeReturnGoodsRepository.updatePushKingdeeReturnGoodsState(pushKingdeeReturnGoods);
            }
            return result;
        }
    }


    /**
     * push退货单给金蝶
     */
    public Boolean newPilepushAuditKingdee(NewPileReturnOrder returnOrder) {
        logger.info("ReturnOrderService.newPilepushAuditKingdee req Ptid:{} returnOrderId:{}", returnOrder.getTid(), JSONObject.toJSONString(returnOrder));
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            return true;//非自营商家不考虑
        }
        Integer number = tradePushKingdeeReturnGoodsRepository.selcetPushKingdeeReturnGoodsNumber(returnOrder.getId());
        TradePushKingdeeReturnGoods pushKingdeeReturnGoods = new TradePushKingdeeReturnGoods();
        pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Boolean result = false;
        try {
            if (!checkAuditKingdee(returnOrder, pushKingdeeReturnGoods)) {
                pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return result;
            }
            NewPileTrade trade = newPileTradeService.detail(returnOrder.getTid());
            logger.info("ReturnOrderService.newPilepushAuditKingdee trade.getActivityType:{}", trade.getActivityType());
            List<PileStockRecordAttachment> recordAttachments = new ArrayList<>();
            if (Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
                recordAttachments.addAll(getReturnGoodsLinkedData(returnOrder));
            }
            KingdeeAuditOrder order = new KingdeeAuditOrder();
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.nonNull(employeeResponse) || StringUtils.isNotEmpty(employeeResponse.getErpEmployeeId())) {
                Map fSalerId = new HashMap();
                fSalerId.put("FNumber", employeeResponse.getErpEmployeeId());
                order.setFSalerId(fSalerId);
            }
            order.setOrderNumber(returnOrder.getId());
            order.setFSaleNum(returnOrder.getTid());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
            order.setFRetcustId(FCustId);//客户
            order.setFDate(DateUtil.nowDate());
            Map FSaleOrgId = new HashMap();
            FSaleOrgId.put("FNumber", kingdeeOrganization);
            order.setFSaleOrgId(FSaleOrgId);//组织
            //仓库id
            Map FStockId = new HashMap();
//            if("WH01".equals(trade.getWareHouseCode())){
//                FStockId.put("FNumber",ERPWMSConstants.MAIN_WH);
//            }else{
//                FStockId.put("FNumber",ERPWMSConstants.SUB_WH);
//            }

            if ("WH01".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
            } else if ("WH02".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
            } else if ("WH03".equals(trade.getWareHouseCode())) {
                FStockId.put("FNumber", ERPWMSConstants.STORE_WH);
            } else {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "推送金蝶退货找不到对应仓库ID.");
            }
            order.setFStockId(FStockId);
            //退货备注
            if (Objects.nonNull(returnOrder.getReturnReason())) {
                order.setFNote(returnOrder.getReturnReason().getDesc());
            }
            List<AuditOrderEntry> fSaleOrderEntry = new ArrayList<>();
            //购买商品
            if (returnOrder.getReturnItems().size() > 0) {
                Map<String, BigDecimal> returnGoodsMap = new HashMap();

                List<ReturnItem> returnItems = returnOrder.getReturnItems();//购买商品
                //赠送商品
                List<ReturnItem> returnGifts = returnOrder.getReturnGifts();
                if (CollectionUtils.isNotEmpty(returnGifts) && returnGifts.size() > 0) {
                    returnGifts.forEach(returnItem -> {
                        returnItem.setFIsFree(true);
                        returnItems.add(returnItem);
                    });
                }

                for (ReturnItem item : returnItems) {
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    if (Objects.isNull(prefix)) {
                        //零售仓库，默认001
                        prefix = "001-";
                    }
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix, ""));
                    if (!checkAuditKingdeeGoods(item, pushKingdeeReturnGoods)) {
                        pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return result;
                    }
                    if (CollectionUtils.isNotEmpty(recordAttachments)) {
                        AuditOrderEntry orderEntry = new AuditOrderEntry();
                        for (PileStockRecordAttachment stockRecordAttachment : recordAttachments) {
                            BigDecimal num = returnGoodsMap.get(item.getSkuId());
                            if (num == null) {
                                num = BigDecimal.ZERO;
                            }
                            if (item.getSkuId().equals(stockRecordAttachment.getSkuId()) && item.getNum().compareTo(num) == 1) {
                                AuditOrderEntry associatedOrderEntry = new AuditOrderEntry();
                                //还需要推多少商品
                                BigDecimal useNum = item.getNum().subtract(num);
                                //关联数据量是否大于还需要推多少商品
                                if ((BigDecimal.valueOf(stockRecordAttachment.getNum()).subtract(useNum)).compareTo(BigDecimal.ZERO) == 1) {
                                    associatedOrderEntry.setFRealQty(useNum);
                                    returnGoodsMap.put(item.getSkuId(), useNum.add(num));
                                } else {
                                    associatedOrderEntry.setFRealQty(BigDecimal.valueOf(stockRecordAttachment.getNum()));
                                    returnGoodsMap.put(item.getSkuId(), BigDecimal.valueOf(stockRecordAttachment.getNum()).add(num));
                                }
                                Map FMaterialId = new HashMap();
                                FMaterialId.put("FNumber", item.getErpSkuNo());
                                associatedOrderEntry.setFMaterialId(FMaterialId);
                                Map FSStockId = new HashMap();
                                if (Objects.nonNull(item.getGoodsInfoType()) && 1 == item.getGoodsInfoType()) {
                                    FSStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                                } else {
                                    if ("WH01".equals(trade.getWareHouseCode())) {
                                        FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                                    } else if ("WH02".equals(trade.getWareHouseCode())) {
                                        FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
                                    } else if ("WH03".equals(trade.getWareHouseCode())) {
                                        FStockId.put("FNumber", ERPWMSConstants.STORE_WH);
                                    } else {
                                        logger.info("ReturnOrderService.newPilepushAuditKingdee new_error");
                                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "推送金蝶退货找不到对应仓库ID.");
                                    }
                                }
                                associatedOrderEntry.setFStockId(FSStockId);//仓库
                                if (Objects.nonNull(item.getGoodsBatchNo())) {
                                    Map FLot = new HashMap();
                                    FLot.put("FNumber", item.getGoodsBatchNo());
                                    associatedOrderEntry.setFLot(FLot);
                                    associatedOrderEntry.setFProduceDate(item.getGoodsBatchNo());
                                }
                                associatedOrderEntry.setFTaxPrice(stockRecordAttachment.getPrice());
                                associatedOrderEntry.setF_According(stockRecordAttachment.getOrderCode());
                                //是否为赠品
                                if (stockRecordAttachment.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                                    associatedOrderEntry.setFIsFree(true);
                                } else {
                                    associatedOrderEntry.setFIsFree(item.getFIsFree());
                                }

                                log.info("ReturnOrderService.newPilepushAuditKingdee returnGoodsMap:{}", returnGoodsMap.get(item.getSkuId()), returnOrder.getId());
                                fSaleOrderEntry.add(associatedOrderEntry);
                                log.info("ReturnOrderService.newPilepushAuditKingdee returnGoodsEntity:{}" + associatedOrderEntry.toString());
                            }
                        }
                    } else {
                        AuditOrderEntry orderEntry = new AuditOrderEntry();
                        //没有关联数据
                        Map FMaterialId = new HashMap();
                        FMaterialId.put("FNumber", item.getErpSkuNo());
                        orderEntry.setFMaterialId(FMaterialId);
                        orderEntry.setFRealQty(item.getNum());
                        Map FSStockId = new HashMap();
                        if (Objects.nonNull(item.getGoodsInfoType()) && 1 == item.getGoodsInfoType()) {
                            FSStockId.put("FNumber", ERPWMSConstants.MAIN_MARKETING_WH);
                        } else {
                            if ("WH01".equals(trade.getWareHouseCode())) {
                                FStockId.put("FNumber", ERPWMSConstants.MAIN_WH);
                            } else if ("WH02".equals(trade.getWareHouseCode())) {
                                FStockId.put("FNumber", ERPWMSConstants.SUB_WH);
                            } else if ("WH03".equals(trade.getWareHouseCode())) {
                                FStockId.put("FNumber", ERPWMSConstants.STORE_WH);
                            } else {
                                logger.info("ReturnOrderService.newPilepushAuditKingdee new_error");
                                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "推送金蝶退货找不到对应仓库ID.");
                            }
                        }
                        orderEntry.setFStockId(FSStockId);//仓库
                        if (Objects.nonNull(item.getGoodsBatchNo())) {
                            Map FLot = new HashMap();
                            FLot.put("FNumber", item.getGoodsBatchNo());
                            orderEntry.setFLot(FLot);
                            orderEntry.setFProduceDate(item.getGoodsBatchNo());
                        }
                        //设置拆箱主键
                        if (Objects.nonNull(item.getDevanningId())) {
                            orderEntry.setFSkucxId(item.getDevanningId().toString());
                        }
                        orderEntry.setFTaxPrice(item.getPrice());
                        //是否为赠品
                        if (item.getSplitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                            orderEntry.setFIsFree(true);
                        } else {
                            orderEntry.setFIsFree(item.getFIsFree());
                        }


                        fSaleOrderEntry.add(orderEntry);
                    }
                }
            }


            order.setFEntity(fSaleOrderEntry);

            log.info("ReturnOrderService.newPilepushAuditKingdee new==============");
            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", order);
//                log.info("进入推送阶段============");
                logger.info("ReturnOrderService.newPilepushAuditKingdee loginnew");
                HttpCommonResult result1 = HttpCommonUtil.postHeader(stockUpRetUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.newPilepushAuditKingdee result1:{}", result1.getResultData());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    result = true;
                } else {
                    pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                    result = false;
                }
                pushKingdeeReturnGoods.setInstructions(result1.getResultData());
            } else {
                logger.error("ReturnOrderService.newPilepushAuditKingdee push kingdee error");
                String res = "金蝶登录失败";
                pushKingdeeReturnGoods.setInstructions(res);
                pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                result = false;
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.newPilepushAuditKingdee error:{}", e);
            String res = "金蝶推送失败";
            pushKingdeeReturnGoods.setInstructions(res);
            pushKingdeeReturnGoods.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return result;
        } finally {
            if (number == 0) {
                pushKingdeeReturnGoods.setReturnGoodsCode(returnOrder.getId());
                pushKingdeeReturnGoods.setOrderCode(returnOrder.getTid());
                pushKingdeeReturnGoods.setCreateTime(LocalDateTime.now());
                pushKingdeeReturnGoods.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeReturnGoodsRepository.saveAndFlush(pushKingdeeReturnGoods);
            } else {
                pushKingdeeReturnGoods.setUpdateTime(LocalDateTime.now());
                pushKingdeeReturnGoods.setReturnGoodsCode(returnOrder.getId());
                tradePushKingdeeReturnGoodsRepository.updatePushKingdeeReturnGoodsState(pushKingdeeReturnGoods);
            }
            return result;
        }
    }

    /**
     * 校验推送头部参数
     *
     * @param returnOrder
     * @return
     */
    private Boolean checkAuditKingdee(ReturnOrder returnOrder, TradePushKingdeeReturnGoods pushKingdeeReturnGoods) {
        if (StringUtils.isEmpty(returnOrder.getTid())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack Tid");
            pushKingdeeReturnGoods.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getBuyer().getAccount())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack FCustId");
            pushKingdeeReturnGoods.setInstructions("Lack FCustId");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getId())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack id");
            pushKingdeeReturnGoods.setInstructions("Lack id");
            return false;
        }
        return true;
    }


    /**
     * 校验推送头部参数
     *
     * @param returnOrder
     * @return
     */
    private Boolean checkAuditKingdee(NewPileReturnOrder returnOrder, TradePushKingdeeReturnGoods pushKingdeeReturnGoods) {
        if (StringUtils.isEmpty(returnOrder.getTid())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack Tid");
            pushKingdeeReturnGoods.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getBuyer().getAccount())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack FCustId");
            pushKingdeeReturnGoods.setInstructions("Lack FCustId");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getId())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack id");
            pushKingdeeReturnGoods.setInstructions("Lack id");
            return false;
        }
        return true;
    }

    /**
     * 校验推送表体参数
     *
     * @param item
     * @param pushKingdeeReturnGoods
     * @return
     */
    private Boolean checkAuditKingdeeGoods(ReturnItem item, TradePushKingdeeReturnGoods pushKingdeeReturnGoods) {
        if (StringUtils.isEmpty(item.getErpSkuNo())) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack ErpSkuNo ");
            pushKingdeeReturnGoods.setInstructions("Lack ErpSkuNo");
            return false;
        }
        if (item.getNum() == null) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack Num ");
            pushKingdeeReturnGoods.setInstructions("Lack Num");
            return false;
        }
        if (item.getGoodsInfoType() == null) {
            logger.info("ReturnOrderService.pushAuditKingdee Lack GoodsInfoType");
            pushKingdeeReturnGoods.setInstructions("Lack GoodsInfoType");
            return false;
        }
//        if (StringUtils.isEmpty(item.getGoodsBatchNo())){
//            logger.info("ReturnOrderService.pushAuditKingdee Lack GoodsBatchNo");
//            pushKingdeeReturnGoods.setInstructions("Lack GoodsBatchNo");
//            return false;
//        }
        return true;
    }

    /**
     * 给退货单添加仓库信息
     *
     * @param returnOrder
     * @param trade
     */
    private void addReturnOrderErp(ReturnOrder returnOrder, Trade trade,boolean isMultiBeforeTradeCreate) {
        if (returnOrder.getReturnItems().size() > 0 && trade.getTradeItems().size() > 0) {
            for (ReturnItem returnItem : returnOrder.getReturnItems()) {

                //补全信息
                if (Objects.nonNull(returnItem.getDevanningId())) {
                    TradeItem tradeItem = trade.getTradeItems().stream().filter(t -> t.getDevanningId().equals(returnItem.getDevanningId())).findFirst().orElse(null);
                    returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
                    returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                    returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
                    //查询拆箱商品
                    DevanningGoodsInfoByIdResponse context = devanningGoodsInfoQueryProvider.getInfoById(DevanningGoodsInfoByIdRequest
                            .builder()
                            .devanningId(tradeItem.getDevanningId())
                            .build())
                            .getContext();
                    if (Objects.nonNull(context) && Objects.nonNull(context.getDevanningGoodsInfoVO())) {
                        if(isMultiBeforeTradeCreate) {
                            returnItem.setNum(returnItem.getNum().multiply(context.getDevanningGoodsInfoVO().getDivisorFlag()));
                        }
                    }
                } else {
                    TradeItem tradeItem = trade.getTradeItems().stream().filter(t -> t.getSkuId().equals(returnItem.getSkuId())).findFirst().orElse(null);
                    returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
                    returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                    returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
                }

//                for (TradeItem tradeItem : trade.getTradeItems()) {
//                    if (returnItem.getSkuId().equals(tradeItem.getSkuId())) {
//                        returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
//                        returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
//                        returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
//                    }
//                }
            }
        }


        if (returnOrder.getReturnGifts().size() > 0 && trade.getGifts().size() > 0) {
            for (ReturnItem returnItem : returnOrder.getReturnGifts()) {
                for (TradeItem tradeItem : trade.getGifts()) {
                    if (returnItem.getSkuId().equals(tradeItem.getSkuId())) {
                        returnItem.setErpSkuNo(tradeItem.getErpSkuNo());
                        returnItem.setGoodsBatchNo(tradeItem.getGoodsBatchNo());
                        returnItem.setGoodsInfoType(tradeItem.getGoodsInfoType());
                    }
                }
            }
        }


    }

    /**
     * 查询可退金额
     *
     * @param rid
     * @return
     */
    public BigDecimal queryRefundPrice(String rid) {
        ReturnOrder returnOrder = findById(rid);
        return returnOrder.getReturnPrice().getTotalPrice();
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
        returnFSMService.changeState(request);
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
        ReturnOrder returnOrder = findById(rid);

        // 生成财务退款单
        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        if (Objects.isNull(returnPrice.getApplyPrice())) {
            return;
        }
        BigDecimal price = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();
        refundOrderService.generateRefundOrderByReturnOrderCode(rid, returnOrder.getBuyer().getId(), price,
                returnOrder.getPayType());
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.RECEIVE)
                .build();
        returnFSMService.changeState(request);

        //判断是否全量退货完成
        if (isReturnFull(returnOrder)) {
            //作废订单
            tradeService.voidTrade(returnOrder.getTid(), operator);
            Trade trade = tradeService.detail(returnOrder.getTid());
            trade.setRefundFlag(true);
            tradeService.updateTrade(trade);
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
        returnFSMService.changeState(request);

        // 拒绝退单时，发送MQ消息
        ReturnOrder returnOrder = findById(rid);
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnOrder.getBuyer().getId())
                .orderId(returnOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnOrderFlow(sendMQRequest);

        //退货物品拒收通知发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName(), reason);
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.RETURN_ORDER_GOODS_REJECT,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount());

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
        ReturnOrder returnOrder = findById(rid);
        Trade trade = tradeService.detail(returnOrder.getTid());
        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        returnPrice.setActualReturnPrice(price);
        returnOrder.setReturnPrice(returnPrice);
        if (trade.getGrouponFlag()) {
            //拼团订单退款后的处理
            modifyGrouponInfo(returnOrder, trade);
        }

       /*if (false) {
            PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                    TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
            if (Objects.isNull(payTradeRecordResponse)) {
                String tid = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
                payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(
                        new TradeRecordByOrderCodeRequest(tid)).getContext();
            }
            PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                    ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                    payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                    payTradeRecordResponse.getChannelItemId())).getContext();

            PayWay payWay = StringUtils.isNotEmpty(channelItemResponse.getChannel()) ? PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()) : PayWay.valueOf("CASH".toUpperCase());
            this.pushRefundOrderKingdee(returnOrder, refundOrder, payWay);
        }*/

        //退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.REFUND)
                .data(price)
                .build();
        returnFSMService.changeState(request);
        trade.setRefundFlag(true);
        tradeService.updateTrade(trade);

        String businessId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();

        if (returnOrder.getReturnType() == ReturnType.REFUND && (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())) {

            //释放囤货数量
            if (TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
                freePileGoodsNum(trade);
            }

            //仅退款退单在退款完成后释放商品库存
            freeStock(returnOrder, trade);

            //更新分摊金额退款完成标记为已退
            if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
                inventoryDetailSamountService.returnAmountByRid(rid);
            }

            //作废订单（非囤货的提货退单走原逻辑）
            //来源于囤货的提货退单，全部退完时 作废订单
            if (!TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType()) || newPilePickTradeAllReturned(returnOrder)) {
                tradeService.voidTrade(returnOrder.getTid(), operator);
            }
            trade.getTradeState().setEndTime(LocalDateTime.now());
        }
        if (returnOrder.getPayType() == PayType.OFFLINE) {
            saveReconciliation(returnOrder, "", businessId, "");
        }
        //ares埋点-订单-用户退单(线上/线下)
        orderAresService.dispatchFunction("returnOrder", returnOrder);
    }

    /**
     * 检查来源于新囤货的提货订单是否全部退完
     *
     * @param returnOrder
     * @return
     */
    private boolean newPilePickTradeAllReturned(ReturnOrder returnOrder) {
        List<InventoryDetailSamount> allTaskAmountList = inventoryDetailSamountService.getInventoryByTakeId(returnOrder.getTid());
        List<InventoryDetailSamount> notReturnedList = allTaskAmountList.stream().filter(item -> Objects.equals(0, item.getReturnFlag())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notReturnedList)) {
            return true;
        }

        for (InventoryDetailSamount item : notReturnedList) {
            //提货未退 或者已申请退单但退单不为当前退单
            if (StringUtils.isBlank(item.getReturnId())
                    || !Objects.equals(item.getReturnId(), returnOrder.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 退款
     *
     * @param rid
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void refundNewPile(NewPileTrade trade, String rid, Operator operator, BigDecimal price) {
        NewPileReturnOrder returnOrder = newPileReturnOrderQuery.findByIdNewPile(rid);
        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        returnPrice.setActualReturnPrice(price);
        returnOrder.setReturnPrice(returnPrice);

        //退单状态
        /*ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.REFUND)
                .data(price)
                .build();
        returnFSMService.changeState(request);*/
        trade.setRefundFlag(true);


        String businessId = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();

        //释放囤货数量
        //恢复虚拟库存
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
        trade.getTradeState().setEndTime(LocalDateTime.now());
        trade.getTradeState().setFlowState(NewPileFlowState.COMPLETED);
        if (returnOrder.getPayType() == PayType.OFFLINE) {
            newPileReturnOrderQuery.saveReconciliation(returnOrder, "", businessId, "");
        }
        returnOrder.setReturnFlowState(NewPileReturnFlowState.COMPLETED);
        newPileReturnOrderQuery.updateReturnOrder(returnOrder);
        newPileTradeService.updateTrade(trade);
    }


    /**
     * 保存退款对账明细
     *
     * @param returnOrder
     * @param payWayStr
     */
    @Transactional
    public void saveReconciliation(ReturnOrder returnOrder, String payWayStr, String businessId, String tradeNo) {
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrder.getId());
        if (Objects.isNull(refundOrder)) {
            return;
        }
        AccountRecordAddRequest reconciliation = new AccountRecordAddRequest();
        reconciliation.setAmount(returnOrder.getReturnPrice().getApplyStatus() ?
                returnOrder.getReturnPrice().getApplyPrice() : returnOrder.getReturnPrice().getTotalPrice());
        reconciliation.setCustomerId(returnOrder.getBuyer().getId());
        reconciliation.setCustomerName(returnOrder.getBuyer().getName());
        reconciliation.setOrderCode(returnOrder.getTid());
        reconciliation.setOrderTime(returnOrder.getCreateTime());
        reconciliation.setTradeTime(Objects.isNull(refundOrder.getRefundBill()) ? LocalDateTime.now() :
                refundOrder.getRefundBill().getCreateTime());

        if (StringUtils.isNotBlank(businessId)) {
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
        reconciliation.setReturnOrderCode(returnOrder.getId());
        reconciliation.setStoreId(returnOrder.getCompany().getStoreId());
        reconciliation.setSupplierId(returnOrder.getCompany().getCompanyInfoId());
        reconciliation.setType((byte) 1);
        accountRecordProvider.add(reconciliation);
    }


    /**
     * 商家退款申请(修改退单价格新增流水)
     *
     * @param returnOrder
     * @param refundComment
     * @param actualReturnPoints
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void onlineEditPrice(ReturnOrder returnOrder, String refundComment, BigDecimal actualReturnPrice, Long actualReturnPoints, Operator operator) {
        tradeSaveRefundBill(returnOrder, refundComment, actualReturnPrice, actualReturnPoints, operator);

        // 以下代码不生效 先注释掉
        /*if(Objects.nonNull(returnOrder.getTradeVO())&&Objects.nonNull(returnOrder.getTradeVO().getSupplier())&&!orderCommonService.isSelf(returnOrder.getTradeVO().getSupplier().getCompanyType())){
            log.info("第三方商家直接退款，不需要运营端审核，rid：[{}],商家：[{}]",returnOrder.getId(),returnOrder.getTradeVO().getSupplier().getStoreId());
            returnOrderService.refundOnlineByTid(returnOrder.getId(),operator,false);
        }*/
    }


    /**
     *
     *
     *
     **
     */


    /**
     * 商家退款申请(修改退单价格新增流水)
     *
     * @param returnOrder
     * @param refundComment
     * @param actualReturnPoints
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void onlineEditPriceNewPile(NewPileReturnOrder returnOrder, String refundComment, BigDecimal actualReturnPrice, Long actualReturnPoints, Operator operator) {

        // 查询退款单
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrder.getId());

        log.info("囤货退款单----查询退款单----" + JSON.toJSONString(refundOrder));
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
            log.info("refundBill = refundOrder.getRefundBill()----" + JSON.toJSONString(refundBill));
            refundBillService.save(refundBill);
        } else {
            refundBill.setActualReturnPrice(Objects.isNull(actualReturnPrice) ? refundOrder.getReturnPrice() :
                    actualReturnPrice);
            refundBill.setActualReturnPoints(actualReturnPoints);
        }
        log.info("囤货退款单refundBill ----" + JSON.toJSONString(refundBill));
        refundBillService.saveAndFlush(refundBill);
        //设置退款单状态为待平台退款
        refundOrder.setRefundStatus(RefundStatus.APPLY);
        if (returnOrder.getReturnPrice().getTotalPrice().compareTo(actualReturnPrice) == 1) {
            returnOrder.getReturnPrice().setApplyStatus(true);
            returnOrder.getReturnPrice().setApplyPrice(actualReturnPrice);
        }
        log.info("保存refundOrder ----" + JSON.toJSONString(refundOrder));
        refundOrderRepository.saveAndFlush(refundOrder);
        String detail = String.format("退单[%s]已添加线上退款单，操作人:%s", returnOrder.getId(), operator.getName());
        returnOrder.appendReturnEventLog(
                ReturnEventLog.builder()
                        .operator(operator)
                        .eventType(ReturnEvent.REFUND.getDesc())
                        .eventTime(LocalDateTime.now())
                        .eventDetail(detail)
                        .build()
        );
        log.info("保存updateReturnOrder ----" + JSON.toJSONString(returnOrder));
        newPileReturnOrderQuery.updateReturnOrder(returnOrder);
        log.info("发送消息" + detail);
        this.operationLogMq.convertAndSend(operator, ReturnEvent.REFUND.getDesc(), detail);

        //退款审核通过发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName());
        String pic = returnOrder.getReturnItems().get(0).getPic();
        log.info("发送消息params" + JSON.toJSONString(params));
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.REFUND_CHECK_PASS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount());
    }

    public void tradeSaveRefundBill(ReturnOrder returnOrderRequest, String refundComment, BigDecimal actualReturnPrice,
                                    Long actualReturnPoints, Operator operator) {
        // 查询退款单
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrderRequest.getId());
        ReturnOrder dbReturnOrder = this.findById(returnOrderRequest.getId());

        log.info("查询退款单----" + JSON.toJSONString(refundOrder));
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
            log.info("refundBill = refundOrder.getRefundBill()----" + JSON.toJSONString(refundBill));
            refundBillService.save(refundBill);
        } else {
            refundBill.setActualReturnPrice(Objects.isNull(actualReturnPrice) ? refundOrder.getReturnPrice() :
                    actualReturnPrice);
            refundBill.setActualReturnPoints(actualReturnPoints);
        }
        log.info("refundBill ----" + JSON.toJSONString(refundBill));
        refundBillService.saveAndFlush(refundBill);
        //设置退款单状态为待平台退款
        refundOrder.setRefundStatus(RefundStatus.APPLY);
        if (dbReturnOrder.getReturnPrice().getTotalPrice().compareTo(actualReturnPrice) == 1) {
            dbReturnOrder.getReturnPrice().setApplyStatus(true);
            dbReturnOrder.getReturnPrice().setApplyPrice(actualReturnPrice);
        }
        if (Objects.nonNull(returnOrderRequest.getReturnPoints())) {
            dbReturnOrder.getReturnPoints().setActualPoints(actualReturnPoints);
        }
        log.info("保存refundOrder ----" + JSON.toJSONString(refundOrder));
        refundOrderRepository.saveAndFlush(refundOrder);
        String detail = String.format("退单[%s]已添加线上退款单，操作人:%s", returnOrderRequest.getId(), operator.getName());
        dbReturnOrder.appendReturnEventLog(
                ReturnEventLog.builder()
                        .operator(operator)
                        .eventType(ReturnEvent.REFUND.getDesc())
                        .eventTime(LocalDateTime.now())
                        .eventDetail(detail)
                        .build()
        );
        log.info("保存updateReturnOrder ----" + JSON.toJSONString(dbReturnOrder));
        returnOrderService.updateReturnOrder(dbReturnOrder);
        log.info("发送消息" + detail);
        this.operationLogMq.convertAndSend(operator, ReturnEvent.REFUND.getDesc(), detail);

        //退款审核通过发送MQ消息
        List<String> params = Lists.newArrayList(returnOrderRequest.getReturnItems().get(0).getSkuName());
        String pic = dbReturnOrder.getReturnItems().get(0).getPic();
        log.info("发送消息params" + JSON.toJSONString(params));
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.REFUND_CHECK_PASS,
                params,
                dbReturnOrder.getId(),
                dbReturnOrder.getBuyer().getId(),
                pic,
                dbReturnOrder.getBuyer().getAccount());
    }

   /**
    * @desc  在线退款
    * @author shiy  2023/9/1 9:24
   */
    @Transactional
    @LcnTransaction
    public void refundOnline(ReturnOrder returnOrder, RefundOrder refundOrder, Operator operator) {
        try {
            RefundOnlineBO refundOnlineBO = buildBoByRefundOnline(returnOrder, refundOrder, operator);
            refundAfterRefundBo(returnOrder, refundOrder, operator, refundOnlineBO);
        } catch (SbcRuntimeException e) {
            logger.error("{}退单状态修改异常,error={}", returnOrder.getId(), e);
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
    }

    @Transactional
    @LcnTransaction
    public void refundOnlineByWalletRecordVO(WalletRecordVO walletRecordVO, Operator operator) {
        try {
            ReturnOrder returnOrder = findById(walletRecordVO.getRelationOrderId());
            RefundOrder refundOrder = refundOrderService.generateRefundOrderByWalletRecord(returnOrder,walletRecordVO).get();
            PayTradeRecordResponse payTradeRecordResponse = new PayTradeRecordResponse();
            payTradeRecordResponse.setChannelItemId(21L);//PC余额支付
            payTradeRecordResponse.setTradeNo(returnOrder.getTid());
            String refundCommont =StringUtils.isBlank(walletRecordVO.getRemark())?walletRecordVO.getRemark():"退单支付鲸币";
            RefundOnlineBO refundOnlineBO = builRefundOnlineBOWithPayTradeRecord(returnOrder, refundOrder, operator,payTradeRecordResponse,refundCommont);
            refundAfterRefundBo(returnOrder, refundOrder, operator, refundOnlineBO);
        } catch (SbcRuntimeException e) {
            logger.error("{}退单状态修改异常,error={}", walletRecordVO.getRelationOrderId(), e);
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
    }

    /**
     * @desc  在线退款 因为建行存在退款失败，退款时调用该方法已经移动了位置，影响了推ERP原逻辑，所以将该方法拆成两部分
     * @author shiy  2023/9/1 9:24
     */
    @Transactional
    @LcnTransaction
    public void refundOnline(ReturnOrder returnOrder, RefundOrder refundOrder, Operator operator,RefundOnlineBO refundOnlineBO) {
        try {
            if(refundOnlineBO == null) {
                refundOnlineBO = buildBoByRefundOnline(returnOrder, refundOrder, operator);
            }
            refundAfterRefundBo(returnOrder, refundOrder, operator, refundOnlineBO);
        } catch (SbcRuntimeException e) {
            logger.error("{}退单状态修改异常,error={}", returnOrder.getId(), e);
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
    }

    @Transactional
    @LcnTransaction
    public RefundOnlineBO buildBoByRefundOnline(ReturnOrder returnOrder, RefundOrder refundOrder, Operator operator){
        PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
        if (Objects.isNull(payTradeRecordResponse)) {
            Trade trade = tradeService.detail(returnOrder.getTid());
            String tid = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(
                    new TradeRecordByOrderCodeRequest(tid)).getContext();
        }
        return builRefundOnlineBOWithPayTradeRecord(returnOrder, refundOrder, operator, payTradeRecordResponse);
    }

    private RefundOnlineBO builRefundOnlineBOWithPayTradeRecord(ReturnOrder returnOrder, RefundOrder refundOrder, Operator operator, PayTradeRecordResponse payTradeRecordResponse){
        return builRefundOnlineBOWithPayTradeRecord(returnOrder, refundOrder, operator, payTradeRecordResponse,null);
    }

    private RefundOnlineBO builRefundOnlineBOWithPayTradeRecord(ReturnOrder returnOrder, RefundOrder refundOrder, Operator operator, PayTradeRecordResponse payTradeRecordResponse,String refundComment) {
        PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                payTradeRecordResponse.getChannelItemId())).getContext();
        RefundBill refundBill;
        // 退款流水保存
        if ((refundBill = refundOrder.getRefundBill()) == null) {
            refundBill = new RefundBill();
            refundBill.setPayChannel(channelItemResponse.getName());
            refundBill.setPayChannelId(channelItemResponse.getId());
            refundBill.setActualReturnPrice(refundOrder.getReturnPrice());
            refundBill.setCreateTime(LocalDateTime.now());
            refundBill.setRefundId(refundOrder.getRefundId());
            refundBill.setRefundComment(refundComment);
            refundBillService.save(refundBill);
        } else {
            refundBill.setPayChannel(channelItemResponse.getName());
            refundBill.setPayChannelId(channelItemResponse.getId());
            refundBill.setCreateTime(LocalDateTime.now());
            refundBillService.saveAndFlush(refundBill);
        }
        logger.info("ReturnOrderService.refundOnline operator:{} returnOrder：{} channelItemResponse:{}", JSONObject.toJSONString(operator)
                , JSONObject.toJSONString(returnOrder)
                , JSONObject.toJSONString(channelItemResponse));
        RefundOnlineBO refundOnlineBO = new RefundOnlineBO();
        refundOnlineBO.setRefundBill(refundBill);
        refundOnlineBO.setPayTradeRecordResponse(payTradeRecordResponse);
        refundOnlineBO.setChannelItemResponse(channelItemResponse);
        return refundOnlineBO;
    }

    private void refundAfterRefundBo(ReturnOrder returnOrder, RefundOrder refundOrder, Operator operator, RefundOnlineBO refundOnlineBO) {
        PayTradeRecordResponse payTradeRecordResponse= refundOnlineBO.getPayTradeRecordResponse();
        RefundBill refundBill=  refundOnlineBO.getRefundBill();
        PayChannelItemResponse channelItemResponse= refundOnlineBO.getChannelItemResponse();
        if (returnOrder.getReturnFlowState() != ReturnFlowState.COMPLETED) {
            //处理订单状态
            refund(returnOrder.getId(), operator, returnOrder.getReturnPrice().getApplyStatus() ?
                    returnOrder.getReturnPrice().getApplyPrice() : refundBill.getActualReturnPrice());
            refundOrder.setRefundStatus(RefundStatus.FINISH);
            //Trade trade = tradeService.detail(returnOrder.getTid());
            //wms 三方推送 退款
            // TODO: 2020/5/19 库存号待完善 退款不加推送取消订单的接口
//                if (wmsPushFlag){
//                    if (pushCancelOrder(returnOrder,trade.getWareHouseCode())){
//                        returnOrder.setWmsPushState(WMSPushState.CANCEL_ORDER_SUCCESS);
//                    }else {
//                        returnOrder.setWmsPushState(WMSPushState.CANCEL_ORDER_FAIL);
//                    }
//                }


            // 更改订单状态
            refundOrderRepository.saveAndFlush(refundOrder);
            //保存退款对账明细
            String tradeNo = Objects.nonNull(payTradeRecordResponse) ? payTradeRecordResponse.getTradeNo() : "";
            saveReconciliation(returnOrder, channelItemResponse.getChannel(), "", tradeNo);
            tradeService.returnCoupon(returnOrder.getTid());

            // 退单完成时，发送MQ消息
            ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                    .addFlag(Boolean.FALSE)
                    .customerId(returnOrder.getBuyer().getId())
                    .orderId(returnOrder.getTid())
                    .returnId(returnOrder.getId())
                    .build();
            returnOrderProducerService.returnOrderFlow(sendMQRequest);
        }
    }

    public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal(0);
        BigDecimal b2 = new BigDecimal(10);
        int goType = 0;
        if(b1.compareTo(BigDecimal.ZERO) != 0 &&
                b2 .compareTo(BigDecimal.ZERO) != 0) {
            goType = 1;
        }else if(b1.compareTo(BigDecimal.ZERO) != 0){
            goType = 2;
        }else if(b2.compareTo(BigDecimal.ZERO) != 0){
            goType = 3;
        }
        System.out.println(goType);
    }

    public void pushKingdeeEntry(Trade trade,ReturnOrder returnOrder, RefundOrder refundOrder){

        int goType = 0;
        if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) != 0 &&
                returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) != 0){
            goType = 1;
        }else if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) != 0){
            //goType = 2;
        }else if(returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) != 0){
            goType = 3;
        }

        PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
        if (Objects.isNull(payTradeRecordResponse)) {
            String tid = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(
                    new TradeRecordByOrderCodeRequest(tid)).getContext();
        }
        PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                payTradeRecordResponse.getChannelItemId())).getContext();

        log.info("new pushKingdeeEntry goType :{},{}" ,goType,channelItemResponse.getChannel());

        if (trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
            //不为wms缺货退款
            if (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats()) {
                //为交易单时，推金蝶退款接口
                if (kingdeeOpenState) {//判断是否开启新金蝶
                    //鲸币和原路返回各调用一次
                    if(goType == 0 || goType == 3){
                        //向金蝶push退款单
                        if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus()) && StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                        } else if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                        }
                    }else if(goType == 2){
                        pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.BALANCE);
                    }else if(goType == 1){
                        //向金蝶push退款单
                        if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus()) && StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                        } else if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                        }
                        //pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.BALANCE);
                    }
                }
            }
        } else {
            //为交易单时，推金蝶退款接口
            if (kingdeeOpenState) {//判断是否开启新金蝶
                //向金蝶push退款单
                if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                    //TODO 测试打印日志上线删除
                    log.info("========boos退款打印值" + JSONObject.toJSONString(channelItemResponse));
//                    if (Objects.nonNull(channelItemResponse.getChannel())) {
//                        PayWay payWay = PayWay.valueOf("UNIONPAY");
//                        try {
//                            payWay = PayWay.valueOf(channelItemResponse.getChannel().toUpperCase());
//                        } catch (IllegalArgumentException e) {
//                            payWay = PayWay.valueOf("UNIONPAY");
//                        }
//                        if(Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
//                            payWay = PayWay.valueOf("BALANCE");
//                        }
//                        pushRefundOrderKingdee(returnOrder, refundOrder, payWay);
//                    } else {
//                        PayWay payWay = PayWay.valueOf("UNIONPAY");
//                        if(Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
//                            payWay = PayWay.valueOf("BALANCE");
//                        }
//                        pushRefundOrderKingdee(returnOrder, refundOrder, payWay);
//                    }

                    if(goType == 0 || goType == 3){
                        //向金蝶push退款单
                        if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus()) && StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                        } else if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                        }
                    }else if(goType == 2){
                        pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.BALANCE);
                    }else if(goType == 1){
                        //向金蝶push退款单
                        if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus()) && StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                        } else if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                            pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                        }
                        //pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.BALANCE);
                    }
                }
            }
        }
    }

    @Autowired
    PushKingdeeService pushKingdeeService;

    public void pushKingdeeForClaims(Trade trade, ReturnOrder returnOrder) {
        if (kingdeeOpenState) {
            pushKingdeeService.pushRefundOrderKingdeeForClaims(trade, returnOrder);
        }
    }

    /**
     * 在线退款
     *
     * @param returnOrder 退货单
     * @param refundOrder 退款单
     * @param operator
     */
    @Transactional
    @LcnTransaction
    public void refundOnlineNewPile(NewPileReturnOrder returnOrder, RefundOrder refundOrder, Operator operator) {
        try {
            RefundBill refundBill;
            PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                    TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
            if (Objects.isNull(payTradeRecordResponse)) {
                NewPileTrade trade = newPileTradeService.detail(returnOrder.getTid());
                String tid = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
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
            logger.info("ReturnOrderService.refundOnline operator:{} returnOrder：{} channelItemResponse:{}", JSONObject.toJSONString(operator)
                    , JSONObject.toJSONString(returnOrder)
                    , JSONObject.toJSONString(channelItemResponse));
            if (returnOrder.getReturnFlowState() != NewPileReturnFlowState.COMPLETED) {
                NewPileTrade trade = newPileTradeService.detail(returnOrder.getTid());
                //处理订单状态
                refundNewPile(trade, returnOrder.getId(), operator, returnOrder.getReturnPrice().getApplyStatus() ?
                        returnOrder.getReturnPrice().getApplyPrice() : refundBill.getActualReturnPrice());
                refundOrder.setRefundStatus(RefundStatus.FINISH);

                //wms 三方推送 退款
                // TODO: 2020/5/19 库存号待完善 退款不加推送取消订单的接口
//                if (wmsPushFlag){
//                    if (pushCancelOrder(returnOrder,trade.getWareHouseCode())){
//                        returnOrder.setWmsPushState(WMSPushState.CANCEL_ORDER_SUCCESS);
//                    }else {
//                        returnOrder.setWmsPushState(WMSPushState.CANCEL_ORDER_FAIL);
//                    }
//                }


                // 更改订单状态
                refundOrderRepository.saveAndFlush(refundOrder);
                //保存退款对账明细
                String tradeNo = Objects.nonNull(payTradeRecordResponse) ? payTradeRecordResponse.getTradeNo() : "";
                newPileReturnOrderQuery.saveReconciliation(returnOrder, channelItemResponse.getChannel(), "", tradeNo);
                //囤货线上退款退优惠券
                newPileTradeService.returnCoupon(returnOrder.getTid(),returnOrder.getId());

                // 退单完成时，发送MQ消息
                ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                        .addFlag(Boolean.FALSE)
                        .customerId(returnOrder.getBuyer().getId())
                        .orderId(returnOrder.getTid())
                        .returnId(returnOrder.getId())
                        .build();
                returnOrderProducerService.returnOrderFlow(sendMQRequest);
            }


        } catch (SbcRuntimeException e) {
            logger.error("{}退单状态修改异常,error={}", returnOrder.getId(), e);
            throw new SbcRuntimeException(e.getErrorCode(), e.getParams());
        }
    }

    public void pushKingdeeEntryPile(NewPileTrade trade,NewPileReturnOrder returnOrder, RefundOrder refundOrder){
        PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new
                TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
        if (Objects.isNull(payTradeRecordResponse)) {
            String tid = trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId();
            payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(
                    new TradeRecordByOrderCodeRequest(tid)).getContext();
        }
        PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new
                ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) || Objects.isNull(
                payTradeRecordResponse.getChannelItemId()) ? Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                payTradeRecordResponse.getChannelItemId())).getContext();
        if (trade.getActivityType().equals(TradeActivityTypeEnum.STOCKUP.toActivityType())) {
            //不为wms缺货退款
            if (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats()) {
                //为交易单时，推金蝶退款接口
                if (kingdeeOpenState) {//判断是否开启新金蝶
                    //todo 向金蝶push退款单
                    if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus()) && StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                        //pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                    } else if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                        //pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                    }
                }
            }
        } else {
            //为交易单时，推金蝶退款接口
            if (kingdeeOpenState) {//判断是否开启新金蝶
                int goType = 0;
                if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) != 0 &&
                        returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) != 0){
                    goType = 1;
                }else if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) != 0){
                    goType = 2;
                }else if(returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) != 0){
                    goType = 3;
                }
                //todo 向金蝶push退款单
                if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                    if(goType == 2){
                        newPilePushRefundOrderKingdee(returnOrder, refundOrder, PayWay.BALANCE);
                    }else  if(goType == 0 || goType == 3) {
                        //todo
                        PayWay payWay = null;
                        if (StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                            payWay = PayWay.valueOf(channelItemResponse.getChannel().toUpperCase());
                        } else {
                            payWay = PayWay.UNIONPAY;
                        }
                        newPilePushRefundOrderKingdee(returnOrder, refundOrder, payWay);
                    }else  if(goType == 1) {
                        PayWay payWay = null;
                        if (StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                            payWay = PayWay.valueOf(channelItemResponse.getChannel().toUpperCase());
                        } else {
                            payWay = PayWay.UNIONPAY;
                        }
                        newPilePushRefundOrderKingdee(returnOrder, refundOrder, payWay);
                        newPilePushRefundOrderKingdee(returnOrder, refundOrder, PayWay.BALANCE);
                    }

                }
            }
        }
    }

    /**
     * 向金蝶push退款单
     *
     * @param returnOrder
     * @param refundOrder
     */
    public void pushRefundOrderKingdee(ReturnOrder returnOrder, RefundOrder refundOrder, PayWay payWay) {
        log.info("ReturnOrderService.pushRefundOrderKingdee tid:[{}],rid:[{}],payway:[{}]",returnOrder.getTid(),returnOrder.getId(),payWay.toValue());
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            log.info("ReturnOrderService.pushRefundOrderKingdee 不推ERP tid:[{}],rid:[{}]",returnOrder.getTid(),returnOrder.getId());
            return;//非自营商家不考虑
        }
        logger.info("ReturnOrderService.pushRefundOrderKingdee req RefundOrderId:{} payWay:{}", returnOrder.getTid(), payWay);
        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(refundOrder.getReturnOrderCode());
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            logger.info("ReturnOrderService.pushRefundOrderKingdee rid:{} refundOrder:{}", returnOrder.getTid(), JSONObject.toJSONString(refundOrder));
            if (!checkRefundOrderKingdee(returnOrder, refundOrder, payWay, pushKingdeeRefundOrder)) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(payWay.equals(PayWay.BALANCE) ? (refundOrder.getReturnOrderCode() + "G"): refundOrder.getReturnOrderCode());
            payOrder.setFSaleNum(returnOrder.getTid());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment;
            if (StringUtils.isEmpty(refundOrder.getRefundBill().getRefundComment())) {
                logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FNOTE");
                refundComment = "申请退款";
            } else {
                refundComment = refundOrder.getRefundBill().getRefundComment();
            }
            Map FSETTLETYPEID = new HashMap();
//            if (payWay.equals(PayWay.CASH)) {
//                //先下支付
//                FSETTLETYPEID.put("FNumber", "offlinepay");
//            } else {
//                FSETTLETYPEID.put("FNumber", payWay.toValue());
//            }
            if (payWay.equals(PayWay.BALANCE)) {
                 FSETTLETYPEID.put("FNumber", "QB");
            }else {
                if (payWay.equals(PayWay.CASH)) {
//                //先下支付
                    FSETTLETYPEID.put("FNumber", "offlinepay");
                } else {
                    FSETTLETYPEID.put("FNumber", payWay.toValue());
                }
            }

            //使用银行卡
            Map FACCOUNTID = new HashMap();
            if (payWay.equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
            } else if (payWay.equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            } else if (payWay.equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            }else if (payWay.equals(PayWay.CUPSALI)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            }else if (payWay.equals(PayWay.CUPSWECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            } else if (payWay.equals(PayWay.CCB)) {
                FACCOUNTID.put("FNumber", kingdeeBocomPay);
            }else if(payWay.equals(PayWay.BALANCE)){
                FACCOUNTID.put("FNumber", "QB");
            }
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            BigDecimal price = BigDecimal.ZERO;
            if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) == 0 &&
                returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) == 0 ){
                if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) == 1) {
                    //申请金额
                    price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                } else {
                    //商品总金额
                    price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                }
            }else{
                if (payWay.equals(PayWay.BALANCE)) {
                    price = returnOrder.getReturnPrice().getTotalBalanceRefundAmount().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                }else {
                    price = returnOrder.getReturnPrice().getTotalOnlineRefundAmount().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                }
            }

            // 配送到店退款单 运费部分不推送ERP 231110
            BigDecimal deliveryPrice = returnOrder.getReturnPrice().getDeliveryPrice();
            if (Objects.nonNull(deliveryPrice) && deliveryPrice.compareTo(BigDecimal.ZERO) > 0) {
                Trade trade = tradeService.detail(returnOrder.getTid());
                if (Objects.nonNull(trade) && DeliverWay.isCcbSubBill(trade.getDeliverWay())) {
                    log.info("配送到店推送ERP收款退款单 运费部分不推送，运费:{}", deliveryPrice.toPlainString());
                    price = price.subtract(deliveryPrice);
                    log.info("配送到店推送ERP收款退款单 运费部分不推送，去除运费金额:{}", price.toPlainString());
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toPlainString());
                }
            }


            // 退款收款单 佣金分开推送金蝶 不分开推了 230807
            /*BigDecimal commission = BigDecimal.ZERO;
            if (Objects.equals(payWay, PayWay.CCB)) {
                String tradeId = returnOrder.getTid();
                Trade trade = tradeService.detail(tradeId);
                if (Objects.nonNull(trade) && Objects.nonNull(trade.getPayOrderNo())) {
                    CcbPayOrderRecordResponse recordResponse = ccbPayProvider.queryCcbPayOrderRecord(tradeId, trade.getPayOrderNo()).getContext();
                    if (Objects.nonNull(recordResponse)) {
                        log.info("推金蝶退款收款单，佣金部分，比例：{},退单金额：{}",recordResponse.getRatio(), price);
                        BigDecimal ratio = recordResponse.getRatio();
                        BigDecimal amt = price.multiply(ratio).setScale(2, RoundingMode.UP);
                        BigDecimal txnAmt = recordResponse.getTxnAmt();
                        if (amt.compareTo(txnAmt) >= 0) {
                            amt = txnAmt.setScale(6, RoundingMode.DOWN);
                            payOrderSettlement.setFRECTOTALAMOUNTFOR(amt.toString());
                            commission = recordResponse.getCommission();
                        }else {
                            commission = price.subtract(amt).setScale(6, RoundingMode.DOWN);
                            payOrderSettlement.setFRECTOTALAMOUNTFOR(amt.toString());
                        }
                    }
                    log.info("推金蝶退款收款单,佣金部分,计算佣金：{}", commission);
                }
            }

            if (commission.compareTo(BigDecimal.ZERO) > 0) {
                this.pushRefundOrderCommissionKingdee(commission, refundOrder.getReturnOrderCode(),returnOrder.getTid(), returnOrder.getBuyer().getAccount());
            }*/


            payOrderSettlement.setFNOTE(refundComment + "-" + payOrder.getOrderNumber());
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map fSaleType = new HashMap();
            if (Objects.nonNull(returnOrder.getSaleType())) {
                fSaleType.put("FNumber", String.valueOf(returnOrder.getSaleType().toValue()));
                payOrder.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
                payOrder.setFSaleType(fSaleType);
            }

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.pushRefundOrderKingdee result1:{} returnFlowState:{} code:{}", result1.getResultData(), returnOrder.getReturnFlowState(), result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.pushRefundOrderKingdee error:{}", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return;
        } finally {
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(returnOrder.getTid());
                pushKingdeeRefundOrder.setRefundCode(refundOrder.getReturnOrderCode());
                pushKingdeeRefundOrder.setPayType(payWay.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(returnOrder.getBuyer().getAccount());
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(refundOrder.getReturnOrderCode());
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
            log.info("ReturnOrderService.pushRefundOrderKingdee ");
            if (pushKingdeeRefundOrder.getPushStatus().intValue() == PushKingdeeStatusEnum.PUSHSUCCESS.toStatus().intValue()
            && payWay.equals(PayWay.BALANCE)) {
                //推送收款单
                pushPayOrderKingdee(returnOrder, payWay);
            }
        }
    }

    public void pushRefundOrderCommissionKingdee(BigDecimal commission, String returnOrderCode, String tid, String account, String suffix) {
        returnOrderCode = returnOrderCode + suffix;
        tid = tid + suffix;

        log.info("退款收款单推佣金送金蝶 单号:{},佣金:{}", returnOrderCode, commission);

        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(returnOrderCode);
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {

            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(returnOrderCode);
            payOrder.setFSaleNum(tid);
            Map FCustId = new HashMap();
            FCustId.put("FNumber", account);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment = "申请退款";
            Map FSETTLETYPEID = new HashMap();

            FSETTLETYPEID.put("FNumber", PayWay.CCB.toValue());

            // 使用银行卡
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", kingdeeCcbpay);

            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            payOrderSettlement.setFRECTOTALAMOUNTFOR(commission.toString());

            payOrderSettlement.setFNOTE(refundComment + "-" + returnOrderCode);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            // 销售类型
            Map fSaleType = new HashMap();
            fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
            payOrder.setFSaleType(fSaleType);

            // 登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                // 提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.pushRefundOrderCommissionKingdee result1:{}  code:{}", result1.getResultData(), result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.pushRefundOrderCommissionKingdee error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            // 没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(tid);
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                pushKingdeeRefundOrder.setPayType(PayWay.CCB.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(account);
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }

        }
    }


    /**
     * 向金蝶push囤货退款单
     *
     * @param returnOrder
     * @param refundOrder
     */
    public void newPilePushRefundOrderKingdee(NewPileReturnOrder returnOrder, RefundOrder refundOrder, PayWay payWay) {
        logger.info("ReturnOrderService.pushRefundOrderKingdee req RefundOrderId:{} payWay:{}", returnOrder.getTid(), payWay);
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            return;//非自营商家不考虑
        }
        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(refundOrder.getReturnOrderCode());
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            if (!checkRefundOrderKingdee(returnOrder, refundOrder, payWay, pushKingdeeRefundOrder)) {
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            if(null != returnOrder.getReturnEventLogs() && returnOrder.getReturnEventLogs().size() > 0){
                payOrder.setFDate(DateUtil.getDate(returnOrder.getReturnEventLogs().get(returnOrder.getReturnEventLogs().size() - 1).getEventTime()));
            }else if(null != returnOrder.getCreateTime() ){
                payOrder.setFDate(DateUtil.getDate(returnOrder.getCreateTime()));
            }else{
                payOrder.setFDate(DateUtil.nowDate());
            }
            payOrder.setFDate(DateUtil.nowDate()  );
            if(null != returnOrder.getReturnEventLogs() && returnOrder.getReturnEventLogs().size() > 0){
                payOrder.setFDate(DateUtil.getDate(returnOrder.getReturnEventLogs().get(returnOrder.getReturnEventLogs().size() - 1).getEventTime()));
            }else if(null != returnOrder.getCreateTime() ){
                payOrder.setFDate(DateUtil.getDate(returnOrder.getCreateTime()));
            }else{
                payOrder.setFDate(DateUtil.nowDate());
            }
            payOrder.setOrderNumber(payWay.equals(PayWay.BALANCE) ? (refundOrder.getReturnOrderCode() + "G"): refundOrder.getReturnOrderCode());
            payOrder.setFSaleNum(returnOrder.getTid());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment;
            if (StringUtils.isEmpty(refundOrder.getRefundBill().getRefundComment())) {
                logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FNOTE");
                refundComment = "申请退款";
            } else {
                refundComment = refundOrder.getRefundBill().getRefundComment();
            }
            Map FSETTLETYPEID = new HashMap();
//            if (payWay.equals(PayWay.CASH)) {
//                //先下支付
//                FSETTLETYPEID.put("FNumber", "offlinepay");
//            } else {
//                FSETTLETYPEID.put("FNumber", payWay.toValue());
//            }

//            FSETTLETYPEID.put("FNumber", "QB");
//            //使用银行卡
//            Map FACCOUNTID = new HashMap();
//            if (payWay.equals(PayWay.ALIPAY)) {
//                FACCOUNTID.put("FNumber", kingdeeAlipay);
//            } else if (payWay.equals(PayWay.WECHAT)) {
//                FACCOUNTID.put("FNumber", kingdeeWechat);
//            } else if (payWay.equals(PayWay.UNIONPAY)) {
//                FACCOUNTID.put("FNumber", kingdeeWechat);
//            }
            if (payWay.equals(PayWay.BALANCE)) {
                FSETTLETYPEID.put("FNumber", "QB");
            }else {
                if (payWay.equals(PayWay.CASH)) {
//                //先下支付
                    FSETTLETYPEID.put("FNumber", "offlinepay");
                } else {
                    FSETTLETYPEID.put("FNumber", payWay.toValue());
                }
            }

            //使用银行卡
            Map FACCOUNTID = new HashMap();
            if (payWay.equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
            } else if (payWay.equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            } else if (payWay.equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            }else if (payWay.equals(PayWay.CUPSALI)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            }else if (payWay.equals(PayWay.CUPSWECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            }else if (payWay.equals(PayWay.CCB)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
            }else if(payWay.equals(PayWay.BALANCE)){
                FACCOUNTID.put("FNumber", "QB");
            }
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            BigDecimal price = BigDecimal.ZERO;
            if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) == 0 &&
                    returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) == 0 ){
                if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) == 1) {
                    //申请金额
                    price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                } else {
                    //商品总金额
                    price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                }
            }else{
                if (payWay.equals(PayWay.BALANCE)) {
                    price = returnOrder.getReturnPrice().getTotalBalanceRefundAmount().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                }else {
                    price = returnOrder.getReturnPrice().getTotalOnlineRefundAmount().setScale(2, BigDecimal.ROUND_DOWN);
                    payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
                }
            }

            // 退款收款单 佣金分开推送金蝶  不分开推了 230807
            /*BigDecimal commission = BigDecimal.ZERO;
            if (Objects.equals(payWay, PayWay.CCB)) {
                String tradeId = returnOrder.getTid();
                NewPileTrade trade = newPileTradeService.detail(tradeId);
                if (Objects.nonNull(trade) && Objects.nonNull(trade.getPayOrderNo())) {
                    CcbPayOrderRecordResponse recordResponse = ccbPayProvider.queryCcbPayOrderRecord(tradeId, trade.getPayOrderNo()).getContext();
                    if (Objects.nonNull(recordResponse)) {
                        log.info("推金蝶退款收款单，佣金部分，比例：{},退单金额：{}",recordResponse.getRatio(), price);
                        BigDecimal ratio = recordResponse.getRatio();
                        BigDecimal amt = price.multiply(ratio).setScale(2, RoundingMode.UP);
                        BigDecimal txnAmt = recordResponse.getTxnAmt();
                        if (amt.compareTo(txnAmt) >= 0) {
                            commission = recordResponse.getCommission();
                            amt = txnAmt.setScale(6, RoundingMode.DOWN);
                            payOrderSettlement.setFRECTOTALAMOUNTFOR(amt.toString());
                        }else {
                            commission = price.subtract(amt).setScale(6, RoundingMode.DOWN);
                            payOrderSettlement.setFRECTOTALAMOUNTFOR(amt.toString());
                        }
                    }
                    log.info("推金蝶退款收款单,佣金部分,计算佣金：{}", commission);
                }
            }

            if (commission.compareTo(BigDecimal.ZERO) > 0) {
                this.newPilePushRefundOrderCommissionKingdee(commission, refundOrder.getReturnOrderCode(), returnOrder.getTid(), returnOrder.getBuyer().getAccount());
            }*/


            payOrderSettlement.setFNOTE(refundComment + "-" + payOrder.getOrderNumber());
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map fSaleType = new HashMap();
            if (Objects.nonNull(returnOrder.getSaleType())) {
                fSaleType.put("FNumber", String.valueOf(returnOrder.getSaleType().toValue()));
                payOrder.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
                payOrder.setFSaleType(fSaleType);
            }

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.pushRefundOrderKingdee result1:{} returnFlowState:{} code:{}", result1.getResultData(), returnOrder.getReturnFlowState(), result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.pushRefundOrderKingdee error:{}", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return;
        } finally {
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(returnOrder.getTid());
                pushKingdeeRefundOrder.setRefundCode(refundOrder.getReturnOrderCode());
                pushKingdeeRefundOrder.setPayType(payWay.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(returnOrder.getBuyer().getAccount());
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(refundOrder.getReturnOrderCode());
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
            log.info("ReturnOrderService.pushRefundOrderKingdee ");
            if (pushKingdeeRefundOrder.getPushStatus().intValue() == PushKingdeeStatusEnum.PUSHSUCCESS.toStatus().intValue()
                    && payWay.equals(PayWay.BALANCE)) {
                //推送收款单
                pushPayOrderKingdee(returnOrder, payWay);
                //推送囤货退货单
                newPilepushAuditKingdee(returnOrder);
            }
        }
    }

    public void newPilePushRefundOrderCommissionKingdee(BigDecimal commission, String returnOrderCode, String tid, String account, String suffix) {
        returnOrderCode = returnOrderCode + suffix;
        tid = tid + suffix;

        log.info("囤货退款收款单推佣金送金蝶 单号:{},佣金:{}", returnOrderCode, commission);

        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(returnOrderCode);
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {

            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(returnOrderCode);
            payOrder.setFSaleNum(tid);
            Map FCustId = new HashMap();
            FCustId.put("FNumber", account);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment = "申请退款";

            Map FSETTLETYPEID = new HashMap();

            FSETTLETYPEID.put("FNumber", PayWay.CCB.toValue());

            // 使用银行卡
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", kingdeeCcbpay);

            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            payOrderSettlement.setFRECTOTALAMOUNTFOR(commission.toString());

            payOrderSettlement.setFNOTE(refundComment + "-" + returnOrderCode);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            // 销售类型
            Map fSaleType = new HashMap();
            fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
            payOrder.setFSaleType(fSaleType);

            // 登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                // 提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.pushRefundOrderCommissionKingdee result1:{}  code:{}", result1.getResultData(), result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.newPilePushRefundOrderCommissionKingdee error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            // 没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(tid);
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                pushKingdeeRefundOrder.setPayType(PayWay.CCB.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(account);
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }

        }
    }


    public boolean ticketsFormPushPayOrderKingdee(TicketsFormQueryVO ticketsForms, String payType) {
        logger.info("ReturnOrderService.ticketsFormPushPayOrderKingdee req RefundOrderId:{} payWay:{}", ticketsForms.getRecordNo(), payType);
        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(ticketsForms.getRecordNo());
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {
            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(ticketsForms.getRecordNo());
            payOrder.setFSaleNum(ticketsForms.getFormId() + "");
            Map FCustId = new HashMap();
            FCustId.put("FNumber", ticketsForms.getCustomerWallet().getCustomerAccount());
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment;
            refundComment = "提现申请";
            Map FSETTLETYPEID = new HashMap();
//            if (payWay.equals(PayWay.CASH)) {
//                //先下支付
//                FSETTLETYPEID.put("FNumber", "offlinepay");
//            } else {
//                FSETTLETYPEID.put("FNumber", payWay.toValue());
//            }
            FSETTLETYPEID.put("FNumber", payType);
            //使用银行卡
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", ticketsForms.getBankNo());
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);
            payOrderSettlement.setFRECTOTALAMOUNTFOR(ticketsForms.getArrivalPrice().toString());
            payOrderSettlement.setFNOTE(refundComment + "-" + payOrder.getOrderNumber());
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map fSaleType = new HashMap();
            fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
            payOrder.setFSaleType(fSaleType);

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.ticketsFormPushPayOrderKingdee result1:{} , code:{}", result1.getResultData(), result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.ticketsFormPushPayOrderKingdee error:{}", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return false;
        } finally {
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(ticketsForms.getRecordNo());
                pushKingdeeRefundOrder.setRefundCode(ticketsForms.getRecordNo());
                pushKingdeeRefundOrder.setPayType(payType);
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(ticketsForms.getCustomerWallet().getCustomerAccount());
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(ticketsForms.getRecordNo());
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
        }
        return true;
    }

    /**
     * 向金蝶push支付单
     */
    public Boolean pushPayOrderKingdee(ReturnOrder returnOrder, PayWay payWay) {
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            return true;//非自营商家不考虑
        }
        logger.info("returnOrder.pushPayOrderKingdee req {}", JSONObject.toJSONString(returnOrder));
        Boolean resultState = false;
        //查询支付记录中是否有支付单
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(returnOrder.getId());
        try {
            KingdeePayOrder payOrder = new KingdeePayOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(returnOrder.getId());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
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
            FSETTLETYPEID.put("FNumber", "QB");

            /**
             *
             * 结算方式需要修改
             * */
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式

//            if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) == 1) {
//                //申请金额
//                BigDecimal price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, BigDecimal.ROUND_DOWN);
//                payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
//            } else {
//                //商品总金额
//                BigDecimal price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
//                payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
//            }

            BigDecimal price = returnOrder.getReturnPrice().getTotalBalanceRefundAmount().setScale(2, BigDecimal.ROUND_DOWN);
            payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());

            //销售订单号
            payOrderSettlement.setF_ora_YDDH(returnOrder.getTid());

            //使用银行卡,先下支付不用传银行卡，是先下支付trade.getPayWay()为空
            Map FACCOUNTID = new HashMap();
            if (payWay != null && payWay.equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (payWay != null && payWay.equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (payWay != null && payWay.equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", "103");
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            }
            //线下支付将收款银行信息推到金蝶
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
                tradePushKingdeePayOrder.setPayCode(returnOrder.getId());
                tradePushKingdeePayOrder.setOrderCode(returnOrder.getTid());
                tradePushKingdeePayOrder.setPayType(payWay.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) == 1) {
                    //申请金额
                    BigDecimal price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    tradePushKingdeePayOrder.setPracticalPrice(price);
                } else {
                    //商品总金额
                    BigDecimal price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    tradePushKingdeePayOrder.setPracticalPrice(price);
                }
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(returnOrder.getId());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
            return resultState;
        }
    }

    /**
     * 向金蝶push支付单
     */
    public Boolean pushPayOrderKingdee(NewPileReturnOrder returnOrder, PayWay payWay) {
        logger.info("returnOrder.pushPayOrderKingdee req {}", JSONObject.toJSONString(returnOrder));
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            return true;//非自营商家不考虑
        }
        Boolean resultState = false;
        //查询支付记录中是否有支付单
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(returnOrder.getId());
        try {
            KingdeePayOrder payOrder = new KingdeePayOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(returnOrder.getId());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", returnOrder.getBuyer().getAccount());
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
            FSETTLETYPEID.put("FNumber", "QB");

            /**
             *
             * 结算方式需要修改
             * */
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式

            if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) == 1) {
                //申请金额
                BigDecimal price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, BigDecimal.ROUND_DOWN);
                payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
            } else {
                //商品总金额
                BigDecimal price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
                payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
            }


            //销售订单号
            payOrderSettlement.setF_ora_YDDH(returnOrder.getTid());

            //使用银行卡,先下支付不用传银行卡，是先下支付trade.getPayWay()为空
            Map FACCOUNTID = new HashMap();
            if (payWay != null && payWay.equals(PayWay.ALIPAY)) {
                FACCOUNTID.put("FNumber", kingdeeAlipay);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (payWay != null && payWay.equals(PayWay.WECHAT)) {
                FACCOUNTID.put("FNumber", kingdeeWechat);
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            } else if (payWay != null && payWay.equals(PayWay.UNIONPAY)) {
                FACCOUNTID.put("FNumber", "103");
                payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            }
            //线下支付将收款银行信息推到金蝶
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
                tradePushKingdeePayOrder.setPayCode(returnOrder.getId());
                tradePushKingdeePayOrder.setOrderCode(returnOrder.getTid());
                tradePushKingdeePayOrder.setPayType(payWay.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) == 1) {
                    //申请金额
                    BigDecimal price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    tradePushKingdeePayOrder.setPracticalPrice(price);
                } else {
                    //商品总金额
                    BigDecimal price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    tradePushKingdeePayOrder.setPracticalPrice(price);
                }
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(returnOrder.getId());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
            return resultState;
        }
    }

    /**
     * 向金蝶push支付单
     */
    public Boolean balanceFormPushPayOrderKingdee(AddWalletRecordRequest addWalletRecordRequest) {
        logger.info("balanceFormPushPayOrderKingdee.pushPayOrderKingdee req {}", JSONObject.toJSONString(addWalletRecordRequest));
        Boolean resultState = false;
        //查询支付记录中是否有支付单
        TradePushKingdeePayOrder tradePushKingdeePayOrder = new TradePushKingdeePayOrder();
        tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        Integer number = tradePushKingdeePayRepository.selectPushKingdeePayOrderNumber(addWalletRecordRequest.getRecordNo());
        try {
            KingdeePayOrder payOrder = new KingdeePayOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(addWalletRecordRequest.getRecordNo());
            Map FCustId = new HashMap();
            FCustId.put("FNumber", addWalletRecordRequest.getCustomerAccount());
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);//收款组织
            //收款方式
            payOrder.setFColType("线上");
            List<KingdeePayOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeePayOrderSettlement payOrderSettlement = new KingdeePayOrderSettlement();
            Map FSETTLETYPEID = new HashMap();
            FSETTLETYPEID.put("FNumber", "QB");

            /**
             *
             * 结算方式需要修改
             * */
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);//结算方式

            if (addWalletRecordRequest.getDealPrice().compareTo(BigDecimal.ZERO) == 1) {
                //申请金额
                BigDecimal price = addWalletRecordRequest.getDealPrice().setScale(2, BigDecimal.ROUND_DOWN);
                payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
            } else {
                //商品总金额
                BigDecimal price = addWalletRecordRequest.getDealPrice().setScale(2, BigDecimal.ROUND_DOWN);
                payOrderSettlement.setFRECTOTALAMOUNTFOR(price.toString());
            }


            //销售订单号
            payOrderSettlement.setF_ora_YDDH(addWalletRecordRequest.getRelationOrderId());

            //使用银行卡,先下支付不用传银行卡，是先下支付trade.getPayWay()为空
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", "103");
            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            //线下支付将收款银行信息推到金蝶
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
                log.info("收款单推金蝶1111---实际推送内容-->" + JSONObject.toJSONString(payOrder));
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(payUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                logger.info("balanceFormPushPayOrderKingdee.pushPayOrderKingdee result1:{} code:{}", httpCommonResult.getResultData(), kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                    resultState = true;
                } else {
                    tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeePayOrder.setInstructions(httpCommonResult.getResultData());
            } else {
                logger.error("balanceFormPushPayOrderKingdee.pushPayOrderKingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeePayOrder.setInstructions(res);
                tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("balanceFormPushPayOrderKingdee.pushPayOrderKingdee error:{}", e);
            String res = "金蝶推送失败";
            tradePushKingdeePayOrder.setInstructions(res);
            tradePushKingdeePayOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {

            if (number == 0) {
                tradePushKingdeePayOrder.setPayCode(addWalletRecordRequest.getRecordNo());
                tradePushKingdeePayOrder.setOrderCode(addWalletRecordRequest.getRelationOrderId());
                tradePushKingdeePayOrder.setPayType(PayWay.UNIONPAY.toValue());
                tradePushKingdeePayOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                if (addWalletRecordRequest.getDealPrice().compareTo(BigDecimal.ZERO) == 1) {
                    //申请金额
                    BigDecimal price = addWalletRecordRequest.getDealPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    tradePushKingdeePayOrder.setPracticalPrice(price);
                } else {
                    //商品总金额
                    BigDecimal price = addWalletRecordRequest.getDealPrice().setScale(2, BigDecimal.ROUND_DOWN);
                    tradePushKingdeePayOrder.setPracticalPrice(price);
                }
                tradePushKingdeePayRepository.saveAndFlush(tradePushKingdeePayOrder);
            } else {
                tradePushKingdeePayOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeePayOrder.setPayCode(addWalletRecordRequest.getRecordNo());
                tradePushKingdeePayRepository.updatePushKingdeePayOrderState(tradePushKingdeePayOrder);
            }
            return resultState;
        }
    }


    private Boolean checkRefundOrderKingdee(ReturnOrder returnOrder, RefundOrder refundOrder, PayWay payWay, TradePushKingdeeRefund pushKingdeeRefundOrder) {
        if (StringUtils.isEmpty(returnOrder.getTid())) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack Tid");
            pushKingdeeRefundOrder.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getBuyer().getAccount())) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FCustId");
            pushKingdeeRefundOrder.setInstructions("Lack FCustId");
            return false;
        }
        if (StringUtils.isEmpty(refundOrder.getReturnOrderCode())) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack ReturnOrderCode");
            pushKingdeeRefundOrder.setInstructions("Lack ReturnOrderCode");
            return false;
        }
        if (!(payWay == PayWay.CASH) && StringUtils.isEmpty(refundOrder.getRefundBill().getPayChannel())&&payWay != PayWay.CCB) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FSETTLETYPEID");
            pushKingdeeRefundOrder.setInstructions("Lack FSETTLETYPEID");
            return false;
        }
        if (refundOrder.getRefundBill().getActualReturnPrice() == null) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FRECTOTALAMOUNTFOR");
            pushKingdeeRefundOrder.setInstructions("Lack FRECTOTALAMOUNTFOR");
            return false;
        }
        return true;
    }

    private Boolean checkRefundOrderKingdee(NewPileReturnOrder returnOrder, RefundOrder refundOrder, PayWay payWay, TradePushKingdeeRefund pushKingdeeRefundOrder) {
        if (StringUtils.isEmpty(returnOrder.getTid())) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack Tid");
            pushKingdeeRefundOrder.setInstructions("Lack Tid");
            return false;
        }
        if (StringUtils.isEmpty(returnOrder.getBuyer().getAccount())) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FCustId");
            pushKingdeeRefundOrder.setInstructions("Lack FCustId");
            return false;
        }
        if (StringUtils.isEmpty(refundOrder.getReturnOrderCode())) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack ReturnOrderCode");
            pushKingdeeRefundOrder.setInstructions("Lack ReturnOrderCode");
            return false;
        }
        if (!(payWay == PayWay.CASH) && StringUtils.isEmpty(refundOrder.getRefundBill().getPayChannel())&&payWay != PayWay.CCB) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FSETTLETYPEID");
            pushKingdeeRefundOrder.setInstructions("Lack FSETTLETYPEID");
            return false;
        }
        if (refundOrder.getRefundBill().getActualReturnPrice() == null) {
            logger.info("ReturnOrderService.pushRefundOrderKingdee Lack FRECTOTALAMOUNTFOR");
            pushKingdeeRefundOrder.setInstructions("Lack FRECTOTALAMOUNTFOR");
            return false;
        }
        return true;
    }

    /**
     * 向金蝶发生取消订单
     *
     * @param returnOrder  退款单
     * @param messageQueue 是否用消息队列
     * @return
     */
    public Boolean cancelOrder(ReturnOrder returnOrder, Boolean messageQueue) {
        if(Objects.nonNull(returnOrder.getCompany()) && Objects.nonNull(returnOrder.getCompany().getCompanyType()) && !orderCommonService.isSelf(returnOrder.getCompany().getCompanyType())) {
            return true;//非自营商家不考虑
        }
        logger.info("ReturnOrderService.cancelOrder req orderId:{} returnFlowState:{}", returnOrder.getTid(), returnOrder.getReturnFlowState());
        Boolean result = false;
        //查询销售订单推送是否成功
        TradePushKingdeeOrder pushKingdeeOrder = tradePushKingdeeOrderRepository.queryPushKingdeeOrder(returnOrder.getTid());
        if (Objects.isNull(pushKingdeeOrder)) {
            logger.info("ReturnOrderService.cancelOrder pushKingdeeOrder is null orderId:{}", returnOrder.getTid());
            //通过延时队列发送取消订单
//            if (messageQueue) {
//                orderProducerService.kingdeeCancelOrder(returnOrder, 3 * 60 * 1000L);
//            }
            return false;
        } else if (Objects.nonNull(pushKingdeeOrder) && pushKingdeeOrder.getOrderStatus().equals(1)) {
            //销售单状态为取消，不处理直接返回
            return true;
        }
        TradePushKingdeeOrder tradePushKingdeeOrder = new TradePushKingdeeOrder();
        tradePushKingdeeOrder.setOrderCode(returnOrder.getTid());
        tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CREATE.toOrderStatus());
        try {
            //销售订单推送成功，请求金蝶取消接口
            if (returnOrder.getTid() == null) {
                logger.info("ReturnOrderService.cancelOrder orderId is null");
                tradePushKingdeeOrder.setCancelOperation("参数错误");
                tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.PARAMETERERROR.toOrderStatus());
                result = false;
                return result;
            }
            KingdeeSalesOrder order = new KingdeeSalesOrder();
            order.setOrderNumber(returnOrder.getTid());
            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", order);
                HttpCommonResult httpCommonResult = HttpCommonUtil.postHeader(orderInvalidUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(httpCommonResult.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.cancelOrder result:{}", httpCommonResult.getResultData());
                if (StringUtils.isNotEmpty(httpCommonResult.getResultData()) && httpCommonResult.getResultData().length() > 50) {
                    String resultStr = httpCommonResult.getResultData().substring(0, 20);
                    tradePushKingdeeOrder.setCancelOperation(resultStr);
                } else {
                    tradePushKingdeeOrder.setCancelOperation(httpCommonResult.getResultData());
                }
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CANCELSUCCESS.toOrderStatus());
                    result = true;
                } else {
                    tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CANCELFAILURE.toOrderStatus());
                    result = false;
                }
            } else {
                logger.error("ReturnOrderService.cancelOrder push kingdee error");
                tradePushKingdeeOrder.setCancelOperation("金蝶登录失败");
                tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CANCELFAILURE.toOrderStatus());
                result = false;
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.cancelOrder error orderId:{} er:{}", returnOrder.getTid(), e);
            tradePushKingdeeOrder.setCancelOperation("金蝶取消推送失败");
            tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CANCELFAILURE.toOrderStatus());
            result = false;
        } finally {
            tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
            tradePushKingdeeOrderRepository.updatePushKingdeeCancelOrderState(tradePushKingdeeOrder);
            return result;
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
        ReturnOrder returnOrder = findById(rid);

        // 如果offlineAccount非空，新增后使用
        if (Objects.nonNull(customerAccount)) {

            CustomerAccountByCustomerIdRequest customerAccountByCustomerIdRequest =
                    CustomerAccountByCustomerIdRequest.builder().customerId(customerAccount.getCustomerId()).build();
            //查询会员有几条银行账户信息
            BaseResponse<CustomerAccountByCustomerIdResponse> integerBaseResponse =
                    customerAccountQueryProvider.countByCustomerId(customerAccountByCustomerIdRequest);
            Integer count = integerBaseResponse.getContext().getResult();
            if (null != count && count >= 5) {
                //会员最多有5条银行账户信息
                throw new SbcRuntimeException("K-010005");
            }

            CustomerAccountAddRequest customerAccountAddRequest = new CustomerAccountAddRequest();
            BeanUtils.copyProperties(customerAccount, customerAccountAddRequest);

            // 客户编号
            customerAccountAddRequest.setCustomerId(returnOrder.getBuyer().getId());
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
        ReturnOrder returnOrder = findById(rid);
//        BigDecimal price = returnOrder.getReturnItems().stream().map(r -> r.getSplitPrice())
//                .reduce(BigDecimal::add).get();
//
//
//        if(refundBill.getActualReturnPrice().compareTo(price) == 1){
//            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
//        }

        // 积分信息
        returnOrder.getReturnPoints().setActualPoints(refundBill.getActualReturnPoints());
        // 退货金额 (改价的退款金额 小于 生成退单时的计算金额)
        if (refundBill.getActualReturnPrice().compareTo(returnOrder.getReturnPrice().getApplyPrice()) == -1) {
            Trade trade = tradeService.detail(returnOrder.getTid());
            if (DeliverStatus.NOT_YET_SHIPPED.equals(trade.getTradeState().getDeliverStatus())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单未发货，暂不支持售前改价");
            }
            returnOrder.getReturnPrice().setApplyStatus(true);
            returnOrder.getReturnPrice().setApplyPrice(refundBill.getActualReturnPrice());
        }
        // 如果customerAccount非空，临时账号，当快照冗余在退单
        if (Objects.nonNull(customerAccount)) {
            // 客户编号
            customerAccount.setCustomerId(returnOrder.getBuyer().getId());
            returnOrder.setCustomerAccount(customerAccount);
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
                returnOrder.setCustomerAccount(customerAccount);
            } else {
                throw new SbcRuntimeException("K-070009");
            }
        }
        // 根据退款单号查询财务退款单据的编号
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        if (refundOrder.getRefundStatus().equals(RefundStatus.APPLY) || returnOrder.getReturnFlowState().equals
                (ReturnFlowState.REJECT_REFUND)) {
            throw new SbcRuntimeException("K-050002", new Object[]{"退款"});
        }
        if (!Objects.isNull(refundOrder.getRefundBill())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        refundBill.setRefundId(refundOrder.getRefundId());
        // 生成退款记录
        refundBillService.save(refundBill);
        returnOrder.appendReturnEventLog(
                ReturnEventLog.builder()
                        .operator(operator)
                        .eventType(ReturnEvent.REFUND.getDesc())
                        .eventTime(LocalDateTime.now())
                        .eventDetail(String.format("退单[%s]已添加线下退款单，操作人:%s", returnOrder.getId(), operator.getName()))
                        .build()
        );
        refundOrder.setRefundStatus(RefundStatus.APPLY);
        refundOrderRepository.save(refundOrder);
        returnOrderService.updateReturnOrder(returnOrder);
    }

    public void refillReturnBalancePriceAndCashPrice(ReturnOrder returnOrder, BigDecimal modifyPrice) {
        Assert.notNull(returnOrder.getActivityType(), "returnOrder.getActivityType() 不能为null");
        if (modifyPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SbcRuntimeException("改价金额不能小于0");
        }

        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        if (modifyPrice.compareTo(returnPrice.getTotalPrice()) > 0) {
            throw new SbcRuntimeException("改价金额不能大于总可退金额");
        }

        BigDecimal goodsPaidPrice = returnPrice.getActualReturnCash().add(returnPrice.getActualBalanceReturnPrice());
        if (modifyPrice.compareTo(goodsPaidPrice) > 0) {
            throw new SbcRuntimeException("售后改价金额不能大于商品实付金额");
        }

        returnPrice.setDeliveryPrice(BigDecimal.ZERO);
        returnPrice.setPackingPrice(BigDecimal.ZERO);
        if (modifyPrice.compareTo(returnPrice.getActualReturnCash()) > 0) {
            //修改价格大于 付现金：现金金额不变，退余额 = 修改价格 - 退现金; 退运费=0
            returnPrice.setActualBalanceReturnPrice(modifyPrice.subtract(returnPrice.getActualReturnCash()));
            returnPrice.setDeliveryPrice(BigDecimal.ZERO);
        } else {
            //修改价格小于等于 付现金：现金金额=修改价格，退余额 =0
            returnPrice.setActualReturnCash(modifyPrice);
            returnPrice.setActualBalanceReturnPrice(BigDecimal.ZERO);
        }

        if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(returnOrder.getActivityType())) {
            //清空实退金额
            for (ReturnItem item : returnOrder.getReturnItems()) {
                for (PickGoodsDTO pickGoodsDTO : item.getReturnGoodsList()) {
                    pickGoodsDTO.setActualReturnCashPrice(BigDecimal.ZERO);
                    pickGoodsDTO.setActualReturnBalancePrice(BigDecimal.ZERO);
                }
            }
            //新提货退单需分摊到每个退的囤货单
            for (ReturnItem item : returnOrder.getReturnItems()) {
                for (PickGoodsDTO pickGoodsDTO : item.getReturnGoodsList()) {
                    if (modifyPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new SbcRuntimeException("改价分摊异常-1");
                    }

                    if (modifyPrice.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }

                    if (modifyPrice.compareTo(pickGoodsDTO.getReturnCashPrice()) >= 0) {
                        pickGoodsDTO.setActualReturnCashPrice(pickGoodsDTO.getReturnCashPrice());
                        modifyPrice = modifyPrice.subtract(pickGoodsDTO.getReturnCashPrice());
                    } else {
                        pickGoodsDTO.setActualReturnCashPrice(modifyPrice);
                        modifyPrice = BigDecimal.ZERO;
                        break;
                    }
                }
            }

            //分摊完实付金额后，还有剩余，继续分摊到余额
            for (ReturnItem returnItem : returnOrder.getReturnItems()) {
                for (PickGoodsDTO pickGoodsDTO : returnItem.getReturnGoodsList()) {
                    if (modifyPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new SbcRuntimeException("改价分摊异常：2");
                    }

                    if (modifyPrice.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }

                    if (modifyPrice.compareTo(pickGoodsDTO.getReturnBalancePrice()) >= 0) {
                        pickGoodsDTO.setActualReturnBalancePrice(pickGoodsDTO.getReturnBalancePrice());
                        modifyPrice = modifyPrice.subtract(pickGoodsDTO.getReturnBalancePrice());
                    } else {
                        pickGoodsDTO.setActualReturnBalancePrice(modifyPrice);
                        modifyPrice = BigDecimal.ZERO;
                        break;
                    }
                }
            }

            //重算每个退项的总实退金额
            for (ReturnItem item : returnOrder.getReturnItems()) {
                for (PickGoodsDTO pickGoodsDTO : item.getReturnGoodsList()) {
                    pickGoodsDTO.setActualSplitPrice(
                            pickGoodsDTO.getActualReturnCashPrice().add(pickGoodsDTO.getActualReturnBalancePrice())
                    );
                }
            }
        }
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
        //修改退款单状态
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(rid);
        Optional<RefundBill> result = refundBillService.save(refundBill);
        refundOrder.setRefundBill(result.get());

        // 线下退款完成，发送MQ消息
        ReturnOrder returnOrder = findById(rid);
        refund(rid, operator, refundBill.getActualReturnPrice());
        refundOrder.setRefundStatus(RefundStatus.FINISH);
        refundOrderRepository.saveAndFlush(refundOrder);

        Trade trade = tradeService.detail(returnOrder.getTid());
        //退优惠券
        if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
            //DONE: 退款开关，提货退款需审核-囤货提
            RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.NEW_PICK_RETURN)
                    .refund(trade, returnOrder);
            returnOrderService.returnCoupon(rid);
        } else {
            //DONE: 退款开关，提货退款需审核-普通提
            RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.TRADE_RETURN)
                    .refund(trade, returnOrder);
            tradeService.returnCoupon(tid);
        }

        // 如果全部退完，则更新订单状态为已作废
//        if(returnOrderService.isReturnFull(returnOrder)){
//            tradeService.voidTrade(returnOrder.getTid(),operator);
//        }
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnOrder.getBuyer().getId())
                .orderId(returnOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnOrderFlow(sendMQRequest);
        //金蝶退款
        if (kingdeeOpenState) {//判断是否开启新金蝶
            if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.TRADE.toActivityType())) {
                PayTradeRecordResponse payTradeRecordResponse = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(returnOrder.getId())).getContext();
                PayChannelItemResponse channelItemResponse = payQueryProvider.getChannelItemById(new ChannelItemByIdRequest(Objects.isNull(payTradeRecordResponse) ||
                        Objects.isNull(payTradeRecordResponse.getChannelItemId()) ?
                        Constants.DEFAULT_RECEIVABLE_ACCOUNT :
                        payTradeRecordResponse.getChannelItemId())).getContext();
                log.info("ReturnOrderService.s2bBoosRefundOffline Channel:{} rid:{}", channelItemResponse.getChannel(), returnOrder.getId());
                //向金蝶push退款单
                if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                    if (StringUtils.isNotEmpty(channelItemResponse.getChannel())) {
                        pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf(channelItemResponse.getChannel().toUpperCase()));
                    } else {
                        pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                    }
                }
            } else if (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats()) {
                //向金蝶push退款单
                if (RefundStatus.FINISH.equals(refundOrder.getRefundStatus())) {
                    pushRefundOrderKingdee(returnOrder, refundOrder, PayWay.valueOf("CASH".toUpperCase()));
                }
            }
        }
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
        ReturnOrder returnOrder = findById(rid);
        TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                (returnOrder.getTid(), returnOrder.getId())).getContext().getTradeStatus();
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
        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(ReturnEvent.REJECT_REFUND)
                .data(reason)
                .build();
        returnFSMService.changeState(request);

        // 拒绝退款时，发送MQ消息
        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnOrder.getBuyer().getId())
                .orderId(returnOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnOrderFlow(sendMQRequest);

        //退款审核未通过发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName(), reason);
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.REFUND_CHECK_NOT_PASS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount());
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
            ReturnOrder returnOrder = findById(refundOrderResponse.getReturnOrderCode());
            TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                    (returnOrder.getTid(), returnOrder.getId())).getContext().getTradeStatus();
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
            returnFSMService.changeState(request);
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
        returnFSMService.changeState(request);

        // 取消退单时，发送MQ消息
        ReturnOrder returnOrder = findById(rid);
        if (CollectionUtils.isNotEmpty(returnOrder.getSendCouponCodeIds())) {
            //撤回删除的赠券
            couponCodeProvider.recallDelCouponCodes(CouponCodeDeleteRequest.builder()
                    .customerId(returnOrder.getBuyer().getId()).couponCodeIds(returnOrder.getSendCouponCodeIds()).build());
        }
        //新提货退单，撤销锁定的分摊金额
        if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(returnOrder.getActivityType())) {
            inventoryDetailSamountService.unlockAmountByRid(rid);
        } else if (TradeActivityTypeEnum.TRADE.toActivityType().equals(returnOrder.getActivityType())) {
            // 同步到价格商品列表 (0未退款)
            this.updateInventoryDetailSamountTrade(returnOrder, 0, null);
        }

        ReturnOrderSendMQRequest sendMQRequest = ReturnOrderSendMQRequest.builder()
                .addFlag(Boolean.FALSE)
                .customerId(returnOrder.getBuyer().getId())
                .orderId(returnOrder.getTid())
                .returnId(rid)
                .build();
        returnOrderProducerService.returnOrderFlow(sendMQRequest);

        //售后审核未通过发送MQ消息
        List<String> params = Lists.newArrayList(returnOrder.getReturnItems().get(0).getSkuName(), remark);
        String pic = returnOrder.getReturnItems().get(0).getPic();
        sendNoticeMessage(NodeType.RETURN_ORDER_PROGRESS_RATE,
                ReturnOrderProcessType.AFTER_SALE_ORDER_CHECK_NOT_PASS,
                params,
                returnOrder.getId(),
                returnOrder.getBuyer().getId(),
                pic,
                returnOrder.getBuyer().getAccount());

    }

    /**
     * 功能描述: <br>排除特价商品重新计算可退货数量
     * 〈〉
     *
     * @Param: [trade]
     * @Return: java.lang.Boolean
     * @Date: 2020/9/25 10:46
     */
    public Boolean removeSpecial(Trade trade) {
        //排除特价商品

        // trade.getTradeItems().removeIf(next -> Objects.nonNull(next.getGoodsInfoType()) && next.getGoodsInfoType() == 1);
        //计算商品可退数
        Map<String, BigDecimal> map = findLeftItems(trade);
        BigDecimal total = BigDecimal.ZERO;
        Optional<BigDecimal> goods = map.values().stream().reduce(BigDecimal::add);
        if (goods.isPresent()) {
            total = total.add(goods.get());
        }
        //计算赠品可退数
        if (CollectionUtils.isNotEmpty(trade.getGifts())) {
            Map<String, BigDecimal> giftItems = findLeftGiftItems(trade);
            Optional<BigDecimal> gift = giftItems.values().stream().reduce(BigDecimal::add);
            if (gift.isPresent()) {
                total = total.add(gift.get());
            }
        }
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return true;
    }

    /**
     * 查询订单详情,如已发货则带出可退商品数
     *
     * @param tid
     * @return
     */
    public Trade queryCanReturnItemNumByTid(String tid) {
        Trade trade = tradeService.detail(tid);
        //校验支付状态trade
        PayOrderResponse payOrder = payOrderService.findPayOrder(trade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
        if (trade.getTradeState().getFlowState() == FlowState.GROUPON) {
            throw new SbcRuntimeException("K-050141");
        }
        /*if(trade.getDeliverWay() == DeliverWay.PICK_SELF){
            throw new SbcRuntimeException("K-050142");
        }*/
        //排除特价商品
        // trade.getTradeItems().removeIf(next -> Objects.nonNull(next.getGoodsInfoType()) && next.getGoodsInfoType() == 1);
        DeliverStatus deliverStatus = trade.getTradeState().getDeliverStatus();
        if (deliverStatus != DeliverStatus.NOT_YET_SHIPPED && deliverStatus != DeliverStatus.VOID) {
            //计算商品可退数
            Map<String, BigDecimal> map = findLeftItems(trade);
            trade.getTradeItems().forEach(
                    item -> item.setCanReturnNum(map.get(item.getSkuId()))
            );

            //计算赠品可退数
            if (CollectionUtils.isNotEmpty(trade.getGifts())) {
                Map<String, BigDecimal> giftMap = findLeftGiftItems(trade);
                trade.getGifts().forEach(
                        item -> item.setCanReturnNum(giftMap.get(item.getSkuId()))
                );
            }
        } else {
            //未发货时全部可退
            trade.getTradeItems().forEach(
                    item -> item.setCanReturnNum(BigDecimal.valueOf(item.getNum()))
            );
            trade.getGifts().forEach(
                    item -> item.setCanReturnNum(BigDecimal.valueOf(item.getNum()))
            );
        }

        //过滤可退商品为0的商品
        List<TradeItem> tradeItemList = trade.getTradeItems().stream().filter(t -> BigDecimal.ZERO.compareTo(t.getCanReturnNum()) == NumberUtils.INTEGER_MINUS_ONE).collect(Collectors.toList());

        trade.setTradeItems(tradeItemList);

        List<ReturnOrder> returnsNotVoid = findReturnsNotVoid(tid);
        // 已退积分
        Long retiredPoints = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPoints()) && Objects.nonNull(o.getReturnPoints().getActualPoints()))
                .map(o -> o.getReturnPoints().getActualPoints())
                .reduce((long) 0, Long::sum);
        // 可退积分
        Long points = trade.getTradePrice().getPoints() == null ? 0 : trade.getTradePrice().getPoints();
        trade.setCanReturnPoints(points - retiredPoints);
        // 可退金额
        BigDecimal totalPrice = trade.getTradeItems().stream()
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal retiredPrice = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
                .map(o -> o.getReturnPrice().getActualReturnPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
        trade.setCanReturnPrice(canReturnPrice);
        return trade;
    }

    /**
     * 查询拆箱订单详情,如已发货则带出可退商品数
     *
     * @param tid
     * @return
     */
    public Trade queryCanReturnDevanningItemNumByTid(String tid) {
        Trade trade = tradeService.detail(tid);
        if (trade.getTradeState().getFlowState() == FlowState.GROUPON) {
            throw new SbcRuntimeException("K-050141");
        }

        if (!trade.getActivityType().equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType())) {
            tradeComputer(trade);
        } else {
            tradePickComputer(trade);
        }
        return trade;
    }


    public void tradePickComputer(Trade trade) {
        DeliverStatus deliverStatus = trade.getTradeState().getDeliverStatus();
        //计算商品可退数
        fillCanReturnNum(trade, deliverStatus);

        //过滤可退商品为0的商品
        List<TradeItem> tradeItemList = trade.getTradeItems().stream()
                .filter(t -> BigDecimal.ZERO.compareTo(t.getCanReturnNum()) == NumberUtils.INTEGER_MINUS_ONE)
                .collect(Collectors.toList());
        trade.setTradeItems(tradeItemList);
        trade.setCanReturnPoints(0l);

        //筛选出未退的商品
        List<InventoryDetailSamountVO> unassignedAmountList = newPileReturnOrderService.getUnassignedAmountListByTakeId(trade.getId());
        //填充分摊金额
        fillTradeItemAmountList(trade.getTradeItems(), unassignedAmountList);
        //提货单计算可退金额
        BigDecimal sumPayPrice = BigDecimal.ZERO;
        BigDecimal sumBalancePrice = BigDecimal.ZERO;
        BigDecimal totalPrice;

        Map<Integer, BigDecimal> amountByMoneyTypeMap = unassignedAmountList.stream().collect(
                Collectors.groupingBy(InventoryDetailSamountVO::getMoneyType,
                        Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
        if (Objects.nonNull(amountByMoneyTypeMap.get(0))) {
            sumBalancePrice = amountByMoneyTypeMap.get(0);
        }
        if (Objects.nonNull(amountByMoneyTypeMap.get(1))) {
            sumPayPrice = amountByMoneyTypeMap.get(1);
        }

        //重新赋值使用金额
        Map<String, BigDecimal> amountBySkuMap = unassignedAmountList.stream().collect(
                Collectors.groupingBy(InventoryDetailSamountVO::getGoodsInfoId,
                        Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
        trade.getTradeItems().forEach(item -> {
            List<TradeItem.WalletSettlement> arr = new ArrayList<>();
            TradeItem.WalletSettlement walletSettlement = new TradeItem.WalletSettlement();
            walletSettlement.setSplitPrice(amountBySkuMap.get(item.getSkuId()));
            arr.add(walletSettlement);
            item.setWalletSettlements(arr);
        });

        totalPrice = sumPayPrice.add(sumBalancePrice);
        if (deliverStatus != DeliverStatus.NOT_YET_SHIPPED && deliverStatus != DeliverStatus.VOID) {
            totalPrice = totalPrice.add(trade.getTradePrice().getDeliveryPrice());
        }
        trade.setBalanceReturnPrice(sumBalancePrice);
        trade.setCanReturnPrice(totalPrice);
        trade.getTradePrice().setTotalPrice(totalPrice);
    }

    private void fillTradeItemAmountList(List<TradeItem> tradeItems, List<InventoryDetailSamountVO> unassignedAmountList) {
        Map<String, List<InventoryDetailSamountTrade>> mergeAmountListBySkuIdMap = newPileReturnOrderService.mergeAmountList(unassignedAmountList);
        tradeItems.forEach(item -> {
            item.setInventoryDetailSamountTrades(mergeAmountListBySkuIdMap.get(item.getSkuId()));
        });
    }

    /**
     * 计算商品可退数
     * @param trade
     * @param deliverStatus
     */
    private void fillCanReturnNum(Trade trade, DeliverStatus deliverStatus) {
        if (deliverStatus != DeliverStatus.NOT_YET_SHIPPED && deliverStatus != DeliverStatus.VOID) {
            //计算商品可退数
            TradeItem tradeItem = trade.getTradeItems().stream().findFirst().orElse(null);
            log.info("devannTradeItem===== {}", JSONObject.toJSONString(tradeItem));
            if (Objects.nonNull(tradeItem.getDevanningId())) {
                Map<Long, BigDecimal> map = findLeftDevanningItems(trade);
                trade.getTradeItems().forEach(
                        item -> item.setCanReturnNum(map.get(item.getDevanningId()))
                );
            } else {
                Map<String, BigDecimal> leftItemsMap = this.findLeftItems(trade);
                trade.getTradeItems().forEach(
                        item -> item.setCanReturnNum(leftItemsMap.get(item.getSkuId()))
                );
            }

        } else {
            //未发货时全部可退
            trade.getTradeItems().forEach(
                    item -> item.setCanReturnNum(BigDecimal.valueOf(item.getNum()))
            );
        }
    }

    /**
     * 查询拆箱订单详情,如已发货则带出可退商品数
     *
     * @param
     * @return
     */
    public void tradeComputer(Trade trade) {
        //校验支付状态trade
        PayOrderResponse payOrder = payOrderService.findPayOrder(trade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
        //排除特价商品
        DeliverStatus deliverStatus = trade.getTradeState().getDeliverStatus();
        if (deliverStatus != DeliverStatus.NOT_YET_SHIPPED && deliverStatus != DeliverStatus.VOID) {
            //计算商品可退数
            TradeItem tradeItem = trade.getTradeItems().stream().findFirst().orElse(null);
            log.info("devannTradeItem===== {}", JSONObject.toJSONString(tradeItem));
            if (Objects.nonNull(tradeItem.getDevanningId())) {
                Map<Long, BigDecimal> map = findLeftDevanningItems(trade);
                trade.getTradeItems().forEach(
                        item -> item.setCanReturnNum(map.get(item.getDevanningId()))
                );
            } else {
                Map<String, BigDecimal> leftItemsMap = this.findLeftItems(trade);
                trade.getTradeItems().forEach(
                        item -> item.setCanReturnNum(leftItemsMap.get(item.getSkuId()))
                );
            }

            //计算赠品可退数
            if (CollectionUtils.isNotEmpty(trade.getGifts())) {
                Map<String, BigDecimal> giftMap = findLeftGiftItems(trade);
                trade.getGifts().forEach(
                        item -> item.setCanReturnNum(giftMap.get(item.getSkuId()))
                );
            }
        } else {
            //未发货时全部可退
            trade.getTradeItems().forEach(
                    item -> item.setCanReturnNum(BigDecimal.valueOf(item.getNum()))
            );
            trade.getGifts().forEach(
                    item -> item.setCanReturnNum(BigDecimal.valueOf(item.getNum()))
            );
        }

        //过滤可退商品为0的商品
        List<TradeItem> tradeItemList = trade.getTradeItems().stream().filter(t -> BigDecimal.ZERO.compareTo(t.getCanReturnNum()) == NumberUtils.INTEGER_MINUS_ONE).collect(Collectors.toList());
        trade.setTradeItems(tradeItemList);

        //获取订单商品价格信息与退货信息
        BaseResponse<InventoryDetailSamountTradeResponse> inventoryByOId = inventoryDetailSamountTradeProvider.getInventoryAdaptive(InventoryDetailSamountTradeRequest.builder().oid(trade.getId()).build());
        if (inventoryByOId.getContext() == null || CollectionUtils.isEmpty(inventoryByOId.getContext().getInventoryDetailSamountTradeVOS())) {
            throw new SbcRuntimeException("获取可退商品错误!");
        }
        for (TradeItem tradeItem : tradeItemList) {
            List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS = inventoryByOId.getContext().getInventoryDetailSamountTradeVOS();
            List<InventoryDetailSamountTradeVO> collect = inventoryDetailSamountTradeVOS
                    .stream().filter(inventoryDetailSamountTradeVO -> StringUtils.equals(inventoryDetailSamountTradeVO.getGoodsInfoId(), tradeItem.getSkuId())).collect(Collectors.toList());
            tradeItem.setInventoryDetailSamountTrades(KsBeanUtil.convert(collect, InventoryDetailSamountTrade.class));
        }

        List<ReturnOrder> returnsNotVoid = findReturnsNotVoid(trade.getId());
        // 已退积分
        Long retiredPoints = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPoints()) && Objects.nonNull(o.getReturnPoints().getActualPoints()))
                .map(o -> o.getReturnPoints().getActualPoints())
                .reduce((long) 0, Long::sum);
        // 可退积分
        Long points = trade.getTradePrice().getPoints() == null ? 0 : trade.getTradePrice().getPoints();
        trade.setCanReturnPoints(points - retiredPoints);

        // 可退金额
        BigDecimal totalPrice =  trade.getTradePrice().getTotalPrice();
        //发货运费及包装费不退 2022 08 23
        if (deliverStatus != DeliverStatus.NOT_YET_SHIPPED) {
            TradePrice tradePrice = trade.getTradePrice();
            totalPrice = totalPrice
                    .subtract(Objects.nonNull(tradePrice.getDeliveryPrice()) ? tradePrice.getDeliveryPrice() : BigDecimal.ZERO)
                    .subtract(Objects.nonNull(tradePrice.getPackingPrice()) ? tradePrice.getPackingPrice() : BigDecimal.ZERO);
        }
        //余额使用金额
        BigDecimal sumReduceWalletPrice =  trade.getTradeItems()
                .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal retiredPrice = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
                .map(o -> o.getReturnPrice().getActualReturnPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
        trade.setBalanceReturnPrice(sumReduceWalletPrice);
        trade.setCanReturnPrice(canReturnPrice);
        trade.getTradePrice().setTotalPrice(canReturnPrice);

        trade.setCanReturnPrice(canReturnPrice);
    }


    /**
     * 查询订单详情,如已发货则带出可退商品数
     *
     * @param tid
     * @return
     */
    public Trade queryWmsCanReturnItemNumByTid(String tid) {
        Trade trade = tradeService.detail(tid);
        //校验支付状态
        PayOrderResponse payOrder = payOrderService.findPayOrder(trade.getId());
        if (payOrder.getPayOrderStatus() != PayOrderStatus.PAYED) {
            throw new SbcRuntimeException("K-050105");
        }
        if (trade.getTradeState().getFlowState() == FlowState.GROUPON) {
            throw new SbcRuntimeException("K-050141");
        }


        List<ReturnOrder> returnsNotVoid = findReturnsNotVoid(tid);
        // 可退金额
        BigDecimal totalPrice = trade.getTradeItems().stream()
                .map(TradeItem::getSplitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal retiredPrice = returnsNotVoid.stream()
                .filter(o -> Objects.nonNull(o.getReturnPrice().getActualReturnPrice()))
                .map(o -> o.getReturnPrice().getActualReturnPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal canReturnPrice = totalPrice.subtract(retiredPrice);
        trade.setCanReturnPrice(canReturnPrice);
        return trade;
    }


    /**
     * 查询退单详情,如已发货则带出可退商品数
     *
     * @param rid
     * @return
     */
    public ReturnOrder queryCanReturnItemNumById(String rid) {
        ReturnOrder returnOrder = findById(rid);
        Trade trade = tradeService.detail(returnOrder.getTid());
        if (trade.getTradeState().getDeliverStatus() != DeliverStatus.NOT_YET_SHIPPED && trade.getTradeState()
                .getDeliverStatus() != DeliverStatus.VOID) {
            //计算商品可退数
            Map<String, BigDecimal> map = findLeftItems(trade);
            ReturnItem returnItem = returnOrder.getReturnItems().stream().findFirst().orElse(null);
            if (Objects.nonNull(returnItem.getDevanningId())) {
                Map<Long, BigDecimal> leftDevanningItemsMap = findLeftDevanningItems(trade);
                returnOrder.getReturnItems().forEach(item -> item.setCanReturnNum(leftDevanningItemsMap.get(item.getDevanningId())));
            } else {
                returnOrder.getReturnItems().forEach(item -> item.setCanReturnNum(map.get(item.getSkuId())));
            }
        }
        return returnOrder;
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
        ReturnOrder returnOrder = returnOrderRepository.findById(rid).orElse(new ReturnOrder());
        ReturnEvent event = returnOrder.getReturnType() == ReturnType.RETURN ? ReturnEvent.REVERSE_RETURN :
                ReturnEvent.REVERSE_REFUND;
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(rid)
                .operator(operator)
                .returnEvent(event)
                .build();
        returnFSMService.changeState(request);
    }

    /**
     * 修改退单
     *
     * @param newReturnOrder
     * @param operator
     */
    @LcnTransaction
    @Transactional
    public void remedy(ReturnOrder newReturnOrder, Operator operator) {
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(newReturnOrder.getId())
                .operator(operator)
                .returnEvent(ReturnEvent.REMEDY)
                .data(newReturnOrder)
                .build();
        returnFSMService.changeState(request);
    }

    /**
     * 订单中可退货的数量
     *
     * @param trade
     */
    private Map<String, BigDecimal> findLeftItems(Trade trade) {
        if (!trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !trade.getTradeState()
                .getFlowState().equals(FlowState.COMPLETED)) {
            throw new SbcRuntimeException("K-050002");
        }

        Map<String, BigDecimal> map = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                g -> BigDecimal.valueOf(g.getDeliveredNum()), (o, n) -> o.add(n)));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream().filter(returnOrder -> Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())

                .map(ReturnOrder::getReturnItems)
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
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }
                            return total.subtract(BigDecimal.valueOf(returned));
                        },
                        (o, n) -> o.add(n)
                ));
    }

    /**
     * 拆箱订单中可退货的数量
     *
     * @param trade
     */
    private Map<Long, BigDecimal> findLeftDevanningItems(Trade trade) {
        /*if (!trade.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED) && !trade.getTradeState()
                .getFlowState().equals(FlowState.COMPLETED)) {
            throw new SbcRuntimeException("K-050002");
        }*/

        Map<Long, BigDecimal> map = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItem::getDevanningId,
                g -> BigDecimal.valueOf(g.getDeliveredNum())));

        List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream().filter(returnOrder -> Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())
                .map(ReturnOrder::getReturnItems)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
        Map<Long, List<ReturnItem>> groupMap = IteratorUtils.groupBy(allReturnItems, ReturnItem::getDevanningId);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Long key = entry.getKey();
                            BigDecimal total = map.get(key);
                            Double returned = 0d;
                            if (groupMap.get(key) != null) {
                                returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                            }
                            return total.subtract(BigDecimal.valueOf(returned));
                        },
                        (o, n) -> o.add(n)
                ));
    }

    /**
     * 订单中可退赠品的数量
     *
     * @param trade
     */
    private Map<String, BigDecimal> findLeftGiftItems(Trade trade) {
        if (CollectionUtils.isNotEmpty(trade.getGifts())) {
            Map<String, BigDecimal> map = trade.getGifts().stream().collect(Collectors.toMap(TradeItem::getSkuId,
                    g -> BigDecimal.valueOf(g.getDeliveredNum())));
            List<ReturnItem> allReturnItems = this.findReturnsNotVoid(trade.getId()).stream()
                    .map(ReturnOrder::getReturnGifts)
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
                                    returned = groupMap.get(key).stream().mapToDouble(g -> g.getNum().doubleValue()).sum();
                                }
                                return total.subtract(BigDecimal.valueOf(returned));
                            }
                    ));
        }
        return new HashMap<>();
    }


    private void verifyNum(Trade trade, List<ReturnItem> returnItems) {
        Map<String, BigDecimal> map = this.findLeftItems(trade);
        Map<Long, BigDecimal> leftDevanningItemsMap = Maps.newHashMap();
        //拆箱处理
        ReturnItem returnItem = returnItems.stream().findFirst().orElse(null);
        if (Objects.nonNull(returnItem.getDevanningId())) {
            leftDevanningItemsMap = this.findLeftDevanningItems(trade);
        }
        Map<Long, BigDecimal> finalLeftDevanningItemsMap = leftDevanningItemsMap;
        returnItems.stream().forEach(
                t -> {
                    if (Objects.nonNull(t.getDevanningId())) {
                        if (finalLeftDevanningItemsMap.get(t.getDevanningId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                        }
                    } else {
                        if (map.get(t.getSkuId()).subtract(t.getNum()).compareTo(BigDecimal.ZERO) < 0) {
                            throw new SbcRuntimeException("K-050001", new Object[]{t.getSkuId()});
                        }
                    }
                    if (t.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new SbcRuntimeException("K-050102");
                    }

                }
        );
    }

    /**
     * 查询退单列表，不包含已作废状态以及拒绝收货的退货单与拒绝退款的退款单
     *
     * @return
     */
    public List<ReturnOrder> findReturnsNotVoid(String tid) {
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(tid);
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
    public List<ReturnOrder> filterFinishedReturnOrder(List<ReturnOrder> returnOrders) {
        return returnOrders.stream().filter(t -> !t.getReturnFlowState().equals(ReturnFlowState.VOID)
                && !t.getReturnFlowState().equals(ReturnFlowState.REJECT_RECEIVE) &&
                !(t.getReturnType() == ReturnType.REFUND && t.getReturnFlowState() == ReturnFlowState.REJECT_REFUND))
                .collect(Collectors.toList());
    }

    /**
     * 根据订单id查询所有退单
     *
     * @param tid
     * @return
     */
    public List<ReturnOrder> findReturnByTid(String tid) {
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(tid);
        return returnOrders == null ? Collections.emptyList() : returnOrders;
    }

    /**
     * 分页
     *
     * @param endDate
     * @return
     */
    public int countReturnOrderByEndDate(LocalDateTime endDate, ReturnFlowState returnFlowState) {
        Query query = getReturnOrderQuery(endDate, returnFlowState);
        return mongoTemplate.find(query, ReturnOrder.class).size();
    }

    /**
     * 分页查询退单
     *
     * @param endDate
     * @param start
     * @param end
     * @return
     */
    public List<ReturnOrder> queryReturnOrderByEndDate(LocalDateTime endDate, int start, int end, ReturnFlowState
            returnFlowState) {
        Query query = getReturnOrderQuery(endDate, returnFlowState);
        return mongoTemplate.find(query, ReturnOrder.class).subList(start, end);
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
     * 分页查询订单
     *
     * @param endDate         endDate
     * @param returnFlowState returnFlowState
     * @param pageable        pageable
     * @return List<Trade>
     */
//    public List<ReturnOrder> queryReturnOrderByPage(LocalDateTime endDate, ReturnFlowState returnFlowState, Pageable
//            pageable) {
//        val pageSize = 1000;
//        //超过
//        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
//        ExistsQueryBuilder filter = QueryBuilders.existsQuery("returnLogistics.createTime");
//        QueryBuilder queryBuilder;
//        //已发货的查询条件
//        if (ReturnFlowState.DELIVERED.equals(returnFlowState)) {
//            queryBuilder = QueryBuilders.boolQuery()
//                    .mustNot(QueryBuilders.boolQuery().filter(filter))
//                    .must(QueryBuilders.matchQuery("returnFlowState", returnFlowState.toValue()))
//                    .must(QueryBuilders.rangeQuery("createTime")
//                            .to(DateUtil.format(endDate, DateUtil.FMT_TIME_4)))
//                    .should(QueryBuilders.boolQuery()
//                            .must(QueryBuilders.matchQuery("returnFlowState", returnFlowState.toValue()))
//                            .must(QueryBuilders.rangeQuery("returnLogistics.createTime")
//                                    .to(DateUtil.format(endDate, DateUtil.FMT_TIME_4))));
//            //客户端带客退单的初始化状态
//        } else {
//            queryBuilder = QueryBuilders.boolQuery()
//                    .must(QueryBuilders.matchQuery("returnFlowState", returnFlowState.toValue()))
//                    .must(QueryBuilders.rangeQuery("createTime")
//                            .to(DateUtil.format(endDate, DateUtil.FMT_TIME_4)));
//        }
//
//
//        builder
//                .withIndices(EsConstants.RETURN_ORDER_INDEX)
//                .withTypes(EsConstants.RETURN_ORDER_TYPE)
//                .withQuery(
//                        queryBuilder
//                ).withPageable(new PageRequest(0, pageSize));
//
//        FacetedPage<ReturnOrder> facetedPage = template.queryForPage(builder.build(), ReturnOrder.class);
//        return facetedPage.getContent();
//    }


    /**
     * 根据退单状态统计退单
     *
     * @param
     * @return
     */
//    public ReturnOrderTodoReponse countReturnOrderByFlowState(ReturnQueryRequest request) {
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        nativeSearchQueryBuilder.withQuery(request.buildEs());
//        nativeSearchQueryBuilder.withIndices("b2b_return_order");
//        nativeSearchQueryBuilder.withTypes("return_order");
//      //  nativeSearchQueryBuilder.withSearchType(SearchType.COUNT);
//        AbstractAggregationBuilder abstractAggregationBuilder = AggregationBuilders.terms("returnType").field
//                ("returnFlowState").size(0);
//        nativeSearchQueryBuilder.addAggregation(abstractAggregationBuilder);
//        return this.template.query(nativeSearchQueryBuilder.build(), ReturnOrderTodoReponse::build);
//    }
    private void autoDeliver(String rid, Operator operator) {
        ReturnStateRequest request;
        ReturnOrder returnOrder = findById(rid);

        //非快递退回的退货单，审核通过后变更为已发货状态
        if (returnOrder.getReturnType() == ReturnType.RETURN && returnOrder.getReturnWay() == ReturnWay.OTHER) {
            request = ReturnStateRequest
                    .builder()
                    .rid(rid)
                    .operator(operator)
                    .returnEvent(ReturnEvent.DELIVER)
                    .build();
            returnFSMService.changeState(request);
        }

    }

    /**
     * 释放提货订单的商品数量
     *
     * @param trade
     */
    private void freePileGoodsNum(Trade trade) {
        //查询关联表中的数据
        List<String> skuIds = trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordTidAndSkuid(trade.getId(), skuIds);
        //查询囤货记录表
        List<Long> stockRecordId = stockRecordAttachmentList.stream().map(PileStockRecordAttachment::getStockRecordId).collect(Collectors.toList());
        List<PileStockRecord> stockRecordList = pileStockRecordRepository.findAllById(stockRecordId);
        List<PileStockRecord> pileStockRecordList = new ArrayList<>();
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
                PilePurchase purchase = pilePurchases.stream().findFirst().get();
                purchase.setGoodsNum(purchase.getGoodsNum() + num);
                //加完以后
                pilePurchaseRepository.save(purchase);
                if (stockRecordAttachmentList.size() == 0) {
                    //加入囤货流水
                    BigDecimal realPay = tradeItem.getSplitPrice().divide(new BigDecimal(tradeItem.getNum()), 2, BigDecimal.ROUND_HALF_UP);
                    PileStockRecord pileStockRecord = PileStockRecord.builder()
                            .goodsId("T")
                            .goodsInfoId(skuId)
                            .customerId(customerId)
                            .stockRecordNum(tradeItem.getNum())
                            .stockRecordRemainingNum(0L)
                            .stockRecordPrice(realPay)
                            .orderCode(trade.getId())
                            .isUse(0L)
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now())
                            .build();
                    pileStockRecordList.add(pileStockRecord);
                }
            }
        });
        if (stockRecordAttachmentList.size() > 0) {
            stockRecordAttachmentList.stream().forEach(pileStockRecordAttachment -> {
                stockRecordList.stream().forEach(pileStockRecord -> {
                    if (pileStockRecordAttachment.getStockRecordId().equals(pileStockRecord.getStockRecordId()) && pileStockRecordAttachment.getTid().equals(trade.getId())) {
                        Long num = pileStockRecord.getStockRecordRemainingNum();
                        Long attachment = pileStockRecordAttachment.getNum();
                        pileStockRecord.setStockRecordRemainingNum(num - attachment);
                        if (pileStockRecord.getIsUse() == 1 && (pileStockRecord.getStockRecordNum() - pileStockRecord.getStockRecordRemainingNum()) > 0) {
                            pileStockRecord.setIsUse(0L);
                        }
                        pileStockRecord.setUpdateTime(LocalDateTime.now());
                    }
                });
            });
            if (stockRecordAttachmentList.size() == 0) {
                pileStockRecordRepository.saveAll(pileStockRecordList);
            } else {
                pileStockRecordRepository.saveAll(stockRecordList);
            }
        }
        log.info("ReturnOrderService.freePileGoodsNum stockRecordList:{}", JSONObject.toJSONString(stockRecordList));
    }

    /**
     * 查询退货需要的关联数据
     *
     * @param returnOrder
     * @return
     */
    public List<PileStockRecordAttachment> getReturnGoodsLinkedData(ReturnOrder returnOrder) {
        //查询关联表中的数据
        List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<String> gitftskuIds = returnOrder.getReturnGifts().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        Iterator it = gitftskuIds.iterator();
        while (it.hasNext()) {
            skuIds.add((String) it.next());
        }

        List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordTidAndSkuid(returnOrder.getTid(), skuIds);
        return stockRecordAttachmentList;
    }

    /**
     * 查询退货需要的关联数据
     *
     * @param returnOrder
     * @return
     */
    public List<PileStockRecordAttachment> getReturnGoodsLinkedData(NewPileReturnOrder returnOrder) {
        //查询关联表中的数据
        List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<String> gitftskuIds = returnOrder.getReturnGifts().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        Iterator it = gitftskuIds.iterator();
        while (it.hasNext()) {
            skuIds.add((String) it.next());
        }

        List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordTidAndSkuid(returnOrder.getTid(), skuIds);
        return stockRecordAttachmentList;
    }

    /**
     * 根据退单id释放提货订单商品数量(wms缺货)
     *
     * @param rid
     */
    public void freePileGoodsNumByRid(String rid) {
        ReturnOrder returnOrder = findById(rid);
        //查询关联表中的数据
        List<String> skuIds = returnOrder.getReturnItems().stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordTidAndSkuid(returnOrder.getTid(), skuIds);
        log.info("ReturnOrderService.freePileGoodsNumByRid stockRecordAttachmentList:{}", JSONObject.toJSONString(stockRecordAttachmentList));
        //查询囤货记录表
        List<Long> stockRecordId = stockRecordAttachmentList.stream().map(PileStockRecordAttachment::getStockRecordId).collect(Collectors.toList());
        List<PileStockRecord> stockRecordList = pileStockRecordRepository.findAllById(stockRecordId);
        String customerId = returnOrder.getBuyer().getId();
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        List<PileStockRecord> pileStockRecordList = new ArrayList<>();
        Map<String, Long> goodsUseNum = new HashMap<>();
        returnItems.stream().forEach(returnItem -> {
            Long num = 0L;
            String skuId = returnItem.getSkuId();
            if (Objects.isNull(returnItem.getCanReturnNum())) {
                num = returnItem.getNum().longValue();
            } else {
                num = returnItem.getCanReturnNum().longValue();
            }
            Long useNum = 0L;
            if (goodsUseNum.get(returnItem.getSkuId()) != null) {
                useNum = goodsUseNum.get(returnItem.getSkuId());
            }
            List<PilePurchase> pilePurchase = pilePurchaseRepository.getPilePurchase(customerId, skuId);
            if (CollectionUtils.isNotEmpty(pilePurchase)) {
                PilePurchase purchase = pilePurchase.stream().findFirst().get();
                purchase.setGoodsNum(purchase.getGoodsNum() + num);
                pilePurchaseRepository.save(purchase);
                if (stockRecordAttachmentList.size() == 0) {
                    //加入囤货流水
                    BigDecimal realPay = returnItem.getSplitPrice().divide(returnItem.getNum(), 2, BigDecimal.ROUND_HALF_UP);
                    PileStockRecord pileStockRecord = PileStockRecord.builder()
                            .goodsId("T")
                            .goodsInfoId(skuId)
                            .customerId(customerId)
                            .stockRecordNum(returnItem.getNum().longValue())
                            .stockRecordRemainingNum(0L)
                            .stockRecordPrice(realPay)
                            .orderCode(rid)
                            .isUse(0L)
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now())
                            .build();
                    pileStockRecordList.add(pileStockRecord);

                }
            }

            if (stockRecordAttachmentList.size() > 0) {
                if (useNum < num) {
                    stockRecordAttachmentList.stream().forEach(pileStockRecordAttachment -> {
                        stockRecordList.stream().forEach(pileStockRecord -> {
                            //退货数量
                            Long nums = 0L;
                            if (Objects.isNull(returnItem.getCanReturnNum())) {
                                nums = returnItem.getNum().longValue();
                            } else {
                                nums = returnItem.getCanReturnNum().longValue();
                            }
                            Long useNums = 0L;
                            if (goodsUseNum.get(returnItem.getSkuId()) != null) {
                                useNums = goodsUseNum.get(returnItem.getSkuId());
                            }
                            if (pileStockRecordAttachment.getStockRecordId().equals(pileStockRecord.getStockRecordId()) &&
                                    pileStockRecordAttachment.getSkuId().equals(returnItem.getSkuId()) && pileStockRecordAttachment.getTid().equals(returnOrder.getTid())) {
                                //囤货已使用数量
                                Long stockRecordNum = pileStockRecord.getStockRecordRemainingNum();
                                //关联表提货数据
                                Long attachmentNum = pileStockRecordAttachment.getNum();
                                goodsUseNum.put(returnItem.getSkuId(), useNums + attachmentNum);
                                if (useNums < nums) {
                                    if ((attachmentNum + useNums) > nums) {
                                        pileStockRecord.setStockRecordRemainingNum(stockRecordNum - (nums - useNums));
                                    } else {
                                        pileStockRecord.setStockRecordRemainingNum(stockRecordNum - attachmentNum);
                                    }
                                    if (pileStockRecord.getIsUse() == 1 && (pileStockRecord.getStockRecordNum() - pileStockRecord.getStockRecordRemainingNum()) > 0) {
                                        pileStockRecord.setIsUse(0L);
                                    }
                                }
                            }
                        });
                    });
                }
            }
        });
        log.info("ReturnOrderService.freePileGoodsNumByRid stockRecordList:{}", JSONObject.toJSONString(stockRecordList));

        if (stockRecordAttachmentList.size() == 0) {
            pileStockRecordRepository.saveAll(pileStockRecordList);
        } else {
            pileStockRecordRepository.saveAll(stockRecordList);
        }

    }

    /**
     * 释放订单商品库存
     *
     * @param returnOrder
     */
    private void freeStock(ReturnOrder returnOrder, Trade trade) {
//        returnOrder.getReturnItems().stream().forEach(returnItem -> goodsInfoService.addStockById(returnItem.getNum()
//                .longValue(), returnItem.getSkuId()));
//        // 若存在赠品,赠品库存也释放
//        if (CollectionUtils.isNotEmpty(trade.getGifts())) {
//            trade.getGifts().stream().forEach(gift -> goodsInfoService.addStockById(gift.getNum(), gift.getSkuId()));
//        }
        //批量库存释放
        Map<String, BigDecimal> mapStock = getStock(returnOrder.getReturnItems(), trade.getTradeItems());

        log.info("mapStock=========== {}", JSONObject.toJSONString(mapStock));

        List<GoodsInfoPlusStockDTO> stockList = new ArrayList<>();

        List<String> skuIdList = returnOrder.getReturnItems().stream().map(t -> t.getSkuId()).distinct().collect(Collectors.toList());

        //非整箱退单
        if(!SaleType.WHOLESALE.equals(trade.getSaleType())) {
            verifyService.buildAddItems(trade,returnOrder.getWareId(),returnOrder.getSaleType(),trade.getTradeItems(),stockList);
        }else {
            skuIdList.forEach(id -> {
                GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
                dto.setStock(mapStock.get(id));
                dto.setGoodsInfoId(id);
                dto.setWareId(returnOrder.getWareId());
                stockList.add(dto);
            });
        }
//        returnOrder.getReturnItems().forEach(var->{
//            GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
//            GoodsInfoPlusStockDTO goodsInfoPlusStockDTO = stockList.stream().filter(s -> s.getGoodsInfoId().equals(var.getSkuId())).findAny().orElse(null);
//            if(Objects.isNull(goodsInfoPlusStockDTO)){
//                dto.setStock(mapStock.get(var.getSkuId()));
//                dto.setGoodsInfoId(var.getSkuId());
//                dto.setWareId(returnOrder.getWareId());
//                stockList.add(dto);
//            }
//        });

        if (CollectionUtils.isNotEmpty(trade.getGifts())) {
            trade.getGifts().forEach(gift -> {
                GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
                dto.setStock(BigDecimal.valueOf(gift.getNum()));
                dto.setGoodsInfoId(gift.getSkuId());
                dto.setWareId(trade.getWareId());
                stockList.add(dto);
            });
        }

        if (CollectionUtils.isNotEmpty(stockList)) {
            if (!trade.getVillageFlag()) {
                log.info("恢复库存-----------stockList>" + JSONObject.toJSONString(stockList));
                //恢复预售商品库存 --@jkp
                if (trade.getPresellFlag()){
                    List<GoodsInfoPresellRecordDTO> recordDTOList = new ArrayList<>();
                    stockList.forEach(item->{
                        GoodsInfoPresellRecordDTO recordDTO = new GoodsInfoPresellRecordDTO();
                        recordDTO.setGoodsInfoId(item.getGoodsInfoId());
                        recordDTO.setPresellCount(item.getStock().longValue());
                        recordDTO.setType(1);
                        recordDTO.setTradeId(trade.getId());
                        recordDTO.setWareId(trade.getWareId());
                        recordDTOList.add(recordDTO);
                    });
                    goodsInfoProvider.updatePresellGoodsInfoStock(recordDTOList);
                } else {
                    goodsInfoProvider.batchPlusStock(GoodsInfoBatchPlusStockRequest.builder().stockList(stockList).wareId(returnOrder.getWareId()).build());
                }
            }
        }
    }

    //实际恢复库存数量
    private Map<String, BigDecimal> getStock(List<ReturnItem> returnItems, List<TradeItem> tradeItems) {
        Map<String, BigDecimal> result = new HashMap();
        Map<Long, TradeItem> tradeItemMap = Maps.newHashMap();
        Map<String, List<ReturnItem>> returnItemMap = returnItems.stream().collect(Collectors.groupingBy(ReturnItem::getSkuId));
        TradeItem tradeItem = tradeItems.stream().findFirst().orElse(null);
        if (Objects.nonNull(tradeItem.getDevanningId())) {
            tradeItemMap = tradeItems.stream().collect(Collectors.toMap(TradeItem::getDevanningId, Function.identity()));
            Map<Long, TradeItem> finalTradeItemMap = tradeItemMap;
            returnItemMap.forEach((t, data) -> {
                AtomicReference<BigDecimal> refundCount = new AtomicReference<>(BigDecimal.ZERO);
                data.forEach(d -> {
                    TradeItem item = finalTradeItemMap.get(d.getDevanningId());
                    refundCount.set(refundCount.get().add(d.getNum().multiply(item.getDivisorFlag())));
                });
                result.put(t, refundCount.get());
            });
            return result;
        } else {
            Map<String, List<ReturnItem>> returnMap = returnItems.stream().collect(Collectors.groupingBy(ReturnItem::getSkuId));
            returnMap.forEach((k, v) -> {
                result.put(k, v.stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add));
            });
            return result;
        }
    }

    public boolean isReturnFull(ReturnOrder returnOrder) {
        List<ReturnOrder> returnOrders = returnOrderRepository.findByTid(returnOrder.getTid());
        returnOrders = returnOrders.stream().filter(item -> item.getReturnType() == ReturnType.RETURN).collect
                (Collectors.toList());

        List<ReturnItem> returnItems = returnOrders.stream().filter(item -> item.getReturnFlowState() ==
                ReturnFlowState.COMPLETED
                || item.getReturnFlowState() == ReturnFlowState.RECEIVED
                || item.getReturnFlowState() == ReturnFlowState.REJECT_REFUND).flatMap(item -> item.getReturnItems()
                .stream())
                .collect(Collectors.toList());
        //拆箱
        ReturnItem returnDevanItem = returnOrder.getReturnItems().stream().findFirst().orElse(null);
        if (Objects.nonNull(returnDevanItem.getDevanningId())) {
            Map<Long, Long> tradeNumMap = tradeService.detail(returnOrder.getTid()).getTradeItems().stream().collect(
                    Collectors.toMap(TradeItem::getDevanningId, TradeItem::getDeliveredNum));

            Map<Long, BigDecimal> returnNumMap = IteratorUtils.groupBy(returnItems, ReturnItem::getDevanningId).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> entry.getValue().stream().map(ReturnItem::getNum).reduce(BigDecimal.ZERO, BigDecimal::add)
                    ));
            Optional optional = tradeNumMap.entrySet().stream().filter(entry -> {
                BigDecimal num = returnNumMap.get(entry.getKey());
                return num == null || num.compareTo(BigDecimal.valueOf(entry.getValue())) != 0;
            }).findFirst();
            return !optional.isPresent();
        } else {
            Map<String, Long> tradeNumMap = tradeService.detail(returnOrder.getTid()).getTradeItems().stream().collect(
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
    }

    private void verifyPrice(Map<String, TradeItem> skuItemMap, List<ReturnItem> returnItems) {
        returnItems.forEach(r -> {
            TradeItem item = skuItemMap.get(r.getSkuId());
            if (r.getPrice().compareTo(item.getPrice()) != 0) {
                throw new SbcRuntimeException("K-050207");
            }
        });
    }

    /**
     * 分页查询账期内退单信息
     *
     * @param storeId
     * @param startTime
     * @param endTime
     * @param pageRequest
     * @return
     */
    public Page<ReturnOrder> getReturnListByPage(Long storeId, Date startTime, Date endTime, PageRequest pageRequest) {
        return returnOrderRepository.findByCompany_StoreIdAndFinishTimeBetweenAndReturnFlowState(storeId, startTime,
                endTime, pageRequest, ReturnFlowState.COMPLETED);
    }

    /**
     * 更新退单的结算状态
     *
     * @param storeId
     * @param startDate
     * @param endDate
     */
    public void updateSettlementStatus(Long storeId, Date startDate, Date endDate) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("company.storeId").is(storeId)
                , Criteria.where("returnFlowState").is(ReturnFlowState.COMPLETED)
                , Criteria.where("finishTime").lt(endDate).gt(startDate));
        mongoTemplate.updateMulti(new Query(criteria), new Update().set("hasBeanSettled", true), ReturnOrder.class);
    }


    /**
     * 结算解析获取原始数据，获取退单集合
     *
     * @param storeId
     * @param startDate
     * @param endDate
     */
    public List<ReturnOrder> findReturnOrderListForSettlement(Long storeId, Date startDate, Date endDate, Pageable
            pageRequest) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("company.storeId").is(storeId)
                , Criteria.where("returnFlowState").is(ReturnFlowState.COMPLETED)
                , Criteria.where("finishTime").lt(endDate).gte(startDate)
                , Criteria.where("hasBeanSettled").ne(true)
        );
        return mongoTemplate.find(new Query(criteria).skip(pageRequest.getPageNumber() * pageRequest.getPageSize())
                .limit(pageRequest.getPageSize()), ReturnOrder.class);
    }

    /**
     * 更新退单的业务员
     *
     * @param employeeId 业务员
     * @param customerId 客户
     */
    public void updateEmployeeId(String employeeId, String customerId) {
        mongoTemplate.updateMulti(new Query(Criteria.where("buyer.id").is(customerId)), new Update().set("buyer" +
                ".employeeId", employeeId), ReturnOrder.class);
    }

    /**
     * @description  修改退货订单流水凭证
     * @author  shiy
     * @date    2023/3/14 9:32
     * @params  [java.lang.String, java.lang.String]
     * @return  void
    */
    public void updateVoucherImagesFlagById(String id, String refundVoucherImagesFlag) {
        mongoTemplate.updateFirst(new Query(Criteria.where("id").is(id)), new Update().set("refundVoucherImagesFlag", refundVoucherImagesFlag), ReturnOrder.class);
    }

    /**
     * 完善没有业务员的退单
     */
    public void fillEmployeeId() {
        List<ReturnOrder> trades = mongoTemplate.find(new Query(Criteria.where("buyer.employeeId").is(null)),
                ReturnOrder.class);
        if (CollectionUtils.isEmpty(trades)) {
            return;
        }
        List<String> buyerIds = trades.stream()
                .filter(t -> Objects.nonNull(t.getBuyer()) && StringUtils.isNotBlank(t.getBuyer().getId()))
                .map(ReturnOrder::getBuyer)
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
     * 根据条件统计
     *
     * @param queryBuilder
     * @return
     */
    private long countNum(Criteria queryBuilder) {
        Query query = new Query(queryBuilder);
        long total = mongoTemplate.count(query, ReturnOrder.class);
        return total;
    }

    /**
     * 退款失败
     *
     * @param refundOrderRefundRequest
     */
    @Transactional
    @LcnTransaction
    public void refundFailed(RefundOrderRefundRequest refundOrderRefundRequest) {
        ReturnOrder returnOrder = returnOrderRepository.findById(refundOrderRefundRequest.getRid()).orElse(null);
        if (Objects.isNull(returnOrder)) {
            logger.error("退单ID:{},查询不到退单信息", refundOrderRefundRequest.getRid());
            return;
        }
        ReturnFlowState flowState = returnOrder.getReturnFlowState();
        // 如果已是退款状态的订单，直接return，不做状态扭转处理
        if (flowState == ReturnFlowState.REFUND_FAILED) {
            return;
        }
        returnOrder.setRefundFailedReason(refundOrderRefundRequest.getFailedReason());
        returnOrderService.updateReturnOrder(returnOrder);
        //修改退单状态
        ReturnStateRequest request = ReturnStateRequest
                .builder()
                .rid(refundOrderRefundRequest.getRid())
                .operator(refundOrderRefundRequest.getOperator())
                .returnEvent(ReturnEvent.REFUND_FAILED)
                .build();
        returnFSMService.changeState(request);
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
        ReturnOrder returnOrder = findById(rid);
        TradeStatus tradeStatus = payQueryProvider.getRefundResponseByOrdercode(new RefundResultByOrdercodeRequest
                (returnOrder.getTid(), returnOrder.getId())).getContext().getTradeStatus();
        if (tradeStatus != null) {
            if (tradeStatus == TradeStatus.SUCCEED) {
                throw new SbcRuntimeException("K-100104");
            } else if (tradeStatus == TradeStatus.PROCESSING) {
                throw new SbcRuntimeException("K-100105");
            }
        }

        if (!Objects.equals(ReturnFlowState.REFUND_FAILED, returnOrder.getReturnFlowState())) {
            throw new SbcRuntimeException("K-100104");
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
        returnFSMService.changeState(request);

        /*Trade trade = tradeService.detail(returnOrder.getTid());
        trade.setRefundFlag(true);
        tradeService.updateTrade(trade);*/
        //释放囤货数量
        /*if (TradeActivityTypeEnum.STOCKUP.toActivityType().equals(trade.getActivityType())) {
            freePileGoodsNum(trade);
        }*/
        // 作废订单也需要释放库存
        // freeStock(returnOrder, trade);
        //作废订单
        // tradeService.voidTrade(returnOrder.getTid(), operator);
        // trade.getTradeState().setEndTime(LocalDateTime.now());
        //
        Trade trade = tradeService.detail(returnOrder.getTid());
        trade.setRefundFlag(true);
        tradeService.updateTrade(trade);

        if (returnOrder.getReturnType() == ReturnType.REFUND && (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())) {
            //仅退款退单在退款完成后释放商品库存
            freeStock(returnOrder, trade);

            //更新分摊金额退款完成标记为已退
            if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
                inventoryDetailSamountService.returnAmountByRid(rid);
            }

            //作废订单（非囤货的提货退单走原逻辑）
            //来源于囤货的提货退单，全部退完时 作废订单
            if (!TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType()) || newPilePickTradeAllReturned(returnOrder)) {
                if (!Objects.equals(FlowState.VOID, trade.getTradeState().getFlowState())) {
                    tradeService.voidTrade(returnOrder.getTid(), operator);
                }
            }
            trade.getTradeState().setEndTime(LocalDateTime.now());
        }
    }

    /**
     * 返回掩码后的字符串
     *
     * @param bankNo
     * @return
     */
    public static String getDexAccount(String bankNo) {
        String middle = "****";
        if (bankNo.length() > 4) {
            if (bankNo.length() <= 8) {
                return middle;
            } else {
                // 如果是手机号
                if (bankNo.length() == 11) {
//                    bankNo = bankNo.substring(0, 3) + middle + bankNo.substring(bankNo.length() - 4);
                } else {
                    bankNo = bankNo.substring(0, 4) + middle + bankNo.substring(bankNo.length() - 4);
                }
            }
        } else {
            return middle;
        }
        return bankNo;
    }

    /**
     * 拼团退单相应处理
     *
     * @param returnOrder
     * @param trade
     */
    private void modifyGrouponInfo(final ReturnOrder returnOrder, final Trade trade) {
        List<String> tradeItemIds =
                trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        TradeGroupon tradeGroupon = trade.getTradeGroupon();
        //拼团订(退)单只可能有一个sku
        ReturnItem returnItem = returnOrder.getReturnItems().get(0);
        //step1. 修改订单拼团信息
        tradeGroupon.setReturnPrice(returnOrder.getReturnPrice().getActualReturnPrice());
        tradeGroupon.setReturnNum(returnItem.getNum().intValue());
        //step2. 修改拼团商品计数
        GrouponGoodsInfoReturnModifyRequest returnModifyRequest;
        if (RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND.equals(returnOrder.getDescription())) {
            returnModifyRequest = GrouponGoodsInfoReturnModifyRequest.builder()
                    .grouponActivityId(tradeGroupon.getGrouponActivityId())
                    .amount(returnOrder.getReturnPrice().getActualReturnPrice())
                    .goodsInfoId(returnItem.getSkuId())
                    .num(NumberUtils.INTEGER_ZERO)
                    .build();
        } else {
            returnModifyRequest = GrouponGoodsInfoReturnModifyRequest.builder()
                    .grouponActivityId(tradeGroupon.getGrouponActivityId())
                    .amount(returnOrder.getReturnPrice().getActualReturnPrice())
                    .goodsInfoId(returnItem.getSkuId())
                    .num(returnItem.getNum().intValue())
                    .build();
        }
        grouponGoodsInfoProvider.modifyReturnInfo(returnModifyRequest);
        //step3.修改拼团活动计数
        if (RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND.equals(returnOrder.getDescription()) ||
                RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND_USER.equals(returnOrder.getDescription())) {
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
        if (RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND.equals(returnOrder.getDescription()) ||
                RefundReasonConstants.Q_ORDER_SERVICE_GROUPON_AUTO_REFUND_USER.equals(returnOrder.getDescription())) {
            if (tradeGroupon.getLeader()) {
                GrouponInstance grouponInstance = grouponOrderService.getGrouponInstanceByActivityIdAndGroupon(tradeGroupon.getGrouponNo());
                grouponInstance.setGrouponStatus(GrouponOrderStatus.FAIL);
                grouponInstance.setFailTime(LocalDateTime.now());
                grouponOrderService.updateGrouponInstance(grouponInstance);
            }
        }
        //step4.减去拼团商品已购买数 todo :拼团的未做小数处理
        recordProvider.decrBuyNumByGrouponActivityIdAndCustomerIdAndGoodsInfoId(GrouponRecordDecrBuyNumRequest.builder()
                .customerId(returnOrder.getBuyer().getId())
                .grouponActivityId(tradeGroupon.getGrouponActivityId())
                .goodsInfoId(returnItem.getSkuId())
                .buyNum(returnItem.getNum().intValue())
                .build()
        );
    }

    /**
     * 根据退单ID在线退款
     *
     * @param returnOrderCode
     * @param operator
     * @return
     */

    public List<Object> refundOnlineByTid(String returnOrderCode, Operator operator, Boolean wmsStats) {

        //查询退单
        ReturnOrder returnOrder = findById(returnOrderCode);

        //退款退单状态需要是已审核
        if (returnOrder != null && returnOrder.getReturnType() == ReturnType.REFUND) {
            if (returnOrder.getReturnFlowState() != ReturnFlowState.AUDIT &&
                    returnOrder.getReturnFlowState() != ReturnFlowState.REFUND_FAILED) {
                throw new SbcRuntimeException("K-050004");
            }
        }
        //退货退单状态需要是已收到退货/退款失败
        if (returnOrder != null && returnOrder.getReturnType() == ReturnType.RETURN) {
            if (returnOrder.getReturnFlowState() != ReturnFlowState.RECEIVED &&
                    returnOrder.getReturnFlowState() != ReturnFlowState.REFUND_FAILED) {
                throw new SbcRuntimeException("K-050004");
            }
        }
        // 查询退款单
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrderCode);
        Trade trade = tradeService.detail(returnOrder.getTid());
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }

        if (Objects.isNull(operator) || StringUtils.isBlank(operator.getUserId())) {
            operator = Operator.builder().ip("127.0.0.1").adminId("1").name("system").platform(Platform
                    .BOSS).build();
        }

        //添加wms货物不足退款标识
        if (Objects.nonNull(wmsStats)) {
            returnOrder.setWmsStats(wmsStats);
        }

        BigDecimal returnAmount = returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice()
                .getApplyPrice() : refundOrder.getReturnPrice();
        log.info("ReturnOrderService.refundOnlineByTid rid:{} returnAmount:{}", returnOrder.getId(), returnAmount);
        return refundOrderService.autoRefund(Collections.singletonList(trade), Collections.singletonList(returnOrder), Collections.singletonList(refundOrder), operator);
    }



    /**
     * 根据退单ID在线退款
     *
     * @param returnOrderCode
     * @param operator
     * @return
     */

    public List<Object> refundOnlineByTidNewPile(String returnOrderCode, Operator operator, Boolean wmsStats) {

        //查询退单
        NewPileReturnOrder returnOrder = newPileReturnOrderQuery.findByIdNewPile(returnOrderCode);


        //退款退单状态需要是已审核
        if (returnOrder != null && returnOrder.getReturnType() == ReturnType.REFUND) {
            if (returnOrder.getReturnFlowState() != NewPileReturnFlowState.AUDIT) {
                throw new SbcRuntimeException("K-050004");
            }
        }
        //退货退单状态需要是已收到退货/退款失败
//        if (returnOrder != null && returnOrder.getReturnType() == ReturnType.RETURN) {
//            if (returnOrder.getReturnFlowState() != ReturnFlowState.RECEIVED &&
//                    returnOrder.getReturnFlowState() != ReturnFlowState.REFUND_FAILED) {
//                throw new SbcRuntimeException("K-050004");
//            }
//        }

        // 查询退款单
        RefundOrder refundOrder = refundOrderService.findRefundOrderByReturnOrderNo(returnOrderCode);
        NewPileTrade trade = newPileTradeService.detail(returnOrder.getTid());
        if (Objects.nonNull(trade) && Objects.nonNull(trade.getBuyer()) && StringUtils.isNotEmpty(trade.getBuyer()
                .getAccount())) {
            trade.getBuyer().setAccount(ReturnOrderService.getDexAccount(trade.getBuyer().getAccount()));
        }

        if (Objects.isNull(operator) || StringUtils.isBlank(operator.getUserId())) {
            operator = Operator.builder().ip("127.0.0.1").adminId("1").name("system").platform(Platform
                    .BOSS).build();
        }

        //添加wms货物不足退款标识
        if (Objects.nonNull(wmsStats)) {
            returnOrder.setWmsStats(wmsStats);
        }
        BigDecimal returnAmount = returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice()
                .getApplyPrice() : refundOrder.getReturnPrice();
        log.info("ReturnOrderService.refundOnlineByTid rid:{} returnAmount:{}", returnOrder.getId(), returnAmount);
        return refundOrderService.autoRefundNewPile(Collections.singletonList(trade), Collections.singletonList(returnOrder), Collections.singletonList(refundOrder), operator);
    }


    /**
     * 退单通知节点发送MQ消息
     *
     * @param nodeType
     * @param processType
     * @param params
     * @param rid
     * @param customerId
     */
    private void sendNoticeMessage(NodeType nodeType, ReturnOrderProcessType processType, List<String> params, String rid, String customerId, String pic, String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", nodeType.toValue());
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
     * 功能描述: <br>调用第三方WMS接口
     * 〈〉
     *
     * @Param: [returnOrder, warehouseId]
     * @Return: java.lang.Boolean
     * @Author:
     * @Date: 2020/5/19 15:12
     */
    private Boolean pushCancelOrder(ReturnOrder returnOrder, String warehouseId,Trade trade) {
        if(!orderCommonService.wmsCanTrade(trade)){
            return true;
        }
        //预售订单取消时，不调用wms取消  @jkp
        if (trade.getPresellFlag()){
            return true;
        }
        BaseResponse<ResponseWMSReturnResponse> result = null;
        try {
            result = requestWMSOrderProvider.cancelOrder(WMSOrderCancelRequest.builder()
                    .docNo(returnOrder.getTid())
                    .customerId("XYY")
                    .orderType("XSCK")
                    .warehouseId(warehouseId)
                    .consigneeId(returnOrder.getBuyer().getCustomerErpId())
                    .erpCancelReason(returnOrder.getReturnReason().getDesc())
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

    public void fillReturnGoodsList(ReturnOrder returnOrder) {
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        List<ReturnItem> newReturnItems = new ArrayList<>();
        for (ReturnItem returnItem : returnItems) {
            // 获取商品来源(囤货单)
            List<PickGoodsDTO> returnGoodsList = returnItem.getReturnGoodsList();
            if (CollectionUtils.isEmpty(returnGoodsList)) {
                log.warn("fillReturnGoodsList 数据异常：无退货商品来源囤货单数据，退单号：{}", returnOrder.getId());
                ReturnItem newTradeItem = KsBeanUtil.convert(returnItem, ReturnItem.class);
                newReturnItems.add(newTradeItem);
                continue;
            }

            for (PickGoodsDTO returnGoodsDTO : returnGoodsList) {
                ReturnItem newTradeItem = KsBeanUtil.convert(returnItem, ReturnItem.class);
                newTradeItem.setReturnGoodsList(Collections.singletonList(returnGoodsDTO));
                newTradeItem.setNum(BigDecimal.valueOf(returnGoodsDTO.getNum()));
                //设置实付金额
                newTradeItem.setSplitPrice(returnGoodsDTO.getSplitPrice());
                newReturnItems.add(newTradeItem);
            }
        }
        if (!newReturnItems.isEmpty()) {
            returnOrder.setReturnItems(newReturnItems);
        }
    }

    /**
     * 新提货退单：退优惠券
     *
     * @param rid
     */
    public void returnCoupon(String rid) {
        ReturnOrder returnOrder = findById(rid);
        if (CollectionUtils.isEmpty(returnOrder.getReturnItems())) {
            log.warn("退货条目为空，退单号={}", returnOrder.getId());
            return;
        }
        returnOrder.getReturnItems().forEach(item -> {
            if (CollectionUtils.isEmpty(item.getReturnGoodsList())) {
                log.warn("退货条目：无退货商品记录，退单号={}", returnOrder.getId());
                return;
            }
        });

        List<String> assignedPileNos = returnOrder.getReturnItems().stream()
                .flatMap(returnItemVO -> returnItemVO.getReturnGoodsList().stream())
                .map(PickGoodsDTO::getNewPileOrderNo)
                .distinct()
                .collect(Collectors.toList());
        List<NewPileTrade> newPileTrades = newPileTradeService.listByPileNos(assignedPileNos);
        List<NewPileTrade> pileTradesNeedReturnCoupon = newPileTrades.stream().filter(pileTrade -> {
            //收集使用了优惠券的囤货订单
            return pileTrade.getTradePrice().getCouponPrice().compareTo(BigDecimal.ZERO) > 0;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(pileTradesNeedReturnCoupon)) {
            return;
        }

        //处理需要退优惠券的囤货单
        pileTradesNeedReturnCoupon.forEach(newPileTradeVO -> {
            newPileTradeService.returnCoupon(newPileTradeVO.getId(), returnOrder.getId());
        });
    }

    public List<ReturnOrder> findByTid(String tid) {
        return returnOrderRepository.findByTid(tid);
    }

    public void pushRefundOrderFreightKingdee(BigDecimal freight, String returnOrderCode, String tid, String account) {
        returnOrderCode = returnOrderCode + "YF";
        tid = tid + "YF";

        log.info("退款收款单运费推送金蝶 单号:{},运费:{}", returnOrderCode, freight);

        Integer number = tradePushKingdeeRefundRepository.selcetPushKingdeeRefundNumber(returnOrderCode);
        TradePushKingdeeRefund pushKingdeeRefundOrder = new TradePushKingdeeRefund();
        pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        try {

            KingdeeRefundOrder payOrder = new KingdeeRefundOrder();
            payOrder.setFDate(DateUtil.nowDate());
            payOrder.setOrderNumber(returnOrderCode);
            payOrder.setFSaleNum(tid);
            Map FCustId = new HashMap();
            FCustId.put("FNumber", account);
            payOrder.setFCustId(FCustId);
            Map FPAYORGID = new HashMap();
            FPAYORGID.put("FNumber", kingdeeOrganization);
            payOrder.setFPAYORGID(FPAYORGID);
            List<KingdeeRefundOrderSettlement> freceivebillentry = new ArrayList<>();
            KingdeeRefundOrderSettlement payOrderSettlement = new KingdeeRefundOrderSettlement();
            String refundComment = "申请退款";
            Map FSETTLETYPEID = new HashMap();

            FSETTLETYPEID.put("FNumber", PayWay.CCB.toValue());

            //使用银行卡
            Map FACCOUNTID = new HashMap();
            FACCOUNTID.put("FNumber", kingdeeCcbpay);

            payOrderSettlement.setFACCOUNTID(FACCOUNTID);
            payOrderSettlement.setFSETTLETYPEID(FSETTLETYPEID);

            payOrderSettlement.setFRECTOTALAMOUNTFOR(freight.toPlainString());

            payOrderSettlement.setFNOTE(refundComment + "-" + returnOrderCode);
            freceivebillentry.add(payOrderSettlement);
            payOrder.setFRECEIVEBILLENTRY(freceivebillentry);
            //销售类型
            Map fSaleType = new HashMap();
            fSaleType.put("FNumber", String.valueOf(SaleType.WHOLESALE.toValue()));
            payOrder.setFSaleType(fSaleType);

            //登录财务系统
            Map<String, Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user", kingdeeUser);
            requestLogMap.put("pwd", kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)) {
                //提交财务单
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("Model", payOrder);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(refurnUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                logger.info("ReturnOrderService.pushRefundOrderCommissionKingdee result1:{}  code:{}", result1.getResultData(),  result1.getResultCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")) {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                } else {
                    pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                pushKingdeeRefundOrder.setInstructions(result1.getResultData());
            } else {
                String res = "金蝶登录失败";
                pushKingdeeRefundOrder.setInstructions(res);
                pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        } catch (Exception e) {
            logger.error("ReturnOrderService.pushRefundOrderCommissionKingdee error:", e);
            String res = "金蝶推送失败";
            pushKingdeeRefundOrder.setInstructions(res);
            pushKingdeeRefundOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
        } finally {
            //没有创建，有就更新
            if (number == 0) {
                pushKingdeeRefundOrder.setOrderCode(tid);
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                pushKingdeeRefundOrder.setPayType(PayWay.CCB.toValue());
                pushKingdeeRefundOrder.setCreateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setCustomerAccount(account);
                tradePushKingdeeRefundRepository.saveAndFlush(pushKingdeeRefundOrder);
            } else {
                pushKingdeeRefundOrder.setUpdateTime(LocalDateTime.now());
                pushKingdeeRefundOrder.setRefundCode(returnOrderCode);
                tradePushKingdeeRefundRepository.updatePushKingdeeRefundOrderState(pushKingdeeRefundOrder);
            }
        }
    }

    public List<ReturnOrder> findByVillageTradeIdList(List<String> tids){
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(Criteria.where("tid").in(tids));
        Map fieldsObject = new HashMap();
        fieldsObject.put("_id", true);
        fieldsObject.put("tid", true);
        Query query = new BasicQuery(new Document(), new Document(fieldsObject));
        Criteria whereCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
        query.addCriteria(whereCriteria);
        List<ReturnOrder> returnOrderList = mongoTemplate.find(query, ReturnOrder.class);
        if(returnOrderList == null){
            returnOrderList = new ArrayList<>(0);
        }
        return returnOrderList;
    }
}
