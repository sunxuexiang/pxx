package com.wanmi.sbc.goods.api.provider.goodswarestockdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailByIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailPageRequest;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailByIdResponse;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailListResponse;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p> 库存明细表查询服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsWareStockDetailQueryProvider")
public interface GoodsWareStockDetailQueryProvider {

	/**
	 * 分页查询 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailPageReq 分页请求参数和筛选对象 {@link GoodsWareStockDetailPageRequest}
	 * @return  库存明细表分页列表信息 {@link GoodsWareStockDetailPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/page")
    BaseResponse<GoodsWareStockDetailPageResponse> page(@RequestBody @Valid GoodsWareStockDetailPageRequest goodsWareStockDetailPageReq);

	/**
	 * 列表查询 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailListReq 列表请求参数和筛选对象 {@link GoodsWareStockDetailListRequest}
	 * @return  库存明细表的列表信息 {@link GoodsWareStockDetailListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/list")
    BaseResponse<GoodsWareStockDetailListResponse> list(@RequestBody @Valid GoodsWareStockDetailListRequest goodsWareStockDetailListReq);

	/**
	 * 单个查询 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailByIdRequest 单个查询 库存明细表请求参数 {@link GoodsWareStockDetailByIdRequest}
	 * @return  库存明细表详情 {@link GoodsWareStockDetailByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/get-by-id")
    BaseResponse<GoodsWareStockDetailByIdResponse> getById(@RequestBody @Valid GoodsWareStockDetailByIdRequest goodsWareStockDetailByIdRequest);

}

