package com.wanmi.sbc.shopcart.provider.impl.cart;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAndStockListByIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.marketing.api.response.market.MarketingGoodsSortResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingGroupCard;
import com.wanmi.sbc.marketing.bean.vo.PriceInfoOfWholesale;
import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.CheckPurchaseNumDTO;
import com.wanmi.sbc.shopcart.api.request.purchase.CheckPurchaseNumRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.CheckPurchaseNumResponse;
import com.wanmi.sbc.shopcart.api.request.purchase.CheckPurchaseNumVO;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseCountGoodsRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseGetGoodsMarketingRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseGetStoreCouponExistRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseGetStoreMarketingRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseListRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseQueryCacheRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseQueryGoodsMarketingListRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseQueryRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.ValidateAndSetGoodsMarketingsRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseVO;
import com.wanmi.sbc.shopcart.cart.BulkShopCart;
import com.wanmi.sbc.shopcart.cart.BulkShopCartService;
import com.wanmi.sbc.shopcart.cart.request.BulkShopCartRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-04
 */
@Validated
@RestController
@Log4j2
public class BulkShopCartQueryController implements BulkShopCartQueryProvider {

    @Autowired
    private BulkShopCartService bulkShopCartService;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

    @Override
    public BaseResponse<MarketingGroupCardResponse> purchaseInfoCache(PurchaseListRequest request) {

        String bulkWareIdStr  = request.getBulkWareId(); //前端传的是空字符串

        if(Strings.isBlank(bulkWareIdStr)){
            return BaseResponse.success(new MarketingGroupCardResponse());
        }

        if(!StringUtils.isNumeric(bulkWareIdStr)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        Long bulkWareId = Long.valueOf(bulkWareIdStr);

        BulkShopCartRequest purchaseRequest = BulkShopCartRequest.builder()
                .customerId(request.getCustomerId())
                .inviteeId(request.getInviteeId())
                .wareId(bulkWareId)
                .matchWareHouseFlag(request.getMatchWareHouseFlag())
                .companyInfoId(request.getCompanyInfoId())
                .isRefresh(request.getIsRefresh())
                .build();
        purchaseRequest.setPageSize(request.getPageSize());
        purchaseRequest.setPageNum(request.getPageNum());
        PurchaseResponse purchaseResponse = bulkShopCartService.pageListAndCache(purchaseRequest);

        List<GoodsInfoVO> goodsInfos = Optional.ofNullable(purchaseResponse.getGoodsInfos()).orElse(Lists.newArrayList());
        if (CollectionUtils.isEmpty(goodsInfos)){
            return BaseResponse.success(new MarketingGroupCardResponse());
        }

        //设置商品状态
        setStockAndGoodsStatus(goodsInfos,Long.parseLong(request.getBulkWareId()));
        this.setGoodsSaleableStatus(goodsInfos, request);

        //调用营销接口获取最优
        List<DevanningGoodsInfoVO> noJoinMarketingGoods = Lists.newArrayList(); //一定不会去参加营销的商品
        // 注释的原因：散批没有参加营销活动
//        List<DevanningGoodsInfoVO> joinMarketingGoods = Lists.newArrayList(); //需要去参加营销的商品
        noJoinMarketingGoods.addAll(KsBeanUtil.convert(goodsInfos, DevanningGoodsInfoVO.class));

        CustomerVO customer = request.getCustomer();
        MarketingGroupCardResponse marketingGroupCardResponse = new MarketingGroupCardResponse();
        // 注释的原因：散批没有参加营销活动
//        if (CollectionUtils.isNotEmpty(joinMarketingGoods)){
//              marketingGroupCardResponse = marketingQueryProvider.goodsGroupByMarketing(MarketingCardGroupRequest.builder()
//                    .devanningGoodsInfoVOList(joinMarketingGoods)
//                    .customerId(request.getCustomerId())
//                    .customerVO(customer)
//                    .build()).getContext();
//        }
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
            if (Objects.isNull(marketingGroupCardResponse.getPriceInfoOfWholesale())){
                marketingGroupCardResponse.setPriceInfoOfWholesale(new PriceInfoOfWholesale());
            }

            BigDecimal totalAmount = Objects.nonNull(marketingGroupCardResponse.getPriceInfoOfWholesale())
                    ? marketingGroupCardResponse.getPriceInfoOfWholesale().getTotalAmount().add(reduce)
                    : BigDecimal.ZERO.add(reduce);
            marketingGroupCardResponse.getPriceInfoOfWholesale().setTotalAmount(totalAmount);

            BigDecimal payableAmount = Objects.nonNull(marketingGroupCardResponse.getPriceInfoOfWholesale())
                    ? marketingGroupCardResponse.getPriceInfoOfWholesale().getPayableAmount().add(reduce)
                    : BigDecimal.ZERO.add(reduce);
            marketingGroupCardResponse.getPriceInfoOfWholesale().setPayableAmount(payableAmount);

            //没参加营销的商品
            List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList = marketingGroupCardResponse.getNoHaveGoodsInfoVOList();
            if (CollectionUtils.isEmpty(noHaveGoodsInfoVOList)){
                noHaveGoodsInfoVOList = noJoinMarketingGoods;
            } else {
                noHaveGoodsInfoVOList.addAll(noJoinMarketingGoods);
            }
            marketingGroupCardResponse.setNoHaveGoodsInfoVOList(noHaveGoodsInfoVOList);
        }
        //对营销及商品排序
        sortGoodsAndMarketing(marketingGroupCardResponse);

        if (CollectionUtils.isNotEmpty(purchaseResponse.getStores())){
            marketingGroupCardResponse.setStoreId(purchaseResponse.getStores().stream().findAny().get().getStoreId());
        }
        return BaseResponse.success(marketingGroupCardResponse);
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
    private void sortGoodsAndMarketing(MarketingGroupCardResponse marketingGroupCardResponse){
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
     * 设置sku可售状态
     * @param goodsInfoVOS
     * @param request
     */
    public void setGoodsSaleableStatus(List<GoodsInfoVO> goodsInfoVOS, PurchaseListRequest request){
        goodsInfoVOS.forEach(goodsInfoVO -> {

            boolean goodsIsSaleable = Objects.equals(DeleteFlag.NO, goodsInfoVO.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfoVO.getAuditStatus())
                    && Objects.equals(AddedFlag.YES.toValue(), goodsInfoVO.getAddedFlag());

            if (!goodsIsSaleable) {
                goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                return;
            }

            goodsInfoVO.setGoodsStatus(GoodsStatus.OK);
            // 判断是否有T，如果是1，就设置为2
            if (goodsInfoVO.getGoodsInfoName().endsWith("T") || goodsInfoVO.getGoodsInfoName().endsWith("t")) {
                log.info("check t flag ");
                if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) <= 0 && (Objects.isNull(goodsInfoVO.getMarketingId()) || goodsInfoVO.getPurchaseNum() == -1)) {
                    goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {
                if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) <= 0 && (Objects.isNull(goodsInfoVO.getMarketingId()) || goodsInfoVO.getPurchaseNum() == -1)) {
                    log.info("1 else flag");
                    goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
                //根据指定区域销售地址和客户收货地址省、市对比判断商品状态
                if (StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea()) && Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())){
                    log.info("2 else flag");
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    log.info("allowedPurchaseAreaList1:{}",JSON.toJSONString(allowedPurchaseAreaList));
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                    }
                }
                //定位区域限购
                if (StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea()) && Objects.nonNull(request.getLocationProvinceId()) && Objects.nonNull(request.getLocationCityId())){
                    log.info("3 else flag");
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    log.info("allowedPurchaseAreaList1:{}",JSON.toJSONString(allowedPurchaseAreaList));
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(request.getLocationCityId()) && !allowedPurchaseAreaList.contains(request.getLocationProvinceId())) {
                        log.info("4 else flag");
                        goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                    }
                }
            }
        });
    }

    /**
     * @param request 查询采购单请求结构 {@link PurchaseQueryRequest}
     * @return
     */
    @Override
    public BaseResponse<PurchaseQueryResponse> query(@RequestBody @Valid PurchaseQueryRequest request) {
        List<BulkShopCart> shopCartList = bulkShopCartService.queryPurchase(request.getCustomerId(), request.getGoodsInfoIds
                (), request.getInviteeId());
        PurchaseQueryResponse purchaseQueryResponse = new PurchaseQueryResponse();
        purchaseQueryResponse.setPurchaseList(KsBeanUtil.convertList(shopCartList, PurchaseVO.class));
        return BaseResponse.success(purchaseQueryResponse);
    }

    @Override
    public BaseResponse<PurchaseQueryResponse> queryShopCarExit(PurchaseQueryCacheRequest request) {
        List<BulkShopCart> shopCartList = bulkShopCartService.getShopCarCache(request.getCustomerId(), request.getWareId(), request.getGoodsInfos());
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
        List<BulkShopCart> shopCartList = bulkShopCartService.queryPurchase(request.getCustomer().getCustomerId(), null, request.getInviteeId());
        if (CollectionUtils.isEmpty(shopCartList)) return BaseResponse.success(new PurchaseGetStoreCouponExistResponse());

        List<GoodsInfoVO> goodsInfos = bulkGoodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(
                shopCartList.stream().map(i -> i.getGoodsInfoId()).collect(Collectors.toList())
        ).build()).getContext().getGoodsInfos();

        Map<Long, Boolean> storeCouponExist = bulkShopCartService.getStoreCouponExist(KsBeanUtil.convertList(goodsInfos, GoodsInfoVO.class),
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
        Map<Long, List<PurchaseMarketingCalcResponse>> storeMarketing = bulkShopCartService.getStoreMarketingBase(request
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
        List<GoodsMarketingVO> goodsMarketingVOS = bulkShopCartService.queryGoodsMarketingList(request.getCustomerId());
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
        return BaseResponse.success(bulkShopCartService.getGoodsMarketing(goodsInfos, request.getCustomer(), request.getWareId()));
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
            total = bulkShopCartService.countGoodsByCompanyInfoId(request.getCustomerId(), request.getInviteeId(),request.getCompanyInfoId());
        }else {
            total = bulkShopCartService.countGoods(request.getCustomerId(), request.getInviteeId());
        }
        PurchaseCountGoodsResponse purchaseCountGoodsResponse = new PurchaseCountGoodsResponse();
        purchaseCountGoodsResponse.setTotal(total);
        return BaseResponse.success(purchaseCountGoodsResponse);
    }

    /**
     * 未登录时,验证并设置前端传入的商品使用营销信息
     *
     * @return
     */
    @Override
    public BaseResponse<PurchaseResponse> validateAndSetGoodsMarketings(@RequestBody @Valid
                                                                        ValidateAndSetGoodsMarketingsRequest request) {
        PurchaseResponse response = bulkShopCartService.validateAndSetGoodsMarketings(
                request.getResponse(), request.getGoodsMarketingDTOList());
        return BaseResponse.success(response);
    }

    /**
     * 查询采购车配置
     * @return
     */
    @Override
    public BaseResponse<ProcurementConfigResponse> queryProcurementConfig(){
        return bulkShopCartService.getProcurementType();
    }

    @Override
    public BaseResponse<CheckPurchaseNumResponse> checkPurchaseNum(CheckPurchaseNumRequest request) {
        if(CollectionUtils.isEmpty(request.getCheckPurchaseNumDTOS())){
            return BaseResponse.success(new CheckPurchaseNumResponse());
        }
        List<CheckPurchaseNumVO> checkPurchaseNumVOS = new ArrayList<>();
        List<String> goodsInfoIds = request.getCheckPurchaseNumDTOS().stream().map(CheckPurchaseNumDTO::getGoodsInfoId).collect(Collectors.toList());
        List<GoodsInfoVO> goodsInfoVOS = bulkGoodsInfoQueryProvider.listGoodsInfoAndStcokByIds(GoodsInfoAndStockListByIdsRequest.builder().goodsInfoIds(goodsInfoIds).build()).getContext().getGoodsInfos();
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

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
