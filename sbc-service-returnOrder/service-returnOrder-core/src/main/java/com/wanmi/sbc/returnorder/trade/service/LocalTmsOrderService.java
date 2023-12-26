package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.returnorder.bean.enums.PayState;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Consignee;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Logistics;
import com.wanmi.sbc.returnorder.trade.model.entity.value.ShippingItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.reponse.TradeFreightResponse;
import com.wanmi.sbc.returnorder.trade.request.TradeParams;
import com.wanmi.sbc.tms.api.RemoteTmsOrderService;
import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.dto.TmsCarrierDTO;
import com.wanmi.sbc.tms.api.domain.dto.TmsOrderAmountDTO;
import com.wanmi.sbc.tms.api.domain.dto.TmsOrderThirdDeliveryDTO;
import com.wanmi.sbc.tms.api.domain.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @desc  
 * @author shiy  2023/11/13 9:53
*/
@Service
@Slf4j
public class LocalTmsOrderService extends TradeTmsService{

    private static final String REMOTE_TMS_ORDER_SERVICE = "remoteTmsOrderService";

    @Autowired
    private RemoteTmsOrderService remoteTmsOrderService;

    private TmsOrderBatchSaveVO createTmsOrder(Trade trade, CustomerDeliveryAddressByIdResponse customerDelivery) {
        if(trade.getTradeState().getPayState()!=PayState.PAID){
            log.info("当前订单[{}]状态不支持创建TMS",trade.getId());
            return null;
        }
        TmsOrderBatchSaveVO tmsOrderBatchSaveVO = new TmsOrderBatchSaveVO();
        TmsOrderSaveVO tmsOrderSaveVO = getTmsOrderSaveVO(trade, customerDelivery);
        TmsOrderTradeSaveVO tmsOrderTradeSaveVO = getTmsOrderTradeSaveVO(trade);
        List<TmsOrderTradeItemSaveVO> items = new ArrayList<>();
        if(CollectionUtils.isNotEmpty( trade.getTradeItems())) {
            trade.getTradeItems().forEach(tradeItem -> {
                items.add(getTmsOrderTradeItemSaveVO(tradeItem));
            });
        }
        if(CollectionUtils.isNotEmpty(trade.getGifts())) {
            trade.getGifts().forEach(tradeItem -> {
                items.add(getTmsOrderTradeItemSaveVO(tradeItem));
            });
        }
        tmsOrderBatchSaveVO.setTmsOrder(tmsOrderSaveVO);
        tmsOrderBatchSaveVO.setTradeOrder(tmsOrderTradeSaveVO);
        tmsOrderBatchSaveVO.setItems(items);
        return tmsOrderBatchSaveVO;
    }

    private static TmsOrderTradeItemSaveVO getTmsOrderTradeItemSaveVO(TradeItem tradeItem) {
        TmsOrderTradeItemSaveVO tmsOrderTradeItemSaveVO = new TmsOrderTradeItemSaveVO();
        tmsOrderTradeItemSaveVO.setSpuId(tradeItem.getSpuId());
        tmsOrderTradeItemSaveVO.setSkuId(tradeItem.getSkuId());
        tmsOrderTradeItemSaveVO.setSkuName(tradeItem.getSkuName());
        tmsOrderTradeItemSaveVO.setGoodsType(Objects.toString(tradeItem.getGoodsInfoType()));
        tmsOrderTradeItemSaveVO.setUnit(tradeItem.getUnit());
        tmsOrderTradeItemSaveVO.setCargoValue(tradeItem.getPrice());
        //tmsOrderTradeItemSaveVO.setCargoBarcode(tradeItem.getba);
        tmsOrderTradeItemSaveVO.setQuantity(tradeItem.getBNum().intValue());
        tmsOrderTradeItemSaveVO.setVolume(tradeItem.getGoodsCubage());
        tmsOrderTradeItemSaveVO.setWeight(tradeItem.getGoodsWeight());
        tmsOrderTradeItemSaveVO.setRemark(Constants.EMPTY_STR);
        tmsOrderTradeItemSaveVO.setSpec(tradeItem.getGoodsSubtitle());
        tmsOrderTradeItemSaveVO.setSkuNo(tradeItem.getSkuNo());
        return tmsOrderTradeItemSaveVO;
    }
    private static TmsOrderTradeSaveVO getTmsOrderTradeSaveVO(Trade trade) {
        TmsOrderTradeSaveVO tmsOrderTradeSaveVO = new TmsOrderTradeSaveVO();
        tmsOrderTradeSaveVO.setTradeOrderId(trade.getId());
        tmsOrderTradeSaveVO.setTradeParentOrderId(trade.getParentId());
        tmsOrderTradeSaveVO.setStoreId(trade.getSupplier().getStoreId());
        tmsOrderTradeSaveVO.setStoreName(trade.getSupplier().getStoreName());
        tmsOrderTradeSaveVO.setTradeAmount(trade.getTradePrice().getGoodsPrice());
        Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
        tmsOrderTradeSaveVO.setGoodsTotalNum(goodsTotalNum.intValue());
        //tmsOrderTradeSaveVO.setTradeType(0);
        tmsOrderTradeSaveVO.setTradeStatus(trade.getTradeState().getFlowState().getDescription());
        return tmsOrderTradeSaveVO;
    }
    private TmsOrderSaveVO getTmsOrderSaveVO(Trade trade, CustomerDeliveryAddressByIdResponse customerDelivery) {
        TmsOrderSaveVO tmsOrderSaveVO = new TmsOrderSaveVO();
        tmsOrderSaveVO.setReceiverId(customerDelivery.getCustomerId());
        tmsOrderSaveVO.setReceiverProvinceCode(Objects.toString(customerDelivery.getProvinceId()));
        tmsOrderSaveVO.setReceiverCityCode(Objects.toString(customerDelivery.getCityId()));
        tmsOrderSaveVO.setReceiverDistrictCode(Objects.toString(customerDelivery.getAreaId()));
        tmsOrderSaveVO.setReceiverStreetCode(Objects.toString(customerDelivery.getTwonId()));
        tmsOrderSaveVO.setReceiverAddress(customerDelivery.getDeliveryAddress());
        tmsOrderSaveVO.setReceiverName(customerDelivery.getConsigneeName());
        tmsOrderSaveVO.setReceiverPhone(customerDelivery.getConsigneeNumber());
        tmsOrderSaveVO.setSenderId(trade.getTradeDelivers().get(0).getLogistics().getLogisticCompanyId());
        StoreInfoResponse storeVO = getStoreInfoById(StoreInfoByIdRequest.builder().storeId(trade.getSupplier().getStoreId()).build()).getContext();
        if(storeVO!=null) {
            tmsOrderSaveVO.setSenderProvinceCode(Objects.toString(storeVO.getProvinceId()));
            tmsOrderSaveVO.setSenderCityCode(Objects.toString(storeVO.getCityId()));
            tmsOrderSaveVO.setSenderDistrictCode(Objects.toString(storeVO.getAreaId()));
            tmsOrderSaveVO.setSenderStreetCode(Constants.EMPTY_STR);
            tmsOrderSaveVO.setSenderAddress(storeVO.getAddressDetail());
            tmsOrderSaveVO.setSenderName(storeVO.getContactPerson());
            tmsOrderSaveVO.setSenderPhone(storeVO.getContactMobile());
        }
        tmsOrderSaveVO.setShipmentSiteId(trade.getTradeDelivers().get(0).getLogistics().getShipmentSiteId());
        tmsOrderSaveVO.setShipmentSiteName(trade.getTradeDelivers().get(0).getLogistics().getShipmentSiteName());
        tmsOrderSaveVO.setPickupSiteId(Objects.isNull(trade.getNetWorkVO())?Constants.EMPTY_STR:Objects.toString(trade.getNetWorkVO().getNetworkId()));
        tmsOrderSaveVO.setPickupSiteName(Objects.isNull(trade.getNetWorkVO())?Constants.EMPTY_STR:Objects.toString(trade.getNetWorkVO().getNetworkName()));
        tmsOrderSaveVO.setPaymentMethod(0);
        tmsOrderSaveVO.setPaymentStatus(1);
        tmsOrderSaveVO.setAmount(trade.getTradePrice().getDeliveryPrice());
        Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
        tmsOrderSaveVO.setQuantity(goodsTotalNum.intValue());
        /*tmsOrderSaveVO.setEstimatedArrivalTime(LocalDateTime.now());
        tmsOrderSaveVO.setDistance(new BigDecimal("0"));*/
        tmsOrderSaveVO.setVillageFlag(trade.getConsignee().getVillageFlag()?1:0);
        //tmsOrderSaveVO.setRemark(Constants.EMPTY_STR);
        return tmsOrderSaveVO;
    }

    private static TmsOrderTradeStatusGoodsVO getTmsOrderTradeStatusGoodsVO(TradeItem item) {
        TmsOrderTradeStatusGoodsVO goodsVO = new TmsOrderTradeStatusGoodsVO();
        goodsVO.setSkuId(item.getSkuId());
        goodsVO.setDeliverNum(item.getDeliveredNum().intValue());
        return goodsVO;
    }

    private static TmsOrderTradeStatusUpdateVO getTmsOrderTradeStatusUpdateVO(Trade trade) {
        TmsOrderTradeStatusUpdateVO tmsOrderTradeStatusUpdateVO = new TmsOrderTradeStatusUpdateVO();
        tmsOrderTradeStatusUpdateVO.setTradeOrderId(trade.getId());
        Logistics logistics = trade.getTradeDelivers().get(0).getLogistics();
        tmsOrderTradeStatusUpdateVO.setTmsOrderId(logistics.getTmsOrderId());
        tmsOrderTradeStatusUpdateVO.setShipmentSiteId(logistics.getShipmentSiteId());
        tmsOrderTradeStatusUpdateVO.setShipmentSiteName(logistics.getShipmentSiteName());
        Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
        List<TmsOrderTradeStatusGoodsVO> goodsVOS = new ArrayList<>(goodsTotalNum.intValue());
        tmsOrderTradeStatusUpdateVO.setGoods(goodsVOS);
        /*tmsOrderTradeStatusUpdateVO.setThirdPartyDeliveryOrderNo(Constants.EMPTY_STR);
        tmsOrderTradeStatusUpdateVO.setThirdPartyDeliveryType(Constants.EMPTY_STR);*/
        return tmsOrderTradeStatusUpdateVO;
    }

    private static TmsOrderTradeStatusGoodsVO getTmsOrderTradeStatusGoodsVO(ShippingItem shippingItem) {
        TmsOrderTradeStatusGoodsVO goodsVO = new TmsOrderTradeStatusGoodsVO();
        goodsVO.setSkuId(shippingItem.getSkuId());
        goodsVO.setDeliverNum(shippingItem.getItemNum().intValue());
        return goodsVO;
    }

    public void updateStatusForTradeBuyerReceive(Trade trade) {
        if (!DeliverWay.isDeliveryToStore(trade.getDeliverWay())) {
            log.info("订单配送方式不是配送到店，不推TMS[{}]",trade.getId());
            return;
        }
        if(PayState.PAID != trade.getTradeState().getPayState()){
            return;
        }
        TmsOrderTradeStatusUpdateVO tmsOrderTradeStatusUpdateVO = getTmsOrderTradeStatusUpdateVO(trade);
        String apiName = "updateStatusForTradeBuyerReceive";
        log.info(param_to_tms,REMOTE_TMS_ORDER_SERVICE, apiName, JSONObject.toJSONString(tmsOrderTradeStatusUpdateVO));
        R<Boolean> r = remoteTmsOrderService.updateStatusForTradeBuyerReceive(tmsOrderTradeStatusUpdateVO);
        log.info(param_from_tms, REMOTE_TMS_ORDER_SERVICE, apiName,JSONObject.toJSONString(r));
        checkTmsResponse(r);
        Boolean isTmsed = r.getData();
    }

    public void updateStatusForTradeCancel(Trade trade) {
        if (!DeliverWay.isDeliveryToStore(trade.getDeliverWay())) {
            log.info("订单配送方式不是配送到店，不推TMS[{}]",trade.getId());
            return;
        }
        if(PayState.PAID != trade.getTradeState().getPayState()){
            return;
        }
        TmsOrderTradeStatusUpdateVO tmsOrderTradeStatusUpdateVO = getTmsOrderTradeStatusUpdateVO(trade);
        String apiName = "updateStatusForTradeCancel";
        log.info(param_to_tms,REMOTE_TMS_ORDER_SERVICE, apiName,JSONObject.toJSONString(tmsOrderTradeStatusUpdateVO));
        R<Boolean> r = remoteTmsOrderService.updateStatusForTradeCancel(tmsOrderTradeStatusUpdateVO);
        log.info(param_from_tms,REMOTE_TMS_ORDER_SERVICE, apiName, JSONObject.toJSONString(r));
        checkTmsResponse(r);
        Boolean isTmsed = r.getData();
    }

    public void computerTradeDeliveryPrice(List<Trade> tradesDeliveryToStore) {
        Map<Long, List<Trade>> TradeListByMarketIdMap = new HashMap<>(tradesDeliveryToStore.size());
        for(Trade trade:tradesDeliveryToStore){
            if(Objects.isNull(trade.getSupplier().getMarketId()))
                continue;
            List<Trade> tradeListSameMarket = TradeListByMarketIdMap.get(trade.getSupplier().getMarketId());
            if(null== tradeListSameMarket){
                tradeListSameMarket = new ArrayList<>(10);
                TradeListByMarketIdMap.put(trade.getSupplier().getMarketId(),tradeListSameMarket);
            }
            tradeListSameMarket.add(trade);
        }
        Consignee consignee = tradesDeliveryToStore.get(0).getConsignee();
        resetConsigneeById(consignee);
        boolean villageFlag = isToStoreVillageFlag(consignee);
        for(Map.Entry<Long, List<Trade>> listEntry:TradeListByMarketIdMap.entrySet()){
            List<TmsOrderAreaStoreVO> areaStoreList = new ArrayList<>(listEntry.getValue().size());
            for(Trade trade:listEntry.getValue()){
                Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
                TmsOrderAreaStoreVO tmsOrderAreaStoreVO = new TmsOrderAreaStoreVO();
                tmsOrderAreaStoreVO.setStoreId(Objects.toString(trade.getSupplier().getStoreId()));
                tmsOrderAreaStoreVO.setQuantity(goodsTotalNum);
                areaStoreList.add(tmsOrderAreaStoreVO);
            }
            log.warn("提交订单配送到店批发市场[{}],商家明细:[{}]",listEntry.getKey(),JSONObject.toJSONString(areaStoreList));
            List<TmsOrderAmountDTO> orderAmountDTOList = this.calcTradeFreightDeliveryToStore(consignee,areaStoreList,villageFlag);
            for(Trade trade:listEntry.getValue()){
                TradePrice tradePrice = trade.getTradePrice();
                TmsOrderAmountDTO tmsOrderAmountDTO = orderAmountDTOList.stream().filter(m->Objects.toString(trade.getSupplier().getStoreId()).equals(m.getStoreId())).findFirst().orElse(null);
                BigDecimal deliveryPrice = BigDecimal.ZERO ;
                if(tmsOrderAmountDTO!=null){
                    setTradeTmsInfo(trade,villageFlag,tmsOrderAmountDTO);
                    deliveryPrice=BigDecimal.valueOf(tmsOrderAmountDTO.getAmount());
                }
                // 8.5.订单总价、原始金额追加运费
                tradePrice.setDeliveryPrice(deliveryPrice);
                tradePrice.setOriginPrice(tradePrice.getOriginPrice().add(deliveryPrice));
                tradePrice.setTotalPrice(tradePrice.getTotalPrice().add(deliveryPrice));//应付金额 = 应付+运费
            }
        }
    }


    private List<TmsOrderAmountDTO> calcTradeFreightDeliveryToStore(Consignee consignee,List<TmsOrderAreaStoreVO> areaStoreList,boolean villageFlag) {
        TmsOrderAreaVO tmsOrderAreaVO =getTmsOrderAreaVO(consignee);
        tmsOrderAreaVO.setAreaStoreList(areaStoreList);
        tmsOrderAreaVO.setVillageFlag(villageFlag);
        String apiName = "calculateTmsAmountMutli";
        log.info(param_to_tms,REMOTE_TMS_ORDER_SERVICE,apiName,JSONObject.toJSONString(tmsOrderAreaVO));
        R<List<TmsOrderAmountDTO>> rData = remoteTmsOrderService.calculateTmsAmountMutli(tmsOrderAreaVO);
        log.info(param_from_tms,REMOTE_TMS_ORDER_SERVICE,apiName,JSONObject.toJSONString(rData));
        checkTmsResponse(rData);
        List<TmsOrderAmountDTO> tmsOrderAmountDTO = rData.getData();
        return tmsOrderAmountDTO;
    }

    private void setTradeTmsInfo(Trade trade,boolean villageFlag, TmsOrderAmountDTO tmsOrderAmountDTO) {
        String apiName = "getCarrierById";
        log.info(param_to_tms,REMOTE_TMS_ORDER_SERVICE, apiName,tmsOrderAmountDTO.getCarrierId());
        R<TmsCarrierDTO> r2 = remoteTmsCarrierService.getCarrierById(Objects.toString(tmsOrderAmountDTO.getCarrierId()));
        log.info(param_from_tms,REMOTE_TMS_ORDER_SERVICE, apiName,JSONObject.toJSONString(r2));
        checkTmsResponse(r2);
        TmsCarrierDTO tmsCarrierDTO = r2.getData();
        TradeDeliver tradeDeliver = new TradeDeliver();
        tradeDeliver.setLogistics(Logistics.builder().
                logisticFee(BigDecimal.valueOf(tmsOrderAmountDTO.getAmount())).
                logisticCompanyId(Objects.toString(tmsOrderAmountDTO.getCarrierId())).
                logisticCompanyName(tmsOrderAmountDTO.getCarrierName()).
                logisticPhone(tmsCarrierDTO.getContactMobile()).
                ccbMerchantNumber(tmsCarrierDTO.getCcbCode()).
                shareRatio(BigDecimal.valueOf(tmsCarrierDTO.getFzRatio())).
                finalPeriod(tmsCarrierDTO.getFinalPeriod()).build());
        trade.setTradeDelivers(Arrays.asList(tradeDeliver));
        trade.getConsignee().setVillageFlag(villageFlag);
    }

    private static TmsOrderAreaVO getTmsOrderAreaVO(Consignee consignee) {
        TmsOrderAreaVO tmsOrderAreaVO = new TmsOrderAreaVO();
        tmsOrderAreaVO.setProviceCode(Objects.toString(consignee.getProvinceId()));
        tmsOrderAreaVO.setCityCode(Objects.toString(consignee.getCityId()));
        tmsOrderAreaVO.setDistrictCode(Objects.toString(consignee.getAreaId()));
        tmsOrderAreaVO.setStreetCode(Objects.toString(consignee.getTwonId()));
        return tmsOrderAreaVO;
    }


    /**
     * @desc  创建tms运单
     * @author shiy  2023/9/20 9:01
     */
    public List<TmsOrderSaveResponseVO>  createTmsOrder(List<Trade> trades) {
        TmsOrderBatchWrapSaveVO tmsOrderBatchWrapSaveVO = new TmsOrderBatchWrapSaveVO();
        List<TmsOrderBatchSaveVO> orders = new ArrayList<>(trades.size());
        tmsOrderBatchWrapSaveVO.setOrders(orders);
        CustomerDeliveryAddressByIdResponse customerDelivery = getCustomerDeliveryAddressById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(trades.get(0).getConsignee().getId()).build()).getContext();
        trades.forEach(trade -> {
            if(DeliverWay.isDeliveryToStore(trade.getDeliverWay())) {
                TmsOrderBatchSaveVO tmsOrderBatchSaveVO = createTmsOrder(trade, customerDelivery);
                if (tmsOrderBatchSaveVO != null) {
                    orders.add(tmsOrderBatchSaveVO);
                }
            }
        });
        if(orders.size()>0){
            String apiName = "createTmsOrder";
            log.info(param_to_tms,REMOTE_TMS_ORDER_SERVICE, apiName,JSONObject.toJSONString(tmsOrderBatchWrapSaveVO));
            R<List<TmsOrderSaveResponseVO>> r1 = remoteTmsOrderService.createTmsOrder(tmsOrderBatchWrapSaveVO);
            log.info(param_from_tms,REMOTE_TMS_ORDER_SERVICE, apiName,JSONObject.toJSONString(r1));
            checkTmsResponse(r1);
            List<TmsOrderSaveResponseVO> tmsOrderSaveResponseVOList = r1.getData();
            if(CollectionUtils.isNotEmpty(tmsOrderSaveResponseVOList)){
                return tmsOrderSaveResponseVOList;
            }
        }
        return null;
    }

    public List<TradeFreightResponse> getFreightForDelivery(List<TradeParams> tradeParamsList) {
        List<TradeFreightResponse> freightResponseList = new ArrayList<>();
        Map<Long, List<TradeParams>> tradeParamsListByMarketId = new HashMap<>(tradeParamsList.size());
        for(TradeParams tradeParams:tradeParamsList){
            if(Objects.isNull(tradeParams.getSupplier().getMarketId()))
                continue;
            List<TradeParams> tradeParamsSameMarket = tradeParamsListByMarketId.get(tradeParams.getSupplier().getMarketId());
            if(null== tradeParamsSameMarket){
                tradeParamsSameMarket = new ArrayList<>(10);
                tradeParamsListByMarketId.put(tradeParams.getSupplier().getMarketId(),tradeParamsSameMarket);
            }
            tradeParamsSameMarket.add(tradeParams);
        }
        Consignee consignee = tradeParamsList.get(0).getConsignee();
        resetConsigneeById(consignee);
        boolean villageFlag = isToStoreVillageFlag(consignee);
        for(Map.Entry<Long, List<TradeParams>> listEntry:tradeParamsListByMarketId.entrySet()){
            List<TmsOrderAreaStoreVO> areaStoreList = new ArrayList<>(listEntry.getValue().size());
            for(TradeParams tradeParams:listEntry.getValue()){
                Long goodsTotalNum = getTradeSkuNumber(tradeParams.getOldTradeItems(), tradeParams.getOldGifts());
                TmsOrderAreaStoreVO tmsOrderAreaStoreVO = new TmsOrderAreaStoreVO();
                tmsOrderAreaStoreVO.setStoreId(Objects.toString(tradeParams.getSupplier().getStoreId()));
                tmsOrderAreaStoreVO.setQuantity(goodsTotalNum);
                areaStoreList.add(tmsOrderAreaStoreVO);
            }
            log.warn("配送到店批发市场[{}],商家明细:[{}]",listEntry.getKey(),JSONObject.toJSONString(areaStoreList));
            List<TmsOrderAmountDTO> orderAmountDTOList = this.calcTradeFreightDeliveryToStore(consignee,areaStoreList,villageFlag);
            for(TradeParams tradeParams:listEntry.getValue()){
                TradeFreightResponse freightResponse = new TradeFreightResponse();
                freightResponse.setStoreId(tradeParams.getSupplier().getStoreId());
                freightResponse.setStoreName(tradeParams.getSupplier().getStoreName());
                TmsOrderAmountDTO tmsOrderAmountDTO = orderAmountDTOList.stream().filter(m->Objects.toString(tradeParams.getSupplier().getStoreId()).equals(m.getStoreId())).findFirst().orElse(null);
                if(tmsOrderAmountDTO==null){
                    freightResponse.setDeliveryPrice(BigDecimal.ZERO);
                }else{
                    freightResponse.setFreightRuleDesc(tmsOrderAmountDTO.getFreightRuleDesc());
                    freightResponse.setDeliveryPrice(BigDecimal.valueOf(tmsOrderAmountDTO.getAmount()));
                }
                freightResponseList.add(freightResponse);
            }
        }
        return freightResponseList;
    }
    public TmsOrderThirdDeliveryDTO updateStatusForTradeSupplierDeliver(Trade trade) {
        if (DeliverWay.DELIVERY_TO_STORE != trade.getDeliverWay()) {
            log.info("订单配送方式不是配送到店，不推TMS[{}]",trade.getId());
            return null;
        }
        if(PayState.PAID != trade.getTradeState().getPayState()){
            return null;
        }
        TmsOrderThirdDeliveryDTO thirdDeliveryDTO = updateTmsByTradeSupplierDeliver(trade);
        Double refundAmount = getTmsRefundAmountByTradeId(trade);
        thirdDeliveryDTO.setRefundAmount(refundAmount);
        return thirdDeliveryDTO;
    }

    private TmsOrderThirdDeliveryDTO updateTmsByTradeSupplierDeliver(Trade trade) {
        TmsOrderTradeStatusUpdateVO tmsOrderTradeStatusUpdateVO = getTmsOrderTradeStatusUpdateVO(trade);
        List<TmsOrderTradeStatusGoodsVO> tmsOrderTradeStatusGoodsVOS = tmsOrderTradeStatusUpdateVO.getGoods();
        if(CollectionUtils.isNotEmpty(trade.getTradeItems())){
            trade.getTradeItems().forEach(item->{
                TmsOrderTradeStatusGoodsVO goodsVO = getTmsOrderTradeStatusGoodsVO(item);
                //if(goodsVO.getDeliverNum()>0) {
                tmsOrderTradeStatusGoodsVOS.add(goodsVO);
                //}
            });
        }
        if(CollectionUtils.isNotEmpty(trade.getGifts())) {
            trade.getGifts().forEach(item->{
                TmsOrderTradeStatusGoodsVO goodsVO = getTmsOrderTradeStatusGoodsVO(item);
                //if(goodsVO.getDeliverNum()>0) {
                tmsOrderTradeStatusGoodsVOS.add(goodsVO);
                //}
            });
        }
        if(tmsOrderTradeStatusGoodsVOS.size()==0){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单发货数不能为0");
        }
        tmsOrderTradeStatusUpdateVO.setGoods(tmsOrderTradeStatusGoodsVOS);
        if(selfOrder(trade) && StringUtils.isNotBlank(trade.getTradeDelivers().get(0).getLogistics().getLogisticNo())){
            tmsOrderTradeStatusUpdateVO.setThirdPartyDeliveryOrderNo(trade.getTradeDelivers().get(0).getLogistics().getLogisticNo());
        }
        String apiName = "updateStatusForTradeSupplierDeliver";
        log.info(param_to_tms, REMOTE_TMS_ORDER_SERVICE, apiName,JSONObject.toJSONString(tmsOrderTradeStatusUpdateVO));
        R<TmsOrderThirdDeliveryDTO> r = remoteTmsOrderService.updateStatusForTradeSupplierDeliver(tmsOrderTradeStatusUpdateVO);
        log.info(param_from_tms, REMOTE_TMS_ORDER_SERVICE, apiName, JSONObject.toJSONString(r));
        checkTmsResponse(r);
        TmsOrderThirdDeliveryDTO rData = r.getData();
        return rData;
    }

    private Double getTmsRefundAmountByTradeId(Trade trade) {
        String apiName = "getRefundAmountByTradeId";
        log.info(param_to_tms,REMOTE_TMS_ORDER_SERVICE, apiName,trade.getId());
        R<Double> rdata= remoteTmsOrderService.getRefundAmountByTradeId(trade.getId());
        log.info(param_from_tms,REMOTE_TMS_ORDER_SERVICE,apiName, JSONObject.toJSONString(rdata));
        checkTmsResponse(rdata);
        return rdata.getData();
    }

}
