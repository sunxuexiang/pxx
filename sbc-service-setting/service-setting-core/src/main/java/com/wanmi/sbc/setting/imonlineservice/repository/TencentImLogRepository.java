package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.TencentImLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TencentImLogRepository extends JpaRepository<TencentImLog, Long>,
        JpaSpecificationExecutor<TencentImLog> {
}
