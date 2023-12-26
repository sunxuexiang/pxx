package com.wanmi.sbc.fadadaInterface;

import com.alibaba.excel.util.StringUtils;
import com.fadada.sdk.base.model.req.*;
import com.fadada.sdk.verify.model.req.ApplyCertParams;
import com.fadada.sdk.verify.model.req.FindCompanyCertParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.ValidateUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.contract.CustomerContractProviderTo;
import com.wanmi.sbc.customer.api.provider.contract.ManagerContractProviderTo;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.employeecontract.EmployeeContractProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeLoginRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractFindRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractSaveRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaParamsRequest;
import com.wanmi.sbc.customer.api.request.fadada.WordParamsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreAuditJHInfoRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreContractRegisterSaveRequest;
import com.wanmi.sbc.customer.api.request.store.StoreSupplierPersonIdEditRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByCompanyIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeRegisterResponse;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractPageResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import com.wanmi.sbc.customer.api.response.fadada.*;
import com.wanmi.sbc.customer.api.response.store.StoreByCompanyInfoIdResponse;
import com.wanmi.sbc.customer.bean.enums.SmsTemplate;
import com.wanmi.sbc.store.StoreBaseService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.FadadaUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.util.sms.SmsSendUtil;
import io.jsonwebtoken.Claims;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Api(tags = "FadadaInterfaceMobileController", description = "法大大线上合同 API")
@RestController("FadadaInterfaceMobileController")
@RequestMapping("/fadada-h5")
@Slf4j
public class FadadaInterfaceMobileController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ManagerContractProviderTo contractProvider;

    @Autowired
    private EmployeeContractProvider employeeContractProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private CustomerContractProviderTo customerContractProviderTo;
    @Autowired
    private StoreProvider storeProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private SupplierInController supplierInController;
    @Autowired
    private StoreBaseService storeBaseService;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private SmsSendUtil smsSendUtil;

    // 设置上传文件的保存路径
    private static final String UPLOAD_FOLDER = "uploads";
    private static final String REDIS_KEY = "fadada_";
    @Value("${asyc.ext.sign.h5.url}")
    private  String return_url ;

    @Value("${register.after.url}")
    private String registerAfterUrl;

    @Value("${aycn.ext.sign}")
    private String aycnExtSignUrl;

    @Value("${auto.sign.return.url}")
    private String autoSignReturnUrl;

    @Value("${asyc.auth.h5.sign}")
    private String asycAuthSign;

    @Value("${auto.sign}")
    private String autoSign;
    @Value("${auto.sign.h5.after}")
    private String signAfter;

    @Value("${email}")
    private String email;

    @Value("${fadada.version}")
    private String v;
    @Value("${fadada.app.id}")
    private String appId;
    @Value("${fadada.app.secret}")
    private String appSecret;
    @Value("${fadada.url}")
    private String url;
    //    private static final String appId = "407545";
//    private static final String appSecret = "bC81SsTSCyhxxYRWRgxm5mrC";
//    private static final String url = "http://test.api.fabigbig.com:8888/api/";
//    private static final String v = "2.0";
//    private static final String appId = "502560";
//    private static final String appSecret = "AAqpM7GXAPhwS44llazUvMQc";
//    private static final String url = "https://textapi.fadada.com/api2/";
    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    @ApiOperation(value = "注册回退重新签署合同")
    @RequestMapping(value = "/re-signContract", method = RequestMethod.POST)
    @Transactional
    public BaseResponse signContract(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request) {
        // 查询用户账号
        StoreByCompanyInfoIdResponse companyInfo = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(paramsRequest.getCompanyInfoId()).build()).getContext();
        EmployeeByCompanyIdResponse context = employeeQueryProvider.getByCompanyId(EmployeeByCompanyIdRequest.builder().companyInfoId(paramsRequest.getCompanyInfoId()).build()).getContext();
        StoreAuditJHInfoRequest audit = new StoreAuditJHInfoRequest();
        audit.setStoreId(companyInfo.getStoreVO().getStoreId());
        // 重新初始化店铺信息--修改状态为null表示刚刚注册的店铺
        storeProvider.updateAuditStateToContract(audit);

        // 重新初始化店铺信息
        saveContract(paramsRequest,context.getEmployeeId(),companyInfo.getStoreVO().getStoreId().toString());
        operateLogMQUtil.convertAndSend("法大大线上合同", "注册回退重新签署合同", "注册回退重新签署合同：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "保存招商信息和线上或者线下合同")
    @RequestMapping(value = "/saveContractInfo", method = RequestMethod.POST)
    @Transactional
    public BaseResponse saveContractInfo(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request){
        if (StringUtils.isEmpty(paramsRequest.getInvestmentManager())) {
            throw new SbcRuntimeException("K-000002","请选择招商经理");
        }
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        String storeId = ((Claims) request.getAttribute("claims")).get("storeId").toString();
        // 保存基本信息
        saveContract(paramsRequest,employeeId,storeId);
        operateLogMQUtil.convertAndSend("法大大线上合同", "保存招商信息和线上或者线下合同", "保存招商信息和线上或者线下合同：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
        return BaseResponse.SUCCESSFUL();
    }

    private void saveContract(FadadaParamsRequest paramsRequest,String employeeId,String storeId) {

        // 保存签署信息
        EmployeeOptionalByIdRequest byIdRequest = new EmployeeOptionalByIdRequest();
        byIdRequest.setEmployeeId(employeeId);
        EmployeeOptionalByIdResponse context = employeeQueryProvider.getOptionalById(byIdRequest).getContext();
        EmployeeContractResponese context1 = employeeContractProvider.findByEmployeeId(employeeId).getContext();
        WordParamsRequest wordParamsRequest = new WordParamsRequest();
        wordParamsRequest.setEmployeeId(employeeId);
        WordParamsRequest context2 = customerContractProviderTo.findByEmployeeId(wordParamsRequest).getContext();
        // 查询商家信息编号

        EmployeeContractSaveRequest contractSaveRequest = new EmployeeContractSaveRequest();
        if (null != context2) {
            contractSaveRequest.setContractId(context2.getContractId());
        }
        contractSaveRequest.setCreateTime(LocalDateTime.now());
        contractSaveRequest.setStatus(0);
        contractSaveRequest.setIsPerson(paramsRequest.getIsPerson());
        contractSaveRequest.setInvestemntManagerId(paramsRequest.getInvestemntManagerId());
        contractSaveRequest.setInvestmentManager(paramsRequest.getInvestmentManager());
        contractSaveRequest.setSignType(paramsRequest.getSignType());
        contractSaveRequest.setEmployeeId(employeeId);
        contractSaveRequest.setEmployeeName(context.getAccountName());
        if(context1 == null) {
            CompanyInfoByIdResponse companyInfo = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(context.getCompanyInfoId()).build()).getContext();
            contractSaveRequest.setUserContractId(companyInfo.getCompanyCodeNew());
            employeeContractProvider.saveEmployeeContract(contractSaveRequest);
        } else {
            contractSaveRequest.setUserContractId(context1.getUserContractId());
            employeeContractProvider.save(contractSaveRequest);
        }

        // 更新商家店铺
        StoreSupplierPersonIdEditRequest storeSupplierPersonIdEditRequest = new StoreSupplierPersonIdEditRequest();
        storeSupplierPersonIdEditRequest.setPersonId(paramsRequest.getIsPerson());
        storeSupplierPersonIdEditRequest.setStoreId(Long.valueOf(storeId));
        storeProvider.editPersonId(storeSupplierPersonIdEditRequest);
    }
    public static String generateMerchantNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("BJ-");

        for (int i = 0; i < 7; i++) {
            int digit;
            do {
                digit = random.nextInt(10);
            } while (digit == 4); // Exclude digit 4
            sb.append(digit);
        }
        return sb.toString();
    }

    @ApiOperation(value = "查询企业信息")
    @RequestMapping(value = "/queryCompanyInfo" , method = RequestMethod.POST)
    public BaseResponse queryCompanyInfo (HttpServletRequest request) throws IOException {
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        // 查询数据库数据
        String transactionNoFromRedis = getTransactionNoFromRedis(employeeId);
        EmployeeContractResponese context1 = employeeContractProvider.findByEmployeeId(employeeId).getContext();
        if (null != context1) {
            transactionNoFromRedis = context1.getTransactionNo();
        }
        FindCompanyCertParams params = new FindCompanyCertParams();
        params.setVerifiedSerialNo(transactionNoFromRedis);
        FadadaUtil bulider = new FadadaUtil.Bulider().initVerifyClient(appId,appSecret,v,url).setParam(params).queryCompany().bulider();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(bulider.getCompanyInfo());
        redisTemplate.opsForHash().put(REDIS_KEY+employeeId, "companyName", jsonNode.path("data").path("company").path("companyName").asText());
        FadadaCompanyResponese myData = objectMapper.readValue(bulider.getCompanyInfo(), FadadaCompanyResponese.class);
        return BaseResponse.success(myData);
    }

    @ApiOperation(value = "绑定实名信息")
    @RequestMapping(value = "/applyCert", method = RequestMethod.POST)
    public BaseResponse applyCert(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "绑定实名信息", "绑定实名信息：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        ApplyCertParams params = new ApplyCertParams();
        params.setCustomerId(getCustomerIdFromRedis(employeeId));
        params.setVerifiedSerialNo(getTransactionNoFromRedis(employeeId));
        FadadaUtil bulider = new FadadaUtil.Bulider()
                .initVerifyClient(appId,appSecret,v,url)
                .setParam(params)
                .initApplyCert().bulider();
        return BaseResponse.success(new FadadaParamsResponese(getCustomerIdFromRedis(employeeId), getTransactionNoFromRedis(employeeId),null,bulider.getApplyCertInfo()));
    }

    @ApiOperation(value = "授权后异步回调")
    @RequestMapping(value = "/asycAuthSign",method = RequestMethod.GET)
    public RedirectView asycAuthSign(@Param("transaction_id") String transaction_id,@Param("result_code")String result_code,@Param("result_desc") String result_desc
            ,@Param("download_url") String download_url,@Param("viewpdf_url")String viewpdf_url,@Param("timestamp")String timestamp,@Param("msg_digest")String msg_digest) {
        RedirectView redirectView = new RedirectView(autoSignReturnUrl);
        redirectView.setExposeModelAttributes(false);
        return redirectView;
    }

    @ApiOperation(value = "上传合同接口")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public BaseResponse<String> uploadContract(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "上传合同接口", "上传合同接口：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        // 保存合同到数据库
        ContractUploadRequest uploadRequest = new ContractUploadRequest();
        uploadRequest.setContractUrl(paramsRequest.getContractUrl());
        uploadRequest.setContractFlag(paramsRequest.getContractFlag());
        uploadRequest.setContractName(paramsRequest.getDocTitle());
        uploadRequest.setCreatePerson(employeeId);
        uploadRequest.setIsPerson(paramsRequest.getIsPerson());
        return contractProvider.uploadContract(uploadRequest);
    }

    @ApiOperation(value = "上传合同接口")
    @RequestMapping(value = "/uploadTemplate", method = RequestMethod.POST)
    public BaseResponse<String> uploadTemplate(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request) {
        try {
            operateLogMQUtil.convertAndSend("法大大线上合同", "上传合同模板接口", "上传合同模板接口：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
            String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
            String uuid = generateUUID();
            String c = generatorService.generate("UUID");
            UploadTemplateParams templateParams = new UploadTemplateParams();
            templateParams.setTemplateId(c);
            templateParams.setDocUrl(paramsRequest.getContractUrl());
            new FadadaUtil.Bulider()
                    .initBaseClient(appId,appSecret,v,url)
                    .setParam(templateParams)
                    .initUploadTemplate()
                    .bulider();
            // 保存合同到数据库
            ContractUploadRequest uploadRequest = new ContractUploadRequest();
            uploadRequest.setFadadaId(c);
            uploadRequest.setContractUrl(paramsRequest.getContractUrl());
            uploadRequest.setContractFlag(paramsRequest.getContractFlag());
            uploadRequest.setContractName(paramsRequest.getDocTitle());
            uploadRequest.setCreatePerson(employeeId);
            return contractProvider.uploadContract(uploadRequest);
        } catch (SbcRuntimeException e) {
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        }

    }

    @ApiOperation(value = "印章上传接口")
    @PostMapping(value = "/addSignature")
    public BaseResponse addSignature(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "印章上传接口", "印章上传接口：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        CustomSignatureParams params = new CustomSignatureParams ();
        params.setCustomerId(getCustomerIdFromRedis(employeeId)); //客户编号
        params.setContent(getCompanyNameFromRedis(employeeId));
        FadadaUtil bulider = new FadadaUtil.Bulider()
                .initBaseClient(appId,appSecret,v,url)
                .setParam(params)
                .initSignature()
                .bulider();
        return BaseResponse.success(new FadadaSignatureParamsResponese(bulider.getSignatureImgBase64(),bulider.getSignatureId()));
    }

    @ApiOperation(value = "手动签署接口")
    @RequestMapping(value = "/extSign", method = RequestMethod.POST)
    public BaseResponse extSign(@RequestBody FadadaParamsRequest paramsRequest, HttpServletRequest request) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "手动签署接口", "手动签署接口：法大大对应喜吖吖系统唯一ID" + (null == paramsRequest ? "" : paramsRequest.getCustomerId()));
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        ExtSignParams params = new ExtSignParams();
        String contractId = contractProvider.seachValidContract();
        params.setTransactionId(getTransactionNoFromRedis(employeeId));//平台自定义唯一交易号
        params.setContractId(contractId);//此处传入调用上传或填充合同接口成功时定义的合同编号
        params.setCustomerId(getCustomerIdFromRedis(employeeId));//此处传入认证成功后成功绑定实名信息的客户编号
        params.setDocTitle(paramsRequest.getDocTitle());
        params.setPositionType("0");//0-关键字（默认）1-坐标
        params.setSignKeyword(getCompanyNameFromRedis(employeeId));
//        params.setSignKeyword("鲸雷科技有限公司");
        params.setKeywordStrategy("0");//0-所有关键字签章 （默认） 1-第一个关键字签章 2-最后一个关键字签章
        FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient(appId,appSecret,v,url).setParam(params).initExtSign().bulider();
        redisTemplate.opsForHash().put(REDIS_KEY+employeeId, "contractId", contractId);
        return BaseResponse.success(new FadadaSignParamsResponese(bulider.getExtSign()));
    }

    @ApiOperation(value = "合同归档接口")
    @RequestMapping(value = "/contractFilling/{contractId}", method = RequestMethod.POST)
    public BaseResponse contractFilling(@PathVariable String contractId, HttpServletRequest request) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "合同归档接口", "合同归档接口：合同ID" + (StringUtils.isEmpty(contractId) ? "" : contractId));
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        ContractFillingParams params = new ContractFillingParams();
//        params.setContractId(getContractIdFromRedis(employeeId));
        params.setContractId(contractId);
        FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient(appId,appSecret,v,url).setParam(params).contractFilling().bulider();
        return BaseResponse.success(bulider.getMsg());
    }

    @ApiOperation(value = "合同下载接口")
    @RequestMapping(value = "/downloadPDF", method = RequestMethod.POST)
    public BaseResponse downloadPDF(@PathVariable String contractId, HttpServletRequest request) {
        log.info("合同下载接口===={}",request.toString());
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        DownloadPdfParams params = new DownloadPdfParams();
//        params.setContractId(getContractIdFromRedis(employeeId));
        params.setContractId(contractId);
        FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient("502560","AAqpM7GXAPhwS44llazUvMQc",v,"https://textapi.fadada.com/api2/").setParam(params).downloadPDF().bulider();
        operateLogMQUtil.convertAndSend("法大大线上合同", "合同下载接口", "操作成功");
        return  BaseResponse.success(bulider.getContractUrl());
    }

    @ApiOperation(value = "查看合同接口")
    @RequestMapping(value = "/viewContractPDF", method = RequestMethod.POST)
    public BaseResponse viewContractPDF(@PathVariable String contractId, HttpServletRequest request) {
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        ViewPdfURLParams params = new ViewPdfURLParams();
//        params.setContractId(getContractIdFromRedis(employeeId));
        params.setContractId(contractId);
        FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient("502560","AAqpM7GXAPhwS44llazUvMQc",v,"https://textapi.fadada.com/api2/").setParam(params).viewContractPDF().bulider();
        return BaseResponse.success(bulider.getContractUrl());
    }

    @ApiOperation(value = "查看合同签署状态")
    @RequestMapping(value = "/viewContractStatus",method = RequestMethod.POST)
    public BaseResponse viewContractStatus(HttpServletRequest request) {
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        log.info("查看合同签署状态===={}",request.toString());
        BaseResponse<EmployeeContractResponese> byEmployeeId = employeeContractProvider.findByEmployeeId(employeeId);
        if (byEmployeeId.getContext() == null) {
            return BaseResponse.error("用户还未签署合同");
        }
        return  byEmployeeId;
    }

    @ApiOperation(value = "签署后异步通知接口")
    @RequestMapping(value = "/asycExtSign",method = RequestMethod.GET)
    public RedirectView astcExtSign (@Param("transaction_id") String transaction_id,@Param("result_code")String result_code,@Param("result_desc") String result_desc
            ,@Param("download_url") String download_url,@Param("viewpdf_url")String viewpdf_url,@Param("timestamp")String timestamp,@Param("msg_digest")String msg_digest) {
        log.info("进入签署后接口异步回调通知====={},{},{},{},{}",transaction_id,result_code,result_desc,download_url,viewpdf_url);

        // 如果不是手机号码还是走原来的逻辑
        String employeeIdFromRedis = getEmployeeIdFromRedis(transaction_id);
        Integer resultCode = Integer.valueOf(result_code);
        EmployeeContractSaveRequest request1 = new EmployeeContractSaveRequest();
        request1.setStatus(resultCode);
        request1.setContractUrl(convertUrl(download_url)+","+convertUrl(viewpdf_url));
        WordParamsRequest queryVo = new WordParamsRequest();
        if(!ValidateUtil.isPhone(employeeIdFromRedis)){
            queryVo.setAppCustomerId(employeeIdFromRedis);
        } else {
            queryVo.setContractPhone(employeeIdFromRedis);
        }
        WordParamsRequest contractInfo = customerContractProviderTo.findByEmployeeId(queryVo).getContext();
        // 多线程异步提交
        // 如果是手机号码就走注册的逻辑
        EmployeeRegisterResponse context = supplierInController.register(EmployeeLoginRequest.builder().account(contractInfo.getContractPhone()).password("123456").build()).getContext();
        CompanyInfoByIdResponse companyInfo = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(context.getCompanyInfoId()).build()).getContext();
        contractInfo.setStoreId(companyInfo.getStoreVOList().get(0).getStoreId());
        contractInfo.setCompanyId(context.getCompanyInfoId());
        // 刷新customer_contract中employee_id
        WordParamsRequest request = new WordParamsRequest();
        request.setContractPhone(contractInfo.getContractPhone());
        request.setEmployeeId(context.getEmployeeId());
        request.setAppCustomerId(contractInfo.getAppCustomerId());
        customerContractProviderTo.saveAndUpdate(request);
        // 刷新employee_contract表中employee_id
        request1.setEmployeeId(context.getEmployeeId());
        request1.setEmployeeName(contractInfo.getContractPhone());
        request1.setAppCustomerId(contractInfo.getAppCustomerId());
        employeeContractProvider.updateEmployeeContractByEmlpoyeeName(request1);
        // 保存商家信息
        saveInfo(contractInfo);

        StoreSupplierPersonIdEditRequest storeSupplierPersonIdEditRequest = new StoreSupplierPersonIdEditRequest();
        storeSupplierPersonIdEditRequest.setPersonId(contractInfo.getIsPerson());
        storeSupplierPersonIdEditRequest.setStoreId(contractInfo.getStoreId());
        storeProvider.editPersonId(storeSupplierPersonIdEditRequest);
        smsSendUtil.send(SmsTemplate.CONTRACT_SIGN_IN,new String[]{contractInfo.getContractPhone()}, contractInfo.getContractPhone(), "123456");
        RedirectView redirectView = new RedirectView(signAfter+"?phone="+contractInfo.getContractPhone());
        redirectView.setExposeModelAttributes(false);
        return redirectView;
    }

    private String convertUrl(String url) {
        return url.replace("&amp;", "&").replace("%26", "&");
    }

    private void saveInfo(WordParamsRequest contractInfo) {
        log.info("保存合同基本信息======={}",contractInfo.toString());
        // 店铺需要更新：商家名称，联系人，联系方式，详细地址
        // 公司表需要更新：联系人，联系方式，详细地址，公司名称，社会信用代码，法定代表人，法人身份证正面，法人电话，门头照，仓库照片
        // 签约商城更新
        StoreContractRegisterSaveRequest registerSaveRequest = new StoreContractRegisterSaveRequest();
        registerSaveRequest.setSupplierName(contractInfo.getCompanyName());
        registerSaveRequest.setContactPerson(contractInfo.getLegalPersonName());
        registerSaveRequest.setContactMobile(contractInfo.getContractPhone());
        registerSaveRequest.setSocialCreditCode(contractInfo.getCreditCode());
        registerSaveRequest.setFrontIDCard(contractInfo.getIdCradUrl());
        registerSaveRequest.setDoorImage(contractInfo.getDoorPhoto());
        registerSaveRequest.setWarehouseImage(contractInfo.getStoreSign());
        registerSaveRequest.setStoreId(contractInfo.getStoreId());
        registerSaveRequest.setCompanyInfoId(contractInfo.getCompanyId());
        registerSaveRequest.setRelationValue(contractInfo.getRelationValue());
        registerSaveRequest.setRelationName(contractInfo.getRelationName());
        registerSaveRequest.setRelationType(contractInfo.getRelationType());
        registerSaveRequest.setPersonId(contractInfo.getIsPerson());
        registerSaveRequest.setBusinessScope(contractInfo.getBusinessScope());
        registerSaveRequest.setBusinessUrl(contractInfo.getBusinessUrl());
        registerSaveRequest.setBusinessAddress(contractInfo.getBusinessAddress());
        registerSaveRequest.setProvinceId(contractInfo.getProvinceId());
        registerSaveRequest.setAreaId(contractInfo.getAreaId());
        registerSaveRequest.setCityId(contractInfo.getCityId());
        registerSaveRequest.setAddressDetail(contractInfo.getDetailAddress());
        registerSaveRequest.setBankAreaId(contractInfo.getBankAreaId());
        registerSaveRequest.setBankCityId(contractInfo.getBankCityId());
        registerSaveRequest.setBankProvinceId(contractInfo.getBankProvinceId());
        registerSaveRequest.setBankAccount(contractInfo.getAccount());
        registerSaveRequest.setBankAccountName(contractInfo.getAccountName());
        registerSaveRequest.setBank(contractInfo.getBank());
        registerSaveRequest.setProvinceId(contractInfo.getProvinceId());
        registerSaveRequest.setCityId(contractInfo.getCityId());
        registerSaveRequest.setAreaId(contractInfo.getAreaId());
        registerSaveRequest.setStoreContract(contractInfo.getStoreContract());
        registerSaveRequest.setStoreContractPhone(contractInfo.getStoreContractPhone());
        registerSaveRequest.setDetailAddress(contractInfo.getDetailAddress());
        registerSaveRequest.setTabRelationName(contractInfo.getTabRelationName());
        registerSaveRequest.setTabRelationValue(contractInfo.getTabRelationValue());
        registerSaveRequest.setStoreName(contractInfo.getStoreName());
        registerSaveRequest.setIdCardNo(contractInfo.getIdCardNo());
        log.info("保存商家性质========{}，{}",registerSaveRequest.getPersonId(),contractInfo.getIsPerson());
        storeBaseService.registerContractUpdateStoreInfo(registerSaveRequest);
    }

    @ApiOperation(value = "查看已签署合同列表")
    @RequestMapping(value = "/viewContractList",method = RequestMethod.POST)
    public BaseResponse<EmployeeContractPageResponese> viewContractList (@RequestBody EmployeeContractFindRequest employeeContractFindRequest,HttpServletRequest request) {
        log.info("查看已签署合同列表===={}",employeeContractFindRequest.toString());
        return employeeContractProvider.findByUserContract(employeeContractFindRequest);
    }

    @ApiOperation(value = "修改合同状态")
    @RequestMapping(value = "/updateStatus",method = RequestMethod.POST)
    public BaseResponse<BaseResponse> updateStatus(@RequestBody ContractUpdateRequest contractUpdateRequest) {
        log.info("修改合同状态===={}",contractUpdateRequest.toString());
        contractProvider.updateContractStatus(contractUpdateRequest);
        operateLogMQUtil.convertAndSend("法大大线上合同", "修改合同状态", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "查看已经上传的合同")
    @RequestMapping(value = "viewContract",method = RequestMethod.POST)
    public BaseResponse<List<UploadContractResponese>> viewContract (@RequestBody ContractUploadRequest request) {
        log.info("查看已经上传的合同===={}",request.toString());
        return contractProvider.viewContractByIsPerson(request);
    }

    @ApiOperation(value = "下载照片压缩包")
    @RequestMapping(value = "/downLoadImg/{userContrantId}",method = RequestMethod.GET)
    public void downLoadImg (@PathVariable String userContrantId, HttpServletResponse response) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "下载照片压缩包", "下载照片压缩包:合同ID" + (StringUtils.isEmpty(userContrantId) ? "" : userContrantId));
        EmployeeContractResponese context = employeeContractProvider.findByUserContractId(userContrantId).getContext();
        if (null != context && !StringUtils.isEmpty(context.getImgUrl())) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ZipOutputStream zipOut = new ZipOutputStream(bos);
                for (String s : context.getImgUrl().split(",")) {
                    addToZip(s,zipOut);
                }
                zipOut.close();
                bos.close();
                String fileName = "签署合同.zip";
                fileName = URLEncoder.encode(fileName, "UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentType("application/zip");
                OutputStream outputStream = response.getOutputStream();
                bos.writeTo(outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                throw new SbcRuntimeException(e.getMessage());
            }
        }
    }

    private void addToZip(String urlString, ZipOutputStream zipOut) throws IOException {
        URL url = new URL(urlString);
        InputStream inputStream = url.openStream();
        ZipEntry zipEntry = new ZipEntry(urlString.substring(urlString.lastIndexOf('/') + 1));
        zipOut.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            zipOut.write(buffer, 0, bytesRead);
        }

        zipOut.closeEntry();
        inputStream.close();
    }

    /**
     * 合同模版删除接口
     * */
    @ApiOperation(value = "删除平台合同模版")
    @RequestMapping(value = "delContract",method = RequestMethod.POST)
    public BaseResponse<String> delContract(@RequestBody ContractUpdateRequest contractUpdateRequest) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "删除平台合同模版", "删除平台合同模版:合同ID" + (Objects.nonNull(contractUpdateRequest) ? contractUpdateRequest.getContractId() : ""));
        return contractProvider.delContractStatus(contractUpdateRequest);
    }

    @GetMapping(value = "/viewPDF", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> viewPDF(@RequestParam String url) throws IOException {

        URL pdfUrl = new URL(url);
        URLConnection conn = pdfUrl.openConnection();
        InputStream inputStream = conn.getInputStream();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=example.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    @ApiOperation(value = "线下签署上传合同")
    @RequestMapping(value = "uploadContractInfo",method = RequestMethod.POST)
    public BaseResponse<Void> uploadContractInfo(@RequestBody FadadaParamsRequest fadadaParamsRequest) {
        operateLogMQUtil.convertAndSend("法大大线上合同", "线下签署上传合同", "线下签署上传合同:法大大对应喜吖吖系统唯一ID" + (Objects.nonNull(fadadaParamsRequest) ? fadadaParamsRequest.getCustomerId() : ""));
        EmployeeContractSaveRequest employeeContractResponese = new EmployeeContractSaveRequest();
        EmployeeContractResponese context1 = employeeContractProvider.findByEmployeeId(fadadaParamsRequest.getEmployeeId()).getContext();
        if (!StringUtils.isEmpty(fadadaParamsRequest.getContractUrl())) {
            context1.setContractUrl(fadadaParamsRequest.getContractUrl());
        }
        if (!StringUtils.isEmpty(fadadaParamsRequest.getImgUrl())) {
            context1.setImgUrl(fadadaParamsRequest.getImgUrl());
        }
        context1.setSupplierName(fadadaParamsRequest.getSupplierName());
        KsBeanUtil.copyProperties(context1,employeeContractResponese);
        employeeContractResponese.setStatus(3000);
        employeeContractResponese.setTransactionNo(generatorService.generate("C"));
        return employeeContractProvider.save(employeeContractResponese);
    }

    @ApiOperation(value = "线下签署上传合同")
    @RequestMapping(value = "generateContract",method = RequestMethod.POST)
    public BaseResponse<Void> generateContract() {
        operateLogMQUtil.convertAndSend("法大大线上合同", "线下签署上传合同", "线下签署上传合同" );
        String contractId = contractProvider.seachValidContract();
        // 模版填充
        GenerateContractParams contractParams = new GenerateContractParams();
        contractParams.setTemplateId(contractId);
        contractParams.setContractId(contractId);
        contractParams.setFillType("1");
        contractParams.setParameterMap(getparamter("西呀呀呀"));
        FadadaUtil bulider = new FadadaUtil.Bulider().initBaseClient(appId,appSecret,v,url).setParam(contractParams).generateContract().bulider();
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "法大大同步回调接口")
    @RequestMapping(value = "/returnRegister",method = RequestMethod.GET)
    public RedirectView returnRegister (@Param("companyName")String companyName,
                                        @Param("transactionNo")String transactionNo,
                                        @Param("authenticationType") String authenticationType,
                                        @Param("status")String status,@Param("sign")String sign) throws SbcRuntimeException {

        try{
            log.info("法大大验证完成同步回调接口========={},{},{},{},{}",companyName,transactionNo,authenticationType,status,sign);
            String employeeIdFromRedis = getEmployeeIdFromRedis(transactionNo); //"2c97d6e2886f94de0188a31774fb014d";
            String customerIdFromRedis = getCustomerIdFromRedis(employeeIdFromRedis); //"89DDC566314951C89BB973F4920A6040";
            // 解密公司名称
            companyName = URLDecoder.decode(companyName, "UTF-8");
            redisTemplate.opsForHash().put(REDIS_KEY+employeeIdFromRedis, "companyName", companyName);
            if (status.equals("4")) {
                WordParamsRequest wordRequest = new WordParamsRequest();
                EmployeeContractResponese context1 = null;
                if (!ValidateUtil.isPhone(employeeIdFromRedis)) {
                    wordRequest.setAppCustomerId(employeeIdFromRedis);
                    context1 = employeeContractProvider.findByAppCustomerId(employeeIdFromRedis).getContext();
                } else {
                    wordRequest.setContractPhone(employeeIdFromRedis);
                    context1 = employeeContractProvider.findByEmployeeName(employeeIdFromRedis).getContext();
                }
                WordParamsRequest context2 = customerContractProviderTo.findByEmployeeId(wordRequest).getContext();
                // 绑定实名信息
                ApplyCertParams params = new ApplyCertParams();
                params.setCustomerId(customerIdFromRedis);
                params.setVerifiedSerialNo(transactionNo);
                // 印章上传
                CustomSignatureParams customSignatureParams = new CustomSignatureParams();
                customSignatureParams.setContent(companyName);
                customSignatureParams.setCustomerId(customerIdFromRedis);
                // 手动签署
                // 手动签署重新生成交易号
                String uuid = generatorService.generate("UUID");
                ExtSignParams extSignParams = new ExtSignParams();
                log.info("获取到的合同ID===={}",context2.getContractId());
                extSignParams.setTransactionId(transactionNo);//平台自定义唯一交易号
                extSignParams.setContractId(context2.getContractId());//此处传入调用上传或填充合同接口成功时定义的合同编号
                extSignParams.setCustomerId(customerIdFromRedis);//此处传入认证成功后成功绑定实名信息的客户编号
                extSignParams.setDocTitle(companyName);
                extSignParams.setPositionType("0");//0-关键字（默认）1-坐标
                extSignParams.setSignKeyword("授权单位盖章");
                extSignParams.setReturnUrl(return_url);
                extSignParams.setKeywordStrategy("0");//0-所有关键字签章 （默认） 1-第一个关键字签章 2-最后一个关键字签章

                // 发送短信
//                PushShortUrlSmsParams pushShortUrlSmsParams = new PushShortUrlSmsParams();
//                EmployeeOptionalByIdRequest employeeOptionalByIdRequest = new EmployeeOptionalByIdRequest();
//                employeeOptionalByIdRequest.setEmployeeId(employeeIdFromRedis);
//                log.info("查询用户员工编号======={}",employeeIdFromRedis);
//                EmployeeOptionalByIdResponse context = employeeQueryProvider.getOptionalById(employeeOptionalByIdRequest).getContext();
//                String employeeMobile = context.getEmployeeMobile();
//                pushShortUrlSmsParams.setMobile(employeeMobile);

                ExtSignAutoParams signAutoParams = new ExtSignAutoParams();
                signAutoParams.setContractId(context2.getContractId());
                signAutoParams.setCustomerId(autoSign);
//                signAutoParams.setCustomerId("0EB574226E5B623D32EB7A48E76193E3");
                signAutoParams.setTransactionId(uuid);
                signAutoParams.setPositionType("0");
                signAutoParams.setDocTitle("入驻商家签约条款");
                signAutoParams.setSignKeyword("甲方（公章）");
                extSignParams.setKeywordStrategy("0");
                FadadaUtil bulider = new FadadaUtil.Bulider()
                        .initVerifyClient(appId,appSecret,v,url)
                        .setParam(params)
                        // 绑定实名信息
                        .initApplyCert()
                        .initBaseClient(appId,appSecret,v,url)
                        // 印章上传
                        .setParam(customSignatureParams)
                        .initBaseClient(appId,appSecret,v,url)
                        .initSignature()
                        //自动签署
                        .setParam(signAutoParams)
                        .autoExtSign()
                        // 手动签署
                        .setParam(extSignParams)
                        .initExtSign()
                        //生成短链
                        .initExtraClient(appId,appSecret,v,url)
                        .shortUrl()
                        //发送信息
//                        .setParam(pushShortUrlSmsParams)
//                        .pushShortUrlSms(appSecret)
                        .bulider();

                //发送短信
                redisTemplate.opsForHash().put(REDIS_KEY+employeeIdFromRedis, "contractId", context2.getContractId());
                EmployeeContractSaveRequest contractSaveRequest = new EmployeeContractSaveRequest();
                contractSaveRequest.setSupplierName(companyName);
                contractSaveRequest.setContractId(context2.getContractId());
                contractSaveRequest.setTransactionNo(transactionNo);
                contractSaveRequest.setEmployeeName(context1.getEmployeeName());
                contractSaveRequest.setCustomerId(getCustomerIdFromRedis(employeeIdFromRedis));
                contractSaveRequest.setStatus(9999);
                contractSaveRequest.setUserContractId(context1.getUserContractId());
                contractSaveRequest.setAppCustomerId(context1.getAppCustomerId());
                contractSaveRequest.setSignType(context1.getSignType());
                contractSaveRequest.setCreateTime(context1.getCreateTime());
                contractSaveRequest.setInvestemntManagerId(context1.getInvestemntManagerId());
                contractSaveRequest.setInvestmentManager(context1.getInvestmentManager());
                contractSaveRequest.setIsPerson(context1.getIsPerson());
                // 刷新redis缓存中的交易号
                if (context1.getStatus() != 3000) {
                    employeeContractProvider.save(contractSaveRequest);
                    log.info("用户签署合同====={},合同ID=========={}",REDIS_KEY+ employeeIdFromRedis,context2.getContractId());
                }
                RedirectView redirectView = new RedirectView(bulider.getShortUrl());
                redirectView.setExposeModelAttributes(false);
                return redirectView;
            }
            RedirectView redirectView = new RedirectView(registerAfterUrl);
            redirectView.setExposeModelAttributes(false);
            return redirectView;
        }catch (SbcRuntimeException  e) {
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "法大大同步回调接口")
    @RequestMapping(value = "/returnRegisterH5",method = RequestMethod.GET)
    public BaseResponse<FadadaParamsResponese> returnRegisterH5 () throws SbcRuntimeException {
        String userId = commonUtil.getUserInfo().getUserId();
        EmployeeContractResponese context = employeeContractProvider.findByAppCustomerId(userId).getContext();
        ExtSignParams extSignParams = new ExtSignParams();
        log.info("获取到的合同ID===={}",context.getContractId());
        extSignParams.setTransactionId(context.getTransactionNo());//平台自定义唯一交易号
        extSignParams.setContractId(context.getContractId());//此处传入调用上传或填充合同接口成功时定义的合同编号
        extSignParams.setCustomerId(context.getCustomerId());//此处传入认证成功后成功绑定实名信息的客户编号
        extSignParams.setDocTitle(context.getSupplierName());
        extSignParams.setPositionType("0");//0-关键字（默认）1-坐标
        extSignParams.setSignKeyword("授权单位盖章");
        extSignParams.setReturnUrl(return_url);
        extSignParams.setKeywordStrategy("0");//0-所有关键字签章 （默认） 1-第一个关键字签章 2-最后一个关键字签章
        String uuid = generatorService.generate("UUID");
        ExtSignAutoParams signAutoParams = new ExtSignAutoParams();
        signAutoParams.setContractId(context.getContractId());
        signAutoParams.setCustomerId(autoSign);
//                signAutoParams.setCustomerId("0EB574226E5B623D32EB7A48E76193E3");
        signAutoParams.setTransactionId(uuid);
        signAutoParams.setPositionType("0");
        signAutoParams.setDocTitle("入驻商家签约条款");
        signAutoParams.setSignKeyword("甲方（公章）");
        // 印章上传
        CustomSignatureParams customSignatureParams = new CustomSignatureParams();
        customSignatureParams.setContent(context.getSupplierName());
        customSignatureParams.setCustomerId(context.getCustomerId());
        // 绑定实名信息
        ApplyCertParams params = new ApplyCertParams();
        params.setCustomerId(context.getCustomerId());
        params.setVerifiedSerialNo(context.getTransactionNo());
        FadadaUtil bulider = new FadadaUtil.Bulider()
                .initVerifyClient(appId,appSecret,v,url)
                .setParam(params)
                // 绑定实名信息
                .initApplyCert()
                .initBaseClient(appId,appSecret,v,url)
                // 印章上传
                .setParam(customSignatureParams)
                .initBaseClient(appId,appSecret,v,url)
                .initSignature()
                //自动签署
                .setParam(signAutoParams)
                .autoExtSign()
                // 手动签署
                .setParam(extSignParams)
                .initExtSign()
                //生成短链
                .initExtraClient(appId,appSecret,v,url)
                .shortUrl()
                //发送信息
//                        .setParam(pushShortUrlSmsParams)
//                        .pushShortUrlSms(appSecret)
                .bulider();
        return BaseResponse.success(new FadadaParamsResponese(context.getCustomerId(),context.getTransactionNo(),bulider.getShortUrl()));
    }

    private String getparamter(String companyName) {
        JSONObject paramter = new JSONObject();
        paramter.put("companyName",companyName);
        return paramter.toString();
    }
    // 法大大那边的对这个号的唯一标识
    public String getCustomerIdFromRedis(String userId) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY+userId, "customerId");
    }

    // 交易号
    public String getTransactionNoFromRedis(String userId) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY+userId, "transactionNo");
    }
    // 预保存到redis公司名称
    public String getCompanyNameFromRedis(String userId) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY+userId, "companyName");
    }

    public String getContractIdFromRedis(String userId) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY+userId, "contractId");
    }

    public String generateUUID(){
        return  UUID.randomUUID().toString();
    }

    public String getEmployeeIdFromRedis (String transactionNo) {
        return (String) redisTemplate.opsForHash().get(REDIS_KEY+transactionNo,"employeeId");
    }

}
