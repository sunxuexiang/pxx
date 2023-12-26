package com.wanmi.sbc.customer.invitationstatistics.repository;

import com.wanmi.sbc.customer.invitationstatistics.model.root.InvitationStatistics;
import com.wanmi.sbc.customer.invitationstatistics.model.root.InvitationStatisticsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/6/10
 */
@Repository
public interface InvitationStatisticsOrderRepository extends JpaRepository<InvitationStatisticsOrder, String>,
        JpaSpecificationExecutor<InvitationStatisticsOrder> {

    /**
     *
     * @param employeeId
     * @param data
     * @return
     */
    @Query(value = "select * from invitation_statistics_order ist where ist.employee_Id = ?1 and ist.date = ?2 and  ist.order_id = ?3 ",nativeQuery = true)
    Optional<InvitationStatisticsOrder> getByDate(String employeeId, String data,String orderId);


}
