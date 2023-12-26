package com.wanmi.sbc.setting.provider.impl.viewversionconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.viewversionconfig.ViewVersionConfigProvider;
import com.wanmi.sbc.setting.bean.vo.ViewVersionConfigVO;
import com.wanmi.sbc.setting.viewversionconfig.service.ViewVersionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板升级
 */
@RestController
@Validated
public class ViewVersionConfigController implements ViewVersionConfigProvider {

    @Autowired
    private ViewVersionConfigService viewVersionConfigService;

    @Override
    public BaseResponse<ViewVersionConfigVO> getViewVersion(String systemType, String currentVersion) {
        return BaseResponse.success(viewVersionConfigService.getViewVersion(systemType, currentVersion));
    }
}
