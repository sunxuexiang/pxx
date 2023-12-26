package com.wanmi.sbc.goods.provider.impl.stockoutmanage;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageProvider;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageAddRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageAddResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageModifyRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageModifyResponse;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageDelByIdRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageDelByIdListRequest;
import com.wanmi.sbc.goods.stockoutmanage.service.StockoutManageService;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>缺货管理保存服务接口实现</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@RestController
@Validated
public class StockoutManageController implements StockoutManageProvider {
	@Autowired
	private StockoutManageService stockoutManageService;

	@Override
	public BaseResponse<StockoutManageAddResponse> add(@RequestBody @Valid StockoutManageAddRequest stockoutManageAddRequest) {
		StockoutManage stockoutManage = KsBeanUtil.convert(stockoutManageAddRequest, StockoutManage.class);
		return BaseResponse.success(new StockoutManageAddResponse(
				stockoutManageService.wrapperVo(stockoutManageService.add(stockoutManage))));
	}

	@Override
	public BaseResponse<StockoutManageModifyResponse> modify(@RequestBody @Valid StockoutManageModifyRequest stockoutManageModifyRequest) {
		StockoutManage stockoutManage = KsBeanUtil.convert(stockoutManageModifyRequest, StockoutManage.class);
		return BaseResponse.success(new StockoutManageModifyResponse(
				stockoutManageService.wrapperVo(stockoutManageService.modify(stockoutManage))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid StockoutManageDelByIdRequest stockoutManageDelByIdRequest) {
		StockoutManage stockoutManage = KsBeanUtil.convert(stockoutManageDelByIdRequest, StockoutManage.class);
		stockoutManage.setDelFlag(DeleteFlag.YES);
		stockoutManageService.deleteById(stockoutManage);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid StockoutManageDelByIdListRequest stockoutManageDelByIdListRequest) {
		List<StockoutManage> stockoutManageList = stockoutManageDelByIdListRequest.getStockoutIdList().stream()
			.map(Id -> {
				StockoutManage stockoutManage = KsBeanUtil.convert(Id, StockoutManage.class);
				stockoutManage.setDelFlag(DeleteFlag.YES);
				return stockoutManage;
			}).collect(Collectors.toList());
		stockoutManageService.deleteByIdList(stockoutManageList);
		return BaseResponse.SUCCESSFUL();
	}

}

