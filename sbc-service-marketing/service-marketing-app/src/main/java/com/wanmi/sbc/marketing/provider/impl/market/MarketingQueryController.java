package com.wanmi.sbc.marketing.provider.impl.market;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoResponseVO;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCalculatorRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCardGroupRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingEffectiveRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingTradeBatchWrapperRequest;
import com.wanmi.sbc.marketing.api.response.market.*;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingTradeBatchWrapperResponse;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingWrapperDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.request.MarketingQueryListRequest;
import com.wanmi.sbc.marketing.common.request.MarketingRequest;
import com.wanmi.sbc.marketing.common.request.SkuExistsRequest;
import com.wanmi.sbc.marketing.common.request.TradeItemInfo;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.common.service.MarkeingGoodsLevelService;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.gift.service.MarketingFullGiftService;
import com.wanmi.sbc.marketing.marketing.MarketService;
import com.wanmi.sbc.marketing.marketing.price.MarketingCalculatorResult;
import com.wanmi.sbc.marketing.suittobuy.model.root.MarketingSuitDetail;
import com.wanmi.sbc.marketing.suittobuy.repository.MarketingSuitDetialRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-21 14:27
 */
@Slf4j
@Validated
@RestController
public class MarketingQueryController implements MarketingQueryProvider {

    @Autowired
    private MarketingService marketingService;

    @Autowired
    private MarketService marketService;

    @Autowired
    private MarkeingGoodsLevelService markeingGoodsLevelService;

    @Autowired
    private MarketingSuitDetialRepository marketingSuitDetialRepository;

    @Autowired
    private MarketingFullGiftService marketingFullGiftService;

    @Autowired
    GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Override
    public BaseResponse<MarketingTradeBatchWrapperResponse> batchWrapper(MarketingTradeBatchWrapperRequest request) {
        List<TradeMarketingWrapperDTO> wraperDTOList = request.getWraperDTOList();
        List<TradeMarketingWrapperVO> tradeMarketingWrapperVOS = marketService.batchWrapper(wraperDTOList);
        MarketingTradeBatchWrapperResponse marketingTradeBatchWrapperResponse = MarketingTradeBatchWrapperResponse.builder()
                .wraperVOList(tradeMarketingWrapperVOS)
                .build();
        return BaseResponse.success(marketingTradeBatchWrapperResponse);
    }

    /**
     * 促销分组接口
     * @param request
     * @return
     */
    @Override
    public BaseResponse<MarketingGroupCardResponse> goodsGroupByMarketing(MarketingCardGroupRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("营销分组调用总耗时");
        MarketingGroupCardResponse marketingGroupCardResponse = marketService.marketingGroupList(request);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return BaseResponse.success(marketingGroupCardResponse);
    }

    @Override
    public BaseResponse<MarketingGroupCardResponse> singleMarketingGroupList(MarketingCardGroupRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("单个营销分组调用总耗时");
        MarketingGroupCardResponse marketingGroupCardResponse = marketService.singleMarketingGroupList(request);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return BaseResponse.success(marketingGroupCardResponse);
    }

    @Override
    public BaseResponse<MarketingCalculatorResultResponse> optimalMarketingSingleGoods(@Valid MarketingCalculatorRequest request) {
        String goodsInfoId = request.getGoodsInfoId();
        GoodsInfoByIdResponse context = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(goodsInfoId).build()).getContext();
        log.info("MarketingQueryController optimalMarketingSingleGoods context: {}",JSON.toJSONString(context));
        if(Objects.isNull(context)){
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        DevanningGoodsInfoVO devanningGoodsInfoVO = KsBeanUtil.convert(context, DevanningGoodsInfoVO.class);
        devanningGoodsInfoVO.setBuyCount(request.getBuyCount());
        devanningGoodsInfoVO.setPileFlag(BoolFlag.YES);

        MarketingCalculatorResult marketingCalculatorResult = marketService.optimalMarketingSingleGoods(devanningGoodsInfoVO);
        log.info("MarketingQueryController optimalMarketingSingleGoods marketingCalculatorResult: {}",JSON.toJSONString(marketingCalculatorResult));
        MarketingCalculatorResultResponse convert = KsBeanUtil.convert(marketingCalculatorResult, MarketingCalculatorResultResponse.class);
        return BaseResponse.success(convert);
    }

    @Override
    public BaseResponse<MarketingByGoodsInfoIdResponse> getMarketingByGoodsInfo(MarketingByGoodsIdRequest request) {
        List<String> goodsinfos = request.getGoodsinfos();
        Set<String> goodsInfoIds = new HashSet<>(goodsinfos);
        Map<String, List<MarketingVO>> marketingByGoodsInfoId = marketService.getMarketingByGoodsInfoId(goodsInfoIds);
        return BaseResponse.success(MarketingByGoodsInfoIdResponse.builder().marketingVOS(marketingByGoodsInfoId).build());
    }

    /**
     * 父营销类型（满折，满减，满折）下的子营销类型（满金额，满数量）之间 商品不能重复
     * 父营销之间可重复
     * @param request 查询参数 {@link ExistsSkuByMarketingTypeRequest}
     * @return
     */
    @Override
    public BaseResponse<List<String>> queryExistsSkuByMarketingType(@RequestBody @Valid ExistsSkuByMarketingTypeRequest request) {
        List<String> skuList = marketingService.getExistsSkuByMarketingType(request.getStoreId(),
                KsBeanUtil.convert(request.getSkuExistsDTO(), SkuExistsRequest.class));
        return BaseResponse.success(skuList);
    }

    @Override
    public BaseResponse<List<String>> queryExistsMarketingByMarketingType(ExistsSkuByMarketingTypeRequest request) {
        List<String> skuList = marketingService.getExistsMarketingByMarketingType(request.getStoreId(),
                KsBeanUtil.convert(request.getSkuExistsDTO(), SkuExistsRequest.class));
        return BaseResponse.success(skuList);
    }

    @Override
    public BaseResponse<List<String>> queryExistsSkuByMarketingTypeFullOrder(@Valid ExistsSkuByMarketingTypeRequest request) {
        List<String> skuList = marketingService.getExistsSkuByMarketingTypeOrderFull(request.getStoreId(),
                KsBeanUtil.convert(request.getSkuExistsDTO(), SkuExistsRequest.class));
        return BaseResponse.success(skuList);
    }

    /**
     * @param marketingPageRequest 分页查询参数 {@link MarketingPageRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingPageResponse> page(@RequestBody @Valid MarketingPageRequest marketingPageRequest) {
        MarketingQueryListRequest convert = KsBeanUtil.convert(marketingPageRequest.getMarketingPageDTO(),
                MarketingQueryListRequest.class);
        convert.setGoodsInfoIds(marketingPageRequest.getMarketingPageDTO().getGoodsInfoIds());
        MicroServicePage<MarketingPageVO> marketingPage = marketingService.getMarketingPage(convert, marketingPageRequest.getStoreId());
        return BaseResponse.success(MarketingPageResponse.builder().marketingVOS(marketingPage).build());
    }

    @Override
    public BaseResponse<MarketingPageResponse> pageForSuit(MarketingPageRequest marketingPageRequest) {
        MicroServicePage<MarketingPageVO> marketingPage = marketingService.getMarketingForSuitPage(KsBeanUtil.convert(marketingPageRequest.getMarketingPageDTO(),
                MarketingQueryListRequest.class), marketingPageRequest.getStoreId());
        return BaseResponse.success(MarketingPageResponse.builder().marketingVOS(marketingPage).build());
    }

    /**
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingGetByIdResponse> getById(@RequestBody @Valid MarketingGetByIdRequest getByIdRequest) {
        MarketingVO marketingVO = KsBeanUtil.convert(marketingService.queryById(getByIdRequest.getMarketingId()), MarketingVO.class);
        return BaseResponse.success(MarketingGetByIdResponse.builder().marketingVO(marketingVO).build());
    }

    /**
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingGetByIdForSupplierResponse> getByIdForSupplier(@RequestBody @Valid MarketingGetByIdByIdRequest getByIdRequest) {
        MarketingResponse marketingResponse = marketingService.getMarketingByIdForSupplier(getByIdRequest.getMarketingId());
        MarketingForEndVO marketingForEndVO = KsBeanUtil.convert(marketingResponse, MarketingForEndVO.class);
        marketingForEndVO.setIsAddMarketingName(marketingResponse.getIsAddMarketingName());
        boolean ifexist = Objects.isNull(marketingResponse.getGoodsList());
        marketingForEndVO.setGoodsList(
                GoodsInfoResponseVO.builder()
                        .brands(ifexist ? Arrays.asList() : marketingResponse.getGoodsList().getBrands())
                        .cates(ifexist ? Arrays.asList() : marketingResponse.getGoodsList().getCates())
                        .goodses(ifexist ? Arrays.asList() : marketingResponse.getGoodsList().getGoodses())
                        .goodsInfoPage(ifexist ? new MicroServicePage() : marketingResponse.getGoodsList().getGoodsInfoPage()).build());
        return BaseResponse.success(MarketingGetByIdForSupplierResponse.builder().marketingForEndVO(marketingForEndVO).build());

    }

    /**
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingGetByIdForCustomerResponse> getByIdForCustomer(@RequestBody @Valid MarketingGetByIdRequest getByIdRequest) {
        MarketingForEndVO marketingForEndVO = KsBeanUtil.convert(marketingService.getMarketingByIdForCustomer(getByIdRequest.getMarketingId()), MarketingForEndVO.class);
        return BaseResponse.success(MarketingGetByIdForCustomerResponse.builder().marketingForEndVO(marketingForEndVO).build());
    }

    /**
     * @param queryByIdsRequest 唯一编号参数列表 {@link MarketingGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingQueryByIdsResponse> queryByIds(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest) {
        List<MarketingVO> marketingVOList =KsBeanUtil.convert(marketingService.queryByIds(queryByIdsRequest.getMarketingIds()),MarketingVO.class);
        return BaseResponse.success(MarketingQueryByIdsResponse.builder().marketingVOList(marketingVOList).build());
    }

    /**
     * @param getByIdRequest 唯一编号参数 {@link MarketingGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingGetGoodsByIdResponse> getGoodsById(@RequestBody @Valid MarketingGetByIdRequest getByIdRequest) {
        GoodsInfoResponse goodsByMarketingId = marketingService.getGoodsByMarketingId(getByIdRequest.getMarketingId());
        return BaseResponse.success(MarketingGetGoodsByIdResponse.builder()
        .goodsInfoResponseVO(KsBeanUtil.convert(goodsByMarketingId,GoodsInfoResponseVO.class )).build());
    }

    /**
     * @param queryByIdsRequest 唯一编号列表参数 {@link MarketingQueryByIdsRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingQueryStartingByIdsResponse> queryStartingByIds(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest) {
        List<String> ids = marketingService.queryStartingMarketing(queryByIdsRequest.getMarketingIds());
        return BaseResponse.success(MarketingQueryStartingByIdsResponse.builder().marketingIdList(ids).build());
    }

    /**
     * @param request 查询参数 {@link MarketingMapGetByGoodsIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingMapGetByGoodsIdResponse> getMarketingMapByGoodsId(@RequestBody @Valid MarketingMapGetByGoodsIdRequest request) {
        MarketingRequest marketingRequest = KsBeanUtil.convert(request, MarketingRequest.class);
        Map<String, List<MarketingResponse>> marketingMapByGoodsId = marketingService.getMarketingMapByGoodsId
                (marketingRequest);
        return BaseResponse.success(MarketingMapGetByGoodsIdResponse.builder()
                .listMap(MarketingConvert.marketingRes2MarketingForEnd(marketingMapByGoodsId)).build());
    }

    /**
     * @param request 查询参数 {@link MarketingMapGetByGoodsIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingOrderMapResponse> getOrderMarketingMap(@RequestBody @Valid MarketingMapGetByGoodsIdRequest request) {
        MarketingRequest marketingRequest = KsBeanUtil.convert(request, MarketingRequest.class);
        Map<String, List<MarketingResponse>> marketingMapByGoodsId = marketingService.getMarketingMapByGoodsId
                (marketingRequest);
        Map<String, List<MarketingVO>> listMap = new HashMap<>();
        marketingMapByGoodsId.entrySet().stream().forEach(goodsMap->{
            List<MarketingVO> convert = KsBeanUtil.convert(goodsMap.getValue(), MarketingVO.class);
           listMap.put(goodsMap.getKey(),convert);
        });
        return BaseResponse.success(MarketingOrderMapResponse.builder().listMap(listMap).build());
    }

    @Override
    public BaseResponse<MarketingListResponse> list(@RequestBody @Valid MarketingBaseRequest marketingBaseRequest) {
        List<MarketingVO> marketingVOS = marketingService.list(marketingBaseRequest);
        return BaseResponse.success(MarketingListResponse.builder().marketingVOList(marketingVOS).build());
    }

    @Override
    public BaseResponse<MarketingGoodsForXsiteResponse> queryForXsite(@RequestBody @Valid MarketingGoodsForXsiteRequest request){
        BaseResponse<MarketingGoodsForXsiteResponse> response = markeingGoodsLevelService.dealByMarketingLevel(request.getGoodsInfoIds(), request.getMarketingLevelType());
        return response;
    }

    @Override
    public BaseResponse<MarketingGetByIdsForCustomerResponse> getByIdsForCustomer(@RequestBody @Valid MarketingQueryByIdsRequest request) {
        List<MarketingResponse> marketingByIdsForCustomer = marketingService.getMarketingByIdsForCustomer(request.getMarketingIds());
        List<MarketingForEndVO> marketingForEndVOS = new ArrayList<>();
        for (MarketingResponse marketingResponse : marketingByIdsForCustomer) {
            MarketingForEndVO endVO = new MarketingForEndVO();
            BeanUtils.copyProperties(marketingResponse, endVO);
            marketingForEndVOS.add(endVO);
        }
        return BaseResponse.success(MarketingGetByIdsForCustomerResponse.builder().marketingForEndVOS(marketingForEndVOS).build());
    }

    @Override
    public BaseResponse<MarketingQueryByIdsResponse> getChooseGoodsMarketing(@RequestBody @Valid MarketingQueryByIdsRequest queryByIdsRequest) {
        List<MarketingVO> marketingVOList =KsBeanUtil.convert(marketingService.getChooseGoodsMarketingList(queryByIdsRequest.getMarketingIds()),MarketingVO.class);
        return BaseResponse.success(MarketingQueryByIdsResponse.builder().marketingVOList(marketingVOList).build());
    }

    @Override
    public BaseResponse<Map<Long, List<MarketingSuitDetialVO>>> getSuitToBuyGoods(MarketingQueryByIdsRequest queryByIdsRequest) {
        return BaseResponse.success(marketingService.getSuitDetialByMarketingIds(queryByIdsRequest.getMarketingIds()));
    }

    @Override
    public BaseResponse<List<MarketingSuitDetialVO>> getSuitToBuyByGoodInfoIds(MarketingMapGetByGoodsIdRequest request) {

        List<MarketingSuitDetail> suitToBuyByGoodInfoIds = marketingSuitDetialRepository.getSuitToBuyByGoodInfoIds(request.getGoodsInfoIdList());

        if(CollectionUtils.isEmpty(suitToBuyByGoodInfoIds)){
            return BaseResponse.SUCCESSFUL();
        }

        return BaseResponse.success(KsBeanUtil.convert(suitToBuyByGoodInfoIds, MarketingSuitDetialVO.class));
    }

    @Override
    public BaseResponse<List<MarketingVO>> getSuitByMarketingIds(MarketingQueryByIdsRequest queryByIdsRequest) {

        if(CollectionUtils.isEmpty(queryByIdsRequest.getMarketingIds())){
            return BaseResponse.SUCCESSFUL();
        }

        List<MarketingSuitDetail> marketingSuitDetails = marketingSuitDetialRepository.queryMarketingSuitDetailsByGoodsMarketingIds(queryByIdsRequest.getMarketingIds());

        if(CollectionUtils.isEmpty(marketingSuitDetails)){
            return BaseResponse.SUCCESSFUL();
        }
        List<Long> marketingIds = marketingSuitDetails.stream().map(m -> m.getMarketingId()).distinct().collect(Collectors.toList());
        //查询当前营销活动id是否套装活动
        List<Marketing> marketings = marketingService.queryByIds(marketingIds);
        if(CollectionUtils.isEmpty(marketings)){
            return BaseResponse.SUCCESSFUL();
        }

        List<MarketingVO> marketingVOList = Lists.newArrayList();
        marketings.forEach(m->{
           if(MarketingSubType.SUIT_TO_BUY.equals(m.getSubType())){
               marketingVOList.add(KsBeanUtil.convert(m,MarketingVO.class));
           }
        });

        return BaseResponse.success(marketingVOList);
    }

    @Override
    public BaseResponse<List<MarketingSuitDetialVO>> getSuitDetialByMarketingIds(MarketingQueryByIdsRequest queryByIdsRequest) {
        List<MarketingSuitDetail> marketingSuitDetails = marketingSuitDetialRepository.findMarketingSuitDetailByMarketingId(queryByIdsRequest.getMarketingId());
        return BaseResponse.success(KsBeanUtil.convert(marketingSuitDetails,MarketingSuitDetialVO.class));
    }

    @Override
    public BaseResponse<List<String>> getMarketingScope(@Valid AddActivitGoodsRequest request) {
        List<MarketingScope> marketingScope = marketingService.getMarketingScope(request.getMarketingId());
        List<String> goodsInfoIds = marketingScope.stream().map(o -> o.getScopeId()).collect(Collectors.toList());
        return BaseResponse.success(goodsInfoIds);
    }

    @Override
    public BaseResponse stopGiveGoods(MarketingStopGiveGoodsRequest request) {
        marketService.stopGiveGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<String>> addActivityGiveGoods(@Valid AddActivityGiveGoodsRequest request) {
        return BaseResponse.success(marketService.addActivityGiveGoods(request));
    }

    @Override
    public BaseResponse addActivityGoods(@Valid AddActivitGoodsRequest request) {
        return BaseResponse.success(marketService.addActivityGoods(request));
    }

    @Override
    public BaseResponse<List<String>> getMarketingGiveGoods(@Valid AddActivitGoodsRequest request) {
        List<MarketingFullGiftLevel> marketingFullGiftLevels = marketingFullGiftService.getLevelsByMarketingId(request.getMarketingId());
        if(CollectionUtils.isEmpty(marketingFullGiftLevels)){
            return BaseResponse.SUCCESSFUL();
        }
        List<String> goodsInfoIds = new ArrayList<>();
        for (MarketingFullGiftLevel marketingFullGiftLevel : marketingFullGiftLevels) {

            List<MarketingFullGiftDetail> fullGiftDetailList = marketingFullGiftLevel.getFullGiftDetailList();
            if(CollectionUtils.isEmpty(fullGiftDetailList)){continue;}

            List<String> collect = fullGiftDetailList.stream()
                    .map(o -> o.getProductId())
                    .collect(Collectors.toList());
            goodsInfoIds.addAll(collect);

        }
        return BaseResponse.success(goodsInfoIds);
    }

    @Override
    public BaseResponse<MarketingByGoodsInfoIdAndIdResponse> getEffectiveMarketingByIdsAndGoods(MarketingEffectiveRequest request) {
        if (request.getType().equalsIgnoreCase("redis")){
            return BaseResponse.success( marketService.getEffectiveMarketingByIdsAndGoods(request.getTradeMarketingList(),KsBeanUtil.convert(request.getTradeItems(),TradeItemInfo.class),request.getWareId()));
        }else {
            return BaseResponse.success( marketService.getEffectiveMarketingByIdsAndGoodsByMysql(request.getTradeMarketingList(),KsBeanUtil.convert(request.getTradeItems(),TradeItemInfo.class),request.getWareId()));

        }
    }

    @Override
    public BaseResponse<MarketingEffectiveStoreResponse> listEffectiveStore() {
    	List<Long> activeMarketingStoreIds = marketingService.getActiveMarketingStoreIds();
        return BaseResponse.success(MarketingEffectiveStoreResponse.builder().storeIds(activeMarketingStoreIds).build());
    }

    @Override
    public BaseResponse<List<String>> listEffectiveStoreGoodsInfoIds() {
        return BaseResponse.success(marketingService.listEffectiveStoreGoodsInfoIds());
    }
}
