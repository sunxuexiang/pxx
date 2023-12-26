package com.wanmi.sbc.setting.onlineservice.repository;

import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceStoreRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerServiceStoreRelationRepository extends JpaRepository<CustomerServiceStoreRelation, Long>,
        JpaSpecificationExecutor<CustomerServiceStoreRelation> {
    Integer deleteByParentStoreId(Long storeId);

    @Query("from CustomerServiceStoreRelation m where  m.parentStoreId in ?1 ")
    List<CustomerServiceStoreRelation> findByStoreId(List<Long> storeIds);

    List<CustomerServiceStoreRelation> findByCompanyInfoId(Long companyInfoId);

    @Query(value = "select distinct store_id from customer_service_store_relation where parent_store_id = ?1", nativeQuery = true)
    List<Long> findChildStoreIdByParentStoreId(Long storeId);
}
