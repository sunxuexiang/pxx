package com.wanmi.sbc.setting.imonlineservice.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceVO;
import com.wanmi.sbc.setting.imonlineservice.repository.ImOnlineServiceRepository;
import com.wanmi.sbc.setting.imonlineservice.root.ImOnlineService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.onlineservice.repository.CustomerServiceSwitchRepository;
import com.wanmi.sbc.setting.onlineservice.service.CustomerServiceSwitchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
/**
 * <p>ImOnlineServiceService业务逻辑</p>
 * @author SGY
 * @date 2023-06-05 16:10:28
 */
@Slf4j
@Service("ImOnlineServiceService")
public class ImOnlineServiceService {
    @Autowired
    private ImOnlineServiceRepository onlineServiceRepository;

    @Lazy
    @Autowired
    private CustomerServiceSwitchService customerServiceSwitchService;

    @Lazy
    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;

    /**
     * 新增onlineService
     *
     * @author sgy
     */
    @Transactional
    public ImOnlineService add(ImOnlineService entity) {
        onlineServiceRepository.save(entity);
        return entity;
    }

    /**
     * 通过店铺id 查询在线客服
     *
     * @author sgy
     */
    public ImOnlineService getByCompanyInfoId(Long storeId) {
        ImOnlineService onlineServer = onlineServiceRepository.findByCompanyInfoIdAndDelFlag(storeId, DeleteFlag.NO);
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
    public ImOnlineService getById(Integer onlineServiceId) {
        return onlineServiceRepository.getOne(onlineServiceId);
    }

    /**
     * 初始化一条客服开关设置记录
     *
     * @param storeId
     * @return
     */
    @Transactional
    public ImOnlineService saveOnlineServer(Long storeId) {
        ImOnlineService onlineServer = new ImOnlineService();
        onlineServer.setCompanyInfoId(storeId);
        onlineServer.setServerStatus(DefaultFlag.YES);
        onlineServer.setEffectiveApp(DefaultFlag.YES);
        onlineServer.setEffectiveMobile(DefaultFlag.YES);
        onlineServer.setEffectivePc(DefaultFlag.YES);
        onlineServer.setDelFlag(DeleteFlag.NO);
        onlineServer.setCreateTime(LocalDateTime.now());

        return onlineServiceRepository.saveAndFlush(onlineServer);
    }

    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public ImOnlineServiceVO wrapperVo(ImOnlineService onlineService) {
        if (onlineService != null) {
            ImOnlineServiceVO onlineServiceVO = new ImOnlineServiceVO();
            KsBeanUtil.copyPropertiesThird(onlineService, onlineServiceVO);

            return onlineServiceVO;
        }
        return null;
    }

    public boolean initStoreIMCustomerAccount(OnlineServiceListRequest request) {
        try {
            customerServiceSwitchService.initStoreIMSwitch(request);
            List<ImOnlineServiceItem> serviceItemList = imOnlineServiceItemService.getListByCompanyInfoId(request.getCompanyInfoId());
            if (ObjectUtils.isEmpty(serviceItemList)) {
                ImOnlineService imOnlineService = getByCompanyInfoId(request.getCompanyInfoId());
                imOnlineServiceItemService.initStoreIMAccount(request, imOnlineService);
            }
        } catch (Exception e) {
            log.error("初始化商家客服账号异常", e);
            return false;
        }
        return true;
    }
}
