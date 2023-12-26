package com.wanmi.sbc.wms.erp.repository;

import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeOrder;
import com.wanmi.sbc.wms.erp.model.root.StockPushKingdeeReturnGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推金蝶退货
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface StockPushKingdeeReturnGoodsRepository extends JpaRepository<StockPushKingdeeReturnGoods,Long> {

    /**
     * 更新囤货退货记录数据
     * @param stockPushKingdeeReturnGoods
     * @return
     */
    @Transactional
    @Modifying
    @Query("update StockPushKingdeeReturnGoods g set g.pushStatus=:#{#stockPushKingdeeReturnGoods.pushStatus},g.instructions=:#{#stockPushKingdeeReturnGoods.instructions}," +
            " g.updateTime=:#{#stockPushKingdeeReturnGoods.updateTime} where g.stockReturnGoodsCode=:#{#stockPushKingdeeReturnGoods.stockReturnGoodsCode}")
    int updateStockPushKingdeeReturnGoodsOrder(@Param("stockPushKingdeeReturnGoods") StockPushKingdeeReturnGoods stockPushKingdeeReturnGoods);

    /**
     * 查询异常单
     * @param pushKingdeeId
     * @param createTime
     * @return
     */
    @Query(value = "select * from stock_push_kingdee_return_goods o where o.create_time>=:createTime " +
            " and o.push_kingdee_return_goods_id>:pushKingdeeId" +
            " and o.push_status in (2,3) order by o.push_kingdee_return_goods_id limit 50", nativeQuery = true)
    List<StockPushKingdeeReturnGoods> findStockPushKingdeeReturnGoodsOrderFailure(@Param("pushKingdeeId") Long pushKingdeeId, @Param("createTime") LocalDateTime createTime);

    /**
     * 根据订单查询订单
     * @param stockReturnGoodsCode
     * @return
     */
    @Query("from StockPushKingdeeReturnGoods g where g.stockReturnGoodsCode=?1")
    StockPushKingdeeReturnGoods findStockPushKingdeeReturnGoodsOrder(String stockReturnGoodsCode);
}
