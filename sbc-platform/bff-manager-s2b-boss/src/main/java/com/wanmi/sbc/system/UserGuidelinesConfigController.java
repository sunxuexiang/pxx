package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.VASStatus;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.AuditProvider;
import com.wanmi.sbc.setting.api.provider.UserGuidelinesConfigProvider;
import com.wanmi.sbc.setting.api.request.ConfigStatusModifyByTypeAndKeyRequest;
import com.wanmi.sbc.setting.api.request.UserGuidelinesConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.UserGuidelinesConfigQueryResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户须知配置
 */
@Api(tags = "UserGuidelinesConfigController", description = "用户须知配置开关配置API")
@RestController
@RequestMapping("/userGuidelines/config")
public class UserGuidelinesConfigController {

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private UserGuidelinesConfigProvider userGuidelinesConfigProvider;

    /**
     * 启用Saas化
     *
     * @return
     */
    @ApiOperation(value = "修改用户须知开关")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public BaseResponse modifyUserGuidelinesSetting(@RequestBody UserGuidelinesConfigModifyRequest request) {
        operateLogMQUtil.convertAndSend("设置", "修改用户须知开关", "修改用户须知开关："+request.getStatus());
        return userGuidelinesConfigProvider.modifyUserGuidelinesConfig(request);
    }

    /**
     * 关闭Saas化
     *
     * @return
     */
    @ApiOperation(value = "查询用户须知开关")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public BaseResponse<UserGuidelinesConfigQueryResponse> closeSaasSetting() {
        return userGuidelinesConfigProvider.queryUserGuidelinesConfig();
    }

}
