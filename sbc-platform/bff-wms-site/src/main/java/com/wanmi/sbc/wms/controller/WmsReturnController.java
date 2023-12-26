package com.wanmi.sbc.wms.controller;

import com.alibaba.fastjson.JSONObject;
import com.rop.client.CompositeResponse;
import com.wanmi.open.sdk.SdkClient;
import com.wanmi.open.sdk.request.ReturnOrderRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.wms.request.WMSChargeBackRequest;
import com.wanmi.sbc.wms.request.WmsBaseRequest;
import com.wanmi.sbc.wms.request.back.BaseReturnStructRequest;
import com.wanmi.sbc.wms.response.WmsBaseResponse;
import com.wanmi.sbc.wms.service.SendChainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author baijianzhong
 * @ClassName WmsTradeController
 * @Date 2020-07-15 21:02
 * @Description TODO  退单接受都是全部接受，有部分损坏的找物流，而不应该显现在平台
 **/
@Controller
@Slf4j
public class WmsReturnController {

    @Autowired
    private SendChainService sendChainService;

    @Value("${send.chain.flag:false}")
    private Boolean sendChainFlag;

    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ResponseBody
    public String deliverGoods(WmsBaseRequest wmsBaseRequest){
        log.info("===================== request info ================\n" + JSONObject.toJSONString(wmsBaseRequest));

        if (sendChainFlag) {
            String result = sendChainService.sendChain(wmsBaseRequest, "/receive");
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
        JSONObject jsonObject = JSONObject.parseObject(wmsBaseRequest.getData());
        log.info("===================== request info data ================\n" + JSONObject.toJSONString(jsonObject));
        BaseReturnStructRequest baseOrderStructRequest =  JSONObject.toJavaObject(jsonObject,BaseReturnStructRequest.class);
        List<ReturnOrderRequest> receptionOrderRequests = this.getDeliverStructInfo(baseOrderStructRequest);
        if(!CollectionUtils.isEmpty(receptionOrderRequests)){
            for(ReturnOrderRequest ropRequest: receptionOrderRequests){
                try {
                    //调用开放平台订单发货
                    SdkClient sdkClient = new SdkClient(wmsBaseRequest.getAppkey(), wmsBaseRequest.getApptoken());
                    CompositeResponse post = sdkClient.buildClientRequest().post(ropRequest, Object.class,
                            wmsBaseRequest.getMethod(), wmsBaseRequest.getMessageid());
                    BaseResponse baseResponse = JSONObject.parseObject(JSONObject.toJSONString(post.getSuccessResponse()), BaseResponse.class);
                    log.info("=================== open response: ================ {}",JSONObject.toJSONString(baseResponse));
                    if (Objects.nonNull(baseResponse) && CommonErrorCode.SUCCESSFUL.equals(baseResponse.getCode())) {
                        wmsBaseResponse.setReturnFlag(1);
                        wmsBaseResponse.setReturnCode("0000");
                        wmsBaseResponse.setReturnDesc("ok");
                    } else {
                        wmsBaseResponse.setReturnCode("0001");
                        wmsBaseResponse.setReturnFlag(0);
                        wmsBaseResponse.setReturnDesc("接口异常");
                    }
                }catch (Exception e){
                    wmsBaseResponse.setReturnCode("0001");
                    wmsBaseResponse.setReturnFlag(0);
                    wmsBaseResponse.setReturnDesc("接口异常");
                }
            }

        }
        return this.getResponseInfo(wmsBaseResponse);
    }

    private List<ReturnOrderRequest> getDeliverStructInfo(BaseReturnStructRequest baseOrderStructRequest){
        List<ReturnOrderRequest> returnOrderRequests = new ArrayList<>();
        List<WMSChargeBackRequest> orderRequestList = baseOrderStructRequest.getXmldata().getData().getOrderinfo();
        if(!CollectionUtils.isEmpty(orderRequestList)){
            orderRequestList.forEach(o->{
                ReturnOrderRequest orderRequest = new ReturnOrderRequest();
                //customerErpId
                orderRequest.setCustomerErpId(o.getSupplierId());
                //退单号
                orderRequest.setReturnOrderId(o.getDocNo());
                //收货时间
                orderRequest.setReceiveTime(DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_1));
                //仓库的编号
                orderRequest.setWareCode(o.getWarehouseId());
                //订单
                orderRequest.setReturnItems(JSONObject.toJSONString(o.getDetails()));
                //细单处理
//                List<ShippingItemDTO> shippingItemDTOS = new ArrayList<>();
//                List<WMSPushOrderDetailsRequest> details = o.getDetails();
//                details.forEach(d->{
//                    ShippingItemDTO shippingItemDTO = new ShippingItemDTO();
//                    //发货的数量
//                    shippingItemDTO.setItemNum(d.getQtyOrdered());
//                    //批次号
//                    shippingItemDTO.setGoodsBatchNo(d.getLotAtt01());
//                    //sku
//                    shippingItemDTO.setGoodsInfoNo(d.getSku());
//                    shippingItemDTOS.add(shippingItemDTO);
//                });
//                orderRequest.setTradeItems(JSON.toJSONString(shippingItemDTOS));
                returnOrderRequests.add(orderRequest);
            });
        }
        return returnOrderRequests;
    }

    private String getResponseInfo(WmsBaseResponse response){
        Map<String,Object> firstMap = new HashMap<>();
        Map<String,Object> secondMap = new HashMap<>();
        firstMap.put("Response",secondMap);
        secondMap.put("return",response);
        log.info(" ============================== 返回给wms的数据 ========================== {}",JSONObject.toJSONString(firstMap));
        return JSONObject.toJSONString(firstMap);

    }


    public static void main(String[] args) {
        String str = "{\"xmldata\":{\"data\":{\"orderinfo\":[{\"orderNo\":\"湖南海霸食品有限公司（乡村之恋）\",\"orderType\":\"CGRK\",\"customerId\":\"XYY\",\"warehouseId\":\"WH01\",\"userDefine1\":\"16548\",\"userDefine2\":\"30\",\"userDefine3\":\"\",\"userDefine4\":\"\",\"userDefine5\":\"\",\"userDefine6\":\"\",\"userDefine7\":\"\",\"userDefine8\":\"\",\"userDefine9\":\"\",\"userDefine10\":\"\",\"details\":[{\"sku\":\"003.003.035.011\",\"receivedQty\":\"80\",\"lotatt01\":\"2020-07-08\",\"lotatt02\":\"\",\"lotatt03\":\"\",\"lotatt04\":\"001\",\"lotatt05\":\"\",\"lotatt06\":\"N\",\"lotatt07\":\"\",\"lotatt08\":\"\",\"lotatt09\":\"\",\"lotatt10\":\"\",\"lotatt11\":\"\",\"lotatt12\":\"\",\"userDefine1\":\"\",\"userDefine2\":\"\",\"userDefine3\":\"\",\"userDefine4\":\"\",\"userDefine5\":\"\",\"userDefine6\":\"\",\"lotatt13\":\"\",\"lotatt14\":\"\",\"lotatt15\":\"\",\"lotatt16\":\"\",\"lineNo\":\"4\",\"referenceNo\":\"0\",\"lotatt17\":\"\",\"lotatt18\":\"\",\"lotatt19\":\"\",\"lotatt20\":\"\",\"lotatt21\":\"\",\"lotatt22\":\"\",\"lotatt23\":\"\",\"lotatt24\":\"\"},{\"sku\":\"003.003.035.004\",\"receivedQty\":\"10\",\"lotatt01\":\"2020-07-16\",\"lotatt02\":\"\",\"lotatt03\":\"\",\"lotatt04\":\"001\",\"lotatt05\":\"\",\"lotatt06\":\"N\",\"lotatt07\":\"\",\"lotatt08\":\"\",\"lotatt09\":\"\",\"lotatt10\":\"\",\"lotatt11\":\"\",\"lotatt12\":\"\",\"userDefine1\":\"\",\"userDefine2\":\"\",\"userDefine3\":\"\",\"userDefine4\":\"\",\"userDefine5\":\"\",\"userDefine6\":\"\",\"lotatt13\":\"\",\"lotatt14\":\"\",\"lotatt15\":\"\",\"lotatt16\":\"\",\"lineNo\":\"3\",\"referenceNo\":\"0\",\"lotatt17\":\"\",\"lotatt18\":\"\",\"lotatt19\":\"\",\"lotatt20\":\"\",\"lotatt21\":\"\",\"lotatt22\":\"\",\"lotatt23\":\"\",\"lotatt24\":\"\"}],\"receivedTime\":\"2020-07-2018:19:00\",\"docNo\":\"POORD016979\"}]}}}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        BaseReturnStructRequest baseOrderStructRequest =  JSONObject.toJavaObject(jsonObject,BaseReturnStructRequest.class);
        List<WMSChargeBackRequest> wmsChargeBackRequests = baseOrderStructRequest.getXmldata().getData().getOrderinfo();
        System.out.println(wmsChargeBackRequests);


    }

}
