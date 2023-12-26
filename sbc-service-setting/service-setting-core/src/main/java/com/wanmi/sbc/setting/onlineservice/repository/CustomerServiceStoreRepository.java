package com.wanmi.sbc.setting.onlineservice.repository;

import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerServiceStoreRepository extends JpaRepository<CustomerServiceStore, Long>,
        JpaSpecificationExecutor<CustomerServiceStore> {

    Integer deleteByStoreId(Long storeId);

    List<CustomerServiceStore> findByStoreId(Long storeId);

    @Query(value = "select DISTINCT store_id from customer_service_store", nativeQuery = true)
    List<Long> getAllRelationStoreIds();

    List<CustomerServiceStore> findByCompanyInfoId(Long companyInfoId);
}
