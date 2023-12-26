package com.wanmi.sbc.returnorder.api.provider.pointstrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradeConfirmReceiveRequest;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradeDeliverRequest;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradeDeliveryCheckRequest;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradeRemedySellerRemarkRequest;
import com.wanmi.sbc.returnorder.api.response.trade.TradeDeliverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author lvzhenwei
 * @Description 积分订单接口方法
 * @Date 10:54 2019/5/10
 * @Param
 * @return
 **/
@FeignClient(value = "${application.returnOrder.name}", url="${feign.url.returnOrder:#{null}}", contextId = "ReturnPointsTradeProvider")
public interface PointsTradeProvider {

    /**
     * 发货校验,检查请求发货商品数量是否符合应发货数量
     *
     * @param pointsTradeDeliveryCheckRequest 订单号 物流信息 {@link PointsTradeDeliveryCheckRequest}
     * @return 处理结果 {@link BaseResponse}
     */
    @PostMapping("/points/returnOrder/${application.order.version}/trade/delivery-check")
    BaseResponse deliveryCheck(@RequestBody @Valid PointsTradeDeliveryCheckRequest pointsTradeDeliveryCheckRequest);

    /**
     * 积分订单发货
     *
     * @param pointsTradeDeliverRequest 物流信息 操作信息 {@link PointsTradeDeliverRequest}
     * @return 物流编号 {@link TradeDeliverResponse}
     */
    @PostMapping("/points/returnOrder/${application.order.version}/points-trade/deliver")
    BaseResponse<TradeDeliverResponse> deliver(@RequestBody @Valid PointsTradeDeliverRequest pointsTradeDeliverRequest);

    /**
     * 确认收货
     *
     * @param pointsTradeConfirmReceiveRequest 订单编号 操作信息 {@link PointsTradeConfirmReceiveRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/points/returnOrder/${application.order.version}/points-trade/confirm-receive")
    BaseResponse confirmReceive(@RequestBody @Valid PointsTradeConfirmReceiveRequest pointsTradeConfirmReceiveRequest);

    /**
     * 修改卖家备注
     *
     * @param pointsTradeRemedySellerRemarkRequest 订单修改相关必要信息 {@link PointsTradeRemedySellerRemarkRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/points/returnOrder/${application.order.version}/trade/remedy-seller-remark")
    BaseResponse remedySellerRemark(@RequestBody @Valid PointsTradeRemedySellerRemarkRequest pointsTradeRemedySellerRemarkRequest);
}
