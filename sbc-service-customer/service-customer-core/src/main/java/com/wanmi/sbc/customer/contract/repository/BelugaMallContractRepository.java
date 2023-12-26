package com.wanmi.sbc.customer.contract.repository;

import com.wanmi.sbc.customer.contract.model.root.BelugaMallContract;
import com.wanmi.sbc.customer.contract.model.root.CustomerContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface BelugaMallContractRepository extends JpaRepository<BelugaMallContract, String>, JpaSpecificationExecutor<BelugaMallContract> {

    BelugaMallContract findByBelugaUser(String userId);

    BelugaMallContract findByTransactionNo(String transactionNo);

    BelugaMallContract findByPhoneNumber(String phoneNumber);

    BelugaMallContract findByUnifiedCreditCodeOfSettlement(String creditCode);

}
