package com.wanmi.sbc.livestream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.rule.LiveRuleProvider;
import com.wanmi.sbc.live.api.request.rule.LiveRuleAddRequest;
import com.wanmi.sbc.live.api.request.rule.LiveRuleInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
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

@Api(description = "新版直播规则设置管理API", tags = "LiveRuleController")
@RestController
@RequestMapping(value = "/liveStream")
public class LiveRuleController {
    @Autowired
    private LiveRuleProvider liveRuleProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "规则保存")
    @PostMapping("/saveRule")
    public BaseResponse saveRule(@RequestBody @Valid LiveRuleAddRequest addReq) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("新版直播规则设置管理", "规则保存", "规则保存");
        return liveRuleProvider.createRule(addReq);
    }

    @ApiOperation(value = "规则获取")
    @PostMapping("/ruleInfo")
    public BaseResponse getRuleInfo(@RequestBody @Valid LiveRuleInfoRequest infoRequest){
        return liveRuleProvider.getRuleInfo(infoRequest);
    }
}
