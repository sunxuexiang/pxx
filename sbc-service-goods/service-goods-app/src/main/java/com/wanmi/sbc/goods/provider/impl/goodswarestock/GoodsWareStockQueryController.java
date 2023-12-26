package com.wanmi.sbc.goods.provider.impl.goodswarestock;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.shortages.ShortagesGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockPageVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.ShortagesGoodsInfoVO;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockSqlWhereCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>sku分仓库存表查询服务接口实现</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@RestController
@Validated
public class GoodsWareStockQueryController implements GoodsWareStockQueryProvider {
	@Autowired
	private GoodsWareStockService goodsWareStockService;

	@Override
	public BaseResponse<GoodsWareStockPageResponse> page(@RequestBody @Valid GoodsWareStockPageRequest goodsWareStockPageReq) {
		GoodsWareStockSqlWhereCriteriaBuilder queryReq = KsBeanUtil.convert(goodsWareStockPageReq, GoodsWareStockSqlWhereCriteriaBuilder.class);
		Page<GoodsWareStockPageVO> goodsWareStockPage = goodsWareStockService.page(queryReq);
		MicroServicePage<GoodsWareStockPageVO> microPage = new MicroServicePage<>(goodsWareStockPage, goodsWareStockPageReq.getPageable());
		GoodsWareStockPageResponse finalRes = new GoodsWareStockPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	//已处理父子物料库存关系
	@Override
	public BaseResponse<GoodsWareStockListResponse> list(@RequestBody @Valid GoodsWareStockListRequest goodsWareStockListReq) {
		GoodsWareStockQueryRequest queryReq = KsBeanUtil.convert(goodsWareStockListReq, GoodsWareStockQueryRequest.class);
		List<GoodsWareStock> goodsWareStockList = goodsWareStockService.list(queryReq);
		List<GoodsWareStockVO> newList = goodsWareStockList.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockListResponse(newList));
	}

	@Override
	public BaseResponse<GoodsWareStockByIdResponse> getById(@RequestBody @Valid GoodsWareStockByIdRequest goodsWareStockByIdRequest) {
		GoodsWareStock goodsWareStock =
		goodsWareStockService.getOne(goodsWareStockByIdRequest.getId(), goodsWareStockByIdRequest.getStoreId());
		return BaseResponse.success(new GoodsWareStockByIdResponse(goodsWareStockService.wrapperVo(goodsWareStock)));
	}

	@Override
	public BaseResponse<GoodsWareStockByAreaIdsResponse> getGoodsStockByAreaIds(@RequestBody @Valid GoodsWareStockByAreaIdsRequest request) {
		List<GoodsWareStock> goodsWareStockList = goodsWareStockService.getGoodsStockByAreaIds(request);
		List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockList.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockByAreaIdsResponse(goodsWareStockVOList));
	}

	@Override
	public BaseResponse<GoodsWareStockListResponse> getGoodsStockByAreaIdsAndGoodsInfoIds(@RequestBody @Valid StockQueryByAresAndInfoIdListRequest request) {
		List<GoodsWareStock> goodsWareStockList = goodsWareStockService.getGoodsStockByAreaIdsAndGoodsInfoIds(request);
		List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockList.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockListResponse(goodsWareStockVOList));
	}

	@Override
	public BaseResponse<GoodsWareStockInitListResponse> initList(@RequestBody @Valid GoodsWareStockListRequest goodsWareStockListReq) {
		GoodsWareStockQueryRequest queryReq = KsBeanUtil.convert(goodsWareStockListReq, GoodsWareStockQueryRequest.class);
		return BaseResponse.success(goodsWareStockService.initList(queryReq));
	}

	//已处理父子物料库存关系
	@Override
	public BaseResponse<GoodsWareStockListResponse> getGoodsWareStockByGoodsInfoIds(@RequestBody @Valid GoodsWareStockByGoodsForIdsRequest request) {
		List<GoodsWareStock> goodsWareStockList = goodsWareStockService.getGoodsWareStockByGoodsInfoIds(request);
		List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockList.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockListResponse(goodsWareStockVOList));
	}

	@Override
	public BaseResponse<Map<String, BigDecimal>> getGoodsWareStockByGoodsInfoIdsjiya(@RequestBody @Valid GoodsWareStockByGoodsForIdsRequest request) {
		Map<String, BigDecimal> stringBigDecimalMap = goodsWareStockService.getskusJiYastock(request.getGoodsForIdList());
		return BaseResponse.success(stringBigDecimalMap);
	}

	@Override
	public BaseResponse<GoodsWareStockListResponse> getGoodsWareStockByGoodsInfoNos(@RequestBody @Valid GoodsWareStockByGoodsForNoRequest request) {
		List<GoodsWareStock> goodsWareStockList = goodsWareStockService.findByGoodsInfoNoIn(request);
		List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockList.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockListResponse(goodsWareStockVOList));
	}

	@Override
	public BaseResponse<GoodsWareStockListResponse> findByGoodsInfoIdIn(@RequestBody @Valid GoodsWareStockByGoodsForIdsRequest request) {
		List<GoodsWareStock> goodsWareStockList = goodsWareStockService.findByGoodsInfoIdIn(request.getGoodsForIdList());
		List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockList.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockListResponse(goodsWareStockVOList));
	}

	@Override
	public BaseResponse<GoodsWareStockListResponse> getGoodsStockByWareIdAndStoreId(@Valid GoodsWareStockByWareIdAndStoreIdRequest request) {
		List<GoodsWareStock> goodsStockByWareIdAndStoreId = goodsWareStockService.getGoodsStockByWareIdAndStoreId(request);
		List<GoodsWareStockVO> goodsWareStockVOList = goodsStockByWareIdAndStoreId.stream().map(entity -> goodsWareStockService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsWareStockListResponse(goodsWareStockVOList));
	}

	@Override
	public BaseResponse<GoodsWareStockByGoodsIdResponse> findByGoodsIdIn(@Valid GoodsWareStockByGoodsIdRequest request) {
		List<GoodsWareStock> byGoodsIdIn = goodsWareStockService.findByGoodsIdIn(request.getGoodsIds());
		List<GoodsWareStockVO> convert = KsBeanUtil.convert(byGoodsIdIn, GoodsWareStockVO.class);
		return BaseResponse.success(GoodsWareStockByGoodsIdResponse.builder().goodsWareStockVOList(convert).build());
	}

	@Override
	public BaseResponse<List<Object[]>> getInventory() {
		List<Object[]> inventory = goodsWareStockService.getInventory();
		return BaseResponse.success(inventory);
	}

	@Override
	public BaseResponse<ShortagesGoodsInfoResponse> getShortagesGoodsInfos() {
		List<ShortagesGoodsInfoVO> shortagesGoodsInfos = goodsWareStockService.getShortagesGoodsInfos();
		return BaseResponse.success(ShortagesGoodsInfoResponse.builder().goodsInfos(shortagesGoodsInfos).build());
	}

	@Override
	public BaseResponse<GoodsWareStockListResponse> queryWareStockByWareIdAndGoodsInfoId(GoodsWareStockByGoodsInfoIdAndWareIdRequest request) {
		GoodsWareStockVO vo = goodsWareStockService.queryWareStockByWareIdAndGoodsInfoId(request.getGoodsInfoId(),request.getWareId());
		if(Objects.isNull(vo)){
			return BaseResponse.success(GoodsWareStockListResponse.builder().build());
		}
		List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
		goodsWareStockVOList.add(vo);
		return BaseResponse.success(GoodsWareStockListResponse.builder().goodsWareStockVOList(goodsWareStockVOList).build());
	}

	@Override
	public BaseResponse<GoodsWareStockListResponse> queryWareStockByWareIdAndGoodsInfoIdList(GoodsWareStockListRequest goodsWareStockListReq) {
		List<GoodsWareStock> wareStockList = goodsWareStockService.getListByWareIdAndInfoIdList(goodsWareStockListReq.getGoodsInfoIds(),goodsWareStockListReq.getWareId());
		List<GoodsWareStockVO> convert = KsBeanUtil.convert(wareStockList, GoodsWareStockVO.class);
		return BaseResponse.success(new GoodsWareStockListResponse(convert));
	}

	@Override
	public BaseResponse<GoodsWareStockVO> getWareStockByWareIdAndInfoId(String goodsInfoId, Long wareId) {
		GoodsWareStockVO vo = goodsWareStockService.queryWareStockByWareIdAndGoodsInfoId(goodsInfoId,wareId);
		return BaseResponse.success(vo);
	}
}

