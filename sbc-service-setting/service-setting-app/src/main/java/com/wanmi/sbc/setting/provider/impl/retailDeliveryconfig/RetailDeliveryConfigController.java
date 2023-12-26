package com.wanmi.sbc.setting.provider.impl.retailDeliveryconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigProvider;
import com.wanmi.sbc.setting.api.provider.retaildeliveryconfig.RetailDeveryConfigProvider;
import com.wanmi.sbc.setting.api.provider.retaildeliveryconfig.RetailDeveryConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.request.retaildeliveryconfig.RetailDeliveryConfigRequest;
import com.wanmi.sbc.setting.packingconfig.model.root.PackingConfigItem;
import com.wanmi.sbc.setting.packingconfig.service.PackingConfigItemService;
import com.wanmi.sbc.setting.retaildeliveryconfig.model.root.RetailDeliveryConfigItem;
import com.wanmi.sbc.setting.retaildeliveryconfig.service.RetailDeliverConifgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@Validated
public class RetailDeliveryConfigController implements RetailDeveryConfigProvider {


    @Autowired
    private RetailDeliverConifgService retailDeliverConifgService;

    @Override
    public BaseResponse modify(@Valid RetailDeliveryConfigRequest retailDeliveryConfigRequest) {
        retailDeliverConifgService.save(KsBeanUtil.convert(retailDeliveryConfigRequest.getRetailDeliverConfigVO(), RetailDeliveryConfigItem.class));
        return BaseResponse.SUCCESSFUL();
    }
}

