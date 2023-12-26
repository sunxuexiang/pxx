package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.provider.quicklogin.ThirdLoginRelationQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailWithNotDeleteByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomListRequest;
import com.wanmi.sbc.customer.api.request.quicklogin.ThirdLoginRelationByCustomerRequest;
import com.wanmi.sbc.customer.api.request.store.NoDeleteStoreByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailGetWithNotDeleteByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributorLevelByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.quicklogin.ThirdLoginRelationResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.enums.ThirdLoginType;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.DistributorLevelVO;
import com.wanmi.sbc.customer.bean.vo.LiveRoomVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.distribute.response.ShopInfoResponse;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.request.dto.EsGoodsInfoDTO;
import com.wanmi.sbc.es.elastic.response.EsGoodsInfoLimitResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsInfoResponse;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoSiteQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.prop.GoodsPropQueryProvider;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelListRequest;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoListByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoPageByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.prop.GoodsPropListAllByCateIdRequest;
import com.wanmi.sbc.goods.api.request.prop.GoodsPropListIndexByCateIdRequest;
import com.wanmi.sbc.goods.api.response.distributor.goods.DistributorGoodsInfoListByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.distributor.goods.DistributorGoodsInfoPageByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoDetailByGoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.prop.GoodsPropListAllByCateIdResponse;
import com.wanmi.sbc.goods.api.response.prop.GoodsPropListIndexByCateIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.service.GoodsEvaluateService;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsDetailFilterRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CoinActivityDetailResponse;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.dto.GoodsInfoDetailByGoodsInfoDTO;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityGoodsVo;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingForEndVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartNewPileProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.ProcurementConfigResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.shopcart.bean.enums.ProcurementTypeEnum;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品Controller
 * Created by daiyitian on 17/4/12.
 */
@Slf4j
@RestController
@RequestMapping("/goods")
@Api(tags = "GoodsInfoBaseController", description = "S2B web公用-商品信息API")
public class GoodsInfoBaseController {

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsInfoSiteQueryProvider goodsInfoSiteQueryProvider;

    @Autowired
    private PurchaseProvider purchaseProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsPropQueryProvider goodsPropQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private DistributorGoodsInfoQueryProvider distributorGoodsInfoQueryProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private ThirdLoginRelationQueryProvider thirdLoginRelationQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;

    @Autowired
    private LiveGoodsQueryProvider liveGoodsQueryProvider;
    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;

    @Autowired
    private GoodsEvaluateService goodsEvaluateService;

    @Autowired
    private PurchaseQueryProvider purchaseQueryProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private ShopCartProvider shopCartProvider;

    @Autowired
    private ShopCartNewPileProvider shopCartNewPileProvider;


    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    /**
     * 未登录时,查询商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "未登录时,查询商品分页")
    @RequestMapping(value = "/skuListFront", method = RequestMethod.POST)
    public BaseResponse<EsGoodsInfoLimitResponse> skuListFront(@RequestBody EsGoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        List<String> biddingKeywords = commonUtil.getKeywordsFromCache(BiddingType.KEY_WORDS_TYPE);
        if(Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }else{
            queryRequest.setIsKeywords(false);
        }
        if(CollectionUtils.isNotEmpty(biddingKeywords) && biddingKeywords.contains(queryRequest.getKeywords())){
            queryRequest.setIsKeywords(true);
        }
        EsGoodsInfoLimitResponse response = list(queryRequest, null,httpRequest);
        if (CollectionUtils.isNotEmpty(queryRequest.getEsGoodsInfoDTOList()) &&
                CollectionUtils.isNotEmpty(response.getEsGoodsInfoPage().getContent())) {
            Map<String, List<EsGoodsInfoDTO>> buyCountMap =
                    queryRequest.getEsGoodsInfoDTOList().stream()
                            .collect(Collectors.groupingBy(EsGoodsInfoDTO::getGoodsInfoId));

            response.getEsGoodsInfoPage().getContent().stream()
                    .filter(esGoodsInfo -> Objects.nonNull(esGoodsInfo.getGoodsInfo())
                            && buyCountMap.containsKey(esGoodsInfo.getGoodsInfo().getGoodsInfoId()))
                    .forEach(esGoodsInfo -> {
                        GoodsInfoNest goodsInfo = esGoodsInfo.getGoodsInfo();
                        goodsInfo.setBuyCount(
                                buyCountMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId()).get(0).getGoodsNum());
                    });
        }
        return BaseResponse.success(response);
    }

    /**
     * 商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品分页")
    @RequestMapping(value = "/skus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsInfoLimitResponse> list(@RequestBody EsGoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        queryRequest.setIsKeywords(false);
        if(StringUtils.isNotEmpty(queryRequest.getKeywords()) ) {
            String keywordsStr = esGoodsInfoElasticService.analyze(queryRequest.getKeywords());
            if (StringUtils.isNotEmpty(keywordsStr)) {
                List<String> biddingKeywords = commonUtil.getKeywordsFromCache(BiddingType.KEY_WORDS_TYPE);
                List<String> keyWords = Arrays.asList(keywordsStr.split(" "));
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(biddingKeywords) && biddingKeywords.stream().anyMatch(b -> keyWords.contains(b))) {
                    queryRequest.setIsKeywords(true);
                }
            }
        }
        if(Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        return BaseResponse.success(list(queryRequest, commonUtil.getCustomer(),httpRequest));
    }


    @ApiOperation(value = "根据分销员-会员ID查询店铺精选小店名称")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "distributorId", value = "分销员-会员id", required =
            true)
    @RequestMapping(value = "/shop-info/{distributorId}", method = RequestMethod.GET)
    public BaseResponse<ShopInfoResponse> getShopInfo(@PathVariable String distributorId) {
        ShopInfoResponse response = new ShopInfoResponse();
        BaseResponse<CustomerDetailGetWithNotDeleteByCustomerIdResponse> baseResponse = customerDetailQueryProvider
                .getCustomerDetailWithNotDeleteByCustomerId(new CustomerDetailWithNotDeleteByCustomerIdRequest
                        (distributorId));
        CustomerDetailGetWithNotDeleteByCustomerIdResponse customerDetailGetWithNotDeleteByCustomerIdResponse =
                baseResponse.getContext();
        if (Objects.nonNull(customerDetailGetWithNotDeleteByCustomerIdResponse)) {
            String customerName = customerDetailGetWithNotDeleteByCustomerIdResponse.getCustomerName();
            String shopName = distributionCacheService.getShopName();
            response.setShopName(customerName + "的" + shopName);

        }
        ThirdLoginRelationResponse thirdLoginRelationResponse = thirdLoginRelationQueryProvider
                .listThirdLoginRelationByCustomer
                        (ThirdLoginRelationByCustomerRequest.builder()
                                .customerId(distributorId)
                                .thirdLoginType(ThirdLoginType.WECHAT)
                                .build()).getContext();
        if (Objects.nonNull(thirdLoginRelationResponse) && Objects.nonNull(thirdLoginRelationResponse
                .getThirdLoginRelation())) {
            response.setHeadImg(thirdLoginRelationResponse.getThirdLoginRelation().getHeadimgurl());
        }
        return BaseResponse.success(response);
    }

    /**
     * 小C-店铺精选页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "小C-店铺精选页")
    @RequestMapping(value = "/shop/sku-list-to-c", method = RequestMethod.POST)
    public BaseResponse<EsGoodsInfoResponse> shopSkuListToC(@RequestBody EsGoodsInfoQueryRequest queryRequest) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainInfo)){
            queryRequest.setStoreId(domainInfo.getStoreId());
        }
        MicroServicePage<DistributorGoodsInfoVO> microServicePage = pageDistributorGoodsInfoPageByCustomerId
                (queryRequest, queryRequest.getCustomerId());
        List<DistributorGoodsInfoVO> distributorGoodsInfoVOList = microServicePage.getContent();
        if (CollectionUtils.isEmpty(distributorGoodsInfoVOList)) {
            return BaseResponse.success(new EsGoodsInfoResponse());
        }
        List<String> goodsIdList = distributorGoodsInfoVOList.stream().map(DistributorGoodsInfoVO::getGoodsInfoId)
                .collect(Collectors.toList());
        Map<String, String> map = distributorGoodsInfoVOList.stream().collect(Collectors.toMap
                (DistributorGoodsInfoVO::getGoodsInfoId, DistributorGoodsInfoVO::getId));
        queryRequest.setGoodsInfoIds(goodsIdList);
        queryRequest.setPageNum(0);
        queryRequest.setPageSize(goodsIdList.size());
        queryRequest.setDistributionGoodsAudit(DistributionGoodsAudit.CHECKED.toValue());
        queryRequest = wrapEsGoodsInfoQueryRequest(queryRequest);
        queryRequest.setCustomerLevelId(null);
        queryRequest.setCustomerLevelDiscount(null);
        EsGoodsInfoResponse response = esGoodsInfoElasticService.distributorGoodsListByCustomerId(queryRequest);
        response = filterDistributionGoods(queryRequest, response, map);
        if (Objects.nonNull(response.getEsGoodsInfoPage()) && CollectionUtils.isNotEmpty(response.getEsGoodsInfoPage
                ().getContent())) {
            response.setEsGoodsInfoPage(new PageImpl<>(response.getEsGoodsInfoPage().getContent(),  PageRequest.of
                    (microServicePage.getNumber(), microServicePage.getSize()), microServicePage
                    .getTotalElements()));
        }
        return BaseResponse.success(response);
    }

    /**
     * 分销员-我的店铺-店铺精选页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "分销员-我的店铺-店铺精选页")
    @RequestMapping(value = "/shop/sku-list", method = RequestMethod.POST)
    public BaseResponse<EsGoodsInfoResponse> shopSkuList(@RequestBody EsGoodsInfoQueryRequest queryRequest) {
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        Boolean saasStatus = Objects.nonNull(domainStoreRelaVO);
        if(saasStatus) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        MicroServicePage<DistributorGoodsInfoVO> microServicePage = pageDistributorGoodsInfoPageByCustomerId
                (queryRequest, commonUtil.getOperator().getUserId());
        List<DistributorGoodsInfoVO> distributorGoodsInfoVOList = microServicePage.getContent();
        if (CollectionUtils.isEmpty(distributorGoodsInfoVOList)) {
            return BaseResponse.success(new EsGoodsInfoResponse());
        }
        List<String> goodsIdList = distributorGoodsInfoVOList.stream().map(DistributorGoodsInfoVO::getGoodsInfoId)
                .collect(Collectors.toList());
        Map<String, String> map = distributorGoodsInfoVOList.stream().collect(Collectors.toMap
                (DistributorGoodsInfoVO::getGoodsInfoId, DistributorGoodsInfoVO::getId));
        queryRequest.setPageNum(0);
        queryRequest.setPageSize(goodsIdList.size());
        queryRequest.setGoodsInfoIds(goodsIdList);
        queryRequest.setDistributionGoodsAudit(DistributionGoodsAudit.CHECKED.toValue());
        queryRequest = wrapEsGoodsInfoQueryRequest(queryRequest);
        queryRequest.setCustomerLevelId(null);
        queryRequest.setCustomerLevelDiscount(null);
        EsGoodsInfoResponse response = esGoodsInfoElasticService.distributorGoodsListByCustomerId(queryRequest);
        response = filterDistributionGoods(queryRequest, response, map);
        if (Objects.nonNull(response.getEsGoodsInfoPage()) && CollectionUtils.isNotEmpty(response.getEsGoodsInfoPage
                ().getContent())) {
            response.setEsGoodsInfoPage(new PageImpl<>(response.getEsGoodsInfoPage().getContent(),  PageRequest.of
                    (microServicePage.getNumber(), microServicePage.getSize()), microServicePage
                    .getTotalElements()));
        }
        return BaseResponse.success(response);
    }

    /**
     * 分销员-我的店铺-选品功能(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "分销员-我的店铺-选品功能")
    @RequestMapping(value = "/shop/add-distributor-goods", method = RequestMethod.POST)
    public BaseResponse<EsGoodsInfoResponse> addDistributorGoods(@RequestBody EsGoodsInfoQueryRequest queryRequest) {

        DistributorGoodsInfoListByCustomerIdRequest request = new DistributorGoodsInfoListByCustomerIdRequest();
        request.setCustomerId(commonUtil.getOperator().getUserId());

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        Boolean saasStatus = Objects.nonNull(domainStoreRelaVO);
        if(saasStatus) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        BaseResponse<DistributorGoodsInfoListByCustomerIdResponse> baseResponse = distributorGoodsInfoQueryProvider.listByCustomerId(request);
        List<DistributorGoodsInfoVO> distributorGoodsInfoVOList = baseResponse.getContext()
                .getDistributorGoodsInfoVOList();
        List<String> goodsIdList = distributorGoodsInfoVOList.stream().map(DistributorGoodsInfoVO::getGoodsInfoId)
                .collect(Collectors.toList());
        Map<String, String> map = distributorGoodsInfoVOList.stream().collect(Collectors.toMap
                (DistributorGoodsInfoVO::getGoodsInfoId, DistributorGoodsInfoVO::getId));
        queryRequest.setDistributionGoodsAudit(DistributionGoodsAudit.CHECKED.toValue());
        queryRequest.setDistributionGoodsStatus(NumberUtils.INTEGER_ZERO);
        queryRequest = wrapEsGoodsInfoQueryRequest(queryRequest);
        queryRequest.setCustomerLevelId(null);
        queryRequest.setCustomerLevelDiscount(null);
        queryRequest.setDistributionGoodsInfoIds(goodsIdList);
        if(saasStatus){
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        EsGoodsInfoResponse response = esGoodsInfoElasticService.distributorGoodsList(queryRequest, goodsIdList);
        response = filterDistributionGoods(queryRequest, response, map);
        return BaseResponse.success(response);
    }


    /**
     * 未登录时，分页
     *
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "未登录时，查询商品分页")
    @RequestMapping(value = "/unLogin/skus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsInfoLimitResponse> unLoginList(@RequestBody EsGoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {
        return BaseResponse.success(list(queryRequest, null,httpRequest));
    }

    /**
     * 根据商品分类id查询索引的商品属性列表
     *
     * @param cateId 商品分类id
     * @return 索引的属性列表
     */
    @ApiOperation(value = "根据商品分类id查询索引的商品属性列表")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cateId", value = "商品分类id", required = true)
    @RequestMapping(value = "/props/{cateId}", method = RequestMethod.GET)
    public BaseResponse<List<GoodsPropVO>> listByCateId(@PathVariable Long cateId) {
        BaseResponse<GoodsPropListIndexByCateIdResponse> baseResponse = goodsPropQueryProvider.listIndexByCateId(new
                GoodsPropListIndexByCateIdRequest(cateId));
        GoodsPropListIndexByCateIdResponse goodsPropListIndexByCateIdResponse = baseResponse.getContext();
        if (Objects.isNull(goodsPropListIndexByCateIdResponse)) {
            return BaseResponse.success(Collections.emptyList());
        }
        return BaseResponse.success(goodsPropListIndexByCateIdResponse.getGoodsPropVOList());
    }

    /**
     * 根据商品分类id查询商品属性列表
     *
     * @param cateId 商品分类id
     * @return 所有属性列表
     */
    @ApiOperation(value = "根据商品分类id查询商品属性列表")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cateId", value = "商品分类id", required = true)
    @RequestMapping(value = "/props/all/{cateId}", method = RequestMethod.GET)
    public BaseResponse<List<GoodsPropVO>> propsList(@PathVariable Long cateId) {
        BaseResponse<GoodsPropListAllByCateIdResponse> baseResponse = goodsPropQueryProvider.listAllByCateId(new
                GoodsPropListAllByCateIdRequest(cateId));
        GoodsPropListAllByCateIdResponse goodsPropListAllByCateIdResponse = baseResponse.getContext();
        if (Objects.isNull(goodsPropListAllByCateIdResponse)) {
            return BaseResponse.success(Collections.emptyList());
        }
        return BaseResponse.success(goodsPropListAllByCateIdResponse.getGoodsPropVOList());
    }

    /**
     * 商品详情
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/sku/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsInfoDetailByGoodsInfoResponse> detail(@PathVariable String skuId, HttpServletRequest httpRequest) {
        return BaseResponse.success(detail(skuId, commonUtil.getCustomer(),httpRequest));
    }




    @ApiOperation(value = "未登录时查询商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/unLogin/sku/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsInfoDetailByGoodsInfoResponse> unLoginDetail(@PathVariable String skuId, HttpServletRequest httpRequest) {
        return BaseResponse.success(detail(skuId, null,httpRequest));
    }

    /**
     * 根据skuIds获取商品信息
     *
     * @param skuIds
     * @return
     */
    @ApiOperation(value = "根据skuIds获取商品信息")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewByIdsResponse> queryGoodsListBySkuIds(@RequestBody List<String> skuIds) {
        GoodsInfoViewByIdsRequest goodsInfoRequest = GoodsInfoViewByIdsRequest.builder()
                .goodsInfoIds(skuIds)
                .isHavSpecText(Constants.yes)
                .build();
        return goodsInfoQueryProvider.listViewByIds(goodsInfoRequest);
    }



    /**
     * es商品列表
     *
     * @param queryRequest 条件
     * @param customer     会员
     * @return es商品封装数据
     */
    private EsGoodsInfoLimitResponse list(EsGoodsInfoQueryRequest queryRequest, CustomerVO customer, HttpServletRequest httpRequest) {
        //用来防止生产的手动排序查询报错
        if(queryRequest.getSortFlag()==null){
            queryRequest.setSortFlag(10);
        }
        MarketingForEndVO marketingForEndVO = null;
        if (Objects.nonNull(queryRequest.getMarketingId())) {
            MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
            marketingGetByIdRequest.setMarketingId(queryRequest.getMarketingId());
            marketingForEndVO = marketingQueryProvider.getByIdForCustomer(marketingGetByIdRequest).getContext().getMarketingForEndVO();


            queryRequest.setGoodsInfoIds(
                    marketingForEndVO.getMarketingScopeList().stream()
                            .filter(v->{
                                if (v.getTerminationFlag().equals(BoolFlag.YES)){
                                    return false;
                                }
                                return true;
                            })
                            .map(MarketingScopeVO::getScopeId)
                            .collect(Collectors.toList()));
            //非PC端分销商品状态
            if(!Objects.equals(ChannelType.PC_MALL,commonUtil.getDistributeChannel().getChannelType())&&DefaultFlag.YES.equals(distributionCacheService.queryOpenFlag())){
                queryRequest.setExcludeDistributionGoods(Boolean.TRUE);
            }
        }

        if (Objects.nonNull(queryRequest.getActivityId())) {
            List<CouponActivityGoodsVO> couponActivityGoodsVOS = couponActivityQueryProvider.listCouponActivityGoods(CouponActivityGoodsRequest
                    .builder().activityIds(Arrays.asList(queryRequest.getActivityId())).build()).getContext().getCouponActivityGoodsVOS();
            if (Objects.nonNull(couponActivityGoodsVOS)) {
                queryRequest.setGoodsInfoIds(couponActivityGoodsVOS.stream()
                        .map(CouponActivityGoodsVO::getGoodsInfoId).collect(Collectors.toList()));
                //非PC端分销商品状态
                if(!Objects.equals(ChannelType.PC_MALL,commonUtil.getDistributeChannel().getChannelType())&&DefaultFlag.YES.equals(distributionCacheService.queryOpenFlag())){
                    queryRequest.setExcludeDistributionGoods(Boolean.TRUE);
                }
            }
        }

        if (Objects.nonNull(queryRequest.getCoinActivityId())) {
            BaseResponse<CoinActivityDetailResponse> detail = coinActivityProvider.detail(queryRequest.getCoinActivityId());
            if (Objects.nonNull(detail) && Objects.nonNull(detail.getContext()) && CollectionUtils.isNotEmpty(detail.getContext().getCoinActivityGoodsVoList())) {
                List<String> goodsInfoIds = detail.getContext().getCoinActivityGoodsVoList().stream().filter(o -> Objects.equals(o.getTerminationFlag(), BoolFlag.NO)).map(CoinActivityGoodsVo::getGoodsInfoId).collect(Collectors.toList());
                queryRequest.setGoodsInfoIds(goodsInfoIds);
                //非PC端分销商品状态
                if(!Objects.equals(ChannelType.PC_MALL,commonUtil.getDistributeChannel().getChannelType())&&DefaultFlag.YES.equals(distributionCacheService.queryOpenFlag())){
                    queryRequest.setExcludeDistributionGoods(Boolean.TRUE);
                }
            }

        }


        //只看分享赚商品信息
        if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue() == queryRequest.getDistributionGoodsAudit()){
            queryRequest.setDistributionGoodsStatus(NumberUtils.INTEGER_ZERO);
        }
        queryRequest = wrapEsGoodsInfoQueryRequest(queryRequest);

        EsGoodsInfoResponse response;
        if (Objects.nonNull(queryRequest.getCateId()) && queryRequest.getSortByCateBrand()) {
            CateBrandSortRelListRequest cateBrandSortRelListRequest = new CateBrandSortRelListRequest();
            cateBrandSortRelListRequest.setCateId(queryRequest.getCateId());
            List<CateBrandSortRelVO> cateBrandSortRelVOList =
                    cateBrandSortRelQueryProvider.list(cateBrandSortRelListRequest).getContext().getCateBrandSortRelVOList();
            //当前三级类目包含的品牌ID
            List<Long> cateBindBrandIds = cateBrandSortRelVOList.stream()
                    .sorted(Comparator.comparing(CateBrandSortRelVO::getSerialNo)) //按照排序字段升序排列
                    .map(item -> item.getBrandId()).collect(Collectors.toList());

            queryRequest.setCateBindBrandIds(cateBindBrandIds);
            queryRequest.putSort("goodsBrand.brandRelSeqNum", SortOrder.ASC);
            response = esGoodsInfoElasticService.page(queryRequest);
            //排序
            //对有效品类数据进行筛选
//        List<EsGoodsInfo> esGoodsInfos = response.getEsGoodsInfoPage().getContent();
//        List<EsGoodsInfo> esGoodsPart1 = esGoodsInfos.stream()
//                .filter(item -> cateBindBrandIds.contains(item.getGoodsBrand().getBrandId()))
//                .sorted(Comparator.comparing(EsGoodsInfo::getGoodsBrand, (a, b) -> {
//                            for (Long cateBindBrandId : cateBindBrandIds) {
//                                if (a.getBrandId().equals(cateBindBrandId) || b.getBrandId().equals(cateBindBrandId)) {
//                                    if (a.getBrandId().equals(b.getBrandId())) {
//                                        return 0;
//                                    } else if (a.getBrandId().equals(cateBindBrandId)) {
//                                        return -1;
//                                    } else {
//                                        return 1;
//                                    }
//                                }
//                            }
//                            return 0;
//                        })
//                ).collect(Collectors.toList());
//            List<EsGoodsInfo> esGoodsPart2 = esGoodsInfos.stream()
//                    .filter(item -> !cateBindBrandIds.contains(item.getGoodsBrand().getBrandId()))
//                    .collect(Collectors.toList());
//            //装回去
//            esGoodsPart1.addAll(esGoodsPart2);
//            response.setEsGoodsInfoPage(new PageImpl<>(esGoodsPart1,  PageRequest.of(queryRequest.getPageNum(),
//                    queryRequest.getPageSize()), response.getEsGoodsInfoPage().getTotalElements()));
        }else {
            response = esGoodsInfoElasticService.page(queryRequest);
        }
        if (Objects.nonNull(marketingForEndVO)) {
            response.setIsOverlap(marketingForEndVO.getIsOverlap());
        }
        response = calIntervalPriceAndMarketingPrice(response,customer,queryRequest);

        if(Objects.nonNull(customer)){
            //通过商品指定区域设置商品状态
            CustomerDeliveryAddressResponse deliveryAddress =  commonUtil.getProvinceCity(httpRequest);
            if (Objects.nonNull(deliveryAddress)){
                response.getEsGoodsInfoPage().forEach(page ->{
                    if (StringUtils.isNotBlank(page.getGoodsInfo().getAllowedPurchaseArea())){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(page.getGoodsInfo().getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                            page.getGoodsInfo().setGoodsStatus(GoodsStatus.QUOTA);
                        }

                    }
                });
            }
        }

        EsGoodsInfoLimitResponse page = KsBeanUtil.convert(response, EsGoodsInfoLimitResponse.class);
        page.setEsGoodsInfoPage(response.getEsGoodsInfoPage());

        // 获取直播商品
        Map<String, Long> liveMapLong = new HashMap<>();
        List<String> goodsInfoIds = page.getEsGoodsInfoPage().stream().map(EsGoodsInfo::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            // 根据商品id,查询直播商品的id
            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(liveGoodsVOList)) {
                liveMapLong =
                        liveGoodsVOList.stream().filter(entity -> {
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
                page.getEsGoodsInfoPage().stream().forEach(item -> {
                    Long liveGoodsId = liveMap.get(item.getId());
                    if (liveGoodsId != null) {
                        if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId)) {
                            List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + liveGoodsId, GoodsLiveRoomVO.class);
                            if (CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
                                for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
                                    if (liveRoomIdList.contains(liveRoomVO.getRoomId())) {
                                        item.getGoodsInfo().setLiveEndTime(liveRoomVO.getLiveEndTime());
                                        item.getGoodsInfo().setLiveStartTime(liveRoomVO.getLiveStartTime());
                                        item.getGoodsInfo().setRoomId(liveRoomVO.getRoomId());
                                        break;
                                    }
                                }

                            }
                        }
                    }
                });
            }
        }

        HashMap<String, List<GoodsLabelVO>> map = new HashMap<>();
        page.getEsGoodsInfoPage().forEach(item->{
            map.put(item.getGoodsInfo().getGoodsId(), item.getGoodsLabels());
        });
        page.getGoodsList().stream().forEach(item->{
            if (map.containsKey(item.getGoodsId())) {
                item.setGoodsLabels(map.get(item.getGoodsId()));
            }
        });

        //控制标签排序和显示
        page.getEsGoodsInfoPage().getContent().stream().forEach(item -> {
            List<GoodsLabelVO> collect = item.getGoodsLabels().stream()
                    .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                    .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                    .collect(Collectors.toList());
            item.setGoodsLabels(collect);
            if(Objects.nonNull(item.getGoodsInfo()) && Objects.nonNull(item.getGoodsInfo().getGoodsInfoType()) && item.getGoodsInfo().getGoodsInfoType() == 1){
                item.getGoodsInfo().setMarketPrice(item.getGoodsInfo().getSpecialPrice());
                item.getGoodsInfo().setSalePrice(item.getGoodsInfo().getSpecialPrice());
            }
        });
        page.getGoodsList().stream().forEach(item -> {
            List<GoodsLabelVO> collect = item.getGoodsLabels().stream()
                    .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                    .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                    .collect(Collectors.toList());
            item.setGoodsLabels(collect);
        });
        List<EsGoodsInfo> results = new ArrayList<>();
        page.getEsGoodsInfoPage().stream().forEach(goods->{
            if (Objects.nonNull(goods.getGoodsInfo().getGoodsStatus()) && Objects.nonNull(goods.getGoodsInfo().getStock()) && goods.getGoodsInfo().getGoodsStatus().equals(GoodsStatus.OK) && goods.getGoodsInfo().getStock().compareTo(BigDecimal.ZERO) > 0) {
                results.add(goods);
            }
        });
        page.setEsGoodsInfoPage(new PageImpl<>(results, page.getEsGoodsInfoPage().getPageable(),results.size()));
        return page;
    }

    /**
     * 包装EsGoodsInfoQueryRequest搜索对象
     *
     * @param queryRequest
     * @return
     */
    private EsGoodsInfoQueryRequest wrapEsGoodsInfoQueryRequest(EsGoodsInfoQueryRequest queryRequest) {
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        queryRequest.setContractStartDate(now);
        queryRequest.setContractEndDate(now);
        queryRequest.setCustomerLevelId(0L);
        queryRequest.setCustomerLevelDiscount(BigDecimal.ONE);
        return queryRequest;
    }

    /**
     * 小B-店铺管理页，选品页、编辑，小C-店铺精选页，分销商品统一验证接口
     *
     * @param queryRequest
     * @param response
     * @return
     */
    private EsGoodsInfoResponse filterDistributionGoods(EsGoodsInfoQueryRequest queryRequest, EsGoodsInfoResponse
            response, Map<String, String> map) {
        List<EsGoodsInfo> esGoodsInfoList = response.getEsGoodsInfoPage().getContent();
        if (CollectionUtils.isNotEmpty(esGoodsInfoList)) {


            List<GoodsInfoVO> goodsInfoList = esGoodsInfoList.stream().filter(e -> Objects.nonNull(e.getGoodsInfo()))
                    .map(e -> KsBeanUtil.convert(e.getGoodsInfo(), GoodsInfoVO.class))
                    .collect(Collectors.toList());
            queryRequest.setDistributionGoodsAudit(DistributionGoodsAudit.CHECKED.toValue());
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsInfoResponse();
            }

            String customerId = commonUtil.getOperatorId();
            BaseResponse<DistributorLevelByCustomerIdResponse> baseResponse = null;
            if (StringUtils.isNotBlank(customerId)){
                baseResponse = distributionService.getByCustomerId(customerId);
            }
            final BaseResponse<DistributorLevelByCustomerIdResponse> resultBaseResponse = baseResponse;
            //重新赋值于Page内部对象
            Map<String, GoodsInfoNest> voMap = goodsInfoList.stream()
                    .collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> KsBeanUtil.convert(g, GoodsInfoNest
                            .class), (s, a) -> s));
            response.getEsGoodsInfoPage().getContent().forEach(esGoodsInfo -> {
                GoodsInfoNest goodsInfoNest = esGoodsInfo.getGoodsInfo();
                if (Objects.nonNull(goodsInfoNest)) {
                    goodsInfoNest = voMap.get(goodsInfoNest.getGoodsInfoId());
                    if (Objects.nonNull(goodsInfoNest)) {
                        goodsInfoNest.setDistributionGoodsInfoId(map.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId()));
                        DistributorLevelVO distributorLevelVO = Objects.isNull(resultBaseResponse) ? null : resultBaseResponse.getContext().getDistributorLevelVO();
                        if (Objects.nonNull(distributorLevelVO) && Objects.nonNull(distributorLevelVO.getCommissionRate()) && DistributionGoodsAudit.CHECKED == goodsInfoNest.getDistributionGoodsAudit()) {
                            goodsInfoNest.setDistributionCommission(distributionService.calDistributionCommission(goodsInfoNest.getDistributionCommission(),distributorLevelVO.getCommissionRate()));
                        }
                        esGoodsInfo.setGoodsInfo(goodsInfoNest);
                    }
                }
            });
        }
        return response;
    }

    /**
     * 分页查询分销员商品列表
     *
     * @param queryRequest
     * @param customerId
     * @return
     */
    private MicroServicePage<DistributorGoodsInfoVO> pageDistributorGoodsInfoPageByCustomerId(EsGoodsInfoQueryRequest
                                                                                                      queryRequest,
                                                                                              String customerId) {
        DistributorGoodsInfoPageByCustomerIdRequest distributorGoodsInfoPageByCustomerIdRequest = new
                DistributorGoodsInfoPageByCustomerIdRequest();
        distributorGoodsInfoPageByCustomerIdRequest.setCustomerId(customerId);
        distributorGoodsInfoPageByCustomerIdRequest.setPageNum(queryRequest.getPageNum());
        distributorGoodsInfoPageByCustomerIdRequest.setPageSize(queryRequest.getPageSize());
        BaseResponse<DistributorGoodsInfoPageByCustomerIdResponse> baseResponse = distributorGoodsInfoQueryProvider
                .pageByCustomerId(distributorGoodsInfoPageByCustomerIdRequest);
        MicroServicePage<DistributorGoodsInfoVO> microServicePage = baseResponse.getContext().getMicroServicePage();
        return microServicePage;
    }

    /**
     * 过滤符合条件的分销商品数据
     *
     * @param goodsInfoList
     * @param queryRequest
     * @return
     */
    private List<GoodsInfoVO> filterDistributionGoods(List<GoodsInfoVO> goodsInfoList, EsGoodsInfoQueryRequest
            queryRequest) {
        //根据开关重新设置分销商品标识
        distributionService.checkDistributionSwitch(goodsInfoList);
        //只看分享赚商品信息
        if (Objects.nonNull(queryRequest.getDistributionGoodsAudit()) && DistributionGoodsAudit.CHECKED.toValue() ==
                queryRequest.getDistributionGoodsAudit()) {
            goodsInfoList = goodsInfoList.stream().filter(goodsInfoVO -> DistributionGoodsAudit.CHECKED.equals
                    (goodsInfoVO.getDistributionGoodsAudit())).collect(Collectors.toList());
        }
        return goodsInfoList;
    }

    /**
     * 计算区间价、营销价
     *
     * @param response
     * @param customer
     * @return
     */
    private EsGoodsInfoResponse calIntervalPriceAndMarketingPrice(EsGoodsInfoResponse response, CustomerVO customer,
                                                                  EsGoodsInfoQueryRequest queryRequest) {
        List<EsGoodsInfo> esGoodsInfoList = response.getEsGoodsInfoPage().getContent();
        Boolean vipPriceFlag = false;
        if(Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
            vipPriceFlag = true;
        }
        if (CollectionUtils.isNotEmpty(esGoodsInfoList)) {
            List<GoodsInfoVO> goodsInfoList = esGoodsInfoList.stream().filter(e -> Objects.nonNull(e.getGoodsInfo()))
                    .map(e -> KsBeanUtil.convert(e.getGoodsInfo(), GoodsInfoVO.class))
                    .collect(Collectors.toList());

            goodsInfoList = filterDistributionGoods(goodsInfoList, queryRequest);
            if (CollectionUtils.isEmpty(goodsInfoList)) {
                return new EsGoodsInfoResponse();
            }
            //查询是否开启囤货功能
            BaseResponse<ProcurementConfigResponse> results = purchaseQueryProvider.queryProcurementConfig();
            if (results.getContext().getProcurementType() == ProcurementTypeEnum.STOCKUP) {
                //查询商品限购数量
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
                        goodsInfo.setVirtualStock(goodsInfoVO.getVirtualStock());
                    }
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

            //填充购物车数量
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest purchaseFillBuyCountRequest = new PurchaseFillBuyCountRequest();
                purchaseFillBuyCountRequest.setGoodsInfoList(goodsInfoList);
                purchaseFillBuyCountRequest.setCustomerId(customer.getCustomerId());
                purchaseFillBuyCountRequest.setInviteeId("0");
                //囤货购物车
                if(Objects.isNull(queryRequest.getPurchaseType()) || queryRequest.getPurchaseType() == NumberUtils.INTEGER_ZERO){
                    PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = purchaseProvider.fillBuyCount
                            (purchaseFillBuyCountRequest).getContext();
                    goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
                }else{
                    //正常购物车
                    PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount
                            (purchaseFillBuyCountRequest).getContext();
                    goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
                }
                if(Objects.isNull(queryRequest.getNewPurchaseType()) || queryRequest.getNewPurchaseType() == NumberUtils.INTEGER_ZERO){
                    PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartNewPileProvider.fillBuyCount
                            (purchaseFillBuyCountRequest).getContext();
                    goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
                } else {
                    //正常购物车
                    PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount
                            (purchaseFillBuyCountRequest).getContext();
                    goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
                }
            }

//            Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(goodsInfoList
//                    .stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();

            //重新赋值于Page内部对象
            Map<String, GoodsInfoNest> voMap = goodsInfoList.stream()
                    .collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> KsBeanUtil.convert(g, GoodsInfoNest.class), (s, a) -> s));
            Boolean finalVipPriceFlag = vipPriceFlag;
            response.getEsGoodsInfoPage().getContent().forEach(esGoodsInfo -> {
                GoodsInfoNest goodsInfo = esGoodsInfo.getGoodsInfo();

                GoodsInfoNest goodsInfoNest =  voMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId());
                goodsInfoNest.setGoodsEvaluateNum(goodsInfo.getGoodsEvaluateNum());
                goodsInfoNest.setGoodsCollectNum(goodsInfo.getGoodsCollectNum());
                goodsInfoNest.setGoodsFavorableCommentNum(goodsInfo.getGoodsFavorableCommentNum());
                goodsInfoNest.setGoodsFeedbackRate(goodsInfo.getGoodsFeedbackRate());
                goodsInfoNest.setGoodsSalesNum(goodsInfo.getGoodsSalesNum());
                goodsInfoNest.setEnterPriseAuditStatus(goodsInfo.getEnterPriseAuditStatus());
                goodsInfoNest.setSpecialPrice(goodsInfo.getSpecialPrice());
                goodsInfoNest.setGoodsSubtitle(goodsInfo.getGoodsSubtitle());
                if(goodsInfo.getIsScatteredQuantitative() == null){
                    goodsInfoNest.setIsScatteredQuantitative(0);
                }else{
                    goodsInfoNest.setIsScatteredQuantitative(goodsInfo.getIsScatteredQuantitative());
                }
                if(finalVipPriceFlag
                        && Objects.nonNull(goodsInfoNest.getVipPrice())
                        && goodsInfoNest.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                    goodsInfoNest.setSalePrice(goodsInfoNest.getVipPrice());
                }
                if(Objects.nonNull(goodsInfoNest)){
                    if (CollectionUtils.isNotEmpty(goodsInfoNest.getMarketingLabels())) {
                        //活动限购库存
                        /**
                         * 注释的原因：这个版本没有营销限购
                         */
//                        goodsInfoNest.getMarketingLabels().forEach(marketingLabelVO -> {
//                            if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber()) && marketingLabelVO.getGoodsPurchasingNumber() > 0) {
//                                if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
//                                        && marketingLabelVO.getGoodsPurchasingNumber() > 0
//                                        && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(goodsInfoNest.getStock()) < 0) {
//                                    goodsInfoNest.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
//                                }
//                            }
//                        });
                        //vip价格不参与营销活动设置为0
                        goodsInfoNest.setVipPrice(BigDecimal.ZERO);
                    }
                    goodsInfoNest.getMarketingLabels().stream().forEach(marketingLabelVO -> {
                        if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                && marketingLabelVO.getSubType() == 1 && marketingLabelVO.getNumber() == 1){
                            goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice().subtract(marketingLabelVO.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                        }else if (Objects.nonNull(marketingLabelVO.getSubType()) && Objects.nonNull(marketingLabelVO.getNumber())
                                && marketingLabelVO.getSubType() == 3 && marketingLabelVO.getNumber() == 1){
                            goodsInfoNest.setTheirPrice(goodsInfoNest.getMarketPrice()
                                    .multiply(marketingLabelVO.getFullFold().divide(new BigDecimal(10)))
                                    .setScale(2,BigDecimal.ROUND_HALF_UP));
                        }
//                        calGoodsInfoNestStock(goodsInfoNest,goodsNumsMap);
//                        addActivityRestricted(goodsInfoNest,marketingLabelVO,results);
                    });
                    esGoodsInfo.setGoodsInfo(goodsInfoNest);
                }
            });
        }
        return response;
    }

    /**
     * 商品详情
     *
     * @param skuId    商品skuId
     * @param customer 会员
     * @return 商品详情
     */
    private GoodsInfoDetailByGoodsInfoResponse detail(String skuId, CustomerVO customer, HttpServletRequest httpRequest) {
        //获取会员和等级
        GoodsInfoRequest goodsInfoRequest = new GoodsInfoRequest();
        goodsInfoRequest.setGoodsInfoId(skuId);
        GoodsInfoDetailByGoodsInfoResponse response = goodsInfoSiteQueryProvider.getByGoodsInfo(goodsInfoRequest)
                .getContext();
        GoodsInfoVO goodsInfoVO = response.getGoodsInfo();
        //计算区间价
        GoodsIntervalPriceByCustomerIdRequest goodsIntervalPriceByCustomerIdRequest = new
                GoodsIntervalPriceByCustomerIdRequest();
        goodsIntervalPriceByCustomerIdRequest.setGoodsInfoDTOList(KsBeanUtil.convert(Arrays.asList(response
                .getGoodsInfo()), GoodsInfoDTO.class));
        if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
            goodsIntervalPriceByCustomerIdRequest.setCustomerId(customer.getCustomerId());
        }
        GoodsIntervalPriceByCustomerIdResponse goodsIntervalPriceByCustomerIdResponse = goodsIntervalPriceProvider
                .putByCustomerId(goodsIntervalPriceByCustomerIdRequest).getContext();
        response.setGoodsIntervalPrices(goodsIntervalPriceByCustomerIdResponse.getGoodsIntervalPriceVOList());
        if (CollectionUtils.isNotEmpty(goodsIntervalPriceByCustomerIdResponse.getGoodsInfoVOList())) {
            response.setGoodsInfo(goodsIntervalPriceByCustomerIdResponse.getGoodsInfoVOList().get(0));
        }

        //计算营销价格
        MarketingPluginGoodsDetailFilterRequest filterRequest = new MarketingPluginGoodsDetailFilterRequest();
        filterRequest.setGoodsInfoDetailByGoodsInfoDTO(KsBeanUtil.convert(response, GoodsInfoDetailByGoodsInfoDTO
                .class));
        if (Objects.nonNull(customer)) {
            filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            //判断商品是否在指定销售区域 add by jiangxin 20210913
            CustomerDeliveryAddressResponse deliveryAddress =  commonUtil.getProvinceCity(httpRequest);
            if (Objects.nonNull(deliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                        .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                    goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                }
            }
        }
        response = KsBeanUtil.convert(marketingPluginProvider.goodsDetailFilter(filterRequest).getContext(),
                GoodsInfoDetailByGoodsInfoResponse.class);

        if (Objects.nonNull(response.getGoods().getStoreId())) {
            StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(response.getGoods()
                    .getStoreId())).getContext().getStoreVO();
            response.setStoreLogo(store.getStoreLogo());
            response.setStoreName(store.getStoreName());
            response.setCompanyType(store.getCompanyType());
            response.setStoreId(store.getStoreId());
        }
        return response;
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

    private void addActivityRestricted(GoodsInfoNest goodsInfoNest,MarketingLabelVO marketingLabelVO,BaseResponse<ProcurementConfigResponse> results){
        if (Objects.nonNull(results.getContext()) && results.getContext().getProcurementType() == ProcurementTypeEnum.STOCKUP){
            //囤货有活动id，添加活动库存
            if (Objects.equals(DeleteFlag.NO, goodsInfoNest.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfoNest.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoNest.getAddedFlag())) {
                if (Objects.nonNull(goodsInfoNest.getMarketingId()) && Objects.nonNull(marketingLabelVO.getMarketingId())
                        && goodsInfoNest.getMarketingId().equals(marketingLabelVO.getMarketingId())) {
                    //限购数是否为0
                    if (goodsInfoNest.getPurchaseNum() > 0) {
                        goodsInfoNest.setGoodsStatus(GoodsStatus.OK);
                        goodsInfoNest.setStock(BigDecimal.valueOf(goodsInfoNest.getPurchaseNum()));
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
}
