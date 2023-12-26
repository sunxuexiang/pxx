package com.wanmi.sbc.customer.contract.repository;

import com.wanmi.sbc.customer.contract.model.root.BelugaContractInfo;
import com.wanmi.sbc.customer.contract.model.root.BelugaMallContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hulk on 2017/4/18.
 */
@Repository
@Transactional
public interface BelugaMallInfoRepository extends JpaRepository<BelugaContractInfo, String>, JpaSpecificationExecutor<BelugaContractInfo> {


    BelugaContractInfo findByTransactionNo(String transactionNo);

    BelugaContractInfo findByCreditCode(String creditCode);
}
