package com.wanmi.sbc.setting.imonlineservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ImChatStateEnum;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatSearchRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingContentVo;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceChatResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.CustomerServiceInfoResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceChatRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceLoginRecordRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceSettingRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChat;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChatMark;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceLoginRecord;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceSetting;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.onlineservice.service.OnlineServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>客服服务聊天服务类</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Slf4j
@Service
public class CustomerServiceChatService {

    @Autowired
    private CustomerServiceChatRepository customerServiceChatRepository;

    @Autowired
    private CustomerServiceSettingRepository customerServiceSettingRepository;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;

    @Autowired
    private CustomerServiceLoginRecordRepository customerServiceLoginRecordRepository;

    @Lazy
    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;

    @Autowired
    private CustomerServiceSettingService customerServiceSettingService;

    @Autowired
    private OnlineServiceService onlineServiceService;

    @Autowired
    private CustomerServiceChatMarkService customerServiceChatMarkService;

    private ThreadPoolExecutor threadPoolExecutor;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void initMethod () {
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(16);
        threadPoolExecutor = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, blockingQueue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public List<CustomerServiceChat> getReplyTimeoutChatList(Long companyInfoId, Integer timeoutSeconds, Integer timeoutState) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        log.info("客服超时时间 {} - {}", (System.currentTimeMillis() / 1000), lastMsgTime);
        return customerServiceChatRepository.getReplyTimeoutChatList(companyInfoId, 0, 0, lastMsgTime, timeoutState);
    }

    public List<CustomerServiceChat> getReplyTimeoutChatListByPage(Integer timeoutSeconds, Integer timeoutState, Integer pageSize) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        log.info("客服超时时间 {} - {}", (System.currentTimeMillis() / 1000), lastMsgTime);
        return customerServiceChatRepository.getReplyTimeoutChatListByPage(0, 0, lastMsgTime, timeoutState, pageSize);
    }

    public void sendGroupSetting (ImOnlineServiceItem imOnlineServiceItem, String groupId, String fromAccount, ImSettingTypeEnum settingTypeEnum, String storeName,
                                  Long sourceStoreId, String customerIMAccount) {
        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");

        sendGroupSetting(imOnlineServiceItem, groupId, fromAccount, settingTypeEnum, appKey, appId, storeName, sourceStoreId, customerIMAccount);
    }

    public void sendGroupSetting (ImOnlineServiceItem imOnlineServiceItem, String groupId, String fromAccount, ImSettingTypeEnum settingTypeEnum, String appKey, Long appId,
                                  String storeName, Long sourceStoreId, String customerIMAccount) {
        sendGroupSetting(0l, imOnlineServiceItem, groupId, fromAccount, settingTypeEnum, appKey, appId, storeName, sourceStoreId, customerIMAccount);
    }

    public void sendGroupSetting (Long companyInfoId, ImOnlineServiceItem imOnlineServiceItem, String groupId, String fromAccount, ImSettingTypeEnum settingTypeEnum,
                                  String appKey, Long appId, String storeName, Long sourceStoreId, String customerIMAccount) {
        sendGroupSetting(companyInfoId, imOnlineServiceItem, groupId, fromAccount, settingTypeEnum, appKey, appId, storeName, sourceStoreId, customerIMAccount, null, null);
    }

    public void sendGroupSetting (Long companyInfoId, ImOnlineServiceItem imOnlineServiceItem, String groupId, String fromAccount, ImSettingTypeEnum settingTypeEnum,
                                  String appKey, Long appId, String storeName, Long sourceStoreId, String customerIMAccount,
                                  String switchSourceAccount, String switchSourceAccountNick) {
        if (imOnlineServiceItem == null || StringUtils.isEmpty(groupId)) {
            return;
        }

        CustomerServiceSetting offlineCustomerServiceSetting = customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(companyInfoId, settingTypeEnum.getType());
        log.info("发送群组自动回复语 公司ID {} settingType {} - {}", companyInfoId, settingTypeEnum.getType(), offlineCustomerServiceSetting);
        // 自动发送人工欢迎语
        if (offlineCustomerServiceSetting != null && !StringUtils.isEmpty(offlineCustomerServiceSetting.getContentJson())) {
            CustomerServiceSettingContentVo customerServiceSettingContentVo = JSON.parseObject(offlineCustomerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
            if (customerServiceSettingContentVo.isSwitchStatus() && !StringUtils.isEmpty(customerServiceSettingContentVo.getMessage())) {
                TencentImCustomerUtil.sendGroupMsg(groupId, fromAccount, imOnlineServiceItem.getCompanyInfoId(), sourceStoreId, storeName,
                        customerServiceSettingContentVo.getMessage(), appKey, appId, settingTypeEnum.getType(), imOnlineServiceItem.getCustomerServiceName(),
                        customerIMAccount, "system", switchSourceAccount, switchSourceAccountNick, null);
            }
        }
    }

    public void sendQueueSettingGroupMessage (Long companyInfoId, ImOnlineServiceItem imOnlineServiceItem, String groupId, String fromAccount,
                                              ImSettingTypeEnum settingTypeEnum, String appKey, Long appId, String storeName, Integer queue, Long sourceStoreId) {
        if (imOnlineServiceItem == null || StringUtils.isEmpty(groupId)) {
            return;
        }

        CustomerServiceSetting offlineCustomerServiceSetting = customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(companyInfoId, settingTypeEnum.getType());
        log.info("发送群组自动回复语 公司ID {} settingType {} - {}", companyInfoId, settingTypeEnum.getType(), offlineCustomerServiceSetting);
        // 自动发送人工欢迎语
        if (offlineCustomerServiceSetting != null && !StringUtils.isEmpty(offlineCustomerServiceSetting.getContentJson())) {
            CustomerServiceSettingContentVo customerServiceSettingContentVo = JSON.parseObject(offlineCustomerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
            if (customerServiceSettingContentVo.isSwitchStatus() && !StringUtils.isEmpty(customerServiceSettingContentVo.getMessage())) {
                TencentImCustomerUtil.sendGroupMsg(groupId, fromAccount, imOnlineServiceItem.getCompanyInfoId(), sourceStoreId, storeName,
                        customerServiceSettingContentVo.getMessage().replace("#人数#", queue+""), appKey, appId, settingTypeEnum.getType(), imOnlineServiceItem.getCustomerServiceName());
            }
        }
    }


    public void saveChat(CustomerServiceChat chat) {
        log.info("修改聊天记录 {} - {} - {} - {}", chat.getChatId(), chat.getImGroupId(), chat.getChatState(), chat.getSendState());
        customerServiceChatRepository.saveAndFlush(chat);
    }

    public void saveAllChat(List<CustomerServiceChat> chatList) {
        chatList.forEach(chat -> {
            log.info("修改聊天记录 {}", JSON.toJSONString(chat));
        });
        customerServiceChatRepository.saveAll(chatList);
    }

    public List<CustomerServiceChat> getReplyTimeoutFinishChatList(Long companyInfoId, Integer timeoutSeconds) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        log.info("客服超时时间 {} - {}", (System.currentTimeMillis() / 1000), lastMsgTime);
        return customerServiceChatRepository.getReplyTimeoutFinishChatList(companyInfoId, 0, 1, lastMsgTime);
    }

    public List<CustomerServiceChat> getReplyTimeoutFinishChatListByPage(Integer timeoutSeconds, Integer pageSize) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        log.info("客服超时时间 {} - {}", (System.currentTimeMillis() / 1000), lastMsgTime);
        return customerServiceChatRepository.getTimeoutFinishChatListByPage(0, 1, 0, lastMsgTime, pageSize);
    }

    public List<CustomerServiceInfoResponse> getCustomerServiceManagerList(Long companyInfo) {
        List<ImOnlineServiceItem> serviceItemList = imOnlineServiceItemService.getListByCompanyInfoId(companyInfo);
        List<CustomerServiceInfoResponse> resultList = new ArrayList<>();
        if (ObjectUtils.isEmpty(serviceItemList)) {
            return resultList;
        }
        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        List<String> accountList = serviceItemList.stream().map(ImOnlineServiceItem::getCustomerServiceAccount).collect(Collectors.toList());
        List<String> onlineAccount = TencentImCustomerUtil.getOnlineAccount(accountList, appId, appKey);

        List<CustomerServiceLoginRecord> loginRecordList = customerServiceLoginRecordRepository.findByCompanyInfo(companyInfo);
        List<Map<String, Object>> statisVoList = customerServiceChatRepository.countCompanyAcceptQuantity(companyInfo, 0, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serviceItemList.forEach(serviceItem -> {
            CustomerServiceInfoResponse response = KsBeanUtil.convert(serviceItem, CustomerServiceInfoResponse.class);
            if (onlineAccount.contains(serviceItem.getCustomerServiceAccount()) && serviceItem.getServiceStatus().equals(0)) {
                response.setStatus(0);
            }
            else {
                response.setStatus(1);
            }
            for (Map<String, Object> statisVo : statisVoList) {
                if (response.getCustomerServiceAccount().equals(statisVo.get("serverAccount"))) {
                    response.setAcceptQuantity((BigInteger) statisVo.get("quantity"));
                    break;
                }
            }
            for (CustomerServiceLoginRecord loginRecord : loginRecordList) {
                if (serviceItem.getCustomerServiceAccount().equals(loginRecord.getServerAccount())) {
                    response.setLoginTime(simpleDateFormat.format(new Date(loginRecord.getLoginTime() * 1000)));
                }
            }
            resultList.add(response);
        });
        return resultList;
    }

    public List<CustomerServiceChat> getUserTimeoutChatList(Long companyInfoId, Integer timeoutSeconds) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        log.info("客户超时时间 {} - {}", (System.currentTimeMillis() / 1000), lastMsgTime);
        return customerServiceChatRepository.getUserTimeoutChatList(companyInfoId, 0, 1, lastMsgTime, 0);
    }


    public List<CustomerServiceChat> getUserTimeoutChatListByPage(Integer timeoutSeconds, Integer pageSize) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        log.info("客户超时时间 {} - {}", (System.currentTimeMillis() / 1000), lastMsgTime);
        return customerServiceChatRepository.getUserTimeoutChatListByPage(0, 1, lastMsgTime, 0, pageSize);
    }

    public CustomerServiceChatResponse getChatUserInfoByGroup(CustomerServiceChatRequest request) {
        CustomerServiceChat chat = customerServiceChatRepository.findByImGroupId(request.getImGroupId());
        if (chat == null) {
            return new CustomerServiceChatResponse();
        }
        return KsBeanUtil.convert(chat, CustomerServiceChatResponse.class);
    }

    public List<CustomerServiceChatResponse> getChatListByGroupId(List<String> groupList) {
        if (ObjectUtils.isEmpty(groupList)) {
            return new ArrayList<>();
        }
        List<CustomerServiceChat> chatList = customerServiceChatRepository.findByImGroupIds(groupList);
        if (ObjectUtils.isEmpty(chatList)) {
            return new ArrayList<>();
        }

        List<ImOnlineServiceItem> imOnlineServiceItemList = imOnlineServiceItemService.getListByCompanyInfoId(chatList.get(0).getCompanyInfoId());
        List<CustomerServiceChatResponse> resultList = KsBeanUtil.convert(chatList, CustomerServiceChatResponse.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultList.forEach(chat -> {
            for (ImOnlineServiceItem imOnlineServiceItem : imOnlineServiceItemList) {
                if (chat.getServerAccount().equals(imOnlineServiceItem.getCustomerServiceAccount())) {
                    chat.setServerAccountName(imOnlineServiceItem.getCustomerServiceName());
                    break;
                }
            }
            if (chat.getMsgTime() != null) {
                chat.setCloseTime(simpleDateFormat.format(new Date(chat.getMsgTime() * 1000)));
            }
        });
        return resultList;
    }

    public List<CustomerServiceChatResponse> getOfflineList(CustomerServiceChatRequest request) {
        List<CustomerServiceChat> chatList = null;
        if (!StringUtils.isEmpty(request.getServerAccount())) {
            chatList = customerServiceChatRepository.findByCompanyInfoIdAndServerAccountAndChatStateAndServiceState(request.getCompanyInfoId(), request.getServerAccount(), 0, 1);
        }
        else {
            chatList = customerServiceChatRepository.findByCompanyInfoIdAndChatStateAndServiceState(request.getCompanyInfoId(), 0, 1);
        }
        List<CustomerServiceChatResponse> resultList = KsBeanUtil.convert(chatList, CustomerServiceChatResponse.class);
        return resultList;
    }

    /**
     * 客服下线，将当前聊天转接到在线的其他客服人员
     */
    public void customerServiceOffline(ImOnlineServiceItemVO request) {
        /**
         * 聊天中的会话处理，转接给其它在线客服或者是离线留言
         */
        List<CustomerServiceChat> chatList = customerServiceChatRepository.findByServerAccountAndChatState(request.getCustomerServiceAccount(), ImChatStateEnum.chatting.getState());
        if (ObjectUtils.isEmpty(chatList)) {
            return;
        }
        chatList.forEach(chat -> {
            chat.setChatState(3);
            chat.setEndTime(System.currentTimeMillis() / 1000);
            if (chat.getStartTime() != null && chat.getStartTime() > 0) {
                chat.setChatDuration(chat.getEndTime() - chat.getStartTime());
            }
            else {
                chat.setChatDuration(0l);
                chat.setStartTime(0l);
            }
            saveChat(chat);

            OnlineServiceListRequest onlineServiceListRequest = OnlineServiceListRequest.builder().storeId(chat.getCompanyInfoId()).build();
            onlineServiceListRequest.setAppVersion(chat.getAppVersion());
            onlineServiceListRequest.setCustomerImAccount(chat.getCustomerImAccount());
            onlineServiceListRequest.setAppPlatform(chat.getAppPlatform());
            onlineServiceListRequest.setAppSysModel(chat.getAppSysModel());
            onlineServiceListRequest.setUserId(chat.getCustomerId());
            onlineServiceListRequest.setServiceSwitchType(1);
            onlineServiceListRequest.setIpAddr(chat.getIpAddr());
            onlineServiceListRequest.setStoreName(chat.getStoreName());
            onlineServiceListRequest.setSourceStoreId(chat.getSourceStoreId());
            onlineServiceListRequest.setSourceCompanyId(chat.getSourceCompanyId());
            onlineServiceListRequest.setCompanyInfoId(chat.getCompanyInfoId());
            BaseResponse<ImOnlineServiceListResponse> result =  onlineServiceQueryProvider.getCompanyServiceAccount(onlineServiceListRequest);
            log.info("客户离线状态设置，转接在线客服相应 {}", JSON.toJSONString(result));
        });

//        threadPoolExecutor.execute(() -> {
//            SystemConfigResponse systemConfigResponse =
//                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
//                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
//            SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
//            JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
//            long appId= Long.valueOf(jsonObject.getString("appId"));
//            String appKey= jsonObject.getString("appKey");
//            onlineServiceService.pushWebUpdateNotify(request.getCompanyInfoId(), appKey, appId, TencentImMessageType.UpdateChatList);
//
//            /**
//             * 排队中的会话处理，推送手动结束会话语，并结束会话
//             */
//            List<CustomerServiceChat> queueChatList = customerServiceChatRepository.findByCompanyInfoIdAndChatState(request.getCompanyInfoId(), ImChatStateEnum.queue.getState());
//            if (ObjectUtils.isEmpty(queueChatList)) {
//                return;
//            }
//            CustomerServiceSetting customerServiceSetting = customerServiceSettingService.getByCompanyInfoIdAndSettingType(0l, ImSettingTypeEnum.HandClose.getType());
//            CustomerServiceSettingContentVo settingContent;
//            if (customerServiceSetting != null && !StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
//                settingContent = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
//            }
//            else {
//                settingContent = null;
//            }
//
//            queueChatList.forEach(chat -> {
//                log.info("客服离线 - 执行手动结束会话 {}", JSON.toJSONString(chat));
//
//                if (settingContent != null && settingContent.isSwitchStatus()) {
//                    TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(),
//                            settingContent.getMessage(), appKey, appId, ImSettingTypeEnum.HandClose.getType(), chat.getCustomerImAccount());
//                }
//                List<String> accountList = Arrays.asList(chat.getServerAccount());
//                TencentImCustomerUtil.deleteGroupMember(chat.getImGroupId(), accountList, appKey, appId);
//                TencentImCustomerUtil.sendCustomMsg(chat.getServerAccount(), "更新聊天会话列表", TencentImMessageType.UpdateChatList, appId, appKey);
//                chat.setChatState(3);
//                chat.setSendState(1);
//                saveChat(chat);
//            });
//            onlineServiceService.pushWebUpdateNotify(request.getCompanyInfoId(), appKey, appId, TencentImMessageType.ImChatQueue);
//        });
    }

    public MicroServicePage<CustomerServiceChatResponse> getClosedChatListByGroupId(CustomerServiceChatSearchRequest searchRequest) {
        MicroServicePage<CustomerServiceChatResponse> microServicePage = new MicroServicePage<>();
        List<CustomerServiceChatResponse> resultList = new ArrayList<>();
        microServicePage.setContent(resultList);
        if (ObjectUtils.isEmpty(searchRequest.getGroupIdList())) {
            return microServicePage;
        }

        // 查询正在聊天中的对话，并删除正在聊天中的对话
        List<String> chatingGroupIdList = customerServiceChatRepository.findChatingGroupIdList(searchRequest.getGroupIdList());
        log.info("查询今天结束会话群组 {} 正在聊天中的群组 {}", JSON.toJSONString(searchRequest.getGroupIdList()), JSON.toJSONString(chatingGroupIdList));
        searchRequest.getGroupIdList().removeAll(chatingGroupIdList);
        microServicePage.setTotal(searchRequest.getGroupIdList().size());
        if (ObjectUtils.isEmpty(searchRequest.getGroupIdList())) {
            return microServicePage;
        }

        int start = (searchRequest.getPageNum() - 1) * searchRequest.getPageSize();
        int end = start + searchRequest.getPageSize();
        List<String> searchGroupIdList = new ArrayList<>();
        for (int i=start; i<end && i<searchRequest.getGroupIdList().size(); i++) {
            searchGroupIdList.add(searchRequest.getGroupIdList().get(i));
        }

        Date todayStartTime = getTodayStartDate();
        Date todayEndTime = getTodayEndDate();
        List<CustomerServiceChat> chatList = null;
        if (StringUtils.isEmpty(searchRequest.getFromAccount())) {
            chatList = customerServiceChatRepository.findClosedChatByImGroupIds(searchGroupIdList, todayStartTime, todayEndTime);
        }
        else {
            chatList = customerServiceChatRepository.findClosedChatByImGroupIdsAndServerAccount(searchGroupIdList, searchRequest.getFromAccount(), todayStartTime, todayEndTime);
        }
        if (ObjectUtils.isEmpty(chatList)) {
            return microServicePage;
        }
        log.info("查询今天结束会话群组 {}", JSON.toJSONString(chatList.stream().map(CustomerServiceChat::getImGroupId).collect(Collectors.toList())));

        List<ImOnlineServiceItem> imOnlineServiceItemList = imOnlineServiceItemService.getListByCompanyInfoId(chatList.get(0).getCompanyInfoId());
        resultList = KsBeanUtil.convert(chatList, CustomerServiceChatResponse.class);
        microServicePage.setContent(resultList);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<CustomerServiceChatMark> markList = customerServiceChatMarkService.getTodayListByServerAccount(searchRequest.getFromAccount());
        resultList.forEach(chat -> {
            for (ImOnlineServiceItem imOnlineServiceItem : imOnlineServiceItemList) {
                if (chat.getServerAccount().equals(imOnlineServiceItem.getCustomerServiceAccount())) {
                    chat.setServerAccountName(imOnlineServiceItem.getCustomerServiceName());
                    break;
                }
            }
            if (chat.getCreateTime() != null) {
                chat.setCreateTimeSecond(chat.getCreateTime().toEpochSecond(ZoneOffset.of("+8")));
            }
            chat.setCloseTime(simpleDateFormat.format(new Date(chat.getMsgTime()*1000)));
            if (!StringUtils.isEmpty(chat.getImGroupId())) {
                markList.forEach(mark -> {
                    if (chat.getImGroupId().equals(mark.getImGroupId())) {
                        chat.setMarkState(1);
                    }
                });
            }
        });
        return microServicePage;
    }

    private Date getTodayStartDate () {
        String startDateStr = dateFormat.format(new Date());
        startDateStr = startDateStr + " 00:00:00";
        try {
            return dateTimeFormat.parse(startDateStr);
        }
        catch (Exception e) {}
        return new Date();
    }

    private Date getTodayEndDate () {
        String startDateStr = dateFormat.format(new Date());
        startDateStr = startDateStr + " 23:59:59";
        try {
            return dateTimeFormat.parse(startDateStr);
        }
        catch (Exception e) {}
        return new Date();
    }

    public void closeChat(ImChatRequest imChatRequest) {
        if (imChatRequest.getAppId() == null) {
            // 获取腾讯IM 账号配置
            SystemConfigResponse systemConfigResponse =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
            SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
            JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
            long appId= Long.valueOf(jsonObject.getString("appId"));
            String appKey= jsonObject.getString("appKey");
            imChatRequest.setAppId(appId);
            imChatRequest.setAppKey(appKey);
        }

        List<CustomerServiceChat> chatList = customerServiceChatRepository.findByImGroupIdAndChatState(imChatRequest.getImGroupId(), 0);
        if (ObjectUtils.isEmpty(chatList)) {
            List<String> accountList = TencentImCustomerUtil.getGroupMemberList(imChatRequest.getImGroupId(), imChatRequest.getAppKey(), imChatRequest.getAppId());
            log.info("手动结束会话 群成员 {}", accountList);
            if (ObjectUtils.isEmpty(accountList)) {
                return;
            }
            for (int i=0; i<accountList.size(); i++) {
                String account = accountList.get(i);
                if (!account.contains("_")) {
                    accountList.remove(i);
                    i--;
                }
            }
            TencentImCustomerUtil.deleteGroupMember(imChatRequest.getImGroupId(), accountList, imChatRequest.getAppKey(), imChatRequest.getAppId());
            onlineServiceService.pushWebUpdateNotify(imChatRequest.getCompanyId(), imChatRequest.getAppKey(), imChatRequest.getAppId(), TencentImMessageType.UpdateChatList);
            return;
        }


        List<String> memberList = new ArrayList<>();
        chatList.forEach(chat -> {
            log.info("手动结束会话 {}", JSON.toJSONString(chat));
            imChatRequest.setCompanyId(chat.getCompanyInfoId());
            if (imChatRequest.getSettingContent() != null && imChatRequest.getSettingContent().isSwitchStatus()) {
                TencentImCustomerUtil.sendGroupMsg(chat.getImGroupId(), chat.getServerAccount(), chat.getCompanyInfoId(), chat.getStoreId(), chat.getStoreName(),
                        imChatRequest.getSettingContent().getMessage(), imChatRequest.getAppKey(), imChatRequest.getAppId(), ImSettingTypeEnum.Close.getType(), chat.getCustomerImAccount());
            }
            memberList.add(chat.getServerAccount());
            chat.setChatState(3);
            chat.setSendState(1);
            chat.setEndTime(System.currentTimeMillis() / 1000);
            if (chat.getStartTime() != null && chat.getStartTime() > 0) {
                chat.setChatDuration(chat.getEndTime() - chat.getStartTime());
            }
            else {
                chat.setChatDuration(0l);
                chat.setStartTime(0l);
            }
            saveChat(chat);
        });
        CustomerServiceChat chat = chatList.get(0);
        TencentImCustomerUtil.sendCustomMsg(chat.getServerAccount(), "更新聊天会话列表", TencentImMessageType.UpdateChatList, imChatRequest.getAppId(), imChatRequest.getAppKey());
        TencentImCustomerUtil.deleteGroupMember(imChatRequest.getImGroupId(), memberList, imChatRequest.getAppKey(), imChatRequest.getAppId());


        threadPoolExecutor.execute(() -> {
            List<String> accountList = TencentImCustomerUtil.getGroupMemberList(chat.getImGroupId(), imChatRequest.getAppKey(), imChatRequest.getAppId());
            if (accountList == null) {
                accountList = new ArrayList<>();
            }
            accountList.addAll(memberList);
            accountList.remove(chat.getCustomerImAccount());
            TencentImCustomerUtil.deleteGroupMember(chat.getImGroupId(), accountList, imChatRequest.getAppKey(), imChatRequest.getAppId());
            onlineServiceService.pushWebUpdateNotify(imChatRequest.getCompanyId(), imChatRequest.getAppKey(), imChatRequest.getAppId(), TencentImMessageType.UpdateChatList, true);
            processQueue(imChatRequest);
        });
    }

    public void processQueue (ImChatRequest imChatRequest) {
        Long companyId = imChatRequest.getCompanyId();
        Integer limit = customerServiceSettingService.getCompanyAcceptQuantity(imChatRequest.getCompanyId());
        imChatRequest.setLimit(limit);
        List<CustomerServiceChat> chatList = customerServiceChatRepository.findByCompanyInfoIdAndChatState(companyId, ImChatStateEnum.queue.getState());
        log.info("排队聊天处理：公司 {} 正在排队的会话数量 {}", companyId, chatList);
        if (ObjectUtils.isEmpty(chatList)) {
            return;
        }
        for (int i=0; i<chatList.size(); i++) {
            CustomerServiceChat dbCustomerServiceChat = chatList.get(i);
            if (StringUtils.isEmpty(dbCustomerServiceChat.getImGroupId())) {
                continue;
            }
            ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getCompanyUsableAccount(imChatRequest);
            if (imOnlineServiceItem == null || imOnlineServiceItem.isOfflineStatue()) {
                log.info("排队聊天处理：公司 {} 没有空闲客服", companyId);
                break;
            }

            if (!StringUtils.isEmpty(dbCustomerServiceChat.getServerAccount()) && dbCustomerServiceChat.getServerAccount().equals(imOnlineServiceItem.getCustomerServiceAccount())) {
                List<String> accountList = TencentImCustomerUtil.getGroupMemberList(dbCustomerServiceChat.getImGroupId(), imChatRequest.getAppKey(), imChatRequest.getAppId());
                if (accountList == null) {
                    accountList = new ArrayList<>();
                }
                if (!accountList.contains(dbCustomerServiceChat.getServerAccount())) {
                    accountList.add(dbCustomerServiceChat.getServerAccount());
                }
                accountList.remove(dbCustomerServiceChat.getCustomerImAccount());
                TencentImCustomerUtil.deleteGroupMember(dbCustomerServiceChat.getImGroupId(), accountList, imChatRequest.getAppKey(), imChatRequest.getAppId());
            }
            List<String> accountList = new ArrayList<>();
            accountList.add(imOnlineServiceItem.getCustomerServiceAccount());
            accountList.add(dbCustomerServiceChat.getCustomerImAccount());
            TencentImCustomerUtil.addGroupMember(dbCustomerServiceChat.getImGroupId(), accountList, imChatRequest.getAppKey(), imChatRequest.getAppId());

            String groupId = dbCustomerServiceChat.getImGroupId();

            // 客服不在线说辞处理
            if (imOnlineServiceItem.isOfflineStatue()) {
                sendGroupSetting(0l, imOnlineServiceItem, groupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Offline,
                        imChatRequest.getAppKey(), imChatRequest.getAppId(), dbCustomerServiceChat.getStoreName(), dbCustomerServiceChat.getSourceStoreId(), dbCustomerServiceChat.getCustomerImAccount());
                dbCustomerServiceChat.setServiceState(1);
                dbCustomerServiceChat.setStartTime(0l);
            }
            else {
                // 自动发送人工欢迎语
                sendGroupSetting(0l, imOnlineServiceItem, groupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Welecome,
                        imChatRequest.getAppKey(), imChatRequest.getAppId(), dbCustomerServiceChat.getStoreName(), dbCustomerServiceChat.getSourceStoreId(), dbCustomerServiceChat.getCustomerImAccount());
                dbCustomerServiceChat.setServiceState(0);
                dbCustomerServiceChat.setStartTime(System.currentTimeMillis() / 1000);
            }
            dbCustomerServiceChat.setChatState(0);
            dbCustomerServiceChat.setServiceState(0);
            dbCustomerServiceChat.setSendState(1);
            dbCustomerServiceChat.setTimeoutState(0);
            dbCustomerServiceChat.setMsgTime(System.currentTimeMillis() / 1000);
            dbCustomerServiceChat.setUserTimeoutState(0);
            dbCustomerServiceChat.setServerAccount(imOnlineServiceItem.getCustomerServiceAccount());
            saveChat(dbCustomerServiceChat);

            if (!StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(dbCustomerServiceChat.getServerAccount())) {
                try {
                    CustomerServiceChatMark customerServiceChatMark = customerServiceChatMarkService.getTodayByServerAccountAndImGroupId(dbCustomerServiceChat.getServerAccount(), dbCustomerServiceChat.getImGroupId());
                    if (customerServiceChatMark != null) {
                        List<Integer> setMarkList = new ArrayList<>();
                        setMarkList.add(0);
                        TencentImCustomerUtil.markContact(customerServiceChatMark.getImGroupId(), customerServiceChatMark.getServerAccount(), setMarkList, imChatRequest.getAppKey(), imChatRequest.getAppId());
                    }
                }
                catch (Exception e) {
                    log.error("分配客服处理星标功能异常", e);
                }
            }
        }

        onlineServiceService.pushWebUpdateNotify(imChatRequest.getCompanyId(), imChatRequest.getAppKey(), imChatRequest.getAppId(), TencentImMessageType.ImChatQueue);
    }

    public List<String> getAllQueueCustomerList(Long companyInfoId) {
        return customerServiceChatRepository.findAllQueueCustomerList(companyInfoId, ImChatStateEnum.queue.getState());
    }

    public MicroServicePage<CustomerServiceChatResponse> getQueueChatList(ImChatRequest imChatRequest) {
        PageRequest pageRequest = PageRequest.of(imChatRequest.getPageNum(), imChatRequest.getPageSize());
        Page<CustomerServiceChat> page = customerServiceChatRepository.findAll(getWhereCriteria(imChatRequest), pageRequest);
        MicroServicePage<CustomerServiceChatResponse> microServicePage = KsBeanUtil.convertPage(page, CustomerServiceChatResponse.class);
        if (!ObjectUtils.isEmpty(microServicePage.getContent())) {
            microServicePage.getContent().forEach(chat -> {
                if (chat.getCreateTime() != null) {
                    chat.setCreateTimeSecond(chat.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000);
                }
            });
        }
        return microServicePage;
    }

    public static Specification<CustomerServiceChat> getWhereCriteria(ImChatRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cbuild.equal(root.get("companyInfoId"), request.getCompanyId()));
            predicates.add(cbuild.equal(root.get("chatState"), ImChatStateEnum.queue.getState()));
            cquery.orderBy(cbuild.asc(root.get("createTime")));
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public CustomerServiceChat getLastChatByGroupId(String imGroupId) {
        return customerServiceChatRepository.findByImGroupId(imGroupId);
    }

    public List<Long> getQueueCompanyIdList() {
        return customerServiceChatRepository.findAllQueueCompanyId();
    }

    public List<CustomerServiceChat> getAllQueueChatList(Long companyInfoId) {
        return customerServiceChatRepository.findByCompanyInfoIdAndChatStateOrderByChatIdAsc(companyInfoId, ImChatStateEnum.queue.getState());
    }

    public List<Long> findAllQueueChatIdByCompanyId(Long companyInfoId) {
        return customerServiceChatRepository.findChatIdByCompanyInfoIdAndChatStateOrderByChatIdAsc(companyInfoId, ImChatStateEnum.queue.getState());
    }

    public String leaveMessage(ImChatRequest request) {
        CustomerServiceChat chat = customerServiceChatRepository.findByImGroupId(request.getImGroupId());
        if (chat == null) {
            return "聊天群组不存在";
        }
        ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCustomerServiceAccount(request.getServerAccount());
        if (imOnlineServiceItem == null) {
            return "客服IM账号不存在";
        }
        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        TencentImCustomerUtil.sendGroupMsg(request.getImGroupId(), request.getServerAccount(), chat.getSourceCompanyId(), chat.getStoreId(), chat.getStoreName(),
                request.getMessage(), appKey, appId, -1, imOnlineServiceItem.getCustomerServiceName(), chat.getCustomerImAccount(), "server", request.getMessageType());
        return null;
    }

    public List<CustomerServiceChat> getTimeoutOffliceChat(int timeoutSeconds, int pageSize) {
        Long lastMsgTime = (System.currentTimeMillis() / 1000) - timeoutSeconds;
        return customerServiceChatRepository.findByTimeoutOffliceChat(0, 1, 0, lastMsgTime, pageSize);
    }

    public List<String> getGroupIdListByCompanyId(Long companyInfoId) {
        return customerServiceChatRepository.findGroupIdListByCompanyId(companyInfoId);
    }

    public List<CustomerServiceChatResponse> getAllGroupChat() {
        List<CustomerServiceChat> chatList = customerServiceChatRepository.findAllGroupChat();
        return KsBeanUtil.convert(chatList, CustomerServiceChatResponse.class);
    }
}
