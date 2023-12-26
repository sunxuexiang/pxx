package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.bean.vo.PayOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.AliPayProvider;
import com.wanmi.sbc.pay.api.provider.PayProvider;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.*;
import com.wanmi.sbc.pay.api.response.AliPayRefundResponse;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 定时查询支付宝支付订单状态并更新订单状态
 * @author: XinJiang
 * @time: 2022/1/24 17:38
 */
@JobHandler(value="aliPaymentSlipHandler")
@Component
@Slf4j
public class AliPaymentSlipHandler extends IJobHandler {

    private static final int ERROR_MAX_NUM = 5;

    private static final int PAGE_SIZE = 100;

    private static final String TODO = "0";

    private static final String HANDLING = "1";

    private static final String FAILED = "3";

    private static final String ORDER_CODE = "4";

    private static final String PARENT_ID = "5";

    @Autowired
    private AliPayProvider aliPayProvider;

    @Autowired
    private PayProvider payProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Override
    @LcnTransaction
    public ReturnT<String> execute(String paramStr) throws Exception {
        XxlJobLogger.log("支付宝订单状态查询定时任务执行==========  " + LocalDateTime.now());
        log.info("支付宝订单状态查询定时任务执行开始========== ：：：" + LocalDateTime.now());
        if (StringUtils.isNotBlank(paramStr)) {
            XxlJobLogger.log("指定订单号查询，参数paramStr："+paramStr);
            String[] paramStrArr = paramStr.split("&");
            String type = paramStrArr[0];
            if (type.equals(ORDER_CODE)) {
                String orderCodeStr = paramStrArr[1];
                List<String> businessIds = new ArrayList<>();
                Collections.addAll(businessIds, orderCodeStr.split(","));
                businessIds.forEach(businessId -> {
                    AlipayTradeQueryResponse response = aliPayProvider.queryAlipayPaymentSlip(AliPayPaymentSlipRequest.builder().businessId(businessId).build()).getContext();
                    XxlJobLogger.log("支付宝订单查询交易信息："+JSONObject.toJSONString(response));
                    log.info("支付宝订单查询交易信息：{}",JSONObject.toJSONString(response));
                    if (response.isSuccess()){
                        //防止重复回调，幂等
                        TradeVO tradeVO = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(businessId).build()).getContext().getTradeVO();
                        if (Objects.nonNull(tradeVO) && tradeVO.getTradeState().getPayState().equals(PayState.NOT_PAID)) {
                            //todo 1.新增或保存交易信息  2.支付回调 3.记录操作日志
                            XxlJobLogger.log("开始执行回调逻辑，订单号："+tradeVO.getId());
                            savePayTradeRecord(tradeVO);
                            aliPayOnlineCallBackHandler(response);
                            //支付单信息
                            PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                                    .builder().payOrderId(tradeVO.getPayOrderId()).build()).getContext().getPayOrder();
                            if (Objects.nonNull(payOrder.getReceivable())) {
                                operateLogMQUtil.convertAndSendForXxlJob("支付宝订单回调", "指定订单号回调",
                                        "查询支付宝交易状态并执行回调逻辑，订单号：" + tradeVO.getId());
                            }
                        }
                    }
                });
            } else if (type.equals(PARENT_ID)) {
                String parentId = paramStrArr[1];
                boolean isMergeOrderCode = isMergePayOrder(parentId);
                if (!isMergeOrderCode) {
                    XxlJobLogger.log("父订单号有误，任务结束！请检查参数父订单号：：："+parentId);
                    log.info("父订单号有误，任务结束！请检查参数父订单号：：：{}",parentId);
                    return SUCCESS;
                }
                AlipayTradeQueryResponse response = aliPayProvider.queryAlipayPaymentSlip(AliPayPaymentSlipRequest.builder().businessId(parentId).build()).getContext();
                XxlJobLogger.log("支付宝订单查询交易信息："+JSONObject.toJSONString(response));
                log.info("支付宝订单查询交易信息：{}",JSONObject.toJSONString(response));
                if (response.isSuccess()){
                    //防止重复回调，幂等
                    List<TradeVO> trades = tradeQueryProvider.getListByParentId(
                            new TradeListByParentIdRequest(parentId)).getContext().getTradeVOList();
                    //订单合并支付场景状态采样
                    boolean paid =
                            trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);
                    if (!paid) {
                        //todo 1.新增或保存交易信息  2.支付回调 3.记录操作日志
                        XxlJobLogger.log("开始执行回调逻辑，父订单号："+parentId);
                        trades.forEach(tradeVO -> {
                            savePayTradeRecord(tradeVO);
                        });
                        aliPayOnlineCallBackHandler(response);
                        trades.forEach(tradeVO -> {
                            //支付单信息
                            PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                                    .builder().payOrderId(tradeVO.getPayOrderId()).build()).getContext().getPayOrder();
                            if (Objects.nonNull(payOrder.getReceivable())) {
                                operateLogMQUtil.convertAndSendForXxlJob("支付宝订单回调", "指定父订单号回调",
                                        "查询支付宝交易状态并执行回调逻辑，父订单号："+tradeVO.getParentId()+"，订单号：" + tradeVO.getId());
                            }
                        });
                    }
                }
            }
        } else {
            TradeQueryDTO tradeQueryDTO = new TradeQueryDTO();
            TradeStateDTO tradeStateDTO = new TradeStateDTO();
            tradeStateDTO.setPayState(PayState.NOT_PAID);
            tradeStateDTO.setAuditState(AuditState.CHECKED);
            tradeQueryDTO.setTradeState(tradeStateDTO);
            String now = DateUtil.nowDate();
            tradeQueryDTO.setBeginTime(now+" 00:00:00");
            tradeQueryDTO.setEndTime(now+" 23:59:59");
            final Long tradeCount = tradeQueryProvider.countCriteria(TradeCountCriteriaRequest.builder().tradePageDTO(tradeQueryDTO).build()).getContext().getCount();
            int pageNum = tradeCount.intValue() / PAGE_SIZE + 1;
            tradeQueryDTO.setPageSize(PAGE_SIZE);
            int pageNo = 0;

            XxlJobLogger.log("需要查询支付宝订单总数："+tradeCount+"，分页数："+pageNum);
            while (pageNo < pageNum) {
                tradeQueryDTO.setPageNum(pageNo);
                TradePageCriteriaRequest tradePage = TradePageCriteriaRequest.builder().tradePageDTO(tradeQueryDTO).build();
                List<TradeVO> tradeVOList = tradeQueryProvider.pageCriteria(tradePage).getContext().getTradePage().getContent();
                tradeVOList.forEach(tradeVO -> {
                    AlipayTradeQueryResponse response = aliPayProvider.queryAlipayPaymentSlip(AliPayPaymentSlipRequest.builder().businessId(tradeVO.getId()).build()).getContext();
                    XxlJobLogger.log("支付宝订单查询交易信息："+JSONObject.toJSONString(response));
                    log.info("支付宝订单查询交易信息：{}",JSONObject.toJSONString(response));
                    if (response.isSuccess()) {
                        //防止重复回调，幂等
                        TradeVO tradeVORel = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tradeVO.getId()).build()).getContext().getTradeVO();
                        if (Objects.nonNull(tradeVORel) && tradeVORel.getTradeState().getPayState().equals(PayState.NOT_PAID)) {
                            //todo 1.新增或保存交易信息  2.支付回调 3.记录操作日志
                            XxlJobLogger.log("开始执行回调逻辑，订单号："+tradeVO.getId());
                            savePayTradeRecord(tradeVORel);
                            aliPayOnlineCallBackHandler(response);
                            //支付单信息
                            PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                                    .builder().payOrderId(tradeVORel.getPayOrderId()).build()).getContext().getPayOrder();
                            if (Objects.nonNull(payOrder.getReceivable())) {
                                operateLogMQUtil.convertAndSendForXxlJob("支付宝订单回调", "定时任务查询订单号回调",
                                        "查询支付宝交易状态并执行回调逻辑，订单号：" + tradeVORel.getId());
                            }
                        }
                    }
                });
                pageNo++;
                XxlJobLogger.log("执行第"+pageNo+"次循环");
            }
        }
        XxlJobLogger.log("支付宝订单状态查询定时任务执行结束==========  " + LocalDateTime.now());
        log.info("支付宝订单状态查询定时任务执行结束========== ：：：" + LocalDateTime.now());
        return SUCCESS;
    }


    /**
     * 获取交易记录
     * @param tradeVO
     * @return
     */
    private void savePayTradeRecord(TradeVO tradeVO) {
        TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest = new TradeRecordByOrderCodeRequest(tradeVO.getId());
        PayTradeRecordResponse recordResponse = payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
        if (Objects.isNull(recordResponse)) {
            PayTradeRecordRequest request = new PayTradeRecordRequest();
            request.setBusinessId(tradeVO.getId());
            request.setChargeId(tradeVO.getId());
            request.setApplyPrice(tradeVO.getTradePrice().getTotalPrice());
            //支付宝支付渠道
            request.setChannelItemId(19L);
            request.setResult_code("SUCCESS");
            request.setClientIp(tradeVO.getRequestIp());
            payProvider.savePayTradeRecord(request);
        }
    }

    /**
     * 定时查询支付宝支付成功订单，未回调处理方法
     * @param tradeQueryResponse
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void aliPayOnlineCallBackHandler(AlipayTradeQueryResponse tradeQueryResponse) {
        log.info("===============支付宝回调开始(定时任务查询支付宝订单状态补偿)==============");
        Map<String, Object> bodyParams =
                JSONObject.parseObject(tradeQueryResponse.getBody(), Map.class);
        Map<String, Object> params =
                JSONObject.parseObject(bodyParams.get("alipay_trade_query_response").toString(), Map.class);

        try {
            //商户订单号
            String out_trade_no = params.get("out_trade_no").toString();
            //支付宝交易号
            String trade_no = params.get("trade_no").toString();
            //交易状态
            String trade_status = params.get("trade_status").toString();
            //订单金额
            String total_amount = params.get("total_amount").toString();
            //支付终端类型
            String type = params.get("passback_params").toString();

            boolean isMergePay = isMergePayOrder(out_trade_no);
            log.info("-------------aliPaymentSlipHandler支付回调,单号：{}，流水：{}，交易状态：{}，金额：{}，是否合并支付：{}，支付终端类型：{}------------",
                    out_trade_no, trade_no, trade_status, total_amount, isMergePay,type);
            String lockName;
            //非组合支付，则查出该单笔订单。
            if (!isMergePay) {
                TradeVO tradeVO =
                        tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no)).getContext().getTradeVO();
                // 锁资源：无论是否组合支付，都锁父单号，确保串行回调
                lockName = tradeVO.getParentId();
            } else {
                lockName = out_trade_no;
            }
            Operator operator =
                    Operator.builder().ip("127.0.0.1").adminId("-1").name(PayGatewayEnum.ALIPAY.name())
                            .account(PayGatewayEnum.ALIPAY.name()).platform(Platform.THIRD).build();
            //redis锁，防止同一订单重复回调
            RLock rLock = redissonClient.getFairLock(lockName);
            rLock.lock();

            //执行
            try {
                List<TradeVO> trades = new ArrayList<>();
                //查询交易记录
                TradeRecordByOrderCodeRequest tradeRecordByOrderCodeRequest =
                        new TradeRecordByOrderCodeRequest(out_trade_no);
                PayTradeRecordResponse recordResponse =
                        payQueryProvider.getTradeRecordByOrderCode(tradeRecordByOrderCodeRequest).getContext();
                if (isMergePay) {
                    /*
                     * 合并支付
                     * 查询订单是否已支付或过期作废
                     */
                    trades = tradeQueryProvider.getListByParentId(
                            new TradeListByParentIdRequest(out_trade_no)).getContext().getTradeVOList();
                    //订单合并支付场景状态采样
                    boolean paid =
                            trades.stream().anyMatch(i -> i.getTradeState().getPayState() == PayState.PAID);

                    boolean cancel =
                            trades.stream().anyMatch(i -> i.getTradeState().getFlowState() == FlowState.VOID);
                    //如果已支付，直接返回
                    if (paid) {
                        return;
                    }
                    //订单的支付渠道。17、18、19是我们自己对接的支付宝渠道， 表：pay_channel_item
                    if (cancel || (paid && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                            != 18L && recordResponse.getChannelItemId() != 19L)) {
                        //重复支付，直接退款
//                        alipayRefundHandle(out_trade_no, total_amount);
                        return;
                    } else {
                        alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                operator, trades, true, recordResponse);
                    }

                } else {
                    //单笔支付
                    TradeVO tradeVO = tradeQueryProvider.getById(new TradeGetByIdRequest(out_trade_no))
                            .getContext().getTradeVO();
                    //如果已支付，直接返回
                    if (tradeVO.getTradeState().getPayState() == PayState.PAID) {
                        return;
                    }
                    if (tradeVO.getTradeState().getFlowState() == FlowState.VOID || (tradeVO.getTradeState()
                            .getPayState() == PayState.PAID && recordResponse.getChannelItemId() != 17L && recordResponse.getChannelItemId()
                            != 18L && recordResponse.getChannelItemId() != 19L)) {
                        //同一批订单重复支付或过期作废，直接退款
//                        alipayRefundHandle(out_trade_no, total_amount);
                        return;
                    } else {
                        trades.add(tradeVO);
                        alipayCallbackHandle(out_trade_no, trade_no, trade_status, total_amount, type,
                                operator, trades, false, recordResponse);
                    }

                }

            } finally {
                //解锁
                rLock.unlock();
            }
        } catch (Exception e) {
            log.error("支付宝回调异常：", e);
            throw e;
        }
    }

    /**
     * 支付宝退款处理
     * @param out_trade_no
     * @param total_amount
     */
    private void alipayRefundHandle(String out_trade_no, String total_amount) {
        //调用退款接口。直接退款。不走退款流程，没有交易对账，只记了操作日志
        AliPayRefundResponse aliPayRefundResponse =
                aliPayProvider.aliPayRefund(AliPayRefundRequest.builder().businessId(out_trade_no)
                        .amount(new BigDecimal(total_amount)).description("重复支付退款").build()).getContext();

        if (aliPayRefundResponse.getAlipayTradeRefundResponse().isSuccess()) {
            operateLogMQUtil.convertAndSend("支付宝退款", "重复支付、超时订单退款",
                    "订单号：" + out_trade_no);
        }
        log.info("支付宝重复支付、超时订单退款,单号：{}", out_trade_no);
    }

    private void alipayCallbackHandle(String out_trade_no, String trade_no, String trade_status, String total_amount,
                                      String type, Operator operator, List<TradeVO> trades, boolean isMergePay,
                                      PayTradeRecordResponse recordResponse) {
        if (recordResponse.getApplyPrice().compareTo(new BigDecimal(total_amount)) == 0 && trade_status.equals(
                "TRADE_SUCCESS")) {
            //异步回调添加交易数据
            PayTradeRecordRequest payTradeRecordRequest = new PayTradeRecordRequest();
            //流水号
            payTradeRecordRequest.setTradeNo(trade_no);
            //商品订单号
            payTradeRecordRequest.setBusinessId(out_trade_no);
            payTradeRecordRequest.setResult_code("SUCCESS");
            payTradeRecordRequest.setPracticalPrice(new BigDecimal(total_amount));
            payTradeRecordRequest.setChannelItemId(Long.valueOf(type));
            //添加交易数据（与微信共用）
            payProvider.wxPayCallBack(payTradeRecordRequest);
            log.info("isMergePay==== {}", isMergePay);
            payCallbackOnline(trades, operator, isMergePay);
            log.info("支付回调成功,单号：{}", out_trade_no);
        }
    }

    /**
     * 线上订单支付回调
     * 订单 支付单 操作信息
     * @return 操作结果
     */
    private void payCallbackOnline(List<TradeVO> trades, Operator operator, boolean isMergePay) {
        //封装回调参数
        /*  List<PickUpRecordAddRequest> pickUpRecordAddBatchRequest = new ArrayList<>();*/
        List<TradePayCallBackOnlineDTO> reqOnlineDTOList = trades.stream().map(i -> {
            //每笔订单做是否合并支付标识
            i.getPayInfo().setMergePay(isMergePay);
            log.info("PayCallbackController.payCallbackOnline tid:{}",i.getId());
            TradeAddMergePayRequest mergePayRequest = new TradeAddMergePayRequest();
            mergePayRequest.setMergePay(isMergePay);
            mergePayRequest.setTid(i.getId());
            tradeProvider.addMergePay(mergePayRequest);
            log.info("PayCallbackController.payCallbackOnline end");

//            tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(i,TradeUpdateDTO.class)).build());

           /* TradeUpdateRequest tradeUpdateRequest = new TradeUpdateRequest(KsBeanUtil.convert(i, TradeUpdateDTO.class));
            //判断是否是先货后款
            boolean goodsFirst=Objects.equals(i.getTradeState().getAuditState() , AuditState.CHECKED) && i.getPaymentOrder() == PaymentOrder.NO_LIMIT;
            //是否是自提订单如果不是发送自提码
            if (!goodsFirst) {
                //是否是自提订单如果是组装参数，发送自提码
                if (null != i.getDeliverWay() && i.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                    PickUpRecordAddRequest pickUpRecordAddRequest = sendPickUpCode(i);
                    pickUpRecordAddBatchRequest.add(pickUpRecordAddRequest);
                    tradeUpdateRequest.getTrade().getTradeWareHouse().setPickUpCode(pickUpRecordAddRequest.getPickUpCode());
                    i.getTradeWareHouse().setPickUpCode(pickUpRecordAddRequest.getPickUpCode());
                }
            }
            tradeProvider.update(tradeUpdateRequest);*/
            //支付单信息
            PayOrderVO payOrder = tradeQueryProvider.getPayOrderById(TradeGetPayOrderByIdRequest
                    .builder().payOrderId(i.getPayOrderId()).build()).getContext().getPayOrder();
            return TradePayCallBackOnlineDTO.builder()
                    .trade(KsBeanUtil.convert(i, TradeDTO.class))
                    .payOrderOld(KsBeanUtil.convert(payOrder, PayOrderDTO.class))
                    .build();
        }).collect(Collectors.toList());
        //批量回调
        tradeProvider.payCallBackOnlineBatch(new TradePayCallBackOnlineBatchRequest(reqOnlineDTOList,
                operator));

        //自提码处理
     /*   if (CollectionUtils.isNotEmpty(pickUpRecordAddBatchRequest)) {
            //数据库新增
            pickUpRecordProvider.addBatch(PickUpRecordAddBatchRequest.builder().pickUpRecordAddRequestList(pickUpRecordAddBatchRequest).build());
            //发送自提码
            for (TradeVO inner:trades){
                if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)){
                    tradeProvider.sendPickUpMessage(SendPickUpMessageRequest.builder().trade(inner).build());
                }
            }
        }*/
    }

    /**
     * @return boolean
     * @Author lvzhenwei
     * @Description 判断是否为主订单
     * @Date 15:36 2020/7/2
     * @Param [businessId]
     **/
    private boolean isMergePayOrder(String businessId) {
        log.info("============================= isMergePayOrder.businessId:{}============================",businessId);
        return businessId.startsWith(GeneratorService._PREFIX_PARENT_TRADE_ID);
    }

}
