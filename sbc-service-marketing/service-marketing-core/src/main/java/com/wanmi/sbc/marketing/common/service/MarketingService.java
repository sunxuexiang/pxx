package com.wanmi.sbc.marketing.common.service;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoResponseVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingPageVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingSuitDetialVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.repository.MarketingScopeRepository;
import com.wanmi.sbc.marketing.common.request.*;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.discount.repository.MarketingFullDiscountLevelRepository;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftDetailRepository;
import com.wanmi.sbc.marketing.gift.service.MarketingFullGiftService;
import com.wanmi.sbc.marketing.redis.RedisService;
import com.wanmi.sbc.marketing.reduction.repository.MarketingFullReductionLevelRepository;
import com.wanmi.sbc.marketing.suittobuy.model.root.MarketingSuitDetail;
import com.wanmi.sbc.marketing.suittobuy.repository.MarketingSuitDetialRepository;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MarketingService {

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MarketingScopeRepository marketingScopeRepository;

    @Autowired
    private MarketingSuitDetialRepository marketingSuitDetialRepository;

    @Autowired
    private MarketingFullDiscountLevelRepository marketingFullDiscountLevelRepository;

    @Autowired
    private MarketingFullReductionLevelRepository marketingFullReductionLevelRepository;

    @Autowired
    private MarketingFullGiftService marketingFullGiftService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MarketingFullGiftDetailRepository marketingFullGiftDetailRepository;

    /**
     * 获取当前活动类型+时间段，是否有已经绑定的sku
     *
     * @param storeId
     * @param skuExistsRequest
     * @return
     */
    @Deprecated
    public List<String> getExistsSkuByMarketingType(Long storeId, SkuExistsRequest skuExistsRequest) {
        List<MarketingSubType> subTypes=new ArrayList<>();
        subTypes.add(MarketingSubType.REDUCTION_FULL_ORDER);
        subTypes.add(MarketingSubType.DISCOUNT_FULL_ORDER);
        return marketingRepository.getExistsSkuByMarketingType(skuExistsRequest.getSkuIds()
                , skuExistsRequest.getMarketingType(), skuExistsRequest.getStartTime()
                , skuExistsRequest.getEndTime(), storeId, skuExistsRequest.getExcludeId(),subTypes);

    }

    /**
     * 套装活动类型+时间段，是否有已经绑定的活动类型
     * @param storeId
     * @param skuExistsRequest
     * @return
     */
    public List<String> getExistsMarketingByMarketingType(Long storeId,SkuExistsRequest skuExistsRequest) {
        List<Long> marketIds = marketingRepository.getExistsMarketingByMarketingType(skuExistsRequest.getMarketIds(),
                skuExistsRequest.getStartTime(),skuExistsRequest.getEndTime(),storeId,skuExistsRequest.getExcludeId());
        return marketIds.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * 获取当前活动类型+时间段，是否有已经绑定的sku(订单满赠)
     *
     * @param storeId
     * @param skuExistsRequest
     * @return
     */
    public List<String> getExistsSkuByMarketingTypeOrderFull(Long storeId, SkuExistsRequest skuExistsRequest) {
        List<MarketingSubType> subTypes=new ArrayList<>();
        subTypes.add(MarketingSubType.GIFT_FULL_ORDER);
        subTypes.add(MarketingSubType.REDUCTION_FULL_ORDER);
        subTypes.add(MarketingSubType.DISCOUNT_FULL_ORDER);
        List<Tuple> tupleList= marketingRepository.getExistsSkuByMarketingTypeOrder(subTypes,skuExistsRequest.getStartTime()
                , skuExistsRequest.getEndTime(), storeId, skuExistsRequest.getExcludeId());

        if(CollectionUtils.isEmpty(tupleList)){
            if (MarketingType.GIFT.equals(skuExistsRequest.getMarketingType())) {
                tupleList = marketingRepository.getExistsSkuByMarketingTypeOrderFull(skuExistsRequest.getMarketingType(), skuExistsRequest.getStartTime()
                        , skuExistsRequest.getEndTime(), storeId, skuExistsRequest.getExcludeId());
            }
        } else {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单满减、订单满折、订单满赠不可在同一时间存在");
        }
        // 活动保存时，校验是否存在满系类型为“订单满减、订单满折、订单满赠”活动状态为进行中、暂停中、未开始的活动，存在则给出提示
        List<String> result = new ArrayList<>();
        tupleList.stream().forEach(entity -> {
            MarketingPageVO pageVO = new MarketingPageVO();
            BoolFlag isPause = entity.get(1, BoolFlag.class);
            LocalDateTime beginTime = entity.get(2, LocalDateTime.class);
            LocalDateTime endTime = entity.get(3, LocalDateTime.class);
            pageVO.setIsPause(isPause);
            pageVO.setBeginTime(beginTime);
            pageVO.setEndTime(endTime);
            MarketingStatus marketingStatus = pageVO.getMarketingStatus();
            MarketingType marketingType = entity.get(4, MarketingType.class);
            String marketingTypeStr = "";
            if (marketingType.equals(MarketingType.DISCOUNT)) {
                marketingTypeStr = "满折";
            } else if (marketingType.equals(MarketingType.REDUCTION)) {
                marketingTypeStr = "满减";
            } else if (marketingType.equals(MarketingType.GIFT)) {
                marketingTypeStr = "满赠";
            }
            if (marketingStatus == MarketingStatus.STARTED) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在进行中的" + marketingTypeStr + "活动");
            }
            if (marketingStatus == MarketingStatus.PAUSED) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在暂停中的" + marketingTypeStr + "活动");
            }
            if (marketingStatus == MarketingStatus.NOT_START) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在未开始的" + marketingTypeStr + "活动");
            }
            String scopeId = entity.get(0, String.class);
            result.add(scopeId);
        });
        return result;
    }


    /**
     * 分页查询营销列表
     *
     * @param request
     * @param storeId
     * @return
     */
    public MicroServicePage<MarketingPageVO> getMarketingPage(MarketingQueryListRequest request, Long storeId) {



        //查询列表
        String sql = "SELECT t.* FROM marketing t ";
        //查询记录的总数量
        String countSql = "SELECT count(1) count FROM marketing t ";
        //条件查询
        String whereSql = "WHERE 1 = 1";
        if (storeId != null){
            whereSql += " AND t.store_id = " + storeId;
        }
        if (Objects.nonNull(request.getWareId())){
            whereSql += " AND t.ware_id = " + request.getWareId();
        }
        if(CollectionUtils.isNotEmpty(request.getGoodsInfoIds())){
            List<MarketingScope> allByScopeIdIn = marketingScopeRepository.findByScopeIdIn(request.getGoodsInfoIds());
            StringBuilder  stringBuilder = new StringBuilder();

            if(CollectionUtils.isNotEmpty(allByScopeIdIn)){
                allByScopeIdIn.forEach(var->{
                    stringBuilder.append(var.getMarketingId());
                    stringBuilder.append(",");
                });
            }else{
                stringBuilder.append(Long.MAX_VALUE);
            }

            whereSql += " AND t.marketing_id in (" + stringBuilder.toString().substring(0,stringBuilder.toString().length()-1) + ")";
        }
        if (request.getDelFlag() != null) {
            whereSql += " AND t.del_flag = " + request.getDelFlag().toValue();
        }

        if (StringUtils.isNotBlank(request.getMarketingName())) {
            whereSql += " AND t.marketing_name LIKE '%" + request.getMarketingName() + "%' ";
        }
        if (request.getMarketingSubType() != null) {
            whereSql += " AND t.sub_type = " + request.getMarketingSubType().toValue();
        }
        if (request.getStartTime() != null) {
            whereSql += " AND '" + DateUtil.format(request.getStartTime(), DateUtil.FMT_TIME_1) + "' <= t.begin_time ";
        }
        if (request.getEndTime() != null) {
            whereSql += " AND '" + DateUtil.format(request.getEndTime(), DateUtil.FMT_TIME_1) + "' >= t.end_time ";
        }
        if (request.getTargetLevelId() != null) {
            whereSql += " AND find_in_set( '" + request.getTargetLevelId() + "' , t.join_level)";
        }
        if (Objects.nonNull(request.getTerminationFlag())) {
            whereSql += " AND t.termination_flag = " +request.getTerminationFlag().toValue();
        }
        switch (request.getQueryTab()) {
            case STARTED://进行中
                whereSql += " AND now() >= t.begin_time AND now() <= t.end_time AND t.is_pause = 0  AND t.is_draft = 0";
                break;
            case PAUSED://暂停中
                whereSql += " AND now() >= t.begin_time AND now() <= t.end_time AND t.is_pause = 1  AND t.is_draft = 0";
                break;
            case NOT_START://未开始
                whereSql += " AND now() < t.begin_time  AND t.is_draft = 0";
                break;
            case ENDED://已结束
                whereSql += " AND now() > t.end_time  AND t.is_draft = 0";
                break;
            case S_NS: // 进行中&未开始
                whereSql += " AND now() <= t.end_time AND t.is_pause = 0  AND t.is_draft = 0";
                break;
            case TERMINATION:
                whereSql +=" AND t.termination_flag = 1  AND t.is_draft = 0";
                break;
            case DRAFT:
                whereSql +=" AND t.is_draft = 1";
                break;
            default:
                break;
        }

        if (Objects.nonNull(request.getMarketingSubType()) && request.getMarketingSubType().equals(MarketingSubType.SUIT_TO_BUY)) {
            whereSql += " order by t.create_time asc";
        } else {
            whereSql += " order by t.create_time desc";
        }

        Query query = entityManager.createNativeQuery(sql.concat(whereSql));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("t", Marketing.class);
        List<MarketingPageVO> responsesList = ((List<Marketing>) query.getResultList()).stream().map(source -> {
            log.info("source---------------->{}",JSONObject.toJSONString(source));
            MarketingPageVO response = new MarketingPageVO();
            BeanUtils.copyProperties(source, response);
            return response;
        }).collect(Collectors.toList());

        long count = 0;

        if (CollectionUtils.isNotEmpty(responsesList)) {
            Query queryCount = entityManager.createNativeQuery(countSql.concat(whereSql));
            count = Long.parseLong(queryCount.getSingleResult().toString());

            List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList();
            responsesList.forEach(marketingPageVO -> marketingPageVO
//                    .setWareName(wareHouseVOList.stream().filter(wareHouseVO -> wareHouseVO.getWareId().equals(marketingPageVO.getWareId())).findFirst().get().getWareName()));
            		  .setWareName(wareHouseVOList.stream().filter(wareHouseVO -> wareHouseVO.getWareId().equals(marketingPageVO.getWareId())).findFirst().orElse(new WareHouseVO()).getWareName()));
        }

        return new MicroServicePage<>(responsesList, request.getPageable(), count);
    }

    /**
     * 分页查询营销列表
     *
     * @param request
     * @param storeId
     * @return
     */
    public MicroServicePage<MarketingPageVO> getMarketingForSuitPage(MarketingQueryListRequest request, Long storeId) {
        //查询列表
        String sql = "SELECT t.* FROM marketing t ";
        //查询记录的总数量
        String countSql = "SELECT count(1) count FROM marketing t ";
        //条件查询
        String whereSql = "WHERE 1 = 1";
        if (storeId != null){
            whereSql += " AND t.store_id = " + storeId;
        }
        if (request.getDelFlag() != null) {
            whereSql += " AND t.del_flag = " + request.getDelFlag().toValue();
        }
        if (Objects.nonNull(request.getWareId())){
            whereSql += " AND t.ware_id = " + request.getWareId();
        }
        if (StringUtils.isNotBlank(request.getMarketingName())) {
            whereSql += " AND t.marketing_name LIKE '%" + request.getMarketingName() + "%' ";
        }
        if (request.getMarketingSubType() != null) {
            whereSql += " AND t.sub_type = " + request.getMarketingSubType().toValue();
        } else {
            //写死，只有满减、满赠、瞒折活动商品参与套装
            whereSql += " AND t.sub_type in (0,1,2,3,4,5)";
        }
        if (request.getStartTime() != null) {
            whereSql += " AND '" + DateUtil.format(request.getStartTime(), DateUtil.FMT_TIME_1) + "' <= t.begin_time ";
        }
        if (request.getEndTime() != null) {
            whereSql += " AND '" + DateUtil.format(request.getEndTime(), DateUtil.FMT_TIME_1) + "' >= t.end_time ";
        }
        if (request.getTargetLevelId() != null) {
            whereSql += " AND find_in_set( '" + request.getTargetLevelId() + "' , t.join_level)";
        }

        switch (request.getQueryTab()) {
            case STARTED://进行中
                whereSql += " AND now() >= t.begin_time AND now() <= t.end_time AND t.is_pause = 0";
                break;
            case PAUSED://暂停中
                whereSql += " AND now() >= t.begin_time AND now() <= t.end_time AND t.is_pause = 1";
                break;
            case NOT_START://未开始
                whereSql += " AND now() < t.begin_time";
                break;
            case ENDED://已结束
                whereSql += " AND now() > t.end_time";
                break;
            case S_NS: // 进行中&未开始
                whereSql += " AND now() <= t.end_time AND t.is_pause = 0";
                break;
            case TERMINATION:
                whereSql +=" AND t.termination_flag = 1";
                break;
            default:
                break;
        }

        whereSql += " order by t.create_time desc";

        Query query = entityManager.createNativeQuery(sql.concat(whereSql));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("t", Marketing.class);
        List<MarketingPageVO> responsesList = ((List<Marketing>) query.getResultList()).stream().map(source -> {
            MarketingPageVO response = new MarketingPageVO();
            BeanUtils.copyProperties(source, response);
            return response;
        }).collect(Collectors.toList());

        long count = 0;

        if (CollectionUtils.isNotEmpty(responsesList)) {
            Query queryCount = entityManager.createNativeQuery(countSql.concat(whereSql));
            count = Long.parseLong(queryCount.getSingleResult().toString());
        }

        return new MicroServicePage<>(responsesList, request.getPageable(), count);
    }

    /**
     * 保存营销信息
     *
     * @param request
     * @return
     * @throws SbcRuntimeException
     */
    @Transactional(rollbackFor = Exception.class)
    public Marketing addMarketing(MarketingSaveRequest request) throws SbcRuntimeException {
        this.validateGiftFullOrder(request);
        Marketing marketing = request.generateMarketing();

        if (request.getMarketingId() != null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        marketing.setWhetherChoice(BoolFlag.NO);
        //当前活动是否有必选商品
        if (CollectionUtils.isNotEmpty(request.getBundleSalesSkuIds())) {
            Long number = request.getBundleSalesSkuIds().stream().filter(goods -> goods.getWhetherChoice() == BoolFlag.YES).count();
            if (number > 0){
                marketing.setWhetherChoice(BoolFlag.YES);
            }
        }

        marketing.setCreateTime(LocalDateTime.now());
        marketing.setTerminationFlag(BoolFlag.NO);
        marketing.setDelFlag(DeleteFlag.NO);
        marketing.setIsPause(BoolFlag.NO);
        marketing.setMarketingType(request.getMarketingType());
        marketing.setIsAddMarketingName(request.getIsAddMarketingName());

        // 营销规则
        marketing = marketingRepository.save(marketing);

        // 保存营销和商品关联关系
        if (!MarketingSubType.SUIT_TO_BUY.equals(marketing.getSubType())) {
            if (MarketingSubType.GIFT_FULL_ORDER.equals(marketing.getSubType()) || MarketingSubType.REDUCTION_FULL_ORDER.equals(marketing.getSubType())
                    || MarketingSubType.DISCOUNT_FULL_ORDER.equals(marketing.getSubType())) {
                List<MarketingScope> marketingScopes = new ArrayList<>();
                MarketingScope marketingScope = new MarketingScope();
                marketingScope.setMarketingId(marketing.getMarketingId());
                marketingScope.setScopeId(Constant.FULL_GIT_ORDER_GOODS);
                marketingScopes.add(marketingScope);
                this.saveScopeList(marketingScopes);
            } else {
                this.saveScopeList(request.generateMarketingScopeList(marketing.getMarketingId()));
            }
        }

        return marketing;
    }

    /**
     * 修改营销信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyMarketing(MarketingSaveRequest request) throws SbcRuntimeException {
        this.validParam(request);
        this.validateGiftFullOrder(request);
        Marketing marketing = marketingRepository.findById(request.getMarketingId())
                .orElseThrow(() -> new SbcRuntimeException(MarketingErrorCode.NOT_EXIST));

        if (!Objects.equals(request.getStoreId(), marketing.getStoreId())) {
            throw new SbcRuntimeException("K-000018");
        }

        if (marketing.getBeginTime().isBefore(LocalDateTime.now())) {
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_STARTED_OR_ENDED);
        }

        marketing.setMarketingName(request.getMarketingName());
        marketing.setBeginTime(request.getBeginTime());
        marketing.setEndTime(request.getEndTime());
        marketing.setMarketingType(request.getMarketingType());

        marketing.setSubType(request.getSubType());
        marketing.setUpdatePerson(request.getUpdatePerson());
        marketing.setUpdateTime(LocalDateTime.now());
        marketing.setJoinLevel(request.getJoinLevel());
        marketing.setWareId(request.getWareId());

        marketing.setIsAddMarketingName(request.getIsAddMarketingName());
        marketing.setIsOverlap(request.getIsOverlap());

        log.info("marketing---save---->"+JSONObject.toJSONString(marketing));
        // 营销规则
        marketingRepository.save(marketing);

        // 先删除已有的营销和商品关联关系，然后再保存[套装活动不需要]
        if (!MarketingType.SUIT.equals(marketing.getMarketingType())) {
            marketingScopeRepository.deleteByMarketingId(marketing.getMarketingId());

            // 自定义商品才需要保存
            /*   if (request.getScopeType() == MarketingScopeType.SCOPE_TYPE_CUSTOM) {// 保存营销和商品关联关系*/
            if (MarketingSubType.GIFT_FULL_ORDER.equals(marketing.getSubType()) || MarketingSubType.REDUCTION_FULL_ORDER.equals(marketing.getSubType())
                    || MarketingSubType.DISCOUNT_FULL_ORDER.equals(marketing.getSubType())) {
                List<MarketingScope> marketingScopes = new ArrayList<>();
                MarketingScope marketingScope = new MarketingScope();
                marketingScope.setMarketingId(marketing.getMarketingId());
                marketingScope.setScopeId(Constant.FULL_GIT_ORDER_GOODS);
                marketingScopes.add(marketingScope);
                this.saveScopeList(marketingScopes);
            } else {
                log.info("保存营销活动那个----------->{}",request);
                log.info("保存营销活动那个----------->{}",request.getBundleSalesSkuIds());
                log.info("保存营销活动那个----------->{}",JSONObject.toJSONString(request.getBundleSalesSkuIds()));
                this.saveScopeList(request.generateMarketingScopeList(marketing.getMarketingId()));
            }
            /* }*/
        }
    }

    /**
     * 参数校验
     *
     * @param request
     */
    public List<String> validParam(MarketingSaveRequest request) {
        boolean invalidParam = true;

        if (request.getMarketingType() == MarketingType.REDUCTION) {
            invalidParam = request.getSubType() != MarketingSubType.REDUCTION_FULL_AMOUNT && request.getSubType() != MarketingSubType.REDUCTION_FULL_COUNT && request.getSubType() != MarketingSubType.REDUCTION_FULL_ORDER;
        } else if (request.getMarketingType() == MarketingType.DISCOUNT) {
            invalidParam = request.getSubType() != MarketingSubType.DISCOUNT_FULL_AMOUNT && request.getSubType() != MarketingSubType.DISCOUNT_FULL_COUNT && request.getSubType() != MarketingSubType.DISCOUNT_FULL_ORDER;
        } else if (request.getMarketingType() == MarketingType.GIFT) {
            invalidParam = request.getSubType() != MarketingSubType.GIFT_FULL_AMOUNT && request.getSubType() != MarketingSubType.GIFT_FULL_COUNT && request.getSubType() != MarketingSubType.GIFT_FULL_ORDER;
        } else if (request.getMarketingType() == MarketingType.SUIT && request.getSubType().equals(MarketingSubType.SUIT_TO_BUY)) {
            invalidParam = false;
        }

        if (invalidParam) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        // 自定义商品才需要校验
        if (request.getScopeType() == MarketingScopeType.SCOPE_TYPE_CUSTOM) {

            List<MarketingSubType> subTypes = new ArrayList<>();
            subTypes.add(MarketingSubType.REDUCTION_FULL_ORDER);
            subTypes.add(MarketingSubType.DISCOUNT_FULL_ORDER);
            List<String> existsList = marketingRepository.getExistsSkuByMarketingType(request.getSkuIds(), request.getMarketingType(), request.getBeginTime(), request.getEndTime(), request.getStoreId(), request.getMarketingId(), subTypes);

            if (CollectionUtils.isNotEmpty(existsList)) {
                //throw new SbcRuntimeException(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT, new Object[]{existsList.size()});}
                return existsList;
            }
        }
        return new ArrayList<>();
    }

    /**
     * 校验订单那满赠营销
     */
    private void validateGiftFullOrder(MarketingSaveRequest request){
        List<Marketing> marketings = this.queryAllByMarketingSubType(MarketingSubTypeRequest.builder()
                .marketingSubType(request.getSubType())
                .storeId(request.getStoreId())
                .deleteFlag(DeleteFlag.NO)
                .isPause(BoolFlag.NO)
                .build());
        List<Marketing> targetMarketings = marketings;
        if(request.getMarketingId() != null && request.getMarketingId() > 0){
            targetMarketings.clear();
            targetMarketings = marketings.stream().filter(m->!m.getMarketingId().equals(request.getMarketingId())).collect(Collectors.toList());
        }
        // 同一个时间只能有一个订单满营销
        targetMarketings.stream().forEach((i)->{
            if(!(request.getBeginTime().isAfter(i.getEndTime()) || request.getEndTime().isBefore(i.getBeginTime()))){
                new SbcRuntimeException(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT);
            }
        });
        //校验商品中是否有超过5个必选商品
        if (CollectionUtils.isNotEmpty(request.getBundleSalesSkuIds())){
            Long  number = request.getBundleSalesSkuIds().stream().filter(goods->goods.getWhetherChoice() == BoolFlag.YES).count();
            if (number > 5){
                new SbcRuntimeException("必选商品最多可选5个");
            }
        }
    }

    /**
     * 保存营销和商品关联关系
     */
    private void saveScopeList(List<MarketingScope> marketingScopeList) {
        if (CollectionUtils.isNotEmpty(marketingScopeList)) {
            marketingScopeRepository.saveAll(marketingScopeList);
        } else {
            throw new SbcRuntimeException("K-000009");
        }
    }

    /**
     * 删除营销活动
     *
     * @param marketingId
     * @return
     */
    @Transactional
    @Deprecated
    public int deleteMarketingById(Long marketingId) {
//        List<MarketingScope> MarketingScopes = marketingScopeRepository.findByMarketingId(marketingId);
//        if(CollectionUtils.isNotEmpty(MarketingScopes)){
//            List<PurchaseNumDTO> purchaseNumDTOS = new ArrayList<>();
//            MarketingScopes.forEach(marketingScope -> {
//                if(Objects.nonNull(marketingScope.getPurchaseNum())){
//                    PurchaseNumDTO dto = new PurchaseNumDTO();
//                    dto.setMarketingId(marketingScope.getMarketingId());
//                    dto.setGoodsInfoId(marketingScope.getScopeId());
//                    dto.setPurchaseNum(marketingScope.getPurchaseNum());
//                    purchaseNumDTOS.add(dto);
//                }
//            });
//            if(CollectionUtils.isNotEmpty(purchaseNumDTOS)){
//                goodsInfoProvider.updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest.builder().goodsInfoPurchaseNumDTOS(KsBeanUtil.convertList(purchaseNumDTOS, GoodsInfoPurchaseNumDTO.class)).b(false).build());
//            }
//        }
        return marketingRepository.deleteMarketing(marketingId);
    }

    /**
     * 暂停营销活动
     *
     * @param marketingId
     * @return
     */
    @Transactional
    @Deprecated
    public int pauseMarketingById(Long marketingId) {
//        List<MarketingScope> MarketingScopes = marketingScopeRepository.findByMarketingId(marketingId);
//        if(CollectionUtils.isNotEmpty(MarketingScopes)){
//            List<PurchaseNumDTO> purchaseNumDTOS = new ArrayList<>();
//            MarketingScopes.forEach(marketingScope -> {
//                if(Objects.nonNull(marketingScope.getPurchaseNum())){
//                    PurchaseNumDTO dto = new PurchaseNumDTO();
//                    dto.setMarketingId(marketingScope.getMarketingId());
//                    dto.setGoodsInfoId(marketingScope.getScopeId());
//                    dto.setPurchaseNum(marketingScope.getPurchaseNum());
//                    purchaseNumDTOS.add(dto);
//                }
//            });
//            if(CollectionUtils.isNotEmpty(purchaseNumDTOS)){
//                goodsInfoProvider.updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest.builder().goodsInfoPurchaseNumDTOS(KsBeanUtil.convertList(purchaseNumDTOS, GoodsInfoPurchaseNumDTO.class)).b(false).build());
//            }
//        }
        return marketingRepository.pauseOrStartMarketing(marketingId, BoolFlag.YES);
    }

    /**
     * 启动营销活动
     *
     * @param marketingId
     * @return
     */
    @Transactional
    @Deprecated
    public int startMarketingById(Long marketingId) {
//        List<MarketingScope> MarketingScopes = marketingScopeRepository.findByMarketingId(marketingId);
//        if(CollectionUtils.isNotEmpty(MarketingScopes)){
//            List<PurchaseNumDTO> purchaseNumDTOS = new ArrayList<>();
//            MarketingScopes.forEach(marketingScope -> {
//                if(Objects.nonNull(marketingScope.getPurchaseNum()) && marketingScope.getTerminationFlag().equals(BoolFlag.NO)){
//                    PurchaseNumDTO dto = new PurchaseNumDTO();
//                    dto.setMarketingId(marketingScope.getMarketingId());
//                    dto.setGoodsInfoId(marketingScope.getScopeId());
//                    dto.setPurchaseNum(marketingScope.getPurchaseNum());
//                    purchaseNumDTOS.add(dto);
//                }
//            });
//            if(CollectionUtils.isNotEmpty(purchaseNumDTOS)){
//                goodsInfoProvider.updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest.builder().goodsInfoPurchaseNumDTOS(KsBeanUtil.convertList(purchaseNumDTOS, GoodsInfoPurchaseNumDTO.class)).b(true).build());
//            }
//        }
        return marketingRepository.pauseOrStartMarketing(marketingId, BoolFlag.NO);
    }

    /**
     * 查询有必选商品的活动
     * @param marketings
     * @return
     */
    @Transactional
    public List<Marketing> getChooseGoodsMarketingList(List<Long> marketings){
        return marketingRepository.getMarketingList(marketings);
    }

    /**
     * 终止营销活动
     *
     * @param marketingId
     * @return
     */
    @Transactional
    @LcnTransaction
    @Deprecated
    public void terminationMarketingById(Long marketingId,String operatoerId,Boolean notStartFlag) {
        Optional<Marketing> byId = marketingRepository.findById(marketingId);
        if (byId.isPresent()){
            LocalDateTime now = LocalDateTime.now();
            Marketing marketing = byId.get();
            marketing.setTerminationFlag(BoolFlag.YES);
            marketing.setRealEndTime(marketing.getEndTime());
            if (notStartFlag){
                marketing.setBeginTime(now);
            }
            marketing.setEndTime(now);
            marketing.setUpdateTime(now);
            marketing.setUpdatePerson(operatoerId);
            marketingRepository.save(marketing);
//            List<PurchaseNumDTO> purchaseNum = MarketingPurchaseNumUtil.getPurchaseNum(marketing);
//            //同步商品限购信息
//            if(CollectionUtils.isNotEmpty(purchaseNum)){
//                goodsInfoProvider.updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest.builder().goodsInfoPurchaseNumDTOS(KsBeanUtil.convertList(purchaseNum, GoodsInfoPurchaseNumDTO.class)).b(false).build());
//            }
        }
    }

    /**
     * 获取营销实体
     *
     * @param marketingId
     * @return
     */
    public Marketing queryById(Long marketingId) {
        return marketingRepository.findById(marketingId).get();
    }

    public List<Marketing> queryByIds(List<Long> marketingIds) {
        return marketingRepository.findAllById(marketingIds);
    }

    // 关联营销活动级别
    private void joinMarketingLevels(Marketing marketing, MarketingResponse marketingResponse) {
        switch (marketing.getMarketingType()) {
            case REDUCTION:
                marketingResponse.setFullReductionLevelList(marketingFullReductionLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(marketing.getMarketingId()));
                break;
            case DISCOUNT:
                marketingResponse.setFullDiscountLevelList(marketingFullDiscountLevelRepository.findByMarketingIdOrderByFullAmountAscFullCountAsc(marketing.getMarketingId()));
                break;
            case GIFT:
                marketingResponse.setFullGiftLevelList(marketingFullGiftService.getLevelsByMarketingId(marketing.getMarketingId()));
                break;
            default:
                break;
        }
    }

    /**
     * 将营销活动集合，map成 { goodsId - list<Marketing> } 结构
     *
     * @param marketingRequest
     * @return
     */
    @Transactional
    public Map<String, List<MarketingResponse>> getMarketingMapByGoodsId(MarketingRequest marketingRequest) {
        Map<String, List<MarketingResponse>> map = new HashMap<>();
        /* List<Marketing> marketings = marketingRepository.findAllGoingMarketing();*/
        List<String> goodsInfoIds = marketingRequest.getGoodsInfoIdList();
     /*   List<Marketing> marketingList = marketings.stream().filter(m-> MarketingSubType.GIFT_FULL_ORDER.equals(m.getSubType()) ||
            m.getMarketingScopeList().stream().filter(s->goodsInfoIds.contains(s.getScopeId())).findFirst().isPresent()
        ).collect(Collectors.toList());*/
        List<MarketingScope> allByMarketingScopeIdIn = new ArrayList<>(20);
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            List<String> skuIds = new ArrayList<>(20);
            skuIds.add("all");
            skuIds.addAll(goodsInfoIds);
//            allByMarketingScopeIdIn = marketingScopeRepository.findAllByScopeIdIn(skuIds);
            if(!org.springframework.util.StringUtils.isEmpty(marketingRequest.getStartTime())
                    && !org.springframework.util.StringUtils.isEmpty(marketingRequest.getEndTime())){
                allByMarketingScopeIdIn = marketingScopeRepository.findAllByScopeIds(skuIds);
            }else{
                allByMarketingScopeIdIn = marketingScopeRepository.findAllByScopeIdIn(skuIds);
            }
        } else {
            allByMarketingScopeIdIn = marketingScopeRepository.findAllByScopeId("all");
        }
        log.info("============营销活动查询allByMarketingScopeIdIn==={}：", JSONObject.toJSONString(allByMarketingScopeIdIn));
        if (CollectionUtils.isNotEmpty(allByMarketingScopeIdIn)) {
            HashSet<Long> marketingId = new HashSet<>(20);
            allByMarketingScopeIdIn.forEach(scope -> {
                marketingId.add(scope.getMarketingId());
            });
            List<Object> marketingObj = null;
            if(!org.springframework.util.StringUtils.isEmpty(marketingRequest.getStartTime())
                    && !org.springframework.util.StringUtils.isEmpty(marketingRequest.getEndTime())){
                marketingObj = marketingRepository.getMarketingByGoodsInfoIdsTime(new ArrayList<>(marketingId),marketingRequest.getStartTime(),marketingRequest.getEndTime());
            }else{
                marketingObj = marketingRepository.getMarketingByGoodsInfoIds(new ArrayList<>(marketingId));
            }

            if (CollectionUtils.isNotEmpty(marketingObj)) {
                List<Marketing> marketingList = objToMarketing(marketingObj);
                if (marketingList != null && !marketingList.isEmpty()) {
                    //关联活动级别
                    List<MarketingResponse> marketingResponseList = new ArrayList<>();

                    for (Marketing marketing : marketingList) {
                        //填充参与营销活动范围
                        List<MarketingScope> marketingScope = allByMarketingScopeIdIn.stream()
                                .filter(param -> param.getMarketingId().equals(marketing.getMarketingId())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(marketingScope)) {
                            marketing.setMarketingScopeList(marketingScope);
                        }

                        MarketingResponse marketingResponse = new MarketingResponse();
                        BeanUtils.copyProperties(marketing, marketingResponse);
                        if (marketingRequest.getCascadeLevel() != null && marketingRequest.getCascadeLevel()) {
                            joinMarketingLevels(marketing, marketingResponse);
                        }

                        marketingResponseList.add(marketingResponse);
                    }
                    for (MarketingResponse marketing : marketingResponseList) {
                        List<MarketingScope> marketingScopeList = marketing.getMarketingScopeList();
                        for (MarketingScope scope : marketingScopeList) {
                            if (marketingRequest.getGoodsInfoIdList().contains(scope.getScopeId())) {
                                List<MarketingResponse> list;
                                if (map.get(scope.getScopeId()) == null) {
                                    list = new LinkedList<>();
                                    map.put(scope.getScopeId(), list);
                                } else {
                                    list = map.get(scope.getScopeId());
                                }
                                list.add(marketing);
                            }
                        }
                    }
                    //设置满订单赠
                    Optional<MarketingResponse> marketingResponse = marketingResponseList.stream()
                            .filter(m -> MarketingSubType.GIFT_FULL_ORDER.equals(m.getSubType())).findFirst();
                    if (marketingResponse.isPresent()) {
                        for (String goodsInfoId : map.keySet()) {
                            if (!"all".equals(goodsInfoId)) {
                                map.get(goodsInfoId).add(marketingResponse.get());
                            }
                        }
                        for (String g : goodsInfoIds) {
                            if (!CollectionUtils.isNotEmpty(map.get(g))) {
                                map.put(g, Arrays.asList(marketingResponse.get()));
                            }
                        }
                    }
                }
                log.info("============营销活动查询map==={}：", JSONObject.toJSONString(map));
                return map;
            }
        }
        log.info("============营销活动查询map1==={}：", JSONObject.toJSONString(map));
        return map;
    }

    /**
     * 提供管理端使用
     * 获取营销实体，包括详细信息，level，detail等
     *
     * @param marketingId
     * @return
     */
    @Transactional
    public MarketingResponse getMarketingByIdForSupplier(Long marketingId) {
        Marketing marketing = marketingRepository.findById(marketingId).orElse(null);
        MarketingResponse marketingResponse = new MarketingResponse();
        // 不存在, 已删除, 未开始均认为不存在
        if (marketing == null || marketing.getDelFlag() == DeleteFlag.YES) {
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
        return getMarketing(marketing, marketingResponse);
    }

    /**
     * 提供用户端使用
     * 获取营销实体，包括详细信息，level，detail等
     *
     * @param marketingId
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public MarketingResponse getMarketingByIdForCustomer(Long marketingId) {
        log.info("MarketingService getMarketingByIdForCustomerId:{}",marketingId);
        Marketing marketing = marketingRepository.findById(marketingId).orElse(null);
        log.info("MarketingService getMarketingByIdForCustomer:{}",marketing);
        MarketingResponse marketingResponse = new MarketingResponse();
        // 不存在, 已删除, 未开始均认为不存在
        if (marketing == null || marketing.getDelFlag() == DeleteFlag.YES ||
                marketing.getBeginTime().isAfter(LocalDateTime.now())) {
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        } else if (marketing.getIsPause() == BoolFlag.YES) { // 暂停
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_SUSPENDED);
        } else if (marketing.getEndTime().isBefore(LocalDateTime.now())) { // 结束
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_OVERDUE);
        }
//        List<MarketingSuitDetail> marketingSuitDetailByMarketingId = marketingSuitDetialRepository.findMarketingSuitDetailByMarketingId(marketingId);
//
//        if(CollectionUtils.isNotEmpty(marketingSuitDetailByMarketingId)){
//            marketingResponse.setMarketingSuitDetialVOList(KsBeanUtil.convertList(marketingSuitDetailByMarketingId,MarketingSuitDetialVO.class));
//        }

        return getMarketing(marketing, marketingResponse);
    }

    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public Marketing getMarketingById(Long marketingId) {
        Marketing marketing = marketingRepository.findById(marketingId).orElse(null);
        return marketing;
    }

    /**
     * 获取营销
     *
     * @param marketing
     * @param marketingResponse
     * @return
     */
    private MarketingResponse getMarketing(Marketing marketing, MarketingResponse marketingResponse) {
        //组装营销类型信息
        joinMarketingLevels(marketing, marketingResponse);
        BeanUtils.copyProperties(marketing, marketingResponse);
        List<MarketingScope> scopeList = marketing.getMarketingScopeList();
        marketingResponse.setMarketingScopeList(scopeList);
        List<MarketingSuitDetialVO> marketingSuitDetialVOList = KsBeanUtil.convertList(marketing.getMarketingSuitDetialList(),MarketingSuitDetialVO.class);
        //组装商品信息
        if (CollectionUtils.isNotEmpty(scopeList)) {
            //营销活动包含的所有商品Id
            List<String> goodsInfoIds = scopeList.stream().map(MarketingScope::getScopeId).collect(Collectors.toList());
            /*List<String> goodsInfoIds = scopeList.stream().filter(v->{
                if (v.getTerminationFlag().equals(BoolFlag.YES)){
                    return false;
                }
                return true;
            }).map(MarketingScope::getScopeId).collect(Collectors.toList());*/
            //将满赠赠品的商品信息也带出
            if (marketingResponse.getMarketingType() == MarketingType.GIFT && CollectionUtils.isNotEmpty(marketingResponse.getFullGiftLevelList())) {
                List<String> detailGoodsInfoIds = marketingResponse.getFullGiftLevelList().stream().flatMap(level -> level.getFullGiftDetailList().stream())
                        .map(MarketingFullGiftDetail::getProductId).collect(Collectors.toList());
                goodsInfoIds.addAll(detailGoodsInfoIds);
            }
            //FIXME 营销是平铺展示，但是数量达到一定层级，还是需要分页，先暂时这么控制
            GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
            queryRequest.setPageSize(10000);
            queryRequest.setStoreId(marketing.getStoreId());
            // 下架和删除的商品也要显示，方便财务核对营销数据
//            queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
//            queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
            queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
            queryRequest.setGoodsInfoIds(goodsInfoIds);
            GoodsInfoViewPageResponse pageResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();
//            List<MarketingScope> marketingScopeList = marketing.getMarketingScopeList();
//            //封装限购数量信息
//            pageResponse.getGoodsInfoPage().stream().forEach(goodsInfoVO -> {
//                for (MarketingScope marketingScope : marketingScopeList) {
//                    if(Objects.nonNull(marketingScope.getPurchaseNum())){
//                        if(goodsInfoVO.getGoodsInfoId().equals(marketingScope.getScopeId())){
//                            goodsInfoVO.setPurchaseNum(marketingScope.getPurchaseNum());
//                        }
//                    }
//                }
//            });
            List<GoodsInfoVO> content = pageResponse.getGoodsInfoPage().getContent();
            if(CollectionUtils.isNotEmpty(content) && CollectionUtils.isNotEmpty(scopeList)){
                Map<String, MarketingScope> goodsMap = scopeList.stream().collect(Collectors.toMap(MarketingScope::getScopeId, g -> g,(a,b)->a));
                content.forEach(var->{
                    MarketingScope marketingScope = goodsMap.get(var.getGoodsInfoId());
                    if(Objects.nonNull(marketingScope)){
                        if(marketingScope.getWhetherChoice().equals(BoolFlag.YES)){
                            var.setChecked(true);
                        }else{
                            var.setChecked(false);
                        }
                        var.setPurchaseNum(marketingScope.getPurchaseNum());
                    }
                });
            }
            marketingResponse.setGoodsList(GoodsInfoResponseVO.builder()
                    .goodsInfoPage(pageResponse.getGoodsInfoPage())
                    .goodses(pageResponse.getGoodses())
                    .brands(CollectionUtils.isEmpty(pageResponse.getBrands()) ? Collections.emptyList() : pageResponse.getBrands())
                    .cates(pageResponse.getCates())
                    .build());
        }
        //组装商品信息
        if(CollectionUtils.isNotEmpty(marketingSuitDetialVOList)) {
            marketingSuitDetialVOList.forEach(marketingSuitDetailVO -> {
                GoodsInfoVO goodsInfoVO = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                        .goodsInfoId(marketingSuitDetailVO.getGoodsInfoId()).build()).getContext();
                marketingSuitDetailVO.setGoodsInfoVO(goodsInfoVO);
            });
            marketingResponse.setMarketingSuitDetialVOList(marketingSuitDetialVOList);
        }
        return marketingResponse;
    }


    /**
     * 获取营销对应的商品信息
     *
     * @param marketingId
     * @return
     */
    @Transactional
    public GoodsInfoResponse getGoodsByMarketingId(Long marketingId) {
        Marketing marketing = marketingRepository.findById(marketingId).orElse(null);
        if (marketing != null && marketing.getDelFlag() == DeleteFlag.NO) {
            List<MarketingScope> scopeList = marketing.getMarketingScopeList();
            if (CollectionUtils.isNotEmpty(scopeList)) {
                List<String> goodsInfoIds = scopeList.stream().map(MarketingScope::getScopeId).collect(Collectors.toList());
                GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
                //FIXME 营销是平铺展示，但是数量达到一定层级，还是需要分页，先暂时这么控制
                queryRequest.setPageSize(10000);
                queryRequest.setStoreId(marketing.getStoreId());
                queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
                queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
                queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
                queryRequest.setGoodsInfoIds(goodsInfoIds);
                GoodsInfoViewPageResponse pageResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();
                return GoodsInfoResponse.builder()
                        .goodsInfoPage(pageResponse.getGoodsInfoPage())
                        .brands(pageResponse.getBrands())
                        .cates(pageResponse.getCates())
                        .goodses(pageResponse.getGoodses())
                        .build();
            } else {
                return new GoodsInfoResponse();
            }
        } else {
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
    }

    /**
     * 获取验证进行中的营销
     *
     * @param marketingIds 参数
     */
    public List<String> queryStartingMarketing(List<Long> marketingIds) {
        return marketingRepository.queryStartingMarketing(marketingIds);
    }

    /**
     * 根据营销类型查询营销
     * @param request
     * @return
     */
    public List<Marketing> queryAllByMarketingSubType(MarketingSubTypeRequest request){
        return marketingRepository.findAll(request.getWhereCriteria());
    }

    /**
     * 根据子类型条件获取营销信息
     * @return
     */
    public List<MarketingVO> list(MarketingBaseRequest marketingBaseRequest){
        MarketingRequest marketingRequest = new MarketingRequest();
        marketingRequest.setMarketingSubType(marketingBaseRequest.getSubType());
        marketingRequest.setExcludeStatus(MarketingStatus.ENDED);
        marketingRequest.setDeleteFlag(DeleteFlag.NO);
        List<Marketing> marketings = marketingRepository.findAll(marketingRequest.getWhereCriteria());
        return KsBeanUtil.convert(marketings,MarketingVO.class);
    }

    /**
     * 获取批量营销
     * @param marketingIds
     * @return
     */
    public List<MarketingResponse> getMarketingByIdsForCustomer(List<Long> marketingIds) {
        List<Marketing> marketings = marketingRepository.findAllById(marketingIds);
        List<MarketingResponse> marketingResponseList = marketings.stream().map(marketing -> {
            MarketingResponse marketingResponse = new MarketingResponse();
            // 不存在, 已删除, 未开始均认为不存在
            if (marketing == null || marketing.getDelFlag() == DeleteFlag.YES ||
                    marketing.getBeginTime().isAfter(LocalDateTime.now())) {
                throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
            } else if (marketing.getIsPause() == BoolFlag.YES) { // 暂停
                throw new SbcRuntimeException(MarketingErrorCode.MARKETING_SUSPENDED);
            } else if (marketing.getEndTime().isBefore(LocalDateTime.now())) { // 结束
                throw new SbcRuntimeException(MarketingErrorCode.MARKETING_OVERDUE);
            }
            //组装营销类型信息
            joinMarketingLevels(marketing, marketingResponse);
            BeanUtils.copyProperties(marketing, marketingResponse);
            List<MarketingScope> scopeList = marketing.getMarketingScopeList();
            marketingResponse.setMarketingScopeList(scopeList);
            return marketingResponse;
        }).collect(Collectors.toList());
        return marketingResponseList;
    }

    private List<Marketing> objToMarketing(List<Object> marketingObj){
        return marketingObj.stream().map(item -> {
            Marketing marketing = new Marketing();
            Object[] results = StringUtil.cast(item, Object[].class);

            System.out.println("resluts =====================" + JSONObject.toJSONString(results));
            marketing.setMarketingId(StringUtil.cast(results, 0, BigInteger.class).longValue());
            marketing.setMarketingName(StringUtil.cast(results, 1, String.class));
            int marketingType = StringUtil.cast(results, 2, Byte.class).intValue();
            try {
                marketing.setMarketingType(MarketingType.fromValue(marketingType));
            }catch (Exception e){
                System.out.println(marketing);
                throw new SbcRuntimeException();
            }

            int subType = StringUtil.cast(results, 3, Byte.class).intValue();
            marketing.setSubType(MarketingSubType.fromValue(subType));
            Timestamp beginTime = StringUtil.cast(results, 4, Timestamp.class);
            if (Objects.nonNull(beginTime)){
                marketing.setBeginTime(beginTime.toLocalDateTime());
            }
            Timestamp endTime = StringUtil.cast(results, 5, Timestamp.class);
            if (Objects.nonNull(endTime)){
                marketing.setEndTime(endTime.toLocalDateTime());
            }
            marketing.setJoinLevel(StringUtil.cast(results, 6, String.class));
            marketing.setIsBoss(StringUtil.cast(results, 7, Byte.class).intValue()==0?BoolFlag.NO:BoolFlag.YES);
            marketing.setStoreId(StringUtil.cast(results, 8, BigInteger.class).longValue());
            marketing.setIsOverlap(StringUtil.cast(results, 9, Byte.class).intValue()==0?BoolFlag.NO:BoolFlag.YES);
            int scopeType = StringUtil.cast(results, 10, Byte.class).intValue();
            marketing.setScopeType(MarketingScopeType.fromValue(scopeType));
            System.out.println("scopeType ==========" + scopeType);
            System.out.println("StringUtil.cast(results, 11, Byte.class) ===================" + Integer.parseInt(results[11].toString()));
            if(!Objects.isNull(results[11].toString())){
                marketing.setIsAddMarketingName(Integer.parseInt(results[11].toString())==0?BoolFlag.NO:BoolFlag.YES);
            }else{
                marketing.setIsAddMarketingName(BoolFlag.NO);
            }
            if (!Objects.isNull(results[12].toString())) {
                marketing.setWhetherChoice(StringUtil.cast(results, 12, Byte.class).intValue() == 0 ? BoolFlag.NO : BoolFlag.YES);
            }
            if (!Objects.isNull(results[13].toString())) {
                marketing.setTerminationFlag(Integer.parseInt(results[13].toString())==0?BoolFlag.NO:BoolFlag.YES);
            }else{
                marketing.setTerminationFlag(BoolFlag.NO);
            }
            marketing.setSuitCouponDesc(StringUtil.cast(results, 14, String.class));
            Timestamp updateTime = StringUtil.cast(results, 15, Timestamp.class);
            if (Objects.nonNull(updateTime)){
                marketing.setUpdateTime(updateTime.toLocalDateTime());
            }
            marketing.setUpdatePerson(StringUtil.cast(results, 16, String.class));
            return marketing;
        }).collect(Collectors.toList());
    }

    public Map<Long, List<MarketingSuitDetialVO>> getSuitDetialByMarketingIds(List<Long> marketingIds){
        Map<Long,List<MarketingSuitDetialVO>> marketingSuitDetialVOMap = new HashMap<>();
        marketingIds.forEach(marketingId -> {
            List<MarketingSuitDetail> marketingSuitDetailList = marketingSuitDetialRepository.findMarketingSuitDetailByMarketingId(marketingId);
            List<MarketingSuitDetialVO> marketingSuitDetialVOList = KsBeanUtil.convertList(marketingSuitDetailList,MarketingSuitDetialVO.class);
            marketingSuitDetialVOList.forEach(marketingSuitDetailVO -> {
                GoodsInfoVO goodsInfoVO = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                        .goodsInfoId(marketingSuitDetailVO.getGoodsInfoId()).build()).getContext();
                marketingSuitDetailVO.setGoodsInfoVO(goodsInfoVO);
            });
            marketingSuitDetialVOMap.put(marketingId,marketingSuitDetialVOList);
        });
        return marketingSuitDetialVOMap;
    }

    //营销活动赠品原子自减
    public Long decrActiveNum(String markingId,String levelId,String goodsInfoId,long num){

        String key = markingId+levelId+goodsInfoId;
        String o = redisService.getString(key);
        Long rnum = Long.parseLong(o);
        if (rnum.compareTo(num)<0){
            throw new SbcRuntimeException("限赠送数量不足");
        }
        return redisService.decrKey(key,num);
    }

    //是否存在
    public Boolean hasNext(String markingId,String levelId,String goodsInfoId){
        String key = markingId+levelId+goodsInfoId;
        return redisService.hasKey(key);
    }


    //营销活动赠品原子自增
    public Long incrActiveNum(String markingId,String levelId,String goodsInfoId){
        String key = markingId+levelId+goodsInfoId;
        return redisService.incrKey(key);
    }

    //营销活动赠品添加redis没有过期时间
    public void setMarketingGiftNum(String markingId,String levelId,String goodsInfoId,Long num){
        String key = markingId+levelId+goodsInfoId;
        redisService.setString(key,num.toString());
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

    public List<MarketingScope> getMarketingScope(Long marketingId){
        return marketingScopeRepository.findByMarketingId(marketingId);
    }
    //满赠停止一个赠品
    public void stopGiveGoods(MarketingStopGiveGoodsRequest request){
        Optional<MarketingFullGiftDetail> byId = marketingFullGiftDetailRepository.findById(Long.parseLong(request.getGiftDetailId()));
        if(!byId.isPresent()){
            throw new SbcRuntimeException("数据异常，未查询到对应的营销活动赠品信息");
        }else {
            MarketingFullGiftDetail marketingFullGiftDetail = byId.get();
            marketingFullGiftDetail.setTerminationFlag(BoolFlag.YES);
            marketingFullGiftDetailRepository.save(marketingFullGiftDetail);
        }
    }

    //满赠增加一个赠品
    @Transactional
    public List<String> addActivityGiveGoods(AddActivityGiveGoodsRequest request){
        List<String> productIds = request.getAddActivitGoodsRequest().stream().map(o -> o.getProductId()).collect(Collectors.toList());

        List<MarketingFullGiftDetail> byMarketingIdAndProductIdIn = marketingFullGiftDetailRepository.findByMarketingIdAndProductIdIn(request.getMarketingId(), productIds);
        if(CollectionUtils.isNotEmpty(byMarketingIdAndProductIdIn)){
            return byMarketingIdAndProductIdIn.stream().map(o -> o.getProductId()).collect(Collectors.toList());
        }

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
        return new ArrayList<>();
    }

    //满赠增加一个商品
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

            if (CollectionUtils.isNotEmpty(existsList)) {
                return existsList;
            }
        }
        List<MarketingScope> byMarketingId = marketingScopeRepository.findByMarketingId(request.getMarketingId());
        List<ActivitGoodsRequest> requestArrayList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(byMarketingId)){
            List<String> goodsInfoIds = byMarketingId.stream().map(o -> o.getScopeId()).collect(Collectors.toList());
            request.getAddActivitGoodsRequest().forEach(var->{
                if(!goodsInfoIds.contains(var.getScopeId())){
                    requestArrayList.add(var);
                }
            });
        }else{
            requestArrayList.addAll(request.getAddActivitGoodsRequest());
        }
        requestArrayList.forEach(var->{
            MarketingScope marketingScope = new MarketingScope();
            BeanUtils.copyProperties(var,marketingScope);
            marketingScope.setTerminationFlag(BoolFlag.NO);
            marketingScope.setMarketingId(request.getMarketingId());
            marketingScopeRepository.save(marketingScope);
        });
        return new ArrayList<>();
    }
    
    /**
     * 获得正在进行中的促销活动的店铺id集合
     * @return
     */
    public List<Long>  getActiveMarketingStoreIds() {
    	List<Long> ids = marketingRepository.getActiveMarketingStoreIds();
    	return ids;
    }

    public List<String> listEffectiveStoreGoodsInfoIds() {
        return marketingRepository.listEffectiveStoreGoodsInfoIds();
    }
}
