package com.wanmi.sbc.setting.provider.impl.packingconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceByIdRequest;
import com.wanmi.sbc.setting.api.request.onlineservice.OnlineServiceListRequest;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceByIdResponse;
import com.wanmi.sbc.setting.api.response.onlineservice.OnlineServiceListResponse;
import com.wanmi.sbc.setting.api.response.packingconfig.PackingConfigResponse;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import com.wanmi.sbc.setting.onlineservice.model.root.OnlineService;
import com.wanmi.sbc.setting.onlineservice.service.OnlineServiceService;
import com.wanmi.sbc.setting.onlineserviceitem.model.root.OnlineServiceItem;
import com.wanmi.sbc.setting.onlineserviceitem.service.OnlineServiceItemService;
import com.wanmi.sbc.setting.packingconfig.model.root.PackingConfigItem;
import com.wanmi.sbc.setting.packingconfig.service.PackingConfigItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Validated
public class PackingConfigQueryController implements PackingConfigQueryProvider {


    @Autowired
    private PackingConfigItemService packingConfigItemService;

    @Override
    public BaseResponse<PackingConfigResponse> list() {
        PackingConfigItem packingConfigItem = packingConfigItemService.list();
        return BaseResponse.success(PackingConfigResponse.builder().packingConfigVO(KsBeanUtil.convert(packingConfigItem, PackingConfigVO.class)).build());
    }
}

