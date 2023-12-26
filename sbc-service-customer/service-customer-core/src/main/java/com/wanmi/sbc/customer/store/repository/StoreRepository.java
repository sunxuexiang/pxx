package com.wanmi.sbc.customer.store.repository;

import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.store.model.entity.StoreName;
import com.wanmi.sbc.customer.store.model.root.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 店铺信息数据源
 * Created by CHENLI on 2017/11/2.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {
    @Query("select s from Store s where s.storeId <> ?1 and s.storeId in ?2 and s.assignSort = ?3 and s.delFlag = ?4")
    List<Store> findByStoreIdNotAndStoreIdInAndAssignSortAndDelFlag(Long storeId, Collection<Long> storeIds, Integer assignSort, DeleteFlag delFlag);

    @Transactional
    @Modifying
    @Query("update Store s set s.personId = ?1 where s.storeId = ?2")
    int updatePersonIdByStoreId(Integer personId, Long storeId);

    @Transactional
    @Modifying
    @Query("update Store s set s.selfManage = ?1 where s.storeId = ?2")
    int updateSelfManageByStoreId(Integer selfManage, Long storeId);

    @Transactional
    @Modifying
    @Query("update Store s set s.assignSort = ?1 where s.storeId = ?2")
    int updateAssignSortByStoreId(Integer assignSort, Long storeId);


    /**
     * 根据id查询店铺
     *
     * @param storeId
     * @param deleteFlag
     * @return
     */
    Store findByStoreIdAndDelFlag(Long storeId, DeleteFlag deleteFlag);

    @Transactional
    @Modifying
    @Query("update Store s set s.auditState = null,s.delFlag=1 where s.storeId = ?1")
    int updateAuditStateByStoreId(Long storeId);

    /**
     * 根据店铺Id和商家Id查询店铺信息
     *
     * @param storeId
     * @param companyInfoId
     * @param deleteFlag
     * @return
     */
    @Query("from Store s where s.storeId = :storeId and s.companyInfo.companyInfoId = :companyInfoId " +
            "and s.delFlag = :deleteFlag")
    Optional<Store> findByStoreIdAndCompanyInfoIdAndDelFlag(@Param("storeId") Long storeId,
                                                            @Param("companyInfoId") Long companyInfoId,
                                                            @Param("deleteFlag") DeleteFlag deleteFlag);
    @Query(value = "select t1.supplier_name,t1.store_name,t2.company_code_new,em.account_name,t3.deal_price,t3.deal_time,t3.record_no,t1.store_id from " +
            "`sbc-account`.wallet_record t3 " +
            "inner join `sbc-customer`.employee em on t3.customer_account = em.account_name  " +
            "inner join store t1 on em.company_info_id = t1.company_info_id " +
            "INNER JOIN company_info t2 ON t1.company_info_id = t2.company_info_id " +
            " where 1=1 and t3.trade_type = 0 AND ( if (:startTime !=''||null , t3.deal_time >= :startTime , 1=1)) " +
            " AND ( if (:endTime !=''||null , t3.deal_time >= :endTime , 1=1))" +
            " AND if (:storeName !=''||null , t1.store_name like concat('%', :storeName, '%') , 1=1)" +
            " AND if (:contractMobile !=''||null , em.account_name like concat('%', :contractMobile, '%') , 1=1) order by t3.deal_time desc"
            ,countQuery = "select count(*) from `sbc-account`.wallet_record t3 " +
            "inner join `sbc-customer`.employee em on t3.customer_account = em.account_name " +
            "inner join store t1 on em.company_info_id = t1.company_info_id " +
            "INNER JOIN company_info t2 ON t1.company_info_id = t2.company_info_id " +
            "where 1=1 and t3.trade_type = 0 AND ( if (:startTime !=''||null , t3.deal_time >= :startTime , 1=1)) " +
            "AND ( if (:endTime !=''||null , t3.deal_time >= :endTime , 1=1)) " +
            "AND if (:storeName !=''||null , t1.store_name like concat('%', :storeName, '%') , 1=1) " +
            "AND if (:contractMobile !=''||null , em.account_name like concat('%', :contractMobile, '%') , 1=1)"
            ,nativeQuery = true)
    Page<Object> page(@Param("storeName")String storeName, @Param("contractMobile")String contractMobile, @Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime, Pageable pageable);

    /**
     * 根据id查询店铺
     *
     * @param companyInfoId
     * @param deleteFlag
     * @return
     */
    @Query("from Store s where s.companyInfo.companyInfoId = :companyInfoId and s.delFlag = :deleteFlag")
    Store findStoreByCompanyInfoId(@Param("companyInfoId") Long companyInfoId, @Param("deleteFlag") DeleteFlag
            deleteFlag);

    /**
     * 根据店铺名称查询店铺
     *
     * @param storeName
     * @param deleteFlag
     * @return
     */
    Optional<Store> findByStoreNameAndDelFlag(String storeName, DeleteFlag deleteFlag);


    @Query("from Store s where s.delFlag = :deleteFlag and s.storeId in (:ids)")
    List<Store> queryListByIds(@Param("deleteFlag") DeleteFlag deleteFlag, @Param("ids") List<Long> ids);

    @Modifying
    @Query("update Store s set s.smallProgramCode = null ")
    void clearStoreProgramCode();

    /**
     * 根据店铺ID集合查询已过期的店铺ID集合
     *
     * @param ids
     * @return
     */
    @Query("select s.storeId from Store s where s.contractEndDate < now() and s.storeId in (:ids)")
    List<Long> findExpiredByStoreIds(@Param("ids") List<Long> ids);

    /**
     * 根据店铺id列表查询店铺名称
     */
    @Query("select new com.wanmi.sbc.customer.store.model.entity.StoreName(s.storeId, s.storeName) from Store s where" +
            " s.storeId in (?1)")
    List<StoreName> listStoreNameByStoreIds(List<Long> storeIds);

    /**
     * 根据店铺id列表查询店铺名称
     */
    @Query("select new com.wanmi.sbc.customer.bean.vo.StoreNameVO(s.storeId, s.storeName) from Store s where" +
            " s.storeId in (?1)")
    List<com.wanmi.sbc.customer.bean.vo.StoreNameVO> listStoreNameVOByStoreIds(List<Long> storeIds);


    List<Store> findAllByCompanyType(CompanyType companyType);

    /**
     * 批量修改门店的类型
     * @param companyType
     * @param storeIds
     */
    @Modifying
    @Query("update Store as s set s.companyType = :companyType where s.storeId in (:storeIds)")
    void batchUpdateCompanyType(@Param("companyType")CompanyType companyType, @Param("storeIds") List<Long> storeIds );


    /**
     * 批量修改门店的erpId
     * @param erpId
     * @param storeIds
     */
    @Modifying
    @Query("update Store as s set s.erpId = :erpId where s.storeId in (:storeIds)")
    void batchUpdateCompanyErpId(@Param("erpId")String erpId, @Param("storeIds") List<Long> storeIds );

    @Query("select s.storeId from Store s where s.delFlag = 0 and s.selfManage = 1")
    List<Long> listStoreIdsBySelfManage();
}
