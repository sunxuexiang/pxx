package com.wanmi.sbc.setting.provider.impl.systemstoreconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemstoreconfig.SystemStoreConfigProvider;
import com.wanmi.sbc.setting.api.request.systemstoreconfig.SystemStoreConfigModifyRequest;
import com.wanmi.sbc.setting.api.request.systemstoreconfig.SystemStoreConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemstoreconfig.SystemStoreConfigListResponse;
import com.wanmi.sbc.setting.api.response.systemstoreconfig.SystemStoreConfigResponse;
import com.wanmi.sbc.setting.bean.vo.SystemStoreConfigVO;
import com.wanmi.sbc.setting.config.ConfigService;
import com.wanmi.sbc.setting.systemstoreconfig.model.root.SystemStoreConfig;
import com.wanmi.sbc.setting.systemstoreconfig.service.SystemStoreConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @desc  
 * @author shiy  2023/7/3 18:10
*/
@RestController
public class SystemStoreConfigController implements SystemStoreConfigProvider {
    @Autowired
    ConfigService configService;
    @Autowired
    private SystemStoreConfigService systemStoreConfigService;

    @Override
    public BaseResponse<SystemStoreConfigListResponse> findByStoreIdAndConfigKey(SystemStoreConfigQueryRequest request) {
        List<SystemStoreConfigVO> storeConfigVOList = systemStoreConfigService.getVoList(SystemStoreConfigQueryRequest.builder().storeId(request.getStoreId()).configKey(request.getConfigKey()).delFlag(DeleteFlag.NO).build());
        SystemStoreConfigListResponse response = new SystemStoreConfigListResponse();
        response.setSystemStoreConfigVOList(storeConfigVOList);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<SystemStoreConfigResponse> findByStoreIdAndConfigKeyAndConfigTypeAndDelFlag(SystemStoreConfigQueryRequest request) {
        List<SystemStoreConfigVO> storeConfigVOList = systemStoreConfigService.getVoList(SystemStoreConfigQueryRequest.builder().storeId(request.getStoreId()).configKey(request.getConfigKey()).configType(request.getConfigType()).delFlag(DeleteFlag.NO).build());
        SystemStoreConfigResponse response = new SystemStoreConfigResponse();
        if(CollectionUtils.isNotEmpty(storeConfigVOList)){
            response.setSystemStoreConfigVO(storeConfigVOList.get(0));
            return BaseResponse.success(response);
        }else{
            return BaseResponse.success(null);
        }
    }

    @Override
    public BaseResponse<SystemStoreConfigListResponse> listByStoreId(SystemStoreConfigQueryRequest request) {
        List<SystemStoreConfigVO> storeConfigVOList = systemStoreConfigService.getVoList(SystemStoreConfigQueryRequest.builder().storeId(request.getStoreId()).delFlag(DeleteFlag.NO).build());
        SystemStoreConfigListResponse response = new SystemStoreConfigListResponse();
        response.setSystemStoreConfigVOList(storeConfigVOList);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse add(SystemStoreConfigModifyRequest request) {
        List<SystemStoreConfig> systemStoreConfigList = systemStoreConfigService.list(SystemStoreConfigQueryRequest.builder().storeId(request.getStoreId()).configKey(request.getConfigKey()).configType(request.getConfigType()).build());
        Assert.isTrue(CollectionUtils.isEmpty(systemStoreConfigList),"当前配置在商家中已存在");
        SystemStoreConfig systemStoreConfig = KsBeanUtil.copyPropertiesThird(request,SystemStoreConfig.class);
        systemStoreConfig.setCreateTime(LocalDateTime.now());
        systemStoreConfig.setUpdateTime(LocalDateTime.now());
        systemStoreConfigService.add(systemStoreConfig);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse update(SystemStoreConfigModifyRequest request) {
        if(request.getId()!=null){
            SystemStoreConfig systemStoreConfig = KsBeanUtil.copyPropertiesThird(request,SystemStoreConfig.class);
            systemStoreConfig.setUpdateTime(LocalDateTime.now());
            systemStoreConfigService.modify(systemStoreConfig);
        }else{
            add(request);
        }
        return BaseResponse.SUCCESSFUL();
    }
}
