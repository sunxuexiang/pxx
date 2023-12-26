package com.wanmi.sbc.order.api.provider.manualrefund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.manualrefund.ManualRefundImgRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "ManualRefundImgProvider")
public interface ManualRefundImgProvider {

    @PostMapping("/order/${application.order.version}/manualrefundImg/addManualRefundImgs")
    BaseResponse addManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest);

    @PostMapping("/order/${application.order.version}/manualrefundImg/deleteManualRefundImgs")
    BaseResponse deleteManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest);

    @PostMapping("/order/${application.order.version}/manualrefundImg/listManualRefundImgs")
    BaseResponse listManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest);

    /**
     * @description  初始化历史订单退款凭证状态
     * @author  shiy
     * @date    2023/3/17 9:20
     * @params  [com.wanmi.sbc.order.api.request.manualrefund.ManualRefundImgRequest]
     * @return  com.wanmi.sbc.common.base.BaseResponse
    */
    @PostMapping("/order/${application.order.version}/manualrefundImg/initOrderImgsFlag")
    BaseResponse initOrderImgsFlag(@RequestBody ManualRefundImgRequest manualRefundImgRequest);
}
