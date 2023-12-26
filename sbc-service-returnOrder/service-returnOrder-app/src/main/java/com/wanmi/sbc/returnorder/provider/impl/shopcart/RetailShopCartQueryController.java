package com.wanmi.sbc.returnorder.provider.impl.shopcart;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DistributionCommissionUtils;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributorLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerEnableByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributorLevelByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.distribution.DistributionCustomerEnableByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributorLevelByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.DistributorLevelVO;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.image.ImageProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.image.ImagesQueryRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAndStockListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.api.response.image.ImagesResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountQueryProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingMapGetByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingCardGroupRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingGoodsSortResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.returnorder.api.provider.shopcart.RetailShopCartQueryProvider;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingViewCalcVO;
import com.wanmi.sbc.returnorder.bean.vo.PurchaseVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchase;
import com.wanmi.sbc.returnorder.pilepurchase.PilePurchaseService;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseAction;
import com.wanmi.sbc.returnorder.pilepurchaseaction.PilePurchaseActionRepository;
import com.wanmi.sbc.returnorder.purchase.PurchaseService;
import com.wanmi.sbc.returnorder.redis.RedisCache;
import com.wanmi.sbc.returnorder.redis.RedisKeyConstants;
import com.wanmi.sbc.returnorder.redis.RedisService;
import com.wanmi.sbc.returnorder.shopcart.RetailShopCart;
import com.wanmi.sbc.returnorder.shopcart.RetailShopCartService;
import com.wanmi.sbc.returnorder.shopcart.request.RetailShopCartRequest;
import com.wanmi.sbc.returnorder.api.request.purchase.*;
import com.wanmi.sbc.returnorder.api.response.purchase.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-04
 */
@Validated
@RestController
@Log4j2
public class RetailShopCartQueryController implements RetailShopCartQueryProvider {

    @Autowired
    private RetailShopCartService retailShopCartService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private DistributionCustomerQueryProvider distributionCustomerQueryProvider;

    @Autowired
    private DistributorLevelQueryProvider distributorLevelQueryProvider;

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private MarketingFullDiscountQueryProvider marketingFullDiscountQueryProvider;

    @Autowired
    private MarketingFullReductionQueryProvider marketingFullReductionQueryProvider;

    @Autowired
    private PilePurchaseService pilePurchaseService;

    @Autowired
    private PilePurchaseActionRepository pilePurchaseActionRepository;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CustomerAreaLimitDetailProvider customerAreaLimitDetailProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private ImageProvider imageProvider;

    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;


    private static Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    @Override
    public BaseResponse updateCheckStaues(CheckedRetailCartRequest request) {
        retailShopCartService.checkedCartGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 查询迷你采购单请求结构 {@link PurchaseMiniListRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseMiniListResponse> minilist(@RequestBody @Valid PurchaseMiniListRequest request) {
        MiniPurchaseResponse miniPurchaseResponse = retailShopCartService.miniList(KsBeanUtil.convert(request,
                RetailShopCartRequest.class),
                request.getCustomer());
        return BaseResponse.success(KsBeanUtil.convert(miniPurchaseResponse, PurchaseMiniListResponse.class));
    }

    /**
     * @param request 采购单列表请求结构 {@link PurchaseListRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseListResponse> list(@RequestBody @Valid PurchaseListRequest request) {
        PurchaseResponse purchaseResponse = retailShopCartService.list(KsBeanUtil.convert(request, RetailShopCartRequest.class));
        return BaseResponse.success(KsBeanUtil.convert(purchaseResponse, PurchaseListResponse.class));
    }

    /**
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseQueryResponse> query(@RequestBody @Valid PurchaseQueryRequest request) {
        List<RetailShopCart> shopCartList = retailShopCartService.queryPurchase(request.getCustomerId(), request.getGoodsInfoIds
                (), request.getInviteeId());
        PurchaseQueryResponse purchaseQueryResponse = new PurchaseQueryResponse();
        purchaseQueryResponse.setPurchaseList(KsBeanUtil.convertList(shopCartList, PurchaseVO.class));
        return BaseResponse.success(purchaseQueryResponse);
    }

    /**
     * @param request 获取店铺下，是否有优惠券营销，展示优惠券标签请求结构 {@link PurchaseGetStoreCouponExistRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseGetStoreCouponExistResponse> getStoreCouponExist(@RequestBody @Valid
                                                                                 PurchaseGetStoreCouponExistRequest request) {

        if(request.getCustomer()==null){
            return BaseResponse.success(new PurchaseGetStoreCouponExistResponse());
        }
        List<RetailShopCart> shopCartList = retailShopCartService.queryPurchase(request.getCustomer().getCustomerId(), null, request.getInviteeId());
        if (CollectionUtils.isEmpty(shopCartList)) return BaseResponse.success(new PurchaseGetStoreCouponExistResponse());

        List<GoodsInfoVO> goodsInfos = retailGoodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(
                shopCartList.stream().map(i -> i.getGoodsInfoId()).collect(Collectors.toList())
        ).build()).getContext().getGoodsInfos();

        Map<Long, Boolean> storeCouponExist = retailShopCartService.getStoreCouponExist(KsBeanUtil.convertList(goodsInfos, GoodsInfoVO.class),
                KsBeanUtil.convert(request.getCustomer(), CustomerVO.class));
        PurchaseGetStoreCouponExistResponse purchaseGetStoreCouponExistResponse = new
                PurchaseGetStoreCouponExistResponse();
        HashMap<Long, Boolean> map = new HashMap<>();
        storeCouponExist.forEach(map::put);
        purchaseGetStoreCouponExistResponse.setMap(map);
        return BaseResponse.success(purchaseGetStoreCouponExistResponse);
    }

    /**
     * @param request 获取店铺营销信息请求结构 {@link PurchaseGetStoreMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseGetStoreMarketingResponse> getStoreMarketing(@RequestBody @Valid
                                                                                         PurchaseGetStoreMarketingRequest request) {
        Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketing = retailShopCartService.getStoreMarketingBase(request
                        .getGoodsMarketings(),
                KsBeanUtil.convert(request.getCustomer(), CustomerVO.class), request.getFrontReq(), request
                        .getGoodsInfoIdList(), request.getWareId(), null);
        HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
        storeMarketing.forEach((k, v) -> {
            map.put(k, KsBeanUtil.convertList(v, PurchaseMarketingCalcVO.class));
        });
        PurchaseGetStoreMarketingResponse purchaseGetStoreMarketingResponse = new PurchaseGetStoreMarketingResponse();
        purchaseGetStoreMarketingResponse.setMap(map);
        return BaseResponse.success(purchaseGetStoreMarketingResponse);
    }

    /**
     * @param request 获取采购单商品选择的营销请求结构 {@link PurchaseQueryGoodsMarketingListRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseQueryGoodsMarketingListResponse> queryGoodsMarketingList(@RequestBody @Valid
                                                                                                     PurchaseQueryGoodsMarketingListRequest request) {
        List<GoodsMarketingVO> goodsMarketingVOS = retailShopCartService.queryGoodsMarketingList(request.getCustomerId());
        PurchaseQueryGoodsMarketingListResponse purchaseQueryGoodsMarketingListResponse = new
                PurchaseQueryGoodsMarketingListResponse();
        purchaseQueryGoodsMarketingListResponse.setGoodsMarketingList(goodsMarketingVOS);
        return BaseResponse.success(purchaseQueryGoodsMarketingListResponse);
    }

    /**
     * @param request 获取商品营销信息请求结构 {@link PurchaseGetGoodsMarketingRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseGetGoodsMarketingResponse> getGoodsMarketing(@RequestBody @Valid
                                                                                         PurchaseGetGoodsMarketingRequest request) {
        List<GoodsInfoVO> goodsInfos = request.getGoodsInfos();
        return BaseResponse.success(retailShopCartService.getGoodsMarketing(goodsInfos, request.getCustomer(), request.getWareId()));
    }

    /**
     * @param request 获取采购单商品数量请求结构 {@link PurchaseCountGoodsRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseCountGoodsResponse> countGoods(@RequestBody @Valid PurchaseCountGoodsRequest request) {
        Integer total;
        if(request.getSaasStatus()!=null
                && request.getSaasStatus()){
            total = retailShopCartService.countGoodsByCompanyInfoId(request.getCustomerId(), request.getInviteeId(),request.getCompanyInfoId());
        }else {
            total = retailShopCartService.countGoods(request.getCustomerId(), request.getInviteeId());
        }
        PurchaseCountGoodsResponse purchaseCountGoodsResponse = new PurchaseCountGoodsResponse();
        purchaseCountGoodsResponse.setTotal(total);
        return BaseResponse.success(purchaseCountGoodsResponse);
    }

    /**
     * 未登录时,根据前端缓存信息查询迷你购物车信息
     *
     * @param frontReq
     * @return
     */
    @Override
    public BaseResponse<MiniPurchaseResponse> miniListFront(@RequestBody @Valid PurchaseFrontMiniRequest frontReq) {
        MiniPurchaseResponse response = retailShopCartService.miniListFront(frontReq);
        return BaseResponse.success(response);
    }

    /**
     * 未登陆时,根据前端传入的采购单信息,查询组装必要信息
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<PurchaseResponse> listFront(@RequestBody @Valid PurchaseFrontRequest request) {
        PurchaseResponse response = retailShopCartService.listFront(request);
        return BaseResponse.success(response);
    }

    /**
     * 未登录时,验证并设置前端传入的商品使用营销信息
     *
     * @return
     */
    @Override
    public BaseResponse<PurchaseResponse> validateAndSetGoodsMarketings(@RequestBody @Valid
                                                                                    ValidateAndSetGoodsMarketingsRequest request) {
        PurchaseResponse response = retailShopCartService.validateAndSetGoodsMarketings(
                request.getResponse(), request.getGoodsMarketingDTOList());
        return BaseResponse.success(response);
    }

    /**
     * 查询采购车配置
     * @return
     */
    @Override
    public BaseResponse<ProcurementConfigResponse> queryProcurementConfig(){
        return retailShopCartService.getProcurementType();
    }

    /**
     * 采购单社交分销信息
     *
     * @param request
     * @return <p>
     * 1.如果为社交分销    验证分销开关   关 显示营销信息
     * * 如果为社交分销    验证分销开关   开 显示分销价 非自购  分销员状态启用  不显示返利
     * 开 显示分销价 自购 分销员状态启用     显示返利
     * 分销员状态禁用    不显示返利
     * <p>
     * 2.如果为店铺精选购买  验证店铺状态
     * 验证分销店铺商品
     * 3.分销商品去除阶梯价等信息
     * 4.分销价叠加分销员等级
     */
    @Override
    public BaseResponse<Purchase4DistributionResponse> distribution(@RequestBody @Valid Purchase4DistributionRequest
                                                                                request) {
        DistributeChannel channel = request.getDistributeChannel();
        List<GoodsInfoVO> goodsInfoVOList = request.getGoodsInfos();
        List<GoodsInfoVO> goodsInfoComList = request.getGoodsInfos();
        List<GoodsIntervalPriceVO> goodsIntervalPrices = request.getGoodsIntervalPrices();
        CustomerVO customer = request.getCustomer();
        Purchase4DistributionResponse response = Purchase4DistributionResponse.builder().goodsInfos(goodsInfoVOList)
                .goodsInfoComList(goodsInfoComList).build();

        //1.如果为社交分销渠道
        if (null != channel && !Objects.equals(channel.getChannelType(), ChannelType.PC_MALL)) {
            response.setSelfBuying(Boolean.FALSE);
            //分销商品
            List<GoodsInfoVO> goodsInfoDistributionList = goodsInfoVOList.stream().filter(goodsInfo -> Objects
                    .equals(goodsInfo.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED)).collect
                    (Collectors.toList());
            //验证自购
            if ((Objects.equals(channel.getInviteeId(), Constants.PURCHASE_DEFAULT) || Objects.isNull(channel
                    .getInviteeId())) && Objects.nonNull(customer)) {
                DistributionCustomerEnableByCustomerIdResponse customerEnableByCustomerIdResponse =
                        distributionCustomerQueryProvider.checkEnableByCustomerId
                                (DistributionCustomerEnableByCustomerIdRequest.builder().customerId(customer
                                        .getCustomerId()).build()).getContext();
                response.setSelfBuying(customerEnableByCustomerIdResponse.getDistributionEnable() && CollectionUtils
                        .isNotEmpty(goodsInfoDistributionList));
            }
           /* //排除分销商品  todo;暂无分销逻辑，剔除以免导致PC端与APP端同一商品价格不同
            if (channel.getChannelType() == ChannelType.SHOP) {
                goodsInfoComList = new ArrayList<>();
            } else {
                goodsInfoComList = goodsInfoVOList.stream().filter(goodsInfo -> !Objects.equals(goodsInfo
                        .getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED)).collect(Collectors.toList());
            }*/
            //3.分销商品去除阶梯价等信息
            goodsIntervalPrices = setDistributorPrice(goodsInfoVOList, goodsIntervalPrices);

            //2.如果为店铺精选购买
            retailShopCartService.verifyDistributorGoodsInfo(channel, goodsInfoVOList);

            //4.分销价叠加分销员等级
            if (Objects.nonNull(customer)) {
                BaseResponse<DistributorLevelByCustomerIdResponse> resultBaseResponse =
                        distributorLevelQueryProvider.getByCustomerId(new DistributorLevelByCustomerIdRequest
                                (customer.getCustomerId()));
                DistributorLevelVO distributorLevelVO = Objects.isNull(resultBaseResponse) ? null :
                        resultBaseResponse.getContext().getDistributorLevelVO();
                if (Objects.nonNull(distributorLevelVO) && Objects.nonNull(distributorLevelVO.getCommissionRate())) {
                    goodsInfoVOList.stream().forEach(goodsInfoVO -> {
                        if (DistributionGoodsAudit.CHECKED.equals(goodsInfoVO.getDistributionGoodsAudit())) {
                            BigDecimal commissionRate = distributorLevelVO.getCommissionRate();
                            BigDecimal distributionCommission = goodsInfoVO.getDistributionCommission();
                            distributionCommission = DistributionCommissionUtils.calDistributionCommission(distributionCommission,commissionRate);
                            goodsInfoVO.setDistributionCommission(distributionCommission);
                        }
                    });
                }
            }
        }
        response.setGoodsInfoComList(goodsInfoComList);
        response.setGoodsInfos(goodsInfoVOList);
        response.setGoodsIntervalPrices(goodsIntervalPrices);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(CheckPurchaseNumRequest request) {
        if(CollectionUtils.isEmpty(request.getCheckPurchaseNumDTOS())){
            return BaseResponse.success(new CheckPurchaseNumResponse());
        }
        List<CheckPurchaseNumVO> checkPurchaseNumVOS = new ArrayList<>();
        List<String> goodsInfoIds = request.getCheckPurchaseNumDTOS().stream().map(CheckPurchaseNumDTO::getGoodsInfoId).collect(Collectors.toList());
        List<GoodsInfoVO> goodsInfoVOS = retailGoodsInfoQueryProvider.listGoodsInfoAndStcokByIds(GoodsInfoAndStockListByIdsRequest.builder().goodsInfoIds(goodsInfoIds).build()).getContext().getGoodsInfos();
        List<CheckPurchaseNumDTO> checkPurchaseNumDTOS = request.getCheckPurchaseNumDTOS();
        if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
            goodsInfoVOS.forEach(goodsInfoVO -> {
                if(Objects.nonNull(goodsInfoVO.getPurchaseNum())){
                    Long num = checkPurchaseNumDTOS.stream().
                            filter(checkPurchaseNumDTO -> checkPurchaseNumDTO.getGoodsInfoId().equals(goodsInfoVO.getGoodsInfoId())).collect(Collectors.toList())
                            .stream().findFirst().get().getNum();
                    if(goodsInfoVO.getPurchaseNum() >= 0 && num > goodsInfoVO.getPurchaseNum()){
                        CheckPurchaseNumVO vo = new CheckPurchaseNumVO();
                        vo.setGoodsInfoId(goodsInfoVO.getGoodsInfoId());
                        vo.setGoodsName(goodsInfoVO.getGoodsInfoName());
                        vo.setMarketingId(goodsInfoVO.getMarketingId());
                        vo.setNum(goodsInfoVO.getPurchaseNum());
                        checkPurchaseNumVOS.add(vo);
                    }
                }
            });
        }
        CheckPurchaseNumResponse checkPurchaseNumResponse = new CheckPurchaseNumResponse();
        if(CollectionUtils.isNotEmpty(checkPurchaseNumVOS)){
            checkPurchaseNumResponse.setCheckPurchaseNum(checkPurchaseNumVOS);
        }
        return BaseResponse.success(checkPurchaseNumResponse);
    }

    @Override
    public BaseResponse<PurchaseListResponse> purchaseInfo(@RequestBody @Valid PurchaseListRequest request) {


        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase purchaseInfo start");

        try {
            RetailShopCartRequest purchaseRequest = RetailShopCartRequest.builder()
                    .customerId(request.getCustomerId())
                    .inviteeId(request.getInviteeId())
                    .wareId(request.getWareId())
                    .matchWareHouseFlag(request.getMatchWareHouseFlag())
                    .build();
            if (Objects.nonNull(request.getCompanyInfoId())) {
                purchaseRequest.setCompanyInfoId(request.getCompanyInfoId());
            }
            PurchaseResponse purchaseResponse;
            // 需要分页增加入参
            if (request.getIsPage()) {
                purchaseRequest.setIsRefresh(request.getIsRefresh());
                purchaseRequest.setPageNum(request.getPageNum());
                purchaseRequest.setPageSize(request.getPageSize());
                purchaseResponse = retailShopCartService.pageList(purchaseRequest);
            } else {
                purchaseResponse = retailShopCartService.list(purchaseRequest);
            }
            sb.append(",shopCartService.list end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<String> checkGoodsInfoIds = purchaseResponse.getGoodsInfos().stream()
                    .filter(info -> info.getIsCheck().equals(DefaultFlag.YES))
                    .map(GoodsInfoVO::getGoodsInfoId)
                    .collect(Collectors.toList());

            if (checkGoodsInfoIds.size() > 0) {
                request.setGoodsInfoIds(checkGoodsInfoIds);
            }

            CustomerVO customer = request.getCustomer();

            PurchaseListResponse response = KsBeanUtil.convert(purchaseResponse, PurchaseListResponse.class);
            if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
//            // 改造，直接查处所有商品营销信息
//            List<String> goodsInfoIdList = response.getGoodsInfos().stream()
//                    .map(GoodsInfoVO::getGoodsInfoId)
//                    .collect(Collectors.toList());
//            request.setGoodsInfoIds(goodsInfoIdList);

                //设定SKU状态及计算区间价
           /* List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class);
            GoodsIntervalPriceByCustomerIdResponse priceResponse = goodsIntervalPriceProvider.putValidGoodsInfoByCustomerId(ValidGoodsIntervalPriceByCustomerIdRequest.builder()
                    .goodsInfoDTOList(goodsInfoDTOList)
                    .customerId(request.getCustomerId())
                    .matchWareHouseFlag(request.getMatchWareHouseFlag())
                    .build()).getContext();
            if (Objects.nonNull(priceResponse.getGoodsIntervalPriceVOList())) {
                response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            } else {
                response.setGoodsIntervalPrices(Collections.emptyList());
            }
            response.setGoodsInfos(priceResponse.getGoodsInfoVOList());*/

                /**
                 * 根据囤货、购物车类型判断商品状态
                 */
//                ProcurementConfigResponse context = queryProcurementConfig().getContext();
//                if(ProcurementTypeEnum.STOCKUP.equals(context.getProcurementType())){
//                    response.getGoodsInfos().forEach(goodsInfoVO -> {
//                        if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
//                                && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())){
//                            goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
//                        }else{
//                            goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
//                        }
//                    });
//                }else{
                    //设置商品状态
                    response.getGoodsInfos().forEach(goodsInfoVO -> {
                        if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
                                && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
                            // 判断是否有T，如果是1，就设置为2
                            if (goodsInfoVO.getGoodsInfoName().endsWith("T") || goodsInfoVO.getGoodsInfoName().endsWith("t")) {
                                if (goodsInfoVO.getStock().compareTo(BigDecimal.ONE) < 0 && (Objects.isNull(goodsInfoVO.getMarketingId()) || goodsInfoVO.getPurchaseNum() == -1)) {
                                    goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                                }
                                //限购商品
//                                if(Objects.nonNull(goodsInfoVO.getMarketingId()) && goodsInfoVO.getPurchaseNum() != -1){
//                                    if(goodsInfoVO.getPurchaseNum() < 1){
//                                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
//                                    }
//                                }
                            } else {
                                if (goodsInfoVO.getStock().compareTo(BigDecimal.ONE) < 0 && (Objects.isNull(goodsInfoVO.getMarketingId()) || goodsInfoVO.getPurchaseNum() == -1)) {
                                    goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                                }
                                //限购商品
//                                if(Objects.nonNull(goodsInfoVO.getMarketingId()) && goodsInfoVO.getPurchaseNum() != -1){
//                                    if(goodsInfoVO.getPurchaseNum() < 1){
//                                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                                    }
//                                }
                                //根据指定区域销售地址和客户收货地址省、市对比判断商品状态
                                if (StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea()) && Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())){
                                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                                    if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                                    }
                                }
                            }
                        } else {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                        }
                    });
//                }

                //排除分销商品
                List<GoodsInfoVO> goodsInfoComList = response.getGoodsInfos();

                // 采购单商品编号，只包含有效的商品
                List<GoodsInfoVO> goodsInfos = goodsInfoComList.stream()
                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
                List<GoodsInfoVO> requestGoods = KsBeanUtil.convert(goodsInfos, GoodsInfoVO.class);
                //特价商品设置市场价&大客户设置vip价格
                requestGoods.stream().forEach(g -> {
                    if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                        if (Objects.nonNull(g.getSpecialPrice())) {
                            g.setMarketPrice(g.getSpecialPrice());
                        }
                    }
                });
                /**
                 * 取请求skuId,和购物车有效商品交集
                 */
                List<String> purchaseGoodsIdList = requestGoods.stream().map(g -> g.getGoodsInfoId()).collect(Collectors.toList());
                List<String> intersectionList = request.getGoodsInfoIds().stream().filter(r -> purchaseGoodsIdList.contains(r)).collect(Collectors.toList())
                        .parallelStream().collect(Collectors.toList());

                log.info("purchaseGoodsIdList ============ {}", purchaseGoodsIdList);

                log.info("intersectionList ============ {}", intersectionList);

               // requestGoods = requestGoods.stream().filter(param -> param.getGoodsInfoType() == 0).collect(Collectors.toList());
                // 不分页的时候
                if (!request.getIsPage()) {
                    // 获取采购营销信息及同步商品营销
                    PurchaseMarketingResponse purchasesMarketing = retailShopCartService.getPurchasesMarketingInit(intersectionList
                            , requestGoods, customer, request.getWareId());

                    sb.append(",shopCartService.getPurchasesMarketing end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                    response.setGoodsMarketingMap(purchasesMarketing.getMap());
                    response.setSelfBuying(false);
                    goodsInfos = response.getGoodsInfos().stream().map(goodsInfo ->
                            purchasesMarketing.getGoodsInfos().stream()
                                    .filter(item -> item.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                                    .findFirst().map(goodsInfoVO -> {
                                if (null != customer.getEnterpriseStatusXyy() && customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED)) {
                                    GoodsInfoVO vo = new GoodsInfoVO();
                                    KsBeanUtil.copyPropertiesThird(goodsInfoVO, vo);
//                                    vo.setMarketPrice(null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                                    vo.setMarketPrice(goodsInfoVO.getMarketPrice());
                                    return vo;
                                } else {
                                    return goodsInfoVO;
                                }
                            }).orElse(goodsInfo)).collect(Collectors.toList());

                    //参与营销的商品
                    List<String> marketingGoodsInfoIds = response.getGoodsMarketingMap().entrySet().stream().map(set -> set.getKey()).collect(Collectors.toList());
                    //特价判断，设置特价
                    goodsInfos.stream().forEach(g -> {
                        if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                            if (Objects.nonNull(g.getSpecialPrice())) {
                                g.setMarketPrice(g.getSpecialPrice());
                            }
                        }
                        if (g.getAddedFlag() == 0 && DefaultFlag.YES.equals(g.getIsCheck())) {
                            g.setIsCheck(DefaultFlag.NO);
                        }
                        //没参加营销活动的商品有vip则设置vip价格
                        if (purchaseGoodsIdList.contains(g.getGoodsInfoId())
                                && !marketingGoodsInfoIds.contains(g.getGoodsInfoId())) {
                            if (Objects.nonNull(customer.getVipFlag()) && DefaultFlag.YES.equals(customer.getVipFlag())) {
                                g.setMarketPrice(null != g.getVipPrice() && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? g.getVipPrice() : g.getMarketPrice());
                            }
                        }
                    });

                    response.setGoodsInfos(goodsInfos);

                    response.setGoodsMarketings(purchasesMarketing.getGoodsMarketings());

                    // 获取店铺对应的营销信息
                    List<PurchaseMarketingCalcVO> giftMarketing = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(response.getGoodsMarketings())) {
                        response.setStoreMarketingMap(purchasesMarketing.getStoreMarketingMap());
                        //过滤已选营销
                        HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                        response.getStoreMarketingMap().forEach((k, v) -> {
                            List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p -> CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                            map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                            calcResponses.forEach(purchaseMarketingCalcVO -> {
                                if (Objects.nonNull(purchaseMarketingCalcVO.getFullGiftLevelList())) {
                                    giftMarketing.add(purchaseMarketingCalcVO);
                                }
                            });
                        });
                        response.setStoreMarketingMap(map);
                    } else {
                        response.setStoreMarketingMap(new HashMap<>());
                    }
                    //组装赠品信息
                    if (CollectionUtils.isNotEmpty(giftMarketing)) {
                        setGiftMarketingShop(giftMarketing, request.getMatchWareHouseFlag());
                        response.setGiftList(giftMarketing);
                    }

                    //排除选中的商品id中无效的商品id
                    List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().stream()
                            .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
                    request.setGoodsInfoIds(goodsInfoVOS.stream()
                            .filter(goodsInfo -> request.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                            .map(GoodsInfoVO::getGoodsInfoId)
                            .collect(Collectors.toList()));

                    sb.append(",shopCartService.calcAmount start time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                    response = retailShopCartService.calcAmount(KsBeanUtil.convert(response, PurchaseListResponse.class), customer, intersectionList);

                    sb.append(",shopCartService.calcAmount end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    //特价判断，设置特价
                    response.getGoodsInfos().stream().forEach(g -> {
                        if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                            if (Objects.nonNull(g.getSpecialPrice())) {
                                g.setMarketPrice(g.getSpecialPrice());
                            }
                        }
                        if (g.getAddedFlag() == 0 && DefaultFlag.YES.equals(g.getIsCheck())) {
                            g.setIsCheck(DefaultFlag.NO);
                        }
                    });

                    // 初始化时获取采购营销信息及同步商品营销
                    if (request.getIsRefresh()) {
                        PurchaseMarketingResponse purchasesMarketing = retailShopCartService.getPurchasesMarketingInit(intersectionList,
                                requestGoods, customer, request.getWareId());

                        sb.append(",shopCartService.getPurchasesMarketing end time=");
                        sb.append(System.currentTimeMillis() - sTm);
                        sTm = System.currentTimeMillis();

                        response.setGoodsMarketingMap(purchasesMarketing.getMap());
                        response.setSelfBuying(false);
                        goodsInfos = response.getGoodsInfos().stream().map(goodsInfo ->
                                purchasesMarketing.getGoodsInfos().stream()
                                        .filter(item -> item.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId()))
                                        .findFirst().map(goodsInfoVO -> {
                                    if ((null != customer.getEnterpriseStatusXyy() && customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED))
                                            || (Objects.nonNull(customer.getVipFlag()) && DefaultFlag.YES.equals(customer.getVipFlag()))) {
                                        log.info("参与影响活动，满足设置大客户价条件==============================》vipPrice={}",goodsInfoVO.getVipPrice());
                                        GoodsInfoVO vo = new GoodsInfoVO();
                                        KsBeanUtil.copyPropertiesThird(goodsInfoVO, vo);
//                                        vo.setMarketPrice(null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                                        vo.setMarketPrice(goodsInfoVO.getMarketPrice());
                                        return vo;
                                    } else {
                                        return goodsInfoVO;
                                    }
                                }).orElse(goodsInfo)).collect(Collectors.toList());
                        //没参加营销活动的商品有vip则设置vip价格
                        //参与营销的商品
                        List<String> marketingGoodsInfoIds = response.getGoodsMarketingMap().entrySet().stream().map(set -> set.getKey()).collect(Collectors.toList());
                        goodsInfos.stream().forEach(goodsInfoVO -> {
                            if (purchaseGoodsIdList.contains(goodsInfoVO.getGoodsInfoId())
                                    && !marketingGoodsInfoIds.contains(goodsInfoVO.getGoodsInfoId())) {
                                if (Objects.nonNull(customer.getVipFlag()) && DefaultFlag.YES.equals(customer.getVipFlag())) {
                                    goodsInfoVO.setMarketPrice(null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                                }
                            }
                        });

                        response.setGoodsMarketings(purchasesMarketing.getGoodsMarketings());

                        // 获取店铺对应的营销信息
                        List<PurchaseMarketingCalcVO> giftMarketing = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(response.getGoodsMarketings())) {
                            response.setStoreMarketingMap(purchasesMarketing.getStoreMarketingMap());
                            //过滤已选营销
                            HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                            response.getStoreMarketingMap().forEach((k, v) -> {
                                List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p -> CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                                map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                                calcResponses.forEach(purchaseMarketingCalcVO -> {
                                    if (Objects.nonNull(purchaseMarketingCalcVO.getFullGiftLevelList())) {
                                        giftMarketing.add(purchaseMarketingCalcVO);
                                    }
                                });
                            });
                            response.setStoreMarketingMap(map);
                        } else {
                            response.setStoreMarketingMap(new HashMap<>());
                        }
                        //组装赠品信息
                        if (CollectionUtils.isNotEmpty(giftMarketing)) {
                            setGiftMarketingShop(giftMarketing, request.getMatchWareHouseFlag());
                            response.setGiftList(giftMarketing);
                        }
                        response.setGoodsInfos(goodsInfos);
                    }

                    //排除选中的商品id中无效的商品id
                    List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().stream()
                            .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());

                    if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
                        request.setGoodsInfoIds(goodsInfoVOS.stream()
                                .filter(goodsInfo -> request.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                                .map(GoodsInfoVO::getGoodsInfoId)
                                .collect(Collectors.toList()));
                    }

                    sb.append(",shopCartService.calcAmount start time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                    if (request.getIsRefresh()) {
                        response = retailShopCartService.calcAmount(KsBeanUtil.convert(response, PurchaseListResponse.class), customer,intersectionList);
                    }

                    sb.append(",shopCartService.calcAmount end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                }
            }
            //根据goodsInfo中的stock顺序对goods重新排序
            if (CollectionUtils.isNotEmpty(response.getGoodsInfos())&&CollectionUtils.isNotEmpty(response.getGoodses())) {
                response.getGoodsInfos().sort((a, b) -> {
                    BigDecimal su = (a.getBuyCount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(a.getBuyCount())) .subtract (a.getStock() == null ? BigDecimal.ZERO : a.getStock());
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
//                        return Long.compare(su2, su);
                    }
                });
                List<GoodsVO> goodses = response.getGoodses();
                Map<String, GoodsVO> goodsVOMap = goodses.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
                List<GoodsVO> goodsSortList = new ArrayList<>(20);
                for (GoodsInfoVO inner : response.getGoodsInfos()) {
                    goodsSortList.add(goodsVOMap.get(inner.getGoodsId()));
                    if(goodsVOMap.get(inner.getGoodsId())!=null&&StringUtils.isNotBlank(goodsVOMap.get(inner.getGoodsId()).getGoodsSubtitle())){
                        inner.setGoodsSubtitle((goodsVOMap.get(inner.getGoodsId()).getGoodsSubtitle()));
                        /*//重新计算商品副标题（以营销/vip价格计算副标题）
                        String subTitlePrice = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(inner.getGoodsSubtitle(), "元"), "=");
                        //修改副标题
                        BigDecimal goodsScale = inner.getMarketPrice().divide(inner.getAddStep(),2, BigDecimal.ROUND_HALF_UP);
                        Matcher isNum = NUMBER_PATTERN.matcher(subTitlePrice);
                        String replace = inner.getGoodsSubtitle();
                        if(isNum.matches()){
                            //纯数字
                            replace = StringUtils.replace(inner.getGoodsSubtitle(), subTitlePrice, String.valueOf(goodsScale));
                        }else{
                            //不是纯数字
                            replace = StringUtils.appendIfMissingIgnoreCase(replace,
                                    "=" + String.valueOf(goodsScale) + "元/"
                                            + inner.getGoodsSubtitle()
                                            .substring(inner.getGoodsSubtitle().length()-1,
                                                    inner.getGoodsSubtitle().length()));
                        }
                        inner.setGoodsSubtitle(replace);*/

                    }
                }
                response.setGoodses(goodsSortList);
            }

            if(request.getIsPage()) {
                Page<PurchaseVO> purchasePage = purchaseResponse.getPurchasePage();
                if (Objects.nonNull(purchasePage)) {
                    if (request.getIsRefresh()) {
                        Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
                        Map<String, GoodsInfoVO> goodsInfoVOMap = response.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
                        List<GoodsVO> goodsVOS = purchaseResponse.getGoodsPageIds().stream()
                                .map(id -> goodsVOMap.get(id))
                                .distinct()
                                .collect(Collectors.toList());
                        List<GoodsInfoVO> goodsInfoVOS = purchaseResponse.getGoodsInfoPageIds().stream()
                                .map(id -> goodsInfoVOMap.get(id))
                                .collect(Collectors.toList());
                        PageImpl<GoodsVO> goodsPage = new PageImpl<>(goodsVOS, request.getPageable(), purchasePage.getTotalElements());
                        PageImpl<GoodsInfoVO> goodsInfoPage = new PageImpl<>(goodsInfoVOS, request.getPageable(), purchasePage.getTotalElements());
                        response.setGoodsPage(new MicroServicePage<>(goodsPage, request.getPageable()));
                        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoPage, request.getPageable()));
                        response.setGoodses(new ArrayList<>());
                        response.setGoodsInfos(new ArrayList<>());
                    } else {
                        PurchaseListResponse purchaseListResponse = new PurchaseListResponse();
                        PageImpl<GoodsVO> goodsPage = new PageImpl<>(response.getGoodses(), request.getPageable(), purchasePage.getTotalElements());
                        PageImpl<GoodsInfoVO> goodsInfoPage = new PageImpl<>(response.getGoodsInfos(), request.getPageable(), purchasePage.getTotalElements());
                        purchaseListResponse.setGoodsPage(new MicroServicePage<>(goodsPage, request.getPageable()));
                        purchaseListResponse.setGoodsInfoPage(new MicroServicePage<>(goodsInfoPage, request.getPageable()));
                        response = purchaseListResponse;
                    }
                }
            }
//            if(Objects.nonNull(response.getGoodsInfoPage())){
//                MicroServicePage<GoodsInfoVO> goodsInfoPage = response.getGoodsInfoPage();
//                Map<Long, List<PurchaseMarketingCalcVO>> storeMarketingMap = response.getStoreMarketingMap();
//                List<MarketingScopeVO> marketingScopes = new ArrayList<>();
//                if(CollectionUtils.isNotEmpty(goodsInfoPage.getContent()) && !storeMarketingMap.isEmpty()){
//                    goodsInfoPage.stream().forEach(goodsInfoVO -> {
//                        storeMarketingMap.forEach((id,purchaseMarketings) ->{
//                            purchaseMarketings.stream().forEach(purchaseMarketing ->{
//                                //满赠营销
//                                List<MarketingFullGiftLevelVO> fullGiftLevelList = purchaseMarketing.getFullGiftLevelList();
//                                if(CollectionUtils.isNotEmpty(fullGiftLevelList)){
//                                    List<MarketingScopeVO> marketingScopeVOList = new ArrayList<>();
//                                    fullGiftLevelList.forEach(fullGiftLevel ->{
//                                        MarketingScopeByMarketingIdRequest marketingIdRequest = new MarketingScopeByMarketingIdRequest();
//                                        marketingIdRequest.setMarketingId(fullGiftLevel.getMarketingId());
//                                        marketingIdRequest.setSkuId(goodsInfoVO.getGoodsInfoId());
//                                        List<MarketingScopeVO> marketingScopeVOLists = marketingScopeQueryProviderl.listByMarketingIdAndSkuId(marketingIdRequest).getContext().getMarketingScopeVOList();
//                                        if(CollectionUtils.isNotEmpty(marketingScopeVOLists)){
//                                            marketingScopeVOLists.stream().forEach(marketingScopeVO -> {
//                                                if(Objects.nonNull(marketingScopeVO.getPurchaseNum())){
//                                                    marketingScopeVOList.add(marketingScopeVO);
//                                                }
//                                            });
//                                            marketingScopes.addAll(marketingScopeVOList);
//                                        }
//                                    });
//                                }
//                                //满减营销
//                                List<MarketingFullReductionLevelVO> fullReductionLevelList = purchaseMarketing.getFullReductionLevelList();
//                                if(CollectionUtils.isNotEmpty(fullReductionLevelList)){
//                                    List<MarketingScopeVO> marketingScopeVOList = new ArrayList<>();
//                                    fullReductionLevelList.forEach(fullReductionLevel -> {
//                                        MarketingScopeByMarketingIdRequest marketingIdRequest = new MarketingScopeByMarketingIdRequest();
//                                        marketingIdRequest.setMarketingId(fullReductionLevel.getMarketingId());
//                                        marketingIdRequest.setSkuId(goodsInfoVO.getGoodsInfoId());
//                                        List<MarketingScopeVO> marketingScopeVOLists = marketingScopeQueryProviderl.listByMarketingIdAndSkuId(marketingIdRequest).getContext().getMarketingScopeVOList();
//                                        if(CollectionUtils.isNotEmpty(marketingScopeVOLists)){
//                                            marketingScopeVOLists.stream().forEach(marketingScopeVO -> {
//                                                if(Objects.nonNull(marketingScopeVO.getPurchaseNum())){
//                                                    marketingScopeVOList.add(marketingScopeVO);
//                                                }
//                                            });
//                                            marketingScopes.addAll(marketingScopeVOList);
//
//                                        }
//                                    });
//                                }
//                                //满折营销
//                                List<MarketingFullDiscountLevelVO> fullDiscountLevelList = purchaseMarketing.getFullDiscountLevelList();
//                                if(CollectionUtils.isNotEmpty(fullDiscountLevelList)){
//                                    List<MarketingScopeVO> marketingScopeVOList = new ArrayList<>();
//                                    fullDiscountLevelList.forEach(fullDiscountLevel -> {
//                                        MarketingScopeByMarketingIdRequest marketingIdRequest = new MarketingScopeByMarketingIdRequest();
//                                        marketingIdRequest.setMarketingId(fullDiscountLevel.getMarketingId());
//                                        marketingIdRequest.setSkuId(goodsInfoVO.getGoodsInfoId());
//                                        List<MarketingScopeVO> marketingScopeVOLists = marketingScopeQueryProviderl.listByMarketingIdAndSkuId(marketingIdRequest).getContext().getMarketingScopeVOList();
//                                        if(CollectionUtils.isNotEmpty(marketingScopeVOLists)){
//                                            marketingScopeVOLists.stream().forEach(marketingScopeVO -> {
//                                                if(Objects.nonNull(marketingScopeVO.getPurchaseNum())){
//                                                    marketingScopeVOList.add(marketingScopeVO);
//                                                }
//                                            });
//                                            marketingScopes.addAll(marketingScopeVOList);
//                                        }
//                                    });
//                                }
//                            });
//                        });
//                    });
//                }
//                if(CollectionUtils.isNotEmpty(marketingScopes)){
//                    Map<String, List<MarketingScopeVO>> collect = marketingScopes.stream().collect(Collectors.groupingBy(MarketingScopeVO::getScopeId));
//                    response.setPurchaseNumMap(collect);
//                }
//            }
            return BaseResponse.success(response);
        }finally {
            log.info(sb.toString());
        }

    }


    @Override
    public BaseResponse<com.wanmi.sbc.returnorder.api.response.purchase.MarketingGroupCardResponse> purchaseInfoAndCache(@RequestBody @Valid PurchaseListRequest request) {
            CustomerVO customer = request.getCustomer();
            RetailShopCartRequest purchaseRequest = RetailShopCartRequest.builder()
                    .customerId(request.getCustomerId())
                    .inviteeId(request.getInviteeId())
                    .wareId(request.getWareId())
                    .matchWareHouseFlag(request.getMatchWareHouseFlag())
                    .build();
            if (Objects.nonNull(request.getCompanyInfoId())) {
                purchaseRequest.setCompanyInfoId(request.getCompanyInfoId());
            }
            PurchaseResponse purchaseResponse;
            purchaseResponse = retailShopCartService.pageListAndCache(purchaseRequest);
            List<GoodsInfoVO> retailgoodsInfos = purchaseResponse.getGoodsInfos();


            if (CollectionUtils.isEmpty(retailgoodsInfos)){
                return BaseResponse.success(new com.wanmi.sbc.returnorder.api.response.purchase.MarketingGroupCardResponse());
            }

            /**
             * 格式化零售封面图
             */
            List<String> goodsIds = retailgoodsInfos.stream().map(x -> x.getGoodsId()).collect(Collectors.toList());
            ImagesQueryRequest build = ImagesQueryRequest.builder().goodsIds(goodsIds).build();
            ImagesResponse imagesResponse = imageProvider.listByGoodsIds(build).getContext();
            List<GoodsImageVO> imageVOS = imagesResponse.getImageVOS();
            Map<String, List<GoodsImageVO>> collect2 = imageVOS.stream().sorted(Comparator.comparing(GoodsImageVO::getImageId))
                    .collect(Collectors.groupingBy(GoodsImageVO::getGoodsId));
            for (GoodsInfoVO goodsInfoVO : retailgoodsInfos){
                List<GoodsImageVO> goodsImageVOS = collect2.get(goodsInfoVO.getGoodsId());
                if(CollectionUtils.isEmpty(goodsImageVOS) || goodsImageVOS.size() < 2){
                    continue;
                }
                GoodsImageVO goodsImageVO = goodsImageVOS.get(1);// 第二张是窄图
                goodsInfoVO.setGoodsInfoImg(goodsImageVO.getArtworkUrl());
            }

            //设置商品状态 库存 上下架 是否失效 加设置特价商品的价格
            setStockAndGoodsStatus(retailgoodsInfos,45l);
            this.setGoodsStaues(retailgoodsInfos,request);
            List<DevanningGoodsInfoVO> noJoinMarketingGoods = new LinkedList<>(); //一定不会去参加营销的商品
            List<DevanningGoodsInfoVO> JoinMarketingGoods = new LinkedList<>();//需要去参加营销的商品
            //目前散批不参加营销
            //调用营销接口获取最优
            com.wanmi.sbc.marketing.api.response.market.MarketingGroupCardResponse marketingGroupCardResponse = new com.wanmi.sbc.marketing.api.response.market.MarketingGroupCardResponse();
            noJoinMarketingGoods.addAll(KsBeanUtil.convert(retailgoodsInfos,DevanningGoodsInfoVO.class));
            if (CollectionUtils.isNotEmpty(JoinMarketingGoods)){
                marketingGroupCardResponse = marketingQueryProvider.goodsGroupByMarketing(MarketingCardGroupRequest.builder()
                        .devanningGoodsInfoVOList(JoinMarketingGoods).customerId(request.getCustomerId())
                        .customerVO(customer)
                        .build()).getContext();
            }
            //给未参加营销的商品算值
            if (CollectionUtils.isNotEmpty(noJoinMarketingGoods)){
                noJoinMarketingGoods.forEach(goodsInfoVO -> {
                    if (Objects.nonNull(customer.getVipFlag()) && DefaultFlag.YES.equals(customer.getVipFlag())) {
                        goodsInfoVO.setMarketPrice(
                                null != goodsInfoVO.getVipPrice() && goodsInfoVO.getVipPrice().compareTo(BigDecimal.ZERO) > 0
                                        &&  goodsInfoVO.getVipPrice().compareTo(goodsInfoVO.getMarketPrice()) < 0
                                        ? goodsInfoVO.getVipPrice() : goodsInfoVO.getMarketPrice());
                        goodsInfoVO.setVipPrice(goodsInfoVO.getMarketPrice());
                    }
                });
                //计算未参加营销总价
                BigDecimal reduce = noJoinMarketingGoods.stream().filter(v -> {
                    if (v.getIsCheck().equals(DefaultFlag.YES) && GoodsStatus.OK.equals(v.getGoodsStatus())) {
                        return true;
                    }
                    return false;
                }).map(v -> v.getMarketPrice().multiply(BigDecimal.valueOf(v.getBuyCount()))).reduce(BigDecimal.ZERO, BigDecimal::add);
                PriceInfoOfWholesale priceInfoOfWholesale = marketingGroupCardResponse.getPriceInfoOfWholesale();
                if(Objects.nonNull(priceInfoOfWholesale)){
                    priceInfoOfWholesale.setTotalAmount(marketingGroupCardResponse.getPriceInfoOfWholesale().getTotalAmount().add(reduce));
                    priceInfoOfWholesale.setPayableAmount(marketingGroupCardResponse.getPriceInfoOfWholesale().getPayableAmount().add(reduce));
                } else {
                    priceInfoOfWholesale = PriceInfoOfWholesale.builder().totalAmount(reduce).payableAmount(reduce).build();
                }
                marketingGroupCardResponse.setPriceInfoOfWholesale(priceInfoOfWholesale);
                //没参加营销的商品
                List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList = marketingGroupCardResponse.getNoHaveGoodsInfoVOList();
                if(!CollectionUtils.isEmpty(noHaveGoodsInfoVOList)){
                    noHaveGoodsInfoVOList.addAll(noJoinMarketingGoods);
                    marketingGroupCardResponse.setNoHaveGoodsInfoVOList(noHaveGoodsInfoVOList);
                } else {
                    marketingGroupCardResponse.setNoHaveGoodsInfoVOList(noJoinMarketingGoods);
                }
            }
            if (CollectionUtils.isNotEmpty(marketingGroupCardResponse.getMarketingGroupCards())){
                for (MarketingGroupCard marketingGroupCard:marketingGroupCardResponse.getMarketingGroupCards()){
                    //赠品状态赋值
                    if (Objects.nonNull(marketingGroupCard.getCurrentFullGiftLevel())){
                        for (MarketingFullGiftDetailVO marketingFullGiftDetailVO:marketingGroupCard.getCurrentFullGiftLevel().getFullGiftDetailList()){
                            //填充赠品实体
                            if (Objects.isNull(marketingFullGiftDetailVO.getGiftGoodsInfoVO())){
                                List<String> list =  new LinkedList<>();
                                list.add(marketingFullGiftDetailVO.getProductId());
                                //这里已经判断库存和失效
                                List<GiftGoodsInfoVO> goodsInfos = goodsInfoQueryProvider.findGoodsInfoByIdsAndCache(GoodsInfoListByIdsRequest.builder()
                                        .goodsInfoIds(list).matchWareHouseFlag(request.getMatchWareHouseFlag()).build()).getContext().getGoodsInfos();
                                if (CollectionUtils.isEmpty(goodsInfos)){
                                    throw new SbcRuntimeException("商品不存在");
                                }
                                marketingFullGiftDetailVO.setGiftGoodsInfoVO(goodsInfos.stream().findAny().get());
                            }
                            //限赠状态已经限赠数量
                            this.limitPresent(marketingFullGiftDetailVO);
                        }
                    }
                    for (DevanningGoodsInfoVO v:marketingGroupCard.getDevanningGoodsInfoVOList()){
                        //添加购物车营销快照
                        String key = RedisKeyConstants.FIRST_SNAPSHOT.concat(request.getCustomerId());
                        Map<String,Object> map = new HashMap<>();

                        GoodsMarketingVO goodsMarketing = new GoodsMarketingVO();
                        goodsMarketing.setMarketingId(marketingGroupCard.getMarketingVO().getMarketingId());
                        goodsMarketing.setGoodsInfoId(v.getGoodsInfoId());
                        goodsMarketing.setId("1");
                        goodsMarketing.setCustomerId(request.getCustomer().getCustomerId());
                        map.put(v.getGoodsInfoId(),goodsMarketing);
                        redisCache.setHashAll(key,map);
                        //查询商品区域限购
                        if (Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())  && StringUtils.isNotBlank(v.getSingleOrderAssignArea())) {
                            List<Long> allowedPurchaseAreaList = Arrays.stream(v.getAllowedPurchaseArea().split(","))
                                    .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                                continue;
                            }
                            if (StringUtils.isNotBlank(v.getSingleOrderAssignArea()) && Objects.nonNull(v.getSingleOrderPurchaseNum())){
                                List<Long> singleOrderAssignAreaList = Arrays.stream(v.getSingleOrderAssignArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(singleOrderAssignAreaList)&& (singleOrderAssignAreaList.contains(request.getProvinceId()) || singleOrderAssignAreaList.contains(request.getCityId()))){
                                    List<CustomerAreaLimitDetailVO> detailVOS = customerAreaLimitDetailProvider.listByCids(CustomerAreaLimitDetailRequest.builder().customerId(request.getCustomerId())
                                            .goodsInfoId(v.getGoodsInfoId()).regionIds(allowedPurchaseAreaList).build()).getContext().getDetailVOS();
                                    AtomicReference<BigDecimal> quyunum = new AtomicReference(BigDecimal.ZERO);
                                    if (CollectionUtils.isNotEmpty(detailVOS)){
                                        //过滤出已经退货或者已经取消的订单
                                        List<String> collect = detailVOS.stream().map(CustomerAreaLimitDetailVO::getTradeId).collect(Collectors.toList());
                                        //传入集合中生效的订单
                                        List<TradeVO> context = tradeQueryProvider.getOrderByIds(collect).getContext();
                                        List<String> collect1 = context.stream().map(TradeVO::getId).collect(Collectors.toList());
                                        detailVOS.forEach(q->{
                                            if (collect1.contains(q.getTradeId())){
                                                quyunum.set(quyunum.get().add(q.getNum()));
                                            }
                                        });
                                        v.setSingleUserPurchaseNum(BigDecimal.valueOf(v.getSingleOrderPurchaseNum()).subtract(quyunum.get()));
                                    }
                                }
                            }
                        }
                        //营销限购
                        if (Objects.nonNull(marketingGroupCard.getMarketingVO())){
                            if (Objects.nonNull(marketingGroupCard.getMarketingVO().getMarketingId())){
                                AtomicReference<BigDecimal> marketingunum = new AtomicReference<>(BigDecimal.ZERO);
                                //通过用户id查询当前商品的营销购买数量
                                Map<String,Object> req = new LinkedHashMap<>();
                                req.put("customerId",request.getCustomerId());
                                req.put("marketingId",marketingGroupCard.getMarketingVO().getMarketingId());
                                req.put("goodsInfoId",v.getGoodsInfoId());
                                List<MarketingPurchaseLimitVO> context1 = marketingPurchaseLimitProvider.queryListByParm(req).getContext();
                                List<MarketingPurchaseLimitVO> context = marketingPurchaseLimitProvider.queryListByParmNoUser(req).getContext();
                                if (CollectionUtils.isNotEmpty(context)){
                                    List<String> collect = context.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
                                    //获取生效订单
                                    List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
                                    List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
                                    context.forEach(q->{
                                        if (collect1.contains(q.getTradeId())){
                                            marketingunum.set(marketingunum.get().add(q.getNum()));
                                        }
                                    });
                                    v.setPurchaseNumOfMarketingScope(v.getPurchaseNumOfMarketingScope().subtract(marketingunum.get()));

                                }
                                if (CollectionUtils.isNotEmpty(context1)){
                                    List<String> collect = context1.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
                                    //获取生效订单
                                    List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
                                    List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
                                    context1.forEach(q->{
                                        if (collect1.contains(q.getTradeId())){
                                            marketingunum.set(marketingunum.get().add(q.getNum()));
                                        }
                                    });
                                    v.setPerPurchaseNumOfMarketingScope(v.getPerPurchaseNumOfMarketingScope().subtract(marketingunum.get()));
                                }
                            }
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(marketingGroupCardResponse.getNoHaveGoodsInfoVOList())){
                for (DevanningGoodsInfoVO v:marketingGroupCardResponse.getNoHaveGoodsInfoVOList()){
                    //查询商品区域限购
                    if (Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())  && StringUtils.isNotBlank(v.getSingleOrderAssignArea())) {
                        List<Long> allowedPurchaseAreaList = Arrays.stream(v.getAllowedPurchaseArea().split(","))
                                .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                            continue;
                        }
                        if (StringUtils.isNotBlank(v.getSingleOrderAssignArea()) && Objects.nonNull(v.getSingleOrderPurchaseNum())){
                            List<Long> singleOrderAssignAreaList = Arrays.stream(v.getSingleOrderAssignArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(singleOrderAssignAreaList)&& (singleOrderAssignAreaList.contains(request.getProvinceId()) || singleOrderAssignAreaList.contains(request.getCityId()))){
                                List<CustomerAreaLimitDetailVO> detailVOS = customerAreaLimitDetailProvider.listByCids(CustomerAreaLimitDetailRequest.builder().customerId(request.getCustomerId())
                                        .goodsInfoId(v.getGoodsInfoId()).regionIds(allowedPurchaseAreaList).build()).getContext().getDetailVOS();
                                AtomicReference<BigDecimal> quyunum = new AtomicReference(BigDecimal.ZERO);
                                if (CollectionUtils.isNotEmpty(detailVOS)){
                                    //过滤出已经退货或者已经取消的订单
                                    List<String> collect = detailVOS.stream().map(CustomerAreaLimitDetailVO::getTradeId).collect(Collectors.toList());
                                    //传入集合中生效的订单
                                    List<TradeVO> context = tradeQueryProvider.getOrderByIds(collect).getContext();
                                    List<String> collect1 = context.stream().map(TradeVO::getId).collect(Collectors.toList());
                                    detailVOS.forEach(q->{
                                        if (collect1.contains(q.getTradeId())){
                                            quyunum.set(quyunum.get().add(q.getNum()));
                                        }
                                    });
                                    v.setSingleUserPurchaseNum(BigDecimal.valueOf(v.getSingleOrderPurchaseNum()).subtract(quyunum.get()));
                                }
                            }
                        }
                    }
                }
            }

        //对营销及商品排序
        sortGoodsAndMarketing(marketingGroupCardResponse);
        MarketingGroupCardResponse convert = KsBeanUtil.convert(marketingGroupCardResponse, MarketingGroupCardResponse.class);
        if (CollectionUtils.isNotEmpty(purchaseResponse.getStores())){
            convert.setStoreId(purchaseResponse.getStores().stream().findAny().get().getStoreId());
        }
        return BaseResponse.success(convert);
    }

    /***
     * 设置库存及goodsstatus
     */
    private void setStockAndGoodsStatus(List<GoodsInfoVO> goodsInfoVOS, Long wareId){
        GoodsWareStockListResponse goodsWareStockByGoodsInfoIds = new GoodsWareStockListResponse();
        Map<String, BigDecimal> townStock = new HashMap<>();

        if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
            List<String> skuIds = goodsInfoVOS.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());

            //获取库存
            goodsWareStockByGoodsInfoIds = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoIds(
                    GoodsWareStockByGoodsForIdsRequest.builder()
                            .wareId(wareId)
                            .goodsForIdList(new ArrayList<>(skuIds))
                            .build()
            ).getContext();
            //乡镇件库存
            townStock = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoIdsjiya(GoodsWareStockByGoodsForIdsRequest.builder()
                    .wareId(wareId)
                    .goodsForIdList(new ArrayList<>(skuIds))
                    .build()
            ).getContext();
        }

        List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockByGoodsInfoIds.getGoodsWareStockVOList();
        //实际库存
        Map<String, BigDecimal> stockMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(goodsWareStockVOList)){
            stockMap = goodsWareStockVOList.stream().collect(Collectors.toMap(GoodsWareStockVO::getGoodsInfoId, GoodsWareStockVO::getStock, (a, b) -> b));
        }
        //乡镇件库存
        Map<String, BigDecimal> townStockMap = townStock;

        //活动相关
        if(CollectionUtils.isNotEmpty(goodsInfoVOS)){
            Map<String, BigDecimal> finalStockMap = stockMap;
            goodsInfoVOS.forEach(var ->{
                setValue(var, finalStockMap,townStockMap);
            });
        }
    }

    private void setValue(GoodsInfoVO goodsInfo,Map<String, BigDecimal> stockMap,Map<String, BigDecimal> townStockMap){
        //设置库存信息 扣除乡镇件
        BigDecimal stock = BigDecimal.ZERO;
        if(Objects.nonNull(stockMap.get(goodsInfo.getGoodsInfoId()))){
            stock = stockMap.get(goodsInfo.getGoodsInfoId());
            log.info("stock-----------"+stock);
        }
        if(Objects.nonNull(townStockMap.get(goodsInfo.getGoodsInfoId()))){
            BigDecimal orDefault = townStockMap.get(goodsInfo.getGoodsInfoId());
            stock = stock.subtract(orDefault);
            log.info("orDefault-----------orDefault"+orDefault);
            log.info("stock-----------orDefault"+stock);
        }
        goodsInfo.setStock(stock);
    }

    /**
     *
     * 对商品及活动排序
     */
    private void sortGoodsAndMarketing(com.wanmi.sbc.marketing.api.response.market.MarketingGroupCardResponse marketingGroupCardResponse){
        //未参与营销活动商品排序
        if (CollectionUtils.isNotEmpty(marketingGroupCardResponse.getNoHaveGoodsInfoVOList())){
            //根据购物车加入时间排序，加入时间已经在前面复制到 DevanningGoodsInfoVO
            List<DevanningGoodsInfoVO> collect = marketingGroupCardResponse.getNoHaveGoodsInfoVOList()
                    .stream().sorted(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime).reversed()).collect(Collectors.toList());
            marketingGroupCardResponse.setNoHaveGoodsInfoVOList(collect);
        }
        //根据商品加入购物车时间排序，商品排序将带动对应营销顺序，商品越晚加如营销顺序越靠前，商品也越靠前
        if (CollectionUtils.isNotEmpty(marketingGroupCardResponse.getMarketingGroupCards())){
            //根据营销分组后  根据购物车加入时间排序，加入时间已经在前面复制到 DevanningGoodsInfoVO
            List<MarketingGroupCard> marketingGroupCards = marketingGroupCardResponse.getMarketingGroupCards();
            //营销排序中间阶段
            List<MarketingGoodsSortResponse> haveMarketingGoodsSort = new ArrayList<>();
            marketingGroupCards.forEach(var->{
                if(CollectionUtils.isNotEmpty(var.getDevanningGoodsInfoVOList())){
                    log.info("购物车商品排序前----->"+ JSONObject.toJSONString(var.getDevanningGoodsInfoVOList()));

                    //将对应营销中的商品排序，后加入的在前面
                    List<DevanningGoodsInfoVO> collect =
                            var.getDevanningGoodsInfoVOList()
                                    .stream().sorted(Comparator.comparing(DevanningGoodsInfoVO::getCreateTime).reversed()).collect(Collectors.toList());
                    var.setDevanningGoodsInfoVOList(collect);

                    log.info("购物车商品排序后----->"+ JSONObject.toJSONString(var.getDevanningGoodsInfoVOList()));

                    MarketingGoodsSortResponse build = MarketingGoodsSortResponse.builder()
                            .devanningGoodsInfoVO(var.getDevanningGoodsInfoVOList().get(0))
                            .marketingVO(var.getMarketingVO())
                            .addTime(var.getDevanningGoodsInfoVOList().get(0).getCreateTime())
                            .build();
                    haveMarketingGoodsSort.add(build);
                }
            });
            //对营销活动按照商品加入购物车顺序排序
            if(CollectionUtils.isNotEmpty(haveMarketingGoodsSort)){
                List<MarketingGoodsSortResponse> collect =
                        haveMarketingGoodsSort.stream().sorted(
                                Comparator.comparing(MarketingGoodsSortResponse::getAddTime).reversed()
                        ).collect(Collectors.toList());

                Map<String, MarketingGroupCard> marketingGroupCardMap = new HashMap<>();
                marketingGroupCards.forEach(var->{
                    marketingGroupCardMap.put(
                            JSONObject.toJSONString(var.getMarketingVO()) +
                                    JSONObject.toJSONString(var.getDevanningGoodsInfoVOList().get(0))
                            ,var);
                });

                //最终顺序
                List<MarketingGroupCard> resultMarketingGroupCards = new ArrayList<>();
                collect.forEach(var->{
                    MarketingGroupCard marketingGroupCard = marketingGroupCardMap.get(JSONObject.toJSONString(var.getMarketingVO()) +
                            JSONObject.toJSONString(var.getDevanningGoodsInfoVO()));
                    resultMarketingGroupCards.add(marketingGroupCard);
                });
                marketingGroupCardResponse.setMarketingGroupCards(resultMarketingGroupCards);
            }
        }
    }


    /**
     * 限赠判断
     * @param marketingFullGiftDetailVO
     */
    private void limitPresent(MarketingFullGiftDetailVO marketingFullGiftDetailVO){
        if (Objects.nonNull(marketingFullGiftDetailVO)){
            String productId = marketingFullGiftDetailVO.getProductId();
            Long marketingId = marketingFullGiftDetailVO.getMarketingId();
            Long giftLevelId = marketingFullGiftDetailVO.getGiftLevelId();
            String key = marketingId.toString()+giftLevelId.toString()+productId;
            String o = redisService.getString(key);
            if (Objects.nonNull(o)){
                Long num = Long.parseLong(o);
                if (num.compareTo(0l)<=0){
                    marketingFullGiftDetailVO.getGiftGoodsInfoVO().setGoodsStatus(GoodsStatus.OUT_GIFTS_STOCK);
                } else if (num.compareTo(marketingFullGiftDetailVO.getProductNum())<0){
                    marketingFullGiftDetailVO.setProductNum(num);
                }
            }
        }
    }

    //设置商品状态 1.库存为0的 2.区域限购的 3失效的
    public void setGoodsStaues(List<GoodsInfoVO> goodsInfoVOS,PurchaseListRequest request){
        goodsInfoVOS.forEach(goodsInfoVO -> {
            if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())) {
                goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
                // 判断是否有T，如果是1，就设置为2
                if (goodsInfoVO.getGoodsInfoName().endsWith("T") || goodsInfoVO.getGoodsInfoName().endsWith("t")) {
                    if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) <= 0 && (Objects.isNull(goodsInfoVO.getMarketingId()) || goodsInfoVO.getPurchaseNum() == -1)) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                    }
                } else {
                    if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) <= 0 && (Objects.isNull(goodsInfoVO.getMarketingId()) || goodsInfoVO.getPurchaseNum() == -1)) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                    //根据指定区域销售地址和客户收货地址省、市对比判断商品状态
                    if (StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea()) && Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                    //定位区域限购
                    if (StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea()) && Objects.nonNull(request.getLocationProvinceId()) && Objects.nonNull(request.getLocationCityId())){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(request.getLocationCityId()) && !allowedPurchaseAreaList.contains(request.getLocationProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                }
            } else {
                goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
            }
        });
    }

    @Override
    public BaseResponse<PurchaseListResponse> newPurchaseInfo(@RequestBody @Valid PurchaseListNewRequest request) {


        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase purchaseInfo start");

        try {
            RetailShopCartRequest purchaseRequest = RetailShopCartRequest.builder()
                    .customerId(request.getCustomerId())
                    .inviteeId(request.getInviteeId())
                    .wareId(request.getWareId())
                    .matchWareHouseFlag(request.getMatchWareHouseFlag())
                    .build();
            if (Objects.nonNull(request.getCompanyInfoId())) {
                purchaseRequest.setCompanyInfoId(request.getCompanyInfoId());
            }
            PurchaseResponse purchaseResponse;
            // 需要分页增加入参
            if (request.getIsPage()) {
                purchaseRequest.setIsRefresh(request.getIsRefresh());
                purchaseRequest.setPageNum(request.getPageNum());
                purchaseRequest.setPageSize(request.getPageSize());
                purchaseResponse = retailShopCartService.pageList(purchaseRequest);
            } else {
                purchaseResponse = retailShopCartService.list(purchaseRequest);
            }
            sb.append(",shopCartService.list end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            List<String> checkGoodsInfoIds = purchaseResponse.getGoodsInfos().stream()
                    .filter(info -> info.getIsCheck().equals(DefaultFlag.YES))
                    .map(GoodsInfoVO::getGoodsInfoId)
                    .collect(Collectors.toList());
            if (checkGoodsInfoIds.size() > 0) {
                request.setGoodsInfoIds(checkGoodsInfoIds);
            }

            CustomerGetByIdRequest customerGetByIdRequest = new CustomerGetByIdRequest();
            customerGetByIdRequest.setCustomerId(request.getCustomerId());
            CustomerVO customer = customerQueryProvider.getCustomerById(customerGetByIdRequest).getContext();

            PurchaseListResponse response = KsBeanUtil.convert(purchaseResponse, PurchaseListResponse.class);
            if (CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
                //设置商品状态
                response.getGoodsInfos().forEach(goodsInfoVO -> {
                    if (Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
                        if (goodsInfoVO.getStock().compareTo(BigDecimal.ONE) < 0) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        }
                    } else {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                    }
                });

                //排除分销商品
                List<GoodsInfoVO> goodsInfoComList = response.getGoodsInfos();

                // 采购单商品编号，只包含有效的商品
                List<GoodsInfoVO> goodsInfos = goodsInfoComList.stream()
                        .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
                List<GoodsInfoVO> requestGoods = KsBeanUtil.convert(goodsInfos, GoodsInfoVO.class);
                requestGoods = requestGoods.stream().filter(param -> param.getGoodsInfoType() == 0).collect(Collectors.toList());
                // 不分页的时候
                if (!request.getIsPage()) {
                    // 获取采购营销信息及同步商品营销
                    PurchaseMarketingResponse purchasesMarketing = retailShopCartService.getPurchasesMarketingInit(request.getGoodsInfoIds(), requestGoods, customer, request.getWareId());

                    sb.append(",shopCartService.getPurchasesMarketing end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                    response.setGoodsMarketingMap(purchasesMarketing.getMap());
                    response.setSelfBuying(false);
                    //设置大客户价
                    if (null != customer.getEnterpriseStatusXyy() && customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED)) {
                        response.getGoodsInfos().stream().forEach(r -> r.setMarketPrice(
                                null != r.getVipPrice() && r.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? r.getVipPrice() : r.getMarketPrice()
                        ));
                    }
                    goodsInfos = response.getGoodsInfos().stream().map(goodsInfo ->
                            purchasesMarketing.getGoodsInfos().stream()
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
                    goodsInfos.stream().forEach(g -> {
                        if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                            if (Objects.nonNull(g.getSpecialPrice())) {
                                g.setMarketPrice(g.getSpecialPrice());
                            }
                        }
                        if (g.getAddedFlag() == 0 && DefaultFlag.YES.equals(g.getIsCheck())) {
                            g.setIsCheck(DefaultFlag.NO);
                        }
                    });

                    response.setGoodsInfos(goodsInfos);

                    response.setGoodsMarketings(purchasesMarketing.getGoodsMarketings());

                    // 获取店铺对应的营销信息
                    List<PurchaseMarketingCalcVO> giftMarketing = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(response.getGoodsMarketings())) {
                        response.setStoreMarketingMap(purchasesMarketing.getStoreMarketingMap());
                        //过滤已选营销
                        HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                        response.getStoreMarketingMap().forEach((k, v) -> {
                            List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p -> CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                            map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                            calcResponses.forEach(purchaseMarketingCalcVO -> {
                                if (Objects.nonNull(purchaseMarketingCalcVO.getFullGiftLevelList())) {
                                    giftMarketing.add(purchaseMarketingCalcVO);
                                }
                            });
                        });
                        response.setStoreMarketingMap(map);
                    } else {
                        response.setStoreMarketingMap(new HashMap<>());
                    }
                    //组装赠品信息
                    if (CollectionUtils.isNotEmpty(giftMarketing)) {
                        setGiftMarketingShop(giftMarketing, request.getMatchWareHouseFlag());
                        response.setGiftList(giftMarketing);
                    }

                    //排除选中的商品id中无效的商品id
                    List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().stream()
                            .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
                    request.setGoodsInfoIds(goodsInfoVOS.stream()
                            .filter(goodsInfo -> request.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                            .map(GoodsInfoVO::getGoodsInfoId)
                            .collect(Collectors.toList()));

                    sb.append(",shopCartService.calcAmount start time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                    response = retailShopCartService.calcAmount(KsBeanUtil.convert(response, PurchaseListResponse.class), customer, request.getGoodsInfoIds());

                    sb.append(",shopCartService.calcAmount end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();
                } else {
                    //设置大客户价
                    if (null != customer.getEnterpriseStatusXyy() && customer.getEnterpriseStatusXyy().equals(EnterpriseCheckState.CHECKED)) {
                        response.getGoodsInfos().stream().forEach(r -> r.setMarketPrice(
                                null != r.getVipPrice() && r.getVipPrice().compareTo(BigDecimal.ZERO) > 0 ? r.getVipPrice() : r.getMarketPrice()
                        ));
                    }

                    //特价判断，设置特价
                    response.getGoodsInfos().stream().forEach(g -> {
                        if (Objects.nonNull(g) && Objects.nonNull(g.getGoodsInfoType()) && 1 == g.getGoodsInfoType()) {
                            if (Objects.nonNull(g.getSpecialPrice())) {
                                g.setMarketPrice(g.getSpecialPrice());
                            }
                        }
                        if (g.getAddedFlag() == 0 && DefaultFlag.YES.equals(g.getIsCheck())) {
                            g.setIsCheck(DefaultFlag.NO);
                        }
                    });

                    // 初始化时获取采购营销信息及同步商品营销
                    if (request.getIsRefresh()) {
                        PurchaseMarketingResponse purchasesMarketing = retailShopCartService.getPurchasesMarketingInit(request.getGoodsInfoIds(), requestGoods, customer, request.getWareId());

                        sb.append(",shopCartService.getPurchasesMarketing end time=");
                        sb.append(System.currentTimeMillis() - sTm);
                        sTm = System.currentTimeMillis();

                        response.setGoodsMarketingMap(purchasesMarketing.getMap());
                        response.setSelfBuying(false);
                        goodsInfos = response.getGoodsInfos().stream().map(goodsInfo ->
                                purchasesMarketing.getGoodsInfos().stream()
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

                        response.setGoodsMarketings(purchasesMarketing.getGoodsMarketings());

                        // 获取店铺对应的营销信息
                        List<PurchaseMarketingCalcVO> giftMarketing = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(response.getGoodsMarketings())) {
                            response.setStoreMarketingMap(purchasesMarketing.getStoreMarketingMap());
                            //过滤已选营销
                            HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
                            response.getStoreMarketingMap().forEach((k, v) -> {
                                List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p -> CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                                map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                                calcResponses.forEach(purchaseMarketingCalcVO -> {
                                    if (Objects.nonNull(purchaseMarketingCalcVO.getFullGiftLevelList())) {
                                        giftMarketing.add(purchaseMarketingCalcVO);
                                    }
                                });
                            });
                            response.setStoreMarketingMap(map);
                        } else {
                            response.setStoreMarketingMap(new HashMap<>());
                        }
                        //组装赠品信息
                        if (CollectionUtils.isNotEmpty(giftMarketing)) {
                            setGiftMarketingShop(giftMarketing, request.getMatchWareHouseFlag());
                            response.setGiftList(giftMarketing);
                        }
                        response.setGoodsInfos(goodsInfos);
                    }

                    //排除选中的商品id中无效的商品id
                    List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().stream()
                            .filter(goodsInfo -> goodsInfo.getGoodsStatus() == GoodsStatus.OK).collect(Collectors.toList());
                    request.setGoodsInfoIds(goodsInfoVOS.stream()
                            .filter(goodsInfo -> request.getGoodsInfoIds().contains(goodsInfo.getGoodsInfoId()))
                            .map(GoodsInfoVO::getGoodsInfoId)
                            .collect(Collectors.toList()));

                    sb.append(",shopCartService.calcAmount start time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                    if (request.getIsRefresh()) {
                        response = retailShopCartService.calcAmount(KsBeanUtil.convert(response, PurchaseListResponse.class), customer, request.getGoodsInfoIds());
                    }

                    sb.append(",shopCartService.calcAmount end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                }
            }
            //根据goodsInfo中的stock顺序对goods重新排序
            if (CollectionUtils.isNotEmpty(response.getGoodsInfos())&&CollectionUtils.isNotEmpty(response.getGoodses())) {
                response.getGoodsInfos().sort((a, b) -> {
                    BigDecimal su = (a.getBuyCount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(a.getBuyCount())) .subtract (a.getStock() == null ? BigDecimal.ZERO : a.getStock());
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
//                        return Long.compare(su2, su);
                    }
                });
                List<GoodsVO> goodses = response.getGoodses();
                Map<String, GoodsVO> goodsVOMap = goodses.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
                List<GoodsVO> goodsSortList = new ArrayList<>(20);
                for (GoodsInfoVO inner : response.getGoodsInfos()) {
                    goodsSortList.add(goodsVOMap.get(inner.getGoodsId()));
                    if(goodsVOMap.get(inner.getGoodsId())!=null&&StringUtils.isNotBlank(goodsVOMap.get(inner.getGoodsId()).getGoodsSubtitle())){
                        inner.setGoodsSubtitle((goodsVOMap.get(inner.getGoodsId()).getGoodsSubtitle()));
                    }
                }
                response.setGoodses(goodsSortList);
            }
            if(request.getIsPage()) {
                Page<PurchaseVO> purchasePage = purchaseResponse.getPurchasePage();
                if (Objects.nonNull(purchasePage)) {
                    if (request.getIsRefresh()) {
                        Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
                        Map<String, GoodsInfoVO> goodsInfoVOMap = response.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity()));
                        List<GoodsVO> goodsVOS = purchaseResponse.getGoodsPageIds().stream()
                                .map(id -> goodsVOMap.get(id))
                                .distinct()
                                .collect(Collectors.toList());
                        List<GoodsInfoVO> goodsInfoVOS = purchaseResponse.getGoodsInfoPageIds().stream()
                                .map(id -> goodsInfoVOMap.get(id))
                                .collect(Collectors.toList());
                        PageImpl<GoodsVO> goodsPage = new PageImpl<>(goodsVOS, request.getPageable(), purchasePage.getTotalElements());
                        PageImpl<GoodsInfoVO> goodsInfoPage = new PageImpl<>(goodsInfoVOS, request.getPageable(), purchasePage.getTotalElements());
                        response.setGoodsPage(new MicroServicePage<>(goodsPage, request.getPageable()));
                        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoPage, request.getPageable()));
                        response.setGoodses(new ArrayList<>());
                        response.setGoodsInfos(new ArrayList<>());
                    } else {
                        PurchaseListResponse purchaseListResponse = new PurchaseListResponse();
                        PageImpl<GoodsVO> goodsPage = new PageImpl<>(response.getGoodses(), request.getPageable(), purchasePage.getTotalElements());
                        PageImpl<GoodsInfoVO> goodsInfoPage = new PageImpl<>(response.getGoodsInfos(), request.getPageable(), purchasePage.getTotalElements());
                        purchaseListResponse.setGoodsPage(new MicroServicePage<>(goodsPage, request.getPageable()));
                        purchaseListResponse.setGoodsInfoPage(new MicroServicePage<>(goodsInfoPage, request.getPageable()));
                        response = purchaseListResponse;
                    }
                }
            }
            return BaseResponse.success(response);
        }finally {
            log.info(sb.toString());
        }

    }



    /**
     * 获取商品店铺营销
     * @param request
     * @return
     */
    public BaseResponse<PurchaseStoreMarketingResponse> getStoreMarketings(@RequestBody @Valid PurchaseStoreMarketingRequest request) {
        final List<GoodsInfoVO> goodsInfos = request.getGoodsInfos();
        // 处理选择的商品
        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase getStoreMarketings start");
        try {
            RetailShopCartRequest purchaseRequest = RetailShopCartRequest.builder()
                    .customerId(request.getCustomer().getCustomerId())
                    .inviteeId(request.getInviteeId())
                    .wareId(request.getWareId())
                    .matchWareHouseFlag(request.getMatchWareHouseFlag())
                    .build();
            if (Objects.nonNull(request.getCompanyInfoId())) {
                purchaseRequest.setCompanyInfoId(request.getCompanyInfoId());
            }
            if (StringUtils.isBlank(request.getInviteeId())) {
                purchaseRequest.setInviteeId("0");
            }

            sb.append(",updateIsCheck start time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            retailShopCartService.updateIsCheck(purchaseRequest, request.getGoodsInfoIdList());

            sb.append(",shopCartService.updateIsCheck end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            if (Objects.isNull(request.getGoodsInfoIdList()) || request.getGoodsInfoIdList().size() == 0) {
                return BaseResponse.success(new PurchaseStoreMarketingResponse());
            }
            List<GoodsInfoVO> reqGoodsInfoFilter = retailGoodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder()
                    .goodsInfoIds(request.getGoodsInfoIdList())
                    .wareId(request.getWareId())
                    .build()).getContext().getGoodsInfos();

            sb.append(",goodsInfoQueryProvider.listViewByIds end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();
            //特价商品将市场价设置为特价
            reqGoodsInfoFilter.forEach(param -> {
                if (param.getGoodsInfoType() == 1) {
                    param.setMarketPrice(param.getSpecialPrice());
                }
            });

            List<String> reqGoodsInfoIds = reqGoodsInfoFilter.stream().map(GoodsInfoVO::getGoodsInfoId)
                    .collect(Collectors.toList());

//            Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketing = shopCartService.getStoreMarketingBase(request.getGoodsMarketingDTOList(), request.getCustomer(),
//                    new PurchaseFrontRequest(), reqGoodsInfoIds, request.getWareId(), reqGoodsInfoFilter);
            Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketing = retailShopCartService.getStoreMarketingBaseCopy(request.getGoodsMarketingDTOList(), request.getCustomer(),
                    new PurchaseFrontRequest(), reqGoodsInfoIds, request.getWareId(), reqGoodsInfoFilter,goodsInfos,request);
            if(Objects.isNull(storeMarketing)){
                request.setGoodsMarketingMap(new HashMap<>());
                storeMarketing = new HashMap<>();
            }
            log.info("================================= 删除后的sku信息：{}==========================",new ArrayList(request.getGoodsMarketingMap().keySet()));

//            List<Long> removeIds = new ArrayList<>();
//            storeMarketing.forEach((id,marketing) ->{
//                //满折需要干掉的
//                List<MarketingFullDiscountLevelVO> removeFullDiscountLevelList = new ArrayList<>();
//                //满减需要干掉的
//                List<MarketingFullReductionLevelVO> removeFullReductionLevelList = new ArrayList<>();
//                //满赠需要干掉的
//                List<MarketingFullGiftLevelVO> removeFullGiftLevelList = new ArrayList<>();
//                marketing.stream().forEach(purchaseMarketingViewCalcVO -> {
//                    //满折
//                    List<MarketingFullDiscountLevelVO> fullDiscountLevelList = purchaseMarketingViewCalcVO.getFullDiscountLevelList();
//                    if(CollectionUtils.isNotEmpty(fullDiscountLevelList)){
//                        this.filterMarketingFullDiscountLevelList(goodsInfos,removeFullDiscountLevelList,fullDiscountLevelList);
//                        if(CollectionUtils.isEmpty(fullDiscountLevelList)){
//                            purchaseMarketingViewCalcVO.setFullDiscountLevel(new MarketingFullDiscountLevelVO());
//                        }
//                    }
//                    //满减
//                    List<MarketingFullReductionLevelVO> fullReductionLevelList = purchaseMarketingViewCalcVO.getFullReductionLevelList();
//                    if(CollectionUtils.isNotEmpty(fullReductionLevelList)){
//                        this.filterMarketingFullReductionLevelList(goodsInfos,removeFullReductionLevelList,fullReductionLevelList);
//                        if(CollectionUtils.isEmpty(fullReductionLevelList)){
//                            purchaseMarketingViewCalcVO.setFullReductionLevel(new MarketingFullReductionLevelVO());
//                        }
//                    }
//                    //满赠
//                    List<MarketingFullGiftLevelVO> fullGiftLevelList = purchaseMarketingViewCalcVO.getFullGiftLevelList();
//                    if(CollectionUtils.isNotEmpty(fullGiftLevelList)){
//                        this.filterMarketingFullGiftLevelList(goodsInfos,removeFullGiftLevelList,fullGiftLevelList);
//                        if(CollectionUtils.isEmpty(fullGiftLevelList)){
//                            purchaseMarketingViewCalcVO.setFullGiftLevel(new MarketingFullGiftLevelVO());
//                        }
//                    }
//                    //营销活动为空
//                    if(CollectionUtils.isEmpty(fullDiscountLevelList) && CollectionUtils.isEmpty(fullReductionLevelList) && CollectionUtils.isEmpty(fullGiftLevelList)){
//                        if(!removeIds.contains(id)){
//                            removeIds.add(id);
//                        }
//                    }
//                });
//            });
//            if(CollectionUtils.isNotEmpty(removeIds)){
//                request.setGoodsMarketingMap(null);
//                for (Long removeId : removeIds) {
//                    storeMarketing.remove(removeId);
//                }
//            }
            sb.append(",shopCartService.getStoreMarketingBase end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
            PurchaseStoreMarketingResponse response = new PurchaseStoreMarketingResponse();
            storeMarketing.forEach((k, v) -> {
                map.put(k, KsBeanUtil.convertList(v, PurchaseMarketingCalcVO.class));
            });

            // 获取采购营销信息及同步商品营销
//        PurchaseMarketingResponse purchasesMarketing = shopCartService.getPurchasesMarketing(request.getGoodsInfoIdList(), reqGoodsInfo, request.getCustomer(), request.getWareId());
            /*response.setGoodsMarketingMap(new HashMap<>());*/

            PurchaseListResponse purchaseListResponse = new PurchaseListResponse();
            purchaseListResponse.setStoreMarketingMap(map);
            purchaseListResponse.setGoodsMarketingMap(request.getGoodsMarketingMap());
            purchaseListResponse.setGoodsInfos(request.getGoodsInfos());
            purchaseListResponse.setGoodsIntervalPrices(request.getGoodsIntervalPrices());
            // 计算价格
            purchaseListResponse = retailShopCartService.calcAmount(purchaseListResponse, request.getCustomer(), request.getGoodsInfoIdList());

            sb.append(",shopCartService.calcAmount end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            Map<Long, List<PurchaseMarketingViewCalcVO>> storeMarketingMap = new HashedMap();
            List<PurchaseMarketingViewCalcVO> giftMarketing=new ArrayList<>();
            map.entrySet().stream().forEach(set -> {
                List<PurchaseMarketingViewCalcVO> viewCalcVOS = new ArrayList<>();
                for (PurchaseMarketingCalcVO calcVO : set.getValue()) {
                    PurchaseMarketingViewCalcVO purchaseMarketingViewCalcVO = new PurchaseMarketingViewCalcVO();
                    KsBeanUtil.copyPropertiesThird(calcVO, purchaseMarketingViewCalcVO);
                    if (Objects.nonNull(calcVO.getFullGiftLevel())) {
                        MarketingFullGiftLevelViewVO marketingFullGiftLevelViewVO = new MarketingFullGiftLevelViewVO();
                        KsBeanUtil.copyPropertiesThird(calcVO.getFullGiftLevel(), marketingFullGiftLevelViewVO);
                        purchaseMarketingViewCalcVO.setFullGiftLevel(marketingFullGiftLevelViewVO);
                        giftMarketing.add(purchaseMarketingViewCalcVO);
                    }
                    if(CollectionUtils.isNotEmpty(calcVO.getFullGiftLevelList())){
                        purchaseMarketingViewCalcVO.setFullGiftLevelList(calcVO.getFullGiftLevelList());
                    }
                    if(CollectionUtils.isNotEmpty(calcVO.getFullReductionLevelList())){
                        purchaseMarketingViewCalcVO.setFullReductionLevelList(calcVO.getFullReductionLevelList());
                    }
                    if(CollectionUtils.isNotEmpty(calcVO.getFullDiscountLevelList())){
                        purchaseMarketingViewCalcVO.setFullDiscountLevelList(calcVO.getFullDiscountLevelList());
                    }
                    viewCalcVOS.add(purchaseMarketingViewCalcVO);
                }
                storeMarketingMap.put(set.getKey(), viewCalcVOS);
            });

            sb.append(",map.entrySet.forEach end time=");
            sb.append(System.currentTimeMillis()-sTm);
            sTm = System.currentTimeMillis();

            if(CollectionUtils.isNotEmpty(giftMarketing)){
                setGiftMarketing(giftMarketing,request.getMatchWareHouseFlag());

            }
            sb.append(",setGiftMarketing end time=");
            sb.append(System.currentTimeMillis()-sTm);

            response.setStoreMarketingMap(storeMarketingMap);
            response.setTotalPrice(purchaseListResponse.getTotalPrice());
            response.setTradePrice(purchaseListResponse.getTradePrice());
            response.setDiscountPrice(purchaseListResponse.getDiscountPrice());
            response.setDistributeCommission(purchaseListResponse.getDistributeCommission());
            response.setGiftList(giftMarketing);

//            response.getStoreMarketingMap().forEach((id,marketing) ->{
//                //满折需要干掉的
//                List<MarketingFullDiscountLevelVO> removeFullDiscountLevelList = new ArrayList<>();
//                //满减需要干掉的
//                List<MarketingFullReductionLevelVO> removeFullReductionLevelList = new ArrayList<>();
//                //满赠需要干掉的
//                List<MarketingFullGiftLevelVO> removeFullGiftLevelList = new ArrayList<>();
//                marketing.stream().forEach(purchaseMarketingViewCalcVO -> {
//                    //满折
//                    List<MarketingFullDiscountLevelVO> fullDiscountLevelList = purchaseMarketingViewCalcVO.getFullDiscountLevelList();
//                    this.filterMarketingFullDiscountLevelList(goodsInfos,removeFullDiscountLevelList,fullDiscountLevelList);
//                    if(CollectionUtils.isEmpty(fullDiscountLevelList)){
//                        purchaseMarketingViewCalcVO.setFullDiscountLevel(new MarketingFullDiscountLevelVO());
//                    }
//                    //满减
//                    List<MarketingFullReductionLevelVO> fullReductionLevelList = purchaseMarketingViewCalcVO.getFullReductionLevelList();
//                    this.filterMarketingFullReductionLevelList(goodsInfos,removeFullReductionLevelList,fullReductionLevelList);
//                    if(CollectionUtils.isEmpty(fullReductionLevelList)){
//                        purchaseMarketingViewCalcVO.setFullReductionLevel(new MarketingFullReductionLevelVO());
//                    }
//                    //满赠
//                    List<MarketingFullGiftLevelVO> fullGiftLevelList = purchaseMarketingViewCalcVO.getFullGiftLevelList();
//                    this.filterMarketingFullGiftLevelList(goodsInfos,removeFullGiftLevelList,fullGiftLevelList);
//                    if(CollectionUtils.isEmpty(fullGiftLevelList)){
//                        purchaseMarketingViewCalcVO.setFullGiftLevel(new MarketingFullGiftLevelViewVO());
//                    }
//                });
//            });
            return BaseResponse.success(response);
        }finally {
         log.info(sb.toString());
        }
    }

    public void filterMarketing(Map<String, List<MarketingViewVO>> marketingMap,Map<String, List<MarketingViewVO>> marketingMapCount,List<GoodsInfoVO> goodsInfoList){
        //查询所有商品的囤货信息
        List<PilePurchaseAction> purchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfoList.stream().map(GoodsInfoVO::getGoodsInfoId)
                .distinct().collect(Collectors.toList()));
        //过滤掉已经买完限购商品
        marketingMapCount.forEach((sku,marketing) -> {
            List<MarketingViewVO> marketingViewVOS = new ArrayList<>();
            marketing.stream().forEach(marketingViewVO -> {
                List<MarketingScopeVO> marketingScopeList = marketingViewVO.getMarketingScopeList();
                for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
                    Long purchaseNum = marketingScopeVO.getPurchaseNum();
                    if(Objects.nonNull(purchaseNum)){
                        if(sku.equals(marketingScopeVO.getScopeId())){
                            //查询出此商品的囤货总量
                            Long sum = purchaseActions.stream().filter(pilePurchaseAction -> pilePurchaseAction.getGoodsInfoId().equals(sku)).collect(Collectors.toList()).stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                            //商品购买量
                            Long buyCount = goodsInfoList.stream().filter(goodsInfoDTO -> goodsInfoDTO.getGoodsInfoId().equals(sku)).collect(Collectors.toList()).stream().findFirst().get().getBuyCount();
                            Long count = sum + buyCount;
                            //当购买量+已购买量 > 限购数量则取消该条营销信息
                            if(count > purchaseNum){
                                marketingViewVOS.add(marketingViewVO);
                            }
                        }
                    }
                }
            });
            if(CollectionUtils.isNotEmpty(marketingViewVOS)){
                marketing.removeAll(marketingViewVOS);
            }
            if(CollectionUtils.isNotEmpty(marketing)){
                marketingMap.put(sku,marketing);
            }
        });
    }

    /**
     * 过滤满折营销
     */
    public void filterMarketingFullDiscountLevelList(List<GoodsInfoVO> goodsInfos,List<MarketingFullDiscountLevelVO> removeFullDiscountLevelList,List<MarketingFullDiscountLevelVO> fullDiscountLevelList){
        log.info("=========================== 开始过滤限购满折营销信息 ====================================");
        if(CollectionUtils.isEmpty(fullDiscountLevelList)){
            log.info("=========================== 满折营销信息为空 ====================================");
            return;
        }
        List<PilePurchaseAction> pilePurchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));
        log.info("=========================== 满折营销囤货数据：{} ====================================",pilePurchaseActions);
        fullDiscountLevelList.stream().forEach(fullDiscountLevel ->{
            MarketingGetByIdRequest MarketingRequest = new MarketingGetByIdRequest();
            MarketingRequest.setMarketingId(fullDiscountLevel.getMarketingId());
            MarketingVO marketingVO = marketingQueryProvider.getById(MarketingRequest).getContext().getMarketingVO();
            List<MarketingScopeVO> marketingScopeList = marketingVO.getMarketingScopeList();
            for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
                Long purchaseNum = marketingScopeVO.getPurchaseNum();
                //如果限制了
                if(Objects.nonNull(purchaseNum)){
                    String sku = marketingScopeVO.getScopeId();
                    List<PilePurchaseAction> purchaseActions = pilePurchaseActions.stream().filter((p) -> p.getGoodsId().equals(sku)).collect(Collectors.toList());
                    //已囤货数量
                    long sum = purchaseActions.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                    List<GoodsInfoVO> goodsInfoVOS = goodsInfos.stream().filter(g -> g.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
                    //购买数量
                    long buyCount = goodsInfoVOS.stream().mapToLong(GoodsInfoVO::getBuyCount).sum();
                    //总数量
                    Long count = sum + buyCount;
                    if(count > purchaseNum){
                        removeFullDiscountLevelList.add(fullDiscountLevel);
                    }
                }
            }
        });
        if(CollectionUtils.isNotEmpty(removeFullDiscountLevelList)){
            fullDiscountLevelList.removeAll(removeFullDiscountLevelList);
        }
    }

    /**
     * 过滤满减营销
     */
    public void filterMarketingFullReductionLevelList(List<GoodsInfoVO> goodsInfos,List<MarketingFullReductionLevelVO> removeFullReductionLevelList,List<MarketingFullReductionLevelVO> fullReductionLevelList){
        log.info("=========================== 开始过滤限购满减营销信息 ====================================");
        if(CollectionUtils.isEmpty(fullReductionLevelList)){
            log.info("=========================== 满减营销信息为空 ====================================");
            return;
        }
        List<PilePurchaseAction> pilePurchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));
        log.info("=========================== 满减营销信囤货数据：{} ====================================",pilePurchaseActions);
        fullReductionLevelList.stream().forEach(fullDiscountLevel ->{
            MarketingGetByIdRequest MarketingRequest = new MarketingGetByIdRequest();
            MarketingRequest.setMarketingId(fullDiscountLevel.getMarketingId());
            MarketingVO marketingVO = marketingQueryProvider.getById(MarketingRequest).getContext().getMarketingVO();
            List<MarketingScopeVO> marketingScopeList = marketingVO.getMarketingScopeList();
            for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
                Long purchaseNum = marketingScopeVO.getPurchaseNum();
                //如果限制了
                if(Objects.nonNull(purchaseNum)){
                    String sku = marketingScopeVO.getScopeId();
                    List<PilePurchaseAction> purchaseActions = pilePurchaseActions.stream().filter((p) -> p.getGoodsId().equals(sku)).collect(Collectors.toList());
                    //已囤货数量
                    long sum = purchaseActions.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                    List<GoodsInfoVO> goodsInfoVOS = goodsInfos.stream().filter(g -> g.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
                    //购买数量
                    long buyCount = goodsInfoVOS.stream().mapToLong(GoodsInfoVO::getBuyCount).sum();
                    //总数量
                    Long count = sum + buyCount;
                    if(count > purchaseNum){
                        removeFullReductionLevelList.add(fullDiscountLevel);
                    }
                }
            }
        });
        if(CollectionUtils.isNotEmpty(removeFullReductionLevelList)){
            fullReductionLevelList.removeAll(removeFullReductionLevelList);
        }
    }

    /**
     * 过滤满赠营销
     */
    public void filterMarketingFullGiftLevelList(List<GoodsInfoVO> goodsInfos,List<MarketingFullGiftLevelVO> removeFullGiftLevelList,List<MarketingFullGiftLevelVO> fullGiftLevelList){
        log.info("=========================== 开始过滤限购满赠营销信息 ====================================");
        if(CollectionUtils.isEmpty(fullGiftLevelList)){
            log.info("=========================== 满赠营销信息为空 ====================================");
            return;
        }
        List<PilePurchaseAction> pilePurchaseActions = pilePurchaseActionRepository.queryPilePurchaseActionInGoodsInfoId(goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));
        log.info("=========================== 满赠营销囤货数据：{} ====================================",pilePurchaseActions);
        fullGiftLevelList.stream().forEach(fullDiscountLevel ->{
            MarketingGetByIdRequest MarketingRequest = new MarketingGetByIdRequest();
            MarketingRequest.setMarketingId(fullDiscountLevel.getMarketingId());
            MarketingVO marketingVO = marketingQueryProvider.getById(MarketingRequest).getContext().getMarketingVO();
            List<MarketingScopeVO> marketingScopeList = marketingVO.getMarketingScopeList();
            for (MarketingScopeVO marketingScopeVO : marketingScopeList) {
                Long purchaseNum = marketingScopeVO.getPurchaseNum();
                //如果限制了
                if(Objects.nonNull(purchaseNum)){
                    String sku = marketingScopeVO.getScopeId();
                    List<PilePurchaseAction> purchaseActions = pilePurchaseActions.stream().filter((p) -> p.getGoodsId().equals(sku)).collect(Collectors.toList());
                    //已囤货数量
                    long sum = purchaseActions.stream().mapToLong(PilePurchaseAction::getGoodsNum).sum();
                    List<GoodsInfoVO> goodsInfoVOS = goodsInfos.stream().filter(g -> g.getGoodsInfoId().equals(sku)).collect(Collectors.toList());
                    //购买数量
                    long buyCount = goodsInfoVOS.stream().mapToLong(GoodsInfoVO::getBuyCount).sum();
                    //总数量
                    Long count = sum + buyCount;
                    if(count > purchaseNum){
                        removeFullGiftLevelList.add(fullDiscountLevel);
                    }
                }
            }
        });
        if(CollectionUtils.isNotEmpty(removeFullGiftLevelList)){
            fullGiftLevelList.removeAll(removeFullGiftLevelList);
        }
    }

    @Override
    public BaseResponse<PurchaseOrderMarketingResponse> getOrderMarketings(@RequestBody @Valid PurchaseOrderMarketingRequest request) {
        Long totalCount=request.getGoodsTotalNum();
        BigDecimal totalPrice  =request.getTotalPrice();
        List<String> goodsInfoIds = new ArrayList<>();
        goodsInfoIds.add(Constant.FULL_GIT_ORDER_GOODS);
        Map<String, List<MarketingVO>> listMap = marketingQueryProvider.getOrderMarketingMap(MarketingMapGetByGoodsIdRequest.builder()
                .goodsInfoIdList(goodsInfoIds)
                .deleteFlag(DeleteFlag.NO)
                .cascadeLevel(true)
                .marketingStatus(MarketingStatus.STARTED)
                .build()).getContext().getListMap();
        if (Objects.nonNull(listMap)) {
            List<MarketingVO> marketingViewVOS = listMap.get(Constant.FULL_GIT_ORDER_GOODS);
            if(CollectionUtils.isEmpty(marketingViewVOS)){
                return BaseResponse.success(null);
            }
            MarketingVO orderByMarketingVO = marketingViewVOS.stream().findFirst().orElse(null);
            if (Objects.nonNull(orderByMarketingVO)) {
                if (MarketingType.DISCOUNT.equals(orderByMarketingVO.getMarketingType())) {
                    MarketingFullDiscountLevelVO discountLevelVO = new MarketingFullDiscountLevelVO();
                    List<MarketingFullDiscountLevelVO> levelVOList = marketingFullDiscountQueryProvider.listByMarketingId
                            (new MarketingFullDiscountByMarketingIdRequest(orderByMarketingVO.getMarketingId())).getContext().getMarketingFullDiscountLevelVOList();
                    if (CollectionUtils.isNotEmpty(levelVOList)) {
                        for (MarketingFullDiscountLevelVO level : levelVOList) {
                            if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                    (Objects.isNull(discountLevelVO.getFullCount()) ||
                                            discountLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                discountLevelVO = level;
                            }
                        }
                        // 满足条件的最大的等级
                        if (Objects.nonNull(discountLevelVO.getFullCount())) {
                            BigDecimal amount = totalPrice.multiply(discountLevelVO.getDiscount()).setScale(2, BigDecimal.ROUND_DOWN);
                            return BaseResponse.success(PurchaseOrderMarketingResponse.builder().totalPrice(amount).subType(orderByMarketingVO.getSubType()).discountsTotalOrderPrice(totalPrice.subtract(amount)).build());
                        }
                    }
                } else if (MarketingType.REDUCTION.equals(orderByMarketingVO.getMarketingType())) {
                    List<MarketingFullReductionLevelVO> levelVOList = marketingFullReductionQueryProvider.listByMarketingId
                            (new MarketingFullReductionByMarketingIdRequest(orderByMarketingVO.getMarketingId())).getContext().getMarketingFullReductionLevelVOList();
                    if (CollectionUtils.isNotEmpty(levelVOList)) {
                        MarketingFullReductionLevelVO marketingFullReductionLevelVO = new MarketingFullReductionLevelVO();
                        for (MarketingFullReductionLevelVO level : levelVOList) {
                            if (level.getFullCount().compareTo(totalCount) <= 0 &&
                                    (Objects.isNull(marketingFullReductionLevelVO.getFullCount()) ||
                                            marketingFullReductionLevelVO.getFullCount().compareTo(level.getFullCount()) < 0)) {
                                marketingFullReductionLevelVO = level;
                            }
                        }
                        // 满足条件的最大的等级
                        if (Objects.nonNull(marketingFullReductionLevelVO.getFullCount())) {
                            BigDecimal reduction = marketingFullReductionLevelVO.getReduction();
                            // 若可叠加，计算优惠金额
                            if (BoolFlag.YES.equals(orderByMarketingVO.getIsOverlap()) ) {
                                reduction = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(marketingFullReductionLevelVO.getFullCount()), 10, BigDecimal.ROUND_DOWN).multiply(marketingFullReductionLevelVO.getReduction()).setScale(2, BigDecimal.ROUND_DOWN);
                            }
                            if (reduction.compareTo(totalPrice) > 0) {
                                reduction = totalPrice;
                            }
                            return BaseResponse.success(PurchaseOrderMarketingResponse.builder().totalPrice(totalPrice.subtract(reduction)).subType(orderByMarketingVO.getSubType()).discountsTotalOrderPrice(reduction).build());
                        }
                    }
                }
            }
        }
        return BaseResponse.success(null);
    }

    /**
     * 分销商品去除阶梯价等信息
     *
     * @param goodsInfoVOList
     */
    private List<GoodsIntervalPriceVO> setDistributorPrice(List<GoodsInfoVO> goodsInfoVOList,
                                                           List<GoodsIntervalPriceVO> goodsIntervalPrices) {
        //        分销商品
        List<GoodsInfoVO> goodsInfoDistributionList = goodsInfoVOList.stream().filter(goodsInfo -> Objects.equals
                (goodsInfo.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED)).collect(Collectors.toList());
        List<String> skuIdList = goodsInfoDistributionList.stream().map(GoodsInfoVO::getGoodsInfoId).collect
                (Collectors.toList());

        goodsInfoVOList.forEach(goodsInfo -> {
            if (skuIdList.contains(goodsInfo.getGoodsInfoId())) {
                goodsInfo.setIntervalPriceIds(null);
                goodsInfo.setIntervalMinPrice(null);
                goodsInfo.setIntervalMaxPrice(null);
                goodsInfo.setCount(null);
                goodsInfo.setMaxCount(null);
            }
        });
        return goodsIntervalPrices.stream().filter(intervalPrice -> !skuIdList.contains(intervalPrice.getGoodsInfoId
                ())).collect(Collectors.toList());
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 查询补充赠品信息
     * @Return: void
     * @Author: yxb
     * @Date: 2021/2/2 19:50
     */
    private void setGiftMarketing (List<PurchaseMarketingViewCalcVO> giftMarketing ,Boolean matchWareHouseFlag) {
        if (CollectionUtils.isNotEmpty(giftMarketing)) {
            Set<String> giftSkus = new HashSet<>();
            for (PurchaseMarketingViewCalcVO inner : giftMarketing) {
                List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                if(CollectionUtils.isNotEmpty(fullGiftLevelList)){
                    for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                        List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                        for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                            giftSkus.add(gift.getProductId());
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(giftSkus)) {
                List<GiftGoodsInfoVO> goodsInfos = retailGoodsInfoQueryProvider.findGoodsInfoByIds(GoodsInfoListByIdsRequest.builder()
                        .goodsInfoIds(new ArrayList<>(giftSkus)).matchWareHouseFlag(matchWareHouseFlag).build()).getContext().getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                    Map<String, GiftGoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GiftGoodsInfoVO::getGoodsInfoId, goods -> goods));
                    for (PurchaseMarketingViewCalcVO inner : giftMarketing) {
                        List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                        for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                            List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                            for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                                if (Objects.nonNull(goodsInfoVOMap.get(gift.getProductId()))) {
                                    gift.setGiftGoodsInfoVO(goodsInfoVOMap.get(gift.getProductId()));
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: 查询补充赠品信息
     * @Return: void
     * @Author: yxb
     * @Date: 2021/2/2 19:50
     */
    private void setGiftMarketingShop (List<PurchaseMarketingCalcVO> giftMarketing ,Boolean matchWareHouseFlag) {
        if (CollectionUtils.isNotEmpty(giftMarketing)) {
            Set<String> giftSkus = new HashSet<>();
            for (PurchaseMarketingCalcVO inner : giftMarketing) {
                List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                    List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                    for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                        giftSkus.add(gift.getProductId());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(giftSkus)) {
                List<GiftGoodsInfoVO> goodsInfos = retailGoodsInfoQueryProvider.findGoodsInfoByIds(GoodsInfoListByIdsRequest.builder()
                        .goodsInfoIds(new ArrayList<>(giftSkus)).matchWareHouseFlag(matchWareHouseFlag).build()).getContext().getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                    Map<String, GiftGoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GiftGoodsInfoVO::getGoodsInfoId, goods -> goods));
                    for (PurchaseMarketingCalcVO inner : giftMarketing) {
                        List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                        for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                            List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                            for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                                if (Objects.nonNull(goodsInfoVOMap.get(gift.getProductId()))) {
                                    gift.setGiftGoodsInfoVO(goodsInfoVOMap.get(gift.getProductId()));
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * 查询我的囤货
     * @return
     */
    @Override
    public BaseResponse<PilePurchaseResponse> queryMyPilePurchaseList(@RequestBody @Valid PilePurchaseRequest request){
        com.wanmi.sbc.returnorder.pilepurchase.request.PilePurchaseRequest purchaseRequest = KsBeanUtil.convert(request, com.wanmi.sbc.returnorder.pilepurchase.request.PilePurchaseRequest.class);
        PurchaseResponse response = pilePurchaseService.pageList(purchaseRequest);
        if(Objects.isNull(response) || Objects.isNull(response.getPurchasePage())){
            return BaseResponse.success(new PilePurchaseResponse());
        }
        PilePurchaseResponse purchaseResponse = new PilePurchaseResponse();
        purchaseResponse.setGoodsInfos(new MicroServicePage<GoodsInfoVO>(response.getGoodsInfos(),
                                                                         request.getPageRequest(),
                                                                         response.getPurchasePage().getTotalElements()));
        purchaseResponse.setGoodsTotalCount(response.getGoodsTotalCount());
        purchaseResponse.setTotalPrice(response.getTotalPrice());
        return BaseResponse.success(purchaseResponse);
    }

    /**
     * 查询客户囤货
     * @param request
     * @return
     */
    @Override
    public BaseResponse<PurchaseQueryResponse> pileQuery(@RequestBody @Valid PurchaseQueryRequest request) {
        List<PilePurchase> purchaseList = pilePurchaseService.queryPurchase(request.getCustomerId(), request.getGoodsInfoIds
                (), request.getInviteeId());
        PurchaseQueryResponse purchaseQueryResponse = new PurchaseQueryResponse();
        purchaseQueryResponse.setPurchaseList(KsBeanUtil.convertList(purchaseList, PurchaseVO.class));
        return BaseResponse.success(purchaseQueryResponse);
    }

    /**
     * 查询客户某个商品囤货数量
     * @param request
     * @return
     */
    @Override
    public BaseResponse<Long> getGoodsNumByCustomerIdAndSkuId(PurchaseQueryRequest request) {
        return BaseResponse.success(pilePurchaseService.getGoodsNumByCustomerIdAndSkuId(request));
    }

    /**
     * 根据客户id查询商品囤货总数量
     * @param request
     * @return
     */
    @Override
    public BaseResponse<Long> getPileCountNumByCustomerId(PurchaseQueryRequest request) {
        return BaseResponse.success(pilePurchaseService.getPileCountNumByCustomerId(request));
    }

    @Override
    public BaseResponse<PurchaseCountGoodsResponse> calShopCartAndPurchaseNum(PurchaseCountGoodsRequest request) {
        //查询（正常）购物车数量
//        if(request.getSaasStatus()!=null
//                && request.getSaasStatus()){
//            total = shopCartService.countGoodsByCompanyInfoId(request.getCustomerId(), request.getInviteeId(),request.getCompanyInfoId());
//        }
        Long shopCartTotal = retailShopCartService.queryGoodsNum(request.getCustomerId(), request.getInviteeId());
        //查询囤货购物车数量
//        if(request.getSaasStatus()!=null
//                && request.getSaasStatus()){
//            total += purchaseService.countGoodsByCompanyInfoId(request.getCustomerId(), request.getInviteeId(),request.getCompanyInfoId());
//        }
//        Long pileTotal = purchaseService.queryGoodsNum(request.getCustomerId(), request.getInviteeId());

        Integer shopCartTotalInt = Objects.isNull(shopCartTotal) ? NumberUtils.INTEGER_ZERO : shopCartTotal.intValue();
//        Integer pileTotalInt = Objects.isNull(pileTotal) ? NumberUtils.INTEGER_ZERO : pileTotal.intValue();

        PurchaseCountGoodsResponse purchaseCountGoodsResponse = new PurchaseCountGoodsResponse();
        purchaseCountGoodsResponse.setTotal(shopCartTotalInt);
        purchaseCountGoodsResponse.setShopCartTotal(shopCartTotal);
//        purchaseCountGoodsResponse.setPileTotal(pileTotal);
        return BaseResponse.success(purchaseCountGoodsResponse);
    }

    @Override
    public BaseResponse<PurchaseGetGoodsMarketingResponse> querySuitGoodsByPurchase(PurchaseQueryRequest request) {

        BaseResponse<List<MarketingSuitDetialVO>> suitToBuyByGoodInfoIds = marketingQueryProvider.getSuitToBuyByGoodInfoIds(MarketingMapGetByGoodsIdRequest
                .builder()
                .goodsInfoIdList(request.getGoodsInfoIds())
                .build());

        if(Objects.isNull(suitToBuyByGoodInfoIds) || Objects.isNull(suitToBuyByGoodInfoIds.getContext())
                || CollectionUtils.isEmpty(suitToBuyByGoodInfoIds.getContext())){
            return BaseResponse.SUCCESSFUL();
        }

        List<String> goodsInfoIds = suitToBuyByGoodInfoIds.getContext().stream().map(s -> s.getGoodsInfoId()).collect(Collectors.toList());

        GoodsInfoListByIdsResponse goodsInfoListByIdsResponse = retailGoodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(goodsInfoIds).build()).getContext();

        if(Objects.isNull(goodsInfoListByIdsResponse) || CollectionUtils.isEmpty(goodsInfoListByIdsResponse.getGoodsInfos())){
            return BaseResponse.SUCCESSFUL();
        }
        PurchaseGetGoodsMarketingResponse purchaseGetGoodsMarketingResponse = new PurchaseGetGoodsMarketingResponse();
        purchaseGetGoodsMarketingResponse.setGoodsInfos(goodsInfoListByIdsResponse.getGoodsInfos());
        return BaseResponse.success(purchaseGetGoodsMarketingResponse);
    }
}
