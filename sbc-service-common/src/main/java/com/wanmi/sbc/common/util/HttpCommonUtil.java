package com.wanmi.sbc.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.base.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Slf4j
public class HttpCommonUtil {
    /**
     * http请求的配置
     */
    private static RequestConfig config = RequestConfig.custom()
                                                       .setConnectionRequestTimeout(60000)
                                                       .setConnectTimeout(60000)
                                                       .setSocketTimeout(240000)
                                                       .build();

    private static CloseableHttpClient client = HttpClients.createDefault();

    /**
     * postHeader请求  在头部添加信息
     *
     * @param url
     * @param requestMap
     * @return
     */
    public static HttpCommonResult postHeader(String url, Map<String, Object> requestMap,String header) {
        //1.创建请求类型
        HttpPost httpPost = new HttpPost(url);
        //2.设置请求的配置信息
        httpPost.setConfig(config);
        //3.设置请求的内容 —— (参数不用json)


        httpPost.setEntity(new StringEntity(JSONObject.toJSONString(requestMap, SerializerFeature.SortField,SerializerFeature.WriteMapNullValue), ContentType.APPLICATION_JSON));
        log.info("=========== kingdeeHeader 请求 url: {} ;============== 请求参数：{}",url,JSONObject.toJSONString(requestMap));
        //向头部加Authorization
        if (header != null || StringUtils.isNotEmpty(header)){
            httpPost.setHeader("Authorization",header);
        }


        //4.发送请求
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            log.info("HttpCommonUtil.postHeader response:{}",JSONObject.toJSONString(response));
            String resultDate = EntityUtils.toString(response.getEntity(), "utf-8");
            log.info("=========== kingdeeHeader 请求 url: {} resultDate：{}",url,resultDate);
            int status = response.getStatusLine().getStatusCode();
            HttpCommonResult result = new HttpCommonResult(resultDate, String.valueOf(status));
//            log.info("=========== kingdeeHeader 请求 url: {} 返回：{}",url,result.getResultData());
            return result;
        } catch (IOException e) {
            HttpCommonResult resultError = new HttpCommonResult("--- 接口异常：" + e.getMessage(), ResultCode.FAILED);
            log.info("=========== kingdeeHeader 请求 url: {} 接口异常返回：{}",url,resultError.getResultData());
            return resultError;
        }finally {
            //释放连接
            if (response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * post请求
     *
     * @param url
     * @param requestMap
     * @return
     */
    public static HttpCommonResult post(String url, Map<String, Object> requestMap) {
        //1.创建请求类型
        HttpPost httpPost = new HttpPost(url);
        //2.设置请求的配置信息
        httpPost.setConfig(config);
        //3.设置请求的内容 —— (参数不用json)
        httpPost.setEntity(new StringEntity(JSONObject.toJSONString(requestMap), ContentType.APPLICATION_JSON));
        log.info("=========== kingdee 请求 url: {} ;============== 请求参数：{}",url,requestMap);
        //4.发送请求
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            String resultDate = EntityUtils.toString(response.getEntity(), "utf-8");
            int status = response.getStatusLine().getStatusCode();
            return new HttpCommonResult(resultDate, String.valueOf(status));
        } catch (IOException e) {
            return new HttpCommonResult("--- 接口异常：" + e.getMessage(), ResultCode.FAILED);
        }finally {
            //释放连接
            if (response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * get请求
     *
     * @param url
     * @param requestMap
     * @return
     */
    public static HttpCommonResult get(String url, Map<String, Object> requestMap) {
        String queryParam = getQueryParam(requestMap);
        //1.创建请求类型
        HttpGet httpGet = new HttpGet(url + queryParam);
        //2.设置请求的配置信息
        httpGet.setConfig(config);
        log.info("=========== erp 请求 url: {} ;============== 请求参数：{}",url,requestMap);
        //3.发送请求
        try {
            HttpResponse response = client.execute(httpGet);
            String resultData = EntityUtils.toString(response.getEntity());
            int status = response.getStatusLine().getStatusCode();
            return new HttpCommonResult(resultData, String.valueOf(status));
        } catch (IOException e) {
            return new HttpCommonResult("--- 接口异常：" + e.getMessage(), ResultCode.FAILED);
        }finally {
            //释放连接
            httpGet.releaseConnection();
        }
    }

    /**
     * 将map参数转换为string
     *
     * @param requestMap
     * @return
     */
    private static String getQueryParam(Map<String, Object> requestMap) {
        if (requestMap.isEmpty()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("?");
        Set<String> keySets = requestMap.keySet();
        for (String key : keySets) {
            stringBuffer.append(key);
            stringBuffer.append("=");
            stringBuffer.append(requestMap.get(key));
            stringBuffer.append("&");
        }
        return stringBuffer.substring(0, stringBuffer.length() - 1);
    }

}
