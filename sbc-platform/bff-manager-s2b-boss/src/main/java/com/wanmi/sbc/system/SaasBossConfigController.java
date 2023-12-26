package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.VASStatus;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.AuditProvider;
import com.wanmi.sbc.setting.api.request.ConfigStatusModifyByTypeAndKeyRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * S2B 平台端-审核开关配置
 * Created by wj on 2017/12/06.
 */
@Api(tags = "BossConfigController", description = "S2B 平台端-审核开关配置API")
@RestController
@RequestMapping("/boss/config")
public class SaasBossConfigController {

    @Autowired
    private AuditProvider auditProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RedisService redisService;

    /**
     * 启用Saas化
     *
     * @return
     */
    @ApiOperation(value = "启用Saas化")
    @RequestMapping(value = "/audit/saassetting/open", method = RequestMethod.POST)
    public BaseResponse openSaasSetting() {
        ConfigStatusModifyByTypeAndKeyRequest request = new ConfigStatusModifyByTypeAndKeyRequest();
        request.setConfigKey(ConfigKey.S2BAUDIT);
        request.setConfigType(ConfigType.SAASSETTING);
        request.setStatus(1);
        auditProvider.modifyStatusByTypeAndKey(request);

        redisService.setString(CacheKeyConstant.SAAS_SETTING, VASStatus.ENABLE.toValue());
        operateLogMQUtil.convertAndSend("设置", "修改审核开关", "修改审核开关：启用SaaS化");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 关闭Saas化
     *
     * @return
     */
    @ApiOperation(value = "关闭Saas化")
    @RequestMapping(value = "/audit/saassetting/close", method = RequestMethod.POST)
    public BaseResponse closeSaasSetting() {
        ConfigStatusModifyByTypeAndKeyRequest request = new ConfigStatusModifyByTypeAndKeyRequest();
        request.setConfigKey(ConfigKey.S2BAUDIT);
        request.setConfigType(ConfigType.SAASSETTING);
        request.setStatus(0);
        auditProvider.modifyStatusByTypeAndKey(request);
        redisService.setString(CacheKeyConstant.SAAS_SETTING, VASStatus.DISABLE.toValue());
        operateLogMQUtil.convertAndSend("设置", "修改审核开关", "修改审核开关：关闭SaaS化");
        return BaseResponse.SUCCESSFUL();
    }

}
