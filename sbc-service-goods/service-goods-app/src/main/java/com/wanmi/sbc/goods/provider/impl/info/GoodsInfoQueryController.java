package com.wanmi.sbc.goods.provider.impl.info;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.enterprise.goods.EnterpriseGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockQueryRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.reponse.GoodsInfoResponse;
import com.wanmi.sbc.goods.info.reponse.*;
import com.wanmi.sbc.goods.info.request.GoodsInfoRequest;
import com.wanmi.sbc.goods.info.request.*;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.info.service.GoodsService;
import com.wanmi.sbc.goods.info.service.RetailGoodsInfoService;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>对商品sku查询接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@Slf4j
@RestController
@Validated
public class GoodsInfoQueryController implements GoodsInfoQueryProvider {

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsWareStockVillagesService goodsWareStockVillagesService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RetailGoodsInfoService retailGoodsInfoService;

    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;

    @Override
    public BaseResponse<GoodsInfoViewPageResponse> pageViewWrapper(@Valid GoodsInfoViewPageRequest request) {
        Integer goodsType = request.getGoodsType();

        // 零售
        if(Objects.nonNull(goodsType) && Objects.equals(1,goodsType)){ //0、批发 1、零售
            GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
            KsBeanUtil.copyPropertiesThird(request, queryRequest);
            queryRequest.setImageFlag(Boolean.TRUE);
            RetailGoodsInfoResponse pageResponse = retailGoodsInfoService.pageView(queryRequest);
            GoodsInfoViewPageResponse response = new GoodsInfoViewPageResponse();
            response.setGoodsInfoPage(KsBeanUtil.convertPage(pageResponse.getGoodsInfoPage(), GoodsInfoVO.class));
            if(CollectionUtils.isNotEmpty(pageResponse.getGoodses())) {
                response.setGoodses(KsBeanUtil.convert(pageResponse.getGoodses(), GoodsVO.class));
            }
            if(CollectionUtils.isNotEmpty(pageResponse.getBrands())) {
                response.setBrands(KsBeanUtil.convert(pageResponse.getBrands(), GoodsBrandVO.class));
            }
            if(CollectionUtils.isNotEmpty(pageResponse.getCates())) {
                response.setCates(KsBeanUtil.convert(pageResponse.getCates(), GoodsCateVO.class));
            }
            return BaseResponse.success(response);
        }
        // 批发
        return this.pageView(request);
    }

    /**
     * 分页查询商品sku视图列表
     *
     * @param request 商品sku视图分页条件查询结构 {@link GoodsInfoViewPageRequest}
     * @return 商品sku视图分页列表 {@link GoodsInfoViewPageResponse}
     */
    @Override
    public BaseResponse<GoodsInfoViewPageResponse> pageView(@RequestBody @Valid GoodsInfoViewPageRequest request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        queryRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        GoodsInfoResponse pageResponse = goodsInfoService.pageView(queryRequest);
        GoodsInfoViewPageResponse response = new GoodsInfoViewPageResponse();
        response.setGoodsInfoPage(KsBeanUtil.convertPage(pageResponse.getGoodsInfoPage(), GoodsInfoVO.class));
        if (CollectionUtils.isNotEmpty(pageResponse.getGoodses())) {
            response.setGoodses(KsBeanUtil.convertList(pageResponse.getGoodses(), GoodsVO.class));
        }
        if (CollectionUtils.isNotEmpty(pageResponse.getBrands())) {
            response.setBrands(KsBeanUtil.convertList(pageResponse.getBrands(), GoodsBrandVO.class));
        }
        if (CollectionUtils.isNotEmpty(pageResponse.getCates())) {
            response.setCates(KsBeanUtil.convertList(pageResponse.getCates(), GoodsCateVO.class));
        }
        return BaseResponse.success(response);
    }

    /**
     * 分页查询商品sku列表
     *
     * @param request 商品sku分页条件查询结构 {@link GoodsInfoPageRequest}
     * @return 商品sku分页列表 {@link GoodsInfoPageResponse}
     */
    @Override
    public BaseResponse<GoodsInfoPageResponse> page(@RequestBody @Valid GoodsInfoPageRequest request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        GoodsInfoPageResponse response = new GoodsInfoPageResponse();
        response.setGoodsInfoPage(KsBeanUtil.convertPage(goodsInfoService.page(queryRequest), GoodsInfoVO.class));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<List<String>> listGoodsInfoIds(@RequestBody @Valid ListGoodsInfoIdsRequest request) {
        return BaseResponse.success(goodsInfoService.listGoodsInfoIds(request));
    }

    @Override
    public BaseResponse<Map<String, ListGoodsInfoByGoodsInfoIdsVO>> listGoodsInfoByGoodsInfoIds(List<String> request) {
        return BaseResponse.success(goodsInfoService.listGoodsInfoByGoodsInfoIds(request));
    }

    /**
     * 根据商品skuId批量查询商品sku视图列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoViewByIdsRequest}
     * @return 商品sku视图列表 {@link GoodsInfoViewByIdsResponse}
     */
    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByIds(@RequestBody @Valid GoodsInfoViewByIdsRequest
                                                                          request) {
        GoodsInfoRequest infoRequest = new GoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        GoodsInfoResponse infoResponse = goodsInfoService.findSkuByIds(infoRequest);
        return BaseResponse.success(
                GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(KsBeanUtil.convertList(infoResponse.getGoodsInfos(), GoodsInfoVO.class))
                        .goodses(KsBeanUtil.convertList(infoResponse.getGoodses(), GoodsVO.class)).build());
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsLimitWareId(@Valid GoodsInfoViewByIdsRequest request) {
        GoodsInfoRequest infoRequest = new GoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        GoodsInfoResponse infoResponse = goodsInfoService.findSkuByIdsLimitWareId(infoRequest);
        return BaseResponse.success(
                GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(KsBeanUtil.convertList(infoResponse.getGoodsInfos(), GoodsInfoVO.class))
                        .goodses(KsBeanUtil.convertList(infoResponse.getGoodses(), GoodsVO.class)).build());
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsByMatchFlag(@Valid GoodsInfoViewByIdsRequest request) {
        GoodsInfoRequest infoRequest = new GoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        GoodsInfoResponse infoResponse = goodsInfoService.findSkuByIdsAndMatchFlag(infoRequest);
        return BaseResponse.success(
                GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(KsBeanUtil.convertList(infoResponse.getGoodsInfos(), GoodsInfoVO.class))
                        .goodses(KsBeanUtil.convertList(infoResponse.getGoodses(), GoodsVO.class)).build());
    }

    /**
     * 根据商品skuId查询商品sku视图
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoViewByIdRequest}
     * @return 商品sku视图 {@link GoodsInfoViewByIdResponse}
     */
    @Override

    public BaseResponse<GoodsInfoViewByIdResponse> getViewById(@RequestBody @Valid GoodsInfoViewByIdRequest request) {
        GoodsInfoEditResponse editResponse = goodsInfoService.findById(request.getGoodsInfoId(), request.getWareId());
        return BaseResponse.success(GoodsInfoConvert.toGoodsInfoViewByGoodsInfoEditResponse(editResponse));
    }


    /**
     * 根据商品skuId批量查询商品sku列表
     *
     * @param request 根据批量商品skuId查询结构 {@link GoodsInfoListByIdsRequest}
     * @return 商品sku列表 {@link GoodsInfoListByIdsResponse}
     */
    @Override

    public BaseResponse<GoodsInfoListByIdsResponse> listByIds(@RequestBody @Valid GoodsInfoListByIdsRequest request) {
        List<GoodsInfo> goodsInfos = goodsInfoService.findByIds(request.getGoodsInfoIds(), request.getWareId());
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convert(goodsInfos,
                        GoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> listByParentIds(@Valid GoodsInfoListByParentIdsRequest request) {
        List<GoodsInfo> goodsInfos = goodsInfoService.listByParentIds(request.getGoodsInfoParentsIds(), request.getWareId());
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfos,
                        GoodsInfoVO.class))
                .build());
    }

    /**
     * 根据商品skuId查询商品sku
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoByIdRequest}
     * @return 商品sku {@link GoodsInfoByIdResponse}
     */
    @Override
    public BaseResponse<GoodsInfoByIdResponse> getById(@RequestBody @Valid GoodsInfoByIdRequest request) {
        GoodsInfoByIdResponse response = null;
        GoodsInfo goodsInfo = goodsInfoService.findOne(request.getGoodsInfoId());
        if (Objects.nonNull(goodsInfo)) {
            if(Objects.nonNull(request.getWareId())&&!request.getWareId().equals(goodsInfo.getWareId())){
                goodsInfo = goodsInfoService.findByParentGoodsInfoIdAndWareId(goodsInfo.getParentGoodsInfoId(), request.getWareId());
            }
        }
//        if(StringUtils.isNotEmpty(request.getGoodsInfoId())) {
//            goodsInfo = goodsInfoService.findByGoodsInfoIdAndParentGoodsInfoIdAndWareId(request.getGoodsInfoId(), request.getWareId(),request.getParentGoodsInfoId());
//        }else {
//            goodsInfo = goodsInfoService.findByParentGoodsInfoIdAndWareId(request.getParentGoodsInfoId(), request.getWareId());
//        }
        if (Objects.nonNull(goodsInfo)) {
            List<GoodsWareStock> goodsWareStockList = goodsWareStockService.list(GoodsWareStockQueryRequest.builder()
                    .goodsInfoId(goodsInfo.getGoodsInfoId()).delFlag(DeleteFlag.NO).wareId(request.getWareId()).build());
            List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.list(GoodsWareStockQueryRequest
                    .builder().goodsInfoId(goodsInfo.getGoodsInfoId()).delFlag(DeleteFlag.NO).wareId(request.getWareId()).build());

            if (CollectionUtils.isNotEmpty(goodsWareStockList)) {
                BigDecimal sumStock = goodsWareStockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                goodsInfo.setStock(sumStock);
                if (CollectionUtils.isNotEmpty(goodsWareStockVillagesList)) {
                    BigDecimal sumVillages = goodsWareStockVillagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (sumStock.subtract(sumVillages).compareTo(BigDecimal.ZERO) <= 0) {
                        goodsInfo.setStock(BigDecimal.ZERO);
                    } else {
                        goodsInfo.setStock(sumStock.subtract(sumVillages));
                    }
                } else {
                    goodsInfo.setStock(sumStock);
                }
            }
            if (Objects.isNull(goodsInfo.getIsCanPile())) {
                goodsInfo.setIsCanPile(DefaultFlag.NO);
            }
            response = new GoodsInfoByIdResponse();
            KsBeanUtil.copyPropertiesThird(goodsInfo, response);
            response.setGoodsInfos(KsBeanUtil.convertList(Lists.newArrayList(goodsInfo), GoodsInfoVO.class));

        }


//        List<String> skuIds = new ArrayList<>();
//        skuIds.add(goodsInfo.getGoodsInfoId());
////        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(skuIds);
//        //设置库存
//        List<GoodsWareStock> goodsWareStockList = goodsWareStockService.list(GoodsWareStockQueryRequest.builder()
//                .goodsInfoId(request.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());
//        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.list(GoodsWareStockQueryRequest
//                .builder().goodsInfoId(request.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());
//
//        //如果不能匹配到仓,只匹配线下仓
////        if (Objects.nonNull(request.getMatchWareHouseFlag())&&!request.getMatchWareHouseFlag()){
////            List<WareHouseVO> wareHouses =wareHouseService.queryWareHousesByStoreid(goodsInfo.getStoreId(),WareHouseType.STORRWAREHOUSE);
////            List<Long> wareIds= wareHouses.stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
////            goodsWareStockList = goodsWareStockList.stream().filter(param -> wareIds.contains(param.getWareId())).collect(Collectors.toList());
////        }
//        if(CollectionUtils.isNotEmpty(goodsWareStockList)&&Objects.nonNull(goodsInfo)){
//            BigDecimal sumStock = goodsWareStockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//            goodsInfo.setStock(sumStock);
//
////            long sumStock = goodsWareStockList.stream().mapToLong(GoodsWareStock::getStock).sum();
//            if (CollectionUtils.isNotEmpty(goodsWareStockVillagesList)) {
//                BigDecimal sumVillages = goodsWareStockVillagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
//                if (sumStock.subtract(sumVillages).compareTo(BigDecimal.ZERO)  <= 0) {
//                    goodsInfo.setStock(BigDecimal.ZERO);
//                } else {
//                    goodsInfo.setStock(sumStock.subtract(sumVillages));
//                }
//            } else {
//                goodsInfo.setStock(sumStock);
//            }
//            //计算库存 加上虚拟库存 减去囤货数量
////            goodsInfoService.calGoodsInfoStock(goodsInfo,goodsNumsMap);
//        }
//        if(Objects.nonNull(goodsInfo)) {
//            if(Objects.isNull(goodsInfo.getIsCanPile())){
//                goodsInfo.setIsCanPile(DefaultFlag.NO);
//            }
//            response = new GoodsInfoByIdResponse();
//            KsBeanUtil.copyPropertiesThird(goodsInfo, response);
//            response.setGoodsInfos(KsBeanUtil.convertList(Lists.newArrayList(goodsInfo),GoodsInfoVO.class));
//        }
        return BaseResponse.success(response);
    }

    /**
     * 根据动态条件查询商品sku列表
     *
     * @param request 根据动态条件查询结构 {@link GoodsInfoListByConditionRequest}
     * @return 商品sku列表 {@link GoodsInfoListByConditionResponse}
     */
    @Override

    public BaseResponse<GoodsInfoListByConditionResponse> listByCondition(@RequestBody @Valid GoodsInfoListByConditionRequest
                                                                                  request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        GoodsInfoListByConditionResponse response = GoodsInfoListByConditionResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfoService.findByParams(queryRequest), GoodsInfoVO.class))
                .build();
        return BaseResponse.success(response);
    }

    /**
     * 根据erpNos查询特价商品
     *
     * @return
     */
    @Override
    public BaseResponse<GoodsInfoByNoResponse> findSpecialGoodsByErpNos(@RequestBody @Valid GoodsInfoByErpNosRequest request) {
        List<GoodsInfoVO> goodsInfoVos = KsBeanUtil.convertList(goodsInfoService.findSpecialGoodsByErpNos(request.getErpGoodsInfoNos()),
                GoodsInfoVO.class);
        return BaseResponse.success(GoodsInfoByNoResponse.builder().goodsInfo(goodsInfoVos).build());
    }

    @Override
    public BaseResponse<GoodsInfoByNoResponse> findAllGoodsByErpNos(@Valid GoodsInfoByErpNosRequest request) {
        List<GoodsInfoVO> goodsInfoVos = KsBeanUtil.convertList(goodsInfoService.findAllGoodsByErpNos(request.getErpGoodsInfoNos()),
                GoodsInfoVO.class);
        return BaseResponse.success(GoodsInfoByNoResponse.builder().goodsInfo(goodsInfoVos).build());
    }

    /**
     * 根据动态条件统计商品sku个数
     *
     * @param request 根据动态条件统计结构 {@link GoodsInfoCountByConditionRequest}
     * @return 商品sku个数 {@link GoodsInfoCountByConditionResponse}
     */
    @Override
    public BaseResponse<GoodsInfoCountByConditionResponse> countByCondition(@RequestBody @Valid
                                                                            GoodsInfoCountByConditionRequest
                                                                                    request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        GoodsInfoCountByConditionResponse response = GoodsInfoCountByConditionResponse.builder()
                .count(goodsInfoService.count(queryRequest))
                .build();
        return BaseResponse.success(response);
    }

    /**
     * 分页查询分销商品sku视图列表
     *
     * @param request 分销商品sku视图分页条件查询结构 {@link DistributionGoodsPageRequest}
     * @return DistributionGoodsInfoPageResponse
     */
    @Override
    public BaseResponse<DistributionGoodsInfoPageResponse> distributionGoodsInfoPage(@RequestBody @Valid DistributionGoodsPageRequest request) {
        DistributionGoodsQueryRequest queryRequest = new DistributionGoodsQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        DistributionGoodsQueryResponse queryResponse = goodsInfoService.distributionGoodsPage(queryRequest);

        DistributionGoodsInfoPageResponse response = new DistributionGoodsInfoPageResponse();

        response.setGoodsInfoPage(KsBeanUtil.convertPage(queryResponse.getGoodsInfoPage(), GoodsInfoVO.class));
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsBrandList())) {
            response.setBrands(KsBeanUtil.convertList(queryResponse.getGoodsBrandList(), GoodsBrandVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsCateList())) {
            response.setCates(KsBeanUtil.convertList(queryResponse.getGoodsCateList(), GoodsCateVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsInfoSpecDetails())) {
            response.setGoodsInfoSpecDetails(KsBeanUtil.convertList(queryResponse.getGoodsInfoSpecDetails(),
                    GoodsInfoSpecDetailRelVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getCompanyInfoList())) {
            response.setCompanyInfoList(queryResponse.getCompanyInfoList());
        }
        return BaseResponse.success(response);
    }

    /**
     * 分页查询特价商品sku视图列表
     *
     * @param request 特价商品sku视图分页条件查询结构 {@link DistributionGoodsPageRequest}
     * @return DistributionGoodsInfoPageResponse
     */
    @Override
    public BaseResponse<SpecialGoodsInfoResponse> specialGoodsInfoPage(@RequestBody @Valid SpecialGoodsPageRequest request) {
        SpecialGoodsInfoQueryRequest queryRequest = new SpecialGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        queryRequest.setGoodsInfoType(1);
        queryRequest.setDelFlag(0);
        SpecialGoodsQueryResponse queryResponse = goodsInfoService.specialgoodsPage(queryRequest);
        SpecialGoodsInfoResponse response = new SpecialGoodsInfoResponse();
        if (request.isPageFlag() == false) {
            response.setGoodsInfoList(KsBeanUtil.convertList(queryResponse.getGoodsInfoList(), GoodsInfoVO.class));
        } else {
            response.setGoodsInfoPage(KsBeanUtil.convertPage(queryResponse.getGoodsInfoPage(), GoodsInfoVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsBrandList())) {
            response.setBrands(KsBeanUtil.convertList(queryResponse.getGoodsBrandList(), GoodsBrandVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsCateList())) {
            response.setCates(KsBeanUtil.convertList(queryResponse.getGoodsCateList(), GoodsCateVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsInfoSpecDetails())) {
            response.setGoodsInfoSpecDetails(KsBeanUtil.convertList(queryResponse.getGoodsInfoSpecDetails(),
                    GoodsInfoSpecDetailRelVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodList())) {
            response.setGoods(KsBeanUtil.convertList(queryResponse.getGoodList(),
                    GoodsVO.class));
        }

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoByGoodsIdresponse> getBygoodsId(@RequestBody @Valid DistributionGoodsChangeRequest request) {
        List<GoodsInfo> goodsInfos = goodsInfoService.queryBygoodsId(request.getGoodsId());
        List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convertList(goodsInfos, GoodsInfoVO.class);
        return BaseResponse.success(GoodsInfoByGoodsIdresponse.builder().goodsInfoVOList(goodsInfoVOS).build());
    }

    @Override
    public BaseResponse<EnterpriseGoodsInfoPageResponse> enterpriseGoodsInfoPage(@Valid EnterpriseGoodsInfoPageRequest request) {
        EnterpriseGoodsQueryRequest queryRequest = new EnterpriseGoodsQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        EnterPriseGoodsQueryResponse queryResponse = goodsInfoService.enterpriseGoodsPage(queryRequest);
        EnterpriseGoodsInfoPageResponse response = new EnterpriseGoodsInfoPageResponse();
        response.setGoodsInfoPage(KsBeanUtil.convertPage(queryResponse.getGoodsInfoPage(), GoodsInfoVO.class));
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsInfoSpecDetails())) {
            response.setGoodsInfoSpecDetails(KsBeanUtil.convertList(queryResponse.getGoodsInfoSpecDetails(),
                    GoodsInfoSpecDetailRelVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsCateList())) {
            response.setCates(KsBeanUtil.convertList(queryResponse.getGoodsCateList(), GoodsCateVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsBrandList())) {
            response.setBrands(KsBeanUtil.convertList(queryResponse.getGoodsBrandList(), GoodsBrandVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getCompanyInfoList())) {
            response.setCompanyInfoList(queryResponse.getCompanyInfoList());
        }
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoNoResponse> listGoodsInfoNoByErpNos(@RequestBody @Valid GoodsInfoNoByErpNoRequest request) {
        List<String> goodInfoNos = goodsInfoService.listGoodsInfoNoByErpNos(request.getErpGoodsInfoNo());
        return BaseResponse.success(GoodsInfoNoResponse.builder().goodsInfoNoList(goodInfoNos).build());
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> findAllSpecialGoods() {
        List<GoodsInfoVO> goodsInfoVOList = goodsInfoService.findAllSpecialGoods();
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder().goodsInfos(goodsInfoVOList).build());
    }

    /**
     * //     * 功能描述: <br>
     * //     * 〈〉
     * //     * @Param: 查询第三方 库存接口
     * //     * @Return: com.wanmi.sbc.common.base.BaseResponse<com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse>
     * //
     * //     * @Date: 2020/5/14 14:59
     * //
     */
//    private BaseResponse<InventoryQueryResponse> queryStorkByWMS(InventoryQueryRequest param){
//        // TODO: 2020/5/14 仓库地址带完善
//        BaseResponse<InventoryQueryResponse> response = this.requestWMSInventoryProvider.queryInventory(param);
//        if (Objects.isNull(response)||Objects.isNull(response.getContext())){
//            throw new SbcRuntimeException(CommonErrorCode.FAILED);
//        }
//        return response;
//    }
    @Override
    public BaseResponse<GoodsInfoStoreIdBySkuIdResponse> getStoreIdByGoodsId(@Valid GoodsInfoStoreIdBySkuIdRequest request) {
        Long storeId = goodsInfoService.queryStoreId(request.getSkuId());
        return BaseResponse.success(GoodsInfoStoreIdBySkuIdResponse.builder().StoreId(storeId).build());
    }

    @Override
    public BaseResponse<GetGoodsInfoStockByIdResponse> findGoodsInfoStock(@RequestBody @Valid GoodsInfoStockByIdsRequest request) {
        //购物车请求sku集合
        List<GoodsInfoStockRequest> requestGoods = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getGoodsInfo())) {
            requestGoods.addAll(request.getGoodsInfo());
        }
        if (CollectionUtils.isNotEmpty(request.getRetailGoodsInfo())) {
            requestGoods.addAll(request.getRetailGoodsInfo());
        }
        //购物车商品信息集合（批发+零售、含库存）
        List<GoodsInfo> shopCartGoodsInfos = new ArrayList<>();
        //批发
        if (CollectionUtils.isNotEmpty(request.getGoodsInfo())) {

            shopCartGoodsInfos.addAll(devanningGoodsInfoService.findGoodsInfoByIds(request.getGoodsInfo().stream().map(GoodsInfoStockRequest::getDevanningId)
                    .collect(Collectors.toList()), request.getGoodsInfo().stream()
                    .map(GoodsInfoStockRequest::getSkuId).collect(Collectors.toList()), request.getMatchWareHouseFlag()));
        }
        //零售
        if (CollectionUtils.isNotEmpty(request.getRetailGoodsInfo())) {
            shopCartGoodsInfos.addAll(KsBeanUtil.convert(retailGoodsInfoService.findGoodsInfoByIds(request.getRetailGoodsInfo().stream()
                    .map(GoodsInfoStockRequest::getSkuId).collect(Collectors.toList()), request.getMatchWareHouseFlag()), GoodsInfo.class));
        }
        Iterator<GoodsInfo> iterator = shopCartGoodsInfos.iterator();
        while (iterator.hasNext()) {
            GoodsInfo next = iterator.next();
            Optional<GoodsInfoStockRequest> first;
            if (Objects.nonNull(next.getDevanningId())) {
                first = requestGoods.stream()
                        .filter(param -> param.getDevanningId().equals(next.getDevanningId())).findFirst();
            } else {
                first = requestGoods.stream()
                        .filter(param -> param.getSkuId().equals(next.getGoodsInfoId())).findFirst();
            }
            //这里要区分拆箱和散批商品库存判断
            if (first.isPresent()) {
                GoodsInfo goodsInfo = new GoodsInfo();
                if (Objects.nonNull(next.getDevanningId())) {
                    goodsInfo = shopCartGoodsInfos.stream().filter(sg -> sg.getDevanningId().equals(next.getDevanningId())).findFirst().orElse(null);
                    if (first.get().getByCount() > 0 && BigDecimal.valueOf(first.get().getByCount()).multiply(first.get().getDivisorFlag()).compareTo(next.getStock()) <= 0) {
                        if (GoodsStatus.OK.equals(goodsInfo.getGoodsStatus()) ||
                                (Objects.nonNull(goodsInfo.getCheckedAddedFlag()) && goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue()))) {
                            iterator.remove();
                        }
                    }
                } else {
                    goodsInfo = shopCartGoodsInfos.stream().filter(sg -> sg.getGoodsInfoId().equals(next.getGoodsInfoId())).findFirst().orElse(null);
                    if (first.get().getByCount() > 0 && BigDecimal.valueOf(first.get().getByCount()).compareTo(next.getStock()) <= 0) {
                        if (GoodsStatus.OK.equals(goodsInfo.getGoodsStatus()) ||
                                (Objects.nonNull(goodsInfo.getCheckedAddedFlag()) && goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue()))) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(shopCartGoodsInfos)) {
            List<String> skus = shopCartGoodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList());
            SpecialGoodsModifyRequest skuRequest = new SpecialGoodsModifyRequest();
            skuRequest.setGoodsInfoIdList(skus);
            List<GoodsInfo> goodsInfoList = new ArrayList<>();
            //批发
            GoodsInfoResponse goodInfoByIds = goodsInfoService.findGoodInfoByIds(skuRequest);
            //零售
            GoodsInfoResponse retailGoodInfoByIds = retailGoodsInfoService.findGoodInfoByIds(skuRequest);
            //批发
            if (CollectionUtils.isNotEmpty(goodInfoByIds.getGoodsInfos())) {
                goodsInfoList.addAll(goodInfoByIds.getGoodsInfos());
            }
            //零售
            if (CollectionUtils.isNotEmpty(retailGoodInfoByIds.getGoodsInfos())) {
                List<GoodsInfo> retailGoodsInfoVO = retailGoodInfoByIds.getGoodsInfos();
                //区分零售和批发商品
                retailGoodsInfoVO.forEach(i -> i.setIsSupermarketGoods(DefaultFlag.YES.toValue()));
                goodsInfoList.addAll(retailGoodsInfoVO);
            }
            shopCartGoodsInfos.forEach(goods -> {
                Optional<GoodsInfo> first = goodsInfoList.stream().filter(param -> param.getGoodsInfoId()
                        .equals(goods.getGoodsInfoId())).findFirst();
                first.ifPresent(goodsInfo -> {
                    if (Objects.nonNull(goodsInfo.getIsSupermarketGoods()) && DefaultFlag.YES.toValue() == goodsInfo.getIsSupermarketGoods()) {
                        goods.setIsSupermarketGoods(DefaultFlag.YES.toValue());
                    } else {
                        goods.setIsSupermarketGoods(DefaultFlag.NO.toValue());
                    }
                    goods.setGoodsInfoName(goodsInfo.getGoodsInfoName());
                    goods.setGoodsStatus(goodsInfo.getGoodsStatus());
                    if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                        goods.setGoodsInfoImg(goodsInfo.getGoods().getGoodsImg());
                    } else {
                        goods.setGoodsInfoImg(goodsInfo.getGoodsInfoImg());
                    }
                });
            });
        }
        return BaseResponse.success(GetGoodsInfoStockByIdResponse.builder().goodsInfos(KsBeanUtil.convert(shopCartGoodsInfos, GoodsInfoVO.class)).build());
    }

    @Override
    public BaseResponse<GetGoodsInfoStockByIdResponse> findGoodsInfoStockForPile(GoodsInfoStockByIdsRequest request) {
        List<String> sku = new ArrayList<>();
        request.getGoodsInfo().forEach(param -> {
            sku.add(param.getSkuId());
        });

        //获取商品囤货数量
//        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(sku);
        Map<String, GoodsInfo> goodsInfoMap = goodsInfoService.findByIds(sku, null).stream().collect(Collectors.toMap(GoodsInfo::getGoodsInfoId, g -> g));
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.findByGoodsInfoIdIn(sku);
        List<GoodsInfo> statusGoodsInfoByIds = goodsInfoService.findGoodsInfoByIds(sku, request.getMatchWareHouseFlag());
        List<GoodsInfoVO> goodsInfoVOS = new ArrayList<>(10);
        List<Long> unOnline = new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(request.getMatchWareHouseFlag()) && !request.getMatchWareHouseFlag()) {
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (String goodsInfoId : sku) {
                List<GoodsWareStock> stockList;
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                //获取sku信息
                GoodsInfo goodsInfo = goodsInfoMap.getOrDefault(goodsInfoId, null);
                //获取sku囤货数量
//                Long pileGoodsNum = goodsNumsMap.getOrDefault(goodsInfoId,null);
                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
                    GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
                    goodsInfoVO.setGoodsInfoId(goodsInfoId);
                    goodsInfoVO.setStock(sumStock);
                    //如果sku信息和虚拟库存数量不为空，且虚拟库存大于0
//                    if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getVirtualStock()) && goodsInfo.getVirtualStock() > 0){
//                        //库存=真实库存+虚拟库存
//                        goodsInfoVO.setStock(goodsInfoVO.getStock()+goodsInfo.getVirtualStock());
//                    }
//                    if(Objects.nonNull(goodsInfo) && Objects.nonNull(pileGoodsNum) && pileGoodsNum > 0){
//                        goodsInfoVO.setStock(goodsInfoVO.getStock()-pileGoodsNum);
//                    }
                    if (goodsInfoVO.getStock().compareTo(BigDecimal.ZERO) < 0) {
                        goodsInfoVO.setStock(BigDecimal.ZERO);
                    }
                    goodsInfoVOS.add(goodsInfoVO);
                } else {
                    GoodsInfoVO goodsInfoVO = new GoodsInfoVO();
                    goodsInfoVO.setGoodsInfoId(goodsInfoId);
                    goodsInfoVO.setStock(BigDecimal.ZERO);
//                    if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getVirtualStock()) && goodsInfo.getVirtualStock() > 0){
//                        if (Objects.nonNull(pileGoodsNum) && pileGoodsNum > 0){
//                            goodsInfoVO.setStock(goodsInfoVO.getStock()+goodsInfo.getVirtualStock()-pileGoodsNum);
//                        }else{
//                            goodsInfoVO.setStock(goodsInfoVO.getStock()+goodsInfo.getVirtualStock());
//                        }
//                    }
//                    if (goodsInfoVO.getStock() < 0){
//                        goodsInfoVO.setStock(0L);
//                    }
                    goodsInfoVOS.add(goodsInfoVO);
                }
            }
        }
        Iterator<GoodsInfoVO> iterator = goodsInfoVOS.iterator();
        while (iterator.hasNext()) {
            GoodsInfoVO next = iterator.next();
            Optional<GoodsInfoStockRequest> first = request.getGoodsInfo().stream()
                    .filter(param -> param.getSkuId().equals(next.getGoodsInfoId())).findFirst();
            if (first.isPresent()) {
                GoodsInfo goodsInfo = statusGoodsInfoByIds.stream().filter(sg -> sg.getGoodsInfoId().equals(next.getGoodsInfoId())).findFirst().orElse(null);
                //重新赋值商品状态
                goodsInfo.setStock(next.getStock());
                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())
                        && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (goodsInfo.getStock().compareTo(BigDecimal.ONE) < 0) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    } else {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
                if (BigDecimal.valueOf(first.get().getByCount()).compareTo(next.getStock()) <= 0) {
                    if (GoodsStatus.OK.equals(goodsInfo.getGoodsStatus()) ||
                            (Objects.nonNull(goodsInfo.getCheckedAddedFlag()) && goodsInfo.getCheckedAddedFlag().equals(DefaultFlag.NO.toValue()))) {
                        iterator.remove();
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(goodsInfoVOS)) {
            List<String> skus = goodsInfoVOS.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
            SpecialGoodsModifyRequest skuRequest = new SpecialGoodsModifyRequest();
            skuRequest.setGoodsInfoIdList(skus);
            GoodsInfoResponse goodInfoByIds = goodsInfoService.findGoodInfoByIds(skuRequest);
            goodsInfoVOS.forEach(goods -> {
                Optional<GoodsInfo> first = goodInfoByIds.getGoodsInfos().stream().filter(param -> param.getGoodsInfoId()
                        .equals(goods.getGoodsInfoId())).findFirst();
                first.ifPresent(goodsInfo -> {
                    goods.setGoodsInfoName(goodsInfo.getGoodsInfoName());
                    goods.setGoodsStatus(goodsInfo.getGoodsStatus());
                    if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                        goods.setGoodsInfoImg(goodsInfo.getGoods().getGoodsImg());
                    } else {
                        goods.setGoodsInfoImg(goodsInfo.getGoodsInfoImg());
                    }
                });
            });
        }
        return BaseResponse.success(GetGoodsInfoStockByIdResponse.builder().goodsInfos(goodsInfoVOS).build());
    }


    @Override
    public BaseResponse<GoodsInfoStockByIdsResponse> findGoodsInfoStockByIds(@RequestBody @Valid GoodsInfoStockByGoodsInfoIdsRequest request) {
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.findByGoodsInfoIdIn(request.getGoodsInfoIds());
        //获取对应的商品乡镇件分仓库存
        List<GoodsWareStockVillages> goodsWareStockVillagesList = goodsWareStockVillagesService.findByGoodsInfoIdIn(request.getGoodsInfoIds());
        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(request.getGoodsInfoIds());

//        Map<String, Long> stockMap = new HashMap<>();
        Map<String, BigDecimal> stockMap = new HashMap<>();
        List<Long> unOnline = new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(request.getMatchWareHouseFlag()) && !request.getMatchWareHouseFlag()) {
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (String goodsInfoId : request.getGoodsInfoIds()) {
                List<GoodsWareStock> stockList = new ArrayList<>();
                List<GoodsWareStockVillages> villagesList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                    //乡镇件库存
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());

                    //乡镇件库存
                    villagesList = goodsWareStockVillagesList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }

                if (CollectionUtils.isNotEmpty(stockList)) {
                    stockMap.put(goodsInfoId,getskusstock.getOrDefault(goodsInfoId,BigDecimal.ZERO));


//                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    if (CollectionUtils.isNotEmpty(villagesList)) {
//                        BigDecimal sumVillageStock = villagesList.stream().map(GoodsWareStockVillages::getStock).reduce(BigDecimal.ZERO, BigDecimal::add);
//                        if (sumStock.subtract(sumVillageStock).compareTo(BigDecimal.ZERO) <= 0) {
//                            stockMap.put(goodsInfoId, BigDecimal.ZERO);
//                        } else {
//                            stockMap.put(goodsInfoId, sumStock.subtract(sumVillageStock));
//                        }
//                    } else {
//                        stockMap.put(goodsInfoId, sumStock);
//                    }
                } else {
                    stockMap.put(goodsInfoId, BigDecimal.ZERO);
                }
            }
        }


        stockMap.forEach((skuId, stock) -> {

        });
        GoodsInfoStockByIdsResponse response = new GoodsInfoStockByIdsResponse();
        response.setGoodsInfoStockMap(stockMap);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> listGoodsInfoAndStcokByIds(@Valid GoodsInfoAndStockListByIdsRequest request) {
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfoService.findGoodsInfoAndStockByIds(request.getGoodsInfoIds(), request.getMatchWareHouseFlag()),
                        GoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GiftGoodsInfoListByIdsResponse> findGoodsInfoByIds(@Valid GoodsInfoListByIdsRequest request) {

        return BaseResponse.success(GiftGoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfoService.findGoodsInfoByIds(request.getGoodsInfoIds(), request.getMatchWareHouseFlag()),
                        GiftGoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GiftGoodsInfoListByIdsResponse> findGoodsInfoByIdsAndCache(GoodsInfoListByIdsRequest request) {
        return BaseResponse.success(GiftGoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfoService.findGoodsInfoByIdsAndCache(request.getGoodsInfoIds(), request.getMatchWareHouseFlag()),
                        GiftGoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GoodsInfoOnlyShelflifeResponse> findGoodsInfoShelflifeByIds(@Valid GoodsInfoListByIdsRequest request) {
        GoodsInfoOnlyShelflifeResponse list = goodsInfoService.findGoodInfoByIds(request.getGoodsInfoIds());
        return BaseResponse.success(list);
    }

    @Override
    public BaseResponse<List<String>> listGoodsInfoByStock() {
        List<String> response = goodsInfoService.listGoodsInfoByStock();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> getGoodsInfoByT() {
        List<GoodsInfo> goodsInfoList = goodsInfoService.getGoodsInfoByT();
        return BaseResponse.success(GoodsInfoViewByIdsResponse.builder().goodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoVO.class)).build());
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> listValidGoodsInfoAndStcokByErpGoodsInfoNos(GoodsInfoByErpNosRequest request) {
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfoService.queryByValidErpGoodsInfoNos(request.getErpGoodsInfoNos(), request.getWareId(), request.getMatchWareHouseFlag()),
                        GoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> listGoodsStatusByGoodsIds(GoodsInfoListByIdsRequest request) {

        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convertList(goodsInfoService.findGoodsInfoByIds(request.getGoodsInfoIds(), request.getMatchWareHouseFlag()),
                        GoodsInfoVO.class))
                .build());
    }


//    @Override
//    public BaseResponse<GoodsSalesRankingResponse> getSalesRanking(GoodsSalesRankingRequest request) {
//        return BaseResponse.success( GoodsSalesRankingResponse.builder().rankingVOList(goodsInfoService.querySalesRanking(request)).build());
//    }

//    @Override
//    public BaseResponse<GoodsSalesRankingTopResponse> getSalesRankingTop(GoodsSalesRankingTopRequest request) {
////        String skuId = request.getSkuId();
////        Integer top = redisService.getObj("SALES_RANKING_TOP_" + skuId, Integer.class);
////        if(Objects.isNull(top)){
//        Integer  top = goodsInfoService.getSalesRankingTop(request);
////            //同步到redis
////            redisService.setObj("SALES_RANKING_TOP_" + skuId,top,60*60*24);
////        }
//        return BaseResponse.success(GoodsSalesRankingTopResponse.builder().top(top).build());
//    }

    @Override
    public BaseResponse<List<String>> listStockoutGoods() {
        List<String> response = goodsInfoService.listStockoutGoods();
        return BaseResponse.success(response);
    }

    @Override
    public  BaseResponse<List<String>> listByCondition4PileActivity(ListByCondition4PileActivityRequest request) {
        List<String> list = goodsInfoService.listByCondition4PileActivity(request);
        return BaseResponse.success(list);
    }

    @Override
    public BaseResponse<List<Long>> findBrandsHasAddedSku(FindBrandsHasAddedSkuRequest request) {
        List<Long> list = goodsInfoService.findBrandsHasAddedSku(request.getBrandIds(), request.getWareId());
        return BaseResponse.success(list);
    }

    @Override
    public BaseResponse<Map<String, Map<String, String>>> getChangShaSkuInfoByNanChangSkuIdMap() {
        return BaseResponse.success(goodsInfoService.getChangShaSkuInfoByNanChangSkuIdMap());
    }

    @Override
    public BaseResponse<Map<String, Map<String, String>>> getChangShaSkuInfoBySkuIdMap() {
        return BaseResponse.success(goodsInfoService.getChangShaSkuInfoBySkuIdMap());
    }

    @Override
    public BaseResponse<List<GoodsInfoSimpleVo>> findGoodsInfoSimpleVoBySkuIds(List<String> skuIds) {
        List<GoodsInfoSimpleVo> list = goodsInfoService.findGoodsInfoSimpleVoBySkuIds(skuIds);
        return BaseResponse.success(list);
    }
    @Override
    public BaseResponse<GoodsInfoByGoodsIdresponse> getByGoodsIdAndAdded(@RequestBody @Valid DistributionGoodsChangeRequest request) {
        List<GoodsInfo> goodsInfos = goodsInfoService.getByGoodsIdAndAdded(request.getGoodsId());
        List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convertList(goodsInfos, GoodsInfoVO.class);
        return BaseResponse.success(GoodsInfoByGoodsIdresponse.builder().goodsInfoVOList(goodsInfoVOS).build());
    }

    @Override
    public BaseResponse<MicroServicePage<GoodsInfoAreaLimitPageResponse>> goodsInfoAreaLimitPage(GoodsInfoAreaLimitPageRequest request) {
        return BaseResponse.success(goodsInfoService.goodsInfoAreaLimitPage(request));
    }

    @Override
    public BaseResponse goodsInfoAreaLimitAdd(GoodsInfoAreaEditRequest request) {
        goodsInfoService.goodsInfoAreaLimitAdd(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GoodsInfoAreaLimitPageResponse> goodsInfoAreaLimitGetById(String goodsInfoId) {
        return BaseResponse.success(goodsInfoService.goodsInfoAreaLimitGetById(goodsInfoId));
    }

    @Override
    public BaseResponse goodsInfoAreaLimitDeleteById(String goodsInfoId) {
        goodsInfoService.goodsInfoAreaLimitDeleteById(goodsInfoId);
        return BaseResponse.SUCCESSFUL();
    }

}
