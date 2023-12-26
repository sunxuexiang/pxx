package com.wanmi.sbc.goods.provider.impl.stockoutmanage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageQueryProvider;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageByIdRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageListRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManagePageRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageQueryRequest;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageByIdResponse;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageListResponse;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManagePageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.StockoutDetailVO;
import com.wanmi.sbc.goods.bean.vo.StockoutManageVO;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import com.wanmi.sbc.goods.stockoutmanage.service.StockoutManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>缺货管理查询服务接口实现</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@RestController
@Validated
public class StockoutManageQueryController implements StockoutManageQueryProvider {
	@Autowired
	private StockoutManageService stockoutManageService;
	@Autowired
	private GoodsBrandService goodsBrandService;
	@Autowired
	private GoodsWareStockService goodsWareStockService;

	@Override
	public BaseResponse<StockoutManagePageResponse> page(@RequestBody @Valid StockoutManagePageRequest stockoutManagePageReq) {
		StockoutManageQueryRequest queryReq = KsBeanUtil.convert(stockoutManagePageReq, StockoutManageQueryRequest.class);
		Page<StockoutManage> stockoutManagePage = stockoutManageService.page(queryReq);
		Page<StockoutManageVO> newPage = stockoutManagePage.map(
				(entity) -> {
					StockoutManageVO stockoutManageVO = stockoutManageService.wrapperVo(entity);
					stockoutManageVO.setStockoutDetailList(KsBeanUtil.convertList(entity.getStockoutDetailList(), StockoutDetailVO.class));
					return stockoutManageVO;
				}
		);

		// 查询商家 对应的品牌
		List<GoodsBrandVO> goodsBrandVOS = KsBeanUtil.convert(goodsBrandService.query(GoodsBrandQueryRequest.builder().
				delFlag(DeleteFlag.NO.toValue())
				.storeId(stockoutManagePageReq.getStoreId()).build()), GoodsBrandVO.class);

		MicroServicePage<StockoutManageVO> microPage = new MicroServicePage<>(newPage, stockoutManagePageReq.getPageable());
		StockoutManagePageResponse finalRes = StockoutManagePageResponse.builder().stockoutManageVOPage(microPage).
				goodsBrandList(goodsBrandVOS).build();
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<StockoutManageListResponse> pushGoodsStockPage(@RequestBody @Valid StockoutManagePageRequest stockoutManagePageReq) {
		StockoutManageQueryRequest queryReq = KsBeanUtil.convert(stockoutManagePageReq, StockoutManageQueryRequest.class);
		Page<StockoutManage> stockoutManagePage = goodsWareStockService.checkGoodsArrival(queryReq);
		return BaseResponse.success(new StockoutManageListResponse(stockoutManagePage.getContent().stream()
				.map(entity -> stockoutManageService.wrapperVo(entity)).collect(Collectors.toList())));
	}

	@Override
	public BaseResponse<StockoutManageListResponse> list(@RequestBody @Valid StockoutManageListRequest stockoutManageListReq) {
		StockoutManageQueryRequest queryReq = KsBeanUtil.convert(stockoutManageListReq, StockoutManageQueryRequest.class);
		List<StockoutManage> stockoutManageList = stockoutManageService.list(queryReq);
		List<StockoutManageVO> newList = stockoutManageList.stream().map(entity -> stockoutManageService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new StockoutManageListResponse(newList));
	}

	@Override
	public BaseResponse<StockoutManageByIdResponse> getById(@RequestBody @Valid StockoutManageByIdRequest stockoutManageByIdRequest) {
		StockoutManage stockoutManage =
		stockoutManageService.getOne(stockoutManageByIdRequest.getStockoutId());
		return BaseResponse.success(new StockoutManageByIdResponse(stockoutManageService.wrapperVo(stockoutManage)));
	}

}

