package com.wanmi.sbc.setting.iosappversionconfig.service;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigPageRequest;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigPageResponse;
import com.wanmi.sbc.setting.bean.vo.IosAppVersionConfigVO;
import com.wanmi.sbc.setting.iosappversionconfig.model.root.IosAppVersionConfig;
import com.wanmi.sbc.setting.iosappversionconfig.repository.IosAppVersionConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: ios版本配置信息逻辑
 * @author: jiangxin
 * @create: 2021-09-23 15:59
 */
@Service
public class IosAppVersionConfigService {

    @Autowired
    private IosAppVersionConfigRepository iosAppVersionConfigRepository;

    @Transactional
    public IosAppVersionConfig add(IosAppVersionConfig request){
        /**
         * 禅道ID:5926 230927
         * [期望]
         * 不存在的版本号，会新增记录。
         * 已存在的版本号，则为更新记录。
         */
        iosAppVersionConfigRepository.findByVersionNoOrderByBuildNoDesc(request.getVersionNo()).stream()
                .findFirst()
                .ifPresent(iosAppVersionConfig -> {
                    request.setId(iosAppVersionConfig.getId());
                });
        return iosAppVersionConfigRepository.save(request);
    }

    public IosAppVersionConfig getById(Long id){
        return iosAppVersionConfigRepository.getById(id);
    }

    public IosAppVersionConfig getIosAppVersionConfigByVersionNoAndBuildNo(String versionNo,Long buildNo){
        return iosAppVersionConfigRepository.getIosAppVersionConfigByVersionNoAndBuildNo(versionNo,buildNo);
    }

    public String getLastVersionNo(){
        return iosAppVersionConfigRepository.getLastVersionNo();
    }

    public IosAppVersionConfigPageResponse page(IosAppVersionConfigPageRequest request){
        Page<IosAppVersionConfig> iosAppVersionConfigs = iosAppVersionConfigRepository.findAll(request.getPageRequest());
        List<IosAppVersionConfigVO> iosAppVersionConfigVOList = KsBeanUtil.convert(iosAppVersionConfigs.getContent(),IosAppVersionConfigVO.class);
        MicroServicePage<IosAppVersionConfigVO> page = new MicroServicePage<>(iosAppVersionConfigVOList, request.getPageable(), iosAppVersionConfigs.getTotalElements());
        return IosAppVersionConfigPageResponse.builder().iosVersionPages(page).build();
    }

}
