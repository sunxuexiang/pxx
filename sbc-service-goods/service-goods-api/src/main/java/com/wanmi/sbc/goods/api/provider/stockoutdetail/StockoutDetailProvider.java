package com.wanmi.sbc.goods.api.provider.stockoutdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.stockoutdetail.*;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockoutDetailAddResponse;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockoutDetailModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>缺货管理保存服务Provider</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "StockoutDetailProvider")
public interface StockoutDetailProvider {

	/**
	 * 新增缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailAddRequest 缺货管理新增参数结构 {@link StockoutDetailAddRequest}
	 * @return 新增的缺货管理信息 {@link StockoutDetailAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/add")
	BaseResponse<StockoutDetailAddResponse> add(@RequestBody @Valid StockoutDetailAddRequest stockoutDetailAddRequest);

	/**
	 * 新增缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailAddRequest 缺货管理新增参数结构 {@link StockoutDetailAddAllRequest}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/addAll")
	BaseResponse addAll(@RequestBody @Valid StockoutDetailAddAllRequest stockoutDetailAddRequest);

	/**
	 * 修改缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailModifyRequest 缺货管理修改参数结构 {@link StockoutDetailModifyRequest}
	 * @return 修改的缺货管理信息 {@link StockoutDetailModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/modify")
	BaseResponse<StockoutDetailModifyResponse> modify(@RequestBody @Valid StockoutDetailModifyRequest stockoutDetailModifyRequest);

	/**
	 * 单个删除缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailDelByIdRequest 单个删除参数结构 {@link StockoutDetailDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid StockoutDetailDelByIdRequest stockoutDetailDelByIdRequest);

	/**
	 * 批量删除缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailDelByIdListRequest 批量删除参数结构 {@link StockoutDetailDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid StockoutDetailDelByIdListRequest stockoutDetailDelByIdListRequest);

}

