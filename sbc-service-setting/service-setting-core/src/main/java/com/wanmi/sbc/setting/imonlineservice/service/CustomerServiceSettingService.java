package com.wanmi.sbc.setting.imonlineservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingContentVo;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceSettingResponse;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceSettingRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>客服服务设置服务类</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Slf4j
@Service
public class CustomerServiceSettingService {

    @Autowired
    private CustomerServiceSettingRepository customerServiceSettingRepository;

    public List<CustomerServiceSetting> saveCustomerServiceSetting(List<CustomerServiceSettingRequest> settingRequest) {
        List<CustomerServiceSetting> dbSettingList = customerServiceSettingRepository.findByCompanyInfoId(settingRequest.get(0).getCompanyInfoId());
        settingRequest.forEach(setting -> {
            CustomerServiceSetting dbSetting = null;
            for (CustomerServiceSetting tmpSetting : dbSettingList) {
                if (tmpSetting.getSettingType().equals(setting.getSettingType())) {
                    dbSetting = tmpSetting;
                    break;
                }
            }
            if (dbSetting == null) {
                dbSetting = KsBeanUtil.convert(setting, CustomerServiceSetting.class);
                dbSettingList.add(dbSetting);
                log.info("dbSetting is null, add new");
            }
            dbSetting.setContentJson(JSON.toJSONString(setting.getContent()));
            dbSetting.setOperatorId(setting.getOperatorId());
        });
        customerServiceSettingRepository.saveAll(dbSettingList);
        return dbSettingList;


//        CustomerServiceSetting customerServiceSetting = customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(settingRequest.getCompanyInfoId(), settingRequest.getSettingType());
//        if (customerServiceSetting == null) {
//            customerServiceSetting = KsBeanUtil.convert(settingRequest, CustomerServiceSetting.class);
//
//        }
//        customerServiceSetting.setContentJson(JSON.toJSONString(settingRequest.getContent()));
//        customerServiceSetting.setOperatorId(settingRequest.getOperatorId());
//        customerServiceSettingRepository.save(customerServiceSetting);
//        return customerServiceSetting;
    }

    public List<CustomerServiceSettingResponse> getCustomerServiceSettingList(CustomerServiceSettingRequest settingRequest) {
        if (settingRequest.getSettingType() != null) {
            CustomerServiceSetting customerServiceSetting = customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(settingRequest.getCompanyInfoId(), settingRequest.getSettingType());
            List<CustomerServiceSettingResponse> resultList = new ArrayList<>();
            if (customerServiceSetting == null) {
                if (ImSettingTypeEnum.LIMIT.getType().equals(settingRequest.getSettingType())) {
                    customerServiceSetting = new CustomerServiceSetting();
                    customerServiceSetting.setSettingType(ImSettingTypeEnum.LIMIT.getType());
                    customerServiceSetting.setCompanyInfoId(settingRequest.getCompanyInfoId());
                    customerServiceSetting.setStoreId(settingRequest.getStoreId());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("quantity", 15);
                    customerServiceSetting.setContentJson(jsonObject.toJSONString());
                }
                else {
                    return resultList;
                }
            }
            CustomerServiceSettingResponse response = KsBeanUtil.convert(customerServiceSetting, CustomerServiceSettingResponse.class);
            response.setContent(JSON.parseObject(customerServiceSetting.getContentJson()));
            resultList.add(response);
            return resultList;
        }
        else {
            List<CustomerServiceSetting> list = customerServiceSettingRepository.findByCompanyInfoId(settingRequest.getCompanyInfoId());
            List<CustomerServiceSettingResponse> resultList = new ArrayList<>();
            list.forEach(setting -> {
                CustomerServiceSettingResponse response = KsBeanUtil.convert(setting, CustomerServiceSettingResponse.class);
                response.setContent(JSON.parseObject(setting.getContentJson()));
                resultList.add(response);
            });
            return resultList;
        }
    }

    public List<CustomerServiceSetting> getCustomerServiceSettingListByPage(Integer settingType, int startIndex, int pageSize) {
        return customerServiceSettingRepository.findPageBySettingType(settingType, startIndex, pageSize);
    }

    public CustomerServiceSetting getByCompanyInfoIdAndSettingType(long companyInfoId, Integer settingType) {
        return customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(companyInfoId, settingType);
    }

    public Integer getCompanyAcceptQuantity(Long companyId) {
        Integer defaultQuantity = 15;
        CustomerServiceSetting customerServiceSetting = customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(companyId, ImSettingTypeEnum.LIMIT.getType());
        if (customerServiceSetting == null) {
            return defaultQuantity;
        }
        try {
            CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
            if (settingContent != null && settingContent.getQuantity() != null && settingContent.getQuantity() > 0) {
                return settingContent.getQuantity();
            }
        }
        catch (Exception e) {
            log.error("解析商家接待客服数量上限异常 "+companyId, e);
        }
        return defaultQuantity;
    }
}
