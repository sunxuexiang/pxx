package com.wanmi.sbc.live.api.provider.activity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.activity.LiveBagInfoRequest;
import com.wanmi.sbc.live.api.request.activity.LiveStreamActivityAddRequest;
import com.wanmi.sbc.live.api.request.stream.BagAppRequest;
import com.wanmi.sbc.live.api.response.activity.LiveBagLogInfoResponse;
import com.wanmi.sbc.live.api.response.bag.LiveBagInfoResponse;
import com.wanmi.sbc.live.api.response.stream.BagAppResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>直播福袋APP页面</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveBagLogProvider")
public interface LiveBagLogProvider {


    /**
     * 参与福袋活动
     * @param mobileAddReq
     * @return
     */
    @PostMapping("/liveBag/${application.live.version}/liveBag/joinBag")
    BaseResponse joinBag(@RequestBody BagAppRequest mobileAddReq);

    /**
     * 福袋开奖信息
     * @param mobileAddReq
     * @return
     */
    @PostMapping("/liveBag/${application.live.version}/liveBag/openBag")
    BaseResponse<BagAppResponse> openBag(@RequestBody BagAppRequest mobileAddReq);
    /**
     * app 用户福袋详细信息
     * @param bagInfoRequest
     * @return
     */
    @PostMapping("/liveBag/${application.live.version}/liveBag/getBagInfo")
    BaseResponse<LiveBagLogInfoResponse> getBagInfo(@RequestBody @Valid LiveBagInfoRequest bagInfoRequest);

    /**
     * app 福袋发放信息
     * @param bagInfoRequest
     * @return
     */
    @PostMapping("/liveBag/${application.live.version}/liveBag/getPushBagInfo")
    BaseResponse<LiveBagLogInfoResponse> getPushBagInfo(@RequestBody @Valid LiveBagInfoRequest bagInfoRequest);
}
