package com.wanmi.sbc.setting.onlineservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ImChatStateEnum;
import com.wanmi.sbc.common.enums.ImSettingTypeEnum;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.repository.CommonChatMsgRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceChatRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceSettingRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CommonChatMsg;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChat;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChatMark;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatMarkService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceSettingService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.setting.onlineservice.repository.OnlineServiceRepository;
import com.wanmi.sbc.setting.onlineservice.model.root.OnlineService;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>onlineService业务逻辑</p>
 *
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@Slf4j
@Service("OnlineServiceService")
public class OnlineServiceService {
    @Autowired
    private OnlineServiceRepository onlineServiceRepository;

    @Autowired
    private CommonChatMsgRepository commonChatMsgRepository;

    @Autowired
    private CustomerServiceChatRepository customerServiceChatRepository;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private CustomerServiceSettingRepository customerServiceSettingRepository;

    @Lazy
    @Autowired
    private CustomerServiceChatService customerServiceChatService;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;

    @Autowired
    private CustomerServiceSettingService customerServiceSettingService;

    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CustomerServiceChatMarkService customerServiceChatMarkService;

    @PostConstruct
    private void init () {
        BlockingDeque<Runnable> blockingDeque = new LinkedBlockingDeque<>(12);
        threadPoolExecutor = new ThreadPoolExecutor(3, 6, 30l, TimeUnit.SECONDS, blockingDeque, new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * 新增onlineService
     *
     * @author lq
     */
    @Transactional
    public OnlineService add(OnlineService entity) {
        onlineServiceRepository.save(entity);
        return entity;
    }

    /**
     * 通过店铺id 查询在线客服
     *
     * @author lq
     */
    public OnlineService getByStoreId(Long storeId) {
        OnlineService onlineServer = onlineServiceRepository.findByStoreIdAndDelFlag(storeId, DeleteFlag.NO);
        if (Objects.nonNull(onlineServer)) {
            return onlineServer;
        } else {
            return this.saveOnlineServer(storeId);
        }
    }

    /**
     * 单个查询onlineService
     *
     * @param onlineServiceId
     * @return
     */
    public OnlineService getById(Integer onlineServiceId) {
        return onlineServiceRepository.getOne(onlineServiceId);
    }

    /**
     * 初始化一条客服开关设置记录
     *
     * @param storeId
     * @return
     */
    @Transactional
    public OnlineService saveOnlineServer(Long storeId) {
        OnlineService onlineServer = new OnlineService();
        onlineServer.setStoreId(storeId);
        onlineServer.setServerStatus(DefaultFlag.NO);
        onlineServer.setEffectiveApp(DefaultFlag.NO);
        onlineServer.setEffectiveMobile(DefaultFlag.NO);
        onlineServer.setEffectivePc(DefaultFlag.NO);
        onlineServer.setDelFlag(DeleteFlag.NO);
        onlineServer.setCreateTime(LocalDateTime.now());

        return onlineServiceRepository.saveAndFlush(onlineServer);
    }

    /**
     * 将实体包装成VO
     *
     * @author lq
     */
    public OnlineServiceVO wrapperVo(OnlineService onlineService) {
        if (onlineService != null) {
            OnlineServiceVO onlineServiceVO = new OnlineServiceVO();
            KsBeanUtil.copyPropertiesThird(onlineService, onlineServiceVO);
            return onlineServiceVO;
        }
        return null;
    }

    public List<CommonChatMsg> getCommonChatMessageList(Long imOnlineServiceId) {
        return commonChatMsgRepository.findByImOnlineServiceIdOrderBySortNumAsc(imOnlineServiceId);
    }

    private boolean isNewVersion (String appVersion) {
        if (StringUtils.isEmpty(appVersion)) {
            return false;
        }
        try {
            String regex = "[\\d]+";
            int[] oldVersions = new int[]{3, 2, 8};
            int index = 0;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(appVersion);
            while (matcher.find()) {
                String str = matcher.group();
                Integer value = Integer.parseInt(str);
                if (value > oldVersions[index]) {
                    return true;
                } else if (value < oldVersions[index]) {
                    return false;
                }
                index++;
            }
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }


    public ImOnlineServiceListResponse saveCustomerServiceChat(ImOnlineServiceItem imOnlineServiceItem, OnlineServiceListRequest onlineServiceListReq) {
        // 如果商家开启腾讯IM客服功能
        if (!((Integer)1).equals(onlineServiceListReq.getServiceSwitchType())
                || imOnlineServiceItem == null || StringUtils.isEmpty(onlineServiceListReq.getCustomerImAccount())) {
            return null;
        }

        log.info("APP联系商家客服群聊功能：App版本号 {}", onlineServiceListReq.getAppVersion());
        if (!isNewVersion(onlineServiceListReq.getAppVersion())) {
            log.info("APP联系商家客服群聊功能：App版本号 {}，不创建群", onlineServiceListReq.getAppVersion());
            return null;
        }

        return sendGroupChat(imOnlineServiceItem, onlineServiceListReq, false);
    }

    private ImOnlineServiceListResponse sendGroupChat(ImOnlineServiceItem imOnlineServiceItem, OnlineServiceListRequest onlineServiceListReq, boolean switchToNewService) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        onlineServiceListReq.setAppKey(appKey);
        onlineServiceListReq.setAppId(appId);

        CustomerServiceChat dbCustomerServiceChat = customerServiceChatRepository.findByCustomerImAccountAndSourceCompanyId(onlineServiceListReq.getCustomerImAccount(), onlineServiceListReq.getSourceCompanyId());
        stopWatch.stop();
        log.info("联系商家客服耗时 查询历史聊天记录 {} - {} - 耗时 {}", onlineServiceListReq.getStoreId(), JSON.toJSONString(dbCustomerServiceChat), stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        if (switchToNewService && dbCustomerServiceChat != null) {
            if (StringUtils.isEmpty(onlineServiceListReq.getUserId())) {
                onlineServiceListReq.setUserId(dbCustomerServiceChat.getCustomerId());
            }
            if (StringUtils.isEmpty(onlineServiceListReq.getAppPlatform()) || StringUtils.isEmpty(onlineServiceListReq.getAppPlatform())) {
                onlineServiceListReq.setAppPlatform(dbCustomerServiceChat.getAppPlatform());
                onlineServiceListReq.setAppSysModel(dbCustomerServiceChat.getAppSysModel());
                onlineServiceListReq.setAppVersion(dbCustomerServiceChat.getAppVersion());
                onlineServiceListReq.setIpAddr(dbCustomerServiceChat.getIpAddr());
            }
            if (StringUtils.isEmpty(onlineServiceListReq.getStoreName()) || ObjectUtils.isEmpty(onlineServiceListReq.getSourceCompanyId()) || ObjectUtils.isEmpty(onlineServiceListReq.getSourceStoreId())) {
                onlineServiceListReq.setStoreName(dbCustomerServiceChat.getStoreName());
                onlineServiceListReq.setSourceStoreId(dbCustomerServiceChat.getSourceStoreId());
                onlineServiceListReq.setSourceCompanyId(dbCustomerServiceChat.getSourceCompanyId());
            }
        }

        if (dbCustomerServiceChat != null && !StringUtils.isEmpty(dbCustomerServiceChat.getImGroupId()) && ImChatStateEnum.queue.getState().equals(dbCustomerServiceChat.getChatState())) {
            List<Long> queueChatIdList = customerServiceChatService.findAllQueueChatIdByCompanyId(imOnlineServiceItem.getCompanyInfoId());
            int index = queueChatIdList.indexOf(dbCustomerServiceChat.getChatId());
            int ranking = queueChatIdList.size() + 1;
            if (index >= 0) {
                ranking = index + 1;
            }
            try {
                String redisKey = "im.chat.queue." + imOnlineServiceItem.getCompanyInfoId() + "." + dbCustomerServiceChat.getCustomerImAccount();
                String redisRanking = redisService.getString(redisKey);
                log.info("联系商家客服读取Redis排名 {} - {} - Redis排名 {} 当前排名 {}", dbCustomerServiceChat.getCompanyInfoId(), dbCustomerServiceChat.getCustomerImAccount(), redisRanking, ranking);
                if (StringUtils.isEmpty(redisRanking) || !redisRanking.equals(String.valueOf(ranking))) {
                    customerServiceChatService.sendQueueSettingGroupMessage(0l, imOnlineServiceItem, dbCustomerServiceChat.getImGroupId(),
                            imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Queue, appKey, appId, onlineServiceListReq.getStoreName(), index + 1, onlineServiceListReq.getSourceStoreId());
                    redisService.setString(redisKey, String.valueOf(ranking), 600);
                }
            }
            catch (Exception e) {
                log.error("联系商家客服写入Redis排名异常", e);
            }

//            TencentImCustomerUtil.updateGroupInfo(dbCustomerServiceChat.getImGroupId(), StringUtils.isEmpty(onlineServiceListReq.getStoreName()) ? "" : onlineServiceListReq.getStoreName(),
//                    onlineServiceListReq.getStoreLogo(), onlineServiceListReq.getCustomerImAccount(), "Private", appKey, appId);
            stopWatch.stop();
            log.info("联系商家客服耗时 {} - 排队中总耗时 {}", onlineServiceListReq.getStoreId(), stopWatch.getTotalTimeSeconds());
            return ImOnlineServiceListResponse.builder().imGroupId(dbCustomerServiceChat.getImGroupId()).queue(ranking).build();
        }


        // 如果当前会话还没有结束
        if (dbCustomerServiceChat != null && !switchToNewService
                && !StringUtils.isEmpty(dbCustomerServiceChat.getImGroupId()) && dbCustomerServiceChat.getChatState().equals(0)) {
            // 如果是在线聊天
            boolean onlineChat = ((Integer) 0).equals(dbCustomerServiceChat.getServiceState());
            // 如果是离线聊天，并且当前没有客服在线
            boolean offlineAndAccountOffline = imOnlineServiceItem.isOfflineStatue();
            if (onlineChat || offlineAndAccountOffline) {
                List<String> accountList = new ArrayList<>();
                accountList.add(dbCustomerServiceChat.getServerAccount());
                if (!StringUtils.isEmpty(onlineServiceListReq.getCustomerImAccount())) {
                    accountList.add(onlineServiceListReq.getCustomerImAccount());
                }
                TencentImCustomerUtil.addGroupMember(dbCustomerServiceChat.getImGroupId(), accountList, appKey, appId);
                stopWatch.stop();
                log.info("联系商家客服耗时 {} - 正在聊天中总耗时 {}", onlineServiceListReq.getStoreId(), stopWatch.getTotalTimeSeconds());
                return ImOnlineServiceListResponse.builder().imGroupId(dbCustomerServiceChat.getImGroupId()).build();
            }

        }
        String groupId = null;
        if (dbCustomerServiceChat != null) {
            groupId = dbCustomerServiceChat.getImGroupId();
            if (!StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(dbCustomerServiceChat.getServerAccount())
                    && !dbCustomerServiceChat.getServerAccount().equals(imOnlineServiceItem.getCustomerServiceAccount())) {
                List<String> accountList = Arrays.asList(dbCustomerServiceChat.getServerAccount());
                TencentImCustomerUtil.deleteGroupMember(groupId, accountList, appKey, appId);
//                TencentImCustomerUtil.sendCustomMsg(dbCustomerServiceChat.getServerAccount(), "更新聊天会话列表", TencentImMessageType.UpdateChatList, appId, appKey);
            }
//            TencentImCustomerUtil.updateGroupInfo(groupId, StringUtils.isEmpty(onlineServiceListReq.getStoreName()) ? "" : onlineServiceListReq.getStoreName(),
//                    onlineServiceListReq.getStoreLogo(), onlineServiceListReq.getCustomerImAccount(), "Private", appKey, appId);
        }
        if (StringUtils.isEmpty(groupId)) {
            groupId = TencentImCustomerUtil.createGroup(StringUtils.isEmpty(onlineServiceListReq.getStoreName()) ? "" : onlineServiceListReq.getStoreName(),
                    onlineServiceListReq.getStoreLogo(), onlineServiceListReq.getCustomerImAccount(), "Private", appKey, appId);
        }
        List<String> accountList = new ArrayList<>();
        // 聊天排队， 连天排队中，先不拉客服入群，由系统自动回复消息
        if (!imOnlineServiceItem.isWaitInLine()) {
            accountList.add(imOnlineServiceItem.getCustomerServiceAccount());
        }
        if (!StringUtils.isEmpty(onlineServiceListReq.getCustomerImAccount())) {
            accountList.add(onlineServiceListReq.getCustomerImAccount());
        }
        TencentImCustomerUtil.addGroupMember(groupId, accountList, appKey, appId);

//        if (!switchToNewService) {
//            TencentImCustomerUtil.sendGroupMsg(groupId, imOnlineServiceItem.getCustomerServiceAccount(), imOnlineServiceItem.getCompanyInfoId(), onlineServiceListReq.getSourceStoreId(),
//                    onlineServiceListReq.getStoreName(), onlineServiceListReq.getStoreName(), appKey, appId, ImSettingTypeEnum.SourceStore.getType(), imOnlineServiceItem.getCustomerServiceName(), onlineServiceListReq.getCustomerImAccount());
//        }

        ImOnlineServiceListResponse imOnlineServiceListResponse = ImOnlineServiceListResponse.builder().imGroupId(groupId).build();
        // 客服不在线说辞处理
        /*if (imOnlineServiceItem.isOfflineStatue()) {
            customerServiceChatService.sendGroupSetting(0l, imOnlineServiceItem, groupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Offline,
                    appKey, appId, onlineServiceListReq.getStoreName(), onlineServiceListReq.getSourceStoreId(), onlineServiceListReq.getCustomerImAccount());
        }
        // 排队消息
        else if (imOnlineServiceItem.isWaitInLine()) {
            List<Long> queueChatIdList = customerServiceChatService.findAllQueueChatIdByCompanyId(imOnlineServiceItem.getCompanyInfoId());
            int ranking = queueChatIdList.size() + 1;
            if (dbCustomerServiceChat != null) {
                int index = queueChatIdList.indexOf(dbCustomerServiceChat.getChatId());
                if (index > 0) {
                    ranking = index + 1;
                }
            }
            imOnlineServiceListResponse.setQueue(ranking);
            customerServiceChatService.sendQueueSettingGroupMessage(0l, imOnlineServiceItem, groupId,
                    imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Queue, appKey, appId, onlineServiceListReq.getStoreName(), ranking, onlineServiceListReq.getSourceStoreId());

        }
        // 客服在线，自动发送人工欢迎语
        else {
            // 自动发送人工欢迎语
            customerServiceChatService.sendGroupSetting(0l, imOnlineServiceItem, groupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Welecome,
                    appKey, appId, onlineServiceListReq.getStoreName(), onlineServiceListReq.getSourceStoreId(), onlineServiceListReq.getCustomerImAccount());
        }*/

        stopWatch.stop();
        log.info("联系商家客服耗时 {} - 创建群拉群发送推送耗时 {}", onlineServiceListReq.getStoreId(), stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        CustomerServiceChat customerServiceChat = KsBeanUtil.convert(onlineServiceListReq, CustomerServiceChat.class);
        customerServiceChat.setCompanyInfoId(imOnlineServiceItem.getCompanyInfoId());
        customerServiceChat.setStoreId(imOnlineServiceItem.getStoreId());
        customerServiceChat.setSourceStoreId(onlineServiceListReq.getSourceStoreId());
        customerServiceChat.setCustomerId(onlineServiceListReq.getUserId());
        customerServiceChat.setServerAccount(imOnlineServiceItem.getCustomerServiceAccount());
        customerServiceChat.setMsgTime(System.currentTimeMillis() / 1000);
        customerServiceChat.setImGroupId(groupId);
        if (imOnlineServiceItem.isOfflineStatue()) {
            customerServiceChat.setServiceState(1);
            customerServiceChat.setStartTime(0l);
        }
        else {
            customerServiceChat.setServiceState(0);
            customerServiceChat.setStartTime(System.currentTimeMillis() / 1000);
        }
        customerServiceChat.setSendState(1);
        customerServiceChat.setTimeoutState(0);
        customerServiceChat.setUserTimeoutState(0);
        customerServiceChat.setStoreName(onlineServiceListReq.getStoreName());
        if (imOnlineServiceItem.isWaitInLine()) {
            customerServiceChat.setChatState(4);
            customerServiceChat.setStartTime(0l);
        }
        else {
            customerServiceChat.setChatState(0);
            customerServiceChat.setStartTime(System.currentTimeMillis() / 1000);
        }
        customerServiceChat.setEndTime(0l);
        customerServiceChat.setChatDuration(0l);
        customerServiceChat.setSourceCompanyId(onlineServiceListReq.getSourceCompanyId());
        customerServiceChat.setSendLeave(0);
        if (!StringUtils.isEmpty(onlineServiceListReq.getSwitchSourceAccount())) {
            customerServiceChat.setSwitchSourceAccount(onlineServiceListReq.getSwitchSourceAccount());
        }

        if (dbCustomerServiceChat != null && (dbCustomerServiceChat.getChatState().equals(0) || dbCustomerServiceChat.getChatState().equals(4))) {
            dbCustomerServiceChat.setChatState(3);
            dbCustomerServiceChat.setEndTime(System.currentTimeMillis() / 1000);
            if (dbCustomerServiceChat.getStartTime() != null && dbCustomerServiceChat.getStartTime() > 0) {
                dbCustomerServiceChat.setChatDuration(dbCustomerServiceChat.getEndTime() - dbCustomerServiceChat.getStartTime());
            }
            else {
                dbCustomerServiceChat.setChatDuration(0l);
                dbCustomerServiceChat.setStartTime(0l);
            }
            List<CustomerServiceChat> customerServiceChatList = Arrays.asList(customerServiceChat, dbCustomerServiceChat);
            customerServiceChatService.saveAllChat(customerServiceChatList);
        }
        else {
            customerServiceChatService.saveChat(customerServiceChat);
        }
        String imGroupId = groupId;
        threadPoolExecutor.execute(() -> {
            StopWatch inStopWatch = new StopWatch();
            inStopWatch.start();

            List<String> memberList = TencentImCustomerUtil.getGroupMemberList(imGroupId, appKey, appId);
            memberList.remove(customerServiceChat.getServerAccount());
            memberList.remove(customerServiceChat.getCustomerImAccount());
            if (!ObjectUtils.isEmpty(memberList)) {
                TencentImCustomerUtil.deleteGroupMember(imGroupId, memberList, appKey, appId);
            }


            if (imOnlineServiceItem.isOfflineStatue()) {
                customerServiceChatService.sendGroupSetting(0l, imOnlineServiceItem, imGroupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Offline,
                        appKey, appId, onlineServiceListReq.getStoreName(), onlineServiceListReq.getSourceStoreId(), onlineServiceListReq.getCustomerImAccount());
            }
            // 排队消息
            else if (imOnlineServiceItem.isWaitInLine()) {
                List<Long> queueChatIdList = customerServiceChatService.findAllQueueChatIdByCompanyId(imOnlineServiceItem.getCompanyInfoId());
                int ranking = queueChatIdList.size() + 1;
                if (customerServiceChat != null) {
                    try {
                        log.info("联系商家客服排名 {} - {} - {}", imOnlineServiceItem.getCompanyInfoId(), customerServiceChat.getChatId(), JSON.toJSONString(queueChatIdList));
                        int index = queueChatIdList.indexOf(customerServiceChat.getChatId());
                        if (index >= 0) {
                            ranking = index + 1;
                        }
                    }
                    catch (Exception e) {
                        log.error("联系商家客服排名异常", e);
                    }
                }
                try {
                    String redisKey = "im.chat.queue." + imOnlineServiceItem.getCompanyInfoId() + "." + customerServiceChat.getCustomerImAccount();
                    redisService.setString(redisKey, String.valueOf(ranking), 600);
                    log.info("联系商家客服写入Redis排名 {} - {} - {}", imOnlineServiceItem.getCompanyInfoId(), customerServiceChat.getCustomerImAccount(), ranking);
                }
                catch (Exception e) {
                    log.error("联系商家客服写入Redis排名异常", e);
                }
                imOnlineServiceListResponse.setQueue(ranking);
                customerServiceChatService.sendQueueSettingGroupMessage(0l, imOnlineServiceItem, imGroupId,
                        imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Queue, appKey, appId, onlineServiceListReq.getStoreName(), ranking, onlineServiceListReq.getSourceStoreId());

            }
            // 客服在线，自动发送人工欢迎语
            else {
                String switchSourceAccount = onlineServiceListReq.getSwitchSourceAccount();
                String switchSourceAccountNick = onlineServiceListReq.getSwitchSourceNick();
                // 自动发送人工欢迎语
                customerServiceChatService.sendGroupSetting(0l, imOnlineServiceItem, imGroupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Welecome,
                        appKey, appId, onlineServiceListReq.getStoreName(), onlineServiceListReq.getSourceStoreId(), onlineServiceListReq.getCustomerImAccount(),
                        switchSourceAccount, switchSourceAccountNick);
            }

            if (!StringUtils.isEmpty(imGroupId) && !StringUtils.isEmpty(customerServiceChat.getServerAccount())) {
                try {
                    CustomerServiceChatMark customerServiceChatMark = customerServiceChatMarkService.getTodayByServerAccountAndImGroupId(customerServiceChat.getServerAccount(), customerServiceChat.getImGroupId());
                    if (customerServiceChatMark != null) {
                        List<Integer> setMarkList = new ArrayList<>();
                        setMarkList.add(0);
                        TencentImCustomerUtil.markContact(customerServiceChatMark.getImGroupId(), customerServiceChatMark.getServerAccount(), setMarkList, appKey, appId);
                    }
                }
                catch (Exception e) {
                    log.error("分配客服处理星标功能异常", e);
                }
            }

            pushWebUpdateNotify(imOnlineServiceItem.getCompanyInfoId(), appKey, appId, TencentImMessageType.UpdateChatList, true);
            if (imOnlineServiceItem.isOfflineStatue()) {
                pushWebUpdateNotify(imOnlineServiceItem.getCompanyInfoId(), appKey, appId, TencentImMessageType.ImChatQueue, true);
            }
            inStopWatch.stop();
            log.info("联系商家客服耗时 查询历史聊天记录 {} - 推送更新列表通知耗时 {}", onlineServiceListReq.getStoreId(), inStopWatch.getTotalTimeSeconds());
        });

        stopWatch.stop();
        log.info("联系商家客服耗时 {} - 插入聊天记录 {}", onlineServiceListReq.getStoreId(), stopWatch.getTotalTimeSeconds());
        return imOnlineServiceListResponse;
    }

    public String createChat(ImChatRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");

        CustomerServiceChat dbCustomerServiceChat = customerServiceChatRepository.findByImGroupId(request.getImGroupId());
        if (dbCustomerServiceChat == null) {
            stopWatch.stop();
            return "聊天会话不存在";
        }
        List<ImOnlineServiceItem> serverAccountList = imOnlineServiceItemService.getListByCompanyInfoId(dbCustomerServiceChat.getCompanyInfoId());
        if (dbCustomerServiceChat.getChatState().equals(0)) {
            String accountName = null;
            for (ImOnlineServiceItem item : serverAccountList) {
                if (item.getCustomerServiceAccount().equals(dbCustomerServiceChat.getServerAccount())) {
                    accountName = item.getCustomerServiceName();
                    break;
                }
            }
            stopWatch.stop();
            return "客户正在与 "+accountName +"对话中";
        }
        stopWatch.stop();
        log.info("联系商家客服耗时 查询历史聊天记录 {} - {} - 耗时 {}", dbCustomerServiceChat.getStoreId(), JSON.toJSONString(dbCustomerServiceChat), stopWatch.getTotalTimeSeconds());
        stopWatch.start();

        ImOnlineServiceItem serviceItem = null;
        for (ImOnlineServiceItem item : serverAccountList) {
            if (item.getCustomerServiceAccount().equals(request.getServerAccount())) {
                serviceItem = item;
                break;
            }
        }
        if (serviceItem == null) {
            stopWatch.stop();
            return "客服账号不存在";
        }
        if (((Integer)1).equals(serviceItem.getServiceStatus())) {
            return "当前为离线状态不能重建会话";
        }
        String groupId = dbCustomerServiceChat.getImGroupId();
        List<String> accountList = new ArrayList<>();
        accountList.add(request.getServerAccount());
        accountList.add(dbCustomerServiceChat.getCustomerImAccount());
        TencentImCustomerUtil.addGroupMember(groupId, accountList, appKey, appId);

        CustomerServiceChat customerServiceChat = new CustomerServiceChat();
        customerServiceChat.setCustomerImAccount(dbCustomerServiceChat.getCustomerImAccount());
        customerServiceChat.setAppVersion(dbCustomerServiceChat.getAppVersion());
        customerServiceChat.setAppSysModel(dbCustomerServiceChat.getAppSysModel());
        customerServiceChat.setAppPlatform(dbCustomerServiceChat.getAppPlatform());
        customerServiceChat.setCompanyInfoId(dbCustomerServiceChat.getCompanyInfoId());
        customerServiceChat.setStoreId(dbCustomerServiceChat.getStoreId());
        customerServiceChat.setSourceStoreId(dbCustomerServiceChat.getSourceStoreId());
        customerServiceChat.setCustomerId(dbCustomerServiceChat.getCustomerId());
        customerServiceChat.setServerAccount(request.getServerAccount());
        customerServiceChat.setMsgTime(System.currentTimeMillis() / 1000);
        customerServiceChat.setImGroupId(groupId);
        customerServiceChat.setServiceState(0);
        customerServiceChat.setStartTime(System.currentTimeMillis() / 1000);
        customerServiceChat.setSendState(1);
        customerServiceChat.setTimeoutState(0);
        customerServiceChat.setUserTimeoutState(0);
        customerServiceChat.setStoreName(dbCustomerServiceChat.getStoreName());
        customerServiceChat.setChatState(0);
        customerServiceChat.setStartTime(System.currentTimeMillis() / 1000);
        customerServiceChat.setEndTime(0l);
        customerServiceChat.setChatDuration(0l);
        customerServiceChat.setSendLeave(0);
        customerServiceChat.setSourceCompanyId(dbCustomerServiceChat.getSourceCompanyId());

        customerServiceChatService.saveChat(customerServiceChat);

        ImOnlineServiceItem imOnlineServiceItem = serviceItem;
        threadPoolExecutor.execute(() -> {
            StopWatch inStopWatch = new StopWatch();
            inStopWatch.start();

            if (!StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(customerServiceChat.getServerAccount())) {
                try {
                    CustomerServiceChatMark customerServiceChatMark = customerServiceChatMarkService.getTodayByServerAccountAndImGroupId(customerServiceChat.getServerAccount(), customerServiceChat.getImGroupId());
                    if (customerServiceChatMark != null) {
                        List<Integer> setMarkList = new ArrayList<>();
                        setMarkList.add(0);
                        TencentImCustomerUtil.markContact(customerServiceChatMark.getImGroupId(), customerServiceChatMark.getServerAccount(), setMarkList, appKey, appId);
                    }
                }
                catch (Exception e) {
                    log.error("分配客服处理星标功能异常", e);
                }
            }
            customerServiceChatService.sendGroupSetting(0l, imOnlineServiceItem, groupId, imOnlineServiceItem.getCustomerServiceAccount(), ImSettingTypeEnum.Welecome,
                    appKey, appId, dbCustomerServiceChat.getStoreName(), dbCustomerServiceChat.getSourceStoreId(), dbCustomerServiceChat.getCustomerImAccount());
            pushWebUpdateNotify(imOnlineServiceItem.getCompanyInfoId(), appKey, appId, TencentImMessageType.UpdateChatList, true);
            if (imOnlineServiceItem.isOfflineStatue()) {
                pushWebUpdateNotify(imOnlineServiceItem.getCompanyInfoId(), appKey, appId, TencentImMessageType.ImChatQueue, true);
            }
            inStopWatch.stop();
            log.info("客服发起联系客户 推送异步消息耗时 {} - {}", dbCustomerServiceChat.getCompanyInfoId(), inStopWatch.getTotalTimeSeconds());
        });
        stopWatch.stop();
        log.info("客服发起联系客户 总耗时 {} - {}", dbCustomerServiceChat.getCompanyInfoId(), stopWatch.getTotalTimeSeconds());
        return null;
    }

    public void pushWebUpdateNotify(Long companyInfoId, String appKey, long appId, TencentImMessageType tencentImMessageType) {
        pushWebUpdateNotify(companyInfoId, appKey, appId, tencentImMessageType, false);
    }

    public void pushWebUpdateNotify(Long companyInfoId, String appKey, long appId, TencentImMessageType tencentImMessageType, boolean inThreadPool) {
        if (companyInfoId == null) {
            return;
        }
        List<ImOnlineServiceItem> imOnlineServiceItemList = imOnlineServiceItemService.getListByCompanyInfoId(companyInfoId);
        if (ObjectUtils.isEmpty(imOnlineServiceItemList)) {
            return;
        }

        if (!inThreadPool) {
            threadPoolExecutor.execute(() -> {
                realPushWebUpdateNotify(appKey, appId, tencentImMessageType, imOnlineServiceItemList);
            });
        }
        else {
            realPushWebUpdateNotify(appKey, appId, tencentImMessageType, imOnlineServiceItemList);
        }
    }

    private void realPushWebUpdateNotify(String appKey, long appId, TencentImMessageType tencentImMessageType, List<ImOnlineServiceItem> imOnlineServiceItemList) {
        if (StringUtils.isEmpty(appKey)) {
            // 获取腾讯IM 账号配置
            SystemConfigResponse systemConfigResponse =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
            SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
            JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
            appId = Long.valueOf(jsonObject.getString("appId"));
            appKey = jsonObject.getString("appKey");
        }
        String realAppKey = appKey;
        long realAppId = appId;
        List<String> accountList = imOnlineServiceItemList.stream().map(ImOnlineServiceItem::getCustomerServiceAccount).collect(Collectors.toList());
        List<String> onlineAccountList = TencentImCustomerUtil.getOnlineAccount(accountList, appId, appKey);
        onlineAccountList.forEach(account -> {
            TencentImCustomerUtil.sendCustomMsg(account, tencentImMessageType.getMsgDesc(), tencentImMessageType, realAppId, realAppKey);
        });
    }

    public List<ImOnlineServiceItemVO> getOnlineImAccount(ImOnlineServiceSignRequest request) {
        // 店铺客服列表
        List<ImOnlineServiceItem> onlineServiceItemList = imOnlineServiceItemService.getListByCompanyInfoId(request.getCompanyId());
        if (ObjectUtils.isEmpty(onlineServiceItemList)) {
            return new ArrayList<>();
        }

        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");

        List<ImOnlineServiceItemVO> resultList = new ArrayList<>();
        // 腾讯IM，在线客服账号逻辑处理。APP端请求客服聊天服务，优先分配在线的客服账号提供聊天服务
        // 调用腾讯IM接口，获取在线登录的客户账号
        List<String> onlineAccountList = TencentImCustomerUtil.getOnlineAccount(onlineServiceItemList.stream().map(ImOnlineServiceItem::getCustomerServiceAccount).collect(Collectors.toList()), appId, appKey);
        if (request.getStatus() == null) {
            request.setStatus(0);
        }
        for (String account : onlineAccountList) {
            // 查询状态：0、可以接受聊天的登录在线客服；
            if (request.getStatus().equals(0)) {
                if (account.equals(request.getCustomerServiceAccount())) {
                    continue;
                }
                for (ImOnlineServiceItem item : onlineServiceItemList) {
                    if (account.equals(item.getCustomerServiceAccount()) && ((Integer)0).equals(item.getServiceStatus())) {
                        resultList.add(KsBeanUtil.convert(item, ImOnlineServiceItemVO.class));
                        break;
                    }
                }
            }
            // 查询状态：1、在线的客服
            else if (request.getStatus().equals(1)) {
                for (ImOnlineServiceItem item : onlineServiceItemList) {
                    if (account.equals(item.getCustomerServiceAccount())) {
                        continue;
                    }
                    if (((Integer)0).equals(item.getServiceStatus()) || ((Integer)2).equals(item.getServiceStatus())) {
                        resultList.add(KsBeanUtil.convert(item, ImOnlineServiceItemVO.class));
                        break;
                    }
                }
            }
        }
        return resultList;
    }

    public String switchStoreIMAccount(OnlineServiceListRequest request) {
        Integer limit = customerServiceSettingService.getCompanyAcceptQuantity(request.getCompanyInfoId());
        log.info("切换客服 接待上限 {} - {}", request.getCompanyInfoId(), limit);
        ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getAccountByCustomerServiceAccountAndCompanyInfoId(request.getServerAccount(), request.getCompanyInfoId(), limit);
        if (imOnlineServiceItem == null) {
            throw new SbcRuntimeException("K-000009", "客服不存在或者不在线");
        }
        if (imOnlineServiceItem.isWaitInLine()) {
            throw new SbcRuntimeException("k-0000010", "客服正在忙碌状态");
        }
        CustomerServiceChat dbCustomerServiceChat = customerServiceChatService.getLastChatByGroupId(request.getImGroupId());
        if (dbCustomerServiceChat != null) {
            request.setCustomerImAccount(dbCustomerServiceChat.getCustomerImAccount());
            request.setSourceCompanyId(dbCustomerServiceChat.getSourceCompanyId());
            request.setStoreName(dbCustomerServiceChat.getStoreName());
            request.setSourceStoreId(dbCustomerServiceChat.getSourceStoreId());
        }
        if (dbCustomerServiceChat != null && !StringUtils.isEmpty(dbCustomerServiceChat.getServerAccount())) {
            SystemConfigResponse systemConfigResponse =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
            SystemConfigVO systemConfigVO = systemConfigResponse.getSystemConfigVOList().get(Constants.no);
            JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
            long appId = Long.valueOf(jsonObject.getString("appId"));
            String appKey = jsonObject.getString("appKey");
            List<String> accountList = Arrays.asList(dbCustomerServiceChat.getServerAccount());
            TencentImCustomerUtil.deleteGroupMember(request.getImGroupId(), accountList, appKey, appId);
        }
        ImOnlineServiceListResponse imOnlineServiceListResponse = sendGroupChat(imOnlineServiceItem, request, true);
        if (imOnlineServiceListResponse == null) {
            return null;
        }
        threadPoolExecutor.execute(() -> {
            customerServiceChatService.processQueue(ImChatRequest.builder().companyId(request.getCompanyInfoId()).appId(request.getAppId()).appKey(request.getAppKey()).build());
        });
        return imOnlineServiceListResponse.getImGroupId();
    }

}
