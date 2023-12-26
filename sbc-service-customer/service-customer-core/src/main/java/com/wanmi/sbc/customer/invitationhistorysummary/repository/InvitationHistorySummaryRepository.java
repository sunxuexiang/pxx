package com.wanmi.sbc.customer.invitationhistorysummary.repository;

import com.wanmi.sbc.customer.invitationhistorysummary.model.root.InvitationHistorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * <p>邀新历史汇总计表DAO</p>
 * @author fcq
 * @date 2021-04-27 11:31:55
 */
@Repository
public interface InvitationHistorySummaryRepository extends JpaRepository<InvitationHistorySummary, String>,
        JpaSpecificationExecutor<InvitationHistorySummary> {

    @Query(value = "select * from invitation_history_summary ihs where ihs.employee_Id = ?1  ",
            nativeQuery = true)
    Optional<InvitationHistorySummary> findByEmployeeId(String employeeId);
}
