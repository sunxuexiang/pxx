package com.wanmi.sbc.live.provider.impl.activity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.activity.service.LiveStreamActivityService;
import com.wanmi.sbc.live.api.provider.activity.LiveStreamActivityQueryProvider;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityListRequest;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityInfoResponse;
import com.wanmi.sbc.live.api.response.activity.LiveStreamActivityListResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamActivityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>直播活动查询服务接口实现</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@RestController
@Validated
public class LiveStreamActivityQueryController implements LiveStreamActivityQueryProvider {
    @Autowired
    private LiveStreamActivityService activityService;
    @Override
    public BaseResponse<LiveStreamActivityListResponse> list(LiveStreamActivityListRequest request) {
        List<LiveStreamActivityVO> activityVOList=activityService.getActivity(request);
        return BaseResponse.success(new LiveStreamActivityListResponse(activityVOList));
    }

    @Override
    public BaseResponse<LiveStreamActivityInfoResponse> activityInfo(@RequestBody LiveStreamActivityInfoRequest infoRequest) {
        LiveStreamActivityInfoResponse activityInfoResponse=new LiveStreamActivityInfoResponse();
        activityInfoResponse.setStreamActivityVO(activityService.getActivityInfo(infoRequest));
        return BaseResponse.success(activityInfoResponse);
    }
}
