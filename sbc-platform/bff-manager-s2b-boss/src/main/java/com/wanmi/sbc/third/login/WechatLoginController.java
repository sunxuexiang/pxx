package com.wanmi.sbc.third.login;

//import com.rop.client.CompositeResponse;
//import com.wanmi.open.sdk.SdkClient;
//import com.wanmi.open.sdk.request.WechatLoginSetSaveRopRequest;
//import com.wanmi.open.sdk.response.WechatLoginSetRopResponse;
//import com.wanmi.open.sdk.response.base.BaseRopResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.setting.api.provider.wechatloginset.WechatLoginSetQueryProvider;
import com.wanmi.sbc.setting.api.provider.wechatloginset.WechatLoginSetSaveProvider;
import com.wanmi.sbc.setting.api.request.wechatloginset.WechatLoginSetAddRequest;
import com.wanmi.sbc.setting.api.response.wechatloginset.WechatLoginSetResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Api(tags = "WechatLoginController", description = "微信授信登录")
@RestController
@RequestMapping("/third/login/wechat")
public class WechatLoginController {

//    @Autowired
//    private SdkClient sdkClient;

    @Autowired
    private WechatLoginSetQueryProvider wechatLoginSetQueryProvider;

    @Autowired
    private WechatLoginSetSaveProvider wechatLoginSetSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 获取微信授信配置
     *
     * @return
     */
    @ApiOperation(value = "获取微信授信配置")
    @RequestMapping(value = "/set/detail", method = RequestMethod.GET)
    public BaseResponse<WechatLoginSetResponse> getWechatLoginSetDetail() {
//    public BaseResponse<WechatLoginSetRopResponse> getWechatLoginSetDetail(){
//        CompositeResponse<WechatLoginSetRopResponse> response =
//                sdkClient.buildClientRequest().get(WechatLoginSetRopResponse.class, "wechat.login.querySetDetail", "1.0.0");
//        return BaseResponse.success(response.getSuccessResponse());
        return wechatLoginSetQueryProvider.getInfo();
    }


    /**
     * 保存授信配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "保存授信配置")
    @RequestMapping(value = "/set", method = RequestMethod.PUT)
    public BaseResponse saveWechatLoginSet(@RequestBody WechatLoginSetAddRequest request) {
//    public BaseResponse saveWechatLoginSet(@RequestBody WechatLoginSetSaveRopRequest request) {
        if (request.getAppServerStatus() == null || request.getMobileServerStatus() == null || request.getPcServerStatus() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        } else {
            if (Objects.equals(DefaultFlag.YES, request.getMobileServerStatus())) {
                if (request.getMobileAppId() == null || request.getMobileAppSecret() == null) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            }
            if (Objects.equals(DefaultFlag.YES, request.getPcServerStatus())) {
                if (request.getPcAppId() == null || request.getPcAppSecret() == null) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            }
            if (request.getMobileAppId() != null && request.getMobileAppId().length() > 50) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            if (request.getMobileAppSecret() != null && request.getMobileAppSecret().length() > 50) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            if (request.getPcAppId() != null && request.getPcAppId().length() > 50) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            if (request.getPcAppSecret() != null && request.getPcAppSecret().length() > 50) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }

        }

        request.setOperatePerson(commonUtil.getOperatorId());
        request.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);

//        CompositeResponse<BaseRopResponse> response =
//                sdkClient.buildClientRequest().post(request, BaseRopResponse.class, "wechat.login.saveLoginSet", "1.0.0");
//        if (response.getSuccessResponse() != null) {
//
//            operateLogMQUtil.convertAndSend("设置", "编辑登录接口", "编辑登录接口");
//            return BaseResponse.success(response.getSuccessResponse());
//        } else {
//            return BaseResponse.FAILED();
//        }

        operateLogMQUtil.convertAndSend("设置", "编辑登录接口", "编辑登录接口");

        return wechatLoginSetSaveProvider.add(request);
    }
}
