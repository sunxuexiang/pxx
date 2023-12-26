package com.wanmi.sbc.marketing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.EnumTranslateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdAndCustomerRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingPageRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingStartByIdRequest;
import com.wanmi.sbc.marketing.api.response.gift.FullGiftLevelListByMarketingIdAndCustomerResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingGetByIdResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingPageResponse;
import com.wanmi.sbc.marketing.bean.dto.MarketingPageDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.vo.MarketingForEndVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingPageVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.request.MarketingPageListRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "MarketingController", description = "商家活动中心营销服务API")
@RestController
@RequestMapping("/marketing")
@Slf4j
public class MarketingController {

	@Autowired
	private MarketingQueryProvider marketingQueryProvider;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private MarketingProvider marketingProvider;

	@Autowired
	private OperateLogMQUtil operateLogMQUtil;
	
    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;
    
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    

	/**
	 * 获取营销活动列表
	 * 
	 * @author zhangchen
	 * @date 2023年6月28日 上午8:34:21
	 * @param marketingPageListRequest
	 * @return
	 */
	@ApiOperation(value = "获取营销活动列表")
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public BaseResponse<MicroServicePage<MarketingPageVO>> getMarketingList(
			@RequestBody MarketingPageListRequest marketingPageListRequest) {
		log.info("====进入/marketing/list====");

		MarketingPageDTO request = KsBeanUtil.convert(marketingPageListRequest, MarketingPageDTO.class);
		MarketingPageRequest marketingPageRequest = new MarketingPageRequest();
		marketingPageRequest.setStoreId(marketingPageListRequest.getStoreId());
		marketingPageRequest.setMarketingPageDTO(request);
		BaseResponse<MarketingPageResponse> pageResponse = marketingQueryProvider.page(marketingPageRequest);
		
		// 商家ids
		MicroServicePage<MarketingPageVO> marketingVOS = pageResponse.getContext().getMarketingVOS();
		Set<Long> storeIds = marketingVOS.stream().map(MarketingPageVO::getStoreId).collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(storeIds)) {
			// 一次性给商家名称赋值
			BaseResponse<ListStoreByIdsResponse> listByIds = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<>(storeIds)).build());
			marketingVOS.forEach(m -> {
				Optional<StoreVO> findFirst = listByIds.getContext().getStoreVOList().stream().filter(s -> s.getStoreId().equals(m.getStoreId())).findFirst();
				findFirst.ifPresent(v -> {
					m.setSupplierName(v.getSupplierName());
					m.setStoreName(v.getStoreName());
				});
			});
			
		}
		return BaseResponse.success(marketingVOS);
	}
	

	/**
	 * 根据营销Id获取营销详细信息
	 * 
	 * @author zhangchen
	 * @date 2023年6月28日 上午10:49:46
	 * @param marketingId
	 * @return
	 */
	@ApiOperation(value = "根据营销Id获取营销详细信息")
	@ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
	@RequestMapping(value = "/{marketingId}", method = RequestMethod.GET)
	public BaseResponse<MarketingForEndVO> getMarketingById(@PathVariable("marketingId") Long marketingId) {
		log.info("====进入/marketing/{marketingId}====");

		MarketingGetByIdByIdRequest marketingGetByIdRequest = new MarketingGetByIdByIdRequest();
		marketingGetByIdRequest.setMarketingId(marketingId);
		MarketingForEndVO marketingResponse = marketingQueryProvider.getByIdForSupplier(marketingGetByIdRequest).getContext()
				.getMarketingForEndVO();

		List<MarketingScopeVO> marketingScopeList = marketingResponse.getMarketingScopeList();
		Map<String, BoolFlag> scopeMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(marketingScopeList)) {
			scopeMap = marketingScopeList.stream().collect(
					Collectors.toMap(MarketingScopeVO::getScopeId, MarketingScopeVO::getTerminationFlag, (k1, k2) -> k1));
		}
		// 折扣商品：将折扣价设置为市场价
		for (GoodsInfoVO goodsInfoVO : marketingResponse.getGoodsList().getGoodsInfoPage()) {
			if (goodsInfoVO.getGoodsInfoType() == 1 && Objects.nonNull(goodsInfoVO.getSpecialPrice())) {
				goodsInfoVO.setSalePrice(goodsInfoVO.getSpecialPrice());
			}
			BoolFlag boolFlag = scopeMap.get(goodsInfoVO.getGoodsInfoId());
			goodsInfoVO.setTerminationFlag(boolFlag);
		}

		/**
		 * isShowActiveStatus决定营销活动详情页，商品条目操作拦是否展示操作"终止"按钮
		 */
		LocalDateTime beginTime = marketingResponse.getBeginTime();
		LocalDateTime endTime = marketingResponse.getEndTime();
		BoolFlag isPause = marketingResponse.getIsPause();
		DeleteFlag delFlag = marketingResponse.getDelFlag();
		BoolFlag isDraft = marketingResponse.getIsDraft();

		LocalDateTime now = LocalDateTime.now();
		boolean start = now.isAfter(beginTime) && now.isBefore(endTime) && BoolFlag.NO.equals(isPause)
				&& DeleteFlag.NO.equals(delFlag) && BoolFlag.NO.equals(isDraft);
		log.info("getMarketingById.start:{}", start);
		log.info("getMarketingById.isPause:{}", isPause);
		log.info("getMarketingById.delFlag:{}", delFlag);
		log.info("getMarketingById.isDraft:{}", isDraft);
		log.info("getMarketingById.beginTime:{}", beginTime);
		log.info("getMarketingById.endTime:{}", endTime);
		log.info("getMarketingById.now:{}", now);
		if (start) {
			marketingResponse.setIsShowActiveStatus(Boolean.TRUE);
		}

		// 暂时没有套餐，注掉
		// 填充套装活动关联商品的营销活动信息
//		if (marketingResponse.getMarketingType().equals(MarketingType.SUIT)) {
//			marketingResponse.getMarketingSuitDetialVOList().forEach(marketingSuitDetialVO -> {
//				MarketingGetByIdRequest request = new MarketingGetByIdRequest();
//				request.setMarketingId(marketingSuitDetialVO.getGoodsMarketingId());
//				marketingSuitDetialVO
//						.setMarketingVO(marketingQueryProvider.getById(request).getContext().getMarketingVO());
//			});
//		}
		return BaseResponse.success(marketingResponse);
	}

	/**
	 * 终止营销
	 * 
	 * @author zhangchen
	 * @date 2023年6月28日 上午9:51:31
	 * @param marketingId
	 * @return
	 */
	@ApiOperation(value = "终止营销活动")
	@ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
	@RequestMapping(value = "/termination/{marketingId}", method = RequestMethod.PUT)
	@Transactional
	public BaseResponse<?> terminationMarketingId(@PathVariable("marketingId") Long marketingId) {
		log.info("====进入/marketing/termination/{marketingId}====");

		MarketingStartByIdRequest marketingStartByIdRequest = new MarketingStartByIdRequest();
		marketingStartByIdRequest.setMarketingId(marketingId);
		marketingStartByIdRequest.setOperatorId(commonUtil.getOperatorId());
		marketingProvider.terminationMarketingById(marketingStartByIdRequest);

		MarketingVO marketingVo = getMarketingVo(marketingId);
		operateLogMQUtil.convertAndSend("营销", "终止促销活动",
				EnumTranslateUtil.getFieldAnnotation(marketingVo.getSubType()).get(marketingVo.getSubType().name()) + "-"
						+ marketingVo.getMarketingName());
		return BaseResponse.SUCCESSFUL();
	}

	/**
	 * 公共方法获取促销活动实体
	 * 
	 * @param marketId
	 * @return
	 */
	private MarketingVO getMarketingVo(long marketId) {
		MarketingGetByIdRequest request = new MarketingGetByIdRequest();
		request.setMarketingId(marketId);
		MarketingGetByIdResponse marketing = marketingQueryProvider.getById(request).getContext();
		return Objects.nonNull(marketing) ? marketing.getMarketingVO() : defaultMarketing();
	}

	/**
	 * 返回默认促销活动实体
	 * 
	 * @return
	 */
	private MarketingVO defaultMarketing() {
		MarketingVO marketingVO = new MarketingVO();
		marketingVO.setMarketingName("");
		marketingVO.setSubType(MarketingSubType.REDUCTION_FULL_AMOUNT);
		return marketingVO;
	}

	
    /**
     * 根据营销Id获取赠品信息
     * @param request 参数
     * @return
     */
    @ApiOperation(value = "根据营销Id获取赠品信息")
    @RequestMapping(value = "/fullGift/giftList", method = RequestMethod.POST)
    public BaseResponse<FullGiftLevelListByMarketingIdAndCustomerResponse> getGiftByMarketingId(@Valid @RequestBody FullGiftLevelListByMarketingIdAndCustomerRequest request) {
		log.info("====进入/marketing/fullGift/giftList====");
		
        CustomerGetByIdResponse customer = null;
        if(StringUtils.isNotBlank(request.getCustomerId())){
            customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();
        }
        request.setCustomer(KsBeanUtil.convert(customer,CustomerDTO.class));

        return fullGiftQueryProvider.listGiftByMarketingIdAndCustomerBoss(request);
    }
}
