package com.wanmi.sbc.live.provider.impl.activity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.activity.service.LiveStreamActivityService;
import com.wanmi.sbc.live.api.provider.activity.LiveStreamActivityProvider;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityAddRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityModifyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>直播活动保存服务接口实现</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@RestController
@Validated
public class LiveStreamActivityController implements LiveStreamActivityProvider {
    @Autowired
    private LiveStreamActivityService activityService;

    @Override
    public BaseResponse supplier(@RequestBody LiveStreamActivityAddRequest supplierAddReq) {
        activityService.addActivity(supplierAddReq);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(@RequestBody LiveStreamActivityModifyRequest supplierModifyReq) {
        activityService.modifyActivity(supplierModifyReq);
        return BaseResponse.SUCCESSFUL();
    }
}
