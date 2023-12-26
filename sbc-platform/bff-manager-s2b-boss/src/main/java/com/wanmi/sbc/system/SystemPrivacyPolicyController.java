package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.systemprivacypolicy.SystemPrivacyPolicyQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemprivacypolicy.SystemPrivacyPolicySaveProvider;
import com.wanmi.sbc.setting.api.request.systemprivacypolicy.*;
import com.wanmi.sbc.setting.api.response.systemprivacypolicy.*;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(description = "隐私政策管理API", tags = "SystemPrivacyPolicyController")
@RestController
@RequestMapping(value = "/boss/systemprivacypolicy")
public class SystemPrivacyPolicyController {

    @Autowired
    private SystemPrivacyPolicyQueryProvider systemPrivacyPolicyQueryProvider;

    @Autowired
    private SystemPrivacyPolicySaveProvider systemPrivacyPolicySaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "查询隐私政策")
    @GetMapping
    public BaseResponse<SystemPrivacyPolicyResponse> query() {
        return systemPrivacyPolicyQueryProvider.querySystemPrivacyPolicy();
    }


    @ApiOperation(value = "编辑/新增隐私政策")
    @PostMapping
    public BaseResponse modify(@RequestBody @Valid SystemPrivacyPolicyRequest modifyReq) {
        modifyReq.setOperator(commonUtil.getOperator());
        operateLogMQUtil.convertAndSend("设置", "隐私政策管理", "编辑/新增隐私政策");
        return systemPrivacyPolicySaveProvider.modifyOrAdd(modifyReq);
    }


}
