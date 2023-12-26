package com.wanmi.sbc.navigationconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.navigationconfig.NavigationConfigProvider;
import com.wanmi.sbc.setting.api.provider.navigationconfig.NavigationConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigDelByIdRequest;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigListRequest;
import com.wanmi.sbc.setting.api.request.navigationconfig.NavigationConfigModifyRequest;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigListResponse;
import com.wanmi.sbc.setting.api.response.navigationconfig.NavigationConfigModifyResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(description = "导航配置管理API", tags = "NavigationConfigController")
@RestController
@RequestMapping(value = "/navigationconfig")
public class NavigationConfigController {

    @Autowired
    private NavigationConfigQueryProvider navigationConfigQueryProvider;

    @Autowired
    private NavigationConfigProvider navigationConfigProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "列表查询导航配置")
    @PostMapping("/list")
    public BaseResponse<NavigationConfigListResponse> getList(@RequestBody @Valid NavigationConfigListRequest listReq) {
        return navigationConfigQueryProvider.list(listReq);
    }

    @ApiOperation(value = "修改导航配置")
    @PutMapping("/modify")
    public BaseResponse<NavigationConfigModifyResponse> modify(@RequestBody @Valid NavigationConfigModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("导航配置管理", "修改导航配置", "操作成功");
        return navigationConfigProvider.modify(modifyReq);
    }

    @ApiOperation(value = "删除导航配置")
    @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "navId", value = "导航配置ID", required = true)
    @DeleteMapping("/{navId}")
    public BaseResponse delete(@PathVariable Integer navId) {
        operateLogMQUtil.convertAndSend("导航配置管理", "删除导航配置", "操作成功：导航配置ID" + navId);
        return navigationConfigProvider.delete(NavigationConfigDelByIdRequest.builder().id(navId).build());
    }
}
