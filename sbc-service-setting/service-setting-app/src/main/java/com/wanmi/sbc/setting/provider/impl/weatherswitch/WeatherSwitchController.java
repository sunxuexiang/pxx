package com.wanmi.sbc.setting.provider.impl.weatherswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.weatherswitch.WeatherSwitchProvider;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchAddRequest;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchModifyRequest;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchAddResponse;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchModifyResponse;
import com.wanmi.sbc.setting.weatherswitch.model.root.WeatherSwitch;
import com.wanmi.sbc.setting.weatherswitch.service.WeatherSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>天气设置保存服务接口实现</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@RestController
@Validated
public class WeatherSwitchController implements WeatherSwitchProvider {
	@Autowired
	private WeatherSwitchService weatherSwitchService;

	@Override
	public BaseResponse<WeatherSwitchAddResponse> add(@RequestBody @Valid WeatherSwitchAddRequest weatherSwitchAddRequest) {
		WeatherSwitch weatherSwitch = KsBeanUtil.convert(weatherSwitchAddRequest, WeatherSwitch.class);
		return BaseResponse.success(new WeatherSwitchAddResponse(
				weatherSwitchService.wrapperVo(weatherSwitchService.add(weatherSwitch))));
	}

	@Override
	public BaseResponse<WeatherSwitchModifyResponse> modify(@RequestBody @Valid WeatherSwitchModifyRequest weatherSwitchModifyRequest) {
		WeatherSwitch weatherSwitch = KsBeanUtil.convert(weatherSwitchModifyRequest, WeatherSwitch.class);
		return BaseResponse.success(new WeatherSwitchModifyResponse(
				weatherSwitchService.wrapperVo(weatherSwitchService.modify(weatherSwitch))));
	}


}

