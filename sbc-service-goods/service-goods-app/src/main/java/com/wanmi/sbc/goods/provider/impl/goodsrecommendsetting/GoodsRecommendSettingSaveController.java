package com.wanmi.sbc.goods.provider.impl.goodsrecommendsetting;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsrecommendsetting.GoodsRecommendSettingSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingAddResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingModifyResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import com.wanmi.sbc.goods.goodsrecommendsetting.service.GoodsRecommendSettingCacheService;
import com.wanmi.sbc.goods.goodsrecommendsetting.service.GoodsRecommendSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>商品推荐配置保存服务接口实现</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@RestController
@Validated
public class GoodsRecommendSettingSaveController implements GoodsRecommendSettingSaveProvider {

	@Autowired
	private GoodsRecommendSettingService goodsRecommendSettingService;

	@Autowired
	private GoodsRecommendSettingCacheService goodsRecommendSettingCacheService;

	@Override
	public BaseResponse<GoodsRecommendSettingAddResponse> add(@RequestBody @Valid GoodsRecommendSettingAddRequest goodsRecommendSettingAddRequest) {
		GoodsRecommendSetting goodsRecommendSetting = new GoodsRecommendSetting();

		KsBeanUtil.copyPropertiesThird(goodsRecommendSettingAddRequest, goodsRecommendSetting);

		GoodsRecommendSettingVO goodsRecommendSettingVO = goodsRecommendSettingService.wrapperVo(goodsRecommendSettingService.add(goodsRecommendSetting));

		goodsRecommendSettingCacheService.saveSetting(GoodsRecommendSettingResponse.builder().goodsRecommendSettingVO(goodsRecommendSettingVO).build());
		goodsRecommendSettingCacheService.delPageSettingCache();
		return BaseResponse.success(GoodsRecommendSettingAddResponse.builder().goodsRecommendSettingVO(goodsRecommendSettingVO).build());
	}

	@Override
	public BaseResponse<GoodsRecommendSettingModifyResponse> modify(@RequestBody @Valid GoodsRecommendSettingModifyRequest goodsRecommendSettingModifyRequest) {
		GoodsRecommendSetting goodsRecommendSetting = new GoodsRecommendSetting();

		KsBeanUtil.copyPropertiesThird(goodsRecommendSettingModifyRequest, goodsRecommendSetting);

		List<GoodsRecommendSetting> goodsRecommendSettings = goodsRecommendSettingService.modify(goodsRecommendSetting);
		List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = KsBeanUtil.convertList(goodsRecommendSettings,
				GoodsRecommendSettingVO.class);


		goodsRecommendSettingCacheService.saveSetting(GoodsRecommendSettingResponse.builder().goodsRecommendSettingVOS(goodsRecommendSettingVOS).build());
		goodsRecommendSettingCacheService.delPageSettingCache();
		return BaseResponse.success(GoodsRecommendSettingModifyResponse.builder().goodsRecommendSettingVOS(goodsRecommendSettingVOS).build());
	}


	@Override
	public BaseResponse<GoodsRecommendSettingModifyResponse> modifyStrategy(@RequestBody @Valid GoodsRecommendSettingModifyStrategyRequest goodsRecommendSettingModifyRequest) {

		List<GoodsRecommendSetting> goodsRecommendSettings =
				goodsRecommendSettingService.modifyStrategy(goodsRecommendSettingModifyRequest.getIsOPenIntelligentStrategy());

		List<GoodsRecommendSettingVO> goodsRecommendSettingVOS = KsBeanUtil.convertList(goodsRecommendSettings,
				GoodsRecommendSettingVO.class);

		goodsRecommendSettingCacheService.saveSetting(GoodsRecommendSettingResponse.builder().goodsRecommendSettingVOS(goodsRecommendSettingVOS).build());
		goodsRecommendSettingCacheService.delPageSettingCache();
		return BaseResponse.success(GoodsRecommendSettingModifyResponse.builder().goodsRecommendSettingVOS(goodsRecommendSettingVOS).build());
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid GoodsRecommendSettingDelByIdRequest goodsRecommendSettingDelByIdRequest) {
		goodsRecommendSettingService.deleteById(goodsRecommendSettingDelByIdRequest.getSettingId());
		goodsRecommendSettingCacheService.delPageSettingCache();
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid GoodsRecommendSettingDelByIdListRequest goodsRecommendSettingDelByIdListRequest) {
		goodsRecommendSettingService.deleteByIdList(goodsRecommendSettingDelByIdListRequest.getSettingIdList());
		goodsRecommendSettingCacheService.delPageSettingCache();
		return BaseResponse.SUCCESSFUL();
	}

}

