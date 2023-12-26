package com.wanmi.sbc.goods.provider.impl.merchantconfig;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.merchantconfig.AppGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.MerchantConfigGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.MerchantConfigGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsListResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsPageResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendGoodsVO;
import com.wanmi.sbc.goods.goodsBackups.request.GoodsBackupsQueryRequest;
import com.wanmi.sbc.goods.goodsBackups.root.GoodsRecommendBackups;
import com.wanmi.sbc.goods.goodsBackups.service.GoodsRecommendBackupsService;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.merchantconfig.root.MerchantRecommendGoods;
import com.wanmi.sbc.goods.merchantconfig.service.MerchantConfigGoodsService;
import com.wanmi.sbc.goods.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>商品推荐商品查询服务接口实现</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@RestController
@Validated
@Slf4j
public class MerchantConfigGoodsQueryController implements MerchantConfigGoodsQueryProvider {
	@Autowired
	private MerchantConfigGoodsService goodsRecommendGoodsService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private StoreQueryProvider storeQueryProvider;
	@Autowired
	private GoodsInfoService goodsInfoService;
	@Autowired
	private GoodsRecommendBackupsService goodsRecommendBackupsService;
	@Override
	public BaseResponse<MerchantConfigGoodsPageResponse> page(@RequestBody @Valid MerchantConfigGoodsPageRequest goodsRecommendGoodsPageReq) {
		MerchantConfigGoodsQueryRequest queryReq = new MerchantConfigGoodsQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsPageReq, queryReq);
		queryReq.setCompanyInfoId(goodsRecommendGoodsPageReq.getCompanyInfoId());
		queryReq.setStoreId(goodsRecommendGoodsPageReq.getStoreId());
		queryReq.putSort("sort", "asc");
		Page<MerchantRecommendGoods> goodsRecommendGoodsPage = goodsRecommendGoodsService.page(queryReq);
		Page<MerchantRecommendGoodsVO> newPage = goodsRecommendGoodsPage.map(entity -> goodsRecommendGoodsService.wrapperGoodsInfoVo(entity));
		MicroServicePage<MerchantRecommendGoodsVO> microPage = new MicroServicePage<>(newPage, goodsRecommendGoodsPageReq.getPageable());
		MerchantConfigGoodsPageResponse finalRes = new MerchantConfigGoodsPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<MerchantConfigGoodsListResponse> list(@RequestBody @Valid MerchantConfigGoodsQueryRequest goodsRecommendGoodsListReq) {
		MerchantConfigGoodsQueryRequest queryReq = new MerchantConfigGoodsQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsListReq, queryReq);
		queryReq.putSort("sort", "asc");
		List<MerchantRecommendGoods> goodsRecommendGoodsList = goodsRecommendGoodsService.list(queryReq);
		List<MerchantRecommendGoodsVO> newList = goodsRecommendGoodsList.stream().map(entity -> goodsRecommendGoodsService.wrapperGoodsInfoVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new MerchantConfigGoodsListResponse(newList));
	}

	@Override
	public BaseResponse<MerchantConfigGoodsByIdResponse> getById(@RequestBody @Valid MerchantConfigGoodsQueryRequest goodsRecommendGoodsByIdRequest) {
		MerchantRecommendGoods goodsRecommendGoods = goodsRecommendGoodsService.getById(goodsRecommendGoodsByIdRequest.getRecommendId());
		return BaseResponse.success(new MerchantConfigGoodsByIdResponse(goodsRecommendGoodsService.wrapperVo(goodsRecommendGoods)));
	}



	/***
	 * @param appGoodsQueryRequest
	 * @<p>app端推荐商品<p/>
	 *
	 * */


	@Override
	public BaseResponse<GoodsRecommendSettingVO> getAppGoodsInfo(@RequestBody  @Valid  AppGoodsQueryRequest appGoodsQueryRequest) {
		GoodsRecommendSettingVO vo =new GoodsRecommendSettingVO();
		List<String> goodsInfoIds =new ArrayList<>();
		if (null!=appGoodsQueryRequest && null!=appGoodsQueryRequest.getCustomerId()){
			BaseResponse<StoreInfoResponse> storeInfoById = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(appGoodsQueryRequest.getStoreId()).build());
			GoodsBackupsQueryRequest build = GoodsBackupsQueryRequest.builder().
					companyId(storeInfoById.getContext().getCompanyInfoId()).customerId(appGoodsQueryRequest.getCustomerId()).build();
			List<GoodsRecommendBackups> list1 = goodsRecommendBackupsService.query(build);

			if (CollectionUtils.isNotEmpty(list1)){
				//降序插入
				list1.stream().forEach(gs->{
					GoodsInfo one = goodsInfoService.findOne(gs.getSkuId());
					if (Objects.nonNull(one) && one.getAddedFlag().equals(AddedFlag.YES.toValue())){
						goodsInfoIds.add(gs.getSkuId());
					}
				});

			}
		}
		//获取店铺缓存
		String comKey = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_COMPANY+appGoodsQueryRequest.getStoreId()+ SpecialSymbols.UNDERLINE.toValue();
		List<MerchantRecommendGoods> list = JSONObject.parseArray(redisService.getString(comKey), MerchantRecommendGoods.class);
		if (CollectionUtils.isNotEmpty(list)){
			list = list.stream().sorted(Comparator.comparing(MerchantRecommendGoods::getSort)).collect(Collectors.toList());
			List<String> filterSkuIds = list.stream().map(MerchantRecommendGoods::getGoodsInfoId).collect(Collectors.toList());
			goodsInfoIds.addAll(filterSkuIds);
		}
		vo.setGoodsInfoIds(goodsInfoIds.stream().distinct().collect(Collectors.toList()));
		return BaseResponse.success(vo);
	}





	@Override
	public BaseResponse<MerchantConfigGoodsListResponse> notList(@RequestBody @Valid MerchantConfigGoodsQueryRequest goodsRecommendGoodsListReq) {
		MerchantConfigGoodsQueryRequest queryReq = new MerchantConfigGoodsQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsListReq, queryReq);
		List<MerchantRecommendGoods> goodsRecommendGoodsList = goodsRecommendGoodsService.notList(queryReq);
		List<MerchantRecommendGoodsVO> newList = goodsRecommendGoodsList.stream().map(entity -> goodsRecommendGoodsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new MerchantConfigGoodsListResponse(newList));
	}






}

