package com.wanmi.sbc.third.share;

//import com.rop.client.CompositeResponse;
//import com.wanmi.open.sdk.SdkClient;
//import com.wanmi.open.sdk.request.WechatShareSetSaveRopRequest;
//import com.wanmi.open.sdk.response.WechatShareSetRopResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.setting.api.provider.wechatshareset.WechatShareSetQueryProvider;
import com.wanmi.sbc.setting.api.provider.wechatshareset.WechatShareSetSaveProvider;
import com.wanmi.sbc.setting.api.request.wechatshareset.WechatShareSetAddRequest;
import com.wanmi.sbc.setting.api.request.wechatshareset.WechatShareSetInfoRequest;
import com.wanmi.sbc.setting.api.response.wechatshareset.WechatShareSetInfoResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: songhanlin
 * @Date: Created In 10:57 AM 2018/8/13
 * @Description: 微信分享Controller
 */
@Api(tags="WechatShareController", description = "微信分享")
@RestController
@RequestMapping("/third/share/wechat")
public class WechatShareController {
//    @Autowired
//    private SdkClient sdkClient;

    @Autowired
    private WechatShareSetQueryProvider wechatShareSetQueryProvider;

    @Autowired
    private WechatShareSetSaveProvider wechatShareSetSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询详情
     * @return
     */
    @ApiOperation(value = "查询详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public BaseResponse<WechatShareSetInfoResponse> findOne() {
//    public BaseResponse<WechatShareSetRopResponse> findOne() {
//        CompositeResponse<WechatShareSetRopResponse> response = sdkClient.buildClientRequest().get
//                (WechatShareSetRopResponse.class, "wechat.share.findOne", "1.0.0");
//        return BaseResponse.success(response.getSuccessResponse());

        return wechatShareSetQueryProvider.getInfo(WechatShareSetInfoRequest.builder().
                operatePerson(commonUtil.getOperatorId()).build());
    }

    /**
     * 修改
     * @param request
     * @return
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/save", method = RequestMethod.PUT)
    public BaseResponse save(@RequestBody WechatShareSetAddRequest request) {
//    public BaseResponse save(@RequestBody WechatShareSetSaveRopRequest request) {
        if (StringUtils.isBlank(request.getShareAppId()) || request.getShareAppId().length() > 50) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (StringUtils.isBlank(request.getShareAppSecret()) || request.getShareAppSecret().length() > 50) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setOperatePerson(commonUtil.getOperatorId());
//        sdkClient.buildClientRequest().post(request, WechatShareSetRopResponse.class, "wechat.share.save", "1.0.0");
        operateLogMQUtil.convertAndSend("设置", "编辑分享接口", "编辑分享接口");
        request.setStoreId(Constants.BOSS_DEFAULT_STORE_ID);
        return wechatShareSetSaveProvider.add(request);
    }
}
