package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.bean.enums.CcbSubOrderType;
import com.wanmi.sbc.pay.model.root.CcbPayOrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CcbPayOrderRecordRepository extends JpaRepository<CcbPayOrderRecord, Long>, JpaSpecificationExecutor<CcbPayOrderRecord> {
    CcbPayOrderRecord findByBusinessIdAndMainOrderNoAndCommissionFlag(String businessId, String mainOrderNo, CcbSubOrderType commissionFlag);
    CcbPayOrderRecord findByBusinessIdAndPyTrnNoAndCommissionFlag(String businessId, String pyTrnNo, CcbSubOrderType commissionFlag);
    CcbPayOrderRecord findByPyTrnNoAndCommissionFlag(String pyTrnNo, CcbSubOrderType commissionFlag);
    @Transactional
    @Modifying
    @Query("update CcbPayOrderRecord c set c.clrgStcd = :clrgStcd, c.clrgDt = :clrgDt, c.updateTime = :updateTime " +
            "where c.subOrdrId = :subOrdrId")
    int updateClrgStcdAndClrgDtAndUpdateTimeBySubOrdrId(@Param("clrgStcd") String clrgStcd, @Param("clrgDt") String clrgDt, @Param("updateTime") LocalDateTime updateTime, @Param("subOrdrId") String subOrdrId);
    List<CcbPayOrderRecord> findByBusinessIdAndPyTrnNo(String businessId, String pyTrnNo);
    List<CcbPayOrderRecord> findByPyTrnNo(String pyTrnNo);

}