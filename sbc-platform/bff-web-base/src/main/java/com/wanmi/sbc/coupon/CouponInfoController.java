package com.wanmi.sbc.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.coupon.request.CouponGoodsPageRequest;
import com.wanmi.sbc.coupon.request.CouponInfoQueryRequest;
import com.wanmi.sbc.coupon.response.CouponCacheCenterPageResponseNew;
import com.wanmi.sbc.coupon.response.CouponGoodsPageResponse;
import com.wanmi.sbc.coupon.service.CouponService;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCacheProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCacheCenterByIdsPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCacheCenterPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCacheListForGoodsDetailRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCacheListForGoodsListRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponGoodsListRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoStoreIdsQueryRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCacheCenterPageResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCacheCenterResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCacheListForGoodsListResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponGoodsListResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponInfosQueryResponse;
import com.wanmi.sbc.marketing.bean.vo.CouponVO;
import com.wanmi.sbc.marketing.bean.vo.StoreCouponVO;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.util.CommonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by CHENLI on 2018/9/25.
 */
@RestController
@RequestMapping("/coupon-info")
@Api(tags = "CouponInfoController", description = "S2B web公用-优惠券信息API")
@Slf4j
public class CouponInfoController {

    @Autowired
    private CouponCacheProvider couponCacheProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private PurchaseProvider purchaseProvider;

    @Autowired
    private ShopCartProvider shopCartProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;
    
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private CouponService couponService;
    
    /**
     * 未登录时，领券中心列表
     *
     * @return
     */
    @ApiOperation(value = "未登录时，领券中心列表")
    @RequestMapping(value = "/front/center", method = RequestMethod.POST)
    public BaseResponse<CouponCacheCenterPageResponse> getCouponStartedFront(@RequestBody CouponCacheCenterPageRequest queryRequest) {

        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        List<Long> wareIds = new ArrayList<>(2);
        wareIds.add(-1L);
        wareIds.add(queryRequest.getWareId());
        queryRequest.setWareIds(wareIds);

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCacheProvider.pageCouponStarted(queryRequest);
    }

    /**
     * 领券中心列表
     *
     * @return
     */
    @ApiOperation(value = "登录后，领券中心列表")
    @RequestMapping(value = "/center", method = RequestMethod.POST)
    public BaseResponse<CouponCacheCenterPageResponse> getCouponStarted(@RequestBody CouponCacheCenterPageRequest queryRequest) {
        queryRequest.setCustomerId(commonUtil.getOperatorId());
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        List<Long> wareIds = new ArrayList<>(2);
        wareIds.add(-1L);
        wareIds.add(queryRequest.getWareId());
        queryRequest.setWareIds(wareIds);
        queryRequest.setSendType(0L);

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }

        return couponCacheProvider.pageCouponStarted(queryRequest);
    }
    
    /**
     * 领券中心列表（商家入驻版本）
     *
     * @return
     */
    @ApiOperation(value = "登录后，领券中心列表")
    @RequestMapping(value = "/centerNew", method = RequestMethod.POST)
    public BaseResponse<CouponCacheCenterPageResponseNew> getCouponStartedNew(@RequestBody CouponCacheCenterPageRequest queryRequest) {
    	log.info("====进入/coupon-info/centerNew====");
    	
        queryRequest.setCustomerId(commonUtil.getOperatorId());
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        List<Long> wareIds = new ArrayList<>(2);
        wareIds.add(-1L);
        wareIds.add(queryRequest.getWareId());
        queryRequest.setWareIds(wareIds);
        queryRequest.setSendType(0L);

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        // 查出所有优惠券
        queryRequest.setPageSize(9999);
        BaseResponse<CouponCacheCenterPageResponse> pageCouponStarted = couponCacheProvider.pageCouponStarted(queryRequest);
        
        List<CouponVO> couponViews = pageCouponStarted.getContext().getCouponViews().getContent();
        
        List<StoreCouponVO> couponVOList = couponService.parseCouponVOList(couponViews);
        return  BaseResponse.success(CouponCacheCenterPageResponseNew.builder().storeCouponVOList(couponVOList).build());
        
    }
   

    /**
     * 领券中心列表
     *
     * @return
     */
    @ApiOperation(value = "登录后，领券中心列表过滤商户和平台卷")
    @RequestMapping(value = "/centerByStores", method = RequestMethod.POST)
    public BaseResponse<CouponCacheCenterPageResponse> centerByStores(@RequestBody CouponCacheCenterPageRequest queryRequest) {
        queryRequest.setCustomerId(commonUtil.getOperatorId());
        List<Long> wareIds = new ArrayList<>();
        //获取仓库所属地的优惠券信息
        wareIds.add(commonUtil.getWareId(HttpUtil.getRequest()));
        //查询所有区域都可用的优惠券信息
        wareIds.add(-1L);
        queryRequest.setWareIds(wareIds);
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        queryRequest.setSendType(0L); // 0代表普通赠券

        if (CollectionUtils.isEmpty(queryRequest.getStoreIds()) && queryRequest.getStoreIds().size()<1){
            throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
        }
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        CouponCacheCenterPageResponse couponCacheCenterPageResponse = couponCacheProvider.pageCouponStarted(queryRequest).getContext();
            couponCacheCenterPageResponse.getCouponViews().getContent().stream().filter(v->{
                for (String storeid:queryRequest.getStoreIds()){
                    if (v.getPlatformFlag().equals(DefaultFlag.NO) && storeid.equalsIgnoreCase(String.valueOf(v.getStoreId()))){
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());

        return BaseResponse.success(couponCacheCenterPageResponse);
    }
    
    /**
     * 登录后，购物车商家显示优惠券
     *
     * @return
     */
    @ApiOperation(value = "登录后，购物车商家显示优惠券")
    @RequestMapping(value = "/centerByStoresNew", method = RequestMethod.POST)
    public BaseResponse<CouponCacheCenterPageResponseNew> centerByStoresNew(@RequestBody CouponCacheCenterPageRequest queryRequest) {
    	log.info("====进入/coupon-info/centerByStoresNew====");
        queryRequest.setCustomerId(commonUtil.getOperatorId());
        List<Long> wareIds = new ArrayList<>();
        //获取仓库所属地的优惠券信息
        wareIds.add(commonUtil.getWareId(HttpUtil.getRequest()));
        //查询所有区域都可用的优惠券信息
        wareIds.add(-1L);
        queryRequest.setWareIds(wareIds);
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        queryRequest.setSendType(0L); // 0代表普通赠券

        if (CollectionUtils.isEmpty(queryRequest.getStoreIds()) && queryRequest.getStoreIds().size()<1){
            throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
        }
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }
        // 查出所有优惠券
        queryRequest.setPageSize(9999);
        CouponCacheCenterPageResponse couponCacheCenterPageResponse = couponCacheProvider.pageCouponStarted(queryRequest).getContext();
		List<CouponVO> couponViews = couponCacheCenterPageResponse.getCouponViews().getContent().stream().filter(v -> {
			for (String storeid : queryRequest.getStoreIds()) {
				// 只保留对应商家的优惠券
				if (v.getPlatformFlag().equals(DefaultFlag.NO) && storeid.equalsIgnoreCase(String.valueOf(v.getStoreId()))) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());

        List<StoreCouponVO> couponVOList = couponService.parseCouponVOList(couponViews);
        return  BaseResponse.success(CouponCacheCenterPageResponseNew.builder().storeCouponVOList(couponVOList).build());
    }

    /**
     * 领券中心列表
     *
     * @return
     */
    @ApiOperation(value = "登录后，领券中心列表")
    @RequestMapping(value = "/getCouponByIds", method = RequestMethod.POST)
    public BaseResponse<CouponCacheCenterResponse> getCouponByIds(@RequestBody CouponCacheCenterByIdsPageRequest queryRequest) {
        queryRequest.setCustomerId(commonUtil.getOperatorId());

      /*  DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            queryRequest.setStoreId(domainStoreRelaVO.getStoreId());
        }*/

        return couponCacheProvider.getCouponStartedById(queryRequest);
    }

    /**
     * 未登录时,通过商品id列表，查询与商品相关优惠券
     * 购物车 - 优惠券列表
     *
     * @return
     */
    @ApiOperation(value = "未登录时，通过商品id列表，查询与商品相关优惠券")
    @RequestMapping(value = "/front/goods-list", method = RequestMethod.POST)
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsListFront(@RequestBody List<String> goodsInfoIds) {
        CouponCacheListForGoodsListRequest request = new CouponCacheListForGoodsListRequest();
        request.setGoodsInfoIds(goodsInfoIds);
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCacheProvider.listCouponForGoodsList(request);
    }

    /**
     * 未登录时,通过商品id列表，查询与商品相关优惠券
     * 购物车 - 优惠券列表
     *
     * @return
     */
    @ApiOperation(value = "未登录时，通过商品id列表，查询与商品相关优惠券")
    @RequestMapping(value = "/front/goods-list/forApp", method = RequestMethod.POST)
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsListFrontForApp(@RequestBody CouponInfoQueryRequest queryRequest) {
        CouponCacheListForGoodsListRequest request = new CouponCacheListForGoodsListRequest();
        request.setGoodsInfoIds(queryRequest.getGoodsInfoIds());
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCacheProvider.listCouponForGoodsList(request);
    }


    /**
     * 通过商品id列表，查询与商品相关优惠券
     * 购物车 - 优惠券列表
     *
     * @return
     */
    @ApiOperation(value = "登录后，通过商品id列表，查询与商品相关优惠券")
    @RequestMapping(value = "/goods-list/forApp", method = RequestMethod.POST)
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsListForApp(@RequestBody CouponInfoQueryRequest queryRequest) {
        CouponCacheListForGoodsListRequest request = new CouponCacheListForGoodsListRequest();
        request.setGoodsInfoIds(queryRequest.getGoodsInfoIds());
        request.setCustomerId(commonUtil.getOperatorId());
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCacheProvider.listCouponForGoodsList(request);
    }


    /**
     * 通过商品id列表，查询与商品相关优惠券
     * 购物车 - 优惠券列表
     *
     * @return
     */
    @ApiOperation(value = "登录后，通过商品id列表，查询与商品相关优惠券")
    @RequestMapping(value = "/goods-list", method = RequestMethod.POST)
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsList(@RequestBody List<String> goodsInfoIds)  {
        CouponCacheListForGoodsListRequest request = new CouponCacheListForGoodsListRequest();
        request.setGoodsInfoIds(goodsInfoIds);
        request.setCustomerId(commonUtil.getOperatorId());
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCacheProvider.listCouponForGoodsList(request);
    }

    /**
     * 未登录时,通过商品Id，查询单个商品相关优惠券
     *
     * @return
     */
    @ApiOperation(value = "未登录时，通过商品Id，查询单个商品相关优惠券")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsInfoId", value = "商品Id", required = true)
    @RequestMapping(value = "/front/goods-detail/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsDetailFront(@PathVariable String goodsInfoId) {

        CouponCacheListForGoodsDetailRequest request = new CouponCacheListForGoodsDetailRequest();
        request.setGoodsInfoId(goodsInfoId);

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        CouponCacheListForGoodsListResponse goodsDetailResponse =
                couponCacheProvider.listCouponForGoodsDetail(request).getContext();

        return BaseResponse.success(goodsDetailResponse);
    }

    /**
     * 通过商品Id，查询单个商品相关优惠券
     *
     * @return
     */
    @ApiOperation(value = "登录后，通过商品Id，查询单个商品相关优惠券")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsInfoId", value = "商品Id", required = true)
    @RequestMapping(value = "/goods-detail/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse<CouponCacheListForGoodsListResponse> listCouponForGoodsDetail(@PathVariable String goodsInfoId) {
        CouponCacheListForGoodsDetailRequest request = new CouponCacheListForGoodsDetailRequest();
        request.setGoodsInfoId(goodsInfoId);
        request.setCustomerId(commonUtil.getOperatorId());
        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            request.setStoreId(domainStoreRelaVO.getStoreId());
        }
        return couponCacheProvider.listCouponForGoodsDetail(request);
    }

    /**
     * 优惠券凑单页
     *
     * @return
     */
    @ApiOperation(value = "优惠券凑单页")
    @RequestMapping(value = "/coupon-goods", method = RequestMethod.POST)
    public BaseResponse<CouponGoodsPageResponse> listGoodsByCouponId(@RequestBody CouponGoodsPageRequest request, HttpServletRequest httpServletRequest) {

        CouponGoodsPageResponse couponGoodsPageResponse = new CouponGoodsPageResponse();
        EsGoodsInfoResponse esGoodsInfoResponse = new EsGoodsInfoResponse();
        // 列表排序默认按最新上架的SKU排序
        // 凑单页面的筛选条件排序按照 默认、最新、价格排序
        CouponGoodsListRequest requ = CouponGoodsListRequest.builder()
                .activityId(request.getActivity())
                .customerId(commonUtil.getOperatorId())
                .couponId(request.getCouponId()).build();

        DomainStoreRelaVO domainStoreRelaVO = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            requ.setStoreId(domainStoreRelaVO.getStoreId());
        }
        BaseResponse<CouponGoodsListResponse> baseResponsequeryResponse = couponCacheProvider.listGoodsByCouponId(requ);

        CouponGoodsListResponse queryResponse = baseResponsequeryResponse.getContext();

        couponGoodsPageResponse.setCouponInfo(queryResponse);
        if (CollectionUtils.isNotEmpty(queryResponse.getBrandIds()) && CollectionUtils.isEmpty(queryResponse.getQueryBrandIds())) {
            couponGoodsPageResponse.setEsGoodsInfoResponse(esGoodsInfoResponse);
            return BaseResponse.success(couponGoodsPageResponse);
        }

        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setWareId(commonUtil.getWareId(httpServletRequest));
        esGoodsInfoQueryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        esGoodsInfoQueryRequest.setStoreState(StoreState.OPENING.toValue());
        esGoodsInfoQueryRequest.setAddedFlag(AddedFlag.YES.toValue());
        esGoodsInfoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        esGoodsInfoQueryRequest.setSortFlag(request.getSortType() == null ? 0 : request.getSortType());
        esGoodsInfoQueryRequest.setPageNum(request.getPageNum());
        esGoodsInfoQueryRequest.setPageSize(request.getPageSize());
        esGoodsInfoQueryRequest.setCateAggFlag(true);
        esGoodsInfoQueryRequest.setStoreId(queryResponse.getStoreId());
        esGoodsInfoQueryRequest.setCompanyType(request.getCompanyType());
        String now = DateUtil.format(LocalDateTime.now(), DateUtil.FMT_TIME_4);
        esGoodsInfoQueryRequest.setContractStartDate(now);
        esGoodsInfoQueryRequest.setContractEndDate(now);

        esGoodsInfoQueryRequest.setSortFlag(request.getSortFlag());
        if (CollectionUtils.isNotEmpty(request.getCateIds())) {
            esGoodsInfoQueryRequest.setCateIds(request.getCateIds());
        }
        if (CollectionUtils.isNotEmpty(request.getBrandIds())) {
            esGoodsInfoQueryRequest.setBrandIds(request.getBrandIds());
        }

        switch (queryResponse.getScopeType()) {
            case ALL:
                break;
            case BOSS_CATE:
                if (CollectionUtils.isEmpty(esGoodsInfoQueryRequest.getCateIds()) && CollectionUtils.isNotEmpty(queryResponse.getCateIds())) {
                    esGoodsInfoQueryRequest.setCateIds(queryResponse.getCateIds4es());
                }
                break;
            case BRAND:
                if (CollectionUtils.isEmpty(esGoodsInfoQueryRequest.getBrandIds()) && CollectionUtils.isNotEmpty(queryResponse.getQueryBrandIds())) {
                    esGoodsInfoQueryRequest.setBrandIds(queryResponse.getQueryBrandIds());
                }
                break;
            case SKU:
                if (CollectionUtils.isNotEmpty(queryResponse.getGoodsInfoId())) {
                    esGoodsInfoQueryRequest.setGoodsInfoIds(queryResponse.getGoodsInfoId());
                }
                break;
            case STORE_CATE:
                if (CollectionUtils.isNotEmpty(queryResponse.getStoreCateIds())) {
                    esGoodsInfoQueryRequest.setStoreCateIds(new ArrayList(queryResponse.getStoreCateIds()));
                }
                break;
            default:
                break;
        }
        // 优惠券凑单页过滤分销商品
        if (!Objects.equals(ChannelType.PC_MALL, commonUtil.getDistributeChannel().getChannelType()) && DefaultFlag.YES.equals(distributionCacheService.queryOpenFlag())) {
            esGoodsInfoQueryRequest.setExcludeDistributionGoods(Boolean.TRUE);
        }

        esGoodsInfoResponse = esGoodsInfoElasticService.page(esGoodsInfoQueryRequest);
        List<EsGoodsInfo> goodsInfos = esGoodsInfoResponse.getEsGoodsInfoPage().getContent();

        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            //获取会员和等级
            CustomerVO customer = commonUtil.getCustomer();

            //通过客户收货地址和商品指定区域设置商品状态
            //根据用户ID得到收货地址
            CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)) {
                deliveryAddress = commonUtil.getDeliveryAddress();
            }
            CustomerDeliveryAddressResponse finalDeliveryAddress = deliveryAddress;

            //组装优惠券标签
            List<GoodsInfoVO> goodsInfoList = goodsInfos.stream().filter(e -> Objects.nonNull(e.getGoodsInfo()))
                    .map(e -> KsBeanUtil.convert(e.getGoodsInfo(), GoodsInfoVO.class))
                    .collect(Collectors.toList());

            //计算营销
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class));
            filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
            goodsInfoList = marketingPluginProvider.goodsListFilter(filterRequest).getContext().getGoodsInfoVOList();

            //计算区间价
            GoodsIntervalPriceByCustomerIdResponse priceResponse =
                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(goodsInfoList, customer.getCustomerId());
            esGoodsInfoResponse.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoList = priceResponse.getGoodsInfoVOList();

            //填充
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest purchaseFillBuyCountRequest = new PurchaseFillBuyCountRequest();
                purchaseFillBuyCountRequest.setCustomerId(customer.getCustomerId());
                purchaseFillBuyCountRequest.setGoodsInfoList(goodsInfoList);
                purchaseFillBuyCountRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
                //囤货
//                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = purchaseProvider.fillBuyCount(request)
//                        .getContext();
                //购物车
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount(purchaseFillBuyCountRequest)
                        .getContext();
                goodsInfoList = purchaseFillBuyCountResponse.getGoodsInfoList();
            }

            //重新赋值于Page内部对象
            Map<String, GoodsInfoVO> voMap = goodsInfoList.stream()
                    .collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g));
            esGoodsInfoResponse.getEsGoodsInfoPage().getContent().forEach(esGoodsInfo -> {
                GoodsInfoVO vo = voMap.get(esGoodsInfo.getGoodsInfo().getGoodsInfoId());
                if (Objects.nonNull(vo)) {
                    //活动限购库存
                    if (Objects.nonNull(vo.getMarketingLabels())) {
                        vo.getMarketingLabels().forEach(marketingLabelVO -> {
                            if (Objects.nonNull(marketingLabelVO.getGoodsPurchasingNumber())
                                    && marketingLabelVO.getGoodsPurchasingNumber() > 0
                                    && BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()).compareTo(vo.getStock()) < 0) {
                                vo.setStock(BigDecimal.valueOf(marketingLabelVO.getGoodsPurchasingNumber().longValue()));
                            }
                        });
                    }
                    //如果商品有营销活动 vip价格不参与 设置为0返给前端
                    if (CollectionUtils.isNotEmpty(vo.getMarketingLabels())) {
                        vo.setVipPrice(BigDecimal.ZERO);
                    }
                    //重新填充商品状态
                    if (Objects.equals(DeleteFlag.NO, vo.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, vo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), vo.getAddedFlag())) {
                        vo.setGoodsStatus(GoodsStatus.OK);
                        // 判断是否有T，如果是1，就设置为2
                        if (vo.getGoodsInfoName().endsWith("T") || vo.getGoodsInfoName().endsWith("t")) {
                            if (vo.getStock().compareTo(BigDecimal.ONE) < 0) {
                                vo.setGoodsStatus(GoodsStatus.INVALID);
                            }
                        } else {
                            if (vo.getStock().compareTo(BigDecimal.ONE) < 0) {
                                vo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                            }
                            if (Objects.nonNull(finalDeliveryAddress) && Objects.nonNull(vo.getAllowedPurchaseArea()) && StringUtils.isNotBlank(vo.getAllowedPurchaseArea())){
                                List<Long> allowedPurchaseAreaList = Arrays.asList(vo.getAllowedPurchaseArea().split(","))
                                                                           .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                                if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                                    vo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                                }
                            }
                        }
                    } else {
                        vo.setGoodsStatus(GoodsStatus.INVALID);
                    }

                    vo.setGoodsSubtitle(esGoodsInfo.getGoodsInfo().getGoodsSubtitle());
                    esGoodsInfo.setGoodsInfo(KsBeanUtil.convert(vo, GoodsInfoNest.class));
                }
            });

            couponGoodsPageResponse.setEsGoodsInfoResponse(esGoodsInfoResponse);
            return BaseResponse.success(couponGoodsPageResponse);
        } else {
            couponGoodsPageResponse.setEsGoodsInfoResponse(esGoodsInfoResponse);
            return BaseResponse.success(couponGoodsPageResponse);
        }
    }


    /**
     * 优惠券凑单页
     *
     * @return
     */
    @ApiOperation(value = "通过商家ids查询商户的有效优惠券")
    @RequestMapping(value = "/stores/coupon", method = RequestMethod.POST)
    public BaseResponse<CouponInfosQueryResponse> listCouponByStores(@RequestBody CouponInfoStoreIdsQueryRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        return couponInfoQueryProvider.queryCouponStoreidsInfos(request);
    }


}
