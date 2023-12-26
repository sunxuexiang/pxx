package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * <p>客服快捷回复常用语OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceCommonMessageProvider")
public interface CustomerServiceCommonMessageProvider {

    @PostMapping("/setting/${application.setting.version}/commonMessage/addCustomerCommonMessage")
    BaseResponse addCustomerCommonMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest);

    @PostMapping("/setting/${application.setting.version}/commonMessage/deleteCustomerCommonMessage")
    BaseResponse deleteCustomerCommonMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest);

    @PostMapping("/setting/${application.setting.version}/commonMessage/updateCustomerCommonMessage")
    BaseResponse updateCustomerCommonMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest);

    @PostMapping("/setting/${application.setting.version}/commonMessage/getCustomerCommonMessageList")
    BaseResponse<MicroServicePage<CustomerServiceCommonMessageResponse>> getCustomerCommonMessageList(@RequestBody CustomerServiceCommonMessageRequest messageRequest);

    @PostMapping("/setting/${application.setting.version}/commonMessage/searchMessage")
    BaseResponse<List<CustomerServiceCommonMessageGroupResponse>> searchMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest);
}
