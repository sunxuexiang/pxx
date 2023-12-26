package com.wanmi.sbc.goods.provider.impl.goodstypeconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodstypeconfig.GoodsTypeConfigQueryProvider;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigByIdRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigListRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigPageRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigQueryRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByCacheResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdsResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigByIdResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigListResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import com.wanmi.sbc.goods.bean.vo.RecommendTypeVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateVO;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.root.RecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.service.MerchantTypeConfigService;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import com.wanmi.sbc.goods.storecate.service.StoreCateService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>分类推荐分类查询服务接口实现</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@RestController
@Validated
public class MerchantTypeConfigQueryController implements GoodsTypeConfigQueryProvider {
	@Autowired
	private MerchantTypeConfigService goodsRecommendGoodsService;
	@Autowired
	private GoodsCateService goodsCateService;
	@Autowired
	private StoreCateService storeCateService;

	@Override
	public BaseResponse<MerchantTypeConfigPageResponse> page(@RequestBody @Valid MerchantTypeConfigPageRequest goodsRecommendGoodsPageReq) {
		MerchantTypeConfigQueryRequest queryReq = new MerchantTypeConfigQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsPageReq, queryReq);
		Page<MerchantRecommendType> goodsRecommendGoodsPage = goodsRecommendGoodsService.page(queryReq);
		Page<MerchantRecommendTypeVO> newPage = goodsRecommendGoodsPage.map(entity -> goodsRecommendGoodsService.wrapperVo(entity));
		MicroServicePage<MerchantRecommendTypeVO> microPage = new MicroServicePage<>(newPage, goodsRecommendGoodsPageReq.getPageable());
		MerchantTypeConfigPageResponse finalRes = new MerchantTypeConfigPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<MerchantTypeConfigListResponse> list(@RequestBody @Valid MerchantTypeConfigListRequest goodsRecommendGoodsListReq) {
		MerchantTypeConfigQueryRequest queryReq = new MerchantTypeConfigQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsListReq, queryReq);
		List<MerchantRecommendType> goodsRecommendGoodsList = goodsRecommendGoodsService.sotreList(queryReq);
		List<MerchantRecommendTypeVO> collect = goodsRecommendGoodsList.stream().map(entity -> goodsRecommendGoodsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new MerchantTypeConfigListResponse(collect));
	}

	@Override
	public BaseResponse<MerchantTypeConfigByIdResponse> getById(@RequestBody @Valid MerchantTypeConfigByIdRequest goodsRecommendGoodsByIdRequest) {
		MerchantRecommendType recommendType = goodsRecommendGoodsService.getById(goodsRecommendGoodsByIdRequest.getMerchantTypeId());
		return BaseResponse.success(new MerchantTypeConfigByIdResponse(goodsRecommendGoodsService.wrapperVo(recommendType)));
	}

	/**
	 *
	 *推荐给app ，此一次就10个,暂时不做缓存和千人面，需要可以改造这此接口
	 * */
	@Override
	public BaseResponse<GoodsCateByIdsResponse> appList(@RequestBody @Valid  MerchantTypeConfigListRequest goodsRecommendGoodsListReq) {
		GoodsCateByIdsResponse response = new GoodsCateByIdsResponse();
		MerchantTypeConfigQueryRequest queryReq = new MerchantTypeConfigQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsListReq, queryReq);
		//加入排序资格
		queryReq.setSortColumn("sort");
		queryReq.setSortRole(SortType.ASC.toValue());
		List<RecommendType> goodsRecommendGoodsList = goodsRecommendGoodsService.list(queryReq);
		List<RecommendTypeVO> collect1 = goodsRecommendGoodsList.stream().map(entity -> goodsRecommendGoodsService.wrapperTypeVo(entity)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(collect1)){
			List<Long> stringList = collect1.stream().map(RecommendTypeVO::getStoreCateId).collect(Collectors.toList());
			List<StoreCate> goodsCateList = storeCateService.findByIds(stringList);
			List<StoreCateVO> goodsCateVOList = KsBeanUtil.convert(goodsCateList, StoreCateVO.class);
			final Map<Long, StoreCateVO> storeCatIdMap = goodsCateVOList.stream().collect(Collectors.toMap(StoreCateVO::getStoreCateId, Function.identity(), (o, n) -> o));
			List<StoreCateVO> result = new ArrayList<>();
			collect1.forEach(o -> result.add(storeCatIdMap.get(o.getStoreCateId())));
			response.setStoreCateVOList(result);
			return BaseResponse.success(response);
		}
		return BaseResponse.success(response);

	}

}

