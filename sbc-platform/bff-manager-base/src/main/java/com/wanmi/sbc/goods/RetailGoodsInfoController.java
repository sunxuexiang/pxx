package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @description: 零售商品sku服务
 * @author: XinJiang
 * @time: 2022/3/8 16:23
 */
@Api(tags = "RetailGoodsInfoController", description = "零售商品sku服务 Api")
@RestController
@RequestMapping(value = "/retail")
public class RetailGoodsInfoController {



    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private RetailGoodsInfoProvider retailGoodsInfoProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;


    /**
     * 获取商品SKU详情信息
     *
     * @param goodsInfoId 商品编号
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品SKU详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsInfoId", value = "sku Id", required = true)
    @RequestMapping(value = "/goods/sku/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse<GoodsInfoViewByIdResponse> info(@PathVariable String goodsInfoId) {
        return retailGoodsInfoQueryProvider.getViewRetailById(GoodsInfoViewByIdRequest.builder().goodsInfoId(goodsInfoId).build());
    }



    /**
     * 编辑商品SKU
     */
    @ApiOperation(value = "编辑商品SKU")
    @RequestMapping(value = "/goods/sku", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(@RequestBody GoodsInfoModifyRequest saveRequest) {
        if (saveRequest.getGoodsInfo() == null || saveRequest.getGoodsInfo().getGoodsInfoId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsInfoByIdResponse goodsInfoVo = retailGoodsInfoQueryProvider.getRetailById(
                GoodsInfoByIdRequest.builder().goodsInfoId(saveRequest.getGoodsInfo().getGoodsInfoId()).build()).getContext();
        if(StringUtils.isNotEmpty(goodsInfoVo.getProviderGoodsInfoId())){
            GoodsInfoByIdResponse providerGoodsInfoVo = retailGoodsInfoQueryProvider.getRetailById(
                    GoodsInfoByIdRequest.builder().goodsInfoId(goodsInfoVo.getProviderGoodsInfoId()).build()).getContext();
            if(providerGoodsInfoVo.getAddedFlag().equals(AddedFlag.NO.toValue())){
                throw new SbcRuntimeException("K-030506");
            }
        }

        //校验关联本品是否重复
        if (Objects.nonNull(saveRequest.getGoodsInfo().getChoseProductSkuId())) {
            List<GoodsInfoVO> goodsInfoVOList = retailGoodsInfoQueryProvider.page(GoodsInfoPageRequest.builder()
                    .notGoodsInfoId(saveRequest.getGoodsInfo().getGoodsInfoId())
                    .choseProductSkuId(saveRequest.getGoodsInfo().getChoseProductSkuId())
                    .build()).getContext().getGoodsInfoPage().getContent();
            if (CollectionUtils.isNotEmpty(goodsInfoVOList)) {
                throw new SbcRuntimeException("K-030570","关联本品商品重复，请重新选择");
            }
        }

        retailGoodsInfoProvider.retailModify(saveRequest);

        //持化至ES 零售商品无ES
        esRetailGoodsInfoElasticService.initEsRetailGoodsInfo(EsGoodsInfoRequest.builder()
                .skuIds(Collections.singletonList(saveRequest.getGoodsInfo().getGoodsInfoId())).build());

        operateLogMQUtil.convertAndSend("商品", "编辑商品", "编辑商品：SKU编码" + saveRequest.getGoodsInfo().getGoodsInfoNo());

        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 编辑商品SKU
     */
    @ApiOperation(value = "编辑商品SKU")
    @RequestMapping(value = "/goods/sku/price", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> price(@RequestBody GoodsInfoPriceModifyRequest saveRequest) {
        if (saveRequest.getGoodsInfo() == null || saveRequest.getGoodsInfo().getGoodsInfoId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        retailGoodsInfoProvider.modifyPrice(saveRequest);

        //持化至ES 零售商品无ES
        esRetailGoodsInfoElasticService.initEsRetailGoodsInfo(EsGoodsInfoRequest.builder()
                .skuIds(Collections.singletonList(saveRequest.getGoodsInfo().getGoodsInfoId())).build());

        operateLogMQUtil.convertAndSend("商品", "设价",
                "设价：SKU编码" + saveRequest.getGoodsInfo().getGoodsInfoNo());
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


}
