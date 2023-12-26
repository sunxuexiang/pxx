package com.wanmi.sbc.goods.api.provider.stockoutmanage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageAddRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageAddResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageModifyRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageModifyResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageDelByIdRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>缺货管理保存服务Provider</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "StockoutManageProvider")
public interface StockoutManageProvider {

	/**
	 * 新增缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManageAddRequest 缺货管理新增参数结构 {@link StockoutManageAddRequest}
	 * @return 新增的缺货管理信息 {@link StockoutManageAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/add")
	BaseResponse<StockoutManageAddResponse> add(@RequestBody @Valid StockoutManageAddRequest stockoutManageAddRequest);

	/**
	 * 修改缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManageModifyRequest 缺货管理修改参数结构 {@link StockoutManageModifyRequest}
	 * @return 修改的缺货管理信息 {@link StockoutManageModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/modify")
	BaseResponse<StockoutManageModifyResponse> modify(@RequestBody @Valid StockoutManageModifyRequest stockoutManageModifyRequest);

	/**
	 * 单个删除缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManageDelByIdRequest 单个删除参数结构 {@link StockoutManageDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid StockoutManageDelByIdRequest stockoutManageDelByIdRequest);

	/**
	 * 批量删除缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManageDelByIdListRequest 批量删除参数结构 {@link StockoutManageDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid StockoutManageDelByIdListRequest stockoutManageDelByIdListRequest);

}

