package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceCommonMessageProvider;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageResponse;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessage;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceCommonMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class CustomerServiceCommonMessageController implements CustomerServiceCommonMessageProvider {

    @Autowired
    private CustomerServiceCommonMessageService customerServiceCommonMessageService;

    @Override
    public BaseResponse addCustomerCommonMessage(CustomerServiceCommonMessageRequest messageRequest) {
        CustomerServiceCommonMessageResponse response = customerServiceCommonMessageService.addCustomerCommonMessage(messageRequest);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse deleteCustomerCommonMessage(CustomerServiceCommonMessageRequest messageRequest) {
        customerServiceCommonMessageService.deleteCustomerCommonMessageById(messageRequest.getMsgId());
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse updateCustomerCommonMessage(CustomerServiceCommonMessageRequest messageRequest) {
        boolean success = customerServiceCommonMessageService.updateCustomerCommonMessage(messageRequest);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse<MicroServicePage<CustomerServiceCommonMessageResponse>> getCustomerCommonMessageList(CustomerServiceCommonMessageRequest messageRequest) {
        MicroServicePage<CustomerServiceCommonMessageResponse> messagePage = customerServiceCommonMessageService.getCustomerCommonMessageList(messageRequest);
        return BaseResponse.success(messagePage);
    }

    @Override
    public BaseResponse<List<CustomerServiceCommonMessageGroupResponse>> searchMessage(CustomerServiceCommonMessageRequest messageRequest) {
        List<CustomerServiceCommonMessageGroupResponse> list = customerServiceCommonMessageService.searchMessage(messageRequest);
        return BaseResponse.success(list);
    }

}
