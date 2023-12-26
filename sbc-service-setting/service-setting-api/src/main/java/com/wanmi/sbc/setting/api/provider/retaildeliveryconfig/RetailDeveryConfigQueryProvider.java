package com.wanmi.sbc.setting.api.provider.retaildeliveryconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.request.retaildeliveryconfig.RetailDeliveryConfigRequest;
import com.wanmi.sbc.setting.api.response.packingconfig.PackingConfigResponse;
import com.wanmi.sbc.setting.api.response.retaildeliveryconfig.RetailDeliveryConfigResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>onlineService查询服务Provider</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "RetailDeveryConfigQueryProvider")
public interface RetailDeveryConfigQueryProvider {

	/**
	 * packingConfig 查询包装配置
	 *
	 * @author lq
	 * @param
	 * @return
	 */
	@PostMapping("/setting/${application.setting.version}/retaildeveryconfig/getdata")
	 BaseResponse<RetailDeliveryConfigResponse> list( );


}

