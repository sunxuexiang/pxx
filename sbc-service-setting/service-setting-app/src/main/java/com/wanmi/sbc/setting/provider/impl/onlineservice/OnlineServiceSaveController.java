package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CustomerServiceType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceSaveProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceSwitchUpdateRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.SobotServiceRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceModifyRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.*;
import com.wanmi.sbc.setting.imonlineservice.repository.CommonChatMsgRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CommonChatMsg;
import com.wanmi.sbc.setting.imonlineservice.root.ImOnlineService;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceChatService;
import com.wanmi.sbc.setting.imonlineservice.service.ImOnlineServiceService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceSwitch;
import com.wanmi.sbc.setting.onlineservice.model.root.OnlineService;
import com.wanmi.sbc.setting.onlineservice.service.CustomerServiceSwitchService;
import com.wanmi.sbc.setting.onlineservice.service.OnlineServiceService;
import com.wanmi.sbc.setting.onlineserviceitem.model.root.OnlineServiceItem;
import com.wanmi.sbc.setting.onlineserviceitem.service.OnlineServiceItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>onlineService保存服务接口实现</p>
 *
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@RestController
@Validated
@Slf4j

public class OnlineServiceSaveController implements OnlineServiceSaveProvider {
    @Autowired
    private OnlineServiceService onlineServiceService;

    @Autowired
    private OnlineServiceItemService onlineServiceItemService;

    @Autowired
    private ImOnlineServiceService imOnlineServiceService;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    @Autowired
    private CustomerServiceSwitchService customerServiceSwitchService;

    @Autowired
    private CommonChatMsgRepository commonChatMsgRepository;

    @Autowired
    private CustomerServiceChatService customerServiceChatService;

    @Override
    public BaseResponse modify(@RequestBody @Valid OnlineServiceModifyRequest onlineServiceModifyRequest) {
        // 在线客服设置
        OnlineServiceVO onlineServerVo = onlineServiceModifyRequest.getQqOnlineServerRop();
        // 客服座席列表
        List<OnlineServiceItemVO> onlineServerItemVoList = onlineServiceModifyRequest.getQqOnlineServerItemRopList();

        if (Objects.isNull(onlineServerVo)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if(Objects.equals(onlineServerVo.getServerStatus(), DefaultFlag.YES)
                && CollectionUtils.isEmpty(onlineServerItemVoList)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (onlineServerItemVoList.size() > 10){
            throw new SbcRuntimeException(SettingErrorCode.ONLINE_SERVER_MAX_ERROR);
        }

        //客服设置
        OnlineService onlineServer = onlineServiceService.getById(onlineServerVo.getOnlineServiceId());
        onlineServer.setServerStatus(onlineServerVo.getServerStatus());
        onlineServer.setServiceTitle(onlineServerVo.getServiceTitle());
        onlineServer.setEffectivePc(onlineServerVo.getEffectivePc());
        onlineServer.setEffectiveApp(onlineServerVo.getEffectiveApp());
        onlineServer.setEffectiveMobile(onlineServerVo.getEffectiveMobile());
        onlineServer.setUpdateTime(LocalDateTime.now());

        //删除客服座席
        onlineServiceItemService.deleteByOnlineServiceId(onlineServer.getOnlineServiceId());
        if (CollectionUtils.isNotEmpty(onlineServerItemVoList)) {
            List<String> serverAccount = onlineServerItemVoList.stream().map(vo -> vo.getCustomerServiceAccount()).collect(Collectors.toList());
            //查询重复的QQ号
            List<OnlineServiceItem> duplicateItem = onlineServiceItemService.checkDuplicateAccount(serverAccount);
            if (CollectionUtils.isNotEmpty(duplicateItem)) {
                List<String> dupAccounts = duplicateItem.stream().map(item -> item.getCustomerServiceAccount()).collect(Collectors.toList());
                String dupAccount = StringUtils.join(dupAccounts, ",");
                throw new SbcRuntimeException(SettingErrorCode.ONLINE_SERVER_ACCOUNT_ALREADY_EXIST, new Object[]{dupAccount});
            }

            List<OnlineServiceItem> onlineServerItemList = onlineServiceItemService.getOnlineServerItemList(onlineServerItemVoList);
            //保存客服座席
            onlineServiceItemService.save(onlineServerItemList);
        }

        //保存在线客服
        onlineServiceService.add(onlineServer);

        return BaseResponse.SUCCESSFUL();
    }

    @Transactional
    @Override
    public BaseResponse imModify(@RequestBody @Valid ImOnlineServiceModifyRequest imOnlineServiceModifyRequest) {
        // 在线客服设置
        ImOnlineServiceVO onlineServerVo = imOnlineServiceModifyRequest.getImOnlineServerRop();
        // 客服座席列表
        List<ImOnlineServiceItemVO> onlineServerItemVoList = imOnlineServiceModifyRequest.getImOnlineServerItemRopList();

        if (Objects.isNull(onlineServerVo)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if(Objects.equals(onlineServerVo.getServerStatus(), DefaultFlag.YES)
                && CollectionUtils.isEmpty(onlineServerItemVoList)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (onlineServerItemVoList.size() > 10){
            throw new SbcRuntimeException(SettingErrorCode.ONLINE_SERVER_MAX_ERROR);
        }

        //客服设置
        ImOnlineService onlineServer = imOnlineServiceService.getById(onlineServerVo.getImOnlineServiceId());
        onlineServer.setServerStatus(onlineServerVo.getServerStatus());
        onlineServer.setServiceTitle(onlineServerVo.getServiceTitle());
        onlineServer.setEffectivePc(onlineServerVo.getEffectivePc());
        onlineServer.setEffectiveApp(onlineServerVo.getEffectiveApp());
        onlineServer.setEffectiveMobile(onlineServerVo.getEffectiveMobile());
        onlineServer.setUpdateTime(LocalDateTime.now());

        /**
         * 20230729 zhouzhenguo
         * 修改客服总开关
         */
        CustomerServiceSwitchUpdateRequest updateRequest = CustomerServiceSwitchUpdateRequest.builder()
                .customerServiceType(CustomerServiceType.TENCENT_IM)
                .companyId(imOnlineServiceModifyRequest.getCompanyId())
                .switchStatus(onlineServer.getServerStatus().toValue()).build();
        updateCustomerServiceSwitch(updateRequest);

        //删除客服座席
        imOnlineServiceItemService.deleteByOnlineServiceId(onlineServer.getImOnlineServiceId());

        if (CollectionUtils.isNotEmpty(onlineServerItemVoList)) {
            List<String> serverAccount = onlineServerItemVoList.stream().map(vo -> vo.getCustomerServiceAccount()).collect(Collectors.toList());
            //查询重复
            List<ImOnlineServiceItem> duplicateItem = imOnlineServiceItemService.checkDuplicateAccount(serverAccount);
            if (CollectionUtils.isNotEmpty(duplicateItem)) {
                List<String> dupAccounts = duplicateItem.stream().map(item -> item.getCustomerServiceAccount()).collect(Collectors.toList());
                String dupAccount = StringUtils.join(dupAccounts, ",");
                throw new SbcRuntimeException(SettingErrorCode.ONLINE_SERVER_ACCOUNT_ALREADY_EXIST, new Object[]{dupAccount});
            }
            List<ImOnlineServiceItem> onlineServerItemList = imOnlineServiceItemService.getOnlineServerItemList(onlineServerItemVoList);

            //List<ImOnlineServiceItem> serviceItems = imOnlineServiceItemService.list(onlineServerVo.getImOnlineServiceId());

            onlineServerItemList.forEach(l->{
                // 如果IM账号为空，不保存
                if (StringUtils.isEmpty(l.getCustomerServiceAccount())) {
                    return;
                }
                l.setImOnlineServiceId(onlineServerVo.getImOnlineServiceId());
                l.setCompanyInfoId(imOnlineServiceModifyRequest.getCompanyId());
                l.setUpdateTime(LocalDateTime.now());
                // 保存店铺ID
                l.setStoreId(imOnlineServiceModifyRequest.getStoreId());
                l.setEmployeeId(l.getEmployeeId());
            });

            //保存客服座席
            imOnlineServiceItemService.save(onlineServerItemList);

            //注册账号
            imOnlineServiceItemService.addImByAccount(onlineServerItemList,imOnlineServiceModifyRequest.getLogo());
        }

        //保存在线客服
        imOnlineServiceService.add(onlineServer);

        imOnlineServiceModifyRequest.setImOnlineServiceId(onlineServer.getImOnlineServiceId());
        updateCommonChatMessage(imOnlineServiceModifyRequest);

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 常用聊天语入库
     * @param imOnlineServiceModifyRequest
     */
    private void updateCommonChatMessage(ImOnlineServiceModifyRequest imOnlineServiceModifyRequest) {
        commonChatMsgRepository.deleteByImOnlineServiceId(imOnlineServiceModifyRequest.getImOnlineServiceId().longValue());
        if (imOnlineServiceModifyRequest.getAutoMsg() != null) {
            CommonChatMsg autoMessage = KsBeanUtil.convert(imOnlineServiceModifyRequest.getAutoMsg(), CommonChatMsg.class);
            autoMessage.setImOnlineServiceId(imOnlineServiceModifyRequest.getImOnlineServiceId().longValue());
            autoMessage.setMsgType(0);
            autoMessage.setSortNum(1);
            autoMessage.setStoreId(imOnlineServiceModifyRequest.getStoreId());
            autoMessage.setCompanyInfoId(imOnlineServiceModifyRequest.getCompanyId());
            commonChatMsgRepository.save(autoMessage);
        }
        if (ObjectUtils.isEmpty(imOnlineServiceModifyRequest.getCommonMsgList())) {
            return;
        }
        for (int i=0; i<imOnlineServiceModifyRequest.getCommonMsgList().size(); i++) {
            CommonChatMsg message = KsBeanUtil.convert(imOnlineServiceModifyRequest.getCommonMsgList().get(i), CommonChatMsg.class);
            message.setImOnlineServiceId(imOnlineServiceModifyRequest.getImOnlineServiceId().longValue());
            message.setSortNum(i+1);
            message.setMsgType(1);
            message.setStoreId(imOnlineServiceModifyRequest.getStoreId());
            message.setCompanyInfoId(imOnlineServiceModifyRequest.getCompanyId());
            commonChatMsgRepository.save(message);
        }
    }


    @Override
    public BaseResponse<String> addImAccount(@RequestBody @Valid ImOnlineServiceSignRequest vo) {
        long entryTime = System.currentTimeMillis();
        //添加IM账号信息
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        if (systemConfigResponse.getSystemConfigVOList().isEmpty()){
            return BaseResponse.error("请开启IM在线客服系统");
        }
        //添加IM账号信息
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        String appKey= jsonObject.getString("appKey");
        long appId= Long.valueOf(jsonObject.getString("appId"));
        TencentImCustomerUtil.accountImport(vo.getCustomerServiceAccount(),vo.getCustomerServiceName(),
                vo.getUserLogo(), TencentImCustomerUtil.getTxCloudUserSig(vo.getCustomerServiceAccount(),appId,appKey),appId,appKey);
        long exitTime = System.currentTimeMillis();
        log.info("添加腾讯IM账号耗时 addImAccount {}", (exitTime - entryTime) / 1000d);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商家自定义客服服务类型
     * @param request
     * @return
     */
    public BaseResponse updateCustomerServiceSwitch(CustomerServiceSwitchUpdateRequest request) {
        Long companyId = request.getCompanyId();
        CustomerServiceType customerServiceType = request.getCustomerServiceType();
        Integer switchType = request.getSwitchStatus();
        CustomerServiceSwitch customerServiceSwitch = customerServiceSwitchService.getByCompanyId(companyId);
        // 等于null，还没有设置过在线客服开关
        if (customerServiceSwitch == null) {
            customerServiceSwitchService.add(companyId, switchType.equals(1) ? customerServiceType.getType() : CustomerServiceType.CLOSE.getType());
            return BaseResponse.SUCCESSFUL();
        }
        // 开启智齿客服
        if (switchType.equals(1)) {
            // 如果当前商家还没有开启任何类型的客服服务
            if (customerServiceSwitch.getServiceSwitchType().equals(CustomerServiceType.CLOSE.getType())) {
                customerServiceSwitch.setServiceSwitchType(customerServiceType.getType());
                customerServiceSwitchService.update(customerServiceSwitch);
            }
            else if (!customerServiceSwitch.getServiceSwitchType().equals(customerServiceType.getType())) {
                throw new SbcRuntimeException("K-99999", "请先关闭其它类型客服");
            }
        }
        else {
            if (customerServiceSwitch.getServiceSwitchType().equals(customerServiceType.getType())) {
                customerServiceSwitch.setServiceSwitchType(CustomerServiceType.CLOSE.getType());
                customerServiceSwitchService.update(customerServiceSwitch);
            }
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateServiceStatus(ImOnlineServiceItemVO request) {
        log.info("客服切换在线状态 请求参数 {}", JSON.toJSONString(request));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean isSuccess = imOnlineServiceItemService.updateServiceStatus(request);
        if (!isSuccess) {
            throw new SbcRuntimeException("当前没有其他客服在线，不允许修改");
        }
        if (request.getServiceStatus().equals(1)) {
            customerServiceChatService.customerServiceOffline(request);
        }
        stopWatch.stop();
        log.info("客服切换在线状态总耗时 {}", stopWatch.getTotalTimeSeconds());
        return BaseResponse.success("");
    }

    @Override
    public BaseResponse initStoreIMCustomerService(OnlineServiceListRequest initRequest) {
        imOnlineServiceService.initStoreIMCustomerAccount(initRequest);
        return BaseResponse.success("");
    }


}

