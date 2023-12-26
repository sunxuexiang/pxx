package com.wanmi.sbc.marketing.coupon.request;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.CouponType;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.enums.ScopeType;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 正在进行的优惠券活动查询
 */
@Data
@Builder
public class CouponCacheQueryRequest {

    /**
     * 优惠券活动Id集合
     */
    private List<String> couponActivityIds;

    /**
     * 品牌List
     */
    private List<Long> brandIds;

    /**
     * 平台分类List
     */
    private List<Long> cateIds;

    /**
     * 店铺分类List
     */
    private List<Long> storeCateIds;

    /**
     * 商品集合
     */
    private List<String> goodsInfoIds;

    /**
     * 用户店铺 + 等级 Map
     */
//    Map<Long, CustomerLevel> levelMap;
    Map<Long, CommonLevelVO> levelMap;

    /**
     * 店铺Id集合
     */
    List<Long> storeIds;

    /**
     * 优惠券分类Id
     */
    private String couponCateId;

    /**
     * 优惠券类型 0通用券 1店铺券 2运费券
     */
    private CouponType couponType;

    /**
     * 是否展示已过期
     */
    private Boolean showLimit;

    /**
     * 店铺Id
     */
    private Long storeId;

    /**
     * 移动端请求仓库id
     */
    private Long wareId;

    /**
     * 仓库id
     */
    private List<Long> wareIds;

    /**
     * 赠券类型
     */
    private Long sendType;


    /**
     * 构建平台优惠券+店铺优惠券的查询条件
     *
     * @return
     */
    public Criteria getCriteria() {

        //店铺相关
        List<Criteria> storeCriteriaList = this.getStoreCriteria();
        //店铺scope相关
        List<Criteria> storeScopeList = this.getStoreScope();
        if (CollectionUtils.isNotEmpty(storeScopeList)) {
            storeCriteriaList.add(new Criteria().orOperator(storeScopeList.toArray(new Criteria[storeScopeList.size()])));
        }
        //店铺券（符合等级的/正在进行的/Scope满足的）
        Criteria storeCriteria = new Criteria().andOperator(storeCriteriaList.toArray(new Criteria[storeCriteriaList.size()]));


        //平台相关
        List<Criteria> platformCriteriaList = this.getPlatformCriteria();
        //平台scope相关
        if (CollectionUtils.isNotEmpty(brandIds) || CollectionUtils.isNotEmpty(cateIds)){
            platformCriteriaList.add(this.getPlatformScope());
        }
        //平台券（符合等级的/正在进行的/Scope满足的）
        Criteria platformCriteria = new Criteria().andOperator(platformCriteriaList.toArray(new Criteria[platformCriteriaList.size()]));


        //优惠券相关过滤，过滤出 平台 + 店铺
        Criteria couponCriteria = new Criteria();
        couponCriteria.orOperator(platformCriteria, storeCriteria);

        List<Criteria> criteria = new ArrayList<>();
        criteria.add(couponCriteria);

        //限定优惠券活动Id
        if (CollectionUtils.isNotEmpty(couponActivityIds)) {
            criteria.add(Criteria.where("couponActivityId").in(couponActivityIds));
        }

        //优惠券分类查询
        if (StringUtils.isNotBlank(couponCateId)) {
            criteria.add(Criteria.where("couponCateIds").is(couponCateId));
        }

        //优惠券类型查询
        if (couponType != null) {
            criteria.add(Criteria.where("couponInfo.couponType").is(couponType.toString()));
        }

        if (storeId !=null){
            Criteria.where("couponActivity.storeId").is(storeId);
        }
        //活动状态
        if (Objects.isNull(showLimit)||showLimit){
            criteria.add(Criteria.where("couponActivity.pauseFlag").is(DefaultFlag.NO.toString()));
        }

        //赠券类型
        if (Objects.nonNull(sendType)) {
            criteria.add(Criteria.where("couponActivity.sendType").is(sendType));
        }

        return new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()]));
    }

    /**
     * 构建平台优惠券+店铺优惠券的查询条件(领券中心查询条件构建)
     *
     * @return
     */
    public Criteria getCriteriaNew() {

        //店铺相关
        List<Criteria> storeCriteriaList = this.getStoreCriteriaNew();
        //店铺scope相关
        List<Criteria> storeScopeList = this.getStoreScope();
        if (CollectionUtils.isNotEmpty(storeScopeList)) {
            storeCriteriaList.add(new Criteria().orOperator(storeScopeList.toArray(new Criteria[storeScopeList.size()])));
        }
        //店铺券（符合等级的/正在进行的/Scope满足的）
        Criteria storeCriteria = new Criteria().andOperator(storeCriteriaList.toArray(new Criteria[storeCriteriaList.size()]));


        //平台相关
        List<Criteria> platformCriteriaList = this.getPlatformCriteriaNew();
        //平台scope相关
        if (CollectionUtils.isNotEmpty(brandIds) || CollectionUtils.isNotEmpty(cateIds)){
            platformCriteriaList.add(this.getPlatformScope());
        }
        //平台券（符合等级的/正在进行的/Scope满足的）
        Criteria platformCriteria = new Criteria().andOperator(platformCriteriaList.toArray(new Criteria[platformCriteriaList.size()]));


        //优惠券相关过滤，过滤出 平台 + 店铺
        Criteria couponCriteria = new Criteria();
        couponCriteria.orOperator(platformCriteria, storeCriteria);

        List<Criteria> criteria = new ArrayList<>();
        criteria.add(couponCriteria);

        //仓库id -1代表all
        if (CollectionUtils.isNotEmpty(wareIds)) {
            criteria.add(Criteria.where("couponInfo.wareId").in(wareIds));
        }

        /*if (Objects.nonNull(wareId)) {
            criteria.add(Criteria.where("couponInfo.wareId").is(wareId));
        }*/

        //限定优惠券活动Id
        if (CollectionUtils.isNotEmpty(couponActivityIds)) {
            criteria.add(Criteria.where("couponActivityId").in(couponActivityIds));
        }

        //优惠券分类查询
        if (StringUtils.isNotBlank(couponCateId)) {
            criteria.add(Criteria.where("couponCateIds").is(couponCateId));
        }

        //优惠券类型查询
        if (couponType != null) {
            criteria.add(Criteria.where("couponInfo.couponType").is(couponType.toString()));
        }

        if (storeId !=null){
            Criteria.where("couponActivity.storeId").is(storeId);
        }
        //活动状态
        if (Objects.isNull(showLimit)||showLimit){
            criteria.add(Criteria.where("couponActivity.pauseFlag").is(DefaultFlag.NO.toString()));
        }

        if(Objects.nonNull(sendType)){
            criteria.add(Criteria.where("couponActivity.sendType").is(sendType));
        }

        return new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()]));
    }

    /**
     * 获取店铺的查询条件
     *
     * @return
     */
    private List<Criteria> getStoreCriteria() {
        //店铺相关券
        List<Criteria> storeCriteriaList = new ArrayList<>();
        storeCriteriaList.add(Criteria.where("couponActivity.platformFlag").is(DefaultFlag.NO.toString()));
        storeCriteriaList.add(Criteria.where("couponActivity.couponActivityType").is(CouponActivityType.ALL_COUPONS.toString()));
        if (Objects.isNull(showLimit)||showLimit){
            storeCriteriaList.add(Criteria.where("couponActivity.startTime").lt(LocalDateTime.now()));
            storeCriteriaList.add(Criteria.where("couponActivity.endTime").gt(LocalDateTime.now()));
        }
        return storeCriteriaList;
    }

    /**
     * 获取店铺的查询条件(新包含24小时内即将开始)
     *
     * @return
     */
    private List<Criteria> getStoreCriteriaNew() {
        //店铺相关券
        List<Criteria> storeCriteriaList = new ArrayList<>();
        storeCriteriaList.add(Criteria.where("couponActivity.platformFlag").is(DefaultFlag.NO.toString()));
        storeCriteriaList.add(Criteria.where("couponActivity.couponActivityType").is(CouponActivityType.ALL_COUPONS.toString()));
        if (Objects.isNull(showLimit)||showLimit){
            //新增24小时之内开始的优惠券活动
            storeCriteriaList.add(Criteria.where("couponActivity.startTime").lt(LocalDateTime.now().plusHours(24)));
            storeCriteriaList.add(Criteria.where("couponActivity.endTime").gt(LocalDateTime.now()));
        }
        return storeCriteriaList;
    }

    /**
     * 获取平台的查询条件
     *
     * @return
     */
    private List<Criteria> getPlatformCriteria() {
        //店铺相关券
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("couponActivity.couponActivityType").is(CouponActivityType.ALL_COUPONS.toString()));
        criteriaList.add(Criteria.where("couponActivity.platformFlag").is(DefaultFlag.YES.toString()));
        if (Objects.isNull(showLimit)||showLimit){
            criteriaList.add(Criteria.where("couponActivity.startTime").lt(LocalDateTime.now()));
            criteriaList.add(Criteria.where("couponActivity.endTime").gt(LocalDateTime.now()));
        }
        return criteriaList;
    }

    /**
     * 获取平台的查询条件
     *
     * @return
     */
    private List<Criteria> getPlatformCriteriaNew() {
        //店铺相关券
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("couponActivity.couponActivityType").is(CouponActivityType.ALL_COUPONS.toString()));
        criteriaList.add(Criteria.where("couponActivity.platformFlag").is(DefaultFlag.YES.toString()));
        if (Objects.isNull(showLimit)||showLimit){
            //新增24小时之内开始的优惠券活动
            criteriaList.add(Criteria.where("couponActivity.startTime").lt(LocalDateTime.now().plusHours(24)));
            criteriaList.add(Criteria.where("couponActivity.endTime").gt(LocalDateTime.now()));
        }
        return criteriaList;
    }

    /**
     * 获取平台的Scope相关查询条件
     *
     * @return
     */
    private Criteria getPlatformScope() {
        //按照scope进行过滤
        Criteria scopeCriteria = new Criteria();

        List<Criteria> scopeCriteriaList = new ArrayList<>();

        //查询品牌
        if (CollectionUtils.isNotEmpty(brandIds)) {
            Criteria brandCriteria = new Criteria();
            brandCriteria.andOperator(
                    Criteria.where("scopes.scopeType").is(ScopeType.BRAND.toString()),
                    Criteria.where("scopes.scopeId").in(brandIds.stream().map(Object::toString).collect(Collectors.toList()))
            );
            scopeCriteriaList.add(brandCriteria);
        }

        //查询平台分类
        if (CollectionUtils.isNotEmpty(cateIds)) {
            Criteria cateCriteria = new Criteria();
            cateCriteria.andOperator(
                    Criteria.where("scopes.scopeType").is(ScopeType.BOSS_CATE.toString()),
                    Criteria.where("scopes.scopeId").in(cateIds.stream().map(Object::toString).collect(Collectors.toList()))
            );
            scopeCriteriaList.add(cateCriteria);
        }

        //全部商品
        scopeCriteriaList.add(new Criteria().andOperator(
                Criteria.where("couponInfo.scopeType").is(ScopeType.ALL.toString())
        ));

        //查商品
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            Criteria goodsCriteria = new Criteria();
            goodsCriteria.andOperator(
                    Criteria.where("scopes.scopeType").is(ScopeType.SKU.toString()),
                    Criteria.where("scopes.scopeId").in(goodsInfoIds)
            );
            scopeCriteriaList.add(goodsCriteria);
        }

        //所有商品
        scopeCriteria.orOperator(scopeCriteriaList.toArray(new Criteria[scopeCriteriaList.size()]));

        return scopeCriteria;
    }

    /**
     * 获取商铺的Scope相关查询条件
     *
     * @return
     */
    private List<Criteria> getStoreScope() {
        List<Criteria> levelMapCriteria = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(storeIds)) {
            storeIds.stream().distinct().forEach(storeId -> {
                        //按照scope进行过滤
                        Criteria scopeCriteria = new Criteria();

                        List<Criteria> scopeCriteriaList = new ArrayList<>();

                        //查询品牌
                        if (CollectionUtils.isNotEmpty(brandIds)) {
                            Criteria brandCriteria = new Criteria();
                            brandCriteria.andOperator(
                                    Criteria.where("scopes.scopeType").is(ScopeType.BRAND.toString()),
                                    Criteria.where("scopes.scopeId").in(brandIds.stream().map(Object::toString).collect(Collectors.toList())),
                                    Criteria.where("couponActivity.storeId").is(storeId)
                            );
                            scopeCriteriaList.add(brandCriteria);
                        }

                        //查询店铺分类
                        if (CollectionUtils.isNotEmpty(storeCateIds)) {
                            Criteria storeCateCriteria = new Criteria();
                            storeCateCriteria.andOperator(
                                    Criteria.where("scopes.scopeType").is(ScopeType.STORE_CATE.toString()),
                                    Criteria.where("scopes.scopeId").in(storeCateIds.stream().map(Object::toString).collect(Collectors.toList())),
                                    Criteria.where("couponActivity.storeId").is(storeId)
                            );
                            scopeCriteriaList.add(storeCateCriteria);
                        }

                        //查询平台分类
                        if (CollectionUtils.isNotEmpty(cateIds)) {
                            Criteria cateCriteria = new Criteria();
                            cateCriteria.andOperator(
                                    Criteria.where("scopes.scopeType").is(ScopeType.BOSS_CATE.toString()),
                                    Criteria.where("scopes.scopeId").in(cateIds.stream().map(Object::toString).collect(Collectors.toList())),
                                    Criteria.where("couponActivity.storeId").is(storeId)
                            );
                            scopeCriteriaList.add(cateCriteria);
                        }
                        //查商品
                        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
                            Criteria goodsCriteria = new Criteria();
                            goodsCriteria.andOperator(
                                    Criteria.where("scopes.scopeType").is(ScopeType.SKU.toString()),
                                    Criteria.where("scopes.scopeId").in(goodsInfoIds),
                                    Criteria.where("couponActivity.storeId").is(storeId)
                            );
                            scopeCriteriaList.add(goodsCriteria);
                        }

                        //全部商品
                        scopeCriteriaList.add(new Criteria().andOperator(
                                Criteria.where("couponInfo.scopeType").is(ScopeType.ALL.toString()),
                                Criteria.where("couponActivity.storeId").is(storeId)
                        ));

                        //所有商品
                        scopeCriteria.orOperator(scopeCriteriaList.toArray(new Criteria[scopeCriteriaList.size()]));

                        levelMapCriteria.add(new Criteria().andOperator(
                                Criteria.where("couponActivity.storeId").is(storeId),
                                (
                                        Objects.nonNull(levelMap) && levelMap.get(storeId) != null ?
                                                new Criteria().orOperator(
                                                        Criteria.where("couponActivity.joinLevel").is(MarketingJoinLevel.ALL_LEVEL.toValue() + ""),
                                                        Criteria.where("couponActivity.joinLevel").is(levelMap.get(storeId).getLevelId().toString()),
                                                        Criteria.where("couponActivity.joinLevel").is(MarketingJoinLevel.ALL_CUSTOMER.toValue() + "")
                                                ) :
                                                Criteria.where("couponActivity.joinLevel").is(MarketingJoinLevel.ALL_CUSTOMER.toValue() + "")
                                ),
                                scopeCriteria
                        ));
                    }
            );
        }
        return levelMapCriteria;
    }
}
