package com.wanmi.sbc.live.api.provider.bag;

import javax.validation.Valid;

import com.wanmi.sbc.live.api.request.bag.LiveBagListRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.bag.LiveBagListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.bag.LiveBagAddRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagModifyRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagPageRequest;
import com.wanmi.sbc.live.api.response.bag.LiveBagInfoResponse;
import com.wanmi.sbc.live.bean.vo.LiveBagVO;

/**
 * <p>福袋服务Provider</p>
 */
@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveBagProvider")
public interface LiveBagProvider {

    /**
     * 保存福袋
     * @param addRequest
     * @return
     */
    @PostMapping("/bag/${application.live.version}/liveBag/add")
    BaseResponse add(@Valid @RequestBody LiveBagAddRequest addRequest);

    /**
     * 更新福袋
     * @param modifyRequest
     * @return
     */
    @PostMapping("/bag/${application.live.version}/liveBag/modify")
    BaseResponse modify(@Valid @RequestBody LiveBagModifyRequest modifyRequest);


    /**
     * 根据ID查询福袋
     * @param liveBagId
     * @return
     */
    @PostMapping("/bag/${application.live.version}/liveBag/getInfo")
    BaseResponse<LiveBagInfoResponse> getInfo( @RequestBody Long liveBagId);

    /**
     * 查询福袋分页
     * @param pageRequest
     * @return
     */
    @PostMapping("/bag/${application.live.version}/liveBag/getPage")
    BaseResponse getPage(@Valid @RequestBody LiveBagPageRequest pageRequest);

    /**
     * 查询福袋直播记录
     * @param request
     * @return
     */
    @PostMapping("/bag/${application.live.version}/liveBag/getListLog")
    BaseResponse liveBagRecordList(@RequestBody LiveBagListRequest request);


    /**
     * 直播间福袋列表
     * @param request
     * @return
     */
    @PostMapping("/bag/${application.live.version}/liveBag/liveBagRoomList")
    BaseResponse<LiveBagListResponse> liveBagRoomList(@RequestBody LiveBagListRequest request);
}