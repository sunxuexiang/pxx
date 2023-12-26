package com.wanmi.sbc.pay.service;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.*;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.pay.bean.enums.TradeType;
import com.wanmi.sbc.pay.bean.enums.WxPayTradeType;
import com.wanmi.sbc.pay.model.root.PayChannelItem;
import com.wanmi.sbc.pay.model.root.PayGatewayConfig;
import com.wanmi.sbc.pay.model.root.PayRecord;
import com.wanmi.sbc.pay.model.root.PayTradeRecord;
import com.wanmi.sbc.pay.repository.GatewayConfigRepository;
import com.wanmi.sbc.pay.repository.PayRecordRepository;
import com.wanmi.sbc.pay.repository.TradeRecordRepository;
import com.wanmi.sbc.pay.utils.GeneratorUtils;
//import com.github.wxpay.sdk.WXPayUtil;
import com.wanmi.sbc.pay.weixinpaysdk.WXPayUtil;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 微信支付
 */
@Slf4j
@Service
public class WxPayService {

    private static int socketTimeout = 10000;// 连接超时时间,默认10秒
    private static int connectTimeout = 30000;// 传输超时时间,默认30秒
    private static RequestConfig requestConfig;// 请求器的配置
    private static CloseableHttpClient httpClient;// HTTP请求器

    private static final String WXPAYAPPTYPE = "APP"; //微信支付类型--为app，对应调用参数对应开放平台参数

    private static final String WXPAYREFUNDURL = "https://api.mch.weixin.qq.com/secapi/pay/refund"; //微信退款调用微信接口地址

    private static final String WXUNIFIEDORDERURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";//微信支付调用微信接口地址

    private static final String WXPAYCOMPANYPAYMENTURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";//微信企业付款到零钱接口地址

    private static final String WXPAYORDERQUERY = "https://api.mch.weixin.qq.com/pay/orderquery";//查询订单支付单详情

    private static final String WXCLOSEORDER = "https://api.mch.weixin.qq.com/pay/closeorder";//关闭订单支付单


    @Autowired
    private TradeRecordRepository recordRepository;

    @Autowired
    private GatewayConfigRepository gatewayConfigRepository;

    @Autowired
    private PayDataService payDataService;

    @Autowired
    private PayRecordRepository payRecordRepository;


    /**
     * 统一下单接口--native扫码支付（pc扫码支付）
     *
     * @param request
     */
    @Transactional
    public WxPayForNativeResponse wxPayForNative(WxPayForNativeRequest request) {
        WxPayForNativeResponse response = new WxPayForNativeResponse();
        try {
            WxPayForNativeBaseRequest baseRequest =  KsBeanUtil.convert(request, WxPayForNativeBaseRequest.class);

            //将订单对象转为xml格式  
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
            //添加交易记录
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            payTradeRecordRequest.setBusinessId(request.getPay_order_no());
            payTradeRecordRequest.setPayOrderNo(request.getOut_trade_no());
            payTradeRecordRequest.setBusinessId(request.getOut_trade_no());
            payTradeRecordRequest.setApplyPrice(new BigDecimal(request.getTotal_fee()).divide(new BigDecimal(100)).
                    setScale(2, BigDecimal.ROUND_DOWN));
            payTradeRecordRequest.setClientIp(request.getSpbill_create_ip());
            payTradeRecordRequest.setChannelItemId(14L);
            log.info("封装支付记录数据 {} - {}", JSON.toJSONString(payTradeRecordRequest), JSON.toJSONString(request));
            payTradeRecordRequest.setPayOrderNo(request.getOut_trade_no());
            log.info("获取扫码链接保存交易记录开始==============================>{}",payTradeRecordRequest.getPayOrderNo());
            addTradeRecord(payTradeRecordRequest);
            log.info("获取扫码链接保存交易记录结束==============================>{}",payTradeRecordRequest.getPayOrderNo());
            response = wxPayUnifiedOrder(xStream.toXML(baseRequest), WxPayForNativeResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setTime_stamp(String.valueOf(WXPayUtil.getCurrentTimestamp()));
        return response;
    }

    /**
     * 统一下单接口--非微信浏览器h5支付
     *
     * @param request
     */
    @Transactional
    public WxPayForMWebResponse wxPayForMWeb(WxPayForMWebRequest request) {
        WxPayForMWebResponse response = new WxPayForMWebResponse();
        try {
            WxPayForMWebBaseRequest baseRequest =  KsBeanUtil.convert(request, WxPayForMWebBaseRequest.class);

            //将订单对象转为xml格式
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
            //添加交易记录
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            payTradeRecordRequest.setBusinessId(request.getOut_trade_no());
            payTradeRecordRequest.setApplyPrice(new BigDecimal(request.getTotal_fee()).divide(new BigDecimal(100)).
                    setScale(2, BigDecimal.ROUND_DOWN));
            payTradeRecordRequest.setClientIp(request.getSpbill_create_ip());
            payTradeRecordRequest.setChannelItemId(15L);
            addTradeRecord(payTradeRecordRequest);
            response = wxPayUnifiedOrder(xStream.toXML(baseRequest), WxPayForMWebResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 统一下单接口--微信浏览器内JSApi支付
     *
     * @param request
     */
    @Transactional
    public WxPayForJSApiResponse wxPayForJSApi(WxPayForJSApiRequest request) {
        WxPayForJSApiResponse response = new WxPayForJSApiResponse();
        try {
            WxPayForJSApiBaseRequest baseRequest =  KsBeanUtil.convert(request, WxPayForJSApiBaseRequest.class);
            //将订单对象转为xml格式
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForJSApiBaseRequest.class);//根元素名需要是xml
            //添加交易记录
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            payTradeRecordRequest.setBusinessId(request.getOut_trade_no());
            payTradeRecordRequest.setPayOrderNo(request.getPayOrderNo());
            payTradeRecordRequest.setApplyPrice(new BigDecimal(request.getTotal_fee()).divide(new BigDecimal(100)).
                    setScale(2, BigDecimal.ROUND_DOWN));
            payTradeRecordRequest.setClientIp(request.getSpbill_create_ip());
            payTradeRecordRequest.setChannelItemId(16L);
            addTradeRecord(payTradeRecordRequest);

            // 保存拉起支付记录
            PayRecord payRecord = new PayRecord();
            payRecord.setBusinessId(payTradeRecordRequest.getBusinessId());
            payRecord.setApplyPrice(payTradeRecordRequest.getApplyPrice());
            payRecord.setChannelItemId(payTradeRecordRequest.getChannelItemId());
            payRecord.setCreateTime(LocalDateTime.now());
            payRecord.setPayOrderNo(payTradeRecordRequest.getPayOrderNo());
            payRecord.setStatus(0);
            payRecordRepository.saveAndFlush(payRecord);

            xStream.omitField(WxPayForJSApiRequest.class,"payOrderNo");
            baseRequest.setOut_trade_no(request.getPayOrderNo());
            response = wxPayUnifiedOrder(xStream.toXML(baseRequest), WxPayForJSApiResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 统一下单接口--微信App支付
     *
     * @param request
     */
    @Transactional
    public WxPayForAppResponse wxPayForApp(WxPayForAppRequest request) {
        WxPayForAppResponse response = new WxPayForAppResponse();
        try {
            WxPayForAppBaseRequest baseRequest =  KsBeanUtil.convert(request, WxPayForAppBaseRequest.class);

            //将订单对象转为xml格式
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
            //添加交易记录
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            payTradeRecordRequest.setBusinessId(request.getOut_trade_no());
            payTradeRecordRequest.setApplyPrice(new BigDecimal(request.getTotal_fee()).divide(new BigDecimal(100)).
                    setScale(2, BigDecimal.ROUND_DOWN));
            payTradeRecordRequest.setClientIp(request.getSpbill_create_ip());
            payTradeRecordRequest.setChannelItemId(20L);
            addTradeRecord(payTradeRecordRequest);
            response = wxPayUnifiedOrder(xStream.toXML(baseRequest), WxPayForAppResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 调统一下单API
     *
     * @param orderInfo
     * @return
     */
    private <T> T wxPayUnifiedOrder(String orderInfo, Class<T> valueType) throws IllegalAccessException, InstantiationException {
        T t = valueType.newInstance();
        BufferedReader reader = null;
        BufferedOutputStream buffOutStr = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(WXUNIFIEDORDERURL).openConnection();
            //加入数据
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            buffOutStr = new BufferedOutputStream(conn.getOutputStream());
            buffOutStr.write(orderInfo.getBytes());
            buffOutStr.flush();
            buffOutStr.close();
            //获取输入流
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));//说明3(见文末)
            //将请求返回的内容通过xStream转换为UnifiedOrderResponse对象
            xStream.alias("xml", valueType);
            String wxPayOrderResponse = sb.toString();
            log.info("调用微信支付订单相应结果 {}", wxPayOrderResponse);
            t = (T) xStream.fromXML(wxPayOrderResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != buffOutStr) {
                    buffOutStr.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     * 微信支付退款
     *
     * @param refundRequest
     * @param type          微信退款类型--（App：app支付退款）
     */
    public WxPayRefundResponse wxPayRefund(WxPayRefundRequest refundRequest, String type, Long storeId) {
        //将订单对象转为xml格式
        XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
        String refundXmlStr = xStream.toXML(refundRequest);
        WxPayRefundResponse wxPayRefundResponse = new WxPayRefundResponse();
        try {
            //带证书的post
            // 加载证书
            initCert(refundRequest.getMch_id(), type, storeId);
            log.info("refundXmlStr======== {}" ,refundXmlStr);
            String resXml = postData(WXPAYREFUNDURL, refundXmlStr);
            //解析xml为集合,请打断点查看resXml详细信息
            Map<String, String> refundResultMap = WXPayUtil.xmlToMap(resXml);
            wxPayRefundResponse = (WxPayRefundResponse) WXPayUtil.mapToObject(refundResultMap, WxPayRefundResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wxPayRefundResponse;
    }

    /**
     * 微信企业付款到零钱
     *
     * @param request
     */
    public WxPayCompanyPaymentRsponse wxPayCompanyPayment(WxPayCompanyPaymentInfoRequest request) {
        PayGatewayConfig payGatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,request.getStoreId());
        String appId = payGatewayConfig.getAppId();
        String account = payGatewayConfig.getAccount();
        String apiKey = payGatewayConfig.getApiKey();
        if (request.getPayType() == WxPayTradeType.APP) {
            appId = payGatewayConfig.getOpenPlatformAppId();
            account = payGatewayConfig.getOpenPlatformAccount();
            apiKey = payGatewayConfig.getOpenPlatformApiKey();
        }
        WxPayCompanyPaymentRequest companyPaymentRequest = new WxPayCompanyPaymentRequest();
        companyPaymentRequest.setPartner_trade_no(request.getPartner_trade_no());
        companyPaymentRequest.setOpenid(request.getOpenid());
        companyPaymentRequest.setCheck_name(request.getCheck_name());
        companyPaymentRequest.setRe_user_name(request.getRe_user_name());
        companyPaymentRequest.setAmount(request.getAmount());
        companyPaymentRequest.setDesc(request.getDesc());
        companyPaymentRequest.setSpbill_create_ip(request.getSpbill_create_ip());
        companyPaymentRequest.setMch_appid(appId);
        companyPaymentRequest.setMchid(account);
        companyPaymentRequest.setNonce_str(WXPayUtil.generateNonceStr());
        try {
            Map<String, String> companyPaymentMap = WXPayUtil.objectToMap(companyPaymentRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(companyPaymentMap, apiKey);
            companyPaymentRequest.setSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将对象转为xml格式
        XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
        String refundXmlStr = xStream.toXML(companyPaymentRequest);
        WxPayCompanyPaymentRsponse wxPayCompanyPaymentRsponse = new WxPayCompanyPaymentRsponse();
        //带证书的post
        // 加载证书
        try {
            initCert(account, request.getPayType().toString(), request.getStoreId());
            String resXml = postData(WXPAYCOMPANYPAYMENTURL, refundXmlStr);
            //解析xml为集合,请打断点查看resXml详细信息
            Map<String, String> refundResultMap = WXPayUtil.xmlToMap(resXml);
            wxPayCompanyPaymentRsponse = (WxPayCompanyPaymentRsponse) WXPayUtil.mapToObject(refundResultMap, WxPayCompanyPaymentRsponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wxPayCompanyPaymentRsponse;
    }

    /**
     * 通过Https往API post xml数据
     *
     * @param url    API地址
     * @param xmlObj 要提交的XML数据对象
     * @return
     */
    public static String postData(String url, String xmlObj) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        // 得指明使用UTF-8编码,否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(xmlObj, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);
        // 根据默认超时限制初始化requestConfig
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        // 设置请求器的配置
        httpPost.setConfig(requestConfig);
        try {
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();
            try {
                result = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            httpPost.abort();
        }
        return result;
    }

    /**
     * 加载证书
     *
     * @param machId
     * @throws Exception
     */
    private void initCert(String machId, String type, Long storeId) throws Exception {
        PayGatewayConfig payGatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, storeId);
        // 证书密码,默认为商户ID
        String key = machId;
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取PKCS12证书文件
        InputStream instream = new ByteArrayInputStream(payGatewayConfig.getWxPayCertificate());
        //如果退款为app支付，则证书用对应微信开放平台参数
        if (type.equals(WXPAYAPPTYPE)) {
            instream = new ByteArrayInputStream(payGatewayConfig.getWxOpenPayCertificate());
        }
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, key.toCharArray())
                .build();
        // 指定TLS版本
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"},
                null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        // 设置httpclient的SSLSocketFactory
        httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }

    /**
     * 添加订单支付--交易记录
     *
     * @param request
     */
    private void addTradeRecord(PayTradeRecordRequest request) {
        //是否重复支付
        PayTradeRecord record = recordRepository.findByBusinessId(request.getBusinessId());
        String html = "";
        if (!Objects.isNull(record) && record.getStatus() == TradeStatus.SUCCEED) {
            log.info("获取扫码链接保存交易记录重复记录==============================>{}",request.getPayOrderNo());
            //如果重复支付，判断状态，已成功状态则做异常提示
            throw new SbcRuntimeException("K-100203");
        } else {
            if (record == null) {
                record = new PayTradeRecord();
                record.setId(GeneratorUtils.generatePT());
            }
            record.setApplyPrice(request.getApplyPrice());
            record.setBusinessId(request.getBusinessId());
            record.setClientIp(request.getClientIp());
            record.setChannelItemId(request.getChannelItemId());
            record.setTradeType(TradeType.PAY);
            record.setCreateTime(LocalDateTime.now());
            record.setStatus(TradeStatus.PROCESSING);
            record.setPayOrderNo(request.getPayOrderNo());
            log.info("插入支付记录 {}", JSON.toJSONString(record));
            recordRepository.saveAndFlush(record);
            log.info("获取扫码链接保存交易记录完成==============================>{}",request.getPayOrderNo());
        }
    }

    public WxPayOrderCloseForJSApiResponse closeWxPayOrderForJSApi(WxPayOrderCloseForJSApiRequest request){
        Assert.hasText(request.getPayOrderNo(), "payOrderNo不能为空");
        try {
            PayGatewayConfig payGatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, Constants.BOSS_DEFAULT_STORE_ID);
            String appId = payGatewayConfig.getAppId();
            String account = payGatewayConfig.getAccount();
            String apiKey = payGatewayConfig.getApiKey();
            //<xml>
            //   <appid>wx2421b1c4370ec43b</appid>
            //   <mch_id>10000100</mch_id>
            //   <nonce_str>4ca93f17ddf3443ceabf72f26d64fe0e</nonce_str>
            //   <out_trade_no>1415983244</out_trade_no>
            //   <sign>59FF1DF214B2D279A0EA7077C54DD95D</sign>
            //</xml>
            WxPayOrderDetailBaseRequest wxPayOrderDetailBaseRequest = new WxPayOrderDetailBaseRequest();
            wxPayOrderDetailBaseRequest.setAppid(appId);
            wxPayOrderDetailBaseRequest.setMch_id(account);
            wxPayOrderDetailBaseRequest.setNonce_str(WXPayUtil.generateNonceStr());
            wxPayOrderDetailBaseRequest.setOut_trade_no(request.getPayOrderNo());
            Map<String, String> wxPayOrderDetailMap = WXPayUtil.objectToMap(wxPayOrderDetailBaseRequest);
            wxPayOrderDetailMap.remove("payOrderNo");

            //获取签名
            String sign = WXPayUtil.generateSignature(wxPayOrderDetailMap, apiKey);
            wxPayOrderDetailBaseRequest.setSign(sign);
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
            String refundXmlStr = xStream.toXML(wxPayOrderDetailBaseRequest);
            return wxPayOrderCloseForJSApi(refundXmlStr, WxPayOrderCloseForJSApiResponse.class, WXCLOSEORDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new WxPayOrderCloseForJSApiResponse();
    }

    private <T> T wxPayOrderCloseForJSApi(String orderInfo, Class<T> valueType,String url) throws IllegalAccessException, InstantiationException {
        T t = valueType.newInstance();
        BufferedReader reader = null;
        BufferedOutputStream buffOutStr = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //加入数据
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            buffOutStr = new BufferedOutputStream(conn.getOutputStream());
            buffOutStr.write(orderInfo.getBytes());
            buffOutStr.flush();
            buffOutStr.close();
            //获取输入流
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String resultXml = sb.toString();
            resultXml = resultXml.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            resultXml = resultXml.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            resultXml = resultXml.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));//说明3(见文末)
            //将请求返回的内容通过xStream转换为UnifiedOrderResponse对象
            xStream.alias("xml", valueType);
            t = (T) xStream.fromXML(resultXml);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != buffOutStr) {
                    buffOutStr.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     * @Author lvzhenwei
     * @Description 获取订单支付详情
     * @Date 14:27 2020/9/17
     * @Param [request]
     * @return com.wanmi.sbc.pay.api.response.WxPayOrderDetailReponse
     **/
    public WxPayOrderDetailReponse getWxPayOrderDetail(WxPayOrderDetailRequest request){
        WxPayOrderDetailReponse wxPayOrderDetailReponse = new WxPayOrderDetailReponse();
        try {
            WxPayTradeType wxPayTradeType = WxPayTradeType.JSAPI;
            PayTradeRecord payTradeRecord = payDataService.queryByBusinessId(request.getBusinessId());
            if(Objects.nonNull(payTradeRecord)&&Objects.nonNull(payTradeRecord.getChannelItemId())){
                PayChannelItem channelItem = payDataService.queryItemById(payTradeRecord.getChannelItemId());
                if("wx_app".equals(channelItem.getCode())){
                    wxPayTradeType = WxPayTradeType.APP;
                }
            }
            PayGatewayConfig payGatewayConfig = gatewayConfigRepository.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT,request.getStoreId());
            String appId = payGatewayConfig.getAppId();
            String account = payGatewayConfig.getAccount();
            String apiKey = payGatewayConfig.getApiKey();
            if (wxPayTradeType == WxPayTradeType.APP) {
                appId = payGatewayConfig.getOpenPlatformAppId();
                account = payGatewayConfig.getOpenPlatformAccount();
                apiKey = payGatewayConfig.getOpenPlatformApiKey();
            }
            WxPayOrderDetailBaseRequest wxPayOrderDetailBaseRequest = new WxPayOrderDetailBaseRequest();
            wxPayOrderDetailBaseRequest.setAppid(appId);
            wxPayOrderDetailBaseRequest.setMch_id(account);
            wxPayOrderDetailBaseRequest.setNonce_str(WXPayUtil.generateNonceStr());
            wxPayOrderDetailBaseRequest.setOut_trade_no(request.getBusinessId());
            Map<String, String> wxPayOrderDetailMap = null;

            wxPayOrderDetailMap = WXPayUtil.objectToMap(wxPayOrderDetailBaseRequest);

            //获取签名
            String sign = WXPayUtil.generateSignature(wxPayOrderDetailMap, apiKey);
            wxPayOrderDetailBaseRequest.setSign(sign);
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
            String refundXmlStr = xStream.toXML(wxPayOrderDetailBaseRequest);
            wxPayOrderDetailReponse = wxPayOrderDetail(refundXmlStr,WxPayOrderDetailReponse.class, WXPAYORDERQUERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wxPayOrderDetailReponse;
    }


    private <T> T wxPayOrderDetail(String orderInfo, Class<T> valueType,String url) throws IllegalAccessException, InstantiationException {
        T t = valueType.newInstance();
        BufferedReader reader = null;
        BufferedOutputStream buffOutStr = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //加入数据
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            buffOutStr = new BufferedOutputStream(conn.getOutputStream());
            buffOutStr.write(orderInfo.getBytes());
            buffOutStr.flush();
            buffOutStr.close();
            //获取输入流
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String resultXml = sb.toString();
            resultXml = resultXml.replaceAll("<coupon_id_[0-9]{0,11}[^>]*>(.*?)</coupon_id_[0-9]{0,11}>", "");
            resultXml = resultXml.replaceAll("<coupon_type_[0-9]{0,11}[^>]*>(.*?)</coupon_type_[0-9]{0,11}>", "");
            resultXml = resultXml.replaceAll("<coupon_fee_[0-9]{0,11}[^>]*>(.*?)</coupon_fee_[0-9]{0,11}>", "");
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));//说明3(见文末)
            //将请求返回的内容通过xStream转换为UnifiedOrderResponse对象
            xStream.alias("xml", valueType);
            t = (T) xStream.fromXML(resultXml);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != buffOutStr) {
                    buffOutStr.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }
    public boolean isPayCompleted(CmbPayOrderRequest request) {
        Assert.hasText(request.getPayOrderNo(), "payOrderNo不能为空");

        PayGatewayConfig payGatewayConfig = payDataService.queryConfigByNameAndStoreId(PayGatewayEnum.WECHAT, request.getStoreId());
        //这里可以加验证，请求银联是否已经交易成功了
        String appId = payGatewayConfig.getAppId();
        String account = payGatewayConfig.getAccount();
        String apiKey = payGatewayConfig.getApiKey();

        WxPayOrderDetailBaseRequest wxPayOrderDetailBaseRequest = new WxPayOrderDetailBaseRequest();
        wxPayOrderDetailBaseRequest.setAppid(appId);
        wxPayOrderDetailBaseRequest.setMch_id(account);
        wxPayOrderDetailBaseRequest.setNonce_str(WXPayUtil.generateNonceStr());
        //按支付单号进行查询
        wxPayOrderDetailBaseRequest.setOut_trade_no(request.getPayOrderNo());

        try {
            Map<String, String> wxPayOrderDetailMap = WXPayUtil.objectToMap(wxPayOrderDetailBaseRequest);
            //获取签名
            String sign = WXPayUtil.generateSignature(wxPayOrderDetailMap, apiKey);

            wxPayOrderDetailBaseRequest.setSign(sign);
            XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            xStream.alias("xml", WxPayForNativeRequest.class);//根元素名需要是xml
            String refundXmlStr = xStream.toXML(wxPayOrderDetailBaseRequest);
            WxPayOrderDetailReponse wxPayOrderDetailReponse = wxPayOrderDetail(refundXmlStr, WxPayOrderDetailReponse.class, WXPAYORDERQUERY);

            if ("SUCCESS".equals(wxPayOrderDetailReponse.getReturn_code()) && "SUCCESS".equals(wxPayOrderDetailReponse.getResult_code())
                    && (
                    "SUCCESS".equals(wxPayOrderDetailReponse.getTrade_state())
                            || "USERPAYING".equals(wxPayOrderDetailReponse.getTrade_state())
            )
            ) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
