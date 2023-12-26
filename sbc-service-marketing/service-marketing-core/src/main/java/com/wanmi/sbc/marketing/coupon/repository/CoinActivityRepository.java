package com.wanmi.sbc.marketing.coupon.repository;

import com.wanmi.sbc.marketing.coupon.model.root.CoinActivity;
import com.wanmi.sbc.marketing.coupon.model.vo.CoinActivityQueryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * 指定商品赠金币
 *
 * @Author : Like
 * @create 2023/5/22 9:27
 */

@Repository
public interface CoinActivityRepository extends JpaRepository<CoinActivity, String>, JpaSpecificationExecutor<CoinActivity> {

    /**
     * 删除
     *
     * @param id
     * @param operatorId
     * @param now
     * @return
     */
    @Modifying
    @Query("update CoinActivity c set c.delFlag  = 1,c.delPerson = :operatorId, c.delTime = :now where c.activityId = :id")
    int updateDelFlagById(@Param("id") String id, @Param("operatorId") String operatorId, @Param("now") LocalDateTime now);

    /**
     * 终止
     *
     * @param id
     * @param operatorId
     * @param now
     * @return
     */
    @Modifying
    @Query("update CoinActivity c set c.pauseFlag  = 1,c.updatePerson = :operatorId, c.updateTime = :now where c.activityId = :id")
    int updatePauseFlagById(String id, String operatorId, LocalDateTime now);


    @Query(value = "select new com.wanmi.sbc.marketing.coupon.model.vo.CoinActivityQueryVo" +
            "(ca.activityId,ca.activityName,ca.activityType,ca.startTime,ca.endTime,ca.coinActivityFullType,ca.storeId,ca.delFlag,ca.joinLevel,ca.isOverlap,ca.coinNum,ca.terminationFlag,cag.goodsInfoId,cag.displayType) " +
            "from CoinActivityGoods cag left join CoinActivity ca on cag.activityId = ca.activityId " +
            "where cag.terminationFlag = 0 AND ca.delFlag = 0 and ca.terminationFlag = 0 and ca.startTime < now() and ca.endTime > now() " +
            "and cag.goodsInfoId in ?1")
    List<CoinActivityQueryVo> queryByGoodsInfoIds(List<String> goodsInfoIds);


    @Query(value = "select new com.wanmi.sbc.marketing.coupon.model.vo.CoinActivityQueryVo" +
            "(ca.activityId,ca.activityName,ca.activityType,ca.startTime,ca.endTime,ca.coinActivityFullType,ca.storeId,ca.delFlag,ca.joinLevel,ca.isOverlap,ca.coinNum,ca.terminationFlag,cag.goodsInfoId,cag.displayType) " +
            "from CoinActivityGoods cag left join CoinActivity ca on cag.activityId = ca.activityId " +
            "where cag.terminationFlag = 0 AND ca.delFlag = 0 and cag.displayType=0 and ca.terminationFlag = 0 and ca.startTime < now() and ca.endTime > now() " +
            "and cag.goodsInfoId in ?1")
    List<CoinActivityQueryVo> queryByGoodsInfoIdDisplayTypes(List<String> goodsInfoIds);
    
    @Query(value = "SELECT\r\n"
    		+ "	ca.* \r\n"
    		+ "FROM\r\n"
    		+ "	`coin_activity_store` cag\r\n"
    		+ "	LEFT JOIN `coin_activity` ca ON cag.activity_id = ca.activity_id \r\n"
    		+ "WHERE\r\n"
    		+ "	cag.termination_flag = 0 \r\n"
    		+ "	AND ca.del_flag = 0 \r\n"
    		+ "	AND ca.termination_flag = 0 \r\n"
    		+ "	AND ca.start_time < now() AND ca.end_time > now() \r\n"
    		+ "	AND cag.store_id=?1", nativeQuery = true)
   CoinActivity queryStoreRunningAct(Long storeId);

    @Query(value = "SELECT\r\n"
    		+ "	* \r\n"
    		+ "FROM\r\n"
    		+ "	`coin_activity` ca \r\n"
    		+ "WHERE\r\n"
    		+ "	ca.del_flag = 0 \r\n"
    		+ "	AND ca.activity_type=1\r\n"
    		+ "	AND ca.termination_flag = 0 \r\n"
    		+ "	AND ca.start_time < now() AND ca.end_time > now() \r\n"
    		+ "	LIMIT 1", nativeQuery = true)
   CoinActivity queryAllStoreRunningAct();

}
