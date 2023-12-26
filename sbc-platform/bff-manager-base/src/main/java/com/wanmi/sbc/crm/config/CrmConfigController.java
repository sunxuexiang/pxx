package com.wanmi.sbc.crm.config;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.crm.config.response.CrmFlagGetResponse;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(description = "Crm基本配置API", tags = "CrmConfigController")
@RestController
@RequestMapping(value = "/crm/config")
public class CrmConfigController {

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @ApiOperation(value = "获取CRM标记")
    @GetMapping("/flag")
    public BaseResponse<CrmFlagGetResponse> isCrm() {
        TradeConfigGetByTypeRequest typeRequest = new TradeConfigGetByTypeRequest();
        typeRequest.setConfigType(ConfigType.CRM_FLAG);
        Integer status = auditQueryProvider.getTradeConfigByType(typeRequest).getContext().getStatus();
        return BaseResponse.success(CrmFlagGetResponse.builder().crmFlag(Constants.yes.equals(status)).build());
    }
}
