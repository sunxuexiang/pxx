package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelWithDefaultByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.es.elastic.EsBulkGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsBulkGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandShelflifeResponse;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteProvider;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteQueryProvider;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.BulkGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelListRequest;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsViewByIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewByIdsRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteAddRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteListRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteModifyRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.GoodsBranchByCategoryIdsRequest;
import com.wanmi.sbc.goods.request.GoodsByParentCateIdQueryRequest;
import com.wanmi.sbc.goods.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.request.GoodsWareStockRequest;
import com.wanmi.sbc.goods.response.BrandsResponse;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingMapGetByGoodsIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingQueryByIdsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.info.GoodsInfoListByGoodsInfoResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingSuitDetialVO;
import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @description: 散批商品
 * @author: XinJiang
 * @time: 2022/4/14 10:30
 */
@RestController
@RequestMapping("/bulk/goods")
@Api(tags = "BulkGoodsBaseController", description = "S2B web公用-散批商品信息API")
@Slf4j
public class BulkGoodsBaseController {

    @Autowired
    private EsBulkGoodsInfoElasticService esBulkGoodsInfoElasticService;


    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private BulkGoodsQueryProvider bulkGoodsQueryProvider;

    @Autowired
    private BulkGoodsInfoProvider bulkGoodsInfoProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

    @Autowired
    private LastGoodsWriteQueryProvider lastGoodsWriteQueryProvider;

    @Autowired
    private LastGoodsWriteProvider lastGoodsWriteProvider;

    @Autowired
    private CustomerAreaLimitDetailProvider customerAreaLimitDetailProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private BulkShopCartProvider bulkShopCartProvider;

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
    private OsUtil osUtil;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BulkGoodsRecommendSettingProvider bulkGoodsRecommendSettingProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    /**
     * 商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "商品分页")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsLimitBrandShelflifeResponse> goodslist(@RequestBody EsBulkGoodsInfoQueryRequest queryRequest,HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        Long bulkWareId = commonUtil.getBulkWareId(httpRequest);
        queryRequest.setWareId(bulkWareId);
        EsGoodsLimitBrandResponse esGoodsLimitBrandResponse = list(queryRequest, customer,httpRequest);

        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();
        if (queryRequest.getCateId() != null) {
            // 根据分类查询分类名称
            GoodsCateByIdRequest request = new GoodsCateByIdRequest();
            request.setCateId(queryRequest.getCateId());
            GoodsCateByIdResponse goodsCateByIdResponse = goodsCateQueryProvider.getById(request).getContext();
            if (Objects.nonNull(goodsCateByIdResponse)) {
                esGoodsLimitBrandResponse.setCateName(goodsCateByIdResponse.getCateName());
            }
        }
        // 格式化商品规格信息
        EsGoodsLimitBrandShelflifeResponse realResponse = new EsGoodsLimitBrandShelflifeResponse();
        List<GoodsVO> goodsList = esGoodsLimitBrandResponse.getGoodsList();
        Map<String, String> goodsVOMap = goodsList.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew, (key1, key2) -> key2));

        for (EsGoods listEsGood : listEsGoods) {
            List<GoodsInfoNest> goodsInfos = listEsGood.getGoodsInfos();
            goodsInfos.forEach(var->{
                var.setGoodsSubtitleNew(goodsVOMap.get(var.getGoodsId()));
            });
        }

        BeanUtils.copyProperties(esGoodsLimitBrandResponse, realResponse);

        return BaseResponse.success(realResponse);
    }

    /**
     * 散批发商品详情
     * 没有营销限购
     * @return 拆箱查看详情
     */
    @ApiOperation(value = "散批商品详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "skuId", value = "skuId", required = true)
    @RequestMapping(value = "/bulk/spu/{skuId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> bulkDetail(@PathVariable String skuId, HttpServletRequest httpRequest) {

        CustomerVO customer = commonUtil.getCustomer();
        GoodsWareStockRequest goodsWareStockRequestBuild = GoodsWareStockRequest.builder()
                .skuId(skuId).wareId(commonUtil.getWareId(HttpUtil.getRequest()))
                .matchWareHouseFlag(true)
                .build();
        GoodsViewByIdResponse detail = detail(goodsWareStockRequestBuild, customer, httpRequest);

        //记录最后一次用户浏览商品记录
        if (Objects.nonNull(customer)) {
            String customerId = customer.getCustomerId();
            List<LastGoodsWriteVO> lastGoodsWriteVOList =
                    lastGoodsWriteQueryProvider.list(LastGoodsWriteListRequest.builder().customerId(customerId).build()).getContext().getLastGoodsWriteVOList();
            GoodsInfoVO goodsInfoVO = detail.getGoodsInfos().get(0);
            if (CollectionUtils.isNotEmpty(lastGoodsWriteVOList)) {
                LastGoodsWriteVO lastGoodsWriteVO = lastGoodsWriteVOList.stream().sorted(Comparator.comparing(LastGoodsWriteVO::getCreateTime).reversed()).findFirst().get();
                LastGoodsWriteModifyRequest build = LastGoodsWriteModifyRequest.builder()
                        .goodsInfoId(goodsInfoVO.getGoodsInfoId())
                        .brandId(goodsInfoVO.getBrandId())
                        .cateId(goodsInfoVO.getCateId())
                        .customerId(customerId)
                        .updateTime(LocalDateTime.now())
                        .createTime(lastGoodsWriteVO.getCreateTime())
                        .id(lastGoodsWriteVO.getId())
                        .build();
                lastGoodsWriteProvider.modify(build);
            } else {
                LastGoodsWriteAddRequest addRequest =
                        LastGoodsWriteAddRequest.builder().brandId(goodsInfoVO.getBrandId()).cateId(goodsInfoVO.getCateId()).
                                customerId(customerId).createTime(LocalDateTime.now()).goodsInfoId(goodsInfoVO.getGoodsInfoId()).build();
                lastGoodsWriteProvider.add(addRequest);
            }
        }
        //限购
        final AtomicReference<BigDecimal>[] quyunum = new AtomicReference[]{null}; //个人商品区域已经购买数量

        CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
        detail.getGoodsInfos().forEach(goodsInfoVOs -> {
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
            }
        });
        final AtomicReference<BigDecimal>[] marketingunum = new AtomicReference[]{null}; //个人某个营销某个商品的购买数量
        final AtomicReference<Long>[] marketingAlluNum = new AtomicReference[]{null}; //总营销某个商品的限购数量
        final AtomicReference<Long>[] marketingAllUseruNum = new AtomicReference[]{null}; //总营销总个人能买限购数量
        final AtomicReference<Long>[] markingId = new AtomicReference[]{null};

        detail.setAlreadyNum(Objects.isNull(quyunum[0])?null:quyunum[0].get());
        detail.setAlreadyMarketingNum(Objects.isNull(marketingunum[0])?null: marketingunum[0].get());
        detail.setAllMarketingNum(Objects.isNull(marketingAlluNum[0])?null: marketingAlluNum[0].get());
        detail.setAllMarkeingUserNum(Objects.isNull(marketingAllUseruNum[0])?null: marketingAllUseruNum[0].get());
        detail.setMarktingId(Objects.isNull(markingId[0])?null: markingId[0].get());
        return BaseResponse.success(detail);
    }

    /**
     * 分类商品分页(ES级)
     *
     * @param queryRequest 搜索条件
     * @return 返回分页结果
     */
    @ApiOperation(value = "分类页商品分页")
    @RequestMapping(value = "/brandSeqBySpus", method = RequestMethod.POST)
    public BaseResponse<EsGoodsResponse> cateBrandSortGoodslist(@RequestBody GoodsByParentCateIdQueryRequest queryRequest, HttpServletRequest httpRequest) {
        CustomerVO customer = commonUtil.getCustomer();
        Long bulkWareId = commonUtil.getBulkWareId(httpRequest);
        queryRequest.setWareId(bulkWareId);

        EsGoodsResponse esGoodsLimitBrandResponse = listByBrandSeq(queryRequest, customer,httpRequest);

        Page<EsGoods> listEsGoods = esGoodsLimitBrandResponse.getEsGoods();

        if (CollectionUtils.isEmpty(listEsGoods.getContent())){
            return BaseResponse.success(EsGoodsResponse.builder().esGoods(new PageImpl<>(Lists.newArrayList())).build());
        }

        List<GoodsVO> goodsList = esGoodsLimitBrandResponse.getGoodsList();
        Map<String, String> goodsVOMap = goodsList.stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew, (key1, key2) -> key2));

        for (EsGoods listEsGood : listEsGoods) {
            List<GoodsInfoNest> goodsInfos = listEsGood.getGoodsInfos();
            goodsInfos.forEach(var->{
                var.setGoodsSubtitleNew(goodsVOMap.get(var.getGoodsId()));
            });
        }

        return BaseResponse.success(esGoodsLimitBrandResponse);
    }

    /**
     * 查询商品品牌信息
     *
     * @return 返回分页结果
     */
    @ApiOperation(value = "查询商品品牌信息")
    @RequestMapping(value = "/getBrandByCateIds", method = RequestMethod.POST)
    public BaseResponse<BrandsResponse> getBrandByCateId(@RequestBody GoodsBranchByCategoryIdsRequest goodsBranchByCategoryIdsRequest) {
        EsGoodsInfoQueryRequest request = new EsGoodsInfoQueryRequest();
        request.setCateIds(goodsBranchByCategoryIdsRequest.getCateIds());
        List<GoodsBrandVO> esBaseInfoByCateId = esGoodsInfoElasticService.getEsBaseInfoByCateId(request);
        return BaseResponse.success(BrandsResponse.builder().goodsBrandVOS(esBaseInfoByCateId).build());
    }

    @ApiOperation("获取散批推荐商品列表商品信息")
    @PostMapping("/get-recommend-by-cache")
    public BaseResponse<GoodsInfoViewByIdsResponse> getListByCache(HttpServletRequest httpRequest){
        //散批仓库id
        Long bulkWareId = commonUtil.getBulkWareId(httpRequest);

        if (!redisService.hasKey(CacheKeyConstant.BULK_GOODS_RECOMMEND)) {
            bulkGoodsRecommendSettingProvider.fillRedis();
        }
        GoodsInfoViewByIdsResponse response = JSONObject.parseObject(redisService.getString(CacheKeyConstant.BULK_GOODS_RECOMMEND),GoodsInfoViewByIdsResponse.class);
        // log.info("返回结果"+response.getGoodsInfos());
        if (Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getGoodsInfos())) {
            response.setGoodsInfos(response.getGoodsInfos().stream().filter(v->{
                        if (Objects.isNull(v.getWareId())){
                            return false;
                        }
                        return v.getWareId().compareTo(bulkWareId)==0L;
                    }).collect(Collectors.toList())
            );
            List<String> skuIds = response.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(skuIds)){
              return   BaseResponse.success(response);
            }
            Map<String, String> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, GoodsVO::getGoodsSubtitleNew, (key1, key2) -> key2));
            response.getGoodsInfos().forEach(var->{
                var.setGoodsSubtitleNew(goodsVOMap.get(var.getGoodsId()));
            });

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
                });

                response.setGoodsInfos(response.getGoodsInfos().stream().filter(v->{
                    if (v.getGoodsStatus().equals(GoodsStatus.INVALID)){
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList()));
            }
            //限购
            CustomerDeliveryAddressResponse finalDeliveryAddress =commonUtil.getProvinceCity(httpRequest);
            response.getGoodsInfos().forEach(goodsInfoVO -> {
                if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                    }
                }
            });

            // 填充购物车信息
            CustomerVO customer = commonUtil.getCustomer();
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(response.getGoodsInfos());
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
                //购物车
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = bulkShopCartProvider.fillBuyCount(request).getContext();
                response.setGoodsInfos(purchaseFillBuyCountResponse.getGoodsInfoList());
            }
        }

        return BaseResponse.success(response);
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
        EsBulkGoodsInfoQueryRequest esGoodsInfoQueryRequest = KsBeanUtil.convert(queryRequest, EsBulkGoodsInfoQueryRequest.class);

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
                            CustomerLevelWithDefaultByCustomerIdRequest.builder().customerId(customer.getCustomerId()).build()).getContext();
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
            response = esBulkGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
        } else {
            response = esBulkGoodsInfoElasticService.pageByGoods(esGoodsInfoQueryRequest);
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
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = bulkShopCartProvider.fillBuyCount(request)
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
                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoNest.getAllowedPurchaseArea())
                            && goodsInfoNest.getGoodsStatus().equals(GoodsStatus.OK)) {
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoNest.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoNest.setGoodsStatus(GoodsStatus.QUOTA);
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
        }

        return response;
    }


    /**
     * SPU商品详情
     *
     * @param goodsWareStockRequest 商品skuId
     * @param customer              会员
     * @return SPU商品详情
     */
    private GoodsViewByIdResponse detail(GoodsWareStockRequest goodsWareStockRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        String skuId = goodsWareStockRequest.getSkuId();
        Long wareId = goodsWareStockRequest.getWareId();
        Boolean matchWareHouseFlag = goodsWareStockRequest.getMatchWareHouseFlag();
        String parentSkuId = goodsWareStockRequest.getParentSkuId();
        GoodsViewByIdResponse response = goodsDetailBaseInfo(skuId, wareId, matchWareHouseFlag, parentSkuId);
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if (Objects.nonNull(domainInfo)
                && !domainInfo.getStoreId().equals(response.getGoods().getStoreId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfos().stream()
                .filter(g -> AddedFlag.YES.toValue() == g.getAddedFlag())
                .collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
//            response = detailGoodsInfoVOList(response, goodsInfoVOList, customer);
//        }

        List<GoodsLabelVO> collect = response.getGoods().getGoodsLabels().stream()
                .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                .collect(Collectors.toList());
        response.getGoods().setGoodsLabels(collect);
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
                List<GoodsInfoVO> suitGoodsList = bulkGoodsInfoQueryProvider.listViewByIds(GoodsInfoViewByIdsRequest.builder()
                        .goodsInfoIds(suitSkuIds).build()).getContext().getGoodsInfos();
                goodsInfos.setSuitGoodsList(suitGoodsList);
            } else {
                goodsInfos.setIsSuitToBuy(DefaultFlag.NO);
            }
            //填充本品价格
            if (Objects.nonNull(goodsInfos.getIsSuitGoods()) && goodsInfos.getIsSuitGoods().equals(DefaultFlag.YES)
                    && Objects.nonNull(goodsInfos.getChoseProductSkuId())) {
                GoodsInfoVO productGoodsInfo = bulkGoodsInfoQueryProvider.getBulkById(GoodsInfoByIdRequest.builder()
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
            // log.info("商品详情计算副标题常出现异常--->"+JSONObject.toJSONString(goodsVO));
        }
        goodsVO.setGoodsSubtitle(replace);
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
     * SPU商品详情-基础信息（不包括区间价、营销信息）
     *
     * @param skuId 商品skuId
     * @return SPU商品详情
     */
    private GoodsViewByIdResponse goodsDetailBaseInfo(String skuId, Long wareId, Boolean matchWareHouseFlag, String parentSkuId) {
        GoodsInfoByIdRequest goodsInfoQueryBuilder = GoodsInfoByIdRequest.builder()
                .goodsInfoId(skuId)
                .wareId(wareId)
                .matchWareHouseFlag(matchWareHouseFlag)
                .build();
        GoodsInfoVO goodsInfo = bulkGoodsInfoQueryProvider.getBulkById(goodsInfoQueryBuilder).getContext();

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
        GoodsViewByIdResponse response = bulkGoodsQueryProvider.getBulkViewById(request).getContext();
        return response;
    }

    /**
     * 查询SPU列表
     *
     * @param queryRequest 查询列表
     * @param customer     会员
     * @return spu商品封装数据
     */
    private EsGoodsLimitBrandResponse list(EsBulkGoodsInfoQueryRequest queryRequest, CustomerVO customer,HttpServletRequest httpRequest) {
        if (queryRequest.getSortFlag() == null) {
            queryRequest.setSortFlag(10);
        }
        queryRequest.setIsKeywords(false);
        if (StringUtils.isNotEmpty(queryRequest.getKeywords())) {
            String keywordsStr = esBulkGoodsInfoElasticService.analyze(queryRequest.getKeywords());
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
            response = esBulkGoodsInfoElasticService.pageByGoods(queryRequest);
        } else {
            response = esBulkGoodsInfoElasticService.pageByGoods(queryRequest);
        }
        log.info("bulkgoods pageByGoods list:{}", JSON.toJSONString(response));

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
            // 填充购物车信息
            if (Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getCustomerId())) {
                PurchaseFillBuyCountRequest request = new PurchaseFillBuyCountRequest();
                request.setCustomerId(customer.getCustomerId());
                request.setGoodsInfoList(goodsInfoList);
                request.setInviteeId(commonUtil.getPurchaseInviteeId());
                //购物车
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = bulkShopCartProvider.fillBuyCount(request)
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
}
