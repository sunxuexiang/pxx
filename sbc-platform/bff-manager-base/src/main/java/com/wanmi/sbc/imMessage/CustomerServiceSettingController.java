package com.wanmi.sbc.imMessage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceSettingProvider;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "CustomerServiceSettingController", description = "客服服务设置API")
@RestController
@RequestMapping("/serviceSetting")
@Slf4j
public class CustomerServiceSettingController {

    @Autowired
    private CustomerServiceSettingProvider customerServiceSettingProvider;

    @Autowired
    private CommonUtil commonUtil;


    @ApiOperation(value = "客服服务设置保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public BaseResponse saveCustomerServiceSetting(@RequestBody List<CustomerServiceSettingRequest> settingRequest) {
        if (ObjectUtils.isEmpty(settingRequest)) {
            return BaseResponse.error("参数错误");
        }
        for (CustomerServiceSettingRequest request : settingRequest) {
            if (request.getContent() == null || request.getSettingType() == null) {
                return BaseResponse.error("参数错误");
            }
            try {
                request.setOperatorId(commonUtil.getOperatorId());
                if (request.getCompanyInfoId() == null) {
                    request.setCompanyInfoId(commonUtil.getCompanyInfoId());
                }
                if (request.getStoreId() == null) {
                    request.setStoreId(commonUtil.getStoreId());
                }
            }
            catch (Exception e){}
            if (request.getCompanyInfoId() == null) {
                request.setCompanyInfoId(-1l);
                request.setStoreId(-1l);
            }
        }
        return customerServiceSettingProvider.saveCustomerServiceSetting(settingRequest);
    }

    @ApiOperation(value = "客服服务设置列表")
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public BaseResponse getCustomerServiceSettingList(@RequestBody CustomerServiceSettingRequest settingRequest) {
        try {
            if (settingRequest.getCompanyInfoId() == null) {
                settingRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
            }
            if (settingRequest.getStoreId() == null) {
                settingRequest.setStoreId(commonUtil.getStoreId());
            }
        }
        catch (Exception e) {}
        if (ObjectUtils.isEmpty(settingRequest.getCompanyInfoId())) {
            settingRequest.setCompanyInfoId(-1l);
            settingRequest.setStoreId(-1l);
        }
        return customerServiceSettingProvider.getCustomerServiceSettingList(settingRequest);
    }

    @GetMapping("/testCloseChat")
    public BaseResponse testCloseChat () {
        customerServiceSettingProvider.customerReplyTimeout(ImSettingTypeEnum.Close.getType());
        return BaseResponse.success("");
    }
}
