// package com.wanmi.sbc.returnorder.returnorder.mq;
//
// import com.wanmi.sbc.returnorder.api.constant.JmsDestinationConstants;
// import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.stream.annotation.EnableBinding;
// import org.springframework.cloud.stream.annotation.StreamListener;
// import org.springframework.stereotype.Service;
//
// /**
//  * Created by IntelliJ IDEA.
//  *
//  * @Author : Like
//  * @create 2023/9/25 17:51
//  */
// @Service
// @Slf4j
// @EnableBinding(ReturnOrderSink.class)
// public class AutoReturnOrderConsumerService {
//
//     @Autowired
//     private ReturnOrderService returnOrderService;
//
//     /**
//      * @param rid
//      */
//     @StreamListener(JmsDestinationConstants.Q_SUPPLIER_REFUND_ORDER_AUTO_AUDIT_CONSUMER)
//     public void supplierRefundOrderAutoAuditProducer(String rid) {
//         log.info("商家后台退款自动审核，退单号：{}", rid);
//         try {
//             returnOrderService.refundOnlineByTid(rid, null, false);
//             log.info("商家后台退款自动审核,完成。退单号：{}", rid);
//         } catch (Exception e) {
//             log.error("商家后台退款自动审核错误! 退单号={}", rid, e);
//         }
//     }
// }
