package com.wanmi.sbc.fadadaInterface;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.request.fadada.BusinessParamsRequest;
import com.wanmi.sbc.customer.api.response.company.BusinessParamsResponse;
import com.wanmi.sbc.customer.api.response.company.IdCardParamsResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;


@Api(tags = "BusinessLicenseController", description = "解析营业执照")
@RestController("BusinessLicenseController")
@RequestMapping("/license")
@Slf4j
public class BusinessLicenseController {

    @Value("${tencent.srcret.id}")
    private String secretId;
    @Value("${tencent.secret.key}")
    private String secretKey;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "营业执照识别")
    @RequestMapping(value = "/analysisLicense", method = RequestMethod.POST)
    public BaseResponse<BusinessParamsResponse> analysisLicense (@RequestBody BusinessParamsRequest businessParamsRequest) {
        try {
//            // 替换为你的 API 密钥
//            String secretId = "AKIDRjnz4hCTYtFl6D1h9NN870fCSgjcPyJS";
//            String secretKey = "4whDVaUDtRozzmLlz3we871kIhw31tlR";
            log.info("增加营业执照识别日志==========={}",businessParamsRequest.toString());
            Credential cred = new Credential(secretId,secretKey);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ocr.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            OcrClient client = new OcrClient(cred, "ap-guangzhou", clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            BizLicenseOCRRequest req = new BizLicenseOCRRequest();
            req.setImageUrl(businessParamsRequest.getBusinessLicence());

            BizLicenseOCRResponse resp = client.BizLicenseOCR(req);
            operateLogMQUtil.convertAndSend("解析营业执照", "营业执照识别", "营业执照识别：营业执照名称" + (nonNull(resp) ? resp.getName() : ""));
            return BaseResponse.success(covertInfo(BizLicenseOCRResponse.toJsonString(resp)));
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "请正确上传营业执照");
        }
    }

    @ApiOperation(value = "身份证识别")
    @RequestMapping(value = "/idCrad",method = RequestMethod.POST)
    public BaseResponse<IdCardParamsResponse> idCrad(@RequestBody BusinessParamsRequest businessParamsRequest) {
        try {
//            // 替换为你的 API 密钥
//            String secretId = "AKIDRjnz4hCTYtFl6D1h9NN870fCSgjcPyJS";
//            String secretKey = "4whDVaUDtRozzmLlz3we871kIhw31tlR";
            log.info("增加身份证识别==========={}",businessParamsRequest.toString());
            Credential cred = new Credential(secretId,secretKey);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ocr.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            OcrClient client = new OcrClient(cred, "ap-guangzhou", clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            IDCardOCRRequest request = new IDCardOCRRequest();
            request.setImageUrl(businessParamsRequest.getBusinessLicence());

            IDCardOCRResponse idCardOCRResponse = client.IDCardOCR(request);
            System.out.println(idCardOCRResponse);
//            operateLogMQUtil.convertAndSend("身份证识别", "身份证识别", "身份证识别：姓名" + (nonNull(classifyDetectOCRResponse) ? classifyDetectOCRResponse.getName() : ""));
            return BaseResponse.success(covertIdCard(idCardOCRResponse));
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "请上传正确身份证");
        }
    }

    private IdCardParamsResponse covertIdCard(IDCardOCRResponse idCardOCRResponse) {
        IdCardParamsResponse idCardParamsResponse = new IdCardParamsResponse();
        idCardParamsResponse.setName(idCardOCRResponse.getName());
        idCardParamsResponse.setSex(idCardOCRResponse.getSex());
        idCardParamsResponse.setAddress(idCardOCRResponse.getAddress());
        idCardParamsResponse.setNation(idCardOCRResponse.getNation());
        idCardParamsResponse.setBirth(idCardOCRResponse.getBirth());
        idCardParamsResponse.setIdNum(idCardOCRResponse.getIdNum());
        return idCardParamsResponse;
    }

    private BusinessParamsResponse covertInfo (String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            BusinessParamsResponse licenseInfo = objectMapper.readValue(json, BusinessParamsResponse.class);
            return licenseInfo;
        } catch (Exception e) {
            log.info("解析出现问题======={}",e);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "请正确上传营业执照");
        }
    }

}
