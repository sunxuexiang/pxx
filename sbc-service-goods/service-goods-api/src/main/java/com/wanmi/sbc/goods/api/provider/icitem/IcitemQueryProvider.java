package com.wanmi.sbc.goods.api.provider.icitem;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemPageRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemPageResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemListRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemListResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemByIdRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>配送到家查询服务Provider</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "IcitemQueryProvider")
public interface IcitemQueryProvider {

	/**
	 * 分页查询配送到家API
	 *
	 * @author lh
	 * @param icitemPageReq 分页请求参数和筛选对象 {@link IcitemPageRequest}
	 * @return 配送到家分页列表信息 {@link IcitemPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/page")
	BaseResponse<IcitemPageResponse> page(@RequestBody @Valid IcitemPageRequest icitemPageReq);

	/**
	 * 列表查询配送到家API
	 *
	 * @author lh
	 * @param icitemListReq 列表请求参数和筛选对象 {@link IcitemListRequest}
	 * @return 配送到家的列表信息 {@link IcitemListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/list")
	BaseResponse<IcitemListResponse> list(@RequestBody @Valid IcitemListRequest icitemListReq);

	/**
	 * 单个查询配送到家API
	 *
	 * @author lh
	 * @param icitemByIdRequest 单个查询配送到家请求参数 {@link IcitemByIdRequest}
	 * @return 配送到家详情 {@link IcitemByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/get-by-id")
	BaseResponse<IcitemByIdResponse> getById(@RequestBody @Valid IcitemByIdRequest icitemByIdRequest);

}

