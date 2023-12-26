package com.wanmi.sbc.onlineservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.IpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByMobileRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByCompanyIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreBaseInfoResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByCompanyInfoIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.onlineservice.request.CustomerServiceAccountRequest;
import com.wanmi.sbc.onlineservice.response.ContactCustomerServerResponse;
import com.wanmi.sbc.onlineservice.response.StoreCommonInfoResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.onlineservice.*;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.*;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.ImConfigRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.*;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.RecentContactVO;
import com.wanmi.sbc.setting.bean.vo.UnreadMsgNumVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-07 10:49
 * @Description: 腾讯在线客服调用系统
 * @Version 1.0
 */
@Api(tags = "ImOnLineServiceController", description = "腾讯 im api")
@RestController
@RequestMapping("/imOnlineService")
@Slf4j
@Validated
@Data
public class ImOnLineServiceController {
    @Autowired
    private OnlineServiceSaveProvider onlineServiceSaveProvider ;
    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    @Autowired
    private CustomerQueryProvider customerQueryProvider;
    @Autowired
    private RedisService redisService;
    final Long expireTime = 60*24L;
    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private CustomerServiceStoreRelationProvider customerServiceStoreRelationProvider;
    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Value(value = "${self.store.id}")
    private Long selfStoreId;
    @Autowired
    private CustomerServiceChatProvider customerServiceChatProvider;
    @Autowired
    private CustomerServiceSettingProvider customerServiceSettingProvider;
    /**
     * 获取腾讯 Im 签名
     * @param imOnlineServiceSignRequest 登录信息
     * @return 签名
     */
    @ApiOperation(value = "获取腾讯 Im 签名")
    @RequestMapping(value = "/imSign", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<String> imSign(@RequestBody @Valid ImOnlineServiceSignRequest imOnlineServiceSignRequest) {

        CustomerGetByIdRequest customerGetByIdRequest=new CustomerGetByIdRequest();
        customerGetByIdRequest.setCustomerId(imOnlineServiceSignRequest.getCustomerId());
        BaseResponse<CustomerGetByIdResponse> CList = customerQueryProvider.getCustomerById(customerGetByIdRequest);
        String customerAccount = CList.getContext().getCustomerAccount();
        imOnlineServiceSignRequest.setCustomerServiceAccount(customerAccount);

        String key= CacheKeyConstant.EVALUATE_RATIO + SpecialSymbols.UNDERLINE.toValue()+ customerAccount;
        String o = redisService.getString(key);
        if (null==o){
            ImConfigRequest request =new ImConfigRequest();
            SystemConfigResponse response =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
            JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
            String appKey= jsonObject.getString("appKey");
            long appId= jsonObject.getLong("appId");
            request.setAccount(customerAccount);
            request.setKey(appKey);
            request.setAppid(appId);
            BaseResponse<String> stringBaseResponse = systemConfigQueryProvider.imSign(request);
            redisService.setNx(key, stringBaseResponse.getContext(),expireTime);
            return stringBaseResponse ;
        }
        return BaseResponse.success(o);
    }


    /**
     * <p>添加 腾讯 Im 账号</p>
     * @param request 对应登录的手机号码
     * @return 签名
     */
    @ApiOperation(value = "添加 腾讯 Im 账号")
    @RequestMapping(value = "/addImAccount", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<String> addImAccount(@RequestBody @Valid ImOnlineServiceSignRequest request) {
        CustomerGetByIdRequest customerGetByIdRequest=new CustomerGetByIdRequest();
        customerGetByIdRequest.setCustomerId(request.getCustomerId());
        BaseResponse<CustomerGetByIdResponse> CList = customerQueryProvider.getCustomerById(customerGetByIdRequest);
        String customerAccount = CList.getContext().getCustomerAccount();
        request.setCustomerServiceAccount(customerAccount);
        onlineServiceSaveProvider.addImAccount(request);
        return BaseResponse.success(customerAccount);
    }
    /**
     * 根据商家ID查询当前商户的客服（发送人聊天对象）
     *
     * @param request 商家Id
     * @return 客服账号信息（暂时只设置一个，后期可能很多）
     */
    @ApiOperation(value = "查询当前商户的客服")
    @RequestMapping(value = {"/tencentImDetail"}, method = RequestMethod.POST)
    public BaseResponse<ContactCustomerServerResponse> tencentImDetail(@RequestBody @Valid ImOnlineServiceSignRequest request, HttpServletRequest httpRequest) {
        if (!StringUtils.isEmpty(request.getCustomerImAccount())) {
            try {
                String redisKey = "chat_" + request.getCompanyId() + "_" + request.getCustomerImAccount();
                Boolean result = redisService.setNx(redisKey, "1", 1l);
                if (!result) {
                    return BaseResponse.error("");
                }
            }
            catch (Exception e) {
                log.error("联系客服获取锁异常", e);
            }
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        request.setSourceCompanyId(request.getStoreId());
        boolean isRelationStore = false;
        String sourceStoreName = null;
        Long sourceStoreId = null;
        Long sourceCompanyId = request.getStoreId();
        if (request.getStoreId().longValue() > 0l) {
            BaseResponse<CustomerServiceStoreRelationDetailResponse> storeRelationResponse = customerServiceStoreRelationProvider.getByCompanyInfoId(request.getStoreId());
            log.info("查询商家客服 {} 绑定父级店铺 {}", request.getStoreId(), JSON.toJSONString(storeRelationResponse.getContext()));
            if (!ObjectUtils.isEmpty(storeRelationResponse.getContext()) && !ObjectUtils.isEmpty(storeRelationResponse.getContext().getParentCompanyInfoId())) {
                BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(request.getStoreId()).build());
                if (storeResponse != null && storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null) {
                    sourceStoreId = storeResponse.getContext().getStoreVO().getStoreId();
                    sourceStoreName = storeResponse.getContext().getStoreVO().getStoreName();
                }
                log.info("查询商家客服 {} 替换父级店铺 {}", request.getStoreId(), storeRelationResponse.getContext().getParentCompanyInfoId());
                request.setStoreId(storeRelationResponse.getContext().getParentCompanyInfoId());
                isRelationStore = true;
            }
            stopWatch.stop();
            log.info("联系商家客服耗时 查询父子关系耗时 {} - {}", sourceCompanyId, stopWatch.getTotalTimeSeconds());
            stopWatch.start();
        }
        else {
            sourceStoreId = -1l;
            sourceStoreName = "大白鲸平台";
        }

        /**
         * 如果店铺为喜吖吖代管商家，客户配置统一使用喜吖吖的客服配置
         */
        BaseResponse<StoreByCompanyInfoIdResponse> storeResponse = null;
        if (request.getStoreId().longValue() > 0l) {
            storeResponse = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(request.getStoreId()).build());
            stopWatch.stop();
            log.info("联系商家客服耗时 查询店铺信息耗时 {} - {}", sourceCompanyId, stopWatch.getTotalTimeSeconds());
            stopWatch.start();
        }
        // 如果商家为由喜吖吖代管的商家，获取客户信息，全部取喜吖吖的数据（所以，这边把companyId设置为1174,1174为喜吖吖店铺ID）
        if (!isRelationStore && storeResponse != null && storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null
                && ((Integer) 1).equals(storeResponse.getContext().getStoreVO().getSelfManage())) {
            sourceStoreId = storeResponse.getContext().getStoreVO().getStoreId();
            sourceStoreName = storeResponse.getContext().getStoreVO().getStoreName();
            log.info("查询商家客服 {} 为自有商家替换 {} - {}", request.getStoreId(), 1230, selfStoreId);
            // 设置为系统唯一的自营商家 公司ID 1174
            request.setStoreId(selfStoreId != null ? selfStoreId : 1230l);
        }


        request.setCompanyId(request.getStoreId());
        // 查询商家客服总开关类型 CustomerServiceType
        BaseResponse<CustomerServiceSwitchResponse> switchResponse = onlineServiceQueryProvider.getCustomerServiceSwitch(request);
        stopWatch.stop();
        log.info("联系商家客服耗时 查询客服开关 {} - {}", sourceCompanyId, stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        if (CustomerServiceType.CLOSE.getType().equals(switchResponse.getContext().getServiceSwitchType())) {
            BaseResponse baseResponse = BaseResponse.success(ImOnlineServiceListResponse.builder().imOnlineServerItemRopList(new ArrayList<>()).imOnlineServerRop(new ImOnlineServiceVO())
                    .customerServiceType(CustomerServiceType.CLOSE).build());
            baseResponse.setMessage("商家未开启客服功能");
            return baseResponse;
        }

        if (switchResponse.getContext().isInitIMAccount()) {
            if (storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null) {


                StoreVO storeVO = storeResponse.getContext().getStoreVO();
                OnlineServiceListRequest initRequest = OnlineServiceListRequest.builder()
                        .companyInfoId(request.getStoreId()).realStoreId(storeVO.getStoreId())
                        .storeId(storeVO.getStoreId()).storeName(storeVO.getStoreName()).build();
                BaseResponse<EmployeeByCompanyIdResponse> employeeResponse = employeeQueryProvider.getByCompanyId(EmployeeByCompanyIdRequest.builder().companyInfoId(initRequest.getCompanyInfoId()).build());
                if (employeeResponse.getContext() != null) {
                    initRequest.setStoreMasterEmployeeId(employeeResponse.getContext().getEmployeeId());
                    initRequest.setStoreMasterEmployeeMobile(employeeResponse.getContext().getEmployeeMobile());
                }
                onlineServiceSaveProvider.initStoreIMCustomerService(initRequest);
                stopWatch.stop();
                log.info("联系商家客服耗时 初始化商家客服账号耗时 {} - {}", sourceCompanyId, stopWatch.getTotalTimeSeconds());
                stopWatch.start();
            }
            else {
                BaseResponse baseResponse = BaseResponse.success(ImOnlineServiceListResponse.builder().imOnlineServerItemRopList(new ArrayList<>()).imOnlineServerRop(new ImOnlineServiceVO())
                        .customerServiceType(CustomerServiceType.CLOSE).build());
                baseResponse.setMessage("商家未开启客服功能");
                return baseResponse;
            }
        }

//        CompanyInfoByIdRequest companyInfoByIdRequest = CompanyInfoByIdRequest.builder().companyInfoId(request.getStoreId()).build();
//        BaseResponse<CompanyInfoByIdResponse> baseResponse = companyInfoQueryProvider.getCompanyInfoById(companyInfoByIdRequest);
//        CompanyInfoByIdResponse companyInfoByIdResponse = baseResponse.getContext();
//        // 统仓统配 商家类型公用 喜吖吖店铺 客服，将公司ID设置为喜吖吖公司ID
//        if (CompanyType.UNIFIED.equals(companyInfoByIdResponse.getCompanyType())) {
//            // 设置为系统唯一的自营商家 公司ID 1174
//            request.setStoreId(1174l);
//        }

        OnlineServiceListRequest onlineServiceListRequest = OnlineServiceListRequest.builder().storeId(request.getStoreId()).build();
        KsBeanUtil.copyProperties(request, onlineServiceListRequest);
        onlineServiceListRequest.setUserId(commonUtil.getOperatorId());
        onlineServiceListRequest.setServiceSwitchType(switchResponse.getContext().getServiceSwitchType());
        onlineServiceListRequest.setIpAddr(IpUtil.getIpAddr(httpRequest));
        onlineServiceListRequest.setSourceStoreId(sourceStoreId);
        onlineServiceListRequest.setSourceCompanyId(sourceCompanyId);
        onlineServiceListRequest.setStoreName(sourceStoreName);
        if (storeResponse != null && storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null) {
            onlineServiceListRequest.setStoreLogo(storeResponse.getContext().getStoreVO().getStoreLogo());
            if (onlineServiceListRequest.getSourceStoreId() == null || StringUtils.isEmpty(sourceStoreName)) {
                onlineServiceListRequest.setSourceStoreId(storeResponse.getContext().getStoreVO().getStoreId());
                onlineServiceListRequest.setStoreName(storeResponse.getContext().getStoreVO().getStoreName());
            }
        }
        else if (request.getStoreId().longValue() == -1) {
            onlineServiceListRequest.setStoreName("大白鲸平台");
            onlineServiceListRequest.setSourceCompanyId(-1l);
            onlineServiceListRequest.setSourceStoreId(-1l);
            onlineServiceListRequest.setStoreId(-1l);
            onlineServiceListRequest.setCompanyInfoId(-1l);
            onlineServiceListRequest.setStoreLogo("https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311301552060621.png");
        }
        BaseResponse<ImOnlineServiceListResponse> result =  onlineServiceQueryProvider.getCompanyServiceAccount(onlineServiceListRequest);
        stopWatch.stop();
        log.info("联系商家客服耗时 分配客服即创建群聊 {} - {}", sourceCompanyId, stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        // 如果商家客服开启智齿
        if (CustomerServiceType.SOBOT.getType().equals(switchResponse.getContext().getServiceSwitchType())) {
            // 返回APP端，商家使用智齿客服类型
            result.getContext().setCustomerServiceType(CustomerServiceType.SOBOT);
        }
        ContactCustomerServerResponse customerServerResponse = KsBeanUtil.convert(result.getContext(), ContactCustomerServerResponse.class);
        if (storeResponse != null && storeResponse.getContext() != null && storeResponse.getContext().getStoreVO() != null) {
            customerServerResponse.setStoreVO(KsBeanUtil.convert(storeResponse.getContext().getStoreVO(), StoreCommonInfoResponse.class));
        }
        BaseResponse baseResponse = BaseResponse.success(customerServerResponse);
        stopWatch.stop();
        log.info("联系商家客服总耗时 {} - {} - {}", stopWatch.getTotalTimeSeconds(), request.getCustomerImAccount(), JSON.toJSONString(baseResponse));
        return baseResponse;
    }
    /**
     * 购物车im  通过  店铺id查询
     *
     * @param request 商家Id
     * @return 客服账号信息
     */
    @ApiOperation(value = "查询当前商户的客服")
    @RequestMapping(value = {"/tencentImDetailByStoreId"}, method = RequestMethod.POST)
    public BaseResponse<ImOnlineServiceListResponse> tencentImDetailByStoreId(@RequestBody @Valid ImOnlineServiceSignRequest request) {

        BaseResponse<StoreInfoResponse> response = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(request.getStoreId()).build());
        request.setCompanyId(response.getContext().getCompanyInfoId());
        request.setStoreId(response.getContext().getCompanyInfoId());
        // 查询商家客服总开关类型 CustomerServiceType
        BaseResponse<CustomerServiceSwitchResponse> switchResponse = onlineServiceQueryProvider.getCustomerServiceSwitch(request);
        if (CustomerServiceType.CLOSE.getType().equals(switchResponse.getContext().getServiceSwitchType())) {
            BaseResponse baseResponse = BaseResponse.success(ImOnlineServiceListResponse.builder().imOnlineServerItemRopList(new ArrayList<>()).imOnlineServerRop(new ImOnlineServiceVO())
                    .customerServiceType(CustomerServiceType.CLOSE).build());
            baseResponse.setMessage("商家未开启客服功能");
            return baseResponse;
        }
        /**
         * 查询公司信息，如果商家类型 companyType=2 为 “统仓统配”，那么商家共享 “喜吖吖” 商品、客服
         */
        CompanyInfoByIdRequest companyInfoByIdRequest = CompanyInfoByIdRequest.builder().companyInfoId(request.getStoreId()).build();
        BaseResponse<CompanyInfoByIdResponse> baseResponse = companyInfoQueryProvider.getCompanyInfoById(companyInfoByIdRequest);
        CompanyInfoByIdResponse companyInfoByIdResponse = baseResponse.getContext();
        // 统仓统配 商家类型公用 喜吖吖店铺 客服，将公司ID设置为喜吖吖公司ID
        if (CompanyType.UNIFIED.equals(companyInfoByIdResponse.getCompanyType())) {
            // 设置为系统唯一的自营商家 公司ID 1174
            request.setStoreId(1174l);
        }

        OnlineServiceListRequest onlineServiceListRequest = OnlineServiceListRequest.builder().storeId(request.getStoreId()).build();
        onlineServiceListRequest.setUserId(commonUtil.getOperatorId());
        BaseResponse<ImOnlineServiceListResponse> result =  onlineServiceQueryProvider.getCompanyServiceAccount(onlineServiceListRequest);

        // 如果商家客服开启智齿
        if (CustomerServiceType.SOBOT.getType().equals(switchResponse.getContext().getServiceSwitchType())) {
            // 返回APP端，商家使用智齿客服类型
            result.getContext().setCustomerServiceType(CustomerServiceType.SOBOT);
        }
        return result;
    }
    /**
     *获取平台账号 userid
     */
    @ApiOperation(value = "查询当前运营的客服")
    @RequestMapping(value = {"/platformUserId"}, method = RequestMethod.POST)
    public BaseResponse<String> platformUserId(@RequestBody @Valid ImOnlineServiceModifyRequest request) {
        request.setStoreId(Long.valueOf(-1));
        request.setCompanyId(Long.valueOf(-1));
        return onlineServiceQueryProvider.platformUserId(request);
    }
    /**
     * 拉起会话列表
     * @return BaseResponse
     */
    @ApiOperation(value = "拉起会话列表")
    @RequestMapping(value = {"/tencentImGetList"}, method = RequestMethod.POST)
    public BaseResponse<RecentContactVO> tencentImGetList(@RequestBody  @Valid  ImOnlineServiceSignRequest request) {
        CustomerGetByIdRequest customerGetByIdRequest=new CustomerGetByIdRequest();
        customerGetByIdRequest.setCustomerId(request.getCustomerId());
        BaseResponse<CustomerGetByIdResponse> CList = customerQueryProvider.getCustomerById(customerGetByIdRequest);
        String customerAccount = CList.getContext().getCustomerAccount();
        request.setCustomerServiceAccount(customerAccount);
        return   systemConfigQueryProvider.tencentImGetList(request);
    }

    /**
     * 查询单聊未读数(每个人的)
     * @return  BaseResponse
     */
    @ApiOperation(value = "查询单聊未读数")
    @RequestMapping(value = {"/tencentImUnreadMsgNum"}, method = RequestMethod.POST)
    public BaseResponse<UnreadMsgNumVO> unreadMsgNum(@RequestBody  @Valid  ImOnlineServiceUnReadRequest signRequest) {
        return   systemConfigQueryProvider.unreadMsgNum(signRequest);
    }
    /**
     * 删除会话
     * @return  BaseResponse
     */
    @ApiOperation(value = "删除会话")
    @RequestMapping(value = {"/tencentImDelMsg"}, method = RequestMethod.POST)
    public BaseResponse<String> tencentImDelMsg(@RequestBody   @Valid  ImOnlineServiceDelMsgRequest signRequest) {
        return   systemConfigQueryProvider.tencentImDelMsg(signRequest);
    }

    /**
     * 查询单聊未读数当前商户的总数
     * @return  BaseResponse
     */
    @ApiOperation(value = "查询单聊未读数")
    @RequestMapping(value = {"/tencentMerchantImUnreadMsgNum"}, method = RequestMethod.POST)
    public BaseResponse<UnreadMsgNumVO> merchantUnreadMsgNum(@RequestBody  @Valid  ImOnlineServiceSignRequest signRequest) {
        return   systemConfigQueryProvider.merchantUnreadMsgNum(signRequest);
    }

    @ApiOperation(value = "根据手机号码获取客服IM账号")
    @RequestMapping(value = {"/getCustomerServiceImAccount"}, method = RequestMethod.POST)
    public BaseResponse getCustomerServiceImAccount(@RequestBody CustomerServiceAccountRequest request) {
        return onlineServiceQueryProvider.getCustomerServiceAccountByMobile(request.getMobileNumber());
    }

    @ApiOperation(value = "获取商家在线的客服列表")
    @RequestMapping(value = "/getOnlineImAccount", method = RequestMethod.POST)
    public BaseResponse getOnlineImAccount (@RequestBody ImOnlineServiceSignRequest request) {
        if (request.getCompanyId() == null) {
            return BaseResponse.error("公司ID不能为空");
        }
        return onlineServiceQueryProvider.getOnlineImAccount(request);
    }


    @ApiOperation(value = "切换到指定客服")
    @RequestMapping(value = "/switchStoreIMAccount", method = RequestMethod.POST)
    public BaseResponse switchStoreIMAccount (@RequestBody OnlineServiceListRequest request) {
        if (StringUtils.isEmpty(request.getServerAccount())) {
            return BaseResponse.error("请选择商家客服");
        }
        if (request.getCompanyInfoId() == null) {
            return BaseResponse.error("公司ID不能为空");
        }
        return onlineServiceQueryProvider.switchStoreIMAccount(request);
    }

    @ApiOperation(value = "获取聊天消息详情")
    @RequestMapping(value = "/getChatDetailInfo", method = RequestMethod.POST)
    public BaseResponse<CustomerServiceChatResponse> getChatDetailInfo (@RequestBody CustomerServiceChatRequest request) {
        if (StringUtils.isEmpty(request.getImGroupId())) {
            return BaseResponse.error("参数不能为空");
        }
        return customerServiceChatProvider.getChatDetailInfo(request);
    }

    @ApiOperation(value = "获取结束会话的配置语")
    @RequestMapping(value = "/getCloseLanguage", method = RequestMethod.POST)
    public BaseResponse getCloseLanguage () {
        CustomerServiceSettingRequest request = CustomerServiceSettingRequest.builder().companyInfoId(0l).build();
        BaseResponse baseResponse = customerServiceSettingProvider.getCustomerServiceSettingList(request);
        List<CustomerServiceSettingResponse> settingList = (List<CustomerServiceSettingResponse>) baseResponse.getContext();
        List<String> resultList = new ArrayList<>();
        if (ObjectUtils.isEmpty(settingList)) {
            return BaseResponse.success(resultList);
        }
        for (CustomerServiceSettingResponse setting : settingList) {
            if (ImSettingTypeEnum.Close.getType().equals(setting.getSettingType()) || ImSettingTypeEnum.HandClose.getType().equals(setting.getSettingType())) {
                if (setting.getContent().containsKey("message")) {
                    resultList.add(setting.getContent().getString("message"));
                }
            }
        }
        return BaseResponse.success(resultList);
    }
}
