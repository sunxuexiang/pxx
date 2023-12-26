package com.wanmi.sbc.goods.api.provider.bidding;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.bidding.BiddingAddRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingAddResponse;
import com.wanmi.sbc.goods.api.request.bidding.BiddingModifyRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingModifyResponse;
import com.wanmi.sbc.goods.api.request.bidding.BiddingDelByIdRequest;
import com.wanmi.sbc.goods.api.request.bidding.BiddingDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>竞价配置保存服务Provider</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BiddingProvider")
public interface BiddingProvider {

	/**
	 * 新增竞价配置API
	 *
	 * @author baijz
	 * @param biddingAddRequest 竞价配置新增参数结构 {@link BiddingAddRequest}
	 * @return 新增的竞价配置信息 {@link BiddingAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/add")
	BaseResponse<BiddingAddResponse> add(@RequestBody @Valid BiddingAddRequest biddingAddRequest);

	/**
	 * 修改竞价配置API
	 *
	 * @author baijz
	 * @param biddingModifyRequest 竞价配置修改参数结构 {@link BiddingModifyRequest}
	 * @return 修改的竞价配置信息 {@link BiddingModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/modify")
	BaseResponse<BiddingModifyResponse> modify(@RequestBody @Valid BiddingModifyRequest biddingModifyRequest);

	/**
	 * 单个删除竞价配置API
	 *
	 * @author baijz
	 * @param biddingDelByIdRequest 单个删除参数结构 {@link BiddingDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid BiddingDelByIdRequest biddingDelByIdRequest);

	/**
	 * 批量删除竞价配置API
	 *
	 * @author baijz
	 * @param biddingDelByIdListRequest 批量删除参数结构 {@link BiddingDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid BiddingDelByIdListRequest biddingDelByIdListRequest);

	/**
	 * 批量删除竞价配置API
	 *
	 * @author baijz
	 * @param biddingDelByIdRequest 批量删除参数结构 {@link BiddingDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/bidding/finish-bidding-activity")
	BaseResponse finishBiddingActivity(@RequestBody @Valid BiddingDelByIdRequest biddingDelByIdRequest);

}

