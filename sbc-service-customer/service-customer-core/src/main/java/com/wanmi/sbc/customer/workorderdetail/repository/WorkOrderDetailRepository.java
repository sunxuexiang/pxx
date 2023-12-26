package com.wanmi.sbc.customer.workorderdetail.repository;


import com.wanmi.sbc.customer.workorderdetail.model.root.WorkOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <p>工单明细DAO</p>
 * @author baijz
 * @date 2020-05-17 16:03:58
 */
@Repository
public interface WorkOrderDetailRepository extends JpaRepository<WorkOrderDetail, String>,
        JpaSpecificationExecutor<WorkOrderDetail> {

    Optional<List<WorkOrderDetail>> findByWorkOrderIdOrderByDealTimeAsc(String id);
//    findByWorkOrderIdOrderOrderByDealTimeDesc


    @Query(value = "SELECT a.work_order_id FROM work_order_detail a WHERE a.work_order_id in ?1 ",nativeQuery = true)
   List<Object> findAllByWorkId(List<String> workIds);

}
