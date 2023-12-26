package com.wanmi.sbc.marketing.plugin.impl;

import com.google.common.collect.Maps;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.vo.CouponLabelVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.enums.FullBuyType;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.request.TradeMarketingPluginRequest;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.coupon.model.entity.TradeCoupon;
import com.wanmi.sbc.marketing.coupon.model.entity.cache.CouponCache;
import com.wanmi.sbc.marketing.coupon.model.root.CouponCode;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import com.wanmi.sbc.marketing.coupon.model.root.CouponMarketingScope;
import com.wanmi.sbc.marketing.coupon.model.vo.CouponView;
import com.wanmi.sbc.marketing.coupon.repository.CouponInfoRepository;
import com.wanmi.sbc.marketing.coupon.repository.CouponMarketingScopeRepository;
import com.wanmi.sbc.marketing.coupon.service.CouponCacheService;
import com.wanmi.sbc.marketing.coupon.service.CouponCodeService;
import com.wanmi.sbc.marketing.distribution.service.DistributionCacheService;
import com.wanmi.sbc.marketing.plugin.IGoodsDetailPlugin;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.plugin.ITradeCommitPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 营销满赠插件
 * Created by dyt on 2016/12/8.
 */
@Repository("couponPlugin")
public class CouponPlugin implements IGoodsListPlugin, IGoodsDetailPlugin, ITradeCommitPlugin {


    @Autowired
    private CouponCacheService couponCacheService;

    @Autowired
    private CouponCodeService couponCodeService;

    @Autowired
    private CouponMarketingScopeRepository couponMarketingScopeRepository;

    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private DistributionCacheService distributionCacheService;

    /**
     * 商品列表处理
     *
     * @param goodsInfos 商品数据
     * @param request    参数
     */
    @Override
    public void goodsListFilter(List<GoodsInfoVO> goodsInfos, MarketingPluginRequest request) {
//        goodsInfos.forEach(item -> {
//            List<CouponCache> couponCacheList = couponCacheService.listCouponForGoodsInfo(item, request.getLevelMap());
//
//            Map<String, Map<String, Boolean>> couponFetchStatusMap = Maps.newHashMap();
//
//            if(request.getCustomer() != null && Objects.nonNull(request.getCustomer().getCustomerId())){
//                couponFetchStatusMap = couponCodeService.mapFetchStatus(couponCacheList, request.getCustomer().getCustomerId());
//            }
//
//            Map<String, Map<String, Boolean>> finalCouponFetchStatusMap = couponFetchStatusMap;
//            List<CouponLabelVO> labelList = couponCacheList.stream()/*.limit(6)*/.map(cache ->
//                    CouponLabelVO.builder()
//                            .couponActivityId(cache.getCouponActivityId())
//                            .couponInfoId(cache.getCouponInfoId())
//                            .couponDesc(getLabelMap(cache))
//                            .hasLeft(cache.getHasLeft())
//                            .denomination(cache.getCouponInfo().getDenomination())
//                            .platformFlag(cache.getCouponInfo().getPlatformFlag())
//                            .storeId(cache.getCouponInfo().getStoreId())
//                            .couponType(cache.getCouponInfo().getCouponType().toValue())
//                            .hasFetched(finalCouponFetchStatusMap.isEmpty() ? false : finalCouponFetchStatusMap.get(cache.getCouponActivityId()).get(cache.getCouponInfoId()))
//                            .build()
//            ).sorted(Comparator.comparing(CouponLabelVO::getHasFetched))
//                    .sorted(Comparator.comparing(CouponLabelVO::getHasLeft).reversed())
//                    .collect(Collectors.toList());
////            item.getCouponLabels().addAll(labelList);
//            item.setCouponLabels(labelList);
//        });
    }


    /**
     * 商品详情处理
     *
     * @param detailResponse 商品详情数据
     * @param request        参数
     */
    @Override
    public void goodsDetailFilter(GoodsInfoDetailByGoodsInfoResponse detailResponse, MarketingPluginRequest request) {
        //把品牌从goods搬运到goodsInfo
        detailResponse.getGoodsInfo().setBrandId(detailResponse.getGoods().getBrandId());
        detailResponse.getGoodsInfo().setCateId(detailResponse.getGoods().getCateId());
        List<CouponCache> couponCacheList = couponCacheService.listCouponForGoodsInfo(detailResponse.getGoodsInfo(), request.getLevelMap());

        Map<String, Map<String, Boolean>> couponFetchStatusMap = Maps.newHashMap();
        if(Objects.nonNull(request.getCustomer()) || Objects.nonNull(request.getCustomer().getCustomerId())){
            couponFetchStatusMap = couponCodeService.mapFetchStatus(couponCacheList, request.getCustomer().getCustomerId());
        }
        Map<String, Map<String, Boolean>> finalCouponFetchStatusMap = couponFetchStatusMap;

        List<CouponLabelVO> labelList = couponCacheList.stream()/*.limit(3)*/.map(cache ->
                CouponLabelVO.builder()
                        .couponActivityId(cache.getCouponActivityId())
                        .couponInfoId(cache.getCouponInfoId())
                        .couponDesc(getLabelMap(cache))
                        .hasLeft(cache.getHasLeft())
                        .denomination(cache.getCouponInfo().getDenomination())
                        .platformFlag(cache.getCouponInfo().getPlatformFlag())
                        .storeId(cache.getCouponInfo().getStoreId())
                        .couponType(cache.getCouponInfo().getCouponType().toValue())
                        .hasFetched(finalCouponFetchStatusMap.isEmpty() ? false : finalCouponFetchStatusMap.get(cache.getCouponActivityId()).get(cache.getCouponInfoId()))
                        .build()
        ).sorted(Comparator.comparing(CouponLabelVO::getHasFetched))
                .sorted(Comparator.comparing(CouponLabelVO::getHasLeft).reversed())
                .collect(Collectors.toList());
//        detailResponse.getGoodsInfo().getCouponLabels().addAll(labelList);
        detailResponse.getGoodsInfo().setCouponLabels(labelList);
    }


    /**
     * 订单营销处理
     */
    @Override
    public TradeMarketingResponse wraperMarketingFullInfo(TradeMarketingPluginRequest request) {
        String couponCodeId = request.getCouponCodeId();
        List<TradeItemInfo> tradeItems = request.getTradeItems();
        if(StringUtils.isEmpty(couponCodeId)) {
            return null;
        }

        // 1.查询我的未使用优惠券
        List<CouponCode> couponCodes = couponCodeService.listCouponCodeByCondition(CouponCodeQueryRequest.builder()
                .customerId(request.getCustomerId())
                .useStatus(DefaultFlag.NO)
                .delFlag(DeleteFlag.NO).build());

        // 2.判断所传优惠券，是否是我的未用优惠券
        CouponCode couponCode = couponCodes.stream().filter(item ->
            StringUtils.equals(item.getCouponCodeId(), couponCodeId)
        ).findFirst().orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID));

        // 3.判断所传优惠券，是否在使用时间
        if(LocalDateTime.now().isAfter(couponCode.getEndTime())) {
            if(!request.isForceCommit()) {
                CouponInfo couponInfo = couponInfoRepository.findById(couponCode.getCouponId()).get();
                StringBuilder sb = new StringBuilder("很抱歉，");
                if(FullBuyType.NO_THRESHOLD.equals(couponInfo.getFullBuyType())) {
                    sb.append("无门槛减").append(couponInfo.getDenomination().setScale(0));
                } else {
                    sb.append("满").append(couponInfo.getFullBuyPrice().setScale(0))
                            .append("减").append(couponInfo.getDenomination().setScale(0));
                }
                sb.append("优惠券已失效");
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, sb.toString());
            } else {
                return null;
            }
        }

        // 4.查找出优惠券关联的商品列表
        List<CouponMarketingScope> scopeList = couponMarketingScopeRepository.findByCouponId(couponCode.getCouponId());

        CouponInfo couponInfo = couponInfoRepository.findById(couponCode.getCouponId()).get();
        CouponCodeVO couponCodeVo = new CouponCodeVO();
        KsBeanUtil.copyPropertiesThird(couponCode, couponCodeVo);
        couponCodeVo.setScopeType(couponInfo.getScopeType());
        couponCodeVo.setPlatformFlag(couponInfo.getPlatformFlag());
        couponCodeVo.setStoreId(couponInfo.getStoreId());
        couponCodeVo.setFullBuyPrice(couponInfo.getFullBuyPrice()==null? BigDecimal.ZERO:couponInfo.getFullBuyPrice());
        couponCodeVo.setCouponType(couponInfo.getCouponType());
        List<String> goodsInfoIds = couponCodeService.listCouponSkuIds(tradeItems, couponCodeVo, scopeList)
                .stream().map((item) -> item.getSkuId()).collect(Collectors.toList());

        // 5.排除分销商品
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        List<String> distriSkuIds = tradeItems.stream()
                .filter(item -> {
                    DefaultFlag storeOpenFlag = distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString());
                    return DefaultFlag.YES.equals(openFlag)
                            && DefaultFlag.YES.equals(storeOpenFlag)
                            && DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit());
                })
                .map(TradeItemInfo::getSkuId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(distriSkuIds)) {
            goodsInfoIds = goodsInfoIds.stream()
                    .filter(skuId -> !distriSkuIds.contains(skuId)).collect(Collectors.toList());
        }

        // 6.如果优惠券没有关联下单商品
        if(CollectionUtils.isEmpty(goodsInfoIds)) {
            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID);
        }

        TradeMarketingResponse response = new TradeMarketingResponse();
        response.setTradeCoupon(TradeCoupon.builder()
                .couponCodeId(couponCode.getCouponCodeId())
                .couponCode(couponCode.getCouponCode())
                .goodsInfoIds(goodsInfoIds)
                .discountsAmount(couponInfo.getDenomination())
                .couponType(couponInfo.getCouponType())
                .fullbuyType(couponInfo.getFullBuyType())
                .fullBuyPrice(couponCodeVo.getFullBuyPrice()).build());
        return response;
    }

    @Override
    public TradeMarketingResponse wraperMarketingFullInfoDevanning(TradeMarketingPluginRequest request) {
        String couponCodeId = request.getCouponCodeId();
        List<TradeItemInfo> tradeItems = request.getTradeItems();
        if(StringUtils.isEmpty(couponCodeId)) {
            return null;
        }

        // 1.查询我的未使用优惠券
        List<CouponCode> couponCodes = couponCodeService.listCouponCodeByCondition(CouponCodeQueryRequest.builder()
                .customerId(request.getCustomerId())
                .useStatus(DefaultFlag.NO)
                .delFlag(DeleteFlag.NO).build());

        // 2.判断所传优惠券，是否是我的未用优惠券
        CouponCode couponCode = couponCodes.stream().filter(item ->
                StringUtils.equals(item.getCouponCodeId(), couponCodeId)
        ).findFirst().orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID));

        // 3.判断所传优惠券，是否在使用时间
        if(LocalDateTime.now().isAfter(couponCode.getEndTime())) {
            if(!request.isForceCommit()) {
                CouponInfo couponInfo = couponInfoRepository.findById(couponCode.getCouponId()).get();
                StringBuilder sb = new StringBuilder("很抱歉，");
                if(FullBuyType.NO_THRESHOLD.equals(couponInfo.getFullBuyType())) {
                    sb.append("无门槛减").append(couponInfo.getDenomination().setScale(0));
                } else {
                    sb.append("满").append(couponInfo.getFullBuyPrice().setScale(0))
                            .append("减").append(couponInfo.getDenomination().setScale(0));
                }
                sb.append("优惠券已失效");
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, sb.toString());
            } else {
                return null;
            }
        }

        // 4.查找出优惠券关联的商品列表
        List<CouponMarketingScope> scopeList = couponMarketingScopeRepository.findByCouponId(couponCode.getCouponId());

        CouponInfo couponInfo = couponInfoRepository.findById(couponCode.getCouponId()).get();
        CouponCodeVO couponCodeVo = new CouponCodeVO();
        KsBeanUtil.copyPropertiesThird(couponCode, couponCodeVo);
        couponCodeVo.setScopeType(couponInfo.getScopeType());
        couponCodeVo.setPlatformFlag(couponInfo.getPlatformFlag());
        couponCodeVo.setStoreId(couponInfo.getStoreId());
        couponCodeVo.setFullBuyPrice(couponInfo.getFullBuyPrice());
        couponCodeVo.setCouponType(couponInfo.getCouponType());
        List<String> goodsInfoIds = couponCodeService.listCouponSkuIds(tradeItems, couponCodeVo, scopeList)
                .stream().map((item) -> item.getSkuId()).collect(Collectors.toList());

        // 5.排除分销商品
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        List<String> distriSkuIds = tradeItems.stream()
                .filter(item -> {
                    DefaultFlag storeOpenFlag = distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString());
                    return DefaultFlag.YES.equals(openFlag)
                            && DefaultFlag.YES.equals(storeOpenFlag)
                            && DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit());
                })
                .map(TradeItemInfo::getSkuId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(distriSkuIds)) {
            goodsInfoIds = goodsInfoIds.stream()
                    .filter(skuId -> !distriSkuIds.contains(skuId)).collect(Collectors.toList());
        }

        // 6.如果优惠券没有关联下单商品
        if(CollectionUtils.isEmpty(goodsInfoIds)) {
            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_ORDER_COUPON_INVALID);
        }

        TradeMarketingResponse response = new TradeMarketingResponse();
        response.setTradeCoupon(TradeCoupon.builder()
                .couponCodeId(couponCode.getCouponCodeId())
                .couponCode(couponCode.getCouponCode())
                .goodsInfoIds(goodsInfoIds)
                .discountsAmount(couponInfo.getDenomination())
                .couponType(couponInfo.getCouponType())
                .fullBuyPrice(couponInfo.getFullBuyPrice()).build());
        return response;
    }

    /**
     * 获取优惠券描述
     *
     * @return
     */
    private String getLabelMap(CouponCache coupon) {
        if (coupon.getCouponInfo().getFullBuyType() == FullBuyType.FULL_MONEY) {
            return String.format("满%s减%s", coupon.getCouponInfo().getFullBuyPrice().intValue(), coupon.getCouponInfo().getDenomination().intValue());
        } else {
            return String.format("直减%s", coupon.getCouponInfo().getDenomination().intValue());
        }
    }
}