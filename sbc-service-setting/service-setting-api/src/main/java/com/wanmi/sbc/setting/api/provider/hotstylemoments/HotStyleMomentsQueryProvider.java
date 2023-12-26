package com.wanmi.sbc.setting.api.provider.hotstylemoments;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsCheckTimeRequest;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsGetByIdRequest;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsQueryRequest;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsListResponse;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsPageResponse;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description: 爆款时刻查询接口
 * @Author: XinJiang
 * @Date: 2022/5/9 21:45
 */
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "HotStyleMomentsQueryProvider")
public interface HotStyleMomentsQueryProvider {

    /**
     * 根据id获取爆款时刻
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/get-by-id")
    BaseResponse<HotStyleMomentsResponse> getById(@RequestBody @Valid HotStyleMomentsGetByIdRequest request);

    /**
     * 根据条件查询列表数据
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/get-list")
    BaseResponse<HotStyleMomentsListResponse> getList(@RequestBody @Valid HotStyleMomentsQueryRequest request);

    /**
     * 根据条件查询分页数据
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/get-page")
    BaseResponse<HotStyleMomentsPageResponse> getPage(@RequestBody @Valid HotStyleMomentsQueryRequest request);

    /**
     * 校验活动时间是否重复
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/check-time")
    BaseResponse<HotStyleMomentsListResponse> checkTime(@RequestBody @Valid HotStyleMomentsCheckTimeRequest request);
}
