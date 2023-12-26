package com.wanmi.sbc.goods.api.provider.stockoutmanage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManagePageRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManagePageResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageListRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageListResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageByIdRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>缺货管理查询服务Provider</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "StockoutManageQueryProvider")
public interface StockoutManageQueryProvider {

	/**
	 * 分页查询缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManagePageReq 分页请求参数和筛选对象 {@link StockoutManagePageRequest}
	 * @return 缺货管理分页列表信息 {@link StockoutManagePageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/page")
	BaseResponse<StockoutManagePageResponse> page(@RequestBody @Valid StockoutManagePageRequest stockoutManagePageReq);

	/**
	 * 分页推送到货通知消息
	 *
	 * @author tzx
	 * @param stockoutManagePageReq 分页请求参数和筛选对象 {@link StockoutManagePageRequest}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/pushGoodsStockPage")
	BaseResponse<StockoutManageListResponse> pushGoodsStockPage(@RequestBody @Valid StockoutManagePageRequest stockoutManagePageReq);


	/**
	 * 列表查询缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManageListReq 列表请求参数和筛选对象 {@link StockoutManageListRequest}
	 * @return 缺货管理的列表信息 {@link StockoutManageListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/list")
	BaseResponse<StockoutManageListResponse> list(@RequestBody @Valid StockoutManageListRequest stockoutManageListReq);

	/**
	 * 单个查询缺货管理API
	 *
	 * @author tzx
	 * @param stockoutManageByIdRequest 单个查询缺货管理请求参数 {@link StockoutManageByIdRequest}
	 * @return 缺货管理详情 {@link StockoutManageByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutmanage/get-by-id")
	BaseResponse<StockoutManageByIdResponse> getById(@RequestBody @Valid StockoutManageByIdRequest stockoutManageByIdRequest);

}

