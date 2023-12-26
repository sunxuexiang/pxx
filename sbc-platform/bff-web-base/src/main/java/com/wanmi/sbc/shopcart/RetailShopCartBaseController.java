package com.wanmi.sbc.shopcart;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoFillGoodsStatusRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoStockByIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceRequest;
import com.wanmi.sbc.goods.api.request.price.ValidGoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseCalcAmountDTO;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: （大白鲸超市）购物车服务API
 * @author: XinJiang
 * @time: 2022/3/10 17:26
 */
@Api(tags = "RetailShopCartBaseController", description = "（大白鲸超市）购物车服务API")
@RestController
@RequestMapping("/retail/shop")
@Validated
public class RetailShopCartBaseController {

    @Autowired
    private RetailShopCartQueryProvider retailShopCartQueryProvider;

    @Autowired
    private RetailShopCartProvider retailShopCartProvider;

    @Autowired
    private RetailGoodsInfoProvider retailGoodsInfoProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @ApiOperation(value = "修改商品的选中状态")
    @RequestMapping(value = "/checkedRetailCartGoods", method = RequestMethod.POST)
    public BaseResponse  checkedRetailCartGoods(@RequestBody CheckedRetailCartRequest request) {
        //获取会员
        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(commonUtil.getOperatorId());
        request.setCustomerId(customerVO.getCustomerId());
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        if (!StringUtils.isNotBlank(request.getType())){
            throw new RuntimeException("必传字段未传");
        }
        retailShopCartQueryProvider.updateCheckStaues(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 未登录时,根据采购单缓存信息获取采购单详情
     *
     * @return 采购单
     * @author bail
     */
    @ApiOperation(value = "未登录时,根据采购单缓存信息获取采购单详情")
    @RequestMapping(value = "/front/shopCarts", method = RequestMethod.POST)
    public BaseResponse<PurchaseResponse> frontInfo(@RequestBody @Valid PurchaseFrontRequest request) {
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        // 根据前端传入的采购单信息,查询并组装其他必要信息
        PurchaseResponse response = retailShopCartQueryProvider.listFront(request).getContext();
        CustomerVO customer = null;

        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
            //设定SKU状态
            List<GoodsInfoVO> goodsInfoVOList = retailGoodsInfoProvider.fillGoodsStatus(
                    new GoodsInfoFillGoodsStatusRequest(KsBeanUtil.convert(response.getGoodsInfos(),
                            GoodsInfoDTO.class),request.getWareId(),request.getMatchWareHouseFlag())).getContext().getGoodsInfos();
            //根据开关重新设置分销商品标识
            distributionService.checkDistributionSwitch(goodsInfoVOList);

            //计算区间价
            GoodsIntervalPriceRequest goodsIntervalPriceRequest = new GoodsIntervalPriceRequest(
                    KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class));
            GoodsIntervalPriceResponse priceResponse = goodsIntervalPriceProvider.put(goodsIntervalPriceRequest)
                    .getContext();
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            response.setGoodsInfos(priceResponse.getGoodsInfoVOList());

            //社交分销业务
            Purchase4DistributionRequest purchase4DistributionRequest = new Purchase4DistributionRequest();
            purchase4DistributionRequest.setGoodsInfos(response.getGoodsInfos());
            purchase4DistributionRequest.setGoodsIntervalPrices(response.getGoodsIntervalPrices());
            purchase4DistributionRequest.setCustomer(customer);
            purchase4DistributionRequest.setDistributeChannel(commonUtil.getDistributeChannel());
            Purchase4DistributionResponse purchase4DistributionResponse =
                    retailShopCartQueryProvider.distribution(purchase4DistributionRequest).getContext();
            response.setGoodsInfos(purchase4DistributionResponse.getGoodsInfos());
            response.setGoodsIntervalPrices(purchase4DistributionResponse.getGoodsIntervalPrices());
            response.setSelfBuying(purchase4DistributionResponse.isSelfBuying());
            //排除分销商品
            List<GoodsInfoVO> goodsInfoComList = purchase4DistributionResponse.getGoodsInfoComList();

            // 采购单商品编号，只包含有效的商品
            List<GoodsInfoVO> goodsInfos = goodsInfoComList.stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());

            // 获取商品对应的营销信息
            // if (goodsInfos.size() > 0) {
            response.setGoodsMarketingMap(retailShopCartQueryProvider.getGoodsMarketing(new
                    PurchaseGetGoodsMarketingRequest(goodsInfos, customer, request.getWareId())).getContext().getMap());
            // }

            // 验证并设置 各商品使用的营销信息(满减/满折/满赠)
            ValidateAndSetGoodsMarketingsRequest validateAndSetGoodsMarketingsRequest =
                    new ValidateAndSetGoodsMarketingsRequest();
            validateAndSetGoodsMarketingsRequest.setGoodsMarketingDTOList(request.getGoodsMarketingDTOList());
            validateAndSetGoodsMarketingsRequest.setResponse(response);
            response =
                    retailShopCartQueryProvider.validateAndSetGoodsMarketings(validateAndSetGoodsMarketingsRequest).getContext();

            // 获取店铺对应的营销信息
            if (MapUtils.isNotEmpty(response.getGoodsMarketingMap())) {
                PurchaseGetStoreMarketingRequest purchaseGetStoreMarketingRequest =
                        new PurchaseGetStoreMarketingRequest();
                purchaseGetStoreMarketingRequest.setGoodsInfoIdList(request.getGoodsInfoIds());
                purchaseGetStoreMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerDTO.class));
                purchaseGetStoreMarketingRequest.setFrontReq(request);
                purchaseGetStoreMarketingRequest.setWareId(request.getWareId());
                if (response.getGoodsMarketings() != null) {
                    purchaseGetStoreMarketingRequest.setGoodsMarketings(
                            KsBeanUtil.convertList(response.getGoodsMarketings(), GoodsMarketingDTO.class));
                }
                response.setStoreMarketingMap(retailShopCartQueryProvider.getStoreMarketing(purchaseGetStoreMarketingRequest).getContext().getMap());
            } else {
                response.setStoreMarketingMap(new HashMap<>());
            }


            // 获取店铺下是否存在可以领取的优惠券，用于显示店铺旁的"优惠券"标识
            PurchaseGetStoreCouponExistRequest purchaseGetStoreCouponExistRequest =
                    new PurchaseGetStoreCouponExistRequest();
            purchaseGetStoreCouponExistRequest.setCustomer(customer);
            purchaseGetStoreCouponExistRequest.setGoodsInfoList(goodsInfoComList);
            response.setStoreCouponMap(
                    retailShopCartQueryProvider.getStoreCouponExist(purchaseGetStoreCouponExistRequest).getContext().getMap());

            //排除选中的商品id中无效的商品id
            List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
            request.setGoodsInfoIds(goodsInfoVOS.stream()
                    .filter(goodsInfo -> request.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                    .map(GoodsInfoVO::getGoodsInfoId)
                    .collect(Collectors.toList()));

            PurchaseCalcAmountRequest purchaseCalcAmountRequest = new PurchaseCalcAmountRequest();
            purchaseCalcAmountRequest.setPurchaseCalcAmount(KsBeanUtil.convert(response, PurchaseCalcAmountDTO.class));
            if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
                if (CollectionUtils.isEmpty(response.getGoodsInfos())){
                    purchaseCalcAmountRequest.setGoodsInfoIds(new ArrayList<>());
                }else {
                    List<String> checkGoodsInfoIds = response.getGoodsInfos().stream().filter(param -> DefaultFlag.YES.equals(param.getIsCheck()))
                            .map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                    purchaseCalcAmountRequest.setGoodsInfoIds(checkGoodsInfoIds);
                }
            } else {
                purchaseCalcAmountRequest.setGoodsInfoIds(request.getGoodsInfoIds());
            }
            purchaseCalcAmountRequest.setCustomerVO(null);
            response = KsBeanUtil.convert(retailShopCartProvider.calcAmount(
                    purchaseCalcAmountRequest).getContext(), PurchaseResponse.class);
            //根据goodsInfo中的stock顺序对goods重新排序
            if (CollectionUtils.isNotEmpty(response.getGoodsInfos())&&CollectionUtils.isNotEmpty(response.getGoodses())) {
                response.getGoodsInfos().sort(Comparator.comparing(GoodsInfoVO::getStock).reversed());
                List<GoodsVO> goodses = response.getGoodses();
                Map<String, GoodsVO> goodsVOMap = goodses.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
                List<GoodsVO> goodsSortList=new ArrayList<>(20);
                for (GoodsInfoVO inner:response.getGoodsInfos()){
                    goodsSortList.add(goodsVOMap.get(inner.getGoodsId()));
                }
                response.setGoodses(goodsSortList);
            }
            // 副标题
            Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
            response.getGoodsInfos().forEach(goodsInfoVO -> goodsInfoVO.setGoodsSubtitle(goodsVOMap.get(goodsInfoVO.getGoodsId()).getGoodsSubtitle()));
        }

        return BaseResponse.success(response);
    }

    @ApiOperation(value = "获取店铺下，是否有优惠券营销，展示优惠券标签")
    @RequestMapping(value = "/getStoreCouponExist", method = RequestMethod.GET)
    public BaseResponse<PurchaseGetStoreCouponExistResponse> getStoreCouponExist() {
        return retailShopCartQueryProvider.getStoreCouponExist(
                PurchaseGetStoreCouponExistRequest.builder().inviteeId(commonUtil.getPurchaseInviteeId()).customer(commonUtil.getCustomer()).build()
        );
    }

    /**
     * 获取采购单
     *
     * @return 采购单
     */
    @ApiOperation(value = "获取采购单")
    @RequestMapping(value = "/shopCarts", method = RequestMethod.POST)
    public BaseResponse<PurchaseListResponse> info(@RequestBody PurchaseListRequest request) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        PurchaseListRequest purchaseListRequest = new PurchaseListRequest();
        purchaseListRequest.setCustomerId(customer.getCustomerId());
        purchaseListRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        purchaseListRequest.setWareId(request.getWareId());
        purchaseListRequest.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            purchaseListRequest.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        Long currentTime = System.currentTimeMillis();
        PurchaseListResponse response = retailShopCartQueryProvider.list(purchaseListRequest).getContext();
        System.out.println("================================== PurchaseListResponse: " + (System.currentTimeMillis() - currentTime));

        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
            Long currentTime1 = System.currentTimeMillis();

            //设定SKU状态及计算区间价
            List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class);
            GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceProvider.putValidGoodsInfoByCustomerId(ValidGoodsIntervalPriceByCustomerIdRequest.builder()
                    .goodsInfoDTOList(goodsInfoDTOList)
                    .customerId(customer.getCustomerId())
                    .matchWareHouseFlag(request.getMatchWareHouseFlag())
                    .build()).getContext();
            if (Objects.nonNull(priceResponse.getGoodsIntervalPriceVOList())) {
                response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            } else {
                response.setGoodsIntervalPrices(Collections.emptyList());
            }
            response.setGoodsInfos(priceResponse.getGoodsInfoVOList());

            //排除分销商品
            List<GoodsInfoVO> goodsInfoComList = response.getGoodsInfos();

            // 采购单商品编号，只包含有效的商品
            List<GoodsInfoVO> goodsInfos = goodsInfoComList.stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
            List<GoodsInfoVO> requestGoods = KsBeanUtil.convert(goodsInfos, GoodsInfoVO.class);
            requestGoods=requestGoods.stream().filter(param->param.getGoodsInfoType()==0).collect(Collectors.toList());

            System.out.println("================================== 设定SKU状态: " + (System.currentTimeMillis() - currentTime1));
            Long currentTime2 = System.currentTimeMillis();

            // 获取采购营销信息及同步商品营销
            PurchaseMarketingRequest purchaseMarketingRequest = new PurchaseMarketingRequest();
//          计算营销使用有效商品计算
            purchaseMarketingRequest.setGoodsInfoIds(requestGoods.stream().map(rg->rg.getGoodsInfoId()).collect(Collectors.toList()));
            purchaseMarketingRequest.setGoodsInfos(requestGoods);
            purchaseMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
            purchaseMarketingRequest.setWareId(request.getWareId());
            PurchaseMarketingResponse purchaseMarketingResponse = retailShopCartProvider.getPurchasesMarketing(purchaseMarketingRequest).getContext();
            response.setGoodsMarketingMap(purchaseMarketingResponse.getMap());
            response.setSelfBuying(false);
            //设置大客户价
            if (null != customer.getEnterpriseStatusXyy() && customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED)) {
                response.getGoodsInfos().stream().forEach(r->r.setMarketPrice(
                        null != r.getVipPrice() && r.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? r.getVipPrice() : r.getMarketPrice()
                ));
            }
            System.out.println("================================== 获取商品对应的营销信息 时长: " + (System.currentTimeMillis() - currentTime2));
            goodsInfos = response.getGoodsInfos().stream().map(goodsInfo ->
                    purchaseMarketingResponse.getGoodsInfos().stream()
                            .filter(item -> item.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                            .findFirst().map(goodsInfoVO -> {
                        if (null != customer.getEnterpriseStatusXyy() && customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED)) {
                            GoodsInfoVO vo = new GoodsInfoVO();
                            KsBeanUtil.copyPropertiesThird(goodsInfoVO, vo);
                            vo.setMarketPrice(null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                            return vo;
                        } else {
                            return goodsInfoVO;
                        }
                    }).orElse(goodsInfo)).collect(Collectors.toList());
            //特价判断，设置特价
            goodsInfos.stream().forEach(g->{
                if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                    if(Objects.nonNull(g.getSpecialPrice())){
                        g.setMarketPrice(g.getSpecialPrice());
                    }
                }
            });

            response.setGoodsInfos(goodsInfos);

            response.setGoodsMarketings(purchaseMarketingResponse.getGoodsMarketings());

            // 获取店铺对应的营销信息
            if (CollectionUtils.isNotEmpty(response.getGoodsMarketings()) && CollectionUtils.isNotEmpty(request.getGoodsInfoIds())) {
                response.setStoreMarketingMap(purchaseMarketingResponse.getStoreMarketingMap());
                //过滤已选营销
                HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                response.getStoreMarketingMap().forEach((k, v) -> {
                    List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p->CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                    map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                });
                response.setStoreMarketingMap(map);
            } else {
                response.setStoreMarketingMap(new HashMap<>());
            }

            //获取店铺下是否存在可以领取的优惠券，用于显示店铺旁的"优惠券"
//            PurchaseGetStoreCouponExistRequest purchaseGetStoreCouponExistRequest =
//                    new PurchaseGetStoreCouponExistRequest();
//            purchaseGetStoreCouponExistRequest.setGoodsInfoList(goodsInfoComList);
//            purchaseGetStoreCouponExistRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
//            response.setStoreCouponMap(shopCartQueryProvider.getStoreCouponExist(purchaseGetStoreCouponExistRequest).getContext().getMap());

            //排除选中的商品id中无效的商品id
            List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
            request.setGoodsInfoIds(goodsInfoVOS.stream()
                    .filter(goodsInfo -> request.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                    .map(GoodsInfoVO::getGoodsInfoId)
                    .collect(Collectors.toList()));

            Long currentTime7 = System.currentTimeMillis();

            PurchaseCalcAmountRequest purchaseCalcAmountRequest = new PurchaseCalcAmountRequest();
            purchaseCalcAmountRequest.setPurchaseCalcAmount(KsBeanUtil.convert(response, PurchaseCalcAmountDTO.class));
            purchaseCalcAmountRequest.setGoodsInfoIds(goodsInfos.stream().map(rg->rg.getGoodsInfoId()).collect(Collectors.toList()));
            purchaseCalcAmountRequest.setCustomerVO(customer);
            response = retailShopCartProvider.calcAmount(purchaseCalcAmountRequest).getContext();

            System.out.println("================================== 过滤已选营销 时长: " + (System.currentTimeMillis() - currentTime7));
        }
        //根据goodsInfo中的stock顺序对goods重新排序
        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())&&CollectionUtils.isNotEmpty(response.getGoodses())) {
            response.getGoodsInfos().sort((a, b) -> {
                BigDecimal su = (a.getBuyCount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(a.getBuyCount())).subtract(a.getStock() == null ? BigDecimal.ZERO : a.getStock());
                BigDecimal su2 = (b.getBuyCount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(b.getBuyCount())).subtract (b.getStock() == null ? BigDecimal.ZERO : b.getStock());
                if (a.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    su = BigDecimal.ONE;
                }
                if (b.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    su2 = BigDecimal.ONE;
                }
                if (su.compareTo(BigDecimal.ZERO) <= 0 && su2.compareTo(BigDecimal.ZERO) <= 0) {
                    return 0;
                } else {
                    return su2.compareTo(su);
                }
            });
            List<GoodsVO> goodses = response.getGoodses();
            Map<String, GoodsVO> goodsVOMap = goodses.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
            List<GoodsVO> goodsSortList = new ArrayList<>(20);
            for (GoodsInfoVO inner : response.getGoodsInfos()) {
                goodsSortList.add(goodsVOMap.get(inner.getGoodsId()));
            }
            response.setGoodses(goodsSortList);
        }
        return BaseResponse.success(response);
    }

    /**
     * 未登录时,根据参数获取迷你采购单信息
     *
     * @return
     */
    @ApiOperation(value = "未登录时,根据参数获取迷你采购单信息")
    @RequestMapping(value = "/front/miniShopCarts", method = RequestMethod.POST)
    public BaseResponse<MiniPurchaseResponse> miniFrontInfo(@RequestBody @Valid PurchaseFrontMiniRequest request) {
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        MiniPurchaseResponse response = retailShopCartQueryProvider.miniListFront(request).getContext();
        List<GoodsInfoVO> goodsInfos = retailGoodsInfoProvider.fillGoodsStatus(GoodsInfoFillGoodsStatusRequest.builder()
                .goodsInfos(KsBeanUtil.convertList(response.getGoodsList(), GoodsInfoDTO.class)).build()).getContext().getGoodsInfos();
        response.getGoodsList().forEach(goodsInfo -> {
            goodsInfos.forEach(info -> {
                if (info.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())) {
                    goodsInfo.setGoodsStatus(info.getGoodsStatus());
                }
            });
        });

        return BaseResponse.success(response);
    }

    /**
     * 获取迷你采购单
     *
     * @return
     */
    @ApiOperation(value = "获取迷你采购单")
    @RequestMapping(value = "/miniShopCarts", method = RequestMethod.POST)
    public BaseResponse<PurchaseMiniListResponse> miniInfo(@RequestParam Long wareId) {
        CustomerVO customer = commonUtil.getCustomer();
        PurchaseMiniListRequest purchaseMiniListRequest = new PurchaseMiniListRequest();
        purchaseMiniListRequest.setCustomerId(customer.getCustomerId());

        purchaseMiniListRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        purchaseMiniListRequest.setCustomer(KsBeanUtil.convert(customer, CustomerDTO.class));
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            purchaseMiniListRequest.setSaasStatus(Boolean.TRUE);
            purchaseMiniListRequest.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //需要叠加访问端Pc\app不体现分销业务
        DefaultFlag openFlag = distributionCacheService.queryOpenFlag();
        if (Objects.equals(ChannelType.PC_MALL, commonUtil.getDistributeChannel().getChannelType()) || DefaultFlag.NO.equals(openFlag)) {
            purchaseMiniListRequest.setPcAndNoOpenFlag(Boolean.TRUE);
        }

        PurchaseMiniListResponse purchaseMiniListResponse =
                retailShopCartQueryProvider.minilist(purchaseMiniListRequest).getContext();
        List<GoodsInfoVO> goodsInfos = retailGoodsInfoProvider.fillGoodsStatus(GoodsInfoFillGoodsStatusRequest.builder()
                .goodsInfos(KsBeanUtil.convertList(purchaseMiniListResponse.getGoodsList(), GoodsInfoDTO.class))
                .wareId(wareId)
                .build()).getContext().getGoodsInfos();
        purchaseMiniListResponse.getGoodsList().forEach(goodsInfo -> {
            goodsInfos.forEach(info -> {
                if (info.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())) {
                    goodsInfo.setGoodsStatus(info.getGoodsStatus());
                    goodsInfo.setStock(info.getStock());
                }
                if (Objects.nonNull(info.getGoodsInfoType()) && info.getGoodsInfoType() == 1 && Objects.nonNull(info.getSpecialPrice())) {
                    goodsInfo.setMarketPrice(info.getSpecialPrice());
                    goodsInfo.setSalePrice(info.getSpecialPrice());
                }
            });
            if (EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())) {
                goodsInfo.setEnterpriseStatusXyy(customer.getEnterpriseStatusXyy().toValue());
                goodsInfo.setMarketPrice(Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfo.getVipPrice() : goodsInfo.getMarketPrice());
            }
        });
        return BaseResponse.success(purchaseMiniListResponse);
    }

    /**
     * 获取采购单商品数量
     *
     * @return
     */
    @ApiOperation(value = "获取采购单商品数量")
    @RequestMapping(value = "/countGoods", method = RequestMethod.GET)
    public BaseResponse<Integer> countGoods() {
        PurchaseCountGoodsRequest purchaseCountGoodsRequest = new PurchaseCountGoodsRequest();
        purchaseCountGoodsRequest.setCustomerId(commonUtil.getOperatorId());
        purchaseCountGoodsRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            purchaseCountGoodsRequest.setSaasStatus(Boolean.TRUE);
            purchaseCountGoodsRequest.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        PurchaseCountGoodsResponse purchaseCountGoodsResponse =
                retailShopCartQueryProvider.countGoods(purchaseCountGoodsRequest).getContext();
        return BaseResponse.success(purchaseCountGoodsResponse.getTotal());
    }

    /**
     * 新增采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "新增采购单")
    @RequestMapping(value = "/shopCart", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody @Valid PurchaseSaveRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isBlank(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.save(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新增采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "新增采购单")
    @RequestMapping(value = "/newShopCart", method = RequestMethod.POST)
    public BaseResponse newAdd(@RequestBody @Valid PurchaseSaveRequest request, HttpServletRequest request1) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isBlank(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(commonUtil.getWareId(request1));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.saveWithCache(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 调整数量
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "调整数量")
    @RequestMapping(value = "/newShopCart", method = RequestMethod.PUT)
    public BaseResponse newEdit(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isEmpty(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.updateNumWithCache(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "删除采购单")
    @RequestMapping(value = "/newShopCart", method = RequestMethod.DELETE)
    public BaseResponse newDelete(@RequestBody PurchaseDeleteRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isBlank(request.getCustomerId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.deleteWithCache(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量新增采购单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "批量新增采购单")
    @RequestMapping(value = "/batchAdd", method = RequestMethod.POST)
    public BaseResponse batchAdd(@RequestBody PurchaseBatchSaveRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || request.getGoodsInfos() == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.batchSave(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量新增采购单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "批量新增采购单")
    @RequestMapping(value = "/batchSaveAndCache", method = RequestMethod.POST)
    public BaseResponse batchSaveAndCache(@RequestBody PurchaseBatchSaveRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || request.getGoodsInfos() == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.batchSaveAndCache(request);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 调整数量
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "调整数量")
    @RequestMapping(value = "/shopCart", method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isEmpty(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.updateNum(request);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 调整数量
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "批量调整数量")
    @RequestMapping(value = "/shopCartList", method = RequestMethod.PUT)
    public BaseResponse editByList(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) ||CollectionUtils.isEmpty(request.getGoodsInfos())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.updateNumList(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "删除采购单")
    @RequestMapping(value = "/shopCart", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody PurchaseDeleteRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isBlank(request.getCustomerId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.delete(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 清除失效商品
     *
     * @return
     */
    @ApiOperation(value = "清除失效商品")
    @RequestMapping(value = "/clearLoseGoods", method = RequestMethod.DELETE)
    public BaseResponse clearLoseGoods() {
        PurchaseClearLoseGoodsRequest purchaseClearLoseGoodsRequest = new PurchaseClearLoseGoodsRequest();
        purchaseClearLoseGoodsRequest.setUserId(commonUtil.getOperatorId());

        DistributeChannel channel = commonUtil.getDistributeChannel();
        channel.setInviteeId(commonUtil.getPurchaseInviteeId());
        purchaseClearLoseGoodsRequest.setDistributeChannel(channel);
        retailShopCartProvider.clearLoseGoods(purchaseClearLoseGoodsRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 采购单商品移入收藏夹
     *
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "采购单商品移入收藏夹")
    @RequestMapping(value = "/addFollow", method = RequestMethod.PUT)
    public BaseResponse addFollow(@RequestBody PurchaseAddFollowRequest queryRequest) {
        queryRequest.setCustomerId(commonUtil.getOperatorId());

        queryRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.addFollow(queryRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 合并登录前后采购单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "合并登录前后采购单")
    @RequestMapping(value = "/mergeShopCart", method = RequestMethod.POST)
    public BaseResponse mergePurchase(@RequestBody @Valid PurchaseMergeRequest request) {
        request.setCustomer(KsBeanUtil.convert(commonUtil.getCustomer(), CustomerDTO.class));

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        retailShopCartProvider.mergePurchase(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 未登录时,计算采购单中参加同种营销的商品列表/总额/优惠
     *
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "未登录时,计算采购单中参加同种营销的商品列表/总额/优惠")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/{marketingId}/calcMarketingByMarketingIdFront", method = RequestMethod.POST)
    public BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingIdFront(
            @PathVariable Long marketingId, @RequestBody @Valid PurchaseFrontRequest queryRequest) {
        PurchaseCalcMarketingRequest request = new PurchaseCalcMarketingRequest();
        request.setMarketingId(marketingId);
        request.setFrontRequest(queryRequest);
        request.setGoodsInfoIds(queryRequest.getGoodsInfoIds());
        request.setIsPurchase(false);
        return retailShopCartProvider.calcMarketingByMarketingId(request);
    }

    /**
     * 计算采购单中参加同种营销的商品列表/总额/优惠
     *
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "计算采购单中参加同种营销的商品列表/总额/优惠")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/{marketingId}/calcMarketingByMarketingId", method = RequestMethod.GET)
    public BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@PathVariable Long marketingId, Long wareId) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        PurchaseCalcMarketingRequest purchaseCalcMarketingRequest = new PurchaseCalcMarketingRequest();
        purchaseCalcMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerDTO.class));
        purchaseCalcMarketingRequest.setGoodsInfoIds(null);
        purchaseCalcMarketingRequest.setIsPurchase(false);
        purchaseCalcMarketingRequest.setWareId(wareId);
        purchaseCalcMarketingRequest.setMarketingId(marketingId);
        return BaseResponse.success(retailShopCartProvider.calcMarketingByMarketingId(purchaseCalcMarketingRequest).getContext());
    }

    /**
     * 计算采购单中参加同种赠券活动的商品列表/总额/优惠
     * @param activityId
     * @param wareId
     * @return
     */
    @ApiOperation(value = "计算采购单中参加同种营销的商品列表/总额/优惠")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/{activityId}/calcCouponActivityByActivityId", method = RequestMethod.GET)
    public BaseResponse<PurchaseCalcMarketingResponse> calcMarketingByMarketingId(@PathVariable String activityId, Long wareId) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        PurchaseCalcMarketingRequest request = new PurchaseCalcMarketingRequest();
        request.setCustomer(KsBeanUtil.convert(customer, CustomerDTO.class));
        request.setActivityId(activityId);
        request.setIsPurchase(false);
        request.setWareId(wareId);
        return BaseResponse.success(retailShopCartProvider.calcCouponActivityByActivityId(request).getContext());
    }

    /**
     * 修改商品选择的营销
     *
     * @param goodsInfoId
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "修改商品选择的营销")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Map",
                    name = "goodsInfoId", value = "sku Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long",
                    name = "marketingId", value = "营销Id", required = true)
    })
    @RequestMapping(value = "/goodsMarketing/{goodsInfoId}/{marketingId}", method = RequestMethod.PUT)
    @LcnTransaction
    public BaseResponse modifyGoodsMarketing(@PathVariable String goodsInfoId, @PathVariable Long marketingId) {
        if (goodsInfoId == null || marketingId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        PurchaseModifyGoodsMarketingRequest purchaseModifyGoodsMarketingRequest =
                new PurchaseModifyGoodsMarketingRequest();
        purchaseModifyGoodsMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerDTO.class));
        purchaseModifyGoodsMarketingRequest.setGoodsInfoId(goodsInfoId);
        purchaseModifyGoodsMarketingRequest.setMarketingId(marketingId);
        retailShopCartProvider.modifyGoodsMarketing(purchaseModifyGoodsMarketingRequest);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 获取采购单所有商品使用的营销
     *
     * @return
     */
    @ApiOperation(value = "获取采购单所有商品使用的营销")
    @RequestMapping(value = "/goodsMarketing", method = RequestMethod.GET)
    public BaseResponse<List<GoodsMarketingVO>> queryGoodsMarketingList() {
        PurchaseQueryGoodsMarketingListRequest purchaseQueryGoodsMarketingListRequest =
                new PurchaseQueryGoodsMarketingListRequest();
        purchaseQueryGoodsMarketingListRequest.setCustomerId(commonUtil.getOperatorId());
        PurchaseQueryGoodsMarketingListResponse purchaseQueryGoodsMarketingListResponse =
                retailShopCartQueryProvider.queryGoodsMarketingList(purchaseQueryGoodsMarketingListRequest).getContext();
        return BaseResponse.success(purchaseQueryGoodsMarketingListResponse.getGoodsMarketingList());
    }


    /**
     * 获取视图采购单
     *
     * @return 采购单
     */
    @ApiOperation(value = "获取视图采购单")
    @RequestMapping(value = "/view/shopCarts", method = RequestMethod.POST)
    public BaseResponse<PurchaseListViewResponse> infoView(@RequestBody PurchaseListRequest request) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(request.getWareId());
        request.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        request.setCustomer(customer);
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        if(Objects.nonNull(deliveryAddress)){
            request.setProvinceId(deliveryAddress.getProvinceId());
            request.setCityId(deliveryAddress.getCityId());
        }
        PurchaseListResponse response = retailShopCartQueryProvider.purchaseInfo(request).getContext();
        if (CollectionUtils.isNotEmpty(response.getPurchaseGoodsInfos())) {
            response.getPurchaseGoodsInfos().forEach(i -> i.setIsSupermarketGoods(DefaultFlag.YES.toValue()));
        }
//        response.getGoodsInfoPage().forEach(i -> i.setIsSupermarketGoods(DefaultFlag.YES.toValue()));

        return BaseResponse.success(KsBeanUtil.convert(response, PurchaseListViewResponse.class));
    }


    /**
     * 获取视图采购单
     *
     * @return 采购单
     */
    @ApiOperation(value = "获取视图采购单")
    @RequestMapping(value = "/view/shopCartsAndCache", method = RequestMethod.POST)
    public BaseResponse<MarketingGroupCardResponse> infoViewAndCache(@RequestBody PurchaseListRequest request,HttpServletRequest request1) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(commonUtil.getWareId(request1));
        request.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        request.setCustomer(customer);
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        CustomerDeliveryAddressResponse deliveryAddress = commonUtil.getDeliveryAddress();
        if(Objects.nonNull(deliveryAddress)){
            request.setProvinceId(deliveryAddress.getProvinceId());
            request.setCityId(deliveryAddress.getCityId());
        }
        MarketingGroupCardResponse context = retailShopCartQueryProvider.purchaseInfoAndCache(request).getContext();
        return BaseResponse.success(context);
    }


    @ApiOperation(value = "校验限购数量")
    @RequestMapping(value = "/checkShopCartNum",method = RequestMethod.POST)
    public BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(@RequestBody CheckPurchaseNumRequest request){
        return retailShopCartQueryProvider.checkPurchaseNum(request);
    }

    /**
     * 获取视图采购单（新）
     *
     * @return 采购单
     */
    @ApiOperation(value = "（新）获取视图采购单")
    @RequestMapping(value = "/view/shopCartsNew", method = RequestMethod.POST)
    public BaseResponse<PurchaseListViewResponse> infoViewNew(@RequestBody PurchaseListNewRequest request) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(request.getWareId());
        request.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        // 采购单列表
        PurchaseListResponse response = retailShopCartQueryProvider.newPurchaseInfo(request).getContext();

        return BaseResponse.success(KsBeanUtil.convert(response, PurchaseListViewResponse.class));
    }

    /**
     * 查询选择商品店铺营销
     *
     * @return 查询选择商品店铺营销
     */
    @ApiOperation(value = "查询选择商品店铺营销")
    @RequestMapping(value = "/store/marketing", method = RequestMethod.POST)
    public BaseResponse<PurchaseStoreMarketingResponse> getStoreMarketings(@RequestBody PurchaseStoreMarketingRequest request) {
        //获取会员
        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(commonUtil.getOperatorId());
        request.setCustomer(customerVO);
        return BaseResponse.success(retailShopCartQueryProvider.getStoreMarketings(request).getContext());
    }

    /**
     * 查询满订单优惠
     *
     * @return 查询满订单优惠
     */
    @ApiOperation(value = "查询满订单优惠")
    @RequestMapping(value = "/orderMarketing", method = RequestMethod.POST)
    public BaseResponse<PurchaseOrderMarketingResponse> getOrderMarketings(@RequestBody PurchaseOrderMarketingRequest request) {
        return retailShopCartQueryProvider.getOrderMarketings(request);
    }

    /**
     * 查询采购车配置
     * @return
     */
    @ApiOperation(value = "查询采购车配置")
    @RequestMapping(value = "/queryProcurementConfig",method = RequestMethod.GET)
    public BaseResponse queryProcurementConfig(){
        BaseResponse<ProcurementConfigResponse> results = retailShopCartQueryProvider.queryProcurementConfig();
        if (Objects.nonNull(results.getContext())){
            return BaseResponse.success(results.getContext().getProcurementType().toProcurementType());
        }
        return BaseResponse.FAILED();
    }

    /**
     * 查询购物车总数量
     */
    @ApiOperation(value = "查询采购车配置")
    @RequestMapping(value = "/calShopCartAndPurchaseNum",method = RequestMethod.POST)
    public BaseResponse<PurchaseCountGoodsResponse> calShopCartAndPurchaseNum(){
        PurchaseCountGoodsRequest purchaseCountGoodsRequest = new PurchaseCountGoodsRequest();
        purchaseCountGoodsRequest.setCustomerId(commonUtil.getOperatorId());
        purchaseCountGoodsRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            purchaseCountGoodsRequest.setSaasStatus(Boolean.TRUE);
            purchaseCountGoodsRequest.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        PurchaseCountGoodsResponse purchaseCountGoodsResponse =
                retailShopCartQueryProvider.calShopCartAndPurchaseNum(purchaseCountGoodsRequest).getContext();
        return BaseResponse.success(purchaseCountGoodsResponse);
    }

    /**
     * 查询购物车是否包含套装商品
     */
    @ApiOperation(value = "查询购物车是否包含套装商品")
    @RequestMapping(value = "/querySuitGoodsByPurchase",method = RequestMethod.POST)
    public BaseResponse<PurchaseGetGoodsMarketingResponse> querySuitGoodsByPurchase(@RequestBody GoodsInfoStockByIdsRequest request){
        if( CollectionUtils.isEmpty(request.getGoodsInfo())){
            return BaseResponse.SUCCESSFUL();
        }

        List<String> goodsInfoIdList = request.getGoodsInfo().stream().map(g -> g.getSkuId()).collect(Collectors.toList());
        PurchaseQueryRequest purchaseQueryRequest = new PurchaseQueryRequest();
        purchaseQueryRequest.setGoodsInfoIds(goodsInfoIdList);
        purchaseQueryRequest.setCustomerId(commonUtil.getOperatorId());
        PurchaseGetGoodsMarketingResponse context = retailShopCartQueryProvider.querySuitGoodsByPurchase(purchaseQueryRequest).getContext();

        return BaseResponse.success(context);
    }


}
