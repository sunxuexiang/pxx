package com.wanmi.sbc.goods.provider.impl.goodsimage;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.goodsimage.GoodsImageProvider;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.images.service.GoodsImageService;

@RestController
@Validated
public class GoodsImageController implements GoodsImageProvider {

	@Autowired
	private GoodsImageService goodsImageService;

	@Override
	public BaseResponse<List<GoodsImageVO>> getGoodsImagesByGoodsIds(@Valid List<String> request) {
		return BaseResponse.success(goodsImageService.findByGoodsIds(request));
	}

	@Override
	public BaseResponse<Integer> watermark() {
		return BaseResponse.success(goodsImageService.watermark());
	}

}
