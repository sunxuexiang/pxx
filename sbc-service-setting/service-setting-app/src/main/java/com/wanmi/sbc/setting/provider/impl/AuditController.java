package com.wanmi.sbc.setting.provider.impl;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.AuditProvider;
import com.wanmi.sbc.setting.api.request.*;
import com.wanmi.sbc.setting.api.response.InvoiceConfigModifyResponse;
import com.wanmi.sbc.setting.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuditController implements AuditProvider {
    @Autowired
    private AuditService auditService;

    @Override
    public BaseResponse modifyStatusByTypeAndKey(@RequestBody @Valid ConfigStatusModifyByTypeAndKeyRequest request) {
        auditService.updateStatusByTypeAndKey(request.getConfigKey(), request.getConfigType(), request.getStatus());
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse<InvoiceConfigModifyResponse> modifyInvoiceConfig(@RequestBody @Valid InvoiceConfigModifyRequest request) {
        InvoiceConfigModifyResponse response = new InvoiceConfigModifyResponse();
        response.setCount(auditService.modifyInvoiceConfig(request.getStatus()));

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse modifyTradeConfigs(@RequestBody @Valid TradeConfigsModifyRequest request) {
        auditService.modifyTradeConfigs(request.getTradeConfigDTOList());

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyShareLittleProgram(@RequestBody @Valid ConfigContextModifyByTypeAndKeyRequest request) {
        auditService.modifyShareLittleProgram(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新配置信息
     *
     * @param configListModifyRequest
     * @return
     */
    @Override
    public BaseResponse modifyConfigList(@RequestBody ConfigListModifyRequest configListModifyRequest) {
        auditService.modifyConfigList(configListModifyRequest.getConfigRequestList());
        return BaseResponse.SUCCESSFUL();
    }
}
