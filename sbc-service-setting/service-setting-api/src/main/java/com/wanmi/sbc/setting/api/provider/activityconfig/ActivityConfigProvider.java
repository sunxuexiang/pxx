package com.wanmi.sbc.setting.api.provider.activityconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.activityconfig.ActivityConfigAddRequest;
import com.wanmi.sbc.setting.api.response.activityconfig.ActivityConfigAddResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>导航配置保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "ActivityConfigProvider")
public interface ActivityConfigProvider {

	/**
	 * 新增导航配置API
	 *
	 * @author lvheng
	 * @param activityConfigAddRequest 导航配置新增参数结构 {@link ActivityConfigAddRequest}
	 * @return 新增的导航配置信息 {@link ActivityConfigAddResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/activityconfig/add")
	BaseResponse addAll(@RequestBody @Valid ActivityConfigAddRequest activityConfigAddRequest);

}

