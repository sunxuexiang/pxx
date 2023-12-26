package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.TencentIMNotifyProvider;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceTodayMessageResponse;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceTodayMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>腾讯IM回调接口</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Slf4j
@RestController
public class TencentIMNotifyController implements TencentIMNotifyProvider {

    @Autowired
    private CustomerServiceTodayMessageService customerServiceTodayMessageService;

    @Override
    public BaseResponse<List<CustomerServiceTodayMessageResponse>> chatMsgNotify(JSONObject jsonParam) {
        List<CustomerServiceTodayMessageResponse> customerServiceTodayMessageResponse = customerServiceTodayMessageService.chatMsgNotify(jsonParam);
        return BaseResponse.success(customerServiceTodayMessageResponse);
    }
}
