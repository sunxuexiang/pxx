package com.wanmi.sbc.wms.requestwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.request.wms.*;
import com.wanmi.sbc.wms.constant.WMMessageIdConstants;
import com.wanmi.sbc.wms.constant.WMMethodConstants;
import com.wanmi.sbc.wms.constant.WMSErrorCode;
import com.wanmi.sbc.wms.pushwmslog.model.root.PushWmsLog;
import com.wanmi.sbc.wms.pushwmslog.service.PushWmsLogService;
import com.wanmi.sbc.wms.record.model.root.Record;
import com.wanmi.sbc.wms.record.service.RecordService;
import com.wanmi.sbc.wms.requestwms.model.Inventory;
import com.wanmi.sbc.wms.requestwms.model.WMSChargeBack;
import com.wanmi.sbc.wms.requestwms.model.WMSOrderCancel;
import com.wanmi.sbc.wms.requestwms.model.WMSPushOrder;
import com.wanmi.sbc.wms.requestwms.model.response.ResponseWMSReturn;
import com.wanmi.sbc.wms.util.HttpClientWMS;
import com.wanmi.sbc.wms.util.RequestJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import sun.misc.BASE64Encoder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @ClassName: OrderService
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 18:00
 * @Version: 1.0
 */
@Slf4j
@Service("OrderService")
public class WMSOrderService {

    @Autowired
    private RecordService recordService;

    /**
     * 平台退货订单下发接口(退单调用接口)
     */
    @Value("${wms.pusASN.url}")
    private String REQUEST_ASN_STR;

    /**
     * 申请退款接口
     */
    @Value("${wms.cancelOrder.url}")
    private String CANCEL_ORDER_STR;

    /**
     * 订单下发
     */
    @Value("${wms.putSales.url}")
    private String REQUEST_SALES_ORDER_STR;

    @Value("${wms.store.code}")
    private String systemCode;

    @Autowired
    private PushWmsLogService pushWmsLogService;

    /**
     * 功能描述: <br> 退单下发
     * 〈〉
     * @Param: [order]
     * @Return: java.lang.Boolean
     * @Author: yxb
     * @Date: 2020/5/15 19:04
     */
    public ResponseWMSReturn putASN(WMSChargeBack backOrder){
        backOrder.setAsnReferenceB(systemCode);
        return this.sendRequest(backOrder, REQUEST_ASN_STR, WMMethodConstants.PUSH_RETURN,false);
    }


    /**
     * 功能描述: <br> 退款
     * 〈〉
     * @Param: [WMSOrderCancel]
     * @Return: java.lang.Boolean
     * @Author: yxb
     * @Date: 2020/5/15 19:04
     */

    public ResponseWMSReturn cancelOrder(WMSOrderCancel wmsOrderCancel){
        return this.sendRequest(wmsOrderCancel, CANCEL_ORDER_STR, WMMethodConstants.CANCLE_SHIPMENT_ORDER,false);
    }


    /**
     * 功能描述: <br>订单下发
     * 〈〉
     * @Param: [WMSSalesOrderBack]
     * @Return: java.lang.Boolean
     * @Author: yxb
     * @Date: 2020/5/15 19:04
     */
    @Async("pushWmsExecutor")
    public ResponseWMSReturn putSalesOrder(@Valid WMSPushOrderRequest request) {
        try {
            log.info("线程：{} 推送订单开始,订单号={}", Thread.currentThread().getName(), request.getDocNo());
            WMSPushOrder wmsPushOrder = KsBeanUtil.convert(request, WMSPushOrder.class);

            wmsPushOrder.setSoReferenceB(systemCode);
            wmsPushOrder.setSoReferenceA(systemCode);
            this.sendRequest(wmsPushOrder, REQUEST_SALES_ORDER_STR, WMMethodConstants.PUSH_ORDER, false);
            log.info("线程：{} 推送订单结束,订单号={}", Thread.currentThread().getName(), request.getDocNo());
        } catch (Exception ex) {
            log.error("线程：{} 推送订单异常,订单号={}", Thread.currentThread().getName(), request.getDocNo());
        }
        return new ResponseWMSReturn();
    }



    /**
     * 功能描述: <br> 确认发货接口
     * 〈〉
     * @Param: [WMSOrderCancel]
     * @Return: java.lang.Boolean
     * @Author: yxb
     * @Date: 2020/5/15 19:04
     */

    public ResponseWMSReturn confirmSalesOrder(WMSOrderCancel wmsOrderCancel,PushWmsLog pushWmsLogVO){
        if (ObjectUtils.isEmpty(pushWmsLogVO)){
            pushWmsLogVO=new PushWmsLog();
        }else {
            //如果传了数据加入修改时间
            pushWmsLogVO.setUpdateTime(LocalDateTime.now());
        }
        pushWmsLogVO.setCustomerId(wmsOrderCancel.getCustomerId()).setDocNo(ObjectUtils.isEmpty(wmsOrderCancel.getDocNo())?"":wmsOrderCancel.getDocNo()).
        setErpCancelReason(wmsOrderCancel.getErpCancelReason()).setPPrarmJson(JSON.toJSONString(wmsOrderCancel)).
        setWarehouseId(wmsOrderCancel.getWarehouseId()).setOrderType(wmsOrderCancel.getOrderType());
        ResponseWMSReturn responseWMSReturn =null;
        try {
            responseWMSReturn = this.sendRequest(wmsOrderCancel, CANCEL_ORDER_STR, WMMethodConstants.CANCLE_SHIPMENT_ORDER, true);
            if (!ObjectUtils.isEmpty(responseWMSReturn)){
                pushWmsLogVO.setStatues(StringUtils.isEmpty(responseWMSReturn.getReturnFlag())?800:Integer.valueOf(responseWMSReturn.getReturnFlag()));
                pushWmsLogVO.setResposeInfo(JSON.toJSONString(responseWMSReturn));
            }
        }catch (Exception e){
            if (e instanceof RestClientResponseException){
                RestClientResponseException status=(RestClientResponseException) e;
                pushWmsLogVO.setStatues(status.getRawStatusCode());
            }else {
                pushWmsLogVO.setStatues(500);
            }
            pushWmsLogVO.setErroInfo(ObjectUtils.isEmpty(e.getMessage())?e.toString():e.getMessage());

        }finally {
            pushWmsLogService.SaveOrUpdate(pushWmsLogVO);
        }
        return responseWMSReturn;
    }


    /**
     * 统一发送请求
     * @param requestParam
     * @param url
     * @param method
     * @return
     */
    private ResponseWMSReturn sendRequest(Object requestParam, String url, String method, Boolean confirmFlag){
        JSONObject response;
        Record record=RequestJsonUtil.createRecordPost(url, JSONObject.toJSONString(requestParam));
        try {
            //组装请求记录实体类
            String result = HttpClientWMS.post(url, requestParam, method, confirmFlag);
            response = JSONObject.parseObject(result,JSONObject.class);
            record.setStatus("200");
        }catch (Exception e){
            log.info("统一发送wms请求接口报错日志....:::{}", e.getMessage());
            if (e instanceof RestClientResponseException){
                RestClientResponseException status=(RestClientResponseException) e;
                record.setStatus(String.valueOf(status.getRawStatusCode()));
            }else {
                record.setStatus("500");
            }
            recordService.add(record);
            throw new SbcRuntimeException(WMSErrorCode.PUSH_TIME_OUT);
        }
        if (ObjectUtils.isEmpty(response)){
            recordService.add(record);
            return new ResponseWMSReturn();
        }
        //请求结果返回
        record.setResposeInfo(response.toJSONString());
        JSONObject returnJson = response.getJSONObject("Response").getJSONObject("return");
        recordService.add(record);
        return returnJson.toJavaObject(ResponseWMSReturn.class);
    }

    public static void main(String[] args) {


//        JSONObject response = JSONObject.parseObject("{\"Response\":{\"return\":{\"returnFlag\":\"1\",\"returnCode\":\"0000\",\"returnDesc\":\"消息处理成功\",\"resultInfo\":[]}}}",JSONObject.class);
//        JSONObject returnJson = response.getJSONObject("Response").getJSONObject("return");
//        ResponseWMSReturn wmsReturn = returnJson.toJavaObject(ResponseWMSReturn.class);
//        System.out.println(wmsReturn.getReturnCode());

        try
        {
            HttpClient client = new HttpClient();
            JSONObject jsonObject = HttpClientWMS.getRequestStruct(WMMethodConstants.QUERY_STOCK,getParams());
//            JSONObject jsonObject = HttpClientWMS.getRequestStruct(WMMethodConstants.PUSH_RETURN,getParams());
            String fluxurl="http://47.104.108.89:19192/datahub/FluxWmsJsonApiv4/";
//            String fluxurl ="http://47.104.108.89:19192/putSalesOrder";
            String appSecret="1234567890";
            String jsonStr=JSONObject.toJSONString(jsonObject);
            String newsign = appSecret+jsonStr+appSecret;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(newsign.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String newsign2 = new BigInteger(1, md.digest()).toString(16);
            //以0开头,BigInteger会把0省略掉，需补全至32位
            int j = 32-newsign2.length();
            for (int i = 0; i < j; i++) {
                newsign2 = "0"+newsign2;
            }
            BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            newsign2 = encoder.encode(newsign2.getBytes());//base64编码

            PostMethod post  = new PostMethod(fluxurl);
            post.addRequestHeader("Content-Encoding","UTF-8");
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            post.setParameter("method", WMMethodConstants.QUERY_STOCK); //接口API方法名，根据接口做相应
            post.setParameter("client_customerid", "FLUXWMSJSON_V4"); //固定值 待提供
            post.setParameter("client_db", "FLUXWMSJSONDB");//固定值 待提供
            post.setParameter("messageid", WMMessageIdConstants.QUERY_STOCK);//消息代码，根据接口做相应调整
            post.setParameter("apptoken", "80AC1A3F-F949-492C-A024-7044B28C8025");
            post.setParameter("timestamp", "2020-08-18 16:00:21");
            post.setParameter("appkey", "test");
            post.setParameter("sign", URLEncoder.encode(newsign2.toUpperCase(), "utf-8"));//URL编码
            post.setParameter("data", jsonStr);//URL编码
            //设置连接的超时时间
            client.getHttpConnectionManager().getParams().setConnectionTimeout(
                    6000);
            //设置读取数据的超时时间
            client.getHttpConnectionManager().getParams().setSoTimeout(
                    6000);
            System.out.println("==========requstParams:" + JSONObject.toJSONString(post.getParameters()));
            client.executeMethod(post);
            System.out.println(URLDecoder.decode(post.getResponseBodyAsString(),"UTF-8"));

            JSONObject object = JSONObject.parseObject(URLDecoder.decode(post.getResponseBodyAsString(),"UTF-8"),JSONObject.class);
            JSONObject returnCode = object.getJSONObject("return");
            JSONObject items = object.getJSONObject("items");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Object getParams(){
//        List<String> skuIds = new ArrayList<>();
//        skuIds.add("001.001.004.001");
//        skuIds.add("002.001.001.102");
//        String skuStr = skuIds.toString().trim();

        //库存查询
        Inventory inventory = new Inventory();
        inventory.setWarehouseID("WH01");
//        inventory.setCustomerID("XYY");
//        inventory.setSkus(skuStr.substring(1,skuStr.length() -1));
//        inventory.setSKU("001.001.002.064");
        inventory.setSKU("003.004.042.005");
        inventory.setLotatt04("001");
//        inventory.setPageSize(20);
//        inventory.setPageNo(1);
//        inventory.setSKU("001.001.004.001");
        //订单推送
        WMSPushOrderDetailsRequest orderDetails = WMSPushOrderDetailsRequest.builder()
                .price(BigDecimal.ONE)
                .sku("001.001.002.071")
                .qtyOrdered(BigDecimal.ONE)
                .qtyOrdered_each(BigDecimal.ONE)
                .lineNo(2)
                .customerId("XYY")
                .userDefine1(1)
                .userDefine5("1234567890")
                .build();
        WMSPushOrderRequest orderRequest = WMSPushOrderRequest.builder()
                .warehouseId("WH01")
                .customerId("XYY")
                .orderType("XSCK")
                .soReferenceA("001")
                .consigneeId("ds-94fd-59384e9e512d")
                .consigneeName("柏某某")
                .consigneeAddress1("软件大道新华汇118号")
                .docNo("O202007091738513")
                .addWho("DS")
                .soReferenceD("电商订单")
                .orderTime(DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_1))
                .details(Arrays.asList(orderDetails))
                .soReferenceB("001")
                .build();

        //取消订单
        WMSOrderCancelRequest orderCancelRequest = WMSOrderCancelRequest.builder()
                .docNo("R202008181722446844")
                .customerId("XYY")
                .erpCancelReason("不要了")
                .orderType("XSCK")//这里的类型是根据单子的类型来定的
                .warehouseId("WH01")
//                .consigneeId("ds-94fd-59384e9e512d")//收货人
                .build();

        //退单
        WMSChargeBackDetailsRequest backDetailsRequest = WMSChargeBackDetailsRequest.builder()
                .sku("001.002.009.010")
                .expectedQty(BigDecimal.ONE)
                .totalPrice(BigDecimal.valueOf(6.79))
                .referenceNo("R202008144301")
                .userDefine5("1234567890")
                .lineNo(1)
                .build();
        WMSChargeBackRequest chargeBackRequest = WMSChargeBackRequest.builder()
                .warehouseId("WH01")
                .customerId("XYY")
                .soReferenceD("电商退单")
                .asnType("XSTHRK")
                .docNo("R202008144301")//订单号
                .asnReferenceA("O202008181542201")//退单号
                .asnCreationTime("2020-08-18 16:00:21")
                .details(Arrays.asList(backDetailsRequest))
                .asnReferenceB("001")
                .userDefine2("xyy")
                .supplierName("喜吖吖食品城")
                .supplierId("XYY")
                .build();
        return inventory;
    }
}
