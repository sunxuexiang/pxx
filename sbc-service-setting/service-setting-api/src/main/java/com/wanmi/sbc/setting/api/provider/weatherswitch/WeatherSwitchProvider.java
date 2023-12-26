package com.wanmi.sbc.setting.api.provider.weatherswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchAddRequest;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchModifyRequest;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchAddResponse;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>天气设置保存服务Provider</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "WeatherSwitchProvider")
public interface WeatherSwitchProvider {

	/**
	 * 新增天气设置API
	 *
	 * @author 费传奇
	 * @param weatherSwitchAddRequest 天气设置新增参数结构 {@link WeatherSwitchAddRequest}
	 * @return 新增的天气设置信息 {@link WeatherSwitchAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/weatherswitch/add")
	BaseResponse<WeatherSwitchAddResponse> add(@RequestBody @Valid WeatherSwitchAddRequest weatherSwitchAddRequest);

	/**
	 * 修改天气设置API
	 *
	 * @author 费传奇
	 * @param weatherSwitchModifyRequest 天气设置修改参数结构 {@link WeatherSwitchModifyRequest}
	 * @return 修改的天气设置信息 {@link WeatherSwitchModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/weatherswitch/modify")
	BaseResponse<WeatherSwitchModifyResponse> modify(@RequestBody @Valid WeatherSwitchModifyRequest weatherSwitchModifyRequest);



}

