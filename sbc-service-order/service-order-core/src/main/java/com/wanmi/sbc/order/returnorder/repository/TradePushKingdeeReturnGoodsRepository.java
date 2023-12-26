package com.wanmi.sbc.order.returnorder.repository;

import com.wanmi.sbc.order.returnorder.model.root.TradePushKingdeeReturnGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * push金蝶退货单记录
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface TradePushKingdeeReturnGoodsRepository extends JpaRepository<TradePushKingdeeReturnGoods,Long> {
    /**
     * 更新
     * @param pushKingdeeReturnGoods
     * @return
     */
    @Transactional
    @Modifying
    @Query("update TradePushKingdeeReturnGoods g set g.pushStatus=:#{#pushKingdeeReturnGoods.pushStatus},g.updateTime=:#{#pushKingdeeReturnGoods.updateTime} " +
            "where g.returnGoodsCode=:#{#pushKingdeeReturnGoods.returnGoodsCode}")
    int updatePushKingdeeReturnGoodsState(@Param("pushKingdeeReturnGoods") TradePushKingdeeReturnGoods pushKingdeeReturnGoods);

    /**
     * 销售单是否有对应的退货单
     * @param returnGoodsCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_return_goods g where g.return_goods_code = ?1", nativeQuery = true)
    Integer selcetPushKingdeeReturnGoodsNumber(String returnGoodsCode);

    /**
     * 查询退货单异常数据
     * @param pushKingdeeReturnGoodsId
     * @param updateTime
     * @return
     */
    @Query(value = "select * from push_kingdee_return_goods g where g.push_kingdee_return_goods_id > :pushKingdeeReturnGoodsId " +
            " and g.push_status in (2,3)" +
            " and g.update_time >= :updateTime order by g.push_kingdee_return_goods_id limit 50",nativeQuery = true)
    List<TradePushKingdeeReturnGoods> selectPushKingdeeRefundOrderList(@Param("pushKingdeeReturnGoodsId") Long pushKingdeeReturnGoodsId, @Param("updateTime") LocalDateTime updateTime);
}
