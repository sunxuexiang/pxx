package com.wanmi.sbc.returnorder.returnextra.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeAppendEventLogRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.request.CcbRefundExtraRequest;
import com.wanmi.sbc.pay.api.response.CcbRefundFreightResponse;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightCallbackRequest;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.bean.enums.ReturnType;
import com.wanmi.sbc.returnorder.refundfreight.service.RefundFreightPushKingdieeService;
import com.wanmi.sbc.returnorder.returnextra.model.root.RefundExtraRecord;
import com.wanmi.sbc.returnorder.returnextra.repository.RefundExtraRecordRepository;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class RefundExtraRecordService {

    @Autowired
    private RefundExtraRecordRepository refundExtraRecordRepository;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private RefundFreightPushKingdieeService refundFreightPushKingdieeService;

    public void refundExtra(String tid, String rid) {
        ReturnOrder returnOrder = returnOrderService.findById(rid);
        if (Objects.isNull(returnOrder) || !Objects.equals(returnOrder.getReturnFlowState(), ReturnFlowState.COMPLETED)) {
            log.info("运费加收退款延迟消费消息, 退单号：{} ,退单状态不满足不处理 ", rid);
            return;
        }
        if (Objects.equals(returnOrder.getReturnType(), ReturnType.RETURN)) {
            log.info("运费加收退款延迟消费消息, 退单号：{} ,售后退款不处理", rid);
            return;
        }
        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (Objects.isNull(tradeVO) || !Objects.equals(tradeVO.getTradeState().getPayState(), PayState.PAID)) {
            log.info("运费加收退款延迟消费消息, 订单号：{} ,订单不存在 ", tid);
            return;
        }
        if (!DeliverWay.isTmsDelivery(tradeVO.getDeliverWay())) {
            log.info("运费加收退款延迟消费消息, 订单号：{} ,不是TMS物流订单不处理", tid);
            return;
        }
        // 查询是否需要退运费加收 以及运费加收对应的订单号
        String targetTradeId = tradeProvider.getCanReturnVillagePriceTrade(tid).getContext();
        log.info("运费加收退款延迟消费消息, 订单号：{}, 退单号：{}, 加收订单ID：{}", tid, rid, targetTradeId);
        if (StringUtils.isBlank(targetTradeId)) {
            log.info("运费加收退款延迟消费消息, 订单号：{} ,查询是否可退加收,不满足不处理", tid);
            return;
        }
        // 校验对应订单号是否有退运费加收记录 已经完成的不再处理
        RefundExtraRecord extraRecord = refundExtraRecordRepository.findByExtraId(targetTradeId);
        if (Objects.nonNull(extraRecord) && Objects.equals(extraRecord.getRefundStatus(), 1)) {
            log.info("运费加收退款延迟消费消息, 订单号：{}, 退单号：{}, 加收订单ID：{} 对应订单加收退款已完成，不能退款加收", tid, rid, targetTradeId);
        }
        //  退款
        TradeVO targetTrade = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(targetTradeId).build()).getContext().getTradeVO();
        String payOrderNo = targetTrade.getPayOrderNo();
        if (StringUtils.isBlank(payOrderNo) || !Objects.equals(targetTrade.getPayWay(), PayWay.CCB)) {
            log.info("运费加收退款延迟消费消息, 退运费加收单号：{},无支付单号 ", targetTradeId);
            return;
        }
        CcbRefundExtraRequest extraRequest = CcbRefundExtraRequest
                .builder()
                .rid("E" + targetTradeId)
                .tid(targetTradeId)
                .payOrderNo(payOrderNo)
                .build();
        CcbRefundFreightResponse response = ccbPayProvider.extraRefund(extraRequest).getContext();
        log.info("运费加收退款延迟消费消息，请求支付返回：{}", JSON.toJSONString(response));
        Integer refundStatus = response.getRefundStatus();

        Operator operator = Operator
                .builder()
                .platform(Platform.PLATFORM)
                .name("system")
                .account("system")
                .ip("127.0.0.1")
                .build();
        if (Objects.isNull(extraRecord)) {
            extraRecord = new RefundExtraRecord();
            extraRecord.setCreateTime(LocalDateTime.now());
            extraRecord.setCreatePerson(operator.getName());
        }
        String remark = String.format("系统自动退运费加收, 关联订单：%s,关联退单：%s", tid, rid);
        extraRecord.setRid(rid);
        extraRecord.setTid(tid);
        extraRecord.setRefundStatus(refundStatus);
        extraRecord.setExtraPrice(response.getRefundFreightPrice());
        extraRecord.setRemark(remark);
        extraRecord.setExtraId(targetTradeId);
        extraRecord.setUpdateTime(LocalDateTime.now());
        refundExtraRecordRepository.saveAndFlush(extraRecord);

        String desc = String.format("订单[%s], 运费加收退款[%s]元, 建行退款状态:[%s]。\r\n 备注：[%s]",
                targetTradeId, response.getRefundFreightPrice().toPlainString(),
                convertCcbRefundStatusName(refundStatus), remark);

        TradeAppendEventLogRequest appendEventLogRequest = TradeAppendEventLogRequest
                .builder()
                .tid(targetTradeId)
                .desc(desc)
                .operator(operator)
                .eventType("运费加收退款")
                .build();

        tradeProvider.tradeAppendEventLog(appendEventLogRequest);

        //  推送ERP
        if (Objects.equals(1, refundStatus)) {
            refundFreightPushKingdieeService.refundExtraPushKingdiee(targetTradeId);
        }
    }

    public void callback(RefundFreightCallbackRequest request) {
        String rid = request.getRid();
        if (rid.startsWith("E")) {
            rid = rid.replace("E", "");
        }
        RefundExtraRecord record = refundExtraRecordRepository.findByExtraId(rid);
        if (Objects.nonNull(record)) {
            record.setUpdateTime(LocalDateTime.now());
            record.setRefundStatus(request.getRefundStatus());
            refundExtraRecordRepository.saveAndFlush(record);

            String remark = String.format("系统自动退运费加收, 关联订单：%s,关联退单：%s", record.getTid(), record.getRid());

            String desc = String.format("订单[%s], 运费加收退款[%s]元, 建行退款回调状态:[%s]。\r\n 备注：[%s]",
                    record.getExtraId(), record.getExtraPrice().toPlainString(),
                    convertCcbRefundStatusName(request.getRefundStatus()), remark);

            Operator operator = Operator
                    .builder()
                    .platform(Platform.PLATFORM)
                    .name("system")
                    .account("system")
                    .ip("127.0.0.1")
                    .build();
            TradeAppendEventLogRequest appendEventLogRequest = TradeAppendEventLogRequest
                    .builder()
                    .tid(record.getExtraId())
                    .desc(desc)
                    .operator(operator)
                    .eventType("运费加收退款")
                    .build();

            tradeProvider.tradeAppendEventLog(appendEventLogRequest);

            //  推送ERP
            if (Objects.equals(1, request.getRefundStatus())) {
                refundFreightPushKingdieeService.refundExtraPushKingdiee(record.getExtraId());
            }
        }

    }

    public static String convertCcbRefundStatusName(Integer refundStatus) {
        if (Objects.isNull(refundStatus)) {
            return "退款失败";
        }
        if (Objects.equals(refundStatus, 1)) {
            return "退款成功";
        }
        if (Objects.equals(refundStatus, 2)) {
            return "退款中";
        }
        if (Objects.equals(refundStatus, 3)) {
            return "退款失败";
        }
        return "退款失败";
    }
}
