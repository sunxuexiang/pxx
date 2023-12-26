package com.wanmi.sbc.goods.api.provider.merchantconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.*;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantRecommendGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsModifyResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsListResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐商品保存服务Provider</p>
 * @author sgy
 * @date 2013-06-07 10:53:36
 */
@FeignClient(value = "${application.goods.name}", url = "${feign.url.goods:#{null}}", contextId = "MerchantConfigGoodsSaveProvider")
public interface MerchantConfigGoodsSaveProvider {

	/**
	 * 新增商品推荐商品API
	 *
	 * @author sgy
	 * @param goodsRecommendGoodsAddRequest 商品推荐商品新增参数结构 {@link MerchantConfigGoodsAddRequest}
	 * @return 新增的商品推荐商品信息 {@link MerchantConfigGoodsAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/add")
    BaseResponse<MerchantConfigGoodsAddResponse> add(@RequestBody @Valid MerchantConfigGoodsAddRequest goodsRecommendGoodsAddRequest);

	/**
	 * 批量新增
	 * @param goodsRecommendGoodsBatchAddRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/batch-add")
	BaseResponse batachAdd(@RequestBody @Valid MerchantConfigGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest);

	/**
	 * 修改商品推荐商品API
	 *
	 * @author sgy
	 * @param merchantConfigGoodsModifyRequest 商品推荐商品修改参数结构 {@link MerchantConfigGoodsModifyRequest}
	 * @return 修改的商品推荐商品信息 {@link MerchantConfigGoodsModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/modify")
    BaseResponse<MerchantConfigGoodsModifyResponse> modify(@RequestBody @Valid MerchantConfigGoodsModifyRequest merchantConfigGoodsModifyRequest);

	/**
	 * 单个删除商品推荐商品API
	 *
	 * @author sgy
	 * @param goodsRecommendGoodsDelByIdRequest 单个删除参数结构 {@link MerchantConfigGoodsDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid MerchantConfigGoodsDelByIdRequest goodsRecommendGoodsDelByIdRequest);

	/**
	 * 批量删除商品推荐商品API
	 *
	 * @author sgy
	 * @param goodsRecommendGoodsDelByIdListRequest 批量删除参数结构 {@link MerchantConfigGoodsDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid MerchantConfigGoodsDelByIdListRequest goodsRecommendGoodsDelByIdListRequest);

	/**
	 *
	 * @author sgy
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/delete-all")
    BaseResponse deleteAll(@RequestBody @Valid MerchantConfigGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest);
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/sort-merchant-goods")
    BaseResponse sortMerchantGoods(@RequestBody @Valid MerchantGoodsSortRequest addReq);
	/**
	 * 列表查询商品推荐商品API
	 *
	 * @param merchantConfigGoodsListRequest 列表请求参数和筛选对象 {@link GoodsRecommendGoodsListRequest}
	 * @return 商品推荐商品的列表信息 {@link GoodsRecommendGoodsListResponse}
	 * @author sgy
	 */
	@PostMapping("/goods/${application.goods.version}/merchantConfigGoods/recommend-redis")
	BaseResponse <MerchantConfigGoodsListResponse> recommendRedis(@RequestBody @Valid MerchantConfigGoodsQueryRequest merchantConfigGoodsListRequest  );

	@PostMapping("/goods/${application.goods.version}/sortMerchantRecommendGoods/sort-merchant-goods")
	BaseResponse sortMerchantRecommendGoods(@RequestBody @Valid MerchantRecommendGoodsSortRequest request);
}

