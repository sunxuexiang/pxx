package com.wanmi.sbc.onlineservice;


import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.TencentImLogProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.TencentImLogRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "TencentImLogController", description = "腾讯IM记录日志接口")
@RestController
@RequestMapping("/tencentImLog")
@Slf4j
@Validated
public class TencentImLogController {

    @Autowired
    private TencentImLogProvider tencentImLogProvider;

    @PostMapping("/add")
    public BaseResponse add (@RequestBody TencentImLogRequest request) {
        log.info("腾讯IM打印APP日志 {}", JSON.toJSONString(request));
        return tencentImLogProvider.add(request);
    }
}
