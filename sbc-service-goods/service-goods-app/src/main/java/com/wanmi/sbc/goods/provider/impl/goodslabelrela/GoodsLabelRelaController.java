package com.wanmi.sbc.goods.provider.impl.goodslabelrela;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodslabelrela.GoodsLabelRelaProvider;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaAddRequest;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaInGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaModifyRequest;
import com.wanmi.sbc.goods.api.response.goodslabelrela.GoodsLabelRelaAddResponse;
import com.wanmi.sbc.goods.api.response.goodslabelrela.GoodsLabelRelaModifyResponse;
import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import com.wanmi.sbc.goods.goodslabelrela.service.GoodsLabelRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>邀新统计保存服务接口实现</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@RestController
@Validated
public class GoodsLabelRelaController implements GoodsLabelRelaProvider {
	@Autowired
	private GoodsLabelRelaService goodsLabelRelaService;

	@Override
	public BaseResponse<GoodsLabelRelaAddResponse> add(@RequestBody @Valid GoodsLabelRelaAddRequest goodsLabelRelaAddRequest) {
		GoodsLabelRela goodsLabelRela = KsBeanUtil.convert(goodsLabelRelaAddRequest, GoodsLabelRela.class);
		return BaseResponse.success(new GoodsLabelRelaAddResponse(
				goodsLabelRelaService.wrapperVo(goodsLabelRelaService.add(goodsLabelRela))));
	}

	@Override
	public BaseResponse<GoodsLabelRelaModifyResponse> modify(@RequestBody @Valid GoodsLabelRelaModifyRequest goodsLabelRelaModifyRequest) {
		GoodsLabelRela goodsLabelRela = KsBeanUtil.convert(goodsLabelRelaModifyRequest, GoodsLabelRela.class);
		return BaseResponse.success(new GoodsLabelRelaModifyResponse(
				goodsLabelRelaService.wrapperVo(goodsLabelRelaService.modify(goodsLabelRela))));
	}

	@Override
	public BaseResponse deleteLabelInGoods(@Valid GoodsLabelRelaInGoodsIdsRequest request) {
		Integer integer = goodsLabelRelaService.deleteInGoodsIds(request.getGoodsIds(), request.getLabelId());
		return BaseResponse.SUCCESSFUL();
	}

}

