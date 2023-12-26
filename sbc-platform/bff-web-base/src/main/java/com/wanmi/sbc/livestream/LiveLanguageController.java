package com.wanmi.sbc.livestream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.stream.LiveLanguageProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/liveLanguage")
@Api(tags = "LiveLanguageController", description = "S2B web公用-直播")
@Slf4j
public class LiveLanguageController {

    @Autowired
    private LiveLanguageProvider liveLanguageProvider;

    @ApiOperation(value = "直播间发送短语列表")
    @RequestMapping(value = "/list")
    public BaseResponse getList () {
        return liveLanguageProvider.getList();
    }
}
