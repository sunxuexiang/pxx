package com.wanmi.sbc.live.provider.impl.rule;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.rule.LiveRuleProvider;
import com.wanmi.sbc.live.api.request.rule.LiveRuleAddRequest;
import com.wanmi.sbc.live.api.request.rule.LiveRuleInfoRequest;
import com.wanmi.sbc.live.rule.model.root.LiveRule;
import com.wanmi.sbc.live.rule.service.LiveRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class LiveRuleController implements LiveRuleProvider {
    @Autowired
    private LiveRuleService liveRuleService;
    @Override
    public BaseResponse createRule(LiveRuleAddRequest ruleAddRequest) {
        liveRuleService.createRule(ruleAddRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse getRuleInfo(LiveRuleInfoRequest ruleInfoRequest) {
        LiveRule liveRule=liveRuleService.getRuleInfo(ruleInfoRequest);
        return BaseResponse.success(liveRule);
    }
}
