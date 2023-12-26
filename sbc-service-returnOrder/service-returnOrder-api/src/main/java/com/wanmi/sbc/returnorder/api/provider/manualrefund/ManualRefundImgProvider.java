package com.wanmi.sbc.returnorder.api.provider.manualrefund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundImgRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnManualRefundImgProvider")
public interface ManualRefundImgProvider {

    @PostMapping("/returnOrder/${application.order.version}/manualrefundImg/addManualRefundImgs")
    BaseResponse addManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest);

    @PostMapping("/returnOrder/${application.order.version}/manualrefundImg/deleteManualRefundImgs")
    BaseResponse deleteManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest);

    @PostMapping("/returnOrder/${application.order.version}/manualrefundImg/listManualRefundImgs")
    BaseResponse listManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest);

    /**
     * @description  初始化历史订单退款凭证状态
     * @author  shiy
     * @date    2023/3/17 9:20
     * @params  [com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundImgRequest]
     * @return  com.wanmi.sbc.common.base.BaseResponse
    */
    @PostMapping("/returnOrder/${application.order.version}/manualrefundImg/initOrderImgsFlag")
    BaseResponse initOrderImgsFlag(@RequestBody ManualRefundImgRequest manualRefundImgRequest);
}
