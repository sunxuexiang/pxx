package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.systemcancellationplicy.SystemCancellationPolicyQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemcancellationplicy.SystemCancellationPolicySaveProvider;
import com.wanmi.sbc.setting.api.request.systemcancellationpolicy.SystemCancellationPolicyRequest;
import com.wanmi.sbc.setting.api.request.systemprivacypolicy.SystemPrivacyPolicyRequest;
import com.wanmi.sbc.setting.api.response.systemcancellationpolicy.SystemCancellationPolicyResponse;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.SystemPrivacyPolicyResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @description: 注销账户政策管理API
 * @author: XinJiang
 * @time: 2021/12/7 11:54
 */
@Api(description = "注销账户政策管理API", tags = "SystemCancellationPolicyController")
@RestController
@RequestMapping(value = "/boss/systemcancellationpolicy")
public class SystemCancellationPolicyController {

    @Autowired
    private SystemCancellationPolicySaveProvider systemCancellationPolicySaveProvider;

    @Autowired
    private SystemCancellationPolicyQueryProvider systemCancellationPolicyQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "查询注销政策")
    @GetMapping
    public BaseResponse<SystemCancellationPolicyResponse> query() {
        return systemCancellationPolicyQueryProvider.querySystemCancellationPolicy();
    }

    @ApiOperation(value = "编辑/新增注销政策")
    @PostMapping
    public BaseResponse modify(@RequestBody @Valid SystemCancellationPolicyRequest modifyReq) {
        modifyReq.setOperator(commonUtil.getOperator());
        operateLogMQUtil.convertAndSend("设置", "注销账户政策管理", "编辑/新增注销政策");
        return systemCancellationPolicySaveProvider.modifyOrAdd(modifyReq);
    }
}
