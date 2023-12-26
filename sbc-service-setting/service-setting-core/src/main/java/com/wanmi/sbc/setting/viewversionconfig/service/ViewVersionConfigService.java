package com.wanmi.sbc.setting.viewversionconfig.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.bean.vo.ViewVersionConfigVO;
import com.wanmi.sbc.setting.viewversionconfig.model.root.ViewVersionConfig;
import com.wanmi.sbc.setting.viewversionconfig.repository.ViewVersionConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description: ios版本配置信息逻辑
 * @author: jiangxin
 * @create: 2021-09-23 15:59
 */
@Slf4j
@Service
public class ViewVersionConfigService {

    @Autowired
    private ViewVersionConfigRepository viewVersionConfigRepository;


    public ViewVersionConfigVO getViewVersion(String systemType, String currentVersion) {
        if (StringUtils.isBlank(currentVersion)) {
            ViewVersionConfig viewVersionConfig = viewVersionConfigRepository.getLastViewVersionConfig(systemType);
            if (Objects.nonNull(viewVersionConfig)) {
                return KsBeanUtil.convert(viewVersionConfig, ViewVersionConfigVO.class);
            }
        } else {
            List<ViewVersionConfig> all = viewVersionConfigRepository.findLastVersionBySystemTypeAndCurrentVersion(systemType, currentVersion);
            if (all.size() == 1) {
                return KsBeanUtil.convert(all.get(0), ViewVersionConfigVO.class);
            } else if (all.size() > 1) {
                ViewVersionConfig systemVersionConfig = all.get(0);
                for (ViewVersionConfig version : all) {
                    if (!version.getVersionNo().equals(currentVersion) && version.getUpdatePromptStatus() == 3) {
                        systemVersionConfig.setUpdatePromptStatus(3);
                        break;
                    }
                }
                return KsBeanUtil.convert(systemVersionConfig, ViewVersionConfigVO.class);
            }
        }
        return null;
    }
}
