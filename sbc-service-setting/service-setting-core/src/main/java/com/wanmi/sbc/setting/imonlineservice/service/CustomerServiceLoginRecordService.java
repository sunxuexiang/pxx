package com.wanmi.sbc.setting.imonlineservice.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceLoginRecordRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceLoginRecord;
import com.wanmi.sbc.setting.imonlineserviceitem.repository.ImOnlineServiceItemRepository;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>客服登录信息服务类</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Slf4j
@Service
public class CustomerServiceLoginRecordService {

    @Autowired
    private CustomerServiceLoginRecordRepository customerServiceLoginRecordRepository;

    @Autowired
    private ImOnlineServiceItemRepository imOnlineServiceItemRepository;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;


    public CustomerServiceLoginRecord loginSuccess(ImOnlineServiceSignRequest request) {
        List<ImOnlineServiceItem> accountList = imOnlineServiceItemRepository.findByCustomerServiceAccount(request.getCustomerServiceAccount());
        if (ObjectUtils.isEmpty(accountList)) {
            return null;
        }

        /**
        // 登录成功将客户标记为离线状态，必须客服手动切换成在线后才能接受客户消息
        accountList.forEach(item -> {
            item.setServiceStatus(1);
        });
        imOnlineServiceItemService.save(accountList);*/

        CustomerServiceLoginRecord loginRecord = KsBeanUtil.convert(request, CustomerServiceLoginRecord.class);
        loginRecord.setCompanyInfoId(accountList.get(0).getCompanyInfoId());
        loginRecord.setStoreId(accountList.get(0).getStoreId());
        loginRecord.setServerAccount(request.getCustomerServiceAccount());
        loginRecord.setLoginTime(System.currentTimeMillis() / 1000);
        customerServiceLoginRecordRepository.save(loginRecord);
        return loginRecord;
    }

}
