package com.wanmi.sbc.goods.provider.impl.goodsImagestype;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsImagestype.GoodsImageStypeProvider;
import com.wanmi.sbc.goods.api.provider.goodsimage.GoodsImageProvider;
import com.wanmi.sbc.goods.bean.vo.GoodsImageStypeVO;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.goodsimagestype.model.root.GoodsImageStype;
import com.wanmi.sbc.goods.goodsimagestype.repository.GoodsImagestypeRepository;
import com.wanmi.sbc.goods.images.service.GoodsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class GoodsImageStypeController implements GoodsImageStypeProvider {
	@Autowired
	private GoodsImagestypeRepository goodsImagestypeRepository;


	@Override
	public BaseResponse<List<GoodsImageStypeVO>> getHcImageByGoodsIds(List<String> request) {
		List<GoodsImageStype> byGoodsIdsAndHc = goodsImagestypeRepository.findByGoodsIdsAndHc(request);
		return BaseResponse.success(KsBeanUtil.convert(byGoodsIdsAndHc,GoodsImageStypeVO.class));
	}
}
