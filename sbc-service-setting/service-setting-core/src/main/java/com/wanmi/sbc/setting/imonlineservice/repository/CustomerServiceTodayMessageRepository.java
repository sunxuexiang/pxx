package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceTodayMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerServiceTodayMessageRepository extends JpaRepository<CustomerServiceTodayMessage, Long>,
        JpaSpecificationExecutor<CustomerServiceTodayMessage> {
}
