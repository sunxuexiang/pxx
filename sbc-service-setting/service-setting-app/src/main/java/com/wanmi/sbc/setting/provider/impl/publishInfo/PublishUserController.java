package com.wanmi.sbc.setting.provider.impl.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishUserProvider;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishUserRequest;
import com.wanmi.sbc.setting.publishInfo.root.PublishUser;
import com.wanmi.sbc.setting.publishInfo.service.PublishUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


/**
 * 信息发布用户API
 * @Author: lwp
 * @Version: 1.0
 */
@RestController
@Validated
public class PublishUserController implements PublishUserProvider {

    @Autowired
    private PublishUserService publishUserService;

    @Override
    public BaseResponse addPublishUser(PublishUserRequest request) {
        PublishUser publishUser = new PublishUser();
        KsBeanUtil.copyPropertiesThird(request, publishUser);
        publishUser.setDelFlag(DeleteFlag.NO);
        publishUserService.add(publishUser);
        return BaseResponse.SUCCESSFUL();
    }
}
