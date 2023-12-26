package com.wanmi.sbc.goods.provider.impl.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.info.GoodsCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.info.GoodsCountByConditionResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import com.wanmi.sbc.goods.info.model.root.*;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsQueryResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsQueryResponse;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.service.BulkGoodsInfoService;
import com.wanmi.sbc.goods.info.service.BulkGoodsService;
import com.wanmi.sbc.goods.info.service.RetailGoodsInfoService;
import com.wanmi.sbc.goods.info.service.RetailGoodsService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
public class BulkGoodsQueryController implements BulkGoodsQueryProvider {

    @Autowired
    private BulkGoodsService bulkGoodsService;

    @Autowired
    private BulkGoodsInfoService bulkGoodsInfoService;

    @Autowired
    private GoodsBrandService goodsBrandService;

    @Autowired
    private WareHouseService wareHouseService;

    @Override
    public BaseResponse<GoodsViewByIdResponse> getBulkViewById(@Valid GoodsViewByIdRequest request) {
        String goodsId = request.getGoodsId();
        BulkGoodsEditResponse goodsEditResponse = bulkGoodsService.findInfoById(goodsId,request.getWareId(),request.getMatchWareHouseFlag());
        // log.info("放回数据=============lc"+goodsEditResponse.getGoods().toString());
        GoodsViewByIdResponse goodsByIdResponse = KsBeanUtil.convert(goodsEditResponse, GoodsViewByIdResponse.class);
        return BaseResponse.success(goodsByIdResponse);
    }

    @Override
    public BaseResponse<GoodsPageResponse> bulkpage(@Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        goodsQueryRequest.setStoreIds(request.getStoreIds());
        BulkGoodsQueryResponse retailGoodsQueryResponse = bulkGoodsService.page(goodsQueryRequest);
        Page<BulkGoods> goodsPage = retailGoodsQueryResponse.getGoodsPage();
        GoodsPageResponse response = new GoodsPageResponse();
        MicroServicePage<GoodsVO> microServicePage = new MicroServicePage<>();
        if(Objects.nonNull(goodsPage) && CollectionUtils.isNotEmpty(goodsPage.getContent())) {
            response.setGoodsBrandList(KsBeanUtil.convert(retailGoodsQueryResponse.getGoodsBrandList(), GoodsBrandVO.class));
            response.setGoodsCateList(KsBeanUtil.convert(retailGoodsQueryResponse.getGoodsCateList(), GoodsCateVO.class));
            response.setGoodsInfoList(KsBeanUtil.convert(retailGoodsQueryResponse.getGoodsInfoList(), GoodsInfoVO.class));
            response.setGoodsInfoSpecDetails(KsBeanUtil.convert(retailGoodsQueryResponse.getGoodsInfoSpecDetails(), GoodsInfoSpecDetailRelVO.class));
            response.setImportStandard(retailGoodsQueryResponse.getImportStandard());
            microServicePage = KsBeanUtil.convertPage(goodsPage, GoodsVO.class);
            //赋值仓库名称
            List<WareHouse> list = wareHouseService.list(new WareHouseQueryRequest());
            List<GoodsVO> content = microServicePage.getContent();
            if(CollectionUtils.isNotEmpty(content)){
                content.forEach(var->{
                    var.setWareName(list.stream().filter(wareHouse -> wareHouse.getWareId().equals(var.getWareId())).findFirst().get().getWareName());
                });
            }
        }
        response.setGoodsPage(microServicePage);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsByIdResponse> getBulkById(@Valid GoodsByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(bulkGoodsService.getRetailGoodsById(request.getGoodsId()),GoodsByIdResponse.class));
    }

    @Override
    public BaseResponse<GoodsListByIdsResponse> listBulkByIds(@Valid GoodsListByIdsRequest request) {
        List<BulkGoods> goodsList = bulkGoodsService.listRetailGoodsByGoodsIds(request.getGoodsIds());
        if(CollectionUtils.isEmpty(goodsList)){
            return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(Collections.emptyList()).build());
        }
        List<GoodsVO> voList = KsBeanUtil.convert(goodsList, GoodsVO.class);
        return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(voList).build());
    }

    @Override
    public BaseResponse<GoodsByConditionResponse> bulkListByCondition(@Valid GoodsByConditionRequest goodsByConditionRequest) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(goodsByConditionRequest,GoodsQueryRequest.class);
        List<BulkGoods> goodsList = bulkGoodsService.findAll(goodsQueryRequest);
        if (CollectionUtils.isEmpty(goodsList)){
            return BaseResponse.success(new GoodsByConditionResponse(Collections.EMPTY_LIST));
        }
        List<GoodsVO> goodsVOList = KsBeanUtil.convert(goodsList,GoodsVO.class);
        return BaseResponse.success(new GoodsByConditionResponse(goodsVOList));
    }


    @Override
    public BaseResponse<GoodsListByIdsResponse> bulkListByGoodsIdsNoValid(GoodsListByIdsRequest request) {

        if(CollectionUtils.isEmpty(request.getGoodsIds())){
            return BaseResponse.success(null);
        }
        List<BulkGoods> goods = bulkGoodsService.listByGoodsIdsNoValid(request.getGoodsIds());

        if(CollectionUtils.isEmpty(goods)){
            return BaseResponse.success(null);
        }
        List<GoodsVO> goodsVOS = KsBeanUtil.convertList(goods, GoodsVO.class);
        return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(goodsVOS).build());
    }

    @Override
    public BaseResponse<GoodsCountByConditionResponse> countByCondition(GoodsCountByConditionRequest goodsByConditionRequest) {
        long count = bulkGoodsService.countByCondition(KsBeanUtil.convert(goodsByConditionRequest, GoodsQueryRequest.class));
        return BaseResponse.success(GoodsCountByConditionResponse.builder().count(count).build());
    }

    @Override
    public BaseResponse<GoodsPropDetailRelByIdsResponse> getRefByGoodIds(GoodsPropDetailRelByIdsRequest request) {
        List<String> goodsIds = request.getGoodsIds();
        List<GoodsPropDetailRel> goodsPropDetailRelList = bulkGoodsService.findRefByGoodIds(goodsIds);

        GoodsPropDetailRelByIdsResponse response = new GoodsPropDetailRelByIdsResponse();
        List<GoodsPropDetailRelVO> goodsPropDetailRelVOList =
                KsBeanUtil.convertList(goodsPropDetailRelList, GoodsPropDetailRelVO.class);
        response.setGoodsPropDetailRelVOList(goodsPropDetailRelVOList);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsListByIdsResponse> listByProviderGoodsId(GoodsListByIdsRequest request) {
        List<BulkGoods> goodsList = bulkGoodsService.listByProviderGoodsIds(request.getGoodsIds());
        if(CollectionUtils.isEmpty(goodsList)){
            return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(Collections.emptyList()).build());
        }
        List<GoodsVO> voList = KsBeanUtil.convert(goodsList, GoodsVO.class);
        return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(voList).build());
    }

    @Override
    public BaseResponse<GoodsListResponse> listByErp() {
        List<BulkGoodsInfo> allByErp = bulkGoodsInfoService.findAllByErp();
        List<String> goodsIds = allByErp.stream()
                .map(BulkGoodsInfo::getGoodsId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> goodsErpMap = allByErp.stream().collect(Collectors.toMap(BulkGoodsInfo::getGoodsId, BulkGoodsInfo::getErpGoodsInfoNo));

        Map<String,Integer> goodsInfoTypeMap=allByErp.stream().collect(Collectors.toMap(BulkGoodsInfo::getGoodsId, BulkGoodsInfo::getGoodsInfoType));
        List<BulkGoods> goods = bulkGoodsService.listByGoodsIdsNoValid(goodsIds);
        List<Long> brandIds = goods.stream()
                .map(BulkGoods::getBrandId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, GoodsBrand> brandMap = goodsBrandService.query(GoodsBrandQueryRequest.builder()
                .brandIds(brandIds)
                .delFlag(0)
                .build()).stream()
                .collect(Collectors.toMap(GoodsBrand::getBrandId, Function.identity()));
        List<GoodsVO> voList = KsBeanUtil.convert(goods, GoodsVO.class);
        voList.forEach(vo -> {
            if (Objects.nonNull(vo.getBrandId()) && Objects.nonNull(brandMap.get(vo.getBrandId()))) {
                vo.setBrandSeqNum(brandMap.get(vo.getBrandId()).getBrandSeqNum()
                );
            }
            vo.setErpNo(goodsErpMap.get(vo.getGoodsId()));
            vo.setGoodsInfoType(goodsInfoTypeMap.get(vo.getGoodsId()));
        });
        return BaseResponse.success(GoodsListResponse.builder().goodsVOList(voList).build());
    }



}
