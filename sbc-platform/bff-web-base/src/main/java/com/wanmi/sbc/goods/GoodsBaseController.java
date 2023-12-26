package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.configuration.MobileNacosConfig;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomListRequest;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributorLevelByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelWithDefaultByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.DistributorLevelVO;
import com.wanmi.sbc.customer.bean.vo.LiveRoomVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsCateNest;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.request.dto.EsGoodsInfoDTO;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandShelflifeResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.es.elastic.response.EsSearchResponse;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.provider.activitygoodspicture.ActivityGoodsPictureProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsImagestype.GoodsImageStypeProvider;
import com.wanmi.sbc.goods.api.provider.goodsattributekey.GoodsAttributeKeyQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsimage.GoodsImageProvider;
import com.wanmi.sbc.goods.api.provider.goodstypeconfig.GoodsTypeConfigQueryProvider;
import com.wanmi.sbc.goods.api.provider.image.ImageProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoMappingProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteProvider;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingQueryProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.relationgoodsimages.RelationGoodsImagesProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelListRequest;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoListByCustomerIdAndGoodsIdRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.goodsattributekey.GoodsAttributeKeyQueryRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigListRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteAddRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteListRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteModifyRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.AppGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdsResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.distributor.goods.DistributorGoodsInfoListByCustomerIdAndGoodsIdResponse;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.goodsattributekey.GoodsAttributeKeyListResponse;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingListByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoAllowedArea;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.*;
import com.wanmi.sbc.goods.response.GoodsInfoResponse;
import com.wanmi.sbc.goods.response.*;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsQueryProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsInfoListResponse;
import com.wanmi.sbc.live.bean.vo.LiveHaveGoodsVO;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdAndCustomerRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingMapGetByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.enums.GrouponDetailOptStatus;
import com.wanmi.sbc.marketing.bean.enums.GrouponDetailOptType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.order.api.provider.groupon.GrouponProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.groupon.GrouponDetailQueryRequest;
import com.wanmi.sbc.order.api.request.groupon.GrouponDetailWithGoodsRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseQueryRequest;
import com.wanmi.sbc.order.api.response.groupon.GrouponDetailQueryResponse;
import com.wanmi.sbc.order.api.response.groupon.GrouponDetailWithGoodsResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.ProcurementConfigResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseQueryResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.shopcart.bean.enums.ProcurementTypeEnum;
import com.wanmi.sbc.order.bean.vo.GrouponDetailVO;
import com.wanmi.sbc.order.bean.vo.GrouponDetailWithGoodsVO;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.redis.RedisZSetUtil;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.search.api.provider.SearchGoodsQueryProvider;
import com.wanmi.sbc.search.api.request.GoodsSearchRequest;
import com.wanmi.sbc.search.api.response.GoodsSearchResponse;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.setting.api.request.MiniProgramQrCodeRequest;
import com.wanmi.sbc.setting.api.request.ShareMiniProgramRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 商品Controller
 * Created by daiyitian on 17/4/12.
 */
    @RestController
    @RequestMapping("/goods")
    @Api(tags = "GoodsBaseController", description = "S2B web公用-商品信息API")
    @Slf4j
    public class GoodsBaseController {

    @Autowired
    private RetailGoodsQueryProvider retailGoodsQueryProvider;

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

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
    private ImageProvider imageProvider;

    @Autowired
    private ShopCartProvider shopCartProvider;

    @Autowired
    private RetailShopCartProvider retailShopCartProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;
    @Autowired
    private GoodsImageProvider goodsImageProvider;

    @Autowired
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Autowired
    private WechatAuthProvider wechatAuthProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private DistributorGoodsInfoQueryProvider distributorGoodsInfoQueryProvider;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private GrouponProvider grouponProvider;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisZSetUtil redisZSetUtil;

    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;

    @Autowired
    private LastGoodsWriteProvider lastGoodsWriteProvider;

    @Autowired
    private LastGoodsWriteQueryProvider lastGoodsWriteQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private LiveGoodsQueryProvider liveGoodsQueryProvider;
    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private PurchaseQueryProvider purchaseQueryProvider;

    @Autowired
    private ShopCartQueryProvider shopCartQueryProvider;

    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private CustomerAreaLimitDetailProvider customerAreaLimitDetailProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private GoodsMarketingQueryProvider goodsMarketingQueryProvider;

    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;
    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private PurchaseProvider provider;

    @Autowired
    private LiveStreamGoodsQueryProvider liveStreamGoodsQueryProvider;

    @Autowired
    private GoodsInfoMappingProvider goodsInfoMappingProvider;

    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;

    @Autowired
    private ActivityGoodsPictureProvider activityGoodsPictureProvider;


    @Autowired
    private GoodsImageStypeProvider goodsImageStypeProvider;

    @Autowired
    private RelationGoodsImagesProvider relationGoodsImagesProvider;

    @Autowired
    private GoodsAttributeKeyQueryProvider goodsAttributeKeyQueryProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;
    @Value("${retail.retailWareId}")
    private Long retailWareId;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    private ExecutorService executor = Executors.newFixedThreadPool(60);


    private static Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    private static final String threeCateGoodsCache = "threeCateGoodsCache_";

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private GoodsTypeConfigQueryProvider goodsTypeConfigQueryProvider;

    @Autowired
    private MerchantConfigGoodsQueryProvider merchantConfigGoodsQueryProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    private static final String BUYER_MARKET_DEFAULT = "BUYER_MARKET_DEFAULT_CACHE";

    @Autowired
    private MobileNacosConfig mobileNacosConfig;

    @Autowired
    private SearchGoodsQueryProvider searchGoodsQueryProvider;


    /**
     * 首页三级分类商品（缓存级） 每个分类前50
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "首页三级分类商品（缓存级） 每个分类前50")
    @RequestMapping(value = "/spus/cache")
    public BaseResponse<GoodsRecommendEsResponse> goodsListCache(@RequestBody EsGoodsInfoQueryRequest queryRequest, HttpServletRequest httpRequest) {

        List<GoodsInfoVO> goodsInfoList = new ArrayList<>();
        if (redisService.hasKey(threeCateGoodsCache.concat(queryRequest.getCateId().toString()))) {
            goodsInfoList = redisService.getList(threeCateGoodsCache.concat(queryRequest.getCateId().toString()),GoodsInfoVO.class);
        } else {
            queryRequest.setPageNum(0);
            queryRequest.setPageSize(50);
            EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, null,httpRequest);
            goodsInfoList = esGoodsLimitBrandResponse.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            Map<String, BigDecimal> goodsInfoStockMap = goodsInfoQueryProvider.findGoodsInfoStockByIds(GoodsInfoStockByGoodsInfoIdsRequest.builder()
                    .goodsInfoIds(goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsInfoStockMap();
            Map<String,String> goodsVOMap = esGoodsLimitBrandResponse.getGoodsList().stream().collect(Collectors.toMap(GoodsVO::getGoodsId,GoodsVO::getGoodsSubtitle));
            goodsInfoList.forEach(goodsInfoVO -> {
                if (StringUtils.isNotBlank(goodsVOMap.get(goodsInfoVO.getGoodsId()))) {
                    goodsInfoVO.setGoodsSubtitle(goodsVOMap.get(goodsInfoVO.getGoodsId()));
                }
                if (Objects.nonNull(goodsInfoStockMap.get(goodsInfoVO.getGoodsInfoId()))) {
                    goodsInfoVO.setStock(goodsInfoStockMap.get(goodsInfoVO.getGoodsInfoId()));

                    //重新填充商品状态
                    if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
                        // 判断是否有T，如果是1，就设置为2
                        if (goodsInfoVO.getGoodsInfoName().endsWith("T") || goodsInfoVO.getGoodsInfoName().endsWith("t")) {
                            if (goodsInfoVO.getStock().compareTo(BigDecimal.ONE) < 0) {
                                goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                            }
                        } else {
                            if (goodsInfoVO.getStock().compareTo(BigDecimal.ONE) < 0) {
                                goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                            }
                        }

                    } else {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                    }
                }
            });
            redisService.setString(threeCateGoodsCache.concat(queryRequest.getCateId().toString()),JSONObject.toJSONString(goodsInfoList,
                    SerializerFeature.DisableCircularReferenceDetect),30*60);
        }
        GoodsRecommendEsResponse response = GoodsRecommendEsResponse.builder()
                .goodsInfoVOS(goodsInfoList).build();
        return BaseResponse.success(response);
    }

    /**
     * 商品分页(ES级)
     *
     * @param liveStreamGoodsListRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "商品分页")
    @RequestMapping(value = "/retail/spus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> goods(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest , HttpServletRequest httpRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("/retail/spus前置查询");
        CustomerVO customer = commonUtil.getCustomer();
        Long wareId = commonUtil.getWareId(httpRequest);

        liveStreamGoodsListRequest.setGoodsStatus(1L);// 1代表直播间上架状态
        List<com.wanmi.sbc.live.bean.vo.LiveGoodsVO> liveGoodsVOS = Optional.ofNullable(liveStreamGoodsQueryProvider.listInfo(liveStreamGoodsListRequest))
                .map(BaseResponse::getContext)
                .map(LiveStreamGoodsInfoListResponse::getLiveGoodsVOS)
                .orElse(Lists.newArrayList());
        // TODO log
        // log.info("goods goodsListResponse:{}", JSON.toJSONString(liveGoodsVOS));
        List<String> sortList = liveGoodsVOS.stream().map(com.wanmi.sbc.live.bean.vo.LiveGoodsVO::getGoodsInfoId).collect(Collectors.toList());

        List<LiveGoodsInfoVO> liveGoodsInfoVOS = liveGoodsVOS.stream()
                .filter(x->Objects.nonNull(x))
                .map(liveGoodsVO -> {
                    return LiveGoodsInfoVO.builder()
                            .goodsInfoId(liveGoodsVO.getGoodsInfoId())
                            .goodsType(liveGoodsVO.getGoodsType())
                            .parentGoodsInfoId(liveGoodsVO.getParentGoodsInfoId())
                            .build();
                }).collect(Collectors.toList());
        // TODO log
        // log.info("goods liveGoodsInfoVOS:{}", JSON.toJSONString(liveGoodsInfoVOS));

        if(CollectionUtils.isEmpty(liveGoodsInfoVOS)){
            EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
            return BaseResponse.success(realResponse);
        }

        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setPageNum(0);
        queryRequest.setPageSize(100);// 一个直播间最多有100个商品
        queryRequest.setAddedFlag(1); // 1表示上架
        queryRequest.setDelFlag(0);   // 0表示未删除

        EsGoodsInfoQueryRequest wholeSaleQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);
        EsGoodsInfoQueryRequest retailQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);
        EsGoodsInfoQueryRequest otherQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);

        // TODO log
        // log.info("goods queryRequest:{}", JSON.toJSONString(queryRequest));

        // 对查询结果进行分组
        Map<Long, List<LiveGoodsInfoVO>> groupLiveGoodsInfoByType = liveGoodsInfoVOS.stream().collect(Collectors.groupingBy(LiveGoodsInfoVO::getGoodsType));
        // TODO log
        // log.info("goods group:{}", JSON.toJSONString(groupLiveGoodsInfoByType));

        // 查询批发数据
        List<LiveGoodsInfoVO> liveGoodsInfoForWholeSale = Optional.ofNullable(groupLiveGoodsInfoByType.get(0L)).orElse(Lists.newArrayList());
        stopWatch.stop();
        stopWatch.start("/retail/spus ES查询耗时");

        /**
         * 并行查询
         */
        CountDownLatch countDown = new CountDownLatch(3);
        AtomicReference<Map<String, String>> goodsInfoIdsForWholeSaleMap = new AtomicReference<>();// parentGoodsInfoId 与 SKU ID的映射关系
        AtomicReference<List<EsGoods>> catEsGoods = new AtomicReference<>();
        executor.execute(()->{
            try{
                if(CollectionUtils.isNotEmpty(liveGoodsInfoForWholeSale)){
                    // 切换仓库
                    List<String> liveGoodsInfoForWholeSaleIds = liveGoodsInfoVOS.stream().map(LiveGoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                    CatWareGetGoodsInfoMappingRequest catWareGetGoodsInfoMappings = CatWareGetGoodsInfoMappingRequest.builder()
                            .goodsInfoIds(liveGoodsInfoForWholeSaleIds)
                            .wareId(wareId)
                            .build();
                    List<GoodsInfoMappingVO> goodsInfoMappingVOS = Optional.ofNullable(goodsInfoMappingProvider.catWareGetGoodsInfoMapping(catWareGetGoodsInfoMappings))
                            .map(BaseResponse::getContext).orElse(Lists.newArrayList());
                    goodsInfoIdsForWholeSaleMap.set(goodsInfoMappingVOS.stream().collect(Collectors.toMap(GoodsInfoMappingVO::getParentGoodsInfoId, GoodsInfoMappingVO::getGoodsInfoId, (x, y) -> y)));
                    Collection<String> strings = Optional.ofNullable(goodsInfoIdsForWholeSaleMap).map(AtomicReference::get).map(Map::values).orElse(Lists.newArrayList());
                    if(CollectionUtils.isNotEmpty(strings)) {
                        List<String> collect = strings.stream().collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(collect)){
                            otherQueryRequest.setGoodsInfoIds(collect);
                            otherQueryRequest.setQueryGoods(false);
                            otherQueryRequest.setSortFlag(null);

                            EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = this.simpleList(otherQueryRequest, customer, httpRequest);
                            catEsGoods.set(
                                Optional.ofNullable(esGoodsLimitBrandResponse)
                                    .map(EsGoodsLimitBrandResponse::getEsGoods)
                                    .map(Page::getContent)
                                    .orElse(Lists.newArrayList())
                            );
                        }
                    }
                    // TODO log
                    // log.info("goods goodsInfoIdsForWholeSaleMap:{}", JSON.toJSONString(goodsInfoIdsForWholeSaleMap));
                }
            } finally{
                countDown.countDown();
            }
        });

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        AtomicReference<EsGoodsLimitBrandResponse> esGoodsLimitBrandResponseForWholeSale = new AtomicReference<>();
        List<String> goodsInfoIdsForWholeSale = liveGoodsInfoForWholeSale.stream().map(LiveGoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()); // 批发的商品ID
        executor.execute(()->{
            try{
                // 查询批发数据
                if(CollectionUtils.isNotEmpty(goodsInfoIdsForWholeSale)){
                    RequestContextHolder.setRequestAttributes(attributes, true);
                    wholeSaleQueryRequest.setGoodsInfoIds(goodsInfoIdsForWholeSale);
                    wholeSaleQueryRequest.setQueryGoods(false);
                    wholeSaleQueryRequest.setSortFlag(null);
                    // TODO log
                    // log.info("esGoodsLimitBrandResponse1 goodsInfoIdsForWholeSale:{}", goodsInfoIdsForWholeSale);
                    // log.info("esGoodsLimitBrandResponse1 queryRequest:{}", wholeSaleQueryRequest);


                    EsGoodsLimitBrandResponse list = this.simpleList(wholeSaleQueryRequest, customer, httpRequest);

                    // TODO log
                    // log.info("esGoodsLimitBrandResponse1:{}", list);
                    esGoodsLimitBrandResponseForWholeSale.set(list);
                }
                // TODO log
                // log.info("goods goodsInfoIdsForWholeSale:{}", JSON.toJSONString(goodsInfoIdsForWholeSale));

            } finally{
                countDown.countDown();
            }
        });

        List<LiveGoodsInfoVO> liveGoodsInfoForRetail = Optional.ofNullable(groupLiveGoodsInfoByType.get(1L)).orElse(Lists.newArrayList());;
        List<String> goodsInfoIdsForRetail = liveGoodsInfoForRetail.stream().map(LiveGoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()); // 零售的商品ID
        // TODO log
        // log.info("goodsInfoIdsForRetail:{}", JSON.toJSONString(goodsInfoIdsForRetail));
        AtomicReference<EsGoodsLimitBrandResponse> esGoodsLimitBrandResponseForRetail = new AtomicReference<>();;
        executor.execute(()->{
            try{
                // 查询散批数据
                if(CollectionUtils.isNotEmpty(liveGoodsInfoForRetail)){
                    RequestContextHolder.setRequestAttributes(attributes, true);
                    retailQueryRequest.setImageFlag(Boolean.TRUE);
                    retailQueryRequest.setGoodsInfoIds(goodsInfoIdsForRetail);
                    retailQueryRequest.setQueryGoods(false);
                    retailQueryRequest.setSortFlag(null);
                    // TODO log
                    // log.info("esGoodsLimitBrandResponse2 liveGoodsInfoForRetail:{}", liveGoodsInfoForRetail);
                    // log.info("esGoodsLimitBrandResponse2 queryRequest:{}", retailQueryRequest);
                    EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = this.listForRetail(retailQueryRequest, customer, httpRequest);
                    // TODO log
                    // log.info("esGoodsLimitBrandResponse2:{}", esGoodsLimitBrandResponse);
                    esGoodsLimitBrandResponseForRetail.set(esGoodsLimitBrandResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                countDown.countDown();
            }
        });
        try {
            countDown.await();
        } catch (InterruptedException e) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "网络繁忙请稍后再试!");
        }
        stopWatch.stop();
        stopWatch.start("/retail/spus 格式化耗时");
        // TODO log
        // log.info("esGoodsLimitBrandResponseForWholeSale:{}",JSON.toJSONString(esGoodsLimitBrandResponseForWholeSale));
        // log.info("esGoodsLimitBrandResponseForRetail:{}",JSON.toJSONString(esGoodsLimitBrandResponseForRetail));
        // log.info("goodsInfoIdsForWholeSaleMap:{}",JSON.toJSONString(goodsInfoIdsForWholeSaleMap));

        /**
         * 合并goodsinfo查询结果
         */
        List<EsGoods> contentForWholesale = Optional.ofNullable(esGoodsLimitBrandResponseForWholeSale)
                .map(AtomicReference::get)
                .map(EsGoodsLimitBrandResponse::getEsGoods).map(Page::getContent).orElse(Lists.newArrayList());
        List<EsGoods> contentForRetail = Optional.ofNullable(esGoodsLimitBrandResponseForRetail)
                .map(AtomicReference::get)
                .map(EsGoodsLimitBrandResponse::getEsGoods).map(Page::getContent).orElse(Lists.newArrayList());
        List<EsGoods> contentForCat = Optional.ofNullable(catEsGoods)
                .map(AtomicReference::get)
                .orElse(Lists.newArrayList());

        // 另外一个仓库的商品映射关系
        Map<String, EsGoods> contentForCatMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(contentForCat)){
            for (EsGoods esGoods : contentForCat) {
                List<GoodsInfoNest> goodsInfos = esGoods.getGoodsInfos();
                if(CollectionUtils.isNotEmpty(goodsInfos)){
                    continue;
                }
                GoodsInfoNest goodsInfoNest = goodsInfos.get(0);
                contentForCatMap.put(goodsInfoNest.getGoodsInfoId(), esGoods);
            }
        }

        List<EsGoods> content = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(contentForWholesale)){
            content.addAll(contentForWholesale);
        }
        if(CollectionUtils.isNotEmpty(contentForRetail)){
            content.addAll(contentForRetail);
        }
        // TODO log
        // log.info("goods content:{}", JSON.toJSONString(content));

        if(CollectionUtils.isEmpty(content)){
            EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
            return BaseResponse.success(realResponse);
        }

        // 合并GoodsVO
//        List<GoodsVO> goodsVOS = Optional.ofNullable(esGoodsLimitBrandResponseForWholeSale)
//                .map(AtomicReference::get)
//                .map(EsGoodsLimitBrandResponse::getGoodsList).orElse(Lists.newArrayList());
//        List<GoodsVO> goodsVOSForRetail = Optional.ofNullable(esGoodsLimitBrandResponseForRetail)
//                .map(AtomicReference::get)
//                .map(EsGoodsLimitBrandResponse::getGoodsList)
//                .orElse(Lists.newArrayList());
//        goodsVOS.addAll(goodsVOSForRetail);

        /**
         * 保持排序
         */
        List<EsGoods> contentOrdered = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(content)){

            // 商品ID与esGoods的映射关系
            Map<String, EsGoods> newHashMap = new HashMap<>();
            for (EsGoods esGoods : content) {
                List<GoodsInfoNest> goodsInfos = esGoods.getGoodsInfos();

                if(CollectionUtils.isEmpty(goodsInfos)){continue;}
                GoodsInfoNest goodsInfoNest = goodsInfos.get(0);

                if(Objects.isNull(goodsInfoNest)){continue;}
                String goodsInfoId = goodsInfoNest.getGoodsInfoId();

                // 如果有父级ID需要进行切换仓库
                String parentGoodsInfoId = goodsInfoNest.getParentGoodsInfoId();
                if(Objects.nonNull(parentGoodsInfoId) && Objects.nonNull(goodsInfoIdsForWholeSaleMap.get().get(parentGoodsInfoId))){
                    String goodsInfoIdFromOtherWarehouse = goodsInfoIdsForWholeSaleMap.get().get(parentGoodsInfoId);
                    if(Objects.nonNull(goodsInfoIdFromOtherWarehouse)){
                        EsGoods goodsFromEs = contentForCatMap.get(goodsInfoIdFromOtherWarehouse);
                        if(Objects.nonNull(goodsFromEs)){
                            newHashMap.put(goodsInfoId, esGoods);
                            continue;
                        }
                    }
                }
                newHashMap.put(goodsInfoId, esGoods);
            }
            // TODO log
            // log.info("goods newHashMap:{}", JSON.toJSONString(newHashMap));

            for (String goodInfoId : sortList) {
                if(Objects.isNull(goodInfoId)){
                    continue;
                }
                EsGoods esGoods = newHashMap.get(goodInfoId);
                if(Objects.isNull(esGoods)){
                    continue;
                }

                // 格式化商品的状态
                for (GoodsInfoNest goodsInfoNest : esGoods.getGoodsInfos()){

                    // 更新SKU ID
                    String parentGoodsInfoId = goodsInfoNest.getParentGoodsInfoId();
                    if(Objects.nonNull(parentGoodsInfoId) && Objects.nonNull(goodsInfoIdsForWholeSaleMap.get().get(parentGoodsInfoId))){
                        String goodsInfoId = goodsInfoIdsForWholeSaleMap.get().get(parentGoodsInfoId);
                        goodsInfoNest.setGoodsInfoId(goodsInfoId);
                    }

                    GoodsStatus goodsStatus = goodsInfoNest.getGoodsStatus();
                    if(GoodsStatus.OUT_STOCK.equals(goodsStatus)){
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                    }
                    // 上线临时补丁，保证前端不显示等货状态
                    BigDecimal stock = Optional.ofNullable(goodsInfoNest.getStock()).orElse(BigDecimal.ZERO);
                    if(BigDecimal.ONE.compareTo(stock)<0){
                        goodsInfoNest.setStock(BigDecimal.ONE);
                    }

                    List<GoodsWareStockVO> goodsWareStockVOS=goodsInfoNest.getGoodsWareStockVOS();
                    if(CollectionUtils.isNotEmpty(goodsWareStockVOS)&&Objects.nonNull(goodsInfoNest)) {
                        BigDecimal sumStock = goodsWareStockVOS.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                        goodsInfoNest.setStock(sumStock.setScale(0, BigDecimal.ROUND_DOWN));
                    }
                    if(goodsInfoIdsForRetail.size()>0){
                        // 批发/散批标识
                        Boolean isWholeSale = goodsInfoIdsForRetail.contains(goodsInfoNest.getGoodsInfoId());
                        if(Boolean.FALSE.equals(isWholeSale)){
                            goodsInfoNest.setGoodsType(0L);// 0 批发 1散批
                            GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(goodsInfoNest.getGoodsInfoId()).wareId(wareId).matchWareHouseFlag(true).parentGoodsInfoId(goodsInfoNest.getParentGoodsInfoId()).build()).getContext();
                            if(Objects.nonNull(goodsInfo)){
                                goodsInfoNest.setStock(goodsInfo.getStock());
                            }
                        } else {
                            goodsInfoNest.setPileFlag(ForcePileFlag.CLOSE);
                            goodsInfoNest.setGoodsType(1L);// 0 批发 1散批
                        }
                    }else{
                        goodsInfoNest.setGoodsType(0L);// 0 批发 1散批
                        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(goodsInfoNest.getGoodsInfoId()).wareId(wareId).matchWareHouseFlag(true).parentGoodsInfoId(goodsInfoNest.getParentGoodsInfoId()).build()).getContext();
                        if(Objects.nonNull(goodsInfo)){
                            goodsInfoNest.setStock(goodsInfo.getStock());
                        }
                    }


                }
                contentOrdered.add(esGoods);
            }
        }
        // TODO log
        // log.info("goods contentOrdered:{}", JSON.toJSONString(contentOrdered));


        /**
         * 封装响应的结果
         */
        Integer pageNum = liveStreamGoodsListRequest.getPageNum();
        Integer pageSize = liveStreamGoodsListRequest.getPageSize();
        PagedListHolder<EsGoods> pagedListHolder = new PagedListHolder<>(contentOrdered);
        pagedListHolder.setPageSize(pageSize);
        pagedListHolder.setPage(pageNum);

        PageImpl<EsGoods> esGoods = new PageImpl<>(pagedListHolder.getPageList(), PageRequest.of(pagedListHolder.getPage(), pagedListHolder.getPageSize(), Sort.unsorted()), contentOrdered.size());
        // TODO log
        log.info("goods esGoods isLast:{}", esGoods.isLast());

        List<GoodsVO> newGoodsListForWholesales = Lists.newArrayList();
        // TODO log
        // log.info("goods esGoods:{}", JSON.toJSONString(esGoods));

        // 合并GoodsVo集合中的数据
//        List<EsGoods> content1 = esGoods.getContent();
//        for (EsGoods e : content1){
//            List<GoodsInfoNest> goodsInfos = e.getGoodsInfos();
//            List<String> collect = goodsInfos.stream().map(GoodsInfoNest::getGoodsInfoId).filter(x -> Objects.nonNull(x)).collect(Collectors.toList());
//            if(CollectionUtils.isEmpty(collect)){
//                continue;
//            }
//            for (GoodsVO goodsVO : goodsVOS){
//                List<String> goodsInfoIds = goodsVO.getGoodsInfoIds();
//                if(CollectionUtils.isEmpty(goodsInfoIds)){
//                    continue;
//                }
//                for (String goodsInfo : goodsInfoIds){
//                    if(!collect.contains(goodsInfo)){
//                        continue;
//                    }
//                }
//                newGoodsListForWholesales.add(goodsVO);
//            }
//        }
        // TODO log
        // log.info("goods newGoodsListForWholesales:{}", JSON.toJSONString(newGoodsListForWholesales));

        EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = EsGoodsLimitBrandResponse.builder()
                .esGoods(esGoods)
                .goodsList(newGoodsListForWholesales)
                .build();

        /**
         * 格式化
         */
        EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();
        //没有搜索出数据
        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
            return BaseResponse.success(realResponse);
        }

        BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        return BaseResponse.success(realResponse);
    }

    /**
     * 商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "商品分页")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> goodslist(@RequestBody EsGoodsInfoQueryRequest queryRequest, HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        Long wareId = commonUtil.getWareId(httpRequest);
        if (Objects.equals(1, mobileNacosConfig.getSearchSpuForSearchSearchOpen())) {
            // 走最新的搜索服务
            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
            final GoodsSearchRequest goodsSearchRequest = new GoodsSearchRequest();
            goodsSearchRequest.setCustomerVO(customer);
            goodsSearchRequest.setRequestES(queryRequest);
            goodsSearchRequest.setFinalDeliveryAddress(finalDeliveryAddress);
            goodsSearchRequest.setChannelType(commonUtil.getDistributeChannel().getChannelType());
            goodsSearchRequest.setPurchaseInviteeId(commonUtil.getPurchaseInviteeId());
            final GoodsSearchResponse goodsSearchResponse = searchGoodsQueryProvider.searchGoods(goodsSearchRequest).getContext();
            return BaseResponse.success(goodsSearchResponse.getResponseES());
        }
        // 默认排序
//        queryRequest.setSortFlag(10);
        wrapMarketIdQuery(queryRequest);
        final EsGoodsInfoQueryRequest copyRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);
        queryRequest.setWareId(wareId);
        wrapKeyWords(queryRequest);
        EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customer,httpRequest);
        if (CollectionUtils.isEmpty(esGoodsLimitBrandResponse.getEsGoods().getContent()) && StringUtils.isNotBlank(queryRequest.getKeywords())) {
            copyRequest.setKeywords(null);
            copyRequest.setStoreName(queryRequest.getKeywords());
            esGoodsLimitBrandResponse = list(copyRequest, customer,httpRequest);
        }
        // TODO log
        log.info("GoodsBaseController goodslist:{}",Optional.ofNullable(queryRequest).map(EsGoodsInfoQueryRequest::getKeywords).orElse(""));
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
        //增加保质期
        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();
        //没有搜索出数据
        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
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
        Map<String, GoodsImageStypeVO> collect = context1.stream()
                .collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
        Map<String, GoodsImageStypeVO> finalCollect = collect;


        for (EsGoods listEsGood : listEsGoods) {
            List<GoodsInfoNest> goodsInfos = listEsGood.getGoodsInfos();
            List<String> goodsInfoId = goodsInfos.stream().map(y -> y.getGoodsInfoId()).collect(Collectors.toList());
            goodsInfos.forEach(var->{
                if (null == var.getGoodsSubtitleNew()){
                    var.setGoodsSubtitleNew(goodsVOMap.get(var.getGoodsId()));
                }
                //填充活动图片
                if (CollectionUtils.isNotEmpty(var.getMarketingLabels())){
                    GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(var.getGoodsId());
                    if (Objects.nonNull(goodsImageStypeVO)){
                        var.setGoodsInfoImg(goodsImageStypeVO.getArtwork_url());
                    }
                }
                // 填充店铺名称
                if(StringUtils.isBlank(var.getStoreName())){
                    var.setStoreName(storeNameMap.get(var.getStoreId()));
                }
            });
            goodsInfoIds.addAll(goodsInfoId);
        }
        BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);

        /*realResponse.setBrands(esGoodsLimitBrandResponse.getBrands());
        realResponse.setCateList(esGoodsLimitBrandResponse.getCateList());
        realResponse.setCateName(esGoodsLimitBrandResponse.getCateName());
        realResponse.setGoodsIntervalPrices(esGoodsLimitBrandResponse.getGoodsIntervalPrices());
        realResponse.setGoodsList(esGoodsLimitBrandResponse.getGoodsList());
        realResponse.setGoodsSpecDetails(esGoodsLimitBrandResponse.getGoodsSpecDetails());
        realResponse.setGoodsSpecs(esGoodsLimitBrandResponse.getGoodsSpecs());

        Page<EsGoods> esGoods = esGoodsLimitBrandResponse.getEsGoods();

        log.info("sdfghhjkl;'---------------->"+JSONObject.toJSONString(esGoods.getContent()));
        List<EsGoodsShelflife> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(esGoods.getContent())){
            esGoods.getContent().forEach(var ->{
                log.info("sdfghhjkl;'----------------var >"+JSONObject.toJSONString(var));
                EsGoodsShelflife esGoodsShelflife = new EsGoodsShelflife();
                BeanUtils.copyProperties(var, esGoodsShelflife);
                esGoodsShelflife.setGoodsSubtitleNew(var.getGoodsSubtitleNew());
                esGoodsShelflife.setGoodsSubtitle(var.getGoodsSubtitle());

                log.info("sdfghhjkl;'----------------esGoodsShelflife >"+JSONObject.toJSONString(esGoodsShelflife));
                list.add(esGoodsShelflife);
            });
        }
        log.info("sdfghhjkl;'----------------list >"+JSONObject.toJSONString(list));
        Page<EsGoodsShelflife> page = new PageImpl<>(list,  PageRequest.of(queryRequest.getPageNum(), queryRequest.getPageSize()), esGoods.getTotalPages());

        realResponse.setEsGoods(page);*/
        return BaseResponse.success(realResponse);
    }

    private void wrapMarketIdQuery(EsGoodsInfoQueryRequest queryRequest) {
        if (!Objects.equals(mobileNacosConfig.getGoodsSearchByMarketFlag(), 1)){
            return;
        }
        Long marketId = queryRequest.getMarketId();
        if (null == marketId) {
            String userId = commonUtil.getOperator().getUserId();
            if (null != userId) {
                String cacheMarketId = redisService.hget(BUYER_MARKET_DEFAULT, userId);
                if (StringUtils.isNotBlank(cacheMarketId)) {
                    marketId = Long.valueOf(cacheMarketId);
                }
            }
        }
        if (null == marketId) {
            return;
        }
        List<CompanyMallContractRelationVO> contractSuppliers = listMarketByMarketId(marketId);
        if (CollectionUtils.isNotEmpty(contractSuppliers)) {
            final List<Long> marketStoreIds = contractSuppliers.stream().map(CompanyMallContractRelationVO::getStoreId).distinct().collect(Collectors.toList());
            for (int i = 0; i < mobileNacosConfig.getGoodsSearchByMarketFlagNum(); i++) {
                marketStoreIds.add(10000000000L+i);
            }
            queryRequest.setStoreIds(marketStoreIds);
        }
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

    private void wrapKeyWords(EsGoodsInfoQueryRequest queryRequest) {
        if (Objects.equals(queryRequest.getKeywords(), "找新品")) {
            final BaseResponse<List<String>> listBaseResponse = goodsQueryProvider.listRecentAddedNewGoods();
            if (CollectionUtils.isNotEmpty(listBaseResponse.getContext())) {
                queryRequest.setGoodsIds(listBaseResponse.getContext());
                queryRequest.setKeywords(null);
            }
        } else if (Objects.equals(queryRequest.getKeywords(), "找促销品")) {
            final BaseResponse<List<String>> listBaseResponse = marketingQueryProvider.listEffectiveStoreGoodsInfoIds();
            if (CollectionUtils.isNotEmpty(listBaseResponse.getContext())) {
                queryRequest.setGoodsInfoIds(listBaseResponse.getContext());
                queryRequest.setKeywords(null);
            }
        }
    }

    private Map<Long, String> mapStoreName(Page<EsGoods> listEsGoods) {
        try {
            List<Long> storeIds = listEsGoods.stream().flatMap(v ->
                    v.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId)).distinct().collect(Collectors.toList());
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

    /**
     * 商品分页(ES级)
     *
     * @param queryRequest 搜索条件【商品搜索】
     * @return 返回分页结果
     */
    @ApiOperation(value = "商品分页")
    @RequestMapping(value = "/spus/for-store", method = RequestMethod.POST)
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> goodslistForStore(@RequestBody EsGoodsInfoQueryRequest queryRequest, HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        Long wareId = commonUtil.getWareId(httpRequest);
//        queryRequest.setWareId(wareId);
        EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customer,httpRequest);
        // TODO log
        log.info("GoodsBaseController goodslist:{}",Optional.ofNullable(queryRequest).map(EsGoodsInfoQueryRequest::getKeywords).orElse(""));
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
        //增加保质期
        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();
        //没有搜索出数据
        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
            return BaseResponse.success(realResponse);
        }

        List<GoodsVO> goodsList = esGoodsLimitBrandResponse.getGoodsList();
        Map<String, String> goodsVOMap = goodsList.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew, (key1, key2) -> key2));

        List<String> goodsInfoIds = Lists.newArrayList();
        List<String> goodIds = listEsGoods.stream().flatMap(v -> {
            return v.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsId);
        }).collect(Collectors.toList());

        List<GoodsImageStypeVO> context1 = goodsImageStypeProvider.getHcImageByGoodsIds(goodIds).getContext();
        Map<String, GoodsImageStypeVO> collect = context1.stream()
                .collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
        Map<String, GoodsImageStypeVO> finalCollect = collect;


        for (EsGoods listEsGood : listEsGoods) {
            List<GoodsInfoNest> goodsInfos = listEsGood.getGoodsInfos();
            List<String> goodsInfoId = goodsInfos.stream().map(y -> y.getGoodsInfoId()).collect(Collectors.toList());
            goodsInfos.forEach(var->{
                if (null == var.getGoodsSubtitleNew()){
                var.setGoodsSubtitleNew(goodsVOMap.get(var.getGoodsId()));
                }
                //填充活动图片
                if (CollectionUtils.isNotEmpty(var.getMarketingLabels())){
                    GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(var.getGoodsId());
                    if (Objects.nonNull(goodsImageStypeVO)){
                        var.setGoodsInfoImg(goodsImageStypeVO.getArtwork_url());
                    }
                }
            });
            goodsInfoIds.addAll(goodsInfoId);
        }
        BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);
        return BaseResponse.success(realResponse);
    }


    /**
     * 分类商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "分类商品分页")
    @RequestMapping(value = "/brandSeqBySpus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsResponse> cateBrandSortGoodslist(@RequestBody GoodsByParentCateIdQueryRequest queryRequest, HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        EsGoodsResponse esGoodsLimitBrandResponse = listByBrandSeq(queryRequest, customer,httpRequest);
        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();

        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
            return BaseResponse.success(EsGoodsResponse.builder().esGoods(new PageImpl<>(Lists.newArrayList())).build());
        }
        //活动图片填充
        List<EsGoods> content = esGoodsLimitBrandResponse.getEsGoods().getContent();
        List<String> goodsIds = content.stream().flatMap(v -> {
            return v.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsId);
        }).collect(Collectors.toList());
        List<GoodsImageStypeVO> context1 = goodsImageStypeProvider.getHcImageByGoodsIds(goodsIds).getContext();
        Map<String, GoodsImageStypeVO> collect = context1.stream()
                .collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
        Map<String, GoodsImageStypeVO> finalCollect = collect;
        esGoodsLimitBrandResponse.getEsGoods().getContent().forEach(v->{

                GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(v.getId());
                if (Objects.nonNull(goodsImageStypeVO)){
                    v.getGoodsInfos().forEach(q->{
                        if (CollectionUtils.isNotEmpty(q.getMarketingLabels())){
                            q.setGoodsInfoImg(goodsImageStypeVO.getArtwork_url());
                        }
                    });
                }


        });
        return BaseResponse.success(esGoodsLimitBrandResponse);
    }

    /**
     * 分类商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "分类商品分页【商家模块】")
    @RequestMapping(value = "/brandSeqBySpus/for-store", method = RequestMethod.POST)
    public BaseResponse<EsGoodsResponse> cateBrandSortGoodslistForStore(@RequestBody GoodsByParentCateIdQueryRequest queryRequest, HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        if (CollectionUtils.isEmpty(queryRequest.getStoreCateIds()) && queryRequest.getStoreCatId() != null){
            queryRequest.setStoreCateIds(Lists.newArrayList(queryRequest.getStoreCatId()));
        }
        
        // sortFlag设置为13
        queryRequest.setSortFlag(13);
        EsGoodsResponse esGoodsLimitBrandResponse = listByBrandSeq(queryRequest, customer,httpRequest);
        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();

        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
            return BaseResponse.success(EsGoodsResponse.builder().esGoods(new PageImpl<>(Lists.newArrayList())).build());
        }
        //活动图片填充
        List<EsGoods> content = esGoodsLimitBrandResponse.getEsGoods().getContent();
        List<String> goodsIds = content.stream().flatMap(v -> {
            return v.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsId);
        }).collect(Collectors.toList());
        List<GoodsImageStypeVO> context1 = goodsImageStypeProvider.getHcImageByGoodsIds(goodsIds).getContext();
        Map<String, GoodsImageStypeVO> collect = context1.stream()
                .collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
        Map<String, GoodsImageStypeVO> finalCollect = collect;
        esGoodsLimitBrandResponse.getEsGoods().getContent().forEach(v->{

            GoodsImageStypeVO goodsImageStypeVO = finalCollect.get(v.getId());
            if (Objects.nonNull(goodsImageStypeVO)){
                v.getGoodsInfos().forEach(q->{
                    if (CollectionUtils.isNotEmpty(q.getMarketingLabels())){
                        q.setGoodsInfoImg(goodsImageStypeVO.getArtwork_url());
                    }
                });
            }


        });
        return BaseResponse.success(esGoodsLimitBrandResponse);
    }

    /**
     * 商品分页(ES级)[新]
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "商品分页")
    @RequestMapping(value = "/getGoodsByCateId", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoResponse> getGoodsByCateId(@RequestBody GoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {

        GoodsInfoResponse goodsByCateId = getGoodsByCateId(queryRequest, commonUtil.getCustomer(),httpRequest);

        return BaseResponse.success(goodsByCateId);
    }


    /**
     * 未登录时,查询商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "未登录时,查询商品分页")
    @RequestMapping(value = "/spuListFront", method = RequestMethod.POST)
    public BaseResponse<EsGoodsLimitBrandResponse> spuListFront(@RequestBody EsGoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {
        EsGoodsLimitBrandResponse response = list(queryRequest, null,httpRequest);
        if (CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            Map<String, List<EsGoodsInfoDTO>> buyCountMap =
                    queryRequest.getEsGoodsInfoDTOList().stream()
                            .collect(Collectors.groupingBy(EsGoodsInfoDTO::getGoodsInfoId));

            if (MapUtils.isEmpty(buyCountMap)) {
                return BaseResponse.success(response);
            }
            response.getEsGoods().getContent().forEach(goods -> {
                goods.getGoodsInfos().stream()
                        .filter(goodsInfo -> Objects.nonNull(goodsInfo) && buyCountMap.containsKey(goodsInfo
                                .getGoodsInfoId()))
                        .forEach(goodsInfo -> goodsInfo.setBuyCount(
                                buyCountMap.get(goodsInfo.getGoodsInfoId()).get(0).getGoodsNum()));
            });
        }
        return BaseResponse.success(response);
    }

    /**
     * Spu商品详情
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询Spu商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/spu/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> detail(@PathVariable String skuId, @RequestParam Long wareId, @RequestParam(required = false) Boolean matchWareHouseFlag,HttpServletRequest httpRequest) {

        CustomerVO customer = commonUtil.getCustomer();
        GoodsViewByIdResponse detail = detail(GoodsWareStockRequest.builder()
                .skuId(skuId).wareId(wareId)
                .matchWareHouseFlag(matchWareHouseFlag)
                .build(), customer,httpRequest);

        //记录最后一次用户浏览商品记录
        if (Objects.nonNull(customer)) {
            String customerId = customer.getCustomerId();
            List<LastGoodsWriteVO> lastGoodsWriteVOList =
                    lastGoodsWriteQueryProvider.list(LastGoodsWriteListRequest.builder().customerId(customerId).build()).getContext().getLastGoodsWriteVOList();
            GoodsInfoVO goodsInfoVO = detail.getGoodsInfos().get(0);
            if (CollectionUtils.isNotEmpty(lastGoodsWriteVOList)) {
                LastGoodsWriteVO lastGoodsWriteVO = lastGoodsWriteVOList.stream().sorted(Comparator.comparing(LastGoodsWriteVO::getCreateTime).reversed()).findFirst().get();
                lastGoodsWriteProvider.modify(LastGoodsWriteModifyRequest.builder().goodsInfoId(goodsInfoVO.getGoodsInfoId()).brandId(goodsInfoVO.getBrandId())
                        .cateId(goodsInfoVO.getCateId()).customerId(customerId).updateTime(LocalDateTime.now()).createTime(lastGoodsWriteVO.getCreateTime()).id(lastGoodsWriteVO.getId()).build());
            } else {
                LastGoodsWriteAddRequest addRequest =
                        LastGoodsWriteAddRequest.builder().brandId(goodsInfoVO.getBrandId()).cateId(goodsInfoVO.getCateId()).
                                customerId(customerId).createTime(LocalDateTime.now()).goodsInfoId(goodsInfoVO.getGoodsInfoId()).build();
                lastGoodsWriteProvider.add(addRequest);
            }
        }
        //限购
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        detail.getGoodsInfos().forEach(goodsInfoVO -> {
            if(Objects.nonNull(goodsInfoVO)) {
                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())
                        && goodsInfoVO.getGoodsStatus().equals(GoodsStatus.OK)) {
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                    }
                }
                //加入商品属性（sku）
                BaseResponse<GoodsAttributeKeyListResponse> skuList = goodsAttributeKeyQueryProvider.getList(GoodsAttributeKeyQueryRequest.builder().goodsInfoId(goodsInfoVO.getGoodsInfoId()).build());
                if (skuList.getContext()!=null && CollectionUtils.isNotEmpty(skuList.getContext().getAttributeKeyVOS())){
                    goodsInfoVO.setGoodsAttributeKeys(skuList.getContext().getAttributeKeyVOS());
                }else {
                    goodsInfoVO.setGoodsAttributeKeys(null);
                }

            }

        });
        //erp 封装前端需要的唯一值
        detail.getGoods().setErpNo(detail.getGoodsInfos().get(Constants.no).getErpGoodsInfoNo());
        detail.getGoods().setIsScatteredQuantitative(detail.getGoodsInfos().get(Constants.no).getIsScatteredQuantitative());
        detail.getGoods().setShelflife(detail.getGoodsInfos().get(Constants.no).getShelflife());
        // List<String> skuIds=new ArrayList<>();
        // skuIds.add(skuId);
        // esGoodsModifyInventoryService.modifyInventory(skuIds);
        return BaseResponse.success(detail);
    }




    /**
     * Spu商品详情
     *
     * @return 拆箱查看详情
     */
    @ApiOperation(value = "拆箱查询Spu商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/devanning/spu/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> devanningDetail(@PathVariable String skuId,HttpServletRequest httpRequest) {

        CustomerVO customer = commonUtil.getCustomer();
        GoodsViewByIdResponse detail = detail(GoodsWareStockRequest.builder()
                .skuId(skuId).wareId(commonUtil.getWareId(HttpUtil.getRequest()))
                .matchWareHouseFlag(true)
                .build(), customer,httpRequest);
//        GoodsViewByIdResponse detail = detail(skuId,commonUtil.getWareId(HttpUtil.getRequest()), Boolean matchWareHouseFlag, String parentSkuId);
        //记录最后一次用户浏览商品记录
        GoodsInfoVO goodsInfoVO = detail.getGoodsInfos().stream().filter(w -> skuId.equals(w.getGoodsInfoId())).findFirst().orElse(null);
        if (Objects.isNull(goodsInfoVO)){
            goodsInfoVO = detail.getGoodsInfos().get(0);
            detail.getGoodsInfos().forEach(info -> {
                convertDateDesc(info);//附加日期描述
            });
        }
        if (Objects.nonNull(customer)) {
            String customerId = customer.getCustomerId();
            List<LastGoodsWriteVO> lastGoodsWriteVOList =
                    lastGoodsWriteQueryProvider.list(LastGoodsWriteListRequest.builder().customerId(customerId).build()).getContext().getLastGoodsWriteVOList();

            if (CollectionUtils.isNotEmpty(lastGoodsWriteVOList)) {
                LastGoodsWriteVO lastGoodsWriteVO = lastGoodsWriteVOList.stream().sorted(Comparator.comparing(LastGoodsWriteVO::getCreateTime).reversed()).findFirst().get();
                lastGoodsWriteProvider.modify(LastGoodsWriteModifyRequest.builder().goodsInfoId(goodsInfoVO.getGoodsInfoId()).brandId(goodsInfoVO.getBrandId())
                        .cateId(goodsInfoVO.getCateId()).customerId(customerId).updateTime(LocalDateTime.now()).createTime(lastGoodsWriteVO.getCreateTime()).id(lastGoodsWriteVO.getId()).build());
            } else {
                LastGoodsWriteAddRequest addRequest =
                        LastGoodsWriteAddRequest.builder().brandId(goodsInfoVO.getBrandId()).cateId(goodsInfoVO.getCateId()).
                                customerId(customerId).createTime(LocalDateTime.now()).goodsInfoId(goodsInfoVO.getGoodsInfoId()).build();
                lastGoodsWriteProvider.add(addRequest);
            }
        }
        //填充拆箱商品多种规格
        List<String> goodsinfoids =new LinkedList<>();
        goodsinfoids.add(goodsInfoVO.getGoodsInfoId());
        detail.setDevanningGoodsInfoVO(devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(goodsinfoids).build()).getContext().getDevanningGoodsInfoVOS()
                .stream().sorted(Comparator.comparing(DevanningGoodsInfoVO::getDivisorFlag,Comparator.reverseOrder())).collect(Collectors.toList()));
        //限购
        final AtomicReference<BigDecimal>[] quyunum = new AtomicReference[]{null}; //个人商品区域已经购买数量
        //加入商品属性（spu）
        BaseResponse<GoodsAttributeKeyListResponse> spuList = goodsAttributeKeyQueryProvider.getList(GoodsAttributeKeyQueryRequest.builder().goodsId(goodsInfoVO.getGoodsId()).build());
        if (spuList.getContext()!=null && CollectionUtils.isNotEmpty(spuList.getContext().getAttributeKeyVOS())){
            detail.setGoodsAttribute(spuList.getContext().getAttributeKeyVOS());
        }else{
            detail.setGoodsAttribute(null);
        }

        //加入商品属性（sku）
        BaseResponse<GoodsAttributeKeyListResponse> skuList = goodsAttributeKeyQueryProvider.getList(GoodsAttributeKeyQueryRequest.builder().goodsInfoId(goodsInfoVO.getGoodsInfoId()).build());
       if (spuList.getContext()!=null && CollectionUtils.isNotEmpty(skuList.getContext().getAttributeKeyVOS())){
           goodsInfoVO.setGoodsInfoAttribute(skuList.getContext().getAttributeKeyVOS());
       }else {
           goodsInfoVO.setGoodsInfoAttribute(null);
       }
       //兼容图片
        if (null!=goodsInfoVO && Objects.isNull(goodsInfoVO.getGoodsInfoImg())){
            goodsInfoVO.setGoodsInfoImg(detail.getGoods().getGoodsImg());
        }
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        Map<String,String> unitMap = new HashMap<>();
        detail.getDevanningGoodsInfoVO().forEach(goodsInfoVOs -> {
            if(Objects.nonNull(goodsInfoVOs)) {
                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVOs.getAllowedPurchaseArea())) {
                    List<Long> allowedPurchaseAreaList = Arrays.stream(goodsInfoVOs.getAllowedPurchaseArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    List<Long> singleOrderAssignAreaList = new ArrayList<>();
                    if (StringUtils.isNotBlank(goodsInfoVOs.getSingleOrderAssignArea())){
                        singleOrderAssignAreaList = Arrays.stream(goodsInfoVOs.getSingleOrderAssignArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    }
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                        goodsInfoVOs.setGoodsStatus(GoodsStatus.QUOTA);
                    }else if (CollectionUtils.isNotEmpty(singleOrderAssignAreaList)&&allowedPurchaseAreaList.contains(singleOrderAssignAreaList)){
                        //在限购区域内获取商品限购数量 以及客户以及购买的数量
                        detail.setAllNum(goodsInfoVOs.getSingleOrderPurchaseNum());
                        List<CustomerAreaLimitDetailVO> detailVOS = customerAreaLimitDetailProvider.listByCids(CustomerAreaLimitDetailRequest.builder().customerId(customer.getCustomerId())
                                .goodsInfoId(skuId).regionIds(allowedPurchaseAreaList).build()).getContext().getDetailVOS();
                        if (CollectionUtils.isNotEmpty(detailVOS)){
                            if (Objects.isNull(quyunum [0])){
                                quyunum [0] =new AtomicReference<>(BigDecimal.ZERO);
                            }
                            //过滤出已经退货或者已经取消的订单
                            List<String> collect = detailVOS.stream().map(CustomerAreaLimitDetailVO::getTradeId).collect(Collectors.toList());
                            //传入集合中生效的订单
                            List<TradeVO> context = tradeQueryProvider.getOrderByIds(collect).getContext();
                            List<String> collect1 = context.stream().map(TradeVO::getId).collect(Collectors.toList());
                            detailVOS.forEach(v->{
                                if (collect1.contains(v.getTradeId())){
                                    quyunum[0].set(quyunum[0].get().add(v.getNum()) );
                                }
                            });
                        }
                    }
                }
                //判断为空情况加入goods 副标题
                if (null!=goodsInfoVOs && StringUtils.isEmpty(goodsInfoVOs.getGoodsInfoSubtitle())){
                    goodsInfoVOs.setGoodsInfoSubtitle(detail.getGoods().getGoodsSubtitle());
                    goodsInfoVOs.setGoodsSubtitleNew(detail.getGoods().getGoodsSubtitleNew());
                }

                //附加日期描述
                Long shelflife = goodsInfoVOs.getShelflife();
                if (null != goodsInfoVOs.getDateUnit()){
                    unitMap.put(goodsInfoVOs.getGoodsInfoId(),goodsInfoVOs.getDateUnit());
                    DateEnum dateEnum = DateEnum.getDateEnum(goodsInfoVOs.getDateUnit());
                    if(shelflife != null && dateEnum != null){
                        Long convertShelflife = Objects.isNull(goodsInfoVOs.getDateUnit()) ? goodsInfoVOs.getShelflife():goodsInfoVOs.getShelflife() * dateEnum.getValue();
                        goodsInfoVOs.setShelflife(convertShelflife);
                        goodsInfoVOs.setShelflifeDesc(shelflife.toString().concat(dateEnum.getDesc()));
                    }
                }else {
                    if(goodsInfoVOs.getShelflife() != null){
                        goodsInfoVOs.setShelflifeDesc(goodsInfoVOs.getShelflife() +"天");
                    }
                }
            }
        });
        final AtomicReference<BigDecimal>[] marketingunum = new AtomicReference[]{null}; //个人某个营销某个商品的购买数量
        final AtomicReference<Long>[] marketingAlluNum = new AtomicReference[]{null}; //总营销某个商品的限购数量
        final AtomicReference<Long>[] marketingAllUseruNum = new AtomicReference[]{null}; //总营销总个人能买限购数量
        final AtomicReference<Long>[] markingId = new AtomicReference[]{null};
        detail.getGoodsInfos().forEach(v->{
            if(Objects.nonNull(v) && Objects.nonNull(customer)) {
                GoodsMarketingListByCustomerIdResponse context = goodsMarketingQueryProvider.listByCustomerIdAndGoodsInfoId(GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest.builder().customerId(customer.getCustomerId())
                        .goodsInfoIds(v.getGoodsInfoId()).build()).getContext();
                if (CollectionUtils.isNotEmpty(context.getGoodsMarketings())){
                    if (Objects.isNull(marketingunum [0])){
                        marketingunum [0] =new AtomicReference<>(BigDecimal.ZERO);
                    }
                    if (Objects.isNull(marketingAlluNum [0])){
                        marketingAlluNum [0] =new AtomicReference<>(0l);
                    }
                    if (Objects.isNull(marketingAllUseruNum [0])){
                        marketingAllUseruNum [0] =new AtomicReference<>(0l);
                    }
                    if (Objects.isNull(markingId [0])){
                        markingId [0] =new AtomicReference<>(0l);
                    }
                    GoodsMarketingVO goodsMarketingVO = context.getGoodsMarketings().stream().findAny().get();
                    //查询营销总限购和单商品限购
                    MarketingScopeByMarketingIdRequest request = new MarketingScopeByMarketingIdRequest();
                    request.setMarketingId(goodsMarketingVO.getMarketingId());
                    request.setSkuId(v.getGoodsInfoId());
                    List<MarketingScopeVO> marketingScopeVOList = marketingScopeQueryProvider.listByMarketingIdAndSkuIdAndCache(request).getContext().getMarketingScopeVOList();
                    if (CollectionUtils.isNotEmpty(marketingScopeVOList)){
                        MarketingScopeVO marketingScopeVO = marketingScopeVOList.stream().findFirst().get();
                        marketingAlluNum[0].set(marketingScopeVO.getPurchaseNum());
                        marketingAllUseruNum[0].set(marketingScopeVO.getPerUserPurchaseNum());
                        markingId[0].set(marketingScopeVO.getMarketingId());
                        //通过用户id查询当前商品的营销购买数量
                        Map<String,Object> req = new LinkedHashMap<>();
                        req.put("customerId",customer.getCustomerId());
                        req.put("marketingId",goodsMarketingVO.getMarketingId());
                        req.put("goodsInfoId",goodsMarketingVO.getGoodsInfoId());
                        List<MarketingPurchaseLimitVO> context1 = marketingPurchaseLimitProvider.queryListByParm(req).getContext();
                        if (CollectionUtils.isNotEmpty(context1)){
                            List<String> collect = context1.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
                            //传入集合中生效的订单
                            List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
                            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
                            context1.forEach(q->{
                                if (collect1.contains(q.getTradeId())){
                                    marketingunum[0].set(marketingunum[0].get().add(q.getNum()));
                                }
                            });
                        }
                    }

                }
            }
        });

        //设置上商品参与囤货活动(多商家囤货活动) @jkp
        List<String> goodsInfoIds = detail.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        List<PileActivityVO> context = pileActivityProvider.getStartPileActivity().getContext();
        if (CollectionUtils.isNotEmpty(goodsInfoIds) && CollectionUtils.isNotEmpty(context)) {
            for(PileActivityVO pileActivityVO : context) {

                BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods =
                        pileActivityProvider.getStartPileActivityPileActivityGoods(
                                PileActivityPileActivityGoodsRequest.builder()
                                        .pileActivityId(pileActivityVO.getActivityId()).goodsInfoIds(goodsInfoIds).build());
                BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;


                if (CollectionUtils.isNotEmpty(pileActivityReturn.getContext())) {

                    Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g, (a, b) -> a));

                    detail.getGoodsInfos().forEach(goodsInfo -> {
                        if (Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))) {
                            goodsInfo.setVirtualStock(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock().longValue());
                            if (pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()) {
                                goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                            } else {
                                goodsInfo.setPileFlag(ForcePileFlag.PILE);
                            }
                        } else {
                            goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                        }
                    });
                    break;
                }
            }

        }

        //活动图片处理
//        Map<String, ActivityGoodsResponse> collect = new HashMap<>();
//        if(CollectionUtils.isNotEmpty(goodsInfoIds)){
//            BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(goodsInfoIds).build());
//            List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
//            if(CollectionUtils.isNotEmpty(activityGoodsResponse)){
//                collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsId, g -> g,(a,b)->a));
//            }
//        }
        List<String> goodsids = detail.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsId).collect(Collectors.toList());
        List<GoodsImageStypeVO> context1 = goodsImageStypeProvider.getHcImageByGoodsIds(goodsids).getContext();
        Map<String, GoodsImageStypeVO> collect = context1.stream()
                .collect(Collectors.toMap(GoodsImageStypeVO::getGoodsId, Function.identity(), (a, b) -> a));
        Map<String, GoodsImageStypeVO> finalCollect = collect;
        //关联实体
        RelationGoodsImagesVO context2 = relationGoodsImagesProvider.getRelationByGoodsId(goodsids.stream().findFirst().get()).getContext();
        AtomicBoolean falg = new AtomicBoolean(false);
        //设置商品是否在直播
        detail.getGoodsInfos().forEach(goodsInfo -> {
            if (goodsInfo.getDateUnit() == null){
                goodsInfo.setDateUnit(unitMap.get(goodsInfo.getGoodsInfoId()));
            }
            convertDateDesc(goodsInfo);//附加日期描述
            LiveHaveGoodsVO liveHaveGoodsVO=liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
            if(liveHaveGoodsVO.getIsHaveLive()==1){
                goodsInfo.setIsHaveLive(1);
                goodsInfo.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
                goodsInfo.setLiveId(liveHaveGoodsVO.getLiveId());
            }else{
                goodsInfo.setIsHaveLive(0);
            }
            log.info("goodsInfo.getMarketingLabels()------->"+CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels()));
            log.info("goodsInfo.getMarketingLabels()------->"+JSONObject.toJSONString(goodsInfo.getMarketingLabels()));
            //设置参与活动商品的活动图片
            if(CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())){
                falg.set(true);
            }
        });
        if(falg.get()){
            if (Objects.isNull(context2)){

//                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "无关联信息");
            }else {
                AtomicBoolean falg1 = new AtomicBoolean(true);
                if(Objects.nonNull(finalCollect.get(detail.getGoods().getGoodsId()))){
                    for (int i =0 ;i<detail.getImages().size();i++){
                        GoodsImageVO v = detail.getImages().get(i);
                        if (v.getImageId().compareTo(context2.getImageId())==0){
                            v.setArtworkUrl(finalCollect.get(detail.getGoods().getGoodsId()).getArtwork_url());
                            //并且放到第一位
                            if (i!=0){
                                falg1.set(false);
                            }
                        }
                    }
                }
                if (!falg1.get()){
                    //重排
                    Map<Long, GoodsImageVO> collect1 = detail.getImages().stream().collect(Collectors.toMap(GoodsImageVO::getImageId, Function.identity()));
                    //第一个放入
                    List<GoodsImageVO> newimages = new ArrayList<>();
                    newimages.add(collect1.get(context2.getImageId()));
                    //原数据删除
                    Iterator<GoodsImageVO> iterator = detail.getImages().iterator();
                    while (iterator.hasNext()){
                        GoodsImageVO next = iterator.next();
                        if (next.getImageId().compareTo(context2.getImageId())==0){
                            iterator.remove();
                        }
                    }
                    //重新加
                    newimages.addAll(detail.getImages());
                    detail.setImages(newimages);
                }
            }

        }

        detail.setAlreadyNum(Objects.isNull(quyunum[0])?null:quyunum[0].get());
        detail.setAlreadyMarketingNum(Objects.isNull(marketingunum[0])?null: marketingunum[0].get());
        detail.setAllMarketingNum(Objects.isNull(marketingAlluNum[0])?null: marketingAlluNum[0].get());
        detail.setAllMarkeingUserNum(Objects.isNull(marketingAllUseruNum[0])?null: marketingAllUseruNum[0].get());
        detail.setMarktingId(Objects.isNull(markingId[0])?null: markingId[0].get());
        detail.getGoodsInfos().forEach(f -> {
            if (!Objects.equals(f.getCompanyType(),CompanyType.SUPPLIER)){
                if (StringUtils.isNotBlank(f.getGoodsInfoBatchNo())) f.setGoodDateDesc(f.getGoodsInfoBatchNo());
            }else {
                f.setGoodsInfoBatchNo("见外装箱");
            }
        });
        // List<String> skuIds=new ArrayList<>();
        // skuIds.add(skuId);
        // esGoodsModifyInventoryService.modifyInventory(skuIds);
        return BaseResponse.success(detail);
    }


    /**
     * Spu查询对应的规格
     *
     * @return 拆箱查看详情
     */
    @ApiOperation(value = "Spu查询对应的规格")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "spuId", value = "spuId", required = true)
    @RequestMapping(value = "/devanning/spuGoodsAttribute/{spuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsSpuAttributeResponse> spuGoodsAttribute(@PathVariable String spuId) {
        GoodsSpuAttributeResponse goodsSpuResponseSpu=new GoodsSpuAttributeResponse();
        List<GoodsInfoAttributeVO> attributeList=new ArrayList<>();

        GoodsInfoByGoodsIdresponse goodsIdresponse = goodsInfoQueryProvider.getByGoodsIdAndAdded(DistributionGoodsChangeRequest.builder().goodsId(spuId).build()).getContext();
        if (CollectionUtils.isNotEmpty(goodsIdresponse.getGoodsInfoVOList())){
            goodsIdresponse.getGoodsInfoVOList().forEach(goodsInfoId -> {
                GoodsInfoAttributeVO vo=new GoodsInfoAttributeVO();
                StringBuilder builder =new StringBuilder();
                BaseResponse<GoodsAttributeKeyListResponse> list = goodsAttributeKeyQueryProvider.getList(GoodsAttributeKeyQueryRequest.builder().goodsInfoId(goodsInfoId.getGoodsInfoId()).build());
                if (list.getContext()!=null && list.getContext()!=null&& CollectionUtils.isNotEmpty(list.getContext().getAttributeKeyVOS())){
                    list.getContext().getAttributeKeyVOS().forEach(at -> {
                        builder.append(at.getGoodsAttributeValue());
                        builder.append(" ");
                });
                    vo.setGoodsId(spuId);
                    vo.setGoodsInfoId(goodsInfoId.getGoodsInfoId());
                    vo.setSpecText(builder.toString());
                    //加入库存
                  GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(goodsInfoId.getGoodsInfoId()).wareId(goodsInfoId.getWareId()).build()).getContext();
                    vo.setStock(goodsInfo.getStock());
                    vo.setLockStock(goodsInfoId.getLockStock());
                    DevanningGoodsInfoRequest devanningGoodsInfoByIdRequest=new DevanningGoodsInfoRequest();
                    DevanningGoodsInfoVO devanningGoodsInfoVO=new DevanningGoodsInfoVO();
                    devanningGoodsInfoVO.setGoodsInfoId(goodsInfoId.getGoodsInfoId());
                    devanningGoodsInfoByIdRequest.setDevanningGoodsInfoVO(devanningGoodsInfoVO);
                    BaseResponse<DevanningGoodsInfoByIdResponse> de=  devanningGoodsInfoQueryProvider.findBySkuId(devanningGoodsInfoByIdRequest);
                 if (Objects.nonNull(de)){
                     vo.setDevanningId(de.getContext().getDevanningGoodsInfoVO().getDevanningId());
                 }
                 attributeList.add(vo);
                }

            });
        }
        goodsSpuResponseSpu.setAttributeList(attributeList);
        return BaseResponse.success(goodsSpuResponseSpu);
    }
    /**
     * 积分Spu商品详情
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询积分Spu商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "pointsGoodsId", value = "pointsGoodsId",
            required = true)
    @RequestMapping(value = "/points/spu/{pointsGoodsId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByPointsGoodsIdResponse> pointsGoodsDetail(@PathVariable String pointsGoodsId, @RequestParam Long wareId) {
        GoodsViewByPointsGoodsIdRequest request = new GoodsViewByPointsGoodsIdRequest();
        request.setPointsGoodsId(pointsGoodsId);
        request.setWareId(wareId);
        return BaseResponse.success(goodsQueryProvider.getViewByPointsGoodsId(request).getContext());
    }

    /**
     * 小C-店铺精选-进入商品详情
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "小C-店铺精选-进入商品详情")
    @RequestMapping(value = "/shop/goods-detail/{distributorId}/{goodsId}/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdAndSkuIdsResponse> shopGoodsDetail(@PathVariable String distributorId,
                                                                        @PathVariable String goodsId, @PathVariable
                                                                                String goodsInfoId) {
        DistributorGoodsInfoListByCustomerIdAndGoodsIdRequest request = new
                DistributorGoodsInfoListByCustomerIdAndGoodsIdRequest(distributorId, goodsInfoId, goodsId);
        BaseResponse<DistributorGoodsInfoListByCustomerIdAndGoodsIdResponse> baseResponse =
                distributorGoodsInfoQueryProvider.listByCustomerIdAndGoodsId(request);
        List<DistributorGoodsInfoVO> list = baseResponse.getContext().getDistributorGoodsInfoVOList();
        if (CollectionUtils.isEmpty(list)) {
            return BaseResponse.FAILED();
        }
        List<String> skuIds = list.stream().map(DistributorGoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        return BaseResponse.success(goodsDetailBaseInfo(request.getGoodsInfoId(), skuIds, distributorId));
    }


    @ApiOperation(value = "未登录时,查询Spu商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/unLogin/spu/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> unLoginDetail(@PathVariable String skuId, @RequestParam(required = false) Long wareId
            , @RequestParam(required = false) Boolean matchWareHouseFlag,HttpServletRequest httpRequest) {
        return detail(skuId, wareId, matchWareHouseFlag,httpRequest);
    }

    @ApiOperation(value = "根据二级分类查询SPU列表")
    @PostMapping(value = "/getGoodsByParentCateId")
    public BaseResponse<CateGoodsInfoResponse> getGoodsByParentCateId(@RequestBody GoodsByParentCateIdQueryRequest request,HttpServletRequest httpRequest) {

        return BaseResponse.success(getGoodsByParentId(request, commonUtil.getCustomer(),httpRequest));
    }

    @ApiOperation(value = "不登陆根据二级分类查询SPU列表")
    @PostMapping(value = "/unLogin/getGoodsByParentCateId")
    public BaseResponse<CateGoodsInfoResponse> unLonginGoodsByParentCateId(@RequestBody GoodsByParentCateIdQueryRequest request,HttpServletRequest httpRequest) {
        return BaseResponse.success(getGoodsByParentId(request, commonUtil.getCustomer(),httpRequest));
    }

    @ApiOperation(value = "二级分类查询商品营销活动列表")
    @PostMapping(value = "/getGoodsMarketingByParentCateId")
    public BaseResponse<List<GoodsInfoVO>> getGoodsMarketingByParentCateId(@RequestBody GoodsByParentCateIdQueryRequest request,HttpServletRequest httpRequest) {

        return BaseResponse.success(getMarketingByParentCateId(request, commonUtil.getCustomer(),httpRequest));
    }

    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse list(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
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
            response = esGoodsInfoElasticService.pageByGoods(queryRequest);
            //对有效品类数据进行筛选
//            List<EsGoods> esGoods = response.getEsGoods().getContent();
//            List<EsGoods> esGoodsPart1 = esGoods.stream()
//                    .filter(item -> cateBindBrandIds.contains(item.getGoodsBrand().getBrandId()))
//                    .sorted(Comparator.comparing(EsGoods::getGoodsBrand, (a, b) -> {
//                                for (Long cateBindBrandId : cateBindBrandIds) {
//                                    if (a.getBrandId().equals(cateBindBrandId) || b.getBrandId().equals(cateBindBrandId)) {
//                                        if (a.getBrandId().equals(b.getBrandId())) {
//                                            return 0;
//                                        } else if (a.getBrandId().equals(cateBindBrandId)) {
//                                            return -1;
//                                        } else {
//                                            return 1;
//                                        }
//                                    }
//                                }
//                                return 0;
//                            })
//                    ).collect(Collectors.toList());
//            List<EsGoods> esGoodsPart2 = esGoods.stream()
//                    .filter(item -> !cateBindBrandIds.contains(item.getGoodsBrand().getBrandId()))
//                    .collect(Collectors.toList());
//            //装回去
//            esGoodsPart1.addAll(esGoodsPart2);
//            response.setEsGoods(new PageImpl<>(esGoodsPart1,  PageRequest.of(queryRequest.getPageNum(),
//                    queryRequest.getPageSize()), response.getEsGoods().getTotalElements()));
        } else {
            response = esGoodsInfoElasticService.pageByGoods(queryRequest);
        }
        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
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
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsLimitBrandResponse();
            }
            List<String> skuIds =
                    goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            //批量查询sku信息
            Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                    GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
            ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g, (x,y)->x));
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
                /**if (Objects.nonNull(goodsInfoVO.getMarketingId())) {
                 //活动id
                 goodsInfo.setMarketingId(goodsInfoVO.getMarketingId());
                 }
                 if (Objects.nonNull(goodsInfoVO.getPurchaseNum())) {
                 //活动限购数量
                 goodsInfo.setPurchaseNum(goodsInfoVO.getPurchaseNum());
                 }
                 //虚拟库存
                 if (Objects.nonNull(goodsInfoVO.getVirtualStock())){
                 goodsInfo.setVirtualStock(goodsInfoVO.getVirtualStock());
                 }*/
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

            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                goodsInfoList = filterResponse.getGoodsInfoVOList();
            }
            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
                //囤货
//                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = purchaseProvider.fillBuyCount(request)
//                        .getContext();
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
            /*CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)) {
                deliveryAddress = commonUtil.getDeliveryAddress();
            }*/
            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);

            //查询是否开启囤货功能
//            BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();

            response.getEsGoods().getContent().forEach(esGoods -> {
                List<GoodsInfoVO> goodsInfoVOList = voMap.get(esGoods.getId());
                Map<String,String> esGoodsInfoSubtitleNewMap = new HashMap<>();
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfoNests)){
                    goodsInfoNests.forEach(goodsInfoNest -> esGoodsInfoSubtitleNewMap.put(goodsInfoNest.getGoodsInfoId(),goodsInfoNest.getGoodsSubtitleNew()));
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

//                    Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(resultGoodsInfos
//                            .stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();
                    //设置库存
                    /**List<Long> unOnline = new ArrayList<>(10);
                     if (Objects.nonNull(queryRequest.getMatchWareHouseFlag()) && !queryRequest.getMatchWareHouseFlag()) {
                     unOnline = commonUtil.getWareHouseByStoreId(new ArrayList<>(storeIds), WareHouseType.STORRWAREHOUSE).stream()
                     .map(WareHouseVO::getWareId).collect(Collectors.toList());
                     }
                     List<Long> finalUnOnline = unOnline; del by jiangxin 20220427*/
                    List<String> goodsInfoIds = resultGoodsInfos.stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList());
                    Map<String, Long> collect2 = new HashMap<>();
                    //设置囤货商品活动是否过期
                    BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();
                    if(CollectionUtils.isNotEmpty(startPileActivity.getContext())){
                        //多商家囤货获取参与囤货活动商品虚拟库存
                        List<PileActivityGoodsVO> context = pileActivityProvider.getStartPileActivityPileActivityGoods(
                                PileActivityPileActivityGoodsRequest.builder().goodsInfoIds(goodsInfoIds).build()).getContext();

                        if (CollectionUtils.isNotEmpty(context)) {
                            collect2= context.stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, PileActivityGoodsVO::getVirtualStock, (x,y)->x));
                        }
                    }


                    Map<String, Long> finalCollect = collect2;
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {
                            List<GoodsWareStockVO> goodsWareStockVOS = optionalGoodsInfoNest.get().getGoodsWareStockVOS();
                            if (CollectionUtils.isNotEmpty(goodsWareStockVOS)) {
                                /**List<GoodsWareStockVO> stockList;
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
                                 goodsInfoNest.setStock(0L);
                                 } del by jiangxin 20220427 */
                                /**
                                 * 注释的原因：打开活动限购限制
                                 */
                                //活动限购库存
//                                if (Objects.nonNull(goodsInfoNest.getMarketingLabels())) {
//                                    goodsInfoNest.getMarketingLabels().forEach(marketingLabelVO -> {
//                                        if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
//                                                && marketingLabelVO.getGoodsPurchasingNumber() > 0
//                                                && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
//                                            goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
//                                        }
//                                    });
//                                }
                                //计算库存，虚拟库存、囤货数量
//                                calGoodsInfoNestStock(goodsInfoNest,goodsNumsMap);

                                //重新填充商品状态
                                if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
                                        && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
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
//                            addActivityRestricted(goodsInfoNest,marketingLabelVO,results);
                        });
                        //计算到手价
                        this.calNestTheirPrice(goodsInfoNest);
                        if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                    .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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

//            List<EsGoods> esGoodsList = new ArrayList<>();
//            //缺货
//            List<EsGoods> outStockList = new ArrayList<>();
//            response.getEsGoods().getContent().stream().forEach(item -> {
//                item.getGoodsInfos().stream().forEach(goodsInfoNest -> {
//                    if (goodsInfoNest.getGoodsStatus() == GoodsStatus.OK){
//                        esGoodsList.add(item);
//                    }else {
//                        outStockList.add(item);
//                    }
//                });
//            });
//            esGoodsList.addAll(outStockList);
//            response.setEsGoods(new PageImpl<>(esGoodsList));
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
                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
                        LiveGoodsVO::getGoodsId, (x,y)->x));
            }
        }

        //设置上商品参与囤货活动
        List<PileActivityVO> context1 = pileActivityProvider.getStartPileActivity().getContext();
        if (CollectionUtils.isNotEmpty(goodsInfoIds) && CollectionUtils.isNotEmpty(context1)) {
            PileActivityVO pileActivityVO = context1.get(0);
            BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods = pileActivityProvider.getStartPileActivityPileActivityGoods(
                    PileActivityPileActivityGoodsRequest.builder().pileActivityId(pileActivityVO.getActivityId()).goodsInfoIds(goodsInfoIds).build());
            BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
            if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){

                Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));

                response.getEsGoods().getContent().forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(goodsInfo -> {
                        if(Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))){
                            goodsInfo.setVirtualStock(BigDecimal.valueOf(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock()));
                            if(pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                                goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                            }else {
                                goodsInfo.setPileFlag(ForcePileFlag.PILE);
                            }
                        }else {
                            goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                        }
                    });
                });
            }
        }

        //活动图片处理
        Map<String, ActivityGoodsResponse> collect = new HashMap<>();
        if(CollectionUtils.isNotEmpty(goodsInfoIds)){
            BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(goodsInfoIds).build());
            List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
            if(CollectionUtils.isNotEmpty(activityGoodsResponse)){
                collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsInfoId, g -> g,(a,b)->a));
            }
        }

        Map<String, ActivityGoodsResponse> finalCollect = collect;
        //设置商品是否在直播
        response.getEsGoods().getContent().forEach(esGoods -> {
            esGoods.getGoodsInfos().forEach(goodsInfo -> {
                LiveHaveGoodsVO liveHaveGoodsVO=liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
                if(liveHaveGoodsVO.getIsHaveLive()==1){
                    goodsInfo.setIsHaveLive(1);
                    goodsInfo.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
                    goodsInfo.setLiveId(liveHaveGoodsVO.getLiveId());
                }else{
                    goodsInfo.setIsHaveLive(0);
                }
                //设置参与活动商品的活动图片
                if (CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())) {
                    ActivityGoodsResponse activityGoodsResponse = finalCollect.get(goodsInfo.getGoodsInfoId());
                    if(Objects.nonNull(activityGoodsResponse)){
                        goodsInfo.setGoodsInfoImg(activityGoodsResponse.getImgPath());
                    }
                }
            });
        });

        if (Objects.nonNull(liveMapLong)) {

            // 根据直播房价的id,查询直播信息
            LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
            liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
            liveRoomListReq.setDelFlag(DeleteFlag.NO);
            List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
            List<Long> liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(liveRoomVOList)) {
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
        sortMoreSkusSort(page.getEsGoods());
        return page;
    }

    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse simpleList(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }
        queryRequest.setIsKeywords(false);
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
            vipPriceFlag = true;
        }
        if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())) {
            vipPriceFlag = true;
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
        EsGoodsResponse response = esGoodsInfoElasticService.pageByGoods(queryRequest);
        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsLimitBrandResponse();
            }
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
            //重新赋值于Page内部对象
            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy(GoodsInfoVO::getGoodsId));
            Boolean finalVipPriceFlag = vipPriceFlag;

            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
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

                        if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                    .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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


        List<String> goodsInfoIds = new ArrayList<>();
        response.getEsGoods().getContent().forEach(esGoods -> {
            goodsInfoIds.addAll(esGoods.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList()));
        });

        //设置上商品参与囤货活动
        List<PileActivityVO> context1 = pileActivityProvider.getStartPileActivity().getContext();
        if (CollectionUtils.isNotEmpty(goodsInfoIds) && CollectionUtils.isNotEmpty(context1)) {
            PileActivityVO pileActivityVO = context1.get(0);
            BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods = pileActivityProvider.getStartPileActivityPileActivityGoods(
                    PileActivityPileActivityGoodsRequest.builder().pileActivityId(pileActivityVO.getActivityId()).goodsInfoIds(goodsInfoIds).build());
            BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
            if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){

                Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));

                response.getEsGoods().getContent().forEach(esGoods -> {
                    esGoods.getGoodsInfos().forEach(goodsInfo -> {
                        if(Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))){
                            goodsInfo.setVirtualStock(BigDecimal.valueOf(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock()));
                            if(pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                                goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                            }else {
                                goodsInfo.setPileFlag(ForcePileFlag.PILE);
                            }
                        }else {
                            goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                        }
                    });
                });
            }
        }

        //设置商品是否在直播
        response.getEsGoods().getContent().forEach(esGoods -> {
            esGoods.getGoodsInfos().forEach(goodsInfo -> {
                LiveHaveGoodsVO liveHaveGoodsVO=liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
                if(liveHaveGoodsVO.getIsHaveLive()==1){
                    goodsInfo.setIsHaveLive(1);
                    goodsInfo.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
                    goodsInfo.setLiveId(liveHaveGoodsVO.getLiveId());
                }else{
                    goodsInfo.setIsHaveLive(0);
                }
            });
        });
        return page;
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
        //如果二级分类为坚果炒货 排序方式为商品编辑排序  暂时写死 后续优化
        if (Objects.nonNull(queryRequest.getCateId())){
            if (queryRequest.getCateId().equals(1735L) || queryRequest.getCateId().equals(1733L)){
                queryRequest.setSortFlag(11);
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

        //查询商品分类，0散称，1定量 条件
//        Integer iss = request.getIsScatteredQuantitative();
//        if(Objects.nonNull(queryRequest.getIsScatteredQuantitative())){
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(queryRequest.getIsScatteredQuantitative());
//        }
//        if (iss == CategoriesStatus.ZERO.toValue() || iss == CategoriesStatus.ONE.toValue()) {
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(iss);
//        }

        //此接口不支持关键词搜索
        esGoodsInfoQueryRequest.setIsKeywords(false);
//        esGoodsInfoQueryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));

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

        log.info("根绝三级分类查询商品数据参数：{}",JSONObject.toJSONString(esGoodsInfoQueryRequest));

        EsGoodsResponse response;
        if (queryRequest.getSortFlag() != 2 && queryRequest.getSortFlag() != 3) {
            esGoodsInfoQueryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
        }

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

            response = esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
            //对有效品类数据进行筛选
//            List<EsGoods> esGoods = response.getEsGoods().getContent();
//            List<EsGoods> esGoodsPart1 = esGoods.stream()
//                    .filter(item -> cateBindBrandIds.contains(item.getGoodsBrand().getBrandId()))
//                    .sorted(Comparator.comparing(EsGoods::getGoodsBrand, (a, b) -> {
//                                for (Long cateBindBrandId : cateBindBrandIds) {
//                                    if (a.getBrandId().equals(cateBindBrandId) || b.getBrandId().equals(cateBindBrandId)) {
//                                        if (a.getBrandId().equals(b.getBrandId())) {
//                                            return 0;
//                                        } else if (a.getBrandId().equals(cateBindBrandId)) {
//                                            return -1;
//                                        } else {
//                                            return 1;
//                                        }
//                                    }
//                                }
//                                return 0;
//                            })
//                    ).collect(Collectors.toList());
//            List<EsGoods> esGoodsPart2 = esGoods.stream()
//                    .filter(item -> !cateBindBrandIds.contains(item.getGoodsBrand().getBrandId()))
//                    .collect(Collectors.toList());
//            //装回去
//            esGoodsPart1.addAll(esGoodsPart2);
//            response.setEsGoods(new PageImpl<>(esGoodsPart1,  PageRequest.of(queryRequest.getPageNum(),
//                    queryRequest.getPageSize()), response.getEsGoods().getTotalElements()));
        } else {
            response = esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        }
        List<String> skuIds = new ArrayList<>();
        log.info("根绝三级分类查询商品数据：{}",JSONObject.toJSONString(response.getEsGoods().getContent()));

        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
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
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsResponse();
            }
            skuIds = goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
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

                //填充询价标志
                goodsInfo.setInquiryFlag(goodsInfoVO.getInquiryFlag());
                /**即买即提===》删除以下属性
                 if (Objects.nonNull(goodsInfoVO.getMarketingId())) {
                 //活动id
                 goodsInfo.setMarketingId(goodsInfoVO.getMarketingId());
                 }
                 if (Objects.nonNull(goodsInfoVO.getPurchaseNum())) {
                 //活动限购数量
                 goodsInfo.setPurchaseNum(goodsInfoVO.getPurchaseNum());
                 }
                 //虚拟库存
                 if (Objects.nonNull(goodsInfoVO.getVirtualStock())){
                 goodsInfo.setVirtualStock(goodsInfoVO.getVirtualStock());
                 }*/
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

            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                goodsInfoList = filterResponse.getGoodsInfoVOList();
            }
            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
//                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = purchaseProvider.fillBuyCount(request)
//                        .getContext();
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount(request)
                        .getContext();
                goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
            }
            //重新赋值于Page内部对象
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
            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);

            //查询是否开启囤货功能
//            BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();

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
                    }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
                            LiveGoodsVO::getGoodsId));
                }
            }

            Map<String, Long> finalLiveMapLong = liveMapLong;
            final Map<String, Long> liveMap = liveMapLong;

            List<Long> liveRoomIdList = Lists.newArrayList();

            // 根据直播房价的id,查询直播信息
            if (Objects.nonNull(liveMapLong)) {
                LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
                liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
                liveRoomListReq.setDelFlag(DeleteFlag.NO);
                List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
                liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
            }

            List<Long> finalLiveRoomIdList = liveRoomIdList;

            //重新赋值于Page内部对象
            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy
                    (GoodsInfoVO::getGoodsId));
            List<GoodsVO> goodsList = response.getGoodsList();
            Map<String, String> goodsVOMap = goodsList.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew, (key1, key2) -> key2));

            response.getEsGoods().getContent().forEach(esGoods -> {
                esGoods.setGoodsInfos(KsBeanUtil.convert(voMap.get(esGoods.getId()), GoodsInfoNest.class));
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();
//              List<GoodsInfoNest> resultGoodsInfos = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoNest.class);

                /**Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(goodsInfoNests
                 .stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();
                 //设置库存 ==========>es填充库存数量
                 List<Long> unOnline = new ArrayList<>(10);
                 if (Objects.nonNull(queryRequest.getMatchWareHouseFlag()) && !queryRequest.getMatchWareHouseFlag()) {
                 unOnline = commonUtil.getWareHouseByStoreId(new ArrayList<>(storeIds), WareHouseType.STORRWAREHOUSE).stream()
                 .map(WareHouseVO::getWareId).collect(Collectors.toList());
                 }
                 List<Long> finalUnOnline = unOnline;*/
                goodsInfoNests.forEach(goodsInfoNest -> {

                    goodsInfoNest.setGoodsFavorableCommentNum(esGoods.getGoodsFavorableCommentNum());
                    goodsInfoNest.setGoodsSalesNum(esGoods.getGoodsSalesNum());
                    goodsInfoNest.setGoodsCollectNum(esGoods.getGoodsCollectNum());
                    goodsInfoNest.setGoodsEvaluateNum(esGoods.getGoodsEvaluateNum());
                    goodsInfoNest.setGoodsSubtitleNew(goodsVOMap.get(goodsInfoNest.getGoodsId()));
                    /**Optional<GoodsInfoNest> optionalGoodsInfoNest =
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
                     long sumStock = stockList.stream().mapToLong(GoodsWareStockVO::getStock).sum();
                     goodsInfoNest.setStock(sumStock);
                     } else {
                     goodsInfoNest.setStock(0L);
                     }
                     //计算库存，虚拟库存、囤货数量
                     //                            calGoodsInfoNestStock(goodsInfoNest,goodsNumsMap);

                     //重新填充商品状态
                     if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
                     && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
                     goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                     // 判断是否有T，如果是1，就设置为2
                     if (goodsInfoNest.getGoodsInfoName().endsWith("T") || goodsInfoNest.getGoodsInfoName().endsWith("t")) {
                     if (goodsInfoNest.getStock() < 1) {
                     goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                     }
                     } else {
                     if (goodsInfoNest.getStock() < 1) {
                     goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                     }
                     }

                     } else {
                     goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                     }

                     goodsInfoNest.setGoodsWareStockVOS(optionalGoodsInfoNest.get().getGoodsWareStockVOS());
                     }
                     }*/

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
                            if (null !=goodsInfoNest.getStock()  && goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0) {
                                goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                            }
                        }

                    } else {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                    }

                    //设置企业商品的审核状态 ，以及会员的大客户价
                    if (finalVipPriceFlag
                            && Objects.nonNull(goodsInfoNest.getVipPrice())
                            && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                        if (goodsInfoNest.getGoodsInfoType() == 1) {
                            goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
                        } else {
                            goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                        }
                    }
                    log.info("GoodsBaseController.list goodsInfoNest:{}",goodsInfoNest);
                    //如果商品参加了营销活动，vip价格设置为0 vipPrice不参与任何营销活动
                    if (CollectionUtils.isNotEmpty(goodsInfoNest.getMarketingLabels())) {
                        goodsInfoNest.setVipPrice(BigDecimal.ZERO);
                    }
                    //计算到手价
                    goodsInfoNest.getMarketingLabels().stream().forEach(marketingLabelVO -> {

                        /**
                         * 注释的原因：营销限购放开加购限制
                         */
                        //填充限购数量
//                        if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
//                                && marketingLabelVO.getGoodsPurchasingNumber() > 0
//                                && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
//                            goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
//                        }

                        if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                && marketingLabelVO.getSubType() == 1 && marketingLabelVO.getNumber() == 1) {//1：满数量减
                            goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice().subtract(marketingLabelVO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));

                        }else if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1) {//3:满数量折
                            goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice()
                                    .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
                                    .setScale(2,BigDecimal.ROUND_HALF_UP));

                        }
//                        addActivityRestricted(goodsInfoNest,marketingLabelVO,results);
                    });
                    //列表计算到手价
                    this.calNestTheirPrice(goodsInfoNest);
                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }

                    //直播
                    if (Objects.nonNull(finalLiveMapLong)){
                        Long liveGoodsId = liveMap.get(goodsInfoNest.getGoodsInfoId());
                        if (liveGoodsId != null) {
                            if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId)) {
                                List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId, GoodsLiveRoomVO.class);
                                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
                                    for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
                                        if (finalLiveRoomIdList.contains(liveRoomVO.getRoomId())) {
                                            goodsInfoNest.setLiveEndTime(liveRoomVO.getLiveEndTime());
                                            goodsInfoNest.setLiveStartTime(liveRoomVO.getLiveStartTime());
                                            goodsInfoNest.setRoomId(liveRoomVO.getRoomId());
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    }

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
            //设置上商品参与囤货活动
            List<PileActivityVO> context1 = pileActivityProvider.getStartPileActivity().getContext();
            if (CollectionUtils.isNotEmpty(skuIds) && CollectionUtils.isNotEmpty(context1)) {

                PileActivityVO pileActivityVO = context1.get(0);
                BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods =
                        pileActivityProvider.getStartPileActivityPileActivityGoods(
                                PileActivityPileActivityGoodsRequest.builder()
                                        .goodsInfoIds(skuIds).pileActivityId(pileActivityVO.getActivityId()).build());

                BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
                if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){

                    Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));

                    response.getEsGoods().getContent().forEach(esGoods -> {
                        esGoods.getGoodsInfos().forEach(goodsInfo -> {
                            if(Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))){
                                goodsInfo.setVirtualStock(BigDecimal.valueOf(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock()));
                                if(pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                                    goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                                }else {
                                    goodsInfo.setPileFlag(ForcePileFlag.PILE);
                                }
                            }else {
                                goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                            }
                        });
                    });
                }
            }
            //活动图片处理
            Map<String, ActivityGoodsResponse> collect = new HashMap<>();
            if(CollectionUtils.isNotEmpty(skuIds)){
                BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(skuIds).build());
                List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
                if(CollectionUtils.isNotEmpty(activityGoodsResponse)){
                    collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsInfoId, g -> g,(a,b)->a));
                }
            }

            Map<String, ActivityGoodsResponse> finalCollect = collect;

            //设置商品是否在直播
            response.getEsGoods().getContent().forEach(esGoods -> {
                esGoods.getGoodsInfos().forEach(goodsInfo -> {
                    LiveHaveGoodsVO liveHaveGoodsVO = liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfo.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
                    if (liveHaveGoodsVO.getIsHaveLive() == 1) {
                        goodsInfo.setIsHaveLive(1);
                        goodsInfo.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
                        goodsInfo.setLiveId(liveHaveGoodsVO.getLiveId());
                    } else {
                        goodsInfo.setIsHaveLive(0);
                    }
                    //设置参与活动商品的活动图片
                    if (CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())) {
                        ActivityGoodsResponse activityGoodsResponse = finalCollect.get(goodsInfo.getGoodsInfoId());
                        if(Objects.nonNull(activityGoodsResponse)){
                            goodsInfo.setGoodsInfoImg(activityGoodsResponse.getImgPath());
                        }
                    }
                });
            });
//            List<EsGoods> esGoodsList = new ArrayList<>();
//            //缺货
//            List<EsGoods> outStockList = new ArrayList<>();
//            response.getEsGoods().getContent().stream().forEach(item -> {
//                item.getGoodsInfos().stream().forEach(goodsInfoNest -> {
//                    if (goodsInfoNest.getGoodsStatus() == GoodsStatus.OK){
//                        esGoodsList.add(item);
//                    }else {
//                        outStockList.add(item);
//                    }
//                });
//            });
//            esGoodsList.addAll(outStockList);

//            response.setEsGoods(new MicroServicePage<>(esGoodsList, cateIdQueryRequest.getPageable(), esGoodsList.size()));
        }
        sortMoreSkusSort(response.getEsGoods());
        return response;
    }

    private void sortMoreSkusSort(Page<EsGoods> esGoods) {
        if (null == esGoods || CollectionUtils.isEmpty(esGoods.getContent())){
            return;
        }
        esGoods.getContent().forEach(goods -> {
            final List<GoodsInfoNest> goodsInfos = goods.getGoodsInfos();
            if (CollectionUtils.isEmpty(goodsInfos) || goodsInfos.size() <2){
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


    /*********************************************/


    private CustomerDeliveryAddressResponse getDeliveryAddress() {
        CustomerDeliveryAddressRequest queryRequest = new CustomerDeliveryAddressRequest();
        String customerId = commonUtil.getOperatorId();
        queryRequest.setCustomerId(customerId);
        BaseResponse<CustomerDeliveryAddressResponse> customerDeliveryAddressResponseBaseResponse = customerDeliveryAddressQueryProvider.getDefaultOrAnyOneByCustomerId(queryRequest);
        CustomerDeliveryAddressResponse customerDeliveryAddressResponse = customerDeliveryAddressResponseBaseResponse.getContext();
        return customerDeliveryAddressResponse;
    }


    private List<String> getAllowedArea(String allowedPurchaseArea) {
        if(Objects.isNull(allowedPurchaseArea)){
            return Lists.newArrayList();
        }
        List<GoodsInfoAllowedArea> goodsInfoAllowedAreas = JSON.parseArray(allowedPurchaseArea, GoodsInfoAllowedArea.class);
        List<String> list = goodsInfoAllowedAreas.stream().map(x -> x.getCode()).collect(Collectors.toList());
        return list;
    }

    /**
     * 查询SPU列表(新)
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private GoodsInfoResponse getGoodsByCateId(GoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
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

        /**
         * 请求参数转化
         * @param
         */
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);

        //查询商品分类，0散称，1定量 条件
//        Integer iss = queryRequest.getIsScatteredQuantitative();
//        if(Objects.nonNull(queryRequest.getIsScatteredQuantitative())){
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(queryRequest.getIsScatteredQuantitative());
//        }
//        if (iss == CategoriesStatus.ZERO.toValue() || iss == CategoriesStatus.ONE.toValue()) {
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(iss);
//        }

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
            esGoodsInfoQueryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
            response = esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        } else {
            response = esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        }

        if (Objects.nonNull(response.getEsGoods()) && CollectionUtils.isNotEmpty(response.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            List<String> skuIds =
                    goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());

            //批量查询sku信息
            Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                    GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
            ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));
            //填充商品持久化信息
            for (GoodsInfoVO goodsInfo : goodsInfoList) {
                GoodsInfoVO goodsInfoVO = goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO());
                //填充询价标志
                goodsInfo.setInquiryFlag(goodsInfoVO.getInquiryFlag());
                if (Objects.nonNull(goodsInfoVO.getMarketingId())) {
                    //活动id
                    goodsInfo.setMarketingId(goodsInfoVO.getMarketingId());
                }
                if (Objects.nonNull(goodsInfoVO.getPurchaseNum())) {
                    Long max = Long.max(goodsInfoVO.getPurchaseNum(), 0L);
                    //活动限购数量
                    goodsInfo.setPurchaseNum(max);
                }
                if (Objects.nonNull(goodsInfoVO.getVirtualStock())){
                    //虚拟库存
                    goodsInfo.setVirtualStock(goodsInfoVO.getVirtualStock());
                }
            }

            //根据开关重新设置分销商品标识
            distributionService.checkDistributionSwitch(goodsInfoList);
            //只看分享赚商品信息
            if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue()
                    == queryRequest.getDistributionGoodsAudit()) {
                goodsInfoList = goodsInfoList.stream().filter(goodsInfoVO -> DistributionGoodsAudit.CHECKED.equals
                        (goodsInfoVO.getDistributionGoodsAudit())).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new GoodsInfoResponse();
            }
            //计算区间价
//            GoodsIntervalPriceByCustomerIdRequest priceRequest = new GoodsIntervalPriceByCustomerIdRequest();
//            priceRequest.setGoodsInfoDTOList(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class));
//            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
//                priceRequest.setCustomerId(customer.getCustomerId());
//            }
//            GoodsIntervalPriceByCustomerIdResponse priceResponse =
//                    goodsIntervalPriceProvider.putByCustomerId(priceRequest).getContext();
//            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
//            goodsInfoList = priceResponse.getGoodsInfoVOList();

            //计算营销价格
            /**
             * 填充优惠券标签couponLabels字段
             */
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


            //获取加入购物车商品数量
            List<PurchaseVO> purchaseList = new ArrayList<>();
            if (Objects.nonNull(customer) && StringUtils.isNotEmpty(customer.getCustomerId())) {
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customer.getCustomerId());
                purchaseQueryRequest.setGoodsInfoIds(skuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = purchaseQueryProvider.query(purchaseQueryRequest).getContext();
                if (purchaseQueryResponse != null && CollectionUtils.isNotEmpty(purchaseQueryResponse.getPurchaseList())) {
                    purchaseList.addAll(purchaseQueryResponse.getPurchaseList());
                    log.info("GoodsBaseController.getGoodsByParentId purchaseQueryResponse size:{}", purchaseList.size());
                }
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

            //获取客户收货地址
           /* CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)){
                deliveryAddress = commonUtil.getDeliveryAddress();
            }*/
            //供内部类使用
            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
            //查询是否开启囤货功能
//            BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();

            response.getEsGoods().getContent().forEach(esGoods -> {
                List<GoodsInfoVO> goodsInfoVOList = voMap.get(esGoods.getId());
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
                    List<GoodsInfoNest> resultGoodsInfos = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoNest.class);
                    //商品囤货数量
//                    Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(resultGoodsInfos
//                            .stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();
                    //设置库存
                    /**List<Long> unOnline = new ArrayList<>(10);
                    if (Objects.nonNull(queryRequest.getMatchWareHouseFlag()) && !queryRequest.getMatchWareHouseFlag()) {
                        unOnline = commonUtil.getWareHouseByStoreId(new ArrayList<>(storeIds), WareHouseType.STORRWAREHOUSE).stream()
                                .map(WareHouseVO::getWareId).collect(Collectors.toList());
                    }
                    List<Long> finalUnOnline = unOnline;*/
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
                        if (optionalGoodsInfoNest.isPresent()) {

                            if(Objects.isNull(goodsInfoNest.getGoodsInfoImg())){
//                                goodsInfoNest.setGoodsInfoImg(goodsInfoNests.stream().filter(n->goodsInfoNest.getGoodsInfoId().equals(n.getGoodsInfoId())).findFirst().orElse(null).getGoodsInfoImg());
                                goodsInfoNest.setGoodsInfoImg(optionalGoodsInfoNest.get().getGoodsInfoImg());
                            }

                            /**List<GoodsWareStockVO> goodsWareStockVOS = optionalGoodsInfoNest.get().getGoodsWareStockVOS();
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
                                //计算库存，虚拟库存、囤货数量
//                                calGoodsInfoNestStock(goodsInfoNest,goodsNumsMap);
                            }*/
                        }

                        //设置企业商品的审核状态 ，以及会员的大客户价
                        if (finalVipPriceFlag
                                && Objects.nonNull(goodsInfoNest.getVipPrice())
                                && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                            if (goodsInfoNest.getGoodsInfoType() == 1) {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
                            } else {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                            }
                        }

                        //填充购物车中的商品数量
                        if (goodsInfoNest != null && purchaseList != null){
                            purchaseList.forEach(goodsInfo->{
                                log.info("GoodsBaseController.getGoodsByParentId goodsInfoNest.getGoodsInfoId:{} goodsInfo.GoodsInfoId:{}",
                                        goodsInfoNest.getGoodsInfoId(),goodsInfo.getGoodsInfoId());
                                if (goodsInfo.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId()) && goodsInfo.getGoodsNum() > 0){
                                    goodsInfoNest.setBuyCount(goodsInfo.getGoodsNum());
                                }
                            });
                        }

                        //设置商品状态
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
                                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                        goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                                    }
                                }
                            }

                        } else {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                        }
                        //计算到手价
                        goodsInfoNest.getMarketingLabels().stream().forEach(marketingLabelVO -> {
                            log.info("GoodsBaseController.getGoodsByCateId marketingLabelVO:{}",JSONObject.toJSONString(marketingLabelVO));
                            if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                    && marketingLabelVO.getSubType() == 1 && marketingLabelVO.getNumber() == 1) {//1：满数量减
                                goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice().subtract(marketingLabelVO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));

                            }else if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                    && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1){
                                goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice()
                                        .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
                                        .setScale(2,BigDecimal.ROUND_HALF_UP));
                            }
//                            addActivityRestricted(goodsInfoNest,marketingLabelVO,results);
                        });

                        if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                            List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                    .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                            if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                            }
                        }
                    });
                    //设置企业商品的审核状态 ，以及会员的大客户价
//                    resultGoodsInfos.forEach(goodsInfoNest -> {
//                        if (finalVipPriceFlag
//                                && Objects.nonNull(goodsInfoNest.getVipPrice())
//                                && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
//                            if (goodsInfoNest.getGoodsInfoType() == 1) {
//                                goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
//                            } else {
//                                goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
//                            }
//                        }
//                    });
                    //填充购物车中的商品数量
//                    resultGoodsInfos.forEach(goodsInfoNest -> {
//                        if (goodsInfoNest != null && purchaseList != null){
//                            purchaseList.forEach(goodsInfo->{
//                                log.info("GoodsBaseController.getGoodsByParentId goodsInfoNest.getGoodsInfoId:{} goodsInfo.GoodsInfoId:{}",
//                                         goodsInfoNest.getGoodsInfoId(),goodsInfo.getGoodsInfoId());
//                                if (goodsInfo.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId()) && goodsInfo.getGoodsNum() > 0){
//                                    goodsInfoNest.setBuyCount(goodsInfo.getGoodsNum());
//                                }
//                            });
//                        }
//                    });

                    //设置商品状态
//                    resultGoodsInfos.forEach(goodsInfoVO -> {
//                            if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
//                                    && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())) {
//                                goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
//                                // 判断是否有T，如果是1，就设置为2
//                                if (goodsInfoVO.getGoodsInfoName().endsWith("T") || goodsInfoVO.getGoodsInfoName().endsWith("t")) {
//                                    if (goodsInfoVO.getStock() < 1) {
//                                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
//                                    }
//                                } else {
//                                    if (goodsInfoVO.getStock() < 1) {
//                                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                                    }
//                                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
//                                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
//                                                                                   .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
//                                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
//                                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
//                                            goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                                        }
//                                    }
//                                }
//
//                            } else {
//                                goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
//                            }
//                        //计算到手价
//                        goodsInfoVO.getMarketingLabels().stream().forEach(marketingLabelVO -> {
//                            log.info("GoodsBaseController.getGoodsByCateId marketingLabelVO:{}",JSONObject.toJSONString(marketingLabelVO));
//                            if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
//                                    && marketingLabelVO.getSubType() == 1 && marketingLabelVO.getNumber() == 1
//                                    && Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber()) && marketingLabelVO.getGoodsPurchasingNumber() > 0) {//1：满数量减
//                                goodsInfoVO.setTheirPrice(goodsInfoVO.getMarketPrice().subtract(marketingLabelVO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
//                            }else if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
//                                    && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1
//                                    && Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber()) && marketingLabelVO.getGoodsPurchasingNumber() > 0){
//                                goodsInfoVO.setTheirPrice(goodsInfoVO.getMarketPrice()
//                                                                     .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
//                                                                     .setScale(2,BigDecimal.ROUND_HALF_UP));
//                            }
//                            addActivityRestricted(goodsInfoVO,marketingLabelVO,results);
//                        });
//                    });

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

        //TODO :编写代码处
        GoodsInfoResponse page = KsBeanUtil.convert(response, GoodsInfoResponse.class);

        //查询到所有商品类型转换
        MicroServicePage<GoodsNewVo> goodsNewVos = KsBeanUtil.convertPage(response.getEsGoods(), GoodsNewVo.class);

        //按商品分类 分组商品
        ArrayList<GoodsNewVo> categoriesZeroList = Lists.newArrayList();
        ArrayList<GoodsNewVo> categoriesOneList = Lists.newArrayList();
        ArrayList<GoodsNewVo> goodsDatas = Lists.newArrayList();
        goodsNewVos.getContent().forEach(g -> {
            int iSQ = g.getGoodsInfos().get(0).getIsScatteredQuantitative();
            if(Objects.isNull(iSQ) || iSQ == -1){
                goodsDatas.add(g);
            }
            if (iSQ == CategoriesStatus.ZERO.toValue()) {
                categoriesZeroList.add(g);
            }
            if (iSQ == CategoriesStatus.ONE.toValue()) {
                categoriesOneList.add(g);
            }
        });
        page.setGoodsData(goodsDatas);
        page.setCategoriesOne(categoriesOneList);
        page.setCategoriesZero(categoriesZeroList);

        goodsNewVos.setContent(new ArrayList<>());
        page.setEsGoods(goodsNewVos);


        // 获取直播商品
//        Map<String, Long> liveMapLong = new HashMap<>();
//        List<String> goodsInfoIds = new ArrayList<>();
//        response.getEsGoods().getContent().forEach(esGoods -> {
//            goodsInfoIds.addAll(esGoods.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList()));
//        });
//        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
//            // 根据商品id,查询直播商品的id
//            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
//            // 将数据转成map(商品sku,直播商品名称)
//            if (CollectionUtils.isNotEmpty(liveGoodsVOList)) {
//                liveMapLong = liveGoodsVOList.stream().filter(entity -> {
//                    return entity.getGoodsId() != null;
//                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
//                        LiveGoodsVO::getGoodsId));
//            }
//        }
//
//        if (!liveMapLong.isEmpty()) {
//
//            // 根据直播房价的id,查询直播信息
//            LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
//            liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
//            liveRoomListReq.setDelFlag(DeleteFlag.NO);
//            List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
//            List<Long> liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(liveRoomVOList)) {
//                final Map<String, Long> liveMap = liveMapLong;
//                response.getEsGoods().getContent().forEach(item -> {
//                    item.getGoodsInfos().stream().forEach(goodsInfoNest -> {
//                        Long liveGoodsId = liveMap.get(goodsInfoNest.getGoodsInfoId());
//                        if (liveGoodsId != null) {
//                            if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId)) {
//                                List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId, GoodsLiveRoomVO.class);
//                                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
//                                    for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
//                                        if (liveRoomIdList.contains(liveRoomVO.getRoomId())) {
//                                            goodsInfoNest.setLiveEndTime(liveRoomVO.getLiveEndTime());
//                                            goodsInfoNest.setLiveStartTime(liveRoomVO.getLiveStartTime());
//                                            goodsInfoNest.setRoomId(liveRoomVO.getRoomId());
//                                            break;
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//                    });
//
//                });
//            }
//        }

        return page;
    }

    /**
     * 根据二级分类查询商品
     */
    private CateGoodsInfoResponse getGoodsByParentId(GoodsByParentCateIdQueryRequest request, CustomerVO customer,HttpServletRequest httpRequest) {
        GoodsInfoQueryRequest queryRequest = KsBeanUtil.convert(request, GoodsInfoQueryRequest.class);
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }
        //如果二级分类为坚果炒货 排序方式为商品编辑排序  暂时写死 后续优化
        if (queryRequest.getCateId().equals(1735L) || queryRequest.getCateId().equals(1733L)){
            queryRequest.setSortFlag(11);
        }

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
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
        /**
         * 请求参数转化
         * @param
         */
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);

        //查询商品分类，0散称，1定量 条件
//        Integer iss = request.getIsScatteredQuantitative();
//        if(Objects.nonNull(queryRequest.getIsScatteredQuantitative())){
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(queryRequest.getIsScatteredQuantitative());
//        }
//        if (iss == CategoriesStatus.ZERO.toValue() || iss == CategoriesStatus.ONE.toValue()) {
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(iss);
//        }

        //此接口不支持关键词搜索
        esGoodsInfoQueryRequest.setIsKeywords(false);

        //获取会员和等级
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        esGoodsInfoQueryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
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
//        EsGoodsResponse response;
        EsGoodsResponse esBaseInfoByParams = null;
        //分类查询特定情况，通过分类绑定的品牌进行排序
        if (Objects.nonNull(queryRequest.getCateId())) {
            //查询二级分类下所有三级分类
            List<GoodsCateNewVO> goodsCateNewVOS = JSON.parseArray(goodsCateQueryProvider.getByCacheNew().getContext().getResult(), GoodsCateNewVO.class);

            List<Long> childrenCateIds = goodsCateNewVOS.stream().filter(g -> request.getCateId().equals(g.getCateParentId())).collect(Collectors.toList())
                    .stream().map(c -> c.getCateId()).collect(Collectors.toList());

            System.out.println("childrenCateIds ======================" + childrenCateIds);

            CateBrandSortRelListRequest cateBrandSortRelListRequest = new CateBrandSortRelListRequest();
            cateBrandSortRelListRequest.setCateIdList(childrenCateIds);
            List<CateBrandSortRelVO> cateBrandSortRelVOList =
                    cateBrandSortRelQueryProvider.list(cateBrandSortRelListRequest).getContext().getCateBrandSortRelVOList();
            //当前三级类目包含的品牌ID
            List<Long> cateBindBrandIds = cateBrandSortRelVOList.stream()
                    .filter(item -> item.getSerialNo() != 0)
                    .sorted(Comparator.comparing(CateBrandSortRelVO::getSerialNo)) //按照排序字段升序排列
                    .map(item -> item.getBrandId()).collect(Collectors.toList());

            esGoodsInfoQueryRequest.setCateBindBrandIds(cateBindBrandIds);
            esGoodsInfoQueryRequest.setCateIds(childrenCateIds);
            esGoodsInfoQueryRequest.setPageNum(0);
            //获取默认最大长度
            esGoodsInfoQueryRequest.setPageSize(10000);
            esGoodsInfoQueryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
            esBaseInfoByParams = esGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        }
        if(esBaseInfoByParams.getEsGoods() == null){
            return CateGoodsInfoResponse.builder().goodsInfoByCateIdResponses(Lists.newArrayList()).build();
        }

        if (CollectionUtils.isNotEmpty(esBaseInfoByParams.getEsGoods().getContent())) {
            List<GoodsInfoVO> goodsInfoList = esBaseInfoByParams.getEsGoods().stream().map(EsGoods::getGoodsInfos)
                    .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                    .collect(Collectors.toList());
            List<String> skuIds =
                    goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());

//            //批量查询sku信息
//            Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
//                    GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
//            ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));
//            //填充商品持久化信息
//            for (GoodsInfoVO goodsInfo : goodsInfoList) {
//                GoodsInfoVO goodsInfoVO = goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO());
//                //填充询价标志
//                goodsInfo.setInquiryFlag(goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO()).getInquiryFlag());
//                if (Objects.nonNull(goodsInfoVO.getMarketingId())) {
//                    //活动id
//                    goodsInfo.setMarketingId(goodsInfoVO.getMarketingId());
//                }
//                if (Objects.nonNull(goodsInfoVO.getPurchaseNum())) {
//                    //活动限购数量
//                    goodsInfo.setPurchaseNum(goodsInfoVO.getPurchaseNum());
//                }
//            }
            //获取加入购物车商品数量
            List<PurchaseVO> purchaseList = new ArrayList<>();
            if (Objects.nonNull(customer) && StringUtils.isNotEmpty(customer.getCustomerId())) {

                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(customer.getCustomerId());
                purchaseQueryRequest.setGoodsInfoIds(skuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                PurchaseQueryResponse purchaseQueryResponse = purchaseQueryProvider.query(purchaseQueryRequest).getContext();
                if (purchaseQueryResponse != null && CollectionUtils.isNotEmpty(purchaseQueryResponse.getPurchaseList())) {
                    purchaseList.addAll(purchaseQueryResponse.getPurchaseList());
                    log.info("GoodsBaseController.getGoodsByParentId purchaseQueryResponse size:{}", purchaseList.size());
                }
            }

            //计算营销价格
//            /**
//             * 填充优惠券标签couponLabels字段
//             */
//            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
//            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class));
//            if (Objects.nonNull(customer)) {
//                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
//            }
//            GoodsInfoListByGoodsInfoResponse filterResponse = marketingPluginProvider.goodsListFilter(filterRequest)
//                    .getContext();
//
//            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
//                goodsInfoList = filterResponse.getGoodsInfoVOList();
//            }


            //重新赋值于Page内部对象
//            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy
//                    (GoodsInfoVO::getGoodsId));
            Boolean finalVipPriceFlag = vipPriceFlag;
            Set<Long> storeIds = new HashSet<>(10);
            for (EsGoods inner : esBaseInfoByParams.getEsGoods()) {
                Set<Long> collect = inner.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId).collect(Collectors.toSet());
                storeIds.addAll(collect);
            }

            //获取客户收货地址
            /*CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)){
                deliveryAddress = commonUtil.getDeliveryAddress();
            }*/
            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
            //查询是否开启囤货功能
//            BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();

            //查询商品所有囤货数量
//            Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsNumsMap();

            esBaseInfoByParams.getEsGoods().forEach(esGoods -> {
//                List<GoodsInfoVO> goodsInfoVOList = voMap.get(esGoods.getId());
                List<GoodsInfoNest> goodsInfoNests = esGoods.getGoodsInfos();

                if (CollectionUtils.isNotEmpty(goodsInfoList)) {
//                    List<GoodsInfoNest> resultGoodsInfos = KsBeanUtil.convert(goodsInfoVOList, GoodsInfoNest.class);
                    //设置散称和定量默认为-1
//                    resultGoodsInfos.forEach(goodsInfoNest -> {
//                        goodsInfoNests.forEach(goodsInfoNest1 -> {
//                            if (goodsInfoNest.getGoodsInfoId().equals(goodsInfoNest1.getGoodsInfoId())) {
//                                goodsInfoNest.setIsScatteredQuantitative(goodsInfoNest1.getIsScatteredQuantitative());
//                            }
//                        });
//                        if (goodsInfoNest.getIsScatteredQuantitative() == null) {
//                            goodsInfoNest.setIsScatteredQuantitative(-1);
//                        }
//
//                    });

                    //设置库存
                    /**List<Long> unOnline = new ArrayList<>(10);
                    if (Objects.nonNull(queryRequest.getMatchWareHouseFlag()) && !queryRequest.getMatchWareHouseFlag()) {
                        unOnline = commonUtil.getWareHouseByStoreId(new ArrayList<>(storeIds), WareHouseType.STORRWAREHOUSE).stream()
                                .map(WareHouseVO::getWareId).collect(Collectors.toList());
                    }
                    List<Long> finalUnOnline = unOnline;*/
                    goodsInfoNests.forEach(goodsInfoNest -> {

//                        goodsInfoNests.forEach(goodsInfoNest1 -> {
//                            if (goodsInfoNest.getGoodsInfoId().equals(goodsInfoNest1.getGoodsInfoId())) {
//                                goodsInfoNest.setIsScatteredQuantitative(goodsInfoNest1.getIsScatteredQuantitative());
//                            }
//                        });

                        if (goodsInfoNest.getIsScatteredQuantitative() == null) {
                            goodsInfoNest.setIsScatteredQuantitative(-1);
                        }

//                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
//                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
//                        if (optionalGoodsInfoNest.isPresent()) {
                        /**List<GoodsWareStockVO> goodsWareStockVOS = goodsInfoNest.getGoodsWareStockVOS();
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
                            //计算库存，虚拟库存、囤货数量
//                                calGoodsInfoNestStock(goodsInfoNest,goodsNumsMap);
                        }*/
//                        }

                        //设置企业商品的审核状态 ，以及会员的大客户价
                        if (finalVipPriceFlag
                                && Objects.nonNull(goodsInfoNest.getVipPrice())
                                && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                            if (goodsInfoNest.getGoodsInfoType() == 1) {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
                            } else {
                                goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                            }
                        }

                        //填充购物车中的商品数量
                        if (goodsInfoNest != null && CollectionUtils.isNotEmpty(purchaseList)){

                            List<PurchaseVO> collect = purchaseList.stream().filter(p -> p.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).collect(Collectors.toList());

                            if(CollectionUtils.isNotEmpty(collect)){
                                long buyCount = collect.stream().mapToLong(c -> c.getGoodsNum()).sum();
                                if(buyCount > 0){
                                    goodsInfoNest.setBuyCount(buyCount);
                                }
                            }

//                            purchaseList.forEach(goodsInfo->{
//                                log.info("GoodsBaseController.getGoodsByParentId goodsInfoNest.getGoodsInfoId:{} goodsInfo.GoodsInfoId:{}",
//                                        goodsInfoNest.getGoodsInfoId(),goodsInfo.getGoodsInfoId());
//                                if (goodsInfo.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId()) && goodsInfo.getGoodsNum() > 0){
//                                    goodsInfoNest.setBuyCount(goodsInfo.getGoodsNum());
//                                }
//                            });
                        }

                        //设置商品状态
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
                                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                        goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                                    }
                                }
                            }
                        } else {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                        }
                    });
                    //设置企业商品的审核状态 ，以及会员的大客户价
//                    resultGoodsInfos.forEach(goodsInfoNest -> {
//                        if (finalVipPriceFlag
//                                && Objects.nonNull(goodsInfoNest.getVipPrice())
//                                && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
//                            if (goodsInfoNest.getGoodsInfoType() == 1) {
//                                goodsInfoNest.setSalePrice(goodsInfoNest.getMarketPrice());
//                            } else {
//                                goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
//                            }
//                        }
//                    });
                    //填充购物车中的商品数量
//                    resultGoodsInfos.forEach(goodsInfoNest -> {
//                        if (goodsInfoNest != null && purchaseList != null){
//                            purchaseList.forEach(goodsInfo->{
//                                log.info("GoodsBaseController.getGoodsByParentId goodsInfoNest.getGoodsInfoId:{} goodsInfo.GoodsInfoId:{}",
//                                         goodsInfoNest.getGoodsInfoId(),goodsInfo.getGoodsInfoId());
//                                if (goodsInfo.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId()) && goodsInfo.getGoodsNum() > 0){
//                                    goodsInfoNest.setBuyCount(goodsInfo.getGoodsNum());
//                                }
//                            });
//                        }
//                    });
                    //设置商品状态
//                    resultGoodsInfos.forEach(goodsInfoVO -> {
//                        //设置商品状态
//                        if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
//                                && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())) {
//                            goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
//                            // 判断是否有T，如果是1，就设置为2
//                            if (goodsInfoVO.getGoodsInfoName().endsWith("T") || goodsInfoVO.getGoodsInfoName().endsWith("t")) {
//                                if (goodsInfoVO.getStock() < 1) {
//                                    goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
//                                }
//                            } else {
//                                if (goodsInfoVO.getStock() < 1) {
//                                    goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                                }
//                                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
//                                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
//                                                                                   .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
//                                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
//                                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
//                                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                                    }
//                                }
//                            }
//                        } else {
//                            goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
//                        }
////                        log.info("GoodsBaseController.getGoodsByParentId goodsInfoVO:{}",JSONObject.toJSONString(goodsInfoVO));
////                        goodsInfoVO.getMarketingLabels().stream().forEach(marketingLabelVO -> {
////                            log.info("GoodsBaseController.getGoodsByParentId marketingLabelVO:{}",JSONObject.toJSONString(marketingLabelVO));
////                            addActivityRestricted(goodsInfoVO,marketingLabelVO,results);
////                        });
//                    });

//                    esGoods.setGoodsInfos(resultGoodsInfos);
                }
            });
        }
        // 获取直播商品
//        Map<String, Long> liveMapLong = new HashMap<>();
//        List<String> goodsInfoIds = new ArrayList<>();
//        esBaseInfoByParams.getEsGoods().forEach(esGoods -> {
//            goodsInfoIds.addAll(esGoods.getGoodsInfos().stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList()));
//        });
//        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
//            // 根据商品id,查询直播商品的id
//            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
//            // 将数据转成map(商品sku,直播商品名称)
//            if (CollectionUtils.isNotEmpty(liveGoodsVOList)) {
//                liveMapLong = liveGoodsVOList.stream().filter(entity -> {
//                    return entity.getGoodsId() != null;
//                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
//                        LiveGoodsVO::getGoodsId));
//            }
//        }

//        if (!liveMapLong.isEmpty()) {
//
//            // 根据直播房价的id,查询直播信息
//            LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
//            liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
//            liveRoomListReq.setDelFlag(DeleteFlag.NO);
//            List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
//            List<Long> liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(liveRoomVOList)) {
//                final Map<String, Long> liveMap = liveMapLong;
//                esBaseInfoByParams.getEsGoods().forEach(item -> {
//                    item.getGoodsInfos().stream().forEach(goodsInfoNest -> {
//                        Long liveGoodsId = liveMap.get(goodsInfoNest.getGoodsInfoId());
//                        if (liveGoodsId != null) {
//                            if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId)) {
//                                List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId, GoodsLiveRoomVO.class);
//                                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
//                                    for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
//                                        if (liveRoomIdList.contains(liveRoomVO.getRoomId())) {
//                                            goodsInfoNest.setLiveEndTime(liveRoomVO.getLiveEndTime());
//                                            goodsInfoNest.setLiveStartTime(liveRoomVO.getLiveStartTime());
//                                            goodsInfoNest.setRoomId(liveRoomVO.getRoomId());
//                                            break;
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//                    });
//
//                });
//            }
//        }

        Map<GoodsCateNest, List<EsGoods>> nestListMap = esBaseInfoByParams.getEsGoods().stream().collect(Collectors.groupingBy(EsGoods::getGoodsCate));
        ArrayList<GoodsInfoByCateIdResponse> result = Lists.newArrayList();
        nestListMap.forEach((k, es) -> {
            //按商品分类 分组商品
            ArrayList<GoodsNewVo> categoriesZeroList = Lists.newArrayList();
            ArrayList<GoodsNewVo> categoriesOneList = Lists.newArrayList();
            List<GoodsNewVo> goodsData = Lists.newArrayList();
            es.forEach(s -> {
                GoodsNewVo convert = KsBeanUtil.convert(s, GoodsNewVo.class);

                log.info("convert ================================= {}", s.getGoodsInfos());

                int iSQ = convert.getGoodsInfos().get(0).getIsScatteredQuantitative();
                if(Objects.isNull(iSQ) || iSQ == -1){
                    goodsData.add(convert);
                }
                if (iSQ == CategoriesStatus.ZERO.toValue()) {
                    categoriesZeroList.add(convert);
                }
                if (iSQ == CategoriesStatus.ONE.toValue()) {
                    categoriesOneList.add(convert);
                }
            });
            result.add(GoodsInfoByCateIdResponse.builder().goodsCateNest(k).goodsData(goodsData).categoriesOne(categoriesOneList).categoriesZero(categoriesZeroList).build());

        });

        return CateGoodsInfoResponse.builder().goodsInfoByCateIdResponses(result).build();
    }

    /**
     * 查询商品营销信息列表
     *
     * @param request 查询列表
     * @param customer     会员
     * @return 商品营销封装数据
     */
    private List<GoodsInfoVO> getMarketingByParentCateId(GoodsByParentCateIdQueryRequest request, CustomerVO customer,HttpServletRequest httpRequest){
        GoodsInfoQueryRequest queryRequest = KsBeanUtil.convert(request, GoodsInfoQueryRequest.class);
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }
        //如果二级分类为坚果炒货 排序方式为商品编辑排序  暂时写死 后续优化
        if (queryRequest.getCateId().equals(1735L) || queryRequest.getCateId().equals(1733L)){
            queryRequest.setSortFlag(11);
        }
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
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
        /**
         * 请求参数转化
         * @param
         */
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = KsBeanUtil.convert(queryRequest, EsGoodsInfoQueryRequest.class);


        //查询商品分类，0散称，1定量 条件
//        if(Objects.nonNull(queryRequest.getIsScatteredQuantitative())){
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(queryRequest.getIsScatteredQuantitative());
//        }
////        Integer iss = request.getIsScatteredQuantitative();
//
//        if(Objects.isNull(queryRequest.getIsScatteredQuantitative())){
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(-1);
//        }
//
//        if (iss == CategoriesStatus.ZERO.toValue() || iss == CategoriesStatus.ONE.toValue()) {
//            esGoodsInfoQueryRequest.setIsScatteredQuantitative(iss);
//        }

        //此接口不支持关键词搜索
        esGoodsInfoQueryRequest.setIsKeywords(false);

        //获取会员和等级
        esGoodsInfoQueryRequest.setQueryGoods(true);
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        esGoodsInfoQueryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
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
//        EsGoodsResponse response;
        EsSearchResponse esBaseInfoByParams = null;
        //分类查询特定情况，通过分类绑定的品牌进行排序
        if (Objects.nonNull(queryRequest.getCateId())) {
            //查询二级分类下所有三级分类
            List<GoodsCateNewVO> goodsCateNewVOS = JSON.parseArray(goodsCateQueryProvider.getByCacheNew().getContext().getResult(), GoodsCateNewVO.class);

            List<Long> childrenCateIds = goodsCateNewVOS.stream().filter(g -> request.getCateId().equals(g.getCateParentId())).collect(Collectors.toList())
                    .stream().map(c -> c.getCateId()).collect(Collectors.toList());

//            System.out.println("childrenCateIds ======================" + childrenCateIds);

            CateBrandSortRelListRequest cateBrandSortRelListRequest = new CateBrandSortRelListRequest();
            cateBrandSortRelListRequest.setCateIdList(childrenCateIds);
            List<CateBrandSortRelVO> cateBrandSortRelVOList =
                    cateBrandSortRelQueryProvider.list(cateBrandSortRelListRequest).getContext().getCateBrandSortRelVOList();
            //当前三级类目包含的品牌ID
            List<Long> cateBindBrandIds = cateBrandSortRelVOList.stream()
                    .filter(item -> item.getSerialNo() != 0)
                    .sorted(Comparator.comparing(CateBrandSortRelVO::getSerialNo)) //按照排序字段升序排列
                    .map(item -> item.getBrandId()).collect(Collectors.toList());

            esGoodsInfoQueryRequest.setCateBindBrandIds(cateBindBrandIds);
//            esGoodsInfoQueryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
            esGoodsInfoQueryRequest.setCateIds(childrenCateIds);
            esGoodsInfoQueryRequest.setPageNum(0);
            //获取默认最大长度
            esGoodsInfoQueryRequest.setPageSize(10000);
            esBaseInfoByParams = esGoodsInfoElasticService.getEsGoodsInfoByParams(esGoodsInfoQueryRequest);
        }

        if(esBaseInfoByParams.getGoodsData() == null || CollectionUtils.isEmpty(esBaseInfoByParams.getGoodsData())){
            return Lists.newArrayList();
        }

        List<GoodsInfoVO> goodsInfoList = esBaseInfoByParams.getGoodsData().stream().map(EsGoods::getGoodsInfos)
                .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                .collect(Collectors.toList());
        List<String> skuIds =
                goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());


        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        /*CustomerDeliveryAddressResponse deliveryAddress = null;
        if (Objects.nonNull(customer)) {
            deliveryAddress = commonUtil.getDeliveryAddress();
        }*/
        CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);

        //批量查询sku信息
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(
                GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()
        ).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));
        //填充商品持久化信息
        for (GoodsInfoVO goodsInfo : goodsInfoList) {
            GoodsInfoVO goodsInfoVO = goodsInfoMap.getOrDefault(goodsInfo.getGoodsInfoId(), new GoodsInfoVO());
//            if (Objects.nonNull(goodsInfoVO.getMarketingId())) {
//                //活动id
//                goodsInfo.setMarketingId(goodsInfoVO.getMarketingId());
//            }
//            if (Objects.nonNull(goodsInfoVO.getPurchaseNum())) {
//                //活动限购数量
//                goodsInfo.setPurchaseNum(goodsInfoVO.getPurchaseNum());
//            }
//            if (Objects.nonNull(goodsInfoVO.getVirtualStock())){
//                //虚拟库存
//                goodsInfo.setVirtualStock(goodsInfoVO.getVirtualStock());
//            }
        }

        //查询优惠营销信息
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
            PurchaseFillBuyCountRequest requests = new PurchaseFillBuyCountRequest();
            requests.setCustomerId(customer.getCustomerId());
            requests.setGoodsInfoList(goodsInfoList);
            requests.setInviteeId(commonUtil.getPurchaseInviteeId());
//            PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = purchaseProvider.fillBuyCount(requests)
//                                                                                        .getContext();
            PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount(requests)
                    .getContext();
            goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
        }

        //重新赋值于Page内部对象
        Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy
                (GoodsInfoVO::getGoodsId));
        Set<Long> storeIds = new HashSet<>(10);
        for (EsGoods inner : esBaseInfoByParams.getGoodsData()) {
            Set<Long> collect = inner.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId).collect(Collectors.toSet());
            storeIds.addAll(collect);
        }

        //查询是否开启囤货功能
        BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();

        esBaseInfoByParams.getGoodsData().stream().forEach(esGoods -> {
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
//                Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(resultGoodsInfos
//                        .stream().map(GoodsInfoNest::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();
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
//                            calGoodsInfoNestStock(goodsInfoNest,goodsNumsMap);
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
                                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                            goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                                        }
                                    }
                                }

                            } else {
                                goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
                            }

                            goodsInfoNest.setGoodsWareStockVOS(optionalGoodsInfoNest.get().getGoodsWareStockVOS());
                        }
                    }
                    log.info("GoodsBaseController.getMarketingByParentCateId goodsInfoNest:{}",JSONObject.toJSONString(goodsInfoNest));
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
//                        addActivityRestricted(goodsInfoNest,marketingLabelVO,results);
                    });
                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                });
                esGoods.setGoodsInfos(resultGoodsInfos);
            }

        });

        return esBaseInfoByParams.getGoodsData().stream().map(EsGoods::getGoodsInfos)
                .flatMap(Collection::stream).map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class))
                .collect(Collectors.toList());
    }

    /**
     * SPU商品详情
     *
     * @param goodsWareStockRequest 商品skuId
     * @param customer              会员
     * @return SPU商品详情
     */
    private GoodsViewByIdResponse detail(GoodsWareStockRequest goodsWareStockRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        GoodsViewByIdResponse response = goodsDetailBaseInfo(goodsWareStockRequest.getSkuId(), goodsWareStockRequest.getWareId()
                , goodsWareStockRequest.getMatchWareHouseFlag(),goodsWareStockRequest.getParentSkuId());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)
                && !domainInfo.getStoreId().equals(response.getGoods().getStoreId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos().stream()
                .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            response = detailGoodsInfoVOList(response, goodsInfoVOList, customer);
        }

        // 获取直播商品
        Map<String, Long> liveMapLong = new HashMap<>();
        List<String> goodsInfoIds =
                response.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            // 根据商品id,查询直播商品的id
            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
            if (CollectionUtils.isNotEmpty(liveGoodsVOList)) {
                liveMapLong = liveGoodsVOList.stream().filter(entity -> {
                    return entity.getGoodsId() != null;
                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
                        LiveGoodsVO::getGoodsId));
            }
        }
        if (Objects.nonNull(liveMapLong)) {
            final Map<String, Long> liveMap = liveMapLong;

            // 根据直播房价的id,查询直播信息
            LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
            liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
            liveRoomListReq.setDelFlag(DeleteFlag.NO);
            List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
            List<Long> liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(liveRoomVOList)) {
                response.getGoodsInfos().stream().forEach(item -> {
                    Long liveGoodsId = liveMap.get(item.getGoodsInfoId());
                    if (liveGoodsId != null) {
                        if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId)) {
                            List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId, GoodsLiveRoomVO.class);
                            if (CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
                                for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
                                    if (liveRoomIdList.contains(liveRoomVO.getRoomId())) {
                                        item.setLiveEndTime(liveRoomVO.getLiveEndTime());
                                        item.setLiveStartTime(liveRoomVO.getLiveStartTime());
                                        item.setRoomId(liveRoomVO.getRoomId());
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    List<GoodsLabelVO> collect = item.getGoodsLabels().stream()
                            .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                            .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                            .collect(Collectors.toList());
                    item.setGoodsLabels(collect);
                });
            }
        }
        List<GoodsLabelVO> collect = response.getGoods().getGoodsLabels().stream()
                .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                .collect(Collectors.toList());
        response.getGoods().setGoodsLabels(collect);
        //查询是否开启囤货功能
//        BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();

        //商品信息
        GoodsVO goodsSubtitle = response.getGoods();
        //计算到手价
        response.getGoodsInfos().stream().forEach(goodsInfos -> {
            //填充套装活动id
            List<MarketingSuitDetialVO> suitToBuyByGoodInfoIds = marketingQueryProvider.getSuitToBuyByGoodInfoIds(MarketingMapGetByGoodsIdRequest.builder()
                    .goodsInfoIdList(Arrays.asList(goodsInfos.getGoodsInfoId())).build()).getContext();
            if (CollectionUtils.isNotEmpty(suitToBuyByGoodInfoIds)) {
                goodsInfos.setIsSuitToBuy(DefaultFlag.YES);
                goodsInfos.setSuitMarketingId(suitToBuyByGoodInfoIds.get(0).getMarketingId());
                //填充套装活动所有商品信息
                MarketingQueryByIdsRequest queryByIdsRequest = new MarketingQueryByIdsRequest();
                queryByIdsRequest.setMarketingId(goodsInfos.getSuitMarketingId());
                List<MarketingSuitDetialVO> suitDetialVOS = marketingQueryProvider.getSuitDetialByMarketingIds(queryByIdsRequest).getContext();
                if (CollectionUtils.isEmpty(suitDetialVOS)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                List<String> suitSkuIds = suitDetialVOS.stream().map(MarketingSuitDetialVO::getGoodsInfoId).collect(Collectors.toList());
                List<GoodsInfoVO> suitGoodsList = goodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder()
                        .goodsInfoIds(suitSkuIds).build()).getContext().getGoodsInfos();
                goodsInfos.setSuitGoodsList(suitGoodsList);
            } else {
                goodsInfos.setIsSuitToBuy(DefaultFlag.NO);
            }
            //填充本品价格
            if (Objects.nonNull(goodsInfos.getIsSuitGoods()) && goodsInfos.getIsSuitGoods().equals(DefaultFlag.YES)
                    && Objects.nonNull(goodsInfos.getChoseProductSkuId())) {
                GoodsInfoVO productGoodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                        .goodsInfoId(goodsInfos.getChoseProductSkuId()).matchWareHouseFlag(goodsWareStockRequest.getMatchWareHouseFlag())
                        .build()).getContext();
                if (Objects.nonNull(productGoodsInfo)) {
                    goodsInfos.setProductMarketPrice(productGoodsInfo.getMarketPrice());
                } else {
                    throw new SbcRuntimeException(GoodsErrorCode.GOODS_PRODUCT_NOT_EXISTS_FOR_QUERY);
                }
            }

            //如果商品有任何营销活动信息 则vip价格不参与 前端不展示（传0元）
            if (CollectionUtils.isNotEmpty(goodsInfos.getMarketingLabels())) {
                goodsInfos.setVipPrice(BigDecimal.ZERO);
            }

            //计算到手价
            this.calTheirPrice(goodsInfos);

            goodsInfos.getMarketingLabels().stream().forEach(marketingLabelVO -> {
                //活动限购库存
//                if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
//                        && marketingLabelVO.getGoodsPurchasingNumber() > 0
//                        && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfos.getStock()) < 0) {
//                    goodsInfos.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
//                }
                //满赠活动，带出赠品信息
                if (Objects.nonNull(marketingLabelVO.getMarketingType()) && marketingLabelVO.getMarketingType() == 2) {
                    FullGiftLevelListByMarketingIdAndCustomerRequest fullgiftRequest = FullGiftLevelListByMarketingIdAndCustomerRequest
                            .builder().build();
                    if (Objects.nonNull(customer)){
                        fullgiftRequest.setCustomer(KsBeanUtil.convert(commonUtil.getCustomer(), CustomerDTO.class));
                        fullgiftRequest.setMatchWareHouseFlag(false);
                    }
                    fullgiftRequest.setMarketingId(marketingLabelVO.getMarketingId());
                    List<GoodsInfoVO> giftList = fullGiftQueryProvider.listGiftByMarketingIdAndCustomer(fullgiftRequest).getContext().getGiftList();
                    //排序
                    if (CollectionUtils.isNotEmpty(giftList)){
                        giftList=giftList.stream().sorted(Comparator.comparing(GoodsInfoVO::getGoodsStatus).thenComparing(GoodsInfoVO::getMarketPrice,Comparator.reverseOrder())).collect(Collectors.toList());

                    }
                    goodsInfos.setGiftList(giftList);
                }

                //修改副标题(如果到手价不为空已到手价计算)
                if(Objects.nonNull(goodsInfos.getTheirPrice()) && StringUtils.isNotEmpty(goodsSubtitle.getGoodsSubtitle())){
                    //商品详情计算副标题
                    this.setGoodsSubtitle(goodsInfos,goodsSubtitle,goodsInfos.getTheirPrice());
                }else if (Objects.nonNull(customer) && Objects.nonNull(customer.getVipFlag()) && customer.getVipFlag().equals(DefaultFlag.YES)
                        && Objects.nonNull(goodsInfos.getVipPrice()) && goodsInfos.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //商品详情计算副标题（如果到手价为空，判断是否有大客户价；否则用原价显示副标题）
                    this.setGoodsSubtitle(goodsInfos,goodsSubtitle,goodsInfos.getVipPrice());
                }

//                addDetailsActivityRestricted(goodsInfos,marketingLabelVO,results);
            });

            if (CollectionUtils.isNotEmpty(goodsInfos.getMarketingLabels())) {
                goodsInfos.setVipPrice(BigDecimal.ZERO);
            }

            //如果没有营销活动计算修改副标题
            if (CollectionUtils.isEmpty(goodsInfos.getMarketingLabels())) {
                if (Objects.nonNull(customer) && Objects.nonNull(customer.getVipFlag()) && customer.getVipFlag().equals(DefaultFlag.YES)
                        && Objects.nonNull(goodsInfos.getVipPrice()) && goodsInfos.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                    //商品详情计算副标题
                    this.setGoodsSubtitle(goodsInfos,goodsSubtitle,goodsInfos.getVipPrice());
                }
            }

            //通过商品指定区域销售设置商品状态
            //获取客户收货地址
            if(Objects.nonNull(commonUtil.getCustomer())){
                CustomerDeliveryAddressResponse deliveryAddress =  commonUtil.getProvinceCity(httpRequest);
                if(Objects.nonNull(deliveryAddress)){
                    if (StringUtils.isNotBlank(goodsInfos.getAllowedPurchaseArea())) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfos.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                            goodsInfos.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                }
            }
        });
        return response;
    }


    /**
     * 计算到手价
     * @param goodsInfoVO
     */
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
                                theirPriceFullCount = levelLabelVO.getAmount()
                                        .divide(goodsInfoVO.getMarketPrice(), 5, BigDecimal.ROUND_HALF_UP)
                                        .setScale(0, BigDecimal.ROUND_UP);
                                discount = levelLabelVO.getReduction()
                                        .divide(theirPriceFullCount, 2, BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            } else if (marketingLabelVO.getSubType().equals(1)) { //满数量
                                theirPriceFullCount = BigDecimal.valueOf(levelLabelVO.getNumber());
                                discount = levelLabelVO.getReduction()
                                        .divide(theirPriceFullCount, 2, BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
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
                                theirPriceFullCountForDiscount = levelLabelVO.getAmount()
                                        .divide(goodsInfoVO.getMarketPrice(), 5, BigDecimal.ROUND_HALF_UP)
                                        .setScale(0, BigDecimal.ROUND_UP);
                                totalPrice = goodsInfoVO.getMarketPrice()
                                        .multiply(theirPriceFullCountForDiscount).setScale(5, BigDecimal.ROUND_HALF_UP);
                                //折扣后单价即到手价
                                discount = totalPrice.multiply(levelLabelVO.getDiscount()
                                        .divide(new BigDecimal(10), 2, BigDecimal.ROUND_HALF_UP))
                                        .divide(theirPriceFullCountForDiscount, 2, BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            } else if (marketingLabelVO.getSubType().equals(3)) { //满数量
                                theirPriceFullCountForDiscount = BigDecimal.valueOf(levelLabelVO.getNumber());
                                totalPrice = goodsInfoVO.getMarketPrice()
                                        .multiply(theirPriceFullCountForDiscount)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                                discount = totalPrice.multiply(levelLabelVO.getDiscount()
                                        .divide(new BigDecimal(10), 2, BigDecimal.ROUND_HALF_UP))
                                        .divide(theirPriceFullCountForDiscount, 2, BigDecimal.ROUND_HALF_UP)
                                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                            goodsInfoVO.setTheirPrice(discount);
                        }

                    });
                }
            });
        }
    }

    /**
     * 计算到手价
     * @param goodsInfoVO
     */
    private void calTheirPrice(GoodsInfoVO goodsInfoVO) {
        if (CollectionUtils.isNotEmpty(goodsInfoVO.getMarketingLabels())) {
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

    /**
     * 商品详情计算副标题
     * @param goodsInfoVO sku信息
     * @param goodsVO spu信息
     * @param calPrice 计算金额
     */
    private void setGoodsSubtitle(GoodsInfoVO goodsInfoVO,GoodsVO goodsVO,BigDecimal calPrice){
        String replace = goodsVO.getGoodsSubtitle();
        try{
            String subTitlePrice =  goodsVO.getGoodsSubtitle().split("x")[1];
            subTitlePrice =  subTitlePrice.split("元")[0];
            //修改副标题
            BigDecimal goodsScale = calPrice.divide(goodsInfoVO.getAddStep(),2, BigDecimal.ROUND_HALF_UP);
            replace = StringUtils.replace(goodsVO.getGoodsSubtitle(), subTitlePrice, String.valueOf(goodsScale));
        }catch (Exception e){
            replace = goodsVO.getGoodsSubtitle();
            log.info("商品详情计算副标题常出现异常---goodsVO.getGoodsSubtitle()>"+goodsVO.getGoodsSubtitle());
            log.info("商品详情计算副标题常出现异常--->"+JSONObject.toJSONString(goodsVO));
        }
        goodsVO.setGoodsSubtitle(replace);
       /* String subTitlePrice = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(goodsVO.getGoodsSubtitle(), "元"), "=");
        //修改副标题
        BigDecimal goodsScale = calPrice.divide(goodsInfoVO.getAddStep(),2, BigDecimal.ROUND_HALF_UP);
        Matcher isNum = NUMBER_PATTERN.matcher(subTitlePrice);
        String replace = goodsVO.getGoodsSubtitle();
        if(isNum.matches()){
            //纯数字
            replace = StringUtils.replace(goodsVO.getGoodsSubtitle(), subTitlePrice, String.valueOf(goodsScale));
        }else{
            //不是纯数字
            replace = StringUtils.appendIfMissingIgnoreCase(replace,
                    "=" + String.valueOf(goodsScale) + "元/"
                            + goodsVO.getGoodsSubtitle()
                            .substring(goodsVO.getGoodsSubtitle().length()-1,
                                    goodsVO.getGoodsSubtitle().length()));
        }
        goodsVO.setGoodsSubtitle(replace);
        */
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
            //根据开关重新设置分销商品标识
//            distributionService.checkDistributionSwitch(goodsInfoVOList);
//            String customerId = null;
//            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
//                customerId = customer.getCustomerId();
//            }
            //计算区间价
//            GoodsIntervalPriceByGoodsAndSkuResponse priceResponse =
//                    goodsIntervalPriceService.getGoodsAndSku(goodsInfoVOList,
//                            Collections.singletonList(response.getGoods()), customerId);
//            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
//            goodsInfoVOList = priceResponse.getGoodsInfoVOList();

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

                    if(Objects.nonNull(goodsInfoVO.getMarketingLabels())){
                        goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
                            String desc = marketingLabelVO.getMarketingDesc();
                            List<String> descList = marketingLabelVO.getMarketingDescList();
                            if(Objects.nonNull(desc) && desc.indexOf("（") != -1){
                                String newDesc = "跨单品"+desc.substring(0, desc.indexOf("（"));
                                marketingLabelVO.setMarketingDesc(newDesc);
                                if (Objects.nonNull(descList)){
                                    List<String> newDescList = new ArrayList<>();
                                    descList.forEach( s -> {
                                        newDescList.add("跨单品"+s.substring(0,s.indexOf("（")));
                                    });
                                    marketingLabelVO.setMarketingDescList(newDescList);
                                }
                            }
                            if (Objects.equals(marketingLabelVO.getMarketingType(), 7)) {
                                marketingLabelVO.setMarketingDesc("您已满足" + marketingLabelVO.getMarketingDesc());
                            }
                        });
                    }

                    if (Objects.nonNull(customer)
                            && null != customer.getEnterpriseStatusXyy()
                            && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                        //特价商品销售价取市场价
                        if (goodsInfoVO.getGoodsInfoType() == 1) {
                            goodsInfoVO.setSalePrice(goodsInfoVO.getMarketPrice());
                        } else {
                            goodsInfoVO.setSalePrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                        }
                        goodsInfoVO.setEnterpriseStatusXyy(customer.getEnterpriseStatusXyy().toValue());
                        goodsInfoVO.setMarketPrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
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
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).build()).getContext();

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
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(distributionCacheService.queryStoreOpenFlag
                (String.valueOf(goodsInfo.getStoreId()))) || !DistributionGoodsAudit.CHECKED.equals(goodsInfo
                .getDistributionGoodsAudit())) {
            response.setDistributionGoods(Boolean.FALSE);
        } else {
            response.setDistributionGoods(Boolean.TRUE);
        }

        //查询是否开启囤货功能
        /**BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();
         //通过商品指定区域销售设置商品状态
         //获取客户收货地址
         if(Objects.nonNull(commonUtil.getCustomer())){
         CustomerDeliveryAddressResponse deliveryAddress =  commonUtil.getDeliveryAddress();
         if(Objects.nonNull(deliveryAddress)){
         response.getGoodsInfos().forEach(goodsInfoVO -> {
         if (StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
         List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
         .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
         //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
         if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
         goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
         }
         }
         log.info("GoodsBaseController.goodsDetailBaseInfo goodsInfoVO:{}",JSONObject.toJSONString(goodsInfoVO));
         goodsInfoVO.getMarketingLabels().stream().forEach(marketingLabelVO -> {
         addDetailsActivityRestricted(goodsInfoVO,marketingLabelVO,results);
         });
         });
         }
         }*/

        return response;
    }
    private GoodsViewByIdResponse goodsDetailBaseInfo(String skuId, Long wareId, Boolean matchWareHouseFlag, String parentSkuId) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).wareId(wareId).matchWareHouseFlag(matchWareHouseFlag).parentGoodsInfoId(parentSkuId).build()).getContext();

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
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(distributionCacheService.queryStoreOpenFlag
                (String.valueOf(goodsInfo.getStoreId()))) || !DistributionGoodsAudit.CHECKED.equals(goodsInfo
                .getDistributionGoodsAudit())) {
            response.setDistributionGoods(Boolean.FALSE);
        } else {
            response.setDistributionGoods(Boolean.TRUE);
        }

        return response;
    }


    /**
     * 店铺精选页进入-商品详情页
     *
     * @param skuId
     * @param skuIds
     * @return
     */
    private GoodsViewByIdAndSkuIdsResponse goodsDetailBaseInfo(String skuId, List<String> skuIds,
                                                               String distributorId) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).build
                ()).getContext();

        if (Objects.isNull(goodsInfo)
                || Objects.equals(DeleteFlag.YES, goodsInfo.getDelFlag())
                || (!Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())) || DefaultFlag.NO.equals
                (distributionCacheService.queryStoreOpenFlag(String.valueOf(goodsInfo.getStoreId())))) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        if (AddedFlag.NO.toValue() == goodsInfo.getAddedFlag()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_ADDEDFLAG);
        }

        if (!DistributionGoodsAudit.CHECKED.equals(goodsInfo.getDistributionGoodsAudit())) {
            skuIds = skuIds.stream().filter(goodsInfoId -> !goodsInfoId.equals(skuId)).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(skuIds)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        GoodsViewByIdAndSkuIdsRequest request = new GoodsViewByIdAndSkuIdsRequest();
        request.setGoodsId(goodsInfo.getGoodsId());
        request.setSkuIds(skuIds);
        GoodsViewByIdAndSkuIdsResponse goodsViewByIdAndSkuIdsResponse = goodsQueryProvider.getViewByIdAndSkuIds
                (request).getContext();
        if (CollectionUtils.isEmpty(goodsViewByIdAndSkuIdsResponse.getGoodsInfos())) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        if (StringUtils.isNotBlank(distributorId)) {
            BaseResponse<DistributorLevelByCustomerIdResponse> baseResponse =
                    distributionService.getByCustomerId(distributorId);
            DistributorLevelVO distributorLevelVO = Objects.isNull(baseResponse) ? null :
                    baseResponse.getContext().getDistributorLevelVO();
            goodsViewByIdAndSkuIdsResponse.getGoodsInfos().forEach(goodsInfoVO -> {
                if (Objects.nonNull(distributorLevelVO) && Objects.nonNull(distributorLevelVO.getCommissionRate()) && DistributionGoodsAudit.CHECKED == goodsInfoVO.getDistributionGoodsAudit()) {
                    goodsInfoVO.setDistributionCommission(distributionService.calDistributionCommission(goodsInfoVO.getDistributionCommission(), distributorLevelVO.getCommissionRate()));
                }
            });
        }
        return goodsViewByIdAndSkuIdsResponse;
    }


    /**
     * 获取某个商品的小程序码
     *
     * @return
     */
    @ApiOperation(value = "获取某个商品的小程序码")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/getSkuQrCode", method = RequestMethod.POST)
    public BaseResponse<String> getSkuQrCode(@RequestBody ShareMiniProgramRequest request) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(
                GoodsInfoByIdRequest.builder().goodsInfoId(request.getSkuId()).build()).getContext();
        if (Objects.isNull(goodsInfo)
                || Objects.equals(DeleteFlag.YES, goodsInfo.getDelFlag())
                || (!Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()))) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Boolean saasStatus = Objects.nonNull(domainInfo);
        if (saasStatus) {
            if ((!Objects.equals(domainInfo.getStoreId(), goodsInfo.getStoreId()))) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
            }
        }

        if (AddedFlag.NO.toValue() == goodsInfo.getAddedFlag()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_ADDEDFLAG);
        }

        // 新增判断条件，未登录情况下取数据库小程序码的oss地址，如果当前用户已登录，重新生成
        if (Objects.isNull(request.getShareUserId())) {
            if (!saasStatus
                    && StringUtils.isNotBlank(goodsInfo.getSmallProgramCode())) {
                return BaseResponse.success(goodsInfo.getSmallProgramCode());
            }
            //没有，重新生成
            MiniProgramQrCodeRequest miniProgramQrCodeRequest = new MiniProgramQrCodeRequest();
            miniProgramQrCodeRequest.setPage("pages/sharepage/sharepage");
            miniProgramQrCodeRequest.setScene(request.getSkuId());
            if (saasStatus) {
                miniProgramQrCodeRequest.setSaasStatus(Boolean.TRUE);
                miniProgramQrCodeRequest.setStoreId(domainInfo.getStoreId());
            }
            String codeUrl = wechatAuthProvider.getWxaCodeUnlimit(miniProgramQrCodeRequest).getContext().toString();
            //更新字段
            if (StringUtils.isNotBlank(codeUrl)) {
                if (!saasStatus) {
                    GoodsInfoSmallProgramCodeRequest goodsInfoSmallProgramCodeRequest = new GoodsInfoSmallProgramCodeRequest();
                    goodsInfoSmallProgramCodeRequest.setGoodsInfoId(request.getSkuId());
                    goodsInfoSmallProgramCodeRequest.setCodeUrl(codeUrl);
                    BaseResponse response = goodsInfoProvider.updateSkuSmallProgram(goodsInfoSmallProgramCodeRequest);
                    if (response.getCode().equals(BaseResponse.SUCCESSFUL().getCode())) {
                        return BaseResponse.success(codeUrl);
                    }
                }
            }
            return BaseResponse.success(codeUrl);
        } else {
            if (saasStatus) {
                request.setSaasStatus(Boolean.TRUE);
                request.setStoreId(domainInfo.getStoreId());
            }
            return wechatAuthProvider.getMiniProgramQrCodeWithShareUserId(request);
        }

    }


    /**
     * 拼团-进入商品详情
     */
    @ApiOperation(value = "拼团-进入商品详情")
    @RequestMapping(value = "/groupon/goods-detail/spu/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GrouponGoodsViewByIdResponse> grouponGoodsDetailLogin(@PathVariable String skuId, @RequestParam Long wareId) {
        //拼团业务信息
        GrouponDetailQueryRequest grouponDetailQueryRequest = GrouponDetailQueryRequest.builder().
                optType(GrouponDetailOptType.GROUPON_GOODS_DETAIL)
                .leader(Boolean.TRUE).goodsInfoId(skuId).wareId(wareId).build();

        return grouponGoodsDetail(grouponDetailQueryRequest);
    }


    /**
     * 拼团-进入商品详情-未登录
     */
    @ApiOperation(value = "拼团-进入商品详情")
    @RequestMapping(value = "/unLogin/groupon/goods-detail/spu/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GrouponGoodsViewByIdResponse> grouponGoodsDetailUnLogin(
            @PathVariable String skuId) {
        //拼团业务信息
        GrouponDetailQueryRequest grouponDetailQueryRequest = GrouponDetailQueryRequest.builder().optType
                (GrouponDetailOptType
                        .GROUPON_GOODS_DETAIL)
                .leader(Boolean.TRUE).goodsInfoId(skuId).build();
        return grouponGoodsDetail(grouponDetailQueryRequest);
    }

    /**
     * 拼团-进入拼团详情页-未登录
     */
    @ApiOperation(value = "拼团-进入拼团详情")
    @RequestMapping(value = "/unLogin/groupon/groupon-detail/{grouponNo}", method = RequestMethod.GET)
    public BaseResponse<GrouponGoodsViewByIdResponse> grouponDetailByGrouponNoUnLogin(
            @PathVariable String grouponNo) {
        //拼团业务信息
        GrouponDetailQueryRequest grouponDetailWithGoodsRequest = GrouponDetailQueryRequest.builder().optType
                (GrouponDetailOptType.GROUPON_JOIN).grouponNo(grouponNo).build();
        return grouponGoodsDetailByGrouponNo(grouponDetailWithGoodsRequest);
    }

    /**
     * 拼团-进入拼团详情页-登录
     */
    @ApiOperation(value = "拼团-进入拼团详情")
    @RequestMapping(value = "/groupon/groupon-detail/{grouponNo}", method = RequestMethod.GET)
    public BaseResponse<GrouponGoodsViewByIdResponse> grouponDetailByGrouponNo(
            @PathVariable String grouponNo) {
        //拼团业务信息
        GrouponDetailQueryRequest grouponDetailWithGoodsRequest = GrouponDetailQueryRequest.builder().optType
                (GrouponDetailOptType.GROUPON_JOIN).grouponNo(grouponNo).build();
        return grouponGoodsDetailByGrouponNo(grouponDetailWithGoodsRequest);
    }

    /**
     * 1.根据商品获取团信息
     * 2.查询商品信息
     * 3.根据拼团活动设置商品信息
     *
     * @param grouponDetailQueryRequest
     * @return
     */
    private BaseResponse<GrouponGoodsViewByIdResponse> grouponGoodsDetail(GrouponDetailQueryRequest
                                                                                  grouponDetailQueryRequest) {
        // 用户信息
        CustomerVO customer = Objects.nonNull(commonUtil.getOperatorId()) ? commonUtil.getCustomer() : null;
        //spu
        GoodsViewByIdResponse response = goodsDetailBaseInfo(grouponDetailQueryRequest.getGoodsInfoId()
                , grouponDetailQueryRequest.getWareId(), null);
        //验证分销商品
        if (response.getDistributionGoods()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos().stream()
                .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                .collect(Collectors.toList());
        // 查询skus信息
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            response = detailGoodsInfoVOList(response, response.getGoodsInfos(), customer);
        }

        //过滤分销商品
        goodsInfoVOList = response.getGoodsInfos().stream().filter(g -> !DistributionGoodsAudit.CHECKED.equals(g
                .getDistributionGoodsAudit())).collect(Collectors.toList());

        //拼团信息
        grouponDetailQueryRequest.setGoodsId(response.getGoods().getGoodsId());
        grouponDetailQueryRequest.setCustomerId(Objects.isNull(customer) ? null : customer.getCustomerId());
        GrouponDetailQueryResponse grouponDetailQueryResponse = grouponProvider
                .getGrouponDetail(grouponDetailQueryRequest)
                .getContext();
        //sku-spu-skus
        List<GrouponGoodsInfoVO> grouponGoodsInfoVOList = grouponDetailQueryResponse.getGrouponDetail()
                .getGrouponGoodsInfos();
        List<String> skuIds = grouponGoodsInfoVOList.stream().map(GrouponGoodsInfoVO::getGoodsInfoId).collect
                (Collectors.toList());

        //过滤拼团商品
        goodsInfoVOList = goodsInfoVOList.stream()
                .filter(g -> skuIds.contains(g.getGoodsInfoId())).filter(g -> !DistributionGoodsAudit.CHECKED.equals
                        (g.getDistributionGoodsAudit()))
                .collect(Collectors.toList());
        response.setGoodsInfos(goodsInfoVOList);
        //商品是否存在
        if (CollectionUtils.isEmpty(goodsInfoVOList)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        //以上为商品信息
        return BaseResponse.success(wrapeGrouponGoodsViewByIdResponse(response, grouponDetailQueryResponse));
    }

    /**
     * 1.根据团编号获取团信息
     * 2.查询商品信息
     * 3.根据拼团活动设置商品信息
     *
     * @param grouponDetailQueryRequest
     * @return
     */
    private BaseResponse<GrouponGoodsViewByIdResponse> grouponGoodsDetailByGrouponNo(GrouponDetailQueryRequest
                                                                                             grouponDetailQueryRequest) {
        // 用户信息
        CustomerVO customer = Objects.nonNull(commonUtil.getOperatorId()) ? commonUtil.getCustomer() : null;
        //拼团信息
        grouponDetailQueryRequest.setCustomerId(Objects.isNull(customer) ? null : customer.getCustomerId());
        GrouponDetailQueryResponse grouponDetailQueryResponse = grouponProvider
                .getGrouponDetail(grouponDetailQueryRequest)
                .getContext();
        //根据活动商品筛选sku
        List<GrouponGoodsInfoVO> grouponGoodsInfoVOList = grouponDetailQueryResponse.getGrouponDetail()
                .getGrouponGoodsInfos();
        List<String> skuIds = grouponGoodsInfoVOList.stream().map(GrouponGoodsInfoVO::getGoodsInfoId).collect
                (Collectors.toList());

        if (CollectionUtils.isEmpty(skuIds)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //团详情页面根据groupon反查sku
        GoodsViewByIdResponse response = goodsDetailBaseInfo(skuIds.get(0), null, true);
        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos().stream()
                .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                .collect(Collectors.toList());
        // 查询skus信息
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            response = detailGoodsInfoVOList(response, goodsInfoVOList, customer);
        }
        //判断活动是否结束
        if (!GrouponDetailOptStatus.ACTIVITY_END.equals(grouponDetailQueryResponse.getGrouponDetail()
                .getGrouponDetailOptStatus())) {
            //过滤分销商品
            goodsInfoVOList = response.getGoodsInfos().stream().filter(g -> !DistributionGoodsAudit.CHECKED.equals(g
                    .getDistributionGoodsAudit())).collect(Collectors.toList());
            //过滤拼团商品
            goodsInfoVOList = goodsInfoVOList.stream()
                    .filter(g -> skuIds.contains(g.getGoodsInfoId())).filter(g -> !DistributionGoodsAudit.CHECKED.equals
                            (g.getDistributionGoodsAudit()))
                    .collect(Collectors.toList());
            response.setGoodsInfos(goodsInfoVOList);

        }
        //商品是否存在
        if (CollectionUtils.isEmpty(goodsInfoVOList)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        //以上为商品信息
        return BaseResponse.success(wrapeGrouponGoodsViewByIdResponse(response, grouponDetailQueryResponse));
    }

    /**
     * 商品信息处理拼团信息
     * 起订限定量
     *
     * @param response
     * @param grouponDetailQueryResponse
     * @return
     */
    private GrouponGoodsViewByIdResponse wrapeGrouponGoodsViewByIdResponse(GoodsViewByIdResponse response,
                                                                           GrouponDetailQueryResponse
                                                                                   grouponDetailQueryResponse) {
        GrouponDetailVO grouponDetail = grouponDetailQueryResponse.getGrouponDetail();
        // 用户信息
        CustomerVO customer = Objects.nonNull(commonUtil.getOperatorId()) ? commonUtil.getCustomer() : null;

        //商品处理拼团信息
        GrouponDetailWithGoodsRequest grouponDetailWithGoodsRequest = new GrouponDetailWithGoodsRequest();
        grouponDetailWithGoodsRequest.setGrouponActivity(grouponDetail
                .getGrouponActivity());
        grouponDetailWithGoodsRequest.setGoodsInfoList(response.getGoodsInfos());
        grouponDetailWithGoodsRequest.setGrouponGoodsInfoVOList(grouponDetail
                .getGrouponGoodsInfos());
        grouponDetailWithGoodsRequest.setCustomerId(Objects.isNull(customer) ? null : customer.getCustomerId());
        GrouponDetailWithGoodsResponse grouponDetailWithGoodsResponse = grouponProvider
                .getGrouponDetailWithGoodsInfos(grouponDetailWithGoodsRequest)
                .getContext();
        //返回拼团信息
        GrouponGoodsViewByIdResponse grouponGoodsViewByIdResponse = KsBeanUtil.convert(response,
                GrouponGoodsViewByIdResponse.class);
        //精简返回页面的数据
        GrouponDetailWithGoodsVO grouponDetailWithGoodsVO = KsBeanUtil.convert(grouponDetail, GrouponDetailWithGoodsVO
                .class);

        //参团商品sku优先显示为团长开团的sku
        if (Objects.nonNull(grouponDetail.getTradeInGroupon())) {
            grouponDetailWithGoodsVO.setGoodInfoId(grouponDetail.getTradeInGroupon().getTradeGroupon().getGoodInfoId());
            grouponDetailWithGoodsVO.setGroupCustomerId(grouponDetail.getTradeInGroupon().getBuyer().getId());
        }
        grouponGoodsViewByIdResponse.setGrouponDetails(grouponDetailWithGoodsVO);
        grouponGoodsViewByIdResponse.setGoodsInfos(grouponDetailWithGoodsResponse.getGoodsInfoVOList());
        grouponGoodsViewByIdResponse.setGrouponInstanceList(grouponDetail.getGrouponInstanceList());


        return grouponGoodsViewByIdResponse;
    }


    /**
     * 查询商品图文信息和属性
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品图文信息和属性")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId",
            required = true)
    @RequestMapping(value = "/goodsDetailProper/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsDetailProperResponse> goodsDetailProper(@PathVariable String skuId) {
        GoodsDetailProperBySkuIdRequest request = new GoodsDetailProperBySkuIdRequest();
        request.setSkuId(skuId);
        return BaseResponse.success(goodsQueryProvider.getGoodsDetail(request).getContext());
    }


    /**
     * 查询商品页面展示简易信息
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品页面展示简易信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId",
            required = true)
    @RequestMapping(value = "/goodsDetailSimple/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsDetailSimpleResponse> goodsDetailSimple(@PathVariable String skuId) {
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).build
                ()).getContext();

        if (Objects.isNull(goodsInfo) || (!Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()))) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        GoodsDetailSimpleRequest request = new GoodsDetailSimpleRequest();
        request.setGoodsId(goodsInfo.getGoodsId());
        GoodsDetailSimpleResponse response = goodsQueryProvider.getGoodsDetailSimple(request).getContext();
        return BaseResponse.success(response);
    }

    /**
     * 查询商品品牌信息
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品品牌信息")
    @RequestMapping(value = "/getBrandByCateId/{cateId}", method = RequestMethod.GET)
    public BaseResponse<BrandsResponse> getBrandByCateId(@PathVariable long cateId) {
        EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
        request.setCateId(cateId);
        List<GoodsBrandVO> brandVOList = esGoodsInfoElasticService.getEsBaseInfoByCateId(request);
        if(CollectionUtils.isEmpty(brandVOList)){
            return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(brandVOList).build());
        }

        List<Long> brandIds = brandVOList.stream().map(GoodsBrandVO::getBrandId).collect(Collectors.toList());
        //获取有上架商品的品牌集合
        List<Long> brandIdsHasAddedSku = goodsInfoQueryProvider.findBrandsHasAddedSku(FindBrandsHasAddedSkuRequest.builder()
                .brandIds(brandIds).wareId(commonUtil.getWareId(HttpUtil.getRequest())).build()).getContext();
        //过滤出有上架商品的品牌
        List<GoodsBrandVO> brandVOListHasAddedSku = brandVOList.stream()
                .filter(item -> brandIdsHasAddedSku.contains(item.getBrandId()))
                .collect(Collectors.toList());
        return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(brandVOListHasAddedSku).build());
    }

    /**
     * 查询商品品牌信息
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品品牌信息")
    @RequestMapping(value = "/getBrandByStoreCateId/by-store", method = RequestMethod.POST)
    public BaseResponse<BrandsResponse> getBrandByStoreCateIdAndStore(@RequestBody GoodsBrandsQueryByStoreCatIdAndStoreRequest queryRequest, HttpServletRequest httpRequest) {
        EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
        List<Long> storeCatIds = wrapStoreCatId(queryRequest.getStoreId(), queryRequest.getStoreCatId());
        request.setStoreCateIds(storeCatIds);
        request.setStoreId(queryRequest.getStoreId());
        if (Objects.equals(queryRequest.getStoreCatId(), -1L)) {
            final AppGoodsQueryRequest appGoodsQueryRequest = new AppGoodsQueryRequest();
            appGoodsQueryRequest.setStoreId(queryRequest.getStoreId());
            String customerId = commonUtil.getOperator() == null || commonUtil.getOperator().getUserId() == null ? "-1" : commonUtil.getOperator().getUserId();
            appGoodsQueryRequest.setCustomerId(customerId);
            final List<String> goodsInfoIdsList = merchantConfigGoodsQueryProvider.getAppGoodsInfo(appGoodsQueryRequest).getContext().getGoodsInfoIds();
            if (CollectionUtils.isNotEmpty(goodsInfoIdsList)) {
                request.setGoodsInfoIds(goodsInfoIdsList);
            }
        }
        List<GoodsBrandVO> brandVOList = esGoodsInfoElasticService.getEsBaseInfoByCateId(request);
        if(CollectionUtils.isEmpty(brandVOList)){
            return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(brandVOList).build());
        }
        List<Long> brandIds = brandVOList.stream().map(GoodsBrandVO::getBrandId).collect(Collectors.toList());
        //获取有上架商品的品牌集合
        List<Long> brandIdsHasAddedSku = goodsInfoQueryProvider.findBrandsHasAddedSku(FindBrandsHasAddedSkuRequest.builder()
                .brandIds(brandIds).wareId(commonUtil.getWareId(HttpUtil.getRequest())).build()).getContext();
        //过滤出有上架商品的品牌
        List<GoodsBrandVO> brandVOListHasAddedSku = brandVOList.stream()
                .filter(item -> brandIdsHasAddedSku.contains(item.getBrandId()))
                .collect(Collectors.toList());
        return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(brandVOListHasAddedSku).build());
    }

    private List<Long> wrapStoreCatId(Long storeId,Long storeCatId) {
//        Set<String> rIds = getRecommend(storeId, storeCatId);
        // 获取所有分类
        final StoreCateListByStoreIdRequest storeIdRequest = new StoreCateListByStoreIdRequest();
        storeIdRequest.setStoreId(storeId);
        final BaseResponse<StoreCateListByIdsResponse> baseResponse = storeCateQueryProvider.listTreeByStoreId(storeIdRequest);
        final List<StoreCateVO> storeCateResponseVOList = baseResponse.getContext().getStoreCateVOList();
        Set<Long> allIds = Sets.newHashSet();
        storeCateResponseVOList.forEach(one -> {
            if (CollectionUtils.isEmpty(one.getStoreCateList())) return;
            one.getStoreCateList().forEach(two -> {
                final Long twoStoreCateId = two.getStoreCateId();
//                if (Objects.equals(Objects.toString(storeCatId), "-1") && rIds.contains(Objects.toString(twoStoreCateId))) {
//                    // 推荐查找所有
//                    allIds.add(twoStoreCateId);
//                } else
                if (Objects.equals(Objects.toString(storeCatId), Objects.toString(one.getStoreCateId()))) {
                    // 一级类目
                    allIds.add(twoStoreCateId);
                } else if (Objects.equals(Objects.toString(storeCatId), Objects.toString(twoStoreCateId))) {
                    allIds.add(twoStoreCateId);
                }
            });
        });
        if (CollectionUtils.isNotEmpty(allIds)) {
            return Lists.newArrayList(allIds);
        }
        return null;
    }

    private Set<String> getRecommend(Long storeId, Long storeCatId) {
        Set<String> rIds;
        if (Objects.equals(Objects.toString(storeCatId), "-1")) {
            // 赋值店铺分类
            final MerchantTypeConfigListRequest configListRequest = new MerchantTypeConfigListRequest();
            configListRequest.setStoreId(storeId);
            final BaseResponse<GoodsCateByIdsResponse> goodsCateByIdsResponse = goodsTypeConfigQueryProvider.appList(configListRequest);
            final List<StoreCateVO> recommendGoodsCate = goodsCateByIdsResponse.getContext().getStoreCateVOList();
            if (CollectionUtils.isNotEmpty(recommendGoodsCate)) {
                rIds = recommendGoodsCate.stream().map(f -> Objects.toString(f.getStoreCateId())).collect(Collectors.toSet());
            } else {
                rIds = new HashSet<>();
            }
        } else {
            rIds = new HashSet<>();
        }
        return rIds;
    }

    /**
     * 分页商品品牌
     *
     * @return 商品品牌
     */
    @ApiOperation(value = "分页商品品牌")
    @RequestMapping(value = "/listBrands", method = RequestMethod.POST)
    public BaseResponse<BrandsResponse> listBrands(@RequestBody GoodsBrandsQueryRequest request) {
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setPageNum(request.getPageNum());
        esGoodsInfoQueryRequest.setPageSize(request.getPageSize());
        esGoodsInfoQueryRequest.setCate1Id(request.getCate1Id());
        esGoodsInfoQueryRequest.setCate2Id(request.getCate2Id());
        esGoodsInfoQueryRequest.setCate3Id(request.getCate3Id());
        esGoodsInfoQueryRequest.setClassifyType(request.getClassifyType());
        List<GoodsBrandVO> esBaseInfoByCateId = esGoodsInfoElasticService.listBrands(esGoodsInfoQueryRequest);
        return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(esBaseInfoByCateId).build());
    }

    /**
     * 查询商品库存信息
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品库存信息")
    @RequestMapping(value = "/findGoodsInfoStock", method = RequestMethod.POST)
    public BaseResponse<GetGoodsInfoStockByIdResponse> findGoodsInfoStock(@RequestBody GoodsInfoStockByIdsRequest request) {
        List<GoodsInfoVO> infoVOList = goodsInfoQueryProvider.findGoodsInfoStock(request).getContext().getGoodsInfos();
        List<String> skuIds = infoVOList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        //填充限购库存
        HashMap<String, List<MarketingForEndVO>> listMap = marketingQueryProvider.getMarketingMapByGoodsId(MarketingMapGetByGoodsIdRequest.builder()
                .goodsInfoIdList(skuIds).build()).getContext().getListMap();
        //填充囤货虚拟库存
        Map<String, PileActivityGoodsVO> pileGoodsStockMap = new HashMap<>();

        BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();

        if (CollectionUtils.isNotEmpty(skuIds) && CollectionUtils.isNotEmpty(startPileActivity.getContext())) {
            PileActivityVO pileActivityVO = startPileActivity.getContext().get(0);

            BaseResponse<List<PileActivityGoodsVO>> startPileActivityVirtualStock =
                    pileActivityProvider.getStartPileActivityPileActivityGoods(PileActivityPileActivityGoodsRequest.builder()
                            .goodsInfoIds(skuIds).pileActivityId(pileActivityVO.getActivityId()).build());

            List<PileActivityGoodsVO> context = startPileActivityVirtualStock.getContext();

            if (CollectionUtils.isNotEmpty(context)) {
                pileGoodsStockMap = context.stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g, (a, b) -> a));
            }
        }

        if (Objects.nonNull(listMap)) {
            Map<String, PileActivityGoodsVO> finalPileGoodsStockMap = pileGoodsStockMap;
            infoVOList.forEach(goodsInfoVO -> {
                List<MarketingForEndVO> marketingViewVOS = listMap.getOrDefault(goodsInfoVO.getGoodsInfoId(),Collections.emptyList());
                if (CollectionUtils.isNotEmpty(marketingViewVOS)) {
                    List<MarketingScopeVO> scopeVOList = new ArrayList<>();
                    marketingViewVOS.forEach(marketingViewVO -> scopeVOList.addAll(marketingViewVO.getMarketingScopeList()));
                    if (CollectionUtils.isNotEmpty(scopeVOList)) {
                        Optional<MarketingScopeVO> optional = scopeVOList.stream().filter(s -> s.getScopeId().equals(goodsInfoVO.getGoodsInfoId())).findFirst();
                        if (optional.isPresent() && Objects.nonNull(optional.get().getPurchaseNum()) && optional.get().getPurchaseNum() > 0 && BigDecimal.valueOf(optional.get().getPurchaseNum()).compareTo(goodsInfoVO.getStock()) < 0) {
                            goodsInfoVO.setStock(BigDecimal.valueOf(optional.get().getPurchaseNum()));
                        }
                    }
                }
                //囤货商品库存赋值
                PileActivityGoodsVO pileActivityGoodsVO = finalPileGoodsStockMap.get(goodsInfoVO.getGoodsInfoId());
                if(Objects.nonNull(pileActivityGoodsVO)){
                    goodsInfoVO.setVirtualStock(pileActivityGoodsVO.getVirtualStock().longValue());
                }
            });
        }
        return BaseResponse.success(GetGoodsInfoStockByIdResponse.builder().goodsInfos(infoVOList).build());
    }

    /**
     * 查询商品库存信息
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "检查购物车商品是否库存和区域限购和营销限购")
    @RequestMapping(value = "/checkStockAndPurchase", method = RequestMethod.POST)
    public BaseResponse<StockAndPureChainNodeRsponse> checkStockAndPurchase(@RequestBody StockAndPureChainNodeRequeest request, HttpServletRequest httpRequest) {
        log.info("checkStockAndPurchase入参====================="+request);

        // 对散批和批发商品进行分组处理
        List<DevanningGoodsInfoMarketingVO> allGoodsInfos = request.getCheckPure();
        List<DevanningGoodsInfoMarketingVO> wholesGoodsInfos = Lists.newArrayList();
        List<DevanningGoodsInfoMarketingVO> bulkGoodsInfos = Lists.newArrayList();
        Map<Integer, List<DevanningGoodsInfoMarketingVO>> maps = allGoodsInfos.stream().collect(Collectors.groupingBy(DevanningGoodsInfoMarketingVO::getSaleType));
        for (Map.Entry<Integer, List<DevanningGoodsInfoMarketingVO>> map : maps.entrySet()) {
            Integer saleType = map.getKey();
            if(saleType == 2){// 2代表散批
                bulkGoodsInfos.addAll(map.getValue());
            } else {
                wholesGoodsInfos.addAll(map.getValue());
            }
        }

        CustomerVO customer = commonUtil.getCustomer();
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        Long cityId = Optional.ofNullable(deliveryAddress).map(CustomerDeliveryAddressResponse::getCityId).orElse(null);
        Long provinceId = Optional.ofNullable(deliveryAddress).map(CustomerDeliveryAddressResponse::getProvinceId).orElse(null);

        request.setCustomerId(customer.getCustomerId());
        request.setCityId(cityId);
        request.setProvinceId(provinceId);


        List<DevanningGoodsInfoPureVO> checkPure = Lists.newArrayList();
        // 批发+零售
        if(CollectionUtils.isNotEmpty(wholesGoodsInfos)){
            WareHouseVO wareHouseVO = this.matchWareStore(cityId);
            request.setWareId(wareHouseVO.getWareId());
            request.setCheckPure(wholesGoodsInfos);

            List<DevanningGoodsInfoPureVO> devanningGoodsInfoPureVOS = Optional.ofNullable(provider.checkStockPurchase(request))
                    .map(BaseResponse::getContext)
                    .map(StockAndPureChainNodeRsponse::getCheckPure).orElse(Lists.newArrayList());
            checkPure.addAll(devanningGoodsInfoPureVOS);
        }
        // 散批
        if(CollectionUtils.isNotEmpty(bulkGoodsInfos)){
            WareHouseVO wareHouseVO = this.matchBulkWareStore(cityId);

            // 拿到用户购物车中的地址
            request.setSubType(4);// 4代表校验散批
            request.setCheckPure(bulkGoodsInfos);
            request.setWareId(wareHouseVO.getWareId());
            log.info("获取散批匹配仓:{}",JSON.toJSONString(wareHouseVO));

            List<DevanningGoodsInfoPureVO> devanningGoodsInfoPureVOS = Optional.ofNullable(provider.checkStockPurchase(request))
                    .map(BaseResponse::getContext)
                    .map(StockAndPureChainNodeRsponse::getCheckPure).orElse(Lists.newArrayList());
            checkPure.addAll(devanningGoodsInfoPureVOS);
        }

        StockAndPureChainNodeRsponse result = StockAndPureChainNodeRsponse.builder().CheckPure(checkPure).build();
        return BaseResponse.success(result);
    }


    private WareHouseVO matchBulkWareStore(Long cityCode){
        List<WareHouseVO> wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .defaultFlag(DefaultFlag.SP)
                .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
        //设置selectedAreas
        wareHouseMainList.stream().forEach(w->{
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if(wareHouseMainList.stream().anyMatch(w->w.getSelectedAreas().contains(cityCode))){
            Optional<WareHouseVO> matchedWareHouse = wareHouseMainList.stream().filter(w->w.getSelectedAreas()
                    .contains(cityCode)).findFirst();
            if(matchedWareHouse.isPresent()){
                return matchedWareHouse.orElse(new WareHouseVO());
            }
        }else {
            return new WareHouseVO();
        }
        // 如果没有匹配到分仓，则获取默认的分仓
        Optional<WareHouseVO> wareHouseVOOptional = wareHouseMainList.stream().filter(w-> DefaultFlag.YES.equals(w.getDefaultFlag())).findFirst();
        return wareHouseVOOptional.orElse(new WareHouseVO());
    }

    private WareHouseVO matchWareStore(Long cityCode){
        //1. 从redis里获取主仓
        List<WareHouseVO> wareHouseMainList;
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            if(CollectionUtils.isNotEmpty(wareHouseVOS)){
                wareHouseMainList = wareHouseVOS.stream()
                        .filter(wareHouseVO -> PickUpFlag.NO.equals(wareHouseVO.getPickUpFlag())).collect(Collectors.toList());
            }else{
                wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                        .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
            }
        }else{
            wareHouseMainList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO)
                    .pickUpFlag(PickUpFlag.NO).build()).getContext().getWareHouseVOList();
        }
        //设置selectedAreas
        wareHouseMainList.stream().forEach(w->{
            String[] cityIds = w.getDestinationArea().split(",");
            Long[] cityIdList = (Long[]) ConvertUtils.convert(cityIds, Long.class);
            w.setSelectedAreas(Arrays.asList(cityIdList));
        });
        //2. 匹配分仓信息
        if(wareHouseMainList.stream().anyMatch(w->w.getSelectedAreas().contains(cityCode))){
            Optional<WareHouseVO> matchedWareHouse = wareHouseMainList.stream().filter(w->w.getSelectedAreas()
                    .contains(cityCode)).findFirst();
            if(matchedWareHouse.isPresent()){
                return matchedWareHouse.orElse(new WareHouseVO());
            }
        }else {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"您所在的区域没有可配的仓库，请重新修改收货地址");
        }
        // 如果没有匹配到分仓，则获取默认的分仓
        Optional<WareHouseVO> wareHouseVOOptional = wareHouseMainList.stream().filter(w-> DefaultFlag.YES.equals(w.getDefaultFlag())).findFirst();
        return wareHouseVOOptional.orElse(new WareHouseVO());
    }
    /**
     * 查询商品库存信息（囤货购物车专属）
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品库存信息")
    @RequestMapping(value = "/findGoodsInfoStockForPile", method = RequestMethod.POST)
    public BaseResponse<GetGoodsInfoStockByIdResponse> findGoodsInfoStockForPile(@RequestBody GoodsInfoStockByIdsRequest request) {
        return goodsInfoQueryProvider.findGoodsInfoStockForPile(request);
    }

    /**
     * 初始化热销排行榜redis数据
     * @param wareId
     * @param redisKey
     */
    public void fillHotSaleRedis(String wareId,String redisKey,String cateId) {
        Collection<String> otherKeys = new ArrayList<>(7);
        String key = null;
        if (redisKey.equals(RedisKeyConstant.TOTAL_HOT_SALE_WEEK)) {
            //总榜
            key = RedisKeyConstant.HIT_LIST.concat("-").concat(wareId).concat("-");
        } else {
            //分类榜
            key = RedisKeyConstant.HIT_LIST.concat("-").concat(cateId)
                    .concat("-").concat(wareId).concat("-");
        }
        for(int i = 2; i <= 7; i++) {
            //需要合并的排行榜
            otherKeys.add(key.concat(DateUtil.getDayAddEnd(i,DateUtil.FMT_TIME_5)));
        }

        if (redisKey.equals(RedisKeyConstant.TOTAL_HOT_SALE_WEEK)) {
            //并集一周内的单日总榜 ===》周总榜
            redisZSetUtil.zUnionAndStore(key.concat(DateUtil.getDayAddEnd(1,DateUtil.FMT_TIME_5)),
                    otherKeys, wareId.concat(RedisKeyConstant.TOTAL_HOT_SALE_WEEK));
            //延迟5分钟刷新
            redisService.expireBySeconds(wareId.concat(RedisKeyConstant.TOTAL_HOT_SALE_WEEK), DateUtil.getSeconds()+300);
        } else {
//            log.info("==============fillHotSaleRedis:::key:{} , otherKeys:{} , destKey:{}",key.concat(DateUtil.getDayAddEnd(1,DateUtil.FMT_TIME_5))
//            ,JSONObject.toJSONString(otherKeys),wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(cateId));

            //并集一周内的单日分类榜 ===》周分类榜
            redisZSetUtil.zUnionAndStore(key.concat(DateUtil.getDayAddEnd(1,DateUtil.FMT_TIME_5)),
                    otherKeys, wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(cateId));

//            log.info("==============fillHotSaleRedis.expireBySeconds:::destKey:{} , times:{} ",wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(cateId)
//            ,DateUtil.getSeconds()+300);
            //延迟5分钟刷新
            redisService.expireBySeconds(wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(cateId), DateUtil.getSeconds()+300);
        }

    }

    /**
     * 销量排行榜
     */
    @ApiOperation(value = "查询商品销量排行")
    @RequestMapping(value = "/getSalesRanking", method = RequestMethod.POST)
    public BaseResponse<GoodsSalesRankingResponse> getSalesRanking(@RequestBody GoodsSalesRankingRequest request,HttpServletRequest httpRequest) {
        String wareId = commonUtil.getWareId(HttpUtil.getRequest()).toString();
        CustomerVO customer = commonUtil.getCustomer();

        GoodsSalesRankingResponse returnResponse = new GoodsSalesRankingResponse();
        Set<ZSetOperations.TypedTuple<String>> hitSet = new HashSet<>();

        if (Objects.isNull(request.getCateId()) || request.getCateId().equals(0L)){
            if (!redisService.hasKey(wareId.concat(RedisKeyConstant.TOTAL_HOT_SALE_WEEK))) {
                this.fillHotSaleRedis(wareId, RedisKeyConstant.TOTAL_HOT_SALE_WEEK, null);
            }
            hitSet = redisZSetUtil.zReverseRangeWithScores(wareId.concat(RedisKeyConstant.TOTAL_HOT_SALE_WEEK),0,19);
        }else {
            if (!redisService.hasKey(wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(request.getCateId().toString()))) {
                this.fillHotSaleRedis(wareId, RedisKeyConstant.CATE_HOT_SALE_WEEK, request.getCateId().toString());
            }
            hitSet = redisZSetUtil.zReverseRangeWithScores(wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(request.getCateId().toString()),0,19);
        }

        if (CollectionUtils.isNotEmpty(hitSet)){
            //排行榜集合
            List<GoodsBySalesRankingVO> rankingVOList = new ArrayList<>(10);
            List<String> skuIds = new ArrayList<>(10);
            hitSet.forEach(hit ->{
                GoodsBySalesRankingVO topGoodsInfo = new GoodsBySalesRankingVO();
                topGoodsInfo.setGoodsInfoId(hit.getValue());
                topGoodsInfo.setGoodsSalesNum(hit.getScore().longValue());
                rankingVOList.add(topGoodsInfo);
                skuIds.add(hit.getValue());
            });

            //获取所有商品的sku信息
            GoodsInfoViewByIdsResponse response = goodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder().goodsInfoIds(skuIds).build()).getContext();
            //计算营销价格
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            filterRequest.setGoodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class));
            if (Objects.nonNull(customer)) {
                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            }
            GoodsInfoListByGoodsInfoResponse filterResponse = marketingPluginProvider.goodsListFilter(filterRequest)
                    .getContext();

            if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
                response.setGoodsInfos(filterResponse.getGoodsInfoVOList());
            }

            //通过客户收货地址和商品指定区域设置商品状态
            //根据用户ID得到收货地址
            /*CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)) {
                deliveryAddress = commonUtil.getDeliveryAddress();
            }*/

            CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
            if (CollectionUtils.isNotEmpty(response.getGoodsInfos()) && Objects.nonNull(customer)){
                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) &&
                                !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                });
            }

            Map<String, GoodsInfoVO> goodsInfoMap = response.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g -> g));
            List<String> goodsInfoIds = response.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            Map<String, GoodsVO> goodsMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g));
            Map<String, Long> purchaseMap = new HashMap<>();
            //获取客户购物车商品信息
            if(Objects.nonNull(commonUtil.getOperatorId())){
                PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
                purchaseQueryRequest.setCustomerId(commonUtil.getOperatorId());
                purchaseQueryRequest.setGoodsInfoIds(skuIds);
                purchaseQueryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                /**改查询正常购物车数量*/
                List<PurchaseVO> purchaseList = shopCartQueryProvider.query(purchaseQueryRequest).getContext().getPurchaseList();
                if (CollectionUtils.isNotEmpty(purchaseList)){
                    purchaseMap  = purchaseList.stream().collect(Collectors.toMap(PurchaseVO::getGoodsInfoId,PurchaseVO::getGoodsNum,(oldData,newData)->oldData+newData));
                }
            }
            Map<String, Long> finalPurchaseMap = purchaseMap;

            Map<String, PileActivityGoodsVO> pileActivityGoodsMap = new HashMap<>();

            //获取商品参与囤货活动
//            List<PileActivityVO> context1 = pileActivityProvider.getStartPileActivity().getContext();
            List<PileActivityVO> pileActivityVOS = Optional.ofNullable(pileActivityProvider.getStartPileActivity())
                    .map(BaseResponse::getContext)
                    .orElse(Lists.newArrayList());
            PileActivityVO pileActivityVO =  null;
            if(CollectionUtils.isNotEmpty(pileActivityVOS)){
                pileActivityVO = pileActivityVOS.get(0);
            }

            if (CollectionUtils.isNotEmpty(skuIds) && Objects.nonNull(pileActivityVO)) {
                BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods =
                        pileActivityProvider.getStartPileActivityPileActivityGoods(
                                PileActivityPileActivityGoodsRequest.builder()
                                        .goodsInfoIds(skuIds).pileActivityId(pileActivityVO.getActivityId()).build());

                BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
                if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){
                    pileActivityGoodsMap = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));
                }
            }

            //活动图片处理
            Map<String, ActivityGoodsResponse> collect = new HashMap<>();
            if(CollectionUtils.isNotEmpty(goodsInfoIds)){
                BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(goodsInfoIds).build());
                List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
                if(CollectionUtils.isNotEmpty(activityGoodsResponse)){
                    collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsInfoId, g -> g,(a,b)->a));
                }
            }

            Map<String, ActivityGoodsResponse> finalCollect = collect;

            //填充热销排行榜信息
            Map<String, PileActivityGoodsVO> finalPileActivityGoodsMap = pileActivityGoodsMap;
            PileActivityVO finalPileActivityVO = pileActivityVO;
            rankingVOList.forEach(rankingVo -> {
                GoodsInfoVO goodsInfoVO = goodsInfoMap.get(rankingVo.getGoodsInfoId());
                if (null == goodsInfoVO) return;
                GoodsVO goodsVO = goodsMap.get(goodsInfoVO.getGoodsId());
                rankingVo.setGoodsId(goodsVO.getGoodsId());
                rankingVo.setGoodsName(goodsVO.getGoodsName());
                //设置参与活动商品的活动图片
                if(CollectionUtils.isNotEmpty(goodsInfoVO.getMarketingLabels())){
                    if(CollectionUtils.isNotEmpty(goodsInfoVO.getGoodsImages())){
                        ActivityGoodsResponse activityGoodsResponse = finalCollect.get(goodsInfoVO.getGoodsInfoId());
                        goodsInfoVO.getGoodsImages().get(0).setArtworkUrl(activityGoodsResponse.getImgPath());
                    }
                }
                rankingVo.setGoodsImg(Objects.nonNull(goodsVO.getGoodsImg()) ? goodsVO.getGoodsImg() : null);
                rankingVo.setGoodsSubtitle(goodsVO.getGoodsSubtitle());
                rankingVo.setMarketPrice(goodsInfoVO.getMarketPrice());
                rankingVo.setCateId(goodsInfoVO.getCateId());
                rankingVo.setStock(goodsInfoVO.getStock());
                rankingVo.setVipPrice(goodsInfoVO.getVipPrice());
                rankingVo.setGoodsStatus(goodsInfoVO.getGoodsStatus());
                rankingVo.setInquiryFlag(goodsInfoVO.getInquiryFlag());

                if(Objects.nonNull(finalPileActivityVO) && Objects.nonNull(finalPileActivityGoodsMap.get(rankingVo.getGoodsInfoId()))){
                    rankingVo.setVirtualStock(finalPileActivityGoodsMap.get(rankingVo.getGoodsInfoId()).getVirtualStock().longValue());
                    if(finalPileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                        rankingVo.setPileFlag(ForcePileFlag.FORCEPILE);
                    }else {
                        rankingVo.setPileFlag(ForcePileFlag.PILE);
                    }
                }else {
                    rankingVo.setPileFlag(ForcePileFlag.CLOSE);
                }
                //vip价格计算
                if (Objects.isNull(customer)) {
                    //用户不登录，不展示vip价格 返0给前端
                    rankingVo.setVipPrice(BigDecimal.ZERO);
                } else {
                    //vip价格不参与任何营销活动 返0给前端
                    if (CollectionUtils.isNotEmpty(goodsInfoVO.getMarketingLabels())) {
                        rankingVo.setVipPrice(BigDecimal.ZERO);
                    }
                }
                //填充客户购物车商品数量
                if(Objects.nonNull(finalPurchaseMap.get(rankingVo.getGoodsInfoId()))){
                    rankingVo.setPurchaseNum(finalPurchaseMap.get(rankingVo.getGoodsInfoId()));
                }
            });
            //过滤下架、无效商品，去前10条记录
            List<GoodsBySalesRankingVO> responseList = rankingVOList.stream().filter(r -> !r.getGoodsStatus().equals(GoodsStatus.INVALID)).limit(10).collect(Collectors.toList());
            returnResponse.setRankingVOList(responseList);
        }

        return BaseResponse.success(returnResponse);
    }

    /**
     * 查看某个sku排名
     */
    @ApiOperation(value = "查看某个sku排名")
    @RequestMapping(value = "/getSalesRanking/top", method = RequestMethod.POST)
    public BaseResponse<GoodsSalesRankingTopResponse> getSalesRankingTop(@RequestBody GoodsSalesRankingTopRequest request) {
        //递归获取二级分类信息(现在redis里面没有存2级类目的，所以先用3级查询)
        GoodsCateByIdResponse goodsCate = goodsCateQueryProvider.getByIdForLevel(GoodsCateByIdRequest.builder().cateId(request.getCateId()).build()).getContext();
        String wareId = commonUtil.getWareId(HttpUtil.getRequest()).toString();
        String totalRedisKey = wareId.concat(RedisKeyConstant.TOTAL_HOT_SALE_WEEK);
        String cateRedisKey = wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(goodsCate.getCateId().toString());
//        String cateRedisKey = wareId.concat(RedisKeyConstant.CATE_HOT_SALE_WEEK).concat(request.getCateId().toString());
        log.info("====GoodsSalesRankingTopRequest:{}", JSONObject.toJSONString(request));
        log.info("====goodsCate:{}", JSONObject.toJSONString(goodsCate));
        log.info("====totalRedisKey:{}", totalRedisKey);
        log.info("====cateRedisKey:{}", cateRedisKey);
        Long top = -1L;
        //获取缓存排行信息
        if (Objects.isNull(goodsCate)) {
            if (!redisService.hasKey(totalRedisKey)) {
                this.fillHotSaleRedis(wareId, RedisKeyConstant.TOTAL_HOT_SALE_WEEK, null);
            }
            top = redisZSetUtil.zReverseRank(totalRedisKey, request.getSkuId());
        } else {
            if (!redisService.hasKey(cateRedisKey)) {
                this.fillHotSaleRedis(wareId, RedisKeyConstant.CATE_HOT_SALE_WEEK, goodsCate.getCateId().toString());
            }
            top = redisZSetUtil.zReverseRank(cateRedisKey, request.getSkuId());
        }
        log.info("====top:{}", top);
        if (Objects.isNull(top)) {
            return BaseResponse.success(null);
        }
        if (top.equals(-1L)) {
            return BaseResponse.success(null);
        } else {
//            return BaseResponse.success(GoodsSalesRankingTopResponse.builder().top(top+1L).cateName(goodsCate.getCateName()).cateId(goodsCate.getCateId()).build());
            // 兼容客户端，写死20不展示
            return BaseResponse.success(GoodsSalesRankingTopResponse.builder().top(20L).cateName(goodsCate.getCateName()).cateId(goodsCate.getCateId()).build());
        }
    }

    /**
     * 囤货，添加活动限购数
     * @param goodsInfoNest
     */
    private void addActivityRestricted(GoodsInfoNest goodsInfoNest,MarketingLabelVO marketingLabelVO,BaseResponse<ProcurementConfigResponse> results){
        if (Objects.nonNull(results.getContext()) && results.getContext().getProcurementType() == ProcurementTypeEnum.STOCKUP){
            //囤货有活动id，添加活动库存
            if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
                if (Objects.nonNull(goodsInfoNest.getMarketingId()) && Objects.nonNull(marketingLabelVO.getMarketingId())
                        && goodsInfoNest.getMarketingId().equals(marketingLabelVO.getMarketingId())) {
                    Long purchaseNum = goodsInfoNest.getPurchaseNum();
                    //限购数是否为0
                    if (purchaseNum > 0) {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                        goodsInfoNest.setStock(BigDecimal.valueOf(purchaseNum));
                    } else {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        goodsInfoNest.setStock(BigDecimal.ZERO);
                    }
                }else {
                    if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0){
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                }
            }else {
                goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
            }
        }
    }

    /**
     * 囤货，添加活动限购数（详情）
     * @param goodsInfoNest
     */
    private void addDetailsActivityRestricted(GoodsInfoVO goodsInfoNest,MarketingLabelVO marketingLabelVO,BaseResponse<ProcurementConfigResponse> results){
        if (Objects.nonNull(results.getContext()) && results.getContext().getProcurementType() == ProcurementTypeEnum.STOCKUP){
            //囤货有活动id，添加活动库存
            if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
                if (Objects.nonNull(goodsInfoNest.getMarketingId()) && Objects.nonNull(marketingLabelVO.getMarketingId())
                        && goodsInfoNest.getMarketingId().equals(marketingLabelVO.getMarketingId())) {
                    Long purchaseNum = goodsInfoNest.getPurchaseNum();
                    //限购数是否为0
                    if (purchaseNum > 0) {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                        goodsInfoNest.setStock(BigDecimal.valueOf(purchaseNum));
                    } else {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        goodsInfoNest.setStock(BigDecimal.ZERO);
                    }
                }else {
                    if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0){
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                }
            }else {
                goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
            }
        }
    }

    /**
     * 计算商品列表 商品库存信息
     * @param goodsInfoNest
     * @param goodsNumsMap
     */
    private void calGoodsInfoNestStock(GoodsInfoNest goodsInfoNest,Map<String, Long> goodsNumsMap){
        //虚拟库存不为空且大于0
        if (Objects.nonNull(goodsInfoNest.getVirtualStock()) && goodsInfoNest.getVirtualStock().compareTo(BigDecimal.ZERO) > 0){
            goodsInfoNest.setStock(goodsInfoNest.getStock().add(goodsInfoNest.getVirtualStock()));
        }
        //囤货数量不为空且大于0
        Long pileGoodsNum = goodsNumsMap.getOrDefault(goodsInfoNest.getGoodsInfoId(),null);
        if (Objects.nonNull(pileGoodsNum) && pileGoodsNum > 0){
            goodsInfoNest.setStock(goodsInfoNest.getStock().subtract(BigDecimal.valueOf(pileGoodsNum)));
        }
    }

    /**
     * 零售商品条件查询（分页、mysql） 大白鲸超市【共用】
     * @param queryRequest 商品查询条件
     * @return GoodsInfoViewPageResponse
     */
    @ApiOperation(value = "零售商品条件查询（分页、mysql） 大白鲸超市【共用】")
    @RequestMapping(value = "/skus/retail", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> retailSkusPage(@RequestBody GoodsInfoViewPageRequest queryRequest, HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        //按品牌、上下架时间、商品名称排序
        if (CollectionUtils.isEmpty(queryRequest.getGoodsInfoIds())) {
            queryRequest.putSort("brandId", SortType.DESC.toValue());
            queryRequest.putSort("addedTime", SortType.DESC.toValue());
            queryRequest.putSort("goodsInfoName", SortType.DESC.toValue());
        }
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        queryRequest.setAddedFlag(DefaultFlag.YES.toValue());
        queryRequest.setPageSize(200);
        GoodsInfoViewPageResponse response = retailGoodsInfoQueryProvider.pageView(queryRequest).getContext();

        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfoPage().getContent();
        if (CollectionUtils.isEmpty(goodsInfoVOList)) {
            response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoVOList, queryRequest.getPageRequest(),
                    response.getGoodsInfoPage().getTotalElements()));
            return BaseResponse.success(response);
        }

        //计算区间价
        if(customer != null && StringUtils.isNotBlank(customer.getCustomerId())) {
            GoodsIntervalPriceByCustomerIdResponse priceResponse =
                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(goodsInfoVOList, customer.getCustomerId());
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoVOList = priceResponse.getGoodsInfoVOList();
        }else {
            GoodsIntervalPriceResponse priceResponse =
                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(goodsInfoVOList);
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoVOList = priceResponse.getGoodsInfoVOList();
        }

        //填充购买数量
        if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
            PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
            request.setCustomerId(customer.getCustomerId());
            request.setGoodsInfoList(goodsInfoVOList);
            request.setInviteeId(commonUtil.getPurchaseInviteeId());

            //购物车
            PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = retailShopCartProvider.fillBuyCount(request)
                    .getContext();
            goodsInfoVOList = purchaseFillBuyCountResponse.getGoodsInfoList();
        }

        //计算营销价格
        /**
         * 填充优惠券标签couponLabels字段
         */
        MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
        filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class));
        if (Objects.nonNull(customer)) {
            filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
        }
        GoodsInfoListByGoodsInfoResponse filterResponse = marketingPluginProvider.goodsListFilter(filterRequest)
                .getContext();

        if (Objects.nonNull(filterResponse) && CollectionUtils.isNotEmpty(filterResponse.getGoodsInfoVOList())) {
            goodsInfoVOList = filterResponse.getGoodsInfoVOList();
        }

        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        /*CustomerDeliveryAddressResponse deliveryAddress = null;
        if (Objects.nonNull(customer)) {
            deliveryAddress = commonUtil.getDeliveryAddress();
        }*/
        CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);

        //折扣商品：将折扣价设置为市场价
        goodsInfoVOList.forEach(goodsInfoVO -> {
            if (goodsInfoVO.getGoodsInfoType() == 1 && Objects.nonNull(goodsInfoVO.getSpecialPrice())) {
                goodsInfoVO.setMarketPrice(goodsInfoVO.getSpecialPrice());
            }

            if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())
                    && goodsInfoVO.getGoodsStatus().equals(GoodsStatus.OK)) {
                List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                    goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                }
            }
        });
        if(CollectionUtils.isNotEmpty(response.getGoodses())){
            Map<String, GoodsVO> goodsMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g));
            if(CollectionUtils.isNotEmpty(goodsInfoVOList)){
                for (GoodsInfoVO goodsDatum : goodsInfoVOList) {
                    GoodsVO goodsVO = goodsMap.get(goodsDatum.getGoodsId());
                    if(Objects.nonNull(goodsVO)){
                        goodsDatum.setGoodsSubtitle(goodsVO.getGoodsSubtitle());
                        goodsDatum.setGoodsSubtitleNew(goodsVO.getGoodsSubtitleNew());
                    }
                }
            }
        }
        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoVOList, queryRequest.getPageRequest(),
                response.getGoodsInfoPage().getTotalElements()));
        return BaseResponse.success(response);
    }

    /**
     * 零售商品Spu商品详情
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "零售商品Spu商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/spu/retail/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> retailDetail(@PathVariable String skuId, @RequestParam Long wareId, @RequestParam(required = false) Boolean matchWareHouseFlag,HttpServletRequest httpRequest) {
        log.info("========retailWareId:{}",retailWareId);
        CustomerVO customer = commonUtil.getCustomer();
        GoodsViewByIdResponse detail = retailDetail(GoodsWareStockRequest.builder()
                .skuId(skuId).wareId(retailWareId == null || retailWareId.longValue() <= 0 ? commonUtil.getWareId(HttpUtil.getRequest()) : retailWareId)
                .matchWareHouseFlag(matchWareHouseFlag)
                .build(), customer,httpRequest);
        if (CollectionUtils.isNotEmpty(detail.getImages()) && detail.getImages().size() > 1) {
            detail.getImages().remove(0);
        }

        log.info(" ============detail:{}",detail);

        return BaseResponse.success(detail);
    }


    /**
     * SPU商品详情
     *
     * @param goodsWareStockRequest 商品skuId
     * @param customer              会员
     * @return SPU商品详情
     */
    private GoodsViewByIdResponse retailDetail(GoodsWareStockRequest goodsWareStockRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        GoodsViewByIdResponse response = retailGoodsDetailBaseInfo(goodsWareStockRequest.getSkuId(), goodsWareStockRequest.getWareId()
                , goodsWareStockRequest.getMatchWareHouseFlag());

        log.info("=========GoodsViewByIdResponse:{}",JSONObject.toJSONString(response));

        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)
                && !domainInfo.getStoreId().equals(response.getGoods().getStoreId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos().stream()
                .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
            response = retailDetailGoodsInfoVOList(response, goodsInfoVOList, customer);
        }

        List<GoodsLabelVO> collect = response.getGoods().getGoodsLabels().stream()
                .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                .collect(Collectors.toList());
        response.getGoods().setGoodsLabels(collect);

        //商品信息
        GoodsVO goodsSubtitle = response.getGoods();
        //计算到手价
        response.getGoodsInfos().stream().forEach(goodsInfos -> {

            //如果商品有任何营销活动信息 则vip价格不参与 前端不展示（传0元）
            if (CollectionUtils.isNotEmpty(goodsInfos.getMarketingLabels())) {
                goodsInfos.setVipPrice(BigDecimal.ZERO);
            }

            //计算到手价
            this.calTheirPrice(goodsInfos);

            goodsInfos.getMarketingLabels().stream().forEach(marketingLabelVO -> {
                //满赠活动，带出赠品信息
                if (Objects.nonNull(marketingLabelVO.getMarketingType()) && marketingLabelVO.getMarketingType() == 2) {
                    FullGiftLevelListByMarketingIdAndCustomerRequest fullgiftRequest = FullGiftLevelListByMarketingIdAndCustomerRequest
                            .builder().build();
                    if (Objects.nonNull(customer)){
                        fullgiftRequest.setCustomer(KsBeanUtil.convert(commonUtil.getCustomer(), CustomerDTO.class));
                        fullgiftRequest.setMatchWareHouseFlag(false);
                    }
                    fullgiftRequest.setMarketingId(marketingLabelVO.getMarketingId());
                    List<GoodsInfoVO> giftList = fullGiftQueryProvider.listGiftByMarketingIdAndCustomer(fullgiftRequest).getContext().getGiftList();
                    goodsInfos.setGiftList(giftList);
                }

                //修改副标题(如果到手价不为空已到手价计算)
                if(Objects.nonNull(goodsInfos.getTheirPrice()) && StringUtils.isNotEmpty(goodsSubtitle.getGoodsSubtitle())){
                    //商品详情计算副标题
                    this.setGoodsSubtitle(goodsInfos,goodsSubtitle,goodsInfos.getTheirPrice());
                }else if (Objects.nonNull(customer) && Objects.nonNull(customer.getVipFlag()) && customer.getVipFlag().equals(DefaultFlag.YES)
                        && Objects.nonNull(goodsInfos.getVipPrice()) && goodsInfos.getVipPrice().compareTo(BigDecimal.ZERO) > 0) {
                    //商品详情计算副标题（如果到手价为空，判断是否有大客户价；否则用原价显示副标题）
                    this.setGoodsSubtitle(goodsInfos,goodsSubtitle,goodsInfos.getVipPrice());
                }

            });

            if (CollectionUtils.isNotEmpty(goodsInfos.getMarketingLabels())) {
                goodsInfos.setVipPrice(BigDecimal.ZERO);
            }

            //如果没有营销活动计算修改副标题
            if (CollectionUtils.isEmpty(goodsInfos.getMarketingLabels())) {
                if (Objects.nonNull(customer) && Objects.nonNull(customer.getVipFlag()) && customer.getVipFlag().equals(DefaultFlag.YES)
                        && Objects.nonNull(goodsInfos.getVipPrice()) && goodsInfos.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                    //商品详情计算副标题
                    this.setGoodsSubtitle(goodsInfos,goodsSubtitle,goodsInfos.getVipPrice());
                }
            }

            //设置商品是否直播
            LiveHaveGoodsVO liveHaveGoodsVO=liveStreamQueryProvider.goodsLiveInfo(LiveStreamInfoRequest.builder().goodsInfoId(goodsInfos.getGoodsInfoId()).build()).getContext().getLiveHaveGoodsVO();
            if(liveHaveGoodsVO.getIsHaveLive()==1){
                goodsInfos.setIsHaveLive(1);
                goodsInfos.setLiveRoomId(liveHaveGoodsVO.getLiveRoomId());
                goodsInfos.setLiveId(liveHaveGoodsVO.getLiveId());
            }else{
                goodsInfos.setIsHaveLive(0);
            }
            //通过商品指定区域销售设置商品状态
            //获取客户收货地址
            /*if(Objects.nonNull(commonUtil.getCustomer()) && goodsInfos.getGoodsStatus().equals(GoodsStatus.OK)){
                CustomerDeliveryAddressResponse deliveryAddress =  commonUtil.getProvinceCity(httpRequest);
                if(Objects.nonNull(deliveryAddress)){
                    if (StringUtils.isNotBlank(goodsInfos.getAllowedPurchaseArea())) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfos.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                            goodsInfos.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                }
            }*/
        });
        return response;
    }


    /**
     * 零售SPU商品详情-基础信息（不包括区间价、营销信息）
     *
     * @param skuId 商品skuId
     * @return SPU商品详情
     */
    private GoodsViewByIdResponse retailGoodsDetailBaseInfo(String skuId, Long wareId, Boolean matchWareHouseFlag) {
        GoodsInfoVO goodsInfo = retailGoodsInfoQueryProvider.getRetailById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).matchWareHouseFlag(matchWareHouseFlag).build()).getContext();
        log.info("==========retailgoodsInfo:{},{},{},{}",JSONObject.toJSONString(goodsInfo),skuId,wareId,matchWareHouseFlag);
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
        GoodsViewByIdResponse response = retailGoodsQueryProvider.getRetailViewById(request).getContext();
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(distributionCacheService.queryStoreOpenFlag
                (String.valueOf(goodsInfo.getStoreId()))) || !DistributionGoodsAudit.CHECKED.equals(goodsInfo
                .getDistributionGoodsAudit())) {
            response.setDistributionGoods(Boolean.FALSE);
        } else {
            response.setDistributionGoods(Boolean.TRUE);
        }

        return response;
    }

    private GoodsViewByIdResponse retailGoodsDetailBaseInfo(String skuId, Long wareId, Boolean matchWareHouseFlag,String parentSkuId) {
        GoodsInfoVO goodsInfo = retailGoodsInfoQueryProvider.getRetailById(GoodsInfoByIdRequest.builder().goodsInfoId(skuId).matchWareHouseFlag(matchWareHouseFlag).build()).getContext();

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
        GoodsViewByIdResponse response = retailGoodsQueryProvider.getRetailViewById(request).getContext();
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        if (DefaultFlag.NO.equals(openFlag) || DefaultFlag.NO.equals(distributionCacheService.queryStoreOpenFlag
                (String.valueOf(goodsInfo.getStoreId()))) || !DistributionGoodsAudit.CHECKED.equals(goodsInfo
                .getDistributionGoodsAudit())) {
            response.setDistributionGoods(Boolean.FALSE);
        } else {
            response.setDistributionGoods(Boolean.TRUE);
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
    private GoodsViewByIdResponse retailDetailGoodsInfoVOList(GoodsViewByIdResponse response, List<GoodsInfoVO>
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

                    if(Objects.nonNull(goodsInfoVO.getMarketingLabels())){
                        goodsInfoVO.getMarketingLabels().forEach(marketingLabelVO -> {
                            String desc = marketingLabelVO.getMarketingDesc();
                            List<String> descList = marketingLabelVO.getMarketingDescList();
                            if(Objects.nonNull(desc) && desc.indexOf("（") != -1){
                                String newDesc = "跨单品"+desc.substring(0, desc.indexOf("（"));
                                marketingLabelVO.setMarketingDesc(newDesc);
                                if (Objects.nonNull(descList)){
                                    List<String> newDescList = new ArrayList<>();
                                    descList.forEach( s -> {
                                        newDescList.add("跨单品"+s.substring(0,s.indexOf("（")));
                                    });
                                    marketingLabelVO.setMarketingDescList(newDescList);
                                }
                            }
                        });
                    }

                    if (Objects.nonNull(customer)
                            && null != customer.getEnterpriseStatusXyy()
                            && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                        //特价商品销售价取市场价
                        if (goodsInfoVO.getGoodsInfoType() == 1) {
                            goodsInfoVO.setSalePrice(goodsInfoVO.getMarketPrice());
                        } else {
                            goodsInfoVO.setSalePrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                        }
                        goodsInfoVO.setEnterpriseStatusXyy(customer.getEnterpriseStatusXyy().toValue());
                        goodsInfoVO.setMarketPrice(Objects.nonNull(goodsInfoVO.getVipPrice()) && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                    }
                });
            }

        }
        return response;
    }

    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse listForRetail(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        if(Objects.nonNull(queryRequest.getWareId())){
            queryRequest.setWareId(null);
        }
//        if (queryRequest.getSortFlag() == null) {
//            queryRequest.setSortFlag(10);
//        }
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
//        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
            vipPriceFlag = true;
        }
        if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())) {
            vipPriceFlag = true;
        }
//        if (Objects.nonNull(domainInfo)) {
//            queryRequest.setStoreId(domainInfo.getStoreId());
//        }
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
                                /*if (Objects.nonNull(goodsInfoNest.getMarketingLabels())) {
                                    goodsInfoNest.getMarketingLabels().forEach(marketingLabelVO -> {
                                        if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
                                                && marketingLabelVO.getGoodsPurchasingNumber() > 0
                                                && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
                                            goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
                                        }
                                    });
                                }*/

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
                    List<GoodsImageVO> context = goodsImageProvider.getGoodsImagesByGoodsIds(goodsIds).getContext();

                    //设置企业商品的审核状态 ，以及会员的大客户价,及图片
                    resultGoodsInfos.forEach(goodsInfoNest -> {

                        if (queryRequest.getImageFlag() &&  CollectionUtils.isNotEmpty(context)) {
                            Map<String, List<GoodsImageVO>> goodsImagesMap = context.stream().collect(Collectors.groupingBy(GoodsImageVO::getGoodsId));

                            List<GoodsImageVO> goodsImages = goodsImagesMap.get(goodsInfoNest.getGoodsId());
                            if(CollectionUtils.isNotEmpty(goodsImages) && goodsImages.size() > 1){
                                goodsInfoNest.setGoodsInfoImg(goodsImages.get(1).getArtworkUrl());
                            }
                        }
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

                        List<GoodsWareStockVO> goodsWareStockVOS=goodsInfoNest.getGoodsWareStockVOS();
                        if(CollectionUtils.isNotEmpty(goodsWareStockVOS)&&Objects.nonNull(goodsInfoNest)) {
                            BigDecimal sumStock = goodsWareStockVOS.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                            goodsInfoNest.setStock(sumStock.setScale(0, BigDecimal.ROUND_DOWN));
                        }
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

    private EsGoodsLimitBrandResponse simpleListForRetail(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        if(Objects.nonNull(queryRequest.getWareId())){
            queryRequest.setWareId(null);
        }
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }
        Boolean vipPriceFlag = false;
        if (Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
            vipPriceFlag = true;
        }
        if (Objects.nonNull(customer) && DefaultFlag.YES.equals(customer.getVipFlag())) {
            vipPriceFlag = true;
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
        EsGoodsResponse response = esRetailGoodsInfoElasticService.pageByGoods(queryRequest);

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
            //重新赋值于Page内部对象
            Map<String, List<GoodsInfoVO>> voMap = goodsInfoList.stream().collect(Collectors.groupingBy
                    (GoodsInfoVO::getGoodsId));
            Boolean finalVipPriceFlag = vipPriceFlag;
            Set<Long> storeIds = new HashSet<>(10);
            for (EsGoods inner : response.getEsGoods()) {
                Set<Long> collect = inner.getGoodsInfos().stream().map(GoodsInfoNest::getStoreId).collect(Collectors.toSet());
                storeIds.addAll(collect);
            }

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
//                    List<Long> unOnline = new ArrayList<>(10);
//                    if (Objects.nonNull(queryRequest.getMatchWareHouseFlag()) && !queryRequest.getMatchWareHouseFlag()) {
//                        unOnline = commonUtil.getWareHouseByStoreId(new ArrayList<>(storeIds), WareHouseType.STORRWAREHOUSE).stream()
//                                .map(WareHouseVO::getWareId).collect(Collectors.toList());
//                    }
//                    List<Long> finalUnOnline = unOnline;

                    List<String> goodsIds = new ArrayList<>();
                    resultGoodsInfos.forEach(goodsInfoNest -> {
                        goodsIds.add(goodsInfoNest.getGoodsId());
//                        Optional<GoodsInfoNest> optionalGoodsInfoNest =
//                                goodsInfoNests.stream().filter((g) -> g.getGoodsInfoId().equals(goodsInfoNest.getGoodsInfoId())).findFirst();
//                        if (optionalGoodsInfoNest.isPresent()) {
//                            List<GoodsWareStockVO> goodsWareStockVOS = optionalGoodsInfoNest.get().getGoodsWareStockVOS();
//                            if (CollectionUtils.isNotEmpty(goodsWareStockVOS)) {
//                                List<GoodsWareStockVO> stockList;
//                                if (CollectionUtils.isNotEmpty(finalUnOnline)) {
//                                    stockList = goodsWareStockVOS.stream().
//                                            filter(goodsWareStock -> finalUnOnline.contains(goodsWareStock.getWareId())).
//                                            collect(Collectors.toList());
//                                } else {
//                                    stockList = goodsWareStockVOS;
//                                }
//                                if (CollectionUtils.isNotEmpty(stockList)) {
//                                    BigDecimal sumStock = stockList.stream().map(GoodsWareStockVO::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//                                    goodsInfoNest.setStock(sumStock);
//                                } else {
//                                    goodsInfoNest.setStock(BigDecimal.ZERO);
//                                }
//                                //活动限购库存
//                                if (Objects.nonNull(goodsInfoNest.getMarketingLabels())) {
//                                    goodsInfoNest.getMarketingLabels().forEach(marketingLabelVO -> {
//                                        if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
//                                                && marketingLabelVO.getGoodsPurchasingNumber() > 0
//                                                && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
//                                            goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
//                                        }
//                                    });
//                                }
//
//                                //重新填充商品状态
//                                if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
//                                        && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
//                                    goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
//                                    // 判断是否有T，如果是1，就设置为2
//                                    if (goodsInfoNest.getGoodsInfoName().endsWith("T") || goodsInfoNest.getGoodsInfoName().endsWith("t")) {
//                                        if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0) {
//                                            goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
//                                        }
//                                    } else {
//                                        if (goodsInfoNest.getStock().compareTo(BigDecimal.ONE) < 0) {
//                                            goodsInfoNest.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                                        }
//                                    }
//
//                                } else {
//                                    goodsInfoNest.setGoodsStatus(GoodsStatus.INVALID);
//                                }
//
//                                goodsInfoNest.setGoodsWareStockVOS(optionalGoodsInfoNest.get().getGoodsWareStockVOS());
//                            }
//                        }
                    });
                    List<GoodsImageVO> context = goodsImageProvider.getGoodsImagesByGoodsIds(goodsIds).getContext();

                    //设置企业商品的审核状态 ，以及会员的大客户价,及图片
                    resultGoodsInfos.forEach(goodsInfoNest -> {

                        if (queryRequest.getImageFlag() &&  CollectionUtils.isNotEmpty(context)) {
                            Map<String, List<GoodsImageVO>> goodsImagesMap = context.stream().collect(Collectors.groupingBy(GoodsImageVO::getGoodsId));

                            List<GoodsImageVO> goodsImages = goodsImagesMap.get(goodsInfoNest.getGoodsId());
                            if(CollectionUtils.isNotEmpty(goodsImages) && goodsImages.size() > 1){
                                goodsInfoNest.setGoodsInfoImg(goodsImages.get(1).getArtworkUrl());
                            }
                        }

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

    private GoodsInfoVO convertDateDesc(GoodsInfoVO goodsInfoVOs){
        Long shelflife = goodsInfoVOs.getShelflife();
        if (null == shelflife) return goodsInfoVOs;
        if (goodsInfoVOs.getDateUnit() != null){
            DateEnum dateEnum = DateEnum.getDateEnum(goodsInfoVOs.getDateUnit());
            if(dateEnum != null){
                Long convertShelflife = Objects.isNull(goodsInfoVOs.getDateUnit()) ? goodsInfoVOs.getShelflife():goodsInfoVOs.getShelflife() * dateEnum.getValue();
                goodsInfoVOs.setShelflife(convertShelflife);
                goodsInfoVOs.setShelflifeDesc(shelflife.toString().concat(dateEnum.getDesc()));
            }
        }
        if (StringUtils.isBlank(goodsInfoVOs.getShelflifeDesc())){
            goodsInfoVOs.setShelflifeDesc(shelflife+"天");
        }
        return goodsInfoVOs;
    }
}
