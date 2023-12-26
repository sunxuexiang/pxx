package com.wanmi.sbc.goods.provider.impl.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByParentIdRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockQueryRequest;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.info.model.root.BulkGoodsInfo;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsInfoEditResponse;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsInfoResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsInfoEditResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsInfoResponse;
import com.wanmi.sbc.goods.info.request.BulkGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsInfoRequest;
import com.wanmi.sbc.goods.info.request.RetailGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.service.BulkGoodsInfoService;
import com.wanmi.sbc.goods.info.service.RetailGoodsInfoService;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 零售商品查询实现类
 * @author: XinJiang
 * @time: 2022/3/8 15:52
 */
@RestController
@Validated
public class BulkGoodsInfoQueryController implements BulkGoodsInfoQueryProvider {

    @Autowired
    private BulkGoodsInfoService bulkGoodsInfoService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private WareHouseService wareHouseService;



    @Override
    public BaseResponse<GoodsInfoViewPageResponse> pageView(GoodsInfoViewPageRequest request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        BulkGoodsInfoResponse pageResponse = bulkGoodsInfoService.pageView(queryRequest);
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

    @Override
    public BaseResponse<GoodsInfoViewByIdResponse> getViewBulkById(@Valid GoodsInfoViewByIdRequest request) {
        BulkGoodsInfoEditResponse editResponse = bulkGoodsInfoService.findById(request.getGoodsInfoId());
        return BaseResponse.success(BulkGoodsInfoConvert.toGoodsInfoViewByGoodsInfoEditResponse(editResponse));
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByIds(GoodsInfoViewByIdsRequest request) {
        com.wanmi.sbc.goods.info.request.GoodsInfoRequest infoRequest = new GoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        BulkGoodsInfoResponse infoResponse = bulkGoodsInfoService.findSkuByIds(infoRequest);
        Map<String, Long> stockMap = infoResponse.getGoodsInfos().stream().collect(Collectors.toMap(BulkGoodsInfo::getGoodsInfoId, BulkGoodsInfo::getStock, (a, b) -> a + b));
        List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convert(infoResponse.getGoodsInfos(), GoodsInfoVO.class);
        goodsInfoVOS.forEach(v->{
            v.setStock(BigDecimal.valueOf(stockMap.get(v.getGoodsInfoId())));
        });
        return BaseResponse.success(
                GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(goodsInfoVOS)
                        .goodses(KsBeanUtil.convertList(infoResponse.getGoodses(), GoodsVO.class)).build());
    }

    /**
     * 根据商品skuId查询商品sku
     *
     * @param request 根据商品skuId查询结构 {@link GoodsInfoByIdRequest}
     * @return 商品sku {@link GoodsInfoByIdResponse}
     */
    @Override
    public BaseResponse<GoodsInfoByIdResponse> getBulkById(@RequestBody @Valid GoodsInfoByIdRequest request){
        GoodsInfoByIdResponse response = null;
        BulkGoodsInfo goodsInfo;
        if(Objects.nonNull(request.getStoreId())) {
            goodsInfo = bulkGoodsInfoService.findByGoodsInfoIdAndStoreIdAndDelFlag(request.getGoodsInfoId(), request.getStoreId());
        }else {
            goodsInfo = bulkGoodsInfoService.findOne(request.getGoodsInfoId());
        }
        List<String> skuIds = new ArrayList<>();
        skuIds.add(goodsInfo.getGoodsInfoId());
//        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(skuIds);
        //设置库存
        List<GoodsWareStock> goodsWareStockList = goodsWareStockService.list(GoodsWareStockQueryRequest.builder()
                .goodsInfoId(request.getGoodsInfoId()).delFlag(DeleteFlag.NO).build());
        //如果不能匹配到仓,只匹配线下仓
        if (Objects.nonNull(request.getMatchWareHouseFlag())&&!request.getMatchWareHouseFlag()){
            List<WareHouseVO> wareHouses =wareHouseService.queryWareHousesByStoreid(goodsInfo.getStoreId(), WareHouseType.STORRWAREHOUSE);
            List<Long> wareIds= wareHouses.stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
            goodsWareStockList = goodsWareStockList.stream().filter(param -> wareIds.contains(param.getWareId())).collect(Collectors.toList());
        }
        if(CollectionUtils.isNotEmpty(goodsWareStockList)&&Objects.nonNull(goodsInfo)){
            BigDecimal sumStock = goodsWareStockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
            goodsInfo.setStock(sumStock.setScale(0,BigDecimal.ROUND_DOWN).longValue());

            //计算库存 加上虚拟库存 减去囤货数量
//            goodsInfoService.calGoodsInfoStock(goodsInfo,goodsNumsMap);
        }
        if(Objects.nonNull(goodsInfo)) {
            if(Objects.isNull(goodsInfo.getIsCanPile())){
                goodsInfo.setIsCanPile(DefaultFlag.NO);
            }
            response = new GoodsInfoByIdResponse();
            KsBeanUtil.copyPropertiesThird(goodsInfo, response);
            response.setStock(BigDecimal.valueOf(goodsInfo.getStock()));
        }
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> listByIds(GoodsInfoListByIdsRequest request) {
        List<BulkGoodsInfo> goodsInfos = bulkGoodsInfoService.findByIds(request.getGoodsInfoIds(), request.getWareId());
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convert(goodsInfos ,
                        GoodsInfoVO.class))
                .build());
    }


    /**
     * 分页查询商品sku列表
     *
     * @param request 商品sku分页条件查询结构 {@link GoodsInfoPageRequest}
     * @return 商品sku分页列表 {@link GoodsInfoPageResponse}
     */
    @Override
    public BaseResponse<GoodsInfoPageResponse> page(@RequestBody @Valid GoodsInfoPageRequest request){
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        GoodsInfoPageResponse response = new GoodsInfoPageResponse();
        response.setGoodsInfoPage(KsBeanUtil.convertPage(bulkGoodsInfoService.page(queryRequest), GoodsInfoVO.class));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoListByConditionResponse> listByCondition(GoodsInfoListByConditionRequest request) {
        BulkGoodsInfoQueryRequest queryRequest = new BulkGoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        GoodsInfoListByConditionResponse response = GoodsInfoListByConditionResponse.builder()
                .goodsInfos(KsBeanUtil.convert(bulkGoodsInfoService.findByParams(queryRequest), GoodsInfoVO.class))
                .build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoCountByConditionResponse> countByCondition(GoodsInfoCountByConditionRequest request) {
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        GoodsInfoCountByConditionResponse response = GoodsInfoCountByConditionResponse.builder()
                .count(bulkGoodsInfoService.count(queryRequest))
                .build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoListByIdsResponse> listGoodsInfoAndStcokByIds(GoodsInfoAndStockListByIdsRequest request) {
        return BaseResponse.success(GoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convert( bulkGoodsInfoService.findGoodsInfoAndStockByIds(request.getGoodsInfoIds(),request.getMatchWareHouseFlag()) ,
                        GoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GiftGoodsInfoListByIdsResponse> findGoodsInfoByIds(GoodsInfoListByIdsRequest request) {
        return BaseResponse.success(GiftGoodsInfoListByIdsResponse.builder()
                .goodsInfos(KsBeanUtil.convert( bulkGoodsInfoService.findGoodsInfoByIds(request.getGoodsInfoIds(),request.getMatchWareHouseFlag()) ,
                        GiftGoodsInfoVO.class))
                .build());
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsByMatchFlag(GoodsInfoViewByIdsRequest request) {
        GoodsInfoRequest infoRequest = new GoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        BulkGoodsInfoResponse infoResponse = bulkGoodsInfoService.findSkuByIdsAndMatchFlag(infoRequest);
        Map<String, BulkGoodsInfo> collect = infoResponse.getGoodsInfos().stream().collect(Collectors.toMap(BulkGoodsInfo::getGoodsInfoId, Function.identity(), (a, b) -> b));
        List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convert(infoResponse.getGoodsInfos(), GoodsInfoVO.class);
        for (GoodsInfoVO goodsInfoVO: goodsInfoVOS){
            goodsInfoVO.setStock(BigDecimal.valueOf(collect.get(goodsInfoVO.getGoodsInfoId()).getStock()));
        }

        return BaseResponse.success(
                GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(goodsInfoVOS)
                        .goodses(KsBeanUtil.convert(infoResponse.getGoodses(), GoodsVO.class)).build());
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByIdsByMatchFlagNoStock(GoodsInfoViewByIdsRequest request) {
        GoodsInfoRequest infoRequest = new GoodsInfoRequest();
        KsBeanUtil.copyPropertiesThird(request, infoRequest);
        BulkGoodsInfoResponse infoResponse = bulkGoodsInfoService.findSkuByIdsAndMatchFlagNoStock(infoRequest);
        Map<String, BulkGoodsInfo> collect = infoResponse.getGoodsInfos().stream().collect(Collectors.toMap(BulkGoodsInfo::getGoodsInfoId, Function.identity(), (a, b) -> b));
        List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convert(infoResponse.getGoodsInfos(), GoodsInfoVO.class);
        for (GoodsInfoVO goodsInfoVO: goodsInfoVOS){
            goodsInfoVO.setStock(BigDecimal.valueOf(collect.get(goodsInfoVO.getGoodsInfoId()).getStock()));
        }

        return BaseResponse.success(
                GoodsInfoViewByIdsResponse.builder()
                        .goodsInfos(goodsInfoVOS)
                        .goodses(KsBeanUtil.convert(infoResponse.getGoodses(), GoodsVO.class)).build());
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdsResponse> listViewByGoodsInfoImgIsNull() {
        return BaseResponse.success(GoodsInfoViewByIdsResponse.builder().goodsInfos(bulkGoodsInfoService.findByRetailGoodsInfoIsNull()).build());
    }

    @Override
    public BaseResponse<Map<String, List<GoodsImageVO>>> getGoodsImageOfMap(GoodsImagesBySpuIdsRequest request) {
        return BaseResponse.success(bulkGoodsInfoService.getGoodsImagesOfMap(request.getSpuIds()));
    }

    @Override
    public BaseResponse updateGoodsImg(GoodsInfoUpdateImgRequest request) {
        bulkGoodsInfoService.updateGoodsInfoImg(request.getGoodsInfoVOList());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GoodsInfoViewByIdResponse> getGoodsInfoByErpNo(GoodsInfoByErpNoRequest request) {
        GoodsInfoViewByIdResponse response = new GoodsInfoViewByIdResponse();
        GoodsInfoVO goodsInfoVO = KsBeanUtil.convert(bulkGoodsInfoService.findRetailGoodsInfoByErpNo(request.getErpNo()), GoodsInfoVO.class);
        if (Objects.nonNull(goodsInfoVO)) {
            response.setGoodsInfo(goodsInfoVO);
        }
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<DevanningGoodsInfoListResponse> listByParentId(@Valid DevanningGoodsInfoListByParentIdRequest request) {
        DevanningGoodsInfoQueryRequest queryRequest = DevanningGoodsInfoQueryRequest.builder().parentGoodsInfoId(request.getParentGoodsInfoId()).build();
        List<BulkGoodsInfo> bulkGoodsInfos = bulkGoodsInfoService.listByParentId(queryRequest);
        DevanningGoodsInfoListResponse response = DevanningGoodsInfoListResponse.builder()
                .devanningGoodsInfoVOS(KsBeanUtil.convertList(bulkGoodsInfos, DevanningGoodsInfoVO.class))
                .build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoByGoodsIdresponse> getByGoodsId(DistributionGoodsChangeRequest request) {
        List<BulkGoodsInfo> goodsInfos = bulkGoodsInfoService.queryBygoodsId(request.getGoodsId());
        List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convertList(goodsInfos, GoodsInfoVO.class);
        return BaseResponse.success(GoodsInfoByGoodsIdresponse.builder().goodsInfoVOList(goodsInfoVOS).build());
    }

}
