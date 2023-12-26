package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbStatementRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 建行对账单退款数据层
 * @author hudong
 * 2023-09-04 10:56
 */
@Repository
public interface CcbStatementRefundRepository extends JpaRepository<CcbStatementRefund, Long>, JpaSpecificationExecutor<CcbStatementRefund> {
    /**
     * 根据订单号查询退款单
     * @param pyOrdrNo
     * @return
     */
    CcbStatementRefund findByPyOrdrNo(String pyOrdrNo);

    /**
     * 根据日期统计数据
     * @param tfrDt
     * @return
     */
    Integer countCcbStatementRefundByTfrDt(Date tfrDt);

}
