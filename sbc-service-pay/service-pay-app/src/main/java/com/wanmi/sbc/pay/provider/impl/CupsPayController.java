package com.wanmi.sbc.pay.provider.impl;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.provider.CupsPayProvider;
import com.wanmi.sbc.pay.api.request.CmbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CupsPayRefundDataRequest;
import com.wanmi.sbc.pay.api.request.CupsPaySignRequest;
import com.wanmi.sbc.pay.api.response.CpusPayOrderResponse;
import com.wanmi.sbc.pay.api.response.CupsPayRefundResponse;
import com.wanmi.sbc.pay.service.CupsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: service-pay
 * @description: 银联
 * @create: 2019-01-28 16:30
 **/
@RestController
@Validated
@Slf4j
public class CupsPayController implements CupsPayProvider {


    @Autowired
    private CupsService cupsService;


    @Override
    public BaseResponse<CpusPayOrderResponse> cupsPayOrder(@RequestBody CmbPayOrderRequest cmbPayOrderRequest) {
        return BaseResponse.success(cupsService.cupsPayOrder(cmbPayOrderRequest));
    }

    @Override
    public BaseResponse<Boolean> cupsPaySign(CupsPaySignRequest cupsPaySignRequest) {
        return BaseResponse.success(cupsService.cupsPaySign(cupsPaySignRequest));
    }

    @Override
    public BaseResponse<CupsPayRefundResponse> cupsPayRefund(@RequestBody CupsPayRefundDataRequest cupsPayRefundDataRequest) {
        return BaseResponse.success(cupsService.cupsPayRefund(cupsPayRefundDataRequest));
    }

    @Override
    public BaseResponse<Boolean> isPayCompleted(CmbPayOrderRequest cmbPayOrderRequest) {
        return BaseResponse.success(cupsService.isPayCompleted(cmbPayOrderRequest));
    }

    @Override
    public BaseResponse<String> closePayOrder(Map<String, String> params) {
        Assert.notNull(params.get("payOrderNo"),"payOrderNo 不能为空");
        Assert.notNull(params.get("channelId"),"channelId 不能为空");
        Assert.notNull(params.get("storeId"),"storeId 不能为空");

        cupsService.closePayOrder(params.get("payOrderNo"), Long.valueOf(params.get("channelId")), Long.valueOf(params.get("storeId")));
        return BaseResponse.success("success");
    }


    public static void main(String[] args) throws Exception {

        //获取公式
       /* String sMerchantKey = "0123456789ASdf12";
        CmbDoBusinessRequest doBusinessRequest = new CmbDoBusinessRequest();
        doBusinessRequest.setVersion("1.0");
        doBusinessRequest.setCharset("UTF-8");
        doBusinessRequest.setSignType("SHA-256");

        CmbReqDataRequest reqDataRequest = new CmbReqDataRequest();
        reqDataRequest.setDateTime(DateUtil.format(new Date(), DateUtil.FMT_TIME_3));
        reqDataRequest.setTxCode("FBPK");
        reqDataRequest.setBranchNo("0731");
        reqDataRequest.setMerchantNo("000131");

        doBusinessRequest.setReqData(reqDataRequest);
        StringBuilder dbStrToSign = createCrmLinkString(doBusinessRequest.getReqData());
        log.info(dbStrToSign.toString());
        dbStrToSign.append("&").append(sMerchantKey);
        String dbSign =convertSHA256(dbStrToSign);
        doBusinessRequest.setSign(dbSign);

        HttpClient httpClient = new HttpClient("https://cmbchinab2b.bas.cmburl.cn/CmbBank_B2B/UI/NetPay/DoBusiness.ashx",10000,10000);
        Map<String, String> jsonRequestData = new HashMap<>();
        jsonRequestData.put("jsonRequestData", JSONObject.toJSONString(doBusinessRequest));
        jsonRequestData.put("charset", "UTF-8");
        String result =  httpClient.sendSring(jsonRequestData,"UTF-8");
        CmbDoBusinessResponse cmbDoBusinessResponse = JSONObject.parseObject(result, CmbDoBusinessResponse.class);*/


        //查询单笔订单API
       /* String sMerchantKey = "0123456789ASdf12";
        CmbQuerySingleOrderRequest cmbQuerySingleOrderRequest = new CmbQuerySingleOrderRequest();
        cmbQuerySingleOrderRequest.setVersion("2.0");
        cmbQuerySingleOrderRequest.setCharset("UTF-8");
        cmbQuerySingleOrderRequest.setSignType("SHA-256");

        CmbSingleOrderDataRequest cmbSingleOrderDataRequest = new CmbSingleOrderDataRequest();
        cmbSingleOrderDataRequest.setDateTime(DateUtil.format(new Date(), DateUtil.FMT_TIME_3));
        cmbSingleOrderDataRequest.setBranchNo("0731");
        cmbSingleOrderDataRequest.setMerchantNo("000131");
        cmbSingleOrderDataRequest.setType("B");
        //获取订单数据
        cmbSingleOrderDataRequest.setDate("20160623");//订单生成日期
        cmbSingleOrderDataRequest.setOrderNo("9999000001");
        cmbSingleOrderDataRequest.setOperatorNo("9999");
        cmbSingleOrderDataRequest.setBankSerialNo("");
        cmbQuerySingleOrderRequest.setReqData(cmbSingleOrderDataRequest);

        StringBuilder dbStrToSign = createCrmLinkString(cmbQuerySingleOrderRequest.getReqData());
        log.info(dbStrToSign.toString());
        dbStrToSign.append("&").append(sMerchantKey);
        String dbSign =convertSHA256(dbStrToSign);
        cmbQuerySingleOrderRequest.setSign(dbSign);

        HttpClient httpClient = new HttpClient("http://121.15.180.66:801/netpayment_directlink_nosession/BaseHttp.dll?QuerySingleOrder",10000,10000);
        Map<String, String> jsonRequestData = new HashMap<>();
        jsonRequestData.put("jsonRequestData", JSONObject.toJSONString(cmbQuerySingleOrderRequest));
        jsonRequestData.put("charset", "UTF-8");
        String result =  httpClient.sendSring(jsonRequestData,"UTF-8");
        CmbDoBusinessResponse cmbDoBusinessResponse = JSONObject.parseObject(result, CmbDoBusinessResponse.class);*/


        /*CmbCallBackRequest appRequest = new CmbCallBackRequest();

        //成功支付结果通知API
        StringBuilder strToSign = createCrmLinkString(appRequest.getNoticeData());

        //假设已排序字符串为strToSign
        //添加商户密钥
        strToSign.append("&").append(sMerchantKey);
        // 创建加密对象
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        // 传入要加密的字符串,按指定的字符集将字符串转换为字节流
        messageDigest.update(strToSign.toString().getBytes("UTF-8"));
        byte byteBuffer[] = messageDigest.digest();
        // 將 byte数组转换为16进制string
        String signStr = HexString(byteBuffer);
        isValidSignature(strToSign.toString(),signStr, sMerchantKey);*/



    }

}
