package com.wanmi.sbc.goods.api.provider.relationgoodsimages;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goods.GoodsImageTypeAddRequest;
import com.wanmi.sbc.goods.bean.vo.RelationGoodsImagesVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "RelationGoodsImagesProvider")
public interface RelationGoodsImagesProvider {


	@PostMapping("/goods/${application.goods.version}/getRelationByGoodsId")
	BaseResponse<RelationGoodsImagesVO> getRelationByGoodsId(@RequestBody @Valid String goodsid);

	@PostMapping("/goods/${application.goods.version}/addList")
	BaseResponse addList(@RequestBody @Valid GoodsImageTypeAddRequest goodsImageTypeAddRequest);

}
