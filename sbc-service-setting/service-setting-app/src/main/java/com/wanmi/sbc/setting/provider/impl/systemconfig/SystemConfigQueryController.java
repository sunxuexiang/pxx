package com.wanmi.sbc.setting.provider.impl.systemconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceDelMsgRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceUnReadRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineSignRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.ImConfigRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceSignResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.*;
import com.wanmi.sbc.setting.config.Config;
import com.wanmi.sbc.setting.config.ConfigService;
import com.wanmi.sbc.setting.redis.RedisService;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.wanmi.sbc.common.util.Constants.no;

/**
 * Created by feitingting on 2019/11/6.
 */
@Slf4j
@RestController
public class SystemConfigQueryController implements SystemConfigQueryProvider {

    @Autowired
    ConfigService configService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    final Long expireTime = 60*24L;
    @Override
    public BaseResponse<SystemConfigResponse> findByConfigKeyAndDelFlag(@RequestBody @Valid ConfigQueryRequest request){
        List<Config> configList = configService.findByConfigKeyAndDelFlag(request.getConfigKey(), DeleteFlag.NO);
        if (CollectionUtils.isEmpty(configList)){
            return BaseResponse.success(new SystemConfigResponse());
        }
        List<ConfigVO> configVOList = KsBeanUtil.convert(configList, ConfigVO.class);
        return BaseResponse.success(new SystemConfigResponse(configVOList, null));
    }

    @Override
    public BaseResponse<SystemConfigTypeResponse> findByConfigTypeAndDelFlag(@RequestBody @Valid ConfigQueryRequest request) {
        return BaseResponse.success(configService.findByConfigTypeAndDelFlag(request.getConfigType(),DeleteFlag.NO));
    }

    @Override
    public BaseResponse<LogisticsRopResponse> findKuaiDiConfig(@RequestBody ConfigQueryRequest request){
        return BaseResponse.success(configService.findKuaiDiConfig(request.getConfigType(),DeleteFlag.NO));
    }

    @Override
    public BaseResponse<SystemConfigResponse> list(@RequestBody @Valid SystemConfigQueryRequest request) {
        List<SystemConfig> systemConfigList = systemConfigService.list(request);
        List<SystemConfigVO> systemConfigVOList =
                systemConfigList.stream().map(systemConfig -> systemConfigService.wrapperVo(systemConfig)).collect(Collectors.toList());
        return BaseResponse.success(new SystemConfigResponse(null, systemConfigVOList));
    }



    @Override
    public BaseResponse<String> imSign( @RequestBody @Valid ImConfigRequest request) {
        return BaseResponse.success(TencentImCustomerUtil.getTxCloudUserSig(request.getAccount(),request.getAppid(),request.getKey()));
    }
    @Override
    public BaseResponse<String> getSign(@RequestBody  @Valid ImOnlineSignRequest account) {

        if (null==account || null ==account.getCustomerServiceAccount()){
            return BaseResponse.error("没有签名账号");
        }
        String key=CacheKeyConstant.EVALUATE_RATIO + SpecialSymbols.UNDERLINE.toValue()+ account.getCustomerServiceAccount();
       String o = redisService.getString(key);
      if (null==o){
            ImConfigRequest request =new ImConfigRequest();
            SystemConfigResponse response =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
            JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
            String appKey= jsonObject.getString("appKey");
            long appId= jsonObject.getLong("appId");
            request.setAccount(account.getCustomerServiceAccount());
            request.setKey(appKey);
            request.setAppid(appId);
            BaseResponse<String> stringBaseResponse = this.imSign(request);
            redisService.setString(key,stringBaseResponse.getContext(),expireTime);
            return BaseResponse.success(stringBaseResponse.getContext());
      }
     return BaseResponse.success(o);
    }
    @Override
    public BaseResponse<RecentContactVO> tencentImGetList(@RequestBody  @Valid ImOnlineServiceSignRequest imOnlineServiceSignRequest) {
        ImOnlineServiceSignResponse signAndAppidAndKey = getSignAndAppidAndKey(ImOnlineServiceSignRequest.builder().customerServiceAccount(imOnlineServiceSignRequest.getCustomerServiceAccount()).build());

        String s = TencentImCustomerUtil.tencentImGetList(imOnlineServiceSignRequest.getCustomerServiceAccount(), signAndAppidAndKey.getSign(), signAndAppidAndKey.getAppid());
        RecentContactVO recentContactVO =  JSONObject.toJavaObject(JSON.parseObject(s),RecentContactVO.class);
        String userInfo =  TencentImCustomerUtil.tencentImGetUserInfo(imOnlineServiceSignRequest.getCustomerServiceAccount(), signAndAppidAndKey.getSign(), signAndAppidAndKey.getAppid());
        RecentUserInfoVO vo =  JSONObject.toJavaObject(JSON.parseObject(userInfo),RecentUserInfoVO.class);
        //暂时只有一个
        recentContactVO.setProfileItem(vo.getUserProfileItem().get(no).getProfileItem());
        return BaseResponse.success(recentContactVO);

    }

    @Override
    public BaseResponse<String> tencentImDelMsg(@RequestBody  @Valid ImOnlineServiceDelMsgRequest imOnlineServiceSignRequest) {
        ImOnlineServiceSignResponse signAndAppidAndKey = getSignAndAppidAndKey(ImOnlineServiceSignRequest.builder().customerServiceAccount(imOnlineServiceSignRequest.getCustomerServiceAccount()).build());

        String s = TencentImCustomerUtil.tencentImDelMsg(imOnlineServiceSignRequest.getCustomerServiceAccount(),imOnlineServiceSignRequest.getType(), signAndAppidAndKey.getSign(), signAndAppidAndKey.getAppid(),signAndAppidAndKey.getKey());
        DelMsgVO vo =  JSONObject.toJavaObject(JSON.parseObject(s), DelMsgVO.class);
        if (no.equals(vo.getErrorCode())){
            return BaseResponse.success(Constants.yes.toString());
        }else {
            return BaseResponse.error(vo.getErrorInfo());
        }
    }

    @Override
    public BaseResponse<UnreadMsgNumVO> unreadMsgNum( @RequestBody  @Valid ImOnlineServiceUnReadRequest imOnlineServiceSignRequest) {
        ImOnlineServiceSignResponse signAndAppidAndKey = getSignAndAppidAndKey(ImOnlineServiceSignRequest.builder().customerServiceAccount(imOnlineServiceSignRequest.getCustomerServiceAccount()).build());
        String s = TencentImCustomerUtil.unReadMsgNum(imOnlineServiceSignRequest.getCustomerServiceAccount(),imOnlineServiceSignRequest.getUserId(), signAndAppidAndKey.getSign(), signAndAppidAndKey.getAppid(), signAndAppidAndKey.getKey() );
        UnreadMsgNumVO vo =  JSONObject.toJavaObject(JSON.parseObject(s), UnreadMsgNumVO.class);
        if (vo.getErrorCode()== no){
            return BaseResponse.success(vo);
        }else{
            return BaseResponse.error(vo.getErrorInfo());
        }
    }
    @Override
    public BaseResponse<UnreadMsgNumVO> merchantUnreadMsgNum(@RequestBody  @Valid ImOnlineServiceSignRequest imOnlineServiceSignRequest) {
        ImOnlineServiceSignResponse signAndAppidAndKey = getSignAndAppidAndKey(imOnlineServiceSignRequest);
        String s = TencentImCustomerUtil.merchantUnReadMsgNum(imOnlineServiceSignRequest.getCustomerServiceAccount(), signAndAppidAndKey.getSign(), signAndAppidAndKey.getAppid(),signAndAppidAndKey.getKey());
        UnreadMsgNumVO vo =  JSONObject.toJavaObject(JSON.parseObject(s), UnreadMsgNumVO.class);
        if (vo.getErrorCode()== no){
            return BaseResponse.success(vo);
        }else{
            return BaseResponse.error(vo.getErrorInfo());
        }
    }


    public ImOnlineServiceSignResponse getSignAndAppidAndKey(ImOnlineServiceSignRequest imOnlineServiceSignRequest){
        ImOnlineServiceSignResponse signResponse=new ImOnlineServiceSignResponse();
         String key=CacheKeyConstant.EVALUATE_RATIO + SpecialSymbols.UNDERLINE.toValue()+ imOnlineServiceSignRequest.getCustomerServiceAccount();
         String sign = redisService.getString(key);
         SystemConfigResponse response =
                 systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                         .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
         JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
         long appId=jsonObject.getLong("appId");
         String appKey= jsonObject.getString("appKey");
         if (null==sign){
             BaseResponse<String> stringBaseResponse =
                     this.imSign(ImConfigRequest.builder().account(imOnlineServiceSignRequest.getCustomerServiceAccount()).appid(appId).key(appKey).build());
             sign=stringBaseResponse.getContext();
         }
        signResponse.setSign(sign);
        signResponse.setAppid(appId);
        signResponse.setKey(appKey);
        return signResponse;
    }
}

