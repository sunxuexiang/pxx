package com.wanmi.sbc.goods.api.provider.goodslabel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelListRequest;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelListResponse;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelByIdRequest;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>导航配置查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsLabelQueryProvider")
public interface GoodsLabelQueryProvider {

	/**
	 * 列表查询导航配置API
	 *
	 * @author lvheng
	 * @param goodsLabelListReq 列表请求参数和筛选对象 {@link GoodsLabelListRequest}
	 * @return 导航配置的列表信息 {@link GoodsLabelListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/list")
	BaseResponse<GoodsLabelListResponse> list(@RequestBody @Valid GoodsLabelListRequest goodsLabelListReq);

	/**
	 * 单个查询导航配置API
	 *
	 * @author lvheng
	 * @param goodsLabelByIdRequest 单个查询导航配置请求参数 {@link GoodsLabelByIdRequest}
	 * @return 导航配置详情 {@link GoodsLabelByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/get-by-id")
	BaseResponse<GoodsLabelByIdResponse> getById(@RequestBody @Valid GoodsLabelByIdRequest goodsLabelByIdRequest);

}

