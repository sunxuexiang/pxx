package com.wanmi.sbc.goods.provider.impl.goodswarestockdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestockdetail.GoodsWareStockDetailQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailByIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailPageRequest;
import com.wanmi.sbc.goods.api.request.goodswarestockdetail.GoodsWareStockDetailQueryRequest;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailByIdResponse;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailListResponse;
import com.wanmi.sbc.goods.api.response.goodswarestockdetail.GoodsWareStockDetailPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockDetailVO;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.service.GoodsWareStockDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p> 库存明细表查询服务接口实现</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@RestController
@Validated
public class GoodsWareStockDetailQueryController implements GoodsWareStockDetailQueryProvider {
	@Autowired
	private GoodsWareStockDetailService goodsWareStockDetailService;

	@Override
	public BaseResponse<GoodsWareStockDetailPageResponse> page(@RequestBody @Valid GoodsWareStockDetailPageRequest goodsWareStockDetailPageReq) {
		GoodsWareStockDetailQueryRequest queryReq = KsBeanUtil.convert(goodsWareStockDetailPageReq, GoodsWareStockDetailQueryRequest.class);
		Page<GoodsWareStockDetail> goodsWareStockDetailPage = goodsWareStockDetailService.page(queryReq);
		Page<GoodsWareStockDetailVO> newPage = goodsWareStockDetailPage.map(entity -> goodsWareStockDetailService.wrapperVo(entity));
		MicroServicePage<GoodsWareStockDetailVO> microPage = new MicroServicePage<>(newPage, goodsWareStockDetailPageReq.getPageable());
		GoodsWareStockDetailPageResponse finalRes = new GoodsWareStockDetailPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<GoodsWareStockDetailListResponse> list(@RequestBody @Valid GoodsWareStockDetailListRequest goodsWareStockDetailListReq) {
		GoodsWareStockDetailQueryRequest queryReq = KsBeanUtil.convert(goodsWareStockDetailListReq, GoodsWareStockDetailQueryRequest.class);
		List<GoodsWareStockDetail> goodsWareStockDetailList = goodsWareStockDetailService.list(queryReq);
		List<GoodsWareStockDetailVO> newList = goodsWareStockDetailList.stream().map(entity -> goodsWareStockDetailService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockDetailListResponse(newList));
	}

	@Override
	public BaseResponse<GoodsWareStockDetailByIdResponse> getById(@RequestBody @Valid GoodsWareStockDetailByIdRequest goodsWareStockDetailByIdRequest) {
		GoodsWareStockDetail goodsWareStockDetail =
		goodsWareStockDetailService.getOne(goodsWareStockDetailByIdRequest.getId());
		return BaseResponse.success(new GoodsWareStockDetailByIdResponse(goodsWareStockDetailService.wrapperVo(goodsWareStockDetail)));
	}

}

