package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.configuration.MobileConfig;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "MobileConfigController", description = "移动端配置 API")
@RestController
@RequestMapping("/mobile/config")
public class MobileConfigController {

    @Resource
    private AuditQueryProvider auditQueryProvider;

    @Resource
    private MobileConfig mobileConfig;

    @ApiOperation(value = "获取配置列表")
    @RequestMapping(value = "/audit/list", method = RequestMethod.GET)
    public BaseResponse<List<ConfigVO>> listConfigs() {
        return BaseResponse.success(auditQueryProvider.listAuditConfig().getContext().getConfigVOList());
    }

    @ApiOperation(value = "获取显示图标开关")
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public BaseResponse<Map<String,Object>> showConfig() {
        Map<String, Object> result = new HashMap<>(2);
        result.put("showPublishIcon", mobileConfig.getShowPublishIcon());
        result.put("showLiveIcon", mobileConfig.getShowLiveIcon());

        return BaseResponse.success(result);
    }
}
