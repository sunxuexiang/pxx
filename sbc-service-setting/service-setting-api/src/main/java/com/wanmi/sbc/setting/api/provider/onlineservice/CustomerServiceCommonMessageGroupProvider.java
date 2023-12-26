package com.wanmi.sbc.setting.api.provider.onlineservice;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageGroupRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>客服快捷回复常用语OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceCommonMessageGroupProvider")
public interface CustomerServiceCommonMessageGroupProvider {

    @PostMapping("/setting/${application.setting.version}/commonMessageGroup/addCustomerCommonMessageGroup")
    BaseResponse addCustomerCommonMessageGroup(@RequestBody CustomerServiceCommonMessageGroupRequest messageRequest);


    @PostMapping("/setting/${application.setting.version}/commonMessageGroup/updateCustomerCommonMessageGroup")
    BaseResponse updateCustomerCommonMessageGroup(@RequestBody CustomerServiceCommonMessageGroupRequest messageRequest);

    @PostMapping("/setting/${application.setting.version}/commonMessageGroup/deleteCustomerCommonMessageGroupById")
    BaseResponse deleteCustomerCommonMessageGroupById(@RequestBody Long groupId);

    @PostMapping("/setting/${application.setting.version}/commonMessageGroup/getAllCustomerCommonMessageGroup")
    BaseResponse<List<CustomerServiceCommonMessageGroupResponse>> getAllCustomerCommonMessageGroup(@RequestBody CustomerServiceCommonMessageGroupRequest messageRequest);
}
