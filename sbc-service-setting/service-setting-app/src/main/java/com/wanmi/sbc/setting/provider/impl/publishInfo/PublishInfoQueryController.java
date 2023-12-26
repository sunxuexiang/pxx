package com.wanmi.sbc.setting.provider.impl.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishInfoQueryProvider;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishInfoRequest;
import com.wanmi.sbc.setting.api.response.publishInfo.PublishInfoResponse;
import com.wanmi.sbc.setting.bean.vo.PublishInfoVO;
import com.wanmi.sbc.setting.publishInfo.service.PublishInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 信息发布查询API
 * @Author: lwp
 * @Version: 1.0
 */
@RestController
@Validated
public class PublishInfoQueryController implements PublishInfoQueryProvider {

    @Autowired
    private PublishInfoService publishInfoService;


    @Override
    public BaseResponse<PublishInfoResponse> getPublishInfo(@RequestBody PublishInfoRequest request) {
        MicroServicePage<PublishInfoVO> microPage = new MicroServicePage<>(publishInfoService.page(request), request.getPageable());
        PublishInfoResponse publishInfoResponse = new PublishInfoResponse(microPage);
        return BaseResponse.success(publishInfoResponse);
    }
}
