package com.wanmi.sbc.setting.imonlineserviceitem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.ImChatRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceSettingContentVo;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.ImOnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.ImSignResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceChatRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceSettingRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceSetting;
import com.wanmi.sbc.setting.imonlineservice.root.ImOnlineService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceSettingService;
import com.wanmi.sbc.setting.imonlineserviceitem.repository.ImOnlineServiceItemRepository;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>ImOnlineServiceItemService业务逻辑</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Slf4j
@Service("ImOnlineServiceItemService")
public class ImOnlineServiceItemService {
    @Autowired
    private ImOnlineServiceItemRepository onlineServiceItemRepository;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CustomerServiceSettingRepository customerServiceSettingRepository;
    @Autowired
    private CustomerServiceChatRepository customerServiceChatRepository;

    @Autowired
    private CustomerServiceSettingService customerServiceSettingService;

    /**
     * 新增OnlineServiceItem
     *
     * @author sgy
     */
    @Transactional
    public void save(List<ImOnlineServiceItem> entity) {
        if (ObjectUtils.isEmpty(entity)) {
            return;
        }
        entity.forEach(item -> {
            if (item.getServiceStatus() == null) {
                item.setServiceStatus(0);
            }
        });
        onlineServiceItemRepository.saveAll(entity);
    }

    /**
     * 根据客服设置id删除客服列表onlineerviceItem
     *
     * @author sgy
     */
    @Transactional
    public void deleteByOnlineServiceId(Integer id) {
        onlineServiceItemRepository.deleteByImOnlineServiceId(id);
    }

    /**
     * 列表查询onlineerviceItem
     *
     * @author sgy
     */
    public List<ImOnlineServiceItem> list(Integer onlineServiceId) {
        return onlineServiceItemRepository.findByImOnlineServiceIdAndDelFlagOrderByCreateTimeDesc(
                onlineServiceId, DeleteFlag.NO);
    }

    private List<ImOnlineServiceItem> findCanServerList(Integer onlineServiceId) {
        return onlineServiceItemRepository.findByImOnlineServiceIdAndDelFlagAndServiceStatusOrderByCreateTimeDesc(onlineServiceId, DeleteFlag.NO, 0);
    }

    /**
     * 查询客服的im号是否重复
     *
     * @param serverAccount
     * @return
     */
    public List<ImOnlineServiceItem> checkDuplicateAccount(List<String> serverAccount) {
        return onlineServiceItemRepository.checkDuplicateAccount(serverAccount);
    }

    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public ImOnlineServiceItemVO wrapperVo(ImOnlineServiceItem onlineServiceItem) {
        if (onlineServiceItem != null) {
            ImOnlineServiceItemVO onlineServiceItemVO = new ImOnlineServiceItemVO();
            KsBeanUtil.copyPropertiesThird(onlineServiceItem, onlineServiceItemVO);
            return onlineServiceItemVO;
        }
        return null;
    }

    /**
     * 转换对象
     *
     * @param onlineServerItemVoList
     * @return
     */
    public List<ImOnlineServiceItem> getOnlineServerItemList(List<ImOnlineServiceItemVO> onlineServerItemVoList) {
        List<ImOnlineServiceItem> onlineServerItemList = new ArrayList<>();
        onlineServerItemVoList.forEach(onlineServerItemVo -> {
            ImOnlineServiceItem onlineServerItem = new ImOnlineServiceItem();
            BeanUtils.copyProperties(onlineServerItemVo, onlineServerItem);
            onlineServerItem.setCreateTime(LocalDateTime.now());
            onlineServerItem.setDelFlag(DeleteFlag.NO);
            if (onlineServerItem.getManagerFlag() == null) {
                onlineServerItem.setManagerFlag(0);
            }
            onlineServerItemList.add(onlineServerItem);
        });
        return onlineServerItemList;
    }


    @Transactional
    public void addImByAccount(List<ImOnlineServiceItem> onlineServerItemList, String logo) {
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();

        //添加IM账号信息
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        String appKey= jsonObject.getString("appKey");
        long appId= Long.valueOf(jsonObject.getString("appId"));
        if (CollectionUtils.isNotEmpty(onlineServerItemList)) {
            for (ImOnlineServiceItem vo:onlineServerItemList) {
                if (vo.getCompanyInfoId() != null && vo.getCompanyInfoId().equals(-1l)) {
                    logo = "https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311301552060621.png";
                }
                TencentImCustomerUtil.accountImport(vo.getCustomerServiceAccount(),vo.getCustomerServiceName(),
                        logo, TencentImCustomerUtil.getTxCloudUserSig(vo.getCustomerServiceAccount(),appId,appKey),appId,appKey);
            }
        }
    }

    public ImOnlineServiceItem getCompanyUsableAccount(ImChatRequest imChatRequest) {
        // 店铺客服列表
        List<ImOnlineServiceItem> onlineServiceItemList = getListByCompanyInfoId(imChatRequest.getCompanyId());
        if (ObjectUtils.isEmpty(onlineServiceItemList)) {
            log.info("客户排队处理：查询店铺客服为空 {}", imChatRequest.getCompanyId());
            return null;
        }

        // 腾讯IM，在线客服账号逻辑处理。APP端请求客服聊天服务，优先分配在线的客服账号提供聊天服务
        // 调用腾讯IM接口，获取在线登录的客户账号
        List<String> onlineAccountList = TencentImCustomerUtil.getOnlineAccount(onlineServiceItemList.stream()
                .map(ImOnlineServiceItem::getCustomerServiceAccount).collect(Collectors.toList()), imChatRequest.getAppId(), imChatRequest.getAppKey());
        if (ObjectUtils.isEmpty(onlineAccountList)) {
            log.info("客户排队处理：查询腾讯IM登录状态，客服全部离线 {}", imChatRequest.getCompanyId());
            return null;
        }

        List<ImOnlineServiceItem> tmpList = new ArrayList<>();
        for (String account : onlineAccountList) {
            for (ImOnlineServiceItem item : onlineServiceItemList) {
                if (account.equals(item.getCustomerServiceAccount()) && ((Integer) 0).equals(item.getServiceStatus())) {
                    tmpList.add(item);
                    break;
                }
            }
        }
        if (ObjectUtils.isEmpty(tmpList)) {
            log.info("客户排队处理：客服全部离线 {}", imChatRequest.getCompanyId());
            return null;
        }

        List<Map<String, Object>> statisVoList = customerServiceChatRepository.countCompanyAcceptQuantity(imChatRequest.getCompanyId(), 0, 0);
        BigInteger minNum = BigInteger.valueOf(99999);
        ImOnlineServiceItem minNumAccountItem = null;
        for (ImOnlineServiceItem accountItem : tmpList) {
            Map<String, Object> quantityMap = null;
            for (Map<String, Object> statisVo : statisVoList) {
                if (accountItem.getCustomerServiceAccount().equals(statisVo.get("serverAccount"))) {
                    quantityMap = statisVo;
                    break;
                }
            }

            int redisQuantity = 0;
            try {
                String redisKey = "im.quantity." + accountItem.getCustomerServiceAccount();
                Integer value = redisService.getObj(redisKey, Integer.class);
                if (value != null && value > 0) {
                    redisQuantity = value;
                }
            }
            catch (Exception  e) {
                log.error("获取Redis客服数量异常", e);
            }
            log.info("客服账号接待数 {} {} redis已经接待数量 {}", accountItem.getCompanyInfoId(), accountItem.getCustomerServiceAccount(), redisQuantity);

            if (quantityMap == null && redisQuantity < 1) {
                log.info("客服账号接待数 {} {} 当前接待数 {}", accountItem.getCompanyInfoId(), accountItem.getCustomerServiceAccount(), 0);
                minNum = BigInteger.valueOf(0);
                minNumAccountItem = accountItem;
                break;
            } else {
                BigInteger dbQuantity = BigInteger.ZERO;
                if (quantityMap != null && quantityMap.get("quantity") != null) {
                    dbQuantity = (BigInteger) quantityMap.get("quantity");
                }
                int totalQuantity = dbQuantity.intValue() + redisQuantity;
                log.info("客服账号接待数 {} {} 当前接待数 {} redis数量 {} 总数量 {}", accountItem.getCompanyInfoId(), accountItem.getCustomerServiceAccount(), dbQuantity, redisQuantity, dbQuantity.intValue() + redisQuantity);
                if (minNum.intValue() > totalQuantity) {
                    minNum = BigInteger.valueOf(totalQuantity);
                    minNumAccountItem = accountItem;
                }
            }
        }

        log.info("客户排队处理 客服账号接待数：最少接待数客服信息 {} - {} - {}", imChatRequest.getCompanyId(), minNum, JSON.toJSON(minNumAccountItem));
        if (minNumAccountItem != null && minNum != null && minNum.intValue() >= imChatRequest.getLimit()) {
            return null;
        }
        else if (minNumAccountItem != null){
            try {
                String redisKey = "im.quantity." + minNumAccountItem.getCustomerServiceAccount();
                Long value = redisService.increment(redisKey, 2);
                log.info("客服账号接待数 写入Redis数量 {} {} - {}", minNumAccountItem.getCustomerServiceAccount(), value);
            }
            catch (Exception e) {
                log.info("Redis记录客服接待数量写入异常", e);
            }
        }
        return minNumAccountItem;
    }


    public ImOnlineServiceItem getCompanyOnlineCacheAccount(Long companyId, String userId) {
        // 店铺客服列表
        List<ImOnlineServiceItem> onlineServiceItemList = getListByCompanyInfoId(companyId);
        if (ObjectUtils.isEmpty(onlineServiceItemList)) {
            return null;
        }

        Integer limit = customerServiceSettingService.getCompanyAcceptQuantity(companyId);

        // 获取腾讯IM 账号配置
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");

        // 腾讯IM，在线客服账号逻辑处理。APP端请求客服聊天服务，优先分配在线的客服账号提供聊天服务
        // 调用腾讯IM接口，获取在线登录的客户账号
        List<String> onlineAccountList = TencentImCustomerUtil.getOnlineAccount(onlineServiceItemList.stream().map(ImOnlineServiceItem::getCustomerServiceAccount).collect(Collectors.toList()), appId, appKey);

        List<ImOnlineServiceItem> tmpList = new ArrayList<>();
        for (String account : onlineAccountList) {
            for (ImOnlineServiceItem item : onlineServiceItemList) {
                if (account.equals(item.getCustomerServiceAccount()) && ((Integer)0).equals(item.getServiceStatus())) {
                    tmpList.add(item);
                    break;
                }
            }
        }
        log.info("联系商家客服信息 {} - {} - {}", companyId, onlineAccountList, JSON.toJSON(onlineServiceItemList));

        // 如果客服全部不在线
        if (ObjectUtils.isEmpty(tmpList)) {
            // 返回接收离线消息的客服账号
            ImOnlineServiceItem imOnlineServiceItem = offlineCustomerServiceAccount(companyId, onlineServiceItemList);
            if (imOnlineServiceItem != null) {
                imOnlineServiceItem.setOfflineStatue(true);
            }
            else {
                imOnlineServiceItem = getServerAccountByQuantity(companyId, onlineServiceItemList,  Integer.MAX_VALUE);
                if (imOnlineServiceItem != null) {
                    imOnlineServiceItem.setOfflineStatue(true);
                }
            }
            log.info("联系商家客服信息 不在线 {} - {}", JSON.toJSONString(imOnlineServiceItem));
            return imOnlineServiceItem;
        }

        // 赋值为在线客服列表
        onlineServiceItemList = tmpList;
//        String accountRedisKey = "company.online.im.account."+onlineService.getCompanyInfoId();
//        redisService.setString(accountRedisKey, JSON.toJSONString(onlineAccountList), 60);



//        if (!StringUtils.isEmpty(userId)) {
//            /**
//             * 如果7天内，大白鲸APP用户与商家某个客服发起过对话，优先将该大白鲸APP用户继续与该客服发起对话
//             */
//            String cacheAccountKey = "chat.im."+userId+"."+onlineService.getCompanyInfoId();
//            String cacheIMAccount = redisService.getString(cacheAccountKey);
//            if (!StringUtils.isEmpty(cacheIMAccount)) {
//                for (ImOnlineServiceItem item : onlineServiceItemList) {
//                    if (cacheIMAccount.equals(item.getCustomerServiceAccount())) {
//                        return item;
//                    }
//                }
//            }
//        }


        return getServerAccountByQuantity(companyId, onlineServiceItemList,  limit);

//        String redisKey = "store.customer.service.index."+onlineService.getCompanyInfoId();
//        // 店铺客户分流RedisKey，有效期24小时
//        Long index = redisService.increment(redisKey, 24 * 60 * 60);
//
//        // 商家配置多个客服，APP端发起联系客服时，通过Redis缓存有效时间内的请求次数，轮询均衡分流商家多客服在线聊天
//        ImOnlineServiceItem usedImAccountService = onlineServiceItemList.get((int) (index % onlineServiceItemList.size()));
//        if (!StringUtils.isEmpty(userId)) {
//            String cacheAccountKey = "chat.im." + userId + "." + onlineService.getCompanyInfoId();
//            // 缓存大白鲸APP用户与商家某个客服聊天对话账号，缓存时间7天
//            redisService.setString(cacheAccountKey, usedImAccountService.getCustomerServiceAccount(), 7 * 24 * 60 * 60);
//        }
//        return usedImAccountService;
    }

    private ImOnlineServiceItem getServerAccountByQuantity(Long companyId, List<ImOnlineServiceItem> onlineServiceItemList, Integer limit) {
        if (!ObjectUtils.isEmpty(onlineServiceItemList) && onlineServiceItemList.size() > 1) {
            Collections.shuffle(onlineServiceItemList);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Map<String, Object>> statisVoList = customerServiceChatRepository.countCompanyAcceptQuantity(companyId, 0, 0);
        stopWatch.stop();
        log.info("联系商家客服耗时 客服账号接待数统计耗时 {} - {} - {}", companyId, stopWatch.getTotalTimeSeconds(), JSON.toJSON(statisVoList));
        ImOnlineServiceItem minNumAccountItem = null;
        BigInteger minNum = BigInteger.valueOf(99999);
        for (ImOnlineServiceItem accountItem : onlineServiceItemList) {
            Map<String, Object> quantityMap = null;
            for (Map<String, Object> statisVo : statisVoList) {
                if (accountItem.getCustomerServiceAccount().equals(statisVo.get("serverAccount"))) {
                    quantityMap = statisVo;
                    break;
                }
            }
            int redisQuantity = 0;
            try {
                String redisKey = "im.quantity." + accountItem.getCustomerServiceAccount();
                Integer value = redisService.getObj(redisKey, Integer.class);
                if (value != null && value > 0) {
                    redisQuantity = value;
                }
            }
            catch (Exception  e) {
                log.error("获取Redis客服数量异常", e);
            }
            log.info("客服账号接待数 {} {} redis已经接待数量 {}", accountItem.getCompanyInfoId(), accountItem.getCustomerServiceAccount(), redisQuantity);

            if (quantityMap == null && redisQuantity < 1) {
                log.info("客服账号接待数 {} 当前接待数 {}", accountItem.getCustomerServiceAccount(), 0);
                minNum = BigInteger.valueOf(0);
                minNumAccountItem = accountItem;
                break;
            }
            else {
                BigInteger dbQuantity = BigInteger.ZERO;
                if (quantityMap != null) {
                    dbQuantity = (BigInteger) quantityMap.get("quantity");
                }
                int totalQuantity = dbQuantity.intValue() + redisQuantity;
                log.info("客服账号接待数 {} 当前接待数 {} redis数量 {} 总数量 {}", accountItem.getCustomerServiceAccount(), dbQuantity, redisQuantity, dbQuantity.intValue() + redisQuantity);
                if (minNum.intValue() > totalQuantity) {
                    minNum = BigInteger.valueOf(totalQuantity);
                    minNumAccountItem = accountItem;
                }
            }
        }
        if (minNumAccountItem != null && minNum != null && minNum.intValue() >= limit) {
            minNumAccountItem.setWaitInLine(true);
        }
        else if (minNumAccountItem != null){
            try {
                String redisKey = "im.quantity." + minNumAccountItem.getCustomerServiceAccount();
                Long value = redisService.increment(redisKey, 2);
                log.info("客服账号接待数 {} 写入Redis数量 {} - {}", minNumAccountItem.getCompanyInfoId(), minNumAccountItem.getCustomerServiceAccount(), value);
            }
            catch (Exception e) {
                log.info("Redis记录客服接待数量写入异常", e);
            }
        }
        return minNumAccountItem;
    }


    /**
     * 返回接收离线消息的客服账号
     * @return
     */
    private ImOnlineServiceItem offlineCustomerServiceAccount(Long companyId, List<ImOnlineServiceItem> onlineServiceItemList) {
        CustomerServiceSetting customerServiceSetting = customerServiceSettingRepository.findByCompanyInfoIdAndSettingType(companyId, 4);
        /*
         如果公司没有设置离线消息接收账号，默认使用客服管理员账号接收离线消息
         */
        if (customerServiceSetting == null || StringUtils.isEmpty(customerServiceSetting.getContentJson())) {
            for (ImOnlineServiceItem item : onlineServiceItemList) {
                if (item.getManagerFlag() != null && item.getManagerFlag().equals(1)) {
                    return item;
                }
            }
            return null;
        }
        String redisKey = "store.customer.service.index."+companyId;
        // 店铺客户分流RedisKey，有效期24小时
        Long index = redisService.increment(redisKey, 24 * 60 * 60);
        CustomerServiceSettingContentVo contentVo = JSON.parseObject(customerServiceSetting.getContentJson(), CustomerServiceSettingContentVo.class);
        if (!ObjectUtils.isEmpty(contentVo.getOfflineReceiveAccounts())) {
            String account = contentVo.getOfflineReceiveAccounts()[(int) (index % contentVo.getOfflineReceiveAccounts().length)];
            for (ImOnlineServiceItem serviceItem : onlineServiceItemList) {
                if (serviceItem.getCustomerServiceAccount().equals(account)) {
                    return serviceItem;
                }
            }
        }
        return onlineServiceItemList.get((int) (index % onlineServiceItemList.size()));
    }

    /**
     * 根据公司ID，获取一个商家客服账号
     * 	APP端发起客服聊天时，在公司多IM账号中，使用轮询策略分配一个客服给APP段提供IM聊天功能
     * @return
     */
    public ImOnlineServiceItem getCompanyOnlineAccount(ImOnlineService onlineService) {
        return getCompanyOnlineCacheAccount(onlineService.getCompanyInfoId(), null);
    }

    public List<ImOnlineServiceItem> platformImList(ImOnlineServiceModifyRequest onlineServiceListReq) {
        return onlineServiceItemRepository.findPlatformImList(onlineServiceListReq.getCustomerServiceAccount(),String.valueOf(onlineServiceListReq.getCompanyId()));
    }

    public List<ImOnlineServiceItem> getListByCompanyInfoId(Long companyInfo) {
        return onlineServiceItemRepository.findByCompanyInfoIdAndDelFlag(companyInfo, DeleteFlag.NO);
    }

    public boolean updateServiceStatus(ImOnlineServiceItemVO request) {
        List<ImOnlineServiceItem> imOnlineServiceItemList = onlineServiceItemRepository.findByCustomerServiceAccount(request.getCustomerServiceAccount());
        if (ObjectUtils.isEmpty(imOnlineServiceItemList)) {
            return false;
        }

        /**
         * 修改为忙碌状态，如果当前店铺只有1个客服在线，不允许修改
         */
        if (((Integer)2).equals(request.getServiceStatus())) {
            List<ImOnlineServiceItem> onlineServiceItemList = getListByCompanyInfoId(imOnlineServiceItemList.get(0).getCompanyInfoId());
            // 获取腾讯IM 账号配置
            SystemConfigResponse systemConfigResponse =
                    systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                            .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
            SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
            JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
            long appId= Long.valueOf(jsonObject.getString("appId"));
            String appKey= jsonObject.getString("appKey");

            // 腾讯IM，在线客服账号逻辑处理。APP端请求客服聊天服务，优先分配在线的客服账号提供聊天服务
            // 调用腾讯IM接口，获取在线登录的客户账号
            List<String> onlineAccountList = TencentImCustomerUtil.getOnlineAccount(onlineServiceItemList.stream().map(ImOnlineServiceItem::getCustomerServiceAccount).collect(Collectors.toList()), appId, appKey);
            onlineAccountList.remove(request.getCustomerServiceAccount());
            List<ImOnlineServiceItem> allItemList = getListByCompanyInfoId(imOnlineServiceItemList.get(0).getCompanyInfoId());
            allItemList.forEach(item -> {
                if (item.getServiceStatus() != null && !item.getServiceStatus().equals(0)) {
                    onlineAccountList.remove(item.getCustomerServiceAccount());
                }
            });
            if (ObjectUtils.isEmpty(onlineAccountList)) {
                return false;
            }
        }



        imOnlineServiceItemList.forEach(db -> {
            db.setServiceStatus(request.getServiceStatus());
        });
        onlineServiceItemRepository.saveAll(imOnlineServiceItemList);
        return true;
    }

    public ImSignResponse getCustomerServiceAccountByMobile(String mobileNumber) {
        List<String> accountList = onlineServiceItemRepository.findCustomerServiceAccountByMobile(mobileNumber);
        log.info("根据手机号码 {} 查询IM客服账号 {}", mobileNumber, JSON.toJSONString(accountList));
        if (ObjectUtils.isEmpty(accountList)) {
            return new ImSignResponse();
        }
        log.info("根据手机号码 {} 使用的IM客服账号 {}", mobileNumber, accountList.get(0));
        ImSignResponse imSignResponse = new ImSignResponse();
        imSignResponse.setImAccount(accountList.get(0));

        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
        String appKey= jsonObject.getString("appKey");
        long appId= jsonObject.getLong("appId");
        imSignResponse.setImSign(TencentImCustomerUtil.getTxCloudUserSig(imSignResponse.getImAccount(),appId,appKey));
        return imSignResponse;
    }

    public List<Long> getHaveCustomerServiceStoreIds() {
        return onlineServiceItemRepository.findHaveCustomerServiceStoreIds();
    }

    public ImOnlineServiceItem getAccountByCustomerServiceAccountAndCompanyInfoId(String customerServiceAccount, Long companyInfoId, Integer limit) {
        List<ImOnlineServiceItem> resultList = onlineServiceItemRepository.findByCustomerServiceAccountAndCompanyInfoId(customerServiceAccount, companyInfoId);
        if (ObjectUtils.isEmpty(resultList)) {
            return null;
        }
        ImOnlineServiceItem imOnlineServiceItem = resultList.get(0);
        Integer quantity = customerServiceChatRepository.countAccountQuantityByServerAccount(customerServiceAccount);
        if (quantity != null && quantity >= limit) {
            imOnlineServiceItem.setWaitInLine(true);
        }
        log.info("查询客服接待数量 {} - {}", customerServiceAccount, quantity);
        return imOnlineServiceItem;
    }

    public ImOnlineServiceItem initStoreIMAccount(OnlineServiceListRequest request, ImOnlineService imOnlineService) {
        if (StringUtils.isEmpty(request.getStoreMasterEmployeeMobile())) {
            return null;
        }
        String customerAccount = request.getCompanyInfoId()+"_"+request.getStoreMasterEmployeeMobile();
        List<ImOnlineServiceItem> itemList = onlineServiceItemRepository.findByCustomerServiceAccount(customerAccount);
        if (!ObjectUtils.isEmpty(itemList)) {
            return itemList.get(0);
        }
        ImOnlineServiceItem imOnlineServiceItem = new ImOnlineServiceItem();
        imOnlineServiceItem.setCompanyInfoId(request.getCompanyInfoId());
        imOnlineServiceItem.setImOnlineServiceId(imOnlineService.getImOnlineServiceId());
        imOnlineServiceItem.setCustomerServiceName(request.getStoreName()+"客服01号");
        imOnlineServiceItem.setCustomerServiceAccount(customerAccount);
        imOnlineServiceItem.setDelFlag(DeleteFlag.NO);
        imOnlineServiceItem.setCreateTime(LocalDateTime.now());
        imOnlineServiceItem.setUpdateTime(LocalDateTime.now());
        imOnlineServiceItem.setOperatePerson("default");
        imOnlineServiceItem.setStoreId(request.getRealStoreId());
        imOnlineServiceItem.setEmployeeId(request.getStoreMasterEmployeeId());
        imOnlineServiceItem.setPhoneNo(request.getStoreMasterEmployeeMobile());
        imOnlineServiceItem.setManagerFlag(1);
        imOnlineServiceItem.setServiceStatus(0);
        onlineServiceItemRepository.save(imOnlineServiceItem);

        try {
            List<ImOnlineServiceItem> list = new ArrayList<>();
            list.add(imOnlineServiceItem);
            addImByAccount(new ArrayList<>(list), request.getStoreLogo());
        }
        catch (Exception e) {
            log.info("初始化商家客户异常", e);
        }
        return imOnlineServiceItem;
    }

    public ImOnlineServiceItem getByCustomerServiceAccount(String customerServiceAccount) {
        List<ImOnlineServiceItem> list = onlineServiceItemRepository.findByCustomerServiceAccount(customerServiceAccount);
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public ImOnlineServiceItem getByCompanyInfoIdAndPhoneNo(Long companyInfoId, String phoneNo) {
        List<ImOnlineServiceItem> list = onlineServiceItemRepository.findByCompanyInfoIdAndPhoneNo(companyInfoId, phoneNo);
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public List<ImOnlineServiceItemVO> getListByCompanyId(Long companyInfoId) {
        List<ImOnlineServiceItem> list = onlineServiceItemRepository.findByCompanyInfoId(companyInfoId);
        if (list == null) {
            list = new ArrayList<>();
        }
        return KsBeanUtil.convert(list, ImOnlineServiceItemVO.class);
    }
}
