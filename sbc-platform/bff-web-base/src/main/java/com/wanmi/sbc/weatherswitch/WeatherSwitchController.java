package com.wanmi.sbc.weatherswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.weatherswitch.WeatherSwitchQueryProvider;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchByIdRequest;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchByIdResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(description = "天气设置管理API", tags = "WeatherSwitchController")
@RestController
@RequestMapping(value = "/weatherswitch")
public class WeatherSwitchController {

    @Autowired
    private WeatherSwitchQueryProvider weatherSwitchQueryProvider;


    @ApiOperation(value = "查询天气设置")
    @GetMapping("/getConfig")
    public BaseResponse<WeatherSwitchByIdResponse> findConfig() {
        return weatherSwitchQueryProvider.getConfig();
    }



}
