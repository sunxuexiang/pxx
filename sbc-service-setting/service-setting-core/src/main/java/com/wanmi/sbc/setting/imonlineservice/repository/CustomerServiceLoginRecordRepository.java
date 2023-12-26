package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceLoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerServiceLoginRecordRepository extends JpaRepository<CustomerServiceLoginRecord, Long>,
        JpaSpecificationExecutor<CustomerServiceLoginRecord> {

    @Query(value = "select * from customer_service_login_record where login_id in " +
            "(select max(login_id) from customer_service_login_record where company_info_id = ?1 group by server_account)", nativeQuery = true)
    List<CustomerServiceLoginRecord> findByCompanyInfo(Long companyInfo);
}
