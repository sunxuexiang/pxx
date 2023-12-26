package com.wanmi.sbc.setting.userguidelines;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.request.AboutUsModifyRequest;
import com.wanmi.sbc.setting.api.request.AppShareSettingModifyRequest;
import com.wanmi.sbc.setting.api.request.AppUpgradeModifyRequest;
import com.wanmi.sbc.setting.api.request.UserGuidelinesConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.AppShareSettingGetResponse;
import com.wanmi.sbc.setting.api.response.AppUpgradeGetResponse;
import com.wanmi.sbc.setting.api.response.UserGuidelinesConfigQueryResponse;
import com.wanmi.sbc.setting.authority.repository.MenuInfoRepository;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.config.Config;
import com.wanmi.sbc.setting.config.ConfigRepository;
import com.wanmi.sbc.setting.util.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * @Author: gaomuwei
 * @Date: Created In 上午11:02 2018/11/16
 * @Description: 移动端设置service
 */
@Service
@Transactional(timeout = 10)
public class UserGuidelinesSettingService {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private MenuInfoRepository menuInfoRepository;

    /**
     * 修改用户须知
     */
    @Transactional
    public void modifyUserGuidelines(UserGuidelinesConfigModifyRequest userGuidelinesConfigModifyRequest) {
        configRepository.updateStatusByTypeAndConfigKey(ConfigType.USER_GUIDELINES_CONFIG.toValue(), ConfigKey.GOODS_SETTING
                .toValue(), userGuidelinesConfigModifyRequest.getStatus(), null);

        menuInfoRepository.modifyMenuInfo(userGuidelinesConfigModifyRequest.getStatus()==0?1:0,"用户须知设置");
    }

    /**
     * 查询用户须知
     */
    @Transactional
    public UserGuidelinesConfigQueryResponse getUserGuidelines() {
        Config config = configRepository.findByConfigTypeAndDelFlag(ConfigType.USER_GUIDELINES_CONFIG.toValue(), DeleteFlag.NO);
        if (Objects.isNull(config)) {
            config = new Config();
            config.setConfigKey(ConfigKey.GOODS_SETTING.toValue());
            config.setConfigType(ConfigType.USER_GUIDELINES_CONFIG.toValue());
            config.setConfigName("用户须知配置");
            config.setContext("");
            config.setDelFlag(DeleteFlag.NO);
            config.setStatus(1);
            configRepository.save(config);
        }
        return new UserGuidelinesConfigQueryResponse(config.getStatus());
    }

}
