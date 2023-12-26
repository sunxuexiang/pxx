package com.wanmi.sbc.marketing.coupon.service;

import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import com.wanmi.sbc.marketing.coupon.model.entity.cache.CouponCache;
import com.wanmi.sbc.marketing.coupon.request.CouponCacheInitRequest;
import com.wanmi.sbc.marketing.coupon.repository.CouponActivityConfigRepository;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;


/**
 * 优惠券活动配置service
 */
@Service
public class CouponActivityConfigService {

    @Autowired
    private CouponActivityConfigRepository couponActivityConfigRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * 插入活动配置信息
     */
    @Transactional
    public void insertCouponActivityConfig(CouponActivityConfig couponActivityConfig) {
        couponActivityConfigRepository.save(couponActivityConfig);
    }


    /**
     * 根据优惠券id获取活动配置信息
     *
     * @param couponIds 优惠券id
     * @return
     */
    public List<CouponActivityConfig> queryByCouponIds(List<String> couponIds) {
        return couponActivityConfigRepository.findByCouponIds(couponIds);
    }

    /**
     * 根据优惠券id获取活动配置信息
     *
     * @param couponId
     * @return
     */
    public List<CouponActivityConfig> findByCouponId(String couponId) {
        return couponActivityConfigRepository.findByCouponId(couponId);
    }

    /**
     * 根据活动id获取活动配置信息
     *
     * @param activityId 活动id
     * @return
     */
    public List<CouponActivityConfig> queryByActivityId(String activityId) {
        return couponActivityConfigRepository.findByActivityId(activityId);
    }

    /**
     * 根据活动id及关联等级id获取活动配置信息
     * @param activityId
     * @param activityLevelId
     * @return
     */
    public List<CouponActivityConfig> queryByActivityIdAndActivityLevelId(String activityId,String activityLevelId) {
        return couponActivityConfigRepository.findByActivityIdAndActivityLevelId(activityId,activityLevelId);
    }

    /**
     * 根据活动id和优惠券id，查询具体规则
     *
     * @param activityId
     * @param couponId
     * @return
     */
    public CouponActivityConfig queryByActivityIdAndCouponId(String activityId, String couponId) {
        return couponActivityConfigRepository.findByActivityIdAndCouponId(activityId, couponId);
    }

    /**
     * 根据主键更新信息
     *
     * @param couponActivityConfig
     */
    @Transactional
    public void updateByPk(CouponActivityConfig couponActivityConfig) {
        couponActivityConfigRepository.save(couponActivityConfig);
    }


    /**
     * 获取已经开始的优惠券活动信息
     *
     * @param request
     * @return
     */
    public List<CouponCache> queryCouponStarted(CouponCacheInitRequest request) {
        Query query = entityManager.createNativeQuery(request.getQuerySql());
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return CouponCacheInitRequest.converter(query.getResultList());
    }

    /**
     * 获取已经开始的优惠券活动信息and24小时内即将开始的优惠券活动信息
     * @param request
     * @return
     */
    public List<CouponCache> queryCouponStartedNew(CouponCacheInitRequest request) {
        Query query = entityManager.createNativeQuery(request.getQuerySqlNew());
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return CouponCacheInitRequest.converter(query.getResultList());
    }

}
