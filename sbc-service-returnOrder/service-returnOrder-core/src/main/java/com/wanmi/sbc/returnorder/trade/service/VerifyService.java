package com.wanmi.sbc.returnorder.trade.service;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.api.provider.invoice.InvoiceProjectSwitchQueryProvider;
import com.wanmi.sbc.account.api.request.invoice.InvoiceProjectSwitchByCompanyInfoIdRequest;
import com.wanmi.sbc.account.api.response.invoice.InvoiceProjectSwitchByCompanyInfoIdResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelMapByCustomerIdAndStoreIdsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreCheckRequest;
import com.wanmi.sbc.customer.api.request.store.StoreCustomerRelaQueryRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreCustomerRelaResponse;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsSaveProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.soldoutgoods.SoldOutGoodsProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.flashsalegoods.FlashSaleGoodsBatchStockAndSalesVolumeRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockUpdateRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchMinusStockRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchPlusStockRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsAddRequest;
import com.wanmi.sbc.goods.api.response.cate.ContractCateListResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockByGoodsIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoMinusStockDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountQueryProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionQueryProvider;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoQueryRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.enums.FullBuyType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.returnorder.api.request.flashsale.FlashSaleGoodsOrderCancelReturnStockRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeCommitRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradeRefreshInventoryRequest;
import com.wanmi.sbc.returnorder.api.response.trade.TradeGetGoodsResponse;
import com.wanmi.sbc.returnorder.bean.enums.DeliverStatus;
import com.wanmi.sbc.returnorder.bean.vo.TradeGoodsListVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeItemVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.common.OrderCommonService;
import com.wanmi.sbc.returnorder.mq.FlashSaleGoodsOrderCancelReturnStockService;
import com.wanmi.sbc.returnorder.mq.OrderProducerService;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchase;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseRepository;
import com.wanmi.sbc.returnorder.redis.RedisCache;
import com.wanmi.sbc.returnorder.redis.RedisService;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeState;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Consignee;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Invoice;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Supplier;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemGroup;
import com.wanmi.sbc.returnorder.trade.model.root.TradeItemSplitRecord;
import com.wanmi.sbc.setting.api.provider.SystemPointsConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.SystemPointsConfigQueryResponse;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 校验Service
 * Created by jinwei on 22/3/2017.
 */
@Service
@Slf4j
public class VerifyService {

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private VillagesAddressConfigQueryProvider villagesAddressConfigQueryProvider;

    /***
     * @desc  检查乡镇件、免费店配、第三方物流
     * @author shiy  2023/7/4 11:32
    */
    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private InvoiceProjectSwitchQueryProvider invoiceProjectSwitchQueryProvider;

    @Autowired
    private MarketingFullDiscountQueryProvider marketingFullDiscountQueryProvider;

    @Autowired
    private MarketingFullReductionQueryProvider marketingFullReductionQueryProvider;

    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private SystemPointsConfigQueryProvider systemPointsConfigQueryProvider;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private FlashSaleGoodsSaveProvider flashSaleGoodsSaveProvider;

    @Autowired
    private FlashSaleGoodsOrderCancelReturnStockService flashSaleGoodsOrderCancelReturnStockService;

    @Autowired
    private TradeCacheService tradeCacheService;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private GoodsWareStockProvider goodsWareStockProvider;

    @Autowired
    private SoldOutGoodsProvider soldOutGoodsProvider;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private PilePurchaseRepository pilePurchaseRepository;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private TradeItemSplitRecordService tradeItemSplitRecordService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OrderCommonService orderCommonService;

    @Value("${d2p.erp.desku.url}")
    private String erpDeSkuUrl;

    /**
     * 校验购买商品,计算商品价格
     * 1.校验商品库存，删除，上下架状态
     * 2.验证购买商品的起订量,限定量
     * 3.根据isFull,填充商品信息与levelPrice(第一步价格计算)
     *
     * @param tradeItems        订单商品数据，仅包含skuId与购买数量
     * @param oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param goodsInfoResponse 关联商品信息
     * @param isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     */
    public List<TradeItem> verifyGoods(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                       boolean isFull,Boolean checkStockFlag) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity(),(v1, v2) -> v2));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity(),(v1, v2) -> v2));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
//        boolean lackStockFlag=false;
        Map<String, List<TradeItem>> itemsMap = tradeItems.stream().collect(Collectors.groupingBy(TradeItem::getSkuId));

        Map<String,BigDecimal> availableStockBySkuMap = new LinkedHashMap<>();
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                    || goods.getAuditStatus() == CheckStatus.FORBADE) {
                throw new SbcRuntimeException("K-050117");
            }

            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())&&(Objects.nonNull(checkStockFlag)&&checkStockFlag)) {
                if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((goodsInfo.getStock().add(BigDecimal.valueOf(oldNum)) )) > 0  && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    throw new SbcRuntimeException("K-050116");
                }
                //缺货和库存不存为两种情况
                if (goodsInfo.getStock().subtract(goodsInfo.getLockStock()).compareTo(BigDecimal.ZERO) <= 0) {
//                    lackStockFlag=true;
                    availableStockBySkuMap.putIfAbsent(goodsInfo.getGoodsInfoId(),goodsInfo.getStock().subtract(goodsInfo.getLockStock()));
                    continue;
                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
        if (!availableStockBySkuMap.isEmpty() && (Objects.nonNull(checkStockFlag) && checkStockFlag)) {
            List<Map<String,Object>> lackStockSkuInfos = new ArrayList<>();
            availableStockBySkuMap.forEach((k, v) -> {
                GoodsInfoVO goodsInfoVO = goodsInfoMap.getOrDefault(k, new GoodsInfoVO());
                Map<String,Object> skuMap = new LinkedHashMap<>();
                skuMap.put("goodsInfoId",k);
                skuMap.put("goodsInfoName",goodsInfoVO.getGoodsInfoName());
                skuMap.put("availableStock",v);
                lackStockSkuInfos.add(skuMap);
            });
            throw new SbcRuntimeException(JSON.toJSONString(lackStockSkuInfos),"K-050521","很抱歉，您的订单中包含缺货商品，请修改后重新提交！");
        }
        return tradeItems;
    }


    /**
     * 校验购买商品,计算商品价格
     * 1.校验商品库存，删除，上下架状态
     * 2.验证购买商品的起订量,限定量
     * 3.根据isFull,填充商品信息与levelPrice(第一步价格计算)
     *
     * @param tradeItems        订单商品数据，仅包含skuId与购买数量
     * @param oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param goodsInfoResponse 关联商品信息
     * @param isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     */
    public List<TradeItem> verifyGoodsDevanning(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                       boolean isFull,Boolean checkStockFlag) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity(),(v1, v2) -> v2));
        Map<Long, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getDevanningId, Function.identity(),(v1, v2) -> v2));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getDevanningId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                    || goods.getAuditStatus() == CheckStatus.FORBADE) {
                throw new SbcRuntimeException("K-050117");
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())&&(Objects.nonNull(checkStockFlag)&&!checkStockFlag)) {
                if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((goodsInfo.getStock().add(BigDecimal.valueOf(oldNum)) )) > 0  && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    throw new SbcRuntimeException("K-050116");
                }
                //缺货和库存不存为两种情况
                if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    lackStockFlag=true;
                    continue;
                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
        if (lackStockFlag&&(Objects.nonNull(checkStockFlag)&&!checkStockFlag)){
            throw new SbcRuntimeException("K-050137");
        }
        return tradeItems;
    }


    public List<TradeItem> verifyPileGoods(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                       boolean isFull,Boolean checkStockFlag) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity()));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                    || goods.getAuditStatus() == CheckStatus.FORBADE) {
                throw new SbcRuntimeException("K-050117");
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())&&(Objects.nonNull(checkStockFlag)&&!checkStockFlag)) {
                if (BigDecimal.valueOf(tradeItem.getNum()).compareTo(goodsInfo.getStock().add(BigDecimal.valueOf(oldNum))) >  0 && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    throw new SbcRuntimeException("K-050116");
                }
                //缺货和库存不存为两种情况
                if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    lackStockFlag=true;
                    continue;
                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
//        if (lackStockFlag&&(Objects.nonNull(checkStockFlag)&&!checkStockFlag)){
//            throw new SbcRuntimeException("K-050137");
//        }
        return tradeItems;
    }

    /**
     * 校验购买商品,计算商品价格
     * 1.校验商品库存，删除，上下架状态
     * 2.验证购买商品的起订量,限定量
     * 3.根据isFull,填充商品信息与levelPrice(第一步价格计算)
     *
     * @param tradeItems        订单商品数据，仅包含skuId与购买数量
     * @param oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param goodsInfoResponse 关联商品信息
     * @param isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     */
    public List<TradeItem> verifyTakeGoods(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                       boolean isFull,Boolean checkStockFlag,String customerId) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity()));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //是否校验商品上下架状态
            if (!goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue())) {
                //1. 校验商品库存，删除，上下架状态
                if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                        || goods.getAuditStatus() == CheckStatus.FORBADE) {
                    throw new SbcRuntimeException("K-050117");
                }
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
//                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())&&(Objects.nonNull(checkStockFlag)&&!checkStockFlag)) {
                if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((goodsInfo.getStock().add(BigDecimal.valueOf(oldNum)))) > 0  && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    throw new SbcRuntimeException("K-050116");
                }
                //缺货和库存不存为两种情况
                if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    lackStockFlag=true;
                    continue;
                }
                //2. 验证购买商品的起订量,限定量,
                verifyGoodsInternal(tradeItem, goodsInfo);

                //3. 严验证提货商品数量是否超过囤货商品数量
                verifyTakeGoodsNum(tradeItem,customerId);
            }
        }
        //缺货
        if (lackStockFlag&&(Objects.nonNull(checkStockFlag)&&!checkStockFlag)){
            throw new SbcRuntimeException("K-050137");
        }
        return tradeItems;
    }

    /**
     * 校验购买商品,计算商品价格
     * 1.校验商品库存，删除，上下架状态
     * 2.验证购买商品的起订量,限定量
     * 3.根据isFull,填充商品信息与levelPrice(第一步价格计算)
     *
     * @param tradeItems        订单商品数据，仅包含skuId与购买数量
     * @param oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param goodsInfoResponse 关联商品信息
     * @param isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     */
    public Boolean verifyGoodsTo(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                       boolean isFull) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity()));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        boolean unenoughStock=false;
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (!goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue())) {
                if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                        || goods.getAuditStatus() == CheckStatus.FORBADE) {
                    throw new SbcRuntimeException("K-050117");
                }
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
                if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((goodsInfo.getStock() .add(BigDecimal.valueOf(oldNum)) )) > 0  && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    if (!unenoughStock){
                        unenoughStock=true;
                    }
                   /* TradeItemSnapshot byCustomerId = tradeItemService.findByCustomerId(customerId);
                    byCustomerId.getItemGroups().forEach(param->{if (param.getSupplier().getStoreId().equals(storeId)){param.setTradeMarketings(tradeMarketings);}});
                    tradeItemService.updateTradeItemSnapshotNoRollback(byCustomerId);
                    throw new SbcRuntimeException("K-050116");*/
                }
                //缺货和库存不存为两种情况
                if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    lackStockFlag=true;
                    continue;
                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
        if (lackStockFlag||unenoughStock){
           /* throw new SbcRuntimeException("K-050137");*/
            return true;
        }
        return false;
    }

    /**
     * 校验购买商品,计算商品价格
     * 1.校验商品库存，删除，上下架状态
     * 2.验证购买商品的起订量,限定量
     * 3.根据isFull,填充商品信息与levelPrice(第一步价格计算)
     *
     * @param tradeItems        订单商品数据，仅包含skuId与购买数量
     * @param oldTradeItems     旧订单商品数据，可以为emptyList，用于编辑订单的场景，由于旧订单商品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param goodsInfoResponse 关联商品信息
     * @param isFull            是否填充订单商品信息与设价(区间价/已经算好的会员价)
     */
    public Boolean verifyDevanningGoodsTo(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                 boolean isFull) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity(),(v1, v2) -> v2));
        Map<String, GoodsInfoVO> goodsInfoMap = new HashMap<>();
        for (GoodsInfoVO f: goodsInfos){
            if (Objects.nonNull(f.getDevanningId())){
                goodsInfoMap.put(f.getDevanningId().toString(),f);
            }else {
                goodsInfoMap.put(f.getGoodsInfoId(),f);
            }
        }

//        Map<Long, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
//                (GoodsInfoVO::getDevanningId, Function.identity(),(v1, v2) -> v2));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        boolean unenoughStock=false;

        //统计商品数量
        HashMap<String, BigDecimal> buyNumMaps = Maps.newHashMap();

        Map<Long, DevanningGoodsInfoVO> devanningMaps = devanningGoodsInfoQueryProvider.getInfoByIds(DevanningGoodsInfoByIdRequest
                        .builder().devanningIds(tradeItems.stream().map(TradeItem::getDevanningId).collect(Collectors.toList()))
                        .build()).getContext()
                .getDevanningGoodsInfoVOS().stream()
                .collect(Collectors.toMap(DevanningGoodsInfoVO::getDevanningId, Function.identity()));


        tradeItems.forEach(v->{
          if (Objects.nonNull(v.getDevanningId())) {
              DevanningGoodsInfoVO devanningInfoVO=  devanningMaps.get(v.getDevanningId());
//              DevanningGoodsInfoVO devanningInfoVO = devanningGoodsInfoQueryProvider.getInfoById(
//                      DevanningGoodsInfoByIdRequest.builder()
//                              .devanningId(v.getDevanningId())
//                              .build()).getContext().getDevanningGoodsInfoVO();
              v.setDivisorFlag(devanningInfoVO.getDivisorFlag());
              //设置商品副标题
              v.setGoodsSubtitle(devanningInfoVO.getGoodsInfoSubtitle());
              BigDecimal multiply = BigDecimal.valueOf(v.getNum()).multiply(devanningInfoVO.getDivisorFlag());
              if(Objects.isNull(buyNumMaps.get(v.getSkuId()))){
                  buyNumMaps.put(v.getSkuId(),multiply);
              }else{
                  buyNumMaps.put(v.getSkuId(),buyNumMaps.get(v.getSkuId()).add(multiply));
              }
          }else {
              buyNumMaps.put(v.getSkuId(),BigDecimal.valueOf(v.getNum()));
          }

        });
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = Objects.nonNull(tradeItem.getDevanningId())?
                    goodsInfoMap.get(tradeItem.getDevanningId().toString()):goodsInfoMap.get(tradeItem.getSkuId());
//            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getDevanningId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (!goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue())) {
                if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                        || goods.getAuditStatus() == CheckStatus.FORBADE) {
                    throw new SbcRuntimeException("K-050117");
                }
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
                if(Objects.nonNull(tradeItem.getDevanningId())){
                    //拆箱判断
                    BigDecimal buyNum = buyNumMaps.get(tradeItem.getSkuId());
                    if (buyNum.compareTo((goodsInfo.getStock() .add( BigDecimal.valueOf(oldNum)))) > 0
                            && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                        if (!unenoughStock){
                            unenoughStock=true;
                        }
                    }
                }else{
                    if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((goodsInfo.getStock() .add( BigDecimal.valueOf(oldNum)))) > 0
                            && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                        if (!unenoughStock){
                            unenoughStock=true;
                        }
                       /* TradeItemSnapshot byCustomerId = tradeItemService.findByCustomerId(customerId);
                        byCustomerId.getItemGroups().forEach(param->{if (param.getSupplier().getStoreId().equals(storeId)){param.setTradeMarketings(tradeMarketings);}});
                        tradeItemService.updateTradeItemSnapshotNoRollback(byCustomerId);
                        throw new SbcRuntimeException("K-050116");*/
                    }
                }
                //缺货和库存不存为两种情况
                if (goodsInfo.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    lackStockFlag=true;
                    continue;
                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
        if (lackStockFlag||unenoughStock){
            /* throw new SbcRuntimeException("K-050137");*/
            return true;
        }
        return false;
    }


    /**
     * 囤货 下单
     * @param tradeItems
     * @param oldTradeItems
     * @param goodsInfoResponse
     * @param storeId
     * @param isFull
     * @return
     */
    public Boolean verifyDevanningGoodsToNewPile(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                          boolean isFull) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();

        List<String> goodsInfoids = goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());

        //设置囤货商品活动是否过期
        Map<String, Long> collect2 = new HashMap<>();
        BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();
        if(CollectionUtils.isEmpty(startPileActivity.getContext())){
            throw new SbcRuntimeException("K-060137", "无正在进行中的囤货活动");
        }
        //多商家囤货获取参与囤货活动商品虚拟库存
        List<PileActivityGoodsVO> context = pileActivityProvider.getStartPileActivityPileActivityGoods(
                PileActivityPileActivityGoodsRequest.builder().goodsInfoIds(goodsInfoids).build()).getContext();

        if (CollectionUtils.isNotEmpty(context)) {
            collect2= context.stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, PileActivityGoodsVO::getVirtualStock));
        }


        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity(),(v1, v2) -> v2));
        Map<String, GoodsInfoVO> goodsInfoMap = new HashMap<>();
        for (GoodsInfoVO f: goodsInfos){
            if (Objects.nonNull(f.getDevanningId())){
                goodsInfoMap.put(f.getDevanningId().toString(),f);
            }else {
                goodsInfoMap.put(f.getGoodsInfoId(),f);
            }
        }

//        Map<Long, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
//                (GoodsInfoVO::getDevanningId, Function.identity(),(v1, v2) -> v2));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        boolean unenoughStock=false;

        //统计商品数量
        HashMap<String, BigDecimal> buyNumMaps = Maps.newHashMap();
        tradeItems.forEach(v->{
            if (Objects.nonNull(v.getDevanningId())) {
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
            }else {
                buyNumMaps.put(v.getSkuId(),BigDecimal.valueOf(v.getNum()));
            }

        });
        for (TradeItem tradeItem : tradeItems) {
            Long virtualStock = collect2.getOrDefault(tradeItem.getSkuId(), 0L); //囤货虚拟库存
            GoodsInfoVO goodsInfo = Objects.nonNull(tradeItem.getDevanningId())?
                    goodsInfoMap.get(tradeItem.getDevanningId().toString()):goodsInfoMap.get(tradeItem.getSkuId());
//            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getDevanningId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (!goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue())) {
                if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                        || goods.getAuditStatus() == CheckStatus.FORBADE) {
                    throw new SbcRuntimeException("K-050117");
                }
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
                if(Objects.nonNull(tradeItem.getDevanningId())){

                    //拆箱判断
                    BigDecimal buyNum = buyNumMaps.get(tradeItem.getSkuId());
                    if (buyNum.compareTo((BigDecimal.valueOf(virtualStock) .add( BigDecimal.valueOf(oldNum)))) > 0
                            && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                        if (!unenoughStock){
                            unenoughStock=true;
                        }
                    }
                }else{
                    if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((BigDecimal.valueOf(virtualStock) .add( BigDecimal.valueOf(oldNum)))) > 0
                            && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                        if (!unenoughStock){
                            unenoughStock=true;
                        }
                    }
                }
                //缺货和库存不存为两种情况
                if (BigDecimal.valueOf(virtualStock).compareTo(BigDecimal.ZERO) <= 0) {
                    lackStockFlag=true;
                    continue;
                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
        if (lackStockFlag||unenoughStock){
            /* throw new SbcRuntimeException("K-050137");*/
            return true;
        }
        return false;
    }


    public Boolean verifyPileGoodsTo(List<TradeItem> tradeItems, List<TradeItem> oldTradeItems, TradeGoodsListVO goodsInfoResponse, Long storeId,
                                 boolean isFull) {
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity()));
        Map<String, Long> oldTradeItemMap = oldTradeItems.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        boolean lackStockFlag=false;
        boolean unenoughStock=false;
        for (TradeItem tradeItem : tradeItems) {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            Long oldNum = oldTradeItemMap.getOrDefault(tradeItem.getSkuId(), 0L);
            //1. 校验商品库存，删除，上下架状态
            if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                    || goods.getAuditStatus() == CheckStatus.FORBADE) {
                throw new SbcRuntimeException("K-050117");
            }
            if (isFull) {
                //3. 填充订单项基本数据
                merge(tradeItem, goodsInfo, goods, storeId);
                //4. 填充订单项区间价->levelPrice
                calcPrice(tradeItem, goodsInfo, goods, goodsInfoResponse.getGoodsIntervalPrices());
            }
            //判断是否是秒杀抢购订单，如果不是秒杀抢购订单进行一下校验
            if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
                if (BigDecimal.valueOf(tradeItem.getNum()).compareTo((goodsInfo.getStock() .add(BigDecimal.valueOf(oldNum)) )) > 0  && goodsInfo.getStock().compareTo(BigDecimal.ZERO) > 0) {
                    if (!unenoughStock){
                        unenoughStock=true;
                    }
                   /* TradeItemSnapshot byCustomerId = tradeItemService.findByCustomerId(customerId);
                    byCustomerId.getItemGroups().forEach(param->{if (param.getSupplier().getStoreId().equals(storeId)){param.setTradeMarketings(tradeMarketings);}});
                    tradeItemService.updateTradeItemSnapshotNoRollback(byCustomerId);
                    throw new SbcRuntimeException("K-050116");*/
                }
                //缺货和库存不存为两种情况
//                if (goodsInfo.getStock() <= 0) {
//                    lackStockFlag=true;
//                    continue;
//                }
                //2. 验证购买商品的起订量,限定量
                verifyGoodsInternal(tradeItem, goodsInfo);
            }
        }
        //缺货
//        if (lackStockFlag||unenoughStock){
            /* throw new SbcRuntimeException("K-050137");*/
//            return true;
//        }
        return false;
    }

    /**
     * * 校验购买积分商品
     * 1.校验积分商品库存，删除，上下架状态
     *
     * @param goodsInfoResponse 关联商品信息
     * @param storeId           店铺ID
     * @return
     */
    public TradeItem verifyPointsGoods(TradeItem tradeItem, TradeGoodsListVO goodsInfoResponse, PointsGoodsVO
            pointsGoodsVO, Long storeId) {
        GoodsInfoVO goodsInfo = goodsInfoResponse.getGoodsInfos().get(0);
        GoodsVO goods = goodsInfoResponse.getGoodses().get(0);

        // 1.验证积分商品(校验积分商品库存，删除，启用停用状态，兑换时间)
        if (DeleteFlag.YES.equals(pointsGoodsVO.getDelFlag()) || EnableStatus.DISABLE.equals(pointsGoodsVO.getStatus())) {
            throw new SbcRuntimeException("K-120001");
        }
        if (pointsGoodsVO.getStock() < tradeItem.getNum()) {
            throw new SbcRuntimeException("K-120002");
        }
        if (pointsGoodsVO.getEndTime().isBefore(LocalDateTime.now())) {
            throw new SbcRuntimeException("K-120003");
        }
        // 2.填充订单商品信息
        merge(tradeItem, goodsInfo, goods, storeId);

        return tradeItem;
    }


    /**
     * 为tradeItem 填充商品基本信息
     *
     * @param tradeItems        订单商品数据，仅包含skuId/价格
     * @param goodsInfoResponse 关联商品信息
     */
    public List<TradeItem> mergeGoodsInfo(List<TradeItem> tradeItems, TradeGetGoodsResponse goodsInfoResponse) {
        if (CollectionUtils.isEmpty(tradeItems)) {
            return Collections.emptyList();
        }
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity()));
        tradeItems
                .forEach(tradeItem -> {
                    GoodsInfoVO goodsInfo = goodsInfoMap.get(tradeItem.getSkuId());
                    GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
                    //为tradeItem填充商品基本信息
                    merge(tradeItem, goodsInfo, goods, null);
                });
        return tradeItems;
    }

    public void verifyStore(List<Long> storeIds) {
        StoreCheckRequest request = new StoreCheckRequest();
        request.setIds(storeIds);
        if (!storeProvider.checkStore(request).getContext().getResult()) {
            throw new SbcRuntimeException("K-050117");
        }
    }

    /**
     * 为tradeItem填充商品基本信息
     * 主要是合并价格和名称这些字段
     *
     * @param tradeItem
     * @param goodsInfo
     * @param goods
     */
    void merge(TradeItem tradeItem, GoodsInfoVO goodsInfo, GoodsVO goods, Long storeId) {
        tradeItem.setSkuName(goodsInfo.getGoodsInfoName());
        tradeItem.setSpuName(goods.getGoodsName());
        tradeItem.setPic(goodsInfo.getGoodsInfoImg());
        tradeItem.setBrand(goods.getBrandId());
        tradeItem.setDeliverStatus(DeliverStatus.NOT_YET_SHIPPED);
        tradeItem.setCateId(goods.getCateId());
        tradeItem.setSkuNo(goodsInfo.getGoodsInfoNo());
        tradeItem.setSpuId(goods.getGoodsId());
        tradeItem.setUnit(goods.getGoodsUnit());
        tradeItem.setGoodsWeight(goods.getGoodsWeight());
        tradeItem.setGoodsCubage(goods.getGoodsCubage());
        tradeItem.setFreightTempId(goods.getFreightTempId());
        tradeItem.setStoreId(storeId);
        tradeItem.setDistributionGoodsAudit(goodsInfo.getDistributionGoodsAudit());
        tradeItem.setCommissionRate(goodsInfo.getCommissionRate());
        tradeItem.setDistributionCommission(goodsInfo.getDistributionCommission());
        tradeItem.setOriginalPrice(BigDecimal.ZERO);
        tradeItem.setLevelPrice(BigDecimal.ZERO);
        tradeItem.setSplitPrice(BigDecimal.ZERO);
        tradeItem.setEnterPriseAuditState(goodsInfo.getEnterPriseAuditState());
        tradeItem.setVipPrice(goodsInfo.getVipPrice());//订单的vipPrice
        tradeItem.setCost(goodsInfo.getCostPrice());//订单商品的成本价
        tradeItem.setGoodsInfoType(goodsInfo.getGoodsInfoType());
        tradeItem.setSpecialPrice(goodsInfo.getSpecialPrice());
        tradeItem.setGoodsBatchNo(goodsInfo.getGoodsInfoBatchNo());
        tradeItem.setErpSkuNo(goodsInfo.getErpGoodsInfoNo());
        tradeItem.setAddStep(goodsInfo.getAddStep());
        if(Objects.isNull(tradeItem.getGoodsSubtitle())){
            tradeItem.setGoodsSubtitle(goods.getGoodsSubtitle());
        }
        if (StringUtils.isBlank(tradeItem.getSpecDetails())) {
            tradeItem.setSpecDetails(goodsInfo.getSpecText());
        }

        if (osUtil.isS2b() && storeId != null) {
            BaseResponse<ContractCateListResponse> baseResponse = tradeCacheService.queryContractCateList(storeId,goods.getCateId());
            ContractCateListResponse contractCateListResponse = baseResponse.getContext();
            if (Objects.nonNull(contractCateListResponse)) {
                List<ContractCateVO> cates = contractCateListResponse.getContractCateList();
                if (CollectionUtils.isNotEmpty(cates)) {
                    ContractCateVO cateResponse = cates.get(0);
                    tradeItem.setCateName(cateResponse.getCateName());
                    tradeItem.setCateRate(cateResponse.getCateRate() != null ? cateResponse.getCateRate() : cateResponse.getPlatformCateRate());
                }

            }
        }
    }

    /**
     * 设置订单项商品的订货区间价
     * 若无区间价,则设置为会员价(在前面使用插件已经算好的salePrice)
     * 【商品价格计算第①步】: 商品的 客户级别价格 (完成客户级别价格/客户指定价/订货区间价计算) -> levelPrice
     *
     * @param tradeItem
     * @param goodsInfo
     * @param goods
     * @param goodsIntervalPrices
     */
    void calcPrice(TradeItem tradeItem, GoodsInfoVO goodsInfo, GoodsVO goods, List<GoodsIntervalPriceVO>
            goodsIntervalPrices) {
        // 订货区间设价
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            Long buyNum = tradeItem.getNum();
            Optional<GoodsIntervalPriceVO> first = goodsIntervalPrices.stream()
                    .filter(item -> item.getGoodsInfoId().equals(tradeItem.getSkuId()))
                    .filter(intervalPrice -> buyNum >= intervalPrice.getCount())
                    .max(Comparator.comparingLong(GoodsIntervalPriceVO::getCount));
            if (first.isPresent()) {
                GoodsIntervalPriceVO goodsIntervalPrice = first.get();
                tradeItem.setLevelPrice(goodsIntervalPrice.getPrice());
                tradeItem.setOriginalPrice(goodsInfo.getMarketPrice());
                //判断是否为秒杀抢购商品
                if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
                    tradeItem.setPrice(goodsIntervalPrice.getPrice());
                    tradeItem.setSplitPrice(tradeItem.getLevelPrice().multiply(
                            new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP)
                    );
                } else {
                    tradeItem.setSplitPrice(tradeItem.getPrice().multiply(
                            new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                return;
            }
        }
        tradeItem.setLevelPrice(goodsInfo.getSalePrice());
        tradeItem.setOriginalPrice(goodsInfo.getMarketPrice());
        //判断是否为秒杀抢购商品
        if (!(Objects.nonNull(tradeItem.getIsFlashSaleGoods()) && tradeItem.getIsFlashSaleGoods())) {
            tradeItem.setPrice(goodsInfo.getSalePrice());
            tradeItem.setSplitPrice(tradeItem.getLevelPrice().multiply(
                    new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP)
            );
        } else {
            tradeItem.setSplitPrice(tradeItem.getPrice().multiply(
                    new BigDecimal(tradeItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    /**
     * 校验起订量,限定量
     *
     * @param tradeItem
     * @param goodsInfo
     * @return
     */
    public void verifyGoodsInternal(TradeItem tradeItem, GoodsInfoVO goodsInfo) {

        // 起订量
        if (goodsInfo.getCount() != null) {
            if (BigDecimal.valueOf(goodsInfo.getCount()).compareTo(goodsInfo.getStock()) > 0 ) {
                throw new SbcRuntimeException("K-050116");
            }
            if (BigDecimal.valueOf(goodsInfo.getCount()).compareTo(BigDecimal.valueOf(tradeItem.getNum())) >  0) {
                throw new SbcRuntimeException("K-050140", new Object[]{goodsInfo.getGoodsInfoName(), goodsInfo.getCount(), tradeItem.getNum()});
            }
        }

        // 限定量
        if (goodsInfo.getMaxCount() != null && goodsInfo.getMaxCount() < tradeItem.getNum()) {
            throw new SbcRuntimeException("K-050140", new Object[]{goodsInfo.getGoodsInfoName(), goodsInfo.getMaxCount(), tradeItem.getNum()});
        }

    }

    /**
     * 校验提货商品数量是否超过商品
     * @param tradeItem
     */
    public void verifyTakeGoodsNum(TradeItem tradeItem,String customerId){
        if (Objects.nonNull(tradeItem) && Objects.nonNull(customerId)){
            //查询客户此商品囤货信息
            List<PilePurchase> pilePurchases = pilePurchaseRepository.queryPilePurchase(customerId, tradeItem.getSpuId(), tradeItem.getSkuId());
            if (CollectionUtils.isNotEmpty(pilePurchases)){
                //囤货数量
                Long sum = pilePurchases.stream().mapToLong(PilePurchase::getGoodsNum).sum();
                if (tradeItem.getNum() > sum){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+tradeItem.getSkuName()+
                            "，提货数量："+tradeItem.getNum()+"大于囤货数量："+sum+"，请修改提货订单数量后下单！");
                }
                PilePurchase pilePurchase = pilePurchases.stream().findFirst().get();
                if (tradeItem.getNum() > pilePurchase.getGoodsNum()){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+tradeItem.getSkuName()+
                            "，提货数量："+tradeItem.getNum()+"大于囤货数量："+pilePurchase.getGoodsNum()+"，请修改提货订单数量后下单！");
                }
            } else {
               throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商品："+tradeItem.getSkuName()+"，该商品无囤货记录，请先囤货后提货！");
            }
        }else{
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }

    /**
     * 减库存
     *
     * @param tradeItems
     */
    public void subSkuListStock(List<TradeItem> tradeItems, Long wareId, SaleType saleType) {
        updateSkuListSubStock(tradeItems,  wareId, saleType,null);
    }

    /**
     * 减库存
     *
     * @param tradeItems
     */
    public void subSkuListStock(List<TradeItem> tradeItems, Long wareId, SaleType saleType,Trade trade) {
        updateSkuListSubStock(tradeItems, wareId, saleType,trade);
    }

    /**
     * 扣减秒杀商品库存和增加销量
     *
     * @param tradeItems
     */
    public void batchFlashSaleGoodsStockAndSalesVolume(List<TradeItem> tradeItems) {
        tradeItems.forEach(tradeItem -> {
            //扣减秒杀商品库存和增加销量
            FlashSaleGoodsBatchStockAndSalesVolumeRequest request = new FlashSaleGoodsBatchStockAndSalesVolumeRequest();
            request.setId(tradeItem.getFlashSaleGoodsId());
            request.setNum(tradeItem.getNum().intValue());
            flashSaleGoodsSaveProvider.batchStockAndSalesVolume(request);
        });
    }

    /**
     * 增加秒杀商品库存和减少销量
     *
     * @param tradeItems
     */
    public void addFlashSaleGoodsStock(List<TradeItem> tradeItems,String customerId) {
        tradeItems.forEach(tradeItem -> {
            //增加秒杀商品库存同时减少销量
            FlashSaleGoodsBatchStockAndSalesVolumeRequest request = new FlashSaleGoodsBatchStockAndSalesVolumeRequest();
            request.setId(tradeItem.getFlashSaleGoodsId());
            request.setNum(tradeItem.getNum().intValue());
            flashSaleGoodsSaveProvider.subStockAndSalesVolume(request);
            //还redis库存
            FlashSaleGoodsOrderCancelReturnStockRequest returnStockRequest = new FlashSaleGoodsOrderCancelReturnStockRequest();
            returnStockRequest.setCustomerId(customerId);
            returnStockRequest.setFlashSaleGoodsId(tradeItem.getFlashSaleGoodsId());
            returnStockRequest.setFlashSaleGoodsNum(tradeItem.getNum().intValue());
            flashSaleGoodsOrderCancelReturnStockService.flashSaleGoodsOrderCancelReturnStock(returnStockRequest);
        });
    }


    /**
     * 加库存
     *
     * @param tradeItems
     */
    public void addSkuListStock(List<TradeItem> tradeItems, Long wareId,SaleType saleType) {
        updateSkuListAddStock(tradeItems,wareId,saleType,null);
    }

    public void addSkuListStock(List<TradeItem> tradeItems, Long wareId,SaleType saleType,Trade trade) {
        updateSkuListAddStock(tradeItems,wareId,saleType,trade);
    }


    /**
     * 库存变动
     *
     * @param tradeItems 订单项
     */
    private void updateSkuListSubStock(List<TradeItem> tradeItems,Long wareId, SaleType saleType, Trade trade) {
        if (CollectionUtils.isEmpty(tradeItems)) {
            return;
        }
        //合并数量 拆箱会出现多个skuid 在这一步合并去减库存
        dealTradeItemBeforeUpdateStock(tradeItems);
        List<GoodsInfoMinusStockDTO> stockList = new ArrayList<>(tradeItems.size());
        buildSubItems(tradeItems, wareId, saleType, trade, stockList);
        goodsInfoProvider.batchMinusStock(GoodsInfoBatchMinusStockRequest.builder().stockList(stockList).wareId(wareId)
                //线上支付订单 且 不为囤货提货单时，需要更新锁定库存
                .needUpdateLockStock(!tradeService.isPayByOfflineOrIsOPK(trade))
                .build());
        if (Objects.isNull(saleType) || SaleType.WHOLESALE.equals(saleType)) {
            //检查一下库存数量，为0时记录
            //过滤除商品名称是带T的
            List<String> goodsIds = tradeItems.stream()
                    .filter(item -> isErpTGoods(item.getSpuName()))
                    .map(item -> item.getSpuId()).collect(Collectors.toList());
            //筛选除指定spuid中库存容量为空的数据
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                BaseResponse<GoodsWareStockByGoodsIdResponse> byGoodsIdIn =
                        goodsWareStockQueryProvider.findByGoodsIdIn(GoodsWareStockByGoodsIdRequest.builder().goodsIds(goodsIds).build());
                List<GoodsWareStockVO> goodsWareStockVOList = byGoodsIdIn.getContext().getGoodsWareStockVOList();
                //过滤除库存为0的商品数据，记录
                goodsIds = goodsWareStockVOList.stream()
                        .filter(item -> item.getStock().compareTo(BigDecimal.ONE) == 0)
                        .map(item -> item.getGoodsId()).collect(Collectors.toList());
                //存储到商品定时下架表
                soldOutGoodsProvider.batchAdd(SoldOutGoodsAddRequest.builder().goodsIds(goodsIds).build());
            }
        }
        refreshEsAfterUpdateStock(tradeItems, saleType);
    }

    private void refreshEsAfterUpdateStock(List<TradeItem> tradeItems, SaleType saleType) {
        if (Objects.isNull(saleType) || SaleType.WHOLESALE.equals(saleType)) {
            //mq 刷新es
            List<String> skuIds = tradeItems.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(skuIds)) {
                orderProducerService.stockRefreshEs(TradeRefreshInventoryRequest.builder().skuIds(skuIds).build());
            }
        }
    }

    private static void dealTradeItemBeforeUpdateStock(List<TradeItem> tradeItems) {
        tradeItems.forEach(v -> {
            if (Objects.isNull(v.getNum())) {
                throw new SbcRuntimeException("生成订单失败参数错误");
            }
            v.setBNum(BigDecimal.valueOf(v.getNum()));
            if (Objects.nonNull(v.getDivisorFlag())) {
                v.setBNum(v.getDivisorFlag().multiply(BigDecimal.valueOf(v.getNum())));
            }
        });
    }

    private void updateSkuListAddStock(List<TradeItem> tradeItems,Long wareId, SaleType saleType,Trade trade) {
        if (CollectionUtils.isEmpty(tradeItems)) {
            return;
        }
        //合并数量 拆箱会出现多个skuid 在这一步合并去减库存
        dealTradeItemBeforeUpdateStock(tradeItems);
        List<GoodsInfoPlusStockDTO> stockList = new ArrayList<>(tradeItems.size());
        buildAddItems(trade, wareId, saleType, tradeItems, stockList);
        goodsInfoProvider.batchPlusStock(GoodsInfoBatchPlusStockRequest.builder().stockList(stockList).wareId(wareId).build());
        refreshEsAfterUpdateStock(tradeItems, saleType);
    }

    public void buildAddItems(Trade trade, Long wareId, SaleType saleType, List<TradeItem> tradeItems, List<GoodsInfoPlusStockDTO> stockList) {
        Map<String,List<TradeItemSplitRecord>> splitMap = new HashMap<>();
        for(TradeItem tradeItem: tradeItems){
            GoodsInfoPlusStockDTO dto = getGoodsInfoPlusStockDTO(tradeItem);
            dto.setWareId(wareId);
            if(SaleType.BULK.equals(saleType)||SaleType.RETAIL.equals(saleType)) {
                if (trade != null) {
                    TradeState tradeState = trade.getTradeState();
                    //未付款不需要处理
                    //if (!PayState.PAID.equals(tradeState.getPayState())) {
                    List<TradeItemSplitRecord> splitRecords = splitMap.get(trade.getId());
                    if (null == splitRecords) {
                        splitRecords = tradeItemSplitRecordService.findListByTradeNo(trade.getId());
                        if (null == splitRecords) {
                            splitRecords = new ArrayList<>(1);
                        }
                        splitMap.put(tradeItem.getOid(), splitRecords);
                    }
                    if (!CollectionUtils.isEmpty(splitRecords)) {
                        TradeItemSplitRecord tmpChild = splitRecords.stream().filter(s -> s.getChildSkuId().equals(dto.getGoodsInfoId()) && s.getChildWareId().equals(wareId)).findFirst().orElse(null);
                        if (null != tmpChild && dto.getStock().compareTo(tmpChild.getChildOrderNum()) == 0) {
                            GoodsInfoPlusStockDTO wholeDto = getGoodsInfoPlusStockDTO(tmpChild.getExchangeParentNum(), tmpChild.getParentSkuId());
                            //把拆分的主物料加回来
                            goodsInfoProvider.batchPlusStock(GoodsInfoBatchPlusStockRequest.builder().stockList(Arrays.asList(wholeDto)).wareId(tmpChild.getParentWareId()).build());
                            //更新回退表示
                            tradeItemSplitRecordService.updateBackFlagById(tmpChild.getId());
                            //把主物料的换算库存扣掉
                            dto.setStock(dto.getStock().subtract(tmpChild.getExchangeParentNum().multiply(tmpChild.getMainAddStep())));
                        }
                    }
                    //}
                }
            }
            stockList.add(dto);
        }
    }

    private static GoodsInfoPlusStockDTO getGoodsInfoPlusStockDTO(TradeItem tradeItem) {
        GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
        dto.setStock(BigDecimal.valueOf(tradeItem.getNum()));
        dto.setGoodsInfoId(tradeItem.getSkuId());
        return dto;
    }
    private static GoodsInfoPlusStockDTO getGoodsInfoPlusStockDTO(BigDecimal stock,String goodsInfoId) {
        GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
        dto.setStock(stock);
        dto.setGoodsInfoId(goodsInfoId);
        return dto;
    }

    private void buildSubItems(List<TradeItem> tradeItems, Long wareId, SaleType saleType, Trade trade, List<GoodsInfoMinusStockDTO> stockList) {
        for(TradeItem tradeItem: tradeItems){
            GoodsInfoMinusStockDTO dto = getGoodsInfoMinusStockDTO(tradeItem);
            if ((SaleType.BULK.equals(saleType)||SaleType.RETAIL.equals(saleType))&& trade !=null) {
                BaseResponse<GoodsWareStockVO> goodsWareStockVOBaseResponse= goodsWareStockQueryProvider.getWareStockByWareIdAndInfoId(dto.getGoodsInfoId(), wareId);
                GoodsWareStockVO goodsWareStockVO = goodsWareStockVOBaseResponse.getContext();
                if(goodsWareStockVO!=null) {
                    if (dto.getStock().compareTo(goodsWareStockVO.getSelfStock()) > 0) {
                        //子物料不够的库存
                        BigDecimal diffStock = dto.getStock().subtract(goodsWareStockVO.getSelfStock());
                        if (goodsWareStockVO.getMainAddStep() != null) {
                            //要拆的主物料库存向上取整
                            BigDecimal wholesaleAmount = diffStock.divide(goodsWareStockVO.getMainAddStep(), 0, BigDecimal.ROUND_CEILING);
                            TradeItemSplitRecord splitRecord = new TradeItemSplitRecord();
                            splitRecord.setExchangeParentNum(wholesaleAmount);
                            splitRecord.setBackFlag(0);
                            splitRecord.setTradeNo(trade.getId());
                            splitRecord.setChildSkuId(tradeItem.getSkuId());
                            splitRecord.setChildWareId(wareId);
                            splitRecord.setChildStock(goodsWareStockVO.getSelfStock());
                            if (null != goodsWareStockVO.getParentGoodsWareStockId()) {
                                splitRecord.setParentSkuId(goodsWareStockVO.getMainSkuId());
                                splitRecord.setParentWareId(goodsWareStockVO.getMainSkuWareId());
                            } else {
                                continue;
                            }
                            splitRecord.setMainAddStep(goodsWareStockVO.getMainAddStep());
                            splitRecord.setParentStock(goodsWareStockVO.getParentStock());
                            splitRecord.setChildOrderNum(tradeItem.getBNum());
                            tradeItemSplitRecordService.save(splitRecord);
                            //修改主物料的库存
                            GoodsWareStockUpdateRequest request = GoodsWareStockUpdateRequest.builder().goodsInfoId(splitRecord.getParentSkuId()).wareId(splitRecord.getParentWareId()).stock(splitRecord.getExchangeParentNum()).build();
                            goodsWareStockProvider.subStock(request);
                            //订单数=原库存数+拆的主物料数-mi  整箱 10 箱 扣0.5  应该是9箱 散批剩1份
                            dto.setStock(goodsWareStockVO.getSelfStock().subtract(goodsWareStockVO.getMainAddStep().multiply(wholesaleAmount).subtract(diffStock)));
                        }

                        asyncPushDeSkuToErp(tradeItem,trade);
                    }
                }
            }
            stockList.add(dto);
        }
    }


    private void asyncPushDeSkuToErp(TradeItem tradeItem,Trade trade) {
        /*Long wareId = trade.getWareId();
        if(wareId == null || wareId.longValue() == retailWareId.longValue()){
            wareId = 1L;
        }
        String prefix = Constants.ERP_NO_PREFIX.get(wareId);
        item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));*/
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("warehouseId", trade.getWareHouseCode());
        requestMap.put("deSku", tradeItem.getErpSkuNo());
        requestMap.put("packId", tradeItem.getGoodsSubtitle());
        HttpCommonResult result = HttpCommonUtil.post(erpDeSkuUrl, requestMap);
        if (result != null) {
            log.warn("电商推组装单中间表入参[{}]结果[{}][{}]", requestMap, result.getResultData(), result.getResultCode());
        }
    }

    private static GoodsInfoMinusStockDTO getGoodsInfoMinusStockDTO(TradeItem tradeItem) {
        GoodsInfoMinusStockDTO dto = new GoodsInfoMinusStockDTO();
        dto.setStock(tradeItem.getBNum());
        dto.setGoodsInfoId(tradeItem.getSkuId());
        return dto;
    }

    /**
     * 减库存
     *
     * @param tradeItems
     */
    public void subSkuVillagesStock(List<TradeItem> tradeItems, Long wareId) {
        updateSkuVillagesStock(tradeItems, true, wareId);
    }

    /**
     * 加库存
     *
     * @param tradeItems
     */
    public void addSkuVillagesStock(List<TradeItem> tradeItems, Long wareId) {
        updateSkuVillagesStock(tradeItems, false,wareId);
    }

    /**
     * 乡镇件库存变动
     *
     * @param tradeItems 订单项
     * @param subFlag    扣库存标识 true:减库存  false:加库存
     */
    private void updateSkuVillagesStock(List<TradeItem> tradeItems, boolean subFlag, Long wareId) {
        if (CollectionUtils.isEmpty(tradeItems)) {
            return;
        }
        dealTradeItemBeforeUpdateStock(tradeItems);
        if (subFlag) {
            List<GoodsInfoMinusStockDTO> stockList = tradeItems.stream().map(tradeItem -> {
                GoodsInfoMinusStockDTO dto = new GoodsInfoMinusStockDTO();
                dto.setStock(BigDecimal.valueOf(tradeItem.getNum()));
                dto.setGoodsInfoId(tradeItem.getSkuId());
                return dto;
            }).collect(Collectors.toList());
            goodsInfoProvider.batchVillagesMinusStock(GoodsInfoBatchMinusStockRequest.builder().stockList(stockList).wareId(wareId).build());
        } else {
            List<GoodsInfoPlusStockDTO> stockList = tradeItems.stream().map(tradeItem -> {
                GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
                dto.setStock(BigDecimal.valueOf(tradeItem.getNum()));
                dto.setGoodsInfoId(tradeItem.getSkuId());
                return dto;
            }).collect(Collectors.toList());
            goodsInfoProvider.batchVillagesPlusStock(GoodsInfoBatchPlusStockRequest.builder().stockList(stockList).wareId(wareId).build());
        }
    }

    private Boolean isErpTGoods(String goodsName) {
        String substring = goodsName.substring(goodsName.length() - 1, goodsName.length());
        if ("T".equals(substring) || "t".equals(substring)) {
            return true;
        }
        return false;
    }

    public CustomerGetByIdResponse verifyCustomer(String customerId) {
        // 客户信息
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();

        if (customer == null) {
            throw new SbcRuntimeException("K-010014");
        }

        if (customer.getCustomerDetail().getCustomerStatus() == CustomerStatus.DISABLE) {
            throw new SbcRuntimeException("K-010015");
        }
        return customer;
    }
    public CustomerGetByIdResponse verifyCustomerNoThird(String customerId) {
        // 客户信息
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerByIdNoThird(new CustomerGetByIdRequest
                (customerId)).getContext();

        if (customer == null) {
            throw new SbcRuntimeException("K-010014");
        }

        if (customer.getCustomerDetail().getCustomerStatus() == CustomerStatus.DISABLE) {
            throw new SbcRuntimeException("K-010015");
        }
        return customer;
    }

    /**
     * 校验可使用积分
     *
     * @param tradeCommitRequest
     */
    public void verifyPoints(List<Trade> trades, TradeCommitRequest tradeCommitRequest) {
        //查询积分设置
        SystemPointsConfigQueryResponse pointsConfig = systemPointsConfigQueryProvider.querySystemPointsConfig().getContext();
        //商城积分体系未开启
        if (!EnableStatus.ENABLE.equals(pointsConfig.getStatus())) {
            throw new SbcRuntimeException("K-000018");
        }
        //积分抵扣使用起始值
        Long overPointsAvailable = pointsConfig.getOverPointsAvailable();

        String customerId = tradeCommitRequest.getCustomer().getCustomerId();
        //订单使用积分
        Long points = tradeCommitRequest.getPoints();

        //查询会员可用积分
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        //会员积分余额
        Long pointsAvailable = customer.getPointsAvailable();
        //如果是子账号，使用主账号的积分
        if (StringUtils.isNotBlank(customer.getParentCustomerId())){
            CustomerGetByIdResponse parentCustomer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customer.getParentCustomerId())).getContext();
            pointsAvailable=parentCustomer.getPointsAvailable();
        }

        //订单使用积分超出会员可用积分
        if (points.compareTo(pointsAvailable) == 1) {
            throw new SbcRuntimeException("K-050314");
        }

        //会员可用积分未满足积分抵扣使用值
        if (pointsAvailable.compareTo(overPointsAvailable) == -1) {
            throw new SbcRuntimeException("K-050315");
        }

        //订单使用积分超出积分抵扣限额
        //订单总金额
        BigDecimal totalTradePrice = trades.stream().map(t -> t.getTradePrice().getTotalPrice()).reduce(BigDecimal::add).get();
        //积分抵扣限额
        BigDecimal pointWorth = BigDecimal.valueOf(pointsConfig.getPointsWorth());
        BigDecimal maxDeductionRate = pointsConfig.getMaxDeductionRate();
        Long maxPoints = totalTradePrice.multiply(maxDeductionRate).multiply(pointWorth).setScale(0, BigDecimal.ROUND_DOWN).longValue();
        if (points.compareTo(maxPoints) == 1) {
            throw new SbcRuntimeException("K-050316");
        }
    }

    /**
     * 校验可使用积分
     *
     * @param tradeCommitRequest
     */
    public void verifyPointsNewPile(List<NewPileTrade> trades, TradeCommitRequest tradeCommitRequest) {
        //查询积分设置
        SystemPointsConfigQueryResponse pointsConfig = systemPointsConfigQueryProvider.querySystemPointsConfig().getContext();
        //商城积分体系未开启
        if (!EnableStatus.ENABLE.equals(pointsConfig.getStatus())) {
            throw new SbcRuntimeException("K-000018");
        }
        //积分抵扣使用起始值
        Long overPointsAvailable = pointsConfig.getOverPointsAvailable();

        String customerId = tradeCommitRequest.getCustomer().getCustomerId();
        //订单使用积分
        Long points = tradeCommitRequest.getPoints();

        //查询会员可用积分
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        //会员积分余额
        Long pointsAvailable = customer.getPointsAvailable();
        //如果是子账号，使用主账号的积分
        if (StringUtils.isNotBlank(customer.getParentCustomerId())){
            CustomerGetByIdResponse parentCustomer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customer.getParentCustomerId())).getContext();
            pointsAvailable=parentCustomer.getPointsAvailable();
        }

        //订单使用积分超出会员可用积分
        if (points.compareTo(pointsAvailable) == 1) {
            throw new SbcRuntimeException("K-050314");
        }

        //会员可用积分未满足积分抵扣使用值
        if (pointsAvailable.compareTo(overPointsAvailable) == -1) {
            throw new SbcRuntimeException("K-050315");
        }

        //订单使用积分超出积分抵扣限额
        //订单总金额
        BigDecimal totalTradePrice = trades.stream().map(t -> t.getTradePrice().getTotalPrice()).reduce(BigDecimal::add).get();
        //积分抵扣限额
        BigDecimal pointWorth = BigDecimal.valueOf(pointsConfig.getPointsWorth());
        BigDecimal maxDeductionRate = pointsConfig.getMaxDeductionRate();
        Long maxPoints = totalTradePrice.multiply(maxDeductionRate).multiply(pointWorth).setScale(0, BigDecimal.ROUND_DOWN).longValue();
        if (points.compareTo(maxPoints) == 1) {
            throw new SbcRuntimeException("K-050316");
        }
    }

    /**
     * 验证失效的营销信息(目前包括失效的赠品、满系活动、优惠券)
     */
    public void verifyInvalidMarketings(TradeCommitRequest tradeCommitRequest, List<TradeItemGroup> tradeItemGroups, CustomerVO customer) {
        String customerId = tradeCommitRequest.getCustomer().getCustomerId();

        // 1.验证赠品、满系活动
        // 1.1.从订单快照中获取下单时选择的营销、商品信息
//        List<TradeMarketingDTO> tradeMarketings = tradeItemGroups.stream()
//                .flatMap(group -> group.getTradeMarketingList().stream()).collect(Collectors.toList());

        List<TradeMarketingDTO> tradeMarketings = tradeItemGroups.stream()
                .flatMap(group -> {
                    if (CollectionUtils.isNotEmpty(group.getTradeMarketingList()))
                    return group.getTradeMarketingList().stream();
                    else {
                        group.setTradeMarketingList(new ArrayList<>());
                    }
                    return group.getTradeMarketingList().stream();
                }).collect(Collectors.toList());

        List<TradeItem> tradeItems = tradeItemGroups.stream()
                .flatMap(group -> group.getTradeItems().stream()).collect(Collectors.toList());

        // 1.2.验证失效赠品、满系活动
        String validInfo = this.verifyTradeMargeting(
                tradeMarketings, Collections.emptyList(), tradeItems, customerId, tradeCommitRequest.getWareId());

        // 1.3.将失效内容更新至request中的订单快照
        List<Long> tradeMarketingIds = tradeMarketings.stream()
                .map(TradeMarketingDTO::getMarketingId).collect(Collectors.toList());
        tradeItemGroups.stream().forEach(group -> {
            group.setTradeMarketingList(
                    group.getTradeMarketingList().stream()
                            .filter(item -> tradeMarketingIds.contains(item.getMarketingId()))
                            .collect(Collectors.toList())
            );
        });

        // 2.验证优惠券
        List<String> couponCodeIds = new ArrayList<>();
        tradeCommitRequest.getStoreCommitInfoList().forEach(item -> {
            if (Objects.nonNull(item.getCouponCodeId())) {
                couponCodeIds.add(item.getCouponCodeId());
            }
        });
        if (Objects.nonNull(tradeCommitRequest.getCommonCodeId())) {
            couponCodeIds.add(tradeCommitRequest.getCommonCodeId());
        }

        // 2.1.查询我的未使用优惠券
        List<CouponCodeDTO> couponCodes = couponCodeQueryProvider.listCouponCodeByCondition(
                CouponCodeQueryRequest.builder().customerId(StringUtils.isNotBlank(customer.getParentCustomerId())?customer.getParentCustomerId():customerId).useStatus(DefaultFlag.NO).delFlag(DeleteFlag.NO).build()
        ).getContext().getCouponCodeList();

        couponCodes = couponCodes.stream()
                .filter(item -> couponCodeIds.contains(item.getCouponCodeId())).collect(Collectors.toList());

        // 2.2.判断所传优惠券是否为我的未使用优惠券
        if (couponCodeIds.size() != couponCodes.size()) {
            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID);
        }

        // 2.3.查看过期优惠券
        couponCodes = couponCodes.stream().filter(couponCode ->
                LocalDateTime.now().isAfter(couponCode.getEndTime())
        ).collect(Collectors.toList());

        // 2.4.针对过期的优惠券进行提示
        if (couponCodes.size() > 0) {
            List<CouponInfoVO> couponInfos = couponInfoQueryProvider.queryCouponInfos(CouponInfoQueryRequest.builder()
                    .couponIds(couponCodes.stream().map(CouponCodeDTO::getCouponId).collect(Collectors.toList())).build()
            ).getContext().getCouponCodeList();
            List<String> couponValidInfos = couponInfos.stream().map(couponInfo -> {
                StringBuilder sb = new StringBuilder();
                if (FullBuyType.NO_THRESHOLD.equals(couponInfo.getFullBuyType())) {
                    sb.append("无门槛减").append(couponInfo.getDenomination().setScale(0));
                } else {
                    sb.append("满").append(couponInfo.getFullBuyPrice().setScale(0))
                            .append("减").append(couponInfo.getDenomination().setScale(0));
                }
                return sb.toString();
            }).collect(Collectors.toList());

            validInfo = validInfo + StringUtils.join(couponValidInfos, "、") + "优惠券已失效！";
        }

        // 2.5.从request对象中移除过期的优惠券
        List<String> invalidCodeIds = couponCodes.stream().map(CouponCodeDTO::getCouponCodeId).collect(Collectors.toList());
        if (invalidCodeIds.contains(tradeCommitRequest.getCommonCodeId())) {
            tradeCommitRequest.setCommonCodeId(null);
        }
        tradeCommitRequest.getStoreCommitInfoList().forEach(item -> {
            if (invalidCodeIds.contains(item.getCouponCodeId())) {
                item.setCouponCodeId(null);
            }
        });

        // 2.6.如果存在提示信息、且为非强制提交，则抛出异常
        if (StringUtils.isNotEmpty(validInfo) && !tradeCommitRequest.isForceCommit()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "很抱歉，" + validInfo);
        }

    }



    /**
     * 验证失效的营销信息(目前包括失效的赠品、满系活动、优惠券)
     */
    public void verifyInvalidMarketingsNewPile(TradeCommitRequest tradeCommitRequest, List<TradeItemGroup> tradeItemGroups, CustomerVO customer) {
        String customerId = tradeCommitRequest.getCustomer().getCustomerId();

        // 1.验证赠品、满系活动
        // 1.1.从订单快照中获取下单时选择的营销、商品信息
        List<TradeMarketingDTO> tradeMarketings = tradeItemGroups.stream()
                .flatMap(group -> group.getTradeMarketingList().stream()).collect(Collectors.toList());
        List<TradeItem> tradeItems = tradeItemGroups.stream()
                .flatMap(group -> group.getTradeItems().stream()).collect(Collectors.toList());

        // 1.2.验证失效赠品、满系活动
        String validInfo = this.verifyTradeMargetingByCache(
                tradeMarketings, Collections.emptyList(), tradeItems, customerId, tradeCommitRequest.getWareId());

        // 1.3.将失效内容更新至request中的订单快照
        List<Long> tradeMarketingIds = tradeMarketings.stream()
                .map(TradeMarketingDTO::getMarketingId).collect(Collectors.toList());
        tradeItemGroups.stream().forEach(group -> {
            group.setTradeMarketingList(
                    group.getTradeMarketingList().stream()
                            .filter(item -> tradeMarketingIds.contains(item.getMarketingId()))
                            .collect(Collectors.toList())
            );
        });

        // 2.验证优惠券
        List<String> couponCodeIds = new ArrayList<>();
        tradeCommitRequest.getStoreCommitInfoList().forEach(item -> {
            if (Objects.nonNull(item.getCouponCodeId())) {
                couponCodeIds.add(item.getCouponCodeId());
            }
        });
        if (Objects.nonNull(tradeCommitRequest.getCommonCodeId())) {
            couponCodeIds.add(tradeCommitRequest.getCommonCodeId());
        }

        // 2.1.查询我的未使用优惠券
        List<CouponCodeDTO> couponCodes = couponCodeQueryProvider.listCouponCodeByCondition(
                CouponCodeQueryRequest.builder().customerId(StringUtils.isNotBlank(customer.getParentCustomerId())?customer.getParentCustomerId():customerId).useStatus(DefaultFlag.NO).delFlag(DeleteFlag.NO).build()
        ).getContext().getCouponCodeList();

        couponCodes = couponCodes.stream()
                .filter(item -> couponCodeIds.contains(item.getCouponCodeId())).collect(Collectors.toList());

        // 2.2.判断所传优惠券是否为我的未使用优惠券
        if (couponCodeIds.size() != couponCodes.size()) {
            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID);
        }

        // 2.3.查看过期优惠券
        couponCodes = couponCodes.stream().filter(couponCode ->
                LocalDateTime.now().isAfter(couponCode.getEndTime())
        ).collect(Collectors.toList());

        // 2.4.针对过期的优惠券进行提示
        if (couponCodes.size() > 0) {
            List<CouponInfoVO> couponInfos = couponInfoQueryProvider.queryCouponInfos(CouponInfoQueryRequest.builder()
                    .couponIds(couponCodes.stream().map(CouponCodeDTO::getCouponId).collect(Collectors.toList())).build()
            ).getContext().getCouponCodeList();
            List<String> couponValidInfos = couponInfos.stream().map(couponInfo -> {
                StringBuilder sb = new StringBuilder();
                if (FullBuyType.NO_THRESHOLD.equals(couponInfo.getFullBuyType())) {
                    sb.append("无门槛减").append(couponInfo.getDenomination().setScale(0));
                } else {
                    sb.append("满").append(couponInfo.getFullBuyPrice().setScale(0))
                            .append("减").append(couponInfo.getDenomination().setScale(0));
                }
                return sb.toString();
            }).collect(Collectors.toList());

            validInfo = validInfo + StringUtils.join(couponValidInfos, "、") + "优惠券已失效！";
        }

        // 2.5.从request对象中移除过期的优惠券
        List<String> invalidCodeIds = couponCodes.stream().map(CouponCodeDTO::getCouponCodeId).collect(Collectors.toList());
        if (invalidCodeIds.contains(tradeCommitRequest.getCommonCodeId())) {
            tradeCommitRequest.setCommonCodeId(null);
        }
        tradeCommitRequest.getStoreCommitInfoList().forEach(item -> {
            if (invalidCodeIds.contains(item.getCouponCodeId())) {
                item.setCouponCodeId(null);
            }
        });

        // 2.6.如果存在提示信息、且为非强制提交，则抛出异常
        if (StringUtils.isNotEmpty(validInfo) && !tradeCommitRequest.isForceCommit()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "很抱歉，" + validInfo);
        }

    }



    /**
     * 带客下单校验customer跟supplier的关系
     *
     * @param customerId customerId
     * @param companyId  companyId
     */
//    public void verifyCustomerWithSupplier(String customerId, Long companyId) {
//        StoreCustomerRela storeCustomerRela = storeCustomerService.findCustomerRelatedForAll(customerId, companyId);
//
//        if (Objects.isNull(storeCustomerRela)) {
//            throw new SbcRuntimeException("K-010201");
//        }
//    }
    public void verifyCustomerWithSupplier(String customerId, Long companyId) {
        StoreCustomerRelaQueryRequest request = new StoreCustomerRelaQueryRequest();
        request.setCustomerId(customerId);
        request.setCompanyInfoId(companyId);
        request.setQueryPlatform(false);

        StoreCustomerRelaResponse storeCustomerRela = storeCustomerQueryProvider.getCustomerRelated(request).getContext();

        if (Objects.isNull(storeCustomerRela)) {
            throw new SbcRuntimeException("K-010201");
        }
    }

    /**
     * 营销活动校验（通过抛出异常返回结果）
     */
    public void verifyTradeMargeting(List<TradeMarketingDTO> tradeMarketingList, List<TradeItem> oldGifts, List<TradeItem> tradeItems,
                                     String customerId, boolean isFoceCommit, Long wareId) {
        String validInfo = this.verifyTradeMargeting(tradeMarketingList, oldGifts, tradeItems, customerId,wareId);
        if (StringUtils.isNotEmpty(validInfo) && !isFoceCommit) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "很抱歉，" + validInfo);
        }
    }

    /**
     * 营销活动校验（通过字符串方式返回结果）
     *
     * @param tradeMarketingList
     * @param oldGifts           旧订单赠品数据，用于编辑订单的场景，由于旧订单赠品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param tradeItems
     * @param customerId
     */
    public String verifyTradeMargeting(List<TradeMarketingDTO> tradeMarketingList, List<TradeItem> oldGifts,
                                       List<TradeItem> tradeItems, String customerId,Long wareId) {
        //校验营销活动
        List<MarketingVO> invalidMarketings = verifyMarketing(tradeMarketingList, customerId,tradeItems);
        List<TradeMarketingDTO> tpMarketingList = new ArrayList<>();
        if (!invalidMarketings.isEmpty()) {
            List<Long> invalidIds = invalidMarketings.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList());
            tpMarketingList = tradeMarketingList.stream().filter(i -> invalidIds.contains(i.getMarketingId()))
                    .collect(Collectors.toList());
            tradeMarketingList.removeAll(tpMarketingList);
        }

        //校验无效赠品
        List<String> giftIds = tradeMarketingList.stream().filter(param->CollectionUtils.isNotEmpty(param.getGiftSkuIds()))
                .flatMap(r -> r.getGiftSkuIds().stream()).distinct()
                .collect(Collectors.toList());
        List<GoodsInfoVO> invalidGifts = new ArrayList<>();
        if (!giftIds.isEmpty()) {
            GoodsInfoViewByIdsResponse gifts = getGoodsResponseLimitWareId(giftIds,wareId);
            //与赠品相同的商品列表
            List<TradeItem> sameItems = tradeItems.stream().filter(i -> giftIds.contains(i.getSkuId())).collect(Collectors.toList());
            invalidGifts = verifyGiftSku(giftIds, sameItems, gifts, oldGifts,tradeMarketingList);
            if (!invalidGifts.isEmpty()) {
                final List<String> tpGiftList = invalidGifts.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                tradeMarketingList.forEach(
                        i -> {
                            List<String> ids = i.getGiftSkuIds().stream().filter(tpGiftList::contains).collect(Collectors.toList());
                            i.getGiftSkuIds().removeAll(ids);
                        }
                );
            }
        }

        String info = "";
        if (!invalidGifts.isEmpty()) {
            //无效赠品提示
            String tmp = "赠品%s";
            List<String> skuInfo = new ArrayList<>();
            invalidGifts.forEach(
                    i -> skuInfo.add(String.format(tmp, i.getGoodsInfoName() + (i.getSpecText() == null ? "" : i.getSpecText())))
            );
            info = info + StringUtils.join(skuInfo, "、") + "已失效或缺货或已赠完！";
        }
        if (!tpMarketingList.isEmpty()) {
            //无效营销活动提示
            info = info + wraperInvalidMarketingInfo(tpMarketingList, invalidMarketings);
        }

        return info;
    }



    /**
     * 营销活动校验（通过字符串方式返回结果）缓存级
     *
     * @param tradeMarketingList
     * @param oldGifts           旧订单赠品数据，用于编辑订单的场景，由于旧订单赠品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param tradeItems
     * @param customerId
     */
    public String verifyTradeMargetingByCache(List<TradeMarketingDTO> tradeMarketingList, List<TradeItem> oldGifts,
                                       List<TradeItem> tradeItems, String customerId,Long wareId) {
        //校验营销活动
        List<MarketingVO> invalidMarketings = verifyMarketingAndCahe(tradeMarketingList, customerId,tradeItems);
        List<TradeMarketingDTO> tpMarketingList = new ArrayList<>();
        if (!invalidMarketings.isEmpty()) {
            List<Long> invalidIds = invalidMarketings.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList());
            tpMarketingList = tradeMarketingList.stream().filter(i -> invalidIds.contains(i.getMarketingId()))
                    .collect(Collectors.toList());
            tradeMarketingList.removeAll(tpMarketingList);
        }

        //校验无效赠品
        List<String> giftIds = tradeMarketingList.stream().filter(param->CollectionUtils.isNotEmpty(param.getGiftSkuIds()))
                .flatMap(r -> r.getGiftSkuIds().stream()).distinct()
                .collect(Collectors.toList());
        List<GoodsInfoVO> invalidGifts = new ArrayList<>();
        if (!giftIds.isEmpty()) {
            GoodsInfoViewByIdsResponse gifts = getGoodsResponseLimitWareId(giftIds,wareId);
            //与赠品相同的商品列表
            List<TradeItem> sameItems = tradeItems.stream().filter(i -> giftIds.contains(i.getSkuId())).collect(Collectors.toList());
            invalidGifts = verifyGiftSku(giftIds, sameItems, gifts, oldGifts,tradeMarketingList);
            if (!invalidGifts.isEmpty()) {
                final List<String> tpGiftList = invalidGifts.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                tradeMarketingList.forEach(
                        i -> {
                            List<String> ids = i.getGiftSkuIds().stream().filter(tpGiftList::contains).collect(Collectors.toList());
                            i.getGiftSkuIds().removeAll(ids);
                        }
                );
            }
        }

        String info = "";
        if (!invalidGifts.isEmpty()) {
            //无效赠品提示
            String tmp = "赠品%s";
            List<String> skuInfo = new ArrayList<>();
            invalidGifts.forEach(
                    i -> skuInfo.add(String.format(tmp, i.getGoodsInfoName() + (i.getSpecText() == null ? "" : i.getSpecText())))
            );
            info = info + StringUtils.join(skuInfo, "、") + "已失效或缺货或已赠完！";
        }
        if (!tpMarketingList.isEmpty()) {
            //无效营销活动提示
            info = info + wraperInvalidMarketingInfo(tpMarketingList, invalidMarketings);
        }

        return info;
    }


    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息
     */
    public GoodsInfoViewByIdsResponse getGoodsResponse(List<String> skuIds,Long wareId) {
        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(skuIds)
                .isHavSpecText(Constants.yes)
                .wareId(wareId)
                .build();

        return goodsInfoQueryProvider.listViewByIds(goodsInfoRequest).getContext();
    }

    /**
     * 获取订单商品详情,不包含区间价，会员级别价信息(不累加库存)
     */
    public GoodsInfoViewByIdsResponse getGoodsResponseLimitWareId(List<String> skuIds,Long wareId) {
        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(skuIds)
                .isHavSpecText(Constants.yes)
                .wareId(wareId)
                .build();

        return goodsInfoQueryProvider.listViewByIdsLimitWareId(goodsInfoRequest).getContext();
    }

    /**
     * 订单营销信息校验，返回失效的营销活动
     *
     * @param tradeMarketingList 订单营销信息
     */
    private List<MarketingVO> verifyMarketing(List<TradeMarketingDTO> tradeMarketingList, String customerId,List<TradeItem> tradeItems) {
        if (CollectionUtils.isEmpty(tradeMarketingList)) {
            return Collections.emptyList();
        }
        //用于存放无效的营销活动
        List<MarketingVO> invalidIds = new ArrayList<>();
        //限购的所有营销
//        List<MarketingVO> invalidIdsPurchaseNum = new ArrayList<>();
        //限购所有的有效营销
//        List<MarketingVO> invalidIdsPurchaseNum2 = new ArrayList<>();
        //获取商户设置的营销活动信息
        List<Long> marketingIds = tradeMarketingList.stream().map(TradeMarketingDTO::getMarketingId).distinct()
                .collect(Collectors.toList());
        MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
        marketingQueryByIdsRequest.setMarketingIds(marketingIds);
        List<MarketingVO> marketings = marketingQueryProvider.queryByIds(marketingQueryByIdsRequest).getContext().getMarketingVOList();
        if (marketingIds.isEmpty()) {
            throw new SbcRuntimeException("K-000001");
        }
        //请求信息根据营销活动分组
        Map<Long, List<TradeMarketingDTO>> marketingGroup = tradeMarketingList.stream().collect(Collectors.groupingBy
                (TradeMarketingDTO::getMarketingId));
        Map<Long, List<String>> skuGroup = marketingGroup.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                i -> i.getValue().stream().flatMap(m -> m.getSkuIds().stream()).collect(Collectors.toList())));
        //获取用户在商户店铺下的等级信息
        /*StoreCustomerRequest customerRequest = new StoreCustomerRequest();
        customerRequest.setStoreIds(marketings.stream().mapToLong(Marketing::getStoreId).distinct().boxed().collect(Collectors.toList()));
        customerRequest.setCustomerId(customerId);
        List<StoreCustomerRela> relax = storeCustomerRepository.findAll(customerRequest.getWhereCriteria());
        Map<Long, Long> levelMap = relax.stream().collect(Collectors.toMap(StoreCustomerRela::getStoreId, StoreCustomerRela::getCustomerLevelId));*/

        CustomerLevelMapByCustomerIdAndStoreIdsRequest customerRequest = new CustomerLevelMapByCustomerIdAndStoreIdsRequest();
        customerRequest.setStoreIds(marketings.stream().mapToLong(MarketingVO::getStoreId).distinct().boxed().collect(Collectors.toList()));
        customerRequest.setCustomerId(customerId);

        Map<Long, CommonLevelVO> commonLevelVOMap = customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds(customerRequest).getContext().getCommonLevelVOMap();
        Map<Long, Long> levelMap = new HashMap<>();
        commonLevelVOMap.forEach((storeId, commonLevelVO) -> levelMap.put(storeId, commonLevelVO.getLevelId()));

//        List<String> skuIds = tradeItems.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        //根据sku集合查询所有囤货
//        List<PilePurchaseAction> pilePurchases = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(skuIds);
        marketings.forEach(i -> {
            //校验营销活动
            if (i.getIsPause() == BoolFlag.YES || i.getDelFlag() == DeleteFlag.YES || i.getBeginTime().isAfter(LocalDateTime.now())
                    || i.getEndTime().isBefore(LocalDateTime.now())) {
                invalidIds.add(i);
            } else {
                //校验关联商品是否匹配
                List<String> scopeList = marketingScopeQueryProvider.listByMarketingId(new MarketingScopeByMarketingIdRequest(i.getMarketingId(),null)).getContext().getMarketingScopeVOList().stream().map(
                        MarketingScopeVO::getScopeId).collect(Collectors.toList());
                List<String> skuList = skuGroup.get(i.getMarketingId());
                if (skuList.stream().anyMatch(s -> {
                    if(scopeList.size() == 1){
                        return !scopeList.contains("all")&&!scopeList.contains(s);
                    }
                    return !scopeList.contains(s);
                })) {
                    //营销活动创建后不可更改，如果关联商品与后台设置不匹配，基本是安全问题造成
                    throw new SbcRuntimeException("K-000001");
                }
            }
            //校验用户级别
            Long level = levelMap.get(i.getStoreId());
            switch (i.getMarketingJoinLevel()) {
                case ALL_CUSTOMER:
                    break;
                case ALL_LEVEL:
                    if (level == null) {
                        invalidIds.add(i);
                    }
                    break;
                case LEVEL_LIST:
                    if (!i.getJoinLevelList().contains(level)) {
                        invalidIds.add(i);
                    }
                    break;
                default:
                    break;
            }

            //校验每个商品的限购数量
//            List<MarketingScopeVO> marketingScopeList = i.getMarketingScopeList();
//            marketingScopeList.forEach(marketingScopeVO -> {
//                Long purchaseNum = marketingScopeVO.getPurchaseNum();
//                if(Objects.nonNull(purchaseNum)){
//                    String scopeId = marketingScopeVO.getScopeId();
//                    for (TradeItem tradeItem : tradeItems) {
//                        //当营销活动关联的sku和本次购买的sku相等的时候,查询是否已经囤了指定的商品数量
//                        if(scopeId.equals(tradeItem.getSkuId())){
//                            Long sum = tradeItem.getNum();
//                                //营销失效的商品
//                                if(sum > purchaseNum || purchaseNum <= 0){
//                                    if(!invalidIdsPurchaseNum.contains(i)){
//                                        //失效的营销sku
//                                        invalidIdsPurchaseNum.add(i);
//                                    }
//                                }else {
//                                    //营销有效的商品,只要有一个商品能够使用这个营销,就不能去掉
//                                    if(!invalidIdsPurchaseNum2.contains(i)){
//                                        invalidIdsPurchaseNum2.add(i);
//                                    }
//                                }
////                            }
//                        }
//                    }
//                }
//            });
        });
        log.info(" ====================================== 所有失效的营销信息 invalidIds:{}",invalidIds.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
//        log.info(" ====================================== 限购营销信息 ids:{}",invalidIdsPurchaseNum.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
//        //有限购失效的营销信息
        /**if(CollectionUtils.isNotEmpty(invalidIdsPurchaseNum)){
            if(CollectionUtils.isNotEmpty(invalidIdsPurchaseNum2)){
                log.info(" ====================================== 限购有用的营销商品 ids:{}",invalidIdsPurchaseNum2.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
                invalidIdsPurchaseNum.removeAll(invalidIdsPurchaseNum2);
            }
            if(CollectionUtils.isNotEmpty(invalidIdsPurchaseNum)){
                invalidIds.addAll(invalidIdsPurchaseNum);
            }
            log.info(" ====================================== 所有失效的营销信息加上限购营销信息后 invalidIds:{}",invalidIds.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
        }*/
        return invalidIds;
    }


    /**
     * 订单营销信息校验，返回失效的营销活动
     *
     * @param tradeMarketingList 订单营销信息
     */
    private List<MarketingVO> verifyMarketingAndCahe(List<TradeMarketingDTO> tradeMarketingList, String customerId,List<TradeItem> tradeItems) {
        if (CollectionUtils.isEmpty(tradeMarketingList)) {
            return Collections.emptyList();
        }
        //用于存放无效的营销活动
        List<MarketingVO> invalidIds = new ArrayList<>();
        //获取商户设置的营销活动信息
        List<Long> marketingIds = tradeMarketingList.stream().map(TradeMarketingDTO::getMarketingId).distinct()
                .collect(Collectors.toList());
        MarketingQueryByIdsRequest marketingQueryByIdsRequest = new MarketingQueryByIdsRequest();
        marketingQueryByIdsRequest.setMarketingIds(marketingIds);
        List<MarketingVO> marketings = marketingQueryProvider.queryByIds(marketingQueryByIdsRequest).getContext().getMarketingVOList();
        if (marketingIds.isEmpty()) {
            throw new SbcRuntimeException("K-000001");
        }
        //请求信息根据营销活动分组
        Map<Long, List<TradeMarketingDTO>> marketingGroup = tradeMarketingList.stream().collect(Collectors.groupingBy
                (TradeMarketingDTO::getMarketingId));
        Map<Long, List<String>> skuGroup = marketingGroup.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                i -> i.getValue().stream().flatMap(m -> m.getSkuIds().stream()).collect(Collectors.toList())));
        //获取用户在商户店铺下的等级信息
        /*StoreCustomerRequest customerRequest = new StoreCustomerRequest();
        customerRequest.setStoreIds(marketings.stream().mapToLong(Marketing::getStoreId).distinct().boxed().collect(Collectors.toList()));
        customerRequest.setCustomerId(customerId);
        List<StoreCustomerRela> relax = storeCustomerRepository.findAll(customerRequest.getWhereCriteria());
        Map<Long, Long> levelMap = relax.stream().collect(Collectors.toMap(StoreCustomerRela::getStoreId, StoreCustomerRela::getCustomerLevelId));*/

        CustomerLevelMapByCustomerIdAndStoreIdsRequest customerRequest = new CustomerLevelMapByCustomerIdAndStoreIdsRequest();
        customerRequest.setStoreIds(marketings.stream().mapToLong(MarketingVO::getStoreId).distinct().boxed().collect(Collectors.toList()));
        customerRequest.setCustomerId(customerId);

        Map<Long, CommonLevelVO> commonLevelVOMap = customerLevelQueryProvider.listCustomerLevelMapByCustomerIdAndIds(customerRequest).getContext().getCommonLevelVOMap();
        Map<Long, Long> levelMap = new HashMap<>();
        commonLevelVOMap.forEach((storeId, commonLevelVO) -> levelMap.put(storeId, commonLevelVO.getLevelId()));

//        List<String> skuIds = tradeItems.stream().map(TradeItem::getSkuId).collect(Collectors.toList());
        //根据sku集合查询所有囤货
//        List<PilePurchaseAction> pilePurchases = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(skuIds);
        marketings.forEach(i -> {
            //校验营销活动
            if (i.getIsPause() == BoolFlag.YES || i.getDelFlag() == DeleteFlag.YES || i.getBeginTime().isAfter(LocalDateTime.now())
                    || i.getEndTime().isBefore(LocalDateTime.now())) {
                invalidIds.add(i);
            } else {
                //校验关联商品是否匹配
                List<String> scopeList = marketingScopeQueryProvider.listByMarketingId(new MarketingScopeByMarketingIdRequest(i.getMarketingId(),null)).getContext().getMarketingScopeVOList().stream().map(
                        MarketingScopeVO::getScopeId).collect(Collectors.toList());
                List<String> skuList = skuGroup.get(i.getMarketingId());
                if (skuList.stream().anyMatch(s -> {
                    if(scopeList.size() == 1){
                        return !scopeList.contains("all")&&!scopeList.contains(s);
                    }
                    return !scopeList.contains(s);
                })) {
                    //营销活动创建后不可更改，如果关联商品与后台设置不匹配，基本是安全问题造成
                    throw new SbcRuntimeException("K-000001");
                }
            }
            //校验用户级别
            Long level = levelMap.get(i.getStoreId());
            switch (i.getMarketingJoinLevel()) {
                case ALL_CUSTOMER:
                    break;
                case ALL_LEVEL:
                    if (level == null) {
                        invalidIds.add(i);
                    }
                    break;
                case LEVEL_LIST:
                    if (!i.getJoinLevelList().contains(level)) {
                        invalidIds.add(i);
                    }
                    break;
                default:
                    break;
            }

            //校验每个商品的限购数量
            /**List<MarketingScopeVO> marketingScopeList = i.getMarketingScopeList();
             marketingScopeList.forEach(marketingScopeVO -> {
             Long purchaseNum = marketingScopeVO.getPurchaseNum();
             if(Objects.nonNull(purchaseNum)){
             String scopeId = marketingScopeVO.getScopeId();
             for (TradeItem tradeItem : tradeItems) {
             //当营销活动关联的sku和本次购买的sku相等的时候,查询是否已经囤了指定的商品数量
             if(scopeId.equals(tradeItem.getSkuId())){
             //关于这个商品所有的囤货记录,判断是否大于一百,大于囤货数量
             //                            List<PilePurchaseAction> purchases = pilePurchases.stream().filter(pilePurchase -> pilePurchase.getGoodsInfoId().equals(tradeItem.getSkuId())).collect(Collectors.toList());
             //                            if(CollectionUtils.isNotEmpty(purchases)){
             //                                long sum = purchases.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
             //                                sum = sum + tradeItem.getNum();
             Long sum = tradeItem.getNum();
             //营销失效的商品
             if(sum > purchaseNum || purchaseNum <= 0){
             if(!invalidIdsPurchaseNum.contains(i)){
             //失效的营销sku
             invalidIdsPurchaseNum.add(i);
             }
             }else {
             //营销有效的商品,只要有一个商品能够使用这个营销,就不能去掉
             if(!invalidIdsPurchaseNum2.contains(i)){
             invalidIdsPurchaseNum2.add(i);
             }
             }
             //                            }
             }
             }
             }
             });*/
        });
        log.info(" ====================================== 所有失效的营销信息 invalidIds:{}",invalidIds.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
//        log.info(" ====================================== 限购营销信息 ids:{}",invalidIdsPurchaseNum.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
//        //有限购失效的营销信息
        /**if(CollectionUtils.isNotEmpty(invalidIdsPurchaseNum)){
         if(CollectionUtils.isNotEmpty(invalidIdsPurchaseNum2)){
         log.info(" ====================================== 限购有用的营销商品 ids:{}",invalidIdsPurchaseNum2.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
         invalidIdsPurchaseNum.removeAll(invalidIdsPurchaseNum2);
         }
         if(CollectionUtils.isNotEmpty(invalidIdsPurchaseNum)){
         invalidIds.addAll(invalidIdsPurchaseNum);
         }
         log.info(" ====================================== 所有失效的营销信息加上限购营销信息后 invalidIds:{}",invalidIds.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList()));
         }*/
        return invalidIds;
    }

    /**
     * 校验营销赠品是否有效，返回无效赠品信息
     *
     * @param giftIds           赠品id集合
     * @param sameItems         与赠品有重复的商品列表
     * @param goodsInfoResponse 赠品基础信息
     * @param oldGifts          旧订单赠品数据，用于编辑订单的场景，由于旧订单赠品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @return
     */
    private List<GoodsInfoVO> verifyGiftSku(List<String> giftIds, List<TradeItem> sameItems,
                                            GoodsInfoViewByIdsResponse goodsInfoResponse, List<TradeItem> oldGifts,List<TradeMarketingDTO> tradeMarketingList) {
        List<GoodsInfoVO> result = new ArrayList<>();
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getGoodsInfos();
        Map<String, GoodsVO> goodsMap = goodsInfoResponse.getGoodses().stream().collect(Collectors.toMap
                (GoodsVO::getGoodsId, Function.identity()));
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap
                (GoodsInfoVO::getGoodsInfoId, Function.identity()));
        Map<String, List<TradeItem>> sameItemMap = sameItems.stream().collect(Collectors.groupingBy(TradeItem::getSkuId));
        Map<String, Long> oldGiftMap = oldGifts.stream().collect(Collectors.toMap(TradeItem::getSkuId, TradeItem::getNum));
        giftIds.forEach(id -> {
            GoodsInfoVO goodsInfo = goodsInfoMap.get(id);
            GoodsVO goods = goodsMap.get(goodsInfo.getGoodsId());
            List<TradeItem> sameList = sameItemMap.get(id);
            Long oldNum = oldGiftMap.getOrDefault(id, 0L);
            long num = 0;
            if (sameList != null) {
                num = sameList.stream().map(TradeItem::getNum).reduce(0L, (a, b) -> a + b);
            }
            //校验赠品库存，删除，上下架状态
            if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                    || goods.getAuditStatus() == CheckStatus.FORBADE) {
                result.add(goodsInfo);
            }
            if (goodsInfo.getStock().add(BigDecimal.valueOf(oldNum)).subtract(BigDecimal.valueOf(num)).compareTo(BigDecimal.ZERO) == 0) {
                result.add(goodsInfo);
            }
        });
//        tradeMarketingList.forEach(marketing->{
//            Long marketingId = marketing.getMarketingId();
//            Long marketingLevelId = marketing.getMarketingLevelId();
//            if (CollectionUtils.isNotEmpty(marketing.getGiftSkuIds())){
//                marketing.getGiftSkuIds().forEach(v->{
//                String key = marketingId.toString()+marketingLevelId.toString()+v;
//                    String o = redisService.getString(key);
//                    if (Objects.nonNull(o)){
//                        Long num = Long.parseLong(o);
//                        if (num.compareTo(0l)<=0){
//                            GoodsInfoVO goodsInfo = goodsInfoMap.get(v);
//                            result.add(goodsInfo);
//                        }
//                    }
//                });
//            }
//        });


        return result.stream().filter(IteratorUtils.distinctByKey(GoodsInfoVO::getGoodsInfoId)).collect(Collectors
                .toList());
    }

    /**
     * 封装并返回无效营销活动描述信息
     *
     * @param tradeMarketingList
     * @param invalidMarketings
     * @return
     */
    private String wraperInvalidMarketingInfo(List<TradeMarketingDTO> tradeMarketingList, List<MarketingVO> invalidMarketings) {
        Map<Long, List<TradeMarketingDTO>> marketingMap = tradeMarketingList.stream().collect(
                Collectors.groupingBy(TradeMarketingDTO::getMarketingId));
        List<String> infoList = new ArrayList<>();
        invalidMarketings.forEach(
                i -> {
                    List<Long> reqLevelList = marketingMap.get(i.getMarketingId()).stream().map(TradeMarketingDTO::getMarketingLevelId)
                            .distinct().collect(Collectors.toList());
                    MarketingSubType subType = i.getSubType();
                    reqLevelList.forEach(
                            l -> {
                                String info = "";
                                DecimalFormat fm = new DecimalFormat("#.##");
                                switch (i.getMarketingType()) {
                                    case REDUCTION:
                                        Map<Long, MarketingFullReductionLevelVO> reductionMap = marketingFullReductionQueryProvider.listByMarketingId
                                                (new MarketingFullReductionByMarketingIdRequest(i.getMarketingId())).getContext().getMarketingFullReductionLevelVOList().stream().collect(Collectors.toMap(MarketingFullReductionLevelVO::
                                                getReductionLevelId, Function.identity()));

                                        MarketingFullReductionLevelVO reductionLevel = reductionMap.get(l);
                                        if (reductionLevel == null) {
                                            throw new SbcRuntimeException("K-050221");
                                        }
                                        info = "满%s减%s活动";
                                        if (subType == MarketingSubType.REDUCTION_FULL_AMOUNT) {
                                            info = String.format(info, fm.format(reductionLevel.getFullAmount()), reductionLevel.getReduction());
                                        } else if (subType == MarketingSubType.REDUCTION_FULL_COUNT) {
                                            info = String.format(info, reductionLevel.getFullCount() + "箱", reductionLevel.getReduction());
                                        }
                                        break;
                                    case DISCOUNT:
                                        Map<Long, MarketingFullDiscountLevelVO> discountMap = marketingFullDiscountQueryProvider.listByMarketingId
                                                (new MarketingFullDiscountByMarketingIdRequest(i.getMarketingId())).getContext().getMarketingFullDiscountLevelVOList().stream().collect(Collectors.toMap(MarketingFullDiscountLevelVO::
                                                getDiscountLevelId, Function.identity()));
                                        MarketingFullDiscountLevelVO discountLevel = discountMap.get(l);
                                        if (discountLevel == null) {
                                            throw new SbcRuntimeException("K-050221");
                                        }
                                        info = "满%s享%s折活动";
                                        BigDecimal discount = discountLevel.getDiscount().multiply(BigDecimal.TEN)
                                                .setScale(1, BigDecimal.ROUND_HALF_UP);
                                        if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT) {
                                            info = String.format(info, fm.format(discountLevel.getFullAmount()), discount);
                                        } else if (subType == MarketingSubType.DISCOUNT_FULL_COUNT) {
                                            info = String.format(info, discountLevel.getFullCount() + "箱", discount);
                                        }
                                        break;
                                    case GIFT:
                                        FullGiftLevelListByMarketingIdRequest fullGiftLevelListByMarketingIdRequest = new FullGiftLevelListByMarketingIdRequest();
                                        fullGiftLevelListByMarketingIdRequest.setMarketingId(i.getMarketingId());
                                        Map<Long, MarketingFullGiftLevelVO> giftMap = fullGiftQueryProvider.listLevelByMarketingId
                                                (fullGiftLevelListByMarketingIdRequest).getContext().getFullGiftLevelVOList().stream().collect(Collectors.toMap(MarketingFullGiftLevelVO::
                                                getGiftLevelId, Function.identity()));
                                        MarketingFullGiftLevelVO giftLevel = giftMap.get(l);
                                        if (giftLevel == null) {
                                            throw new SbcRuntimeException("K-050221");
                                        }
                                        info = "满%s获赠品活动";
                                        if (subType == MarketingSubType.GIFT_FULL_AMOUNT) {
                                            info = String.format(info, fm.format(giftLevel.getFullAmount()));
                                        } else if (subType == MarketingSubType.GIFT_FULL_COUNT) {
                                            info = String.format(info, giftLevel.getFullCount() + "箱");
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                infoList.add(info);
                            }
                    );

                });
        return infoList.isEmpty() ? "" : StringUtils.join(infoList, "、") + "已失效！";
    }

    /**
     * 校验订单开票规则
     *
     * @param invoice  订单开票信息
     * @param supplier 商家店铺信息
     */
    public void verifyInvoice(Invoice invoice, Supplier supplier) {
        InvoiceProjectSwitchByCompanyInfoIdRequest request = new InvoiceProjectSwitchByCompanyInfoIdRequest();
        request.setCompanyInfoId(supplier.getSupplierId());
        BaseResponse<InvoiceProjectSwitchByCompanyInfoIdResponse> baseResponse = invoiceProjectSwitchQueryProvider.getByCompanyInfoId(request);
        InvoiceProjectSwitchByCompanyInfoIdResponse response = baseResponse.getContext();
        log.info("=======================>"+invoice.getType());
        if(Objects.nonNull(invoice.getType())){
            if ((response.getIsSupportInvoice() == DefaultFlag.NO && invoice.getType() != -1) ||
                    (response.getIsPaperInvoice() == DefaultFlag.NO && invoice.getType() == 0) ||
                    (response.getIsValueAddedTaxInvoice() == DefaultFlag.NO && invoice.getType() == 1)) {
                throw new SbcRuntimeException("K-050209", new String[]{supplier.getStoreName()});
            }
        }
    }

    /**
     * 验证乡镇件
     * @param trade
     */
    public Boolean verifyVillages(Trade trade, Long tradeGoodsTotalNum) {
        //暂时只有湖南省内 写死 后续可以用枚举  我也跟着写死加个南昌 你们后续也可以用枚举
        if (Objects.nonNull(trade.getConsignee().getProvinceId())
                && (430000 == trade.getConsignee().getProvinceId() || 360000 == trade.getConsignee().getProvinceId())) {
            //如果四级地址（乡、镇）不为空且不为-1
            if (Objects.nonNull(trade.getConsignee().getTwonId()) && -1 != trade.getConsignee().getTwonId()) {
                Integer count = villagesAddressConfigQueryProvider.getCountByAllId(VillagesAddressConfigQueryRequest.builder()
                        .provinceId(trade.getConsignee().getProvinceId()).cityId(trade.getConsignee().getCityId())
                        .areaId(trade.getConsignee().getAreaId()).villageId(trade.getConsignee().getTwonId()).storeId(trade.getSupplier().getStoreId()).build()).getContext();
                if (Objects.nonNull(count) && count > 0) {
                    if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME) && tradeGoodsTotalNum < 10) {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "乡镇免费店配数量不足10件！");
                    }
                    return true;
                }
            } else {
                //根据省、市、区id获取乡镇件地址信息
                List<VillagesAddressConfigVO> villagesAddressConfigVOList = villagesAddressConfigQueryProvider.list(VillagesAddressConfigQueryRequest.builder()
                        .provinceId(trade.getConsignee().getProvinceId()).cityId(trade.getConsignee().getCityId())
                        .areaId(trade.getConsignee().getAreaId()).storeId(trade.getSupplier().getStoreId()).build()).getContext().getVillagesAddressConfigVOList();
                if (CollectionUtils.isNotEmpty(villagesAddressConfigVOList)) {
                    for (VillagesAddressConfigVO config : villagesAddressConfigVOList) {
                        //如果收货地址街道是手动填写 则用手动填写的街道去比较是否乡镇件
                        if (Objects.nonNull(trade.getConsignee().getTwonName())) {
                            if (trade.getConsignee().getTwonName().contains(config.getVillageName())) {
                                if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME) && tradeGoodsTotalNum < 10) {
                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "乡镇免费店配数量不足10件！");
                                }
                                return true;
                            }
                        } else {
                            if (trade.getConsignee().getDetailAddress().contains(config.getVillageName())) {
                                if (trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME) && tradeGoodsTotalNum < 10) {
                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "乡镇免费店配数量不足10件！");
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 验证失效的营销信息(目前包括失效的赠品、满系活动、优惠券)
     */
    public int verifyInvalidMarketingsByTrades(List<TradeVO> trades) {
        Assert.notEmpty(trades, "订单集合不能为空");
        String customerId = trades.get(0).getBuyer().getId();

        List<TradeItemVO> tradeItems = trades.stream().flatMap(trade -> trade.getTradeItems().stream()).collect(Collectors.toList());
        List<TradeMarketingDTO> tradeMarketings = trades.stream().filter(trade -> Objects.nonNull(trade.getTradeMarketings())).flatMap(trade -> trade.getTradeMarketings().stream())
                .map(item -> KsBeanUtil.convert(item, TradeMarketingDTO.class))
                .collect(Collectors.toList());
        //1 促销活动: 验证失效赠品、满系活动
        boolean pass = this.verifyTradeMargetingReturnErrorCode(
                tradeMarketings, Collections.emptyList(), KsBeanUtil.convert(tradeItems, TradeItem.class), customerId, trades.get(0).getWareId());
        if (!pass) {
            return 1;
        }

        //2 买商品赠券 TODO 2023-6-8

        // 3 使用的优惠券已过期
        List<String> couponCodeIds = trades.stream().filter(trade -> Objects.nonNull(trade.getTradeCoupon()))
                .map(trade -> trade.getTradeCoupon().getCouponCodeId()).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(couponCodeIds)) {
            // 2.1.查询我的已使用优惠券
            List<CouponCodeDTO> couponCodes = couponCodeQueryProvider.listCouponCodeByCondition(
                    CouponCodeQueryRequest.builder().customerId(customerId).useStatus(DefaultFlag.YES).delFlag(DeleteFlag.NO).build()
            ).getContext().getCouponCodeList();

            couponCodes = couponCodes.stream()
                    .filter(item -> couponCodeIds.contains(item.getCouponCodeId())).collect(Collectors.toList());

            // 2.2.判断所传优惠券是否为我的已使用优惠券
            if (couponCodeIds.size() != couponCodes.size()) {
                return 3;
            }

            // 2.3.查看过期优惠券
            couponCodes = couponCodes.stream().filter(couponCode -> LocalDateTime.now().isAfter(couponCode.getEndTime())).collect(Collectors.toList());
            // 2.4.针对过期的优惠券进行提示
            if (couponCodes.size() > 0) {
                return 3;
            }
        }
        return 0;
    }

    /**
     * 营销活动校验（通过字符串方式返回结果）
     *
     * @param tradeMarketingList
     * @param oldGifts           旧订单赠品数据，用于编辑订单的场景，由于旧订单赠品库存已先还回但事务未提交，因此在处理中将库存做逻辑叠加
     * @param tradeItems
     * @param customerId
     */
    public boolean verifyTradeMargetingReturnErrorCode(List<TradeMarketingDTO> tradeMarketingList, List<TradeItem> oldGifts,
                                                   List<TradeItem> tradeItems, String customerId, Long wareId) {
        //校验营销活动
        List<MarketingVO> invalidMarketings = verifyMarketing(tradeMarketingList, customerId,tradeItems);
        List<TradeMarketingDTO> tpMarketingList = new ArrayList<>();
        if (!invalidMarketings.isEmpty()) {
            List<Long> invalidIds = invalidMarketings.stream().map(MarketingVO::getMarketingId).collect(Collectors.toList());
            tpMarketingList = tradeMarketingList.stream().filter(i -> invalidIds.contains(i.getMarketingId()))
                    .collect(Collectors.toList());
            tradeMarketingList.removeAll(tpMarketingList);
        }

        //校验无效赠品
        List<String> giftIds = tradeMarketingList.stream().filter(param->CollectionUtils.isNotEmpty(param.getGiftSkuIds()))
                .flatMap(r -> r.getGiftSkuIds().stream()).distinct()
                .collect(Collectors.toList());
        List<GoodsInfoVO> invalidGifts = new ArrayList<>();
        if (!giftIds.isEmpty()) {
            GoodsInfoViewByIdsResponse gifts = getGoodsResponseLimitWareId(giftIds,wareId);
            //与赠品相同的商品列表
            List<TradeItem> sameItems = tradeItems.stream().filter(i -> giftIds.contains(i.getSkuId())).collect(Collectors.toList());
            invalidGifts = verifyGiftSku(giftIds, sameItems, gifts, oldGifts,tradeMarketingList);
            if (!invalidGifts.isEmpty()) {
                final List<String> tpGiftList = invalidGifts.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                tradeMarketingList.forEach(
                        i -> {
                            List<String> ids = i.getGiftSkuIds().stream().filter(tpGiftList::contains).collect(Collectors.toList());
                            i.getGiftSkuIds().removeAll(ids);
                        }
                );
            }
        }

        if (!invalidGifts.isEmpty() || !tpMarketingList.isEmpty()) {
            //无效赠品提示，无效营销活动提示,检查失败
            return false;
        }
        return true;
    }

    /***
     * @desc  配送到家校验
     * @author shiy  2023/7/4 17:13
    */
    public Integer verifyDeliveryConfig(Trade trade) {
        if (Objects.nonNull(trade.getConsignee().getProvinceId()) && trade.getDeliverWay().equals(DeliverWay.DELIVERY_HOME)) {
            Long tradeGoodsTotalNum = trade.getGoodsTotalNum();
            Long storeId = trade.getSupplier().getStoreId();
            Long wareId = trade.getWareId();
            Long provinceId = trade.getConsignee().getProvinceId();
            Long cityId = trade.getConsignee().getCityId();
            if (DeliverWay.SITE_PICK_SELF == trade.getDeliverWay()) {
                if (!orderCommonService.selfOrder(trade)) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家[" + trade.getSupplier().getSupplierName() + "]不支持站点自提");
                }
                if (provinceId == 420000 && tradeGoodsTotalNum >= 5) {
                    return -1;
                } else {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "湖北区域满5件可用站点自提");
                }
            }
            if (DeliverWay.DELIVERY_HOME == trade.getDeliverWay()) {
                List<FreightTemplateDeliveryAreaVO> freightTemplateDeliveryAreaVOList = freightTemplateDeliveryAreaQueryProvider.queryDeliveryHomeConfifg(FreightTemplateDeliveryAreaListRequest.builder().storeId(storeId).wareId(wareId).build()).getContext();
                if (CollectionUtils.isNotEmpty(freightTemplateDeliveryAreaVOList)) {
                    List<FreightTemplateDeliveryAreaVO> deliveryFreeStoreCfg = freightTemplateDeliveryAreaVOList.
                            stream().filter(f -> f.getDestinationType() == freightTemplateDeliveryType.CONVENTION).collect(Collectors.toList());
                    FreightTemplateDeliveryAreaVO returnVo = veriryFreeStore(provinceId, cityId, deliveryFreeStoreCfg);
                    if (returnVo != null) {
                        if (tradeGoodsTotalNum >= returnVo.getFreightFreeNumber()) {
                            List<FreightTemplateDeliveryAreaVO> deliveryVillagesCfg = freightTemplateDeliveryAreaVOList.
                                    stream().filter(f -> f.getDestinationType() == freightTemplateDeliveryType.AREATENDELIVER).collect(Collectors.toList());
                            returnVo = veriryVillages(trade.getConsignee(), storeId, provinceId, cityId, deliveryVillagesCfg);
                            if (returnVo != null) {
                                if (tradeGoodsTotalNum >= returnVo.getFreightFreeNumber()) {
                                    return freightTemplateDeliveryType.AREATENDELIVER.toValue();
                                } else {
                                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "乡镇免费店配数量不足" + returnVo.getFreightFreeNumber() + "件");
                                }
                            }else {
                                return freightTemplateDeliveryType.CONVENTION.toValue();
                            }
                        } else {
                            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "至少" + returnVo.getFreightFreeNumber() + "件商品才可以使用免费店配");
                        }
                    }
                }else{
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家缺失免费店配规则");
                }
            }
            if (DeliverWay.LOGISTICS == trade.getDeliverWay()) {
                List<FreightTemplateDeliveryAreaVO> freightTemplateDeliveryAreaVOList = freightTemplateDeliveryAreaQueryProvider.queryThirdLogisticsDilivery(FreightTemplateDeliveryAreaListRequest.builder().storeId(storeId).wareId(wareId).build()).getContext();
                List<FreightTemplateDeliveryAreaVO> deliveryThirdLogistics = freightTemplateDeliveryAreaVOList.
                        stream().filter(f -> f.getDestinationType() == freightTemplateDeliveryType.THIRDLOGISTICS).collect(Collectors.toList());
                FreightTemplateDeliveryAreaVO returnVo = veriryThirdLogistic(provinceId, cityId, deliveryThirdLogistics);
                if (returnVo != null) {
                    if (tradeGoodsTotalNum >= returnVo.getFreightFreeNumber()) {
                        return freightTemplateDeliveryType.THIRDLOGISTICS.toValue();
                    } else {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "至少" + returnVo.getFreightFreeNumber() + "件商品才可以使用"+DeliverWay.LOGISTICS.getDesc());
                    }
                }else{
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家缺失"+DeliverWay.LOGISTICS.getDesc()+"规则");
                }
            }
            if (DeliverWay.SPECIFY_LOGISTICS == trade.getDeliverWay()) {
                List<FreightTemplateDeliveryAreaVO> freightTemplateDeliveryAreaVOList = freightTemplateDeliveryAreaQueryProvider.queryThirdLogisticsDilivery(FreightTemplateDeliveryAreaListRequest.builder().storeId(storeId).wareId(wareId).build()).getContext();
                List<FreightTemplateDeliveryAreaVO> deliveryThirdLogistics = freightTemplateDeliveryAreaVOList.
                        stream().filter(f -> f.getDestinationType() == freightTemplateDeliveryType.SPECIFY_LOGISTICS).collect(Collectors.toList());
                FreightTemplateDeliveryAreaVO returnVo = veriryThirdLogistic(provinceId, cityId, deliveryThirdLogistics);
                if (returnVo != null) {
                    if (tradeGoodsTotalNum >= returnVo.getFreightFreeNumber()) {
                        return freightTemplateDeliveryType.SPECIFY_LOGISTICS.toValue();
                    } else {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "至少" + returnVo.getFreightFreeNumber() + "件商品才可以使用"+DeliverWay.SPECIFY_LOGISTICS.getDesc());
                    }
                }else{
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家缺失"+DeliverWay.SPECIFY_LOGISTICS.getDesc()+"规则");
                }
            }
            if(DeliverWay.SITE_PICK_SELF==trade.getDeliverWay()){
                List<FreightTemplateDeliveryAreaVO> freightTemplateDeliveryAreaVOList = freightTemplateDeliveryAreaQueryProvider.queryToDoorPick(FreightTemplateDeliveryAreaListRequest.builder().storeId(storeId).wareId(wareId).build()).getContext();
                if(CollectionUtils.isNotEmpty(freightTemplateDeliveryAreaVOList)){
                    FreightTemplateDeliveryAreaVO returnVo = freightTemplateDeliveryAreaVOList.get(0);
                    if (tradeGoodsTotalNum >= returnVo.getFreightFreeNumber()) {
                        return freightTemplateDeliveryType.TODOORPICK.toValue();
                    }else {
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "统仓统配数量不满足" + returnVo.getFreightFreeNumber() + "件");
                    }
                }else{
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家缺失统仓统配规则");
                }
            }
        }
        return -1;
    }

    public static FreightTemplateDeliveryAreaVO veriryThirdLogistic(Long provinceId, Long CityId, List<FreightTemplateDeliveryAreaVO> deliveryThirdLogistics) {
        if (CollectionUtils.isNotEmpty(deliveryThirdLogistics)) {
            //排序取最小启用数量的
            deliveryThirdLogistics = deliveryThirdLogistics.stream().sorted(Comparator.comparing(FreightTemplateDeliveryAreaVO::getFreightFreeNumber)).collect(Collectors.toList());
            for (FreightTemplateDeliveryAreaVO areaVO : deliveryThirdLogistics) {
                if(true){
                    return areaVO;
                }
                FreightTemplateDeliveryAreaVO areaVO1 = veriryFreightTemplateDeliveryAreaVO12(provinceId, CityId, areaVO);
                if (areaVO1 != null) return areaVO1;
            }
        }
        return null;
    }

    /**
     * @desc  校验省、市
     * @author shiy  2023/9/18 14:59
    */
    public static FreightTemplateDeliveryAreaVO veriryFreightTemplateDeliveryAreaVO12(Long provinceId, Long CityId, FreightTemplateDeliveryAreaVO areaVO) {
        if (Objects.nonNull(areaVO.getDestinationArea())
                && ((provinceId!=null && ArrayUtils.contains(areaVO.getDestinationArea(), provinceId.toString()))
                || (CityId!=null && ArrayUtils.contains(areaVO.getDestinationArea(), CityId.toString())))) {
            return areaVO;
        }
        return null;
    }

    /**
     * @desc  校验区、街道
     * @author shiy  2023/9/18 14:59
    */
    public static FreightTemplateDeliveryAreaVO veriryFreightTemplateDeliveryAreaVO34(Long areaId, Long townId, FreightTemplateDeliveryAreaVO areaVO) {
        if (Objects.nonNull(areaVO.getDestinationArea())
                && ((areaId!=null && ArrayUtils.contains(areaVO.getDestinationArea(), areaId.toString()))
                || (townId!=null && ArrayUtils.contains(areaVO.getDestinationArea(), townId.toString())))) {
            return areaVO;
        }
        return null;
    }


    public FreightTemplateDeliveryAreaVO getFreightTemplateDeliveryAreaVO(freightTemplateDeliveryType deliveryType) {
        FreightTemplateDeliveryAreaVO platDeliveryToStoreCfg_10 = freightTemplateDeliveryAreaQueryProvider.queryByStoreIdAndWareIdAndDestinationType(Constants.BOSS_DEFAULT_STORE_ID,1L,deliveryType.toValue()).getContext();
        return platDeliveryToStoreCfg_10;
    }

    public static FreightTemplateDeliveryAreaVO veriryFreeStore(Long provinceId, Long CityId,List<FreightTemplateDeliveryAreaVO> deliveryFreeStoreCfg) {
        if (CollectionUtils.isNotEmpty(deliveryFreeStoreCfg)) {
            //排序取最小启用数量的
            deliveryFreeStoreCfg = deliveryFreeStoreCfg.stream().sorted(Comparator.comparing(FreightTemplateDeliveryAreaVO::getFreightFreeNumber)).collect(Collectors.toList());
            for (FreightTemplateDeliveryAreaVO areaVO : deliveryFreeStoreCfg) {
                FreightTemplateDeliveryAreaVO areaVO1 = veriryFreightTemplateDeliveryAreaVO12(provinceId, CityId, areaVO);
                if (areaVO1 != null) return areaVO1;
            }
        }
        return null;
    }

    public FreightTemplateDeliveryAreaVO veriryVillages(Consignee consignee,Long storeId, Long provinceId, Long CityId, List<FreightTemplateDeliveryAreaVO> deliveryVillagesCfg) {
        if (CollectionUtils.isNotEmpty(deliveryVillagesCfg)) {
            //排序取最小启用数量的
            deliveryVillagesCfg = deliveryVillagesCfg.stream().sorted(Comparator.comparing(FreightTemplateDeliveryAreaVO::getFreightFreeNumber)).collect(Collectors.toList());
            for (FreightTemplateDeliveryAreaVO areaVO : deliveryVillagesCfg) {
                //如果四级地址（乡、镇）不为空且不为-1 乡镇件
                if (Objects.nonNull(consignee.getTwonId()) && -1 != consignee.getTwonId()) {
                    Integer count = villagesAddressConfigQueryProvider.getCountByAllId(VillagesAddressConfigQueryRequest.builder()
                            .provinceId(provinceId).cityId(CityId)
                            .areaId(consignee.getAreaId()).villageId(consignee.getTwonId()).storeId(storeId).build()).getContext();
                    if (Objects.nonNull(count) && count > 0) {
                        return areaVO;
                    }
                } else {
                    //免费店配
                    List<VillagesAddressConfigVO> villagesAddressConfigVOList = villagesAddressConfigQueryProvider.list(VillagesAddressConfigQueryRequest.builder()
                            .provinceId(provinceId).cityId(CityId)
                            .areaId(consignee.getAreaId()).storeId(storeId).build()).getContext().getVillagesAddressConfigVOList();
                    if (CollectionUtils.isNotEmpty(villagesAddressConfigVOList)) {
                        for (VillagesAddressConfigVO config : villagesAddressConfigVOList) {
                            //如果收货地址街道是手动填写 则用手动填写的街道去比较是否乡镇件
                            if (Objects.nonNull(consignee.getTwonName())) {
                                if (consignee.getTwonName().contains(config.getVillageName())) {
                                    return areaVO;
                                }
                            } else {
                                if (consignee.getDetailAddress().contains(config.getVillageName())) {
                                    return areaVO;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public FreightTemplateDeliveryAreaVO veriryToDoorPick(Long provinceId, Long cityId, List<FreightTemplateDeliveryAreaVO> deliveryThirdLogistics) {
        return null;
    }
}
