package com.wanmi.sbc.marketing;


import com.wanmi.sbc.marketing.api.provider.market.MarketingProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wanmi.sbc.common.base.BaseResponse;

@Api(tags = "MarketingController", description = "营销服务 API")
@RestController
@RequestMapping("/marketing-support")
@Slf4j
public class MarketingSupportController {
    @Autowired
    private MarketingProvider marketingProvider;

    @ApiOperation(value = "营销活动中的数据同步到Redis缓存中")
    @PostMapping(value = "/sync-cache-from-persistence")
    public BaseResponse syncCacheFromPersistence(){
        log.info("Start MarketingSupportController.syncCacheFromPersistence \n");
        marketingProvider.syncCacheFromPersistence();
        return BaseResponse.SUCCESSFUL();
    }
}
