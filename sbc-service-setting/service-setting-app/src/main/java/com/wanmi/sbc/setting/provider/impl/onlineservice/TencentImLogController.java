package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.TencentImLogProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.TencentImLogRequest;
import com.wanmi.sbc.setting.imonlineservice.service.TencentImLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class TencentImLogController implements TencentImLogProvider {

    @Autowired
    private TencentImLogService tencentImLogService;


    @Override
    public BaseResponse add(TencentImLogRequest request) {
        tencentImLogService.add(request);
        return BaseResponse.success("");
    }
}
