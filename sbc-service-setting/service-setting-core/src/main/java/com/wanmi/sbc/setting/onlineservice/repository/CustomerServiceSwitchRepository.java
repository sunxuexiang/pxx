package com.wanmi.sbc.setting.onlineservice.repository;

import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceSwitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 自定义开关JPA操作累
 */
public interface CustomerServiceSwitchRepository extends JpaRepository<CustomerServiceSwitch, Long>, JpaSpecificationExecutor<CustomerServiceSwitch> {

    /**
     * 按公司ID查询
     * @param companyInfoId
     * @return
     */
    CustomerServiceSwitch findByCompanyInfoId(Long companyInfoId);
}
