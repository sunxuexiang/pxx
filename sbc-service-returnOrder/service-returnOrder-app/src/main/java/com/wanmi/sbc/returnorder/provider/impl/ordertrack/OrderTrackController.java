package com.wanmi.sbc.returnorder.provider.impl.ordertrack;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.ordertrack.OrderTrackProvider;
import com.wanmi.sbc.returnorder.api.request.ordertrack.OrderTrackRequest;
import com.wanmi.sbc.returnorder.api.response.ordertrack.OrderTrackListResp;
import com.wanmi.sbc.returnorder.api.response.ordertrack.OrderTrackResp;
import com.wanmi.sbc.returnorder.ordertrack.root.OrderTrack;
import com.wanmi.sbc.returnorder.ordertrack.service.OrderTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc  
 * @author shiy  2023/6/17 10:26
*/
@Validated
@RestController
public class OrderTrackController implements OrderTrackProvider {

    @Autowired
    private OrderTrackService orderTrackService;


    @Override
    public BaseResponse add(OrderTrackRequest orderTrackRequest) {
        OrderTrack orderTrack = KsBeanUtil.convert(orderTrackRequest, OrderTrack.class);
        orderTrackService.add(orderTrack);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<OrderTrackResp> modify(OrderTrackRequest orderTrackRequest) {
        return null;
    }

    @Override
    public BaseResponse<OrderTrackResp> findByComNum(String com, String num) {
        OrderTrack orderTrack = orderTrackService.findByComNum(com,num);
        return BaseResponse.success(KsBeanUtil.convert(orderTrack,OrderTrackResp.class));
    }

    @Override
    public BaseResponse<OrderTrackListResp> list(OrderTrackRequest orderTrackRequest) {
        return null;
    }
}
