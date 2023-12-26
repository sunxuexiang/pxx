package com.wanmi.sbc.setting.provider.impl.videomanagement;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoSearchRecordProvider;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementPageRequest;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoSearchRecordResponse;
import com.wanmi.sbc.setting.videomanagement.service.VideoSearchRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class VideoSearchRecordController implements VideoSearchRecordProvider {

    @Autowired
    private VideoSearchRecordService videoSearchRecordService;

    @Override
    public BaseResponse<MicroServicePage<VideoSearchRecordResponse>> getSearchHistory(VideoManagementPageRequest request) {
        MicroServicePage<VideoSearchRecordResponse> microServicePage = videoSearchRecordService.getUserSearchList(request);
        return BaseResponse.success(microServicePage);
    }

}
