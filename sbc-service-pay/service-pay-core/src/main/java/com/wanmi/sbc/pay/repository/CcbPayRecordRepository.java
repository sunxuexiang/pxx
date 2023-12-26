package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbPayRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface CcbPayRecordRepository extends JpaRepository<CcbPayRecord, Long>, JpaSpecificationExecutor<CcbPayRecord> {
    @Transactional
    @Modifying
    @Query("update CcbPayRecord c set c.ccbPayImg = ?1 where c.mainOrdrNo = ?2")
    int updateCcbPayImgByMainOrdrNo(String ccbPayImg, String mainOrdrNo);
    CcbPayRecord findByMainOrdrNo(String mainOrdrNo);
    @Transactional
    @Modifying
    @Query("update CcbPayRecord c set c.subAccStcd = ?1, c.updateTime = ?2 where c.pyTrnNo = ?3")
    int updateSubAccStcdAndUpdateTimeByPyTrnNo(String subAccStcd, LocalDateTime updateTime, String pyTrnNo);
    CcbPayRecord findByPyTrnNo(String pyTrnNo);

}