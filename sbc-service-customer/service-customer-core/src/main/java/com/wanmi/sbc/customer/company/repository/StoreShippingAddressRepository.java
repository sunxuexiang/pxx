package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.StoreShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreShippingAddressRepository extends JpaRepository<StoreShippingAddress, Long>, JpaSpecificationExecutor<StoreShippingAddress> {
    @Query("select (count(s) > 0) from StoreShippingAddress s " +
            "where s.storeId = ?1 and s.delFlag = ?2 and s.defaultFlag = ?3 and s.id <> ?4")
    boolean existsByStoreIdAndDelFlagAndDefaultFlagAndIdNot(Long storeId, DeleteFlag delFlag, Integer defaultFlag, Long id);
}

