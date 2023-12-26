package com.wanmi.sbc.goods.provider.impl.goodstypeconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodstypeconfig.GoodsTypeGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.*;
import com.wanmi.sbc.goods.api.request.merchantconfig.*;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigAddResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigModifyResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigResponse;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import com.wanmi.sbc.goods.goodstypeconfig.repository.MerchantTypeConfigRepository;
import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.root.RecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.service.MerchantTypeConfigCacheService;
import com.wanmi.sbc.goods.goodstypeconfig.service.MerchantTypeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>分类推荐分类保存服务接口实现</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@RestController
@Validated
@Slf4j
public class MerchantTypeConfigSaveController implements GoodsTypeGoodsSaveProvider {
	@Autowired
	private MerchantTypeConfigService merchantConfigTypeService;
	@Autowired
	private MerchantTypeConfigRepository merchantTypeConfigRepository;
	@Autowired
	private MerchantTypeConfigCacheService merchantConfigGoodsCacheService;

	@Override
	public BaseResponse<MerchantTypeConfigAddResponse> add(@RequestBody @Valid MerchantTypeConfigAddRequest goodsRecommendGoodsAddRequest) {
		RecommendType type = new RecommendType();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsAddRequest, type);
		type.setStoreCateId(goodsRecommendGoodsAddRequest.getMerchantRecommendTypeId());
		List<RecommendType> list = merchantConfigTypeService.findByCompanyInfoId(goodsRecommendGoodsAddRequest.getCompanyInfoId());
		if (CollectionUtils.isNotEmpty(list)){
			type.setSort(list.size()+ Constants.yes);
		}
		return BaseResponse.success(MerchantTypeConfigAddResponse.builder().typeVO(merchantConfigTypeService.wrapperTypeVo(merchantConfigTypeService.add(type))).build());
	}

	@Override
	public BaseResponse batachAdd(@RequestBody @Valid MerchantTypeConfigBatchAddRequest goodsRecommendGoodsBatchAddRequest) {
		goodsRecommendGoodsBatchAddRequest.getMerchantRecommendTypeId().forEach(typesIds->{
			MerchantTypeConfigAddRequest merchantTypeConfigAddRequest =new MerchantTypeConfigAddRequest();
			merchantTypeConfigAddRequest.setStoreId(goodsRecommendGoodsBatchAddRequest.getStoreId());
			merchantTypeConfigAddRequest.setCompanyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId());
			merchantTypeConfigAddRequest.setMerchantRecommendTypeId(typesIds);
			this.add(merchantTypeConfigAddRequest);
		});
		merchantConfigGoodsCacheService.saveRecommendGoods(MerchantTypeConfigResponse.builder().merchantTypeConfigIds(goodsRecommendGoodsBatchAddRequest.getMerchantRecommendTypeId()).companyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId()).build());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<MerchantTypeConfigModifyResponse> modify(@RequestBody @Valid MerchantTypeConfigModifyRequest goodsRecommendGoodsModifyRequest) {
		MerchantRecommendType goodsRecommendGoods = new MerchantRecommendType();
		KsBeanUtil.copyPropertiesThird(goodsRecommendGoodsModifyRequest, goodsRecommendGoods);
		return BaseResponse.success(new MerchantTypeConfigModifyResponse(
				merchantConfigTypeService.wrapperVo(merchantConfigTypeService.modify(goodsRecommendGoods))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid MerchantConfigGoodsDelByIdRequest goodsRecommendGoodsDelByIdRequest) {
		merchantConfigTypeService.deleteById(goodsRecommendGoodsDelByIdRequest.getMerchantRecommendId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid MerchantConfigGoodsDelByIdListRequest goodsRecommendGoodsDelByIdListRequest) {
		goodsRecommendGoodsDelByIdListRequest.getRecommendIdList().forEach(l->{
			merchantConfigTypeService.deleteById(l);
		});
		return BaseResponse.SUCCESSFUL();
	}

	/**
	 * @return 删除结果 {@link BaseResponse}
	 * @author sgy
	 */
	@Override
	@Transactional
	public BaseResponse deleteAll(@RequestBody @Valid MerchantTypeConfigBatchAddRequest goodsRecommendGoodsBatchAddRequest) {
		//需要保存的skuIds
		goodsRecommendGoodsBatchAddRequest.getMerchantRecommendTypeId().forEach(l->{
			RecommendType type=new RecommendType();
			type.setStoreCateId(l);
			type.setCompanyInfoId(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId());
			merchantConfigTypeService.deleteByCateId(type);
		});
		merchantConfigGoodsCacheService.deleteRecommendGoodsCache(goodsRecommendGoodsBatchAddRequest.getCompanyInfoId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	@Transactional
	public BaseResponse sortMerchantType(@RequestBody @Valid MerchantTypeSortRequest addReq) {
		List<MerchantRecommendTypeVO> sortList = addReq.getSortList();
		sortList.forEach(l->{
			MerchantRecommendType recommendType = merchantTypeConfigRepository.findById(l.getMerchantTypeId()).get();
			recommendType.setMerchantTypeId(l.getMerchantTypeId());
			recommendType.setSort(l.getSort());
			recommendType.setCompanyInfoId(addReq.getCompanyInfoId());
		   merchantConfigTypeService.modify(recommendType);
		});
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse sortMerchantRecommendCat(MerchantRecommendCatSortRequest request) {
		merchantConfigTypeService.sortMerchantRecommendCat(request);
		return BaseResponse.SUCCESSFUL();
	}
}

