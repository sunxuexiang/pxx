package com.wanmi.sbc.goods.api.provider.goodslabel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodslabel.*;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelAddResponse;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>导航配置保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsLabelProvider")
public interface GoodsLabelProvider {

	/**
	 * 新增导航配置API
	 *
	 * @author lvheng
	 * @param goodsLabelAddRequest 导航配置新增参数结构 {@link GoodsLabelAddRequest}
	 * @return 新增的导航配置信息 {@link GoodsLabelAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/add")
	BaseResponse<GoodsLabelAddResponse> add(@RequestBody @Valid GoodsLabelAddRequest goodsLabelAddRequest);

	/**
	 * 修改导航配置API
	 *
	 * @author lvheng
	 * @param goodsLabelModifyRequest 导航配置修改参数结构 {@link GoodsLabelModifyRequest}
	 * @return 修改的导航配置信息 {@link GoodsLabelModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/modify")
	BaseResponse<GoodsLabelModifyResponse> modify(@RequestBody @Valid GoodsLabelModifyRequest goodsLabelModifyRequest);


	/**
	 * 修改导航配置顺序API
	 *
	 * @author lvheng
	 * @param goodsLabelModifyRequest 导航配置修改参数结构 {@link GoodsLabelModifyRequest}
	 * @return 修改的导航配置信息 {@link GoodsLabelModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/modify-sort")
	BaseResponse modifySort(@RequestBody @Valid GoodsLabelModifySortRequest goodsLabelModifyRequest);

	/**
	 * 单个删除导航配置API
	 *
	 * @author lvheng
	 * @param goodsLabelDelByIdRequest 单个删除参数结构 {@link GoodsLabelDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid GoodsLabelDelByIdRequest goodsLabelDelByIdRequest);

	/**
	 * 批量删除导航配置API
	 *
	 * @author lvheng
	 * @param goodsLabelDelByIdListRequest 批量删除参数结构 {@link GoodsLabelDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodslabel/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid GoodsLabelDelByIdListRequest goodsLabelDelByIdListRequest);

}

