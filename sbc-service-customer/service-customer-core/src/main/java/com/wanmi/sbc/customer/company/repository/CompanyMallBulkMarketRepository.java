package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyMallBulkMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:17
 **/
@Repository
public interface CompanyMallBulkMarketRepository extends JpaRepository<CompanyMallBulkMarket, Long>, JpaSpecificationExecutor<CompanyMallBulkMarket> {
    @Query("select c from CompanyMallBulkMarket c where c.marketName = ?1 and c.marketId <> ?2 and c.delFlag = ?3")
    CompanyMallBulkMarket findByMarketNameAndMarketIdNotAndDelFlag(String marketName, Long marketId, DeleteFlag delFlag);

    @Query("select max(a.sort) from CompanyMallBulkMarket a where a.delFlag = 0")
    BigDecimal getMaxSort();

    @Modifying
    @Query("update CompanyMallBulkMarket a set a.sort =?2  where a.marketId = ?1")
    void updateSortByMarketId(Long marketId, BigDecimal sort);

    @Modifying
    @Query("update CompanyMallBulkMarket a set a.marketCode =?2  where a.marketId = ?1")
    void updateMarketCodeByMarketId(Long marketId, String marketCode);

    @Modifying
    @Query("update CompanyMallBulkMarket a set a.concatInfo =?2  where a.marketId = ?1")
    void updateConcatInfoByMarketId(Long marketId, String concatInfo);
}

