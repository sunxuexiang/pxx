package com.wanmi.sbc.gatherboxset;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.gatherboxset.GatherBoxSetProvider;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetBannerResponse;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetInfoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "GatherBoxSetController", description = "凑箱设置服务API")
@RestController
@RequestMapping("/gatherBoxSet")
public class GatherBoxSetController {
    @Autowired
    private GatherBoxSetProvider gatherBoxSetProvider;

    @ApiOperation(value = "获取散批banner")
    @RequestMapping(value = "/bulkBanner", method = RequestMethod.GET)
    public BaseResponse<GatherBoxSetBannerResponse> bulkBanner(){
        return gatherBoxSetProvider.getBulkBanner();
    }

    @ApiOperation(value = "获取凑箱设置信息")
    @RequestMapping(value = "/gatherBoxSet/info", method = RequestMethod.GET)
    public BaseResponse<GatherBoxSetInfoResponse> getGatherBoxSetInfo(){
        return gatherBoxSetProvider.getGatherBoxSetInfo();
    }
}
