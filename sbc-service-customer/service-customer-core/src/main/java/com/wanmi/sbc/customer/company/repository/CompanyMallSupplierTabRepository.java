package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:17
 **/
@Repository
public interface CompanyMallSupplierTabRepository extends JpaRepository<CompanyMallSupplierTab, Long>, JpaSpecificationExecutor<CompanyMallSupplierTab> {
    @Query("select c from CompanyMallSupplierTab c where c.tabName = ?1 and c.id <> ?2 and c.delFlag = ?3")
    CompanyMallSupplierTab findByTabNameAndIdNotAndDelFlag(String tabName, Long id, DeleteFlag delFlag);

    @Query("select c from CompanyMallSupplierTab c where c.tabName = ?1 and c.delFlag = 0")
    CompanyMallSupplierTab findByTabName(String tabName);
    
    @Transactional
    @Modifying
    @Query("update CompanyMallSupplierTab c set c.sort = ?1 where c.id = ?2")
    void updateSortById(BigDecimal sort, Long id);

    @Query("select max(c.sort)from CompanyMallSupplierTab c where c.delFlag = 0")
    BigDecimal getMaxSort();
}

