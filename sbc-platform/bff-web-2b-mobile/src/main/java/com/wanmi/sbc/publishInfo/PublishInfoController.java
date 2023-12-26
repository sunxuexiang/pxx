package com.wanmi.sbc.publishInfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishInfoProvider;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishInfoQueryProvider;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishInfoRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by lwp on 2323/10/18.
 */
@Api(tags = "PublishInfoController", description = "信息发布API")
@RestController
@RequestMapping("/publishInfo")
public class PublishInfoController {

    @Autowired
    private PublishInfoProvider publishInfoProvider;

    @Autowired
    private PublishInfoQueryProvider publishInfoQueryProvider;

    /**
     * 添加发布信息
     * @return BaseResponse
     */
    @ApiOperation(value = "添加发布信息")
    @RequestMapping(value = "/addInfo", method = RequestMethod.POST)
    public BaseResponse addInfo(@RequestBody PublishInfoRequest request) {
        publishInfoProvider.addPublishInfo(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查看发布信息
     * @return
     */
    @ApiOperation(value = "查看发布信息")
    @RequestMapping(value = "/viewInfo", method = RequestMethod.POST)
    public BaseResponse viewInfo(@RequestBody PublishInfoRequest request){
        return publishInfoQueryProvider.getPublishInfo(request);
    }
}
