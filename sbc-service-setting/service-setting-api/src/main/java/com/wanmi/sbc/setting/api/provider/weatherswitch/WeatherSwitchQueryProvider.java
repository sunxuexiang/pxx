package com.wanmi.sbc.setting.api.provider.weatherswitch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchByIdRequest;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchListRequest;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchByIdResponse;
import com.wanmi.sbc.setting.api.response.weatherswitch.WeatherSwitchListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>天气设置查询服务Provider</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "WeatherSwitchQueryProvider")
public interface WeatherSwitchQueryProvider {

	/**
	 * 列表查询天气设置API
	 *
	 * @author 费传奇
	 * @param weatherSwitchListReq 列表请求参数和筛选对象 {@link WeatherSwitchListRequest}
	 * @return 天气设置的列表信息 {@link WeatherSwitchListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/weatherswitch/find-config")
	BaseResponse<WeatherSwitchByIdResponse> getConfig();

	/**
	 * 单个查询天气设置API
	 *
	 * @author 费传奇
	 * @param weatherSwitchByIdRequest 单个查询天气设置请求参数 {@link WeatherSwitchByIdRequest}
	 * @return 天气设置详情 {@link WeatherSwitchByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/weatherswitch/get-by-id")
	BaseResponse<WeatherSwitchByIdResponse> getById(@RequestBody @Valid WeatherSwitchByIdRequest weatherSwitchByIdRequest);

}

