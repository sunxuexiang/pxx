package com.wanmi.sbc.goods.provider.impl.standard;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.standard.*;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.standard.*;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.request.StandardQueryRequest;
import com.wanmi.sbc.goods.standard.response.StandardEditResponse;
import com.wanmi.sbc.goods.standard.response.StandardQueryResponse;
import com.wanmi.sbc.goods.standard.service.StandardGoodsService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseWhereCriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-08 15:27
 */
@Validated
@RestController
@Slf4j
public class StandardGoodsQueryController implements StandardGoodsQueryProvider {

    @Autowired
    private StandardGoodsService standardGoodsService;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    /**
     * @param request 分页查询商品库 {@link StandardGoodsPageRequest}
     * @return
     */
    @Override
    public BaseResponse<StandardGoodsPageResponse> page(@RequestBody @Valid StandardGoodsPageRequest request) {
        StandardQueryRequest standardQueryRequest = KsBeanUtil.convert(request, StandardQueryRequest.class);
        StandardQueryResponse queryResponse = standardGoodsService.page(standardQueryRequest);
        StandardGoodsPageResponse pageResponse = new StandardGoodsPageResponse();
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsCateList())) {
            pageResponse.setGoodsCateList(KsBeanUtil.convert(queryResponse.getGoodsCateList(), GoodsCateVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getGoodsBrandList())) {
            pageResponse.setGoodsBrandList(KsBeanUtil.convert(queryResponse.getGoodsBrandList(), GoodsBrandVO.class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getStandardSkuList())) {
            pageResponse.setStandardSkuList(KsBeanUtil.convert(queryResponse.getStandardSkuList(), StandardSkuVO
                    .class));
        }
        if (CollectionUtils.isNotEmpty(queryResponse.getStandardSkuSpecDetails())) {
            pageResponse.setStandardSkuSpecDetails(KsBeanUtil.convert(queryResponse.getStandardSkuSpecDetails(), StandardSkuSpecDetailRelVO
                    .class));
        }

        pageResponse.setUsedStandard(queryResponse.getUsedStandard());
        pageResponse.setStandardGoodsPage(KsBeanUtil.convertPage(queryResponse.getStandardGoodsPage(), StandardGoodsVO.class));

        MicroServicePage<StandardGoodsVO> standardGoodsPage = pageResponse.getStandardGoodsPage();
        if (CollectionUtils.isNotEmpty(standardGoodsPage.getContent())){
            List<WareHouse> wareHouseList = wareHouseRepository.findAll(WareHouseWhereCriteriaBuilder.build(WareHouseQueryRequest.builder().build()));
            standardGoodsPage.getContent().forEach(standardGoodsVO -> {
                if(Objects.nonNull(standardGoodsVO.getWareId())){
                    standardGoodsVO.setWareName(wareHouseList.stream().filter(wareHouse -> wareHouse.getWareId().equals(standardGoodsVO.getWareId())).findFirst().get().getWareName());
                }
            });
        }
        return BaseResponse.success(pageResponse);
    }

    /**
     * @param request 根据ID查询商品库 {@link StandardGoodsByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<StandardGoodsByIdResponse> getById(@RequestBody @Valid StandardGoodsByIdRequest request) {
        StandardEditResponse standardEditResponse = standardGoodsService.findInfoById(request.getGoodsId());
        StandardGoodsByIdResponse response = KsBeanUtil.convert(standardEditResponse, StandardGoodsByIdResponse.class);
        StandardGoods standardGoods = standardEditResponse.getGoods();
        if (Objects.nonNull(standardGoods) && Objects.nonNull(standardGoods.getCateId())){
            GoodsCate goodsCate = goodsCateService.findById(standardGoods.getCateId());
            response.getGoods().setCateName(Objects.nonNull(goodsCate) ? goodsCate.getCateName() : "");
        }
        return BaseResponse.success(response);
    }

    /**
     * @param request 列出已被导入的商品库ID {@link StandardGoodsGetUsedStandardRequest}
     * @return
     */
    @Override
    public BaseResponse<StandardGoodsGetUsedStandardResponse> getUsedStandard(@RequestBody @Valid StandardGoodsGetUsedStandardRequest request) {
        return BaseResponse.success(StandardGoodsGetUsedStandardResponse.builder()
                .standardIds(standardGoodsService.getUsedStandard(request.getStandardIds(), request.getStoreIds()))
                .build());
    }

    /**
     * @param request 列出已被导入的商品ID {@link StandardGoodsGetUsedGoodsRequest}
     * @return {@link StandardGoodsGetUsedGoodsResponse}
     */
    @Override
    public BaseResponse<StandardGoodsGetUsedGoodsResponse> getUsedGoods(@RequestBody @Valid StandardGoodsGetUsedGoodsRequest request) {
        return BaseResponse.success(StandardGoodsGetUsedGoodsResponse.builder()
                .goodsIds(standardGoodsService.getUsedGoods(request.getGoodsIds())).build());
    }

    /**
     * 列出已被导入的SKUID
     * @param request
     * @return
     */
    @Override
    public BaseResponse<StandardGoodsListUsedGoodsIdResponse> listUsedGoodsId(@RequestBody @Valid StandardGoodsListUsedGoodsIdRequest request) {
        List<String> goodsIds = standardGoodsService.getUsedGoodsId(request.getStandardIds(), request.getStoreIds());
        return BaseResponse.success(new StandardGoodsListUsedGoodsIdResponse(goodsIds));
    }

    @Override
    public BaseResponse<StandardGoodsIdsQueryResponse> queryGoodsIds(@Valid StandardGoodsPageRequest request) {
        StandardQueryRequest standardQueryRequest = KsBeanUtil.convert(request, StandardQueryRequest.class);
        List<String> strings = standardGoodsService.queryGoodsIds(standardQueryRequest);
        return  BaseResponse.success(new StandardGoodsIdsQueryResponse(strings));
    }

    @Override
    public BaseResponse<StandardGoodsGetMapResponse> getStandardGoodsIds(@Valid StandardGoodsGetUsedGoodsRequest request) {
        return BaseResponse.success(StandardGoodsGetMapResponse.builder()
                .goodsIds(standardGoodsService.getStandardGoodsIds(request.getGoodsIds())).build());
    }
}
