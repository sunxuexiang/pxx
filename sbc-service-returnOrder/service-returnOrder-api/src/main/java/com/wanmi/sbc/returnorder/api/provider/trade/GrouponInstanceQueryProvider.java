package com.wanmi.sbc.returnorder.api.provider.trade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.trade.GrouponInstanceByGrouponNoRequest;
import com.wanmi.sbc.returnorder.api.request.trade.GrouponInstancePageRequest;
import com.wanmi.sbc.returnorder.api.request.trade.TradePageCriteriaRequest;
import com.wanmi.sbc.returnorder.api.response.trade.GrouponInstanceByActivityIdResponse;
import com.wanmi.sbc.returnorder.api.response.trade.GrouponInstanceByGrouponNoResponse;
import com.wanmi.sbc.returnorder.api.response.trade.GrouponInstancePageResponse;
import com.wanmi.sbc.returnorder.api.response.trade.GrouponInstancePageWithCustomerInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Description:
 * @Date: 2018-12-03 17:24
 */
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnGrouponInstanceQueryProvider")
public interface GrouponInstanceQueryProvider {


    /**
     * 条件分页
     * @param grouponInstancePageRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/groupon/instance/page-criteria")
    BaseResponse<GrouponInstancePageResponse> pageCriteria(@RequestBody @Valid GrouponInstancePageRequest grouponInstancePageRequest );


    /**
     * 条件分页
     * 返回结果包装用户信息
     * @param grouponInstancePageRequest 带参分页参数 {@link TradePageCriteriaRequest}
     * @return
     */
    @PostMapping("/returnOrder/${application.order.version}/groupon/instance/page-criteria-with-customer-info")
    BaseResponse<GrouponInstancePageWithCustomerInfoResponse> pageCriteriaWithCustomerInfoResponse(@RequestBody @Valid GrouponInstancePageRequest grouponInstancePageRequest );

    /**
     * 根据groupoNo，查询团活动信息
     */
    @PostMapping("/returnOrder/${application.order.version}/groupon/instance/groupon-no")
    BaseResponse<GrouponInstanceByGrouponNoResponse> detailByGrouponNo(@RequestBody @Valid GrouponInstanceByGrouponNoRequest
                                                                     request);

    @PostMapping("/returnOrder/${application.order.version}/groupon/instance/latest-group")
    BaseResponse<GrouponInstanceByActivityIdResponse>getGrouponLatestInstanceByActivityId(@RequestBody @Valid GrouponInstancePageRequest request);

    }


