package com.wanmi.sbc.shopcart;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.distribute.DistributionService;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoFillGoodsStatusRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoStockByIdsRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceRequest;
import com.wanmi.sbc.goods.api.request.price.ValidGoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingByGoodsInfoIdResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingGroupCard;
import com.wanmi.sbc.marketing.bean.vo.PriceInfoOfWholesale;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartNewPileProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartNewPileQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseCalcAmountDTO;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.shopcart.response.MarketingGroupPileResultResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 采购单Controller
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "ShopCartNewPileTradeBaseController", description = "（囤货）购物车服务API")
@RestController
@RequestMapping("/newpiletradeshop")
@Validated
@Slf4j
public class ShopCartNewPileTradeBaseController {

    @Autowired
    private ShopCartNewPileQueryProvider shopCartQueryProvider;

    @Autowired
    private ShopCartNewPileProvider shopCartNewPileProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsIntervalPriceProvider goodsIntervalPriceProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

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
        PurchaseResponse response = shopCartQueryProvider.listFront(request).getContext();
        CustomerVO customer = null;
        //List<String> skuIdList = request.getGoodsInfoIds();

        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
            //设定SKU状态
            List<GoodsInfoVO> goodsInfoVOList = goodsInfoProvider.fillGoodsStatus(
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
                    shopCartQueryProvider.distribution(purchase4DistributionRequest).getContext();
            response.setGoodsInfos(purchase4DistributionResponse.getGoodsInfos());
            response.setGoodsIntervalPrices(purchase4DistributionResponse.getGoodsIntervalPrices());
            response.setSelfBuying(purchase4DistributionResponse.isSelfBuying());
            //排除分销商品
            List<GoodsInfoVO> goodsInfoComList = purchase4DistributionResponse.getGoodsInfoComList();
            /*if (commonUtil.findVASBuyOrNot(VASConstants.VAS_IEP_SETTING)) {
                //企业购分支，设置企业会员价
                GoodsPriceSetBatchByIepRequest iepRequest = GoodsPriceSetBatchByIepRequest.builder()
                        .customer(customer)
                        .goodsInfos(response.getGoodsInfos())
                        .goodsIntervalPrices(response.getGoodsIntervalPrices())
                        .build();
                GoodsPriceSetBatchByIepResponse iepResponse = goodsPriceAssistProvider.goodsPriceSetBatchByIep(iepRequest).getContext();
                response.setGoodsInfos(iepResponse.getGoodsInfos());
                response.setGoodsIntervalPrices(iepResponse.getGoodsIntervalPrices());
            }*/

            // 采购单商品编号，只包含有效的商品
            List<GoodsInfoVO> goodsInfos = goodsInfoComList.stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());

            // 获取商品对应的营销信息
            // if (goodsInfos.size() > 0) {
            response.setGoodsMarketingMap(shopCartQueryProvider.getGoodsMarketing(new
                    PurchaseGetGoodsMarketingRequest(goodsInfos, customer, request.getWareId())).getContext().getMap());
            // }

            // 验证并设置 各商品使用的营销信息(满减/满折/满赠)
            ValidateAndSetGoodsMarketingsRequest validateAndSetGoodsMarketingsRequest =
                    new ValidateAndSetGoodsMarketingsRequest();
            validateAndSetGoodsMarketingsRequest.setGoodsMarketingDTOList(request.getGoodsMarketingDTOList());
            validateAndSetGoodsMarketingsRequest.setResponse(response);
            response =
                    shopCartQueryProvider.validateAndSetGoodsMarketings(validateAndSetGoodsMarketingsRequest).getContext();

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
                response.setStoreMarketingMap(shopCartQueryProvider.getStoreMarketing(purchaseGetStoreMarketingRequest).getContext().getMap());
            } else {
                response.setStoreMarketingMap(new HashMap<>());
            }


            // 获取店铺下是否存在可以领取的优惠券，用于显示店铺旁的"优惠券"标识
            PurchaseGetStoreCouponExistRequest purchaseGetStoreCouponExistRequest =
                    new PurchaseGetStoreCouponExistRequest();
            purchaseGetStoreCouponExistRequest.setCustomer(customer);
            purchaseGetStoreCouponExistRequest.setGoodsInfoList(goodsInfoComList);
            response.setStoreCouponMap(
                    shopCartQueryProvider.getStoreCouponExist(purchaseGetStoreCouponExistRequest).getContext().getMap());

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
            response = KsBeanUtil.convert(shopCartNewPileProvider.calcAmount(
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
        return shopCartQueryProvider.getStoreCouponExist(
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
        PurchaseListResponse response = shopCartQueryProvider.list(purchaseListRequest).getContext();
        System.out.println("================================== PurchaseListResponse: " + (System.currentTimeMillis() - currentTime));

        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
            Long currentTime1 = System.currentTimeMillis();
            //设定SKU状态
//            List<GoodsInfoVO> goodsInfoVOList = goodsInfoProvider.fillGoodsStatus(
//                    GoodsInfoFillGoodsStatusRequest.builder()
//                            .goodsInfos(KsBeanUtil.convertList(response.getGoodsInfos(), GoodsInfoDTO.class))
//                            .matchWareHouseFlag(request.getMatchWareHouseFlag()).build()
//            ).getContext().getGoodsInfos();
//            System.out.println("================================== 设定SKU状态: " + (System.currentTimeMillis() - currentTime1));

            //根据开关重新设置分销商品标识
//            distributionService.checkDistributionSwitch(goodsInfoVOList);

            //设定SKU状态及计算区间价
            List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class);
//            GoodsIntervalPriceByCustomerIdResponse priceResponse =
//                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(goodsInfoVOList, customer.getCustomerId());
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


            //社交分销业务
//            Purchase4DistributionRequest purchase4DistributionRequest = new Purchase4DistributionRequest();
//            purchase4DistributionRequest.setGoodsInfos(response.getGoodsInfos());
//            purchase4DistributionRequest.setGoodsIntervalPrices(response.getGoodsIntervalPrices());
//            purchase4DistributionRequest.setCustomer(customer);
//            purchase4DistributionRequest.setDistributeChannel(commonUtil.getDistributeChannel());
//            Purchase4DistributionResponse purchase4DistributionResponse =
//                    shopCartQueryProvider.distribution(purchase4DistributionRequest).getContext();
//            response.setGoodsInfos(purchase4DistributionResponse.getGoodsInfos());
//            response.setGoodsIntervalPrices(purchase4DistributionResponse.getGoodsIntervalPrices());

            //排除分销商品
            List<GoodsInfoVO> goodsInfoComList = response.getGoodsInfos();

            // 采购单商品编号，只包含有效的商品
            List<GoodsInfoVO> goodsInfos = goodsInfoComList.stream()
                    .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
            List<GoodsInfoVO> requestGoods = KsBeanUtil.convert(goodsInfos, GoodsInfoVO.class);
            requestGoods=requestGoods.stream().filter(param->param.getGoodsInfoType()==0).collect(Collectors.toList());

            System.out.println("================================== 设定SKU状态: " + (System.currentTimeMillis() - currentTime1));
            Long currentTime2 = System.currentTimeMillis();
            // 获取商品对应的营销信息
//            PurchaseGetGoodsMarketingRequest purchaseGetGoodsMarketingRequest = new PurchaseGetGoodsMarketingRequest();
//            purchaseGetGoodsMarketingRequest.setGoodsInfos(requestGoods);
//            purchaseGetGoodsMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
//            purchaseGetGoodsMarketingRequest.setWareId(request.getWareId());
//            PurchaseGetGoodsMarketingResponse marketingResponse =
//                    shopCartQueryProvider.getPurchasesMarketing(purchaseGetGoodsMarketingRequest).getContext();
            // 获取采购营销信息及同步商品营销
            PurchaseMarketingRequest purchaseMarketingRequest = new PurchaseMarketingRequest();
//            purchaseMarketingRequest.setGoodsInfoIds(request.getGoodsInfoIds());
//          计算营销使用有效商品计算
            purchaseMarketingRequest.setGoodsInfoIds(requestGoods.stream().map(rg->rg.getGoodsInfoId()).collect(Collectors.toList()));
            purchaseMarketingRequest.setGoodsInfos(requestGoods);
            purchaseMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
            purchaseMarketingRequest.setWareId(request.getWareId());
            PurchaseMarketingResponse purchaseMarketingResponse = shopCartNewPileProvider.getPurchasesMarketing(purchaseMarketingRequest).getContext();
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

            //企业购分支，设置企业会员价
//            if (commonUtil.findVASBuyOrNot(VASConstants.VAS_IEP_SETTING)) {
//                GoodsPriceSetBatchByIepRequest iepRequest = GoodsPriceSetBatchByIepRequest.builder()
//                        .customer(customer)
//                        .goodsInfos(response.getGoodsInfos())
//                        .filteredGoodsInfoIds(goodsInfoComList.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()))
//                        .goodsIntervalPrices(response.getGoodsIntervalPrices())
//                        .build();
//                GoodsPriceSetBatchByIepResponse iepResponse = goodsPriceAssistProvider.goodsPriceSetBatchByIep(iepRequest).getContext();
//                response.setGoodsInfos(iepResponse.getGoodsInfos());
//                response.setGoodsIntervalPrices(iepResponse.getGoodsIntervalPrices());
//            }

//            Long currentTime3 = System.currentTimeMillis();
//            PurchaseSyncGoodsMarketingsRequest purchaseSyncGoodsMarketingsRequest =
//                    new PurchaseSyncGoodsMarketingsRequest();
//            purchaseSyncGoodsMarketingsRequest.setGoodsMarketingMap(response.getGoodsMarketingMap());
//            purchaseSyncGoodsMarketingsRequest.setCustomerId(customer.getCustomerId());
//            shopCartNewPileProvider.syncGoodsMarketings(purchaseSyncGoodsMarketingsRequest);
//
//            System.out.println("================================== syncGoodsMarketings 时长: " + (System.currentTimeMillis() - currentTime3));
//            Long currentTime4 = System.currentTimeMillis();
//            PurchaseQueryGoodsMarketingListRequest purchaseQueryGoodsMarketingListRequest =
//                    new PurchaseQueryGoodsMarketingListRequest();
//            purchaseQueryGoodsMarketingListRequest.setCustomerId(customer.getCustomerId());
//            response.setGoodsMarketings(shopCartQueryProvider.queryGoodsMarketingList(purchaseQueryGoodsMarketingListRequest).getContext().getGoodsMarketingList());
            response.setGoodsMarketings(purchaseMarketingResponse.getGoodsMarketings());

//            System.out.println("================================== syncGoodsMarketings 时长: " + (System.currentTimeMillis() - currentTime4));

            // 获取店铺对应的营销信息
            if (CollectionUtils.isNotEmpty(response.getGoodsMarketings()) && CollectionUtils.isNotEmpty(request.getGoodsInfoIds())) {
//                PurchaseGetStoreMarketingRequest purchaseGetStoreMarketingRequest =
//                        new PurchaseGetStoreMarketingRequest();
//                List<String> reqGoodsInfos =new ArrayList<>();
//                Long currentTime5 = System.currentTimeMillis();
//                if (CollectionUtils.isNotEmpty(request.getGoodsInfoIds())){
//                    List<GoodsInfoVO> reqGoodsInfo = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder()
//                            .goodsInfoIds(request.getGoodsInfoIds()).wareId(request.getWareId()).build()).getContext().getGoodsInfos();
//                    reqGoodsInfos=reqGoodsInfo.stream().filter(param->param.getGoodsInfoType()==0).map(GoodsInfoVO::getGoodsInfoId)
//                            .collect(Collectors.toList());
//                }
//                System.out.println("================================== listByIds 时长: " + (System.currentTimeMillis() - currentTime5));
//                Long currentTime6 = System.currentTimeMillis();
//                purchaseGetStoreMarketingRequest.setGoodsInfoIdList(CollectionUtils.isNotEmpty(request.getGoodsInfoIds())?reqGoodsInfos
//                        :request.getGoodsInfoIds());
//                purchaseGetStoreMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerDTO.class));
//                purchaseGetStoreMarketingRequest.setGoodsMarketings(KsBeanUtil.convertList(response.getGoodsMarketings(), GoodsMarketingDTO.class));
//                purchaseGetStoreMarketingRequest.setWareId(request.getWareId());
//                response.setStoreMarketingMap(shopCartQueryProvider.getStoreMarketing(purchaseGetStoreMarketingRequest).getContext().getMap());
                response.setStoreMarketingMap(purchaseMarketingResponse.getStoreMarketingMap());
                //过滤已选营销
                HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                response.getStoreMarketingMap().forEach((k, v) -> {
                    List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p->CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                    map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                });
//                System.out.println("================================== 过滤已选营销 时长: " + (System.currentTimeMillis() - currentTime6));
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
//            purchaseCalcAmountRequest.setGoodsInfoIds(request.getGoodsInfoIds());
            purchaseCalcAmountRequest.setGoodsInfoIds(goodsInfos.stream().map(rg->rg.getGoodsInfoId()).collect(Collectors.toList()));
            purchaseCalcAmountRequest.setCustomerVO(customer);
            response = shopCartNewPileProvider.calcAmount(purchaseCalcAmountRequest).getContext();

            System.out.println("================================== 过滤已选营销 时长: " + (System.currentTimeMillis() - currentTime7));
            //TODO 脱敏
//            if (CollectionUtils.isNotEmpty(response.getCompanyInfos())) {
//                response.getCompanyInfos().forEach(
//                        companyInfoVO -> {
//                            if (CollectionUtils.isNotEmpty(companyInfoVO.getEmployeeVOList())) {
//                                companyInfoVO.getEmployeeVOList().forEach(
//                                        employeeVO -> {
//                                            employeeVO.setAccountPassword(null);
//                                            employeeVO.setEmployeeSaltVal(null);
//                                            employeeVO.setEmployeeName(null);
//                                            employeeVO.setEmployeeMobile(null);
//                                            employeeVO.setLoginTime(null);
//
//                                        }
//                                );
//                            }
//                            if (CollectionUtils.isNotEmpty(companyInfoVO.getStoreVOList())) {
//                                companyInfoVO.getStoreVOList().forEach(
//                                        storeVO -> {
//                                            storeVO.setContactEmail(null);
//                                            storeVO.setAddressDetail(null);
//                                            storeVO.setContactMobile(null);
//                                        }
//                                );
//                            }
//                        });
//            }
//            if (CollectionUtils.isNotEmpty(response.getStores())) {
//                response.getStores().forEach(storeVO -> {
//                    storeVO.setContactEmail(null);
//                    storeVO.setAddressDetail(null);
//                    storeVO.setContactMobile(null);
//                });
//            }
        }
        //根据goodsInfo中的stock顺序对goods重新排序
        if (CollectionUtils.isNotEmpty(response.getGoodsInfos())&&CollectionUtils.isNotEmpty(response.getGoodses())) {
            response.getGoodsInfos().sort((a, b) -> {
                BigDecimal su = (a.getBuyCount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(a.getBuyCount())).subtract (a.getStock() == null ? BigDecimal.ZERO : a.getStock());
                BigDecimal su2 = (b.getBuyCount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(b.getBuyCount())) .subtract (b.getStock() == null ? BigDecimal.ZERO : b.getStock());
                if (a.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    su = BigDecimal.ZERO;
                }
                if (b.getStock().compareTo(BigDecimal.ZERO) <= 0) {
                    su2 = BigDecimal.ZERO;
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
        MiniPurchaseResponse response = shopCartQueryProvider.miniListFront(request).getContext();
        List<GoodsInfoVO> goodsInfos = goodsInfoProvider.fillGoodsStatus(GoodsInfoFillGoodsStatusRequest.builder()
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
                shopCartQueryProvider.minilist(purchaseMiniListRequest).getContext();
        List<GoodsInfoVO> goodsInfos = goodsInfoProvider.fillGoodsStatus(GoodsInfoFillGoodsStatusRequest.builder()
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
                shopCartQueryProvider.countGoods(purchaseCountGoodsRequest).getContext();
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
        if (Objects.nonNull(request)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"请下载最新app添加购物车，或联系客户服务人员");
        }
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isBlank(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        if(Objects.isNull(request.getWareId()) || request.getWareId().longValue() == 0){
            request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.save(request);
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
        if (Objects.nonNull(request)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"请下载最新app添加购物车，或联系客户服务人员");
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || request.getGoodsInfos() == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.batchSave(request);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 批量新增采购单(拆箱之后的)
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "批量新增采购单")
    @RequestMapping(value = "/devanning/batchAdd", method = RequestMethod.POST)
    public BaseResponse devanningBatchAdd(@RequestBody PurchaseBatchSaveRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || request.getGoodsInfos() == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.batchSaveDevanning(request);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 批量新增采购单(拆箱之后的)
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "批量新增采购单")
    @RequestMapping(value = "/devanning/newbatchAdd", method = RequestMethod.POST)
    public BaseResponse devanningNewBatchAdd(@RequestBody PurchaseBatchSaveRequest request,HttpServletRequest httpRequest) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || request.getGoodsInfos() == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        request.setCityId(finalDeliveryAddress.getCityId());
        request.setProvinceId(finalDeliveryAddress.getProvinceId());
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.batchSaveNewDevanning(request);
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
        shopCartNewPileProvider.updateNum(request);
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * 调整数量
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "调整数量")
    @RequestMapping(value = "/devanning/shopCart", method = RequestMethod.PUT)
    public BaseResponse devanningEdit(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isEmpty(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        if (Objects.isNull(request.getDevanningId())){
            List<String>  list =new LinkedList<>();
            list.add(request.getGoodsInfoId());
            DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().
                            goodsInfoIds(list).wareId(commonUtil.getWareId(HttpUtil.getRequest()))
                            .build()).getContext().getDevanningGoodsInfoVOS().stream()
                    .filter(v -> {
                        if (v.getDivisorFlag().compareTo(BigDecimal.ONE) == 0) {
                            return true;
                        }
                        return false;
                    }).findFirst().orElse(null);
            if (Objects.isNull(devanningGoodsInfoVO)){
                throw new SbcRuntimeException("未查询到数据请联系管理员");
            }
            request.setDevanningId(devanningGoodsInfoVO.getDevanningId());
        }

        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.updateDevanningNum(request);
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 调整数量
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "调整数量")
    @RequestMapping(value = "/devanning/newshopCart", method = RequestMethod.PUT)
    public BaseResponse devanningnewEdit(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isEmpty(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        if (Objects.isNull(request.getDevanningId())){
            List<String>  list =new LinkedList<>();
            list.add(request.getGoodsInfoId());
            DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().
                            goodsInfoIds(list).wareId(commonUtil.getWareId(HttpUtil.getRequest()))
                            .build()).getContext().getDevanningGoodsInfoVOS().stream()
                    .filter(v -> {
                        if (v.getDivisorFlag().compareTo(BigDecimal.ONE) == 0) {
                            return true;
                        }
                        return false;
                    }).findFirst().orElse(null);
            if (Objects.isNull(devanningGoodsInfoVO)){
                throw new SbcRuntimeException("未查询到数据请联系管理员");
            }
            request.setDevanningId(devanningGoodsInfoVO.getDevanningId());
        }

        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setProvinceId(commonUtil.getProvinceId(HttpUtil.getRequest()));
        request.setCityId(commonUtil.getCityId(HttpUtil.getRequest()));
        shopCartNewPileProvider.updateDevanningNumNew(request);
        return BaseResponse.SUCCESSFUL();
    }





    /**
     * 调整数量
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "调整数量")
    @RequestMapping(value = "/devanning/newshopCartAndShopCart", method = RequestMethod.PUT)
    public BaseResponse<MarketingGroupCardResponse> newshopCartAndShopCart(@RequestBody @Valid PurchaseUpdateNumRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isEmpty(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        if (Objects.isNull(request.getDevanningId())){
            List<String>  list =new LinkedList<>();
            list.add(request.getGoodsInfoId());
            DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().
                            goodsInfoIds(list).wareId(commonUtil.getWareId(HttpUtil.getRequest()))
                            .build()).getContext().getDevanningGoodsInfoVOS().stream()
                    .filter(v -> {
                        if (v.getDivisorFlag().compareTo(BigDecimal.ONE) == 0) {
                            return true;
                        }
                        return false;
                    }).findFirst().orElse(null);
            if (Objects.isNull(devanningGoodsInfoVO)){
                throw new SbcRuntimeException("未查询到数据请联系管理员");
            }
            request.setDevanningId(devanningGoodsInfoVO.getDevanningId());
        }

        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setProvinceId(commonUtil.getProvinceId(HttpUtil.getRequest()));
        request.setCityId(commonUtil.getCityId(HttpUtil.getRequest()));
        shopCartNewPileProvider.updateDevanningNumNew(request);

//        {"goodsInfoIds":[],"isRefresh":true,"matchWareHouseFlag":true,"pageNum":0,"pageSize":200,"wareId":0}
        PurchaseListRequest purchaseListRequest = new PurchaseListRequest();
        purchaseListRequest.setGoodsInfoIds(new ArrayList<>());
        purchaseListRequest.setIsRefresh(true);
        purchaseListRequest.setMatchWareHouseFlag(true);
        purchaseListRequest.setPageNum(0);
        purchaseListRequest.setPageSize(200);
        purchaseListRequest.setWareId(0L);


        return  this.devanningInfoViewAndCache(purchaseListRequest,HttpUtil.getRequest());



    }

























    /**
     * 删除采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "删除采购单")
    @RequestMapping(value = "/devanning/shopCart", method = RequestMethod.DELETE)
    public BaseResponse devanningDelete(@RequestBody PurchaseDeleteRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isBlank(request.getCustomerId()) || CollectionUtils.isEmpty(request.getDevanningIds())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.devanningDelete(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除采购单 缓存删除
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "删除采购单")
    @RequestMapping(value = "/devanning/newshopCart", method = RequestMethod.DELETE)
    public BaseResponse devanningDeletenew(@RequestBody PurchaseDeleteRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isBlank(request.getCustomerId()) || CollectionUtils.isEmpty(request.getDevanningIds())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        shopCartNewPileProvider.devanningDeleteCache(request);
        return BaseResponse.SUCCESSFUL();
    }




    /**
     * 新增采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "新增采购单")
    @RequestMapping(value = "/devanning/shopCart", method = RequestMethod.POST)
    public BaseResponse devanningAdd(@RequestBody @Valid PurchaseSaveRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isBlank(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        List<String>  list =new LinkedList<>();
        list.add(request.getGoodsInfoId());
        DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(list)
                .wareId(commonUtil.getWareId(HttpUtil.getRequest())).build()).getContext().getDevanningGoodsInfoVOS().stream()
                .filter(v -> {
                    if (v.getDivisorFlag().compareTo(BigDecimal.ONE) == 0) {
                        return true;
                    }
                    return false;
                }).findFirst().orElse(null);
        if (Objects.isNull(devanningGoodsInfoVO)){
            throw new SbcRuntimeException("未查询到数据请联系管理员");
        }
        request.setDevanningId(devanningGoodsInfoVO.getDevanningId());
        shopCartNewPileProvider.save(request);
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * 新增采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "新增采购单")
    @RequestMapping(value = "/devanning/newshopCart", method = RequestMethod.POST)
    public BaseResponse newdevanningAdd(@RequestBody @Valid PurchaseSaveRequest request) {
        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        request.setCustomerId(commonUtil.getOperatorId());
        if (StringUtils.isBlank(request.getCustomerId()) || StringUtils.isBlank(request.getGoodsInfoId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }
        request.setWareId(wareId);
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        List<String>  list =new LinkedList<>();
        list.add(request.getGoodsInfoId());
        DevanningGoodsInfoVO devanningGoodsInfoVO = devanningGoodsInfoProvider.getQueryList(DevanningGoodsInfoPageRequest.builder().goodsInfoIds(list)
                        .wareId(commonUtil.getWareId(HttpUtil.getRequest())).build()).getContext().getDevanningGoodsInfoVOS().stream()
                .filter(v -> {
                    if (v.getDivisorFlag().compareTo(BigDecimal.ONE) == 0) {
                        return true;
                    }
                    return false;
                }).findFirst().orElse(null);
        if (Objects.isNull(devanningGoodsInfoVO)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前定位区域不支持销售该商品");
        }
        Long devanningGoodsInfoVOWareId = Optional.ofNullable(devanningGoodsInfoVO.getWareId()).orElse(null);
        if(Objects.nonNull(devanningGoodsInfoVOWareId) && !Objects.equals(wareId, devanningGoodsInfoVOWareId)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前定位区域不支持销售该商品");
        }
        request.setDevanningId(devanningGoodsInfoVO.getDevanningId());
        request.setCityId(commonUtil.getCityId(HttpUtil.getRequest()));
        request.setProvinceId(commonUtil.getProvinceId(HttpUtil.getRequest()));
        shopCartNewPileProvider.newsave(request);
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
        shopCartNewPileProvider.updateNumList(request);
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
        shopCartNewPileProvider.delete(request);
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
        shopCartNewPileProvider.clearLoseGoods(purchaseClearLoseGoodsRequest);
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
        shopCartNewPileProvider.addFollow(queryRequest);
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
        shopCartNewPileProvider.mergePurchase(request);
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
        return shopCartNewPileProvider.calcMarketingByMarketingId(request);
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
        purchaseCalcMarketingRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        purchaseCalcMarketingRequest.setMarketingId(marketingId);
        return BaseResponse.success(shopCartNewPileProvider.calcMarketingByMarketingId(purchaseCalcMarketingRequest).getContext());
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
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        return BaseResponse.success(shopCartNewPileProvider.calcCouponActivityByActivityId(request).getContext());
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
        shopCartNewPileProvider.modifyGoodsMarketing(purchaseModifyGoodsMarketingRequest);
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
                shopCartQueryProvider.queryGoodsMarketingList(purchaseQueryGoodsMarketingListRequest).getContext();
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
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
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
        PurchaseListResponse response = shopCartQueryProvider.purchaseInfo(request).getContext();

        PurchaseListViewResponse purchaseListViewResponse=KsBeanUtil.convert(response, PurchaseListViewResponse.class);
        purchaseListViewResponse.setMarketingDiscountDetails(response.getMarketingDiscountDetails());
        return BaseResponse.success(purchaseListViewResponse);

    }


    /**
     * 获取视图采购单
     *
     * @return 采购单
     */
    @ApiOperation(value = "获取视图采购单")
    @RequestMapping(value = "/devanning/view/shopCarts", method = RequestMethod.POST)
    public BaseResponse<PurchaseListViewResponse> devanningInfoView(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        request.setCustomer(customer);
        request.setLocationCityId(commonUtil.getCityId(httpRequest));
        request.setLocationProvinceId(commonUtil.getProvinceId(httpRequest));
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        log.info("============devanningInfoView：" + JSONObject.toJSONString(request));
        PurchaseListResponse response = shopCartQueryProvider.devanningPurchaseInfo(request).getContext();

        PurchaseListViewResponse purchaseListViewResponse=KsBeanUtil.convert(response, PurchaseListViewResponse.class);
        purchaseListViewResponse.setMarketingDiscountDetails(response.getMarketingDiscountDetails());
        //限购
        CustomerDeliveryAddressResponse deliveryAddress=commonUtil.getDeliveryAddress();
        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        if(Objects.nonNull(purchaseListViewResponse.getDevanningGoodsInfoPage())) {
            purchaseListViewResponse.getDevanningGoodsInfoPage().getContent().forEach(goodsInfoVO -> {
                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())) {
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                    }
                    if(Objects.nonNull(deliveryAddress)){
                        if (!allowedPurchaseAreaList.contains(deliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(deliveryAddress.getProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }

                }
            });
        }
        return BaseResponse.success(purchaseListViewResponse);

    }




    /**
     * 获取视图采购单
     *
     * @return 采购单
     */
    @Deprecated
    @ApiOperation(value = "获取视图采购单缓存级计算最优惠组合")
    @RequestMapping(value = "/devanning/view/shopCartsAndCache", method = RequestMethod.POST)
    public BaseResponse<MarketingGroupCardResponse> devanningInfoViewAndCache(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        request.setCustomer(customer);
        request.setLocationCityId(commonUtil.getCityId(httpRequest));
        request.setLocationProvinceId(commonUtil.getProvinceId(httpRequest));
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        log.info("============devanningInfoView：" + JSONObject.toJSONString(request));
        MarketingGroupCardResponse response = shopCartQueryProvider.devanningPurchaseInfoCache(request).getContext();
        return BaseResponse.success(response);
    }


    @ApiOperation(value = "通过商品id集合获取商品对应的营销")
    @RequestMapping(value = "/getMarketingByGoodsInfoids",method = RequestMethod.POST)
    public BaseResponse<MarketingByGoodsInfoIdResponse> getMarketingByGoodsInfoids(@RequestBody MarketingByGoodsIdRequest request){

        return marketingQueryProvider.getMarketingByGoodsInfo(request);
    }


    @ApiOperation(value = "校验限购数量")
    @RequestMapping(value = "/checkShopCartNum",method = RequestMethod.POST)
    public BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(@RequestBody CheckPurchaseNumRequest request){
        return shopCartQueryProvider.checkPurchaseNum(request);
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
        PurchaseListResponse response = shopCartQueryProvider.newPurchaseInfo(request).getContext();

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
        return BaseResponse.success(shopCartQueryProvider.getStoreMarketings(request).getContext());
    }

    /**
     * 查询选择商品店铺营销
     *
     * @return 查询选择商品店铺营销
     */
    @ApiOperation(value = "查询选择商品店铺营销")
    @RequestMapping(value = "/devanning/store/marketing", method = RequestMethod.POST)
    public BaseResponse<PurchaseStoreMarketingResponse> getDevanningStoreMarketings(@RequestBody PurchaseStoreMarketingRequest request) {
        //获取会员
        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(commonUtil.getOperatorId());
        request.setCustomer(customerVO);
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        return BaseResponse.success(shopCartQueryProvider.getDevanningStoreMarketings(request).getContext());
    }

    /**
     * 查询选择商品店铺营销
     *
     * @return 查询选择商品店铺营销
     */
    @ApiOperation(value = "修改商品的选中状态")
    @RequestMapping(value = "/devanning/shopCar/updateCheckStaues", method = RequestMethod.POST)
    public BaseResponse  updateShopCarCheckStaues(@RequestBody ShopCarUpdateCheckStauesRequest request) {
        //获取会员
        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(commonUtil.getOperatorId());
        request.setCustomerId(customerVO.getCustomerId());
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        if (!StringUtils.isNotBlank(request.getType())){
            throw new RuntimeException("必传字段未传");
        }
        shopCartQueryProvider.updateCheckStaues(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询满订单优惠
     *
     * @return 查询满订单优惠
     */
    @ApiOperation(value = "查询满订单优惠")
    @RequestMapping(value = "/orderMarketing", method = RequestMethod.POST)
    public BaseResponse<PurchaseOrderMarketingResponse> getOrderMarketings(@RequestBody PurchaseOrderMarketingRequest request) {
        return shopCartQueryProvider.getOrderMarketings(request);
    }

    /**
     * 查询采购车配置
     * @return
     */
    @ApiOperation(value = "查询采购车配置")
    @RequestMapping(value = "/queryProcurementConfig",method = RequestMethod.GET)
    public BaseResponse queryProcurementConfig(){
        BaseResponse<ProcurementConfigResponse> results = shopCartQueryProvider.queryProcurementConfig();
        if (Objects.nonNull(results.getContext())){
            return BaseResponse.success(results.getContext().getProcurementType().toProcurementType());
        }
        return BaseResponse.FAILED();
    }

    /**
     * 查询购物车总数量
     */
    @ApiOperation(value = "查询采购车配置（兼容零售）")
    @RequestMapping(value = "/calShopCartAndPurchaseNum",method = RequestMethod.POST)
    public BaseResponse<PurchaseCountGoodsResponse> calShopCartAndPurchaseNum(HttpServletRequest request){
        PurchaseCountGoodsRequest purchaseCountGoodsRequest = new PurchaseCountGoodsRequest();
        purchaseCountGoodsRequest.setCustomerId(commonUtil.getOperatorId());
        purchaseCountGoodsRequest.setInviteeId(commonUtil.getPurchaseInviteeId());
        Long wareId = commonUtil.getWareId(request);
        log.info("warid====================="+wareId);
        purchaseCountGoodsRequest.setWareId(wareId);
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            purchaseCountGoodsRequest.setSaasStatus(Boolean.TRUE);
            purchaseCountGoodsRequest.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        PurchaseCountGoodsResponse purchaseCountGoodsResponse =
                shopCartQueryProvider.calShopCartAndPurchaseNum(purchaseCountGoodsRequest).getContext();
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
        PurchaseGetGoodsMarketingResponse context = shopCartQueryProvider.querySuitGoodsByPurchase(purchaseQueryRequest).getContext();

        return BaseResponse.success(context);
    }


    /**
     * 获取视图采购单(商家入驻分组)
     *
     * @return 采购单
     */
    @ApiOperation(value = "获取视图采购单缓存级计算最优惠组合")
    @RequestMapping(value = "/devanning/view/shopCartsAndCacheNew", method = RequestMethod.POST)
    public BaseResponse<MarketingGroupPileResultResponse> devanningInfoViewAndCacheNew(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        request.setMatchWareHouseFlag(request.getMatchWareHouseFlag());
        request.setCustomer(customer);
        request.setLocationCityId(commonUtil.getCityId(httpRequest));
        request.setLocationProvinceId(commonUtil.getProvinceId(httpRequest));
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        //通过客户收货地址和商品指定区域设置商品状态
        //根据用户ID得到收货地址
        log.info("============devanningInfoView：" + JSONObject.toJSONString(request));
        //查询购物车信息
        MarketingGroupCardResponse response = shopCartQueryProvider.devanningPurchaseInfoCache(request).getContext();

        Map<Long, MarketingGroupCardResponse> allMarketingGroupMap = new HashMap<>();
        //1.分组未参与营销活动的商品
        //根据店铺id分组商品列表
        if (CollectionUtils.isNotEmpty(response.getNoHaveGoodsInfoVOList())) {
            Map<Long, List<DevanningGoodsInfoVO>> dgiMap = response.getNoHaveGoodsInfoVOList().stream().collect(Collectors.groupingBy(s -> s.getStoreId()));
            for (Map.Entry<Long, List<DevanningGoodsInfoVO>> entry : dgiMap.entrySet()) {
                Long storeId = entry.getKey();
                //根据店铺Id查询店铺信息
                StoreByIdRequest storeByIdRequest = StoreByIdRequest.builder().storeId(storeId).build();
                BaseResponse<StoreByIdResponse> storeResponse = storeQueryProvider.getById(storeByIdRequest);

                StoreVO storeVO = storeResponse.getContext().getStoreVO();
                MarketingGroupCardResponse mgcr = MarketingGroupCardResponse.builder()
                        .storeId(storeVO.getStoreId())
                        .storeName(storeVO.getStoreName())
                        .key(storeVO.getStoreName())
                        .companyType(storeVO.getCompanyType().toValue())
                        .noHaveGoodsInfoVOList(entry.getValue())
                        .build();

                //获取最近1条加入购物车商品的时间
                LocalDateTime storeMaxTime = entry.getValue().stream().max(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime)).get().getCreateTime();
                mgcr.setAddTime(storeMaxTime);

                allMarketingGroupMap.put(storeId, mgcr);
            }

        }

        //2.分组参与营销活动的商品
        if (CollectionUtils.isNotEmpty(response.getMarketingGroupCards())) {
            for (MarketingGroupCard mgc : response.getMarketingGroupCards()) {
                Long storeId = mgc.getMarketingVO().getStoreId();
                //获取最近1条加入购物车商品的时间
                LocalDateTime storeMaxTime = mgc.getDevanningGoodsInfoVOList().stream().max(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime)).get().getCreateTime();

                if (allMarketingGroupMap.containsKey(storeId)) {//已存在店铺
                    MarketingGroupCardResponse mgcr = allMarketingGroupMap.get(storeId);
                    mgcr.setMarketingGroupCards(Lists.newArrayList(mgc));

                    if (mgcr.getAddTime().isBefore(storeMaxTime)){
                        mgcr.setAddTime(storeMaxTime);
                    }
                } else {//未存在店铺
                    //根据店铺Id查询店铺信息
                    StoreByIdRequest storeByIdRequest = StoreByIdRequest.builder().storeId(storeId).build();
                    BaseResponse<StoreByIdResponse> storeResponse = storeQueryProvider.getById(storeByIdRequest);

                    StoreVO storeVO = storeResponse.getContext().getStoreVO();
                    MarketingGroupCardResponse mgcr = MarketingGroupCardResponse.builder()
                            .storeId(storeVO.getStoreId())
                            .storeName(storeVO.getStoreName())
                            .key(storeVO.getStoreName())
                            .companyType(storeVO.getCompanyType().toValue())
                            .marketingGroupCards(Lists.newArrayList(mgc))
                            .build();
                    mgcr.setAddTime(storeMaxTime);

                    allMarketingGroupMap.put(storeId, mgcr);
                }
            }
        }

        if (MapUtils.isEmpty(allMarketingGroupMap)){
            return BaseResponse.success(MarketingGroupPileResultResponse.builder().build());
        }
        //3.计算营销活动的优惠信息
        for(MarketingGroupCardResponse mgcr : allMarketingGroupMap.values()){
            List<MarketingGroupCard> mgcList = Optional.ofNullable(mgcr.getMarketingGroupCards()).orElse(Lists.newArrayList());//单个商家的营销活动商品
            List<DevanningGoodsInfoVO> dgiList = Optional.ofNullable(mgcr.getNoHaveGoodsInfoVOList()).orElse(Lists.newArrayList());//单个商家的非营销活动商品

            //计算单个商家的营销活动优惠信息
            PriceInfoOfWholesale piow = this.totalPriceCalculator(mgcList, dgiList, customer);
            mgcr.setPriceInfoOfWholesale(piow);
        }
        //4.重新根据商家最近加入囤货购物车商品的时间排序
        List<MarketingGroupCardResponse> mgcrList = allMarketingGroupMap.values().stream().sorted(Comparator.comparing(MarketingGroupCardResponse::getAddTime).reversed()).collect(Collectors.toList());
        return BaseResponse.success(MarketingGroupPileResultResponse.builder().result(mgcrList).build());
    }


    /**
     * 计算营销活动的优惠信息
     * @param marketingGroupCards
     * @param noHaveGoodsInfoVOList
     * @param customer
     * @return
     */
    private PriceInfoOfWholesale totalPriceCalculator(List<MarketingGroupCard> marketingGroupCards, List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList, CustomerVO customer) {
        BigDecimal totalAmount = BigDecimal.ZERO;           // 总金额
        BigDecimal profitAmountOfReduce = BigDecimal.ZERO;  // 满减金额
        BigDecimal profitAmountOfDiscount = BigDecimal.ZERO; // 满赠金额

        log.info("计算营销活动的优惠信息:{},{}", marketingGroupCards.size(), noHaveGoodsInfoVOList.size());

        // 计算参加活动的商品总价格
        for (MarketingGroupCard marketingGroupCard : marketingGroupCards) {
            totalAmount = totalAmount.add(marketingGroupCard.getBeforeAmount());
            BigDecimal profitAmount = marketingGroupCard.getProfitAmount();

            // 未达到活动门槛
            Boolean reachLevel = marketingGroupCard.getReachLevel();
            if(Boolean.FALSE.equals(reachLevel)){ continue; }

            MarketingFullReductionLevelVO currentFullReductionLevel = marketingGroupCard.getCurrentFullReductionLevel();
            if(Objects.nonNull(currentFullReductionLevel)){
                profitAmountOfReduce = profitAmountOfReduce.add(profitAmount);
            }

            MarketingFullDiscountLevelVO currentFullDiscountLevel = marketingGroupCard.getCurrentFullDiscountLevel();
            if(Objects.nonNull(currentFullDiscountLevel)){
                profitAmountOfDiscount = profitAmountOfDiscount.add(profitAmount);
            }
        }

        // 营销活动下的商品超过了限购数量，按照原价进行计算
        for (MarketingGroupCard marketingGroupCard : marketingGroupCards) {
            List<DevanningGoodsInfoVO> devanningGoodsInfoVOs = marketingGroupCard.getOverPurchuseLimitDevanningGoodsInfoVOList();
            if(org.springframework.util.CollectionUtils.isEmpty(devanningGoodsInfoVOs)){
                continue;
            }
            for (DevanningGoodsInfoVO noHaveGoodsInfoVO : devanningGoodsInfoVOs) {
                if(!DefaultFlag.YES.equals(noHaveGoodsInfoVO.getIsCheck())){ continue; }
                /**
                 * 使用VIP价格的情况
                 * （1）当前用户是VIP用户
                 * （2）商品设置了VIP价格，注意0代表的是未设置
                 * （3）VIP价格 < 市场价
                 */
                BigDecimal price = noHaveGoodsInfoVO.getMarketPrice();
                // 当前用户是否为VIP客户
                Boolean isVipCustomer = DefaultFlag.YES.equals(customer.getVipFlag());
                // 注意0代表的就是未设置VIP价
                Boolean vipPriceIsNotBlank = BigDecimal.ZERO.compareTo(noHaveGoodsInfoVO.getVipPrice()) < 0;
                if(isVipCustomer && vipPriceIsNotBlank){
                    BigDecimal vipPrice = noHaveGoodsInfoVO.getVipPrice();
                    BigDecimal marketPrice = noHaveGoodsInfoVO.getMarketPrice();
                    // 市场价和VIP价谁低就用谁
                    if(marketPrice.compareTo(vipPrice) > 0){
                        price = vipPrice;
                    }
                }

                BigDecimal buyCount = BigDecimal.valueOf(noHaveGoodsInfoVO.getBuyCount());
                BigDecimal multiply = price.multiply(buyCount);
                totalAmount = totalAmount.add(multiply);
            }
        }

        // 未参加营销活动的商品
        for (DevanningGoodsInfoVO noHaveGoodsInfoVO : noHaveGoodsInfoVOList) {
            if(!DefaultFlag.YES.equals(noHaveGoodsInfoVO.getIsCheck())){ continue; }
            /**
             * 使用VIP价格的情况
             * （1）当前用户是VIP用户
             * （2）商品设置了VIP价格，注意0代表的是未设置
             * （3）VIP价格 < 市场价
             */
            BigDecimal price = noHaveGoodsInfoVO.getMarketPrice();
            // 当前用户是否为VIP客户
            Boolean isVipCustomer = DefaultFlag.YES.equals(customer.getVipFlag());
            // 注意0代表的就是未设置VIP价
            Boolean vipPriceIsNotBlank = BigDecimal.ZERO.compareTo(noHaveGoodsInfoVO.getVipPrice()) < 0;
            if(isVipCustomer && vipPriceIsNotBlank){
                BigDecimal vipPrice = noHaveGoodsInfoVO.getVipPrice();
                BigDecimal marketPrice = noHaveGoodsInfoVO.getMarketPrice();
                // 市场价和VIP价谁低就用谁
                if(marketPrice.compareTo(vipPrice) > 0){
                    price = vipPrice;
                }
            }

            BigDecimal buyCount = BigDecimal.valueOf(noHaveGoodsInfoVO.getBuyCount());
            BigDecimal multiply = price.multiply(buyCount);
            totalAmount = totalAmount.add(multiply);
        }
        // 优惠的总金额
        BigDecimal profitAmount = profitAmountOfReduce.add(profitAmountOfDiscount);
        BigDecimal payableAmount = totalAmount.subtract(profitAmount);

        PriceInfoOfWholesale priceInfoOfWholesale = PriceInfoOfWholesale.builder()
                .totalAmount(totalAmount)
                .profitAmountOfReduce(profitAmountOfReduce)
                .profitAmountOfDiscount(profitAmountOfDiscount)
                .profitAmount(profitAmount)
                .payableAmount(payableAmount)
                .build();
        return priceInfoOfWholesale;
    }


}
