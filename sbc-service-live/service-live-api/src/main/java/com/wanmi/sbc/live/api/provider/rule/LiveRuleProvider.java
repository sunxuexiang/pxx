package com.wanmi.sbc.live.api.provider.rule;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.request.room.LiveRoomAddRequest;
import com.wanmi.sbc.live.api.request.rule.LiveRuleAddRequest;
import com.wanmi.sbc.live.api.request.rule.LiveRuleInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${application.live.name}", url="${feign.url.live:#{null}}",contextId = "LiveRuleProvider")
public interface LiveRuleProvider {

    /**
     * 创建人数规则
     * @param ruleAddRequest
     * @return
     */
    @PostMapping("/live/${application.live.version}/liveRule/createRule")
    BaseResponse createRule(@RequestBody LiveRuleAddRequest ruleAddRequest);


    @PostMapping("/live/${application.live.version}/liveRule/getRuleInfo")
    BaseResponse getRuleInfo(@RequestBody LiveRuleInfoRequest ruleInfoRequest);
}
