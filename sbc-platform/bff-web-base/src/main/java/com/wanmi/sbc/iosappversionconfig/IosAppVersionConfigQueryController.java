package com.wanmi.sbc.iosappversionconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.iosappversionconfig.IosAppVersionConfigProvider;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Ios版本配置管理请求Controller
 * @author: jiangxin
 * @create: 2021-09-15 17:13
 */
@Api(description = "IOS版本配置信息API", tags = "IosAppVersionInfo")
@RestController
@RequestMapping(value = "/iosAppVersionInfo")
public class IosAppVersionConfigQueryController {

    @Autowired
    IosAppVersionConfigProvider iosAppVersionConfigProvider;

    @ApiOperation(value = "查询版本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionNo",value = "版本号" , dataType = "String", required = true),
            @ApiImplicitParam(name = "buildNo",value = "构建号" , dataType = "Long", required = true)
    })
    @RequestMapping(value = "/getVersionInfo/{versionNo}/{buildNo}",method = RequestMethod.GET)
    public BaseResponse<IosAppVersionConfigResponse> getVersionInfo(@PathVariable String versionNo, @PathVariable Long buildNo){
        return iosAppVersionConfigProvider.getVersionInfo(versionNo, buildNo);
    }

}
