package com.wanmi.sbc.system;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigSaveProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.HomeValueClerkResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.ProductInfoSwitchResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "StoreInfoSwitchController", description = "商品信息显示开关 API")
@Slf4j
@RestController
@RequestMapping("/storeInfoSwitch")
public class StoreInfoSwitchController {

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private SystemConfigSaveProvider systemConfigSaveProvider;

    @RequestMapping(value = "/getValue", method = RequestMethod.POST)
    public BaseResponse getValue () {
        ConfigQueryRequest queryReq = new ConfigQueryRequest();
        queryReq.setConfigKey(ConfigKey.PRODUCT_INFO_SHOW_SWITCH.toValue());
        queryReq.setConfigType(ConfigType.PRODUCT_INFO_SHOW_SWITCH_TYPE.toValue());
        queryReq.setDelFlag(0);
        BaseResponse<SystemConfigTypeResponse> baseResponse = systemConfigQueryProvider.findByConfigTypeAndDelFlag(queryReq);
        if (baseResponse.getContext() != null && baseResponse.getContext().getConfig() != null &&
                !StringUtils.isEmpty(baseResponse.getContext().getConfig().getContext())) {
            ProductInfoSwitchResponse homeValueClerkResponse = JSON.parseObject(baseResponse.getContext().getConfig().getContext(), ProductInfoSwitchResponse.class);
            return BaseResponse.success(homeValueClerkResponse);
        }
        return BaseResponse.success(new HomeValueClerkResponse());
    }
}
