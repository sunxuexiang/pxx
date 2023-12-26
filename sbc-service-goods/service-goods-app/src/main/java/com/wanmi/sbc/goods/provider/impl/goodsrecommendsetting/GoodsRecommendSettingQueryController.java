package com.wanmi.sbc.goods.provider.impl.goodsrecommendsetting;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsrecommendsetting.GoodsRecommendSettingQueryProvider;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingListRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingPageRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingPageResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsCacheService;
import com.wanmi.sbc.goods.goodsrecommendgoods.service.GoodsRecommendGoodsService;
import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import com.wanmi.sbc.goods.goodsrecommendsetting.service.GoodsRecommendSettingCacheService;
import com.wanmi.sbc.goods.goodsrecommendsetting.service.GoodsRecommendSettingService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品推荐配置查询服务接口实现</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@RestController
@Validated
public class GoodsRecommendSettingQueryController implements GoodsRecommendSettingQueryProvider {

	@Autowired
	private GoodsRecommendSettingService goodsRecommendSettingService;

	@Autowired
	private GoodsRecommendGoodsService goodsRecommendGoodsService;

	@Autowired
	private GoodsRecommendSettingCacheService goodsRecommendSettingCacheService;

	@Autowired
	private GoodsRecommendGoodsCacheService goodsRecommendGoodsCacheService;



	@Override
	public BaseResponse<GoodsRecommendSettingPageResponse> page(@RequestBody @Valid GoodsRecommendSettingPageRequest goodsRecommendSettingPageReq) {
		GoodsRecommendSettingQueryRequest queryReq = new GoodsRecommendSettingQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendSettingPageReq, queryReq);
		Page<GoodsRecommendSetting> goodsRecommendSettingPage = goodsRecommendSettingService.page(queryReq);
		Page<GoodsRecommendSettingVO> newPage = goodsRecommendSettingPage.map(entity -> goodsRecommendSettingService.wrapperVo(entity));
		MicroServicePage<GoodsRecommendSettingVO> microPage = new MicroServicePage<>(newPage, goodsRecommendSettingPageReq.getPageable());
		GoodsRecommendSettingPageResponse finalRes = new GoodsRecommendSettingPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<GoodsRecommendSettingListResponse> list(@RequestBody @Valid GoodsRecommendSettingListRequest goodsRecommendSettingListReq) {
		GoodsRecommendSettingQueryRequest queryReq = new GoodsRecommendSettingQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendSettingListReq, queryReq);
		List<GoodsRecommendSetting> goodsRecommendSettingList = goodsRecommendSettingService.list(queryReq);
		List<GoodsRecommendSettingVO> newList = goodsRecommendSettingList.stream().map(entity -> goodsRecommendSettingService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsRecommendSettingListResponse(newList));
	}

	@Override
	public BaseResponse<GoodsRecommendSettingByIdResponse> getById(@RequestBody @Valid GoodsRecommendSettingByIdRequest goodsRecommendSettingByIdRequest) {
		GoodsRecommendSetting goodsRecommendSetting = goodsRecommendSettingService.getById(goodsRecommendSettingByIdRequest.getSettingId());
		return BaseResponse.success(new GoodsRecommendSettingByIdResponse(goodsRecommendSettingService.wrapperVo(goodsRecommendSetting)));
	}

	/**
	 * 单个查询商品推荐配置API
	 *
	 * @return 商品推荐配置详情 {@link GoodsRecommendSettingByIdResponse}
	 * @author chenyufei
	 */
	@Override
	public BaseResponse<GoodsRecommendSettingResponse> getSetting(Long wareId) {
		GoodsRecommendSettingResponse response = goodsRecommendSettingCacheService.querySettingCache();
		List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = response.getGoodsRecommendSettingVOS();
		if (CollectionUtils.isNotEmpty(goodsRecommendSettingVOS)) {
			for (GoodsRecommendSettingVO goodsRecommendSettingVO : goodsRecommendSettingVOS) {
				//手动推荐菜且是策略开启情况查询商品信息
				if( BoolFlag.NO.equals(goodsRecommendSettingVO.getIsIntelligentRecommend())){
					goodsRecommendSettingVO.setGoodsInfoIds(goodsRecommendGoodsCacheService.queryRecommendGoodsCache(wareId).getGoodsInfoIds());
				}
			}

		}
		return BaseResponse.success(response);
	}

}

