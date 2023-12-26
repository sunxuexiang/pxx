package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.order.api.provider.trade.PileTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.purchase.PilePurchaseActionRequestVO;
import com.wanmi.sbc.order.api.request.trade.TradePageCriteriaRequest;
import com.wanmi.sbc.order.api.response.purchase.PilePurchaseActionResponse;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.order.bean.vo.PilePurchaseActionVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 囤货订单，同步订单总金额和商品优惠后金额至囤货明细表
 * 只执行一次，把10月10号凌晨00:00-跑批时间的订单数据同步
 * @author: jiangxin
 * @create: 2021-10-21 9:42
 */
@JobHandler("pileOrderJobHandler")
@Component
@Slf4j
public class PileOrderJobHandler extends IJobHandler {

    private static final int PAGE_SIZE = 100;

    @Autowired
    private PileTradeProvider pileTradeProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    /**
     * 执行一次后关闭
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        XxlJobLogger.log("订单同步金额定时任务执行,开始时间:" + startTime);

        //查询条件
//        PilePurchaseActionRequestVO request = new PilePurchaseActionRequestVO();
//        request.setSyncFlag(true);
//        Long totalCount = pileTradeProvider.countPilePurchaseAction(request).getContext();
//        int pageNum = totalCount.intValue() / PAGE_SIZE + 1;
//        int pageNo = 0;
//        request.setPageSize(PAGE_SIZE);
//        XxlJobLogger.log("总明细："+totalCount+"条，分页数："+pageNum+"页。");
//        while (pageNo < pageNum) {
////            request.setPageNum(pageNo);
//            PilePurchaseActionResponse response = pileTradeProvider.pilePurchaseActionPage(request).getContext();
//            if (CollectionUtils.isNotEmpty(response.getPilePurchaseActionVOPage().getContent())) {
//                List<String> orderCodeList = response.getPilePurchaseActionVOPage().getContent().stream().map(PilePurchaseActionVO::getOrderCode).distinct().collect(Collectors.toList());
//                //订单查询条件封装
//                TradePageCriteriaRequest tradePageCriteriaRequest = new TradePageCriteriaRequest();
//                TradeQueryDTO tradeQueryDTO = new TradeQueryDTO();
//                tradeQueryDTO.setTradeIds(orderCodeList);
//                tradeQueryDTO.setPageSize(orderCodeList.size());
//                tradePageCriteriaRequest.setReturn(false);
//                tradePageCriteriaRequest.setTradePageDTO(tradeQueryDTO);
//                //分页查询订单
//                MicroServicePage<TradeVO> tradePage = pileTradeQueryProvider.pageCriteria(tradePageCriteriaRequest).getContext().getTradePage();
//                //订单信息
//                Map<String,TradeVO>  tradeVOMap = tradePage.getContent().stream().collect(Collectors.toMap(TradeVO::getId,t -> t));
//                //更新囤货明细信息
//                List<PilePurchaseActionVO> pilePurchaseActionVOS = new ArrayList<>();
//                response.getPilePurchaseActionVOPage().getContent().forEach(pileAction -> {
//                    if (Objects.nonNull(pileAction.getOrderCode())) {
//                        TradeVO tradeVO = tradeVOMap.getOrDefault(pileAction.getOrderCode(),null);
//                        if (Objects.nonNull(tradeVO)) {
//                            pileAction.setPid(tradeVO.getParentId());
//                            pilePurchaseActionVOS.add(pileAction);
//                        }
//                    }
//                });
//                pileTradeProvider.batchSavePilePurchaseAction(PilePurchaseActionRequestVO.builder().purchaseActionVOList(pilePurchaseActionVOS).build());
//                XxlJobLogger.log("第"+pageNo+"次循环，总订单数："+tradePage.getContent().size()+"，"+"总明细："+pilePurchaseActionVOS.size());
//            }
//            pageNo++;
//        }
        int i = 0;
        int num = 100;
        if (StringUtils.isNotBlank(s)){
            num = Integer.valueOf(s);
        }
        while (i < num){
            pileTradeProvider.syncPileOrderPrice();
            XxlJobLogger.log("循环第"+i+"次");
            i++;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        XxlJobLogger.log("订单同步金额定时任务执行，结束时间:"+endTime+"，总计耗时："+duration.toMillis()+"毫秒");
        return SUCCESS;
    }
}
