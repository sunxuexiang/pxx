package com.wanmi.sbc.setting.provider.impl.saas;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.saas.SaasSettingQueryProvider;
import com.wanmi.sbc.setting.saas.SaasServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: songhanlin
 * @Date: Created In 19:31 2020/1/15
 * @Description: Saas配置QueryContoller
 */
@RestController
@Validated
public class SaasSettingQueryController implements SaasSettingQueryProvider {

    @Autowired
    private SaasServiceUtils saasServiceUtils;

    @Override
    public BaseResponse<Boolean> queryStatus() {
        return BaseResponse.success(saasServiceUtils.getSaasStatus());
    }
}
