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
 * 囤货销售订单
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface StockPushKingdeeOrderRepository extends JpaRepository<StockPushKingdeeOrder,Long> {

    /**
     * 更新囤货记录数据
     * @param stockPushKingdeeOrder
     * @return
     */
    @Transactional
    @Modifying
    @Query("update StockPushKingdeeOrder o set o.pushStatus=:#{#stockPushKingdeeOrder.pushStatus},o.instructions=:#{#stockPushKingdeeOrder.instructions}," +
            " o.updateTime=:#{#stockPushKingdeeOrder.updateTime} where o.stockOrderCode=:#{#stockPushKingdeeOrder.stockOrderCode}")
    int updateStockPushKingdeeOrder(@Param("stockPushKingdeeOrder") StockPushKingdeeOrder stockPushKingdeeOrder);

    /**
     * 查询异常单
     * @param pushKingdeeId
     * @param createTime
     * @return
     */
    @Query(value = "select * from stock_push_kingdee_order o where o.create_time>=:createTime " +
            " and o.push_kingdee_id>:pushKingdeeId" +
            " and o.push_status in (2,3) order by o.push_kingdee_id limit 50", nativeQuery = true)
    List<StockPushKingdeeOrder> findStockPushKingdeeOrderFailure(@Param("pushKingdeeId") Long pushKingdeeId,@Param("createTime") LocalDateTime createTime);

    /**
     * 根据订单查询订单
     * @param stockOrderCode
     * @return
     */
    @Query("from StockPushKingdeeOrder o where o.stockOrderCode=?1")
    StockPushKingdeeOrder findStockPushKingdeeOrder(String stockOrderCode);
}
