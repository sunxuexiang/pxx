package com.wanmi.sbc.goods.provider.impl.biddinggoods;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.provider.biddinggoods.BiddingGoodsProvider;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsAddRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsAddResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsModifyRequest;
import com.wanmi.sbc.goods.api.response.biddinggoods.BiddingGoodsModifyResponse;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsDelByIdRequest;
import com.wanmi.sbc.goods.api.request.biddinggoods.BiddingGoodsDelByIdListRequest;
import com.wanmi.sbc.goods.biddinggoods.service.BiddingGoodsService;
import com.wanmi.sbc.goods.biddinggoods.model.root.BiddingGoods;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>竞价商品保存服务接口实现</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@RestController
@Validated
public class BiddingGoodsController implements BiddingGoodsProvider {
	@Autowired
	private BiddingGoodsService biddingGoodsService;

	@Override
	public BaseResponse<BiddingGoodsAddResponse> add(@RequestBody @Valid BiddingGoodsAddRequest biddingGoodsAddRequest) {
		BiddingGoods biddingGoods = KsBeanUtil.convert(biddingGoodsAddRequest, BiddingGoods.class);
		return BaseResponse.success(new BiddingGoodsAddResponse(
				biddingGoodsService.wrapperVo(biddingGoodsService.add(biddingGoods))));
	}

	@Override
	public BaseResponse<BiddingGoodsModifyResponse> modify(@RequestBody @Valid BiddingGoodsModifyRequest biddingGoodsModifyRequest) {
		BiddingGoods biddingGoods = KsBeanUtil.convert(biddingGoodsModifyRequest, BiddingGoods.class);
		return BaseResponse.success(new BiddingGoodsModifyResponse(
				biddingGoodsService.wrapperVo(biddingGoodsService.modify(biddingGoods))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid BiddingGoodsDelByIdRequest biddingGoodsDelByIdRequest) {
		BiddingGoods biddingGoods = KsBeanUtil.convert(biddingGoodsDelByIdRequest, BiddingGoods.class);
		biddingGoods.setDelFlag(DeleteFlag.YES);
		biddingGoodsService.deleteById(biddingGoods);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid BiddingGoodsDelByIdListRequest biddingGoodsDelByIdListRequest) {
		List<BiddingGoods> biddingGoodsList = biddingGoodsDelByIdListRequest.getBiddingGoodsIdList().stream()
			.map(BiddingGoodsId -> {
				BiddingGoods biddingGoods = KsBeanUtil.convert(BiddingGoodsId, BiddingGoods.class);
				biddingGoods.setDelFlag(DeleteFlag.YES);
				return biddingGoods;
			}).collect(Collectors.toList());
		biddingGoodsService.deleteByIdList(biddingGoodsList);
		return BaseResponse.SUCCESSFUL();
	}

}

