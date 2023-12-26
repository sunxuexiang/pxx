package com.wanmi.sbc.setting.api.provider.homedelivery;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryPageRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryPageResponse;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryListRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryListResponse;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryByIdRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>配送到家查询服务Provider</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "HomeDeliveryQueryProvider")
public interface HomeDeliveryQueryProvider {

	/**
	 * 分页查询配送到家API
	 *
	 * @author lh
	 * @param homeDeliveryPageReq 分页请求参数和筛选对象 {@link HomeDeliveryPageRequest}
	 * @return 配送到家分页列表信息 {@link HomeDeliveryPageResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/homedelivery/page")
	BaseResponse<HomeDeliveryPageResponse> page(@RequestBody @Valid HomeDeliveryPageRequest homeDeliveryPageReq);

	/**
	 * 列表查询配送到家API
	 *
	 * @author lh
	 * @param homeDeliveryListReq 列表请求参数和筛选对象 {@link HomeDeliveryListRequest}
	 * @return 配送到家的列表信息 {@link HomeDeliveryListResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/homedelivery/list")
	BaseResponse<HomeDeliveryListResponse> list(@RequestBody @Valid HomeDeliveryListRequest homeDeliveryListReq);

	/**
	 * 单个查询配送到家API
	 *
	 * @author lh
	 * @param homeDeliveryByIdRequest 单个查询配送到家请求参数 {@link HomeDeliveryByIdRequest}
	 * @return 配送到家详情 {@link HomeDeliveryByIdResponse}
	 */
	@PostMapping("/setting/${application.setting.version}/homedelivery/get-by-id")
	BaseResponse<HomeDeliveryByIdResponse> getById(@RequestBody @Valid HomeDeliveryByIdRequest homeDeliveryByIdRequest);

	@PostMapping("/setting/${application.setting.version}/homedelivery/get-by-menuid")
	BaseResponse<String> getByMenuId(@RequestBody HomeDeliveryListRequest homeDeliveryListReq);

	/**
	 * @desc 运营端配置
	 * @author shiy  2023/10/17 16:14
	*/
	@GetMapping("/setting/${application.setting.version}/homedelivery/get-boss-cfg")
	BaseResponse<HomeDeliveryByIdResponse> getBossCfg();

}

