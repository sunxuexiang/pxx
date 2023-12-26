package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>客服登录信息OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-09-7 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceLoginProvider")
public interface CustomerServiceLoginProvider {

    @PostMapping("/setting/${application.setting.version}/customerAccount/loginSuccess")
    BaseResponse loginSuccess(@RequestBody ImOnlineServiceSignRequest request);

    @PostMapping("/setting/${application.setting.version}/customerAccount/getStatus")
    BaseResponse getStatus(@RequestBody ImOnlineServiceSignRequest request);

    @PostMapping("/setting/${application.setting.version}/customerAccount/getImAccountLoginState")
    BaseResponse getImAccountLoginState(@RequestBody ImOnlineServiceSignRequest request);
}
