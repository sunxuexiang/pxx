package com.wanmi.sbc.order.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.StoreShippingAddressQueryDTO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.trade.model.entity.TradeDeliver;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Consignee;
import com.wanmi.sbc.order.trade.model.entity.value.Logistics;
import com.wanmi.sbc.order.trade.model.entity.value.ShippingItem;
import com.wanmi.sbc.order.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.reponse.TradeFreightResponse;
import com.wanmi.sbc.order.trade.request.TradeParams;
import com.wanmi.sbc.tms.api.RemoteExpressOrderService;
import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.dto.ExpressOrderAmountDTO;
import com.wanmi.sbc.tms.api.domain.dto.ExpressOrderThirdDeliveryDTO;
import com.wanmi.sbc.tms.api.domain.dto.ExpressSaveDTO;
import com.wanmi.sbc.tms.api.domain.dto.TmsCarrierDTO;
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
public class LocalExpressOrderService extends TradeTmsService{

    private static final String REMOTE_EXPRESS_ORDER_SERVICE = "remoteExpressOrderService";

    @Autowired
    private RemoteExpressOrderService remoteExpressOrderService;


    private ExpressSaveVO createTmsOrder(Trade trade, CustomerDeliveryAddressByIdResponse customerDelivery) {
        if(trade.getTradeState().getPayState()!=PayState.PAID){
            log.info("当前订单[{}]状态不支持创建TMS",trade.getId());
            return null;
        }
        ExpressSaveVO tmsOrderBatchSaveVO = new ExpressSaveVO();
        ExpressOrderSaveVO tmsOrderSaveVO = getExpressOrderSaveVO(trade, customerDelivery);
        ExpressTradeSaveVO tmsOrderTradeSaveVO = getTmsOrderTradeSaveVO(trade);
        List<ExpressTradeItemSaveVO> items = new ArrayList<>();
        if(CollectionUtils.isNotEmpty( trade.getTradeItems())) {
            trade.getTradeItems().forEach(tradeItem -> {
                items.add(getExpressTradeItemSaveVO(tradeItem));
            });
        }
        if(CollectionUtils.isNotEmpty(trade.getGifts())) {
            trade.getGifts().forEach(tradeItem -> {
                items.add(getExpressTradeItemSaveVO(tradeItem));
            });
        }
        tmsOrderBatchSaveVO.setOrderSaveVO(tmsOrderSaveVO);
        tmsOrderBatchSaveVO.setTradeSaveVO(tmsOrderTradeSaveVO);
        tmsOrderBatchSaveVO.setItems(items);
        return tmsOrderBatchSaveVO;
    }

    private static ExpressTradeItemSaveVO getExpressTradeItemSaveVO(TradeItem tradeItem) {
        ExpressTradeItemSaveVO tmsOrderTradeItemSaveVO = new ExpressTradeItemSaveVO();
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
    private static ExpressTradeSaveVO getTmsOrderTradeSaveVO(Trade trade) {
        ExpressTradeSaveVO tmsOrderTradeSaveVO = new ExpressTradeSaveVO();
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
    private ExpressOrderSaveVO getExpressOrderSaveVO(Trade trade, CustomerDeliveryAddressByIdResponse customerDelivery) {
        ExpressOrderSaveVO tmsOrderSaveVO = new ExpressOrderSaveVO();
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
        /*tmsOrderSaveVO.setShipmentSiteId(trade.getTradeDelivers().get(0).getLogistics().getShipmentSiteId());
        tmsOrderSaveVO.setShipmentSiteName(trade.getTradeDelivers().get(0).getLogistics().getShipmentSiteName());
        tmsOrderSaveVO.setPickupSiteId(Objects.isNull(trade.getNetWorkVO())?"":Objects.toString(trade.getNetWorkVO().getNetworkId()));
        tmsOrderSaveVO.setPickupSiteName(Objects.isNull(trade.getNetWorkVO())?"":Objects.toString(trade.getNetWorkVO().getNetworkName()));
        tmsOrderSaveVO.setPaymentMethod(0);
        tmsOrderSaveVO.setPaymentStatus(1);*/
        tmsOrderSaveVO.setAmount(trade.getTradePrice().getDeliveryPrice());
        Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
        tmsOrderSaveVO.setQuantity(goodsTotalNum.intValue());
        BigDecimal goodsTotalWeight = getTradeSkuWeight(trade.getTradeItems(), trade.getGifts());
        tmsOrderSaveVO.setWeight(goodsTotalWeight);
        /*tmsOrderSaveVO.setEstimatedArrivalTime(LocalDateTime.now());
        tmsOrderSaveVO.setDistance(new BigDecimal("0"));*/
        tmsOrderSaveVO.setVillageFlag(trade.getConsignee().getVillageFlag()?1:0);
        //tmsOrderSaveVO.setRemark("");
        return tmsOrderSaveVO;
    }

    private static ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO getTmsOrderTradeStatusGoodsVO(TradeItem item) {
        ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO goodsVO = new ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO();
        goodsVO.setSkuId(item.getSkuId());
        goodsVO.setDeliverNum(item.getDeliveredNum().intValue());
        goodsVO.setWeight(item.getGoodsWeight().doubleValue());
        Integer refundNum =0;
        if(item.getNum()!=null&&item.getDeliveredNum()!=null) {
            refundNum = item.getNum().intValue()-item.getDeliveredNum().intValue();
        }else{
            log.info("getTmsOrderTradeStatusGoodsVO商品skuid[{}]下单数量[{}]发货数量[{}]",item.getSkuId(),item.getNum(),item.getDeliveredNum());
        }
        goodsVO.setRefundNum(refundNum);
        return goodsVO;
    }

    private static ExpressOrderDeliverVO getTmsOrderTradeStatusUpdateVO(Trade trade) {
        ExpressOrderDeliverVO tmsOrderTradeStatusUpdateVO = new ExpressOrderDeliverVO();
        tmsOrderTradeStatusUpdateVO.setTradeOrderId(trade.getId());
        Logistics logistics = trade.getTradeDelivers().get(0).getLogistics();
        tmsOrderTradeStatusUpdateVO.setExpressOrderId(logistics.getTmsOrderId());
        tmsOrderTradeStatusUpdateVO.setShipmentSiteId(logistics.getShipmentSiteId());
        tmsOrderTradeStatusUpdateVO.setShipmentSiteName(logistics.getShipmentSiteName());
        Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
        List<ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO> goodsVOS = new ArrayList<>(goodsTotalNum.intValue());
        tmsOrderTradeStatusUpdateVO.setGoodsList(goodsVOS);
        /*tmsOrderTradeStatusUpdateVO.setThirdPartyDeliveryOrderNo("");
        tmsOrderTradeStatusUpdateVO.setThirdPartyDeliveryType("");*/
        return tmsOrderTradeStatusUpdateVO;
    }

    private static TmsOrderTradeStatusGoodsVO getTmsOrderTradeStatusGoodsVO(ShippingItem shippingItem) {
        TmsOrderTradeStatusGoodsVO goodsVO = new TmsOrderTradeStatusGoodsVO();
        goodsVO.setSkuId(shippingItem.getSkuId());
        goodsVO.setDeliverNum(shippingItem.getItemNum().intValue());
        return goodsVO;
    }

    public void updateStatusForTradeBuyerReceive(Trade trade) {
        if (!DeliverWay.isExpressSelfPaid(trade.getDeliverWay())) {
            log.info("订单配送方式不是快递到家自费，不推TMS[{}]",trade.getId());
            return;
        }
        if(PayState.PAID != trade.getTradeState().getPayState()){
            return;
        }
        ExpressOrderDeliverVO tmsOrderTradeStatusUpdateVO = getTmsOrderTradeStatusUpdateVO(trade);
        String apiName = "updateTradeBuyerReceive";
        log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName, JSONObject.toJSONString(tmsOrderTradeStatusUpdateVO));
        R<Boolean> r = remoteExpressOrderService.updateTradeBuyerReceive(tmsOrderTradeStatusUpdateVO);
        log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName,JSONObject.toJSONString(r));
        checkTmsResponse(r);
        Boolean isTmsed = r.getData();
    }

    public void updateStatusForTradeCancel(Trade trade) {
        if (!DeliverWay.isExpressSelfPaid(trade.getDeliverWay())) {
            log.info("订单配送方式不是快递到家自费，不推TMS[{}]",trade.getId());
            return;
        }
        if(PayState.PAID != trade.getTradeState().getPayState()){
            return;
        }
        ExpressOrderDeliverVO tmsOrderTradeStatusUpdateVO = getTmsOrderTradeStatusUpdateVO(trade);
        String apiName = "updateTradeCancel";
        log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName,JSONObject.toJSONString(tmsOrderTradeStatusUpdateVO));
        R<Boolean> r = remoteExpressOrderService.updateTradeCancel(tmsOrderTradeStatusUpdateVO);
        log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName, JSONObject.toJSONString(r));
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
            List<ExpressOrderAreaVO.ExpressAreaStoreVO> areaStoreList = new ArrayList<>(listEntry.getValue().size());
            for(Trade trade:listEntry.getValue()){
                Long goodsTotalNum = getTradeSkuNumber(trade.getTradeItems(), trade.getGifts());
                ExpressOrderAreaVO.ExpressAreaStoreVO tmsOrderAreaStoreVO = new ExpressOrderAreaVO.ExpressAreaStoreVO();
                tmsOrderAreaStoreVO.setStoreId(Objects.toString(trade.getSupplier().getStoreId()));
                tmsOrderAreaStoreVO.setQuantity(goodsTotalNum);
                Long provinceId = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(trade.getSupplier().getStoreId()).build()).getContext().getProvinceId();
                tmsOrderAreaStoreVO.setDeliverProviceCode(Objects.toString(provinceId));
                BigDecimal totalWeight = getTradeSkuWeight(trade.getTradeItems(), trade.getGifts());
                tmsOrderAreaStoreVO.setTotalWeight(totalWeight.doubleValue());
                areaStoreList.add(tmsOrderAreaStoreVO);
            }
            log.warn("提交订单快递到家自费批发市场[{}],商家明细:[{}]",listEntry.getKey(),JSONObject.toJSONString(areaStoreList));
            List<ExpressOrderAmountDTO> orderAmountDTOList = this.calcTradeFreightDeliveryToStore(consignee,areaStoreList,villageFlag);
            for(Trade trade:listEntry.getValue()){
                TradePrice tradePrice = trade.getTradePrice();
                ExpressOrderAmountDTO tmsOrderAmountDTO = orderAmountDTOList.stream().filter(m->Objects.toString(trade.getSupplier().getStoreId()).equals(m.getStoreId())).findFirst().orElse(null);
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


    private List<ExpressOrderAmountDTO> calcTradeFreightDeliveryToStore(Consignee consignee, List<ExpressOrderAreaVO.ExpressAreaStoreVO> areaStoreList, boolean villageFlag) {
        ExpressOrderAreaVO tmsOrderAreaVO =getTmsOrderAreaVO(consignee);
        tmsOrderAreaVO.setAreaStoreList(areaStoreList);
        tmsOrderAreaVO.setVillageFlag(villageFlag);
        String apiName = "calculateMutli";
        log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE,apiName,JSONObject.toJSONString(tmsOrderAreaVO));
        R<List<ExpressOrderAmountDTO>> rData = remoteExpressOrderService.calculateMutli(tmsOrderAreaVO);
        log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE,apiName,JSONObject.toJSONString(rData));
        checkTmsResponse(rData);
        List<ExpressOrderAmountDTO> tmsOrderAmountDTO = rData.getData();
        return tmsOrderAmountDTO;
    }

    private void setTradeTmsInfo(Trade trade,boolean villageFlag, ExpressOrderAmountDTO tmsOrderAmountDTO) {
        String apiName = "getCarrierById";
        log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName,tmsOrderAmountDTO.getCarrierId());
        R<TmsCarrierDTO> r2 = remoteTmsCarrierService.getCarrierById(Objects.toString(tmsOrderAmountDTO.getCarrierId()));
        log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName,JSONObject.toJSONString(r2));
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

    private static ExpressOrderAreaVO getTmsOrderAreaVO(Consignee consignee) {
        ExpressOrderAreaVO tmsOrderAreaVO = new ExpressOrderAreaVO();
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
    public ExpressSaveDTO  createTmsOrder(Trade trade) {
        CustomerDeliveryAddressByIdResponse customerDelivery = getCustomerDeliveryAddressById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(trade.getConsignee().getId()).build()).getContext();
        if (DeliverWay.isExpressSelfPaid(trade.getDeliverWay())) {
            ExpressSaveVO expressSaveVO = createTmsOrder(trade, customerDelivery);
            if (expressSaveVO != null) {
                String apiName = "createExpressOrder";
                log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName, JSONObject.toJSONString(expressSaveVO));
                R<ExpressSaveDTO> r1 = remoteExpressOrderService.createExpressOrder(expressSaveVO);
                log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName, JSONObject.toJSONString(r1));
                checkTmsResponse(r1);
                return r1.getData();
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
            List<ExpressOrderAreaVO.ExpressAreaStoreVO> areaStoreList = new ArrayList<>(listEntry.getValue().size());
            for(TradeParams tradeParams:listEntry.getValue()){
                Long goodsTotalNum = getTradeSkuNumber(tradeParams.getOldTradeItems(), tradeParams.getOldGifts());
                ExpressOrderAreaVO.ExpressAreaStoreVO tmsOrderAreaStoreVO = new ExpressOrderAreaVO.ExpressAreaStoreVO();
                tmsOrderAreaStoreVO.setStoreId(Objects.toString(tradeParams.getSupplier().getStoreId()));
                tmsOrderAreaStoreVO.setQuantity(goodsTotalNum);
                Long provinceId = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(tradeParams.getSupplier().getStoreId()).build()).getContext().getProvinceId();
                tmsOrderAreaStoreVO.setDeliverProviceCode(Objects.toString(provinceId));
                BigDecimal totalWeight = getTradeSkuWeight(tradeParams.getOldTradeItems(), tradeParams.getOldGifts());
                tmsOrderAreaStoreVO.setTotalWeight(totalWeight.doubleValue());
                areaStoreList.add(tmsOrderAreaStoreVO);
            }
            log.warn("快递到家自费批发市场[{}],商家明细:[{}]",listEntry.getKey(),JSONObject.toJSONString(areaStoreList));
            List<ExpressOrderAmountDTO> orderAmountDTOList = this.calcTradeFreightDeliveryToStore(consignee,areaStoreList,villageFlag);
            for(TradeParams tradeParams:listEntry.getValue()){
                TradeFreightResponse freightResponse = new TradeFreightResponse();
                freightResponse.setStoreId(tradeParams.getSupplier().getStoreId());
                freightResponse.setStoreName(tradeParams.getSupplier().getStoreName());
                ExpressOrderAmountDTO tmsOrderAmountDTO = orderAmountDTOList.stream().filter(m->Objects.toString(tradeParams.getSupplier().getStoreId()).equals(m.getStoreId())).findFirst().orElse(null);
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
    public ExpressOrderThirdDeliveryDTO updateStatusForTradeSupplierDeliver(Trade trade) {
        if (!DeliverWay.isExpressSelfPaid(trade.getDeliverWay())) {
            log.info("订单配送方式不是{}，不推TMS[{}]",DeliverWay.EXPRESS.getDesc(),trade.getId());
            return null;
        }
        if(PayState.PAID != trade.getTradeState().getPayState()){
            return null;
        }
        ExpressOrderThirdDeliveryDTO thirdDeliveryDTO = updateTmsByTradeSupplierDeliver(trade);
        Double refundAmount = getTmsRefundAmountByTradeId(trade);
        thirdDeliveryDTO.setRefundAmount(refundAmount);
        return thirdDeliveryDTO;
    }

    private void fillExpressOrderDeliverVO(String logisticId,ExpressOrderDeliverVO expressOrderDeliverVO){
        StoreShippingAddressQueryDTO queryDTO = new StoreShippingAddressQueryDTO();
        //queryDTO.setDefaultFlag(1);
        queryDTO.setId(Long.parseLong(logisticId));
        /*List<StoreShippingAddressVO> shippingAddressVOList = storeQueryProvider.listShippingAddress(queryDTO).getContext();
        if(CollectionUtils.isEmpty(shippingAddressVOList))
            return;
        StoreShippingAddressVO storeShippingAddressVO = shippingAddressVOList.get(0);
        expressOrderDeliverVO.setExpressPickProvinceCode(Objects.toString(storeShippingAddressVO.getProvinceCode()));
        expressOrderDeliverVO.setExpressPickCityCode(Objects.toString(storeShippingAddressVO.getCityCode()));
        expressOrderDeliverVO.setExpressPickDistrictCode(Objects.toString(storeShippingAddressVO.getDistrictCode()));
        expressOrderDeliverVO.setExpressPickStreetCode(Objects.toString(storeShippingAddressVO.getStreetCode()));
        expressOrderDeliverVO.setExpressPickName(Objects.toString(storeShippingAddressVO.getShippingPerson()));
        expressOrderDeliverVO.setExpressPickAddress(Objects.toString(storeShippingAddressVO.getDetailAddress()));
        expressOrderDeliverVO.setExpressPickPhone(Objects.toString(storeShippingAddressVO.getShippingPhone()));*/
    }

    private ExpressOrderThirdDeliveryDTO updateTmsByTradeSupplierDeliver(Trade trade) {
        ExpressOrderDeliverVO tmsOrderTradeStatusUpdateVO = getTmsOrderTradeStatusUpdateVO(trade);
        //fillExpressOrderDeliverVO(trade.getTradeDelivers().get(0).getLogistics().getShipmentSiteId(),tmsOrderTradeStatusUpdateVO);
        List<ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO> tmsOrderTradeStatusGoodsVOS = tmsOrderTradeStatusUpdateVO.getGoodsList();
        if(CollectionUtils.isNotEmpty(trade.getTradeItems())){
            trade.getTradeItems().forEach(item->{
                ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO goodsVO = getTmsOrderTradeStatusGoodsVO(item);
                //if(goodsVO.getDeliverNum()>0) {
                tmsOrderTradeStatusGoodsVOS.add(goodsVO);
                //}
            });
        }
        if(CollectionUtils.isNotEmpty(trade.getGifts())) {
            trade.getGifts().forEach(item->{
                ExpressOrderDeliverVO.ExpressOrderDeliverGoodsVO goodsVO = getTmsOrderTradeStatusGoodsVO(item);
                //if(goodsVO.getDeliverNum()>0) {
                tmsOrderTradeStatusGoodsVOS.add(goodsVO);
                //}
            });
        }
        if(tmsOrderTradeStatusGoodsVOS.size()==0){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单发货数不能为0");
        }
        tmsOrderTradeStatusUpdateVO.setGoodsList(tmsOrderTradeStatusGoodsVOS);
        if(selfOrder(trade) && StringUtils.isNotBlank(trade.getTradeDelivers().get(0).getLogistics().getLogisticNo())){
            tmsOrderTradeStatusUpdateVO.setThirdOrderNo(trade.getTradeDelivers().get(0).getLogistics().getLogisticNo());
        }
        String apiName = "updateTradeSupplierDeliver";
        log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName,JSONObject.toJSONString(tmsOrderTradeStatusUpdateVO));
        R<ExpressOrderThirdDeliveryDTO> r = remoteExpressOrderService.updateTradeSupplierDeliver(tmsOrderTradeStatusUpdateVO);
        log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE, apiName, JSONObject.toJSONString(r));
        checkTmsResponse(r);
        ExpressOrderThirdDeliveryDTO rData = r.getData();
        return rData;
    }

    private Double getTmsRefundAmountByTradeId(Trade trade) {
        String getRefundAmountByTradeId = "getRefundAmountByTradeId";
        log.info(param_to_tms, REMOTE_EXPRESS_ORDER_SERVICE, getRefundAmountByTradeId,trade.getId());
        R<Double> rdata= remoteExpressOrderService.getRefundAmountByTradeId(trade.getId());
        log.info(param_from_tms, REMOTE_EXPRESS_ORDER_SERVICE,getRefundAmountByTradeId, JSONObject.toJSONString(rdata));
        checkTmsResponse(rdata);
        return rdata.getData();
    }

}
