package com.wanmi.sbc.goods.api.provider.stockoutdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.stockoutdetail.*;
import com.wanmi.sbc.goods.api.response.stockoutdetail.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>缺货管理查询服务Provider</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "StockoutDetailQueryProvider")
public interface StockoutDetailQueryProvider {

	/**
	 * 分页查询缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailPageReq 分页请求参数和筛选对象 {@link StockoutDetailPageRequest}
	 * @return 缺货管理分页列表信息 {@link StockoutDetailPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/page")
	BaseResponse<StockoutDetailPageResponse> page(@RequestBody @Valid StockoutDetailPageRequest stockoutDetailPageReq);

	/**
	 * 列表查询缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailListReq 列表请求参数和筛选对象 {@link StockoutDetailListRequest}
	 * @return 缺货管理的列表信息 {@link StockoutDetailListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/list")
	BaseResponse<StockoutDetailListResponse> list(@RequestBody @Valid StockoutDetailListRequest stockoutDetailListReq);

	/**
	 * 单个查询缺货管理API
	 *
	 * @author tzx
	 * @param stockoutDetailByIdRequest 单个查询缺货管理请求参数 {@link StockoutDetailByIdRequest}
	 * @return 缺货管理详情 {@link StockoutDetailByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/get-by-id")
	BaseResponse<StockoutDetailByIdResponse> getById(@RequestBody @Valid StockoutDetailByIdRequest stockoutDetailByIdRequest);


	/**
	 * 校验当前用户缺货明细记录数据
	 *
	 * @author tzx
	 * @param stockoutDetailVerifyRequest 校验当前用户缺货明细记录数据 {@link StockoutDetailByIdRequest}
	 * @return 校验当前用户缺货明细记录数据 {@link StockoutDetailByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/verify-detail")
	BaseResponse<StockoutDetailVerifyResponse> verifyDetail(@RequestBody @Valid StockoutDetailVerifyRequest stockoutDetailVerifyRequest);

	/**
	 * 校验当前用户缺货明细记录数据
	 *
	 * @author tzx
	 * @param stockoutDetailVerifyRequest 校验当前用户缺货明细记录数据 {@link StockoutDetailByIdRequest}
	 * @return 校验当前用户缺货明细记录数据 {@link StockoutDetailByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/stockoutdetail/verify-by-goodInfoId-detail")
	BaseResponse<StockouDetailVerifyGoodInfoIdResponse> verifyByGoodInfoIdDetail(@RequestBody @Valid StockoutDetailQueryRequest stockoutDetailVerifyRequest);

}

