package com.wanmi.sbc.goods.provider.impl.price;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CommonLevelVO;
import com.wanmi.sbc.goods.api.provider.price.GoodsIntervalPriceProvider;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceByGoodsAndSkuRequest;
import com.wanmi.sbc.goods.api.request.price.GoodsIntervalPriceRequest;
import com.wanmi.sbc.goods.api.request.price.ValidGoodsIntervalPriceByCustomerIdRequest;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByGoodsAndSkuResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.GoodsPriceType;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsIntervalPriceVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.service.GoodsIntervalPriceService;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
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
 * com.wanmi.sbc.goods.provider.impl.intervalprice.GoodsIntervalPriceController
 *
 * @author lipeng
 * @dateTime 2018/11/7 下午3:20
 */
@RestController
@Validated
public class GoodsIntervalPriceController implements GoodsIntervalPriceProvider {

    @Autowired
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private WareHouseService wareHouseService;

    /**
     * 为每个SKU填充区间价范围值intervalMinPrice,intervalMaxPrice,intervalPriceIds，并返回相应区间价结果
     *
     * @param request {@link GoodsIntervalPriceRequest}
     * @return 区间价结果 {@link GoodsIntervalPriceResponse}
     */
    @Override
    public BaseResponse<GoodsIntervalPriceResponse> put(
            @RequestBody @Valid GoodsIntervalPriceRequest request) {
        GoodsIntervalPriceResponse goodsIntervalPriceResponse = new GoodsIntervalPriceResponse();
        List<GoodsInfo> goodsInfoList = KsBeanUtil.convert(request.getGoodsInfoDTOList(), GoodsInfo.class);

        List<GoodsIntervalPrice> goodsIntervalPriceList = goodsIntervalPriceService.putIntervalPrice(goodsInfoList, null);
        goodsIntervalPriceResponse.setGoodsInfoVOList(KsBeanUtil.convert(goodsInfoList, GoodsInfoVO.class));
        if (CollectionUtils.isEmpty(goodsIntervalPriceList)) {
            return BaseResponse.success(goodsIntervalPriceResponse);
        }
        goodsIntervalPriceResponse.setGoodsIntervalPriceVOList(KsBeanUtil.convert(goodsIntervalPriceList, GoodsIntervalPriceVO.class));
        return BaseResponse.success(goodsIntervalPriceResponse);
    }

    /**
     * 为每个SKU填充区间价范围值intervalMinPrice,intervalMaxPrice,intervalPriceIds，并返回相应区间价结果
     *
     * @param request {@link GoodsIntervalPriceByCustomerIdRequest}
     * @return 区间价结果 {@link GoodsIntervalPriceResponse}
     */
    @Override
    public BaseResponse<GoodsIntervalPriceByCustomerIdResponse> putByCustomerId(
            @RequestBody @Valid GoodsIntervalPriceByCustomerIdRequest request) {
        GoodsIntervalPriceByCustomerIdResponse goodsIntervalPriceResponse = new GoodsIntervalPriceByCustomerIdResponse();
        List<GoodsInfo> goodsInfoList = KsBeanUtil.convert(request.getGoodsInfoDTOList(), GoodsInfo.class);

        List<Long> storeIds = goodsInfoList.stream()
                .filter(goodsInfo -> Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goodsInfo.getPriceType()))
                .map(GoodsInfo::getStoreId)
                .filter(id -> id != null && id > 0)
                .distinct().collect(Collectors.toList());
        Map<Long, CommonLevelVO> levelMap = goodsIntervalPriceService.getLevelMap(request.getCustomerId(), storeIds);
        List<GoodsIntervalPrice> goodsIntervalPriceList =
                goodsIntervalPriceService.putIntervalPrice(goodsInfoList, levelMap);
        goodsIntervalPriceResponse.setGoodsInfoVOList(KsBeanUtil.convert(goodsInfoList, GoodsInfoVO.class));
        if (CollectionUtils.isEmpty(goodsIntervalPriceList)) {
            return BaseResponse.success(goodsIntervalPriceResponse);
        }
        goodsIntervalPriceResponse.setGoodsIntervalPriceVOList(KsBeanUtil.convert(goodsIntervalPriceList, GoodsIntervalPriceVO.class));
        return BaseResponse.success(goodsIntervalPriceResponse);
    }

    /**
     * 为每个SKU和SPU填充区间价范围值intervalMinPrice,intervalMaxPrice,intervalPriceIds，并返回相应区间价结果
     *
     * @param request {@link GoodsIntervalPriceByGoodsAndSkuRequest}
     * @return 区间价结果 {@link GoodsIntervalPriceByGoodsAndSkuResponse}
     */
    @Override
    public BaseResponse<GoodsIntervalPriceByGoodsAndSkuResponse> putGoodsAndSku (
            @RequestBody @Valid GoodsIntervalPriceByGoodsAndSkuRequest request) {
        GoodsIntervalPriceByGoodsAndSkuResponse response = new GoodsIntervalPriceByGoodsAndSkuResponse();
        List<GoodsInfo> goodsInfoList = KsBeanUtil.convert(request.getGoodsInfoDTOList(), GoodsInfo.class);
        List<Goods> goods = KsBeanUtil.convert(request.getGoodsDTOList(), Goods.class);
        if(CollectionUtils.isEmpty(goodsInfoList) && CollectionUtils.isEmpty(goods)){
            response.setGoodsInfoVOList(Collections.emptyList());
            response.setGoodsDTOList(Collections.emptyList());
            response.setGoodsIntervalPriceVOList(Collections.emptyList());
            return BaseResponse.success(response);
        }

        //提取店铺
        Map<Long, CommonLevelVO> levelMap = null;
        if(StringUtils.isNotBlank(request.getCustomerId())) {
            List<Long> storeIds = goods.stream()
                    .filter(spu -> Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(spu.getPriceType()))
                    .map(Goods::getStoreId)
                    .filter(id -> id != null && id > 0)
                    .distinct().collect(Collectors.toList());
            levelMap = goodsIntervalPriceService.getLevelMap(request.getCustomerId(), storeIds);
        }

        List<GoodsIntervalPrice> goodsIntervalPriceList = goodsIntervalPriceService.putIntervalPrice(goodsInfoList, levelMap);
        goodsIntervalPriceList.addAll(goodsIntervalPriceService.putGoodsIntervalPrice(goods, levelMap));

        response.setGoodsInfoVOList(KsBeanUtil.convert(goodsInfoList, GoodsInfoVO.class));
        response.setGoodsDTOList(KsBeanUtil.convert(goods, GoodsVO.class));
        if (CollectionUtils.isEmpty(goodsIntervalPriceList)) {
            return BaseResponse.success(response);
        }
        response.setGoodsIntervalPriceVOList(KsBeanUtil.convert(goodsIntervalPriceList, GoodsIntervalPriceVO.class));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsIntervalPriceByCustomerIdResponse> putValidGoodsInfoByCustomerId(@RequestBody @Valid ValidGoodsIntervalPriceByCustomerIdRequest request) {
        List<GoodsInfo> goodsInfoList =
                goodsInfoService.fillGoodsStatus(KsBeanUtil.convert(request.getGoodsInfoDTOList(), GoodsInfo.class));

        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.findByGoodsInfoIdIn(
                goodsInfoList.stream()
                        .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        List<Long> unOnline =new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(request.getMatchWareHouseFlag())&&!request.getMatchWareHouseFlag()){
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (GoodsInfo g : goodsInfoList) {
                List<GoodsWareStock> stockList;
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }

                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    g.setStock(sumStock);
                } else {
                    g.setStock(BigDecimal.ZERO);
                }
            }
        }
        List<GoodsInfoDTO> goodsInfoDTOList = KsBeanUtil.convert(goodsInfoList, GoodsInfoDTO.class);
        return this.putByCustomerId(GoodsIntervalPriceByCustomerIdRequest.builder()
                .goodsInfoDTOList(goodsInfoDTOList)
                .customerId(request.getCustomerId())
                .build());
    }
}
