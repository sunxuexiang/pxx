package com.wanmi.sbc.customer.contract.repository;

import com.wanmi.sbc.customer.contract.model.root.Contract;
import com.wanmi.sbc.customer.contract.model.root.CustomerContract;
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
public interface CustomerContractRepository extends JpaRepository<CustomerContract, String>, JpaSpecificationExecutor<CustomerContract> {

    CustomerContract findByEmployeeId(String employeeId);

    CustomerContract findByContractPhone(String employeeName);

    CustomerContract findByAppCustomerId(String customerId);
    @Query("select new CustomerContract(c.contractPhone,c.tabRelationValue,c.tabRelationName) from CustomerContract c where c.tabRelationValue in :relationIds or c.contractPhone in :phones")
    List<CustomerContract> findByTabRelationIds(@Param("relationIds") List<String> relationIds,@Param("phones")List<String>phones);

    @Transactional
    @Modifying
    @Query("delete CustomerContract ec  where ec.employeeId = :employeeId")
    void delContractInfo(@Param("employeeId") String employeeId);



}
