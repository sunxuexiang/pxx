package com.wanmi.sbc.goods.provider.impl.goodsrecommendgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsModifyResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsResponse;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsCacheService;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品推荐商品保存服务接口实现</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@RestController
@Validated
public class GoodsRecommendGoodsSaveController implements GoodsRecommendGoodsSaveProvider {
	@Autowired
	private GoodsRecommendGoodsService goodsRecommendGoodsService;

	@Autowired
	private GoodsRecommendGoodsCacheService goodsRecommendGoodsCacheService;

	@Override
	public BaseResponse<GoodsRecommendGoodsAddResponse> add(@RequestBody @Valid GoodsRecommendGoodsAddRequest goodsRecommendGoodsAddRequest) {
		GoodsRecommendGoods goodsRecommendGoods = new GoodsRecommendGoods();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsAddRequest, goodsRecommendGoods);
		return BaseResponse.success(GoodsRecommendGoodsAddResponse.builder().goodsRecommendGoodsVO(goodsRecommendGoodsService.wrapperVo(goodsRecommendGoodsService.add(goodsRecommendGoods))).build());
	}

	@Override
	public BaseResponse batachAdd(@RequestBody @Valid GoodsRecommendGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest) {
		List<String> goodsInfoIds = Arrays.asList(goodsRecommendGoodsBatchAddRequest.getGoodsInfoId());
		goodsInfoIds.forEach(goodsInfoId->{
			this.add(GoodsRecommendGoodsAddRequest.builder().goodsInfoId(goodsInfoId).wareId(goodsRecommendGoodsBatchAddRequest.getWareId()).build());
		});
		goodsRecommendGoodsCacheService.saveRecommendGoods(GoodsRecommendGoodsResponse.builder().goodsInfoIds(goodsInfoIds).wareId(goodsRecommendGoodsBatchAddRequest.getWareId()).build());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<GoodsRecommendGoodsModifyResponse> modify(@RequestBody @Valid GoodsRecommendGoodsModifyRequest goodsRecommendGoodsModifyRequest) {
		GoodsRecommendGoods goodsRecommendGoods = new GoodsRecommendGoods();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsModifyRequest, goodsRecommendGoods);
		return BaseResponse.success(new GoodsRecommendGoodsModifyResponse(
				goodsRecommendGoodsService.wrapperVo(goodsRecommendGoodsService.modify(goodsRecommendGoods))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid GoodsRecommendGoodsDelByIdRequest goodsRecommendGoodsDelByIdRequest) {
		goodsRecommendGoodsService.deleteById(goodsRecommendGoodsDelByIdRequest.getRecommendId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid GoodsRecommendGoodsDelByIdListRequest goodsRecommendGoodsDelByIdListRequest) {
		goodsRecommendGoodsService.deleteByIdList(goodsRecommendGoodsDelByIdListRequest.getRecommendIdList());
		return BaseResponse.SUCCESSFUL();
	}

	/**
	 * @return 删除结果 {@link BaseResponse}
	 * @author chenyufei
	 */
	@Override
	public BaseResponse deleteAll(@RequestBody @Valid GoodsRecommendGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest) {
		//需要保存的skuIds
		List<String> goodsInfoIds = Arrays.asList(goodsRecommendGoodsBatchAddRequest.getGoodsInfoId());
		List<GoodsRecommendGoods> goodsRecommendGoods = goodsRecommendGoodsService.findByWareId(goodsRecommendGoodsBatchAddRequest.getWareId());
		//已保存的skuIs
		List<String> skuIds = goodsRecommendGoods.stream().map(GoodsRecommendGoods::getGoodsInfoId).collect(Collectors.toList());
		//被删除的skuIds
		List<String> collect = skuIds.stream().filter(i -> goodsInfoIds.stream().noneMatch(g->g.equals(i))).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(collect)) {
			goodsRecommendGoodsService.clearRecommendSort(collect);

		}
		goodsRecommendGoodsService.deleteByIdList(goodsRecommendGoods.stream().map(GoodsRecommendGoods::getRecommendId).collect(Collectors.toList()));
		goodsRecommendGoodsCacheService.deleteRecommendGoodsCache(goodsRecommendGoodsBatchAddRequest.getWareId());
		return BaseResponse.SUCCESSFUL();
	}

}

