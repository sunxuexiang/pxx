package com.wanmi.sbc.system;

import com.alibaba.fastjson.JSONObject;
import com.alipay.fc.csplatform.common.crypto.Base64Util;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailGetCustomerIdResponse;
import com.wanmi.sbc.setting.api.provider.baseconfig.BaseConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.businessconfig.BusinessConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.baseconfig.BaseConfigRopResponse;
import com.wanmi.sbc.setting.api.response.businessconfig.BusinessConfigRopResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.system.request.OnlineServiceUrlRequest;
import com.wanmi.sbc.system.response.SobotResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Objects;

/**
 * 基本设置服务
 * Created by CHENLI on 2017/5/12.
 */
@Api(tags = "SystemController", description = "基本设置 API")
@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private BaseConfigQueryProvider baseConfigQueryProvider;

    @Autowired
    private BusinessConfigQueryProvider businessConfigQueryProvider;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    /**
     * 查询基本设置
     */
    @ApiOperation(value = "查询基本设置")
//    @Cacheable(value = "BASE_CONFIG")
    @RequestMapping(value = "/baseConfig", method = RequestMethod.GET)
    public BaseResponse<BaseConfigRopResponse> findBaseConfig() {
        return baseConfigQueryProvider.getBaseConfig();
//        CompositeResponse<BaseConfigRopResponse> response =  sdkClient.buildClientRequest()
//                .get(BaseConfigRopResponse.class, "baseConfig.query", "1.0.0");
//        return BaseResponse.success( response.getSuccessResponse());
    }

    /**
     * 查询招商页设置
     * @return
     */
    @ApiOperation(value = "查询招商页设置")
    @Cacheable(value = "BUSINESS_CONFIG")
    @RequestMapping(value = "/businessConfig", method = RequestMethod.GET)
    public BaseResponse<BusinessConfigRopResponse> findConfig() {
        return businessConfigQueryProvider.getInfo();
//        CompositeResponse<BusinessConfigRopResponse> response =  sdkClient.buildClientRequest()
//                .get(BusinessConfigRopResponse.class, "businessConfig.query", "1.0.0");
//        return BaseResponse.success( response.getSuccessResponse() );
    }

    /**
     * 获取服务时间
     * @return
     */
    @ApiOperation(value = "获取服务时间")
    @RequestMapping(value = "/queryServerTime", method = RequestMethod.GET)
    public long queryServerTime() {
        return System.currentTimeMillis();
    }

    /**
     *  查询阿里云客服配置
     * @return
     */
    @ApiOperation(value = "查询阿里云客服配置")
    @PostMapping("/aliyun/detail")
    public BaseResponse queryAliyun(@RequestBody OnlineServiceUrlRequest onlineServiceUrlRequest){
        String url = "";
        try {
            SystemConfigResponse response =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.ALIYUN_ONLINE_SERVICE.toValue()).delFlag(DeleteFlag.NO).status(1).build()).getContext();
            if (!response.getSystemConfigVOList().isEmpty()){
                String context = response.getSystemConfigVOList().get(0).getContext();

                Map obj = JSONObject.parseObject(context);

                //根据用户id查询用户的信息
                CustomerDetailGetCustomerIdResponse customer = customerDetailQueryProvider.getCustomerDetailByCustomerId(
                        CustomerDetailByCustomerIdRequest.builder().customerId(onlineServiceUrlRequest.getCustomerId()).build())
                        .getContext();
                if (customer.getCustomerId() == null) {
                    return BaseResponse.error("客户不存在！");
                }
                // 还原公钥
                PublicKey publicKey = getPubKey(obj.get("key").toString());
                // 封装请求体
                JSONObject extInfo = new JSONObject();
                extInfo.put("userId", onlineServiceUrlRequest.getCustomerId());
                extInfo.put("userName",onlineServiceUrlRequest.getCustomerName());
                JSONObject cinfo = new JSONObject();
                cinfo.put("userId", onlineServiceUrlRequest.getCustomerId());
                cinfo.put("extInfo", extInfo);
                Map<String, String> map = CustomerInfoCryptoUtil.encryptByPublicKey(cinfo.toString(), publicKey);
                String params = "&key=" + map.get("key") + "&cinfo=" + map.get("text");

                String aliyunChat = obj.get("aliyunChat").toString();
                url = aliyunChat.concat(params);
            }
        } catch (Exception e){
            return BaseResponse.FAILED();
        }
        return BaseResponse.success(url);
    }

    /**
     *  查询智齿客服配置
     * @return
     */
    @ApiOperation(value = "查询智齿客服配置")
    @GetMapping("/sobot/detail")
    public BaseResponse<SobotResponse> querySobot(){
        SystemConfigResponse sobotRespnose =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.SOBOT_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO sobotConfigVO = new SystemConfigVO();
        if (!sobotRespnose.getSystemConfigVOList().isEmpty()){
            sobotConfigVO = sobotRespnose.getSystemConfigVOList().get(0);
        }
        JSONObject jsonObject = JSONObject.parseObject(sobotConfigVO.getContext());
        if (Objects.isNull(jsonObject)|| StringUtils.isBlank(jsonObject.getString("h5Url"))){
            return null;
        }
        SobotResponse response=new SobotResponse();
        response.setEffectiveApp(Objects.nonNull(jsonObject.getInteger("effectiveApp"))?jsonObject.getInteger("effectiveApp"):0);
        response.setEffectiveH5(Objects.nonNull(jsonObject.getInteger("effectiveH5"))?jsonObject.getInteger("effectiveH5"):0);
        response.setEffectiveMiniProgram(Objects.nonNull(jsonObject.getInteger("effectiveMiniProgram"))?jsonObject.getInteger("effectiveMiniProgram"):0);
        response.setEffectivePc(Objects.nonNull(jsonObject.getInteger("effectivePc"))?jsonObject.getInteger("effectivePc"):0);
        response.setStatus(sobotConfigVO.getStatus());
        response.setH5Url(jsonObject.getString("h5Url"));
        return BaseResponse.success(response);
    }

    /**
     *  查询腾讯IM客服配置
     * @return
     */
    @ApiOperation(value = "查询腾讯IM客服配置")
    @GetMapping("/tencentIm/detail")
    public BaseResponse<SobotResponse> queryTencentIm(){
        SystemConfigResponse sobotRespnose =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO sobotConfigVO = new SystemConfigVO();
        if (!sobotRespnose.getSystemConfigVOList().isEmpty()){
            sobotConfigVO = sobotRespnose.getSystemConfigVOList().get(0);
        }
        JSONObject jsonObject = JSONObject.parseObject(sobotConfigVO.getContext());

        SobotResponse response=new SobotResponse();
        response.setEffectiveApp(Objects.nonNull(jsonObject.getInteger("effectiveApp"))?jsonObject.getInteger("effectiveApp"):0);
        response.setEffectiveH5(Objects.nonNull(jsonObject.getInteger("effectiveH5"))?jsonObject.getInteger("effectiveH5"):0);
        response.setEffectiveMiniProgram(Objects.nonNull(jsonObject.getInteger("effectiveMiniProgram"))?jsonObject.getInteger("effectiveMiniProgram"):0);
        response.setEffectivePc(Objects.nonNull(jsonObject.getInteger("effectivePc"))?jsonObject.getInteger("effectivePc"):0);
        response.setStatus(sobotConfigVO.getStatus());
        return BaseResponse.success(response);
    }


    private PublicKey getPubKey(String pubKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Util.decode(pubKey));
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        return key;
    }

    /**
     * 查询版本升级配置
     */
    /**
     * 查询基本设置
     */
    @ApiOperation(value = "查询基本设置")
    @RequestMapping(value = "/upgradeConfig", method = RequestMethod.POST)
    public BaseResponse<SystemConfigResponse> findupgradeConfig() {
        ConfigQueryRequest request = new ConfigQueryRequest();
        request.setDelFlag(0);
        request.setConfigKey("upgrade_config");
        request.setConfigType("version_upgrade_config");
        return systemConfigQueryProvider.findByConfigKeyAndDelFlag(request);
    }
}