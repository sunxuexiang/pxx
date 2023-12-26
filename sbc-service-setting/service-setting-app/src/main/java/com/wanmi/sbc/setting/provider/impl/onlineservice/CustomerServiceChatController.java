package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatSearchRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingContentVo;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceChatResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.CustomerServiceInfoResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChat;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceSetting;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceSettingService;
import com.wanmi.sbc.setting.onlineservice.service.OnlineServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>客服聊天OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-09-6 16:10:28
 */
@RestController
@Validated
@Slf4j
public class CustomerServiceChatController implements CustomerServiceChatProvider {

    @Autowired
    private CustomerServiceChatService customerServiceChatService;

    @Autowired
    private CustomerServiceSettingService customerServiceSettingService;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private OnlineServiceService onlineServiceService;

    @Override
    public BaseResponse<List<CustomerServiceInfoResponse>> getCustomerServiceManagerList(Long companyInfo) {
        List<CustomerServiceInfoResponse> accountList = customerServiceChatService.getCustomerServiceManagerList(companyInfo);
        return BaseResponse.success(accountList);
    }

    @Override
    public BaseResponse<CustomerServiceChatResponse> getAppChatUserInfo(CustomerServiceChatRequest request) {
        CustomerServiceChatResponse chatResponse = customerServiceChatService.getChatUserInfoByGroup(request);
        return BaseResponse.success(chatResponse);
    }

    @Override
    public BaseResponse<List<CustomerServiceChatResponse>> getChatListByGroupId(List<String> groupList) {
        List<CustomerServiceChatResponse> chatList = customerServiceChatService.getChatListByGroupId(groupList);
        return BaseResponse.success(chatList);
    }

    @Override
    public BaseResponse getOfflineList(CustomerServiceChatRequest request) {
        List<CustomerServiceChatResponse> chatList = customerServiceChatService.getOfflineList(request);
        return BaseResponse.success(chatList);
    }

    @Override
    public BaseResponse<MicroServicePage<CustomerServiceChatResponse>> getClosedChatListByGroupId(CustomerServiceChatSearchRequest customerServiceChatSearchRequest) {
        MicroServicePage<CustomerServiceChatResponse> chatList = customerServiceChatService.getClosedChatListByGroupId(customerServiceChatSearchRequest);
        return BaseResponse.success(chatList);
    }

    @Override
    public BaseResponse closeChat(ImChatRequest imChatRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("手动结束会话 请求参数 {}", JSON.toJSONString(imChatRequest));
        CustomerServiceSetting customerServiceSetting = customerServiceSettingService.getByCompanyInfoIdAndSettingType(0l, imChatRequest.getSettingType());
        if (customerServiceSetting != null) {
            CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
            imChatRequest.setSettingContent(settingContent);
        }
        customerServiceChatService.closeChat(imChatRequest);
        stopWatch.stop();
        log.info("手动结束会话总耗时 {}", stopWatch.getTotalTimeSeconds());
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse getQueueChatList(ImChatRequest imChatRequest) {
        MicroServicePage<CustomerServiceChatResponse> microServicePage = customerServiceChatService.getQueueChatList(imChatRequest);
        return BaseResponse.success(microServicePage);
    }

    @Override
    public BaseResponse processChatQueueTask() {
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        List<Long> companyIdList = customerServiceChatService.getQueueCompanyIdList();
        companyIdList.forEach(companyId -> {
            try {
                ImChatRequest request = ImChatRequest.builder().companyId(companyId).appKey(appKey).appId(appId).build();
                customerServiceChatService.processQueue(request);
            }
            catch (Exception e) {
                log.error("定时任务处理排队聊天异常", e);
            }
        });
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse<CustomerServiceChatResponse> getChatDetailInfo(CustomerServiceChatRequest request) {
        CustomerServiceChatResponse chatResponse = customerServiceChatService.getChatUserInfoByGroup(request);
        return BaseResponse.success(chatResponse);
    }

    @Override
    public BaseResponse createChat(ImChatRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String message = onlineServiceService.createChat(request);
        stopWatch.stop();
        log.info("客服重建会话总耗时 {}", stopWatch.getTotalTimeSeconds());
        if (!StringUtils.isEmpty(message)) {
            throw new SbcRuntimeException(message);
        }
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse leaveMessage(ImChatRequest request) {
        String message = customerServiceChatService.leaveMessage(request);
        if (!StringUtils.isEmpty(message)) {
            throw new SbcRuntimeException(message);
        }
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse<List<String>> getGroupIdListByCompanyId(Long companyId) {
        List<String> groupIdList = customerServiceChatService.getGroupIdListByCompanyId(companyId);
        return BaseResponse.success(groupIdList);
    }

    @Override
    public BaseResponse<List<CustomerServiceChatResponse>> getAllGroupChat() {
        List<CustomerServiceChatResponse> chatList = customerServiceChatService.getAllGroupChat();
        return BaseResponse.success(chatList);
    }

}
