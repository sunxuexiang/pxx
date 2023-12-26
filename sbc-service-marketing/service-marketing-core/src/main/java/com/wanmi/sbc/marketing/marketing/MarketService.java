package com.wanmi.sbc.marketing.marketing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingBatchAddRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingDeleteByMaketingIdAndGoodsInfoIdsRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingListByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCardGroupRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingByGoodsInfoIdAndIdResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingGroupCardResponse;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.repository.MarketingScopeRepository;
import com.wanmi.sbc.marketing.common.request.SkuExistsRequest;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.service.MarketingScopeService;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.discount.repository.MarketingFullDiscountLevelRepository;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftDetailRepository;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftLevelRepository;
import com.wanmi.sbc.marketing.marketing.cache.MarketingInfoCacheSupport;
import com.wanmi.sbc.marketing.marketing.price.MarketingCaculatorData;
import com.wanmi.sbc.marketing.marketing.price.MarketingCalculator;
import com.wanmi.sbc.marketing.marketing.price.MarketingCalculatorFactory;
import com.wanmi.sbc.marketing.marketing.price.MarketingCalculatorResult;
import com.wanmi.sbc.marketing.marketing.strategy.MarketStrategyFacory;
import com.wanmi.sbc.marketing.marketing.strategy.MarketingStrategy;
import com.wanmi.sbc.marketing.marketing.strategy.check.MarketingCheckResult;
import com.wanmi.sbc.marketing.marketingpurchaselimit.Service.MarketingPurchaseLimitService;
import com.wanmi.sbc.marketing.marketingpurchaselimit.model.root.MarketingPurchaseLimit;
import com.wanmi.sbc.marketing.redis.*;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import com.wanmi.sbc.marketing.reduction.repository.MarketingFullReductionLevelRepository;
import com.wanmi.sbc.marketing.util.JsonUtil;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MarketService{

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private MarketingFullGiftDetailRepository marketingFullGiftDetailRepository;

    @Autowired
    private MarketingScopeRepository marketingScopeRepository;

    @Autowired
    private MarketingFullDiscountLevelRepository marketingFullDiscountLevelRepository;

    @Autowired
    MarketingFullReductionLevelRepository marketingFullReductionLevelRepository;

    @Autowired
    MarketingFullGiftLevelRepository marketingFullGiftLevelRepository;

    @Autowired
    private GoodsMarketingQueryProvider goodsMarketingQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    MarketingScopeService marketingScopeService;

    @Autowired
    private GoodsMarketingProvider goodsMarketingProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MarketingPurchaseLimitService marketingPurchaseLimitService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Transactional
    public Marketing saveOrUpdateMarketing(SaveOrUpdateMarketingRequest request) throws SbcRuntimeException {
        // 新增营销活动
        if(Objects.isNull(request.getMarketingId())){
            return saveMarketing(request);
        }
        // 编辑营销活动
        return updateMarketing(request);
    }

    //满赠增加一个赠品
    @Transactional
    public List<String> addActivityGiveGoods(AddActivityGiveGoodsRequest request){
        List<String> productIds = request.getAddActivitGoodsRequest().stream().map(o -> o.getProductId()).collect(Collectors.toList());

        List<MarketingFullGiftDetail> byMarketingIdAndProductIdIn = marketingFullGiftDetailRepository.findByMarketingIdAndProductIdIn(request.getMarketingId(), productIds);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(byMarketingIdAndProductIdIn)){
            return byMarketingIdAndProductIdIn.stream().map(o -> o.getProductId()).collect(Collectors.toList());
        }

        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + request.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", request.getMarketingId());
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }
        try {
            request.getAddActivitGoodsRequest().forEach(var->{
                MarketingFullGiftDetail detail = new MarketingFullGiftDetail();
                BeanUtils.copyProperties(var,detail);
                detail.setGiftLevelId(request.getGiftLevelId());
                detail.setMarketingId(request.getMarketingId());
                detail.setTerminationFlag(BoolFlag.NO);

                marketingFullGiftDetailRepository.save(detail);
                if(Objects.nonNull(var.getBoundsNum()) && var.getBoundsNum() > 0){
                    this.updateMarketingGiftNum(request.getMarketingId().toString(),request.getGiftLevelId().toString(),var.getProductId(),var.getBoundsNum());
                }
            });
            Marketing marketing = marketingRepository.findById(request.getMarketingId()).orElse(null);
            if(Objects.isNull(marketing)){
                throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
            }
            // 写入Redis缓存中，更新Redis
            this.clearMarketingCache(marketing);
            this.updateMarketingCache(marketing);

        } catch (Exception e){
            log.info("DeleteMarketingById exception", e);
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }

        return new ArrayList<>();
    }

    //营销活动修改reids数量
    public void updateMarketingGiftNum(String markingId,String levelId,String goodsInfoId,Long num){
        String key = markingId+levelId+goodsInfoId;
        if (!redisService.hasKey(key)){
            this.setMarketingGiftNum(markingId,levelId,goodsInfoId,num);
        }else {
            MarketingFullGiftDetail marketingFullGiftDetail = marketingFullGiftDetailRepository.findByMarketingIdAndGiftLevelId(Long.parseLong(markingId), Long.parseLong(levelId))
                    .stream().filter(v -> {
                        if (v.getProductId().equalsIgnoreCase(goodsInfoId)) {
                            return true;
                        }
                        return false;
                    }).findFirst().orElse(null);
            if (Objects.isNull(marketingFullGiftDetail)){
                throw new SbcRuntimeException("数据异常，未查询到对应的营销活动赠品信息");
            }
            if (Objects.isNull(marketingFullGiftDetail.getBoundsNum())){
                throw new SbcRuntimeException("数据异常，赠品数量为空");
            }

            this.setMarketingGiftNum(markingId,levelId,goodsInfoId,num);
        }
    }

    //营销活动赠品添加redis没有过期时间
    public void setMarketingGiftNum(String markingId,String levelId,String goodsInfoId,Long num){
        String key = markingId+levelId+goodsInfoId;
        redisService.setString(key,num.toString());
    }

    /**
     * 营销活动关联一个商品
     * @param request
     * @return
     */
    @Transactional
    public List<String> addActivityGoods(AddActivitGoodsRequest request){
        Optional<Marketing> byId = marketingRepository.findById(request.getMarketingId());
        Marketing marketing = byId.get();
        // 自定义商品才需要校验
        if (marketing.getScopeType() == MarketingScopeType.SCOPE_TYPE_CUSTOM) {

            List<MarketingSubType> subTypes=new ArrayList<>();
            subTypes.add(MarketingSubType.REDUCTION_FULL_ORDER);
            subTypes.add(MarketingSubType.DISCOUNT_FULL_ORDER);
            List<String> scopeIds = request.getAddActivitGoodsRequest().stream().map(o -> o.getScopeId()).collect(Collectors.toList());
            List<String> existsList = marketingRepository.getExistsSkuByMarketingType(scopeIds, marketing.getMarketingType(), marketing.getBeginTime(), marketing.getEndTime(), marketing.getStoreId(), request.getMarketingId(),subTypes);

            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(existsList)) {
                return existsList;
            }
        }
        List<MarketingScope> byMarketingId = marketingScopeRepository.findByMarketingId(request.getMarketingId());
        List<ActivitGoodsRequest> requestArrayList = new ArrayList<>();

        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(byMarketingId)){
            List<String> goodsInfoIds = byMarketingId.stream().map(o -> o.getScopeId()).collect(Collectors.toList());
            request.getAddActivitGoodsRequest().forEach(var->{
                if(!goodsInfoIds.contains(var.getScopeId())){
                    requestArrayList.add(var);
                }
            });
        }else{
            requestArrayList.addAll(request.getAddActivitGoodsRequest());
        }

        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + request.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", request.getMarketingId());
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }
        try {

            requestArrayList.forEach(var->{
                MarketingScope marketingScope = new MarketingScope();
                BeanUtils.copyProperties(var,marketingScope);
                marketingScope.setTerminationFlag(BoolFlag.NO);
                marketingScope.setMarketingId(request.getMarketingId());
                marketingScopeRepository.save(marketingScope);
            });

            // 更新Redis
            this.updateMarketingScopeHashCache(marketing);
            this.updateMarketingScopeStringCache(marketing);

            return new ArrayList<>();

        } catch (Exception e){
            log.info("DeleteMarketingById exception", e);
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }

    }
    /**
     * 删除营销活动
     * @param marketingId
     * @return
     */
    @Transactional
    public int deleteMarketingById(Long marketingId, String operatorId)   {

        Optional<Marketing> marketingOptional = marketingRepository.findById(marketingId);
        if(!marketingOptional.isPresent()){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Marketing marketing = marketingOptional.get();

        // 校验营销活动是否满足删除的前置条件
        checkMarketingCanDelete(marketing);

        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + marketing.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", marketing.getMarketingId());
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }
        try {

            // 删除数据库
            marketing.setDeleteTime(LocalDateTime.now());
            marketing.setDeletePerson(operatorId);
            marketing.setDelFlag(DeleteFlag.YES);
            marketingRepository.save(marketing);

            // 清理缓存
            clearMarketingCache(marketing);

            return 1; // 1代表更新成功
        } catch (Exception e){
            log.info("DeleteMarketingById exception", e);
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }
    }

    /**
     * 终止营销活动
     * @param marketingId
     * @param operatorId
     */
    @Transactional
    public void terminationMarketingById(Long marketingId, String operatorId) {

        Optional<Marketing> marketingOptional = marketingRepository.findById(marketingId);
        if(!marketingOptional.isPresent()){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Marketing marketing = marketingOptional.get();

        // 校验营销活动是否可被终止
        checkMarketingCanTerminate(marketing);

        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + marketing.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", marketing.getMarketingId());
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        try {
            // 清理缓存
            clearMarketingCache(marketing);

            LocalDateTime now = LocalDateTime.now();
            marketing.setTerminationFlag(BoolFlag.YES);
            marketing.setRealEndTime(marketing.getEndTime());
            boolean marketingIsStart = (now.isAfter(marketing.getBeginTime())
                    && now.isBefore(marketing.getEndTime()));
            if (!marketingIsStart){ marketing.setBeginTime(now); }
            marketing.setEndTime(now);
            marketing.setUpdateTime(now);
            marketing.setUpdatePerson(operatorId);
            marketingRepository.save(marketing);

        } catch (Exception e){
            log.info("TerminationMarketingById exception", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        } finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }
    }

    /**
     * 营销活动下终止单个关联的商品
     * @param request
     * @return
     */
    @Transactional
    public int terminationMarketingIdAndScopeId(TerminationMarketingScopeRequest request) {
        Long marketingScopeId = request.getMarketingScopeId();
        MarketingScope marketingScope = marketingScopeRepository.findById(marketingScopeId).orElse(null);
        if(Objects.isNull(marketingScope)){
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXISTS);
        }
        Long marketingId = marketingScope.getMarketingId();
        Marketing marketing = marketingRepository.findByMarketingId(marketingId);
        if(Objects.isNull(marketing)){
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXISTS);
        }

        log.info("营销活动下终止单个关联的商品:{}",marketing);

        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + marketing.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", marketing.getMarketingId());
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }
        try {
            int result = marketingScopeRepository.terminationMarketingIdAndScopeId(request.getMarketingScopeId(), BoolFlag.YES);
            // 更新缓存信息
            clearMarketingCache(marketing);
            // 移除固定的营销活动
            removeGoodsMarketingRelate(marketing.getMarketingId(), marketingScope.getScopeId());
            // 清理营销活动快照
            updateMarketingCache(marketing);
            return result;
        } finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }
    }

    @Transactional
    public int terminationByMarketingIdAndScopeId(TerminationMarketingScopeRequest request) {
        String scopeId = request.getScopeId();
        Long marketingId = request.getMarketingId();
        Marketing marketing = marketingRepository.findByMarketingId(marketingId);
        if(Objects.isNull(marketing)){
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXISTS);
        }
        log.info("营销活动下终止单个关联的商品:{}",marketing);

        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + marketing.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", marketing.getMarketingId());
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }
        try {
            List<MarketingScope> byMarketingIdAndSkuIds = marketingScopeRepository.findByMarketingIdAndSkuIds(marketingId, scopeId);
            if(CollectionUtils.isEmpty(byMarketingIdAndSkuIds)){
                return 0;
            }
            AtomicInteger result = new AtomicInteger();
            byMarketingIdAndSkuIds.forEach(marketingScope -> {
                int row = marketingScopeRepository.terminationMarketingIdAndScopeId(marketingScope.getMarketingScopeId(), BoolFlag.YES);
                result.addAndGet(row);
                // 更新缓存信息
                clearMarketingCache(marketing);
                // 移除固定的营销活动
                removeGoodsMarketingRelate(marketing.getMarketingId(), scopeId);
                // 清理营销活动快照
                updateMarketingCache(marketing);

            });
            return result.get();
        } finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }
    }

    /**
     * 移除固定的营销活动
     * @param marketingId
     * @param goodsInfoId
     */
    private void removeGoodsMarketingRelate(Long marketingId, String goodsInfoId){
        GoodsMarketingDeleteByMaketingIdAndGoodsInfoIdsRequest request = GoodsMarketingDeleteByMaketingIdAndGoodsInfoIdsRequest.builder()
                .marketingId(marketingId)
                .goodsInfoId(goodsInfoId)
                .build();
        goodsMarketingProvider.deleteGoodsMarketings(request);
    }

    /**
     * 获取当前活动类型+时间段，是否有已经绑定的sku
     * 父营销类型（满折，满减，满折）下的子营销类型（满金额，满数量）之间 商品不能重复
     * 父营销之间可重复参加
     * @param storeId
     * @param request
     * @return
     */
    public List<String> getExistsSkuByMarketingType(Long storeId, SkuExistsRequest request) {

        MarketingType marketingType = request.getMarketingType();

        // 同一时间一个商品只能参与同营销活动中的一个子营销活动
        Map<MarketingType, List<MarketingSubType>> marketSubTypeMapping = new HashMap<>();
        marketSubTypeMapping.put(MarketingType.GIFT, Arrays.asList(MarketingSubType.DISCOUNT_FULL_AMOUNT, MarketingSubType.DISCOUNT_FULL_COUNT,MarketingSubType.REDUCTION_FULL_AMOUNT, MarketingSubType.REDUCTION_FULL_COUNT));
        marketSubTypeMapping.put(MarketingType.DISCOUNT, Arrays.asList(MarketingSubType.GIFT_FULL_AMOUNT, MarketingSubType.GIFT_FULL_COUNT,MarketingSubType.REDUCTION_FULL_AMOUNT, MarketingSubType.REDUCTION_FULL_COUNT));
        marketSubTypeMapping.put(MarketingType.REDUCTION, Arrays.asList(MarketingSubType.GIFT_FULL_AMOUNT, MarketingSubType.GIFT_FULL_COUNT,MarketingSubType.DISCOUNT_FULL_AMOUNT, MarketingSubType.DISCOUNT_FULL_COUNT));
        List<MarketingSubType> subTypes = marketSubTypeMapping.get(marketingType);

        return marketingRepository.getExistsSkuByMarketingType(
                request.getSkuIds(),
                marketingType,
                request.getStartTime(),
                request.getEndTime(),
                storeId,
                request.getExcludeId(),
                subTypes
        );
    }

    /**
     * 满赠停止一个赠品
     * @param request
     */
    public void stopGiveGoods(MarketingStopGiveGoodsRequest request){

        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(request.getMarketingId());

        Marketing marketing = marketingRepository.findById(request.getMarketingId()).orElse(null);
        if(Objects.isNull(marketing)){
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }

        if(!MarketingType.GIFT.equals(marketing.getMarketingType())){
            throw new SbcRuntimeException("非满赠活动");
        }
        Boolean marketingGoingOn = LocalDateTime.now().isAfter(marketing.getBeginTime())
                && LocalDateTime.now().isBefore(marketing.getEndTime());

        if(!marketingGoingOn){
            throw new SbcRuntimeException("活动未开始不可使用");
        }

        Optional<MarketingFullGiftDetail> marketingFullGiftDetailOptional = marketingFullGiftDetailRepository.findById(Long.parseLong(request.getGiftDetailId()));
        if(!marketingFullGiftDetailOptional.isPresent()){
            throw new SbcRuntimeException("数据异常，未查询到对应的营销活动赠品信息");
        }

        // 加锁
        Long marketingId = marketing.getMarketingId();
        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + marketingId;
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", marketingId);
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }
        try {
            MarketingFullGiftDetail marketingFullGiftDetail = marketingFullGiftDetailOptional.get();
            marketingFullGiftDetail.setTerminationFlag(BoolFlag.YES);
            marketingFullGiftDetailRepository.save(marketingFullGiftDetail);

            // 刷新缓存,缓存为空自动重新构建
            redisCache.delete(RedisKeyConstants.MARKETING_GIFT_DETAIL_HASH + marketing.getMarketingId());
            clearMarketingCache(marketing);
            updateMarketingCache(marketing);
        } finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }
    }

    /**
     * 促销分组
     * @param request
     * @return
     * @throws SbcRuntimeException
     */
    public MarketingGroupCardResponse marketingGroupList(MarketingCardGroupRequest request) throws SbcRuntimeException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("准备工作");
        // 购物车中的所有商品
        List<DevanningGoodsInfoVO> devanningGoodsInfoVOList = request.getDevanningGoodsInfoVOList();

        // 减少内存计算消耗，对商品ID和商品信息做映射关系。
        Map<String, DevanningGoodsInfoVO> devanningGoodsInfoMaps = devanningGoodsInfoVOList
                .stream()
                .collect(Collectors.toMap(DevanningGoodsInfoVO::getGoodsInfoId, item -> item, (x, y)->y));
        stopWatch.stop();

        stopWatch.start("获取系统中正在进行中的营销活动ID");
        // 获取系统中正在进行中的营销活动ID。
        List<Long> availableMarketingIds = new ArrayList<>();
        BoolFlag isPileShopcart = request.getIsPileShopcart();
        if(Objects.nonNull(isPileShopcart) && BoolFlag.YES.equals(isPileShopcart)){
            availableMarketingIds = getAvailableMarketingIds(Arrays.asList(MarketingType.DISCOUNT,MarketingType.REDUCTION));
        } else {
            availableMarketingIds = this.getAvailableMarketingIds();
        }

        log.info("=======获取系统中正在进行中的营销活动ID=======:{}",availableMarketingIds);
        stopWatch.stop();

        // 系统中没有可用的营销活动，提前返回结果避免重复计算
        if(CollectionUtils.isEmpty(availableMarketingIds)){
            PriceInfoOfWholesale priceInfoOfWholesale = this.totalPriceCalculator(Lists.newArrayList(), devanningGoodsInfoVOList, request.getCustomerVO());

            CustomerVO customer = request.getCustomerVO();
            devanningGoodsInfoVOList.forEach(goodsInfoVO -> {
                if (Objects.nonNull(customer.getVipFlag()) && DefaultFlag.YES.equals(customer.getVipFlag())) {
                    goodsInfoVO.setMarketPrice(
                            null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0
                                    &&  goodsInfoVO.getVipPrice().compareTo(goodsInfoVO.getMarketPrice()) < 0
                                    ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                    goodsInfoVO.setVipPrice(goodsInfoVO.getMarketPrice());
                }
            });

            return MarketingGroupCardResponse.builder()
                    .marketingGroupCards(Lists.newArrayList())
                    .noHaveGoodsInfoVOList(devanningGoodsInfoVOList)
                    .priceInfoOfWholesale(priceInfoOfWholesale)
                    .build();
        }

        stopWatch.start("从MongoDB中获取当前登陆用户固定的营销活动");
        // 固定的营销不参与促销分组。数据来源mongodb：商品=>[营销]
        String customerId = request.getCustomerId();
        Map<String, Long> pinMarketingGoodsInfoMaps = this.getPinMarketingGoodsInfoMaps(customerId, availableMarketingIds);
        log.info("=======从MongoDB中获取当前登陆用户固定的营销活动=======:{}",pinMarketingGoodsInfoMaps);
        stopWatch.stop();

        stopWatch.start("从Redis中获取营销活动ID与商品ID集合的映射关系");

        Map<Long, Set<String>> marketingAndGoodsInfoMaps = new HashMap<>();
        Map<Long, Marketing> avaliableMarketingAndInfoMaps = new HashMap<>();
        for (Long marketingId : availableMarketingIds) {
            if(Objects.isNull(marketingId)){continue;}
            Set<String> scopeIds = this.getValidMarketingScopeIds(marketingId);
            marketingAndGoodsInfoMaps.put(marketingId, scopeIds);

            Marketing marketingInfo = this.getMarketingInfo(marketingId);
            avaliableMarketingAndInfoMaps.put(marketingId, marketingInfo);
        }
        stopWatch.stop();

        stopWatch.start("营销活动选取出最优组合");
        // 营销活动选取出最优组合
        List<MarketingCalculatorResult> marketingCalculatorResults = this.optimalMarketingBetweenMultGoods(devanningGoodsInfoMaps, marketingAndGoodsInfoMaps, pinMarketingGoodsInfoMaps, avaliableMarketingAndInfoMaps, request.getCustomerId());
        stopWatch.stop();


        List<MarketingGroupCard>  marketingGroupCards = new ArrayList<>();
        List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList = new ArrayList<>();
        log.info("待营销分组格式化数量大小:{}",marketingCalculatorResults.size());

        stopWatch.start("营销分组格式化");
        for (MarketingCalculatorResult result : marketingCalculatorResults) {

            Long marketingId = result.getMarketingId();

            // 未参加营销活动的分组
            if(Objects.isNull(marketingId)){
                List<String> goodsInfoIds = result.getGoodsInfoIds();
                List<DevanningGoodsInfoVO> collect = goodsInfoIds.stream()
                        .map(x -> devanningGoodsInfoMaps.get(x))
//                        .sorted(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime).reversed())// createTime传过来的是购物车的加购时间
                        .collect(Collectors.toList());
                noHaveGoodsInfoVOList.addAll(collect);
                continue;
            }

            List<String> goodsInfoIds = result.getGoodsInfoIds();
            List<DevanningGoodsInfoVO> allDevanningGoodsInfoVOS = new ArrayList<>();// 满足当前营销活动的商品
            List<DevanningGoodsInfoVO> checkedDevanningGoodsInfoVOS = new ArrayList<>(); // 满足当前营销活动而且勾选了的商品
            List<DevanningGoodsInfoVO> overPurchuseLimitDevanningGoodsInfoVOList = new ArrayList<>(); // 超过营销限购数量的商品

            if(CollectionUtils.isEmpty(goodsInfoIds)){ continue; }
            Map<String, MarketingScope> marketingScopeInfoList = this.getMarketingScopeInfoList(marketingId, goodsInfoIds);

            // TODO 写入缓存
            Map<String, List<MarketingPurchaseLimit>> marketingPurchaseLimitVOMappings = this.getMarketingPurchaseLimitVOMappings(marketingId, goodsInfoIds);

            for (String goodsInfoId : goodsInfoIds) {
                DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoMaps.get(goodsInfoId);
                // 在这里加入营销活动的限购数据，购物车需要做限购判断
                MarketingScope marketingScope = marketingScopeInfoList.get(goodsInfoId);
                if(Objects.nonNull(marketingScope)) {
                    Long perUserPurchaseNum = marketingScope.getPerUserPurchaseNum();
                    Long purchaseNum = marketingScope.getPurchaseNum();
                    if(Objects.nonNull(perUserPurchaseNum)){
                        devanningGoodsInfoVO.setPerPurchaseNumOfMarketingScope(BigDecimal.valueOf(marketingScope.getPerUserPurchaseNum()));
                    }
                    if(Objects.nonNull(purchaseNum)){
                        devanningGoodsInfoVO.setPurchaseNumOfMarketingScope(BigDecimal.valueOf(marketingScope.getPurchaseNum()));
                    }
                }
                allDevanningGoodsInfoVOS.add(devanningGoodsInfoVO);


                // 限购记录
                List<MarketingPurchaseLimit> marketingPurchaseLimits = marketingPurchaseLimitVOMappings.get(goodsInfoId);

                // 需要做营销限购判读，超过限购数量的从活动计算中拿出来
                if(this.reachMarketingPurchuseLimit(devanningGoodsInfoVO, marketingPurchaseLimits, customerId)){
                    // 格式化限购数量
                    overPurchuseLimitDevanningGoodsInfoVOList.add(devanningGoodsInfoVO);
                    continue;
                }

                if(DefaultFlag.YES.equals(devanningGoodsInfoVO.getIsCheck())){
                    checkedDevanningGoodsInfoVOS.add(devanningGoodsInfoVO);
                }
            }
            Marketing marketingInfo = avaliableMarketingAndInfoMaps.get(marketingId);// 获取营销活动
            MarketingCalculator caculator = MarketingCalculatorFactory.getCaculator(marketingInfo.getMarketingType().name());
            MarketingCalculatorResult calculate = caculator.calculate(checkedDevanningGoodsInfoVOS, marketingInfo);

            log.info("营销分组价格计算的结果：{}",calculate);

            if(!CollectionUtils.isEmpty(allDevanningGoodsInfoVOS)){
                allDevanningGoodsInfoVOS = allDevanningGoodsInfoVOS.stream()
//                        .sorted(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime).reversed())// createTime传过来的是购物车的加购时间
                        .collect(Collectors.toList());
            }

            MarketingGroupCard marketingGroupCard = MarketingGroupCard.builder()
                    .marketingVO(KsBeanUtil.convert(marketingInfo, MarketingVO.class))
                    .overPurchuseLimitDevanningGoodsInfoVOList(overPurchuseLimitDevanningGoodsInfoVOList)
                    .devanningGoodsInfoVOList(allDevanningGoodsInfoVOS)
                    .beforeAmount(calculate.getBeforeAmount())
                    .payableAmount(calculate.getPayableAmount())
                    .profitAmount(calculate.getProfitAmount())
                    .reachLevel(calculate.getReachLevel())
                    .reMarketingFold(calculate.getReMarketingFold())
                    .diffNextLevel(calculate.getDiffNextLevel())
                    .currentFullDiscountLevel(calculate.getCurrentFullDiscountLevel())
                    .currentFullReductionLevel(calculate.getCurrentFullReductionLevel())
                    .currentFullGiftLevel(calculate.getCurrentFullGiftLevel())
                    .nextFullDiscountLevel(calculate.getNextFullDiscountLevel())
                    .nextFullReductionLevel(calculate.getNextFullReductionLevel())
                    .nextFullGiftLevel(calculate.getNextFullGiftLevel())
                    .build();
            marketingGroupCards.add(marketingGroupCard);
        }
        stopWatch.stop();

        if(!CollectionUtils.isEmpty(noHaveGoodsInfoVOList)){
            noHaveGoodsInfoVOList = noHaveGoodsInfoVOList.stream()
//                    .sorted(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime).reversed()) // createTime传过来的是购物车的加购时间
                    .collect(Collectors.toList());
        }

        stopWatch.start("营销分组价格计算");
        PriceInfoOfWholesale priceInfoOfWholesale = this.totalPriceCalculator(marketingGroupCards, noHaveGoodsInfoVOList, request.getCustomerVO());
        stopWatch.stop();

        stopWatch.start("营销分组排序");
        if(!CollectionUtils.isEmpty(marketingGroupCards)){
            marketingGroupCards = this.sortMarketingGroupCards(marketingGroupCards);
        }

        if(!CollectionUtils.isEmpty(noHaveGoodsInfoVOList)){
            CustomerVO customer = request.getCustomerVO();
            noHaveGoodsInfoVOList.forEach(goodsInfoVO -> {
                if (Objects.nonNull(customer.getVipFlag()) && DefaultFlag.YES.equals(customer.getVipFlag())) {
                    goodsInfoVO.setMarketPrice(
                            null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0
                                    &&  goodsInfoVO.getVipPrice().compareTo(goodsInfoVO.getMarketPrice()) < 0
                                    ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                    goodsInfoVO.setVipPrice(goodsInfoVO.getMarketPrice());
                }
            });
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return MarketingGroupCardResponse.builder()
                .marketingGroupCards(marketingGroupCards)
                .noHaveGoodsInfoVOList(noHaveGoodsInfoVOList)
                .priceInfoOfWholesale(priceInfoOfWholesale)
                .build();
    }

    private Map<String, List<MarketingPurchaseLimit>> getMarketingPurchaseLimitVOMappings(Long marketingId, List<String> goodsInfoIds){

        StopWatch stopWatch = new StopWatch();
        // 商品ID->已经购买的数量
        Map<String, List<MarketingPurchaseLimit>> marketingPurchaseLimitVOMappings = Maps.newHashMap();

        stopWatch.start("获取该营销所有购买记录");
        // 该营销的所有购买记录
        List<MarketingPurchaseLimit> marketingPurchaseLimits = marketingPurchaseLimitService.getbyMarketingIdAndGoodsInfosId(marketingId, goodsInfoIds);
        stopWatch.stop();
        stopWatch.start("获取订单信息");
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(marketingPurchaseLimits)){
            List<String> tradeIds = marketingPurchaseLimits.stream().map(MarketingPurchaseLimit::getTradeId).distinct().collect(Collectors.toList());
            List<TradeVO> context2 = tradeQueryProvider.getOrderByIdsSimplify(tradeIds).getContext();
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(context2)){
                List<String> tids = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
                marketingPurchaseLimitVOMappings = marketingPurchaseLimits.stream()
                        .filter(x -> tids.contains(x.getTradeId()))
                        .collect(Collectors.groupingBy(MarketingPurchaseLimit::getGoodsInfoId));
            }
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return marketingPurchaseLimitVOMappings;
    }

    /**
     * 是否达到营销限购数量
     * @param devanningGoodsInfoVO
     * @param marketingPurchaseLimits
     * @param customerId
     * @return
     */
    private boolean reachMarketingPurchuseLimit(DevanningGoodsInfoVO devanningGoodsInfoVO, List<MarketingPurchaseLimit> marketingPurchaseLimits, String customerId) {
        Long buyCount = devanningGoodsInfoVO.getBuyCount();
        BigDecimal perUserPurchaseNum = devanningGoodsInfoVO.getPerPurchaseNumOfMarketingScope();// 单用户限购量
        BigDecimal purchaseNum = devanningGoodsInfoVO.getPurchaseNumOfMarketingScope(); // 总限购量

        // 1. 未设置限购数量
        if(Objects.isNull(purchaseNum) && Objects.isNull(perUserPurchaseNum)){
            return false;
        }

        // 2. 无购买记录
        if(CollectionUtils.isEmpty(marketingPurchaseLimits)){
            if(Objects.nonNull(purchaseNum) && Objects.nonNull(perUserPurchaseNum)) {
                Long min = Long.min(purchaseNum.longValue(), perUserPurchaseNum.longValue());
                min = Long.max(min, 0L);
                // TODO log
                log.info("reachMarketingPurchuseLimit1===,{},{}", buyCount, min);
                return buyCount > min;
            }
            if(Objects.nonNull(purchaseNum)){
                Long max = Long.max(purchaseNum.longValue(), 0L);
                // TODO log
                log.info("reachMarketingPurchuseLimit2===,{},{}", buyCount, max);
                return buyCount > max;
            }
            Long max = Long.max(perUserPurchaseNum.longValue(), 0L);
            // TODO log
            log.info("reachMarketingPurchuseLimit3===,{},{}", buyCount, max);
            return buyCount > max;
        }

        Long size = marketingPurchaseLimits.stream().map(MarketingPurchaseLimit::getNum).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
        Long sizeForPerUser = marketingPurchaseLimits.stream().filter(x -> Objects.equals(x.getCustomerId(), customerId))
                .map(MarketingPurchaseLimit::getNum)
                .reduce(BigDecimal.ZERO, BigDecimal::add).longValue();

        // 3. 该商品同时设置了两个限购值
        if(Objects.nonNull(purchaseNum) && Objects.nonNull(perUserPurchaseNum)) {
            Long l1 = Long.max(perUserPurchaseNum.longValue() - sizeForPerUser, 0L);// 单用户限可提
            Long l2 = Long.max(purchaseNum.longValue() - size, 0L); // 总限购可提
            Long min = Long.min(l1, l2);
            // TODO log
            log.info("reachMarketingPurchuseLimit4===,{},{}", buyCount, min);
            return buyCount > min;
        }

        // 4. 只设置了营销总限购
        if(Objects.nonNull(purchaseNum)){
            Long l = Long.max(purchaseNum.longValue() - size, 0L);
            // TODO log
            log.info("reachMarketingPurchuseLimit5===,{},{}", buyCount, l);
            return buyCount > l;
        }

        // 5. 只设置了单用户营销总限购
        Long l = Long.max(perUserPurchaseNum.longValue() - sizeForPerUser, 0L);
        // TODO log
        log.info("reachMarketingPurchuseLimit6===,{},{}", buyCount, l);
        return buyCount > l;
    }

    /**
     * 对促销分组进行排序
     * @param marketingGroupCards
     * @return
     */
    private List<MarketingGroupCard> sortMarketingGroupCards(List<MarketingGroupCard> marketingGroupCards){
        return marketingGroupCards.stream().sorted(Comparator.comparing(MarketingGroupCard::getMarketingVO, (x, y) -> {
            // 按活动大类先后排序：满减-满折-满赠
            MarketingType marketingType1 = x.getMarketingType();
            MarketingType marketingType2 = y.getMarketingType();
            int result = marketingType1.toValue() - marketingType2.toValue();
            return result;
        }).reversed().thenComparing(Comparator.comparing(MarketingGroupCard::getMarketingVO, (x, y) -> {
            // 同一大类下的营销活动：满数量-满金额
            MarketingSubType subType1 = x.getSubType();
            MarketingSubType subType2 = y.getSubType();
            int result = subType1.toValue() - subType2.toValue();
            return result;
        })).reversed().thenComparing(Comparator.comparing(MarketingGroupCard::getMarketingVO, (x, y) -> {
            MarketingSubType subType = x.getSubType();
            // 如果是满减，减金额最大的排在前面
            if(Arrays.asList(MarketingSubType.REDUCTION_FULL_AMOUNT, MarketingSubType.REDUCTION_FULL_COUNT).contains(subType)){
                MarketingFullReductionLevelVO level1 = x.getMarketingFullReductionLevels().stream().findFirst().orElseGet(null);
                MarketingFullReductionLevelVO level2 = y.getMarketingFullReductionLevels().stream().findFirst().orElseGet(null);
                if(Objects.nonNull(level1) && Objects.nonNull(level2)){
                    return level1.getReduction().compareTo(level2.getReduction());
                }
            }
            // 如果是满折，折扣力度最大的放在前面
            if(Arrays.asList(MarketingSubType.DISCOUNT_FULL_AMOUNT, MarketingSubType.DISCOUNT_FULL_COUNT).contains(subType)){
                MarketingFullDiscountLevelVO level1 = x.getMarketingFullDiscountLevels().stream().findFirst().orElseGet(null);
                MarketingFullDiscountLevelVO level2 = y.getMarketingFullDiscountLevels().stream().findFirst().orElseGet(null);
                if(Objects.nonNull(level1) && Objects.nonNull(level2)){
                    int i = level1.getDiscount().compareTo(level2.getDiscount());
                    if(i < 0) return 1;
                    if(i > 0) return -1;
                    return i;
                }
            }
            // 满赠排序，优惠力度不好用金额衡量。这个按照活动的门槛来进行衡量，门槛越低的排在前面。如满3赠1、满4赠1，满3赠1排在前面（门槛更低）
            if(MarketingSubType.GIFT_FULL_AMOUNT.equals(subType)){
                MarketingFullGiftLevelVO level1 = x.getMarketingFullGiftLevels().stream().findFirst().orElseGet(null);
                MarketingFullGiftLevelVO level2 = y.getMarketingFullGiftLevels().stream().findFirst().orElseGet(null);
                if(Objects.nonNull(level1) && Objects.nonNull(level2)){
                    int i = level1.getFullAmount().compareTo(level2.getFullAmount());
                    if(i < 0) return 1;
                    if(i > 0) return -1;
                    return i;
                }
            }
            if(MarketingSubType.GIFT_FULL_COUNT.equals(subType)){
                MarketingFullGiftLevelVO level1 = x.getMarketingFullGiftLevels().stream().findFirst().orElseGet(null);
                MarketingFullGiftLevelVO level2 = y.getMarketingFullGiftLevels().stream().findFirst().orElseGet(null);
                if(Objects.nonNull(level1) && Objects.nonNull(level2)){
                    int i = level1.getFullCount().compareTo(level2.getFullCount());
                    if(i < 0) return 1;
                    if(i > 0) return -1;
                    return i;
                }
            }
            return 0;
        }).reversed())).collect(Collectors.toList());
    }

    /**
     * 计算营销活动的优惠信息
     * @param marketingGroupCards
     * @param noHaveGoodsInfoVOList
     * @param customer
     * @return
     */
    private PriceInfoOfWholesale totalPriceCalculator(List<MarketingGroupCard> marketingGroupCards, List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList, CustomerVO customer) {
        BigDecimal totalAmount = BigDecimal.ZERO;           // 总金额
        BigDecimal profitAmountOfReduce = BigDecimal.ZERO;  // 满减金额
        BigDecimal profitAmountOfDiscount = BigDecimal.ZERO; // 满赠金额

        log.info("计算营销活动的优惠信息:{},{}", marketingGroupCards.size(), noHaveGoodsInfoVOList.size());

        // 计算参加活动的商品总价格
        for (MarketingGroupCard marketingGroupCard : marketingGroupCards) {
            totalAmount = totalAmount.add(marketingGroupCard.getBeforeAmount());
            BigDecimal profitAmount = marketingGroupCard.getProfitAmount();

            // 未达到活动门槛
            Boolean reachLevel = marketingGroupCard.getReachLevel();
            if(Boolean.FALSE.equals(reachLevel)){ continue; }

            MarketingFullReductionLevelVO currentFullReductionLevel = marketingGroupCard.getCurrentFullReductionLevel();
            if(Objects.nonNull(currentFullReductionLevel)){
                profitAmountOfReduce = profitAmountOfReduce.add(profitAmount);
            }

            MarketingFullDiscountLevelVO currentFullDiscountLevel = marketingGroupCard.getCurrentFullDiscountLevel();
            if(Objects.nonNull(currentFullDiscountLevel)){
                profitAmountOfDiscount = profitAmountOfDiscount.add(profitAmount);
            }
        }

        // 营销活动下的商品超过了限购数量，按照原价进行计算
        for (MarketingGroupCard marketingGroupCard : marketingGroupCards) {
            List<DevanningGoodsInfoVO> devanningGoodsInfoVOs = marketingGroupCard.getOverPurchuseLimitDevanningGoodsInfoVOList();
            if(CollectionUtils.isEmpty(devanningGoodsInfoVOs)){
                continue;
            }
            for (DevanningGoodsInfoVO noHaveGoodsInfoVO : devanningGoodsInfoVOs) {
                if(!DefaultFlag.YES.equals(noHaveGoodsInfoVO.getIsCheck())){ continue; }
                /**
                 * 使用VIP价格的情况
                 * （1）当前用户是VIP用户
                 * （2）商品设置了VIP价格，注意0代表的是未设置
                 * （3）VIP价格 < 市场价
                 */
                BigDecimal price = noHaveGoodsInfoVO.getMarketPrice();
                // 当前用户是否为VIP客户
                Boolean isVipCustomer = DefaultFlag.YES.equals(customer.getVipFlag());
                // 注意0代表的就是未设置VIP价
                Boolean vipPriceIsNotBlank = BigDecimal.ZERO.compareTo(noHaveGoodsInfoVO.getVipPrice()) < 0;
                if(isVipCustomer && vipPriceIsNotBlank){
                    BigDecimal vipPrice = noHaveGoodsInfoVO.getVipPrice();
                    BigDecimal marketPrice = noHaveGoodsInfoVO.getMarketPrice();
                    // 市场价和VIP价谁低就用谁
                    if(marketPrice.compareTo(vipPrice) > 0){
                        price = vipPrice;
                    }
                }

                BigDecimal buyCount = BigDecimal.valueOf(noHaveGoodsInfoVO.getBuyCount());
                BigDecimal multiply = price.multiply(buyCount);
                totalAmount = totalAmount.add(multiply);
            }
        }

        // 未参加营销活动的商品
        for (DevanningGoodsInfoVO noHaveGoodsInfoVO : noHaveGoodsInfoVOList) {
            if(!DefaultFlag.YES.equals(noHaveGoodsInfoVO.getIsCheck())){ continue; }
            /**
             * 使用VIP价格的情况
             * （1）当前用户是VIP用户
             * （2）商品设置了VIP价格，注意0代表的是未设置
             * （3）VIP价格 < 市场价
             */
            BigDecimal price = noHaveGoodsInfoVO.getMarketPrice();
            // 当前用户是否为VIP客户
            Boolean isVipCustomer = DefaultFlag.YES.equals(customer.getVipFlag());
            // 注意0代表的就是未设置VIP价
            Boolean vipPriceIsNotBlank = BigDecimal.ZERO.compareTo(noHaveGoodsInfoVO.getVipPrice()) < 0;
            if(isVipCustomer && vipPriceIsNotBlank){
                BigDecimal vipPrice = noHaveGoodsInfoVO.getVipPrice();
                BigDecimal marketPrice = noHaveGoodsInfoVO.getMarketPrice();
                // 市场价和VIP价谁低就用谁
                if(marketPrice.compareTo(vipPrice) > 0){
                    price = vipPrice;
                }
            }

            BigDecimal buyCount = BigDecimal.valueOf(noHaveGoodsInfoVO.getBuyCount());
            BigDecimal multiply = price.multiply(buyCount);
            totalAmount = totalAmount.add(multiply);
        }
        // 优惠的总金额
        BigDecimal profitAmount = profitAmountOfReduce.add(profitAmountOfDiscount);
        BigDecimal payableAmount = totalAmount.subtract(profitAmount);

        PriceInfoOfWholesale priceInfoOfWholesale = PriceInfoOfWholesale.builder()
                .totalAmount(totalAmount)
                .profitAmountOfReduce(profitAmountOfReduce)
                .profitAmountOfDiscount(profitAmountOfDiscount)
                .profitAmount(profitAmount)
                .payableAmount(payableAmount)
                .build();
        return priceInfoOfWholesale;
    }

    /**
     * 已经固定的营销不参加营销分组
     * @param customerId
     * @param availableMarketingIds
     * @return
     */
    private Map<String, Long> getPinMarketingGoodsInfoMaps(String customerId, List<Long> availableMarketingIds) {
        return this.getGoodsMarketingMapsFromMongoDB(customerId, availableMarketingIds);
    }

    /**
     * 提取最优的促销分组
     * @param devanningGoodsInfoVMappings
     * @param marketingAndGoodsInfoMaps
     * @param pinMarketingGoodsInfoMaps
     * @param avaliableMarketingAndInfoMaps
     * @param customerId
     * @return
     */
    private List<MarketingCalculatorResult> optimalMarketingBetweenMultGoods(
            Map<String, DevanningGoodsInfoVO> devanningGoodsInfoVMappings, // 所有待分组的商品
            Map<Long, Set<String>> marketingAndGoodsInfoMaps,   // 系统中有效的营销活动ID与商品ID集合的映射关系
            Map<String, Long> pinMarketingGoodsInfoMaps,        // 当前用户已经固定的营销活动的商品映射关系
            Map<Long, Marketing> avaliableMarketingAndInfoMaps,  // 营销ID与营销信息的映射关系（全集）
            String customerId // 用户ID
    ) {

        Map<String, Set<Long>> objectObjectHashMap = reverseMarketingAndGoodsInfoMap(marketingAndGoodsInfoMaps);

        Set<String> allGoodsInfos = objectObjectHashMap.keySet(); // 系统中参加营销活动的所有商品
        log.info("=======系统中参加营销活动的所有商品=======:{}",allGoodsInfos);

        // 用户购物车中所有的商品
        Set<String> goodsInfoIds = devanningGoodsInfoVMappings.keySet(); // 当前购物车中所有的商品
        log.info("=======购物车中所有的商品=======:{}",goodsInfoIds);

        // 用户固定营销的商品ID集合
        Set<String> marketingGoodsInfo = pinMarketingGoodsInfoMaps.keySet();
        log.info("=======用户固定营销的所有商品=======:{}",marketingGoodsInfo);

        // 购物车中参加营销活动的商品ID集合
        Set<String> penddingGoodsInfoIds = Sets.intersection(allGoodsInfos, goodsInfoIds);
        log.info("=======购物车中所有参加营销活动的商品=======:{}",penddingGoodsInfoIds);

        // 购物车中未固定营销活动的商品ID集合：购物车中参加营销活动的商品ID集合 - 固定营销的商品ID集合
        Set<String> notPinMarketingGoodsInfo = Sets.difference(penddingGoodsInfoIds, marketingGoodsInfo);
        log.info("=======购物车中未固定营销的商品集合=======:{}",notPinMarketingGoodsInfo);
        // 购物车中已固定营销活动的商品ID集合：参加营销 与 固定的营销取交集
        Set<String> pinMarketingGoodsInfo = Sets.intersection(penddingGoodsInfoIds, marketingGoodsInfo);
        log.info("=======购物车中已固定营销活动的商品集合=======:{}",pinMarketingGoodsInfo);
        // 购物车中未参加营销活动的商品ID
        Set<String> notMarketingGoodsInfo = Sets.difference(goodsInfoIds, penddingGoodsInfoIds);
        log.info("=======购物车中未参加营销活动的商品集合=======:{}",notMarketingGoodsInfo);


        Map<Long, Set<String>> result = Maps.newHashMap();// 最终的分组结果：key是营销ID，values是商品ID
        // 1.购物车中的所有商品都没有参加营销活动，直接返回。
        if(!CollectionUtils.isEmpty(notMarketingGoodsInfo)){
            result.put(0L, notMarketingGoodsInfo); // 0代表没有参加
        }
        Boolean notAnyMarketing = CollectionUtils.isEmpty(notPinMarketingGoodsInfo) && CollectionUtils.isEmpty(pinMarketingGoodsInfo);
        if(Boolean.TRUE.equals(notAnyMarketing)){
            List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
            log.info("=======1.购物车中的所有商品都没有参加营销活动=======:{}",marketingCalculatorResults);
            return marketingCalculatorResults;
        }

        // 2.购物车中参加营销活动的商品都固定了营销，分组完直接返回.
        if(!CollectionUtils.isEmpty(pinMarketingGoodsInfo)){
            for (String goodsInfoId : pinMarketingGoodsInfo) {
                Long marketingId = pinMarketingGoodsInfoMaps.get(goodsInfoId);

                Set<String> set = result.get(marketingId);
                if(CollectionUtils.isEmpty(set)){
                    set = Sets.newHashSet(goodsInfoId);
                } else {
                    set.add(goodsInfoId);
                }
                result.put(marketingId, set);
            }
        }
        if(CollectionUtils.isEmpty(notPinMarketingGoodsInfo) ){
            List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
            log.info("=======2.购物车中参加营销活动的商品都固定了营销=======:{}",marketingCalculatorResults);
            return marketingCalculatorResults;
        }

        // 3.未固定营销的商品只参加了一个活动，没必要进行促销分组，计算完提前返回。
        Map<String, Long> todoPin = Maps.newHashMap(); // 待固定营销分组的商品和ID的映射关系
        if(!CollectionUtils.isEmpty(notPinMarketingGoodsInfo)){
            Set<String> onlyOneMarkting = Sets.newHashSet();
            for (String goodsInfoId : notPinMarketingGoodsInfo) {
                Set<Long> set = objectObjectHashMap.get(goodsInfoId);
                if(set.size() != 1){ // 1表示该商品只参加了一个活动
                    continue;
                }
                onlyOneMarkting.add(goodsInfoId);

                Long marketingId = set.stream().findFirst().get();
                Set<String> gs = result.get(marketingId);
                if(CollectionUtils.isEmpty(gs)){
                    gs = Sets.newHashSet(goodsInfoId);
                } else {
                    gs.add(goodsInfoId);
                }
                result.put(marketingId, gs);

                todoPin.put(goodsInfoId, marketingId);
            }
            // 未固定营销的商品中排除只参加一个营销活动的商品
            if(!CollectionUtils.isEmpty(onlyOneMarkting)){
                notPinMarketingGoodsInfo = Sets.difference(notPinMarketingGoodsInfo, onlyOneMarkting);
            }
        }
        if(CollectionUtils.isEmpty(notPinMarketingGoodsInfo)){
            List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
            this.pinGoodsMarketingRelate(todoPin, customerId);
            log.info("=======3.未固定营销的商品只参加了一个活动=======:{}",marketingCalculatorResults);
            return marketingCalculatorResults;
        }

        // 4. 未固定营销的商品只有一个，不需要进行排列组合计算
        if(notPinMarketingGoodsInfo.size() == 1){
            Iterator<String> it = notPinMarketingGoodsInfo.iterator();
            // 获取商品ID
            String goodsInfoId = it.next();
            // 获取商品参加的营销活动
            Set<Long> marketingIds = objectObjectHashMap.get(goodsInfoId);
            // 待重新计算最优的促销分组
            Map<Long, Set<String>> tempResult = Maps.newHashMap();
            for (Long marketingId : marketingIds){
                // 如果当前商品和前面固定营销的商品使用的营销活动有交集，则合并起来
                Set<String> strings = result.get(marketingId);
                if(CollectionUtils.isEmpty(strings)){
                    strings = Sets.newHashSet(goodsInfoId);
                } else {
                    strings.add(goodsInfoId);
                }
                tempResult.put(marketingId, strings);
            }
            log.info("MarketService optimalMarketingBetweenMultGoods notPinMarketingGoodsInfo tempResult:{}", tempResult);

            // 组合进行价格计算
            List<MarketingCalculatorResult> todo = Lists.newArrayList();
            for (Map.Entry<Long, Set<String>> map : tempResult.entrySet()) {
                Long marketingId = map.getKey();
                Set<String> goodsInfoIdList = map.getValue();
                Marketing marketing = avaliableMarketingAndInfoMaps.get(marketingId);
                if(Objects.isNull(marketing)){ continue;}

                Map<Long, Set<String>> mapOfCal = Maps.newHashMap();
                mapOfCal.put(marketingId, goodsInfoIdList);
                // 促销分组价格计算
                List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(mapOfCal, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
                if(CollectionUtils.isEmpty(marketingCalculatorResults)){ continue; }

                // 这一步是避免不垮单品的情况下出现异常问题
                MarketingCalculatorResult marketingCalculatorResult = marketingCalculatorResults.stream().filter(x -> {
                    return x.getGoodsInfoIds().contains(goodsInfoId);
                }).findFirst().orElse(null);
                if(Objects.isNull(marketingCalculatorResult)){ continue; }

                todo.add(marketingCalculatorResult);
            }
            // 当达到营销活动门槛，按照门槛扣减的金额，选取营销活动
            MarketingCalculatorResult reachMarketingCalculatorResult = todo.stream().filter(x -> {
                return Boolean.TRUE.equals(x.getReachLevel());
            }).sorted(Comparator.comparing(MarketingCalculatorResult::getProfitAmount, (x, y) -> {
                return x.compareTo(y);
            }).reversed()).findFirst().orElse(null);
            if(Objects.nonNull(reachMarketingCalculatorResult)){

                Long marketingId = reachMarketingCalculatorResult.getMarketingId();
                Set<String> strings = result.get(marketingId);
                if(CollectionUtils.isEmpty(strings)){
                    strings = Sets.newHashSet(goodsInfoId);
                } else {
                    strings.add(goodsInfoId);
                }

                // 固定营销活动
                todoPin.put(goodsInfoId, marketingId);
                this.pinGoodsMarketingRelate(todoPin, customerId);

                // 最终的价格计算
                result.put(marketingId, strings);
                List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
                log.info("=======4.未固定营销的商品只有一个，且已经达到门槛,不需要进行排列组合计算=======:{}",marketingCalculatorResults);
                return marketingCalculatorResults;
            }
            /**
             * 都没有达到门槛，忽略门槛值进行计算。
             */
            DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoVMappings.get(goodsInfoId);
            List<MarketingCalculatorResult> objects = Lists.newArrayList();
            for (Long marketingId : marketingIds){
                Marketing marketing = avaliableMarketingAndInfoMaps.get(marketingId);
                if(Objects.isNull(marketing)) {continue;}
                List<MarketingScope> marketingScopeList = marketing.getMarketingScopeList();
                if(CollectionUtils.isEmpty(marketingScopeList)){
                    marketing.setMarketingScopeList(this.getAvaliableMarketingScopeInfoListFromDB(marketing.getMarketingId())); // TODO 封装Redis
                }
                MarketingCalculator caculator = MarketingCalculatorFactory.getCaculator(marketing.getMarketingType().name());
                MarketingCalculatorResult marketingCalculatorResult = caculator.calculateIgnoreLevelThreshold(devanningGoodsInfoVO, marketing);
                objects.add(marketingCalculatorResult);
            }

            MarketingCalculatorResult marketingCalculatorResult = objects.stream()
                    .sorted(Comparator.comparing(MarketingCalculatorResult::getProfitAmount, (x, y) -> {
                        return x.compareTo(y);
                    }).reversed()).findFirst().get();
            Long pinMarketingId = marketingCalculatorResult.getMarketingId();
            Set<String> strings = result.get(pinMarketingId);
            if(CollectionUtils.isEmpty(strings)){
                strings = Sets.newHashSet(goodsInfoId);
            } else {
                strings.add(goodsInfoId);
            }
            result.put(pinMarketingId, strings);
            todoPin.put(goodsInfoId, pinMarketingId);

            List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
            this.pinGoodsMarketingRelate(todoPin, customerId);
            log.info("=======4.未固定营销的商品只有一个，不需要进行排列组合计算=======:{}",marketingCalculatorResults);
            return marketingCalculatorResults;
        }


        // 5. 排列组合未固定营销活动商品的所有情况
        for (;;) {
            log.info("MarketService optimalMarketingBetweenMultGoods notPinMarketingGoodsInfo: {}",notPinMarketingGoodsInfo);
            List<Map<Long, Set<String>>> todoList = this.comboMarketingAndGoodsInfo(objectObjectHashMap, notPinMarketingGoodsInfo, result);
            log.info("MarketService optimalMarketingBetweenMultGoods todoList: {}", todoList);

            List<MarketingIdAndGoodsInfoWrapper> wrappers = Lists.newArrayList();
            for (Map<Long, Set<String>> todo : todoList) {
                // 每一个todo代表一个促销分组方案
                List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(todo, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
                // 统计待分组的营销活动每一个等级的优惠信息
                BigDecimal profitAmountTotal = BigDecimal.ZERO; // 总的优惠金额
                Long reachLevelCount = 0L; // 已达营销活动门槛的总数
                for (MarketingCalculatorResult marketingCalculatorResult : marketingCalculatorResults) {
                    Boolean reachLevel = marketingCalculatorResult.getReachLevel();
                    // 没达到最低的活动门槛
                    if(Boolean.FALSE.equals(reachLevel)){ continue; }
                    BigDecimal profitAmount = marketingCalculatorResult.getProfitAmount();
                    profitAmountTotal = profitAmountTotal.add(profitAmount);
                    reachLevelCount++;
                }
                MarketingIdAndGoodsInfoWrapper wrapper = new MarketingIdAndGoodsInfoWrapper();
                wrapper.setReachCount(reachLevelCount);
                wrapper.setReachProfileValue(profitAmountTotal);
                wrapper.setGroupMap(todo);
                wrappers.add(wrapper);
            }
            log.info("MarketService optimalMarketingBetweenMultGoods marketingIdAndGoodsInfoWrappers: {}", wrappers);
            // 比较每种方案，拿到最优的组合
            List<MarketingIdAndGoodsInfoWrapper> collect = wrappers.stream()
                    .filter(x->x.getReachCount() > 0) // 该组合达到门槛的总次数
                    .sorted(Comparator.comparing(MarketingIdAndGoodsInfoWrapper::getReachProfileValue,Comparator.reverseOrder())
                            .thenComparing(Comparator.comparing(MarketingIdAndGoodsInfoWrapper::getReachCount, Comparator.reverseOrder()))
                    ).collect(Collectors.toList());
            log.info("MarketService optimalMarketingBetweenMultGoods sort marketingIdAndGoodsInfoWrappers:{}", collect);

            if(CollectionUtils.isEmpty(collect) || collect.size()<1){
               break;
            }

            MarketingIdAndGoodsInfoWrapper marketingIdAndGoodsInfoWrapper = collect.get(0);
            log.info("MarketService optimalMarketingBetweenMultGoods marketingIdAndGoodsInfoWrapper: {}", JSON.toJSONString(marketingIdAndGoodsInfoWrapper));
            Map<Long, Set<String>> groupMap = marketingIdAndGoodsInfoWrapper.getGroupMap();
            for (Map.Entry<Long, Set<String>> r : groupMap.entrySet()) {
                Long marketingId = r.getKey();
                Set<String> value = r.getValue();

                Set<String> strings = result.get(marketingId);
                if(CollectionUtils.isEmpty(strings)){
                    strings = value;
                } else {
                    strings.addAll(value);
                }
                result.put(marketingId, strings);

                // 固定营销活动
                notPinMarketingGoodsInfo = Sets.difference(notPinMarketingGoodsInfo, value);
                for (String s : value) { todoPin.put(s, marketingId); }
            }

            if(CollectionUtils.isEmpty(notPinMarketingGoodsInfo)){
                List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
                this.pinGoodsMarketingRelate(todoPin, customerId);
                log.info("=======5.非固定营销分组=======:{}",marketingCalculatorResults);
                return marketingCalculatorResults;
            }
        }

        // 6. 剩下的商品不能组成达到门槛的营销活动，进行最大优惠值的比对
        for (String goodsInfoId : notPinMarketingGoodsInfo) {

            // 获取当前商品能参加的所有营销活动
            Set<Long> marketingIds = objectObjectHashMap.get(goodsInfoId);

            List<MarketingCalculatorResult> marketingCalculatorResults = Lists.newArrayList();
            for (Long marketingId : marketingIds) {
                Marketing marketing = avaliableMarketingAndInfoMaps.get(marketingId);
                DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoVMappings.get(goodsInfoId);

                MarketingCalculator caculator = MarketingCalculatorFactory.getCaculator(marketing.getMarketingType().name());
                MarketingCalculatorResult marketingCalculatorResult = caculator.calculateIgnoreLevelThreshold(devanningGoodsInfoVO, marketing);
                marketingCalculatorResults.add(marketingCalculatorResult);
            }
            log.info("MarketService optimalMarketingBetweenMultGoods marketingCalculatorResults:{}",marketingCalculatorResults);
            if(CollectionUtils.isEmpty(marketingCalculatorResults)){continue;}

            MarketingCalculatorResult marketingCalculatorResult = marketingCalculatorResults.stream()
                    .sorted(Comparator.comparing(MarketingCalculatorResult::getProfitAmount,Comparator.reverseOrder())).findFirst().get();
            log.info("MarketService optimalMarketingBetweenMultGoods marketingCalculatorResult:{}",marketingCalculatorResult);

            Long pinMarketingId = marketingCalculatorResult.getMarketingId();
            Set<String> strings = result.get(pinMarketingId);
            if(CollectionUtils.isEmpty(strings)){
                strings = Sets.newHashSet(goodsInfoId);
            } else {
                strings.add(goodsInfoId);
            }
            result.put(pinMarketingId, strings);
            todoPin.put(goodsInfoId, pinMarketingId);
        }

        List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(result, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
        this.pinGoodsMarketingRelate(todoPin, customerId);
        log.info("=======6.最终返回=======:{}",marketingCalculatorResults);
        return marketingCalculatorResults;
    }



    public MarketingGroupCardResponse singleMarketingGroupList(MarketingCardGroupRequest request) throws SbcRuntimeException {
        log.info("MarketService singleMarketingGroupList request:{}",request);
        List<DevanningGoodsInfoVO> devanningGoodsInfoVOList = request.getDevanningGoodsInfoVOList();
        if(CollectionUtils.isEmpty(devanningGoodsInfoVOList)){
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoVOList.get(0);
        MarketingCalculatorResult calculate = this.optimalMarketingSingleGoods(devanningGoodsInfoVO);

        List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = Lists.newArrayList();
        devanningGoodsInfoVOS.add(devanningGoodsInfoVO);

        if(Boolean.TRUE.equals(calculate.getReachLevel())){
            Long marketingId = calculate.getMarketingId();
            Marketing marketingInfo = this.getMarketingInfo(marketingId);

            // 达到营销活动门槛
            MarketingGroupCard marketingGroupCard = MarketingGroupCard.builder()
                    .marketingVO(KsBeanUtil.convert(marketingInfo, MarketingVO.class))
                    .devanningGoodsInfoVOList(devanningGoodsInfoVOS)
                    .beforeAmount(calculate.getBeforeAmount())
                    .payableAmount(calculate.getPayableAmount())
                    .profitAmount(calculate.getProfitAmount())
                    .reachLevel(calculate.getReachLevel())
                    .reMarketingFold(calculate.getReMarketingFold())
                    .diffNextLevel(calculate.getDiffNextLevel())
                    .currentFullDiscountLevel(calculate.getCurrentFullDiscountLevel())
                    .currentFullReductionLevel(calculate.getCurrentFullReductionLevel())
                    .currentFullGiftLevel(calculate.getCurrentFullGiftLevel())
                    .nextFullDiscountLevel(calculate.getNextFullDiscountLevel())
                    .nextFullReductionLevel(calculate.getNextFullReductionLevel())
                    .nextFullGiftLevel(calculate.getNextFullGiftLevel())
                    .build();
            List<MarketingGroupCard> marketingGroupCards = Lists.newArrayList();
            marketingGroupCards.add(marketingGroupCard);
            PriceInfoOfWholesale priceInfoOfWholesale = this.totalPriceCalculator(marketingGroupCards, Lists.newArrayList(), request.getCustomerVO());
            return MarketingGroupCardResponse.builder()
                    .marketingGroupCards(marketingGroupCards)
                    .priceInfoOfWholesale(priceInfoOfWholesale)
                    .build();
        }
        // 未达到任何营销活动门槛
        PriceInfoOfWholesale priceInfoOfWholesale = this.totalPriceCalculator(Lists.newArrayList(), devanningGoodsInfoVOS, request.getCustomerVO());
        return MarketingGroupCardResponse.builder()
                .noHaveGoodsInfoVOList(devanningGoodsInfoVOS)
                .priceInfoOfWholesale(priceInfoOfWholesale)
                .build();
    }


    /**
     * 计算单个商品已经达到的营销活动门槛
     */
    public MarketingCalculatorResult optimalMarketingSingleGoods(
            DevanningGoodsInfoVO devanningGoodsInfoVO // 单个商品
    ) {
        // 囤货活动不展示赠品信息
//        BoolFlag isPileShopcart = devanningGoodsInfoVO.getPileFlag();
//        List<Long> availableMarketingIds = new ArrayList<>();
//        if(Objects.nonNull(isPileShopcart) && BoolFlag.YES.equals(isPileShopcart)){
//            availableMarketingIds = getAvailableMarketingIds(Arrays.asList(MarketingType.DISCOUNT,MarketingType.REDUCTION));
//        } else {
//            availableMarketingIds = this.getAvailableMarketingIds();
//        }
        List<Long> availableMarketingIds = this.getAvailableMarketingIds();
        // 系统中有效的营销活动ID与商品ID集合的映射关系
        Map<Long, Set<String>> marketingAndGoodsInfoMaps = new HashMap<>();

        // 系统中营销ID与营销信息的映射关系
        Map<Long, Marketing> avaliableMarketingAndInfoMaps = new HashMap<>();
        for (Long marketingId : availableMarketingIds) {
            if(Objects.isNull(marketingId)){continue;}

            Set<String> scopeIds = this.getValidMarketingScopeIds(marketingId);
            marketingAndGoodsInfoMaps.put(marketingId, scopeIds);

            Marketing marketingInfo = this.getMarketingInfo(marketingId);
            avaliableMarketingAndInfoMaps.put(marketingId, marketingInfo);
        }

        Map<String, Set<Long>> objectObjectHashMap = reverseMarketingAndGoodsInfoMap(marketingAndGoodsInfoMaps);
        String goodsInfoId = devanningGoodsInfoVO.getGoodsInfoId();
        Map<String, DevanningGoodsInfoVO> devanningGoodsInfoVMappings = Maps.newHashMap();
        devanningGoodsInfoVMappings.put(goodsInfoId, devanningGoodsInfoVO);

        // 获取商品参加的所有营销活动ID
        Set<Long> marketingIds = objectObjectHashMap.get(goodsInfoId);
        if(CollectionUtils.isEmpty(marketingIds)){
            return MarketingCalculatorResult.builder().reachLevel(Boolean.FALSE).build();
        }
        // 待重新计算最优的促销分组
        Map<Long, Set<String>> tempResult = Maps.newHashMap();
        for (Long marketingId : marketingIds){
            tempResult.put(marketingId, Sets.newHashSet(goodsInfoId));
        }

        // 组合进行价格计算
        List<MarketingCalculatorResult> todo = Lists.newArrayList();
        for (Map.Entry<Long, Set<String>> map : tempResult.entrySet()) {
            Long marketingId = map.getKey();
            Set<String> goodsInfoIdList = map.getValue();
            Marketing marketing = avaliableMarketingAndInfoMaps.get(marketingId);
            if(Objects.isNull(marketing)){ continue;}

            Map<Long, Set<String>> mapOfCal = Maps.newHashMap();
            mapOfCal.put(marketingId, goodsInfoIdList);
            // 促销分组价格计算
            List<MarketingCalculatorResult> marketingCalculatorResults = this.calculatorResults(mapOfCal, devanningGoodsInfoVMappings, avaliableMarketingAndInfoMaps);
            if(CollectionUtils.isEmpty(marketingCalculatorResults)){ continue; }

            // 这一步是避免不垮单品的情况下出现异常问题
            MarketingCalculatorResult marketingCalculatorResult = marketingCalculatorResults.stream().filter(x -> {
                return x.getGoodsInfoIds().contains(goodsInfoId);
            }).findFirst().orElse(null);
            if(Objects.isNull(marketingCalculatorResult)){ continue; }

            todo.add(marketingCalculatorResult);
        }

        log.info("MarketService optimalMarketingSingleGoods MarketingCalculatorResult: {}", todo);
        // 当达到营销活动门槛，按照门槛扣减的金额，拿到最优的营销活动
        MarketingCalculatorResult reachMarketingCalculatorResult = todo.stream().filter(x -> {
            return Boolean.TRUE.equals(x.getReachLevel());
        }).sorted(Comparator.comparing(MarketingCalculatorResult::getProfitAmount, (x, y) -> {
            return x.compareTo(y);
        }).reversed()).findFirst().orElse(null);

        log.info("MarketService optimalMarketingSingleGoods reachMarketingCalculatorResult: {}", reachMarketingCalculatorResult);

        if(Objects.isNull(reachMarketingCalculatorResult)){
            return MarketingCalculatorResult.builder().reachLevel(Boolean.FALSE).build();
        }

        // 格式化赠品列表
        //赠品状态赋值
        if (Objects.nonNull(reachMarketingCalculatorResult.getCurrentFullGiftLevel())){
            List<String> giftGoodsInfoIds = reachMarketingCalculatorResult.getCurrentFullGiftLevel().getFullGiftDetailList().stream().map(MarketingFullGiftDetailVO::getProductId).collect(Collectors.toList());
            //这里已经判断库存和失效
            List<GiftGoodsInfoVO> giftGoodsInfos = goodsInfoQueryProvider.findGoodsInfoByIdsAndCache(GoodsInfoListByIdsRequest.builder()
                    .goodsInfoIds(giftGoodsInfoIds).matchWareHouseFlag(Boolean.TRUE).build()).getContext().getGoodsInfos();
            log.info("查询出赠品数据=====：{}", giftGoodsInfos);

            if (org.apache.commons.collections4.CollectionUtils.isEmpty(giftGoodsInfos)){
                throw new SbcRuntimeException("商品不存在");
            }
            Map<String, GiftGoodsInfoVO> infoVOMap = giftGoodsInfos.stream().collect(Collectors.toMap(GiftGoodsInfoVO::getGoodsInfoId, Function.identity(), (a, b) -> a));
            List<MarketingFullGiftDetailVO> fullGiftDetailList = reachMarketingCalculatorResult.getCurrentFullGiftLevel().getFullGiftDetailList();
            for (MarketingFullGiftDetailVO marketingFullGiftDetailVO : fullGiftDetailList){
                //填充赠品实体
                if (Objects.isNull(marketingFullGiftDetailVO.getGiftGoodsInfoVO())){
                    if (Objects.nonNull(infoVOMap.get(marketingFullGiftDetailVO.getProductId()))){
                        marketingFullGiftDetailVO.setGiftGoodsInfoVO(infoVOMap.get(marketingFullGiftDetailVO.getProductId()));
                    }
                }
                //限赠状态已经限赠数量
                this.limitPresent(marketingFullGiftDetailVO);

            }
            //排序
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(fullGiftDetailList)){
                reachMarketingCalculatorResult.getCurrentFullGiftLevel().setFullGiftDetailList(
                        reachMarketingCalculatorResult.getCurrentFullGiftLevel().getFullGiftDetailList().stream()
                                .sorted(Comparator.comparing(MarketingFullGiftDetailVO::mgetGitGoodsStaus)
                                        .thenComparing(MarketingFullGiftDetailVO::mgetGitGoodsPrice,Comparator.reverseOrder()))
                                .collect(Collectors.toList()));
            }
        }

        return reachMarketingCalculatorResult;
    }

    private void limitPresent(MarketingFullGiftDetailVO marketingFullGiftDetailVO){
        if(Objects.isNull(marketingFullGiftDetailVO)){
            return;
        }
        String productId = marketingFullGiftDetailVO.getProductId();
        Long marketingId = marketingFullGiftDetailVO.getMarketingId();
        Long giftLevelId = marketingFullGiftDetailVO.getGiftLevelId();
        String key = marketingId.toString()+giftLevelId.toString()+productId;
        String o = redisService.getString(key);
        log.info("赠品库存数量：{}->{}", key, o);
        if (Objects.nonNull(o)){
            Long num = Long.parseLong(o); // 赠品剩余可赠数量
            log.info("赠品{}在营销活动{}下的库存数量：{}", productId, marketingId, num);
            marketingFullGiftDetailVO.getGiftGoodsInfoVO().setStockNum(num);
            if (num.compareTo(0l)<=0){
                marketingFullGiftDetailVO.getGiftGoodsInfoVO().setGoodsStatus(GoodsStatus.OUT_GIFTS_STOCK);
            } else if (num.compareTo(marketingFullGiftDetailVO.getProductNum())<0){
                marketingFullGiftDetailVO.setProductNum(num);
            }
        }
    }


    /**
     * 将映射关系：营销活动ID->商品集合
     * 改变成：商品->营销活动集合
     * @param marketingAndGoodsInfoMaps
     * @return
     */
    private Map<String, Set<Long>> reverseMarketingAndGoodsInfoMap(Map<Long, Set<String>> marketingAndGoodsInfoMaps) {
        Map<String, Set<Long>> objectObjectHashMap = Maps.newHashMap();// 系统中存放的商品和营销活动的映射关系
        for (Map.Entry<Long, Set<String>> map: marketingAndGoodsInfoMaps.entrySet()){
            Long key = map.getKey();
            Set<String> sets = map.getValue();
            if(Objects.isNull(sets)){ continue; }

            for (String goodsInfoId : sets) {
                Set<Long> longs = objectObjectHashMap.get(goodsInfoId);
                if(CollectionUtils.isEmpty(longs)){
                    longs = Sets.newHashSet(key);
                } else {
                    longs.add(key);
                }
                objectObjectHashMap.put(goodsInfoId, longs);
            }
        }
        return objectObjectHashMap;
    }

    /**
     * 进行排列组合
     * @param objectObjectHashMap 营销活动ID与营销活动的映射关系
     * @param notPinMarketingGoodsInfo 未固定营销的商品
     * @param result 当前营销已经计算完成的结果。
     * @return
     */
    private List<Map<Long, Set<String>>> comboMarketingAndGoodsInfo(Map<String, Set<Long>> objectObjectHashMap, Set<String> notPinMarketingGoodsInfo, Map<Long, Set<String>> result) {
        List<String[]> list = Lists.newArrayList();
        for (String goodsInfoId : notPinMarketingGoodsInfo) {
            Set<Long> marketingIds = objectObjectHashMap.get(goodsInfoId);
            log.info("MarketService optimalMarketingBetweenMultGoods notPinMarketingGoodsInfo marketingIds: {}",marketingIds);
            if(CollectionUtils.isEmpty(marketingIds)){ continue; }

            List<String> itemList = Lists.newArrayList();
            for (Long marketingId : marketingIds) {
                itemList.add(marketingId+":"+goodsInfoId);
            }
            String[] itemsArray = new String[itemList.size()];
            itemsArray = itemList.toArray(itemsArray);
            list.add(itemsArray);
        }
        log.info("MarketService optimalMarketingBetweenMultGoods combo before: {}", JSON.toJSONString(list));
        for (int i = 0; i < list.size() - 1; i++) {
            String[] combo = combo(list.get(i), list.get(i + 1));
            list.set(i + 1, combo);
        }
        log.info("MarketService optimalMarketingBetweenMultGoods combo after: {}",JSON.toJSONString(list));

        String[] resultList = list.get(list.size() - 1);// 营销活动和商品的排列组合的结果
        List<Map<Long, Set<String>>> todoList = Lists.newArrayList();
        log.info("MarketService optimalMarketingBetweenMultGoods resultList: {}", JSON.toJSONString(resultList));

        // 每一个循环都是一个促销分组方案
        for (String r : resultList) { // 每一个循环是一个促销分组
            Map<Long, Set<String>> tempMap = Maps.newHashMap();
            String[] split = r.split("&");
            log.info("MarketService optimalMarketingBetweenMultGoods split:{}",JSON.toJSONString(split));
            for (String s : split) {
                String[] split1 = s.split(":");
                Long marketingId = Long.valueOf(split1[0]);
                String goodsInfoId = split1[1];

                Set<String> strings = result.get(marketingId);// 从前面已经分好组的营销活动中拿结果
                if(CollectionUtils.isEmpty(strings)){
                    strings = Sets.newHashSet(goodsInfoId);
                } else {
                    strings.add(goodsInfoId);
                }

                Set<String> strings1 = tempMap.get(marketingId);
                if(CollectionUtils.isEmpty(strings1)){
                    strings1 = Sets.newHashSet(strings);
                } else {
                    strings1.addAll(strings);
                }
                tempMap.put(marketingId, strings1);
            }
            todoList.add(tempMap);
        }
        return todoList;
    }


    /**
     * 固定营销活动与商品之间的关系
     * @param result 还没有固定营销活动的商品
     * @param customerId
     */
    private void pinGoodsMarketingRelate(Map<String, Long> result, String customerId){
        List<GoodsMarketingDTO> goodsMarketings = Lists.newArrayList();
        for (Map.Entry<String, Long> map :result.entrySet()) {
            String goodsInfoId = map.getKey();
            Long marketingId = map.getValue();
            GoodsMarketingDTO dto = GoodsMarketingDTO.builder().marketingId(marketingId)
                    .goodsInfoId(goodsInfoId)
                    .customerId(customerId)
                    .build();
            goodsMarketings.add(dto);
        }
        GoodsMarketingBatchAddRequest request = GoodsMarketingBatchAddRequest.builder()
                .goodsMarketings(goodsMarketings)
                .build();
        goodsMarketingProvider.batchAdd(request);
    }

    /**
     * 分组 + 计算营销价格
     * @param maps key是营销ID，values是商品ID
     * @param devanningGoodsInfoVMappings
     * @param avaliableMarketingAndInfoMaps
     * @return
     */
    private List<MarketingCalculatorResult> calculatorResults(Map<Long, Set<String>> maps, Map<String, DevanningGoodsInfoVO> devanningGoodsInfoVMappings, Map<Long, Marketing> avaliableMarketingAndInfoMaps ){
        if(CollectionUtils.isEmpty(maps)){
            return Lists.newArrayList();
        }
        log.info("=======calculatorResults=======:{}",maps);
        List<MarketingCalculatorResult> result = Lists.newArrayList();
        for (Map.Entry<Long, Set<String>> map: maps.entrySet()) {
            Long marketingId = map.getKey();
            Set<String> goodsInfoIds = map.getValue();
            if(CollectionUtils.isEmpty(goodsInfoIds)){ continue; }

            // 1.未参加营销活动的商品不需要做价格计算
            if(Long.compare(marketingId, 0L) == 0){ // 0标识未参加营销活动
                MarketingCalculatorResult build = MarketingCalculatorResult.builder().goodsInfoIds(goodsInfoIds.stream().collect(Collectors.toList())).build();
                result.add(build);
                continue;
            }

            Marketing marketing = avaliableMarketingAndInfoMaps.get(marketingId);
            if(Objects.isNull(marketing)){ continue; }

            // 2.不垮单品的营销活动，营销活动下的商品需要拆开展示。
            if(BoolFlag.NO.equals(marketing.getIsAddMarketingName())){
                for (String goodsInfoId : goodsInfoIds) {
                    DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoVMappings.get(goodsInfoId);
                    MarketingCalculator caculator = MarketingCalculatorFactory.getCaculator(marketing.getMarketingType().name());
                    List<DevanningGoodsInfoVO> devanningGoodsInfoVOS = Arrays.asList(devanningGoodsInfoVO);
                    MarketingCalculatorResult calculate = caculator.calculate(devanningGoodsInfoVOS, marketing);
                    result.add(calculate);
                }
                continue;
            }

            // 3.促销优惠价格计算
            List<DevanningGoodsInfoVO> devanningGoodsInfos = goodsInfoIds.stream().map(x -> devanningGoodsInfoVMappings.get(x)).collect(Collectors.toList());
            MarketingCalculator caculator = MarketingCalculatorFactory.getCaculator(marketing.getMarketingType().name());
            MarketingCalculatorResult calculate = caculator.calculate(devanningGoodsInfos, marketing);
            result.add(calculate);
        }
        return result;
    }

    /**
     * 营销分组时进行排列组合的助手方法
     */
    private static String[] combo(String[] arr1, String[] arr2) {
        String[] newArr = new String[arr1.length * arr2.length];
        int k = 0;
        for (String s : arr1) {
            for (String ss : arr2) {
                newArr[k] = s + "&" + ss;
                k++;
            }
        }
        return newArr;
    }

    /**
     * 根据商品ID集合进行促销分组
     * @return
     */
    private Map<Long, Set<String>> groupGoodsByMarketingId(
            Set<String> penddingGoodsInfoIds,// 待促销分组的商品集合
            Set<String> overGoodsInfoIds, // 已经计算完成的营销活动的商品ID集合
            Set<Long> avaliableMarketingIds, // 有效的活动ID集合
            Map<Long, Set<String>> marketingAndGoodsInfoMaps, // 营销活动ID与商品ID集合的映射关系(本地缓存)
            Map<Long, Set<String>> pinMarketingGoodsInfoMaps  // 固定营销活动的商品
    ){
        // 促销分组后的结果
        Map<Long, Set<String>> marketingGroupAndGoodsInfoMaps =  new HashMap<>();

        // 按促销进行分组
        for (Long marketingId : avaliableMarketingIds) {
            // 参与活动的所有商品ID（未终止），这个是有后台人员进行配置的。
            Set<String> scopeIds = marketingAndGoodsInfoMaps.get(marketingId);
            if(CollectionUtils.isEmpty(scopeIds)){ continue; } //营销活动关联的有效商品为空

            // 待处理集合中满足营销活动的商品ID (已经完成)
            Set<String> goodsInfoIdsIntersection = Sets.intersection(penddingGoodsInfoIds, scopeIds);
            if(CollectionUtils.isEmpty(goodsInfoIdsIntersection)){ continue; }

            // 排除不是当前营销活动的商品。
            Set<String> excludeGoodsInfoSets = new HashSet<>();
            if(!CollectionUtils.isEmpty(pinMarketingGoodsInfoMaps)){
                for (Map.Entry<Long, Set<String>> pinMarketingGoodsInfoMap: pinMarketingGoodsInfoMaps.entrySet()) {
                    Long key = pinMarketingGoodsInfoMap.getKey();// 营销活动ID和商品ID
                    if(key.equals(marketingId)){
                        continue;
                    }
                    Set<String> value = pinMarketingGoodsInfoMap.getValue();
                    excludeGoodsInfoSets.addAll(value);
                }
            }

            // 排除已经计算完营销活动的商品（已选营销活动和已计算好最优分组的商品集合）
            Set<String> difference = Sets.difference(goodsInfoIdsIntersection, overGoodsInfoIds);
            difference = Sets.difference(difference, excludeGoodsInfoSets);
            if(CollectionUtils.isEmpty(difference)){ continue; }

            marketingGroupAndGoodsInfoMaps.put(marketingId, difference);
        }

        return marketingGroupAndGoodsInfoMaps;
    }

    /**
     * 从mongodb中获取已经选择了营销活动和商品之间的关系
     * @param customerId
     * @return
     */
    private Map<String, Long>  getGoodsMarketingMapsFromMongoDB(String customerId, List<Long> availableMarketingIds){
        log.info("getGoodsMarketingMapsFromMongoDB.customerId:{}",customerId);
        GoodsMarketingListByCustomerIdRequest goodsMarketingListByCustomerIdRequest = new GoodsMarketingListByCustomerIdRequest();
        goodsMarketingListByCustomerIdRequest.setCustomerId(customerId);
        List<GoodsMarketingVO> goodsMarketingVOS = new ArrayList<>();

        BaseResponse<GoodsMarketingListByCustomerIdResponse> response = goodsMarketingQueryProvider.listByCustomerId(goodsMarketingListByCustomerIdRequest);
        Optional<List<GoodsMarketingVO>> optional = Optional.ofNullable(response)
                .map(BaseResponse::getContext)
                .map(GoodsMarketingListByCustomerIdResponse::getGoodsMarketings);
        if(!optional.isPresent()){
            return new HashMap<>();
        }
        goodsMarketingVOS = optional.get();
        log.info("getGoodsMarketingMapsFromMongoDB.goodsMarketingVOS:{}",goodsMarketingVOS);

        if(CollectionUtils.isEmpty(goodsMarketingVOS)){
            return new HashMap<>();
        }
        Map<String, Long> effectiveGoodsMarketingMaps = Maps.newHashMap();
        for (GoodsMarketingVO goodsMarketingVO : goodsMarketingVOS){
            Long marketingId = goodsMarketingVO.getMarketingId();
            String goodsInfoId = goodsMarketingVO.getGoodsInfoId();

            if(!availableMarketingIds.contains(marketingId)){ continue; }
            effectiveGoodsMarketingMaps.put(goodsInfoId, marketingId);
        }
        return effectiveGoodsMarketingMaps;
    }

    /**
     * 避免Redis缓存淘汰的情况，从获取系统中正在进行中的营销活动ID。
     *
     * @return
     */
    private List<Long> getAvailableMarketingIds(){
        return this.getAvailableMarketingIds(null);
    }
    private List<Long> getAvailableMarketingIds(List<MarketingType> marketingTypes){
        List<Marketing> marketingIds = marketingRepository.findAll((Specification<Marketing>) (root, cQuery, cBuilder) -> {
            cQuery.select(root.get("marketingId"));
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cBuilder.equal(root.get("delFlag"), DeleteFlag.NO));
            predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
            predicates.add(cBuilder.notEqual(root.get("isDraft"), BoolFlag.YES));
            predicates.add(cBuilder.equal(root.get("isPause"), BoolFlag.NO));
            if(Objects.nonNull(marketingTypes) && !CollectionUtils.isEmpty(marketingTypes)){
                predicates.add(cBuilder.in(root.get("marketingType")).value(marketingTypes));
            }

            LocalDateTime now = LocalDateTime.now();
            predicates.add(cBuilder.and(
                    cBuilder.lessThanOrEqualTo(root.get("beginTime"), now),// <
                    cBuilder.greaterThanOrEqualTo(root.get("endTime"), now) // >
            ));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });

        if(CollectionUtils.isEmpty(marketingIds)){
            return new ArrayList<>();
        }
        return marketingIds.stream()
                .map(Marketing::getMarketingId)
                .collect(Collectors.toList());
    }

    /**
     * 校验营销活动是否满足终止操作的前置条件
     * 只有进行中的活动才可以被终止
     * @param marketing
     */
    private void checkMarketingCanTerminate(Marketing marketing) {
        boolean marketingIsDraft = Objects.equals(BoolFlag.YES, marketing.getIsDraft());
        boolean marketingTerminated = Objects.equals(BoolFlag.YES, marketing.getTerminationFlag());
        boolean marketingDeleted = Objects.equals(BoolFlag.YES, marketing.getDelFlag());
        boolean marketingStarted = LocalDateTime.now().isAfter(marketing.getBeginTime())
                && LocalDateTime.now().isBefore(marketing.getEndTime());

        if(!marketingStarted        // 活动未开始或已经结束
                || marketingIsDraft     // 草稿
                || marketingTerminated  // 已终止
                || marketingDeleted){   // 已删除
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_PAUSE);
        }
    }

    /**
     * 校验营销活动是否满足删除操作的前置条件
     * 只有未开始的活动和草稿才能被删除
     * @param marketing
     */
    private void checkMarketingCanDelete(Marketing marketing){
         boolean marketingIsNotDraft = Objects.equals(BoolFlag.NO, marketing.getIsDraft());
        boolean marketingTerminated = Objects.equals(BoolFlag.YES, marketing.getTerminationFlag());
//        boolean marketingDeleted = Objects.equals(DeleteFlag.YES, marketing.getDelFlag());
        LocalDateTime now = LocalDateTime.now();
        boolean marketingStarted = now.isAfter(marketing.getBeginTime())
                && now.isBefore(marketing.getEndTime());

        if(marketingIsNotDraft
            && marketingStarted
        ){
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }
    }

    /**
     * 保存营销活动的数据
     * @param marketingStrategy
     * @param request
     * @return
     */
    private Marketing marketingPutStorage(MarketingStrategy marketingStrategy, SaveOrUpdateMarketingRequest request){
        // 营销数据持久化到MySQL
        Marketing marketing = marketingStrategy.saveOrUpdate(request);

        // 清理缓存
        clearMarketingCache(marketing);

        // 草稿状态不写入缓存中
        if(BoolFlag.YES == marketing.getIsDraft()){
            return marketing;
        }

        // 更新缓存
        updateMarketingCache(marketing);

        return marketing;
    }

    /**
     * 新增营销活动
     * @param request
     * @return
     * @throws SbcRuntimeException
     */
    private Marketing saveMarketing(SaveOrUpdateMarketingRequest request) throws SbcRuntimeException{
        log.info("SaveMarketing: {}", request);
        // 加锁防重复提交
        String lockKey = RedisLockKeyConstants.MARKET_REQUEST_KEY + request.getCreatePerson();
        if (!redisLock.tryLock(lockKey)) {
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }
        try {
            // 业务规则校验
            MarketingStrategy marketingStrategy = MarketStrategyFacory.getMarketingStrategy(request.getMarketingType().name());
            MarketingCheckResult marketingCheckResult = marketingStrategy.checkIt(request);
            if(!marketingCheckResult.isSuccess()){
                throw new SbcRuntimeException(marketingCheckResult.getResultCode(), marketingCheckResult.getShowMessage());
            }
            return marketingPutStorage(marketingStrategy, request);
        }  finally {
            redisLock.unlock(lockKey);
        }
    }

    /**
     * 更新营销活动
     * @param request
     * @return
     * @throws SbcRuntimeException
     */
    private Marketing updateMarketing(SaveOrUpdateMarketingRequest request) throws SbcRuntimeException{
        log.info("UpdateMarketing: {}", request);
        String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + request.getMarketingId();
        boolean lock = redisLock.writeLock(marketingUpdateLock);
        if (!lock) {
            log.info("操作营销活动获取锁失败，operator:{}", request.getMarketingId());
            throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
        }

        try {
            // 业务规则校验
            MarketingStrategy marketingStrategy = MarketStrategyFacory.getMarketingStrategy(request.getMarketingType().name());
            MarketingCheckResult marketingCheckResult = marketingStrategy.checkIt(request);
            if(!marketingCheckResult.isSuccess()){
                throw new SbcRuntimeException(marketingCheckResult.getResultCode(), marketingCheckResult.getShowMessage());
            }

            return marketingPutStorage(marketingStrategy, request);
        } finally {
            redisLock.unWriteLock(marketingUpdateLock);
        }
    }

    /**
     * 更新营销活动Redis缓存
     *
     * @param marketing
     */
    private void updateMarketingCache(Marketing marketing) {
        // 校验Scope
        marketing.setMarketingScopeList(this.getAvaliableMarketingScopeInfoListFromDB(marketing.getMarketingId()));
        log.info("营销活动列表：{}", marketing);
        // 校验营销活动等级
        MarketingType marketingType = marketing.getMarketingType();
        if(MarketingType.GIFT.equals(marketingType)){
            List<MarketingFullGiftLevel> levels = marketingFullGiftLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(marketing.getMarketingId());
            for (MarketingFullGiftLevel level : levels){
                Long giftLevelId = level.getGiftLevelId();
                List<MarketingFullGiftDetail> byMarketingIdAndGiftLevelId = marketingFullGiftDetailRepository.findByMarketingIdAndGiftLevelId(marketing.getMarketingId(), giftLevelId);
                byMarketingIdAndGiftLevelId = byMarketingIdAndGiftLevelId.stream().filter(x -> BoolFlag.NO.equals(x.getTerminationFlag())).collect(Collectors.toList());
                level.setFullGiftDetailList(byMarketingIdAndGiftLevelId);
            }
            marketing.setMarketingFullGiftLevels(levels);
        }
        if(MarketingType.REDUCTION.equals(marketingType)){
            List<MarketingFullReductionLevel> byMarketingId = marketingFullReductionLevelRepository.findByMarketingId(marketing.getMarketingId());
            marketing.setMarketingFullReductionLevels(byMarketingId);
        }
        if(MarketingType.DISCOUNT.equals(marketingType)){
            List<MarketingFullDiscountLevel> byMarketingId = marketingFullDiscountLevelRepository.findByMarketingId(marketing.getMarketingId());
            marketing.setMarketingFullDiscountLevels(byMarketingId);
        }

        // 更新Redis中促销活动下商品的限购信息
        updateMarketingScopeHashCache(marketing);

        // 更新Redis中促销活动和商品之间的关系
        updateMarketingScopeStringCache(marketing);

        // 更新Redis中促销活动的规则
        updateMarketingInfoStringCache(marketing);

        // 更新Redis中的赠品信息
        if(Objects.equals(MarketingType.GIFT, marketing.getMarketingType())){
            updateMarketingGiftDetailHashCache(marketing);
            // 更新限赠数量
            updateMarketingGiftNum(marketing);
        }
    }

    /**
     * 更新Redis中促销活动下商品的限购信息
     *
     * marketing_scope_hash:[marketing_id]
     * {
     *  [goods_info_id1] : [总限购数量，单用户限购量]
     *  [goods_info_id2] : [总限购数量，单用户限购量]
     * }
     *
     * @param marketing
     */
    private void updateMarketingScopeHashCache(Marketing marketing){
        String marketingScopeHashKey = RedisKeyConstants.MARKETING_SCOPE_HASH + marketing.getMarketingId();;
        List<MarketingScope> marketingScopeList = marketing.getMarketingScopeList();

        if(CollectionUtils.isEmpty(marketingScopeList)){
            marketingScopeList = this.getAvaliableMarketingScopeInfoListFromDB(marketing.getMarketingId());
        }

        Map<String, String> marketingScopeMap = new HashMap<>();
        for (MarketingScope marketingScope : marketingScopeList) {
            marketingScopeMap.put(marketingScope.getScopeId(), JsonUtil.object2Json(marketingScope));
        }
        redisCache.hPutAll(marketingScopeHashKey, marketingScopeMap);
        long ttl = Duration.between(LocalDateTime.now(), marketing.getEndTime()).toDays();
        redisCache.expire(marketingScopeHashKey, ttl, TimeUnit.DAYS);
    }

    /**
     * 更新Redis中促销活动和商品之间的关系
     * @param marketing
     */
    private void updateMarketingScopeStringCache(Marketing marketing){
        String marketingScopeZsetKey = RedisKeyConstants.MARKETING_SCOPE_STRING + marketing.getMarketingId();
        List<MarketingScope> marketingScopeList = marketing.getMarketingScopeList();
        if(CollectionUtils.isEmpty(marketingScopeList)){
            marketingScopeList = this.getAvaliableMarketingScopeInfoListFromDB(marketing.getMarketingId());
        }
        List<String> skuIds = new ArrayList<>();
        for (MarketingScope goodsItemRequest : marketingScopeList) {
            if(BoolFlag.YES.equals(goodsItemRequest.getTerminationFlag())){
                continue;
            }
            skuIds.add(goodsItemRequest.getScopeId());
        }

        long ttl = Duration.between(LocalDateTime.now(), marketing.getEndTime()).getSeconds();
        redisCache.set(marketingScopeZsetKey, JSONObject.toJSONString(skuIds), ttl);
    }

    /**
     * 更新Redis中促销活动的规则
     * @param marketing
     */
    private void updateMarketingInfoStringCache(Marketing marketing){
        String marketingInfoKey = MarketingInfoCacheSupport.buildKey(marketing.getMarketingId()) ;
        String marketingInfoValue = MarketingInfoCacheSupport.buildValue(marketing);
        long ttl = Duration.between(LocalDateTime.now(), marketing.getEndTime()).getSeconds();
        redisCache.set(marketingInfoKey, marketingInfoValue, ttl);
    }

    /**
     * 营销活动赠品设置库存信息
     * @param marketing
     */
    private void updateMarketingGiftNum(Marketing marketing){
        if(Objects.isNull(marketing)){
            return;
        }
        if(!MarketingType.GIFT.equals(marketing.getMarketingType())){
            return;
        }
        List<MarketingFullGiftLevel> marketingFullGiftLevels = marketing.getMarketingFullGiftLevels();
        if(!CollectionUtils.isEmpty(marketingFullGiftLevels)){
            return;
        }
        for (MarketingFullGiftLevel level : marketingFullGiftLevels){
            List<MarketingFullGiftDetail> marketingFullGiftDetails = level.getMarketingFullGiftDetails();
            if(CollectionUtils.isEmpty(marketingFullGiftDetails)){ continue; }

            for (MarketingFullGiftDetail marketingFullGiftDetail : marketingFullGiftDetails){
                Long boundsNum = marketingFullGiftDetail.getBoundsNum();
                // 如果赠品的限赠数量未设置，不需要写入缓存
                if(Objects.isNull(boundsNum)){
                    continue;
                }
                Long marketingId = marketing.getMarketingId();
                Long giftLevelId = marketingFullGiftDetail.getGiftLevelId();
                String productId = marketingFullGiftDetail.getProductId();
                String key = marketingId + giftLevelId + productId;
                // 如果缓存中已经有了不需要重新设置赠品限购, 避免运营人员在后台添加赠品导致限赠数量被重置
                if(redisService.hasKey(key)){ continue; }
                redisService.setString(key,String.valueOf(boundsNum));
            }
        }
    }

    /**
     * 更新赠品信息
     * @param marketing
     */
    private void updateMarketingGiftDetailHashCache(Marketing marketing){
        String cacheKey = RedisKeyConstants.MARKETING_GIFT_DETAIL_HASH + marketing.getMarketingId();
        Map<String, String> map = new HashMap<>();
        List<MarketingFullGiftLevel> marketingFullGiftLevels = marketing.getMarketingFullGiftLevels();
        if(CollectionUtils.isEmpty(marketingFullGiftLevels)){
            return;
        }
        for (MarketingFullGiftLevel marketingFullGiftLevel : marketingFullGiftLevels) {
            List<MarketingFullGiftDetail> fullGiftDetailList = marketingFullGiftLevel.getMarketingFullGiftDetails();
            // TODO 处理NPE问题，区分空缓存和空数据的情况
            List<MarketingFullGiftDetail> collect = fullGiftDetailList.stream()
                    .filter(s -> Objects.equals(s.getTerminationFlag(), BoolFlag.NO))
                    .collect(Collectors.toList());
            map.put(String.valueOf(marketingFullGiftLevel.getGiftLevelId()),  JSONObject.toJSONString(collect));
        }
        redisCache.hPutAll(cacheKey, map);
        long ttl = Duration.between(LocalDateTime.now(), marketing.getEndTime()).toDays();
        redisCache.expire(cacheKey, ttl, TimeUnit.DAYS);
    }

    /**
     * 清理营销活动相关的缓存
     * @param marketing
     */
    private void clearMarketingCache(Marketing marketing) {
        redisCache.delete(RedisKeyConstants.MARKETING_SCOPE_HASH + marketing.getMarketingId());
        redisCache.delete(RedisKeyConstants.MARKETING_SCOPE_STRING + marketing.getMarketingId());
        redisCache.delete(MarketingInfoCacheSupport.buildKey(marketing.getMarketingId()));
        redisCache.delete(RedisKeyConstants.MARKETING_GIFT_DETAIL_HASH + marketing.getMarketingId());
    }

    private Map<String, MarketingScope> getMarketingScopeInfoList(Long marketingId, List<String> goodsInfoIds){
        List<String> strings = redisCache.multiGet(RedisKeyConstants.MARKETING_SCOPE_HASH + marketingId, goodsInfoIds);
        // 避免空指针异常
        List<String> collect = strings.stream().filter(x -> {return Objects.nonNull(x);}).collect(Collectors.toList());

        log.info("getMarketingScopeInfoList:{}",collect);
        if(!CollectionUtils.isEmpty(collect) && !CollectionUtils.isEmpty(collect)){
            Map<String, MarketingScope> map = new HashMap<>();
            for (String s : collect) {
                MarketingScope marketingScope = JSONObject.parseObject(s, MarketingScope.class);
                map.put(marketingScope.getScopeId(), marketingScope);
            }
            return map;
        }
        return this.getMarketingScopeInfoListFromDB(marketingId, goodsInfoIds);
    }

    private Map<String, MarketingScope> getMarketingScopeInfoListFromDB(Long marketingId, List<String> goodsInfoIds){
        List<MarketingScope> marketingScopes = marketingScopeRepository.findAll((Specification<MarketingScope>) (root, cQuery, cBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
            predicates.add(cBuilder.in(root.get("scopeId")).value(goodsInfoIds));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });
        if(CollectionUtils.isEmpty(marketingScopes)){
            return new HashMap<>();
        }
        Map<String, MarketingScope> result = marketingScopes.stream()
                .collect(Collectors.toMap(x -> x.getScopeId(), x -> x));
        // TODO 写到Redis中
        return result;
    }

    private List<MarketingScope> getAvaliableMarketingScopeInfoListFromDB(Long marketingId){
        List<MarketingScope> marketingScopes = marketingScopeRepository.findAll((Specification<MarketingScope>) (root, cQuery, cBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
            predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });
        if(CollectionUtils.isEmpty(marketingScopes)){
            return Lists.newArrayList();
        }
        return marketingScopes;
    }

    /**
     * 获取营销活动信息（需要关联营销活动的等级）
     * @param marketingId
     * @return
     */
    private Marketing getMarketingInfo(Long marketingId){
        String marketingString = redisCache.get(RedisKeyConstants.MARKETING_INFO_STRING + marketingId);
        if(Objects.nonNull(marketingString)){
            return JSONObject.parseObject(marketingString, Marketing.class);
        }
        return this.getMarketingInfoFromDB(marketingId);
    }

    private Map<Long, Marketing> batchGetMarketingInfo(List<Long> availableMarketingIds){
        Set<String> marketingInfoStringKeys  = Sets.newHashSet();
        for (Long marketingId : availableMarketingIds) {
            marketingInfoStringKeys.add(RedisKeyConstants.MARKETING_INFO_STRING + marketingId);
        }
        List<String> marketingInfoStringList = redisCache.multiGet(marketingInfoStringKeys);
        if(CollectionUtils.isEmpty(marketingInfoStringList)){
            return Maps.newHashMap();
        }

        Set<String> collect = marketingInfoStringList.stream()
                .filter(x -> Objects.nonNull(x))
                .collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(collect)){ return Maps.newHashMap(); }

        Map<Long, Marketing> avaliableMarketingAndInfoMaps = collect.stream()
                .map(x -> {
                    return JSONObject.parseObject(x, Marketing.class);
                }).collect(Collectors.toMap(x -> x.getMarketingId(), y -> y, (a, b) -> a));
        if(CollectionUtils.isEmpty(avaliableMarketingAndInfoMaps)){ return Maps.newHashMap(); }

        Set<Long> collect1 = availableMarketingIds.stream().collect(Collectors.toSet());
        Set<Long> collect2 = avaliableMarketingAndInfoMaps.keySet();
        Set<Long> difference = Sets.difference(collect1, collect2);
        if(CollectionUtils.isEmpty(difference)){return avaliableMarketingAndInfoMaps;}

        for (Long d : difference){
            avaliableMarketingAndInfoMaps.put(d, this.getMarketingInfo(d));
        }
        return avaliableMarketingAndInfoMaps;
    }

    /**
     * 获取营销活动关联的商品ID（过滤已终止的商品）
     * @param marketingId
     * @return
     */
    private Set<String> getValidMarketingScopeIds(Long marketingId){
        String marketingScopeString = redisCache.get(RedisKeyConstants.MARKETING_SCOPE_STRING + marketingId);
        if(Objects.nonNull(marketingScopeString)){
            List<String> strings = JSONObject.parseArray(marketingScopeString, String.class);
            return strings.stream().collect(Collectors.toSet());
        }
        return this.getValidMarketingScopeIdsFromDB(marketingId);
    }

    private Map<Long, Set<String>> batchGetValidMarketingScopeIds(List<Long> availableMarketingIds){
        Map<Long, Set<String>> marketingAndGoodsInfoMaps = new HashMap<>(availableMarketingIds.size());
        Set<String> cacheKeys = Sets.newHashSet();
        for (Long marketingId : availableMarketingIds) {
            cacheKeys.add(RedisKeyConstants.MARKETING_SCOPE_STRING + marketingId);
        }
        List<String> strings = redisCache.multiGet(cacheKeys);
        if(CollectionUtils.isEmpty(strings)){
            return Maps.newHashMap();
        }
        for (int i = 0; i < strings.size(); i++){
            String s = strings.get(i);
            Long aLong = availableMarketingIds.get(0);
            if(Objects.isNull(s)){
                marketingAndGoodsInfoMaps.put(aLong, this.getValidMarketingScopeIds(aLong));
            } else {
                List<String> strings1 = JSONObject.parseArray(s, String.class);
                Set<String> collect = strings1.stream().collect(Collectors.toSet());
                marketingAndGoodsInfoMaps.put(aLong, collect);
            }
        }
        return marketingAndGoodsInfoMaps;
    }


    /**
     * 从数据库中获取营销信息
     * @param marketingId
     * @return
     */
    private Marketing getMarketingInfoFromDB(Long marketingId) {
        Marketing marketing = marketingRepository.findOne((Specification<Marketing>) (root, cQuery, cBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
            predicates.add(cBuilder.equal(root.get("delFlag"), DeleteFlag.NO));
            predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
            predicates.add(cBuilder.equal(root.get("isDraft"), BoolFlag.NO));
            predicates.add(cBuilder.equal(root.get("isPause"), BoolFlag.NO));
            LocalDateTime now = LocalDateTime.now();
            predicates.add(cBuilder.and(
                cBuilder.lessThanOrEqualTo(root.get("beginTime"), now),// <=
                cBuilder.greaterThanOrEqualTo(root.get("endTime"), now) // >=
            ));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        }).orElse(null);

        if(Objects.isNull(marketing)){
            return null;
        }
        MarketingType marketingType = marketing.getMarketingType();
        if(MarketingType.GIFT.equals(marketingType)){
            log.info("Start MarketService.getMarketingInfoFromDB.GIFT. \n");
            List<MarketingFullGiftLevel> marketingFullGiftLevels = marketingFullGiftLevelRepository.findAll((Specification<MarketingFullGiftLevel>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            });
            marketing.setMarketingFullGiftLevels(marketingFullGiftLevels);
            log.info("{} \n",marketing);
            log.info("End MarketService.getMarketingInfoFromDB.GIFT. \n");
        }
        if(MarketingType.REDUCTION.equals(marketingType)){
            log.info("Start MarketService.getMarketingInfoFromDB.Reduce. \n");
            List<MarketingFullReductionLevel> marketingFullReductionLevels = marketingFullReductionLevelRepository.findAll((Specification<MarketingFullReductionLevel>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            });
            marketing.setMarketingFullReductionLevels(marketingFullReductionLevels);
            log.info("{}\n",marketing);
            log.info("End MarketService.getMarketingInfoFromDB.Reduce. \n");
        }
        if(MarketingType.DISCOUNT.equals(marketingType)){
            log.info("Start MarketService.getMarketingInfoFromDB.DISCOUNT. \n");
            List<MarketingFullDiscountLevel> marketingFullDiscountLevels = marketingFullDiscountLevelRepository.findAll((Specification<MarketingFullDiscountLevel>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("marketingId"), marketingId));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            });
            marketing.setMarketingFullDiscountLevels(marketingFullDiscountLevels);
            log.info("{} \n",marketing);
            log.info("End MarketService.getMarketingInfoFromDB.DISCOUNT. \n");
        }
        // TODO 写入缓存
        return marketing;
    }

    /**
     * 从数据库中获取营销信息
     * @param marketingId
     * @return
     */
    private Set<String> getValidMarketingScopeIdsFromDB(Long marketingId) {

        List<MarketingScope> marketingScopes = marketingScopeRepository.findAll((Specification<MarketingScope>) (root, cQuery, cBuilder) -> {
            cQuery.select(root.get("scopeId"));

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cBuilder.in(root.get("marketingId")).value(marketingId));
            predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));

            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });

        if(CollectionUtils.isEmpty(marketingScopes)){
            return new HashSet<>();
        }
        // TODO 写入缓存
        return marketingScopes.stream()
                .map(MarketingScope::getScopeId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取 Map<goodsinfoid,List<MarketingVO>> 的集合
     * @param goodsInfoIds
     * @return
     */
    public Map<String,List<MarketingVO>> getMarketingByGoodsInfoId(Set<String> goodsInfoIds){
        Map<Long,Set<String>> map = new HashMap<>();
        Map<String,List<MarketingVO>> re= new HashMap<>();
        // 1. 获取正在进行中的所有有效的营销活动ID（From MySQL）
        List<Long> allValidMarketingIds = this.getAvailableMarketingIds();
        for (Long marketingId:allValidMarketingIds){
            Set<String> scopeIds = this.getValidMarketingScopeIds(marketingId); // TODO 这个结果可以缓存到本地
            // 取交集：待处理集合集合中满足营销活动的商品ID
            Set<String> goodsInfoIdsIntersection = Sets.intersection(goodsInfoIds, scopeIds);
            if (CollectionUtils.isEmpty(goodsInfoIdsIntersection)){continue;}
            map.put(marketingId,goodsInfoIdsIntersection);
        }
        if (CollectionUtils.isEmpty(map)){
            return null;
        }
        for (Map.Entry<Long, Set<String>> a :map.entrySet()){
            Set<String> value = a.getValue();
            for (String goodsinfoid:value){
                if (CollectionUtils.isEmpty(re.get(goodsinfoid))){
                    List<MarketingVO> marketingVOS = new LinkedList<>();
                    marketingVOS.add(KsBeanUtil.convert(this.getMarketingInfo(a.getKey()),MarketingVO.class));
                    re.put(goodsinfoid,marketingVOS);
                }else {
                    List<MarketingVO> marketingVOS = re.get(goodsinfoid);
                    marketingVOS.add(KsBeanUtil.convert(this.getMarketingInfo(a.getKey()),MarketingVO.class));
                    re.put(goodsinfoid,marketingVOS);
                }
            }
        }
        return re;
    }

    /**
     * 查询营销是否失效以及赠品是否失效 redis  用于生成快照的使用
     * @param tradeMarketingList
     * @param tradeItems
     * @param wareId
     * @return
     */
    public MarketingByGoodsInfoIdAndIdResponse getEffectiveMarketingByIdsAndGoods(List<TradeMarketingDTO> tradeMarketingList,
                                                                                  List<TradeItemInfo> tradeItems, Long wareId){
        StopWatch stopWatch = new StopWatch("查询营销是否失效以及赠品是否失效用于生成快照的使用消耗详情");
        stopWatch.start("验证商品数据有效性");
        log.info("MarketingService getEffectiveMarketingByIdsAndGoods Orgin tradeMarketingList : {}",tradeMarketingList);
        //失效营销
        List<TradeMarketingDTO> removemarketinglist = new LinkedList<>();
        //失效赠品
        List<String> removelist= new LinkedList<>();

        // 一个商品只能参加一个营销活动，避免前端传过来的数据导致商品重复参加营销活动
        if(!CollectionUtils.isEmpty(tradeMarketingList)){
            List<List<String>> skuIdList = tradeMarketingList.stream().map(TradeMarketingDTO::getSkuIds).collect(Collectors.toList());
            Set<String> allSkuIds = Sets.newHashSet();
            for (List<String> skuIds : skuIdList) {
                if(CollectionUtils.isEmpty(skuIds)){continue;}
                Set<String> strings = Sets.newHashSet(skuIds);
                Set<String> intersection = Sets.intersection(allSkuIds, strings);
                if(CollectionUtils.isEmpty(intersection)){
                    allSkuIds.addAll(skuIds);
                    continue;
                }
                // 如果SKU有交集，说明传递的营销活动有重复。
                // 一个商品只允许参加一个营销活动
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "请刷新购物车后提交订单");
            }
        }
        stopWatch.stop();
        stopWatch.start("验证营销有效性");
        //查询营销是否有效 无效就移除
        tradeMarketingList = tradeMarketingList.stream().filter(x -> {
            Long marketingId = x.getMarketingId();
            if(Objects.isNull(marketingId)){
                return false;
            }
            Marketing marketing = this.getMarketingInfo(marketingId);
            if (Objects.nonNull(marketing)){
                return true;
            }
            removemarketinglist.add(x);
            return false;
        }).collect(Collectors.toList());
        stopWatch.stop();
        stopWatch.start("营销分组");
        log.info("MarketingService getEffectiveMarketingByIdsAndGoods tradeMarketingList: {}",tradeMarketingList);
        log.info("MarketingService getEffectiveMarketingByIdsAndGoods removemarketinglist: {}",removemarketinglist);
        //查商品是否对应营销
//        Map<Long, List<TradeMarketingDTO>> longListMap = tradeMarketingList.stream().collect(Collectors.groupingBy(TradeMarketingDTO::getMarketingId));
//        Map<Long, List<List<String>>> collect = longListMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, i ->
//                i.getValue().stream().map(TradeMarketingDTO::getSkuIds).collect(Collectors.toList())
//        ));

        //请求信息根据营销活动分组
        Map<Long, List<TradeMarketingDTO>> longListMap = tradeMarketingList.stream().collect(Collectors.groupingBy
                (TradeMarketingDTO::getMarketingId));
        Map<Long, List<String>> collect = longListMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                i -> i.getValue().stream().flatMap(m -> m.getSkuIds().stream()).collect(Collectors.toList())));
        stopWatch.stop();
        stopWatch.start("判断营销");
        tradeMarketingList.forEach(v->{
            List<String> lists = collect.get(v.getMarketingId());
            String s = redisCache.get(RedisKeyConstants.MARKETING_SCOPE_STRING.concat(v.getMarketingId().toString()));
            if (StringUtils.isNotBlank(s)){
                JSONArray list = JSONObject.parseArray(s);
                log.info("===============营销集合"+list);
                log.info("===============商品集合"+lists);
                if (lists.stream().anyMatch(pa->{
                    if (list.size()==1){
                        return !list.contains(pa)&&!list.contains("all");
                    }
                    return !list.contains(pa);
                })){
                    //不匹配有问题
                    throw new SbcRuntimeException("K-000001");
                }
            }
        });
        stopWatch.stop();
        stopWatch.start("验证赠品");
        //赠品信息集合
        List<String> giftSkusList = tradeMarketingList.stream().filter(v -> {
            if (CollectionUtils.isEmpty(v.getGiftSkuIds())) {
                return false;
            }
            return true;
        }).flatMap(v -> v.getGiftSkuIds().stream().distinct())
                .collect(Collectors.toList());
        //检查赠品库存，限赠，有效信息
        if (!giftSkusList.isEmpty()) {
            GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                    .goodsInfoIds(giftSkusList)
                    .isHavSpecText(Constants.yes)
                    .wareId(wareId)
                    .build();
            GoodsInfoViewByIdsResponse context = goodsInfoQueryProvider.listViewByIdsLimitWareId(goodsInfoRequest).getContext();
            //和赠品相同的商品
            List<TradeItemInfo> samegoods = tradeItems.stream().filter(i -> giftSkusList.contains(i.getSkuId())).collect(Collectors.toList());
            Map<String, List<TradeItemInfo>> collect1 = samegoods.stream().collect(Collectors.groupingBy(TradeItemInfo::getSkuId));
            Map<String, GoodsInfoVO> goodsInfoVOMap = context.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            Map<String, GoodsVO> goodsVOMap = context.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
            giftSkusList.forEach(v->{
                GoodsInfoVO goodsInfo = goodsInfoVOMap.get(v);
                GoodsVO goods = goodsVOMap.get(goodsInfo.getGoodsId());
                //校验赠品库存，删除，上下架状态
                if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                        || goods.getAuditStatus() == CheckStatus.FORBADE) {
                    //需要移除赠品
                    removelist.add(v);
                }
                BigDecimal stock = goodsInfo.getStock();
                //考虑拆箱
                BigDecimal reduce = BigDecimal.ZERO;
                if (!CollectionUtils.isEmpty( collect1.get(v))){
                    reduce = collect1.get(v).stream().map(vb ->
                            BigDecimal.valueOf(vb.getNum()).multiply(vb.getDivisorFlag())
                    ).reduce(BigDecimal.ZERO, BigDecimal::add);
                }

                if (stock.compareTo(reduce)<0){
                    removelist.add(v);
                }
            });
            for (TradeMarketingDTO p: tradeMarketingList){
                p.getGiftSkuIds().removeIf(l-> removelist.contains(l));
            }
        }
        stopWatch.stop();
        log.info("生成快照接口判断营销接口耗时"+stopWatch.prettyPrint());
        return MarketingByGoodsInfoIdAndIdResponse.builder().removelist(removelist)
                .removemarketinglist(removemarketinglist)
                .tradeMarketingList(tradeMarketingList).build();


    }

    /**
     * 查询营销是否失效以及赠品是否失效 mysql  用于下单 和查询快照接口使用
     * @param tradeMarketingList
     * @param tradeItems
     * @param wareId
     * @return
     */
    public MarketingByGoodsInfoIdAndIdResponse getEffectiveMarketingByIdsAndGoodsByMysql(List<TradeMarketingDTO> tradeMarketingList,
                                                                                         List<TradeItemInfo> tradeItems, Long wareId){
        //失效营销
        List<TradeMarketingDTO> removemarketinglist = new LinkedList<>();
        //失效赠品
        List<String> removelist= new LinkedList<>();

        // 一个商品只能参加一个营销活动，避免前端传过来的数据导致商品重复参加营销活动
        if(!CollectionUtils.isEmpty(tradeMarketingList)){
            List<List<String>> skuIdList = tradeMarketingList.stream().map(TradeMarketingDTO::getSkuIds).collect(Collectors.toList());
            Set<String> allSkuIds = Sets.newHashSet();
            for (List<String> skuIds : skuIdList) {
                if(CollectionUtils.isEmpty(skuIds)){continue;}
                Set<String> strings = Sets.newHashSet(skuIds);
                Set<String> intersection = Sets.intersection(allSkuIds, strings);
                if(CollectionUtils.isEmpty(intersection)){
                    allSkuIds.addAll(skuIds);
                    continue;
                }
                // 如果SKU有交集，说明传递的营销活动有重复。
                // 一个商品只允许参加一个营销活动
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "请刷新购物车后提交订单");
            }
        }

        //查询营销是否有效 无效就移除
        tradeMarketingList.removeIf(l -> {
                    if (  Objects.isNull(this.getMarketingInfoFromDB(l.getMarketingId()))){
                        removemarketinglist.add(l);
                        return true;
                    }
                    return false;
                }
        );
        //查商品是否对应营销
        Map<Long, List<TradeMarketingDTO>> longListMap = tradeMarketingList.stream().collect(Collectors.groupingBy(TradeMarketingDTO::getMarketingId));
        Map<Long, List<List<String>>> collect = longListMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, i ->
                i.getValue().stream().map(TradeMarketingDTO::getSkuIds).collect(Collectors.toList())
        ));
        tradeMarketingList.forEach(v->{
            List<List<String>> lists = collect.get(v.getMarketingId());

            //校验关联商品是否匹配
            List<String> list = marketingScopeService.findByMarketingId(v.getMarketingId())
                    .stream()
                    .map(MarketingScope::getScopeId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)){
                //不匹配有问题
                throw new SbcRuntimeException("K-000001");
            }
            if (lists.stream().anyMatch(pa->{
                if (list.size()==1){
                    return !list.contains(pa)&&!list.contains("all");
                }
                return !list.contains(pa);
            })){
                //不匹配有问题
                throw new SbcRuntimeException("K-000001");
            }

        });
        //赠品信息集合
        List<String> giftSkusList = tradeMarketingList.stream().filter(v -> {
            if (CollectionUtils.isEmpty(v.getGiftSkuIds())) {
                return false;
            }
            return true;
        }).flatMap(v -> v.getGiftSkuIds().stream().distinct())
                .collect(Collectors.toList());
        //检查赠品库存，限赠，有效信息
        if (!giftSkusList.isEmpty()) {
            GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                    .goodsInfoIds(giftSkusList)
                    .isHavSpecText(Constants.yes)
                    .wareId(wareId)
                    .build();
            GoodsInfoViewByIdsResponse context = goodsInfoQueryProvider.listViewByIdsLimitWareId(goodsInfoRequest).getContext();
            //和赠品相同的商品
            List<TradeItemInfo> samegoods = tradeItems.stream().filter(i -> giftSkusList.contains(i.getSkuId())).collect(Collectors.toList());
            Map<String, List<TradeItemInfo>> collect1 = samegoods.stream().collect(Collectors.groupingBy(TradeItemInfo::getSkuId));
            Map<String, GoodsInfoVO> goodsInfoVOMap = context.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
            Map<String, GoodsVO> goodsVOMap = context.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
            giftSkusList.forEach(v->{
                GoodsInfoVO goodsInfo = goodsInfoVOMap.get(v);
                GoodsVO goods = goodsVOMap.get(goodsInfo.getGoodsId());
                //校验赠品库存，删除，上下架状态
                if (goodsInfo.getDelFlag() == DeleteFlag.YES || goodsInfo.getAddedFlag() == 0
                        || goods.getAuditStatus() == CheckStatus.FORBADE) {
                    //需要移除赠品
                    removelist.add(v);
                }
                BigDecimal stock = goodsInfo.getStock();
                //考虑拆箱
                BigDecimal reduce = collect1.get(v).stream().map(vb ->
                        BigDecimal.valueOf(vb.getNum()).multiply(vb.getDivisorFlag())
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (stock.compareTo(reduce)<0){
                    removelist.add(v);
                }


            });
            for (TradeMarketingDTO p: tradeMarketingList){
                p.getGiftSkuIds().removeIf(l-> removelist.contains(l));
            }
        }




        return MarketingByGoodsInfoIdAndIdResponse.builder().removelist(removelist)
                .removemarketinglist(removemarketinglist)
                .tradeMarketingList(tradeMarketingList).build();


    }

    /**
     * 将营销数据同步到Redis缓存中
     */
    public void syncCacheFromPersistence(){
        List<Marketing> marketings = marketingRepository.findAll((Specification<Marketing>) (root, cQuery, cBuilder) -> {
            cQuery.select(root.get("marketingId"));
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cBuilder.equal(root.get("delFlag"), DeleteFlag.NO));
            predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
            predicates.add(cBuilder.notEqual(root.get("isDraft"), BoolFlag.YES));
            predicates.add(cBuilder.equal(root.get("isPause"), BoolFlag.NO));
            LocalDateTime now = LocalDateTime.now();
            predicates.add(cBuilder.and(
                    cBuilder.lessThanOrEqualTo(root.get("beginTime"), now),// <
                    cBuilder.greaterThanOrEqualTo(root.get("endTime"), now) // >
            ));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });

        if(CollectionUtils.isEmpty(marketings)){ return; }

        for (Marketing marketing : marketings) {
            Long marketingId = marketing.getMarketingId();
            String marketingUpdateLock = RedisLockKeyConstants.MARKETING_UPDATE_LOCK_PREFIX + marketingId;
            boolean lock = redisLock.writeLock(marketingUpdateLock);
            if (!lock) {
                log.info("操作营销活动获取锁失败，operator:{}", marketingId);
                throw new SbcRuntimeException(CommonErrorCode.REPEAT_REQUEST);
            }
            try {
                Marketing marketingInfo = this.getMarketingInfoFromDB(marketingId);
                if(Objects.isNull(marketingInfo)){
                    continue;
                }
                clearMarketingCache(marketingInfo);

                List<MarketingScope> marketingScopes = marketingScopeRepository.findAll((Specification<MarketingScope>) (root, cQuery, cBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cBuilder.equal(root.get("marketingId"), marketingInfo.getMarketingId()));
                    predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
                    Predicate[] array = new Predicate[predicates.size()];
                    return cBuilder.and(predicates.toArray(array));
                });

                marketingInfo.setMarketingScopeList(marketingScopes);
                updateMarketingInfoStringCache(marketingInfo); // MARKETING_INFO_STRING

                if(!CollectionUtils.isEmpty(marketingScopes)){
                    List<String> skuIds = marketingScopes.stream()
                            .map(MarketingScope::getScopeId)
                            .collect(Collectors.toList());
                    long ttl = Duration.between(LocalDateTime.now(), marketing.getEndTime()).getSeconds();
                    redisCache.set(RedisKeyConstants.MARKETING_SCOPE_STRING+marketingId, JSONObject.toJSONString(skuIds), ttl);// MARKETING_SCOPE_STRING
                }
                // 更新促销活动的商品限购信息
                updateMarketingScopeHashCache(marketingInfo);

                // // MARKETING_GIFT_DETAIL_HASH
                if(MarketingType.GIFT.equals(marketingInfo.getMarketingType())){
                    updateMarketingGiftDetailHashCache(marketingInfo);
                }
            } finally {
                redisLock.unWriteLock(marketingUpdateLock);
            }
        }
    }

    /**
     * 将营销数据同步到Redis缓存中
     */
    public void syncCacheFromPersistence2(){
        List<Marketing> marketings = marketingRepository.findAll((Specification<Marketing>) (root, cQuery, cBuilder) -> {
            cQuery.select(root.get("marketingId"));
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cBuilder.equal(root.get("delFlag"), DeleteFlag.NO));
            predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
            predicates.add(cBuilder.notEqual(root.get("isDraft"), BoolFlag.YES));
            predicates.add(cBuilder.equal(root.get("isPause"), BoolFlag.NO));
            LocalDateTime now = LocalDateTime.now();
            predicates.add(cBuilder.and(
                    cBuilder.lessThanOrEqualTo(root.get("beginTime"), now),// <
                    cBuilder.greaterThanOrEqualTo(root.get("endTime"), now) // >
            ));
            Predicate[] array = new Predicate[predicates.size()];
            return cBuilder.and(predicates.toArray(array));
        });

        if(CollectionUtils.isEmpty(marketings)){ return; }

        for (Marketing marketing : marketings) {

            Long marketingId = marketing.getMarketingId();
            Marketing marketingInfo = this.getMarketingInfoFromDB(marketingId);

            List<MarketingScope> marketingScopes = marketingScopeRepository.findAll((Specification<MarketingScope>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("marketingId"), marketingInfo.getMarketingId()));
                predicates.add(cBuilder.equal(root.get("terminationFlag"), BoolFlag.NO));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            });
            if(Objects.isNull(marketingInfo)){
                continue;
            }
            marketingInfo.setMarketingScopeList(marketingScopes);
            updateMarketingInfoStringCache(marketingInfo); // MARKETING_INFO_STRING

            if(!CollectionUtils.isEmpty(marketingScopes)){
                List<String> skuIds = marketingScopes.stream()
                        .map(MarketingScope::getScopeId)
                        .collect(Collectors.toList());
                long ttl = Duration.between(LocalDateTime.now(), marketing.getEndTime()).getSeconds();
                redisCache.set(RedisKeyConstants.MARKETING_SCOPE_STRING+marketingId, JSONObject.toJSONString(skuIds), ttl);// MARKETING_SCOPE_STRING
            }
            // 更新促销活动的商品限购信息
            updateMarketingScopeHashCache(marketingInfo);

            // // MARKETING_GIFT_DETAIL_HASH
            if(MarketingType.GIFT.equals(marketingInfo.getMarketingType())){
                updateMarketingGiftDetailHashCache(marketingInfo);
            }

        }
    }

    /**
     * 购物车中的营销活动重新计算价格
     * @param wraperDTOList
     * @return
     */
    public List<TradeMarketingWrapperVO> batchWrapper(List<TradeMarketingWrapperDTO> wraperDTOList){
        log.info("MarketService batchWrapper Old wraperDTOList:{}",JSON.toJSONString(wraperDTOList));
        if(CollectionUtils.isEmpty(wraperDTOList)){
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        List<TradeMarketingWrapperVO> tradeMarketingWrapperVOS = Lists.newArrayList();

        // 如果营销活动相同则合并订单项进行校验
        Map<String, List<TradeMarketingWrapperDTO>>  groupTradeMarketingMap = Maps.newHashMap();
        List<TradeMarketingWrapperDTO> newWraperDTOList = Lists.newArrayList();
        for (TradeMarketingWrapperDTO wraperDTO : wraperDTOList) {
            TradeMarketingDTO tradeMarketingDTO = wraperDTO.getTradeMarketingDTO();
            if(Objects.isNull(tradeMarketingDTO)){continue;}

            Long marketingId = tradeMarketingDTO.getMarketingId();
            Long marketingLevelId = tradeMarketingDTO.getMarketingLevelId();

            // 不跨单品，不能进行合并
            Marketing marketingInfo = this.getMarketingInfo(marketingId);
            if(Objects.isNull(marketingInfo)){ continue; }
            if(BoolFlag.NO.equals(marketingInfo.getIsAddMarketingName())){
                newWraperDTOList.add(wraperDTO);
                continue;
            }

            String mapKey = marketingId+":"+marketingLevelId;
            List<TradeMarketingWrapperDTO> tradeMarketingWrapperDTOS = groupTradeMarketingMap.get(mapKey);
            if(CollectionUtils.isEmpty(tradeMarketingWrapperDTOS)){
                groupTradeMarketingMap.put(mapKey, Lists.newArrayList(wraperDTO));
                continue;
            }

            tradeMarketingWrapperDTOS.add(wraperDTO);
            groupTradeMarketingMap.put(mapKey, tradeMarketingWrapperDTOS);
        }
        log.info("MarketService batchWrapper groupTradeMarketingMap:{}",JSON.toJSONString(groupTradeMarketingMap));

        // Map进行合并
        if(!CollectionUtils.isEmpty(groupTradeMarketingMap)){
            for (Map.Entry<String, List<TradeMarketingWrapperDTO>> map :groupTradeMarketingMap.entrySet()) {
                List<TradeMarketingWrapperDTO> list = map.getValue();
                if(CollectionUtils.isEmpty(list)){
                    continue;
                }
                if(list.size() == 1){
                    newWraperDTOList.add(list.get(0));
                    continue;
                }
                // 分组合并
                List<TradeItemInfoDTO> tradeItemInfoDTOS  = Lists.newArrayList();
                Boolean forceCommit = false;
                Long marketingId = null;
                Long marketingLevelId = null;
                String customerId = null;
                String couponCodeId = null;
                List<String> skuIds = Lists.newArrayList();
                List<String> giftSkuIds = Lists.newArrayList();
                List<String> giftErpSkuNos = Lists.newArrayList();
                // 合并营销跨单品的情况
                for (TradeMarketingWrapperDTO tradeMarketingWrapperDTO : list) {
                    tradeItemInfoDTOS.addAll(Optional.ofNullable(tradeMarketingWrapperDTO.getTradeItems()).orElse(Lists.newArrayList()));
                    customerId = tradeMarketingWrapperDTO.getCustomerId();
                    couponCodeId = tradeMarketingWrapperDTO.getCouponCodeId();

                    TradeMarketingDTO tradeMarketingDTO = tradeMarketingWrapperDTO.getTradeMarketingDTO();
                    skuIds.addAll(Optional.ofNullable(tradeMarketingDTO.getSkuIds()).orElse(Lists.newArrayList()));
                    giftErpSkuNos.addAll(Optional.ofNullable(tradeMarketingDTO.getGiftErpSkuNos()).orElse(Lists.newArrayList()));
                    giftSkuIds.addAll(Optional.ofNullable(tradeMarketingDTO.getGiftSkuIds()).orElse(Lists.newArrayList()));
                    marketingId = tradeMarketingDTO.getMarketingId();
                    marketingLevelId = tradeMarketingDTO.getMarketingLevelId();
                }
                if(!CollectionUtils.isEmpty(giftSkuIds)){
                    giftSkuIds = giftSkuIds.stream().distinct().collect(Collectors.toList());
                }
                if(!CollectionUtils.isEmpty(giftErpSkuNos)){
                    giftErpSkuNos = giftErpSkuNos.stream().distinct().collect(Collectors.toList());
                }
                // 构建新的WraperDTOList
                TradeMarketingDTO build = TradeMarketingDTO.builder()
                        .marketingId(marketingId).marketingLevelId(marketingLevelId)
                        .skuIds(skuIds).giftSkuIds(giftSkuIds)
                        .giftErpSkuNos(giftErpSkuNos).build();
                newWraperDTOList.add(TradeMarketingWrapperDTO.builder().customerId(customerId)
                        .tradeItems(tradeItemInfoDTOS)
                        .tradeMarketingDTO(build)
                        .couponCodeId(couponCodeId)
                        .forceCommit(forceCommit).build());
            }
        }
        log.info("MarketService batchWrapper New wraperDTOList:{}",JSON.toJSONString(newWraperDTOList));


        for (TradeMarketingWrapperDTO wraperDTO: newWraperDTOList) {
            TradeMarketingDTO tradeMarketingDTO = wraperDTO.getTradeMarketingDTO();
            Long marketingLevelId = tradeMarketingDTO.getMarketingLevelId();

            Long marketingId = tradeMarketingDTO.getMarketingId();
            Optional<Marketing> marketingOptional = marketingRepository.findById(marketingId);
            if(!marketingOptional.isPresent()){
                throw new SbcRuntimeException("K-050312");
            }
            Marketing marketing = marketingOptional.get();
            log.info("MarketingService batchWrapper marketing:{},{}",marketing,this.marketingIsAvaliable(marketing));
            if(Boolean.FALSE.equals(this.marketingIsAvaliable(marketing))){
                throw new SbcRuntimeException("K-050312");
            }
            MarketingType marketingType = marketing.getMarketingType();
            MarketingLevel marketLevelByIdAndMarketingTypeFromDB = this.getMarketLevelByIdAndMarketingTypeFromDB(marketingLevelId, marketingType);
            if(Objects.isNull(marketLevelByIdAndMarketingTypeFromDB)){
                throw new SbcRuntimeException("K-050312");
            }

            MarketingCalculator caculator = MarketingCalculatorFactory.getCaculator(marketingType.name());

            List<TradeItemInfoDTO> tradeItems = wraperDTO.getTradeItems();
            List<TradeItemInfo> tradeItemInfoList = KsBeanUtil.convert(tradeItems, TradeItemInfo.class);
            MarketingCaculatorData marketingCaculatorData = MarketingCaculatorData.builder()
                    .tradeItems(tradeItemInfoList)
                    .tradeMarketingDTO(tradeMarketingDTO)
                    .build();
            MarketingCalculatorResult calculate = caculator.calculate(marketingCaculatorData);
            log.info("MarketingService batchWrapper marketingCalculatorResult:{}", JSON.toJSONString(calculate));
            TradeMarketingWrapperVO tradeMarketingWrapperVO = new TradeMarketingWrapperVO();

            Optional<MarketingCalculatorResult> optional = Optional.ofNullable(calculate);
            TradeMarketingVO tradeMarketingVO = TradeMarketingVO.builder()
                    .marketingId(optional.map(MarketingCalculatorResult::getMarketingId).orElse(null))
                    .marketingName(optional.map(MarketingCalculatorResult::getMarketingName).orElse(null))
                    .marketingType(optional.map(MarketingCalculatorResult::getMarketingType).orElse(null))
                    .skuIds(optional.map(MarketingCalculatorResult::getGoodsInfoIds).orElse(null))
                    .subType(optional.map(MarketingCalculatorResult::getSubType).orElse(null))
                    .fullDiscountLevel(optional.map(MarketingCalculatorResult::getCurrentFullDiscountLevel).orElse(null))
                    .giftLevel(optional.map(MarketingCalculatorResult::getCurrentFullGiftLevel).orElse(null))
                    .reductionLevel(optional.map(MarketingCalculatorResult::getCurrentFullReductionLevel).orElse(null))
                    .discountsAmount(optional.map(MarketingCalculatorResult::getProfitAmount).orElse(null))
                    .realPayAmount(optional.map(MarketingCalculatorResult::getPayableAmount).orElse(null))
                    .giftIds(optional.map(MarketingCalculatorResult::getGiftIds).orElse(Lists.newArrayList()))
                    .isOverlap(optional.map(MarketingCalculatorResult::getIsOverlap).orElse(null))
                    .multiple(optional.map(MarketingCalculatorResult::getReMarketingFold).map(BigDecimal::longValue).orElse(null))
                    .build();
            tradeMarketingWrapperVO.setTradeMarketing(tradeMarketingVO);
            tradeMarketingWrapperVOS.add(tradeMarketingWrapperVO);
        }

        return tradeMarketingWrapperVOS;
    }

    private Boolean marketingIsAvaliable(Marketing marketing){
        if(Objects.isNull(marketing)){
            return Boolean.FALSE;
        }
        if(DeleteFlag.YES.equals(marketing.getDelFlag())){
            return Boolean.FALSE;
        }
        if(BoolFlag.YES.equals(marketing.getIsPause())){
            return Boolean.FALSE;
        }
        if(BoolFlag.YES.equals(marketing.getTerminationFlag())){
            return Boolean.FALSE;
        }
        if(BoolFlag.YES.equals(marketing.getIsDraft())){
            return Boolean.FALSE;
        }
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(marketing.getEndTime()) || now.isBefore(marketing.getBeginTime())){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private MarketingLevel getMarketLevelByIdAndMarketingTypeFromDB(Long levelId, MarketingType marketingType){
        MarketingLevel marketingLevel = new MarketingLevel();
        if(MarketingType.GIFT.equals(marketingType)){
            MarketingFullGiftLevel level = marketingFullGiftLevelRepository.findOne((Specification<MarketingFullGiftLevel>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("giftLevelId"), levelId));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            }).orElse(null);
            marketingLevel.setMarketingFullGiftLevel(level);
        }
        if(MarketingType.REDUCTION.equals(marketingType)){
            MarketingFullReductionLevel level = marketingFullReductionLevelRepository.findOne((Specification<MarketingFullReductionLevel>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("reductionLevelId"), levelId));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            }).orElse(null);;
            marketingLevel.setMarketingFullReductionLevel(level);
        }
        if(MarketingType.DISCOUNT.equals(marketingType)){
            MarketingFullDiscountLevel level = marketingFullDiscountLevelRepository.findOne((Specification<MarketingFullDiscountLevel>) (root, cQuery, cBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cBuilder.equal(root.get("discountLevelId"), levelId));
                Predicate[] array = new Predicate[predicates.size()];
                return cBuilder.and(predicates.toArray(array));
            }).orElse(null);
            marketingLevel.setMarketingFullDiscountLevel(level);
        }
        if(Objects.nonNull(marketingLevel.getMarketingFullDiscountLevel())
                || Objects.nonNull(marketingLevel.getMarketingFullGiftLevel())
                || Objects.nonNull(marketingLevel.getMarketingFullReductionLevel())
        ){
            return marketingLevel;
        }
        return null;
    }

    @Data
    public class MarketingLevel{
        /**
         * 满减
         */
        private MarketingFullReductionLevel marketingFullReductionLevel;

        /**
         * 满折
         */
        private MarketingFullDiscountLevel marketingFullDiscountLevel;

        /**
         * 满赠
         */
        private MarketingFullGiftLevel marketingFullGiftLevel;
    }

    /**
     * 记录促销分组的临时结果
     * marketingId为空时，goodsInfoIds是没参与营销活动的商品ID集合
     */
    @Data
    @NoArgsConstructor
    private class MarketingIdAndGoodsInfoWrapper{
        /**
         * 分组方案
         */
        Map<Long, Set<String>> groupMap;
        /**
         * 累计优惠金额（已达到门槛）
         */
        BigDecimal reachProfileValue = BigDecimal.ZERO;
        /**
         * 累计优惠金额（未达到门槛）
         */
        BigDecimal notReachProfileValue = BigDecimal.ZERO;
        /**
         * 已达营销活动门槛的活动数量
         */
        Long reachCount = 0L;
        /**
         * 将要达到的营销活动优惠金额
         */
        BigDecimal todoProfileValue = BigDecimal.ZERO;
    }
}
