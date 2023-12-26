package com.wanmi.sbc.goods.provider.impl.goodswarestockdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestockdetail.GoodsWareStockDetailProvider;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailAddRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailDelByIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailModifyRequest;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailAddResponse;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailModifyResponse;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.service.GoodsWareStockDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> 库存明细表保存服务接口实现</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@RestController
@Validated
public class GoodsWareStockDetailController implements GoodsWareStockDetailProvider {
	@Autowired
	private GoodsWareStockDetailService goodsWareStockDetailService;

	@Override
	public BaseResponse<GoodsWareStockDetailAddResponse> add(@RequestBody @Valid GoodsWareStockDetailAddRequest goodsWareStockDetailAddRequest) {
		GoodsWareStockDetail goodsWareStockDetail = KsBeanUtil.convert(goodsWareStockDetailAddRequest, GoodsWareStockDetail.class);
		return BaseResponse.success(new GoodsWareStockDetailAddResponse(
				goodsWareStockDetailService.wrapperVo(goodsWareStockDetailService.add(goodsWareStockDetail))));
	}

	@Override
	public BaseResponse<GoodsWareStockDetailModifyResponse> modify(@RequestBody @Valid GoodsWareStockDetailModifyRequest goodsWareStockDetailModifyRequest) {
		GoodsWareStockDetail goodsWareStockDetail = KsBeanUtil.convert(goodsWareStockDetailModifyRequest, GoodsWareStockDetail.class);
		return BaseResponse.success(new GoodsWareStockDetailModifyResponse(
				goodsWareStockDetailService.wrapperVo(goodsWareStockDetailService.modify(goodsWareStockDetail))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid GoodsWareStockDetailDelByIdRequest goodsWareStockDetailDelByIdRequest) {
		GoodsWareStockDetail goodsWareStockDetail = KsBeanUtil.convert(goodsWareStockDetailDelByIdRequest, GoodsWareStockDetail.class);
		goodsWareStockDetail.setDelFlag(DeleteFlag.YES);
		goodsWareStockDetailService.deleteById(goodsWareStockDetail);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid GoodsWareStockDetailDelByIdListRequest goodsWareStockDetailDelByIdListRequest) {
		List<GoodsWareStockDetail> goodsWareStockDetailList = goodsWareStockDetailDelByIdListRequest.getIdList().stream()
			.map(Id -> {
				GoodsWareStockDetail goodsWareStockDetail = KsBeanUtil.convert(Id, GoodsWareStockDetail.class);
				goodsWareStockDetail.setDelFlag(DeleteFlag.YES);
				return goodsWareStockDetail;
			}).collect(Collectors.toList());
		goodsWareStockDetailService.deleteByIdList(goodsWareStockDetailList);
		return BaseResponse.SUCCESSFUL();
	}

}

