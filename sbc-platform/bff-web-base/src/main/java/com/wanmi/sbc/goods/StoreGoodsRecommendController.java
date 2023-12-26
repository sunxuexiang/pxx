package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomListRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelWithDefaultByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.LiveRoomVO;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsImagestype.GoodsImageStypeProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendsetting.GoodsRecommendSettingQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsViewByIdRequest;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteListRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.GoodsRecommendGoodsListResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.response.GoodsRecommendEsResponse;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.bean.vo.LiveHaveGoodsVO;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-24 14:12
 * @Description: TODO
 * @Version 1.0
 */
@Api(description = "商户商品推荐配置管理API", tags = "StoreGoodsRecommendController")
@RestController
@RequestMapping(value = "/goods/store/recommend")
@Slf4j
public class StoreGoodsRecommendController {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private GoodsRecommendGoodsQueryProvider goodsRecommendGoodsQueryProvider;
    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;
    @Autowired
    private OsUtil osUtil;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;
    @Autowired
    private LiveGoodsQueryProvider liveGoodsQueryProvider;
    @Autowired
    private MarketingPluginProvider marketingPluginProvider;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PileActivityProvider pileActivityProvider;
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;
    @Autowired
    private ShopCartProvider shopCartProvider;
    @Autowired
    private GoodsImageStypeProvider goodsImageStypeProvider;

    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    /**
     * 推荐商品（GoodsRecommendSettingController 与这个有区别）
     * TODO 基于大数据的没做  以后可在此处接口改造（待算法工程师）
     *
     * @return
     */
    @ApiOperation(value = "查询商品推荐配置")
    @PostMapping("/get-setting")
    public BaseResponse<GoodsRecommendEsResponse> getSetting(@RequestBody EsGoodsInfoQueryRequest queryRequest, HttpServletRequest httpRequest) {
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        log.info("wareId::::{}",queryRequest.getWareId());
        //登录用户信息
        CustomerVO customerVO = commonUtil.getCustomer();
        boolean isEnterPrise = false;
        if (Objects.nonNull(customerVO) && (EnterpriseCheckState.CHECKED.equals(customerVO.getEnterpriseStatusXyy())
                || DefaultFlag.YES.equals(customerVO.getVipFlag()))) {
            isEnterPrise = true;
        }
        List<GoodsInfoVO> goodsInfoList = new ArrayList<>();
        Integer totalPages = null;
        Long totalElements = null;
        String key =RedisKeyConstant.SCREEN_ORDER_ADD_LAST_TIME+queryRequest.getCompanyInfoId()+"_"+customerVO.getCustomerId();
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        GoodsRecommendSettingVO recommendSettingVO = new GoodsRecommendSettingVO();
        String string = redisService.getString(key);
        List<String> goodsId = JSONObject.parseArray(string, String.class);
        if (CollectionUtils.isNotEmpty(goodsId)){

        }else{
            //直接从推荐列表拿
            BaseResponse<GoodsRecommendGoodsListResponse> list = goodsRecommendGoodsQueryProvider.list(GoodsRecommendGoodsListRequest.builder().companyInfoId(queryRequest.getCompanyInfoId()).build());
            goodsId=list.getContext().getGoodsRecommendGoodsVOList().stream().map(GoodsRecommendGoodsVO::getGoodsInfoId).distinct().collect(Collectors.toList());
        }
            String redisKey = RedisKeyConstant.STORE_RECOMMEND_PAGE_SETTING.concat(queryRequest.getWareId().toString()).concat(":").concat(String.valueOf(queryRequest.getPageNum())).concat(":").concat(String.valueOf(queryRequest.getPageSize()));
            if (redisService.hasKey(redisKey)) {
                // goodsInfoList = redisService.getList(redisKey,GoodsRecommendEsResponse.class);

                GoodsRecommendEsResponse esResponse = redisService.getObj(redisKey, GoodsRecommendEsResponse.class);
                goodsInfoList = esResponse.getGoodsInfoVOS();
                // log.info("获取缓存goodsInfoList:::{}",goodsInfoList);

                totalPages = esResponse.getTotalPages();
                totalElements = esResponse.getTotalElements();
            }else{
                //通过es查询推荐商品信息==》包含营销等全部信息
                queryRequest.setGoodsInfoIds(goodsId);
                // queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
                // queryRequest.setPageSize(1000);
                EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customerVO);
                goodsInfoList = esGoodsLimitBrandResponse.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                        .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                        .collect(Collectors.toList());
                Map<String,String> goodsVOMap = esGoodsLimitBrandResponse.getGoodsList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId,GoodsVO::getGoodsSubtitle));

                Map<String,String> goodsSubtitleNewMap = esGoodsLimitBrandResponse.getGoodsList().stream().filter(var-> StringUtils.isNotEmpty(var.getGoodsSubtitleNew())).collect(Collectors.toMap(GoodsVO::getGoodsId,GoodsVO::getGoodsSubtitleNew));
                goodsInfoList.forEach(goodsInfoVO -> {
                    if (StringUtils.isNotBlank(goodsVOMap.get(goodsInfoVO.getGoodsId()))) {
                        goodsInfoVO.setGoodsSubtitle(goodsVOMap.get(goodsInfoVO.getGoodsId()));
                        goodsInfoVO.setGoodsSubtitleNew(goodsSubtitleNewMap.get(goodsInfoVO.getGoodsId()));
                    }
                    if(Objects.isNull(goodsInfoVO.getRecommendSort())){
                        goodsInfoVO.setRecommendSort(0);
                    }
                    //计算到手价
                    this.calTheirPrice(goodsInfoVO);
                });
                //排序顺序 升序
                // log.info("获取es商品信息：：：{}",goodsInfoList);
                goodsInfoList.sort(Comparator.comparing(GoodsInfoVO::getRecommendSort));
                totalPages = esGoodsLimitBrandResponse.getEsGoods().getTotalPages();
                totalElements = esGoodsLimitBrandResponse.getEsGoods().getTotalElements();
                GoodsRecommendEsResponse settingResponse = GoodsRecommendEsResponse
                        .builder()
                        .goodsInfoVOS(goodsInfoList)
                        .totalPages(esGoodsLimitBrandResponse.getEsGoods().getTotalPages())
                        .totalElements(esGoodsLimitBrandResponse.getEsGoods().getTotalElements())
                        .build();
                redisService.setString(redisKey, JSONObject.toJSONString(settingResponse, SerializerFeature.DisableCircularReferenceDetect),60);
            }

        GoodsViewByIdRequest request = new GoodsViewByIdRequest();
        GoodsInfoVO first = goodsInfoList.stream().findFirst().orElse(null);
        if(Objects.nonNull(first)){
            request.setGoodsId(first.getGoodsId());
            GoodsViewByIdResponse goodsViewByIdResponse = goodsQueryProvider.getViewById(request).getContext();
            first.setGoodsImages(goodsViewByIdResponse.getImages());
        }

        if (CollectionUtils.isNotEmpty(goodsInfoList)) {
            //设置上商品参与囤货活动
            List<String> goodsInfoIds = goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            List<PileActivityVO> context = pileActivityProvider.getStartPileActivity().getContext();
            if (CollectionUtils.isNotEmpty(goodsInfoIds) && CollectionUtils.isNotEmpty(context)) {
                PileActivityVO pileActivityVO = context.get(0);

                BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods =
                        pileActivityProvider.getStartPileActivityPileActivityGoods(
                                PileActivityPileActivityGoodsRequest.builder()
                                        .pileActivityId(pileActivityVO.getActivityId()).goodsInfoIds(goodsInfoIds).build());
                BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
                if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){

                    Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));

                    goodsInfoList.forEach(goodsInfo -> {
                        if(Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))){
                            goodsInfo.setVirtualStock(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock().longValue());
                            if(pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                                goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                            }else {
                                goodsInfo.setPileFlag(ForcePileFlag.PILE);
                            }
                        }else {
                            goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                        }
                    });
                }
            }

            List<GoodsImageStypeVO> context1 = goodsImageStypeProvider.getHcImageByGoodsIds(goodsInfoList.stream()
                    .map(GoodsInfoVO::getGoodsId).collect(Collectors.toList())).getContext();
            Map<String, GoodsImageStypeVO> collect = context1.stream()
                    .collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
            Map<String, GoodsImageStypeVO> finalCollect = collect;
            AtomicInteger i = new AtomicInteger();
            goodsInfoList.forEach(goodsInfo -> {
                i.set(i.get() + 1);
                //设置直播
                LiveHaveGoodsVO liveHaveGoodsVO=liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
                if(liveHaveGoodsVO.getIsHaveLive()==1){
                    goodsInfo.setIsHaveLive(1);
                    goodsInfo.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
                    goodsInfo.setLiveId(liveHaveGoodsVO.getLiveId());
                }else{
                    goodsInfo.setIsHaveLive(0);
                }
                //设置商品状态
                if (goodsInfo.getGoodsInfoName().endsWith("T") || goodsInfo.getGoodsInfoName().endsWith("t")) {
                    if (goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                } else {
                    if (goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                }
                if(CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())){
                    if (i.get()==1){
                        if(CollectionUtils.isNotEmpty(goodsInfo.getGoodsImages())){
                            GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(goodsInfo.getGoodsId());
                            if (Objects.nonNull(goodsImageStypeVO)){
                                goodsInfo.getGoodsImages().get(0).setArtworkUrl(goodsImageStypeVO.getArtwork_url());
                            }
                        }
                    }else {
                        GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(goodsInfo.getGoodsId());
                        if (Objects.nonNull(goodsImageStypeVO)){
                            goodsInfo.setGoodsInfoImg(goodsImageStypeVO.getArtwork_url());
                        }
                    }

                }

            });

            //限购
            goodsInfoList.forEach(goodsInfoVO -> {
                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                    }
                }
            });
        }
        GoodsRecommendEsResponse settingResponse = GoodsRecommendEsResponse.builder()
                .goodsRecommendSettingVO(recommendSettingVO)
                .goodsInfoVOS(goodsInfoList)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
        return BaseResponse.success(settingResponse);
    }


    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse list(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer) {
        //推荐商品排序
        queryRequest.setSortFlag(12);
        queryRequest.setIsKeywords(false);

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
            vipPriceFlag = true;
        }
        if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())) {
            vipPriceFlag = true;
        }
        if (Objects.nonNull(domainInfo)) {
            queryRequest.setStoreId(domainInfo.getStoreId());
        }

        //只看分享赚商品信息
        if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue() ==
                queryRequest.getDistributionGoodsAudit()) {
            queryRequest.setDistributionGoodsStatus(NumberUtils.INTEGER_ZERO);
        }

        //获取会员和等级
        queryRequest.setQueryGoods(true);
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        //B2b模式下，可以按会员/客户价格排序，否则按市场价排序
        if (Objects.nonNull(customer) && osUtil.isB2b()) {
            CustomerLevelWithDefaultByCustomerIdResponse response = customerLevelQueryProvider
                    .getCustomerLevelWithDefaultByCustomerId(
                            CustomerLevelWithDefaultByCustomerIdRequest.builder().customerId(customer.getCustomerId()
                            ).build
                                    ()).getContext();
            queryRequest.setCustomerLevelId(response.getLevelId());
            queryRequest.setCustomerLevelDiscount(response.getLevelDiscount());
        } else {
            String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
            queryRequest.setContractStartDate(now);
            queryRequest.setContractEndDate(now);
            queryRequest.setCustomerLevelId(0L);
            queryRequest.setCustomerLevelDiscount(BigDecimal.ONE);
        }
        EsGoodsResponse response;
        //分类查询特定情况，通过分类绑定的品牌进行排序
        response = esGoodsInfoElasticService.pageByGoods(queryRequest);


        if (Objects.nonNull(response.getEsGoods()) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            //根据开关重新设置分销商品标识
            distributionService.checkDistributionSwitch(goodsInfoList);
            //只看分享赚商品信息
            if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue()
                    == queryRequest.getDistributionGoodsAudit()) {
                goodsInfoList = goodsInfoList.stream().filter(goodsInfoVO -> DistributionGoodsAudit.CHECKED.equals
                        (goodsInfoVO.getDistributionGoodsAudit())).collect(Collectors.toList());
            }
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsLimitBrandResponse();
            }
            List<String> skuIds =
                    goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            //批量查询sku信息
            Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                    GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
            ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));
            for (GoodsInfoVO goodsInfo : goodsInfoList) {
                GoodsInfoVO goodsInfoVO = goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO());
                //填充商品保质期
                if(Objects.nonNull(goodsInfoVO.getShelflife()) && goodsInfoVO.getShelflife() == 9999){
                    goodsInfo.setShelflife(0L);
                }else{
                    goodsInfo.setShelflife(goodsInfoVO.getShelflife());
                }
                if (Objects.nonNull(goodsInfoVO.getIsSuitGoods())) {
                    goodsInfo.setIsSuitGoods(goodsInfoVO.getIsSuitGoods());
                    goodsInfo.setChoseProductSkuId(goodsInfoVO.getChoseProductSkuId());
                } else {
                    goodsInfo.setIsSuitGoods(DefaultFlag.NO);
                }
                //填充询价标志
                goodsInfo.setInquiryFlag(goodsInfoVO.getInquiryFlag());

                if (vipPriceFlag && Objects.nonNull(goodsInfoVO.getVipPrice())) {
                    goodsInfo.setVipPrice(goodsInfoVO.getVipPrice());
                }
                //是否隐藏
                if (Objects.nonNull(goodsInfoVO.getHiddenFlag())) {
                    goodsInfo.setHiddenFlag(goodsInfoVO.getHiddenFlag());
                } else {
                    goodsInfo.setHiddenFlag(0);
                }
            }
            //计算区间价
            GoodsIntervalPriceByCustomerIdRequest priceRequest = new GoodsIntervalPriceByCustomerIdRequest();
            priceRequest.setGoodsInfoDTOList(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class));
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                priceRequest.setCustomerId(customer.getCustomerId());
            }
            GoodsIntervalPriceByCustomerIdResponse priceResponse =
                    goodsIntervalPriceProvider.putByCustomerId(priceRequest).getContext();
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoList = priceResponse.getGoodsInfoVOList();
            //计算营销价格
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class));
            if (Objects.nonNull(customer)) {
                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            }
            GoodsInfoListByGoodsInfoResponse filterResponse = marketingPluginProvider.goodsListFilter(filterRequest)
                    .getContext();

            if (Objects.nonNull(filterResponse) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                goodsInfoList = filterResponse.getGoodsInfoVOList();
            }
            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());

                //购物车
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount(request)
                        .getContext();
                goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
            }
            //重新赋值于Page内部对象
            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy
                    (GoodsInfoVO::getGoodsId));
            Boolean finalVipPriceFlag = vipPriceFlag;
            Set<Long> storeIds = new HashSet<>(10);
            for (EsGoods inner : response.getEsGoods()) {
                Set<Long> collect = inner.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId).collect(Collectors.toSet());
                storeIds.addAll(collect);
            }

            //通过客户收货地址和商品指定区域设置商品状态
            //根据用户ID得到收货地址
//            CustomerDeliveryAddressResponse deliveryAddress = null;
//            if (Objects.nonNull(customer)) {
//                deliveryAddress = commonUtil.getDeliveryAddress();
//            }
//            CustomerDeliveryAddressResponse finalDeliveryAddress = deliveryAddress;

            response.getEsGoods().getContent().forEach(esGoods -> {
                List<GoodsInfoVO> goodsInfoVOList = voMap.get(esGoods.getId());
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoVOList)) {
                    goodsInfoVOList.forEach(goodsInfoVO -> {
                        goodsInfoVO.setGoodsFavorableCommentNum(esGoods.getGoodsFavorableCommentNum());
                        goodsInfoVO.setGoodsSalesNum(esGoods.getGoodsSalesNum());
                        goodsInfoVO.setGoodsCollectNum(esGoods.getGoodsCollectNum());
                        goodsInfoVO.setGoodsEvaluateNum(esGoods.getGoodsEvaluateNum());
                    });
                    List<GoodsInfoNest> resultGoodsInfos = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoNest.class);

                    //设置库存
                    List<Long> unOnline = new ArrayList<>(10);
                    if (Objects.nonNull(queryRequest.getMatchWareHouseFlag()) && !queryRequest.getMatchWareHouseFlag()) {
                        unOnline = commonUtil.getWareHouseByStoreId(new ArrayList<>(storeIds), WareHouseType.STORRWAREHOUSE).stream()
                                .map(WareHouseVO::getWareId).collect(Collectors.toList());
                    }
                    List<Long> finalUnOnline = unOnline;
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {
                            List<GoodsWareStockVO> goodsWareStockVOS = optionalGoodsInfoNest.get().getGoodsWareStockVOS();
                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsWareStockVOS)) {
                                List<GoodsWareStockVO> stockList;
                                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(finalUnOnline)) {
                                    stockList = goodsWareStockVOS.stream().
                                            filter(goodsWareStock -> finalUnOnline.contains(goodsWareStock.getWareId())).
                                            collect(Collectors.toList());
                                } else {
                                    stockList = goodsWareStockVOS;
                                }
                                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(stockList)) {
                                    BigDecimal sumStock = stockList.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                                    goodsInfoNest.setStock(sumStock);
                                } else {
                                    goodsInfoNest.setStock(BigDecimal.ZERO);
                                }

                                //重新填充商品状态
                                if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
                                        && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
                                    goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                                    // 判断是否有T，如果是1，就设置为2
                                    if (goodsInfoNest.getGoodsInfoName().endsWith("T") || goodsInfoNest.getGoodsInfoName().endsWith("t")) {
                                        if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0) {
                                            goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                                        }
                                    } else {
                                        if (goodsInfoNest.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                                            goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                                        }
                                    }

                                } else {
                                    goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                                }

                                goodsInfoNest.setGoodsWareStockVOS(optionalGoodsInfoNest.get().getGoodsWareStockVOS());
                            }
                        }
                    });
                    //设置企业商品的审核状态 ，以及会员的大客户价
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        if (finalVipPriceFlag
                                && Objects.nonNull(goodsInfoNest.getVipPrice())
                                && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                            if (goodsInfoNest.getGoodsInfoType() == 1) {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
                            } else {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                            }
                        }
                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {
                            goodsInfoNest.setEnterPriseAuditStatus(optionalGoodsInfoNest.get().getEnterPriseAuditStatus());
                        }
                        // log.info("GoodsBaseController.list goodsInfoNest:{}",goodsInfoNest);
                        //如果商品参加了营销活动，vip价格设置为0 vipPrice不参与任何营销活动
                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoNest.getMarketingLabels())) {
                            goodsInfoNest.setVipPrice(BigDecimal.ZERO);
                        }
                        //计算到手价
                        goodsInfoNest.getMarketingLabels().stream().forEach(marketingLabelVO -> {

                            if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                    && marketingLabelVO.getSubType() == 1 && marketingLabelVO.getNumber() == 1) {//1：满数量减
                                goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice().subtract(marketingLabelVO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));

                            }else if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                    && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1) {//3:满数量折
                                goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice()
                                        .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
                                        .setScale(2,BigDecimal.ROUND_HALF_UP));

                            }
                        });

//                        if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())
//                                && goodsInfoNest.getGoodsStatus().equals(GoodsStatus.OK)) {
//                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
//                                    .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
//                            //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
//                            if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
//                                goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                            }
//                        }
                    });
                    esGoods.setGoodsInfos(resultGoodsInfos);
                }
            });
        }

        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            response.getGoodsList().forEach(item -> {
                response.getEsGoods().getContent().forEach(esGoods -> {
                    if (esGoods.getId().equals(item.getGoodsId())) {
                        item.setGoodsLabels(esGoods.getGoodsLabels());
                    }
                });
            });

            response.getGoodsList().stream().forEach(item -> {
                List<GoodsLabelVO> collect = item.getGoodsLabels().stream()
                        .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                        .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                        .collect(Collectors.toList());
                item.setGoodsLabels(collect);
            });

            response.getEsGoods().getContent().stream().forEach(item -> {
                List<GoodsLabelVO> collect = item.getGoodsLabels().stream()
                        .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                        .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                        .collect(Collectors.toList());
                item.setGoodsLabels(collect);
            });

        }
        EsGoodsLimitBrandResponse page = KsBeanUtil.convert(response, EsGoodsLimitBrandResponse.class);
        page.setEsGoods(response.getEsGoods());


        // 获取直播商品
        Map<String, Long> liveMapLong = new HashMap<>();
        List<String> goodsInfoIds = new ArrayList<>();
        response.getEsGoods().getContent().forEach(esGoods -> {
            goodsInfoIds.addAll(esGoods.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList()));
        });
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoIds)) {
            // 根据商品id,查询直播商品的id
            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
            // 将数据转成map(商品sku,直播商品名称)
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(liveGoodsVOList)) {
                liveMapLong = liveGoodsVOList.stream().filter(entity -> {
                    return entity.getGoodsId() != null;
                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
                        LiveGoodsVO::getGoodsId));
            }
        }

        if (Objects.nonNull(liveMapLong)) {

            // 根据直播房价的id,查询直播信息
            LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
            liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
            liveRoomListReq.setDelFlag(DeleteFlag.NO);
            List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
            List<Long> liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(liveRoomVOList)) {
                final Map<String, Long> liveMap = liveMapLong;
                response.getEsGoods().getContent().forEach(item -> {
                    item.getGoodsInfos().stream().forEach(goodsInfoNest -> {
                        Long liveGoodsId = liveMap.get(goodsInfoNest.getGoodsInfoId());
                        if (liveGoodsId != null) {
                            if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId)) {
                                List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId, GoodsLiveRoomVO.class);
                                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
                                    for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
                                        if (liveRoomIdList.contains(liveRoomVO.getRoomId())) {
                                            goodsInfoNest.setLiveEndTime(liveRoomVO.getLiveEndTime());
                                            goodsInfoNest.setLiveStartTime(liveRoomVO.getLiveStartTime());
                                            goodsInfoNest.setRoomId(liveRoomVO.getRoomId());
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    });

                });
            }
        }

        return page;
    }
    /**
     * 计算到手价
     * @param goodsInfoVO
     */
    private void calTheirPrice(GoodsInfoVO goodsInfoVO) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoVO.getMarketingLabels())) {
            goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
                if (!marketingLabelVO.getMarketingType().equals(4) && !marketingLabelVO.getMarketingType().equals(2)) {

                    marketingLabelVO.getLevelLabelVOS().forEach(levelLabelVO->{
                        if (marketingLabelVO.getMarketingType().equals(0)) { //满减
                            //单件优惠金额
                            BigDecimal discount = BigDecimal.ZERO;
                            //满减到手价需要购买满足的件数
                            BigDecimal theirPriceFullCount = BigDecimal.ZERO;
                            if (marketingLabelVO.getSubType().equals(0)) { //满金额
                                theirPriceFullCount = levelLabelVO.getAmount()
                                        .divide(goodsInfoVO.getMarketPrice(),5,BigDecimal.ROUND_HALF_UP)
                                        .setScale(0, BigDecimal.ROUND_UP);
                                discount = levelLabelVO.getReduction()
                                        .divide(theirPriceFullCount,2,BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            } else if (marketingLabelVO.getSubType().equals(1)) { //满数量
                                theirPriceFullCount = BigDecimal.valueOf(levelLabelVO.getNumber());
                                discount = levelLabelVO.getReduction()
                                        .divide(theirPriceFullCount,2,BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                            if ( discount.compareTo(Objects.isNull(goodsInfoVO.getTheirPriceDiscount())?BigDecimal.ZERO:goodsInfoVO.getTheirPriceDiscount())>0){
                                goodsInfoVO.setTheirPrice(goodsInfoVO.getMarketPrice().subtract(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
                                goodsInfoVO.setTheirPriceDiscount(discount);
                                goodsInfoVO.setTheirPriceFullCount(theirPriceFullCount);
                                goodsInfoVO.setTheirPriceFullCountForDiscount(null);
                            }


                        } else if (marketingLabelVO.getMarketingType().equals(1)) { //瞒折
                            //折扣前总金额
                            BigDecimal totalPrice = BigDecimal.ZERO;
                            //折扣后单价即到手价
                            BigDecimal discount = BigDecimal.ZERO;
                            //满折到手价需要购买满足的件数
                            BigDecimal theirPriceFullCountForDiscount = BigDecimal.ZERO;
                            if (marketingLabelVO.getSubType().equals(2)) { //满金额
                                //需要购买件数，才能满足条件
                                theirPriceFullCountForDiscount = levelLabelVO.getAmount()
                                        .divide(goodsInfoVO.getMarketPrice(),5,BigDecimal.ROUND_HALF_UP)
                                        .setScale(0, BigDecimal.ROUND_UP);
                                totalPrice = goodsInfoVO.getMarketPrice()
                                        .multiply(theirPriceFullCountForDiscount).setScale(5, BigDecimal.ROUND_HALF_UP);
                                //折扣后单价即到手价
                                discount = totalPrice.multiply(levelLabelVO.getDiscount()
                                                .divide(new BigDecimal(10),2,BigDecimal.ROUND_HALF_UP))
                                        .divide(theirPriceFullCountForDiscount,2,BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            } else if (marketingLabelVO.getSubType().equals(3)) { //满数量
                                theirPriceFullCountForDiscount = BigDecimal.valueOf(levelLabelVO.getNumber());
                                totalPrice = goodsInfoVO.getMarketPrice()
                                        .multiply(theirPriceFullCountForDiscount)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                                discount = totalPrice.multiply(levelLabelVO.getDiscount()
                                                .divide(new BigDecimal(10),2,BigDecimal.ROUND_HALF_UP))
                                        .divide(theirPriceFullCountForDiscount,2,BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            }

                            BigDecimal bigDecimal = goodsInfoVO.getMarketPrice().subtract(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                            if ( bigDecimal.compareTo(Objects.isNull(goodsInfoVO.getTheirPriceDiscount())?BigDecimal.ZERO:goodsInfoVO.getTheirPriceDiscount())>0){
                                goodsInfoVO.setTheirPrice(discount);
                                goodsInfoVO.setTheirPriceDiscount(bigDecimal);
                                goodsInfoVO.setTheirPriceFullCountForDiscount(theirPriceFullCountForDiscount);
                                goodsInfoVO.setTheirPriceFullCount(null);
                            }
                        }
                    });



                }
            });
        }
    }
}
