package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <p>客服快捷回复常用语表</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
public interface CustomerServiceCommonMessageRepository extends JpaRepository<CustomerServiceCommonMessage, Long>,
        JpaSpecificationExecutor<CustomerServiceCommonMessage> {

    @Query(value = "select max(sort_num) from customer_service_common_message where one_group_id = ?1 and second_group_id = ?2",nativeQuery = true)
    Integer findMaxSortNoByCompanyInfoId(Long oneGroupId, Long secondGroupId);

    @Query(value = "select * from customer_service_common_message where company_info_id = ?2 and second_group_id = ?1",
            countQuery = "select count(1) from customer_service_common_message where company_info_id = ?2 and second_group_id = ?1", nativeQuery = true)
    Page<CustomerServiceCommonMessage> findBySecondGroupIdAndCompanyInfoId(Long secondGroupId, Long companyInfoId, Pageable pageable);

    @Query(value = "select * from customer_service_common_message where company_info_id = ?2 and one_group_id = ?1",
            countQuery = "select count(1) from customer_service_common_message where company_info_id = ?2 and one_group_id = ?1", nativeQuery = true)
    Page<CustomerServiceCommonMessage> findByOneGroupIdAndCompanyInfoId(Long oneGroupId, Long companyInfoId, Pageable pageable);

    @Query(value = "select * from customer_service_common_message where company_info_id = ?1",
            countQuery = "select count(1) from customer_service_common_message where company_info_id = ?1", nativeQuery = true)
    Page<CustomerServiceCommonMessage> findByCompanyInfoId(Long companyInfoId, Pageable pageable);

    @Query(value = "select * from customer_service_common_message where company_info_id = ?1 and message like '%'",
            nativeQuery = true)
    List<CustomerServiceCommonMessage> findMessageList(Long companyInfoId, String message);
}
