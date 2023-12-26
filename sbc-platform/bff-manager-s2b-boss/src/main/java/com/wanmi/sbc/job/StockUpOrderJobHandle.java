package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.BatchAddStockupActionRequest;
import com.wanmi.sbc.order.api.request.trade.StockupActionDeleteRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCountCriteriaRequest;
import com.wanmi.sbc.order.api.request.trade.TradePageCriteriaRequest;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.order.bean.dto.TradeStateDTO;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.StockupActionVO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 提货订单拆分明细定时任务(时间参数为必填)
 * @author: XinJiang
 * @time: 2021/12/17 14:47
 */
@JobHandler(value = "stockUpOrderJobHandle")
@Component
@Slf4j
public class StockUpOrderJobHandle extends IJobHandler {

    private static final int PAGE_SIZE = 100;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Override
    public ReturnT<String> execute(String params) throws Exception {
        XxlJobLogger.log("stockUpOrderJobHandle历史提货明细定时任务执行开始： " + LocalDateTime.now());
        log.info("stockUpOrderJobHandle历史提货明细定时任务执行开始：{}",LocalDateTime.now());
        //参数为必填，查询开始时间，结束时间
        if (StringUtils.isNotBlank(params)) {
            String[] paramsArr = params.split("&");
            XxlJobLogger.log("参数为："+params+"，参数数据组length："+paramsArr.length);
            if (paramsArr.length <= 1) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            String beginTime = paramsArr[0];
            String endTime = paramsArr[1];
            if (StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            //删除明细记录
            tradeProvider.deleteStockupAction(StockupActionDeleteRequest.builder()
                    .beginTime(DateUtil.parseDayTime(beginTime))
                    .endTime(DateUtil.parseDayTime(endTime))
                    .build());
            XxlJobLogger.log("删除提货商品明细记录成功");
            TradeCountCriteriaRequest tradeCountCriteriaRequest = new TradeCountCriteriaRequest();
            //订单查询条件封装
            TradeQueryDTO tradeQueryDTO = new TradeQueryDTO();
            tradeQueryDTO.setBeginTime(beginTime);
            tradeQueryDTO.setEndTime(endTime);
            tradeQueryDTO.setTradeState(TradeStateDTO.builder().payState(PayState.PAID).build());
            //查询订单总数
            tradeCountCriteriaRequest.setTradePageDTO(tradeQueryDTO);
            Long totalNum = tradeQueryProvider.countCriteria(tradeCountCriteriaRequest).getContext().getCount();
            //分页数
            int pageNum = totalNum.intValue() / PAGE_SIZE + 1;
            XxlJobLogger.log("查询时间范围内订单总数为："+totalNum+"，分页数为："+pageNum+"。");
            //分页 每页100条
            tradeQueryDTO.setPageSize(PAGE_SIZE);
            int pageNo = 0;
            while (pageNo < pageNum) {
                tradeQueryDTO.setPageNum(pageNo);
                TradePageCriteriaRequest tradePageCriteriaRequest = new TradePageCriteriaRequest();
                tradePageCriteriaRequest.setTradePageDTO(tradeQueryDTO);
                tradePageCriteriaRequest.setReturn(false);
                //分页查询订单
                MicroServicePage<TradeVO> tradePage = tradeQueryProvider.pageCriteria(tradePageCriteriaRequest).getContext().getTradePage();
                //需要插入的提货订单明细
                List<StockupActionVO> stockupActionVOS = new ArrayList<>();
                if (Objects.nonNull(tradePage) && CollectionUtils.isNotEmpty(tradePage.getContent())) {
                    tradePage.getContent().forEach(trade -> {
                        //订单总金额
                        BigDecimal totalOrderPrice = trade.getTradeItems().stream().reduce(BigDecimal.ZERO,(x,y) -> {
                            return x.add(y.getPrice().multiply(BigDecimal.valueOf(y.getNum())));
                        },BigDecimal::add);
                        //订单商品
                        trade.getTradeItems().forEach(tradeItemVO -> {
                            StockupActionVO stockupActionVO = new StockupActionVO();
                            stockupActionVO.setPid(trade.getParentId());
                            stockupActionVO.setOrderCode(trade.getId());
                            stockupActionVO.setOrderTotalPrice(totalOrderPrice);
                            stockupActionVO.setCustomerId(trade.getBuyer().getId());
                            stockupActionVO.setGoodsId(tradeItemVO.getSpuId());
                            stockupActionVO.setGoodsInfoId(tradeItemVO.getSkuId());
                            stockupActionVO.setGoodsSplitPrice(tradeItemVO.getPrice());
                            stockupActionVO.setGoodsNum(tradeItemVO.getNum());
                            stockupActionVO.setCreateTime(trade.getTradeState().getCreateTime());
                            stockupActionVOS.add(stockupActionVO);
                        });
                    });
                }
                //批量插入订单明细
                tradeProvider.batchAddStockupAction(BatchAddStockupActionRequest.builder().stockupActionVOS(stockupActionVOS).build());
                XxlJobLogger.log("循环第"+ pageNo +"次，插入提货商品明细完成，记录为："+stockupActionVOS.size()+"条。");
                pageNo++;
            }

        }
        XxlJobLogger.log("stockUpOrderJobHandle历史提货明细定时任务执行结束： " + LocalDateTime.now());
        log.info("stockUpOrderJobHandle历史提货明细定时任务执行结束：{}",LocalDateTime.now());
        return SUCCESS;
    }
}
