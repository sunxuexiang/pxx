package com.wanmi.sbc.shopcart;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.response.market.MarketingGoodsSortResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingGroupCard;
import com.wanmi.sbc.marketing.bean.vo.PriceInfoOfWholesale;
import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.RetailShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseListRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.MarketingGroupCardResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.MarketingGroupCartMallResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.ShopCarAllResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "ShopCartBaseController", description = "（正常下单）购物车服务API")
@RestController
@RequestMapping("/shopCartAll")
@Validated
@Slf4j
public class ShopCartAllController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private ShopCartQueryProvider shopCartQueryProvider;

    @Autowired
    private BulkShopCartQueryProvider bulkShopCartQueryProvider;

    @Autowired
    private RetailShopCartQueryProvider retailShopCartQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @ApiOperation(value = "获取视图采购单缓存级计算最优惠组合/三合一")
    @RequestMapping(value = "/view/shopCarts", method = RequestMethod.POST)
    public BaseResponse<ShopCarAllResponse> shopCartsAll(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        List<MarketingGoodsSortResponse> sortResponseList = new ArrayList<>();
        //批发部分
        MarketingGroupCartMallResponse response = shopCartsAndCacheMall(request, httpRequest).getContext();
        String pShopCarts = "shopCarts",pRetailShopCarts = "retailShopCarts",pBulkShopCarts = "bulkShopCarts";
        Map<String,MarketingGroupCardResponse> marketingGroupCardResponseMap = new HashMap<>();
        List<Long> storeList = response.getMarketingGroupCardResponseList().stream().map(MarketingGroupCardResponse::getStoreId).distinct().collect(Collectors.toList());
        List<StoreSimpleResponse> storeSimpleResponses = storeQueryProvider.listSimple(StoreQueryRequest.builder().storeIds(storeList).build()).getContext();
        Map<Long, StoreSimpleResponse> storeSimpleResponseMap = storeSimpleResponses.stream().collect(Collectors.toMap(StoreSimpleResponse::getStoreId, Function.identity()));
        for(MarketingGroupCardResponse groupCardResponse: response.getMarketingGroupCardResponseList()){
            StoreSimpleResponse storeInfo = storeSimpleResponseMap.get(groupCardResponse.getStoreId());
            if(storeInfo.getCompanyType()==CompanyType.PLATFORM){
                groupCardResponse.setStoreId(groupCardResponse.getStoreId());
                groupCardResponse.setKey(groupCardResponse.storeName);
                addValue(groupCardResponse,sortResponseList, groupCardResponse.storeName,CompanyType.PLATFORM.toValue());
                marketingGroupCardResponseMap.put(groupCardResponse.storeName,groupCardResponse);
            }else if(storeInfo.getCompanyType()==CompanyType.UNIFIED){
                groupCardResponse.setStoreId(groupCardResponse.getStoreId());
                groupCardResponse.setKey(groupCardResponse.storeName);
                addValue(groupCardResponse,sortResponseList, groupCardResponse.storeName,CompanyType.UNIFIED.toValue());
                marketingGroupCardResponseMap.put(groupCardResponse.storeName,groupCardResponse);
            }else{
                groupCardResponse.setStoreId(groupCardResponse.getStoreId());
                groupCardResponse.setKey(groupCardResponse.storeName);
                addValue(groupCardResponse,sortResponseList,groupCardResponse.storeName,CompanyType.SUPPLIER.toValue());
                marketingGroupCardResponseMap.put(groupCardResponse.storeName,groupCardResponse);
            }
        }
        //零售部分
        MarketingGroupCardResponse retailShopCarts = new MarketingGroupCardResponse(); //retailShopCartsAndCache(request, httpRequest).getContext();
        addValue(retailShopCarts,sortResponseList, pRetailShopCarts,CompanyType.RETAIL.toValue());
        //散批部分
        MarketingGroupCardResponse bulkShopCarts = new MarketingGroupCardResponse(); //bulkShopCartsAndCache(request, httpRequest).getContext();
        addValue(bulkShopCarts,sortResponseList, pBulkShopCarts,CompanyType.BULK.toValue());

        //最终顺序
        List<MarketingGroupCardResponse> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(sortResponseList)){
            //排序
            List<MarketingGoodsSortResponse> collect = sortResponseList
                    .stream().sorted(Comparator.comparing(MarketingGoodsSortResponse::getAddTime).reversed()).collect(Collectors.toList());
            log.info("最终排序--------->"+"collect"+JSONObject.toJSONString(collect));
            collect.forEach(var->{
                if(marketingGroupCardResponseMap.get(var.getKey())!=null){
                    result.add(marketingGroupCardResponseMap.get(var.getKey()));
                }else if(var.getKey().equals(pRetailShopCarts)){
                    retailShopCarts.setKey(pRetailShopCarts);
                    result.add(retailShopCarts);
                }else if(var.getKey().equals(pBulkShopCarts)){
                    bulkShopCarts.setKey(pBulkShopCarts);
                    result.add(bulkShopCarts);
                }
            });
        }
        //校验购物车商品是否可预售
//        this.checkGoodsPresellList(result);

        return BaseResponse.success(ShopCarAllResponse.builder()
                .result(result).build());
    }

    private void calcNoJoinMarketTotalPrice(MarketingGroupCardResponse marketingGroupCardResponse,boolean isAdd) {
        List<DevanningGoodsInfoVO> noJoinMarketingGoods = marketingGroupCardResponse.getNoHaveGoodsInfoVOList();
        //计算未参加营销总价
        BigDecimal reduce = noJoinMarketingGoods.stream().filter(v -> {
            if (v.getIsCheck().equals(DefaultFlag.YES) && GoodsStatus.OK.equals(v.getGoodsStatus())) {
                return true;
            }
            return false;
        }).map(v -> v.getMarketPrice().multiply(BigDecimal.valueOf(v.getBuyCount()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("devanningPurchaseInfoCache reduce: {}", reduce);
        if (Objects.isNull(marketingGroupCardResponse.getPriceInfoOfWholesale())){
            marketingGroupCardResponse.setPriceInfoOfWholesale(new PriceInfoOfWholesale());
        }

        BigDecimal totalAmount =isAdd?(Objects.nonNull(marketingGroupCardResponse.getPriceInfoOfWholesale())
                ? marketingGroupCardResponse.getPriceInfoOfWholesale().getTotalAmount().add(reduce)
                : BigDecimal.ZERO.add(reduce)):marketingGroupCardResponse.getPriceInfoOfWholesale().getTotalAmount().subtract(reduce);
        marketingGroupCardResponse.getPriceInfoOfWholesale().setTotalAmount(totalAmount);

        BigDecimal payableAmount =isAdd?(Objects.nonNull(marketingGroupCardResponse.getPriceInfoOfWholesale())
                ? marketingGroupCardResponse.getPriceInfoOfWholesale().getPayableAmount().add(reduce)
                : BigDecimal.ZERO.add(reduce)):marketingGroupCardResponse.getPriceInfoOfWholesale().getPayableAmount().subtract(reduce);
        marketingGroupCardResponse.getPriceInfoOfWholesale().setPayableAmount(payableAmount);
    }
    private void addValue(MarketingGroupCardResponse response,List<MarketingGoodsSortResponse> sortResponseList,String key,Integer companyType){
        response.setCompanyType(companyType);
        List<MarketingGoodsSortResponse> sortFlag = new ArrayList<>();
        //活动相关
        List<MarketingGroupCard> marketingGroupCards = response.marketingGroupCards;
        if(CollectionUtils.isNotEmpty(marketingGroupCards)){

            marketingGroupCards.forEach(var ->{
                log.info("参与营销商品数量---------> key :"+key+"var.getDevanningGoodsInfoVOList()"+var.getDevanningGoodsInfoVOList().size());
                if(CollectionUtils.isNotEmpty(var.getDevanningGoodsInfoVOList())){
                    //获取最后加入购物车的商品
                    DevanningGoodsInfoVO devanningGoodsInfoVO = var.getDevanningGoodsInfoVOList().get(0);

                    sortFlag.add(MarketingGoodsSortResponse.builder()
                            .addTime(devanningGoodsInfoVO.getCreateTime())
                            .key(key)
                            .devanningGoodsInfoVO(devanningGoodsInfoVO)
                            .marketingVO(var.getMarketingVO())
                            .build());
                }
            });
        }
        //无活动商品
        List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList = response.getNoHaveGoodsInfoVOList();
        if(CollectionUtils.isNotEmpty(noHaveGoodsInfoVOList)){
            log.info("未参与营销商品数量---------> key :"+key+"noHaveGoodsInfoVOList"+noHaveGoodsInfoVOList.size());
            DevanningGoodsInfoVO devanningGoodsInfoVO = noHaveGoodsInfoVOList.get(0);

            sortFlag.add(MarketingGoodsSortResponse.builder()
                    .addTime(devanningGoodsInfoVO.getCreateTime())
                    .key(key)
                    .devanningGoodsInfoVO(devanningGoodsInfoVO)
                    .build());
        }

        log.info("排序信息---------> key :"+key+"sortFlag"+JSONObject.toJSONString(sortFlag));
        if(CollectionUtils.isNotEmpty(sortFlag)){
            List<MarketingGoodsSortResponse> collect = sortFlag
                    .stream().sorted(Comparator.comparing(MarketingGoodsSortResponse::getAddTime).reversed()).collect(Collectors.toList());
            //最终排序结构
            sortResponseList.add(collect.get(0));
        }
    }

    /**
     * 批发部分
     */
    public BaseResponse<MarketingGroupCardResponse> shopCartsAndCache(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        //获取会员
        buildRequestResponse(request, httpRequest);
        MarketingGroupCardResponse response = shopCartQueryProvider.devanningPurchaseInfoCache(request).getContext();
        return BaseResponse.success(response);
    }

    public BaseResponse<MarketingGroupCartMallResponse> shopCartsAndCacheMall(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        //获取会员
        buildRequestResponse(request, httpRequest);
        MarketingGroupCartMallResponse response = shopCartQueryProvider.devanningPurchaseMallInfoCache(request).getContext();
        return BaseResponse.success(response);
    }

    private void buildRequestResponse(PurchaseListRequest request, HttpServletRequest httpRequest) {
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
    }


    /**
     * 零售部分
     * @param request
     * @param request1
     * @return
     */
    public BaseResponse<MarketingGroupCardResponse> retailShopCartsAndCache(@RequestBody PurchaseListRequest request,HttpServletRequest request1) {
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

    /**
     * 散批部分
     * @param request
     * @param httpRequest
     * @return
     */
    public BaseResponse<MarketingGroupCardResponse> bulkShopCartsAndCache(@RequestBody PurchaseListRequest request, HttpServletRequest httpRequest) {
        Long bulkWareId = commonUtil.getBulkWareId(HttpUtil.getRequest());

        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        // 采购单列表
        request.setCustomerId(customer.getCustomerId());
        request.setInviteeId(commonUtil.getPurchaseInviteeId());
        request.setWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));
        if(Objects.nonNull(bulkWareId)){
            request.setBulkWareId(String.valueOf(bulkWareId));
        } else {
            request.setBulkWareId("");
        }
        request.setCustomer(customer);

        request.setLocationCityId(commonUtil.getCityId(httpRequest));
        request.setLocationProvinceId(commonUtil.getProvinceId(httpRequest));
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)) {
            request.setCompanyInfoId(domainInfo.getCompanyInfoId());
        }
        log.info("============devanningInfoView：" + JSONObject.toJSONString(request));
        MarketingGroupCardResponse response = bulkShopCartQueryProvider.purchaseInfoCache(request).getContext();
        return BaseResponse.success(response);
    }

    /**
     * 校验购物车商品是否可预售 @jkp
     * @param list
     */
    @Deprecated
    private void checkGoodsPresellList(List<MarketingGroupCardResponse> list){
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        List<DevanningGoodsInfoVO> dgiVOList = new ArrayList<>();
        list.forEach(item->{
            //检测商家是否可预售
            BaseResponse<Boolean> response = storeQueryProvider.checkStoreIsPresell(item.getStoreId());
            if (!response.getContext()){
                return;
            }
            //未参与营销活动的商品
            if (CollectionUtils.isNotEmpty(item.getNoHaveGoodsInfoVOList())){
                dgiVOList.addAll(item.getNoHaveGoodsInfoVOList());
            }
            //营销活动商品
            if (CollectionUtils.isNotEmpty(item.getMarketingGroupCards())){
                item.getMarketingGroupCards().forEach(mgc->{
                    dgiVOList.addAll(mgc.getDevanningGoodsInfoVOList());
                    if (CollectionUtils.isNotEmpty(mgc.getOverPurchuseLimitDevanningGoodsInfoVOList())){
                        dgiVOList.addAll(mgc.getOverPurchuseLimitDevanningGoodsInfoVOList());
                    }
                });
            }
        });
        if (CollectionUtils.isEmpty(dgiVOList)){
            return;
        }
        List<String> skuIds = dgiVOList.stream().map(DevanningGoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());

        //批量查询sku信息
        GoodsInfoListByIdsRequest request = GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build();
        Map<String, GoodsInfoVO> goodsInfoMap = goodsInfoQueryProvider.listByIds(request).getContext().getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, g -> g, (x,y)->x));

        dgiVOList.forEach(item->{
            //预售虚拟库存数量
            item.setPresellStock(goodsInfoMap.getOrDefault(item.getGoodsInfoId(), new GoodsInfoVO()).getPresellStock());
            boolean isPresell = false;
            if (item.getStock()!=null && item.getPresellStock()!=null){
                isPresell = checkGoodsInfoIsPresell(item.getStock().doubleValue(), item.getPresellStock());
            }
            //是否可预售
            item.setIsPresell(isPresell?1:0);
        });
    }


    /**
     * 判断商品是否可预售 @jkp
     * @param stock
     * @param presellStock
     */
    @Deprecated
    private boolean checkGoodsInfoIsPresell(double stock, long presellStock){
        return stock<=0//真实库存数量
                && presellStock>0;//预售虚拟库存
    }

}
