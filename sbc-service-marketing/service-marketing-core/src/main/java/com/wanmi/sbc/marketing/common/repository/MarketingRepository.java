package com.wanmi.sbc.marketing.common.repository;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.entity.FullReductionActivities;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 营销规则
 */
@Repository
public interface MarketingRepository extends JpaRepository<Marketing, Long>, JpaSpecificationExecutor<Marketing> {

    /**
     * 获取类型重复的marketingIds
     * @param
     * @param
     * @return
     */
    @Query("select s.goodsMarketingId from Marketing m left join m.marketingSuitDetialList s " +
            "where m.delFlag = 0 and m.terminationFlag = 0  and m.storeId = :storeId and s.goodsMarketingId in :goodsMarketIds " +
            "and not(m.beginTime > :endTime or m.endTime < :startTime) AND m.endTime > now() and (:excludeId is null or m.marketingId <> :excludeId) " +
            "and  m.subType = 9 ")
    List<Long> getExistsMarketingByMarketingType(@Param("goodsMarketIds") List<Long> goodsMarketIds
            , @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("storeId") Long storeId
            , @Param("excludeId") Long excludeId);

    /**
     * 获取类型重复的skuIds
     * @param skuIds
     * @param
     * @return
     */
    @Query("select distinct s.scopeId from Marketing m left join m.marketingScopeList s " +
            "where m.delFlag = 0 and m.isDraft = 0 and m.terminationFlag = 0 and s.terminationFlag = 0  and m.marketingType = :marketingType and m.storeId = :storeId and (s.scopeId in :skuIds or s.scopeId='all') " +
            "and not(m.beginTime > :endTime or m.endTime < :startTime) AND m.endTime > now() and (:excludeId is null or m.marketingId <> :excludeId) " +
            "and  m.subType not in :subTypes ")
    List<String> getExistsSkuByMarketingType(@Param("skuIds") List<String> skuIds, @Param("marketingType") MarketingType marketingType
            , @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("storeId") Long storeId
            , @Param("excludeId") Long excludeId, @Param("subTypes") List<MarketingSubType> subTypes);

    /**
     *
     * 全订单满赠获取类型重复的skuIds,去掉终止的数据
     * @param
     * @return
     */
    @Query("select s.scopeId,m.isPause,m.beginTime,m.endTime,m.marketingType from Marketing m left join m.marketingScopeList s " +
            "where m.delFlag = 0 and m.marketingType = :marketingType and (m.terminationFlag is null or m.terminationFlag = 0) and m.storeId = " +
            ":storeId  " +
            "and not(m.beginTime > :endTime or m.endTime < :startTime) AND m.endTime > now() and (:excludeId is null or m.marketingId <> :excludeId)")
    List<Tuple> getExistsSkuByMarketingTypeOrderFull(@Param("marketingType") MarketingType marketingType
            , @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("storeId") Long storeId
            , @Param("excludeId") Long excludeId);

    @Query("select s.scopeId,m.isPause,m.beginTime,m.endTime,m.marketingType from Marketing m left join m.marketingScopeList s " +
            "where m.delFlag = 0  and (m.terminationFlag is null or m.terminationFlag = 0) and m.subType in :subTypes" +
            " and m.storeId = :storeId" +
            "  " +
            "and not(m.beginTime > :endTime or m.endTime < :startTime) AND m.endTime > now() and (:excludeId is null or m.marketingId <> :excludeId)")
    List<Tuple> getExistsSkuByMarketingTypeOrder(@Param("subTypes") List<MarketingSubType> subTypes
            , @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("storeId") Long storeId
            , @Param("excludeId") Long excludeId);

    /**
     * 查询出有必选商品的活动
     * @param marketingIds
     * @return
     */
    @Query("select m from Marketing m where m.marketingId in (:marketingIds) AND m.whetherChoice = 1")
    List<Marketing> getMarketingList(@Param("marketingIds") List<Long> marketingIds);

    /**
     * 删除活动
     * @param marketingId
     * @return
     */
    @Modifying
    @Query("update Marketing set delFlag = 1 where marketingId = :marketingId")
    int deleteMarketing(@Param("marketingId") Long marketingId);


    /**
     * 暂停活动
     * @param marketingId
     * @return
     */
    @Modifying
    @Query("update Marketing set isPause = :isPause where marketingId = :marketingId")
    int pauseOrStartMarketing(@Param("marketingId") Long marketingId, @Param("isPause") BoolFlag isPause);

    /**
     * 获取验证进行中的营销
     * @param marketingIds
     * @return
     */
    @Query("select t.marketingId from Marketing t " +
            "where t.delFlag = 0 AND now() >= t.beginTime AND now() <= t.endTime AND t.isPause = 0 AND t.marketingId in (:marketingIds)")
    List<String> queryStartingMarketing(@Param("marketingIds") List<Long> marketingIds);

    /**
     * 根据商铺Id和促销类型获取促销集合（配合全部商品选项，但是目前不考虑全部商品选项）
     * @param storeId
     * @param scopeType
     * @return
     */
    List<Marketing> findByStoreIdAndScopeType(Long storeId, MarketingScopeType scopeType);

    /**
     *
     * @return
     */
    @Query("select t from Marketing t " +
            "where t.delFlag = 0 AND now() >= t.beginTime AND now() <= t.endTime AND t.isPause = 0")
    List<Marketing> findAllGoingMarketing();

    /**
     *
     * @return
     */
    @Query("select m from Marketing m left join m.marketingScopeList s " +
            "where m.delFlag = 0 and (s.scopeId in :skuIds or m.subType = :subType) " +
            "AND now() >= m.beginTime AND now() <= m.endTime AND m.isPause = 0")
    List<Marketing> findAllGoingMarketingAndSubTypeOrScopeId(@Param("subType") MarketingSubType subType,@Param("skuIds") List<String> skuIds);


    @Query(value = "SELECT t.marketing_id,t.marketing_name,t.marketing_type,t.sub_type,t.begin_time,t.end_time,t.join_level,t.is_boss,t.store_id,t.is_overlap,t.scope_type,IF\n" +
            "\t( t.is_add_marketing_name != NULL or t.is_add_marketing_name != '', 1, 0 ) AS is_add_marketing_name,t.whether_choice,t.termination_flag,t.suit_coupon_desc, \n" +
            " t.update_time, t.update_person FROM marketing t\n" +
            "where t.marketing_id in (:marketingIds) AND t.del_flag = 0 AND now() >= t.begin_time AND now() <= t.end_time AND t.is_pause = 0\n",nativeQuery = true)
    List<Object> getMarketingByGoodsInfoIds(@Param("marketingIds") List<Long> marketingIds);

    @Query(value = "SELECT t.marketing_id,t.marketing_name,t.marketing_type,t.sub_type,t.begin_time,t.end_time,t.join_level,t.is_boss,t.store_id,t.is_overlap,t.scope_type,IF\n" +
            "\t( t.is_add_marketing_name != NULL or t.is_add_marketing_name != '', 1, 0 ) AS is_add_marketing_name,t.whether_choice,t.termination_flag,t.suit_coupon_desc, \n" +
            " t.update_time, t.update_person FROM marketing t\n" +
            "where t.marketing_id in (:marketingIds) AND t.del_flag = 0 AND t.begin_time >= :startTime  AND t.begin_time <= :endTime AND t.is_pause = 0\n",nativeQuery = true)
    List<Object> getMarketingByGoodsInfoIdsTime(@Param("marketingIds") List<Long> marketingIds, @Param("startTime") String startTime
            , @Param("endTime") String endTime);

    /**
     * 查询营销有效商品数大于1的营销ID
     */
    @Query(value = "SELECT\n" +
            "\tt2.marketing_id \n" +
            "FROM\n" +
            "\tmarketing t1\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tt.marketing_id,\n" +
            "\t\tSUM( IF ( ms.scope_id = 'all', 100, 1 ) ) AS goods_info_sum \n" +
            "\tFROM\n" +
            "\t\tmarketing t\n" +
            "\t\tLEFT JOIN marketing_scope ms ON t.marketing_id = ms.marketing_id \n" +
            "\tWHERE\n" +
            "\t\tt.marketing_id IN ( :marketingIds ) \n" +
            "\t\tAND t.del_flag = 0 \n" +
            "\t\tAND now( ) >= t.begin_time \n" +
            "\t\tAND now( ) <= t.end_time \n" +
            "\t\tAND t.is_pause = 0 \n" +
            "\tGROUP BY\n" +
            "\t\tt.marketing_id \n" +
            "\t) t2 ON t1.marketing_id = t2.marketing_id \n" +
            "WHERE\n" +
            "\tt1.marketing_id IN ( :marketingIds ) \n" +
            "\tAND t1.del_flag = 0 \n" +
            "\tAND now( ) >= t1.begin_time \n" +
            "\tAND now( ) <= t1.end_time \n" +
            "\tAND t1.is_pause = 0 AND t2.goods_info_sum > 1 ", nativeQuery = true)
    List<String> getMarketingByGoodsInfoIdsAndNum(@Param("marketingIds") List<Long> marketingIds);

    Marketing findByMarketingId(Long marketingId);

    @Query(value = "SELECT store_id FROM `marketing` WHERE "
    		+ "NOW() BETWEEN begin_time AND end_time "
    		+ "AND is_pause=0 AND del_flag=0 AND is_draft=0 "
    		+ "GROUP BY store_id", nativeQuery = true)
	List<Long> getActiveMarketingStoreIds();


    @Query(value = "select distinct (b.scope_id)\n" +
            "from `sbc-marketing`.marketing as a\n" +
            "         LEFT JOIN `sbc-marketing`.marketing_scope as b on a.marketing_id = b.marketing_id\n" +
            "where a.del_flag = 0\n" +
            "  and a.is_pause = 0\n" +
            "  and is_draft = 0\n" +
            "  and now() between a.begin_time and a.end_time\n" +
            "  and b.termination_flag = 0  limit 200", nativeQuery = true)
    List<String> listEffectiveStoreGoodsInfoIds();
}
