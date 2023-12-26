package com.wanmi.sbc.order.claims.repository;

import com.wanmi.sbc.order.claims.model.root.ClaimsApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimsApplyRepository extends JpaRepository<ClaimsApply,Long>, JpaSpecificationExecutor<ClaimsApply> {
    Optional<ClaimsApply> findByApplyNo(String applyNo);
}
