package com.wanmi.sbc.setting.provider.impl.packingconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigProvider;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.response.packingconfig.PackingConfigResponse;
import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import com.wanmi.sbc.setting.packingconfig.model.root.PackingConfigItem;
import com.wanmi.sbc.setting.packingconfig.service.PackingConfigItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@Validated
public class PackingConfigController implements PackingConfigProvider {


    @Autowired
    private PackingConfigItemService packingConfigItemService;

    @Override
    public BaseResponse modify(@Valid PackingConfigRequest packingConfigRequest) {
        packingConfigItemService.save(KsBeanUtil.convert(packingConfigRequest.getPackingConfigVO(), PackingConfigItem.class));
        return BaseResponse.SUCCESSFUL();
    }
}

