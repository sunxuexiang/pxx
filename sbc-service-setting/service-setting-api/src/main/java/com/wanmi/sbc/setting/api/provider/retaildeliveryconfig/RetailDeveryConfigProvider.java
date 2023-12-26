package com.wanmi.sbc.setting.api.provider.retaildeliveryconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.request.retaildeliveryconfig.RetailDeliveryConfigRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>onlineService保存服务Provider</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "RetailDeveryConfigProvider")
public interface RetailDeveryConfigProvider {

	/**
	 *添加或修改PackingConfig表
	 *
	 * @param retailDeliveryConfigRequest
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/retaildeveryconfig/addAndUpdate")
	BaseResponse modify(@RequestBody @Valid RetailDeliveryConfigRequest
                                retailDeliveryConfigRequest);
}

