package com.wanmi.sbc.setting.api.provider.systemconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceDelMsgRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceUnReadRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineSignRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.ImConfigRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigTypeResponse;
import com.wanmi.sbc.setting.bean.vo.RecentContactVO;
import com.wanmi.sbc.setting.bean.vo.UnreadMsgNumVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * Created by feitingting on 2019/11/6.
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "SystemConfigQueryProvider")
public interface SystemConfigQueryProvider {

    @PostMapping("/setting/${application.setting.version}/sysconfig/list")
    BaseResponse<SystemConfigResponse> findByConfigKeyAndDelFlag(@RequestBody @Valid ConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysconfig/find-by-type")
    BaseResponse<SystemConfigTypeResponse> findByConfigTypeAndDelFlag(@RequestBody @Valid ConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysconfig/find-kuaidi-config")
    BaseResponse<LogisticsRopResponse> findKuaiDiConfig (@RequestBody @Valid ConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysconfig/find-list")
    BaseResponse<SystemConfigResponse> list (@RequestBody @Valid SystemConfigQueryRequest request);

    @PostMapping("/setting/${application.setting.version}/sysconfig/im-sign")
    BaseResponse<String> imSign (@RequestBody @Valid ImConfigRequest request);
    @PostMapping("/setting/${application.setting.version}/sysconfig/get-sign")
    BaseResponse<String> getSign(@RequestBody   @Valid ImOnlineSignRequest customerServiceAccount);
    @PostMapping("/setting/${application.setting.version}/sysconfig/get-list")
    BaseResponse<RecentContactVO> tencentImGetList( @RequestBody @Valid ImOnlineServiceSignRequest signRequest);
    @PostMapping("/setting/${application.setting.version}/sysconfig/del_msg")
    BaseResponse<String> tencentImDelMsg( @RequestBody @Valid ImOnlineServiceDelMsgRequest signRequest);
    @PostMapping("/setting/${application.setting.version}/sysconfig/unread_msg_num")
    BaseResponse<UnreadMsgNumVO> unreadMsgNum( @RequestBody @Valid ImOnlineServiceUnReadRequest signRequest);
    @PostMapping("/setting/${application.setting.version}/sysconfig/merchant_unread_msg_num")
    BaseResponse<UnreadMsgNumVO> merchantUnreadMsgNum( @RequestBody @Valid ImOnlineServiceSignRequest signRequest);
}
