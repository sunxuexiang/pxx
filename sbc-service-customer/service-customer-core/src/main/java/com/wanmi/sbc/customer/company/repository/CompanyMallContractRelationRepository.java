package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:17
 **/
@Repository
public interface CompanyMallContractRelationRepository extends JpaRepository<CompanyMallContractRelation, Long>, JpaSpecificationExecutor<CompanyMallContractRelation> {

    @Modifying
    @Query("delete from CompanyMallContractRelation c where c.relationType = ?1 and c.relationValue = ?2")
    void deleteByRelationTypeAndRelationValue(Integer relationType, String relationValue);

    @Modifying
    @Query("update CompanyMallContractRelation a set a.sort =?2  where a.id = ?1")
    void updateSortById(Long id, Integer sort);

    @Query("select max(a.sort) from CompanyMallContractRelation a where a.relationType = ?1 and a.relationValue = ?2")
    Integer getMaxSortByTypeAndValue(Integer relationType, String relationValue);
}

