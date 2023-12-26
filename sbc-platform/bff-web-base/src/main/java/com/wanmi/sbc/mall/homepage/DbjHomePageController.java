package com.wanmi.sbc.mall.homepage;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.ares.view.replay.ReplayTradeBuyerGoodsNumResponse;
import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.response.QueryActiveActResp;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.configuration.MobileNacosConfig;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabListQueryRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelWithDefaultByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.MallOpenStatus;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandShelflifeResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.api.provider.activitygoodspicture.ActivityGoodsPictureProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsImagestype.GoodsImageStypeProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureGetRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsResponse;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsViewResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.mall.homepage.request.HomePageGoodsCatRequest;
import com.wanmi.sbc.mall.homepage.request.HomePageGoodsRecommendRequest;
import com.wanmi.sbc.mall.homepage.request.HomePageGoodsRequest;
import com.wanmi.sbc.mall.homepage.request.HomePageModeRequest;
import com.wanmi.sbc.mall.homepage.response.HomePageCatResponse;
import com.wanmi.sbc.mall.homepage.response.HomePageCatSimpleRes;
import com.wanmi.sbc.mall.homepage.response.HomePageMallCat;
import com.wanmi.sbc.mall.homepage.response.HomePageModeResponse;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/homepage")
@Api(tags = "大白鲸首页服务")
@Slf4j
public class DbjHomePageController {

    private final static Long DEFAULT_MALL_ID = -1L;
    private final static Integer DEFAULT_PAGE_NUM = 30;
    private final static String BUYER_GOODS_NUM_TRADE = "BUYER_GOODS_NUM_TRADE";
    private final static String PAGE_ADV_GOODS_IDS_CACHE = "PAGE_ADV_GOODS_IDS_CACHE_{marketId}_{mallId}_{catId}";
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    private MarketingPluginProvider marketingPluginProvider;
    @Autowired
    private MarketingQueryProvider marketingQueryProvider;
    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;
    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;
    @Autowired
    private ShopCartProvider shopCartProvider;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private OsUtil osUtil;
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;
    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;
    @Autowired
    private LiveGoodsQueryProvider liveGoodsQueryProvider;
    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;
    @Autowired
    private PileActivityProvider pileActivityProvider;
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private ActivityGoodsPictureProvider activityGoodsPictureProvider;
    @Autowired
    private GoodsImageStypeProvider goodsImageStypeProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;
    @Autowired
    private MobileNacosConfig mobileNacosConfig;
    @Autowired
    private AdActivityProvider adActivityProvider;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static String wrapPageNumKey(HomePageGoodsCatRequest request) {
        String sb = PAGE_ADV_GOODS_IDS_CACHE;
        sb = sb.replace("{marketId}", request.getMarketId().toString());
        sb = sb.replace("{mallId}", request.getMallId() == null ? "-1" : request.getMallId().toString());
        sb = sb.replace("{catId}", request.getCatId() == null ? "-1" : request.getCatId().toString());
        return sb;
    }

    @ApiOperation(value = "获取首页分类")
    @PostMapping(value = "/mode")
    public BaseResponse<HomePageModeResponse> homePageMode(@RequestBody HomePageModeRequest request) {
        // 获取所有开启的商城
        final Integer homePageMode = mobileNacosConfig.getHomePageMode();
        final HomePageModeResponse homePageModeResponse = new HomePageModeResponse();
        homePageModeResponse.setMode(homePageMode);
        return BaseResponse.success(homePageModeResponse);
    }

    @ApiOperation(value = "获取首页分类")
    @PostMapping(value = "/listCat")
    public BaseResponse<HomePageCatResponse> listHomePageCats(@RequestBody HomePageGoodsRequest request) {
        // 获取所有开启的商城
        final List<HomePageMallCat> cats = new ArrayList<>();
        final HomePageCatResponse homePageCatResponse = new HomePageCatResponse();
        homePageCatResponse.setCats(cats);
        final HomePageMallCat homePageMallCat = new HomePageMallCat();
        cats.add(homePageMallCat);
        homePageMallCat.setMallId(DEFAULT_MALL_ID);
        homePageMallCat.setMallName("推荐");
        final CompanyMallSupplierTabListQueryRequest mallQuery = new CompanyMallSupplierTabListQueryRequest();
        mallQuery.setOpenStatus(MallOpenStatus.OPEN.toValue());
        mallQuery.setDeleteFlag(DeleteFlag.NO);
        final BaseResponse<List<CompanyMallSupplierTabVO>> mallListRes = companyIntoPlatformQueryProvider.listSupplierTab(mallQuery);
        final List<CompanyMallSupplierTabVO> mallList = mallListRes.getContext();
        final List<Long> relCatIds = mallList.stream().map(CompanyMallSupplierTabVO::getRelCatId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        final GoodsCateListByConditionRequest goodsCatQuery = new GoodsCateListByConditionRequest();
        goodsCatQuery.setDelFlag(DeleteFlag.NO.toValue());
        goodsCatQuery.setCateParentIds(relCatIds);
        final BaseResponse<GoodsCateListByConditionResponse> goodsCateRes = goodsCateQueryProvider.listByCondition(goodsCatQuery);
        final List<GoodsCateVO> goodsCateList = goodsCateRes.getContext().getGoodsCateVOList();
        final Map<Long, List<GoodsCateVO>> parentCatsMap = goodsCateList.stream().collect(Collectors.groupingBy(GoodsCateVO::getCateParentId));
        mallList.forEach(m -> {
            final HomePageMallCat mallCat = new HomePageMallCat();
            cats.add(mallCat);
            mallCat.setMallId(m.getId());
            mallCat.setMallName(m.getTabName());
            final List<GoodsCateVO> goodsCats = parentCatsMap.get(m.getRelCatId());
            goodsCats.forEach(f -> {
                if (f.getSort() == null) {
                    f.setSort(0);
                }
            });
            goodsCats.sort(Comparator.comparing(GoodsCateVO::getSort));
            List<HomePageCatSimpleRes> newList = new ArrayList<>();
            goodsCats.forEach(f -> {
                final HomePageCatSimpleRes goodsCateVO = new HomePageCatSimpleRes();
                goodsCateVO.setCateId(f.getCateId());
                goodsCateVO.setCateName(f.getCateName());
                goodsCateVO.setCateImg(f.getCateImg());
                newList.add(goodsCateVO);
            });
            mallCat.setGoodsCats(newList);
        });
        return BaseResponse.success(homePageCatResponse);
    }

    @ApiOperation(value = "获取商城下商品")
    @PostMapping(value = "/goods/spu")
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> pageHomePageGoods(@RequestBody HomePageGoodsRequest request, HttpServletRequest httpRequest) {
        final HomePageGoodsCatRequest convert = KsBeanUtil.convert(request, HomePageGoodsCatRequest.class);
        return pageHomePageGoodsCat(convert, httpRequest);
    }

    @ApiOperation(value = "首页获取推荐商品")
    @PostMapping(value = "/goods/recommend")
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> pageHomePageGoodsRecommend(@RequestBody HomePageGoodsRecommendRequest request, HttpServletRequest httpRequest) {
        final HomePageGoodsCatRequest convert = KsBeanUtil.convert(request, HomePageGoodsCatRequest.class);
        return pageHomePageGoodsCat(convert, httpRequest);
    }

    @ApiOperation(value = "获取商城下类目商品")
    @PostMapping(value = "/goods/cat")
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> pageHomePageGoodsCat(@RequestBody HomePageGoodsCatRequest request, HttpServletRequest httpRequest) {
        String sb = wrapPageNumKey(request);
        int pageNumCli = request.getPageNum();
        request.setPageSize(DEFAULT_PAGE_NUM);
        int pageSize = request.getPageSize();
        CustomerVO customer = commonUtil.getCustomer();
        Long wareId = commonUtil.getWareId(httpRequest);
        // 默认排序
        final EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setPageSize(pageSize);
        queryRequest.setPageNum(request.getPageNum());
        queryRequest.setPageSize(request.getPageSize());
        queryRequest.setMatchWareHouseFlag(true);
        queryRequest.setCateId(request.getCatId());
        queryRequest.setMarketMallQuery(true);
        queryRequest.setMallId(request.getMallId());
        queryRequest.setMarketId(request.getMarketId());
        Map<String, Integer> advMap = wrapGoodsAdv(request.getMarketId(), request.getMallId(), request.getCatId());
        // 处理广告位逻辑
        if (advMap.size() > 0) {
            if (pageNumCli == 0) {
                List<String> queryGoodsIds = new ArrayList<>(advMap.keySet());
                // 去补充剩余商品
                final EsGoodsInfoQueryRequest addAdvEs = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);
                addAdvEs.setPageSize(pageSize - advMap.keySet().size());
                addAdvEs.setGoodsIdsNot(Lists.newArrayList(advMap.keySet()));
                List<String> goodsIdsAdd = addADVGoods(addAdvEs);
                if (CollectionUtils.isNotEmpty(goodsIdsAdd)) {
                    queryGoodsIds.addAll(goodsIdsAdd);
                }
                queryRequest.setGoodsIds(queryGoodsIds);
            } else {
                // 如果有广告商品，剔除广告商品，往前找一页
                final String s = stringRedisTemplate.opsForValue().get(sb);
                if (StringUtils.isNotBlank(s)) {
                    queryRequest.setGoodsIdsNot(JSON.parseArray(s, String.class));
                }
                queryRequest.setPageNum(queryRequest.getPageNum() - 1);
            }
        } else {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sb))) {
                stringRedisTemplate.delete(sb);
            }
        }


        // 市场和商城实时查找
//        queryRequest.setStoreIds(wrapMarketIdQuery(request.getMarketId(), request.getMallId()));
        queryRequest.setWareId(wareId);
        EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customer, httpRequest);
        EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
        //增加保质期
        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();
        //没有搜索出数据
        if (CollectionUtils.isEmpty(listEsGoods.getContent())) {
            return BaseResponse.success(realResponse);
        }

        List<GoodsVO> goodsList = esGoodsLimitBrandResponse.getGoodsList();
        Map<String, String> goodsVOMap = goodsList.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew, (key1, key2) -> key2));

        List<String> goodsInfoIds = Lists.newArrayList();
        List<String> goodIds = listEsGoods.stream().flatMap(v -> {
            return v.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsId);
        }).collect(Collectors.toList());

        //获取店铺信息
        final Map<Long, String> storeNameMap = mapStoreName(listEsGoods);

        List<GoodsImageStypeVO> context1 = goodsImageStypeProvider.getHcImageByGoodsIds(goodIds).getContext();
        Map<String, GoodsImageStypeVO> collect = context1.stream().collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
        Map<String, GoodsImageStypeVO> finalCollect = collect;


        for (EsGoods listEsGood : listEsGoods) {
            List<GoodsInfoNest> goodsInfos = listEsGood.getGoodsInfos();
            List<String> goodsInfoId = goodsInfos.stream().map(y -> y.getGoodsInfoId()).collect(Collectors.toList());
            goodsInfos.forEach(var -> {
                if (null == var.getGoodsSubtitleNew()) {
                    var.setGoodsSubtitleNew(goodsVOMap.get(var.getGoodsId()));
                }
                //填充活动图片
                if (CollectionUtils.isNotEmpty(var.getMarketingLabels())) {
                    GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(var.getGoodsId());
                    if (Objects.nonNull(goodsImageStypeVO)) {
                        var.setGoodsInfoImg(goodsImageStypeVO.getArtwork_url());
                    }
                }
                // 填充店铺名称
                if (StringUtils.isBlank(var.getStoreName())) {
                    var.setStoreName(storeNameMap.get(var.getStoreId()));
                }
            });
            goodsInfoIds.addAll(goodsInfoId);
        }
        final List<EsGoods> content = esGoodsLimitBrandResponse.getEsGoods().getContent();
        if (advMap.size() > 0) {
            if (CollectionUtils.isNotEmpty(content) && pageNumCli == 0) {
                Map<String, Integer> adJustMap = new HashMap<>();
                List<String> allGoodsIds = new ArrayList<>();
                content.forEach(f -> {
                    allGoodsIds.add(f.getId());
                    final Integer advSort = advMap.get(f.getId());
                    if (null != advSort) {
                        adJustMap.put(f.getId(), advSort);
                    }
                });
                final Page<EsGoods> esGoodsPage = esGoodsLimitBrandResponse.getEsGoods();
                final List<EsGoods> esGoodsSortNew = dealAdvSort(content, adJustMap);
                esGoodsLimitBrandResponse.setEsGoods(new PageImpl<>(esGoodsSortNew,
                        PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), esGoodsPage.getTotalElements()));
                // 广告位要排除的加入缓存
                stringRedisTemplate.opsForValue().set(sb, JSON.toJSONString(allGoodsIds));
            }
        }
        BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);
        // 处理广告指定位置
        return BaseResponse.success(realResponse);
    }

    private List<String> addADVGoods(EsGoodsInfoQueryRequest request) {
        final EsGoodsResponse esGoodsResponse = esGoodsInfoElasticService.pageByGoodsBySimple(request);
        if (esGoodsResponse.getEsGoods() != null && CollectionUtils.isNotEmpty(esGoodsResponse.getEsGoods().getContent())) {
            return esGoodsResponse.getEsGoods().getContent().stream().map(EsGoods::getId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<EsGoods> dealAdvSort(List<EsGoods> goodsSource, Map<String, Integer> adjustMap) {
        if (adjustMap.isEmpty()) return goodsSource;
        List<EsGoods> goods = Lists.newArrayList(goodsSource);
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(adjustMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());
        List<EsGoods> adjList = new ArrayList<>();

        Set<Integer> insetIndex = new HashSet<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String goodsId = entry.getKey();
            int targetIndex = entry.getValue();

            EsGoods esGoodsShelflife = null;
            for (EsGoods s : goods) {
                if (goodsId.equals(s.getId())) {
                    esGoodsShelflife = s;
                    break;
                }
            }

            if (esGoodsShelflife != null) {
                goods.remove(esGoodsShelflife);
                adjList.add(esGoodsShelflife);
                insetIndex.add(targetIndex);
            }
        }

        if (CollectionUtils.isEmpty(insetIndex)) return goodsSource;
        // 重新加回去
        insetIndex.forEach(index -> {
            if (adjList.size() < 1) return;
            Integer reduceIndex = index - 1;
            if (reduceIndex < 0) return;
            goods.add(reduceIndex, adjList.remove(0));
        });
        return goods;
    }

    public Map<String, Integer> wrapGoodsAdv(Long marketId, Long mallId, Long catId) {
        Map<String, Integer> goodsAdVMap = new HashMap<>();
        try {
            final ActQueryActiveActRequest actRequest = new ActQueryActiveActRequest();
            actRequest.setMarketId(marketId.intValue());
            Integer mallIdAdv = null == mallId ? 0 : mallId.intValue();
            Integer catIdAdv = null == catId ? 0 : catId.intValue();
            actRequest.setGoodsCateId(catIdAdv);
            actRequest.setMallTabId(mallIdAdv);
            actRequest.setSlotType(SlotType.MALL_GOOODS_LIST);
            final BaseResponse<QueryActiveActResp> queryActiveAct = adActivityProvider.queryMallGoodsActiveAct(actRequest);
            List<AdActivityDTO> activeActs;
            if (queryActiveAct.getContext() == null || CollectionUtils.isEmpty(activeActs = queryActiveAct.getContext().getActiveActs())) {
                return goodsAdVMap;
            }
            activeActs.forEach(o -> goodsAdVMap.put(o.getSpuId(), o.getSlotGroupSeq()));
        } catch (Exception e) {
            log.error("wrapAdvMap error", e);
        }
        return goodsAdVMap;
    }

    public Map<String, Integer> wrapCustomer(String customerId) {
        Map<String, Integer> map = new HashMap<>();
        if (StringUtils.isBlank(customerId) || customerId.equals("-1")) {
            return map;
        }
        final String hget = redisService.hget(BUYER_GOODS_NUM_TRADE, customerId);
        if (StringUtils.isNotBlank(hget)) {
            final List<ReplayTradeBuyerGoodsNumResponse> resRedis = JSON.parseArray(hget, ReplayTradeBuyerGoodsNumResponse.class);
            return resRedis.stream().collect(Collectors.toMap(ReplayTradeBuyerGoodsNumResponse::getGoodsId, ReplayTradeBuyerGoodsNumResponse::getNum, (o, n) -> o));
        }
        return map;
    }


    private List<Long> wrapMarketIdQuery(Long marketId, Long mallId) {
        List<Long> storeIds = new ArrayList<>();
        if (!Objects.equals(mallId, DEFAULT_MALL_ID)) {
            CompanyMallContractRelationPageRequest mallReq = new CompanyMallContractRelationPageRequest();
            mallReq.setRelationType(MallContractRelationType.TAB.getValue());
            mallReq.setPageSize(0);
            mallReq.setPageSize(2000);
            mallReq.setDeleteFlag(DeleteFlag.NO);
            mallReq.setRelationValue(mallId.toString());
            final BaseResponse<CompanyMallContractRelationPageResponse> mallRes = companyIntoPlatformQueryProvider.pageContractRelation(mallReq);
            storeIds = mallRes.getContext().getPage().getContent().stream().map(CompanyMallContractRelationVO::getStoreId).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(storeIds)) {
                return Lists.newArrayList(-3434L);
            }
        }
        CompanyMallContractRelationPageRequest marketReq = new CompanyMallContractRelationPageRequest();
        marketReq.setRelationType(MallContractRelationType.MARKET.getValue());
        marketReq.setPageSize(0);
        marketReq.setPageSize(2000);
        marketReq.setDeleteFlag(DeleteFlag.NO);
        marketReq.setStoreIds(storeIds);
        marketReq.setRelationValue(marketId.toString());
        final BaseResponse<CompanyMallContractRelationPageResponse> marketRes = companyIntoPlatformQueryProvider.pageContractRelation(marketReq);
        storeIds = marketRes.getContext().getPage().getContent().stream().map(CompanyMallContractRelationVO::getStoreId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIds)) {
            return storeIds;
        } else {
            return Lists.newArrayList(-3434L);
        }
    }

    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse list(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer, HttpServletRequest httpRequest) {
        queryRequest.setIsKeywords(false);
        queryRequest.setSortFlag(15);
        if (StringUtils.isNotBlank(queryRequest.getKeywords())) {
            String keywordsStr = esGoodsInfoElasticService.analyze(queryRequest.getKeywords());
            if (StringUtils.isNotEmpty(keywordsStr)) {
                List<String> biddingKeywords = commonUtil.getKeywordsFromCache(BiddingType.KEY_WORDS_TYPE);
                List<String> keyWords = Arrays.asList(keywordsStr.split(" "));
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(biddingKeywords) && biddingKeywords.stream().anyMatch(b -> keyWords.contains(b))) {
                    queryRequest.setIsKeywords(true);
                }
            }
        }
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
        if (Objects.nonNull(queryRequest.getMarketingId())) {
            MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
            marketingGetByIdRequest.setMarketingId(queryRequest.getMarketingId());
            queryRequest.setGoodsInfoIds(marketingQueryProvider.getByIdForCustomer(marketingGetByIdRequest).getContext().getMarketingForEndVO().getMarketingScopeList().stream().map(MarketingScopeVO::getScopeId).collect(Collectors.toList()));
        }
        //只看分享赚商品信息
        if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue() == queryRequest.getDistributionGoodsAudit()) {
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
            CustomerLevelWithDefaultByCustomerIdResponse response = customerLevelQueryProvider.getCustomerLevelWithDefaultByCustomerId(CustomerLevelWithDefaultByCustomerIdRequest.builder().customerId(customer.getCustomerId()).build()).getContext();
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
        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos).flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class)).collect(Collectors.toList());
            //根据开关重新设置分销商品标识
            distributionService.checkDistributionSwitch(goodsInfoList);
            //只看分享赚商品信息
            if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue() == queryRequest.getDistributionGoodsAudit()) {
                goodsInfoList = goodsInfoList.stream().filter(goodsInfoVO -> DistributionGoodsAudit.CHECKED.equals(goodsInfoVO.getDistributionGoodsAudit())).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsLimitBrandResponse();
            }
            List<String> skuIds = goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            //批量查询sku信息
            Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g, (x, y) -> x));
            for (GoodsInfoVO goodsInfo : goodsInfoList) {
                GoodsInfoVO goodsInfoVO = goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO());
                //填充商品保质期
                if (Objects.nonNull(goodsInfoVO.getShelflife()) && goodsInfoVO.getShelflife() == 9999) {
                    goodsInfo.setShelflife(0L);
                } else {
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
            GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceProvider.putByCustomerId(priceRequest).getContext();
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoList = priceResponse.getGoodsInfoVOList();
            //计算营销价格
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class));
            if (Objects.nonNull(customer)) {
                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            }
            GoodsInfoListByGoodsInfoResponse filterResponse = marketingPluginProvider.goodsListFilter(filterRequest).getContext();

            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                goodsInfoList = filterResponse.getGoodsInfoVOList();
            }
            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount(request).getContext();
                goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
            }
            //重新赋值于Page内部对象
            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy(GoodsInfoVO::getGoodsId));
            Boolean finalVipPriceFlag = vipPriceFlag;
            Set<Long> storeIds = new HashSet<>(10);
            for (EsGoods inner : response.getEsGoods()) {
                Set<Long> collect = inner.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId).collect(Collectors.toSet());
                storeIds.addAll(collect);
            }

            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);


            response.getEsGoods().getContent().forEach(esGoods -> {
                List<GoodsInfoVO> goodsInfoVOList = voMap.get(esGoods.getId());
                Map<String, String> esGoodsInfoSubtitleNewMap = new HashMap<>();
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfoNests)) {
                    goodsInfoNests.forEach(goodsInfoNest -> esGoodsInfoSubtitleNewMap.put(goodsInfoNest.getGoodsInfoId(), goodsInfoNest.getGoodsSubtitleNew()));
                }
                if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
                    goodsInfoVOList.forEach(goodsInfoVO -> {
                        if (esGoodsInfoSubtitleNewMap.get(goodsInfoVO.getGoodsInfoId()) != null) {
                            goodsInfoVO.setGoodsSubtitleNew(esGoodsInfoSubtitleNewMap.get(goodsInfoVO.getGoodsInfoId()));
                        }
                        goodsInfoVO.setGoodsFavorableCommentNum(esGoods.getGoodsFavorableCommentNum());
                        goodsInfoVO.setGoodsSalesNum(esGoods.getGoodsSalesNum());
                        goodsInfoVO.setGoodsCollectNum(esGoods.getGoodsCollectNum());
                        goodsInfoVO.setGoodsEvaluateNum(esGoods.getGoodsEvaluateNum());
                    });
                    List<GoodsInfoNest> resultGoodsInfos = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoNest.class);

                    List<String> goodsInfoIds = resultGoodsInfos.stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList());
                    Map<String, Long> collect2 = new HashMap<>();
                    //设置囤货商品活动是否过期
                    BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();
                    if (CollectionUtils.isNotEmpty(startPileActivity.getContext())) {
                        //多商家囤货获取参与囤货活动商品虚拟库存
                        List<PileActivityGoodsVO> context = pileActivityProvider.getStartPileActivityPileActivityGoods(PileActivityPileActivityGoodsRequest.builder().goodsInfoIds(goodsInfoIds).build()).getContext();

                        if (CollectionUtils.isNotEmpty(context)) {
                            collect2 = context.stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, PileActivityGoodsVO::getVirtualStock, (x, y) -> x));
                        }
                    }


                    Map<String, Long> finalCollect = collect2;
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        Optional<GoodsInfoNest> optionalGoodsInfoNest = goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {
                            List<GoodsWareStockVO> goodsWareStockVOS = optionalGoodsInfoNest.get().getGoodsWareStockVOS();
                            if (CollectionUtils.isNotEmpty(goodsWareStockVOS)) {
                                //重新填充商品状态
                                if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag()) && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
                                    goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                                    // 判断是否有T，如果是1，就设置为2
                                    if (goodsInfoNest.getGoodsInfoName().endsWith("T") || goodsInfoNest.getGoodsInfoName().endsWith("t")) {
                                        if (goodsInfoNest.getStock().compareTo(BigDecimal.ZERO) <= 0) {
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
                        if (finalVipPriceFlag && Objects.nonNull(goodsInfoNest.getVipPrice()) && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                            if (goodsInfoNest.getGoodsInfoType() == 1) {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
                            } else {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                            }
                        }
                        Optional<GoodsInfoNest> optionalGoodsInfoNest = goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {
                            goodsInfoNest.setEnterPriseAuditStatus(optionalGoodsInfoNest.get().getEnterPriseAuditStatus());
                        }
                        log.info("GoodsBaseController.list goodsInfoNest:{}", goodsInfoNest);
                        //如果商品参加了营销活动，vip价格设置为0 vipPrice不参与任何营销活动
                        if (CollectionUtils.isNotEmpty(goodsInfoNest.getMarketingLabels())) {
                            goodsInfoNest.setVipPrice(BigDecimal.ZERO);
                        }
                        //计算到手价
                        goodsInfoNest.getMarketingLabels().stream().forEach(marketingLabelVO -> {

                            if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber()) && marketingLabelVO.getSubType() == 1 && marketingLabelVO.getNumber() == 1) {//1：满数量减
                                goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice().subtract(marketingLabelVO.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));

                            } else if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber()) && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1) {//3:满数量折
                                goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice().multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10))).setScale(2, BigDecimal.ROUND_HALF_UP));

                            }
//                            addActivityRestricted(goodsInfoNest,marketingLabelVO,results);
                        });
                        //计算到手价
                        this.calNestTheirPrice(goodsInfoNest);
                        if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                            if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                            }
                        }
                    });
                    esGoods.setGoodsInfos(resultGoodsInfos);
                }
            });
        }

        if (CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            response.getGoodsList().forEach(item -> {
                response.getEsGoods().getContent().forEach(esGoods -> {
                    if (esGoods.getId().equals(item.getGoodsId())) {
                        item.setGoodsLabels(esGoods.getGoodsLabels());
                    }
                });
            });

            response.getGoodsList().stream().forEach(item -> {
                List<GoodsLabelVO> collect = item.getGoodsLabels().stream().sorted((a, b) -> a.getSort().compareTo(b.getSort())).filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible())).collect(Collectors.toList());
                item.setGoodsLabels(collect);
            });

            response.getEsGoods().getContent().stream().forEach(item -> {
                List<GoodsLabelVO> collect = item.getGoodsLabels().stream().sorted((a, b) -> a.getSort().compareTo(b.getSort())).filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible())).collect(Collectors.toList());
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

        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            // 根据商品id,查询直播商品的id
            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
            // 将数据转成map(商品sku,直播商品名称)
            if (CollectionUtils.isNotEmpty(liveGoodsVOList)) {
                liveMapLong = liveGoodsVOList.stream().filter(entity -> {
                    return entity.getGoodsId() != null;
                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId, LiveGoodsVO::getGoodsId, (x, y) -> x));
            }
        }

        //设置上商品参与囤货活动
        List<PileActivityVO> context1 = pileActivityProvider.getStartPileActivity().getContext();
        if (CollectionUtils.isNotEmpty(goodsInfoIds) && CollectionUtils.isNotEmpty(context1)) {
            PileActivityVO pileActivityVO = context1.get(0);
            BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods = pileActivityProvider.getStartPileActivityPileActivityGoods(PileActivityPileActivityGoodsRequest.builder().pileActivityId(pileActivityVO.getActivityId()).goodsInfoIds(goodsInfoIds).build());
            BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
            if (CollectionUtils.isNotEmpty(pileActivityReturn.getContext())) {

                Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g, (a, b) -> a));

                response.getEsGoods().getContent().forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(goodsInfo -> {
                        if (Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))) {
                            goodsInfo.setVirtualStock(BigDecimal.valueOf(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock()));
                            if (pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()) {
                                goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                            } else {
                                goodsInfo.setPileFlag(ForcePileFlag.PILE);
                            }
                        } else {
                            goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                        }
                    });
                });
            }
        }

        //活动图片处理
        Map<String, ActivityGoodsResponse> collect = new HashMap<>();
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(goodsInfoIds).build());
            List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
            if (CollectionUtils.isNotEmpty(activityGoodsResponse)) {
                collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsInfoId, g -> g, (a, b) -> a));
            }
        }

        Map<String, ActivityGoodsResponse> finalCollect = collect;
        //设置商品是否在直播
        response.getEsGoods().getContent().forEach(esGoods -> {
            esGoods.getGoodsInfos().forEach(goodsInfo -> {
//                LiveHaveGoodsVO liveHaveGoodsVO = liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
//                if (liveHaveGoodsVO.getIsHaveLive() == 1) {
//                    goodsInfo.setIsHaveLive(1);
//                    goodsInfo.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
//                    goodsInfo.setLiveId(liveHaveGoodsVO.getLiveId());
//                } else {
//                    goodsInfo.setIsHaveLive(0);
//                }
                goodsInfo.setIsHaveLive(0);
                //设置参与活动商品的活动图片
                if (CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())) {
                    ActivityGoodsResponse activityGoodsResponse = finalCollect.get(goodsInfo.getGoodsInfoId());
                    if (Objects.nonNull(activityGoodsResponse)) {
                        goodsInfo.setGoodsInfoImg(activityGoodsResponse.getImgPath());
                    }
                }
            });
        });
        sortMoreSkusSort(page.getEsGoods());
        return page;
    }

    private void sortMoreSkusSort(Page<EsGoods> esGoods) {
        if (null == esGoods || CollectionUtils.isEmpty(esGoods.getContent())) {
            return;
        }
        esGoods.getContent().forEach(goods -> {
            final List<GoodsInfoNest> goodsInfos = goods.getGoodsInfos();
            if (CollectionUtils.isEmpty(goodsInfos) || goodsInfos.size() < 2) {
                return;
            }
            goodsInfos.forEach(f -> {
                if (f.getHostSku() == null) {
                    f.setHostSku(0);
                }
            });
            goodsInfos.sort(Comparator.comparing(GoodsInfoNest::getHostSku).reversed());
        });
    }

    private Map<Long, String> mapStoreName(Page<EsGoods> listEsGoods) {
        try {
            List<Long> storeIds = listEsGoods.stream().flatMap(v -> v.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId)).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(storeIds)) return new HashMap<>();
            final StoreQueryRequest storeQueryRequest = new StoreQueryRequest();
            storeQueryRequest.setStoreIds(storeIds);
            List<StoreSimpleResponse> storeByIdsResponseList = storeQueryProvider.listSimple(storeQueryRequest).getContext();
            return storeByIdsResponseList.stream().collect(Collectors.toMap(StoreSimpleResponse::getStoreId, StoreSimpleResponse::getStoreName, (key1, key2) -> key2));
        } catch (Exception e) {
            log.error("GoodsBaseController mapStoreName error", e);
            return Maps.newHashMap();
        }
    }

    private void calNestTheirPrice(GoodsInfoNest goodsInfoVO) {
        if (CollectionUtils.isNotEmpty(goodsInfoVO.getMarketingLabels())) {
            goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
                if (!marketingLabelVO.getMarketingType().equals(4) && !marketingLabelVO.getMarketingType().equals(2) && !marketingLabelVO.getMarketingType().equals(7)) {

                    marketingLabelVO.getLevelLabelVOS().forEach(levelLabelVO -> {
                        if (marketingLabelVO.getMarketingType().equals(0)) { //满减
                            //单件优惠金额
                            BigDecimal discount = BigDecimal.ZERO;
                            //满减到手价需要购买满足的件数
                            BigDecimal theirPriceFullCount = BigDecimal.ZERO;
                            if (marketingLabelVO.getSubType().equals(0)) { //满金额
                                theirPriceFullCount = levelLabelVO.getAmount().divide(goodsInfoVO.getMarketPrice(), 5, BigDecimal.ROUND_HALF_UP).setScale(0, BigDecimal.ROUND_UP);
                                discount = levelLabelVO.getReduction().divide(theirPriceFullCount, 2, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                            } else if (marketingLabelVO.getSubType().equals(1)) { //满数量
                                theirPriceFullCount = BigDecimal.valueOf(levelLabelVO.getNumber());
                                discount = levelLabelVO.getReduction().divide(theirPriceFullCount, 2, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                            goodsInfoVO.setTheirPrice(goodsInfoVO.getMarketPrice().subtract(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else if (marketingLabelVO.getMarketingType().equals(1)) { //瞒折
                            //折扣前总金额
                            BigDecimal totalPrice = BigDecimal.ZERO;
                            //折扣后单价即到手价
                            BigDecimal discount = BigDecimal.ZERO;
                            //满折到手价需要购买满足的件数
                            BigDecimal theirPriceFullCountForDiscount = BigDecimal.ZERO;
                            if (marketingLabelVO.getSubType().equals(2)) { //满金额
                                //需要购买件数，才能满足条件
                                theirPriceFullCountForDiscount = levelLabelVO.getAmount().divide(goodsInfoVO.getMarketPrice(), 5, BigDecimal.ROUND_HALF_UP).setScale(0, BigDecimal.ROUND_UP);
                                totalPrice = goodsInfoVO.getMarketPrice().multiply(theirPriceFullCountForDiscount).setScale(5, BigDecimal.ROUND_HALF_UP);
                                //折扣后单价即到手价
                                discount = totalPrice.multiply(levelLabelVO.getDiscount().divide(new BigDecimal(10), 2, BigDecimal.ROUND_HALF_UP)).divide(theirPriceFullCountForDiscount, 2, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                            } else if (marketingLabelVO.getSubType().equals(3)) { //满数量
                                theirPriceFullCountForDiscount = BigDecimal.valueOf(levelLabelVO.getNumber());
                                totalPrice = goodsInfoVO.getMarketPrice().multiply(theirPriceFullCountForDiscount).setScale(2, BigDecimal.ROUND_HALF_UP);
                                discount = totalPrice.multiply(levelLabelVO.getDiscount().divide(new BigDecimal(10), 2, BigDecimal.ROUND_HALF_UP)).divide(theirPriceFullCountForDiscount, 2, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                            goodsInfoVO.setTheirPrice(discount);
                        }

                    });
                }
            });
        }
    }
}
