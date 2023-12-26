package com.wanmi.sbc.setting.provider.impl.retailDeliveryconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.retaildeliveryconfig.RetailDeveryConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.request.retaildeliveryconfig.RetailDeliveryConfigRequest;
import com.wanmi.sbc.setting.api.response.packingconfig.PackingConfigResponse;
import com.wanmi.sbc.setting.api.response.retaildeliveryconfig.RetailDeliveryConfigResponse;
import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import com.wanmi.sbc.setting.bean.vo.RetailDeliverConfigVO;
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
public class RetailDelivertQueryController implements RetailDeveryConfigQueryProvider {


    @Autowired
    private RetailDeliverConifgService retailDeliverConifgService;



    @Override
    public BaseResponse<RetailDeliveryConfigResponse> list() {
        RetailDeliveryConfigItem retailDeliveryConfigItem = retailDeliverConifgService.list();
        return BaseResponse.success(RetailDeliveryConfigResponse.builder().retailDeliverConfigVO(KsBeanUtil.convert(retailDeliveryConfigItem, RetailDeliverConfigVO.class)).build());

    }
}

