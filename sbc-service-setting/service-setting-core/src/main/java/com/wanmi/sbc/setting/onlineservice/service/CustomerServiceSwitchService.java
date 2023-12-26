package com.wanmi.sbc.setting.onlineservice.service;

import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceSwitch;
import com.wanmi.sbc.setting.onlineservice.repository.CustomerServiceSwitchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 在线客服类型开关服务
 */
@Slf4j
@Service
public class CustomerServiceSwitchService {

    @Autowired
    private CustomerServiceSwitchRepository customerServiceSwitchRepository;

    public void update (CustomerServiceSwitch customerServiceSwitch) {
        customerServiceSwitchRepository.save(customerServiceSwitch);
    }

    public CustomerServiceSwitch getByCompanyId(Long companyId) {
        return customerServiceSwitchRepository.findByCompanyInfoId(companyId);
    }

    public void add(Long companyId, int serviceSwitchType) {
        CustomerServiceSwitch customerServiceSwitch = new CustomerServiceSwitch();
        customerServiceSwitch.setCompanyInfoId(companyId);
        customerServiceSwitch.setServiceSwitchType(serviceSwitchType);
        customerServiceSwitchRepository.save(customerServiceSwitch);
    }

    public CustomerServiceSwitch initStoreIMSwitch(OnlineServiceListRequest request) {
        CustomerServiceSwitch serviceSwitch = customerServiceSwitchRepository.findByCompanyInfoId(request.getCompanyInfoId());
        if (serviceSwitch == null) {
            serviceSwitch = new CustomerServiceSwitch();
            serviceSwitch.setServiceSwitchType(1);
            serviceSwitch.setCompanyInfoId(request.getCompanyInfoId());
            customerServiceSwitchRepository.save(serviceSwitch);
        }
        return serviceSwitch;
    }
}
