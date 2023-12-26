package com.wanmi.sbc.returnorder.refundfreight.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeAppendEventLogRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeStateVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.request.CcbRefundFreightRequest;
import com.wanmi.sbc.pay.api.response.CcbRefundFreightResponse;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightCallbackRequest;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightRequest;
import com.wanmi.sbc.returnorder.refundfreight.model.root.RefundFreightRecord;
import com.wanmi.sbc.returnorder.refundfreight.repository.RefundFreightRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 14:15
 */
@Service
@Slf4j
public class RefundFreightService {

    @Autowired
    private RefundFreightRecordRepository refundFreightRecordRepository;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private RefundFreightPushKingdieeService refundFreightPushKingdieeService;


    public void add(RefundFreightRequest request) {

        RefundFreightRecord refundFreightRecord = refundFreightRecordRepository.findByTid(request.getTid());
        if (Objects.nonNull(refundFreightRecord) && Objects.equals(refundFreightRecord.getRefundStatus(), 1)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单退运费已完成，请勿重复退运费");
        }

        String tid = request.getTid();
        TradeGetByIdRequest.builder().tid(tid).build();
        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (Objects.isNull(tradeVO)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单不存在");
        }
        BigDecimal deliveryPrice = tradeVO.getTradePrice().getDeliveryPrice();
        if (Objects.isNull(deliveryPrice) || request.getAmount().compareTo(deliveryPrice) > 0) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "金额大于订单运费金额");
        }
        TradeStateVO tradeState = tradeVO.getTradeState();
        if (!Objects.equals(tradeState.getPayState(), PayState.PAID)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单未支付");
        }
        // 有售前商家待分账不足退款失败的情况，接口暂时不校验售前
        // if (Objects.equals(tradeState.getDeliverStatus(), DeliverStatus.NOT_YET_SHIPPED)) {
        //     throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单未发货不允许退运费");
        // }
        String payOrderNo = tradeVO.getPayOrderNo();
        if (StringUtils.isBlank(payOrderNo)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单缺失支付单号，不能退运费");
        }
        String rid = "F" + request.getTid();
        CcbRefundFreightRequest refundFreightRequest = CcbRefundFreightRequest
                .builder()
                .tid(request.getTid())
                .rid(rid)
                .payOrderNo(payOrderNo)
                .amount(request.getAmount())
                .build();
        CcbRefundFreightResponse response = ccbPayProvider.freightRefund(refundFreightRequest).getContext();
        log.info("手动退运费，请求支付返回：{}", JSON.toJSONString(response));
        Integer refundStatus = response.getRefundStatus();
        if (Objects.equals(refundStatus, 3)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退运费失败，请联系IT中心或稍后再试！");
        }
        Operator operator = request.getOperator();
        if (Objects.isNull(refundFreightRecord)) {
            refundFreightRecord = new RefundFreightRecord();
            refundFreightRecord.setCreateTime(LocalDateTime.now());
            refundFreightRecord.setCreatePerson(operator.getName());
        }
        refundFreightRecord.setRid(rid);
        refundFreightRecord.setTid(request.getTid());
        refundFreightRecord.setRefundStatus(refundStatus);
        refundFreightRecord.setFreightPrice(response.getRefundFreightPrice());
        refundFreightRecord.setRemarks(request.getRemarks());
        refundFreightRecord.setUpdateTime(LocalDateTime.now());
        refundFreightRecordRepository.saveAndFlush(refundFreightRecord);

        String desc = String.format("订单[%s], 退运费[%s]元, 建行退款状态:[%s]。\r\n 备注：[%s]",
                tid, response.getRefundFreightPrice().toPlainString(),
                Objects.equals(refundStatus, 1) ? "退款成功" : "退款中", request.getRemarks());

        TradeAppendEventLogRequest appendEventLogRequest = TradeAppendEventLogRequest
                .builder()
                .tid(request.getTid())
                .desc(desc)
                .operator(request.getOperator())
                .eventType("手动退运费")
                .build();

        tradeProvider.tradeAppendEventLog(appendEventLogRequest);

        //  如果退款成功 推送ERP
        if (Objects.equals(1, refundStatus)) {
            refundFreightPushKingdieeService.refundFreightPushKingdiee(request.getTid());
        }
    }

    public void callback(RefundFreightCallbackRequest request) {

        RefundFreightRecord record = refundFreightRecordRepository.findByRid(request.getRid());
        if (Objects.nonNull(record)) {
            record.setUpdateTime(LocalDateTime.now());
            record.setRefundStatus(request.getRefundStatus());
            refundFreightRecordRepository.saveAndFlush(record);

            String desc = String.format("订单[%s], 退运费[%s]元, 建行回调退款状态:[%s]。\r\n 备注：[%s]",
                    record.getId(), record.getFreightPrice().toPlainString(),
                    Objects.equals(request.getRefundStatus(), 1) ? "退款成功" : "退款失败", record.getRemarks());
            Operator operator = Operator
                    .builder()
                    .platform(Platform.PLATFORM)
                    .name("system")
                    .account("system")
                    .ip("127.0.0.1")
                    .build();
            TradeAppendEventLogRequest appendEventLogRequest = TradeAppendEventLogRequest
                    .builder()
                    .tid(record.getTid())
                    .desc(desc)
                    .operator(operator)
                    .eventType("手动退运费")
                    .build();

            tradeProvider.tradeAppendEventLog(appendEventLogRequest);

            if (Objects.equals(1, request.getRefundStatus())) {
                refundFreightPushKingdieeService.refundFreightPushKingdiee(record.getTid());
            }
        }

    }
}
