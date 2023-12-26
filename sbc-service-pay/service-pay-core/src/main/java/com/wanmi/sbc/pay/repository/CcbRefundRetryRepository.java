package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.CcbRefundRetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CcbRefundRetryRepository extends JpaRepository<CcbRefundRetry, Long>, JpaSpecificationExecutor<CcbRefundRetry> {
    CcbRefundRetry findByRid(String rid);
    boolean existsByRid(String rid);

}