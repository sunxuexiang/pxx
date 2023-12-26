package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbStatementSum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 建行对账单汇总数据层
 * @author hudong
 * 2023-09-04 10:56
 */
@Repository
public interface CcbStatementSumRepository extends JpaRepository<CcbStatementSum, Long>, JpaSpecificationExecutor<CcbStatementSum> {
    /**
     * 根据市场商户编号查询汇总
     * @param mktMrchId
     * @return
     */
    CcbStatementSum findByMktMrchId(String mktMrchId);

    /**
     * 根据分账日期统计数据
     * @param clrgDt
     * @return
     */
    Integer countCcbStatementSumByClrgDt(Date clrgDt);

}
