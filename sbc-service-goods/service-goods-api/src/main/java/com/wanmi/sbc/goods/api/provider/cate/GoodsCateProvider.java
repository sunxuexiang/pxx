package com.wanmi.sbc.goods.api.provider.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateAddRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateDeleteByIdRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateModifyRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateBatchModifySortRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateAddResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateDeleteByIdResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * com.wanmi.sbc.goods.api.provider.goodscate.GoodsCateProvider 商品分类增、删、改接口
 * 
 * @author lipeng
 * @dateTime 2018/11/1 下午3:08
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsCateProvider")
public interface GoodsCateProvider {

	/**
	 * 新增商品分类
	 *
	 * @param request {@link GoodsCateAddRequest}
	 * @return 新增结果 {@link GoodsCateAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/cate/add")
	BaseResponse<GoodsCateAddResponse> add(@RequestBody @Valid GoodsCateAddRequest request);

	/**
	 * 修改商品分类
	 *
	 * @param request {@link GoodsCateModifyRequest}
	 * @return 修改结果 {@link GoodsCateModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/cate/modify")
	BaseResponse<GoodsCateModifyResponse> modify(@RequestBody @Valid GoodsCateModifyRequest request);

	/**
	 * 根据编号删除商品分类
	 *
	 * @param request {@link GoodsCateDeleteByIdRequest}
	 * @return 删除的分类编号列表 {@link GoodsCateDeleteByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/cate/delete-by-id")
	BaseResponse<GoodsCateDeleteByIdResponse> deleteById(@RequestBody @Valid GoodsCateDeleteByIdRequest request);

	/**
	 * 批量修改分类排序
	 *
	 * @param request 批量分类排序信息结构 {@link GoodsCateBatchModifySortRequest}
	 * @return 操作结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/cate/batch-modify-sort")
	BaseResponse batchModifySort(@RequestBody @Valid GoodsCateBatchModifySortRequest request);

	/**
	 * 批量修改分类排序
	 *
	 * @param request 批量分类排序信息结构 {@link GoodsCateBatchModifySortRequest}
	 * @return 操作结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/cate/batch-recommend-modify-sort")
	BaseResponse batchRecommendModifySort(@RequestBody @Valid GoodsCateBatchModifySortRequest request);

	/**
	 * 初始化商品分类信息
	 *
	 */
	@PostMapping("/goods/${application.goods.version}/cate/init")
	BaseResponse init();

	/**
	 * 同步成长值购物规则为积分购物规则
	 *
	 */
	@PostMapping("/goods/${application.goods.version}/cate/synchronize-points-rule")
	BaseResponse synchronizePointsRule();

	/**
	 * 新增推荐商品分类
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/cate/add-recommend")
	BaseResponse addGoodsCateRecommend(@RequestBody @Valid GoodsCateAddRequest request);

	/**
	 * 删除推荐商品分类
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/cate/del-recommend")
	BaseResponse delGoodsCateRecommend(@RequestBody @Valid GoodsCateAddRequest request);

	/**
	 * 新增散批推荐商品分类
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/cate/add-retail-recommend")
	BaseResponse addRetailGoodsCateRecommend(@RequestBody @Valid GoodsCateAddRequest request);

	/**
	 * 删除散批推荐商品分类
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/cate/del-retail-recommend")
	BaseResponse delRetailGoodsCateRecommend(@RequestBody @Valid GoodsCateAddRequest request);

	/**
	 * 批量修改散批推荐分类排序
	 *
	 * @param request 批量分类排序信息结构 {@link GoodsCateBatchModifySortRequest}
	 * @return 操作结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/cate/retail-commend-batch-modify-sort")
	BaseResponse batchRetailRecommendModifySort(@RequestBody @Valid GoodsCateBatchModifySortRequest request);
}
