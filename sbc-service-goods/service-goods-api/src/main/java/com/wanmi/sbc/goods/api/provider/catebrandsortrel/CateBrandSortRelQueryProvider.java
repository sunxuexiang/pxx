package com.wanmi.sbc.goods.api.provider.catebrandsortrel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelPageRequest;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelPageResponse;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelListRequest;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelListResponse;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelByIdRequest;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>类目品牌排序表查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "CateBrandSortRelQueryProvider")
public interface CateBrandSortRelQueryProvider {

	/**
	 * 分页查询类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelPageReq 分页请求参数和筛选对象 {@link CateBrandSortRelPageRequest}
	 * @return 类目品牌排序表分页列表信息 {@link CateBrandSortRelPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/page")
	BaseResponse<CateBrandSortRelPageResponse> page(@RequestBody @Valid CateBrandSortRelPageRequest cateBrandSortRelPageReq);

	/**
	 * 列表查询类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelListReq 列表请求参数和筛选对象 {@link CateBrandSortRelListRequest}
	 * @return 类目品牌排序表的列表信息 {@link CateBrandSortRelListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/list")
	BaseResponse<CateBrandSortRelListResponse> list(@RequestBody @Valid CateBrandSortRelListRequest cateBrandSortRelListReq);

	/**
	 * 单个查询类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelByIdRequest 单个查询类目品牌排序表请求参数 {@link CateBrandSortRelByIdRequest}
	 * @return 类目品牌排序表详情 {@link CateBrandSortRelByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/get-by-id")
	BaseResponse<CateBrandSortRelByIdResponse> getById(@RequestBody @Valid CateBrandSortRelByIdRequest cateBrandSortRelByIdRequest);

}

