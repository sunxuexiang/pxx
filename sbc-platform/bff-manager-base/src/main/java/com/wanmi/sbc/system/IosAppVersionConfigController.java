package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.setting.api.provider.iosappversionconfig.IosAppVersionConfigProvider;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigAddRequest;
import com.wanmi.sbc.setting.api.request.iosappversionconfig.IosAppVersionConfigPageRequest;
import com.wanmi.sbc.setting.api.response.iosappversionconfig.IosAppVersionConfigPageResponse;
import com.wanmi.sbc.setting.bean.vo.IosAppVersionConfigVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @Description: Ios版本配置管理Controller
 * @author: jiangxin
 * @create: 2021-09-15 17:13
 */
@Api(description = "IOS版本基本配置API", tags = "IosAppVersionConfig")
@RestController
@RequestMapping(value = "/iosAppVersionConfig")
public class IosAppVersionConfigController {

    @Autowired
    IosAppVersionConfigProvider iosAppVersionConfigProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 新增/编辑版本配置信息
     * @param addRequest
     * @return
     */
    @ApiOperation(value = "保存配置版本号")
    @PostMapping(value = "/add")
    public BaseResponse add(@RequestBody IosAppVersionConfigAddRequest addRequest){
        operateLogMQUtil.convertAndSend("IOS版本基本配置", "保存配置版本号", "保存配置版本号：版本号" + (Objects.nonNull(addRequest) ? addRequest.getVersionNo() : ""));
        return iosAppVersionConfigProvider.add(addRequest);
    }

    /**
     * 查看版本详细信息
     * @param id
     * @return
     */
    @ApiOperation(value = "版本配置信息详情")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "ios配置信息Id", required = true)
    @GetMapping(value = "/getVersionInfoById/{id}")
    public BaseResponse<IosAppVersionConfigVO> getById(@PathVariable Long id){
        return iosAppVersionConfigProvider.getVersionInfoById(id);
    }

    /**
     * 版本配置信息列表
     * @param request
     * @return
     */
    @ApiOperation(value = "版本配置信息列表")
    @PostMapping(value = "/list")
    public BaseResponse<IosAppVersionConfigPageResponse> list(@RequestBody @Valid IosAppVersionConfigPageRequest request){
        request.putSort("buildNo", SortType.DESC.toValue());
        return iosAppVersionConfigProvider.getListVersions(request);
    }

}
