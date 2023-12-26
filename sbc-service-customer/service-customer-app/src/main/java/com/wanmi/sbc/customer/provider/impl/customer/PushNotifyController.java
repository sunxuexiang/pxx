package com.wanmi.sbc.customer.provider.impl.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.customer.PushNotifyProvider;
import com.wanmi.sbc.customer.api.request.customer.PushNotifyRequest;
import com.wanmi.sbc.customer.service.PushNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class PushNotifyController implements PushNotifyProvider {

    @Autowired
    private PushNotifyService pushNotifyService;

    @Override
    public BaseResponse pushNotify(PushNotifyRequest request) {
        pushNotifyService.pushNotify(request);
        return BaseResponse.success("");
    }
}
