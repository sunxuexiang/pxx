package com.wanmi.sbc.goods.provider.impl.goodsrecommendgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsListRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendGoodsVO;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品推荐商品查询服务接口实现</p>
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@RestController
@Validated
public class GoodsRecommendGoodsQueryController implements GoodsRecommendGoodsQueryProvider {
	@Autowired
	private GoodsRecommendGoodsService goodsRecommendGoodsService;

	@Override
	public BaseResponse<GoodsRecommendGoodsPageResponse> page(@RequestBody @Valid GoodsRecommendGoodsPageRequest goodsRecommendGoodsPageReq) {
		GoodsRecommendGoodsQueryRequest queryReq = new GoodsRecommendGoodsQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsPageReq, queryReq);
		Page<GoodsRecommendGoods> goodsRecommendGoodsPage = goodsRecommendGoodsService.page(queryReq);
		Page<GoodsRecommendGoodsVO> newPage = goodsRecommendGoodsPage.map(entity -> goodsRecommendGoodsService.wrapperVo(entity));
		MicroServicePage<GoodsRecommendGoodsVO> microPage = new MicroServicePage<>(newPage, goodsRecommendGoodsPageReq.getPageable());
		GoodsRecommendGoodsPageResponse finalRes = new GoodsRecommendGoodsPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<GoodsRecommendGoodsListResponse> list(@RequestBody @Valid GoodsRecommendGoodsListRequest goodsRecommendGoodsListReq) {
		GoodsRecommendGoodsQueryRequest queryReq = new GoodsRecommendGoodsQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsListReq, queryReq);
		List<GoodsRecommendGoods> goodsRecommendGoodsList = goodsRecommendGoodsService.list(queryReq);
		List<GoodsRecommendGoodsVO> newList = goodsRecommendGoodsList.stream().map(entity -> goodsRecommendGoodsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsRecommendGoodsListResponse(newList));
	}

	@Override
	public BaseResponse<GoodsRecommendGoodsByIdResponse> getById(@RequestBody @Valid GoodsRecommendGoodsByIdRequest goodsRecommendGoodsByIdRequest) {
		GoodsRecommendGoods goodsRecommendGoods = goodsRecommendGoodsService.getById(goodsRecommendGoodsByIdRequest.getRecommendId());
		return BaseResponse.success(new GoodsRecommendGoodsByIdResponse(goodsRecommendGoodsService.wrapperVo(goodsRecommendGoods)));
	}

}

