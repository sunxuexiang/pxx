package com.wanmi.sbc.marketing;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreBaseInfoByIdRequest;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsViewByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdAndCustomerRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingPageRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCalculatorRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.gift.FullGiftLevelListByMarketingIdAndCustomerResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingCalculatorResultResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingGroupCardResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingPageResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.bean.dto.MarketingPageDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.marketing.request.MarketingPageListRequest;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.setting.api.provider.systemresource.SystemResourceQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemresourcecate.SystemResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.request.systemresource.SystemResourcePageRequest;
import com.wanmi.sbc.setting.api.request.systemresourcecate.SystemResourceCateByNameRequest;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.setting.bean.vo.SystemResourceCateVO;
import com.wanmi.sbc.setting.bean.vo.SystemResourceVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Api(tags = "MarketingController", description = "营销服务 API")
@RestController
@RequestMapping("/marketing")
@Validated
@Slf4j
public class MarketingController {

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;

    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private SystemResourceCateQueryProvider systemResourceCateQueryProvider;

    @Autowired
    private SystemResourceQueryProvider systemResourceQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    /**
     * 根据营销Id获取营销详细信息
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "根据营销Id获取营销详细信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/{marketingId}", method = RequestMethod.GET)
    public BaseResponse<MarketingForEndVO> getMarketingById(@PathVariable("marketingId")Long marketingId){
        CustomerVO customer = commonUtil.getCustomer();
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        MarketingForEndVO response = marketingQueryProvider.getByIdForCustomer(marketingGetByIdRequest).getContext().getMarketingForEndVO();
        // 校验店铺是否过期
        storeQueryProvider.getStoreBaseInfoById(new StoreBaseInfoByIdRequest(response.getStoreId()));

        //填充价格
        if(Objects.nonNull(response) && response.getMarketingType().equals(MarketingType.SUIT)){
            response.getMarketingSuitDetialVOList().forEach(marketingSuitDetialVO -> {
                //获取套装活动关联商品活动信息
                MarketingGetByIdRequest request = new MarketingGetByIdRequest();
                request.setMarketingId(marketingSuitDetialVO.getGoodsMarketingId());
                MarketingVO marketingVO = marketingQueryProvider.getById(request).getContext().getMarketingVO();
                marketingSuitDetialVO.setMarketingVO(marketingVO);

                //获取商品详情包括营销信息
                GoodsViewByIdResponse goodsResponse = detail(marketingSuitDetialVO.getGoodsInfoId(),customer);
                if (Objects.nonNull(goodsResponse.getGoods().getGoodsImg())) {
                    goodsResponse.getGoodsInfos().get(0).setGoodsInfoImg(goodsResponse.getGoods().getGoodsImg());
                }
                //如果是满赠带出赠品信息，并在下一步计算套装总原价
                if (marketingVO.getMarketingType().equals(MarketingType.GIFT)) {
                    //参与套装活动的商品只能设置1个级别的满赠活动
                    FullGiftLevelListByMarketingIdAndCustomerRequest giftLevelListRequest = new FullGiftLevelListByMarketingIdAndCustomerRequest();
                    giftLevelListRequest.setMarketingId(marketingVO.getMarketingId());
                    giftLevelListRequest.setMatchWareHouseFlag(true);
                    giftLevelListRequest.setCustomer(KsBeanUtil.convert(customer,CustomerDTO.class));
                    FullGiftLevelListByMarketingIdAndCustomerResponse fullResponse = fullGiftQueryProvider.listGiftByMarketingIdAndCustomer(giftLevelListRequest).getContext();

                    Map<String,GoodsInfoVO> giftMap = fullResponse.getGiftList().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
                    List<BigDecimal> giftMarketPrices = new ArrayList<>();
                    fullResponse.getLevelList().get(0).getFullGiftDetailList().forEach(marketingFullGiftDetailVO -> {
                        //赠品原价*赠品数量
                        giftMarketPrices.add(giftMap.get(marketingFullGiftDetailVO.getProductId()).getMarketPrice()
                                .multiply(new BigDecimal(marketingFullGiftDetailVO.getProductNum()))
                                .setScale(2,BigDecimal.ROUND_HALF_UP));
                    });
                    marketingSuitDetialVO.setGiftPriceSum(giftMarketPrices.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                }

                //如果是瞒折计算到手价
                if (CollectionUtils.isNotEmpty(goodsResponse.getGoodsInfos().get(0).getMarketingLabels())){
                    goodsResponse.getGoodsInfos().get(0).getMarketingLabels().forEach(marketingLabelVO -> {
                        if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1){
                            goodsResponse.getGoodsInfos().get(0).setTheirPrice(goodsResponse.getGoodsInfos().get(0).getMarketPrice()
                                    .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
                                    .setScale(2,BigDecimal.ROUND_HALF_UP));
                        }
                    });
                }
                marketingSuitDetialVO.setGoodsInfoVO(goodsResponse.getGoodsInfos().get(0));
            });

            log.info("计算营销价格后套餐详情商品list：setMarketingSuitDetialList:::{}",response.getMarketingSuitDetialVOList());
            List<GoodsInfoVO> goodsInfoVOS = response.getMarketingSuitDetialVOList().stream().map(MarketingSuitDetialVO::getGoodsInfoVO).collect(Collectors.toList());
            BigDecimal suitPrice = BigDecimal.ZERO;
            if (Objects.nonNull(goodsInfoVOS.get(0).getTheirPrice())) {
                suitPrice = goodsInfoVOS.stream().map(GoodsInfoVO::getTheirPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
            } else {
                suitPrice = goodsInfoVOS.stream().map(GoodsInfoVO::getMarketPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
            }

            //套装价格
            response.setSuitPrice(suitPrice.multiply(new BigDecimal(response.getSuitBuyNum())).setScale(2,BigDecimal.ROUND_HALF_UP));
            //原价
            if (response.getMarketingSuitDetialVOList().get(0).getMarketingVO().getMarketingType().equals(MarketingType.GIFT)) {
                //参与套装活动商品原价总和
                BigDecimal suitGoodsPrice = goodsInfoVOS.stream().map(GoodsInfoVO::getMarketPrice)
                        .reduce(BigDecimal.ZERO,BigDecimal::add).multiply(new BigDecimal(response.getSuitBuyNum())
                                .setScale(2,BigDecimal.ROUND_HALF_UP));
                //参与套装活动的商品参与的满赠活动的赠品的原价总和
                BigDecimal suitGiftPrice = response.getMarketingSuitDetialVOList().stream()
                        .map(MarketingSuitDetialVO::getGiftPriceSum).reduce(BigDecimal.ZERO,BigDecimal::add);
                response.setSuitInitPrice(suitGiftPrice.add(suitGoodsPrice));
            } else {
                response.setSuitInitPrice(goodsInfoVOS.stream().map(GoodsInfoVO::getMarketPrice)
                        .reduce(BigDecimal.ZERO,BigDecimal::add).multiply(new BigDecimal(response.getSuitBuyNum())
                                .setScale(2,BigDecimal.ROUND_HALF_UP)));
            }
        }

        return BaseResponse.success(response);
    }


    /**
     * 根据商品获取当前最优的营销活动（忽略未达到门槛的营销活动）
     */
    @ApiOperation(value = "根据商品获取当前最优的营销活动")
    @RequestMapping(value = "/optimalMarketingSingleGoods", method = RequestMethod.POST)
    BaseResponse<MarketingCalculatorResultResponse> optimalMarketingSingleGoods(@RequestBody @Valid MarketingCalculatorRequest request){

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("根据商品获取当前最优的营销活动");

        String goodsInfoId = request.getGoodsInfoId();
        CustomerVO customer = commonUtil.getCustomer();
        // 校验囤货活动
        List<PileActivityVO> contextOfPile = Optional.ofNullable(pileActivityProvider.getStartPileActivity()).map(BaseResponse::getContext)
                .orElse(Lists.newArrayList());
        if(CollectionUtils.isNotEmpty(contextOfPile) && Objects.nonNull(goodsInfoId)){
            PileActivityVO pileActivityVO = contextOfPile.get(0);
            PileActivityPileActivityGoodsRequest build = PileActivityPileActivityGoodsRequest.builder()
                    .pileActivityId(pileActivityVO.getActivityId())
                    .goodsInfoIds(Arrays.asList(goodsInfoId))
                    .build();
            List<PileActivityGoodsVO> startPileActivityPileActivityGoods = pileActivityProvider.getStartPileActivityPileActivityGoods(build).getContext();
            if(CollectionUtils.isNotEmpty(startPileActivityPileActivityGoods)){
                // 设置囤货活动标识
                request.setIsPileShopcart(BoolFlag.YES);
            }
        }
        stopWatch.stop();

        stopWatch.start("获取单个商品的最优营销活动");
        // 设置限购数量
        BaseResponse<MarketingCalculatorResultResponse> result = marketingQueryProvider.optimalMarketingSingleGoods(request);
        stopWatch.stop();
        stopWatch.start("营销限购判断");
        MarketingCalculatorResultResponse marketingCalculatorResultResponse = Optional.ofNullable(result).map(BaseResponse::getContext).orElse(null);
        if(Objects.nonNull(marketingCalculatorResultResponse)){
            Long marketingId = marketingCalculatorResultResponse.getMarketingId();
            if(Objects.nonNull(marketingId)){
                Long purchaseNumOfMarketing = this.getPurchaseNumOfMarketing(marketingId, goodsInfoId, customer.getCustomerId());

                // 如果达到满赠的限购数量，不展示赠品
                Long buyCount = request.getBuyCount();
                if(Objects.nonNull(purchaseNumOfMarketing) && Objects.nonNull(buyCount)){
                    if(buyCount > purchaseNumOfMarketing){
                        MarketingFullGiftLevelVO currentFullGiftLevel = marketingCalculatorResultResponse.getCurrentFullGiftLevel();
                        if(Objects.nonNull(currentFullGiftLevel)){
                            currentFullGiftLevel.setFullGiftDetailList(null);
                            marketingCalculatorResultResponse.setCurrentFullGiftLevel(currentFullGiftLevel);
                        }
                    }
                }

                marketingCalculatorResultResponse.setPurchaseNumOfMarketing(purchaseNumOfMarketing);
            }
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return BaseResponse.success(marketingCalculatorResultResponse);
    }

    /**
     * 分页获取所有套装购买信息
     * @param marketingPageListRequest
     * @return
     */
    @ApiOperation(value = "分页获取所有套装购买信息")
    @RequestMapping(value = "/page/suit-to-buy",method = RequestMethod.POST)
    public BaseResponse<MarketingPageResponse> pageSuitToBuyResponse(@RequestBody MarketingPageListRequest marketingPageListRequest){
        CustomerVO customer = commonUtil.getCustomer();
        marketingPageListRequest.setMarketingSubType(MarketingSubType.SUIT_TO_BUY);
        marketingPageListRequest.setQueryTab(MarketingStatus.STARTED);
        marketingPageListRequest.setDelFlag(DeleteFlag.NO);
        marketingPageListRequest.setTerminationFlag(DefaultFlag.NO);
        MarketingPageRequest marketingPageRequest = new MarketingPageRequest();
        marketingPageRequest.setMarketingPageDTO(KsBeanUtil.convert(marketingPageListRequest, MarketingPageDTO.class));
        BaseResponse<MarketingPageResponse> pageResponse = marketingQueryProvider.page(marketingPageRequest);

        //填充商品信息
        List<Long> marketingIds = pageResponse.getContext().getMarketingVOS().stream().map(MarketingPageVO::getMarketingId).collect(Collectors.toList());
        MarketingQueryByIdsRequest request = new MarketingQueryByIdsRequest();
        request.setMarketingIds(marketingIds);
        Map<Long,List<MarketingSuitDetialVO>> marketingSuitDetialMap = marketingQueryProvider.getSuitToBuyGoods(request).getContext();
        pageResponse.getContext().getMarketingVOS().forEach(marketingPageVO -> {
            if (CollectionUtils.isNotEmpty(marketingSuitDetialMap.getOrDefault(marketingPageVO.getMarketingId(), Collections.EMPTY_LIST))) {
                marketingPageVO.setMarketingSuitDetialList(marketingSuitDetialMap.get(marketingPageVO.getMarketingId()));
                //计算营销价格
                marketingPageVO.getMarketingSuitDetialList().forEach(marketingSuitDetialVO -> {
                    MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
                    marketingGetByIdRequest.setMarketingId(marketingSuitDetialVO.getGoodsMarketingId());
                    MarketingVO marketingVO = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
                    marketingSuitDetialVO.setMarketingVO(marketingVO);

                    //获取商品详情包括营销信息
                    GoodsViewByIdResponse response = detail(marketingSuitDetialVO.getGoodsInfoId(),customer);
                    if (Objects.nonNull(response.getGoods().getGoodsImg())) {
                        response.getGoodsInfos().get(0).setGoodsInfoImg(response.getGoods().getGoodsImg());
                    }

                    //如果是满赠带出赠品信息，并在下一步计算套装总原价
                    if (marketingVO.getMarketingType().equals(MarketingType.GIFT)) {
                        //参与套装活动的商品只能设置1个级别的满赠活动
                        FullGiftLevelListByMarketingIdAndCustomerRequest giftLevelListRequest = new FullGiftLevelListByMarketingIdAndCustomerRequest();
                        giftLevelListRequest.setMarketingId(marketingVO.getMarketingId());
                        giftLevelListRequest.setMatchWareHouseFlag(true);
                        giftLevelListRequest.setCustomer(KsBeanUtil.convert(customer,CustomerDTO.class));
                        FullGiftLevelListByMarketingIdAndCustomerResponse fullResponse = fullGiftQueryProvider.listGiftByMarketingIdAndCustomer(giftLevelListRequest).getContext();

                        Map<String,GoodsInfoVO> giftMap = fullResponse.getGiftList().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
                        List<BigDecimal> giftMarketPrices = new ArrayList<>();
                        fullResponse.getLevelList().get(0).getFullGiftDetailList().forEach(marketingFullGiftDetailVO -> {
                            //赠品原价*赠品数量
                            giftMarketPrices.add(giftMap.get(marketingFullGiftDetailVO.getProductId()).getMarketPrice()
                                    .multiply(new BigDecimal(marketingFullGiftDetailVO.getProductNum()))
                                    .setScale(2,BigDecimal.ROUND_HALF_UP));
                        });
                        marketingSuitDetialVO.setGiftPriceSum(giftMarketPrices.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                    }

                    //如果是瞒折计算到手价
                    if (CollectionUtils.isNotEmpty(response.getGoodsInfos().get(0).getMarketingLabels())){
                        response.getGoodsInfos().get(0).getMarketingLabels().forEach(marketingLabelVO -> {
                            if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                    && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1){
                                response.getGoodsInfos().get(0).setTheirPrice(response.getGoodsInfos().get(0).getMarketPrice()
                                        .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
                                        .setScale(2,BigDecimal.ROUND_HALF_UP));
                            }
                        });
                    }
                    marketingSuitDetialVO.setGoodsInfoVO(response.getGoodsInfos().get(0));
                });
                log.info("计算营销价格后套餐商品list：setMarketingSuitDetialList:::{}",marketingPageVO.getMarketingSuitDetialList());
                List<GoodsInfoVO> goodsInfoVOS = marketingPageVO.getMarketingSuitDetialList().stream().map(MarketingSuitDetialVO::getGoodsInfoVO).collect(Collectors.toList());
                BigDecimal suitPrice = BigDecimal.ZERO;
                if (Objects.nonNull(goodsInfoVOS.get(0).getTheirPrice())) {
                    suitPrice = goodsInfoVOS.stream().map(GoodsInfoVO::getTheirPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
                } else {
                    suitPrice = goodsInfoVOS.stream().map(GoodsInfoVO::getMarketPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
                }

                //套装价格
                marketingPageVO.setSuitPrice(suitPrice.multiply(new BigDecimal(marketingPageVO.getSuitBuyNum())).setScale(2,BigDecimal.ROUND_HALF_UP));
                //原价
                if (marketingPageVO.getMarketingSuitDetialList().get(0).getMarketingVO().getMarketingType().equals(MarketingType.GIFT)) {
                    //参与套装活动商品原价总和
                    BigDecimal suitGoodsPrice = goodsInfoVOS.stream().map(GoodsInfoVO::getMarketPrice)
                            .reduce(BigDecimal.ZERO,BigDecimal::add).multiply(new BigDecimal(marketingPageVO.getSuitBuyNum())
                                    .setScale(2,BigDecimal.ROUND_HALF_UP));
                    //参与套装活动的商品参与的满赠活动的赠品的原价总和
                    BigDecimal suitGiftPrice = marketingPageVO.getMarketingSuitDetialList().stream()
                            .map(MarketingSuitDetialVO::getGiftPriceSum).reduce(BigDecimal.ZERO,BigDecimal::add);
                    marketingPageVO.setSuitInitPrice(suitGiftPrice.add(suitGoodsPrice));
                } else {
                    marketingPageVO.setSuitInitPrice(goodsInfoVOS.stream().map(GoodsInfoVO::getMarketPrice)
                            .reduce(BigDecimal.ZERO,BigDecimal::add).multiply(new BigDecimal(marketingPageVO.getSuitBuyNum())
                                    .setScale(2,BigDecimal.ROUND_HALF_UP)));
                }

            }
        });

        //查询套装购买营销活动头图
        SystemResourceCateVO systemResourceCateVO = systemResourceCateQueryProvider.getByName(SystemResourceCateByNameRequest
                .builder().cateName("套装购买营销头图").build()).getContext().getSystemResourceCateVO();
        if (Objects.nonNull(systemResourceCateVO)) {
            SystemResourceVO systemResourceVO = systemResourceQueryProvider.page(SystemResourcePageRequest.builder()
                    .cateId(systemResourceCateVO.getCateId()).resourceType(ResourceType.IMAGE).build())
                    .getContext().getSystemResourceVOPage().getContent().get(0);
            if (Objects.nonNull(systemResourceVO)) {
                pageResponse.getContext().setActiveTopImage(systemResourceVO.getArtworkUrl());
            }
        }
        return BaseResponse.success(pageResponse.getContext());
    }

    /**
     * 商品详情
     *
     * @param skuId    商品skuId
     * @param customer 会员
     * @return 商品详情
     */
    private GoodsViewByIdResponse detail(String skuId, CustomerVO customer) {
        GoodsViewByIdResponse response = goodsDetailBaseInfo(skuId, 1L, true);

        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos().stream()
                .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            response = detailGoodsInfoVOList(response, goodsInfoVOList, customer);
        }
        return response;
    }

    /**
     * SPU商品详情
     *
     * @param response
     * @param goodsInfoVOList
     * @param customer
     * @return
     */
    private GoodsViewByIdResponse detailGoodsInfoVOList(GoodsViewByIdResponse response, List<GoodsInfoVO>
            goodsInfoVOList, CustomerVO customer) {
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            //计算营销价格
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            if (Objects.nonNull(customer)) {
                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            }
            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class));
            response.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext()
                    .getGoodsInfoVOList());

            //商品详情营销文案更改，其他地方不变
            if(Objects.nonNull(response.getGoodsInfos())){
                response.getGoodsInfos().forEach(goodsInfoVO -> {

                    if(Objects.nonNull(goodsInfoVO.getShelflife()) && goodsInfoVO.getShelflife() == 9999){
                        goodsInfoVO.setShelflife(0L);
                    }

//                    if(Objects.nonNull(goodsInfoVO.getMarketingLabels())){
//                        goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
//                            String desc = marketingLabelVO.getMarketingDesc();
//                            List<String> descList = marketingLabelVO.getMarketingDescList();
//                            if(Objects.nonNull(desc) && desc.indexOf("（") != -1){
//                                String newDesc = "跨单品"+desc.substring(0, desc.indexOf("（"));
//                                marketingLabelVO.setMarketingDesc(newDesc);
//                                if (Objects.nonNull(descList)){
//                                    List<String> newDescList = new ArrayList<>();
//                                    descList.forEach( s -> {
//                                        newDescList.add("跨单品"+s.substring(0,s.indexOf("（")));
//                                    });
//                                    marketingLabelVO.setMarketingDescList(newDescList);
//                                }
//                            }
//                        });
//                    }

                    if (Objects.nonNull(customer)
                            && null != customer.getEnterpriseStatusXyy()
                            && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                        //vip价格不参与营销活动商品
                        if (CollectionUtils.isEmpty(goodsInfoVO.getMarketingLabels())){
                            //特价商品销售价取市场价
                            if (goodsInfoVO.getGoodsInfoType() == 1) {
                                goodsInfoVO.setSalePrice(goodsInfoVO.getMarketPrice());
                            } else {
                                goodsInfoVO.setSalePrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                            }
                            goodsInfoVO.setEnterpriseStatusXyy(customer.getEnterpriseStatusXyy().toValue());
                            goodsInfoVO.setMarketPrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                        }
                    }
                });
            }

        }
        return response;
    }

    /**
     * SPU商品详情-基础信息（不包括区间价、营销信息）
     *
     * @param skuId 商品skuId
     * @return SPU商品详情
     */
    private GoodsViewByIdResponse goodsDetailBaseInfo(String skuId, Long wareId, Boolean matchWareHouseFlag) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).matchWareHouseFlag(matchWareHouseFlag).build()).getContext();

        if (Objects.isNull(goodsInfo)
                || Objects.equals(DeleteFlag.YES, goodsInfo.getDelFlag())
                || (!Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()))) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        if (AddedFlag.NO.toValue() == goodsInfo.getAddedFlag()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_ADDEDFLAG);
        }
        GoodsViewByIdRequest request = new GoodsViewByIdRequest();
        request.setGoodsId(goodsInfo.getGoodsId());
        request.setMatchWareHouseFlag(matchWareHouseFlag);
        if (wareId != null) {
            request.setWareId(wareId);
        }
        GoodsViewByIdResponse response = goodsQueryProvider.getViewById(request).getContext();

        return response;
    }

    /**
     * 获取限购提示
     * （1）数字：表示限购数量
     * （2）null：没有限购
     * @param marketingId
     * @param goodsInfoId
     * @param customerId
     * @return
     */
    private Long getPurchaseNumOfMarketing(Long marketingId, String goodsInfoId,String customerId){
        StopWatch stopWatch = new StopWatch();

        //查询营销总限购和单商品限购
        stopWatch.start("查询营销总限购和单商品限购");
        MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest = new MarketingScopeByMarketingIdRequest();
        marketingScopeByMarketingIdRequest.setMarketingId(marketingId);
        marketingScopeByMarketingIdRequest.setSkuId(goodsInfoId);
        BaseResponse<MarketingScopeByMarketingIdResponse> marketingScopeByMarketingIdResponseBaseResponse = marketingScopeQueryProvider.listByMarketingIdAndSkuIdAndCache(marketingScopeByMarketingIdRequest);
        stopWatch.stop();
        List<MarketingScopeVO> marketingScopeVOList = Optional.ofNullable(marketingScopeByMarketingIdResponseBaseResponse).map(BaseResponse::getContext)
                .map(MarketingScopeByMarketingIdResponse::getMarketingScopeVOList)
                .orElse(Lists.newArrayList());
        if(CollectionUtils.isEmpty(marketingScopeVOList)){
            return null;
        }
        MarketingScopeVO marketingScopeVO = marketingScopeVOList.stream().findFirst().get();
        Long purchaseNum = marketingScopeVO.getPurchaseNum(); // 营销总限购量
        Long perUserPurchaseNum = marketingScopeVO.getPerUserPurchaseNum(); // 营销单用户限购量

        // TODO log
        log.info("getPurchaseNumOfMarketing--->总限购数：{}",purchaseNum);
        log.info("getPurchaseNumOfMarketing--->单用户限购数：{}",perUserPurchaseNum);

        // 1. 后台未设置限购数量
        if(Objects.isNull(purchaseNum) && Objects.isNull(perUserPurchaseNum)){
            return null;
        }

        AtomicReference<BigDecimal> marketingNum = new AtomicReference<>(BigDecimal.ZERO); // 已占用的限购物数量（总限购）
        AtomicReference<BigDecimal> marketinguNumOfPerUser = new AtomicReference<>(BigDecimal.ZERO);// 已占用的单用户限购（单用户）

        //通过用户id查询当前商品的营销购买数量
        stopWatch.start("通过用户id查询当前商品的营销购买数量");
        Map<String,Object> req = new LinkedHashMap<>();
        req.put("customerId", customerId);
        req.put("marketingId",marketingId);
        req.put("goodsInfoId", goodsInfoId);
        List<MarketingPurchaseLimitVO> purchaseLimits = marketingPurchaseLimitProvider.queryListByParmNoUser(req).getContext();
        stopWatch.stop();
        stopWatch.start("查询trade");
        if (CollectionUtils.isNotEmpty(purchaseLimits)){
            List<String> collect = purchaseLimits.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
            //获取生效订单
            List<TradeVO> context2 = tradeQueryProvider.getOrderByIdsSimplify(collect).getContext();
            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
            purchaseLimits = purchaseLimits.stream().filter(q->{
                if (collect1.contains(q.getTradeId())){
                   return true;
                }
                return false;
            }).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(purchaseLimits)){
                BigDecimal reduce = purchaseLimits.stream().map(MarketingPurchaseLimitVO::getNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                marketingNum.set(marketingNum.get().add(reduce));
            }
        }
        // TODO log
        log.info("getPurchaseNumOfMarketing--->总限购已售卖数量：{}",marketingNum.get());
        stopWatch.stop();
        stopWatch.start("查询trade");
        List<MarketingPurchaseLimitVO> purchaseLimitForPerUsers = marketingPurchaseLimitProvider.queryListByParm(req).getContext();
        if (CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
            List<String> collect = purchaseLimitForPerUsers.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
            //获取生效订单
            List<TradeVO> context2 = tradeQueryProvider.getOrderByIdsSimplify(collect).getContext();
            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
            purchaseLimitForPerUsers = purchaseLimitForPerUsers.stream().filter(q->{
                if (collect1.contains(q.getTradeId())){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
                BigDecimal reduce = purchaseLimitForPerUsers.stream().map(MarketingPurchaseLimitVO::getNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                marketinguNumOfPerUser.set(marketinguNumOfPerUser.get().add(reduce));
            }

        }
        // TODO log
        log.info("getPurchaseNumOfMarketing--->单用户限购已售卖数量：{}",marketinguNumOfPerUser.get());
        stopWatch.stop();
        // 2. 该商品的没有购买记录,直接返回设置值
        if(CollectionUtils.isEmpty(purchaseLimits) && CollectionUtils.isEmpty(purchaseLimitForPerUsers)){
            if(Objects.nonNull(purchaseNum) && Objects.nonNull(perUserPurchaseNum)) {
                Long min = Long.min(purchaseNum, perUserPurchaseNum);
                return Long.max(min, 0L);
            }
            if(Objects.nonNull(purchaseNum)){
                return Long.max(purchaseNum, 0L);
            }
            return Long.max(perUserPurchaseNum, 0L);
        }

        // 3. 有购买记录-两个限购均有
        if(CollectionUtils.isNotEmpty(purchaseLimits) && CollectionUtils.isNotEmpty(purchaseLimitForPerUsers)){
            Long l1 =  Long.max(purchaseNum - marketingNum.get().longValue(), 0L);
            if(Objects.isNull(perUserPurchaseNum)){
                return l1;
            }
            Long l2 =  Long.max( perUserPurchaseNum - marketinguNumOfPerUser.get().longValue(), 0L);
            return Long.min(l1, l2);
        }

        // 4. 有购买记录-只有营销总限购
        if(CollectionUtils.isNotEmpty(purchaseLimits)){
            return Long.max(purchaseNum - marketingNum.get().longValue(), 0L);
        }

        log.info(stopWatch.prettyPrint());
        // 5. 有购买记录-只有单用户限购
        return  Long.max( perUserPurchaseNum - marketinguNumOfPerUser.get().longValue(), 0L);
    }
}
