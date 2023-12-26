package com.wanmi.sbc.setting.provider.impl.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishInfoQueryProvider;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishUserQueryProvider;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishUserRequest;
import com.wanmi.sbc.setting.api.response.publishInfo.PublishInfoResponse;
import com.wanmi.sbc.setting.api.response.publishInfo.PublishUserResponse;
import com.wanmi.sbc.setting.bean.vo.PublishUserVO;
import com.wanmi.sbc.setting.publishInfo.root.PublishUser;
import com.wanmi.sbc.setting.publishInfo.service.PublishInfoService;
import com.wanmi.sbc.setting.publishInfo.service.PublishUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 信息发布用户查询API
 * @Author: lwp
 * @Version: 1.0
 */
@Slf4j
@RestController
@Validated
public class PublishUserQueryController implements PublishUserQueryProvider {

    @Autowired
    private PublishUserService publishUserService;


    @Override
    public BaseResponse<PublishUserResponse> getPublishUser(@RequestBody PublishUserRequest request) {
        log.info("用户请求：{}",request);
        PublishUser publishUser = publishUserService.getPublishUser(request);
        log.info("用户信息：{}",publishUser);
        if(publishUser == null){
            return BaseResponse.FAILED();
        }
        PublishUserResponse publishUserResponse = new PublishUserResponse();
        PublishUserVO publishUserVO = new PublishUserVO();
        KsBeanUtil.copyPropertiesThird(publishUser, publishUserVO);
        publishUserResponse.setPublishUserVO(publishUserVO);
        return BaseResponse.success(publishUserResponse);
    }
}
