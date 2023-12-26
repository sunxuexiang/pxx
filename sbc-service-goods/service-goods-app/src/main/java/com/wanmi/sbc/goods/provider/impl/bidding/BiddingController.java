package com.wanmi.sbc.goods.provider.impl.bidding;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingProvider;
import com.wanmi.sbc.goods.api.request.bidding.BiddingAddRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingAddResponse;
import com.wanmi.sbc.goods.api.request.bidding.BiddingModifyRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingModifyResponse;
import com.wanmi.sbc.goods.api.request.bidding.BiddingDelByIdRequest;
import com.wanmi.sbc.goods.api.request.bidding.BiddingDelByIdListRequest;
import com.wanmi.sbc.goods.bidding.service.BiddingService;
import com.wanmi.sbc.goods.bidding.model.root.Bidding;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>竞价配置保存服务接口实现</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@RestController
@Validated
public class BiddingController implements BiddingProvider {
	@Autowired
	private BiddingService biddingService;

	@Override
	public BaseResponse<BiddingAddResponse> add(@RequestBody @Valid BiddingAddRequest biddingAddRequest) {
		Bidding bidding = KsBeanUtil.convert(biddingAddRequest, Bidding.class);
		return BaseResponse.success(new BiddingAddResponse(
				biddingService.wrapperVo(biddingService.add(bidding,biddingAddRequest.getGoodsInfoIds()))));
	}

	@Override
	public BaseResponse<BiddingModifyResponse> modify(@RequestBody @Valid BiddingModifyRequest biddingModifyRequest) {
		Bidding bidding = KsBeanUtil.convert(biddingModifyRequest, Bidding.class);
		bidding.setDelFlag(DeleteFlag.NO);
		return BaseResponse.success(new BiddingModifyResponse(
				biddingService.wrapperVo(biddingService.modify(bidding,biddingModifyRequest.getGoodsInfoIds()))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid BiddingDelByIdRequest biddingDelByIdRequest) {
		biddingService.deleteById(biddingDelByIdRequest.getBiddingId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid BiddingDelByIdListRequest biddingDelByIdListRequest) {
		List<Bidding> biddingList = biddingDelByIdListRequest.getBiddingIdList().stream()
			.map(BiddingId -> {
				Bidding bidding = KsBeanUtil.convert(BiddingId, Bidding.class);
				bidding.setDelFlag(DeleteFlag.YES);
				return bidding;
			}).collect(Collectors.toList());
		biddingService.deleteByIdList(biddingList);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse finishBiddingActivity(@RequestBody @Valid BiddingDelByIdRequest biddingDelByIdRequest) {
		biddingService.finishBiddingActivity(biddingDelByIdRequest.getBiddingId());
		return BaseResponse.SUCCESSFUL();
	}

}

