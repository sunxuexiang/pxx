package com.wanmi.sbc.returnorder.api.provider.groupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.groupon.*;
import com.wanmi.sbc.returnorder.api.response.groupon.GrouponDetailQueryResponse;
import com.wanmi.sbc.returnorder.api.response.groupon.GrouponDetailWithGoodsResponse;
import com.wanmi.sbc.returnorder.api.response.groupon.GrouponOrderStatusGetByOrderIdResponse;
import com.wanmi.sbc.returnorder.api.response.groupon.GrouponOrderValidResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>拼团业务Provider</p>
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnGrouponProvider")
public interface GrouponProvider {


    /**
     * 查询拼团信息
     */
    @PostMapping("/returnOrder/${application.order.version}/getGrouponDetail")
    BaseResponse<GrouponDetailQueryResponse> getGrouponDetail(@RequestBody @Valid GrouponDetailQueryRequest
                                                                      grouponDetailQueryRequest);

    /**
     * 根据商品信息组装拼团信息
     */
    @PostMapping("/returnOrder/${application.order.version}/getGrouponDetail/goods")
    BaseResponse<GrouponDetailWithGoodsResponse> getGrouponDetailWithGoodsInfos(@RequestBody @Valid
                                                                                GrouponDetailWithGoodsRequest
                                                                                        grouponDetailWithGoodsRequest);

    /**
     * 根据订单ID验证拼团订单状态是否可支付
     *
     * @param request
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/groupon/get-groupon-order-status-by-order-id")
    BaseResponse<GrouponOrderStatusGetByOrderIdResponse> getGrouponOrderStatusByOrderId(@RequestBody @Valid
                                                                                        GrouponOrderStatusGetByOrderIdRequest request);

    /**
     * 订单提交前验证拼团订单信息
     */
    @PostMapping("/returnOrder/${application.order.version}/groupon/groupon-order-valid-before-commit")
    BaseResponse<GrouponOrderValidResponse> validGrouponOrderBeforeCommit(@RequestBody @Valid
                                                                          GrouponOrderValidRequest request);

    @PostMapping("/returnOrder/${application.order.version}/groupon/get-activity-by-no")
    BaseResponse getGrouponActivityByGrouponNo(@RequestBody @Valid GrouponActivityQueryRequest request);

    @PostMapping("/returnOrder/${application.order.version}/groupon/get-recent-activity")
    BaseResponse grouponInstanceQuery(@RequestBody @Valid GrouponRecentActivityQueryRequest request);


}

