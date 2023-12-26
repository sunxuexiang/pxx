package com.wanmi.sbc.goods.api.provider.bidding;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.bidding.*;
import com.wanmi.sbc.goods.api.response.bidding.BiddingPageResponse;
import com.wanmi.sbc.goods.api.response.bidding.BiddingListResponse;
import com.wanmi.sbc.goods.api.response.bidding.BiddingByIdResponse;
import com.wanmi.sbc.goods.api.response.bidding.BiddingValidateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>竞价配置查询服务Provider</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BiddingQueryProvider")
public interface BiddingQueryProvider {

	/**
	 * 分页查询竞价配置API
	 *
	 * @author baijz
	 * @param biddingPageReq 分页请求参数和筛选对象 {@link BiddingPageRequest}
	 * @return 竞价配置分页列表信息 {@link BiddingPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/page")
	BaseResponse<BiddingPageResponse> page(@RequestBody @Valid BiddingPageRequest biddingPageReq);

	/**
	 * 列表查询竞价配置API
	 *
	 * @author baijz
	 * @param biddingListReq 列表请求参数和筛选对象 {@link BiddingListRequest}
	 * @return 竞价配置的列表信息 {@link BiddingListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/list")
	BaseResponse<BiddingListResponse> list(@RequestBody @Valid BiddingListRequest biddingListReq);

	/**
	 * 单个查询竞价配置API
	 *
	 * @author baijz
	 * @param biddingByIdRequest 单个查询竞价配置请求参数 {@link BiddingByIdRequest}
	 * @return 竞价配置详情 {@link BiddingByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/get-by-id")
	BaseResponse<BiddingByIdResponse> getById(@RequestBody @Valid BiddingByIdRequest biddingByIdRequest);

	/**
	 * 校验关键字重复
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/validate-bidding-keywords")
	BaseResponse<BiddingValidateResponse> validatekeywords(@RequestBody @Valid BiddingValidateKeywordsRequest request);

	/**
	 * 校验商品重复
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/validate-bidding-goods")
	BaseResponse<BiddingValidateResponse> validateBiddingGoods(@RequestBody @Valid BiddingValidateGoodsRequest request);

}

