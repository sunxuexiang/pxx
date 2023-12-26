package com.wanmi.sbc.util;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.ResultCode;
import lombok.extern.slf4j.Slf4j;
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
 * @author baijianzhong
 * @ClassName HttpClientUtil
 * @Date 2020-06-04 18:35
 * @Description TODO
 **/
@Slf4j
public class HttpClientUtil {

    /**
     * http请求的配置
     */
    private static RequestConfig config = RequestConfig.custom()
            .setConnectionRequestTimeout(1000)
            .setConnectTimeout(3000)
            .setSocketTimeout(3000)
            .build();

    private static CloseableHttpClient client = HttpClients.createDefault();

    /**
     * post请求
     *
     * @param url
     * @param requestMap
     * @return
     */
    public static HttpResult post(String url, Map<String, Object> requestMap) {
        //1.创建请求类型
        HttpPost httpPost = new HttpPost(url);
        //2.设置请求的配置信息
        httpPost.setConfig(config);
        //3.设置请求的内容 —— (参数不用json)
        httpPost.setEntity(new StringEntity(JSONObject.toJSONString(requestMap), ContentType.APPLICATION_JSON));
        log.info("=========== erp 请求 url: {} ;============== 请求参数：{}",url,requestMap);
        //4.发送请求
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            String resultDate = EntityUtils.toString(response.getEntity(), "utf-8");
            int status = response.getStatusLine().getStatusCode();
            return new HttpResult(resultDate, String.valueOf(status));
        } catch (IOException e) {
            return new HttpResult("--- 接口异常：" + e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * get请求
     *
     * @param url
     * @param requestMap
     * @return
     */
    public static HttpResult get(String url, Map<String, Object> requestMap) {
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
            return new HttpResult(resultData, String.valueOf(status));
        } catch (IOException e) {
            return new HttpResult("--- 接口异常：" + e.getMessage(), ResultCode.FAILED);
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
