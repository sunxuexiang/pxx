package com.wanmi.sbc.goods.api.provider.goodsrecommendgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐商品保存服务Provider</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}",contextId = "GoodsRecommendGoodsSaveProvider")
public interface GoodsRecommendGoodsSaveProvider {

	/**
	 * 新增商品推荐商品API
	 *
	 * @author chenyufei
	 * @param goodsRecommendGoodsAddRequest 商品推荐商品新增参数结构 {@link GoodsRecommendGoodsAddRequest}
	 * @return 新增的商品推荐商品信息 {@link GoodsRecommendGoodsAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/add")
    BaseResponse<GoodsRecommendGoodsAddResponse> add(@RequestBody @Valid GoodsRecommendGoodsAddRequest goodsRecommendGoodsAddRequest);

	/**
	 * 批量新增
	 * @param goodsRecommendGoodsBatchAddRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/batch-add")
	BaseResponse batachAdd(@RequestBody @Valid GoodsRecommendGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest);

	/**
	 * 修改商品推荐商品API
	 *
	 * @author chenyufei
	 * @param goodsRecommendGoodsModifyRequest 商品推荐商品修改参数结构 {@link GoodsRecommendGoodsModifyRequest}
	 * @return 修改的商品推荐商品信息 {@link GoodsRecommendGoodsModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/modify")
    BaseResponse<GoodsRecommendGoodsModifyResponse> modify(@RequestBody @Valid GoodsRecommendGoodsModifyRequest goodsRecommendGoodsModifyRequest);

	/**
	 * 单个删除商品推荐商品API
	 *
	 * @author chenyufei
	 * @param goodsRecommendGoodsDelByIdRequest 单个删除参数结构 {@link GoodsRecommendGoodsDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid GoodsRecommendGoodsDelByIdRequest goodsRecommendGoodsDelByIdRequest);

	/**
	 * 批量删除商品推荐商品API
	 *
	 * @author chenyufei
	 * @param goodsRecommendGoodsDelByIdListRequest 批量删除参数结构 {@link GoodsRecommendGoodsDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid GoodsRecommendGoodsDelByIdListRequest goodsRecommendGoodsDelByIdListRequest);

	/**
	 *
	 * @author chenyufei
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsrecommendgoods/delete-all")
    BaseResponse deleteAll(@RequestBody @Valid GoodsRecommendGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest);
}

