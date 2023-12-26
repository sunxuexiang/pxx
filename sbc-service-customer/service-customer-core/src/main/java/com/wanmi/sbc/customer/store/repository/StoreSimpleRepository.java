package com.wanmi.sbc.customer.store.repository;

import com.wanmi.sbc.customer.store.model.root.StoreSimple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

// 这个只用来查找
@Repository
public interface StoreSimpleRepository extends JpaRepository<StoreSimple, Long>, JpaSpecificationExecutor<StoreSimple> {
}
