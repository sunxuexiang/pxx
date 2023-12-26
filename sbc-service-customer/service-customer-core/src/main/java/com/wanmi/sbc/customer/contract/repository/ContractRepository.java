package com.wanmi.sbc.customer.contract.repository;

import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.contract.model.root.Contract;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by hulk on 2017/4/18.
 */
@Repository
@Transactional
public interface ContractRepository extends JpaRepository<Contract, String>, JpaSpecificationExecutor<Contract> {


    // 查询当前系统有效合同
    List<Contract> findByContractFlagAndIsPerson(Integer contractFlag,Integer isPerson);
    Contract findByContractName(String name);

    List<Contract> findByIsPerson(Integer isPerson);

    // 修改合同状态
    @Modifying
    @Query("UPDATE Contract c SET c.contractFlag = :contractFlag WHERE c.contractId IN :ids and c.isPerson = :isPerson")
    void updateContractFlagByIds(@Param("ids") List<Long> ids, @Param("contractFlag") Integer contractFlag,@Param("isPerson") Integer isPerson);

    // 删除合同

}
