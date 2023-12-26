package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.TradePushKingdeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推送金蝶销售单记录
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface TradePushKingdeeOrderRepository extends JpaRepository<TradePushKingdeeOrder,Long>, JpaSpecificationExecutor<TradePushKingdeeOrder>{

    @Transactional
    @Modifying
    @Query("update TradePushKingdeeOrder o set o.pushStatus=:#{#pushKingdeeOrder.pushStatus},o.instructions=:#{#pushKingdeeOrder.instructions},o.updateTime=:#{#pushKingdeeOrder.updateTime} " +
            "where o.orderCode=:#{#pushKingdeeOrder.orderCode}")
    void updatePushKingdeeOrderState(@Param("pushKingdeeOrder") TradePushKingdeeOrder pushKingdeeOrder);

    /**
     * 更新订单取消
     * @param pushKingdeeOrder
     */
    @Transactional
    @Modifying
    @Query("update TradePushKingdeeOrder o set o.orderStatus=:#{#pushKingdeeOrder.orderStatus},o.cancelOperation=:#{#pushKingdeeOrder.cancelOperation}," +
            "o.updateTime=:#{#pushKingdeeOrder.updateTime} where o.orderCode=:#{#pushKingdeeOrder.orderCode}")
    void updatePushKingdeeCancelOrderState(@Param("pushKingdeeOrder") TradePushKingdeeOrder pushKingdeeOrder);

    /**
     * 查询推送金蝶异常
     * @param pushKingdeeId
     * @param updateTime
     * @return
     */
    @Query(value = "select * from push_kingdee_order o where o.update_time>=:updateTime " +
            " and o.push_kingdee_id>:pushKingdeeId" +
            " and o.push_status in (0,2,3) and o.order_status = 0 order by o.push_kingdee_id limit 50", nativeQuery = true)
    List<TradePushKingdeeOrder> selcetPushKingdeeOrder(@Param("pushKingdeeId") Long pushKingdeeId, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询推送金蝶异常
     * @param pushKingdeeId
     * @param createTime
     * @return
     */
    @Query(value = "select * from push_kingdee_order o where o.create_time>=:createTime " +
            " and o.push_kingdee_id>:pushKingdeeId" +
            " and o.push_status in (2,3) and o.order_status = 0 order by o.push_kingdee_id limit 50", nativeQuery = true)
    List<TradePushKingdeeOrder> selcetPushKingdeeOrderError(@Param("pushKingdeeId") Long pushKingdeeId, @Param("createTime") LocalDateTime createTime);

    /**
     * 查询推送金蝶数据
     * @param orderCode
     * @return
     */
    @Query("from TradePushKingdeeOrder o where o.orderCode = :orderCode")
    TradePushKingdeeOrder queryPushKingdeeOrder(@Param("orderCode") String orderCode);

    /**
     * 查询订单是否已存在
     * @param orderCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_order o where o.order_code = ?1 and o.order_status = 1",nativeQuery = true)
    Integer queryPushKingdeeOrderPushStatus(String orderCode);

    /**
     * 查找销售订单是否已存在了
     * @param orderCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_order o where o.order_code = ?1", nativeQuery = true)
    Integer selcetPushKingdeeOrderNumber(String orderCode);

    /**
     * 查询当前订单是否推送成功
     * @param orderCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_order o where o.order_code = ?1 and o.push_status = 1", nativeQuery = true)
    Integer selcetPushKingdeeOrderSuccessfulNumber(String orderCode);

}
