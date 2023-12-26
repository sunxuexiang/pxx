package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivity;
import com.wanmi.sbc.marketing.coupon.model.vo.CouponActivityQueryVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: songhanlin
 * @Date: Created In 11:39 AM 2018/9/12
 * @Description: 优惠券活动Repository
 */
@Repository
public interface CouponActivityRepository extends JpaRepository<CouponActivity, String>,
        JpaSpecificationExecutor<CouponActivity> {

    @Modifying
    @Query("update CouponActivity a set a.pauseFlag = 0 where a.activityId = ?1")
    int startActivity(String id);

    @Modifying
    @Query("update CouponActivity a set a.pauseFlag = 1,a.updateTime = ?2 where a.pauseFlag = 0 and a.activityId = ?1")
    int pauseActivity(String id,LocalDateTime updateTime);

    @Modifying
    @Query("update CouponActivity a set a.delFlag = 1 ,a.delTime = now() ,a.delPerson = ?2 where a.activityId = ?1")
    int deleteActivity(String id, String operatorId);

    @Query("from CouponActivity where delFlag = 0 and startTime < now() and endTime > now()")
    List<CouponActivity> getLastActivity(Pageable pageable);

    @Query("from CouponActivity where delFlag = 0 and startTime < ?1 and startTime > now() and endTime > now()")
    List<CouponActivity> getFirstActivity(LocalDateTime startTime,Pageable pageable);

    /**
     * 查询指定时间内有多少活动
     * @param statTime
     * @param endTime
     * @param type
     * @param storeId
     * @return
     */
    @Query("from CouponActivity where delFlag = 0 and startTime >= ?1 and endTime <= ?2 and couponActivityType = ?3 and storeId = ?4")
    List<CouponActivity> queryActivityByTime(LocalDateTime statTime, LocalDateTime endTime, CouponActivityType type, Long storeId);

    /**
     * 查询指定时间开始时间是否交叉
     * @param statTime
     * @param endTime
     * @param type
     * @return
     */
    @Query("from CouponActivity where delFlag = 0 and startTime >= ?1 and startTime <= ?2 and couponActivityType = ?3")
    List<CouponActivity> queryActivityByStartTime(LocalDateTime statTime, LocalDateTime endTime, CouponActivityType type);


    @Query("from CouponActivity where delFlag = 0 and endTime >= ?1 and endTime <= ?2 and couponActivityType = ?3")
    List<CouponActivity> queryActivityByEndTime(LocalDateTime statTime, LocalDateTime endTime, CouponActivityType type);

    /**
     * 通过活动类型查询正在进行中的活动,并且活动优惠券组数>0
     * @param type
     * @param storeId
     * @return
     */
    @Query("from CouponActivity where delFlag = 0 and couponActivityType = ?1 and storeId = ?2 and leftGroupNum > 0 and pauseFlag = 0 and startTime < now() and endTime > now()")
    List<CouponActivity> queryGoingActivityByType(CouponActivityType type,Long storeId);

    /**
     * 通过skuId查询正在进行的活动，并且活动优惠券组数>0
     * @param type
     * @param skuIds
     * @return
     */
    @Query(value = "select DISTINCT t1.* from coupon_activity t1 \n" +
            "left join coupon_activity_goods t2 on t1.activity_id = t2.activity_id \n" +
            "where t1.start_time < now() and t1.end_time > now() \n" +
            "and t1.left_group_num > 0 and pause_flag = 0 and del_flag = 0 \n" +
            "and t1.activity_type = ?1 and t2.goods_info_id in ?2"
            ,nativeQuery = true)
    List<CouponActivity> queryGoingActivityByGoods(int type,List<String> skuIds);


    /* @Query(value = "select DISTINCT t1.*,t2.goods_info_id from coupon_activity t1 \n" +
             "left join coupon_activity_goods t2 on t1.activity_id = t2.activity_id \n" +
             "where t1.start_time < now() and t1.end_time > now() \n" +
             "and t1.left_group_num > 0 and pause_flag = 0 and del_flag = 0 \n" +
             "and t1.activity_type = ?1 and t2.goods_info_id in ?2"
             ,nativeQuery = true)*/
    @Query(value = " select new com.wanmi.sbc.marketing.coupon.model.vo.CouponActivityQueryVo(t1.activityId,t1.couponActivityFullType,t2.goodsInfoId) " +
            " from CouponActivityGoods t2 left join CouponActivity t1 on t1.activityId = t2.activityId " +
            " where t1.startTime < now() and t1.endTime > now() " +
            " and t1.leftGroupNum > 0 and t1.pauseFlag = 0 and t1.delFlag = 0 " +
            " and t1.couponActivityType = 9 and t2.goodsInfoId in ?1 ")
    List<CouponActivityQueryVo> queryGoingActivityByGoodsNew(List<String> skuIds);





    /**
     * 通过skuId查询正在进行的活动，并且活动优惠券组数>0
     * @param type
     * @param skuIds
     * @return
     */
    @Query(value = "select DISTINCT t1.* from coupon_activity t1 \n" +
            "left join coupon_activity_goods t2 on t1.activity_id = t2.activity_id \n" +
            "where t1.start_time < now() and t1.end_time > now() \n" +
            "and t1.left_group_num > 0 and pause_flag = 0 and del_flag = 0 \n" +
            "and t1.activity_type = ?1 and t2.goods_info_id in ?2 and t1.ware_id = ?3"
            ,nativeQuery = true)
    List<CouponActivity> queryGoingActivityByGoodsByWareId(int type,List<String> skuIds,Long wareId);




    /**
     * 通过活动类型查询正在进行中、未开始的活动,并且活动优惠券组数>0
     * @param type
     * @return
     */
    @Query("from CouponActivity where delFlag = 0 and couponActivityType = ?1 and storeId = ?2 and leftGroupNum > 0 and endTime >= now()")
    List<CouponActivity> querySNActivityByType(CouponActivityType type,Long storeId);

    /**
     * 查询正在进行中的订单满额赠券活动
     * 不判断优惠券组数是否>0,优惠券剩余组数在订单满额配置信息表中判断
     * @param type
     * @param storeId
     * @return
     */
    @Query("from CouponActivity where delFlag = 0 and couponActivityType = ?1 and storeId = ?2 and pauseFlag = 0 and startTime < now() and endTime > now()")
    List<CouponActivity> queryCouponActivityByType(CouponActivityType type,Long storeId);

    /**
     * 领取一组优惠券
     * @param activityId
     * @return
     */
    @Modifying
    @Query("update CouponActivity a set a.leftGroupNum = a.leftGroupNum - 1 where a.activityId = ?1 and a.leftGroupNum > 0 and a.delFlag = 0 and a.pauseFlag = 0 ")
    int getCouponGroup(String activityId);

    /**
     * 领取N组优惠券
     * @param activityId
     * @return
     */
    @Modifying
    @Query("update CouponActivity a set a.leftGroupNum = a.leftGroupNum - ?2 where a.activityId = ?1 and a.leftGroupNum > 0 and a.delFlag = 0 and a.pauseFlag = 0 ")
    int getCouponGroup(String activityId,Integer receiveNum);

    /**
     * 查询活动（注册赠券活动、进店赠券活动）不可用的时间范围
     * @param type
     * @param storeId
     * @return
     */
    @Query("from CouponActivity where delFlag =0  and couponActivityType = ?1 and storeId =?2 and endTime>= now() order by startTime ")
    List<CouponActivity> queryActivityDisableTime(CouponActivityType type , Long storeId);

    /**
     * 查询分销邀新赠券活动
     * @return
     */
    @Query("from CouponActivity where couponActivityType = 5")
    CouponActivity findDistributeCouponActivity();

    @Query("from CouponActivity where couponActivityType = ?1 and startTime <= ?2 and endTime >= ?2 and delFlag = 0 ")
    List<CouponActivity> getCouponActivityByActivityType(CouponActivityType activityType, LocalDateTime localDateTime);

    /**
     * 通过活动类型查询正在进行的活动
     * @param activityType
     * @param localDateTime
     * @return
     */
    @Query("from CouponActivity where couponActivityType = ?1 and startTime <= ?2 and endTime >= ?2 and delFlag = 0 and pauseFlag = 0")
    List<CouponActivity> getRegisteredCouponActivity(CouponActivityType activityType,LocalDateTime localDateTime);
}
