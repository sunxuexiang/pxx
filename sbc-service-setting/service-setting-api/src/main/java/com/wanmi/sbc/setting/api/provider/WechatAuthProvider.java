package com.wanmi.sbc.setting.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.DistributionMiniProgramRequest;
import com.wanmi.sbc.setting.api.request.MiniProgramQrCodeRequest;
import com.wanmi.sbc.setting.api.request.MiniProgramSetRequest;
import com.wanmi.sbc.setting.api.request.ShareMiniProgramRequest;
import com.wanmi.sbc.setting.api.response.MiniProgramSetGetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by feitingting on 2018/12/28.
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "WechatAuthProvider")
public interface WechatAuthProvider {
    /**
     * 获取小程序码
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/wechat-auth/get-wxa-code-unlimit")
    BaseResponse<String> getWxaCodeUnlimit(@RequestBody MiniProgramQrCodeRequest request);

    @PostMapping("/setting/${application.setting.version}/wechat-auth/get-mini-program-set")
    BaseResponse<MiniProgramSetGetResponse> getMiniProgramSet();

    @PostMapping("/setting/${application.setting.version}/wechat-auth/update-mini-program-set")
    BaseResponse updateMiniProgramSet(@RequestBody MiniProgramSetRequest request);

    @PostMapping("/setting/${application.setting.version}/wechat-auth/distribution/miniProgram")
    BaseResponse<String> distributionMiniProgram(@RequestBody DistributionMiniProgramRequest request);

    @PostMapping("/wechat-auth/shareuserid/miniProgram")
    BaseResponse<String> getMiniProgramQrCodeWithShareUserId(@RequestBody ShareMiniProgramRequest request);
}