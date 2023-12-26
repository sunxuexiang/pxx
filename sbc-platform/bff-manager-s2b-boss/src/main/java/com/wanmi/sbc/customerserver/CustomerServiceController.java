package com.wanmi.sbc.customerserver;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CustomerServiceType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceSaveProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigSaveProvider;
import com.wanmi.sbc.setting.api.request.ConfigContextModifyByTypeAndKeyRequest;
import com.wanmi.sbc.setting.api.request.SobotConfigModifyRequest;
import com.wanmi.sbc.setting.api.request.SobotContextRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.*;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceByIdRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.ImConfigRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Api(description = "客服API", tags = "CustomerServiceController")
@RestController
@RequestMapping("/customerService")
public class CustomerServiceController {


    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;

    @Autowired
    private OnlineServiceSaveProvider onlineServiceSaveProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private SystemConfigSaveProvider systemConfigSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 查询qq客服配置明细
     *
     * @return
     */
    @ApiOperation(value = "查询qq客服配置明细")
    @ApiImplicitParam(paramType = "path", dataType = "Long",
            name = "storeId", value = "店铺id", required = true)
    @RequestMapping(value = {"/qq/detail/{storeId}"}, method = RequestMethod.GET)
    public BaseResponse<OnlineServiceListResponse> qqDetail(@PathVariable Long storeId) {
//    public BaseResponse<QQOnline> qqDetail(@PathVariable Long storeId) {
//        QQOnline response = qqServerService.qqDetail(storeId);
//        return BaseResponse.success(response);

        return onlineServiceQueryProvider.list(OnlineServiceListRequest.builder().storeId(storeId).build());

    }

    /**
     * 查询qq客服开关
     *
     * @return
     */
    @ApiOperation(value = "查询qq客服开关")
    @ApiImplicitParam(paramType = "path", dataType = "Long",
            name = "storeId", value = "店铺id", required = true)
    @RequestMapping(value = {"/qq/switch/{storeId}"}, method = RequestMethod.GET)
    public BaseResponse<ConfigResponse> qqSwitch(@PathVariable Long storeId) {
        OnlineServiceByIdResponse onlineServiceByIdResponse = onlineServiceQueryProvider.getById(
                OnlineServiceByIdRequest.builder().storeId(storeId).build()).getContext();

        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.ALIYUN_ONLINE_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO = new SystemConfigVO();
        if (!response.getSystemConfigVOList().isEmpty()){
            systemConfigVO = response.getSystemConfigVOList().get(0);
        }
        SystemConfigResponse sobotRespnose =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.SOBOT_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO sobotConfigVO = new SystemConfigVO();
        if (!sobotRespnose.getSystemConfigVOList().isEmpty()){
            sobotConfigVO = sobotRespnose.getSystemConfigVOList().get(0);
        }
        //运营端IM开关
        SystemConfigResponse imRespnose =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO imConfigVO = new SystemConfigVO();
        if (!imRespnose.getSystemConfigVOList().isEmpty()){
            imConfigVO = imRespnose.getSystemConfigVOList().get(0);
        }
        // 改造 bff
        return BaseResponse.success(ConfigResponse.builder().onlineServiceVO(onlineServiceByIdResponse.getOnlineServiceVO())
                .imSystemConfigVO(systemConfigVO).imSystemConfigVO(imConfigVO)
                .systemConfigVO(systemConfigVO).sobotConfigVO(sobotConfigVO).build());
    }

    /**
     * 保存qq客服配置明细
     *
     * @return
     */
    @ApiOperation(value = "保存qq客服配置明细")
    @RequestMapping(value = {"/qq/saveDetail"}, method = RequestMethod.POST)
    public BaseResponse qqSaveDetail(@RequestBody OnlineServiceModifyRequest ropRequest) {
//    public BaseResponse qqSaveDetail(@RequestBody QQOnline ropRequest) {
//        BaseResponse baseResponse = qqServerService.qqSaveDetail(ropRequest);

//        return baseResponse;
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.ALIYUN_ONLINE_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        if (!response.getSystemConfigVOList().isEmpty()){
            SystemConfigVO systemConfigVO = response.getSystemConfigVOList().get(0);
            if (ropRequest.getQqOnlineServerRop().getServerStatus().toValue()== 1 && systemConfigVO.getStatus() == 1){
                return BaseResponse.FAILED();
            }
        }
        operateLogMQUtil.convertAndSend("设置", "编辑在线客服", "编辑在线客服");

        return onlineServiceSaveProvider.modify(ropRequest);

    }

    /**
     *  查询阿里云客服配置
     * @return
     */
    @ApiOperation(value = "查询阿里云客服配置")
    @PostMapping("/aliyun/detail")
    public BaseResponse queryAliyun(){
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                .configType(ConfigType.ALIYUN_ONLINE_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        if (!response.getSystemConfigVOList().isEmpty()){
            return BaseResponse.success(response.getSystemConfigVOList().get(0));
        }
        return BaseResponse.FAILED();
    }

    @ApiOperation(value = "修改阿里云客服配置")
    @PostMapping("/aliyun/modify")
    public BaseResponse modifyAliyun(@RequestBody ConfigContextModifyByTypeAndKeyRequest request){

        // 不知道为什么有店铺ID。目前都是写死的0
        OnlineServiceByIdResponse onlineServiceByIdResponse = onlineServiceQueryProvider.getById(
                OnlineServiceByIdRequest.builder().storeId(0L).build()).getContext();
        if (Objects.nonNull(onlineServiceByIdResponse) && onlineServiceByIdResponse.getOnlineServiceVO()
                .getServerStatus().toValue() == 1 && request.getStatus() == 1){
            return BaseResponse.FAILED();
        }
        request.setConfigKey(ConfigKey.ONLINESERVICE);
        request.setConfigType(ConfigType.ALIYUN_ONLINE_SERVICE);
        systemConfigSaveProvider.modify(request);
        operateLogMQUtil.convertAndSend("设置", "修改阿里云客服配置", "修改阿里云客服配置");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "修改智齿客服配置")
    @PostMapping("/sobot/modify")
    public BaseResponse modifySobot(@RequestBody SobotConfigModifyRequest request) {
        OnlineServiceByIdResponse onlineServiceByIdResponse = onlineServiceQueryProvider.getById(
                OnlineServiceByIdRequest.builder().storeId(0L).build()).getContext();
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.ALIYUN_ONLINE_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        if (request.getEnableFlag()==1){
            boolean qqFlag=Objects.nonNull(onlineServiceByIdResponse) && onlineServiceByIdResponse.getOnlineServiceVO()
                    .getServerStatus().toValue() == 1;
            boolean sobotFlag=Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getSystemConfigVOList())
                    && response.getSystemConfigVOList().get(0).getStatus() == 1;
            if (qqFlag||sobotFlag){
                return BaseResponse.FAILED();
            }
        }

        if (request.getEnableFlag() == 1) {
            if (request.getEffectiveApp() == 0 && request.getEffectiveH5() == 0
                    && request.getEffectiveMiniProgram() == 0 && request.getEffectivePc() == 0) {
                return BaseResponse.FAILED();
            }
        }
        SobotContextRequest convert = KsBeanUtil.convert(request, SobotContextRequest.class);
        if (StringUtils.isBlank(convert.getH5Url())){
            convert.setH5Url("");
        }
        ConfigContextModifyByTypeAndKeyRequest configRequest = new ConfigContextModifyByTypeAndKeyRequest();
        configRequest.setConfigKey(ConfigKey.ONLINESERVICE);
        configRequest.setConfigType(ConfigType.SOBOT_ONLIEN_SERVICE);
        configRequest.setStatus(request.getEnableFlag());
        configRequest.setContext(JSONObject.toJSONString(convert));
        systemConfigSaveProvider.modify(configRequest);
        operateLogMQUtil.convertAndSend("设置", "修改智齿客服配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }



    @ApiOperation(value = "修改腾讯IM客服配置")
    @PostMapping("/tencentIm/modify")
    public BaseResponse modifyTencentIm(@RequestBody ImConfigModifyRequest request) {
        OnlineServiceByIdResponse onlineServiceByIdResponse = onlineServiceQueryProvider.getById(
                OnlineServiceByIdRequest.builder().storeId(0L).build()).getContext();
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        if (request.getEnableFlag()==1){
            boolean imFlag=Objects.nonNull(onlineServiceByIdResponse) && onlineServiceByIdResponse.getOnlineServiceVO()
                    .getServerStatus().toValue() == 1;
            boolean sobotFlag=Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getSystemConfigVOList())
                    && response.getSystemConfigVOList().get(0).getStatus() == 1;
            if (imFlag||sobotFlag){
                return BaseResponse.FAILED();
            }
        }

        if (request.getEnableFlag() == 1) {
            if (request.getEffectiveApp() == 0 && request.getEffectiveH5() == 0
                    && request.getEffectiveMiniProgram() == 0 && request.getEffectivePc() == 0) {
                return BaseResponse.FAILED();
            }
        }

        ImConfigModifyRequest convert = KsBeanUtil.convert(request, ImConfigModifyRequest.class);
        ConfigContextModifyByTypeAndKeyRequest configRequest = new ConfigContextModifyByTypeAndKeyRequest();
        configRequest.setConfigKey(ConfigKey.ONLINESERVICE);
        configRequest.setConfigType(ConfigType.TX_IM_ONLIEN_SERVICE);
        configRequest.setStatus(request.getEnableFlag());
        configRequest.setContext(JSONObject.toJSONString(convert));
        systemConfigSaveProvider.imModify(configRequest);
        operateLogMQUtil.convertAndSend("设置", "修改腾讯IM客服配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 智齿客服开关修改接口
     * @return
     */
    @RequestMapping(path = "/sobot/switch", method = RequestMethod.POST)
    public BaseResponse updateSobotSwitch (@RequestBody SobotServiceRequest request) {
        CustomerServiceSwitchUpdateRequest updateRequest = CustomerServiceSwitchUpdateRequest.builder()
                .customerServiceType(CustomerServiceType.SOBOT)
                .companyId(commonUtil.getCompanyInfoId())
                .switchStatus(request.getSwitchStatus()).build();
        onlineServiceSaveProvider.updateCustomerServiceSwitch(updateRequest);
        operateLogMQUtil.convertAndSend("设置", "智齿客服开关修改", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

}
