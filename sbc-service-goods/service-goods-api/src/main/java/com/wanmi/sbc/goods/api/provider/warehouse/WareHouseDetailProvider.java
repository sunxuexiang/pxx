package com.wanmi.sbc.goods.api.provider.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>仓库表保存服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "WareHouseDetailProvider")
public interface WareHouseDetailProvider {


	/**
	 * 列表查询仓库表API
	 *
	 * @param wreHouseDetailRequest 列表请求参数和筛选对象 {@link WareHouseListRequest}
	 * @return 仓库表的列表信息 {@link WareHouseListResponse}
	 * @author zhangwenchang
	 */
	@PostMapping("/goods/${application.goods.version}/warehousedetali/list")
	BaseResponse<WareHouseDetailPageResponse> list(@RequestBody @Valid WareHouseDetailRequest wreHouseDetailRequest);


	/**
	 * 新增仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseDetailAddRequest 仓库表新增参数结构 {@link WareHouseAddRequest}
	 * @return 新增的仓库表信息 {@link WareHouseAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousedetali/add")
	BaseResponse<WareHouseDetailResponse> add(@RequestBody @Valid WareHouseDetailAddRequest wareHouseDetailAddRequest);


	/**
	 * 修改仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseDetailAddRequest 仓库表修改参数结构 {@link WareHouseModifyRequest}
	 * @return 修改的仓库表信息 {@link WareHouseModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousedetali/modify")
	BaseResponse<WareHouseDetailResponse> modify(@RequestBody @Valid WareHouseDetailAddRequest wareHouseDetailAddRequest);


	/**
	 * 单个删除仓库表API
	 *
	 * @author zhangwenchang
	 * @param wreHouseDetailAddRequest 单个删除参数结构 {@link WareHouseDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousedetali/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid WareHouseDetailAddRequest wreHouseDetailAddRequest);

}

