package com.wanmi.sbc.activityconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.activityconfig.ActivityConfigProvider;
import com.wanmi.sbc.setting.api.provider.activityconfig.ActivityConfigQueryProvider;
import com.wanmi.sbc.setting.api.response.activityconfig.ActivityConfigListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(description = "满系活动配合配置API", tags = "ActivityConfigBaseController")
@RestController
@RequestMapping(value = "/reduced/config")
public class ActivityConfigBaseController {

    @Autowired
    private ActivityConfigQueryProvider activityConfigQueryProvider;

    @Autowired
    private ActivityConfigProvider activityConfigProvider;



    @ApiOperation(value = "列表查询满系配置")
    @GetMapping("/get")
    public BaseResponse<ActivityConfigListResponse> getList() {
        return activityConfigQueryProvider.list();
    }

}
