package com.wanmi.sbc.goods.provider.impl.stockoutdetail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.stockoutdetail.StockoutDetailProvider;
import com.wanmi.sbc.goods.api.request.stockoutdetail.*;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockoutDetailAddResponse;
import com.wanmi.sbc.goods.api.response.stockoutdetail.StockoutDetailModifyResponse;
import com.wanmi.sbc.goods.bean.dto.StockoutDetailDTO;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import com.wanmi.sbc.goods.stockoutdetail.service.StockoutDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>缺货管理保存服务接口实现</p>
 * @author tzx
 * @date 2020-05-27 09:39:38
 */
@RestController
@Validated
public class StockoutDetailController implements StockoutDetailProvider {
	@Autowired
	private StockoutDetailService stockoutDetailService;

	@Override
	public BaseResponse<StockoutDetailAddResponse> add(@RequestBody @Valid StockoutDetailAddRequest stockoutDetailAddRequest) {
		StockoutDetailDTO stockoutDetailDTO = KsBeanUtil.convert(stockoutDetailAddRequest, StockoutDetailDTO.class);
		return BaseResponse.success(new StockoutDetailAddResponse(
				stockoutDetailService.wrapperVo(stockoutDetailService.add(stockoutDetailDTO))));
	}

	@Override
	public BaseResponse addAll(@Valid StockoutDetailAddAllRequest stockoutDetailAddRequest) {
		List<StockoutDetailDTO> stockoutDetailDTOList=KsBeanUtil.convert(stockoutDetailAddRequest.getStockOutList(),StockoutDetailDTO.class);
		stockoutDetailService.addAll(stockoutDetailDTOList);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<StockoutDetailModifyResponse> modify(@RequestBody @Valid StockoutDetailModifyRequest stockoutDetailModifyRequest) {
		StockoutDetail stockoutDetail = KsBeanUtil.convert(stockoutDetailModifyRequest, StockoutDetail.class);
		return BaseResponse.success(new StockoutDetailModifyResponse(
				stockoutDetailService.wrapperVo(stockoutDetailService.modify(stockoutDetail))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid StockoutDetailDelByIdRequest stockoutDetailDelByIdRequest) {
		StockoutDetail stockoutDetail = KsBeanUtil.convert(stockoutDetailDelByIdRequest, StockoutDetail.class);
		stockoutDetail.setDelFlag(DeleteFlag.YES);
		stockoutDetailService.deleteById(stockoutDetail);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid StockoutDetailDelByIdListRequest stockoutDetailDelByIdListRequest) {
		List<StockoutDetail> stockoutDetailList = stockoutDetailDelByIdListRequest.getStockoutDetailIdList().stream()
			.map(Id -> {
				StockoutDetail stockoutDetail = KsBeanUtil.convert(Id, StockoutDetail.class);
				stockoutDetail.setDelFlag(DeleteFlag.YES);
				return stockoutDetail;
			}).collect(Collectors.toList());
		stockoutDetailService.deleteByIdList(stockoutDetailList);
		return BaseResponse.SUCCESSFUL();
	}

}

