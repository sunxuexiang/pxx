package com.wanmi.sbc.goods.api.provider.goodslabelrela;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodslabelrela.*;
import com.wanmi.sbc.goods.api.response.goodslabelrela.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品标签关联查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsLabelRelaQueryProvider")
public interface GoodsLabelRelaQueryProvider {

	/**
	 * 分页查询商品标签关联API
	 *
	 * @author lvheng
	 * @param goodsLabelRelaPageReq 分页请求参数和筛选对象 {@link GoodsLabelRelaPageRequest}
	 * @return 商品标签关联分页列表信息 {@link GoodsLabelRelaPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/page")
	BaseResponse<GoodsLabelRelaPageResponse> page(@RequestBody @Valid GoodsLabelRelaPageRequest goodsLabelRelaPageReq);

	/**
	 * 列表查询商品标签关联API
	 *
	 * @author lvheng
	 * @param goodsLabelRelaListReq 列表请求参数和筛选对象 {@link GoodsLabelRelaListRequest}
	 * @return 商品标签关联的列表信息 {@link GoodsLabelRelaListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/list")
	BaseResponse<GoodsLabelRelaListResponse> list(@RequestBody @Valid GoodsLabelRelaListRequest goodsLabelRelaListReq);

	/**
	 * 单个查询商品标签关联API
	 *
	 * @author lvheng
	 * @param goodsLabelRelaByIdRequest 单个查询商品标签关联请求参数 {@link GoodsLabelRelaByIdRequest}
	 * @return 商品标签关联详情 {@link GoodsLabelRelaByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/get-by-id")
	BaseResponse<GoodsLabelRelaByIdResponse> getById(@RequestBody @Valid GoodsLabelRelaByIdRequest goodsLabelRelaByIdRequest);

	/**
	 * 根据标签ID查询关联商品对象
	 * @param goodsLabelRelaByIdRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/get-label-id")
	 BaseResponse<GoodsLabelRelaByLabelIdResponse> findByLabelId(@RequestBody @Valid GoodsLabelRelaByLabelIdRequest goodsLabelRelaByIdRequest);

	/**
	 * 根据商品ID查询关联的标签数据
	 * @param goodsLabelRelaByIdRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/get-goods-id")
	BaseResponse<GoodsLabelRelaByGoodsIdsResponse> findByGoodsIds(@RequestBody @Valid GoodsLabelRelaByGoodsIdsRequest goodsLabelRelaByIdRequest);
}

