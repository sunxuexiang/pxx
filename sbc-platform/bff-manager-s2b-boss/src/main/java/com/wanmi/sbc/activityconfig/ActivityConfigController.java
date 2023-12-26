package com.wanmi.sbc.activityconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.activityconfig.ActivityConfigProvider;
import com.wanmi.sbc.setting.api.provider.activityconfig.ActivityConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.activityconfig.ActivityConfigAddRequest;
import com.wanmi.sbc.setting.api.response.activityconfig.ActivityConfigAddResponse;
import com.wanmi.sbc.setting.api.response.activityconfig.ActivityConfigListResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(description = "满系活动配合配置API", tags = "ActivityConfigController")
@RestController
@RequestMapping(value = "/reduced/config")
public class ActivityConfigController {

    @Autowired
    private ActivityConfigQueryProvider activityConfigQueryProvider;

    @Autowired
    private ActivityConfigProvider activityConfigProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;



    @ApiOperation(value = "列表查询满系配置")
    @GetMapping("/get")
    public BaseResponse<ActivityConfigListResponse> getList() {
        return activityConfigQueryProvider.list();
    }



    @ApiOperation(value = "新增导航配置")
    @PostMapping("/add")
    public BaseResponse<ActivityConfigAddResponse> add(@RequestBody @Valid ActivityConfigAddRequest addReq) {
        operateLogMQUtil.convertAndSend("满系活动配合配置", "新增导航配置", "新增导航配置");
        return activityConfigProvider.addAll(addReq);
    }




}
