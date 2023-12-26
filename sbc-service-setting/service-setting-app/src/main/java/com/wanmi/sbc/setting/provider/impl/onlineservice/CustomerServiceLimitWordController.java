package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceLimitWordProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceLimitWordRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.AllCustomerServiceLimitWordResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceLimitWordResponse;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceLimitWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Slf4j
public class CustomerServiceLimitWordController implements CustomerServiceLimitWordProvider {

    @Autowired
    private CustomerServiceLimitWordService customerServiceLimitWordService;

    @Override
    public BaseResponse add(CustomerServiceLimitWordRequest request) {
        customerServiceLimitWordService.add(request);
        return BaseResponse.success(request.getWordId());
    }

    @Override
    public BaseResponse update(CustomerServiceLimitWordRequest request) {
        customerServiceLimitWordService.update(request);
        return BaseResponse.success(request.getWordId());
    }

    @Override
    public BaseResponse delete(CustomerServiceLimitWordRequest request) {
        customerServiceLimitWordService.delete(request.getWordId());
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse getAll() {
        AllCustomerServiceLimitWordResponse allCustomerServiceLimitWordResponse = customerServiceLimitWordService.getAll();
        return BaseResponse.success(allCustomerServiceLimitWordResponse);
    }

    @Override
    public BaseResponse getByType(CustomerServiceLimitWordRequest request) {
        List<CustomerServiceLimitWordResponse> list = customerServiceLimitWordService.findByWordType(request.getWordType());
        return BaseResponse.success(list);
    }

    @Override
    public BaseResponse getRegex() {
        List<String> regexList = customerServiceLimitWordService.getAllRegex();
        return BaseResponse.success(regexList);
    }
}
