package com.wanmi.sbc.order.trade.repository;

import com.wanmi.sbc.order.trade.model.root.TradePushKingdeePayOrder;
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
 * push金蝶支付单记录
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface TradePushKingdeePayRepository extends JpaRepository<TradePushKingdeePayOrder, Long>, JpaSpecificationExecutor<TradePushKingdeePayOrder> {

    @Transactional
    @Modifying
    @Query("update TradePushKingdeePayOrder p set p.pushStatus = :#{#pushKingdeePayOrder.pushStatus},p.instructions = :#{#pushKingdeePayOrder.instructions}," +
            " p.updateTime = :#{#pushKingdeePayOrder.updateTime} " +
            " where p.payCode = :#{#pushKingdeePayOrder.payCode}")
    int updatePushKingdeePayOrderState(@Param("pushKingdeePayOrder") TradePushKingdeePayOrder pushKingdeePayOrder);

    /**
     * 查询支付单是否存在
     * @param payCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_pay p where p.pay_code = ?1 and push_status=1",nativeQuery = true)
    Integer selectPushKingdeePayOrderNumberPushStatus(String payCode);

    /**
     * 查询支付单是否存在(异常)
     * @param payCode
     * @return
     */
    @Query(value = "select * from push_kingdee_pay p where p.pay_code = ?1 and push_status in(0,2,3)",nativeQuery = true)
    TradePushKingdeePayOrder selectPushKingdeePayOrderNumberPushStatusError(String payCode);

    /**
     * 查询销售单是否存在
     * @param payCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_pay p where p.pay_code = ?1",nativeQuery = true)
    Integer selectPushKingdeePayOrderNumber(String payCode);

    /**
     * 查询销售单收款单状态是否都成功
     * @param tid
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_pay p where p.order_code = ?1 AND push_status!=1",nativeQuery = true)
    Integer selectPushKingdeePayStutuByOrderNo(String tid);


    /**
     * 查询销售对应的支付单
     * @param payCode
     * @return
     */
    @Query("from TradePushKingdeePayOrder p where p.payCode=?1")
    TradePushKingdeePayOrder selectPushKingdeePayOrder(String payCode);

    /**
     * 获取异常支付单
     * @param pushKingdeePayId
     * @param updateTime
     * @return
     */
    @Query(value = "select * " +
            "from push_kingdee_pay p " +
            "inner join push_kingdee_order o on p.order_code=o.order_code and o.push_status=1" +
            " where p.push_kingdee_pay_id>:pushKingdeePayId and p.push_status in (0,2,3)" +
            " and p.update_time >= :updateTime order by p.push_kingdee_pay_id limit 50",nativeQuery = true)
    List<TradePushKingdeePayOrder> selectPushKingdeePayOrderList(@Param("pushKingdeePayId") Long pushKingdeePayId, @Param("updateTime") LocalDateTime updateTime);
}
