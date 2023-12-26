package com.wanmi.sbc.returnorder.returnorder.repository;

import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrderLogistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReturnOrderLogisticsRepository extends JpaRepository<ReturnOrderLogistics, Long>, JpaSpecificationExecutor<ReturnOrderLogistics> {
    List<ReturnOrderLogistics> findByStoreIdAndCustomerIdOrderByCreateTimeDesc(Long storeId, String customerId);


}