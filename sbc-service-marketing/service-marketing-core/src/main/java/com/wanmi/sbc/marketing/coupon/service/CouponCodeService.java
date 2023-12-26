package com.wanmi.sbc.marketing.coupon.service;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelByCustomerIdAndStoreIdRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelByCustomerIdAndStoreIdResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByIdsRequest;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateGoodsRelaVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateVO;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCheckoutRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeBatchSendCouponRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponFetchRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCheckoutResponse;
import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.dto.CouponActivityConfigAndCouponInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeBatchModifyDTO;
import com.wanmi.sbc.marketing.bean.dto.CouponInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.*;
import com.wanmi.sbc.marketing.bean.vo.CheckGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoGetRecordVO;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.coupon.model.entity.TradeCouponSnapshot;
import com.wanmi.sbc.marketing.coupon.model.entity.cache.CouponCache;
import com.wanmi.sbc.marketing.coupon.model.root.*;
import com.wanmi.sbc.marketing.coupon.mongorepository.TradeCouponSnapshotRepository;
import com.wanmi.sbc.marketing.coupon.repository.CouponCodeRepository;
import com.wanmi.sbc.marketing.coupon.repository.CouponInfoRepository;
import com.wanmi.sbc.marketing.coupon.repository.CouponMarketingScopeRepository;
import com.wanmi.sbc.marketing.coupon.request.CouponCodeBuildRequest;
import com.wanmi.sbc.marketing.coupon.request.CouponCodeListForUseRequest;
import com.wanmi.sbc.marketing.coupon.request.CouponCodePageRequest;
import com.wanmi.sbc.marketing.coupon.response.CouponCodeCountResponse;
import com.wanmi.sbc.marketing.coupon.response.CouponCodeQueryResponse;
import com.wanmi.sbc.marketing.coupon.response.CouponLeftResponse;
import com.wanmi.sbc.marketing.distribution.service.DistributionCacheService;
import com.wanmi.sbc.marketing.redis.RedisService;
import com.wanmi.sbc.marketing.util.common.CodeGenUtil;
import com.wanmi.sbc.mongo.MongoTccHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 优惠券码Service
 *
 * @author CHENLI
 */
@Slf4j
@Service
public class CouponCodeService {

    //优惠券库存key
    private final static String COUPON_BANK = "COUPON_BANK_";

    @Autowired
    private CouponCodeRepository couponCodeRepository;

    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private CouponMarketingScopeRepository couponMarketingScopeRepository;

    @Autowired
    private TradeCouponSnapshotRepository tradeCouponSnapshotRepository;

    @Autowired
    private TradeCouponSnapshotService tradeCouponSnapshotService;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
//    private CustomerService customerService;
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CouponActivityService couponActivityService;

    @Autowired
    private RedisService redisService;

    @Autowired
//    private CustomerLevelService customerLevelService;
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private CouponActivityConfigService couponActivityConfigService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CouponCacheService couponCacheService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * 根据条件查询优惠券码列表
     */
    public List<CouponCode> listCouponCodeByCondition(CouponCodeQueryRequest request) {
        Sort sort = request.getSort();
        if (Objects.nonNull(sort)) {
            return couponCodeRepository.findAll(this.getWhereCriteria(request), sort);
        } else {
            return couponCodeRepository.findAll(this.getWhereCriteria(request));
        }
    }

    private Specification<CouponCode> getWhereCriteria(CouponCodeQueryRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 领取人id
            if (Objects.nonNull(request.getCustomerId())) {
                predicates.add(cbuild.equal(root.get("customerId"), request.getCustomerId()));
            }

            //领取人ids
            if (CollectionUtils.isNotEmpty(request.getCustomerIds())){
                CriteriaBuilder.In in = cbuild.in(root.get("customerId"));
                request.getCustomerIds().forEach(in::value);
                predicates.add(in);
            }

            // 优惠券id
            if (Objects.nonNull(request.getCouponId())) {
                predicates.add(cbuild.equal(root.get("couponId"), request.getCouponId()));
            }

            // 优惠券id集合
            if (CollectionUtils.isNotEmpty(request.getCouponIds())) {
                CriteriaBuilder.In in = cbuild.in(root.get("couponId"));
                request.getCouponIds().forEach(in::value);
                predicates.add(in);
            }

            // 优惠券活动id
            if (Objects.nonNull(request.getActivityId())) {
                predicates.add(cbuild.equal(root.get("activityId"), request.getActivityId()));
            }

            // 使用状态
            if (Objects.nonNull(request.getUseStatus())) {
                predicates.add(cbuild.equal(root.get("useStatus"), request.getUseStatus()));
            }

            // 删除标记
            if (Objects.nonNull(request.getDelFlag())) {
                predicates.add(cbuild.equal(root.get("delFlag"), request.getDelFlag()));
            }

            // 过期标识
            if (Objects.nonNull(request.getNotExpire())) {
                predicates.add(cbuild.greaterThan(root.get("endTime"), LocalDateTime.now()));
            }

            //获取优惠券的时间，大于开始时间
            if(Objects.nonNull(request.getAcquireStartTime())){
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("acquireTime"), request.getAcquireStartTime()));
            }
            //获取优惠券的时间，大于结束时间
            if(Objects.nonNull(request.getAcquireEndTime())){
                predicates.add(cbuild.lessThanOrEqualTo(root.get("acquireTime"), request.getAcquireEndTime()));
            }

            //优惠券到期时间
            if(Objects.nonNull(request.getEndTime())){
                predicates.add(cbuild.equal(root.get("endTime"), request.getEndTime()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    @Resource
    private MongoTccHelper mongoTccHelper;

    @SuppressWarnings("unused")
    public void confirmListCouponCodeForUse(CouponCodeListForUseRequest request) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelListCouponCodeForUse(CouponCodeListForUseRequest request) {
        mongoTccHelper.cancel();
    }

    /**
     * 查询使用优惠券页需要的优惠券列表
     */
    @TccTransaction
    public List<CouponCodeVO> listCouponCodeForUse(CouponCodeListForUseRequest request) {

        // 1.设置tradeItem的storeCateIds
        List<TradeItemInfo> tradeItemInfos = request.getTradeItems();
        List<String> goodsIds = tradeItemInfos.stream()
                .map(TradeItemInfo::getSpuId).distinct().collect(Collectors.toList());
        List<StoreCateGoodsRelaVO> relas =
                storeCateQueryProvider.listByGoods(new StoreCateListByGoodsRequest(goodsIds)).getContext().getStoreCateGoodsRelaVOList();
        Map<String, List<StoreCateGoodsRelaVO>> relasMap = relas.stream().collect(Collectors.groupingBy(rela -> rela.getGoodsId()));
        tradeItemInfos.forEach(item -> {
            String spuId = item.getSpuId();
            List<StoreCateGoodsRelaVO> storeCateGoodsRelaVOS = relasMap.get(spuId);
            if(CollectionUtils.isNotEmpty(storeCateGoodsRelaVOS)){
                item.setStoreCateIds(storeCateGoodsRelaVOS.stream().map(rela -> rela.getStoreCateId()).collect(Collectors.toList()));
            }
        });

        // 2.查询我的未使用的优惠券，并关联优惠券信息
        Query query = entityManager.createNativeQuery(request.getQuerySql());
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<CouponCodeVO> couponCodeVos = CouponCodeListForUseRequest.converter(query.getResultList());
        log.info("个人所有的优惠价=========="+couponCodeVos);

        // 3.循环处理每个优惠券
        TradeCouponSnapshot checkInfo = new TradeCouponSnapshot();
        if(CollectionUtils.isNotEmpty(couponCodeVos)) {
            BaseResponse<ListStoreByIdsResponse> baseResponse =
                    storeQueryProvider.listByIds(new ListStoreByIdsRequest(couponCodeVos.stream().map(CouponCodeVO::getStoreId).collect(Collectors.toList())));
            List<StoreVO> storeVOList = baseResponse.getContext().getStoreVOList();
            for (CouponCodeVO couponCodeVO : couponCodeVos) {
                for (StoreVO storeVO : storeVOList) {
                    if (couponCodeVO.getStoreId().equals(storeVO.getStoreId())) {
                        couponCodeVO.setStoreName(storeVO.getStoreName());
                        couponCodeVO.setCompanyType(storeVO.getCompanyType());
                    }
                }
            }
            List<CouponMarketingScope> allScopeList = couponMarketingScopeRepository.findByCouponIdIn(
                    couponCodeVos.stream().map(vo -> vo.getCouponId()).collect(Collectors.toList())
            );

            // 开始遍历能使用的优惠券
            couponCodeVos = couponCodeVos.stream().map(couponCode -> {

                // 3.1.标记未到可用时间优惠券
                if (!(LocalDateTime.now().isAfter(couponCode.getStartTime())
                        && LocalDateTime.now().isBefore(couponCode.getEndTime()))) {
                    couponCode.setStatus(CouponCodeStatus.UN_REACH_TIME);
                }

                //3.2.判断优惠券是否即将过期 结束时间加上5天，大于现在时间，即将过期 true 是 false 否
                if (Objects.nonNull(couponCode.getEndTime())) {
                    if (LocalDateTime.now().plusDays(Constant.OUT_OF_DAYS).isAfter(couponCode.getEndTime())) {
                        couponCode.setNearOverdue(true);
                    } else {
                        couponCode.setNearOverdue(false);
                    }
                }

                // 3.3.根据优惠券营销类型计算关联的订单商品
                List<CouponMarketingScope> scopeList = allScopeList.stream()
                        .filter(scope -> StringUtils.equals(scope.getCouponId(), couponCode.getCouponId()))
                        .collect(Collectors.toList());
                // 满足当前优惠券使用范围的订单项
                List<TradeItemInfo> tradeItems = this.listCouponSkuIds(tradeItemInfos, couponCode, scopeList);
                log.info("满足当前优惠券使用范围的订单项===:{}", JSON.toJSONString(tradeItems));
                // 只能选择积分商品
                DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
                tradeItems = tradeItems.stream()
                        .filter(item -> {
                            DefaultFlag storeOpenFlag =
                                    distributionCacheService.queryStoreOpenFlag(item.getStoreId().toString());
                            return !(DefaultFlag.YES.equals(openFlag)
                                    && DefaultFlag.YES.equals(storeOpenFlag)
                                    && DistributionGoodsAudit.CHECKED.equals(item.getDistributionGoodsAudit()));
                        }).collect(Collectors.toList());
//                //排除特价商品
//                tradeItems=tradeItems.stream().filter(param->(Objects.isNull(param.getGoodsInfoType()))
//                        ||(Objects.nonNull(param.getGoodsInfoType())&&param.getGoodsInfoType()==0)).collect(Collectors.toList());
                log.info("满足当前优惠券使用范围的订单项===2:{}", JSON.toJSONString(tradeItems));
                /**
                 * 只能满足一个，只能一种商品类型使用：
                 * 如果批发满足给批发使用
                 * 如果散批满足给散批使用
                 */
                // 优惠券关联的订单商品总价
                BigDecimal totalPriceForWholeSale = tradeItems.stream().filter(x-> SaleType.WHOLESALE.equals(x.getSaleType()))
                        .map((item) -> item.getPrice()).reduce(BigDecimal.ZERO, (total, price) -> total.add(price));

                BigDecimal totalPriceForRetail = tradeItems.stream().filter(x-> SaleType.RETAIL.equals(x.getSaleType()))
                        .map((item) -> item.getPrice()).reduce(BigDecimal.ZERO, (total, price) -> total.add(price));

                BigDecimal totalPriceForBulk = tradeItems.stream().filter(x-> SaleType.BULK.equals(x.getSaleType()))
                        .map((item) -> item.getPrice()).reduce(BigDecimal.ZERO, (total, price) -> total.add(price));

                // 优惠券关联的订单商品
                List<String> goodsInfoIds = tradeItems.stream().filter(x-> SaleType.WHOLESALE.equals(x.getSaleType())).map((item) -> item.getSkuId()).collect(Collectors.toList());
                List<String> retailGoodsInfoIds = tradeItems.stream().filter(x-> SaleType.RETAIL.equals(x.getSaleType())).map((item) -> item.getSkuId()).collect(Collectors.toList());
                List<String> bulkGoodsInfoIds = tradeItems.stream().filter(x-> SaleType.BULK.equals(x.getSaleType())).map((item) -> item.getSkuId()).collect(Collectors.toList());

                List<String> allGoodsInfoIds = Lists.newArrayList();

                // 3.4.根据优惠券关联的订单商品，计算优惠券状态
                if (couponCode.getStatus() != CouponCodeStatus.UN_REACH_TIME) {
                    if (goodsInfoIds.size() == 0 && retailGoodsInfoIds.size()==0 && bulkGoodsInfoIds.size()==0) {
                        couponCode.setStatus(CouponCodeStatus.NO_AVAILABLE_SKU);
                    } else {
                        // 有门槛
                        if (FullBuyType.FULL_MONEY.equals(couponCode.getFullBuyType())){
                            if(totalPriceForWholeSale.compareTo(couponCode.getFullBuyPrice()) >= 1){
                                // 批发订单达到门槛
                                couponCode.setStatus(CouponCodeStatus.AVAILABLE);
                                allGoodsInfoIds.addAll(goodsInfoIds);
                            } else {
                                if(totalPriceForBulk.compareTo(couponCode.getFullBuyPrice()) >= 1){
                                    // 散批订单达到门槛
                                    couponCode.setStatus(CouponCodeStatus.AVAILABLE);
                                    allGoodsInfoIds.addAll(bulkGoodsInfoIds);
                                } else if(totalPriceForRetail.compareTo(couponCode.getFullBuyPrice()) >= 1){
                                    // 散批订单达到门槛
                                    couponCode.setStatus(CouponCodeStatus.AVAILABLE);
                                    allGoodsInfoIds.addAll(retailGoodsInfoIds);
                                } else {
                                    couponCode.setStatus(CouponCodeStatus.UN_REACH_PRICE);
                                }
                            }
                        // 无门槛
                        } else {
                            couponCode.setStatus(CouponCodeStatus.AVAILABLE);
                            if(CollectionUtils.isNotEmpty(goodsInfoIds)){
                                allGoodsInfoIds.addAll(goodsInfoIds);
                            } else if (CollectionUtils.isNotEmpty(bulkGoodsInfoIds)){
                                allGoodsInfoIds.addAll(bulkGoodsInfoIds);
                            }else {
                                allGoodsInfoIds.addAll(retailGoodsInfoIds);
                            }
                        }
                    }
                }

                // 3.5.将优惠券和商品关联关系放入缓存对象
                TradeCouponSnapshot.CheckCouponCode checkCouponCode = new TradeCouponSnapshot.CheckCouponCode();
                checkCouponCode.setGoodsInfoIds(allGoodsInfoIds);
                checkCouponCode.setCouponCodeId(couponCode.getCouponCodeId());
                checkCouponCode.setFullBuyPrice(couponCode.getFullBuyPrice());
                checkCouponCode.setDenomination(couponCode.getDenomination());
                checkCouponCode.setCouponType(couponCode.getCouponType());
                checkInfo.getCouponCodes().add(checkCouponCode);

                return couponCode;
            }).collect(Collectors.toList());
        }

        // 4.将缓存对象存入mongo
        checkInfo.setGoodsInfos(request.getTradeItems().stream().map((item) -> {
            TradeCouponSnapshot.CheckGoodsInfo checkGoodsInfo = new TradeCouponSnapshot.CheckGoodsInfo();
            checkGoodsInfo.setStoreId(item.getStoreId());
            checkGoodsInfo.setGoodsInfoId(item.getSkuId());
            checkGoodsInfo.setSplitPrice(item.getPrice());
            return checkGoodsInfo;
        }).collect(Collectors.toList()));
        checkInfo.setBuyerId(request.getCustomerId());

        List<TradeCouponSnapshot> tradeCouponSnapshots;
        if (Objects.nonNull(request.getStoreId())) {
            tradeCouponSnapshots =
                    tradeCouponSnapshotRepository.findAllByBuyerIdAndGoodsInfos_StoreId(request.getCustomerId(),
                            request.getStoreId());
        } else {
            tradeCouponSnapshots =
                    tradeCouponSnapshotRepository.findAllByBuyerId(request.getCustomerId());
        }

        if (Objects.nonNull(tradeCouponSnapshots) && CollectionUtils.isNotEmpty(tradeCouponSnapshots)) {
            tradeCouponSnapshots.forEach(t->{
                tradeCouponSnapshotService.deleteTradeCouponSnapshot(t.getId());
            });
        }
        checkInfo.setId(UUIDUtil.getUUID());
        tradeCouponSnapshotService.addTradeCouponSnapshot(checkInfo);
        log.info("个人所有的优惠价2=========="+couponCodeVos);
        // 5.返回处理后的优惠券列表
//        if (CollectionUtils.isNotEmpty(request.getWareIds())){
//            Long aLong = request.getWareIds().stream().findFirst().orElse(null);
//            if (Objects.nonNull(aLong)){
//                couponCodeVos = couponCodeVos.stream().filter(v -> {
//                    String couponId = v.getCouponId();
//                    CouponInfo couponInfoById = couponInfoService.getCouponInfoById(couponId);
//                    if (Objects.nonNull(couponInfoById)) {
//                        Long wareId = couponInfoById.getWareId();
//                        if (wareId == -1 || wareId == aLong) {
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    } else {
//                        return false;
//                    }
//
//                }).collect(Collectors.toList());
//            }
//
//        }


        return couponCodeVos;
    }


    /**
     * 过滤出优惠券包含的商品列表
     *
     * @param tradeItems 待过滤商品列表
     * @param couponCode 优惠券信息
     * @param scopeList  优惠券作用范围
     * @return
     */
    public List<TradeItemInfo> listCouponSkuIds(List<TradeItemInfo> tradeItems, CouponCodeVO couponCode,
                                                List<CouponMarketingScope> scopeList) {
        Stream<TradeItemInfo> tradeItemsStream = tradeItems.stream();
        List<String> scopeIds = scopeList.stream().map(scope -> scope.getScopeId()).collect(Collectors.toList());
        switch (couponCode.getScopeType()) {
            case ALL: //全部商品
                if (couponCode.getPlatformFlag() != DefaultFlag.YES) {
                    tradeItemsStream =
                            tradeItemsStream.filter((item) -> couponCode.getStoreId().equals(item.getStoreId()));
                }
                break;
            case BRAND: //按品牌
                couponBrandName(couponCode, scopeList);
                if (couponCode.getPlatformFlag() == DefaultFlag.YES) {
                    tradeItemsStream = tradeItemsStream
                            .filter((item) -> scopeIds.contains(String.valueOf(item.getBrandId())));
                } else {
                    tradeItemsStream = tradeItemsStream
                            .filter((item) -> couponCode.getStoreId().equals(item.getStoreId()))
                            .filter((item) -> scopeIds.contains(String.valueOf(item.getBrandId())));
                }
                break;
            case BOSS_CATE: //按平台分类
                couponGoodsCateName(couponCode, scopeList);
                tradeItemsStream = tradeItemsStream
                        .filter((item) -> scopeIds.contains(String.valueOf(item.getCateId())));
                break;
            case STORE_CATE: //按店铺分类
                couponStoreCateName(couponCode, scopeList);
                tradeItemsStream = tradeItemsStream
                        .filter((item) -> !Collections.disjoint(scopeIds,
                                item.getStoreCateIds().stream().map(String::valueOf).collect(Collectors.toList()))
                        );
                break;
            case SKU: // 自定义货品
                tradeItemsStream = tradeItemsStream
                        .filter((item) -> scopeIds.contains(item.getSkuId()));
                break;
        }
        tradeItems = tradeItemsStream.collect(Collectors.toList());

        return tradeItems;
    }

    /**
     * 根据勾选的优惠券，返回不可用的平台券、以及优惠券实际优惠总额、每个店铺优惠总额
     */
    public CouponCheckoutResponse checkoutCoupons(CouponCheckoutRequest request) {
        List<TradeCouponSnapshot> snapshots;
        if (Objects.nonNull(request.getStoreId())) {
            snapshots = tradeCouponSnapshotRepository.findAllByBuyerIdAndGoodsInfos_StoreId(request.getCustomerId(),
                    request.getStoreId());
        } else {
            snapshots = tradeCouponSnapshotRepository.findAllByBuyerId(request.getCustomerId());
        }
        // TODO log
        log.info("snapshots:{}",JSON.toJSONString(snapshots));

        List<TradeCouponSnapshot.CheckCouponCode> couponCodes = null;
        List<TradeCouponSnapshot.CheckGoodsInfo> goodsInfos = null;
        if(Objects.nonNull(snapshots) && CollectionUtils.isNotEmpty(snapshots)){
            TradeCouponSnapshot snapshot = snapshots.get(0);
            couponCodes = snapshot.getCouponCodes();
            goodsInfos = snapshot.getGoodsInfos();
        }
        List<TradeCouponSnapshot.CheckGoodsInfo> finalGoodsInfos = goodsInfos;

        // TODO log
        log.info("couponCodes:{}",JSON.toJSONString(couponCodes));
        log.info("goodsInfos:{}",JSON.toJSONString(goodsInfos));

        // 1.将选择的店铺券产生的折扣价，均摊到相应的商品均摊价下，并计算优惠券优惠总价
        BigDecimal couponTotalPrice = couponCodes.stream()
                .filter(couponCode -> couponCode.getCouponType().equals(CouponType.STORE_VOUCHERS))
                .filter(couponCode -> request.getCouponCodeIds().contains(couponCode.getCouponCodeId()))
                .map(couponCode -> {
                    this.calcSplitPrice(finalGoodsInfos, couponCode);
                    return couponCode.getDenomination();
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
        // TODO log
        log.info("couponTotalPrice1:{}",couponTotalPrice);

        // 2.计算平台券是否还满足，返回不满足的平台券id
        List<String> unreachedIds = couponCodes.stream()
                .filter(couponCode -> couponCode.getCouponType().equals(CouponType.GENERAL_VOUCHERS))
                .filter(couponCode -> {
                    BigDecimal totalPrice = finalGoodsInfos.stream()
                            .filter(goodsInfo -> couponCode.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                            .map(match -> match.getSplitPrice())
                            .reduce(BigDecimal.ZERO, (total, splitPrice) -> total.add(splitPrice));
                    BigDecimal fullBuyPrice = couponCode.getFullBuyPrice();
                    fullBuyPrice = fullBuyPrice != null ? fullBuyPrice : BigDecimal.ZERO;
                    return totalPrice.compareTo(fullBuyPrice) == -1;
                })
                .map(couponCode -> couponCode.getCouponCodeId()).collect(Collectors.toList());
        // TODO log
        log.info("unreachedIds:{}",JSON.toJSONString(unreachedIds));

        // 3.将选择的平台券产生的折扣价，均摊到相应的商品均摊价下
        TradeCouponSnapshot.CheckCouponCode couponCode = couponCodes.stream()
                .filter(c -> c.getCouponType().equals(CouponType.GENERAL_VOUCHERS))
                .filter(c -> !unreachedIds.contains(c.getCouponCodeId()))
                .filter(c -> request.getCouponCodeIds().contains(c.getCouponCodeId())).findFirst()
                .orElse(null);

        // TODO log
        log.info("TradeCouponSnapshot.CheckCouponCode:{}",JSON.toJSONString(couponCode));

        if (couponCode != null) {
            this.calcSplitPrice(goodsInfos, couponCode);
            couponTotalPrice = couponTotalPrice.add(couponCode.getDenomination());
        }
        // TODO log
        log.info("couponTotalPrice2:{}",couponTotalPrice);

        BigDecimal totalPrice = goodsInfos.stream()
                .map(goodsInfo -> goodsInfo.getSplitPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CouponCheckoutResponse(
                unreachedIds,
                totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP),
                couponTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP),
                KsBeanUtil.convert(goodsInfos, CheckGoodsInfoVO.class));
    }

    private void calcSplitPrice(
            List<TradeCouponSnapshot.CheckGoodsInfo> goodsInfos,
            TradeCouponSnapshot.CheckCouponCode couponCode) {
        List<TradeCouponSnapshot.CheckGoodsInfo> matchs = goodsInfos.stream()
                .filter(goodsInfo -> couponCode.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                .collect(Collectors.toList());
        BigDecimal totalPrice = matchs.stream().map(match -> match.getSplitPrice())
                .reduce(BigDecimal.ZERO, (total, splitPrice) -> total.add(splitPrice));

        if (totalPrice.compareTo(couponCode.getDenomination()) == -1) {
            couponCode.setDenomination(totalPrice);
        }
        matchs.forEach(match -> {
            if (totalPrice.compareTo(BigDecimal.ZERO) != 0) {
                match.setSplitPrice(
                        match.getSplitPrice().subtract(
                                match.getSplitPrice()
                                        .multiply(couponCode.getDenomination())
                                        .divide(totalPrice, 8, BigDecimal.ROUND_FLOOR)
                        )
                );
            }
        });
    }

    /**
     * APP / H5 查询我的优惠券
     *
     * @param request
     * @return
     * @returncustomerFetchCoupon
     */
    public CouponCodeQueryResponse listMyCouponList(CouponCodePageRequest request) {
        CouponCodeCountResponse countResponse = this.queryCouponCodeCount(request);

        return CouponCodeQueryResponse.builder().couponCodeVos(this.pageMyCouponList(request))
                .unUseCount(countResponse.getUnUseCount())
                .usedCount(countResponse.getUsedCount())
                .overDueCount(countResponse.getOverDueCount()).build();
    }

    /**
     * PC - 分页查询我的优惠券
     *
     * @param request
     * @return
     */
    public Page<CouponCodeVO> pageMyCouponList(CouponCodePageRequest request) {

        Query query = entityManager.createNativeQuery(request.getQuerySql().concat(request.getQueryConditionSql()));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<CouponCodeVO> couponCodeVoList = CouponCodePageRequest.converter(query.getResultList());
        BaseResponse<ListStoreByIdsResponse> baseResponse =
                storeQueryProvider.listByIds(new ListStoreByIdsRequest(couponCodeVoList.stream().map(CouponCodeVO::getStoreId).collect(Collectors.toList())));
        List<StoreVO> storeVOList = baseResponse.getContext().getStoreVOList();
        for (CouponCodeVO couponCodeVO : couponCodeVoList) {
            for (StoreVO storeVO : storeVOList) {
                if (couponCodeVO.getStoreId().equals(storeVO.getStoreId())) {
                    couponCodeVO.setStoreName(storeVO.getStoreName());
                    couponCodeVO.setCompanyType(storeVO.getCompanyType());
                }
            }
        }
        //查询优惠券总数
        Query totalCountRes =
                entityManager.createNativeQuery(request.getQueryTotalCountSql().concat(request.getQueryConditionSql()));
        long totalCount = Long.parseLong(totalCountRes.getSingleResult().toString());

        couponCodeVoList.forEach(couponCodeVo -> {
            //判断优惠券是否即将过期 结束时间加上5天，大于现在时间，即将过期 true 是 false 否
            if (Objects.nonNull(couponCodeVo.getEndTime())) {
                if (LocalDateTime.now().plusDays(Constant.OUT_OF_DAYS).isAfter(couponCodeVo.getEndTime())) {
                    couponCodeVo.setNearOverdue(true);
                } else {
                    couponCodeVo.setNearOverdue(false);
                }
            }
            //是否可以立即使用 true 是(立即使用) false(查看可用商品)
            if (request.getUseStatus() == 0) {
                if (Objects.nonNull(couponCodeVo.getStartTime())) {
                    if (couponCodeVo.getStartTime().isBefore(LocalDateTime.now())) {
                        couponCodeVo.setCouponCanUse(true);
                    } else {
                        couponCodeVo.setCouponCanUse(false);
                    }
                }
            }

            //优惠券商品范围
            List<CouponMarketingScope> scopeList = couponMarketingScopeRepository.findByCouponId(couponCodeVo
                    .getCouponId());
            //适用品牌
            if (ScopeType.BRAND.equals(couponCodeVo.getScopeType())) {
                couponBrandName(couponCodeVo, scopeList);
            }

            //适用平台分类
            if (ScopeType.BOSS_CATE.equals(couponCodeVo.getScopeType())) {
                couponGoodsCateName(couponCodeVo, scopeList);
            }

            //适用店铺分类
            if (ScopeType.STORE_CATE.equals(couponCodeVo.getScopeType())) {
                couponStoreCateName(couponCodeVo, scopeList);
            }
        });
        return new PageImpl<>(couponCodeVoList, request.getPageable(), totalCount);
    }

    /**
     * APP | H5 我的优惠券 查询优惠券总数
     *
     * @param request
     * @return 优惠券总数
     */
    private CouponCodeCountResponse queryCouponCodeCount(CouponCodePageRequest request) {
        //查询未使用的优惠券总数
        Query queryUnusedCount = entityManager.createNativeQuery(request.getQueryUnUseCountSql());
        long unUsedCount = Long.parseLong(queryUnusedCount.getSingleResult().toString());
        //查询已使用的优惠券总数
        Query queryUsedCount = entityManager.createNativeQuery(request.getQueryUsedCountSql());
        long usedCount = Long.parseLong(queryUsedCount.getSingleResult().toString());
        //查询已过期的优惠券总数
        Query queryOverDueCount = entityManager.createNativeQuery(request.getQueryOverDueCountSql());
        long overDueCount = Long.parseLong(queryOverDueCount.getSingleResult().toString());

        return CouponCodeCountResponse.builder().unUseCount(unUsedCount).usedCount(usedCount).overDueCount(overDueCount).build();
    }

    /**
     * 用户领取优惠券
     *
     * @param couponFetchRequest
     */
    public void customerFetchCoupon(CouponFetchRequest couponFetchRequest) {
        //优惠券是否存在
        CouponInfo couponInfo = couponInfoService.getCouponInfoById(couponFetchRequest.getCouponInfoId());
        if (couponInfo == null) {
            throw new SbcRuntimeException(CouponErrorCode.COUPON_INFO_NOT_EXIST);
        }
        if (Objects.nonNull(couponFetchRequest.getStoreId())) {
            if (!Objects.equals(couponInfo.getStoreId(), couponFetchRequest.getStoreId())) {
                throw new SbcRuntimeException(CouponErrorCode.COUPON_INFO_NOT_EXIST);
            }
        }
        //优惠券活动是否存在
        CouponActivity couponActivity = couponActivityService.getCouponActivityByPk(couponFetchRequest
                .getCouponActivityId());
        if (couponActivity == null) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_EXIST);
        }
        if (Objects.nonNull(couponFetchRequest.getStoreId())) {
            if (!Objects.equals(couponActivity.getStoreId(), couponFetchRequest.getStoreId())) {
                throw new SbcRuntimeException(CouponErrorCode.COUPON_INFO_NOT_EXIST);
            }
        }
        //优惠券活动还未开始
        if (couponActivity.getStartTime().isAfter(LocalDateTime.now())) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_INUSE);
        }
        //优惠券活动已经过期
        if (couponActivity.getEndTime().isBefore(LocalDateTime.now())) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_INUSE);
        }
        // 暂停
        if (couponActivity.getPauseFlag() == DefaultFlag.YES) { // 暂停
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_NOT_INUSE);
        }
        //优惠券规则查询
        CouponActivityConfig couponActivityConfig = couponActivityConfigService
                .queryByActivityIdAndCouponId(couponFetchRequest.getCouponActivityId(), couponFetchRequest
                        .getCouponInfoId());
        if (couponActivityConfig == null) {
            throw new SbcRuntimeException(CouponErrorCode.ACTIVITY_COUPON_CONFIG_NOT_EXIST);
        }
        //用户是否存在
//        Customer customer = customerService.findById(couponFetchRequest.getCustomerId());
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(couponFetchRequest
                        .getCustomerId())).getContext();
        if (customer == null) {
            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_NOT_EXIST);
        }
        if (StringUtils.isNotBlank(customer.getParentCustomerId())){
            throw new SbcRuntimeException(CouponErrorCode.CHILD_CANT_GET);
        }
        //获取用户等级，判断用户等级是否满足要求
        if (!couponActivity.getJoinLevel().equals(MarketingJoinLevel.ALL_CUSTOMER.toValue() + "")) {
//            CustomerLevel customerLevel = customerLevelService.findLevelByCustomer(customer.getCustomerId(),
//            couponActivity.getStoreId());
            CustomerLevelByCustomerIdAndStoreIdResponse customerLevel =
                    customerLevelQueryProvider.getCustomerLevelByCustomerIdAndStoreId(new
                            CustomerLevelByCustomerIdAndStoreIdRequest(customer.getCustomerId(),
                            couponActivity.getStoreId())
                    ).getContext();
            if (couponActivity.getJoinLevel().equals(MarketingJoinLevel.ALL_LEVEL.toValue() + "")) {
                if (customerLevel == null) {
                    throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_CAN_NOT_FETCH_COUPON_INFO);
                }
            } else {
                if (Objects.isNull(customerLevel) || !Arrays.asList(couponActivity.getJoinLevel().split(",")).contains(customerLevel
                        .getLevelId() + "")) {
                    throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_CAN_NOT_FETCH_COUPON_INFO);
                }
            }
        }
        /**
         * 注释的原因：http://192.168.1.15:81/zentao/bug-view-3760.html
         * 修复：不限次数领取，用户领券一张后不能再领取
         */
        //判断重复领取
//        List<CouponCode> couponCodeList =
//                listCouponCodeByCondition(CouponCodeQueryRequest.builder().couponId(couponFetchRequest.getCouponInfoId()).activityId(couponFetchRequest.getCouponActivityId())
//                        .customerId(couponFetchRequest.getCustomerId()).delFlag(DeleteFlag.NO).useStatus(DefaultFlag.NO).notExpire(true)
//                        .build());
//        if (CollectionUtils.isNotEmpty(couponCodeList)) {
//            throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_HAS_FETCHED_COUPON);
//        }
        //判断领取次数
        if (Objects.equals(couponActivity.getReceiveType(), DefaultFlag.YES)) {
            int countCustomerHasFetchedCoupon =
                    couponCodeRepository.countCustomerHasFetchedCoupon(couponFetchRequest.getCustomerId(),
                            couponFetchRequest.getCouponInfoId(), couponFetchRequest.getCouponActivityId());
            if (couponActivity.getReceiveCount() - countCustomerHasFetchedCoupon <= 0) {
                throw new SbcRuntimeException(CouponErrorCode.CUSTOMER_FETCHED_ALL);
            }
        }
        String redisKey = getCouponBankKey(couponActivityConfig.getActivityId(), couponActivityConfig.getCouponId());

        // 获取剩余优惠券
        long leftCount = refreshCouponLeftCount(couponActivityConfig);
        if (leftCount == 0) {
            throw new SbcRuntimeException(CouponErrorCode.COUPON_INFO_NO_LEFT);
        }

        SessionCallback<List<Object>> callback = new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations)
                    throws DataAccessException {
                int retryLimit = 10;
                List<Object> results = null;
                int i = 0;
                while ((results == null || results.isEmpty()) && i < retryLimit) {
                    operations.watch(redisKey);
                    // Read
                    String origin = (String) operations.opsForValue().get(redisKey);
                    if (Long.parseLong(origin) <= 0) {
                        couponNoLeft(couponActivityConfig);
                        throw new SbcRuntimeException(CouponErrorCode.COUPON_INFO_NO_LEFT);
                    }
                    operations.multi();
                    // Write
                    operations.opsForValue().increment(redisKey, -1);
                    results = operations.exec();
                    if (results != null && !results.isEmpty()) {
                        //领券业务代码
                        sendCouponCode(couponInfo, couponFetchRequest);
                    } else {
                        throw new SbcRuntimeException(CouponErrorCode.COUPON_INFO_NO_LEFT);
                    }
                    i++;
                }
                return results;
            }
        };
        redisTemplate.execute(callback);
    }


    /**
     * 标记优惠券已经被抢光
     *
     * @param couponActivityConfig
     */
    private void couponNoLeft(CouponActivityConfig couponActivityConfig) {
        // 更新mysql
        couponActivityConfig.setHasLeft(DefaultFlag.NO);
        couponActivityConfigService.updateByPk(couponActivityConfig);
        // 删除redis
        redisService.delete(getCouponBankKey(couponActivityConfig.getActivityId(), couponActivityConfig.getCouponId()));
        // 更新缓存
        couponCacheService.updateCouponLeftCache(couponActivityConfig.getActivityConfigId(), DefaultFlag.NO);
    }


    /**
     * 刷新优惠券库存
     *
     * @param couponActivityConfig
     * @return
     */
    private long refreshCouponLeftCount(CouponActivityConfig couponActivityConfig) {
        String redisKey = getCouponBankKey(couponActivityConfig.getActivityId(), couponActivityConfig.getCouponId());
        String couponCountCache = redisService.getString(redisKey);
        //优惠券状态 - 是否被抢完
        if (couponActivityConfig.getHasLeft() == DefaultFlag.NO) {
            return 0L;
        }
        //redis中不存在，需要重新加载
        if (couponCountCache == null) {
            int couponUsedCount = couponCodeRepository.countCouponUsed(couponActivityConfig.getCouponId(),
                    couponActivityConfig.getActivityId());
            if (couponUsedCount == couponActivityConfig.getTotalCount()) {
                this.couponNoLeft(couponActivityConfig);
                return 0L;
            } else {
                long leftCount = couponActivityConfig.getTotalCount() - couponUsedCount;
                redisService.setString(redisKey, leftCount + "");
                return leftCount;
            }
        } else if (Long.parseLong(couponCountCache) == 0L) {
            this.couponNoLeft(couponActivityConfig);
            return 0L;
        } else {
            return Long.parseLong(couponCountCache);
        }
    }

    /**
     * 获取优惠券库存 -- 不精确
     *
     * @param activityId
     * @param couponId
     * @param totalCount
     * @param hasLeft
     * @return
     */
    public long getCouponLeftCount(String activityId, String couponId, Long totalCount, DefaultFlag hasLeft) {
        String redisKey = getCouponBankKey(activityId, couponId);
        String couponCountCache = redisService.getString(redisKey);
        //优惠券状态 - 是否被抢完
        if (hasLeft == DefaultFlag.NO) {
            return 0L;
        }
        //redis中不存在，需要重新加载
        if (couponCountCache == null) {
            //因为优惠券的数量不再缓存里面，要么是缓存丢失，要么是优惠券未被领取过，数量并不要求那么精确，返回totalCount
            return totalCount;
        } else if (Long.parseLong(couponCountCache) == 0L) {
            return 0L;
        } else {
            return Long.parseLong(couponCountCache);
        }
    }

    /**
     * 批量获取优惠券的领取状态
     *
     * @param couponCacheList
     * @return <优惠券活动Id，<优惠券Id, 领取状态>>
     */
    public Map<String, Map<String, CouponLeftResponse>> mapLeftCount(List<CouponCache> couponCacheList) {
        if (CollectionUtils.isEmpty(couponCacheList)) {
            return null;
        }
        Map<String, Map<String, CouponLeftResponse>> result = new HashMap<>();
        couponCacheList.forEach(item -> {
            Map<String, CouponLeftResponse> itemMap = result.computeIfAbsent(item.getCouponActivityId(),
                    k -> new HashMap<>());
            itemMap.put(item.getCouponInfoId(),
                    CouponLeftResponse.builder()
                            .leftCount(getCouponLeftCount(item.getCouponActivityId(), item.getCouponInfoId(),
                                    item.getTotalCount(), item.getHasLeft()))
                            .totalCount(item.getTotalCount()).build()
            );
        });
        return result;
    }


    /**
     * 批量获取优惠券领用状态
     *
     * @param couponCacheList
     * @return <优惠券活动Id，<优惠券Id, 领取状态>>
     */
    public Map<String, Map<String, Boolean>> mapFetchStatus(List<CouponCache> couponCacheList, String customerId) {
        if (CollectionUtils.isEmpty(couponCacheList)) {
            return null;
        }
        Map<String, Map<String, Boolean>> result = new HashMap<>();
        List<CouponCode> couponCodeList = new ArrayList<>();
        if (StringUtils.isNotBlank(customerId)) {
            couponCodeList = listCouponCodeByCondition(CouponCodeQueryRequest.builder()
                    .customerId(customerId).delFlag(DeleteFlag.NO).useStatus(DefaultFlag.NO).notExpire(true)
                    .build());
        }
        Map<String, List<CouponCode>> couponCodeMap = couponCodeList.stream().collect(Collectors.groupingBy(
                item -> item.getActivityId().concat("_").concat(item.getCouponId())));
        couponCacheList.forEach(item -> {
            Map<String, Boolean> itemMap = result.computeIfAbsent(item.getCouponActivityId(), k -> new HashMap<>());
            if (CollectionUtils.isNotEmpty(couponCodeMap.get(item.getCouponActivityId().concat("_").concat(item.getCouponInfoId())))) {
                itemMap.put(item.getCouponInfoId(), true);
            } else {
                itemMap.put(item.getCouponInfoId(), false);
            }
        });
        return result;
    }

    /**
     * 批量修改优惠券
     */
    @Transactional
    public void batchModify(List<CouponCodeBatchModifyDTO> requests) {
        List<CouponCode> couponCodes = couponCodeRepository.findByIds(
                requests.stream().map(item -> item.getCouponCodeId()).collect(Collectors.toList()));
        couponCodes.forEach(couponCode -> {
            CouponCodeBatchModifyDTO request = requests.stream().filter(
                    r -> couponCode.getCouponCodeId().equals(r.getCouponCodeId())).findFirst().orElse(null);
            couponCode.setUpdateTime(LocalDateTime.now());
            couponCode.setUseStatus(request.getUseStatus());
            couponCode.setOrderCode(request.getOrderCode());
        });
        couponCodeRepository.saveAll(couponCodes);
    }


    /**
     * 优惠券商品品牌信息
     *
     * @param couponCodeVo
     * @param scopeList
     */
    private void couponBrandName(CouponCodeVO couponCodeVo, List<CouponMarketingScope> scopeList) {
        //优惠券品牌信息
        if (CollectionUtils.isNotEmpty(scopeList) && Objects.equals(couponCodeVo.getScopeType(), ScopeType.BRAND)) {
            //优惠券包含的所有品牌Id
            List<Long> brandsIds = scopeList.stream().map(scope -> Long.valueOf(scope.getScopeId())).collect
                    (Collectors.toList());
            GoodsBrandListRequest brandRequest = new GoodsBrandListRequest();
            brandRequest.setBrandIds(brandsIds);
            List<GoodsBrandVO> brandList =
                    goodsBrandQueryProvider.list(brandRequest).getContext().getGoodsBrandVOList();
            couponCodeVo.setBrandNames(brandList.stream().map(GoodsBrandVO::getBrandName).collect(Collectors.toList()));
        }
    }

    /**
     * 优惠券适用平台分类
     *
     * @param couponCodeVo
     * @param scopeList
     */
    private void couponGoodsCateName(CouponCodeVO couponCodeVo, List<CouponMarketingScope> scopeList) {
        if (CollectionUtils.isNotEmpty(scopeList) && Objects.equals(couponCodeVo.getScopeType(), ScopeType.BOSS_CATE)) {
            //优惠券包含的所有品牌Id
            List<Long> cateIds = scopeList.stream().map(scope -> Long.valueOf(scope.getScopeId())).collect(Collectors
                    .toList());
            //平台分类
            if (Objects.equals(couponCodeVo.getPlatformFlag(), DefaultFlag.YES)) {
                List<GoodsCateVO> cateList =
                        goodsCateQueryProvider.getByIds(new GoodsCateByIdsRequest(cateIds)).getContext().getGoodsCateVOList();
                couponCodeVo.setGoodsCateNames(cateList.stream().map(GoodsCateVO::getCateName).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 优惠券适用店铺分类
     *
     * @param couponCodeVo
     * @param scopeList
     */
    private void couponStoreCateName(CouponCodeVO couponCodeVo, List<CouponMarketingScope> scopeList) {
        if (CollectionUtils.isNotEmpty(scopeList) && Objects.equals(couponCodeVo.getScopeType(), ScopeType
                .STORE_CATE)) {
            //优惠券包含的所有品牌Id
            List<Long> cateIds = scopeList.stream().map(scope -> Long.valueOf(scope.getScopeId())).collect(Collectors
                    .toList());
            //店铺分类
            if (Objects.equals(couponCodeVo.getPlatformFlag(), DefaultFlag.NO)) {
                List<StoreCateVO> storeCateList =
                        storeCateQueryProvider.listByIds(new StoreCateListByIdsRequest(cateIds)).getContext().getStoreCateVOList();
                couponCodeVo.setStoreCateNames(storeCateList.stream().map(cate -> cate.getCateName()).collect(Collectors
                        .toList()));
            }
        }
    }

    /**
     * 发放优惠券码
     *
     * @return
     */
    @Transactional
    public CouponCode sendCouponCode(CouponInfo couponInfo, CouponFetchRequest couponFetchRequest) {
        CouponCode couponCode = new CouponCode();
        couponCode.setCouponCode(CodeGenUtil.toSerialCode(RandomUtils.nextInt(1, 10000)).toUpperCase());
        couponCode.setCouponId(couponFetchRequest.getCouponInfoId());
        couponCode.setActivityId(couponFetchRequest.getCouponActivityId());
        couponCode.setCustomerId(couponFetchRequest.getCustomerId());
        couponCode.setUseStatus(DefaultFlag.NO);
        couponCode.setAcquireTime(LocalDateTime.now());
        if (Objects.equals(RangeDayType.RANGE_DAY, couponInfo.getRangeDayType())) {//优惠券的起止时间
            couponCode.setStartTime(couponInfo.getStartTime());
            couponCode.setEndTime(couponInfo.getEndTime());
        } else {//领取生效
            couponCode.setStartTime(LocalDateTime.now());
            couponCode.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(couponInfo.getEffectiveDays()).minusSeconds(1));
        }
        couponCode.setDelFlag(DeleteFlag.NO);
        couponCode.setCreateTime(LocalDateTime.now());
        couponCode.setCreatePerson(couponFetchRequest.getCustomerId());
        CouponCode sendCouponCode = couponCodeRepository.save(couponCode);
        return sendCouponCode;
    }

    /**
     * 获取优惠券库存key
     *
     * @param activityId 活动
     * @param couponId   优惠券
     * @return
     */
    private String getCouponBankKey(String activityId, String couponId) {
        return CouponCodeService.COUPON_BANK.concat(activityId).concat("_").concat(couponId);
    }

    /**
     * 退优惠券
     *
     * @param couponCodeId
     */
    @Transactional
    @LcnTransaction
    public void returnCoupon(String couponCodeId,String customerId) {
        CouponCode couponCodeById = couponCodeRepository.findByCouponCodeId(couponCodeId);
        CouponCode couponCode = KsBeanUtil.convert(couponCodeById, CouponCode.class);
        //查询该customer有没有主账号，如果没有主账号信息，则退回给自己
        CustomerGetByIdRequest request = new CustomerGetByIdRequest();
        request.setCustomerId(customerId);
        CustomerGetByIdResponse context = customerQueryProvider.getCustomerById(request).getContext();
        if (Objects.nonNull(couponCode) && Objects.nonNull(context)) {
            if (StringUtils.isBlank(context.getParentCustomerId())) {
                couponCode.setCustomerId(customerId);
                if (couponCode.getUseStatus() == DefaultFlag.YES) {
                    couponCode.setOrderCode("");
                    couponCode.setUseStatus(DefaultFlag.NO);
                    couponCode.setUseDate(null);
                    couponCodeRepository.save(couponCode);
                }
            } else  {
                couponCode.setCustomerId(context.getParentCustomerId());
                if (couponCode.getUseStatus() == DefaultFlag.YES) {
                    couponCode.setOrderCode("");
                    couponCode.setUseStatus(DefaultFlag.NO);
                    couponCode.setUseDate(null);
                    couponCode.setCouponCodeId(null);
                    couponCodeRepository.save(couponCode);
                    couponCodeRepository.deleteById(couponCodeId);
                }
            }
        }
    }

    /**
     * 功能描述: 子账号绑定主账号，优惠券归于主账号
     */
    @LcnTransaction
    @Transactional
    public void bindAccount(String parentId,CouponCodeQueryRequest request){
        List<CouponCode> couponCodeList = couponCodeRepository.findAll(this.getWhereCriteria(request));
        if (CollectionUtils.isNotEmpty(couponCodeList)){
            for (CouponCode inner:couponCodeList){
                inner.setCustomerId(parentId);
            }
            List<String> couponCodeIds = couponCodeList.stream().map(CouponCode::getCouponCodeId).collect(Collectors.toList());
            List<CouponCode> list = KsBeanUtil.convert(couponCodeList,CouponCode.class);
            //置空id，重新生成再次根据更具customerid分片，之后删除元数据
            list =  list.stream().peek(couponCode -> couponCode.setCouponCodeId(null)).collect(Collectors.toList());
            int size = couponCodeRepository.saveAll(list).size();
            if (size == couponCodeIds.size()){
                couponCodeRepository.deleteByCouponCodeIdsIn(couponCodeIds);
            }
        }
    }

    /**
     * 对同一个用户，批量发放优惠券
     *
     * @param couponInfoList
     * @param customerId
     * @param couponActivityId
     * @return
     */
    @Transactional
    @LcnTransaction
    public List<CouponCode> sendBatchCouponCodeByCustomerLongNotOrder(List<GetCouponGroupResponse> couponInfoList,
                                                                      String customerId, String couponActivityId) {

        List<CouponCode> couponCodeList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        couponInfoList.forEach(item -> {
            CouponCode couponCode = new CouponCode();
            couponCode.setCouponCode(CodeGenUtil.toSerialCode(RandomUtils.nextInt(1, 10000)).toUpperCase());
            couponCode.setCouponId(item.getCouponId());
            couponCode.setActivityId(couponActivityId);
            couponCode.setCustomerId(customerId);
            couponCode.setUseStatus(DefaultFlag.NO);
            couponCode.setAcquireTime(LocalDateTime.now());
            if (Objects.equals(RangeDayType.RANGE_DAY, item.getRangeDayType())) {//优惠券的起止时间
                couponCode.setStartTime(item.getStartTime());
                couponCode.setEndTime(item.getEndTime());
            } else {//领取生效
                couponCode.setStartTime(now);
                couponCode.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(item.getEffectiveDays()).minusSeconds(1));
            }
            couponCode.setDelFlag(DeleteFlag.NO);
            couponCode.setCreateTime(LocalDateTime.now());
            couponCode.setCreatePerson(customerId);
            couponCodeList.add(couponCode);
        });
        return couponCodeRepository.saveAll(couponCodeList);
    }

    /**
     * 对同一个用户，批量发放优惠券
     *
     * @param couponInfoList
     * @param customerId
     * @param couponActivityId
     * @return
     */
    @Transactional
    @LcnTransaction
    public List<CouponCode> sendBatchCouponCodeByCustomer(List<GetCouponGroupResponse> couponInfoList,
                                                          String customerId, String couponActivityId) {
        List<CouponCode> couponCodeList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        couponInfoList.forEach(item -> {
            for (int i = 0; i < item.getTotalCount(); i++) {
                CouponCode couponCode = new CouponCode();
                couponCode.setCouponCode(CodeGenUtil.toSerialCode(RandomUtils.nextInt(1, 10000)).toUpperCase());
                couponCode.setCouponId(item.getCouponId());
                couponCode.setActivityId(couponActivityId);
                couponCode.setCustomerId(customerId);
                couponCode.setUseStatus(DefaultFlag.NO);
                couponCode.setAcquireTime(LocalDateTime.now());
                if (Objects.equals(RangeDayType.RANGE_DAY, item.getRangeDayType())) {//优惠券的起止时间
                    couponCode.setStartTime(item.getStartTime());
                    couponCode.setEndTime(item.getEndTime());
                } else {//领取生效
                    couponCode.setStartTime(now);
                    couponCode.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(item.getEffectiveDays()).minusSeconds(1));
                }
                couponCode.setDelFlag(DeleteFlag.NO);
                couponCode.setCreateTime(LocalDateTime.now());
                couponCode.setCreatePerson(customerId);
                couponCodeList.add(couponCode);
            }
        });
        return couponCodeRepository.saveAll(couponCodeList);
    }

    /**
     * 批量发券，根据会员id和活动id
     */
    public List<String> sendBatchCouponCodeByCustomerList( CouponCodeBatchSendCouponRequest request){
        List<String> customerIdList = request.getCustomerIds();

        if(CollectionUtils.isNotEmpty(request.getList()) && CollectionUtils.isNotEmpty(customerIdList)){

            for(CouponActivityConfigAndCouponInfoDTO activity : request.getList()) {
                // 查询券礼包权益关联的优惠券活动配置列表
                List<CouponActivityConfig> couponActivityConfigList = couponActivityConfigService.queryByActivityId(activity.getActivityId());
                // 根据配置查询需要发放的优惠券列表
                List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().map(
                        CouponActivityConfig::getCouponId).collect(Collectors.toList()));
                // 组装优惠券发放数据
                List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList, GetCouponGroupResponse.class);
                getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> couponActivityConfigList.forEach(config -> {
                    if (item.getCouponId().equals(config.getCouponId())) {
                        item.setTotalCount(config.getTotalCount());
                    }
                })).collect(Collectors.toList());
                for(String customerId : customerIdList) {

                    // 批量发放优惠券
                    sendBatchCouponCodeByCustomer(getCouponGroupResponse, customerId, activity.getActivityId());
                }
            }
        }
       return customerIdList;

    }

    /**
     * @param customerId
     * @return
     */
    public Integer countByCustomerId(String customerId) {
        return couponCodeRepository.countByCustomerId(customerId);
    }

    /**
     * 根据customerId和activityId查询优惠券数量
     *
     * @param customerId
     * @param activieyId
     * @return
     */
    public Integer countByCustomerIdAndActivityId(String customerId, String activieyId) {
        return couponCodeRepository.countByCustomerIdAndActivityId(customerId, activieyId);
    }


    /**
     * 精准发券
     *
     * @param customerIds
     * @param list
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void precisionVouchers(List<String> customerIds, List<CouponActivityConfigAndCouponInfoDTO> list) {
        if (CollectionUtils.isEmpty(customerIds)) {
            log.info("=============精准发券用户ID集合不存在===========================");
            return;
        }
        CouponInfoDTO couponInfoDTO;
        CouponCode couponCode;
        int resultSize = customerIds.size() * list.size();
        List<CouponCode> result = new ArrayList<>(resultSize);
        for (String customerId : customerIds) {
            for (CouponActivityConfigAndCouponInfoDTO dto : list) {
                Long totalCount = dto.getTotalCount();
                String couponId = dto.getCouponId();
                String activityId = dto.getActivityId();
                couponInfoDTO = dto.getCouponInfoDTO();
                for (long i = 0; i < totalCount; i++) {
                    couponCode = new CouponCode();
                    couponCode.setCouponCode(CodeGenUtil.toSerialCode(RandomUtils.nextInt(1, 10000)).toUpperCase());
                    couponCode.setCouponId(couponId);
                    couponCode.setActivityId(activityId);
                    couponCode.setCustomerId(customerId);
                    couponCode.setUseStatus(DefaultFlag.NO);
                    couponCode.setAcquireTime(LocalDateTime.now());
                    //优惠券的起止时间
                    if (RangeDayType.RANGE_DAY == couponInfoDTO.getRangeDayType()) {
                        couponCode.setStartTime(couponInfoDTO.getStartTime());
                        couponCode.setEndTime(couponInfoDTO.getEndTime());
                    } else {//领取生效
                        couponCode.setStartTime(LocalDateTime.now());
                        couponCode.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(couponInfoDTO.getEffectiveDays()).minusSeconds(1));
                    }
                    couponCode.setDelFlag(DeleteFlag.NO);
                    couponCode.setCreateTime(LocalDateTime.now());
                    couponCode.setCreatePerson(customerId);
                    result.add(couponCode);
                }
            }
        }
        couponCodeRepository.saveAll(result);
    }

    /**
     * 查询领券记录
     */
    public MicroServicePage<CouponInfoGetRecordVO> getRecord(CouponCodeBuildRequest request){
        Query query = entityManager.createNativeQuery(request.getQuerySql().concat(request.getQueryConditionSql()));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<CouponInfoGetRecordVO> couponInfoGetRecordVOList = CouponCodeBuildRequest.converter(query.getResultList());
        log.info("couponInfoGetRecordVOList:"+ JSON.toJSONString(couponInfoGetRecordVOList));
        //查询领券记录总数 用作分页，coupon_code sharding分片，不能用子查询；后续改造也用数某做报表
        Query queryCount = entityManager.createNativeQuery(request.getQueryTotalCountSql().concat(request.getQueryConditionSql()));
        queryCount.unwrap(SQLQuery.class).setResultTransformer(Transformers.TO_LIST);
        List<Long> countList = queryCount.getResultList();
        Integer totalCount = countList.size();

        return new MicroServicePage<>(couponInfoGetRecordVOList,request.getPageable(),totalCount);
    }

    /**
     * 根据codeIds查询优惠券couponIds集合
     */
    public List<CouponCode> findCouponsByCouponCodeIds(List<String> codeIds){

        return couponCodeRepository.findByIds(codeIds);

    }

    /**
     * 通过用户id及优惠券id删除优惠券码
     * @param customerId
     * @param couponCodeIds
     */
    public void delCouponCode(String customerId,List<String> couponCodeIds) {
        List<CouponCode> couponCodes = this.findCouponsByCouponCodeIds(couponCodeIds);
        if (CollectionUtils.isNotEmpty(couponCodes)) {
            couponCodes.forEach(couponCode -> {
                couponCode.setDelFlag(DeleteFlag.YES);
                couponCode.setDelPerson(customerId);
                couponCode.setDelTime(LocalDateTime.now());
            });
            couponCodeRepository.saveAll(couponCodes);
        }
    }

    /**
     * 撤回删除
     * @param couponCodeIds
     */
    public void recallDelCouponCode(List<String> couponCodeIds) {
        List<CouponCode> couponCodes = this.findCouponsByCouponCodeIds(couponCodeIds);
        if (CollectionUtils.isNotEmpty(couponCodes)) {
            couponCodes.forEach(couponCode -> {
                couponCode.setDelTime(null);
                couponCode.setDelPerson("");
                couponCode.setDelFlag(DeleteFlag.NO);
            });
        }
        couponCodeRepository.saveAll(couponCodes);
    }
}
