package com.wanmi.sbc.setting.provider.impl.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishInfoProvider;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishInfoRequest;
import com.wanmi.sbc.setting.publishInfo.root.PublishInfo;
import com.wanmi.sbc.setting.publishInfo.service.PublishInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 信息发布API
 * @Author: lwp
 * @Version: 1.0
 */
@RestController
@Validated
public class PublishInfoController implements PublishInfoProvider {

    @Autowired
    private PublishInfoService publishInfoService;

    @Override
    public BaseResponse addPublishInfo(@RequestBody PublishInfoRequest request) {
        PublishInfo publishInfo = new PublishInfo();
        KsBeanUtil.copyPropertiesThird(request, publishInfo);
        publishInfoService.add(publishInfo);
        return BaseResponse.SUCCESSFUL();
    }
}
