package com.wanmi.sbc.goods.api.provider.lastgoodswrite;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWritePageRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWritePageResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteListRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteListResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteByIdRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>用户最后一次商品记录查询服务Provider</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "LastGoodsWriteQueryProvider")
public interface LastGoodsWriteQueryProvider {

	/**
	 * 分页查询用户最后一次商品记录API
	 *
	 * @author 费传奇
	 * @param lastGoodsWritePageReq 分页请求参数和筛选对象 {@link LastGoodsWritePageRequest}
	 * @return 用户最后一次商品记录分页列表信息 {@link LastGoodsWritePageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/lastgoodswrite/page")
	BaseResponse<LastGoodsWritePageResponse> page(@RequestBody @Valid LastGoodsWritePageRequest lastGoodsWritePageReq);

	/**
	 * 列表查询用户最后一次商品记录API
	 *
	 * @author 费传奇
	 * @param lastGoodsWriteListReq 列表请求参数和筛选对象 {@link LastGoodsWriteListRequest}
	 * @return 用户最后一次商品记录的列表信息 {@link LastGoodsWriteListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/lastgoodswrite/list")
	BaseResponse<LastGoodsWriteListResponse> list(@RequestBody @Valid LastGoodsWriteListRequest lastGoodsWriteListReq);

	/**
	 * 单个查询用户最后一次商品记录API
	 *
	 * @author 费传奇
	 * @param lastGoodsWriteByIdRequest 单个查询用户最后一次商品记录请求参数 {@link LastGoodsWriteByIdRequest}
	 * @return 用户最后一次商品记录详情 {@link LastGoodsWriteByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/lastgoodswrite/get-by-id")
	BaseResponse<LastGoodsWriteByIdResponse> getById(@RequestBody @Valid LastGoodsWriteByIdRequest lastGoodsWriteByIdRequest);

}

