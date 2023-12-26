package com.wanmi.sbc.customer.detail.repository;


import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetailBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员详情数据源
 * Created by CHENLI on 2017/4/18.
 */
@Repository
public interface CustomerDetailRepository extends JpaRepository<CustomerDetail, String>,
        JpaSpecificationExecutor<CustomerDetail> {

    List<CustomerDetail> findByCustomerIdIn(@Param("customerIds") List<String> customerIds);

    /**
     * 根据会员详情ID查询会员详情信息
     *
     * @param customerDetailId
     * @return
     */
    @Query("from CustomerDetail c where c.delFlag = 0 and c.customerDetailId = :customerDetailId")
    CustomerDetail findByCustomerDetailId(@Param("customerDetailId") String customerDetailId);

    /**
     * 根据会员详情ID查询会员详情信息
     *
     * @param customerId
     * @return
     */
    @Query("from CustomerDetail c where c.delFlag = 0 and c.customerId = :customerId")
    CustomerDetail findByCustomerId(@Param("customerId") String customerId);

    /**
     * 根据会员详情ID查询会员详情信息
     *
     * @param customerId
     * @return
     */
    @Query("from CustomerDetail c where  c.customerId = :customerId")
    CustomerDetail findAnyByCustomerId(@Param("customerId") String customerId);

    /**
     * 根据会员详情ID查询会员详情信息
     *
     * @param customerIds
     * @return
     */
    @Query("from CustomerDetail c where  c.customerId in :customerIds")
    List<CustomerDetail> findAnyByCustomerIds(@Param("customerIds") List<String> customerIds);

    /**
     * 批量启用/禁用会员
     *
     * @param customerStatus
     * @param customerIds
     * @return
     */
    @Modifying
    @Query("update CustomerDetail c set c.customerStatus = :customerStatus, c.forbidReason = :forbidReason where c" +
            ".delFlag = 0 and c.customerId in :customerIds")
    int updateCustomerState(@Param("customerStatus") CustomerStatus customerStatus, @Param("customerIds")
            List<String> customerIds, @Param("forbidReason") String forbidReason);

    /**
     * 批量删除会员详情
     *
     * @param customerIds
     * @return
     */
    @Modifying
    @Query("update CustomerDetail c set c.delFlag = 1 where c.delFlag = 0 and c.customerId in :customerIds")
    int deleteByCustomerId(@Param("customerIds") List<String> customerIds);

    /**
     * 审核时修改rejectReason
     *
     * @param cashbackFlag
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update CustomerDetail c set c.cashbackFlag = :cashbackFlag where c.customerId = :customerId")
    int updateCashBack(@Param("cashbackFlag") Integer cashbackFlag, @Param("customerId") String customerId);

    /**
     * 审核时修改rejectReason
     *
     * @param rejectReason
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update CustomerDetail c set c.rejectReason = :rejectReason where c.customerId = :customerId")
    int updateRejectReason(@Param("rejectReason") String rejectReason, @Param("customerId") String customerId);

    /**
     * 查询业务员下所有客户id
     *
     * @param employeeId
     * @param deleteFlag
     * @return
     */
    @Query("select c.customerId from CustomerDetail c where c.employeeId = :employeeId and c.delFlag = :deleteFlag")
    List<String> queryAllCustomerIdByEmployeeId(@Param("employeeId") String employeeId, @Param("deleteFlag")
            DeleteFlag deleteFlag);

    /**
     * 查询所有客户id
     *
     * @return
     */
    @Query("select c.customerId from CustomerDetail c where c.delFlag = :deleteFlag")
    List<String> queryAllCustomerId(@Param("deleteFlag") DeleteFlag deleteFlag);

    @Query("select c.customerName from CustomerDetail c where  c.customerId = :customerId and c.delFlag = :delFlag ")
    String getCustomerNameByCustomerId(@Param("customerId") String customerId ,@Param("delFlag") DeleteFlag delFlag);

    /**
     * 根据会员id集合查询会员详情ID
     * @param customerIds
     * @return
     */
    @Query("select new com.wanmi.sbc.customer.detail.model.root.CustomerDetailBase(c.customerId,c.customerDetailId) from CustomerDetail c where c.customerId in :customerIds ")
    List<CustomerDetailBase>  listCustomerDetailBaseByCustomerIds(@Param("customerIds") List<String> customerIds);

    /**
     * 修改业务员
     * @param customerId
     * @param employeeId
     * @return
     */
    @Modifying
    @Query("update CustomerDetail set employeeId = ?2 where customerId in (?1)")
    int handoverEmployee(List<String> customerId, String employeeId);


    @Query(value = "SELECT b.customer_name,a.customer_account,b.contact_name,b.contact_phone,a.customer_register_type,b.province_id,b.city_id,b.area_id,a.customer_id,a.parent_customer_id " +
            " FROM customer a INNER JOIN customer_detail b ON a.customer_id=b.customer_id" +
            " LEFT JOIN (" +
            "  SELECT approval_customer_id " +
            "  FROM work_order" +
            "  WHERE del_flag=0 AND account_merge_status=0" +
            ") c ON a.customer_id=c.approval_customer_id " +
            " LEFT JOIN( " +
            " SELECT registed_customer_id  " +
            " FROM work_order " +
            " WHERE del_flag=0 AND account_merge_status=0 " +
            " ) d ON a.customer_id=d.registed_customer_id" +
            " WHERE (a.enterprise_status_xyy=2 or a.customer_register_type=0)  " +
            " AND a.del_flag=0 AND b.del_flag=0 AND c.approval_customer_id IS NULL AND a.customer_id !=?4 AND (a.parent_customer_id IS NULL OR a.parent_customer_id ='') " +
            " AND d.registed_customer_id IS NULL AND if(?1 =''||?1 is null,1=1,b.customer_name LIKE %?1%) AND if(?2 =''||?2 is null,1=1,b.contact_phone LIKE %?2%) " +
            " AND if(?3 =''||?3 is null,1=1,a.customer_account LIKE %?3%)"
            ,countQuery =  "SELECT count(1) " +
            " FROM customer a INNER JOIN customer_detail b ON a.customer_id=b.customer_id" +
            " LEFT JOIN (" +
            "  SELECT approval_customer_id " +
            "  FROM work_order" +
            "  WHERE del_flag=0 AND account_merge_status=0" +
            ") c ON a.customer_id=c.approval_customer_id " +
            " LEFT JOIN( " +
            " SELECT registed_customer_id  " +
            " FROM work_order " +
            " WHERE del_flag=0 AND account_merge_status=0 " +
            " ) d ON a.customer_id=d.registed_customer_id" +
            " WHERE  (a.enterprise_status_xyy=2 or a.customer_register_type=0)  " +
            " AND a.del_flag=0 AND b.del_flag=0 AND c.approval_customer_id IS NULL AND a.customer_id !=?4 AND (a.parent_customer_id IS NULL OR a.parent_customer_id ='') " +
            " AND d.registed_customer_id IS NULL AND if(?1 =''||?1 is null,1=1,b.customer_name LIKE %?1%) AND if(?2 =''||?2 is null,1=1,b.contact_phone LIKE %?2%) " +
            " AND if(?3 =''||?3 is null,1=1,a.customer_account LIKE %?3%)"
            ,nativeQuery = true)
    Page<Object> queryGroupByFullTime(String name, String phone, String account,String customerId, Pageable pageable);

    @Query(value = "SELECT a.customer_name,b.employee_name,a.customer_id " +
            "FROM customer_detail a LEFT JOIN employee b ON a.employee_id=b.employee_id " +
            "WHERE a.customer_id in ?1 ",nativeQuery = true)
    List<Object> queryStockOutCustomerInfo(List<String> customerIds);

    /**
     * 审核时修改rejectReason
     *
     * @param isLive
     * @param customerId
     * @return
     */
    @Modifying
    @Query("update CustomerDetail c set c.isLive = :isLive where c.customerId = :customerId")
    int updateIsLive(@Param("isLive") Integer isLive, @Param("customerId") String customerId);

    /**
     * 修改客户关系
     * @param customerDetailId
     * @param employeeId
     * @param managerId
     * @return
     */
    @Modifying
    @Query("update CustomerDetail c set c.employeeId = :employeeId , c.managerId = :managerId where c.customerDetailId = :customerDetailId")
    int updateCustomerRelation(@Param("customerDetailId") String customerDetailId, @Param("employeeId") String employeeId, @Param("managerId") String managerId);
}
