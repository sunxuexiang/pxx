package com.wanmi.sbc.goods.api.provider.goodslabelrela;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaAddRequest;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaInGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.goodslabelrela.GoodsLabelRelaModifyRequest;
import com.wanmi.sbc.goods.api.response.goodslabelrela.GoodsLabelRelaAddResponse;
import com.wanmi.sbc.goods.api.response.goodslabelrela.GoodsLabelRelaModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>邀新统计保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsLabelRelaProvider")
public interface GoodsLabelRelaProvider {

	/**
	 * 新增邀新统计API
	 *
	 * @author lvheng
	 * @param goodsLabelRelaAddRequest 邀新统计新增参数结构 {@link GoodsLabelRelaAddRequest}
	 * @return 新增的邀新统计信息 {@link GoodsLabelRelaAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/add")
	BaseResponse<GoodsLabelRelaAddResponse> add(@RequestBody @Valid GoodsLabelRelaAddRequest goodsLabelRelaAddRequest);

	/**
	 * 修改邀新统计API
	 *
	 * @author lvheng
	 * @param goodsLabelRelaModifyRequest 邀新统计修改参数结构 {@link GoodsLabelRelaModifyRequest}
	 * @return 修改的邀新统计信息 {@link GoodsLabelRelaModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/modify")
	BaseResponse<GoodsLabelRelaModifyResponse> modify(@RequestBody @Valid GoodsLabelRelaModifyRequest goodsLabelRelaModifyRequest);

	/**
	 * 删除制定的商品关联标签关系
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabelrela/del-in-goodsids")
	BaseResponse deleteLabelInGoods(@RequestBody @Valid GoodsLabelRelaInGoodsIdsRequest request);
}

