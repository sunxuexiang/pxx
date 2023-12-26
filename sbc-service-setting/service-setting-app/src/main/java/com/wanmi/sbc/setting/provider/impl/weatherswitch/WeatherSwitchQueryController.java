package com.wanmi.sbc.setting.provider.impl.weatherswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.weatherswitch.WeatherSwitchQueryProvider;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchByIdRequest;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchQueryRequest;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchByIdResponse;
import com.wanmi.sbc.setting.weatherswitch.model.root.WeatherSwitch;
import com.wanmi.sbc.setting.weatherswitch.service.WeatherSwitchService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>天气设置查询服务接口实现</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@RestController
@Validated
public class WeatherSwitchQueryController implements WeatherSwitchQueryProvider {
	@Autowired
	private WeatherSwitchService weatherSwitchService;


	@Override
	public BaseResponse<WeatherSwitchByIdResponse> getConfig() {
		List<WeatherSwitch> weatherSwitchList = weatherSwitchService.list(new WeatherSwitchQueryRequest());
		if (CollectionUtils.isNotEmpty(weatherSwitchList)) {
			WeatherSwitch weatherSwitch = weatherSwitchList.get(0);
			return BaseResponse.success(new WeatherSwitchByIdResponse(weatherSwitchService.wrapperVo(weatherSwitch)));
		}else {
			return BaseResponse.success(new WeatherSwitchByIdResponse());
		}

	}

	@Override
	public BaseResponse<WeatherSwitchByIdResponse> getById(@RequestBody @Valid WeatherSwitchByIdRequest weatherSwitchByIdRequest) {
		WeatherSwitch weatherSwitch =
		weatherSwitchService.getOne(weatherSwitchByIdRequest.getId());
		return BaseResponse.success(new WeatherSwitchByIdResponse(weatherSwitchService.wrapperVo(weatherSwitch)));
	}

}

