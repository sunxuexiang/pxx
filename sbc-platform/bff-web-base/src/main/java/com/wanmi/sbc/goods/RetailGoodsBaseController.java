package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelWithDefaultByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandShelflifeResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
//import com.wanmi.sbc.goods.api.provider.goodsimage.GoodsImageProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.RetailGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockQueryRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.GoodsByParentCateIdQueryRequest;
import com.wanmi.sbc.goods.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.hotstylemoments.HotStyleMomentsProvider;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailGoodsConfigVO;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsConfigVO;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 零售商品
 * @author: XinJiang
 * @time: 2022/4/14 10:30
 */
@RestController
@RequestMapping("/retail/goods")
@Api(tags = "RetailGoodsBaseController", description = "S2B web公用-散批商品信息API")
@Slf4j
public class RetailGoodsBaseController {

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private RetailShopCartProvider retailShopCartProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private HotStyleMomentsProvider hotStyleMomentsProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private RetailGoodsRecommendSettingProvider retailGoodsRecommendSettingProvider;

//    @Autowired
//    private GoodsImageProvider goodsImageProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation("爆款时刻查询并排序")
    @PostMapping("/get-hot-style-moments")
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> getHotStyleMoments(HttpServletRequest httpRequest) {
        EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
        HotStyleMomentsVO hotStyleMomentsVO = new HotStyleMomentsVO();
        if (redisService.hasKey(CacheKeyConstant.RETAIL_HOT_STYLE_MOMENTS)) {
            hotStyleMomentsVO = redisService.getObj(CacheKeyConstant.RETAIL_HOT_STYLE_MOMENTS, HotStyleMomentsVO.class);
        } else {
            hotStyleMomentsProvider.fillRedis();
            hotStyleMomentsVO = redisService.getObj(CacheKeyConstant.RETAIL_HOT_STYLE_MOMENTS, HotStyleMomentsVO.class);
        }
        if (Objects.nonNull(hotStyleMomentsVO)) {
            CustomerVO customer = commonUtil.getCustomer();
            CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
            //爆款信息sku集合
            List<String> skuIds = hotStyleMomentsVO.getHotStyleMomentsConfigs().stream()
                    .map(HotStyleMomentsConfigVO::getGoodsInfoId).collect(Collectors.toList());
            //爆款信息sku商品排序map
            Map<String,Integer> sortMap = hotStyleMomentsVO.getHotStyleMomentsConfigs().stream()
                    .collect(Collectors.toMap(HotStyleMomentsConfigVO::getGoodsInfoId, HotStyleMomentsConfigVO::getSortNum, (k1,k2) -> k1));
            EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
            request.setGoodsInfoIds(skuIds);
            request.setPageSize(1000);
            request.setPageNum(0);
            EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(request, customer,httpRequest);
            esGoodsLimitBrandResponse.getEsGoods().getContent().forEach(esGoods -> {
                esGoods.getGoodsInfos().forEach(goodsInfoNest -> {
                    Integer sortNum = sortMap.getOrDefault(goodsInfoNest.getGoodsInfoId(),null);
                    if (Objects.nonNull(sortNum)) {
                        esGoods.setGoodsSeqNum(sortNum);
                    } else {
                        esGoods.setGoodsSeqNum(0);
                    }
                    /*if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }*/
                });
            });
//            esGoodsLimitBrandResponse.getEsGoods().getContent().sort(Comparator.comparing(EsGoods::getGoodsSeqNum));
            BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);
            realResponse.setBannerImageUrl(hotStyleMomentsVO.getBannerImageUrl());
        }
        return BaseResponse.success(realResponse);
    }

    @ApiOperation("分栏商品查询并排序")
    @PostMapping("/get-column-goods")
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> getColumnGoods(@RequestBody List<AdvertisingRetailGoodsConfigVO> advertisingRetailGoodsConfigVOS,HttpServletRequest httpRequest) {
        EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
        if (CollectionUtils.isNotEmpty(advertisingRetailGoodsConfigVOS)) {
            CustomerVO customer = commonUtil.getCustomer();
            advertisingRetailGoodsConfigVOS.sort(Comparator.comparing(AdvertisingRetailGoodsConfigVO::getSortNum));
            Map<String,Integer> sortMap = advertisingRetailGoodsConfigVOS.stream().collect(Collectors.toMap(AdvertisingRetailGoodsConfigVO::getGoodsInfoId,AdvertisingRetailGoodsConfigVO::getSortNum,(key1,key2) -> key1));
            List<String> skuIds = advertisingRetailGoodsConfigVOS.stream().map(AdvertisingRetailGoodsConfigVO::getGoodsInfoId).collect(Collectors.toList());
            EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
            request.setGoodsInfoIds(skuIds);
            request.setPageSize(1000);
            request.setPageNum(0);
            EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(request, customer,httpRequest);
            esGoodsLimitBrandResponse.getEsGoods().getContent().forEach(esGoods -> {
                esGoods.getGoodsInfos().forEach(goodsInfoNest -> {
                    Integer sortNum = sortMap.getOrDefault(goodsInfoNest.getGoodsInfoId(),null);
                    if (Objects.nonNull(sortNum)) {
                        esGoods.setGoodsSeqNum(sortNum);
                    } else {
                        esGoods.setGoodsSeqNum(0);
                    }
                });
            });
            BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);
        }
        return BaseResponse.success(realResponse);
    }

    @ApiOperation("获取散批推荐商品列表商品信息")
    @PostMapping("/get-recommend-by-cache")
    public BaseResponse<GoodsInfoViewByIdsResponse> getListByCache(HttpServletRequest httpRequest){
        if (!redisService.hasKey(CacheKeyConstant.RETAIL_GOODS_RECOMMEND)) {
            retailGoodsRecommendSettingProvider.fillRedis();
        }
        GoodsInfoViewByIdsResponse response = JSONObject.parseObject(redisService.getString(CacheKeyConstant.RETAIL_GOODS_RECOMMEND),GoodsInfoViewByIdsResponse.class);
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
            List<String> skuIds = response.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider
                    .getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest.builder().goodsForIdList(skuIds).build())
                    .getContext().getGoodsWareStockVOList();
            if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    GoodsWareStockVO goodsWareStockVO = goodsWareStockVOList.stream()
                            .filter(i -> i.getGoodsInfoId().equals(goodsInfoVO.getGoodsInfoId())).collect(Collectors.toList())
                            .stream().findFirst().orElse(null);
                    if (Objects.nonNull(goodsWareStockVO)) {
                        goodsInfoVO.setStock(goodsWareStockVO.getStock());
                    }
                    /*if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())
                            && goodsInfoVO.getGoodsStatus().equals(GoodsStatus.OK)){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }*/
                });

                response.setGoodsInfos(response.getGoodsInfos().stream().filter(v->{
                    if (v.getGoodsStatus().equals(GoodsStatus.INVALID)){
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList()));


            }
        }

        return BaseResponse.success(response);
    }

    /**
     * 商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "商品分页")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> goodslist(@RequestBody EsGoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customer,httpRequest);
        if (queryRequest.getCateId() != null) {
            // 根据分类查询分类名称
            GoodsCateByIdRequest request = new GoodsCateByIdRequest();
            request.setCateId(queryRequest.getCateId());
            GoodsCateByIdResponse goodsCateByIdResponse = goodsCateQueryProvider.getById(request).getContext();
            if (Objects.nonNull(goodsCateByIdResponse)) {
                esGoodsLimitBrandResponse.setCateName(goodsCateByIdResponse.getCateName());
            }
        }
        EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
        BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);

        return BaseResponse.success(realResponse);
    }

    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse list(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        if(Objects.nonNull(queryRequest.getWareId())){
            queryRequest.setWareId(null);
        }
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }
        queryRequest.setIsKeywords(false);
        if (StringUtils.isNotEmpty(queryRequest.getKeywords())) {
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
            queryRequest.setGoodsInfoIds(
                    marketingQueryProvider.getByIdForCustomer(marketingGetByIdRequest).getContext()
                            .getMarketingForEndVO().getMarketingScopeList().stream().map(MarketingScopeVO::getScopeId)
                            .collect(Collectors.toList()));
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
        if (Objects.nonNull(queryRequest.getCateId()) && queryRequest.getSortByCateBrand()) {
            CateBrandSortRelListRequest cateBrandSortRelListRequest = new CateBrandSortRelListRequest();
            cateBrandSortRelListRequest.setCateId(queryRequest.getCateId());
            List<CateBrandSortRelVO> cateBrandSortRelVOList =
                    cateBrandSortRelQueryProvider.list(cateBrandSortRelListRequest).getContext().getCateBrandSortRelVOList();
            //当前三级类目包含的品牌ID
            List<Long> cateBindBrandIds = cateBrandSortRelVOList.stream()
                    .filter(item -> item.getSerialNo() != 0)
                    .sorted(Comparator.comparing(CateBrandSortRelVO::getSerialNo)) //按照排序字段升序排列
                    .map(item -> item.getBrandId()).collect(Collectors.toList());

            queryRequest.setCateBindBrandIds(cateBindBrandIds);
            queryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
            response = esRetailGoodsInfoElasticService.pageByGoods(queryRequest);
        } else {
            response = esRetailGoodsInfoElasticService.pageByGoods(queryRequest);
        }

        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsLimitBrandResponse();
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

            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                goodsInfoList = filterResponse.getGoodsInfoVOList();
            }
            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
                //购物车
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = retailShopCartProvider.fillBuyCount(request)
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
            /*CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)) {
                deliveryAddress = commonUtil.getDeliveryAddress();
            }*/
            CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);

            response.getEsGoods().getContent().forEach(esGoods -> {
                List<GoodsInfoVO> goodsInfoVOList = voMap.get(esGoods.getId());
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
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

                    List<String> goodsIds = new ArrayList<>();
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        goodsIds.add(goodsInfoNest.getGoodsId());
                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {
                            List<GoodsWareStockVO> goodsWareStockVOS = optionalGoodsInfoNest.get().getGoodsWareStockVOS();
                            if (CollectionUtils.isNotEmpty(goodsWareStockVOS)) {
                                List<GoodsWareStockVO> stockList;
                                if (CollectionUtils.isNotEmpty(finalUnOnline)) {
                                    stockList = goodsWareStockVOS.stream().
                                            filter(goodsWareStock -> finalUnOnline.contains(goodsWareStock.getWareId())).
                                            collect(Collectors.toList());
                                } else {
                                    stockList = goodsWareStockVOS;
                                }
                                if (CollectionUtils.isNotEmpty(stockList)) {
                                    BigDecimal sumStock = stockList.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                                    goodsInfoNest.setStock(sumStock);
                                } else {
                                    goodsInfoNest.setStock(BigDecimal.ZERO);
                                }
                                //活动限购库存
                                if (Objects.nonNull(goodsInfoNest.getMarketingLabels())) {
                                    goodsInfoNest.getMarketingLabels().forEach(marketingLabelVO -> {
                                        if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
                                                && marketingLabelVO.getGoodsPurchasingNumber() > 0
                                                && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
                                            goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
                                        }
                                    });
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
                                        if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0) {
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
//                    List<GoodsImageVO> context = goodsImageProvider.getGoodsImagesByGoodsIds(goodsIds).getContext();

                    //设置企业商品的审核状态 ，以及会员的大客户价,及图片
                    resultGoodsInfos.forEach(goodsInfoNest -> {

//                        if (queryRequest.getImageFlag() &&  CollectionUtils.isNotEmpty(context)) {
//                            Map<String, List<GoodsImageVO>> goodsImagesMap = context.stream().collect(Collectors.groupingBy(GoodsImageVO::getGoodsId));
//
//                            List<GoodsImageVO> goodsImages = goodsImagesMap.get(goodsInfoNest.getGoodsId());
//                            if(CollectionUtils.isNotEmpty(goodsImages) && goodsImages.size() > 1){
//                                goodsInfoNest.setGoodsInfoImg(goodsImages.get(1).getArtworkUrl());
//                            }
//                        }

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
                        if (CollectionUtils.isNotEmpty(goodsInfoNest.getMarketingLabels())) {
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

                        /*if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())
                                && goodsInfoNest.getGoodsStatus().equals(GoodsStatus.OK)) {
                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                    .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                            if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                            }
                        }*/
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

        return page;
    }

    /**
     * 分类商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "分类商品分页")
    @RequestMapping(value = "/brandSeqBySpus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsResponse> cateBrandSortGoodslist(@RequestBody GoodsByParentCateIdQueryRequest queryRequest,HttpServletRequest httpRequest) {
        //迎合安卓
        queryRequest.setWareId(null);
        queryRequest.setMatchWareHouseFlag(false);
        CustomerVO customer = commonUtil.getCustomer();
        EsGoodsResponse esGoodsLimitBrandResponse = listByBrandSeq(queryRequest, customer,httpRequest);

        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();

        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
            return BaseResponse.success(EsGoodsResponse.builder().esGoods(new PageImpl<>(Lists.newArrayList())).build());
        }
        if(CollectionUtils.isNotEmpty(esGoodsLimitBrandResponse.getGoodsList())){
            Map<String, GoodsVO> goodsMap = esGoodsLimitBrandResponse.getGoodsList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g));
            List<EsGoods> goodsData = listEsGoods.getContent();
            for (EsGoods goodsDatum : goodsData) {
                GoodsVO goodsVO = goodsMap.get(goodsDatum.getId());
                if(Objects.nonNull(goodsVO) && CollectionUtils.isNotEmpty(goodsDatum.getGoodsInfos())){
                    goodsDatum.getGoodsInfos().forEach(var->{
                        var.setGoodsSubtitle(goodsVO.getGoodsSubtitle());
                        var.setGoodsSubtitleNew(goodsVO.getGoodsSubtitleNew());
                    });
                }
            }
        }
        return BaseResponse.success(esGoodsLimitBrandResponse);
    }


    /**
     * 获取3级分类无商品的分类的id todo 这个接口需要放入白名单不需要登录也可查看
     * @return
     */
    @ApiOperation(value = "获取3级分类无零售商品的分类的id")
    @RequestMapping(value = "/getNoGoodsCateids", method = RequestMethod.POST)
    public BaseResponse<List<Long>> getNoGoodsCateids (){
          return   goodsCateQueryProvider.getNoGoodsCateids();
    }


    /**
     * 根据分类id查询商品
     */
    /**
     * 查询SPU列表
     *
     * @param cateIdQueryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsResponse listByBrandSeq(GoodsByParentCateIdQueryRequest cateIdQueryRequest, CustomerVO customer,HttpServletRequest httpRequest) {

        GoodsInfoQueryRequest queryRequest = KsBeanUtil.convert(cateIdQueryRequest, GoodsInfoQueryRequest.class);
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
//        Boolean vipPriceFlag = false;
//        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
//            vipPriceFlag = true;
//        }
//        if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())) {
//            vipPriceFlag = true;
//        }
        if (Objects.nonNull(domainInfo)) {
            queryRequest.setStoreId(domainInfo.getStoreId());
        }
        //只看分享赚商品信息
        if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue() ==
                queryRequest.getDistributionGoodsAudit()) {
            queryRequest.setDistributionGoodsStatus(NumberUtils.INTEGER_ZERO);
        }

        /**
         * 请求参数转化
         * @param
         */
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);

        //此接口不支持关键词搜索
        esGoodsInfoQueryRequest.setIsKeywords(false);

        //获取会员和等级
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        //B2b模式下，可以按会员/客户价格排序，否则按市场价排序
        if (Objects.nonNull(customer) && osUtil.isB2b()) {
            CustomerLevelWithDefaultByCustomerIdResponse response = customerLevelQueryProvider
                    .getCustomerLevelWithDefaultByCustomerId(
                            CustomerLevelWithDefaultByCustomerIdRequest.builder().customerId(customer.getCustomerId()
                            ).build
                                    ()).getContext();
            esGoodsInfoQueryRequest.setCustomerLevelId(response.getLevelId());
            esGoodsInfoQueryRequest.setCustomerLevelDiscount(response.getLevelDiscount());
        } else {
            String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
            esGoodsInfoQueryRequest.setContractStartDate(now);
            esGoodsInfoQueryRequest.setContractEndDate(now);
            esGoodsInfoQueryRequest.setCustomerLevelId(0L);
            esGoodsInfoQueryRequest.setCustomerLevelDiscount(BigDecimal.ONE);
        }

        EsGoodsResponse response;
        esGoodsInfoQueryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
        //分类查询特定情况，通过分类绑定的品牌进行排序
        if (Objects.nonNull(queryRequest.getCateId()) && queryRequest.getSortByCateBrand()) {
            CateBrandSortRelListRequest cateBrandSortRelListRequest = new CateBrandSortRelListRequest();
            cateBrandSortRelListRequest.setCateId(queryRequest.getCateId());
            List<CateBrandSortRelVO> cateBrandSortRelVOList =
                    cateBrandSortRelQueryProvider.list(cateBrandSortRelListRequest).getContext().getCateBrandSortRelVOList();
            //当前三级类目包含的品牌ID
            List<Long> cateBindBrandIds = cateBrandSortRelVOList.stream()
                    .filter(item -> item.getSerialNo() != 0)
                    .sorted(Comparator.comparing(CateBrandSortRelVO::getSerialNo)) //按照排序字段升序排列
                    .map(item -> item.getBrandId()).collect(Collectors.toList());

            esGoodsInfoQueryRequest.setCateBindBrandIds(cateBindBrandIds);
            response = esRetailGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        } else {
            response = esRetailGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        }
        // log.info("散批es查询:::{}",response);
        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsResponse();
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

            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                goodsInfoList = filterResponse.getGoodsInfoVOList();
            }
            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = retailShopCartProvider.fillBuyCount(request)
                        .getContext();
                goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
            }
            //重新赋值于Page内部对象
//            Boolean finalVipPriceFlag = vipPriceFlag;
            Set<Long> storeIds = new HashSet<>(10);
            for (EsGoods inner : response.getEsGoods()) {
                Set<Long> collect = inner.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId).collect(Collectors.toSet());
                storeIds.addAll(collect);
            }

            //通过客户收货地址和商品指定区域设置商品状态
            //根据用户ID得到收货地址
            /*CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)) {
                deliveryAddress = commonUtil.getDeliveryAddress();
            }*/
            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);

            //重新赋值于Page内部对象
            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy
                    (GoodsInfoVO::getGoodsId));

            response.getEsGoods().getContent().forEach(esGoods -> {
                esGoods.setGoodsInfos(KsBeanUtil.convert(voMap.get(esGoods.getId()), GoodsInfoNest.class));
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();

                goodsInfoNests.forEach(goodsInfoNest -> {

                    goodsInfoNest.setGoodsFavorableCommentNum(esGoods.getGoodsFavorableCommentNum());
                    goodsInfoNest.setGoodsSalesNum(esGoods.getGoodsSalesNum());
                    goodsInfoNest.setGoodsCollectNum(esGoods.getGoodsCollectNum());
                    goodsInfoNest.setGoodsEvaluateNum(esGoods.getGoodsEvaluateNum());

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
                            if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0) {
                                goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                            }
                        }

                    } else {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                    }

                    //设置企业商品的审核状态 ，以及会员的大客户价
                    if (Objects.nonNull(goodsInfoNest.getVipPrice())
                            && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                        if (goodsInfoNest.getGoodsInfoType() == 1) {
                            goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
                        } else {
                            goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                        }
                    }
                    // log.info("GoodsBaseController.list goodsInfoNest:{}",goodsInfoNest);
                    //如果商品参加了营销活动，vip价格设置为0 vipPrice不参与任何营销活动
                    if (CollectionUtils.isNotEmpty(goodsInfoNest.getMarketingLabels())) {
                        goodsInfoNest.setVipPrice(BigDecimal.ZERO);
                    }
                    //计算到手价
                    goodsInfoNest.getMarketingLabels().stream().forEach(marketingLabelVO -> {

                        //填充限购数量
                        if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
                                && marketingLabelVO.getGoodsPurchasingNumber() > 0
                                && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
                            goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
                        }

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
                    /*if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())
                            && goodsInfoNest.getGoodsStatus().equals(GoodsStatus.OK)) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }*/
                });
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

        return response;
    }

}
