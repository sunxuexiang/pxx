package com.wanmi.sbc.imMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.DisableCustomerDetailGetByAccountRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.DisableCustomerDetailGetByAccountResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceSaveProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceDelMsgRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceUnReadRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.ImConfigRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceSignResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.RecentContactVO;
import com.wanmi.sbc.setting.bean.vo.UnreadMsgNumVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-07 10:49
 * @Description: 腾讯在线客服调用系统
 * @Version 1.0
 */
@Api(tags = "ImOnLineServiceController", description = "腾讯 im api")
@RestController
@RequestMapping("/imOnlineService/web")
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
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 获取腾讯 Im 签名
     * @param imOnlineServiceSignRequest 登录信息
     * @return 签名
     */
    @ApiOperation(value = "获取腾讯 Im 签名")
    @RequestMapping(value = "/imSign", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<ImOnlineServiceSignResponse> imSign(@RequestBody @Valid ImOnlineServiceSignRequest imOnlineServiceSignRequest) {
        if (StringUtils.isEmpty(imOnlineServiceSignRequest.getCustomerServiceAccount())) {
            return BaseResponse.error("IM账号不能为空");
        }
        String customerAccount = imOnlineServiceSignRequest.getCustomerServiceAccount();
        ImConfigRequest request =new ImConfigRequest();
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
        String appKey= jsonObject.getString("appKey");
        long appId= jsonObject.getLong("appId");

        String key= CacheKeyConstant.EVALUATE_RATIO + SpecialSymbols.UNDERLINE.toValue()+ customerAccount;
        String cacheSign = redisService.getString(key);
        ImOnlineServiceSignResponse imOnlineResponse = new ImOnlineServiceSignResponse();
        imOnlineResponse.setAppid(appId);
        if (!StringUtils.isEmpty(cacheSign)) {
            imOnlineResponse.setSign(cacheSign);
            return BaseResponse.success(imOnlineResponse) ;
        }
        request.setAccount(customerAccount);
        request.setKey(appKey);
        request.setAppid(appId);
        BaseResponse<String> stringBaseResponse = systemConfigQueryProvider.imSign(request);
        redisService.setNx(key, stringBaseResponse.getContext(),expireTime);
        imOnlineResponse.setSign(stringBaseResponse.getContext());
        return BaseResponse.success(imOnlineResponse) ;
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
        log.info("添加腾讯IM账号打印登录信息 {}", JSON.toJSONString(commonUtil.getOperator()));
        DisableCustomerDetailGetByAccountRequest customerRequest = new DisableCustomerDetailGetByAccountRequest();
        customerRequest.setCustomerAccount(commonUtil.getOperator().getAccount());
        BaseResponse<DisableCustomerDetailGetByAccountResponse> customerResponse = customerQueryProvider.getCustomerDetailByAccount(customerRequest);
        if (ObjectUtils.isEmpty(customerResponse.getContext())) {
            return BaseResponse.error("用户登录信息已失效，请重新登录");
        }
        CustomerDetailVO customerDetailVO = customerResponse.getContext();
        String imAccount = getStoreId() + "_"+ commonUtil.getOperator().getAccount();
        request.setCustomerServiceAccount(imAccount);
//        request.setCustomerServiceName("临时会话");
        request.setUserLogo(null);
        onlineServiceSaveProvider.addImAccount(request);
        return BaseResponse.success(imAccount);
    }
    private Long getStoreId () {
        try {
            return commonUtil.getStoreId();
        }
        catch (Exception e) {

        }
        return 0l;
    }
    /**
     * 根据商家ID查询当前商户的客服（发送人聊天对象）
     *
     * @param request 商家Id
     * @return 客服账号信息（暂时只设置一个，后期可能很多）
     */
    @ApiOperation(value = "查询当前商户的客服")
    @RequestMapping(value = {"/tencentImDetail"}, method = RequestMethod.POST)
    public BaseResponse<ImOnlineServiceListResponse> tencentImDetail(@RequestBody @Valid ImOnlineServiceSignRequest request) {
        return onlineServiceQueryProvider.imList(OnlineServiceListRequest.builder().storeId(request.getStoreId()).build());
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
        //记录操作日志
        operateLogMQUtil.convertAndSend("腾讯IM", "删除会话", "删除会话：客服账号" + (Objects.nonNull(signRequest) ? signRequest.getCustomerServiceAccount() : ""));
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

    @RequestMapping(value = {"/testPushMsg"}, method = RequestMethod.POST)
    public BaseResponse testPushMsg (@RequestBody ImOnlineServiceSignRequest requestParam) {
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
        String appKey= jsonObject.getString("appKey");
        long appId= jsonObject.getLong("appId");
        TencentImCustomerUtil.sendSingleChat(requestParam.getCustomerServiceAccount(), "", appId, appKey);
        return BaseResponse.success("");
    }



}
