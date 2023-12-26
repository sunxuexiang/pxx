package com.wanmi.sbc.order.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.trade.PushFailLogRequest;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/19 16:06
 */
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "PushWmsFailLogProvider")
public interface PushWmsFailLogProvider {

    @PostMapping("/order/${application.order.version}/pushWmsFailLog/findAllTrade")
    BaseResponse<List<TradeVO>> findAllTrade();

    @PostMapping("/order/${application.order.version}/pushWmsFailLog/savePushFailLog")
    BaseResponse savePushFailLog(@RequestBody PushFailLogRequest pushFailLogRequest);
}
