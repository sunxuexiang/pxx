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
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
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
import com.wanmi.sbc.es.elastic.response.EsSearchResponse;
import com.wanmi.sbc.goods.api.provider.activitygoodspicture.ActivityGoodsPictureProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsImagestype.GoodsImageStypeProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendsetting.GoodsRecommendSettingQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsViewByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteListRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.AppGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsrecommendsetting.GoodsRecommendSettingResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.RecommendGuessLoveForStoreRequest;
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
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
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
 * 商品推荐 分页面
 *
 * @author chenyufei
 */
@Api(description = "商品推荐配置管理API", tags = "GoodsRecommendSettingController")
@RestController
@RequestMapping(value = "/goods/recommend/setting")
@Slf4j
public class GoodsRecommendSettingController {

    @Autowired
    private GoodsRecommendSettingQueryProvider goodsRecommendSettingQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private ShopCartProvider shopCartProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;

    @Autowired
    private LiveGoodsQueryProvider liveGoodsQueryProvider;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    private static double EARTH_RADIUS = 6378137;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OsUtil osUtil;


    @Autowired
    private LastGoodsWriteQueryProvider lastGoodsWriteQueryProvider;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;

    @Autowired
    private ActivityGoodsPictureProvider activityGoodsPictureProvider;

    @Autowired
    private GoodsImageStypeProvider goodsImageStypeProvider;

    @Autowired
    private MerchantConfigGoodsQueryProvider merchantConfigGoodsQueryProvider;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ResultsMapper resultsMapper;

    @Autowired
    private StoreQueryProvider storeQueryProvider;


    /**
     * 查询商品推荐配置
     * TODO 基于大数据的没做  以后可在此处接口改造
     *
     * @return
     */
    @ApiOperation(value = "查询商品推荐配置")
    @PostMapping("/get-setting")
//    @Cacheable(value = "RECOMMEND_PAGE_SETTING")
    public BaseResponse<GoodsRecommendEsResponse> getSetting(@RequestBody EsGoodsInfoQueryRequest queryRequest, HttpServletRequest httpRequest) {
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        log.info("wareId::::{}",queryRequest.getWareId());
        BaseResponse<GoodsRecommendSettingResponse> response = goodsRecommendSettingQueryProvider.getSetting(queryRequest.getWareId());

        CustomerVO customerVO = commonUtil.getCustomer();
        boolean isEnterPrise = false;
        if (Objects.nonNull(customerVO) && (EnterpriseCheckState.CHECKED.equals(customerVO.getEnterpriseStatusXyy())
                || DefaultFlag.YES.equals(customerVO.getVipFlag()))) {
            isEnterPrise = true;
        }

        List<GoodsRecommendSettingVO> goodsRecommendSettingVOS =
                response.getContext().getGoodsRecommendSettingVOS();
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        GoodsRecommendSettingVO recommendSettingVO = new GoodsRecommendSettingVO();
        List<GoodsInfoVO> goodsInfoList = new ArrayList<>();
        Integer totalPages = null;
        Long totalElements = null;
        if (CollectionUtils.isNotEmpty(goodsRecommendSettingVOS)) {
            for (GoodsRecommendSettingVO goodsRecommendSettingVO : goodsRecommendSettingVOS) {
                //TODO 基于大数据
                if (goodsRecommendSettingVO.getRule() == 1) {

                } else {
                    //手动策略开启且商品推荐开关打开
                    if (RecommendStrategyStatus.OPEN.equals(goodsRecommendSettingVO.getIntelligentStrategy()) && GoodsRecommendStatus.OPEN.equals(goodsRecommendSettingVO.getEnabled())) {
                        //手动推荐
                        if (BoolFlag.NO.equals(goodsRecommendSettingVO.getIsIntelligentRecommend()) && CollectionUtils.isNotEmpty(goodsRecommendSettingVO.getGoodsInfoIds())) {
                            String redisKey = RedisKeyConstant.RECOMMEND_PAGE_SETTING.concat(queryRequest.getWareId().toString()).concat(":").concat(String.valueOf(queryRequest.getPageNum())).concat(":").concat(String.valueOf(queryRequest.getPageSize()));
                            if (redisService.hasKey(redisKey)) {
                                // goodsInfoList = redisService.getList(redisKey,GoodsRecommendEsResponse.class);

                                GoodsRecommendEsResponse esResponse = redisService.getObj(redisKey, GoodsRecommendEsResponse.class);
                                goodsInfoList = esResponse.getGoodsInfoVOS();
                                // log.info("获取缓存goodsInfoList:::{}",goodsInfoList);

                                totalPages = esResponse.getTotalPages();
                                totalElements = esResponse.getTotalElements();
                            } else {
                                //通过es查询推荐商品信息==》包含营销等全部信息
                                queryRequest.setGoodsInfoIds(goodsRecommendSettingVO.getGoodsInfoIds());
                                queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
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
                                // log.info("获取缓存配置的商品id：：：{}",goodsRecommendSettingVO.getGoodsInfoIds());
                                // redisService.setString(redisKey, JSONObject.toJSONString(goodsInfoList, SerializerFeature.DisableCircularReferenceDetect),60);
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
                        }
                        //智能推荐
                        if (BoolFlag.YES.equals(goodsRecommendSettingVO.getIsIntelligentRecommend())) {
                            Integer amount =
                                    goodsRecommendSettingVO.getIntelligentRecommendAmount();
                            if (Objects.isNull(amount) || Objects.equals(amount, 0)) {
                                break;
                            }
                            //用户为空不推荐商品
                            if (Objects.nonNull(customerVO)) {
                                //查询用户最后一次商品记录
                                GoodsInfoViewPageRequest viewPageRequest = new GoodsInfoViewPageRequest();
                                List<LastGoodsWriteVO> lastGoodsWriteVOList =
                                        lastGoodsWriteQueryProvider.list(LastGoodsWriteListRequest.builder().customerId(customerVO.getCustomerId()).build()).getContext().getLastGoodsWriteVOList();
                                if (CollectionUtils.isNotEmpty(lastGoodsWriteVOList)) {
                                    LastGoodsWriteVO lastGoodsWriteVO =
                                            lastGoodsWriteVOList.stream().sorted(Comparator.comparing(LastGoodsWriteVO::getCreateTime).reversed()).findFirst().get();
                                    Long cateId = lastGoodsWriteVO.getCateId();
                                    Long brandId = lastGoodsWriteVO.getBrandId();
                                    Integer dimensionality =
                                            goodsRecommendSettingVO.getIntelligentRecommendDimensionality();
                                    if (dimensionality == 0) {
                                        if (Objects.isNull(cateId)) {
                                            break;
                                        }
                                        viewPageRequest.setCateId(cateId);
                                    }
                                    if (dimensionality == 1) {
                                        if (Objects.isNull(brandId)) {
                                            break;
                                        }
                                        viewPageRequest.setBrandId(brandId);
                                    }
                                    viewPageRequest.setPageSize(amount);
                                    //'0:普通商品  1：特价商品',
                                    viewPageRequest.setGoodsInfoType(0);
                                    GoodsInfoViewPageResponse goodsInfoResponse =
                                            this.goodsInfoResponse(viewPageRequest);
                                    List<GoodsInfoVO> goodsInfos = new ArrayList<>();
                                    //综合
                                    if (goodsRecommendSettingVO.getPriority().toValue() == 5) {
                                        List<GoodsInfoVO> content = goodsInfoResponse.getGoodsInfoPage().getContent();
                                        List<GoodsInfoVO> goodsInfoVOS =
                                                content.stream().filter(c -> c.getAloneFlag() != null && c.getGoodsSalesNum() != null).collect(Collectors.toList());
                                        List<GoodsInfoVO> infoVOS =
                                                goodsInfoVOS.stream().sorted((o1, o2) -> o2.getGoodsSalesNum().compareTo(o1.getGoodsSalesNum()))
                                                        .sorted((o1, o2) -> o2.getAddedTime().compareTo(o1.getAddedTime())).collect(Collectors.toList());
                                        if (CollectionUtils.isNotEmpty(goodsInfoVOS)) {
                                            content.removeAll(goodsInfoVOS);
                                            content.addAll(infoVOS);
                                        }
                                        goodsInfos = content;
                                        //默认
                                    } else if (goodsRecommendSettingVO.getPriority().toValue() == 4) {
                                        List<GoodsInfoVO> content = goodsInfoResponse.getGoodsInfoPage().getContent();
                                        List<GoodsInfoVO> goodsInfoVOS =
                                                content.stream().filter(c -> Objects.nonNull(c.getGoods()) && c.getGoods().getBrandSeqNum() != null && c.getGoods().getGoodsSeqNum() != null).collect(Collectors.toList());
                                        List<GoodsInfoVO> infoVOS = goodsInfoVOS.stream()
                                                .sorted(Comparator.comparing(o -> o.getGoods().getBrandSeqNum()))
                                                .sorted(Comparator.comparing(o -> o.getGoods().getGoodsSeqNum()))
                                                .sorted((o1, o2) -> o2.getGoodsSalesNum().compareTo(o1.getGoodsSalesNum()))
                                                .sorted((o1, o2) -> o2.getAddedTime().compareTo(o1.getAddedTime()))
                                                .collect(Collectors.toList());
                                        //提出空值按照品牌排序序号 商品排序序号
                                        if (CollectionUtils.isNotEmpty(goodsInfoVOS)) {
                                            content.removeAll(goodsInfoVOS);
                                            content.addAll(infoVOS);
                                        }
                                        goodsInfos = content;

                                        //销量
                                    } else if (goodsRecommendSettingVO.getPriority().toValue() == 3) {
                                        goodsInfos = goodsInfoResponse.getGoodsInfoPage().getContent().stream()
                                                .sorted((o1, o2) -> o2.getGoods().getGoodsSalesNum().compareTo(o1.getGoods().getGoodsSalesNum()))
                                                .collect(Collectors.toList());
                                    }

                                    //封装对象
                                    setProperties(isEnterPrise, recommendSettingVO, goodsRecommendSettingVO, goodsInfoResponse, goodsInfos,customerVO);
                                }

                            }
                        }
                    }
                }

            }
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

            //活动图片处理
//        Map<String, ActivityGoodsResponse> collect = new HashMap<>();
//        if(CollectionUtils.isNotEmpty(goodsInfoIds)){
//            BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(goodsInfoIds).build());
//            List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
//            if(CollectionUtils.isNotEmpty(activityGoodsResponse)){
//                collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsInfoId, g -> g,(a,b)->a));
//            }
//        }
//
//        Map<String, ActivityGoodsResponse> finalCollect = collect;
            //获取所有合成图片地址 封装成map

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
                //设置参与活动商品的活动图片
//            if(CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())){
//                if(CollectionUtils.isNotEmpty(goodsInfo.getGoodsImages())){
//                    ActivityGoodsResponse activityGoodsResponse = finalCollect.get(goodsInfo.getGoodsInfoId());
//                    if(Objects.nonNull(activityGoodsResponse)){
//                        goodsInfo.getGoodsImages().get(0).setArtworkUrl(activityGoodsResponse.getImgPath());
//                    }
//                }
//            }


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
     * 计算到手价
     * @param goodsInfoVO
     */
    private void calTheirPrice(GoodsInfoVO goodsInfoVO) {
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoVO.getMarketingLabels())) {
            goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
                if (!marketingLabelVO.getMarketingType().equals(4) && !marketingLabelVO.getMarketingType().equals(2) && !marketingLabelVO.getMarketingType().equals(7)) {

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


    @ApiOperation(value = "查询购物车及商品详情页推荐(伪智能)")
    @PostMapping("/guessLove")
    public BaseResponse<GoodsRecommendEsResponse> guessLove(HttpServletRequest httpRequest){
        //获取到配置的商品推荐
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setMatchWareHouseFlag(true);
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        queryRequest.setPageNum(0);
        queryRequest.setPageSize(100);
        BaseResponse<GoodsRecommendEsResponse> setting = this.getSetting(queryRequest,httpRequest);

        GoodsRecommendEsResponse response = new GoodsRecommendEsResponse();
        List<GoodsInfoVO> randomList = new ArrayList<>();
        if(Objects.nonNull(setting) && Objects.nonNull(setting.getContext())){
            List<GoodsInfoVO> goodsInfoVOS = setting.getContext().getGoodsInfoVOS();
            if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
                if(goodsInfoVOS.size() > 10){
                    randomList = getRandomList(goodsInfoVOS, 10);
                }else{
                    randomList = getRandomList(goodsInfoVOS, goodsInfoVOS.size());
                }
            }
        }else{
            return BaseResponse.success(response);
        }
        response.setGoodsRecommendSettingVO(setting.getContext().getGoodsRecommendSettingVO());
        response.setGoodsInfoVOS(randomList);
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "查询购物车及商品详情页推荐(伪智能)")
    @PostMapping("/guessLove/for-store")
    public BaseResponse<GoodsRecommendEsResponse> guessLoveForStore(@RequestBody RecommendGuessLoveForStoreRequest request, HttpServletRequest httpRequest){
        //获取到配置的商品推荐
        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setGoodsInfoType(0);
        esGoodsInfoQueryRequest.setPageNum(0);
        esGoodsInfoQueryRequest.setPageSize(100);
        esGoodsInfoQueryRequest.setStoreId(request.getStoreId());
        BaseResponse<GoodsRecommendEsResponse> setting = this.getSettingForStore(esGoodsInfoQueryRequest, httpRequest);

        GoodsRecommendEsResponse response = new GoodsRecommendEsResponse();
        List<GoodsInfoVO> randomList = new ArrayList<>();
        if(Objects.nonNull(setting) && Objects.nonNull(setting.getContext())){
            List<GoodsInfoVO> goodsInfoVOS = setting.getContext().getGoodsInfoVOS();
            if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
                if(goodsInfoVOS.size() > 10){
                    randomList = getRandomList(goodsInfoVOS, 10);
                }else{
                    randomList = getRandomList(goodsInfoVOS, goodsInfoVOS.size());
                }
            }
        }else{
            return BaseResponse.success(response);
        }
        response.setGoodsRecommendSettingVO(setting.getContext().getGoodsRecommendSettingVO());
        response.setGoodsInfoVOS(randomList);
        return BaseResponse.success(response);
    }


    /**
     * 查询商品推荐配置
     * TODO 基于大数据的没做  以后可在此处接口改造
     *
     * @return
     */
    @ApiOperation(value = "查询商品推荐配置【商家首页】")
    @PostMapping("/get-setting/for-store")
    public BaseResponse<GoodsRecommendEsResponse> getSettingForStore(@RequestBody EsGoodsInfoQueryRequest queryRequest, HttpServletRequest httpRequest) {
        final AppGoodsQueryRequest appGoodsQueryRequest = new AppGoodsQueryRequest();
        appGoodsQueryRequest.setStoreId(queryRequest.getStoreId());
        String customerId = commonUtil.getOperator() == null || commonUtil.getOperator().getUserId() == null ? "-1" : commonUtil.getOperator().getUserId();
        appGoodsQueryRequest.setCustomerId(customerId);
        //"2c97d6e2892f271b01892fc07957001e"
        final BaseResponse<GoodsRecommendEsResponse> response = BaseResponse.success(GoodsRecommendEsResponse.builder().goodsInfoVOS(new ArrayList<>()).build());
        final List<String> goodsInfoIdsList = merchantConfigGoodsQueryProvider.getAppGoodsInfo(appGoodsQueryRequest).getContext().getGoodsInfoIds();
        if (CollectionUtils.isEmpty(goodsInfoIdsList)) {
            return response;
        }
//        final List<String> goodsInfoIdsList = Lists.newArrayList("2c97d6e2892f271b01892fc07957001e");
        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setStoreId(queryRequest.getStoreId());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setGoodsInfoIds(goodsInfoIdsList);
        esGoodsInfoQueryRequest.setGoodsInfoType(queryRequest.getGoodsInfoType());
        esGoodsInfoQueryRequest.setAuditStatus(1);
        esGoodsInfoQueryRequest.setBrandIds(queryRequest.getBrandIds());
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        esGoodsInfoQueryRequest.setContractStartDate(now);
        esGoodsInfoQueryRequest.setContractEndDate(now);
        final EsSearchResponse esGoodsInfoByParams = getEsGoodsInfoByParams(esGoodsInfoQueryRequest, goodsInfoIdsList.size());
        List<? extends EsSearchResponse.AggregationResultItem> cateBucket = esGoodsInfoByParams.getAggResultMap().get(
                "_id");
        List<String> matchSkuIds;
        if (CollectionUtils.isNotEmpty(cateBucket)){
            matchSkuIds = cateBucket.stream().map(EsSearchResponse.AggregationResultItem<String>::getKey).collect(Collectors.toList());
        }else {
            matchSkuIds = new ArrayList<>();
        }
        final Iterator<String> iterator = goodsInfoIdsList.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            if (!matchSkuIds.contains(next)){
                iterator.remove();
            }
        }
        if (CollectionUtils.isEmpty(goodsInfoIdsList)) {return response;}
        final int totals = goodsInfoIdsList.size();
        // 查找上架然后内存分页
        final Integer pageSize = queryRequest.getPageSize();
        List<String> pageData = goodsInfoIdsList.stream()
                .skip((queryRequest.getPageNum()) * pageSize) // 跳过前面的数据
                .limit(pageSize) // 取pageSize条数据
                .collect(Collectors.toList()); // 收集数据到新
        Long totalElements = (long) totals;
        Integer totalPages = totals / pageSize + 1;
        CustomerVO customerVO = commonUtil.getCustomer();
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        GoodsRecommendSettingVO recommendSettingVO = new GoodsRecommendSettingVO();
        List<GoodsInfoVO> goodsInfoList;
        // 后面放在hash
        String redisKey = RedisKeyConstant.RECOMMEND_PAGE_SETTING.concat(customerId).concat(":").concat(String.valueOf(queryRequest.getPageNum())).concat(":").concat(String.valueOf(pageSize));
        if (redisService.hasKey(redisKey)) {
            GoodsRecommendEsResponse esResponse = redisService.getObj(redisKey, GoodsRecommendEsResponse.class);
            goodsInfoList = esResponse.getGoodsInfoVOS();
            // log.info("获取缓存goodsInfoList:::{}", goodsInfoList);
            totalPages = esResponse.getTotalPages();
            totalElements = esResponse.getTotalElements();
        } else {
            //通过es查询推荐商品信息==》包含营销等全部信息
            queryRequest.setGoodsInfoIds(pageData);
//            queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
            queryRequest.setStoreId(queryRequest.getStoreId());
            EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customerVO);
            goodsInfoList = esGoodsLimitBrandResponse.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            Map<String, String> goodsVOMap = esGoodsLimitBrandResponse.getGoodsList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitle));

            Map<String, String> goodsSubtitleNewMap = esGoodsLimitBrandResponse.getGoodsList().stream().filter(var -> StringUtils.isNotEmpty(var.getGoodsSubtitleNew())).collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew));
            goodsInfoList.forEach(goodsInfoVO -> {
                if (StringUtils.isNotBlank(goodsVOMap.get(goodsInfoVO.getGoodsId()))) {
                    if (StringUtils.isBlank(goodsInfoVO.getGoodsSubtitle())) {
                        goodsInfoVO.setGoodsSubtitle(goodsVOMap.get(goodsInfoVO.getGoodsId()));
                    }
                    if (StringUtils.isBlank(goodsInfoVO.getGoodsSubtitleNew())) {
                        goodsInfoVO.setGoodsSubtitleNew(goodsSubtitleNewMap.get(goodsInfoVO.getGoodsId()));

                    }
                }
                if (Objects.isNull(goodsInfoVO.getRecommendSort())) {
                    goodsInfoVO.setRecommendSort(0);
                }
                //计算到手价
                this.calTheirPrice(goodsInfoVO);
            });
            //排序顺序 升序
            // log.info("获取es商品信息：：：{}", goodsInfoList);
//            goodsInfoList.sort(Comparator.comparing(GoodsInfoVO::getRecommendSort));
//            totalPages = esGoodsLimitBrandResponse.getEsGoods().getTotalPages();
//            totalElements = esGoodsLimitBrandResponse.getEsGoods().getTotalElements();
//            GoodsRecommendEsResponse settingResponse = GoodsRecommendEsResponse
//                    .builder()
//                    .goodsInfoVOS(goodsInfoList)
//                    .totalPages(totalPages)
//                    .totalElements(totalElements)
//                    .build();
//            redisService.setString(redisKey, JSONObject.toJSONString(settingResponse, SerializerFeature.DisableCircularReferenceDetect), 60);
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
        final Map<String, GoodsInfoVO> goodsInfoVOMap = goodsInfoList.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity(), (a, b) -> a));
        List<GoodsInfoVO> sortedList = new ArrayList<>();
        pageData.forEach(o -> {
            GoodsInfoVO goodsInfoVO = goodsInfoVOMap.get(o);
            if (Objects.nonNull(goodsInfoVO)) {
                sortedList.add(goodsInfoVO);
            }
        });
        //检测商品是否可预售
        this.checkGoodsPresellStock(queryRequest.getStoreId(), sortedList);

        GoodsRecommendEsResponse settingResponse = GoodsRecommendEsResponse.builder()
                .goodsRecommendSettingVO(recommendSettingVO)
                .goodsInfoVOS(sortedList)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();

        return BaseResponse.success(settingResponse);
    }
    /**
     * @Description list 随机取数据
     * @params     list    list集合
     *           num     随机取多少条
     **/
    public List<GoodsInfoVO> getRandomList(List<GoodsInfoVO> list, int num) {
        List<GoodsInfoVO> olist = new ArrayList<>();
        if (list.size() <= num) {
            return list;
        } else {
            Random random = new Random();
            for (int i = 0 ;i<num;i++){
                int intRandom = random.nextInt(list.size() - 1);
                olist.add(list.get(intRandom));
                list.remove(list.get(intRandom));
            }
            return olist;
        }
    }

    private void setProperties(boolean isEnterPrise, GoodsRecommendSettingVO recommendSettingVO,
                               GoodsRecommendSettingVO goodsRecommendSettingVO, GoodsInfoViewPageResponse goodsInfoResponse,
                               List<GoodsInfoVO> goodsInfos, CustomerVO customerVO) {
        //设置大客户价
        if (isEnterPrise) {
            goodsInfos.stream().forEach(g -> {
                if (g.getVipPrice() != null && BigDecimal.ZERO.compareTo(g.getVipPrice()) < 0) {
                    g.setSalePrice(g.getVipPrice());
                }
                //计算到手价
                this.calTheirPrice(g);
            });
        }

        //设置指定区域销售商品状态(第一次加载时缓存)
        /*if(Objects.nonNull(customerVO)){
            CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
            goodsInfos.forEach(goodsInfoVO -> {
                if (Objects.nonNull(deliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                }
            });
        }*/

        KsBeanUtil.copyProperties(goodsRecommendSettingVO, recommendSettingVO);
        recommendSettingVO.setGoodsInfos(goodsInfos);
        recommendSettingVO.setBrands(goodsInfoResponse.getBrands());
        recommendSettingVO.setCates(goodsInfoResponse.getCates());
    }


    public GoodsInfoViewPageResponse goodsInfoResponse(GoodsInfoViewPageRequest viewPageRequest) {
        viewPageRequest.setAddedFlag(AddedFlag.YES.toValue());
        viewPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        viewPageRequest.setAuditStatus(CheckStatus.CHECKED);
        GoodsInfoViewPageResponse goodsInfoResponse = goodsInfoQueryProvider.pageView(viewPageRequest).getContext();
        goodsInfoResponse.getGoodsInfoPage().getContent().forEach(goodsInfoVO -> {
            GoodsInfoVO skuInfoVO = this.goodsInfoQueryProvider.getById(GoodsInfoByIdRequest
                    .builder().goodsInfoId(goodsInfoVO.getGoodsInfoId()).build()).getContext();
            goodsInfoVO.setGoodsInfoImg(skuInfoVO.getGoodsInfoImg());
        });

        return goodsInfoResponse;
    }

    /**
     * 计算两点的距离
     *
     * @param sourceLatitude
     * @param sourceLongitude
     * @param targetLatitude
     * @param targetLongitude
     * @return
     */
    public static double getDistance(Double sourceLatitude, Double sourceLongitude, Double targetLatitude, Double targetLongitude) {
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((rad(sourceLatitude) - rad(targetLatitude)) / 2), 2) +
                Math.cos(rad(sourceLatitude)) * Math.cos(rad(targetLatitude)) * Math.pow(Math.sin((rad(sourceLongitude) - rad(targetLongitude)) / 2), 2)));
        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000) / 10000;
        return distance;
    }

    /**
     * distance
     *
     * @param distance
     * @return
     */
    private static double rad(double distance) {
        return distance * Math.PI / 180.0;
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
                        log.info("GoodsBaseController.list goodsInfoNest:{}",goodsInfoNest);
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

    public EsSearchResponse getEsGoodsInfoByParams(EsGoodsInfoQueryRequest queryRequest,Integer size) {
        //聚合品牌
        queryRequest.setPageNum(0);
        queryRequest.setPageSize(1);
        queryRequest.setQueryGoods(false);
        queryRequest.putAgg(AggregationBuilders.terms("_id").field("_id").size(size));
        EsSearchResponse response = elasticsearchTemplate.query(queryRequest.getSearchCriteria(),
                searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));

        if (org.apache.commons.collections4.CollectionUtils.isEmpty(response.getGoodsData()) || response.getGoodsData().size() < 1) {
            if(response.getData() == null || org.apache.commons.collections4.CollectionUtils.isEmpty(response.getData())){
                return response;
            }
            return response;
        }
        return response;
    }

    /**
     * 检测商品库存是否可预售
     * @param storeId
     * @param goodsInfoList
     */
    private void checkGoodsPresellStock(Long storeId, List<GoodsInfoVO> goodsInfoList){
        if (CollectionUtils.isEmpty(goodsInfoList)){
            return;
        }
        BaseResponse<Boolean> storeResponse = storeQueryProvider.checkStoreIsPresell(storeId);
        if (storeResponse==null || !storeResponse.getContext()){
            return ;
        }
        goodsInfoList.forEach(goodsInfoVO->{
            //判断商品是否可预售 @jkp
            boolean isPresell = false;
            if (goodsInfoVO.getStock()!=null && goodsInfoVO.getPresellStock()!=null){
                isPresell = this.checkGoodsInfoIsPresell(storeResponse.getContext(), goodsInfoVO.getStock().doubleValue(), goodsInfoVO.getPresellStock());
            }
            goodsInfoVO.setIsPresell(isPresell?1:0);
        });

    }

    /**
     * 判断商品是否可预售 @jkp
     * @param presellState
     * @param stock
     * @param presellStock
     */
    private boolean checkGoodsInfoIsPresell(boolean presellState, double stock, long presellStock){
        return presellState//商家预售权限状态
                && stock<=0//真实库存数量
                && presellStock>0;//预售虚拟库存
    }
}
