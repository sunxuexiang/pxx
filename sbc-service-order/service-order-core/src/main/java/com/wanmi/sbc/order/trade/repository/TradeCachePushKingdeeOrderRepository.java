package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.entity.value.TimerCachePushKingdeeOrder;
import com.wanmi.sbc.order.trade.model.root.TradeCachePushKingdeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 缓存推送金蝶销售单
 *
 * @author yitang
 * @version 1.0
 */
public interface TradeCachePushKingdeeOrderRepository extends JpaRepository<TradeCachePushKingdeeOrder,Long> {

    /**
     * 查询订单是否存在
     * @param orderId
     * @return
     */
    @Query("from TradeCachePushKingdeeOrder o where o.orderCode = ?1")
    TradeCachePushKingdeeOrder findByCachePushKingdeeOrder(String orderId);

    /**
     * 退单查询订单是否存在
     * @param orderId
     * @return
     */
    @Query("from TradeCachePushKingdeeOrder o where o.orderCode = ?1 and o.pushStatus = 0 and o.orderStatus = 0")
    TradeCachePushKingdeeOrder findByReturnOrderCachePushKingdeeOrder(String orderId);




    /**
     * 获取需要推送订单
     * @param dateTime
     * @param
     * @return
     */
    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tcache_push_kingdee_order o \n" +
            "WHERE\n" +
            "\to.create_time <=:dateTime \n" +
            "\tAND \n" +
            "\t(o.push_status = -1 AND o.erro_reson =0 AND o.flag_id !=:flagId and o.order_status = 0)\n" +
            "\tor \n" +
            "\t(o.push_status = 0 and o.order_status = 0)\n" +
            "\tLIMIT 20",nativeQuery = true)
    List<TradeCachePushKingdeeOrder> getTimerCachePushKingdeeOrder(@Param("dateTime") LocalDateTime dateTime,@Param("flagId") String flagId);


//    /**
//     * 获取需要推送订单
//     * @param dateTime
//     * @param
//     * @return
//     */
//    @Query(value = "select * from cache_push_kingdee_order o where o.create_time <=:dateTime " +
//            " and o.push_status in(0,-1)  and o.order_status = 0  order by o.push_kingdee_id  LIMIT 20",nativeQuery = true)
//    List<TradeCachePushKingdeeOrder> getTimerCachePushKingdeeOrder(@Param("dateTime") LocalDateTime dateTime);


//    /**
//     * 获取需要推送订单
//     * @param dateTime
//     * @param pushKingdeeId
//     * @return
//     */
//    @Query(value = "select * from cache_push_kingdee_order o where o.create_time <=:dateTime and o.push_kingdee_id >:pushKingdeeId" +
//            " and o.push_status = 0 and o.order_status = 0 and (o.flag_id!=:flagId or o.flag_id is null)  order by o.push_kingdee_id  LIMIT 20",nativeQuery = true)
//    List<TradeCachePushKingdeeOrder> getTimerCachePushKingdeeOrder(@Param("dateTime") LocalDateTime dateTime, @Param("pushKingdeeId") Long pushKingdeeId,@Param("flagId") String flagId);
//
    /**
     * 批量查询拦截订单
     * @param orders
     * @return
     */
    @Query(value = "select o.order_code from cache_push_kingdee_order o where o.order_code in (?1)",nativeQuery = true)
    List<String> getCachePushKingdeeOrder(List<String> orders);

    /**
     * 更新订单取消
     * @param dateTime
     * @param pushKingdeeId
     */
    @Transactional
    @Modifying
    @Query("update TradeCachePushKingdeeOrder o set o.orderStatus=1,o.updateTime=?1 where o.pushKingdeeId=?2")
    void updateCachePushKingdeeOrderStatus(LocalDateTime dateTime,Long pushKingdeeId);

    /**
     * 更新订单取消
     * @param dateTime
     * @param orderCode
     */
    @Transactional
    @Modifying
    @Query("update TradeCachePushKingdeeOrder o set o.pushStatus=1,o.updateTime=?1 where o.orderCode=?2")
    void updateTimingCachePushKingdeeOrderStatus(LocalDateTime dateTime,String orderCode);


    /**
     * 更新缓存表
     * @param dateTime
     * @param orderCode
     */
    @Transactional
    @Modifying
    @Query("update TradeCachePushKingdeeOrder o set o.pushStatus=?3,o.updateTime=?1,o.flagId=?4,o.erroReson=?5 where o.orderCode=?2")
    void updateTimingCachePushKingde(LocalDateTime dateTime,String orderCode,int pushStatus,String flagId,int erroReson);


    /**
     * 更新缓存错误次数表
     * @param orderCode
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE cache_push_kingdee_order set erro_num = erro_num+1 WHERE order_code =?1",nativeQuery = true)
    void updateCachePushKingdeNum(String orderCode);


    /**
     * 错误次数为10状态修改为-1为人工干预
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE cache_push_kingdee_order set push_status = -1 WHERE erro_num >=10 and push_status =0",nativeQuery = true)
    void updateCachePushKingdePushStatus();
}
