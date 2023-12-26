package com.wanmi.sbc.customer;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.constant.CustomerLabel;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.enterpriseinfo.EnterpriseInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.follow.StoreCustomerFollowQueryProvider;
import com.wanmi.sbc.customer.api.provider.growthvalue.CustomerGrowthValueProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailModifyRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.request.enterpriseinfo.EnterpriseInfoByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.follow.StoreCustomerFollowCountRequest;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueAddRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByIdRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerSendMobileCodeRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerValidateSendMobileCodeRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.api.response.enterpriseinfo.EnterpriseInfoByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.enums.*;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.request.CustomerBaseInfoRequest;
import com.wanmi.sbc.customer.request.CustomerMobileRequest;
import com.wanmi.sbc.customer.response.CustomerBaseInfoResponse;
import com.wanmi.sbc.customer.response.CustomerCenterResponse;
import com.wanmi.sbc.customer.response.CustomerFollowsCountResponse;
import com.wanmi.sbc.customer.response.CustomerSafeResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodePageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryBindAccountRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.order.api.provider.follow.FollowQueryProvider;
import com.wanmi.sbc.order.api.request.follow.FollowCountRequest;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.vas.bean.vo.IepSettingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 客户信息
 * Created by CHENLI on 2017/7/11.
 */
@RestController
@RequestMapping("/customer")
@Validated
@Api(tags = "CustomerBaseController", description = "S2B web公用-客户信息API")
@Slf4j
public class CustomerBaseController {
    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerProvider customerProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    @Autowired
    private CustomerDetailProvider customerDetailProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private CustomerGrowthValueProvider customerGrowthValueProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private EnterpriseInfoQueryProvider enterpriseInfoQueryProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private StoreCustomerFollowQueryProvider storeCustomerFollowQueryProvider;

    @Autowired
    private FollowQueryProvider followQueryProvider;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    /**
     * 查询会员基本信息数据
     *
     * @return
     */
    @ApiOperation(value = "查询会员基本信息数据")
    @RequestMapping(value = "/customerBase", method = RequestMethod.GET)
    public BaseResponse<CustomerBaseInfoResponse> findCustomerBaseInfo() {
        String customerId = commonUtil.getOperatorId();
        CustomerVO customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId)).getContext();

        CustomerLevelVO customerLevel = customerLevelQueryProvider.getCustomerLevelWithDefaultById(
                CustomerLevelWithDefaultByIdRequest.builder().customerLevelId(customer.getCustomerLevelId()).build())
                .getContext();

        EmployeeOptionalByIdRequest idRequest = new EmployeeOptionalByIdRequest();
        idRequest.setEmployeeId(customer.getCustomerDetail().getEmployeeId());

        EmployeeOptionalByIdResponse employee = new EmployeeOptionalByIdResponse();

        if (StringUtils.isNotBlank(customer.getCustomerDetail().getEmployeeId())) {
            employee = employeeQueryProvider.getOptionalById(idRequest).getContext();
        }

        //企业信息
        Boolean isEnterpriseCustomer = !EnterpriseCheckState.INIT.equals(customer.getEnterpriseCheckState());
        EnterpriseInfoVO enterpriseInfo=null;
        if(isEnterpriseCustomer){
            BaseResponse<EnterpriseInfoByCustomerIdResponse> enterpriseInfoResponse = enterpriseInfoQueryProvider.getByCustomerId(EnterpriseInfoByCustomerIdRequest.builder()
                    .customerId(customerId)
                    .build());
            if(Objects.nonNull(enterpriseInfoResponse.getContext())){
                enterpriseInfo=enterpriseInfoResponse.getContext().getEnterpriseInfoVO();
            }
        }

        return BaseResponse.success(CustomerBaseInfoResponse.builder()
                .customerDetailId(customer.getCustomerDetail().getCustomerDetailId())
                .customerId(customerId)
                .customerAccount(customer.getCustomerAccount())
                .customerName(customer.getCustomerDetail().getCustomerName())
                .customerLevelName(customerLevel.getCustomerLevelName())
                .provinceId(customer.getCustomerDetail().getProvinceId())
                .cityId(customer.getCustomerDetail().getCityId())
                .areaId(customer.getCustomerDetail().getAreaId())
                .customerAddress(customer.getCustomerDetail().getCustomerAddress())
                .contactName(customer.getCustomerDetail().getContactName())
                .contactPhone(customer.getCustomerDetail().getContactPhone())
                .employeeName(employee.getEmployeeName())
                .birthDay(customer.getCustomerDetail().getBirthDay())
                .gender(customer.getCustomerDetail().getGender())
                .isEnterpriseCustomer(isEnterpriseCustomer)
                .enterpriseInfo(enterpriseInfo)
                .customerTag(customer.getCustomerTag())
                .businessLicenseUrl(customer.getBusinessLicenseUrl())
                .socialCreditCode(customer.getSocialCreditCode())
                .enterpriseName(customer.getEnterpriseName())
                .customerRegisterType(customer.getCustomerRegisterType())
                .enterpriseStatusXyy(customer.getEnterpriseStatusXyy())
                .parentId(customer.getParentCustomerId())
                .headImg(customer.getHeadImg())
                .build()
        );
    }

    /**
     * 修改会员基本信息
     *
     * @param customerEditRequest
     * @return
     */
    @ApiOperation(value = "修改会员基本信息")
    @RequestMapping(value = "/customerBase", method = RequestMethod.PUT)
    public BaseResponse updateCustomerBaseInfo(@Valid @RequestBody CustomerBaseInfoRequest customerEditRequest) {
        if (StringUtils.isEmpty(customerEditRequest.getCustomerId())) {
            return BaseResponse.error("参数不正确");
        }

        //防止越权
        if (!commonUtil.getOperatorId().equals(customerEditRequest.getCustomerId())) {
            return BaseResponse.error("非法越权操作");
        }

        //”基本信息->联系方式”中的联系方式，在前端进行了格式校验，后端没有校验
        if (StringUtils.isNotEmpty(customerEditRequest.getContactPhone()) &&
                !Pattern.matches(commonUtil.REGEX_MOBILE, customerEditRequest.getContactPhone())) {
            return BaseResponse.error("手机号格式不正确");
        }

        CustomerDetailModifyRequest modifyRequest = new CustomerDetailModifyRequest();
        KsBeanUtil.copyProperties(customerEditRequest, modifyRequest);
        customerDetailProvider.modifyCustomerDetail(modifyRequest);

        if (customerEditRequest.getAreaId() != null && customerEditRequest.getCustomerAddress() != null
                && customerEditRequest.getBirthDay() != null && customerEditRequest.getGender() != null) {
            // 增加成长值
            customerGrowthValueProvider.increaseGrowthValue(CustomerGrowthValueAddRequest.builder()
                    .customerId(commonUtil.getOperatorId())
                    .type(OperateType.GROWTH)
                    .serviceType(GrowthValueServiceType.PERFECTINFO)
                    .build());
            // 增加积分
            customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                    .customerId(commonUtil.getOperatorId())
                    .type(OperateType.GROWTH)
                    .serviceType(PointsServiceType.PERFECTINFO)
                    .build());
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询会员中心主页面数据
     *
     * @return
     */
    @ApiOperation(value = "查询会员中心主页面数据")
    @RequestMapping(value = "/customerCenter", method = RequestMethod.GET)
    public BaseResponse<CustomerCenterResponse> findCustomerCenterInfo() {
        String customerId = commonUtil.getOperatorId();
        log.info("customerId------------->"+customerId);
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        // log.info("customer------------->"+ JSONObject.toJSONString(customer));
        CustomerLevelVO customerLevel = new CustomerLevelVO();
        Long pointsAvailable=customer.getPointsAvailable();
        if (Objects.nonNull(customer.getCustomerLevelId())) {
            customerLevel = customerLevelQueryProvider.getCustomerLevelWithDefaultById(
                    CustomerLevelWithDefaultByIdRequest.builder().customerLevelId(customer.getCustomerLevelId()).build())
                    .getContext();
        }
        log.info("customerLevel------------->"+ JSONObject.toJSONString(customerLevel));
        if (Strings.isNotBlank(customer.getParentCustomerId())){
            CustomerGetByIdResponse parentCustomer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (customer.getParentCustomerId())).getContext();
            log.info("parentCustomer------------->"+ JSONObject.toJSONString(parentCustomer));
            pointsAvailable=parentCustomer.getPointsAvailable();
        }

        CustomerCenterResponse customerCenterResponse = CustomerCenterResponse.builder()
                .customerId(customerId)
                .customerAccount(StringUtils.substring(customer.getCustomerAccount(), 0, 3).concat("****").concat(StringUtils.substring(customer.getCustomerAccount(), 7)))
                .customerName(customer.getCustomerDetail().getCustomerName())
                .customerLevelName(customerLevel.getCustomerLevelName())
                .growthValue(customer.getGrowthValue())
                .rankBadgeImg(customerLevel.getRankBadgeImg())
                .headImg(customer.getHeadImg())
                .pointsAvailable(pointsAvailable)
                .parentId(customer.getParentCustomerId())
                .customerLabelList(new ArrayList<>())
                .enterpriseStatusXyy(customer.getEnterpriseStatusXyy())
                .customerRegisterType(customer.getCustomerRegisterType())
                .vipFlag(customer.getVipFlag())
                .build();
        if (EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseCheckState())){
            customerCenterResponse.getCustomerLabelList().add(CustomerLabel.EnterpriseCustomer);
            IepSettingVO iepSettingInfo = commonUtil.getIepSettingInfo();
            customerCenterResponse.setEnterpriseCustomerName(iepSettingInfo.getEnterpriseCustomerName());
            customerCenterResponse.setEnterpriseCustomerLogo(iepSettingInfo.getEnterpriseCustomerLogo());
        }
        return BaseResponse.success(customerCenterResponse);
    }

    /**
     * 会员中心查询会员绑定的手机号
     *
     * @return
     */
    @ApiOperation(value = "会员中心查询会员绑定的手机号")
    @RequestMapping(value = "/customerMobile", method = RequestMethod.GET)
    public BaseResponse<CustomerSafeResponse> findCustomerMobile() {
        String customerAccount = commonUtil.getOperator().getAccount();
        if (Objects.nonNull(customerAccount)) {
            return BaseResponse.success(CustomerSafeResponse.builder().customerAccount(customerAccount).build());
        }
        return BaseResponse.FAILED();
    }

    /**
     * 会员中心 修改绑定手机号 给原号码发送验证码
     *
     * @param customerAccount
     * @return
     */
    @ApiOperation(value = "会员中心 修改绑定手机号 给原号码发送验证码")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerAccount", value = "会员账户", required =
            true)
    @RequestMapping(value = "/customerVerified/{customerAccount}", method = RequestMethod.POST)
    public BaseResponse sendVerifiedCode(@PathVariable String customerAccount) {
        CustomerValidateSendMobileCodeRequest request = new CustomerValidateSendMobileCodeRequest();
        request.setMobile(customerAccount);
        //是否可以发送
        if (!customerSiteProvider.validateSendMobileCode(request).getContext().getResult()) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
        }
        //验证原手机号状态
        String result = this.checkOldCustomerAccount(customerAccount);
        if (StringUtils.isNotEmpty(result)) {
            return BaseResponse.error(result);
        }
        CustomerSendMobileCodeRequest customerSendMobileCodeRequest = new CustomerSendMobileCodeRequest();
        customerSendMobileCodeRequest.setMobile(customerAccount);
        customerSendMobileCodeRequest.setRedisKey(CacheKeyConstant.YZM_MOBILE_OLD_KEY);
        customerSendMobileCodeRequest.setSmsTemplate(SmsTemplate.CHANGE_PHONE);
        //发送验证码
        if (Constants.yes.equals(customerSiteProvider.sendMobileCode(customerSendMobileCodeRequest).getContext().getResult())) {
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.FAILED();
    }

    /**
     * 验证原手机号状态
     *
     * @param customerAccount
     * @return
     */
    private String checkOldCustomerAccount(String customerAccount) {
        String result = "";
        NoDeleteCustomerGetByAccountResponse customer =
                customerQueryProvider.getNoDeleteCustomerByAccount(new NoDeleteCustomerGetByAccountRequest
                (customerAccount)).getContext();
        if (Objects.isNull(customer)) {
            result = "该账号不存在！";
        } else {
            //如果该手机号已存在
            CustomerDetailVO customerDetail = customer.getCustomerDetail();
            //是否禁用
            if (CustomerStatus.DISABLE.toValue() == customerDetail.getCustomerStatus().toValue()) {
                result = "该手机号已被禁用！";
            }
        }

        return result;
    }

    /**
     * 会员中心 修改绑定手机号 验证原号码发送的验证码
     *
     * @param mobileRequest
     * @return
     */
    @ApiOperation(value = "会员中心 修改绑定手机号 验证原号码发送的验证码")
    @RequestMapping(value = "/oldMobileCode", method = RequestMethod.POST)
    public BaseResponse<String> validateVerifiedCode(@RequestBody CustomerMobileRequest mobileRequest) {
        //验证原手机号状态
        String result = this.checkOldCustomerAccount(mobileRequest.getCustomerAccount());
        if (StringUtils.isNotEmpty(result)) {
            return BaseResponse.error(result);
        }
        //验证验证码
        String t_verifyCode =
                redisService.getString(CacheKeyConstant.YZM_MOBILE_OLD_KEY.concat(mobileRequest.getCustomerAccount()));
        if (t_verifyCode == null || (!t_verifyCode.equalsIgnoreCase(mobileRequest.getVerifyCode()))) {
            throw new SbcRuntimeException(CommonErrorCode.VERIFICATION_CODE_ERROR);
        }

        //为了最后修改新手机号码用
        redisService.setString(CacheKeyConstant.YZM_MOBILE_OLD_KEY_AGAIN.concat(mobileRequest.getVerifyCode()),
                mobileRequest.getVerifyCode());
        //删除验证码缓存
        redisService.delete(CacheKeyConstant.YZM_MOBILE_OLD_KEY.concat(mobileRequest.getCustomerAccount()));
        return BaseResponse.success(mobileRequest.getVerifyCode());
    }

    /**
     * 会员中心 修改绑定手机号
     * 1）验证新输入的手机号
     * 2）发送验证码给新手机号
     *
     * @param customerAccount
     * @return
     */
    @ApiOperation(value = "会员中心 修改绑定手机号 发送验证码给新手机号")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerAccount", value = "会员账户", required =
            true)
    @RequestMapping(value = "/newCustomerVerified/{customerAccount}", method = RequestMethod.POST)
    public BaseResponse sendVerifiedCodeNew(@PathVariable String customerAccount) {
        CustomerValidateSendMobileCodeRequest request = new CustomerValidateSendMobileCodeRequest();
        request.setMobile(customerAccount);
        //是否可以发送
        if (!customerSiteProvider.validateSendMobileCode(request).getContext().getResult()) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
        }
        //验证新输入的手机号
        String result = this.checkCustomerAccount(customerAccount);
        if (StringUtils.isNotEmpty(result)) {
            return BaseResponse.error(result);
        }

        CustomerSendMobileCodeRequest customerSendMobileCodeRequest = new CustomerSendMobileCodeRequest();
        customerSendMobileCodeRequest.setMobile(customerAccount);
        customerSendMobileCodeRequest.setRedisKey(CacheKeyConstant.YZM_MOBILE_NEW_KEY);
        customerSendMobileCodeRequest.setSmsTemplate(SmsTemplate.CHANGE_PHONE);
        //发送验证码
        if (Constants.yes.equals(customerSiteProvider.sendMobileCode(customerSendMobileCodeRequest).getContext().getResult())) {
            return BaseResponse.SUCCESSFUL();
        }

        return BaseResponse.FAILED();
    }

    /**
     * 会员中心 修改绑定手机号
     * 1）验证新手机号码的验证码是否正确
     * 2）更换绑定手机号码
     *
     * @param mobileRequest
     * @return
     */
    @ApiOperation(value = "会员中心 修改绑定手机号")
    @RequestMapping(value = "/newMobileCode", method = RequestMethod.POST)
    public BaseResponse changeNewMobile(@RequestBody CustomerMobileRequest mobileRequest) {
        if (StringUtils.isEmpty(mobileRequest.getOldVerifyCode())) {
            return BaseResponse.error("操作失败");
        }

        //查询原验证码
        String oldVerifyCode =
                redisService.getString(CacheKeyConstant.YZM_MOBILE_OLD_KEY_AGAIN.concat(mobileRequest.getOldVerifyCode()));
        if (Objects.isNull(oldVerifyCode)) {
            return BaseResponse.error("操作失败");
        }

        String result = this.checkCustomerAccount(mobileRequest.getCustomerAccount());
        if (StringUtils.isNotEmpty(result)) {
            return BaseResponse.error(result);
        }

        //验证验证码
        String t_verifyCode =
                redisService.getString(CacheKeyConstant.YZM_MOBILE_NEW_KEY.concat(mobileRequest.getCustomerAccount()));
        if (t_verifyCode == null || (!t_verifyCode.equalsIgnoreCase(mobileRequest.getVerifyCode()))) {
            throw new SbcRuntimeException(CommonErrorCode.VERIFICATION_CODE_ERROR);
        }

        //更换绑定手机号码
//        if (Constants.yes == customerService.updateCustomerAccount(commonUtil.getOperatorId(), mobileRequest
//        .getCustomerAccount())) {
        if (Constants.yes == customerProvider.modifyCustomerAccount(new CustomerAccountModifyRequest(commonUtil
                .getOperatorId(), mobileRequest
                .getCustomerAccount())
        ).getContext().getCount()) {
            //删除验证码缓存
            redisService.delete(CacheKeyConstant.YZM_MOBILE_NEW_KEY.concat(mobileRequest.getCustomerAccount()));
            redisService.delete(CacheKeyConstant.YZM_MOBILE_OLD_KEY_AGAIN.concat(mobileRequest.getOldVerifyCode()));

            return BaseResponse.SUCCESSFUL();
        }

        return BaseResponse.FAILED();
    }

    /**
     * 验证手机号码是否存在或禁用
     *
     * @param customerAccount
     * @return
     */
    private String checkCustomerAccount(String customerAccount) {
        //原手机号
        String customerAccountOld = commonUtil.getOperator().getAccount();

        String result = "";
//        Customer customer = customerService.findByCustomerAccountAndDelFlag(customerAccount);
        NoDeleteCustomerGetByAccountResponse customer =
                customerQueryProvider.getNoDeleteCustomerByAccount(new NoDeleteCustomerGetByAccountRequest
                (customerAccount)).getContext();
        if (Objects.nonNull(customer)) {
            //如果该手机号已存在
            CustomerDetailVO customerDetail = customer.getCustomerDetail();
            //是否禁用
            if (CustomerStatus.DISABLE.toValue() == customerDetail.getCustomerStatus().toValue()) {
                result = "该手机号已被禁用！";
            } else {
                //如果新手机号不是原手机号，则新手机号已被绑定
                if (!customerAccount.equals(customerAccountOld)) {
                    result = "该手机号已被绑定！";
                }
            }
        }
        return result;
    }

    /**
     * 根据用户ID查询用户详情
     *
     * @param
     * @return
     */
    @ApiOperation(value = "根据用户ID查询用户详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员id", required = true)
    @RequestMapping(value = "/customerInfoById/{customerId}", method = RequestMethod.GET)
    public BaseResponse<CustomerDetailVO> getCustomerBaseInfo(@PathVariable String customerId) {
        if (StringUtils.isEmpty(customerId)) {
            return BaseResponse.error("参数不正确");
        }
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId)).getContext();
        return BaseResponse.success(customer.getCustomerDetail());
    }

    /**
     * 验证token
     *
     * @return
     */
    @ApiOperation(value = "验证token")
    @RequestMapping(value = "/check-token", method = RequestMethod.GET)
    public BaseResponse checkToken() {
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取当前登录人信息
     *
     * @return
     */
    @ApiOperation(value = "获取当前登录人信息")
    @RequestMapping(value = "/getLoginCustomerInfo", method = RequestMethod.GET)
    public BaseResponse<CustomerGetByIdResponse> getLoginCustomerInfo() {
        String customerId = commonUtil.getOperatorId();
        return customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId));
    }

    /**
     * 获取完善信息可得成长值和积分
     *
     * @return
     */
    @ApiOperation(value = "获取当前登录人信息")
    @RequestMapping(value = "/getGrowthValueAndPoint", method = RequestMethod.GET)
    public BaseResponse<GrowthValueAndPointResponse> getGrowthValueAndPoint() {
        String customerId = commonUtil.getOperatorId();
        BaseResponse<CustomerGetByIdResponse> customerById = customerQueryProvider.getCustomerById(
                new CustomerGetByIdRequest(customerId));
        GrowthValueAndPointResponse response = new GrowthValueAndPointResponse();
        //判断完善信息获取积分设置是否开启
        ConfigQueryRequest pointsRequest = new ConfigQueryRequest();
        pointsRequest.setConfigType(ConfigType.POINTS_BASIC_RULE_COMPLETE_INFORMATION.toValue());
        pointsRequest.setDelFlag(DeleteFlag.NO.toValue());
        ConfigVO pointsConfig =
                systemConfigQueryProvider.findByConfigTypeAndDelFlag(pointsRequest).getContext().getConfig();
        if (pointsConfig != null && pointsConfig.getStatus() == 1) {
            response.setPointFlag(true);
            response.setPoint(this.getValue(pointsConfig.getContext()));
        } else {
            response.setPointFlag(false);
            response.setPoint(0L);
        }
        //判断完善信息获取成长值设置是否开启
        ConfigQueryRequest request = new ConfigQueryRequest();
        request.setConfigType(ConfigType.GROWTH_VALUE_BASIC_RULE_COMPLETE_INFORMATION.toValue());
        request.setDelFlag(DeleteFlag.NO.toValue());
        ConfigVO growthConfig = systemConfigQueryProvider.findByConfigTypeAndDelFlag(request).getContext().getConfig();
        if (growthConfig != null && growthConfig.getStatus() == 1) {
            response.setGrowthFlag(true);
            response.setGrowthValue(this.getValue(growthConfig.getContext()));
        } else {
            response.setGrowthFlag(false);
            response.setGrowthValue(0L);
        }
        return BaseResponse.success(response);
    }

    /**
     * 积分/成长值转换
     *
     * @param value
     * @return
     */
    private Long getValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            return JSONObject.parseObject(value).getLong("value");
        } else {
            return null;
        }

    }

    /**
     * 修改会员的企业信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyEnterpriseInfo", method = RequestMethod.POST)
    public BaseResponse<CustomerGetByIdResponse> modifyEnterpriseInfo(@RequestBody @Valid CustomerEnterpriseRequest request){
        CustomerGetByIdResponse customerGetByIdResponse = customerQueryProvider
                .getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();
        if(StringUtils.isNotEmpty(customerGetByIdResponse.getParentCustomerId())){
            throw new SbcRuntimeException(CustomerErrorCode.CHILD_ACCOUNT_WIHTOUT_EDIT);
        }
        return customerProvider.modifyEnterpriseInfo(request);
    }


    /**
     * 批量绑定子账户
     * @param request
     * @return
     */
    @RequestMapping(value = "/addCustomerRela", method = RequestMethod.POST)
    @ApiOperation(value = "批量绑定子账户")
    @LcnTransaction
    public  BaseResponse<CustomerMergeRelaResponse>  addCustomerRela(@RequestBody @Valid CustomerAddRelaRequest  request){
        request.setCustomerId(commonUtil.getOperatorId());
        BaseResponse<CustomerMergeRelaResponse> result = customerProvider.addCustomerRela(request);
        List<String> customerIds = result.getContext().getCustomerMergeVOS().stream()
                .map(CustomerMergeVO::getCustomerId).distinct().collect(Collectors.toList());
        CouponCodeQueryRequest queryRequest=new CouponCodeQueryRequest();
        queryRequest.setUseStatus(DefaultFlag.NO);
        queryRequest.setNotExpire(true);
        queryRequest.setDelFlag(DeleteFlag.NO);
        queryRequest.setCustomerIds(customerIds);
        couponCodeQueryProvider.bindAccount(CouponCodeQueryBindAccountRequest.builder()
                .couponCodeQueryRequest(queryRequest).parentId(request.getCustomerId()).build());
        return result;
    }
    /**
     * 校验手机号码是否能否添加为子账户
     * @param request
     * @return
     */
    @RequestMapping(value = "/verifyCustomerRela", method = RequestMethod.POST)
    @ApiOperation(value = "校验手机号码是否能否添加为子账户")
    public  BaseResponse<CustomerVerifyRelaResponse>  verifyCustomerRela(@RequestBody @Valid CustomerAddRelaRequest  request){
        request.setCustomerId(commonUtil.getOperatorId());
        return customerQueryProvider.verifyCustomerRela(request);
    }

    /**
     * 解绑账户
     * @param request
     * @return
     */
    @RequestMapping(value = "/releaseBindCustomers", method = RequestMethod.POST)
    @ApiOperation(value = "解绑账户")
    public BaseResponse releaseBindCustomers(@RequestBody @Valid CustomerReleaseByIdRequest request){
        return customerProvider.releaseBindCustomers(request);
    }


    /**
     * 根据会员的Id查询已绑定的子账户
     * @return
     */
    @RequestMapping(value = "/queryChildsByParentId", method = RequestMethod.GET)
    @ApiOperation(value = "根据会员的Id查询已绑定的子账户")
    public   BaseResponse<CustomerListByConditionResponse> queryChildsByParentId(){
        CustomerGetByIdRequest request = new CustomerGetByIdRequest();
        request.setCustomerId(commonUtil.getOperatorId());
        BaseResponse<CustomerListByConditionResponse> baseResponse = customerQueryProvider.queryChildsByParentId(request);
        baseResponse.getContext().getCustomerVOList().stream().forEach(customer->{
            customer.setCustomerAccount(StringUtils.substring(customer.getCustomerAccount(), 0, 3).
                    concat("****").concat(StringUtils.substring(customer.getCustomerAccount(), 7))) ;
        });

        return baseResponse;
    }

    /**
     * 获取会员收藏商品、关注店铺、优惠券数量、钱包余额
     */
    @GetMapping(value = "/followGoodsAndStoreAndCouponCount")
    @ApiOperation(notes = "获取会员收藏商品、关注店铺、优惠券数量、钱包余额", value = "获取会员收藏商品、关注店铺、优惠券数量、钱包余额")
    public BaseResponse<CustomerFollowsCountResponse> followGoodsAndStoreAndCouponCount(){

        String operatorId = commonUtil.getOperatorId();
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();

        CouponCodePageRequest couponCodePageRequest = new CouponCodePageRequest();
        //店铺收藏数
        Long followNum = storeCustomerFollowQueryProvider.queryStoreCustomerFollowCountByCustomerId(
                StoreCustomerFollowCountRequest.builder().customerId(operatorId).build()).getContext().getFollowNum();

        //商品收藏数
        FollowCountRequest request = FollowCountRequest.builder()
                .customerId(operatorId)
                .build();

        if(Objects.nonNull(domainInfo)){
            request.setStoreId(domainInfo.getStoreId());
            couponCodePageRequest.setStoreId(domainInfo.getStoreId());
        }
        Long goodsFollow = followQueryProvider.count(request).getContext().getValue();

        /**
         * 优惠券数
         * TODO:待优化
         */
        couponCodePageRequest.setCustomerId(operatorId);
        List<Long> wareIds = new ArrayList<>(2);
        wareIds.add(-1L);
        wareIds.add(commonUtil.getWareId(HttpUtil.getRequest()));
        couponCodePageRequest.setWareIds(wareIds);
        long unUseCount = couponCodeQueryProvider.page(couponCodePageRequest).getContext().getUnUseCount();


        //获取用户钱包余额
        WalletByCustomerIdQueryRequest walletByCustomerIdQueryRequest =  new WalletByCustomerIdQueryRequest();
        walletByCustomerIdQueryRequest.setCustomerId(operatorId);
        BaseResponse<BalanceByCustomerIdResponse> balance = customerWalletQueryProvider.getBalanceByCustomerId(walletByCustomerIdQueryRequest);
        BigDecimal bance = new BigDecimal(0);
         if (Objects.nonNull(balance.getContext())){
            BalanceByCustomerIdResponse context = balance.getContext();
             CustomerWalletVO customerWalletVO = context.getCustomerWalletVO();
             if(customerWalletVO.getBalance() != null){
                 bance = customerWalletVO.getBalance();
             }
         }

        return BaseResponse.success(CustomerFollowsCountResponse.builder()
                .goodsFollow(goodsFollow)
                .storeFollow(followNum)
                .unUseCount(unUseCount)
                .balance(bance)
                .build());

    }




}
