package com.wanmi.sbc.third.login;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.CustomerWalletModifyRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.account.api.request.wallet.WalletByWalletIdAddRequest;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.authorize.request.WechatAuthRequest;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.enterpriseinfo.EnterpriseInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.growthvalue.CustomerGrowthValueProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.provider.quicklogin.ThirdLoginRelationQueryProvider;
import com.wanmi.sbc.customer.api.provider.quicklogin.WeChatQuickLoginProvider;
import com.wanmi.sbc.customer.api.provider.quicklogin.WeChatQuickLoginQueryProvider;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.enterpriseinfo.EnterpriseInfoByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueAddRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerBindThirdAccountRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerByAccountRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerLoginAndBindThirdAccountRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.quicklogin.ThirdLoginRelationByUidRequest;
import com.wanmi.sbc.customer.api.request.quicklogin.WeChatQuickLoginAddReq;
import com.wanmi.sbc.customer.api.request.quicklogin.WeChatQuickLoginQueryReq;
import com.wanmi.sbc.customer.api.response.loginregister.ThirdLoginAndBindResponse;
import com.wanmi.sbc.customer.bean.dto.ThirdLoginRelationDTO;
import com.wanmi.sbc.customer.bean.enums.GrowthValueServiceType;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.customer.bean.enums.ThirdLoginType;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.response.LoginResponse;
import com.wanmi.sbc.customer.service.DistributionInviteNewService;
import com.wanmi.sbc.customer.service.LoginBaseService;
import com.wanmi.sbc.distribute.DistributionCacheService;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.GetCouponGroupRequest;
import com.wanmi.sbc.marketing.api.response.coupon.GetRegisterOrStoreCouponResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.RegisterLimitType;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.setting.api.provider.storewechatminiprogramconfig.StoreWechatMiniProgramConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.wechatloginset.WechatLoginSetQueryProvider;
import com.wanmi.sbc.setting.api.request.storewechatminiprogramconfig.StoreWechatMiniProgramConfigByCacheRequest;
import com.wanmi.sbc.setting.api.request.wechatloginset.WechatLoginSetByStoreIdRequest;
import com.wanmi.sbc.setting.api.request.wechatloginset.WechatServiceStatusByStoreIdRequest;
import com.wanmi.sbc.setting.api.response.MiniProgramSetGetResponse;
import com.wanmi.sbc.setting.api.response.storewechatminiprogramconfig.StoreWechatMiniProgramConfigByCacheResponse;
import com.wanmi.sbc.setting.api.response.wechatloginset.WechatLoginSetResponse;
import com.wanmi.sbc.setting.api.response.wechatloginset.WechatLoginSetServerStatusResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.third.login.api.WechatApi;
import com.wanmi.sbc.third.login.request.WechatBindForLoginRequest;
import com.wanmi.sbc.third.login.request.WechatBindRequest;
import com.wanmi.sbc.third.login.request.WechatQuickLoginRequest;
import com.wanmi.sbc.third.login.response.*;
import com.wanmi.sbc.third.login.util.WeChatClient;
import com.wanmi.sbc.third.login.util.WeChatSession;
import com.wanmi.sbc.third.login.util.WxDataDecryptUtils;
import com.wanmi.sbc.util.CommonUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.compression.CompressionCodecs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

//import com.rop.client.CompositeResponse;
//import com.wanmi.open.sdk.SdkClient;
//import com.wanmi.open.sdk.response.WechatLoginSetRopResponse;
//import com.wanmi.open.sdk.response.WechatServerStatusRopResponse;

@Slf4j
@Api(tags = "WechatLoginController", description = "第三方登录-微信 API")
@RestController
@RequestMapping("/third/login/wechat")
public class WechatLoginController {

    private static String USER_INFO_KEY = CacheKeyConstant.WE_CHAT + SpecialSymbols.COLON.toValue() + "USER_INFO" +
            SpecialSymbols.COLON.toValue();

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdLoginRelationQueryProvider thirdLoginRelationQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private WechatLoginSetQueryProvider wechatLoginSetQueryProvider;

    @Autowired
    private CustomerSiteQueryProvider customerSiteQueryProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    @Autowired
    private CustomerGrowthValueProvider customerGrowthValueProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private WechatAuthProvider wechatAuthProvider;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Autowired
    private WechatApi wechatApi;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private DistributionCacheService distributionCacheService;

    /**
     * 注入分销邀新service
     */
    @Autowired
    private DistributionInviteNewService distributionInviteNewService;

    @Autowired
    private LoginBaseService loginBaseService;

    @Autowired
    private StoreWechatMiniProgramConfigQueryProvider storeWechatMiniProgramConfigQueryProvider;

    @Autowired
    private EnterpriseInfoQueryProvider enterpriseInfoQueryProvider;

    @Autowired
    private DistributionCustomerQueryProvider distributionCustomerQueryProvider;

    @Autowired
    private WeChatQuickLoginQueryProvider weChatQuickLoginQueryProvider;

    @Autowired
    private WeChatQuickLoginProvider weChatQuickLoginProvider;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    private static final String JSON_WEB_TOKEN = "JSON_WEB_TOKEN:";

    /**
     * 登录页（不需要token）微信授权，获得微信用户信息
     *
     * @return
     */
    @ApiOperation(value = "登录页（不需要token）微信授权，获得微信用户信息")
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public BaseResponse<ThirdLoginResponse> weChatQuickLogin(@RequestBody @Valid WechatQuickLoginRequest request) {
        return this.bind(request);
    }


    /**
     * 绑定微信账号(登录入口)
     *
     * @return
     */
    @MultiSubmit
    @ApiOperation(value = "绑定微信账号(登录入口)")
    @RequestMapping(value = "/auth/bind", method = RequestMethod.POST)
    public BaseResponse<LoginResponse> weChatBind(@Validated @RequestBody WechatBindForLoginRequest request) {
        //校验验证码，小程序端的话不需要校验
        if (StringUtils.isBlank(request.getVerifyCode()) && !request.getChannel().equals(TerminalStringType.WEAPP)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        log.info("=============绑定微信账号(登录入口)-开始,入参对象信息：{}===================", request);
        GetWeChatUserInfoResponse userInfoResponse = redisService.getObj(USER_INFO_KEY + request.getId(),
                GetWeChatUserInfoResponse.class);

        // log.error("小程序获取授权信息：昵称：" + userInfoResponse.getNickname() + ",头像："+ userInfoResponse.getHeadimgurl());

        if (userInfoResponse == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //验证 验证码是否正确,小程序端不需要验证
        String code = redisService.getString(CacheKeyConstant.WX_BINDING_LOGIN.concat(request.getPhone()));
        if (!request.getChannel().equals(TerminalStringType.WEAPP) && !request.getVerifyCode().equals(code)) {
            throw new SbcRuntimeException(CommonErrorCode.VERIFICATION_CODE_ERROR);
        }
        ThirdLoginRelationDTO thirdLoginRelation = new ThirdLoginRelationDTO();
        thirdLoginRelation.setThirdLoginType(ThirdLoginType.WECHAT);
        thirdLoginRelation.setThirdLoginOpenId(userInfoResponse.getOpenid());
        thirdLoginRelation.setThirdLoginUid(userInfoResponse.getUnionid());
        thirdLoginRelation.setNickname(userInfoResponse.getNickname());
        thirdLoginRelation.setHeadimgurl(userInfoResponse.getHeadimgurl());
        // 设置店铺Id
        thirdLoginRelation.setStoreId(commonUtil.getStoreIdWithDefault());
        CustomerLoginAndBindThirdAccountRequest customerLoginAndBindThirdAccountRequest =
                new CustomerLoginAndBindThirdAccountRequest();
        customerLoginAndBindThirdAccountRequest.setPhone(request.getPhone());
        customerLoginAndBindThirdAccountRequest.setThirdLoginRelationDTO(thirdLoginRelation);
        customerLoginAndBindThirdAccountRequest.setShareUserId(request.getShareUserId());
        CustomerVO customer = new CustomerVO();
        //判断手机号是否注册
        CustomerByAccountRequest accountRequest = new CustomerByAccountRequest();
        accountRequest.setCustomerAccount(request.getPhone());
        CustomerVO res = customerSiteQueryProvider.getCustomerByCustomerAccount(accountRequest).getContext();
        DistributionCustomerVO distributionCustomerVO = null;
        if (Objects.isNull(res)) {
            //验证邀请ID、邀请码是否正确
            distributionCustomerVO = loginBaseService.checkInviteIdAndInviteCode(request.getInviteeId(),
                    request.getInviteCode());
        }
        ThirdLoginAndBindResponse thirdLoginAndBindResponse =
                customerSiteQueryProvider.loginAndBindThirdAccount(customerLoginAndBindThirdAccountRequest).getContext();
        if (Objects.nonNull(thirdLoginAndBindResponse)) {
            KsBeanUtil.copyPropertiesThird(thirdLoginAndBindResponse, customer);
        }
        Map<String, String> vasList = redisService.hgetall(ConfigKey.VALUE_ADDED_SERVICES.toString());
        //组装登录信息
        Date date = new Date();
        String token = Jwts.builder().setSubject(thirdLoginAndBindResponse.getCustomerAccount())
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(date)
                .claim("customerId", thirdLoginAndBindResponse.getCustomerId())
                .claim("customerAccount", thirdLoginAndBindResponse.getCustomerAccount())
                .claim("customerName", thirdLoginAndBindResponse.getCustomerDetail().getCustomerName())
                .claim("customerType",thirdLoginAndBindResponse.getCustomerType())
                .claim("ip", thirdLoginAndBindResponse.getLoginIp())
                .claim("customerRegisterType",thirdLoginAndBindResponse.getCustomerRegisterType())
                .setExpiration(DateUtils.addMonths(date, 1))
                .claim(ConfigKey.VALUE_ADDED_SERVICES.toString(), JSONObject.toJSONString(vasList))
                .compact();

        /**
         * 用户token存入redis
         */
        if(!redisService.hasKey(JSON_WEB_TOKEN.concat(token))){
            // 当前时间
            OffsetDateTime startTime = OffsetDateTime.now().with(LocalTime.MAX);
            // 当前时间加七天
            OffsetDateTime endTime = OffsetDateTime.now().with(LocalTime.MIN).plusDays(300);
            redisService.setString(JSON_WEB_TOKEN.concat(token),token, ChronoUnit.SECONDS.between(startTime, endTime));
        }

        // 如果是企业会员，则增加公司信息与审核信息/不通过原因
        EnterpriseInfoVO enterpriseInfoVO =
                enterpriseInfoQueryProvider.getByCustomerId(EnterpriseInfoByCustomerIdRequest.builder()
                        .customerId(customer.getCustomerId()).build()).getContext().getEnterpriseInfoVO();
        // 登陆时查询是否是分销员邀请注册，若是则增加邀请码返回前台展示使用
        DistributionCustomerVO distributionCusVO =
                distributionCustomerQueryProvider.getByCustomerId(DistributionCustomerByCustomerIdRequest.builder()
                        .customerId(customer.getCustomerId()).build()).getContext().getDistributionCustomerVO();
        String inviteCode = StringUtils.EMPTY;
        if(Objects.nonNull(distributionCusVO)){
            String inviteId = StringUtils.isEmpty(distributionCusVO.getInviteCustomerIds()) ? StringUtils.EMPTY :
                    distributionCusVO.getInviteCustomerIds().split(",")[0];
            DistributionCustomerVO inviteCustomer =
                    distributionCustomerQueryProvider.getByCustomerId(DistributionCustomerByCustomerIdRequest.builder()
                            .customerId(inviteId).build()).getContext().getDistributionCustomerVO();
            if(Objects.nonNull(inviteCustomer)){
                inviteCode = inviteCustomer.getInviteCode();
            }
        }

        // 判断是否可以登录
        LoginResponse response = LoginResponse.builder()
                .accountName(thirdLoginAndBindResponse.getCustomerAccount())
                .customerId(thirdLoginAndBindResponse.getCustomerId())
                .token(token)
                .checkState(thirdLoginAndBindResponse.getCheckState().toValue())
                .customerDetail(thirdLoginAndBindResponse.getCustomerDetail())
                .enterpriseCheckState(customer.getEnterpriseCheckState())
                .enterpriseCheckReason(customer.getEnterpriseCheckReason())
                .enterpriseInfoVO(enterpriseInfoVO)
                .inviteCode(inviteCode)
                .build();

        // 领取注册赠券 ,如果完善信息开关打开则不能参加注册赠券活动
        boolean isPerfectCustomer = auditQueryProvider.isPerfectCustomerInfo().getContext().isPerfect();
        if (thirdLoginAndBindResponse.getIsNewCustomer() && !isPerfectCustomer) {
            // 领取注册赠券
            GetCouponGroupRequest getCouponGroupRequest = new GetCouponGroupRequest();
            getCouponGroupRequest.setCustomerId(thirdLoginAndBindResponse.getCustomerId());
            getCouponGroupRequest.setType(CouponActivityType.REGISTERED_COUPON);
            getCouponGroupRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
            GetRegisterOrStoreCouponResponse getRegisterOrStoreCouponResponse =
                    couponActivityProvider.getCouponGroup(getCouponGroupRequest).getContext();
            response.setCouponResponse(getRegisterOrStoreCouponResponse);
        }
        if (thirdLoginAndBindResponse.getIsNewCustomer() && Objects.nonNull(distributionCustomerVO)) {
            String customerName = thirdLoginAndBindResponse.getCustomerDetail().getCustomerName();
            String inviteCustomerIds = StringUtils.isNotBlank(distributionCustomerVO.getInviteCustomerIds()) ?
                    StringUtils.join(distributionCustomerVO.getCustomerId(), ",",
                            StringUtils.split(distributionCustomerVO.getInviteCustomerIds(), ",")[0]) :
                    distributionCustomerVO.getCustomerId();
            loginBaseService.addDistributionCustomer(thirdLoginAndBindResponse.getCustomerId(),
                    thirdLoginAndBindResponse.getCustomerAccount(), customerName, inviteCustomerIds);
            // 新增邀新记录
            distributionInviteNewService.addRegisterInviteNewRecord(thirdLoginAndBindResponse.getCustomerId(),
                    distributionCustomerVO.getCustomerId());
        }
        redisService.setObj(CacheKeyConstant.WEAPP_LOGIN_INFO + response.getCustomerId(), response, 30 * 60);

        /**查询用户账户*/
        BaseResponse<BalanceByCustomerIdResponse> balanceByCustomerId = customerWalletQueryProvider.getBalanceByCustomerId(
                WalletByCustomerIdQueryRequest.builder().customerId(response.getCustomerId()).build());

        if(Objects.isNull(balanceByCustomerId) ||  Objects.isNull(balanceByCustomerId.getContext()) || Objects.isNull(balanceByCustomerId.getContext().getCustomerWalletVO().getCustomerId())){
            //初始化用户钱包
            customerWalletProvider.addUserWallet(WalletByWalletIdAddRequest.builder()
                    .customerId(response.getCustomerId()).customerAccount(response.getAccountName()).customerName(response.getAccountName()).build());
            /*
            *//**新增用户账户*//*
            CustomerWalletVO addWallet = new CustomerWalletVO();
            addWallet.setBalance(BigDecimal.ZERO);
            addWallet.setGiveBalance(BigDecimal.ZERO);
            addWallet.setRechargeBalance(BigDecimal.ZERO);
            addWallet.setCustomerAccount(response.getAccountName());
            addWallet.setCustomerId(response.getCustomerId());
            addWallet.setBlockBalance(BigDecimal.ZERO);
            addWallet.setCustomerStatus(DefaultFlag.NO);
            addWallet.setDelFlag(DefaultFlag.NO);
            addWallet.setCustomerName(response.getCustomerDetail().getCustomerName());
            addWallet.setCreateTime(LocalDateTime.now());
            addWallet.setCreateId(response.getCustomerId());
            addWallet.setUpdateId(response.getCustomerId());
            addWallet.setUpdateTime(LocalDateTime.now());
            addWallet.setGiveBalanceState(DefaultFlag.NO.toValue());
            CustomerWalletModifyRequest customerWalletModifyRequest = new CustomerWalletModifyRequest();
            customerWalletModifyRequest.setCustomerWalletVO(addWallet);
            customerWalletProvider.updateCustomerWalletByWalletId(customerWalletModifyRequest);*/
        }

        return BaseResponse.success(response);
    }


    /**
     * 绑定微信账号(个人中心入口)
     *
     * @return
     */
    @ApiOperation(value = "绑定微信账号(个人中心入口)")
    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    public BaseResponse<LoginResponse> weChatBind(@Validated @RequestBody WechatBindRequest weChatBindRequest) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        WechatLoginSetResponse wechatSetResponse;
        if(Objects.nonNull(domainInfo)){
            wechatSetResponse = this.getWechatLoginSetByStoreId(domainInfo.getStoreId());
        }else {
            wechatSetResponse = wechatLoginSetQueryProvider.getInfo().getContext();
        }
        String appId;
        String secret;
        if (TerminalStringType.APP == weChatBindRequest.getType()) {
            appId = weChatBindRequest.getAppId();
            secret = weChatBindRequest.getAppSecret();
        } else if (TerminalStringType.MOBILE == weChatBindRequest.getType()) {
            appId = wechatSetResponse.getMobileAppId();
            secret = wechatSetResponse.getMobileAppSecret();
        } else if (TerminalStringType.PC == weChatBindRequest.getType()) {
            appId = wechatSetResponse.getPcAppId();
            secret = wechatSetResponse.getPcAppSecret();
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GetAccessTokeResponse resp = wechatApi.getWeChatAccessToken(appId, secret, weChatBindRequest.getCode());
        GetWeChatUserInfoResponse weChatUserInfoResponse = wechatApi.getUserInfo(resp);

        CustomerBindThirdAccountRequest thirdLoginRelation = new CustomerBindThirdAccountRequest();
        thirdLoginRelation.setThirdLoginType(ThirdLoginType.WECHAT);
        thirdLoginRelation.setThirdLoginOpenId(weChatUserInfoResponse.getOpenid());
        thirdLoginRelation.setThirdLoginUid(weChatUserInfoResponse.getUnionid());
        thirdLoginRelation.setCustomerId(commonUtil.getOperatorId());
        thirdLoginRelation.setHeadimgurl(weChatUserInfoResponse.getHeadimgurl());
        thirdLoginRelation.setNickname(weChatUserInfoResponse.getNickname());

        // 设置店铺Id
        Long storeId = commonUtil.getStoreIdWithDefault(domainInfo);
        thirdLoginRelation.setStoreId(storeId);

        customerSiteProvider.bindThirdAccount(thirdLoginRelation);
        // 增加成长值
        customerGrowthValueProvider.increaseGrowthValue(CustomerGrowthValueAddRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .type(OperateType.GROWTH)
                .serviceType(GrowthValueServiceType.BINDINGWECHAT)
                .build());
        // 增加积分
        customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .type(OperateType.GROWTH)
                .serviceType(PointsServiceType.BINDINGWECHAT)
                .build());

        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 获取微信授信配置
     *
     * @return
     */
    @ApiOperation(value = "获取微信授信配置", notes = "channel: PC,MOBILE,APP")
    @ApiImplicitParam(paramType = "path", name = "channel", value = "类型终端", required = true)
    @RequestMapping(value = "/set/detail/{channel}", method = RequestMethod.GET)
    public BaseResponse<WechatSetDetailResponse> getWechatLoginSetDetail(@PathVariable TerminalStringType channel) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        WechatLoginSetResponse wechatSetResponse;
        if(Objects.nonNull(domainInfo)){
            wechatSetResponse = this.getWechatLoginSetByStoreId(domainInfo.getStoreId());
        }else {
            wechatSetResponse = wechatLoginSetQueryProvider.getInfo().getContext();
        }
        if (channel == TerminalStringType.MOBILE) {
            if(Objects.equals(DefaultFlag.NO, wechatSetResponse.getMobileServerStatus())){
                return BaseResponse.FAILED();
            }
            return BaseResponse.success(WechatSetDetailResponse.builder().appId(wechatSetResponse
                    .getMobileAppId()).build());
        } else if (channel == TerminalStringType.PC) {
            if(Objects.equals(DefaultFlag.NO, wechatSetResponse.getPcServerStatus())){
                return BaseResponse.FAILED();
            }
            return BaseResponse.success(WechatSetDetailResponse.builder().appId(wechatSetResponse
                    .getPcAppId()).build());
        }
        return BaseResponse.FAILED();
    }

    @ApiOperation(value = "根据终端类型，获取授信开关状态", notes = "channel: PC,MOBILE,APP")
    @ApiImplicitParam(paramType = "path", name = "channel", value = "类型终端", required = true)
    @RequestMapping(value = "/status/{channel}", method = RequestMethod.GET)
    public BaseResponse<Boolean> getWechatServerStatus(@PathVariable TerminalStringType channel) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        WechatLoginSetServerStatusResponse serverStatusResponse;
        if(Objects.nonNull(domainInfo)){
            BaseResponse<WechatLoginSetServerStatusResponse> loginSetServerStatusResponse = wechatLoginSetQueryProvider.getWechatServerStatusByStoreId(WechatServiceStatusByStoreIdRequest
                    .builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if(Objects.isNull(loginSetServerStatusResponse)
                    || Objects.isNull(loginSetServerStatusResponse.getContext())){
                throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
            }
            serverStatusResponse = loginSetServerStatusResponse.getContext();
        }else {
            serverStatusResponse = wechatLoginSetQueryProvider.getWechatServerStatus().getContext();
        }
        if (channel == TerminalStringType.APP) {
            return BaseResponse.success(Objects.equals(DefaultFlag.NO, serverStatusResponse.getAppStatus()) ?
                    Boolean.FALSE : Boolean.TRUE);
        } else if (channel == TerminalStringType.MOBILE) {
            return BaseResponse.success(Objects.equals(DefaultFlag.NO, serverStatusResponse.getMobileStatus()) ?
                    Boolean.FALSE : Boolean.TRUE);
        } else if (channel == TerminalStringType.PC) {
            return BaseResponse.success(Objects.equals(DefaultFlag.NO, serverStatusResponse.getPcStatus()) ?
                    Boolean.FALSE : Boolean.TRUE);
        }
        return BaseResponse.FAILED();
    }


    @ApiOperation(value = "根据customerId获取从小程序端登录的登录信息")
    @ApiImplicitParam(paramType = "path", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/logininfo/{customerId}", method = RequestMethod.GET)
    public BaseResponse<LoginResponse> getWeappLoinInfo(@PathVariable String customerId) {
        LoginResponse response = redisService.getObj(CacheKeyConstant.WEAPP_LOGIN_INFO + customerId,
                LoginResponse.class);
        return BaseResponse.success(response);
    }


    /**
     * 仅提供小程序登录
     * @param code
     * @return
     */
    @ApiOperation(value = "小程序code换取openId和sessionKey,并将当前环境的小程序配置返回,无需登录")
    @ApiImplicitParam(paramType = "path", name = "code", value = "临时票据", required = true)
    @RequestMapping(value = "/weapp/getSessionKey/{code}", method = RequestMethod.GET)
    public BaseResponse<GetWeAppOpenIdResponse> getWeappOpenId(@PathVariable String code) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Long storeId = commonUtil.getStoreIdWithDefault(domainInfo);
        Boolean saasStatus = Objects.nonNull(domainInfo);
        GetWeAppOpenIdResponse getWeAppOpenIdResponse = new GetWeAppOpenIdResponse();
        String appId;
        String appSecret;
        if(saasStatus){
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> config = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest.builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if(config == null
                    || config.getContext() ==null){
                return BaseResponse.FAILED();
            }
            appId=config.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            appSecret=config.getContext().getStoreWechatMiniProgramConfigVO().getAppSecret();
        }else {
            MiniProgramSetGetResponse response = wechatAuthProvider.getMiniProgramSet().getContext();
            JSONObject configJson = JSONObject.parseObject(response.getContext());
            //小程序被禁用
            if (response.getStatus().equals("0")) {
                throw new SbcRuntimeException(CommonErrorCode.WEAPP_FORBIDDEN);
            }
            appId = configJson.getString("appId");
            appSecret = configJson.getString("appSecret");
        }
        //请求微信api,获取openId和access_key
        LittleProgramAuthResponse littleProgramAuthResponse = wechatApi.getLittleProgramAccessToken(appId, appSecret, code);
        //两个对象组装一下返回到前台
        getWeAppOpenIdResponse.setAppId(appId);
        getWeAppOpenIdResponse.setSessionKey(littleProgramAuthResponse.getSession_key());
        getWeAppOpenIdResponse.setOpenid(littleProgramAuthResponse.getOpenid());
        getWeAppOpenIdResponse.setUnionId(littleProgramAuthResponse.getUnionid());
        //是否绑定过微信号
        ThirdLoginRelationVO thirdLoginRelation =
                thirdLoginRelationQueryProvider.thirdLoginRelationByUidAndDeleteflagAndStoreId(
                        ThirdLoginRelationByUidRequest.builder()
                                .thirdLoginType(ThirdLoginType.WECHAT)
                                .thirdLoginUid(littleProgramAuthResponse.getUnionid()).delFlag(DeleteFlag.NO).storeId(storeId).build()
                ).getContext().getThirdLoginRelation();

        //如果不为空，代表是已经绑定过的账号，走登录流程
        if(Objects.nonNull(thirdLoginRelation)){
            getWeAppOpenIdResponse.setLoginFlag(true);
        }else{
            //否则视为新用户，走注册
            getWeAppOpenIdResponse.setLoginFlag(false);
        }

        return BaseResponse.success(getWeAppOpenIdResponse);
    }

    @ApiOperation(value = "提现页面，查询当前登录用户的基础信息")
    @RequestMapping(value = "/deposit/auth", method = RequestMethod.POST)
    public BaseResponse<WechatBaseInfoResponse> getDepositUerInfo(@RequestBody @Valid WechatQuickLoginRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        WechatLoginSetResponse wechatSetResponse;
        if(Objects.nonNull(domainInfo)){
            wechatSetResponse = this.getWechatLoginSetByStoreId(domainInfo.getStoreId());
        }else {
            wechatSetResponse= wechatLoginSetQueryProvider.getInfo().getContext();
        }
        String appId;
        String secret;
        if (TerminalStringType.APP == request.getChannel()) {
            appId = request.getAppId();
            secret = request.getAppSecret();
        } else if (TerminalStringType.MOBILE == request.getChannel()) {
            appId = wechatSetResponse.getMobileAppId();
            secret = wechatSetResponse.getMobileAppSecret();
        } else if (TerminalStringType.PC == request.getChannel()) {
            appId = wechatSetResponse.getPcAppId();
            secret = wechatSetResponse.getPcAppSecret();
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GetAccessTokeResponse resp = wechatApi.getWeChatAccessToken(appId, secret, request.getCode());
        GetWeChatUserInfoResponse weChatUserInfoResponse = wechatApi.getUserInfo(resp);
        WechatBaseInfoResponse response = new WechatBaseInfoResponse();
        response.setName(weChatUserInfoResponse.getNickname());
        response.setHeadImgUrl(weChatUserInfoResponse.getHeadimgurl());
        return BaseResponse.success(response);
    }


    /**
     * 小程序登录流程代码，乱的一批，请结合前端代码一起看
     *
     * */
    @ApiOperation(value = "登录页（不需要token）微信授权，获得微信用户信息(小程序端)")
    @PostMapping(value = "/weChatLogin")
    public BaseResponse<ThirdLoginResponse> weChatLogin(@RequestBody @Valid WechatQuickLoginRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Long storeId = commonUtil.getStoreIdWithDefault(domainInfo);
        boolean saasStatus = Objects.nonNull(domainInfo);
        String appId;
        String appSecret;
        if(saasStatus){
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> config = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest.builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if(config == null
                    || config.getContext() ==null){
                return BaseResponse.FAILED();
            }
            appId=config.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            appSecret=config.getContext().getStoreWechatMiniProgramConfigVO().getAppSecret();
        }else {
            MiniProgramSetGetResponse response = wechatAuthProvider.getMiniProgramSet().getContext();
            JSONObject configJson = JSONObject.parseObject(response.getContext());
            //小程序被禁用
            if (response.getStatus() == 0) {
                throw new SbcRuntimeException(CommonErrorCode.WEAPP_FORBIDDEN);
            }
            appId = configJson.getString("appId");
            appSecret = configJson.getString("appSecret");
        }

        WeChatClient weChatClient = new WeChatClient(appId, appSecret);
        WeChatSession weChatSession = weChatClient.authorize(request.getCode());
        // 2 request中的解密信息有两种情况，1、getUserInfo 2、getPhoneNumber（只能拿到前端传来的手机号）
        if (StringUtils.isNotBlank(request.getIv()) && StringUtils.isNotBlank(request.getEncryptedData())){
            String phoneNumber = WxDataDecryptUtils.getPhoneNumber(request.getIv(), weChatSession.getSessionKey(),
                    request.getEncryptedData());
            request.setPhonNumber(phoneNumber);
            JSONObject jsonObject = WxDataDecryptUtils.getUserBaseInfo(request.getIv(), weChatSession.getSessionKey(),
                    request.getEncryptedData());
            request.setNickName(Objects.nonNull(jsonObject.get("nickName"))?jsonObject.get("nickName").toString():"");
            request.setHeadimgurl(Objects.nonNull(jsonObject.get("avatarUrl"))?jsonObject.get("avatarUrl").toString():"");
            // log.error("小程序解析授权信息：昵称：" + request.getNickName() + ",头像："+ request.getHeadimgurl());
            if (StringUtils.isBlank(weChatSession.getUnionId())) {
                request.setUnionId(Objects.nonNull(jsonObject.get("unionId")) ? jsonObject.get("unionId").toString() : "");
            } else {
                request.setUnionId(weChatSession.getUnionId());
            }
        }
        request.setOpenId(weChatSession.getOpenId());
        return this.bind(request);
    }

    private BaseResponse<ThirdLoginResponse> bind (WechatQuickLoginRequest request){
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        WechatLoginSetResponse wechatSetResponse;
        if(Objects.nonNull(domainInfo)){
            wechatSetResponse = this.getWechatLoginSetByStoreId(domainInfo.getStoreId());
        }else {
            wechatSetResponse= wechatLoginSetQueryProvider.getInfo().getContext();
        }
        Long storeId = commonUtil.getStoreIdWithDefault(domainInfo);
        String appId;
        String secret;
        if (TerminalStringType.APP == request.getChannel()) {
            appId = request.getAppId();
            secret = request.getAppSecret();
        } else if (TerminalStringType.MOBILE == request.getChannel()) {
            appId = wechatSetResponse.getMobileAppId();
            secret = wechatSetResponse.getMobileAppSecret();
        } else if (TerminalStringType.PC == request.getChannel()) {
            appId = wechatSetResponse.getPcAppId();
            secret = wechatSetResponse.getPcAppSecret();
        } else if (TerminalStringType.WEAPP == request.getChannel()) {
            GetWeChatUserInfoResponse getWeChatUserInfoResponse = redisService.getObj(request.getOpenId(),
                    GetWeChatUserInfoResponse.class);

            if (Objects.isNull(getWeChatUserInfoResponse)){
                WeChatQuickLoginVo weChatQuickLoginVo =
                        weChatQuickLoginQueryProvider.get(WeChatQuickLoginQueryReq.builder().openId(request.getOpenId())
                                .build()).getContext();
                if (Objects.isNull(weChatQuickLoginVo)){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "查表获取Unionid为空");
                }
                getWeChatUserInfoResponse = new GetWeChatUserInfoResponse();
                getWeChatUserInfoResponse.setUnionid(weChatQuickLoginVo.getUnionId());
                getWeChatUserInfoResponse.setOpenid(request.getOpenId());
                getWeChatUserInfoResponse.setNickname(request.getNickName());
                getWeChatUserInfoResponse.setHeadimgurl(request.getHeadimgurl());
            }

            if (StringUtils.isBlank(getWeChatUserInfoResponse.getUnionid())) {
                getWeChatUserInfoResponse.setUnionid(request.getUnionId());
            }
            //将用户头像，昵称信息存放在redis中，在weChatBind()中再读取保存至数据库（注意:这个类里还有其他使用redis的地方，key别搞混了）
//            if(StringUtils.isNotBlank(getWeChatUserInfoResponse.getNickname()) && StringUtils.isNotBlank(getWeChatUserInfoResponse.getHeadimgurl())){
//                redisService.setObj(USER_INFO_KEY + getWeChatUserInfoResponse.getUnionid(), getWeChatUserInfoResponse,
//                        30 * 60);
//            }

            ThirdLoginRelationVO thirdLoginRelation = new ThirdLoginRelationVO();
            ThirdLoginResponse response = new ThirdLoginResponse();
            WechatBaseInfoResponse info = new WechatBaseInfoResponse();
            if (StringUtils.isNotBlank(getWeChatUserInfoResponse.getUnionid())) {
                thirdLoginRelation = thirdLoginRelationQueryProvider.thirdLoginRelationByUidAndDeleteflagAndStoreId(
                        ThirdLoginRelationByUidRequest.builder()
                                .thirdLoginType(ThirdLoginType.WECHAT)
                                .thirdLoginUid(getWeChatUserInfoResponse.getUnionid())
                                .delFlag(DeleteFlag.NO)
                                .storeId(storeId)
                                .build()
                ).getContext().getThirdLoginRelation();

                info.setId(getWeChatUserInfoResponse.getUnionid());
            }

            //不存在，将授权信息存放在redis里，
            if (Objects.isNull(thirdLoginRelation) || Objects.isNull(thirdLoginRelation.getCustomerId())) {
                //将用户头像，昵称信息存放在redis中，在weChatBind()中再读取保存至数据库（注意:这个类里还有其他使用redis的地方，key别搞混了）
                if(StringUtils.isNotBlank(getWeChatUserInfoResponse.getNickname()) && StringUtils.isNotBlank(getWeChatUserInfoResponse.getHeadimgurl())){

                    // log.error("小程序存储授权信息：昵称：" + getWeChatUserInfoResponse.getNickname() + ",头像："+
                    // getWeChatUserInfoResponse.getHeadimgurl());
                    redisService.setObj(USER_INFO_KEY + getWeChatUserInfoResponse.getUnionid(), getWeChatUserInfoResponse,
                            30 * 60);
                }
                //此次请求处理的是getUserInfo，提示前端在选择手机号绑定再次请求处理getPhoneNumber
                if (StringUtils.isBlank(request.getPhonNumber())){
                    response.setLoginFlag(Boolean.FALSE);
                    return BaseResponse.success(response);
                }
                if (StringUtils.isBlank(request.getInviteCode())){
                    RegisterLimitType registerLimitType = distributionCacheService.getRegisterLimitType();
                    DefaultFlag defaultFlag = distributionCacheService.queryOpenFlag();
                    if(registerLimitType == RegisterLimitType.INVITE && defaultFlag == DefaultFlag.YES) {
                        //判断当前微信绑定的手机是否是新用户；新用户需要填写邀请码
                        CustomerByAccountRequest accountRequest = new CustomerByAccountRequest();
                        accountRequest.setCustomerAccount(request.getPhonNumber());
                        CustomerVO res = customerSiteQueryProvider.getCustomerByCustomerAccount(accountRequest).getContext();
                        if (Objects.isNull(res)){
                            response.setLoginFlag(Boolean.FALSE);
                            return BaseResponse.success(response);
                        }
                    }
                }
                WechatBindForLoginRequest wechatBindForLoginRequest = new WechatBindForLoginRequest();
                wechatBindForLoginRequest.setPhone(request.getPhonNumber());
                wechatBindForLoginRequest.setId(getWeChatUserInfoResponse.getUnionid());
                wechatBindForLoginRequest.setChannel(TerminalStringType.WEAPP);
                wechatBindForLoginRequest.setInviteCode(request.getInviteCode());
                //绑定并登录
                //日志打印
                System.out.println("WechatBindForLoginRequest1 ===========" + JSON.toJSONString(wechatBindForLoginRequest));
                response.setLogin(this.weChatBind(wechatBindForLoginRequest).getContext());
                response.setLoginFlag(Boolean.TRUE);
            } else {
                //存在,走登录流程
                info.setName(thirdLoginRelation.getNickname());
                info.setHeadImgUrl(thirdLoginRelation.getHeadimgurl());
                response.setLoginFlag(Boolean.TRUE);
                String customerId = thirdLoginRelation.getCustomerId();
                LoginResponse loginResponse = commonUtil.loginByCustomerId(customerId, jwtSecretKey);
                redisService.setObj(CacheKeyConstant.WEAPP_LOGIN_INFO + loginResponse.getCustomerId(), loginResponse,
                        30 * 60);
                response.setLogin(loginResponse);
            }
            response.setInfo(info);
            redisService.delete(request.getOpenId());
            return BaseResponse.success(response);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        /*--------------------------非小程序端逻辑---------------------------------------*/
        GetAccessTokeResponse resp = wechatApi.getWeChatAccessToken(appId, secret, request.getCode());
        GetWeChatUserInfoResponse weChatUserInfoResponse = wechatApi.getUserInfo(resp);
        //判断openID是否存在
        ThirdLoginRelationVO thirdLoginRelation =
                thirdLoginRelationQueryProvider.thirdLoginRelationByUidAndDeleteflagAndStoreId(
                        ThirdLoginRelationByUidRequest.builder()
                                .thirdLoginType(ThirdLoginType.WECHAT)
                                .thirdLoginUid(weChatUserInfoResponse.getUnionid()).delFlag(DeleteFlag.NO).storeId(storeId).build()
                ).getContext().getThirdLoginRelation();
        ThirdLoginResponse response = new ThirdLoginResponse();
        //不存在，将授权信息存放在redis里，
        if (Objects.isNull(thirdLoginRelation) || Objects.isNull(thirdLoginRelation.getCustomerId())) {
            redisService.setObj(USER_INFO_KEY + weChatUserInfoResponse.getUnionid(), weChatUserInfoResponse, 30 * 60);
            response.setLoginFlag(Boolean.FALSE);
            WechatBaseInfoResponse info = new WechatBaseInfoResponse();
            info.setId(weChatUserInfoResponse.getUnionid());
            info.setName(weChatUserInfoResponse.getNickname());
            info.setHeadImgUrl(weChatUserInfoResponse.getHeadimgurl());
            response.setInfo(info);
        } else {
            //存在,走登录流程
            response.setLoginFlag(Boolean.TRUE);
            String customerId = thirdLoginRelation.getCustomerId();
            LoginResponse loginResponse = commonUtil.loginByCustomerId(customerId, jwtSecretKey);
            response.setLogin(loginResponse);
        }
        return BaseResponse.success(response);
    }

    /**
     * 门店id获得微信登录配置
     * @return
     */
    private WechatLoginSetResponse getWechatLoginSetByStoreId(Long storeId){
        BaseResponse<WechatLoginSetResponse> loginSetResponse = wechatLoginSetQueryProvider.getInfoByStoreId(WechatLoginSetByStoreIdRequest
                .builder()
                .storeId(storeId)
                .build());
        if(Objects.isNull(loginSetResponse)
                || Objects.isNull(loginSetResponse.getContext())){
            throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
        }
        return loginSetResponse.getContext();
    }

    @ApiOperation(value = "小程序code换取openId和sessionKey,并将当前环境的小程序配置返回,无需登录")
    @PostMapping(value = "/weapp/authorize")
    public BaseResponse<GetWeAppOpenIdResponse> authorize(@RequestBody WechatAuthRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        Long storeId = commonUtil.getStoreIdWithDefault(domainInfo);
        boolean saasStatus = Objects.nonNull(domainInfo);
        GetWeAppOpenIdResponse getWeAppOpenIdResponse = new GetWeAppOpenIdResponse();
        String appId;
        String appSecret;
        if(saasStatus){
            BaseResponse<StoreWechatMiniProgramConfigByCacheResponse> config = storeWechatMiniProgramConfigQueryProvider.getCacheByStoreId(StoreWechatMiniProgramConfigByCacheRequest.builder()
                    .storeId(domainInfo.getStoreId())
                    .build());
            if(config == null
                    || config.getContext() ==null){
                return BaseResponse.FAILED();
            }
            appId=config.getContext().getStoreWechatMiniProgramConfigVO().getAppId();
            appSecret=config.getContext().getStoreWechatMiniProgramConfigVO().getAppSecret();
        }else {
            MiniProgramSetGetResponse response = wechatAuthProvider.getMiniProgramSet().getContext();
            JSONObject configJson = JSONObject.parseObject(response.getContext());
            //小程序被禁用
            if (response.getStatus() == 0) {
                throw new SbcRuntimeException(CommonErrorCode.WEAPP_FORBIDDEN);
            }
            appId = configJson.getString("appId");
            appSecret = configJson.getString("appSecret");
        }
        WeChatClient weChatClient = new WeChatClient(appId, appSecret);
        WeChatSession weChatSession = weChatClient.authorize(request.getCode());
        // 获取用户基本信息
        JSONObject jsonObject = WxDataDecryptUtils.getUserBaseInfo(request.getIv(), weChatSession.getSessionKey(),
                request.getEncryptedData());

        if (Objects.isNull(jsonObject.get("unionId"))){
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        String unionId = jsonObject.get("unionId").toString();

        //两个对象组装一下返回到前台
        getWeAppOpenIdResponse.setAppId(appId);
        getWeAppOpenIdResponse.setOpenid(Objects.nonNull(jsonObject.get("openId"))?jsonObject.get("openId").toString():"");
        getWeAppOpenIdResponse.setUnionId(unionId);
        //是否绑定过微信号
        ThirdLoginRelationVO thirdLoginRelation =
                thirdLoginRelationQueryProvider.thirdLoginRelationByUidAndDeleteflagAndStoreId(
                        ThirdLoginRelationByUidRequest.builder()
                                .thirdLoginType(ThirdLoginType.WECHAT)
                                .thirdLoginUid(unionId).delFlag(DeleteFlag.NO).storeId(storeId).build()
                ).getContext().getThirdLoginRelation();

        GetWeChatUserInfoResponse getWeChatUserInfoResponse = new GetWeChatUserInfoResponse();
        getWeChatUserInfoResponse.setOpenid(Objects.nonNull(jsonObject.get("openId"))?jsonObject.get("openId").toString():"");
        getWeChatUserInfoResponse.setNickname(Objects.nonNull(jsonObject.get("nickName"))?jsonObject.get("nickName").toString():"");
        getWeChatUserInfoResponse.setSex(Objects.nonNull(jsonObject.get("gender"))?jsonObject.get("gender").toString():"");
        getWeChatUserInfoResponse.setCity(Objects.nonNull(jsonObject.get("city"))?jsonObject.get("city").toString():"");
        getWeChatUserInfoResponse.setProvince(Objects.nonNull(jsonObject.get("province"))?jsonObject.get("province").toString():"");
        getWeChatUserInfoResponse.setCountry(Objects.nonNull(jsonObject.get("country"))?jsonObject.get("country").toString():"");
        getWeChatUserInfoResponse.setHeadimgurl(Objects.nonNull(jsonObject.get("avatarUrl"))?jsonObject.get("avatarUrl").toString():"");
        getWeChatUserInfoResponse.setUnionid(Objects.nonNull(jsonObject.get("unionId"))?jsonObject.get("unionId").toString():"");

        WechatBaseInfoResponse info = new WechatBaseInfoResponse();
        info.setId(unionId);
        info.setName(getWeChatUserInfoResponse.getNickname());
        info.setHeadImgUrl(getWeChatUserInfoResponse.getHeadimgurl());
        getWeAppOpenIdResponse.setInfo(info);
        //如果不为空，代表是已经绑定过的账号，走登录流程
        if(Objects.nonNull(thirdLoginRelation) && Objects.nonNull(thirdLoginRelation.getCustomerId())){
            getWeAppOpenIdResponse.setLoginFlag(true);
            LoginResponse loginResponse = commonUtil.loginByCustomerId(thirdLoginRelation.getCustomerId(), jwtSecretKey);
            getWeAppOpenIdResponse.setLogin(loginResponse);
        }else{
            //否则视为新用户，走微信绑定手机号流程
            getWeAppOpenIdResponse.setLoginFlag(false);
        }

        redisService.setObj(getWeChatUserInfoResponse.getOpenid(), getWeChatUserInfoResponse,
                Duration.ofHours(24).getSeconds());

        WeChatQuickLoginAddReq req = new WeChatQuickLoginAddReq();
        req.setDelFlag(DeleteFlag.NO);
        req.setOpenId(getWeChatUserInfoResponse.getOpenid());
        req.setUnionId(getWeChatUserInfoResponse.getUnionid());
        req.setCreateTime(LocalDateTime.now());
        weChatQuickLoginProvider.save(req);

        return BaseResponse.success(getWeAppOpenIdResponse);
    }

    /**
     * 绑定微信账号(个人中心入口)
     *
     * @return
     */
    @ApiOperation(value = "小程序绑定微信账号(个人中心入口)")
    @RequestMapping(value = "/miniprogram/bind", method = RequestMethod.POST)
    public BaseResponse miniprogramBind(@Validated @RequestBody WechatAuthRequest wechatAuthRequest) {

        String result = wechatAuthProvider.getMiniProgramSet().getContext().getContext();
        JSONObject confJson = JSONObject.parseObject(result);
        String appId = confJson.getString("appId");
        String appSecret = confJson.getString("appSecret");
        WeChatClient weChatClient = new WeChatClient(appId, appSecret);

        // 1 微信授权
        WeChatSession weChatSession = weChatClient.authorize(wechatAuthRequest.getCode());

        // 2 获取用户基本信息
        JSONObject jsonObject = WxDataDecryptUtils.getUserBaseInfo(wechatAuthRequest.getIv(), weChatSession.getSessionKey(),
                wechatAuthRequest.getEncryptedData());

        CustomerBindThirdAccountRequest thirdLoginRelation = new CustomerBindThirdAccountRequest();
        thirdLoginRelation.setThirdLoginType(ThirdLoginType.WECHAT);
        thirdLoginRelation.setThirdLoginOpenId(Objects.nonNull(jsonObject.get("openId"))?jsonObject.get("openId").toString():"");
        thirdLoginRelation.setThirdLoginUid(Objects.nonNull(jsonObject.get("unionId"))?jsonObject.get("unionId").toString():"");
        thirdLoginRelation.setCustomerId(commonUtil.getOperatorId());
        thirdLoginRelation.setHeadimgurl(Objects.nonNull(jsonObject.get("avatarUrl"))?jsonObject.get("avatarUrl").toString():"");
        thirdLoginRelation.setNickname(Objects.nonNull(jsonObject.get("nickName"))?jsonObject.get("nickName").toString():"");
        thirdLoginRelation.setStoreId(commonUtil.getStoreIdWithDefault());
        customerSiteProvider.bindThirdAccount(thirdLoginRelation);

        WeChatQuickLoginAddReq req = new WeChatQuickLoginAddReq();
        req.setDelFlag(DeleteFlag.NO);
        req.setOpenId(thirdLoginRelation.getThirdLoginOpenId());
        req.setUnionId(thirdLoginRelation.getThirdLoginUid());
        weChatQuickLoginProvider.save(req);
        // 增加成长值
        customerGrowthValueProvider.increaseGrowthValue(CustomerGrowthValueAddRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .type(OperateType.GROWTH)
                .serviceType(GrowthValueServiceType.BINDINGWECHAT)
                .build());
        // 增加积分
        customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .type(OperateType.GROWTH)
                .serviceType(PointsServiceType.BINDINGWECHAT)
                .build());

        return BaseResponse.SUCCESSFUL();
    }
}
