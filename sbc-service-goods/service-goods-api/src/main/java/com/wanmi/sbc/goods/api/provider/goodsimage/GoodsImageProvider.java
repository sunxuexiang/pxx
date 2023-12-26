package com.wanmi.sbc.goods.api.provider.goodsimage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goods.GoodsAddRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsAddResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsImageProvider")
public interface GoodsImageProvider {

	/**
	 * 新增特价商品
	 *
	 * @param request {@link GoodsAddRequest}
	 * @return 新增结果 {@link GoodsAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/getGoodsImagesByGoodsIds")
	BaseResponse<List<GoodsImageVO>> getGoodsImagesByGoodsIds(@RequestBody @Valid List<String> request);

	/**
	 * 水印图片
	 * 
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/watermark")
	BaseResponse<Integer> watermark();

}
