package com.wanmi.sbc.goods.api.provider.lastgoodswrite;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteAddRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteAddResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteModifyRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteModifyResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteDelByIdRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>用户最后一次商品记录保存服务Provider</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "LastGoodsWriteProvider")
public interface LastGoodsWriteProvider {

	/**
	 * 新增用户最后一次商品记录API
	 *
	 * @author 费传奇
	 * @param lastGoodsWriteAddRequest 用户最后一次商品记录新增参数结构 {@link LastGoodsWriteAddRequest}
	 * @return 新增的用户最后一次商品记录信息 {@link LastGoodsWriteAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/lastgoodswrite/add")
	BaseResponse<LastGoodsWriteAddResponse> add(@RequestBody @Valid LastGoodsWriteAddRequest lastGoodsWriteAddRequest);

	/**
	 * 修改用户最后一次商品记录API
	 *
	 * @author 费传奇
	 * @param lastGoodsWriteModifyRequest 用户最后一次商品记录修改参数结构 {@link LastGoodsWriteModifyRequest}
	 * @return 修改的用户最后一次商品记录信息 {@link LastGoodsWriteModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/lastgoodswrite/modify")
	BaseResponse<LastGoodsWriteModifyResponse> modify(@RequestBody @Valid LastGoodsWriteModifyRequest lastGoodsWriteModifyRequest);

	/**
	 * 单个删除用户最后一次商品记录API
	 *
	 * @author 费传奇
	 * @param lastGoodsWriteDelByIdRequest 单个删除参数结构 {@link LastGoodsWriteDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/lastgoodswrite/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid LastGoodsWriteDelByIdRequest lastGoodsWriteDelByIdRequest);



}

