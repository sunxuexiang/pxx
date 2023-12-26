package com.wanmi.sbc.marketing.pile.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.request.pile.*;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityDetailByIdResponse;
import com.wanmi.sbc.marketing.bean.constant.PileErrorCode;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.PileActivityType;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.marketing.pile.model.root.PileActivity;
import com.wanmi.sbc.marketing.pile.model.root.PileActivityGoods;
import com.wanmi.sbc.marketing.pile.repository.PileActivityGoodsRepository;
import com.wanmi.sbc.marketing.pile.repository.PileActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: chenchang
 * @Date: 2022/09/06
 * @Description: 囤货活动Service
 */
@Service
@Slf4j
public class PileActivityService {

    @Autowired
    private PileActivityRepository pileActivityRepository;

    @Autowired
    private PileActivityGoodsRepository pileActivityGoodsRepository;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    /**
     * 查询活动列表
     *
     * @param request
     */
    public Page<PileActivity> page(PileActivityPageRequest request, Long storeId) {
        //查询列表
        String sql = "SELECT t.* FROM pile_activity t ";
        //条件查询
        StringBuilder whereSql = new StringBuilder("WHERE t.del_flag = 0");
        if (null != storeId){
            whereSql.append(" AND t.store_id = " + storeId);
        }
        if (StringUtils.isNotBlank(request.getStoreName())){
            whereSql.append(" AND t.store_name LIKE '%" + request.getStoreName() + "%'");
        }
        //活动名称查找
        if (StringUtils.isNotBlank(request.getActivityName())) {
            whereSql.append(" AND t.activity_name LIKE '%" + request.getActivityName() + "%'");
        }

        //活动类型筛选
        if (PileActivityType.FULL_PAID_PILE.equals(request.getPileActivityType())) {
            whereSql.append(" AND t.activity_type = " + PileActivityType.FULL_PAID_PILE.toValue());
        }

        //时间筛选
        if (null != request.getStartTime()) {
            whereSql.append(" AND '" + DateUtil.format(request.getStartTime(), DateUtil.FMT_TIME_1) + "' <= t" +
                    ".start_time");
        }
        if (null != request.getEndTime()) {
            whereSql.append(" AND '" + DateUtil.format(request.getEndTime(), DateUtil.FMT_TIME_1) + "' >= t.end_time");
        }

        switch (Objects.isNull(request.getQueryTab()) ? MarketingStatus.ALL : request.getQueryTab()) {
            case STARTED://进行中
                whereSql.append(" AND ((now() >= t.start_time AND now() <= t.end_time) ) AND t.termination_flag = 0");
                break;
            case NOT_START://未开始
                whereSql.append(" AND now() < t.start_time AND t.termination_flag = 0");
                break;
            case ENDED://已结束
                whereSql.append(" AND (now() > t.end_time OR t.termination_flag = 1)");
                break;
            default:
                break;
        }


        Query query = entityManager.createNativeQuery(sql.concat(whereSql.toString()));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("t", PileActivity.class);
        List<PileActivity> responsesList = (List<PileActivity>) query.getResultList();
        //查询记录的总数量
        String countSql = "SELECT count(1) count FROM pile_activity t ";
        long count = 0;
        if (CollectionUtils.isNotEmpty(responsesList)) {
            Query queryCount = entityManager.createNativeQuery(countSql.concat(whereSql.toString()));
            count = Long.parseLong(queryCount.getSingleResult().toString());
        }
        log.info("囤货活动列表返回信息------------------>{}", responsesList);
        return new PageImpl<>(responsesList, request.getPageable(), count);
    }

    /**
     * 获取囤货活动详情
     *
     * @param activityId
     * @return
     */
    public PileActivityDetailByIdResponse getDetailById(String activityId) {
        Optional<PileActivity> pileActivityOptional = pileActivityRepository.findById(activityId);
        if (!pileActivityOptional.isPresent()) {
           return new PileActivityDetailByIdResponse();
        }

        PileActivityVO pileActivityVO = new PileActivityVO();
        KsBeanUtil.copyPropertiesThird(pileActivityOptional.get(), pileActivityVO);

        PileActivityDetailByIdResponse response = new PileActivityDetailByIdResponse();
        response.setPileActivity(pileActivityVO);
        return response;
    }

    /**
     * 创建囤货活动
     * 1、囤货场次将做为活动形式展现，同一时间段内只允许一场囤货场次在进行中状态
     * 2、如果有正在进行中的囤货场次，新增的囤货场次开始时间要大于上一场活动的结束时间，否则新增失败进行提示：场次时间不能与进行中场次重叠请重新选择时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(PileActivityAddRequest request) {
        RLock rLock = redissonClient.getFairLock(RedisKeyConstant.PILE_ACTIVITY_ADD + request.getStoreId());
        rLock.lock();
        try {
            check(request.getStoreId(), request.getStartTime(), request.getEndTime(), null);

            PileActivity pileActivity = new PileActivity();
            KsBeanUtil.copyPropertiesThird(request, pileActivity);
            pileActivity.setCreateTime(LocalDateTime.now());
            pileActivity.setDelFlag(DeleteFlag.NO);

            //查询店铺信息
            BaseResponse<StoreByIdResponse> resp =  storeQueryProvider.getById(StoreByIdRequest.builder().storeId(request.getStoreId()).build());
            pileActivity.setStoreName(resp.getContext().getStoreVO().getStoreName());

            pileActivityRepository.saveAndFlush(pileActivity);
        } finally {
            rLock.unlock();
        }
    }


    /**
     * 修改囤货活动
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void modify(PileActivityModifyRequest request) {
        RLock rLock = redissonClient.getFairLock(RedisKeyConstant.PILE_ACTIVITY_ADD + request.getStoreId());
        rLock.lock();
        try {
//            check(request.getStoreId(), request.getStartTime(), request.getEndTime(), request.getActivityId());
            // 开始时间不能大于启动时间
            if (Objects.requireNonNull(request.getStartTime()).isAfter(request.getEndTime())) {
                throw new SbcRuntimeException(PileErrorCode.END_DATE_ERROR);
            }

            // 校验，如果活动未开始才可以编辑
            PileActivity pileActivity = checkExists(request.getActivityId());

            // 查询当前活动可修改的时间区间
            LocalDateTime endTime = pileActivity.getEndTime();

            PileActivity recentPileActivity = pileActivityRepository.findRecentTimeByEndTime(request.getStoreId(),endTime);
            if(Objects.nonNull(recentPileActivity)){// 当前活动结束时间后存在活动
                LocalDateTime startTime = recentPileActivity.getStartTime();
                // 修改的时间不能大于最近一个活动的开始时间
                if (request.getEndTime().isAfter(startTime)) {
                    throw new SbcRuntimeException(PileErrorCode.TIME_NOT_AVAILABLE);
                }
            }
            KsBeanUtil.copyProperties(request, pileActivity);
            pileActivity.setUpdateTime(LocalDateTime.now());
            pileActivityRepository.save(pileActivity);
        } finally {
            rLock.unlock();
        }
    }

    private void check(Long storeId, LocalDateTime startTime, LocalDateTime endTime, String activityId) {
        if (Objects.isNull(activityId)) {
            //创建时，还未产生id, 检查时间范围时，排除自身用空串进行排除
            activityId = "";
        }

        //开始时间不能晚于结束时间
        if (Objects.requireNonNull(startTime).isAfter(endTime)) {
            throw new SbcRuntimeException(PileErrorCode.END_DATE_ERROR);
        }

        //同一个时间段内，只能有一场囤货活动在进行中状态 (场次时间不能与进行中场次重叠请重新选择时间)
        List<PileActivity> pileActivityList = pileActivityRepository.findByTimeSpan(
                storeId,
                startTime,
                endTime,
                activityId);

        if (CollectionUtils.isNotEmpty(pileActivityList)) {
            throw new SbcRuntimeException(PileErrorCode.TIME_NOT_AVAILABLE);
        }

    }

    /**
     * 关闭囤货活动
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void close(PileActivityCloseByIdRequest request) {
        if (Objects.isNull(request.getId())) {
            return;
        }
        pileActivityRepository.close(request.getId(), LocalDateTime.now());
    }

    /**
     * 删除囤货活动
     *
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(PileActivityDeleteByIdRequest request) {
        if (Objects.isNull(request.getId())) {
            return;
        }
        PileActivity pileActivity = pileActivityRepository.getOne(request.getId());
        pileActivity.setDelFlag(DeleteFlag.YES);
        pileActivity.setDelPerson(request.getDeletePerson());
        pileActivity.setDelTime(LocalDateTime.now());
        pileActivityRepository.save(pileActivity);
    }

    /**
     * 构建用于展示的商品信息
     *
     * @param activityGoods
     * @return
     */
    private GoodsInfoResponse buildGoodsList(List<PileActivityGoods> activityGoods) {
        //组装商品信息
        List<String> goodsInfoIds = activityGoods.stream().map(PileActivityGoods::getGoodsInfoId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(goodsInfoIds)) {
            return new GoodsInfoResponse();
        }

        GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
        //FIXME 营销是平铺展示，但是数量达到一定层级，还是需要分页，先暂时这么控制
        queryRequest.setPageSize(10000);
//            queryRequest.setStoreId(couponInfo.getStoreId());
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        GoodsInfoViewPageResponse goodsInfoResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();
        return GoodsInfoResponse.builder()
                .goodsInfoPage(goodsInfoResponse.getGoodsInfoPage())
                .brands(goodsInfoResponse.getBrands())
                .cates(goodsInfoResponse.getCates())
                .goodses(goodsInfoResponse.getGoodses())
                .build();
    }

    //获取参与活动的商品goodsInfoIds
    public List<String>  getStartPileActivityGoodsInfoIds(){
        //囤货活动仅可同时开启一个
        List<PileActivity> startPileActivity = pileActivityRepository.getStartPileActivity(PileActivityType.FULL_PAID_PILE.toValue());
        if(CollectionUtils.isNotEmpty(startPileActivity)){
            //多商家囤货活动商品
            List<String> activityIds = startPileActivity.stream().map(PileActivity::getActivityId).collect(Collectors.toList());
            List<PileActivityGoods> byActivityId = pileActivityGoodsRepository.findByActivityIdIn(activityIds);
            return byActivityId.stream().map(PileActivityGoods::getGoodsInfoId).collect(Collectors.toList());
        }
        return null;
    }


    //获取参与活动
    public List<PileActivityVO>  getStartPileActivity(){
        //囤货活动仅可同时开启一个
        return KsBeanUtil.convert(pileActivityRepository.getStartPileActivity(PileActivityType.FULL_PAID_PILE.toValue()), PileActivityVO.class);
    }

    //获取下单囤货商品虚拟库存
    public List<PileActivityGoods>  getStartPileActivityVirtualStock(String pileActivityId,  List<String> goodsInfoIds){
        return pileActivityGoodsRepository.findByActivityIdAndGoodsInfoIdInAndDelFlag(pileActivityId,goodsInfoIds,DeleteFlag.NO);
    }


    //获取下单囤货商品虚拟库存
    @Transactional
    public void  updateVirtualStock(List<PileActivityStockRequest> request){
        if(CollectionUtils.isNotEmpty(request)){
            request.forEach(var->{
                if(var.getAddOrSub()){
                    pileActivityGoodsRepository.subVirtualStock(var.getNum().longValue(),var.getGoodsInfoId(),var.getActivityId());
                }else {
                    pileActivityGoodsRepository.addVirtualStock(var.getNum().longValue(),var.getGoodsInfoId(),var.getActivityId());
                }
            });
        }else{
           throw new RuntimeException("扣减囤货虚拟库存参数异常！");
        }
    }


    public PileActivityVO  getByActivityId(PileActivityCloseByIdRequest request){
        Optional<PileActivity> pileActivityOptional = pileActivityRepository.findById(request.getId());
        if(pileActivityOptional.isPresent()){
            PileActivityVO convert = KsBeanUtil.convert(pileActivityOptional.get(), PileActivityVO.class);
            return convert;
        }
        return null;
    }

    public PileActivity checkExists(String activityId) {
        PileActivity pileActivity = pileActivityRepository.findByActivityIdAndDelFlag(activityId, DeleteFlag.NO);
        if (Objects.isNull(pileActivity)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "囤货活动不存在！");
        }
        return pileActivity;
    }


    //获取多商家下单囤货商品虚拟库存
    public List<PileActivityGoods> getStartPileActivityGoodsListVirtualStock(List<String> goodsInfoIds){
        if (CollectionUtils.isEmpty(goodsInfoIds)){
            return new ArrayList<>();
        }
        return pileActivityGoodsRepository.findPideGoodsListByGoodsInfoIds(goodsInfoIds);
    }
}
