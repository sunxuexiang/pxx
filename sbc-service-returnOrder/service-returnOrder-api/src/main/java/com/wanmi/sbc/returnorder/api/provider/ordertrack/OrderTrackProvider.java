package com.wanmi.sbc.returnorder.api.provider.ordertrack;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.ordertrack.OrderTrackRequest;
import com.wanmi.sbc.returnorder.api.response.ordertrack.OrderTrackListResp;
import com.wanmi.sbc.returnorder.api.response.ordertrack.OrderTrackResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @desc  
 * @author shiy  2023/6/17 10:15
*/
@FeignClient(value = "${application.returnOrder.name}", contextId = "ReturnOrderTrackProvider")
public interface OrderTrackProvider {

    /**
     * @desc  新增
     * @author shiy  2023/6/17 10:18
    */
    @PostMapping("/returnOrder/${application.order.version}/track/add")
    BaseResponse add(@RequestBody @Valid OrderTrackRequest orderTrackRequest);

    /**
     * @desc  修改
     * @author shiy  2023/6/17 10:18
     */
    @PostMapping("/returnOrder/${application.order.version}/track/modify")
    BaseResponse<OrderTrackResp> modify(@RequestBody @Valid OrderTrackRequest orderTrackRequest);

    /**
     * @desc  查询
     * @author shiy  2023/6/17 10:18
     */
    @GetMapping("/returnOrder/${application.order.version}/track/find-by-com-num")
    BaseResponse<OrderTrackResp> findByComNum(@RequestParam(value="com") String com, @RequestParam(value="num") String num);

    /**
     * @desc  查询
     * @author shiy  2023/6/17 10:18
     */
    @PostMapping("/returnOrder/${application.order.version}/track/list")
    BaseResponse<OrderTrackListResp> list(@RequestBody @Valid OrderTrackRequest orderTrackRequest);
}
