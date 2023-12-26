package com.wanmi.sbc.goods.api.provider.biddinggoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsAddRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsAddResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsModifyRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsModifyResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsDelByIdRequest;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>竞价商品保存服务Provider</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "BiddingGoodsProvider")
public interface BiddingGoodsProvider {

	/**
	 * 新增竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsAddRequest 竞价商品新增参数结构 {@link BiddingGoodsAddRequest}
	 * @return 新增的竞价商品信息 {@link BiddingGoodsAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/add")
	BaseResponse<BiddingGoodsAddResponse> add(@RequestBody @Valid BiddingGoodsAddRequest biddingGoodsAddRequest);

	/**
	 * 修改竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsModifyRequest 竞价商品修改参数结构 {@link BiddingGoodsModifyRequest}
	 * @return 修改的竞价商品信息 {@link BiddingGoodsModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/modify")
	BaseResponse<BiddingGoodsModifyResponse> modify(@RequestBody @Valid BiddingGoodsModifyRequest biddingGoodsModifyRequest);

	/**
	 * 单个删除竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsDelByIdRequest 单个删除参数结构 {@link BiddingGoodsDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid BiddingGoodsDelByIdRequest biddingGoodsDelByIdRequest);

	/**
	 * 批量删除竞价商品API
	 *
	 * @author baijz
	 * @param biddingGoodsDelByIdListRequest 批量删除参数结构 {@link BiddingGoodsDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/biddinggoods/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid BiddingGoodsDelByIdListRequest biddingGoodsDelByIdListRequest);

}

