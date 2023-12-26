package com.wanmi.sbc.wms.util;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.wms.constant.URLConstant;
import com.wanmi.sbc.wms.record.model.root.Record;
import com.wanmi.sbc.wms.requestwms.model.WMSRequestSystemParam;

import java.net.URLEncoder;
import java.time.LocalDateTime;

/**
 * @ClassName: RequestJson
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 16:21
 * @Version: 1.0
 */
public class RequestJsonUtil {
    private static final String CLIENT_CUSTMER="FLUXWMSJSON_V4";
    private static final String CLIENT_DB="FLUXWMSJSONDB";

    public static JSONObject reustJosnBase(WMSRequestSystemParam param){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method",param.getMethod());
        jsonObject.put("client_customerid", param.getClientCustomerID());
        jsonObject.put("client_db",param.getClientDB());
        jsonObject.put("messageid",param.getMessageID());
        jsonObject.put("apptoken",param.getAppToken());
        jsonObject.put("appkey",param.getAppKey());
        jsonObject.put("sign", param.getSign());
        jsonObject.put("timestamp", param.getTimeStamp());
        return jsonObject;
    }

    // TODO: 2020/5/7 apptoken sign 未填写 
//    /**
//     * 功能描述: 库存请求接口系统级别输入参数
//     * 〈〉
//     * @Param: []
//     * @Return: com.alibaba.fastjson.JSONObject
//     * @Author: lh
//     * @Date: 2020/5/7 18:04
//     */
//    public static JSONObject requestInventory(String requestParam){
//        WMSRequestSystemParam WMSRequestSystemParam = new WMSRequestSystemParam();
//        WMSRequestSystemParam.setMethod("queryInventory")
//                .setClientCustomerID(CLIENT_CUSTMER)
//                .setClientDB(CLIENT_DB)
//                .setMessageID(FluxConfig.messageId)
//                .setTimeStamp(DateUtil.nowTime())
//                .setAppKey(FluxConfig.appKey)
//                .setAppToken(FluxConfig.token)
//                .setSign(SignUtil.getSignForFLUX(requestParam));
//
//        return reustJosnBase(WMSRequestSystemParam);
//    }
//
//    /**
//     * 功能描述: 平台退货订单下发接口系统级别输入参数
//     * 〈〉
//     * @Param: []
//     * @Return: com.alibaba.fastjson.JSONObject
//     * @Author: yxb
//     * @Date: 2020/5/7 18:04
//     */
//    public static JSONObject requestASN(){
//        WMSRequestSystemParam WMSRequestSystemParam = new WMSRequestSystemParam();
//        WMSRequestSystemParam.setMethod("putASN").setClientCustomerID(CLIENT_CUSTMER)
//                .setClientDB(CLIENT_DB).setMessageID("ASN").setTimeStamp(DateUtil.nowTime())
//                .setAppKey(null).setAppToken(null).setSign(null);
//        return reustJosnBase(WMSRequestSystemParam);
//    }
//    /**
//     * 功能描述: 平台销售订单取消接口系统级别输入参数
//     * 〈〉
//     * @Param: []
//     * @Return: com.alibaba.fastjson.JSONObject
//     * @Author: yxb
//     * @Date: 2020/5/7 19:57
//     */
//    public static JSONObject cancelOrder(){
//        WMSRequestSystemParam WMSRequestSystemParam = new WMSRequestSystemParam();
//        WMSRequestSystemParam.setMethod("cancelShipmentOrder").setClientCustomerID(CLIENT_CUSTMER)
//                .setClientDB(CLIENT_DB).setMessageID("SOC").setTimeStamp(DateUtil.nowTime())
//                .setAppKey(null).setAppToken(null).setSign(null);
//        return reustJosnBase(WMSRequestSystemParam);
//    }
//
//    /**
//     * 功能描述: 平台销售订单下发接口系统级别输入参数
//     * 〈〉
//     * @Param: []
//     * @Return: com.alibaba.fastjson.JSONObject
//     * @Author: yxb
//     * @Date: 2020/5/7 19:57
//     */
//    public static JSONObject putSalesOrder(){
//        WMSRequestSystemParam WMSRequestSystemParam = new WMSRequestSystemParam();
//        WMSRequestSystemParam.setMethod("putSalesOrder").setClientCustomerID(CLIENT_CUSTMER)
//                .setClientDB(CLIENT_DB).setMessageID("SO").setTimeStamp(DateUtil.nowTime())
//                .setAppKey(null).setAppToken(null).setSign(null);
//        return reustJosnBase(WMSRequestSystemParam);
//    }

    /**
     * 功能描述: <br>组装请求记录实体类
     * 〈〉
     * @Param: [url, requestParam]
     * @Return: com.wanmi.sbc.wms.record.model.root.Record
     * @Author: yxb
     * @Date: 2020/5/8 15:23
     */
    public static Record createRecordPost(String url,String requestParam){
        Record record = new Record();
        record.setRequestUrl(url)
                .setRequestBody(requestParam)
                .setMethod(URLConstant.POST_INT)
                .setCreateTime(LocalDateTime.now());
        return record;
    }
}
