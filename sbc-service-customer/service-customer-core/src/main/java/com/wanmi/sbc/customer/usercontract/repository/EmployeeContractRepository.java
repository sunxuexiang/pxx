package com.wanmi.sbc.customer.usercontract.repository;

import com.wanmi.sbc.customer.storelevel.model.root.StoreLevel;
import com.wanmi.sbc.customer.usercontract.model.root.EmployeeContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hulk on 2017/4/18.
 */
@Repository
@Transactional
public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, String>, JpaSpecificationExecutor<EmployeeContract> {


    @Modifying
    @Query("update EmployeeContract ec set ec.contractUrl = :contractUrl,ec.status = :status where ec.userContractId = :id")
    void updateContractUrl(@Param("id") String id, @Param("status")int status,@Param("contractUrl") String contractUrl);

    @Transactional
    @Modifying
    @Query("delete EmployeeContract ec  where ec.employeeId = :employeeId")
    void delEmployeeContract(@Param("employeeId") String employeeId);

    EmployeeContract findByUserContractId(String userContractId);
    EmployeeContract findByAppId(String appId);

    EmployeeContract findByEmployeeId(String employeeId);

    EmployeeContract findByEmployeeName(String employeeName);

    EmployeeContract findByAppCustomerId(String appCustomerId);

    @Query("from EmployeeContract c where c.investemntManagerId=?1 and c.supplierName=?2 and c.status=3000")
    List<EmployeeContract> findByInverstmentIdAndSupplierName(String investmentId,String supplierName);

    Page<EmployeeContract> findAll(Specification<EmployeeContract> specification, Pageable pageable);


    /**
     * 模糊搜索入驻代表
     */
    @Query("from EmployeeContract c where c.investmentManager like %?1%")
    List<EmployeeContract> findAllByInvestmentManagerLike(String investmentManager);
    /**
     * 批量查询empId
     */
    List<EmployeeContract> findAllByEmployeeIdIn(List<String> ids);


}
