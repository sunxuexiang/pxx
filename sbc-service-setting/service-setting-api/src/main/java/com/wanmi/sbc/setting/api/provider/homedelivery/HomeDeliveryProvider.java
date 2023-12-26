package com.wanmi.sbc.setting.api.provider.homedelivery;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryModifyRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>配送到家保存服务Provider</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "HomeDeliveryProvider")
public interface HomeDeliveryProvider {


	/**
	 * 修改配送到家API
	 *
	 * @author lh
	 * @param homeDeliveryModifyRequest 配送到家修改参数结构 {@link HomeDeliveryModifyRequest}
	 * @return 修改的配送到家信息 {@link HomeDeliveryModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/homedelivery/modify")
	BaseResponse<HomeDeliveryModifyResponse> modify(@RequestBody @Valid HomeDeliveryModifyRequest homeDeliveryModifyRequest);

	/**
	 * 修改配送到家根据类型API
	 *
	 * @author lh
	 * @param homeDeliveryModifyRequest 配送到家修改参数结构 {@link HomeDeliveryModifyRequest}
	 * @return 修改的配送到家信息 {@link HomeDeliveryModifyResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/homedelivery/modifyByDeliverType")
	BaseResponse<HomeDeliveryModifyResponse> modifyByDeliverType(@RequestBody HomeDeliveryModifyRequest homeDeliveryModifyRequest);





}

