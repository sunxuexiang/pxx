package com.wanmi.sbc.system;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigSaveProvider;
import com.wanmi.sbc.setting.api.request.ConfigContextModifyByTypeAndKeyRequest;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.HomeValueClerkRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.HomeValueClerkResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "HomeValueConfigController", description = "大白鲸APP首页右上角文字配置 API")
@Slf4j
@RestController
@RequestMapping("/homeValueClerk")
public class HomeValueConfigController {

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private SystemConfigSaveProvider systemConfigSaveProvider;


    @RequestMapping(value = "/getValue", method = RequestMethod.POST)
    public BaseResponse getValue () {
        ConfigQueryRequest queryReq = new ConfigQueryRequest();
        queryReq.setConfigKey(ConfigKey.APP_HOME_VALUE_CLERK.toValue());
        queryReq.setConfigType(ConfigType.APP_HOME_VALUE_CLERK_TYPE.toValue());
        queryReq.setDelFlag(0);
        BaseResponse<SystemConfigTypeResponse> baseResponse = systemConfigQueryProvider.findByConfigTypeAndDelFlag(queryReq);
        if (baseResponse.getContext() != null && baseResponse.getContext().getConfig() != null &&
                !StringUtils.isEmpty(baseResponse.getContext().getConfig().getContext())) {
            HomeValueClerkResponse homeValueClerkResponse = JSON.parseObject(baseResponse.getContext().getConfig().getContext(), HomeValueClerkResponse.class);
            return BaseResponse.success(homeValueClerkResponse);
        }
        return BaseResponse.success(new HomeValueClerkResponse());
    }



    @RequestMapping(value = "/saveValue", method = RequestMethod.POST)
    public BaseResponse saveValue (@RequestBody HomeValueClerkRequest request) {
        ConfigContextModifyByTypeAndKeyRequest modify = new ConfigContextModifyByTypeAndKeyRequest();
        modify.setConfigKey(ConfigKey.APP_HOME_VALUE_CLERK);
        modify.setConfigType(ConfigType.APP_HOME_VALUE_CLERK_TYPE);
        modify.setStatus(1);
        modify.setContext(JSON.toJSONString(request));
        systemConfigSaveProvider.modify(modify);
        return BaseResponse.success("");
    }
}
