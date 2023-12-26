package com.wanmi.sbc.wms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rop.client.CompositeResponse;
import com.wanmi.open.sdk.SdkClient;
import com.wanmi.open.sdk.request.ReceptionOrderRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeDeliverRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.bean.dto.TradeDeliverDTO;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.wms.dto.ShippingItemDTO;
import com.wanmi.sbc.wms.request.WMSPushOrderDetailsRequest;
import com.wanmi.sbc.wms.request.WMSPushOrderRequest;
import com.wanmi.sbc.wms.request.WmsBaseRequest;
import com.wanmi.sbc.wms.request.order.BaseOrderStructRequest;
import com.wanmi.sbc.wms.response.WmsBaseResponse;
import com.wanmi.sbc.wms.service.SendChainService;
import com.wanmi.sbc.wms.vo.TradeItemsGiftsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author baijianzhong
 * @ClassName WmsTradeController
 * @Date 2020-07-15 21:02
 * @Description TODO
 **/
@Controller
@Slf4j
public class WmsTradeController {

    @Autowired
    private SendChainService sendChainService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    @Autowired
    private TradeProvider tradeProvider;
    @Value("${send.chain.flag:false}")
    private Boolean sendChainFlag;
    /*@Value("${wms.logisticPhone}")
    private String logisticPhone;*/

    @RequestMapping(value = "/deliver", method = RequestMethod.POST)
    @ResponseBody
    public String deliverGoods(WmsBaseRequest wmsBaseRequest){
        log.info("===================== request info ================\n " + JSONObject.toJSONString(wmsBaseRequest));

        if (sendChainFlag) {
            String result = sendChainService.sendChain(wmsBaseRequest, "/deliver");
            if (Objects.nonNull(result) && result.contains("0000") && result.contains("ok")) {
                WmsBaseResponse response = WmsBaseResponse
                        .builder()
                        .returnFlag(1)
                        .returnCode("0000")
                        .returnDesc("ok")
                        .build();
                return this.getResponseInfo(response);
            }
        }

        WmsBaseResponse wmsBaseResponse = new WmsBaseResponse();
        setSuccessResponse(wmsBaseResponse, "0000", 1, "ok");
        JSONObject jsonObject = JSONObject.parseObject(wmsBaseRequest.getData());
        BaseOrderStructRequest baseOrderStructRequest =  JSONObject.toJavaObject(jsonObject,BaseOrderStructRequest.class);
        log.info("baseOrderStructRequest {}", JSONObject.toJSONString(baseOrderStructRequest));
        List<ReceptionOrderRequest> receptionOrderRequests = this.getDeliverStructInfo(baseOrderStructRequest);
        log.info("===================== request info open params ================\n " + JSONObject.toJSONString(receptionOrderRequests));
        if(!CollectionUtils.isEmpty(receptionOrderRequests)){
            for(ReceptionOrderRequest ropRequest: receptionOrderRequests){
                try {
                    TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(ropRequest.getTradeId()).build()).getContext().getTradeVO();
                    if(tradeVO!=null) {
                        if(DeliverStatus.isShipped(tradeVO.getTradeState().getDeliverStatus())){
                            log.info("订单[{}]状态已发货,不再执行发货", tradeVO.getId());
                            continue;
                        }
                        //if(tradeVO.getDeliverWay().toValue()==7) {
                            selfStoreDelivery(ropRequest, tradeVO);
                            continue;
                        //}
                    }
                    //调用开放平台订单发货
//                    ClassPathResource classPathResource = new ClassPathResource("excleTemplate/test.xlsx");
//                    InputStream inputStream =classPathResource.getInputStream();
                    SdkClient sdkClient = new SdkClient(wmsBaseRequest.getAppkey(), wmsBaseRequest.getApptoken());
                    ClassPathResource resource = new ClassPathResource("host.properties");
                    Properties properties = new Properties();
                    properties.load(resource.getInputStream());
                    log.info("=================== sdk client config:url {} ", properties.getProperty("open_url"));
                    log.info("=================== sdk client config:appkey {} ", wmsBaseRequest.getAppkey());
                    log.info("=================== sdk client config:apptoken {} ", wmsBaseRequest.getApptoken());
                    CompositeResponse post = sdkClient.buildClientRequest().post(ropRequest, Object.class,
                            wmsBaseRequest.getMethod(), wmsBaseRequest.getMessageid());
                    BaseResponse baseResponse = JSONObject.parseObject(JSONObject.toJSONString(post.getSuccessResponse()), BaseResponse.class);
                    log.info("=================== open response: ================ {}",JSONObject.toJSONString(baseResponse));
                    if (Objects.nonNull(baseResponse) && CommonErrorCode.SUCCESSFUL.equals(baseResponse.getCode())) {
                        setSuccessResponse(wmsBaseResponse, "0000", 1, "ok");
                    } else {
                        wmsBaseResponse.setReturnCode("0001");
                        wmsBaseResponse.setReturnFlag(0);
                        wmsBaseResponse.setReturnDesc("接口异常");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    wmsBaseResponse.setReturnCode("0001");
                    wmsBaseResponse.setReturnFlag(0);
                    wmsBaseResponse.setReturnDesc("接口异常");
                }
            }
        }
        return this.getResponseInfo(wmsBaseResponse);
    }

    private static void setSuccessResponse(WmsBaseResponse wmsBaseResponse, String returnCode, int returnFlag, String ok) {
        wmsBaseResponse.setReturnCode(returnCode);
        wmsBaseResponse.setReturnFlag(returnFlag);
        wmsBaseResponse.setReturnDesc(ok);
    }

    private void selfStoreDelivery(ReceptionOrderRequest ropRequest, TradeVO tradeVO) {
        TradeDeliverVO tradeDeliver = new TradeDeliverVO();
        if(DeliverWay.isTmsDelivery(tradeVO.getDeliverWay())) {
            TradeDeliverVO dbTradeDeliverVo = tradeVO.getTradeDelivers().get(0);
            tradeDeliver = KsBeanUtil.convert(dbTradeDeliverVo, TradeDeliverVO.class);
            //logistics.setLogisticNo(ropRequest.getDeliverNo());
            //logistics.setLogisticPhone(logisticPhone);//"4008319899"
        }else{
            LogisticsVO logisticsVO = new LogisticsVO();//tradeVO.getLogisticsCompanyInfo(), LogisticsVO.class);
            logisticsVO.setLogisticNo(ropRequest.getDeliverNo());
            if(StringUtils.isNotBlank(tradeVO.getLogisticsCompanyInfo().getLogisticsAddress())) {
                logisticsVO.setLogisticCompanyName(tradeVO.getLogisticsCompanyInfo().getLogisticsAddress());
            }
        }
        tradeDeliver.setDeliverAll(ropRequest.getDeliverAll());
        if(StringUtils.isBlank(ropRequest.getSendTime())) {
            tradeDeliver.setDeliverTime(LocalDateTime.now());
        }else{
            tradeDeliver.setDeliverTime(DateUtil.getLocalDateTimeByDateTime(DateUtil.stringToDate(ropRequest.getSendTime())));
        }
        List<ShippingItemDTO> itemDTOS = JSON.parseArray(ropRequest.getTradeItems(), ShippingItemDTO.class);
        List<ShippingItemDTO> sumItemDTOs = Lists.newArrayList();
        //统计商品总数
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(itemDTOS)) {
            ShippingItemDTO shippingItemFirst = itemDTOS.stream().findFirst().orElse(null);
            log.info("shippingItemFirst+++++++++ {}", JSONObject.toJSONString(shippingItemFirst));
            if (Objects.nonNull(shippingItemFirst.getDevanningId())) {
                Map<Long, List<ShippingItemDTO>> itemsMap = itemDTOS.stream().collect(Collectors.groupingBy(ShippingItemDTO::getDevanningId, Collectors.toList()));
                itemsMap.forEach((k, v) -> {
                    if (v.size() < 2) {
                        sumItemDTOs.add(v.get(0));
                    } else {
                        ShippingItemDTO shippingItemDTO = v.stream().findFirst().orElse(null);
                        //数量统计
                        BigDecimal reduce = v.stream().map(m -> m.getDeliveryNum()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        shippingItemDTO.setDeliveryNum(reduce);
                        sumItemDTOs.add(shippingItemDTO);
                    }
                });
            } else {
                Map<String, List<ShippingItemDTO>> itemsMap = itemDTOS.stream().collect(Collectors.groupingBy(ShippingItemDTO::getGoodsInfoNo, Collectors.toList()));
                itemsMap.forEach((k, v) -> {
                    if (v.size() < 2) {
                        sumItemDTOs.add(v.get(0));
                    } else {
                        ShippingItemDTO shippingItemDTO = v.stream().findFirst().orElse(null);
                        //数量统计
                        BigDecimal reduce = v.stream().map(m -> m.getDeliveryNum()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        shippingItemDTO.setDeliveryNum(reduce);
                        sumItemDTOs.add(shippingItemDTO);
                    }
                });
            }
        }
        TradeItemsGiftsVo detailGiftItems = detailGiftItems(ropRequest,tradeVO);
        tradeDeliver.setShippingItems(detailGiftItems.getTradeItems());
        tradeDeliver.setGiftItemList(detailGiftItems.getGifts());
        tradeDeliver.setDeliverWay(tradeVO.getDeliverWay().toValue());
        TradeDeliverRequest tradeDeliverRequest = TradeDeliverRequest.builder()
                .tradeDeliver(KsBeanUtil.convert(tradeDeliver, TradeDeliverDTO.class))
                .tid(tradeVO.getId())
                .operator(getOperator())
                .build();
        String deliverId = tradeProvider.deliver(tradeDeliverRequest).getContext().getDeliverId();
        log.info("wms发货成功订单id:[{}],发货结果:[{}]",tradeVO.getId(),deliverId);
    }

    private TradeItemsGiftsVo detailGiftItems(ReceptionOrderRequest ropRequest,TradeVO trade) {
        // 正常购买商品
        Map<String, TradeItemVO> skuItemMap = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItemVO::getSkuNo, Function.identity(),(oldValue, newValue)->newValue));
        //拆箱商品
        TradeItemVO item = trade.getTradeItems().stream().findAny().orElse(null);
        Map<Long, TradeItemVO> devanningSkuItemMap = Maps.newHashMap();
        if(Objects.nonNull(item.getDevanningId())){
            devanningSkuItemMap = trade.getTradeItems().stream().collect(Collectors.toMap(TradeItemVO::getDevanningId, Function.identity()));
        }
        // 赠品
        Map<String, TradeItemVO> giftItemMap = trade.getGifts().stream().collect(Collectors.toMap(TradeItemVO::getSkuNo, Function.identity()));
        // 组装发货信息
        List<ShippingItemVO> shippingItemList = new ArrayList<>();
        List<ShippingItemVO> giftItemList = new ArrayList<>();
        Map<Long, TradeItemVO> finalDevanningSkuItemMap = devanningSkuItemMap;
        List<ShippingItemDTO> itemDTOS = JSON.parseArray(ropRequest.getTradeItems(), ShippingItemDTO.class);
        itemDTOS.forEach(deliveryGoodsVO -> {
            setDeliveryGoodsItemNum(trade, deliveryGoodsVO);
            if(Objects.nonNull(deliveryGoodsVO.getDevanningId())){
                TradeItemVO tradeItem = finalDevanningSkuItemMap.get(deliveryGoodsVO.getDevanningId());
                if(Objects.nonNull(tradeItem)){
                    // 若发的数量大于购买数量，表示多出来的部分是赠品
                    if (tradeItem.getNum() < deliveryGoodsVO.getItemNum()) {
                        ShippingItemVO giftItemShip = new ShippingItemVO();
                        KsBeanUtil.copyProperties(tradeItem,giftItemShip);
                        giftItemShip.setSpecDetails(tradeItem.getGoodsSubtitle());
                        giftItemShip.setListNo(tradeItem.getGoodsBatchNo());
                        giftItemShip.setItemNum(deliveryGoodsVO.getItemNum() - tradeItem.getNum());
                        giftItemList.add(giftItemShip);
                        ShippingItemVO shippingItemDTO = new ShippingItemVO();
                        KsBeanUtil.copyProperties(tradeItem,shippingItemDTO);
                        shippingItemDTO.setSpecDetails(tradeItem.getGoodsSubtitle());
                        shippingItemDTO.setListNo(tradeItem.getGoodsBatchNo());
                        shippingItemDTO.setItemNum(tradeItem.getNum());
                        shippingItemList.add(shippingItemDTO);
                    } else {
                        ShippingItemVO shippingItemDTO = new ShippingItemVO();
                        KsBeanUtil.copyProperties(tradeItem,shippingItemDTO);
                        shippingItemDTO.setSpecDetails(tradeItem.getGoodsSubtitle());
                        shippingItemDTO.setListNo(tradeItem.getGoodsBatchNo());
                        shippingItemDTO.setItemNum(deliveryGoodsVO.getItemNum());
                        shippingItemList.add(shippingItemDTO);
                    }
                }else{
                    TradeItemVO giftItem = giftItemMap.get(deliveryGoodsVO.getGoodsInfoNo());
                    // 发的是赠品
                    ShippingItemVO giftItemDTO = new ShippingItemVO();
                    KsBeanUtil.copyProperties(giftItem,giftItemDTO);
                    giftItemDTO.setItemNum(deliveryGoodsVO.getItemNum());
                    giftItemDTO.setSpecDetails(giftItem.getGoodsSubtitle());
                    giftItemDTO.setListNo(giftItem.getGoodsBatchNo());
                    giftItemList.add(giftItemDTO);
                }
            }else{
                // 发的货有正常商品
                if (Objects.nonNull(skuItemMap.get(deliveryGoodsVO.getGoodsInfoNo()))) {
                    TradeItemVO tradeItem = skuItemMap.get(deliveryGoodsVO.getGoodsInfoNo());
                    // 若发的数量大于购买数量，表示多出来的部分是赠品
                    if (tradeItem.getNum() < deliveryGoodsVO.getItemNum()) {
                        ShippingItemVO giftItemShip = new ShippingItemVO();
                        KsBeanUtil.copyProperties(tradeItem,giftItemShip);
                        giftItemShip.setItemNum(deliveryGoodsVO.getItemNum() - tradeItem.getNum());
                        giftItemList.add(giftItemShip);
                        ShippingItemVO shippingItemDTO = new ShippingItemVO();
                        KsBeanUtil.copyProperties(tradeItem,shippingItemDTO);
                        shippingItemDTO.setItemNum(tradeItem.getNum());
                        shippingItemList.add(shippingItemDTO);
                    } else {
                        ShippingItemVO shippingItemDTO = new ShippingItemVO();
                        KsBeanUtil.copyProperties(tradeItem,shippingItemDTO);
                        shippingItemDTO.setItemNum(deliveryGoodsVO.getItemNum());
                        shippingItemList.add(shippingItemDTO);
                    }
                } else {
                    TradeItemVO giftItem = giftItemMap.get(deliveryGoodsVO.getGoodsInfoNo());
                    // 发的是赠品
                    ShippingItemVO giftItemDTO = new ShippingItemVO();
                    KsBeanUtil.copyProperties(giftItem,giftItemDTO);
                    giftItemDTO.setItemNum(deliveryGoodsVO.getItemNum());
                    giftItemList.add(giftItemDTO);
                }
            }
        });
        return TradeItemsGiftsVo.builder().gifts(giftItemList).tradeItems(shippingItemList).build();
    }

    private static void setDeliveryGoodsItemNum(TradeVO trade, ShippingItemDTO deliveryGoodsVO) {
        trade.getTradeItems().forEach(t->{
            //拆箱规格
            if(Objects.nonNull(t.getDevanningId())){
                if(t.getDevanningId().equals(deliveryGoodsVO.getDevanningId())){
                    Long num = deliveryGoodsVO.getDeliveryNum().divide(t.getAddStep(), 0, BigDecimal.ROUND_DOWN).longValue();
                    deliveryGoodsVO.setItemNum(num);
                }
            }else{
                if(t.getSkuNo().equals(deliveryGoodsVO.getGoodsInfoNo())){
                    Long num = deliveryGoodsVO.getDeliveryNum().divide(t.getAddStep(), 0, BigDecimal.ROUND_DOWN).longValue();
                    deliveryGoodsVO.setItemNum(num);
                }
            }
        });
        trade.getGifts().forEach(t->{
            if(t.getSkuNo().equals(deliveryGoodsVO.getGoodsInfoNo())){
                Long num = deliveryGoodsVO.getDeliveryNum().divide(t.getAddStep(), 0, BigDecimal.ROUND_DOWN).longValue();
                deliveryGoodsVO.setItemNum(num);
            }
        });
    }

    public static Operator getOperator(){
        return Operator.builder().platform(Platform.THIRD).name("wms平台").adminId("123456").userId("123456").ip("127.0.0.1").storeId("0").companyType(CompanyType.PLATFORM.toValue())
                .account("123456").companyInfoId(0L).build();
    }

    private List<ReceptionOrderRequest> getDeliverStructInfo(BaseOrderStructRequest baseOrderStructRequest){
        List<ReceptionOrderRequest> receptionOrderRequests = new ArrayList<>();
        List<WMSPushOrderRequest> orderRequestList = baseOrderStructRequest.getXmldata().getData().getOrderinfo();
        if(!CollectionUtils.isEmpty(orderRequestList)){
            orderRequestList.forEach(o->{
                ReceptionOrderRequest orderRequest = new ReceptionOrderRequest();
                //客户的erpId
                orderRequest.setCustomerErpId(o.getConsigneeId());
                //订单号
                orderRequest.setTradeId(o.getDocNo());
                //物流单号
                if("xyykd".equals(o.getDeliveryNo())||"1".equals(o.getDeliveryNo())){
                    String uuid = UUID.randomUUID().toString();
                    String deliveryNo = "xyy-wl-" + uuid.substring(29);
                    orderRequest.setDeliverNo(deliveryNo);
                }else{
                    orderRequest.setDeliverNo(o.getDeliveryNo());
                }
                //物流编码
                orderRequest.setDeliverCode(o.getUserDefine2());
                //发货时间
                orderRequest.setSendTime(DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_1));
                //仓库的编号
                orderRequest.setWareCode(o.getWarehouseId());
                //细单处理
                List<ShippingItemDTO> shippingItemDTOS = new ArrayList<>();
                List<WMSPushOrderDetailsRequest> details = o.getDetails();

                //物料批次号合并，防止自动退款
//                Map<String, List<WMSPushOrderDetailsRequest>> itemsMap = details.stream().collect(Collectors.groupingBy(WMSPushOrderDetailsRequest::getUserDefine5, Collectors.toList()));
//                itemsMap.forEach((k,v)->{
//                    ShippingItemDTO shippingItemDTO = new ShippingItemDTO();
//                    WMSPushOrderDetailsRequest wmsPushOrderDetailsRequest = v.stream().findFirst().orElse(null);
//                    //统计发货数量
//                    BigDecimal qtyShipped = v.stream().map(w -> w.getQtyShipped()).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    if(Objects.nonNull(wmsPushOrderDetailsRequest)){
//                        //wms发货的数量
//                        shippingItemDTO.setDeliveryNum(qtyShipped);
//                        //批次号
//                        shippingItemDTO.setGoodsBatchNo(wmsPushOrderDetailsRequest.getLotAtt01());
//                        //sku
//                        shippingItemDTO.setGoodsInfoNo(wmsPushOrderDetailsRequest.getUserDefine5());
//                        if(Objects.nonNull(wmsPushOrderDetailsRequest.getDedi08())){
//                            shippingItemDTO.setDevanningId(Long.parseLong(wmsPushOrderDetailsRequest.getDedi08()));
//                        }
//                    }
//                    shippingItemDTOS.add(shippingItemDTO);
//                });

                details.forEach(d->{
                    ShippingItemDTO shippingItemDTO = new ShippingItemDTO();
                    //wms发货的数量
                    shippingItemDTO.setDeliveryNum(d.getQtyShipped());
                    //批次号
                    shippingItemDTO.setGoodsBatchNo(d.getLotAtt01());
                    //sku
                    shippingItemDTO.setGoodsInfoNo(d.getUserDefine5());
                    if(Objects.nonNull(d.getDedi08()) && StringUtils.isNotBlank(d.getDedi08())){
                            shippingItemDTO.setDevanningId(Long.parseLong(d.getDedi08()));
                    }
                    shippingItemDTOS.add(shippingItemDTO);
                });
                if (StringUtils.isNotBlank(o.getUserDefine10())){
                    orderRequest.setDeliverAll(Integer.valueOf(o.getUserDefine10()));
                }

                //多批次商品合并
                List<ShippingItemDTO> shippingItemResult = new ArrayList<>();
                //正常商品
                List<ShippingItemDTO> devanns = shippingItemDTOS.stream().filter(s -> Objects.nonNull(s.getDevanningId())).collect(Collectors.toList());
                //赠品
                List<ShippingItemDTO> gifts = shippingItemDTOS.stream().filter(s -> Objects.isNull(s.getDevanningId())).collect(Collectors.toList());
                Map<Long, List<ShippingItemDTO>> listMap = devanns.stream().collect(Collectors.groupingBy(ShippingItemDTO::getDevanningId));
                listMap.forEach((g,d)->{
                    if(d.size() > 1){
                        ShippingItemDTO shippingItemDTO = d.stream().findAny().orElse(null);
                        BigDecimal reduce = d.stream().map(t -> t.getDeliveryNum()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        shippingItemDTO.setDeliveryNum(reduce);
                        shippingItemResult.add(shippingItemDTO);
                    }else{
                        shippingItemResult.add(d.stream().findFirst().orElse(null));
                    }
                });
                if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(gifts)){
                    shippingItemResult.addAll(gifts);
                }


                log.info("JSON.toJSONString(shippingItemDTOS) {}", JSON.toJSONString(shippingItemResult));

                orderRequest.setTradeItems(JSON.toJSONString(shippingItemResult));
                receptionOrderRequests.add(orderRequest);
            });
        }
        return receptionOrderRequests;
    }

    private String getResponseInfo(WmsBaseResponse response){
        Map<String,Object> firstMap = new HashMap<>();
        Map<String,Object> secondMap = new HashMap<>();
        secondMap.put("return",response);
        firstMap.put("Response",secondMap);
        log.info(" ============================== 返回给wms的数据 ========================== {}",JSONObject.toJSONString(firstMap));
        return JSONObject.toJSONString(firstMap);

    }

}
