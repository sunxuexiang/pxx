package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>客服快捷回复常用语OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceSettingProvider")
public interface CustomerServiceSettingProvider {

    @PostMapping("/setting/${application.setting.version}/serviceSetting/saveCustomerServiceSetting")
    BaseResponse saveCustomerServiceSetting(@RequestBody List<CustomerServiceSettingRequest> settingRequest);

    @PostMapping("/setting/${application.setting.version}/serviceSetting/getCustomerServiceSettingList")
    BaseResponse getCustomerServiceSettingList(@RequestBody CustomerServiceSettingRequest settingRequest);

    @PostMapping("/setting/${application.setting.version}/serviceSetting/customerServiceTimeoutOneTask")
    BaseResponse customerServiceTimeoutOneTask(@RequestBody Integer settingType);

    @PostMapping("/setting/${application.setting.version}/serviceSetting/customerReplyTimeout")
    BaseResponse customerReplyTimeout(@RequestBody Integer type);

    @PostMapping("/setting/${application.setting.version}/serviceSetting/userTimeoutTask")
    BaseResponse userTimeoutTask(@RequestBody Integer type);
}
