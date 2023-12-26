package com.wanmi.sbc.fadadaInterface;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.ValidateUtil;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.provider.contract.CustomerContractProviderTo;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.employeecontract.EmployeeContractProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeLoginRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeRegisterRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.CustomerBlackRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractFindRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractSaveRequest;
import com.wanmi.sbc.customer.api.request.fadada.WordParamsRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByAccountNameResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeRegisterResponse;
import com.wanmi.sbc.customer.api.response.employeecontract.CustomerBlackResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import com.wanmi.sbc.fadadaInterface.response.AddressResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.HttpUtils;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

@Api(tags = "SupplierInController", description = "线上合同签署H5API")
@RestController("SupplierInController")
@RequestMapping("/supplierIn")
@Slf4j
public class SupplierInController {
    @Autowired
    private CustomerContractProviderTo customerContractProviderTo;
    @Autowired
    private EmployeeContractProvider employeeContractProvider;
    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Autowired
    private YunServiceProvider yunServiceProvider;
    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private RedisService redisService;
    @Autowired
    private EmployeeProvider employeeProvider;
    @Autowired
    private CommonUtil commonUtil;
    @ApiOperation(value = "保存入驻基础信息")
    @PostMapping(value = "/saveSupplierIn")
    @Transactional
    @LcnTransaction
    public BaseResponse saveSupplierIn(@RequestBody WordParamsRequest wordParamsRequest, HttpServletRequest request) throws Exception {
        log.info("保存入驻基本信息========{}",wordParamsRequest.toString());
        // 手机号码验证
        //发送验证码，验证手机号
        Integer type = 2;
        String account = wordParamsRequest.getContractPhone();
        if(!ValidateUtil.isPhone(account)){
            log.error("手机号码:{}格式错误", account);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"手机号码错误");
        }
        CustomerBlackRequest customerBlackRequest = new CustomerBlackRequest();
        customerBlackRequest.setStoreName(wordParamsRequest.getCompanyName());
        CustomerBlackResponese blackResponese = employeeContractProvider.findByStoreName(customerBlackRequest).getContext();
        if (blackResponese!=null) {
            log.error("商家--"+wordParamsRequest.getCompanyName()+"--被禁用");
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该营业执照已被禁用");
        }
        //传了类型则校验同商家类型下该手机号是否已注册，不传类型则默认为商家，校验是否存在该手机号注册的商家
        if (Objects.nonNull(type) && employeeQueryProvider.getByAccountName(EmployeeByAccountNameRequest.builder()
                .accountName(account).accountType(AccountType.fromValue(type)).build()).getContext().getEmployee() != null
                || Objects.isNull(type) &&  employeeQueryProvider.getByAccountName(EmployeeByAccountNameRequest.builder()
                .accountName(account).accountType(AccountType.s2bSupplier).build()).getContext().getEmployee() != null ){
            log.error("手机号码:{}已注册", account);
            throw new SbcRuntimeException(EmployeeErrorCode.ALREADY_EXIST);
        }
        EmployeeContractFindRequest em = new EmployeeContractFindRequest();
        em.setSupplierName(wordParamsRequest.getCompanyName());
        em.setInvestmentManagerId(wordParamsRequest.getInvestemntManagerId());
        if (employeeContractProvider.findByInverstmentIdAndSupplierName(em).getContext() != null) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,wordParamsRequest.getCompanyName()+"已注册相同公司和入驻经理");
        }
        // 判断签约商城如果不是数字就报错
        if (!StringUtils.isNumeric(wordParamsRequest.getTabRelationValue())
        ||  !StringUtils.isNumeric(wordParamsRequest.getRelationValue())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请选择正确的商城或者批发市场");
        }
        // 保存合同基本信息
        String employeeId = StringUtils.isEmpty(commonUtil.getUserInfo().getUserId())?null:commonUtil.getUserInfo().getUserId();
        wordParamsRequest.setAppCustomerId(employeeId);
        customerContractProviderTo.save(wordParamsRequest);
        // 保存合同员工表，入驻经理
        saveContract(employeeId,wordParamsRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "上传图片")
    @RequestMapping(value = "/uploadContractImage", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadContractImage(@RequestParam("uploadFile")List<MultipartFile> multipartFiles, Long
            cateId) {

        //验证上传参数
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<String> resourceUrls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (file == null || file.getSize() == 0) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            try {
                // 上传
                String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                        .cateId(cateId)
                        .resourceType(ResourceType.IMAGE)
                        .content(file.getBytes())
                        .resourceName(file.getOriginalFilename())
                        .build()).getContext();
                resourceUrls.add(resourceUrl);
            } catch (Exception e) {
                throw new SbcRuntimeException(e);
            }
        }
        operateLogMQUtil.convertAndSend("设置", "上传图片", "操作成功");
        return ResponseEntity.ok(resourceUrls);
    }

    /**
     * 商家注册
     * */
    @ApiOperation(value = "商家注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseResponse<EmployeeRegisterResponse> register(@Valid @RequestBody EmployeeLoginRequest loginRequest){
        log.info("商家注册========={}",loginRequest.toString());
        EmployeeLoginRequest employeeLoginRequest = new EmployeeLoginRequest();
        BeanUtils.copyProperties(loginRequest,employeeLoginRequest);
        //没传账号类型，默认2，商家
        if(Objects.isNull(loginRequest.getAccountType())){
            employeeLoginRequest.setAccountType(2);
        }
        //没传店铺类型，默认1，商家
        if(Objects.isNull(loginRequest.getStoreType())){
            employeeLoginRequest.setStoreType(1);
        }
        //验证手机号
        if(!ValidateUtil.isPhone(employeeLoginRequest.getAccount())){
            log.error("手机号码:{}格式错误", loginRequest.getAccount());
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //同种商家类型的该手机号是否已注册
        EmployeeByAccountNameResponse accountNameResponse = employeeQueryProvider.getByAccountName(EmployeeByAccountNameRequest.builder()
                .accountName(employeeLoginRequest.getAccount()).accountType(AccountType.fromValue(employeeLoginRequest.getAccountType())).build()).getContext();
        EmployeeRegisterResponse employeeRegisterResponse = new EmployeeRegisterResponse();
        if ( accountNameResponse.getEmployee() != null){
            log.error("手机号码:{}已注册", employeeLoginRequest.getAccount());
            employeeRegisterResponse.setEmployeeId(accountNameResponse.getEmployee().getEmployeeId());
            employeeRegisterResponse.setCompanyInfoId(accountNameResponse.getEmployee().getCompanyInfoId());
            return BaseResponse.success(employeeRegisterResponse);
        }
        EmployeeRegisterRequest registerRequest = new EmployeeRegisterRequest();
        KsBeanUtil.copyPropertiesThird(employeeLoginRequest, registerRequest);
        employeeRegisterResponse = employeeProvider.register(registerRequest).getContext();
        if (Objects.nonNull(employeeRegisterResponse)){
            //删除验证码缓存--区分商家类型
            if(AccountType.s2bSupplier.toValue()==employeeLoginRequest.getAccountType()){
                redisService.delete(CacheKeyConstant.YZM_SUPPLIER_REGISTER.concat(employeeLoginRequest.getAccount()));
            }
            if(AccountType.s2bProvider.toValue()==employeeLoginRequest.getAccountType()){
                redisService.delete(CacheKeyConstant.YZM_PROVIDER_REGISTER.concat(employeeLoginRequest.getAccount()));
            }
            operateLogMQUtil.convertAndSend("公司信息", "商家注册", "注册成功：账号" + (Objects.nonNull(loginRequest) ? loginRequest.getAccount() : ""));
            return BaseResponse.success(employeeRegisterResponse);
        }
        operateLogMQUtil.convertAndSend("公司信息", "商家注册", "注册失败：账号" + (Objects.nonNull(loginRequest) ? loginRequest.getAccount() : ""));
        return BaseResponse.FAILED();
    }

    @ApiOperation(value = "商家地址解析")
    @GetMapping(value = "/addressParse/{address}")
    public BaseResponse<AddressResponse> addressParse(@PathVariable String address) {
        String host = "https://addre.market.alicloudapi.com";
        String path = "/format";
        String method = "GET";
        String appcode = "8e8192dab1ed401ba9675f6782529197";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("text", address);
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            JSONObject data = responseJson.getJSONObject("data");
            AddressResponse addressResponse = new AddressResponse();
            addressResponse.setDetailAddress(data.getString("detail"));
            addressResponse.setCountyCode(data.getLong("county_code"));
            addressResponse.setProvinceCode(data.getLong("province_code"));
            addressResponse.setCityCode(data.getLong("city_code"));
            return BaseResponse.success(addressResponse);
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"地址无法解析请手动输入");
        }
    }


    private void saveContract(String employeeId,WordParamsRequest wordParamsRequest) {
        log.info("保存合同相关信息资料==========={}",wordParamsRequest.toString());
        // 保存合同员工表，入驻经理
        EmployeeContractResponese context = new EmployeeContractResponese();
        if (StringUtils.isNotEmpty(wordParamsRequest.getContractPhone())) {
            context = employeeContractProvider.findByEmployeeName(wordParamsRequest.getContractPhone()).getContext();
        }
        if (StringUtils.isNotEmpty(employeeId) && context == null){
            context = employeeContractProvider.findByAppCustomerId(employeeId).getContext();
        }
        if (StringUtils.isNotEmpty(wordParamsRequest.getAppId()) && context == null) {
            context = employeeContractProvider.findByAppId(wordParamsRequest.getAppId()).getContext();
        }
        EmployeeContractSaveRequest employeeContractSaveRequest = new EmployeeContractSaveRequest();
        if (!Objects.isNull(context)) {
            KsBeanUtil.copyProperties(context,employeeContractSaveRequest);
        }
        employeeContractSaveRequest.setInvestmentManager(wordParamsRequest.getInvestmentManager());
        employeeContractSaveRequest.setInvestemntManagerId(wordParamsRequest.getInvestemntManagerId());
        employeeContractSaveRequest.setEmployeeName(wordParamsRequest.getContractPhone());
        employeeContractSaveRequest.setAppCustomerId(employeeId);
        employeeContractSaveRequest.setSignType(0);
        employeeContractSaveRequest.setAppId(wordParamsRequest.getAppId());
        employeeContractSaveRequest.setStatus(0);
        employeeContractSaveRequest.setSupplierName(wordParamsRequest.getCompanyName());
        employeeContractSaveRequest.setCreateTime(LocalDateTime.now());
        employeeContractSaveRequest.setUserContractId(StringUtils.isNotEmpty(employeeContractSaveRequest.getUserContractId())?employeeContractSaveRequest.getUserContractId():generateMerchantNumber());
        employeeContractSaveRequest.setIsPerson(wordParamsRequest.getIsPerson());
        employeeContractProvider.save(employeeContractSaveRequest);
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


}
