package com.wanmi.sbc.goods.api.provider.goodstypeconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.*;
import com.wanmi.sbc.goods.api.request.merchantconfig.*;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigAddResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigModifyResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsAddResponse;
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
@FeignClient(value = "${application.goods.name}",url="${feign.url.goods:#{null}}",contextId = "goodsTypeGoodsSaveProvider")
public interface GoodsTypeGoodsSaveProvider {

	/**
	 * 新增商品推荐商品API
	 *
	 * @author sgy
	 * @param goodsRecommendGoodsAddRequest 商品推荐商品新增参数结构 {@link MerchantConfigGoodsAddRequest}
	 * @return 新增的商品推荐商品信息 {@link MerchantConfigGoodsAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/add")
	BaseResponse<MerchantTypeConfigAddResponse> add(@RequestBody @Valid MerchantTypeConfigAddRequest goodsRecommendGoodsAddRequest);

	/**
	 * 批量新增
	 * @param goodsRecommendGoodsBatchAddRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/batch-add")
	BaseResponse batachAdd(@RequestBody @Valid MerchantTypeConfigBatchAddRequest goodsRecommendGoodsBatchAddRequest);

	/**
	 * 修改商品推荐商品API
	 *
	 * @author sgy
	 * @param merchantConfigGoodsModifyRequest 商品推荐商品修改参数结构 {@link MerchantConfigGoodsModifyRequest}
	 * @return 修改的商品推荐商品信息 {@link MerchantTypeConfigModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/modify")
	BaseResponse<MerchantTypeConfigModifyResponse> modify(@RequestBody @Valid MerchantTypeConfigModifyRequest merchantConfigGoodsModifyRequest);

	/**
	 * 单个删除商品推荐商品API
	 *
	 * @author sgy
	 * @param goodsRecommendGoodsDelByIdRequest 单个删除参数结构 {@link MerchantConfigGoodsDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid MerchantConfigGoodsDelByIdRequest goodsRecommendGoodsDelByIdRequest);

	/**
	 * 批量删除商品推荐商品API
	 *
	 * @author sgy
	 * @param goodsRecommendGoodsDelByIdListRequest 批量删除参数结构 {@link MerchantConfigGoodsDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid MerchantConfigGoodsDelByIdListRequest goodsRecommendGoodsDelByIdListRequest);

	/**
	 *
	 * @author sgy
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/delete-all")
	BaseResponse deleteAll(@RequestBody @Valid MerchantTypeConfigBatchAddRequest goodsRecommendGoodsBatchAddRequest);
	/**
	 *排序
	 * @author sgy
	 * @return 结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/sort")
	BaseResponse sortMerchantType(@RequestBody @Valid MerchantTypeSortRequest addReq);


	@PostMapping("/goods/${application.goods.version}/goodsTypeGoods/sort-recommend")
	BaseResponse sortMerchantRecommendCat(@RequestBody @Valid MerchantRecommendCatSortRequest request);
}

