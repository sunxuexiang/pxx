package com.wanmi.sbc.setting.api.provider.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatSearchRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceChatResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.CustomerServiceInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>客服聊天服务OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "CustomerServiceChatProvider")
public interface CustomerServiceChatProvider {

    @PostMapping("/setting/${application.setting.version}/serviceChat/getManagerList")
    BaseResponse<List<CustomerServiceInfoResponse>> getCustomerServiceManagerList(@RequestBody Long companyInfo);

    @PostMapping("/setting/${application.setting.version}/serviceChat/getAppChatUserInfo")
    BaseResponse<CustomerServiceChatResponse> getAppChatUserInfo(@RequestBody CustomerServiceChatRequest request);

    @PostMapping("/setting/${application.setting.version}/serviceChat/getChatListByGroupId")
    BaseResponse<List<CustomerServiceChatResponse>> getChatListByGroupId(@RequestBody List<String> groupList);

    @PostMapping("/setting/${application.setting.version}/serviceChat/getOfflineList")
    BaseResponse getOfflineList(@RequestBody CustomerServiceChatRequest request);

    @PostMapping("/setting/${application.setting.version}/serviceChat/getClosedChatListByGroupId")
    BaseResponse<MicroServicePage<CustomerServiceChatResponse>> getClosedChatListByGroupId(@RequestBody CustomerServiceChatSearchRequest customerServiceChatSearchRequest);

    @PostMapping("/setting/${application.setting.version}/serviceChat/closeChat")
    BaseResponse closeChat(@RequestBody ImChatRequest imChatRequest);

    @PostMapping("/setting/${application.setting.version}/serviceChat/getQueueChatList")
    BaseResponse getQueueChatList(@RequestBody ImChatRequest imChatRequest);

    @PostMapping("/setting/${application.setting.version}/serviceChat/processChatQueueTask")
    BaseResponse processChatQueueTask();

    @PostMapping("/setting/${application.setting.version}/serviceChat/getChatDetailInfo")
    BaseResponse<CustomerServiceChatResponse> getChatDetailInfo(@RequestBody CustomerServiceChatRequest request);

    @PostMapping("/setting/${application.setting.version}/serviceChat/createChat")
    BaseResponse createChat(@RequestBody ImChatRequest request);

    @PostMapping("/setting/${application.setting.version}/serviceChat/leaveMessage")
    BaseResponse leaveMessage(@RequestBody ImChatRequest request);

    @PostMapping("/setting/${application.setting.version}/serviceChat/getGroupIdListByCompanyId")
    BaseResponse<List<String>> getGroupIdListByCompanyId(@RequestBody Long companyId);


    @PostMapping("/setting/${application.setting.version}/serviceChat/getAllGroupChat")
    BaseResponse<List<CustomerServiceChatResponse>> getAllGroupChat();
}
