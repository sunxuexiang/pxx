package com.wanmi.sbc.livestream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.bag.LiveBagProvider;
import com.wanmi.sbc.live.api.request.bag.LiveBagAddRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagListRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagModifyRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagPageRequest;

import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Api(description = "新版直播间福袋", tags = "LiveStreamRoomBagController")
@RestController
@RequestMapping(value = "/liveStreamRoomBag")
public class LiveStreamRoomBagController {

    @Autowired
    private LiveBagProvider liveBagProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "直播间福袋修改")
    @PostMapping("/modify")
    public BaseResponse modify(@RequestBody @Valid LiveBagModifyRequest modifyRequest) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播间福袋", "直播间福袋修改", "直播间福袋修改:福袋名称" + (Objects.nonNull(modifyRequest) ? modifyRequest.getBagName() : ""));
        return liveBagProvider.modify(modifyRequest);
    }

    @ApiOperation(value = "直播间福袋信息")
    @PostMapping("/getInfo")
    public BaseResponse getInfo(@RequestBody  @Valid LiveBagModifyRequest modifyRequest) {
        return liveBagProvider.getInfo(modifyRequest.getLiveBagId());
    }

    @ApiOperation(value = "直播间福袋列表")
    @PostMapping("/getPage")
    public BaseResponse getPage(@RequestBody  LiveBagPageRequest pageRequest) {
        BaseResponse baseResponse = liveBagProvider.getPage(pageRequest);
        return baseResponse;
    }

    @ApiOperation(value = "直播间福袋新增")
    @PostMapping("/add")
    public BaseResponse add(@RequestBody @Valid LiveBagAddRequest liveRoomAddRequest) {
        liveBagProvider.add(liveRoomAddRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播间福袋", "直播间福袋新增", "直播间福袋新增:福袋名称" + (Objects.nonNull(liveRoomAddRequest) ? liveRoomAddRequest.getBagName() : ""));
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "直播福袋记录表记录列表")
    @PostMapping("/liveBagRecordList")
    public BaseResponse liveBagRecordList(@RequestBody LiveBagListRequest request) {
        BaseResponse baseResponse = liveBagProvider.liveBagRecordList(request);
        return baseResponse;
    }
}
