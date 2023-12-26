package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <p>在线客服快捷回复常用语分组</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
public interface CustomerServiceCommonMessageGroupRepository extends JpaRepository<CustomerServiceCommonMessageGroup, Long>,
        JpaSpecificationExecutor<CustomerServiceCommonMessageGroup> {
    List<CustomerServiceCommonMessageGroup> findByCompanyInfoId(Long groupId);

    @Query(value = "select max(sort_num) from customer_service_common_message_group where company_info_id = ?1", nativeQuery = true)
    Integer getMaxSortNoByCompanyInfoId(Long companyInfoId);

    @Query(value = "select max(sort_num) from customer_service_common_message_group where parent_group_id = ?1", nativeQuery = true)
    Integer getMaxSortNoByParentGroupId(Long companyInfoId);
}
