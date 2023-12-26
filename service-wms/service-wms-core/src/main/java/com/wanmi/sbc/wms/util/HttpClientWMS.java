package com.wanmi.sbc.wms.util;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.wms.constant.WMMessageIdConstants;
import com.wanmi.sbc.wms.constant.WMMethodConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * @author baijianzhong
 * @ClassName HttpClientWMS
 * @Date 2020-07-08 14:52
 * @Description TODO
 **/
@Slf4j
public class HttpClientWMS {

    /**private static HttpClient client;

    static {
        client = new HttpClient();
        //设置连接的超时时间
        client.getHttpConnectionManager().getParams().setConnectionTimeout(
                40000);
        //设置读取数据的超时时间
        client.getHttpConnectionManager().getParams().setSoTimeout(
                40000);
    }*/


    public static String post(String url, Object requestObject, String method, Boolean confirmFlag) throws Exception{
        HttpClient client = new HttpClient();
        //设置连接的超时时间
        client.getHttpConnectionManager().getParams().setConnectionTimeout(
                40000);
        //设置读取数据的超时时间
        client.getHttpConnectionManager().getParams().setSoTimeout(
                40000);
        client.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(32);
        client.getHttpConnectionManager().getParams().setMaxTotalConnections(256);

        //组装数据
        String requestParam = JSONObject.toJSONString(getRequestStruct(method,requestObject));
        //加签
        String newSign2 = SignUtil.getSignForFLUX(requestParam);
        PostMethod post = new PostMethod(url);
        post.addRequestHeader("Content-Encoding", "UTF-8");
        post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        post.setParameter("method", method);
        post.setParameter("client_customerid", FluxConfig.clientCustomerId);
        post.setParameter("client_db", FluxConfig.clientDb);
        post.setParameter("messageid", getMessageId(method,confirmFlag));
        post.setParameter("apptoken", FluxConfig.token);
        post.setParameter("timestamp", DateUtil.nowTime());
        post.setParameter("appkey", FluxConfig.appKey);
        post.setParameter("sign", newSign2);
        post.setParameter("data", requestParam);
        log.info("============ 请求的地址：{}" + url);
        log.info("============ 请求参数：{}" + JSONObject.toJSONString(post.getParameters()));
        //发送请求
        String response;
        try {
            client.executeMethod(post);
            log.info("thread id ::: {}",Thread.currentThread().getId());
            log.info("post2 {}", post.getResponseBodyAsStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while((str = reader.readLine())!=null){
                stringBuffer.append(str);
            }
            response = stringBuffer.toString();
        } catch (final Exception e) {
            throw e;
        } finally {
            post.releaseConnection();
        }
        String result = URLDecoder.decode(response,"UTF-8");
        log.info("============ result请求返回参数：{}" + result);
        return result;
    }

    public static JSONObject getRequestStruct(String method,Object requestObject){
        JSONObject returnObject = new JSONObject();
        if(WMMethodConstants.QUERY_STOCK.equals(method)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("header",requestObject);
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("data", jsonObject);
            returnObject.put("xmldata", jsonObject2);
        }else if(WMMethodConstants.PUSH_ORDER.equals(method)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("header",requestObject);
            returnObject.put("xmldata", jsonObject);
        }else if(WMMethodConstants.CANCLE_SHIPMENT_ORDER.equals(method)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ordernos",requestObject);
            //库存查询需要加data
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("data", jsonObject);
            returnObject.put("xmldata", jsonObject2);
        }else if(WMMethodConstants.PUSH_RETURN.equals(method)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("header",requestObject);
            returnObject.put("xmldata", jsonObject);
        }
        return returnObject;
    }

    public static String getMessageId(String method,Boolean confirmFlag){
        String messageId = "";
        if(WMMethodConstants.QUERY_STOCK.equals(method)){
            messageId = WMMessageIdConstants.QUERY_STOCK;
        }else if(WMMethodConstants.PUSH_ORDER.equals(method)){
            messageId = WMMessageIdConstants.PUSH_ORDER;
        }else if(WMMethodConstants.CANCLE_SHIPMENT_ORDER.equals(method)){
            if(confirmFlag){
                messageId = WMMessageIdConstants.COMFIRM_SALES_ORDER;
            }else {
                messageId = WMMessageIdConstants.CANCLE_SHIPMENT_ORDER;
            }
        }else if(WMMethodConstants.PUSH_RETURN.equals(method)){
            messageId = WMMessageIdConstants.PUSH_RETURN;
        }
        return messageId;
    }
}
