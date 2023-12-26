package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.ares.enums.DefaultFlag;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoMappingProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateGoodsRelaQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateGoodsRelaListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateGoodsRelaListByGoodsIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoMappingVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateGoodsRelaVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.market.AddActivitGoodsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingGetByIdResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingGetGoodsByIdResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseQueryProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseGetGoodsMarketingRequest;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseGetGoodsMarketingResponse;
import com.wanmi.sbc.stockout.CityCode;
import com.wanmi.sbc.util.CityUtil;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.util.RegionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "StoreGoodsInfoController", description = "商品服务 API")
@RestController
@Slf4j
public class StoreGoodsInfoController {

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private MarketingLevelPluginProvider marketingLevelPluginProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PurchaseQueryProvider purchaseQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private GoodsInfoMappingProvider goodsInfoMappingProvider;
    @Autowired
    private StoreCateGoodsRelaQueryProvider storeCateGoodsRelaQueryProvider;

    @ApiOperation(value = "活动商品（赠品）切仓")
    @RequestMapping(value = "/goods/catWareMarketingGive", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> catWareMarketingGive(@RequestBody GoodsInfoCatWareRequest request) {
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(request.getMarketingId());
        BaseResponse<MarketingGetByIdResponse> byId = marketingQueryProvider.getById(marketingGetByIdRequest);
        if(Objects.isNull(byId.getContext().getMarketingVO())){
            throw new RuntimeException("错误，不存在的活动！");
        }
        BaseResponse<List<String>> marketingGiveGoods = marketingQueryProvider.getMarketingGiveGoods(AddActivitGoodsRequest.builder().marketingId(request.getMarketingId()).build());
        List<String> giveGoodsIds = marketingGiveGoods.getContext();
        if(CollectionUtils.isEmpty(giveGoodsIds)){
            return BaseResponse.success(new GoodsInfoViewPageResponse());
        }

        //获取商品ParentGoodsInfoId
        BaseResponse<GoodsInfoListByIdsResponse> getParentGoodsInfoId = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(giveGoodsIds).build());
        GoodsInfoListByIdsResponse goodsInfoList = getParentGoodsInfoId.getContext();
        if(CollectionUtils.isEmpty(goodsInfoList.getGoodsInfos())){
            return BaseResponse.success(new GoodsInfoViewPageResponse());
        }
        List<String> parentGoodsInfoIds = goodsInfoList.getGoodsInfos().stream().map(o -> o.getParentGoodsInfoId()).collect(Collectors.toList());
        // log.info("catWareMarketing-------parentGoodsInfoIds>"+ JSONObject.toJSONString(parentGoodsInfoIds));
        //获取切换仓库商品goodsInfoid
        BaseResponse<GoodsInfoListByIdsResponse> getCanGoodsInfoIds = goodsInfoQueryProvider.listByParentIds(GoodsInfoListByParentIdsRequest.builder().goodsInfoParentsIds(parentGoodsInfoIds).wareId(request.getWareId()).build());
        List<GoodsInfoVO> goodsInfos = getCanGoodsInfoIds.getContext().getGoodsInfos();
        if(CollectionUtils.isEmpty(goodsInfos)){
            return BaseResponse.success(new GoodsInfoViewPageResponse());
        }
        List<String> goodsInfoIdsCatWare = goodsInfos.stream().map(o -> o.getGoodsInfoId()).collect(Collectors.toList());
        log.info("catWareMarketing-------goodsInfoIdsCatWare>"+ JSONObject.toJSONString(goodsInfoIdsCatWare));

        // 解决营销活动切仓导致部分商品不可见的问题
        GoodsInfoViewPageRequest queryRequest = GoodsInfoViewPageRequest.builder().goodsInfoIds(goodsInfoIdsCatWare).build();
        queryRequest.setPageSize(10000);

        return this.list(GoodsInfoViewPageRequest.builder().goodsInfoIds(goodsInfoIdsCatWare).build());
    }

    @ApiOperation(value = "活动商品切仓")
    @RequestMapping(value = "/goods/catWareMarketing", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> goodsCatWare(@RequestBody GoodsInfoCatWareRequest request) {
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(request.getMarketingId());
        BaseResponse<MarketingGetByIdResponse> byId = marketingQueryProvider.getById(marketingGetByIdRequest);
        if(Objects.isNull(byId.getContext().getMarketingVO())){
            throw new RuntimeException("错误，不存在的活动！");
        }
        log.info("catWareMarketing-------request>"+ JSONObject.toJSONString(request));
        //获取参与活动的商品
        BaseResponse<List<String>> goodsById = marketingQueryProvider.getMarketingScope(AddActivitGoodsRequest.builder().marketingId(request.getMarketingId()).build());
        log.info("catWareMarketing-------goodsById>"+ JSONObject.toJSONString(goodsById.getContext()));
        //获取商品ParentGoodsInfoId
        BaseResponse<GoodsInfoListByIdsResponse> getParentGoodsInfoId = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(goodsById.getContext()).build());
        GoodsInfoListByIdsResponse goodsInfoList = getParentGoodsInfoId.getContext();
        if(CollectionUtils.isEmpty(goodsInfoList.getGoodsInfos())){
            return BaseResponse.success(new GoodsInfoViewPageResponse());
        }
        List<String> parentGoodsInfoIds = goodsInfoList.getGoodsInfos().stream().map(o -> o.getParentGoodsInfoId()).collect(Collectors.toList());
        log.info("catWareMarketing-------parentGoodsInfoIds>"+ JSONObject.toJSONString(parentGoodsInfoIds));
        //获取切换仓库商品goodsInfoid
        BaseResponse<GoodsInfoListByIdsResponse> getCanGoodsInfoIds = goodsInfoQueryProvider.listByParentIds(GoodsInfoListByParentIdsRequest.builder().goodsInfoParentsIds(parentGoodsInfoIds).wareId(request.getWareId()).build());
        List<GoodsInfoVO> goodsInfos = getCanGoodsInfoIds.getContext().getGoodsInfos();
        if(CollectionUtils.isEmpty(goodsInfos)){
            return BaseResponse.success(new GoodsInfoViewPageResponse());
        }
        List<String> goodsInfoIdsCatWare = goodsInfos.stream().map(o -> o.getGoodsInfoId()).collect(Collectors.toList());
        log.info("catWareMarketing-------goodsInfoIdsCatWare>"+ JSONObject.toJSONString(goodsInfoIdsCatWare));

        // 解决营销活动切仓导致部分商品不可见的问题
        GoodsInfoViewPageRequest queryRequest = GoodsInfoViewPageRequest.builder()
                .goodsInfoIds(goodsInfoIdsCatWare)
                .build();
        queryRequest.setPageSize(10000);

        return this.list(queryRequest);
    }

    /**
     * 分页显示商品
     *
     * @param queryRequest
     * @return 商品详情
     */
    @ApiOperation(value = "分页显示商品")
    @RequestMapping(value = "/goods/skus", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> list(@RequestBody GoodsInfoViewPageRequest queryRequest) {
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
        queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
        if(Objects.nonNull(queryRequest.getWareId()) && queryRequest.getWareId() <= 0){
            queryRequest.setWareId(null);
        }
        return this.skuList(queryRequest);
    }

    /**
     * 分页显示商品，比上面的状态更灵活
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "分页显示商品，比上面的状态更灵活")
    @RequestMapping(value = "/goods/list/sku", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> skuList(@RequestBody GoodsInfoViewPageRequest queryRequest) {
        //获取会员
        CustomerGetByIdResponse customer = null;
        if(StringUtils.isNotBlank(queryRequest.getCustomerId())) {
            customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(queryRequest.getCustomerId())
            ).getContext();
            if (Objects.isNull(customer)) {
                throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
            }
        }
        queryRequest.setStoreId(commonUtil.getStoreId());
        //按创建时间倒序、ID升序
        queryRequest.putSort("addedTime", SortType.DESC.toValue());
        queryRequest.putSort("goodsInfoId", SortType.ASC.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        GoodsInfoViewPageResponse response = goodsInfoQueryProvider.pageView(queryRequest).getContext();

        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfoPage().getContent();
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

        //计算会员价
        if(customer != null && StringUtils.isNotBlank(customer.getCustomerId())) {
            goodsInfoVOList = marketingLevelPluginProvider.goodsListFilter(
                    MarketingLevelGoodsListFilterRequest.builder()
                            .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class))
                            .goodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class)).build())
                    .getContext().getGoodsInfoVOList();
        }
        List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList();

        Long marketingId = queryRequest.getMarketingId();
        //折扣商品：将折扣价设置为市场价
        goodsInfoVOList.forEach(goodsInfoVO -> {
            WareHouseVO wareHouseVO = wareHouseVOList.stream().filter(wareHouseVOOne -> wareHouseVOOne.getWareId().equals(goodsInfoVO.getWareId())).findFirst().orElse(new WareHouseVO());
            goodsInfoVO.setWareName(wareHouseVO.getWareName());
            //有限购
            if(Objects.nonNull(goodsInfoVO.getPurchaseNum()) && goodsInfoVO.getPurchaseNum() >= 0){
                //修改
                if(Objects.nonNull(marketingId)){
                    if(!marketingId.equals(goodsInfoVO.getMarketingId())){
                        goodsInfoVO.setIsDisable(1);
                    }
                }else {
                    //新增
                    goodsInfoVO.setIsDisable(1);
                }
            }
            if (goodsInfoVO.getGoodsInfoType() == 1 && Objects.nonNull(goodsInfoVO.getSpecialPrice())) {
                goodsInfoVO.setMarketPrice(goodsInfoVO.getSpecialPrice());
            }
            BaseResponse<StoreInfoResponse> storeInfo = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(commonUtil.getStoreId()).build());
            goodsInfoVO.setStoreName(storeInfo.getContext().getStoreName());
            StoreCateGoodsRelaListByGoodsIdsRequest storeCateGoodsRelaListByGoodsIdsRequest=new StoreCateGoodsRelaListByGoodsIdsRequest();
            List<String> goodsIds =new ArrayList<>();
            goodsIds.add(goodsInfoVO.getGoodsId());
            storeCateGoodsRelaListByGoodsIdsRequest.setGoodsIds(goodsIds);
            BaseResponse<StoreCateGoodsRelaListByGoodsIdsResponse> storeCateGoodsRelaListByGoodsIdsResponseBaseResponse = storeCateGoodsRelaQueryProvider.listByGoodsIds(storeCateGoodsRelaListByGoodsIdsRequest);
            List<Long>  longs = storeCateGoodsRelaListByGoodsIdsResponseBaseResponse.getContext().getStoreCateGoodsRelaVOList().stream().map(StoreCateGoodsRelaVO::getStoreCateId).collect(Collectors.toList());
            goodsInfoVO.setStoreCateIds(longs);
        });
        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoVOList, queryRequest.getPageRequest(),
                response.getGoodsInfoPage().getTotalElements()));
        return BaseResponse.success(response);
    }
    /**
     * 分页显示商品，比上面的状态更灵活
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "分页显示商品，比上面的状态更灵活")
    @RequestMapping(value = "/storeGoods/sku", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> storeGoods(@RequestBody GoodsInfoViewPageRequest queryRequest) {
        //获取会员
        CustomerGetByIdResponse customer = null;
        if(StringUtils.isNotBlank(queryRequest.getCustomerId())) {
            customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(queryRequest.getCustomerId())
            ).getContext();
            if (Objects.isNull(customer)) {
                throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
            }
        }
        queryRequest.setStoreId(commonUtil.getStoreId());
        //按创建时间倒序、ID升序
        queryRequest.putSort("addedTime", SortType.DESC.toValue());
        queryRequest.putSort("goodsInfoId", SortType.ASC.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        GoodsInfoViewPageResponse response = goodsInfoQueryProvider.pageView(queryRequest).getContext();

        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfoPage().getContent();
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

        //折扣商品：将折扣价设置为市场价
        goodsInfoVOList.forEach(goodsInfoVO -> {
            if (goodsInfoVO.getGoodsInfoType() == 1 && Objects.nonNull(goodsInfoVO.getSpecialPrice())) {
                goodsInfoVO.setMarketPrice(goodsInfoVO.getSpecialPrice());
            }
            BaseResponse<StoreInfoResponse> storeInfo = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(commonUtil.getStoreId()).build());
            goodsInfoVO.setStoreName(storeInfo.getContext().getStoreName());
            StoreCateGoodsRelaListByGoodsIdsRequest storeCateGoodsRelaListByGoodsIdsRequest=new StoreCateGoodsRelaListByGoodsIdsRequest();
            List<String> goodsIds =new ArrayList<>();
            goodsIds.add(goodsInfoVO.getGoodsId());
            storeCateGoodsRelaListByGoodsIdsRequest.setGoodsIds(goodsIds);
            BaseResponse<StoreCateGoodsRelaListByGoodsIdsResponse> storeCateGoodsRelaListByGoodsIdsResponseBaseResponse = storeCateGoodsRelaQueryProvider.listByGoodsIds(storeCateGoodsRelaListByGoodsIdsRequest);
            List<Long>  longs = storeCateGoodsRelaListByGoodsIdsResponseBaseResponse.getContext().getStoreCateGoodsRelaVOList().stream().map(StoreCateGoodsRelaVO::getStoreCateId).collect(Collectors.toList());
            goodsInfoVO.setStoreCateIds(longs);
        });
        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoVOList, queryRequest.getPageRequest(),
                response.getGoodsInfoPage().getTotalElements()));
        return BaseResponse.success(response);
    }
    /**
     * 分页显示商品，比上面的状态更灵活
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "分页显示商品，比上面的状态更灵活")
    @RequestMapping(value = "/goods/listSupplier/sku", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> skuListForSupplierApp(@RequestBody GoodsInfoViewPageRequest queryRequest) {
        //获取会员
        CustomerGetByIdResponse customer = null;
        if(StringUtils.isNotBlank(queryRequest.getCustomerId())) {
            customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(queryRequest.getCustomerId())
            ).getContext();
            if (Objects.isNull(customer)) {
                throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
            }
        }
        queryRequest.setStoreId(commonUtil.getStoreId());

        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        GoodsInfoViewPageResponse response=new GoodsInfoViewPageResponse();
        response.setGoodsIntervalPrices(new ArrayList<>());
        EsGoodsInfoQueryRequest request=new EsGoodsInfoQueryRequest();
        request.setBrandIds(queryRequest.getBrandIds());
        request.setAuditStatus(1);
        request.setAddedFlag(1);
        //此wareID供ES只搜索该仓库下的商品
        request.setWareIdApp(queryRequest.getWareId());
        //此wareID供 公共方法塞入库存使用
        request.setWareId(queryRequest.getWareId());
        request.setPageSize(queryRequest.getPageSize());
        request.setPageNum(queryRequest.getPageNum());
        request.setStoreId(queryRequest.getStoreId());
        request.setGoodsInfoType(0);
        if (StringUtils.isNotBlank(queryRequest.getKeyword())){
            request.setKeywords(queryRequest.getKeyword());
        }
        if(StringUtils.isNotBlank(queryRequest.getErpGoodsInfoNo())){
            request.setErpGoodsInfoNo(queryRequest.getErpGoodsInfoNo());
        }

        Page<EsGoodsInfo> esGoodsInfoPage = esGoodsInfoElasticService.page(request).getEsGoodsInfoPage();
        List<EsGoodsInfo> content = esGoodsInfoPage.getContent();

        List<GoodsInfoVO> goodsInfoVOList = toGoodsInfo(content);
        boolean vipPriceFlag = false;
        if(Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
            vipPriceFlag = true;
        }
        boolean finalVipPriceFlag = vipPriceFlag;
        goodsInfoVOList.forEach(esGoodsInfo -> {
            if(finalVipPriceFlag
                    && Objects.nonNull(esGoodsInfo.getVipPrice())
                    && esGoodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                esGoodsInfo.setSalePrice(esGoodsInfo.getVipPrice());
            }
        });

        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoVOList, queryRequest.getPageRequest(),
                esGoodsInfoPage.getTotalElements()));
        return BaseResponse.success(response);
    }





    /**
     * 批量获取商品信息
     */
    @ApiOperation(value = "批量获取商品信息")
    @RequestMapping(value = "/order/skus", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewListResponse> findSkus(@RequestBody GoodsInfoRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //获取会员
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request
                .getCustomerId())).getContext();
        if (Objects.isNull(customer)) {
            throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
        }
        request.setStoreId(commonUtil.getStoreId());
        GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIds(
                GoodsInfoViewByIdsRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).build()).getContext();

        GoodsInfoViewListResponse response = new GoodsInfoViewListResponse();
        response.setGoodsInfos(idsResponse.getGoodsInfos());
        response.setGoodses(idsResponse.getGoodses());

        //计算区间价
        GoodsIntervalPriceByCustomerIdResponse priceResponse =
                goodsIntervalPriceService.getGoodsIntervalPriceVOList(response.getGoodsInfos(),customer.getCustomerId());
        response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
        response.setGoodsInfos(priceResponse.getGoodsInfoVOList());
        //计算营销价格
        MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
        filterRequest.setGoodsInfos(KsBeanUtil.convert(response.getGoodsInfos(), GoodsInfoDTO.class));
        filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
        response.setGoodsInfos(marketingPluginProvider.goodsListFilter(filterRequest).getContext().getGoodsInfoVOList());
        return BaseResponse.success(response);
    }


    /**
     * 批量查询商品生效的营销活动
     */
    @ApiOperation(value = "批量查询商品生效的营销活动", notes = "返回为单品营销信息map, key为单品id，value为营销列表")
    @RequestMapping(value = "/goods/marketings",method = RequestMethod.POST)
    public BaseResponse<Map<String, List<MarketingViewVO>>> getGoodsMarketings(@RequestBody GoodsInfoRequest request){
        Long wareId;
        List<WareHouseVO> wareHouseVOS = commonUtil.queryAllWareHouses();
        WareHouseVO wareHouseVO = wareHouseVOS.stream().filter(w-> DefaultFlag.YES.equals(w.getDefaultFlag())
                && WareHouseType.ONLINE.equals(w.getType())).findFirst().orElse(null);
        wareId = Objects.isNull(wareHouseVO) ? null : wareHouseVO.getWareId();
        //参数验证
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //获取会员
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request
                .getCustomerId())).getContext();
        if (Objects.isNull(customer)) {
            throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
        }
        request.setStoreId(commonUtil.getStoreId());
        request.setDeleteFlag(DeleteFlag.NO);

        GoodsInfoViewByIdsRequest idsRequest = new GoodsInfoViewByIdsRequest();
        KsBeanUtil.copyPropertiesThird(request, idsRequest);
        GoodsInfoViewByIdsResponse idsResponse = goodsInfoQueryProvider.listViewByIds(idsRequest).getContext();
        PurchaseGetGoodsMarketingRequest purchaseGetGoodsMarketingRequest = new PurchaseGetGoodsMarketingRequest();
        purchaseGetGoodsMarketingRequest.setCustomer(customer);
        purchaseGetGoodsMarketingRequest.setWareId(wareId);
        purchaseGetGoodsMarketingRequest.setGoodsInfos(idsResponse.getGoodsInfos());
        PurchaseGetGoodsMarketingResponse purchaseGetGoodsMarketingResponse = purchaseQueryProvider.getGoodsMarketing(purchaseGetGoodsMarketingRequest).getContext();
        return BaseResponse.success(purchaseGetGoodsMarketingResponse.getMap());
    }


    /**
     * 批量删除商品
     */
    @ApiOperation(value = "批量删除商品")
    @RequestMapping(value = "/goods/sku", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody GoodsInfoRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsInfoProvider.deleteByIds(GoodsInfoDeleteByIdsRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).build());
        esGoodsInfoElasticService.delete(request.getGoodsInfoIds());

        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "删除商品",
                    "删除商品：SKU编码" + response.getGoodsInfoNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量删除",
                    "批量删除");
        }
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 批量上架商品
     */
    @ApiOperation(value = "批量上架商品")
    @RequestMapping(value = "/goods/sku/sale", method = RequestMethod.PUT)
    public BaseResponse onSale(@RequestBody GoodsInfoRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsInfoProvider.modifyAddedStatus(
                GoodsInfoModifyAddedStatusRequest.builder().addedFlag(AddedFlag.YES.toValue())
                        .goodsInfoIds(request.getGoodsInfoIds())
                        .build()
        );
        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.YES.toValue(), null, request.getGoodsInfoIds(),request.getGoodsInfoType());

        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "上架",
                    "上架：SKU编码" + response.getGoodsInfoNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量上架", "批量上架");
        }
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 批量下架商品
     */
    @ApiOperation(value = "批量下架商品")
    @RequestMapping(value = "/goods/sku/sale", method = RequestMethod.DELETE)
    public BaseResponse offSale(@RequestBody GoodsInfoRequest request) {

        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsInfoProvider.modifyAddedStatus(
                GoodsInfoModifyAddedStatusRequest.builder().addedFlag(AddedFlag.NO.toValue())
                        .goodsInfoIds(request.getGoodsInfoIds())
                        .build()
        );
        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), null, request.getGoodsInfoIds(),request.getGoodsInfoType());

        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "下架",
                    "下架：SKU编码" + response.getGoodsInfoNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量下架", "批量下架");
        }
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * 批量上下架商品
     */
    @ApiOperation(value = "批量上下架商品")
    @RequestMapping(value = "/storeGoods/sku/sale", method = RequestMethod.POST)
    public BaseResponse storeGoods(@RequestBody GoodsInfoRequest request) {

        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        if (request.getGoodsInfoIds().size()== Constants.yes && request.getAddedFlag()==Constants.no){
            BaseResponse<GoodsInfoViewByIdResponse> viewById = goodsInfoQueryProvider.getViewById(GoodsInfoViewByIdRequest.builder().goodsInfoId(request.getGoodsInfoIds().get(Constants.no)).wareId(Constants.yes.longValue()).build());
            if (Objects.nonNull(viewById.getContext()) && viewById.getContext() .getGoodsInfo().getHostSku()==Constants.yes){
              return   BaseResponse.error("当前商品是主商品不能直接下架");
            }
        }

        goodsInfoProvider.storeModifyAddedStatus(
                GoodsInfoModifyAddedStatusRequest.builder().addedFlag(request.getAddedFlag())
                        .goodsInfoIds(request.getGoodsInfoIds())
                        .build()
        );
        BaseResponse<GiftGoodsInfoListByIdsResponse> goodsInfoByIds = goodsInfoQueryProvider.findGoodsInfoByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).build());
        List<String> stringList = goodsInfoByIds.getContext().getGoodsInfos().stream().map(o -> o.getGoodsId()).collect(Collectors.toList());
        esGoodsInfoElasticService.storeUpdateAddedStatus(null, stringList, null,null);

        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "批量上下架商品",
                    "批量上下架商品：SKU编码" + response.getGoodsInfoNo()+response.getAddedFlag());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量上下架商品", "批量上下架商品");
        }
        return BaseResponse.SUCCESSFUL();
    }






    private List<GoodsInfoVO>  toGoodsInfo(List<EsGoodsInfo> content){
        List<GoodsInfoVO> result=new ArrayList<>(10);
        for (EsGoodsInfo inner: content){
            GoodsInfoVO convert = KsBeanUtil.convert(inner.getGoodsInfo(), GoodsInfoVO.class);
            result.add(convert);
        }
        return result;
    }

    /**
     * 获取省份类表
     */
    @ApiOperation(value = "获取省份类表")
    @RequestMapping(value = "/goods/region/provinces", method = RequestMethod.GET)
    public BaseResponse<List<CityCode>> getProvinceList(){
        return BaseResponse.success(RegionUtil.getProvincesList());
    }

    /**
     * 获取城市列表
     */
    @ApiOperation(value = "获取城市列表")
    @RequestMapping(value = "/goods/region/citys", method = RequestMethod.GET)
    public BaseResponse<List<CityCode>> getProvinceList(String provinceCode){
        List<CityCode> cityList = RegionUtil.getCityList();
        Map<String, List<CityCode>> map = cityList.stream()
                .collect(Collectors.groupingBy(CityCode::getParentCode));
        List<CityCode> cityCodes = map.get(provinceCode);
        return BaseResponse.success(cityCodes);
    }
}
