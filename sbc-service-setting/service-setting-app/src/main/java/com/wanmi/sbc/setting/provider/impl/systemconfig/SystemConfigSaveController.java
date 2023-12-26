package com.wanmi.sbc.setting.provider.impl.systemconfig;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigSaveProvider;
import com.wanmi.sbc.setting.api.request.ConfigContextModifyByTypeAndKeyRequest;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.ConfigUpdateRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.LogisticsSaveRopRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.config.ConfigService;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by feitingting on 2019/11/6.
 */
@RestController
public class SystemConfigSaveController implements SystemConfigSaveProvider{
    @Autowired
    ConfigService configService;
    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public BaseResponse saveKuaidi(@RequestBody @Validated LogisticsSaveRopRequest request){
        configService.save(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(@Valid ConfigContextModifyByTypeAndKeyRequest request) {
        List<SystemConfig> systemConfigList =
                systemConfigService.list(SystemConfigQueryRequest.builder().configKey(request.getConfigKey().toValue())
                        .configType(request.getConfigType().toValue()).build());

        if (CollectionUtils.isNotEmpty(systemConfigList)){
            SystemConfig systemConfig = systemConfigList.get(0);
            systemConfig.setStatus(request.getStatus());
            systemConfig.setContext(request.getContext());
            systemConfigService.modify(systemConfig);
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.FAILED();
    }

    @Override
    public BaseResponse update(@Valid ConfigUpdateRequest request) {
        configService.update(request);
        return BaseResponse.SUCCESSFUL();
    }
    @Override
    public BaseResponse imModify(@Valid ConfigContextModifyByTypeAndKeyRequest request) {
        List<SystemConfig> systemConfigList =
                systemConfigService.list(SystemConfigQueryRequest.builder().configKey(request.getConfigKey().toValue())
                        .configType(request.getConfigType().toValue()).build());

        if (CollectionUtils.isNotEmpty(systemConfigList)){
            SystemConfig systemConfig = systemConfigList.get(0);
            systemConfig.setStatus(request.getStatus());
            systemConfig.setContext(request.getContext());
            systemConfigService.modify(systemConfig);
            return BaseResponse.SUCCESSFUL();
        }else {
            SystemConfig systemConfig =new SystemConfig();
            systemConfig.setConfigKey(request.getConfigKey().toValue());
            systemConfig.setConfigType(request.getConfigType().toValue());
            systemConfig.setConfigName("腾讯IM客服");
            systemConfig.setContext(request.getContext());
            systemConfig.setStatus(request.getStatus());
            systemConfig.setDelFlag(DeleteFlag.NO);
            systemConfig.setCreateTime(LocalDateTime.now());
            systemConfig.setUpdateTime(LocalDateTime.now());
            systemConfigService.modify(systemConfig);
            return BaseResponse.SUCCESSFUL();
        }
    }

    @Override
    public BaseResponse saveConfig(ConfigContextModifyByTypeAndKeyRequest modify) {
        systemConfigService.saveConfig(modify);
        return BaseResponse.success("");
    }

}
