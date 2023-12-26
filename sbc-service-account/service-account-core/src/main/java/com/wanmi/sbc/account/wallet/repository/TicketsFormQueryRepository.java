package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.TicketsFormQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TicketsFormQueryRepository extends JpaRepository<TicketsFormQuery,Long>, JpaSpecificationExecutor<TicketsFormQuery> {

    @Query(value = "select IFNULL(sum(apply_price),0) as applyPrice from tickets_form where  wallet_id = :walletId and extract_status in (:extractStatus)\n" +
            "    and apply_time >= :startTime and apply_time <= :endTime" , nativeQuery = true)
    Object queryApplyPriceNumByType(@Param("walletId") String walletId, @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime, @Param("extractStatus") List<Integer> extractStatus);
}
