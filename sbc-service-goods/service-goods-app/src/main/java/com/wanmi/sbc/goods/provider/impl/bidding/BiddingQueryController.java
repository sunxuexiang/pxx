package com.wanmi.sbc.goods.provider.impl.bidding;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.request.bidding.*;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingValidateResponse;
import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import com.wanmi.sbc.goods.biddinggoods.service.BiddingGoodsService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingQueryProvider;
import com.wanmi.sbc.goods.api.response.bidding.BiddingPageResponse;
import com.wanmi.sbc.goods.api.response.bidding.BiddingListResponse;
import com.wanmi.sbc.goods.api.response.bidding.BiddingByIdResponse;
import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import com.wanmi.sbc.goods.bidding.service.BiddingService;
import com.wanmi.sbc.goods.bidding.model.root.Bidding;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>竞价配置查询服务接口实现</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@RestController
@Validated
public class BiddingQueryController implements BiddingQueryProvider {
	@Autowired
	private BiddingService biddingService;

	@Autowired
	private BiddingGoodsService biddingGoodsService;

	@Override
	public BaseResponse<BiddingPageResponse> page(@RequestBody @Valid BiddingPageRequest biddingPageReq) {
		BiddingQueryRequest queryReq = KsBeanUtil.convert(biddingPageReq, BiddingQueryRequest.class);
		Page<Bidding> biddingPage = biddingService.page(queryReq);
		Page<BiddingVO> newPage = biddingPage.map(entity -> biddingService.wrapperVo(entity));
		MicroServicePage<BiddingVO> microPage = new MicroServicePage<>(newPage, biddingPageReq.getPageable());
		BiddingPageResponse finalRes = new BiddingPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<BiddingListResponse> list(@RequestBody @Valid BiddingListRequest biddingListReq) {
		BiddingQueryRequest queryReq = KsBeanUtil.convert(biddingListReq, BiddingQueryRequest.class);
		List<Bidding> biddingList = biddingService.list(queryReq);
		List<BiddingVO> newList = biddingList.stream().map(entity -> biddingService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new BiddingListResponse(newList));
	}

	@Override
	public BaseResponse<BiddingByIdResponse> getById(@RequestBody @Valid BiddingByIdRequest biddingByIdRequest) {
		Bidding bidding = biddingService.getOne(biddingByIdRequest.getBiddingId());
		BiddingVO biddingVO = biddingService.wrapperVo(bidding);
		if(Objects.nonNull(bidding)){
			BiddingGoodsQueryRequest request = BiddingGoodsQueryRequest.builder()
					.delFlag(DeleteFlag.NO)
					.biddingId(bidding.getBiddingId())
					.build();
			List<BiddingGoods> biddingGoods = biddingGoodsService.list(request);
			List<BiddingGoodsVO> biddingGoodsVOS = KsBeanUtil.convert(biddingGoods, BiddingGoodsVO.class);
			biddingGoodsVOS.sort(Comparator.comparingInt(BiddingGoodsVO::getSort));
			biddingVO.setBiddingGoodsVOS(biddingGoodsVOS);
		}
		return BaseResponse.success(BiddingByIdResponse.builder().biddingVO(biddingVO).build());
	}

	@Override
	public BaseResponse<BiddingValidateResponse> validatekeywords(@RequestBody @Valid BiddingValidateKeywordsRequest request) {
		List<String> repeatKeywords = biddingService.validateKeywords(request.getKeywords(),request.getBiddingId(),request.getStartTime(),request.getEndTime());
		return BaseResponse.success(BiddingValidateResponse.builder().validateResult(repeatKeywords).build());
	}

	@Override
	public BaseResponse<BiddingValidateResponse> validateBiddingGoods(@RequestBody @Valid BiddingValidateGoodsRequest request) {
		List<String> repeatKeywords = biddingService.validateBiddingGoods(request);
		return BaseResponse.success(BiddingValidateResponse.builder().validateResult(repeatKeywords).build());
	}

}

