package com.wanmi.sbc.marketing.coupon.mongorepository;

import com.wanmi.sbc.marketing.coupon.model.entity.cache.CouponCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 优惠券缓存对应dao
 */
@Repository
public interface CouponCacheRepository extends MongoRepository<CouponCache, String>{

    /**
     * 按id删除
     * @param activityIds
     * @return
     */
    int deleteByCouponActivityIdIn(Set<String> activityIds);

    /**
     * 通过活动id获取优惠券活动缓存信息
     * @param activityId
     * @return
     */
    List<CouponCache> getCouponCacheByCouponActivityId(String activityId);

    /**
     * 通过活动id删除优惠券活动缓存信息
     * @param activityId
     * @return
     */
    int deleteByCouponActivityId(String activityId);

}
