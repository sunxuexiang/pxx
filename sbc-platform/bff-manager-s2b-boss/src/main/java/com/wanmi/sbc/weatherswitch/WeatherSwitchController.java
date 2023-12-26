package com.wanmi.sbc.weatherswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.weatherswitch.WeatherSwitchProvider;
import com.wanmi.sbc.setting.api.provider.weatherswitch.WeatherSwitchQueryProvider;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchAddRequest;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchModifyRequest;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchAddResponse;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchByIdResponse;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchModifyResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Api(description = "天气设置管理API", tags = "WeatherSwitchController")
@RestController
@RequestMapping(value = "/weatherswitch")
public class WeatherSwitchController {

    @Autowired
    private WeatherSwitchQueryProvider weatherSwitchQueryProvider;

    @Autowired
    private WeatherSwitchProvider weatherSwitchProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;



    @ApiOperation(value = "查询天气设置")
    @GetMapping("/getConfig")
    public BaseResponse<WeatherSwitchByIdResponse> getConfig() {
        return weatherSwitchQueryProvider.getConfig();
    }


    @ApiOperation(value = "新增天气设置")
    @PostMapping("/add")
    public BaseResponse<WeatherSwitchAddResponse> add(@RequestBody @Valid WeatherSwitchAddRequest addReq) {
        addReq.setCreateTime(LocalDateTime.now());
        operateLogMQUtil.convertAndSend("设置", "天气设置管理", "新增天气设置");
        return weatherSwitchProvider.add(addReq);
    }


    @ApiOperation(value = "修改天气设置")
    @PutMapping("/modify")
    public BaseResponse<WeatherSwitchModifyResponse> modify(@RequestBody @Valid WeatherSwitchModifyRequest modifyReq) {
        modifyReq.setUpdateTime(LocalDateTime.now());
        operateLogMQUtil.convertAndSend("设置", "天气设置管理", "修改天气设置");
        return weatherSwitchProvider.modify(modifyReq);
    }

}
