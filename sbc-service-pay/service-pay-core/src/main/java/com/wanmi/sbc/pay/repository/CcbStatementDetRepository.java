package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbStatementDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 建行分账明细数据层
 * @author hudong
 * 2023-09-19 15:56
 */
@Repository
public interface CcbStatementDetRepository extends JpaRepository<CcbStatementDet, Long>, JpaSpecificationExecutor<CcbStatementDet> {
    /**
     * 根据收款方商家编号查询明细
     * @param rcvprtMktMrchId
     * @return
     */
    List<CcbStatementDet> findByRcvprtMktMrchId(String rcvprtMktMrchId);

    /**
     * 根据日期统计数量
     * @param clrgDt
     * @return
     */
    Integer countCcbStatementDetByClrgDt(Date clrgDt);

}
