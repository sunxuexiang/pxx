package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceTodayMessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>腾讯IM回调接口OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "TencentIMNotifyProvider")
public interface TencentIMNotifyProvider {

    @PostMapping("/setting/${application.setting.version}/tencentIMNotify/sendMsg")
    BaseResponse<List<CustomerServiceTodayMessageResponse>> chatMsgNotify (@RequestBody JSONObject jsonParam);
}
