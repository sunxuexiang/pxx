package com.wanmi.sbc.order.util;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * sdk本地测试
 */
public class TestRequest {


    private static final String APP_KEY = "wm_1TyicBGs";
    private static final String APP_SECRET = "e6da28b3c82848e1863d27e9b46c0323";
    private static final String VERSION = "1.0.0";
    public static final String SIGN_METHOD_MD5 = "md5";
    public static final String CHARSET_UTF8 = "utf8";
    public static final String SIGN_METHOD_HMAC = "sha1";
    public static final String MESSAGE_FORMAT = "JSON";
    //正式环境需替换
    public static final String requestUrl = "http://localhost:8089/open/router";

    public static void main(String[] args) throws Exception{
        //————————————————————————发货接口调用————————————————————————
        // 1. 主单信息
        Map params = new HashMap();
        params.put("tradeId","O202007071353166333");
        params.put("wareCode","WH01");
        params.put("customerErpId","ds-94fd-59384e9e512d");
        params.put("receiveTime","2020-07-07 20:10:32");
        params.put("deliverNo","23424534153");
        params.put("deliverCode","xyykd");

        // 2. 细单信息
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(1L,"001.001.001.014",""));
        params.put("tradeItems", JSON.toJSONString(orderItems));

        // 3. 公共参数
        Map headParam = new HashMap();
        headParam.put("appKey",APP_KEY);
        headParam.put("appSecret",APP_SECRET);
        headParam.put("v",VERSION);
        headParam.put("method","reception.get.shipment");
        headParam.put("timestamp","2020-05-14 22:10:32");
        headParam.put("sign",signTopRequest(params,APP_SECRET,SIGN_METHOD_HMAC));
        headParam.put("messageFormat",MESSAGE_FORMAT);

        BaseResultWM str = doPost(requestUrl,headParam,params);
        System.out.println(str);
    }

    public static BaseResultWM doPost(String url, Map<String, String> mainParam, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (mainParam != null) {
                for (String key : mainParam.keySet()) {
                    builder.addParameter(key, mainParam.get(key));
                }
            }
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(builder.build());
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return JSON.parseObject(resultString,BaseResultWM.class);
    }

    public static String signTopRequest(Map<String, String> params, String secret, String signMethod) throws IOException {

        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        if (SIGN_METHOD_MD5.equals(signMethod)) {
            query.append(secret);
        }
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                query.append(key).append(value);
            }
        }

        // 第三步：使用MD5/HMAC加密
        byte[] bytes;
        if (SIGN_METHOD_HMAC.equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }

        // 第四步：把二进制转化为大写的十六进制(正确签名应该为32大写字符串，此方法需要时使用)
        return byte2hex(bytes);
    }

    public static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    public static byte[] encryptMD5(String data) throws IOException {
        return encryptMD5(new String(data.getBytes(),CHARSET_UTF8));
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem{
        //发货的数量
        public Long itemNum;
        //erp的商品编号
        public String goodsInfoNo;
        //批次号
        public String goodsBatchNo;
    }

    @Data
    public static class BaseResultWM implements Serializable {

        public String code;

        public String message;
    }
}
