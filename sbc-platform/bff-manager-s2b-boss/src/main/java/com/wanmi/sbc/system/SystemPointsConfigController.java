package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.SystemPointsConfigProvider;
import com.wanmi.sbc.setting.api.provider.SystemPointsConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.SystemPointsConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.SystemPointsConfigQueryResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 平台端-积分设置
 */
@Api(tags = "SystemPointsConfigController", description = "平台端-积分设置API")
@RestController
@RequestMapping("/boss/pointsConfig")
public class SystemPointsConfigController {

    @Autowired
    private SystemPointsConfigQueryProvider systemPointsConfigQueryProvider;

    @Autowired
    private SystemPointsConfigProvider systemPointsConfigProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询积分设置
     *
     * @return
     */
    @ApiOperation(value = "查询积分设置")
    @RequestMapping(method = RequestMethod.GET)
    public BaseResponse<SystemPointsConfigQueryResponse> query() {
        return systemPointsConfigQueryProvider.querySystemPointsConfig();
    }

    /**
     * 更新积分设置
     *
     * @return BaseResponse
     */
    @ApiOperation(value = "更新积分设置")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody SystemPointsConfigModifyRequest request) {
        operateLogMQUtil.convertAndSend("设置", "积分设置", "更新积分设置");
        return systemPointsConfigProvider.modifySystemPointsConfig(request);
    }

}
