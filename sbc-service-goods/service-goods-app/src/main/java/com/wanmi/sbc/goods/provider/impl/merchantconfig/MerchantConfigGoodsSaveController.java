package com.wanmi.sbc.goods.provider.impl.merchantconfig;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantRecommendGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.*;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsListResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsModifyResponse;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendGoodsVO;
import com.wanmi.sbc.goods.merchantconfig.repository.MerchantConfigGoodsRepository;
import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import com.wanmi.sbc.goods.merchantconfig.service.MerchantConfigGoodsCacheService;
import com.wanmi.sbc.goods.merchantconfig.service.MerchantConfigGoodsService;
import com.wanmi.sbc.goods.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>商品推荐商品保存服务接口实现</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@RestController
@Validated
@Slf4j
public class MerchantConfigGoodsSaveController implements MerchantConfigGoodsSaveProvider {
	@Autowired
	private MerchantConfigGoodsService merchantConfigGoodsService;

	@Autowired
	private MerchantConfigGoodsCacheService merchantConfigGoodsCacheService;
	@Autowired
	private MerchantConfigGoodsRepository goodsRecommendGoodsRepository;
	@Autowired
	private MerchantConfigGoodsService goodsRecommendGoodsService;
	@Autowired
	private RedisService redisService;
	@Override
	public BaseResponse<MerchantConfigGoodsAddResponse> add(@RequestBody @Valid MerchantConfigGoodsAddRequest goodsRecommendGoodsAddRequest) {
		MerchantRecommendGoods goodsRecommendGoods = new MerchantRecommendGoods();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsAddRequest, goodsRecommendGoods);
		return BaseResponse.success(MerchantConfigGoodsAddResponse.builder().merchantConfigGoodsVO(merchantConfigGoodsService.wrapperVo(merchantConfigGoodsService.add(goodsRecommendGoods))).build());
	}

	@Override
	public BaseResponse batachAdd(@RequestBody @Valid MerchantConfigGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest) {
		List<MerchantRecommendGoods> list = merchantConfigGoodsService.findByCompanyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId());
		if (list.size()>Constants.FOLLOW_MAX_SIZE){
			return BaseResponse.error("推荐商品数据请小于500条");
		}
		AtomicInteger count= new AtomicInteger(1);
		goodsRecommendGoodsBatchAddRequest.getGoodsInfoId().forEach(goodsInfoId->{
			this.add(MerchantConfigGoodsAddRequest.builder().goodsInfoId(goodsInfoId).companyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId()).sort(list.size()+ count.get()).storeId(goodsRecommendGoodsBatchAddRequest.getStoreId()).build());
			count.getAndIncrement();
		});
		//刷新缓存
		this.recommendRedis(MerchantConfigGoodsQueryRequest.builder().companyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId()).
				storeId(goodsRecommendGoodsBatchAddRequest.getStoreId()).build());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<MerchantConfigGoodsModifyResponse> modify(@RequestBody @Valid MerchantConfigGoodsModifyRequest goodsRecommendGoodsModifyRequest) {
		MerchantRecommendGoods goodsRecommendGoods = new MerchantRecommendGoods();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsModifyRequest, goodsRecommendGoods);
		return BaseResponse.success(new MerchantConfigGoodsModifyResponse(
				merchantConfigGoodsService.wrapperVo(merchantConfigGoodsService.modify(goodsRecommendGoods))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid MerchantConfigGoodsDelByIdRequest goodsRecommendGoodsDelByIdRequest) {
		merchantConfigGoodsService.deleteById(goodsRecommendGoodsDelByIdRequest.getMerchantRecommendId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid MerchantConfigGoodsDelByIdListRequest goodsRecommendGoodsDelByIdListRequest) {
		goodsRecommendGoodsDelByIdListRequest.getRecommendIdList().forEach(l->{
			merchantConfigGoodsService.deleteById(l);
		});
		//刷新缓存
		this.recommendRedis(MerchantConfigGoodsQueryRequest.builder().companyInfoId(goodsRecommendGoodsDelByIdListRequest.getCompanyInfoId()).
				storeId(goodsRecommendGoodsDelByIdListRequest.getStoreId()).build());
		return BaseResponse.SUCCESSFUL();
	}

	/**
	 * @return 删除结果 {@link BaseResponse}
	 * @author sgy
	 */
	@Override
	public BaseResponse deleteAll(@RequestBody @Valid MerchantConfigGoodsBatchAddRequest goodsRecommendGoodsBatchAddRequest) {
		goodsRecommendGoodsBatchAddRequest.getGoodsInfoId().forEach(l->{
			MerchantRecommendGoods type=new MerchantRecommendGoods();
			type.setGoodsInfoId(l);
			type.setCompanyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId());
			merchantConfigGoodsService.deleteByGoodsInfoId(type);
		});
		merchantConfigGoodsCacheService.deleteRecommendGoodsCache(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId());
		return BaseResponse.SUCCESSFUL();
	}
	@Override
	@Transactional
	public BaseResponse sortMerchantGoods(@RequestBody @Valid MerchantGoodsSortRequest addReq) {
		List<MerchantRecommendGoodsVO> sortList = addReq.getSortList();
		sortList.forEach(l->{
			MerchantRecommendGoods recommendType = goodsRecommendGoodsRepository.findById(l.getMerchantRecommendId()).get();
			recommendType.setRecommendId(l.getMerchantRecommendId());
			recommendType.setSort(l.getSort());
			recommendType.setCompanyInfoId(addReq.getCompanyInfoId());
			merchantConfigGoodsService.modify(recommendType);
		});
		//刷新缓存
		this.recommendRedis(MerchantConfigGoodsQueryRequest.builder().companyInfoId(addReq.getCompanyInfoId()).
				storeId(addReq.getStoreId()).build());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	@Async
	public BaseResponse<MerchantConfigGoodsListResponse> recommendRedis(@RequestBody @Valid MerchantConfigGoodsQueryRequest goodsRecommendGoodsListReq) {
		return merchantConfigGoodsService.recommendRedis(goodsRecommendGoodsListReq);
	}

	@Override
	public BaseResponse sortMerchantRecommendGoods( @RequestBody @Valid MerchantRecommendGoodsSortRequest request) {
       merchantConfigGoodsService.sortMerchantRecommendGoods(request);
		return BaseResponse.SUCCESSFUL();
	}
}

