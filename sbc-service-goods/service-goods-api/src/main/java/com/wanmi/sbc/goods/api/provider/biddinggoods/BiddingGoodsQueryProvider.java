package com.wanmi.sbc.goods.api.provider.biddinggoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsPageRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsPageResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsListRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsListResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>竞价商品查询服务Provider</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BiddingGoodsQueryProvider")
public interface BiddingGoodsQueryProvider {

	/**
	 * 分页查询竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsPageReq 分页请求参数和筛选对象 {@link BiddingGoodsPageRequest}
	 * @return 竞价商品分页列表信息 {@link BiddingGoodsPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/page")
	BaseResponse<BiddingGoodsPageResponse> page(@RequestBody @Valid BiddingGoodsPageRequest biddingGoodsPageReq);

	/**
	 * 列表查询竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsListReq 列表请求参数和筛选对象 {@link BiddingGoodsListRequest}
	 * @return 竞价商品的列表信息 {@link BiddingGoodsListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/list")
	BaseResponse<BiddingGoodsListResponse> list(@RequestBody @Valid BiddingGoodsListRequest biddingGoodsListReq);

	/**
	 * 单个查询竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsByIdRequest 单个查询竞价商品请求参数 {@link BiddingGoodsByIdRequest}
	 * @return 竞价商品详情 {@link BiddingGoodsByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/get-by-id")
	BaseResponse<BiddingGoodsByIdResponse> getById(@RequestBody @Valid BiddingGoodsByIdRequest biddingGoodsByIdRequest);

}

