package com.wanmi.sbc.setting.api.provider.activityconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.activityconfig.ActivityConfigListRequest;
import com.wanmi.sbc.setting.api.response.activityconfig.ActivityConfigListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>导航配置查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "ActivityConfigQueryProvider")
public interface ActivityConfigQueryProvider {


	/**
	 * 列表查询导航配置API
	 *
	 * @author lvheng
	 * @param activityConfigListReq 列表请求参数和筛选对象 {@link ActivityConfigListRequest}
	 * @return 导航配置的列表信息 {@link ActivityConfigListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/activityconfig/list")
	BaseResponse<ActivityConfigListResponse> list();

}

