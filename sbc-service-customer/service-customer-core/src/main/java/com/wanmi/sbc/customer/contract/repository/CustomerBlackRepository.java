package com.wanmi.sbc.customer.contract.repository;

import com.wanmi.sbc.customer.contract.model.root.CustomerBlack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hulk on 2017/4/18.
 */
@Repository
@Transactional
public interface CustomerBlackRepository extends JpaRepository<CustomerBlack, String>, JpaSpecificationExecutor<CustomerBlack> {

    CustomerBlack findByStoreName(String storeName);

}
