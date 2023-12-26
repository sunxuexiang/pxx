package com.wanmi.sbc.goods.provider.impl.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoFillGoodsStatusRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPriceModifyRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoFillGoodsStatusResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoModifyResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPriceModifyResponse;
import com.wanmi.sbc.goods.ares.GoodsAresService;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.info.request.GoodsInfoSaveRequest;
import com.wanmi.sbc.goods.info.request.RetailGoodsInfoSaveRequest;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.info.service.RetailGoodsInfoService;
import com.wanmi.sbc.goods.price.model.root.GoodsCustomerPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsLevelPrice;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 零售商品操作实现类
 * @author: XinJiang
 * @time: 2022/3/8 15:54
 */
@RestController
@Validated
public class RetailGoodsInfoController implements RetailGoodsInfoProvider {

    @Autowired
    private GoodsAresService goodsAresService;

    @Autowired
    private RetailGoodsInfoService retailGoodsInfoService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private WareHouseService wareHouseService;


    /**
     * 修改商品sku信息
     *
     * @param request 商品sku信息修改结构 {@link GoodsInfoModifyRequest}
     * @return 商品sku信息 {@link GoodsInfoModifyResponse}
     */
    @Override
    public BaseResponse<GoodsInfoModifyResponse> retailModify(@RequestBody @Valid GoodsInfoModifyRequest request){
        RetailGoodsInfo info = KsBeanUtil.convert(request.getGoodsInfo(), RetailGoodsInfo.class);
        RetailGoodsInfoSaveRequest saveRequest = new RetailGoodsInfoSaveRequest();
        saveRequest.setGoodsInfo(info);
        info = retailGoodsInfoService.edit(saveRequest);

//        Long stock = info.getGoodsWareStocks().stream().collect(Collectors.summingLong(GoodsWareStock::getStock));
//        if (info.getGoods().getAddedFlag() == AddedFlag.YES.toValue() && info.getStock() > 0) {
//            goodsService.sendMessage(info, stock);
//        }

        //ares埋点-商品-后台修改单个商品sku
        goodsAresService.dispatchFunction("editOneGoodsSku",new Object[]{info});

        GoodsInfoModifyResponse response = new GoodsInfoModifyResponse();
        KsBeanUtil.copyPropertiesThird(info, response);
        return BaseResponse.success(response);
    }



    /**
     * 修改商品sku设价信息
     *
     * @param request 商品sku设价信息修改结构 {@link GoodsInfoPriceModifyRequest}
     * @return 商品sku设价信息 {@link GoodsInfoPriceModifyResponse}
     */
    @Override
    public BaseResponse<GoodsInfoPriceModifyResponse> modifyPrice(@RequestBody @Valid GoodsInfoPriceModifyRequest
                                                                          request){
        RetailGoodsInfoSaveRequest saveRequest = new RetailGoodsInfoSaveRequest();
        RetailGoodsInfo info = new RetailGoodsInfo();
        KsBeanUtil.copyPropertiesThird(request.getGoodsInfo(), info);
        saveRequest.setGoodsInfo(info);
        //等级设价
        if(CollectionUtils.isNotEmpty(request.getGoodsLevelPrices())) {
            saveRequest.setGoodsLevelPrices(KsBeanUtil.convert(request.getGoodsLevelPrices(), GoodsLevelPrice.class));
        }
        //客户设价
        if(CollectionUtils.isNotEmpty(request.getGoodsCustomerPrices())) {
            saveRequest.setGoodsCustomerPrices(KsBeanUtil.convert(request.getGoodsCustomerPrices(), GoodsCustomerPrice.class));
        }
        //区间设价
        if(CollectionUtils.isNotEmpty(request.getGoodsIntervalPrices())) {
            saveRequest.setGoodsIntervalPrices(KsBeanUtil.convert(request.getGoodsIntervalPrices(), GoodsIntervalPrice.class));
        }

        info = retailGoodsInfoService.editPrice(saveRequest);
        //ares埋点-商品-后台修改单个商品sku
        goodsAresService.dispatchFunction("editOneGoodsSku",new Object[]{info});

        GoodsInfoPriceModifyResponse response = new GoodsInfoPriceModifyResponse();
        KsBeanUtil.copyPropertiesThird(info, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<GoodsInfoFillGoodsStatusResponse> fillGoodsStatus(GoodsInfoFillGoodsStatusRequest request) {
        List<RetailGoodsInfo> goodsInfoList =
                retailGoodsInfoService.fillGoodsStatus(KsBeanUtil.convert(request.getGoodsInfos(), RetailGoodsInfo.class));

//        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(goodsInfoList.stream()
//                .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList=goodsWareStockService.findByGoodsInfoIdIn(goodsInfoList.stream()
                .map(RetailGoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        List<Long> unOnline =new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(request.getMatchWareHouseFlag())&&!request.getMatchWareHouseFlag()){
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline=wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if(CollectionUtils.isNotEmpty(goodsWareStockVOList)){
            for (RetailGoodsInfo g : goodsInfoList) {
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
                    g.setStock(sumStock.setScale(0,BigDecimal.ROUND_DOWN).longValue());
                } else {
                    g.setStock(0L);
                }
                //计算库存 加上虚拟库存 减去囤货数量
//                goodsInfoService.calGoodsInfoStock(g,goodsNumsMap);
                if (Objects.nonNull(g.getMarketingId()) && Objects.nonNull(g.getPurchaseNum()) && g.getPurchaseNum() > 0){
                    g.setStock(g.getPurchaseNum());
                }
                if (g.getStock() <= 0){
                    g.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            }
        }
        return BaseResponse.success(GoodsInfoFillGoodsStatusResponse.builder()
                .goodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoVO.class)).build());
    }

    @Override
    public BaseResponse batchUpdateBatchNos(GoodsInfoBatchNosModifyRequest request) {
        return retailGoodsInfoService.batchUpdateGoodsInfoBatchNo(request.getGoodsBatchNoDTOS());
    }
}
