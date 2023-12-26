package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbStatementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 建行对账单明细数据层
 * @author hudong
 * 2023-09-04 10:56
 */
@Repository
public interface CcbStatementDetailRepository extends JpaRepository<CcbStatementDetail, Long>, JpaSpecificationExecutor<CcbStatementDetail> {
    /**
     * 根据市场商户编号查询明细
     * @param mktMrchId
     * @return
     */
    CcbStatementDetail findByMktMrchId(String mktMrchId);

}
