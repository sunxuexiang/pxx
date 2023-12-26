package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.customer.company.model.root.CompanyMallReturnGoodsAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:17
 **/
@Repository
public interface CompanyMallReturnGoodsAddressRepository extends JpaRepository<CompanyMallReturnGoodsAddress, Long>, JpaSpecificationExecutor<CompanyMallReturnGoodsAddress> {
}

