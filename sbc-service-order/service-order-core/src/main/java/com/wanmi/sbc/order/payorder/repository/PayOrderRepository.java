package com.wanmi.sbc.order.payorder.repository;


import com.wanmi.sbc.account.bean.enums.PayOrderStatus;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.order.payorder.model.root.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 支付单
 * Created by zhangjin on 2017/4/20.
 */
@Repository
public interface PayOrderRepository extends JpaRepository<PayOrder, String>, JpaSpecificationExecutor<PayOrder> {

    /**
     * 根据payOrderId列表查询
     *
     * @param payOrderIds    payOrderIds
     * @return rows
     */
    @Query("from PayOrder p where p.payOrderId in :payOrderIds")
    List<PayOrder> findByPayOrderIds(@Param("payOrderIds") List<String> payOrderIds);

    /**
     * 修改支付单状态
     *
     * @param payOrderIds    payOrderIds
     * @param payOrderStatus payOrderStatus
     * @return rows
     */
    @Query("update PayOrder p set p.payOrderStatus = :payOrderStatus where p.payOrderId in :payOrderIds")
    @Modifying
    int updatePayOrderStatus(@Param("payOrderIds") List<String> payOrderIds, @Param("payOrderStatus") PayOrderStatus
            payOrderStatus);

    /**
     * 修改支付单状态
     *
     * @param payOrderId    payOrderId
     * @param payOrderStatus payOrderStatus
     * @return rows
     */
    @Query("update PayOrder p set p.payOrderStatus = :payOrderStatus,p.payOrderRealPayPrice = :amountRealPay where p.payOrderId = :payOrderId")
    @Modifying
    int updatePayOrderStatusAmountRealPay(@Param("payOrderId") String payOrderId, @Param("payOrderStatus") PayOrderStatus
            payOrderStatus,@Param("amountRealPay") BigDecimal amountRealPay);

    /**
     * 根据定单编号查询支付单
     *
     * @param orderCode orderCode
     * @param delFlag   delFlag
     * @return Optional<payorder>
     */
    Optional<PayOrder> findByOrderCodeAndDelFlag(String orderCode, DeleteFlag delFlag);

    /**
     * 通过订单编号列表查询支付单
     * @param orderNos
     * @return
     */
    @Query("from PayOrder p where p.orderCode in :orderNos and p.delFlag = :delFlag")
    List<PayOrder> findByOrderNosAndDelFlag(@Param("orderNos") List<String> orderNos, @Param("delFlag") DeleteFlag delFlag);

    /**
     * 通过订单编号列表查询支付单
     * @param orderNos
     * @return
     */
    @Query("from PayOrder p where p.orderCode in :orderNos and p.payOrderStatus = :payOrderStatus")
    List<PayOrder> findByOrderNos(@Param("orderNos") List<String> orderNos, @Param("payOrderStatus") PayOrderStatus
            payOrderStatus);

    @Query("update PayOrder p set p.delFlag = 1 where p.payOrderId = :payOrderId")
    @Modifying
    int deletePayOrderById(@Param("payOrderId") String payOrderId);

    /**
     * 统计付款单金额
     * @return BigDecimal
     */
    @Query("select sum(p.payOrderPrice) from PayOrder p where p.payOrderStatus = 0")
    BigDecimal sumPayOrderPrice();


    /**
     * 通过支付编号查询支付单
     * @param payOrderNo
     * @return
     */
    @Query(value = "select * from pay_order where pay_order_no = ?1 and del_flag = 0" , nativeQuery = true)
    PayOrder findByPayOrderNo(String payOrderNo);

    /**
     * 通过支付编号修改支付单
     * @param payOrderNo
     * @return
     */
    @Query(value = "update pay_order set pay_order_no = ?1 where pay_order_id = ?2 " , nativeQuery = true)
    @Modifying
    int updateByPayOrderNo(String payOrderNo, String payOrderId);

    @Modifying
    @Transactional
    @Query(value = "insert into pay_order (pay_order_id, pay_order_no, order_code, pay_order_status, pay_type, customer_detail_id, create_time, update_time, del_time, del_flag, pay_order_price, company_info_id, pay_order_points, pay_order_real_pay_price) " +
            "values (:payOrderId, :payOrderNo, :orderCode, :payOrderStatus, :payType, :customerDetailId, :createTime, :updateTime, :delTime, :delFlag, :payOrderPrice, :companyInfoId, :payOrderPoints, :payOrderRealPayPrice)", nativeQuery = true)
    void insertPayOrder(@Param("payOrderId") String payOrderId, @Param("payOrderNo") String payOrderNo, @Param("orderCode") String orderCode, @Param("payOrderStatus") Integer payOrderStatus, @Param("payType") Integer payType, @Param("customerDetailId") String customerDetailId,
                        @Param("createTime") LocalDateTime createTime, @Param("updateTime") LocalDateTime updateTime, @Param("delTime") LocalDateTime delTime, @Param("delFlag") Integer delFlag, @Param("payOrderPrice") BigDecimal payOrderPrice, @Param("companyInfoId") Long companyInfoId,
                        @Param("payOrderPoints") Long payOrderPoints, @Param("payOrderRealPayPrice") BigDecimal payOrderRealPayPrice);
}
