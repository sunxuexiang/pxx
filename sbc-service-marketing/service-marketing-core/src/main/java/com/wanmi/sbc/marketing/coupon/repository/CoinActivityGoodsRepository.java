package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 指定商品赠金币
 * @Author : Like
 * @create 2023/5/22 9:27
 */

@Repository
public interface CoinActivityGoodsRepository extends JpaRepository<CoinActivityGoods, Long>, JpaSpecificationExecutor<CoinActivityGoods> {
    List<CoinActivityGoods> findByGoodsInfoIdIn(Collection<String> goodsInfoIds);
    long countByActivityIdAndTerminationFlag(String activityId, BoolFlag terminationFlag);
    CoinActivityGoods findByActivityIdAndId(String activityId, Long id);
    /**
     * 终止
     * @param terminationFlag
     * @param terminationTime
     * @param activityId
     * @return
     */
    @Modifying
    @Query("update CoinActivityGoods c set c.terminationFlag = ?1, c.terminationTime = ?2 where c.activityId = ?3")
    int updateTerminationFlagAndTerminationTimeByActivityId(BoolFlag terminationFlag, LocalDateTime terminationTime, String activityId);


    /**
     * 根据活动ID 查找
     * @param activityId
     * @return
     */
    List<CoinActivityGoods> findByActivityId(String activityId);

    /**
     * 根据活动ID删除
     * @param activityId
     * @return
     */
    @Modifying
    @Query("delete from CoinActivityGoods c where c.activityId = ?1")
    int deleteByActivityId(String activityId);

    /**
     * 校验sku是否重复
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "SELECT count(1) FROM coin_activity_goods cag " +
            "LEFT JOIN coin_activity ca ON cag.activity_id = ca.activity_id " +
            "WHERE ca.del_flag = 0 AND ca.termination_flag =0  AND ca.end_time > NOW() " +
            "AND cag.termination_flag =0 AND cag.goods_info_id IN (:goodsInfoIds)",nativeQuery = true)
    int checkSkuIds(@Param("goodsInfoIds") List<String> goodsInfoIds);

    /**
     * 查询商品SKUID
     * @param activityId
     * @return
     */
    @Query("select goodsInfoId from CoinActivityGoods where activityId = :activityId")
    List<String> findGoodsInfoIdsByActivityId(@Param("activityId") String activityId);
}
