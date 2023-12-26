package com.wanmi.sbc.customer.workorder.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>工单DAO</p>
 * @author baijz
 * @date 2020-05-17 16:03:15
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, String>,
        JpaSpecificationExecutor<WorkOrder> {

    /**
     * 单个删除工单
     * @author baijz
     */
    @Modifying
    @Query("update WorkOrder set delFlag = 1 where workOrderId = ?1")
    void deleteById(String workOrderId);

    @Modifying
    @Query("update WorkOrder set  accountMergeStatus=1 where workOrderId = ?1")
    void updateMergeFlagById(String workOrderId);

    /**
     * 批量删除工单
     * @author baijz
     */
    @Modifying
    @Query("update WorkOrder set delFlag = 1 where workOrderId in ?1")
    void deleteByIdList(List<String> workOrderIdList);

    Optional<WorkOrder> findByWorkOrderIdAndDelFlag(String id, DeleteFlag delFlag);

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 更新status状态
     * @Return: int
     * @Author: yxb
     * @Date: 2020/5/25 16:53
     */
    @Modifying
    @Query("update WorkOrder c set c.status = 1 where c.delFlag = 0 and c.workOrderId=?1")
    int updateStatusById( String workOrderId);

    /**
     * 根据已注册人的Id查询工单数
     * @param registeredCustomerId
     * @param accountMergeStatus
     * @return
     */
    @Query("select count(1) from WorkOrder  c where c.delFlag = 0 and c.registedCustomerId =?1 and (c.accountMergeStatus = ?2 or c.status = ?3)")
    int countByRegistedCustomerIdAndStatusOrAccountMergeStatus(String registeredCustomerId, DefaultFlag accountMergeStatus, DefaultFlag status);

    /**
     * 根据注册人的Id查询工单数
     * @param approvalCustomerId
     * @param accountMergeStatus
     * @return
     */
    @Query("select count(1) from WorkOrder  c where c.delFlag = 0 and c.approvalCustomerId =?1 and (c.accountMergeStatus = ?2 or c.status = ?3)")
    int countByApprovalCustomerIdAndStatusOrAccountMergeStatus(String approvalCustomerId, DefaultFlag accountMergeStatus, DefaultFlag status);


    List<WorkOrder> findAllByAccountMergeStatusAndDelFlag(DefaultFlag accountMergeStatus,DeleteFlag delFlag);

    @Query(value = "SELECT work_order_id " +
            " FROM work_order " +
            " WHERE account_merge_status=0 AND del_flag=0 AND (approval_customer_id =?1 OR registed_customer_id=?1) limit 1" , nativeQuery = true)
    Object checkCustomerId(String customerId);

}
