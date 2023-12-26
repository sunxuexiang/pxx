package com.wanmi.sbc.marketing.coupon.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityTerminationRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinAddActiviStoreRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinAddActivitGoodsRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinRecordPageRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CoinActivityDetailResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreDTO;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreRecordDTO;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreRecordDetailDTO;
import com.wanmi.sbc.marketing.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityGoodsVo;
import com.wanmi.sbc.marketing.bean.vo.CoinGoodsVo;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivity;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityGoods;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityRecord;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityRecordDetail;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStore;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStoreRecord;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStoreRecordDetail;
import com.wanmi.sbc.marketing.coupon.model.vo.CoinActivityQueryVo;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityGoodsRepository;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityRecordDetailRepository;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityRecordRepository;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityRepository;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityStoreRecordDetailRepository;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityStoreRecordRepository;
import com.wanmi.sbc.marketing.coupon.repository.CoinActivityStoreRepository;
import com.wanmi.sbc.marketing.redis.RedisLock;
import com.wanmi.sbc.marketing.redis.RedisLockKeyConstants;
import com.wanmi.sbc.marketing.util.ChineseNumberUtil;
import com.wanmi.sbc.marketing.util.ConfigUtil;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.ReturnItemVO;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO.WalletSettlementVo;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletGiveRequest;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletOrderByRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletInfoRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * 指定商品赠金币
 *
 * @Author : Like
 * @create 2023/5/22 9:30
 */
@Service
@Slf4j
public class CoinActivityService {

    @Autowired
    private CoinActivityRepository coinActivityRepository;

    @Autowired
    private CoinActivityGoodsRepository coinActivityGoodsRepository;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CoinActivityRecordRepository coinActivityRecordRepository;

    @Autowired
    private CoinActivityRecordDetailRepository coinActivityRecordDetailRepository;
    
    @Autowired
    private CoinActivityStoreRepository coinActivityStoreRepository;
    
    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    
    @Autowired
    private WalletMerchantProvider walletMerchantProvider;
    
    @Autowired
    private CustomerWalletProvider customerWalletProvider;
    
    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;
    
    @Autowired
    private CoinActivityStoreRecordRepository coinActivityStoreRecordRepository;

    @Autowired
    private CoinActivityStoreRecordDetailRepository coinActivityStoreRecordDetailRepository;
    
    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;
    
    @Autowired
    private ConfigUtil configUtil;
    
    private boolean checkStoreIds(List<Long> configStoreIds, LocalDateTime start, LocalDateTime end) {
    	List<Long> conflictStoreIds = coinActivityStoreRepository.getConflictStoreIds(start, end, configStoreIds);
    	if (CollectionUtils.isEmpty(conflictStoreIds)) {
			return true;
		}
    	return false;
	}
    
    @Transactional(rollbackFor = Exception.class)
    public void add(CoinActivityAddRequest request) {
		if (CoinActivityType.ORDER == request.getActivityType()) {
			List<CoinActivityStoreDTO> configStore = request.getCoinActivityStore();
			if (CollectionUtils.isEmpty(configStore)) {
				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单返鲸币配置的商家不能为空");
			}
			List<Long> configStoreIds = configStore.stream().map(CoinActivityStoreDTO::getStoreId).collect(Collectors.toList());
			boolean checkStoreIds = checkStoreIds(configStoreIds, request.getStartTime(), request.getEndTime());
			if (!checkStoreIds) {
				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "选择的商家与其他活动关联的商家重复");
			}
		} else {
			List<String> goodsInfoIds = request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId)
					.collect(Collectors.toList());
			int num = coinActivityGoodsRepository.checkSkuIds(goodsInfoIds);
			if (num > 0) {
				throw new SbcRuntimeException(CouponErrorCode.CHOSE_ACTIVITY_GOODS_ERROR);
			}
		}

        CoinActivity coinActivity = KsBeanUtil.convert(request, CoinActivity.class);
        coinActivity.setDelFlag(DeleteFlag.NO);
        coinActivity.setCreateTime(LocalDateTime.now());
        coinActivity.setUpdateTime(LocalDateTime.now());
        coinActivity.setUpdatePerson(request.getCreatePerson());
        coinActivityRepository.save(coinActivity);
        
    	if (CoinActivityType.ORDER == request.getActivityType()) {
            request.getCoinActivityStore().forEach(s -> {
            	saveCoinActivityStore(coinActivity.getActivityId(), s);
            });
    	}else {
            List<CoinActivityGoods> coinActivityGoodsList = new ArrayList<>();
            request.getGoodsInfos().forEach(goodsInfo -> {
                CoinActivityGoods coinActivityGoods = new CoinActivityGoods();
                coinActivityGoods.setActivityId(coinActivity.getActivityId());
                coinActivityGoods.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                //新增字段
                coinActivityGoods.setDisplayType(goodsInfo.getDisplayType()==null?0:goodsInfo.getDisplayType());
                coinActivityGoodsList.add(coinActivityGoods);
            });
            coinActivityGoodsRepository.saveAll(coinActivityGoodsList);
		}

    }

	private void saveCoinActivityStore(String actId, CoinActivityStoreDTO s) {
		CoinActivityStore copyPropertiesThird = KsBeanUtil.copyPropertiesThird(s, CoinActivityStore.class);
		copyPropertiesThird.setActivityId(actId);
		copyPropertiesThird.setTerminationFlag(BoolFlag.NO);
		coinActivityStoreRepository.save(copyPropertiesThird);
	}

    @Transactional(rollbackFor = Exception.class)
    public void update(CoinActivityModifyRequest request) {

        CoinActivity activity = coinActivityRepository.findById(request.getActivityId()).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));
		if (CoinActivityType.ORDER == activity.getActivityType()) {
			List<CoinActivityStoreDTO> configStore = request.getCoinActivityStore();
			if (CollectionUtils.isEmpty(configStore)) {
				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单返鲸币配置的商家不能为空");
			}
			List<Long> configStoreIds = configStore.stream().map(CoinActivityStoreDTO::getStoreId).collect(Collectors.toList());
			boolean checkStoreIds = checkStoreIds(configStoreIds, request.getStartTime(), request.getEndTime());
			if (!checkStoreIds) {
				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "选择的商家与其他活动关联的商家重复");
			}
		} else {
			List<String> goodsInfoIds = request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId)
					.collect(Collectors.toList());
			int num = coinActivityGoodsRepository.checkSkuIds(goodsInfoIds);
			if (num > 0) {
				throw new SbcRuntimeException(CouponErrorCode.CHOSE_ACTIVITY_GOODS_ERROR);
			}
		}
        
        KsBeanUtil.copyProperties(request, activity);
        activity.setUpdateTime(LocalDateTime.now());
        coinActivityRepository.saveAndFlush(activity);

    	if (CoinActivityType.ORDER == activity.getActivityType()) {
            request.getCoinActivityStore().forEach(s -> {
            	saveCoinActivityStore(activity.getActivityId(), s);
            });
    	}else {
            List<CoinActivityGoods> coinActivityGoodsList = new ArrayList<>();
            request.getGoodsInfos().forEach(goodsInfo -> {
                CoinActivityGoods coinActivityGoods = new CoinActivityGoods();
                coinActivityGoods.setActivityId(activity.getActivityId());
                coinActivityGoods.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                coinActivityGoods.setDisplayType(goodsInfo.getDisplayType()==null?0:goodsInfo.getDisplayType());
                coinActivityGoodsList.add(coinActivityGoods);
            });
            coinActivityGoodsRepository.saveAll(coinActivityGoodsList);
		}
    }

    public void addActivityGoods(CoinAddActivitGoodsRequest request) {
        CoinActivity activity = coinActivityRepository.findById(request.getActivityId()).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(BoolFlag.YES, activity.getTerminationFlag()) || now.isAfter(activity.getEndTime())) {
            throw new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_IS_OVER);
        }

        if (CollectionUtils.isNotEmpty(request.getGoodsInfos())) {
            List<String> goodsInfoIdList=request.getGoodsInfos().stream().map(GoodsInfoDTO::getGoodsInfoId).collect(Collectors.toList());
            int num = coinActivityGoodsRepository.checkSkuIds(goodsInfoIdList);
            if (num > 0) {
                throw new SbcRuntimeException(CouponErrorCode.CHOSE_ACTIVITY_GOODS_ERROR);
            }
            List<String> goodsInfoIds = coinActivityGoodsRepository.findGoodsInfoIdsByActivityId(request.getActivityId());
            List<CoinActivityGoods> coinActivityGoodsList = new ArrayList<>();
            request.getGoodsInfos().forEach(goodsInfo -> {
                if (goodsInfoIds.contains(goodsInfo.getGoodsInfoId())) {
                    throw new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_IS_OVER);
                }
                CoinActivityGoods coinActivityGoods = new CoinActivityGoods();
                coinActivityGoods.setActivityId(activity.getActivityId());
                coinActivityGoods.setGoodsInfoId(goodsInfo.getGoodsInfoId());
                coinActivityGoods.setDisplayType(goodsInfo.getDisplayType()==null?0:goodsInfo.getDisplayType());
                coinActivityGoodsList.add(coinActivityGoods);
            });
            coinActivityGoodsRepository.saveAll(coinActivityGoodsList);
        }

    }

    public CoinActivityDetailResponse detail(String id) {
        CoinActivity activity = coinActivityRepository.findById(id).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));
        CoinActivityDetailResponse response = KsBeanUtil.convert(activity, CoinActivityDetailResponse.class);
     	if (CoinActivityType.ORDER == activity.getActivityType()) {
     		List<CoinActivityStore> activityStore = coinActivityStoreRepository.findByActivityId(id);
     		response.setCoinActivityStore(KsBeanUtil.convert(activityStore, CoinActivityStoreDTO.class));
     	}else{
     	     List<CoinActivityGoods> activityGoods = coinActivityGoodsRepository.findByActivityId(id);
             response.setCoinActivityGoodsVoList(KsBeanUtil.convert(activityGoods, CoinActivityGoodsVo.class));
     	}
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteById(String id, String operatorId) {
        CoinActivity activity = coinActivityRepository.findById(id).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));

        LocalDateTime now = LocalDateTime.now();
        boolean started = now.isAfter(activity.getStartTime()) && now.isBefore(activity.getEndTime());

        if (started) {
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }

        String updateLock = RedisLockKeyConstants.COIN_UPDATE_LOCK_PREFIX + activity.getActivityId();
        boolean lock = redisLock.writeLock(updateLock);
        if (!lock) {
            log.info("操作金币活动获取锁失败，operator:{}", activity.getActivityId());
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        }
        try {

            return coinActivityRepository.updateDelFlagById(id, operatorId, LocalDateTime.now());
        } catch (Exception e) {
            log.info("DeleteCoinById exception", e);
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_DELETE);
        } finally {
            redisLock.unWriteLock(updateLock);
        }
        // return coinActivityRepository.updateDelFlagById(id, operatorId, LocalDateTime.now());
    }


    @Transactional(rollbackFor = Exception.class)
    public void terminationById(String id, String operatorId) {

        CoinActivity coinActivity = coinActivityRepository.findById(id).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));

        boolean marketingTerminated = Objects.equals(BoolFlag.YES, coinActivity.getTerminationFlag());
        boolean marketingDeleted = Objects.equals(DeleteFlag.YES, coinActivity.getDelFlag());
        boolean marketingStarted = LocalDateTime.now().isAfter(coinActivity.getStartTime())
                && LocalDateTime.now().isBefore(coinActivity.getEndTime());
        if (!marketingStarted        // 活动未开始或已经结束
                || marketingTerminated  // 已终止
                || marketingDeleted) {   // 已删除
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_PAUSE);
        }

        String updateLock = RedisLockKeyConstants.COIN_UPDATE_LOCK_PREFIX + coinActivity.getActivityId();
        boolean lock = redisLock.writeLock(updateLock);
        if (!lock) {
            log.info("操作金币活动获取锁失败，operator:{}", coinActivity.getActivityId());
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        try {
           	if (CoinActivityType.ORDER == coinActivity.getActivityType()) {
           		coinActivityStoreRepository.updateTerminationInfo(BoolFlag.YES, LocalDateTime.now(), coinActivity.getActivityId());
           	}else {
           		coinActivityGoodsRepository.updateTerminationFlagAndTerminationTimeByActivityId(BoolFlag.YES, LocalDateTime.now(), coinActivity.getActivityId());
			}

            LocalDateTime now = LocalDateTime.now();
            coinActivity.setTerminationFlag(BoolFlag.YES);
            coinActivity.setRealEndTime(coinActivity.getEndTime());
            boolean isStart = (now.isAfter(coinActivity.getStartTime()) && now.isBefore(coinActivity.getEndTime()));
            if (!isStart) {
                coinActivity.setStartTime(now);
            }
            coinActivity.setEndTime(now);
            coinActivity.setUpdateTime(now);
            coinActivity.setUpdatePerson(operatorId);
            coinActivityRepository.saveAndFlush(coinActivity);

        } catch (Exception e) {
            log.info("TerminationCoinById exception", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        } finally {
            redisLock.unWriteLock(updateLock);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void terminationGoods(CoinActivityTerminationRequest request) {
        String activityId = request.getActivityId();
        Long activityGoodsId = request.getActivityGoodsId();

        CoinActivity coinActivity = coinActivityRepository.findById(activityId).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));
        CoinActivityGoods coinActivityGoods = coinActivityGoodsRepository.findByActivityIdAndId(activityId, activityGoodsId);

        if (Objects.isNull(coinActivityGoods) || Objects.equals(BoolFlag.YES, coinActivityGoods.getTerminationFlag())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        String updateLock = RedisLockKeyConstants.COIN_UPDATE_LOCK_PREFIX + coinActivity.getActivityId();
        boolean lock = redisLock.writeLock(updateLock);
        if (!lock) {
            log.info("操作金币活动获取锁失败，operator:{}", coinActivity.getActivityId());
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        try {
            coinActivityGoods.setTerminationFlag(BoolFlag.YES);
            coinActivityGoods.setTerminationTime(LocalDateTime.now());
            coinActivityGoodsRepository.saveAndFlush(coinActivityGoods);

            long count = coinActivityGoodsRepository.countByActivityIdAndTerminationFlag(activityId, BoolFlag.NO);

            LocalDateTime now = LocalDateTime.now();
            if (count < 1L) {

                coinActivity.setTerminationFlag(BoolFlag.YES);
                coinActivity.setRealEndTime(coinActivity.getEndTime());
                boolean isStart = (now.isAfter(coinActivity.getStartTime()) && now.isBefore(coinActivity.getEndTime()));
                if (!isStart) {
                    coinActivity.setStartTime(now);
                }
                coinActivity.setEndTime(now);
            }
            coinActivity.setUpdateTime(now);
            coinActivity.setUpdatePerson(request.getOperatorId());
            coinActivityRepository.saveAndFlush(coinActivity);
        } catch (Exception e) {
            log.info("terminationGoods exception", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        } finally {
            redisLock.unWriteLock(updateLock);
        }


    }

    public Page<CoinActivity> pageActivityInfo(CoinActivityPageRequest request) {
        Long storeId = request.getStoreId();
        //查询列表
        String sql = "SELECT t.* FROM coin_activity t ";
        //条件查询
        StringBuilder whereSql = new StringBuilder("WHERE t.del_flag = 0");

    	if (CoinActivityType.ORDER == request.getActivityType()) {
    		whereSql.append(" AND t.activity_type = 1 ");
    		if (StringUtils.isNotEmpty(request.getStoreName())) {
    			List<CoinActivityStore> activityStore = coinActivityStoreRepository.findByStoreNameLike("%" + request.getStoreName() + "%" );
    			List<String> collect = activityStore.stream().map(CoinActivityStore::getActivityId).collect(Collectors.toList());
    			collect.add("-1");
    			request.setActivityIds(collect);
			}
    	}else {
    		whereSql.append(" AND t.activity_type = 0 ");
    	}
        
        if (CollectionUtils.isNotEmpty(request.getGoodsInfoIds())) {
            List<CoinActivityGoods> coinActivityGoodsList = coinActivityGoodsRepository.findByGoodsInfoIdIn(request.getGoodsInfoIds());
            StringBuilder stringBuilder = new StringBuilder();

            if (CollectionUtils.isNotEmpty(coinActivityGoodsList)) {
                coinActivityGoodsList.forEach(var -> {
                    stringBuilder.append("'").append(var.getActivityId()).append("'");
                    stringBuilder.append(",");
                });
            } else {
                stringBuilder.append(Long.MAX_VALUE);
            }

            whereSql.append(" AND t.activity_id in (").append(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1)).append(")");
        }

        if (CollectionUtils.isNotEmpty(request.getActivityIds())) {
            StringBuilder stringBuilder = new StringBuilder();
            request.getActivityIds().forEach(var -> {
                stringBuilder.append("'").append(var).append("'");
                stringBuilder.append(",");
            });

            whereSql.append(" AND t.activity_id in (").append(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1)).append(")");
        }

        if (storeId != null) {
            whereSql.append(" AND t.store_id = ").append(storeId);
        }
        //活动名称查找
        if (StringUtils.isNotBlank(request.getActivityName())) {
            whereSql.append(" AND t.activity_name LIKE '%").append(request.getActivityName()).append("%'");
        }

        //仓库id筛选
        if (Objects.nonNull(request.getWareId())) {
            whereSql.append(" AND t.ware_id = ").append(request.getWareId());
        }


        //时间筛选
        if (null != request.getStartTime()) {
            whereSql.append(" AND '").append(DateUtil.format(request.getStartTime(), DateUtil.FMT_TIME_1)).append("' <= t").append(".start_time");
        }
        if (null != request.getEndTime()) {
            whereSql.append(" AND '").append(DateUtil.format(request.getEndTime(), DateUtil.FMT_TIME_1)).append("' >= t.end_time");
        }

        // 全部0  进行中1  未开始3  已结束4  已终止6
        switch (request.getQueryTab()) {
            // 进行中 1
            case STARTED:
                whereSql.append(" AND ((now() >= t.start_time AND now() <= t.end_time)) AND t" +
                        ".pause_flag = 0");
                break;
            //未开始
            case NOT_START:
                whereSql.append(" AND now() < t.start_time");
                break;
            // 已结束
            case ENDED:
                whereSql.append(" AND now() > t.end_time");
                break;
            // 终止
            case TERMINATION:
                whereSql.append(" AND t.termination_flag = 1");
                break;
            default:
                break;
        }

        whereSql.append(" order by t.create_time desc");
        Query query = entityManager.createNativeQuery(sql.concat(whereSql.toString()));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("t", CoinActivity.class);
        List<CoinActivity> responsesList = (List<CoinActivity>) query.getResultList();
        //查询记录的总数量
        String countSql = "SELECT count(1) count FROM coin_activity t ";
        long count = 0;
        if (CollectionUtils.isNotEmpty(responsesList)) {
            Query queryCount = entityManager.createNativeQuery(countSql.concat(whereSql.toString()));
            count = Long.parseLong(queryCount.getSingleResult().toString());
        }
        log.info("金币活动列表返回信息------------------>{}", responsesList);
        return new PageImpl<>(responsesList, request.getPageable(), count);
    }


    public List<CoinGoodsVo> checkSendCoin(List<TradeItemInfoDTO> items) {
        List<CoinGoodsVo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            List<String> skuIds = items.stream().map(TradeItemInfoDTO::getSkuId).collect(Collectors.toList());
            Map<String, TradeItemInfoDTO> dtoMap = items.stream().collect(Collectors.toMap(TradeItemInfoDTO::getSkuId, Function.identity(), (o1, o2) -> o1));
            List<CoinActivityQueryVo> vos = coinActivityRepository.queryByGoodsInfoIds(skuIds);
            for (CoinActivityQueryVo vo : vos) {
                CoinGoodsVo coinGoodsVo = new CoinGoodsVo();
                coinGoodsVo.setCoinActivityId(vo.getActivityId());
                coinGoodsVo.setGoodsInfoId(vo.getGoodsInfoId());
                Long num = dtoMap.get(vo.getGoodsInfoId()).getNum();
                num = Objects.isNull(num) ? 1 : num;
                coinGoodsVo.setNum(num);
                if (Objects.equals(DefaultFlag.NO, vo.getIsOverlap())) {
                    coinGoodsVo.setTotalCoinNum(vo.getCoinNum());
                }
                if (Objects.equals(DefaultFlag.YES, vo.getIsOverlap())) {
                    coinGoodsVo.setTotalCoinNum(vo.getCoinNum().multiply(BigDecimal.valueOf(coinGoodsVo.getNum())).setScale(2, RoundingMode.HALF_UP));
                }
                result.add(coinGoodsVo);
            }
        }

        return result;
    }

    public List<CoinActivityQueryVo> queryCoinActivityBySkuIds(List<String> skuIds) {
        return coinActivityRepository.queryByGoodsInfoIds(skuIds);
    }

    @Transactional
    @LcnTransaction
    public List<Long> saveCoinRecord(List<CoinActivityRecordDto> request) {
        List<Long> result = new ArrayList<>();
        for (CoinActivityRecordDto recordDto : request) {

            CoinActivityRecord record = KsBeanUtil.convert(recordDto, CoinActivityRecord.class);

            CoinActivityRecord exRecord = coinActivityRecordRepository.findByOrderNoAndActivityId(record.getOrderNo(), record.getActivityId());
            if (Objects.nonNull(exRecord)) {
                exRecord.setSendNo(recordDto.getSendNo());
                coinActivityRecordRepository.save(exRecord);
                result.add(exRecord.getRecordId());
            }else {
                Long recordId = coinActivityRecordRepository.save(record).getRecordId();

                List<CoinActivityRecordDetail> details = KsBeanUtil.convert(recordDto.getDetailList(), CoinActivityRecordDetail.class);
                for (CoinActivityRecordDetail detail : details) {
                    detail.setRecordId(recordId);
                }
                coinActivityRecordDetailRepository.saveAll(details);
                result.add(recordId);
            }
        }
        return result;
    }

    public List<CoinActivityRecord> queryCoinActivityRecordByRecordIds(List<Long> recordIds) {
        return coinActivityRecordRepository.findByRecordIdIn(recordIds);
    }

    public List<CoinActivityRecordDto> queryCoinActivityRecordByOrderId(String orderId) {
        List<CoinActivityRecord> records = coinActivityRecordRepository.findByOrderNo(orderId);
        List<CoinActivityRecordDto> recordDtos = KsBeanUtil.convert(records, CoinActivityRecordDto.class);
        for (CoinActivityRecordDto recordDto : recordDtos) {
            List<CoinActivityRecordDetail> details = coinActivityRecordDetailRepository.findByRecordId(recordDto.getRecordId());
            List<CoinActivityRecordDetailDto> detailDtos = KsBeanUtil.convert(details, CoinActivityRecordDetailDto.class);
            recordDto.setDetailList(detailDtos);
        }
        return recordDtos;
    }


    public List<CoinActivityRecordDetailDto> queryCoinActivityRecordByOrderIdAndSkuIds(String orderId, List<String> skuIds) {
        // List<CoinActivityRecordDetail> detailList = coinActivityRecordDetailRepository.findByOrderNoAndGoodsInfoIdIn(orderId, skuIds);
        List<CoinActivityRecordDetail> detailList = coinActivityRecordDetailRepository.findByOrderNoAndGoodsInfoIdInAndRecordType(orderId, skuIds, 1);
        return KsBeanUtil.convert(detailList, CoinActivityRecordDetailDto.class);
    }

    public Page<CoinActivityRecord> recordPage(CoinRecordPageRequest request) {

        String activityId = request.getActivityId();
        String orderNo = request.getOrderNo();
        String customerAccount = request.getCustomerAccount();
        //查询列表
        String sql = "SELECT * FROM coin_activity_record t ";
        //条件查询
        StringBuilder whereSql = new StringBuilder("WHERE record_type = 1 ");

        if (StringUtils.isNotBlank(activityId)) {
            whereSql.append(" AND  t.activity_id = '").append(activityId).append("'");
        }

        if (StringUtils.isNotBlank(orderNo)) {
            whereSql.append(" AND  t.order_no LIKE '%").append(orderNo).append("%'");
        }

        if (StringUtils.isNotBlank(customerAccount)) {
            whereSql.append(" AND  t.customer_account LIKE '%").append(customerAccount).append("%'");
        }

        whereSql.append(" order by t.record_time desc");
        Query query = entityManager.createNativeQuery(sql.concat(whereSql.toString()));
        query.setFirstResult(request.getPageNum() * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        query.unwrap(SQLQuery.class).addEntity("t", CoinActivityRecord.class);
        List<CoinActivityRecord> responsesList = (List<CoinActivityRecord>) query.getResultList();
        //查询记录的总数量
        String countSql = "SELECT count(1) count FROM coin_activity_record t ";
        long count = 0;
        if (CollectionUtils.isNotEmpty(responsesList)) {
            Query queryCount = entityManager.createNativeQuery(countSql.concat(whereSql.toString()));
            count = Long.parseLong(queryCount.getSingleResult().toString());
        }
        return new PageImpl<>(responsesList, request.getPageable(), count);
    }

    public List<CoinActivityRecordDetailDto> queryCoinActivityRecordDetailByOrderId(String orderId) {
        // List<CoinActivityRecordDetail> details = coinActivityRecordDetailRepository.findByOrderNo(orderId);
        List<CoinActivityRecordDetail> details = coinActivityRecordDetailRepository.findByOrderNoAndRecordType(orderId, 1);
        return KsBeanUtil.convert(details, CoinActivityRecordDetailDto.class);
    }

    public List<CoinActivityRecordDetailDto> queryCoinActivityRecordDetailByOrderIds(List<String> orderIds) {
        List<CoinActivityRecordDetail> details = coinActivityRecordDetailRepository.findByOrderNoInAndRecordType(orderIds, 1);
        return KsBeanUtil.convert(details, CoinActivityRecordDetailDto.class);
    }


    public CoinActivityRecord recordByOrderId(String orderId) {
        CoinActivityRecord record = new CoinActivityRecord();
        List<CoinActivityRecord> recordList = coinActivityRecordRepository.findByOrderNo(orderId);
        if (CollectionUtils.isNotEmpty(recordList)) {
            record = recordList.get(0);
            record.setActivityId(null);
            record.setRecordId(null);
            record.setCoinNum(recordList.stream().map(CoinActivityRecord::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return record;
    }

    public Integer queryGoodsCancelNum(String orderId, String goodsInfoId) {
        Integer num = coinActivityRecordDetailRepository.queryGoodsCancelNum(orderId, goodsInfoId);
        if (Objects.isNull(num)) {
            num = 0;
        }
        return num;
    }

    public CoinActivityRecord recordBySendNo(String sendNo) {
        CoinActivityRecord record = new CoinActivityRecord();
        List<CoinActivityRecord> recordList = coinActivityRecordRepository.findBySendNo(sendNo);
        if (CollectionUtils.isNotEmpty(recordList)) {
            record = recordList.get(0);
            record.setActivityId(null);
            record.setRecordId(null);
            record.setCoinNum(recordList.stream().map(CoinActivityRecord::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return record;
    }

    public Boolean queryCoinActivityRecordIsExist(String tid) {
        return coinActivityRecordRepository.existsByOrderNo(tid);
    }
    
    
    
    
	/**
	 * 订单返鲸币
	 * 
	 */
	@Transactional(rollbackFor = Exception.class)
	@LcnTransaction
	public void sendOrderCoin(String tid) {
		log.info("订单返鲸币开始,订单号：{}", tid);
		TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext()
				.getTradeVO();
        // 已付款普通订单才支持订单返鲸币
        boolean needSend = tradeVO.getTradeState().getPayState().equals(PayState.PAID) && Objects.equals("0", tradeVO.getActivityType());
        if (!needSend) {
            log.info("已付款普通订单才支持订单返鲸币[{}]", tid);
			return;
		}

		// 判断当前商家是否参与了返鲸币活动
		Long storeId = tradeVO.getSupplier().getStoreId();
		String storeName = tradeVO.getSupplier().getStoreName();
		CoinActivity coinActivity = coinActivityRepository.queryStoreRunningAct(storeId);
		if (coinActivity == null) {
			log.info("当前商家未参与返鲸币活动[{}]", storeId);
			return;
		}

		// 判断是否重复消费
		boolean exist = coinActivityStoreRecordRepository.existsByOrderNo(tid);
		if (exist) {
			log.info("订单返鲸币：重复消费，订单号：{}", tid);
			return;
		}

		String customerAccount = tradeVO.getBuyer().getAccount();
		LocalDateTime now = LocalDateTime.now();
		// 订单金额
		BigDecimal totalPrice = tradeVO.getTradePrice().getTotalPrice();
		// 订单运费金额
		BigDecimal deliveryPrice = tradeVO.getTradePrice().getDeliveryPrice();
		// 订单商品实付总额 = totalPrice - deliveryPrice
		BigDecimal realGoodsPrice = totalPrice.subtract(deliveryPrice);
		LocalDateTime orderTime = tradeVO.getTradeState().getCreateTime();
		// 算出此订单总共需要赠多少鲸币,四舍五入保留两位小数
		BigDecimal totalCoin = realGoodsPrice.multiply(coinActivity.getCoinNum()).setScale(2, BigDecimal.ROUND_HALF_UP);
		log.info("订单[{}]返鲸币realGoodsPrice[{}]totalCoin[{}]", tid, realGoodsPrice, totalCoin);
		
		// 赠送记录
		CoinActivityStoreRecordDTO recordDto = new CoinActivityStoreRecordDTO();
		recordDto.setActivityId(coinActivity.getActivityId());
		recordDto.setCustomerAccount(customerAccount);
		recordDto.setOrderNo(tid);
		recordDto.setOrderTime(orderTime);
		recordDto.setOrderPrice(totalPrice);
		recordDto.setRecordType(1);
		recordDto.setRecordTime(now);
		recordDto.setStoreId(storeId);
		recordDto.setStoreName(storeName);
		

		// 赠送记录明细
		List<CoinActivityStoreRecordDetailDTO> detailDtoList = new ArrayList<>();
		BigDecimal temp = BigDecimal.ZERO;
		for (int i = 0; i <  tradeVO.getTradeItems().size(); i++) {
			TradeItemVO tradeItemVO = tradeVO.getTradeItems().get(i);
			CoinActivityStoreRecordDetailDTO detailDto = new CoinActivityStoreRecordDetailDTO();
			String goodsInfoId = tradeItemVO.getSkuId();
			detailDto.setActivityId(coinActivity.getActivityId());
			detailDto.setOrderNo(tid);
			detailDto.setGoodsInfoId(goodsInfoId);
			detailDto.setRecordType(1);
			detailDto.setRecordTime(now);
			detailDto.setStoreId(storeId);
			detailDto.setStoreName(storeName);
			// 购买数量
			Long buyNum = tradeItemVO.getNum();
			detailDto.setGoodsNum(buyNum);

			// 订单item实付总额 = splitPrice + reduceWalletPrice
			BigDecimal itemRealPrice = getItemRealPrice(tradeItemVO);
			log.info("订单[{}]返鲸币sku[{}]itemRealPrice[{}]", tid, tradeItemVO.getSkuId(), itemRealPrice);
			// 算出此TradeItem需要赠送的鲸币总数
			BigDecimal itemTotalCoin = BigDecimal.ZERO;
			if (i + 1 == tradeVO.getTradeItems().size()) {
				itemTotalCoin = totalCoin.subtract(temp);
			}else {
				// 为了更精确ratio保留4位小数
				BigDecimal itemRealPriceRatio = itemRealPrice.divide(realGoodsPrice, 4, BigDecimal.ROUND_HALF_UP);
				itemTotalCoin = itemRealPriceRatio.multiply(totalCoin).setScale(4, BigDecimal.ROUND_HALF_UP);
			}
			log.info("订单[{}]返鲸币sku[{}]itemTotalCoin[{}]", tid, tradeItemVO.getSkuId(), itemTotalCoin);
			temp = temp.add(itemTotalCoin);
			// 算出此TradeItem单个商品需要赠送的鲸币
			BigDecimal singleCoinNum = itemTotalCoin.divide(BigDecimal.valueOf(buyNum), 4, BigDecimal.ROUND_HALF_UP);
			log.info("订单[{}]返鲸币sku[{}]singleCoinNum[{}]", tid, tradeItemVO.getSkuId(), singleCoinNum);
			detailDto.setSingleCoinNum(singleCoinNum);
			detailDto.setCoinNum(itemTotalCoin);
			detailDtoList.add(detailDto);
		}
		recordDto.setCoinNum(totalCoin);
		recordDto.setDetailList(detailDtoList);

		// 修改钱包       
		// 平台鲸币余额减少，用户鲸币余额增加
		String platformStoreId = Constant.WALLET_PLATFORM_STORE_ID;
		WalletInfoRequest req = WalletInfoRequest.builder().storeFlag(true).storeId(platformStoreId).build();
		BaseResponse<CusWalletVO> walletResp = customerWalletProvider.queryCustomerWallet(req);
		if (walletResp.getContext().getBalance().compareTo(totalCoin) >= 0) {
			String remark = Constant.SEND_ORDER_COIN_REMARK + "-" + tid;
			String customerId = tradeVO.getBuyer().getId();
			CustomerWalletGiveRequest walletGiveRequest = CustomerWalletGiveRequest.builder().customerId(customerId)
					.storeId(platformStoreId).relationOrderId(tid).tradeRemark(remark).remark(remark).balance(totalCoin)
					.opertionType(0)
					.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
					.build();
			BaseResponse<WalletRecordVO> merchantGiveUser = walletMerchantProvider.merchantGiveUser(walletGiveRequest);
			String sendNo = merchantGiveUser.getContext().getSendNo();

			// 保存赠送记录
			recordDto.setSendNo(sendNo);
			saveOrderCoinRecord(recordDto);
			log.info("订单[{}]返鲸币成功[{}]", tid, sendNo);
		} else {
			// 平台鲸币不足无法赠送鲸币
			String format = MessageFormat.format("订单[{0}]平台[{1}]鲸币[{2}]不足无法赠送鲸币[{3}]", tid, platformStoreId, walletResp.getContext().getBalance(), totalCoin);
			log.warn(format);
		}
	}

	private BigDecimal getItemRealPrice(TradeItemVO tradeItemVO) {
		BigDecimal reduceWalletPrice = BigDecimal.ZERO;
          if (CollectionUtils.isNotEmpty(tradeItemVO.getWalletSettlements())) {
        	  WalletSettlementVo walletSettlementVo = tradeItemVO.getWalletSettlements().get(0);
        	  reduceWalletPrice = walletSettlementVo.getReduceWalletPrice();
          }
          BigDecimal itemRealPrice = tradeItemVO.getSplitPrice().add(reduceWalletPrice);
          return itemRealPrice;
	}
	
	@Transactional
	@LcnTransaction
	public void saveOrderCoinRecord(CoinActivityStoreRecordDTO request) {
		CoinActivityStoreRecord record = KsBeanUtil.convert(request, CoinActivityStoreRecord.class);
		Long recordId = coinActivityStoreRecordRepository.save(record).getRecordId();

		List<CoinActivityStoreRecordDetail> details = KsBeanUtil.convert(request.getDetailList(),
				CoinActivityStoreRecordDetail.class);
		for (CoinActivityStoreRecordDetail detail : details) {
			detail.setRecordId(recordId);
		}
		coinActivityStoreRecordDetailRepository.saveAll(details);
	}
	
	
	/**
	 * 订单返鲸币收回
	 * 
	 */
	@Transactional(rollbackFor = Exception.class)
	@LcnTransaction
	public void takeBackOrderCoin(String rid, boolean needThrowException) {
		log.info("收回订单返鲸币开始,退单号：{}",  rid);
		ReturnOrderVO returnOrder = returnOrderQueryProvider.getById(ReturnOrderByIdRequest.builder().rid(rid).build()).getContext();
		String tid = returnOrder.getTid();
		// 判断当前订单是否赠送了鲸币
		CoinActivityStoreRecord storeRecord = coinActivityStoreRecordRepository.findByOrderNo(tid);
		if (storeRecord == null) {
			log.info("退单[{}]关联的订单[{}]没有参与订单返鲸币", rid, tid);
			return;
		}
		// 判断当前退单是否重复消费
		boolean exist = coinActivityStoreRecordRepository.existsByOrderNo(rid);
		if (exist) {
			log.info("退单[{}]已收回鲸币,重复消费", rid);
		}

		// 订单送鲸币记录
		LocalDateTime now = LocalDateTime.now();
		CoinActivityStoreRecordDTO recordDto = new CoinActivityStoreRecordDTO();
		recordDto.setActivityId(storeRecord.getActivityId());
		recordDto.setCustomerAccount(storeRecord.getCustomerAccount());
		recordDto.setOrderNo(rid);
		recordDto.setOrderTime(returnOrder.getCreateTime());
		recordDto.setOrderPrice(returnOrder.getReturnPrice().getTotalPrice());
		recordDto.setRecordType(2);
		recordDto.setRecordTime(now);
		recordDto.setStoreId(storeRecord.getStoreId());
		recordDto.setStoreName(storeRecord.getStoreName());

		List<CoinActivityStoreRecordDetailDTO> detailDtoList = new ArrayList<>();
		BigDecimal totalReturnCoin = BigDecimal.ZERO;
		List<ReturnItemVO> returnItems = returnOrder.getReturnItems();
		for (ReturnItemVO returnItemVO : returnItems) {
			// 订单送鲸币记录明细
			CoinActivityStoreRecordDetailDTO detailDto = new CoinActivityStoreRecordDetailDTO();
			detailDto.setDetailId(null);
			detailDto.setRecordId(null);
			detailDto.setActivityId(storeRecord.getActivityId());
			detailDto.setOrderNo(rid);
			CoinActivityStoreRecordDetail skuSendRecord = coinActivityStoreRecordDetailRepository.querySkuSendRecord(tid,
					returnItemVO.getSkuId());
			detailDto.setSingleCoinNum(skuSendRecord.getSingleCoinNum());
			detailDto.setGoodsNum(returnItemVO.getNum());
			// 此次退单sku收回的鲸币总额
			BigDecimal coinNum = detailDto.getSingleCoinNum().multiply(BigDecimal.valueOf(detailDto.getGoodsNum())).setScale(4, BigDecimal.ROUND_HALF_UP);
			log.info("订单[{}]返鲸币收回sku[{}]goodsNum[{}]singleCoinNum[{}]coinNum[{}]", rid, returnItemVO.getSkuId(),
					detailDto.getGoodsNum(), detailDto.getSingleCoinNum(), coinNum);
			detailDto.setCoinNum(coinNum);
			detailDto.setGoodsInfoId(returnItemVO.getSkuId());
			detailDto.setRecordType(2);
			detailDto.setRecordTime(now);
			detailDto.setStoreId(storeRecord.getStoreId());
			detailDto.setStoreName(storeRecord.getStoreName());
			detailDtoList.add(detailDto);
			totalReturnCoin = totalReturnCoin.add(coinNum);
		}
		totalReturnCoin = totalReturnCoin.setScale(2, BigDecimal.ROUND_HALF_UP);
		recordDto.setCoinNum(totalReturnCoin);
		log.info("订单[{}]返鲸币收回totalReturnCoin[{}]", rid, totalReturnCoin);
		recordDto.setDetailList(detailDtoList);
		
		String customerId = returnOrder.getBuyer().getId();
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(customerId).build())
                .getContext().getCusWalletVO();
        if (totalReturnCoin.compareTo(cusWalletVO.getBalance()) > 0) {
			String format = MessageFormat.format("退单[{0}]鲸币收回失败,用户余额不足,应收回[{1}],用户余额[{2}]", rid, totalReturnCoin, cusWalletVO.getBalance());
        	if (needThrowException) {
                throw new SbcRuntimeException(format);
			}
    		log.warn(format);
    		return;
        }
		
    	// 订单返鲸币收回平台鲸币余额增加，用户鲸币余额减少
		String platformStoreId = Constant.WALLET_PLATFORM_STORE_ID;
		String tradeRemark = Constant.TAKE_BACK_ORDER_COIN_REMARK + "-" + rid;
		CustomerWalletOrderByRequest orderByRequest = CustomerWalletOrderByRequest.builder()
				.customerId(customerId).storeId(platformStoreId).balance(totalReturnCoin)
				.relationOrderId(rid).tradeRemark(tradeRemark).remark(tradeRemark)
				.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
				.build();
		BaseResponse<WalletRecordVO> orderByGiveStore = customerWalletProvider.orderByGiveStore(orderByRequest);
		String sendNo = orderByGiveStore.getContext().getSendNo();

		// 保存收回记录
		recordDto.setSendNo(sendNo);
		saveOrderCoinRecord(recordDto);
		log.info("订单[{}]返鲸币收回成功[{}]", tid, sendNo);
	}
	
	public Integer queryStoreCount(String actId) {
		Integer queryStoreCount = coinActivityStoreRepository.queryStoreCount(actId);
		return queryStoreCount;
	}

    public void addActivityStore(CoinAddActiviStoreRequest request) {
        CoinActivity activity = coinActivityRepository.findById(request.getActivityId()).orElseThrow(() -> new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_NOT_EXIST));
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(BoolFlag.YES, activity.getTerminationFlag()) || now.isAfter(activity.getEndTime())) {
            throw new SbcRuntimeException(CouponErrorCode.COIN_ACTIVITY_IS_OVER);
        }

        List<Long> collect = request.getCoinActivityStore().stream().map(CoinActivityStoreDTO::getStoreId).collect(Collectors.toList());
    	boolean checkStoreIds = checkStoreIds(collect, activity.getStartTime(), activity.getEndTime());
    	if (!checkStoreIds) {
    		throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "选择的商家与其他活动关联的商家重复");
		}
    	
        request.getCoinActivityStore().forEach(activityStore -> {
        	saveCoinActivityStore(request.getActivityId(), activityStore);
        });
    }

	public String orderCoinTips() {
		// 订单返鲸币目前只有全场返
		CoinActivity activity = coinActivityRepository.queryAllStoreRunningAct();
		if (activity != null) {
			String allOrderCoinTipsTpl = configUtil.getAllOrderCoinTipsTpl();
			log.info("allOrderCoinTipsTpl[{}]", allOrderCoinTipsTpl);
			DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT1);
			String start = ofPattern.format(activity.getStartTime());
			String end = ofPattern.format(activity.getEndTime());
			BigDecimal multiply = activity.getCoinNum().multiply(BigDecimal.valueOf(1000)).setScale(1, BigDecimal.ROUND_HALF_UP);
			String chineseNumber = ChineseNumberUtil.toChineseNumber(multiply);
			String format = MessageFormat.format(allOrderCoinTipsTpl, start, end, chineseNumber);
			log.info("allOrderCoinTips[{}]", format);
			return format;
		}
		return null;
	}
	
	public List<CoinActivityStoreRecordDetail> querySendRecord(String orderNo) {
		return coinActivityStoreRecordDetailRepository.querySendRecord(orderNo);
	}
}
