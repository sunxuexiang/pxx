package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatMarkProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatMarkRequest;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatMarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@Slf4j
public class CustomerServiceChatMarkController implements CustomerServiceChatMarkProvider {

    @Autowired
    private CustomerServiceChatMarkService customerServiceChatMarkService;

    @Override
    public BaseResponse add(CustomerServiceChatMarkRequest request) {
        customerServiceChatMarkService.add(request);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse delete(CustomerServiceChatMarkRequest request) {
        customerServiceChatMarkService.delete(request);
        return BaseResponse.success("");
    }
}
