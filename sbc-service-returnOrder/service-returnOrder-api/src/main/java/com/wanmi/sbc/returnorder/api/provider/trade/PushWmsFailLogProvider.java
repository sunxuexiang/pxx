package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.PushFailLogRequest;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author lm
 * @date 2022/11/19 16:06
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnPushWmsFailLogProvider")
public interface PushWmsFailLogProvider {

    @PostMapping("/returnOrder/${application.order.version}/pushWmsFailLog/findAllTrade")
    BaseResponse<List<TradeVO>> findAllTrade();

    @PostMapping("/returnOrder/${application.order.version}/pushWmsFailLog/savePushFailLog")
    BaseResponse savePushFailLog(@RequestBody PushFailLogRequest pushFailLogRequest);
}
