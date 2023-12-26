package com.wanmi.sbc.goods.api.provider.goodswarestock;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockAddResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>sku分仓库存表保存服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsWareStockProvider")
public interface GoodsWareStockProvider {

	/**
	 * 新增sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockAddRequest sku分仓库存表新增参数结构 {@link GoodsWareStockAddRequest}
	 * @return 新增的sku分仓库存表信息 {@link GoodsWareStockAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/add")
    BaseResponse<GoodsWareStockAddResponse> add(@RequestBody @Valid GoodsWareStockAddRequest goodsWareStockAddRequest);

	/**
	 * 批量新增sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockAddListRequest sku分仓库存表批量新增参数结构 {@link GoodsWareStockAddListRequest}
	 * @return 新增的sku分仓库存表信息 {@link GoodsWareStockAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/addlist")
    BaseResponse<GoodsWareStockAddResponse> addList(@RequestBody @Valid GoodsWareStockAddListRequest goodsWareStockAddListRequest);

	/**
	 * 修改sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockModifyRequest sku分仓库存表修改参数结构 {@link GoodsWareStockModifyRequest}
	 * @return 修改的sku分仓库存表信息 {@link GoodsWareStockModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/modify")
    BaseResponse<GoodsWareStockModifyResponse> modify(@RequestBody @Valid GoodsWareStockModifyRequest goodsWareStockModifyRequest);

	/**
	 * 单个删除sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDelByIdRequest 单个删除参数结构 {@link GoodsWareStockDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid GoodsWareStockDelByIdRequest goodsWareStockDelByIdRequest);

	/**
	 * 批量删除sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockDelByIdListRequest 批量删除参数结构 {@link GoodsWareStockDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid GoodsWareStockDelByIdListRequest goodsWareStockDelByIdListRequest);

	/**
	 * 批量更新sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockAddListRequest sku分仓库存表批量更新参数结构 {@link GoodsWareStockUpdateListRequest}
	 * @return 新增的sku分仓库存表信息 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/updateList")
	BaseResponse updateList(@RequestBody @Valid GoodsWareStockUpdateListRequest goodsWareStockAddListRequest);

	@PostMapping("/goods/${application.goods.version}/goodswarestock/subStock")
	BaseResponse subStock(@RequestBody @Valid GoodsWareStockUpdateRequest goodsWareStockRequest);

	@PostMapping("/goods/${application.goods.version}/goodswarestock/addStock")
	BaseResponse addStock(@RequestBody @Valid GoodsWareStockUpdateRequest goodsWareStockRequest);
}

