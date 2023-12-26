package com.wanmi.sbc.setting.provider.impl.payswitch;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AppPayType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.payswitch.AppPaySwitchProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.payswitch.AppPaySwitchParam;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class AppPaySwitchController implements AppPaySwitchProvider {

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public BaseResponse updateAppPaySwitch(List<AppPaySwitchParam> requestList) {

        List<SystemConfig> systemConfigList =
                systemConfigService.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.PAY_SWITCH_TYPE.toValue())
                        .configType(ConfigType.APP_PAY_SWITCH_CONFIG.toValue()).build());
        if (CollectionUtils.isNotEmpty(systemConfigList)){
            SystemConfig systemConfig = systemConfigList.get(0);
            systemConfig.setContext(JSON.toJSONString(requestList));
            systemConfigService.modify(systemConfig);
            return BaseResponse.SUCCESSFUL();
        }
        SystemConfig systemConfig =new SystemConfig();
        systemConfig.setConfigKey(ConfigKey.PAY_SWITCH_TYPE.toValue());
        systemConfig.setConfigType(ConfigType.APP_PAY_SWITCH_CONFIG.toValue());
        systemConfig.setConfigName("APP支付开关设置");
        systemConfig.setContext(JSON.toJSONString(requestList));
        systemConfig.setStatus(1);
        systemConfig.setDelFlag(DeleteFlag.NO);
        systemConfig.setCreateTime(LocalDateTime.now());
        systemConfig.setUpdateTime(LocalDateTime.now());
        systemConfigService.modify(systemConfig);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<AppPaySwitchParam>> getAppPaySwitch() {
        SystemConfigQueryRequest queryReq = SystemConfigQueryRequest.builder().configKey(ConfigKey.PAY_SWITCH_TYPE.toValue())
                .configType(ConfigType.APP_PAY_SWITCH_CONFIG.toValue()).build();
        try {
            List<SystemConfig> systemConfigList = systemConfigService.list(queryReq);
            List<AppPaySwitchParam> list = JSON.parseArray(systemConfigList.get(0).getContext(), AppPaySwitchParam.class);
            return BaseResponse.success(list);
        }
        catch (Exception e) {
            // 数据配置异常，返回默认值
            log.error("支付开关数据解析异常", e);
        }
        List<AppPaySwitchParam> list = new ArrayList<>();
        list.add(new AppPaySwitchParam(AppPayType.alipay, 1, 1, 0, "支付宝支付", 1, 1, "支付方式当前不可用，敬请期待"));
        list.add(new AppPaySwitchParam(AppPayType.wechatPay, 1, 1, 0, "微信支付", 1, 1, "支付方式当前不可用，敬请期待"));
        list.add(new AppPaySwitchParam(AppPayType.friendPay, 1, 1, 0, "微信好友代付", 1, 1, "支付方式当前不可用，敬请期待"));
        list.add(new AppPaySwitchParam(AppPayType.publicPay, 1, 1, 0, "对公支付", 1, 1, "支付方式当前不可用，敬请期待"));
        return BaseResponse.success(list);
    }

}
