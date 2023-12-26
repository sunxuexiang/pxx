package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.logisticsbean.dto.WmsOrderStatusTidListDTO;
import com.wanmi.sbc.logisticsbean.vo.WmsOrderStatusVo;
import com.wanmi.sbc.order.api.provider.trade.PushWmsFailLogProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.trade.PushFailLogRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradePushRequest;
import com.wanmi.sbc.order.bean.dto.PushFailLogDTO;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.PayTradeRecordRequest;
import com.wanmi.sbc.pay.api.response.PayTradeRecordResponse;
import com.wanmi.sbc.pay.bean.enums.TradeStatus;
import com.wanmi.sbc.provider.OrderStatusMatchProvider;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 定时重推推送wms失败订单
 * @author lm
 * @date 2022/11/19 15:19
 */
@RestController
@JobHandler("rePushToWmsFailOrderHandler")
@Slf4j
public class RePushToWmsFailOrderHandler extends IJobHandler {

    @Autowired
    private OrderStatusMatchProvider orderStatusMatchProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private PushWmsFailLogProvider pushWmsFailLogProvider;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        final LocalDateTime now = LocalDateTime.now();
        // 查询所有订单
        BaseResponse<List<TradeVO>> baseResponse = pushWmsFailLogProvider.findAllTrade();
        if(Objects.isNull(baseResponse) || CollectionUtils.isEmpty(baseResponse.getContext())){
            XxlJobLogger.log("没有查询到失败推送失败订单");
            return SUCCESS;
        }
        List<TradeVO> tradeVOS = baseResponse.getContext();
        XxlJobLogger.log("查询订单数：{}",tradeVOS.size());

        // 重推失败
        CopyOnWriteArrayList<PushFailLogDTO> rePushFailList = new CopyOnWriteArrayList<>();

        int times =  Double.valueOf(Math.ceil(tradeVOS.size() / 900.00)).intValue();// 次数
        CountDownLatch countDownLatch = new CountDownLatch(times);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < times; i++) {
            int startIndex = 0 * 900;
            int endIndex = Math.min(startIndex+900, tradeVOS.size()-1);
            List<TradeVO> subTradeVOList = tradeVOS.subList(startIndex, endIndex);

            log.info("RePushToWmsFailOrderHandler-->startIndex:{},endIndex:{},subSize:{}",startIndex,endIndex,subTradeVOList.size());
            pool.execute(() -> {
                try {
                    List<String> subList = subTradeVOList.stream().map(TradeVO::getId).distinct().collect(Collectors.toList());
                    // 查询wms订单状态
                    WmsOrderStatusTidListDTO tidListDTO = WmsOrderStatusTidListDTO.builder().tidList(subList).build();
                    BaseResponse<List<WmsOrderStatusVo>> listBaseResponse = orderStatusMatchProvider.queryAllWmsOrderStatusByTidList(tidListDTO);

                    PushFailLogDTO pushFailLogRequest = null;
                    for (TradeVO tradeVO : subTradeVOList) {
                        Optional<WmsOrderStatusVo> orderStatusVoOptional = Optional.empty();
                        if (listBaseResponse != null && !CollectionUtils.isEmpty(listBaseResponse.getContext())) {
                            List<WmsOrderStatusVo> wmsOrderStatusVoList = listBaseResponse.getContext();
                            orderStatusVoOptional = wmsOrderStatusVoList.stream().filter(status -> status.getTid().equals(tradeVO.getId())).findFirst();
                        }
                        if(orderStatusVoOptional.isPresent()){// wms存在订单
                            PayState payState = tradeVO.getTradeState().getPayState();
                            WmsOrderStatusVo orderStatusVo = orderStatusVoOptional.get();
                            if((PayState.PAID == payState && "N".equals(orderStatusVo.getPayStatus())) || (PayState.PAID != payState && "Y".equals(orderStatusVo.getPayStatus()))){
                                // 查询交易流水，判断电商状态是否正常
                                PayTradeRecordRequest recordRequest = new PayTradeRecordRequest();
                                recordRequest.setBusinessId(orderStatusVo.getTid());
                                BaseResponse<PayTradeRecordResponse> payOrderResponse = payQueryProvider.findByPayOrderNo(recordRequest);
                                if(payOrderResponse != null && payOrderResponse.getContext() != null && payOrderResponse.getContext().getStatus() == TradeStatus.SUCCEED){
                                    pushFailLogRequest = pushOrderAndReceipt(tradeVO, now,true);
                                }
                            }
                        }else{
                            pushFailLogRequest = pushOrderAndReceipt(tradeVO, now,false);//推送订单和收款单
                        }
                        if(Objects.nonNull(pushFailLogRequest)){
                            rePushFailList.add(pushFailLogRequest);
                        }
                    }
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        // 存在推送失败的情况，需要做日志记录
        if (!CollectionUtils.isEmpty(rePushFailList)) {
            PushFailLogRequest pushFailLogRequest = PushFailLogRequest.builder().pushFailLogDTOList(rePushFailList).build();
            pushWmsFailLogProvider.savePushFailLog(pushFailLogRequest);
            // todo 发送短信
        }else{
            rePushFailList.clear();
            rePushFailList.add(PushFailLogDTO.builder()
                            .tid("1")
                            .orderTime(now)
                            .createTime(now)
                            .failReason("无")
                            .pushState("1").build());
            PushFailLogRequest pushFailLogRequest = PushFailLogRequest.builder().pushFailLogDTOList(rePushFailList).build();
            pushWmsFailLogProvider.savePushFailLog(pushFailLogRequest);
        }
        return SUCCESS;
    }

    // 推送销售订单和收款单
    private PushFailLogDTO pushOrderAndReceipt(TradeVO tradeVO,LocalDateTime now,Boolean flag) {
        PushFailLogDTO request = null;
        try {
            if(!flag){
                // 推送销售订单
                TradePushRequest tradePushRequest = new TradePushRequest();
                tradePushRequest.setTid(tradeVO.getId());
                tradeProvider.pushOrderToWms(tradePushRequest);
            }
            flag = true;
            // 推送收款单
            TradeGetByIdRequest tradeGetByIdRequest = new TradeGetByIdRequest();
            tradeGetByIdRequest.setTid(tradeVO.getId());
            tradeProvider.pushConfirmOrder(tradeGetByIdRequest);
        }catch (Exception e){
            request = PushFailLogDTO.builder()
                    .tid(tradeVO.getId())
                    .orderTime(tradeVO.getTradeState().getCreateTime())
                    .createTime(now).build();
            if(flag){
                request.setPushState("2");
                request.setFailReason("推送收款单失败,原因："+e.getMessage());
                XxlJobLogger.log("推送收款单失败，原因：{}",e.getMessage());
            }else{
                request.setPushState("1");
                request.setFailReason("推送销售订单单失败,原因："+e.getMessage());
                XxlJobLogger.log("推送销售订单单失败，原因：{}",e.getMessage());
            }
        }
        return request;
    }
}
