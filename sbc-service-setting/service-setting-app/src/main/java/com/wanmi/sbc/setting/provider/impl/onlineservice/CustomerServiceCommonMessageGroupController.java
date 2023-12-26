package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceCommonMessageGroupProvider;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageGroupRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageResponse;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessageGroup;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceCommonMessageGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>客服快捷回复常用语OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@RestController
@Validated
@Slf4j
public class CustomerServiceCommonMessageGroupController implements CustomerServiceCommonMessageGroupProvider {

    @Autowired
    private CustomerServiceCommonMessageGroupService customerServiceCommonMessageGroupService;

    @Override
    public BaseResponse addCustomerCommonMessageGroup(CustomerServiceCommonMessageGroupRequest messageRequest) {
        CustomerServiceCommonMessageGroupResponse responseBean = customerServiceCommonMessageGroupService.addCustomerCommonMessageGroup(messageRequest);
        return BaseResponse.success(responseBean.getGroupId());
    }

    @Override
    public BaseResponse updateCustomerCommonMessageGroup(CustomerServiceCommonMessageGroupRequest messageRequest) {
        boolean success = customerServiceCommonMessageGroupService.updateCustomerCommonMessageGroupById(messageRequest);
        if (!success) {
            return BaseResponse.error("修改失败");
        }
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse deleteCustomerCommonMessageGroupById(Long groupId) {
        customerServiceCommonMessageGroupService.deleteCustomerCommonMessageGroupById(groupId);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse<List<CustomerServiceCommonMessageGroupResponse>> getAllCustomerCommonMessageGroup(CustomerServiceCommonMessageGroupRequest messageRequest) {
        List<CustomerServiceCommonMessageGroupResponse> list = customerServiceCommonMessageGroupService.getAllCustomerCommonMessageGroup(messageRequest);
        return BaseResponse.success(list);
    }
}
