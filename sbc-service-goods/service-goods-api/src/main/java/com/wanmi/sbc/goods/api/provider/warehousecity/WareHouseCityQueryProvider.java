package com.wanmi.sbc.goods.api.provider.warehousecity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityByIdRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityListRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityPageRequest;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityByIdResponse;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityListResponse;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p> 仓库地区表查询服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "WareHouseCityQueryProvider")
public interface WareHouseCityQueryProvider {

	/**
	 * 分页查询 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityPageReq 分页请求参数和筛选对象 {@link WareHouseCityPageRequest}
	 * @return  仓库地区表分页列表信息 {@link WareHouseCityPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/page")
    BaseResponse<WareHouseCityPageResponse> page(@RequestBody @Valid WareHouseCityPageRequest wareHouseCityPageReq);

	/**
	 * 列表查询 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityListReq 列表请求参数和筛选对象 {@link WareHouseCityListRequest}
	 * @return  仓库地区表的列表信息 {@link WareHouseCityListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/list")
    BaseResponse<WareHouseCityListResponse> list(@RequestBody @Valid WareHouseCityListRequest wareHouseCityListReq);

	/**
	 * 单个查询 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityByIdRequest 单个查询 仓库地区表请求参数 {@link WareHouseCityByIdRequest}
	 * @return  仓库地区表详情 {@link WareHouseCityByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/get-by-id")
    BaseResponse<WareHouseCityByIdResponse> getById(@RequestBody @Valid WareHouseCityByIdRequest wareHouseCityByIdRequest);

}

