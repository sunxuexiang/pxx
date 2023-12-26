package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceSettingProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingContentVo;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceSettingResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChat;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceSetting;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceSettingService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.onlineservice.service.OnlineServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>客服服务设置OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Slf4j
@RestController
public class CustomerServiceSettingController implements CustomerServiceSettingProvider {

    @Autowired
    private CustomerServiceSettingService customerServiceSettingService;

    @Autowired
    private CustomerServiceChatService customerServiceChatService;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private OnlineServiceService onlineServiceService;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;

    @Override
    public BaseResponse saveCustomerServiceSetting(List<CustomerServiceSettingRequest> settingRequest) {
        List<CustomerServiceSetting> customerServiceSetting = customerServiceSettingService.saveCustomerServiceSetting(settingRequest);
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse getCustomerServiceSettingList(CustomerServiceSettingRequest settingRequest) {
        List<CustomerServiceSettingResponse> settingList = customerServiceSettingService.getCustomerServiceSettingList(settingRequest);
        return BaseResponse.success(settingList);
    }

//    @PostConstruct
//    private void init () {
//        customerServiceTimeoutOneTask(2);
//        customerServiceTimeoutOneTask(5);
//        new Thread () {
//            @Override
//            public void run() {
//                customerReplyTimeout(ImSettingTypeEnum.Close.getType());
//                try {
//                    Thread.sleep(60000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }.start();
//    }

    /**
     * 客服超时回复设置处理
     * @param settingType
     * @return
     */
    @Override
    public BaseResponse customerServiceTimeoutOneTask(Integer settingType) {
        log.info("客服超时说辞开始执行 {}", settingType);

        CustomerServiceSetting customerServiceSetting = customerServiceSettingService.getByCompanyInfoIdAndSettingType(0l, settingType);
        if (customerServiceSetting == null) {
            log.info("客服超时说辞{}没有配置", settingType);
            return BaseResponse.success("");
        }
        CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
        if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
                && settingContent.getTime() != null && settingContent.getTime() > 0) {
            log.info("客服超时说辞{}没有开启 {} - {}", settingType, customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
            return BaseResponse.success("");
        }

        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        int startIndex = 0;
        int pageSize = 500;
        while (true) {
            List<CustomerServiceChat> chatList = customerServiceChatService.getReplyTimeoutChatListByPage(settingContent.getTime() * 60, 0, pageSize);
            if (ObjectUtils.isEmpty(chatList)) {
                log.info("客服超时说辞{},没有超时聊天", settingType);
                break;
            }
            log.info("客服超时说辞 查询数量 {}", chatList.size());
            chatList.forEach(chat -> {
                log.info("客服超时说辞", JSON.toJSONString(chat));
                ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCustomerServiceAccount(chat.getServerAccount());
                String nickName = chat.getStoreName();
                if (imOnlineServiceItem != null) {
                    nickName = imOnlineServiceItem.getCustomerServiceName();
                }
                TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(),
                        settingContent.getMessage(), appKey, appId, settingType, nickName, chat.getCustomerImAccount());

                chat.setTimeoutState(ImSettingTypeEnum.TimeoutTwo.getType().equals(settingType) ? 2 : 1);
                customerServiceChatService.saveChat(chat);
            });

//            List<CustomerServiceSetting> settingList = customerServiceSettingService.getCustomerServiceSettingListByPage(settingType, startIndex, pageSize);
//            for (CustomerServiceSetting customerServiceSetting : settingList) {
//                if (StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
//                    continue;
//                }
//                CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
//                if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
//                        && settingContent.getTime() != null && settingContent.getTime() > 0) {
//                    log.info("客服超时说辞{}没有开启 {} - {}", settingType, customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
//                    continue;
//                }
//                List<CustomerServiceChat> chatList = customerServiceChatService.getReplyTimeoutChatList(customerServiceSetting.getCompanyInfoId(),
//                        settingContent.getTime() * 60, 0);
//                if (ObjectUtils.isEmpty(chatList)) {
//                    log.info("客服超时说辞{}, 公司 {} 没有超时聊天", settingType, customerServiceSetting.getCompanyInfoId());
//                    continue;
//                }
//                chatList.forEach(chat -> {
//
//                    TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(), settingContent.getMessage(), appKey, appId, settingType);
//
//                    chat.setTimeoutState(ImSettingTypeEnum.TimeoutTwo.getType().equals(settingType) ? 2 : 1);
//                    customerServiceChatService.saveChat(chat);
//                });
//            }
//            if (settingList.size() < pageSize) {
//                break;
//            }
//            startIndex += pageSize;
        }
        return BaseResponse.success("");
    }

//    @Override
//    public BaseResponse customerServiceTimeoutOneTask(Integer settingType) {
//        log.info("客服超时说辞开始执行 {}", settingType);
//        // 获取腾讯IM 账号配置
//        SystemConfigResponse systemConfigResponse =
//                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
//                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
//        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
//        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
//        long appId= Long.valueOf(jsonObject.getString("appId"));
//        String appKey= jsonObject.getString("appKey");
//        int startIndex = 0;
//        int pageSize = 100;
//        while (true) {
//            List<CustomerServiceSetting> settingList = customerServiceSettingService.getCustomerServiceSettingListByPage(settingType, startIndex, pageSize);
//            for (CustomerServiceSetting customerServiceSetting : settingList) {
//                if (StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
//                    continue;
//                }
//                CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
//                if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
//                        && settingContent.getTime() != null && settingContent.getTime() > 0) {
//                    log.info("客服超时说辞{}没有开启 {} - {}", settingType, customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
//                    continue;
//                }
//                List<CustomerServiceChat> chatList = customerServiceChatService.getReplyTimeoutChatList(customerServiceSetting.getCompanyInfoId(),
//                     settingContent.getTime() * 60, 0);
//                if (ObjectUtils.isEmpty(chatList)) {
//                    log.info("客服超时说辞{}, 公司 {} 没有超时聊天", settingType, customerServiceSetting.getCompanyInfoId());
//                    continue;
//                }
//                chatList.forEach(chat -> {
//
//                    TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(), settingContent.getMessage(), appKey, appId, settingType);
//
//                    chat.setTimeoutState(ImSettingTypeEnum.TimeoutTwo.getType().equals(settingType) ? 2 : 1);
//                    customerServiceChatService.saveChat(chat);
//                });
//            }
//            if (settingList.size() < pageSize) {
//                break;
//            }
//            startIndex += pageSize;
//        }
//        return BaseResponse.success("");
//    }

//    /**
//     * 客户超时回复设置处理
//     * @param settingType
//     * @return
//     */
//    @Override
//    public BaseResponse customerReplyTimeout(Integer settingType) {
//        log.info("客户未回复关闭会话任务开始 {}", settingType);
//        // 获取腾讯IM 账号配置
//        SystemConfigResponse systemConfigResponse =
//                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
//                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
//        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
//        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
//        long appId= Long.valueOf(jsonObject.getString("appId"));
//        String appKey= jsonObject.getString("appKey");
//
//        int startIndex = 0;
//        int pageSize = 100;
//        while (true) {
//            List<CustomerServiceSetting> settingList = customerServiceSettingService.getCustomerServiceSettingListByPage(settingType, startIndex, pageSize);
//            for (CustomerServiceSetting customerServiceSetting : settingList) {
//                if (StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
//                    continue;
//                }
//                CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
//                if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
//                        && settingContent.getTime() != null && settingContent.getTime() > 0) {
//                    log.info("客户超时下线提示，没有开启 {} - {}", customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
//                    continue;
//                }
//                List<CustomerServiceChat> chatList = customerServiceChatService.getReplyTimeoutFinishChatList(customerServiceSetting.getCompanyInfoId(), settingContent.getTime() * 60);
//                if (ObjectUtils.isEmpty(chatList)) {
//                    log.info("客户超时下线提示, 公司 {} 没有下线聊天", customerServiceSetting.getCompanyInfoId());
//                    continue;
//                }
//                chatList.forEach(chat -> {
//                    log.info("客户超时下线提示item {}", JSON.toJSONString(chat));
//                    TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(), settingContent.getMessage(), appKey, appId, settingType);
//                    List<String> accountList = Arrays.asList(chat.getServerAccount());
//                    TencentImCustomerUtil.deleteGroupMember(chat.getImGroupId(), accountList, appKey, appId);
//                    TencentImCustomerUtil.sendCustomMsg(chat.getServerAccount(), "更新聊天会话列表", TencentImMessageType.UpdateChatList, appId, appKey);
//                    chat.setChatState(2);
//                    chat.setSendState(1);
//                    customerServiceChatService.saveChat(chat);
//                });
//            }
//            if (settingList.size() < pageSize) {
//                break;
//            }
//            startIndex += pageSize;
//        }
//        return BaseResponse.success("");
//    }

    /**
     * 客户超时回复设置处理
     * @param settingType
     * @return
     */
    @Override
    public BaseResponse customerReplyTimeout(Integer settingType) {
        log.info("客户未回复关闭会话任务开始 {}", settingType);
        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");

        CustomerServiceSetting offlineCustomerServiceSetting = customerServiceSettingService.getByCompanyInfoIdAndSettingType(0l, settingType);
        if (offlineCustomerServiceSetting == null) {
            return BaseResponse.success("");
        }
        CustomerServiceSettingContentVo settingContent = JSON.parseObject(offlineCustomerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
        if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
                && settingContent.getTime() != null && settingContent.getTime() > 0) {
            log.info("客户未回复关闭会话任务，没有开启 {} - {}", offlineCustomerServiceSetting.getCompanyInfoId(), offlineCustomerServiceSetting.getContentJson());
            return BaseResponse.success("");
        }

        int pageSize = 500;
        while (true) {
            List<CustomerServiceChat> chatList = customerServiceChatService.getReplyTimeoutFinishChatListByPage(settingContent.getTime() * 60, pageSize);
            if (ObjectUtils.isEmpty(chatList) || chatList.size() < pageSize) {
                List<CustomerServiceChat> offlineChatList = customerServiceChatService.getTimeoutOffliceChat(settingContent.getTime() * 60, pageSize);
                if (!ObjectUtils.isEmpty(offlineChatList)) {
                    chatList.addAll(offlineChatList);
                }
            }
            if (ObjectUtils.isEmpty(chatList)) {
                log.info("客户未回复关闭会话任务 为空");
                break;
            }
            log.info("客户未回复关闭会话任务 数量 {}", chatList.size());
            chatList.forEach(chat -> {
                log.info("客户未回复关闭会话任务详情 {}", JSON.toJSONString(chat));

                ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCustomerServiceAccount(chat.getServerAccount());
                String nickName = chat.getStoreName();
                if (imOnlineServiceItem != null) {
                    nickName = imOnlineServiceItem.getCustomerServiceName();
                }
                TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(),
                        settingContent.getMessage(), appKey, appId, settingType, nickName, chat.getCustomerImAccount());
//                List<String> accountList = Arrays.asList(chat.getServerAccount());

                List<String> accountList = TencentImCustomerUtil.getGroupMemberList(chat.getImGroupId(), appKey, appId);
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                if (!accountList.contains(chat.getServerAccount())) {
                    accountList.add(chat.getServerAccount());
                }
                accountList.remove(chat.getCustomerImAccount());
                TencentImCustomerUtil.deleteGroupMember(chat.getImGroupId(), accountList, appKey, appId);
                TencentImCustomerUtil.sendCustomMsg(chat.getServerAccount(), "更新聊天会话列表", TencentImMessageType.UpdateChatList, appId, appKey);
                chat.setChatState(2);
                chat.setSendState(1);
                chat.setEndTime(System.currentTimeMillis() / 1000l);
                if (chat.getStartTime() != null) {
                    chat.setChatDuration(chat.getEndTime() - chat.getStartTime());
                }
                else {
                    chat.setChatDuration(0l);
                    chat.setStartTime(0l);
                }
                customerServiceChatService.saveChat(chat);

                ImChatRequest imChatRequest = ImChatRequest.builder().companyId(chat.getCompanyInfoId()).appId(appId).appKey(appKey).build();
                customerServiceChatService.processQueue(imChatRequest);
            });
            List<Long> companyIdList = chatList.stream().map(CustomerServiceChat::getCompanyInfoId).distinct().collect(Collectors.toList());
            companyIdList.forEach(companyId -> {
                onlineServiceService.pushWebUpdateNotify(companyId, appKey, appId, TencentImMessageType.UpdateChatList);
            });
        }
        return BaseResponse.success("");
    }

//    @Override
//    public BaseResponse userTimeoutTask(Integer settingType) {
//        log.info("客户超时说辞开始执行 {}", settingType);
//        // 获取腾讯IM 账号配置
//        SystemConfigResponse systemConfigResponse =
//                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
//                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
//        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
//        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
//        long appId= Long.valueOf(jsonObject.getString("appId"));
//        String appKey= jsonObject.getString("appKey");
//        int startIndex = 0;
//        int pageSize = 100;
//        while (true) {
//            List<CustomerServiceSetting> settingList = customerServiceSettingService.getCustomerServiceSettingListByPage(settingType, startIndex, pageSize);
//            for (CustomerServiceSetting customerServiceSetting : settingList) {
//                if (StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
//                    continue;
//                }
//                CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
//                if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
//                        && settingContent.getTime() != null && settingContent.getTime() > 0) {
//                    log.info("客户超时说辞{}没有开启 {} - {}", settingType, customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
//                    continue;
//                }
//                List<CustomerServiceChat> chatList = customerServiceChatService.getUserTimeoutChatList(customerServiceSetting.getCompanyInfoId(),settingContent.getTime() * 60);
//                if (ObjectUtils.isEmpty(chatList)) {
//                    log.info("客户超时说辞{}, 公司 {} 没有超时聊天", settingType, customerServiceSetting.getCompanyInfoId());
//                    continue;
//                }
//                chatList.forEach(chat -> {
//                    TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(), settingContent.getMessage(), appKey, appId, settingType);
//                    chat.setUserTimeoutState(1);
//                    customerServiceChatService.saveChat(chat);
//                });
//            }
//            if (settingList.size() < pageSize) {
//                break;
//            }
//            startIndex += pageSize;
//        }
//        return BaseResponse.success("");
//    }

    @Override
    public BaseResponse userTimeoutTask(Integer settingType) {
        log.info("客户超时说辞开始执行 {}", settingType);

        CustomerServiceSetting customerServiceSetting = customerServiceSettingService.getByCompanyInfoIdAndSettingType(0l, settingType);
        if (ObjectUtils.isEmpty(customerServiceSetting)) {
            log.info("客户超时说辞{}没有配置 {} - {}", settingType);
            return BaseResponse.success("");
        }
        CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
        if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
                && settingContent.getTime() != null && settingContent.getTime() > 0) {
            log.info("客户超时说辞{}没有开启 {} - {}", settingType, customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
            return BaseResponse.success("");
        }

        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");


        int startIndex = 0;
        int pageSize = 500;
        while (true) {
            List<CustomerServiceChat> chatList = customerServiceChatService.getUserTimeoutChatListByPage(settingContent.getTime() * 60, pageSize);
            if (ObjectUtils.isEmpty(chatList)) {
                log.info("客户超时说辞， 没有超时聊天", settingType, customerServiceSetting.getCompanyInfoId());
                break;
            }
            log.info("客户超时说辞 数量 {}", chatList.size());
            chatList.forEach(chat -> {
                log.info("客户超时说辞 详情 {}", JSON.toJSONString(chat));
                ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCustomerServiceAccount(chat.getServerAccount());
                String nickName = chat.getStoreName();
                if (imOnlineServiceItem != null) {
                    nickName = imOnlineServiceItem.getCustomerServiceName();
                }
                TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(),
                        settingContent.getMessage(), appKey, appId, settingType, nickName, chat.getCustomerImAccount());
                chat.setUserTimeoutState(1);
                customerServiceChatService.saveChat(chat);
            });

//            List<CustomerServiceSetting> settingList = customerServiceSettingService.getCustomerServiceSettingListByPage(settingType, startIndex, pageSize);
//            for (CustomerServiceSetting customerServiceSetting : settingList) {
//                if (StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
//                    continue;
//                }
//                CustomerServiceSettingContentVo settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
//                if (!settingContent.isSwitchStatus() && !StringUtils.isEmpty(settingContent.getMessage())
//                        && settingContent.getTime() != null && settingContent.getTime() > 0) {
//                    log.info("客户超时说辞{}没有开启 {} - {}", settingType, customerServiceSetting.getCompanyInfoId(), customerServiceSetting.getContentJson());
//                    continue;
//                }
//                List<CustomerServiceChat> chatList = customerServiceChatService.getUserTimeoutChatList(customerServiceSetting.getCompanyInfoId(),settingContent.getTime() * 60);
//                if (ObjectUtils.isEmpty(chatList)) {
//                    log.info("客户超时说辞{}, 公司 {} 没有超时聊天", settingType, customerServiceSetting.getCompanyInfoId());
//                    continue;
//                }
//                chatList.forEach(chat -> {
//                    TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(), settingContent.getMessage(), appKey, appId, settingType);
//                    chat.setUserTimeoutState(1);
//                    customerServiceChatService.saveChat(chat);
//                });
//            }
//            if (settingList.size() < pageSize) {
//                break;
//            }
//            startIndex += pageSize;
        }
        return BaseResponse.success("");
    }
}
