package com.wanmi.sbc.returnorder.manualrefund.repository;

import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.returnorder.manualrefund.model.root.ManualRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualRefundRepository extends JpaRepository<ManualRefund,Long>, JpaSpecificationExecutor<ManualRefund> {
    /**
     * 根据订单号查询退单列表
     * @param tid
     * @return
     */
    List<ManualRefund> findByOrderCode(String tid);

    /**
     * 根据订单号和退款单状态查询退单列表
     * @param tid
     * @param refundStatus
     * @return
     */
    List<ManualRefund> findByOrderCodeAndRefundStatus(String tid, RefundStatus refundStatus);

    @Query("from ManualRefund where refundId in(?1) and delFlag=0")
    List<ManualRefund> findListByRefundId(List<String> fundIdList);
}
