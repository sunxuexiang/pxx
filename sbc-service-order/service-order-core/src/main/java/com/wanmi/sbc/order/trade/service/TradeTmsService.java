package com.wanmi.sbc.order.trade.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaQueryProvider;
import com.wanmi.sbc.order.common.OrderCommonService;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Consignee;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.tms.api.RemoteTmsCarrierService;
import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.vo.TmsOrderAreaStoreVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @desc  
 * @author shiy  2023/11/13 9:53
*/
@Service
@Slf4j
public abstract class TradeTmsService {


    @Autowired
    protected RemoteTmsCarrierService remoteTmsCarrierService;

    @Autowired
    protected StoreQueryProvider storeQueryProvider;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    protected VerifyService verifyService;
    @Autowired
    private FreightTemplateDeliveryAreaQueryProvider freightTemplateDeliveryAreaQueryProvider;

    @Autowired
    private OrderCommonService orderCommonService;
    protected static String param_to_tms ="{}.{}入参[{}]";
    protected static String param_from_tms ="{}.{}出参[{}]";
    protected void checkTmsResponse(R r) {
        if (r == null) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"TMS系统连接超时");
        }
        if(r.getCode() != 200){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,r.getMsg());
        }
    }

    protected void resetConsigneeById(Consignee consignee) {
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(consignee.getId()).build()).getContext();
        if(response!=null){
            consignee.setProvinceId(response.getProvinceId());
            consignee.setCityId(response.getCityId());
            consignee.setAreaId(response.getAreaId());
            consignee.setTwonId(response.getTwonId());
        }
    }

    protected boolean isToStoreVillageFlag(Consignee consignee) {
        return freightTemplateDeliveryAreaQueryProvider.queryIsToStoreVillageFlag(consignee.getProvinceId(),consignee.getCityId(),consignee.getTwonId()).getContext();
        /*FreightTemplateDeliveryAreaVO platDeliveryToStoreCfg_10 = verifyService.getFreightTemplateDeliveryAreaVO(freightTemplateDeliveryType.DELIVERYTOSTORE_10);
        platDeliveryToStoreCfg_10 = verifyService.veriryFreightTemplateDeliveryAreaVO34(consignee.getAreaId(),consignee.getTwonId(), platDeliveryToStoreCfg_10);
        boolean villageFlag = platDeliveryToStoreCfg_10!=null;
        return villageFlag;*/
    }

    protected boolean selfOrder(Trade trade){
        return  orderCommonService.selfOrder(trade);
    }

    protected static Long getTradeSkuNumber(List<TradeItem> goodsList, List<TradeItem> giftList) {
        Long goodsTotalNum = 0L;
        if(CollectionUtils.isNotEmpty(goodsList)){
            goodsTotalNum += goodsList.stream().map(TradeItem::getNum).reduce(Long::sum).orElse(0L);
        }
        if(CollectionUtils.isNotEmpty(giftList)){
            goodsTotalNum += giftList.stream().map(TradeItem::getNum).reduce(Long::sum).orElse(0L);
        }
        return goodsTotalNum;
    }

    protected static BigDecimal getTradeSkuWeight(List<TradeItem> goodsList, List<TradeItem> giftList) {
        BigDecimal goodsWeight = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(goodsList)) {
            goodsWeight = goodsWeight.add(getWeightFromTradeItems(goodsList));
        }
        if (CollectionUtils.isNotEmpty(giftList)) {
            goodsWeight = goodsWeight.add(getWeightFromTradeItems(giftList));
        }
        return goodsWeight;
    }

    protected static BigDecimal getTradeSkuVolumn(List<TradeItem> goodsList, List<TradeItem> giftList) {
        BigDecimal goodsVolumn = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(goodsList)) {
            goodsVolumn = goodsVolumn.add(getVolumnFromTradeItems(goodsList));
        }
        if (CollectionUtils.isNotEmpty(giftList)) {
            goodsVolumn = goodsVolumn.add(getVolumnFromTradeItems(giftList));
        }
        return goodsVolumn;
    }

    protected static List<TmsOrderAreaStoreVO.GoodsInfoVO> getTmsGoodsInfoVoList(List<TradeItem> goodsList, List<TradeItem> giftList) {
        List<TmsOrderAreaStoreVO.GoodsInfoVO> goodsInfoVOList = new ArrayList<>(goodsList.size());
        if (CollectionUtils.isNotEmpty(goodsList)) {
            goodsInfoVOList.addAll(getTmsGoodsInfoVO(goodsList));
        }
        if (CollectionUtils.isNotEmpty(giftList)) {
            goodsInfoVOList.addAll(getTmsGoodsInfoVO(giftList));
        }
        return goodsInfoVOList;
    }

    private static BigDecimal getWeightFromTradeItems(List<TradeItem> goodsList) {
        BigDecimal goodsWeight = BigDecimal.ZERO;
        for (TradeItem goods : goodsList) {
            if (goods.getGoodsWeight() == null) {
                log.warn("商品skuId[{}]名字[{}]重量为空", goods.getSkuId(), goods.getSkuName());
                continue;
            }
            BigDecimal buyNum = BigDecimal.valueOf(goods.getNum());
            if (Objects.nonNull(goods.getDevanningId())) {
                buyNum = BigDecimal.valueOf(goods.getNum()).multiply(goods.getDivisorFlag());
                buyNum = buyNum.compareTo(BigDecimal.ONE) == -1 ? BigDecimal.ONE : buyNum;
            }
            goodsWeight = goodsWeight.add(buyNum.multiply(goods.getGoodsWeight()));
        }
        return goodsWeight;
    }

    private static BigDecimal getVolumnFromTradeItems(List<TradeItem> goodsList) {
        BigDecimal goodsVolumn = BigDecimal.ZERO;
        for (TradeItem goods : goodsList) {
            if (goods.getGoodsCubage() == null) {
                log.warn("商品skuId[{}]名字[{}]体积为空", goods.getSkuId(), goods.getSkuName());
                continue;
            }
            BigDecimal buyNum = BigDecimal.valueOf(goods.getNum());
            if (Objects.nonNull(goods.getDevanningId())) {
                buyNum = BigDecimal.valueOf(goods.getNum()).multiply(goods.getDivisorFlag());
                buyNum = buyNum.compareTo(BigDecimal.ONE) == -1 ? BigDecimal.ONE : buyNum;
            }
            goodsVolumn = goodsVolumn.add(buyNum.multiply(goods.getGoodsCubage()));
        }
        return goodsVolumn;
    }

    private static List<TmsOrderAreaStoreVO.GoodsInfoVO> getTmsGoodsInfoVO(List<TradeItem> goodsList) {
        List<TmsOrderAreaStoreVO.GoodsInfoVO> goodsInfoVOList = new ArrayList<>(goodsList.size());
        for (TradeItem goods : goodsList) {
            TmsOrderAreaStoreVO.GoodsInfoVO goodsInfoVO = new TmsOrderAreaStoreVO.GoodsInfoVO();
            if(goods.getGoodsCubage()!=null) {
                goodsInfoVO.setVolumn(goods.getGoodsCubage().doubleValue());
            }
            if(goods.getGoodsWeight()!=null) {
                goodsInfoVO.setWeight(goods.getGoodsWeight().doubleValue());
            }
            if(goods.getNum()!=null) {
                goodsInfoVO.setQuantity(goods.getNum());
            }
            goodsInfoVO.setSkuId(goods.getSkuId());
            goodsInfoVOList.add(goodsInfoVO);
        }
        return goodsInfoVOList;
    }

    protected BaseResponse<StoreInfoResponse> getStoreInfoById(StoreInfoByIdRequest storeInfoByIdRequest){
        return storeQueryProvider.getStoreInfoById(storeInfoByIdRequest);
    }

    protected BaseResponse<CustomerDeliveryAddressByIdResponse> getCustomerDeliveryAddressById(CustomerDeliveryAddressByIdRequest customerDeliveryAddressByIdRequest){
        return customerDeliveryAddressQueryProvider.getById(customerDeliveryAddressByIdRequest);
    }
}
