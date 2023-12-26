package com.wanmi.sbc.order.returnorder.repository;

import com.wanmi.sbc.order.returnorder.model.root.TradePushKingdeeRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * push金蝶退款记录
 *
 * @author yitang
 * @version 1.0
 */
@Repository
public interface TradePushKingdeeRefundRepository extends JpaRepository<TradePushKingdeeRefund,Long> {

    @Transactional
    @Modifying
    @Query("update TradePushKingdeeRefund r set r.pushStatus=:#{#pushKingdeeRefundOrder.pushStatus},r.instructions=:#{#pushKingdeeRefundOrder.instructions},r.updateTime=:#{#pushKingdeeRefundOrder.updateTime} " +
            "where r.refundCode=:#{#pushKingdeeRefundOrder.refundCode}")
    int updatePushKingdeeRefundOrderState(@Param("pushKingdeeRefundOrder") TradePushKingdeeRefund pushKingdeeRefundOrder);

    /**
     * 获取异常支付单
     * @param pushKingdeeRefundId
     * @param updateTime
     * @return
     */
    @Query(value = "select * from push_kingdee_refund re where re.push_kingdee_refund_id>:pushKingdeeRefundId " +
            " and re.push_status in (2,3)" +
            " and re.update_time >= :updateTime order by re.push_kingdee_refund_id limit 50",nativeQuery = true)
    List<TradePushKingdeeRefund> selectPushKingdeeRefundOrderList(@Param("pushKingdeeRefundId") Long pushKingdeeRefundId, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询销售单是否有退款单
     * @param refundCode
     * @return
     */
    @Query(value = "select count(1) from push_kingdee_refund re where re.refund_code = ?1", nativeQuery = true)
    Integer selcetPushKingdeeRefundNumber(String refundCode);

    /**
     * 退款单查询退单数据
     * @param rid
     * @return
     */
    @Query("from TradePushKingdeeRefund r where r.refundCode=?1 and r.pushStatus = 1")
    TradePushKingdeeRefund selectPushKingdeeRefundOrder(String rid);

}
