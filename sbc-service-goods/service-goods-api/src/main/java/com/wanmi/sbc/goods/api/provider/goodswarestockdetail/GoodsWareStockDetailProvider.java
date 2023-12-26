package com.wanmi.sbc.goods.api.provider.goodswarestockdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailAddRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailDelByIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailModifyRequest;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailAddResponse;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p> 库存明细表保存服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsWareStockDetailProvider")
public interface GoodsWareStockDetailProvider {

	/**
	 * 新增 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailAddRequest  库存明细表新增参数结构 {@link GoodsWareStockDetailAddRequest}
	 * @return 新增的 库存明细表信息 {@link GoodsWareStockDetailAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/add")
    BaseResponse<GoodsWareStockDetailAddResponse> add(@RequestBody @Valid GoodsWareStockDetailAddRequest goodsWareStockDetailAddRequest);

	/**
	 * 修改 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailModifyRequest  库存明细表修改参数结构 {@link GoodsWareStockDetailModifyRequest}
	 * @return 修改的 库存明细表信息 {@link GoodsWareStockDetailModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/modify")
    BaseResponse<GoodsWareStockDetailModifyResponse> modify(@RequestBody @Valid GoodsWareStockDetailModifyRequest goodsWareStockDetailModifyRequest);

	/**
	 * 单个删除 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailDelByIdRequest 单个删除参数结构 {@link GoodsWareStockDetailDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid GoodsWareStockDetailDelByIdRequest goodsWareStockDetailDelByIdRequest);

	/**
	 * 批量删除 库存明细表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDetailDelByIdListRequest 批量删除参数结构 {@link GoodsWareStockDetailDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestockdetail/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid GoodsWareStockDetailDelByIdListRequest goodsWareStockDetailDelByIdListRequest);

}

