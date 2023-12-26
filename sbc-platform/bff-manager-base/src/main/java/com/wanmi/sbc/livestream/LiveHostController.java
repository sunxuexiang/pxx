package com.wanmi.sbc.livestream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.host.LiveHostProvider;
import com.wanmi.sbc.live.api.request.host.*;
import com.wanmi.sbc.live.api.response.stream.LiveStreamPageResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

@Api(description = "直播主播管理API", tags = "LiveHostController")
@RestController
@RequestMapping(value = "/liveHost")
public class LiveHostController {

    @Autowired
    private LiveHostProvider liveHostProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "主播分页查询")
    @PostMapping("/getHostPage")
    public BaseResponse<LiveStreamPageResponse> getPage(@RequestBody @Valid LiveHostPageRequest request) {
        return liveHostProvider.getPage(request);
    }

    @ApiOperation(value = "查询所有已启用的直播账户")
    @PostMapping("/getEnableCustomerAccountList")
    public BaseResponse getEnableCustomerAccountList() {
        return liveHostProvider.getEnableCustomerAccountList();
    }

    @ApiOperation(value = "根据账户信息查询主播账户信息")
    @PostMapping("/getInfoByCustomer")
    public BaseResponse getInfoByCustomer(@RequestBody @Valid LiveHostInfoRequest request) {
        return liveHostProvider.getInfoByCustomer(request);
    }

    @ApiOperation(value = "主播添加")
    @PostMapping("/hostAdd")
    public BaseResponse hostAdd(@RequestBody @Valid LiveHostAddRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播主播管理", "主播添加", "主播添加：主播姓名" + (Objects.nonNull(request) ? request.getHostName() : ""));
        return liveHostProvider.add(request);
    }

    @ApiOperation(value = "主播修改")
    @PostMapping("/hostModify")
    public BaseResponse hostModify(@RequestBody @Valid LiveHostModifyRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播主播管理", "主播修改", "主播修改：主播姓名" + (Objects.nonNull(request) ? request.getHostName() : ""));
        return liveHostProvider.modify(request);
    }

    @ApiOperation(value = "主播离职")
    @PostMapping("/hostLeave")
    public BaseResponse hostLeave(@RequestBody @Valid LiveHostLeaveRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播主播管理", "主播离职", "主播离职：主播ID" + (Objects.nonNull(request) ? request.getHostId() : ""));
        return liveHostProvider.leave(request);
    }

    @ApiOperation(value = "主播启用")
    @PostMapping("/hostEnable")
    public BaseResponse hostEnable(@RequestBody @Valid LiveHostEnableRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播主播管理", "主播启用", "主播启用：主播ID" + (Objects.nonNull(request) ? request.getHostId() : ""));
        return liveHostProvider.enable(request);
    }

    @ApiOperation(value = "主播删除")
    @PostMapping("/hostDelete")
    public BaseResponse hostDelete(@RequestBody @Valid LiveHostDeleteRequest request) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播主播管理", "主播删除", "主播删除：主播ID" + (Objects.nonNull(request) ? request.getHostId() : ""));
        return liveHostProvider.delete(request);
    }
}
