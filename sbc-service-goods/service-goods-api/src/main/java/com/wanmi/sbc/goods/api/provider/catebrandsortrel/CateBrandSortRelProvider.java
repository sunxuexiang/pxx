package com.wanmi.sbc.goods.api.provider.catebrandsortrel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.*;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelAddResponse;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelModifyResponse;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelTemplateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>类目品牌排序表保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "CateBrandSortRelProvider")
public interface CateBrandSortRelProvider {

	/**
	 * 新增类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelAddRequest 类目品牌排序表新增参数结构 {@link CateBrandSortRelAddRequest}
	 * @return 新增的类目品牌排序表信息 {@link CateBrandSortRelAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/add")
	BaseResponse<CateBrandSortRelAddResponse> add(@RequestBody @Valid CateBrandSortRelAddRequest cateBrandSortRelAddRequest);

	/**
	 * 修改类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelModifyRequest 类目品牌排序表修改参数结构 {@link CateBrandSortRelModifyRequest}
	 * @return 修改的类目品牌排序表信息 {@link CateBrandSortRelModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/modify")
	BaseResponse<CateBrandSortRelModifyResponse> modify(@RequestBody @Valid CateBrandSortRelModifyRequest cateBrandSortRelModifyRequest);

	/**
	 * 单个删除类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelDelByIdRequest 单个删除参数结构 {@link CateBrandSortRelDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid CateBrandSortRelDelByIdRequest cateBrandSortRelDelByIdRequest);

	/**
	 * 批量删除类目品牌排序表API
	 *
	 * @author lvheng
	 * @param cateBrandSortRelDelByIdListRequest 批量删除参数结构 {@link CateBrandSortRelDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid CateBrandSortRelDelByIdListRequest cateBrandSortRelDelByIdListRequest);


	/**
	 * 批量添加绑定
	 * @param relBatchAddRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/batch-add")
	BaseResponse batchAdd(@RequestBody @Valid CateBrandSortRelBatchAddRequest relBatchAddRequest);

	/**
	 * 获取导出模板
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/catebrandsortrel/export")
	BaseResponse<CateBrandSortRelTemplateResponse> exportTemplate();
}

