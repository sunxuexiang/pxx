package com.wanmi.sbc.goods.provider.impl.biddinggoods;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.biddinggoods.BiddingGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsPageResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsListRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsListResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsByIdResponse;
import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import com.wanmi.sbc.goods.biddinggoods.service.BiddingGoodsService;
import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>竞价商品查询服务接口实现</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@RestController
@Validated
public class BiddingGoodsQueryController implements BiddingGoodsQueryProvider {
	@Autowired
	private BiddingGoodsService biddingGoodsService;

	@Override
	public BaseResponse<BiddingGoodsPageResponse> page(@RequestBody @Valid BiddingGoodsPageRequest biddingGoodsPageReq) {
		BiddingGoodsQueryRequest queryReq = KsBeanUtil.convert(biddingGoodsPageReq, BiddingGoodsQueryRequest.class);
		Page<BiddingGoods> biddingGoodsPage = biddingGoodsService.page(queryReq);
		Page<BiddingGoodsVO> newPage = biddingGoodsPage.map(entity -> biddingGoodsService.wrapperVo(entity));
		MicroServicePage<BiddingGoodsVO> microPage = new MicroServicePage<>(newPage, biddingGoodsPageReq.getPageable());
		BiddingGoodsPageResponse finalRes = new BiddingGoodsPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<BiddingGoodsListResponse> list(@RequestBody @Valid BiddingGoodsListRequest biddingGoodsListReq) {
		BiddingGoodsQueryRequest queryReq = KsBeanUtil.convert(biddingGoodsListReq, BiddingGoodsQueryRequest.class);
		List<BiddingGoods> biddingGoodsList = biddingGoodsService.list(queryReq);
		List<BiddingGoodsVO> newList = biddingGoodsList.stream().map(entity -> biddingGoodsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new BiddingGoodsListResponse(newList));
	}

	@Override
	public BaseResponse<BiddingGoodsByIdResponse> getById(@RequestBody @Valid BiddingGoodsByIdRequest biddingGoodsByIdRequest) {
		BiddingGoods biddingGoods =
		biddingGoodsService.getOne(biddingGoodsByIdRequest.getBiddingGoodsId());
		return BaseResponse.success(new BiddingGoodsByIdResponse(biddingGoodsService.wrapperVo(biddingGoods)));
	}

}

