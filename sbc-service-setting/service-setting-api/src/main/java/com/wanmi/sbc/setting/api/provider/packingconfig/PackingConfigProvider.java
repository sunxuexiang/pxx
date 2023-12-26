package com.wanmi.sbc.setting.api.provider.packingconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>onlineService保存服务Provider</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "PackingConfigProvider")
public interface PackingConfigProvider {

	/**
	 *添加或修改PackingConfig表
	 *
	 * @param packingConfigRequest
	 * @return 修改的onlineService信息
	 */
	@PostMapping("/setting/${application.setting.version}/packingconfig/addAndUpdate")
	BaseResponse modify(@RequestBody @Valid PackingConfigRequest
                                packingConfigRequest);
}

