package com.wanmi.sbc.gatherboxset;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.gatherboxset.GatherBoxSetProvider;
import com.wanmi.sbc.setting.api.request.gatherboxset.GatherBoxSetAddRequest;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetBannerResponse;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetInfoResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "GatherBoxSetController", description = "凑箱设置服务API")
@RestController
@RequestMapping("/gatherBoxSet")
public class GatherBoxSetController {
    @Autowired
    private GatherBoxSetProvider gatherBoxSetProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "更新凑箱设置")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse updateGatherBoxSet(@Valid @RequestBody GatherBoxSetAddRequest gatherBoxSetAddRequest) {
        operateLogMQUtil.convertAndSend("凑箱设置服务", "更新凑箱设置", "更新凑箱设置");
        return gatherBoxSetProvider.add(gatherBoxSetAddRequest);
    }

    @ApiOperation(value = "获取凑箱设置")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public BaseResponse<GatherBoxSetInfoResponse> getGatherBoxSet(){
        return gatherBoxSetProvider.getGatherBoxSetInfo();
    }

    @ApiOperation(value = "获取散批banner")
    @RequestMapping(value = "/bulkBanner", method = RequestMethod.GET)
    public BaseResponse<GatherBoxSetBannerResponse> bulkBanner(){
        return gatherBoxSetProvider.getBulkBanner();
    }

}
