package com.wanmi.sbc.marketing.plugin.impl;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.CouponLabelVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.MarketingLabelVO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.FullBuyType;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.common.request.TradeMarketingPluginRequest;
import com.wanmi.sbc.marketing.common.response.TradeMarketingResponse;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityLevel;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import com.wanmi.sbc.marketing.coupon.model.vo.CoinActivityQueryVo;
import com.wanmi.sbc.marketing.coupon.model.vo.CouponActivityQueryVo;
import com.wanmi.sbc.marketing.coupon.repository.*;
import com.wanmi.sbc.marketing.plugin.IGoodsDetailPlugin;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.plugin.ITradeCommitPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 优惠券活动插件
 * @author: XinJiang
 * @time: 2022/3/2 20:12
 */
@Repository("couponActivityPlugin")
public class CouponActivityPlugin implements IGoodsListPlugin, IGoodsDetailPlugin, ITradeCommitPlugin {

    @Autowired
    private CouponActivityRepository couponActivityRepository;

    @Autowired
    private CouponActivityConfigRepository couponActivityConfigRepository;

    @Autowired
    private CouponActivityLevelRepository couponActivityLevelRepository;

    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private CoinActivityRepository coinActivityRepository;

    /**
     * 商品详情处理
     * @param detailResponse 商品详情数据
     * @param request 参数
     */
    @Override
    public void goodsDetailFilter(GoodsInfoDetailByGoodsInfoResponse detailResponse, MarketingPluginRequest request) {

    }

    /**
     * 商品列表处理
     * @param goodsInfos 商品数据
     * @param request 参数
     */
    @Override
    public void goodsListFilter(List<GoodsInfoVO> goodsInfos, MarketingPluginRequest request) {
        List<String> goodsInfoIds = goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        List<CouponActivityQueryVo> couponActivitiesObj = couponActivityRepository.queryGoingActivityByGoodsNew(goodsInfoIds);
        /*Map<String, List<CouponActivityVO>> couponActivitiesMap = couponActivitiesObject.stream().map(v -> {
            return convertCouponActivityVOSQLResult(v);
        }).collect(Collectors.groupingBy(CouponActivityVO::getGoodsInfoId));*/
        if (CollectionUtils.isNotEmpty(couponActivitiesObj)) {
            Map<String, List<CouponActivityQueryVo>> couponActivitiesMap = couponActivitiesObj.stream().collect(Collectors.groupingBy(CouponActivityQueryVo::getGoodsInfoId));

            goodsInfos.forEach(goodsInfoVO -> {
                // List<CouponActivity> couponActivities = couponActivityRepository.queryGoingActivityByGoods(CouponActivityType.BUY_ASSIGN_GOODS_COUPON.toValue(), Arrays.asList(goodsInfoVO.getGoodsInfoId()));
                List<CouponActivityQueryVo> couponActivitiesVO = couponActivitiesMap.get(goodsInfoVO.getGoodsInfoId());

                if (CollectionUtils.isNotEmpty(couponActivitiesVO)) {
                    /*List<CouponActivity> couponActivities = KsBeanUtil.convert(couponActivitiesVO,CouponActivity.class);
                    CouponActivity couponActivity = couponActivities.get(0);*/
                    CouponActivityQueryVo couponActivity = couponActivitiesVO.get(0);
                    List<CouponActivityConfig> couponActivityConfigs = couponActivityConfigRepository.findByActivityId(couponActivity.getActivityId());
                    List<String> couponIds = couponActivityConfigs.stream().map(CouponActivityConfig::getCouponId).collect(Collectors.toList());
                    List<CouponInfo> couponInfos = couponInfoRepository.findAllById(couponIds);
                    couponInfos.sort(Comparator.comparing(CouponInfo::getDenomination).reversed());
                    if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.ANY_ONE)
                            || couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.ALL)) {
                        this.setMarketingLabel(goodsInfoVO,couponInfos,couponActivity,null);
                    } else {
                        List<CouponActivityLevel> couponActivityLevels = couponActivityLevelRepository.findByActivityId(couponActivity.getActivityId());
                        if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
                            couponActivityLevels.sort(Comparator.comparing(CouponActivityLevel::getFullCount));
                        } else {
                            couponActivityLevels.sort(Comparator.comparing(CouponActivityLevel::getFullAmount));
                        }
                        this.setMarketingLabel(goodsInfoVO,couponInfos,couponActivity,couponActivityLevels.get(0));
                    }
                }
            });
        }

        //List<CoinActivityQueryVo> coinActivityList =  coinActivityRepository.queryByGoodsInfoIds(goodsInfoIds);
        //修改查询设置为显示的商品
        List<CoinActivityQueryVo> coinActivityList =  coinActivityRepository.queryByGoodsInfoIdDisplayTypes(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(coinActivityList)) {
            Map<String, List<CoinActivityQueryVo>> coinActivitiesMap = coinActivityList.stream().collect(Collectors.groupingBy(CoinActivityQueryVo::getGoodsInfoId));

            goodsInfos.forEach(goodsInfoVO -> {
                List<CoinActivityQueryVo> coinActivitiesVO = coinActivitiesMap.get(goodsInfoVO.getGoodsInfoId());

                if (CollectionUtils.isNotEmpty(coinActivitiesVO)) {
                    CoinActivityQueryVo coinActivityQueryVo = coinActivitiesVO.get(0);

                    MarketingLabelVO label = new MarketingLabelVO();

                    label.setConActivityId(coinActivityQueryVo.getActivityId());
                    label.setMarketingType(7);
                    label.setCouponActivityFullType(coinActivityQueryVo.getCoinActivityFullType().toValue());

                    label.setCoinNum(coinActivityQueryVo.getCoinNum());

                    if (coinActivityQueryVo.getIsOverlap().equals(DefaultFlag.YES)) {
                            label.setMarketingDesc(String.format("平台每箱立返%s元鲸币", coinActivityQueryVo.getCoinNum().setScale(0, RoundingMode.HALF_UP)));
                    } else if (coinActivityQueryVo.getIsOverlap().equals(DefaultFlag.NO)) {
                            label.setMarketingDesc(String.format("平台每单立返%s元鲸币", coinActivityQueryVo.getCoinNum().setScale(0, RoundingMode.HALF_UP)));
                    }
                    goodsInfoVO.getMarketingLabels().add(label);
                }
            });
        }


    }

    /**
     * 从数据库实体转换为返回前端的用户信息
     * (字段顺序不可变)
     */
    public static CouponActivityVO convertCouponActivityVOSQLResult(Object result) {
        CouponActivityVO response = new CouponActivityVO();

        response = KsBeanUtil.convert(result, CouponActivityVO.class);
        response.setGoodsInfoId((String) ((Object[]) result)[30]);
//
//        response.setCustomerId((String) ((Object[]) result)[0]);
//        response.setCustomerAccount((String) ((Object[]) result)[1]);
//        response.setCustomerName((String) ((Object[]) result)[2]);
//        if (((Object[]) result)[3] != null) {
//            response.setCustomerLevelId(((BigInteger) ((Object[]) result)[3]).longValue());
//        }
//        response.setCustomerLevelName((String) ((Object[]) result)[4]);
        return response;
    }


    /**
     * 订单营销处理
     * @param request
     * @return
     */
    @Override
    public TradeMarketingResponse wraperMarketingFullInfo(TradeMarketingPluginRequest request) {
        return null;
    }

    @Override
    public TradeMarketingResponse wraperMarketingFullInfoDevanning(TradeMarketingPluginRequest request) {
        return null;
    }

    /**
     * 封装商品活动信息
     * @param goodsInfoVO
     * @param couponInfos
     * @param couponActivity
     */
    private void setMarketingLabel(GoodsInfoVO goodsInfoVO,List<CouponInfo> couponInfos,CouponActivityQueryVo couponActivity,CouponActivityLevel couponActivityLevel) {
        MarketingLabelVO label = new MarketingLabelVO();

        label.setActivityId(couponActivity.getActivityId());
        label.setMarketingType(4);
        label.setCouponActivityFullType(couponActivity.getCouponActivityFullType().toValue());

        List<CouponLabelVO> couponLabelVOS = couponInfos.stream().limit(10).map(couponInfo ->
                CouponLabelVO.builder()
                        .couponActivityId(couponActivity.getActivityId())
                        .couponInfoId(couponInfo.getCouponId())
                        .couponDesc(getLabelMap(couponInfo))
                        .denomination(couponInfo.getDenomination().doubleValue())
                        .platformFlag(couponInfo.getPlatformFlag())
                        .storeId(couponInfo.getStoreId())
                        .couponType(couponInfo.getCouponType().toValue())
                        .build()
        ).collect(Collectors.toList());
        label.setCouponLabelVOS(couponLabelVOS);

        if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.ANY_ONE)) {
            label.setMarketingDesc(String.format("任买1箱返劵"));
        } else if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.ALL)) {
            label.setMarketingDesc(String.format("每样各买1箱起返券"));
        } else if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_COUNT)) {
            label.setNumber(couponActivityLevel.getFullCount().intValue());
            label.setMarketingDesc(String.format("跨单品买%s箱返券",couponActivityLevel.getFullCount()));
        } else if (couponActivity.getCouponActivityFullType().equals(CouponActivityFullType.FULL_AMOUNT)) {
            label.setAmount(couponActivityLevel.getFullAmount());
            label.setMarketingDesc(String.format("跨单品满%s元返券",couponActivityLevel.getFullAmount()));
        }
        goodsInfoVO.getMarketingLabels().add(label);
    }

    /**
     * 获取优惠券描述
     *
     * @return
     */
    private String getLabelMap(CouponInfo coupon) {
        if (coupon.getFullBuyType() == FullBuyType.FULL_MONEY) {
            return String.format("满%s减%s", coupon.getFullBuyPrice().intValue(), coupon.getDenomination().intValue());
        } else {
            return String.format("直减%s", coupon.getDenomination().intValue());
        }
    }
}
