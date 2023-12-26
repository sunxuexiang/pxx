package com.wanmi.sbc.setting.provider.impl;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.OperationLogProvider;
import com.wanmi.sbc.setting.api.request.OperationLogAddRequest;
import com.wanmi.sbc.setting.log.OperationLog;
import com.wanmi.sbc.setting.log.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationLogController implements OperationLogProvider {
    @Autowired
    private OperationLogService operationLogService;

    @Override
    public BaseResponse add(@RequestBody OperationLogAddRequest request) {
        OperationLog log = new OperationLog();

        KsBeanUtil.copyPropertiesThird(request, log);

        operationLogService.add(log);

        return BaseResponse.SUCCESSFUL();
    }
}
