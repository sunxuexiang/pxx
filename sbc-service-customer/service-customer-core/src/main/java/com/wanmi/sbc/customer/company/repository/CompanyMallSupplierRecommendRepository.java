package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:17
 **/
@Repository
public interface CompanyMallSupplierRecommendRepository extends JpaRepository<CompanyMallSupplierRecommend, Long>, JpaSpecificationExecutor<CompanyMallSupplierRecommend> {
    @Query("select c from CompanyMallSupplierRecommend c " +
            "where c.storeId <> ?1 and c.storeId in ?2 and c.delFlag = ?3 and c.assignSort = ?4")
    List<CompanyMallSupplierRecommend> findByStoreIdNotAndStoreIdInAndDelFlagAndAssignSort(Long storeId, Collection<Long> storeIds, DeleteFlag delFlag, Integer assignSort);




    CompanyMallSupplierRecommend findByCompanyInfoIdAndDelFlag(Long companyInfoId, DeleteFlag delFlag);


    @Query("select max(a.sort) from CompanyMallSupplierRecommend a where a.delFlag = 0")
    BigDecimal getMaxSort();

    @Modifying
    @Query("update CompanyMallSupplierRecommend a set a.sort =?2  where a.id = ?1")
    void updateSortById(Long id, BigDecimal sort);
}

