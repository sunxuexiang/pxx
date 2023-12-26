package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanmi.sbc.advertising.api.provider.AdActivityProvider;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.response.QueryActiveActResp;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.configuration.MobileConfig;
import com.wanmi.sbc.configuration.MobileNacosConfig;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBulkMarketPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierRecommendPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabPageRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierRecommendPageResponse;
import com.wanmi.sbc.customer.api.response.store.*;
import com.wanmi.sbc.customer.bean.dto.MallSupplierConstant;
import com.wanmi.sbc.customer.bean.enums.*;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.es.elastic.response.EsSearchResponse;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateQueryHasChildRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandListResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByIdsResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateVO;
import com.wanmi.sbc.goods.request.GoodsMallPlatformMarketRequest;
import com.wanmi.sbc.goods.request.GoodsMallPlatformSupplierTabRequest;
import com.wanmi.sbc.goods.request.IndexAllSearchRequest;
import com.wanmi.sbc.goods.response.*;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.response.stream.StoreLiveStreamResponse;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.response.market.MarketingEffectiveStoreResponse;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeListAllRequest;
import com.wanmi.sbc.order.api.response.trade.OrderRecommendCount;
import com.wanmi.sbc.order.api.response.trade.OrderRecommendSkuCount;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.rediscache.HomePageRedisCache;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingQueryProvider;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingGetByIdRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingResponse;
import com.wanmi.sbc.setting.bean.enums.AdvertisingConfigEnums;
import com.wanmi.sbc.setting.bean.enums.AdvertisingConfigType;
import com.wanmi.sbc.setting.bean.vo.AdvertisingConfigVO;
import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * 商家平台首页
 * gdq
 * 2023-06-16 08:50
 **/

@RestController
@Slf4j
@RequestMapping("/platform-goods")
@Api(tags = "GoodsMallPlatformHomeController", description = "S2B web公用-平台商品信息API")
public class GoodsMallPlatformHomeController {


    private static final String BUYER_MARKET_DEFAULT = "BUYER_MARKET_DEFAULT_CACHE";
    private static final String SEPARATOR = ",";
    private static final String p_city_code = "[{\"id\": 110000, \"name\": \"京\"}, {\"id\": 120000, \"name\": \"津\"}, {\"id\": 130000, \"name\": \"冀\"}, {\"id\": 140000, \"name\": \"晋\"}, {\"id\": 150000, \"name\": \"蒙\"}, {\"id\": 210000, \"name\": \"辽\"}, {\"id\": 220000, \"name\": \"吉\"}, {\"id\": 230000, \"name\": \"黑\"}, {\"id\": 310000, \"name\": \"沪\"}, {\"id\": 320000, \"name\": \"苏\"}, {\"id\": 330000, \"name\": \"浙\"}, {\"id\": 340000, \"name\": \"皖\"}, {\"id\": 350000, \"name\": \"闽\"}, {\"id\": 360000, \"name\": \"赣\"}, {\"id\": 370000, \"name\": \"鲁\"}, {\"id\": 410000, \"name\": \"豫\"}, {\"id\": 420000, \"name\": \"鄂\"}, {\"id\": 430000, \"name\": \"湘\"}, {\"id\": 440000, \"name\": \"粤\"}, {\"id\": 450000, \"name\": \"桂\"}, {\"id\": 460000, \"name\": \"琼\"}, {\"id\": 500000, \"name\": \"渝\"}, {\"id\": 510000, \"name\": \"川\"}, {\"id\": 520000, \"name\": \"贵\"}, {\"id\": 530000, \"name\": \"云\"}, {\"id\": 540000, \"name\": \"藏\"}, {\"id\": 610000, \"name\": \"陕\"}, {\"id\": 620000, \"name\": \"甘\"}, {\"id\": 630000, \"name\": \"青\"}, {\"id\": 640000, \"name\": \"宁\"}, {\"id\": 650000, \"name\": \"新\"}, {\"id\": 710000, \"name\": \"台\"}, {\"id\": 810000, \"name\": \"港\"}, {\"id\": 820000, \"name\": \"澳\"}]";
    public static final String HOME_PAGE_ON_SALE_GOODS_STORE_IDS = "Home:page:onSaleGoods:StoreIds";
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private AdvertisingQueryProvider advertisingQueryProvider;
    @Resource
    private MobileConfig mobileConfig;
    @Resource
    private MarketingQueryProvider marketingQueryProvider;
    @Resource
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;
    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;
    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ResultsMapper resultsMapper;

    @Autowired
    private MobileNacosConfig mobileNacosConfig;

    @Autowired
    private HomePageRedisCache homePageRedisCache;

    @Autowired
    private AdActivityProvider adActivityProvider;

    private static BigDecimal wrapFeeSort(String fee) {
        if (StringUtils.isBlank(fee)) return BigDecimal.ZERO;
        return new BigDecimal(fee.toString()).multiply(new BigDecimal("-1"));
    }

    @ApiOperation(value = "商家首页市场列表")
    @RequestMapping(value = "/market-list", method = RequestMethod.POST)
    public BaseResponse<List<GoodsMallPlatformMarketResponse>> listMarket(@RequestBody GoodsMallPlatformMarketRequest request) {
        List<GoodsMallPlatformMarketResponse> list = new ArrayList<>();
        // 查找所有的市场
        final List<CompanyMallBulkMarketVO> marketList = listAllMarket(request.getMarketName());
        final List<MallMarketSortVO> marketListSort = marketList.stream().map(o -> MallMarketSortVO.builder().id(o.getMarketId().toString()).name(o.getMarketName()).two(o.getSort()).build()).collect(toList());

        String userId = commonUtil.getOperator() == null ? "-1" : commonUtil.getOperator().getUserId();
        String defaultMarketId = redisService.hget(BUYER_MARKET_DEFAULT, userId);
        if (null == defaultMarketId) {
            defaultMarketId = mobileConfig.getHomeDefaultMarketId().toString();
        }
//        String buyerMarketCacheStr = redisService.hget(MallSupplierConstant.BUYER_MARKET_HOME_SORT, userId);
//        Map<String, String> buyerMarketCacheMap = StringUtils.isNotBlank(buyerMarketCacheStr) ? JSON.parseObject(buyerMarketCacheStr, HashMap.class) : Maps.newHashMap();
//        final String marketCacheStr = redisService.getString(MallSupplierConstant.MARKET_HOME_SORT);
//        Map<String, String> marketCacheMap = StringUtils.isNotBlank(marketCacheStr) ? JSON.parseObject(marketCacheStr, HashMap.class) : Maps.newHashMap();
        for (MallMarketSortVO c : marketListSort) {// 默认逻辑
            if (Objects.equals(defaultMarketId, c.getId())) {
                c.setOne(BigDecimal.ZERO);
            } else {
                c.setOne(BigDecimal.ONE);
            }
            // 个人喜好
//            c.setTwo(wrapFeeSort(buyerMarketCacheMap.get(c.getId())));
//            c.setTwo(BigDecimal.ZERO);
//            c.setThree(wrapFeeSort(marketCacheMap.get(c.getId())));
//            c.setThree(BigDecimal.ZERO);
            // 平台排序
            if (c.getTwo() == null) {
                c.setTwo(BigDecimal.ZERO);
            }
        }
        marketListSort.sort(Comparator.comparing(MallMarketSortVO::getOne).thenComparing(MallMarketSortVO::getTwo));
        marketListSort.forEach(o -> {
            final GoodsMallPlatformMarketResponse target = new GoodsMallPlatformMarketResponse();
            target.setMarketId(Long.valueOf(o.getId()));
            target.setMarketName(o.getName());
            list.add(target);
        });
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "商家首页市场列表")
    @RequestMapping(value = "/market-list/v2", method = RequestMethod.POST)
    public BaseResponse<List<GoodsMallPlatformMarketWrapResponse>> listMarketV2(@RequestBody GoodsMallPlatformMarketRequest request) {
        List<GoodsMallPlatformMarketWrapResponse> list = new ArrayList<>();
        // 查找所有的市场
        final List<CompanyMallBulkMarketVO> marketList = listAllMarket(request.getMarketName());
        marketList.forEach(o -> o.setSort(null == o.getSort() ? BigDecimal.ZERO : o.getSort()));
        final Map<Long, List<CompanyMallBulkMarketVO>> pIdMap = marketList.stream().collect(groupingBy(CompanyMallBulkMarketVO::getProvinceId));
        String userId = commonUtil.getOperator() == null ? "-1" : commonUtil.getOperator().getUserId();
        String defaultMarketId = redisService.hget(BUYER_MARKET_DEFAULT, userId) == null ? mobileConfig.getHomeDefaultMarketId().toString() : redisService.hget(BUYER_MARKET_DEFAULT, userId);
        Map<String, String> getPcityMap = getPCityName();
        final Long defaultPId = marketList.stream().filter(o -> Objects.equals(o.getMarketId().toString(), defaultMarketId)).map(CompanyMallBulkMarketVO::getProvinceId).findFirst().orElse(null);
        pIdMap.forEach((k, v) -> {
            final GoodsMallPlatformMarketWrapResponse target = new GoodsMallPlatformMarketWrapResponse();
            target.setProvinceId(k);
            target.setProvinceName(v.get(0).getProvinceName());
            target.setSort(Objects.equals(k, defaultPId) ? new BigDecimal("-1") : getMinSort(v));
            target.setProvinceShortName(getPcityMap.get(k.toString()) == null ? "未知" : getPcityMap.get(k.toString()));
            target.setMarkets(v.stream().map(o -> {
                final GoodsMallPlatformMarketResponse market = new GoodsMallPlatformMarketResponse();
                market.setMarketId(o.getMarketId());
                market.setMarketName(o.getMarketName());
                market.setIsDefault(Objects.equals(defaultMarketId, o.getMarketId().toString()));
                market.setSort(o.getSort() == null ? new BigDecimal("100") : o.getSort());
                return market;
            }).collect(toList()));
            list.add(target);
        });
        // 从新排序下
        list.sort(Comparator.comparing(GoodsMallPlatformMarketWrapResponse::getSort));
        list.forEach(o -> o.getMarkets().sort(Comparator.comparing(GoodsMallPlatformMarketResponse::getSort)));
        return BaseResponse.success(list);
    }

    private Map<String, String> getPCityName() {
        try {
            List<GoodsMallPlatformMarketWrapShortNameResponse> list = JSON.parseArray(p_city_code, GoodsMallPlatformMarketWrapShortNameResponse.class);
            return list.stream().collect(toMap(GoodsMallPlatformMarketWrapShortNameResponse::getId, GoodsMallPlatformMarketWrapShortNameResponse::getName));
        } catch (Exception e) {
            log.error("getPCityName error", e);
            return Maps.newHashMap();
        }
    }

    private BigDecimal getMinSort(List<CompanyMallBulkMarketVO> v) {
        return v.stream().map(CompanyMallBulkMarketVO::getSort).min(BigDecimal::compareTo).orElse(new BigDecimal("100"));
    }

    private List<CompanyMallBulkMarketVO> listAllMarket(String marketName) {
        // 查找所有签约的市场
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageNum(0);
        relationPageRequest.setPageSize(Integer.MAX_VALUE);
        relationPageRequest.setDeleteFlag(DeleteFlag.NO);
        relationPageRequest.setRelationType(MallContractRelationType.MARKET.getValue());
        final BaseResponse<CompanyMallContractRelationPageResponse> contractRelation = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
        if (CollectionUtils.isEmpty(contractRelation.getContext().getPage().getContent())) return Lists.newArrayList();

        // 根据签约市场查找市场信息
        final CompanyMallBulkMarketPageRequest pageRequest = new CompanyMallBulkMarketPageRequest();
        pageRequest.setPageNum(0);
        pageRequest.setPageSize(1000);
        pageRequest.setDeleteFlag(DeleteFlag.NO);
        pageRequest.setOpenStatus(MallOpenStatus.OPEN.toValue());
        pageRequest.setConcatInfo(marketName);
        pageRequest.setMarketIds(contractRelation.getContext().getPage().getContent().stream().map(o -> Long.valueOf(o.getRelationValue())).distinct().collect(toList()));
        final BaseResponse<CompanyMallBulkMarketPageResponse> marketResponse = companyIntoPlatformQueryProvider.pageMarket(pageRequest);
        return marketResponse.getContext().getPage().getContent();
    }

    @ApiOperation(value = "商家首页商家列表")
    @RequestMapping(value = "/supplier-tab-list", method = RequestMethod.POST)
    public BaseResponse<GoodsMallPlatformSupplierTabResponse> listCompanySupplierTab(@RequestBody GoodsMallPlatformSupplierTabRequest request) {
        final Long marketId = request.getMarketId();
        List<GoodsMallPlatformSupplierTabVO> mallTabs = new ArrayList<>();
        final GoodsMallPlatformSupplierTabResponse response = new GoodsMallPlatformSupplierTabResponse();
        response.setMallTabs(mallTabs);
        String userId = commonUtil.getOperator().getUserId();
        if (userId == null) userId = "-1";
        // 获取所有店铺
//        final Map<Long, StoreVO> storeVOMap = getStoreMap();
        // 查找市场对应的签约商家
        List<CompanyMallContractRelationVO> contractSuppliers = listMarketByMarketId(marketId);
        // 市场下的商家Ids(已处理)s
        Set<Long> contractStoreIds = contractSuppliers.stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toSet());
        final Map<Long, StoreSimpleResponse> storeVOMap = getStoreMap(contractStoreIds);
        // 市场下的商家Ids(已处理)
        contractStoreIds = storeVOMap.keySet();
        Set<Long> storeIds = dealQuerySearch(request, contractStoreIds);

        if (CollectionUtils.isEmpty(storeIds)) return BaseResponse.success(response);
        // 获取店铺商家的商城信息
        List<CompanyMallContractRelationVO> supplierTabs = listSupplierTabByMarketStoreIds(storeIds);
        if (CollectionUtils.isEmpty(supplierTabs)) return BaseResponse.success(response);
        final Map<String, List<CompanyMallContractRelationVO>> tag2SuppliersMap = supplierTabs.stream().collect(groupingBy(CompanyMallContractRelationVO::getRelationValue));
        Set<Long> tabStoreIds = supplierTabs.stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toSet());

        // 查找推荐商家
        List<CompanyMallSupplierRecommendVO> recommendSuppliers = listRecommendSupplier();
        final Map<Long, CompanyMallSupplierRecommendVO> recommendSupplierMap = recommendSuppliers.stream().collect(Collectors.toMap(CompanyMallSupplierRecommendVO::getStoreId, Function.identity(), (o, n) -> o));
        Set<Long> recommendStoreIds = listRecommendContractSuppliersStoreIds(supplierTabs, recommendSuppliers);

        //获取有商家商品的商家
        Set<Long> onSaleGoodsStoreIds = getOnSaleGoodsStoreIds();

        // 店铺个人喜好
        Map<String, String> buyerStoreCacheMap = getBuyerStoreCacheMap(userId);

        // 店铺平台喜欢
        Map<String, String> marketStoreCacheMap = mapStoreCache();
//        Map<String, String> marketStoreCacheMap = new HashMap<>();
//        String marketStoreCacheStr = redisService.hget(MallSupplierConstant.MARKET_STORE_HOME_SORT, marketId.toString());
//        Map<String, String> marketStoreCacheMap = StringUtils.isNotBlank(marketStoreCacheStr) ?
//                JSON.parseObject(marketStoreCacheStr, HashMap.class) : Maps.newHashMap();

        // TAB个人喜好
        Map<String, String> buyerTabCacheMap = getTabCacheMap(userId);

        // TAB平台喜欢
        Map<String, String> marketTabCacheMap = new HashMap<>();
//        String marketTabCacheStr = redisService.hget(MallSupplierConstant.MARKET_TAB_HOME_SORT, marketId.toString());
//        Map<String, String> marketTabCacheMap = StringUtils.isNotBlank(marketTabCacheStr) ?
//                JSON.parseObject(marketTabCacheStr, HashMap.class) : Maps.newHashMap();

        // 获取所有商城
        final Map<Long, CompanyMallSupplierTabVO> tabVOMap = getTabVOMap();

        // 获取直播中的商家
//        final List<Long> liveStoreIds = getLiveStoreIds();
        Map<Long,List<Integer>> liveStoreInfo = getLiveStoreInfo();

        // 处理storeIds的排序
        List<MallMarketSortVO> supplierSortTabs = sortSuppliers(tabStoreIds, recommendSupplierMap, buyerStoreCacheMap, marketStoreCacheMap, onSaleGoodsStoreIds);
        // 处理首页广告
        Map<Long,Integer> tabAdvSortMap = new HashMap<>();
        Map<Long,Integer> recommendAdvSortMap = new HashMap<>();
        wrapAdvMap(tabAdvSortMap, recommendAdvSortMap, marketId);
        if (StringUtils.isBlank(request.getKeyword()) && request.getAdvertisingConfigId() == null) {
            // 推荐商家
            fillTabRecommend(supplierSortTabs, recommendStoreIds, mallTabs, storeVOMap, liveStoreInfo, recommendSupplierMap, recommendAdvSortMap);
        }
        // 封装正常tab
        List<String> sortTabs = sortTab(tag2SuppliersMap.keySet(), buyerTabCacheMap, marketTabCacheMap, tabVOMap);
        fillTabOverSupplier(sortTabs, supplierSortTabs, tag2SuppliersMap, mallTabs, storeVOMap, tabVOMap, liveStoreInfo, tabAdvSortMap);
        // 平铺给商家列表
        fillAllSuppliers(response, tabStoreIds, storeVOMap, supplierSortTabs);
        // 记录cache
        redisService.hset(BUYER_MARKET_DEFAULT, userId, marketId.toString());
        return BaseResponse.success(response);
    }

    private void wrapAdvMap(Map<Long, Integer> tabAdvSortMap, Map<Long, Integer> recommendAdvSortMap, Long marketId) {
        try {
            final ActQueryActiveActRequest actRequest = new ActQueryActiveActRequest();
            actRequest.setMarketId(marketId.intValue());
            actRequest.setSlotType(SlotType.MALL_STORE_LIST);
            final BaseResponse<QueryActiveActResp> queryActiveAct = adActivityProvider.queryActiveAct(actRequest);
            final List<AdActivityDTO> activeActs = queryActiveAct.getContext().getActiveActs();
            activeActs.forEach(o -> {
                if (Objects.equals(o.getMallTabId(),0)){
                    recommendAdvSortMap.put(o.getStoreId(),o.getSlotGroupSeq());
                }else {
                    tabAdvSortMap.put(o.getStoreId(),o.getSlotGroupSeq());
                }
            });
        } catch (Exception e) {
            log.error("wrapAdvMap error", e);
        }
    }

    private Set<Long> getOnSaleGoodsStoreIds() {
        Set<Long> result = new HashSet<>();
        if (redisService.hasKey(HOME_PAGE_ON_SALE_GOODS_STORE_IDS)) {
            final Object o = redisService.getString(HOME_PAGE_ON_SALE_GOODS_STORE_IDS);
            if (null != o) {
                result = Sets.newHashSet(JSON.parseArray(o.toString(), Long.class));
            }
        } else {
            result = getOnSaleGoodsStoreIdsFormEs();
            redisService.setString(HOME_PAGE_ON_SALE_GOODS_STORE_IDS, JSON.toJSONString(result),  60 * 5);
        }
        return result;
    }

    private Set<Long> getOnSaleGoodsStoreIdsFormEs() {
        Set<Long> result = new HashSet<>();
        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        // 商品搜索
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        esGoodsInfoQueryRequest.setContractStartDate(now);
        esGoodsInfoQueryRequest.setContractEndDate(now);
        esGoodsInfoQueryRequest.setAuditStatus(1);
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setPageNum(0);
        esGoodsInfoQueryRequest.setPageSize(1);
        esGoodsInfoQueryRequest.setWareId(1L);
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        //聚合品牌
        esGoodsInfoQueryRequest.putAgg(AggregationBuilders.terms("storeId").field("goodsInfos.storeId").size(2000));
        if (Objects.nonNull(esGoodsInfoQueryRequest.getSortFlag()) && esGoodsInfoQueryRequest.getSortFlag() == 11) {
            esGoodsInfoQueryRequest.getSorts().removeAll(esGoodsInfoQueryRequest.getSorts());
            esGoodsInfoQueryRequest.putSort("goodsSeqNum", SortOrder.ASC);
        }
        final SearchQuery searchCriteria = esGoodsInfoQueryRequest.getSearchCriteria();
        log.info("首页商家分组查找店铺参数：" + JSON.toJSONString(searchCriteria));
        EsSearchResponse response = elasticsearchTemplate.query(searchCriteria, searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));

        if (CollectionUtils.isNotEmpty(response.getGoodsData())) {
            response.getAggResultMap().forEach((k, v) -> {
                if (!"storeId".equals(k)) {
                    return;
                }
                v.forEach(u -> result.add(Long.valueOf(u.getKey().toString())));
            });
        }
        return result;
    }

    private Map<String, String> mapStoreCache() {
        try {
            final String storeCacheStr = redisService.getString(MallSupplierConstant.STORE_HOME_SORT);
            Map<String, String> storeCacheMap = StringUtils.isNotBlank(storeCacheStr) ? JSON.parseObject(storeCacheStr, HashMap.class) : Maps.newHashMap();
            return storeCacheMap;
        } catch (SbcRuntimeException e) {
            log.error("mapStoreCache error", e);
            return Maps.newHashMap();
        }
    }

    private Map<Long, List<Integer>> getLiveStoreInfo() {
        Map<Long, List<Integer>> map = new HashMap<>();
        try {
            BaseResponse<Map<Long, List<StoreLiveStreamResponse>>> baseResponse = liveStreamQueryProvider.getLiveStoreInfo();
            final Map<Long, List<StoreLiveStreamResponse>> context = baseResponse.getContext();
            if (MapUtils.isNotEmpty(context)) {
                context.forEach((k, v) ->
                        map.put(k, v.stream().map(StoreLiveStreamResponse::getLiveId).collect(Collectors.toList()))
                );
            }
        } catch (Exception e) {
            log.error("获取直播中的商家详情失败", e);
        }
        return map;
    }

    private Map<String, String> getTabCacheMap(String userId) {
        try {
            String buyerTabCacheStr = redisService.hget(MallSupplierConstant.BUYER_TAB_HOME_SORT, userId);
            Map<String, String> buyerTabCacheMap = StringUtils.isNotBlank(buyerTabCacheStr) ? JSON.parseObject(buyerTabCacheStr, HashMap.class) : Maps.newHashMap();
            return buyerTabCacheMap;
        } catch (Exception e) {
            log.error("获取用户TAb喜好失败,p{}:", userId, e);
            return new HashMap<>();
        }
    }

    private Map<String, String> getBuyerStoreCacheMap(String userId) {
        try {
            String buyerStoreCacheStr = redisService.hget(MallSupplierConstant.BUYER_STORE_HOME_SORT, userId);
            Map<String, String> buyerStoreCacheMap = StringUtils.isNotBlank(buyerStoreCacheStr) ? JSON.parseObject(buyerStoreCacheStr, HashMap.class) : Maps.newHashMap();
            return buyerStoreCacheMap;
        } catch (Exception e) {
            log.error("获取用户店铺喜好失败,p:", userId, e);
            return new HashMap<>();
        }
    }

    /**
     * 智能检索
     * 第一级（商家名，品牌名，类目名，返回商家）
     * 第二级（es 商品名称）
     */
    @ApiOperation(value = "首页全局检索")
    @RequestMapping(value = "/index-all-search", method = RequestMethod.POST)
    public BaseResponse<IndexAllSearchResponse> indexAllSearch(@RequestBody IndexAllSearchRequest request) {
        final Long marketId = request.getMarketId();
        List<GoodsMallPlatformSupplierTabVO> mallTabs = new ArrayList<>();
        final IndexAllSearchResponse response = new IndexAllSearchResponse();
        response.setMallTabs(mallTabs);
        String userId = request.getCustomerId();
        if (userId == null) userId = "-1";
        // 查找市场对应的签约商家
        List<CompanyMallContractRelationVO> contractSuppliers = listMarketByMarketId(marketId);
        if (CollectionUtils.isEmpty(contractSuppliers)){
            return BaseResponse.success(response);
        }
        // 市场下的商家Ids(已处理)s
        Set<Long> contractStoreIds = contractSuppliers.stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toSet());
        final Map<Long, StoreSimpleResponse> storeVOMap = getStoreMap(contractStoreIds);
        contractStoreIds = storeVOMap.keySet();

        // 搜索去掉期待中的商家
        contractStoreIds = contractStoreIds.stream().filter(o -> !Objects.equals(o, mobileConfig.getWaitingForStoreId())).collect(Collectors.toSet());
        Set<Long> storeIds = storeDealKeyWordsSuppliers(request.getKeyword(), contractStoreIds);
        if (CollectionUtils.isNotEmpty(storeIds)) {
            // 获取店铺商家的商城信息
            List<CompanyMallContractRelationVO> supplierTabs = listSupplierTabByMarketStoreIds(storeIds);
            if (CollectionUtils.isNotEmpty(supplierTabs)) {
                final Map<String, List<CompanyMallContractRelationVO>> tag2SuppliersMap = supplierTabs.stream().collect(groupingBy(CompanyMallContractRelationVO::getRelationValue));
                Set<Long> tabStoreIds = supplierTabs.stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toSet());

                // 查找推荐商家
                List<CompanyMallSupplierRecommendVO> recommendSuppliers = listRecommendSupplier();
                final Map<Long, CompanyMallSupplierRecommendVO> recommendSupplierMap = recommendSuppliers.stream().collect(Collectors.toMap(CompanyMallSupplierRecommendVO::getStoreId, Function.identity(), (o, n) -> o));
                Set<Long> recommendStoreIds = listRecommendContractSuppliersStoreIds(supplierTabs, recommendSuppliers);

                // 店铺个人喜好
                Map<String, String> buyerStoreCacheMap = getBuyerStoreCacheMap(userId);

                Map<String, String> marketStoreCacheMap = new HashMap<>();

                // TAB个人喜好
                Map<String, String> buyerTabCacheMap = getTabCacheMap(userId);

                Map<String, String> marketTabCacheMap = new HashMap<>();

                Map<Long,List<Integer>> liveStoreInfo = getLiveStoreInfo();

                // 获取所有商城
                final Map<Long, CompanyMallSupplierTabVO> tabVOMap = getTabVOMap();

                //获取有商家商品的商家
                Set<Long> onSaleGoodsStoreIds = getOnSaleGoodsStoreIds();

                // 处理storeIds的排序
                List<MallMarketSortVO> supplierSortTabs = sortSuppliers(tabStoreIds, recommendSupplierMap, buyerStoreCacheMap, marketStoreCacheMap, onSaleGoodsStoreIds);
                if (StringUtils.isBlank(request.getKeyword())) {
                    // 推荐商家
                    fillTabRecommend(supplierSortTabs, recommendStoreIds, mallTabs, storeVOMap, liveStoreInfo, recommendSupplierMap, null);
                }
                // 封装正常tab
                List<String> sortTabs = sortTab(tag2SuppliersMap.keySet(), buyerTabCacheMap, marketTabCacheMap, tabVOMap);
                fillTabOverSupplier(sortTabs, supplierSortTabs, tag2SuppliersMap, mallTabs, storeVOMap, tabVOMap, liveStoreInfo, null);
                // 平铺给商家列表
                storeFillAllSuppliers(response, tabStoreIds, storeVOMap, supplierSortTabs, liveStoreInfo);
                response.setSearchType(1);
                return BaseResponse.success(response);
            }
        }
        return BaseResponse.success(response);
    }

    private Map<Long, StoreSimpleResponse> getStoreMap(Set<Long> contractSuppliers) {
        final StoreQueryRequest storeQueryRequest = new StoreQueryRequest();
        storeQueryRequest.setStoreIds(Lists.newArrayList(contractSuppliers));
        storeQueryRequest.setAuditState(CheckState.CHECKED);
        storeQueryRequest.setStoreState(StoreState.OPENING);
        final BaseResponse<List<StoreSimpleResponse>> listBaseResponse = storeQueryProvider.listSimple(storeQueryRequest);
        return listBaseResponse.getContext().stream().collect(Collectors.toMap(StoreSimpleResponse::getStoreId,Function.identity(),(o,n) -> o));
    }

    @ApiOperation("首页全局检索商品")
    @RequestMapping(value = "/indexAllSearchForGoods", method = RequestMethod.POST)
    public BaseResponse<IndexAllSearchResponse> indexAllSearchForGoods(@RequestBody IndexAllSearchRequest request) {
        final IndexAllSearchResponse response = new IndexAllSearchResponse();
        EsGoodsResponse esGoodsResponse = searchGoods(request.getKeyword());
        if (CollectionUtils.isNotEmpty(esGoodsResponse.getEsGoods().getContent())) {
            Page<EsGoods> esGoods = esGoodsResponse.getEsGoods();
            //重建排序规则
            Page<EsGoods> esGoodsSort = sort(esGoods, request.getCustomerId());
            esGoodsSort.stream().flatMap(goods -> goods.getGoodsInfos().stream()).forEach(v -> {
                if (Objects.isNull(v.getStoreName())) {
                    //查询数据库
                    BaseResponse<StoreInfoResponse> storeBase = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(v.getStoreId()).build());
                    if (Objects.nonNull(storeBase.getContext())) {
                        v.setStoreName(storeBase.getContext().getStoreName());
                    }
                }

            });

            response.setEsGoods(esGoodsSort);
            response.setSearchType(0);
        }
        return BaseResponse.success(response);
    }

    public Page<EsGoods> sort(Page<EsGoods> goods, String customerId) {

        try {
            List<EsGoods> list = goods.getContent();
            if (CollectionUtils.isNotEmpty(list)) {
                BaseResponse<List<OrderRecommendSkuCount>> coun = null;
                List<OrderRecommendSkuCount> counts1 = null;
                Map<String, Integer> map1 = null;
                if (Objects.nonNull(customerId)) {
                    TradeQueryDTO tradeQueryDTO = new TradeQueryDTO();
                    tradeQueryDTO.setBuyerId(customerId);
                    //查询MongoDB
                    coun = tradeQueryProvider.sortByCustomerId(TradeListAllRequest.builder().tradeQueryDTO(tradeQueryDTO).build());
                    counts1 = coun.getContext();
                    map1 = counts1.stream().collect(Collectors.toMap(OrderRecommendSkuCount::getSkuIdOne, OrderRecommendSkuCount::getCount, (key1, key2) -> key2));

                }
                BaseResponse<List<OrderRecommendCount>> counSku = tradeQueryProvider.sortByUserIdAndSku();
                List<OrderRecommendCount> counts2 = counSku.getContext();
                Map<String, Integer> map2 = counts2.stream().collect(Collectors.toMap(OrderRecommendCount::getSkuId, OrderRecommendCount::getCount, (key1, key2) -> key2));
                fillSort(list, map1, map2);
            }
            // 重新排序
            List<EsGoods> sortedList = list.stream().sorted(comparing(EsGoods::getXnSort).reversed()).collect(Collectors.toList());

            return new PageImpl<>(sortedList);
        } catch (Exception e) {
            // 排序报错就直接跳过排序
            log.error("homePageItemSortError", e);
            return goods;
        }
    }

    private void fillSort(List<EsGoods> list, Map<String, Integer> map1, Map<String, Integer> map2) {

        // 使用Optional处理map1为空的情况
        Optional.ofNullable(map1).ifPresent(m1 -> {

            list.forEach(goods -> {
                Integer count = m1.get(goods.getId());
                if (count != null) {
                    goods.setXnSort(count + 999);
                }
            });

        });

        // 如果map1为空,直接使用map2
        Optional.ofNullable(map2).ifPresent(m2 -> {

            list.forEach(goods -> {
                Integer count = m2.get(goods.getId());
                goods.setXnSort(count != null ? 1 : 0);
            });

        });

    }

    private void fillTabOverSupplier(List<String> sortTabs, List<MallMarketSortVO> supplierSortTabs, Map<String, List<CompanyMallContractRelationVO>> tag2SuppliersMap, List<GoodsMallPlatformSupplierTabVO> mallTabs, Map<Long, StoreSimpleResponse> storeVOMap, Map<Long, CompanyMallSupplierTabVO> tabVOMap, Map<Long, List<Integer>> liveStoreIds, Map<Long, Integer> tabAdvSortMap) {
        sortTabs.forEach(tabIdStr -> {
            final GoodsMallPlatformSupplierTabVO mallTab = new GoodsMallPlatformSupplierTabVO();
            final Long tabId = Long.valueOf(tabIdStr);
            final List<CompanyMallContractRelationVO> v = tag2SuppliersMap.get(tabIdStr);
            final CompanyMallSupplierTabVO companyMallSupplierTabVO = tabVOMap.get(tabId);
            // 过滤掉关闭的tab
            if (null == companyMallSupplierTabVO || Objects.equals(companyMallSupplierTabVO.getOpenStatus(), MallOpenStatus.CLOSE.toValue()))
                return;
            mallTab.setTabName(companyMallSupplierTabVO.getTabName());
            mallTabs.add(mallTab);
            mallTab.setTabId(tabId);

            // 获取分类下面的商家
            final Set<Long> nowTabStoreIds = v.stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toSet());
            List<GoodsMallPlatformSupplierVO> targetSupplierVOS = new ArrayList<>();
            mallTab.setCompanySuppliers(targetSupplierVOS);
            // 从排序里面选择
            List<MallMarketSortVO> reSetSort = tabStoresResort(supplierSortTabs, v);
            reSetSort.forEach(sortVO -> wrapCompanySupplier(targetSupplierVOS, sortVO, nowTabStoreIds, storeVOMap));
            dealAssignSortT(targetSupplierVOS, storeVOMap, tabAdvSortMap);
            wrapLivePop(liveStoreIds, targetSupplierVOS, mallTab.getTabId());
        });
    }

    private List<MallMarketSortVO> tabStoresResort(List<MallMarketSortVO> supplierSortTabs, List<CompanyMallContractRelationVO> v) {
        List<MallMarketSortVO> list = KsBeanUtil.convertList(supplierSortTabs, MallMarketSortVO.class);
        final Map<String, BigDecimal> sotreSortMap = v.stream().filter(o -> o.getSort() != null).collect(toMap(f -> f.getStoreId().toString(), u -> new BigDecimal(u.getSort().toString())));
        // 第四维重新排序
        list.forEach(o -> o.setFour(sotreSortMap.get(o.getId()) == null ? new BigDecimal("100") : sotreSortMap.get(o.getId())));
        list.sort(Comparator.comparing(MallMarketSortVO::getOne).thenComparing(MallMarketSortVO::getTwo).thenComparing(MallMarketSortVO::getThree).thenComparing(MallMarketSortVO::getFour));
        return list;
    }

    private List<String> sortTab(Set<String> tabSources, Map<String, String> buyerTabCacheMap, Map<String, String> marketTabCacheMap, Map<Long, CompanyMallSupplierTabVO> tabVOMap) {
        List<String> sortTabIds = new ArrayList<>();
        List<MallMarketSortVO> sortedList = new ArrayList<>();
        tabSources.forEach(tabIdStr -> {
            final MallMarketSortVO sortVO = new MallMarketSortVO();
            sortedList.add(sortVO);
            sortVO.setId(tabIdStr);
            // 个人喜好
            sortVO.setOne(wrapFeeSort(buyerTabCacheMap.get(tabIdStr)));
            // 平台喜欢
//            sortVO.setTwo(wrapFeeSort(marketTabCacheMap.get(tabIdStr)));
            sortVO.setTwo(BigDecimal.ZERO);
            // 默认排序
            final CompanyMallSupplierTabVO tabVO = tabVOMap.get(Long.valueOf(tabIdStr));
            if (null == tabVO || tabVO.getSort() == null) {
                sortVO.setThree(BigDecimal.ZERO);
            } else {
                sortVO.setThree(tabVO.getSort());
            }
        });
        // 倒序
        sortedList.sort(Comparator.comparing(MallMarketSortVO::getOne).thenComparing(MallMarketSortVO::getTwo).thenComparing(MallMarketSortVO::getThree));
        sortedList.forEach(sortVO -> sortTabIds.add(sortVO.getId()));
        return sortTabIds;
    }

    private void fillAllSuppliers(GoodsMallPlatformSupplierTabResponse response, Set<Long> tabStoreIds, Map<Long, StoreSimpleResponse> storeVOMap, List<MallMarketSortVO> supplierSortTabs) {
        List<GoodsMallPlatformSupplierVO> suppliers = new ArrayList<>();
        supplierSortTabs.forEach(sortVO -> wrapCompanySupplier(suppliers, sortVO, tabStoreIds, storeVOMap));
        response.setSuppliers(suppliers);
    }

    private void storeFillAllSuppliers(IndexAllSearchResponse response, Set<Long> tabStoreIds, Map<Long, StoreSimpleResponse> storeVOMap, List<MallMarketSortVO> supplierSortTabs, Map<Long, List<Integer>> liveStoreInfo) {
        List<GoodsMallPlatformSupplierVO> suppliers = new ArrayList<>();
        supplierSortTabs.forEach(sortVO -> wrapCompanySupplier(suppliers, sortVO, tabStoreIds, storeVOMap));
        wrapLivePop(liveStoreInfo, suppliers, null);
        response.setSuppliers(suppliers);
    }

    private Map<Long, CompanyMallSupplierTabVO> getTabVOMap() {
        final CompanyMallSupplierTabPageRequest tabPageRequest = new CompanyMallSupplierTabPageRequest();
        tabPageRequest.setPageNum(0);
        tabPageRequest.setPageSize(200);
        tabPageRequest.setDeleteFlag(DeleteFlag.NO);
        final Map<Long, CompanyMallSupplierTabVO> tabVOMap = companyIntoPlatformQueryProvider.pageSupplierTab(tabPageRequest).getContext().getPage().getContent().stream().collect(toMap(CompanyMallSupplierTabVO::getId, Function.identity()));
        return tabVOMap;
    }

    private Set<Long> listRecommendContractSuppliersStoreIds(List<CompanyMallContractRelationVO> supplierTabs, List<CompanyMallSupplierRecommendVO> recommendSuppliers) {
        Set<Long> recommendContractStoreIds = new HashSet<>();
        final Set<Long> recommendCompanyIds = recommendSuppliers.stream().map(CompanyMallSupplierRecommendVO::getCompanyInfoId).collect(Collectors.toSet());
        supplierTabs.stream().collect(Collectors.groupingBy(CompanyMallContractRelationVO::getCompanyInfoId)).forEach((k, v) -> {
            if (recommendCompanyIds.contains(k)) {
                recommendContractStoreIds.add(v.get(0).getStoreId());
            }
        });
        return recommendContractStoreIds;
    }

    private List<CompanyMallContractRelationVO> listSupplierTabByMarketStoreIds(Set<Long> storeIds) {
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageNum(0);
        relationPageRequest.setPageSize(2000);
        relationPageRequest.setDeleteFlag(DeleteFlag.NO);
        relationPageRequest.setStoreIds(Lists.newArrayList(storeIds));
        relationPageRequest.setRelationType(MallContractRelationType.TAB.getValue());
        final BaseResponse<CompanyMallContractRelationPageResponse> tableResponse = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
        return tableResponse.getContext().getPage().getContent();
    }

    private List<CompanyMallSupplierRecommendVO> listRecommendSupplier() {
        final CompanyMallSupplierRecommendPageRequest recommendPageRequest = new CompanyMallSupplierRecommendPageRequest();
        recommendPageRequest.setPageNum(0);
        recommendPageRequest.setPageSize(2000);
        recommendPageRequest.setDeleteFlag(DeleteFlag.NO);
        final BaseResponse<CompanyMallSupplierRecommendPageResponse> recommendPageResponseBaseResponse = companyIntoPlatformQueryProvider.pageSupplierRecommend(recommendPageRequest);
        return recommendPageResponseBaseResponse.getContext().getPage().getContent();
    }

    private List<CompanyMallContractRelationVO> listMarketByMarketId(Long marketId) {
        final CompanyMallContractRelationPageRequest pageRequest = new CompanyMallContractRelationPageRequest();
        pageRequest.setPageNum(0);
        pageRequest.setPageSize(2000);
        pageRequest.setDeleteFlag(DeleteFlag.NO);
        pageRequest.setRelationValue(marketId.toString());
        pageRequest.setRelationType(MallContractRelationType.MARKET.getValue());
        final BaseResponse<CompanyMallContractRelationPageResponse> marketResponse = companyIntoPlatformQueryProvider.pageContractRelation(pageRequest);
        return marketResponse.getContext().getPage().getContent();
    }

    private Set<Long> dealQuerySearch(GoodsMallPlatformSupplierTabRequest request, Set<Long> contractStoreIds) {
        if (CollectionUtils.isEmpty(contractStoreIds)) return new HashSet<>();
        if (StringUtils.isNotBlank(request.getAdvertisingConfigId()) && (StringUtils.isNotBlank(request.getAdvertisingId()))) {
            return dealBannerSuppliers(contractStoreIds, request.getAdvertisingConfigId(), request.getAdvertisingId());
        } else if (StringUtils.isNotBlank(request.getKeyword())) {
            return dealKeyWordsSuppliers(request.getKeyword(), contractStoreIds);
        } else {
            return contractStoreIds;
        }
    }

    private EsGoodsResponse searchGoods(String keywords) {
        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        // 商品搜索
        esGoodsInfoQueryRequest.setAddedFlag(1);
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        esGoodsInfoQueryRequest.setContractStartDate(now);
        esGoodsInfoQueryRequest.setContractEndDate(now);
        esGoodsInfoQueryRequest.setAuditStatus(1);
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setLikeGoodsName(keywords);
        esGoodsInfoQueryRequest.setPageNum(0);
        esGoodsInfoQueryRequest.setPageSize(100);
        esGoodsInfoQueryRequest.setWareId(1L);
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        return esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);


    }

    private Set<Long> dealKeyWordsSuppliers(String keywords, Set<Long> recSuppliersCheck) {
        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        // 商品搜索
        esGoodsInfoQueryRequest.setAddedFlag(1);
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        esGoodsInfoQueryRequest.setContractStartDate(now);
        esGoodsInfoQueryRequest.setContractEndDate(now);
        esGoodsInfoQueryRequest.setAuditStatus(1);
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setKeywords(keywords);
        esGoodsInfoQueryRequest.setPageNum(0);
        esGoodsInfoQueryRequest.setPageSize(100);
        esGoodsInfoQueryRequest.setWareId(1L);
        esGoodsInfoQueryRequest.setQueryGoods(true);
        final EsGoodsResponse esGoodsResponse = esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        final Set<Long> skuStoreIds;
        if (CollectionUtils.isEmpty(esGoodsResponse.getGoodsList())) {
            skuStoreIds = new HashSet<>();
        } else {
            skuStoreIds = esGoodsResponse.getGoodsList().stream().map(GoodsVO::getStoreId).collect(Collectors.toSet());
        }
        // 店铺搜索
        final StorePageRequest storePageRequest = new StorePageRequest();
        storePageRequest.setPageNum(0);
        storePageRequest.setPageSize(100);
        storePageRequest.setDelFlag(DeleteFlag.NO);
        storePageRequest.setAuditState(CheckState.CHECKED);
        storePageRequest.setStoreName(keywords);
        final BaseResponse<StorePageResponse> storeSearch = storeQueryProvider.page(storePageRequest);
        final Set<Long> storeIdsLike = storeSearch.getContext().getStoreVOPage().getContent().stream().map(StoreVO::getStoreId).collect(Collectors.toSet());
        skuStoreIds.addAll(storeIdsLike);
        recSuppliersCheck.retainAll(skuStoreIds);
        return recSuppliersCheck;
    }

    private Set<Long> storeDealKeyWordsSuppliers(String keywords, Set<Long> recSuppliersCheck) {
        if (CollectionUtils.isEmpty(recSuppliersCheck)) {
            return new HashSet<>();
        }
        Set<Long> skuStoreIds = new HashSet<>();
        if (Objects.equals(mobileNacosConfig.getStoreLiveSearchSupplier(), keywords)) {
            final Map<Long, List<Integer>> liveStoreInfo = getLiveStoreInfo();
            final Set<Long> liveStoreIds = liveStoreInfo.keySet();
            if (CollectionUtils.isNotEmpty(liveStoreIds)){
                skuStoreIds.addAll(liveStoreIds);
            }
        } else {
            List<Long> brandIds = null;
            List<Long> cates = null;
            List<Long> storeCates = null;
            BaseResponse<GoodsBrandListResponse> list = goodsBrandQueryProvider.list(GoodsBrandListRequest.builder().likeBrandName(keywords).delFlag(0).build());
            if (Objects.nonNull(list.getContext())) {
                brandIds = list.getContext().getGoodsBrandVOList().stream().map(GoodsBrandVO::getBrandId).collect(Collectors.toList());
            }
            BaseResponse<GoodsCateListByConditionResponse> goodsCateListByConditionResponseBaseResponse = goodsCateQueryProvider.listByCondition(GoodsCateListByConditionRequest.builder().keywords(keywords).build());
            if (Objects.nonNull(goodsCateListByConditionResponseBaseResponse.getContext())) {
                cates = goodsCateListByConditionResponseBaseResponse.getContext().getGoodsCateVOList().stream().map(GoodsCateVO::getCateId).collect(Collectors.toList());
            }
            //加入店铺分类名称
            StoreCateQueryHasChildRequest storeCateQueryHasChildRequest = new StoreCateQueryHasChildRequest();
            storeCateQueryHasChildRequest.setCateName(keywords);
            BaseResponse<StoreCateListByIdsResponse> storeCateListByIdsResponseBaseResponse = storeCateQueryProvider.queryByStoreName(storeCateQueryHasChildRequest);
            if (Objects.nonNull(storeCateListByIdsResponseBaseResponse.getContext())) {
                storeCates = storeCateListByIdsResponseBaseResponse.getContext().getStoreCateVOList().stream().map(StoreCateVO::getStoreCateId).collect(Collectors.toList());
            }
            filterStoreByItem(brandIds, cates, storeCates, skuStoreIds);
            // 店铺搜索
            final StorePageRequest storePageRequest = new StorePageRequest();
            storePageRequest.setPageNum(0);
            storePageRequest.setPageSize(100);
            storePageRequest.setDelFlag(DeleteFlag.NO);
            storePageRequest.setAuditState(CheckState.CHECKED);
            storePageRequest.setStoreName(keywords);
            final BaseResponse<StorePageResponse> storeSearch = storeQueryProvider.page(storePageRequest);
            final Set<Long> storeIdsLike = storeSearch.getContext().getStoreVOPage().getContent().stream().map(StoreVO::getStoreId).collect(Collectors.toSet());
            skuStoreIds.addAll(storeIdsLike);
        }
        recSuppliersCheck.retainAll(skuStoreIds);
        return recSuppliersCheck;
    }

    private void filterStoreByItem(List<Long> brandIds, List<Long> cates, List<Long> storeCates, Set<Long> skuStoreIds) {
        if (CollectionUtils.isEmpty(brandIds) && CollectionUtils.isEmpty(cates) && CollectionUtils.isEmpty(storeCates)) {
            return;
        }
        final EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        if (CollectionUtils.isNotEmpty(brandIds)) {
            esGoodsInfoQueryRequest.setBrandIds(brandIds);
        } else if (CollectionUtils.isNotEmpty(cates)) {
            esGoodsInfoQueryRequest.setCateIds(cates);
        } else if (CollectionUtils.isNotEmpty(storeCates)) {
            esGoodsInfoQueryRequest.setStoreCateIds(storeCates);
        }
        // 商品搜索
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        esGoodsInfoQueryRequest.setContractStartDate(now);
        esGoodsInfoQueryRequest.setContractEndDate(now);
        esGoodsInfoQueryRequest.setAuditStatus(1);
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setPageNum(0);
        esGoodsInfoQueryRequest.setPageSize(1);
        esGoodsInfoQueryRequest.setWareId(1L);
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        final EsSearchResponse esGoodsInfoByParams = getEsGoodsInfoByParams(esGoodsInfoQueryRequest);
        esGoodsInfoByParams.getAggResultMap().forEach((k, v) -> {
            if (!"storeId".equals(k)) {
                return;
            }
            v.forEach(u -> skuStoreIds.add(Long.valueOf(u.getKey().toString())));
        });
    }

    private Set<Long> dealBannerSuppliers(Set<Long> recSuppliersCheck, String advertisingConfigId, String advertisingId) {
        Set<Long> emptyResult = new HashSet<>();
        final AdvertisingGetByIdRequest advertisingGetByIdRequest = new AdvertisingGetByIdRequest();
        advertisingGetByIdRequest.setAdvertisingId(advertisingId);
        final BaseResponse<AdvertisingResponse> byId = advertisingQueryProvider.getById(advertisingGetByIdRequest);
        if (null == byId || byId.getContext() == null) {
            return emptyResult;
        }
        final AdvertisingConfigVO configVO = byId.getContext().getAdvertisingConfigList().stream().filter(o -> o.getAdvertisingConfigId().equals(advertisingConfigId)).findFirst().orElse(null);
        if (null == configVO) {
            return emptyResult;
        }
        if (Objects.equals(configVO.getIsSuit(), AdvertisingConfigType.NEW)) {
            final StorePageRequest storePageRequest = new StorePageRequest();
            storePageRequest.setPageNum(0);
            storePageRequest.setPageSize(1000);
            final Integer dateNum = configVO.getDateNum();
            if (null != dateNum) {
                LocalDateTime l;
                if (Objects.equals(AdvertisingConfigEnums.DayType.DAY.getValue(), configVO.getDateType())) {
                    l = LocalDateTime.now().plusDays(-configVO.getDateType());
                } else if (Objects.equals(AdvertisingConfigEnums.DayType.MONTH.getValue(), configVO.getDateType())) {
                    l = LocalDateTime.now().plusMonths(-configVO.getDateType());
                } else {
                    return emptyResult;
                }
                storePageRequest.setApplyEnterTimeGte(l);
                final BaseResponse<StorePageResponse> page = storeQueryProvider.page(storePageRequest);
                final Set<Long> newStoreList = page.getContext().getStoreVOPage().getContent().stream().map(StoreVO::getStoreId).collect(Collectors.toSet());
                recSuppliersCheck.retainAll(newStoreList);
                return recSuppliersCheck;
            } else {
                return emptyResult;
            }
        } else if (Objects.equals(configVO.getIsSuit(), AdvertisingConfigType.MARKETING)) {
            final BaseResponse<MarketingEffectiveStoreResponse> marketingEffectiveStore = marketingQueryProvider.listEffectiveStore();
            final List<Long> storeIds = marketingEffectiveStore.getContext().getStoreIds();
            recSuppliersCheck.retainAll(storeIds);
            return recSuppliersCheck;
        } else {
            return recSuppliersCheck;
        }
    }


    @ApiOperation(value = "平台首页广告位置")
    @RequestMapping(value = "/advertising-list", method = RequestMethod.POST)
    public BaseResponse<PlatformMallAdvertisingResponse> listAdvertising(@RequestBody GoodsMallPlatformMarketRequest request) {
        final AdvertisingQueryRequest advertisingQueryRequest = new AdvertisingQueryRequest();
        advertisingQueryRequest.setWareId(1L);
        List<AdvertisingVO> advertisingVOList = advertisingQueryProvider.listByCache(advertisingQueryRequest).getContext().getAdvertisingVOList();
        final PlatformMallAdvertisingResponse response = new PlatformMallAdvertisingResponse();
        advertisingVOList = advertisingVOList.stream().filter(o -> Objects.equals(o.getStoreId(), -1L)).collect(toList());
        response.setAdvertisingVOList(advertisingVOList);
        final Operator operator = commonUtil.getOperator();
        homePageRedisCache.cacheHomePageVisit(operator == null ? null : operator.getAccount());
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "平台首页广告商家端位置")
    @RequestMapping(value = "/advertising-store-list", method = RequestMethod.POST)
    public BaseResponse<PlatformMallAdvertisingResponse> listStoreAdvertising(@RequestBody GoodsMallPlatformMarketRequest request) {
        final AdvertisingQueryRequest advertisingQueryRequest = new AdvertisingQueryRequest();
        advertisingQueryRequest.setWareId(1L);
        List<AdvertisingVO> advertisingVOList = advertisingQueryProvider.listByCache(advertisingQueryRequest).getContext().getAdvertisingVOList();
        // 商家ids
        List<Long> storeIds = advertisingVOList.stream().map(AdvertisingVO::getStoreId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIds)) {
            // 一次性给商家名称赋值
            BaseResponse<ListStoreByIdsResponse> listByIds = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<>(storeIds)).build());
            advertisingVOList.forEach(a -> {
                Optional<StoreVO> findFirst = listByIds.getContext().getStoreVOList().stream().filter(s -> s.getStoreId().equals(a.getStoreId())).findFirst();
                findFirst.ifPresent(v -> {
                    a.setStoreName(v.getStoreName());
                });
                //赋值advertisingVOList集合
                this.buildAdvertisingVOList(a.getAdvertisingConfigList());
            });
        }
        final PlatformMallAdvertisingResponse response = new PlatformMallAdvertisingResponse();
//        advertisingVOList = advertisingVOList.stream().filter(o -> Objects.equals(o.getStoreId(), -1L)).collect(toList());
        response.setAdvertisingVOList(advertisingVOList);
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "根据店铺id获取店铺信息")
    @RequestMapping(value = "/supplier/get/{storeId}", method = RequestMethod.GET)
    public BaseResponse<GoodsMallPlatformSupplierVO> getSupplierByStoreId(@PathVariable Long storeId) {
        if (null == storeId) {
            return BaseResponse.success(new GoodsMallPlatformSupplierVO());
        }
        final BaseResponse<StoreBaseResponse> responseBaseResponse = storeQueryProvider.getStoreBaseInfoById(StoreBaseInfoByIdRequest.builder().storeId(storeId).build());
        final StoreBaseResponse storeBaseResponse = responseBaseResponse.getContext();
        final GoodsMallPlatformSupplierVO supplierVO = new GoodsMallPlatformSupplierVO();
        supplierVO.setCompanyInfoId(storeBaseResponse.getCompanyInfoId());
        supplierVO.setStoreName(storeBaseResponse.getStoreName());
        supplierVO.setStoreId(storeBaseResponse.getStoreId());
        supplierVO.setStoreLogo(storeBaseResponse.getStoreLogo());
        supplierVO.setStoreSign(storeBaseResponse.getStoreSign());
        supplierVO.setContactPhone(storeBaseResponse.getContactMobile());
        supplierVO.setCompanyType(CompanyType.fromValue(storeBaseResponse.getCompanyType()));
        return BaseResponse.success(supplierVO);
    }

    private List<MallMarketSortVO> sortSuppliers(Set<Long> tabStoreIds, Map<Long, CompanyMallSupplierRecommendVO> setSupplierMap, Map<String, String> buyerStoreCacheMap, Map<String, String> marketStoreCacheMap, Set<Long> onSaleGoodsStoreIds) {
        List<MallMarketSortVO> supplierSortTargets = new ArrayList<>();
        tabStoreIds.forEach(storeId -> {
            String storeIdStr = storeId.toString();
            final MallMarketSortVO sortVO = new MallMarketSortVO();
            supplierSortTargets.add(sortVO);
            sortVO.setId(storeIdStr);
//            // 后面走配置中心Id
//            if (Objects.equals(storeIdStr, mobileConfig.getHomeDefaultStoreId().toString())) {
//                sortVO.setOne(BigDecimal.ZERO);
//            } else {
//                sortVO.setOne(BigDecimal.ONE);
//            }
            if (onSaleGoodsStoreIds.contains(storeId)){
                sortVO.setOne(BigDecimal.ZERO);
            }else {
                sortVO.setOne(BigDecimal.ONE);
            }
            // 个人喜好
            sortVO.setTwo(wrapFeeSort(buyerStoreCacheMap.get(storeIdStr)));
            sortVO.setThree(wrapFeeSort(marketStoreCacheMap.get(storeIdStr)));
//            sortVO.setThree(BigDecimal.ZERO);
            // 排序
            final CompanyMallSupplierRecommendVO companyMallSupplierRecommendVO = setSupplierMap.get(storeId);
            if (null != companyMallSupplierRecommendVO && companyMallSupplierRecommendVO.getSort() != null) {
                sortVO.setFour(companyMallSupplierRecommendVO.getSort());
            } else {
                sortVO.setFour(new BigDecimal("1000"));
            }
        });
        // 倒序
        supplierSortTargets.sort(Comparator.comparing(MallMarketSortVO::getOne).thenComparing(MallMarketSortVO::getTwo).thenComparing(MallMarketSortVO::getThree).thenComparing(MallMarketSortVO::getFour));
        return supplierSortTargets;
    }

    private void wrapCompanySupplier(List<GoodsMallPlatformSupplierVO> targetSupplierVOS, MallMarketSortVO sortVO, Set<Long> matchStoreIds, Map<Long, StoreSimpleResponse> storeVOMap) {
        final Long storeId = Long.valueOf(sortVO.getId());
        if (!matchStoreIds.contains(storeId)) return;
        final StoreSimpleResponse storeVO = storeVOMap.get(storeId);
        if (null == storeVO) return;
        // 过滤期待中的商家
        if (matchStoreIds.size() > 1 && Objects.equals(storeId, mobileConfig.getWaitingForStoreId())) return;

        final GoodsMallPlatformSupplierVO companySupplier = new GoodsMallPlatformSupplierVO();
        targetSupplierVOS.add(companySupplier);
        companySupplier.setStoreId(storeId);
        companySupplier.setStoreName(storeVO.getStoreName());
        companySupplier.setStoreLogo(storeVO.getStoreLogo());
        companySupplier.setStoreSign(storeVO.getStoreSign());
        companySupplier.setContactPhone(storeVO.getContactMobile());
        companySupplier.setCompanyType(storeVO.getCompanyType());
        if (null != sortVO.getTwo() && BigDecimal.ZERO.compareTo(sortVO.getTwo()) != 0){
            companySupplier.setCustomerBought(true);
        }
        if (Objects.equals(mobileConfig.getWaitingForStoreId(), storeId)) {
            companySupplier.setWaitingForStoreId(true);
        } else {
            companySupplier.setWaitingForStoreId(false);
        }
        companySupplier.setCompanyInfoId(storeVO.getCompanyInfoId());
    }

    private void fillTabRecommend(List<MallMarketSortVO> supplierSortTabs, Set<Long> recommendStoreIds, List<GoodsMallPlatformSupplierTabVO> mallTabs, Map<Long, StoreSimpleResponse> storeVOMap, Map<Long, List<Integer>> liveStoreIds, Map<Long, CompanyMallSupplierRecommendVO> recommendSupplierMap, Map<Long, Integer> recommendAdvSortMap) {
        final GoodsMallPlatformSupplierTabVO mallTab = new GoodsMallPlatformSupplierTabVO();
        mallTabs.add(mallTab);
        mallTab.setTabId(0L);
        mallTab.setTabName("推荐");
        List<GoodsMallPlatformSupplierVO> targetSupplierVOS = new ArrayList<>();
        mallTab.setCompanySuppliers(targetSupplierVOS);
        // 从排序里面选择
        supplierSortTabs.forEach(sortVO -> wrapCompanySupplier(targetSupplierVOS, sortVO, recommendStoreIds, storeVOMap));
        // 处理指定顺序
        dealAssignSortR(targetSupplierVOS, recommendSupplierMap, recommendAdvSortMap);
        wrapLivePop(liveStoreIds, targetSupplierVOS, mallTab.getTabId());
    }

    private void dealAssignSortR(List<GoodsMallPlatformSupplierVO> targetSupplierVOS, Map<Long, CompanyMallSupplierRecommendVO> recommendSupplierMap, Map<Long, Integer> recommendAdvSortMap) {
        if (CollectionUtils.isEmpty(targetSupplierVOS)) return;
        Map<Long, Integer> adjustMap = new HashMap<>();
        try {
            targetSupplierVOS.forEach(o -> {
                final CompanyMallSupplierRecommendVO storeVO = recommendSupplierMap.get(o.getStoreId());
                if (null == storeVO) return;
                final Integer assignSort = storeVO.getAssignSort();
                if (null == assignSort || assignSort < 1 || assignSort > targetSupplierVOS.size()) return;
                adjustMap.put(o.getStoreId(), assignSort - 1);
            });
            dealAssignSort(targetSupplierVOS, adjustMap);

            // 广告位重新指定位置
            if (null != recommendAdvSortMap && recommendAdvSortMap.size() >= 1) {
                Map<Long, Integer> adjust2Map = new HashMap<>();
                targetSupplierVOS.forEach(o -> {
                    final Integer assignSort = recommendAdvSortMap.get(o.getStoreId());
                    if (null == assignSort || assignSort < 1 || assignSort > targetSupplierVOS.size()) return;
                    adjust2Map.put(o.getStoreId(), assignSort - 1);
                });
                dealAssignSort(targetSupplierVOS, adjust2Map);
            }
        } catch (Exception e) {
            log.error("dealAssignSortR error", e);
        }
    }

    private void dealAssignSortT(List<GoodsMallPlatformSupplierVO> targetSupplierVOS, Map<Long, StoreSimpleResponse> storeVOMap, Map<Long, Integer> tabAdvSortMap) {
        if (CollectionUtils.isEmpty(targetSupplierVOS)) return;
        Map<Long, Integer> adjustMap = new HashMap<>();
        try {
            targetSupplierVOS.forEach(o -> {
                final StoreSimpleResponse storeVO = storeVOMap.get(o.getStoreId());
                if (null == storeVO) return;
                final Integer assignSort = storeVO.getAssignSort();
                if (null == assignSort || assignSort < 1 || assignSort > targetSupplierVOS.size()) return;
                adjustMap.put(o.getStoreId(), assignSort - 1);
            });
            dealAssignSort(targetSupplierVOS, adjustMap);

            // 广告位重新指定位置
            if (null != tabAdvSortMap && tabAdvSortMap.size() >= 1) {
                Map<Long, Integer> adjust2Map = new HashMap<>();
                targetSupplierVOS.forEach(o -> {
                    final Integer assignSort = tabAdvSortMap.get(o.getStoreId());
                    if (null == assignSort || assignSort < 1 || assignSort > targetSupplierVOS.size()) return;
                    adjust2Map.put(o.getStoreId(), assignSort - 1);
                });
                dealAssignSort(targetSupplierVOS, adjust2Map);
            }
        } catch (Exception e) {
            log.error("dealAssignSortT error", e);
        }
    }

    private void dealAssignSort(List<GoodsMallPlatformSupplierVO> targetSupplierVOS, Map<Long, Integer> adjustMap) {
        if (adjustMap.isEmpty()) return;
        List<Map.Entry<Long, Integer>> sortedEntries = new ArrayList<>(adjustMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());
        List<GoodsMallPlatformSupplierVO> adjList = new ArrayList<>();

        Set<Integer> insetIndex =  new HashSet<>();
        for (Map.Entry<Long, Integer> entry : sortedEntries) {
            Long storeId = entry.getKey();
            int targetIndex = entry.getValue();

            GoodsMallPlatformSupplierVO student = null;
            for (GoodsMallPlatformSupplierVO s : targetSupplierVOS) {
                if (storeId.equals(s.getStoreId())) {
                    student = s;
                    break;
                }
            }

            if (student != null) {
                targetSupplierVOS.remove(student);
                adjList.add(student);
                insetIndex.add(targetIndex);
            }
        }

        if (CollectionUtils.isEmpty(insetIndex)) return;
        // 重新加回去
        insetIndex.forEach(index -> {
            if (adjList.size() < 1) return;
            targetSupplierVOS.add(index, adjList.remove(0));
        });
    }

    private void wrapLivePop(Map<Long, List<Integer>> liveStoreIds, List<GoodsMallPlatformSupplierVO> targetSupplierVOS, Long tabId) {
        if (CollectionUtils.isEmpty(targetSupplierVOS)) {
            return;
        }
        // 获取首页gif
        List<GoodsMallPlatformGifUrlHomePageVO> giftGifUrlHomePageVO = getGifForHomePage(tabId);
        for (int i = 0; i < targetSupplierVOS.size(); i++) {
            final GoodsMallPlatformSupplierVO o = targetSupplierVOS.get(i);
            if (MapUtils.isNotEmpty(liveStoreIds) && liveStoreIds.containsKey(o.getStoreId())) {
                o.setLiveNow(true);
                o.setLiveRooms(liveStoreIds.get(o.getStoreId()));
                o.setLiveDesc(mobileNacosConfig.getStoreLiveOpenMessage());
            } else {
                o.setLiveNow(false);
                o.setLiveDesc(mobileNacosConfig.getStoreLiveCloseMessage());
            }

            if (CollectionUtils.isNotEmpty(giftGifUrlHomePageVO) && giftGifUrlHomePageVO.size() > i) {
                o.setGifUrl(giftGifUrlHomePageVO.get(i).getUrl());
                o.setImageUrl(giftGifUrlHomePageVO.get(i).getUrlImage());
            }
        }
    }

    private List<GoodsMallPlatformGifUrlHomePageVO> getGifForHomePage(Long tabId) {
        try {
            String gifUrlHomepage;
            if (Objects.equals(tabId,0L)){
                gifUrlHomepage = mobileNacosConfig.getPopUrlHomePageRec();
            }else {
                gifUrlHomepage = mobileNacosConfig.getPopUrlHomePage();
            }
            if (StringUtils.isNotBlank(gifUrlHomepage)) {
                final List<GoodsMallPlatformGifUrlHomePageVO> gifUrlHomePageVols = JSONObject.parseArray(gifUrlHomepage, GoodsMallPlatformGifUrlHomePageVO.class);
                if (CollectionUtils.isNotEmpty(gifUrlHomePageVols)) {
                    gifUrlHomePageVols.sort(Comparator.comparing(GoodsMallPlatformGifUrlHomePageVO::getId));
                }
                return gifUrlHomePageVols;
            }
        } catch (Exception e) {
            log.error("getGifForHomePage error", e);
        }
        return new ArrayList<>();
    }

    /**
     * 赋值advertisingVOList集合
     */
    private void buildAdvertisingVOList(List<AdvertisingConfigVO> advertisingConfigList) {
        advertisingConfigList.forEach(configVO -> {
            if (StringUtils.isNotEmpty(configVO.getAdvertisingIds())) {
                List<String> result = new ArrayList<>();
                Collections.addAll(result, configVO.getAdvertisingIds().split(SEPARATOR));
                AdvertisingQueryRequest advertisingQueryRequest = AdvertisingQueryRequest.builder().advertisingIds(result).build();
                configVO.setAdvertisingVOList(advertisingQueryProvider.list(advertisingQueryRequest).getContext().getAdvertisingVOList());
            }
        });
    }

    public EsSearchResponse getEsGoodsInfoByParams(EsGoodsInfoQueryRequest queryRequest) {
        //聚合品牌
        queryRequest.putAgg(AggregationBuilders.terms("storeId").field("goodsInfos.storeId").size(2000));
        if (Objects.nonNull(queryRequest.getSortFlag()) && queryRequest.getSortFlag() == 11) {
            queryRequest.getSorts().removeAll(queryRequest.getSorts());
            queryRequest.putSort("goodsSeqNum", SortOrder.ASC);
        }
        final SearchQuery searchCriteria = queryRequest.getSearchCriteria();
        log.info("分组查找店铺参数：" + JSON.toJSONString(searchCriteria));
        EsSearchResponse response = elasticsearchTemplate.query(searchCriteria, searchResponse -> EsSearchResponse.buildGoods(searchResponse, resultsMapper));
        return response;
    }

}
