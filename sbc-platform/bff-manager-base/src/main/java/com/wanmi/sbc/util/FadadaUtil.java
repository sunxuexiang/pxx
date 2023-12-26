package com.wanmi.sbc.util;


import com.fadada.sdk.api.AbstractApiParams;
import com.fadada.sdk.base.client.FddBaseClient;
import com.fadada.sdk.base.model.req.*;
import com.fadada.sdk.extra.client.FddExtraClient;
import com.fadada.sdk.extra.model.req.PushShortUrlSmsParams;
import com.fadada.sdk.extra.model.req.ShortUrlParams;
import com.fadada.sdk.verify.client.FddVerifyClient;
import com.fadada.sdk.verify.model.req.ApplyCertParams;
import com.fadada.sdk.verify.model.req.CompanyVerifyUrlParams;
import com.fadada.sdk.verify.model.req.FindCompanyCertParams;
import com.wanmi.ares.base.ResultCode;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
@Data
public class FadadaUtil {
    private static final String CODE = "code";
    private static final String DATA = "data";
    private static final String MSG = "msg";
    private static final String COMPANY_URL = "url";
    private static final String STATUS = "status";
    private static final String TRANSACTION_NO = "transactionNo";
    private static final String SIGNATURE_IMG_BASE64 = "signature_img_base64";
    private static final String SIGNATURE_ID = "signature_id";
    private static final String CONTRACT_STATUS = "contract_status";
    private static final String CONTRACT_STATUS_DESC = "contract_status_desc";

    //法大大配置信息
//    private static final String appId = "407545";
//    private static final String appSecret = "bC81SsTSCyhxxYRWRgxm5mrC";
//    private static final String url = "http://test.api.fabigbig.com:8888/api/";
//    private static final String v = "2.0";
//    private static final String appId = "502560";
//    private static final String appSecret = "AAqpM7GXAPhwS44llazUvMQc";
//    private static final String url = "https://textapi.fadada.com/api2/";

    private String signatureImgBase64;
    private FddBaseClient fddBaseClient;
    private String contractUrl;
    private String customerId;
    private String transactionNo;
    private String companyUrl;
    private String signatureId;
    private String applyCertInfo;
    private String companyInfo;
    private String contractStatus;
    private String contractStatusDesc;
    private String extSign;
    private String msg;
    private Boolean signFlag;
    private String authSignUrl;
    private String shortUrl;
    public FadadaUtil(Bulider bulider){
        this.fddBaseClient = bulider.fddBaseClient;
        this.customerId = bulider.customerId;
        this.transactionNo = bulider.transactionNo;
        this.companyUrl = bulider.companyUrl;
        this.signatureId = bulider.signatureId;
        this.signatureImgBase64 = bulider.signatureImgBase64;
        this.applyCertInfo = bulider.applyCertInfo;
        this.companyInfo = bulider.companyInfo;
        this.msg = bulider.result;
        this.contractUrl = bulider.contractUrl;
        this.extSign = bulider.extSign;
        this.contractStatus = bulider.contractStatus;
        this.contractStatusDesc = bulider.contractStatusDesc;
        this.signFlag = bulider.signFlag;
        this.authSignUrl = bulider.authSignUrl;
        this.shortUrl = bulider.shortUrl;
    }

    public static class Bulider {
        private FddBaseClient fddBaseClient;
        private FddVerifyClient fddVerifyClient;
        private FddExtraClient fddExtraClient;
        private AbstractApiParams abstractApiParams;
        private String transactionNo;
        private String contractStatus;
        private String contractStatusDesc;
        private String customerId;
        private String result;
        private String companyUrl;
        private String extSign;
        private String signatureImgBase64;
        private String signatureId;
        private String applyCertInfo;
        private String companyInfo;
        private String contractUrl;

        private Boolean signFlag;

        private String shortUrl;

        private String authSignUrl;
        public Bulider setParam(AbstractApiParams params){
            this.abstractApiParams = params;
            return this;
        }
        public Bulider initBaseClient(String appId,String appSecret,String v,String url){
            this.fddBaseClient = new FddBaseClient(appId, appSecret, v, url);
            return this;
        }
        public Bulider initVerifyClient(String appId,String appSecret,String v,String url){
            this.fddVerifyClient = new FddVerifyClient(appId,appSecret,v,url);
            return this;
        }
        public Bulider initExtraClient(String appId,String appSecret,String v,String url){
            this.fddExtraClient = new FddExtraClient(appId,appSecret,v,url);
            return this;
        }
        public Bulider initCustomer(){
            //注册账户信息
            RegisterAccountParams params = (RegisterAccountParams) this.abstractApiParams;
            String result = this.fddBaseClient.invokeRegisterAccount(params);
            log.info("fadada返回注册账户信息 ==== {}",result);
            if ((int)getParams(result,CODE) != 1) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, (String) getParams(result,MSG));
            }
            // TODO "customerId" need save redis
            this.customerId = (String) getParams(result,DATA);
            return this;
        }
        public Bulider initCompanyVerifyUrl()  {
            //注册企业信息
            CompanyVerifyUrlParams params = (CompanyVerifyUrlParams) this.abstractApiParams;
            params.setCustomerId(this.customerId);
            params.setPageModify("2");
            log.info("发送给法大大信息===={},{}",params.getCustomerId(),params.getReturnUrl());
            String company = this.fddVerifyClient.invokeCompanyVerifyUrl(params);
            log.info("fadada返回注册企业信息 ==== {}",company);
            if ((int) getParams(company,CODE)!=1) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, (String) getParams(company,MSG));
            }
            this.transactionNo = getDataJson(company, DATA).getString(TRANSACTION_NO);
            String url = getDataJson(company,DATA).getString(COMPANY_URL);
            byte[] decodedBytes = Base64.getDecoder().decode(url);
            this.companyUrl = new String(decodedBytes);
            return this;
        }

        public Bulider initApplyCert(){
            //绑定实名信息
            ApplyCertParams params = (ApplyCertParams) this.abstractApiParams;
            String result = this.fddVerifyClient.invokeApplyCert(params);
            if ((int) getParams(result,CODE)!=1) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, (String) getParams(result,MSG));
            }
            this.applyCertInfo = (String) getParams(result,MSG);
            return this;
        }
        public Bulider initUploadDocs(){
            // 上传合同
            UploadDocsParams params = (UploadDocsParams) this.abstractApiParams;
            log.info("上传合同参数======{},{},{}",params.getContractId(),params.getDocTitle(),params.getFile());
            String result = this.fddBaseClient.invokeUploadDocs(params);
            if (!getParams(result,CODE).toString().equals("1000")) {
                throw new SbcRuntimeException((String) getParams(result,MSG));
            }
            this.result = (String) getParams(result,MSG);
            return this;
        }

        public Bulider autoExtSign(){
            ExtSignAutoParams extSignAutoParams = (ExtSignAutoParams) this.abstractApiParams;
            String result = this.fddBaseClient.invokeExtSignAuto(extSignAutoParams);
            log.info("自动签署自营公司合同接口====={}",result);
            return this;
        }

        public Bulider invokeBeforeAuthSign(){
            BeforeAuthSignParams authSignParams = (BeforeAuthSignParams) this.abstractApiParams;
            String result = this.fddBaseClient.invokeBeforeAuthSign(authSignParams);
            log.info("获取自动签署接口权限=={}",result);
            this.authSignUrl = result;
            return this;
        }

        public Bulider invokeGetAuthStatus(){
            GetAuthStatusParams params = (GetAuthStatusParams)this.abstractApiParams;
            String result = this.fddBaseClient.invokeGetAuthStatus(params);
            log.info("查询用户授权状态==={}",result);
            if ((int)getParams(result,CODE) != 1) {
                this.signFlag = false;
            } else {
                Integer status =getDataJson(result,DATA).getInt(STATUS);
                if (status == 1) {
                    this.signFlag = true;
                } else {
                    this.signFlag = false;
                }
            }
            return this;
        }

        public Bulider initUploadTemplate(){
            UploadTemplateParams params = (UploadTemplateParams)this.abstractApiParams;
            String s = this.fddBaseClient.invokeUploadTemplate(params);
            log.info("上传合同模版===={}",s);
            return this;
        }

        public Bulider generateContract() {
            GenerateContractParams params = (GenerateContractParams)this.abstractApiParams;
            String s = this.fddBaseClient.invokeGenerateContract(params);
            log.info("模版填充返回接口===={}",s);
            return this;
        }

        public Bulider initExtSign() {
            //自动签署
            ExtSignParams params = (ExtSignParams) this.abstractApiParams;
            this.extSign = this.fddBaseClient.invokeExtSign(params);
            return this;
        }

        public Bulider initSignature(){
            //自定义上传印章
            CustomSignatureParams params = (CustomSignatureParams) this.abstractApiParams;
            String result = this.fddBaseClient.invokeCustomSignature(params);
            if ((int)getParams(result,CODE) != 1) {
                throw new SbcRuntimeException((String) getParams(result,MSG));
            }
            String img_base64 = getDataJson(result, DATA).getString(SIGNATURE_IMG_BASE64);
            String signature_id = getDataJson(result, DATA).getString(SIGNATURE_ID);
            this.signatureId = signature_id;
            this.signatureImgBase64 = img_base64;
            return this;
        }

        public Bulider queryCompany() {
            FindCompanyCertParams params = (FindCompanyCertParams) this.abstractApiParams;
            this.companyInfo = this.fddVerifyClient.invokeFindCompanyCert(params);
            return this;
        }

        public Bulider contractFilling(){
            ContractFillingParams params = (ContractFillingParams) this.abstractApiParams;
            String result = this.fddBaseClient.invokeContractFilling(params);
            if (!getParams(result,CODE).toString().equals("1000")) {
                throw new SbcRuntimeException((String) getParams(result,MSG));
            }
            this.result = (String) getParams(result,MSG);
            return this;
        }

        public Bulider downloadPDF() {
            DownloadPdfParams downloadPdfParams = (DownloadPdfParams) this.abstractApiParams;
            this.contractUrl = this.fddBaseClient.invokeDownloadPdf(downloadPdfParams);
            return this;
        }

        public Bulider viewContractPDF() {
            ViewPdfURLParams params = (ViewPdfURLParams) this.abstractApiParams;
            this.contractUrl = this.fddBaseClient.invokeViewPdfURL(params);
            return this;
        }


        public Bulider contractStatus() {
            ContractStatusParams params = (ContractStatusParams) this.abstractApiParams;
            String result = this.fddBaseClient.invokeContractStatus(params);
            if (!getParams(result,CODE).toString().equals("1000")) {
                throw new SbcRuntimeException(ResultCode.FAILED,"合同状态查询异常。请稍后再试 ==== "+getParams(result,MSG).toString());
            }
            this.contractStatus = getParams(result,CONTRACT_STATUS).toString();
            this.contractStatusDesc = getParams(result,CONTRACT_STATUS_DESC).toString();
            return this;
        }

        public Bulider pushShortUrlSms(String appSecret) throws Exception {
            PushShortUrlSmsParams params = (PushShortUrlSmsParams) this.abstractApiParams;
            log.info("开始发送短信====={}",params.getMobile());
            params.setSourceUrl(this.shortUrl);//原始链接，不超3000位
            params.setExpireTime(30);//有效时间，单位min，最长不超过10080
            params.setMobile(encrypt(params.getMobile(),appSecret));//手机号,使用3DES对数据加密：3DES(手机号,接入方秘钥)
            params.setMessageType("2");//短信类型1填充模板 2自定义模板
            params.setMessageContent("您已审核通过点击链接前往合同公章签署："+this.shortUrl);
            params.setSmsTemplateType("");
            String result = this.fddExtraClient.invokePushShortUrlSms(params);
            return this;
        }

        /**
         * 生成短链
         * */
        public Bulider shortUrl () {
            ShortUrlParams params = new ShortUrlParams();
            params.setExpireTime("30");//过期时间（默认七天后过期，单位：分钟）
            params.setSourceUrl(this.extSign);//最长500字符
            log.info("短链链接参数======{},{}",params.getSourceUrl(),params.getExpireTime());
            String result = this.fddExtraClient.invokeShortUrl(params);
            this.shortUrl = (String) getParams(result, DATA);
            log.info("短链结果======{}",this.shortUrl);
            return this;
        }

        public FadadaUtil bulider(){
            // TODO customerId save to redis
            return new FadadaUtil(this);
        }

    }

    public static Object getParams(String data,String param){
        log.info("getParams===={}",data);
        JSONObject json = new JSONObject(data);
        return json.get(param);
    }

    public static JSONObject getDataJson(String data,String param) {
        JSONObject json = new JSONObject(data);
        return json.getJSONObject(param);
    }

    public static String base64Parse(String base64){
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    /**
     * 对手机号进行3DES加密
     * @param phoneNumber 手机号
     * @param key 接入方秘钥
     * @return 加密后的手机号
     * @throws Exception
     */
    private static final String ALGORITHM = "DESede/ECB/PKCS5Padding";

    public static String encrypt(String data,String appSecret) throws Exception {
        Key desKey = null;
        DESedeKeySpec spec = new DESedeKeySpec(appSecret.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        desKey = keyFactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, desKey);
        byte[] result = cipher.doFinal(data.getBytes());
        return byte2HexStr(result);
    }

    private static String byte2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }


}
