package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerServiceSettingRepository extends JpaRepository<CustomerServiceSetting, Long>,
        JpaSpecificationExecutor<CustomerServiceSetting> {
    CustomerServiceSetting findByCompanyInfoIdAndSettingType(Long companyInfoId, Integer settingType);

    List<CustomerServiceSetting> findByCompanyInfoId(Long companyInfoId);

    @Query(value = "select * from customer_service_setting where setting_type = ?1 limit ?2, ?3", nativeQuery = true)
    List<CustomerServiceSetting> findPageBySettingType(Integer settingType, int startIndex, int pageSize);
}
