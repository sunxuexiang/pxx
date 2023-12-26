package com.wanmi.sbc.goods.provider.impl.stockoutdetail;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.request.stockoutdetail.*;
import com.wanmi.sbc.goods.api.response.stockoutdetail.*;
import com.wanmi.sbc.goods.bean.vo.StockoutDetailVerifyVO;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.stockoutdetail.StockoutDetailQueryProvider;
import com.wanmi.sbc.goods.bean.vo.StockoutDetailVO;
import com.wanmi.sbc.goods.stockoutdetail.service.StockoutDetailService;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>缺货管理查询服务接口实现</p>
 * @author tzx
 * @date 2020-05-27 09:39:38
 */
@RestController
@Validated
public class StockoutDetailQueryController implements StockoutDetailQueryProvider {
	@Autowired
	private StockoutDetailService stockoutDetailService;
	@Autowired
	private GoodsInfoService  goodsInfoService;
	@Override
	public BaseResponse<StockoutDetailPageResponse> page(@RequestBody @Valid StockoutDetailPageRequest stockoutDetailPageReq) {
		StockoutDetailQueryRequest queryReq = KsBeanUtil.convert(stockoutDetailPageReq, StockoutDetailQueryRequest.class);
		Page<StockoutDetail> stockoutDetailPage = stockoutDetailService.page(queryReq);
		Page<StockoutDetailVO> newPage = stockoutDetailPage.map(entity -> stockoutDetailService.wrapperVo(entity));
		MicroServicePage<StockoutDetailVO> microPage = new MicroServicePage<>(newPage, stockoutDetailPageReq.getPageable());
		StockoutDetailPageResponse finalRes = new StockoutDetailPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<StockoutDetailListResponse> list(@RequestBody @Valid StockoutDetailListRequest stockoutDetailListReq) {
		StockoutDetailQueryRequest queryReq = KsBeanUtil.convert(stockoutDetailListReq, StockoutDetailQueryRequest.class);
		List<StockoutDetail> stockoutDetailList = stockoutDetailService.list(queryReq);
		List<StockoutDetailVO> newList = stockoutDetailList.stream().map(entity -> stockoutDetailService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new StockoutDetailListResponse(newList));
	}

	@Override
	public BaseResponse<StockoutDetailByIdResponse> getById(@RequestBody @Valid StockoutDetailByIdRequest stockoutDetailByIdRequest) {
		StockoutDetail stockoutDetail =
		stockoutDetailService.getOne(stockoutDetailByIdRequest.getStockoutDetailId());
		return BaseResponse.success(new StockoutDetailByIdResponse(stockoutDetailService.wrapperVo(stockoutDetail)));
	}

	@Override
	public BaseResponse<StockoutDetailVerifyResponse> verifyDetail(@Valid StockoutDetailVerifyRequest stockoutDetailVerifyRequest) {
		List<GoodsInfo> goodsInfos = goodsInfoService.queryBygoodsId(stockoutDetailVerifyRequest.getGoodsId());
		List<String> goodsInfoIdList = goodsInfos.stream().map(s -> s.getGoodsInfoId()).collect(Collectors.toList());
		List<StockoutDetailVerifyVO> verifyVOS = stockoutDetailService.verifyDetail(StockoutDetailQueryRequest.builder().
						goodsInfoIdLsit(goodsInfoIdList).customerId(stockoutDetailVerifyRequest.getCustomerId())
						.delFlag(DeleteFlag.NO).build());
		return BaseResponse.success(StockoutDetailVerifyResponse.builder().stockoutVerifyList(verifyVOS).build());
	}

	@Override
	public BaseResponse<StockouDetailVerifyGoodInfoIdResponse> verifyByGoodInfoIdDetail(@Valid StockoutDetailQueryRequest stockoutDetailVerifyRequest) {
		return BaseResponse.success(stockoutDetailService.verifyByGoodInfoIdDetail(stockoutDetailVerifyRequest));
	}

}

