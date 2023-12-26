package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 优惠券活动商品配置信息Repository
 * @author: jiangxin
 * @create: 2021-09-06 15:37
 */
@Repository
public interface CouponActivityGoodsRepository extends JpaRepository<CouponActivityGoods,String>, JpaSpecificationExecutor<CouponActivityGoods> {

    /**
     * 删除活动关联所有商品信息
     * @param activityId
     * @return
     */
    int deleteByActivityId(String activityId);

    /**
     * 根据活动id获取商品skuIds
     * @param activityId
     * @return
     */
    @Query("select goodsInfoId from CouponActivityGoods where activityId = ?1")
    List<String> findGoodsInfoIdsByActivityId(String activityId);

    /**
     * 根据活动id获取关联商品信息
     * @param activityId
     * @return
     */
    List<CouponActivityGoods> findByActivityId(String activityId);

    /**
     * 根据商品skuId获取活动ids
     * @param skuId
     * @return
     */
    @Query("select activityId from CouponActivityGoods where goodsInfoId = ?1")
    List<String> getActivityIdsByGoodsInfoId(String skuId);

    /**
     * 通过优惠券活动id获取参与活动商品信息
     * @param activityIds
     * @return
     */
    @Query("from CouponActivityGoods where activityId in ?1")
    List<CouponActivityGoods> findByActivityIds(List<String> activityIds);
}
